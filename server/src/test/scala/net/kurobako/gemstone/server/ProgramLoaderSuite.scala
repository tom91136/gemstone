package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*

final class ProgramLoaderSuite extends munit.FunSuite {
  private final class RecordingProgram(label: String) extends LoadedProgram {
    override def launch(
        dispatch: KernelDispatch,
        memory: ExecutionMemory,
        control: ExecutionControl
    ): Either[ExecutionFailure, Unit] =
      Either.cond(dispatch.kernelName == label, (), ExecutionFailure.Failed(s"wrong loader: $label"))
  }

  private final class RecordingLoader(val format: ProgramFormat, label: String) extends ProgramLoader {
    override def load(image: Col[Byte]): Either[ExecutionFailure, LoadedProgram] =
      Either.cond(image == Col(label.length.toByte), RecordingProgram(label), ExecutionFailure.InvalidRequest(label))
  }

  private object EmptyMemory extends ExecutionMemory {
    override def read(id: AllocationId, offset: Long, size: Int)         = Right(Col.empty)
    override def write(id: AllocationId, offset: Long, bytes: Col[Byte]) = Right(())
  }

  test("the composite routes each format through an isolated loader") {
    val ptx      = RecordingLoader(ProgramFormat.PTX, "ptx")
    val spirv    = RecordingLoader(ProgramFormat.SPIRV, "spirv")
    val executor = ISATypedProgramExecutor.create(Col(ptx, spirv)).toOption.get
    val ptxId    = ProgramId.fromLong(1L).toOption.get
    val spirvId  = ProgramId.fromLong(2L).toOption.get

    assertEquals(executor.load(ptxId, ProgramFormat.PTX, Col(3.toByte)), Right(()))
    assertEquals(executor.load(spirvId, ProgramFormat.SPIRV, Col(5.toByte)), Right(()))
    val one     = Dimensions.One
    val control = ExecutionControl.default
    assertEquals(
      executor.launch(KernelDispatch.create(ptxId, "ptx", one, one, Col.empty).toOption.get, EmptyMemory, control),
      Right(())
    )
    assertEquals(
      executor.launch(KernelDispatch.create(spirvId, "spirv", one, one, Col.empty).toOption.get, EmptyMemory, control),
      Right(())
    )
  }

  test("duplicate format ownership is rejected before programs can load") {
    val result = ISATypedProgramExecutor.create(
      Col(RecordingLoader(ProgramFormat.PTX, "first"), RecordingLoader(ProgramFormat.PTX, "second"))
    )
    assert(result.left.exists(_.contains("PTX")))
  }
}
