package net.kurobako.gemstone.isa.check

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
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

  private def described(
      target: String,
      found: SASSModel.Instance,
      mnemonic: String,
      form: String
  ): Either[String, Described] =
    // counters are never staged into a checked definition
    SASSModel.definition(found, mnemonic, form).left.map(_.message).flatMap(described =>
      Either.cond(
        NvidiaCases.global(described) && !counters.exists(found.text.contains),
        (
          found,
          mnemonic,
          form,
          Case(found.text, Some(target), described, Lookup.Sass(found.operands, found.constants, found.written))
        ),
        "is not a plain access of global memory"
      )
    )

  private val Digits = "\\d+".r

  private def cause(reason: String): String = reason.substring(reason.lastIndexOf(": ") match {
    case -1 => 0
    case at => at + 2
  })

  /** Every instance `target` describes, but those that access memory other than by plain loads and stores of global
    * memory, with the reason of each row it could not write or describe. Only the entries `included` holds are
    * described.
    */
  private[check] def sweep(
      target: String,
      included: String => Boolean
  ): (instances: Vec[Described], dropped: Vec[String]) = {
    val swept = SASSModel.entries(Some(target)).filter(entry => included(entry.mnemonic)).flatMap(entry =>
      entry.forms.map { form =>
        val (written, alternates, dropped) = SASSModel.report(Some(target), entry.mnemonic, form.name, pairs = true)
          .getOrElse((Vec.empty[SASSModel.Instance], Vec.empty[SASSModel.Instance], Vec.empty[String]))
        def describe(instances: Vec[SASSModel.Instance]) =
          instances.map(described(target, _, entry.mnemonic, form.name))
        val first   = describe(written)
        val opcodes = first.flatMap(_.toOption).map(_.found.text.takeWhile(_ != ' ')).toSet
        // an opcode no instance of lowers is tried again with its defaulted predicates written
        val rest = describe(alternates.filterNot(found => opcodes(found.text.takeWhile(_ != ' '))))
        val kept = rest.flatMap(_.toOption).distinctBy(_.found.text.takeWhile(_ != ' '))
        (
          first.flatMap(_.toOption) ++ kept,
          (dropped.map(_.replace(form.name, "<form>")) ++ (first ++ rest).flatMap(_.left.toOption).map(cause(_)))
            .map(Digits.replaceAllIn(_, "N"))
        )
      }
    )
    (swept.flatMap(_._1), swept.flatMap(_._2))
  }

  private[check] def instances(target: String, included: String => Boolean): Vec[Described] =
    sweep(target, included).instances

  /** Each definition `target` describes once, with the first of the [[instances]] that lowers to it. */
  private[check] def plain(target: String, included: String => Boolean): Vec[Described] =
    instances(target, included).distinctBy(value => ScalaBackend.show(value.kase.definition))

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
          ).toOption
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

  /** The architectures checked and the number of shards each splits into, as the file the build derives its projects
    * from names them.
    */
  lazy val architectures: Vec[(target: String, shards: Int)] =
    Using.resource(getClass.getResourceAsStream("sass-architectures.txt"))(stream =>
      Vec.from(String(stream.readAllBytes(), StandardCharsets.UTF_8).linesIterator.map(_.trim).filter(_.nonEmpty)).map {
        line =>
          val fields = line.split("\\s+")
          (fields(0), fields.drop(1).headOption.fold(1)(_.toInt))
      }
    )

  /** The name the project of `shard` of `shards` of `target` is known by, as the build spells it. */
  def name(target: String, shard: Int, shards: Int): String =
    s"SASS${target.stripPrefix("sm_")}${if (shards == 1) "" else s"s$shard"}"

  /** The cases of the mnemonics of `shard` of `shards` of `target`, each shard compiled on its own as together they
    * outgrow the build's heap.
    */
  def of(target: String, shard: Int, shards: Int): Generator = new Generator {
    def sources: Vec[(name: String, source: String)] =
      NvidiaCases.sources(
        s"${name(target, shard, shards)}x",
        "SASSSuite",
        describe(target, mnemonic => Math.floorMod(mnemonic.hashCode, shards) == shard)
      )
  }
}
