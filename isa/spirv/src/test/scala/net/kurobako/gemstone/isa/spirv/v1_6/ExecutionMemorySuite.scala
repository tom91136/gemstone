package net.kurobako.gemstone.isa.spirv.v1_6

import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
class ExecutionMemorySuite extends munit.FunSuite {
  import execution.*

  test("dirty-page flush preserves sparse runs and writes crossing bitmap and page boundaries") {
    val offsets = Vec[Long >= 0](
      0L,
      8L,
      62L,
      66L,
      4094L,
      8191L
    )
    val parameters   = offsets.indices.map(index => operand(OperandKind.IdResult, 10 + index))
    val voidType     = operand(OperandKind.IdResult, 1)
    val u32          = operand(OperandKind.IdResult, 2)
    val pointerType  = operand(OperandKind.IdResult, 3)
    val functionType = operand(OperandKind.IdResult, 4)
    val function     = operand(OperandKind.IdResult, 5)
    val value        = operand(OperandKind.IdResult, 6)
    val module       = Module(
      Header(ISA.version, Word(0), Word(32), Word(0)),
      Vec[Instruction](
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
        Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(functionType, voidType, Vec.fill(offsets.size)(pointerType)),
        Instruction.Constant(u32, value, operand(OperandKind.LiteralContextDependentNumber, 0x12345678)),
        Instruction.EntryPoint(
          operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
          function,
          stringOperand("sparse"),
          Vec.empty
        ),
        Instruction.Function(
          voidType,
          function,
          operand(OperandKind.FunctionControl, spv.FunctionControl.None),
          functionType
        )
      ) ++ Vec.from(parameters.map(Instruction.FunctionParameter(pointerType, _))) ++
        Vec[Instruction](Instruction.Label(operand(OperandKind.IdResult, 20))) ++
        Vec.from(parameters.map(Instruction.Store(_, value, None))) ++
        Vec[Instruction](
          Instruction.Load(u32, operand(OperandKind.IdResult, 21), parameters(2), None),
          Instruction.Store(parameters(0), operand(OperandKind.IdRef, 21), None),
          Instruction.Load(u32, operand(OperandKind.IdResult, 22), parameters(4), None),
          Instruction.Store(parameters(1), operand(OperandKind.IdRef, 22), None),
          Instruction.Return(),
          Instruction.FunctionEnd()
        ),
      Endianness.Little
    )
    val allocation = AllocationId(200L)
    val writes     = AtomicReference(Vec.empty[(Long, Int)])
    val memory     = ArrayMemory(
      allocation,
      8200,
      written = (offset, size) => {
        writes.updateAndGet(_ :+ (offset -> size))
        ()
      }
    )
    val dispatch = KernelTask.validated(
      kernelName = "sparse",
      groupSize = Dimensions.One,
      groupCount = Dimensions.One,
      arguments = offsets.map(KernelTask.Argument.Memory(allocation, _)),
      globalSize = None
    ).toOption.get
    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(writes.get(), Vec(0L -> 4, 8L -> 4, 62L -> 8, 4094L -> 2, 4096L -> 2, 8191L -> 1, 8192L -> 3))
    val expected = Array.ofDim[Byte](8200)
    offsets.foreach { offset =>
      val arrayOffset = offset.raw.toInt.unsafe[Int >= 0]
      LittleEndian.write(0x12345678L, expected, arrayOffset.raw, 4)
    }
    assertEquals(memory.bytes, Vec.from(expected))
  }
}
