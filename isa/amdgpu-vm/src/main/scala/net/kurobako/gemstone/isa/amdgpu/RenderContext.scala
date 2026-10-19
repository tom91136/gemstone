package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*

private object RenderContext {
  // implicit state, spelt as the assembler spells it
  val Scc  = "scc"
  val Vcc  = "vcc"
  val Exec = "exec"
  val M0   = "m0"
  val Mode = "mode"

  /** A term chosen by an index, without boxing it. */
  @FunctionalInterface
  trait IndexedTerm { def apply(index: Int): Term }

  val Wave: (contextType: String, context: String) = ("net.kurobako.gemstone.isa.amdgpu.Wave", "wave")

  /** A memory a lane reaches, as the context that renders it, with the type of its addresses. */
  enum Space(val context: String, val name: String, val address: Type) derives CanEqual {
    case Global  extends Space("net.kurobako.gemstone.isa.amdgpu.GlobalMemory", "global", Type.unsigned(64))
    case Flat    extends Space("net.kurobako.gemstone.isa.amdgpu.FlatMemory", "flat", Type.unsigned(64))
    case Local   extends Space("net.kurobako.gemstone.isa.amdgpu.LocalMemory", "lds", Type.unsigned(32))
    case Private extends Space("net.kurobako.gemstone.isa.amdgpu.PrivateMemory", "scratch", Type.unsigned(64))
  }

  private val Dword = Type.unsigned(32)
  private val Pair  = Type.unsigned(64)

  val Lane: Term     = Term.context(Wave, "lane", Vec.empty, Vec.empty, Some(Dword))
  val ExecMask: Term = Term.context(Wave, "exec", Vec.empty, Vec.empty, Some(Pair))

  private def width(bytes: Int): Type = Type.unsigned((bytes * 8).unsafe[Int in 1 ~ 64])

  /** `bytes` from `at` in `space`. */
  def load(space: Space, at: Term, bytes: Int): Term =
    Term.context(
      (space.context, space.name),
      s"load${bytes * 8}",
      Vec.empty,
      Vec((at, Some(space.address))),
      Some(width(bytes))
    )

  def store(space: Space, at: Term, value: Term, bytes: Int): Term = Term.context(
    (space.context, space.name),
    s"store${bytes * 8}",
    Vec.empty,
    Vec((at, Some(space.address)), (value, Some(width(bytes)))),
    None,
    effect = true
  )

  /** An assembly spelling, such as `s1`, `v[2:3]`, `0x1234` or `scc`, and the type its operand is read at. */
  case class Spelling(text: String, tpe: Type)

  /** Names each spelling as `spellings` gives it or else as itself. */
  private class Spelled(spellings: String => Option[String]) extends Naming[Spelling] {
    def resolve(spelling: Spelling): Vec[Naming.Name] =
      Vec(Naming.Name.spelt(spellings(spelling.text).getOrElse(spelling.text), spelling.tpe))
  }

  /** Names and types what an instruction reads and writes. A register, register range, literal or piece of implicit
    * state takes the name `spellings` gives its assembly spelling, such as `s1`, `v[2:3]`, `0x1234` or `scc`, or that
    * spelling. A lane mask, VCC and EXEC included, is `waveSize` bits, and SCC is a truth value. M0 and the null
    * register are the SGPRs `isa` encodes them as, GFX9's without one, and buffer and image accesses render only with
    * `isa`.
    */
  class Names(spellings: String => Option[String], val waveSize: Int in (32, 64), val isa: Option[ISA] = None)
      extends Scope[Spelling](Spelled(spellings)) {
    private val m0Index   = m0Register(isa.getOrElse(ISA.GFX9)).raw
    private val discarded = nullRegister(isa.getOrElse(ISA.GFX9)).fold(-1)(_.raw)

    /** Whether writes to the SGPR `index` are dropped. */
    def discards(index: ScalarRegisterIndex): Boolean = index.raw == discarded

    val mask: Type = Type.unsigned(waveSize.widen[Int in 1 ~ 64])

    def named(spelling: String, tpe: Type): Term = Term.Variable(operands(Spelling(spelling, tpe)).head.name)

    /** The name a write to `term`, as this names a register or state, assigns. */
    def name(term: Term): String = term match {
      case Term.Variable(value) => value
      case other                => other.show
    }

    def scc: Term  = named(Scc, Type.Bool)
    def vcc: Term  = named(Vcc, mask)
    def exec: Term = named(Exec, mask)
    def m0: Term   = named(M0, Dword)
    def mode: Term = named(Mode, Dword)

    /** A scalar register read or written as a dword. */
    def scalar(index: ScalarRegisterIndex): Term = index.raw match {
      case Fixed.VccLo if waveSize == 32     => vcc
      case Fixed.ExecLo if waveSize == 32    => exec
      case Fixed.VccLo                       => Render.truncate(vcc, 32)
      case Fixed.ExecLo                      => Render.truncate(exec, 32)
      case Fixed.VccHi if waveSize == 64     => Render.shiftRightLogical(vcc, Term.Literal(32L))
      case Fixed.ExecHi if waveSize == 64    => Render.shiftRightLogical(exec, Term.Literal(32L))
      case Fixed.VccHi                       => named("vcc_hi", Dword)
      case Fixed.ExecHi                      => named("exec_hi", Dword)
      case register if register == m0Index   => m0
      case register if register == discarded => Term.Literal(0L)
      case other                             => named(s"s$other", Dword)
    }

    /** A write of a dword to the SGPR `index`: one to half of a wave64 lane mask keeps the other half, and one to the
      * null register goes nowhere.
      */
    def scalarWrite(index: ScalarRegisterIndex): Term => Term.Step = index.raw match {
      case Fixed.VccLo | Fixed.ExecLo if waveSize == 64 =>
        val whole = if (index.raw == Fixed.VccLo) vcc else exec
        value =>
          Term.Step(
            name(whole),
            Render.or(Render.and(whole, Term.Literal(0xffffffff00000000L)), Render.truncate(value, 32))
          )
      case Fixed.VccHi | Fixed.ExecHi if waveSize == 64 =>
        val whole = if (index.raw == Fixed.VccHi) vcc else exec
        value =>
          Term.Step(
            name(whole),
            Render.or(
              Render.and(whole, Term.Literal(0xffffffffL)),
              Render.shiftLeft(Render.truncate(value, 32), Term.Literal(32L))
            )
          )
      case register if register == discarded => value => Term.Step("_", value)
      case _                                 =>
        val target = name(scalar(index))
        value => Term.Step(target, value)
    }

    def scalarPair(index: ScalarRegisterIndex): Term = index.raw match {
      case Fixed.VccLo  => vcc
      case Fixed.ExecLo => exec
      case other        => named(s"s[$other:${other + 1}]", Pair)
    }

    /** A scalar lane mask: a register pair in a wave64, a dword in a wave32. */
    def scalarMask(index: ScalarRegisterIndex): Term = index.raw match {
      case Fixed.VccLo             => vcc
      case Fixed.ExecLo            => exec
      case other if waveSize == 32 => named(s"s$other", Dword)
      case other                   => named(s"s[$other:${other + 1}]", Pair)
    }

    def vector(index: VectorRegisterIndex): Term      = named(s"v$index", Dword)
    def vectorPair(index: VectorRegisterIndex): Term  = named(s"v[$index:${index + 1}]", Pair)
    def accumulator(index: VectorRegisterIndex): Term = named(s"a$index", Dword)

    def literal(value: Long): Term = named(s"0x${java.lang.Long.toHexString(Bits.low32(value).toLong)}", Dword)
  }
}
