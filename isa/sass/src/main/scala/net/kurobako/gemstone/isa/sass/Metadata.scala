package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import scala.util.Using
import upickle.default.ReadWriter
import upickle.default.read

/** Target identities, shared encoding keys, and the bound for generated instruction schema IDs. */
object Metadata {
  case class Target(name: String, encodingKey: String)
  case class Catalogue(format: Int, targets: Vec[Target], schemaCount: Int)

  private case class TargetRow(name: Int, tool: Int, encodingKey: Int) derives ReadWriter
  private case class Resource(
      format: Int,
      strings: Vec[String],
      targets: Vec[TargetRow],
      schemaCount: Int
  ) derives ReadWriter

  lazy val catalogue: Catalogue = {
    val stream = Option(getClass.getResourceAsStream("metadata.json"))
      .getOrElse(throw IllegalStateException("missing SASS metadata resource"))
    Using.resource(stream) { input =>
      val resource = read[Resource](input)
      val strings  = resource.strings
      val value    = Catalogue(
        resource.format,
        resource.targets.map(row => Target(strings(row.name), strings(row.encodingKey))),
        resource.schemaCount
      )
      require(value.format == 3, "unsupported SASS metadata format")
      val targetNames = value.targets.map(_.name)
      require(
        targetNames.forall(_.nonEmpty) && targetNames.distinct.size == targetNames.size,
        "invalid SASS target catalogue"
      )
      require(
        value.targets.forall(target =>
          target.encodingKey.length == 64 &&
            target.encodingKey.forall(c => (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))
        ),
        "invalid SASS target decoder digest"
      )
      require(value.schemaCount >= 0, "invalid SASS schema count")
      value
    }
  }

}
