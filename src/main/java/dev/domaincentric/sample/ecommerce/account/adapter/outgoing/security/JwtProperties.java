package dev.domaincentric.sample.ecommerce.account.adapter.outgoing.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for JWT authentication.
 *
 * <p>These properties are bound from the application.yml under the {@code app.security.jwt} prefix.
 *
 * <p><b>Example configuration:</b>
 *
 * <pre>
 * app:
 *   security:
 *     jwt:
 *       secret: ${JWT_SECRET:dev-only-secret-key}
 *       anonymous-expiration-days: 30
 *       registered-expiration-days: 7
 *       issuer: dca-ecommerce-sample
 *       cookie-name: shop-identity
 *       session-cookie-name: shop-session
 *       secure-cookies: false   # true in staging/production
 * </pre>
 *
 * <p>Identity and session live in <b>separate</b> cookies on purpose (ADR-030): the identity
 * carries the cart and must survive an expired session, which one shared cookie cannot express —
 * see also ADR-029.
 *
 * @param secret the secret key for signing JWTs (must be at least 256 bits for HS256)
 * @param anonymousExpirationDays how long the identity remains valid (default: 30 days)
 * @param registeredExpirationDays how long an authenticated session remains valid (default: 7 days)
 * @param issuer the token issuer claim (iss)
 * @param cookieName the name of the cookie that stores the identity
 * @param sessionCookieName the name of the cookie that stores the authenticated session
 * @param secureCookies whether cookies are flagged {@code Secure}; must be {@code true} wherever
 *     the application is reachable over HTTPS, and is configuration rather than a constant so that
 *     local HTTP development cannot bake {@code false} into a deployment
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
    String secret,
    int anonymousExpirationDays,
    int registeredExpirationDays,
    String issuer,
    String cookieName,
    String sessionCookieName,
    boolean secureCookies) {

  /** Default cookie name if not configured. */
  public static final String DEFAULT_COOKIE_NAME = "shop-identity";

  /** Default session cookie name if not configured. */
  public static final String DEFAULT_SESSION_COOKIE_NAME = "shop-session";

  /**
   * SameSite policy for both cookies.
   *
   * <p>{@code Lax} rather than {@code Strict}: the identity cookie must survive a top-level
   * navigation from an external link, or someone arriving from a search result would be handed a
   * fresh identity and lose their cart.
   */
  public static final String SAME_SITE = "Lax";

  /** Default issuer if not configured. */
  public static final String DEFAULT_ISSUER = "dca-ecommerce-sample";

  public JwtProperties {
    if (secret == null || secret.length() < 32) {
      throw new IllegalArgumentException(
          "JWT secret must be at least 32 characters (256 bits) for HS256");
    }
    if (anonymousExpirationDays <= 0) {
      anonymousExpirationDays = 30;
    }
    if (registeredExpirationDays <= 0) {
      registeredExpirationDays = 7;
    }
    if (issuer == null || issuer.isBlank()) {
      issuer = DEFAULT_ISSUER;
    }
    if (cookieName == null || cookieName.isBlank()) {
      cookieName = DEFAULT_COOKIE_NAME;
    }
    if (sessionCookieName == null || sessionCookieName.isBlank()) {
      sessionCookieName = DEFAULT_SESSION_COOKIE_NAME;
    }
    if (cookieName.equals(sessionCookieName)) {
      throw new IllegalArgumentException(
          "Identity and session must not share a cookie, see ADR-030: " + cookieName);
    }
  }

  /**
   * Returns how long the identity cookie lives.
   *
   * @return the max age in seconds
   */
  public int identityCookieMaxAgeSeconds() {
    return anonymousExpirationDays * 24 * 60 * 60;
  }

  /**
   * Returns how long the session cookie lives.
   *
   * @return the max age in seconds
   */
  public int sessionCookieMaxAgeSeconds() {
    return registeredExpirationDays * 24 * 60 * 60;
  }

  /**
   * Returns the anonymous token expiration in milliseconds.
   *
   * @return expiration time in milliseconds
   */
  public long anonymousExpirationMs() {
    return (long) anonymousExpirationDays * 24 * 60 * 60 * 1000;
  }

  /**
   * Returns the registered token expiration in milliseconds.
   *
   * @return expiration time in milliseconds
   */
  public long registeredExpirationMs() {
    return (long) registeredExpirationDays * 24 * 60 * 60 * 1000;
  }
}
