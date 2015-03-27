if (! new File(basedir, "consume/target/classes/filein1").isFile()) return false
if (! new File(basedir, "build.log").text.contains("Only linking")) return false
if (new File(basedir, "build.log").text.contains("[CONSUME] Expanding")) return false

