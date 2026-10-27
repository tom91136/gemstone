package net.kurobako.gemstone.codegen

import org.jsoup.nodes.Document

/** The operand rules the PTX document states only in prose. Every row cites the section it comes from and quotes the
  * sentence, so generation fails when the pinned document stops saying it.
  */
object PTXOperandRules {
  case class Cite(section: String, quote: String)

  /** A type outside the type tables; `declared` lists the types a register holding it is declared with. */
  case class TypeRow(
      name: String,
      kind: String,
      bits: Int,
      elements: Int,
      element: Option[String],
      declared: Vector[String],
      cite: Cite
  )

  /** The types of an instruction's operands, by position. `fill` types every position `at` leaves out. A type is a type
    * name, `pred`, `first` or `last` instruction type, `wide` for the double-width last type, or `acc` for the 32-bit
    * accumulator of the dot-product instructions. The first row matching `opcodes` and the modifier conditions decides.
    */
  case class Binding(
      opcodes: Vector[String],
      any: Vector[String],
      at: Vector[(Int, String)],
      cite: Cite
  ) {
    val fill: String = "last"
  }

  /** A check of an immediate operand, or of each element of a vector of immediates when `elements` holds. */
  case class ValueRule(
      opcodes: Vector[String],
      any: Vector[String],
      all: Vector[String],
      none: Vector[String],
      position: Int,
      kind: String,
      values: Vector[Long],
      elements: Boolean,
      cite: Cite
  )

  /** Accepts a disagreement between sources, stating why. */
  case class Override(check: String, subject: String, reason: String, cite: Option[Cite])

  case class Rules(
      types: Vector[TypeRow],
      bindings: Vector[Binding],
      values: Vector[ValueRule],
      overrides: Vector[Override]
  ) {
    def resolution(check: String, subject: String): Option[String] =
      overrides.find(row => row.check == check && row.subject == subject).map(_.reason)
  }

  private def cite(section: String, quote: String): Cite = Cite(section, quote)
  private val Opaque                                     = Cite(
    "texture-sampler-and-surface-types",
    "PTX includes built-in opaque types for defining texture, sampler, and surface descriptor variables."
  )
  private val Alternate  = "alternate-floating-point-data-formats"
  private val Matrix     = "warp-level-matrix-data-types"
  private val Sync       = "parallel-synchronization-and-communication-instructions"
  private val Move       = "data-movement-and-conversion-instructions"
  private val Integer    = "integer-arithmetic-instructions"
  private val Comparison = "comparison-and-selection-instructions"

  private def value(
      opcodes: Vector[String],
      position: Int,
      kind: String,
      values: Vector[Long],
      cite: Cite,
      any: Vector[String] = Vector.empty,
      all: Vector[String] = Vector.empty,
      none: Vector[String] = Vector.empty,
      elements: Boolean = false
  ): ValueRule = ValueRule(opcodes, any, all, none, position, kind, values, elements, cite)

  private def bmsk(max: Long, mode: String): ValueRule = value(
    Vector("bmsk"),
    2,
    "range",
    Vector(0, max),
    cite(
      s"$Integer-bmsk",
      "The bitmask width specified by operand b is limited to range 0..32 in .clamp mode and to range 0..31 in .wrap mode."
    ),
    all = Vector(mode)
  )

  val All: Rules = Rules(
    Vector(
      TypeRow(
        "bf16",
        "alt",
        bits = 16,
        elements = 1,
        None,
        Vector("b16"),
        cite(Alternate, "A register variable containing bf16 data must be declared with .b16 type.")
      ),
      TypeRow(
        "tf32",
        "alt",
        bits = 32,
        elements = 1,
        None,
        Vector("b32"),
        cite(Alternate, "A register variable containing tf32 data must be declared with .b32 type.")
      ),
      TypeRow(
        "ue4m3",
        "alt",
        bits = 8,
        elements = 1,
        None,
        Vector("b8"),
        cite(Alternate, "A register variable containing single ue4m3 value must be declared with .b8 type")
      ),
      TypeRow(
        "e4m3",
        "alt",
        bits = 8,
        elements = 1,
        None,
        Vector("b8"),
        cite(Alternate, "A register variable containing e4m3 value must be declared using bit-size type.")
      ),
      TypeRow(
        "e5m2",
        "alt",
        bits = 8,
        elements = 1,
        None,
        Vector("b8"),
        cite(Alternate, "A register variable containing e5m2 value must be declared using bit-size type.")
      ),
      TypeRow(
        "u16x2",
        "packed",
        bits = 32,
        elements = 2,
        Some("u16"),
        Vector("b32"),
        cite(
          "packed-integer-data-types",
          "A register variable containing .u16x2, .s16x2, .u8x4, .s8x4 data must be declared with .b32 type."
        )
      ),
      TypeRow(
        "s16x2",
        "packed",
        bits = 32,
        elements = 2,
        Some("s16"),
        Vector("b32"),
        cite(
          "packed-integer-data-types",
          "A register variable containing .u16x2, .s16x2, .u8x4, .s8x4 data must be declared with .b32 type."
        )
      ),
      TypeRow(
        "u8x4",
        "packed",
        bits = 32,
        elements = 4,
        Some("u8"),
        Vector("b32"),
        cite(
          "packed-integer-data-types",
          "A register variable containing .u16x2, .s16x2, .u8x4, .s8x4 data must be declared with .b32 type."
        )
      ),
      TypeRow(
        "s8x4",
        "packed",
        bits = 32,
        elements = 4,
        Some("s8"),
        Vector("b32"),
        cite(
          "packed-integer-data-types",
          "A register variable containing .u16x2, .s16x2, .u8x4, .s8x4 data must be declared with .b32 type."
        )
      ),
      TypeRow(
        "u4",
        "u",
        bits = 4,
        elements = 1,
        None,
        Vector.empty,
        cite(Matrix, "Sub-byte integer both .u4 or both .s4 .s32")
      ),
      TypeRow(
        "s4",
        "s",
        bits = 4,
        elements = 1,
        None,
        Vector.empty,
        cite(Matrix, "Sub-byte integer both .u4 or both .s4 .s32")
      ),
      TypeRow("b1", "b", bits = 1, elements = 1, None, Vector.empty, cite(Matrix, "Single-bit integer .b1 .s32")),
      TypeRow(
        "u2",
        "u",
        bits = 2,
        elements = 1,
        None,
        Vector.empty,
        cite(s"$Move-cvt-pack", ".convertType = { .u2, .s2, .u4, .s4, .u8, .s8 }")
      ),
      TypeRow(
        "s2",
        "s",
        bits = 2,
        elements = 1,
        None,
        Vector.empty,
        cite(s"$Move-cvt-pack", ".convertType = { .u2, .s2, .u4, .s4, .u8, .s8 }")
      ),
      TypeRow("texref", "opaque", bits = 0, elements = 1, None, Vector.empty, Opaque),
      TypeRow("samplerref", "opaque", bits = 0, elements = 1, None, Vector.empty, Opaque),
      TypeRow("surfref", "opaque", bits = 0, elements = 1, None, Vector.empty, Opaque)
    ),
    Vector(
      Binding(
        Vector("setp"),
        Vector.empty,
        Vector(0 -> "pred", 3 -> "pred"),
        cite(s"$Comparison-setp", "Operands a and b have type .type; operands p, q, and c have type .pred.")
      ),
      Binding(
        Vector("set"),
        Vector.empty,
        Vector(0 -> "first", 3 -> "pred"),
        cite(
          s"$Comparison-set",
          "Operand d has type .dtype; operands a and b have type .stype; operand c has type .pred."
        )
      ),
      Binding(
        Vector("selp"),
        Vector.empty,
        Vector(3 -> "pred"),
        cite(s"$Comparison-selp", "Operands d, a, and b must be of the same type. Operand c is a predicate.")
      ),
      Binding(
        Vector("shl", "shr"),
        Vector.empty,
        Vector(2 -> "u32"),
        cite(
          "logic-and-shift-instructions-shl",
          "The b operand must be a 32-bit value, regardless of the instruction type."
        )
      ),
      Binding(
        Vector("bfe"),
        Vector.empty,
        Vector(2 -> "u32", 3 -> "u32"),
        cite(s"$Integer-bfe", "Operands b and c are type .u32, but are restricted to the 8-bit value range 0..255.")
      ),
      Binding(
        Vector("bfi"),
        Vector.empty,
        Vector(3 -> "u32", 4 -> "u32"),
        cite(s"$Integer-bfi", "Operands c and d are type .u32, but are restricted to the 8-bit value range 0..255.")
      ),
      Binding(
        Vector("popc"),
        Vector.empty,
        Vector(0 -> "u32"),
        cite(s"$Integer-popc", "Operand a has the instruction type and destination d has type .u32.")
      ),
      Binding(
        Vector("clz"),
        Vector.empty,
        Vector(0 -> "u32"),
        cite(s"$Integer-clz", "Operand a has the instruction type, and destination d has type .u32.")
      ),
      Binding(
        Vector("bfind"),
        Vector.empty,
        Vector(0 -> "u32"),
        cite(s"$Integer-bfind", "Operand a has the instruction type, and destination d has type .u32.")
      )
    ),
    Vector(
      value(
        Vector("bfe"),
        2,
        "range",
        Vector(0, 255),
        cite(s"$Integer-bfe", "Operands b and c are type .u32, but are restricted to the 8-bit value range 0..255.")
      ),
      value(
        Vector("bfe"),
        3,
        "range",
        Vector(0, 255),
        cite(s"$Integer-bfe", "Operands b and c are type .u32, but are restricted to the 8-bit value range 0..255.")
      ),
      value(
        Vector("bfi"),
        3,
        "range",
        Vector(0, 255),
        cite(s"$Integer-bfi", "Operands c and d are type .u32, but are restricted to the 8-bit value range 0..255.")
      ),
      value(
        Vector("bfi"),
        4,
        "range",
        Vector(0, 255),
        cite(s"$Integer-bfi", "Operands c and d are type .u32, but are restricted to the 8-bit value range 0..255.")
      ),
      bmsk(32, "clamp"),
      bmsk(31, "wrap"),
      value(
        Vector("fns"),
        2,
        "range",
        Vector(0, 31),
        cite(s"$Integer-fns", "Operand base must be <= 31, otherwise behavior is undefined.")
      ),
      value(
        Vector("pmevent"),
        0,
        "range",
        Vector(0, 15),
        cite(
          "miscellaneous-instructions-pmevent",
          "pmevent (without modifier .mask) triggers a single performance monitor event indexed by immediate operand a, in the range 0..15."
        ),
        none = Vector("mask")
      ),
      value(
        Vector("mma.sp", "mma.sp::ordered_metadata"),
        5,
        "range",
        Vector(0, 3),
        cite(
          "warp-level-matrix-instructions-sparse-mma",
          "operand f is a 32-bit integer constant with values in the range 0..3"
        )
      ),
      value(
        Vector("tcgen05.alloc", "tcgen05.dealloc"),
        1,
        "range",
        Vector(32, 512),
        cite(
          "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit",
          "The operand nCols must be within the range [32, 512]."
        )
      ),
      value(
        Vector("tcgen05.alloc", "tcgen05.dealloc"),
        1,
        "pow2",
        Vector.empty,
        cite(
          "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit",
          "The number of columns must be a power of 2."
        )
      ),
      value(
        Vector("tensormap.replace"),
        1,
        "range",
        Vector(0, 4),
        cite(s"$Move-tensormap-replace", "The only valid values for ord are [0..4]."),
        any = Vector("box_dim", "global_dim", "global_stride", "element_stride")
      ),
      value(
        Vector("bar", "barrier"),
        0,
        "range",
        Vector(0, 15),
        cite(
          s"$Sync-bar",
          "Source operand a specifies a logical barrier resource as an immediate constant or register with value 0 through 15."
        ),
        any = Vector("sync", "arrive")
      ),
      value(
        Vector("bar", "barrier"),
        1,
        "range",
        Vector(0, 15),
        cite(
          s"$Sync-bar",
          "Source operand a specifies a logical barrier resource as an immediate constant or register with value 0 through 15."
        ),
        any = Vector("red")
      ),
      value(
        Vector("bar", "barrier"),
        1,
        "multiple",
        Vector(32),
        cite(s"$Sync-bar", "When specifying a thread count, the value must be a multiple of the warp size."),
        any = Vector("sync", "arrive")
      ),
      value(
        Vector("cp.async.bulk"),
        2,
        "multiple",
        Vector(16),
        cite(s"$Move-cp-async-bulk", "size must be a multiple of 16.")
      ),
      value(
        Vector("cp.reduce.async.bulk"),
        2,
        "multiple",
        Vector(16),
        cite(s"$Move-cp-reduce-async-bulk", "size must be a multiple of 16.")
      ),
      value(Vector("st.bulk"), 1, "multiple", Vector(8), cite(s"$Move-st-bulk", "size must be a multiple of 8.")),
      value(
        Vector("cp.async"),
        2,
        "oneof",
        Vector(4, 8, 16),
        cite(s"$Move-cp-async", "cp-size can only be 4, 8 and 16.")
      ),
      value(
        Vector("alloca"),
        2,
        "pow2",
        Vector(8388608),
        cite(
          "stack-manipulation-instructions-alloca",
          "It is an integer constant, must be a power of 2 and must not exceed 2^23."
        )
      ),
      value(
        Vector("tex", "tld4"),
        2,
        "range",
        Vector(-8, 7),
        cite("texture-instructions-tex", "Offset value is in the range of -8 to +7."),
        elements = true
      ),
      value(
        Vector("tensormap.cp_fenceproxy"),
        2,
        "oneof",
        Vector(128),
        cite(s"$Sync-tensormap-cp-fenceproxy", "The valid value of immediate operand size is 128.")
      )
    ),
    Vector(
      Override(
        "identifier-unlisted",
        "%clock_hi",
        "the clock section declares it next to %clock",
        Some(cite("special-registers-clock", ".sreg .u32 %clock_hi;"))
      )
    ) ++ Vector.tabulate(8)(index =>
      Override(
        "identifier-unlisted",
        s"%pm${index}_64",
        "the 64-bit counters have a section of their own",
        Some(cite("special-registers-pm0-64-pm7-64", s".sreg .u64 %pm${index}_64;"))
      )
    ) ++ Vector(
      Override(
        "slot-undefined",
        "fma",
        "the sibling sections of the instruction define the type slot the block leaves open",
        None
      )
    )
  )

  /** Cites that name an absent section or a sentence the section no longer holds. */
  def verify(rules: Rules, document: Document): Vector[String] = {
    val cites = rules.types.map(_.cite) ++ rules.bindings.map(_.cite) ++ rules.values.map(_.cite) ++
      rules.overrides.flatMap(_.cite)
    cites.distinct.flatMap { row =>
      PTXReferenceTables.sectionText(document, row.section) match {
        case None => Some(s"section ${row.section} is absent")
        case Some(text) if !text.contains(Text.normaliseWhitespace(row.quote)) =>
          Some(s"section ${row.section} does not say: ${row.quote}")
        case _ => None
      }
    }
  }
}
