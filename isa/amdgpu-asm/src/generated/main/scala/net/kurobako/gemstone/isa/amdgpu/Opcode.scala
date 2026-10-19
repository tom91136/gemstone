// Generated from the pinned AMDGPU instruction metadata. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

object Opcode {
  enum ComparisonKind derives CanEqual {
    case Signed16, Signed32, Signed64, Unsigned16, Unsigned32, Unsigned64
    case Float16, Float32, Float64, ClassFloat32, ClassFloat64
  }

  enum ComparisonRelation derives CanEqual {
    case Equal, NotEqual, OrderedNotEqual, Less, LessOrEqual, Greater, GreaterOrEqual
    case NotLess, NotLessOrEqual, NotGreater, NotGreaterOrEqual
    case Ordered, Unordered, NotLessOrGreater, Class
  }

  case class ComparisonOpcode(
      kind: ComparisonKind,
      relation: ComparisonRelation,
      updateExec: Boolean
  )
}

abstract class Opcode(val mnemonic: NonEmpty[String], private val metadataId: Int) derives CanEqual {
  lazy val meta: Metadata = Metadata.Catalogue.metadata(metadataId)

  /** The encodings of the opcode that `target` supports. */
  def encodings(target: Target): Vec[Metadata.Encoding] = meta.encodings.filter(_.supports(target))
  override final def toString: String = mnemonic
}
