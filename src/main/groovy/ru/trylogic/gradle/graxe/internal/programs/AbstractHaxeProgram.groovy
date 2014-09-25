package ru.trylogic.gradle.graxe.internal.programs

class AbstractHaxeProgram {

    protected final String sdkPath;
    
    protected final String nekoPath;

    AbstractHaxeProgram(String sdkPath, String nekoPath) {
        this.sdkPath = sdkPath
        this.nekoPath = nekoPath
    }

    protected Object execute(Map<String, Object> attributes = [:], Closure cl) {
        def ant = new AntBuilder();

        Map<String, Object> defaultAttributes = [failonerror: true]
        defaultAttributes.putAll(attributes);

        return ant.exec(defaultAttributes) {
            env(key : "HOME", value : sdkPath)
            env(key : "HAXEPATH", value : sdkPath)
            env(key : "HAXE_STD_PATH", value : new File(sdkPath, "std").absolutePath)
            env(key : "NEKOPATH", value : nekoPath)
            env(key : "LD_LIBRARY_PATH", value : nekoPath)
            env(key : "DYLD_LIBRARY_PATH", value : nekoPath)
            
            cl.delegate = delegate;
            cl.call(it)
        }
    }
}
