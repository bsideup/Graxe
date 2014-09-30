package ru.trylogic.gradle.graxe.utils

import org.apache.commons.io.filefilter.DirectoryFileFilter
import org.apache.commons.io.filefilter.FileFileFilter

class HaxeLibUtils {

    static String getHaxeVersionFromNormal(String normalVersion) {
        return normalVersion?.replace(".", ",")
    }

    static File findHaxeLibRoot(File dir) {
        if (!dir.isDirectory()) {
            return null;
        }

        for(file in dir.listFiles(FileFileFilter.FILE as FileFilter)){
            if(file.name == "haxelib.json") {
                return file;
            }
        }

        for(file in dir.listFiles(DirectoryFileFilter.DIRECTORY as FileFilter)){
            def result = findHaxeLibRoot(file)

            if(result != null) {
                return result;
            }
        }

        return null;
    }
}
