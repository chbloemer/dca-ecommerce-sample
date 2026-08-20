# ADR-032: The Context Map Is Declared in Code and Enforced

**Date**: August 20, 2026
**Status**: ✅ Accepted
**Deciders**: Architecture Team

---

## Context

Cross-context relationships were described in three unconnected places: prose documentation, the
`@ApplicationModule.allowedDependencies` declarations that Spring Modulith enforces, and the code
itself (caller-owned ports, outgoing adapters, consumer-defined trigger interfaces). Nothing tied
them together. The documentation could drift silently, and the *strategic intent* of an edge — is
this an Anti-Corruption Layer or does the downstream deliberately conform to the upstream's
published language? — existed nowhere a machine could check it.

A single all-encompassing `@ContextRelationship(target, pattern, integration)` annotation was
considered and rejected: it mixes dimensions that are separate in DDD (organizational relationship,
translation policy, transport), and it forces the downstream to declare properties it does not
control, such as whether the upstream is an Open Host Service.

## Decision

**Each side of a relationship declares only what it controls, as package annotations. ArchUnit
proves the declarations consistent with each other, with Spring Modulith, and with the code. The
context map document is generated from the declarations.**

The declaration vocabulary (all in `sharedkernel/marker/strategic/`):

| Annotation | Side | Declares |
|---|---|---|
| `@Upstream(context, translation, via, rationale)` | downstream | the directed dependency: translation strategy (`ANTI_CORRUPTION_LAYER` or `CONFORMIST`) and consumed channel (`API`, `EVENTS`) |
| `@Partnership(context, rationale)` | both (symmetric) | shared governance of a co-evolved contract; grants **no** dependency permission |
| `@NamedInterface("api"/"events")`, `@OpenHostService` | upstream | the published contract |
| `@ApplicationModule.allowedDependencies` | downstream | the enforced package boundary (Spring Modulith) |

Deliberate omissions:

- **Customer–Supplier is not machine-classified.** It is an organizational statement about whose
  needs drive the upstream's roadmap; ArchUnit cannot verify it. It belongs in `rationale`.
- **Separate Ways is the absence of a declaration.** The completeness rule (every actual
  dependency on a foreign `api`/`events` package requires an `@Upstream`) makes it checkable
  without an enum value.
- **Shared Kernel is not a relationship value.** It is the `sharedkernel` package, already
  governed by its own rules.
- **No `@PublishedLanguage` annotation.** The `api`/`events` named interfaces plus
  `@OpenHostService`/`IntegrationEvent` already carry that statement; an annotation would add no
  checkable claim.
- **Non-context modules (backoffice) declare nothing.** `@Upstream`/`@Partnership` are only legal
  on `@BoundedContext` packages; plain modules are governed by Spring Modulith alone.

The identity of an `@Upstream` declaration is `(context, via)`. A downstream may choose different
translation strategies per channel — checkout translates cart's synchronous API behind an ACL but
conforms to cart's consumer-defined `CartCompletionTrigger` event contract — by repeating the
annotation.

`ContextMapArchUnitTest` enforces, per declaration:

- declarations only on bounded contexts; targets exist; no self-reference; `via` non-empty;
  `(context, via)` unique
- `@Upstream` edges and `allowedDependencies` named-interface entries agree **in both
  directions** — neither side may know more than the other
- `ANTI_CORRUPTION_LAYER` + `API`: upstream contract types only in `adapter.outgoing..`
- `ANTI_CORRUPTION_LAYER` + `EVENTS`: upstream contract types only in `adapter.incoming..`
  (the edge of a consumed event is the incoming side)
- `CONFORMIST`: upstream contract types may appear outside adapters but never in `domain..` —
  conformism does not suspend domain purity
- every actual dependency on a foreign `api`/`events` package has a declaration
- `@Partnership` is symmetric

`ContextMapDocumentationTest` regenerates [docs/architecture/context-map.md](../context-map.md)
from the annotations on every run and fails when the committed file is stale, so the diagram and
tables are a fully derived view — one source (annotations), one enforcement (Modulith + ArchUnit),
one generated document.

## Consequences

**Easier:**

- The context map cannot drift from the code; CI fails on divergence in either direction.
- Strategic intent is reviewable in the same diff as the code that implements it.
- Translation-strategy violations (an upstream DTO leaking out of an adapter) fail the build with
  a message naming the declared relationship.

**Harder:**

- Adding a cross-context dependency now requires three consistent edits (annotation,
  `allowedDependencies`, the adapter itself) — intentional friction that makes new coupling a
  conscious decision.
- The duplication between `@Upstream` and `allowedDependencies` is accepted rather than derived,
  because Spring Modulith must read its own annotation natively; the ArchUnit agreement rule keeps
  the two honest.

**Related:** [ADR-024](adr-024-interface-inversion-spring-modulith.md) (consumer-defined trigger
contracts — the partnership examples), [ADR-026](adr-026-transactional-outbox-integration-events.md)
(integration events), [ADR-027](adr-027-integration-event-contract-identity.md) (event contract
identity).
