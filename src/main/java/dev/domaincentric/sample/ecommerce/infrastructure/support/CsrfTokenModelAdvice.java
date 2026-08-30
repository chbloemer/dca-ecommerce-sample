package dev.domaincentric.sample.ecommerce.infrastructure.support;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes the CSRF token to every Pug template as {@code _csrf}, so each writing form renders
 * {@code input(type="hidden" name=_csrf.parameterName value=_csrf.token)} — the Spring counterpart
 * of ASP.NET Core's {@code @Html.AntiForgeryToken()}.
 *
 * <p>Framework support, not an adapter: the token is a cross-cutting web concern and belongs to no
 * bounded context. Pages rendered without Spring Security's CSRF filter (none today) simply see no
 * attribute; templates guard the input with {@code if _csrf}.
 */
@ControllerAdvice
public class CsrfTokenModelAdvice {

  @ModelAttribute
  public void exposeCsrfToken(final HttpServletRequest request, final Model model) {
    final CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    if (token != null) {
      model.addAttribute("_csrf", token);
    }
  }
}
