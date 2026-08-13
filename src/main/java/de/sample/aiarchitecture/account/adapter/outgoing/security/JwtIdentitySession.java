package de.sample.aiarchitecture.account.adapter.outgoing.security;

import de.sample.aiarchitecture.account.application.shared.IdentitySession;
import de.sample.aiarchitecture.sharedkernel.domain.model.UserId;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * JWT-based implementation of IdentitySession.
 *
 * <p>This component manages user session identity via HTTP cookies. It is request-scoped because it
 * needs access to the current HTTP response to set/clear cookies.
 *
 * <p>Identity and session are separate cookies (ADR-030): {@code shop-identity} carries the visitor
 * {@code UserId} the cart is keyed on, {@code shop-session} carries the authentication. Both are
 * {@code HttpOnly} and {@code SameSite=Lax}; {@code Secure} comes from configuration so that local
 * HTTP development cannot bake {@code false} into a deployment.
 */
@Component
@RequestScope
public class JwtIdentitySession implements IdentitySession {

  private final JwtProperties jwtProperties;
  private final JwtTokenService tokenService;
  private final HttpServletResponse response;

  public JwtIdentitySession(
      final JwtProperties jwtProperties,
      final JwtTokenService tokenService,
      final HttpServletResponse response) {
    this.jwtProperties = jwtProperties;
    this.tokenService = tokenService;
    this.response = response;
  }

  @Override
  public void setRegisteredIdentity(final String token) {
    writeCookie(
        jwtProperties.sessionCookieName(), token, jwtProperties.sessionCookieMaxAgeSeconds());
  }

  @Override
  public void logOut() {
    writeCookie(jwtProperties.sessionCookieName(), "", 0);

    // Rotate rather than delete: the next person on a shared device must not inherit this cart,
    // while the account's own cart is restored on the next login (ADR-029).
    writeCookie(
        jwtProperties.cookieName(),
        tokenService.generateAnonymousToken(UserId.generateAnonymous()),
        jwtProperties.identityCookieMaxAgeSeconds());
  }

  private void writeCookie(final String name, final String value, final int maxAgeSeconds) {
    response.addHeader(
        HttpHeaders.SET_COOKIE,
        ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(jwtProperties.secureCookies())
            .sameSite(JwtProperties.SAME_SITE)
            .path("/")
            .maxAge(maxAgeSeconds)
            .build()
            .toString());
  }
}
