package ru.trylogic.gradle.internal.installers

import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.util.GFileUtils

class ResolvedArtifactInstaller {

    final ResolvedArtifact artifact;

    ResolvedArtifactInstaller(ResolvedArtifact artifact) {
        this.artifact = artifact
    }

    void install(File destination) {
        GFileUtils.copyFile(artifact.file, destination);
    }
}
