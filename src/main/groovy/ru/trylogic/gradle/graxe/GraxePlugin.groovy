package ru.trylogic.gradle.graxe

import org.gradle.api.*
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.internal.os.OperatingSystem
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.plugins.LanguageBasePlugin

import ru.trylogic.gradle.graxe.extensions.HaxeExtension
import ru.trylogic.gradle.graxe.repository.HaxeRepositories
import ru.trylogic.gradle.graxe.source.*
import ru.trylogic.gradle.graxe.tasks.*

import javax.inject.Inject

class GraxePlugin implements Plugin<ProjectInternal> {

    static final String MAIN_CONFIGURATION_NAME = "main"
    static final String COMPILE_CONFIGURATION_NAME = "compile"
    static final String RUNTIME_CONFIGURATION_NAME = "runtime"
    static final String BUILD_CONFIGURATION_NAME = "build"
    
    static final String HAXE_SDK_CONFIGURATION = "sdk"
    static final String NEKO_CONFIGURATION = "neko"
    static final String HAXE_SDK_DEFAULT_GROUP = "org.haxe"
    static final String HAXE_SDK_DEFAULT_ARTIFACT_ID = "haxe"
    static final String NEKO_DEFAULT_ARTIFACT_ID = "neko"
    
    protected final Instantiator instantiator;
    
    @Inject
    GraxePlugin(Instantiator instantiator) {
        this.instantiator = instantiator;
    }
    
    void apply(ProjectInternal project) {
        project.plugins.apply(BasePlugin)
        project.plugins.apply(LanguageBasePlugin)

        project.configurations.create(HAXE_SDK_CONFIGURATION)
        project.configurations.create(NEKO_CONFIGURATION)
        project.configurations.create(COMPILE_CONFIGURATION_NAME)
        project.configurations.create(RUNTIME_CONFIGURATION_NAME)
        project.configurations.create(BUILD_CONFIGURATION_NAME)
        
        project.extensions.create(HaxeExtension.NAME, HaxeExtension, project);
        def sourceSets = project.extensions.create("sourceSets", HaxeSourceSetContainer, project.fileResolver, instantiator);
        
        def mainSourceSet = new HaxeSourceSet(MAIN_CONFIGURATION_NAME, project.fileResolver)
        mainSourceSet.haxe.srcDir(HaxeSourceDirectorySet.DEFAULT)
        sourceSets.add(mainSourceSet)

        project.repositories.extensions.create(HaxeRepositories.NAME, HaxeRepositories, project.repositories, project.services)

        project.dependencies.metaClass.haxeSdk = { String version = "3.1.3" ->
            def spec = "$HAXE_SDK_DEFAULT_GROUP:$HAXE_SDK_DEFAULT_ARTIFACT_ID:$version:${getPlatformClasifier()}"
            (delegate as DependencyHandler).add(HAXE_SDK_CONFIGURATION, spec)
        }

        project.dependencies.metaClass.neko = { String version = "2.0.0" ->
            def spec = "$HAXE_SDK_DEFAULT_GROUP:$NEKO_DEFAULT_ARTIFACT_ID:$version:${getPlatformClasifier()}"
            (delegate as DependencyHandler).add(NEKO_CONFIGURATION, spec)
        }

        project.dependencies.metaClass.haxeLib = { String artifactId, String version ->
            return [group : "lib.haxe.org", name : artifactId, version : version, ext : "har"]
        }

        project.tasks.create(InstallSDKTask.NAME, InstallSDKTask)
        project.tasks.create(InstallNekoTask.NAME, InstallNekoTask)
        project.tasks.create(UnpackDependenciesTask.NAME, UnpackDependenciesTask)
        project.tasks.create(GenerateHXMLTask.NAME, GenerateHXMLTask)
        project.tasks.create(CompileTask.NAME, CompileTask)
        project.tasks.create(RunTask.NAME, RunTask)
    }

    String getPlatformClasifier() {
        switch(OperatingSystem.current()) {
            case OperatingSystem.WINDOWS: return "win@zip";
            case OperatingSystem.MAC_OS: return "osx@tar.gz";
            case [OperatingSystem.LINUX, OperatingSystem.UNIX, OperatingSystem.SOLARIS]:
                def platform;
                switch(System.getProperty("os.arch").toLowerCase()) {
                    case "x86":
                        platform = "linux32";
                        break;
                    case "amd64":
                        platform = "linux64";
                        break;
                    default:
                        throw new Exception("Unknown OS architecture")
                }
                return "$platform@tar.gz";
            default:
                throw new RuntimeException("Unknown platform");
        }
    }
}
