package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*

/** The generated operand domains of every SASS form, by target description. */
object SassDomains {
  private lazy val catalogue: ujson.Value = resources.json[ujson.Value](getClass, "domains.json")

  private lazy val domains: Vec[FieldDomain] =
    Vec.from(catalogue("domains").arr.map(row => FieldDomain(row("label").str, Vec.from(runs(row("domain"))))))

  private lazy val tables: Map[String, Map[String, Vec[(String, FieldDomain)]]] =
    catalogue("tables").arr.map { table =>
      table("digest").str -> table("forms").arr.map { row =>
        row(0).str -> Vec.from(catalogue("signatures")(row(1).num.toInt).arr.map(field =>
          field(0).str -> domains(field(1).num.toInt)
        ))
      }.toMap
    }.toMap

  private lazy val sizes: Map[String, Int] =
    catalogue("banks").arr.map(row => row("bank").str -> row("size").num.toInt).toMap

  /** Every register bank the tables name, longest spelling first so that a prefix match prefers `UR` over `R`. */
  lazy val banks: Vec[String] = Vec.from(sizes.keys.toSeq.sortBy(bank => (-bank.length, bank)))

  /** The registers the largest vocabulary of `bank` names, zero register included; none for an unknown bank. */
  def bankSize(bank: String): Option[Int] = sizes.get(bank)

  /** The domain of every FORMAT field of `form`, in FORMAT order. */
  def fields(database: NVDisasm.Database, form: NVDisasm.Form): Vec[(String, FieldDomain)] =
    tables.get(database.digest).flatMap(_.get(form.name)).getOrElse(Vec.empty)

  def field(database: NVDisasm.Database, form: NVDisasm.Form, binding: String): Option[FieldDomain] =
    fields(database, form).collectFirst { case (name, domain) if name == binding => domain }

  private def runs(domain: ujson.Value): Seq[DomainRun] = {
    def int(key: String): Int = domain(key).num.toInt
    domain("$type").str match {
      case "Union"      => domain("members").arr.toSeq.flatMap(runs(_))
      case "Registers"  => Seq(DomainRun.Registers(bank(domain("file").str), int("first"), int("last"), int("code")))
      case "NamedRange" => Seq(DomainRun.Numbered(domain("prefix").str, int("first"), int("last"), int("code")))
      case "Named"      => Seq(DomainRun.Named(domain("name").str, int("code")))
      case "Immediate"  => Seq(DomainRun.Immediate(int("bits"), domain("signed").bool))
      case other        => throw IllegalStateException(s"unsupported SASS domain $other")
    }
  }

  private def bank(file: String): String = file match {
    case "r"   => "R"
    case "ur"  => "UR"
    case "b"   => "B"
    case "sb"  => "SB"
    case other => throw IllegalStateException(s"unknown SASS register file $other")
  }
}
