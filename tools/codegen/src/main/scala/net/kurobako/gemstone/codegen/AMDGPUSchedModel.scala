package net.kurobako.gemstone.codegen

import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern
import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.matching.Regex

object AMDGPUSchedModel {
  private val NoModel   = "NoSchedModel"
  private val RegexMeta = "()^$|*+?.[]\\{}"
  private val Variant   = """(?<![$\\])\{([^{}]*)\}""".r
  private val Head      = """[^\s$]*""".r
  private val LdsForm   = """\blds\b""".r

  case class Entry(latency: Int, cycles: Int, writes: String)

  case class Processor(
      name: String,
      model: String,
      outcomes: Map[String, Either[String, Entry]],
      ldsOutcomes: Map[String, Either[String, Entry]]
  )

  private case class Write(latency: Int, resources: Vector[(String, Int)], unsupported: Boolean)

  private case class Info(name: String, write: Option[Write], variant: Boolean)

  private case class Scheduled(status: String, infos: Vector[Info])

  private object Matcher {
    case class Entry(matcher: Matcher, writes: Vector[String])
  }

  private enum Matcher derives CanEqual {
    case Names(names: Set[String])
    case Patterns(patterns: Vector[String])

    def matches(name: String): Boolean = this match {
      case Names(names)       => names(name)
      case Patterns(patterns) => patterns.exists(instregex(_, name))
    }
  }

  private val compiled = ConcurrentHashMap[String, Pattern]()

  private def truthy(value: ujson.Value): Boolean = value match {
    case ujson.Str(text)  => text.nonEmpty && text != "0"
    case ujson.Num(num)   => num != 0
    case flag: ujson.Bool => flag.value
    case ujson.Arr(arr)   => arr.nonEmpty
    case ujson.Obj(obj)   => obj.nonEmpty
    case _                => false
  }

  private def integer(value: ujson.Value): Int = value match {
    case ujson.Str(text) => text.toInt
    case other           => other.num.toInt
  }

  private def definition(value: ujson.Value): String = value("def").str

  private def mnemonic(asm: String): String = {
    @tailrec
    def flatten(text: String): String = {
      val next = Variant.replaceAllIn(text, found => Regex.quoteReplacement(found.group(1).takeWhile(_ != '|')))
      if (next == text) text else flatten(next)
    }
    Head.findPrefixOf(flatten(asm)).getOrElse("")
  }

  private def withoutParentheses(text: String): String =
    text.foldLeft((0, mutable.StringBuilder())) { case ((depth, out), char) =>
      if (char == '(') (depth + 1, out)
      else if (char == ')') (depth - 1, out)
      else if (depth == 0) (depth, out.append(char))
      else (depth, out)
    }._2.toString

  // llvm::Regex-style InstRW patterns: a literal prefix is checked before the regular expression is applied
  private def instregex(pattern: String, name: String): Boolean = {
    val anchored = pattern.startsWith("^")
    val original = if (anchored) pattern.drop(1) else pattern
    val meta     = original.indexWhere(RegexMeta.contains(_))
    val first    =
      if (withoutParentheses(original).exists(char => char == '|' || char == '?')) 0
      else if (meta > 0 && "+*?".contains(original(meta))) meta - 1
      else meta
    if (first < 0) name.startsWith(original)
    else {
      val prefix = original.take(first)
      val rest   = original.drop(first)
      val regex  = if (anchored) "^" + rest else "^(" + rest + ")"
      name.startsWith(prefix) &&
      compiled.computeIfAbsent(regex, Pattern.compile(_)).matcher(name.substring(prefix.length)).lookingAt()
    }
  }
}

/** LLVM's AMDGPU scheduling model: which write resources an instruction record resolves to on each processor. */
class AMDGPUSchedModel(dump: AMDGPUSchedRecords.Dump, catalogue: Vector[String]) {
  import AMDGPUSchedModel.*

  private val records = dump.records

  private def record(name: String): ujson.Obj =
    records.getOrElse(name, throw IllegalStateException(s"TableGen dump has no record $name"))

  private val real: Vector[String] = dump.classMembers("Instruction").filter { name =>
    val value = record(name)
    !truthy(value.obj.getOrElse("isPseudo", ujson.Null)) && !truthy(value.obj.getOrElse("isCodeGenOnly", ujson.Null)) &&
    truthy(value("AsmString"))
  }

  private val mnemonics: Map[String, String] = real.map(name => name -> mnemonic(record(name)("AsmString").str)).toMap

  private val known: Set[String] = mnemonics.values.toSet

  private val ldsRecords: Set[String] =
    real.filter(name => LdsForm.findFirstIn(record(name)("AsmString").str).isDefined).toSet

  private val writes: Map[(String, String), Write] = dump.classMembers("WriteRes").map { name =>
    val value     = record(name)
    val resources = value("ProcResources").arr.map(resource => definition(resource)).toVector
    val cycles    = value("ReleaseAtCycles").arr.map(integer(_)).toVector match {
      case Vector() => Vector.fill(resources.size)(1)
      case released => released
    }
    (definition(value("SchedModel")), definition(value("WriteType"))) ->
      Write(integer(value("Latency")), resources.zip(cycles), truthy(value("Unsupported")))
  }.toMap

  private val instrw: Map[String, Vector[Matcher.Entry]] = dump.classMembers("InstRW").map(record(_)).foldLeft(
    Map.empty[String, Vector[Matcher.Entry]]
  ) { (acc, value) =>
    val instrs  = value("Instrs")
    val args    = instrs("args").arr
    val matcher =
      if (definition(instrs("operator")) == "instrs") Matcher.Names(args.map(arg => definition(arg(0))).toSet)
      else Matcher.Patterns(args.map(arg => arg(0).str).toVector)
    val model = definition(value("SchedModel"))
    acc.updated(
      model,
      acc.getOrElse(model, Vector.empty) :+
        Matcher.Entry(matcher, value("OperandReadWrites").arr.map(definition(_)).toVector)
    )
  }

  private val units: Map[String, Int] =
    dump.classMembers("ProcResource").map(name => name -> integer(record(name)("NumUnits"))).toMap

  private val tables = mutable.HashMap.empty[String, Map[String, Matcher.Entry]]

  private def features(processor: String): Set[String] = {
    @tailrec
    def close(seen: Set[String], pending: List[String]): Set[String] = pending match {
      case Nil                        => seen
      case head :: tail if seen(head) => close(seen, tail)
      case head :: tail               =>
        close(seen + head, record(head)("Implies").arr.map(definition(_)).toList ++ tail)
    }
    close(Set.empty, record(processor)("Features").arr.map(definition(_)).toList)
  }

  private def gates(predicate: String): Boolean = truthy(record(predicate)("AssemblerMatcherPredicate"))

  private def condition(node: ujson.Value, enabled: Set[String]): Boolean = node match {
    case dag: ujson.Obj if dag.obj.get("kind").contains(ujson.Str("dag")) =>
      val values = dag("args").arr.map(arg => condition(arg(0), enabled)).toVector
      definition(dag("operator")) match {
        case "all_of"                  => values.forall(identity)
        case "any_of"                  => values.exists(identity)
        case "not" if values.size == 1 => !values.head
        case operator                  => throw IllegalStateException(s"unsupported assembler dag operator $operator")
      }
    case _ =>
      val name  = definition(node)
      val value = record(name)
      if (value("!superclasses").arr.exists(_.str == "SubtargetFeature")) enabled(name)
      else
        value.obj.get("AssemblerCondDag") match {
          case Some(sub: ujson.Obj) => condition(sub, enabled)
          case _ => throw IllegalStateException(s"predicate $name gates the assembler but has no AssemblerCondDag")
        }
  }

  private def available(name: String, enabled: Set[String], cache: mutable.HashMap[String, Boolean]): Boolean =
    record(name)("Predicates").arr.forall { predicate =>
      val gate = definition(predicate)
      !gates(gate) || cache.getOrElseUpdate(gate, condition(record(gate)("AssemblerCondDag"), enabled))
    }

  private def table(model: String): Map[String, Matcher.Entry] = tables.getOrElseUpdate(
    model, {
      val result = mutable.HashMap.empty[String, Matcher.Entry]
      instrw.getOrElse(model, Vector.empty).foreach { entry =>
        real.foreach { name =>
          if (!result.contains(name) && entry.matcher.matches(name)) result(name) = entry
        }
      }
      result.toMap
    }
  )

  private def schedule(model: String, name: String, table: Map[String, Matcher.Entry]): Scheduled = {
    val names = table.get(name).fold(record(name)("SchedRW").arr.map(definition(_)).toVector)(_.writes)
    val infos = names.flatMap { write =>
      records.get(write).map(_("!superclasses").arr.map(_.str)).flatMap { superclasses =>
        if (superclasses.contains("SchedWriteVariant")) Some(Info(write, None, variant = true))
        else if (superclasses.contains("SchedWrite")) Some(Info(write, writes.get((model, write)), variant = false))
        else None
      }
    }
    val status =
      if (model == NoModel) "no-model"
      else if (infos.isEmpty) "no-writes"
      else if (infos.exists(_.variant)) "variant"
      else if (infos.exists(_.write.isEmpty)) "unmapped"
      else if (infos.exists(_.write.exists(_.unsupported))) "unsupported"
      else "ok"
    Scheduled(status, infos)
  }

  private def entry(infos: Vector[Info]): Entry = {
    val resolved = infos.flatMap(_.write)
    val released = resolved.flatMap(_.resources).groupMapReduce(_._1)(_._2)(_ + _)
    val cycles   = released.map((resource, release) => (release + units(resource) - 1) / units(resource))
    Entry(
      resolved.map(_.latency).max,
      cycles.maxOption.getOrElse(0),
      infos.map(_.name).mkString("+")
    )
  }

  def processors: Vector[Processor] =
    dump.classMembers("Processor").map(name => processor(name)).groupBy(_.name).view.mapValues(_.last).toVector
      .sortBy(_._1).map(_._2)

  private def outcome(scheduled: Scheduled): Either[String, Entry] =
    if (scheduled.status == "ok") Right(entry(scheduled.infos)) else Left(scheduled.status)

  private def resolve(
      processor: String,
      model: String,
      mnemonic: String,
      lds: Boolean,
      candidates: Vector[String],
      table: Map[String, Matcher.Entry]
  ): Either[String, Entry] = {
    val outcomes = candidates.map(candidate => candidate -> outcome(schedule(model, candidate, table)))
    outcomes.map(_._2).distinct match {
      case Vector(single) => single
      case _              =>
        val form    = if (lds) s"$mnemonic lds" else mnemonic
        val records = outcomes.map((candidate, value) => s"$candidate $value").mkString("; ")
        throw IllegalStateException(s"AMDGPU schedule candidates disagree on $processor for $form: $records")
    }
  }

  private def processor(name: String): Processor = {
    val value        = record(name)
    val model        = definition(value("SchedModel"))
    val enabled      = features(name)
    val cache        = mutable.HashMap.empty[String, Boolean]
    val matched      = table(model)
    val (lds, plain) = real.filter(available(_, enabled, cache)).partition(ldsRecords)
    val byName       = plain.groupBy(mnemonics)
    val byForm       = lds.groupBy(mnemonics)
    val outcomes     = catalogue.flatMap { opcode =>
      if (!known(opcode)) Some(opcode -> Left("absent"))
      else
        byName.get(opcode).orElse(byForm.get(opcode)).map(candidates =>
          opcode -> resolve(name, model, opcode, lds = false, candidates, matched)
        )
    }.toMap
    val ldsOutcomes = catalogue.filter(byName.contains).flatMap(opcode =>
      byForm.get(opcode).map(candidates => opcode -> resolve(name, model, opcode, lds = true, candidates, matched))
    ).toMap
    Processor(value("Name").str, model, outcomes, ldsOutcomes)
  }
}
