---
name: dca-2-tests
description: dca workflow stage 2 (tests)
model: opus
effort: medium
tools: Read, Write, Edit, Glob, Grep, Bash
hooks:
  Stop:
    - hooks:
        - type: command
          command: "out=$({ ./gradlew testClasses -q && ! ./gradlew test -q; } 2>&1) || { echo \"dca/tests: gate not satisfied. The test sources must compile and at least one test must fail for the new behaviour. Fix what the output below shows, then finish:\" >&2; echo \"$out\" | cut -c1-500 >&2; exit 2; }"
---

- Read the plan and acceptance criteria from the previous stage.
- Write JUnit 5 tests for the planned behaviour under `src/test/java`, mirroring the package of the code under test.
- Cover **every** acceptance criterion with at least one test — including criteria about rendered
  markup (element presence, ordering, `data-test` attributes). A criterion with no test is a criterion
  the implement gate can never fail on, so the workflow would certify work it never checked.
- Add ArchUnit/Spock tests under `src/test-architecture/groovy` only when the plan introduces a new structural rule.
- Do not implement production behaviour; add only the minimum stubs needed for the test sources to compile.
- Delete any throwaway spike or scratch test before you finish — a passing spike does not trip the gate,
  so nothing else will catch it. Keep only what mirrors the package of the code under test, plus test
  helpers you deliberately want kept.
- Never write a criterion number into the code — not into `@DisplayName`, not into JavaDoc, not into a
  comment. The numbering is run-local, so a committed "AC-5" points at nothing and collides with a
  different criterion from the next run. Name the behaviour instead: `@DisplayName("overview item
  links to /account and is the active item")`.
- Return the list of new test classes, the acceptance criterion each covers, and the assertion each
  one currently fails on. Name explicitly any acceptance criterion you could not turn into a test and
  why — do not leave it silently uncovered. Refer to criteria by number here — your return value is
  run-local and never committed.
