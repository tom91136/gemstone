package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.string.*

object DomainTable {
  private val prefixes = Map(
    RegisterFile.Scalar         -> "s",
    RegisterFile.TrapTemporary  -> "ttmp",
    RegisterFile.VectorRegister -> "v",
    RegisterFile.Accumulator    -> "acc"
  )

  case class Alignment(file: RegisterFile, fromBits: Int, alignment: Int)

  def prefix(file: RegisterFile): String = prefixes(file)
}

/** One operand domain on one ISA family: its runs, and the alignment its registers need at each width. */
class DomainTable(val id: String, val runs: Vector[Run], rules: Vector[DomainTable.Alignment]) {
  private val names: Map[String, Int] = runs.collect { case Run.Named(name, code) => name -> code }.toMap

  /** Names spelled by their low half, such as `vcc` for `vcc_lo` and `vcc_hi`, with the code of the low half. */
  private val pairs: Map[String, Int] = names.collect {
    case (name, code) if name.endsWith("_lo") && names.get(name.stripSuffix("_lo") + "_hi").contains(code + 1) =>
      name.stripSuffix("_lo") -> code
  }

  private val spellings: Map[Int, String] = names.map(_.swap)

  val immediate: Option[Run.Immediate] = runs.collectFirst { case run: Run.Immediate => run }

  def has(file: RegisterFile): Boolean = runs.exists {
    case Run.Registers(file = found) => found == file
    case _                           => false
  }

  def hasLiteral(bytes: Int): Boolean = runs.exists {
    case Run.Literal(found, _) => found == bytes
    case _                     => false
  }

  def hasIntegers: Boolean = runs.exists { case Run.Integers(_, _, _) => true; case _ => false }

  def hasFloats: Boolean = runs.exists { case Run.Floats(_, _) => true; case _ => false }

  /** Whether the domain holds inline constants or a trailing literal rather than registers and names alone. */
  def constants: Boolean = hasIntegers || hasFloats || hasLiteral(4) || hasLiteral(8)

  /** The alignment of a `bits`-wide operand in `file`: the largest rule whose width the operand reaches. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def alignment(file: RegisterFile, bits: Int): Int = {
    val steps = alignmentSteps.getOrElse(file, Vector.empty)
    var found = 1
    var i     = 0
    while (i < steps.size && steps(i)._1 <= bits) {
      found = steps(i)._2
      i += 1
    }
    found
  }

  private val alignmentSteps: Map[RegisterFile, Vector[(Int, Int)]] =
    rules.groupBy(_.file).view.mapValues { fileRules =>
      fileRules.sortBy(_.fromBits).scanLeft((0, 1))((step, rule) => (rule.fromBits, step._2.max(rule.alignment))).tail
    }.toMap

  /** The code of the first register of a `bits`-wide operand, all its registers in one run and the first legal. */
  def registers(file: RegisterFile, first: Int, bits: Int): Option[Int] = {
    val count = math.max(bits, 32) / 32
    Option.when(first >= 0 && first % alignment(file, bits) == 0)(runs.collectFirst {
      case Run.Registers(found, low, high, code) if found == file && first >= low && first + count - 1 <= high =>
        code + first - low
    }).flatten
  }

  /** The register file and index of the first register of a `bits`-wide operand starting at `code`. */
  def registersAt(code: Int, bits: Int): Option[(RegisterFile, Int)] = {
    val count = math.max(bits, 32) / 32
    runs.collectFirst {
      case Run.Registers(file, low, high, base)
          if code >= base && code + count - 1 <= base + high - low &&
            (code - base + low) % alignment(file, bits) == 0 =>
        file                   -> (code - base + low)
    }
  }

  /** Names LLVM spells at any width; `src_lds_direct` is one dword. */
  private def widthless(name: String): Boolean = name == "null" || (name.startsWith("src_") && name != "src_lds_direct")

  /** The code of `name`, a pair spelling when `bits` is 64; widthless names take any width. */
  def code(name: String, bits: Int): Option[Int] = {
    val width = math.max(bits, 32)
    if (widthless(name)) names.get(name).orElse(pairs.get(name))
    else if (width == 64) pairs.get(name)
    else if (width == 32) names.get(name)
    else None
  }

  /** The spelling of `code` for a `bits`-wide operand: the pair spelling for 64 bits, the half otherwise. */
  def name(code: Int, bits: Int): Option[String] = {
    val width = math.max(bits, 32)
    spellings.get(code).flatMap { name =>
      if (widthless(name) || width == 32) Some(name)
      else if (width == 64) Option.when(name.endsWith("_lo"))(name.stripSuffix("_lo")).filter(pairs.contains)
      else None
    }
  }

  def encode(slot: Slot): Option[BigInt] = slot match {
    case Slot.Register(file, index)   => registers(file, index, 32).map(BigInt(_))
    case Slot.Named(name)             => names.get(name).map(BigInt(_))
    case Slot.Numbered(prefix, index) =>
      runs.collectFirst {
        case Run.Numbered(found, low, high, code) if found == prefix && index >= low && index <= high =>
          BigInt(code + index - low)
      }
    case Slot.Integer(value) =>
      runs.collectFirst {
        case Run.Integers(first, last, code) if value >= first.min(last) && value <= first.max(last) =>
          BigInt(code + math.abs(value - first))
      }
    case Slot.Floating(value)  => runs.collectFirst { case Run.Floats(found, code) if found == value => BigInt(code) }
    case Slot.Literal(bytes)   => runs.collectFirst { case Run.Literal(found, code) if found == bytes => BigInt(code) }
    case Slot.Immediate(value) =>
      immediate.filter(run => value >= run.min && value <= run.max).map(run => value & ((BigInt(1) << run.bits) - 1))
  }

  /** The value at `code`, leaving immediates to `decodeImmediate`. */
  def decode(code: Long): Option[Slot] = runs.collectFirst(Function.unlift {
    case Run.Registers(file, low, high, base) if code >= base && code <= base + high - low =>
      Some(Slot.Register(file, (code - base).toInt + low))
    case Run.Named(name, found) if found == code                                            => Some(Slot.Named(name))
    case Run.Numbered(prefix, low, high, base) if code >= base && code <= base + high - low =>
      Some(Slot.Numbered(prefix, (code - base).toInt + low))
    case Run.Integers(first, last, base) if code >= base && code <= base + math.abs(last - first) =>
      Some(Slot.Integer(first + (if (last >= first) 1 else -1) * (code - base).toInt))
    case Run.Floats(value, found) if found == code  => Some(Slot.Floating(value))
    case Run.Literal(bytes, found) if found == code => Some(Slot.Literal(bytes))
    case _                                          => None
  })

  /** The decode error for `code` in `field` when this domain defines no value at it; none for unmodelled domains. */
  def undefined(isa: ISA, field: EncodingField, code: Long): Option[String] =
    Option.when(runs.nonEmpty && decode(code).isEmpty)(
      s"$field code $code is outside the $id domain on ${isa.toString.toLowerCaseRoot}"
    )

  /** The immediate stored as `code`: two's complement when the field is signed. */
  def decodeImmediate(code: Long): Option[BigInt] = immediate.map { run =>
    val raw = BigInt(code) & ((BigInt(1) << run.bits) - 1)
    if (run.signed && raw.testBit(run.bits - 1)) raw - (BigInt(1) << run.bits) else raw
  }

  /** Every spelling of a register, name, pair or constant with its code, as assembly writes it. */
  def selectors: Map[String, Int] =
    runs.flatMap {
      case Run.Registers(file, low, high, code) =>
        (low to high).map(index => s"${DomainTable.prefix(file)}$index" -> (code + index - low))
      case Run.Named(name, code)                 => Vector(name -> code)
      case Run.Numbered(prefix, low, high, code) => (low to high).map(index => s"$prefix$index" -> (code + index - low))
      case Run.Integers(first, last, code)       =>
        (first to last by (if (last >= first) 1 else -1)).map(value =>
          value.toString -> (code + math.abs(value - first))
        )
      case Run.Floats(value, code)   => Vector(value.toString -> code)
      case Run.Literal(bytes, code)  => Vector((if (bytes == 4) "src_literal" else s"src_literal${bytes * 8}") -> code)
      case Run.Immediate(_, _, _, _) => Vector.empty
    }.toMap ++ pairs

  /** Every value of the domain, immediates at their bounds. */
  def slots: Vector[Slot] = runs.flatMap {
    case Run.Registers(file, low, high, _)  => (low to high).map(Slot.Register(file, _))
    case Run.Named(name, _)                 => Vector(Slot.Named(name))
    case Run.Numbered(prefix, low, high, _) => (low to high).map(Slot.Numbered(prefix, _))
    case Run.Integers(first, last, _)       =>
      (first to last by (if (last >= first) 1 else -1)).map(Slot.Integer(_))
    case Run.Floats(value, _)           => Vector(Slot.Floating(value))
    case Run.Literal(bytes, _)          => Vector(Slot.Literal(bytes))
    case Run.Immediate(_, _, low, high) => Vector(Slot.Immediate(low), Slot.Immediate(high))
  }
}
