// Each Native linker can consume gigabytes; use the plugin's standard scheduling tag.
Global / concurrentRestrictions +=
  Tags.limit(scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport.NativeTags.Link, 1)

lazy val scala3Version = "3.9.0"
// Bump whenever Col's backing type changes so cached JVM/Native products cannot retain the previous ABI.
lazy val runtimeCollectionABI = "arrayseq-v2"
lazy val munitVersion         = "1.3.5"
lazy val catsCoreVersion      = "2.13.0"
lazy val catsParseVersion     = "1.1.0"
lazy val jsoupVersion         = "1.23.2"
lazy val upickleVersion       = "4.4.3"
lazy val scalafmtVersion      = "3.11.5"
lazy val llvmVersion          = "23.1.0"
lazy val mesaVersion          = "26.2.1"
lazy val cudaPTXVersion       = "13.3.0"
lazy val cudaAPIVersion       = "13.3"
lazy val cudaAPIPackage       = "13.3.29-1"
lazy val cudaCRTPackage       = "13.3.33-1"
lazy val hipVersion           = "7.2.4"
lazy val hsaVersion           = "7.2.4"
lazy val ptxISAVersion        = "9.3"
lazy val ptxISAPackage        = "v" + ptxISAVersion.replace('.', '_')
lazy val amdISAXMLVersion     = "2026_08_06"
lazy val openCLHeadersVersion = "2026.05.29"
lazy val levelZeroVersion     = "1.33.1"
lazy val spirvHeadersVersion  = "vulkan-sdk-1.4.357.0"
lazy val spirvToolsVersion    = "vulkan-sdk-1.4.357.0"
lazy val spirvVersion         = "1.6"
lazy val spirvPackage         = "v" + spirvVersion.replace('.', '_')

lazy val llvmSourceSha256     = "ab1f0e3ec52448c33e8782eaf0422504b87c7b016b22514653ee0d8fcee479ff"
lazy val mesaSourceSha256     = "c47e81bddc4760360a41ac3c5acec38acb81f9d750ecef47e7f3adc7021a4442"
lazy val cudaPTXHtmlSha256    = "40026f79b65fee3f8c8b3e717c6f80c6385e8d97215318d6e4b7f6106f58d5a4"
lazy val cudaDriverDevSha256  = "8bf25d2a822c107afea871c3f521014b55af095d7ac80d56dda27a7599f0fc70"
lazy val cudaRuntimeDevSha256 = "0c137b804e1b68879b873ce996f23f6ca9bc644872be15cb870bdbc43319a717"
lazy val cudaCRTDevSha256     = "945852c041126c913c0b1ed9fa8d231550fcaf5a3e05962ecd4669807a890f66"
lazy val hipSourceSha256      = "e32bb08af5b62575d2daefa1d10edcbac2c9f200c877c6c5b0766b76febd482b"
lazy val hsaSourceSha256      = "60532cd86edce5a603aa18df406ec1f5a3d18f0663d79b3b7822ff721c5a04ec"
lazy val amdISAXMLSha256      = "82404f1126761b7877595b622afa7e1f311f2f41e89a3abe9aaf8ad045c082e2"
lazy val openCLHeadersSha256  = "0bdef6f653c577827a75fac3e3f18b0bf000171e39fa65c2eb84ec804c822cab"
lazy val levelZeroSha256      = "9f6872a84106bdb686131c231396eca67631e1c47081ef50e2fe0b815aca6eb0"
lazy val spirvHeadersSha256   = "7d40c8c1983c1aba6f6f2fcbcf7be345d33a5693aeee0cc5b8c579fb8e3f2fea"
lazy val spirvToolsSha256     = "09f70a9197d9cd28e1e10a2fca5ffde8c5e269f363eed9afa838ad88ed2649c9"

lazy val isaReferenceCacheDirectory = settingKey[File]("Persistent cache for pinned ISA reference artifacts.")
lazy val prepareISAReferences       = taskKey[File]("Download, verify, and selectively extract pinned ISA references.")
lazy val generateAPIValidationManifest =
  taskKey[File]("Record normalized declarations from pinned official API headers.")
lazy val checkAPISurfaces = taskKey[Unit]("Validate checked-in API declarations against pinned official headers.")
lazy val checkGeneratedAPISources  = taskKey[Unit]("Verify checked-in generated ABI and RPC files are current.")
lazy val generatePTXSources        = taskKey[Seq[File]]("Update checked-in PTX instruction metadata.")
lazy val generatePTXReferenceTests =
  taskKey[Seq[File]]("Update checked-in PTX documentation conformance tests.")
lazy val generateAMDGPUSources         = taskKey[Seq[File]]("Update checked-in normalized AMDGPU metadata.")
lazy val generateAMDGPUOracleResources =
  taskKey[Seq[File]]("Update the checked-in, bounded LLVM AMDGPU oracle fixtures.")
lazy val generateAPISources =
  taskKey[Seq[File]]("Bootstrap checked-in CUDA, HIP, HSA, Level Zero, and OpenCL API surfaces.")
lazy val generateSPIRVSources        = taskKey[Seq[File]]("Update checked-in SPIR-V grammar metadata.")
lazy val generateSPIRVReferenceTests =
  taskKey[Seq[File]]("Update checked-in SPIRV-Tools-derived tests.")

Global / isaReferenceCacheDirectory := {
  val cacheRoot = sys.env.get("XDG_CACHE_HOME").filter(_.nonEmpty).map(file)
    .getOrElse(file(sys.props("user.home")) / ".cache")
  cacheRoot / "gemstone" / "isa-references"
}

lazy val commonSettings = Seq(
  organization := "net.kurobako",
  version      := "0.1.0-SNAPSHOT",
  scalaVersion := scala3Version,
  scalacOptions ++= Seq(
    "-no-indent",
    "-deprecation",
    "-Wconf:cat=unchecked:error",
    "-Wconf:name=MatchCaseUnreachable:error",
    "-Wconf:name=PatternMatchExhaustivity:error",
    "-Wnonunit-statement",
    "-Wvalue-discard",
    "-Yretain-trees",
    s"-Xmacro-settings:gemstone.runtime-collection=$runtimeCollectionABI",
    "-language:noAutoTupling",
    "-source",
    "3.9"
  ),
  Compile / scalacOptions += "-Wunused:all",
  Test / scalacOptions ~= { _.filterNot(_ == "-Wunused:all") },
  Test / scalafixConfig := Some(file("quality/test.scalafix.conf")),
  // Keep inter-project classpaths directory-backed. sbt 2's exported JAR cache can
  // retain products whose package changed, which is especially harmful to Native linking.
  Compile / exportJars                   := false,
  Test / parallelExecution               := true,
  scalafmtDetailedError                  := true,
  scalafmtFailOnErrors                   := true,
  libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
)

lazy val catsCoreJVMSettings = Seq(
  libraryDependencies += "org.typelevel" %% "cats-core" % catsCoreVersion
)

lazy val catsCoreNativeSettings = Seq(
  libraryDependencies += "org.typelevel" % "cats-core_native0.5_3" % catsCoreVersion
)

def checkedInGeneratedSources(module: String) = Seq(
  Compile / unmanagedSourceDirectories +=
    (LocalRootProject / baseDirectory).value / module / "src" / "generated" / "main" / "scala",
  Test / unmanagedSourceDirectories +=
    (LocalRootProject / baseDirectory).value / module / "src" / "generated" / "test" / "scala",
  Test / unmanagedResourceDirectories +=
    (LocalRootProject / baseDirectory).value / module / "src" / "generated" / "test" / "resources"
)

lazy val skipSharedSourceFormatting = Seq(
  Compile / scalafmt / unmanagedSources := Seq.empty,
  Test / scalafmt / unmanagedSources    := Seq.empty
)

lazy val qualityRules = project
  .in(file("quality/rules"))
  .disablePlugins(_root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    organization := "net.kurobako",
    name         := "gemstone-quality-rules",
    scalaVersion := scala3Version,
    libraryDependencies +=
      ("ch.epfl.scala" %% "scalafix-core" % _root_.scalafix.sbt.BuildInfo.scalafixVersion)
        .cross(CrossVersion.for3Use2_13)
  )

lazy val generatorClang     = settingKey[String]("Clang executable for official header extraction.")
lazy val generatorPython    = settingKey[String]("Python executable for the pinned ACO exporter.")
lazy val generatorClasspath = taskKey[Seq[File]]("Classpath used by development generators.")
lazy val generateRPCSources = taskKey[Seq[File]]("Generate CUDA transport and server dispatch sources.")

lazy val generatorSettings = Seq(
  generatorClang     := "clang",
  generatorPython    := "python3",
  scalafixConfig     := Some(file("quality/codegen.scalafix.conf")),
  generatorClasspath := Def.uncached {
    val converter = fileConverter.value
    (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toFile)
  }
)

lazy val apiModel = projectMatrix.in(file("api/model"))
  .dependsOn(core)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, name := "gemstone-api-model")
  .jvmPlatform(scalaVersions = Seq(scala3Version), settings = catsCoreJVMSettings)
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = skipSharedSourceFormatting ++ catsCoreNativeSettings)

lazy val api = projectMatrix
  .dependsOn(apiModel)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources("api"),
    name := "gemstone-api",
    Compile / sourceGenerators += (codegen.jvm(scala3Version) / generateAPISources).taskValue
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version), settings = catsCoreJVMSettings)
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = skipSharedSourceFormatting ++ catsCoreNativeSettings)

lazy val codegen = projectMatrix
  .dependsOn(apiModel)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    generatorSettings,
    name                                  := "gemstone-codegen",
    libraryDependencies += "org.jsoup"     % "jsoup"                 % jsoupVersion,
    libraryDependencies += "com.lihaoyi"  %% "upickle"               % upickleVersion,
    libraryDependencies += "org.scalameta" % "scalafmt-dynamic_2.13" % scalafmtVersion,
    prepareISAReferences                  := Def.uncached {
      ReferenceArtifacts.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value / "isa-references",
        pins = ReferenceArtifacts.Pins(
          llvmVersion = llvmVersion,
          llvmSha256 = llvmSourceSha256,
          mesaVersion = mesaVersion,
          mesaSha256 = mesaSourceSha256,
          cudaPTXVersion = cudaPTXVersion,
          cudaPTXSha256 = cudaPTXHtmlSha256,
          cudaAPIVersion = cudaAPIVersion,
          cudaAPIPackage = cudaAPIPackage,
          cudaCRTPackage = cudaCRTPackage,
          cudaDriverDevSha256 = cudaDriverDevSha256,
          cudaRuntimeDevSha256 = cudaRuntimeDevSha256,
          cudaCRTDevSha256 = cudaCRTDevSha256,
          hipVersion = hipVersion,
          hipSha256 = hipSourceSha256,
          hsaVersion = hsaVersion,
          hsaSha256 = hsaSourceSha256,
          amdXMLVersion = amdISAXMLVersion,
          amdXMLSha256 = amdISAXMLSha256,
          openCLHeadersVersion = openCLHeadersVersion,
          openCLHeadersSha256 = openCLHeadersSha256,
          levelZeroVersion = levelZeroVersion,
          levelZeroSha256 = levelZeroSha256,
          spirvHeadersVersion = spirvHeadersVersion,
          spirvHeadersSha256 = spirvHeadersSha256,
          spirvToolsVersion = spirvToolsVersion,
          spirvToolsSha256 = spirvToolsSha256
        ),
        offline = sys.env.get("GEMSTONE_ISA_REFERENCES_OFFLINE").contains("1"),
        log = streams.value.log
      )
    },
    generateAPIValidationManifest := Def.uncached {
      val references = prepareISAReferences.value
      val output     = (LocalRootProject / baseDirectory).value / "api/src/generated/manifest/official-api-headers.json"
      val clang      = Generator.executable(generatorClang.value)
      Generator.run(
        "net.kurobako.gemstone.codegen.GenerateOfficialAPIManifest",
        generatorClasspath.value,
        Seq("--clang", clang.getAbsolutePath, references.getAbsolutePath, output.getAbsolutePath),
        Seq(
          references / "MANIFEST.tsv",
          references / "nvidia/cuda",
          references / "amd/hip",
          references / "amd/hsa",
          references / "khronos/opencl",
          references / "oneapi/level-zero"
        ),
        Seq(output),
        target.value / "generators/api-manifest",
        streams.value.log,
        tools = Seq(clang)
      )
      output
    },
    generateAPISources := Def.uncached {
      val root     = (LocalRootProject / baseDirectory).value
      val manifest = generateAPIValidationManifest.value
      val output   = (LocalRootProject / baseDirectory).value / "api/src/generated/main/scala"
      Generator.run(
        "net.kurobako.gemstone.codegen.GenerateAPISurfaces",
        generatorClasspath.value,
        Seq(
          manifest.getAbsolutePath,
          output.getAbsolutePath,
          cudaAPIVersion,
          hipVersion,
          hsaVersion,
          levelZeroVersion,
          openCLHeadersVersion
        ),
        Seq(manifest, root / ".scalafmt.conf"),
        (output ** "*Surface.scala").get(),
        target.value / "generators/api-surfaces",
        streams.value.log
      )
    }
  ).jvmPlatform(scalaVersions = Seq(scala3Version), settings = catsCoreJVMSettings)

lazy val rpcCodegen = project.in(file("codegen/rpc"))
  .dependsOn(api.jvm(scala3Version), codegen.jvm(scala3Version), qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    generatorSettings,
    name := "gemstone-rpc-codegen",
    Test / unmanagedResourceDirectories += (LocalRootProject / baseDirectory).value / "api/src/generated/manifest",
    generateRPCSources := Def.uncached {
      val root        = (LocalRootProject / baseDirectory).value
      val output      = root / "runtime/cuda/generated"
      val scalaOutput = root / "server/src/generated/main/scala"
      val manifest    = (codegen.jvm(scala3Version) / generateAPIValidationManifest).value
      Generator.run(
        "net.kurobako.gemstone.codegen.GenerateCUDABindings",
        generatorClasspath.value,
        Seq(output.getAbsolutePath, scalaOutput.getAbsolutePath, manifest.getAbsolutePath),
        Seq(manifest, root / ".scalafmt.conf"),
        (output ** "*").get().filter(_.isFile) ++ (scalaOutput ** "*.scala").get(),
        target.value / "generators/rpc",
        streams.value.log
      )
      (scalaOutput ** "*.scala").get()
    },
    checkAPISurfaces := Def.uncached {
      val manifest  = (LocalRootProject / baseDirectory).value / "api/src/generated/manifest/official-api-headers.json"
      val marker    = target.value / "generators/api-validation/complete"
      val classpath = generatorClasspath.value
      Generator.run(
        "net.kurobako.gemstone.codegen.CheckAPISurfaces",
        classpath,
        Seq(manifest.getAbsolutePath),
        Seq(manifest),
        { IO.write(marker, "validated"); Seq(marker) },
        target.value / "generators/api-validation",
        streams.value.log
      )
      ()
    },
    checkGeneratedAPISources := Def.uncached {
      val root    = (LocalRootProject / baseDirectory).value
      val outputs = (root / "runtime/cuda/generated" ** "*").get().filter(_.isFile) ++
        (root / "server/src/generated/main/scala" ** "*.scala").get()
      val manifest = root / "api/src/generated/manifest/official-api-headers.json"
      val marker   = target.value / "generators/rpc-check/complete"
      Generator.run(
        "net.kurobako.gemstone.codegen.GenerateCUDABindings",
        generatorClasspath.value,
        Seq(
          "--check",
          (root / "runtime/cuda/generated").getAbsolutePath,
          (root / "server/src/generated/main/scala").getAbsolutePath,
          manifest.getAbsolutePath
        ),
        outputs ++ Seq(manifest, root / ".scalafmt.conf"),
        { IO.write(marker, "validated"); Seq(marker) },
        target.value / "generators/rpc-check",
        streams.value.log
      )
      ()
    }
  )

lazy val core = projectMatrix
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, name := "gemstone-core")
  .jvmPlatform(scalaVersions = Seq(scala3Version), settings = catsCoreJVMSettings)
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = skipSharedSourceFormatting ++ catsCoreNativeSettings)

lazy val hsa = project.in(file("runtime/hsa"))
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, NativeBuild.settings, name := "gemstone-runtime-hsa")

lazy val hip = project.in(file("runtime/hip"))
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, NativeBuild.settings, name := "gemstone-runtime-hip")

lazy val levelzero = project.in(file("runtime/levelzero"))
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, NativeBuild.settings, name := "gemstone-runtime-levelzero")

lazy val opencl = project.in(file("runtime/opencl"))
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, NativeBuild.settings, name := "gemstone-runtime-opencl")

lazy val cuda = project.in(file("runtime/cuda"))
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    NativeBuild.settings,
    name                                 := "gemstone-runtime-cuda",
    NativeBuild.nativeConfigureArguments := Def.uncached {
      val _          = (rpcCodegen / generateRPCSources).value
      val references = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val headers    = (references / "nvidia/cuda" ** "cuda.h").get().filter { path =>
        val segments = path.toPath
        (0 until segments.getNameCount - 1).exists { index =>
          segments.getName(index).toString == s"cuda-$cudaAPIVersion" &&
          segments.getName(index + 1).toString == "targets"
        }
      }
      require(headers.size == 1, s"expected one pinned CUDA header, found ${headers.size}")
      Seq(s"-DCUDA_INCLUDE_DIR=${headers.head.getParent}")
    }
  )

lazy val ptx = projectMatrix
  .in(file("isa/ptx"))
  .dependsOn(core)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    Compile / sourceGenerators += (LocalProject("ptx") / generatePTXSources).taskValue,
    Test / sourceGenerators += (LocalProject("ptx") / generatePTXReferenceTests).taskValue,
    checkedInGeneratedSources("isa/ptx"),
    name               := "gemstone-isa-ptx",
    generatePTXSources := Def.uncached {
      val root       = (LocalRootProject / baseDirectory).value
      val references = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val input      = references / "nvidia" / "ptx" / "index.html"
      val output     = root / "isa" / "ptx" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "ptx" / ptxISAPackage / "Instruction.scala"
      Generator.run(
        "net.kurobako.gemstone.codegen.GeneratePTXISA",
        (codegen.jvm(scala3Version) / generatorClasspath).value,
        Seq(
          "instructions",
          input.getAbsolutePath,
          output.getAbsolutePath,
          ptxISAVersion,
          ptxISAPackage
        ),
        Seq(input, references / "MANIFEST.tsv", (LocalRootProject / baseDirectory).value / ".scalafmt.conf"),
        Seq(output),
        (codegen.jvm(scala3Version) / target).value / "generators/ptx-sources",
        streams.value.log
      )
    },
    generatePTXReferenceTests := Def.uncached {
      val root       = (LocalRootProject / baseDirectory).value
      val references = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val input      = references / "nvidia" / "ptx" / "index.html"
      val output     = root / "isa" / "ptx" / "src" / "generated" / "test" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "ptx" / ptxISAPackage / "DocumentationSuite.scala"
      Generator.run(
        "net.kurobako.gemstone.codegen.GeneratePTXISA",
        (codegen.jvm(scala3Version) / generatorClasspath).value,
        Seq(
          "tests",
          input.getAbsolutePath,
          output.getAbsolutePath,
          ptxISAVersion,
          ptxISAPackage
        ),
        Seq(input, references / "MANIFEST.tsv", (LocalRootProject / baseDirectory).value / ".scalafmt.conf"),
        Seq(output),
        (codegen.jvm(scala3Version) / target).value / "generators/ptx-tests",
        streams.value.log
      )
    }
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    settings = catsCoreJVMSettings ++ Seq(
      libraryDependencies += "org.typelevel" %% "cats-parse" % catsParseVersion,
      Test / unmanagedSourceDirectories +=
        (LocalRootProject / baseDirectory).value / "isa" / "ptx" / "src" / "test-jvm" / "scala"
    )
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = skipSharedSourceFormatting ++ catsCoreNativeSettings ++ Seq(
      libraryDependencies += "org.typelevel" % "cats-parse_native0.5_3" % catsParseVersion
    )
  )

lazy val amdgpu = projectMatrix
  .in(file("isa/amdgpu"))
  .dependsOn(core)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    Compile / sourceGenerators += (LocalProject("amdgpu") / generateAMDGPUSources).taskValue,
    Test / resourceGenerators += (LocalProject("amdgpu") / generateAMDGPUOracleResources).taskValue,
    checkedInGeneratedSources("isa/amdgpu"),
    name                  := "gemstone-isa-amdgpu",
    generateAMDGPUSources := Def.uncached {
      val root       = (LocalRootProject / baseDirectory).value
      val references = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val exporter   = root / "codegen" / "amdgpu" / "export_aco.py"
      val python     = Generator.executable((codegen.jvm(scala3Version) / generatorPython).value)
      val output = root / "isa" / "amdgpu" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "amdgpu" / "EncodingConstants.scala"
      val instructions  = output.getParentFile / "Instruction.scala"
      val architectures = instructions.getParentFile / "Architectures.scala"
      Generator.run(
        "net.kurobako.gemstone.codegen.GenerateAMDGPUISA",
        (codegen.jvm(scala3Version) / generatorClasspath).value,
        Seq(
          "--python",
          python.getAbsolutePath,
          references.getAbsolutePath,
          exporter.getAbsolutePath,
          output.getAbsolutePath
        ),
        Seq(
          references / "MANIFEST.tsv",
          references / "amd/xml",
          references / "mesa/src/amd/compiler",
          root / ".scalafmt.conf",
          exporter
        ),
        Seq(output, instructions, architectures) ++ ((instructions.getParentFile / "definitions") ** "*.scala").get(),
        (codegen.jvm(scala3Version) / target).value / "generators/amdgpu",
        streams.value.log,
        tools = Seq(python)
      )
    },
    generateAMDGPUOracleResources := Def.uncached {
      val root       = (LocalRootProject / baseDirectory).value
      val references = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val output     = root / "isa" / "amdgpu" / "src" / "generated" / "test" / "resources" / "net" / "kurobako" /
        "gemstone" / "isa" / "amdgpu" / "llvm-oracle"
      Generator.run(
        "net.kurobako.gemstone.codegen.GenerateAMDGPUOracle",
        (codegen.jvm(scala3Version) / generatorClasspath).value,
        Seq(
          references.getAbsolutePath,
          output.getAbsolutePath
        ),
        Seq(references / "MANIFEST.tsv", references / "llvm/llvm/test/MC/AMDGPU", root / ".scalafmt.conf"),
        Seq(output / "manifest.txt") ++ ((output * "*.s").get()),
        (codegen.jvm(scala3Version) / target).value / "generators/amdgpu-oracle",
        streams.value.log
      )
    }
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    settings = catsCoreJVMSettings ++ Seq(
      libraryDependencies += "org.typelevel" %% "cats-parse" % catsParseVersion,
      Test / unmanagedSourceDirectories +=
        (LocalRootProject / baseDirectory).value / "isa" / "amdgpu" / "src" / "test-jvm" / "scala"
    )
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = skipSharedSourceFormatting ++ catsCoreNativeSettings ++ Seq(
      libraryDependencies += "org.typelevel" % "cats-parse_native0.5_3" % catsParseVersion,
      // The LLVM-derived oracle is a JVM conformance gate. Native runs the focused handwritten suites, which exercise
      // the same shared parser, codec, validation, and execution implementation without compiling resource plumbing.
      Test / unmanagedSources / excludeFilter := "LLVMOracle*.scala"
    )
  )

lazy val spirv = projectMatrix
  .in(file("isa/spirv"))
  .dependsOn(core)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    Compile / sourceGenerators += (LocalProject("spirv") / generateSPIRVSources).taskValue,
    Test / sourceGenerators += (LocalProject("spirv") / generateSPIRVReferenceTests).taskValue,
    checkedInGeneratedSources("isa/spirv"),
    name                 := "gemstone-isa-spirv",
    generateSPIRVSources := Def.uncached {
      val root       = (LocalRootProject / baseDirectory).value
      val references = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val output = root / "isa" / "spirv" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "spirv" / spirvPackage / "Instruction.scala"
      val grammar = references / "khronos" / "spirv-headers" / "include" / "spirv" / "unified1" /
        "spirv.core.grammar.json"
      Generator.run(
        "net.kurobako.gemstone.codegen.GenerateSPIRVISA",
        (codegen.jvm(scala3Version) / generatorClasspath).value,
        Seq(
          "instructions",
          grammar.getAbsolutePath,
          output.getAbsolutePath,
          spirvPackage
        ),
        Seq(grammar, references / "MANIFEST.tsv", (LocalRootProject / baseDirectory).value / ".scalafmt.conf"),
        Seq(output),
        (codegen.jvm(scala3Version) / target).value / "generators/spirv-sources",
        streams.value.log
      )
    },
    generateSPIRVReferenceTests := Def.uncached {
      val root       = (LocalRootProject / baseDirectory).value
      val references = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val output = root / "isa" / "spirv" / "src" / "generated" / "test" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "spirv" / spirvPackage / "ReferenceSuite.scala"
      val grammar = references / "khronos" / "spirv-headers" / "include" / "spirv" / "unified1" /
        "spirv.core.grammar.json"
      val tests = references / "khronos" / "spirv-tools" / "test"
      Generator.run(
        "net.kurobako.gemstone.codegen.GenerateSPIRVISA",
        (codegen.jvm(scala3Version) / generatorClasspath).value,
        Seq("tests", grammar.getAbsolutePath, tests.getAbsolutePath, output.getAbsolutePath, spirvPackage),
        Seq(grammar, tests, references / "MANIFEST.tsv", root / ".scalafmt.conf"),
        Seq(output),
        (codegen.jvm(scala3Version) / target).value / "generators/spirv-tests",
        streams.value.log
      )
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version), settings = catsCoreJVMSettings)
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = skipSharedSourceFormatting ++ catsCoreNativeSettings
  )

lazy val server = projectMatrix
  .dependsOn(api, core, ptx, amdgpu, spirv)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources("server"),
    Compile / sourceGenerators += (rpcCodegen / generateRPCSources).taskValue,
    name                := "gemstone-server",
    Compile / mainClass := Some("net.kurobako.gemstone.server.GemstoneRPCServer")
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    settings = catsCoreJVMSettings ++ Seq(
      Compile / unmanagedSourceDirectories +=
        (LocalRootProject / baseDirectory).value / "server" / "src" / "main-jvm" / "scala",
      Test / unmanagedSourceDirectories +=
        (LocalRootProject / baseDirectory).value / "server" / "src" / "test-jvm" / "scala"
    )
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = skipSharedSourceFormatting ++ catsCoreNativeSettings ++ Seq(
      Compile / nativeConfig ~= (_.withMode(scala.scalanative.build.Mode.releaseFast)),
      Compile / unmanagedSourceDirectories +=
        (LocalRootProject / baseDirectory).value / "server" / "src" / "main-native" / "scala"
    )
  )

lazy val isa = project.in(file("isa"))
  .aggregate((ptx.projectRefs ++ amdgpu.projectRefs ++ spirv.projectRefs) *)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, name := "gemstone-isa")

lazy val runtime = project.in(file("runtime"))
  .aggregate(cuda, hip, hsa, levelzero, opencl)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, name := "gemstone-runtime")

lazy val conformance = project.in(file("conformance"))
  .dependsOn(server.jvm(scala3Version), qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    name                                 := "gemstone-conformance-support",
    publish / skip                       := true,
    libraryDependencies += "com.lihaoyi" %% "upickle" % upickleVersion % Test
  )

def nativeRuntimeDirectories = Def.task {
  Seq(
    "cuda"      -> (cuda / NativeBuild.nativeBuild).value,
    "hip"       -> (hip / NativeBuild.nativeBuild).value,
    "hsa"       -> (hsa / NativeBuild.nativeBuild).value,
    "opencl"    -> (opencl / NativeBuild.nativeBuild).value,
    "levelzero" -> (levelzero / NativeBuild.nativeBuild).value
  )
}

lazy val nativeRuntimeResources = taskKey[Seq[File]]("Write current native integration runtime paths.")

lazy val integrationSettings = Seq(
  publish / skip           := true,
  Test / parallelExecution := false,
  Test / fork              := true,
  Test / javaOptions ++= Def.uncached {
    val projectName = name.value
    sys.env.get("GEMSTONE_CONFORMANCE_REPORT_DIR").toSeq.map { directory =>
      IO.createDirectory(file(directory))
      s"-XX:StartFlightRecording=filename=$directory/$projectName.jfr,settings=profile,maxsize=256m,dumponexit=true"
    }
  },
  nativeRuntimeResources := Def.uncached {
    val output = (Test / resourceManaged).value / "native-runtimes.properties"
    NativeBuild.writeRuntimeProperties(output, nativeRuntimeDirectories.value)
    Seq(output)
  },
  Test / resourceGenerators += nativeRuntimeResources.taskValue
)

lazy val conformanceIntegration = project.in(file("conformance/integration"))
  .dependsOn(conformance % "test->test", qualityRules % ScalafixConfig)
  .settings(commonSettings, integrationSettings, name := "gemstone-conformance-integration")

lazy val nativeIntegration = project.in(file("integration/native"))
  .dependsOn(conformance % "test->test", qualityRules % ScalafixConfig)
  .settings(commonSettings, integrationSettings, name := "gemstone-native-integration")

lazy val integration = project.in(file("integration"))
  .aggregate(nativeIntegration, conformanceIntegration)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, name := "gemstone-integration", publish / skip := true)

lazy val gemstone = project.in(file("."))
  .aggregate(apiModel.projectRefs *)
  .aggregate(api.projectRefs *)
  .aggregate(codegen.projectRefs *)
  .aggregate(core.projectRefs *)
  .aggregate(server.projectRefs *)
  .aggregate(qualityRules, rpcCodegen, isa, runtime, conformance)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    name           := "gemstone",
    publish / skip := true,
    // Keep standard sbt output paths; root clean also owns shared fixture and report outputs.
    cleanFiles += baseDirectory.value / "target"
  )

addCommandAlias(
  "check",
  ";rpcCodegen/checkAPISurfaces;rpcCodegen/checkGeneratedAPISources;scalafmtCheckAll;integration/scalafmtCheckAll;scalafixAll --check;integration/scalafixAll --check;testFull;integration/testFull"
)
