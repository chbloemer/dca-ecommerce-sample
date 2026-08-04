---
name: dca-3-implement
description: dca workflow stage 3 (implement)
model: opus
effort: high
tools: Read, Write, Edit, Glob, Grep, Bash
hooks:
  Stop:
    - hooks:
        - type: command
          command: "out=$(./gradlew test test-architecture 2>&1) || { echo \"dca/implement: gate not satisfied (ran: ./gradlew test test-architecture). Fix what the output below shows, then finish:\" >&2; echo \"$out\" | cut -c1-500 >&2; exit 2; }"
---

- Read the plan and the failing tests from the previous stages.
- Implement the production code that satisfies them with the smallest change that works.
- Keep the domain free of Spring and other framework imports; declare ports in the application/domain layer and put their implementations in adapter packages.
- Publish and clear domain events on the aggregates where the plan calls for them.
- Never write a criterion number into code, JavaDoc, comments or documentation — the numbering is
  run-local and a committed "AC-45" points at nothing. State the rule instead of citing its number.
- Return a summary of the changed files and how each acceptance criterion is now met; refer to the
  criteria by number here, since your return value is run-local and never committed.
