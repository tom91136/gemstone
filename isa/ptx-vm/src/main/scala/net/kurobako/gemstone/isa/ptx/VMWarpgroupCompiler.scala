package net.kurobako.gemstone.isa.ptx

import Lowering.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Matrix.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as _, *}

/** `wgmma`: the warpgroup matrix products, which the VM completes as they issue, and the fences and groups that order
  * them, which leave nothing to do.
  */
private object VMWarpgroupCompiler {
  private val SparsitySelectors = Set(0L, 1L, 2L, 3L)

  /** The qualifiers of a product: its N and K, the element types of D, A and B, a sparse A and `.satfinite`. */
  private case class Product(
      n: Int > 0,
      k: Int > 0,
      d: Element,
      a: Element,
      b: Element,
      sparse: Boolean,
      saturate: Boolean
  ) {
    def floating: Boolean = !a.integer

    /** Whether the multiplicands are 16-bit, which alone may be MN-major. */
    def halves: Boolean = a == Element.F16 || a == Element.BF16
  }

  private val Shaped = "m64n([0-9]+)k([0-9]+)".r

  private def element(value: Modifier): Option[Element] = value match {
    case Modifier.F16  => Some(Element.F16)
    case Modifier.Bf16 => Some(Element.BF16)
    case Modifier.Tf32 => Some(Element.TF32)
    case Modifier.F32  => Some(Element.F32)
    case Modifier.S32  => Some(Element.S32)
    case Modifier.S8   => Some(Element.S8)
    case Modifier.U8   => Some(Element.U8)
    case Modifier.B1   => Some(Element.B1)
    case Modifier.E4m3 => Some(Element.E4M3)
    case Modifier.E5m2 => Some(Element.E5M2)
    case _             => None
  }

  private def sparse(instruction: Instruction): Boolean = instruction.form match {
    case Form.WgmmaMmaAsyncSp(_, _, _, _, _, _, _, _, _, _) => true
    case _                                                  => false
  }

  /** The product `instruction`'s qualifiers name: `.sync.aligned` in either order, one shape, D, A and B in that order,
    * `.satfinite` anywhere for integers and `.and.popc` after the types for single bits.
    */
  private def product(instruction: Instruction): Option[Product] = {
    val modifiers = instruction.mods.filterNot(token => token == Modifier.MmaAsync || token == Modifier.Sp)
    val shapes    = modifiers.collect { case Modifier(name) if Shaped.matches(name.raw) => name.raw }
    val types     = modifiers.flatMap(element(_))
    val saturate  = modifiers.contains(Modifier.Satfinite)
    val single    = types.lift(1).contains(Element.B1)
    val known     = modifiers.forall(token =>
      element(token).nonEmpty || PartialFunction.cond(token) {
        case Modifier.Sync | Modifier.Aligned | Modifier.Satfinite => true
      } ||
        (single && (token == Modifier.And || token == Modifier.Popc)) ||
        Shaped.matches(token.name.raw)
    )
    val counted = Vec(Modifier.Sync, Modifier.Aligned).forall(token => modifiers.count(_ == token) == 1) &&
      modifiers.count(_ == Modifier.Satfinite) <= 1
    val bitwise = !single || modifiers.takeRight(2) == Vec(Modifier.And, Modifier.Popc)
    for {
      _      <- Option.when(known && counted && bitwise && shapes.size == 1 && types.size == 3)(())
      (n, k) <- shapes.head match {
        case Shaped(n, k) => (n.toInt.refine[Int > 0].toOption, k.toInt.refine[Int > 0].toOption).tupled
        case _            => None
      }
      _ <- Option.when(!saturate || types(1).integer && types(1) != Element.B1)(())
      _ <- Option.when(
        Warpgroup.supports(
          n = n,
          k = k,
          a = types(1),
          b = types(2),
          d = types(0),
          sparse = sparse(instruction)
        )
      )(())
    } yield Product(n, k, types(0), types(1), types(2), sparse(instruction), saturate)
  }

  /** The registers of D, and of A where it is a register vector, and `None` for the other operands; `None` for an
    * instruction that is not a supported product.
    */
  def fragments(instruction: Instruction): Option[Vec[Option[Int]]] = instruction.form match {
    case Form.WgmmaMmaAsync(_, _, _, _, _, _, _, _) | Form.WgmmaMmaAsyncSp(_, _, _, _, _, _, _, _, _, _) =>
      product(instruction).map { form =>
        val a =
          instruction.operands.lift(1).filter(Operand.isVectorLiteral(_)).map(_ => Warpgroup.MultiplicandRegisters)
        Vec(Some(Warpgroup.accumulatorRegisters(form.n, form.d)), a) ++
          Vec.fill(math.max(instruction.operands.size - 2, 0))(Option.empty[Int])
      }
    case _ => None
  }

  private def check(condition: Boolean, message: NonEmpty[String]): Either[Error, Unit] =
    Either.cond(condition, (), Error.Invalid(message))

  /** The value of an immediate operand among `allowed`. */
  private def immediate(operand: Operand, name: String, allowed: Set[Long]): Either[Error, Long] =
    NumericLiteral.immediate(operand).filter(allowed).toRight(
      Error.Invalid(s"wgmma $name must be one of ${allowed.toSeq.sorted.mkString(", ")}")
    )

  private def lowerProduct(instruction: Instruction, form: Product, operands: Vec[Operand])(using
      scope: Scope
  )
      : Either[Error, VMIR.Instruction] = {
    val extra                   = if (form.sparse) 2 else 0
    val vector                  = operands.lift(1).exists(Operand.isVectorLiteral(_))
    val immediates              = if (!form.floating) 0 else if (!form.halves) 2 else if (vector) 3 else 4
    val shape                   = Shape(64, form.n, form.k)
    val packed                  = if (form.sparse) shape.copy(k = (form.k / 2).unsafe[Int > 0]) else shape
    val immediateAt             = 4 + extra
    def at(index: Int): Operand = operands(immediateAt + index)
    for {
      _ <- check(
        operands.size == immediateAt + immediates,
        s"invalid operand count for ${instruction.qualifiedOpcode}"
      )
      destinations <- check(Operand.isVectorLiteral(operands(0)), "wgmma accumulators are a register vector")
        .flatMap(_ => scope.registers(operands(0)))
      _ <- check(
        destinations.size == Warpgroup.accumulatorRegisters(form.n, form.d),
        s"wgmma accumulators require ${Warpgroup.accumulatorRegisters(form.n, form.d)} registers"
      )
      left     <- if (vector) scope.registers(operands(1)).map(_.map(Value.Register(_))) else Right(Vec.empty[Value])
      _        <- check(left.isEmpty || left.size == Warpgroup.MultiplicandRegisters, "wgmma A requires four registers")
      a        <- if (vector) Right(Vec.empty[Value]) else scope.value(operands(1)).map(Vec(_))
      b        <- scope.value(operands(2))
      sparsity <-
        if (form.sparse)
          (scope.value(operands(3)), immediate(operands(4), "sparsity selector", SparsitySelectors))
            .mapN((metadata, selector) => Some(Sparsity(metadata, Value.Immediate(selector))))
        else Right(None)
      scale <- scope.predicateValue(operands(3 + extra)).map {
        case Value.Immediate(value) => Value.Immediate(if (value != 0L) 1L else 0L)
        case other                  => other
      }
      negateA    <- if (form.floating) immediate(at(0), "imm-scale-a", Set(-1L, 1L)).map(_ < 0L) else Right(false)
      negateB    <- if (form.floating) immediate(at(1), "imm-scale-b", Set(-1L, 1L)).map(_ < 0L) else Right(false)
      transposeA <-
        if (form.halves && !vector) immediate(at(2), "imm-trans-a", Set(0L, 1L)).map(_ != 0L) else Right(false)
      transposeB <-
        if (form.halves) immediate(at(if (vector) 2 else 3), "imm-trans-b", Set(0L, 1L)).map(_ != 0L)
        else Right(false)
    } yield VMIR.Instruction.Tensor(Operation.WarpgroupMultiply(
      Operation.Multiply(
        Fragment(packed, Role.A, form.a, wmma = false),
        Fragment(shape, Role.B, form.b, wmma = false, columnMajor = true),
        Fragment(shape, Role.C, form.d, wmma = false),
        Fragment(shape, Role.C, form.d, wmma = false),
        destinations,
        left,
        Vec.empty,
        destinations.map(Value.Register(_)),
        Matrix.accumulation(
          architecture = 90,
          shape = shape,
          input = form.a,
          result = if (form.a.integer) form.d else Matrix.accumulator(form.d, form.d),
          rounding = ConversionRounding.RN
        ),
        form.saturate,
        Option.when(form.a == Element.B1)(BitOperation.And),
        sparsity
      ),
      a,
      Vec(b),
      scale,
      negateA,
      negateB,
      transposeA,
      transposeB
    ))
  }

  def lower(instruction: Instruction)(using Scope): Either[Error, VMIR.Instruction] = instruction.form match {
    case Form.WgmmaFence() | Form.WgmmaCommitGroup() =>
      check(instruction.mods.toSet == Set(Modifier.Sync, Modifier.Aligned), "wgmma requires .sync.aligned")
        .map(_ => VMIR.Instruction.NoOp)
    case Form.WgmmaWaitGroup(count) =>
      for {
        _ <- check(instruction.mods.toSet == Set(Modifier.Sync, Modifier.Aligned), "wgmma requires .sync.aligned")
        _ <- NumericLiteral.immediate(count).filter(_ >= 0L).toRight(
          Error.Invalid("wgmma.wait_group takes a non-negative integer constant")
        )
      } yield VMIR.Instruction.NoOp
    case _ =>
      product(instruction)
        .toRight(Error.Unsupported(instruction.qualifiedOpcode, "unsupported wgmma qualifiers"))
        .flatMap(lowerProduct(instruction, _, instruction.operands))
  }
}
