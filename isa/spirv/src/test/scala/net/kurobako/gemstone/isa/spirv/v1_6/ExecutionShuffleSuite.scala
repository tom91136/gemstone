package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExecutionShuffleSuite extends munit.FunSuite {
  private val voidType    = operand("IdResult", 1)
  private val u32         = operand("IdResult", 2)
  private val inputType   = operand("IdResult", 3)
  private val outputType  = operand("IdResult", 4)
  private val signature   = operand("IdResult", 5)
  private val builtin     = operand("IdResult", 6)
  private val one         = operand("IdResult", 7)
  private val size        = operand("IdResult", 8)
  private val firstBase   = operand("IdResult", 9)
  private val secondBase  = operand("IdResult", 10)
  private val function    = operand("IdResult", 11)
  private val output      = operand("IdResult", 12)
  private val local       = operand("IdResult", 14)
  private val lane        = operand("IdResult", 15)
  private val current     = operand("IdResult", 16)
  private val other       = operand("IdResult", 17)
  private val last        = operand("IdResult", 18)
  private val reversed    = operand("IdResult", 19)
  private val result      = operand("IdResult", 20)
  private val destination = operand("IdResult", 21)

  private def module(operation: String, varying: Boolean, subgroupSize: Int): Module = {
    val delta   = if (varying) lane else one
    val shuffle = operation match {
      case "up"   => Instruction.SubgroupShuffleUpINTEL(u32, result, other, current, delta)
      case "down" => Instruction.SubgroupShuffleDownINTEL(u32, result, current, other, delta)
      case _      => Instruction.SubgroupShuffleINTEL(u32, result, current, reversed)
    }
    Module(
      Header(ISA.version, Word(0), Word(22), Word(0)),
      Vec(
        Instruction.TypeVoid(voidType),
        Instruction.TypeInt(u32, operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        Instruction.TypePointer(inputType, operand("StorageClass", StorageClass.Input), u32),
        Instruction.TypePointer(outputType, operand("StorageClass", StorageClass.CrossWorkgroup), u32),
        Instruction.TypeFunction(signature, voidType, Vec(outputType)),
        Instruction.Variable(inputType, builtin, operand("StorageClass", StorageClass.Input), None),
        Instruction.Decorate(builtin, operand("Decoration", Decoration.BuiltIn, BuiltIn.LocalInvocationIndex)),
        Instruction.Constant(u32, one, operand("LiteralContextDependentNumber", 1)),
        Instruction.Constant(u32, size, operand("LiteralContextDependentNumber", subgroupSize)),
        Instruction.Constant(u32, firstBase, operand("LiteralContextDependentNumber", 1000)),
        Instruction.Constant(u32, secondBase, operand("LiteralContextDependentNumber", 2000)),
        Instruction.EntryPoint(
          operand("ExecutionModel", ExecutionModel.Kernel),
          function,
          stringOperand("shuffle"),
          Vec(builtin)
        ),
        Instruction.Function(voidType, function, operand("FunctionControl", FunctionControl.None), signature),
        Instruction.FunctionParameter(outputType, output),
        Instruction.Label(operand("IdResult", 13)),
        Instruction.Load(u32, local, builtin, None),
        Instruction.UMod(u32, lane, local, size),
        Instruction.IAdd(u32, current, local, firstBase),
        Instruction.IAdd(u32, other, local, secondBase),
        Instruction.ISub(u32, last, size, one),
        Instruction.ISub(u32, reversed, last, lane),
        shuffle,
        Instruction.PtrAccessChain(outputType, destination, output, local, Vec.empty),
        Instruction.Store(destination, result, None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
  }

  // SPV_INTEL_subgroups explicitly permits Delta to differ between invocations.
  for {
    count                <- Seq(8, 64)
    (operation, varying) <- Seq("reverse" -> false, "up" -> false, "down" -> false, "up" -> true, "down" -> true)
  } test(s"$operation shuffle with varying delta=$varying routes every lane in $count invocations") {
    val subgroupSize = math.min(count, 32)
    val allocation   = AllocationId(1L)
    val memory       = ArrayMemory(allocation, count * 4)
    val task         = KernelTask.validated(
      "shuffle",
      testDimensions(count.unsafe[Int > 0]),
      Dimensions.One,
      Vec(KernelTask.Argument.Memory(allocation, 0L))
    ).toOption.get
    assertEquals(executeUnchecked(module(operation, varying, subgroupSize), task, memory), Right(()))
    val expected = Vec.tabulate(count) { index =>
      val group = index / subgroupSize * subgroupSize
      val lane  = index % subgroupSize
      val delta = if (varying) lane else 1
      operation match {
        case "up" if lane < delta                   => 2000 + group + subgroupSize + lane - delta
        case "up"                                   => 1000 + group + lane - delta
        case "down" if lane + delta >= subgroupSize => 2000 + group + lane + delta - subgroupSize
        case "down"                                 => 1000 + group + lane + delta
        case _                                      => 1000 + group + subgroupSize - 1 - lane
      }
    }.flatMap(value => LittleEndian.toVec(value.toLong, 4))
    assertEquals(memory.bytes, expected)
  }
}
