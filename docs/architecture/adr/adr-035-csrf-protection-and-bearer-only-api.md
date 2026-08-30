# ADR-035: CSRF Protection for Web Forms, Bearer-Only Authentication for the API

**Date**: 2026-08-30 · **Status**: Accepted

## Context

The shop authenticates browsers with a JWT in an `HttpOnly` cookie (ADR-029/030). Until now the security chain
disabled CSRF protection entirely and relied on `SameSite=Lax`. That is defence in depth, not a strategy: `Lax`
still sends the cookie on top-level navigations, older clients ignore it, and every writing form (`/cart/add-product`,
`/checkout/*`, `/account/*`, `/login`, `/register`, `/logout`) was reachable cross-site with the victim's session.

The same filter accepted the session cookie **and** an `Authorization: Bearer` header for every path, including
`/api/**`. An API that is exempt from CSRF but honours cookies is the worst of both worlds: a cross-site form post
to `/api/carts/{id}/checkout` would have been authenticated by the browser's cookie and never challenged.

The .NET sample already validates an antiforgery token on every POST. The two samples must show the same shape.

## Decision

1. **Every browser form carries a CSRF token.** Spring Security's CSRF filter is active on the shop chain with a
   `CookieCsrfTokenRepository` (the chain is stateless, so the token lives in the `XSRF-TOKEN` cookie).
   `CsrfTokenModelAdvice` (framework support, `infrastructure/support`) exposes it to every Pug template as
   `_csrf`; each writing form renders `input(type="hidden" name=_csrf.parameterName value=_csrf.token)`. A POST
   without a matching token is answered `403`.

2. **`/api/**` and `/mcp/**` are Bearer-only.** `JwtAuthenticationFilter` neither reads nor writes cookies on
   these paths: the identity is whatever the `Authorization: Bearer` token says, or a throwaway anonymous identity.
   `AuthResource` returns the token in the response body and sets no cookie; `/api/auth/logout` is a stateless
   acknowledgement (`204`) — the client discards its token. Because no cookie can authenticate these endpoints,
   exempting them from CSRF is sound.

3. **Browser sessions are established only through the web forms** (`/login`, `/register`, `/logout`), which are
   CSRF-protected. Login CSRF against the API is therefore impossible — there is no cookie to plant.

## Consequences

- Positive: the Java and .NET samples render the same forms with the same protection (field name `_csrf` vs.
  `__RequestVerificationToken` is the only difference). ADR-001's promise of "CSRF for web, token for API" is now
  true in code, not only in the ADR.
- Positive: one place decides what a token-only endpoint is (`JwtAuthenticationFilter.isTokenOnlyEndpoint`), and
  the CSRF exemption list mirrors it. Changing one without the other is the mistake to watch for in review.
- Negative: API clients that relied on the cookie set by `/api/auth/login` must send the token as a Bearer header.
  The sample's own UI never did; it uses the web login form.
- Verified by `CsrfProtectionIntegrationTest`: web POST without token → `403`; with the page's token → normal
  redirect; API POST without cookie or token reaches the resource; API POST with browser cookies is neither
  authenticated by them nor answered with a `Set-Cookie`.
