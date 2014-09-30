package ru.trylogic.gradle.graxe.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.TaskAction
import ru.trylogic.gradle.graxe.GraxePlugin
import ru.trylogic.gradle.graxe.extensions.HaxeExtension
import ru.trylogic.gradle.graxe.extensions.Target
import ru.trylogic.gradle.graxe.internal.programs.HaxeCompilerProgram

class CompileTask extends DefaultTask {
    
    static final String NAME = "compileHaxe"

    CompileTask() {
        group = BasePlugin.BUILD_GROUP
        
        dependsOn InstallSDKTask.NAME, GenerateHXMLTask.NAME
        
        project.afterEvaluate {
            def haxeExtension = project.extensions.findByName(HaxeExtension.NAME) as HaxeExtension
            if(haxeExtension.targets.any { Target target -> target.kind == Target.Kind.JAVA }) {
                def buildConfiguration = project.configurations.findByName(GraxePlugin.BUILD_CONFIGURATION_NAME)

                def containsHxjavaDependency = buildConfiguration.dependencies.any {
                    return it.group == "lib.haxe.org" && it.name == "hxjava"
                }
                
                if(!containsHxjavaDependency) {
                    throw new Exception("if you use java target, please specify hxjava dependency")
                }
            }
        }
    }

    @TaskAction
    void run() {
        def haxeExtension = project.extensions.findByName(HaxeExtension.NAME) as HaxeExtension

        def haxeProgram = new HaxeCompilerProgram(haxeExtension.sdkPath, haxeExtension.nekoPath);
        
        haxeProgram.execute(project.buildDir.absolutePath, haxeExtension.hxmlFile)
    }
}
