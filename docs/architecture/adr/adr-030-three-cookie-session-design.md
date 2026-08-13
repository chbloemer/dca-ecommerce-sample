# ADR-030: Separate Cookies for Identity, Session and Renewal

**Date**: August 13, 2026
**Status**: ✅ Accepted (partially implemented — see *Scope of the current implementation*)
**Deciders**: Architecture Team

---

## Context

One cookie, `shop-identity`, currently carries everything: the visitor's `UserId`, the
authentication state, the roles, and the email claim. Its lifetime is the lifetime of all of them.

[ADR-029](adr-029-expiry-is-not-logout.md) requires the visitor identity to survive an expired
session. That is impossible with one cookie — when it expires, everything expires together, and the
filter cannot tell "the session aged out" from "the user logged out" because both look like *no
valid cookie*.

The same conflation creates a second problem that has nothing to do with authentication. A visitor
identifier is attractive for analytics: it is stable and it spans visits. But an identifier used for
measurement and an identifier used for the cart want opposite things.

### The consent trap

Under ePrivacy/GDPR a cart identifier is *strictly necessary* — no consent required. An analytics
identifier is not — consent required. Carrying both in **one** value makes the whole cookie
consent-dependent, which means **a visitor who declines analytics loses their cart**, a functional
guarantee they are entitled to regardless of consent.

The tracking/rotation tension ("rotating on logout is safer but worse for tracking") is real only
while one identifier serves both purposes. Separated, it dissolves.

---

## Decision

**Split by lifetime and by purpose. Four identifiers, three of them in the auth subsystem.**

| Cookie | Carries | Lifetime | Ends when |
|---|---|---|---|
| `shop-identity` | visitor `UserId` — the cart's key | long (30 d) | **explicit logout only** (rotated, not deleted) |
| `shop-session` | authentication: subject, roles, email | short | expiry — with no effect on the identity |
| `shop-refresh` | opaque renewal token, path-scoped to the refresh endpoint | long | logout, revocation, or absolute expiry |
| *analytics* | measurement identifier | its own | consent withdrawal — **never** the auth subsystem's business |

Four rules follow:

1. **The analytics identifier is not `shop-identity`.** It lives in its own consent-governed cookie,
   owned by the analytics tool. The auth subsystem neither reads nor writes it. What rotation on
   logout costs is the linkage of this browser's *future* anonymous sessions to its past ones —
   which is what a user who deliberately logs out is asking for.

2. **`shop-refresh` is path-scoped** to the renewal endpoint, so the browser never sends it to the
   product, cart or checkout paths, even on the same origin. It is an opaque random value; its
   claims live in a server-side row, which is what makes revocation O(1).

3. **Cookie hardening is not optional.** `HttpOnly` always; `Secure` driven by the environment,
   never hardcoded; `SameSite` explicit on every cookie the subsystem sets.

4. **Logout clears the session and the renewal token, and rotates the identity.** It deletes no
   cart: the registered user's cart is keyed on the account and returns through
   `RecoverCartOnLoginUseCase` at the next login.

### Scope of the current implementation

**Implemented:** `shop-identity` / `shop-session` split, expiry falling back to the existing visitor
identity, identity rotation on explicit logout, `Secure` from the environment, explicit `SameSite`.

**Deliberately deferred: `shop-refresh` and the renewal flow.** This is a decision, not an
oversight, and it has a price worth naming: **without a refresh token there is no revocation and no
theft detection, so the session cookie's lifetime is the blast radius of a stolen token.** The
sample is a single service with an in-memory store; a renewal flow needs a persistent token store,
rotation with reuse detection, and an endpoint to scope the cookie to — a subsystem larger than the
rest of this ADR combined. The three-cookie design is recorded here so the two-cookie implementation
is understood as a stage, not as the target.

Until then the session lifetime should be treated as security-relevant: it is the only bound on a
leaked token.

---

## Consequences

### Positive

✅ **[ADR-029](adr-029-expiry-is-not-logout.md) becomes implementable** — the two lifetimes are separable
✅ **The cart is consent-independent** — no analytics decision can take it away
✅ **Rotation on logout costs nothing** that the user did not intend to give up
✅ **Blast radius is bounded by path scope** once `shop-refresh` lands — the cart service never sees it

### Neutral

⚠️ **More cookies to reason about**, each with an explicit purpose rather than one with four
⚠️ **Existing browsers hold a `shop-identity` that contains registered claims** — see *Migration*

### Negative

❌ **No revocation until `shop-refresh` exists** — a stolen session token is valid until it expires
❌ **A logout on device A does not end a session on device B** — same reason

---

## Migration

Browsers in the field hold a `shop-identity` cookie whose token may carry registered claims. The
filter **reads it for its `UserId` only** and ignores any authentication claims in it: the visitor
keeps their cart, and an authenticated session must be re-established by logging in. The alternative
— honouring the old cookie's claims — would mean a cookie named for the visitor identity silently
granting authentication, which is exactly the conflation this ADR removes.

---

## Alternatives Considered

### Alternative 1: One cookie, longer lifetime

**Rejected** in [ADR-029](adr-029-expiry-is-not-logout.md): it couples the lifetimes instead of
separating them.

### Alternative 2: Preserve the visitor identity through logout (as the JWT guide originally advised)

**Rejected.** The guide's rationale was cart continuity — which no longer holds, because the cart is
recovered from the *account* on login. What remains is a shared-device risk with no compensating
benefit.

### Alternative 3: Server-side sessions instead of tokens

**Rejected** for this sample: it trades the token design for session affinity or a shared session
store, and hides the very tradeoffs the reference implementation exists to demonstrate.

---

## Related ADRs

- [ADR-029: Session Expiry Ends the Session, Not the Identity](adr-029-expiry-is-not-logout.md)
- [ADR-008: Repository Interfaces as Output Ports](adr-008-repository-interfaces-as-output-ports.md) —
  where a future refresh-token store's port belongs

---

## Validation

- [ ] `shop-session` expiring leaves `shop-identity` untouched
- [ ] Logout rotates `shop-identity` and clears `shop-session`
- [ ] No cookie is set with a hardcoded `Secure` value
- [ ] Every cookie the subsystem sets declares `SameSite`
- [ ] A legacy single-cookie browser keeps its `UserId` and is treated as anonymous

---

**Accepted by**: Architecture Team
**Date**: August 13, 2026
**Version**: 1.0
