import sbt.*
import scala.sys.process.Process
import scala.sys.process.ProcessLogger

/** One incremental invocation path for all JVM generators. sbt owns the prerequisites and outputs. */
object Generator {

  /** Resolve once and execute that exact binary, including Windows executable spelling. */
  def executable(name: String): File = {
    val requested = file(name)
    val suffixes  = if (scala.util.Properties.isWin) Seq("", ".exe") else Seq("")
    val roots     = if (requested.isAbsolute || requested.getParent != null) Seq(requested.getParentFile)
    else sys.env.getOrElse("PATH", "").split(java.io.File.pathSeparator).toSeq.map(file)
    val candidates = for {
      root   <- roots
      suffix <- suffixes
    } yield new File(root, requested.getName + suffix)
    candidates.find(path => path.isFile && (scala.util.Properties.isWin || path.canExecute))
      .getOrElse(sys.error(s"generator executable not found: $name")).getAbsoluteFile
  }

  private def files(paths: Seq[File]): Seq[File] = paths.flatMap { path =>
    if (path.isDirectory) (path ** "*").get().filter(_.isFile) else Seq(path)
  }

  def run(
      mainClass: String,
      classpath: Seq[File],
      arguments: Seq[String],
      inputs: Seq[File],
      outputs: => Seq[File],
      cache: File,
      log: Logger,
      tools: Seq[File] = Seq.empty
  ): Seq[File] = {
    val options        = cache / "arguments.txt"
    val javaExecutable = executable((file(sys.props("java.home")) / "bin" / "java").getAbsolutePath)
    val binaries       = javaExecutable +: tools
    val versions       = binaries.map { binary =>
      val stdout = new StringBuilder
      val stderr = new StringBuilder
      val status = Process(Seq(binary.getAbsolutePath, "--version")).!(ProcessLogger(
        line => { val _ = stdout.append(line).append('\n') },
        line => { val _ = stderr.append(line).append('\n') }
      ))
      require(status == 0, s"cannot identify generator executable: $binary")
      val versionHash = java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256")
        .digest((stdout.toString + "\u0000" + stderr.toString).getBytes(java.nio.charset.StandardCharsets.UTF_8)))
      binary.getAbsolutePath + "\n" + versionHash
    }
    // These variables affect the selected tools' implicit options and input search paths.
    val environment = Seq(
      "JAVA_TOOL_OPTIONS",
      "JDK_JAVA_OPTIONS",
      "_JAVA_OPTIONS",
      "CPATH",
      "C_INCLUDE_PATH",
      "CPLUS_INCLUDE_PATH",
      "SDKROOT",
      "MACOSX_DEPLOYMENT_TARGET",
      "PYTHONPATH",
      "PYTHONHOME"
    )
      .map(key => key + "=" + sys.env.getOrElse(key, ""))
    val environmentHash = java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256")
      .digest(environment.mkString("\n").getBytes(java.nio.charset.StandardCharsets.UTF_8)))
    val content = (Seq("generator-cache-v2", mainClass, environmentHash) ++ arguments ++ versions)
      .map(value => s"${value.length}:$value").mkString("\n")
    if (!options.isFile || IO.read(options) != content) IO.write(options, content)
    val classes  = files(classpath)
    val generate = FileFunction.cached(cache / "files", FilesInfo.hash, FilesInfo.hash) { (_: Set[File]) =>
      val command =
        Seq(javaExecutable.getAbsolutePath, "-cp", classpath.mkString(java.io.File.pathSeparator), mainClass) ++
          arguments
      require(Process(command).!(ProcessLogger(log.info(_), log.info(_))) == 0, s"$mainClass failed")
      val result = outputs
      require(result.nonEmpty && result.forall(_.isFile), s"$mainClass did not produce its declared outputs")
      result.toSet
    }
    generate((files(inputs) ++ classes ++ binaries :+ options).toSet).toSeq.sortBy(_.getAbsolutePath)
  }
}
