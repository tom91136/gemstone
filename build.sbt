import CheckedInSources.*
import ExternalProjectPlugin.autoImport.*
import Paths.*
import java.nio.file.Path
import org.scalajs.linker.interface.CheckedBehavior
import org.scalajs.testing.adapter.{TestAdapter => JSTestAdapter}
import sbt.testing.Framework
import scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport.NativeTags
import scala.scalanative.sbtplugin.Utilities.*
import scala.scalanative.testinterface.adapter.{TestAdapter => NativeTestAdapter}
import stryker4s.sbt.Stryker4sPlugin

// Test processes of Scala.js and Scala Native projects outlive their tasks; see TestSlots.
lazy val NativeSlotWait = Tags.Tag("native-slot-wait")
lazy val JSSlotWait     = Tags.Tag("js-slot-wait")

def testSlotSettings(
    slots: java.util.concurrent.Semaphore,
    wait: Tags.Tag,
    link: TaskKey[?],
    frameworks: Def.Initialize[Task[Map[TestFramework, Framework]]]
) = Seq(
  Test / loadedTestFrameworks :=
    frameworks
      .dependsOn(Def.uncached(Def.task {
        link.value
        TestSlots.acquire(thisProject.value.id, slots)
      }.tag(wait)))
      .value
) ++ Seq(Test / testResultLogger, Test / testFull / testResultLogger).map { logger =>
  // testFull reports through its own logger rather than delegating to Test / testResultLogger
  logger := {
    val owner    = thisProject.value.id
    val delegate = logger.value
    new TestResultLogger {
      def run(log: Logger, results: Tests.Output, taskName: String): Unit = {
        delegate.run(log, results, taskName)
        TestSlots.release(owner)
      }
    }
  }
}

def nativeTestFrameworks = Def.uncached(Def.task {
  val owner      = thisProject.value.id
  val frameworks = (Test / testFrameworks).value
  val config     = NativeTestAdapter.Config()
    .withBinaryFile(fileConverter.value.toPath((Test / nativeLink).value).toFile)
    .withEnvVars((Test / test / envVars).value)
    .withLogger((Test / loadedTestFrameworks / streams).value.log.toLogger)
  val adapter = new NativeTestAdapter(config)
  TestSlots.adopt(owner, () => adapter.close())
  frameworks.zip(adapter.loadFrameworks(frameworks.map(_.implClassNames.toList).toList)).collect {
    case (framework, Some(loaded)) => framework -> loaded
  }.toMap
})

def jsTestFrameworks = Def.uncached(Def.task {
  val owner      = thisProject.value.id
  val frameworks = (Test / testFrameworks).value
  val config     = JSTestAdapter.Config()
    .withLogger(scalaJSLoggerFactory.value((Test / loadedTestFrameworks / streams).value.log))
    .withEnv((Test / envVars).value)
  val adapter = new JSTestAdapter((Test / jsEnv).value, (Test / jsEnvInput).value, config)
  TestSlots.adopt(owner, () => adapter.close())
  frameworks.zip(adapter.loadFrameworks(frameworks.map(_.implClassNames.toList).toList)).collect {
    case (framework, Some(loaded)) => framework -> loaded
  }.toMap
})

Global / excludeLintKeys += Global / onComplete

Global / onComplete := {
  val previous = (Global / onComplete).value
  () => {
    previous()
    TestSlots.releaseAll()
  }
}

Global / concurrentRestrictions ++= Seq(
  Tags.limit(NativeSlotWait, 1),
  Tags.limit(JSSlotWait, 1),
  Tags.limit(NativeTags.Link, 1),
  Tags.limit(ScalaJSTags.Link, 1),
  Tags.limit(Tags.ForkedTestGroup, math.min(2, math.max(1, java.lang.Runtime.getRuntime.availableProcessors / 8)))
)

lazy val projectOrganisation = "net.kurobako"
lazy val rootPackage         = s"$projectOrganisation.gemstone"
lazy val scala3Version       = "3.9.0"
lazy val munitVersion        = "1.3.6"
lazy val catsCoreVersion     = "2.13.0"
lazy val catsParseVersion    = "1.1.0"
lazy val declineVersion      = "2.6.2"
lazy val jsoupVersion        = "1.23.2"
lazy val scribeVersion       = "3.19.0"
lazy val upickleVersion      = "4.4.3"
lazy val scalafmtVersion     = "3.11.5"
lazy val rocmVersion         = "7.1.1"
lazy val laminarVersion      = "17.2.1"
lazy val scalaJsDomVersion   = "2.8.1"

ThisBuild / organization         := projectOrganisation
ThisBuild / version              := "0.1.0-SNAPSHOT"
ThisBuild / versionScheme        := Some("early-semver")
ThisBuild / mimaFailOnNoPrevious := false
ThisBuild / licenses             := Seq(License.Apache2)
ThisBuild / homepage             := Some(uri("https://github.com/tom91136/gemstone"))
ThisBuild / scmInfo              :=
  Some(ScmInfo(uri("https://github.com/tom91136/gemstone"), "scm:git:git@github.com:tom91136/gemstone.git"))
ThisBuild / developers := List(
  Developer("tom91136", "Tom Lin", "tom91136@gmail.com", uri("https://github.com/tom91136"))
)
ThisBuild / scalaVersion      := scala3Version
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixConfig    := Baumkuchen.scalafixConfig((LocalRootProject / baseDirectory).value.toPath)

lazy val offlineMode             = settingKey[Boolean]("Require cached dependencies and forbid network fetches.")
lazy val conformanceDirectory    = settingKey[Path]("Conformance build, work, artifact, and report root.")
lazy val conformanceTargetSet    = settingKey[String]("Conformance artifact matrix used by builds and emulation.")
lazy val conformanceJfr          = settingKey[Boolean]("Record the conformance emulation server with JFR.")
lazy val conformanceHost         = taskKey[Path]("Built host conformance driver and its tests.")
lazy val emulate                 = inputKey[Unit]("Run conformance through Gemstone.")
lazy val nativeArtifact          = taskKey[Path]("Built native runtime directory.")
lazy val nativeCompileDatabase   = taskKey[Path]("Configured CMake build directory with compile_commands.json.")
lazy val nativeSourceDirectories =
  settingKey[Seq[Path]]("C and C++ source trees governed by the native codestyle checks.")
lazy val checkEnvironment          = taskKey[Unit]("Check environment output freshness and declared names.")
lazy val codegen                   = taskKey[Unit]("Update and format every checked-in generated source and fixture.")
lazy val generateBenchmarkFixtures =
  inputKey[Path]("Build benchmark fixtures in pinned toolchain containers: cuda, hip, hsa, opencl, or fixtures.")

Global / offlineMode := sys.env.get("GEMSTONE_OFFLINE").exists { value =>
  value.toLowerCase(java.util.Locale.ROOT) match {
    case "1" | "true" | "on"   => true
    case "0" | "false" | "off" => false
    case _ => throw sbt.internal.util.MessageOnlyException(s"GEMSTONE_OFFLINE must be a boolean: $value")
  }
}
Global / conformanceDirectory := (LocalRootProject / baseDirectory).value.toPath / "target/conformance"

lazy val containerExecutable = sys.env.getOrElse("CONTAINER_EXECUTABLE", "podman")

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-no-indent",
    "-deprecation",
    "-Wconf:cat=unchecked:error",
    "-Wconf:name=MatchCaseUnreachable:error",
    "-Wconf:name=PatternMatchExhaustivity:error",
    "-Wnonunit-statement",
    "-Wvalue-discard",
    "-Wunused:all",
    "-Yretain-trees",
    "-language:noAutoTupling",
    "-language:strictEquality"
  ),
  libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
  Test / testOptions += Tests.Argument(TestFrameworks.MUnit, "--log=warn", "--logger=sbt"),
  Compile / exportJars  := false,
  scalafmtDetailedError := true
) ++ ResourceSync.settings

// JVM suites run in their own heap, apart from the build's compilers and linkers.
lazy val forkedTestSettings = Seq(
  Test / testForkedParallel := true,
  Test / javaOptions ++= Seq("-Xmx4g", "-Xss6m")
)

// Scala Native has no class path: a binary embeds the resources its modules read at runtime.
lazy val nativeTestSettings = Seq(
  Test / nativeConfig ~= (_.withMode(scala.scalanative.build.Mode.debug).withEmbedResources(true)),
  Test / nativeLink / logLevel           := Level.Warn,
  Test / loadedTestFrameworks / logLevel := Level.Warn,
  Test / parallelExecution               := false
) ++ testSlotSettings(TestSlots.native, NativeSlotWait, Test / nativeLink, nativeTestFrameworks)

// An incremental linker keeps its state for the whole session; one per test link together outgrows the heap.
lazy val jsSettings = EmbeddedResources.settings ++ Seq(
  Test / scalaJSLinkerConfig ~= (_.withBatchMode(true)),
  Test / parallelExecution := false
) ++ testSlotSettings(TestSlots.js, JSSlotWait, Test / fastLinkJS, jsTestFrameworks)

// Threads, files, child processes and class-path resources exist on the JVM and Scala Native but not in Scala.js.
lazy val hostedSourceSettings = Seq(Compile, Test).map { configuration =>
  configuration / unmanagedSourceDirectories ++= {
    val hosted = (configuration / sourceDirectory).value / "scalajvmnative"
    if (virtualAxes.value.contains(VirtualAxis.js)) Nil else Seq(hosted)
  }
}

lazy val mutationSettings = Mutation.settings ++ Seq(
  strykerConcurrency     := 1,
  strykerReporters       := Seq("console", "html", "json"),
  strykerThresholdsBreak := 0,
  strykerOpenReport      := false
)

def module(project: Project): Project = project
  .disablePlugins(Stryker4sPlugin)
  .settings(commonSettings, forkedTestSettings, Test / fork := true, publish / skip := true)

def module(matrix: ProjectMatrix): ProjectMatrix = matrix
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(testSupport % "test->compile")
  .settings(
    commonSettings,
    hostedSourceSettings,
    forkedTestSettings,
    Test / fork := virtualAxes.value.contains(VirtualAxis.jvm)
  )

def compilerPlugin(plugin: String): Project = Project(s"${plugin}Plugin", file(s"codestyle/$plugin"))
  .disablePlugins(Stryker4sPlugin, _root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    name                 := s"gemstone-$plugin",
    Compile / packageBin := {
      val packaged      = (Compile / packageBin).value
      val converter     = fileConverter.value
      val original      = converter.toPath(packaged)
      val fingerprinted = original.resolveSibling(s"$plugin-${sbt.io.Hash.toHex(sbt.io.Hash(original.toFile))}.jar")
      java.nio.file.Files.copy(original, fingerprinted, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
      val output = converter.toVirtualFile(fingerprinted)
      Def.declareOutput(output)
      output
    },
    publish / skip       := true,
    Compile / exportJars := true,
    scalacOptions ++= Seq("-deprecation", "-Wunused:all"),
    libraryDependencies ++= Seq(
      "org.scala-lang" %% "scala3-compiler" % scala3Version % Provided,
      "org.scalameta"  %% "munit"           % munitVersion  % Test
    ),
    Test / fork := true,
    Test / javaOptions ++= {
      val converter = fileConverter.value
      Seq(
        s"-Dgemstone.$plugin.jar=${converter.toPath((Compile / packageBin).value)}",
        s"-Dgemstone.$plugin.classpath=" + (Test / fullClasspath).value
          .map(entry => converter.toPath(entry.data).toString).mkString(java.io.File.pathSeparator)
      )
    }
  )

lazy val publicapiPlugin = compilerPlugin("publicapi")

lazy val codestyle = project.in(file("codestyle"))
  .disablePlugins(Stryker4sPlugin, _root_.scalafix.sbt.ScalafixPlugin)
  .aggregate(publicapiPlugin)
  .settings(name := "gemstone-codestyle", publish / skip := true)

// No dependency on core: its own tests can use these utilities without a cycle. Fixtures that need core types
// live in core test sources, which dependants consume through test->test.
lazy val testSupport = projectMatrix.in(file("test-support"))
  .disablePlugins(Stryker4sPlugin)
  .settings(
    commonSettings,
    hostedSourceSettings,
    name                                   := "gemstone-test-support",
    publish / skip                         := true,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val core = module(projectMatrix)
  .settings(
    Publishing.library("Gemstone core: devices, backends, address spaces, launch control and the execution RPC.")
  )
  .settings(
    CheckedInSources.settings,
    name := "gemstone-core",
    libraryDependencies ++= Seq(
      "com.lihaoyi"   %% "upickle"   % upickleVersion,
      "org.typelevel" %% "cats-core" % catsCoreVersion
    )
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = _.enablePlugins(Stryker4sPlugin).settings(
      mutationSettings,
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
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val codegenProject = module(Project("codegen", file("tools/codegen")))
  .dependsOn(core.jvm(scala3Version), testSupport.jvm(scala3Version) % "test->compile")
  .settings(
    name                 := "gemstone-codegen",
    Test / baseDirectory := (LocalRootProject / baseDirectory).value,
    Test / fork          := true,
    Test / javaOptions +=
      s"-Dgemstone.codegen.sassReferences=${(LocalRootProject / baseDirectory).value / "target/references/sass"}",
    libraryDependencies ++= Seq(
      "org.jsoup"          % "jsoup"            % jsoupVersion,
      "com.monovore"      %% "decline"          % declineVersion,
      "org.scalameta"     %% "scalafmt-core"    % scalafmtVersion,
      "org.typelevel"     %% "cats-core"        % catsCoreVersion,
      "org.typelevel"     %% "cats-parse"       % catsParseVersion,
      "org.apache.commons" % "commons-compress" % "1.28.0",
      "org.tukaani"        % "xz"               % "1.12"
    ),
    checkEnvironment := RunMain.main(
      ThisProject,
      s"$rootPackage.codegen.Codegen",
      Def.task(Seq("check-environment", "--root", s"${(LocalRootProject / baseDirectory).value}"))
    ).value
  )

lazy val samples = module(project.in(file("samples")))
  .dependsOn(
    ptxVm.jvm(scala3Version),
    sassVm.jvm(scala3Version),
    amdgpuVm.jvm(scala3Version),
    spirvVm.jvm(scala3Version)
  )
  .settings(name := "gemstone-samples", publish / skip := true, Compile / mainClass := Some("samples.Samples"))

lazy val nvidia = module(projectMatrix.in(file("isa/nvidia")))
  .settings(Publishing.library("The execution VM PTX and SASS share."))
  .dependsOn(core % "compile->compile;test->test")
  .settings(name := "gemstone-isa-nvidia")
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val sassAsm = module(projectMatrix.in(file("isa/sass-asm")))
  .settings(Publishing.library("SASS instruction model, decoder, assembler, disassembler and cubin containers."))
  .dependsOn(core % "compile->compile;test->test")
  .settings(
    CheckedInSources.settings,
    name                                   := "gemstone-isa-sass-asm",
    libraryDependencies += "org.typelevel" %% "cats-core" % catsCoreVersion
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val sassVm = module(projectMatrix.in(file("isa/sass-vm")))
  .settings(Publishing.library("SASS execution on the shared NVIDIA VM."))
  .dependsOn(
    sassAsm % "compile->compile;test->test",
    nvidia  % "compile->compile;test->test",
    ptxVm   % "test->compile"
  )
  .settings(name := "gemstone-isa-sass-vm")
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val ptxAsm = module(projectMatrix.in(file("isa/ptx-asm")))
  .settings(Publishing.library("PTX instruction model, parser, printer and validation."))
  .dependsOn(core % "compile->compile;test->test")
  .settings(
    CheckedInSources.settings,
    name                                   := "gemstone-isa-ptx-asm",
    libraryDependencies += "org.typelevel" %% "cats-core" % catsCoreVersion
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val ptxVm = module(projectMatrix.in(file("isa/ptx-vm")))
  .settings(Publishing.library("PTX execution on the shared NVIDIA VM."))
  .dependsOn(ptxAsm % "compile->compile;test->test", nvidia % "compile->compile;test->test")
  .settings(CheckedInSources.settings, name := "gemstone-isa-ptx-vm")
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val amdgpuAsm = module(projectMatrix.in(file("isa/amdgpu-asm")))
  .settings(Publishing.library("AMDGPU instruction model, assembler, disassembler and HSA code objects."))
  .dependsOn(core % "compile->compile;test->test")
  .settings(
    CheckedInSources.settings,
    name := "gemstone-isa-amdgpu-asm",
    libraryDependencies ++= Seq(
      "com.outr"      %% "scribe"    % scribeVersion,
      "org.typelevel" %% "cats-core" % catsCoreVersion
    )
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = _.enablePlugins(Stryker4sPlugin).settings(
      mutationSettings,
      strykerMutate     := Seq("src/main/scala/net/kurobako/gemstone/isa/amdgpu/asm/Parser.scala"),
      strykerTestFilter := Seq("net.kurobako.gemstone.isa.amdgpu.ParserSuite")
    )
  )
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val amdgpuVm = module(projectMatrix.in(file("isa/amdgpu-vm")))
  .settings(Publishing.library("AMDGPU wave execution."))
  .dependsOn(amdgpuAsm % "compile->compile;test->test")
  .settings(name := "gemstone-isa-amdgpu-vm")
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val spirvAsm = module(projectMatrix.in(file("isa/spirv-asm")))
  .settings(Publishing.library("SPIR-V module model, binary codec, assembly text and validation."))
  .dependsOn(core % "compile->compile;test->test")
  .settings(
    CheckedInSources.settings,
    name                                   := "gemstone-isa-spirv-asm",
    libraryDependencies += "org.typelevel" %% "cats-core" % catsCoreVersion
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = _.enablePlugins(Stryker4sPlugin).settings(
      mutationSettings,
      strykerMutate     := Seq(s"src/main/scala/${rootPackage.replace('.', '/')}/isa/spirv/Binary.scala"),
      strykerTestFilter := Seq(s"$rootPackage.isa.spirv.BinarySuite")
    )
  )
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val spirvVm = module(projectMatrix.in(file("isa/spirv-vm")))
  .settings(Publishing.library("SPIR-V compute and graphics execution."))
  .dependsOn(spirvAsm % "compile->compile;test->test")
  .settings(
    name := "gemstone-isa-spirv-vm",
    Test / envVars += ("GEMSTONE_NATIVE_DIRECTORY" -> s"${(native / nativeArtifact).value}")
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = _.settings(Test / fork := true)
  )
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)
  .jsPlatform(scalaVersions = Seq(scala3Version), settings = jsSettings)

lazy val explorerSite = taskKey[Path]("Static instruction explorer: index.html, stylesheet and the optimised script.")

lazy val explorer = module(projectMatrix.in(file("explorer")))
  .dependsOn(ptxVm, sassVm, amdgpuVm, spirvVm)
  .settings(name := "gemstone-explorer", publish / skip := true)
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .jsPlatform(
    scalaVersions = Seq(scala3Version),
    settings = jsSettings ++ Seq(
      scalaJSUseMainModuleInitializer := true,
      // describing instructions lowers synthetic ones, whose rejections must throw as they do on the JVM
      scalaJSLinkerConfig ~=
        (_.withSemantics(
          _.withArrayIndexOutOfBounds(CheckedBehavior.Compliant)
            .withStringIndexOutOfBounds(CheckedBehavior.Compliant)
            .withAsInstanceOfs(CheckedBehavior.Compliant)
        )),
      libraryDependencies ++= Seq(
        "com.raquo"    %% "laminar"     % laminarVersion,
        "org.scala-js" %% "scalajs-dom" % scalaJsDomVersion
      ),
      explorerSite := Def.uncached {
        val _      = (Compile / fullLinkJS).value
        val root   = (LocalRootProject / baseDirectory).value
        val output = root / "target/explorer"
        IO.delete(output)
        IO.copyDirectory((Compile / fullLinkJS / scalaJSLinkerOutputDirectory).value, output)
        IO.copyDirectory(root / "explorer/src/main/site", output)
        output.toPath
      }
    )
  )

lazy val generateDefinitions = taskKey[Unit]("Render every staged instruction form as an executable definition.")

/** The harness checking rendered definitions against execution. Each ISA's generated cases compile and run in their own
  * project, `semanticsCheck<ISA>`, outside the root aggregate: together they need far more heap than the build's
  * default, so `semanticsCheckAll` runs them one at a time, every one even after another fails.
  */
lazy val semanticsCheck = module(project.in(file("isa/semantics-check")))
  .dependsOn(
    explorer.jvm(scala3Version),
    ptxVm.jvm(scala3Version)    % "test->test",
    sassVm.jvm(scala3Version)   % "test->test",
    amdgpuVm.jvm(scala3Version) % "test->test",
    spirvVm.jvm(scala3Version)  % "test->test"
  )
  .settings(name := "gemstone-semantics-check")

/** Every instruction form the explorer describes for `isa`, rendered as a definition, compiled and checked. */
def semanticsCheckOf(isa: String): Project =
  module(Project(s"semanticsCheck$isa", file(s"isa/semantics-check/generated/${isa.toLowerCase}")))
    .dependsOn(semanticsCheck % "compile->compile;test->test")
    .settings(
      name                := s"gemstone-semantics-check-${isa.toLowerCase}",
      generateDefinitions := RunMain.generated(
        LocalProject("semanticsCheck"),
        s"$rootPackage.isa.check.Generate",
        Def.setting(((Test / sourceManaged).value / "definitions").toPath),
        isa
      ).value,
      Test / sourceGenerators += Def.task {
        Def.uncached {
          val _ = generateDefinitions.value
          ((Test / sourceManaged).value / "definitions" ** "*.scala").get()
        }
      }
    )

lazy val semanticsCheckPTX            = semanticsCheckOf("PTX")
lazy val semanticsCheckSASS           = semanticsCheckOf("SASS")
lazy val semanticsCheckSASSHopper     = semanticsCheckOf("SASSHopper")
lazy val semanticsCheckSASSBlackwell  = semanticsCheckOf("SASSBlackwell")
lazy val semanticsCheckAMDGPU         = semanticsCheckOf("AMDGPU")
lazy val semanticsCheckAMDGPUMatrix   = semanticsCheckOf("AMDGPUMatrix")
lazy val semanticsCheckAMDGPUResource = semanticsCheckOf("AMDGPUResource")
lazy val semanticsCheckSPIRV          = semanticsCheckOf("SPIRV")

lazy val server = module(projectMatrix.in(file("server")))
  .dependsOn(core % "compile->compile;test->test", ptxVm, sassVm, amdgpuVm, spirvVm)
  .dependsOn(publicapiPlugin % "plugin->compile")
  .settings(
    name           := "gemstone-server",
    publish / skip := true,
    Compile / scalacOptions += s"-P:publicapi:$rootPackage.server",
    Test / scalacOptions -= s"-P:publicapi:$rootPackage.server",
    Test / envVars += ("GEMSTONE_NATIVE_DIRECTORY" -> s"${(native / nativeArtifact).value}"),
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
    configure = _.settings(
      Compile / run / fork          := true,
      Compile / run / envVars += ("GEMSTONE_NATIVE_DIRECTORY" -> s"${(native / nativeArtifact).value}"),
      Compile / run / baseDirectory := (LocalRootProject / baseDirectory).value,
      Test / fork                   := true,
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
      Compile / exportJars := true,
      Compile / nativeConfig ~= (_.withMode(scala.scalanative.build.Mode.releaseFast).withEmbedResources(true))
    )
  )

lazy val benchmarks = module(projectMatrix.in(file("tools/benchmarks")))
  .dependsOn(core, ptxVm, sassVm, amdgpuVm, spirvVm, server)
  .settings(
    name           := "gemstone-benchmarks",
    publish / skip := true,
    libraryDependencies ++= Seq(
      "com.monovore"  %% "decline"   % declineVersion,
      "org.typelevel" %% "cats-core" % catsCoreVersion
    ),
    Compile / mainClass       := Some(s"$rootPackage.benchmarks.BenchmarkRunner"),
    Compile / run / fork      := true,
    generateBenchmarkFixtures := {
      val selected = Def.spaceDelimited("[cuda|hip|hsa|opencl]").parsed.headOption.getOrElse("fixtures")
      val output   = projectMatrixBaseDirectory.value.toPath / "target/generated-fixtures"
      val _        = CMake.build(
        projectMatrixBaseDirectory.value.toPath / "fixtures",
        (LocalRootProject / baseDirectory).value.toPath / "target/benchmarks/fixtures",
        Seq(
          s"-DCONTAINER_EXECUTABLE=$containerExecutable",
          s"-DROCM_VERSION=$rocmVersion",
          s"-DBENCHMARK_FIXTURE_OUTPUT_DIRECTORY=$output"
        ),
        streams.value.log.info,
        Seq(selected),
        Seq.empty
      )
      output
    }
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = _.settings(
      Compile / run / javaOptions ++= Seq("-Xms512m", "-Xmx4g", "--sun-misc-unsafe-memory-access=allow")
    )
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = nativeTestSettings ++ Seq(Compile / nativeConfig ~= (_.withEmbedResources(true)))
  )

lazy val conformanceTool = module(project.in(file("tools/conformance")))
  .dependsOn(core.jvm(scala3Version))
  .settings(
    name                 := "gemstone-conformance-tool",
    Compile / run / fork := true,
    Compile / run / javaOptions += "--sun-misc-unsafe-memory-access=allow",
    libraryDependencies += "com.monovore" %% "decline" % declineVersion
  )

lazy val externalCodestyleSettings = Seq(
  nativeSourceDirectories := Seq(baseDirectory.value.toPath),
  nativeCompileDatabase   := Def.uncached(nativeArtifact.value),
  format                  := NativeCodestyle.format(nativeSourceDirectories.value),
  lint                    := Def.uncached(
    NativeCodestyle.lint(nativeSourceDirectories.value, nativeCompileDatabase.value, streams.value.log.info(_))
  )
)

lazy val native = project.in(file("native"))
  .disablePlugins(Stryker4sPlugin, plugins.JvmPlugin, plugins.IvyPlugin)
  .enablePlugins(ExternalProjectPlugin)
  .settings(
    externalCodestyleSettings,
    name := "gemstone-native",
    nativeSourceDirectories +=
      (LocalRootProject / baseDirectory).value.toPath / "server/src/main/resources/scala-native",
    nativeArtifact := Def.uncached {
      RunMain.main(
        codegenProject,
        s"$rootPackage.codegen.Codegen",
        Def.task(Seq("prepare", "--root", s"${(LocalRootProject / baseDirectory).value}", "api-native", "level-zero"))
      ).value
      val references = (LocalRootProject / baseDirectory).value.toPath / "target/references"
      CMake.build(
        source = baseDirectory.value.toPath,
        directory = (LocalRootProject / baseDirectory).value.toPath / "target/native/build",
        arguments = Seq(
          "-DBUILD_TESTING=ON",
          "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON",
          s"-DCONTAINER_EXECUTABLE=$containerExecutable",
          s"-DROCM_VERSION=$rocmVersion",
          s"-DCMAKE_BUILD_TYPE=${sys.env.getOrElse("CMAKE_BUILD_TYPE", "Release")}",
          s"-DCUDA_INCLUDE_DIR=${references / "api-native/nvidia/cuda/include"}",
          s"-DHIP_INCLUDE_DIR=${references / "api-native/amd/hip/include"}",
          s"-DHSA_INCLUDE_DIR=${references / "api-native/amd/hsa/runtime/hsa-runtime/inc"}",
          s"-DHSAKMT_INCLUDE_DIR=${references / "api-native/amd/hsa/libhsakmt/include"}",
          s"-DLEVEL_ZERO_INCLUDE_DIR=${references / "api-native/oneapi/level-zero/include"}",
          s"-DOPENCL_INCLUDE_DIR=${references / "api-native/khronos/opencl"}",
          s"-DVULKAN_INCLUDE_DIR=${references / "api-native/khronos/vulkan/include"}",
          s"-DLEVEL_ZERO_SOURCE_DIR=${references / "level-zero"}"
        ),
        log = streams.value.log.info
      )
    },
    build       := nativeArtifact.value,
    Test / test := {
      CMake.test(nativeArtifact.value, streams.value.log.info)
      sbt.protocol.testing.TestResult.Passed
    },
    cleanFiles := Seq((LocalRootProject / baseDirectory).value / "target/native")
  )

def conformanceExecutionRoutes(conformance: Path, targetSet: String): Seq[String] =
  scala.util.Try(
    scala.sys.process.Process(
      Seq("cmake", s"-DCONFORMANCE_TARGET_SET=$targetSet", "-P", s"${conformance / "artifacts/Targets.cmake"}")
    ).!!.linesIterator.map(_.trim).filter(_.nonEmpty).toVector
  ).getOrElse(Vector.empty)

// The PTX and SASS execution variants share one artifact.
def conformanceArtifactRoutes(conformance: Path, targetSet: String): Seq[String] =
  conformanceExecutionRoutes(conformance, targetSet).map(_.replaceFirst("^cuda-(ptx|sass)/", "cuda/")).distinct

def conformanceArtifactTarget(route: String): String = route.split('/').toList match {
  case backend :: architecture :: compiler :: Nil => s"$backend-$compiler-$architecture"
  case backend :: architecture :: Nil             => s"$backend-$architecture"
  case _                                          => route
}

def conformanceSelect(routes: Seq[String], selector: String): Seq[String] =
  routes.filter(route => route == selector || route.startsWith(s"$selector/"))

def conformanceOptions(conformance: Path, names: String*): Map[String, (String, Seq[String])] = Map(
  "--target-set" -> ("target set", ArgumentParsers.fileNames(conformance / "artifacts/targets", ".cmake")),
  "--workload"   -> ("workload", ArgumentParsers.fileNames(conformance / "workloads", ".toml")),
  "--machine"    -> ("machine profile", ArgumentParsers.fileNames(conformance / "profiles", ".toml"))
).filter((name, _) => names.contains(name))

def conformanceArtifacts(
    source: Path,
    directory: Path,
    root: Path,
    offline: Boolean,
    targetSet: String,
    targets: Seq[String],
    syclCtsFull: Boolean,
    log: String => Unit
): Path = CMake.build(
  source = source / "artifacts",
  directory = directory / "build" / targetSet,
  arguments = Seq(
    s"-DCONTAINER_EXECUTABLE=$containerExecutable",
    s"-DROCM_VERSION=$rocmVersion",
    s"-DCONFORMANCE_TARGET_SET=$targetSet",
    s"-DCONFORMANCE_ARTIFACT_DIRECTORY=${directory / "artifacts"}",
    s"-DCONFORMANCE_DEPENDENCY_DIRECTORY=${root / ".cache/conformance"}",
    s"-DCONFORMANCE_WORK_DIRECTORY=${directory / "work"}",
    s"-DCONFORMANCE_CCACHE_DIRECTORY=${root / ".cache/ccache"}",
    s"-DCONFORMANCE_OFFLINE=${if (offline) "ON" else "OFF"}",
    s"-DSYCL_CTS_ENABLE_FULL_CONFORMANCE=${if (syclCtsFull) "ON"
      else sys.env.getOrElse("SYCL_CTS_ENABLE_FULL_CONFORMANCE", "OFF")}",
    s"-DCONFORMANCE_SYCL_SHARD=${sys.env.getOrElse("CONFORMANCE_SYCL_SHARD", "")}"
  ) ++ sys.env.get("CONFORMANCE_ARTIFACT_PARALLELISM").map(value => s"-DCONFORMANCE_ARTIFACT_PARALLELISM=$value"),
  log = log,
  buildTargets = targets,
  // each lint route sizes its clang-tidy pool from free memory, so concurrent routes would oversubscribe it
  launcher = Seq("sh", s"${source / "cmake/jobserver.sh"}") ++
    (if (targets == Seq("lint")) Seq("--jobs", "1") else Seq.empty) ++ Seq("--")
)

lazy val conformance = project.in(file("conformance"))
  .disablePlugins(Stryker4sPlugin, plugins.JvmPlugin, plugins.IvyPlugin)
  .enablePlugins(ExternalProjectPlugin)
  .settings(
    externalCodestyleSettings,
    name                  := "gemstone-conformance",
    conformanceTargetSet  := "default",
    nativeCompileDatabase := Def.uncached(conformanceHost.value),
    conformanceJfr        := true,
    build                 := {
      val arguments = Def.setting {
        val conformance = baseDirectory.value.toPath
        val default     = conformanceTargetSet.value
        ArgumentParsers.arguments(
          conformanceOptions(conformance, "--target-set"),
          Seq("--sycl-cts-full"),
          ArgumentParsers.prefixes(conformanceArtifactRoutes(conformance, default))
        ).flatMap { arguments =>
          val targetSet = arguments.option("--target-set").getOrElse(default)
          val routes    = conformanceArtifactRoutes(conformance, targetSet)
          arguments.free.find(selector => conformanceSelect(routes, selector).isEmpty) match {
            case Some(selector) => sbt.complete.DefaultParsers.failure(
                s"$selector names no route in target set $targetSet; expected one of ${routes.mkString(", ")}"
              )
            case None => sbt.complete.DefaultParsers.success(arguments)
          }
        }
      }.parsed
      val targetSet = arguments.option("--target-set").getOrElse(conformanceTargetSet.value)
      val routes    = conformanceArtifactRoutes(baseDirectory.value.toPath, targetSet)
      val targets   = arguments.free.flatMap(conformanceSelect(routes, _)).map(conformanceArtifactTarget).distinct
      val _         = conformanceArtifacts(
        baseDirectory.value.toPath,
        conformanceDirectory.value,
        (LocalRootProject / baseDirectory).value.toPath,
        offlineMode.value,
        targetSet,
        if (targets.isEmpty) Seq("artifacts") else targets,
        arguments.flag("--sycl-cts-full"),
        streams.value.log.info
      )
      conformanceDirectory.value / "artifacts"
    },
    lint := Def.uncached {
      val _ = lint.value
      val _ = conformanceArtifacts(
        baseDirectory.value.toPath,
        conformanceDirectory.value,
        (LocalRootProject / baseDirectory).value.toPath,
        offlineMode.value,
        conformanceTargetSet.value,
        Seq("lint"),
        false,
        streams.value.log.info
      )
    },
    conformanceHost := Def.uncached(
      CMake.build(
        source = baseDirectory.value.toPath,
        directory = conformanceDirectory.value / "host",
        arguments = Seq(
          "-DCMAKE_BUILD_TYPE=Release",
          "-DBUILD_TESTING=ON",
          "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON",
          "-DCONFORMANCE_BACKEND=",
          "-DCONFORMANCE_BUILD_UNIFIED=OFF",
          "-DCONFORMANCE_BUILD_CTS=OFF",
          "-DCONFORMANCE_BUILD_MINIAPPS=OFF",
          s"-DCONFORMANCE_DEPENDENCY_CACHE=${(LocalRootProject / baseDirectory).value.toPath / ".cache/conformance"}"
        ),
        log = streams.value.log.info
      )
    ),
    Test / test := {
      CMake.test(conformanceHost.value, streams.value.log.info)
      sbt.protocol.testing.TestResult.Passed
    },
    Compile / run := Def.inputTaskDyn {
      val arguments = Def.setting {
        val conformance = baseDirectory.value.toPath
        ArgumentParsers.arguments(
          conformanceOptions(conformance, "--machine", "--workload"),
          Seq.empty,
          ArgumentParsers.prefixes(conformanceArtifactRoutes(conformance, conformanceTargetSet.value))
        )
      }.parsed.tokens(Set.empty)
      RunMain.main(
        conformanceTool,
        s"$rootPackage.tools.conformance.CLI",
        Def.task(Seq(
          "run",
          "--executable",
          s"${conformanceHost.value / "bin/conformance"}",
          "--artifacts",
          s"${conformanceDirectory.value / "artifacts"}",
          "--output",
          s"${conformanceDirectory.value / "direct"}",
          "--root",
          s"${(LocalRootProject / baseDirectory).value}",
          "--profiles",
          s"${baseDirectory.value / "profiles"}",
          "--"
        ) ++ arguments)
      )
    }.evaluated,
    emulate := Def.inputTaskDyn {
      val arguments = Def.setting {
        val conformance = baseDirectory.value.toPath
        ArgumentParsers.arguments(
          conformanceOptions(conformance, "--target-set", "--workload"),
          Seq("--prebuilt"),
          "all" +: ArgumentParsers.prefixes(conformanceExecutionRoutes(conformance, conformanceTargetSet.value))
        )
      }.parsed
      val targetSet = arguments.option("--target-set").getOrElse(conformanceTargetSet.value)
      val remaining = arguments.tokens(Set("--target-set", "--prebuilt"))
      val artifacts =
        if (arguments.free.isEmpty || arguments.free.contains("all")) Seq.empty
        else arguments.free.map(_.replaceFirst("^cuda-(ptx|sass)(?=/|$)", "cuda")).distinct
      // chosen outside the task, which would otherwise run build regardless of the branch taken
      val resolved =
        if (arguments.flag("--prebuilt")) Def.task {
          val directory = conformanceDirectory.value / "artifacts"
          val backends  =
            (if (artifacts.isEmpty) conformanceArtifactRoutes(baseDirectory.value.toPath, targetSet) else artifacts)
              .map(_.takeWhile(_ != '/')).distinct
          val missing = backends.filterNot(directory.resolve(_).toFile.isDirectory)
          if (missing.nonEmpty)
            throw sbt.internal.util.MessageOnlyException(s"Missing prebuilt artifacts: ${missing.mkString(", ")}")
          directory
        }
        else build.toTask((s" --target-set $targetSet" +: artifacts).mkString(" "))
      RunMain.main(
        conformanceTool,
        s"$rootPackage.tools.conformance.CLI",
        Def.task {
          val converter = fileConverter.value
          val classpath = (server.jvm(scala3Version) / Runtime / fullClasspath).value
            .map(entry => converter.toPath(entry.data)).mkString(java.io.File.pathSeparator)
          Seq(
            "emulate",
            "--executable",
            s"${conformanceHost.value / "bin/conformance"}",
            "--artifacts",
            s"${resolved.value}",
            "--output",
            s"${conformanceDirectory.value / "emulated"}",
            "--root",
            s"${(LocalRootProject / baseDirectory).value}",
            "--java",
            s"${(server.jvm(scala3Version) / javaHome).value.fold(sys.props("java.home"))(_.toString)}/bin/java",
            "--classpath",
            classpath,
            "--server-java-option",
            "-Xms4g",
            "--native",
            s"${(native / nativeArtifact).value}",
            "--workloads",
            s"${baseDirectory.value / "workloads"}",
            "--routes",
            s"${baseDirectory.value / "artifacts/Targets.cmake"}",
            "--target-set",
            targetSet
          ) ++ (if (conformanceJfr.value) Seq("--jfr", s"${conformanceDirectory.value / "jfr"}") else Seq.empty) ++
            remaining
        }
      )
    }.evaluated,
    cleanFiles := Seq(conformanceDirectory.value.toFile)
  )

// CI tests each platform on its own runner; together they cover what the root aggregates
lazy val platformMatrices =
  Seq(
    testSupport,
    core,
    server,
    ptxAsm,
    ptxVm,
    sassAsm,
    sassVm,
    nvidia,
    amdgpuAsm,
    amdgpuVm,
    spirvAsm,
    spirvVm,
    benchmarks,
    explorer
  )

def platformTests(id: String, axis: VirtualAxis, others: ProjectReference*): Project =
  Project(id, file(s"target/platform-tests/$id"))
    .disablePlugins(Stryker4sPlugin)
    .aggregate(platformMatrices.flatMap(_.filterProjects(Seq(axis))).map(project => LocalProject(project.id)) ++
      others *)
    .settings(publish / skip := true)

lazy val testsJVM =
  platformTests(
    "testsJVM",
    VirtualAxis.jvm,
    codegenProject,
    samples,
    conformanceTool,
    semanticsCheck,
    codestyle,
    native,
    conformance
  )
lazy val testsJS     = platformTests("testsJS", VirtualAxis.js)
lazy val testsNative = platformTests("testsNative", VirtualAxis.native)

lazy val gemstone = project.in(file("."))
  .disablePlugins(Stryker4sPlugin)
  .aggregate(testSupport.projectRefs *)
  .aggregate(codegenProject, samples, conformanceTool)
  .aggregate(core.projectRefs *)
  .aggregate(server.projectRefs *)
  .aggregate(ptxAsm.projectRefs *)
  .aggregate(ptxVm.projectRefs *)
  .aggregate(sassAsm.projectRefs *)
  .aggregate(sassVm.projectRefs *)
  .aggregate(nvidia.projectRefs *)
  .aggregate(amdgpuAsm.projectRefs *)
  .aggregate(amdgpuVm.projectRefs *)
  .aggregate(spirvAsm.projectRefs *)
  .aggregate(spirvVm.projectRefs *)
  .aggregate(benchmarks.projectRefs *)
  .aggregate(explorer.projectRefs *)
  .aggregate(semanticsCheck)
  .aggregate(codestyle)
  .aggregate(native, conformance)
  .settings(
    name           := "gemstone",
    publish / skip := true,
    commands ++= Coverage.commands(
      full = "coverageJvm",
      focused = "coverageOnly",
      title = "Gemstone JVM",
      entries = Seq(
        Coverage.Entry(core.jvm(scala3Version).id),
        Coverage.Entry(nvidia.jvm(scala3Version).id),
        Coverage.Entry(
          sassAsm.jvm(scala3Version).id,
          generated = Seq((sassAsm.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value.toFile)
        ),
        Coverage.Entry(sassVm.jvm(scala3Version).id),
        Coverage.Entry(
          amdgpuAsm.jvm(scala3Version).id,
          generated = Seq((amdgpuAsm.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value.toFile)
        ),
        Coverage.Entry(amdgpuVm.jvm(scala3Version).id),
        Coverage.Entry(
          ptxAsm.jvm(scala3Version).id,
          generated = Seq((ptxAsm.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value.toFile)
        ),
        Coverage.Entry(ptxVm.jvm(scala3Version).id),
        Coverage.Entry(
          spirvAsm.jvm(scala3Version).id,
          generated = Seq((spirvAsm.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value.toFile)
        ),
        Coverage.Entry(spirvVm.jvm(scala3Version).id),
        Coverage.Entry(server.jvm(scala3Version).id),
        Coverage.Entry(codegenProject.id),
        Coverage.Entry(benchmarks.jvm(scala3Version).id)
      ),
      includes = Seq(s"$rootPackage.*"),
      output = baseDirectory.value / "target" / "coverage"
    ),
    codegen := RunMain.main(
      codegenProject,
      s"$rootPackage.codegen.Codegen",
      Def.task(Seq("generate", "--root", s"${baseDirectory.value}"))
    ).value,
    commands += SemanticsChecks.command(
      "semanticsCheckAll",
      Seq(
        semanticsCheckPTX,
        semanticsCheckSASS,
        semanticsCheckSASSHopper,
        semanticsCheckSASSBlackwell,
        semanticsCheckAMDGPU,
        semanticsCheckAMDGPUMatrix,
        semanticsCheckAMDGPUResource,
        semanticsCheckSPIRV
      ).map(_.id)
    ),
    cleanFiles += baseDirectory.value / "target"
  )

addCommandAlias("gemstoneRun", "server/run run")

addCommandAlias(
  "lint",
  s"${if (Baumkuchen.enabled) ";baumkuchenAnalyse" else ""};codegen/checkEnvironment;scalafmtCheckAll;scalafmtSbtCheck;scalafixAll --check;mimaReportBinaryIssues;native/lint;conformance/lint"
)
