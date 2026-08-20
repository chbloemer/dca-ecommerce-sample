package de.sample.aiarchitecture.sharedkernel.marker.strategic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that this bounded context consumes an <em>external system</em> — one that lives outside
 * this codebase — as its upstream.
 *
 * <p>Like {@link Upstream}, this is declared on the downstream side, because an external system has
 * no side in this codebase that could declare anything. The model dependency always points to the
 * external system, regardless of who initiates the exchange: a webhook the external system calls is
 * still <em>its</em> contract that this context conforms to or translates.
 *
 * <p>{@link #interaction()} names who initiates — which is also where the edge sits and where an
 * Anti-Corruption Layer must live. Protocol details (webhook, queue, SFTP batch, polling cadence)
 * belong in {@link #rationale()}; polling an external API is {@code OUTBOUND}, no matter how
 * event-like its semantics.
 *
 * <p><b>Usage:</b>
 *
 * <pre>{@code
 * @ExternalUpstream(
 *     name = "Payment Service Provider",
 *     translation = Upstream.Translation.ANTI_CORRUPTION_LAYER,
 *     interaction = ExternalUpstream.Interaction.OUTBOUND,
 *     rationale = "Synchronous payment operations behind the caller-owned PaymentProvider port")
 * package de.sample.aiarchitecture.checkout;
 * }</pre>
 *
 * <p><b>Architectural rules</b> (enforced by {@code ContextMapArchUnitTest}):
 *
 * <ul>
 *   <li>Only packages annotated with {@link BoundedContext} may declare {@code @ExternalUpstream}
 *   <li>{@code name} must not be blank and must not collide with an internal context's module name
 *   <li>{@code (name, interaction)} must be unique per declaring context
 *   <li>{@code ANTI_CORRUPTION_LAYER}: types from {@link #contractPackages()} appear only in the
 *       adapter matching the interaction (outgoing for {@code OUTBOUND}, incoming for {@code
 *       INBOUND})
 *   <li>{@code CONFORMIST}: types from {@link #contractPackages()} never reach the domain layer
 * </ul>
 *
 * <p>Without {@link #contractPackages()} (plain HTTP, no vendor SDK) the translation rules have
 * nothing to check — the declaration then documents the relationship and feeds the generated
 * context map.
 *
 * @see Upstream
 * @see BoundedContext
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ExternalUpstreams.class)
@Documented
public @interface ExternalUpstream {

  /** Display name of the external system (e.g. {@code "Payment Service Provider"}). */
  String name();

  /** How this context protects its model from the external system's model. */
  Upstream.Translation translation();

  /** Who initiates the exchange — and therefore on which adapter side the edge sits. */
  Interaction interaction();

  /**
   * Packages of the external system's contract types (vendor SDK, generated client), as ArchUnit
   * package patterns (e.g. {@code "com.stripe.."}). Empty when the contract is wire-level only.
   */
  String[] contractPackages() default {};

  /** Why this relationship exists, which protocol it uses, and why this translation was chosen. */
  String rationale() default "";

  /** Who initiates the exchange with the external system. */
  enum Interaction {
    /**
     * This context initiates (API call, polling, file upload) — edge in {@code adapter.outgoing..}.
     */
    OUTBOUND,
    /**
     * The external system initiates (webhook, queue message, file drop) — edge in {@code
     * adapter.incoming..}.
     */
    INBOUND
  }
}
