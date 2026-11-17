package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.RegisterUse
import scala.concurrent.duration.*

/** The registers [[RegisterUses]] reports for every instance the explorer writes, against those the rendered semantics
  * read and write, on the first target of each ISA. The rendering omits an operand whose value it folds away, as a
  * comparison that is always true does its sources, and cannot name a register M0 offsets, so the report may name more
  * than the rendering but not fewer.
  */
class RegisterUseSuite extends Suite {
  override def baseTimeout: FiniteDuration = 15.minutes

  private val Register = raw"([vsa])(\d+)".r
  private val Range    = raw"([vsa])\[(\d+):(\d+)\]".r

  private def dwords(names: Iterable[String]): Set[String] = {
    def span(bank: String, from: Int, to: Int) =
      if (bank == "s" && from >= Fixed.VccLo) Nil else (from to to).map(bank + _)
    names.flatMap {
      case Register(bank, index)    => span(bank, index.toInt, index.toInt)
      case Range(bank, first, last) => span(bank, first.toInt, last.toInt)
      case _                        => Nil
    }.toSet
  }

  // MODE is read by every floating-point instruction and written only by instructions with no definition, which no
  // window contains, so it is constant in every window
  private val tracked: Set[String] = ImplicitState.names.filterNot(_ == "mode").toSet

  private def states(names: Iterable[String]): Set[String] =
    names.map(name => name.stripSuffix("_lo").stripSuffix("_hi")).filter(tracked.contains).toSet

  private case class Difference(target: Target, text: String, side: String, missing: Set[String], extra: Set[String])

  private def decoded(target: Target, text: String): Either[String, (VMIR.Instruction, RegisterUse)] = for {
    program     <- asm.Parser.parse(text).left.map(_.diagnostic.raw)
    code        <- asm.Assembler.encode(program, TargetMode(target)).left.map(_.toString)
    instruction <- VMCompiler.decodeInstruction(code, target).left.map(_.raw)
  } yield (
    instruction,
    RegisterUses.state(
      instruction,
      CodeStream(Vec.toArray(code), TargetMode(target)),
      0,
      TargetDefaults.waveSize(target),
      target.isa
    )
  )

  private def representatives: Vec[Target] =
    Target.all.groupBy(_.isa).toVec.sortBy(_._1.toString).map(_._2.head)

  private def differences(target: Target): Vec[Difference] = {
    val found = Vec.newBuilder[Difference]
    AMDGPUModel.entries(Some(target.raw)).foreach(entry =>
      entry.forms.foreach { form =>
        AMDGPUModel.instances(Some(target.raw), entry.mnemonic, form).foreach { (instanceTarget, text, _) =>
          for {
            definition           <- InstructionSemantics.describe(instanceTarget, text, Map.empty).toOption
            (instruction, state) <- decoded(instanceTarget, text).toOption
          } {
            val use = RegisterUses.of(instruction, TargetDefaults.waveSize(instanceTarget))
            def compare(side: String, reported: Vec[String], rendered: Vec[String]): Unit = {
              val (left, right) = (dwords(reported), dwords(rendered))
              if (left != right) found += Difference(instanceTarget, text, side, right -- left, left -- right)
            }
            compare("reads", use.reads, definition.parameters.map(_.name))
            compare("writes", use.writes, definition.results.map(_.name))
            def implicitly(side: String, reported: Vec[String], rendered: Vec[String]): Unit = {
              val (left, right) = (states(reported), rendered.filter(tracked.contains).toSet)
              if (!right.subsetOf(left)) found += Difference(instanceTarget, text, side, right -- left, Set.empty)
            }
            implicitly("implicit reads", state.reads, definition.parameters.map(_.name))
            implicitly("implicit writes", state.writes, definition.results.map(_.name))
          }
        }
      }
    )
    found.result()
  }

  private def show(difference: Difference): String =
    s"${difference.target.raw} `${difference.text}` ${difference.side} unreported ${difference.missing.toSeq.sorted}"

  test("every register the rendered semantics reads or writes is reported") {
    val all     = representatives.flatMap(differences(_))
    val missing = all.filter(_.missing.nonEmpty)
    println(s"register use: ${representatives.size} targets, ${all.size} differing, ${missing.size} unreported")
    val examples = missing.groupBy(_.text.takeWhile(!_.isWhitespace)).toSeq.sortBy(_._1).map(_._2.head)
    assert(missing.isEmpty, examples.map(show(_)).mkString(s"${examples.size} mnemonics:\n", "\n", ""))
  }
}
