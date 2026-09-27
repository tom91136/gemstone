package net.kurobako.gemstone.server

import net.kurobako.gemstone.Constraint.NoNulString

import net.kurobako.gemstone.Constraint.Positive

import net.kurobako.gemstone.*

import net.kurobako.gemstone.launcher.Environment
import net.kurobako.gemstone.vm.TaskLimits
import net.kurobako.gemstone.rpc.{ExecutionRPC, Wire}
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation
import net.kurobako.gemstone.server.recording.RPCJournal
import scala.collection.mutable

import java.nio.file.Files
import scala.concurrent.duration.*
import scala.util.Using

/** RPC traffic, forced interleavings, host-memory extensions and scheduler regressions across all APIs. */
final class UnifiedMemoryProgressSuite extends munit.FunSuite {
  override val munitTimeout = 150.seconds

  private val atomicsOnly = sys.env.get("GEMSTONE_TEST_HOST_ATOMICS").contains("1")

  private def allocationKinds(api: String): List[String] = api match {
    case "cuda"      => List("managed", "pinned", "registered")
    case "hip"       => List("managed", "host", "registered")
    case "hsa"       => List("region", "pool", "locked")
    case "levelzero" => List("shared", "host")
    case "opencl"    => List("svm", "usm_host", "usm_shared")
  }

  for {
    api         <- List("cuda", "hip", "hsa", "levelzero", "opencl")
    atomicWidth <- if (atomicsOnly) List(32, 64) else List(0, 32, 64)
    fenced      <- if (atomicWidth != 0) List(false, true) else List(false)
    workItems   <- if (atomicWidth != 0 && !fenced) List(1, 128) else List(1)
    scenario    <- if (atomicWidth != 0) List((false, true, false)) ++
      (if (!fenced && workItems == 1) List((false, false, true)) else Nil)
    else (if (api == "opencl") List(false) else List(false, true)).map((_, false, false)) ++
      List((false, true, false))
    hostProbe <- if (scenario._3) List("submission", "overlap", "compare") else List("none")
    if hostProbe != "compare" || atomicWidth == 32
    allocationKind <- if (hostProbe == "compare") allocationKinds(api).take(1)
    else if (scenario._2 || scenario._3) allocationKinds(api) else List("default")
  } test(
    s"$api memory patterns, ordinary=${scenario._1}, concurrency=${scenario._2}, live atomic bits=$atomicWidth, fences=$fenced, work-items=$workItems, allocation=$allocationKind, submission=${hostProbe ==
        "submission"}, overlap=${hostProbe == "overlap"}, compare=${hostProbe == "compare"}"
  ) {
    val atomicProbe               = atomicWidth != 0
    val (ordinary, concurrent, _) = scenario
    val submission                = hostProbe == "submission"
    val directory                 = NativeTestProcess.nativeDirectory
    val client                    = directory.resolve(s"gemstone_memory_$api")
    assert(Files.isExecutable(client), s"missing native memory client: $client")
    val vendors    = Files.createTempDirectory("gemstone-memory-icd-")
    val icd        = vendors.resolve("gemstone.icd")
    val _          = Files.writeString(icd, directory.resolve("libOpenCL_gemstone.so.1").toString + "\n")
    val checkpoint = MemoryConcurrencyCheckpoint(
      forceContention = atomicProbe,
      expectedOperations = if (workItems > 1) workItems else 32
    )
    val atomicWords = mutable.ArrayBuffer.empty[(Long, Long)]
    val archive     = checkpoint.directory.resolve("rpc.msgpack")
    val environment = Map("GEMSTONE_EXECUTION_PARALLELISM" -> "2", "GEMSTONE_RECORD" -> archive.toString)
    try {
      Using.resource(if (concurrent)
        GemstoneRPCServer.startWith(
          checkpoint.backends,
          TaskLimits(Positive.unsafe((if (atomicProbe) 100000 else 10000000).toLong), 64),
          30000,
          environment
        )
      else GemstoneRPCServer.start(TaskLimits(10000000L, 64), environment)) {
        server =>
          val libraryPath = List(directory.toString, sys.env.getOrElse("LD_LIBRARY_PATH", "")).mkString(":")
          val probes      =
            (if (ordinary) Vector.empty else Vector("rpc_only")) ++
              (if (hostProbe == "overlap") Vector("overlap") else Vector.empty) ++
              (if (hostProbe == "compare") Vector("compare") else Vector.empty) ++
              (if (submission) Vector("submission") else Vector.empty) ++
              (if (workItems > 1) Vector("multilane") else Vector.empty) ++
              (if (fenced) Vector("fence") else Vector.empty) ++
              (if (atomicProbe) Vector("atomic") else Vector.empty)
          val base = Map(
            "GEMSTONE_MEMORY_PROBE"           -> probes.mkString(","),
            "GEMSTONE_MEMORY_ALLOCATION_KIND" -> allocationKind,
            "GEMSTONE_RPC_PORT"               -> server.port.raw.toString,
            "GEMSTONE_RPC_CLIENT"             -> server.client.raw.toString,
            "LD_LIBRARY_PATH"                 -> libraryPath,
            "ZE_ENABLE_ALT_DRIVERS"           -> directory.resolve("libze_gemstone.so.1").toString,
            "ZE_ENABLE_LOADER_DEBUG_TRACE"    -> "0",
            "OCL_ICD_VENDORS"                 -> vendors.toString,
            "OCL_ICD_FILENAMES"               -> directory.resolve("libOpenCL_gemstone.so.1").toString
          ) ++
            (if (atomicProbe) Map("GEMSTONE_MEMORY_ATOMIC_BITS" -> atomicWidth.toString)
             else Map.empty[String, String]) ++
            (if (concurrent) Map("GEMSTONE_MEMORY_CONCURRENCY_DIRECTORY" -> checkpoint.directory.toString)
             else Map.empty[String, String])
          val paging    = if (ordinary) Map("GEMSTONE_SYSTEM_PAGING" -> "1") else Map.empty[String, String]
          val arguments = Vector(client.toString) ++ (if (ordinary) Vector("ordinary") else Vector.empty)
          val result    = NativeTestProcess.run(
            NonEmptyVec.unsafeFromVec(Vec.from(arguments).map(NoNulString.unsafe)),
            directory,
            Environment(
              base ++ paging,
              if (ordinary) Set.empty
              else Set("GEMSTONE_SYSTEM_PAGING", "UR_ADAPTERS_FORCE_LOAD")
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
          assertEquals(
            result.stdout.linesIterator.count(_.startsWith("verified ")),
            if (atomicProbe) 1
            else if (concurrent) 6
            else if (ordinary) 8 else 2,
            result.diagnostic
          )
          if (concurrent)
            assertEquals(checkpoint.count, if (atomicProbe) 1 else 6)
          if (atomicProbe && concurrent) {
            val operations = if (workItems > 1) workItems else 32
            assertEquals(checkpoint.contendedOperations.get(), operations)
            assertEquals(checkpoint.contendedAttempts.get(), operations * 3)
          }
          assert(server.usage > 0, "the native client did not exercise the real RPC transport")
          println(s"$api ordinary=$ordinary concurrency=$concurrent\n${result.stdout}")
      }
      Using.resource(RPCJournal.open(archive)) { journal =>
        val hostAllocations = mutable.Set.empty[Long]
        journal.events.filter(_.kind == RPCJournal.Kind.Request).foreach { event =>
          val frame       = journal.frame(event)
          val operationId = RPCJournal.Frames.long(frame, ExecutionRPC.RequestOperationOffset.raw)
          val payload     = java.util.Arrays.copyOfRange(frame, ExecutionRPC.RequestHeaderBytes.raw, frame.length)
          val request     = Wire.Protocol[Operation].decodePayload(operationId, payload, ExecutionRPC.Limits)
            .fold(message => fail(message), identity).value
          request match {
            case Operation.RegisterAllocationAddress(allocation, _, true) =>
              val _ = hostAllocations.add(allocation.raw)
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
              assert(!hostAllocations.contains(allocation.raw), "host-backed storage was uploaded speculatively")
            case Operation.ReleaseAllocation(allocation) => val _ = hostAllocations.remove(allocation.raw)
            case _                                       => ()
          }
        }
      }
    } finally {
      checkpoint.close()
      val _ = Files.deleteIfExists(icd)
      val _ = Files.deleteIfExists(vendors)
    }
  }
}
