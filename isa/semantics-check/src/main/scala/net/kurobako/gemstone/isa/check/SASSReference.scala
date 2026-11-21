package net.kurobako.gemstone.isa.check

import java.util.regex.Pattern
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.semantics.Space
import net.kurobako.gemstone.semantics.render.*
import scala.collection.concurrent.TrieMap
import scala.util.Random

/** SASS instances lowered on their target as the explorer describes them, with the constant bank they read bound to
  * memory holding the harness's bytes, and the shared memory they access loaded from the harness and read back into it.
  * Each instance lowers once, with its constant operands at their default offsets, and a run moves them to the offsets
  * its inputs give, which must encode.
  */
object SASSReference extends Reference[NvidiaHarness, Lookup.Sass] {
  private type Lowered = (lowering: InstructionSemantics.SASSLowering, bank: Long)

  private val BracketedOperand = raw"\[([^\]]*)\]".r

  private val lowered = TrieMap.empty[(Int, String), Option[Lowered]]

  /** The lowering of `instance` with its constant operands in the bank the lowering binds, and the bank they name. */
  private def lower(sm: Int >= 35, instance: String, operands: Map[String, String]): Option[Lowered] = for {
    program  <- Parser.parseProgram(s"$instance;").toOption
    parsed   <- program.statements.collectFirst { case Program.Statement.Operation(operation, _, _) => operation }
    moved    <- SASSForms.banked(parsed).toOption
    lowering <- InstructionSemantics.lowered(sm, moved.instruction, operands, Map.empty).toOption
  } yield (lowering, moved.bank.fold(InstructionSemantics.ConstantBank.toLong)(_.toLong))

  extension (written: SASSModel.Written) {

    /** Whether the instance still encodes with the constant operands whose offset field `offsets` names at that offset.
      */
    private def encodes(offsets: Map[String, Long]): Either[String, Unit] = for {
      selected <- Target.parse(s"sm_${written.sm}")
      database = selected.database
      found <- database.byForm.get(written.form).toRight(s"no form ${written.form}")
      spelt = found.declared.zip(written.texts).toMap
      text <- FormatParser.render(
        found,
        written.opcode,
        field =>
          Option.when(SASSModel.immediate(field))(found.encodedBinding(field)).flatMap(offsets.get)
            .map(offset => s"0x${offset.toHexString}").orElse(spelt.get(field))
      )
      _ <- SASSModel.decode(database, found, text, spelt)
    } yield ()

    /** The factor the form scales the offset field `binding` by, which the field's value is a multiple of. */
    private def stride(binding: String): Long = (for {
      selected <- Target.parse(s"sm_${written.sm}").toOption
      found    <- selected.database.byForm.get(written.form)
      factor   <- found.fields.collectFirst {
        case NVDisasm.Assignment(
              _,
              _,
              Expression.Scale(Expression.Reference(`binding`, None, _), factor)
            ) => factor.toLong
      }
    } yield factor).getOrElse(1L)
  }

  def harness(seed: Long): NvidiaHarness = NvidiaHarness(seed)

  /** The offsets sampled for a constant operand, all but one in eight moved to a multiple of the stride its field
    * encodes; the rest check that execution and the definition both reject an offset the field cannot hold.
    */
  /** The parameters an instance of a shared access addresses with: the registers its bracketed operand names. */
  private def addressing(
      target: Option[String],
      instance: String,
      lookup: Lookup.Sass
  ): Set[String] = (for {
    selected <- target.flatMap(Target.parse(_).toOption)
    found    <- lowered.getOrElseUpdate((selected.sm, instance), lower(selected.sm, instance, lookup.operands))
    if found.lowering.steps.exists(step => SASSSharedMemory.touched(step.instruction).nonEmpty)
    spans = BracketedOperand.findAllMatchIn(instance).map(_.group(1)).toVec
  } yield lookup.operands.collect {
    case (spelling, binding) if spans.exists(_.matches(raw".*\b${Pattern.quote(spelling)}\b.*")) => binding
  }.toSet).getOrElse(Set.empty)

  /** The offsets sampled for a constant operand, all but one in eight moved to a multiple of the stride its field
    * encodes; the rest check that execution and the definition both reject an offset the field cannot hold. The
    * register a shared access addresses with is sampled below the allocation, in the same proportion, at a multiple of
    * the widest access.
    */
  // the operands of one instruction
  @SuppressWarnings(Array("bonsai:keyed-lookup-in-loop"))
  override def inputs(
      target: Option[String],
      instance: String,
      lookup: Lookup.Sass,
      parameters: Vec[(String, Type)],
      sampled: Vec[Long],
      random: Random
  ): Vec[Long] = {
    val addresses = addressing(target, instance, lookup)
    parameters.zip(sampled).map {
      case ((name, Type.Ranged(_, intervals)), value)
          if lookup.constants.exists(_.binding == name) && random.nextInt(8) != 0 =>
        val stride = lookup.written.stride(name)
        val floor  = value - Math.floorMod(value, stride)
        Vec(floor, floor + stride).find(aligned => intervals.exists((low, high) => aligned >= low && aligned <= high))
          .getOrElse(value)
      case ((name, _), _) if addresses(name) && random.nextInt(8) != 0 =>
        random.nextInt(NvidiaHarness.SharedBytes / 16).toLong * 16
      case (_, value) => value
    }
  }

  def execute(
      target: Option[String],
      instance: String,
      lookup: Lookup.Sass,
      parameters: Vec[(String, Type)],
      inputs: Vec[Long],
      results: Vec[(String, Type)],
      harness: NvidiaHarness
  ): Either[String, Vec[Long]] = for {
    selected <- target.toRight(s"$instance has no target").flatMap(Target.parse)
    fields  = lookup.constants.map(_.binding).toSet
    offsets = parameters.map(_._1).zip(inputs).filter((name, _) => fields.contains(name)).toMap
    _     <- if (offsets.isEmpty) Right(()) else lookup.written.encodes(offsets)
    found <- lowered.getOrElseUpdate((selected.sm, instance), lower(selected.sm, instance, lookup.operands))
      .toRight(s"$instance does not lower on ${selected.name}")
    lowering = found.lowering
    steps    = lowering.steps.map(_.instruction)
    product  = NvidiaWarpgroups.product(steps).nonEmpty || NvidiaMatrices.product(steps).nonEmpty
    _ <- Either.cond(
      !product || lowering.steps.forall(_.predication.isEmpty),
      (),
      s"$instance is a product under a predicate, which the reference runs with none"
    )
    bank = SASSConstantBank(
      harness,
      found.bank,
      lookup.constants.flatMap((binding, offset) => offsets.get(binding).map(offset -> _))
    )
    values <- NvidiaWarpgroups.product(steps).fold(
      NvidiaMatrices.product(steps).fold(single(lowering, parameters, inputs, results, bank, harness.sharedBytes))(
        multiply =>
          NvidiaMatrices.run(
            lowering.prologue,
            steps,
            lowering.registerCount,
            lowering.predicateCount,
            multiply,
            parameters,
            inputs,
            results,
            harness.seed,
            bank
          )
      )
    )(warpgroup =>
      NvidiaWarpgroups.run(
        lowering.prologue,
        steps,
        lowering.registerCount,
        lowering.predicateCount,
        warpgroup,
        lowering.predicates.get(_),
        parameters,
        inputs,
        results,
        harness.seed,
        bank
      )
    )
  } yield values

  private def single(
      lowering: InstructionSemantics.SASSLowering,
      parameters: Vec[(String, Type)],
      inputs: Vec[Long],
      results: Vec[(String, Type)],
      bank: SASSConstantBank,
      shared: SeededMemory
  ): Either[String, Vec[Long]] = {
    val memory = SASSSharedMemory(lowering.steps, lowering.registerCount, shared)
    val locate = (name: String) =>
      lowering.registers.get(name).map(NvidiaReference.Slot.Registers(_))
        .orElse(lowering.predicates.get(name).map(NvidiaReference.Slot.Predicate(_)))
    val operands = parameters.zip(inputs).filter((parameter, _) =>
      locate(parameter._1).nonEmpty || NvidiaReference.state(parameter._1).nonEmpty
    )
    for {
      sources <- NvidiaReference.slots(operands.map(_._1._1), locate)
      targets <- NvidiaReference.slots(results.map(_._1), locate)
      values  <- NvidiaReference.run(
        lowering.prologue ++ memory.preload,
        memory.instrumented,
        (lowering.registerCount.raw + memory.registers).unsafe[Int >= 0],
        lowering.predicateCount,
        sources.zip(operands).map { case (slot, ((_, tpe), value)) => (slot, tpe, value) },
        targets.zip(results.map(_._2)) ++ memory.outputs,
        bank
      )
    } yield {
      memory.settle(values.drop(targets.size))
      values.take(targets.size)
    }
  }

  def state(harness: NvidiaHarness): (global: Map[Long, Byte], shared: Map[Long, Byte]) =
    (harness.globalBytes.writes, harness.sharedBytes.writes)

  override def seed(harness: NvidiaHarness, space: Space, bytes: Map[Long, Byte]): Unit = space match {
    case Space.Global => harness.globalBytes.seed(bytes)
    case Space.Shared => harness.sharedBytes.seed(bytes)
    case _            => ()
  }

  override def loads(harness: NvidiaHarness): Map[Space, Set[(Long, Byte)]] =
    Map(Space.Global -> harness.globalBytes.loads, Space.Shared -> harness.sharedBytes.loads)

  override def allocation(space: Space): Option[Long] =
    Option.when(space == Space.Shared)(NvidiaHarness.SharedBytes.toLong)

  override def allocate(harness: NvidiaHarness, space: Space, bytes: Long): Unit =
    if (space == Space.Shared) harness.sharedBytes.resize(bytes)
}
