export const meta = {
  name: "dca",
  description: "Take a feature request through plan, tests, implementation, simplification and a three-way architecture review",
};

// Stages named dca-<n>-<step> run as their own agents: model, effort, tools and the
// Stop gate all come from that agent's frontmatter.
//
// Flow: plan -> tests -> implement -> simplify, then the ddd, hexagonal and clean-code
// reviews run in parallel; converge decides. On real issues, loop back through
// implement -> simplify and re-review, up to 3 rounds, then escalate to a human.
//
// The review stages reuse the reviewer agents shipped by the dca-core and
// software-craftsmanship plugins, so they have no scaffolded file and no gate.

const REQUEST = args ?? "";

// The original request and the approved plan are threaded into EVERY stage. Passing
// only the previous stage's output loses acceptance criteria after stage 2, which
// lets later stages satisfy the gates while silently missing what was asked for.
let plan = "";

const stage = (name, phase, prev, schema) => {
  const context =
    `Original feature request — this is the contract, every detail counts:\n\n${REQUEST}\n\n` +
    (plan ? `Approved plan and acceptance criteria from stage 1:\n\n${plan}\n\n` : "") +
    `Input from the previous stage:\n\n${prev}\n\n` +
    `Do the ${phase} stage and return your output for the next stage.`;
  const opts = schema ? { agentType: name, phase, schema } : { agentType: name, phase };
  return agent(context, opts);
};

// Reviewers only look at the feature change. Without this they also review the
// workflow's own .claude/** scaffolding and the CLAUDE.md diff, which is pure noise.
const REVIEW_SCOPE =
  `Scope: the feature change only — the committed diff from \`git diff main...HEAD\` plus everything\n` +
  `still uncommitted in the working tree, restricted to production and test sources under \`src/\`\n` +
  `and the templates and stylesheets the feature touches.\n` +
  `Ignore entirely: \`.claude/**\`, \`CLAUDE.md\`, \`README.md\`, \`docs/**\`, build files.\n` +
  `A file the change merely edited is fully in scope, not just the lines it added: check that every\n` +
  `comment and JavaDoc in it is still true after the change. Claims about what is or is not covered\n` +
  `elsewhere go stale silently when the change adds the missing coverage.`;

const review = (agentType, lens, priorVerdict) =>
  agent(
    `Review this branch's changes through the ${lens} lens.\n\n${REVIEW_SCOPE}\n\n` +
      `Original feature request the change must satisfy:\n\n${REQUEST}\n\n` +
      // Without the criteria, reviewers keep proposing changes that break literals a criterion pins
      // (exact hint texts, attribute names, hardcoded paths); converge then discards them each round.
      `The approved plan and its acceptance criteria — a criterion pins the behaviour AND the exact\n` +
      `literals it names, so do not propose anything that would break one; if a criterion itself is\n` +
      `the defect, say so explicitly instead of proposing a change that silently violates it:\n\n${plan}\n\n` +
      (priorVerdict
        ? `A previous review round produced this verdict; the fixes for it have since been applied.\n` +
          `Check they landed correctly and look for what the round missed. Do not re-report findings\n` +
          `that round already rejected as unconfirmed:\n\n${priorVerdict}\n\n`
        : "") +
      `Report only defects you can point at in the actual code, each with file, line, problem and fix.\n` +
      `Do not report formatting, and do not report anything the ArchUnit suite already enforces.`,
    { agentType, phase: `review-${lens}` },
  );

const VERDICT = {
  type: "object",
  properties: {
    issues: { type: "boolean" },
    summary: { type: "string" },
  },
  required: ["issues", "summary"],
};

const LENSES = [
  ["dca-core:ddd-reviewer", "ddd"],
  ["dca-core:hexagonal-reviewer", "hexagonal"],
  ["software-craftsmanship:clean-code-reviewer", "clean-code"],
];

// A dead or skipped reviewer resolves to null. Interpolating that into the converge
// prompt reads as "reviewed, found nothing" and can manufacture a false CLEAN verdict,
// so a missing review is retried once and otherwise escalates.
const runReviews = async (priorVerdict) => {
  const results = await parallel(LENSES.map(([type, lens]) => () => review(type, lens, priorVerdict)));
  const retried = await parallel(
    results.map((r, i) => async () => {
      if (r) return r;
      log(`review ${LENSES[i][1]} returned nothing — retrying once`);
      return review(LENSES[i][0], LENSES[i][1], priorVerdict);
    }),
  );
  const missing = LENSES.filter((_, i) => !retried[i]).map(([, lens]) => lens);
  return { reports: retried, missing };
};

// Later simplify rounds must see what earlier rounds deliberately left alone, otherwise round 3
// reverses a decision round 1 declined with a reason and the change churns for nothing.
let lastSimplify = "";
const simplify = async (prev) => {
  const context = lastSimplify
    ? `${prev}\n\nThe simplify stage of an earlier round already reviewed this change and reported:\n\n` +
      `${lastSimplify}\n\n` +
      `Anything it listed as deliberately left unchanged stays unchanged, unless a confirmed review\n` +
      `finding in the input above requires otherwise. Do not re-litigate its judgement calls.`
    : prev;
  lastSimplify = await stage("dca-4-simplify", "simplify", context);
  return lastSimplify;
};

plan = await stage("dca-1-plan", "plan", REQUEST);
let out = await stage("dca-2-tests", "tests", plan);
out = await stage("dca-3-implement", "implement", out);
out = await simplify(out);

let round = 1;
let verdict;
while (true) {
  const priorVerdict = verdict ? verdict.summary : null;
  const { reports, missing } = await runReviews(priorVerdict);
  if (missing.length > 0) {
    return {
      status: "needs-human",
      reason: `review lens failed twice: ${missing.join(", ")} — no verdict can be trusted`,
      rounds: round,
      plan,
      lastStageOutput: out,
    };
  }

  const [ddd, hexagonal, cleanCode] = reports;
  verdict = await stage(
    "dca-8-converge",
    "converge",
    `ddd review:\n${ddd}\n\nhexagonal review:\n${hexagonal}\n\nclean-code review:\n${cleanCode}`,
    VERDICT,
  );
  if (!verdict.issues) {
    return { status: "clean", rounds: round, verdict, plan, lastStageOutput: out };
  }
  if (round >= 3) {
    return { status: "needs-human", reason: "still has confirmed defects after 3 rounds", rounds: round, verdict, plan };
  }
  round++;
  log(`round ${round}: fixing confirmed defects`);
  out = await stage("dca-3-implement", "implement", `Confirmed defects to fix:\n${verdict.summary}`);
  out = await simplify(out);
}

// build-workflow: append stage lines above this line
