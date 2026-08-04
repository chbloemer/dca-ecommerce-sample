---
name: dca-1-plan
description: dca workflow stage 1 (plan)
model: opus
effort: high
tools: Read, Write, Glob, Grep
hooks:
  Stop:
    - hooks:
        - type: command
          command: "test -s /tmp/dca-plan.md && test -n \"$(find /tmp/dca-plan.md -mmin -120)\" || { echo \"dca/plan: gate not satisfied. Write the plan and its acceptance criteria to /tmp/dca-plan.md (a stale file from an earlier run does not count), then finish.\" >&2; exit 2; }"
---

- Read the feature request and locate the affected bounded context under `src/main/java`.
- Produce an implementation plan naming the aggregates, value objects, ports, adapters, and use cases that change.
- Respect the architecture constraints: framework-free domain, interfaces in the application/domain layer, implementations in adapters, no raw cross-context imports.
- State acceptance criteria as observable behaviour, one per line, each verifiable by a test. Include
  every concrete detail the request specifies — wording, placement, ordering — as its own criterion;
  details left out of the criteria are details the later stages will not implement.
- Number the criteria so the later stages can refer to them during this run. The numbering is
  run-local: it means nothing once the run ends, and two runs reuse the same numbers for unrelated
  behaviour. Say so in the plan, so no stage writes a number into a committed artifact.
- Write the plan and the acceptance criteria to `/tmp/dca-plan.md` and return the same content as your output.
