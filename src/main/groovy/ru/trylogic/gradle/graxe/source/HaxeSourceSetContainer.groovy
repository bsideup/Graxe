package ru.trylogic.gradle.graxe.source

import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator

class HaxeSourceSetContainer extends AbstractNamedDomainObjectContainer<HaxeSourceSet> {
    private final FileResolver fileResolver;
    private final Instantiator instantiator;

    public HaxeSourceSetContainer(FileResolver fileResolver, Instantiator classGenerator) {
        super(HaxeSourceSet, classGenerator);
        this.fileResolver = fileResolver;
        this.instantiator = classGenerator;
    }

    @Override
    protected HaxeSourceSet doCreate(String name) {
        HaxeSourceSet sourceSet = instantiator.newInstance(HaxeSourceSet, name, fileResolver);

        return sourceSet;
    }

}