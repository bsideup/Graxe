package ru.trylogic.gradle.graxe.source

import org.gradle.api.Named
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.util.ConfigureUtil

class HaxeSourceSet implements Named {
    protected final SourceDirectorySet haxeSource;
    final String name;

    HaxeSourceSet(String name, FileResolver fileResolver) {
        this.name = name;

        String srcDisplayName = String.format("%s Haxe source", name);

        haxeSource = new HaxeSourceDirectorySet(srcDisplayName, fileResolver);
        haxeSource.getFilter().include("**/*.hx");
    }
    
    SourceDirectorySet getHaxe() {
        return haxeSource
    }

    HaxeSourceSet haxe(Closure configureClosure) {
        haxeSource.srcDirs = []
        ConfigureUtil.configure(configureClosure, haxeSource);
        return this;
    }
}
