package ru.trylogic.gradle.graxe.internal.installers

import groovy.transform.InheritConstructors
import ru.trylogic.gradle.internal.installers.ArchivedResolvedArtifactInstaller

@InheritConstructors
class NekoInstaller extends ArchivedResolvedArtifactInstaller {

    @Override
    protected void install(AntBuilder ant, File destination) {
        ant.delete(dir : destination)
        super.install(ant, destination)
        ant.chmod(dir: destination, includes: "**/neko", perm: "+x")
    }

    @Override
    protected void untarGzTo(AntBuilder ant, File destination) {
        ant.untar(src: artifact.file.absolutePath, dest: destination, overwrite: "true", compression: "gzip") {
            mapper {
                globmapper(from: "neko-2.0.0-osx/*", to: "*")
                globmapper(from: "neko-2.0.0-linux/*", to: "*")
                globmapper(from: "neko-2.0.0-win/*", to: "*")
            }
        }
    }
}
