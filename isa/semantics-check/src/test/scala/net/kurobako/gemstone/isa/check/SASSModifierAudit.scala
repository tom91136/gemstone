package net.kurobako.gemstone.isa.check

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.Instruction
import net.kurobako.gemstone.isa.sass.fixtures.Fixture
import scala.collection.concurrent.TrieMap

/** Writes, for each architecture the committed fixtures compile for, every mnemonic, modifier combination and operand
  * kinds that the fixtures use or an instance has, as a TSV of target, opcode, kinds, state, fixture uses and an
  * example, to the file given: `covered` where an instance with that opcode and those kinds has a checked case, `case`
  * where only an instance does, and where only the fixtures do, `rides` where the instruction is admitted on the cases
  * of its operand kinds alone, or `refused` with the reason it is not. After them, a row of state `dropped` for each
  * reason the model dropped rows of a mnemonic, with their number in place of the fixture uses. Further arguments limit
  * the architectures, as `sm_89`.
  */
object SASSModifierAudit {

  /** An instruction's mnemonic and modifiers, the modifiers in order, and the kinds of its operands. */
  type Shape = (opcode: Vec[String], kinds: Vec[String])

  def shape(instruction: Instruction): Shape =
    (
      instruction.mnemonic.raw +: instruction.modifiers.map(_.spelling.raw).sorted,
      SASSForms.kinds(instruction)
    )

  private val Record = raw"\s*/\*[0-9a-f]{4}\*/\s*\{?\s*([^;]*);.*".r
  private val Target = raw".*Sm(\d+)".r

  /** The instructions of every fixture listing with the architecture it was compiled for, each with the listing it
    * comes from; those that do not parse, such as branches to labels, are left out.
    */
  def used: Vec[(sm: Int, instruction: Instruction, text: String, listing: String)] = Fixture.names.flatMap { name =>
    name match {
      case Target(sm) =>
        Fixture.listing(name).linesIterator.toVec.collect { case Record(text) => text.trim }.flatMap(text =>
          SASSForms.parsed(text).map(instruction => (sm.toInt, instruction, text, name))
        )
      case _ => Vec.empty
    }
  }

  /** The shapes of the instances of `mnemonic` on `target`, each checked in the case of its definition, and the reason
    * of each row the model could not write or describe.
    */
  def checked(target: String, mnemonic: String): (shapes: Set[Shape], dropped: Vec[String]) =
    swept.getOrElseUpdate(
      (target, mnemonic), {
        val (instances, dropped) = SASSGenerator.sweep(target, _ == mnemonic)
        (instances.flatMap(found => SASSForms.parsed(found.kase.instance)).map(shape(_)).toSet, dropped)
      }
    )

  private val swept = TrieMap.empty[(String, String), (shapes: Set[Shape], dropped: Vec[String])]

  /** The number of rows dropped for each reason, by target and mnemonic, for the architectures `only` names, all where
    * it is empty.
    */
  def dropped(only: Set[String]): Vec[(target: String, mnemonic: String, reason: String, count: Int)] =
    used.filter(value => only.isEmpty || only(s"sm_${value.sm}"))
      .map(value => (s"sm_${value.sm}", value.instruction.mnemonic.raw)).distinct.sorted.flatMap((target, mnemonic) =>
        checked(target, mnemonic).dropped.groupBy(identity).toVec.map((reason, rows) =>
          (target, mnemonic, reason, rows.size)
        ).sortBy(_._3)
      )

  /** The rows of the audit for the architectures `only` names, all where it is empty. */
  def rows(only: Set[String]): Vec[Vec[String]] = {
    val found = used.filter(value => only.isEmpty || only(s"sm_${value.sm}"))
    found.groupBy(_.sm).toVec.sortBy(_._1).flatMap { (sm, instructions) =>
      val target = s"sm_$sm"
      instructions.groupBy(_.instruction.mnemonic.raw).toVec.sortBy(_._1).flatMap { (mnemonic, uses) =>
        val cases   = checked(target, mnemonic).shapes
        val counted = uses.groupBy(value => shape(value.instruction))
        val shapes  = (cases ++ counted.keySet).toVec.sortBy((opcode, kinds) => (opcode.mkString("."), kinds.mkString))
        shapes.map { form =>
          val seen  = counted.get(form)
          val state = seen.fold("case")(found =>
            if (cases(form)) "covered"
            else
              SASSForms.of(target, found.head.instruction.copy(guard = None))
                .fold(reason => s"refused: $reason", _ => "rides")
          )
          Vec(
            target,
            form.opcode.mkString("."),
            form.kinds.mkString(" "),
            state,
            seen.fold(0)(_.size).toString,
            seen.flatMap(_.headOption).fold("")(value => s"${value.text} (${value.listing})")
          )
        }
      }
    }
  }

  def main(arguments: Array[String]): Unit = {
    val only  = arguments.drop(1).toSet
    val table =
      (rows(only) ++ dropped(only).map((target, mnemonic, reason, count) =>
        Vec(target, mnemonic, "", "dropped", count.toString, reason)
      )).map(_.map(_.replace('\t', ' ')).mkString("\t"))
    val _ = Files.write(Paths.get(arguments(0)), table.mkString("", "\n", "\n").getBytes(StandardCharsets.UTF_8))
  }
}
