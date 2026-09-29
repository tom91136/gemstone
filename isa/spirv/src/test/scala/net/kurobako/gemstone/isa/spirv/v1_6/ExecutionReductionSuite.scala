package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExecutionReductionSuite extends munit.FunSuite {
  private def id(value: Int): Operand = operand("IdResult", value)
  private val values                  = Seq(7, -3, 9, -8, 4, 1, -2, 6)
  private val cases                   = Seq(
    ("signed minimum", false, true, Seq(7, -3, -3, -8, -8, -8, -8, -8)),
    ("signed maximum", false, false, Seq(7, 7, 9, 9, 9, 9, 9, 9)),
    ("unsigned minimum", false, true, Seq(7, 7, 7, 7, 4, 1, 1, 1)),
    ("unsigned maximum", false, false, Seq(7, -3, -3, -3, -3, -3, -2, -2)),
    ("floating minimum", true, true, Seq(7, -3, -3, -8, -8, -8, -8, -8)),
    ("floating maximum", true, false, Seq(7, 7, 9, 9, 9, 9, 9, 9))
  )

  for {
    (name, floating, minimum, prefix) <- cases
    scan                              <- Seq(false, true)
  } test(s"$name ${if (scan) "inclusive scan" else "reduction"} writes the result for every lane") {
    val operation = operand("GroupOperation", if (scan) GroupOperation.InclusiveScan else GroupOperation.Reduce)
    val reduce    =
      if (floating && minimum) Instruction.GroupFMin(id(3), id(18), id(9), operation, id(17))
      else if (floating) Instruction.GroupFMax(id(3), id(18), id(9), operation, id(17))
      else if (name.startsWith("unsigned") && minimum) Instruction.GroupUMin(id(3), id(18), id(9), operation, id(17))
      else if (name.startsWith("unsigned")) Instruction.GroupUMax(id(3), id(18), id(9), operation, id(17))
      else if (minimum) Instruction.GroupSMin(id(3), id(18), id(9), operation, id(17))
      else Instruction.GroupSMax(id(3), id(18), id(9), operation, id(17))
    val valueType = if (floating) Instruction.TypeFloat(id(3), operand("LiteralInteger", 32), None)
    else Instruction.TypeInt(
      id(3),
      operand("LiteralInteger", 32),
      operand("LiteralInteger", if (name.startsWith("unsigned")) 0 else 1)
    )
    val module = Module(
      Header(ISA.version, Word(0), Word(21), Word(0)),
      Vec(
        Instruction.TypeVoid(id(1)),
        Instruction.TypeInt(id(2), operand("LiteralInteger", 32), operand("LiteralInteger", 0)),
        valueType,
        Instruction.TypePointer(id(4), operand("StorageClass", StorageClass.Input), id(2)),
        Instruction.TypePointer(id(5), operand("StorageClass", StorageClass.CrossWorkgroup), id(3)),
        Instruction.TypeFunction(id(6), id(1), Vec(id(5))),
        Instruction.Variable(id(4), id(7), operand("StorageClass", StorageClass.Input), None),
        Instruction.Decorate(id(7), operand("Decoration", Decoration.BuiltIn, BuiltIn.LocalInvocationIndex)),
        Instruction.Constant(id(2), id(8), operand("LiteralContextDependentNumber", 8)),
        Instruction.Constant(id(2), id(9), operand("LiteralContextDependentNumber", Scope.Workgroup)),
        Instruction.EntryPoint(
          operand("ExecutionModel", ExecutionModel.Kernel),
          id(10),
          stringOperand("reduce"),
          Vec(id(7))
        ),
        Instruction.Function(id(1), id(10), operand("FunctionControl", FunctionControl.None), id(6)),
        Instruction.FunctionParameter(id(5), id(11)),
        Instruction.Label(id(12)),
        Instruction.Load(id(2), id(13), id(7), None),
        Instruction.PtrAccessChain(id(5), id(16), id(11), id(13), Vec.empty),
        Instruction.Load(id(3), id(17), id(16), None),
        reduce,
        Instruction.IAdd(id(2), id(19), id(13), id(8)),
        Instruction.PtrAccessChain(id(5), id(20), id(11), id(19), Vec.empty),
        Instruction.Store(id(20), id(18), None),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    def bytes(input: Seq[Int]): Vec[Byte] = Vec.from(input).flatMap { value =>
      LittleEndian.toVec((if (floating) java.lang.Float.floatToRawIntBits(value.toFloat) else value).toLong, 4)
    }
    val allocation = AllocationId(1L)
    val memory     = ArrayMemory(allocation, 64)
    assertEquals(memory.write(allocation, 0L, bytes(values)), Right(()))
    val task = KernelTask.validated(
      "reduce",
      Dimensions(8, 1, 1),
      Dimensions.One,
      Vec(KernelTask.Argument.Memory(allocation, 0L))
    ).toOption.get
    assertEquals(executeUnchecked(module, task, memory), Right(()))
    assertEquals(memory.bytes, bytes(values) ++ bytes(if (scan) prefix else Seq.fill(8)(prefix.last)))
  }
}
