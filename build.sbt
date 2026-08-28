import scala.sys.process.Process

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val scala3Version        = "3.8.3"
// Bump whenever Col's backing type changes so cached JVM/Native products cannot retain the previous ABI.
lazy val runtimeCollectionABI = "iarray-v1"
lazy val munitVersion         = "1.3.4"
lazy val catsParseVersion     = "1.1.0"
lazy val jsoupVersion         = "1.23.2"
lazy val upickleVersion       = "4.4.3"
lazy val scalafmtVersion      = "3.9.8"
lazy val llvmVersion          = "23.1.0"
lazy val mesaVersion          = "26.2.1"
lazy val cudaPTXVersion       = "13.3.0"
lazy val cudaAPIVersion       = "13.3"
lazy val cudaAPIPackage       = "13.3.29-1"
lazy val cudaCRTPackage       = "13.3.33-1"
lazy val hipVersion           = "7.2.3"
lazy val hsaVersion           = "7.2.3"
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
lazy val cudaDriverDevSha256 = "8bf25d2a822c107afea871c3f521014b55af095d7ac80d56dda27a7599f0fc70"
lazy val cudaRuntimeDevSha256 = "0c137b804e1b68879b873ce996f23f6ca9bc644872be15cb870bdbc43319a717"
lazy val cudaCRTDevSha256 = "945852c041126c913c0b1ed9fa8d231550fcaf5a3e05962ecd4669807a890f66"
lazy val hipSourceSha256 = "e6ab65cb2a236eee0e1f2738457367dffc3ce1e8dfb050ac22b7712e35aa896e"
lazy val hsaSourceSha256 = "fa673a8449be0853acaceb139b6b87abbc66c050b6e92745c26445722725cb05"
lazy val amdISAXMLSha256     = "82404f1126761b7877595b622afa7e1f311f2f41e89a3abe9aaf8ad045c082e2"
lazy val openCLHeadersSha256 = "0bdef6f653c577827a75fac3e3f18b0bf000171e39fa65c2eb84ec804c822cab"
lazy val levelZeroSha256     = "d697c61a2f4897ec9c676c6056dba5368c9c266bb173ff7249f71ef63bf445e0"
lazy val spirvHeadersSha256  = "7d40c8c1983c1aba6f6f2fcbcf7be345d33a5693aeee0cc5b8c579fb8e3f2fea"
lazy val spirvToolsSha256    = "09f70a9197d9cd28e1e10a2fca5ffde8c5e269f363eed9afa838ad88ed2649c9"

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
lazy val generateAPIValidationManifest = taskKey[File]("Record normalized declarations from pinned official API headers.")
lazy val validateAPISurfaces = taskKey[Unit]("Validate checked-in API declarations against pinned official headers.")
lazy val checkGeneratedAPISources = taskKey[Unit]("Verify checked-in generated ABI and RPC files are current.")
lazy val checkRuntimeCollections =
  taskKey[Unit]("Reject direct runtime uses of Vector, List, Seq, or IArray outside Col.")
@transient lazy val generatePTXSources        = taskKey[Seq[File]]("Update checked-in PTX instruction metadata.")
@transient lazy val generatePTXReferenceTests =
  taskKey[Seq[File]]("Update checked-in PTX documentation conformance tests.")
@transient lazy val generateAMDGPUSources     = taskKey[Seq[File]]("Update checked-in normalized AMDGPU metadata.")
@transient lazy val generateAMDGPUGoldenTests =
  taskKey[Seq[File]]("Update the checked-in LLVM AMDGPU golden-test corpus.")
@transient lazy val generateAPISources =
  taskKey[Seq[File]]("Bootstrap checked-in CUDA, HIP, HSA, Level Zero, and OpenCL API surfaces.")
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
    "-Yretain-trees",
    s"-Xmacro-settings:gemstone.runtime-collection=$runtimeCollectionABI",
    "-language:noAutoTupling",
    "-source",
    "3.7"
  ),
  Compile / scalacOptions += "-Wunused:all",
  Test / scalacOptions ~= { _.filterNot(_ == "-Wunused:all") },
  // Keep inter-project classpaths directory-backed. sbt 2's exported JAR cache can
  // retain products whose package changed, which is especially harmful to Native linking.
  Compile / exportJars                   := false,
  scalafmtDetailedError                  := true,
  scalafmtFailOnErrors                   := true,
  libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test
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

lazy val api = projectMatrix
  .dependsOn(core)
  .settings(
    commonSettings,
    checkedInGeneratedSources("api"),
    name := "gemstone-api",
    Test / test := (Test / test)
      .dependsOn(
        LocalProject("codegen") / validateAPISurfaces,
        LocalProject("codegen") / checkGeneratedAPISources
      )
      .evaluated
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = skipSharedSourceFormatting)

lazy val codegen = projectMatrix
  .dependsOn(api)
  .settings(
    commonSettings,
    name                              := "gemstone-codegen",
    Compile / mainClass               := Some("net.kurobako.gemstone.codegen.GenerateAPI"),
    Compile / run / fork              := false,
    libraryDependencies += "org.jsoup" % "jsoup" % jsoupVersion,
    libraryDependencies += "com.lihaoyi" %% "upickle" % upickleVersion,
    libraryDependencies += "org.scalameta" % "scalafmt-dynamic_2.13" % scalafmtVersion,
    Compile / sourceGenerators += Def.task {
      val root = (LocalRootProject / baseDirectory).value
      val apiSources = Vector(root / "api" / "src" / "main" / "scala", root / "api" / "src" / "generated" / "main" / "scala")
        .flatMap(directory => (directory ** "*.scala").get())
        .sortBy(_.getAbsolutePath)
      val digest = java.security.MessageDigest.getInstance("SHA-256")
      apiSources.foreach { source =>
        digest.update(source.getAbsolutePath.getBytes(java.nio.charset.StandardCharsets.UTF_8))
        digest.update(IO.readBytes(source))
      }
      val fingerprint = digest.digest().iterator.map(byte => f"${byte & 0xff}%02x").mkString
      val output = (Compile / sourceManaged).value / "net" / "kurobako" / "gemstone" / "codegen" /
        "APISourceFingerprint.scala"
      IO.write(
        output,
        s"""|package net.kurobako.gemstone.codegen
            |
            |private[gemstone] object APISourceFingerprint {
            |  inline val Value = "$fingerprint"
            |}
            |""".stripMargin
      )
      Seq(output)
    }.taskValue,
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
      val outputs = Vector(
        root / "api" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "cuda" / "CUDASurface.scala",
        root / "api" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "hip" / "HIPSurface.scala",
        root / "api" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "hsa" / "HSASurface.scala",
        root / "api" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "opencl" / "OpenCLSurface.scala",
        root / "api" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "levelzero" / "LevelZeroSurface.scala"
      )
      val sourceRoot = root / "api" / "src" / "generated" / "main" / "scala"
      val converter = fileConverter.value
      val classpath = (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java      = file(sys.props("java.home")) / "bin" / "java"
      val exit      = Process(Seq(
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
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && isaReferenceManifestMarkers.forall(IO.read(manifest).contains)
      }.toVector match {
        case Vector(directory) => directory
        case found             => sys.error(s"expected one prepared API reference view, found ${found.size}")
      }
      val output    = root / "generated" / "manifest" / "official-api-headers.json"
      val converter = fileConverter.value
      val classpath = (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java      = file(sys.props("java.home")) / "bin" / "java"
      val exit = Process(Seq(
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
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && isaReferenceManifestMarkers.forall(IO.read(manifest).contains)
      }.toVector match {
        case Vector(directory) => directory
        case found             => sys.error(s"expected one prepared API reference view, found ${found.size}")
      }
      val output    = root / "generated" / "manifest" / "official-api-headers.json"
      val converter = fileConverter.value
      val classpath = (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java      = file(sys.props("java.home")) / "bin" / "java"
      val exit = Process(Seq(
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
        (root / "generated").getAbsolutePath,
        (root / "api" / "src" / "generated" / "main" / "scala").getAbsolutePath
      )).!
      require(exit == 0, s"generated API validation exited with $exit")
    },
    checkRuntimeCollections := Def.uncached {
      val root      = (LocalRootProject / baseDirectory).value
      val converter = fileConverter.value
      val classpath = (Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java      = file(sys.props("java.home")) / "bin" / "java"
      val exit      = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.AuditRuntimeCollections",
        root.getAbsolutePath
      )).!
      require(exit == 0, s"runtime collection audit exited with $exit")
    }
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))

lazy val core = projectMatrix
  .settings(
    commonSettings,
    name := "gemstone-core",
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = skipSharedSourceFormatting)

lazy val isa_ptx = projectMatrix
  .dependsOn(core)
  .settings(
    commonSettings,
    checkedInGeneratedSources("isa_ptx"),
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
      val output = root / "isa_ptx" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "ptx" / ptxISAPackage / "Instructions.scala"
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
      val output = root / "isa_ptx" / "src" / "generated" / "test" / "scala" / "net" / "kurobako" / "gemstone" /
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
    settings = Seq(libraryDependencies += "org.typelevel" %% "cats-parse" % catsParseVersion)
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = skipSharedSourceFormatting ++ Seq(
      libraryDependencies += "org.typelevel" % "cats-parse_native0.5_3" % catsParseVersion
    )
  )

lazy val isa_amdgpu = projectMatrix
  .dependsOn(core)
  .settings(
    commonSettings,
    checkedInGeneratedSources("isa_amdgpu"),
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
      val output = root / "isa_amdgpu" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "amdgpu" / "common" / "EncodingConstants.scala"
      val instructions = output.getParentFile / "Instructions.scala"
      val architectures = instructions.getParentFile.getParentFile / "Architectures.scala"
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
      Seq(output, instructions, architectures) ++ ((instructions.getParentFile / "definitions") * "*.scala").get()
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
      val output = root / "isa_amdgpu" / "src" / "generated" / "test" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "amdgpu" / "common" / "GoldenCorpus.scala"
      val dataRoot = root / "isa_amdgpu" / "src" / "generated" / "test" / "resources" / "net" / "kurobako" /
        "gemstone" / "isa" / "amdgpu" / "common"
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
    settings = skipSharedSourceFormatting ++ Seq(
      // The exhaustive LLVM oracle is a JVM code-generation/conformance gate. Running 407k parser cases in an
      // unoptimised Native test binary adds minutes without exercising a different implementation.
      Test / unmanagedSources / excludeFilter := "GoldenCorpusSuite.scala"
    )
  )

lazy val isa_spirv = projectMatrix
  .dependsOn(core)
  .settings(
    commonSettings,
    checkedInGeneratedSources("isa_spirv"),
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
      val output = root / "isa_spirv" / "src" / "generated" / "main" / "scala" / "net" / "kurobako" / "gemstone" /
        "isa" / "spirv" / spirvPackage / "Instructions.scala"
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
      val output = root / "isa_spirv" / "src" / "generated" / "test" / "scala" / "net" / "kurobako" / "gemstone" /
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
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = skipSharedSourceFormatting)

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
    Test / test := (Test / test)
      .dependsOn(
        codegen.jvm(scala3Version) / prepareISAReferences,
        codegen.jvm(scala3Version) / checkRuntimeCollections
      )
      .evaluated,
    Test / testFull := Def.uncached {
      (Test / testFull)
        .dependsOn(
          codegen.jvm(scala3Version) / prepareISAReferences,
          codegen.jvm(scala3Version) / checkRuntimeCollections
        )
        .value
    },
    ThrustTest / test := (ThrustTest / test)
      .dependsOn(codegen.jvm(scala3Version) / prepareISAReferences)
      .evaluated,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion % ThrustTest
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    settings = Seq(
      Compile / unmanagedSourceDirectories +=
        (LocalRootProject / baseDirectory).value / "server" / "src" / "main-jvm" / "scala",
      Test / unmanagedSourceDirectories +=
        (LocalRootProject / baseDirectory).value / "server" / "src" / "test-jvm" / "scala"
    )
  )
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = skipSharedSourceFormatting)

addCommandAlias("testThrust", "server / Thrust / testOnly net.kurobako.gemstone.server.ThrustConformanceSuite")
addCommandAlias(
  "check",
  ";codegen / checkRuntimeCollections;api / test;codegen / test;core / test;apiNative / compile;coreNative / compile;serverNative / compile;server / test"
)
