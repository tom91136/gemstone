import com.sun.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import sbt.internal.util.MessageOnlyException
import scala.jdk.CollectionConverters.*
import scala.sys.process.Process
import scala.sys.process.ProcessLogger

object NativeCodestyle {
  private val ExcludedSegments   = Set("generated", "licences", "third_party", "vendor")
  private val CompiledExtensions = Set("c", "cc", "cpp", "cu", "cxx", "hip")
  private val TidyJobBytes       = 4L << 30
  private val DatabaseFile       = raw""""file"\s*:\s*"((?:[^"\\]|\\.)*)"""".r

  def format(roots: Seq[Path]): Unit =
    clangFormat(sources(roots), Seq("-i"), "clang-format failed")

  def lint(roots: Seq[Path], database: Path, log: String => Unit, shard: Int = 0, shards: Int = 1): Unit = {
    val files = sources(roots)
    clangFormat(
      shardFiles(files, shard, shards),
      Seq("--dry-run", "--Werror"),
      "sources are not clang-formatted; run format"
    )
    tidy(files, database.toAbsolutePath.normalize, log, shard, shards)
  }

  private def tidy(files: Seq[Path], database: Path, log: String => Unit, shard: Int, shards: Int): Unit = {
    val units = DatabaseFile.findAllMatchIn(Files.readString(database.resolve("compile_commands.json")))
      .map(entry => Path.of(entry.group(1).replace("\\\\", "\\").replace("\\\"", "\"")).normalize).toSet
    val (checked, rest) = files.partition(units)
    val selected        = shardFiles(checked, shard, shards)
    val skipped         = shardFiles(rest, shard, shards).count(path => extension(path).exists(CompiledExtensions))
    val shardName       = if (shards > 1) s" (shard ${shard + 1}/$shards)" else ""
    log(s"clang-tidy: ${selected.size} translation units$shardName; $skipped outside the compile database skipped")
    val pool = Executors.newFixedThreadPool(tidyJobs)
    try {
      val runs = pool.invokeAll(selected.map { file =>
        (() => {
          val output  = Vector.newBuilder[String]
          val command =
            Seq("clang-tidy", "--quiet", "--experimental-custom-checks", s"-p=$database", "--extra-arg=-w", s"$file")
          val status = Process(command).!(ProcessLogger(output += _, output += _))
          (status, output.result())
        }): Callable[(Int, Vector[String])]
      }.asJava).asScala.toVector.map(_.get)
      runs.foreach((status, output) => if (status != 0) output.foreach(log))
      if (runs.exists(_._1 != 0)) throw MessageOnlyException("clang-tidy reported errors; see .clang-tidy")
    } finally pool.shutdown()
  }

  private def shardFiles(files: Seq[Path], shard: Int, shards: Int): Seq[Path] =
    if (shards == 1) files
    else
      files.sortBy(path => -Files.size(path)).zipWithIndex.collect {
        case (file, index) if index % shards == shard => file
      }

  // template-heavy translation units take several GiB each, so free memory bounds parallelism before cores do
  private def tidyJobs: Int = {
    val cores = sys.runtime.availableProcessors()
    ManagementFactory.getOperatingSystemMXBean match {
      case system: OperatingSystemMXBean => (system.getFreeMemorySize / TidyJobBytes).toInt.max(1).min(cores)
      case _                             => cores
    }
  }

  private def clangFormat(files: Seq[Path], options: Seq[String], failure: String): Unit =
    if (files.nonEmpty && Process(Seq("clang-format") ++ options ++ files.map(_.toString)).! != 0)
      throw MessageOnlyException(failure)

  private def extension(path: Path): Option[String] = {
    val name = path.getFileName.toString
    val dot  = name.lastIndexOf('.')
    Option.when(dot > 0 && dot < name.length - 1)(name.substring(dot + 1).toLowerCase(java.util.Locale.ROOT))
  }

  private def sources(roots: Seq[Path]): Seq[Path] =
    roots.map(_.toAbsolutePath.normalize).flatMap { root =>
      Process(Seq("git", "-C", s"$root", "ls-files", "--cached", "--others", "--exclude-standard")).!!
        .linesIterator.filterNot(_.split('/').exists(ExcludedSegments)).map(root.resolve(_))
        .filter(Files.isRegularFile(_)).filter(path => extension(path).exists(CompiledExtensions))
    }.distinct.sortBy(_.toString)
}
