package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.NonEmptyString

import scala.compiletime.{constValue, erasedValue}

/** Exact processor target used by LLVM MC. */
opaque type Target = String
object Target {
  private case class Classification(family: Family, metadataTarget: NonEmptyString)

  private type GFX6 = ("tahiti", "verde", "gfx600", "gfx601")
  private type GFX7 = ("bonaire", "hawaii", "kabini", "kaveri", "gfx700", "gfx701")
  private type GFX8 = (
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
  )
  private type GFX9    = ("gfx9", "gfx900", "gfx904", "gfx906")
  private type CDNA1   = Tuple1["gfx908"]
  private type CDNA2   = Tuple1["gfx90a"]
  private type CDNA3   = ("gfx940", "gfx941", "gfx942")
  private type CDNA4   = Tuple1["gfx950"]
  private type RDNA1   = ("gfx1001", "gfx1010", "gfx1011", "gfx1012", "gfx1013")
  private type RDNA2   = ("gfx1030", "gfx1031", "gfx1032", "gfx1033", "gfx1034", "gfx1035", "gfx1036")
  private type RDNA3   = ("gfx1100", "gfx1101", "gfx1102", "gfx1103")
  private type RDNA3_5 = ("gfx1150", "gfx1151", "gfx1152", "gfx1153", "gfx1170")
  private type RDNA4   = ("gfx12", "gfx1200", "gfx1250", "gfx1251")

  type AllNames = Tuple.Concat[
    GFX6,
    Tuple.Concat[
      GFX7,
      Tuple.Concat[
        GFX8,
        Tuple.Concat[
          GFX9,
          Tuple.Concat[
            CDNA1,
            Tuple.Concat[
              CDNA2,
              Tuple.Concat[
                CDNA3,
                Tuple.Concat[
                  CDNA4,
                  Tuple.Concat[RDNA1, Tuple.Concat[RDNA2, Tuple.Concat[RDNA3, Tuple.Concat[RDNA3_5, RDNA4]]]]
                ]
              ]
            ]
          ]
        ]
      ]
    ]
  ]

  type Union[T <: Tuple] = T match {
    case EmptyTuple => Nothing
    case h *: t     => h | Union[t]
  }

  type Supported = Union[AllNames]

  private inline def names[T <: Tuple]: Vector[String] =
    inline erasedValue[T] match {
      case _: EmptyTuple => Vector.empty
      case _: (h *: t)   => constValue[h].toString +: names[t]
    }

  private def group(
      family: Family,
      metadataTarget: NonEmptyString,
      names: Vector[String]
  ): Seq[(String, Classification)] =
    names.map(_ -> Classification(family, metadataTarget))

  private val classifications = Map.from(
    group(Family.GFX6, NonEmptyString("gfx6"), names[GFX6]) ++
      group(Family.GFX7, NonEmptyString("gfx7"), names[GFX7]) ++
      group(Family.GFX8, NonEmptyString("gfx8"), names[GFX8]) ++
      group(Family.GFX9, NonEmptyString("gfx9"), names[GFX9]) ++
      group(Family.CDNA1, NonEmptyString("cdna1"), names[CDNA1]) ++
      group(Family.CDNA2, NonEmptyString("cdna2"), names[CDNA2]) ++
      group(Family.CDNA3, NonEmptyString("cdna3"), names[CDNA3]) ++
      group(Family.CDNA4, NonEmptyString("cdna4"), names[CDNA4]) ++
      group(Family.GFX10, NonEmptyString("rdna1"), names[RDNA1]) ++
      group(Family.GFX10, NonEmptyString("rdna2"), names[RDNA2]) ++
      group(Family.GFX11, NonEmptyString("rdna3"), names[RDNA3]) ++
      group(Family.GFX11, NonEmptyString("rdna3_5"), names[RDNA3_5]) ++
      group(Family.GFX12, NonEmptyString("rdna4"), names[RDNA4])
  )

  def either(value: String): Either[String, Target] =
    classifications.get(value)
      .toRight(s"unsupported AMDGPU processor target: $value")
      .map(_ => value)

  inline def unsafe(value: String): Target =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: String & Singleton](inline value: A)(using (A & Supported) =:= A): Target = value

  extension (target: Target) {
    inline def raw: String             = target
    def name: NonEmptyString           = NonEmptyString.unsafe(target)
    def family: Family                 = classification(target).family
    def metadataTarget: NonEmptyString = classification(target).metadataTarget
  }

  private def classification(target: Target): Classification = classifications(target.raw)
}
