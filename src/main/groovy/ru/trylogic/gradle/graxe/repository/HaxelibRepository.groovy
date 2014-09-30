package ru.trylogic.gradle.graxe.repository

import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ConfiguredModuleComponentRepository
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ResourceAwareResolveResult
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.ResolverStrategy
import org.gradle.api.internal.artifacts.metadata.ModuleVersionArtifactMetaData
import org.gradle.api.internal.artifacts.repositories.DefaultIvyArtifactRepository
import org.gradle.api.internal.artifacts.repositories.resolver.ExternalResourceArtifactResolver
import org.gradle.api.internal.artifacts.repositories.resolver.IvyResolver
import org.gradle.api.internal.artifacts.repositories.resolver.ResourcePattern
import org.gradle.api.internal.artifacts.repositories.transport.RepositoryTransportFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.reflect.Instantiator
import org.gradle.internal.resource.LocallyAvailableExternalResource
import org.gradle.internal.resource.ResourceException
import org.gradle.internal.resource.local.FileStore
import org.gradle.internal.resource.local.LocallyAvailableResourceCandidates
import org.gradle.internal.resource.local.LocallyAvailableResourceFinder

class HaxelibRepository extends DefaultIvyArtifactRepository {

    RepositoryTransportFactory transportFactory;
    LocallyAvailableResourceFinder<ModuleVersionArtifactMetaData> locallyAvailableResourceFinder;
    ResolverStrategy resolverStrategy;
    FileStore<ModuleVersionArtifactMetaData> artifactFileStore;

    HaxelibRepository(FileResolver fileResolver, PasswordCredentials credentials, RepositoryTransportFactory transportFactory, LocallyAvailableResourceFinder<ModuleVersionArtifactMetaData> locallyAvailableResourceFinder, Instantiator instantiator, ResolverStrategy resolverStrategy, FileStore<ModuleVersionArtifactMetaData> artifactFileStore) {
        super(fileResolver, credentials, transportFactory, locallyAvailableResourceFinder, instantiator, resolverStrategy, artifactFileStore)
        
        this.transportFactory = transportFactory;
        this.locallyAvailableResourceFinder = locallyAvailableResourceFinder;
        this.resolverStrategy = resolverStrategy;
        this.artifactFileStore = artifactFileStore;
        name = "haxelibRepo"
    }

    @Override
    ConfiguredModuleComponentRepository createResolver() {
        def transport = transportFactory.createTransport(new HashSet<String>(), getName(), getCredentials());
        return new IvyResolver(getName(), transport, locallyAvailableResourceFinder, false, resolverStrategy, artifactFileStore){
            @Override
            protected ExternalResourceArtifactResolver createArtifactResolver(List<ResourcePattern> ivyPatterns, List<ResourcePattern> artifactPatterns) {
                return new ExternalResourceArtifactResolver() {

                    @Override
                    LocallyAvailableExternalResource resolveMetaDataArtifact(ModuleVersionArtifactMetaData artifact, ResourceAwareResolveResult result) {
                        return null
                    }

                    @Override
                    LocallyAvailableExternalResource resolveArtifact(ModuleVersionArtifactMetaData artifact, ResourceAwareResolveResult result) {
                        def url = getURL(artifact);

                        result.attempted(url);

                        LocallyAvailableResourceCandidates localCandidates = locallyAvailableResourceFinder.findCandidates(artifact);
                        try {
                            LocallyAvailableExternalResource resource = transport.resourceAccessor.getResource(new URI(url), { File downloadedResource ->
                                    return artifactFileStore.move(artifact, downloadedResource);
                            }, localCandidates);
                            if (resource != null) {
                                return resource;
                            }
                        } catch (IOException e) {
                            throw new ResourceException("Could not get resource '$url'.", e);
                        }
                    }

                    @Override
                    boolean artifactExists(ModuleVersionArtifactMetaData artifact, ResourceAwareResolveResult result) {
                        if(artifact.id.componentIdentifier.group != "lib.haxe.org" || artifact.toIvyArtifact().ext != "har") {
                            return false;
                        }
                        
                        def url = getURL(artifact);

                        result.attempted(url);

                        InputStream inputStream = new DataInputStream(new URL(url).openStream());

                        try {
                            return inputStream.readInt() == 0x504b0304;
                        } finally {
                            inputStream.close();
                        }
                    }
                    
                    protected String getURL(ModuleVersionArtifactMetaData artifact) {
                        def identifier = artifact.id.componentIdentifier

                        return "http://lib.haxe.org/files/3.0/${identifier.module}-${identifier.version.replace(".", ",")}.zip"
                    }
                }
            }
        }
    }

}
