package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.InstructionOperand.*

object FormDecoder {
  def apply(definition: Definition)(build: () => Form): FormDecoder =
    new FormDecoder(definition, Vec.empty, _ => build())

  def apply[A <: Operand](definition: Definition, a: Kind[A])(build: A => Form): FormDecoder =
    new FormDecoder(definition, Vec(a), operands => build(a.unsafe(operands(0))))

  def apply[A <: Operand, B <: Operand](definition: Definition, a: Kind[A], b: Kind[B])(
      build: (A, B) => Form
  ): FormDecoder =
    new FormDecoder(definition, Vec(a, b), operands => build(a.unsafe(operands(0)), b.unsafe(operands(1))))

  def apply[A <: Operand, B <: Operand, C <: Operand](definition: Definition, a: Kind[A], b: Kind[B], c: Kind[C])(
      build: (A, B, C) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c),
      operands => build(a.unsafe(operands(0)), b.unsafe(operands(1)), c.unsafe(operands(2)))
    )

  def apply[A <: Operand, B <: Operand, C <: Operand, D <: Operand](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D]
  )(
      build: (A, B, C, D) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3))
        )
    )

  def apply[A <: Operand, B <: Operand, C <: Operand, D <: Operand, E <: Operand](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D],
      e: Kind[E]
  )(
      build: (A, B, C, D, E) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d, e),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3)),
          e.unsafe(operands(4))
        )
    )

  def apply[A <: Operand, B <: Operand, C <: Operand, D <: Operand, E <: Operand, F <: Operand](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D],
      e: Kind[E],
      f: Kind[F]
  )(
      build: (A, B, C, D, E, F) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d, e, f),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3)),
          e.unsafe(operands(4)),
          f.unsafe(operands(5))
        )
    )

  def apply[A <: Operand, B <: Operand, C <: Operand, D <: Operand, E <: Operand, F <: Operand, G <: Operand](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D],
      e: Kind[E],
      f: Kind[F],
      g: Kind[G]
  )(
      build: (A, B, C, D, E, F, G) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d, e, f, g),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3)),
          e.unsafe(operands(4)),
          f.unsafe(operands(5)),
          g.unsafe(operands(6))
        )
    )

  def apply[
      A <: Operand,
      B <: Operand,
      C <: Operand,
      D <: Operand,
      E <: Operand,
      F <: Operand,
      G <: Operand,
      H <: Operand
  ](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D],
      e: Kind[E],
      f: Kind[F],
      g: Kind[G],
      h: Kind[H]
  )(
      build: (A, B, C, D, E, F, G, H) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d, e, f, g, h),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3)),
          e.unsafe(operands(4)),
          f.unsafe(operands(5)),
          g.unsafe(operands(6)),
          h.unsafe(operands(7))
        )
    )

  def apply[
      A <: Operand,
      B <: Operand,
      C <: Operand,
      D <: Operand,
      E <: Operand,
      F <: Operand,
      G <: Operand,
      H <: Operand,
      I <: Operand
  ](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D],
      e: Kind[E],
      f: Kind[F],
      g: Kind[G],
      h: Kind[H],
      i: Kind[I]
  )(
      build: (A, B, C, D, E, F, G, H, I) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d, e, f, g, h, i),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3)),
          e.unsafe(operands(4)),
          f.unsafe(operands(5)),
          g.unsafe(operands(6)),
          h.unsafe(operands(7)),
          i.unsafe(operands(8))
        )
    )

  def apply[
      A <: Operand,
      B <: Operand,
      C <: Operand,
      D <: Operand,
      E <: Operand,
      F <: Operand,
      G <: Operand,
      H <: Operand,
      I <: Operand,
      J <: Operand
  ](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D],
      e: Kind[E],
      f: Kind[F],
      g: Kind[G],
      h: Kind[H],
      i: Kind[I],
      j: Kind[J]
  )(
      build: (A, B, C, D, E, F, G, H, I, J) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d, e, f, g, h, i, j),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3)),
          e.unsafe(operands(4)),
          f.unsafe(operands(5)),
          g.unsafe(operands(6)),
          h.unsafe(operands(7)),
          i.unsafe(operands(8)),
          j.unsafe(operands(9))
        )
    )

  def apply[
      A <: Operand,
      B <: Operand,
      C <: Operand,
      D <: Operand,
      E <: Operand,
      F <: Operand,
      G <: Operand,
      H <: Operand,
      I <: Operand,
      J <: Operand,
      K <: Operand
  ](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D],
      e: Kind[E],
      f: Kind[F],
      g: Kind[G],
      h: Kind[H],
      i: Kind[I],
      j: Kind[J],
      k: Kind[K]
  )(
      build: (A, B, C, D, E, F, G, H, I, J, K) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d, e, f, g, h, i, j, k),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3)),
          e.unsafe(operands(4)),
          f.unsafe(operands(5)),
          g.unsafe(operands(6)),
          h.unsafe(operands(7)),
          i.unsafe(operands(8)),
          j.unsafe(operands(9)),
          k.unsafe(operands(10))
        )
    )

  def apply[
      A <: Operand,
      B <: Operand,
      C <: Operand,
      D <: Operand,
      E <: Operand,
      F <: Operand,
      G <: Operand,
      H <: Operand,
      I <: Operand,
      J <: Operand,
      K <: Operand,
      L <: Operand
  ](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D],
      e: Kind[E],
      f: Kind[F],
      g: Kind[G],
      h: Kind[H],
      i: Kind[I],
      j: Kind[J],
      k: Kind[K],
      l: Kind[L]
  )(
      build: (A, B, C, D, E, F, G, H, I, J, K, L) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d, e, f, g, h, i, j, k, l),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3)),
          e.unsafe(operands(4)),
          f.unsafe(operands(5)),
          g.unsafe(operands(6)),
          h.unsafe(operands(7)),
          i.unsafe(operands(8)),
          j.unsafe(operands(9)),
          k.unsafe(operands(10)),
          l.unsafe(operands(11))
        )
    )

  def apply[
      A <: Operand,
      B <: Operand,
      C <: Operand,
      D <: Operand,
      E <: Operand,
      F <: Operand,
      G <: Operand,
      H <: Operand,
      I <: Operand,
      J <: Operand,
      K <: Operand,
      L <: Operand,
      M <: Operand
  ](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D],
      e: Kind[E],
      f: Kind[F],
      g: Kind[G],
      h: Kind[H],
      i: Kind[I],
      j: Kind[J],
      k: Kind[K],
      l: Kind[L],
      m: Kind[M]
  )(
      build: (A, B, C, D, E, F, G, H, I, J, K, L, M) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d, e, f, g, h, i, j, k, l, m),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3)),
          e.unsafe(operands(4)),
          f.unsafe(operands(5)),
          g.unsafe(operands(6)),
          h.unsafe(operands(7)),
          i.unsafe(operands(8)),
          j.unsafe(operands(9)),
          k.unsafe(operands(10)),
          l.unsafe(operands(11)),
          m.unsafe(operands(12))
        )
    )

  def apply[
      A <: Operand,
      B <: Operand,
      C <: Operand,
      D <: Operand,
      E <: Operand,
      F <: Operand,
      G <: Operand,
      H <: Operand,
      I <: Operand,
      J <: Operand,
      K <: Operand,
      L <: Operand,
      M <: Operand,
      N <: Operand
  ](
      definition: Definition,
      a: Kind[A],
      b: Kind[B],
      c: Kind[C],
      d: Kind[D],
      e: Kind[E],
      f: Kind[F],
      g: Kind[G],
      h: Kind[H],
      i: Kind[I],
      j: Kind[J],
      k: Kind[K],
      l: Kind[L],
      m: Kind[M],
      n: Kind[N]
  )(
      build: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) => Form
  ): FormDecoder =
    new FormDecoder(
      definition,
      Vec(a, b, c, d, e, f, g, h, i, j, k, l, m, n),
      operands =>
        build(
          a.unsafe(operands(0)),
          b.unsafe(operands(1)),
          c.unsafe(operands(2)),
          d.unsafe(operands(3)),
          e.unsafe(operands(4)),
          f.unsafe(operands(5)),
          g.unsafe(operands(6)),
          h.unsafe(operands(7)),
          i.unsafe(operands(8)),
          j.unsafe(operands(9)),
          k.unsafe(operands(10)),
          l.unsafe(operands(11)),
          m.unsafe(operands(12)),
          n.unsafe(operands(13))
        )
    )
}

/** Decodes one typed logical form from operands that match its operand kinds in order. */
class FormDecoder private (
    val definition: Definition,
    kinds: Vec[Kind[?]],
    build: Vec[Operand] => Form
) {
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def decode(operands: Vec[Operand]): Option[Form] =
    if (operands.size != kinds.size) None
    else {
      var index = 0
      while (index < kinds.size && kinds(index).admits(operands(index))) index += 1
      if (index == kinds.size) Some(build(operands)) else None
    }
}
