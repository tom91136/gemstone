package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import scala.jdk.CollectionConverters.*
import scala.util.Using
import scala.util.control.NonFatal

/** Recovers digest-pinned machine descriptions using the two version-specific nvdisasm capture hooks. */
object ExtractSASS {
  private given CanEqual[ujson.Value, ujson.Value] = CanEqual.derived

  class Tool(val name: String, val binary: Path, val digest: String, val packageInfo: ujson.Obj)

  private val ResourceRoot            = "/net/kurobako/gemstone/codegen/sass/"
  private val MaximumDescriptionBytes = 64 * 1024 * 1024

  def readTable(path: Path): Array[Byte] = {
    val bytes = Using.resource(GZIPInputStream(Files.newInputStream(path)))(_.readNBytes(MaximumDescriptionBytes + 1))
    require(bytes.length <= MaximumDescriptionBytes, s"table $path exceeds $MaximumDescriptionBytes bytes")
    bytes
  }

  private def resource(name: String): InputStream =
    Option(getClass.getResourceAsStream(ResourceRoot + name))
      .getOrElse(throw IllegalArgumentException(s"missing SASS extraction resource $name"))

  lazy val expected: Map[String, Map[String, String]] =
    Using.resource(resource("expected-sha256.json")) { input =>
      ujson.read(input).obj.map { (mode, targets) =>
        mode -> targets.obj.map((name, digest) => name -> digest.str).toMap
      }.toMap
    }

  private val command = Command("ExtractSASS", "Recover pinned nvdisasm machine descriptions")((
    Opts.option[String]("bundle", "Verified tool bundle MANIFEST.tsv").map(Path.of(_)),
    Opts.option[String]("legacy", "Pinned legacy nvdisasm executable").map(Path.of(_)),
    Opts.option[String]("legacy-sha", "Expected legacy executable SHA-256"),
    Opts.option[String]("modern", "Pinned modern nvdisasm executable").map(Path.of(_)),
    Opts.option[String]("modern-sha", "Expected modern executable SHA-256"),
    Opts.option[String]("output", "Description bundle directory").map(Path.of(_))
  ).tupled)

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) {
    case (bundle, legacy, legacyDigest, modern, modernDigest, output) =>
      val tools = Vec(("legacy", legacy, legacyDigest), ("modern", modern, modernDigest)).map {
        (name, binary, digest) =>
          val entries = Files.readString(bundle).linesIterator.map(_.split('\t').toVec).collect {
            case Vec("artifact", id, filename, packageDigest, url) if id == s"nvidia-nvdisasm-$name" =>
              ujson.Obj("filename" -> filename, "url" -> url, "sha256" -> packageDigest, "nvdisasm_sha256" -> digest)
          }.toVec
          require(entries.size == 1, s"expected one $name nvdisasm package in $bundle")
          Tool(name, binary.toAbsolutePath.normalize, digest, entries.head)
      }
      extract(output, tools)
  }

  private def sha256(bytes: Array[Byte]): String =
    MessageDigest.getInstance("SHA-256").digest(bytes).map(byte => f"${byte & 0xff}%02x").mkString

  private def packages(tools: Vec[Tool]): ujson.Obj = ujson.Obj.from(tools.map(tool => tool.name -> tool.packageInfo))

  /** Checks that the manifest records the given tools, maps every pinned target to its pinned table, and that each
    * table matches its digest.
    */
  def verify(output: Path, tools: Vec[Tool], pins: Map[String, Map[String, String]] = expected): Unit = {
    val root     = output.toAbsolutePath.normalize
    val manifest = ujson.read(Files.readString(root.resolve("manifest.json")))
    require(
      manifest("platform").str == "ubuntu2204/x86_64" && manifest("packages") == packages(tools) &&
        tools.forall(tool => manifest("tools")(tool.name)("sha256").str == tool.digest),
      "manifest provenance does not match the pinned tools"
    )
    val targets =
      manifest("targets").arr.toVec.map(target => (target("tool").str, target("name").str, target("table").str))
    require(
      targets.size == pins.values.map(_.size).sum && targets.map(_._2).distinct.size == targets.size &&
        targets.forall((tool, name, digest) => pins.get(tool).flatMap(_.get(name)).contains(digest)),
      "manifest targets do not match the pinned matrix"
    )
    pins.values.flatMap(_.values).toSet.foreach { digest =>
      val bytes = readTable(root.resolve(s"tables/$digest.txt.gz"))
      require(sha256(bytes) == digest, s"table $digest failed content verification")
    }
  }

  private case class Output(status: Int, stdout: Vec[Byte], stderr: Vec[Byte])

  private def run(command: Vec[String], directory: Path, environment: Map[String, String] = Map.empty): Output = {
    val stdout  = directory.resolve("stdout.bin")
    val stderr  = directory.resolve("stderr.bin")
    val builder = ProcessBuilder(command*)
      .directory(directory.toFile).redirectOutput(stdout.toFile).redirectError(stderr.toFile)
    environment.foreach { (name, value) =>
      val _ = builder.environment().put(name, value)
    }
    val process = builder.start()
    try {
      require(process.waitFor(120, TimeUnit.SECONDS), s"SASS extraction command timed out: ${command.mkString(" ")}")
      require(
        Files.size(stdout) <= 2L * MaximumDescriptionBytes && Files.size(stderr) <= 2L * MaximumDescriptionBytes,
        "SASS capture exceeds size limit"
      )
      Output(
        process.exitValue(),
        Vec.unsafeWrapArray(Files.readAllBytes(stdout)),
        Vec.unsafeWrapArray(Files.readAllBytes(stderr))
      )
    } finally
      if (process.isAlive) {
        val _ = process.destroyForcibly()
        val _ = process.waitFor()
      }
  }

  private def gzip(bytes: Array[Byte]): Array[Byte] = {
    val output = ByteArrayOutputStream()
    Using.resource(GZIPOutputStream(output))(_.write(bytes))
    output.toByteArray
  }

  def extract(output: Path, tools: Vec[Tool]): Unit = {
    require(
      tools.map(_.name).distinct.size == tools.size && tools.map(_.name).toSet == expected.keySet,
      "expected the legacy and modern nvdisasm tools"
    )
    tools.foreach(tool =>
      require(sha256(Files.readAllBytes(tool.binary)) == tool.digest, s"${tool.name} nvdisasm SHA256 mismatch")
    )
    val cached = try { verify(output, tools); true }
    catch { case NonFatal(_) => false }
    if (cached) println(s"Verified cached SASS references in $output")
    else {
      val root   = Files.createDirectories(output.toAbsolutePath.normalize)
      val tables = Files.createDirectories(root.resolve("tables"))
      require(tables.toRealPath().startsWith(root.toRealPath()), "table directory escapes reference bundle")
      val scratch = Files.createTempDirectory(root.getParent, "sass-extract-")
      try {
        val zero     = scratch.resolve("zero-input.bin")
        val _        = Files.write(zero, Array.ofDim[Byte](4096))
        val targets  = Vec.newBuilder[ujson.Value]
        val versions = ujson.Obj()
        tools.foreach { tool =>
          val source = scratch.resolve(s"capture_${tool.name}.c")
          val _      = Using.resource(resource(source.getFileName.toString))(input =>
            Files.copy(input, source, StandardCopyOption.REPLACE_EXISTING)
          )
          val hook     = scratch.resolve(s"capture-${tool.name}.so")
          val compiled =
            run(Vec("gcc", "-shared", "-fPIC", "-O2", "-o", hook.toString, source.toString, "-ldl"), scratch)
          require(
            compiled.status == 0,
            s"capture hook compilation failed: ${String(Vec.toArray(compiled.stderr), UTF_8)}"
          )
          val version = run(Vec(tool.binary.toString, "--version"), scratch)
          require(version.status == 0, s"${tool.name} nvdisasm --version failed")
          val lines = String(Vec.toArray(version.stdout ++ version.stderr), UTF_8).linesIterator.toVec
            .filter(line => line.contains("Cuda compilation tools") || line.startsWith("Build cuda_"))
          versions(tool.name) = ujson.Obj("sha256" -> tool.digest, "version" -> ujson.Arr.from(lines))
          expected(tool.name).toVec.sortBy { (name, _) =>
            val (number, suffix) = name.stripPrefix("SM").span(_.isDigit)
            (number.toInt, suffix)
          }.foreach { (target, digest) =>
            val captures = Vec.from(1 to 2).map { number =>
              val result = run(
                Vec(tool.binary.toString, "--binary", target, "--no-dataflow", zero.toString),
                scratch,
                Map("LD_PRELOAD" -> hook.toString, "TMPDIR" -> scratch.toString)
              )
              val capture = if (tool.name == "legacy") {
                require(result.status == 0, s"$target capture failed: ${String(Vec.toArray(result.stderr), UTF_8)}")
                SASSCapture.legacy(Vec.toArray(result.stdout))
              } else SASSCapture.modern(Vec.toArray(result.stderr))
              // The modern free hook can provoke a diagnostic after capturing a complete, digest-verified stream.
              require(sha256(Vec.toArray(capture.bytes)) == digest, s"$target unexpected table SHA256")
              val detail = ujson.Obj.from(Vec("run" -> ujson.Num(number), "sha256" -> ujson.Str(digest)) ++
                capture.detail.obj.toVec)
              (capture.bytes, detail)
            }
            require(captures(0)._1 == captures(1)._1, s"$target repeated captures differ")
            val bytes = Vec.toArray(captures.head._1)
            GeneratedFiles.writeBytes(root.resolve(s"tables/$digest.txt.gz"), gzip(bytes))
            targets += ujson.Obj(
              "name"                       -> target,
              "tool"                       -> tool.name,
              "table"                      -> digest,
              "captures"                   -> ujson.Arr.from(captures.map(_._2)),
              "repeated_capture_identical" -> true
            )
            println(s"$target: $digest (${bytes.length} bytes)")
          }
        }
        val manifest = ujson.Obj(
          "description" -> "Pinned nvdisasm machine description reference tables",
          "platform"    -> "ubuntu2204/x86_64",
          "packages"    -> packages(tools),
          "tools"       -> versions,
          "targets"     -> ujson.Arr.from(targets.result())
        )
        GeneratedFiles.write(root.resolve("manifest.json"), ujson.write(manifest, indent = 2) + "\n")
        verify(root, tools)
      } finally
        Using.resource(Files.walk(scratch)) { paths =>
          paths.toList.asScala.toVec.reverse.foreach(path => Files.delete(path))
        }
    }
  }
}
