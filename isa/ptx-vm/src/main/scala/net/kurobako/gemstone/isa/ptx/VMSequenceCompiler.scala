package net.kurobako.gemstone.isa.ptx

import Lowering.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as _, *}
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Lowers PTX instructions that take several VM steps: four-value stochastic conversions, field extension, carry-less
  * multiply-add, three-input extrema, the predicate form of `lop3`, warp matching and floating reductions, mbarrier
  * layout and report queries, and the cache policies, fences and breakpoints that change nothing the VM models.
  */
private object VMSequenceCompiler {
  private val Axes = Vec(Axis.X, Axis.Y, Axis.Z)

  private type Lowered = Either[Error, Vec[VMIR.Instruction]]

  private def unsupported(instruction: Instruction, detail: String): Lowered =
    Left(Error.Unsupported(instruction.qualifiedOpcode, detail))

  private def temporary(name: String)(using scope: Scope): Either[Error, Register] =
    scope.register(Operand.Register(name.unsafe[NonEmpty[String]], None))

  private def temporaryPredicate(name: String)(using scope: Scope): Either[Error, Predicate] =
    scope.predicate(Operand.Register(name.unsafe[NonEmpty[String]], None))

  private val MBarrierQualifiers: Set[Modifier] = Set(
    Modifier.Acquire,
    Modifier.Relaxed,
    Modifier.Cta,
    Modifier.Cluster,
    Modifier.Shared,
    Modifier.SharedCta,
    Modifier.B64,
    Modifier.LayoutV0
  )

  /** The destination format of an integer-typed `cvt` when it is signed and narrower than a register. */
  private def narrowSigned(modifiers: Vec[Modifier]): Option[NumericFormat] =
    modifiers.flatMap(VMInstructionCompiler.format(_)).reverse match {
      case Vec(_, destination @ NumericFormat.Signed(bits), _*) if bits < 32 => Some(destination)
      case _                                                                 => None
    }

  /** The lowering of `instruction`, or None when a single-step family owns it. */
  def lower(instruction: Instruction, modifiers: Vec[Modifier])(using scope: Scope): Option[Lowered] = {
    def has(modifier: Modifier): Boolean = modifiers.contains(modifier)
    def are(values: Modifier*): Boolean  = modifiers.toSet == values.toSet && modifiers.size == values.size
    instruction.form match {
      case Form.Cvt(destination, Operand.VectorLiteral(Vec(a, b, e, f)), Some(bits), None)
          if has(Modifier.Rs) =>
        Some(stochasticQuad(instruction, modifiers, destination, Vec(a, b, e, f), bits))
      case Form.Atom(destination, address, value, policy) if lanes(modifiers).nonEmpty =>
        Some(vectorAtomic(instruction, Some(destination), address, value, policy, modifiers))
      case Form.Red(address, value, policy) if lanes(modifiers).nonEmpty =>
        Some(vectorAtomic(instruction, None, address, value, policy, modifiers))
      case Form.Fma(dest = destination)
          if has(Modifier.Relu) && has(Modifier.Rn) && !has(Modifier.Sat) =>
        val unclamped = modifiers.filterNot(_ == Modifier.Relu)
        Some(for {
          fused <-
            VMInstructionCompiler.lowerOperation(
              instruction.copy(mods = instruction.mods.filterNot(_ == Modifier.Relu)),
              unclamped
            )
          target <- scope.register(destination)
        } yield Vec(
          fused,
          // the maximum with +0 propagates a NaN as the canonical one, as .relu does
          VMIR.Instruction.FloatBinaryPacked(
            FloatOperation.Maximum,
            target,
            Value.Register(target),
            Value.Immediate(0L),
            if (has(Modifier.Bf16) || has(Modifier.Bf16x2)) PackedFloatFormat.BF16x2 else PackedFloatFormat.F16x2
          )
        ))
      case Form.Fma(destination, left, right, addend) if has(Modifier.F32x2) =>
        Some(pairFma(destination, left, right, addend, modifiers))
      case Form.Setp(Operand.WithPredicate(low, high), left, right, other)
          if modifiers.lastOption.exists(_.isPackedHalf) =>
        Some(packedComparison(low, high, left, right, other, modifiers))
      case Form.Set(destination, left, right, Some(other))
          if modifiers.lastOption.exists(_.isPackedHalf) =>
        Some(packedSet(destination, left, right, other, modifiers))
      case Form.Cvt(destination, source, second, third) if narrowConversion(modifiers).nonEmpty =>
        Some(convert(instruction, destination, source, second, third, modifiers))
      // a register wider than a signed destination format holds the result sign-extended (PTX ISA 6.5.1)
      case Form.Cvt(dest = destination, secondarySrc = None, roundingBits = None)
          if narrowSigned(modifiers).nonEmpty =>
        Some(for {
          converted <- VMInstructionCompiler.lowerOperation(instruction, modifiers)
          target    <- scope.register(destination)
          format    <- narrowSigned(modifiers).toRight(Error.Invalid("expected a signed destination format"))
        } yield Vec(
          converted,
          VMIR.Instruction.Convert(target, Value.Register(target), format, NumericFormat.Signed(64), None)
        ))
      case Form.ClusterLaunchControlQueryCancel(destination, response)
          if instruction.mods == Vec(Modifier.GetFirstCtaid, Modifier.V4, Modifier.B32, Modifier.B128) =>
        // the fourth component has no value; PTX spells it _
        Some(for {
          targets <- scope.sinks(destination)
          parts   <- scope.values(response).flatMap(pair(_, "cluster cancellation response"))
          _       <- Either.cond(targets.size == 4, (), Error.Invalid("get_first_ctaid.v4 writes four components"))
        } yield Axes.zip(targets).flatMap((axis, target) =>
          target.toVec.map(VMIR.Instruction.ClusterCancelCta(_, parts(0), parts(1), axis))
        ))
      case Form.Szext(destination, source, width)
          if are(Modifier.Clamp, Modifier.U32) || are(Modifier.Clamp, Modifier.S32) ||
            are(Modifier.Wrap, Modifier.U32) || are(Modifier.Wrap, Modifier.S32) =>
        Some(szext(destination, source, width, clamp = has(Modifier.Clamp), signed = has(Modifier.S32)))
      case Form.Clmad(destination, left, right, addend)
          if are(Modifier.Lo, Modifier.U64) || are(Modifier.Hi, Modifier.U64) =>
        Some(for {
          product <- temporary("clmad product")
          target  <- scope.register(destination)
          a       <- scope.value(left)
          b       <- scope.value(right)
          c       <- scope.value(addend)
        } yield Vec(
          VMIR.Instruction.IntegerBinary(IntegerOperation.CarrylessMultiply(has(Modifier.Hi)), product, a, b, 64),
          VMIR.Instruction.IntegerBinary(IntegerOperation.BitwiseXor, target, Value.Register(product), c, 64)
        ))
      // the VM keeps no caches, so every policy is the same and its operands go unread
      case Form.CreatePolicy(cachePolicy = destination) =>
        Some(scope.register(destination).map(target => Vec(VMIR.Instruction.Move(target, Value.Immediate(0L), 64))))
      // without a debugger, a breakpoint stops the kernel
      case Form.Brkpt() if modifiers.isEmpty => Some(Right(Vec(VMIR.Instruction.Trap)))
      case Form.Membar() if are(Modifier.Proxy, Modifier.Alias) || are(Modifier.Proxy, Modifier.Async) =>
        Some(Right(Vec(VMIR.Instruction.Fence)))
      case Form.Fence(None, None)
          if are(Modifier.Release, Modifier.SyncRestrictSharedCta, Modifier.Cluster) =>
        Some(Right(Vec(VMIR.Instruction.Fence)))
      case Form.Min(destination, left, right, Some(third)) if threeInput(modifiers) =>
        Some(extremum(Extremum.Minimum, destination, left, right, third, modifiers))
      case Form.Max(destination, left, right, Some(third)) if threeInput(modifiers) =>
        Some(extremum(Extremum.Maximum, destination, left, right, third, modifiers))
      case Form.Lop3(destination, first, second, third, table, Some(other))
          if are(Modifier.And, Modifier.B32) || are(Modifier.Or, Modifier.B32) =>
        Some(logic(
          destination,
          first,
          second,
          third,
          table,
          other,
          if (has(Modifier.And)) PredicateOperation.And
          else PredicateOperation.Or
        ))
      case Form.MatchAnySync(destination, source, memberMask) if matchType(modifiers).nonEmpty =>
        Some(matching(destination, source, memberMask, if (has(Modifier.B64)) 64 else 32, MatchKind.Any))
      case Form.MatchAllSync(destination, source, memberMask) if matchType(modifiers).nonEmpty =>
        Some(matching(destination, source, memberMask, if (has(Modifier.B64)) 64 else 32, MatchKind.All))
      case Form.ReduxSync(destination, source, memberMask)
          if has(Modifier.F32) && (has(Modifier.Min) || has(Modifier.Max)) &&
            modifiers.forall(PartialFunction.cond(_) {
              case Modifier.Sync | Modifier.Min | Modifier.Max | Modifier.Abs | Modifier.NaN | Modifier.F32 => true
            }) =>
        Some((scope.register(destination), scope.value(source), scope.value(memberMask)).mapN((target, value, mask) =>
          Vec(VMIR.Instruction.WarpReduceFloat(
            destination = target,
            source = value,
            memberMask = mask,
            maximum = has(Modifier.Max),
            absolute = has(Modifier.Abs),
            propagateNaN = has(Modifier.NaN)
          ))
        ))
      case Form.MbarrierCheckLayout(destination, address)
          if modifiers.forall(PartialFunction.cond(_) {
            case Modifier.CheckLayout | Modifier.LayoutV0 | Modifier.LayoutV1 | Modifier.SharedCta | Modifier.Shared |
                Modifier.B64 => true
          }) && (has(Modifier.LayoutV0) != has(Modifier.LayoutV1)) =>
        // every mbarrier the VM creates has the v0 layout, which tracks no fabric reports
        Some((scope.predicate(destination), scope.address(address)).mapN((target, _) =>
          Vec(VMIR.Instruction.MovePredicate(target, Value.Immediate(if (has(Modifier.LayoutV0)) 1L else 0L)))
        ))
      case Form.MbarrierTestWait(completion, report, address, phase)
          if waitForm(modifiers, Modifier.TestWait) =>
        Some(waitWithReport(completion, report, address, phase, has(Modifier.Parity)))
      case Form.MbarrierTryWait(
            completion = completion,
            reportValue = report,
            barrierAddr = address,
            stateOrPhase = phase
          ) if waitForm(modifiers, Modifier.TryWait) =>
        Some(waitWithReport(completion, report, address, phase, has(Modifier.Parity)))
      case _ => None
    }
  }

  private def matchType(modifiers: Vec[Modifier]): Option[Modifier] =
    modifiers.filterNot(PartialFunction.cond(_) { case Modifier.Any | Modifier.All | Modifier.Sync => true }) match {
      case Vec(kind @ (Modifier.B32 | Modifier.B64)) => Some(kind)
      case _                                         => None
    }

  private def lanes(modifiers: Vec[Modifier]): Option[Int] = modifiers.collectFirst {
    case Modifier.V2 => 2
    case Modifier.V4 => 4
    case Modifier.V8 => 8
  }

  private def displaced(address: Value, bytes: Long): Value = address match {
    case _ if bytes == 0L            => address
    case Value.Immediate(value)      => Value.Immediate(value + bytes)
    case Value.Address(base, offset) => Value.Address(base, offset + bytes)
    case other                       => Value.Address(other, bytes)
  }

  /** A vector atomic or reduction: each element is an atomic operation of its own on consecutive elements. */
  private def vectorAtomic(
      instruction: Instruction,
      destination: Option[Operand],
      address: Operand,
      value: Operand,
      policy: Option[Operand],
      modifiers: Vec[Modifier]
  )(using scope: Scope): Lowered = {
    val (space, rest) = VMInstructionCompiler.atomicSpace(modifiers)
    val count         = lanes(modifiers).getOrElse(1)
    VMInstructionCompiler.atomicOperation(rest.filterNot(PartialFunction.cond(_) {
      case Modifier.V2 | Modifier.V4 | Modifier.V8 => true
    })) match {
      case Some((kind, operation)) if policy.isEmpty == !modifiers.contains(Modifier.L2CacheHint) =>
        val width = VMInstructionCompiler.atomicWidth(kind)
        for {
          _       <- policy.traverse(scope.value(_))
          sinks   <- destination.traverse(scope.registers(_))
          sources <- scope.values(value)
          target  <- scope.address(address)
          _       <- Either.cond(
            sources.size == count && sinks.forall(_.size == count),
            (),
            Error.Invalid(s"a .v$count atomic takes $count elements")
          )
        } yield Vec.tabulate(count)(index =>
          VMIR.Instruction.Atomic(
            sinks.map(_(index)),
            displaced(target, index.toLong * width / 8),
            sources(index),
            width,
            space,
            operation
          )
        )
      case _ => unsupported(instruction, "unsupported vector atomic")
    }
  }

  /** The forms the asynchronous family leaves: a phase type, or a report predicate and value. Under the v0 layout of
    * every VM mbarrier the primary and conditional phases complete together and the report is zero.
    */
  private def waitForm(modifiers: Vec[Modifier], operation: Modifier): Boolean =
    modifiers.forall(MBarrierQualifiers ++ Set(
      operation,
      Modifier.Parity,
      Modifier.PhaseTypePrimary,
      Modifier.PhaseTypeConditional
    )) && (!modifiers.contains(Modifier.PhaseTypeConditional) || modifiers.contains(Modifier.Parity)) &&
      !(modifiers.contains(Modifier.PhaseTypePrimary) && modifiers.contains(Modifier.PhaseTypeConditional))

  private def waitWithReport(
      completion: Operand,
      report: Option[Operand],
      address: Operand,
      phase: Operand,
      parity: Boolean
  )(using scope: Scope): Lowered = {
    val (complete, reported) = Operand.splitPredicate(completion)
    for {
      done     <- scope.predicate(complete)
      flag     <- reported.traverse(scope.predicate(_))
      value    <- report.traverse(scope.register(_))
      barrier  <- scope.address(address)
      expected <- scope.value(phase)
    } yield VMIR.Instruction.MBarrierTest(done, barrier, expected, parity) +:
      (flag.toVec.map(VMIR.Instruction.MovePredicate(_, Value.Immediate(0L))) ++
        value.toVec.map(VMIR.Instruction.Move(_, Value.Immediate(0L), 64)))
  }

  /** `fma.f32x2`: binary32 fused multiply-adds of the low and of the high words of three register pairs. */
  private def pairFma(destination: Operand, left: Operand, right: Operand, addend: Operand, modifiers: Vec[Modifier])(
      using scope: Scope
  ): Lowered = {
    val rounding = modifiers.collectFirst {
      case Modifier.Rz => ConversionRounding.RZ
      case Modifier.Rm => ConversionRounding.RM
      case Modifier.Rp => ConversionRounding.RP
    }.getOrElse(ConversionRounding.RN)
    val control                                = FloatControl(flushSubnormal = modifiers.contains(Modifier.Ftz))
    def halves(operand: Operand, name: String) = for {
      value <- scope.value(operand)
      low   <- temporary(s"fma $name low")
      high  <- temporary(s"fma $name high")
    } yield (VMIR.Instruction.MoveSplit(Vec(Some(low), Some(high)), value, 32), low, high)
    for {
      (splitA, aLow, aHigh) <- halves(left, "a")
      (splitB, bLow, bHigh) <- halves(right, "b")
      (splitC, cLow, cHigh) <- halves(addend, "c")
      low                   <- temporary("fma result low")
      high                  <- temporary("fma result high")
      target                <- scope.register(destination)
    } yield Vec(
      splitA,
      splitB,
      splitC,
      VMIR.Instruction.FusedMultiplyAdd(
        low,
        Value.Register(aLow),
        Value.Register(bLow),
        Value.Register(cLow),
        32,
        control,
        rounding
      ),
      VMIR.Instruction.FusedMultiplyAdd(
        high,
        Value.Register(aHigh),
        Value.Register(bHigh),
        Value.Register(cHigh),
        32,
        control,
        rounding
      ),
      VMIR.Instruction.MoveCombine(target, Vec(Value.Register(low), Value.Register(high)), 32)
    )
  }

  /** The relation, the Boolean combination and its predicate operand, and the halves' format of a packed comparison. */
  private def packedRelation(other: Option[Operand], modifiers: Vec[Modifier])(using
      scope: Scope
  ): Either[Error, (Comparison, Option[(PredicateOperation, Predicate, Boolean)], NumericFormat)] = {
    val combine = modifiers.collectFirst {
      case Modifier.And => PredicateOperation.And
      case Modifier.Or  => PredicateOperation.Or
      case Modifier.Xor => PredicateOperation.Xor
    }
    for {
      relation <- modifiers.headOption.flatMap(VMInstructionCompiler.comparison(_))
        .toRight(Error.Invalid("a packed comparison starts with its relation"))
      combined <- (combine, other) match {
        case (Some(operation), Some(operand)) =>
          scope.predicateSource(operand).map((predicate, negated) => Some((operation, predicate, negated)))
        case (None, None) => Right(None)
        case _            => Left(Error.Invalid("a Boolean operator takes a predicate operand, and only it does"))
      }
    } yield (
      relation,
      combined,
      if (modifiers.lastOption.contains(Modifier.Bf16x2)) NumericFormat.BF16 else NumericFormat.IEEE(16)
    )
  }

  private def halves(operand: Operand, name: String)(using scope: Scope) = for {
    value <- scope.value(operand)
    low   <- temporary(s"$name low half")
    high  <- temporary(s"$name high half")
  } yield (VMIR.Instruction.MoveSplit(Vec(Some(low), Some(high)), value, 16), Value.Register(low), Value.Register(high))

  private def compare(
      destination: Predicate,
      relation: Comparison,
      format: NumericFormat,
      left: Value,
      right: Value,
      flush: Boolean,
      combined: Option[(PredicateOperation, Predicate, Boolean)]
  ): VMIR.Instruction = combined match {
    case Some((operation, predicate, negated)) =>
      VMIR.Instruction.SetPredicateCombined(
        destination,
        relation,
        format,
        left,
        right,
        flush,
        operation,
        predicate,
        negated
      )
    case None => VMIR.Instruction.SetPredicate(destination, relation, format, left, right, flush)
  }

  /** `setp` of a pair of halves: the low halves' result in `p` and the high halves' in `q`. */
  private def packedComparison(
      low: Operand,
      high: Operand,
      left: Operand,
      right: Operand,
      other: Option[Operand],
      modifiers: Vec[Modifier]
  )(using scope: Scope): Lowered = for {
    (relation, combined, format) <- packedRelation(other, modifiers)
    (splitA, aLow, aHigh)        <- halves(left, "setp a")
    (splitB, bLow, bHigh)        <- halves(right, "setp b")
    p                            <- scope.predicate(low)
    q                            <- scope.predicate(high)
  } yield {
    val flush = modifiers.contains(Modifier.Ftz)
    Vec(
      splitA,
      splitB,
      compare(p, relation, format, aLow, bLow, flush, combined),
      compare(q, relation, format, aHigh, bHigh, flush, combined)
    )
  }

  /** `set` with a Boolean operator on a pair of halves: each result half holds the type's true value where its combined
    * comparison holds.
    */
  private def packedSet(destination: Operand, left: Operand, right: Operand, other: Operand, modifiers: Vec[Modifier])(
      using scope: Scope
  ): Lowered = {
    val truth = modifiers.reverse.lift(1).collect {
      case Modifier.F16x2              => 0x3c00L
      case Modifier.Bf16x2             => 0x3f80L
      case Modifier.U32 | Modifier.S32 => 0xffffL
    }
    for {
      value                        <- truth.toRight(Error.Invalid("unsupported packed set result type"))
      (relation, combined, format) <- packedRelation(Some(other), modifiers)
      (splitA, aLow, aHigh)        <- halves(left, "set a")
      (splitB, bLow, bHigh)        <- halves(right, "set b")
      p                            <- temporaryPredicate("set low holds")
      q                            <- temporaryPredicate("set high holds")
      lowHalf                      <- temporary("set low result")
      highHalf                     <- temporary("set high result")
      target                       <- scope.register(destination)
    } yield {
      val flush = modifiers.contains(Modifier.Ftz)
      Vec(
        splitA,
        splitB,
        compare(p, relation, format, aLow, bLow, flush, combined),
        compare(q, relation, format, aHigh, bHigh, flush, combined),
        VMIR.Instruction.Select(lowHalf, Value.Immediate(value), Value.Immediate(0L), p, 32),
        VMIR.Instruction.Select(highHalf, Value.Immediate(value << 16), Value.Immediate(0L), q, 32),
        VMIR.Instruction.IntegerBinary(
          IntegerOperation.BitwiseOr,
          target,
          Value.Register(lowHalf),
          Value.Register(highHalf),
          32
        )
      )
    }
  }

  private val narrowFormats: Map[Modifier, NarrowFormat] = Map(
    Modifier.E4m3x2  -> NarrowFormat.E4M3,
    Modifier.E5m2x2  -> NarrowFormat.E5M2,
    Modifier.E2m3x2  -> NarrowFormat.E2M3,
    Modifier.E3m2x2  -> NarrowFormat.E3M2,
    Modifier.E2m1x2  -> NarrowFormat.E2M1,
    Modifier.Ue8m0x2 -> NarrowFormat.UE8M0,
    Modifier.S2f6x2  -> NarrowFormat.S2F6
  )

  /** `cvt.rs` to four 8-, 6- or 4-bit values: the low pair from `e` and `f` into a temporary, then the high pair from
    * `a` and `b` over it.
    */
  private def stochasticQuad(
      instruction: Instruction,
      modifiers: Vec[Modifier],
      destination: Operand,
      sources: Vec[Operand],
      bits: Operand
  )(using scope: Scope): Either[Error, Vec[VMIR.Instruction]] = {
    val format = modifiers.filterNot(PartialFunction.cond(_) { case Modifier.Rs | Modifier.Relu => true }) match {
      case Vec(Modifier.Satfinite, Modifier.E4m3x4, Modifier.F32) => Some(StochasticFormat.E4M3)
      case Vec(Modifier.Satfinite, Modifier.E5m2x4, Modifier.F32) => Some(StochasticFormat.E5M2)
      case Vec(Modifier.Satfinite, Modifier.E2m3x4, Modifier.F32) => Some(StochasticFormat.E2M3)
      case Vec(Modifier.Satfinite, Modifier.E3m2x4, Modifier.F32) => Some(StochasticFormat.E3M2)
      case Vec(Modifier.Satfinite, Modifier.E2m1x4, Modifier.F32) => Some(StochasticFormat.E2M1)
      case _                                                      => None
    }
    val relu = modifiers.contains(Modifier.Relu)
    for {
      chosen  <- format.toRight(Error.Unsupported(instruction.qualifiedOpcode, "unsupported stochastic conversion"))
      target  <- scope.register(destination)
      values  <- sources.traverse(scope.value(_))
      random  <- scope.value(bits)
      partial <- scope.register(Operand.Register("stochastic temporary", None))
    } yield Vec(
      VMIR.Instruction.ConvertStochastic(
        partial,
        values(2),
        values(3),
        random,
        Value.Immediate(0L),
        chosen,
        false,
        relu,
        true
      ),
      VMIR.Instruction.ConvertStochastic(
        target,
        values(0),
        values(1),
        random,
        Value.Register(partial),
        chosen,
        true,
        relu,
        true
      )
    )
  }

  /** The paired narrow conversion `modifiers` name: the destination and source types, in order, where one is a narrow
    * pair; the 8-bit floating forms of binary32 and binary16 pairs belong to the floating family.
    */
  private def narrowConversion(modifiers: Vec[Modifier]): Option[(Modifier, Modifier)] =
    modifiers.filter(modifier =>
      narrowFormats.contains(modifier) || PartialFunction.cond(modifier) {
        case Modifier.F32 | Modifier.F16x2 | Modifier.Bf16x2 => true
      }
    ) match {
      case Vec(target, source) if narrowFormats.contains(target) || narrowFormats.contains(source) =>
        val float8 = Set(Modifier.E4m3x2, Modifier.E5m2x2)
        Option.unless(
          (float8(target) && source != Modifier.Bf16x2) || (float8(source) && target != Modifier.Bf16x2)
        )((target, source))
      case _ => None
    }

  private def convert(
      instruction: Instruction,
      destination: Operand,
      source: Operand,
      second: Option[Operand],
      third: Option[Operand],
      modifiers: Vec[Modifier]
  )(using scope: Scope): Lowered = {
    val (target, origin) = narrowConversion(modifiers).getOrElse((Modifier.F32, Modifier.F32))
    val relu             = modifiers.contains(Modifier.Relu)
    val saturate         = modifiers.contains(Modifier.Satfinite)
    val scaled           = modifiers.contains(Modifier.ScaledN2Ue8m0)
    val rounding         = modifiers.collectFirst {
      case Modifier.Rn => ConversionRounding.RN
      case Modifier.Rz => ConversionRounding.RZ
      case Modifier.Rp => ConversionRounding.RP
    }
    val narrowing    = narrowFormats.contains(target)
    val pair         = narrowing && origin == Modifier.F32
    val format       = narrowFormats.getOrElse(if (narrowing) target else origin, NarrowFormat.E2M1)
    val (low, scale) = if (pair) (second, third) else (None, second.orElse(third))
    val valid        =
      if (format == NarrowFormat.UE8M0)
        if (narrowing) (rounding.contains(ConversionRounding.RZ) || rounding.contains(ConversionRounding.RP)) && !relu
        else rounding.contains(ConversionRounding.RN) && !relu && !saturate && target == Modifier.Bf16x2
      else rounding.contains(ConversionRounding.RN) && (!narrowing || saturate)
    if (!valid) unsupported(instruction, "unsupported narrow conversion qualifiers")
    else if (scaled && !(format == NarrowFormat.S2F6 || (!narrowing && target == Modifier.Bf16x2)))
      unsupported(instruction, "only S2F6 conversions and widening to bfloat16 take scale factors")
    else if (scaled != scale.nonEmpty || (third.nonEmpty && !pair))
      Left(Error.Invalid(".scaled::n2::ue8m0 takes a scale factor operand, and only it does"))
    else if (pair != low.nonEmpty) Left(Error.Invalid("only a binary32 pair conversion takes two sources"))
    else
      for {
        register <- scope.register(destination)
        value    <- scope.value(source)
        factors  <- scale.traverse(scope.value(_)).map(_.getOrElse(Value.Immediate(0x7f7fL)))
        other    <- low.traverse(scope.value(_)).map(_.getOrElse(Value.Immediate(0L)))
      } yield Vec(
        if (narrowing)
          VMIR.Instruction.ConvertNarrow(
            register,
            value,
            other,
            factors,
            format,
            !pair,
            origin == Modifier.Bf16x2,
            rounding.getOrElse(ConversionRounding.RN),
            relu,
            saturate
          )
        else VMIR.Instruction.ConvertWiden(register, value, factors, format, target == Modifier.Bf16x2, relu, saturate)
      )
  }

  private def threeInput(modifiers: Vec[Modifier]): Boolean =
    modifiers.contains(Modifier.F32) &&
      modifiers.forall(PartialFunction.cond(_) {
        case Modifier.F32 | Modifier.Ftz | Modifier.NaN | Modifier.Abs | Modifier.Relu => true
      })

  /** `min` and `max` of three binary32 values: the two-input operation on the first pair, then on the third. */
  private def extremum(
      kind: Extremum,
      destination: Operand,
      left: Operand,
      right: Operand,
      third: Operand,
      modifiers: Vec[Modifier]
  )(using scope: Scope): Lowered = {
    val propagate = modifiers.contains(Modifier.NaN)
    val operation =
      if (kind == Extremum.Maximum) if (propagate) FloatOperation.Maximum else FloatOperation.MaximumNumber
      else if (propagate) FloatOperation.Minimum
      else FloatOperation.MinimumNumber
    val control = FloatControl(
      flushSubnormal = modifiers.contains(Modifier.Ftz),
      propagateNaN = propagate,
      absolute = modifiers.contains(Modifier.Abs)
    )
    if (modifiers.contains(Modifier.Relu)) Left(Error.Invalid("relu does not apply to binary32 min and max"))
    else
      for {
        pair   <- temporary("extremum pair")
        target <- scope.register(destination)
        a      <- scope.value(left)
        b      <- scope.value(right)
        c      <- scope.value(third)
      } yield Vec(
        VMIR.Instruction.FloatBinary(operation, pair, a, b, 32, control),
        VMIR.Instruction.FloatBinary(operation, target, Value.Register(pair), c, 32, control)
      )
  }

  private def szext(destination: Operand, source: Operand, width: Operand, clamp: Boolean, signed: Boolean)(using
      scope: Scope
  ): Lowered = for {
    length <- temporary("szext width")
    target <- scope.register(destination)
    value  <- scope.value(source)
    bits   <- scope.value(width)
  } yield Vec(
    if (clamp) VMIR.Instruction.IntegerBinary(IntegerOperation.Minimum(false), length, bits, Value.Immediate(32L), 32)
    else VMIR.Instruction.IntegerBinary(IntegerOperation.BitwiseAnd, length, bits, Value.Immediate(31L), 32),
    VMIR.Instruction.BitFieldExtract(target, value, Value.Immediate(0L), Value.Register(length), 32, signed)
  )

  /** `lop3.BoolOp`: the table's result, then the predicate of whether it is nonzero combined with `other`. */
  private def logic(
      destination: Operand,
      first: Operand,
      second: Operand,
      third: Operand,
      table: Operand,
      other: Operand,
      operation: PredicateOperation
  )(using scope: Scope): Lowered = {
    val (result, predicate) = Operand.splitPredicate(destination)
    for {
      lut          <- logicTable(table)
      target       <- predicate.toRight(Error.Invalid("lop3 with a Boolean operation requires a predicate destination"))
      combined     <- scope.predicate(target)
      sink         <- scope.sink(result)
      register     <- sink.fold(temporary("lop3 result"))(Right(_))
      nonzero      <- temporaryPredicate("lop3 nonzero")
      a            <- scope.value(first)
      b            <- scope.value(second)
      c            <- scope.value(third)
      (q, negated) <- scope.predicateSource(other)
      inverse      <- if (negated) temporaryPredicate("lop3 operand").map(Option(_)) else Right(Option.empty[Predicate])
    } yield Vec(
      VMIR.Instruction.Logic3(register, a, b, c, lut, 32),
      VMIR.Instruction.SetPredicate(
        nonzero,
        Comparison.NotEqual,
        NumericFormat.Unsigned(32),
        Value.Register(register),
        Value.Immediate(0L)
      )
    ) ++ inverse.toVec.map(VMIR.Instruction.PredicateUnary(_, q)) :+
      VMIR.Instruction.PredicateBinary(operation, combined, nonzero, inverse.getOrElse(q))
  }

  private enum MatchKind derives CanEqual { case Any, All }

  private def matching(
      destination: Operand,
      source: Operand,
      memberMask: Operand,
      width: ScalarBitWidth,
      kind: MatchKind
  )(
      using scope: Scope
  ): Lowered = {
    val all               = kind == MatchKind.All
    val (mask, predicate) = destination match {
      case Operand.WithPredicate(mask, predicate) if all => (mask, Some(predicate))
      case other                                         => (other, None)
    }
    (
      scope.sink(mask),
      predicate.traverse(predicate =>
        predicate match {
          case Operand.Symbol(name) if name.raw == "_" => Right(None)
          case other                                   => scope.predicate(other).map(Some(_))
        }
      ).map(_.flatten),
      scope.value(source),
      scope.value(memberMask)
    ).mapN((target, flag, value, members) =>
      Vec(VMIR.Instruction.WarpMatch(target, flag, value, members, width, all))
    )
  }
}
