package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.semantics.render.*
import scala.collection.concurrent.TrieMap
import scala.util.Random

/** SASS instances lowered on their target as the explorer describes them, with the constant bank they read bound to
  * memory holding the harness's bytes. Each instance lowers once, with its constant operands at their default offsets,
  * and a run moves them to the offsets its inputs give, which must encode.
  */
object SASSReference extends Reference[NvidiaHarness, Lookup.Sass] {
  private val lowered = TrieMap.empty[(Int, String), Option[InstructionSemantics.SASSLowering]]

  private def lower(sm: Int >= 35, instance: String, operands: Map[String, String])
      : Option[InstructionSemantics.SASSLowering] = for {
    program  <- Parser.parseProgram(s"$instance;").toOption
    parsed   <- program.statements.collectFirst { case Program.Statement.Operation(operation, _, _) => operation }
    lowering <- InstructionSemantics.lowered(sm, parsed, operands, Map.empty).toOption
  } yield lowering

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
      _ <- FormatParser.parse(database, found, 0, text)
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
  override def inputs(
      target: Option[String],
      instance: String,
      lookup: Lookup.Sass,
      parameters: Vec[(String, Type)],
      sampled: Vec[Long],
      random: Random
  ): Vec[Long] = parameters.zip(sampled).map {
    case ((name, Type.Ranged(_, intervals)), value)
        if lookup.constants.exists(_.binding == name) && random.nextInt(8) != 0 =>
      val stride = lookup.written.stride(name)
      val floor  = value - Math.floorMod(value, stride)
      Vec(floor, floor + stride).find(aligned => intervals.exists((low, high) => aligned >= low && aligned <= high))
        .getOrElse(value)
    case (_, value) => value
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
    _        <- if (offsets.isEmpty) Right(()) else lookup.written.encodes(offsets)
    lowering <- lowered.getOrElseUpdate((selected.sm, instance), lower(selected.sm, instance, lookup.operands))
      .toRight(s"$instance does not lower on ${selected.name}")
    bank =
      SASSConstantBank(harness, lookup.constants.flatMap((binding, offset) => offsets.get(binding).map(offset -> _)))
    values <- NvidiaWarpgroups.product(lowering.steps).fold(
      NvidiaMatrices.product(lowering.steps).fold(single(lowering, parameters, inputs, results, bank))(multiply =>
        NvidiaMatrices.run(
          lowering.prologue,
          lowering.steps,
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
        lowering.steps,
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
      bank: SASSConstantBank
  ): Either[String, Vec[Long]] = {
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
        lowering.prologue,
        lowering.steps,
        lowering.registerCount,
        lowering.predicateCount,
        sources.zip(operands).map { case (slot, ((_, tpe), value)) => (slot, tpe, value) },
        targets.zip(results.map(_._2)),
        bank
      )
    } yield values
  }

  def state(harness: NvidiaHarness): Long = harness.seed
}
