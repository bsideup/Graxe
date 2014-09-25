package ru.trylogic.gradle.graxe.internal.programs

import groovy.transform.InheritConstructors

@InheritConstructors
class HaxeLibProgram extends AbstractHaxeProgram {

    static final String LIB_FOLDER_NAME = "lib";
    
    void setup() {
        execute(inputstring : (new File(sdkPath, LIB_FOLDER_NAME).absolutePath)) {
            arg(line : "setup")
        }
    }

    void install(String libName) {
        execute() {
            arg(line : "install $libName")
        }
    }

    @Override
    protected Object execute(Map<String, Object> attributes = [:], Closure cl) {
        def defaultAttributes = [executable : new File(sdkPath, "haxelib").absolutePath, failonerror : true];
        defaultAttributes.putAll(attributes)
        return super.execute(defaultAttributes, cl)
    }
}
