package finitereality.modlocator;

import net.neoforged.gradle.common.CommonProjectPlugin;
import net.neoforged.gradle.common.util.constants.RunsConstants;
import net.neoforged.gradle.dsl.common.runs.run.Run;
import org.gradle.api.Named;
import org.gradle.api.NamedDomainObjectCollection;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.attributes.*;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.jvm.component.internal.JvmSoftwareComponentInternal;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModLocatorPlugin implements Plugin<Project>
{
    public static final String RUNS_ELEMENTS = "runs";

    record AttributeValue<T extends Named>(Attribute<T> attribute, Class<T> clazz, String value) { }

    private static final List<AttributeValue<?>> ModConfigurationAttributes =
        List.of(
            new AttributeValue<>(
                Category.CATEGORY_ATTRIBUTE,
                Category.class,
                Category.LIBRARY),
            new AttributeValue<>(
                Bundling.BUNDLING_ATTRIBUTE,
                Bundling.class,
                Bundling.EXTERNAL),
            new AttributeValue<>(
                LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
                LibraryElements.class, RUNS_ELEMENTS),
            new AttributeValue<>(
                Usage.USAGE_ATTRIBUTE,
                Usage.class,
                Usage.JAVA_RUNTIME));

    private static final List<AttributeValue<?>> ModVariantAttributes =
        List.of(
            new AttributeValue<>(
                Category.CATEGORY_ATTRIBUTE,
                Category.class,
                Category.LIBRARY),
            new AttributeValue<>(
                Bundling.BUNDLING_ATTRIBUTE,
                Bundling.class,
                Bundling.EXTERNAL),
            new AttributeValue<>(
                LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
                LibraryElements.class, RUNS_ELEMENTS),
            new AttributeValue<>(
                Usage.USAGE_ATTRIBUTE,
                Usage.class,
                Usage.JAVA_RUNTIME));

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void applyAttributes(
        final AttributeContainer container,
        final ObjectFactory objectFactory,
        final List<AttributeValue<?>> attributes)
    {
        for (final var value : attributes)
        {
            final var namedObject = objectFactory.named(value.clazz(), value.value());
            container.attribute((Attribute)value.attribute(), namedObject);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void apply(final Project project)
    {
        project.getPlugins().apply(CommonProjectPlugin.class);

        final var objectFactory = project.getObjects();
        final var tasks = project.getTasks();

        var runNames = project.provider(() -> (NamedDomainObjectContainer<Run>)project.getExtensions()
                .getByName(RunsConstants.Extensions.RUNS))
            .map(NamedDomainObjectCollection::getNames);

        final var jvmComponent = (JvmSoftwareComponentInternal)project
            .getComponents()
            .getByName("java");

        jvmComponent.getFeatures().configureEach(feature -> {
            // Turns the compiled classes and resources into a mod metadata for the
            // locator to load
            final var prepareRunMetadata = tasks.register(
                feature == jvmComponent.getMainFeature()
                    ? "prepareRunMetadata"
                    : feature.getName() + "PrepareRunMetadata",
                GenerateRunMetadata.class,
                t -> {
                    t.setDescription(
                        "Prepares mod locator information for the '" + feature.getName() + "' feature.");

                    t.getRunConfigurations().addAll(runNames);

                    t.getFeature().set(feature.getName());

                    t.getOutputDirectory().convention(project.getLayout()
                        .getBuildDirectory()
                        .dir("runs/" + feature.getName()));

                    t.getJarInJarDependencies()
                        .from(project
                            .getConfigurations()
                            .getByName(feature == jvmComponent.getMainFeature()
                                ? "jarJar"
                                : feature.getName() + "JarJar"));
                });

            // Add the mod metadata to the API and runtime elements so they can be
            // referenced elsewhere
            feature.getRuntimeElementsConfiguration()
                .getOutgoing()
                .getVariants()
                .create(RUNS_ELEMENTS, variant -> {
                    variant.artifact(prepareRunMetadata, artifact -> {
                        artifact.setType("run-metadata");
                    });
                    applyAttributes(
                        variant.getAttributes(),
                        objectFactory,
                        ModVariantAttributes);
                });

            // Make the run depend on the task which generates the metadata file
            project.getExtensions().<NamedDomainObjectContainer<Run>>configure(
                RunsConstants.Extensions.RUNS,
                runs -> runs.configureEach(
                    run -> run.dependsOn(prepareRunMetadata)));
        });

        // Add the system property to the run
        project.afterEvaluate(ModLocatorPlugin::afterEvaluate);
    }

    private static void afterEvaluate(final Project project)
    {
        final var component = (JvmSoftwareComponentInternal)project
            .getComponents()
            .getByName("java");

        project.getExtensions().<NamedDomainObjectContainer<Run>>configure(
            RunsConstants.Extensions.RUNS,
            runs -> runs.configureEach(
                run -> configureRun(project, component, run)));
    }

    private static void configureRun(
        final Project project,
        final JvmSoftwareComponentInternal component,
        final Run run)
    {
        final var objectFactory = project.getObjects();
        final var modMetadata = component.getFeatures()
            .stream()
            .filter(feature -> run.getModSources().get().contains(feature.getSourceSet()))
            .map(feature -> (GenerateRunMetadata)project.getTasks()
                .getByName(feature == component.getMainFeature()
                    ? "prepareRunMetadata"
                    : feature.getName() + "PrepareRunMetadata"))
            .map(it -> it.getOutputDirectory()
                .file(run.getName() + ".properties")
                .get()
                .getAsFile())
            .collect(Collectors.toSet());

        final var runtimeDependencies = component.getFeatures()
            .stream()
            .filter(feature -> run.getModSources().get().contains(feature.getSourceSet()))
            .flatMap(feature -> feature
                .getRuntimeClasspathConfiguration()
                .getIncoming()
                .artifactView(config -> applyAttributes(
                    config.lenient(true)
                        .withVariantReselection()
                        .getAttributes(),
                    objectFactory,
                    ModVariantAttributes))
                .getArtifacts()
                .getResolvedArtifacts()
                .get()
                .stream()
                .map(ResolvedArtifactResult::getFile))
            .collect(Collectors.toSet());

        final var additionalMods
            = Stream.concat(
                runtimeDependencies.stream(),
                modMetadata.stream())
            .map(f -> f.toPath().toUri().toString())
            .collect(Collectors.joining(","));

        run.getSystemProperties()
            .put("finitereality.modlocator.additionalMods", additionalMods);
    }
}
