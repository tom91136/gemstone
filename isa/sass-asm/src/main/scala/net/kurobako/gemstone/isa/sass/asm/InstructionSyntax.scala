package net.kurobako.gemstone.isa.sass.asm

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.string.*
import scala.collection.mutable

/** Logical instruction text independent of program statements and machine annotations. */
private object InstructionSyntax {
  def parse(source: String): Either[String, Instruction] = {
    val text                                                         = source.trim.stripSuffix(";").trim
    val (prefix, tail)                                               = text.span(!_.isWhitespace)
    val guarded: Either[String, (Option[Instruction.Guard], String)] = if (prefix.startsWith("@")) {
      OperandParser.parse(prefix.drop(1)).flatMap {
        case predicate: Operand.Predicate => Right(Some(Instruction.Guard(predicate)) -> tail.trim)
        case _                            => Left(s"invalid instruction guard $prefix")
      }
    } else Right(None -> text)
    guarded.flatMap { case (guard, body) =>
      val (qualified, written) = body.span(!_.isWhitespace)
      // a warpgroup product reads its descriptors as `gdesc[...]` operands rather than recording the space
      val (operandsText, spaces) =
        if (qualified.takeWhile(_ != '.').endsWith("GMMA")) (written, Vec.empty[String]) else memorySpaces(written)
      val parts = qualified.split('.').toVec
      for {
        _ <- Either.cond(
          !qualified.endsWith(".") && !qualified.contains(".."),
          (),
          s"empty SASS opcode qualifier $qualified"
        )
        mnemonic <- parts.headOption.toRight(s"unknown SASS opcode $qualified")
        parsed   <- OperandParser.parseMany(operandsText.trim)
        // UP2UR and UR2UP spell the uniform predicate file they move, which their encodings leave implicit
        (returns, rest) = returnAddress(parsed.filterNot(_ == Operand.Symbol("UPR")))
        // a GMMA scoreboard, `gsb0`, is a modifier of the encoding however nvdisasm places it
        (scoreboards, spelt) = rest.partition(OperandQueries.isScoreboard(_))
        operands             = elided(mnemonic, spelt)
        modifiers            =
          (parts.drop(1) ++ returns ++ spaces.filterNot(parts.drop(1).contains) ++
            scoreboards.map(OperandQueries.scoreboard(_))).map(Instruction.Modifier(_))
        // an immediate offset into an indexed bank is an address for some forms and an immediate for others
        instruction <- InstructionDefinitions.decode(mnemonic, modifiers, operands, guard).left.flatMap(failure =>
          operands.indices.find(index =>
            index > 0 && OperandQueries.indexedBank(operands(index - 1)) &&
              (operands(index) match {
                case Operand.Address(None, None, _, _, None, None) => true
                case _                                             => false
              })
          ).fold[Either[String, Instruction]](Left(failure)) { index =>
            val Operand.Address(displacement = offset) = operands(index): @unchecked
            InstructionDefinitions.decode(mnemonic, modifiers, operands.updated(index, Operand.Bits(offset, 0)), guard)
          }
        )
      } yield instruction
    }
  }

  private val Spaces = Vec("tmem", "gdesc", "idesc")

  /** The operands with each `tmem[...]`, `gdesc[...]` and `idesc[...]` written as a plain address, and those spaces in
    * operand order, which the encodings of the tcgen05 instructions record as modifiers.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def memorySpaces(operands: String): (String, Vec[String]) = {
    var text  = operands
    val found = mutable.ArrayBuffer.empty[(Int, String)]
    Spaces.foreach { space =>
      val pattern = s"(?i)\\b$space\\[".r
      found.appendAll(pattern.findAllMatchIn(text).map(_.start -> space.toUpperCaseRoot))
      text = pattern.replaceAllIn(text, "[")
    }
    (text, found.toVec.sortBy(_._1).map(_._2))
  }

  /** RPCMOV's `Rpc.HI` or `Rpc.LO` operand as the modifiers its encodings record: `Rpc` and the half where it is the
    * destination, the half alone where it is the source.
    */
  private def returnAddress(operands: Vec[Operand]): (Vec[String], Vec[Operand]) = operands match {
    case Vec(Operand.Qualified(Operand.Symbol("Rpc"), Vec(half)), source) => (Vec("Rpc", half.spelling), Vec(source))
    case Vec(destination, Operand.Qualified(Operand.Symbol("Rpc"), Vec(half))) => (Vec(half.spelling), Vec(destination))
    case _                                                                     => (Vec.empty, operands)
  }

  /** The operands nvdisasm leaves out of a tcgen05 instruction where they are zero: the disabled-lane or zero-column
    * register of an MMA and the multicast mask of UTCBAR.
    */
  private def elided(mnemonic: String, operands: Vec[Operand]): Vec[Operand] = {
    val zero = Operand.Register(RegisterBank.UR, 255)
    mnemonic match {
      case "UTCHMMA" | "UTCIMMA" | "UTCQMMA" | "UTCOMMA" | "__HIR0X130" =>
        operands.indexWhere(OperandQueries.isPredicateAllowingBoth(_)) match {
          case 5 => operands.patch(5, Vec(zero), 0)
          case _ => operands
        }
      case "UTCBAR" if operands.size == 2 => operands :+ zero
      case _                              => operands
    }
  }

  /** `labels` replaces the operands at the given positions with symbolic destinations. */
  def print(instruction: Instruction, labels: Map[Int, String] = Map.empty): Either[String, String] = for {
    operands <- instruction.operands.zipWithIndex.traverse((operand, index) =>
      labels.get(index).fold(OperandPrinter.print(operand))(Right(_))
    )
    guard <- instruction.guard.traverse(value => OperandPrinter.print(value.predicate))
  } yield {
    val opcode = (instruction.mnemonic.raw +: instruction.modifiers.map(_.name)).mkString(".")
    // `cx[URa]` and the offset after it print as one operand, the offset bracketed
    val joined = mutable.ArrayBuffer.empty[String]
    instruction.operands.indices.foreach { index =>
      if (index > 0 && OperandQueries.indexedBank(instruction.operands(index - 1)))
        joined(joined.size - 1) =
          joined.last + (if (operands(index).startsWith("[")) operands(index) else s"[${operands(index)}]")
      else joined.append(operands(index))
    }
    guard.fold("")(value => s"@$value ") + opcode +
      (if (joined.isEmpty) "" else " " + joined.mkString(", ")) + ";"
  }

}
