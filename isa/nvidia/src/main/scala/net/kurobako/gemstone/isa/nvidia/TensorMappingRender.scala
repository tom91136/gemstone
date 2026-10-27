package net.kurobako.gemstone.isa.nvidia

import TensorMapping.*
import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.RenderMachine.*
import scala.collection.mutable

/** The runs of a tensor copy rendered as the loops that visit them: the tensor-map fields the mapping reads, the rows
  * of the box with their base addresses, and the elements of each row with their global addresses and the test that
  * says whether they lie inside the tensor, all from the bodies of [[TensorMapping]] that execution evaluates.
  */
private[isa] object TensorMappingRender {

  /** The tensor map `map` that a copy reads, the transfer prepared from it, and the coordinates and offsets of the
    * copy.
    */
  case class Transfer(map: Term, coordinates: Vec[Term], offsets: Vec[Term], copy: Instruction.AsyncTensor)

  private def fact(tensors: (contextType: String, context: String), transfer: Term, fact: TensorLayout.Fact): Term =
    Term.context(
      tensors,
      MachineCall(
        "fact",
        Vec.empty,
        Vec((transfer, None), (Term.Constant(s"net.kurobako.gemstone.isa.nvidia.TensorLayout.Fact.$fact"), None))
      ),
      Some(Word)
    )

  private def indices(tensors: (contextType: String, context: String), count: Term): Term =
    Term.context(tensors, MachineCall("indices", Vec.empty, Vec((count, Some(Word)))), None)

  /** The field `kind` at `axis` of the tensor map `map`, built from the words of the map that `tensors` reads. */
  def field(tensors: (contextType: String, context: String), map: Term, kind: TensorMapping.Field, axis: Int): Term = {
    val source = Vec((map, None))
    TensorMap.field(
      Render,
      (at, width) =>
        Term.context(
          tensors,
          MachineCall(
            "word",
            Vec.empty,
            source ++ Vec((Term.Constant(at.toString), None), (Term.Constant(width.toString), None))
          ),
          Some(Word)
        ),
      kind,
      axis
    )
  }

  /** `each` over the run `run` of every element of the box that `described` copies, as nested loops over its rows and
    * their elements.
    */
  def runs(names: Names, tensors: (contextType: String, context: String), transfer: Term, described: Transfer)(
      each: Term => Term.Steps
  ): Term.Steps = {
    val copy     = described.copy
    val prelude  = Vec.newBuilder[Term.Step]
    val rowSteps = Vec.newBuilder[Term.Step]
    val colSteps = Vec.newBuilder[Term.Step]

    def binder(into: mutable.Builder[Term.Step, Vec[Term.Step]])(value: Term): Term = value match {
      case Term.Variable(_) | Term.Literal(_) => value
      case _                                  =>
        val name = names.temporary()
        names.keep(name)
        into += Term.Step(name, value)
        Term.Variable(name)
    }
    val outer  = binder(prelude)
    val inRow  = binder(rowSteps)
    val inCell = binder(colSteps)

    val rank                                              = if (copy.gather) 2 else described.coordinates.size
    val values                                            = outer(fact(tensors, transfer, TensorLayout.Fact.Values))
    val fields                                            = mutable.HashMap.empty[(TensorMapping.Field, Int), Term]
    def field(kind: TensorMapping.Field, axis: Int): Term =
      fields.getOrElseUpdate((kind, axis), outer(TensorMappingRender.field(tensors, described.map, kind, axis)))
    def units(value: Term): Term      = outer(Render.counted(value, values))
    lazy val dimension0               = units(field(TensorMapping.Field.Dimension, 0))
    def dimension(axis: Int): Term    = if (axis == 0) dimension0 else field(TensorMapping.Field.Dimension, axis)
    def stride(axis: Int): Term       = field(TensorMapping.Field.Stride, axis)
    def traversal(axis: Int): Term    = field(TensorMapping.Field.Traversal, axis)
    val tensorAddress                 = field(TensorMapping.Field.Address, 0)
    val global                        = outer(fact(tensors, transfer, TensorLayout.Fact.Global))
    val local                         = outer(fact(tensors, transfer, TensorLayout.Fact.Local))
    val start                         = units(described.coordinates(0))
    def coordinateAt(axis: Int): Term = if (axis == 0) start else described.coordinates(axis)

    def cells(row: Term, columns: Term, base: Term, rowInside: Term, step: Term): Term.Steps = {
      val loop      = names.temporary()
      val column    = Term.Variable(loop)
      val position  = inCell(Render.coordinate(start, column, step))
      val within    = inCell(Render.inside(rowInside, position, dimension(0)))
      val boxOffset = inCell(Render.offset(row, columns, column, local))
      val at        = inCell(Render.address(base, position, global))
      val steps     = Term.Steps(colSteps.result())
      colSteps.clear()
      RenderMachine.each(
        loop,
        indices(tensors, columns),
        steps ++ each(Term.Call("run", Vec(boxOffset, at, Term.Literal(1L), within)))
      )
    }

    val loop = if (copy.im2col) {
      val mode    = outer(fact(tensors, transfer, TensorLayout.Fact.Mode))
      val wide    = outer(Render.lessOrEqual(Term.Literal(2L), mode, signed = true))
      val wide128 = outer(Render.equal(mode, Term.Literal(3L)))
      val first   = described.offsets.headOption.getOrElse(Term.Literal(0L))
      val second  = described.offsets.lift(1).getOrElse(Term.Literal(0L))
      val extent  = TensorMapping.shape(Render, wide, wide128, field(TensorMapping.Field.Pixels, 0), first, second)(
        (pixels, halos, shift) => Vec(outer(pixels), outer(halos), outer(shift))
      )
      val channels = units(field(TensorMapping.Field.Channels, 0))
      val loop     = names.temporary()
      val pixel    = Term.Variable(loop)
      TensorMapping.pixelRow(
        Render,
        rank,
        wide,
        wide128,
        pixel,
        extent(0),
        extent(2),
        tensorAddress,
        coordinateAt(_),
        dimension(_),
        stride(_),
        traversal(_),
        field(TensorMapping.Field.Lower, _),
        field(TensorMapping.Field.Upper, _),
        axis => described.offsets.lift(axis).getOrElse(Term.Literal(0L)),
        inRow
      ) { (base, inside) =>
        val body = Term.Steps(rowSteps.result()) ++ cells(pixel, channels, base, inside, Term.Literal(1L))
        RenderMachine.each(loop, indices(tensors, Render.add(extent(0), extent(1))), body)
      }
    } else {
      val memo                    = mutable.HashMap.empty[Int, Term]
      def counts(axis: Int): Term = memo.getOrElseUpdate(
        axis,
        outer(Render.elements(
          if (axis == 0) units(field(TensorMapping.Field.Box, 0)) else field(TensorMapping.Field.Box, axis),
          traversal(axis)
        ))
      )
      val rows = outer(TensorMapping.rows(Render, copy.gather, rank, counts(_)))
      val loop = names.temporary()
      val row  = Term.Variable(loop)
      TensorMapping.tileRow(
        Render,
        copy.gather,
        rank,
        row,
        tensorAddress,
        coordinateAt(_),
        dimension(_),
        stride(_),
        traversal(_),
        counts(_),
        inRow
      ) { (base, inside) =>
        val body = Term.Steps(rowSteps.result()) ++ cells(row, counts(0), base, inside, traversal(0))
        RenderMachine.each(loop, indices(tensors, rows), body)
      }
    }
    Term.Steps(prelude.result()) ++ loop
  }
}
