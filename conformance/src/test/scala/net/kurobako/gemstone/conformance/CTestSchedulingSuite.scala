package net.kurobako.gemstone.conformance

import net.kurobako.gemstone.launcher.*
import java.nio.file.Files
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

final class CTestSchedulingSuite extends munit.FunSuite {
  for (cpus <- Vector(1, 4))
    test(s"a whole-pool CPU job overlaps a GPU oracle with $cpus CPU slots") {
      val directory = Files.createTempDirectory("gemstone-ctest-resources-")
      try {
        val library = Files.createFile(directory.resolve("libcuda.so"))
        val matrix  = CTestMatrix(directory, Endpoint(1, "a" * 64), cpus)
        val names   = Vector(
          "unified/cuda/edge-cases/blas/padded-gemm/f32/n1023",
          "unified/cuda/edge-cases/blas/padded-gemm/f32/n1024",
          "miniapps/cloverleaf/omp-target/gfx1030/hsa"
        )
        try for (name <- names)
            matrix.add(
              name,
              if name.startsWith("miniapps/") then "hsa" else "cuda",
              if name.startsWith("miniapps/") then ComputeAPI.HSA else ComputeAPI.CUDA,
              if name.startsWith("miniapps/") then RuntimeLayout(hsaRuntime = Some(library))
              else RuntimeLayout(cudaDriver = Some(library)),
              Vector("unused"),
              directory,
              Map.empty,
              5
            )
        finally matrix.close()
        val script = directory.resolve("rendezvous.py")
        Files.writeString(
          script,
          """import os, pathlib, sys, time
root = pathlib.Path(__file__).parent
role, case, slots = sys.argv[1:]
def mark(name): (root / (case + '.' + name)).touch()
def wait(name):
    deadline = time.monotonic() + 5
    while not (root / (case + '.' + name)).exists():
        assert time.monotonic() < deadline, 'CPU and GPU jobs failed to overlap: ' + name
        time.sleep(0.01)
if role == 'oracle':
    mark('oracle_started')
    wait('cpu_started')
    mark('oracle_observed_cpu')
else:
    assert os.environ['CTEST_RESOURCE_GROUP_0_CPUS'] == 'id:shared,slots:' + slots
    active = root / 'cpu_active'
    descriptor = os.open(active, os.O_CREAT | os.O_EXCL | os.O_WRONLY)
    try:
        mark('cpu_started')
        wait('oracle_started')
        wait('oracle_observed_cpu')
    finally:
        os.close(descriptor)
        active.unlink()
"""
        )
        val testFile = directory.resolve("CTestTestfile.cmake")
        val original = Files.readString(testFile)
        assert(!original.contains("PROCESSORS"), "CPU reservations must not consume GPU process slots")
        // Keep the generated scheduling properties; replace only the workload commands with bounded gates.
        val commands = "add_test\\(\\[=\\[(.*?)\\]=\\].*".r
        val replaced = original.linesIterator.map {
          case commands(name) =>
            val role = name.takeWhile(_ != '/')
            val size = name.split('/').last
            s"add_test([=[$name]=] python3 [=[$script]=] $role $size $cpus)"
          case line => line
        }.mkString("\n")
        Files.writeString(testFile, replaced + "\n")
        val result = ConformanceSupport.run(
          Vector(
            "ctest",
            "--test-dir",
            directory.toString,
            "--parallel",
            (cpus + 1).toString,
            "--resource-spec-file",
            matrix.resourceFile.toString,
            "--output-on-failure"
          ),
          directory,
          timeout = 15.seconds
        )
        assertEquals(result.exit, 0, result.diagnostic)
        for (name <- names)
          assert(Files.exists(directory.resolve(s"${name.split('/').last}.oracle_observed_cpu")))
      } finally {
        val paths = Files.walk(directory)
        try paths.iterator().asScala.toVector.sortBy(_.getNameCount).reverse.foreach { path =>
            val _ = Files.deleteIfExists(path)
          }
        finally paths.close()
      }
    }

  test("CloverLeaf OpenMP reservation preserves oracle concurrency and other model scheduling") {
    for {
      cpus   <- Vector(1, 8, 32)
      target <- Vector("gfx1030", "gfx1100")
    } {
      val name = s"miniapps/cloverleaf/omp-target/$target/hsa"
      assertEquals(CTestMatrix.cpuSlots(name, "gemstone", cpus), cpus)
      assertEquals(CTestMatrix.cpuSlots(name, "oracle", cpus), 1)
      for (other <- Vector(s"miniapps/cloverleaf/hip/$target/hip-hsa", s"miniapps/tealeaf/omp-target/$target/hsa"))
        assertEquals(CTestMatrix.cpuSlots(other, "gemstone", cpus), 1)
    }
  }

  test("GEMM cases reserve the CPU pool") {
    assertEquals(CTestMatrix.cpuSlots("unified/hip/blas/gemm-tn/f32/7x5x9", "gemstone", 32), 32)
    assertEquals(CTestMatrix.cpuSlots("unified/hip/blas/gemm-tn/f32/7x5x9", "oracle", 32), 1)
  }

  test("two runtime routes share one identical native oracle and isolated output directories") {
    val directory = Files.createTempDirectory("gemstone-shared-oracle-")
    try {
      val library                                              = Files.createFile(directory.resolve("libcuda.so"))
      val oracle                                               = Files.createDirectories(directory.resolve("oracle"))
      val matrix                                               = CTestMatrix(directory, Endpoint(1, "a" * 64), 4)
      def add(name: String, command: String = "same-artifact") = matrix.add(
        name,
        "cuda",
        ComputeAPI.CUDA,
        RuntimeLayout(cudaDriver = Some(library)),
        Vector(command),
        directory,
        Map.empty,
        10,
        validator = Some("tealeaf"),
        reportDirectories = Some(oracle -> directory.resolve(name.takeWhile(_ != '/'))),
        oracleName = Some("shared/workload"),
        oracleProvider = Some("cuda")
      )
      try {
        add("first/workload")
        add("second/workload")
        assertEquals(matrix.size, 3)
        val error = intercept[IllegalArgumentException](add("conflicting/workload", "different-artifact"))
        assert(error.getMessage.contains("conflicting shared oracle"))
        intercept[IllegalArgumentException](add("first/workload"))
        add("recovered/workload")
      } finally matrix.close()
      val tests = Files.readString(directory.resolve("CTestTestfile.cmake"))
      assertEquals(tests.linesIterator.count(_.startsWith("add_test(")), 4)
      assert(!tests.contains("DEPENDS"), "oracle and emulator execution must remain independent")
      val specs = Files.readString(directory.resolve("cases.jsonl")).linesIterator.map(ujson.read(_)).toVector
      assertEquals(specs.map(_("oracle")("directory").str).distinct, Vector(oracle.toString))
      assertEquals(specs.map(_("gemstone")("directory").str).distinct.size, 3)
      assertEquals(specs.map(_("oracle_name").str).distinct, Vector("shared/workload"))
      assertEquals(specs.map(_("name").str), Vector("first/workload", "second/workload", "recovered/workload"))
      val catalogBytes = Files.readAllBytes(directory.resolve("cases.jsonl"))
      val quoted       = "\\[=\\[(.*?)\\]=\\]".r
      tests.linesIterator.filter(_.startsWith("add_test(")).foreach { line =>
        val args     = quoted.findAllMatchIn(line).map(_.group(1)).toVector
        val start    = args(4).toInt
        val length   = args(5).toInt
        val indexed  = ujson.read(String(catalogBytes, start, length, java.nio.charset.StandardCharsets.UTF_8))
        val expected = if args(6) == "oracle" then indexed("oracle_name").str else indexed("name").str
        assertEquals(args.head, s"${args(6)}/$expected")
      }
    } finally {
      val paths = Files.walk(directory)
      try paths.iterator().asScala.toVector.sortBy(_.getNameCount).reverse.foreach { path =>
          val _ = Files.deleteIfExists(path)
        }
      finally paths.close()
    }
  }

}
