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
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import scala.util.control.NonFatal
import scala.jdk.CollectionConverters.*

object ISAReferences {
  final case class Pins(
      llvmVersion: String,
      llvmSha256: String,
      mesaVersion: String,
      mesaSha256: String,
      cudaPTXVersion: String,
      cudaPTXSha256: String,
      cudaAPIVersion: String,
      cudaAPIPackage: String,
      cudaCRTPackage: String,
      cudaDriverDevSha256: String,
      cudaRuntimeDevSha256: String,
      cudaCRTDevSha256: String,
      hipVersion: String,
      hipSha256: String,
      hsaVersion: String,
      hsaSha256: String,
      amdXMLVersion: String,
      amdXMLSha256: String,
      openCLHeadersVersion: String,
      openCLHeadersSha256: String,
      levelZeroVersion: String,
      levelZeroSha256: String,
      spirvHeadersVersion: String,
      spirvHeadersSha256: String,
      spirvToolsVersion: String,
      spirvToolsSha256: String
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
        s"nvidia-ptx-${pins.cudaPTXVersion}.html",
        URI.create(
          s"https://docs.nvidia.com/cuda/archive/${pins.cudaPTXVersion}/parallel-thread-execution/index.html"
        ),
        pins.cudaPTXSha256
      ),
      Artifact(
        "nvidia-cuda-driver-dev",
        s"cuda-driver-dev-${pins.cudaAPIVersion.replace('.', '-')}_${pins.cudaAPIPackage}_amd64.deb",
        URI.create(
          s"https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2604/x86_64/cuda-driver-dev-${pins.cudaAPIVersion.replace('.', '-')}_${pins.cudaAPIPackage}_amd64.deb"
        ),
        pins.cudaDriverDevSha256
      ),
      Artifact(
        "nvidia-cuda-runtime-dev",
        s"cuda-cudart-dev-${pins.cudaAPIVersion.replace('.', '-')}_${pins.cudaAPIPackage}_amd64.deb",
        URI.create(
          s"https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2604/x86_64/cuda-cudart-dev-${pins.cudaAPIVersion.replace('.', '-')}_${pins.cudaAPIPackage}_amd64.deb"
        ),
        pins.cudaRuntimeDevSha256
      ),
      Artifact(
        "nvidia-cuda-crt",
        s"cuda-crt-${pins.cudaAPIVersion.replace('.', '-')}_${pins.cudaCRTPackage}_amd64.deb",
        URI.create(
          s"https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2604/x86_64/cuda-crt-${pins.cudaAPIVersion.replace('.', '-')}_${pins.cudaCRTPackage}_amd64.deb"
        ),
        pins.cudaCRTDevSha256
      ),
      Artifact(
        "amd-hip",
        s"HIP-rocm-${pins.hipVersion}.tar.gz",
        URI.create(s"https://github.com/ROCm/HIP/archive/refs/tags/rocm-${pins.hipVersion}.tar.gz"),
        pins.hipSha256
      ),
      Artifact(
        "amd-hsa",
        s"ROCR-Runtime-rocm-${pins.hsaVersion}.tar.gz",
        URI.create(s"https://github.com/ROCm/ROCR-Runtime/archive/refs/tags/rocm-${pins.hsaVersion}.tar.gz"),
        pins.hsaSha256
      ),
      Artifact(
        "amd-xml",
        s"AMD_GPU_MR_ISA_XML_${pins.amdXMLVersion}.zip",
        URI.create(s"https://gpuopen.com/download/AMD_GPU_MR_ISA_XML_${pins.amdXMLVersion}.zip"),
        pins.amdXMLSha256
      ),
      Artifact(
        "opencl-headers",
        s"OpenCL-Headers-${pins.openCLHeadersVersion}.zip",
        URI.create(
          s"https://github.com/KhronosGroup/OpenCL-Headers/archive/refs/tags/v${pins.openCLHeadersVersion}.zip"
        ),
        pins.openCLHeadersSha256
      ),
      Artifact(
        "level-zero",
        s"level-zero-${pins.levelZeroVersion}.zip",
        URI.create(s"https://github.com/oneapi-src/level-zero/archive/refs/tags/v${pins.levelZeroVersion}.zip"),
        pins.levelZeroSha256
      ),
      Artifact(
        "spirv-headers",
        s"SPIRV-Headers-${pins.spirvHeadersVersion}.zip",
        URI.create(
          s"https://github.com/KhronosGroup/SPIRV-Headers/archive/refs/tags/${pins.spirvHeadersVersion}.zip"
        ),
        pins.spirvHeadersSha256
      ),
      Artifact(
        "spirv-tools",
        s"SPIRV-Tools-${pins.spirvToolsVersion}.zip",
        URI.create(s"https://github.com/KhronosGroup/SPIRV-Tools/archive/refs/tags/${pins.spirvToolsVersion}.zip"),
        pins.spirvToolsSha256
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
      extractDebXz(
        cached("nvidia-cuda-driver-dev"),
        stage.resolve("nvidia/cuda"),
        relative => relative.toString.endsWith("/include/cuda.h") || relative.toString.contains("/share/doc/")
      )
      extractDebXz(
        cached("nvidia-cuda-runtime-dev"),
        stage.resolve("nvidia/cuda"),
        relative => relative.toString.contains("/include/") || relative.toString.contains("/share/doc/")
      )
      extractDebXz(
        cached("nvidia-cuda-crt"),
        stage.resolve("nvidia/cuda"),
        relative => relative.toString.contains("/include/crt/") || relative.toString.contains("/share/doc/")
      )
      extractTarGz(
        cached("amd-hip"),
        stage.resolve("amd/hip"),
        s"hip-rocm-${pins.hipVersion}",
        relative => relative == Path.of("LICENSE.md") || relative.startsWith("include/hip")
      )
      extractTarGz(
        cached("amd-hsa"),
        stage.resolve("amd/hsa"),
        s"ROCR-Runtime-rocm-${pins.hsaVersion}",
        relative =>
          relative == Path.of("LICENSE.txt") || relative == Path.of("runtime/hsa-runtime/LICENSE.md") ||
            (Option(relative.getParent).contains(Path.of("runtime/hsa-runtime/inc")) &&
              relative.getFileName.toString.startsWith("hsa") && relative.getFileName.toString.endsWith(".h"))
      )
      extractZipRoot(
        cached("opencl-headers"),
        stage.resolve("khronos/opencl"),
        s"OpenCL-Headers-${pins.openCLHeadersVersion}",
        relative => relative == Path.of("LICENSE") || relative.startsWith("CL")
      )
      extractZipRoot(
        cached("level-zero"),
        stage.resolve("oneapi/level-zero"),
        s"level-zero-${pins.levelZeroVersion}",
        relative => relative == Path.of("LICENSE") || relative.startsWith("include") || relative.startsWith("doc")
      )
      extractZipRoot(
        cached("spirv-headers"),
        stage.resolve("khronos/spirv-headers"),
        s"SPIRV-Headers-${pins.spirvHeadersVersion}",
        relative =>
          relative == Path.of("LICENSE") || relative.startsWith("LICENSES") ||
            relative.startsWith("include/spirv/unified1")
      )
      extractZipRoot(
        cached("spirv-tools"),
        stage.resolve("khronos/spirv-tools"),
        s"SPIRV-Tools-${pins.spirvToolsVersion}",
        relative =>
          relative == Path.of("LICENSE") || relative == Path.of("test/binary_parse_test.cpp") ||
            relative == Path.of("test/binary_endianness_test.cpp") ||
            relative == Path.of("test/val/val_opencl_test.cpp")
      )
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

  private def extractTarGz(
      archive: Path,
      destination: Path,
      archiveRoot: String,
      select: Path => Boolean
  ): Unit = {
    val _         = Files.createDirectories(destination)
    val fileInput = new BufferedInputStream(Files.newInputStream(archive))
    val gzipInput = new GzipCompressorInputStream(fileInput)
    val tarInput  = new TarArchiveInputStream(gzipInput)
    var total     = 0L
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

  private def extractDebXz(archive: Path, destination: Path, select: Path => Boolean): Unit = {
    val _       = Files.createDirectories(destination)
    val arInput = new ArArchiveInputStream(new BufferedInputStream(Files.newInputStream(archive)))
    var found   = false
    try {
      var entry = arInput.getNextEntry
      while (entry != null && !found) {
        if (entry.getName == "data.tar.xz") {
          val xzInput = XZCompressorInputStream.builder()
            .setInputStream(arInput)
            .setDecompressConcatenated(true)
            .get()
          val tarInput = new TarArchiveInputStream(xzInput)
          var total    = 0L
          try {
            var tarEntry = tarInput.getNextEntry
            while (tarEntry != null) {
              val relative = Option.when(!tarEntry.isDirectory)(safeRelative(tarEntry.getName.stripPrefix("./")))
              if (relative.exists(select)) {
                require(tarEntry.isFile, s"selected archive entry is not a regular file: ${tarEntry.getName}")
                require(
                  !tarEntry.isSymbolicLink && !tarEntry.isLink,
                  s"archive links are not permitted: ${tarEntry.getName}"
                )
                val target = safeTarget(destination, relative.get)
                total += copyEntry(tarInput, target, tarEntry.getSize)
                require(total <= MaximumTotalBytes, s"archive expands beyond $MaximumTotalBytes bytes: $archive")
              }
              tarEntry = tarInput.getNextEntry
            }
          } finally tarInput.close()
          found = true
        } else entry = arInput.getNextEntry
      }
    } finally arInput.close()
    require(found, s"Debian archive has no data.tar.xz payload: $archive")
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

  private def extractZipRoot(
      archive: Path,
      destination: Path,
      archiveRoot: String,
      select: Path => Boolean
  ): Unit = {
    val _        = Files.createDirectories(destination)
    val zipInput = new ZipInputStream(new BufferedInputStream(Files.newInputStream(archive)))
    var total    = 0L
    try {
      var entry = zipInput.getNextEntry
      while (entry != null) {
        val relative = archiveRelative(entry.getName, archiveRoot)
        if (!entry.isDirectory && relative.exists(select)) {
          val target = safeTarget(destination, relative.get)
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
      "format\t2",
      s"llvm-version\t${pins.llvmVersion}",
      s"mesa-version\t${pins.mesaVersion}",
      s"cuda-ptx-version\t${pins.cudaPTXVersion}",
      s"cuda-api-version\t${pins.cudaAPIVersion}",
      s"cuda-api-package\t${pins.cudaAPIPackage}",
      s"cuda-crt-package\t${pins.cudaCRTPackage}",
      s"hip-version\t${pins.hipVersion}",
      s"hsa-version\t${pins.hsaVersion}",
      s"amd-xml-version\t${pins.amdXMLVersion}",
      s"opencl-headers-version\t${pins.openCLHeadersVersion}",
      s"level-zero-version\t${pins.levelZeroVersion}",
      s"spirv-headers-version\t${pins.spirvHeadersVersion}",
      s"spirv-tools-version\t${pins.spirvToolsVersion}"
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
    require(hasFile(output.resolve("nvidia/cuda"), "cuda.h"), "NVIDIA CUDA Driver header is absent")
    require(hasFile(output.resolve("nvidia/cuda"), "cuda_runtime_api.h"), "NVIDIA CUDA Runtime header is absent")
    require(Files.isRegularFile(output.resolve("amd/hip/include/hip/hip_runtime_api.h")), "HIP header is absent")
    require(Files.isRegularFile(output.resolve("amd/hip/LICENSE.md")), "HIP licence is absent")
    require(
      Files.isRegularFile(output.resolve("amd/hsa/runtime/hsa-runtime/inc/hsa.h")),
      "HSA core header is absent"
    )
    require(
      Files.isRegularFile(output.resolve("amd/hsa/runtime/hsa-runtime/inc/hsa_ext_amd.h")),
      "HSA AMD extension header is absent"
    )
    require(Files.isRegularFile(output.resolve("amd/hsa/LICENSE.txt")), "HSA licence is absent")
    require(Files.isRegularFile(output.resolve("khronos/opencl/CL/cl.h")), "OpenCL headers are absent")
    require(Files.isRegularFile(output.resolve("khronos/opencl/LICENSE")), "OpenCL headers licence is absent")
    require(Files.isRegularFile(output.resolve("oneapi/level-zero/include/ze_api.h")), "Level Zero header is absent")
    require(Files.isRegularFile(output.resolve("oneapi/level-zero/LICENSE")), "Level Zero licence is absent")
    require(
      Files.isRegularFile(output.resolve("khronos/spirv-headers/include/spirv/unified1/spirv.core.grammar.json")),
      "SPIR-V grammar is absent"
    )
    require(Files.isRegularFile(output.resolve("khronos/spirv-headers/LICENSE")), "SPIR-V headers licence is absent")
    require(
      Files.isRegularFile(output.resolve("khronos/spirv-tools/test/binary_parse_test.cpp")),
      "SPIRV-Tools binary tests are absent"
    )
    require(Files.isRegularFile(output.resolve("khronos/spirv-tools/LICENSE")), "SPIRV-Tools licence is absent")
  }

  private def requireSha256(id: String, value: String): Unit =
    require(value.matches("[0-9a-f]{64}"), s"$id: invalid SHA-256 pin: $value")

  private def hasFile(root: Path, name: String): Boolean = {
    if (!Files.isDirectory(root)) false
    else {
      val stream = Files.walk(root)
      try stream.iterator().asScala.exists(path => Files.isRegularFile(path) && path.getFileName.toString == name)
      finally stream.close()
    }
  }

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
