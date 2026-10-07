package net.kurobako.gemstone.server.backend

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.backend.spirvAssembly.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.annotation.tailrec
import scala.util.Using

class SPIRVProgramExecutorSuite extends munit.FunSuite {
  private val encoding = ProgramEncoding.SPIRVOpenCLCompute

  test("real SPIR-V printf reaches the handler and preserves handler failures") {
    val bytes = image(s"""
      $openCL
      OpEntryPoint Kernel %main "print_value"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uchar = OpTypeInt 8 0
      %three = OpConstant %uint 3
      %seven = OpConstant %uint 7
      %percent = OpConstant %uchar 37
      %u = OpConstant %uchar 117
      %nul = OpConstant %uchar 0
      %format_type = OpTypeArray %uchar %three
      %format_value = OpConstantComposite %format_type %percent %u %nul
      %format_ptr = OpTypePointer CrossWorkgroup %format_type
      %format = OpVariable %format_ptr CrossWorkgroup %format_value
      %fn = OpTypeFunction %void
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %printed = OpExtInst %uint %opencl printf %format %seven
      OpReturn
      OpFunctionEnd
    """)
    Using.resource(SPIRVBackend(Validation.Environment.OpenCLCompute).load(bytes).value) { program =>
      val task   = KernelTask.validated("print_value", Dimensions.One, Dimensions.One, Vec.empty).value
      val events = scala.collection.mutable.ArrayBuffer.empty[Event]
      assertEquals(
        program.execute(task, RejectingMemory(), TaskControl(), event => { events += event; Right(()) }),
        Right(())
      )
      assertEquals(events.toVector, Vector(Event.Print("%u", Vec(Event.PrintArgument.Integer(Vec(7L), 32)))))
      val rejected = AtomicInteger()
      assertEquals(
        program.execute(
          task,
          RejectingMemory(),
          TaskControl(),
          _ => {
            val _ = rejected.incrementAndGet()
            Left(Fault.EventHandling("sink closed"))
          }
        ),
        Left(Fault.EventHandling("sink closed"))
      )
      assertEquals(rejected.get(), 1)
    }
  }

  for {
    (prologue, model, missing) <- Seq(
      (openCL, "Vertex", "OpEntryPoint Kernel"),
      ("", "Kernel", "OpCapability Kernel")
    )
  } test(s"the external SPIR-V load boundary requires $missing") {
    val bytes = image(s"""
      $prologue
      OpEntryPoint $model %main "main"
      %void = OpTypeVoid
      %fn = OpTypeFunction %void
      %main = OpFunction %void None %fn
      %entry = OpLabel
      OpReturn
      OpFunctionEnd
    """)
    Using.resource(ExecutionSession(Backends.default().programs)) { service =>
      service.load(encoding, bytes) match {
        case Left(ExecutionFailure.InvalidRequest(detail)) => assert(detail.contains(missing))
        case result                                        => fail(s"expected a validation failure, found $result")
      }
    }
  }

  test("server sessions describe and substitute SPIR-V specialization constants without loading a program") {
    def specialized(specId: Long, value: Int): Vec[Byte] = image(s"""
      OpCapability Shader
      OpMemoryModel Logical GLSL450
      OpDecorate %constant SpecId $specId
      %uint = OpTypeInt 32 0
      %constant = OpSpecConstant %uint $value
    """)
    Using.resource(Server.start(Server.Settings(parallelism = 1))) { server =>
      Using.resource(connections.Client(server.endpoint, server.client)) { client =>
        for (id <- Seq(Binary32(0L), Binary32(7L), Binary32(0x80000000L), Binary32(0xffffffffL))) {
          val specId = id.toLong
          val bytes  = specialized(specId, 12)
          assertEquals(client.call(Operation.DescribeSpecialization(bytes)), Right(Vec(SpecializationInfo(id, 4))))
          assertEquals(
            client.call(Operation.SpecializeImage(bytes, Vec(SpecializationConstant(id, Vec[Byte](99, 0, 0, 0))))),
            Right(specialized(specId, 99))
          )
          assert(client.call(Operation.SpecializeImage(bytes, Vec(SpecializationConstant(id, Vec[Byte](1))))).isLeft)
        }
      }
    }
    Using.resource(Server.start(Server.Settings(parallelism = 1), _ => Backends(List.empty))) { server =>
      Using.resource(connections.Client(server.endpoint, server.client)) { client =>
        assertEquals(
          client.call(Operation.DescribeSpecialization(specialized(7L, 12))),
          Left(ExecutionFailure.InvalidRequest("image specialization is unavailable"))
        )
      }
    }
  }

  test("typed SPIR-V executes through the binary program service boundary") {
    val bytes = image(s"""
      $openCL
      OpEntryPoint Kernel %main "store"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr %uint
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %value = OpFunctionParameter %uint
      %entry = OpLabel
      OpStore %out %value
      OpReturn
      OpFunctionEnd
    """)
    Using.resource(ExecutionSession(Backends.default().programs)) { service =>
      val allocation = service.registerAllocation(4).value
      val program    = service.load(encoding, bytes).value
      val task       = KernelTask.validated(
        "store",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(Vec.nonEmpty[Byte](0x78, 0x56, 0x34, 0x12))
        )
      ).value
      val completion = service.launch(program, task).value
      assertEquals(awaitCompletion(service, completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(allocation, 0L, 4), Right(Vec[Byte](0x78, 0x56, 0x34, 0x12)))
    }
  }

  test("multi-workgroup SPIR-V completion publishes every dirty region on repeated queued launches") {
    val bytes = image(s"""
      $openCL
      OpEntryPoint Kernel %main "groups" %global_id
      OpDecorate %global_id BuiltIn GlobalInvocationId
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %ulong3 = OpTypeVector %ulong 3
      %ulong3_ptr = OpTypePointer Input %ulong3
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr %uint
      %page_words = OpConstant %ulong 16384
      %global_id = OpVariable %ulong3_ptr Input
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %seed = OpFunctionParameter %uint
      %entry = OpLabel
      %id = OpLoad %ulong3 %global_id
      %x = OpCompositeExtract %ulong %id 0
      %index = OpIMul %ulong %x %page_words
      %target = OpPtrAccessChain %uint_ptr %out %index
      OpStore %target %seed
      OpReturn
      OpFunctionEnd
    """)
    for {
      groups   <- Vec(1, 2, 3, 5, 9)
      parallel <- Vec(false, true)
    } {
      val queued = AtomicReference(Option.empty[Runnable])
      Using.resource(PooledWorkgroups()) { pool =>
        val workgroups = if (parallel) pool else Workgroups.Sequential
        class CapturingSubmissions extends scala.concurrent.ExecutionContext {
          override def execute(runnable: Runnable): Unit     = assert(queued.compareAndSet(None, Some(runnable)))
          override def reportFailure(error: Throwable): Unit = throw error
        }
        Using.resource(
          ExecutionSession(Backends.default(workgroups).programs, executionContext = CapturingSubmissions())
        ) { service =>
          val size       = ((groups + 3) * 65536 + 8).refine[Int > 0].value
          val allocation = service.registerAllocation(size).value
          val program    = service.load(encoding, bytes).value
          val host       = Array.ofDim[Byte](size.raw)
          for (seed <- Vec(3L, 7L)) {
            val before = service.read(allocation, 0L, size).value
            val task   = KernelTask.validated(
              "groups",
              Dimensions.One,
              testDimensions(groups.refine[Int > 0].value),
              Vec(
                KernelTask.Argument.Memory(allocation, 4L),
                KernelTask.Argument.Scalar(LittleEndian.toVec(seed, 4).refine[NonEmpty[Vec[Byte]]].value)
              ),
              globalOffset = LaunchGeometry.Coordinates(2, 0, 0)
            ).value
            val completion = service.launch(program, task).value
            assertEquals(service.completion(completion), Right(CompletionStatus.Pending))
            assert(service.completionDirtyRanges(completion, 0, ExecutionRPC.MaximumElements).isLeft)
            assertEquals(service.read(allocation, 0L, size), Right(before))
            queued.getAndSet(None).get.run()
            assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
            val ranges = service.completionDirtyRanges(completion, 0, ExecutionRPC.MaximumElements).value
            assertEquals(service.completionDirtyRanges(completion, 0, ExecutionRPC.MaximumElements), Right(ranges))
            ranges.foreach { range =>
              assertEquals(range.allocation, allocation)
              assert(range.offset.raw >= 0 && range.offset.raw + range.size.raw <= size.raw)
              service.read(allocation, range.offset, range.size).value
                .copyToArray(host, range.offset.raw.toInt, range.size.raw)
            }
            val expected = Array.ofDim[Byte](size.raw)
            (0 until groups).foreach { group =>
              val address = 4L + (2L + group) * 65536L
              assert(ranges.exists(range =>
                range.offset.raw <= address && address + 4 <= range.offset.raw + range.size.raw
              ))
              LittleEndian.write(seed, expected, address.toInt, 4)
            }
            assertEquals(Vec.from(host), Vec.from(expected))
            assertEquals(service.releaseCompletion(completion), Right(()))
            assert(service.completion(completion).isLeft)
          }
        }
      }
    }
  }

  private def awaitCompletion(
      service: ExecutionSession,
      completion: CompletionId,
      deadline: Long = platform.monotonicNanos + 5_000_000_000L
  ): Either[ExecutionFailure, CompletionStatus] = {
    @tailrec def loop(): Either[ExecutionFailure, CompletionStatus] = service.completion(completion) match {
      case Right(CompletionStatus.Pending) if platform.monotonicNanos < deadline =>
        Thread.sleep(1L)
        loop()
      case result => result
    }
    loop()
  }
}
