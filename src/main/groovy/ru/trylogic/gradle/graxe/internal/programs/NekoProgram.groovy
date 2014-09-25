package ru.trylogic.gradle.graxe.internal.programs

import groovy.transform.InheritConstructors

@InheritConstructors
class NekoProgram extends AbstractHaxeProgram {
    
    void run(String workingDir, String nekoExecutableFile) {

        execute(dir : workingDir, executable: new File(nekoPath, "neko").absolutePath, failonerror : true) {
            arg(line : nekoExecutableFile)
        }
    }
}
