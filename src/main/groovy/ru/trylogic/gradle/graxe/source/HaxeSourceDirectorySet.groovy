package ru.trylogic.gradle.graxe.source

import groovy.transform.InheritConstructors
import org.gradle.api.internal.file.DefaultSourceDirectorySet

@InheritConstructors
class HaxeSourceDirectorySet extends DefaultSourceDirectorySet {
    static final String DEFAULT = "src/main/haxe"
    
    String defaultSet() {
        return DEFAULT;
    }
}
