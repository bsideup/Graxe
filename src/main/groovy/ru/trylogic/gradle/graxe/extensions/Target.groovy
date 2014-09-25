package ru.trylogic.gradle.graxe.extensions

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.gradle.api.Named
import org.gradle.api.internal.project.ProjectInternal

@ToString
class Target implements Named {

    @TupleConstructor
    static enum Kind {
        SWF("swf", ".swf"),
        NEKO("neko", ".n"),
        JAVA("java", "_hxjava"),
        JS("js", ".js")

        String compilerKey;
        String artifactExtension;
    }

    Kind kind;

    String mainClass;

    String finalName;

    protected final ProjectInternal project;

    String getFinalName() {
        return finalName ?: "${project.name}${kind.artifactExtension}"
    }

    @Override
    String getName() {
        return kind.name()
    }

    Target(Kind kind, ProjectInternal project) {
        this.kind = kind
        this.project = project
    }

    void mainClass(String value) {
        this.mainClass = value;
    }

    void finalName(String value) {
        this.finalName = value;
    }
}