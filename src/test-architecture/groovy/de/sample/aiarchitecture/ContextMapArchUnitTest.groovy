package de.sample.aiarchitecture

import de.sample.aiarchitecture.sharedkernel.marker.strategic.BoundedContext
import de.sample.aiarchitecture.sharedkernel.marker.strategic.ExternalUpstream
import de.sample.aiarchitecture.sharedkernel.marker.strategic.Partnership
import de.sample.aiarchitecture.sharedkernel.marker.strategic.Upstream
import spock.lang.Requires

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

/**
 * ArchUnit tests for the executable Context Map.
 *
 * The context map is declared as package annotations, each side declaring only what it controls:
 * - @Upstream (downstream side): the directed dependency, its translation strategy, and the
 *   consumed channel (api/events)
 * - @ExternalUpstream (downstream side): dependency on a system outside this codebase, with
 *   translation strategy and interaction direction (who initiates: OUTBOUND/INBOUND)
 * - @Partnership (both sides): symmetric governance relationship, no dependency permission
 * - @NamedInterface("api"/"events") + @OpenHostService (upstream side): the published contract
 * - @ApplicationModule.allowedDependencies: the enforced package boundary (Spring Modulith)
 *
 * These tests prove the declarations consistent with each other and with the actual code:
 * - declarations only on @BoundedContext packages, targets exist, no self-reference
 * - (context, channel) unique per declaring context, via never empty
 * - @Upstream declarations and allowedDependencies named-interface entries agree (both ways)
 * - ANTI_CORRUPTION_LAYER + API: upstream contract types only in outgoing adapters
 * - ANTI_CORRUPTION_LAYER + EVENTS: upstream contract types only in incoming adapters
 * - CONFORMIST: upstream contract types never in the domain layer
 * - every actual dependency on a foreign api/events package has a declaration
 * - @Partnership is symmetric
 *
 * Organizational patterns (Customer–Supplier etc.) are deliberately not machine-classified —
 * they live in the rationale() texts. Separate Ways is the absence of any declaration.
 *
 * The Spring Modulith agreement rule is the only Modulith-specific rule; it loads
 * ApplicationModule reflectively and is skipped when Modulith is not on the classpath, so this
 * test class works unchanged in non-Modulith projects.
 */
class ContextMapArchUnitTest extends BaseArchUnitTest {

  private static final String APPLICATION_MODULE_ANNOTATION = "org.springframework.modulith.ApplicationModule"

  static boolean springModulithPresent() {
    try {
      Class.forName(APPLICATION_MODULE_ANNOTATION)
      return true
    } catch (ClassNotFoundException ignored) {
      return false
    }
  }

  // ============================================================================
  // DECLARATION WELL-FORMEDNESS
  // ============================================================================

  def "Upstream, ExternalUpstream, and Partnership may only be declared on bounded context packages"() {
    given:
    Set<String> roots = allRootPackages()

    expect:
    roots.each { pkg ->
      if (getPackageAnnotation(pkg, BoundedContext) == null) {
        assert getPackageAnnotations(pkg, Upstream).isEmpty() :
        "Package '${pkg}' declares @Upstream but is not a @BoundedContext — context map declarations are reserved for bounded contexts"
        assert getPackageAnnotations(pkg, ExternalUpstream).isEmpty() :
        "Package '${pkg}' declares @ExternalUpstream but is not a @BoundedContext — context map declarations are reserved for bounded contexts"
        assert getPackageAnnotations(pkg, Partnership).isEmpty() :
        "Package '${pkg}' declares @Partnership but is not a @BoundedContext — context map declarations are reserved for bounded contexts"
      }
    }
  }

  def "ExternalUpstream declarations must be well-formed and unique per name and interaction"() {
    given:
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()
    Set<String> moduleNames = contexts.keySet().collect { shortName(it) } as Set

    expect:
    contexts.each { pkg, bc ->
      String source = shortName(pkg)
      List<String> edges = []
      getPackageAnnotations(pkg, ExternalUpstream).each { ExternalUpstream e ->
        assert !e.name().isBlank() :
        "Context '${source}' declares an @ExternalUpstream with a blank name"
        assert !moduleNames.contains(e.name()) :
        "Context '${source}' declares external system '${e.name()}', which is an internal bounded context module — use @Upstream for internal contexts"
        String edge = "${e.name()} :: ${e.interaction()}"
        assert !edges.contains(edge) :
        "Context '${source}' declares external system edge '${edge}' more than once — the identity of an @ExternalUpstream declaration is (name, interaction)"
        edges << edge
      }
    }
  }

  def "Upstream declarations must reference an existing bounded context and never the declaring context itself"() {
    given:
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()
    Set<String> moduleNames = contexts.keySet().collect { shortName(it) } as Set

    expect:
    contexts.each { pkg, bc ->
      String source = shortName(pkg)
      getPackageAnnotations(pkg, Upstream).each { Upstream u ->
        assert moduleNames.contains(u.context()) :
        "Context '${source}' declares @Upstream(context = \"${u.context()}\") but no bounded context module with that name exists (known: ${moduleNames})"
        assert u.context() != source :
        "Context '${source}' declares itself as its own upstream"
      }
    }
  }

  def "Upstream declarations must be unique per context and channel, and via must not be empty"() {
    given:
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()

    expect:
    contexts.each { pkg, bc ->
      String source = shortName(pkg)
      List<String> edges = []
      getPackageAnnotations(pkg, Upstream).each { Upstream u ->
        assert u.via().length > 0 :
        "Context '${source}': @Upstream(context = \"${u.context()}\") declares no channel — via must not be empty"
        u.via().each { Upstream.Consumes channel ->
          String edge = "${u.context()} :: ${channelName(channel)}"
          assert !edges.contains(edge) :
          "Context '${source}' declares (context, channel) '${edge}' more than once — the identity of an @Upstream declaration is (context, via); different translations per channel require separate annotations"
          edges << edge
        }
      }
    }
  }

  // ============================================================================
  // CONSISTENCY WITH SPRING MODULITH (skipped when Modulith is not on the classpath)
  // ============================================================================

  @Requires({ ContextMapArchUnitTest.springModulithPresent() })
  def "Upstream declarations and Spring Modulith allowedDependencies must agree"() {
    given:
    // Loaded reflectively so this test class compiles and runs in projects without Spring
    // Modulith — there the rule is skipped and the remaining rules still bind the declarations
    // to the code itself.
    Class<? extends java.lang.annotation.Annotation> applicationModule =
      Class.forName(APPLICATION_MODULE_ANNOTATION) as Class<? extends java.lang.annotation.Annotation>
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()
    Set<String> moduleNames = contexts.keySet().collect { shortName(it) } as Set

    expect:
    contexts.each { pkg, bc ->
      String source = shortName(pkg)
      Set<String> declared = declaredEdges(pkg)

      def module = getPackageAnnotation(pkg, applicationModule)
      Set<String> allowed = (module == null ? [] : module.allowedDependencies().toList())
        .collect { it.replaceAll(/\s*::\s*/, ' :: ').trim() }
        .findAll { it.contains(' :: ') }
        .findAll { moduleNames.contains(it.split(' :: ')[0]) }
        .toSet()

      assert declared == allowed :
      "Context '${source}': @Upstream declarations ${declared.sort()} and @ApplicationModule.allowedDependencies named-interface entries ${allowed.sort()} must describe the same edges — neither side may know more than the other"
    }
  }

  // ============================================================================
  // TRANSLATION ENFORCEMENT (channel-dependent)
  // ============================================================================

  def "Anti-Corruption Layer: upstream contract types must stay inside the matching adapter"() {
    given:
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()
    Map<String, String> packagesByName = contexts.keySet().collectEntries { [(shortName(it)): it] }

    expect:
    contexts.each { pkg, bc ->
      String source = shortName(pkg)
      getPackageAnnotations(pkg, Upstream)
        .findAll { it.translation() == Upstream.Translation.ANTI_CORRUPTION_LAYER }
        .each { Upstream u ->
          String targetPkg = packagesByName[u.context()]
          u.via().each { Upstream.Consumes channel ->
            // The ACL edge sits where the dependency crosses the boundary: outgoing adapters for
            // synchronous API calls, incoming adapters for consumed events.
            String allowedAdapter = channel == Upstream.Consumes.API
              ? "${pkg}.adapter.outgoing.."
              : "${pkg}.adapter.incoming.."

            noClasses()
              .that().resideInAPackage("${pkg}..")
              .and().resideOutsideOfPackage(allowedAdapter)
              .should().dependOnClassesThat()
              .resideInAPackage("${targetPkg}.${channelName(channel)}..")
              .allowEmptyShould(true)
              .because("Context '${source}' declares ANTI_CORRUPTION_LAYER towards '${u.context()}' (${channelName(channel)}) — upstream contract types must not leave ${allowedAdapter}; translate them there into the context's own model")
              .check(allClasses)
          }
        }
    }
  }

  def "Conformist: upstream contract types must never reach the domain layer"() {
    given:
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()
    Map<String, String> packagesByName = contexts.keySet().collectEntries { [(shortName(it)): it] }

    expect:
    contexts.each { pkg, bc ->
      String source = shortName(pkg)
      getPackageAnnotations(pkg, Upstream)
        .findAll { it.translation() == Upstream.Translation.CONFORMIST }
        .each { Upstream u ->
          String targetPkg = packagesByName[u.context()]
          u.via().each { Upstream.Consumes channel ->
            noClasses()
              .that().resideInAPackage("${pkg}.domain..")
              .should().dependOnClassesThat()
              .resideInAPackage("${targetPkg}.${channelName(channel)}..")
              .allowEmptyShould(true)
              .because("Context '${source}' conforms to '${u.context()}' (${channelName(channel)}), but conformism does not suspend domain purity — the domain layer stays free of foreign contract types")
              .check(allClasses)
          }
        }
    }
  }

  def "External system contract types must respect the declared translation and interaction"() {
    given:
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()

    expect:
    // Without contractPackages (wire-level contract, no vendor SDK) there is nothing to check —
    // the declaration then only documents the relationship and feeds the generated context map.
    contexts.each { pkg, bc ->
      String source = shortName(pkg)
      getPackageAnnotations(pkg, ExternalUpstream)
        .findAll { it.contractPackages().length > 0 }
        .each { ExternalUpstream e ->
          if (e.translation() == Upstream.Translation.ANTI_CORRUPTION_LAYER) {
            // The ACL sits where the exchange crosses the boundary: outgoing adapters when this
            // context initiates, incoming adapters when the external system does.
            String allowedAdapter = e.interaction() == ExternalUpstream.Interaction.OUTBOUND
              ? "${pkg}.adapter.outgoing.."
              : "${pkg}.adapter.incoming.."

            noClasses()
              .that().resideInAPackage("${pkg}..")
              .and().resideOutsideOfPackage(allowedAdapter)
              .should().dependOnClassesThat()
              .resideInAnyPackage(e.contractPackages())
              .allowEmptyShould(true)
              .because("Context '${source}' declares ANTI_CORRUPTION_LAYER towards external system '${e.name()}' (${e.interaction()}) — its contract types (${e.contractPackages().join(', ')}) must not leave ${allowedAdapter}")
              .check(allClasses)
          } else {
            noClasses()
              .that().resideInAPackage("${pkg}.domain..")
              .should().dependOnClassesThat()
              .resideInAnyPackage(e.contractPackages())
              .allowEmptyShould(true)
              .because("Context '${source}' conforms to external system '${e.name()}', but conformism does not suspend domain purity — the domain layer stays free of its contract types")
              .check(allClasses)
          }
        }
    }
  }

  def "Cross-context dependencies on published interfaces require an Upstream declaration"() {
    given:
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()

    expect:
    contexts.each { srcPkg, bc ->
      String source = shortName(srcPkg)
      Set<String> declared = declaredEdges(srcPkg)

      contexts.each { tgtPkg, tbc ->
        if (tgtPkg == srcPkg) {
          return
        }
        String target = shortName(tgtPkg)
        ["api", "events"].each { channel ->
          if (!declared.contains("${target} :: ${channel}".toString())) {
            noClasses()
              .that().resideInAPackage("${srcPkg}..")
              .should().dependOnClassesThat().resideInAPackage("${tgtPkg}.${channel}..")
              .allowEmptyShould(true)
              .because("Context '${source}' depends on '${target} :: ${channel}' without declaring it — add @Upstream(context = \"${target}\", translation = ..., via = ...) to its package-info")
              .check(allClasses)
          }
        }
      }
    }
  }

  // ============================================================================
  // PARTNERSHIP SYMMETRY
  // ============================================================================

  def "Partnership declarations must reference an existing bounded context, never themselves, and must be symmetric"() {
    given:
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()
    Map<String, String> packagesByName = contexts.keySet().collectEntries { [(shortName(it)): it] }

    expect:
    contexts.each { pkg, bc ->
      String source = shortName(pkg)
      getPackageAnnotations(pkg, Partnership).each { Partnership p ->
        assert packagesByName.containsKey(p.context()) :
        "Context '${source}' declares @Partnership(context = \"${p.context()}\") but no bounded context module with that name exists"
        assert p.context() != source :
        "Context '${source}' declares a partnership with itself"

        List<Partnership> reverse = getPackageAnnotations(packagesByName[p.context()], Partnership)
        assert reverse.any { it.context() == source } :
        "Partnership between '${source}' and '${p.context()}' is only declared on '${source}' — partnerships are symmetric, add @Partnership(context = \"${source}\") to '${p.context()}'"
      }
    }
  }

  // ============================================================================
  // DIAGNOSTIC
  // ============================================================================

  def "Diagnostic: Display declared context map"() {
    when:
    Map<String, BoundedContext> contexts = discoverBoundedContextPackages()

    then:
    println "=== Context Map (declared) ==="
    contexts.each { pkg, bc ->
      String source = shortName(pkg)
      getPackageAnnotations(pkg, Upstream).each { Upstream u ->
        u.via().each { channel ->
          println "  ${source} --[${u.translation()} / ${channelName(channel)}]--> ${u.context()}"
        }
      }
      getPackageAnnotations(pkg, ExternalUpstream).each { ExternalUpstream e ->
        println "  ${source} --[${e.translation()} / ${e.interaction()}]--> (external) ${e.name()}"
      }
      getPackageAnnotations(pkg, Partnership).each { Partnership p ->
        println "  ${source} <--[PARTNERSHIP]--> ${p.context()}"
      }
    }
    println "=============================="
    contexts.size() >= 1
  }

  // ============================================================================
  // HELPERS
  // ============================================================================

  private Set<String> allRootPackages() {
    Set<String> roots = [] as Set
    allClasses.each { javaClass ->
      String root = extractRootContextPackage(javaClass.getPackageName())
      if (root != null) {
        roots << root
      }
    }
    return roots
  }

  /** All declared upstream edges of a context as "target :: channel" strings. */
  private Set<String> declaredEdges(String contextPackage) {
    Set<String> edges = [] as Set
    getPackageAnnotations(contextPackage, Upstream).each { Upstream u ->
      u.via().each { channel ->
        edges << "${u.context()} :: ${channelName(channel)}".toString()
      }
    }
    return edges
  }

  private static String channelName(Upstream.Consumes channel) {
    return channel == Upstream.Consumes.API ? "api" : "events"
  }

  private String shortName(String packagePath) {
    return packagePath.substring(packagePath.lastIndexOf('.') + 1)
  }
}
