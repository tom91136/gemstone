package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.*

/** The modifier choices of a SASS form, which its instances vary. */
private object SASSModifiers {

  /** The most instances of a form that are tried. */
  val Attempts = 256

  private val Scheduling = Set("REQ", "USCHED_INFO", "BATCH_T", "PM_PRED")

  /** The modifier fields an instance chooses, each but scheduling, with the vocabulary values it may take. */
  def of(database: NVDisasm.Database, fields: Vec[NVDisasm.Syntax.Field])
      : Vec[(field: NVDisasm.Syntax.Field, values: Vec[String], required: Boolean)] =
    fields.filter(field =>
      !field.category.isOperand && field.kind != "CX" && field.binding != "Opcode" && !Scheduling(field.kind)
    ).flatMap { field =>
      val defaults =
        (database.defaultValues(field) ++ database.byVocabulary.get(field.kind).flatMap(_.default).toVec).toSet
      // Each value as the disassembler spells it.
      val values =
        database.vocabulary(field).filterNot(symbol => defaults(symbol.value) || symbol.name.startsWith("INVALID"))
          .groupBy(_.value).toVec.sortBy(_._1).map((_, symbols) =>
            symbols.view.map(_.name).filterNot(_.startsWith("_")).minByOption(_.length).getOrElse(symbols.head.name)
          )
      Option.when(values.nonEmpty)((field, values, defaults.isEmpty))
    }
}
