package ru.trylogic.gradle.graxe.repository

import org.apache.ivy.core.module.descriptor.Artifact
import org.apache.ivy.core.module.descriptor.Configuration
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
import org.apache.ivy.core.module.descriptor.DependencyDescriptor
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.core.resolve.ResolveData
import org.apache.ivy.plugins.resolver.URLResolver
import org.apache.ivy.plugins.resolver.packager.BuiltFileResource
import org.apache.ivy.plugins.resolver.util.ResolvedResource
import org.apache.ivy.plugins.resolver.util.ResourceMDParser
import org.apache.ivy.util.url.BasicURLHandler
import org.apache.ivy.util.url.URLHandler
import org.apache.ivy.util.url.URLHandlerRegistry
import org.gradle.api.internal.artifacts.ivyservice.IvyXmlModuleDescriptorWriter
import ru.trylogic.gradle.graxe.utils.HaxeLibUtils

class HaxeLibResolver extends URLResolver {

    public HaxeLibResolver() {
        m2compatible = true;

        addArtifactPattern("http://lib.haxe.org/files/3.0/[artifact]-[revision].[ext]")//?([classifier])")
    }

    @Override
    protected ResolvedResource findResourceUsingPattern(ModuleRevisionId mrid, String pattern, Artifact artifact, ResourceMDParser rmdparser, Date date) {
        def defaultHandler = URLHandlerRegistry.default;
        URLHandlerRegistry.default = new BasicURLHandler() {
            @Override
            URLHandler.URLInfo getURLInfo(URL url, int timeout) {
                def result = super.getURLInfo(url, timeout)
                return result == UNAVAILABLE ? UNAVAILABLE : url.openConnection().contentType == "text/html" ? UNAVAILABLE : result;
            }
        }
        try {
            return super.findResourceUsingPattern(mrid, pattern, artifact, rmdparser, date)
        } finally {
            URLHandlerRegistry.default = defaultHandler
        }
    }

    @Override
    protected ModuleRevisionId convertM2IdForResourceSearch(ModuleRevisionId mrid) {
        return ModuleRevisionId.newInstance(mrid, HaxeLibUtils.getHaxeVersionFromNormal(mrid.revision));
    }

    @Override
    ResolvedResource findIvyFileRef(DependencyDescriptor dd, ResolveData data) {
        if(dd.dependencyRevisionId.organisation != "lib.haxe.org") {
            return super.findIvyFileRef(dd, data);
        }

        def tempFile = File.createTempFile("ivy.xml", System.currentTimeMillis().toString())

        def moduleDescriptor = DefaultModuleDescriptor.newBasicInstance(dd.dependencyRevisionId, null)

        moduleDescriptor.addConfiguration(new Configuration("compile"))

        def writer = new IvyXmlModuleDescriptorWriter();
        writer.write(moduleDescriptor, tempFile)

        return new ResolvedResource(new BuiltFileResource(tempFile), dd.dependencyRevisionId.revision)
    }
}
