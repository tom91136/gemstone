package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.semantics.render.*
import scala.collection.concurrent.TrieMap

/** PTX instances lowered as the explorer describes them, with every register declared as its PTX type. */
object PTXReference extends Reference[NvidiaHarness, Lookup.Registers] {
  private val lowered = TrieMap.empty[String, Option[InstructionSemantics.PTXLowering]]

  def harness(seed: Long): NvidiaHarness = NvidiaHarness(seed)

  def execute(
      target: Option[String],
      instance: String,
      lookup: Lookup.Registers,
      parameters: Vec[(String, Type)],
      inputs: Vec[Long],
      results: Vec[(String, Type)],
      harness: NvidiaHarness
  ): Either[String, Vec[Long]] = for {
    found <- lowered.getOrElseUpdate(instance, InstructionSemantics.lowering(instance, lookup.declared).toOption)
      .toRight(s"$instance does not lower")
    values <- NvidiaTensorMemory.product(found.steps).map(multiply =>
      NvidiaTensorMemory.run(
        found.steps,
        found.routine.registerCount,
        found.routine.predicateCount,
        multiply,
        parameters,
        inputs,
        results,
        harness.seed,
        DeviceMemory(0)
      )
    ).getOrElse(NvidiaWarpgroups.product(found.steps).fold(
      NvidiaMatrices.product(found.steps).fold(single(found, parameters, inputs, results))(multiply =>
        NvidiaMatrices.run(
          Vec.empty,
          found.steps,
          found.routine.registerCount,
          found.routine.predicateCount,
          multiply,
          parameters,
          inputs,
          results,
          harness.seed,
          DeviceMemory(0)
        )
      )
    )(warpgroup =>
      NvidiaWarpgroups.run(
        Vec.empty,
        found.steps,
        found.routine.registerCount,
        found.routine.predicateCount,
        warpgroup,
        name =>
          found.routine.predicateNames.indexWhere(_.raw == name) match {
            case -1    => None
            case index => Some(VMIR.Predicate(index.unsafe[Int >= 0]))
          },
        parameters,
        inputs,
        results,
        harness.seed,
        DeviceMemory(0)
      )
    ))
  } yield values

  private def single(
      found: InstructionSemantics.PTXLowering,
      parameters: Vec[(String, Type)],
      inputs: Vec[Long],
      results: Vec[(String, Type)]
  ): Either[String, Vec[Long]] = {
    val locate = (name: String) =>
      found.routine.registerNames.indexWhere(_.raw == name) match {
        case -1 =>
          found.routine.predicateNames.indexWhere(_.raw == name) match {
            case -1    => None
            case index => Some(NvidiaReference.Slot.Predicate(VMIR.Predicate(index.unsafe[Int >= 0])))
          }
        case index => Some(NvidiaReference.Slot.Registers(Vec(VMIR.Register(index.unsafe[Int >= 0]))))
      }
    for {
      sources <- NvidiaReference.slots(parameters.map(_._1), locate)
      targets <- NvidiaReference.slots(results.map(_._1), locate)
      values  <- NvidiaReference.run(
        Vec.empty,
        found.steps,
        found.routine.registerCount,
        found.routine.predicateCount,
        sources.zip(parameters).zip(inputs).map { case ((slot, (_, tpe)), value) => (slot, tpe, value) },
        targets.zip(results.map(_._2)),
        DeviceMemory(0)
      )
    } yield values
  }

  def state(harness: NvidiaHarness): Long = harness.seed
}
