package ru.trylogic.gradle.graxe.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.trylogic.gradle.graxe.extensions.HaxeExtension
import ru.trylogic.gradle.graxe.internal.installers.NekoInstaller

class InstallNekoTask extends DefaultTask implements Runnable {

    static final String NAME = "installNeko"
    
    InstallNekoTask() {
        group = "build"
    }

    @TaskAction
    @Override
    void run() {
        def haxeExtension = project.extensions.findByName(HaxeExtension.NAME) as HaxeExtension

        def installer = new NekoInstaller(haxeExtension.resolvedNekoArtifact)
        
        installer.install(new File(haxeExtension.getNekoPath()))
    }
}
