package ru.trylogic.gradle.graxe.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.trylogic.gradle.graxe.extensions.HaxeExtension
import ru.trylogic.gradle.graxe.extensions.Target
import ru.trylogic.gradle.graxe.internal.programs.NekoProgram

class RunTask extends DefaultTask {

    static final String NAME = "runHaxe"

    RunTask() {
        group = "run"

        dependsOn CompileTask.NAME
    }
    
    @TaskAction
    void run() {
        def haxeExtension = project.extensions.findByName(HaxeExtension.NAME) as HaxeExtension

        def nekoTarget = haxeExtension.targets.findByName(Target.Kind.NEKO.name())
        
        assert nekoTarget, "You should specify neko target if you want to execute $NAME"

        (new NekoProgram(haxeExtension.sdkPath, haxeExtension.nekoPath)).run(project.buildDir.absolutePath, nekoTarget.finalName)
    }
}
