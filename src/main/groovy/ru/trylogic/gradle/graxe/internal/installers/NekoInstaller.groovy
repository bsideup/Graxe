package ru.trylogic.gradle.graxe.internal.installers

import groovy.transform.InheritConstructors
import ru.trylogic.gradle.internal.installers.ArchivedResolvedArtifactInstaller

@InheritConstructors
class NekoInstaller extends ArchivedResolvedArtifactInstaller {

    @Override
    protected void installMissing(File destination) {
        ant.delete(dir : destination)
        super.installMissing(destination)
        ant.chmod(dir: destination, includes: "**/neko", perm: "+x")
    }

    @Override
    protected void untarGzTo(File destination) {
        ant.untar(src: artifact.file.absolutePath, dest: destination, overwrite: "true", compression: "gzip") {
            mapper {
                regexpmapper(from: "neko-([^/]+)/(.+)", to: "\\2")
            }
        }
    }
}
