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

        def installer = new HaxeSdkInstaller(haxeExtension.resolvedHaxeArtifact)
        
        installer.install(haxeExtension.sdkPathFile)
    }
}
