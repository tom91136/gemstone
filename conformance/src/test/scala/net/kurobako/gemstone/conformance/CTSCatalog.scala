package net.kurobako.gemstone.conformance

import ConformanceSupport.*
import net.kurobako.gemstone.launcher.*
import java.nio.file.{Files, Path}
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import scala.jdk.CollectionConverters.*

/** Pinned upstream CTS artifacts feed the same case scheduler and report pipeline as unified cases. */
private[conformance] final class CTSCatalog {
  private def digest(bytes: Array[Byte]): String = java.util.HexFormat.of().formatHex(
    MessageDigest.getInstance("SHA-256").digest(bytes)
  ).take(20)

  private def artifact(suite: String): Path = {
    val source = root.resolve(s"conformance/cts/$suite")
    val files  = Files.walk(source)
    val inputs = try files.iterator().asScala.filter { path =>
        val name = path.getFileName.toString
        Files.isRegularFile(path) &&
        (Set("Containerfile", "CMakeLists.txt").contains(name) ||
          Vector(".sh", ".py", ".json", ".patch", ".cmake").exists(name.endsWith))
      }.toVector.sortBy(_.toString)
    finally files.close()
    val hash = MessageDigest.getInstance("SHA-256")
    (inputs ++ Vector("container-common.sh", "runtime-bundle.sh", "fixture-run.py").map(x =>
      root.resolve("conformance/tools").resolve(x)
    ) :+
      root.resolve("conformance/containers/Containerfile")).foreach { path =>
      hash.update(root.relativize(path).toString.getBytes(StandardCharsets.UTF_8));
      hash.update(Files.readAllBytes(path))
    }
    val image = if suite == "sycl" then "GEMSTONE_SYCL_IMAGE" else "GEMSTONE_HOST_IMAGE"
    hash.update(sys.env.getOrElse(image, "pinned").getBytes(StandardCharsets.UTF_8))
    val key    = java.util.HexFormat.of().formatHex(hash.digest()).take(20)
    val base   = sys.env.get("GEMSTONE_CONFORMANCE_ARTIFACTS").fold(target.resolve("artifacts"))(Path.of(_))
    val output = base.resolve("cts").resolve(suite).resolve(key)
    if !Files.isRegularFile(output.resolve("cases.jsonl")) || !Files.isRegularFile(output.resolve("manifest.json")) then
      runChecked(
        Vector("bash", source.resolve("container-build.sh").toString, output.toString),
        root,
        TestProfile.current.environment ++ TestProfile.current.buildEnvironment
      )
    val manifest = ujson.read(Files.readString(output.resolve("manifest.json")))
    require(manifest("revision").str.matches("[0-9a-f]{40}"), s"$suite CTS revision is not pinned")
    output
  }

  def emitCTest(matrix: CTestMatrix): Unit = {
    val suites = sys.env.getOrElse("GEMSTONE_CTS_SUITES", "opencl,sycl").split(',').toVector.filter(_.nonEmpty)
    require(suites.forall(Set("opencl", "sycl")), "unknown CTS suite")
    for (suite <- suites) {
      val providers = if suite == "sycl" then Vector("sycl-levelzero", "sycl-opencl") else Vector("opencl")
      if providers.exists(provider => maySelectPrefix(s"cts/$provider/")) then {
        try {
          val output   = artifact(suite)
          val manifest = ujson.read(Files.readString(output.resolve("manifest.json")))
          val entries  =
            Files.readString(output.resolve("cases.jsonl")).linesIterator.filter(_.nonEmpty).map(ujson.read(_)).toVector
          require(entries.nonEmpty, s"$suite CTS discovery is empty")
          require(entries.map(_("name").str).distinct.size == entries.size, s"duplicate $suite CTS cases")
          val defaults = if sys.env.get("GEMSTONE_CONFORMANCE_FILTER").exists(_.nonEmpty) then entries
          else entries.filter(_.obj.get("default").forall(_.bool))
          for (provider <- providers) {
            val selected = select(defaults.zipWithIndex.map { case (entry, index) =>
              index -> s"cts/$provider/${entry("name").str}"
            })
            for ((index, name) <- selected) {
              val entry     = defaults(index)
              val execution = entry("execution").str
              require(Set("host", "device").contains(execution), s"unknown CTS execution kind: $execution")
              val caseDirectory =
                matrix.directory.resolve("cts").resolve(provider).resolve(digest(name.getBytes(StandardCharsets.UTF_8)))
              val oracleDirectory       = Files.createDirectories(caseDirectory.resolve("oracle"))
              val emulatorDirectory     = Files.createDirectories(caseDirectory.resolve("gemstone"))
              def expand(value: String) = value.replace("{artifact}", output.toString)
              val argv                  = entry("command").arr.toVector.map(x => expand(x.str))
              require(argv.nonEmpty, s"empty CTS command: $name")
              val executable = output.resolve(argv.head).normalize()
              require(
                executable.startsWith(output) && Files.isExecutable(executable),
                s"invalid CTS executable: $executable"
              )
              val layout = provider match {
                case "sycl-levelzero" => RuntimeLayout(levelZeroDriver =
                    Some(providerPath("GEMSTONE_LEVELZERO_DRIVER", "libze_gemstone.so.1").get)
                  )
                case _ =>
                  RuntimeLayout(openCLICD = Some(openCLICD.get), openCLLoader = Some(preferredOpenCLLoader(output).get))
              }
              val compiler = TestProfile.environment.filter { case (key, _) =>
                Set("GEMSTONE_OPENCL_COMPILER", "GEMSTONE_OPENCL_LLVM_SPIRV", "GEMSTONE_OPENCL_LLVM_LINK").contains(key)
              }
              val environment = compiler ++ Map("LD_LIBRARY_PATH" ->
                (output.resolve("lib").toString +
                  sys.env.get("LD_LIBRARY_PATH").filter(_.nonEmpty).fold("")(":" + _))) ++
                entry.obj.get("environment").toVector.flatMap(_.obj.iterator).map { case (k, v) =>
                  k -> expand(v.str)
                }.toMap ++
                Option.when(suite == "sycl") {
                  Map("UR_ADAPTERS_FORCE_LOAD" -> syclAdapter(output, provider == "sycl-opencl").get.toString)
                }.getOrElse(Map.empty)
              val command         = executable.toString +: argv.tail
              val deviceCase      = suite == "sycl" && execution == "device"
              val emulatorCommand = if deviceCase then
                command ++
                  Vector("--device", "Gemstone", "--info-dump", emulatorDirectory.resolve("device.info").toString)
              else command
              val oracleCommand = if deviceCase then
                command ++ Vector("--info-dump", oracleDirectory.resolve("device.info").toString)
              else command
              matrix.add(
                name,
                provider,
                if suite == "sycl" then ComputeAPI.SYCL else ComputeAPI.OpenCL,
                layout,
                emulatorCommand,
                output,
                environment,
                entry("timeout_seconds").num,
                oracleCommand = Some(oracleCommand),
                validator = Some(entry("validator").str),
                reportDirectories = Some(oracleDirectory -> emulatorDirectory),
                cts = Some(ujson.Obj(
                  "suite"     -> suite,
                  "test_name" -> entry("test_name"),
                  "execution" -> execution,
                  "revision"  -> manifest("revision")
                ))
              )
            }
          }
        } catch {
          case scala.util.control.NonFatal(error) => matrix.add(
              s"cts/$suite/fixture-build",
              suite,
              if suite == "sycl" then ComputeAPI.SYCL else ComputeAPI.OpenCL,
              RuntimeLayout(),
              Vector.empty,
              root,
              Map.empty,
              120,
              buildFailure = Some(error.toString)
            )
        }
      }
    }
  }
}
