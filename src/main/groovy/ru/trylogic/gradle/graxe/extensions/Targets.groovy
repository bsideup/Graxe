package ru.trylogic.gradle.graxe.extensions

import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class Targets extends AbstractNamedDomainObjectContainer<Target> {

    protected final ProjectInternal project;

    @Inject
    Targets(Instantiator instantiator, ProjectInternal project) {
        super(Target, instantiator)
        this.project = project;
    }

    @Override
    protected Target doCreate(String name) {
        def kind = Target.Kind.valueOf(Target.Kind, name?.toUpperCase());
        return new Target(kind, project)
    }
}