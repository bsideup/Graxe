package ru.trylogic.gradle.graxe.tasks

import org.apache.commons.io.filefilter.DirectoryFileFilter
import org.apache.commons.io.filefilter.FileFileFilter
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.tasks.TaskAction
import org.gradle.util.GFileUtils
import ru.trylogic.gradle.graxe.GraxePlugin
import ru.trylogic.gradle.graxe.extensions.HaxeExtension
import ru.trylogic.gradle.graxe.utils.HaxeLibUtils

class UnpackDependenciesTask extends DefaultTask {

    static final String NAME = "unpackHxmlDependencies"

    UnpackDependenciesTask() {
        group = "prepare"
        
        dependsOn InstallSDKTask.NAME
    }
    
    @TaskAction
    void run() {
        def haxeExtension = project.extensions.findByName(HaxeExtension.NAME) as HaxeExtension

        unpackConfigurationDependencies(project.configurations.findByName(GraxePlugin.COMPILE_CONFIGURATION_NAME)?.resolvedConfiguration, haxeExtension)
        unpackConfigurationDependencies(project.configurations.findByName(GraxePlugin.BUILD_CONFIGURATION_NAME)?.resolvedConfiguration, haxeExtension)
        unpackConfigurationDependencies(project.configurations.findByName(GraxePlugin.RUNTIME_CONFIGURATION_NAME)?.resolvedConfiguration, haxeExtension)
    }

    public void unpackConfigurationDependencies(ResolvedConfiguration resolvedConfiguration, HaxeExtension haxeExtension) {
        AntBuilder ant = null;
        for (resolvedArtifact in resolvedConfiguration.resolvedArtifacts) {
            def version = HaxeLibUtils.getHaxeVersionFromNormal(resolvedArtifact.moduleVersion.id.version)
            def destinationDirectoryFile = new File(haxeExtension.getHaxeLibDirectoryFile(resolvedArtifact), version)

            def installedCheckFile = new File(destinationDirectoryFile, ".installed");
            if (installedCheckFile.exists()) {
                continue;
            }

            ant = ant ?: new AntBuilder();
            ant.unzip(src: resolvedArtifact.file, dest: destinationDirectoryFile, overwrite: false) {
                mapper {
                    globmapper(from: "${resolvedArtifact.name}/*", to: "*")
                }
            }

            GFileUtils.writeStringToFile(new File(haxeExtension.getHaxeLibDirectoryFile(resolvedArtifact), ".current"), resolvedArtifact.moduleVersion.id.version)

            GFileUtils.touch(installedCheckFile)
        }
    }

    static File findHaxeLibRoot(File dir) {
        if (!dir.isDirectory()) {
            return null;
        }

        for(file in dir.listFiles(FileFileFilter.FILE as FileFilter)){
            if(file.name == "haxelib.json") {
                return file;
            }
        }

        for(file in dir.listFiles(DirectoryFileFilter.DIRECTORY as FileFilter)){
            def result = findHaxeLibRoot(file)

            if(result != null) {
                return result;
            }
        }

        return null;
    }
}
