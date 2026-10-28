package net.kurobako.gemstone.codegen

import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.security.DigestInputStream
import java.security.MessageDigest
import java.time.Duration
import java.util.regex.Pattern
import java.util.zip.ZipInputStream
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import scala.jdk.CollectionConverters.*
import scala.sys.process.Process
import scala.util.Using
import scala.util.control.NonFatal

object References {
  case class Artifact(id: String, fileName: String, url: String, sha256: String)

  case class Source(
      artifact: Artifact,
      into: String,
      root: String = "",
      include: Vector[String] = Vector("**"),
      exclude: Vector[String] = Vector.empty,
      executable: Boolean = false
  )

  case class Expect(glob: String, sha256: Option[String] = None)

  case class Write(path: String, content: String)

  case class Bundle(
      name: String,
      sources: Vector[Source],
      writes: Vector[Write] = Vector.empty,
      expect: Vector[Expect] = Vector.empty
  )

  val CudaAPIVersion: String       = "13.3"
  val HipVersion: String           = "7.1.1"
  val HsaVersion: String           = HipVersion
  val LevelZeroVersion: String     = "1.33.1"
  val OpenCLHeadersVersion: String = "2026.05.29"
  val VulkanHeadersVersion: String = "v1.4.341"
  val PTXISAVersion: String        = "9.3"

  private val LlvmVersion         = "23.1.0"
  private val MesaVersion         = "26.2.1"
  private val CudaPTXVersion      = "13.3.0"
  private val CudaAPIPackage      = "13.3.29-1"
  private val CudaCRTPackage      = "13.3.33-1"
  private val HipVersionPatch     = "52802"
  private val AmdXMLVersion       = "2026_08_06"
  private val SpirvHeadersVersion = "vulkan-sdk-1.4.357.0"
  private val CudaRepository      = "https://developer.download.nvidia.com/compute/cuda/repos"
  private val CudaPackage         = CudaAPIVersion.replace('.', '-')
  private val CudaTarget          = s"usr/local/cuda-$CudaAPIVersion/targets/x86_64-linux"

  val Nvdisasm: Vector[(String, String, Artifact, String)] = Vector(
    (
      "legacy",
      "11.8",
      Artifact(
        "nvidia-nvdisasm-legacy",
        "cuda-nvdisasm-11-8_11.8.86-1_amd64.deb",
        s"$CudaRepository/ubuntu2204/x86_64/cuda-nvdisasm-11-8_11.8.86-1_amd64.deb",
        "81989cf87650ae3126693309f831fbe4332d415a5dc63c25ebb149eed562af70"
      ),
      "89a8a64006f0b0667305e42a5f684be10493a33db4bf31df696b9ce85965f835"
    ),
    (
      "modern",
      "13.3",
      Artifact(
        "nvidia-nvdisasm-modern",
        "cuda-nvdisasm-13-3_13.3.73-1_amd64.deb",
        s"$CudaRepository/ubuntu2204/x86_64/cuda-nvdisasm-13-3_13.3.73-1_amd64.deb",
        "6d103457281439a4a241baecc087a0e30eb33eb6a35b3f7b00c15a0b41aaec91"
      ),
      "5842e6adf9e232c9503a804915f158a576473e542577c070da3be49390474140"
    )
  )

  private val Llvm = Artifact(
    "llvm",
    s"llvm-project-$LlvmVersion.src.tar.xz",
    s"https://github.com/llvm/llvm-project/releases/download/llvmorg-$LlvmVersion/llvm-project-$LlvmVersion.src.tar.xz",
    "ab1f0e3ec52448c33e8782eaf0422504b87c7b016b22514653ee0d8fcee479ff"
  )

  private def cuda(id: String, name: String, version: String, sha256: String): Artifact = Artifact(
    id,
    s"$name-${CudaPackage}_${version}_amd64.deb",
    s"$CudaRepository/ubuntu2604/x86_64/$name-${CudaPackage}_${version}_amd64.deb",
    sha256
  )

  val API: Bundle = Bundle(
    "api-native",
    Vector(
      Source(
        cuda(
          "nvidia-cuda-driver-dev",
          "cuda-driver-dev",
          CudaAPIPackage,
          "8bf25d2a822c107afea871c3f521014b55af095d7ac80d56dda27a7599f0fc70"
        ),
        "nvidia/cuda",
        CudaTarget,
        Vector("include/cuda.h")
      ),
      Source(
        cuda(
          "nvidia-cuda-runtime-dev",
          "cuda-cudart-dev",
          CudaAPIPackage,
          "0c137b804e1b68879b873ce996f23f6ca9bc644872be15cb870bdbc43319a717"
        ),
        "nvidia/cuda",
        CudaTarget,
        Vector("include/**")
      ),
      Source(
        cuda(
          "nvidia-cuda-crt",
          "cuda-crt",
          CudaCRTPackage,
          "945852c041126c913c0b1ed9fa8d231550fcaf5a3e05962ecd4669807a890f66"
        ),
        "nvidia/cuda",
        CudaTarget,
        Vector("include/crt/**")
      ),
      Source(
        Artifact(
          "amd-hip",
          s"HIP-rocm-$HipVersion.tar.gz",
          s"https://github.com/ROCm/HIP/archive/refs/tags/rocm-$HipVersion.tar.gz",
          "c64b3219237903d6b27944f236930a1024ed17eb5399165875fbf410fcacf6f4"
        ),
        "amd/hip",
        s"hip-rocm-$HipVersion",
        Vector("LICENSE.md", "include/hip/**")
      ),
      Source(
        Artifact(
          "amd-clr",
          s"clr-rocm-$HipVersion.tar.gz",
          s"https://github.com/ROCm/clr/archive/refs/tags/rocm-$HipVersion.tar.gz",
          "b09539ef53a775c03352f9843f3a346e4f2ad3941c1954e953d352e4984ee708"
        ),
        "amd/hip",
        s"clr-rocm-$HipVersion/hipamd",
        Vector("include/hip/amd_detail/**")
      ),
      Source(
        Artifact(
          "amd-hsa",
          s"ROCR-Runtime-rocm-$HsaVersion.tar.gz",
          s"https://github.com/ROCm/ROCR-Runtime/archive/refs/tags/rocm-$HsaVersion.tar.gz",
          "4c5b58afa1e11461954bd005a10ebf29941c120f1d6a7863954597f5eacfc605"
        ),
        "amd/hsa",
        s"ROCR-Runtime-rocm-$HsaVersion",
        Vector(
          "LICENSE.txt",
          "runtime/hsa-runtime/LICENSE.md",
          "libhsakmt/include/**",
          "runtime/hsa-runtime/inc/hsa*.h"
        )
      ),
      Source(
        Artifact(
          "opencl-headers",
          s"OpenCL-Headers-$OpenCLHeadersVersion.zip",
          s"https://github.com/KhronosGroup/OpenCL-Headers/archive/refs/tags/v$OpenCLHeadersVersion.zip",
          "0bdef6f653c577827a75fac3e3f18b0bf000171e39fa65c2eb84ec804c822cab"
        ),
        "khronos/opencl",
        s"OpenCL-Headers-$OpenCLHeadersVersion",
        Vector("LICENSE", "CL/**")
      ),
      Source(
        LevelZero,
        "oneapi/level-zero",
        s"level-zero-$LevelZeroVersion",
        Vector("LICENSE", "include/**", "doc/**")
      ),
      Source(
        Artifact(
          "vulkan-headers",
          s"Vulkan-Headers-$VulkanHeadersVersion.zip",
          s"https://github.com/KhronosGroup/Vulkan-Headers/archive/refs/tags/$VulkanHeadersVersion.zip",
          "c3b074a30b5bce4cf32a41d234fa559ff458513b7314d69cd0b336a01b53bb78"
        ),
        "khronos/vulkan",
        s"Vulkan-Headers-${VulkanHeadersVersion.stripPrefix("v")}",
        Vector("LICENSE.md", "include/vulkan/**", "include/vk_video/**")
      )
    ),
    Vector(
      Write(
        "amd/hip/include/hip/hip_version.h",
        s"""#ifndef HIP_VERSION_H
           |#define HIP_VERSION_H
           |
           |#define HIP_VERSION_MAJOR ${HipVersion.split('.')(0)}
           |#define HIP_VERSION_MINOR ${HipVersion.split('.')(1)}
           |#define HIP_VERSION_PATCH $HipVersionPatch
           |#define HIP_VERSION_GITHASH ""
           |#define HIP_VERSION_BUILD_ID 0
           |#define HIP_VERSION_BUILD_NAME ""
           |#define HIP_VERSION    (HIP_VERSION_MAJOR * 10000000 + HIP_VERSION_MINOR * 100000 + HIP_VERSION_PATCH)
           |
           |#define __HIP_HAS_GET_PCH 1
           |
           |#endif
           |""".stripMargin
      )
    ),
    Vector(
      Expect("nvidia/cuda/include/cuda.h"),
      Expect("nvidia/cuda/include/cuda_runtime_api.h"),
      Expect("amd/hip/include/hip/hip_runtime_api.h"),
      Expect("amd/hip/include/hip/amd_detail/host_defines.h"),
      Expect("amd/hsa/runtime/hsa-runtime/inc/hsa.h"),
      Expect("khronos/opencl/CL/cl.h"),
      Expect("oneapi/level-zero/include/ze_api.h"),
      Expect("khronos/vulkan/include/vulkan/vulkan.h")
    )
  )

  lazy val LevelZero: Artifact = Artifact(
    "level-zero",
    s"level-zero-$LevelZeroVersion.zip",
    s"https://github.com/oneapi-src/level-zero/archive/refs/tags/v$LevelZeroVersion.zip",
    "9f6872a84106bdb686131c231396eca67631e1c47081ef50e2fe0b815aca6eb0"
  )

  val LevelZeroSource: Bundle = Bundle(
    "level-zero",
    Vector(Source(LevelZero, "", s"level-zero-$LevelZeroVersion")),
    expect = Vector(Expect("CMakeLists.txt"))
  )

  val PTX: Bundle = Bundle(
    "ptx",
    Vector(
      Source(
        Artifact(
          "nvidia-ptx",
          s"nvidia-ptx-$CudaPTXVersion.html",
          s"https://docs.nvidia.com/cuda/archive/$CudaPTXVersion/parallel-thread-execution/index.html",
          "40026f79b65fee3f8c8b3e717c6f80c6385e8d97215318d6e4b7f6106f58d5a4"
        ),
        "nvidia/ptx/index.html"
      )
    ),
    expect = Vector(Expect("nvidia/ptx/index.html"))
  )

  val SASSTools: Bundle = Bundle(
    "sass-tools",
    Nvdisasm.map((generation, version, artifact, _) =>
      Source(artifact, generation, include = Vector(s"usr/local/cuda-$version/bin/nvdisasm"), executable = true)
    ),
    expect = Nvdisasm.map((generation, version, _, digest) =>
      Expect(s"$generation/usr/local/cuda-$version/bin/nvdisasm", Some(digest))
    )
  )

  val Tablegen: Bundle = Bundle(
    "llvm-tablegen",
    Vector(
      Source(
        Llvm,
        "",
        s"llvm-project-$LlvmVersion.src",
        Vector("llvm/**", "cmake/**", "third-party/**", "libc/**"),
        Vector("llvm/test/**", "llvm/utils/mlgo-utils/*.py")
      )
    ),
    expect = Vector(Expect("llvm/utils/TableGen/CMakeLists.txt"))
  )

  val AMDGPU: Bundle = Bundle(
    "amdgpu",
    Vector(
      Source(
        Llvm,
        "llvm",
        s"llvm-project-$LlvmVersion.src",
        Vector(
          "llvm/LICENSE.TXT",
          "llvm/test/MC/AMDGPU/**",
          "llvm/lib/Target/AMDGPU/SIDefines.h",
          "llvm/lib/Target/AMDGPU/*.td",
          "llvm/include/**/*.td",
          "llvm/include/llvm/BinaryFormat/ELFRelocs/AMDGPU.def",
          "llvm/include/llvm/BinaryFormat/ELF.h",
          "llvm/include/llvm/Support/AMDHSAKernelDescriptor.h"
        )
      ),
      Source(
        Artifact(
          "mesa-aco-opcodes",
          s"aco_opcodes-mesa-$MesaVersion.py",
          s"https://gitlab.freedesktop.org/api/v4/projects/mesa%2Fmesa/repository/files/src%2Famd%2Fcompiler%2Faco_opcodes.py/raw?ref=mesa-$MesaVersion",
          "21d9c086dd32b0dbe094f1523ac337d350258a3fb973a510a46fbea85a56bee1"
        ),
        "mesa/src/amd/compiler/aco_opcodes.py"
      ),
      Source(
        Artifact(
          "amd-xml",
          s"AMD_GPU_MR_ISA_XML_$AmdXMLVersion.zip",
          s"https://gpuopen.com/download/AMD_GPU_MR_ISA_XML_$AmdXMLVersion.zip",
          "82404f1126761b7877595b622afa7e1f311f2f41e89a3abe9aaf8ad045c082e2"
        ),
        "amd/xml",
        include = Vector("amdgpu_isa_*.xml")
      )
    ),
    expect = Vector(
      Expect("llvm/llvm/test/MC/AMDGPU/**"),
      Expect("llvm/llvm/include/llvm/BinaryFormat/ELF.h"),
      Expect("llvm/llvm/include/llvm/Support/AMDHSAKernelDescriptor.h"),
      Expect("mesa/src/amd/compiler/aco_opcodes.py"),
      Expect("amd/xml/amdgpu_isa_rdna2.xml")
    )
  )

  val SPIRV: Bundle = Bundle(
    "spirv",
    Vector(
      Source(
        Artifact(
          "spirv-headers",
          s"SPIRV-Headers-$SpirvHeadersVersion.zip",
          s"https://github.com/KhronosGroup/SPIRV-Headers/archive/refs/tags/$SpirvHeadersVersion.zip",
          "7d40c8c1983c1aba6f6f2fcbcf7be345d33a5693aeee0cc5b8c579fb8e3f2fea"
        ),
        "khronos/spirv-headers",
        s"SPIRV-Headers-$SpirvHeadersVersion",
        Vector("LICENSE", "LICENSES/**", "include/spirv/unified1/**")
      )
    ),
    expect = Vector(
      Expect("khronos/spirv-headers/include/spirv/unified1/spirv.core.grammar.json"),
      Expect("khronos/spirv-headers/include/spirv/unified1/extinst.glsl.std.450.grammar.json"),
      Expect("khronos/spirv-headers/include/spirv/unified1/extinst.opencl.std.100.grammar.json")
    )
  )

  val Bundles: Vector[Bundle] = Vector(API, LevelZeroSource, PTX, SASSTools, Tablegen, AMDGPU, SPIRV)

  private val Client = HttpClient.newBuilder()
    .followRedirects(HttpClient.Redirect.NORMAL)
    .connectTimeout(Duration.ofSeconds(30))
    .build()

  def prepare(bundle: Bundle, cache: Path, output: Path, offline: Boolean): Path = {
    val manifest =
      (Vector("format\t3", s"bundle\t${bundle.name}") ++
        bundle.sources.map(_.artifact).distinct.map(artifact =>
          s"artifact\t${artifact.id}\t${artifact.fileName}\t${artifact.sha256}\t${artifact.url}"
        ) ++ bundle.sources.map(source =>
          Vector(
            "source",
            source.artifact.id,
            source.into,
            source.root,
            source.include.mkString(","),
            source.exclude.mkString(","),
            source.executable.toString
          ).mkString("\t")
        ) ++ bundle.writes.map(write =>
          s"write\t${write.path}\t${sha256(write.content.getBytes(StandardCharsets.UTF_8))}"
        ) ++ bundle.expect.map(expectation => s"expect\t${expectation.glob}\t${expectation.sha256.getOrElse("")}"))
        .mkString("", "\n", "\n")
    val target = output.resolve(bundle.name)
    if (
      Files.isDirectory(target) &&
      (try { check(bundle, target, manifest); true }
      catch { case NonFatal(_) => false })
    )
      target
    else {
      val _       = Files.createDirectories(output)
      val staging = Files.createTempDirectory(output, s".${bundle.name}.")
      bundle.sources.foreach(source => extract(fetch(source.artifact, cache, offline), source, staging))
      bundle.writes.foreach(write => GeneratedFiles.write(staging.resolve(write.path), write.content))
      val _ = Files.writeString(staging.resolve("MANIFEST.tsv"), manifest, StandardCharsets.UTF_8)
      check(bundle, staging, manifest)
      delete(target)
      val _ = Files.move(staging, target)
      Console.out.println(s"prepared reference bundle ${bundle.name} at $target")
      target
    }
  }

  def tablegen(cache: Path, output: Path, offline: Boolean): Path = {
    val source = prepare(Tablegen, cache, output, offline)
    val build  = output.resolve("llvm-tablegen-build")
    if (!Files.isRegularFile(build.resolve("CMakeCache.txt")))
      run(
        Vector(
          "cmake",
          "-S",
          s"${source.resolve("llvm")}",
          "-B",
          s"$build",
          "-G",
          "Ninja",
          "-DCMAKE_BUILD_TYPE=Release",
          "-DLLVM_TARGETS_TO_BUILD=",
          "-DLLVM_INCLUDE_TESTS=OFF",
          "-DLLVM_INCLUDE_BENCHMARKS=OFF",
          "-DLLVM_INCLUDE_EXAMPLES=OFF",
          "-DLLVM_ENABLE_TERMINFO=OFF",
          "-DLLVM_ENABLE_ZLIB=OFF",
          "-DLLVM_ENABLE_ZSTD=OFF"
        )
      )
    run(Vector("cmake", "--build", s"$build", "--target", "llvm-min-tblgen"))
    build.resolve("bin/llvm-min-tblgen")
  }

  private def run(command: Vector[String]): Unit = {
    val status = Process(command).!
    require(status == 0, s"${command.mkString(" ")} exited with $status")
  }

  private def check(bundle: Bundle, path: Path, manifest: String): Unit = {
    require(Files.readString(path.resolve("MANIFEST.tsv")) == manifest, s"stale reference bundle: $path")
    val present = Using.resource(Files.walk(path))(_.toList.asScala.toVector)
      .filter(Files.isRegularFile(_))
      .map(path.relativize(_))
    bundle.expect.foreach { expectation =>
      val matching = present.filter(matcher(expectation.glob))
      require(matching.nonEmpty, s"reference bundle ${bundle.name} lacks ${expectation.glob}")
      expectation.sha256.foreach(digest =>
        require(matching.forall(file => sha256(path.resolve(file)) == digest), s"${expectation.glob} hash mismatch")
      )
    }
  }

  private def fetch(artifact: Artifact, cache: Path, offline: Boolean): Path = {
    val target = cache.resolve(artifact.fileName)
    if (Files.isRegularFile(target) && sha256(target) == artifact.sha256) target
    else {
      require(!offline, s"${artifact.id}: valid artifact is absent from offline cache $target")
      val _       = Files.createDirectories(cache)
      val staging = Files.createTempFile(cache, s".${artifact.fileName}.", ".download")
      Console.out.println(s"downloading ${artifact.id} ${artifact.url}")
      val response = Client.send(
        HttpRequest.newBuilder(URI.create(artifact.url)).timeout(Duration.ofMinutes(30)).GET().build(),
        HttpResponse.BodyHandlers.ofFile(staging)
      )
      require(response.statusCode / 100 == 2, s"${artifact.id}: HTTP ${response.statusCode} from ${artifact.url}")
      val actual = sha256(staging)
      require(actual == artifact.sha256, s"${artifact.id}: expected ${artifact.sha256}, downloaded $actual")
      Files.move(staging, target, StandardCopyOption.REPLACE_EXISTING)
    }
  }

  private def extract(archive: Path, source: Source, staging: Path): Unit = {
    val destination                   = staging.resolve(source.into)
    val included                      = source.include.map(matcher(_))
    val excluded                      = source.exclude.map(matcher(_))
    val name                          = source.artifact.fileName
    def selected(path: Path): Boolean = included.exists(_(path)) && !excluded.exists(_(path))
    def entries(input: InputStream, next: () => Option[(String, Boolean)]): Unit =
      LazyList.continually(next()).takeWhile(_.nonEmpty).flatten.foreach { (entry, file) =>
        relative(entry, source.root).filter(path => file && selected(path)).foreach { path =>
          val target = destination.resolve(path).normalize
          require(target.startsWith(destination), s"archive entry escapes destination: $entry")
          val _ = Files.createDirectories(target.getParent)
          val _ = Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING)
        }
      }
    def tar(input: TarArchiveInputStream): Unit = entries(
      input,
      () =>
        Option(input.getNextEntry).map(entry => entry.getName -> entry.isFile)
    )
    if (name.endsWith(".deb"))
      Using.Manager { use =>
        val ar    = use(ArArchiveInputStream(use(BufferedInputStream(Files.newInputStream(archive)))))
        val found = LazyList.continually(Option(ar.getNextEntry)).takeWhile(_.nonEmpty).flatten
          .exists(_.getName == "data.tar.xz")
        require(found, s"Debian archive has no data.tar.xz payload: $archive")
        tar(use(TarArchiveInputStream(use(XZCompressorInputStream.builder().setInputStream(ar).get()))))
      }.get
    else if (name.endsWith(".tar.xz") || name.endsWith(".tar.gz"))
      Using.Manager { use =>
        val file                      = use(BufferedInputStream(Files.newInputStream(archive)))
        val decompressed: InputStream =
          if (name.endsWith(".xz")) use(XZCompressorInputStream.builder().setInputStream(file).get())
          else use(GzipCompressorInputStream(file))
        tar(use(TarArchiveInputStream(decompressed)))
      }.get
    else if (name.endsWith(".zip"))
      Using.resource(ZipInputStream(BufferedInputStream(Files.newInputStream(archive)))) { zip =>
        entries(zip, () => Option(zip.getNextEntry).map(entry => entry.getName -> !entry.isDirectory))
      }
    else {
      val _ = Files.createDirectories(destination.getParent)
      val _ = Files.copy(archive, destination, StandardCopyOption.REPLACE_EXISTING)
    }
    if (source.executable)
      Using.resource(Files.walk(destination))(_.toList.asScala.toVector)
        .filter(file => Files.isRegularFile(file) && selected(destination.relativize(file)))
        .foreach(file => require(file.toFile.setExecutable(true), s"cannot mark $file executable"))
  }

  private def relative(entry: String, root: String): Option[Path] = {
    val name = entry.stripPrefix("./")
    require(!name.startsWith("/") && !name.split('/').contains(".."), s"unsafe archive path: $entry")
    if (root.isEmpty) Option.when(name.nonEmpty)(Path.of(name))
    else Option.when(name.startsWith(s"$root/") && name.length > root.length + 1)(Path.of(name.drop(root.length + 1)))
  }

  private val GlobToken = raw"\*\*/|\*\*|\*|\?|[^*?]+".r

  private def matcher(glob: String): Path => Boolean = {
    val regex = GlobToken.findAllMatchIn(glob).toVector.map(_.matched match {
      case "**/"   => "(?:.*/)?"
      case "**"    => ".*"
      case "*"     => "[^/]*"
      case "?"     => "[^/]"
      case literal => Pattern.quote(literal)
    }).mkString.r
    path => regex.matches(path.toString.replace(File.separatorChar, '/'))
  }

  private def delete(path: Path): Unit =
    if (Files.exists(path))
      Using.resource(Files.walk(path))(_.toList.asScala.toVector).reverse.foreach(file => Files.delete(file))

  private def sha256(path: Path): String = {
    val digest = MessageDigest.getInstance("SHA-256")
    val _      = Using.resource(DigestInputStream(Files.newInputStream(path), digest))(
      _.transferTo(OutputStream.nullOutputStream())
    )
    java.util.HexFormat.of().formatHex(digest.digest())
  }

  private def sha256(bytes: Array[Byte]): String =
    java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes))
}
