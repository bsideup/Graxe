package ru.trylogic.gradle.graxe.repository

import org.apache.ivy.core.module.descriptor.Artifact
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.plugins.resolver.URLResolver
import org.apache.ivy.plugins.resolver.util.ResolvedResource
import org.apache.ivy.plugins.resolver.util.ResourceMDParser
import org.apache.ivy.util.url.URLHandler
import org.apache.ivy.util.url.URLHandlerRegistry
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.internal.artifacts.BaseRepositoryFactory

class HaxeRepositories {

    static final String NAME = "haxe"
    
    static class NoHeadURLResolver extends URLResolver {
        @Override
        protected ResolvedResource findResourceUsingPattern(ModuleRevisionId mrid, String pattern, Artifact artifact, ResourceMDParser rmdparser, Date date) {
            // work around http://code.google.com/p/support/issues/detail?id=660
            URLHandlerRegistry.getDefault().requestMethod = URLHandler.REQUEST_METHOD_GET
            try {
                return super.findResourceUsingPattern(mrid, pattern, artifact, rmdparser, date)
            } finally {
                URLHandlerRegistry.getDefault().requestMethod = URLHandler.REQUEST_METHOD_HEAD
            }
        }
    }

    protected final RepositoryHandler repositories;
    
    protected final BaseRepositoryFactory baseRepositoryFactory;

    HaxeRepositories(RepositoryHandler repositories, BaseRepositoryFactory baseRepositoryFactory) {
        this.repositories = repositories
        this.baseRepositoryFactory = baseRepositoryFactory
    }
    
    public ArtifactRepository haxeLib() {
        def resolver = new HaxeLibResolver();
        
        resolver.name = "haxelib"
        
        ArtifactRepository resolverRepository = baseRepositoryFactory.createResolverBackedRepository(resolver);

        repositories.add(resolverRepository);

        return resolverRepository;
    }

    public ArtifactRepository haxeOrg() {
        return noHeadRepository('haxeOrg', ["http://haxe.org/file/[artifact]-[revision]-[classifier].[ext]"]);
    }

    public ArtifactRepository neko() {
        return noHeadRepository('neko', ["http://nekovm.org/_media/[artifact]-[revision]-[classifier].[ext]?id=download&cache=cache"] );
    }

    protected noHeadRepository(String name, List<String> artifactPatterns) {
        def resolver = new NoHeadURLResolver(name : name, artifactPatterns: artifactPatterns);

        ArtifactRepository resolverRepository = baseRepositoryFactory.createResolverBackedRepository(resolver);

        repositories.add(resolverRepository);

        return resolverRepository;
    }
}

