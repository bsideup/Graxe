package ru.trylogic.gradle.graxe.repository

import org.gradle.api.artifacts.dsl.RepositoryHandler

class HaxeRepositories {

    static final String NAME = "haxe"

    protected final RepositoryHandler repositories;
    
    HaxeRepositories(RepositoryHandler repositories) {
        this.repositories = repositories
    }
    
    void haxeLib() {
        repositories.ivy {
            artifactPattern "http://dl.bintray.com/bsideup/haxelib/[artifact]-[revision].[ext]"
        }
    }

    void haxeOrg() {
        repositories.ivy {
            artifactPattern "http://haxe.org/file/[artifact]-[revision](-[classifier]).[ext]"
            ivyPattern "http://haxe.org/file/[artifact]-[revision](-[classifier]).[ext]"
        }
    }

    void neko() {
        repositories.ivy {
            artifactPattern "file:///Users/bsideup/Downloads/[artifact]-[revision](-[classifier]).[ext]"
        }
    }
}

