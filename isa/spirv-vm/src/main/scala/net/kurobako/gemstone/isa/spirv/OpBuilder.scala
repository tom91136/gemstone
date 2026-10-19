package net.kurobako.gemstone.isa.spirv

import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

private object OpBuilder {

  /** What a staged semantics family returns for an instruction outside it. */
  val unmatched: Op = Op.Fail(Fault.Backend("no staged semantics family takes the instruction"))

  /** The op of `instruction` in the first of `families` that takes it. The one expansion of the core families, so each
    * family's op classes are generated once.
    */
  def staged(site: Site, instruction: Instruction, families: CoreFamily*): Op = {
    val sinks = OpBuilder(site, instruction.opcode.raw)
    families.view.map(SPIRVStaged.core(_, Execute, sinks, instruction)(unmatched)).find(_ ne unmatched)
      .getOrElse(Op.Fail(Fault.Backend(s"${instruction.opcode} has no ${families.mkString(" or ")} semantics")))
  }
}

/** The sink family that lowers semantics into ops at one site, `opcode` naming the instruction in faults. Shapes read
  * their operand ids and lane counts once, here; each op class inlines its kernel.
  */
private class OpBuilder(site: Site, opcode: String) extends SPIRVSinks[Long, Op] {
  private val program = site.program

  def width(typeId: Int): Int in 1 ~ 64 = program.integerShape(typeId).map(_._1)
    .orElse(program.floatingShape(typeId).map(_._1.widen[Int in 1 ~ 64]))
    .orElse(program.matrixShape(typeId).map(_._1.widen[Int in 1 ~ 64]))
    .getOrElse(1)

  def floatingWidth(typeId: Int): Int in (16, 32, 64) = program.floatingShape(typeId).map(_._1)
    .orElse(program.matrixShape(typeId).map(_._1))
    .getOrElse(32)

  def typeOf(value: Int): Int = program.valueType(value)

  def lanes(typeId: Int): Int = program.numericShape(typeId).map(_._2).orElse(program.booleanLanes(typeId)).getOrElse(1)

  def memberType(typeId: Int, index: Int): Int = program.runtimeType(typeId) match {
    case Some(RuntimeType.Structure(members)) if index < members.size => members(index)
    case Some(RuntimeType.Pointer(_, pointee))                        => pointee
    case _                                                            => -1
  }

  def rounding(result: Int, default: IEEE754.Rounding): IEEE754.Rounding =
    program.roundingModes.get(result).fold(default)(ConversionSemantics.rounding(_, default))

  def saturates(result: Int): Boolean = program.saturatedConversions.contains(result) || program.vulkan

  override def floating(typeId: Int): Boolean = program.floatingShape(typeId).nonEmpty

  override def minifloat(typeId: Int): Option[Minifloat] = program.float8Shape(typeId).map(_._1)

  override def signed(typeId: Int): Boolean = program.runtimeType(typeId) match {
    case Some(RuntimeType.Integer(_, signed))       => signed
    case Some(RuntimeType.VectorType(component, _)) => signed(component)
    case _                                          => false
  }

  private def lanesOf(value: Int): Int = lanes(typeOf(value))

  private def mismatch: Fault = Fault.InvalidRequest(s"$opcode operand/result shapes do not match")

  inline def unary(ids: OperandIds)(inline kernel: Long => Long): Op =
    StagedOps.Unary(site, ids.resultType, ids.result, ids(0))(kernel)

  inline def binary(ids: OperandIds)(inline kernel: (Long, Long) => Long): Op =
    StagedOps.Binary(site, ids.resultType, ids.result, ids(0), ids(1))(kernel)

  inline def ternary(ids: OperandIds)(inline kernel: (Long, Long, Long) => Long): Op =
    StagedOps.Ternary(site, ids.resultType, ids.result, ids(0), ids(1), ids(2))(kernel)

  inline def quaternary(ids: OperandIds)(inline kernel: (Long, Long, Long, Long) => Long): Op =
    StagedOps.Quaternary(
      site = site,
      resultType = ids.resultType,
      dest = ids.result,
      a = ids(0),
      b = ids(1),
      c = ids(2),
      d = ids(3)
    )(
      kernel
    )

  inline def broadcast(ids: OperandIds)(inline kernel: Long => Long): Op =
    StagedOps.Broadcast(ids.resultType, ids.result, ids(0), lanes(ids.resultType))(kernel)

  inline def indexed(ids: OperandIds)(inline kernel: (Long, Long) => Long): Op =
    StagedOps.Indexed(ids.resultType, ids.result, ids(0), lanes(ids.resultType))(kernel)

  inline def structure(ids: OperandIds)(inline first: Long => Long, inline second: Long => Long): Op =
    StagedOps.UnaryStructure(ids.resultType, ids.result, ids(0), lanesOf(ids(0)))(first, second)

  inline def binaryStructure(ids: OperandIds)(
      inline first: (Long, Long) => Long,
      inline second: (Long, Long) => Long
  ): Op = StagedOps.BinaryStructure(
    ids.resultType,
    ids.result,
    ids(0),
    ids(1),
    lanesOf(ids(0)),
    Fault.InvalidRequest("extended integer arithmetic requires a two-member integer structure and matching operands")
  )(first, second)

  private def target(ids: OperandIds): StagedOps.PointerStore = {
    val pointer = ids.operands.last
    StagedOps.PointerStore(program, opcode, pointer, memberType(typeOf(pointer), 0))
  }

  inline def store(ids: OperandIds)(inline first: Long => Long, inline second: Long => Long): Op =
    StagedOps.UnaryStore(ids.resultType, ids.result, ids(0), target(ids))(first, second)

  inline def binaryStore(ids: OperandIds)(inline first: (Long, Long) => Long, inline second: (Long, Long) => Long)
      : Op = StagedOps.BinaryStore(ids.resultType, ids.result, ids(0), ids(1), target(ids))(first, second)

  inline def fold(ids: OperandIds)(inline initial: Long, inline step: (Long, Long) => Long, inline finish: Long => Long)
      : Op = StagedOps.Fold(ids.resultType, ids.result, ids(0))(initial, step, finish)

  inline def binaryFold(ids: OperandIds)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline finish: Long => Long
  ): Op = StagedOps.BinaryFold(ids.resultType, ids.result, ids(0), ids(1))(initial, step, finish)

  inline def accumulatedFold(ids: OperandIds)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline finish: (Long, Long) => Long
  ): Op = StagedOps.AccumulatedFold(ids.resultType, ids.result, ids(0), ids(1), ids(2))(initial, step, finish)

  inline def matrixAccumulate(ids: OperandIds)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline finish: (Long, Long) => Long
  ): Op = {
    val left  = program.cooperativeMatrices.get(typeOf(ids(0)))
    val right = program.cooperativeMatrices.get(typeOf(ids(1)))
    StagedOps.CooperativeProduct(
      resultType = ids.resultType,
      dest = ids.result,
      a = ids(0),
      b = ids(1),
      c = ids(2),
      rows = left.fold(0)(_.rows),
      columns = right.fold(0)(_.columns),
      inner = left.fold(0)(_.columns)
    )(initial, step, finish)
  }

  /** Row-major indexing of column-major lanes: a left matrix is `rows` by `inner`, a right one `inner` by `columns`; a
    * vector operand is one row or column.
    */
  private def layout(ids: OperandIds): StagedOps.Product =
    (program.matrixShape(typeOf(ids(0))), program.matrixShape(typeOf(ids(1)))) match {
      case (Some((_, rows, inner)), Some((_, _, columns))) =>
        StagedOps.Product(
          rows = rows,
          columns = columns,
          inner = inner,
          leftRow = 1,
          leftInner = rows,
          rightInner = 1,
          rightColumn = inner,
          leftLanes = rows * inner,
          rightLanes = inner * columns
        )
      case (Some((_, rows, columns)), None) =>
        StagedOps.Product(
          rows = rows,
          columns = 1,
          inner = columns,
          leftRow = 1,
          leftInner = rows,
          rightInner = 1,
          rightColumn = 0,
          leftLanes = rows * columns,
          rightLanes = columns
        )
      case (_, Some((_, rows, columns))) =>
        StagedOps.Product(
          rows = 1,
          columns = columns,
          inner = rows,
          leftRow = 0,
          leftInner = 1,
          rightInner = 1,
          rightColumn = rows,
          leftLanes = rows,
          rightLanes = rows * columns
        )
      case _ =>
        StagedOps.Product(
          rows = 0,
          columns = 0,
          inner = 0,
          leftRow = 0,
          leftInner = 0,
          rightInner = 0,
          rightColumn = 0,
          leftLanes = -1,
          rightLanes = -1
        )
    }

  inline def product(ids: OperandIds)(inline step: (Long, Long, Long) => Long): Op =
    StagedOps.MatrixProduct(ids.resultType, ids.result, ids(0), ids(1), layout(ids), mismatch)(step)

  inline def outerProduct(ids: OperandIds)(inline kernel: (Long, Long) => Long): Op =
    StagedOps.OuterProduct(
      resultType = ids.resultType,
      dest = ids.result,
      a = ids(0),
      b = ids(1),
      rows = lanesOf(ids(0)),
      columns = lanesOf(ids(1)),
      mismatch = mismatch
    )(
      kernel
    )

  inline def cross(ids: OperandIds)(inline kernel: (Long, Long, Long, Long) => Long): Op =
    StagedOps.Cross(ids.resultType, ids.result, ids(0), ids(1), lanes(ids.resultType))(kernel)

  inline def projection(ids: OperandIds, dotted: Int)(
      inline initial: Long,
      inline step: (Long, Long, Long) => Long,
      inline element: (Long, Long, Long, Long) => Long
  ): Op = StagedOps.Projection(
    resultType = ids.resultType,
    dest = ids.result,
    a = ids(0),
    b = ids(1),
    c = if (ids.operands.size > 2) ids(2) else -1,
    dotted = ids(dotted)
  )(initial, step, element)

  inline def normalised(ids: OperandIds)(
      inline scale: (Long, Long) => Long,
      inline norm: (Long, Long, Long) => Long,
      inline element: (Long, Long, Long) => Long
  ): Op = StagedOps.Normalised(ids.resultType, ids.result, ids(0))(scale, norm, element)

  // shaders leave an out-of-range field undefined, which the semantics resolves; kernels fault
  inline def bitFieldExtract(ids: OperandIds)(
      inline inRange: (Long, Long) => Long,
      inline kernel: (Long, Long, Long) => Long
  ): Op = {
    val field = ternary(ids)(kernel)
    if (program.vulkan) field else StagedOps.FieldRange(program, ids(1), ids(2), field)(inRange)
  }

  inline def bitFieldInsert(ids: OperandIds)(
      inline inRange: (Long, Long) => Long,
      inline kernel: (Long, Long, Long, Long) => Long
  ): Op = {
    val field = quaternary(ids)(kernel)
    if (program.vulkan) field else StagedOps.FieldRange(program, ids(2), ids(3), field)(inRange)
  }

  // saturated and Vulkan conversions clamp an unrepresentable value; others fault on it
  inline def unsignedConversion(ids: OperandIds)(
      inline representable: Long => Long,
      inline kernel: Long => Long
  ): Op =
    if (saturates(ids.result)) unary(ids)(kernel)
    else
      StagedOps.UnsignedUnary(
        site,
        ids.resultType,
        ids.result,
        ids(0),
        floatingWidth(typeOf(ids(0))),
        width(ids.resultType),
        rounding(ids.result, IEEE754.Rounding.TowardZero)
      )(representable, kernel)

  private def access(ids: OperandIds, anyStorage: Boolean): StagedOps.Target = {
    val width = this.width(ids.resultType)
    StagedOps.Target(
      opcode,
      ids.resultType,
      ids(0),
      ids(1),
      ids(2),
      (width / 8).unsafe[Int in 1 ~ 8],
      Bits.mask(width),
      anyStorage
    )
  }

  inline def atomic(ids: OperandIds, anyStorage: Boolean)(inline kernel: (Long, Long) => Long): Op =
    StagedOps.Modify(access(ids, anyStorage), ids.result, ids(3))(kernel)

  inline def atomicUpdate(ids: OperandIds)(inline kernel: Long => Long): Op =
    StagedOps.Update(access(ids, anyStorage = true), ids.result)(kernel)

  inline def compareExchange(ids: OperandIds)(inline kernel: (Long, Long, Long) => Long): Op =
    StagedOps.Exchange(access(ids, anyStorage = false), ids(3), ids.result, ids(4), ids(5))(kernel)

  inline def addresses(ids: OperandIds)(inline kernel: (Long, Long) => Long): Op =
    StagedOps.PointerEquality(program, opcode, ids)(kernel)

  inline def addressDifference(ids: OperandIds)(inline kernel: (Long, Long, Long) => Long): Op =
    StagedOps.PointerDifference(program, ids)(kernel)

  inline def halfLoad(ids: OperandIds, aligned: Boolean)(inline convert: Long => Long): Op = {
    val count = lanes(ids.resultType)
    StagedOps.HalfLoad(ids.resultType, ids.result, ids(0), ids(1), count, if (aligned && count == 3) 4 else count)(
      convert
    )
  }

  inline def halfStore(ids: OperandIds, aligned: Boolean)(inline convert: Long => Long): Op = {
    val count = lanesOf(ids(0))
    StagedOps.HalfStore(ids(0), ids(1), ids(2), count, if (aligned && count == 3) 4 else count)(convert)
  }
}
