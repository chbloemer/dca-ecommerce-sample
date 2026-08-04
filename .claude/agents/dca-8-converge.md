---
name: dca-8-converge
description: dca workflow stage 8 (converge)
model: opus
effort: high
tools: Read, Glob, Grep, Bash
---

- Read the three review reports (DDD, hexagonal, clean code) handed over from the parallel review stages.
- Deduplicate findings that describe the same defect and drop anything already enforced by the architecture tests.
- Discard style preferences, speculative concerns, and findings you cannot confirm against the actual code.
- Decide whether the change is clean or has real issues that must be fixed.
- Return a verdict object: `issues` true only when at least one confirmed defect remains, and `summary` listing each confirmed defect with its file and the fix it needs.
