package ru.trylogic.gradle.graxe.internal.programs

import groovy.transform.InheritConstructors

@InheritConstructors
class HaxeCompilerProgram extends AbstractHaxeProgram {

    void execute(String workingDir, File hxmlFile) {
        execute(dir : workingDir, executable: new File(sdkPath, "haxe").absolutePath, failonerror : true) {
            arg(line : hxmlFile.absolutePath)
        }
    }
}
