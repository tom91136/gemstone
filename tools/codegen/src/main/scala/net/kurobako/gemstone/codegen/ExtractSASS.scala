package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.io.ByteArrayOutputStream
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
  case class Tool(name: String, binary: Path, digest: String, packageInfo: ujson.Obj)

  private val ResourceRoot            = "/net/kurobako/gemstone/codegen/sass/"
  private val MaximumDescriptionBytes = 64 * 1024 * 1024

  private def resource(name: String): java.io.InputStream =
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
    Opts.option[String]("output", "Description bundle directory").map(Path.of(_)),
    Opts.flag("verify", "Verify an existing description bundle without executing tools").orFalse
  ).tupled)

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) {
    case (bundle, legacy, legacyDigest, modern, modernDigest, output, verifyOnly) =>
      val tools = Vec(("legacy", legacy, legacyDigest), ("modern", modern, modernDigest)).map {
        (name, binary, digest) =>
          val entries = Files.readString(bundle).linesIterator.toVec.map(_.split('\t').toVec).collect {
            case Vec("artifact", id, filename, packageDigest, url) if id == s"nvidia-nvdisasm-$name" =>
              ujson.Obj("filename" -> filename, "url" -> url, "sha256" -> packageDigest, "nvdisasm_sha256" -> digest)
          }
          require(entries.size == 1, s"expected one $name nvdisasm package in $bundle")
          Tool(name, binary.toAbsolutePath.normalize, digest, entries.head)
      }
      if (verifyOnly) verify(output, tools)
      else extract(output, tools)
  }

  private def sha256(bytes: Array[Byte]): String =
    MessageDigest.getInstance("SHA-256").digest(bytes).map(byte => f"${byte & 0xff}%02x").mkString

  private def packages(tools: Vec[Tool]): ujson.Obj = ujson.Obj.from(tools.map(tool => tool.name -> tool.packageInfo))

  def verify(
      output: Path,
      tools: Vec[Tool],
      pins: Map[String, Map[String, String]] = expected
  ): Unit = {
    require(
      tools.map(_.name).distinct.size == tools.size && tools.map(_.name).toSet == pins.keySet,
      "tool set does not match description pins"
    )
    val root     = output.toAbsolutePath.normalize
    val manifest = ujson.read(Files.readString(root.resolve("manifest.json")))
    require(manifest("format").num == 1, "unsupported manifest format")
    require(
      manifest("platform").str == "ubuntu2204/x86_64" && manifest("packages") == packages(tools),
      "package provenance does not match pinned package metadata"
    )
    val targets = manifest("targets").arr.toVec
    val names   = targets.map(_("name").str)
    require(
      names.distinct.size == names.size && names.toSet == pins.values.flatMap(_.keys).toSet,
      "manifest target set does not match the pinned matrix"
    )
    val rows = manifest("tables").arr.toVec
    require(rows.map(_("sha256").str).distinct.size == rows.size, "manifest contains duplicate table entries")
    val tables = rows.map(row => row("sha256").str -> row).toMap
    tools.foreach(tool =>
      require(manifest("tools")(tool.name)("sha256").str == tool.digest, s"${tool.name} tool pin mismatch in manifest")
    )
    targets.foreach { target =>
      val mode   = target("tool").str
      val name   = target("name").str
      val digest = target("table").str
      require(
        pins.get(mode).flatMap(_.get(name)).contains(digest) && tables.contains(digest),
        s"unexpected target/table mapping for $name"
      )
    }
    val digests = pins.values.flatMap(_.values).toSet
    require(
      tables.keySet == digests && manifest("unique_descriptions").num == digests.size,
      "unexpected description table set"
    )
    rows.foreach { row =>
      val relative = Path.of(row("path").str)
      require(
        !relative.isAbsolute && !(0 until relative.getNameCount).exists(i => relative.getName(i).toString == ".."),
        "unsafe table path"
      )
      val path = root.resolve(relative).normalize
      require(
        path.startsWith(root) && path.toRealPath().startsWith(root.toRealPath()),
        "table path escapes reference bundle"
      )
      val bytes = Using.resource(GZIPInputStream(Files.newInputStream(path)))(_.readNBytes(MaximumDescriptionBytes + 1))
      require(bytes.length <= MaximumDescriptionBytes, "description exceeds size limit")
      require(sha256(bytes) == row("sha256").str, "table failed content verification")
      val metadata = SASSCapture.inspect(bytes)
      require(
        metadata("class_count") == row("classes") &&
          metadata("unique_class_count") == row("unique_classes") &&
          metadata("alternate_class_count") == row("alternate_classes") &&
          metadata("conditional_directives") == row("conditional_directives"),
        "table failed metadata verification"
      )
    }
  }

  private case class Output(status: Int, stdout: Array[Byte], stderr: Array[Byte])

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
      Output(process.exitValue(), Files.readAllBytes(stdout), Files.readAllBytes(stderr))
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
        val zero         = scratch.resolve("zero-input.bin")
        val _            = Files.write(zero, Array.ofDim[Byte](4096))
        val targets      = Vec.newBuilder[ujson.Value]
        val descriptions = scala.collection.mutable.Map.empty[String, ujson.Value]
        val versions     = ujson.Obj()
        tools.foreach { tool =>
          val source = scratch.resolve(s"capture_${tool.name}.c")
          val _      = Using.resource(resource(source.getFileName.toString))(input =>
            Files.copy(input, source, StandardCopyOption.REPLACE_EXISTING)
          )
          val hook     = scratch.resolve(s"capture-${tool.name}.so")
          val compiled =
            run(Vec("gcc", "-shared", "-fPIC", "-O2", "-o", hook.toString, source.toString, "-ldl"), scratch)
          require(compiled.status == 0, s"capture hook compilation failed: ${String(compiled.stderr, UTF_8)}")
          val version = run(Vec(tool.binary.toString, "--version"), scratch)
          require(version.status == 0, s"${tool.name} nvdisasm --version failed")
          val lines = String(version.stdout ++ version.stderr, UTF_8).linesIterator.toVec
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
                require(result.status == 0, s"$target capture failed: ${String(result.stderr, UTF_8)}")
                SASSCapture.legacy(result.stdout)
              } else SASSCapture.modern(result.stderr)
              // The modern free hook can provoke a diagnostic after capturing a complete, digest-verified stream.
              require(sha256(capture.bytes) == digest, s"$target unexpected table SHA256")
              val metadata = SASSCapture.inspect(capture.bytes)
              val detail   = ujson.Obj.from(Vec("run" -> ujson.Num(number), "sha256" -> ujson.Str(digest)) ++
                metadata.obj.toVec ++ capture.detail.obj.toVec)
              (capture.bytes, metadata, detail)
            }
            require(java.util.Arrays.equals(captures(0)._1, captures(1)._1), s"$target repeated captures differ")
            val (bytes, metadata, _) = captures.head
            val relative             = s"tables/$digest.txt.gz"
            val _                    = GeneratedFiles.writeBytes(root.resolve(relative), gzip(bytes))
            descriptions(digest) = ujson.Obj(
              "sha256"                 -> digest,
              "path"                   -> relative,
              "classes"                -> metadata("class_count"),
              "unique_classes"         -> metadata("unique_class_count"),
              "alternate_classes"      -> metadata("alternate_class_count"),
              "conditional_directives" -> metadata("conditional_directives")
            )
            targets += ujson.Obj(
              "name"                       -> target,
              "tool"                       -> tool.name,
              "table"                      -> digest,
              "captures"                   -> ujson.Arr.from(captures.map(_._3)),
              "repeated_capture_identical" -> true
            )
            println(s"$target: $digest (${bytes.length} bytes)")
          }
        }
        val manifest = ujson.Obj(
          "format"              -> 1,
          "description"         -> "Pinned nvdisasm machine description reference tables",
          "platform"            -> "ubuntu2204/x86_64",
          "packages"            -> packages(tools),
          "tools"               -> versions,
          "targets"             -> ujson.Arr.from(targets.result()),
          "tables"              -> ujson.Arr.from(descriptions.toVec.sortBy(_._1).map(_._2)),
          "unique_descriptions" -> descriptions.size
        )
        val _ = GeneratedFiles.write(root.resolve("manifest.json"), ujson.write(manifest, indent = 2) + "\n")
        verify(root, tools)
      } finally
        Using.resource(Files.walk(scratch)) { paths =>
          paths.toList.asScala.toVec.reverse.foreach(path => Files.delete(path))
        }
    }
  }
}
