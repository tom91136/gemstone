package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.testing.Suite
import scala.concurrent.duration.*
import scala.io.Codec
import scala.io.Source
import scala.util.Using
import scala.util.matching.Regex

class CoverageSuite extends Suite {
  // classifying every target of every ISA takes about six minutes on a 32-core host
  override def baseTimeout: FiniteDuration = 15.minutes
  private val lanes                        = Set("Lane", "Machine", "Both", "LoadTime")
  private val exceptions                   = Set("NoOp", "NotModelled", "NotImplemented", "NotInstantiated")
  private val ratchetPath                  = "explorer/src/test/resources/coverage/ratchet.tsv"

  private case class Classified(isa: String, target: Option[String], mnemonic: String, name: String, actual: String) {
    def label: String = s"$isa ${target.getOrElse("-")} $mnemonic $name"
  }

  private case class Rule(isa: String, target: Regex, mnemonic: Regex, form: Regex, expected: String, reason: String)

  private case class Ratchet(isa: String, actual: String, limit: Int)

  private def table(name: String): Vec[Vec[String]] = {
    val stream = Option(getClass.getResourceAsStream(s"/coverage/$name")).getOrElse(fail(s"missing coverage/$name"))
    Using.resource(Source.fromInputStream(stream)(using Codec.UTF8)) { source =>
      Vec.from(source.getLines().drop(1).map(_.split('\t').toVec))
    }
  }

  private def classOf(semantics: Semantics): String = semantics match {
    case Semantics.Lane(_)             => "Lane"
    case Semantics.Machine(_)          => "Machine"
    case Semantics.Both(_, _)          => "Both"
    case Semantics.NoOp(_)             => "NoOp"
    case Semantics.LoadTime(_)         => "LoadTime"
    case Semantics.Unavailable(reason) => unavailableClass(reason)
  }

  private def unavailableClass(reason: Unavailable): String = reason match {
    case Unavailable.Mechanics(_)       => "Mechanics"
    case Unavailable.NotModelled(_)     => "NotModelled"
    case Unavailable.NotImplemented(_)  => "NotImplemented"
    case Unavailable.NotInstantiated(_) => "NotInstantiated"
    case Unavailable.Unclassified       => "Unclassified"
  }

  private def walk(language: Language): Vec[Option[String]] = {
    val model = language.model
    if (model.targets.isEmpty) Vec(None) else model.targets.map(target => Some(target))
  }

  // same-named forms of an entry are told apart by their order in it, as name#2 from the second on
  private def named(forms: Vec[View.Form]): Vec[(String, View.Form)] =
    forms.zipWithIndex.map { (form, index) =>
      val ordinal = forms.take(index).count(_.name == form.name) + 1
      (if (ordinal == 1) form.name else s"${form.name}#$ordinal", form)
    }

  private def classifyTarget(language: Language, target: Option[String]): Vec[Classified] = {
    val model = language.model
    val forms = model.entries(target).flatMap(entry =>
      named(entry.forms).map((name, form) =>
        Classified(language.toString, target, entry.mnemonic, name, classOf(model.semanticsOf(entry, form)))
      )
    )
    forms
  }

  private lazy val classified: Vec[Classified] =
    Language.values.toVec.flatMap(language => walk(language).flatMap(classifyTarget(language, _)))

  private lazy val rules: Vec[Rule] =
    table("policy.tsv").map(row =>
      Rule(isa = row(0), target = row(1).r, mnemonic = row(2).r, form = row(3).r, expected = row(4), reason = row(5))
    )

  private lazy val ratchet: Vec[Ratchet] = table("ratchet.tsv").map(row => Ratchet(row(0), row(1), row(2).toInt))

  private def covers(rule: Rule, form: Classified): Boolean =
    rule.isa == form.isa && rule.target.matches(form.target.getOrElse("")) && rule.mnemonic.matches(form.mnemonic) &&
      rule.form.matches(form.name)

  private def ruleFor(form: Classified): Option[Rule] = rules.find(covers(_, form))

  test("policy rows expect an exception class and give a reason") {
    val invalid = rules.collect {
      case rule if !exceptions(rule.expected) =>
        s"${rule.isa} ${rule.mnemonic}: expects ${rule.expected}, which is not an exception"
      case rule if rule.reason.trim.isEmpty => s"${rule.isa} ${rule.mnemonic}: no reason"
    }
    assert(invalid.isEmpty, invalid.mkString("\n"))
  }

  test("each form has the class of its first matching policy row") {
    val mismatches = classified.flatMap(form =>
      ruleFor(form)
        .filter(_.expected != form.actual)
        .map(rule => s"${form.label}: expected ${rule.expected}, got ${form.actual}")
    )
    assert(mismatches.isEmpty, mismatches.mkString("\n"))
  }

  test("forms outside the lane, machine, both and load-time classes have a policy row") {
    val unruled = classified.filter(form => !lanes(form.actual) && ruleFor(form).isEmpty)
    assert(unruled.isEmpty, unruled.map(form => s"${form.label}: ${form.actual}").mkString("\n"))
  }

  test("every policy row matches a form on some target") {
    val stale = rules.filterNot(rule => classified.exists(covers(rule, _)))
    assert(stale.isEmpty, stale.map(rule => s"${rule.isa} ${rule.mnemonic} ${rule.form}").mkString("\n"))
  }

  test("class counts stay within the ratchet") {
    val counts = ratchet.map(entry =>
      (entry, classified.count(form => form.isa == entry.isa && form.actual == entry.actual))
    )
    val exceeded = counts.collect {
      case (entry, count) if count > entry.limit => s"${entry.isa} ${entry.actual}: $count, ratchet ${entry.limit}"
    }
    counts.foreach { (entry, count) =>
      if (count < entry.limit)
        println(s"ratchet: lower ${entry.isa} ${entry.actual} from ${entry.limit} to $count in $ratchetPath")
    }
    assert(exceeded.isEmpty, exceeded.mkString("\n"))
  }
}
