package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

/** GLCompute modules that cover the descriptor-binding and `GLSL.std.450` execution paths without an external
  * assembler.
  */
private object vulkan {

  private enum Arithmetic { case Multiply, Sin, Pow }

  private def op(kind: OperandKind, values: Int*): Operand =
    Operand(
      kind,
      Vec.from(values.map(Word(_))).unsafe[NonEmpty[Vec[Word]]]
    )
  private def id(value: Int): Operand                      = op(OperandKind.IdRef, value)
  private def lit(value: Int): Operand                     = op(OperandKind.LiteralInteger, value)
  private def str(value: _root_.java.lang.String): Operand = {
    val bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8) :+ 0.toByte
    val words = bytes.grouped(4).map { group =>
      val width =
        group.length.refine[Int where Between[0, 8]].fold(
          message => throw IllegalArgumentException(message),
          identity
        )
      Word(LittleEndian.decode(group, 0, width).toInt)
    }
    Operand(OperandKind.LiteralString, Vec.from(words).unsafe)
  }
  private val none: Option[Operand] = None

  private val Void             = 1
  private val FnType           = 2
  private val Float            = 3
  private val RuntimeArray     = 4
  private val OutStruct        = 5
  private val OutPtrType       = 6
  private val OutVar           = 7
  private val Int              = 8
  private val Int0             = 9
  private val Args             = 10
  private val ArgsStruct       = 11
  private val ArgsPtrType      = 12
  private val ArgsVar          = 13
  private val UniformFloatPtr  = 14
  private val Int1             = 15
  private val Int2             = 16
  private val StorageFloatPtr  = 17
  private val UInt             = 18
  private val SpecA            = 19
  private val SpecB            = 20
  private val SpecC            = 21
  private val V3UInt           = 22
  private val WorkGroupSize    = 23
  private val Main             = 24
  private val Label            = 25
  private val ExtSet           = 26
  private val AcA              = 27
  private val A                = 28
  private val AcB              = 29
  private val B                = 30
  private val ArithmeticResult = 31
  private val AcC              = 32
  private val C                = 33
  private val Sum              = 34
  private val AcOut            = 35

  private def arithmetic(kind: Arithmetic): Instruction = kind match {
    case Arithmetic.Multiply => Instruction.FMul(id(Float), id(ArithmeticResult), id(A), id(B))
    case Arithmetic.Sin      =>
      Instruction.ExtInst(
        id(Float),
        id(ArithmeticResult),
        id(ExtSet),
        op(OperandKind.LiteralExtInstInteger, ExtendedInstruction.GlslStd450.Sin),
        Vec(id(A))
      )
    case Arithmetic.Pow =>
      Instruction.ExtInst(
        id(Float),
        id(ArithmeticResult),
        id(ExtSet),
        op(OperandKind.LiteralExtInstInteger, ExtendedInstruction.GlslStd450.Pow),
        Vec(id(A), id(B))
      )
  }

  private def descriptorKernel(kind: Arithmetic, interface: Boolean, extra: Vec[Instruction]): Module = {
    val entryInterface = if (interface) Vec(id(OutVar), id(ArgsVar)) else Vec.empty[Operand]
    val instructions   = Vec(
      Instruction.Capability(op(OperandKind.Capability, Capability.Shader)),
      Instruction.ExtInstImport(id(ExtSet), str("GLSL.std.450")),
      Instruction.MemoryModel(
        op(OperandKind.AddressingModel, AddressingModel.Logical),
        op(OperandKind.MemoryModel, MemoryModel.GLSL450)
      ),
      Instruction.EntryPoint(
        op(OperandKind.ExecutionModel, ExecutionModel.GLCompute),
        id(Main),
        str("main"),
        entryInterface
      ),
      Instruction.ExecutionMode(id(Main), op(OperandKind.ExecutionMode, ExecutionMode.LocalSize, 1, 1, 1)),
      Instruction.Decorate(id(RuntimeArray), op(OperandKind.Decoration, Decoration.ArrayStride, 4)),
      Instruction.Decorate(id(OutStruct), op(OperandKind.Decoration, Decoration.Block)),
      Instruction.MemberDecorate(id(OutStruct), lit(0), op(OperandKind.Decoration, Decoration.Offset, 0)),
      Instruction.Decorate(id(OutVar), op(OperandKind.Decoration, Decoration.Binding, 0)),
      Instruction.Decorate(id(OutVar), op(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
      Instruction.MemberDecorate(id(Args), lit(0), op(OperandKind.Decoration, Decoration.Offset, 0)),
      Instruction.MemberDecorate(id(Args), lit(1), op(OperandKind.Decoration, Decoration.Offset, 4)),
      Instruction.MemberDecorate(id(Args), lit(2), op(OperandKind.Decoration, Decoration.Offset, 8)),
      Instruction.Decorate(id(ArgsStruct), op(OperandKind.Decoration, Decoration.Block)),
      Instruction.MemberDecorate(id(ArgsStruct), lit(0), op(OperandKind.Decoration, Decoration.Offset, 0)),
      Instruction.Decorate(id(ArgsVar), op(OperandKind.Decoration, Decoration.Binding, 1)),
      Instruction.Decorate(id(ArgsVar), op(OperandKind.Decoration, Decoration.DescriptorSet, 0)),
      Instruction.Decorate(id(SpecA), op(OperandKind.Decoration, Decoration.SpecId, 1)),
      Instruction.Decorate(id(SpecB), op(OperandKind.Decoration, Decoration.SpecId, 1)),
      Instruction.Decorate(id(SpecC), op(OperandKind.Decoration, Decoration.SpecId, 1)),
      Instruction.Decorate(id(WorkGroupSize), op(OperandKind.Decoration, Decoration.BuiltIn, BuiltIn.WorkgroupSize)),
      Instruction.TypeVoid(id(Void)),
      Instruction.TypeFunction(id(FnType), id(Void), Vec.empty[Operand]),
      Instruction.TypeFloat(id(Float), lit(32), none),
      Instruction.TypeRuntimeArray(id(RuntimeArray), id(Float)),
      Instruction.TypeStruct(id(OutStruct), Vec(id(RuntimeArray))),
      Instruction.TypePointer(id(OutPtrType), op(OperandKind.StorageClass, StorageClass.StorageBuffer), id(OutStruct)),
      Instruction.Variable(id(OutPtrType), id(OutVar), op(OperandKind.StorageClass, StorageClass.StorageBuffer), none),
      Instruction.TypeInt(id(Int), lit(32), lit(1)),
      Instruction.Constant(id(Int), id(Int0), lit(0)),
      Instruction.TypeStruct(id(Args), Vec(id(Float), id(Float), id(Float))),
      Instruction.TypeStruct(id(ArgsStruct), Vec(id(Args))),
      Instruction.TypePointer(id(ArgsPtrType), op(OperandKind.StorageClass, StorageClass.Uniform), id(ArgsStruct)),
      Instruction.Variable(id(ArgsPtrType), id(ArgsVar), op(OperandKind.StorageClass, StorageClass.Uniform), none),
      Instruction.TypePointer(id(UniformFloatPtr), op(OperandKind.StorageClass, StorageClass.Uniform), id(Float)),
      Instruction.Constant(id(Int), id(Int1), lit(1)),
      Instruction.Constant(id(Int), id(Int2), lit(2)),
      Instruction.TypePointer(id(StorageFloatPtr), op(OperandKind.StorageClass, StorageClass.StorageBuffer), id(Float)),
      Instruction.TypeInt(id(UInt), lit(32), lit(0)),
      Instruction.SpecConstant(id(UInt), id(SpecA), lit(1)),
      Instruction.SpecConstant(id(UInt), id(SpecB), lit(1)),
      Instruction.SpecConstant(id(UInt), id(SpecC), lit(1)),
      Instruction.TypeVector(id(V3UInt), id(UInt), lit(3)),
      Instruction.SpecConstantComposite(id(V3UInt), id(WorkGroupSize), Vec(id(SpecA), id(SpecB), id(SpecC)))
    ) ++ extra ++ Vec(
      Instruction.Function(id(Void), id(Main), op(OperandKind.FunctionControl, FunctionControl.None), id(FnType)),
      Instruction.Label(id(Label)),
      Instruction.AccessChain(id(UniformFloatPtr), id(AcA), id(ArgsVar), Vec(id(Int0), id(Int0))),
      Instruction.Load(id(Float), id(A), id(AcA), none),
      Instruction.AccessChain(id(UniformFloatPtr), id(AcB), id(ArgsVar), Vec(id(Int0), id(Int1))),
      Instruction.Load(id(Float), id(B), id(AcB), none),
      arithmetic(kind),
      Instruction.AccessChain(id(UniformFloatPtr), id(AcC), id(ArgsVar), Vec(id(Int0), id(Int2))),
      Instruction.Load(id(Float), id(C), id(AcC), none),
      Instruction.FAdd(id(Float), id(Sum), id(ArithmeticResult), id(C)),
      Instruction.AccessChain(id(StorageFloatPtr), id(AcOut), id(OutVar), Vec(id(Int0), id(Int0))),
      Instruction.Store(id(AcOut), id(Sum), none),
      Instruction.Return(),
      Instruction.FunctionEnd()
    )
    Module(
      Header(if (interface) ISA.version else Version(1, 3), Word(0), Word(64), Word(0)),
      instructions,
      Endianness.Little
    )
  }

  /** Computes `a * b + c` from a uniform struct into a storage buffer. */
  def fma: Module = descriptorKernel(Arithmetic.Multiply, interface = false, Vec.empty[Instruction])

  /** Computes `sin(a) + c`. */
  def sin: Module = descriptorKernel(Arithmetic.Sin, interface = false, Vec.empty[Instruction])

  /** Computes `pow(a, b) + c`. */
  def pow: Module = descriptorKernel(Arithmetic.Pow, interface = true, Vec.empty[Instruction])

  /** Carries a `SpecConstantOp` evaluated by the compiler (`CompositeExtract` over the work-group size). */
  def specialization: Module = descriptorKernel(
    Arithmetic.Multiply,
    interface = false,
    Vec(Instruction.SpecConstantOp(
      id(UInt),
      id(ArithmeticResult),
      op(OperandKind.LiteralSpecConstantOpInteger, Instruction.Definition.CompositeExtract.opcodeNumber.toInt),
      Vec(id(WorkGroupSize), lit(0))
    ))
  )
}
