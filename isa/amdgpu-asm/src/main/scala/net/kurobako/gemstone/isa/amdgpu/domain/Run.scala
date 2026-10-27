package net.kurobako.gemstone.isa.amdgpu.domain

import net.kurobako.gemstone.isa.amdgpu.asm.*

object Run {
  private val Files = Map(
    "sgpr"    -> RegisterFile.Scalar,
    "ttmp"    -> RegisterFile.TrapTemporary,
    "vgpr"    -> RegisterFile.VectorRegister,
    "accvgpr" -> RegisterFile.Accumulator
  )

  def file(name: String): RegisterFile =
    Files.getOrElse(name, throw IllegalStateException(s"unknown register file $name"))

  /** The runs of a domain; partitioned, ignored and unmodelled domains have none. */
  def read(domain: ujson.Value): Vector[Run] = domain.strOpt match {
    case Some(_) => Vector.empty
    case None    =>
      def int(key: String): Int = domain(key).num.toInt
      domain("$type").str match {
        case "Union"        => domain("members").arr.toVector.flatMap(read(_))
        case "Registers"    => Vector(Registers(file(domain("file").str), int("first"), int("last"), int("code")))
        case "Named"        => Vector(Named(domain("name").str, int("code")))
        case "NamedRange"   => Vector(Numbered(domain("prefix").str, int("first"), int("last"), int("code")))
        case "IntegerRange" => Vector(Integers(int("first"), int("last"), int("code")))
        case "FloatSet"     => domain("constants").arr.toVector.map { constant =>
            Floats(constant("text").str.toDouble, constant("code").num.toInt)
          }
        case "Literal"   => Vector(Literal(int("bytes"), int("code")))
        case "Immediate" =>
          val bits                                         = int("bits")
          val signed                                       = domain("signed").bool
          def bound(key: String, fallback: BigInt): BigInt =
            domain.obj.get(key).flatMap(_.arrOpt).flatMap(_.headOption).fold(fallback)(value =>
              BigInt(value.num.toLong)
            )
          val span = BigInt(1) << bits
          Vector(Immediate(
            bits,
            signed,
            bound("min", if (signed) -(span >> 1) else BigInt(0)),
            bound("max", if (signed) (span >> 1) - 1 else span - 1)
          ))
        case _ => Vector.empty
      }
  }
}

/** The values of an operand domain that share one stretch of field codes, read from the generated domain tables. */
enum Run derives CanEqual {
  case Registers(file: RegisterFile, first: Int, last: Int, code: Int)
  case Named(name: String, code: Int)
  case Numbered(prefix: String, first: Int, last: Int, code: Int)

  /** Inline integers from `first` to `last`, stepping towards `last` as the codes rise. */
  case Integers(first: Int, last: Int, code: Int)
  case Floats(value: Double, code: Int)
  case Literal(bytes: Int, code: Int)

  /** Values `min` to `max` stored in `bits` bits; `signed` fields read back as two's complement. */
  case Immediate(bits: Int, signed: Boolean, min: BigInt, max: BigInt)
}
