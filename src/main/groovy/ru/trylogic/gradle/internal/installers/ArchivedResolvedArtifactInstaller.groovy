package ru.trylogic.gradle.internal.installers

import org.gradle.api.artifacts.ResolvedArtifact

class ArchivedResolvedArtifactInstaller extends ResolvedArtifactInstaller {
    
    AntBuilder ant;

    ArchivedResolvedArtifactInstaller(ResolvedArtifact artifact) {
        this(artifact, new AntBuilder())
    }

    ArchivedResolvedArtifactInstaller(ResolvedArtifact artifact, AntBuilder ant) {
        super(artifact)
        this.ant = ant
    }

    @Override
    void install(File destination) {
        def checkFile = getCheckFile(destination);

        if(checkFile.exists()){
            return;
        }
        
        installMissing(destination)
        
        ant.touch(file : checkFile)
    }
    
    protected File getCheckFile(File destination) {
        return new File(destination, ".installed");
    }
    
    protected void installMissing(File destination) {
        switch (artifact.extension) {
            case "tar.gz":
                untarGzTo(destination);
                break;
            case "zip":
                unzipTo(destination);
                break;
            default:
                throw new RuntimeException("Unknown extension: ${artifact.extension}");
        }
    }
    
    protected void untarGzTo(File destination) {
        ant.untar(src: artifact.file.absolutePath, dest: destination, overwrite: "true", compression: "gzip")
    }
    
    protected void unzipTo(File destination) {
        ant.unzip(src: artifact.file.absolutePath, dest: destination, overwrite: "true")
    }
}
