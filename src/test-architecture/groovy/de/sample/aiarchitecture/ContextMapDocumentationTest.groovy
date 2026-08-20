package de.sample.aiarchitecture

import de.sample.aiarchitecture.sharedkernel.marker.strategic.BoundedContext
import de.sample.aiarchitecture.sharedkernel.marker.strategic.Partnership
import de.sample.aiarchitecture.sharedkernel.marker.strategic.Upstream

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Generates docs/architecture/context-map.md from the @BoundedContext, @Upstream, and
 * @Partnership package annotations — the context map as a fully derived view.
 *
 * The test regenerates the file on every run and fails if it was stale, so CI catches a context
 * map that drifted from the declarations. The fix is always: commit the regenerated file.
 */
class ContextMapDocumentationTest extends BaseArchUnitTest {

  private static final Path CONTEXT_MAP = Paths.get("docs/architecture/context-map.md")

  def "docs/architecture/context-map.md must match the declared context map"() {
    given:
    String generated = renderContextMap()
    String existing = Files.exists(CONTEXT_MAP) ? Files.readString(CONTEXT_MAP) : null

    when:
    Files.createDirectories(CONTEXT_MAP.parent)
    Files.writeString(CONTEXT_MAP, generated)

    then:
    assert generated == existing :
    "docs/architecture/context-map.md was stale and has been regenerated from the package annotations — review and commit it"
  }

  private String renderContextMap() {
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()
    // Sort by module name for deterministic output
    List<String> packages = contexts.keySet().sort { shortName(it) }

    StringBuilder md = new StringBuilder()
    md << "# Context Map\n\n"
    md << "> **Generated file — do not edit.** Derived from the `@BoundedContext`, `@Upstream`, and\n"
    md << "> `@Partnership` package annotations by `ContextMapDocumentationTest`. After changing a\n"
    md << "> declaration, rerun `./gradlew test-architecture` and commit the regenerated file.\n\n"
    md << "Each side declares only what it controls: the downstream declares its consumed upstreams\n"
    md << "(`@Upstream`: translation strategy and channel), the upstream publishes its contract\n"
    md << "(`api`/`events` named interfaces, `@OpenHostService`), and partnerships are declared\n"
    md << "symmetrically on both contexts. Organizational patterns such as Customer–Supplier are not\n"
    md << "machine-classified; Separate Ways is the absence of any declaration. Non-context modules\n"
    md << "(e.g. backoffice) and the shared kernel are intentionally not part of this map.\n\n"

    md << "## Bounded Contexts\n\n"
    md << "| Module | Name | Description | Published interfaces |\n"
    md << "|---|---|---|---|\n"
    packages.each { pkg ->
      List<String> published = publishedInterfaces(pkg)
      md << "| ${shortName(pkg)} | ${contexts[pkg].name()} | ${contexts[pkg].description()} | ${published ? published.join(', ') : '—'} |\n"
    }
    md << "\n## Diagram\n\n"
    md << "```mermaid\ngraph LR\n"
    packages.each { pkg ->
      md << "  ${shortName(pkg)}[\"${contexts[pkg].name()}${publishedBadge(pkg)}\"]\n"
    }
    md << "\n"
    packages.each { pkg ->
      String source = shortName(pkg)
      getPackageAnnotations(pkg, Upstream).each { Upstream u ->
        u.via().each { channel ->
          String label = "${translationLabel(u.translation())} / ${channelName(channel)}"
          String arrow = channel == Upstream.Consumes.API ? "-->" : "-.->"
          md << "  ${source} ${arrow}|\"${label}\"| ${u.context()}\n"
        }
      }
    }
    partnershipPairs(contexts).each { pair, rationales ->
      md << "  ${pair[0]} ---|\"Partnership\"| ${pair[1]}\n"
    }
    md << "```\n\n"
    md << "Arrows point from downstream to upstream (dependency direction). Solid arrows are\n"
    md << "synchronous `api` consumption, dotted arrows are `events` consumption, plain lines are\n"
    md << "partnerships. Node badges list the context's published interfaces.\n\n"

    md << "## Upstream relationships\n\n"
    md << "| Downstream | Upstream | Channel | Translation | Rationale |\n"
    md << "|---|---|---|---|---|\n"
    packages.each { pkg ->
      String source = shortName(pkg)
      getPackageAnnotations(pkg, Upstream).each { Upstream u ->
        u.via().each { channel ->
          md << "| ${source} | ${u.context()} | ${channelName(channel)} | ${translationLabel(u.translation())} | ${u.rationale()} |\n"
        }
      }
    }
    md << "\n## Partnerships\n\n"
    Map<List<String>, List<String>> pairs = partnershipPairs(contexts)
    if (pairs.isEmpty()) {
      md << "None declared.\n"
    } else {
      md << "| Contexts | Rationale |\n"
      md << "|---|---|\n"
      pairs.each { pair, rationales ->
        md << "| ${pair[0]} ↔ ${pair[1]} | ${rationales.join(' — ')} |\n"
      }
    }
    return md.toString()
  }

  /** Deduplicated symmetric pairs (sorted) with the distinct rationales of both sides. */
  private Map<List<String>, List<String>> partnershipPairs(Map<String, BoundedContext> contexts) {
    Map<List<String>, List<String>> pairs = [:]
    contexts.keySet().sort { shortName(it) }.each { pkg ->
      String source = shortName(pkg)
      getPackageAnnotations(pkg, Partnership).each { Partnership p ->
        List<String> pair = [source, p.context()].sort()
        pairs.computeIfAbsent(pair) { [] }
        if (p.rationale() && !pairs[pair].contains(p.rationale())) {
          pairs[pair] << p.rationale()
        }
      }
    }
    return pairs
  }

  /** Published interfaces ("api", "events") a context actually contains classes for. */
  private List<String> publishedInterfaces(String contextPackage) {
    return ["api", "events"].findAll { channel ->
      allClasses.any { it.getPackageName().startsWith("${contextPackage}.${channel}") }
    }
  }

  /** Published interfaces of a context, shown as a node badge ("api", "events"). */
  private String publishedBadge(String contextPackage) {
    List<String> published = publishedInterfaces(contextPackage)
    return published ? "<br/><i>${published.join(' · ')}</i>" : ""
  }

  private static String translationLabel(Upstream.Translation translation) {
    return translation == Upstream.Translation.ANTI_CORRUPTION_LAYER ? "ACL" : "Conformist"
  }

  private static String channelName(Upstream.Consumes channel) {
    return channel == Upstream.Consumes.API ? "api" : "events"
  }

  private String shortName(String packagePath) {
    return packagePath.substring(packagePath.lastIndexOf('.') + 1)
  }
}
