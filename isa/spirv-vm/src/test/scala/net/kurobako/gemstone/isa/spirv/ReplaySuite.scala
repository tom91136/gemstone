package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

class ReplaySuite extends Suite {
  private val image = ISA.encode(spv"""
    $openCL
    OpEntryPoint Kernel %main "squares" %gid
    OpDecorate %gid BuiltIn GlobalInvocationId
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %v3uint = OpTypeVector %uint 3
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %gid_ptr = OpTypePointer Input %v3uint
    %gid = OpVariable %gid_ptr Input
    %fn = OpTypeFunction %void %uint_ptr
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %ids = OpLoad %v3uint %gid
    %index = OpCompositeExtract %uint %ids 0
    %square = OpIMul %uint %index %index
    %slot = OpPtrAccessChain %uint_ptr %out %index
    OpStore %slot %square
    OpReturn
    OpFunctionEnd
  """).value

  test("a SPIR-V launch stopped at a checkpoint resumes there and finishes as an uninterrupted launch would") {
    def device(memory: ArrayMemory) =
      ReplayDevice(BackendRegistry(Vec(SPIRVBackend(Validation.Environment.OpenCLCompute))), memory)
    def task(memory: ArrayMemory) =
      KernelTask.validated("squares", Dimensions(4, 1, 1), Dimensions(2, 1, 1), Vec(buffer(memory.allocate(32L)))).value
    val whole = ArrayMemory()
    assert(device(whole).launch(ProgramFormat.SPIRVOpenCLCompute, image, task(whole), (_, _, _) => Right(())).isRight)

    val memory = ArrayMemory()
    val replay = device(memory)
    val points = mutable.ArrayBuffer.empty[(ReplayDevice.Checkpoint, Fault.Location)]
    val halted = replay.launch(
      ProgramFormat.SPIRVOpenCLCompute,
      image,
      task(memory),
      (point, at, _) => if (point.step == 30L) Left(Fault.Cancelled) else Right(points += point -> at).map(_ => ())
    )
    assertEquals(halted, Left(Fault.Cancelled))
    val (checkpoint, location) = points(17)
    val resumed                = mutable.ArrayBuffer.empty[Fault.Location]
    assert(replay.resume(checkpoint, (_, at, _) => Right(resumed += at).map(_ => ())).isRight)
    assertEquals(resumed.head, location)
    assertEquals(memory.snapshot(), whole.snapshot())
  }
}
