package net.kurobako.gemstone.isa.spirv

import StagedTesting.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.Edges.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.*

/** Staged core instruction semantics against the hand-written kernels they replaced. */
class StagedSemanticsSuite extends Suite {
  private val FieldRanges     = (0L to 70L) ++ Seq(0x7fffffffL, 0x80000000L, 0xffffffffL)
  private val ExtendedOpcodes =
    Seq(SPIRV.Opcode.IAddCarry, SPIRV.Opcode.ISubBorrow, SPIRV.Opcode.UMulExtended, SPIRV.Opcode.SMulExtended)
  private val ConversionEdges = Seq(1e18, -1e18, 1.8e19, 4e9, -4e9, 300.0, -300.0, 2.5, -2.5, 255.5)
  private val Strides         = Seq(1L, 3L, 4L, 16L, 24L)

  private def unary(opcode: Int, width: Int in 1 ~ 64): Long => Long =
    IntegerSemantics.unary(
      Execute,
      Closures,
      opcode,
      width
    )(OperandIds.none)(fail(s"opcode $opcode has no unary semantics")).asUnary

  private def extract(opcode: Int, width: Int in 1 ~ 64): Kernel3 =
    IntegerSemantics.bitFieldExtract(
      Execute,
      Closures,
      opcode,
      width
    )(OperandIds.none)(fail(s"opcode $opcode has no field semantics")).asTernary

  private def insert(width: Int in 1 ~ 64): Kernel4 =
    IntegerSemantics.bitFieldInsert(Execute, Closures, width)(OperandIds.none).asQuaternary

  private def extended(opcode: Int, width: Int in 1 ~ 64): (Long, Long) => (Long, Long) =
    IntegerSemantics.extended(
      Execute,
      Closures,
      opcode,
      width
    )(OperandIds.none)(fail(s"opcode $opcode has no extended semantics")).asBinaryPair

  private enum Field derives CanEqual {
    case UnsignedExtract, SignedExtract, Insert
  }

  // the bit-field kernel this replaces: whether the range is valid, and the Vulkan result
  private def fieldReference(operation: Field, width: Int in 1 ~ 64, base: Long, inserted: Long, off: Long, len: Long)
      : (Boolean, Long) = {
    val bits       = mask(width)
    val validRange = off >= 0 && len >= 0 && off <= width && len <= width - off
    val fieldMask  = if (!validRange) 0L else if (len == 64) -1L else (1L << len.toInt) - 1L
    val word       = base & bits
    val value      =
      if (!validRange) { if (operation == Field.Insert) word else 0L }
      else if (len == 0) { if (operation == Field.Insert) word else 0L }
      else if (operation == Field.Insert) (word & ~(fieldMask << off.toInt)) | ((inserted & fieldMask) << off.toInt)
      else {
        val extracted = (word >>> off.toInt) & fieldMask
        if (operation == Field.SignedExtract) Bits.signExtend(extracted, len.toInt.unsafe[Int in 1 ~ 64]) else extracted
      }
    (validRange, value & bits)
  }

  test("bit count, reversal and fields match the replaced kernels") {
    for (width <- integerWidths) {
      val values = integers(width)
      assertEquals(
        mismatches(values, unary(SPIRV.Opcode.BitCount, width), Bits.bitCount(_, width).toLong),
        Vector.empty
      )
      assertEquals(
        mismatches(
          values,
          unary(SPIRV.Opcode.BitReverse, width),
          value => (java.lang.Long.reverse(value & mask(width)) >>> (64 - width)) & mask(width)
        ),
        Vector.empty
      )
      val fields = for {
        base   <- values.take(12)
        offset <- FieldRanges
        count  <- FieldRanges
      } yield (base, offset, count)
      val unsigned = extract(SPIRV.Opcode.BitFieldUExtract, width)
      val signed   = extract(SPIRV.Opcode.BitFieldSExtract, width)
      val inserts  = insert(width)
      assertEquals(
        mismatches(
          fields,
          (_, offset, count) => IntegerSemantics.fieldInRange(Execute, offset, count, width) != 0L,
          (base, offset, count) => fieldReference(Field.Insert, width, base, 0L, offset, count)._1
        ),
        Vector.empty
      )
      assertEquals(
        mismatches(
          fields,
          unsigned.tupled,
          (base, offset, count) => fieldReference(Field.UnsignedExtract, width, base, 0L, offset, count)._2
        ),
        Vector.empty
      )
      assertEquals(
        mismatches(
          fields,
          signed.tupled,
          (base, offset, count) => fieldReference(Field.SignedExtract, width, base, 0L, offset, count)._2
        ),
        Vector.empty
      )
      assertEquals(
        mismatches(
          fields,
          (base, offset, count) =>
            inserts(base, values(Math.floorMod(offset, values.size.toLong).toInt), offset, count),
          (base, offset, count) =>
            fieldReference(
              Field.Insert,
              width,
              base,
              values(Math.floorMod(offset, values.size.toLong).toInt),
              offset,
              count
            )._2
        ),
        Vector.empty
      )
    }
  }

  test("extended arithmetic matches the replaced kernel") {
    for {
      width  <- integerWidths
      opcode <- ExtendedOpcodes
    } {
      val wordMask                                  = mask(width)
      def reference(a: Long, b: Long): (Long, Long) = {
        val x   = a & wordMask
        val y   = b & wordMask
        val low =
          (opcode match {
            case SPIRV.Opcode.IAddCarry  => x + y
            case SPIRV.Opcode.ISubBorrow => x - y
            case _                       => x * y
          }) & wordMask
        val high = opcode match {
          case SPIRV.Opcode.IAddCarry                  => if (java.lang.Long.compareUnsigned(low, x) < 0) 1L else 0L
          case SPIRV.Opcode.ISubBorrow                 => if (java.lang.Long.compareUnsigned(x, y) < 0) 1L else 0L
          case SPIRV.Opcode.SMulExtended if width < 64 => (signExtend(x, width) * signExtend(y, width)) >> width
          case SPIRV.Opcode.SMulExtended               => Math.multiplyHigh(x, y)
          case _ if width < 64                         => (x * y) >>> width
          case _                                       => Math.unsignedMultiplyHigh(x, y)
        }
        (low, high & wordMask)
      }
      assertEquals(mismatches(pairs(integers(width)), extended(opcode, width).tupled, reference.tupled), Vector.empty)
    }
  }

  private val comparisons: Seq[(Int, (Double, Double) => Boolean)] = Seq(
    SPIRV.Opcode.FOrdEqual              -> ((x, y) => x == y),
    SPIRV.Opcode.FOrdNotEqual           -> ((x, y) => !x.isNaN && !y.isNaN && x != y),
    SPIRV.Opcode.LessOrGreater          -> ((x, y) => !x.isNaN && !y.isNaN && x != y),
    SPIRV.Opcode.Ordered                -> ((x, y) => !x.isNaN && !y.isNaN),
    SPIRV.Opcode.FUnordEqual            -> ((x, y) => x == y || x.isNaN || y.isNaN),
    SPIRV.Opcode.FUnordNotEqual         -> ((x, y) => x != y),
    SPIRV.Opcode.FUnordLessThan         -> ((x, y) => x < y || x.isNaN || y.isNaN),
    SPIRV.Opcode.FUnordGreaterThan      -> ((x, y) => x > y || x.isNaN || y.isNaN),
    SPIRV.Opcode.FUnordLessThanEqual    -> ((x, y) => x <= y || x.isNaN || y.isNaN),
    SPIRV.Opcode.FUnordGreaterThanEqual -> ((x, y) => x >= y || x.isNaN || y.isNaN),
    SPIRV.Opcode.Unordered              -> ((x, y) => x.isNaN || y.isNaN),
    SPIRV.Opcode.FOrdLessThan           -> (_ < _),
    SPIRV.Opcode.FOrdLessThanEqual      -> (_ <= _),
    SPIRV.Opcode.FOrdGreaterThan        -> (_ > _),
    SPIRV.Opcode.FOrdGreaterThanEqual   -> (_ >= _)
  )

  private def compare(opcode: Int, width: Int in (16, 32, 64)): (Long, Long) => Long =
    FloatingSemantics.orderedComparison(Execute, Closures, opcode, width)(OperandIds.none)(
      FloatingSemantics.unorderedComparison(
        Execute,
        Closures,
        opcode,
        width
      )(OperandIds.none)(fail(s"opcode $opcode has no comparison"))
    ).asBinary

  private def classify(opcode: Int, width: Int in (16, 32, 64)): Long => Long =
    FloatingSemantics.classify(
      Execute,
      Closures,
      opcode,
      width
    )(OperandIds.none)(fail(s"opcode $opcode has no classification semantics")).asUnary

  test("floating-point comparisons and classification match the replaced predicates") {
    for (width <- floatWidths) {
      val values = floats(width)
      for ((opcode, predicate) <- comparisons)
        assertEquals(
          mismatches(
            pairs(values),
            compare(opcode, width).tupled,
            (a, b) => if (predicate(toDouble(width, a), toDouble(width, b))) 1L else 0L
          ),
          Vector.empty,
          s"opcode $opcode at $width bits"
        )
      val sign     = 1L << (width - 1)
      val fraction =
        (1L <<
          (width.raw match {
            case 16 => 10
            case 32 => 23
            case _  => 52
          })) - 1L
      val exponent                                = (sign - 1L) & ~fraction
      val predicates: Seq[(Int, Long => Boolean)] = Seq(
        SPIRV.Opcode.IsInf      -> (raw => (raw & exponent) == exponent && (raw & fraction) == 0L),
        SPIRV.Opcode.IsNan      -> (raw => (raw & exponent) == exponent && (raw & fraction) != 0L),
        SPIRV.Opcode.IsFinite   -> (raw => (raw & exponent) != exponent),
        SPIRV.Opcode.IsNormal   -> (raw => (raw & exponent) != exponent && (raw & exponent) != 0L),
        SPIRV.Opcode.SignBitSet -> (raw => (raw & sign) != 0L)
      )
      for ((opcode, predicate) <- predicates)
        assertEquals(
          mismatches(values, classify(opcode, width), raw => if (predicate(raw)) 1L else 0L),
          Vector.empty,
          s"opcode $opcode at $width bits"
        )
    }
  }

  test("dot products, scaling and products match the replaced IEEE arithmetic") {
    for (width <- floatWidths) {
      val values  = floats(width)
      val dot     = FloatingSemantics.dot(Execute, Closures, width)(OperandIds.none).asBinaryFold
      val step    = FloatingSemantics.product(Execute, Closures, width)(OperandIds.none).asTernary
      val product = FloatingSemantics.binary(
        Execute,
        Closures,
        SPIRV.Opcode.FMul,
        width
      )(OperandIds.none)(fail("no product")).asBinary
      val vectors = values.sliding(4, 3).filter(_.size == 4).toSeq
      assertEquals(
        mismatches(
          vectors.zip(vectors.reverse),
          dot.tupled,
          (a, b) =>
            sumBits(a.size, IEEE754.fromDouble(0.0, width))((sum, lane) =>
              IEEE754.add(
                sum,
                IEEE754.multiply(a(lane), b(lane), width, IEEE754.NaNOperand.Left),
                width,
                IEEE754.NaNOperand.Left
              )
            ),
          same(width)
        ),
        Vector.empty
      )
      assertEquals(
        mismatches(
          triples(values),
          step.tupled,
          (sum, a, b) =>
            IEEE754.add(sum, IEEE754.multiply(a, b, width, IEEE754.NaNOperand.Left), width, IEEE754.NaNOperand.Left),
          same(width)
        ),
        Vector.empty
      )
      assertEquals(
        mismatches(
          pairs(values),
          product.tupled,
          (a, b) => IEEE754.fromDouble(IEEE754.toDouble(a, width) * IEEE754.toDouble(b, width), width),
          same(width)
        ),
        Vector.empty
      )
    }
  }

  test("selection and Boolean reductions match the replaced kernels") {
    val select =
      ComparisonSemantics.ternary(Execute, Closures, SPIRV.Opcode.Select)(OperandIds.none)(fail("no select")).asTernary
    val values = integers(32)
    assertEquals(mismatches(triples(values), select.tupled, (c, a, b) => if (c != 0L) a else b), Vector.empty)
    val any = ComparisonSemantics.reduction(Execute, Closures, SPIRV.Opcode.Any)(OperandIds.none)(fail("no any")).asFold
    val all = ComparisonSemantics.reduction(Execute, Closures, SPIRV.Opcode.All)(OperandIds.none)(fail("no all")).asFold
    val vectors = Seq(Seq(0L), Seq(1L), Seq(0L, 0L), Seq(0L, 1L), Seq(1L, 1L, 1L), Seq(1L, 0L, 1L, 1L))
    assertEquals(mismatches(vectors, lanes => any(lanes), lanes => if (lanes.exists(_ != 0L)) 1L else 0L), Vector.empty)
    assertEquals(mismatches(vectors, lanes => all(lanes), lanes => if (!lanes.contains(0L)) 1L else 0L), Vector.empty)
  }

  // SPIR-V's FPRoundingMode has no ties-away mode
  private val modes = IEEE754.Rounding.values.toSeq.filterNot(_ == IEEE754.Rounding.NearestAway)

  // an integral value has an unsigned representation when it lies in [0, 2^width), negative zero included
  private def representable(value: Double, width: Int in 1 ~ 64): Boolean =
    value > -1.0 && value < Math.scalb(1.0, width)

  // the unsigned conversion this replaces, with its saturation and Vulkan flags
  private def unsignedReference(value: Double, width: Int in 1 ~ 64, saturate: Boolean): Long =
    if (saturate && (value.isNaN || value <= 0.0)) 0L
    else if (saturate && value >= Math.scalb(1.0, width)) mask(width)
    else if (representable(value, width)) BigDecimal.exact(value).toBigInt.toLong & mask(width)
    else if (value.isNaN || value <= 0.0) 0L
    else mask(width)

  // binary32 inputs and their OpQuantizeToF16 results: rounding to nearest even, overflow to infinity, and magnitudes
  // below the least normal binary16 flushed to a zero of their sign
  private val quantized = Seq(
    0x3f800000L -> 0x3f800000L,
    0x3f801000L -> 0x3f800000L,
    0x3f803000L -> 0x3f804000L,
    0xc0200000L -> 0xc0200000L,
    0x477fe000L -> 0x477fe000L,
    0x477ff000L -> 0x7f800000L,
    0xc77ff000L -> 0xff800000L,
    0x38800000L -> 0x38800000L,
    0x38000000L -> 0x00000000L,
    0xb8000000L -> 0x80000000L,
    0x7f800000L -> 0x7f800000L,
    0x7fc00000L -> 0x7fc00000L
  )

  test("integer and integer-to-floating conversions match the replaced kernels") {
    for {
      width  <- integerWidths
      result <- integerWidths
    } {
      val values = integers(width)
      val sconv  =
        ConversionSemantics.integer(
          Execute,
          Closures,
          SPIRV.Opcode.SConvert,
          width,
          result
        )(OperandIds.none)(fail("no conversion")).asUnary
      val uconv =
        ConversionSemantics.integer(
          Execute,
          Closures,
          SPIRV.Opcode.UConvert,
          width,
          result
        )(OperandIds.none)(fail("no conversion")).asUnary
      assertEquals(mismatches(values, sconv, signExtend(_, width) & mask(result)), Vector.empty)
      assertEquals(mismatches(values, uconv, _ & mask(result)), Vector.empty)
    }
    for {
      width  <- integerWidths
      result <- floatWidths
      mode   <- modes
    } {
      val values          = integers(width)
      def to(opcode: Int) =
        ConversionSemantics.toFloating(
          Execute,
          Closures,
          opcode,
          width,
          result,
          mode
        )(OperandIds.none)(fail("no conversion")).asUnary
      assertEquals(
        mismatches(
          values,
          to(SPIRV.Opcode.ConvertSToF),
          raw => IEEE754.integerToBinary(signExtend(raw, width), unsigned = false, result, mode)
        ),
        Vector.empty
      )
      assertEquals(
        mismatches(
          values,
          to(SPIRV.Opcode.ConvertUToF),
          raw => IEEE754.integerToBinary(raw & mask(width), true, result, mode)
        ),
        Vector.empty
      )
    }
  }

  test("floating conversions match the replaced kernels") {
    for {
      width  <- floatWidths
      result <- integerWidths
      mode   <- modes
    } {
      val values = floats(width) ++ ConversionEdges.map(IEEE754.fromDouble(_, width))
      for (saturate <- booleans)
        assertEquals(
          mismatches(
            values,
            ConversionSemantics.toSigned(Execute, Closures, width, result, mode, saturate)(OperandIds.none).asUnary,
            { raw =>
              val value = IEEE754.toDouble(raw, width)
              if (saturate) IEEE754.toIntegerSaturated(value, result, signed = true, mode)
              else IEEE754.integral(value, mode).toLong & mask(result)
            }
          ),
          Vector.empty
        )
      val unsigned = ConversionSemantics.toUnsigned(Execute, Closures, width, result, mode)(OperandIds.none).asUnary
      for (saturate <- booleans)
        assertEquals(
          mismatches(
            values,
            unsigned,
            raw => unsignedReference(IEEE754.integral(IEEE754.toDouble(raw, width), mode), result, saturate)
          ),
          Vector.empty
        )
      assertEquals(
        mismatches(
          values,
          raw =>
            ConversionSemantics.toUnsigned(Execute, Closures, width, result, mode)(OperandIds.none).asPair(raw)._2 !=
              0L,
          raw => representable(IEEE754.integral(IEEE754.toDouble(raw, width), mode), result)
        ),
        Vector.empty
      )
    }
    for {
      width  <- floatWidths
      result <- floatWidths
      mode   <- modes
    }
      assertEquals(
        mismatches(
          floats(width),
          ConversionSemantics.toFloatingWidth(Execute, Closures, width, result, mode)(OperandIds.none).asUnary,
          raw => IEEE754.fromDouble(IEEE754.toDouble(raw, width), result, mode),
          same(result)
        ),
        Vector.empty
      )
    assertEquals(
      mismatches(
        quantized.map(_._1),
        ConversionSemantics.quantize(Execute, Closures)(OperandIds.none).asUnary,
        quantized.toMap,
        same(32)
      ),
      Vector.empty
    )
  }

  // the integer dot product this replaces, summing exactly
  private def dotReference(
      a: Seq[Long],
      b: Seq[Long],
      accumulator: Option[Long],
      component: Int,
      result: Int,
      first: Boolean,
      second: Boolean
  ): Long = {
    def extend(value: Long, width: Int, signed: Boolean): Long =
      Bits.extend(value, width.unsafe[Int in 1 ~ 64], signed)
    val x     = a.map(extend(_, component, first))
    val y     = b.map(extend(_, component, second))
    val value = accumulator match {
      case None        => x.indices.foldLeft(0L)((sum, lane) => sum + x(lane) * y(lane))
      case Some(start) =>
        val signed = first
        // the replaced kernel read a 64-bit unsigned accumulator as signed, saturating values from 2^63 to zero
        val initial = BigInt(extend(start, result, signed)) + (if (!signed && start < 0L) BigInt(1) << 64 else 0)
        val exact   = x.indices.foldLeft(initial)((sum, lane) =>
          sum + BigInt(x(lane)) * BigInt(y(lane))
        )
        val (low, high) =
          if (signed) (-(BigInt(1) << (result - 1)), (BigInt(1) << (result - 1)) - 1)
          else (BigInt(0), (BigInt(1) << result) - 1)
        exact.max(low).min(high).toLong
    }
    extend(value, result, signed = false)
  }

  test("integer dot products match the replaced exact sums") {
    val forms = Seq(
      (SPIRV.Opcode.SDot, SPIRV.Opcode.SDotAccSat, true, true),
      (SPIRV.Opcode.UDot, SPIRV.Opcode.UDotAccSat, false, false),
      (SPIRV.Opcode.SUDot, SPIRV.Opcode.SUDotAccSat, true, false)
    )
    for {
      (plain, accumulating, first, second) <- forms
      result                               <- integerWidths
    } {
      val words                        = integers(32)
      def lanes(word: Long): Seq[Long] = Seq.tabulate(4)(lane => (word >>> (lane * 8)) & 0xffL)
      val packed                       =
        IntegerDotSemantics.packed(Execute, Closures, plain, result)(OperandIds.none)(fail("no dot product")).asBinary
      val packedAccumulate =
        IntegerDotSemantics.packedAccumulate(
          Execute,
          Closures,
          accumulating,
          result
        )(OperandIds.none)(fail("no dot product")).asTernary
      assertEquals(
        mismatches(
          pairs(words),
          packed.tupled,
          (a, b) => dotReference(lanes(a), lanes(b), None, 8, result, first, second)
        ),
        Vector.empty
      )
      assertEquals(
        mismatches(
          triples(words) ++ triples(integers(result)),
          packedAccumulate.tupled,
          (a, b, c) => dotReference(lanes(a), lanes(b), Some(c), 8, result, first, second)
        ),
        Vector.empty
      )
      for (component <- integerWidths) {
        val vectors = integers(component).sliding(4, 3).filter(_.size == 4).toSeq
        val inputs  = vectors.zip(vectors.reverse)
        val vector  = IntegerDotSemantics.vector(
          Execute,
          Closures,
          plain,
          component,
          result
        )(OperandIds.none)(fail("no product")).asBinaryFold
        assertEquals(
          mismatches(
            inputs,
            vector.tupled,
            (a, b) => dotReference(a, b, None, component, result, first, second)
          ),
          Vector.empty
        )
        if (IntegerDotSemantics.exact(component, 4)) {
          val accumulate =
            IntegerDotSemantics.vectorAccumulate(Execute, Closures, accumulating, component, result)(
              OperandIds.none
            )(fail("no product")).asAccumulatedFold
          val withAddend = for {
            (a, b) <- inputs
            c      <- integers(result).take(12)
          } yield (a, b, c)
          assertEquals(
            mismatches(
              withAddend,
              accumulate.tupled,
              (a, b, c) => dotReference(a, b, Some(c), component, result, first, second)
            ),
            Vector.empty
          )
        }
      }
    }
    assert(IntegerDotSemantics.exact(16, 4) && !IntegerDotSemantics.exact(32, 2))
  }

  test("atomic read-modify-writes store what the shared atomic operations store") {
    val integer = Seq(
      SPIRV.Opcode.AtomicExchange -> AtomicOperation.Exchange,
      SPIRV.Opcode.AtomicIAdd     -> AtomicOperation.Add,
      SPIRV.Opcode.AtomicISub     -> AtomicOperation.Subtract,
      SPIRV.Opcode.AtomicSMin     -> AtomicOperation.MinimumSigned,
      SPIRV.Opcode.AtomicUMin     -> AtomicOperation.MinimumUnsigned,
      SPIRV.Opcode.AtomicSMax     -> AtomicOperation.MaximumSigned,
      SPIRV.Opcode.AtomicUMax     -> AtomicOperation.MaximumUnsigned,
      SPIRV.Opcode.AtomicAnd      -> AtomicOperation.And,
      SPIRV.Opcode.AtomicOr       -> AtomicOperation.Or,
      SPIRV.Opcode.AtomicXor      -> AtomicOperation.Xor
    )
    for (width <- Seq[Int in (32, 64)](32, 64)) {
      val bits: ScalarBitWidth = width.widen
      val values               = integers(width.widen)
      for ((opcode, operation) <- integer)
        assertEquals(
          mismatches(
            pairs(values),
            AtomicSemantics.binary(
              Execute,
              Closures,
              opcode,
              width.widen
            )(OperandIds.none)(fail("no atomic")).asBinary.tupled,
            operation(_, _, bits)
          ),
          Vector.empty,
          s"opcode $opcode"
        )
      for (
        (opcode, operation) <- Seq(
          SPIRV.Opcode.AtomicIIncrement -> AtomicOperation.Increment,
          SPIRV.Opcode.AtomicIDecrement -> AtomicOperation.Decrement
        )
      )
        assertEquals(
          mismatches(
            values,
            AtomicSemantics.unary(Execute, Closures, opcode, width.widen)(OperandIds.none)(fail("no atomic")).asUnary,
            operation(_, 1L, bits)
          ),
          Vector.empty
        )
      assertEquals(
        mismatches(
          triples(values),
          AtomicSemantics.compareExchange(Execute, Closures, width.widen)(OperandIds.none).asTernary.tupled,
          (old, value, comparator) =>
            if ((old & mask(width.widen)) == (comparator & mask(width.widen))) value & mask(width.widen) else old
        ),
        Vector.empty
      )
    }
    for (width <- floatWidths) {
      val bits: ScalarBitWidth = width.widen
      for (
        (opcode, operation) <- Seq(
          SPIRV.Opcode.AtomicFAddEXT -> AtomicOperation.FloatAdd,
          SPIRV.Opcode.AtomicFMinEXT -> AtomicOperation.FloatMinimum,
          SPIRV.Opcode.AtomicFMaxEXT -> AtomicOperation.FloatMaximum
        )
      )
        assertEquals(
          mismatches(
            pairs(floats(width)),
            AtomicSemantics.floating(
              Execute,
              Closures,
              opcode,
              width
            )(OperandIds.none)(fail("no atomic")).asBinary.tupled,
            operation(_, _, bits),
            same(width)
          ),
          Vector.empty
        )
    }
  }

  test("pointer differences divide the exact address difference") {
    val addresses = Seq(0L, 8L, 16L, 4096L, 4100L, -8L, Long.MaxValue, Long.MinValue, 0x7ffffffffff0L, -0x10L)
    for {
      width  <- integerWidths
      stride <- Strides
    } {
      val difference = PointerSemantics.difference(Execute, Closures, width)(OperandIds.none).asTernary
      assertEquals(
        mismatches(
          pairs(addresses),
          (a, b) => difference(a, b, stride),
          (a, b) => ((BigInt(a) - BigInt(b)) / stride).toLong & mask(width)
        ),
        Vector.empty
      )
    }
  }
}
