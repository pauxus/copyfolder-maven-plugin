if (! new File(basedir, "consume/target/classes/filein1").isFile()) return false
if (! new File(basedir, "build.log").text.contains("Skipping copy, sourcefile is older.")) return false