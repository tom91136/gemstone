package net.kurobako.gemstone.server

import net.kurobako.gemstone.Constraint.NoNulString

import net.kurobako.gemstone.Constraint.Positive
import net.kurobako.gemstone.NonEmptyVec
import net.kurobako.gemstone.launcher.Environment
import net.kurobako.gemstone.vm.TaskLimits

import java.nio.file.Files
import scala.concurrent.duration.*
import scala.util.Using

/** The same mixed-backing and pointer-publication scenarios run through every native API. */
final class UnifiedMemoryReviewSuite extends munit.FunSuite {
  override val munitTimeout = 150.seconds
  for (api <- List("cuda", "hip", "hsa", "levelzero", "opencl"))
    test(s"$api mixed memory and pointer publication") {
      val directory = NativeTestProcess.nativeDirectory
      val client    = directory.resolve(s"gemstone_memory_$api")
      assert(Files.isExecutable(client))
      val vendors = Files.createTempDirectory("gemstone-memory-review-")
      val icd     = vendors.resolve("gemstone.icd")
      val _       = Files.writeString(icd, directory.resolve("libOpenCL_gemstone.so.1").toString + "\n")
      try
        Using.resource(GemstoneRPCServer.start(
          TaskLimits(1000000000L, 64),
          Map("GEMSTONE_EXECUTION_PARALLELISM" -> "2")
        )) { server =>
          val result = NativeTestProcess.run(
            NonEmptyVec(NoNulString.unsafe(client.toString)),
            directory,
            Environment(
              Map(
                "GEMSTONE_MEMORY_PROBE" -> "review",
                "GEMSTONE_RPC_PORT"     -> server.port.raw.toString,
                "GEMSTONE_RPC_CLIENT"   -> server.client.raw.toString,
                "LD_LIBRARY_PATH"       ->
                  List(directory.toString, sys.env.getOrElse("LD_LIBRARY_PATH", "")).mkString(":"),
                "ZE_ENABLE_ALT_DRIVERS" -> directory.resolve("libze_gemstone.so.1").toString,
                "OCL_ICD_VENDORS"       -> vendors.toString,
                "OCL_ICD_FILENAMES"     -> directory.resolve("libOpenCL_gemstone.so.1").toString
              ),
              Set("GEMSTONE_SYSTEM_PAGING", "UR_ADAPTERS_FORCE_LOAD")
            ),
            120.seconds
          )
          assertEquals(result.exit, 0, result.diagnostic)
          assertEquals(
            result.stdout.linesIterator.filter(_.startsWith("verified ")).toList,
            List(
              "verified disjoint device writes",
              "verified mixed backing publication",
              "verified synchronized device pointer replacement"
            ),
            result.diagnostic
          )
        }
      finally {
        val _ = Files.deleteIfExists(icd)
        val _ = Files.deleteIfExists(vendors)
      }
    }
}
