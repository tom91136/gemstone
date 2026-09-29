import ExternalProjectPlugin.autoImport.*
import Paths.*
import Paths.given
import StringCase.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import sbt.internal.util.MessageOnlyException
import scala.concurrent.duration.*
import scala.language.implicitConversions
import scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport.NativeTags
import scala.sys.process.Process
import scala.sys.process.ProcessLogger
import scala.util.control.NonFatal
import stryker4s.sbt.Stryker4sPlugin

// Each Native linker can consume gigabytes; use the plugin's standard scheduling tag.
Global / concurrentRestrictions += Tags.limit(NativeTags.Link, 1)

lazy val projectOrganization  = "net.kurobako"
lazy val rootPackage          = s"$projectOrganization.gemstone"
lazy val scala3Version        = "3.9.0"
lazy val munitVersion         = "1.3.6"
lazy val catsCoreVersion      = "2.13.0"
lazy val catsParseVersion     = "1.1.0"
lazy val declineVersion       = "2.6.2"
lazy val jsoupVersion         = "1.23.2"
lazy val scribeVersion        = "3.19.0"
lazy val upickleVersion       = "4.4.3"
lazy val scalafmtVersion      = "3.11.5"
lazy val llvmVersion          = "23.1.0"
lazy val mesaVersion          = "26.2.1"
lazy val cudaPTXVersion       = "13.3.0"
lazy val cudaAPIVersion       = "13.3"
lazy val cudaAPIPackage       = "13.3.29-1"
lazy val cudaCRTPackage       = "13.3.33-1"
lazy val cudaCUBLASPackage    = "13.6.0.2-1"
lazy val cudaCCCLPackage      = "13.3.3.4.1-1"
lazy val rocmVersion          = "7.1.1"
lazy val hipVersion           = rocmVersion
lazy val hsaVersion           = rocmVersion
lazy val amdXMLVersion        = "2026_08_06"
lazy val openCLHeadersVersion = "2026.05.29"
lazy val levelZeroVersion     = "1.33.1"
lazy val vulkanHeadersVersion = "v1.4.341"
lazy val spirvHeadersVersion  = "vulkan-sdk-1.4.357.0"
lazy val spirvToolsVersion    = "vulkan-sdk-1.4.357.0"
lazy val ptxISAVersion        = "9.3"
lazy val ptxISAPackage        = "v" + ptxISAVersion.replace('.', '_')
lazy val spirvVersion         = "1.6"

// Conformance workloads are much larger than interactive use, so the emulated
// suite runs with a bigger budget than TaskControl.Limits.Default. Keep the two in sync
// if the interpreter's instruction accounting changes. The big-index thrust
// cases alone need more than the old 1e9.
lazy val conformanceMaxInstructions = "100000000000"
lazy val spirvPackage               = "v" + spirvVersion.replace('.', '_')

lazy val referenceArtifacts = Vector(
  Fetch.Request(
    id = "llvm",
    fileName = s"llvm-project-$llvmVersion.src.tar.xz",
    url =
      s"https://github.com/llvm/llvm-project/releases/download/llvmorg-$llvmVersion/llvm-project-$llvmVersion.src.tar.xz",
    expectedSha256 = "ab1f0e3ec52448c33e8782eaf0422504b87c7b016b22514653ee0d8fcee479ff"
  ),
  Fetch.Request(
    id = "mesa",
    fileName = s"mesa-$mesaVersion.tar.xz",
    url = s"https://archive.mesa3d.org/mesa-$mesaVersion.tar.xz",
    expectedSha256 = "c47e81bddc4760360a41ac3c5acec38acb81f9d750ecef47e7f3adc7021a4442"
  ),
  Fetch.Request(
    id = "nvidia-ptx",
    fileName = s"nvidia-ptx-$cudaPTXVersion.html",
    url =
      s"https://docs.nvidia.com/cuda/archive/$cudaPTXVersion/parallel-thread-execution/index.html",
    expectedSha256 = "40026f79b65fee3f8c8b3e717c6f80c6385e8d97215318d6e4b7f6106f58d5a4"
  ),
  Fetch.Request(
    id = "nvidia-cuda-driver-dev",
    fileName =
      s"cuda-driver-dev-${cudaAPIVersion.replace('.', '-')}_${cudaAPIPackage}_amd64.deb",
    url =
      s"https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2604/x86_64/cuda-driver-dev-${cudaAPIVersion.replace('.', '-')}_${cudaAPIPackage}_amd64.deb",
    expectedSha256 = "8bf25d2a822c107afea871c3f521014b55af095d7ac80d56dda27a7599f0fc70"
  ),
  Fetch.Request(
    id = "nvidia-cuda-runtime-dev",
    fileName =
      s"cuda-cudart-dev-${cudaAPIVersion.replace('.', '-')}_${cudaAPIPackage}_amd64.deb",
    url =
      s"https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2604/x86_64/cuda-cudart-dev-${cudaAPIVersion.replace('.', '-')}_${cudaAPIPackage}_amd64.deb",
    expectedSha256 = "0c137b804e1b68879b873ce996f23f6ca9bc644872be15cb870bdbc43319a717"
  ),
  Fetch.Request(
    id = "nvidia-cuda-crt",
    fileName =
      s"cuda-crt-${cudaAPIVersion.replace('.', '-')}_${cudaCRTPackage}_amd64.deb",
    url =
      s"https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2604/x86_64/cuda-crt-${cudaAPIVersion.replace('.', '-')}_${cudaCRTPackage}_amd64.deb",
    expectedSha256 = "945852c041126c913c0b1ed9fa8d231550fcaf5a3e05962ecd4669807a890f66"
  ),
  Fetch.Request(
    id = "nvidia-cublas-dev",
    fileName =
      s"libcublas-dev-${cudaAPIVersion.replace('.', '-')}_${cudaCUBLASPackage}_amd64.deb",
    url =
      s"https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2604/x86_64/libcublas-dev-${cudaAPIVersion.replace('.', '-')}_${cudaCUBLASPackage}_amd64.deb",
    expectedSha256 = "ccf766867c60ecbeb9ab8d83f04e1aea97f2940e79b38f984c6c353a4c5a4dd8"
  ),
  Fetch.Request(
    id = "nvidia-cccl",
    fileName =
      s"cccl-${cudaAPIVersion.replace('.', '-')}_${cudaCCCLPackage}_amd64.deb",
    url =
      s"https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2604/x86_64/cccl-${cudaAPIVersion.replace('.', '-')}_${cudaCCCLPackage}_amd64.deb",
    expectedSha256 = "510519a532b50d047b33a7f658e9cf54a3e80489d72e1d6f507e4b8abbb5818d"
  ),
  Fetch.Request(
    id = "amd-hip",
    fileName = s"HIP-rocm-$hipVersion.tar.gz",
    url = s"https://github.com/ROCm/HIP/archive/refs/tags/rocm-$hipVersion.tar.gz",
    expectedSha256 = "c64b3219237903d6b27944f236930a1024ed17eb5399165875fbf410fcacf6f4"
  ),
  Fetch.Request(
    id = "amd-hsa",
    fileName = s"ROCR-Runtime-rocm-$hsaVersion.tar.gz",
    url =
      s"https://github.com/ROCm/ROCR-Runtime/archive/refs/tags/rocm-$hsaVersion.tar.gz",
    expectedSha256 = "4c5b58afa1e11461954bd005a10ebf29941c120f1d6a7863954597f5eacfc605"
  ),
  Fetch.Request(
    id = "amd-xml",
    fileName = s"AMD_GPU_MR_ISA_XML_$amdXMLVersion.zip",
    url = s"https://gpuopen.com/download/AMD_GPU_MR_ISA_XML_$amdXMLVersion.zip",
    expectedSha256 = "82404f1126761b7877595b622afa7e1f311f2f41e89a3abe9aaf8ad045c082e2"
  ),
  Fetch.Request(
    id = "opencl-headers",
    fileName = s"OpenCL-Headers-$openCLHeadersVersion.zip",
    url =
      s"https://github.com/KhronosGroup/OpenCL-Headers/archive/refs/tags/v$openCLHeadersVersion.zip",
    expectedSha256 = "0bdef6f653c577827a75fac3e3f18b0bf000171e39fa65c2eb84ec804c822cab"
  ),
  Fetch.Request(
    id = "level-zero",
    fileName = s"level-zero-$levelZeroVersion.zip",
    url =
      s"https://github.com/oneapi-src/level-zero/archive/refs/tags/v$levelZeroVersion.zip",
    expectedSha256 = "9f6872a84106bdb686131c231396eca67631e1c47081ef50e2fe0b815aca6eb0"
  ),
  Fetch.Request(
    id = "vulkan-headers",
    fileName = s"Vulkan-Headers-$vulkanHeadersVersion.zip",
    url = s"https://github.com/KhronosGroup/Vulkan-Headers/archive/refs/tags/$vulkanHeadersVersion.zip",
    expectedSha256 = "c3b074a30b5bce4cf32a41d234fa559ff458513b7314d69cd0b336a01b53bb78"
  ),
  Fetch.Request(
    id = "spirv-headers",
    fileName = s"SPIRV-Headers-$spirvHeadersVersion.zip",
    url =
      s"https://github.com/KhronosGroup/SPIRV-Headers/archive/refs/tags/$spirvHeadersVersion.zip",
    expectedSha256 = "7d40c8c1983c1aba6f6f2fcbcf7be345d33a5693aeee0cc5b8c579fb8e3f2fea"
  ),
  Fetch.Request(
    id = "spirv-tools",
    fileName = s"SPIRV-Tools-$spirvToolsVersion.zip",
    url =
      s"https://github.com/KhronosGroup/SPIRV-Tools/archive/refs/tags/$spirvToolsVersion.zip",
    expectedSha256 = "09f70a9197d9cd28e1e10a2fca5ffde8c5e269f363eed9afa838ad88ed2649c9"
  )
)

def referenceArtifactsNamed(
    first: String,
    rest: String*
): Vector[Fetch.Request] = {
  val requested = (first +: rest).toSet
  val selected  = referenceArtifacts.filter(artifact => requested(artifact.id))
  require(
    selected.map(_.id).toSet == requested,
    s"unknown reference artifacts: ${(requested -- selected.map(_.id)).mkString(", ")}"
  )
  selected
}

// Native CMake fallbacks share the codegen pins instead of hardcoding them.
def referenceArtifact(id: String): Fetch.Request = {
  val selected = referenceArtifacts.filter(_.id == id)
  require(selected.size == 1, s"expected one reference artifact '$id', found ${selected.size}")
  selected.head
}

// Owned names and shared numeric defaults. Parsing belongs to each consumer.
lazy val environmentVariables: Vector[(String, Option[Long])] = Vector(
  "BENCHMARK_JFR"                -> None,
  "OFFLINE"                      -> None,
  "EXECUTION_MAX_INSTRUCTIONS"   -> None,
  "EXECUTION_PARALLELISM"        -> None,
  "HSA_PAGING_TIMEOUT_SECONDS"   -> None,
  "HSA_REJECTED_CODE_OBJECT"     -> None,
  "NATIVE_DIRECTORY"             -> None,
  "OPENCL_COMPILER"              -> None,
  "OPENCL_LLVM_LINK"             -> None,
  "OPENCL_LLVM_SPIRV"            -> None,
  "OPENCL_LOCAL_MEM_SIZE"        -> Some(65536L),
  "OPENCL_MAX_MEM_ALLOC_SIZE"    -> Some(268435456L),
  "OPENCL_MAX_WORK_GROUP_SIZE"   -> Some(64L),
  "OPENCL_SCAN_BUFFER_POINTERS"  -> None,
  "PROFILE"                      -> None,
  "RECORD"                       -> None,
  "RPC_CLIENT"                   -> None,
  "RPC_ENDPOINT"                 -> None,
  "RPC_TIMEOUT_SECONDS"          -> Some(300L),
  "SYSTEM_PAGING"                -> None,
  "TARGET_ARCH"                  -> None,
  "TRACE"                        -> None,
  "VULKAN_DUMP"                  -> None,
  "MEMORY_ALLOCATION_KIND"       -> None,
  "MEMORY_ATOMIC_BITS"           -> None,
  "MEMORY_CONCURRENCY_DIRECTORY" -> None,
  "MEMORY_PROBE"                 -> None,
  "TEST_LIBRARY"                 -> None
)

def environmentName(key: String): String = {
  require(environmentVariables.exists(_._1 == key), s"unknown environment variable: $key")
  s"GEMSTONE_$key"
}

lazy val environmentArguments: Seq[String] = environmentVariables.flatMap { case (key, default) =>
  Seq(environmentName(key), default.fold("")(_.toString))
}

def positiveEnvironmentInt(name: String, default: => Int): Int = {
  val raw = sys.env.get(name).fold(default) { value =>
    Option.when(value.nonEmpty && value.forall(c => c >= '0' && c <= '9'))(value)
      .flatMap(_.toIntOption).getOrElse(
        throw MessageOnlyException(s"$name must be a positive integer: $value")
      )
  }
  if (raw <= 0) throw MessageOnlyException(s"$name must be a positive integer: $raw")
  raw
}

def environmentFlag(name: String): Boolean =
  sys.env.get(name).exists { value =>
    value.toLowerCaseRoot match {
      case "1" | "true" | "on"   => true
      case "0" | "false" | "off" => false
      case _                     => throw MessageOnlyException(s"$name must be a boolean: $value")
    }
  }

def runConformance(
    root: Path,
    artifacts: Path,
    executable: Path,
    ctest: String,
    arguments: Seq[String],
    output: Path,
    launcher: Seq[String],
    environment: Seq[(String, String)],
    log: String => Unit,
    error: String => Unit
): Unit = {
  val command = Vector(
    s"$executable",
    "run",
    "--profiles",
    s"${root / "conformance/profiles"}",
    "--artifacts",
    s"$artifacts",
    "--output",
    s"$output",
    "--ctest",
    ctest
  ) ++ arguments ++ (if (launcher.isEmpty) Vector.empty else Vector("--launcher", "--") ++ launcher)
  val status = Process(command, root.toFile, environment *).!(ProcessLogger(log, error))
  require(status == 0, s"conformance run exited with $status")
}

// The build owns emulator routing; the conformance project only sees an opaque launcher.
def withConformanceLauncher[A](
    root: Path,
    command: Seq[String],
    environment: Seq[(String, String)]
)(run: Seq[String] => A): A = {
  def quoted(value: String): String = "'" + value.replace("'", "'\"'\"'") + "'"
  val directory                     = root / "target/conformance"
  Files.createDirectories(directory)
  val script     = Files.createTempFile(directory, "launcher-", ".sh")
  val configured = environment.toMap
    .toVector.sortBy(_._1).map { case (key, value) => s"export $key=${quoted(value)}" }.mkString("\n")
  val routing =
    s"""
    |unset CONFORMANCE_DEVICE
    |case "$$CONFORMANCE_TARGET_BACKEND" in
    |  cuda|hip|hsa)
    |    if [ -z "$$${environmentName("TARGET_ARCH")}" ] && [ -n "$$CONFORMANCE_TARGET_ARCHITECTURE" ]; then
    |      export ${environmentName("TARGET_ARCH")}="$$CONFORMANCE_TARGET_ARCHITECTURE"
    |    fi ;;
    |  levelzero) export CONFORMANCE_DEVICE='*Gemstone*' ;;
    |esac
    |toolchain="$$CONFORMANCE_SUPPORT_ROOT/opencl-toolchain"
    |if [ -z "$$${environmentName("OPENCL_COMPILER")}" ] && [ -x "$$toolchain/bin/icpx" ]; then
    |  export ${environmentName("OPENCL_COMPILER")}="$$toolchain/bin/icpx"
    |fi
    |if [ -z "$$${environmentName("OPENCL_LLVM_SPIRV")}" ] && [ -x "$$toolchain/bin/compiler/llvm-spirv" ]; then
    |  export ${environmentName("OPENCL_LLVM_SPIRV")}="$$toolchain/bin/compiler/llvm-spirv"
    |fi
    |if [ -z "$$${environmentName("OPENCL_LLVM_LINK")}" ] && [ -x "$$toolchain/bin/compiler/llvm-link" ]; then
    |  export ${environmentName("OPENCL_LLVM_LINK")}="$$toolchain/bin/compiler/llvm-link"
    |fi
    |export CONFORMANCE_OPENCL_CTS_WIMPY=1
    |export CONFORMANCE_OPENCL_CTS_TIMEOUT=600
    |""".stripMargin
  try {
    Files.writeString(script, configured + "\n" + routing + "exec " + command.map(quoted).mkString(" ") + " \"$@\"\n")
    run(Seq("/bin/sh", script.toString))
  } finally Files.deleteIfExists(script)
}

def awaitGemstoneServerEndpoint(endpointFile: Path, server: scala.sys.process.Process): String =
  Processes.awaitJsonString(endpointFile, "endpoint", server, 30.seconds).getOrElse {
    val reason = if (server.isAlive()) "did not publish an endpoint" else s"exited with ${server.exitValue()}"
    throw MessageOnlyException(s"Gemstone conformance server $reason")
  }

def withGemstoneServer(
    root: Path,
    launchServer: Path => scala.sys.process.Process,
    shutdownServer: String => Boolean,
    log: sbt.util.Logger
)(run: String => Unit): Unit = {
  val parent = root / "target/conformance"
  Files.createDirectories(parent)
  val directory    = Files.createTempDirectory(parent, "server-")
  val endpointFile = directory / "endpoint.json"
  val server       = try launchServer(endpointFile)
  catch {
    case NonFatal(failure) =>
      Files.deleteIfExists(endpointFile)
      Files.deleteIfExists(directory)
      throw failure
  }
  val shutdownHook = Thread(() => server.destroy(), "gemstone-conformance-server-cleanup")
  sys.runtime.addShutdownHook(shutdownHook)
  var endpoint = Option.empty[String]
  try {
    val activeEndpoint = awaitGemstoneServerEndpoint(endpointFile, server)
    endpoint = Some(activeEndpoint)
    run(activeEndpoint)
  } finally {
    endpoint match {
      case Some(activeEndpoint) if server.isAlive() =>
        try
          if (!shutdownServer(activeEndpoint)) server.destroy()
        catch {
          case NonFatal(failure) =>
            log.error(s"cannot stop Gemstone conformance server: ${failure.getMessage}"); server.destroy()
        }
      case None if server.isAlive() => server.destroy()
      case _                        =>
    }
    try {
      val _ = server.exitValue()
      Files.deleteIfExists(endpointFile)
      Files.deleteIfExists(directory)
    } finally
      try sys.runtime.removeShutdownHook(shutdownHook)
      catch { case _: IllegalStateException => () }
  }
}

lazy val apiReferenceBundle = ReferenceBundle(
  name = "api-native",
  recipeRevision = "1",
  artifacts = referenceArtifactsNamed(
    "nvidia-cuda-driver-dev",
    "nvidia-cuda-runtime-dev",
    "nvidia-cuda-crt",
    "nvidia-cublas-dev",
    "nvidia-cccl",
    "amd-hip",
    "amd-hsa",
    "opencl-headers",
    "level-zero",
    "vulkan-headers"
  ),
  populate = (cached, stage) => {
    Compress.extractDebXz(
      archive = cached("nvidia-cuda-driver-dev"),
      destination = stage / "nvidia/cuda",
      select = relative =>
        relative.endsWith(path"include/cuda.h") || containsPathSegments(relative, "share", "doc")
    )
    Compress.extractDebXz(
      archive = cached("nvidia-cuda-runtime-dev"),
      destination = stage / "nvidia/cuda",
      select = relative =>
        (containsPathSegments(relative, "include") && relative != path"usr/local/cuda-$cudaAPIVersion/include") ||
          containsPathSegments(relative, "share", "doc")
    )
    Compress.extractDebXz(
      archive = cached("nvidia-cuda-crt"),
      destination = stage / "nvidia/cuda",
      select = relative =>
        containsPathSegments(relative, "include", "crt") || containsPathSegments(relative, "share", "doc")
    )
    Compress.extractDebXz(
      archive = cached("nvidia-cublas-dev"),
      destination = stage / "nvidia/cuda",
      select = relative =>
        (containsPathSegments(relative, "targets", "x86_64-linux", "include") && relative.fileName.endsWith(".h")) ||
          containsPathSegments(relative, "share", "doc")
    )
    Compress.extractDebXz(
      archive = cached("nvidia-cccl"),
      destination = stage / "nvidia/cuda",
      select = relative => containsPathSegments(relative, "targets", "x86_64-linux", "include")
    )
    Compress.extractTarGz(
      archive = cached("amd-hip"),
      destination = stage / "amd/hip",
      archiveRoot = s"hip-rocm-$hipVersion",
      select = relative =>
        relative == path"LICENSE.md" || relative == path"VERSION" || relative.startsWith("include/hip")
    )
    val hipVersions = Files
      .readString(stage / "amd/hip" / "VERSION", StandardCharsets.UTF_8)
      .linesIterator
      .toList
      .sliding(2)
      .collect { case Seq(key, value) if key.startsWith("#") => key.stripPrefix("#") -> value.trim }
      .toMap
    val hipVersionHeader =
      s"""// Auto-generated from the pinned HIP VERSION file.
         |#ifndef HIP_VERSION_H
         |#define HIP_VERSION_H
         |
         |#define HIP_VERSION_MAJOR ${hipVersions("HIP_VERSION_MAJOR")}
         |#define HIP_VERSION_MINOR ${hipVersions("HIP_VERSION_MINOR")}
         |#define HIP_VERSION_PATCH ${hipVersions("HIP_VERSION_PATCH")}
         |#define HIP_VERSION_GITHASH ""
         |#define HIP_VERSION_BUILD_ID 0
         |#define HIP_VERSION_BUILD_NAME ""
         |#define HIP_VERSION    (HIP_VERSION_MAJOR * 10000000 + HIP_VERSION_MINOR * 100000 + HIP_VERSION_PATCH)
         |
         |#define __HIP_HAS_GET_PCH 1
         |
         |#endif
         |""".stripMargin
    Files.writeString(stage / "amd/hip/include/hip/hip_version.h", hipVersionHeader, StandardCharsets.UTF_8)
    Compress.extractTarGz(
      archive = cached("amd-hsa"),
      destination = stage / "amd/hsa",
      archiveRoot = s"ROCR-Runtime-rocm-$hsaVersion",
      select = relative =>
        relative == path"LICENSE.txt" || relative == path"runtime/hsa-runtime/LICENSE.md" ||
          relative.startsWith("libhsakmt/include") ||
          (relative.parent.contains(path"runtime/hsa-runtime/inc") &&
            relative.fileName.startsWith("hsa") && relative.fileName.endsWith(".h"))
    )
    Compress.extractZipRoot(
      archive = cached("opencl-headers"),
      destination = stage / "khronos/opencl",
      archiveRoot = s"OpenCL-Headers-$openCLHeadersVersion",
      select = relative => relative == path"LICENSE" || relative.startsWith("CL")
    )
    Compress.extractZipRoot(
      archive = cached("level-zero"),
      destination = stage / "oneapi/level-zero",
      archiveRoot = s"level-zero-$levelZeroVersion",
      select = relative =>
        relative == path"LICENSE" || relative.startsWith("include") || relative.startsWith("doc")
    )
    Compress.extractZipRoot(
      archive = cached("vulkan-headers"),
      destination = stage / "khronos/vulkan",
      archiveRoot = s"Vulkan-Headers-${vulkanHeadersVersion.stripPrefix("v")}",
      select = relative => relative == path"LICENSE.md" || relative.startsWith("include/vulkan")
    )
  },
  verify = output => {
    require(
      (output / "nvidia/cuda").descendants("cuda.h").exists(_.isFile),
      "NVIDIA CUDA Driver header is absent"
    )
    require(
      (output / "nvidia/cuda").descendants("cublas_api.h").exists(_.isFile),
      "NVIDIA cuBLAS header is absent"
    )
    require((output / "amd/hip/include/hip/hip_runtime_api.h").isFile, "HIP header is absent")
    require((output / "amd/hip/include/hip/hip_version.h").isFile, "HIP version header is absent")
    require((output / "amd/hsa/runtime/hsa-runtime/inc/hsa.h").isFile, "HSA core header is absent")
    require((output / "khronos/opencl/CL/cl.h").isFile, "OpenCL headers are absent")
    require((output / "oneapi/level-zero/include/ze_api.h").isFile, "Level Zero header is absent")
    require((output / "khronos/vulkan/include/vulkan/vulkan.h").isFile, "Vulkan header is absent")
  }
)

lazy val ptxReferenceBundle = ReferenceBundle(
  name = "ptx",
  recipeRevision = "1",
  artifacts = referenceArtifactsNamed("nvidia-ptx"),
  populate = (cached, stage) =>
    IO.copyFile(
      sourceFile = cached("nvidia-ptx"),
      targetFile = stage / "nvidia/ptx/index.html"
    ),
  verify = output => require((output / "nvidia/ptx/index.html").isFile, "NVIDIA PTX specification is absent")
)

lazy val amdgpuReferenceBundle = ReferenceBundle(
  name = "amdgpu",
  recipeRevision = "1",
  artifacts = referenceArtifactsNamed(
    "llvm",
    "mesa",
    "amd-xml"
  ),
  populate = (cached, stage) => {
    Compress.extractTarXz(
      archive = cached("llvm"),
      destination = stage / "llvm",
      archiveRoot = s"llvm-project-$llvmVersion.src",
      select = relative =>
        relative == path"llvm/LICENSE.TXT" || relative.startsWith("llvm/test/MC/AMDGPU")
    )
    Compress.extractTarXz(
      archive = cached("mesa"),
      destination = stage / "mesa",
      archiveRoot = s"mesa-$mesaVersion",
      select = relative =>
        relative == path"docs/license.rst" || relative == path"licenses/MIT" ||
          (relative.parent.contains(path"src/amd/compiler") && relative.fileName.startsWith("aco_"))
    )
    Compress.extractZip(
      archive = cached("amd-xml"),
      destination = stage / "amd/xml",
      select = relative =>
        relative.getNameCount == 1 && relative.fileName.matches("amdgpu_isa_[a-z0-9_]+\\.xml")
    )
  },
  verify = output => {
    require((output / "llvm/llvm/test/MC/AMDGPU").isDirectory, "LLVM AMDGPU MC tests are absent")
    require((output / "mesa/src/amd/compiler/aco_opcodes.py").isFile, "ACO opcodes are absent")
    require((output / "amd/xml/amdgpu_isa_rdna2.xml").isFile, "AMD RDNA2 XML is absent")
  }
)

lazy val spirvReferenceBundle = ReferenceBundle(
  name = "spirv",
  recipeRevision = "1",
  artifacts = referenceArtifactsNamed("spirv-headers", "spirv-tools"),
  populate = (cached, stage) => {
    Compress.extractZipRoot(
      archive = cached("spirv-headers"),
      destination = stage / "khronos/spirv-headers",
      archiveRoot = s"SPIRV-Headers-$spirvHeadersVersion",
      select = relative =>
        relative == path"LICENSE" ||
          relative.startsWith("LICENSES") || relative.startsWith("include/spirv/unified1")
    )
    Compress.extractZipRoot(
      archive = cached("spirv-tools"),
      destination = stage / "khronos/spirv-tools",
      archiveRoot = s"SPIRV-Tools-$spirvToolsVersion",
      select = relative =>
        relative == path"LICENSE" ||
          relative == path"test/binary_parse_test.cpp" || relative == path"test/binary_endianness_test.cpp" ||
          relative == path"test/val/val_opencl_test.cpp"
    )
  },
  verify = output => {
    require(
      (output / "khronos/spirv-headers/include/spirv/unified1/spirv.core.grammar.json").isFile,
      "SPIR-V grammar is absent"
    )
    require(
      (output / "khronos/spirv-tools/test/binary_parse_test.cpp").isFile,
      "SPIRV-Tools binary tests are absent"
    )
    require(
      (output / "khronos/spirv-headers/include/spirv/unified1/extinst.glsl.std.450.grammar.json").isFile,
      "GLSL.std.450 extended instruction grammar is absent"
    )
    require(
      (output / "khronos/spirv-headers/include/spirv/unified1/extinst.opencl.std.100.grammar.json").isFile,
      "OpenCL.std extended instruction grammar is absent"
    )
  }
)

ThisBuild / organization      := projectOrganization
ThisBuild / version           := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion      := scala3Version
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val isaReferenceCacheDirectory        = settingKey[Path]("Persistent cache for pinned ISA reference artifacts.")
lazy val checkedInGeneratedSourceDirectory =
  settingKey[Path]("Checked-in generated source root for a configuration.")
lazy val checkedInGeneratedResourceDirectory =
  settingKey[Path]("Checked-in generated resource root for a configuration.")
lazy val nativeBindingsDirectory     = settingKey[Path]("Checked-in generated native binding root.")
lazy val prepareAPIReferences        = taskKey[Path]("Prepare pinned API and native-header references.")
lazy val preparePTXReferences        = taskKey[Path]("Prepare the pinned PTX reference.")
lazy val prepareAMDGPUReferences     = taskKey[Path]("Prepare pinned AMDGPU references.")
lazy val prepareSPIRVReferences      = taskKey[Path]("Prepare pinned SPIR-V references.")
lazy val generateOfficialAPIManifest =
  taskKey[Path]("Extract normalized declarations from pinned official API headers.")
lazy val generateEnvironment  = taskKey[Unit]("Generate Scala and C++ environment constants from the build table.")
lazy val checkEnvironment     = taskKey[Unit]("Check environment output freshness and declared names.")
lazy val generateExecutionRPC =
  taskKey[Seq[Path]]("Generate the typed native execution RPC client.")
lazy val generatePTXSources =
  taskKey[Seq[Path]]("Update checked-in PTX sources and reference tests.")
lazy val generateAMDGPUSources =
  taskKey[Seq[Path]]("Update checked-in AMDGPU sources and oracle fixtures.")
lazy val generateAPISources =
  taskKey[Seq[Path]]("Bootstrap checked-in CUDA, HIP, HSA, Level Zero, OpenCL, and Vulkan API surfaces.")
lazy val generateSPIRVSources =
  taskKey[Seq[Path]]("Update checked-in SPIR-V sources and reference tests.")
lazy val checkGeneratedSources    = taskKey[Unit]("Verify every checked-in generated source and fixture is current.")
lazy val codegen                  = taskKey[Unit]("Update and format every checked-in generated source and fixture.")
lazy val nativeConfigureArguments =
  taskKey[Seq[String]]("CMake configure arguments for the native runtime.")
lazy val nativeSourceDirectories =
  settingKey[Seq[Path]]("C and C++ source trees governed by the native codestyle checks.")
lazy val nativeFormatCheck         = taskKey[Unit]("Verify hand-written C and C++ sources are clang-formatted.")
lazy val nativeCodestyleCheck      = taskKey[Unit]("Verify project-specific C and C++ source conventions.")
lazy val terminologyCheck          = taskKey[Unit]("Verify repository-authored sources use precise terminology.")
lazy val nativeArtifact            = taskKey[Path]("Built native runtime directory.")
lazy val conformanceExecutable     = taskKey[Path]("Built native conformance executable.")
lazy val generateBenchmarkFixtures = inputKey[Path]("Build selected benchmark fixtures in pinned toolchain containers.")
lazy val gemstoneRun               = inputKey[Unit]("Build Gemstone and run a command through selected compute APIs.")
lazy val emulate                   = inputKey[Unit]("Run conformance through Gemstone.")
lazy val cmakeExecutable           = settingKey[String]("CMake command used by native builds.")
lazy val ctestExecutable           = settingKey[String]("CTest command used by native tests.")
lazy val ninjaExecutable           = settingKey[String]("Ninja command used by CMake builds.")
lazy val clangExecutable           = settingKey[String]("Clang command used by API generation.")
lazy val clangFormatExecutable     = settingKey[String]("Clang-format command used by native codestyle tasks.")
lazy val pythonExecutable          = settingKey[String]("Python command used by AMDGPU generation.")
lazy val containerExecutable       = settingKey[String]("OCI container executable used by pinned toolchain builds.")
lazy val buildParallelism          = settingKey[Int]("Parallelism for CMake and Ninja builds.")
lazy val testParallelism           = settingKey[Int]("Parallelism for CTest runs.")
lazy val conformanceJobs           = settingKey[Int]("Concurrent conformance jobs for the emulated suite.")
lazy val conformanceArtifactParallelism =
  settingKey[Int]("Parallelism for conformance artifact builds.")
lazy val offlineMode = settingKey[Boolean]("Require cached dependencies and forbid network fetches.")

Global / isaReferenceCacheDirectory := (LocalRootProject / baseDirectory).value / "target" / "cache" / "isa-references"
Global / cmakeExecutable            := sys.env.getOrElse("CMAKE", "cmake")
Global / ctestExecutable            := sys.env.getOrElse("CTEST", "ctest")
Global / ninjaExecutable            := sys.env.getOrElse("NINJA", "ninja")
Global / clangExecutable            := sys.env.getOrElse("CLANG", "clang")
Global / clangFormatExecutable      := sys.env.getOrElse("CLANG_FORMAT", "clang-format")
Global / pythonExecutable           := sys.env.getOrElse("PYTHON", "python3")
Global / containerExecutable        := sys.env.getOrElse("CONTAINER_EXECUTABLE", "podman")
Global / buildParallelism           := positiveEnvironmentInt(
  "CMAKE_BUILD_PARALLEL_LEVEL",
  sys.runtime.availableProcessors()
)
Global / testParallelism := positiveEnvironmentInt(
  "CTEST_PARALLEL_LEVEL",
  sys.runtime.availableProcessors()
)
Global / conformanceJobs := positiveEnvironmentInt(
  "CONFORMANCE_JOBS",
  math.max(1, math.min(8, sys.runtime.availableProcessors() / 4))
)
Global / conformanceArtifactParallelism :=
  positiveEnvironmentInt(
    "CONFORMANCE_ARTIFACT_PARALLELISM",
    math.max(1, math.min(4, sys.runtime.availableProcessors() / 8))
  )
Global / offlineMode := environmentFlag(environmentName("OFFLINE"))

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-no-indent",
    "-deprecation",
    "-Wconf:cat=unchecked:error",
    "-Wconf:name=MatchCaseUnreachable:error",
    "-Wconf:name=PatternMatchExhaustivity:error",
    "-Wnonunit-statement",
    "-Wvalue-discard",
    "-Yretain-trees",
    "-language:noAutoTupling",
    "-source",
    "3.9"
  ),
  scalacOptions += "-Wunused:all",
  Compile / scalafixConfig               := Some(file(".scalafix.conf")),
  Test / scalafixConfig                  := Some(file(".scalafix.conf")),
  libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
  Test / testOptions += Tests.Argument(TestFrameworks.MUnit, "--log=warn", "--logger=sbt"),
  // Directory-backed inter-project classpaths; sbt 2's exported JAR cache retains stale packages.
  Compile / exportJars  := false,
  scalafmtDetailedError := true,
  scalafmtFailOnErrors  := true
)

lazy val nativeTestSettings = Seq(
  Test / nativeConfig ~= (_.withMode(scala.scalanative.build.Mode.debug)),
  Test / nativeLink / logLevel           := Level.Warn,
  Test / loadedTestFrameworks / logLevel := Level.Warn
)

// Checked-in generator output is a persistent build input, not an sbt-managed
// output. Keep it outside target/sourceManaged and expose it as an additional
// unmanaged root so clean cannot remove it.
def checkedInGeneratedSources = Seq(
  Compile / checkedInGeneratedSourceDirectory := projectMatrixBaseDirectory.value.absolute / "src/generated/main/scala",
  Test / checkedInGeneratedSourceDirectory    := projectMatrixBaseDirectory.value.absolute / "src/generated/test/scala",
  Compile / checkedInGeneratedResourceDirectory := projectMatrixBaseDirectory.value.absolute / "src/generated/main/resources",
  Test / checkedInGeneratedResourceDirectory := projectMatrixBaseDirectory.value.absolute / "src/generated/test/resources",
  Compile / unmanagedSourceDirectories += (Compile / checkedInGeneratedSourceDirectory).value,
  Test / unmanagedSourceDirectories += (Test / checkedInGeneratedSourceDirectory).value,
  Compile / unmanagedResourceDirectories += (Compile / checkedInGeneratedResourceDirectory).value,
  Test / unmanagedResourceDirectories += (Test / checkedInGeneratedResourceDirectory).value
)

lazy val generateNativeBindings =
  taskKey[Seq[Path]]("Generate native ABI declarations and implementation checks for every platform.")

lazy val codestyleRules = project.in(file("codestyle/rules"))
  .disablePlugins(Stryker4sPlugin)
  .disablePlugins(_root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    name           := "gemstone-codestyle-rules",
    publish / skip := true,
    libraryDependencies +=
      ("ch.epfl.scala" %% "scalafix-core" % _root_.scalafix.sbt.BuildInfo.scalafixVersion)
        .cross(CrossVersion.for3Use2_13)
  )

lazy val codestyleInput = project.in(file("codestyle/input"))
  .disablePlugins(Stryker4sPlugin)
  .disablePlugins(_root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    publish / skip                         := true,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion,
    semanticdbEnabled                      := true
  )

lazy val codestyleOutput = project.in(file("codestyle/output"))
  .disablePlugins(Stryker4sPlugin)
  .disablePlugins(_root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    publish / skip                         := true,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion
  )

lazy val codestyleTests = project.in(file("codestyle/tests"))
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(codestyleInput, codestyleRules)
  .dependsOn(codestyleRules % ScalafixConfig)
  .enablePlugins(_root_.scalafix.sbt.ScalafixTestkitPlugin)
  .settings(
    publish / skip := true,
    scalacOptions += "-Wunused:all",
    scalafixTestkitOutputSourceDirectories := (codestyleOutput / Compile / sourceDirectories).value,
    scalafixTestkitInputSourceDirectories  := (codestyleInput / Compile / sourceDirectories).value,
    scalafixTestkitInputClasspath          := (codestyleInput / Compile / fullClasspath).value,
    scalafixTestkitInputScalacOptions      := (codestyleInput / Compile / scalacOptions).value,
    scalafixTestkitInputScalaVersion       := (codestyleInput / Compile / scalaVersion).value
  )

lazy val codestyle = project.in(file("codestyle"))
  .disablePlugins(Stryker4sPlugin)
  .aggregate(codestyleRules, codestyleInput, codestyleOutput, codestyleTests)
  .disablePlugins(_root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    name           := "gemstone-codestyle",
    publish / skip := true
  )

// No dependency on core: its own tests can use these utilities without a cycle.
lazy val testSupport = projectMatrix.in(file("tools/test-support"))
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(commonSettings, name := "gemstone-test-support", publish / skip := true)
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)

lazy val api = projectMatrix
  .dependsOn(testSupport % "test->compile")
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(core)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources,
    name := "gemstone-api",
    libraryDependencies ++= Seq(
    )
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)

lazy val codegenProject = Project("codegen", file("tools/codegen"))
  .dependsOn(testSupport.jvm(scala3Version) % "test->compile")
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(core.jvm(scala3Version))
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    name                    := "gemstone-codegen",
    publish / skip          := true,
    Test / baseDirectory    := (LocalRootProject / baseDirectory).value,
    nativeBindingsDirectory := (LocalRootProject / baseDirectory).value / "native/generated",
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % jsoupVersion,

      "com.monovore"  %% "decline"       % declineVersion,
      "org.scalameta" %% "scalafmt-core" % scalafmtVersion,
      "org.typelevel" %% "cats-core"     % catsCoreVersion
    ),
    prepareAPIReferences := Def.uncached {
      apiReferenceBundle.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value / "isa-references",
        offline = (Global / offlineMode).value,
        log = streams.value.log.info
      )
    },
    preparePTXReferences := Def.uncached {
      ptxReferenceBundle.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value / "isa-references",
        offline = (Global / offlineMode).value,
        log = streams.value.log.info
      )
    },
    prepareAMDGPUReferences := Def.uncached {
      amdgpuReferenceBundle.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value / "isa-references",
        offline = (Global / offlineMode).value,
        log = streams.value.log.info
      )
    },
    prepareSPIRVReferences := Def.uncached {
      spirvReferenceBundle.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value / "isa-references",
        offline = (Global / offlineMode).value,
        log = streams.value.log.info
      )
    },
    generateOfficialAPIManifest := Def.uncached {
      val converter  = fileConverter.value
      val references = prepareAPIReferences.value
      val output     = target.value / "official-api-headers.json"
      val clang      = (Global / clangExecutable).value
      (Compile / runner).value.run(
        s"$rootPackage.codegen.GenerateOfficialAPIManifest",
        (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data)),
        Seq("--clang", clang, s"$references", s"$output"),
        streams.value.log
      ).get
      require(output.isFile, s"API manifest generator did not produce $output")
      output
    },
    generateAPISources := Def.uncached {
      val converter = fileConverter.value
      val manifest  = generateOfficialAPIManifest.value
      val output    = (api.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value
      (Compile / runner).value.run(
        s"$rootPackage.codegen.GenerateAPISurfaces",
        (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data)),
        Seq(
          s"$manifest",
          s"$output",
          cudaAPIVersion,
          hipVersion,
          hsaVersion,
          levelZeroVersion,
          openCLHeadersVersion,
          vulkanHeadersVersion
        ),
        streams.value.log
      ).get
      val outputs = output.descendants("*.scala").filter(_.isFile)
      require(outputs.nonEmpty, s"API generator did not produce Scala sources under $output")
      outputs
    },
    generateNativeBindings := Def.uncached {
      val converter = fileConverter.value
      val output    = nativeBindingsDirectory.value
      val manifest  = generateOfficialAPIManifest.value
      (Compile / runner).value.run(
        s"$rootPackage.codegen.GenerateNativeBindings",
        (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data)),
        Seq(s"$output", s"$manifest"),
        streams.value.log
      ).get
      val outputs = output.descendants("*").filter(_.isFile)
      require(outputs.nonEmpty, s"native binding generator did not produce files under $output")
      outputs
    },
    generateEnvironment / logLevel := Level.Warn,
    checkEnvironment / logLevel    := Level.Warn,
    generateEnvironment            := Def.uncached {
      val converter = fileConverter.value
      (Compile / runner).value.run(
        s"$rootPackage.codegen.GenerateEnvironment",
        (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data)),
        Seq((LocalRootProject / baseDirectory).value.toString, "--write") ++ environmentArguments,
        streams.value.log
      ).get
    },
    checkEnvironment := Def.uncached {
      val converter = fileConverter.value
      (Compile / runner).value.run(
        s"$rootPackage.codegen.GenerateEnvironment",
        (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data)),
        Seq((LocalRootProject / baseDirectory).value.toString, "--check") ++ environmentArguments,
        streams.value.log
      ).get
    },
    generateExecutionRPC := Def.uncached {
      val converter = fileConverter.value
      val output    = nativeBindingsDirectory.value / "rpc"
      (Compile / runner).value.run(
        s"$rootPackage.codegen.GenerateExecutionRPC",
        (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data)),
        Seq(s"$output"),
        streams.value.log
      ).get
      val outputs = output.descendants("*").filter(_.isFile)
      require(outputs.nonEmpty, s"execution RPC generator did not produce files under $output")
      outputs
    }
  )

lazy val mutationSettings = Mutation.settings ++ Seq(
  strykerConcurrency     := 1,
  strykerReporters       := Seq("console", "html", "json"),
  strykerThresholdsBreak := 0,
  strykerOpenReport      := false
)

lazy val core = projectMatrix
  .dependsOn(testSupport % "test->compile")
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources,
    name := "gemstone-core",
    libraryDependencies ++= Seq(
      "com.lihaoyi"   %% "upickle"   % upickleVersion,
      "org.typelevel" %% "cats-core" % catsCoreVersion
    )
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = project =>
      project.enablePlugins(Stryker4sPlugin).settings(
        mutationSettings,
        // Focus on authored boundaries; generated code and compiler macros need separate analysis.
        strykerMutate := Seq(
          "src/main/scala/net/kurobako/gemstone/binary/ByteRange.scala",
          "src/main/scala/net/kurobako/gemstone/memory/WrittenRanges.scala",
          "src/main/scala/net/kurobako/gemstone/rpc/Wire.scala"
        ),
        strykerTestFilter := Seq(
          "net.kurobako.gemstone.binary.BinarySuite",
          "net.kurobako.gemstone.memory.WrittenRangesSuite",
          "net.kurobako.gemstone.rpc.*"
        )
      )
  )
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)

lazy val externalCodestyleSettings = Seq(
  nativeSourceDirectories := Seq(baseDirectory.value.toPath),
  format                  := Def.uncached {
    val sources = Sources.native(nativeSourceDirectories.value, exclude = containsPathSegments(_, "generated"))
    ClangFormat.format(
      files = sources,
      executable = (Global / clangFormatExecutable).value,
      workingDirectory = baseDirectory.value.toPath,
      log = streams.value.log.info
    )
  },
  nativeFormatCheck := Def.uncached {
    val sources   = Sources.native(nativeSourceDirectories.value, exclude = containsPathSegments(_, "generated"))
    val formatted = ClangFormat.check(
      files = sources,
      executable = (Global / clangFormatExecutable).value,
      workingDirectory = baseDirectory.value.toPath,
      log = streams.value.log.info
    )
    require(formatted, s"sources are not clang-formatted; run ${thisProject.value.id}/format")
  },
  nativeCodestyleCheck := Def.uncached {
    val forbiddenTypes =
      raw"\bstd::(?:size_t|ptrdiff_t|max_align_t|u?int(?:8|16|32|64)_t|u?int(?:ptr|max)_t|u?int_(?:least|fast)(?:8|16|32|64)_t)\b".r
    val integerConstants = raw"\bU?INT(?:8|16|32|64|MAX|PTR)_C\b".r
    val rules            = Seq(
      Codestyle.forbidden(forbiddenTypes, value => s"forbidden numeric type '$value'"),
      Codestyle.forbidden(integerConstants, value => s"forbidden integer constant macro '$value'; use static_cast"),
      Codestyle.testCaseSpacing,
      Codestyle.includeBlockSpacing
    )
    val violations = Codestyle.scan(
      Sources.native(nativeSourceDirectories.value, exclude = containsPathSegments(_, "generated")),
      rules,
      baseDirectory.value.toPath
    )
    violations.foreach(violation => streams.value.log.error(violation.render))
    require(violations.isEmpty, s"found ${violations.size} native codestyle violations")
  },
  lint := Def.sequential(nativeFormatCheck, nativeCodestyleCheck).value
)

lazy val nativeRuntimeSettings = Seq(
  nativeConfigureArguments := Def.uncached {
    val references      = (codegenProject / prepareAPIReferences).value
    val matchingHeaders = (references / "nvidia/cuda").descendants("cuda.h").filter { path =>
      containsPathSegments(path, s"cuda-$cudaAPIVersion", "targets")
    }
    require(matchingHeaders.size == 1, s"expected one pinned CUDA header, found ${matchingHeaders.size}")
    val cudaInclude =
      matchingHeaders.head.parent.getOrElse(
        throw MessageOnlyException(s"CUDA header has no parent: ${matchingHeaders.head}")
      )
    Seq(
      "-G",
      "Ninja",
      s"-DCMAKE_MAKE_PROGRAM=${(Global / ninjaExecutable).value}",
      "-DBUILD_TESTING=ON",
      s"-DCONTAINER_EXECUTABLE=${(Global / containerExecutable).value}",
      s"-DROCM_VERSION=$rocmVersion",
      s"-DCMAKE_BUILD_TYPE=${sys.env.getOrElse("CMAKE_BUILD_TYPE", "Release")}",
      s"-DCUDA_INCLUDE_DIR=$cudaInclude",
      s"-DHIP_INCLUDE_DIR=${references / "amd/hip/include"}",
      s"-DHSA_INCLUDE_DIR=${references / "amd/hsa/runtime/hsa-runtime/inc"}",
      s"-DHSAKMT_INCLUDE_DIR=${references / "amd/hsa/libhsakmt/include"}",
      s"-DLEVEL_ZERO_INCLUDE_DIR=${references / "oneapi/level-zero/include"}",
      s"-DOPENCL_INCLUDE_DIR=${references / "khronos/opencl"}",
      s"-DVULKAN_INCLUDE_DIR=${references / "khronos/vulkan/include"}",
      s"-DLEVEL_ZERO_SOURCE_URL=${referenceArtifact("level-zero").url}",
      s"-DLEVEL_ZERO_SOURCE_SHA256=${referenceArtifact("level-zero").expectedSha256}",
      s"-DVULKAN_HEADERS_SOURCE_URL=${referenceArtifact("vulkan-headers").url}",
      s"-DVULKAN_HEADERS_SOURCE_SHA256=${referenceArtifact("vulkan-headers").expectedSha256}",
      s"-DOPENCL_HEADERS_SOURCE_URL=${referenceArtifact("opencl-headers").url}",
      s"-DOPENCL_HEADERS_SOURCE_SHA256=${referenceArtifact("opencl-headers").expectedSha256}",
      s"-DROCR_HEADERS_SOURCE_URL=${referenceArtifact("amd-hsa").url}",
      s"-DROCR_HEADERS_SOURCE_SHA256=${referenceArtifact("amd-hsa").expectedSha256}"
    )
  },
  nativeArtifact := Def.uncached {
    val root        = (LocalRootProject / baseDirectory).value.absolute
    val parallelism = (Global / buildParallelism).value
    CMake.build(
      source = baseDirectory.value.toPath,
      build = root / "target/native/build",
      cmake = (Global / cmakeExecutable).value,
      arguments = nativeConfigureArguments.value,
      parallelism = parallelism,
      log = streams.value.log.info
    )
  },
  build := {
    val arguments = Def.spaceDelimited("").parsed
    if (arguments.nonEmpty) throw MessageOnlyException("usage: native/build")
    nativeArtifact.value
  },
  Test / test := {
    val arguments = Def.spaceDelimited("").parsed
    if (arguments.nonEmpty) throw MessageOnlyException("usage: native/test")
    val parallelism = (Global / testParallelism).value
    CMake.test(
      build = nativeArtifact.value,
      ctest = (Global / ctestExecutable).value,
      parallelism = parallelism,
      log = streams.value.log.info
    )
    sbt.protocol.testing.TestResult.Passed
  }
)

lazy val native = project.in(file("native"))
  .disablePlugins(Stryker4sPlugin)
  .enablePlugins(ExternalProjectPlugin)
  .disablePlugins(plugins.JvmPlugin, plugins.IvyPlugin)
  .settings(
    externalCodestyleSettings,
    nativeRuntimeSettings,
    nativeSourceDirectories += (LocalRootProject / baseDirectory).value.toPath / "server/src/main/resources/scala-native",
    name       := "gemstone-native",
    cleanFiles := Seq((LocalRootProject / baseDirectory).value / "target/native")
  )

lazy val ptx = projectMatrix
  .dependsOn(testSupport % "test->compile")
  .disablePlugins(Stryker4sPlugin)
  .in(file("isa/ptx"))
  .dependsOn(core)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources,
    name := "gemstone-isa-ptx",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"  % catsCoreVersion,
      "org.typelevel" %% "cats-parse" % catsParseVersion
    ),
    generatePTXSources := Def.uncached {
      val converter  = fileConverter.value
      val references = (codegenProject / preparePTXReferences).value
      val input      = references / "nvidia/ptx/index.html"
      val sourceRoot = (Compile / checkedInGeneratedSourceDirectory).value
      val testRoot   = (Test / checkedInGeneratedSourceDirectory).value
      val classpath  = (codegenProject / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data))
      val run        = (codegenProject / Compile / runner).value
      val log        = streams.value.log
      run.run(
        s"$rootPackage.codegen.GeneratePTXISA",
        classpath,
        Seq(
          "instructions",
          s"$input",
          s"$sourceRoot",
          ptxISAVersion,
          ptxISAPackage
        ),
        log
      ).get
      run.run(
        s"$rootPackage.codegen.GeneratePTXISA",
        classpath,
        Seq(
          "tests",
          s"$input",
          s"$testRoot",
          ptxISAVersion,
          ptxISAPackage
        ),
        log
      ).get
      val outputs = (sourceRoot.descendants("*.scala") ++ testRoot.descendants("*.scala")).filter(_.isFile)
      require(outputs.nonEmpty, "PTX generator outputs are incomplete")
      outputs
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)

lazy val amdgpu = projectMatrix
  .dependsOn(testSupport % "test->compile")
  .disablePlugins(Stryker4sPlugin)
  .in(file("isa/amdgpu"))
  .dependsOn(core)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources,
    name := "gemstone-isa-amdgpu",
    libraryDependencies ++= Seq(
      "com.outr"      %% "scribe"     % scribeVersion,
      "org.typelevel" %% "cats-core"  % catsCoreVersion,
      "org.typelevel" %% "cats-parse" % catsParseVersion
    ),
    generateAMDGPUSources := Def.uncached {
      val converter    = fileConverter.value
      val references   = (codegenProject / prepareAMDGPUReferences).value
      val python       = (Global / pythonExecutable).value
      val sourceRoot   = (Compile / checkedInGeneratedSourceDirectory).value
      val metadataRoot = (Compile / checkedInGeneratedResourceDirectory).value
      val resourceRoot = (Test / checkedInGeneratedResourceDirectory).value
      val classpath    = (codegenProject / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data))
      val run          = (codegenProject / Compile / runner).value
      val log          = streams.value.log
      run.run(
        s"$rootPackage.codegen.GenerateAMDGPUISA",
        classpath,
        Seq(
          "--python",
          python,
          s"$references",
          s"$sourceRoot",
          s"$metadataRoot"
        ),
        log
      ).get
      run.run(
        s"$rootPackage.codegen.GenerateAMDGPUOracle",
        classpath,
        Seq(
          s"$references",
          s"$resourceRoot"
        ),
        log
      ).get
      val sources       = sourceRoot.descendants("*.scala")
      val metadataFiles = metadataRoot.descendants("*.json")
      val oracleFiles   = resourceRoot.descendants("*").filter(_.isFile)
      require(
        sources.nonEmpty && metadataFiles.nonEmpty && oracleFiles.nonEmpty &&
          sources.forall(_.isFile) && metadataFiles.forall(_.isFile) && oracleFiles.forall(_.isFile),
        "AMDGPU generator outputs are incomplete"
      )
      sources ++ metadataFiles ++ oracleFiles
    }
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = project =>
      project.enablePlugins(Stryker4sPlugin).settings(
        mutationSettings,
        strykerMutate     := Seq("src/main/scala/net/kurobako/gemstone/isa/amdgpu/Parser.scala"),
        strykerTestFilter := Seq("net.kurobako.gemstone.isa.amdgpu.ParserSuite")
      )
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = nativeTestSettings ++ Seq(
      Compile / nativeConfig ~= (_.withEmbedResources(true)),
      Test / nativeConfig ~= (_.withEmbedResources(true))
    )
  )

lazy val spirv = projectMatrix
  .dependsOn(testSupport % "test->compile")
  .disablePlugins(Stryker4sPlugin)
  .in(file("isa/spirv"))
  .dependsOn(core)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources,
    name := "gemstone-isa-spirv",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsCoreVersion
    ),
    generateSPIRVSources := Def.uncached {
      val converter  = fileConverter.value
      val references = (codegenProject / prepareSPIRVReferences).value
      val unified    = references / "khronos/spirv-headers/include/spirv/unified1"
      val grammar    = unified / "spirv.core.grammar.json"
      val sourceRoot = (Compile / checkedInGeneratedSourceDirectory).value
      val testRoot   = (Test / checkedInGeneratedSourceDirectory).value
      val tests      = references / "khronos/spirv-tools/test"
      val classpath  = (codegenProject / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data))
      val run        = (codegenProject / Compile / runner).value
      val log        = streams.value.log
      run.run(
        s"$rootPackage.codegen.GenerateSPIRVISA",
        classpath,
        Seq(
          "instructions",
          s"$grammar",
          s"$unified",
          s"$sourceRoot",
          spirvPackage
        ),
        log
      ).get
      run.run(
        s"$rootPackage.codegen.GenerateSPIRVISA",
        classpath,
        Seq("tests", s"$grammar", s"$tests", s"$testRoot", spirvPackage),
        log
      ).get
      val outputs = (sourceRoot.descendants("*.scala") ++ testRoot.descendants("*.scala")).filter(_.isFile)
      require(outputs.nonEmpty, "SPIR-V generator outputs are incomplete")
      outputs
    }
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = project =>
      project.enablePlugins(Stryker4sPlugin).settings(
        mutationSettings,
        strykerMutate := Seq(
          s"src/main/scala/${rootPackage.replace('.', '/')}/isa/spirv/$spirvPackage/Binary.scala"
        ),
        strykerTestFilter := Seq(
          s"$rootPackage.isa.spirv.$spirvPackage.ReferenceSuite",
          s"$rootPackage.isa.spirv.$spirvPackage.BinarySuite"
        )
      )
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = nativeTestSettings ++ Seq(Test / nativeConfig ~= (_.withEmbedResources(true)))
  )

lazy val server = projectMatrix
  .dependsOn(testSupport % "test->compile")
  .disablePlugins(Stryker4sPlugin)
  .in(file("server"))
  .dependsOn(core, ptx, amdgpu, spirv)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    name := "gemstone-server",
    Test / envVars += (environmentName("NATIVE_DIRECTORY") -> s"${(native / nativeArtifact).value}"),
    libraryDependencies ++= Seq(
      "com.monovore"  %% "decline"   % declineVersion,
      "com.outr"      %% "scribe"    % scribeVersion,
      "org.typelevel" %% "cats-core" % catsCoreVersion
    ),
    Compile / mainClass := Some(s"$rootPackage.server.CLI")
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = project =>
      project.settings(
        Compile / run / fork := true,
        Test / fork          := true,
        Test / javaOptions += Def.uncached {
          val converter = fileConverter.value
          val classpath = (Runtime / fullClasspath).value.map(entry => converter.toPath(entry.data))
          s"-Dgemstone.cli.classpath=${classpath.mkString(java.io.File.pathSeparator)}"
        }
      )
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = nativeTestSettings ++ Seq(
      // sbt 2 exposes resource directories separately; package them once for Native's C-source discovery.
      Compile / exportJars := true,
      Compile / nativeConfig ~= (_.withMode(scala.scalanative.build.Mode.releaseFast).withEmbedResources(true)),
      Test / nativeConfig ~= (_.withEmbedResources(true))
    )
  )

lazy val benchmarks = projectMatrix.in(file("tools/benchmarks"))
  .dependsOn(testSupport % "test->compile")
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(core, ptx, amdgpu, spirv, server)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    name           := "gemstone-benchmarks",
    publish / skip := true,
    libraryDependencies ++= Seq(
      "com.monovore"  %% "decline"   % declineVersion,
      "org.typelevel" %% "cats-core" % catsCoreVersion
    ),
    Compile / mainClass       := Some(s"$rootPackage.benchmarks.BenchmarkRunner"),
    Compile / run / fork      := true,
    generateBenchmarkFixtures := {
      val arguments = Def.spaceDelimited("[cuda|hip|hsa|opencl|all]").parsed
      if (arguments.size > 1)
        throw MessageOnlyException("usage: benchmarks/generateBenchmarkFixtures [cuda|hip|hsa|opencl|all]")
      val selected = arguments.headOption.getOrElse("all")
      if (!Set("cuda", "hip", "hsa", "opencl", "all").contains(selected))
        throw MessageOnlyException(s"unknown benchmark fixture target: $selected")
      val root   = (LocalRootProject / baseDirectory).value.absolute
      val source = root / "tools/benchmarks/fixtures"
      val build  = root / "target/benchmarks/fixtures"
      val output = root / "tools/benchmarks/target/generated-fixtures"
      CMake.build(
        source = source,
        build = build,
        cmake = (Global / cmakeExecutable).value,
        arguments = Seq(
          "-G",
          "Ninja",
          s"-DCMAKE_MAKE_PROGRAM=${(Global / ninjaExecutable).value}",
          s"-DCONTAINER_EXECUTABLE=${(Global / containerExecutable).value}",
          s"-DROCM_VERSION=$rocmVersion",
          s"-DBENCHMARK_FIXTURE_OUTPUT_DIRECTORY=$output"
        ),
        parallelism = (Global / buildParallelism).value,
        log = streams.value.log.info,
        target = Some(if (selected == "all") "fixtures" else selected)
      )
      output
    }
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = project =>
      project.settings(
        Compile / run / javaOptions ++= Seq("-Xms512m", "-Xmx4g")
      )
  )
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)

lazy val conformance = project.in(file("conformance"))
  .disablePlugins(Stryker4sPlugin)
  .enablePlugins(ExternalProjectPlugin)
  .disablePlugins(plugins.JvmPlugin, plugins.IvyPlugin)
  .settings(
    externalCodestyleSettings,
    name  := "gemstone-conformance",
    build := {
      val arguments = Def.spaceDelimited("[target-set]").parsed
      if (arguments.size > 1)
        throw MessageOnlyException("usage: conformance/build [target-set]")
      val targetSet = arguments.headOption.getOrElse("default")
      if (!targetSet.matches("[A-Za-z0-9_-]+"))
        throw MessageOnlyException(s"invalid conformance target set: $targetSet")
      val root                = (LocalRootProject / baseDirectory).value.absolute
      val build               = root / s"target/conformance/build/$targetSet"
      val artifacts           = root / "target/conformance/artifacts"
      val log                 = streams.value.log
      val offline             = (Global / offlineMode).value
      val artifactParallelism = (Global / conformanceArtifactParallelism).value
      CMake.build(
        source = baseDirectory.value / "artifacts",
        build = build,
        cmake = (Global / cmakeExecutable).value,
        arguments = Seq(
          "-G",
          "Ninja",
          s"-DCMAKE_MAKE_PROGRAM=${(Global / ninjaExecutable).value}",
          s"-DCONTAINER_EXECUTABLE=${(Global / containerExecutable).value}",
          s"-DROCM_VERSION=$rocmVersion",
          s"-DCONFORMANCE_TARGET_SET=$targetSet",
          s"-DCONFORMANCE_ARTIFACT_DIRECTORY=$artifacts",
          s"-DCONFORMANCE_DEPENDENCY_DIRECTORY=$root/target/conformance/deps",
          s"-DCONFORMANCE_WORK_DIRECTORY=$root/target/conformance/work",
          s"-DCONFORMANCE_ARTIFACT_PARALLELISM=$artifactParallelism",
          s"-DCONFORMANCE_OFFLINE=${if (offline) "ON" else "OFF"}"
        ),
        parallelism = (Global / buildParallelism).value,
        log = log.info,
        target = Some("artifacts")
      )
      artifacts
    },
    conformanceExecutable := Def.uncached {
      val root  = (LocalRootProject / baseDirectory).value.absolute
      val build = root / "target/conformance/executable"
      val log   = streams.value.log
      CMake.build(
        source = baseDirectory.value.toPath,
        build = build,
        cmake = (Global / cmakeExecutable).value,
        arguments = Seq(
          "-G",
          "Ninja",
          s"-DCMAKE_MAKE_PROGRAM=${(Global / ninjaExecutable).value}",
          "-DCMAKE_BUILD_TYPE=Release",
          "-DBUILD_TESTING=OFF",
          "-DCONFORMANCE_BACKEND=",
          "-DCONFORMANCE_BUILD_UNIFIED=OFF",
          "-DCONFORMANCE_BUILD_CTS=OFF",
          "-DCONFORMANCE_BUILD_MINIAPPS=OFF",
          s"-DCONFORMANCE_DEPENDENCY_CACHE=$root/target/conformance/deps"
        ),
        parallelism = (Global / buildParallelism).value,
        log = log.info,
        target = Some("conformance")
      )
      val result = build / "bin" / "conformance"
      require(result.isFile, s"conformance executable was not produced: $result")
      result
    },
    Test / test := {
      val arguments = Def.spaceDelimited("").parsed
      if (arguments.nonEmpty) throw MessageOnlyException("usage: conformance/test")
      val root  = (LocalRootProject / baseDirectory).value.absolute
      val build = root / "target/conformance/test"
      val log   = streams.value.log
      CMake.build(
        source = baseDirectory.value.toPath,
        build = build,
        cmake = (Global / cmakeExecutable).value,
        arguments = Seq(
          "-G",
          "Ninja",
          s"-DCMAKE_MAKE_PROGRAM=${(Global / ninjaExecutable).value}",
          "-DCMAKE_BUILD_TYPE=Release",
          "-DCONFORMANCE_BACKEND=",
          "-DCONFORMANCE_BUILD_UNIFIED=OFF",
          "-DCONFORMANCE_BUILD_CTS=OFF",
          "-DCONFORMANCE_BUILD_MINIAPPS=OFF",
          s"-DCONFORMANCE_DEPENDENCY_CACHE=$root/target/conformance/deps"
        ),
        parallelism = (Global / buildParallelism).value,
        log = log.info,
        target = Some("conformance_unit_tests")
      )
      CMake.test(
        build = build,
        ctest = (Global / ctestExecutable).value,
        parallelism = (Global / testParallelism).value,
        log = log.info
      )
      sbt.protocol.testing.TestResult.Passed
    },
    Compile / run := {
      val root      = (LocalRootProject / baseDirectory).value.absolute
      val arguments = Def.spaceDelimited("[run option|target ...]").parsed
      val log       = streams.value.log
      runConformance(
        root = root,
        artifacts = root / "target/conformance/artifacts",
        executable = conformanceExecutable.value,
        ctest = (Global / ctestExecutable).value,
        arguments = arguments,
        output = root / "target/conformance/direct",
        launcher = Seq.empty,
        environment = Seq.empty,
        log = log.info,
        error = log.error
      )
    },
    emulate := {
      val root            = (LocalRootProject / baseDirectory).value.absolute
      val arguments       = Def.spaceDelimited("[run option|target ...]").parsed
      val nativeDirectory = (native / nativeArtifact).value.absolute
      val converter       = fileConverter.value
      val log             = streams.value.log
      val jobs            = (Global / conformanceJobs).value
      val workers         = positiveEnvironmentInt(
        environmentName("EXECUTION_PARALLELISM"),
        math.max(1, math.min(64, sys.runtime.availableProcessors()) - 2)
      )
      // Stay below the compressed-reference limit; 32 GiB disables it on the supported JVM.
      val serverHeap  = s"${math.max(8L, math.min(28L, jobs.toLong * 6L))}g"
      val environment = Map(
        "CONFORMANCE_JOBS"                            -> jobs.toString,
        environmentName("EXECUTION_PARALLELISM")      -> workers.toString,
        environmentName("NATIVE_DIRECTORY")           -> s"$nativeDirectory",
        environmentName("EXECUTION_MAX_INSTRUCTIONS") -> sys.env.getOrElse(
          environmentName("EXECUTION_MAX_INSTRUCTIONS"),
          conformanceMaxInstructions
        )
      )
      val mainClass = s"$rootPackage.server.CLI"
      val classpath = (server.jvm(scala3Version) / Runtime / fullClasspath).value
        .map(entry => converter.toPath(entry.data))
      val baseForkOptions = (server.jvm(scala3Version) / Compile / run / forkOptions).value
      val serverOptions   = baseForkOptions
        .withWorkingDirectory(root.toFile)
        .withRunJVMOptions(baseForkOptions.runJVMOptions ++ Seq("-Xms512m", s"-Xmx$serverHeap"))
        .withEnvVars(baseForkOptions.envVars ++ environment)
      val clientOptions = baseForkOptions
        .withWorkingDirectory(root.toFile)
        .withRunJVMOptions(
          baseForkOptions.runJVMOptions ++ Seq("-Xms32m", "-Xmx256m", "-XX:+UseSerialGC", "-XX:ActiveProcessorCount=1")
        )
        .withEnvVars(baseForkOptions.envVars ++ environment)
      val javaHome        = serverOptions.javaHome.map(_.toPath).getOrElse(path"${sys.props("java.home")}")
      val java            = javaHome / "bin" / "java"
      val launcherCommand = Vector(s"$java") ++ clientOptions.runJVMOptions ++
        Vector("-cp", classpath.mkString(sys.props("path.separator")), mainClass)
      withGemstoneServer(
        root = root,
        launchServer = endpointFile =>
          ForkRun(serverOptions).fork(mainClass, classpath, Seq("serve", "--endpoint-file", s"$endpointFile"), log),
        shutdownServer = endpoint =>
          ForkRun(clientOptions).run(mainClass, classpath, Seq("shutdown", "--server", endpoint), log).isSuccess,
        log = log
      ) { endpoint =>
        withConformanceLauncher(root, launcherCommand ++ Seq("run", "--server", endpoint), environment.toSeq) {
          launcher =>
            runConformance(
              root = root,
              artifacts = root / "target/conformance/artifacts",
              executable = conformanceExecutable.value,
              ctest = (Global / ctestExecutable).value,
              arguments = arguments :+ "--isolated-launcher",
              output = root / "target/conformance/emulated",
              launcher = launcher,
              environment = environment.toSeq,
              log = log.info,
              error = log.error
            )
        }
      }
    },
    cleanFiles := Seq((LocalRootProject / baseDirectory).value / "target/conformance")
  )

lazy val gemstone = project.in(file("."))
  .disablePlugins(Stryker4sPlugin)
  .aggregate(testSupport.projectRefs *)
  .aggregate(api.projectRefs *)
  .aggregate(codegenProject)
  .aggregate(core.projectRefs *)
  .aggregate(server.projectRefs *)
  .aggregate(ptx.projectRefs *)
  .aggregate(amdgpu.projectRefs *)
  .aggregate(spirv.projectRefs *)
  .aggregate(benchmarks.projectRefs *)
  .aggregate(codestyle)
  .aggregate(native, conformance)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    name           := "gemstone",
    publish / skip := true,
    commands ++= Coverage.commands(
      full = "coverageJvm",
      focused = "coverageOnly",
      title = "Gemstone JVM",
      entries = Seq(
        Coverage.Entry(core.jvm(scala3Version).id),
        Coverage.Entry(
          amdgpu.jvm(scala3Version).id,
          generated = Seq((amdgpu.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value.toFile)
        ),
        Coverage.Entry(
          ptx.jvm(scala3Version).id,
          generated = Seq((ptx.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value.toFile)
        ),
        Coverage.Entry(
          spirv.jvm(scala3Version).id,
          generated = Seq((spirv.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value.toFile)
        ),
        Coverage.Entry(server.jvm(scala3Version).id),
        Coverage.Entry(
          api.jvm(scala3Version).id,
          tests = false,
          generated = Seq((api.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value.toFile)
        ),
        Coverage.Entry(codegenProject.id),
        Coverage.Entry(benchmarks.jvm(scala3Version).id),
        Coverage.Entry(codestyleRules.id, tests = false),
        Coverage.Entry(codestyleTests.id, measure = false)
      ),
      includes = Seq(s"$rootPackage.*", "fix.*"),
      output = baseDirectory.value / "target" / "coverage"
    ),
    terminologyCheck := Def.uncached {
      val root       = baseDirectory.value.absolute
      val prohibited = Codestyle.wordVariants("contract")
      val rules      = Seq(Codestyle.prohibitedTerm(prohibited, "prohibited vague terminology"))
      val sources    = Sources.authored(root, exclude = _.getFileName.toString == "build.sbt")
      val violations = Codestyle.scan(sources, rules, root)
      violations.foreach(violation => streams.value.log.error(violation.render))
      require(violations.isEmpty, s"found ${violations.size} terminology violations")
    },
    checkEnvironment      := (codegenProject / checkEnvironment).value,
    checkGeneratedSources := Def.taskDyn {
      val root      = baseDirectory.value
      val apiDir    = (api.jvm(scala3Version) / projectMatrixBaseDirectory).value.absolute / "src" / "generated"
      val ptxDir    = (ptx.jvm(scala3Version) / projectMatrixBaseDirectory).value.absolute / "src" / "generated"
      val amdgpuDir = (amdgpu.jvm(scala3Version) / projectMatrixBaseDirectory).value.absolute / "src" / "generated"
      val spirvDir  = (spirv.jvm(scala3Version) / projectMatrixBaseDirectory).value.absolute / "src" / "generated"
      val nativeDir = (codegenProject / nativeBindingsDirectory).value
      val generated = Seq(apiDir, ptxDir, amdgpuDir, spirvDir, nativeDir, root / "core/src/generated")
      def snapshot(): Map[Path, String] =
        generated.flatMap(_.walk()).map(path => path -> Fetch.sha256(path)).toMap
      val before = snapshot()
      Def.task {
        codegen.value
        val after   = snapshot()
        val changed = (before.keySet ++ after.keySet).filter(path => before.get(path) != after.get(path))
          .toVector.sortBy(_.toString).map(root.relativize(_))
        require(changed.isEmpty, s"generated sources were stale; regeneration changed:\n${changed.mkString("\n")}")
      }
    }.value,

    codegen := Def.uncached {
      Def.sequential(
        codegenProject / generateEnvironment,
        codegenProject / generateAPISources,
        codegenProject / generateNativeBindings,
        codegenProject / generateExecutionRPC,
        ptx.jvm(scala3Version) / generatePTXSources,
        amdgpu.jvm(scala3Version) / generateAMDGPUSources,
        spirv.jvm(scala3Version) / generateSPIRVSources
      ).value
    },
    gemstoneRun := {
      val arguments = Def.spaceDelimited("[--apis api,...] -- command [argument ...]").parsed
      if (arguments.isEmpty)
        throw MessageOnlyException("usage: gemstoneRun [--apis api,...] -- command [argument ...]")
      val root            = (LocalRootProject / baseDirectory).value
      val nativeDirectory = (native / nativeArtifact).value.absolute
      val converter       = fileConverter.value
      val log             = streams.value.log
      val classpath       = (server.jvm(scala3Version) / Runtime / fullClasspath).value
        .map(entry => converter.toPath(entry.data))
      val baseForkOptions = (server.jvm(scala3Version) / Compile / run / forkOptions).value
      val options         = baseForkOptions
        .withWorkingDirectory(root)
        .withEnvVars(baseForkOptions.envVars + (environmentName("NATIVE_DIRECTORY") -> s"$nativeDirectory"))
      ForkRun(options)
        .run(s"$rootPackage.server.CLI", classpath, "run" +: arguments, log)
        .get
    },
    // Keep standard sbt output paths; root clean also owns shared fixture and report outputs.
    cleanFiles += baseDirectory.value / "target"
  )

addCommandAlias(
  "lint",
  ";terminologyCheck;checkEnvironment;checkGeneratedSources;scalafmtCheckAll;scalafmtSbtCheck;scalafixAll --check;native/lint;conformance/lint"
)
