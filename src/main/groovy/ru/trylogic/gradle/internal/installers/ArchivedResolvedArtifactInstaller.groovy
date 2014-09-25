package ru.trylogic.gradle.internal.installers

import groovy.transform.InheritConstructors

@InheritConstructors
class ArchivedResolvedArtifactInstaller extends ResolvedArtifactInstaller {

    @Override
    void install(File destination) {
        def checkFile = getCheckFile(destination);

        if(checkFile.exists()){
            return;
        }
        AntBuilder ant = new AntBuilder()
        install(ant, destination)
        ant.touch(file : checkFile)
    }
    
    protected File getCheckFile(File destination) {
        return new File(destination, ".installed");
    }
    
    protected void install(AntBuilder ant, File destination) {
        switch (artifact.extension) {
            case "tar.gz":
                untarGzTo(ant, destination);
                break;
            case "zip":
                unzipTo(ant, destination);
                break;
            default:
                throw new RuntimeException("Unknown extension: ${artifact.extension}");
        }
    }
    
    protected void untarGzTo(AntBuilder ant, File destination) {
        ant.untar(src: artifact.file.absolutePath, dest: destination, overwrite: "true", compression: "gzip")
    }
    
    protected void unzipTo(AntBuilder ant, File destination) {
        ant.unzip(src: artifact.file.absolutePath, dest: destination, overwrite: "true")
    }
}
