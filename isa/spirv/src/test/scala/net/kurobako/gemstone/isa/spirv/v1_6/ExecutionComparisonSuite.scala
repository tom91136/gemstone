package net.kurobako.gemstone.isa.spirv.v1_6

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.v1_6 as spv
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExecutionComparisonSuite extends munit.FunSuite {
  private def id(value: Int): Operand      = operand(OperandKind.IdResult, value)
  private def literal(value: Int): Operand = operand(OperandKind.LiteralContextDependentNumber, value)
  private val comparisons: Seq[(String, (Operand, Operand, Operand, Operand) => Instruction, Seq[Int])] = Seq(
    ("ordered equal", Instruction.FOrdEqual(_, _, _, _), Seq(0, 0, 1, 0, 0)),
    ("unordered equal", Instruction.FUnordEqual(_, _, _, _), Seq(1, 1, 1, 0, 0)),
    ("ordered not equal", Instruction.FOrdNotEqual(_, _, _, _), Seq(0, 0, 0, 1, 1)),
    ("unordered not equal", Instruction.FUnordNotEqual(_, _, _, _), Seq(1, 1, 0, 1, 1)),
    ("ordered less", Instruction.FOrdLessThan(_, _, _, _), Seq(0, 0, 0, 1, 0)),
    ("unordered less", Instruction.FUnordLessThan(_, _, _, _), Seq(1, 1, 0, 1, 0)),
    ("ordered less or equal", Instruction.FOrdLessThanEqual(_, _, _, _), Seq(0, 0, 1, 1, 0)),
    ("unordered less or equal", Instruction.FUnordLessThanEqual(_, _, _, _), Seq(1, 1, 1, 1, 0)),
    ("ordered greater", Instruction.FOrdGreaterThan(_, _, _, _), Seq(0, 0, 0, 0, 1)),
    ("unordered greater", Instruction.FUnordGreaterThan(_, _, _, _), Seq(1, 1, 0, 0, 1)),
    ("ordered greater or equal", Instruction.FOrdGreaterThanEqual(_, _, _, _), Seq(0, 0, 1, 0, 1)),
    ("unordered greater or equal", Instruction.FUnordGreaterThanEqual(_, _, _, _), Seq(1, 1, 1, 0, 1))
  )

  for ((name, compare, expected) <- comparisons)
    test(s"$name handles NaN in either operand, signed zero, and finite ordering") {
      val pairs = Seq(
        (0x7fc00001, 0x3f800000),
        (0x3f800000, 0x7fc00001),
        (0, 0x80000000),
        (0xbf800000, 0x40000000),
        (0x40000000, 0xbf800000)
      )
      val declarations = Vec.from(pairs.zipWithIndex.flatMap { case ((left, right), index) =>
        Seq(
          Instruction.Constant(id(4), id(20 + index * 6), literal(left)),
          Instruction.Constant(id(4), id(21 + index * 6), literal(right)),
          Instruction.Constant(id(2), id(22 + index * 6), literal(index))
        )
      })
      val operations = Vec.from(pairs.indices.flatMap { index =>
        val base = 20 + index * 6
        Seq(
          compare(id(3), id(base + 3), id(base), id(base + 1)),
          Instruction.Select(id(2), id(base + 4), id(base + 3), id(8), id(7)),
          Instruction.PtrAccessChain(id(5), id(base + 5), id(11), id(base + 2), Vec.empty),
          Instruction.Store(id(base + 5), id(base + 4), None)
        )
      })
      val module = Module(
        Header(ISA.version, Word(0), Word(50), Word(0)),
        Vec(
          Instruction.TypeVoid(id(1)),
          Instruction.TypeInt(id(2), operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
          Instruction.TypeBool(id(3)),
          Instruction.TypeFloat(id(4), operand(OperandKind.LiteralInteger, 32), None),
          Instruction.TypePointer(id(5), operand(OperandKind.StorageClass, StorageClass.CrossWorkgroup), id(2)),
          Instruction.TypeFunction(id(6), id(1), Vec(id(5))),
          Instruction.Constant(id(2), id(7), literal(0)),
          Instruction.Constant(id(2), id(8), literal(1))
        ) ++
          declarations ++ Vec(
            Instruction.EntryPoint(
              operand(OperandKind.ExecutionModel, ExecutionModel.Kernel),
              id(10),
              stringOperand("compare"),
              Vec.empty
            ),
            Instruction.Function(id(1), id(10), operand(OperandKind.FunctionControl, FunctionControl.None), id(6)),
            Instruction.FunctionParameter(id(5), id(11)),
            Instruction.Label(id(12))
          ) ++ operations ++
          Vec(Instruction.Return(), Instruction.FunctionEnd()),
        Endianness.Little
      )
      val allocation = AllocationId(1L)
      val memory     = ArrayMemory(allocation, 20)
      val task       = KernelTask.validated(
        "compare",
        Dimensions.One,
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(allocation, 0L))
      ).toOption.get
      assertEquals(executeUnchecked(module, task, memory), Right(()))
      assertEquals(memory.bytes, Vec.from(expected).flatMap(value => LittleEndian.toVec(value.toLong, 4)))
    }

  test("dispatch compares integer values with a typed boolean result") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val u32          = operand(OperandKind.IdResult, 2)
    val bool         = operand(OperandKind.IdResult, 3)
    val pointerType  = operand(OperandKind.IdResult, 4)
    val function     = operand(OperandKind.IdResult, 6)
    val output       = operand(OperandKind.IdResult, 7)
    val left         = operand(OperandKind.IdResult, 8)
    val right        = operand(OperandKind.IdResult, 9)
    val remainder    = operand(OperandKind.IdResult, 11)
    val equal        = operand(OperandKind.IdResult, 12)
    val selected     = operand(OperandKind.IdResult, 13)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeInt(u32, operand(OperandKind.LiteralInteger, 32), operand(OperandKind.LiteralInteger, 0)),
      Instruction.TypeBool(bool),
      Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), u32),
      Instruction.TypeFunction(operand(OperandKind.IdResult, 5), voidType, Vec(pointerType, u32, u32)),
      Instruction.EntryPoint(
        operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
        function,
        stringOperand("integer_equal"),
        Vec.empty
      ),
      Instruction.Function(
        voidType,
        function,
        operand(OperandKind.FunctionControl, spv.FunctionControl.None),
        operand(OperandKind.IdRef, 5)
      ),
      Instruction.FunctionParameter(pointerType, output),
      Instruction.FunctionParameter(u32, left),
      Instruction.FunctionParameter(u32, right),
      Instruction.Label(operand(OperandKind.IdResult, 10)),
      Instruction.SRem(u32, remainder, left, right),
      Instruction.IEqual(bool, equal, remainder, right),
      Instruction.Select(u32, selected, equal, left, remainder),
      Instruction.Store(output, selected, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module =
      Module(Header(ISA.version, Word(0), Word(16), Word(0)), instructions, Endianness.Little)
    val allocation = AllocationId(6L)
    val memory     = ArrayMemory(allocation, 4)
    val dispatch   = KernelTask
      .validated(
        "integer_equal",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0xfffffff9L, 4)),
          KernelTask.Argument.Scalar(LittleEndian.toVec(3L, 4))
        )
      )
      .toOption
      .get

    assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xffffffffL, 4))

    val divided = module.copy(instructions = instructions.updated(11, Instruction.SDiv(u32, remainder, left, right)))
    assertEquals(executeUnchecked(divided, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xfffffffeL, 4))

    val xor = module.copy(instructions = instructions.updated(11, Instruction.BitwiseXor(u32, remainder, left, right)))
    assertEquals(executeUnchecked(xor, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xfffffffaL, 4))

    val shiftedRight = module.copy(instructions =
      instructions.updated(11, Instruction.ShiftRightLogical(u32, remainder, left, right))
    )
    assertEquals(executeUnchecked(shiftedRight, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0x1fffffffL, 4))

    val shiftedLeft = module.copy(instructions =
      instructions.updated(11, Instruction.ShiftLeftLogical(u32, remainder, left, right))
    )
    assertEquals(executeUnchecked(shiftedLeft, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xffffffc8L, 4))

    val not = module.copy(instructions = instructions.updated(11, Instruction.Not(u32, remainder, left)))
    assertEquals(executeUnchecked(not, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(6L, 4))

    val negated = module.copy(instructions = instructions.updated(11, Instruction.SNegate(u32, remainder, left)))
    assertEquals(executeUnchecked(negated, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(7L, 4))

    val signedLess =
      module.copy(instructions = instructions.updated(12, Instruction.SLessThan(bool, equal, left, right)))
    assertEquals(executeUnchecked(signedLess, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xfffffff9L, 4))

    val signedGreater =
      module.copy(instructions = instructions.updated(12, Instruction.SGreaterThan(bool, equal, left, right)))
    assertEquals(executeUnchecked(signedGreater, dispatch, memory), Right(()))
    assertEquals(memory.bytes, LittleEndian.toVec(0xffffffffL, 4))

    val malformed = module.copy(instructions =
      instructions.updated(
        12,
        Instruction.IEqual(u32, equal, remainder, right)
      )
    )
    executeUnchecked(malformed, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpIEqual result type %2 is not a boolean scalar or vector")
      case value => fail(s"expected typed integer equality failure, found $value")
    }

    val malformedRemainder = module.copy(instructions =
      instructions.updated(
        11,
        Instruction.SRem(bool, remainder, left, right)
      )
    )
    executeUnchecked(malformedRemainder, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpSRem result type %3 is not an integer scalar or vector")
      case value => fail(s"expected typed signed remainder failure, found $value")
    }

    val malformedDivision = module.copy(instructions =
      instructions.updated(
        11,
        Instruction.SDiv(bool, remainder, left, right)
      )
    )
    executeUnchecked(malformedDivision, dispatch, memory) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpSDiv result type %3 is not an integer scalar or vector")
      case value => fail(s"expected typed signed division failure, found $value")
    }
  }

  test("dispatch performs ordered floating equality") {
    val voidType     = operand(OperandKind.IdResult, 1)
    val f32          = operand(OperandKind.IdResult, 2)
    val bool         = operand(OperandKind.IdResult, 3)
    val pointerType  = operand(OperandKind.IdResult, 4)
    val function     = operand(OperandKind.IdResult, 6)
    val output       = operand(OperandKind.IdResult, 7)
    val left         = operand(OperandKind.IdResult, 8)
    val right        = operand(OperandKind.IdResult, 9)
    val equal        = operand(OperandKind.IdResult, 11)
    val lessEqual    = operand(OperandKind.IdResult, 12)
    val selected     = operand(OperandKind.IdResult, 13)
    val result       = operand(OperandKind.IdResult, 14)
    val instructions = Vec(
      Instruction.TypeVoid(voidType),
      Instruction.TypeFloat(f32, operand(OperandKind.LiteralInteger, 32), None),
      Instruction.TypeBool(bool),
      Instruction.TypePointer(pointerType, operand(OperandKind.StorageClass, spv.StorageClass.CrossWorkgroup), f32),
      Instruction.TypeFunction(operand(OperandKind.IdResult, 5), voidType, Vec(pointerType, f32, f32)),
      Instruction.EntryPoint(
        operand(OperandKind.ExecutionModel, spv.ExecutionModel.Kernel),
        function,
        stringOperand("floating_equal"),
        Vec.empty
      ),
      Instruction.Function(
        voidType,
        function,
        operand(OperandKind.FunctionControl, spv.FunctionControl.None),
        operand(OperandKind.IdRef, 5)
      ),
      Instruction.FunctionParameter(pointerType, output),
      Instruction.FunctionParameter(f32, left),
      Instruction.FunctionParameter(f32, right),
      Instruction.Label(operand(OperandKind.IdResult, 10)),
      Instruction.FOrdEqual(bool, equal, left, right),
      Instruction.FOrdLessThanEqual(bool, lessEqual, left, right),
      Instruction.Select(f32, selected, equal, left, right),
      Instruction.Select(f32, result, lessEqual, left, selected),
      Instruction.Store(output, result, None),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    val module =
      Module(Header(ISA.version, Word(0), Word(16), Word(0)), instructions, Endianness.Little)

    def run(id: Long > 0, leftBits: Long, rightBits: Long): Vec[Byte] = {
      val allocation = AllocationId(id)
      val memory     = ArrayMemory(allocation, 4)
      val dispatch   = KernelTask
        .validated(
          "floating_equal",
          Dimensions.One,
          Dimensions.One,
          Vec(
            KernelTask.Argument.Memory(allocation, 0L),
            KernelTask.Argument.Scalar(LittleEndian.toVec(leftBits, 4)),
            KernelTask.Argument.Scalar(LittleEndian.toVec(rightBits, 4))
          )
        )
        .toOption
        .get
      assertEquals(executeUnchecked(module, dispatch, memory), Right(()))
      memory.bytes
    }

    assertEquals(run(7L, 0x80000000L, 0L), LittleEndian.toVec(0x80000000L, 4))
    assertEquals(run(8L, 0x7fc00000L, 0x40400000L), LittleEndian.toVec(0x40400000L, 4))
    assertEquals(run(9L, 0x40000000L, 0x40400000L), LittleEndian.toVec(0x40000000L, 4))

    val malformed = module.copy(instructions =
      instructions.updated(
        11,
        Instruction.FOrdEqual(f32, equal, left, right)
      )
    )
    val allocation = AllocationId(10L)
    val dispatch   = KernelTask
      .validated(
        "floating_equal",
        Dimensions.One,
        Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 4)),
          KernelTask.Argument.Scalar(LittleEndian.toVec(0L, 4))
        )
      )
      .toOption
      .get
    executeUnchecked(malformed, dispatch, ArrayMemory(allocation, 4)) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpFOrdEqual result type %2 is not a boolean scalar or vector")
      case value => fail(s"expected typed ordered equality failure, found $value")
    }

    val malformedLessEqual = module.copy(instructions =
      instructions.updated(
        12,
        Instruction.FOrdLessThanEqual(f32, lessEqual, left, right)
      )
    )
    executeUnchecked(malformedLessEqual, dispatch, ArrayMemory(allocation, 4)) match {
      case Left(Fault.InvalidTask(message)) =>
        assertEquals(message, "OpFOrdLessThanEqual result type %2 is not a boolean scalar or vector")
      case value => fail(s"expected typed ordered less-than-or-equal failure, found $value")
    }
  }
}
