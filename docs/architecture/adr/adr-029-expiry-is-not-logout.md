# ADR-029: Session Expiry Ends the Session, Not the Identity

**Date**: August 13, 2026
**Status**: ✅ Accepted
**Deciders**: Architecture Team

---

## Context

A visitor's `UserId` is what the cart, the checkout session and every other context key their
data on. Registration deliberately preserves it (see the account glossary), so that a guest who
registers mid-checkout keeps their cart.

That makes the `UserId` far more valuable than the authentication it happens to travel with.

### Problem

`JwtAuthenticationFilter` treats an expired token and a missing token identically — both produce a
**newly generated** `UserId`:

```java
} else {
  // Token invalid or expired - create new anonymous identity
  identity = createAnonymousIdentity();          // UserId.generateAnonymous()
  token = tokenService.generateAnonymousToken(identity.userId());
}
```

The consequence is a silent data loss with no user action behind it. A registered token lives 7
days (`registered-expiration-days: 7`); on day 8 the visitor returns and the browser is handed a
brand-new `UserId`. The cart still exists, keyed on the old one — it is simply unreachable. Nobody
logged out. Nobody clicked anything. The clock ran out.

The lifetimes make it worse: anonymous tokens live **30** days, registered ones **7**. Signing in
therefore makes a visitor lose their context *four times faster* than staying a guest.

A second problem hides in the same branch. `JwtTokenService.validateToken` distinguishes
`ExpiredJwtException` (logged at DEBUG) from `JwtException` (logged at WARN), but returns
`Optional.empty()` for both — so the filter cannot tell a token that merely aged out from one whose
signature does not verify. The first is routine; the second is an attack or a bug.

---

## Decision

**Expiry ends the *session*. Only an explicit logout ends the *identity*.**

1. **An expired or absent session leaves the visitor identity intact.** The request continues as
   anonymous **under the `UserId` the browser already has**. A new `UserId` is minted only when the
   browser presents none at all — a genuinely first visit.

2. **An explicit logout rotates the visitor identity.** The user asked to leave; on a shared device
   the next person must not inherit the cart. Rotation, not deletion: the cart stays with the
   account and returns via `RecoverCartOnLoginUseCase` at the next login. See
   [ADR-030](adr-030-three-cookie-session-design.md).

3. **Expiry and invalid signature are different outcomes.** Token validation must report which
   occurred, so an expired token can be handled as the routine event it is while a bad signature
   stays a warning. `Optional.empty()` for both erases that distinction at the boundary.

4. **The authentication boundary enriches, it does not gate.** A request with an expired token is
   not an error: it proceeds as anonymous and sees what an anonymous visitor sees. Authorization
   for a protected action is enforced by that action, not by the token filter.

### Prerequisite

Point 1 cannot be implemented while a single cookie carries both the identity and the session — if
that cookie expires, the identity expires with it, and the filter cannot distinguish "expired" from
"logged out". The split is [ADR-030](adr-030-three-cookie-session-design.md); this ADR states the
rule, that one provides the mechanism.

---

## Consequences

### Positive

✅ **No silent cart loss** — the case that motivated this ADR disappears
✅ **Signing in no longer shortens continuity** — identity lifetime is independent of session lifetime
✅ **Attack signal restored** — a forged signature is distinguishable from an aged token again
✅ **Predictable rule** — "did the user act?" decides, not "did a timer fire?"

### Neutral

⚠️ **A stale `UserId` can outlive many sessions** — intended: it holds a cart, not a permission
⚠️ **`IdentitySession.clearIdentity()` is renamed**, since it no longer describes what happens

### Negative

❌ **Two cookies to reason about instead of one** — the price of separating the two lifetimes

---

## Alternatives Considered

### Alternative 1: Keep one cookie, extend its lifetime

**Rejected.** It couples the two lifetimes rather than separating them: either the session lives as
long as the identity (a 30-day authenticated session), or the identity dies as early as the session.
Both are worse than the split.

### Alternative 2: Persist the cart against the browser instead of the identity

**Rejected.** It replaces one identifier with another and re-invents the `UserId` under a different
name, while breaking the guest → account continuity that registration deliberately preserves.

### Alternative 3: Do nothing, accept cart loss on expiry

**Rejected.** The behaviour is invisible in tests and looks like a working system: the user is
"just" anonymous again. That is precisely why it survived this long.

---

## Related ADRs

- [ADR-030: Three-Cookie Session Design](adr-030-three-cookie-session-design.md) — the mechanism
  this rule needs
- [ADR-011: Bounded Context Isolation](adr-011-bounded-context-isolation.md) — why the `UserId`
  crosses contexts while the account does not

---

## Validation

- [ ] The filter, given a valid `shop-identity` and an expired `shop-session`, continues under the
      existing `UserId` and mints nothing
- [ ] A cart created before expiry is still reachable after it
- [ ] An explicit logout produces a different `UserId` than before
- [ ] An invalid signature is logged as a warning, an expired token is not

---

**Accepted by**: Architecture Team
**Date**: August 13, 2026
**Version**: 1.0
