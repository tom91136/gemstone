package net.kurobako.gemstone.isa.amdgpu.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.string.*
import scala.annotation.tailrec

object Instruction {
  enum DecodeError {
    case UnknownOpcode(opcode: String)
    case InvalidOperandCount(opcode: String, expected: Vec[Int], actual: Int)
    case AmbiguousForms(opcode: String, definitions: Vec[Opcode])
  }

  /** Assembly operand counts: distinct explicit operand signatures from the catalogue plus a reviewed syntax overlay
    * for forms absent from ACO's physical opcode model or conditional operands that AMD XML marks as always present.
    */
  def operandCounts(definition: Opcode): Vec[Int] = {
    val explicit = definition.meta.encodings.map(_.operands.count(!_.implicitOperand)).filter(_ > 0)
    (explicit ++ syntaxOperandCounts(definition.opcode)).distinct.sorted match {
      case Vec()  => Vec(0)
      case counts => counts
    }
  }

  /** SOPP branches whose label operand encodes a signed word displacement from the next instruction. */
  def branchesRelative(definition: Opcode, target: Target): Boolean =
    Metadata.encodingsFor(definition, target).exists(encoding =>
      encoding.format == EncodingFormat.SOPP && encoding.operands.exists(_.kind == OperandKind.LABEL)
    )

  private def syntaxOperandCounts(opcode: String): Vec[Int] = opcode match {
    case value if value.startsWith("ds_") && value.contains("_src2_")           => Vec(1)
    case "s_endpgm"                                                             => Vec(0, 1)
    case "image_atomic_rsub"                                                    => Vec(3)
    case value if value.startsWith("buffer_load_")                              => Vec(3, 4)
    case value if value.startsWith("flat_atomic_")                              => Vec(2, 3, 4)
    case value if value.startsWith("global_atomic_")                            => Vec(3, 4)
    case value if value.startsWith("global_load_lds_")                          => Vec(2)
    case value if value.startsWith("global_load_")                              => Vec(2, 3)
    case value if value.startsWith("scratch_load_lds_")                         => Vec(2)
    case value if value.startsWith("scratch_load_")                             => Vec(2, 3)
    case "flat_load_monitor_b128"                                               => Vec(2)
    case "flat_load_monitor_b32" | "flat_load_monitor_b64"                      => Vec(2, 3)
    case "flat_prefetch_b8"                                                     => Vec(1, 2)
    case "v_add_i32" | "v_add_u32" | "v_sub_i32" | "v_sub_u32" | "v_subrev_u32" => Vec(3, 4)
    case value if value.startsWith("v_cmpx_")                                   => Vec(2, 3)
    case "v_dual_cndmask_b32"                                                   => Vec(3, 4)
    case _                                                                      => Vec.empty
  }

  def decode(definition: Opcode, operands: Vec[Operand], mods: Vec[Modifier]): Either[DecodeError, Instruction] = {
    val counts = operandCounts(definition)
    Either.cond(
      counts.contains(operands.size),
      Instruction(definition, operands, mods),
      DecodeError.InvalidOperandCount(definition.opcode, counts, operands.size)
    )
  }

  def decode(
      opcode: String,
      operands: Vec[Operand],
      mods: Vec[Modifier] = Vec.empty
  ): Either[DecodeError, Instruction] =
    decodeWithSuffixes(opcode.toLowerCaseRoot, operands, mods, opcode.toLowerCaseRoot)

  private val EncodingSuffixes: Vec[(String, Modifier)] = Vec(
    "_dpp8" -> Modifier("dpp8"),
    "_sdwa" -> Modifier("sdwa"),
    "_dpp"  -> Modifier("dpp"),
    "_e64"  -> Modifier("e64"),
    "_e32"  -> Modifier("e32")
  )

  @tailrec
  private def decodeWithSuffixes(
      name: String,
      operands: Vec[Operand],
      mods: Vec[Modifier],
      original: String
  ): Either[DecodeError, Instruction] =
    decodeCanonical(name, operands, mods) match {
      case Left(DecodeError.UnknownOpcode(_)) =>
        EncodingSuffixes.collectFirst {
          case (suffix, modifier) if name.endsWith(suffix) => name.dropRight(suffix.length) -> modifier
        } match {
          case Some((canonical, modifier)) => decodeWithSuffixes(canonical, operands, modifier +: mods, original)
          case None                        => Left(DecodeError.UnknownOpcode(original))
        }
      case result => result
    }

  private def decodeCanonical(
      name: String,
      operands: Vec[Operand],
      mods: Vec[Modifier]
  ): Either[DecodeError, Instruction] =
    Opcode.forOpcode(name) match {
      case Vec()      => Left(DecodeError.UnknownOpcode(name))
      case candidates => candidates.flatMap(decode(_, operands, mods).toOption) match {
          case Vec(instruction) => Right(instruction)
          case Vec()            => Left(DecodeError.InvalidOperandCount(
              name,
              candidates.flatMap(operandCounts(_)).distinct.sorted,
              operands.size
            ))
          case matches => Left(DecodeError.AmbiguousForms(name, matches.map(_.definition)))
        }
    }
}

/** An assembly instruction whose operand count matches one of its opcode's forms. */
case class Instruction private (definition: Opcode, operands: Vec[Operand], mods: Vec[Modifier]) {
  def opcode: NonEmpty[String] = definition.opcode
  def meta: Metadata           = definition.meta
}
