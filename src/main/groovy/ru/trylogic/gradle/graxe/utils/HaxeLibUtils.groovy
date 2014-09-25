package ru.trylogic.gradle.graxe.utils

class HaxeLibUtils {

    static String getHaxeVersionFromNormal(String normalVersion) {
        return normalVersion?.replace(".", ",")
    }
}
