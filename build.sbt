import scala.scalanative.build.BuildTarget
import scala.sys.process.Process

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val scala3Version        = "3.8.3"
lazy val munitVersion         = "1.3.4"
lazy val catsParseVersion     = "1.1.0"
lazy val jsoupVersion         = "1.23.2"
lazy val llvmVersion          = "23.1.0"
lazy val mesaVersion          = "26.2.1"
lazy val cudaPTXVersion       = "13.3.0"
lazy val ptxISAVersion        = "9.3"
lazy val ptxISAPackage        = "v" + ptxISAVersion.replace('.', '_')
lazy val amdISAXMLVersion     = "2026_08_06"
lazy val openCLHeadersVersion = "2026.05.29"
lazy val levelZeroVersion     = "1.33.0"
lazy val spirvHeadersVersion  = "vulkan-sdk-1.4.357.0"
lazy val spirvToolsVersion    = "vulkan-sdk-1.4.357.0"
lazy val spirvVersion         = "1.6"
lazy val spirvPackage         = "v" + spirvVersion.replace('.', '_')
lazy val thrustVersion        = "1.15.0"
lazy val thrustCommit         = "67d6622e48ea44cfaa462d44772cc7663026c952"

lazy val ThrustTest = config("thrust").extend(Test)

lazy val llvmSourceSha256    = "ab1f0e3ec52448c33e8782eaf0422504b87c7b016b22514653ee0d8fcee479ff"
lazy val mesaSourceSha256    = "c47e81bddc4760360a41ac3c5acec38acb81f9d750ecef47e7f3adc7021a4442"
lazy val cudaPTXHtmlSha256   = "40026f79b65fee3f8c8b3e717c6f80c6385e8d97215318d6e4b7f6106f58d5a4"
lazy val amdISAXMLSha256     = "82404f1126761b7877595b622afa7e1f311f2f41e89a3abe9aaf8ad045c082e2"
lazy val openCLHeadersSha256 = "0bdef6f653c577827a75fac3e3f18b0bf000171e39fa65c2eb84ec804c822cab"
lazy val levelZeroSha256     = "d697c61a2f4897ec9c676c6056dba5368c9c266bb173ff7249f71ef63bf445e0"
lazy val spirvHeadersSha256  = "7d40c8c1983c1aba6f6f2fcbcf7be345d33a5693aeee0cc5b8c579fb8e3f2fea"
lazy val spirvToolsSha256    = "09f70a9197d9cd28e1e10a2fca5ffde8c5e269f363eed9afa838ad88ed2649c9"

lazy val isaReferenceManifestMarkers = Vector(
  s"llvm-version\t$llvmVersion\n",
  s"mesa-version\t$mesaVersion\n",
  s"cuda-ptx-version\t$cudaPTXVersion\n",
  s"amd-xml-version\t$amdISAXMLVersion\n",
  s"opencl-headers-version\t$openCLHeadersVersion\n",
  s"level-zero-version\t$levelZeroVersion\n",
  s"spirv-headers-version\t$spirvHeadersVersion\n",
  s"spirv-tools-version\t$spirvToolsVersion\n"
)

lazy val isaReferenceCacheDirectory = settingKey[File]("Persistent cache for pinned ISA reference artifacts.")
lazy val prepareISAReferences       = taskKey[Unit]("Download, verify, and selectively extract pinned ISA references.")
@transient lazy val generatePTXSources        = taskKey[Seq[File]]("Update checked-in PTX instruction metadata.")
@transient lazy val generatePTXReferenceTests =
  taskKey[Seq[File]]("Update checked-in PTX documentation conformance tests.")
@transient lazy val generateAMDGPUSources     = taskKey[Seq[File]]("Update checked-in normalized AMDGPU metadata.")
@transient lazy val generateAMDGPUGoldenTests =
  taskKey[Seq[File]]("Update the checked-in LLVM AMDGPU golden-test corpus.")
@transient lazy val generateComputeAPISources =
  taskKey[Seq[File]]("Update checked-in OpenCL and Level Zero API surfaces.")
@transient lazy val generateSPIRVSources        = taskKey[Seq[File]]("Update checked-in SPIR-V grammar metadata.")
@transient lazy val generateSPIRVReferenceTests =
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
    "-language:noAutoTupling",
    "-source",
    "3.7"
  ),
  Compile / scalacOptions += "-Wunused:all",
  Test / scalacOptions ~= { _.filterNot(_ == "-Wunused:all") },
  scalafmtDetailedError                  := true,
  scalafmtFailOnErrors                   := true,
  libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
)

lazy val api = projectMatrix
  .settings(commonSettings, name := "gemstone-api")
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version))

lazy val codegen = projectMatrix
  .dependsOn(api)
  .settings(
    commonSettings,
    name                              := "gemstone-codegen",
    Compile / mainClass               := Some("net.kurobako.gemstone.codegen.GenerateAPI"),
    Compile / run / fork              := false,
    libraryDependencies += "org.jsoup" % "jsoup" % jsoupVersion,
    prepareISAReferences              := Def.uncached {
      val _ = ISAReferences.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value / "isa-references",
        pins = ISAReferences.Pins(
          llvmVersion = llvmVersion,
          llvmSha256 = llvmSourceSha256,
          mesaVersion = mesaVersion,
          mesaSha256 = mesaSourceSha256,
          cudaPTXVersion = cudaPTXVersion,
          cudaPTXSha256 = cudaPTXHtmlSha256,
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
    generateComputeAPISources := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = prepareISAReferences.value
      val referenceRoot = target.value / "isa-references"
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && isaReferenceManifestMarkers.forall(IO.read(manifest).contains)
      }.toVector match {
        case Vector(directory) => directory
        case found             => sys.error(s"expected one prepared compute API reference view, found ${found.size}")
      }
      val outputs = Vector(
        root / "api" / "src" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "opencl" / "OpenCL.scala",
        root / "api" / "src" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "levelzero" / "LevelZero.scala"
      )
      val converter = fileConverter.value
      val classpath = (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java      = file(sys.props("java.home")) / "bin" / "java"
      val exit      = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GenerateComputeAPIs",
        references.getAbsolutePath,
        outputs(0).getAbsolutePath,
        outputs(1).getAbsolutePath,
        openCLHeadersVersion,
        levelZeroVersion
      )).!
      require(exit == 0, s"compute API generator exited with $exit")
      outputs
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))

lazy val core = projectMatrix
  .dependsOn(api)
  .settings(
    commonSettings,
    name := "gemstone-core",
    Compile / unmanagedSourceDirectories += (LocalRootProject / baseDirectory).value / "generated" / "scala"
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version))

lazy val isa_ptx = projectMatrix
  .dependsOn(core)
  .settings(
    commonSettings,
    name               := "gemstone-isa-ptx",
    generatePTXSources := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && isaReferenceManifestMarkers.forall(IO.read(manifest).contains)
      }.toVector match {
        case Vector(directory) => directory
        case found => sys.error(s"expected one prepared ISA reference view, found ${found.size} in $referenceRoot")
      }
      val input  = references / "nvidia" / "ptx" / "index.html"
      val output = root / "isa_ptx" / "src" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "isa" / "ptx" /
        ptxISAPackage / "generated" / "Instructions.scala"
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
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && isaReferenceManifestMarkers.forall(IO.read(manifest).contains)
      }.toVector match {
        case Vector(directory) => directory
        case found => sys.error(s"expected one prepared ISA reference view, found ${found.size} in $referenceRoot")
      }
      val input  = references / "nvidia" / "ptx" / "index.html"
      val output = root / "isa_ptx" / "src" / "test" / "scala" / "net" / "kurobako" / "gemstone" / "isa" / "ptx" /
        ptxISAPackage / "generated" / "DocumentationSuite.scala"
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
    settings = Seq(libraryDependencies += "org.typelevel" %% "cats-parse" % catsParseVersion)
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = Seq(libraryDependencies += "org.typelevel" % "cats-parse_native0.5_3" % catsParseVersion)
  )

lazy val isa_amdgpu = projectMatrix
  .dependsOn(core)
  .settings(
    commonSettings,
    name                  := "gemstone-isa-amdgpu",
    generateAMDGPUSources := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && isaReferenceManifestMarkers.forall(IO.read(manifest).contains)
      }.toVector match {
        case Vector(directory) => directory
        case found => sys.error(s"expected one prepared ISA reference view, found ${found.size} in $referenceRoot")
      }
      val exporter = root / "codegen" / "amdgpu" / "export_aco.py"
      val output   = root / "isa_amdgpu" / "src" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "isa" /
        "amdgpu" / "common" / "generated" / "Database.scala"
      val instructions = output.getParentFile / "Instructions.scala"
      val converter    = fileConverter.value
      val classpath    =
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
      Seq(output, instructions)
    },
    generateAMDGPUGoldenTests := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && isaReferenceManifestMarkers.forall(IO.read(manifest).contains)
      }.toVector match {
        case Vector(directory) => directory
        case found => sys.error(s"expected one prepared ISA reference view, found ${found.size} in $referenceRoot")
      }
      val output = root / "isa_amdgpu" / "src" / "test" / "scala" / "net" / "kurobako" / "gemstone" / "isa" /
        "amdgpu" / "common" / "generated" / "GoldenCorpus.scala"
      val dataRoot = root / "isa_amdgpu" / "src" / "test" / "resources" / "net" / "kurobako" / "gemstone" / "isa" /
        "amdgpu" / "common" / "generated"
      val converter = fileConverter.value
      val classpath =
        (codegen.jvm(scala3Version) / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java = file(sys.props("java.home")) / "bin" / "java"
      val exit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GenerateAMDGPUGolden",
        references.getAbsolutePath,
        output.getAbsolutePath,
        dataRoot.getAbsolutePath
      )).!
      require(exit == 0, s"AMDGPU golden generator exited with $exit")
      Seq(output, dataRoot / "positive.tsv.gz", dataRoot / "diagnostics.tsv.gz")
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = Seq(
      // The exhaustive LLVM oracle is a JVM code-generation/conformance gate. Running 407k parser cases in an
      // unoptimised Native test binary adds minutes without exercising a different implementation.
      Test / unmanagedSources / excludeFilter := "GoldenCorpusSuite.scala"
    )
  )

lazy val isa_spirv = projectMatrix
  .dependsOn(core)
  .settings(
    commonSettings,
    name                 := "gemstone-isa-spirv",
    generateSPIRVSources := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareISAReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && isaReferenceManifestMarkers.forall(IO.read(manifest).contains)
      }.toVector match {
        case Vector(directory) => directory
        case found             => sys.error(s"expected one prepared SPIR-V reference view, found ${found.size}")
      }
      val output = root / "isa_spirv" / "src" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "isa" /
        "spirv" / spirvPackage / "generated" / "Instructions.scala"
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
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && isaReferenceManifestMarkers.forall(IO.read(manifest).contains)
      }.toVector match {
        case Vector(directory) => directory
        case found             => sys.error(s"expected one prepared SPIR-V test reference view, found ${found.size}")
      }
      val output = root / "isa_spirv" / "src" / "test" / "scala" / "net" / "kurobako" / "gemstone" / "isa" /
        "spirv" / spirvPackage / "generated" / "ReferenceSuite.scala"
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
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version))

lazy val server = projectMatrix
  .configs(ThrustTest)
  .dependsOn(api, core, isa_ptx, isa_amdgpu, isa_spirv)
  .settings(
    commonSettings,
    inConfig(ThrustTest)(Defaults.testSettings),
    name                     := "gemstone-server",
    ThrustTest / scalaSource := (LocalRootProject / baseDirectory).value / "server" / "src" / "thrust" / "scala",
    ThrustTest / resourceDirectory := (LocalRootProject / baseDirectory).value / "server" / "src" / "thrust" / "resources",
    ThrustTest / parallelExecution         := false,
    ThrustTest / test / fork               := false,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % ThrustTest
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))

addCommandAlias("testThrust", "server / Thrust / testOnly net.kurobako.gemstone.server.ThrustConformanceSuite")

lazy val native = projectMatrix
  .dependsOn(core, isa_ptx, isa_amdgpu, isa_spirv)
  .settings(
    commonSettings,
    name         := "gemsi-core",
    nativeConfig := {
      val exportMap = (LocalRootProject / baseDirectory).value / "native" / "core-exports.map"
      nativeConfig.value
        .withBuildTarget(BuildTarget.libraryDynamic)
        .withMultithreading(true)
        .withLinkingOptions(Seq(s"-Wl,--version-script=${exportMap.getAbsolutePath}"))
    }
  )
  .nativePlatform(scalaVersions = Seq(scala3Version))
