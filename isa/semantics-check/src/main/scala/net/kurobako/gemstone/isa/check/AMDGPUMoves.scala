package net.kurobako.gemstone.isa.check

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.isa.amdgpu.domain.*
import net.kurobako.gemstone.vm.RegisterUse
import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.Random

/** Candidate replacements of one AMDGPU window, each one move away from it. The generated catalogue, the operand
  * domains and the encoder decide what is legal: a candidate encodes to bytes that decode to the same instructions,
  * holds only instructions the equivalence checker admits ([[AMDGPUForms.of]]), writes no EXEC, reads only what is live
  * into the window or written earlier in the candidate, and writes no register that is live out of the window unless
  * the window writes it. The register use is the VM's, through the kernel the candidate would patch in. The window's
  * loads and stores stay as they are and in their order, and the other moves work around them.
  *
  * Opcodes replace each other when some encoding of each has the same explicit operand kinds, widths and directions. An
  * inserted instruction draws its sources from the window's live-in registers and the registers written before it, and
  * its destinations from the registers the window writes and a bounded pool of scratch registers that are dead across
  * the window.
  */
final class AMDGPUMoves private (
    image: Vec[Byte],
    function: String,
    target: Target,
    window: Window,
    base: Vec[Instruction],
    uses: Vec[RegisterUse],
    scratch: Vec[String],
    tables: AMDGPUMoves.Tables
) {
  import AMDGPUMoves.*
  import Moves.each
  import Moves.unitsOf

  private val mode     = TargetMode(target)
  private val code     = CodeObject.decode(image).toOption.get
  private val exec     = ImplicitState.units(ImplicitState.Exec, mode.waveSize).toSet
  private val written  = uses.flatMap(use => unitsOf(use, use.writes)).toSet
  private val writable = written ++ scratch
  private val original = each(base)(settled(_)).getOrElse(base).map(Printer.render(_))

  private val defined: Vec[Set[String]] = Moves.defined(window, uses)

  private def canonical: mutable.Map[String, Option[Instruction]] = tables.canonical

  private def opcodes: Vec[Opcode] = tables.opcodes

  private def classes: Map[String, Vec[Opcode]] = tables.classes

  private def shapes(instruction: Instruction): Vec[(Metadata.Encoding, Vec[OperandInfo])] =
    instruction.definition.encodings(target).map(encoding => encoding -> encoding.operands.filterNot(_.implicitOperand))
      .filter(_._2.size == instruction.operands.size)

  private def constant(operand: Operand): Boolean = operand match {
    case Operand.IntegerLiteral(_, _) | Operand.DecimalLiteral(_) => true
    case Operand.Expression(text)                                 => text.startsWith("lit(")
    case _                                                        => false
  }

  private def settled(instruction: Instruction): Option[Instruction] = canonical.getOrElseUpdate(
    Printer.render(instruction),
    for {
      bytes <- Binary.encode(instruction, mode).toOption
      value <- Binary.decode(bytes, mode).toOption
      again <- Binary.encode(value, mode).toOption
      back  <- Binary.decode(again, mode).toOption
      if again.sameElements(bytes) && Printer.render(back) == Printer.render(value)
    } yield value
  )

  private def effects(sequences: Vec[Vec[Instruction]]): Vec[Option[Vec[RegisterUse]]] =
    if (sequences.isEmpty) Vec.empty
    else {
      val bytes   = sequences.flatten.flatMap(Binary.encode(_, mode).toOption.get)
      val patched = code.replaceKernel(function.unsafe[NonEmpty[String]], bytes ++ end(mode))
      val found   = patched.toOption.flatMap(value => AMDGPUModel.staticCode(value.image, function).toOption)
        .map(_._2).filter(_.size == sequences.map(_.size).sum + 1)
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

  private def sound(used: Vec[RegisterUse]): Boolean = Moves.sound(window, exec, written, used)

  private def access(instruction: Instruction): Option[Moves.Access[String]] =
    AMDGPUForms.of(target, instruction).toOption.map(_.kase.definition).filter(GlobalAccess.accesses(_))
      .map(definition => Moves.Access(Printer.render(instruction), GlobalAccess.loads(definition)))

  private lazy val pinned = Moves.accesses(base)(access)

  // no s_waitcnt is inside a window, so what a load writes is not there to read before the window ends
  private def ordered(sequence: Vec[Instruction], used: Vec[RegisterUse]): Boolean =
    Moves.ordered(pinned, sequence, used, waits = true)(access)

  private def legal(kind: Kind, raws: Vec[Vec[Instruction]], limit: Int): Vec[Candidate] = {
    val seen = mutable.Set(original)

    @tailrec
    def go(chunks: Iterator[Vec[Vec[Instruction]]], kept: Vec[Candidate]): Vec[Candidate] =
      if (kept.size >= limit || !chunks.hasNext) kept.take(limit)
      else {
        val fresh =
          chunks.next().flatMap(each(_)(settled(_))).filter(sequence => seen.add(sequence.map(Printer.render(_))))
        val named = fresh.filter(_.forall(AMDGPUForms.covered(target, _)))
        // a form is rendered only for the candidates that are sound, and no more of them than are kept
        val legal = named.zip(effects(named)).iterator.collect {
          case (sequence, Some(used))
              if sound(used) && sequence.forall(AMDGPUForms.of(target, _).isRight) && ordered(sequence, used) =>
            Candidate(kind, sequence, used)
        }
        go(chunks, kept ++ legal.take(limit - kept.size))
      }

    go(raws.grouped(ChunkSize), Vec.empty)
  }

  private def replaced: Vec[Vec[Instruction]] = base.indices.toVec.flatMap { at =>
    val instruction = base(at)
    val names       =
      shapes(instruction).map(_._2).map(signature(_)).distinct.flatMap(classes.getOrElse(_, Vec.empty[Opcode]))
    names.distinct.filter(_.mnemonic.raw != instruction.opcode.raw).flatMap(opcode =>
      Instruction.decode(opcode.mnemonic.raw, instruction.operands, instruction.mods).toOption
        .map(value => base.updated(at, value))
    )
  }

  private def swapped: Vec[Vec[Instruction]] = base.indices.toVec.flatMap { at =>
    val instruction = base(at)
    val operands    = instruction.operands
    val pairs       = shapes(instruction).flatMap { (_, infos) =>
      Vec.from(for {
        i <- infos.indices
        j <- (i + 1) until infos.size
        if !infos(i).output && !infos(j).output && infos(i).kind == infos(j).kind &&
          infos(i).bits.raw == infos(j).bits.raw
        if operands(i) != operands(j)
      } yield (i, j))
    }.distinct
    pairs.flatMap((i, j) =>
      Instruction.decode(
        instruction.definition,
        operands.updated(i, operands(j)).updated(j, operands(i)),
        instruction.mods
      )
        .toOption.map(value => base.updated(at, value))
    )
  }

  private def deleted: Vec[Vec[Instruction]] = base.indices.toVec.map(at => base.patch(at, Vec.empty[Instruction], 1))

  private def changed(random: Random): Vec[Vec[Instruction]] = base.indices.toVec.flatMap { at =>
    val instruction = base(at)
    val sites       = shapes(instruction).flatMap { (encoding, infos) =>
      infos.indices.toVec.flatMap { position =>
        val table = infos(position).field.flatMap(field =>
          DomainCatalogue.field(encoding.isa, encoding.format, encoding.opcode, field)
        )
        Option.when(constant(instruction.operands(position)))(table).flatten.filter(table =>
          table.constants || table.immediate.nonEmpty
        )
          .map(position -> _)
      }
    }
    sites.distinctBy(_._1).flatMap { (position, table) =>
      val current = Printer.render(instruction)
      constants(table, random).map(value => instruction.operands.updated(position, value)).flatMap(operands =>
        Instruction.decode(instruction.definition, operands, instruction.mods).toOption
      ).filter(value => Printer.render(value) != current).map(value => base.updated(at, value))
    }
  }

  private def drawn(
      info: OperandInfo,
      encoding: Metadata.Encoding,
      live: Set[String],
      random: Random
  ): Option[Operand] =
    info.field.flatMap(field => DomainCatalogue.field(encoding.isa, encoding.format, encoding.opcode, field)).flatMap {
      table =>
        val bits  = info.bits.raw
        val count = math.max(bits, 32) / 32
        val pool  = if (info.output) writable else live
        val spans = RegisterFiles.filter((file, _) => table.has(file)).flatMap { (file, prefix) =>
          val top = pool.iterator.flatMap(name => name.stripPrefix(prefix).toIntOption).maxOption.getOrElse(-1) + 1
          Vec.from(0 until top).filter(first =>
            (first until first + count).forall(index => pool.contains(s"$prefix$index")) &&
              table.registers(file, first, bits).isDefined
          ).map(first => Operand.Register(file, first, first + count - 1))
        }
        val values = if (info.output) Vec.empty[Operand]
        else random.shuffle(constants(table, random).toList).toVec.take(MaxConstants)
        if (bits <= 0) None
        else if (spans.nonEmpty && (values.isEmpty || random.nextBoolean())) Some(spans(random.nextInt(spans.size)))
        else Option.when(values.nonEmpty)(values(random.nextInt(values.size)))
    }

  private def inserted(random: Random, attempts: Int): Vec[Vec[Instruction]] = Vec.from(Iterator.continually {
    val at      = random.nextInt(base.size + 1)
    val opcode  = opcodes(random.nextInt(opcodes.size))
    val choices = random.shuffle(opcode.encodings(target).toList)
    choices.iterator.map { encoding =>
      val infos = encoding.operands.filterNot(_.implicitOperand)
      val picks = each(infos)(drawn(_, encoding, defined(at), random))
      picks.flatMap(operands => Instruction.decode(opcode.mnemonic.raw, operands, Vec.empty[Modifier]).toOption)
    }.collectFirst { case Some(value) => base.patch(at, Vec(value), 0) }
  }.take(attempts).flatten)

  /** Up to `limit` legal candidates of `kind`, drawn with `random`. */
  def candidates(kind: Kind, random: Random, limit: Int): Vec[Candidate] = legal(
    kind,
    kind match {
      case Kind.Opcode    => random.shuffle(replaced.toList).toVec
      case Kind.Swap      => random.shuffle(swapped.toList).toVec
      case Kind.Delete    => random.shuffle(deleted.toList).toVec
      case Kind.Immediate => random.shuffle(changed(random).toList).toVec
      case Kind.Insert    => inserted(random, math.max(limit, 1) * InsertAttempts)
    },
    limit
  )

  /** Up to `limit` legal candidates of each kind, the same for the same seed. */
  def propose(seed: Long, limit: Int): Vec[Candidate] = Moves.propose(seed, limit)(candidates(_, _, _))

  def from(sequence: Vec[Instruction]): Either[String, AMDGPUMoves] =
    effects(Vec(sequence)).headOption.flatten.toRight("the sequence does not compile in this kernel").map { used =>
      AMDGPUMoves(image, function, target, window, sequence, used, scratch, tables)
    }
}

object AMDGPUMoves {
  import Moves.unitsOf

  type Kind = Moves.Kind
  val Kind = Moves.Kind
  type Candidate = Moves.Candidate[Instruction]

  def Candidate(kind: Kind, instructions: Vec[Instruction], uses: Vec[RegisterUse]): Candidate =
    Moves.Candidate(kind, instructions, uses)

  private final class Tables(
      val opcodes: Vec[Opcode],
      val classes: Map[String, Vec[Opcode]],
      val canonical: mutable.Map[String, Option[Instruction]]
  )

  private val ChunkSize      = 48
  private val InsertAttempts = 48
  private val MaxConstants   = 24
  private val ScratchSize    = 4
  private val RegisterFiles  = Vec(RegisterFile.Scalar -> "s", RegisterFile.VectorRegister -> "v")

  private def signature(encoding: Metadata.Encoding): String = signature(encoding.operands.filterNot(_.implicitOperand))

  private def signature(infos: Vec[OperandInfo]): String =
    infos.map(info => s"${info.kind}:${info.bits}:${info.output}").mkString(",")

  private def end(mode: TargetMode): Vec[Byte] =
    Binary.encode(Instruction.decode("s_endpgm", Vec.empty).toOption.get, mode).toOption.get

  private def constants(table: DomainTable, random: Random): Vec[Operand] = table.slots.toVec.flatMap {
    case Slot.Integer(value)  => Vec(Operand.IntegerLiteral(BigInt(value), 10))
    case Slot.Floating(value) => Vec(Operand.DecimalLiteral(value.toString))
    case Slot.Literal(4)      => Vec.fill(3)(Operand.IntegerLiteral(BigInt(random.nextInt().toLong & 0xffffffffL), 16))
    case Slot.Immediate(_)    =>
      table.immediate.toVec.flatMap { run =>
        val span = run.max - run.min + 1
        Vec(run.min, run.max) ++ Vec.fill(2)(run.min + BigInt(span.bitLength + 8, random).mod(span))
      }.distinct.map(Operand.IntegerLiteral(_, 10))
    case _ => Vec.empty[Operand]
  }.distinct

  /** The kernel `function` of `image` seen from `window` on `target`, one of [[AMDGPUGenerator.targets]]. */
  def of(image: Vec[Byte], function: String, target: String, window: Window): Either[String, AMDGPUMoves] = for {
    named <- target.refine[Target].left.map(_.raw)
    found <- AMDGPUModel.staticCode(image, function)
    (_, uses, assembly) = found
    instructions <- (window.first to window.last).toVector.traverse { index =>
      Parser.parse(assembly(index)).left.map(_.diagnostic.raw).flatMap(program =>
        program.statements match {
          case Vec(Program.Statement.Instruction(instruction)) => Right(instruction)
          case _                                               => Left(s"not a single instruction: ${assembly(index)}")
        }
      )
    }
  } yield {
    val base       = instructions.toVec
    val windowUses = uses.slice(window.first, window.last + 1)
    val named0     = uses.flatMap(use => unitsOf(use, use.reads ++ use.writes)).toSet
    val busy       = window.liveIn ++ window.liveOut ++ windowUses.flatMap(use => unitsOf(use, use.reads ++ use.writes))
    val pool       = Vec("s", "v").flatMap { prefix =>
      val top = named0.iterator.flatMap(_.stripPrefix(prefix).toIntOption).maxOption.getOrElse(-1) + 1 + ScratchSize
      Vec.from(0 until top).map(index => s"$prefix$index").filterNot(busy).take(ScratchSize)
    }
    AMDGPUMoves(image, function, named, window, base, windowUses, pool, catalogue(named))
  }

  private def catalogue(target: Target): Tables = {
    val opcodes = InstructionDefs.definitions.toVec.filter(_.encodings(target).nonEmpty).sortBy(_.mnemonic.raw)
    val classes =
      opcodes.flatMap(opcode => opcode.encodings(target).map(signature(_)).distinct.map(_ -> opcode)).groupMap(_._1)(
        _._2
      )
    Tables(opcodes, classes, mutable.Map.empty)
  }
}
