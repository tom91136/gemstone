package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.*
import upickle.default.*

object ImageMetadata {
  private lazy val instructions: Map[Opcode, ImageMetadata] =
    resources.json[Map[String, ImageMetadata]](getClass, "images.json").flatMap { case (name, properties) =>
      Opcode.forOpcode(name).map(_ -> properties)
    }

  def forOpcode(opcode: Opcode): Option[ImageMetadata] = instructions.get(opcode)
}

case class ImageMetadata(
    store: Boolean,
    atomic: Boolean,
    sampler: Boolean,
    gather4: Boolean,
    numExtraArgs: Int,
    gradients: Boolean,
    g16: Boolean,
    coordinates: Boolean,
    lodOrClampOrMip: Boolean,
    bvh: Boolean,
    bvhAddressWords: Int = 0,
    bvhPackedAddressWords: Int = 0,
    bvhDataWords: Int = 0
) derives ReadWriter {
  def addressWords(dimension: ImageDimension, a16: Boolean, packedGradients: Boolean): Int = {
    val coords          = if (coordinates) dimension.coordinates else 0
    val lod             = if (lodOrClampOrMip) 1 else 0
    val coordinateWords = if (a16) (coords + lod + 1) / 2 else coords + lod
    val gradientWords   = if (!gradients) 0
    else if (g16 || (a16 && packedGradients)) ((dimension.gradients / 2 + 1) / 2) * 2
    else dimension.gradients
    if (bvh) if (a16) bvhPackedAddressWords else bvhAddressWords
    else numExtraArgs + coordinateWords + gradientWords
  }

  def dataWords(mask: Int, d16: Boolean, tfe: Boolean): Int = {
    val components = if (bvh) bvhDataWords else if (gather4 && sampler) 4 else Integer.bitCount(mask).max(1)
    (if (d16) (components + 1) / 2 else components) + (if (tfe) 1 else 0)
  }
}
