package ru.trylogic.gradle.graxe.internal.installers

import groovy.transform.InheritConstructors
import ru.trylogic.gradle.internal.installers.ArchivedResolvedArtifactInstaller

@InheritConstructors
class HaxeSdkInstaller extends ArchivedResolvedArtifactInstaller {

    @Override
    protected void install(AntBuilder ant, File destination) {
        ant.delete(dir : destination)
        
        super.install(ant, destination)

        ant.chmod(dir: destination, includes: "**/haxe", perm: "+x")

        setupHaxelib(ant, destination)   
    }
    
    protected void setupHaxelib(AntBuilder ant, File destination) {
        def libDir = new File(destination, "lib")

        ant.sequential {
            mkdir(dir : libDir)
            echo(file : new File(destination, ".haxelib"), message : libDir.absolutePath)
            chmod(dir: destination, includes: "**/haxelib", perm: "+x")
        }
    }

    @Override
    protected void untarGzTo(AntBuilder ant, File destination) {
        ant.untar(src: artifact.file.absolutePath, dest: destination, overwrite: "true", compression: "gzip") {
            mapper {
                globmapper(from: "build/*", to: "*")
                globmapper(from: "haxe-3.0.0-osx/*", to: "*")
            }
        }
    }
}
