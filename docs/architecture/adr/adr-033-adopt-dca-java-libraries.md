# ADR-033: Adopt the dca-java Libraries for Markers and Architecture Rules

**Status:** ✅ Accepted
**Date:** 2026-08-29
**Deciders:** Architecture Team

## Context

Until now this project carried its own copy of the Domain-Centric Architecture vocabulary
(`sharedkernel/marker/**`: tactical and strategic markers, port interfaces) and its own ArchUnit
governance suite (13 Groovy/Spock classes, ~3 200 lines). Both were the *source* other DCA artifacts
copied from — the marketplace bootstrap skill shipped them as templates, the knowledge catalog parsed
them. Every rule fix had to be mirrored by hand, and a second project adopting DCA had to copy the
files.

The vocabulary and the rules are now published as libraries from the `dca-java` repository:

- `dev.domaincentric:dca-building-blocks` — the markers (`ddd.tactical`, `ddd.strategic`,
  `ddd.strategic.relationships`, `hexagonal.port.in`, `hexagonal.port.out`), zero dependencies.
- `dev.domaincentric:dca-archunit` — the rules (107 rules in 10 sets with stable ids
  `DCA-<SET>-<NNN>`), parameterised by a `DcaLayout`, plus the opt-in `ContextMapRenderer`.

## Decision

1. Delete `sharedkernel/marker/**` and depend on `dca-building-blocks`. Imports change from
   `…sharedkernel.marker.tactical.AggregateRoot` to
   `dev.domaincentric.dca.buildingblocks.ddd.tactical.AggregateRoot`; `SharedKernel`,
   `OpenHostService`, `Upstream`, `ExternalUpstream` and `Partnership` live in
   `ddd.strategic.relationships`.
2. `@AsyncInitialize` is **not** a DCA building block (Spring lifecycle detail) and stays here, in
   `sharedkernel/infrastructure/`.
3. Delete the Groovy/Spock architecture suite and the Groovy toolchain. `src/test-architecture/java`
   holds three JUnit 5 classes: `ArchitectureRulesTest extends DcaArchitectureTest` (the whole
   catalog, one dynamic test per rule), `ContextMapDocumentationTest` (renders and checks
   `docs/architecture/context-map.md`), `SpringModulithVerificationTest` (Modulith boundaries —
   sample-specific, not a DCA rule).
4. Rule changes are made in `dca-java`, never here. A rule this project must not follow is excluded
   by id in `ArchitectureRulesTest.excludedRuleIds()` with an ADR explaining why.
5. Until the artifacts are on Maven Central, `settings.gradle` includes the sibling build
   (`includeBuild('../dca-java')` when the folder exists) so the real coordinates already resolve.

## Consequences

**Positive**
- One source of truth for markers and rules; this project consumes exactly what every other DCA
  project consumes.
- No Groovy toolchain; architecture tests are plain JUnit 5.
- Rule ids in test output (`[DCA-TAC-006] …`) link failures to the rule catalog and the knowledge
  catalog.

**Negative**
- The project can no longer tweak a rule locally; it must open a change in `dca-java` or exclude the
  rule by id.
- Historical ADRs (e.g. ADR-005, ADR-008, ADR-016, ADR-032) cite paths under `sharedkernel/marker/`;
  they remain valid as records of their time and are not rewritten. Read them with this ADR in mind.

## Related

- ADR-015 (ArchUnit governance), ADR-016 (shared kernel pattern), ADR-032 (executable context map)
