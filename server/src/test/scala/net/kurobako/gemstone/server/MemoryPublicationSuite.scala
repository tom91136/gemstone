package net.kurobako.gemstone.server

import java.nio.file.Files
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.*
import scala.util.Using

/** The same mixed-backing and pointer-publication scenarios run through every native API. */
class MemoryPublicationSuite extends munit.FunSuite {
  private val Booleans = List(false, true)

  override val munitTimeout: Duration = 150.seconds
  for {
    api  <- List("cuda", "hip", "hsa", "levelzero", "opencl")
    unix <- Booleans
  }
    test(s"$api mixed memory and pointer publication unix=$unix") {
      val directory = native.directory
      val client    = directory.resolve(s"gemstone_memory_$api")
      assert(Files.isExecutable(client))
      files.temporary { vendors =>
        val icd = vendors.resolve("gemstone.icd")
        val _   = Files.writeString(icd, directory.resolve("libOpenCL_gemstone.so.1").toString + "\n")
        Using.resource(Server.start(
          Server.Settings(
            limits = TaskControl.Limits(1000000000L),
            parallelism = 2,
            bind = if (unix) Transport.Bind.Unix(vendors.resolve("rpc.sock")) else Transport.Bind.Tcp()
          )
        )) { server =>
          val result = native.run(
            Vec.nonEmpty(client.toString),
            directory,
            native.serverEnvironment(
              server.endpoint,
              server.client,
              native.providerEnvironment ++ Map(env.MemoryProbe -> "review", "OCL_ICD_VENDORS" -> vendors.toString),
              Set(env.SystemPaging, "UR_ADAPTERS_FORCE_LOAD")
            ),
            120.seconds
          )
          assertEquals(result.exit, 0, result.diagnostic)
          assertEquals(
            result.stdout.linesIterator.filter(_.startsWith("verified ")).toList,
            List(
              "verified disjoint device writes",
              "verified mixed backing publication",
              "verified synchronised device pointer replacement"
            ),
            result.diagnostic
          )
        }
      }
    }
}
