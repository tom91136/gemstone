package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.*
import upickle.default.*

object ImageMetadata {

  /** GFX12's spellings of image atomics whose LLVM base opcode keeps the earlier name. */
  private val Renamed = Map(
    "image_atomic_add"  -> "image_atomic_add_uint",
    "image_atomic_sub"  -> "image_atomic_sub_uint",
    "image_atomic_smin" -> "image_atomic_min_int",
    "image_atomic_umin" -> "image_atomic_min_uint",
    "image_atomic_smax" -> "image_atomic_max_int",
    "image_atomic_umax" -> "image_atomic_max_uint",
    "image_atomic_inc"  -> "image_atomic_inc_uint",
    "image_atomic_dec"  -> "image_atomic_dec_uint"
  )

  private lazy val instructions: Map[Opcode, ImageMetadata] =
    resources.json[Map[String, ImageMetadata]](getClass, "images.json").flatMap { case (name, properties) =>
      (name +: Renamed.get(name).toVector).flatMap(InstructionDefs.forOpcode(_).map(_ -> properties))
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
