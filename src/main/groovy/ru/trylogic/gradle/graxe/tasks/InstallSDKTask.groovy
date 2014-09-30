package ru.trylogic.gradle.graxe.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.trylogic.gradle.graxe.extensions.HaxeExtension
import ru.trylogic.gradle.graxe.internal.installers.HaxeSdkInstaller

class InstallSDKTask extends DefaultTask implements Runnable {

    static final String NAME = "installSdk"
    
    InstallSDKTask() {
        group = "build"

        dependsOn InstallNekoTask.NAME
    }

    @TaskAction
    @Override
    void run() {
        def haxeExtension = project.extensions.findByName(HaxeExtension.NAME) as HaxeExtension

        def installer = new HaxeSdkInstaller(haxeExtension.resolvedHaxeArtifact, ant)

        def destination = new File(haxeExtension.getSdkPath())
        
        installer.install(destination)

        def libDir = haxeExtension.haxelibPath

        ant.sequential {
            mkdir(dir : libDir)
            echo(file : new File(destination, ".haxelib"), message : libDir)
        }
    }
}
