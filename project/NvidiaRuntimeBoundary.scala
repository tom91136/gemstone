import Paths.*
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile
import scala.jdk.CollectionConverters.*
import scala.sys.process.Process
import scala.sys.process.ProcessLogger
import scala.util.Using

/** Freshly compiles and executes NVIDIA runtime sources with every assembly package absent. */
object NvidiaRuntimeBoundary {
  private val packages  = Vector("net/kurobako/gemstone/isa/ptx/", "net/kurobako/gemstone/isa/sass/")
  private val mainClass = "net.kurobako.gemstone.tools.isa.NvidiaRuntimeCheck"

  def check(
      root: Path,
      java: Path,
      compilerClasspath: Seq[Path],
      dependencyClasspath: Seq[Path],
      productionSources: Seq[Path],
      harnessSources: Seq[Path],
      resourceDirectories: Seq[Path],
      scalacOptions: Seq[String],
      log: String => Unit,
      error: String => Unit,
      compilerJvmOptions: Seq[String] = Seq("-Xmx8g")
  ): Unit = {
    val sources = productionSources.filterNot(path => containsPathSegments(path, "asm")).distinct
    require(
      sources.nonEmpty && harnessSources.nonEmpty,
      "runtime boundary check requires production and harness sources"
    )
    require(productionSources.size > sources.size, "runtime boundary check did not exclude any assembly sources")
    require(!harnessSources.exists(path => containsPathSegments(path, "asm")), "runtime harness must not belong to asm")
    val protectedOptions = Set("-classpath", "--class-path", "-cp", "-d", "-sourcepath")
    require(
      !scalacOptions.exists(option => protectedOptions(option.takeWhile(_ != ':'))),
      "runtime boundary compiler options cannot override isolated paths"
    )
    val compilers = compilerClasspath.map(_.absolute).distinct
    require(compilers.nonEmpty, "runtime boundary check requires a Scala compiler classpath")
    val dependencies = dependencyClasspath.map(_.absolute).distinct
    val resources    = resourceDirectories.map(_.absolute).filter(_.isDirectory).distinct
    // A successful compile must not be able to resolve a runtime symbol from stale PTX or SASS output either.
    (compilers ++ dependencies ++ resources).distinct.foreach(assertNoRuntimeClasses)
    (root / "target/nvidia-runtime-boundary").withTempDirectory("fresh-") { temporary =>
      val output = (temporary / "classes").createDirectories()
      require(output.walk().isEmpty, "runtime boundary output must start empty")
      val arguments = Seq("-classpath", dependencies.mkString(File.pathSeparator), "-d", output.toString) ++
        scalacOptions ++ (sources ++ harnessSources).map(_.absolute.toString)
      val argumentFile = temporary / "compiler.args"
      val _            = Files.writeString(argumentFile, arguments.map(quoted).mkString("\n"), StandardCharsets.UTF_8)
      log(s"Compiling ${sources.size} NVIDIA runtime sources without asm into isolated output")
      run(
        Seq(java.toString) ++ compilerJvmOptions ++
          Seq("-cp", compilers.mkString(File.pathSeparator), "dotty.tools.dotc.Main", s"@$argumentFile"),
        root,
        "NVIDIA runtime-only compilation",
        log,
        error
      )
      val leaked = output.walk().filter(path => containsPathSegments(path, "asm"))
      require(leaked.isEmpty, s"runtime-only compilation produced assembly classes: ${leaked.mkString(", ")}")
      Seq("ptx/v9_3/VMCompiler.class", "sass/VMCompiler.class").foreach { name =>
        require((output / s"net/kurobako/gemstone/isa/$name").isFile, s"runtime compilation did not produce $name")
      }
      run(
        Seq(
          java.toString,
          "-Xmx1g",
          "-cp",
          (Seq(output) ++ dependencies ++ resources).mkString(File.pathSeparator),
          mainClass
        ),
        root,
        "NVIDIA runtime-only execution",
        log,
        error
      )
      log("NVIDIA runtime boundary passed: fresh PTX and SASS runtimes execute with asm absent")
    }
  }

  private def runtimeClass(name: String): Boolean = (name.endsWith(".class") || name.endsWith(".tasty")) &&
    packages.exists(prefix =>
      name.startsWith(prefix) || name.contains("/" + prefix)
    )

  private def assertNoRuntimeClasses(path: Path): Unit = {
    require(path.isDirectory || path.isFile, s"runtime dependency is missing: $path")
    val leaked = if (path.isDirectory) {
      packages.iterator.flatMap { prefix =>
        (path / prefix).walk().iterator.map(file => path.relativize(file).toString.replace(File.separatorChar, '/'))
      }.find(runtimeClass)
    } else Using.resource(new ZipFile(path.toFile)) { archive =>
      archive.entries().asScala.map(_.getName).find(runtimeClass)
    }
    require(
      leaked.isEmpty,
      s"runtime boundary classpath contains existing PTX/SASS classes: $path (${leaked.getOrElse("")})"
    )
  }

  private def quoted(argument: String): String = {
    require(
      !argument.exists(character => character == '\n' || character == '\r'),
      "compiler argument contains a newline"
    )
    "\"" + argument.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
  }

  private def run(
      command: Seq[String],
      root: Path,
      description: String,
      log: String => Unit,
      error: String => Unit
  ): Unit = {
    val status = Process(command, root.toFile).!(ProcessLogger(log, error))
    require(status == 0, s"$description failed with exit code $status")
  }
}
