package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.NoNul

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** Exact processor target used by LLVM MC. */
opaque type Target = String :| (MinLength[1] & NoNul)
object Target {
  private final case class Classification(family: Family, metadataTarget: MetadataTarget)

  private def group(
      family: Family,
      metadataTarget: MetadataTarget,
      names: String*
  ): Seq[(String, Classification)] =
    names.map(_ -> Classification(family, metadataTarget))

  private val classifications = Map.from(
    group(Family.GFX6, MetadataTarget("gfx6"), "tahiti", "verde", "gfx600", "gfx601") ++
      group(Family.GFX7, MetadataTarget("gfx7"), "bonaire", "hawaii", "kabini", "kaveri", "gfx700", "gfx701") ++
      group(
        Family.GFX8,
        MetadataTarget("gfx8"),
        "carrizo",
        "fiji",
        "iceland",
        "stoney",
        "tonga",
        "gfx800",
        "gfx801",
        "gfx802",
        "gfx803",
        "gfx810"
      ) ++
      group(Family.GFX9, MetadataTarget("gfx9"), "gfx9", "gfx900", "gfx904", "gfx906") ++
      group(Family.CDNA1, MetadataTarget("cdna1"), "gfx908") ++
      group(Family.CDNA2, MetadataTarget("cdna2"), "gfx90a") ++
      group(Family.CDNA3, MetadataTarget("cdna3"), "gfx940", "gfx941", "gfx942") ++
      group(Family.CDNA4, MetadataTarget("cdna4"), "gfx950") ++
      group(Family.GFX10, MetadataTarget("rdna1"), "gfx1001", "gfx1010", "gfx1011", "gfx1012", "gfx1013") ++
      group(
        Family.GFX10,
        MetadataTarget("rdna2"),
        "gfx1030",
        "gfx1031",
        "gfx1032",
        "gfx1033",
        "gfx1034",
        "gfx1035",
        "gfx1036"
      ) ++
      group(Family.GFX11, MetadataTarget("rdna3"), "gfx1100", "gfx1101", "gfx1102", "gfx1103") ++
      group(
        Family.GFX11,
        MetadataTarget("rdna3_5"),
        "gfx1150",
        "gfx1151",
        "gfx1152",
        "gfx1153",
        "gfx1170"
      ) ++
      group(Family.GFX12, MetadataTarget("rdna4"), "gfx12", "gfx1200", "gfx1250", "gfx1251")
  )

  def apply(value: String): Target =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  def either(value: String): Either[String, Target] =
    classifications.get(value)
      .toRight(s"unsupported AMDGPU processor target: $value")
      .map(_ => value.refineUnsafe[MinLength[1] & NoNul])

  extension (target: Target) {
    def value: String                          = target
    def name: String :| (MinLength[1] & NoNul) = target
    def family: Family                         = classification(target).family
    def metadataTarget: MetadataTarget         = classification(target).metadataTarget
  }

  private def classification(target: Target): Classification = classifications(target.value)
}
