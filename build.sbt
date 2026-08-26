import scala.scalanative.build.BuildTarget
import scala.sys.process.Process

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val scala3Version    = "3.8.3"
lazy val munitVersion     = "1.3.4"
lazy val catsParseVersion = "1.1.0"
lazy val jsoupVersion     = "1.23.2"
lazy val llvmVersion      = "23.1.0"
lazy val mesaVersion      = "26.2.1"
lazy val cudaPtxVersion   = "13.3.0"
lazy val ptxIsaVersion    = "9.3"
lazy val ptxIsaPackage    = "v" + ptxIsaVersion.replace('.', '_')
lazy val amdIsaXmlVersion = "2026_08_06"

lazy val llvmSourceSha256  = "ab1f0e3ec52448c33e8782eaf0422504b87c7b016b22514653ee0d8fcee479ff"
lazy val mesaSourceSha256  = "c47e81bddc4760360a41ac3c5acec38acb81f9d750ecef47e7f3adc7021a4442"
lazy val cudaPtxHtmlSha256 = "40026f79b65fee3f8c8b3e717c6f80c6385e8d97215318d6e4b7f6106f58d5a4"
lazy val amdIsaXmlSha256   = "82404f1126761b7877595b622afa7e1f311f2f41e89a3abe9aaf8ad045c082e2"

lazy val isaReferenceCacheDirectory = settingKey[File]("Persistent cache for pinned ISA reference artifacts.")
lazy val prepareIsaReferences       = taskKey[Unit]("Download, verify, and selectively extract pinned ISA references.")
@transient lazy val generatePtxSources = taskKey[Seq[File]]("Update checked-in PTX instruction metadata.")
@transient lazy val generatePtxReferenceTests =
  taskKey[Seq[File]]("Update checked-in PTX documentation conformance tests.")
@transient lazy val generateAmdGpuSources = taskKey[Seq[File]]("Update checked-in normalized AMDGPU metadata.")
@transient lazy val generateAmdGpuGoldenTests =
  taskKey[Seq[File]]("Update the checked-in LLVM AMDGPU golden-test corpus.")

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
    Compile / mainClass               := Some("net.kurobako.gemstone.codegen.GenerateApi"),
    Compile / run / fork              := false,
    libraryDependencies += "org.jsoup" % "jsoup" % jsoupVersion,
    prepareIsaReferences              := Def.uncached {
      val _ = IsaReferences.prepare(
        cacheDirectory = (Global / isaReferenceCacheDirectory).value,
        outputRoot = target.value / "isa-references",
        pins = IsaReferences.Pins(
          llvmVersion = llvmVersion,
          llvmSha256 = llvmSourceSha256,
          mesaVersion = mesaVersion,
          mesaSha256 = mesaSourceSha256,
          cudaPtxVersion = cudaPtxVersion,
          cudaPtxSha256 = cudaPtxHtmlSha256,
          amdXmlVersion = amdIsaXmlVersion,
          amdXmlSha256 = amdIsaXmlSha256
        ),
        offline = sys.env.get("GEMSTONE_ISA_REFERENCES_OFFLINE").contains("1"),
        log = streams.value.log
      )
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
    generatePtxSources := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareIsaReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && IO.read(manifest).contains(s"cuda-ptx-version\t$cudaPtxVersion\n")
      }.toVector match {
        case Vector(directory) => directory
        case found => sys.error(s"expected one prepared ISA reference view, found ${found.size} in $referenceRoot")
      }
      val input  = references / "nvidia" / "ptx" / "index.html"
      val output = root / "isa_ptx" / "src" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "isa" / "ptx" /
        ptxIsaPackage / "generated" / "Catalog.scala"
      val converter = fileConverter.value
      val classpath = (codegen.jvm(scala3Version) / Compile / fullClasspath).value
        .map(entry => converter.toPath(entry.data).toString)
      val java = file(sys.props("java.home")) / "bin" / "java"
      val exit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GeneratePtxIsa",
        "catalog",
        input.getAbsolutePath,
        output.getAbsolutePath,
        ptxIsaVersion,
        ptxIsaPackage
      )).!
      require(exit == 0, s"PTX metadata generator exited with $exit")
      Seq(output)
    },
    generatePtxReferenceTests := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareIsaReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && IO.read(manifest).contains(s"cuda-ptx-version\t$cudaPtxVersion\n")
      }.toVector match {
        case Vector(directory) => directory
        case found => sys.error(s"expected one prepared ISA reference view, found ${found.size} in $referenceRoot")
      }
      val input  = references / "nvidia" / "ptx" / "index.html"
      val output = root / "isa_ptx" / "src" / "test" / "scala" / "net" / "kurobako" / "gemstone" / "isa" / "ptx" /
        ptxIsaPackage / "generated" / "DocumentationSuite.scala"
      val converter = fileConverter.value
      val classpath = (codegen.jvm(scala3Version) / Compile / fullClasspath).value
        .map(entry => converter.toPath(entry.data).toString)
      val java = file(sys.props("java.home")) / "bin" / "java"
      val exit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GeneratePtxIsa",
        "tests",
        input.getAbsolutePath,
        output.getAbsolutePath,
        ptxIsaVersion,
        ptxIsaPackage
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
    generateAmdGpuSources := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareIsaReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && {
          val content = IO.read(manifest)
          content.contains(s"llvm-version\t$llvmVersion\n") &&
          content.contains(s"mesa-version\t$mesaVersion\n") &&
          content.contains(s"amd-xml-version\t$amdIsaXmlVersion\n")
        }
      }.toVector match {
        case Vector(directory) => directory
        case found => sys.error(s"expected one prepared ISA reference view, found ${found.size} in $referenceRoot")
      }
      val exporter = root / "codegen" / "amdgpu" / "export_aco.py"
      val output = root / "isa_amdgpu" / "src" / "main" / "scala" / "net" / "kurobako" / "gemstone" / "isa" /
        "amdgpu" / "common" / "generated" / "Database.scala"
      val converter = fileConverter.value
      val classpath =
        (codegen.jvm(scala3Version) / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data).toString)
      val java = file(sys.props("java.home")) / "bin" / "java"
      val exit = Process(Seq(
        java.getAbsolutePath,
        "-cp",
        classpath.mkString(sys.props("path.separator")),
        "net.kurobako.gemstone.codegen.GenerateAmdGpuIsa",
        references.getAbsolutePath,
        exporter.getAbsolutePath,
        output.getAbsolutePath
      )).!
      require(exit == 0, s"AMDGPU metadata generator exited with $exit")
      Seq(output)
    },
    generateAmdGpuGoldenTests := Def.uncached {
      val root          = (LocalRootProject / baseDirectory).value
      val _             = (codegen.jvm(scala3Version) / prepareIsaReferences).value
      val referenceRoot = (codegen.jvm(scala3Version) / target).value / "isa-references"
      val references    = (referenceRoot * DirectoryFilter).get().filter { directory =>
        val manifest = directory / "MANIFEST.tsv"
        manifest.isFile && {
          val content = IO.read(manifest)
          content.contains(s"llvm-version\t$llvmVersion\n") &&
          content.contains(s"mesa-version\t$mesaVersion\n") &&
          content.contains(s"amd-xml-version\t$amdIsaXmlVersion\n")
        }
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
        "net.kurobako.gemstone.codegen.GenerateAmdGpuGolden",
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

lazy val server = projectMatrix
  .dependsOn(api, core, isa_ptx, isa_amdgpu)
  .settings(commonSettings, name := "gemstone-server")
  .jvmPlatform(scalaVersions = Seq(scala3Version))

lazy val native = projectMatrix
  .dependsOn(core, isa_ptx, isa_amdgpu)
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
