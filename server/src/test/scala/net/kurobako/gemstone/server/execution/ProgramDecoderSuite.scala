package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.execution.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicInteger

final class ProgramDecoderSuite extends munit.FunSuite {
  private final class RecordingProgram(label: KernelName) extends ProgramInstance {
    override def launch(
        dispatch: KernelDispatch,
        memory: ExecutionMemory,
        control: ExecutionControl
    ): Either[ExecutionFailure, Unit] =
      Either.cond(
        dispatch.kernelName == label,
        (),
        ExecutionFailure.Failed(s"wrong loader: $label")
      )
  }

  private final class RecordingDecoder(val format: ProgramFormat, label: KernelName)
      extends ProgramDecoder {
    override def decode(image: Vec[Byte]): Either[ExecutionFailure, ProgramInstance] =
      Either.cond(
        image == Vec(label.value.length.toByte),
        RecordingProgram(label),
        ExecutionFailure.InvalidRequest(label.value)
      )
  }

  private final class CountingDecoder(loads: AtomicInteger) extends ProgramDecoder {
    override val format: ProgramFormat                                               = ProgramFormat.PTXText
    override def decode(image: Vec[Byte]): Either[ExecutionFailure, ProgramInstance] = {
      loads.incrementAndGet()
      Right(RecordingProgram(KernelName("ptx")))
    }
  }

  private object EmptyMemory extends ExecutionMemory {
    override def read(id: AllocationId, offset: Long :| Positive0, size: Int :| Positive0) = Right(Vec.empty)
    override def write(id: AllocationId, offset: Long :| Positive0, bytes: Vec[Byte])      = Right(())
    override def recordPointerRelocation(relocation: PointerRelocation)                    = ()
  }

  test("the registry routes each format through an isolated decoder") {
    val ptx      = RecordingDecoder(ProgramFormat.PTXText, KernelName("ptx"))
    val spirv    = RecordingDecoder(ProgramFormat.SPIRVBinary, KernelName("spirv"))
    val executor = ProgramRegistry.validated(Vec(ptx, spirv)).toOption.get
    val ptxId    = ProgramId(1L)
    val spirvId  = ProgramId(2L)

    assertEquals(executor.load(ptxId, ProgramFormat.PTXText, Vec(3.toByte)), Right(()))
    assertEquals(executor.load(spirvId, ProgramFormat.SPIRVBinary, Vec(5.toByte)), Right(()))
    val one     = Dimensions.One
    val control = ExecutionControl.default
    assertEquals(
      executor.launch(
        KernelDispatch.validated(ptxId, KernelName("ptx"), one, one, Vec.empty).toOption.get,
        EmptyMemory,
        control
      ),
      Right(())
    )
    assertEquals(
      executor.launch(
        KernelDispatch.validated(spirvId, KernelName("spirv"), one, one, Vec.empty).toOption.get,
        EmptyMemory,
        control
      ),
      Right(())
    )
  }

  test("duplicate format ownership is rejected before programs can load") {
    val result = ProgramRegistry.validated(
      Vec(
        RecordingDecoder(ProgramFormat.PTXText, KernelName("first")),
        RecordingDecoder(ProgramFormat.PTXText, KernelName("second"))
      )
    )
    assert(result.left.exists(_.contains("PTX")))
  }

  test("a duplicate program ID is rejected before preparing another program") {
    val loads    = AtomicInteger()
    val executor = ProgramRegistry.validated(Vec(CountingDecoder(loads))).toOption.get
    val id       = ProgramId(1L)

    assertEquals(executor.load(id, ProgramFormat.PTXText, Vec.empty), Right(()))
    assertEquals(
      executor.load(id, ProgramFormat.PTXText, Vec.empty),
      Left(ExecutionFailure.InvalidRequest("program 1 is already loaded"))
    )
    assertEquals(loads.get(), 1)
  }
}
