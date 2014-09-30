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

        project.configurations.all {
            for (resolvedArtifact in it.resolvedConfiguration.resolvedArtifacts) {
                if(resolvedArtifact.extension != "har") {
                    continue;
                }

                def version = HaxeLibUtils.getHaxeVersionFromNormal(resolvedArtifact.moduleVersion.id.version)

                def haxeLibDirectoryFile = new File(haxeExtension.getHaxelibPath(), resolvedArtifact.name)

                def destinationDirectoryFile = new File(haxeLibDirectoryFile, version)

                def installedCheckFile = new File(destinationDirectoryFile, ".installed");
                if (installedCheckFile.exists()) {
                    continue;
                }

                ant.unzip(src: resolvedArtifact.file, dest: destinationDirectoryFile, overwrite: false) {
                    mapper {
                        globmapper(from: "${resolvedArtifact.name}/*", to: "*")
                    }
                }

                GFileUtils.writeStringToFile(new File(haxeLibDirectoryFile, ".current"), resolvedArtifact.moduleVersion.id.version)

                GFileUtils.touch(installedCheckFile)
            }
        }
    }

}
