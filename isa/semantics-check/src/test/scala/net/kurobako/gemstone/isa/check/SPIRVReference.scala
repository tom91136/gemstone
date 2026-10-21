package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*
import scala.collection.concurrent.TrieMap

/** Runs an instance as the only instruction of a one-invocation kernel, or a shader where its semantics are a shader's,
  * with its operands loaded from 64-bit slots and its results stored to them. A pointer operand points into the
  * harness' cells at its value, or is converted from its value where only its address matters.
  */
object SPIRVReference extends Reference[SPIRVHarness, Lookup.Mnemonic] {
  import SPIRVHarness.*
  import SPIRVKernel.*

  def harness(seed: Long): SPIRVHarness = SPIRVHarness(seed)

  def state(harness: SPIRVHarness): Vec[(Long, Byte)] = harness.changes

  private val programs = TrieMap.empty[String, Either[String, Program]]

  private case class Program(
      loaded: Backend.Program,
      shader: Boolean,
      parameters: Vec[Parameter],
      results: Vec[String]
  )

  def execute(
      target: Option[String],
      instance: String,
      lookup: Lookup.Mnemonic,
      parameters: Vec[(String, Type)],
      inputs: Vec[Long],
      results: Vec[(String, Type)],
      harness: SPIRVHarness
  ): Either[String, Vec[Long]] = for {
    found   <- SPIRVInstances.instances(lookup.name).findLast(_.text == instance).toRight(s"no instance $instance")
    program <- programs.getOrElseUpdate(instance, load(found))
    values = parameters.map(_._1).zip(inputs).toMap
    slots  = program.parameters.collect { case Parameter.Slot(name) => values.getOrElse(name, 0L) }
    task <- task(program, values, slots, harness)
    _ <- program.loaded.execute(task, TaskContext(harness, TaskControl(), _ => Right(()))).left.map(_.description.raw)
  } yield {
    val base = if (program.shader) slots.size else 0
    results.map { (name, tpe) =>
      val bits = harness.slot(base + program.results.indexOf(name))
      tpe.represent(bits)
    }
  }

  private def task(program: Program, values: Map[String, Long], slots: Vec[Long], harness: SPIRVHarness)
      : Either[String, KernelTask] = {
    val arguments =
      if (program.shader) {
        slots.zipWithIndex.foreach((value, index) => harness.slot(index, value))
        Vec(KernelTask.Argument.Memory(ExchangeRegion, 0L))
      } else
        KernelTask.Argument.Memory(ExchangeRegion, 0L) +: program.parameters.map {
          case Parameter.Slot(name) =>
            KernelTask.Argument.Scalar(LittleEndian.toVec(values.getOrElse(name, 0L), 8).unsafe[NonEmpty[Vec[Byte]]])
          case Parameter.Pointer(name) =>
            KernelTask.Argument.Memory(CellRegion, (values.getOrElse(name, 0L) & (Cells - 1)).unsafe[Long >= 0])
        }
    KernelTask.validated(
      "main",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      arguments
    ).left.map(_.raw)
  }

  private def load(instance: Instance): Either[String, Program] = {
    val module  = SPIRVKernel(instance)
    val backend = SPIRVBackend(
      if (module.shader) Validation.Environment.VulkanCompute else Validation.Environment.OpenCLCompute
    )
    for {
      parsed <- asm.Parser.parse(module.text).left.map(error => s"${error.diagnostic.raw} in\n${module.text}")
      bytes  <- ISA.encode(parsed).left.map(_.message.raw)
      loaded <- backend.load(bytes).left.map(fault => s"${fault.description.raw} in\n${module.text}")
    } yield Program(loaded, module.shader, module.parameters, module.results)
  }
}
