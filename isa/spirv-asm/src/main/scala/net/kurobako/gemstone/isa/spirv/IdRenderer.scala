package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*

object IdRenderer {

  trait LaneTerm { def apply(index: Int): Term }

  /** An instruction's ids as rendered semantics see them: the name and shape of every id, type ids included, the
    * roundings that decorate results and the results whose conversions saturate. An id stands for the scalar values its
    * shape holds.
    */
  case class Instance(
      names: Map[Int, String],
      shapes: Map[Int, Shape],
      roundings: Map[Int, IEEE754.Rounding] = Map.empty,
      saturated: Set[Int] = Set.empty
  ) extends Naming[Int] {

    /** The shape of `id`; a negative id is a member or pointee of another id's shape, as [[IdRenderer.memberType]]
      * numbers it.
      */
    def shape(id: Int): Option[Shape] =
      if (id >= 0) shapes.get(id)
      else
        shape(-id / 16).flatMap {
          case Shape.Structure(members) => members.lift(-id % 16 - 1)
          case Shape.Pointer(pointee)   => Some(pointee)
          case _                        => None
        }

    def name(id: Int): String = names.getOrElse(id, s"%$id")

    def resolve(id: Int): Vec[Naming.Name] =
      shape(id).fold(Vec.empty[Naming.Name])(
        _.components(name(id)).map((name, tpe) => Naming.Name(name, tpe))
      )
  }

  val MemoryType: String = "net.kurobako.gemstone.isa.spirv.SPIRVMemory"

  /** The context machine operations render against. */
  val Memory: String = "memory"

  private val Accumulator = "acc"
  private val Scale       = "scale"
  private val Norm        = "norm"
}

/** Semantics rendered as assignments to the components of result ids, reading the components of operand ids, each named
  * and typed by the instance and declared in `declarations`. Memory accesses render as [[SPIRVMemory]] operations at
  * flat byte addresses.
  */
class IdRenderer(instance: IdRenderer.Instance, declarations: Scope[Int])
    extends SPIRVSinks[Term, Option[Term.Steps]] {
  import IdRenderer.*
  import Term.*

  private def shape(id: Int): Option[Shape] = instance.shape(id)

  private def name(id: Int): String = instance.name(id)

  private def numeric(tpe: Type): Int in 1 ~ 64 = tpe.unranged match {
    case Type.Integer(width, _) => width
    case Type.Float(width)      => width
    case Type.Encoded(format)   => format.width.widen[Int in 1 ~ 64]
    case _                      => 1
  }

  def width(typeId: Int): Int in 1 ~ 64 = shape(typeId).fold(1: Int in 1 ~ 64) {
    case Shape.Scalar(tpe)                        => numeric(tpe)
    case Shape.Vector(component, _)               => numeric(component)
    case Shape.Matrix(component, _, _)            => numeric(component)
    case Shape.Cooperative(component = component) => numeric(component)
    case _                                        => 1
  }

  def floatingWidth(typeId: Int): Int in (16, 32, 64) =
    shape(typeId).map(_.scalar.unranged).collect { case Type.Float(width) => width }
      .flatMap(_.refine[Int in (16, 32, 64)].toOption).getOrElse(32)

  override def minifloat(typeId: Int): Option[Minifloat] = shape(typeId).map(_.scalar.unranged).collect {
    case Type.Encoded(format) => format
  }

  def typeOf(value: Int): Int = value

  def lanes(typeId: Int): Int = shape(typeId).fold(1) {
    case Shape.Vector(_, lanes) => lanes
    case _                      => 1
  }

  def memberType(typeId: Int, index: Int): Int = -(typeId * 16 + index + 1)

  def rounding(result: Int, default: IEEE754.Rounding): IEEE754.Rounding = instance.roundings.getOrElse(result, default)

  def saturates(result: Int): Boolean = instance.saturated.contains(result)

  override def floating(typeId: Int): Boolean = shape(typeId).exists(_.scalar.unranged match {
    case Type.Float(_) => true
    case _             => false
  })

  override def signed(typeId: Int): Boolean = shape(typeId).exists(_.scalar.unranged match {
    case Type.Integer(_, signed) => signed
    case _                       => false
  })

  /** The components of `id`; an id without a shape, such as a void result, has none. */
  private def components(id: Int): Vec[Naming.Name] = instance.resolve(id)

  private def lanesOf(id: Int): Int = shape(id).fold(1)(_.count)

  /** Lane `index` of `id`; a scalar repeats in every lane, and a shorter operand wraps. */
  private def lane(id: Int, index: Int): Term = {
    val all = components(id)
    Variable(all(index % all.size).name)
  }

  private def result(ids: OperandIds, index: Int): String = components(ids.result)(index).name

  private def rendered(ids: OperandIds, read: Seq[Int], steps: Seq[(String, Term)]): Option[Term.Steps] = {
    (read.distinct :+ ids.result).foreach(declarations.operands(_))
    Some(Term.Steps(Vec.from(steps.map(Term.Step(_, _)))))
  }

  private def lanewise(ids: OperandIds, operands: Int*)(kernel: LaneTerm): Option[Term.Steps] =
    rendered(ids, operands.map(ids(_)), Seq.tabulate(lanesOf(ids.result))(index => result(ids, index) -> kernel(index)))

  private def access(method: String, tpe: Type, arguments: Vec[Term], result: Option[Type]): Term = Term.context(
    (MemoryType, Memory),
    method,
    Vec(tpe),
    arguments.zip(Some(Shape.Address) +: Vec.fill(arguments.size - 1)(Some(tpe))),
    result,
    result.isEmpty
  )

  private def readMemory(method: String, tpe: Type, arguments: Vec[Term]): Term =
    access(method, tpe, arguments, Some(tpe))

  private def writeMemory(method: String, tpe: Type, arguments: Vec[Term]): Term = access(method, tpe, arguments, None)

  private def offset(address: Term, bytes: Long): Term =
    if (bytes == 0L) address else Binary("+", address, Literal(bytes))

  private def bytes(tpe: Type): Long = (numeric(tpe) + 7) / 8

  inline def unary(ids: OperandIds)(inline kernel: Term => Term): Option[Term.Steps] =
    lanewise(ids, 0)(index => kernel(lane(ids(0), index)))
  inline def binary(ids: OperandIds)(inline kernel: (Term, Term) => Term): Option[Term.Steps] =
    lanewise(ids, 0, 1)(index => kernel(lane(ids(0), index), lane(ids(1), index)))
  inline def ternary(ids: OperandIds)(inline kernel: (Term, Term, Term) => Term): Option[Term.Steps] =
    lanewise(ids, 0, 1, 2)(index => kernel(lane(ids(0), index), lane(ids(1), index), lane(ids(2), index)))
  inline def quaternary(ids: OperandIds)(inline kernel: (Term, Term, Term, Term) => Term): Option[Term.Steps] =
    lanewise(ids, 0, 1, 2, 3)(index =>
      kernel(lane(ids(0), index), lane(ids(1), index), lane(ids(2), index), lane(ids(3), index))
    )
  inline def broadcast(ids: OperandIds)(inline kernel: Term => Term): Option[Term.Steps]       = unary(ids)(kernel)
  inline def indexed(ids: OperandIds)(inline kernel: (Term, Term) => Term): Option[Term.Steps] =
    lanewise(ids, 0)(index => kernel(lane(ids(0), 0), Literal(index.toLong)))

  private def pairs(ids: OperandIds, count: Int)(first: LaneTerm, second: LaneTerm): Seq[(String, Term)] = {
    val all = components(ids.result)
    Seq.tabulate(count)(index => all(index).name -> first(index)) ++
      Seq.tabulate(count)(index => all(count + index).name -> second(index))
  }

  inline def structure(ids: OperandIds)(inline first: Term => Term, inline second: Term => Term): Option[Term.Steps] =
    rendered(
      ids,
      Seq(ids(0)),
      pairs(ids, lanesOf(ids(0)))(index => first(lane(ids(0), index)), index => second(lane(ids(0), index)))
    )
  inline def binaryStructure(ids: OperandIds)(
      inline first: (Term, Term) => Term,
      inline second: (Term, Term) => Term
  ): Option[Term.Steps] = rendered(
    ids,
    Seq(ids(0), ids(1)),
    pairs(ids, lanesOf(ids(0)))(
      index => first(lane(ids(0), index), lane(ids(1), index)),
      index => second(lane(ids(0), index), lane(ids(1), index))
    )
  )

  /** Stores of `values` through the pointer last among the operands, lane by lane. */
  private def stores(ids: OperandIds, values: LaneTerm): Seq[(String, Term)] = {
    val pointer = ids.operands.last
    val pointee = shape(pointer).collect { case Shape.Pointer(pointee) => pointee }.fold(Type.unsigned(32))(_.scalar)
    Seq.tabulate(lanesOf(ids(0)))(index =>
      Memory -> writeMemory(
        "store",
        pointee,
        Vec(offset(Variable(name(pointer)), index * bytes(pointee)), values(index))
      )
    )
  }

  inline def store(ids: OperandIds)(inline first: Term => Term, inline second: Term => Term): Option[Term.Steps] =
    rendered(
      ids,
      Seq(ids(0), ids.operands.last),
      Seq.tabulate(lanesOf(ids.result))(index => result(ids, index) -> first(lane(ids(0), index))) ++
        stores(ids, index => second(lane(ids(0), index)))
    )
  inline def binaryStore(ids: OperandIds)(inline first: (Term, Term) => Term, inline second: (Term, Term) => Term)
      : Option[Term.Steps] = rendered(
    ids,
    Seq(ids(0), ids(1), ids.operands.last),
    Seq.tabulate(lanesOf(ids.result))(index =>
      result(ids, index) -> first(lane(ids(0), index), lane(ids(1), index))
    ) ++ stores(ids, index => second(lane(ids(0), index), lane(ids(1), index)))
  )

  private def accumulator: Term = Variable(Accumulator)

  private def folded(ids: OperandIds, read: Seq[Int], initial: Term, step: LaneTerm, finish: Term): Option[Term.Steps] =
    rendered(
      ids,
      read,
      ((Accumulator -> initial) +: Seq.tabulate(lanesOf(ids(0)))(index => Accumulator -> step(index))) :+
        (result(ids, 0) -> finish)
    )

  inline def fold(ids: OperandIds)(inline initial: Term, inline step: (Term, Term) => Term, inline finish: Term => Term)
      : Option[Term.Steps] =
    folded(ids, Seq(ids(0)), initial, index => step(accumulator, lane(ids(0), index)), finish(accumulator))
  inline def binaryFold(ids: OperandIds)(
      inline initial: Term,
      inline step: (Term, Term, Term) => Term,
      inline finish: Term => Term
  ): Option[Term.Steps] = folded(
    ids,
    Seq(ids(0), ids(1)),
    initial,
    index => step(accumulator, lane(ids(0), index), lane(ids(1), index)),
    finish(accumulator)
  )
  inline def accumulatedFold(ids: OperandIds)(
      inline initial: Term,
      inline step: (Term, Term, Term) => Term,
      inline finish: (Term, Term) => Term
  ): Option[Term.Steps] = folded(
    ids,
    Seq(ids(0), ids(1), ids(2)),
    initial,
    index => step(accumulator, lane(ids(0), index), lane(ids(1), index)),
    finish(accumulator, lane(ids(2), 0))
  )

  /** Where each lane of a product reads its operands, as the execution sinks lay them out: rows, columns and inner
    * steps, then per step `k` of lane `row + rows * column` the left lane `row * leftRow + k * leftInner` and the right
    * lane `k * rightInner + column * rightColumn`.
    */
  private def layout(ids: OperandIds): (Int, Int, Int, Int, Int, Int, Int) = (shape(ids(0)), shape(ids(1))) match {
    case (Some(Shape.Matrix(_, rows, inner)), Some(Shape.Matrix(_, _, columns))) =>
      (rows, columns, inner, 1, rows, 1, inner)
    case (Some(Shape.Matrix(_, rows, columns)), _) => (rows, 1, columns, 1, rows, 1, 0)
    case (_, Some(Shape.Matrix(_, rows, columns))) => (1, columns, rows, 0, 1, 1, rows)
    case _                                         => (0, 0, 0, 0, 0, 0, 0)
  }

  inline def product(ids: OperandIds)(inline step: (Term, Term, Term) => Term): Option[Term.Steps] = {
    val (rows, columns, inner, leftRow, leftInner, rightInner, rightColumn) = layout(ids)
    rendered(
      ids,
      Seq(ids(0), ids(1)),
      Seq.tabulate(rows * columns) { index =>
        val (row, column) = (index % rows, index / rows)
        ((Accumulator -> Literal(0L)) +: Seq.tabulate(inner)(k =>
          Accumulator -> step(
            accumulator,
            lane(ids(0), row * leftRow + k * leftInner),
            lane(ids(1), k * rightInner + column * rightColumn)
          )
        )) :+ (result(ids, index) -> accumulator)
      }.flatten
    )
  }

  inline def matrixAccumulate(ids: OperandIds)(
      inline initial: Term,
      inline step: (Term, Term, Term) => Term,
      inline finish: (Term, Term) => Term
  ): Option[Term.Steps] = {
    val (rows, inner) = shape(ids(0)).collect { case Shape.Cooperative(_, height, width, _) => (height, width) }
      .getOrElse((0, 0))
    val columns = shape(ids(1)).collect { case Shape.Cooperative(columns = width) => width }.getOrElse(0)
    rendered(
      ids,
      Seq(ids(0), ids(1), ids(2)),
      Seq.tabulate(rows * columns) { index =>
        val (row, column) = (index / columns, index % columns)
        ((Accumulator -> initial) +: Seq.tabulate(inner)(k =>
          Accumulator -> step(accumulator, lane(ids(0), row * inner + k), lane(ids(1), k * columns + column))
        )) :+ (result(ids, index) -> finish(accumulator, lane(ids(2), index)))
      }.flatten
    )
  }

  inline def outerProduct(ids: OperandIds)(inline kernel: (Term, Term) => Term): Option[Term.Steps] = {
    val rows = lanesOf(ids(0))
    lanewise(ids, 0, 1)(index => kernel(lane(ids(0), index % rows), lane(ids(1), index / rows)))
  }

  inline def cross(ids: OperandIds)(inline kernel: (Term, Term, Term, Term) => Term): Option[Term.Steps] =
    lanewise(ids, 0, 1) { index =>
      val (next, after) = ((index + 1) % 3, (index + 2) % 3)
      if (index < 3) kernel(lane(ids(0), next), lane(ids(1), after), lane(ids(0), after), lane(ids(1), next))
      else Literal(0L)
    }

  inline def projection(ids: OperandIds, dotted: Int)(
      inline initial: Term,
      inline step: (Term, Term, Term) => Term,
      inline element: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = {
    val scalar = if (ids.operands.size > 2) lane(ids(2), 0) else Literal(0L)
    rendered(
      ids,
      ids.operands,
      ((Accumulator -> initial) +: Seq.tabulate(lanesOf(ids(dotted)))(index =>
        Accumulator -> step(accumulator, lane(ids(dotted), index), lane(ids(1), index))
      )) ++ Seq.tabulate(lanesOf(ids(0)))(index =>
        result(ids, index) -> element(accumulator, lane(ids(0), index), lane(ids(1), index), scalar)
      )
    )
  }

  inline def normalised(ids: OperandIds)(
      inline scale: (Term, Term) => Term,
      inline norm: (Term, Term, Term) => Term,
      inline element: (Term, Term, Term) => Term
  ): Option[Term.Steps] = {
    val lanes = lanesOf(ids(0))
    rendered(
      ids,
      Seq(ids(0)),
      ((Scale -> Literal(0L)) +: Seq.tabulate(lanes)(index => Scale -> scale(Variable(Scale), lane(ids(0), index)))) ++
        ((Norm -> Literal(0L)) +: Seq.tabulate(lanes)(index =>
          Norm -> norm(Variable(Norm), lane(ids(0), index), Variable(Scale))
        )) ++ Seq.tabulate(lanes)(index =>
          result(ids, index) -> element(lane(ids(0), index), Variable(Scale), Variable(Norm))
        )
    )
  }

  // a kernel's fault where the field leaves the width is not part of its result
  inline def bitFieldExtract(ids: OperandIds)(
      inline inRange: (Term, Term) => Term,
      inline kernel: (Term, Term, Term) => Term
  ): Option[Term.Steps] = ternary(ids)(kernel)
  inline def bitFieldInsert(ids: OperandIds)(
      inline inRange: (Term, Term) => Term,
      inline kernel: (Term, Term, Term, Term) => Term
  ): Option[Term.Steps] = quaternary(ids)(kernel)
  inline def unsignedConversion(ids: OperandIds)(inline representable: Term => Term, inline kernel: Term => Term)
      : Option[Term.Steps] = unary(ids)(kernel)

  /** An atomic read-modify-write: the result is the value the pointer held, and the store is its update. */
  private def modify(ids: OperandIds, read: Seq[Int], update: Term): Option[Term.Steps] = {
    val pointer = Variable(name(ids(0)))
    val tpe     = components(ids.result).head.tpe
    rendered(
      ids,
      ids(0) +: read,
      Seq(
        result(ids, 0) -> readMemory("atomicLoad", tpe, Vec(pointer)),
        Memory         -> writeMemory("atomicStore", tpe, Vec(pointer, update))
      )
    )
  }

  inline def atomic(ids: OperandIds, anyStorage: Boolean)(inline kernel: (Term, Term) => Term): Option[Term.Steps] =
    modify(ids, Seq(ids(3)), kernel(Variable(result(ids, 0)), lane(ids(3), 0)))
  inline def atomicUpdate(ids: OperandIds)(inline kernel: Term => Term): Option[Term.Steps] =
    modify(ids, Seq.empty, kernel(Variable(result(ids, 0))))
  inline def compareExchange(ids: OperandIds)(inline kernel: (Term, Term, Term) => Term): Option[Term.Steps] =
    modify(ids, Seq(ids(4), ids(5)), kernel(Variable(result(ids, 0)), lane(ids(4), 0), lane(ids(5), 0)))
  inline def addresses(ids: OperandIds)(inline kernel: (Term, Term) => Term): Option[Term.Steps] = binary(ids)(kernel)

  /** The stride is the size of the pointee, a constant of the instruction. */
  inline def addressDifference(ids: OperandIds)(inline kernel: (Term, Term, Term) => Term): Option[Term.Steps] = {
    val stride = shape(ids(0)).collect { case Shape.Pointer(pointee) => pointee }
      .fold(1L)(pointee => pointee.components("").map(component => bytes(component.tpe)).sum)
    lanewise(ids, 0, 1)(index => kernel(lane(ids(0), index), lane(ids(1), index), Literal(stride)))
  }

  /** The address of half `index` from offset and pointer, `stride` halves apart per offset unit. */
  private def half(offset: Int, pointer: Int, stride: Int, index: Int): Term = this.offset(
    Binary("+", Variable(name(pointer)), Binary("*", lane(offset, 0), Literal(stride * 2L))),
    index * 2L
  )

  private def stride(aligned: Boolean, count: Int): Int = if (aligned && count == 3) 4 else count

  inline def halfLoad(ids: OperandIds, aligned: Boolean)(inline convert: Term => Term): Option[Term.Steps] = {
    val count = lanesOf(ids.result)
    lanewise(ids, 0, 1)(index =>
      convert(readMemory("load", Type.Float(16), Vec(half(ids(0), ids(1), stride(aligned, count), index))))
    )
  }

  inline def halfStore(ids: OperandIds, aligned: Boolean)(inline convert: Term => Term): Option[Term.Steps] = {
    val count = lanesOf(ids(0))
    rendered(
      ids,
      Seq(ids(0), ids(1), ids(2)),
      Seq.tabulate(count)(index =>
        Memory -> writeMemory(
          "store",
          Type.Float(16),
          Vec(half(ids(1), ids(2), stride(aligned, count), index), convert(lane(ids(0), index)))
        )
      )
    )
  }
}
