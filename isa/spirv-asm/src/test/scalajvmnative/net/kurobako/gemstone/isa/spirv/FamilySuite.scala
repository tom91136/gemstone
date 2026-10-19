package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*
import scala.util.control.NonFatal

class FamilySuite extends munit.FunSuite {
  private val ids      = 1 to 16
  private val instance = IdRenderer.Instance(
    ids.map(id => id -> s"x$id").toMap,
    ids.map(_ -> Shape.Scalar(Type.Float(32))).toMap
  )
  private val sinks                        = IdRenderer(instance, Scope(instance))
  private val declined: Option[Term.Steps] = Some(Term.Steps.empty)

  private val resultKinds = Vec(OperandKind.IdResultType, OperandKind.IdResult)

  /** Every instruction with a result type, a result and an id operand, its ids numbered from 1 and other words 0. */
  private val core: Vec[Instruction] = Instruction.definitions
    .filter(_.opcodeNumber.toInt != SPIRV.Opcode.ExtInst)
    .flatMap { definition =>
      val words = definition.shapes.filter(_.quantifier == OperandQuantifier.One).zipWithIndex.map((shape, index) =>
        Word(if (shape.kind.category == OperandKind.Category.Id) index + 1 else 0)
      )
      definition.decoder(RawInstruction(definition.opcodeNumber, words)).toOption
    }
    .filter(instruction =>
      instruction.definition.shapes.take(2).map(_.kind) == resultKinds &&
        SPIRVStaged.operandIds(instruction).operands.nonEmpty
    )

  private def extended(set: ExtendedSet): Vec[(String, Instruction)] =
    Vec.from(ExtendedInstruction.names(set.name)).map { (name, encoded) =>
      val definition = Instruction.Definition.ExtInst
      val words      = Vec(1, 2, 3, encoded, 4, 5, 6, 7, 8).map(Word(_))
      name ->
        definition.decoder(RawInstruction(definition.opcodeNumber, words)).fold(error => fail(error.message), identity)
    }

  /** The families that render rather than defer, a family that throws counting as one that renders. A family that
    * indexes an operand or member the instance lacks throws, which Scala.js leaves undefined.
    */
  private def claimants[F](families: Array[F])(render: F => Option[Term.Steps]): Seq[F] =
    families.toSeq.filter(family =>
      try !(render(family) eq declined)
      catch { case NonFatal(_) => true }
    )

  private def overlaps[F](instructions: Vec[(String, Instruction)], families: Array[F])(
      render: (F, Instruction) => Option[Term.Steps]
  ): (claimed: Int, shared: Map[String, Seq[F]]) = {
    val found = instructions.map((name, instruction) => name -> claimants(families)(render(_, instruction)))
    (found.count(_._2.nonEmpty), found.filter(_._2.size > 1).toMap)
  }

  test("no two core families take one opcode") {
    val found = overlaps(core.map(instruction => instruction.definition.opcode.raw -> instruction), CoreFamily.values)(
      (family, instruction) => SPIRVStaged.core(family, Render, sinks, instruction)(declined)
    )
    assertEquals(found.shared, Map.empty[String, Seq[CoreFamily]])
    assert(found.claimed > 100, found.claimed)
  }

  test("no two GLSL.std.450 families take one extended instruction") {
    val found = overlaps(extended(ExtendedSet.GLSL), GLSLFamily.values)((family, instruction) =>
      SPIRVStaged.glsl(family, Render, sinks, instruction)(declined)
    )
    assertEquals(found.shared, Map.empty[String, Seq[GLSLFamily]])
    assert(found.claimed > 60, found.claimed)
  }

  test("no two OpenCL.std families take one extended instruction") {
    val found = overlaps(extended(ExtendedSet.OpenCL), OpenCLFamily.values)((family, instruction) =>
      SPIRVStaged.openCL(family, Render, sinks, instruction)(declined)
    )
    assertEquals(found.shared, Map.empty[String, Seq[OpenCLFamily]])
    assert(found.claimed > 140, found.claimed)
  }
}
