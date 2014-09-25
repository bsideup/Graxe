package ru.trylogic.gradle.graxe.extensions

import groovy.transform.ToString
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.internal.file.BaseDirFileResolver
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.nativeplatform.services.FileSystems
import org.gradle.internal.reflect.Instantiator
import ru.trylogic.gradle.graxe.GraxePlugin
import ru.trylogic.gradle.graxe.utils.HaxeLibUtils

@ToString
class HaxeExtension {

    static final String NAME = 'haxe';
    
    Targets targets;
    
    protected ProjectInternal project;
    
    FileResolver graxeBaseDirResolver;

    FileResolver sdkBaseDirResolver;

    FileResolver nekoBaseDirResolver;
    
    File hxmlFile;
    
    String sdkPath;
    
    String nekoPath;

    File getHxmlFile() {
        return hxmlFile ?: new File(project.buildDir, project.name + ".hxml")
    }

    File getNekoPathFile() {
        getNekoBaseDirResolver().resolve(resolvedNekoArtifact.moduleVersion.id.version)
    }

    String getNekoPath() {
        return nekoPath ?: getNekoPathFile().absolutePath
    }

    File getSdkPathFile() {
        getSdkBaseDirResolver().resolve(resolvedHaxeArtifact.moduleVersion.id.version)
    }
    
    String getSdkPath() {
        return sdkPath ?: getSdkPathFile().absolutePath
    }
    
    File getHaxeLibDirectoryFile(ResolvedArtifact resolvedArtifact) {
        return new File(new File(getSdkPathFile(), ".haxelib").text, resolvedArtifact.name)
    }
    
    File getHaxeLibArtifactFile(ResolvedArtifact resolvedArtifact) {
        def version = HaxeLibUtils.getHaxeVersionFromNormal(resolvedArtifact.moduleVersion.id.version)  
        return new File(getHaxeLibDirectoryFile(resolvedArtifact), version)
    }

    ResolvedArtifact getResolvedNekoArtifact() {
        return getResolvedArtifactFromConfiguration(GraxePlugin.NEKO_CONFIGURATION)
    }

    ResolvedArtifact getResolvedHaxeArtifact() {
        return getResolvedArtifactFromConfiguration(GraxePlugin.HAXE_SDK_CONFIGURATION)
    }

    HaxeExtension(ProjectInternal project) {
        this.project = project;
        def instantiator = project.services.get(Instantiator)
        targets = instantiator.newInstance(Targets, instantiator, project)

        def gradleFxUserHomeDirResolver = new BaseDirFileResolver(FileSystems.default, project.gradle.gradleUserHomeDir)
        graxeBaseDirResolver = gradleFxUserHomeDirResolver.withBaseDir("graxe")

        sdkBaseDirResolver = graxeBaseDirResolver.withBaseDir("sdk");
        nekoBaseDirResolver = graxeBaseDirResolver.withBaseDir("neko");
    }

    void targets(Closure cl) {
        targets.configure(cl);
    }
    
    protected ResolvedArtifact getResolvedArtifactFromConfiguration(String configurationName) {
        def configuration = project.configurations.findByName(configurationName)

        assert configuration != null
        assert configuration.dependencies.size() == 1, "You should specify only 1 ${configuration.name} dependency"

        def dependency = configuration.dependencies.iterator().next()

        assert dependency instanceof ModuleDependency
        assert dependency.artifacts.size() == 1

        return configuration.resolvedConfiguration.resolvedArtifacts.iterator().next()
    }

}
