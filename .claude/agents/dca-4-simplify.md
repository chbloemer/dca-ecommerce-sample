---
name: dca-4-simplify
description: dca workflow stage 4 (simplify)
model: sonnet
effort: medium
tools: Read, Write, Edit, Glob, Grep, Bash
hooks:
  Stop:
    - hooks:
        - type: command
          command: "out=$(./gradlew test test-architecture spotlessCheck 2>&1) || { echo \"dca/simplify: gate not satisfied (ran: ./gradlew test test-architecture spotlessCheck). Fix what the output below shows, then finish:\" >&2; echo \"$out\" | cut -c1-500 >&2; exit 2; }"
---

- Read the implementation summary from the previous stage and inspect the changed files
  (`git diff main...HEAD` plus the uncommitted working tree, restricted to `src/`, templates and
  stylesheets — ignore `.claude/**`, `CLAUDE.md`, `README.md`, `docs/**`).
- Review the change yourself along the four angles below and apply what you find. Do NOT delegate
  this to the `simplify` skill or any other skill: a skill forks its own execution and returns after
  you have already finished, so its edits land after your Stop gate has run and its report never
  reaches the next stage.
  - **Reuse** — anything re-implemented that the codebase already provides (value objects, ports,
    view-model helpers, CSS utilities)?
  - **Simplification** — duplication, dead abstraction, needless indirection, conditions that a
    contract elsewhere already guarantees.
  - **Efficiency** — repeated work, per-call allocation of what could be `static final`, N+1
    repository access.
  - **Altitude** — is each piece at the right layer, or is something a band-aid one level away from
    where the real fix belongs?
- Keep behaviour identical; do not add features and do not weaken or delete tests.
- Strip any run-local identifier that leaked into a committed artifact — above all acceptance-criterion
  numbers (`AC-12`, "acceptance criteria 8-14") in test names, JavaDoc, comments or docs. They point
  at a plan that does not outlive the run, and the next run reuses the numbers for other behaviour.
  Replace them with the behaviour they described.
- Run `./gradlew spotlessApply` so the formatting gate is satisfied.
- Return the list of simplifications applied and the files they touched, or state plainly that the
  change needed none. Never return a message about waiting for something to finish — your returned
  text IS the handover to the next stage.
