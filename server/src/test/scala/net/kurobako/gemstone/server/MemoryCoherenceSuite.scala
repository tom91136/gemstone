package net.kurobako.gemstone.server

import java.nio.file.Files
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.*
import scala.collection.mutable
import scala.concurrent.duration.*
import scala.util.Using

/** RPC traffic, forced interleavings, host-memory extensions and workgroups regressions across all APIs. */
class MemoryCoherenceSuite extends Suite {
  override def baseTimeout: FiniteDuration = 150.seconds

  private enum Scenario derives CanEqual {
    case Device, OrdinaryHost, Concurrent, Submission, Overlap, Compare

    def probe: Option[String] = this match {
      case Submission => Some("submission")
      case Overlap    => Some("overlap")
      case Compare    => Some("compare")
      case _          => None
    }
  }
  import Scenario.*

  private val AtomicWidths   = List(0, 32, 64)
  private val FenceModes     = List(false, true)
  private val WorkItemCounts = List(1, 128)
  private val Passes         = List(0, 1)
  private val ProbeScenarios = List(Submission, Overlap, Compare)
  private val HostScenarios  = List(Device, Concurrent, OrdinaryHost)

  private def allocationKinds(api: String): List[String] = api match {
    case "cuda"      => List("managed", "pinned", "registered")
    case "hip"       => List("managed", "host", "registered")
    case "hsa"       => List("region", "pool", "locked")
    case "levelzero" => List("shared", "host")
    case "opencl"    => List("svm", "usm_host", "usm_shared")
  }

  for {
    api         <- List("cuda", "hip", "hsa", "levelzero", "opencl")
    atomicWidth <- AtomicWidths
    fenced      <- if (atomicWidth != 0) FenceModes else List(false)
    workItems   <- if (atomicWidth != 0 && !fenced) WorkItemCounts else List(1)
    scenario    <- if (atomicWidth != 0) List(Concurrent) ++
      (if (!fenced && workItems == 1) ProbeScenarios else Nil)
    else HostScenarios
    if scenario != Compare || atomicWidth == 32
    allocationKind <- if (scenario == Compare) allocationKinds(api).take(1)
    else if (scenario == Concurrent || scenario.probe.nonEmpty) allocationKinds(api) else List("default")
  } test(munit.TestOptions(
    s"$api $scenario allocation=$allocationKind atomic=$atomicWidth fence=$fenced work-items=$workItems"
  ).withTags(if (atomicWidth == 0) Set.empty else Set(munit.Tag("atomic")))) {
    val atomicProbe = atomicWidth != 0
    val ordinary    = scenario == OrdinaryHost
    val concurrent  = scenario == Concurrent
    val directory   = native.directory
    val client      = directory.resolve(s"gemstone_memory_$api")
    assert(Files.isExecutable(client), s"missing native memory client: $client")
    // The C++ fixture runs 32 serial RMW rounds, or 128 lanes in four groups. These independent
    // expected counts verify that every fixture operation crossed the RPC/checkpoint boundary.
    Using.resource(checkpoints.Checkpoint(
      forceContention = atomicProbe,
      expectedOperations = if (workItems > 1) workItems else 32
    )) { checkpoint =>
      val vendors = checkpoint.directory
      val _ = Files.writeString(vendors.resolve("gemstone.icd"), directory.resolve(API.OpenCL.lib).toString + "\n")
      val atomicWords = mutable.ArrayBuffer.empty[(Long, Long)]
      val archive     = checkpoint.directory.resolve("rpc.msgpack")
      val settings    = Server.Settings(
        parallelism = 2,
        record = Some(archive),
        limits = TaskControl.Limits((if (atomicProbe) 100000 else 10000000).toLong.unsafe[Long > 0]),
        requestTimeoutMillis = 30000
      )
      Using.resource(if (concurrent)
        Server.start(settings, checkpoint.backends(_))
      else Server.start(settings)) {
        server =>
          val probes =
            (if (ordinary) Vector.empty else Vector("rpc_only")) ++
              scenario.probe.toVector ++
              (if (workItems > 1) Vector("multilane") else Vector.empty) ++
              (if (fenced) Vector("fence") else Vector.empty) ++
              (if (atomicProbe) Vector("atomic") else Vector.empty)
          val base = native.providerEnvironment ++ Map(
            env.TestMemoryProbeList      -> probes.mkString(","),
            env.TestMemoryAllocationMode -> allocationKind,
            "OCL_ICD_VENDORS"            -> vendors.toString
          ) ++
            (if (atomicProbe) Map(env.TestMemoryAtomicBits -> atomicWidth.toString)
             else Map.empty[String, String]) ++
            (if (concurrent) Map(env.TestMemoryConcurrencyDir -> checkpoint.directory.toString)
             else Map.empty[String, String])
          val paging    = if (ordinary) Map(env.SystemPagingOn -> "1") else Map.empty[String, String]
          val arguments = Vector(client.toString) ++ (if (ordinary) Vector("ordinary") else Vector.empty)
          val result    = native.run(
            Vec.from(arguments).unsafe,
            directory,
            native.serverEnvironment(
              server.endpoint,
              server.client,
              base ++ paging,
              if (ordinary) Set.empty else Set(env.SystemPagingOn, "UR_ADAPTERS_FORCE_LOAD")
            ),
            120.seconds
          )
          assertEquals(result.exit, 0, result.diagnostic)
          result.stdout.linesIterator.filter(_.startsWith("atomic-input ")).foreach { line =>
            val fields = line.split(' ')
            val base   = fields(1).toLong
            val width  = fields(2).toLong
            atomicWords += ((base, base + width))
            atomicWords += ((base + 2 * width, base + 3 * width))
          }
          if (atomicProbe) assertEquals(atomicWords.size, if (scenario == Overlap) 4 else 2, result.diagnostic)
          val expected = if (atomicProbe) List(scenario match {
            case Submission => s"verified asynchronous submission bits=$atomicWidth rounds=32"
            case Overlap    => s"verified overlapping queues bits=$atomicWidth rounds=32 kernel-publication=1"
            case Compare    => s"verified failed compare-exchange bits=$atomicWidth rounds=32 atomic-locations=2"
            case _ if workItems > 1 => s"verified multi-workgroup atomics bits=$atomicWidth lanes=128 groups=4"
            case _ => s"verified live system atomics bits=$atomicWidth rounds=32 fences=${if (fenced) 1 else 0}"
          })
          else if (concurrent) for {
            layout <- List(0, 1, 2)
            pass   <- Passes
          } yield s"verified concurrency layout=$layout pass=$pass"
          else if (ordinary) for {
            layout <- List("contiguous", "strided", "gather", "linked")
            pass   <- Passes
          } yield s"verified $layout pass=$pass"
          else List("verified host-memory transport pass=0", "verified host-memory transport pass=1")
          assertEquals(
            result.stdout.linesIterator.filter(_.startsWith("verified ")).toList,
            expected,
            result.diagnostic
          )
          if (concurrent)
            assertEquals(checkpoint.count, if (atomicProbe) 1 else 6)
          if (atomicProbe && concurrent) {
            val operations = if (workItems > 1) workItems else 32
            assertEquals(checkpoint.contendedOperations.get(), operations)
            assertEquals(checkpoint.contendedAttempts.get(), operations * 3)
          }
      }
      Using.resource(Journal.open(archive)) { journal =>
        val hostAllocations = mutable.Set.empty[AllocationId]
        val requests        = journal.events.filter(_.kind == Journal.Kind.Request)
        assert(requests.nonEmpty, "recording contains no RPC requests")
        requests.foreach { event =>
          val frame        = journal.frame(event)
          val operationTag = Frame.operation(frame)
          val payload      = Frame.payload(frame)
          val request      = Wire.Protocol[Operation].decodePayload(operationTag, payload, ExecutionRPC.Limits)
            .fold(message => fail(message), identity).value
          request match {
            case Operation.RegisterAllocationAddress(allocation, _, true) =>
              val _ = hostAllocations.add(allocation)
            case Operation.SupplyHostMemory(_, ranges) =>
              ranges.foreach { range =>
                assert(
                  !atomicWords.exists((begin, end) =>
                    range.address.raw < end && begin < range.address.raw + range.bytes.size
                  ),
                  "an atomic control word was fetched by an ordinary host read"
                )
              }
            case Operation.WriteAllocation(allocation, _, _) =>
              assert(!hostAllocations.contains(allocation), "host-backed storage was uploaded speculatively")
            case Operation.ReleaseAllocation(allocation) => val _ = hostAllocations.remove(allocation)
            case _                                       => ()
          }
        }
      }
    }
  }
}
