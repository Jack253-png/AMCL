package com.mcreater.amcl.util.java;

public interface JVMArgs {
    String FILE_ENCODING = "-Dfile.encoding=GB18030";
    String MINECRAFT_CLIENT_JAR = "-Dminecraft.client.jar=${jar_path}";
    String UNLOCK_EXPERIMENTAL_OPTIONS = "-XX:+UnlockExperimentalVMOptions";
    // gc choose
    String USE_G1GC = "-XX:+UseG1GC";
    String USE_ZGC = "-XX:+UseZGC";

    String STD_ENCODING = "-Dsun.stdout.encoding=GBK -Dsun.stderr.encoding=GBK";
    // G1GC options
    String YOUNG_SIZE_PERCENT = "-XX:G1NewSizePercent=20";
    String RESERVE_SIZE_PERCENT = "-XX:G1ReservePercent=20";
    String MAX_GC_PAUSE = "-XX:MaxGCPauseMillis=50";
    String HEAP_REGION_SIZE = "-XX:G1HeapRegionSize=16m";
    String ADAPTIVE_SIZE_POLICY = "-XX:-UseAdaptiveSizePolicy";
    String STACK_TRACE_FAST_THROW = "-XX:-OmitStackTraceInFastThrow";
    String DONT_COMPILE_HUGE_METHODS = "-XX:-DontCompileHugeMethods";
    // fml options
    String IGNORE_INVAILD_CERTIFICATES = "-Dfml.ignoreInvalidMinecraftCertificates=true";
    String IGNORE_PATCH_DISCREPANCIES = "-Dfml.ignorePatchDiscrepancies=true";
    // https options
    String USE_CODEBASE_ONLY = "-Djava.rmi.server.useCodebaseOnly=true";
    String TRUST_URL_CODE_BASE = "-Dcom.sun.jndi.rmi.object.trustURLCodebase=false";
    // fix log4j2
    String DISABLE_MSG_LOOPUPS = "-Dlog4j2.formatMsgNoLookups=true";
    // minecraft options
    String INTEL_PERFORMANCE = "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump";
    String MINECRAFT_LAUNCHER_BRAND = "-Dminecraft.launcher.brand=${launcher_brand}";
    String MINECRAFT_LAUNCHER_VERSION = "-Dminecraft.launcher.version=${launcher_version}";
    // native lib options
    String JAVA_LIBRARY_PATH = "-Djava.library.path=${native_path}";

}
