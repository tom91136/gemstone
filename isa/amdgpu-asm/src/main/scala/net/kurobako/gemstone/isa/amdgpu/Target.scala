package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.compiletime.constValue
import scala.compiletime.erasedValue
import scala.compiletime.summonInline

/** An accepted LLVM processor name or alias, classified by its encoding ISA. */
type Target = Target.Type
object Target extends Newtype[NonEmpty[String]] {
  // Both literal acceptance and runtime lookup derive from this one table.
  private type Processors = (
      (ISA.GFX6.type, ("tahiti", "verde", "gfx600", "gfx601", "pitcairn", "oland", "hainan", "gfx602")),
      (
          ISA.GFX7.type,
          (
              "bonaire",
              "hawaii",
              "kabini",
              "kaveri",
              "gfx700",
              "gfx701",
              "mullins",
              "gfx702",
              "gfx703",
              "gfx704",
              "gfx705"
          )
      ),
      (
          ISA.GFX8.type,
          (
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
      ),
      (ISA.GFX9.type, ("gfx9", "gfx900", "gfx904", "gfx906", "gfx90c")),
      (ISA.CDNA1.type, Tuple1["gfx908"]),
      (ISA.CDNA2.type, Tuple1["gfx90a"]),
      (ISA.CDNA3.type, ("gfx940", "gfx941", "gfx942")),
      (ISA.CDNA4.type, Tuple1["gfx950"]),
      (ISA.CDNA5.type, ("gfx1250", "gfx1251")),
      (ISA.RDNA1.type, ("gfx1001", "gfx1010", "gfx1011", "gfx1012", "gfx1013")),
      (ISA.RDNA2.type, ("gfx1030", "gfx1031", "gfx1032", "gfx1033", "gfx1034", "gfx1035", "gfx1036")),
      (ISA.RDNA3.type, ("gfx1100", "gfx1101", "gfx1102", "gfx1103")),
      (ISA.RDNA3_5.type, ("gfx1150", "gfx1151", "gfx1152", "gfx1153")),
      (ISA.GFX11_7.type, Tuple1["gfx1170"]),
      (ISA.RDNA4.type, ("gfx12", "gfx1200", "gfx1201"))
  )

  private type Names[T <: Tuple] <: Tuple = T match {
    case EmptyTuple         => EmptyTuple
    case (_, names) *: tail => Tuple.Concat[names & Tuple, Names[tail]]
  }

  type Supported = Tuple.Union[Names[Processors]]

  private inline def names[T <: Tuple]: Vector[String] =
    inline erasedValue[T] match {
      case _: EmptyTuple     => Vector.empty
      case _: (head *: tail) => constValue[head].toString +: names[tail]
    }

  private inline def entries[T <: Tuple]: Vector[(String, ISA)] =
    inline erasedValue[T] match {
      case _: EmptyTuple                  => Vector.empty
      case _: ((isa, processors) *: tail) =>
        val value = summonInline[isa <:< ISA](summonInline[ValueOf[isa]].value)
        names[processors & Tuple].map(_ -> value) ++ entries[tail]
    }

  private val classifications = entries[Processors].toMap

  /** Every accepted processor name and alias, in catalogue order. */
  lazy val all: Vec[Target] = Vec.from(entries[Processors].map((name, _) => apply(name.unsafe[NonEmpty[String]])))

  override protected def violation(value: NonEmpty[String]): Option[NonEmpty[String]] =
    Option.unless[NonEmpty[String]](classifications.contains(value))(s"unsupported AMDGPU processor target: $value")

  def apply[A <: String & Singleton](value: A)(using (A & Supported) =:= A): Target =
    apply((value: String).unsafe[NonEmpty[String]])

  extension (target: Target) {
    def name: NonEmpty[String] = target.unwrap
    def isa: ISA               = classifications(target.raw)
  }
}
