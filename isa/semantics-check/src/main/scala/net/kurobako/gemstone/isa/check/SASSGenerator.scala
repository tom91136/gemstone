package net.kurobako.gemstone.isa.check

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.semantics.render.*
import scala.util.Using

/** Every SASS instance the explorer describes on a representative target of each architecture generation, checked
  * against executing it.
  */
object SASSGenerator {

  private val counters = Vec("SR_CLOCK", "SR_GLOBALTIMER")

  /** The guards a form is checked under, as written before the predicate's number: a predicate, and one negated. */
  val guards: Vec[String] = Vec("@P", "@!P")

  private[check] type Described = (found: SASSModel.Instance, mnemonic: String, form: String, kase: Case)

  private def described(target: String, found: SASSModel.Instance, mnemonic: String, form: String): Option[Described] =
    // counters are never staged into a checked definition
    SASSModel.definition(found, mnemonic, form).toOption.filter(described =>
      NvidiaCases.global(described) && !counters.exists(found.text.contains)
    ).map(described =>
      (
        found,
        mnemonic,
        form,
        Case(found.text, Some(target), described, Lookup.Sass(found.operands, found.constants, found.written))
      )
    )

  /** Each definition `target` describes once, but those that access memory other than by plain loads and stores of
    * global memory, with the first instance that lowers to it. Only the entries `included` holds are described.
    */
  private[check] def plain(target: String, included: String => Boolean): Vec[Described] =
    SASSModel.entries(Some(target)).filter(entry => included(entry.mnemonic)).flatMap(entry =>
      entry.forms.flatMap(form =>
        SASSModel.instances(Some(target), entry.mnemonic, form.name).toOption.toVec.flatten
          .flatMap(described(target, _, entry.mnemonic, form.name))
      )
    ).distinctBy(value => ScalaBackend.show(value.kase.definition))

  /** The first of `form`, the cases of one mnemonic and operand kinds, that lowers under `guard`, on the last predicate
    * its instance does not name: instances number theirs from `P0`. The reference runs the elements of a matrix product
    * with no guard, so a product has no such case.
    */
  private[check] def guarded(target: String, form: Vec[Described], guard: String): Option[Case] =
    form.iterator.flatMap { (found, mnemonic, name, _) =>
      Vec.tabulate(7)(index => s"$guard${6 - index}").find(spelt => !found.text.contains(spelt.dropWhile(_ != 'P')))
        .flatMap(spelt =>
          described(
            target,
            (found.sm, s"$spelt ${found.text}", found.operands, found.order, found.constants, found.written),
            mnemonic,
            name
          )
        ).map(_.kase).filterNot(kase => NvidiaCases.product(kase.definition))
    }.nextOption()

  /** The cases of [[plain]] and, after them, each of [[guards]] on each of their mnemonics and operand kinds: a guard
    * is the same select whatever the modifiers.
    */
  def describe(target: String, included: String => Boolean = _ => true): Vec[Case] = {
    val plain = this.plain(target, included)
    val forms = plain.flatMap(value =>
      SASSForms.parsed(value.kase.instance)
        .map(parsed => (value.mnemonic, parsed.mnemonic.raw, SASSForms.kinds(parsed)) -> value)
    )
    val grouped = forms.groupMap(_._1)(_._2)
    plain.map(_.kase) ++
      forms.map(_._1).distinct.flatMap(form => guards.flatMap(guarded(target, grouped(form), _)))
  }

  /** The architectures checked, one project each, in the file the build derives those projects from. */
  lazy val architectures: Vec[String] =
    Using.resource(getClass.getResourceAsStream("sass-architectures.txt"))(stream =>
      Vec.from(String(stream.readAllBytes(), StandardCharsets.UTF_8).linesIterator.map(_.trim).filter(_.nonEmpty))
    )

  /** The cases of `target`, compiled on their own as every architecture's together outgrow the build's heap. */
  def of(target: String): Generator = new Generator {
    def sources: Vec[(name: String, source: String)] =
      NvidiaCases.sources(s"SASS${target.stripPrefix("sm_")}x", "SASSSuite", describe(target))
  }
}
