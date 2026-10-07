import CheckedInSources.*
import ExternalProjectPlugin.autoImport.*
import Paths.*
import java.nio.file.Path
import scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport.NativeTags
import stryker4s.sbt.Stryker4sPlugin

Global / concurrentRestrictions += Tags.limit(NativeTags.Link, 1)

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

ThisBuild / organization      := projectOrganisation
ThisBuild / version           := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion      := scala3Version
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

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
lazy val terminologyCheck          = taskKey[Unit]("Verify repository-authored sources use precise terminology.")
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
    "-P:acyclic:warn"
  ),
  libraryDependencies += "org.scalameta" %% "munit" % munitVersion % Test,
  Test / testOptions += Tests.Argument(TestFrameworks.MUnit, "--log=warn", "--logger=sbt"),
  Compile / exportJars  := false,
  scalafmtDetailedError := true
) ++ ResourceSync.settings

lazy val nativeTestSettings = Seq(
  Test / nativeConfig ~= (_.withMode(scala.scalanative.build.Mode.debug)),
  Test / nativeLink / logLevel           := Level.Warn,
  Test / loadedTestFrameworks / logLevel := Level.Warn
)

lazy val mutationSettings = Mutation.settings ++ Seq(
  strykerConcurrency     := 1,
  strykerReporters       := Seq("console", "html", "json"),
  strykerThresholdsBreak := 0,
  strykerOpenReport      := false
)

def module(project: Project): Project = project
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(codestyleRules % ScalafixConfig, acyclicPlugin % "plugin->compile")
  .settings(commonSettings, publish / skip := true)

def module(matrix: ProjectMatrix): ProjectMatrix = matrix
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(testSupport % "test->compile")
  .dependsOn(codestyleRules % ScalafixConfig, acyclicPlugin % "plugin->compile")
  .settings(commonSettings)

lazy val acyclicPlugin = project.in(file("codestyle/acyclic"))
  .disablePlugins(Stryker4sPlugin, _root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    name                 := "gemstone-acyclic",
    Compile / packageBin := {
      val packaged      = (Compile / packageBin).value
      val converter     = fileConverter.value
      val original      = converter.toPath(packaged)
      val fingerprinted = original.resolveSibling(s"acyclic-${sbt.io.Hash.toHex(sbt.io.Hash(original.toFile))}.jar")
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
        s"-Dgemstone.acyclic.jar=${converter.toPath((Compile / packageBin).value)}",
        "-Dgemstone.acyclic.classpath=" + (Test / fullClasspath).value
          .map(entry => converter.toPath(entry.data).toString).mkString(java.io.File.pathSeparator)
      )
    }
  )

lazy val codestyleRules = project.in(file("codestyle/rules"))
  .dependsOn(acyclicPlugin % "plugin->compile")
  .disablePlugins(Stryker4sPlugin, _root_.scalafix.sbt.ScalafixPlugin)
  .settings(
    name           := "gemstone-codestyle-rules",
    publish / skip := true,
    libraryDependencies +=
      ("ch.epfl.scala" %% "scalafix-core" % _root_.scalafix.sbt.BuildInfo.scalafixVersion)
        .cross(CrossVersion.for3Use2_13)
  )

lazy val codestyleInput = project.in(file("codestyle/input"))
  .dependsOn(acyclicPlugin % "plugin->compile")
  .disablePlugins(Stryker4sPlugin, _root_.scalafix.sbt.ScalafixPlugin)
  .settings(publish / skip := true, libraryDependencies += "org.scalameta" %% "munit" % munitVersion)

lazy val codestyleOutput = project.in(file("codestyle/output"))
  .dependsOn(acyclicPlugin % "plugin->compile")
  .disablePlugins(Stryker4sPlugin, _root_.scalafix.sbt.ScalafixPlugin)
  .settings(publish / skip := true, libraryDependencies += "org.scalameta" %% "munit" % munitVersion)

lazy val codestyleTests = project.in(file("codestyle/tests"))
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(codestyleInput, codestyleRules)
  .dependsOn(codestyleRules % ScalafixConfig, acyclicPlugin % "plugin->compile")
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
  .disablePlugins(Stryker4sPlugin, _root_.scalafix.sbt.ScalafixPlugin)
  .aggregate(acyclicPlugin, codestyleRules, codestyleInput, codestyleOutput, codestyleTests)
  .settings(name := "gemstone-codestyle", publish / skip := true)

// No dependency on core: its own tests can use these utilities without a cycle. Fixtures that need core types
// live in core test sources, which dependants consume through test->test.
lazy val testSupport = projectMatrix.in(file("tools/test-support"))
  .disablePlugins(Stryker4sPlugin)
  .dependsOn(codestyleRules % ScalafixConfig, acyclicPlugin % "plugin->compile")
  .settings(
    commonSettings,
    name                                   := "gemstone-test-support",
    publish / skip                         := true,
    libraryDependencies += "org.scalameta" %% "munit" % munitVersion
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)

lazy val core = module(projectMatrix)
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

lazy val isaTools = module(project.in(file("tools/isa")))
  .dependsOn(ptx.jvm(scala3Version), sass.jvm(scala3Version), amdgpu.jvm(scala3Version), spirv.jvm(scala3Version))
  .dependsOn(core.jvm(scala3Version) % "test->test")
  .settings(
    name                                  := "gemstone-isa-tools",
    Compile / mainClass                   := Some(s"$rootPackage.tools.isa.CLI"),
    libraryDependencies += "com.monovore" %% "decline" % declineVersion
  )

lazy val nvidia = module(projectMatrix.in(file("isa/nvidia")))
  .dependsOn(core % "compile->compile;test->test")
  .settings(name := "gemstone-isa-nvidia")
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(scalaVersions = Seq(scala3Version), settings = nativeTestSettings)

lazy val sass = module(projectMatrix.in(file("isa/sass")))
  .dependsOn(core % "compile->compile;test->test", nvidia % "compile->compile;test->test", ptx % "test->compile")
  .settings(
    CheckedInSources.settings,
    name                                   := "gemstone-isa-sass",
    libraryDependencies += "org.typelevel" %% "cats-core" % catsCoreVersion
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = nativeTestSettings ++ Seq(Test / nativeConfig ~= (_.withEmbedResources(true)))
  )

lazy val ptx = module(projectMatrix.in(file("isa/ptx")))
  .dependsOn(core % "compile->compile;test->test", nvidia % "compile->compile;test->test")
  .settings(
    CheckedInSources.settings,
    name                                   := "gemstone-isa-ptx",
    libraryDependencies += "org.typelevel" %% "cats-core" % catsCoreVersion
  )
  .jvmPlatform(scalaVersions = Seq(scala3Version))
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = nativeTestSettings ++ Seq(Test / nativeConfig ~= (_.withEmbedResources(true)))
  )

lazy val amdgpu = module(projectMatrix.in(file("isa/amdgpu")))
  .dependsOn(core % "compile->compile;test->test")
  .settings(
    CheckedInSources.settings,
    name := "gemstone-isa-amdgpu",
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
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = nativeTestSettings ++ Seq(
      Compile / nativeConfig ~= (_.withEmbedResources(true)),
      Test / nativeConfig ~= (_.withEmbedResources(true))
    )
  )

lazy val spirv = module(projectMatrix.in(file("isa/spirv")))
  .dependsOn(core % "compile->compile;test->test")
  .settings(
    CheckedInSources.settings,
    name                                   := "gemstone-isa-spirv",
    libraryDependencies += "org.typelevel" %% "cats-core" % catsCoreVersion,
    Test / envVars += ("GEMSTONE_NATIVE_DIRECTORY" -> s"${(native / nativeArtifact).value}")
  )
  .jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq.empty,
    configure = _.enablePlugins(Stryker4sPlugin).settings(
      mutationSettings,
      Test / fork       := true,
      strykerMutate     := Seq(s"src/main/scala/${rootPackage.replace('.', '/')}/isa/spirv/Binary.scala"),
      strykerTestFilter := Seq(s"$rootPackage.isa.spirv.BinarySuite")
    )
  )
  .nativePlatform(
    scalaVersions = Seq(scala3Version),
    settings = nativeTestSettings ++ Seq(Test / nativeConfig ~= (_.withEmbedResources(true)))
  )

lazy val server = module(projectMatrix.in(file("server")))
  .dependsOn(core % "compile->compile;test->test", ptx, sass, amdgpu, spirv)
  .settings(
    name := "gemstone-server",
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
      Compile / nativeConfig ~= (_.withMode(scala.scalanative.build.Mode.releaseFast).withEmbedResources(true)),
      Test / nativeConfig ~= (_.withEmbedResources(true))
    )
  )

lazy val benchmarks = module(projectMatrix.in(file("tools/benchmarks")))
  .dependsOn(core, ptx, sass, amdgpu, spirv, server)
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
        Some(selected),
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
    settings = nativeTestSettings ++ Seq(
      Compile / nativeConfig ~= (_.withEmbedResources(true)),
      Test / nativeConfig ~= (_.withEmbedResources(true))
    )
  )

lazy val conformanceTool = module(project.in(file("tools/conformance")))
  .dependsOn(core.jvm(scala3Version))
  .settings(
    name                 := "gemstone-conformance-tool",
    Compile / run / fork := true,
    Compile / run / javaOptions += "--sun-misc-unsafe-memory-access=allow",
    libraryDependencies += "com.monovore" %% "decline" % declineVersion
  )

lazy val codestyleTool = module(project.in(file("tools/codestyle")))
  .dependsOn(core.jvm(scala3Version))
  .settings(name := "gemstone-codestyle-tool")

lazy val externalCodestyleSettings = Seq(
  nativeSourceDirectories := Seq(baseDirectory.value.toPath),
  nativeCompileDatabase   := Def.uncached(nativeArtifact.value),
  format                  := RunMain.main(
    codestyleTool,
    s"$rootPackage.tools.codestyle.CLI",
    Def.task("format" +: nativeSourceDirectories.value.map(_.toString))
  ).value,
  lint := RunMain.main(
    codestyleTool,
    s"$rootPackage.tools.codestyle.CLI",
    Def.task(
      Seq("check", "--compile-database", s"${nativeCompileDatabase.value}") ++
        nativeSourceDirectories.value.map(_.toString)
    )
  ).value
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
      val targetSet = Def.spaceDelimited("[target-set]").parsed.headOption.getOrElse(conformanceTargetSet.value)
      val _         = CMake.build(
        source = baseDirectory.value.toPath / "artifacts",
        directory = conformanceDirectory.value / "build" / targetSet,
        arguments = Seq(
          s"-DCONTAINER_EXECUTABLE=$containerExecutable",
          s"-DROCM_VERSION=$rocmVersion",
          s"-DCONFORMANCE_TARGET_SET=$targetSet",
          s"-DCONFORMANCE_ARTIFACT_DIRECTORY=${conformanceDirectory.value / "artifacts"}",
          s"-DCONFORMANCE_DEPENDENCY_DIRECTORY=${(LocalRootProject / baseDirectory).value.toPath / ".cache/conformance"}",
          s"-DCONFORMANCE_WORK_DIRECTORY=${conformanceDirectory.value / "work"}",
          s"-DCONFORMANCE_OFFLINE=${if (offlineMode.value) "ON" else "OFF"}",
          s"-DSYCL_CTS_ENABLE_FULL_CONFORMANCE=${sys.env.getOrElse("SYCL_CTS_ENABLE_FULL_CONFORMANCE", "OFF")}"
        ) ++ sys.env.get("CONFORMANCE_ARTIFACT_PARALLELISM").map(value => s"-DCONFORMANCE_ARTIFACT_PARALLELISM=$value"),
        log = streams.value.log.info,
        buildTarget = Some("artifacts"),
        launcher = Seq("sh", s"${baseDirectory.value / "cmake/jobserver.sh"}", "--")
      )
      conformanceDirectory.value / "artifacts"
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
      val arguments = Def.spaceDelimited("[run option|target ...]").parsed
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
      val arguments = Def.spaceDelimited("[--target-set name] [--workload name] [backend[/architecture] ...]").parsed
      val targetSet = arguments.sliding(2).collectFirst { case Seq("--target-set", name) => name }
      val remaining = arguments.indexOf("--target-set") match {
        case -1    => arguments
        case index => arguments.patch(index, Nil, 2)
      }
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
            s"${conformanceDirectory.value / "artifacts"}",
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
            targetSet.getOrElse(conformanceTargetSet.value)
          ) ++ (if (conformanceJfr.value) Seq("--jfr", s"${conformanceDirectory.value / "jfr"}") else Seq.empty) ++
            remaining
        }
      )
    }.evaluated,
    cleanFiles := Seq(conformanceDirectory.value.toFile)
  )

lazy val gemstone = project.in(file("."))
  .disablePlugins(Stryker4sPlugin)
  .aggregate(testSupport.projectRefs *)
  .aggregate(codegenProject, isaTools, conformanceTool, codestyleTool)
  .aggregate(core.projectRefs *)
  .aggregate(server.projectRefs *)
  .aggregate(ptx.projectRefs *)
  .aggregate(sass.projectRefs *)
  .aggregate(nvidia.projectRefs *)
  .aggregate(amdgpu.projectRefs *)
  .aggregate(spirv.projectRefs *)
  .aggregate(benchmarks.projectRefs *)
  .aggregate(codestyle)
  .aggregate(native, conformance)
  .dependsOn(codestyleRules % ScalafixConfig)
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
          sass.jvm(scala3Version).id,
          generated = Seq((sass.jvm(scala3Version) / Compile / checkedInGeneratedSourceDirectory).value.toFile)
        ),
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
        Coverage.Entry(codegenProject.id),
        Coverage.Entry(isaTools.id),
        Coverage.Entry(benchmarks.jvm(scala3Version).id),
        Coverage.Entry(codestyleRules.id, tests = false),
        Coverage.Entry(codestyleTests.id, measure = false)
      ),
      includes = Seq(s"$rootPackage.*", "fix.*"),
      output = baseDirectory.value / "target" / "coverage"
    ),
    terminologyCheck := RunMain.main(
      codestyleTool,
      s"$rootPackage.tools.codestyle.CLI",
      Def.task(Seq("terminology", s"${baseDirectory.value}", "contract", "plan"))
    ).value,
    codegen := RunMain.main(
      codegenProject,
      s"$rootPackage.codegen.Codegen",
      Def.task(Seq("generate", "--root", s"${baseDirectory.value}"))
    ).value,
    cleanFiles += baseDirectory.value / "target"
  )

addCommandAlias("gemstoneRun", "server/run run")

addCommandAlias(
  "lint",
  ";terminologyCheck;codegen/checkEnvironment;scalafmtCheckAll;scalafmtSbtCheck;scalafixAll --check;native/lint;conformance/lint"
)
