package ru.trylogic.gradle.graxe.repository

import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.ResolverStrategy
import org.gradle.api.internal.artifacts.repositories.DefaultPasswordCredentials
import org.gradle.api.internal.artifacts.repositories.transport.RepositoryTransportFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator
import org.gradle.internal.resource.local.FileStore
import org.gradle.internal.resource.local.LocallyAvailableResourceFinder
import org.gradle.internal.service.ServiceRegistry

import javax.inject.Inject

class HaxeRepositories {

    static final String NAME = "haxe"

    protected final RepositoryHandler repositories;

    ServiceRegistry serviceRegistry;
    
    @Inject
    HaxeRepositories(RepositoryHandler repositories, ServiceRegistry serviceRegistry) {
        this.repositories = repositories
        this.serviceRegistry = serviceRegistry;
    }
    
    void haxeLib() {
        // TODO yup, it's ugly. But it's Gradle API :(
        serviceRegistry.get(ArtifactRepositoryContainer).add(new HaxelibRepository(
                serviceRegistry.get(FileResolver),
                new DefaultPasswordCredentials(),
                serviceRegistry.get(RepositoryTransportFactory),
                serviceRegistry.get(LocallyAvailableResourceFinder),
                serviceRegistry.get(Instantiator),
                serviceRegistry.get(ResolverStrategy),
                serviceRegistry.get(FileStore)
        ));
    }

    void haxeOrg() {
        repositories.ivy {
            artifactPattern "http://haxe.org/file/[artifact]-[revision](-[classifier]).[ext]"
            ivyPattern "http://haxe.org/file/[artifact]-[revision](-[classifier]).[ext]"
        }
    }

    void neko() {
        repositories.ivy {
            artifactPattern "file:///Users/bsideup/Downloads/[artifact]-[revision](-[classifier]).[ext]"
        }
    }
}

