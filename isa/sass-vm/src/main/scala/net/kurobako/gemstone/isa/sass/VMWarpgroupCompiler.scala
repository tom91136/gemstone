package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Matrix.*

/** Hopper's warpgroup matrix products, HGMMA, IGMMA, QGMMA and BGMMA, as ptxas emits them for `wgmma.mma_async`, and
  * the `WARPGROUP` arrivals and scoreboard waits it emits for `wgmma.fence` and `wgmma.wait_group`. A product names D's
  * first register, A's first register (negated where `imm-scale-a` is -1) or the descriptors, C's first register or
  * `RZ`, an optional uniform scale-d predicate and, sparse, the metadata register and a selector. The descriptors are
  * `gdesc[URa]`: A's in URa and URa+1 and B's in URa+2 and URa+3, `.negA`, `.negB`, `.tnspA` and `.tnspB` negating and
  * transposing them.
  */
private object VMWarpgroupCompiler {
  private val knownSpellings = Set("SP", "SAT", "AND", "POPC")

  private type Gmma = Form.Hgmma | Form.Igmma | Form.Qgmma | Form.Bgmma
  private val Shaped = "64x([0-9]+)x([0-9]+)".r

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Warpgroup =>
        modifiers.map(_.spelling.raw).headOption match {
          case Some("ARRIVE") | Some("DEPBAR") => emit(NoOp)
          case _                               => unsupported(s"unsupported WARPGROUP.${modifiers.mkString(".")}")
        }
        true
      // ptxas issues a product into RZ where it needs one whose result nothing reads
      case _: Gmma if OperandQueries.isZero(operands.head) =>
        emit(NoOp)
        true
      case _: Gmma =>
        product()
        true
      case _ => false
    }
  }

  /** The uniform register of a `gdesc[URa]` operand and its qualifiers. */
  private def descriptor(operand: Operand): Option[(base: Int, selectors: Set[String])] = operand match {
    case Operand.Address(Some(Operand.Register(RegisterBank.UR, index)), None, displacement, _, None, None)
        if displacement == 0 =>
      Some((index, Set.empty))
    case Operand.Qualified(inner, selectors) =>
      descriptor(inner).map(found => (found.base, found.selectors ++ selectors.map(_.spelling)))
    case _ => None
  }

  private def element(spelling: String): Option[Element] = spelling match {
    case "F16"            => Some(Element.F16)
    case "F32"            => Some(Element.F32)
    case "E8M7" | "BF16"  => Some(Element.BF16)
    case "TF32" | "E8M10" => Some(Element.TF32)
    case "S8"             => Some(Element.S8)
    case "U8"             => Some(Element.U8)
    case "E4M3"           => Some(Element.E4M3)
    case "E5M2"           => Some(Element.E5M2)
    case _                => None
  }

  private def product()(using context: VMCompilationContext): Unit = {
    import context.*
    val mnemonic = operation.mnemonic.raw
    if (sm != 90) unsupported(s"$mnemonic requires sm_90a")
    val spelt  = modifiers.map(_.spelling.raw).filterNot(_.startsWith("gsb"))
    val sparse = spelt.contains("SP")
    if (spelt.exists(spelling => !knownSpellings(spelling) && !Shaped.matches(spelling) && element(spelling).isEmpty))
      unsupported(s"unsupported $mnemonic.${spelt.mkString(".")}")
    val (n, k) = spelt.collectFirst { case Shaped(n, k) => (n.toInt, k.toInt) }
      .getOrElse(unsupported(s"$mnemonic requires a 64xNxK shape"))
    val types     = spelt.flatMap(element(_))
    val (d, a, b) = (operation.form: @unchecked) match {
      case _: Form.Hgmma =>
        val input = types.drop(1).headOption.getOrElse(Element.F16)
        (types.headOption.getOrElse(unsupported("HGMMA requires an accumulator type")), input, input)
      case _: Form.Qgmma if types.size == 3                                 => (types(0), types(1), types(2))
      case _: Form.Igmma if types.size == 2                                 => (Element.S32, types(0), types(1))
      case _: Form.Bgmma if spelt.contains("AND") && spelt.contains("POPC") =>
        (Element.S32, Element.B1, Element.B1)
      case _ => unsupported(s"unsupported $mnemonic types ${spelt.mkString(".")}")
    }
    if (!Warpgroup.supports(n = n, k = k, a = a, b = b, d = d, sparse = sparse))
      unsupported(s"no wgmma product $mnemonic.${spelt.mkString(".")}")
    // the encodings that take B from registers, which no wgmma form does, decode to the same operand lists
    if (operation.definition.forms.filter(_.contains("sparse") == sparse).forall(_.contains("_URa_Rb_Rc_")))
      unsupported(s"$mnemonic with B in registers is not implemented")
    val registerA   = descriptor(operands(1)).isEmpty
    val descriptors = descriptor(operands(if (registerA) 2 else 1))
      .getOrElse(unsupported(s"$mnemonic requires a gdesc descriptor operand"))
    val rest        = operands.drop(if (registerA) 3 else 2)
    val (c, afterC) = rest.headOption.fold(unsupported(s"$mnemonic requires an accumulator"))(_ -> rest.drop(1))
    val (scale, sparsityOperands) = afterC.headOption match {
      case Some(predicate: Operand.Predicate) =>
        val value = predicate match {
          case Operand.Predicate(None, _, negated) => Value.Immediate(if (negated) 0L else 1L)
          case other                               => predicateSource(other)
        }
        (value, afterC.drop(1))
      case _ => (Value.Immediate(1L), afterC)
    }
    val sparsity = Option.when(sparse) {
      val metadata = sparsityOperands.headOption.getOrElse(unsupported(s"$mnemonic.SP requires a metadata register"))
      Sparsity(source(metadata), sparsityOperands.lift(1).fold(Value.Immediate(0L))(literal(_)))
    }
    if (!bareRegister(operands.head)) unsupported(s"unsupported $mnemonic destination")
    val shape        = Shape(64.unsafe[Int > 0], n.unsafe[Int > 0], k.unsafe[Int > 0])
    val packed       = if (sparse) shape.copy(k = (k / 2).unsafe[Int > 0]) else shape
    val registers    = Warpgroup.accumulatorRegisters(n, d)
    val destinations = Vec.tabulate(registers)(destination(operands.head, _).slot)
    val addend       =
      if (OperandQueries.isZero(c)) Vec.fill(registers)(Value.Immediate(0L): Value)
      else Vec.tabulate(registers)(register(c, _))
    val left =
      if (!registerA) Vec.empty[Value]
      else {
        val base = registerBase(OperandQueries.positive(operands(1)), s"$mnemonic A fragment")
        Vec.tabulate(Warpgroup.MultiplicandRegisters)(register(base, _))
      }
    def uniform(offset: Int): Value =
      source(Operand.Register(RegisterBank.UR, (descriptors.base + offset).unsafe[Int in 0 ~ 255]))
    emit(Tensor(Operation.WarpgroupMultiply(
      Operation.Multiply(
        Fragment(packed, Role.A, a, wmma = false),
        Fragment(shape, Role.B, b, wmma = false, columnMajor = true),
        Fragment(shape, Role.C, d, wmma = false),
        Fragment(shape, Role.C, d, wmma = false),
        destinations,
        left,
        Vec.empty,
        addend,
        Matrix.accumulation(
          architecture = 90,
          shape = shape,
          input = a,
          result = if (a.integer) d else Matrix.accumulator(d, d),
          rounding = ConversionRounding.RN
        ),
        spelt.contains("SAT"),
        Option.when(a == Element.B1)(BitOperation.And),
        sparsity
      ),
      if (registerA) Vec.empty else Vec(uniform(0), uniform(1)),
      Vec(uniform(2), uniform(3)),
      scale,
      descriptors.selectors("negA") || (registerA && OperandQueries.negative(operands(1))),
      descriptors.selectors("negB"),
      descriptors.selectors("tnspA"),
      descriptors.selectors("tnspB")
    )))
  }
}
