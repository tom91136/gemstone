import sbt.File
import sbt.io.IO
import sbt.util.Logger

import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import java.time.Duration
import java.util.HexFormat
import java.util.zip.ZipInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import scala.util.control.NonFatal

object IsaReferences {
  final case class Pins(
      llvmVersion: String,
      llvmSha256: String,
      mesaVersion: String,
      mesaSha256: String,
      cudaPtxVersion: String,
      cudaPtxSha256: String,
      amdXmlVersion: String,
      amdXmlSha256: String
  )

  private final case class Artifact(id: String, fileName: String, url: URI, sha256: String)

  private val MaximumEntryBytes = 64L * 1024L * 1024L
  private val MaximumTotalBytes = 2L * 1024L * 1024L * 1024L

  def prepare(
      cacheDirectory: File,
      outputRoot: File,
      pins: Pins,
      offline: Boolean,
      log: Logger
  ): File = {
    val artifacts = Vector(
      Artifact(
        "llvm",
        s"llvm-project-${pins.llvmVersion}.src.tar.xz",
        URI.create(
          s"https://github.com/llvm/llvm-project/releases/download/llvmorg-${pins.llvmVersion}/llvm-project-${pins.llvmVersion}.src.tar.xz"
        ),
        pins.llvmSha256
      ),
      Artifact(
        "mesa",
        s"mesa-${pins.mesaVersion}.tar.xz",
        URI.create(s"https://archive.mesa3d.org/mesa-${pins.mesaVersion}.tar.xz"),
        pins.mesaSha256
      ),
      Artifact(
        "nvidia-ptx",
        s"nvidia-ptx-${pins.cudaPtxVersion}.html",
        URI.create(
          s"https://docs.nvidia.com/cuda/archive/${pins.cudaPtxVersion}/parallel-thread-execution/index.html"
        ),
        pins.cudaPtxSha256
      ),
      Artifact(
        "amd-xml",
        s"AMD_GPU_MR_ISA_XML_${pins.amdXmlVersion}.zip",
        URI.create(s"https://gpuopen.com/download/AMD_GPU_MR_ISA_XML_${pins.amdXmlVersion}.zip"),
        pins.amdXmlSha256
      )
    )
    artifacts.foreach(artifact => requireSha256(artifact.id, artifact.sha256))

    val manifest    = renderManifest(artifacts, pins)
    val fingerprint = sha256(manifest.getBytes(StandardCharsets.UTF_8)).take(16)
    val output      = outputRoot.toPath.toAbsolutePath.normalize.resolve(fingerprint)
    if (isPrepared(output, manifest)) {
      log.info(s"ISA references are prepared at $output")
      return output.toFile
    }

    val cache  = cacheDirectory.toPath.toAbsolutePath.normalize
    val cached = artifacts.map(artifact => artifact.id -> cachedArtifact(cache, artifact, offline, log)).toMap
    val parent = output.getParent
    val _      = Files.createDirectories(parent)
    val stage  = Files.createTempDirectory(parent, s"$fingerprint.")
    try {
      extractTarXz(
        cached("llvm"),
        stage.resolve("llvm"),
        s"llvm-project-${pins.llvmVersion}.src",
        relative => relative == Path.of("llvm/LICENSE.TXT") || relative.startsWith("llvm/test/MC/AMDGPU")
      )
      extractTarXz(
        cached("mesa"),
        stage.resolve("mesa"),
        s"mesa-${pins.mesaVersion}",
        relative =>
          relative == Path.of("docs/license.rst") ||
            relative == Path.of("licenses/MIT") ||
            (Option(relative.getParent).contains(Path.of("src/amd/compiler")) &&
              relative.getFileName.toString.startsWith("aco_"))
      )
      extractZip(
        cached("amd-xml"),
        stage.resolve("amd/xml"),
        relative => relative.getNameCount == 1 && relative.getFileName.toString.matches("amdgpu_isa_[a-z0-9_]+\\.xml")
      )
      copyFile(cached("nvidia-ptx"), stage.resolve("nvidia/ptx/index.html"))
      val _ = Files.writeString(stage.resolve("MANIFEST.tsv"), manifest, StandardCharsets.UTF_8)
      verifyPrepared(stage, manifest)
      try Files.move(stage, output, StandardCopyOption.ATOMIC_MOVE)
      catch {
        case _: AtomicMoveNotSupportedException          => Files.move(stage, output)
        case _: java.nio.file.FileAlreadyExistsException => verifyPrepared(output, manifest)
      }
      require(Files.isDirectory(output), s"failed to commit prepared ISA references to $output")
      log.info(s"prepared ISA references at $output")
      output.toFile
    } finally
      if (Files.exists(stage)) IO.delete(stage.toFile)
  }

  private def cachedArtifact(cache: Path, artifact: Artifact, offline: Boolean, log: Logger): Path = {
    require(artifact.url.getScheme == "https", s"${artifact.id}: only HTTPS downloads are permitted")
    val _        = Files.createDirectories(cache)
    val target   = cache.resolve(artifact.fileName)
    val lockPath = cache.resolve(s"${artifact.fileName}.lock")
    val channel  = FileChannel.open(lockPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    val lock     = channel.lock()
    try
      if (Files.isRegularFile(target)) {
        val actual = sha256(target)
        require(
          actual == artifact.sha256,
          s"${artifact.id}: cached artifact hash mismatch at $target; expected ${artifact.sha256}, found $actual"
        )
        log.info(s"using cached ${artifact.id} artifact $target")
        target
      } else {
        require(!offline, s"${artifact.id}: artifact is absent from offline cache $target")
        log.info(s"downloading ${artifact.id} ${artifact.url}")
        val temporary = Files.createTempFile(cache, s"${artifact.fileName}.", ".part")
        try {
          val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build()
          val request = HttpRequest.newBuilder(artifact.url)
            .timeout(Duration.ofMinutes(10))
            .header("User-Agent", "gemstone-isa-reference-fetcher/1")
            .GET()
            .build()
          val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
          require(response.statusCode() == 200, s"${artifact.id}: HTTP ${response.statusCode()} from ${artifact.url}")
          val input = response.body()
          try {
            val _ = Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING)
          } finally input.close()
          val actual = sha256(temporary)
          require(actual == artifact.sha256, s"${artifact.id}: expected ${artifact.sha256}, downloaded $actual")
          moveFile(temporary, target)
          target
        } finally {
          val _ = Files.deleteIfExists(temporary)
        }
      }
    finally {
      lock.release()
      channel.close()
    }
  }

  private def extractTarXz(
      archive: Path,
      destination: Path,
      archiveRoot: String,
      select: Path => Boolean
  ): Unit = {
    val _         = Files.createDirectories(destination)
    val fileInput = new BufferedInputStream(Files.newInputStream(archive))
    val xzInput   = XZCompressorInputStream.builder()
      .setInputStream(fileInput)
      .setDecompressConcatenated(true)
      .get()
    val tarInput = new TarArchiveInputStream(xzInput)
    var total    = 0L
    try {
      var entry = tarInput.getNextEntry
      while (entry != null) {
        val relative = archiveRelative(entry.getName, archiveRoot)
        if (relative.exists(select) && !entry.isDirectory) {
          require(entry.isFile, s"selected archive entry is not a regular file: ${entry.getName}")
          require(!entry.isSymbolicLink && !entry.isLink, s"archive links are not permitted: ${entry.getName}")
          val target = safeTarget(destination, relative.get)
          total += copyEntry(tarInput, target, entry.getSize)
          require(total <= MaximumTotalBytes, s"archive expands beyond $MaximumTotalBytes bytes: $archive")
        }
        entry = tarInput.getNextEntry
      }
    } finally tarInput.close()
  }

  private def extractZip(archive: Path, destination: Path, select: Path => Boolean): Unit = {
    val _        = Files.createDirectories(destination)
    val zipInput = new ZipInputStream(new BufferedInputStream(Files.newInputStream(archive)))
    var total    = 0L
    try {
      var entry = zipInput.getNextEntry
      while (entry != null) {
        val relative = safeRelative(entry.getName)
        if (!entry.isDirectory && select(relative)) {
          val target = safeTarget(destination, relative)
          total += copyEntry(zipInput, target, entry.getSize)
          require(total <= MaximumTotalBytes, s"archive expands beyond $MaximumTotalBytes bytes: $archive")
        }
        zipInput.closeEntry()
        entry = zipInput.getNextEntry
      }
    } finally zipInput.close()
  }

  private def archiveRelative(name: String, archiveRoot: String): Option[Path] = {
    val path = safeRelative(name)
    require(path.getNameCount >= 1 && path.getName(0).toString == archiveRoot, s"unexpected archive root: $name")
    if (path.getNameCount == 1) None else Some(path.subpath(1, path.getNameCount))
  }

  private def safeRelative(name: String): Path = {
    require(name.nonEmpty && !name.contains('\\') && !name.contains('\u0000'), s"unsafe archive path: $name")
    val raw = Path.of(name)
    require(!raw.isAbsolute, s"absolute archive path is not permitted: $name")
    val segments = raw.iterator()
    while (segments.hasNext) {
      val segment = segments.next().toString
      require(segment != "." && segment != "..", s"archive traversal is not permitted: $name")
    }
    raw.normalize
  }

  private def safeTarget(destination: Path, relative: Path): Path = {
    val root   = destination.toAbsolutePath.normalize
    val target = root.resolve(relative).normalize
    require(target.startsWith(root), s"archive target escapes destination: $relative")
    target
  }

  private def copyEntry(input: InputStream, target: Path, declaredSize: Long): Long = {
    require(declaredSize <= MaximumEntryBytes, s"archive entry is too large: $target ($declaredSize bytes)")
    val _      = Files.createDirectories(target.getParent)
    val output = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW)
    val buffer = new Array[Byte](128 * 1024)
    var copied = 0L
    try {
      var read = input.read(buffer)
      while (read >= 0) {
        if (read > 0) {
          copied += read
          require(copied <= MaximumEntryBytes, s"archive entry expands beyond $MaximumEntryBytes bytes: $target")
          output.write(buffer, 0, read)
        }
        read = input.read(buffer)
      }
    } finally output.close()
    if (declaredSize >= 0) require(copied == declaredSize, s"truncated archive entry $target: $copied != $declaredSize")
    copied
  }

  private def copyFile(source: Path, target: Path): Unit = {
    val _ = Files.createDirectories(target.getParent)
    val _ = Files.copy(source, target)
  }

  private def moveFile(source: Path, target: Path): Unit =
    try Files.move(source, target, StandardCopyOption.ATOMIC_MOVE)
    catch { case _: AtomicMoveNotSupportedException => Files.move(source, target) }

  private def renderManifest(artifacts: Vector[Artifact], pins: Pins): String = {
    val header = Vector(
      "format\t1",
      s"llvm-version\t${pins.llvmVersion}",
      s"mesa-version\t${pins.mesaVersion}",
      s"cuda-ptx-version\t${pins.cudaPtxVersion}",
      s"amd-xml-version\t${pins.amdXmlVersion}"
    )
    val sources = artifacts.map(artifact => s"artifact\t${artifact.id}\t${artifact.sha256}\t${artifact.url}")
    (header ++ sources).mkString("", "\n", "\n")
  }

  private def isPrepared(output: Path, manifest: String): Boolean =
    Files.isDirectory(output) && {
      try {
        verifyPrepared(output, manifest)
        true
      } catch { case NonFatal(_) => false }
    }

  private def verifyPrepared(output: Path, manifest: String): Unit = {
    require(Files.readString(output.resolve("MANIFEST.tsv"), StandardCharsets.UTF_8) == manifest)
    require(Files.isDirectory(output.resolve("llvm/llvm/test/MC/AMDGPU")), "LLVM AMDGPU MC tests are absent")
    require(Files.isRegularFile(output.resolve("llvm/llvm/LICENSE.TXT")), "LLVM licence is absent")
    require(Files.isRegularFile(output.resolve("mesa/src/amd/compiler/aco_opcodes.py")), "ACO opcodes are absent")
    require(Files.isRegularFile(output.resolve("mesa/src/amd/compiler/aco_assembler.cpp")), "ACO rules are absent")
    require(Files.isRegularFile(output.resolve("mesa/licenses/MIT")), "Mesa MIT licence is absent")
    require(Files.isRegularFile(output.resolve("amd/xml/amdgpu_isa_rdna2.xml")), "AMD RDNA2 XML is absent")
    require(Files.isRegularFile(output.resolve("nvidia/ptx/index.html")), "NVIDIA PTX specification is absent")
  }

  private def requireSha256(id: String, value: String): Unit =
    require(value.matches("[0-9a-f]{64}"), s"$id: invalid SHA-256 pin: $value")

  private def sha256(path: Path): String = {
    val digest = MessageDigest.getInstance("SHA-256")
    val input  = new BufferedInputStream(Files.newInputStream(path))
    val buffer = new Array[Byte](128 * 1024)
    try {
      var read = input.read(buffer)
      while (read >= 0) {
        if (read > 0) digest.update(buffer, 0, read)
        read = input.read(buffer)
      }
    } finally input.close()
    HexFormat.of().formatHex(digest.digest())
  }

  private def sha256(bytes: Array[Byte]): String =
    HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes))
}
