package net.kurobako.gemstone.isa.nvidia

import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** The staged semantics against the interpreter's hand-written formulas they replaced, kept here as the reference. */
class StagedReferenceSuite extends munit.FunSuite {
  private val register  = Register(0)
  private val predicate = Predicate(0)
  private val operand   = Value.Immediate(0L)

  private val samples: Vector[Long] = {
    val random = scala.util.Random(17)
    Vector(
      0L,
      1L,
      2L,
      7L,
      8L,
      15L,
      16L,
      31L,
      32L,
      33L,
      63L,
      64L,
      0x7fL,
      0x80L,
      0xffL,
      0x100L,
      0x7fffL,
      0x8000L,
      0xffffL,
      0x7fffffffL,
      0x80000000L,
      0xffffffffL,
      0x100000000L,
      Long.MaxValue,
      Long.MinValue,
      -1L,
      -2L
    ) ++
      Vector.fill(48)(random.nextLong()) ++ Vector.fill(48)(random.nextInt(80).toLong) ++
      Vector.fill(32)(random.nextLong() & 0xffffffffL)
  }

  private val small: Vector[Long] = samples.filter(value => value >= 0L && value < 300L) ++ samples.take(30)

  private val widths: Vector[ScalarBitWidth] = Vector(8, 16, 32, 64)

  private val Negations: Vector[(Boolean, Boolean, Boolean)] = Vector(
    (false, false, false),
    (true, false, false),
    (false, true, false),
    (false, false, true),
    (true, true, true)
  )

  // The interpreter's formulas before staging.

  private def flushFloat32(bits: Long, enabled: Boolean): Long =
    if (enabled && (bits & 0x7f800000L) == 0) bits & 0x80000000L else bits

  private def bitFind(value: Long, width: ScalarBitWidth, signed: Boolean, shiftAmount: Boolean): Long = {
    val bits   = truncate(value, width)
    val search =
      if (signed && ((bits >>> (width.raw - 1)) & 1L) != 0L) truncate(~bits, width)
      else bits
    val position =
      if (search == 0L) -1
      else if (width == 32) 31 - Integer.numberOfLeadingZeros(search.toInt)
      else 63 - java.lang.Long.numberOfLeadingZeros(search)
    if (position < 0) 0xffffffffL
    else if (shiftAmount) (width.raw - 1 - position).toLong
    else position.toLong
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def dotProduct4(left: Long, right: Long, addend: Long, leftSigned: Boolean, rightSigned: Boolean): Long = {
    var result = signExtend(truncate(addend, 32), 32)
    var index  = 0
    while (index < 4) {
      val leftByte   = (left >>> (index * 8)) & 0xffL
      val rightByte  = (right >>> (index * 8)) & 0xffL
      val leftValue  = if (leftSigned && leftByte >= 0x80L) leftByte - 0x100L else leftByte
      val rightValue = if (rightSigned && rightByte >= 0x80L) rightByte - 0x100L else rightByte
      result += leftValue * rightValue
      index += 1
    }
    truncate(result, 32)
  }

  private def extractBits(source: Long, start: Long, count: Long, width: ScalarBitWidth, signed: Boolean): Long = {
    val sourceBits = truncate(source, width)
    val bitStart   = (start & 0xffL).toInt
    val bitCount   = (count & 0xffL).toInt
    if (bitCount == 0) 0L
    else if (bitStart >= width)
      if (signed && (sourceBits & (1L << (width.raw - 1))) != 0L) truncate(-1L, width) else 0L
    else {
      val extractedWidth    = Math.min(bitCount, width.raw - bitStart)
      val mask              = if (extractedWidth == 64) -1L else (1L << extractedWidth) - 1L
      val extracted         = (sourceBits >>> bitStart) & mask
      val extractedBitWidth = extractedWidth.unsafe[Int in 1 ~ 64]
      truncate(if (signed) Bits.signExtend(extracted, extractedBitWidth) else extracted, width)
    }
  }

  private def bitMask(start: Long, count: Long, mode: BitMaskMode): Long = {
    val rawStart         = start & 0xffffffffL
    val rawCount         = count & 0xffffffffL
    val start1           = rawStart & 0x1fL
    val count1           = rawCount & 0x1fL
    val positionOverflow = mode == BitMaskMode.Clamp && rawStart >= 32L
    val widthOverflow    = mode == BitMaskMode.Clamp && rawCount >= 32L
    val sum              = start1 + count1
    val mask0            =
      if (positionOverflow) 0L else truncate(-1L << start1.toInt, 32)
    val mask1 =
      if (sum >= 32L || positionOverflow || widthOverflow) 0L
      else if (count1 == 0L) 0xffffffffL
      else truncate(-1L << sum.toInt, 32)
    truncate(mask0 & ~mask1, 32)
  }

  private def insertBits(insert: Long, base: Long, start: Long, count: Long, width: ScalarBitWidth): Long = {
    val bitStart = (start & 0xffL).toInt
    val bitCount = (count & 0xffL).toInt
    if (bitCount == 0 || bitStart >= width) truncate(base, width)
    else {
      val insertedWidth    = Math.min(bitCount, width.raw - bitStart)
      val fieldMask        = if (insertedWidth == 64) -1L else ((1L << insertedWidth) - 1L) << bitStart
      val insertedBitWidth = insertedWidth.unsafe[Int in 1 ~ 64]
      truncate((base & ~fieldMask) | ((insert & Bits.truncate(-1L, insertedBitWidth)) << bitStart), width)
    }
  }

  private def permuteBytes(left: Long, right: Long, control: Long, mode: BytePermuteMode): Long = {
    val source = Array.tabulate(8) { index =>
      val value = if (index < 4) left else right
      ((value >>> (8 * (index & 3))) & 0xffL).toInt
    }
    val selector      = (control & 3L).toInt
    val selectedBytes = mode match {
      case BytePermuteMode.Default => Array.tabulate(4)(index => ((control >>> (4 * index)) & 7L).toInt)
      case BytePermuteMode.F4E     => Array.tabulate(4)(index => (selector + index) & 7)
      case BytePermuteMode.B4E     => Array.tabulate(4)(index => (selector - index) & 7)
      case BytePermuteMode.RC8     => Array.fill(4)(selector)
      case BytePermuteMode.ECL     => Array.tabulate(4)(index => Math.max(index, selector))
      case BytePermuteMode.ECR     => Array.tabulate(4)(index => Math.min(index, selector))
      case BytePermuteMode.RC16    => Array.tabulate(4)(index => ((selector & 1) * 2) + (index & 1))
    }
    val result = (0 until 4).foldLeft(0L) { (bits, index) =>
      val selection  = selectedBytes(index)
      val selected   = source(selection)
      val signExtend = mode == BytePermuteMode.Default && ((control >>> (4 * index)) & 8L) != 0L
      val byte       = if (signExtend) { if ((selected & 0x80) != 0) 0xff else 0 }
      else selected
      bits | (byte.toLong << (8 * index))
    }
    truncate(result, 32)
  }

  private def saturateInteger(value: Long, unsigned: Boolean, destination: NumericFormat): Long = {
    val bits   = destination.width
    val signed = destination match {
      case NumericFormat.Signed(_) => true
      case _                       => false
    }
    val maximum = if (signed) (1L << (bits.raw - 1)) - 1L else if (bits == 64) -1L else (1L << bits) - 1L
    if (!unsigned && value < 0) { if (signed) math.max(value, -maximum - 1L) else 0L }
    else if (java.lang.Long.compareUnsigned(value, maximum) > 0) maximum
    else value
  }

  private def convertInteger(
      value: Long,
      source: NumericFormat,
      destination: NumericFormat,
      saturate: Boolean
  ): Long = {
    val sourceBits = truncate(value, source.width)
    val integer    = source match {
      case NumericFormat.Signed(_) => signExtend(sourceBits, source.width)
      case _                       => sourceBits
    }
    val unsigned = source match {
      case NumericFormat.Unsigned(_) => true
      case _                         => false
    }
    truncate(if (saturate) saturateInteger(integer, unsigned, destination) else integer, destination.width)
  }

  private def compareInteger(left: Long, right: Long, comparison: Comparison, format: NumericFormat): Boolean = {
    val width      = format.width
    val leftValue  = truncate(left, width)
    val rightValue = truncate(right, width)
    val order      = format match {
      case NumericFormat.Signed(_) =>
        java.lang.Long.compare(signExtend(leftValue, width), signExtend(rightValue, width))
      case _ =>
        if (width == 64) java.lang.Long.compareUnsigned(leftValue, rightValue)
        else java.lang.Long.compare(leftValue, rightValue)
    }
    comparison match {
      case Comparison.Ordered                                                     => true
      case Comparison.Unordered                                                   => false
      case Comparison.Equal | Comparison.EqualUnordered                           => order == 0
      case Comparison.NotEqual | Comparison.NotEqualUnordered                     => order != 0
      case Comparison.LessThan | Comparison.LessThanUnordered                     => order < 0
      case Comparison.LessThanOrEqual | Comparison.LessThanOrEqualUnordered       => order <= 0
      case Comparison.GreaterThan | Comparison.GreaterThanUnordered               => order > 0
      case Comparison.GreaterThanOrEqual | Comparison.GreaterThanOrEqualUnordered => order >= 0
    }
  }

  private def loadBits(value: Long, width: ScalarBitWidth, signed: Boolean): Long = {
    val bits = truncate(value, width)
    if (signed) signExtend(bits, width) else bits
  }

  /** IADD3 with a carry mode: the sum and, when producing, the carry count. */
  private def addThree(
      operands: (Long, Long, Long),
      negations: (Boolean, Boolean, Boolean),
      width: ScalarBitWidth,
      produce: Boolean,
      carryValue: Int
  ): (Long, Int) = {
    val mask                                        = Bits.mask(width)
    def operand(value: Long, negate: Boolean): Long = {
      val bits = truncate(value, width)
      if (negate) mask - bits else bits
    }
    val (firstSum, firstCarry) =
      IntegerArithmetic.addUnsigned(
        operand(operands._1, negations._1),
        operand(operands._2, negations._2),
        false,
        width
      )
    val (secondSum, secondCarry) =
      IntegerArithmetic.addUnsigned(firstSum, operand(operands._3, negations._3), false, width)
    val count             = Seq(negations._1, negations._2, negations._3).count(identity)
    val correction        = carryValue + (if (produce) count else 0)
    val (sum, finalCarry) = IntegerArithmetic.addUnsigned(secondSum, correction.toLong, false, width)
    (sum, Seq(firstCarry, secondCarry, finalCarry).count(identity))
  }

  /** The source lane and validity of a shuffle by lane `lane` of a warp starting at `warpBase` of `laneCount`. */
  private def shuffle(
      mode: ShuffleMode,
      lane: Int,
      warpBase: Int,
      laneCount: Int,
      selector: Long,
      bounds: Long
  ): (Int, Boolean) = {
    def shuffleClamp(value: Long): Int = Math.min(value & 0xffffffffL, 31L).toInt
    val laneInWarp                     = lane - warpBase
    mode match {
      case ShuffleMode.Down =>
        val distance = (selector & 31L).toInt
        val valid    = laneInWarp + distance <= shuffleClamp(bounds) && lane + distance < laneCount
        (if (valid) lane + distance else lane) -> valid
      case ShuffleMode.Up =>
        val distance = (selector & 31L).toInt
        val valid    = laneInWarp >= distance && laneInWarp - distance >= shuffleClamp(bounds)
        (if (valid) lane - distance else lane) -> valid
      case ShuffleMode.Index =>
        val segmentMask  = ((bounds >>> 8) & 31L).toInt
        val segmentBase  = laneInWarp & segmentMask
        val targetInWarp = segmentBase | ((selector & 31L).toInt & ~segmentMask)
        val maximum      = segmentBase | ((bounds & 31L).toInt & ~segmentMask)
        val valid        = targetInWarp <= maximum && warpBase + targetInWarp < laneCount
        (if (valid) warpBase + targetInWarp else lane) -> valid
      case ShuffleMode.Butterfly =>
        val segmentMask  = ((bounds >>> 8) & 31L).toInt
        val targetInWarp = laneInWarp ^ (selector & 31L).toInt
        val maximum      = (laneInWarp & segmentMask) | ((bounds & 31L).toInt & ~segmentMask)
        val valid        = targetInWarp <= maximum
        (if (valid && warpBase + targetInWarp < laneCount) warpBase + targetInWarp else lane) -> valid
    }
  }

  private def reduce(values: Seq[Long], operation: IntegerOperation, width: ScalarBitWidth): Long = {
    val reduced = values.tail.foldLeft(truncate(values.head, width)) { (accumulator, rawValue) =>
      val value = truncate(rawValue, width)
      operation match {
        case IntegerOperation.Add            => truncate(accumulator + value, width)
        case IntegerOperation.BitwiseAnd     => accumulator & value
        case IntegerOperation.BitwiseOr      => accumulator | value
        case IntegerOperation.BitwiseXor     => accumulator ^ value
        case IntegerOperation.Minimum(true)  => Math.min(signExtend(accumulator, width), signExtend(value, width))
        case IntegerOperation.Maximum(true)  => Math.max(signExtend(accumulator, width), signExtend(value, width))
        case IntegerOperation.Minimum(false) =>
          if (java.lang.Long.compareUnsigned(accumulator, value) < 0) accumulator else value
        case _ => if (java.lang.Long.compareUnsigned(accumulator, value) > 0) accumulator else value
      }
    }
    truncate(reduced, width)
  }

  private def bits(width: ScalarBitWidth): Int in 1 ~ 64 = width.widen[Int in 1 ~ 64]

  test("bit finding matches the reference") {
    for {
      width       <- widths
      signed      <- Vector(false, true)
      shiftAmount <- Vector(false, true)
      value       <- samples
      sinks = Probe(value)
    } assertEquals(
      IntegerSemantics.bitFind(Execute, bits(width), signed, shiftAmount)(sinks, register, operand).head,
      bitFind(value, width, signed, shiftAmount),
      s"bfind $width $signed $shiftAmount $value"
    )
  }

  test("packed dot products match the reference") {
    for {
      leftSigned  <- Vector(false, true)
      rightSigned <- Vector(false, true)
      left        <- samples
      right       <- samples.take(40)
      addend      <- Vector(0L, -1L, 0x7fffffffL, 0x123456789L)
      sinks = Probe(left, right, addend)
    } assertEquals(
      IntegerSemantics.dotProduct4(Execute, leftSigned, rightSigned)(
        sinks = sinks,
        destination = register,
        first = operand,
        second = operand,
        third = operand
      ).head,
      dotProduct4(left, right, addend, leftSigned, rightSigned),
      s"dp4a $leftSigned $rightSigned $left $right $addend"
    )
  }

  test("bit field extraction and insertion match the reference") {
    for {
      width  <- widths
      signed <- Vector(false, true)
      source <- samples.take(40)
      start  <- small
      count  <- small.take(40)
    } {
      val sinks = Probe(source, start, count)
      assertEquals(
        IntegerSemantics.bitFieldExtract(Execute, bits(width), signed)(
          sinks = sinks,
          destination = register,
          first = operand,
          second = operand,
          third = operand
        ).head,
        extractBits(source, start, count, width, signed),
        s"bfe $width $signed $source $start $count"
      )
      if (!signed)
        assertEquals(
          IntegerSemantics.bitFieldInsert(Execute, bits(width))(
            sinks = Probe(source, ~source * 31L, start, count),
            destination = register,
            first = operand,
            second = operand,
            third = operand,
            fourth = operand
          ).head,
          insertBits(source, ~source * 31L, start, count, width),
          s"bfi $width $source $start $count"
        )
    }
  }

  test("bit masks match the reference") {
    for {
      mode  <- BitMaskMode.values.toVector
      start <- samples
      count <- samples
      sinks = Probe(start, count)
    } assertEquals(
      Execute.bitMask(mode)(sinks, register, operand, operand).head,
      bitMask(start, count, mode),
      s"bmsk $mode $start $count"
    )
  }

  test("byte permutes match the reference") {
    val random = scala.util.Random(23)
    for {
      mode    <- BytePermuteMode.values.toVector
      control <- (0L until 0x10000L by 7L) ++ Vector.fill(64)(random.nextLong())
    } {
      val left  = random.nextLong()
      val right = random.nextLong()
      val sinks = Probe(left, right, control)
      assertEquals(
        IntegerSemantics.bytePermute(Execute, mode)(
          sinks = sinks,
          destination = register,
          first = operand,
          second = operand,
          third = operand
        ).head,
        permuteBytes(left, right, control, mode),
        s"prmt $mode $left $right $control"
      )
    }
  }

  test("integer conversions match the reference") {
    val formats = Vector[Int in (8, 16, 32, 64)](8, 16, 32, 64).flatMap(bits =>
      Vector(NumericFormat.Bits(bits), NumericFormat.Signed(bits), NumericFormat.Unsigned(bits))
    )
    for {
      source      <- formats
      destination <- formats
      saturate    <- Vector(false, true)
      value       <- samples
      sinks = Probe(value)
    } assertEquals(
      IntegerSemantics.convert(
        Execute,
        source,
        destination,
        None,
        Option.when(saturate)(ConversionClamp.Saturate)
      )(sinks, register, operand).head,
      convertInteger(value, source, destination, saturate),
      s"cvt $source $destination $saturate $value"
    )
  }

  test("integer comparisons, flushing or not, match the reference") {
    val formats = Vector[Int in (8, 16, 32, 64)](8, 16, 32, 64).flatMap(bits =>
      Vector(NumericFormat.Bits(bits), NumericFormat.Signed(bits), NumericFormat.Unsigned(bits))
    )
    formats.foreach { format =>
      val signed = format match {
        case NumericFormat.Signed(_) => true
        case _                       => false
      }
      for {
        comparison <- Comparison.values.toVector
        flush      <- Vector(false, true)
        left       <- samples.take(40)
        right      <- samples.take(40)
        sinks = Probe(left, right)
      } assertEquals(
        IntegerSemantics.integerComparison(Execute, comparison, signed, bits(format.width), flush)(
          sinks,
          predicate,
          operand,
          operand
        ).head != 0L,
        compareInteger(
          flushFloat32(left, flush),
          flushFloat32(right, flush),
          comparison,
          format
        ),
        s"setp $comparison $format $flush $left $right"
      )
    }
  }

  test("IADD3 carry chains match the reference") {
    for {
      width     <- widths
      produce   <- Vector(false, true)
      negations <- Negations
      carry     <- if (produce) Vector(0) else Vector(0, 1, 2)
      a         <- samples.take(30)
      b         <- samples.take(30)
      c         <- Vector(0L, 1L, -1L, 0x80000000L, Long.MinValue)
    } {
      val (expected, count) = addThree((a, b, c), negations, width, produce, carry)
      val sinks             = Probe(a, b, c, carry.toLong)
      val outputs           = IntegerSemantics.addThreeCarry(
        Execute,
        bits(width),
        produce = produce,
        negateFirst = negations._1,
        negateSecond = negations._2,
        negateThird = negations._3
      )(
        sinks = sinks,
        destination = register,
        first = operand,
        second = operand,
        third = operand
      )
      assertEquals(outputs.head, expected, s"IADD3 $width $produce $negations $carry $a $b $c")
      if (produce) assertEquals(outputs(1), count.toLong, s"IADD3 carries $width $negations $a $b $c")
    }
  }

  test("shuffles select the reference lane and validity") {
    val random    = scala.util.Random(29)
    val selectors = Vector(0L, 1L, 3L, 16L, 31L, 33L, random.nextLong())
    val clamps    = Vector(0L, 7L, 31L, 0x1f00L, 0x181fL, 0x1c03L, 0xffffffffL, random.nextLong())
    for {
      mode      <- ShuffleMode.values.toVector
      laneCount <- Vector(32, 40, 64, 96)
      warpBase  <- Vector(0, 32, 64)
      if warpBase < laneCount
      lane     <- warpBase until math.min(warpBase + 32, laneCount)
      selector <- selectors
      bounds   <- clamps
    } {
      val (target, valid) = shuffle(mode, lane, warpBase, laneCount, selector, bounds)
      val sinks           = Probe((lane - warpBase).toLong, selector, bounds, (laneCount - warpBase).toLong)
      val outputs         = WarpSemantics.shuffle(Execute, mode, 32)(
        sinks = sinks,
        destination = register,
        validity = Some(predicate),
        operand = operand,
        selector = operand,
        bounds = operand
      )
      assertEquals(
        (warpBase + outputs.head.toInt, outputs(1) != 0L),
        (target, valid),
        s"shfl $mode $laneCount $lane $selector $bounds"
      )
    }
  }

  test("votes, elections and reductions match the reference over every member set") {
    val random = scala.util.Random(31)
    for {
      _ <- 0 until 400
    } {
      val members = random.nextLong() & (if (random.nextBoolean()) 0xffffffffL else 0xfL)
      val votes   = random.nextLong() & members
      val lanes   = (0 until 32).filter(lane => (members & (1L << lane)) != 0L)
      val probe   = Probe(votes, members)
      for (negated <- Vector(false, true)) {
        val cast = lanes.map(lane => lane -> ((((votes >>> lane) & 1L) != 0L) != negated))
        assertEquals(
          WarpSemantics.ballot(Execute, negated)(probe, register, predicate, operand).head,
          cast.foldLeft(0L) { case (bits, (lane, set)) => if (set) bits | (1L << lane) else bits }
        )
        val uniform = cast.map(_._2).distinct.size <= 1
        VoteOperation.values.foreach { operation =>
          val expected = operation match {
            case VoteOperation.All     => cast.nonEmpty && cast.forall(_._2)
            case VoteOperation.Any     => cast.exists(_._2)
            case VoteOperation.Uniform => uniform
          }
          assertEquals(
            WarpSemantics.vote(
              Execute,
              operation,
              negated
            )(probe, predicate, predicate, operand).head != 0L,
            expected,
            s"vote $operation $negated $members $votes"
          )
        }
      }
      if (lanes.nonEmpty) {
        val lane = lanes(random.nextInt(lanes.size)).toLong
        assertEquals(
          WarpSemantics.elect(Execute)(Probe(members, lane), Some(register), predicate, operand),
          Vector(lanes.head.toLong, if (lane == lanes.head.toLong) 1L else 0L),
          s"elect $members"
        )
        val values = Vector.fill(lanes.size)(samples(random.nextInt(samples.size)))
        for {
          width     <- widths
          operation <- Vector(
            IntegerOperation.Add,
            IntegerOperation.BitwiseAnd,
            IntegerOperation.BitwiseOr,
            IntegerOperation.BitwiseXor,
            IntegerOperation.Minimum(signed = true),
            IntegerOperation.Minimum(signed = false),
            IntegerOperation.Maximum(signed = true),
            IntegerOperation.Maximum(signed = false)
          )
        } assertEquals(
          WarpSemantics.reduce(
            Execute,
            operation,
            bits(width)
          )(Probe(values*), register, operand, operand)(Vector.empty),
          Vector(reduce(values, operation, width)),
          s"redux $operation $width $values"
        )
      }
    }
    val sinks = Probe(1L)
    assertEquals(
      WarpSemantics.reduce(
        Execute,
        IntegerOperation.MultiplyLow,
        32
      )(sinks, register, operand, operand)(Vector.empty),
      Vector.empty
    )
  }

  test("moves, accesses and cancellation responses match the reference") {
    val access = MemoryAccess.Global(false)
    for {
      value <- samples
      other <- samples.take(20)
    } {
      val pair = Probe(value, other)
      for (part <- Vector[Int in 1 ~ 64](16, 32)) {
        assertEquals(
          IntegerSemantics.split(Execute, part)(Probe(value), Some(register), Some(register), operand),
          Vector(truncate(value, part), truncate(value >>> part, part))
        )
        assertEquals(
          IntegerSemantics.combine(Execute, part)(pair, register, operand, operand).head,
          truncate(truncate(value, part) | (truncate(other, part) << part), (part * 2).unsafe[Int in 1 ~ 64])
        )
      }
      for {
        width  <- widths
        signed <- Vector(false, true)
        index  <- Vector(0L, 1L, 3L)
      } {
        val bytes = width.toScalarByteWidth.toLong
        val sinks = Probe(other, index, value)
        assertEquals(
          MemorySemantics.loadVector(
            Execute,
            width,
            signed
          )(sinks, Vec(register), operand, access),
          Vector(other + index * bytes, loadBits(value, width, signed))
        )
        assertEquals(
          MemorySemantics.load(Execute, width, signed)(sinks, register, operand, access),
          Vector(other, loadBits(value, width, signed))
        )
        assertEquals(
          MemorySemantics.store(Execute, width)(Probe(other, index, value), operand, operand, access),
          Vector(other, truncate(value, width))
        )
        for (comparator <- Vector(value, other, value ^ (1L << (width.raw - 1)), value ^ Long.MinValue))
          assertEquals(
            MemorySemantics.compareExchange(Execute, width)(
              sinks = Probe(value, comparator, other),
              destination = register,
              address = operand,
              comparator = operand,
              source = operand,
              space = MemorySpace.Global
            ).head,
            if (truncate(value, width) == truncate(comparator, width)) other else value
          )
      }
      assertEquals(IntegerSemantics.cancelled(Execute)(Probe(value), predicate, operand).head != 0L, (value & 1L) != 0L)
      assertEquals(
        Vector(Axis.X, Axis.Y, Axis.Z).map(axis =>
          IntegerSemantics.cancelledCoordinate(Execute, axis)(pair, register, operand, operand).head
        ),
        Vector(value & 0xffffffffL, value >>> 32, other >>> 32)
      )
    }
  }
}
