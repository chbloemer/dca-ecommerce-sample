package dev.domaincentric.sample.ecommerce;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.domaincentric.dca.archunit.DcaArchitecture;
import dev.domaincentric.dca.archunit.contextmap.ContextMapRenderer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Renders {@code docs/architecture/context-map.md} from the {@code @BoundedContext},
 * {@code @Upstream}, {@code @ExternalUpstream} and {@code @Partnership} package annotations — the
 * context map as a fully derived view.
 *
 * <p>The test regenerates the file on every run and fails if it was stale, so CI catches a context
 * map that drifted from the declarations. The fix is always: commit the regenerated file.
 */
class ContextMapDocumentationTest {

  private static final Path CONTEXT_MAP = Path.of("docs/architecture/context-map.md");

  @Test
  void contextMapDocumentMatchesTheDeclaredContextMap() throws IOException {
    DcaArchitecture architecture = DcaArchitecture.load(EcommerceLayout.layout());
    String generated = ContextMapRenderer.of(architecture).render();
    String existing = Files.exists(CONTEXT_MAP) ? Files.readString(CONTEXT_MAP) : null;

    Files.createDirectories(CONTEXT_MAP.getParent());
    Files.writeString(CONTEXT_MAP, generated);

    assertEquals(
        generated,
        existing,
        "docs/architecture/context-map.md was stale and has been regenerated from the package"
            + " annotations — review and commit it");
  }
}
