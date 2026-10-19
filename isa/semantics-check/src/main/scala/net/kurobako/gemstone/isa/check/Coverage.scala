package net.kurobako.gemstone.isa.check

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.explorer.*

/** Writes, for every form of every ISA on every target, whether the explorer describes it and why not, as a TSV of
  * language, target, mnemonic, form, reason and detail, to the file given; further arguments limit the languages.
  */
object Coverage {
  private val Described = "Described"

  def main(arguments: Array[String]): Unit = {
    val output    = Paths.get(arguments(0))
    val languages = arguments.drop(1).toVec match {
      case Vec()    => Language.values.toVec
      case selected =>
        val names = selected.toSet
        Language.values.toVec.filter(value => names(value.toString))
    }
    val rows = languages.flatMap { language =>
      val model   = language.model
      val targets = if (model.targets.isEmpty) Vec(Option.empty[String]) else model.targets.map(Some(_))
      targets.flatMap(target =>
        model.entries(target).flatMap(entry =>
          entry.forms.map { form =>
            val outcome = model.semantics(entry, form)
            val reason  = outcome.fold(_.productPrefix, _ => Described)
            val detail  = outcome.fold(_.detail.getOrElse(""), _ => "")
            Vec(language.toString, target.getOrElse(""), entry.mnemonic, form.name, reason, detail)
              .map(_.replace('\t', ' ').replace('\n', ' ')).mkString("\t")
          }
        )
      )
    }
    val _ = Files.write(output, rows.mkString("", "\n", "\n").getBytes(StandardCharsets.UTF_8))
  }
}
