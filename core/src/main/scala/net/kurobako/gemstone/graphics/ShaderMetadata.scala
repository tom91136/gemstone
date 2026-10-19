package net.kurobako.gemstone.graphics

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.Wire.*
import net.kurobako.gemstone.vm.*

/** Process-neutral shader preparation result. Offsets are bytes in VM invocation records; locations and components
  * describe the graphics interface, independently of that memory layout.
  */
object ShaderMetadata {
  enum ShaderStageKind derives Codec, CanEqual { case Vertex, Fragment, Compute            }
  enum ShaderResourceKind derives Codec        { case Buffer, Image, Sampler, SampledImage }
  enum ShaderDirection derives Codec, CanEqual { case Input, Output                        }

  case class ShaderDescriptor(
      set: Int >= 0,
      binding: Int >= 0,
      element: Int >= 0,
      resource: ShaderResourceKind
  ) derives Codec

  case class ShaderQualifiers(
      flat: Boolean,
      noPerspective: Boolean,
      centroid: Boolean,
      component: Int in 0 ~ 3
  ) derives Codec

  case class ShaderField(
      offset: Int >= 0,
      size: Int > 0,
      location: Option[Int >= 0],
      builtin: Option[Int >= 0],
      qualifiers: ShaderQualifiers
  ) derives Codec

  case class ShaderInterface(
      id: Int > 0,
      direction: ShaderDirection,
      size: Int > 0,
      builtin: Option[Int >= 0],
      qualifiers: ShaderQualifiers,
      fields: Vec[ShaderField]
  ) derives Codec

  case class PreparedShader(
      image: Vec[Byte],
      entry: NonEmpty[String],
      stage: ShaderStageKind,
      descriptors: Vec[ShaderDescriptor],
      pushConstants: Boolean,
      interfaces: Vec[ShaderInterface],
      workgroup: Option[LaunchGeometry.Dimensions],
      earlyFragmentTests: Boolean,
      fragmentQuads: Boolean
  ) derives Codec
}
