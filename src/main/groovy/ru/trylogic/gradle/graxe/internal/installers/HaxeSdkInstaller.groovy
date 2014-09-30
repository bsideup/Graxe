package ru.trylogic.gradle.graxe.internal.installers

import groovy.transform.InheritConstructors
import ru.trylogic.gradle.internal.installers.ArchivedResolvedArtifactInstaller

@InheritConstructors
class HaxeSdkInstaller extends ArchivedResolvedArtifactInstaller {

    @Override
    protected void installMissing(File destination) {
        ant.delete(dir : destination)
        
        super.installMissing(destination)

        ant.chmod(dir: destination, includes: "**/haxe", perm: "+x")
        ant.chmod(dir: destination, includes: "**/haxelib", perm: "+x")
    }
    
    @Override
    protected void untarGzTo(File destination) {
        ant.untar(src: artifact.file.absolutePath, dest: destination, overwrite: "true", compression: "gzip") {
            mapper {
                globmapper(from: "build/*", to: "*")
                regexpmapper(from: "haxe-([^/]+)/(.+)", to: "\\2")
            }
        }
    }
}
