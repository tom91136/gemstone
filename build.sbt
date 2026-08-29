import scala.sys.process.Process

Global / onChangedBuildSource := ReloadOnSourceChanges

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
lazy val rocThrustCommit      = "ed551d53d9bac8fd55b2a7fc62683946e33060a0"
lazy val rocThrustSha256      = "e9b656bc5ab7568b07347825c4fc00d2940c1ca31ee4926e5862b0f6b9fb31e4"
lazy val rocmExamplesCommit   = "143ead779346e6e7179100cf7c56d9f3562554ae"
lazy val rocmExamplesSha256   = "566b19aedd133dc913cb25d4b97c60f01ef24f03668e86c4d45ca855e97f14cd"
lazy val oneDPLCommit         = "3e0e05b32bf53850816f6a6bb09bcfded4f7edb7"
lazy val oneDPLSha256         = "ce130630653c88a49844409a4ab307f54d2e5ace356499819556eb4d9c6106e3"
lazy val googleTestVersion    = "release-1.11.0"
lazy val googleTestSha256     = "b4870bf121ff7795ba20d20bcdd8627b8e088f2d1dab299a031c1034eddc93d5"
lazy val openCLCTSVersion     = "v2026-07-09-00"
lazy val openCLCTSSha256      = "019a552f018b78e205baa010e8365db7dcd7bc22538cc79fc72ecbd1fcf7055d"
lazy val ThrustTest = config("thrust").extend(Test)

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

lazy val isaReferenceManifestMarkers = Vector(
  "format\t2\n",
  s"llvm-version\t$llvmVersion\n",
  s"mesa-version\t$mesaVersion\n",
  s"cuda-ptx-version\t$cudaPTXVersion\n",
  s"cuda-api-version\t$cudaAPIVersion\n",
  s"cuda-api-package\t$cudaAPIPackage\n",
  s"cuda-crt-package\t$cudaCRTPackage\n",
  s"hip-version\t$hipVersion\n",
  s"hsa-version\t$hsaVersion\n",
  s"amd-xml-version\t$amdISAXMLVersion\n",
  s"opencl-headers-version\t$openCLHeadersVersion\n",
  s"level-zero-version\t$levelZeroVersion\n",
  s"spirv-headers-version\t$spirvHeadersVersion\n",
  s"spirv-tools-version\t$spirvToolsVersion\n"
)

lazy val isaReferenceCacheDirectory = settingKey[File]("Persistent cache for pinned ISA reference artifacts.")
lazy val prepareISAReferences       = taskKey[Unit]("Download, verify, and selectively extract pinned ISA references.")
lazy val generateAPIValidationManifest =
  taskKey[File]("Record normalized declarations from pinned official API headers.")
lazy val validateAPISurfaces = taskKey[Unit]("Validate checked-in API declarations against pinned official headers.")
lazy val checkGeneratedAPISources      = taskKey[Unit]("Verify checked-in generated ABI and RPC files are current.")
lazy val generatePTXSources = taskKey[Seq[File]]("Update checked-in PTX instruction metadata.")
lazy val generatePTXReferenceTests =
  taskKey[Seq[File]]("Update checked-in PTX documentation conformance tests.")
lazy val generateAMDGPUSources = taskKey[Seq[File]]("Update checked-in normalized AMDGPU metadata.")
lazy val generateAMDGPUOracle  =
  taskKey[Seq[File]]("Update the checked-in, bounded LLVM AMDGPU oracle fixtures.")
lazy val generateAPISources =
  taskKey[Seq[File]]("Bootstrap checked-in CUDA, HIP, HSA, Level Zero, and OpenCL API surfaces.")
lazy val generateSPIRVSources = taskKey[Seq[File]]("Update checked-in SPIR-V grammar metadata.")
lazy val generateSPIRVReferenceTests =
  taskKey[Seq[File]]("Update checked-in SPIRV-Tools-derived tests.")

Global / isaReferenceCacheDirectory := {
  val cacheRoot = sys.env.get("XDG_CACHE_HOME").filter(_.nonEmpty).map(file)
    .getOrElse(file(sys.props("user.home")) / ".cache")
  cacheRoot / "gemstone" / "isa-references"
}

def preparedReferenceView(referenceRoot: File, purpose: String): File =
  (referenceRoot * DirectoryFilter).get().filter { directory =>
    val manifest = directory / "MANIFEST.tsv"
    manifest.isFile && isaReferenceManifestMarkers.forall(IO.read(manifest).contains)
  }.toVector match {
    case Vector(directory) => directory
    case found             => sys.error(s"expected one prepared $purpose reference view, found ${found.size}")
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

lazy val api = projectMatrix
  .dependsOn(core)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources("api"),
    name        := "gemstone-api",
    Test / test := (Test / test)
      .dependsOn(
        LocalProject("codegen") / validateAPISurfaces,
        LocalProject("codegen") / checkGeneratedAPISources
      )
      .evaluated,
    Test / testFull := Def.uncached {
      (Test / testFull)
        .dependsOn(
          LocalProject("codegen") / validateAPISurfaces,
          LocalProject("codegen") / checkGeneratedAPISources
        )
        .value
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version), settings = catsCoreJVMSettings)
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = skipSharedSourceFormatting ++ catsCoreNativeSettings
  )

lazy val codegen = projectMatrix
  .dependsOn(api)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    name                                  := "gemstone-codegen",
    scalafixConfig                        := Some(file("quality/codegen.scalafix.conf")),
    Compile / mainClass                   := Some("net.kurobako.gemstone.codegen.GenerateAPI"),
    Compile / run / fork                  := false,
    Test / unmanagedResourceDirectories +=
      (LocalRootProject / baseDirectory).value / "api" / "src" / "generated" / "manifest",
    libraryDependencies += "org.jsoup"     % "jsoup"                 % jsoupVersion,
    libraryDependencies += "com.lihaoyi"  %% "upickle"               % upickleVersion,
    libraryDependencies += "org.scalameta" % "scalafmt-dynamic_2.13" % scalafmtVersion,
    prepareISAReferences := Def.uncached {
      val _ = ReferenceArtifacts.prepare(
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
    generateAPISources := Def.uncached {
      val root     = (LocalRootProject / baseDirectory).value
      val manifest = generateAPIValidationManifest.value
      val outputs  = Vector(
        root / "api" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "cuda" / "CUDASurface.scala",
        root / "api" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "hip" / "HIPSurface.scala",
        root / "api" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "hsa" / "HSASurface.scala",
        root / "api" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "opencl" / "OpenCLSurface.scala",
        root / "api" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "levelzero" / "LevelZeroSurface.scala"
      )
      val sourceRoot = root / "api" / "src" / "generated" / "main" / "scala"
      val converter  = fileConverter.value
      val classpath  = (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java       = file(sys.props("java.home")) / "bin" / "java"
      val exit       = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GenerateAPISurfaces",
        manifest.getAbsolutePath,
        sourceRoot.getAbsolutePath,
        cudaAPIVersion,
        hipVersion,
        hsaVersion,
        levelZeroVersion,
        openCLHeadersVersion
      )).!
      require(exit == 0, s"API surface bootstrap exited with $exit")
      outputs
    },
    generateAPIValidationManifest := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = prepareISAReferences.value
      val referenceRoot = target.value / "isa-references"
      val references    = preparedReferenceView(referenceRoot, "API")
      val output        = root / "api" / "src" / "generated" / "manifest" / "official-api-headers.json"
      val converter     = fileConverter.value
      val classpath     = (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java          = file(sys.props("java.home")) / "bin" / "java"
      val exit          = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.ValidateAPIs",
        references.getAbsolutePath,
        output.getAbsolutePath
      )).!
      require(exit == 0, s"API header probe exited with $exit")
      output
    },
    validateAPISurfaces := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = prepareISAReferences.value
      val referenceRoot = target.value / "isa-references"
      val references    = preparedReferenceView(referenceRoot, "API")
      val output        = root / "api" / "src" / "generated" / "manifest" / "official-api-headers.json"
      val converter     = fileConverter.value
      val classpath     = (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java          = file(sys.props("java.home")) / "bin" / "java"
      val exit          = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.ValidateAPIs",
        "--check",
        references.getAbsolutePath,
        output.getAbsolutePath
      )).!
      require(exit == 0, s"API header validation exited with $exit")
      val scalaExit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.ValidateScalaAPIs",
        output.getAbsolutePath
      )).!
      require(scalaExit == 0, s"Scala API validation exited with $scalaExit")
      val surfaceExit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GenerateAPISurfaces",
        "--check",
        output.getAbsolutePath,
        (root / "api" / "src" / "generated" / "main" / "scala").getAbsolutePath,
        cudaAPIVersion,
        hipVersion,
        hsaVersion,
        levelZeroVersion,
        openCLHeadersVersion
      )).!
      require(surfaceExit == 0, s"generated Scala API surface validation exited with $surfaceExit")
    },
    checkGeneratedAPISources := Def.uncached {
      val root      = (LocalRootProject / baseDirectory).value
      val converter = fileConverter.value
      val classpath = (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java      = file(sys.props("java.home")) / "bin" / "java"
      val exit      = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GenerateAPI",
        "--check",
        (root / "runtime" / "cuda" / "generated").getAbsolutePath,
        (root / "api" / "src" / "generated" / "main" / "scala").getAbsolutePath,
        (root / "api" / "src" / "generated" / "manifest" / "official-api-headers.json").getAbsolutePath
      )).!
      require(exit == 0, s"generated API validation exited with $exit")
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version), settings = catsCoreJVMSettings)

lazy val core = projectMatrix
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    name := "gemstone-core"
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version), settings = catsCoreJVMSettings)
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = skipSharedSourceFormatting ++ catsCoreNativeSettings
  )

lazy val hsa = project
  .in(file("runtime/hsa"))
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    name := "gemstone-runtime-hsa",
    CMakeTest.ctest := Def.uncached {
      CMakeTest.run(baseDirectory.value, target.value / "cmake", Seq("-DBUILD_TESTING=ON"), streams.value.log)
    },
    Test / test := (Test / test).dependsOn(CMakeTest.ctest).evaluated,
    Test / testFull := Def.uncached {
      (Test / testFull).dependsOn(CMakeTest.ctest).value
    }
  )

lazy val levelzero = project
  .in(file("runtime/levelzero"))
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    name := "gemstone-runtime-levelzero",
    CMakeTest.ctest := Def.uncached {
      CMakeTest.run(baseDirectory.value, target.value / "cmake", Seq("-DBUILD_TESTING=ON"), streams.value.log)
    },
    Test / test := (Test / test).dependsOn(CMakeTest.ctest).evaluated,
    Test / testFull := Def.uncached {
      (Test / testFull).dependsOn(CMakeTest.ctest).value
    }
  )

lazy val ptx = projectMatrix
  .in(file("isa/ptx"))
  .dependsOn(core)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    checkedInGeneratedSources("isa/ptx"),
    name               := "gemstone-isa-ptx",
    generatePTXSources := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = preparedReferenceView(referenceRoot, "ISA")
      val input         = references / "nvidia" / "ptx" / "index.html"
      val output = root / "isa" / "ptx" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "ptx" / ptxISAPackage / "Instruction.scala"
      val converter = fileConverter.value
      val classpath = (codegen.jvm(scala3Version) / Compile / fullClasspath).value
        .map(entry => converter.toPath(entry.data).toString)
      val java = file(sys.props("java.home")) / "bin" / "java"
      val exit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GeneratePTXISA",
        "instructions",
        input.getAbsolutePath,
        output.getAbsolutePath,
        ptxISAVersion,
        ptxISAPackage
      )).!
      require(exit == 0, s"PTX metadata generator exited with $exit")
      Seq(output)
    },
    generatePTXReferenceTests := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = preparedReferenceView(referenceRoot, "ISA")
      val input         = references / "nvidia" / "ptx" / "index.html"
      val output = root / "isa" / "ptx" / "src" / "generated" / "test" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "ptx" / ptxISAPackage / "DocumentationSuite.scala"
      val converter = fileConverter.value
      val classpath = (codegen.jvm(scala3Version) / Compile / fullClasspath).value
        .map(entry => converter.toPath(entry.data).toString)
      val java = file(sys.props("java.home")) / "bin" / "java"
      val exit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GeneratePTXISA",
        "tests",
        input.getAbsolutePath,
        output.getAbsolutePath,
        ptxISAVersion,
        ptxISAPackage
      )).!
      require(exit == 0, s"PTX reference-test generator exited with $exit")
      Seq(output)
    }
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    settings = catsCoreJVMSettings ++ Seq(
      libraryDependencies += "org.typelevel" %% "cats-parse" % catsParseVersion
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
    checkedInGeneratedSources("isa/amdgpu"),
    name                  := "gemstone-isa-amdgpu",
    generateAMDGPUSources := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = preparedReferenceView(referenceRoot, "ISA")
      val exporter      = root / "codegen" / "amdgpu" / "export_aco.py"
      val output = root / "isa" / "amdgpu" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "amdgpu" / "EncodingConstants.scala"
      val instructions  = output.getParentFile / "Instruction.scala"
      val architectures = instructions.getParentFile / "Architectures.scala"
      val converter     = fileConverter.value
      val classpath     =
        (codegen.jvm(scala3Version) / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java = file(sys.props("java.home")) / "bin" / "java"
      val exit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GenerateAMDGPUISA",
        references.getAbsolutePath,
        exporter.getAbsolutePath,
        output.getAbsolutePath
      )).!
      require(exit == 0, s"AMDGPU metadata generator exited with $exit")
      Seq(output, instructions, architectures) ++ ((instructions.getParentFile / "definitions") ** "*.scala").get()
    },
    generateAMDGPUOracle := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = preparedReferenceView(referenceRoot, "ISA")
      val output        = root / "isa" / "amdgpu" / "src" / "generated" / "test" / "resources" / "net" / "kurobako" /
        "gemstone" / "isa" / "amdgpu" / "llvm-oracle"
      val converter = fileConverter.value
      val classpath =
        (codegen.jvm(scala3Version) / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java = file(sys.props("java.home")) / "bin" / "java"
      val exit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GenerateAMDGPUOracle",
        references.getAbsolutePath,
        output.getAbsolutePath
      )).!
      require(exit == 0, s"AMDGPU oracle generator exited with $exit")
      Seq(output / "manifest.txt") ++ ((output * "*.s").get())
    }
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    settings = catsCoreJVMSettings ++ Seq(
      libraryDependencies += "org.typelevel" %% "cats-parse" % catsParseVersion
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
    checkedInGeneratedSources("isa/spirv"),
    name                 := "gemstone-isa-spirv",
    generateSPIRVSources := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = preparedReferenceView(referenceRoot, "SPIR-V")
      val output = root / "isa" / "spirv" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "spirv" / spirvPackage / "Instruction.scala"
      val converter = fileConverter.value
      val classpath = (codegen.jvm(scala3Version) / Compile / fullClasspath).value
        .map(entry => converter.toPath(entry.data).toString)
      val java    = file(sys.props("java.home")) / "bin" / "java"
      val grammar = references / "khronos" / "spirv-headers" / "include" / "spirv" / "unified1" /
        "spirv.core.grammar.json"
      val exit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GenerateSPIRVISA",
        "instructions",
        grammar.getAbsolutePath,
        output.getAbsolutePath,
        spirvPackage
      )).!
      require(exit == 0, s"SPIR-V metadata generator exited with $exit")
      Seq(output)
    },
    generateSPIRVReferenceTests := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = preparedReferenceView(referenceRoot, "SPIR-V test")
      val output = root / "isa" / "spirv" / "src" / "generated" / "test" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "spirv" / spirvPackage / "ReferenceSuite.scala"
      val converter = fileConverter.value
      val classpath = (codegen.jvm(scala3Version) / Compile / fullClasspath).value
        .map(entry => converter.toPath(entry.data).toString)
      val java    = file(sys.props("java.home")) / "bin" / "java"
      val grammar = references / "khronos" / "spirv-headers" / "include" / "spirv" / "unified1" /
        "spirv.core.grammar.json"
      val tests = references / "khronos" / "spirv-tools" / "test"
      val exit  = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GenerateSPIRVISA",
        "tests",
        grammar.getAbsolutePath,
        tests.getAbsolutePath,
        output.getAbsolutePath,
        spirvPackage
      )).!
      require(exit == 0, s"SPIR-V reference-test generator exited with $exit")
      Seq(output)
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
    name := "gemstone-server"
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
    settings = skipSharedSourceFormatting ++ catsCoreNativeSettings
  )

lazy val cuda = project
  .in(file("runtime/cuda"))
  .configs(ThrustTest)
  .dependsOn(server.jvm(scala3Version), qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    inConfig(ThrustTest)(Defaults.testSettings),
    org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings(ThrustTest),
    name                           := "gemstone-runtime-cuda",
    ThrustTest / scalaSource       := baseDirectory.value / "src" / "thrust" / "scala",
    ThrustTest / resourceDirectory := baseDirectory.value / "src" / "thrust" / "resources",
    ThrustTest / parallelExecution := true,
    ThrustTest / test / fork       := false,
    ThrustTest / scalafixConfig    := Some(file("quality/test.scalafix.conf")),
    Test / test                    := (Test / test)
      .dependsOn(
        codegen.jvm(scala3Version) / prepareISAReferences,
        codegen.jvm(scala3Version) / checkGeneratedAPISources
      )
      .evaluated,
    Test / testFull := Def.uncached {
      (Test / testFull)
        .dependsOn(
          codegen.jvm(scala3Version) / prepareISAReferences,
          codegen.jvm(scala3Version) / checkGeneratedAPISources
        )
        .value
    },
    ThrustTest / test := (ThrustTest / test)
      .dependsOn(
        codegen.jvm(scala3Version) / prepareISAReferences,
        codegen.jvm(scala3Version) / checkGeneratedAPISources
      )
      .evaluated,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % ThrustTest
  )

lazy val isa = project
  .in(file("isa"))
  .aggregate((ptx.projectRefs ++ amdgpu.projectRefs ++ spirv.projectRefs) *)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, name := "gemstone-isa")

lazy val runtime = project
  .in(file("runtime"))
  .aggregate(cuda, hsa, levelzero)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(commonSettings, name := "gemstone-runtime")

lazy val conformance = project
  .in(file("conformance"))
  .dependsOn(server.jvm(scala3Version))
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    name                     := "gemstone-conformance",
    Test / parallelExecution := true,
    Test / test / fork       := false,
    Test / resourceGenerators += Def.task {
      val output = (Test / resourceManaged).value / "gemstone-conformance.properties"
      IO.writeLines(
        output,
        Seq(
          s"rocthrust.commit=$rocThrustCommit",
          s"rocthrust.sha256=$rocThrustSha256",
          s"rocm-examples.commit=$rocmExamplesCommit",
          s"rocm-examples.sha256=$rocmExamplesSha256",
          s"onedpl.commit=$oneDPLCommit",
          s"onedpl.sha256=$oneDPLSha256",
          s"googletest.version=$googleTestVersion",
          s"googletest.sha256=$googleTestSha256",
          s"opencl-cts.version=$openCLCTSVersion",
          s"opencl-cts.sha256=$openCLCTSSha256",
          s"opencl-headers.version=$openCLHeadersVersion",
          s"opencl-headers.sha256=$openCLHeadersSha256",
          s"spirv-headers.version=$spirvHeadersVersion",
          s"spirv-headers.sha256=$spirvHeadersSha256"
        )
      )
      Seq(output)
    }.taskValue,
    libraryDependencies += "com.lihaoyi" %% "upickle" % upickleVersion % Test
  )

lazy val gemstone = project
  .in(file("."))
  .aggregate(api.projectRefs *)
  .aggregate(codegen.projectRefs *)
  .aggregate(core.projectRefs *)
  .aggregate(server.projectRefs *)
  .aggregate(qualityRules, isa, runtime)
  .dependsOn(qualityRules % ScalafixConfig)
  .settings(
    commonSettings,
    name           := "gemstone",
    publish / skip := true
  )

addCommandAlias("check", ";scalafmtCheckAll;scalafixAll --check;testFull")
addCommandAlias(
  "testAPIConformance",
  ";codegen / Test / testOnly net.kurobako.gemstone.codegen.APIConformanceSuite"
)
addCommandAlias(
  "testCUDAConformance",
  ";cuda / testFull;cuda / Thrust / testFull"
)
addCommandAlias(
  "testROCmConformance",
  ";hsa / testFull;conformance / Test / testOnly net.kurobako.gemstone.conformance.RocThrustConformanceSuite"
)
addCommandAlias(
  "testSYCLConformance",
  ";levelzero / testFull;conformance / Test / testOnly net.kurobako.gemstone.conformance.OneDPLConformanceSuite"
)
addCommandAlias(
  "testOpenCLConformance",
  ";conformance / Test / testOnly net.kurobako.gemstone.conformance.OpenCLCTSConformanceSuite"
)
addCommandAlias(
  "testConformance",
  ";testAPIConformance;testCUDAConformance;testROCmConformance;testOpenCLConformance;testSYCLConformance"
)
