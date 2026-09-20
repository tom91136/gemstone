import sbt.complete.DefaultParsers.spaceDelimited
import sbt.internal.util.MessageOnlyException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import scala.jdk.CollectionConverters.*
import scala.language.implicitConversions
import scala.sys.process.{Process, ProcessLogger}

import Paths.{*, given}
import ExternalProjectPlugin.autoImport.*

// Each Native linker can consume gigabytes; use the plugin's standard scheduling tag.
Global / concurrentRestrictions +=
  Tags.limit(scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport.NativeTags.Link, 1)

lazy val projectOrganization  = "net.kurobako"
lazy val rootPackage          = s"$projectOrganization.gemstone"
lazy val scala3Version        = "3.9.0"
lazy val munitVersion         = "1.3.6"
lazy val catsCoreVersion      = "2.13.0"
lazy val catsParseVersion     = "1.1.0"
lazy val declineVersion       = "2.6.2"
lazy val ironVersion          = "3.3.2"
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
lazy val spirvHeadersVersion  = "vulkan-sdk-1.4.357.0"
lazy val spirvToolsVersion    = "vulkan-sdk-1.4.357.0"
lazy val ptxISAVersion        = "9.3"
lazy val ptxISAPackage        = "v" + ptxISAVersion.replace('.', '_')
lazy val spirvVersion         = "1.6"
lazy val spirvPackage         = "v" + spirvVersion.replace('.', '_')

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

def containsPathSegments(
    path: Path,
    first: String,
    rest: String*
): Boolean = {
  val segments = first +: rest
  path.iterator().asScala.map(segment => s"$segment").sliding(segments.size).contains(segments)
}

def nonEmptyCommand(
    name: String,
    value: String
): String = {
  require(value.nonEmpty && !value.contains('\u0000'), s"$name must not be empty and cannot contain NUL")
  value
}

def positiveEnvironmentInt(name: String, default: => Int): Int = {
  val raw = sys.env.get(name).fold(default) { value =>
    value.toIntOption.getOrElse(
      throw MessageOnlyException(s"$name must be a positive integer: $value")
    )
  }
  if (raw <= 0) throw MessageOnlyException(s"$name must be a positive integer: $raw")
  raw
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

private val nativeSourceExtensions = Set("c", "cc", "cpp", "cxx", "cu", "h", "hh", "hip", "hpp", "hxx")

def nativeSourceFiles(roots: Seq[Path]): Vector[Path] =
  roots.iterator
    .flatMap(_.descendants("*").iterator)
    .filter(_.isFile)
    .filter { path =>
      val name = path.fileName
      val dot  = name.lastIndexOf('.')
      dot >= 0 && nativeSourceExtensions(name.substring(dot + 1))
    }
    .toVector
    .sortBy(path => s"$path")

lazy val apiReferenceBundle = ReferenceBundles.Bundle(
  name = "api-native",
  artifacts = referenceArtifactsNamed(
    "nvidia-cuda-driver-dev",
    "nvidia-cuda-runtime-dev",
    "nvidia-cuda-crt",
    "nvidia-cublas-dev",
    "nvidia-cccl",
    "amd-hip",
    "amd-hsa",
    "opencl-headers",
    "level-zero"
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
      select = relative => relative == path"LICENSE.md" || relative.startsWith("include/hip")
    )
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
    require((output / "amd/hsa/runtime/hsa-runtime/inc/hsa.h").isFile, "HSA core header is absent")
    require((output / "khronos/opencl/CL/cl.h").isFile, "OpenCL headers are absent")
    require((output / "oneapi/level-zero/include/ze_api.h").isFile, "Level Zero header is absent")
  }
)

lazy val ptxReferenceBundle = ReferenceBundles.Bundle(
  name = "ptx",
  artifacts = referenceArtifactsNamed("nvidia-ptx"),
  populate = (cached, stage) =>
    IO.copyFile(
      sourceFile = cached("nvidia-ptx"),
      targetFile = stage / "nvidia/ptx/index.html"
    ),
  verify = output => require((output / "nvidia/ptx/index.html").isFile, "NVIDIA PTX specification is absent")
)

lazy val amdgpuReferenceBundle = ReferenceBundles.Bundle(
  name = "amdgpu",
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

lazy val spirvReferenceBundle = ReferenceBundles.Bundle(
  name = "spirv",
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
lazy val checkGeneratedAPISources     = taskKey[Unit]("Verify checked-in generated Scala API sources are current.")
lazy val checkGeneratedNativeBindings = taskKey[Unit]("Verify checked-in generated native bindings are current.")
lazy val generateExecutionRPC         =
  taskKey[Seq[Path]]("Generate the typed native execution RPC client.")
lazy val checkGeneratedExecutionRPC = taskKey[Unit]("Verify the typed native execution RPC client is current.")
lazy val generatePTXSources         =
  taskKey[Seq[Path]]("Update checked-in PTX sources and reference tests.")
lazy val checkGeneratedPTX     = taskKey[Unit]("Verify checked-in generated PTX sources and tests are current.")
lazy val generateAMDGPUSources =
  taskKey[Seq[Path]]("Update checked-in AMDGPU sources and oracle fixtures.")
lazy val checkGeneratedAMDGPU = taskKey[Unit]("Verify checked-in generated AMDGPU sources and fixtures are current.")
lazy val generateAPISources   =
  taskKey[Seq[Path]]("Bootstrap checked-in CUDA, HIP, HSA, Level Zero, and OpenCL API surfaces.")
lazy val generateSPIRVSources =
  taskKey[Seq[Path]]("Update checked-in SPIR-V sources and reference tests.")
lazy val checkGeneratedSPIRV      = taskKey[Unit]("Verify checked-in generated SPIR-V sources and tests are current.")
lazy val checkGeneratedSources    = taskKey[Unit]("Verify every checked-in generated source and fixture is current.")
lazy val codegen                  = taskKey[Unit]("Update and format every checked-in generated source and fixture.")
lazy val nativeConfigureArguments =
  taskKey[Seq[String]]("CMake configure arguments for the native runtime.")
lazy val nativeSourceDirectories =
  settingKey[Seq[Path]]("C and C++ source trees governed by the native codestyle checks.")
lazy val nativeFormatCheck     = taskKey[Unit]("Verify hand-written C and C++ sources are clang-formatted.")
lazy val nativeCodestyleCheck  = taskKey[Unit]("Verify project-specific C and C++ source conventions.")
lazy val nativeArtifact        = taskKey[Path]("Built native runtime directory.")
lazy val conformanceExecutable = taskKey[Path]("Built native conformance executable.")
lazy val gemstoneRun           = inputKey[Unit]("Build Gemstone and run a command through selected compute APIs.")
lazy val emulate               = inputKey[Unit]("Run conformance through Gemstone.")
lazy val cmakeExecutable       = settingKey[String]("CMake command used by native builds.")
lazy val ctestExecutable       = settingKey[String]("CTest command used by native tests.")
lazy val ninjaExecutable       = settingKey[String]("Ninja command used by CMake builds.")
lazy val clangExecutable       = settingKey[String]("Clang command used by API generation.")
lazy val clangFormatExecutable = settingKey[String]("Clang-format command used by native codestyle tasks.")
lazy val pythonExecutable      = settingKey[String]("Python command used by AMDGPU generation.")

Global / isaReferenceCacheDirectory := {
  val cacheRoot = sys.env.get("XDG_CACHE_HOME").filter(_.nonEmpty).map(value => path"$value")
    .getOrElse(path"${sys.props("user.home")}" / ".cache")
  cacheRoot / "gemstone/isa-references"
}
Global / cmakeExecutable       := sys.env.getOrElse("CMAKE", "cmake")
Global / ctestExecutable       := sys.env.getOrElse("CTEST", "ctest")
Global / ninjaExecutable       := sys.env.getOrElse("NINJA", "ninja")
Global / clangExecutable       := sys.env.getOrElse("CLANG", "clang")
Global / clangFormatExecutable := sys.env.getOrElse("CLANG_FORMAT", "clang-format")
Global / pythonExecutable      := sys.env.getOrElse("PYTHON", "python3")

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
  Compile / scalacOptions += "-Wunused:all",
  Test / scalacOptions ~= { _.filterNot(_ == "-Wunused:all") },
  Compile / scalafixConfig := Some(file(".scalafix.conf")),
  Test / scalafixConfig    := Some(file(".scalafix.conf")),
  // Keep inter-project classpaths directory-backed. sbt 2's exported JAR cache can
  // retain products whose package changed, which is especially harmful to Native linking.
  Compile / exportJars     := false,
  Test / parallelExecution := true,
  scalafmtDetailedError    := true,
  scalafmtFailOnErrors     := true
)

// Checked-in generator output is a persistent build input, not an sbt-managed
// output. Keep it outside target/sourceManaged and expose it as an additional
// unmanaged root so clean cannot remove it.
def checkedInGeneratedSources = Seq(
  Compile / checkedInGeneratedSourceDirectory :=
    projectMatrixBaseDirectory.value.toPath.absolute / "src/generated/main/scala",
  Test / checkedInGeneratedSourceDirectory :=
    projectMatrixBaseDirectory.value.toPath.absolute / "src/generated/test/scala",
  Test / checkedInGeneratedResourceDirectory :=
    projectMatrixBaseDirectory.value.toPath.absolute / "src/generated/test/resources",
  Compile / unmanagedSourceDirectories += (Compile / checkedInGeneratedSourceDirectory).value,
  Test / unmanagedSourceDirectories += (Test / checkedInGeneratedSourceDirectory).value,
  Test / unmanagedResourceDirectories += (Test / checkedInGeneratedResourceDirectory).value
)

lazy val generateNativeBindings =
  taskKey[Seq[Path]]("Generate native ABI declarations and implementation checks for every platform.")

lazy val codestyleRules = project.in(file("codestyle/rules"))
  .disablePlugins(_root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    name           := "gemstone-codestyle-rules",
    publish / skip := true,
    libraryDependencies +=
      ("ch.epfl.scala" %% "scalafix-core" % _root_.scalafix.sbt.BuildInfo.scalafixVersion)
        .cross(CrossVersion.for3Use2_13)
  )

lazy val codestyleInput = project.in(file("codestyle/input"))
  .disablePlugins(_root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    publish / skip                              := true,
    semanticdbEnabled                           := true,
    libraryDependencies += "io.github.iltotore" %% "iron" % ironVersion
  )

lazy val codestyleOutput = project.in(file("codestyle/output"))
  .disablePlugins(_root_.scalafix.sbt.ScalafixPlugin)
  .settings(publish / skip := true)

lazy val codestyleTests = project.in(file("codestyle/tests"))
  .dependsOn(codestyleInput, codestyleRules)
  .dependsOn(codestyleRules % ScalafixConfig)
  .enablePlugins(_root_.scalafix.sbt.ScalafixTestkitPlugin)
  .settings(
    publish / skip                         := true,
    scalafixTestkitOutputSourceDirectories := (codestyleOutput / Compile / sourceDirectories).value,
    scalafixTestkitInputSourceDirectories  := (codestyleInput / Compile / sourceDirectories).value,
    scalafixTestkitInputClasspath          := (codestyleInput / Compile / fullClasspath).value,
    scalafixTestkitInputScalacOptions      := (codestyleInput / Compile / scalacOptions).value,
    scalafixTestkitInputScalaVersion       := (codestyleInput / Compile / scalaVersion).value
  )

lazy val codestyle = project.in(file("codestyle"))
  .aggregate(codestyleRules, codestyleInput, codestyleOutput, codestyleTests)
  .disablePlugins(_root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    name           := "gemstone-codestyle",
    publish / skip := true
  )

lazy val api = projectMatrix
  .dependsOn(core)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources,
    name := "gemstone-api",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % munitVersion % Test
    )
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version))

lazy val codegenProject = Project("codegen", file("tools/codegen"))
  .dependsOn(core.jvm(scala3Version))
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    name                    := "gemstone-codegen",
    publish / skip          := true,
    nativeBindingsDirectory := (LocalRootProject / baseDirectory).value.toPath / "native/generated",
    libraryDependencies ++= Seq(
      "org.jsoup" % "jsoup" % jsoupVersion,

      "com.lihaoyi"        %% "upickle"       % upickleVersion,
      "com.monovore"       %% "decline"       % declineVersion,
      "io.github.iltotore" %% "iron"          % ironVersion,
      "org.scalameta"      %% "scalafmt-core" % scalafmtVersion,
      "org.typelevel"      %% "cats-core"     % catsCoreVersion,

      "org.scalameta" %% "munit" % munitVersion % Test
    ),
    prepareAPIReferences := Def.uncached {
      ReferenceBundles.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value.toPath / "isa-references",
        bundle = apiReferenceBundle,
        offline = sys.env.get("GEMSTONE_ISA_REFERENCES_OFFLINE").contains("1"),
        log = streams.value.log.info
      )
    },
    preparePTXReferences := Def.uncached {
      ReferenceBundles.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value.toPath / "isa-references",
        bundle = ptxReferenceBundle,
        offline = sys.env.get("GEMSTONE_ISA_REFERENCES_OFFLINE").contains("1"),
        log = streams.value.log.info
      )
    },
    prepareAMDGPUReferences := Def.uncached {
      ReferenceBundles.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value.toPath / "isa-references",
        bundle = amdgpuReferenceBundle,
        offline = sys.env.get("GEMSTONE_ISA_REFERENCES_OFFLINE").contains("1"),
        log = streams.value.log.info
      )
    },
    prepareSPIRVReferences := Def.uncached {
      ReferenceBundles.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value.toPath / "isa-references",
        bundle = spirvReferenceBundle,
        offline = sys.env.get("GEMSTONE_ISA_REFERENCES_OFFLINE").contains("1"),
        log = streams.value.log.info
      )
    },
    generateOfficialAPIManifest := Def.uncached {
      val converter  = fileConverter.value
      val references = prepareAPIReferences.value
      val output     = target.value.toPath / "official-api-headers.json"
      val clang      = nonEmptyCommand("CLANG", (Global / clangExecutable).value)
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
          openCLHeadersVersion
        ),
        streams.value.log
      ).get
      val outputs = output.descendants("*.scala").filter(_.isFile)
      require(outputs.nonEmpty, s"API generator did not produce Scala sources under $output")
      outputs
    },
    checkGeneratedAPISources := Def.uncached {
      val converter = fileConverter.value
      val manifest  = generateOfficialAPIManifest.value
      val output    = (api.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value
      (Compile / runner).value.run(
        s"$rootPackage.codegen.GenerateAPISurfaces",
        (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data)),
        Seq(
          "--check",
          s"$manifest",
          s"$output",
          cudaAPIVersion,
          hipVersion,
          hsaVersion,
          levelZeroVersion,
          openCLHeadersVersion
        ),
        streams.value.log
      ).get
      ()
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
    checkGeneratedNativeBindings := Def.uncached {
      val converter = fileConverter.value
      val manifest  = generateOfficialAPIManifest.value
      val output    = nativeBindingsDirectory.value
      (Compile / runner).value.run(
        s"$rootPackage.codegen.GenerateNativeBindings",
        (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data)),
        Seq(
          "--check",
          s"$output",
          s"$manifest"
        ),
        streams.value.log
      ).get
      ()
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
    },
    checkGeneratedExecutionRPC := Def.uncached {
      val converter = fileConverter.value
      val output    = nativeBindingsDirectory.value / "rpc"
      (Compile / runner).value.run(
        s"$rootPackage.codegen.GenerateExecutionRPC",
        (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data)),
        Seq("--check", s"$output"),
        streams.value.log
      ).get
      ()
    }
  )

lazy val core = projectMatrix
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    name := "gemstone-core",
    libraryDependencies ++= Seq(
      "io.github.iltotore" %% "iron"      % ironVersion,
      "org.typelevel"      %% "cats-core" % catsCoreVersion,

      "org.scalameta" %% "munit" % munitVersion % Test
    )
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version))

lazy val externalCodestyleSettings = Seq(
  nativeSourceDirectories := Seq(baseDirectory.value.toPath),
  format                  := Def.uncached {
    val sources = nativeSourceFiles(nativeSourceDirectories.value)
      .filterNot(containsPathSegments(_, "generated"))
    val command = Vector(nonEmptyCommand("CLANG_FORMAT", (Global / clangFormatExecutable).value), "-i") ++
      sources.map(source => s"$source")
    val status = Process(command, baseDirectory.value).!
    require(status == 0, s"clang-format exited with $status")
  },
  nativeFormatCheck := Def.uncached {
    val sources = nativeSourceFiles(nativeSourceDirectories.value)
      .filterNot(containsPathSegments(_, "generated"))
    val command = Vector(
      nonEmptyCommand("CLANG_FORMAT", (Global / clangFormatExecutable).value),
      "--dry-run",
      "--Werror"
    ) ++ sources.map(source => s"$source")
    val status = Process(command, baseDirectory.value).!
    require(status == 0, s"sources are not clang-formatted; run ${thisProject.value.id}/format")
  },
  nativeCodestyleCheck := Def.uncached {
    val forbidden =
      raw"\bstd::(?:size_t|ptrdiff_t|max_align_t|u?int(?:8|16|32|64)_t|u?int(?:ptr|max)_t|u?int_(?:least|fast)(?:8|16|32|64)_t)\b".r
    val testCase = raw"^[A-Z_]*TEST_CASE[A-Z_]*\s*\(".r
    val include  = raw"^\s*#\s*include\b".r
    def includeBlockNeedsSeparator(lines: Vector[String], includeIndex: Int): Boolean = {
      var index = includeIndex + 1
      while (index < lines.size && lines(index).trim.nonEmpty) {
        if (include.findPrefixOf(lines(index)).nonEmpty) return false
        if (lines(index).trim.matches("#\\s*(else|elif)\\b.*")) return false
        if (!lines(index).trim.startsWith("#")) return true
        index += 1
      }
      false
    }
    val violations = nativeSourceFiles(nativeSourceDirectories.value).flatMap { source =>
      val lines = Files.readAllLines(source, StandardCharsets.UTF_8).asScala.toVector
      lines.iterator.zipWithIndex.flatMap { case (line, index) =>
        val numericType =
          forbidden.findFirstIn(line).map(value => s"$source:${index + 1}: forbidden numeric type '$value'")
        val testSpacing =
          Option.when(testCase.findPrefixOf(line).nonEmpty && index > 0 && lines(index - 1).trim.nonEmpty)(
            s"$source:${index + 1}: Catch test cases must be preceded by a blank line"
          )
        val includeSpacing =
          Option.when(include.findPrefixOf(line).nonEmpty && includeBlockNeedsSeparator(lines, index))(
            s"$source:${index + 2}: include blocks must be followed by a blank line"
          )
        Iterator(numericType, testSpacing, includeSpacing).flatten
      }
    }
    violations.foreach(violation => streams.value.log.error(violation))
    require(violations.isEmpty, s"found ${violations.size} native codestyle violations")
  },
  lint := Def.sequential(nativeFormatCheck, nativeCodestyleCheck).value
)

lazy val nativeRuntimeSettings = Seq(
  nativeConfigureArguments := Def.uncached {
    val references      = (codegenProject / prepareAPIReferences).value
    val matchingHeaders = (references / "nvidia/cuda").descendants("cuda.h").filter { path =>
      (0 until path.getNameCount - 1).exists { index =>
        s"${path.getName(index)}" == s"cuda-$cudaAPIVersion" &&
        s"${path.getName(index + 1)}" == "targets"
      }
    }
    require(matchingHeaders.size == 1, s"expected one pinned CUDA header, found ${matchingHeaders.size}")
    val cudaInclude =
      matchingHeaders.head.parent.getOrElse(
        throw MessageOnlyException(s"CUDA header has no parent: ${matchingHeaders.head}")
      )
    Seq(
      "-G",
      "Ninja",
      s"-DCMAKE_MAKE_PROGRAM=${nonEmptyCommand("NINJA", (Global / ninjaExecutable).value)}",
      "-DBUILD_TESTING=ON",
      s"-DCMAKE_BUILD_TYPE=${sys.env.getOrElse("CMAKE_BUILD_TYPE", "Release")}",
      s"-DCUDA_INCLUDE_DIR=$cudaInclude",
      s"-DHIP_INCLUDE_DIR=${references / "amd/hip/include"}",
      s"-DHSA_INCLUDE_DIR=${references / "amd/hsa/runtime/hsa-runtime/inc"}",
      s"-DHSAKMT_INCLUDE_DIR=${references / "amd/hsa/libhsakmt/include"}",
      s"-DLEVEL_ZERO_INCLUDE_DIR=${references / "oneapi/level-zero/include"}",
      s"-DOPENCL_INCLUDE_DIR=${references / "khronos/opencl"}"
    )
  },
  nativeArtifact := Def.uncached {
    val root        = (LocalRootProject / baseDirectory).value.toPath.absolute
    val parallelism = positiveEnvironmentInt(
      "CMAKE_BUILD_PARALLEL_LEVEL",
      sys.runtime.availableProcessors()
    )
    CMake.build(
      source = baseDirectory.value.toPath,
      build = root / "target/native/build",
      cmake = nonEmptyCommand("CMAKE", (Global / cmakeExecutable).value),
      arguments = nativeConfigureArguments.value,
      parallelism = parallelism,
      log = streams.value.log.info
    )
  },
  build := {
    val arguments = spaceDelimited("").parsed
    if (arguments.nonEmpty) throw MessageOnlyException("usage: native/build")
    nativeArtifact.value
  },
  Test / test := {
    val arguments = spaceDelimited("").parsed
    if (arguments.nonEmpty) throw MessageOnlyException("usage: native/test")
    val parallelism = positiveEnvironmentInt("GEMSTONE_EXECUTION_PARALLELISM", sys.runtime.availableProcessors())
    CMake.test(
      build = nativeArtifact.value,
      ctest = nonEmptyCommand("CTEST", (Global / ctestExecutable).value),
      parallelism = parallelism,
      log = streams.value.log.info
    )
    sbt.protocol.testing.TestResult.Passed
  }
)

lazy val native = project.in(file("native"))
  .enablePlugins(ExternalProjectPlugin)
  .disablePlugins(plugins.JvmPlugin, plugins.IvyPlugin)
  .settings(
    externalCodestyleSettings,
    nativeRuntimeSettings,
    name       := "gemstone-native",
    cleanFiles := Seq((LocalRootProject / baseDirectory).value.toPath / "target/native")
  )

lazy val ptx = projectMatrix
  .in(file("isa/ptx"))
  .dependsOn(core)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources,
    name := "gemstone-isa-ptx",
    libraryDependencies ++= Seq(
      "io.github.iltotore" %% "iron"       % ironVersion,
      "org.typelevel"      %% "cats-core"  % catsCoreVersion,
      "org.typelevel"      %% "cats-parse" % catsParseVersion,

      "org.scalameta" %% "munit" % munitVersion % Test
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
    },
    checkGeneratedPTX := Def.uncached {
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
        Seq("instructions", "--check", s"$input", s"$sourceRoot", ptxISAVersion, ptxISAPackage),
        log
      ).get
      run.run(
        s"$rootPackage.codegen.GeneratePTXISA",
        classpath,
        Seq("tests", "--check", s"$input", s"$testRoot", ptxISAVersion, ptxISAPackage),
        log
      ).get
      ()
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version))

lazy val amdgpu = projectMatrix
  .in(file("isa/amdgpu"))
  .dependsOn(core)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources,
    name := "gemstone-isa-amdgpu",
    libraryDependencies ++= Seq(
      "com.outr"           %% "scribe"     % scribeVersion,
      "io.github.iltotore" %% "iron"       % ironVersion,
      "org.typelevel"      %% "cats-core"  % catsCoreVersion,
      "org.typelevel"      %% "cats-parse" % catsParseVersion,

      "org.scalameta" %% "munit" % munitVersion % Test
    ),
    generateAMDGPUSources := Def.uncached {
      val converter    = fileConverter.value
      val references   = (codegenProject / prepareAMDGPUReferences).value
      val python       = nonEmptyCommand("PYTHON", (Global / pythonExecutable).value)
      val sourceRoot   = (Compile / checkedInGeneratedSourceDirectory).value
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
          s"$sourceRoot"
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
      val sources     = sourceRoot.descendants("*.scala")
      val oracleFiles = resourceRoot.descendants("*").filter(_.isFile)
      require(
        sources.nonEmpty && oracleFiles.nonEmpty && sources.forall(_.isFile) && oracleFiles.forall(_.isFile),
        "AMDGPU generator outputs are incomplete"
      )
      sources ++ oracleFiles
    },
    checkGeneratedAMDGPU := Def.uncached {
      val converter    = fileConverter.value
      val references   = (codegenProject / prepareAMDGPUReferences).value
      val python       = nonEmptyCommand("PYTHON", (Global / pythonExecutable).value)
      val sourceRoot   = (Compile / checkedInGeneratedSourceDirectory).value
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
          "--check",
          s"$references",
          s"$sourceRoot"
        ),
        log
      ).get
      run.run(
        s"$rootPackage.codegen.GenerateAMDGPUOracle",
        classpath,
        Seq("--check", s"$references", s"$resourceRoot"),
        log
      ).get
      ()
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = Seq(
      Test / nativeConfig ~= (_.withEmbedResources(true))
    )
  )

lazy val spirv = projectMatrix
  .in(file("isa/spirv"))
  .dependsOn(core)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources,
    name := "gemstone-isa-spirv",
    libraryDependencies ++= Seq(
      "io.github.iltotore" %% "iron"      % ironVersion,
      "org.typelevel"      %% "cats-core" % catsCoreVersion,

      "org.scalameta" %% "munit" % munitVersion % Test
    ),
    generateSPIRVSources := Def.uncached {
      val converter  = fileConverter.value
      val references = (codegenProject / prepareSPIRVReferences).value
      val grammar    = references / "khronos/spirv-headers/include/spirv/unified1/spirv.core.grammar.json"
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
    },
    checkGeneratedSPIRV := Def.uncached {
      val converter  = fileConverter.value
      val references = (codegenProject / prepareSPIRVReferences).value
      val sourceRoot = (Compile / checkedInGeneratedSourceDirectory).value
      val testRoot   = (Test / checkedInGeneratedSourceDirectory).value
      val grammar    = references / "khronos/spirv-headers/include/spirv/unified1/spirv.core.grammar.json"
      val tests      = references / "khronos/spirv-tools/test"
      val classpath  = (codegenProject / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data))
      val run        = (codegenProject / Compile / runner).value
      val log        = streams.value.log
      run.run(
        s"$rootPackage.codegen.GenerateSPIRVISA",
        classpath,
        Seq("instructions", "--check", s"$grammar", s"$sourceRoot", spirvPackage),
        log
      ).get
      run.run(
        s"$rootPackage.codegen.GenerateSPIRVISA",
        classpath,
        Seq("tests", "--check", s"$grammar", s"$tests", s"$testRoot", spirvPackage),
        log
      ).get
      ()
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version))

lazy val server = projectMatrix
  .in(file("server"))
  .dependsOn(core, ptx, amdgpu, spirv)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    name := "gemstone-server",
    libraryDependencies ++= Seq(
      "com.lihaoyi"        %% "upickle"   % upickleVersion,
      "com.monovore"       %% "decline"   % declineVersion,
      "com.outr"           %% "scribe"    % scribeVersion,
      "io.github.iltotore" %% "iron"      % ironVersion,
      "org.typelevel"      %% "cats-core" % catsCoreVersion,

      "org.scalameta" %% "munit" % munitVersion % Test
    ),
    Compile / mainClass := Some(s"$rootPackage.server.cli.GemstoneCLI")
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    settings = Seq(
      Compile / run / fork := true,
      Test / fork          := true,
      Test / javaOptions += Def.uncached {
        s"-Dgemstone.native.directory=${(native / nativeArtifact).value}"
      }
    )
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = Seq(
      Compile / nativeConfig ~= (_.withMode(scala.scalanative.build.Mode.releaseFast)),
      Test / envVars += (
        "GEMSTONE_NATIVE_TEST_DIRECTORY" -> s"${(native / nativeArtifact).value}"
      )
    )
  )

lazy val benchmarks = projectMatrix.in(file("tools/benchmarks"))
  .dependsOn(core, ptx, amdgpu, spirv, server)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    name           := "gemstone-benchmarks",
    publish / skip := true,
    libraryDependencies ++= Seq(
      "com.lihaoyi"        %% "upickle"   % upickleVersion,
      "com.monovore"       %% "decline"   % declineVersion,
      "io.github.iltotore" %% "iron"      % ironVersion,
      "org.typelevel"      %% "cats-core" % catsCoreVersion,

      "org.scalameta" %% "munit" % munitVersion % Test
    ),
    Compile / mainClass  := Some(s"$rootPackage.benchmarks.BenchmarkRunner"),
    Compile / run / fork := true
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    settings = Seq(
      Compile / run / javaOptions ++= Seq("-Xms512m", "-Xmx4g")
    )
  )
  .nativePlatform(scalaVersions = Seq(scala3Version))

lazy val conformance = project.in(file("conformance"))
  .enablePlugins(ExternalProjectPlugin)
  .disablePlugins(plugins.JvmPlugin, plugins.IvyPlugin)
  .settings(
    externalCodestyleSettings,
    name  := "gemstone-conformance",
    build := {
      val arguments = spaceDelimited("[target-set]").parsed
      if (arguments.size > 1)
        throw MessageOnlyException("usage: conformance/build [target-set]")
      val targetSet = arguments.headOption.getOrElse("default")
      if (!targetSet.matches("[A-Za-z0-9_-]+"))
        throw MessageOnlyException(s"invalid conformance target set: $targetSet")
      val root      = (LocalRootProject / baseDirectory).value.toPath.absolute
      val build     = root / s"target/conformance/build/$targetSet"
      val artifacts = root / "target/conformance/artifacts"
      val log       = streams.value.log
      CMake.build(
        source = baseDirectory.value.toPath / "artifacts",
        build = build,
        cmake = nonEmptyCommand("CMAKE", (Global / cmakeExecutable).value),
        arguments = Seq(
          "-G",
          "Ninja",
          s"-DCMAKE_MAKE_PROGRAM=${nonEmptyCommand("NINJA", (Global / ninjaExecutable).value)}",
          s"-DCONTAINER_EXECUTABLE=${nonEmptyCommand("CONTAINER_EXECUTABLE", sys.env.getOrElse("CONTAINER_EXECUTABLE", "podman"))}",
          s"-DROCM_VERSION=$rocmVersion",
          s"-DCONFORMANCE_TARGET_SET=$targetSet",
          s"-DCONFORMANCE_ARTIFACT_DIRECTORY=$artifacts"
        ),
        parallelism = positiveEnvironmentInt(
          "CMAKE_BUILD_PARALLEL_LEVEL",
          sys.runtime.availableProcessors()
        ),
        log = log.info,
        target = Some("artifacts")
      )
      artifacts
    },
    conformanceExecutable := Def.uncached {
      val root  = (LocalRootProject / baseDirectory).value.toPath.absolute
      val build = root / "target/conformance/executable"
      val log   = streams.value.log
      CMake.build(
        source = baseDirectory.value.toPath,
        build = build,
        cmake = nonEmptyCommand("CMAKE", (Global / cmakeExecutable).value),
        arguments = Seq(
          "-G",
          "Ninja",
          s"-DCMAKE_MAKE_PROGRAM=${nonEmptyCommand("NINJA", (Global / ninjaExecutable).value)}",
          "-DCMAKE_BUILD_TYPE=Release",
          "-DBUILD_TESTING=OFF",
          "-DCONFORMANCE_BACKEND=",
          "-DCONFORMANCE_BUILD_UNIFIED=OFF",
          "-DCONFORMANCE_BUILD_CTS=OFF",
          "-DCONFORMANCE_BUILD_MINIAPPS=OFF"
        ),
        parallelism = positiveEnvironmentInt(
          "CMAKE_BUILD_PARALLEL_LEVEL",
          sys.runtime.availableProcessors()
        ),
        log = log.info,
        target = Some("conformance")
      )
      val suffix = if (sys.props("os.name").toLowerCase.contains("windows")) ".exe" else ""
      val result = build / s"bin/conformance$suffix"
      require(result.isFile, s"conformance executable was not produced: $result")
      result
    },
    Test / test := {
      val arguments = spaceDelimited("").parsed
      if (arguments.nonEmpty) throw MessageOnlyException("usage: conformance/test")
      val root  = (LocalRootProject / baseDirectory).value.toPath.absolute
      val build = root / "target/conformance/test"
      val log   = streams.value.log
      CMake.build(
        source = baseDirectory.value.toPath,
        build = build,
        cmake = nonEmptyCommand("CMAKE", (Global / cmakeExecutable).value),
        arguments = Seq(
          "-G",
          "Ninja",
          s"-DCMAKE_MAKE_PROGRAM=${nonEmptyCommand("NINJA", (Global / ninjaExecutable).value)}",
          "-DCMAKE_BUILD_TYPE=Release",
          "-DCONFORMANCE_BACKEND=",
          "-DCONFORMANCE_BUILD_UNIFIED=OFF",
          "-DCONFORMANCE_BUILD_CTS=OFF",
          "-DCONFORMANCE_BUILD_MINIAPPS=OFF"
        ),
        parallelism = positiveEnvironmentInt(
          "CMAKE_BUILD_PARALLEL_LEVEL",
          sys.runtime.availableProcessors()
        ),
        log = log.info,
        target = Some("conformance_unit_tests")
      )
      CMake.test(
        build = build,
        ctest = nonEmptyCommand("CTEST", (Global / ctestExecutable).value),
        parallelism = positiveEnvironmentInt(
          "GEMSTONE_EXECUTION_PARALLELISM",
          sys.runtime.availableProcessors()
        ),
        log = log.info
      )
      sbt.protocol.testing.TestResult.Passed
    },
    Compile / run := {
      val root      = (LocalRootProject / baseDirectory).value.toPath.absolute
      val arguments = spaceDelimited("[run option|target ...]").parsed
      val log       = streams.value.log
      runConformance(
        root = root,
        artifacts = root / "target/conformance/artifacts",
        executable = conformanceExecutable.value,
        ctest = nonEmptyCommand("CTEST", (Global / ctestExecutable).value),
        arguments = arguments,
        output = root / "target/conformance/direct",
        launcher = Seq.empty,
        environment = Seq.empty,
        log = log.info,
        error = log.error
      )
    },
    emulate := {
      val root            = (LocalRootProject / baseDirectory).value.toPath.absolute
      val arguments       = spaceDelimited("[run option|target ...]").parsed
      val nativeDirectory = (native / nativeArtifact).value.absolute
      val converter       = fileConverter.value
      val log             = streams.value.log
      val classpath       = (server.jvm(scala3Version) / Runtime / fullClasspath).value
        .map(entry => s"${converter.toPath(entry.data)}")
        .mkString(sys.props("path.separator"))
      val java = path"${sys.props("java.home")}" / "bin" /
        (if (sys.props("os.name").toLowerCase.contains("windows")) "java.exe" else "java")
      val launcher = Vector(
        s"$java",
        "-Xms512m",
        s"-Xmx${sys.env.getOrElse("GEMSTONE_CONFORMANCE_SERVER_HEAP", "8g")}",
        "-cp",
        classpath,
        s"$rootPackage.server.cli.GemstoneCLI",
        "run"
      )
      runConformance(
        root = root,
        artifacts = root / "target/conformance/artifacts",
        executable = conformanceExecutable.value,
        ctest = nonEmptyCommand("CTEST", (Global / ctestExecutable).value),
        arguments = arguments :+ "--isolated-launcher",
        output = root / "target/conformance/emulated",
        launcher = launcher,
        environment = Seq(
          "GEMSTONE_NATIVE_DIRECTORY"           -> s"$nativeDirectory",
          "GEMSTONE_EXECUTION_MAX_INSTRUCTIONS" -> sys.env.getOrElse(
            "GEMSTONE_EXECUTION_MAX_INSTRUCTIONS",
            "1000000000"
          )
        ),
        log = log.info,
        error = log.error
      )
    },
    cleanFiles := Seq((LocalRootProject / baseDirectory).value.toPath / "target/conformance")
  )

lazy val gemstone = project.in(file("."))
  .aggregate(api.projectRefs *)
  .aggregate(codegenProject)
  .aggregate(core.projectRefs *)
  .aggregate(server.projectRefs *)
  .aggregate(ptx.projectRefs *)
  .aggregate(amdgpu.projectRefs *)
  .aggregate(spirv.projectRefs *)
  .aggregate(benchmarks.projectRefs *)
  .aggregate(codestyle)
  .dependsOn(codestyleRules % ScalafixConfig)
  .settings(
    commonSettings,
    name                  := "gemstone",
    publish / skip        := true,
    checkGeneratedSources := {
      val _ = (codegenProject / checkGeneratedAPISources).value
      val _ = (codegenProject / checkGeneratedNativeBindings).value
      val _ = (codegenProject / checkGeneratedExecutionRPC).value
      val _ = (ptx.jvm(scala3Version) / checkGeneratedPTX).value
      val _ = (amdgpu.jvm(scala3Version) / checkGeneratedAMDGPU).value
      val _ = (spirv.jvm(scala3Version) / checkGeneratedSPIRV).value
      ()
    },
    codegen := Def.uncached {
      Def.sequential(
        codegenProject / generateAPISources,
        codegenProject / generateNativeBindings,
        codegenProject / generateExecutionRPC,
        ptx.jvm(scala3Version) / generatePTXSources,
        amdgpu.jvm(scala3Version) / generateAMDGPUSources,
        spirv.jvm(scala3Version) / generateSPIRVSources,
        scalafmtAll
      ).value
    },
    gemstoneRun := {
      val arguments = spaceDelimited("[--apis api,...] -- command [argument ...]").parsed
      if (arguments.isEmpty)
        throw MessageOnlyException("usage: gemstoneRun [--apis api,...] -- command [argument ...]")
      val nativeDirectory = (native / nativeArtifact).value.absolute
      val converter       = fileConverter.value
      val classpath       = (server.jvm(scala3Version) / Runtime / fullClasspath).value
        .map(entry => s"${converter.toPath(entry.data)}")
        .mkString(sys.props("path.separator"))
      val java = path"${sys.props("java.home")}" / "bin" /
        (if (sys.props("os.name").toLowerCase.contains("windows")) "java.exe" else "java")
      val command = Vector(
        s"$java",
        "-cp",
        classpath,
        s"$rootPackage.server.cli.GemstoneCLI",
        "run"
      ) ++ arguments
      val status = Process(command, baseDirectory.value, "GEMSTONE_NATIVE_DIRECTORY" -> s"$nativeDirectory")
        .!(ProcessLogger(streams.value.log.info, streams.value.log.error))
      require(status == 0, s"Gemstone process exited with $status")
    },
    // Keep standard sbt output paths; root clean also owns shared fixture and report outputs.
    cleanFiles += baseDirectory.value.toPath / "target"
  )

addCommandAlias(
  "lint",
  ";checkGeneratedSources;scalafmtCheckAll;scalafixAll --check;native/lint;conformance/lint"
)
