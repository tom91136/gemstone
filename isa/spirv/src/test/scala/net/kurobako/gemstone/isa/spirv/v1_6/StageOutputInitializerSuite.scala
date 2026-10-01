package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class StageOutputInitializerSuite extends munit.FunSuite {
  for {
    model <- Seq(0, 4)
    shape <- Seq("scalar", "vector", "matrix", "array")
  } test(s"stage $model initializes every $shape output record on every launch") {
    def ref(n: Int): Operand           = operand("IdRef", n)
    def literal(n: Int): Operand       = operand("LiteralInteger", n)
    val (dataType, initializer, count) = shape match {
      case "scalar" => (2, 10, 1)
      case "vector" => (3, 11, 4)
      case "matrix" => (4, 12, 8)
      case _        => (7, 13, 16)
    }
    val module = Module(
      Header(ISA.version, Word(0), Word(24), Word(0)),
      Vec(
        Instruction.TypeVoid(ref(1)),
        Instruction.TypeFloat(ref(2), literal(32), None),
        Instruction.TypeVector(ref(3), ref(2), literal(4)),
        Instruction.TypeMatrix(ref(4), ref(3), literal(2)),
        Instruction.TypeInt(ref(5), literal(32), literal(0)),
        Instruction.Constant(ref(5), ref(6), literal(2)),
        Instruction.TypeArray(ref(7), ref(4), ref(6)),
        Instruction.Constant(ref(2), ref(10), literal(0x3f800000)),
        Instruction.ConstantComposite(ref(3), ref(11), Vec(ref(10), ref(10), ref(10), ref(10))),
        Instruction.ConstantComposite(ref(4), ref(12), Vec(ref(11), ref(11))),
        Instruction.ConstantComposite(ref(7), ref(13), Vec(ref(12), ref(12))),
        Instruction.TypePointer(ref(14), operand("StorageClass", StorageClass.Output), ref(dataType)),
        Instruction.Variable(ref(14), ref(15), operand("StorageClass", StorageClass.Output), Some(ref(initializer))),
        Instruction.TypeFunction(ref(16), ref(1), Vec.empty),
        Instruction.EntryPoint(operand("ExecutionModel", model), ref(20), stringOperand("main"), Vec(ref(15))),
        Instruction.Function(ref(1), ref(20), operand("FunctionControl", FunctionControl.None), ref(16)),
        Instruction.Label(ref(21)),
        Instruction.Return(),
        Instruction.FunctionEnd()
      ),
      Endianness.Little
    )
    val region = AllocationId(1L)
    val memory = ArrayMemory(region, 3 * count * 4 + 8)
    val task   = KernelTask.validated(
      "main",
      Dimensions.One,
      testDimensions(3),
      Vec(KernelTask.Argument.Memory(region, 4L))
    ).toOption.get
    val expected = Vec.from(Seq.fill(3 * count)(LittleEndian.toVec(0x3f800000L, 4)).flatten)
    for (_ <- 0 until 2) {
      assertEquals(
        memory.write(
          region,
          0L,
          Vec.from(Array.fill[Byte](3 * count * 4 + 8)(0x5a.toByte))
        ),
        Right(())
      )
      assertEquals(executeUnchecked(module, task, memory), Right(()))
      assertEquals(memory.bytes.slice(4, 4 + expected.size), expected)
      assertEquals(memory.bytes.take(4), Vec.from(Array.fill[Byte](4)(0x5a.toByte)))
      assertEquals(memory.bytes.takeRight(4), Vec.from(Array.fill[Byte](4)(0x5a.toByte)))
    }
  }
}
