package net.kurobako.gemstone.isa.spirv

import fixtures.*
import munit.Location
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class IntegerOpSuite extends munit.FunSuite {
  private val Widths   = Seq(8, 16, 32, 64)
  private val Signs    = Seq(false, true)
  private val TypeName = """%([usf])(8|16|32|64)(?:x([0-9]+))?\b""".r

  private def typ(signed: Boolean, width: Int, lanes: Int = 1): String =
    s"%${if (signed) "s" else "u"}$width${if (lanes == 1) "" else s"x$lanes"}"

  private def environment(vulkan: Boolean): String = if (vulkan) "Vulkan" else "OpenCL"

  for ((width, lanes, signed) <- pairwise(Widths, Seq(1, 3, 4, 16), Signs))
    test(s"OpenCL integer abs preserves the unsigned magnitude at width $width, lanes $lanes, signed $signed") {
      val modulus  = BigInt(1) << width
      val boundary = modulus >> 1
      val samples  = Seq(BigInt(0), BigInt(1), boundary - 1, boundary, boundary + 1, modulus - 1)
      val t        = typ(signed = false, width, lanes)
      for (start <- samples.indices) {
        val input    = Seq.tabulate(lanes)(index => samples((start + index) % samples.size))
        val expected = input.map(value => if (signed && value >= boundary) modulus - value else value)
        elementwise(
          t,
          s"%result = OpExtInst $t %opencl ${if (signed) "s" else "u"}_abs %a0",
          Seq(t -> input.map(_.toLong)),
          Some(expected.map(_.toLong))
        )
      }
    }

  for {
    width  <- Widths
    signed <- Signs
  } test(s"OpenCL difference, saturation and clamp handle boundary values at width $width, signed $signed") {
    val modulus = BigInt(1) << width
    val minimum = if (signed) -(modulus >> 1) else BigInt(0)
    val maximum = if (signed) (modulus >> 1) - 1 else modulus - 1
    val samples = Seq(minimum, minimum + 1, BigInt(0), BigInt(1), maximum - 1, maximum)
    val prefix  = if (signed) "s" else "u"
    val t       = typ(signed = false, width)
    for {
      left  <- samples
      right <- samples
    } {
      val inputs = Seq(t -> Seq(left.toLong), t -> Seq(right.toLong))
      elementwise(
        t,
        s"%result = OpExtInst $t %opencl ${prefix}_abs_diff %a0 %a1",
        inputs,
        Some(Seq((left - right).abs.toLong))
      )
      elementwise(
        t,
        s"%result = OpExtInst $t %opencl ${prefix}_add_sat %a0 %a1",
        inputs,
        Some(Seq((left + right).max(minimum).min(maximum).toLong))
      )
    }
    for (lanes <- Seq(1, 3, 16)) {
      val input = Seq.tabulate(lanes)(index => samples(index % samples.size))
      val lower = minimum + 1
      val upper = maximum - 1
      val v     = typ(signed = false, width, lanes)
      elementwise(
        v,
        s"%result = OpExtInst $v %opencl ${prefix}_clamp %a0 %a1 %a2",
        Seq(v -> input.map(_.toLong), v -> Seq.fill(lanes)(lower.toLong), v -> Seq.fill(lanes)(upper.toLong)),
        Some(input.map(_.max(lower).min(upper).toLong))
      )
    }
  }

  private val Overflowing: Seq[(String, Int, Boolean)] =
    (pairwise(Seq("hadd", "rhadd", "sub_sat"), Widths, Signs) ++
      Seq(8, 32, 64).flatMap(width => Seq(("hadd", width, true), ("sub_sat", width, true), ("rhadd", width, false))))
      .distinct ++
      (for {
        name   <- Seq("mul_hi", "mad_hi", "mad_sat")
        width  <- Widths
        signed <- Signs
      } yield (name, width, signed)) ++
      Seq("mul24", "mad24").flatMap(name => Signs.map((name, 32, _))) ++
      Seq(8, 16, 32).flatMap(width => Signs.map(("upsample", width, _)))

  for ((name, width, signed) <- Overflowing)
    test(s"OpenCL $name handles overflow boundaries at width $width, signed $signed") {
      val modulus      = BigInt(1) << width
      val minimum      = if (signed) -(modulus >> 1) else BigInt(0)
      val maximum      = if (signed) (modulus >> 1) - 1 else modulus - 1
      val inputModulus = if (name.endsWith("24")) BigInt(1) << 24 else modulus
      val inputMinimum = if (signed) -(inputModulus >> 1) else BigInt(0)
      val inputMaximum = if (signed) (inputModulus >> 1) - 1 else inputModulus - 1
      val samples      = Seq(inputMinimum, inputMinimum + 1, BigInt(0), BigInt(1), inputMaximum - 1, inputMaximum)
      for {
        lanes <- Seq(1, 3, 4)
        start <- samples.indices
      } {
        val left     = Seq.tabulate(lanes)(index => samples((start + index) % samples.size))
        val right    = Seq.tabulate(lanes)(index => samples((start * 3 + index + 1) % samples.size))
        val third    = Seq.tabulate(lanes)(index => Seq(minimum, BigInt(0), BigInt(1), maximum)((start + index) % 4))
        val expected = left.indices.map { index =>
          val x = left(index); val y = right(index); val z = third(index)
          (name match {
            case "hadd"     => (x + y) >> 1
            case "rhadd"    => (x + y + 1) >> 1
            case "sub_sat"  => (x - y).max(minimum).min(maximum)
            case "mul_hi"   => (x * y) >> width
            case "mad_hi"   => ((x * y) >> width) + z
            case "mad_sat"  => (x * y + z).max(minimum).min(maximum)
            case "mul24"    => x * y
            case "mad24"    => x * y + z
            case "upsample" => (x << width) | (y & (modulus - 1))
          }).toLong
        }
        val t      = typ(signed = false, width, lanes)
        val result = if (name == "upsample") typ(false, width * 2, lanes) else t
        val inputs = Seq(left, right) ++ (if (name.startsWith("mad")) Seq(third) else Seq.empty)
        val names  = inputs.indices.map(i => s"%a$i").mkString(" ")
        elementwise(
          result,
          s"%result = OpExtInst $result %opencl ${if (signed) "s" else "u"}_$name $names",
          inputs.map(values => t -> values.map(_.toLong)),
          Some(expected)
        )
      }
    }

  for (width <- Widths) test(s"OpenCL rotate masks the shift count at width $width") {
    val bits = (BigInt(1) << (width - 1)) | 3
    val t    = typ(signed = false, width, 4)
    for (shift <- Seq(0, 1, width - 1, width, width + 1, -1)) {
      val count    = shift & (width - 1)
      val expected = ((bits << count) | (bits >> (width - count))).toLong
      elementwise(
        t,
        s"%result = OpExtInst $t %opencl rotate %a0 %a1",
        Seq(t -> Seq.fill(4)(bits.toLong), t -> Seq.fill(4)(shift.toLong)),
        Some(Seq.fill(4)(expected))
      )
    }
  }

  private val Divisions = Seq("SDiv", "SRem", "SMod", "UDiv", "UMod")

  // OpenCL declares every integer type with signedness 0, so the signed opcodes alone select signed division
  for {
    (vulkan, signedTypes, rows) <- Seq(
      (false, true, pairwise(Divisions, Widths, Seq(1, 3, 4, 16))),
      (false, false, pairwise(Divisions.filter(_.startsWith("S")), Widths, Seq(1, 3, 4, 16))),
      (true, true, pairwise(Divisions, Seq(32, 64), Seq(1, 4)))
    )
    (opcode, width, lanes) <- rows
  } test(
    s"Op$opcode continues past exceptional lanes in ${environment(vulkan)}" +
      s"${if (signedTypes) "" else " on signedness-0 types"} at width $width, lanes $lanes"
  ) {
    val modulus = BigInt(1) << width
    val signed  = opcode.startsWith("S")
    val samples = Seq(
      (BigInt(7), BigInt(0)),
      (BigInt(9), BigInt(2)),
      (BigInt(-9), BigInt(2)),
      (BigInt(9), BigInt(-2)),
      (-(modulus >> 1), BigInt(-1))
    )
    def value(raw: BigInt): BigInt = if (signed) raw else raw & (modulus - 1)
    val t                          = typ(signed && signedTypes, width, lanes)
    for (start <- samples.indices) {
      val input    = Seq.tabulate(lanes)(index => samples((start + index) % samples.size))
      val expected = input.map((left, right) => divide(opcode, value(left), value(right)))
      elementwise(
        t,
        s"%result = Op$opcode $t %a0 %a1",
        Seq(t -> input.map(_._1.toLong), t -> input.map(_._2.toLong)),
        Some(expected),
        vulkan = vulkan
      )
    }
  }

  for (width <- Seq(32, 64))
    test(s"Vulkan division of constant operands continues past exceptional lanes at width $width") {
      val modulus = BigInt(1) << width
      val samples =
        Seq((BigInt(7), BigInt(0)), (BigInt(9), BigInt(-2)), (-(modulus >> 1), BigInt(-1)), (BigInt(-9), BigInt(2)))
      for (opcode <- Divisions) {
        val signed                                               = opcode.startsWith("S")
        def value(raw: BigInt): BigInt                           = if (signed) raw else raw & (modulus - 1)
        val (t, scalar)                                          = (typ(signed, width, 4), typ(signed, width))
        def constants(name: String, values: Seq[BigInt]): String =
          (values.zipWithIndex.map((raw, lane) => s"%$name$lane = OpConstant $scalar ${value(raw)}") :+
            s"%$name = OpConstantComposite $t ${values.indices.map(lane => s"%$name$lane").mkString(" ")}")
            .mkString("\n")
        elementwise(
          t,
          s"%result = Op$opcode $t %x %y",
          Seq.empty,
          Some(samples.map((left, right) => divide(opcode, value(left), value(right)))),
          declarations = constants("x", samples.map(_._1)) + "\n" + constants("y", samples.map(_._2)),
          vulkan = true
        )
      }
    }

  private def divide(opcode: String, x: BigInt, y: BigInt): Long =
    if (y == 0) 0L
    else (opcode match {
      case "SDiv" | "UDiv" => x / y
      case "SRem" | "UMod" => x % y
      case _               => if ((x % y).signum * y.signum < 0) x % y + y else x % y
    }).toLong

  test("a negative 16-bit Vulkan constant holds the same bits as a converted run-time value") {
    elementwise(
      "%u32x4",
      """%narrow = OpUConvert %u16 %a0
        |%loaded = OpBitcast %s16 %narrow
        |%equal = OpIEqual %bool %minus_one %loaded
        |%below = OpULessThan %bool %loaded %minus_one
        |%equal_flag = OpSelect %u32 %equal %one %zero
        |%below_flag = OpSelect %u32 %below %one %zero
        |%widened = OpUConvert %u32 %minus_one
        |%extended = OpSConvert %u32 %minus_one
        |%result = OpCompositeConstruct %u32x4 %equal_flag %below_flag %widened %extended""".stripMargin,
      Seq("%u32" -> Seq(0xffffL)),
      Some(Seq(1L, 0L, 0xffffL, 0xffffffffL)),
      declarations = """%minus_one = OpConstant %s16 -1
                       |%one = OpConstant %u32 1
                       |%zero = OpConstant %u32 0""".stripMargin,
      vulkan = true
    )
  }

  test("shifts and negation of a negative 32-bit operand") {
    for {
      (instruction, expected) <- Seq(
        "OpShiftRightLogical %u32 %a0 %a1" -> 0x1fffffffL,
        "OpShiftLeftLogical %u32 %a0 %a1"  -> 0xffffffc8L,
        "OpSNegate %u32 %a0"               -> 7L
      )
    } elementwise(
      "%u32",
      s"%result = $instruction",
      Seq("%u32" -> Seq(0xfffffff9L), "%u32" -> Seq(3L)),
      Some(Seq(expected))
    )
  }

  private val bitwise =
    pairwise(Seq("BitwiseAnd", "BitwiseOr", "BitwiseXor", "Not"), Widths, Seq(1, 3, 4), Signs, Signs, Signs)

  // every signedness triple per opcode
  private val bitwiseSignedness = for {
    opcode       <- Seq("BitwiseAnd", "BitwiseOr", "BitwiseXor", "Not")
    resultSigned <- Signs
    leftSigned   <- Signs
    rightSigned  <- Signs
    if !bitwise.exists(row => (row._1, row._4, row._5, row._6) == (opcode, resultSigned, leftSigned, rightSigned))
  } yield (opcode, 32, 4, resultSigned, leftSigned, rightSigned)

  for ((opcode, width, lanes, resultSigned, leftSigned, rightSigned) <- bitwise ++ bitwiseSignedness)
    test(s"Op$opcode accepts signedness $resultSigned/$leftSigned/$rightSigned at width $width, lanes $lanes") {
      val left     = Seq(-1L, 0L, Long.MinValue | 0x80L, 0x5555555555555555L).take(lanes)
      val right    = Seq(0xaaaaaaaaaaaaaaaaL, -1L, 0x7fL, 0L).take(lanes)
      val expected = left.zip(right).map { (a, b) =>
        opcode match {
          case "BitwiseAnd" => a & b
          case "BitwiseOr"  => a | b
          case "BitwiseXor" => a ^ b
          case _            => ~a
        }
      }
      val result = typ(resultSigned, width, lanes)
      elementwise(
        result,
        s"%result = Op$opcode $result %a0${if (opcode == "Not") "" else " %a1"}",
        Seq(typ(leftSigned, width, lanes) -> left, typ(rightSigned, width, lanes) -> right),
        Some(expected)
      )
    }

  private val Extended = Seq(
    "IAddCarry"    -> false,
    "ISubBorrow"   -> false,
    "UMulExtended" -> false,
    "SMulExtended" -> false,
    "SMulExtended" -> true
  )

  for (((opcode, signed), width, lanes) <- pairwise(Extended, Widths, Seq(1, 4)))
    test(s"Op$opcode returns both result words at width $width, lanes $lanes, signed $signed") {
      val wordMask = (BigInt(1) << width) - 1
      val pairs    = Seq(
        (-1L, -1L),
        (Long.MaxValue, Long.MaxValue),
        (0L, 1L),
        (Long.MinValue, 2L),
        (Long.MaxValue, Long.MinValue),
        (Long.MinValue, Long.MinValue),
        (1L, -1L),
        (0x123456789abcdefL, 0xfedcba9876543210L),
        (1L << (width - 1), 1L << (width - 1)),
        ((1L << (width - 1)) - 1, 2L)
      )
      def value(n: Long): BigInt = {
        val bits = BigInt(n) & wordMask
        if (opcode == "SMulExtended" && bits.testBit(width - 1)) bits - (BigInt(1) << width) else bits
      }
      val t = typ(signed, width, lanes)
      for (index <- pairs.indices) {
        val inputs  = Seq.tabulate(lanes)(lane => pairs((index + lane) % pairs.size))
        val results = inputs.map { (a, b) =>
          val x    = value(a); val y = value(b)
          val full = opcode match {
            case "IAddCarry"  => x + y
            case "ISubBorrow" => x - y
            case _            => x * y
          }
          val high = if (opcode == "ISubBorrow") { if (x < y) BigInt(1) else BigInt(0) }
          else full >> width
          ((full & wordMask).toLong, (high & wordMask).toLong)
        }
        val both = typ(signed, width, lanes * 2)
        elementwise(
          both,
          s"""%pair = Op$opcode %pair_t %a0 %a1
             |%low = OpCompositeExtract $t %pair 0
             |%high = OpCompositeExtract $t %pair 1
             |%result = OpCompositeConstruct $both %low %high""".stripMargin,
          Seq(t -> inputs.map(_._1), t -> inputs.map(_._2)),
          Some(results.map(_._1) ++ results.map(_._2)),
          declarations = s"%pair_t = OpTypeStruct $t $t"
        )
      }
    }

  for ((width, resultWidth, lanes, signed) <- pairwise(Widths, Seq(8, 32, 64), Seq(1, 3, 4), Signs))
    test(s"OpBitCount counts $width-bit input into a $resultWidth-bit result, lanes $lanes, input signed $signed") {
      val result = typ(!signed, resultWidth, lanes)
      elementwise(
        result,
        s"%result = OpBitCount $result %a0",
        Seq(typ(signed, width, lanes) -> Seq(-1L, 0L, 0x5555555555555555L, 1L << (width - 1)).take(lanes)),
        Some(Seq(width.toLong, 0L, width / 2L, 1L).take(lanes))
      )
    }

  for {
    opcode <- Seq("FRem", "FMod")
    width  <- Seq(16, 32, 64)
  } test(s"Op$opcode takes the sign of the ${if (opcode == "FRem") "dividend" else "divisor"} at width $width") {
    def bits(value: Double): Long = floatingBits(value, width)
    val t                         = s"%f${width}x4"
    elementwise(
      t,
      s"%result = Op$opcode $t %a0 %a1",
      Seq(t -> Seq(5.5, -5.5, 5.5, -5.5).map(bits(_)), t -> Seq(2.0, 2.0, -2.0, -2.0).map(bits(_))),
      Some((if (opcode == "FMod") Seq(1.5, 0.5, -0.5, -1.5) else Seq(1.5, -1.5, 1.5, -1.5)).map(bits(_)))
    )
  }

  private def ranges(width: Int): Seq[(Int, Int)] = Seq(
    (0, 0),
    (width, 0),
    (0, width),
    (width - 1, 1),
    (1, width - 2),
    (-1, 1),
    (0, -1),
    (width, 1),
    (width - 1, 2),
    (0, width + 1)
  )

  for {
    (vulkan, rows) <- Seq(
      false -> pairwise(Seq("UExtract", "SExtract", "Insert"), ranges(0).indices, Widths, Seq(1, 3, 4), Signs),
      true  -> pairwise(Seq("UExtract", "SExtract", "Insert"), ranges(0).indices, Seq(32, 64), Seq(1, 4), Signs)
    )
    (operation, range, width, lanes, signed) <- rows
    (offset, length) = ranges(width)(range)
  } test(
    s"${environment(vulkan)} OpBitField$operation range $offset/$length at width $width, lanes $lanes, signed $signed"
  ) {
    val left     = Seq(-1L, 0L, Long.MinValue | 0x80L, 0x5555555555555555L).take(lanes)
    val right    = Seq(0xaaaaaaaaaaaaaaaaL, -1L, 0x7fL, 0L).take(lanes)
    val valid    = offset >= 0 && length >= 0 && offset + length <= width
    val wordMask = (BigInt(1) << width) - 1
    val expected = left.zip(right).map { (a, b) =>
      val word      = BigInt(a) & wordMask
      val fieldMask = (BigInt(1) << length) - 1
      val extracted = (word >> offset) & fieldMask
      (operation match {
        case "UExtract" => extracted
        case "SExtract" =>
          if (length > 0 && extracted.testBit(length - 1)) extracted - (BigInt(1) << length) else extracted
        case _ => (word & (wordMask ^ (fieldMask << offset))) | ((BigInt(b) & fieldMask) << offset)
      }).toLong
    }
    // vulkan leaves out-of-range fields undefined: extraction yields zero and insertion keeps the base
    val undefined = if (operation == "Insert") left else Seq.fill(lanes)(0L)
    val t         = typ(signed, width, lanes)
    elementwise(
      t,
      s"%result = OpBitField$operation $t %a0${if (operation == "Insert") " %a1" else ""} %offset %count",
      Seq(t -> left, t -> right),
      if (valid) Some(expected) else Option.when(vulkan)(undefined),
      declarations = s"""%offset = OpConstant %u32 ${offset & 0xffffffffL}
                        |%count = OpConstant %u32 ${length & 0xffffffffL}""".stripMargin,
      vulkan = vulkan
    )
  }

  for ((width, lanes, signed) <- pairwise(Widths, Seq(1, 3, 4), Signs))
    test(s"OpBitReverse at width $width, lanes $lanes, signed $signed") {
      val left = Seq(-1L, 0L, Long.MinValue | 0x80L, 0x5555555555555555L).take(lanes)
      val t    = typ(signed, width, lanes)
      elementwise(
        t,
        s"%result = OpBitReverse $t %a0",
        Seq(t -> left),
        Some(left.map(value =>
          (0 until width).view.filter(BigInt(value).testBit(_)).map(width - 1 - _).map(1L << _).sum
        ))
      )
    }

  /** Computes `%result` of type `result` from `%a0`, `%a1`, ... and checks every stored lane; `expected = None` means
    * the dispatch faults without writing. Type names such as `%u32`, `%s8x4` and `%f16` are declared on use. OpenCL
    * passes inputs as kernel scalars; Vulkan loads them from the leading members of one storage buffer.
    */
  private def elementwise(
      result: String,
      body: String,
      inputs: Seq[(String, Seq[Long])],
      expected: Option[Seq[Long]],
      declarations: String = "",
      vulkan: Boolean = false
  )(using Location): Unit = {
    val members = inputs.map(_._1) :+ result
    val source  = (members ++ Seq(body, declarations, if (vulkan) "%u32" else "")).mkString(" ")
    val names   = TypeName.findAllMatchIn(source).map(_.matched).toSeq.distinct
    def shape(name: String): (String, Int, Int) = name match {
      case TypeName(kind, width, lanes) => (kind, width.toInt, Option(lanes).fold(1)(_.toInt))
      case _                            => fail(s"unknown type $name")
    }
    val shapes = names.map(name => shape(name))
    val types  = shapes.map((kind, width, _) => (kind, width)).distinct.map { (kind, width) =>
      s"%$kind$width = ${if (kind == "f") s"OpTypeFloat $width" else s"OpTypeInt $width ${if (kind == "s") 1 else 0}"}"
    } ++ shapes.collect {
      case (kind, width, lanes) if lanes > 1 => s"%$kind${width}x$lanes = OpTypeVector %$kind$width $lanes"
    } ++ Option.when(body.contains("%bool"))("%bool = OpTypeBool")
    def bytes(name: String): Int = shape(name)._2 / 8
    def slot(name: String): Int  = bytes(name) * (if (shape(name)._3 == 3) 4 else shape(name)._3)
    def encode(name: String, values: Seq[Long]): Vec[Byte] =
      Vec.from(values.flatMap(LittleEndian.toVec(_, bytes(name).refine[Int in 0 ~ 8].value)))
    val lanes             = shape(result)._3
    val output            = lanes * bytes(result)
    val memory            = RegionMemory()
    val (outcome, stored) = if (vulkan) {
      val stride = members.map(name => slot(name)).max
      val module = spv"""
        ${if (shapes.exists(_._2 == 64)) "OpCapability Int64" else ""}
        ${if (shapes.exists(_._2 == 16)) "OpCapability Int16" else ""}
        ${if (shapes.exists(_._2 == 8)) "OpCapability Int8" else ""}
        ${fixtures.vulkan}
        OpEntryPoint GLCompute %main "elementwise" %data
        OpExecutionMode %main LocalSize 1 1 1
        OpDecorate %block Block
        ${members.indices.map(i => s"OpMemberDecorate %block $i Offset ${i * stride}").mkString("\n")}
        OpDecorate %data DescriptorSet 0
        OpDecorate %data Binding 0
        %void = OpTypeVoid
        %fn = OpTypeFunction %void
        ${types.mkString("\n")}
        $declarations
        %block = OpTypeStruct ${members.mkString(" ")}
        %block_ptr = OpTypePointer StorageBuffer %block
        %data = OpVariable %block_ptr StorageBuffer
        ${members.distinct.map(name => s"%ptr_${name.drop(1)} = OpTypePointer StorageBuffer $name").mkString("\n")}
        ${members.indices.map(i => s"%member$i = OpConstant %u32 $i").mkString("\n")}
        %main = OpFunction %void None %fn
        %entry = OpLabel
        ${inputs.zipWithIndex.map { case ((name, _), i) =>
          s"%in$i = OpAccessChain %ptr_${name.drop(1)} %data %member$i\n%a$i = OpLoad $name %in$i"
        }.mkString("\n")}
        $body
        %out = OpAccessChain %ptr_${result.drop(1)} %data %member${inputs.size}
        OpStore %out %result
        OpReturn
        OpFunctionEnd
      """
      val data = memory.allocate((stride * members.size).toLong.refine[Long > 0].value)
      inputs.zipWithIndex.foreach { case ((name, values), i) =>
        val _ = encode(name, values).copyToArray(memory.array(data), i * stride)
      }
      run(module, "elementwise", memory, Vec(buffer(data))) ->
        memory.bytes(data).slice(inputs.size * stride, inputs.size * stride + output)
    } else {
      val module = spv"""
        $openCL
        OpEntryPoint Kernel %main "elementwise"
        %void = OpTypeVoid
        ${types.mkString("\n")}
        $declarations
        %out_ptr = OpTypePointer CrossWorkgroup $result
        %fn = OpTypeFunction %void %out_ptr ${inputs.map(_._1).mkString(" ")}
        %main = OpFunction %void None %fn
        %out = OpFunctionParameter %out_ptr
        ${inputs.indices.map(i => s"%a$i = OpFunctionParameter ${inputs(i)._1}").mkString("\n")}
        %entry = OpLabel
        $body
        OpStore %out %result
        OpReturn
        OpFunctionEnd
      """
      val out     = memory.allocate(slot(result).toLong.refine[Long > 0].value)
      val scalars = inputs.map((name, values) => KernelTask.Argument.Scalar(encode(name, values).unsafe))
      run(module, "elementwise", memory, Vec(buffer(out)) ++ Vec.from(scalars)) -> memory.bytes(out).take(output)
    }
    expected match {
      case Some(values) =>
        assertEquals(outcome, Right(()))
        assertEquals(stored, encode(result, values))
      case None =>
        assert(outcome.isLeft, s"expected a fault, found $outcome")
        assertEquals(stored, Vec.fill(output)(0.toByte))
    }
  }
}
