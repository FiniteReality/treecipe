package finitereality.modlocator;

import net.neoforged.gradle.common.runs.run.RunImpl;
import net.neoforged.gradle.common.util.constants.RunsConstants;
import net.neoforged.gradle.dsl.common.runs.run.Run;
import org.gradle.api.DefaultTask;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.plugins.jvm.internal.JvmFeatureInternal;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.*;
import org.gradle.jvm.component.internal.JvmSoftwareComponentInternal;
import org.gradle.plugins.ide.idea.model.IdeaModel;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.gradle.ext.IdeaExtPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class GenerateRunMetadata extends DefaultTask
{
    @Input
    abstract Property<String> getFeature();

    @InputFiles
    abstract ConfigurableFileCollection getJarInJarDependencies();

    @Input
    abstract SetProperty<String> getRunConfigurations();

    @OutputDirectory
    abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    @SuppressWarnings("unchecked")
    public void execute()
    {
        final var runs = (NamedDomainObjectContainer<Run>)getProject()
            .getExtensions()
            .getByName(RunsConstants.Extensions.RUNS);
        final var jvmComponent = (JvmSoftwareComponentInternal)getProject()
            .getComponents()
            .getByName("java");
        final var ideaExtension = getProject()
            .getExtensions()
            .findByName("idea");
        getRunConfigurations().get().forEach(
            run -> generateRunConfiguration(
                runs.getByName(run),
                jvmComponent,
                (IdeaModel)ideaExtension));
    }


    private void generateRunConfiguration(
        final Run run,
        final JvmSoftwareComponentInternal jvmComponent,
        final @Nullable IdeaModel ideaModel)
    {
        final var file = getOutputDirectory().file(run.getName() + ".properties").get().getAsFile();
        final var props = new Properties();

        final var feature = jvmComponent.getFeatures().getByName(getFeature().get());

        if (ideaModel != null)
            props.put("ideaModule", getProject().getName() + "." + feature.getName());
        props.put("mainClass", run.getMainClass().get());
        props.put("workingDirectory", run.getWorkingDirectory().getLocationOnly().get().getAsFile().getAbsolutePath());
        props.put("jvmArguments", String.join(" ", ((RunImpl)run).realiseJvmArguments()));
        props.put("programArguments", String.join(" ", run.getProgramArguments().get()));
        props.put("ideBeforeRunTask", run.getName() + "IdeBeforeRun");
        props.put("modSources", String.join(",", feature.getSourceSet().getOutput().getFiles().stream().map(File::getAbsolutePath).toList()));
        props.put("jarJarDependencies", String.join(",", getJarInJarDependencies().getFiles().stream().map(File::getAbsolutePath).toList()));

        try
        {
            file.getParentFile().mkdirs();
            props.store(Files.newBufferedWriter(
                file.toPath(),
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE),
                null);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
