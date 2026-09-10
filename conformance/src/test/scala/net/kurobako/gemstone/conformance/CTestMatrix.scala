package net.kurobako.gemstone.conformance

import ConformanceSupport.*
import net.kurobako.gemstone.launcher.*

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import scala.jdk.CollectionConverters.*

/** CTest owns case scheduling. JSONL offsets let each tiny driver read only its own case. */
private[conformance] final class CTestMatrix(val directory: Path, endpoint: Endpoint, parallelism: Int)
    extends AutoCloseable {
  require(parallelism > 0, "CTest parallelism must be positive")
  Files.createDirectories(directory)
  val resourceFile: Path = directory.resolve("resources.json")
  Files.writeString(
    resourceFile,
    ujson.write(ujson.Obj(
      "version" -> ujson.Obj("major" -> 1, "minor" -> 0),
      "local"   -> ujson.Arr(ujson.Obj("cpus" -> ujson.Arr(ujson.Obj("id" -> "shared", "slots" -> parallelism))))
    ))
  )
  private val catalog    = directory.resolve("cases.jsonl")
  private val output     = Files.newOutputStream(catalog)
  private val serverFile = directory.resolve("server.json")
  Files.writeString(
    serverFile,
    ujson.write(ujson.Obj(
      "port"  -> endpoint.port,
      "token" -> endpoint.token,
      "pid"   -> ProcessHandle.current().pid()
    ))
  )
  Vector(catalog, serverFile).foreach { path =>
    val _ = Files.setPosixFilePermissions(path, java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"))
  }
  private val tests       = Files.newBufferedWriter(directory.resolve("CTestTestfile.cmake"))
  private var offset      = 0L
  private var count       = 0
  private var oraclePlans = Map.empty[String, String]
  private var testNames   = Set.empty[String]

  private def jsonEnvironment(values: Map[String, String]): ujson.Obj =
    ujson.Obj.from(values.iterator.map { case (key, value) => key -> ujson.Str(value) })

  private def quote(value: String): String = {
    val equals = Iterator.iterate("=")(_ + "=").find(mark => !value.contains("]" + mark + "]")).get
    "[" + equals + "[" + value + "]" + equals + "]"
  }

  private def oracleEnvironment(provider: String): Map[String, String] =
    TestProfile.current.oracle(provider).toVector.flatMap { target =>
      if provider == "cuda" then Vector("CUDA_VISIBLE_DEVICES" -> "0")
      else if provider == "hip" || provider == "hsa" then Vector("ROCR_VISIBLE_DEVICES" -> "0")
      else if provider == "sycl-levelzero" then Vector("ONEAPI_DEVICE_SELECTOR" -> "level_zero:0")
      else if provider == "opencl" || provider == "sycl-opencl" then {
        val vendors = Path.of("/etc/OpenCL/vendors")
        require(Files.isDirectory(vendors), s"profile ${TestProfile.current.file} requires an OpenCL ICD")
        val files    = Files.list(vendors)
        val selected = try
          files.iterator.asScala.filter(path => Files.isRegularFile(path) && path.toString.endsWith(".icd"))
            .find { path =>
              val text = (path.getFileName.toString + Files.readString(path)).toLowerCase
              target.target.toLowerCase match {
                case "intel" => text.contains("igdrcl")
                case "amd"   => text.contains("amdocl")
                case vendor  => text.contains(vendor)
              }
            }
        finally files.close()
        val icd = selected.getOrElse(
          throw IllegalArgumentException(s"no ${target.target} OpenCL ICD for profile ${TestProfile.current.file}")
        )
        val isolated = directory.resolve("oracle-icd-" + target.resource + "-" + icd.getFileName.toString)
        Files.createDirectories(isolated)
        Files.copy(icd, isolated.resolve(icd.getFileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        Vector("OCL_ICD_VENDORS" -> (isolated.toString + java.io.File.separator)) ++
          (if provider == "sycl-opencl" then Vector("ONEAPI_DEVICE_SELECTOR" -> "opencl:0") else Vector.empty)
      } else Vector.empty
    }.toMap

  def add(
      name: String,
      provider: String,
      api: ComputeAPI,
      layout: RuntimeLayout,
      command: Vector[String],
      workingDirectory: Path,
      environment: Map[String, String],
      timeoutSeconds: Double,
      oracleCommand: Option[Vector[String]] = None,
      oracleExtra: Map[String, String] = Map.empty,
      validator: Option[String] = None,
      reportDirectories: Option[(Path, Path)] = None,
      buildFailure: Option[String] = None,
      oracleName: Option[String] = None,
      oracleProvider: Option[String] = None,
      runtimeRoute: Option[ujson.Obj] = None,
      cts: Option[ujson.Obj] = None
  ): Unit = {
    val host      = provider == "host"
    val profile   = TestProfile.current
    val inherited = TestProfile.environment ++ environment
    val injected  = if host || buildFailure.nonEmpty then Environment(Map.empty, Set.empty)
    else
      Launch.plan(api, layout, endpoint, inherited).fold(
        error => throw IllegalArgumentException(error.message),
        _.environment
      )
    val nativeProvider = oracleProvider.getOrElse(provider)
    val nativeName     = oracleName.getOrElse(name)
    val oracle         = profile.oracle(nativeProvider)
    val skip = Option.when(!host && oracle.isEmpty)(s"${profile.file.getFileName}: no GPU oracle for $nativeProvider")
    val baseUnset = Set(
      "LD_PRELOAD",
      "OCL_ICD_VENDORS",
      "OCL_ICD_FILENAMES",
      "ZE_ENABLE_ALT_DRIVERS",
      "HSA_TOOLS_LIB",
      "HSA_ENABLE_DTIF",
      "ONEAPI_DEVICE_SELECTOR",
      "SYCL_DEVICE_FILTER"
    )
    val spec = ujson.Obj(
      "name"        -> name,
      "api"         -> (if host then "host" else api.id),
      "timeout"     -> timeoutSeconds,
      "allow_skip"  -> (name.startsWith("unified/") || cts.nonEmpty),
      "oracle_name" -> nativeName,
      "server_file" -> serverFile.toString,
      "oracle"      -> ujson.Obj(
        "command"     -> ujson.Arr.from(oracleCommand.getOrElse(command)),
        "directory"   -> workingDirectory.toString,
        "unset"       -> ujson.Arr.from(baseUnset),
        "environment" -> jsonEnvironment((profile.environment ++ environment ++ oracleExtra ++
          layout.openCLLoader.map(path => "LD_PRELOAD" -> path.toString) ++
          (if skip.isEmpty && !host then oracleEnvironment(nativeProvider) else Map.empty)).filterNot(_._1.startsWith(
          "GEMSTONE_"
        )))
      ),
      "gemstone" -> ujson.Obj(
        "command"     -> ujson.Arr.from(command),
        "directory"   -> workingDirectory.toString,
        "unset"       -> ujson.Arr.from(baseUnset ++ injected.unset),
        "environment" -> jsonEnvironment(inherited.filter { case (key, _) =>
          Set(
            "GEMSTONE_TRACE",
            "GEMSTONE_PERF_TRACE",
            "GEMSTONE_HSA_TRACE",
            "GEMSTONE_HIP_TRACE",
            "GEMSTONE_OPENCL_TRACE"
          ).contains(key)
        } ++ environment ++ injected.set)
      )
    )
    runtimeRoute.foreach(value => spec("runtime_route") = value)
    cts.foreach(value => spec("cts") = value)
    skip.foreach(value => spec("oracle")("skip") = value)
    validator.foreach(value => spec("validator") = value)
    buildFailure.foreach(value => spec("build_failure") = value)
    reportDirectories.foreach { case (oracle, gemstone) =>
      spec("oracle")("report_directory") = oracle.toString
      spec("oracle")("directory") = oracle.toString
      spec("gemstone")("report_directory") = gemstone.toString
      spec("gemstone")("directory") = gemstone.toString
    }
    val bytes     = ujson.write(spec).getBytes(StandardCharsets.UTF_8)
    val signature = ujson.write(ujson.Obj(
      "plan"          -> spec("oracle"),
      "timeout"       -> timeoutSeconds,
      "validator"     -> validator.fold[ujson.Value](ujson.Null)(ujson.Str(_)),
      "cts"           -> cts.getOrElse(ujson.Null),
      "build_failure" -> buildFailure.fold[ujson.Value](ujson.Null)(ujson.Str(_)),
      "api"           -> spec("api"),
      "allow_skip"    -> spec("allow_skip")
    ))
    val emitOracle = oraclePlans.get(nativeName) match {
      case Some(previous) =>
        require(previous == signature, s"conflicting shared oracle plan: $nativeName")
        false
      case None => true
    }
    val roles = (if emitOracle then Vector("oracle") else Vector.empty) ++
      (if host then Vector.empty else Vector("gemstone"))
    val identities = roles.map(role => role -> s"$role/${if role == "oracle" then nativeName else name}")
    identities.foreach { case (_, id) => require(!testNames.contains(id), s"duplicate CTest case: $id") }
    // Rejected plans must leave the catalog offset and oracle registry unchanged.
    output.write(bytes)
    output.write('\n')
    if emitOracle then oraclePlans = oraclePlans.updated(nativeName, signature)
    testNames ++= identities.map(_._2)
    identities.foreach { case (role, id) =>
      val arguments = Vector(
        "python3",
        root.resolve("conformance/tools/case-run.py").toString,
        catalog.toString,
        offset.toString,
        bytes.length.toString,
        role
      )
      tests.write(s"add_test(${quote(id)} ${arguments.map(quote).mkString(" ")})\n")
      tests.write(s"set_tests_properties(${quote(id)} PROPERTIES TIMEOUT ${timeoutSeconds +
          20} SKIP_RETURN_CODE 77 LABELS ${quote(
          s"$role;$provider;${name.takeWhile(_ != '/')}"
        )}")
      if role == "gemstone" || host then
        tests.write(" RESOURCE_GROUPS " + quote(s"cpus:${CTestMatrix.cpuSlots(name, role, parallelism)}"))
      if role == "oracle" then oracle.foreach(value => tests.write(" RESOURCE_LOCK " + quote(value.resource)))
      tests.write(")\n")
      count += 1
    }
    offset += bytes.length + 1L
  }

  def size: Int              = count
  override def close(): Unit = { output.close(); tests.close() }
}

private[conformance] object CTestMatrix {

  /** Workloads measured near their deadline reserve the pool to avoid contention from concurrent cases. */
  def cpuSlots(name: String, role: String, parallelism: Int): Int = {
    val parts      = name.split('/').toVector
    val gemm       = name.startsWith("unified/") && parts.contains("blas") && parts.exists(_.contains("gemm"))
    val cloverLeaf = parts match {
      case Vector("miniapps", "cloverleaf", "omp-target", _, "hsa") => true
      case _                                                        => false
    }
    if (role == "gemstone" && (cloverLeaf || gemm)) parallelism else 1
  }
}
