package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.vm.RegisterUse
import net.kurobako.gemstone.vm.Workgroups
import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.Random

/** One-move candidates of a SASS window, each checked by the sass-asm round trip and the window admission. The window's
  * loads and stores stay as decoded and in their order ([[Moves.ordered]]), and the other moves work around them.
  */
final class SASSMoves private (
    module: Cubin.Module,
    function: NonEmpty[String],
    targetName: String,
    target: Target,
    window: Window,
    base: Vec[DecodedInstruction],
    uses: Vec[RegisterUse],
    scratch: Vec[String]
) {
  import Moves.each
  import Moves.unitsOf
  import SASSMoves.*

  private val disassembler              = new Disassembler(target)
  private val written                   = uses.flatMap(use => unitsOf(use, use.writes)).toSet
  private val writable                  = written ++ scratch
  private val original                  = text(base).getOrElse(Vec.empty[String])
  private val defined: Vec[Set[String]] = Moves.defined(window, uses)

  private def sound(used: Vec[RegisterUse]): Boolean = Moves.sound(window, Set.empty, written, used)

  // a load or store is itself only with the encoding word it was decoded with: its text does not show its descriptor
  private def access(instruction: DecodedInstruction): Option[Moves.Access[DecodedInstruction]] =
    SASSForms.of(targetName, instruction.operation).toOption.map(_.kase.definition).filter(GlobalAccess.accesses(_))
      .map(definition => Moves.Access(instruction.copy(address = 0.unsafe[Int >= 0]), GlobalAccess.loads(definition)))

  private lazy val pinned = Moves.accesses(base)(access)

  // a reader waits on a load through its own control word, so a candidate may read what a load wrote
  private def ordered(sequence: Vec[DecodedInstruction], used: Vec[RegisterUse]): Boolean =
    Moves.ordered(pinned, sequence, used, waits = false)(access)

  private def placed(sequence: Vec[DecodedInstruction]): Vec[DecodedInstruction] =
    sequence.zipWithIndex.map { case (instruction, index) => instruction.copy(address = (16 * index).unsafe[Int >= 0]) }

  private def encoded(operations: Vec[Instruction]): Option[Vec[Byte]] =
    each(operations.zipWithIndex) { case (operation, index) =>
      Assembler.encode(operation, target, (16 * index).unsafe[Int >= 0]).toOption.map(bytes =>
        Vec.unsafeWrapArray(bytes)
      )
    }.map(_.flatten)

  private def roundTrips(operations: Vec[Instruction]): Boolean =
    encoded(operations).exists { bytes =>
      disassembler.decode(bytes).toOption.exists { program =>
        val reread = program.statements.collect { case Program.Statement.Operation(instruction, _, _) => instruction }
        reread.size == operations.size && each(reread)(rendered) == each(operations)(rendered) &&
        encoded(reread).contains(bytes)
      }
    }

  private def staticUses(instructions: Vec[DecodedInstruction]): Option[Vec[RegisterUse]] = {
    val program =
      LoadedProgram(
        module,
        Map(function -> Vec(DecodedFunction(function, instructions))),
        Workgroups.Sequential,
        rendered
      )
    try program.staticCode(function).toOption.map(_._2)
    finally program.close()
  }

  private def effects(sequences: Vec[Vec[DecodedInstruction]]): Vec[Option[Vec[RegisterUse]]] =
    if (sequences.isEmpty) Vec.empty
    else {
      val found = staticUses(placed(sequences.flatten ++ Vec(Exit))).filter(_.size == sequences.map(_.size).sum + 1)
      found match {
        case Some(all) =>
          val ends = sequences.map(_.size).scanLeft(0)(_ + _)
          sequences.indices.toVec.map(index => Some(all.slice(ends(index), ends(index + 1))))
        case None if sequences.size == 1 => Vec(None)
        case None                        =>
          val (left, right) = sequences.splitAt(sequences.size / 2)
          effects(left) ++ effects(right)
      }
    }

  private def reoperated(
      source: DecodedInstruction,
      mnemonic: String,
      operands: Vec[Operand]
  ): Option[DecodedInstruction] =
    InstructionDefinitions.decode(mnemonic, source.modifiers, operands, source.guard).toOption
      .map(value => DecodedInstruction(source.address, value, None, None, None))

  private def legal(kind: Kind, raws: Vec[Vec[DecodedInstruction]], limit: Int): Vec[Candidate] = {
    val seen = mutable.Set(original)

    @tailrec
    def go(chunks: Iterator[Vec[Vec[DecodedInstruction]]], kept: Vec[Candidate]): Vec[Candidate] =
      if (kept.size >= limit || !chunks.hasNext) kept.take(limit)
      else {
        val fresh = chunks.next().map(placed).filter(sequence => text(sequence).exists(seen.add))
        val named = fresh.filter(sequence =>
          SASSWindows.admitted(targetName, sequence).forall(verdict => verdict) &&
            roundTrips(sequence.map(_.operation))
        )
        go(
          chunks,
          kept ++ named.zip(effects(named)).collect {
            case (sequence, Some(used)) if sound(used) && ordered(sequence, used) => Candidate(kind, sequence, used)
          }
        )
      }

    go(raws.grouped(ChunkSize), Vec.empty)
  }

  private def replaced: Vec[Vec[DecodedInstruction]] = base.indices.toVec.flatMap { at =>
    val source = base(at)
    mnemonics.filter(_ != source.mnemonic.raw).flatMap(name =>
      reoperated(source, name, source.operands).map(base.updated(at, _))
    )
  }

  private def swapped: Vec[Vec[DecodedInstruction]] = base.indices.toVec.flatMap { at =>
    val source   = base(at)
    val operands = source.operands
    Vec.from(for {
      i <- operands.indices
      j <- (i + 1) until operands.size
      if operands(i).ordinal == operands(j).ordinal && operands(i) != operands(j)
      value <- reoperated(source, source.mnemonic.raw, operands.updated(i, operands(j)).updated(j, operands(i))).toList
    } yield base.updated(at, value))
  }

  private def deleted: Vec[Vec[DecodedInstruction]] =
    base.indices.toVec.map(at => base.patch(at, Vec.empty[DecodedInstruction], 1))

  private def immediate(operand: Operand): Option[(BigInt, BigInt => Operand)] = operand match {
    case Operand.Integer(value)     => Some((value, replacement => Operand.Integer(replacement)))
    case Operand.Bits(value, width) => Some((value, replacement => Operand.Bits(replacement, width)))
    case _                          => None
  }

  private def changed: Vec[Vec[DecodedInstruction]] = base.indices.toVec.flatMap { at =>
    val source = base(at)
    source.operands.indices.toVec.flatMap { position =>
      immediate(source.operands(position)).fold(Vec.empty[Vec[DecodedInstruction]]) { (value, literal) =>
        Vec(BigInt(0), BigInt(1), value + 1, value - 1).distinct.filter(_ != value).flatMap(replacement =>
          reoperated(source, source.mnemonic.raw, source.operands.updated(position, literal(replacement)))
            .map(base.updated(at, _))
        )
      }
    }
  }

  private def inserted(random: Random, attempts: Int): Vec[Vec[DecodedInstruction]] = Vec.from(Iterator.continually {
    val at     = random.nextInt(base.size + 1)
    val source = base(random.nextInt(base.size))
    val pool = (defined(at) ++ writable).iterator.flatMap(_.stripPrefix("R").toIntOption).filter(_ < RZ).toVec.distinct
    Option.when(pool.nonEmpty) {
      val operands = source.operands.map {
        case Operand.Register(RegisterBank.R, _) =>
          Operand.Register(RegisterBank.R, pool(random.nextInt(pool.size)).unsafe[Int in 0 ~ 255])
        case operand => operand
      }
      reoperated(source, source.mnemonic.raw, operands).map(value => base.patch(at, Vec(value), 0))
    }.flatten
  }.take(attempts).flatten)

  /** Up to `limit` legal candidates of `kind`, drawn with `random`. */
  def candidates(kind: SASSMoves.Kind, random: Random, limit: Int): Vec[SASSMoves.Candidate] = legal(
    kind,
    kind match {
      case Kind.Opcode    => random.shuffle(replaced.toList).toVec
      case Kind.Swap      => random.shuffle(swapped.toList).toVec
      case Kind.Delete    => random.shuffle(deleted.toList).toVec
      case Kind.Immediate => random.shuffle(changed.toList).toVec
      case Kind.Insert    => inserted(random, math.max(limit, 1) * InsertAttempts)
    },
    limit
  )

  /** Up to `limit` legal candidates of each kind, the same for the same seed. */
  def propose(seed: Long, limit: Int): Vec[SASSMoves.Candidate] = Moves.propose(seed, limit)(candidates(_, _, _))

  def from(sequence: Vec[DecodedInstruction]): Either[String, SASSMoves] =
    effects(Vec(sequence)).headOption.flatten.toRight("the sequence does not compile in this kernel").map { used =>
      new SASSMoves(module, function, targetName, target, window, placed(sequence), used, scratch)
    }
}

object SASSMoves {
  import Moves.each
  import Moves.unitsOf

  type Kind = Moves.Kind
  val Kind = Moves.Kind
  type Candidate = Moves.Candidate[DecodedInstruction]

  def Candidate(kind: Kind, instructions: Vec[DecodedInstruction], uses: Vec[RegisterUse]): Candidate =
    Moves.Candidate(kind, instructions, uses)

  private val ChunkSize      = 48
  private val InsertAttempts = 48
  private val ScratchSize    = 4
  private val RZ             = 255

  private val Exit = DecodedInstruction(0.unsafe[Int >= 0], Parser.parse("EXIT;").toOption.get, None, None, None)

  private lazy val mnemonics: Vec[String] = Instruction.Definition.all.map(_.mnemonic.raw).distinct

  private def rendered(instruction: Instruction): Option[String] = Printer.print(instruction).toOption

  private def text(sequence: Vec[DecodedInstruction]): Option[Vec[String]] =
    each(sequence)(instruction => rendered(instruction.operation))

  def of(
      module: Cubin.Module,
      decoded: Map[NonEmpty[String], Vec[DecodedFunction]],
      function: NonEmpty[String],
      target: String,
      window: Window
  ): Either[String, SASSMoves] = for {
    named <- Target.parse(target)
    found <- staticCode(module, decoded, function)
    (_, uses, instructions) = found
  } yield {
    val base       = instructions.slice(window.first, window.last + 1)
    val windowUses = uses.slice(window.first, window.last + 1)
    val unitNames  = uses.flatMap(use => unitsOf(use, use.reads ++ use.writes)).toSet
    val busy       = window.liveIn ++ window.liveOut ++ windowUses.flatMap(use => unitsOf(use, use.reads ++ use.writes))
    val top     = unitNames.iterator.flatMap(_.stripPrefix("R").toIntOption).maxOption.getOrElse(-1) + 1 + ScratchSize
    val scratch = Vec.from(0 until top.min(RZ)).map(index => s"R$index").filterNot(busy).take(ScratchSize)
    new SASSMoves(module, function, target, named, window, base, windowUses, scratch)
  }

  def of(image: Vec[Byte], function: String, target: String, window: Window): Either[String, SASSMoves] = for {
    module <- Cubin.decode(image).left.map(_.raw)
    name   <- function.refine[NonEmpty[String]].left.map(_ => s"no SASS kernel '$function'")
    moves  <- of(module, Map.empty, name, target, window)
  } yield moves

  private def staticCode(
      module: Cubin.Module,
      decoded: Map[NonEmpty[String], Vec[DecodedFunction]],
      function: NonEmpty[String]
  ): Either[String, (ControlFlowGraph, Vec[RegisterUse], Vec[DecodedInstruction])] = {
    val program = LoadedProgram(module, decoded, Workgroups.Sequential, rendered)
    try program.staticCode(function).left.map(_.message)
    finally program.close()
  }
}
