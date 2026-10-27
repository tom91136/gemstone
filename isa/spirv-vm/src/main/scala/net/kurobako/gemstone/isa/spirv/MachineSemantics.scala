package net.kurobako.gemstone.isa.spirv

import VMMemory.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.spirv.asm.*
import net.kurobako.gemstone.semantics.render.*
import scala.util.control.NonFatal

/** The machine semantics of SPIR-V instructions, as definitions the explorer shows. */
object MachineSemantics {

  /** An instruction at the operands it is shown with: the words of its operands, the shape of each value id and the
    * names of the ids the definition reads.
    */
  private case class Form(
      text: String,
      opcode: String,
      words: Vec[Int],
      shapes: Map[Int, Shape],
      names: Map[Int, String],
      bytes: Int,
      leaves: (offset: Int, count: Int),
      dims: (rows: Int, columns: Int),
      use: Int,
      indexes: Option[Vec[Long]]
  )

  private val bool: Shape  = Shape.Scalar(Type.Bool)
  private val u32: Shape   = Shape.Scalar(Type.unsigned(Type.width(32)))
  private val u64: Shape   = Shape.Scalar(Type.unsigned(Type.width(64)))
  private val u32x2: Shape = Shape.Vector(Type.unsigned(Type.width(32)), 2)
  private val u32x4: Shape = Shape.Vector(Type.unsigned(Type.width(32)), 4)
  private val u64x3: Shape = Shape.Vector(Type.unsigned(Type.width(64)), 3)
  private val u64x4: Shape = Shape.Vector(Type.unsigned(Type.width(64)), 4)
  private val bool4: Shape = Shape.Vector(Type.Bool, 4)
  private val f32: Shape   = Shape.Scalar(Type.Float(Type.width(32)))
  private val f32x2: Shape = Shape.Vector(Type.Float(Type.width(32)), 2)
  private val f32x3: Shape = Shape.Vector(Type.Float(Type.width(32)), 3)
  private val f32x4: Shape = Shape.Vector(Type.Float(Type.width(32)), 4)
  private val pair: Shape  = Shape.Structure(Vec(u32, Shape.Vector(Type.unsigned(Type.width(32)), 2)))

  private def form(
      opcode: String,
      words: Vec[Int],
      text: String = "",
      bytes: Int = 0,
      leaves: (offset: Int, count: Int) = (0, 0),
      dims: (rows: Int, columns: Int) = (0, 0),
      use: Int = 0,
      indexes: Option[Vec[Long]] = None
  )(shapes: (Int, Shape)*)(names: (Int, String)*): Form =
    Form(
      if (text.isEmpty) opcode else text,
      opcode,
      words,
      shapes.toMap,
      names.toMap,
      bytes,
      leaves,
      dims,
      use,
      indexes
    )

  private def chainForm(opcode: String, base: Int, indexes: Vec[Long], first: String, second: String): Form =
    form(opcode, Vec.from(1 to base + 2), s"$opcode(struct)", indexes = Some(indexes))(
      2        -> u64,
      base     -> u64,
      base + 1 -> u32,
      base + 2 -> u32
    )(2 -> "result", base -> "base", base + 1 -> first, base + 2 -> second)

  private lazy val controlForms: Vec[Form] = Vec(
    form("OpLabel", Vec(1))()(),
    form("OpBranch", Vec(1))()(),
    form("OpBranchConditional", Vec(1, 2, 3), "OpBranchConditional(bool)")(1 -> bool)(1 -> "condition"),
    form("OpSwitch", Vec(1, 2, 0, 3, 1, 4), "OpSwitch(uint32)")(1 -> u32)(1             -> "selector"),
    form("OpPhi", Vec(1, 2, 3, 4, 5, 6), "OpPhi(uint32, uint32): uint32")(2 -> u32, 3 -> u32, 5 -> u32)(
      2 -> "result",
      3 -> "first",
      5 -> "second"
    ),
    form("OpFunctionCall", Vec(1, 2, 3, 4, 5), "OpFunctionCall(uint32, uint32): uint32")(
      2 -> u32,
      4 -> u32,
      5 -> u32
    )(2 -> "result", 4 -> "first", 5 -> "second"),
    form("OpFunctionPointerCallINTEL", Vec(1, 2, 3, 4), "OpFunctionPointerCallINTEL(uint64, uint32): uint32")(
      2 -> u32,
      3 -> u64,
      4 -> u32
    )(2 -> "result", 3 -> "pointer", 4 -> "argument"),
    form("OpReturn", Vec.empty)()(),
    form("OpReturnValue", Vec(1), "OpReturnValue(uint32)")(1 -> u32)(1 -> "value"),
    form("OpKill", Vec.empty)()(),
    form("OpTerminateInvocation", Vec.empty)()(),
    form("OpUnreachable", Vec.empty)()(),
    form("OpAbortKHR", Vec(1, 2), "OpAbortKHR(uint32)")(2 -> u32)(2 -> "message")
  )

  private lazy val memoryForms: Vec[Form] = Vec(
    form("OpLoad", Vec(1, 2, 3), "OpLoad(uint32)", 4)(2 -> u32, 3 -> u64)(2 -> "result", 3  -> "pointer"),
    form("OpStore", Vec(1, 2), "OpStore(uint32)", 4)(1 -> u64, 2 -> u32)(1  -> "pointer", 2 -> "value"),
    form("OpCopyMemory", Vec(1, 2), "OpCopyMemory")(1 -> u64, 2 -> u64)(1   -> "target", 2  -> "source"),
    form("OpCopyMemorySized", Vec(1, 2, 3), "OpCopyMemorySized(uint64)")(1 -> u64, 2 -> u64, 3 -> u64)(
      1 -> "target",
      2 -> "source",
      3 -> "size"
    ),
    form("OpAtomicLoad", Vec(1, 2, 3, 4, 5), "OpAtomicLoad(uint32)", 4)(2 -> u32, 3 -> u64, 4 -> u32, 5 -> u32)(
      2 -> "result",
      3 -> "pointer",
      4 -> "scope",
      5 -> "semantics"
    ),
    form("OpAtomicStore", Vec(1, 2, 3, 4), "OpAtomicStore(uint32)", 4)(1 -> u64, 2 -> u32, 3 -> u32, 4 -> u32)(
      1 -> "pointer",
      2 -> "scope",
      3 -> "semantics",
      4 -> "value"
    ),
    form("OpAtomicFlagTestAndSet", Vec(1, 2, 3, 4, 5), "OpAtomicFlagTestAndSet(bool)", 4)(
      2 -> bool,
      3 -> u64,
      4 -> u32,
      5 -> u32
    )(2 -> "result", 3 -> "pointer", 4 -> "scope", 5 -> "semantics"),
    form("OpAtomicFlagClear", Vec(1, 2, 3), "OpAtomicFlagClear", 4)(1 -> u64, 2 -> u32, 3 -> u32)(
      1 -> "pointer",
      2 -> "scope",
      3 -> "semantics"
    ),
    form("OpGroupAsyncCopy", Vec(1, 2, 3, 4, 5, 6, 7, 8))(3 -> u32, 4 -> u64, 5 -> u64, 6 -> u64, 7 -> u64)(
      3 -> "scope",
      4 -> "destination",
      5 -> "source",
      6 -> "count",
      7 -> "stride"
    ),
    form("OpUntypedGroupAsyncCopyKHR", Vec(1, 2, 3, 4, 5, 6, 7, 8, 9))(
      3 -> u32,
      4 -> u64,
      5 -> u64,
      6 -> u64,
      7 -> u64,
      8 -> u64
    )(3 -> "scope", 4 -> "destination", 5 -> "source", 6 -> "elementBytes", 7 -> "count", 8 -> "stride"),
    form("OpGroupWaitEvents", Vec(1, 2, 3))(1 -> u32, 2 -> u64, 3 -> u64)(1 -> "scope", 2 -> "count", 3 -> "events")
  )

  private lazy val compositeForms: Vec[Form] = Vec(
    form("OpCompositeExtract", Vec(1, 2, 3, 2), "OpCompositeExtract(uint32[4])", leaves = (2, 1))(2 -> u32, 3 -> u32x4)(
      2 -> "result",
      3 -> "composite"
    ),
    form("OpCompositeConstruct", Vec(1, 2, 3, 4, 5, 6), "OpCompositeConstruct(uint32[4])")(
      2 -> u32x4,
      3 -> u32,
      4 -> u32,
      5 -> u32,
      6 -> u32
    )(2 -> "result", 3 -> "x", 4 -> "y", 5 -> "z", 6 -> "w"),
    form(
      "OpCompositeConstructReplicateEXT",
      Vec(1, 2, 3),
      "OpCompositeConstructReplicateEXT(uint32[4])",
      leaves = (0, 4)
    )(
      2 -> u32x4,
      3 -> u32
    )(2 -> "result", 3 -> "value"),
    form("OpCompositeInsert", Vec(1, 2, 3, 4, 1), "OpCompositeInsert(uint32[4])", leaves = (1, 1))(
      2 -> u32x4,
      3 -> u32,
      4 -> u32x4
    )(2 -> "result", 3 -> "inserted", 4 -> "composite"),
    form("OpVectorShuffle", Vec(1, 2, 3, 4, 3, 0, 5, -1), "OpVectorShuffle(uint32[4])")(
      2 -> u32x4,
      3 -> u32x4,
      4 -> u32x4
    )(2 -> "result", 3 -> "left", 4 -> "right"),
    form("OpVectorExtractDynamic", Vec(1, 2, 3, 4), "OpVectorExtractDynamic(uint32[4])")(
      2 -> u32,
      3 -> u32x4,
      4 -> u32
    )(2 -> "result", 3 -> "vector", 4 -> "index"),
    form("OpVectorInsertDynamic", Vec(1, 2, 3, 4, 5), "OpVectorInsertDynamic(uint32[4])")(
      2 -> u32x4,
      3 -> u32x4,
      4 -> u32,
      5 -> u32
    )(2 -> "result", 3 -> "vector", 4 -> "component", 5 -> "index"),
    form("OpCopyObject", Vec(1, 2, 3), "OpCopyObject(uint32[4])")(2 -> u32x4, 3 -> u32x4)(
      2 -> "result",
      3 -> "source"
    ),
    form("OpConditionalCopyObjectINTEL", Vec(1, 2, 3, 4, 5, 6), "OpConditionalCopyObjectINTEL(uint32)")(
      2 -> u32,
      3 -> bool,
      4 -> u32,
      5 -> bool,
      6 -> u32
    )(2 -> "result", 3 -> "condition0", 4 -> "operand0", 5 -> "condition1", 6 -> "operand1"),
    form("OpCopyLogical", Vec(1, 2, 3), "OpCopyLogical(struct)")(2 -> pair, 3 -> pair)(
      2 -> "result",
      3 -> "source"
    ),
    form("OpExpectKHR", Vec(1, 2, 3, 4), "OpExpectKHR(uint32[4])")(2 -> u32x4, 3 -> u32x4, 4 -> u32x4)(
      2 -> "result",
      3 -> "value",
      4 -> "expected"
    ),
    form("OpUndef", Vec(1, 2), "OpUndef(uint32[4])")(2 -> u32x4)(2         -> "result"),
    form("OpPoisonKHR", Vec(1, 2), "OpPoisonKHR(uint32[4])")(2 -> u32x4)(2 -> "result")
  )

  private lazy val pointerForms: Vec[Form] = Vec(
    form("OpConvertPtrToU", Vec(1, 2, 3), "OpConvertPtrToU(uint64)")(2 -> u64, 3 -> u64)(
      2 -> "result",
      3 -> "pointer"
    ),
    form("OpConvertUToPtr", Vec(1, 2, 3), "OpConvertUToPtr(uint64)")(2 -> u64, 3 -> u64)(
      2 -> "result",
      3 -> "source"
    ),
    form("OpPtrCastToGeneric", Vec(1, 2, 3), "OpPtrCastToGeneric")(2 -> u64, 3 -> u64)(2 -> "result", 3 -> "pointer"),
    form("OpGenericCastToPtr", Vec(1, 2, 3), "OpGenericCastToPtr")(2 -> u64, 3 -> u64)(2 -> "result", 3 -> "pointer"),
    form("OpGenericCastToPtrExplicit", Vec(1, 2, 3, 4), "OpGenericCastToPtrExplicit")(2 -> u64, 3 -> u64)(
      2 -> "result",
      3 -> "pointer"
    ),
    form("OpArrayLength", Vec(1, 2, 3, 1), "OpArrayLength")(2 -> u32, 3 -> u64)(2 -> "result", 3 -> "structure"),
    chainForm("OpAccessChain", 3, Vec(1L, 0L), "member", "index"),
    chainForm("OpInBoundsAccessChain", 3, Vec(1L, 0L), "member", "index"),
    chainForm("OpPtrAccessChain", 3, Vec(1L), "element", "member"),
    chainForm("OpInBoundsPtrAccessChain", 3, Vec(1L), "element", "member")
  )

  private lazy val barrierForms: Vec[Form] = Vec(
    form("OpControlBarrier", Vec(1, 2, 3))(1 -> u32, 2 -> u32, 3 -> u32)(
      1 -> "execution",
      2 -> "memory",
      3 -> "semantics"
    ),
    form("OpControlBarrierArriveEXT", Vec(1, 2, 3))(1 -> u32, 2 -> u32, 3 -> u32)(
      1 -> "execution",
      2 -> "memory",
      3 -> "semantics"
    ),
    form("OpControlBarrierWaitEXT", Vec(1, 2, 3))(1 -> u32, 2 -> u32, 3 -> u32)(
      1 -> "execution",
      2 -> "memory",
      3 -> "semantics"
    ),
    form("OpMemoryBarrier", Vec(1, 2))(1 -> u32, 2 -> u32)(1 -> "scope", 2 -> "semantics"),
    form("OpMemoryNamedBarrier", Vec(1, 2, 3))(1 -> u64, 2 -> u32, 3 -> u32)(
      1 -> "barrier",
      2 -> "memory",
      3 -> "semantics"
    ),
    form("OpNamedBarrierInitialize", Vec(1, 2, 3))(2 -> u64, 3 -> u32)(2 -> "result", 3 -> "count")
  )

  private def reductionForm(opcode: String, value: Shape, scheme: String, operation: Int): Form =
    form(opcode, Vec(1, 2, 3, operation, 4), s"$opcode($scheme)")(2 -> value, 3 -> u32, 4 -> value)(
      2 -> "result",
      3 -> "execution",
      4 -> "value"
    )

  private def clusteredForm(opcode: String, value: Shape, scheme: String, operation: Int): Form = {
    val reduction = reductionForm(opcode, value, scheme, operation)
    reduction.copy(
      words = reduction.words :+ 5,
      shapes = reduction.shapes.updated(5, u32),
      names = reduction.names.updated(5, "size")
    )
  }

  private lazy val reductionForms: Vec[Form] = {
    val integers = Vec(
      "OpGroupNonUniformIAdd",
      "OpGroupNonUniformIMul",
      "OpGroupNonUniformSMin",
      "OpGroupNonUniformUMin",
      "OpGroupNonUniformSMax",
      "OpGroupNonUniformUMax",
      "OpGroupNonUniformBitwiseAnd",
      "OpGroupNonUniformBitwiseOr",
      "OpGroupNonUniformBitwiseXor",
      "OpGroupIAdd",
      "OpGroupUMin",
      "OpGroupSMin",
      "OpGroupUMax",
      "OpGroupSMax",
      "OpGroupIMulKHR",
      "OpGroupBitwiseAndKHR",
      "OpGroupBitwiseOrKHR",
      "OpGroupBitwiseXorKHR",
      "OpGroupIAddNonUniformAMD",
      "OpGroupUMinNonUniformAMD",
      "OpGroupSMinNonUniformAMD",
      "OpGroupUMaxNonUniformAMD",
      "OpGroupSMaxNonUniformAMD"
    )
    val floats = Vec(
      "OpGroupNonUniformFAdd",
      "OpGroupNonUniformFMul",
      "OpGroupNonUniformFMin",
      "OpGroupNonUniformFMax",
      "OpGroupFAdd",
      "OpGroupFMin",
      "OpGroupFMax",
      "OpGroupFMulKHR",
      "OpGroupFAddNonUniformAMD",
      "OpGroupFMinNonUniformAMD",
      "OpGroupFMaxNonUniformAMD"
    )
    val logicals = Vec(
      "OpGroupNonUniformLogicalAnd",
      "OpGroupNonUniformLogicalOr",
      "OpGroupNonUniformLogicalXor",
      "OpGroupLogicalAndKHR",
      "OpGroupLogicalOrKHR",
      "OpGroupLogicalXorKHR"
    )
    integers.map(reductionForm(_, u32, "uint32", 0)) ++
      floats.map(reductionForm(_, f32, "float32", 0)) ++
      logicals.map(reductionForm(_, bool, "bool", 0)) ++
      Vec(
        reductionForm("OpGroupNonUniformIAdd", u32, "uint32, inclusive scan", 1),
        reductionForm("OpGroupNonUniformIAdd", u32, "uint32, exclusive scan", 2),
        clusteredForm("OpGroupNonUniformIAdd", u32, "uint32, clustered", 3)
      )
  }

  private lazy val voteForms: Vec[Form] = {
    def scoped(opcode: String, shape: Shape = bool, operand: String = "bool", name: String = "predicate") =
      form(opcode, Vec(1, 2, 3, 4), s"$opcode($operand)")(
        2 -> bool,
        3 -> u32,
        4 -> shape
      )(2 -> "result", 3 -> "execution", 4 -> name)
    def implied(opcode: String) =
      form(opcode, Vec(1, 2, 3), s"$opcode(bool)")(2 -> bool, 3 -> bool)(2 -> "result", 3 -> "predicate")
    Vec(
      scoped("OpGroupAll"),
      scoped("OpGroupAny"),
      scoped("OpGroupNonUniformAll"),
      scoped("OpGroupNonUniformAny"),
      scoped("OpGroupNonUniformAllEqual", u32, "uint32", "value"),
      implied("OpSubgroupAllKHR"),
      implied("OpSubgroupAnyKHR"),
      implied("OpSubgroupAllEqualKHR"),
      form("OpGroupNonUniformElect", Vec(1, 2, 3))(2 -> bool, 3 -> u32)(2 -> "result", 3 -> "execution")
    )
  }

  private lazy val shuffleForms: Vec[Form] = {
    def lane(opcode: String, selector: String) =
      form(opcode, Vec(1, 2, 3, 4, 5), s"$opcode(uint32)")(2 -> u32, 3 -> u32, 4 -> u32, 5 -> u32)(
        2 -> "result",
        3 -> "execution",
        4 -> "value",
        5 -> selector
      )
    Vec(
      lane("OpGroupNonUniformBroadcast", "id"),
      lane("OpGroupNonUniformShuffle", "id"),
      lane("OpGroupNonUniformShuffleXor", "mask"),
      lane("OpGroupNonUniformShuffleUp", "delta"),
      lane("OpGroupNonUniformShuffleDown", "delta"),
      lane("OpGroupNonUniformRotateKHR", "delta"),
      lane("OpGroupNonUniformQuadBroadcast", "index"),
      lane("OpGroupNonUniformQuadSwap", "direction"),
      lane("OpGroupBroadcast", "localId"),
      form("OpGroupNonUniformBroadcastFirst", Vec(1, 2, 3, 4), "OpGroupNonUniformBroadcastFirst(uint32)")(
        2 -> u32,
        3 -> u32,
        4 -> u32
      )(2 -> "result", 3 -> "execution", 4 -> "value"),
      form("OpSubgroupFirstInvocationKHR", Vec(1, 2, 3), "OpSubgroupFirstInvocationKHR(uint32)")(2 -> u32, 3 -> u32)(
        2 -> "result",
        3 -> "value"
      ),
      form("OpSubgroupReadInvocationKHR", Vec(1, 2, 3, 4), "OpSubgroupReadInvocationKHR(uint32)")(
        2 -> u32,
        3 -> u32,
        4 -> u32
      )(2 -> "result", 3 -> "value", 4 -> "index"),
      form("OpSubgroupShuffleINTEL", Vec(1, 2, 3, 4), "OpSubgroupShuffleINTEL(uint32)")(2 -> u32, 3 -> u32, 4 -> u32)(
        2 -> "result",
        3 -> "data",
        4 -> "invocationId"
      ),
      form("OpSubgroupShuffleXorINTEL", Vec(1, 2, 3, 4), "OpSubgroupShuffleXorINTEL(uint32)")(
        2 -> u32,
        3 -> u32,
        4 -> u32
      )(2 -> "result", 3 -> "data", 4 -> "value"),
      form("OpSubgroupShuffleUpINTEL", Vec(1, 2, 3, 4, 5), "OpSubgroupShuffleUpINTEL(uint32)")(
        2 -> u32,
        3 -> u32,
        4 -> u32,
        5 -> u32
      )(2 -> "result", 3 -> "previous", 4 -> "current", 5 -> "delta"),
      form("OpSubgroupShuffleDownINTEL", Vec(1, 2, 3, 4, 5), "OpSubgroupShuffleDownINTEL(uint32)")(
        2 -> u32,
        3 -> u32,
        4 -> u32,
        5 -> u32
      )(2 -> "result", 3 -> "current", 4 -> "next", 5 -> "delta")
    )
  }

  private lazy val ballotForms: Vec[Form] = {
    def query(opcode: String, result: Shape, operation: Option[Int] = None, index: Boolean = false) = {
      val words  = Vec.from(Seq(1, 2, 3) ++ operation ++ Seq(4) ++ Option.when(index)(5))
      val shapes = Seq(2 -> result, 3 -> u32, 4 -> u32x4) ++ Option.when(index)(5 -> u32)
      val names  = Seq(2 -> "result", 3 -> "execution", 4 -> "ballot") ++ Option.when(index)(5 -> "index")
      form(opcode, words, s"$opcode(uint32[4])")(shapes*)(names*)
    }
    Vec(
      form("OpGroupNonUniformBallot", Vec(1, 2, 3, 4), "OpGroupNonUniformBallot(uint32[4])")(
        2 -> u32x4,
        3 -> u32,
        4 -> bool
      )(2 -> "result", 3 -> "execution", 4 -> "predicate"),
      form("OpSubgroupBallotKHR", Vec(1, 2, 3), "OpSubgroupBallotKHR(uint32[4])")(2 -> u32x4, 3 -> bool)(
        2 -> "result",
        3 -> "predicate"
      ),
      form("OpGroupNonUniformPartitionEXT", Vec(1, 2, 3), "OpGroupNonUniformPartitionEXT(uint32[4])")(
        2 -> u32x4,
        3 -> u32
      )(2 -> "result", 3 -> "value"),
      query("OpGroupNonUniformInverseBallot", bool),
      query("OpGroupNonUniformBallotBitExtract", bool, index = true),
      query("OpGroupNonUniformBallotBitCount", u32, operation = Some(0)),
      query("OpGroupNonUniformBallotFindLSB", u32),
      query("OpGroupNonUniformBallotFindMSB", u32)
    )
  }

  private lazy val clockForms: Vec[Form] = Vec(
    form("OpReadClockKHR", Vec(1, 2, 3), "OpReadClockKHR(uint64)")(2 -> u64, 3 -> u32)(2 -> "result", 3 -> "scope"),
    form("OpReadClockKHR", Vec(1, 2, 3), "OpReadClockKHR(uint32[2])", leaves = (0, 2))(2 -> u32x2, 3 -> u32)(
      2 -> "result",
      3 -> "scope"
    )
  )

  private lazy val matrixForms: Vec[Form] = {
    val matrix = Shape.Vector(Type.unsigned(Type.width(32)), 6)
    Vec(
      form("OpTranspose", Vec(1, 2, 3), "OpTranspose(uint32[6])", dims = (2, 3))(2 -> matrix, 3 -> matrix)(
        2 -> "result",
        3 -> "matrix"
      )
    )
  }

  private lazy val lineForms: Vec[Form] = {
    val matrix = u32x4
    val line   = u32x2

    def conversion(opcode: String, use: Int, name: String)(result: Shape, source: (Shape, String)) = form(
      opcode,
      Vec(1, 2, 3),
      s"$opcode($name)",
      dims = (2, 2),
      use = use
    )(2 -> result, 3 -> source._1)(2 -> "result", 3 -> source._2)
    Vec(
      conversion("OpCompositeConstructCoopMatQCOM", 0, "A")(matrix, line           -> "lines"),
      conversion("OpCompositeExtractCoopMatQCOM", 0, "A")(line, matrix             -> "matrix"),
      conversion("OpCompositeConstructCoopMatQCOM", 1, "B")(matrix, line           -> "lines"),
      conversion("OpCompositeExtractCoopMatQCOM", 1, "B")(line, matrix             -> "matrix"),
      conversion("OpCompositeConstructCoopMatQCOM", 2, "accumulator")(matrix, line -> "lines"),
      conversion("OpCompositeExtractCoopMatQCOM", 2, "accumulator")(line, matrix   -> "matrix")
    )
  }

  private lazy val variableForms: Vec[Form] = Vec(
    form("OpVariable", Vec(1, 2, 3))(2 -> u64)(2 -> "result"),
    form("OpVariable", Vec(1, 2, 3, 4), "OpVariable(initialiser)")(2 -> u64, 4 -> u32)(
      2 -> "result",
      4 -> "initialiser"
    ),
    form("OpVariableLengthArrayINTEL", Vec(1, 2, 3))(2 -> u64, 3 -> u32)(2 -> "result", 3 -> "length"),
    form("OpSaveMemoryINTEL", Vec(1, 2))(2 -> u64)(2                       -> "result"),
    form("OpRestoreMemoryINTEL", Vec(1))(1 -> u64)(1                       -> "pointer"),
    form("OpGenericPtrMemSemantics", Vec(1, 2, 3), "OpGenericPtrMemSemantics(uint32)")(2 -> u32, 3 -> u64)(
      2 -> "result",
      3 -> "pointer"
    )
  )

  private lazy val extendedForms: Vec[Form] = {
    def lanes(count: Int): Shape = Shape.Vector(Type.Float(Type.width(32)), count)
    def vectorForm(opcode: String, words: Vec[Int], count: Int, operands: (Int, String, Shape)*): Form =
      form(opcode, words, bytes = 4, dims = (count, count))(operands.map((index, _, shape) => (index, shape))*)(
        operands.map((index, name, _) => (index, name))*
      )
    val matrixWords   = Vec(1, 2, 3)
    val matrixNames   = Seq(2 -> "result", 3 -> "matrix")
    val loadWords     = Vec(1, 2, 3, 4)
    val storeWords    = Vec(3, 4, 5)
    val loadOperands  = Seq((3, "offset", u64), (4, "pointer", u64))
    val storeOperands = Seq((4, "offset", u64), (5, "pointer", u64))
    Vec.from(Seq(2, 3, 4)).flatMap(rows =>
      Vec(
        form("GLSL.std.450 Determinant", matrixWords, bytes = 4, dims = (rows, rows))(
          2 -> f32,
          3 -> lanes(rows * rows)
        )(matrixNames*),
        form("GLSL.std.450 MatrixInverse", matrixWords, bytes = 4, dims = (rows, rows))(
          2 -> lanes(rows * rows),
          3 -> lanes(rows * rows)
        )(matrixNames*)
      )
    ) ++ Vec.from(Seq(2, 3, 4, 8, 16)).flatMap(count =>
      Vec(
        vectorForm("OpenCL.std vloadn", loadWords, count, ((2, "result", lanes(count)) +: loadOperands)*),
        vectorForm("OpenCL.std vstoren", storeWords, count, ((3, "data", lanes(count)) +: storeOperands)*)
      )
    ) ++ Vec(
      form("OpenCL.std printf", Vec(1, 2, 3, 4), bytes = 4)(2 -> u32, 3 -> u64, 4 -> u32)(
        2 -> "result",
        3 -> "format",
        4 -> "argument"
      ),
      form("OpenCL.std shuffle", Vec(1, 2, 3, 4), bytes = 4)(2 -> lanes(4), 3 -> lanes(4), 4 -> u32x4)(
        2 -> "result",
        3 -> "vector",
        4 -> "mask"
      ),
      form(
        "OpenCL.std shuffle2",
        Vec(1, 2, 3, 4, 5),
        bytes = 4
      )(2 -> lanes(4), 3 -> lanes(4), 4 -> lanes(4), 5 -> u32x4)(
        2 -> "result",
        3 -> "first",
        4 -> "second",
        5 -> "mask"
      )
    )
  }

  private lazy val imageForms: Vec[Form] = {
    val image = 3 -> "image"
    val lod   = 2
    def sampling(opcode: String, coordinate: Shape, explicit: Boolean, dref: Boolean, result: Shape): Form = {
      val reference = Seq(5).filter(_ => dref)
      val words  = Vec.from(Seq(1, 2, 3, 4) ++ reference ++ (if (explicit) Seq(lod, 5 + reference.size) else Seq.empty))
      val extras = reference.map(_ -> f32) ++ Option.when(explicit)((5 + reference.size) -> f32)
      val named  = reference.map(_ -> "reference") ++ Option.when(explicit)((5 + reference.size) -> "lod")
      form(opcode, words)((Seq(2 -> result, 3 -> u64, 4 -> coordinate) ++ extras)*)(
        (Seq(2 -> "result", image, 4 -> "coordinate") ++ named)*
      )
    }
    def query(opcode: String, result: Shape, extra: Option[(String, Shape)] = None): Form =
      form(opcode, Vec.from(Seq(1, 2, 3) ++ extra.map(_ => 4)))(
        (Seq(2 -> result, 3 -> u64) ++ extra.map(4 -> _._2))*
      )((Seq(2 -> "result", image) ++ extra.map(4 -> _._1))*)
    Vec(
      form("OpSampledImage", Vec(1, 2, 3, 4))(2 -> u64, 3 -> u64, 4 -> u64)(
        2 -> "result",
        image,
        4 -> "sampler"
      ),
      form("OpImage", Vec(1, 2, 3))(2 -> u64, 3 -> u64)(2 -> "result", 3 -> "sampledImage"),
      sampling("OpImageSampleImplicitLod", f32x2, explicit = false, dref = false, f32x4),
      sampling("OpImageSampleExplicitLod", f32x2, explicit = true, dref = false, f32x4),
      sampling("OpImageSampleDrefImplicitLod", f32x2, explicit = false, dref = true, f32),
      sampling("OpImageSampleDrefExplicitLod", f32x2, explicit = true, dref = true, f32),
      sampling("OpImageSampleProjImplicitLod", f32x3, explicit = false, dref = false, f32x4),
      sampling("OpImageSampleProjExplicitLod", f32x3, explicit = true, dref = false, f32x4),
      sampling("OpImageSampleProjDrefImplicitLod", f32x3, explicit = false, dref = true, f32),
      sampling("OpImageSampleProjDrefExplicitLod", f32x3, explicit = true, dref = true, f32),
      form("OpImageGather", Vec(1, 2, 3, 4, 5))(2 -> f32x4, 3 -> u64, 4 -> f32x2, 5 -> u32)(
        2 -> "result",
        image,
        4 -> "coordinate",
        5 -> "component"
      ),
      form("OpImageDrefGather", Vec(1, 2, 3, 4, 5))(2 -> f32x4, 3 -> u64, 4 -> f32x2, 5 -> f32)(
        2 -> "result",
        image,
        4 -> "coordinate",
        5 -> "reference"
      ),
      query("OpImageQueryLod", f32x2, Some("coordinate" -> f32x2)),
      query("OpImageQueryLevels", u32),
      query("OpImageQuerySizeLod", u32x2, Some("lod" -> u32)),
      query("OpImageQuerySize", u32x2),
      query("OpImageQuerySamples", u32),
      query("OpImageQueryOrder", u32),
      query("OpImageQueryFormat", u32),
      form("OpImageFetch", Vec(1, 2, 3, 4))(2 -> f32x4, 3 -> u64, 4 -> u32x2)(2 -> "result", image, 4 -> "coordinate"),
      form("OpImageRead", Vec(1, 2, 3, 4))(2 -> f32x4, 3 -> u64, 4 -> u32x2)(2  -> "result", image, 4 -> "coordinate"),
      form("OpImageWrite", Vec(1, 2, 3))(1 -> u64, 2 -> u32x2, 3 -> f32x4)(
        1 -> "image",
        2 -> "coordinate",
        3 -> "texel"
      ),
      form("OpImageTexelPointer", Vec(1, 2, 3, 4, 5))(2 -> u64, 3 -> u64, 4 -> u32x2, 5 -> u32)(
        2 -> "result",
        image,
        4 -> "coordinate",
        5 -> "sample"
      )
    )
  }

  private lazy val cooperativeCallForms: Vec[Form] = {
    val matrix = Shape.Vector(Type.Float(Type.width(32)), 6)
    def reduce(name: String, mask: Int, source: (rows: Int, columns: Int), result: Shape) =
      form(
        "OpCooperativeMatrixReduceNV",
        Vec(1, 2, 3, mask, 5),
        s"OpCooperativeMatrixReduceNV($name)",
        dims = source
      )(2 -> result, 3 -> Shape.Vector(Type.Float(Type.width(32)), source.rows * source.columns))(
        2 -> "result",
        3 -> "source"
      )
    Vec(
      form("OpCooperativeMatrixPerElementOpNV", Vec(1, 2, 3, 4), dims = (2, 3))(2 -> matrix, 3 -> matrix)(
        2 -> "result",
        3 -> "source"
      ),
      form(
        "OpCooperativeMatrixPerElementOpNV",
        Vec(1, 2, 3, 4, 5),
        "OpCooperativeMatrixPerElementOpNV(operand)",
        dims = (2, 3)
      )(2 -> matrix, 3 -> matrix, 5 -> matrix)(2 -> "result", 3 -> "source", 5 -> "operand"),
      reduce("Row", 1, (2, 3), matrix),
      reduce("Column", 2, (2, 3), matrix),
      reduce("Row, Column", 3, (2, 3), matrix),
      reduce("2x2", 4, (2, 2), f32)
    )
  }

  private lazy val cooperativeForms: Vec[Form] = {
    val matrix = (2, 2)

    def load(opcode: String, first: String, second: String) =
      form(opcode, Vec(1, 2, 3, 4, 5), dims = matrix)(2 -> f32x4, 3 -> u64, 4 -> u32, 5 -> u32)(
        2 -> "result",
        3 -> "pointer",
        4 -> first,
        5 -> second
      )
    def store(opcode: String, first: String, second: String) =
      form(opcode, Vec(1, 2, 3, 4), dims = matrix)(1 -> u64, 2 -> f32x4, 3 -> u32, 4 -> u32)(
        1 -> "pointer",
        2 -> "source",
        3 -> first,
        4 -> second
      )
    Vec(
      form("OpCooperativeMatrixLengthKHR", Vec(1, 2, 3), dims = matrix)(2 -> u32)(2 -> "result"),
      form("OpCooperativeMatrixLengthNV", Vec(1, 2, 3), dims = matrix)(2 -> u32)(2  -> "result"),
      load("OpCooperativeMatrixLoadKHR", "layout", "stride"),
      load("OpCooperativeMatrixLoadNV", "stride", "columnMajor"),
      store("OpCooperativeMatrixStoreKHR", "layout", "stride"),
      store("OpCooperativeMatrixStoreNV", "stride", "columnMajor"),
      form("OpCooperativeMatrixConvertNV", Vec(1, 2, 3))(2 -> f32x4, 3 -> f32x4)(2 -> "result", 3 -> "source"),
      form("OpCooperativeMatrixTransposeNV", Vec(1, 2, 3), dims = (2, 3))(
        2 -> Shape.Vector(Type.Float(Type.width(32)), 6),
        3 -> Shape.Vector(Type.Float(Type.width(32)), 6)
      )(2 -> "result", 3 -> "source"),
      form("OpCooperativeVectorLoadNV", Vec(1, 2, 3, 4))(2 -> f32x4, 3 -> u64, 4 -> u32)(
        2 -> "result",
        3 -> "pointer",
        4 -> "offset"
      ),
      form("OpCooperativeVectorStoreNV", Vec(1, 2, 3))(1 -> u64, 2 -> u32, 3 -> f32x4)(
        1 -> "pointer",
        2 -> "offset",
        3 -> "source"
      ),
      form("OpCooperativeVectorMatrixMulNV", Vec.from(1 to 11))(
        2  -> f32x4,
        3  -> f32x4,
        4  -> u32,
        5  -> u64,
        6  -> u32,
        7  -> u32,
        8  -> u32,
        9  -> u32,
        10 -> u32,
        11 -> u32
      )(
        2  -> "result",
        3  -> "input",
        4  -> "inputKind",
        5  -> "matrix",
        6  -> "matrixOffset",
        7  -> "matrixKind",
        8  -> "rows",
        9  -> "columns",
        10 -> "layout",
        11 -> "transpose"
      ),
      form("OpCooperativeVectorMatrixMulAddNV", Vec.from(1 to 14))(
        2  -> f32x4,
        3  -> f32x4,
        4  -> u32,
        5  -> u64,
        6  -> u32,
        7  -> u32,
        8  -> u64,
        9  -> u32,
        10 -> u32,
        11 -> u32,
        12 -> u32,
        13 -> u32,
        14 -> u32
      )(
        2  -> "result",
        3  -> "input",
        4  -> "inputKind",
        5  -> "matrix",
        6  -> "matrixOffset",
        7  -> "matrixKind",
        8  -> "bias",
        9  -> "biasOffset",
        10 -> "biasKind",
        11 -> "rows",
        12 -> "columns",
        13 -> "layout",
        14 -> "transpose"
      ),
      form("OpCooperativeVectorOuterProductAccumulateNV", Vec.from(1 to 6))(
        1 -> u64,
        2 -> u32,
        3 -> f32x4,
        4 -> f32x4,
        5 -> u32,
        6 -> u32
      )(1 -> "pointer", 2 -> "offset", 3 -> "left", 4 -> "right", 5 -> "layout", 6 -> "matrixKind"),
      form("OpCooperativeVectorReduceSumAccumulateNV", Vec(1, 2, 3))(1 -> u64, 2 -> u32, 3 -> f32x4)(
        1 -> "pointer",
        2 -> "offset",
        3 -> "source"
      )
    )
  }

  private lazy val pipeForms: Vec[Form] = {
    def transfer(opcode: String, size: Int) = form(opcode, Vec.from(1 to size))(2 -> u32, 3 -> u64, 4 -> u64)(
      2 -> "result",
      3 -> "pipe",
      4 -> "packet"
    )
    def reserved(opcode: String) = form(opcode, Vec.from(1 to 8))(2 -> u32, 3 -> u64, 4 -> u64, 5 -> u32, 6 -> u64)(
      2 -> "result",
      3 -> "pipe",
      4 -> "reservation",
      5 -> "index",
      6 -> "packet"
    )
    def reservation(opcode: String) = form(opcode, Vec.from(1 to 6))(2 -> u64, 3 -> u64, 4 -> u32)(
      2 -> "result",
      3 -> "pipe",
      4 -> "count"
    )
    def grouped(opcode: String) = form(opcode, Vec.from(1 to 7))(2 -> u64, 3 -> u32, 4 -> u64, 5 -> u32)(
      2 -> "result",
      3 -> "scope",
      4 -> "pipe",
      5 -> "count"
    )
    def query(opcode: String) = form(opcode, Vec.from(1 to 5))(2 -> u32, 3 -> u64)(2 -> "result", 3 -> "pipe")
    Vec(
      transfer("OpWritePipe", 6),
      transfer("OpReadPipe", 6),
      reserved("OpReservedWritePipe"),
      reserved("OpReservedReadPipe"),
      reservation("OpReserveWritePipePackets"),
      reservation("OpReserveReadPipePackets"),
      grouped("OpGroupReserveWritePipePackets"),
      grouped("OpGroupReserveReadPipePackets"),
      form("OpIsValidReserveId", Vec(1, 2, 3))(2 -> bool, 3 -> u64)(2 -> "result", 3 -> "reservation"),
      query("OpGetNumPipePackets"),
      query("OpGetMaxPipePackets"),
      form("OpCreatePipeFromPipeStorage", Vec(1, 2, 3))(2 -> u64, 3 -> u64)(2 -> "result", 3 -> "storage")
    )
  }

  private lazy val forms: Vec[Form] = controlForms ++ memoryForms ++ compositeForms ++ pointerForms ++ barrierForms ++
    reductionForms ++ voteForms ++ shuffleForms ++ ballotForms ++ clockForms ++ matrixForms ++ lineForms ++
    variableForms ++ extendedForms ++ imageForms ++ cooperativeForms ++ cooperativeCallForms ++ pipeForms ++
    deviceForms ++ tensorForms ++ tensorArmForms ++ vendorForms ++ untypedForms ++ intelBlockForms

  private lazy val deviceForms: Vec[Form] = {
    val ndrange: Shape = Shape.Structure(Vec(u32, u64x3, u64x3, u64x3))
    val enqueueSlots   = Seq[(Int, String, Shape)](
      (2, "result", u32),
      (3, "queue", u64),
      (4, "flags", u32),
      (5, "range", ndrange),
      (6, "count", u32),
      (7, "waits", u64),
      (8, "returned", u64),
      (9, "invoke", u64),
      (10, "param", u64),
      (11, "size", u32),
      (12, "alignment", u32)
    ) ++ (13 to 16).map(index => (index, s"localSize${index - 13}", u32))
    def enqueueKernel(arity: Int): Form = {
      val slots = enqueueSlots.take(arity - 1)
      form("OpEnqueueKernel", Vec.from(1 to arity))(slots.map((index, _, shape) => (index, shape))*)(
        slots.map((index, name, _) => (index, name))*
      )
    }
    def kernelQuery(opcode: String) = form(opcode, Vec.from(1 to 6))(2 -> u32, 3 -> u64, 4 -> u64, 5 -> u32, 6 -> u32)(
      2 -> "result",
      3 -> "invoke",
      4 -> "param",
      5 -> "size",
      6 -> "alignment"
    )
    def ndrangeQuery(opcode: String) =
      form(opcode, Vec.from(1 to 7))(2 -> u32, 3 -> ndrange, 4 -> u64, 5 -> u64, 6 -> u32, 7 -> u32)(
        2 -> "result",
        3 -> "range",
        4 -> "invoke",
        5 -> "param",
        6 -> "size",
        7 -> "alignment"
      )
    Vec(
      form("OpGetDefaultQueue", Vec.from(1 to 2))(2 -> u64)(2 -> "result"),
      form("OpBuildNDRange", Vec.from(1 to 5))(2 -> ndrange, 3 -> u64x3, 4 -> u64x3, 5 -> u64x3)(
        2 -> "result",
        3 -> "global",
        4 -> "local",
        5 -> "offset"
      ),
      form("OpEnqueueMarker", Vec.from(1 to 6))(2 -> u32, 3 -> u64, 4 -> u32, 5 -> u64, 6 -> u64)(
        2 -> "result",
        3 -> "queue",
        4 -> "count",
        5 -> "waits",
        6 -> "returned"
      ),
      form("OpRetainEvent", Vec(1))(1 -> u64)(1                       -> "event"),
      form("OpReleaseEvent", Vec(1))(1 -> u64)(1                      -> "event"),
      form("OpCreateUserEvent", Vec.from(1 to 2))(2 -> u64)(2         -> "result"),
      form("OpIsValidEvent", Vec.from(1 to 3))(2 -> bool, 3 -> u64)(2 -> "result", 3 -> "event"),
      form("OpSetUserEventStatus", Vec(1, 2))(1 -> u64, 2 -> u32)(1   -> "event", 2  -> "status"),
      form("OpCaptureEventProfilingInfo", Vec.from(1 to 3))(1 -> u64, 2 -> u32, 3 -> u64)(
        1 -> "event",
        2 -> "info",
        3 -> "value"
      ),
      kernelQuery("OpGetKernelWorkGroupSize"),
      kernelQuery("OpGetKernelPreferredWorkGroupSizeMultiple"),
      kernelQuery("OpGetKernelMaxNumSubgroups"),
      form("OpGetKernelLocalSizeForSubgroupCount", Vec.from(1 to 7))(
        2 -> u32,
        3 -> u32,
        4 -> u64,
        5 -> u64,
        6 -> u32,
        7 -> u32
      )(2 -> "result", 3 -> "subgroups", 4 -> "invoke", 5 -> "param", 6 -> "size", 7 -> "alignment"),
      ndrangeQuery("OpGetKernelNDrangeSubGroupCount"),
      ndrangeQuery("OpGetKernelNDrangeMaxSubGroupSize")
    ) ++ Vec.from((13 to 16).map(arity => enqueueKernel(arity)))
  }

  private lazy val tensorForms: Vec[Form] = {
    def operation(opcode: String, words: Vec[Int], operands: (Int, String, Shape)*): Form =
      form(opcode, words)(operands.map((index, _, shape) => (index, shape))*)(
        operands.map((index, name, _) => (index, name))*
      )
    def inputs(first: Int, last: Int, name: String): Seq[(Int, String, Shape)] =
      (first to last).map(index => (index, s"$name$index", u32))
    def sized(opcode: String, arities: Range, head: Seq[(Int, String, Shape)], name: String): Seq[Form] =
      arities.map(arity => operation(opcode, Vec.from(1 to arity), (head ++ inputs(4, arity, name))*))
    val layoutSlots = Seq((2, "result", u64), (3, "layout", u64))
    val viewSlots   = Seq((2, "result", u64), (3, "view", u64))
    Vec.from(Seq(
      Seq(operation("OpCreateTensorLayoutNV", Vec.from(1 to 2), (2, "result", u64))),
      sized("OpTensorLayoutSetDimensionNV", 4 to 8, layoutSlots, "dimension"),
      sized("OpTensorLayoutSetStrideNV", 4 to 8, layoutSlots, "stride"),
      sized("OpTensorLayoutSetBlockSizeNV", 4 to 8, layoutSlots, "size"),
      sized("OpTensorLayoutSliceNV", 4 to 13, layoutSlots, "slice"),
      Seq(operation(
        "OpTensorLayoutSetClampValueNV",
        Vec.from(1 to 4),
        (2, "result", u64),
        (3, "layout", u64),
        (4, "value", u32)
      )),
      Seq(operation("OpCreateTensorViewNV", Vec.from(1 to 2), (2, "result", u64))),
      sized("OpTensorViewSetDimensionNV", 4 to 8, viewSlots, "dimension"),
      sized("OpTensorViewSetStrideNV", 4 to 8, viewSlots, "stride"),
      Seq(operation(
        "OpTensorViewSetClipNV",
        Vec.from(1 to 7),
        (2, "result", u64),
        (3, "view", u64),
        (4, "rowOffset", u32),
        (5, "rowSpan", u32),
        (6, "columnOffset", u32),
        (7, "columnSpan", u32)
      )),
      Seq(
        operation(
          "OpCooperativeMatrixLoadTensorNV",
          Vec(1, 2, 3, 4, 5, 0, 0),
          (2, "result", f32x4),
          (3, "pointer", u64),
          (4, "object", f32x4),
          (5, "layout", u64)
        ),
        operation(
          "OpCooperativeMatrixLoadTensorNV",
          Vec(1, 2, 3, 4, 5, 0, 1, 8),
          (2, "result", f32x4),
          (3, "pointer", u64),
          (4, "object", f32x4),
          (5, "layout", u64),
          (8, "view", u64)
        )
      ),
      Seq(
        operation(
          "OpCooperativeMatrixStoreTensorNV",
          Vec(1, 2, 3, 0, 0),
          (1, "pointer", u64),
          (2, "object", f32x4),
          (3, "layout", u64)
        ),
        operation(
          "OpCooperativeMatrixStoreTensorNV",
          Vec(1, 2, 3, 0, 1, 6),
          (1, "pointer", u64),
          (2, "object", f32x4),
          (3, "layout", u64),
          (6, "view", u64)
        )
      )
    ).flatten)
  }

  private lazy val tensorArmForms: Vec[Form] = {
    def coordinates(rank: Int): Shape = if (rank == 1) u32 else Shape.Vector(Type.unsigned(Type.width(32)), rank)
    val reads                         = Vec(1, 2, 3, 4)
    val outOfBoundsReads              = Vec(1, 2, 3, 4, 2, 5)
    val writes                        = Vec(1, 2, 3)
    Vec.from(Seq(
      form("OpTensorQuerySizeARM", Vec(1, 2, 3, 4))(2 -> u32, 3 -> u64, 4 -> u32)(
        2 -> "result",
        3 -> "tensor",
        4 -> "dimension"
      )
    ) ++ (1 to 3).flatMap(rank =>
      Seq(
        form("OpTensorReadARM", reads, s"OpTensorReadARM rank $rank", bytes = 4, dims = (rank, 0))(
          2 -> u32,
          3 -> u64,
          4 -> coordinates(rank)
        )(2 -> "result", 3 -> "tensor", 4 -> "coordinates"),
        form(
          "OpTensorReadARM",
          outOfBoundsReads,
          s"OpTensorReadARM rank $rank, out of bounds value",
          bytes = 4,
          dims = (rank, 0)
        )(2 -> u32, 3 -> u64, 4 -> coordinates(rank), 5 -> u32)(
          2 -> "result",
          3 -> "tensor",
          4 -> "coordinates",
          5 -> "outOfBounds"
        ),
        form("OpTensorWriteARM", writes, s"OpTensorWriteARM rank $rank", bytes = 4, dims = (rank, 0))(
          1 -> u64,
          2 -> coordinates(rank),
          3 -> u32
        )(1 -> "tensor", 2 -> "coordinates", 3 -> "value")
      )
    ))
  }

  private lazy val vendorForms: Vec[Form] = Vec(
    form("OpConvertHandleToImageINTEL", Vec(1, 2, 3))(2 -> u64, 3 -> u64)(2   -> "result", 3 -> "handle"),
    form("OpConvertHandleToSamplerINTEL", Vec(1, 2, 3))(2 -> u64, 3 -> u64)(2 -> "result", 3 -> "handle"),
    form("OpRawAccessChainNV", Vec(1, 2, 3, 4, 5, 6))(2 -> u64, 3 -> u64, 4 -> u64, 5 -> u64, 6 -> u64)(
      2 -> "result",
      3 -> "base",
      4 -> "stride",
      5 -> "element",
      6 -> "offset"
    ),
    form("OpPredicatedLoadINTEL", Vec(1, 2, 3, 4, 5), bytes = 4)(2 -> u32, 3 -> u64, 4 -> bool, 5 -> u32)(
      2 -> "result",
      3 -> "pointer",
      4 -> "predicate",
      5 -> "default"
    ),
    form("OpPredicatedStoreINTEL", Vec(1, 2, 3), bytes = 4)(1 -> u64, 2 -> u32, 3 -> bool)(
      1 -> "pointer",
      2 -> "value",
      3 -> "predicate"
    ),
    form("OpMaskedGatherINTEL", Vec(1, 2, 3, 4, 5, 6), bytes = 4)(2 -> u32x4, 3 -> u64x4, 5 -> bool4, 6 -> u32x4)(
      2 -> "result",
      3 -> "pointers",
      5 -> "mask",
      6 -> "fill"
    ),
    form("OpMaskedScatterINTEL", Vec(1, 2, 3, 4), bytes = 4)(1 -> u32x4, 2 -> u64x4, 4 -> bool4)(
      1 -> "input",
      2 -> "pointers",
      4 -> "mask"
    ),
    form("OpConvertHandleToSampledImageINTEL", Vec(1, 2, 3))(2 -> u64, 3 -> u64)(2    -> "result", 3  -> "handle"),
    form("OpSubgroupBlockReadINTEL", Vec(1, 2, 3), bytes = 4)(2 -> u32x4, 3 -> u64)(2 -> "result", 3  -> "pointer"),
    form("OpSubgroupBlockWriteINTEL", Vec(1, 2), bytes = 4)(1 -> u64, 2 -> u32x4)(1   -> "pointer", 2 -> "data"),
    form("OpSubgroupBlockPrefetchINTEL", Vec(1, 2))(1 -> u64, 2 -> u32)(1             -> "pointer", 2 -> "numBytes"),
    form("OpUntypedPrefetchKHR", Vec(1, 2))(1 -> u64, 2 -> u32)(1                     -> "pointer", 2 -> "numBytes"),
    form("OpSubgroup2DBlockPrefetchINTEL", Vec.from(1 to 9))(
      1 -> u32,
      2 -> u32,
      3 -> u32,
      4 -> u32,
      5 -> u64,
      6 -> u32,
      7 -> u32,
      8 -> u32,
      9 -> u32
    )(5                                                              -> "pointer"),
    form("OpBitCastArrayQCOM", Vec(1, 2, 3))(2 -> u64, 3 -> u32x2)(2 -> "result", 3 -> "source"),
    form("OpExtractSubArrayQCOM", Vec(1, 2, 3, 4))(2 -> u32x2, 3 -> u32x4, 4 -> u32)(
      2 -> "result",
      3 -> "source",
      4 -> "index"
    )
  )

  private lazy val untypedForms: Vec[Form] = Vec(
    form("OpUntypedVariableKHR", Vec(1, 2, 3, 4))(2 -> u64)(2 -> "result"),
    form("OpUntypedVariableKHR", Vec(1, 2, 3, 4, 5), "OpUntypedVariableKHR(initialiser)")(2 -> u64, 5 -> u32)(
      2 -> "result",
      5 -> "initialiser"
    ),
    form("OpUntypedVariableLengthArrayINTEL", Vec(1, 2, 3, 4))(2 -> u64, 4 -> u32)(2 -> "result", 4 -> "length"),
    form("OpUntypedArrayLengthKHR", Vec(1, 2, 3, 4, 1))(2 -> u32, 4 -> u64)(2        -> "result", 4 -> "pointer"),
    chainForm("OpUntypedAccessChainKHR", 4, Vec(1L, 0L), "member", "index"),
    chainForm("OpUntypedInBoundsAccessChainKHR", 4, Vec(1L, 0L), "member", "index"),
    chainForm("OpUntypedPtrAccessChainKHR", 4, Vec(1L), "element", "member"),
    chainForm("OpUntypedInBoundsPtrAccessChainKHR", 4, Vec(1L), "element", "member")
  )

  private def blockLoad(opcode: String): Form = form(opcode, Vec.from(1 to 10))(
    1  -> u64,
    2  -> u64,
    3  -> u64,
    4  -> u64,
    5  -> u64,
    6  -> u64,
    7  -> u64,
    8  -> u64,
    9  -> u32x2,
    10 -> u64
  )(
    1  -> "size",
    2  -> "width",
    3  -> "height",
    4  -> "count",
    5  -> "source",
    6  -> "rowBytes",
    7  -> "rows",
    8  -> "pitch",
    9  -> "at",
    10 -> "local"
  )

  private lazy val intelBlockForms: Vec[Form] = Vec(
    form("OpSubgroupImageBlockReadINTEL", Vec(1, 2, 3, 4), bytes = 4)(2 -> u32x4, 3 -> u64, 4 -> u32x2)(
      2 -> "result",
      3 -> "image",
      4 -> "coordinate"
    ),
    form("OpSubgroupImageBlockWriteINTEL", Vec(1, 2, 3), bytes = 4)(1 -> u64, 2 -> u32x2, 3 -> u32x4)(
      1 -> "image",
      2 -> "coordinate",
      3 -> "data"
    ),
    form("OpSubgroupImageMediaBlockReadINTEL", Vec(1, 2, 3, 4, 5, 6), bytes = 4)(
      2 -> u32x4,
      3 -> u64,
      4 -> u32x2,
      5 -> u64,
      6 -> u64
    )(2 -> "result", 3 -> "image", 4 -> "coordinate", 5 -> "width", 6 -> "height"),
    form("OpSubgroupImageMediaBlockWriteINTEL", Vec(1, 2, 3, 4, 5), bytes = 4)(
      1 -> u64,
      2 -> u32x2,
      3 -> u64,
      4 -> u64,
      5 -> u32x4
    )(1 -> "image", 2 -> "coordinate", 3 -> "width", 4 -> "height", 5 -> "data"),
    blockLoad("OpSubgroup2DBlockLoadINTEL"),
    blockLoad("OpSubgroup2DBlockLoadTransformINTEL"),
    blockLoad("OpSubgroup2DBlockLoadTransposeINTEL"),
    form("OpSubgroup2DBlockStoreINTEL", Vec.from(1 to 10))(
      1  -> u64,
      2  -> u64,
      3  -> u64,
      4  -> u64,
      5  -> u64,
      6  -> u64,
      7  -> u64,
      8  -> u64,
      9  -> u64,
      10 -> u32x2
    )(
      1  -> "size",
      2  -> "width",
      3  -> "height",
      4  -> "count",
      5  -> "local",
      6  -> "target",
      7  -> "rowBytes",
      8  -> "rows",
      9  -> "pitch",
      10 -> "at"
    ),
    form("OpSubgroupMatrixMultiplyAccumulateINTEL", Vec.from(1 to 6))(
      2 -> u32x4,
      3 -> u32,
      4 -> u32x4,
      5 -> u32x4,
      6 -> u32x4
    )(2 -> "result", 3 -> "inner", 4 -> "a", 5 -> "b", 6 -> "c")
  )

  /** A structure of a word and four words, and the word, whose layout the access chain forms walk. */
  private lazy val sample: Option[(program: VMProgram, block: Int, word: Int)] = for {
    module <- Parser.parse(
      """OpCapability Addresses
        |OpCapability Kernel
        |OpMemoryModel Physical64 OpenCL
        |%word = OpTypeInt 32 0
        |%four = OpConstant %word 4
        |%words = OpTypeArray %word %four
        |%block = OpTypeStruct %word %words
        |""".stripMargin
    ).toOption
    program <- LoadedProgram.loadUnchecked(module).toOption
    block   <- module.instructions.collectFirst { case Instruction.TypeStruct(dest, _) => Operand.id(dest) }
    word    <- module.instructions.collectFirst { case Instruction.TypeInt(dest, _, _) => Operand.id(dest) }
  } yield (program, block, word)

  private def cast(found: Form): Option[PointerCast] = sample.map(layout =>
    PointerCast(
      found.opcode,
      layout.program,
      layout.word,
      StorageClass.CrossWorkgroup,
      found.opcode.endsWith("Explicit")
    )
  )

  private def chain(found: Form): Option[AccessChain] = for {
    literals <- found.indexes
    layout   <- sample
  } yield AccessChain(
    layout.program,
    None,
    layout.word,
    None,
    Pointer(0L, layout.block, StorageClass.CrossWorkgroup),
    literals.toArray
  )

  private def rendered(found: Form): Either[Unrendered, Definition] = try {
    val scope   = Scope(IdRenderer.Instance(found.names, found.shapes))
    val machine = SPIRVMachineRender(scope)
    val steps   = if (found.opcode.contains(' ')) {
      ExtendedSemantics.render(found.opcode, found.words, found.bytes, found.dims.rows, machine).toRight(
        Unrendered.Unstaged(Vec.empty)
      )
    } else {
      Instruction.definitions.find(_.opcode.raw == found.opcode).toRight(
        Unrendered.Undefined(s"${found.opcode} is not in the grammar.")
      ).flatMap(definition =>
        definition.decoder(RawInstruction(definition.opcodeNumber, found.words.map(Word(_)))).left.map(error =>
          Unrendered.Undefined(error.toString)
        )
      ).flatMap { decoded =>
        ControlSemantics.render(decoded, machine).orElse(MemoryAccessSemantics.render(decoded, machine, found.bytes))
          .orElse(IntelBlockSemantics.render(decoded, machine, found.bytes))
          .orElse(VariableSemantics.render(decoded, machine))
          .orElse(BarrierSemantics.render(decoded, machine))
          .orElse(ImageSemantics.render(decoded, machine))
          .orElse(PipeSemantics.render(decoded, machine))
          .orElse(DeviceEnqueueSemantics.render(decoded, machine))
          .orElse(TensorAddressingSemantics.render(decoded, machine))
          .orElse(TensorArmSemantics.render(decoded, machine, found.dims.rows, found.bytes))
          .orElse(CooperativeSemantics.render(decoded, machine, found.dims.rows, found.dims.columns, found.use))
          .orElse(GroupSemantics.render(decoded, machine))
          .orElse(CompositeSemantics.render(decoded, machine, found.leaves.offset, found.leaves.count))
          .orElse(ClockSemantics.render(decoded, machine, found.leaves.count == 2))
          .orElse(MatrixSemantics.render(decoded, machine, found.dims.rows, found.dims.columns))
          .orElse(chain(found).flatMap(PointerAccessSemantics.renderChain(decoded, machine, _)))
          .orElse(chain(found).flatMap(PointerAccessSemantics.renderUntypedChain(decoded, machine, _)))
          .orElse(cast(found).flatMap(PointerAccessSemantics.render(decoded, machine, _)))
          .toRight(Unrendered.Unstaged(Vec.empty))
      }
    }
    steps.flatMap(steps => Definition.from(steps, found.opcode, scope, Vec.empty).left.map(Unrendered.Undefined(_)))
  } catch { case NonFatal(error) => Left(Unrendered.Undefined(s"${found.opcode} does not render: $error")) }

  /** The machine definitions of the instruction named `opcode`, each under the operands it is shown with, or none if it
    * has no machine semantics.
    */
  def machine(opcode: String): Vec[(instance: String, outcome: Either[Unrendered, Definition])] =
    forms.filter(_.opcode == opcode).map(found => (found.text, rendered(found)))
}
