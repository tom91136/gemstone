// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

sealed trait KnownInstruction extends Product with Serializable {
  def arguments: InstructionArguments
  def opcode: Opcode
  def name: String
  final def syntax: Instruction = arguments.toSyntax(opcode)
}

enum InstructionDecodeError {
  case UnknownOpcode(opcode: String)
}

object KnownInstruction {
  private val ChunkCount: Int = 76

  def fromSyntax(value: Instruction): Either[InstructionDecodeError, KnownInstruction] = {
    val name = normalizeOpcode(value.opcode.value)
    val arguments = InstructionArguments(value.operands, value.modifiers)
    decode(name, arguments).toRight(InstructionDecodeError.UnknownOpcode(name))
  }

  private def decode(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    Math.floorMod(name.hashCode, ChunkCount) match {
      case 0 => decode0(name, arguments)
      case 1 => decode1(name, arguments)
      case 2 => decode2(name, arguments)
      case 3 => decode3(name, arguments)
      case 4 => decode4(name, arguments)
      case 5 => decode5(name, arguments)
      case 6 => decode6(name, arguments)
      case 7 => decode7(name, arguments)
      case 8 => decode8(name, arguments)
      case 9 => decode9(name, arguments)
      case 10 => decode10(name, arguments)
      case 11 => decode11(name, arguments)
      case 12 => decode12(name, arguments)
      case 13 => decode13(name, arguments)
      case 14 => decode14(name, arguments)
      case 15 => decode15(name, arguments)
      case 16 => decode16(name, arguments)
      case 17 => decode17(name, arguments)
      case 18 => decode18(name, arguments)
      case 19 => decode19(name, arguments)
      case 20 => decode20(name, arguments)
      case 21 => decode21(name, arguments)
      case 22 => decode22(name, arguments)
      case 23 => decode23(name, arguments)
      case 24 => decode24(name, arguments)
      case 25 => decode25(name, arguments)
      case 26 => decode26(name, arguments)
      case 27 => decode27(name, arguments)
      case 28 => decode28(name, arguments)
      case 29 => decode29(name, arguments)
      case 30 => decode30(name, arguments)
      case 31 => decode31(name, arguments)
      case 32 => decode32(name, arguments)
      case 33 => decode33(name, arguments)
      case 34 => decode34(name, arguments)
      case 35 => decode35(name, arguments)
      case 36 => decode36(name, arguments)
      case 37 => decode37(name, arguments)
      case 38 => decode38(name, arguments)
      case 39 => decode39(name, arguments)
      case 40 => decode40(name, arguments)
      case 41 => decode41(name, arguments)
      case 42 => decode42(name, arguments)
      case 43 => decode43(name, arguments)
      case 44 => decode44(name, arguments)
      case 45 => decode45(name, arguments)
      case 46 => decode46(name, arguments)
      case 47 => decode47(name, arguments)
      case 48 => decode48(name, arguments)
      case 49 => decode49(name, arguments)
      case 50 => decode50(name, arguments)
      case 51 => decode51(name, arguments)
      case 52 => decode52(name, arguments)
      case 53 => decode53(name, arguments)
      case 54 => decode54(name, arguments)
      case 55 => decode55(name, arguments)
      case 56 => decode56(name, arguments)
      case 57 => decode57(name, arguments)
      case 58 => decode58(name, arguments)
      case 59 => decode59(name, arguments)
      case 60 => decode60(name, arguments)
      case 61 => decode61(name, arguments)
      case 62 => decode62(name, arguments)
      case 63 => decode63(name, arguments)
      case 64 => decode64(name, arguments)
      case 65 => decode65(name, arguments)
      case 66 => decode66(name, arguments)
      case 67 => decode67(name, arguments)
      case 68 => decode68(name, arguments)
      case 69 => decode69(name, arguments)
      case 70 => decode70(name, arguments)
      case 71 => decode71(name, arguments)
      case 72 => decode72(name, arguments)
      case 73 => decode73(name, arguments)
      case 74 => decode74(name, arguments)
      case 75 => decode75(name, arguments)
    }

  final case class BufferAtomicAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_add")
    override val name: String = "buffer_atomic_add"
  }

  final case class BufferAtomicAddF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_add_f32")
    override val name: String = "buffer_atomic_add_f32"
  }

  final case class BufferAtomicAddF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_add_f64")
    override val name: String = "buffer_atomic_add_f64"
  }

  final case class BufferAtomicAddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_add_u32")
    override val name: String = "buffer_atomic_add_u32"
  }

  final case class BufferAtomicAddU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_add_u64")
    override val name: String = "buffer_atomic_add_u64"
  }

  final case class BufferAtomicAddX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_add_x2")
    override val name: String = "buffer_atomic_add_x2"
  }

  final case class BufferAtomicAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_and")
    override val name: String = "buffer_atomic_and"
  }

  final case class BufferAtomicAndB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_and_b32")
    override val name: String = "buffer_atomic_and_b32"
  }

  final case class BufferAtomicAndB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_and_b64")
    override val name: String = "buffer_atomic_and_b64"
  }

  final case class BufferAtomicAndX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_and_x2")
    override val name: String = "buffer_atomic_and_x2"
  }

  final case class BufferAtomicCmpswap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_cmpswap")
    override val name: String = "buffer_atomic_cmpswap"
  }

  final case class BufferAtomicCmpswapB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_cmpswap_b32")
    override val name: String = "buffer_atomic_cmpswap_b32"
  }

  final case class BufferAtomicCmpswapB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_cmpswap_b64")
    override val name: String = "buffer_atomic_cmpswap_b64"
  }

  final case class BufferAtomicCmpswapF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_cmpswap_f32")
    override val name: String = "buffer_atomic_cmpswap_f32"
  }

  final case class BufferAtomicCmpswapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_cmpswap_x2")
    override val name: String = "buffer_atomic_cmpswap_x2"
  }

  final case class BufferAtomicCondSubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_cond_sub_u32")
    override val name: String = "buffer_atomic_cond_sub_u32"
  }

  final case class BufferAtomicCsub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_csub")
    override val name: String = "buffer_atomic_csub"
  }

  final case class BufferAtomicCsubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_csub_u32")
    override val name: String = "buffer_atomic_csub_u32"
  }

  final case class BufferAtomicDec(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_dec")
    override val name: String = "buffer_atomic_dec"
  }

  final case class BufferAtomicDecU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_dec_u32")
    override val name: String = "buffer_atomic_dec_u32"
  }

  final case class BufferAtomicDecU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_dec_u64")
    override val name: String = "buffer_atomic_dec_u64"
  }

  final case class BufferAtomicDecX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_dec_x2")
    override val name: String = "buffer_atomic_dec_x2"
  }

  final case class BufferAtomicFcmpswap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_fcmpswap")
    override val name: String = "buffer_atomic_fcmpswap"
  }

  final case class BufferAtomicFcmpswapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_fcmpswap_x2")
    override val name: String = "buffer_atomic_fcmpswap_x2"
  }

  final case class BufferAtomicFmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_fmax")
    override val name: String = "buffer_atomic_fmax"
  }

  final case class BufferAtomicFmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_fmax_x2")
    override val name: String = "buffer_atomic_fmax_x2"
  }

  final case class BufferAtomicFmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_fmin")
    override val name: String = "buffer_atomic_fmin"
  }

  final case class BufferAtomicFminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_fmin_x2")
    override val name: String = "buffer_atomic_fmin_x2"
  }

  final case class BufferAtomicInc(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_inc")
    override val name: String = "buffer_atomic_inc"
  }

  final case class BufferAtomicIncU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_inc_u32")
    override val name: String = "buffer_atomic_inc_u32"
  }

  final case class BufferAtomicIncU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_inc_u64")
    override val name: String = "buffer_atomic_inc_u64"
  }

  final case class BufferAtomicIncX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_inc_x2")
    override val name: String = "buffer_atomic_inc_x2"
  }

  final case class BufferAtomicMaxF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_max_f32")
    override val name: String = "buffer_atomic_max_f32"
  }

  final case class BufferAtomicMaxF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_max_f64")
    override val name: String = "buffer_atomic_max_f64"
  }

  final case class BufferAtomicMaxI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_max_i32")
    override val name: String = "buffer_atomic_max_i32"
  }

  final case class BufferAtomicMaxI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_max_i64")
    override val name: String = "buffer_atomic_max_i64"
  }

  final case class BufferAtomicMaxNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_max_num_f32")
    override val name: String = "buffer_atomic_max_num_f32"
  }

  final case class BufferAtomicMaxNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_max_num_f64")
    override val name: String = "buffer_atomic_max_num_f64"
  }

  final case class BufferAtomicMaxU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_max_u32")
    override val name: String = "buffer_atomic_max_u32"
  }

  final case class BufferAtomicMaxU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_max_u64")
    override val name: String = "buffer_atomic_max_u64"
  }

  final case class BufferAtomicMinF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_min_f32")
    override val name: String = "buffer_atomic_min_f32"
  }

  final case class BufferAtomicMinF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_min_f64")
    override val name: String = "buffer_atomic_min_f64"
  }

  final case class BufferAtomicMinI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_min_i32")
    override val name: String = "buffer_atomic_min_i32"
  }

  final case class BufferAtomicMinI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_min_i64")
    override val name: String = "buffer_atomic_min_i64"
  }

  final case class BufferAtomicMinNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_min_num_f32")
    override val name: String = "buffer_atomic_min_num_f32"
  }

  final case class BufferAtomicMinNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_min_num_f64")
    override val name: String = "buffer_atomic_min_num_f64"
  }

  final case class BufferAtomicMinU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_min_u32")
    override val name: String = "buffer_atomic_min_u32"
  }

  final case class BufferAtomicMinU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_min_u64")
    override val name: String = "buffer_atomic_min_u64"
  }

  final case class BufferAtomicOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_or")
    override val name: String = "buffer_atomic_or"
  }

  final case class BufferAtomicOrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_or_b32")
    override val name: String = "buffer_atomic_or_b32"
  }

  final case class BufferAtomicOrB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_or_b64")
    override val name: String = "buffer_atomic_or_b64"
  }

  final case class BufferAtomicOrX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_or_x2")
    override val name: String = "buffer_atomic_or_x2"
  }

  final case class BufferAtomicPkAddBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_pk_add_bf16")
    override val name: String = "buffer_atomic_pk_add_bf16"
  }

  final case class BufferAtomicPkAddF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_pk_add_f16")
    override val name: String = "buffer_atomic_pk_add_f16"
  }

  final case class BufferAtomicRsub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_rsub")
    override val name: String = "buffer_atomic_rsub"
  }

  final case class BufferAtomicRsubX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_rsub_x2")
    override val name: String = "buffer_atomic_rsub_x2"
  }

  final case class BufferAtomicSmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_smax")
    override val name: String = "buffer_atomic_smax"
  }

  final case class BufferAtomicSmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_smax_x2")
    override val name: String = "buffer_atomic_smax_x2"
  }

  final case class BufferAtomicSmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_smin")
    override val name: String = "buffer_atomic_smin"
  }

  final case class BufferAtomicSminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_smin_x2")
    override val name: String = "buffer_atomic_smin_x2"
  }

  final case class BufferAtomicSub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_sub")
    override val name: String = "buffer_atomic_sub"
  }

  final case class BufferAtomicSubClampU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_sub_clamp_u32")
    override val name: String = "buffer_atomic_sub_clamp_u32"
  }

  final case class BufferAtomicSubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_sub_u32")
    override val name: String = "buffer_atomic_sub_u32"
  }

  final case class BufferAtomicSubU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_sub_u64")
    override val name: String = "buffer_atomic_sub_u64"
  }

  final case class BufferAtomicSubX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_sub_x2")
    override val name: String = "buffer_atomic_sub_x2"
  }

  final case class BufferAtomicSwap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_swap")
    override val name: String = "buffer_atomic_swap"
  }

  final case class BufferAtomicSwapB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_swap_b32")
    override val name: String = "buffer_atomic_swap_b32"
  }

  final case class BufferAtomicSwapB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_swap_b64")
    override val name: String = "buffer_atomic_swap_b64"
  }

  final case class BufferAtomicSwapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_swap_x2")
    override val name: String = "buffer_atomic_swap_x2"
  }

  final case class BufferAtomicUmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_umax")
    override val name: String = "buffer_atomic_umax"
  }

  final case class BufferAtomicUmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_umax_x2")
    override val name: String = "buffer_atomic_umax_x2"
  }

  final case class BufferAtomicUmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_umin")
    override val name: String = "buffer_atomic_umin"
  }

  final case class BufferAtomicUminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_umin_x2")
    override val name: String = "buffer_atomic_umin_x2"
  }

  final case class BufferAtomicXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_xor")
    override val name: String = "buffer_atomic_xor"
  }

  final case class BufferAtomicXorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_xor_b32")
    override val name: String = "buffer_atomic_xor_b32"
  }

  final case class BufferAtomicXorB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_xor_b64")
    override val name: String = "buffer_atomic_xor_b64"
  }

  final case class BufferAtomicXorX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_atomic_xor_x2")
    override val name: String = "buffer_atomic_xor_x2"
  }

  final case class BufferGl0Inv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_gl0_inv")
    override val name: String = "buffer_gl0_inv"
  }

  final case class BufferGl1Inv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_gl1_inv")
    override val name: String = "buffer_gl1_inv"
  }

  final case class BufferInv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_inv")
    override val name: String = "buffer_inv"
  }

  final case class BufferInvl2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_invl2")
    override val name: String = "buffer_invl2"
  }

  final case class BufferLoadB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_b128")
    override val name: String = "buffer_load_b128"
  }

  final case class BufferLoadB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_b32")
    override val name: String = "buffer_load_b32"
  }

  final case class BufferLoadB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_b64")
    override val name: String = "buffer_load_b64"
  }

  final case class BufferLoadB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_b96")
    override val name: String = "buffer_load_b96"
  }

  final case class BufferLoadD16B16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_b16")
    override val name: String = "buffer_load_d16_b16"
  }

  final case class BufferLoadD16FormatX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_format_x")
    override val name: String = "buffer_load_d16_format_x"
  }

  final case class BufferLoadD16FormatXy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_format_xy")
    override val name: String = "buffer_load_d16_format_xy"
  }

  final case class BufferLoadD16FormatXyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_format_xyz")
    override val name: String = "buffer_load_d16_format_xyz"
  }

  final case class BufferLoadD16FormatXyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_format_xyzw")
    override val name: String = "buffer_load_d16_format_xyzw"
  }

  final case class BufferLoadD16HiB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_hi_b16")
    override val name: String = "buffer_load_d16_hi_b16"
  }

  final case class BufferLoadD16HiFormatX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_hi_format_x")
    override val name: String = "buffer_load_d16_hi_format_x"
  }

  final case class BufferLoadD16HiI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_hi_i8")
    override val name: String = "buffer_load_d16_hi_i8"
  }

  final case class BufferLoadD16HiU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_hi_u8")
    override val name: String = "buffer_load_d16_hi_u8"
  }

  final case class BufferLoadD16I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_i8")
    override val name: String = "buffer_load_d16_i8"
  }

  final case class BufferLoadD16U8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_d16_u8")
    override val name: String = "buffer_load_d16_u8"
  }

  final case class BufferLoadDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_dword")
    override val name: String = "buffer_load_dword"
  }

  final case class BufferLoadDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_dwordx2")
    override val name: String = "buffer_load_dwordx2"
  }

  final case class BufferLoadDwordx3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_dwordx3")
    override val name: String = "buffer_load_dwordx3"
  }

  final case class BufferLoadDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_dwordx4")
    override val name: String = "buffer_load_dwordx4"
  }

  final case class BufferLoadFormatD16HiX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_format_d16_hi_x")
    override val name: String = "buffer_load_format_d16_hi_x"
  }

  final case class BufferLoadFormatD16X(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_format_d16_x")
    override val name: String = "buffer_load_format_d16_x"
  }

  final case class BufferLoadFormatD16Xy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_format_d16_xy")
    override val name: String = "buffer_load_format_d16_xy"
  }

  final case class BufferLoadFormatD16Xyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_format_d16_xyz")
    override val name: String = "buffer_load_format_d16_xyz"
  }

  final case class BufferLoadFormatD16Xyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_format_d16_xyzw")
    override val name: String = "buffer_load_format_d16_xyzw"
  }

  final case class BufferLoadFormatX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_format_x")
    override val name: String = "buffer_load_format_x"
  }

  final case class BufferLoadFormatXy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_format_xy")
    override val name: String = "buffer_load_format_xy"
  }

  final case class BufferLoadFormatXyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_format_xyz")
    override val name: String = "buffer_load_format_xyz"
  }

  final case class BufferLoadFormatXyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_format_xyzw")
    override val name: String = "buffer_load_format_xyzw"
  }

  final case class BufferLoadI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_i16")
    override val name: String = "buffer_load_i16"
  }

  final case class BufferLoadI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_i8")
    override val name: String = "buffer_load_i8"
  }

  final case class BufferLoadSbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_sbyte")
    override val name: String = "buffer_load_sbyte"
  }

  final case class BufferLoadSbyteD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_sbyte_d16")
    override val name: String = "buffer_load_sbyte_d16"
  }

  final case class BufferLoadSbyteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_sbyte_d16_hi")
    override val name: String = "buffer_load_sbyte_d16_hi"
  }

  final case class BufferLoadShortD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_short_d16")
    override val name: String = "buffer_load_short_d16"
  }

  final case class BufferLoadShortD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_short_d16_hi")
    override val name: String = "buffer_load_short_d16_hi"
  }

  final case class BufferLoadSshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_sshort")
    override val name: String = "buffer_load_sshort"
  }

  final case class BufferLoadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_u16")
    override val name: String = "buffer_load_u16"
  }

  final case class BufferLoadU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_u8")
    override val name: String = "buffer_load_u8"
  }

  final case class BufferLoadUbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_ubyte")
    override val name: String = "buffer_load_ubyte"
  }

  final case class BufferLoadUbyteD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_ubyte_d16")
    override val name: String = "buffer_load_ubyte_d16"
  }

  final case class BufferLoadUbyteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_ubyte_d16_hi")
    override val name: String = "buffer_load_ubyte_d16_hi"
  }

  final case class BufferLoadUshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_load_ushort")
    override val name: String = "buffer_load_ushort"
  }

  final case class BufferStoreB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_b128")
    override val name: String = "buffer_store_b128"
  }

  final case class BufferStoreB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_b16")
    override val name: String = "buffer_store_b16"
  }

  final case class BufferStoreB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_b32")
    override val name: String = "buffer_store_b32"
  }

  final case class BufferStoreB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_b64")
    override val name: String = "buffer_store_b64"
  }

  final case class BufferStoreB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_b8")
    override val name: String = "buffer_store_b8"
  }

  final case class BufferStoreB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_b96")
    override val name: String = "buffer_store_b96"
  }

  final case class BufferStoreByte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_byte")
    override val name: String = "buffer_store_byte"
  }

  final case class BufferStoreByteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_byte_d16_hi")
    override val name: String = "buffer_store_byte_d16_hi"
  }

  final case class BufferStoreD16FormatX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_d16_format_x")
    override val name: String = "buffer_store_d16_format_x"
  }

  final case class BufferStoreD16FormatXy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_d16_format_xy")
    override val name: String = "buffer_store_d16_format_xy"
  }

  final case class BufferStoreD16FormatXyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_d16_format_xyz")
    override val name: String = "buffer_store_d16_format_xyz"
  }

  final case class BufferStoreD16FormatXyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_d16_format_xyzw")
    override val name: String = "buffer_store_d16_format_xyzw"
  }

  final case class BufferStoreD16HiB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_d16_hi_b16")
    override val name: String = "buffer_store_d16_hi_b16"
  }

  final case class BufferStoreD16HiB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_d16_hi_b8")
    override val name: String = "buffer_store_d16_hi_b8"
  }

  final case class BufferStoreD16HiFormatX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_d16_hi_format_x")
    override val name: String = "buffer_store_d16_hi_format_x"
  }

  final case class BufferStoreDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_dword")
    override val name: String = "buffer_store_dword"
  }

  final case class BufferStoreDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_dwordx2")
    override val name: String = "buffer_store_dwordx2"
  }

  final case class BufferStoreDwordx3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_dwordx3")
    override val name: String = "buffer_store_dwordx3"
  }

  final case class BufferStoreDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_dwordx4")
    override val name: String = "buffer_store_dwordx4"
  }

  final case class BufferStoreFormatD16HiX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_format_d16_hi_x")
    override val name: String = "buffer_store_format_d16_hi_x"
  }

  final case class BufferStoreFormatD16X(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_format_d16_x")
    override val name: String = "buffer_store_format_d16_x"
  }

  final case class BufferStoreFormatD16Xy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_format_d16_xy")
    override val name: String = "buffer_store_format_d16_xy"
  }

  final case class BufferStoreFormatD16Xyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_format_d16_xyz")
    override val name: String = "buffer_store_format_d16_xyz"
  }

  final case class BufferStoreFormatD16Xyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_format_d16_xyzw")
    override val name: String = "buffer_store_format_d16_xyzw"
  }

  final case class BufferStoreFormatX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_format_x")
    override val name: String = "buffer_store_format_x"
  }

  final case class BufferStoreFormatXy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_format_xy")
    override val name: String = "buffer_store_format_xy"
  }

  final case class BufferStoreFormatXyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_format_xyz")
    override val name: String = "buffer_store_format_xyz"
  }

  final case class BufferStoreFormatXyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_format_xyzw")
    override val name: String = "buffer_store_format_xyzw"
  }

  final case class BufferStoreLdsDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_lds_dword")
    override val name: String = "buffer_store_lds_dword"
  }

  final case class BufferStoreShort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_short")
    override val name: String = "buffer_store_short"
  }

  final case class BufferStoreShortD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_store_short_d16_hi")
    override val name: String = "buffer_store_short_d16_hi"
  }

  final case class BufferWbinvl1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_wbinvl1")
    override val name: String = "buffer_wbinvl1"
  }

  final case class BufferWbinvl1Vol(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_wbinvl1_vol")
    override val name: String = "buffer_wbinvl1_vol"
  }

  final case class BufferWbl2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("buffer_wbl2")
    override val name: String = "buffer_wbl2"
  }

  final case class ClusterLoadAsyncToLdsB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("cluster_load_async_to_lds_b128")
    override val name: String = "cluster_load_async_to_lds_b128"
  }

  final case class ClusterLoadAsyncToLdsB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("cluster_load_async_to_lds_b32")
    override val name: String = "cluster_load_async_to_lds_b32"
  }

  final case class ClusterLoadAsyncToLdsB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("cluster_load_async_to_lds_b64")
    override val name: String = "cluster_load_async_to_lds_b64"
  }

  final case class ClusterLoadAsyncToLdsB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("cluster_load_async_to_lds_b8")
    override val name: String = "cluster_load_async_to_lds_b8"
  }

  final case class ClusterLoadB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("cluster_load_b128")
    override val name: String = "cluster_load_b128"
  }

  final case class ClusterLoadB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("cluster_load_b32")
    override val name: String = "cluster_load_b32"
  }

  final case class ClusterLoadB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("cluster_load_b64")
    override val name: String = "cluster_load_b64"
  }

  final case class DsAddF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_f32")
    override val name: String = "ds_add_f32"
  }

  final case class DsAddF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_f64")
    override val name: String = "ds_add_f64"
  }

  final case class DsAddGsRegRtn(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_gs_reg_rtn")
    override val name: String = "ds_add_gs_reg_rtn"
  }

  final case class DsAddRtnF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_rtn_f32")
    override val name: String = "ds_add_rtn_f32"
  }

  final case class DsAddRtnF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_rtn_f64")
    override val name: String = "ds_add_rtn_f64"
  }

  final case class DsAddRtnU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_rtn_u32")
    override val name: String = "ds_add_rtn_u32"
  }

  final case class DsAddRtnU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_rtn_u64")
    override val name: String = "ds_add_rtn_u64"
  }

  final case class DsAddSrc2F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_src2_f32")
    override val name: String = "ds_add_src2_f32"
  }

  final case class DsAddSrc2U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_src2_u32")
    override val name: String = "ds_add_src2_u32"
  }

  final case class DsAddSrc2U64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_src2_u64")
    override val name: String = "ds_add_src2_u64"
  }

  final case class DsAddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_u32")
    override val name: String = "ds_add_u32"
  }

  final case class DsAddU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_add_u64")
    override val name: String = "ds_add_u64"
  }

  final case class DsAndB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_and_b32")
    override val name: String = "ds_and_b32"
  }

  final case class DsAndB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_and_b64")
    override val name: String = "ds_and_b64"
  }

  final case class DsAndRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_and_rtn_b32")
    override val name: String = "ds_and_rtn_b32"
  }

  final case class DsAndRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_and_rtn_b64")
    override val name: String = "ds_and_rtn_b64"
  }

  final case class DsAndSrc2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_and_src2_b32")
    override val name: String = "ds_and_src2_b32"
  }

  final case class DsAndSrc2B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_and_src2_b64")
    override val name: String = "ds_and_src2_b64"
  }

  final case class DsAppend(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_append")
    override val name: String = "ds_append"
  }

  final case class DsAtomicAsyncBarrierArriveB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_atomic_async_barrier_arrive_b64")
    override val name: String = "ds_atomic_async_barrier_arrive_b64"
  }

  final case class DsAtomicBarrierArriveRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_atomic_barrier_arrive_rtn_b64")
    override val name: String = "ds_atomic_barrier_arrive_rtn_b64"
  }

  final case class DsBpermuteB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_bpermute_b32")
    override val name: String = "ds_bpermute_b32"
  }

  final case class DsBpermuteFiB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_bpermute_fi_b32")
    override val name: String = "ds_bpermute_fi_b32"
  }

  final case class DsBvhStackPush4Pop1RtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_bvh_stack_push4_pop1_rtn_b32")
    override val name: String = "ds_bvh_stack_push4_pop1_rtn_b32"
  }

  final case class DsBvhStackPush8Pop1RtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_bvh_stack_push8_pop1_rtn_b32")
    override val name: String = "ds_bvh_stack_push8_pop1_rtn_b32"
  }

  final case class DsBvhStackPush8Pop2RtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_bvh_stack_push8_pop2_rtn_b64")
    override val name: String = "ds_bvh_stack_push8_pop2_rtn_b64"
  }

  final case class DsBvhStackRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_bvh_stack_rtn_b32")
    override val name: String = "ds_bvh_stack_rtn_b32"
  }

  final case class DsCmpstB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpst_b32")
    override val name: String = "ds_cmpst_b32"
  }

  final case class DsCmpstB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpst_b64")
    override val name: String = "ds_cmpst_b64"
  }

  final case class DsCmpstF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpst_f32")
    override val name: String = "ds_cmpst_f32"
  }

  final case class DsCmpstF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpst_f64")
    override val name: String = "ds_cmpst_f64"
  }

  final case class DsCmpstRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpst_rtn_b32")
    override val name: String = "ds_cmpst_rtn_b32"
  }

  final case class DsCmpstRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpst_rtn_b64")
    override val name: String = "ds_cmpst_rtn_b64"
  }

  final case class DsCmpstRtnF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpst_rtn_f32")
    override val name: String = "ds_cmpst_rtn_f32"
  }

  final case class DsCmpstRtnF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpst_rtn_f64")
    override val name: String = "ds_cmpst_rtn_f64"
  }

  final case class DsCmpstoreB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpstore_b32")
    override val name: String = "ds_cmpstore_b32"
  }

  final case class DsCmpstoreB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpstore_b64")
    override val name: String = "ds_cmpstore_b64"
  }

  final case class DsCmpstoreF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpstore_f32")
    override val name: String = "ds_cmpstore_f32"
  }

  final case class DsCmpstoreF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpstore_f64")
    override val name: String = "ds_cmpstore_f64"
  }

  final case class DsCmpstoreRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpstore_rtn_b32")
    override val name: String = "ds_cmpstore_rtn_b32"
  }

  final case class DsCmpstoreRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpstore_rtn_b64")
    override val name: String = "ds_cmpstore_rtn_b64"
  }

  final case class DsCmpstoreRtnF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpstore_rtn_f32")
    override val name: String = "ds_cmpstore_rtn_f32"
  }

  final case class DsCmpstoreRtnF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cmpstore_rtn_f64")
    override val name: String = "ds_cmpstore_rtn_f64"
  }

  final case class DsCondSubRtnU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cond_sub_rtn_u32")
    override val name: String = "ds_cond_sub_rtn_u32"
  }

  final case class DsCondSubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_cond_sub_u32")
    override val name: String = "ds_cond_sub_u32"
  }

  final case class DsCondxchg32RtnB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_condxchg32_rtn_b128")
    override val name: String = "ds_condxchg32_rtn_b128"
  }

  final case class DsCondxchg32RtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_condxchg32_rtn_b64")
    override val name: String = "ds_condxchg32_rtn_b64"
  }

  final case class DsConsume(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_consume")
    override val name: String = "ds_consume"
  }

  final case class DsDecRtnU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_dec_rtn_u32")
    override val name: String = "ds_dec_rtn_u32"
  }

  final case class DsDecRtnU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_dec_rtn_u64")
    override val name: String = "ds_dec_rtn_u64"
  }

  final case class DsDecSrc2U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_dec_src2_u32")
    override val name: String = "ds_dec_src2_u32"
  }

  final case class DsDecSrc2U64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_dec_src2_u64")
    override val name: String = "ds_dec_src2_u64"
  }

  final case class DsDecU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_dec_u32")
    override val name: String = "ds_dec_u32"
  }

  final case class DsDecU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_dec_u64")
    override val name: String = "ds_dec_u64"
  }

  final case class DsDirectLoad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_direct_load")
    override val name: String = "ds_direct_load"
  }

  final case class DsGwsBarrier(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_gws_barrier")
    override val name: String = "ds_gws_barrier"
  }

  final case class DsGwsInit(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_gws_init")
    override val name: String = "ds_gws_init"
  }

  final case class DsGwsSemaBr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_gws_sema_br")
    override val name: String = "ds_gws_sema_br"
  }

  final case class DsGwsSemaP(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_gws_sema_p")
    override val name: String = "ds_gws_sema_p"
  }

  final case class DsGwsSemaReleaseAll(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_gws_sema_release_all")
    override val name: String = "ds_gws_sema_release_all"
  }

  final case class DsGwsSemaV(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_gws_sema_v")
    override val name: String = "ds_gws_sema_v"
  }

  final case class DsIncRtnU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_inc_rtn_u32")
    override val name: String = "ds_inc_rtn_u32"
  }

  final case class DsIncRtnU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_inc_rtn_u64")
    override val name: String = "ds_inc_rtn_u64"
  }

  final case class DsIncSrc2U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_inc_src2_u32")
    override val name: String = "ds_inc_src2_u32"
  }

  final case class DsIncSrc2U64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_inc_src2_u64")
    override val name: String = "ds_inc_src2_u64"
  }

  final case class DsIncU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_inc_u32")
    override val name: String = "ds_inc_u32"
  }

  final case class DsIncU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_inc_u64")
    override val name: String = "ds_inc_u64"
  }

  final case class DsLoad2addrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_2addr_b32")
    override val name: String = "ds_load_2addr_b32"
  }

  final case class DsLoad2addrB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_2addr_b64")
    override val name: String = "ds_load_2addr_b64"
  }

  final case class DsLoad2addrStride64B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_2addr_stride64_b32")
    override val name: String = "ds_load_2addr_stride64_b32"
  }

  final case class DsLoad2addrStride64B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_2addr_stride64_b64")
    override val name: String = "ds_load_2addr_stride64_b64"
  }

  final case class DsLoadAddtidB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_addtid_b32")
    override val name: String = "ds_load_addtid_b32"
  }

  final case class DsLoadB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_b128")
    override val name: String = "ds_load_b128"
  }

  final case class DsLoadB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_b32")
    override val name: String = "ds_load_b32"
  }

  final case class DsLoadB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_b64")
    override val name: String = "ds_load_b64"
  }

  final case class DsLoadB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_b96")
    override val name: String = "ds_load_b96"
  }

  final case class DsLoadI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_i16")
    override val name: String = "ds_load_i16"
  }

  final case class DsLoadI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_i8")
    override val name: String = "ds_load_i8"
  }

  final case class DsLoadI8D16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_i8_d16")
    override val name: String = "ds_load_i8_d16"
  }

  final case class DsLoadI8D16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_i8_d16_hi")
    override val name: String = "ds_load_i8_d16_hi"
  }

  final case class DsLoadTr16B128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_tr16_b128")
    override val name: String = "ds_load_tr16_b128"
  }

  final case class DsLoadTr4B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_tr4_b64")
    override val name: String = "ds_load_tr4_b64"
  }

  final case class DsLoadTr6B96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_tr6_b96")
    override val name: String = "ds_load_tr6_b96"
  }

  final case class DsLoadTr8B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_tr8_b64")
    override val name: String = "ds_load_tr8_b64"
  }

  final case class DsLoadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_u16")
    override val name: String = "ds_load_u16"
  }

  final case class DsLoadU16D16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_u16_d16")
    override val name: String = "ds_load_u16_d16"
  }

  final case class DsLoadU16D16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_u16_d16_hi")
    override val name: String = "ds_load_u16_d16_hi"
  }

  final case class DsLoadU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_u8")
    override val name: String = "ds_load_u8"
  }

  final case class DsLoadU8D16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_u8_d16")
    override val name: String = "ds_load_u8_d16"
  }

  final case class DsLoadU8D16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_load_u8_d16_hi")
    override val name: String = "ds_load_u8_d16_hi"
  }

  final case class DsMaxF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_f32")
    override val name: String = "ds_max_f32"
  }

  final case class DsMaxF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_f64")
    override val name: String = "ds_max_f64"
  }

  final case class DsMaxI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_i32")
    override val name: String = "ds_max_i32"
  }

  final case class DsMaxI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_i64")
    override val name: String = "ds_max_i64"
  }

  final case class DsMaxNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_num_f32")
    override val name: String = "ds_max_num_f32"
  }

  final case class DsMaxNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_num_f64")
    override val name: String = "ds_max_num_f64"
  }

  final case class DsMaxNumRtnF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_num_rtn_f32")
    override val name: String = "ds_max_num_rtn_f32"
  }

  final case class DsMaxNumRtnF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_num_rtn_f64")
    override val name: String = "ds_max_num_rtn_f64"
  }

  final case class DsMaxRtnF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_rtn_f32")
    override val name: String = "ds_max_rtn_f32"
  }

  final case class DsMaxRtnF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_rtn_f64")
    override val name: String = "ds_max_rtn_f64"
  }

  final case class DsMaxRtnI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_rtn_i32")
    override val name: String = "ds_max_rtn_i32"
  }

  final case class DsMaxRtnI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_rtn_i64")
    override val name: String = "ds_max_rtn_i64"
  }

  final case class DsMaxRtnU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_rtn_u32")
    override val name: String = "ds_max_rtn_u32"
  }

  final case class DsMaxRtnU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_rtn_u64")
    override val name: String = "ds_max_rtn_u64"
  }

  final case class DsMaxSrc2F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_src2_f32")
    override val name: String = "ds_max_src2_f32"
  }

  final case class DsMaxSrc2F64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_src2_f64")
    override val name: String = "ds_max_src2_f64"
  }

  final case class DsMaxSrc2I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_src2_i32")
    override val name: String = "ds_max_src2_i32"
  }

  final case class DsMaxSrc2I64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_src2_i64")
    override val name: String = "ds_max_src2_i64"
  }

  final case class DsMaxSrc2U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_src2_u32")
    override val name: String = "ds_max_src2_u32"
  }

  final case class DsMaxSrc2U64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_src2_u64")
    override val name: String = "ds_max_src2_u64"
  }

  final case class DsMaxU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_u32")
    override val name: String = "ds_max_u32"
  }

  final case class DsMaxU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_max_u64")
    override val name: String = "ds_max_u64"
  }

  final case class DsMinF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_f32")
    override val name: String = "ds_min_f32"
  }

  final case class DsMinF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_f64")
    override val name: String = "ds_min_f64"
  }

  final case class DsMinI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_i32")
    override val name: String = "ds_min_i32"
  }

  final case class DsMinI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_i64")
    override val name: String = "ds_min_i64"
  }

  final case class DsMinNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_num_f32")
    override val name: String = "ds_min_num_f32"
  }

  final case class DsMinNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_num_f64")
    override val name: String = "ds_min_num_f64"
  }

  final case class DsMinNumRtnF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_num_rtn_f32")
    override val name: String = "ds_min_num_rtn_f32"
  }

  final case class DsMinNumRtnF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_num_rtn_f64")
    override val name: String = "ds_min_num_rtn_f64"
  }

  final case class DsMinRtnF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_rtn_f32")
    override val name: String = "ds_min_rtn_f32"
  }

  final case class DsMinRtnF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_rtn_f64")
    override val name: String = "ds_min_rtn_f64"
  }

  final case class DsMinRtnI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_rtn_i32")
    override val name: String = "ds_min_rtn_i32"
  }

  final case class DsMinRtnI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_rtn_i64")
    override val name: String = "ds_min_rtn_i64"
  }

  final case class DsMinRtnU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_rtn_u32")
    override val name: String = "ds_min_rtn_u32"
  }

  final case class DsMinRtnU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_rtn_u64")
    override val name: String = "ds_min_rtn_u64"
  }

  final case class DsMinSrc2F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_src2_f32")
    override val name: String = "ds_min_src2_f32"
  }

  final case class DsMinSrc2F64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_src2_f64")
    override val name: String = "ds_min_src2_f64"
  }

  final case class DsMinSrc2I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_src2_i32")
    override val name: String = "ds_min_src2_i32"
  }

  final case class DsMinSrc2I64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_src2_i64")
    override val name: String = "ds_min_src2_i64"
  }

  final case class DsMinSrc2U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_src2_u32")
    override val name: String = "ds_min_src2_u32"
  }

  final case class DsMinSrc2U64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_src2_u64")
    override val name: String = "ds_min_src2_u64"
  }

  final case class DsMinU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_u32")
    override val name: String = "ds_min_u32"
  }

  final case class DsMinU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_min_u64")
    override val name: String = "ds_min_u64"
  }

  final case class DsMskorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_mskor_b32")
    override val name: String = "ds_mskor_b32"
  }

  final case class DsMskorB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_mskor_b64")
    override val name: String = "ds_mskor_b64"
  }

  final case class DsMskorRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_mskor_rtn_b32")
    override val name: String = "ds_mskor_rtn_b32"
  }

  final case class DsMskorRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_mskor_rtn_b64")
    override val name: String = "ds_mskor_rtn_b64"
  }

  final case class DsNop(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_nop")
    override val name: String = "ds_nop"
  }

  final case class DsOrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_or_b32")
    override val name: String = "ds_or_b32"
  }

  final case class DsOrB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_or_b64")
    override val name: String = "ds_or_b64"
  }

  final case class DsOrRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_or_rtn_b32")
    override val name: String = "ds_or_rtn_b32"
  }

  final case class DsOrRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_or_rtn_b64")
    override val name: String = "ds_or_rtn_b64"
  }

  final case class DsOrSrc2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_or_src2_b32")
    override val name: String = "ds_or_src2_b32"
  }

  final case class DsOrSrc2B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_or_src2_b64")
    override val name: String = "ds_or_src2_b64"
  }

  final case class DsOrderedCount(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_ordered_count")
    override val name: String = "ds_ordered_count"
  }

  final case class DsParamLoad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_param_load")
    override val name: String = "ds_param_load"
  }

  final case class DsPermuteB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_permute_b32")
    override val name: String = "ds_permute_b32"
  }

  final case class DsPkAddBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_pk_add_bf16")
    override val name: String = "ds_pk_add_bf16"
  }

  final case class DsPkAddF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_pk_add_f16")
    override val name: String = "ds_pk_add_f16"
  }

  final case class DsPkAddRtnBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_pk_add_rtn_bf16")
    override val name: String = "ds_pk_add_rtn_bf16"
  }

  final case class DsPkAddRtnF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_pk_add_rtn_f16")
    override val name: String = "ds_pk_add_rtn_f16"
  }

  final case class DsRead2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read2_b32")
    override val name: String = "ds_read2_b32"
  }

  final case class DsRead2B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read2_b64")
    override val name: String = "ds_read2_b64"
  }

  final case class DsRead2st64B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read2st64_b32")
    override val name: String = "ds_read2st64_b32"
  }

  final case class DsRead2st64B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read2st64_b64")
    override val name: String = "ds_read2st64_b64"
  }

  final case class DsReadAddtidB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_addtid_b32")
    override val name: String = "ds_read_addtid_b32"
  }

  final case class DsReadB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_b128")
    override val name: String = "ds_read_b128"
  }

  final case class DsReadB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_b32")
    override val name: String = "ds_read_b32"
  }

  final case class DsReadB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_b64")
    override val name: String = "ds_read_b64"
  }

  final case class DsReadB64TrB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_b64_tr_b16")
    override val name: String = "ds_read_b64_tr_b16"
  }

  final case class DsReadB64TrB4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_b64_tr_b4")
    override val name: String = "ds_read_b64_tr_b4"
  }

  final case class DsReadB64TrB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_b64_tr_b8")
    override val name: String = "ds_read_b64_tr_b8"
  }

  final case class DsReadB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_b96")
    override val name: String = "ds_read_b96"
  }

  final case class DsReadB96TrB6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_b96_tr_b6")
    override val name: String = "ds_read_b96_tr_b6"
  }

  final case class DsReadI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_i16")
    override val name: String = "ds_read_i16"
  }

  final case class DsReadI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_i8")
    override val name: String = "ds_read_i8"
  }

  final case class DsReadI8D16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_i8_d16")
    override val name: String = "ds_read_i8_d16"
  }

  final case class DsReadI8D16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_i8_d16_hi")
    override val name: String = "ds_read_i8_d16_hi"
  }

  final case class DsReadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_u16")
    override val name: String = "ds_read_u16"
  }

  final case class DsReadU16D16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_u16_d16")
    override val name: String = "ds_read_u16_d16"
  }

  final case class DsReadU16D16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_u16_d16_hi")
    override val name: String = "ds_read_u16_d16_hi"
  }

  final case class DsReadU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_u8")
    override val name: String = "ds_read_u8"
  }

  final case class DsReadU8D16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_u8_d16")
    override val name: String = "ds_read_u8_d16"
  }

  final case class DsReadU8D16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_read_u8_d16_hi")
    override val name: String = "ds_read_u8_d16_hi"
  }

  final case class DsRsubRtnU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_rsub_rtn_u32")
    override val name: String = "ds_rsub_rtn_u32"
  }

  final case class DsRsubRtnU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_rsub_rtn_u64")
    override val name: String = "ds_rsub_rtn_u64"
  }

  final case class DsRsubSrc2U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_rsub_src2_u32")
    override val name: String = "ds_rsub_src2_u32"
  }

  final case class DsRsubSrc2U64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_rsub_src2_u64")
    override val name: String = "ds_rsub_src2_u64"
  }

  final case class DsRsubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_rsub_u32")
    override val name: String = "ds_rsub_u32"
  }

  final case class DsRsubU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_rsub_u64")
    override val name: String = "ds_rsub_u64"
  }

  final case class DsStore2addrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_2addr_b32")
    override val name: String = "ds_store_2addr_b32"
  }

  final case class DsStore2addrB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_2addr_b64")
    override val name: String = "ds_store_2addr_b64"
  }

  final case class DsStore2addrStride64B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_2addr_stride64_b32")
    override val name: String = "ds_store_2addr_stride64_b32"
  }

  final case class DsStore2addrStride64B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_2addr_stride64_b64")
    override val name: String = "ds_store_2addr_stride64_b64"
  }

  final case class DsStoreAddtidB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_addtid_b32")
    override val name: String = "ds_store_addtid_b32"
  }

  final case class DsStoreB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_b128")
    override val name: String = "ds_store_b128"
  }

  final case class DsStoreB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_b16")
    override val name: String = "ds_store_b16"
  }

  final case class DsStoreB16D16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_b16_d16_hi")
    override val name: String = "ds_store_b16_d16_hi"
  }

  final case class DsStoreB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_b32")
    override val name: String = "ds_store_b32"
  }

  final case class DsStoreB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_b64")
    override val name: String = "ds_store_b64"
  }

  final case class DsStoreB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_b8")
    override val name: String = "ds_store_b8"
  }

  final case class DsStoreB8D16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_b8_d16_hi")
    override val name: String = "ds_store_b8_d16_hi"
  }

  final case class DsStoreB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_store_b96")
    override val name: String = "ds_store_b96"
  }

  final case class DsStorexchg2addrRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_storexchg_2addr_rtn_b32")
    override val name: String = "ds_storexchg_2addr_rtn_b32"
  }

  final case class DsStorexchg2addrRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_storexchg_2addr_rtn_b64")
    override val name: String = "ds_storexchg_2addr_rtn_b64"
  }

  final case class DsStorexchg2addrStride64RtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_storexchg_2addr_stride64_rtn_b32")
    override val name: String = "ds_storexchg_2addr_stride64_rtn_b32"
  }

  final case class DsStorexchg2addrStride64RtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_storexchg_2addr_stride64_rtn_b64")
    override val name: String = "ds_storexchg_2addr_stride64_rtn_b64"
  }

  final case class DsStorexchgRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_storexchg_rtn_b32")
    override val name: String = "ds_storexchg_rtn_b32"
  }

  final case class DsStorexchgRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_storexchg_rtn_b64")
    override val name: String = "ds_storexchg_rtn_b64"
  }

  final case class DsSubClampRtnU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_sub_clamp_rtn_u32")
    override val name: String = "ds_sub_clamp_rtn_u32"
  }

  final case class DsSubClampU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_sub_clamp_u32")
    override val name: String = "ds_sub_clamp_u32"
  }

  final case class DsSubGsRegRtn(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_sub_gs_reg_rtn")
    override val name: String = "ds_sub_gs_reg_rtn"
  }

  final case class DsSubRtnU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_sub_rtn_u32")
    override val name: String = "ds_sub_rtn_u32"
  }

  final case class DsSubRtnU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_sub_rtn_u64")
    override val name: String = "ds_sub_rtn_u64"
  }

  final case class DsSubSrc2U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_sub_src2_u32")
    override val name: String = "ds_sub_src2_u32"
  }

  final case class DsSubSrc2U64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_sub_src2_u64")
    override val name: String = "ds_sub_src2_u64"
  }

  final case class DsSubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_sub_u32")
    override val name: String = "ds_sub_u32"
  }

  final case class DsSubU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_sub_u64")
    override val name: String = "ds_sub_u64"
  }

  final case class DsSwizzleB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_swizzle_b32")
    override val name: String = "ds_swizzle_b32"
  }

  final case class DsWrapRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_wrap_rtn_b32")
    override val name: String = "ds_wrap_rtn_b32"
  }

  final case class DsWrite2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write2_b32")
    override val name: String = "ds_write2_b32"
  }

  final case class DsWrite2B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write2_b64")
    override val name: String = "ds_write2_b64"
  }

  final case class DsWrite2st64B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write2st64_b32")
    override val name: String = "ds_write2st64_b32"
  }

  final case class DsWrite2st64B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write2st64_b64")
    override val name: String = "ds_write2st64_b64"
  }

  final case class DsWriteAddtidB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_addtid_b32")
    override val name: String = "ds_write_addtid_b32"
  }

  final case class DsWriteB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_b128")
    override val name: String = "ds_write_b128"
  }

  final case class DsWriteB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_b16")
    override val name: String = "ds_write_b16"
  }

  final case class DsWriteB16D16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_b16_d16_hi")
    override val name: String = "ds_write_b16_d16_hi"
  }

  final case class DsWriteB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_b32")
    override val name: String = "ds_write_b32"
  }

  final case class DsWriteB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_b64")
    override val name: String = "ds_write_b64"
  }

  final case class DsWriteB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_b8")
    override val name: String = "ds_write_b8"
  }

  final case class DsWriteB8D16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_b8_d16_hi")
    override val name: String = "ds_write_b8_d16_hi"
  }

  final case class DsWriteB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_b96")
    override val name: String = "ds_write_b96"
  }

  final case class DsWriteSrc2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_src2_b32")
    override val name: String = "ds_write_src2_b32"
  }

  final case class DsWriteSrc2B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_write_src2_b64")
    override val name: String = "ds_write_src2_b64"
  }

  final case class DsWrxchg2RtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_wrxchg2_rtn_b32")
    override val name: String = "ds_wrxchg2_rtn_b32"
  }

  final case class DsWrxchg2RtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_wrxchg2_rtn_b64")
    override val name: String = "ds_wrxchg2_rtn_b64"
  }

  final case class DsWrxchg2st64RtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_wrxchg2st64_rtn_b32")
    override val name: String = "ds_wrxchg2st64_rtn_b32"
  }

  final case class DsWrxchg2st64RtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_wrxchg2st64_rtn_b64")
    override val name: String = "ds_wrxchg2st64_rtn_b64"
  }

  final case class DsWrxchgRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_wrxchg_rtn_b32")
    override val name: String = "ds_wrxchg_rtn_b32"
  }

  final case class DsWrxchgRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_wrxchg_rtn_b64")
    override val name: String = "ds_wrxchg_rtn_b64"
  }

  final case class DsXorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_xor_b32")
    override val name: String = "ds_xor_b32"
  }

  final case class DsXorB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_xor_b64")
    override val name: String = "ds_xor_b64"
  }

  final case class DsXorRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_xor_rtn_b32")
    override val name: String = "ds_xor_rtn_b32"
  }

  final case class DsXorRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_xor_rtn_b64")
    override val name: String = "ds_xor_rtn_b64"
  }

  final case class DsXorSrc2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_xor_src2_b32")
    override val name: String = "ds_xor_src2_b32"
  }

  final case class DsXorSrc2B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("ds_xor_src2_b64")
    override val name: String = "ds_xor_src2_b64"
  }

  final case class Exp(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("exp")
    override val name: String = "exp"
  }

  final case class Export(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("export")
    override val name: String = "export"
  }

  final case class FlatAtomicAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_add")
    override val name: String = "flat_atomic_add"
  }

  final case class FlatAtomicAddF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_add_f32")
    override val name: String = "flat_atomic_add_f32"
  }

  final case class FlatAtomicAddF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_add_f64")
    override val name: String = "flat_atomic_add_f64"
  }

  final case class FlatAtomicAddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_add_u32")
    override val name: String = "flat_atomic_add_u32"
  }

  final case class FlatAtomicAddU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_add_u64")
    override val name: String = "flat_atomic_add_u64"
  }

  final case class FlatAtomicAddX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_add_x2")
    override val name: String = "flat_atomic_add_x2"
  }

  final case class FlatAtomicAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_and")
    override val name: String = "flat_atomic_and"
  }

  final case class FlatAtomicAndB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_and_b32")
    override val name: String = "flat_atomic_and_b32"
  }

  final case class FlatAtomicAndB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_and_b64")
    override val name: String = "flat_atomic_and_b64"
  }

  final case class FlatAtomicAndX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_and_x2")
    override val name: String = "flat_atomic_and_x2"
  }

  final case class FlatAtomicCmpswap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_cmpswap")
    override val name: String = "flat_atomic_cmpswap"
  }

  final case class FlatAtomicCmpswapB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_cmpswap_b32")
    override val name: String = "flat_atomic_cmpswap_b32"
  }

  final case class FlatAtomicCmpswapB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_cmpswap_b64")
    override val name: String = "flat_atomic_cmpswap_b64"
  }

  final case class FlatAtomicCmpswapF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_cmpswap_f32")
    override val name: String = "flat_atomic_cmpswap_f32"
  }

  final case class FlatAtomicCmpswapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_cmpswap_x2")
    override val name: String = "flat_atomic_cmpswap_x2"
  }

  final case class FlatAtomicCondSubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_cond_sub_u32")
    override val name: String = "flat_atomic_cond_sub_u32"
  }

  final case class FlatAtomicDec(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_dec")
    override val name: String = "flat_atomic_dec"
  }

  final case class FlatAtomicDecU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_dec_u32")
    override val name: String = "flat_atomic_dec_u32"
  }

  final case class FlatAtomicDecU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_dec_u64")
    override val name: String = "flat_atomic_dec_u64"
  }

  final case class FlatAtomicDecX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_dec_x2")
    override val name: String = "flat_atomic_dec_x2"
  }

  final case class FlatAtomicFcmpswap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_fcmpswap")
    override val name: String = "flat_atomic_fcmpswap"
  }

  final case class FlatAtomicFcmpswapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_fcmpswap_x2")
    override val name: String = "flat_atomic_fcmpswap_x2"
  }

  final case class FlatAtomicFmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_fmax")
    override val name: String = "flat_atomic_fmax"
  }

  final case class FlatAtomicFmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_fmax_x2")
    override val name: String = "flat_atomic_fmax_x2"
  }

  final case class FlatAtomicFmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_fmin")
    override val name: String = "flat_atomic_fmin"
  }

  final case class FlatAtomicFminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_fmin_x2")
    override val name: String = "flat_atomic_fmin_x2"
  }

  final case class FlatAtomicInc(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_inc")
    override val name: String = "flat_atomic_inc"
  }

  final case class FlatAtomicIncU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_inc_u32")
    override val name: String = "flat_atomic_inc_u32"
  }

  final case class FlatAtomicIncU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_inc_u64")
    override val name: String = "flat_atomic_inc_u64"
  }

  final case class FlatAtomicIncX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_inc_x2")
    override val name: String = "flat_atomic_inc_x2"
  }

  final case class FlatAtomicMaxF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_max_f32")
    override val name: String = "flat_atomic_max_f32"
  }

  final case class FlatAtomicMaxF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_max_f64")
    override val name: String = "flat_atomic_max_f64"
  }

  final case class FlatAtomicMaxI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_max_i32")
    override val name: String = "flat_atomic_max_i32"
  }

  final case class FlatAtomicMaxI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_max_i64")
    override val name: String = "flat_atomic_max_i64"
  }

  final case class FlatAtomicMaxNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_max_num_f32")
    override val name: String = "flat_atomic_max_num_f32"
  }

  final case class FlatAtomicMaxNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_max_num_f64")
    override val name: String = "flat_atomic_max_num_f64"
  }

  final case class FlatAtomicMaxU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_max_u32")
    override val name: String = "flat_atomic_max_u32"
  }

  final case class FlatAtomicMaxU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_max_u64")
    override val name: String = "flat_atomic_max_u64"
  }

  final case class FlatAtomicMinF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_min_f32")
    override val name: String = "flat_atomic_min_f32"
  }

  final case class FlatAtomicMinF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_min_f64")
    override val name: String = "flat_atomic_min_f64"
  }

  final case class FlatAtomicMinI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_min_i32")
    override val name: String = "flat_atomic_min_i32"
  }

  final case class FlatAtomicMinI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_min_i64")
    override val name: String = "flat_atomic_min_i64"
  }

  final case class FlatAtomicMinNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_min_num_f32")
    override val name: String = "flat_atomic_min_num_f32"
  }

  final case class FlatAtomicMinNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_min_num_f64")
    override val name: String = "flat_atomic_min_num_f64"
  }

  final case class FlatAtomicMinU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_min_u32")
    override val name: String = "flat_atomic_min_u32"
  }

  final case class FlatAtomicMinU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_min_u64")
    override val name: String = "flat_atomic_min_u64"
  }

  final case class FlatAtomicOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_or")
    override val name: String = "flat_atomic_or"
  }

  final case class FlatAtomicOrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_or_b32")
    override val name: String = "flat_atomic_or_b32"
  }

  final case class FlatAtomicOrB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_or_b64")
    override val name: String = "flat_atomic_or_b64"
  }

  final case class FlatAtomicOrX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_or_x2")
    override val name: String = "flat_atomic_or_x2"
  }

  final case class FlatAtomicPkAddBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_pk_add_bf16")
    override val name: String = "flat_atomic_pk_add_bf16"
  }

  final case class FlatAtomicPkAddF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_pk_add_f16")
    override val name: String = "flat_atomic_pk_add_f16"
  }

  final case class FlatAtomicSmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_smax")
    override val name: String = "flat_atomic_smax"
  }

  final case class FlatAtomicSmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_smax_x2")
    override val name: String = "flat_atomic_smax_x2"
  }

  final case class FlatAtomicSmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_smin")
    override val name: String = "flat_atomic_smin"
  }

  final case class FlatAtomicSminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_smin_x2")
    override val name: String = "flat_atomic_smin_x2"
  }

  final case class FlatAtomicSub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_sub")
    override val name: String = "flat_atomic_sub"
  }

  final case class FlatAtomicSubClampU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_sub_clamp_u32")
    override val name: String = "flat_atomic_sub_clamp_u32"
  }

  final case class FlatAtomicSubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_sub_u32")
    override val name: String = "flat_atomic_sub_u32"
  }

  final case class FlatAtomicSubU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_sub_u64")
    override val name: String = "flat_atomic_sub_u64"
  }

  final case class FlatAtomicSubX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_sub_x2")
    override val name: String = "flat_atomic_sub_x2"
  }

  final case class FlatAtomicSwap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_swap")
    override val name: String = "flat_atomic_swap"
  }

  final case class FlatAtomicSwapB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_swap_b32")
    override val name: String = "flat_atomic_swap_b32"
  }

  final case class FlatAtomicSwapB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_swap_b64")
    override val name: String = "flat_atomic_swap_b64"
  }

  final case class FlatAtomicSwapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_swap_x2")
    override val name: String = "flat_atomic_swap_x2"
  }

  final case class FlatAtomicUmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_umax")
    override val name: String = "flat_atomic_umax"
  }

  final case class FlatAtomicUmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_umax_x2")
    override val name: String = "flat_atomic_umax_x2"
  }

  final case class FlatAtomicUmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_umin")
    override val name: String = "flat_atomic_umin"
  }

  final case class FlatAtomicUminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_umin_x2")
    override val name: String = "flat_atomic_umin_x2"
  }

  final case class FlatAtomicXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_xor")
    override val name: String = "flat_atomic_xor"
  }

  final case class FlatAtomicXorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_xor_b32")
    override val name: String = "flat_atomic_xor_b32"
  }

  final case class FlatAtomicXorB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_xor_b64")
    override val name: String = "flat_atomic_xor_b64"
  }

  final case class FlatAtomicXorX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_atomic_xor_x2")
    override val name: String = "flat_atomic_xor_x2"
  }

  final case class FlatLoadB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_b128")
    override val name: String = "flat_load_b128"
  }

  final case class FlatLoadB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_b32")
    override val name: String = "flat_load_b32"
  }

  final case class FlatLoadB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_b64")
    override val name: String = "flat_load_b64"
  }

  final case class FlatLoadB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_b96")
    override val name: String = "flat_load_b96"
  }

  final case class FlatLoadD16B16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_d16_b16")
    override val name: String = "flat_load_d16_b16"
  }

  final case class FlatLoadD16HiB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_d16_hi_b16")
    override val name: String = "flat_load_d16_hi_b16"
  }

  final case class FlatLoadD16HiI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_d16_hi_i8")
    override val name: String = "flat_load_d16_hi_i8"
  }

  final case class FlatLoadD16HiU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_d16_hi_u8")
    override val name: String = "flat_load_d16_hi_u8"
  }

  final case class FlatLoadD16I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_d16_i8")
    override val name: String = "flat_load_d16_i8"
  }

  final case class FlatLoadD16U8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_d16_u8")
    override val name: String = "flat_load_d16_u8"
  }

  final case class FlatLoadDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_dword")
    override val name: String = "flat_load_dword"
  }

  final case class FlatLoadDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_dwordx2")
    override val name: String = "flat_load_dwordx2"
  }

  final case class FlatLoadDwordx3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_dwordx3")
    override val name: String = "flat_load_dwordx3"
  }

  final case class FlatLoadDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_dwordx4")
    override val name: String = "flat_load_dwordx4"
  }

  final case class FlatLoadI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_i16")
    override val name: String = "flat_load_i16"
  }

  final case class FlatLoadI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_i8")
    override val name: String = "flat_load_i8"
  }

  final case class FlatLoadMonitorB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_monitor_b128")
    override val name: String = "flat_load_monitor_b128"
  }

  final case class FlatLoadMonitorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_monitor_b32")
    override val name: String = "flat_load_monitor_b32"
  }

  final case class FlatLoadMonitorB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_monitor_b64")
    override val name: String = "flat_load_monitor_b64"
  }

  final case class FlatLoadSbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_sbyte")
    override val name: String = "flat_load_sbyte"
  }

  final case class FlatLoadSbyteD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_sbyte_d16")
    override val name: String = "flat_load_sbyte_d16"
  }

  final case class FlatLoadSbyteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_sbyte_d16_hi")
    override val name: String = "flat_load_sbyte_d16_hi"
  }

  final case class FlatLoadShortD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_short_d16")
    override val name: String = "flat_load_short_d16"
  }

  final case class FlatLoadShortD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_short_d16_hi")
    override val name: String = "flat_load_short_d16_hi"
  }

  final case class FlatLoadSshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_sshort")
    override val name: String = "flat_load_sshort"
  }

  final case class FlatLoadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_u16")
    override val name: String = "flat_load_u16"
  }

  final case class FlatLoadU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_u8")
    override val name: String = "flat_load_u8"
  }

  final case class FlatLoadUbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_ubyte")
    override val name: String = "flat_load_ubyte"
  }

  final case class FlatLoadUbyteD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_ubyte_d16")
    override val name: String = "flat_load_ubyte_d16"
  }

  final case class FlatLoadUbyteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_ubyte_d16_hi")
    override val name: String = "flat_load_ubyte_d16_hi"
  }

  final case class FlatLoadUshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_load_ushort")
    override val name: String = "flat_load_ushort"
  }

  final case class FlatPrefetchB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_prefetch_b8")
    override val name: String = "flat_prefetch_b8"
  }

  final case class FlatStoreB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_b128")
    override val name: String = "flat_store_b128"
  }

  final case class FlatStoreB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_b16")
    override val name: String = "flat_store_b16"
  }

  final case class FlatStoreB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_b32")
    override val name: String = "flat_store_b32"
  }

  final case class FlatStoreB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_b64")
    override val name: String = "flat_store_b64"
  }

  final case class FlatStoreB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_b8")
    override val name: String = "flat_store_b8"
  }

  final case class FlatStoreB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_b96")
    override val name: String = "flat_store_b96"
  }

  final case class FlatStoreByte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_byte")
    override val name: String = "flat_store_byte"
  }

  final case class FlatStoreByteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_byte_d16_hi")
    override val name: String = "flat_store_byte_d16_hi"
  }

  final case class FlatStoreD16HiB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_d16_hi_b16")
    override val name: String = "flat_store_d16_hi_b16"
  }

  final case class FlatStoreD16HiB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_d16_hi_b8")
    override val name: String = "flat_store_d16_hi_b8"
  }

  final case class FlatStoreDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_dword")
    override val name: String = "flat_store_dword"
  }

  final case class FlatStoreDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_dwordx2")
    override val name: String = "flat_store_dwordx2"
  }

  final case class FlatStoreDwordx3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_dwordx3")
    override val name: String = "flat_store_dwordx3"
  }

  final case class FlatStoreDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_dwordx4")
    override val name: String = "flat_store_dwordx4"
  }

  final case class FlatStoreShort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_short")
    override val name: String = "flat_store_short"
  }

  final case class FlatStoreShortD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("flat_store_short_d16_hi")
    override val name: String = "flat_store_short_d16_hi"
  }

  final case class GlobalAtomicAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_add")
    override val name: String = "global_atomic_add"
  }

  final case class GlobalAtomicAddF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_add_f32")
    override val name: String = "global_atomic_add_f32"
  }

  final case class GlobalAtomicAddF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_add_f64")
    override val name: String = "global_atomic_add_f64"
  }

  final case class GlobalAtomicAddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_add_u32")
    override val name: String = "global_atomic_add_u32"
  }

  final case class GlobalAtomicAddU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_add_u64")
    override val name: String = "global_atomic_add_u64"
  }

  final case class GlobalAtomicAddX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_add_x2")
    override val name: String = "global_atomic_add_x2"
  }

  final case class GlobalAtomicAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_and")
    override val name: String = "global_atomic_and"
  }

  final case class GlobalAtomicAndB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_and_b32")
    override val name: String = "global_atomic_and_b32"
  }

  final case class GlobalAtomicAndB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_and_b64")
    override val name: String = "global_atomic_and_b64"
  }

  final case class GlobalAtomicAndX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_and_x2")
    override val name: String = "global_atomic_and_x2"
  }

  final case class GlobalAtomicCmpswap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_cmpswap")
    override val name: String = "global_atomic_cmpswap"
  }

  final case class GlobalAtomicCmpswapB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_cmpswap_b32")
    override val name: String = "global_atomic_cmpswap_b32"
  }

  final case class GlobalAtomicCmpswapB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_cmpswap_b64")
    override val name: String = "global_atomic_cmpswap_b64"
  }

  final case class GlobalAtomicCmpswapF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_cmpswap_f32")
    override val name: String = "global_atomic_cmpswap_f32"
  }

  final case class GlobalAtomicCmpswapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_cmpswap_x2")
    override val name: String = "global_atomic_cmpswap_x2"
  }

  final case class GlobalAtomicCondSubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_cond_sub_u32")
    override val name: String = "global_atomic_cond_sub_u32"
  }

  final case class GlobalAtomicCsub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_csub")
    override val name: String = "global_atomic_csub"
  }

  final case class GlobalAtomicCsubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_csub_u32")
    override val name: String = "global_atomic_csub_u32"
  }

  final case class GlobalAtomicDec(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_dec")
    override val name: String = "global_atomic_dec"
  }

  final case class GlobalAtomicDecU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_dec_u32")
    override val name: String = "global_atomic_dec_u32"
  }

  final case class GlobalAtomicDecU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_dec_u64")
    override val name: String = "global_atomic_dec_u64"
  }

  final case class GlobalAtomicDecX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_dec_x2")
    override val name: String = "global_atomic_dec_x2"
  }

  final case class GlobalAtomicFcmpswap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_fcmpswap")
    override val name: String = "global_atomic_fcmpswap"
  }

  final case class GlobalAtomicFcmpswapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_fcmpswap_x2")
    override val name: String = "global_atomic_fcmpswap_x2"
  }

  final case class GlobalAtomicFmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_fmax")
    override val name: String = "global_atomic_fmax"
  }

  final case class GlobalAtomicFmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_fmax_x2")
    override val name: String = "global_atomic_fmax_x2"
  }

  final case class GlobalAtomicFmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_fmin")
    override val name: String = "global_atomic_fmin"
  }

  final case class GlobalAtomicFminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_fmin_x2")
    override val name: String = "global_atomic_fmin_x2"
  }

  final case class GlobalAtomicInc(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_inc")
    override val name: String = "global_atomic_inc"
  }

  final case class GlobalAtomicIncU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_inc_u32")
    override val name: String = "global_atomic_inc_u32"
  }

  final case class GlobalAtomicIncU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_inc_u64")
    override val name: String = "global_atomic_inc_u64"
  }

  final case class GlobalAtomicIncX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_inc_x2")
    override val name: String = "global_atomic_inc_x2"
  }

  final case class GlobalAtomicMaxF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_max_f32")
    override val name: String = "global_atomic_max_f32"
  }

  final case class GlobalAtomicMaxF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_max_f64")
    override val name: String = "global_atomic_max_f64"
  }

  final case class GlobalAtomicMaxI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_max_i32")
    override val name: String = "global_atomic_max_i32"
  }

  final case class GlobalAtomicMaxI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_max_i64")
    override val name: String = "global_atomic_max_i64"
  }

  final case class GlobalAtomicMaxNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_max_num_f32")
    override val name: String = "global_atomic_max_num_f32"
  }

  final case class GlobalAtomicMaxNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_max_num_f64")
    override val name: String = "global_atomic_max_num_f64"
  }

  final case class GlobalAtomicMaxU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_max_u32")
    override val name: String = "global_atomic_max_u32"
  }

  final case class GlobalAtomicMaxU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_max_u64")
    override val name: String = "global_atomic_max_u64"
  }

  final case class GlobalAtomicMinF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_min_f32")
    override val name: String = "global_atomic_min_f32"
  }

  final case class GlobalAtomicMinF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_min_f64")
    override val name: String = "global_atomic_min_f64"
  }

  final case class GlobalAtomicMinI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_min_i32")
    override val name: String = "global_atomic_min_i32"
  }

  final case class GlobalAtomicMinI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_min_i64")
    override val name: String = "global_atomic_min_i64"
  }

  final case class GlobalAtomicMinNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_min_num_f32")
    override val name: String = "global_atomic_min_num_f32"
  }

  final case class GlobalAtomicMinNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_min_num_f64")
    override val name: String = "global_atomic_min_num_f64"
  }

  final case class GlobalAtomicMinU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_min_u32")
    override val name: String = "global_atomic_min_u32"
  }

  final case class GlobalAtomicMinU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_min_u64")
    override val name: String = "global_atomic_min_u64"
  }

  final case class GlobalAtomicOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_or")
    override val name: String = "global_atomic_or"
  }

  final case class GlobalAtomicOrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_or_b32")
    override val name: String = "global_atomic_or_b32"
  }

  final case class GlobalAtomicOrB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_or_b64")
    override val name: String = "global_atomic_or_b64"
  }

  final case class GlobalAtomicOrX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_or_x2")
    override val name: String = "global_atomic_or_x2"
  }

  final case class GlobalAtomicOrderedAddB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_ordered_add_b64")
    override val name: String = "global_atomic_ordered_add_b64"
  }

  final case class GlobalAtomicPkAddBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_pk_add_bf16")
    override val name: String = "global_atomic_pk_add_bf16"
  }

  final case class GlobalAtomicPkAddF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_pk_add_f16")
    override val name: String = "global_atomic_pk_add_f16"
  }

  final case class GlobalAtomicSmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_smax")
    override val name: String = "global_atomic_smax"
  }

  final case class GlobalAtomicSmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_smax_x2")
    override val name: String = "global_atomic_smax_x2"
  }

  final case class GlobalAtomicSmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_smin")
    override val name: String = "global_atomic_smin"
  }

  final case class GlobalAtomicSminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_smin_x2")
    override val name: String = "global_atomic_smin_x2"
  }

  final case class GlobalAtomicSub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_sub")
    override val name: String = "global_atomic_sub"
  }

  final case class GlobalAtomicSubClampU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_sub_clamp_u32")
    override val name: String = "global_atomic_sub_clamp_u32"
  }

  final case class GlobalAtomicSubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_sub_u32")
    override val name: String = "global_atomic_sub_u32"
  }

  final case class GlobalAtomicSubU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_sub_u64")
    override val name: String = "global_atomic_sub_u64"
  }

  final case class GlobalAtomicSubX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_sub_x2")
    override val name: String = "global_atomic_sub_x2"
  }

  final case class GlobalAtomicSwap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_swap")
    override val name: String = "global_atomic_swap"
  }

  final case class GlobalAtomicSwapB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_swap_b32")
    override val name: String = "global_atomic_swap_b32"
  }

  final case class GlobalAtomicSwapB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_swap_b64")
    override val name: String = "global_atomic_swap_b64"
  }

  final case class GlobalAtomicSwapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_swap_x2")
    override val name: String = "global_atomic_swap_x2"
  }

  final case class GlobalAtomicUmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_umax")
    override val name: String = "global_atomic_umax"
  }

  final case class GlobalAtomicUmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_umax_x2")
    override val name: String = "global_atomic_umax_x2"
  }

  final case class GlobalAtomicUmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_umin")
    override val name: String = "global_atomic_umin"
  }

  final case class GlobalAtomicUminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_umin_x2")
    override val name: String = "global_atomic_umin_x2"
  }

  final case class GlobalAtomicXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_xor")
    override val name: String = "global_atomic_xor"
  }

  final case class GlobalAtomicXorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_xor_b32")
    override val name: String = "global_atomic_xor_b32"
  }

  final case class GlobalAtomicXorB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_xor_b64")
    override val name: String = "global_atomic_xor_b64"
  }

  final case class GlobalAtomicXorX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_atomic_xor_x2")
    override val name: String = "global_atomic_xor_x2"
  }

  final case class GlobalInv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_inv")
    override val name: String = "global_inv"
  }

  final case class GlobalLoadAddtidB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_addtid_b32")
    override val name: String = "global_load_addtid_b32"
  }

  final case class GlobalLoadAsyncToLdsB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_async_to_lds_b128")
    override val name: String = "global_load_async_to_lds_b128"
  }

  final case class GlobalLoadAsyncToLdsB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_async_to_lds_b32")
    override val name: String = "global_load_async_to_lds_b32"
  }

  final case class GlobalLoadAsyncToLdsB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_async_to_lds_b64")
    override val name: String = "global_load_async_to_lds_b64"
  }

  final case class GlobalLoadAsyncToLdsB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_async_to_lds_b8")
    override val name: String = "global_load_async_to_lds_b8"
  }

  final case class GlobalLoadB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_b128")
    override val name: String = "global_load_b128"
  }

  final case class GlobalLoadB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_b32")
    override val name: String = "global_load_b32"
  }

  final case class GlobalLoadB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_b64")
    override val name: String = "global_load_b64"
  }

  final case class GlobalLoadB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_b96")
    override val name: String = "global_load_b96"
  }

  final case class GlobalLoadBlock(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_block")
    override val name: String = "global_load_block"
  }

  final case class GlobalLoadD16B16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_d16_b16")
    override val name: String = "global_load_d16_b16"
  }

  final case class GlobalLoadD16HiB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_d16_hi_b16")
    override val name: String = "global_load_d16_hi_b16"
  }

  final case class GlobalLoadD16HiI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_d16_hi_i8")
    override val name: String = "global_load_d16_hi_i8"
  }

  final case class GlobalLoadD16HiU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_d16_hi_u8")
    override val name: String = "global_load_d16_hi_u8"
  }

  final case class GlobalLoadD16I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_d16_i8")
    override val name: String = "global_load_d16_i8"
  }

  final case class GlobalLoadD16U8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_d16_u8")
    override val name: String = "global_load_d16_u8"
  }

  final case class GlobalLoadDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_dword")
    override val name: String = "global_load_dword"
  }

  final case class GlobalLoadDwordAddtid(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_dword_addtid")
    override val name: String = "global_load_dword_addtid"
  }

  final case class GlobalLoadDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_dwordx2")
    override val name: String = "global_load_dwordx2"
  }

  final case class GlobalLoadDwordx3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_dwordx3")
    override val name: String = "global_load_dwordx3"
  }

  final case class GlobalLoadDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_dwordx4")
    override val name: String = "global_load_dwordx4"
  }

  final case class GlobalLoadI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_i16")
    override val name: String = "global_load_i16"
  }

  final case class GlobalLoadI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_i8")
    override val name: String = "global_load_i8"
  }

  final case class GlobalLoadLdsDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_lds_dword")
    override val name: String = "global_load_lds_dword"
  }

  final case class GlobalLoadLdsDwordx3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_lds_dwordx3")
    override val name: String = "global_load_lds_dwordx3"
  }

  final case class GlobalLoadLdsDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_lds_dwordx4")
    override val name: String = "global_load_lds_dwordx4"
  }

  final case class GlobalLoadLdsSbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_lds_sbyte")
    override val name: String = "global_load_lds_sbyte"
  }

  final case class GlobalLoadLdsSshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_lds_sshort")
    override val name: String = "global_load_lds_sshort"
  }

  final case class GlobalLoadLdsUbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_lds_ubyte")
    override val name: String = "global_load_lds_ubyte"
  }

  final case class GlobalLoadLdsUshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_lds_ushort")
    override val name: String = "global_load_lds_ushort"
  }

  final case class GlobalLoadMonitorB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_monitor_b128")
    override val name: String = "global_load_monitor_b128"
  }

  final case class GlobalLoadMonitorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_monitor_b32")
    override val name: String = "global_load_monitor_b32"
  }

  final case class GlobalLoadMonitorB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_monitor_b64")
    override val name: String = "global_load_monitor_b64"
  }

  final case class GlobalLoadSbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_sbyte")
    override val name: String = "global_load_sbyte"
  }

  final case class GlobalLoadSbyteD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_sbyte_d16")
    override val name: String = "global_load_sbyte_d16"
  }

  final case class GlobalLoadSbyteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_sbyte_d16_hi")
    override val name: String = "global_load_sbyte_d16_hi"
  }

  final case class GlobalLoadShortD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_short_d16")
    override val name: String = "global_load_short_d16"
  }

  final case class GlobalLoadShortD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_short_d16_hi")
    override val name: String = "global_load_short_d16_hi"
  }

  final case class GlobalLoadSshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_sshort")
    override val name: String = "global_load_sshort"
  }

  final case class GlobalLoadTr16B128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_tr16_b128")
    override val name: String = "global_load_tr16_b128"
  }

  final case class GlobalLoadTr4B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_tr4_b64")
    override val name: String = "global_load_tr4_b64"
  }

  final case class GlobalLoadTr6B96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_tr6_b96")
    override val name: String = "global_load_tr6_b96"
  }

  final case class GlobalLoadTr8B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_tr8_b64")
    override val name: String = "global_load_tr8_b64"
  }

  final case class GlobalLoadTrB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_tr_b128")
    override val name: String = "global_load_tr_b128"
  }

  final case class GlobalLoadTrB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_tr_b64")
    override val name: String = "global_load_tr_b64"
  }

  final case class GlobalLoadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_u16")
    override val name: String = "global_load_u16"
  }

  final case class GlobalLoadU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_u8")
    override val name: String = "global_load_u8"
  }

  final case class GlobalLoadUbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_ubyte")
    override val name: String = "global_load_ubyte"
  }

  final case class GlobalLoadUbyteD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_ubyte_d16")
    override val name: String = "global_load_ubyte_d16"
  }

  final case class GlobalLoadUbyteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_ubyte_d16_hi")
    override val name: String = "global_load_ubyte_d16_hi"
  }

  final case class GlobalLoadUshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_load_ushort")
    override val name: String = "global_load_ushort"
  }

  final case class GlobalPrefetchB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_prefetch_b8")
    override val name: String = "global_prefetch_b8"
  }

  final case class GlobalStoreAddtidB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_addtid_b32")
    override val name: String = "global_store_addtid_b32"
  }

  final case class GlobalStoreAsyncFromLdsB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_async_from_lds_b128")
    override val name: String = "global_store_async_from_lds_b128"
  }

  final case class GlobalStoreAsyncFromLdsB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_async_from_lds_b32")
    override val name: String = "global_store_async_from_lds_b32"
  }

  final case class GlobalStoreAsyncFromLdsB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_async_from_lds_b64")
    override val name: String = "global_store_async_from_lds_b64"
  }

  final case class GlobalStoreAsyncFromLdsB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_async_from_lds_b8")
    override val name: String = "global_store_async_from_lds_b8"
  }

  final case class GlobalStoreB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_b128")
    override val name: String = "global_store_b128"
  }

  final case class GlobalStoreB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_b16")
    override val name: String = "global_store_b16"
  }

  final case class GlobalStoreB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_b32")
    override val name: String = "global_store_b32"
  }

  final case class GlobalStoreB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_b64")
    override val name: String = "global_store_b64"
  }

  final case class GlobalStoreB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_b8")
    override val name: String = "global_store_b8"
  }

  final case class GlobalStoreB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_b96")
    override val name: String = "global_store_b96"
  }

  final case class GlobalStoreBlock(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_block")
    override val name: String = "global_store_block"
  }

  final case class GlobalStoreByte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_byte")
    override val name: String = "global_store_byte"
  }

  final case class GlobalStoreByteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_byte_d16_hi")
    override val name: String = "global_store_byte_d16_hi"
  }

  final case class GlobalStoreD16HiB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_d16_hi_b16")
    override val name: String = "global_store_d16_hi_b16"
  }

  final case class GlobalStoreD16HiB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_d16_hi_b8")
    override val name: String = "global_store_d16_hi_b8"
  }

  final case class GlobalStoreDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_dword")
    override val name: String = "global_store_dword"
  }

  final case class GlobalStoreDwordAddtid(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_dword_addtid")
    override val name: String = "global_store_dword_addtid"
  }

  final case class GlobalStoreDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_dwordx2")
    override val name: String = "global_store_dwordx2"
  }

  final case class GlobalStoreDwordx3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_dwordx3")
    override val name: String = "global_store_dwordx3"
  }

  final case class GlobalStoreDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_dwordx4")
    override val name: String = "global_store_dwordx4"
  }

  final case class GlobalStoreShort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_short")
    override val name: String = "global_store_short"
  }

  final case class GlobalStoreShortD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_store_short_d16_hi")
    override val name: String = "global_store_short_d16_hi"
  }

  final case class GlobalWb(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_wb")
    override val name: String = "global_wb"
  }

  final case class GlobalWbinv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("global_wbinv")
    override val name: String = "global_wbinv"
  }

  final case class ImageAtomicAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_add")
    override val name: String = "image_atomic_add"
  }

  final case class ImageAtomicAddFlt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_add_flt")
    override val name: String = "image_atomic_add_flt"
  }

  final case class ImageAtomicAddUint(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_add_uint")
    override val name: String = "image_atomic_add_uint"
  }

  final case class ImageAtomicAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_and")
    override val name: String = "image_atomic_and"
  }

  final case class ImageAtomicCmpswap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_cmpswap")
    override val name: String = "image_atomic_cmpswap"
  }

  final case class ImageAtomicDec(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_dec")
    override val name: String = "image_atomic_dec"
  }

  final case class ImageAtomicDecUint(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_dec_uint")
    override val name: String = "image_atomic_dec_uint"
  }

  final case class ImageAtomicFcmpswap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_fcmpswap")
    override val name: String = "image_atomic_fcmpswap"
  }

  final case class ImageAtomicFmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_fmax")
    override val name: String = "image_atomic_fmax"
  }

  final case class ImageAtomicFmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_fmin")
    override val name: String = "image_atomic_fmin"
  }

  final case class ImageAtomicInc(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_inc")
    override val name: String = "image_atomic_inc"
  }

  final case class ImageAtomicIncUint(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_inc_uint")
    override val name: String = "image_atomic_inc_uint"
  }

  final case class ImageAtomicMaxFlt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_max_flt")
    override val name: String = "image_atomic_max_flt"
  }

  final case class ImageAtomicMaxInt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_max_int")
    override val name: String = "image_atomic_max_int"
  }

  final case class ImageAtomicMaxUint(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_max_uint")
    override val name: String = "image_atomic_max_uint"
  }

  final case class ImageAtomicMinFlt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_min_flt")
    override val name: String = "image_atomic_min_flt"
  }

  final case class ImageAtomicMinInt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_min_int")
    override val name: String = "image_atomic_min_int"
  }

  final case class ImageAtomicMinUint(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_min_uint")
    override val name: String = "image_atomic_min_uint"
  }

  final case class ImageAtomicOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_or")
    override val name: String = "image_atomic_or"
  }

  final case class ImageAtomicPkAddBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_pk_add_bf16")
    override val name: String = "image_atomic_pk_add_bf16"
  }

  final case class ImageAtomicPkAddF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_pk_add_f16")
    override val name: String = "image_atomic_pk_add_f16"
  }

  final case class ImageAtomicRsub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_rsub")
    override val name: String = "image_atomic_rsub"
  }

  final case class ImageAtomicSmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_smax")
    override val name: String = "image_atomic_smax"
  }

  final case class ImageAtomicSmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_smin")
    override val name: String = "image_atomic_smin"
  }

  final case class ImageAtomicSub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_sub")
    override val name: String = "image_atomic_sub"
  }

  final case class ImageAtomicSubUint(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_sub_uint")
    override val name: String = "image_atomic_sub_uint"
  }

  final case class ImageAtomicSwap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_swap")
    override val name: String = "image_atomic_swap"
  }

  final case class ImageAtomicUmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_umax")
    override val name: String = "image_atomic_umax"
  }

  final case class ImageAtomicUmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_umin")
    override val name: String = "image_atomic_umin"
  }

  final case class ImageAtomicXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_atomic_xor")
    override val name: String = "image_atomic_xor"
  }

  final case class ImageBvh64IntersectRay(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_bvh64_intersect_ray")
    override val name: String = "image_bvh64_intersect_ray"
  }

  final case class ImageBvh8IntersectRay(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_bvh8_intersect_ray")
    override val name: String = "image_bvh8_intersect_ray"
  }

  final case class ImageBvhDualIntersectRay(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_bvh_dual_intersect_ray")
    override val name: String = "image_bvh_dual_intersect_ray"
  }

  final case class ImageBvhIntersectRay(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_bvh_intersect_ray")
    override val name: String = "image_bvh_intersect_ray"
  }

  final case class ImageGather4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4")
    override val name: String = "image_gather4"
  }

  final case class ImageGather4B(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_b")
    override val name: String = "image_gather4_b"
  }

  final case class ImageGather4BCl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_b_cl")
    override val name: String = "image_gather4_b_cl"
  }

  final case class ImageGather4BClO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_b_cl_o")
    override val name: String = "image_gather4_b_cl_o"
  }

  final case class ImageGather4BO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_b_o")
    override val name: String = "image_gather4_b_o"
  }

  final case class ImageGather4C(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c")
    override val name: String = "image_gather4_c"
  }

  final case class ImageGather4CB(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_b")
    override val name: String = "image_gather4_c_b"
  }

  final case class ImageGather4CBCl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_b_cl")
    override val name: String = "image_gather4_c_b_cl"
  }

  final case class ImageGather4CBClO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_b_cl_o")
    override val name: String = "image_gather4_c_b_cl_o"
  }

  final case class ImageGather4CBO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_b_o")
    override val name: String = "image_gather4_c_b_o"
  }

  final case class ImageGather4CCl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_cl")
    override val name: String = "image_gather4_c_cl"
  }

  final case class ImageGather4CClO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_cl_o")
    override val name: String = "image_gather4_c_cl_o"
  }

  final case class ImageGather4CL_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_l")
    override val name: String = "image_gather4_c_l"
  }

  final case class ImageGather4CLO_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_l_o")
    override val name: String = "image_gather4_c_l_o"
  }

  final case class ImageGather4CLz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_lz")
    override val name: String = "image_gather4_c_lz"
  }

  final case class ImageGather4CLzO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_lz_o")
    override val name: String = "image_gather4_c_lz_o"
  }

  final case class ImageGather4CO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_c_o")
    override val name: String = "image_gather4_c_o"
  }

  final case class ImageGather4Cl_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_cl")
    override val name: String = "image_gather4_cl"
  }

  final case class ImageGather4ClO_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_cl_o")
    override val name: String = "image_gather4_cl_o"
  }

  final case class ImageGather4L(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_l")
    override val name: String = "image_gather4_l"
  }

  final case class ImageGather4LO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_l_o")
    override val name: String = "image_gather4_l_o"
  }

  final case class ImageGather4Lz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_lz")
    override val name: String = "image_gather4_lz"
  }

  final case class ImageGather4LzO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_lz_o")
    override val name: String = "image_gather4_lz_o"
  }

  final case class ImageGather4O(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4_o")
    override val name: String = "image_gather4_o"
  }

  final case class ImageGather4h(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4h")
    override val name: String = "image_gather4h"
  }

  final case class ImageGather4hPck(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather4h_pck")
    override val name: String = "image_gather4h_pck"
  }

  final case class ImageGather8hPck(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_gather8h_pck")
    override val name: String = "image_gather8h_pck"
  }

  final case class ImageGetLod(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_get_lod")
    override val name: String = "image_get_lod"
  }

  final case class ImageGetResinfo(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_get_resinfo")
    override val name: String = "image_get_resinfo"
  }

  final case class ImageLoad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load")
    override val name: String = "image_load"
  }

  final case class ImageLoadBy2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_by2")
    override val name: String = "image_load_by2"
  }

  final case class ImageLoadBy4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_by4")
    override val name: String = "image_load_by4"
  }

  final case class ImageLoadMip(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_mip")
    override val name: String = "image_load_mip"
  }

  final case class ImageLoadMipBy2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_mip_by2")
    override val name: String = "image_load_mip_by2"
  }

  final case class ImageLoadMipBy4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_mip_by4")
    override val name: String = "image_load_mip_by4"
  }

  final case class ImageLoadMipPck(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_mip_pck")
    override val name: String = "image_load_mip_pck"
  }

  final case class ImageLoadMipPck2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_mip_pck2")
    override val name: String = "image_load_mip_pck2"
  }

  final case class ImageLoadMipPck4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_mip_pck4")
    override val name: String = "image_load_mip_pck4"
  }

  final case class ImageLoadMipPckSgn(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_mip_pck_sgn")
    override val name: String = "image_load_mip_pck_sgn"
  }

  final case class ImageLoadPck(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_pck")
    override val name: String = "image_load_pck"
  }

  final case class ImageLoadPck2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_pck2")
    override val name: String = "image_load_pck2"
  }

  final case class ImageLoadPck4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_pck4")
    override val name: String = "image_load_pck4"
  }

  final case class ImageLoadPckSgn(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_load_pck_sgn")
    override val name: String = "image_load_pck_sgn"
  }

  final case class ImageMsaaLoad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_msaa_load")
    override val name: String = "image_msaa_load"
  }

  final case class ImageSample(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample")
    override val name: String = "image_sample"
  }

  final case class ImageSampleB(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_b")
    override val name: String = "image_sample_b"
  }

  final case class ImageSampleBCl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_b_cl")
    override val name: String = "image_sample_b_cl"
  }

  final case class ImageSampleBClO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_b_cl_o")
    override val name: String = "image_sample_b_cl_o"
  }

  final case class ImageSampleBO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_b_o")
    override val name: String = "image_sample_b_o"
  }

  final case class ImageSampleC(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c")
    override val name: String = "image_sample_c"
  }

  final case class ImageSampleCB(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_b")
    override val name: String = "image_sample_c_b"
  }

  final case class ImageSampleCBCl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_b_cl")
    override val name: String = "image_sample_c_b_cl"
  }

  final case class ImageSampleCBClO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_b_cl_o")
    override val name: String = "image_sample_c_b_cl_o"
  }

  final case class ImageSampleCBO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_b_o")
    override val name: String = "image_sample_c_b_o"
  }

  final case class ImageSampleCCd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_cd")
    override val name: String = "image_sample_c_cd"
  }

  final case class ImageSampleCCdCl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_cd_cl")
    override val name: String = "image_sample_c_cd_cl"
  }

  final case class ImageSampleCCdClG16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_cd_cl_g16")
    override val name: String = "image_sample_c_cd_cl_g16"
  }

  final case class ImageSampleCCdClO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_cd_cl_o")
    override val name: String = "image_sample_c_cd_cl_o"
  }

  final case class ImageSampleCCdClOG16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_cd_cl_o_g16")
    override val name: String = "image_sample_c_cd_cl_o_g16"
  }

  final case class ImageSampleCCdG16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_cd_g16")
    override val name: String = "image_sample_c_cd_g16"
  }

  final case class ImageSampleCCdO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_cd_o")
    override val name: String = "image_sample_c_cd_o"
  }

  final case class ImageSampleCCdOG16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_cd_o_g16")
    override val name: String = "image_sample_c_cd_o_g16"
  }

  final case class ImageSampleCCl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_cl")
    override val name: String = "image_sample_c_cl"
  }

  final case class ImageSampleCClO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_cl_o")
    override val name: String = "image_sample_c_cl_o"
  }

  final case class ImageSampleCD_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_d")
    override val name: String = "image_sample_c_d"
  }

  final case class ImageSampleCDCl_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_d_cl")
    override val name: String = "image_sample_c_d_cl"
  }

  final case class ImageSampleCDClG16_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_d_cl_g16")
    override val name: String = "image_sample_c_d_cl_g16"
  }

  final case class ImageSampleCDClO_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_d_cl_o")
    override val name: String = "image_sample_c_d_cl_o"
  }

  final case class ImageSampleCDClOG16_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_d_cl_o_g16")
    override val name: String = "image_sample_c_d_cl_o_g16"
  }

  final case class ImageSampleCDG16_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_d_g16")
    override val name: String = "image_sample_c_d_g16"
  }

  final case class ImageSampleCDO_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_d_o")
    override val name: String = "image_sample_c_d_o"
  }

  final case class ImageSampleCDOG16_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_d_o_g16")
    override val name: String = "image_sample_c_d_o_g16"
  }

  final case class ImageSampleCL_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_l")
    override val name: String = "image_sample_c_l"
  }

  final case class ImageSampleCLO_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_l_o")
    override val name: String = "image_sample_c_l_o"
  }

  final case class ImageSampleCLz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_lz")
    override val name: String = "image_sample_c_lz"
  }

  final case class ImageSampleCLzO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_lz_o")
    override val name: String = "image_sample_c_lz_o"
  }

  final case class ImageSampleCO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_c_o")
    override val name: String = "image_sample_c_o"
  }

  final case class ImageSampleCd_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_cd")
    override val name: String = "image_sample_cd"
  }

  final case class ImageSampleCdCl_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_cd_cl")
    override val name: String = "image_sample_cd_cl"
  }

  final case class ImageSampleCdClG16_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_cd_cl_g16")
    override val name: String = "image_sample_cd_cl_g16"
  }

  final case class ImageSampleCdClO_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_cd_cl_o")
    override val name: String = "image_sample_cd_cl_o"
  }

  final case class ImageSampleCdClOG16_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_cd_cl_o_g16")
    override val name: String = "image_sample_cd_cl_o_g16"
  }

  final case class ImageSampleCdG16_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_cd_g16")
    override val name: String = "image_sample_cd_g16"
  }

  final case class ImageSampleCdO_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_cd_o")
    override val name: String = "image_sample_cd_o"
  }

  final case class ImageSampleCdOG16_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_cd_o_g16")
    override val name: String = "image_sample_cd_o_g16"
  }

  final case class ImageSampleCl_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_cl")
    override val name: String = "image_sample_cl"
  }

  final case class ImageSampleClO_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_cl_o")
    override val name: String = "image_sample_cl_o"
  }

  final case class ImageSampleD(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_d")
    override val name: String = "image_sample_d"
  }

  final case class ImageSampleDCl(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_d_cl")
    override val name: String = "image_sample_d_cl"
  }

  final case class ImageSampleDClG16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_d_cl_g16")
    override val name: String = "image_sample_d_cl_g16"
  }

  final case class ImageSampleDClO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_d_cl_o")
    override val name: String = "image_sample_d_cl_o"
  }

  final case class ImageSampleDClOG16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_d_cl_o_g16")
    override val name: String = "image_sample_d_cl_o_g16"
  }

  final case class ImageSampleDG16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_d_g16")
    override val name: String = "image_sample_d_g16"
  }

  final case class ImageSampleDO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_d_o")
    override val name: String = "image_sample_d_o"
  }

  final case class ImageSampleDOG16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_d_o_g16")
    override val name: String = "image_sample_d_o_g16"
  }

  final case class ImageSampleL(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_l")
    override val name: String = "image_sample_l"
  }

  final case class ImageSampleLO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_l_o")
    override val name: String = "image_sample_l_o"
  }

  final case class ImageSampleLz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_lz")
    override val name: String = "image_sample_lz"
  }

  final case class ImageSampleLzO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_lz_o")
    override val name: String = "image_sample_lz_o"
  }

  final case class ImageSampleO(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_sample_o")
    override val name: String = "image_sample_o"
  }

  final case class ImageStore(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store")
    override val name: String = "image_store"
  }

  final case class ImageStoreBy2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_by2")
    override val name: String = "image_store_by2"
  }

  final case class ImageStoreBy4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_by4")
    override val name: String = "image_store_by4"
  }

  final case class ImageStoreMip(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_mip")
    override val name: String = "image_store_mip"
  }

  final case class ImageStoreMipBy2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_mip_by2")
    override val name: String = "image_store_mip_by2"
  }

  final case class ImageStoreMipBy4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_mip_by4")
    override val name: String = "image_store_mip_by4"
  }

  final case class ImageStoreMipPck(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_mip_pck")
    override val name: String = "image_store_mip_pck"
  }

  final case class ImageStoreMipPck2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_mip_pck2")
    override val name: String = "image_store_mip_pck2"
  }

  final case class ImageStoreMipPck4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_mip_pck4")
    override val name: String = "image_store_mip_pck4"
  }

  final case class ImageStorePck(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_pck")
    override val name: String = "image_store_pck"
  }

  final case class ImageStorePck2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_pck2")
    override val name: String = "image_store_pck2"
  }

  final case class ImageStorePck4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("image_store_pck4")
    override val name: String = "image_store_pck4"
  }

  final case class LdsDirectLoad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("lds_direct_load")
    override val name: String = "lds_direct_load"
  }

  final case class LdsParamLoad(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("lds_param_load")
    override val name: String = "lds_param_load"
  }

  final case class SAbsI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_abs_i32")
    override val name: String = "s_abs_i32"
  }

  final case class SAbsdiffI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_absdiff_i32")
    override val name: String = "s_absdiff_i32"
  }

  final case class SAddCoCiU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_add_co_ci_u32")
    override val name: String = "s_add_co_ci_u32"
  }

  final case class SAddCoI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_add_co_i32")
    override val name: String = "s_add_co_i32"
  }

  final case class SAddCoU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_add_co_u32")
    override val name: String = "s_add_co_u32"
  }

  final case class SAddF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_add_f16")
    override val name: String = "s_add_f16"
  }

  final case class SAddF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_add_f32")
    override val name: String = "s_add_f32"
  }

  final case class SAddI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_add_i32")
    override val name: String = "s_add_i32"
  }

  final case class SAddNcU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_add_nc_u64")
    override val name: String = "s_add_nc_u64"
  }

  final case class SAddPcI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_add_pc_i64")
    override val name: String = "s_add_pc_i64"
  }

  final case class SAddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_add_u32")
    override val name: String = "s_add_u32"
  }

  final case class SAddcU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_addc_u32")
    override val name: String = "s_addc_u32"
  }

  final case class SAddkCoI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_addk_co_i32")
    override val name: String = "s_addk_co_i32"
  }

  final case class SAddkI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_addk_i32")
    override val name: String = "s_addk_i32"
  }

  final case class SAllocVgpr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_alloc_vgpr")
    override val name: String = "s_alloc_vgpr"
  }

  final case class SAndB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_b32")
    override val name: String = "s_and_b32"
  }

  final case class SAndB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_b64")
    override val name: String = "s_and_b64"
  }

  final case class SAndNot0SaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_not0_saveexec_b32")
    override val name: String = "s_and_not0_saveexec_b32"
  }

  final case class SAndNot0SaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_not0_saveexec_b64")
    override val name: String = "s_and_not0_saveexec_b64"
  }

  final case class SAndNot0WrexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_not0_wrexec_b32")
    override val name: String = "s_and_not0_wrexec_b32"
  }

  final case class SAndNot0WrexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_not0_wrexec_b64")
    override val name: String = "s_and_not0_wrexec_b64"
  }

  final case class SAndNot1B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_not1_b32")
    override val name: String = "s_and_not1_b32"
  }

  final case class SAndNot1B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_not1_b64")
    override val name: String = "s_and_not1_b64"
  }

  final case class SAndNot1SaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_not1_saveexec_b32")
    override val name: String = "s_and_not1_saveexec_b32"
  }

  final case class SAndNot1SaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_not1_saveexec_b64")
    override val name: String = "s_and_not1_saveexec_b64"
  }

  final case class SAndNot1WrexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_not1_wrexec_b32")
    override val name: String = "s_and_not1_wrexec_b32"
  }

  final case class SAndNot1WrexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_not1_wrexec_b64")
    override val name: String = "s_and_not1_wrexec_b64"
  }

  final case class SAndSaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_saveexec_b32")
    override val name: String = "s_and_saveexec_b32"
  }

  final case class SAndSaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_and_saveexec_b64")
    override val name: String = "s_and_saveexec_b64"
  }

  final case class SAndn1SaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_andn1_saveexec_b32")
    override val name: String = "s_andn1_saveexec_b32"
  }

  final case class SAndn1SaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_andn1_saveexec_b64")
    override val name: String = "s_andn1_saveexec_b64"
  }

  final case class SAndn1WrexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_andn1_wrexec_b32")
    override val name: String = "s_andn1_wrexec_b32"
  }

  final case class SAndn1WrexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_andn1_wrexec_b64")
    override val name: String = "s_andn1_wrexec_b64"
  }

  final case class SAndn2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_andn2_b32")
    override val name: String = "s_andn2_b32"
  }

  final case class SAndn2B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_andn2_b64")
    override val name: String = "s_andn2_b64"
  }

  final case class SAndn2SaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_andn2_saveexec_b32")
    override val name: String = "s_andn2_saveexec_b32"
  }

  final case class SAndn2SaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_andn2_saveexec_b64")
    override val name: String = "s_andn2_saveexec_b64"
  }

  final case class SAndn2WrexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_andn2_wrexec_b32")
    override val name: String = "s_andn2_wrexec_b32"
  }

  final case class SAndn2WrexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_andn2_wrexec_b64")
    override val name: String = "s_andn2_wrexec_b64"
  }

  final case class SAshrI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ashr_i32")
    override val name: String = "s_ashr_i32"
  }

  final case class SAshrI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ashr_i64")
    override val name: String = "s_ashr_i64"
  }

  final case class SAtcProbe(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atc_probe")
    override val name: String = "s_atc_probe"
  }

  final case class SAtcProbeBuffer(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atc_probe_buffer")
    override val name: String = "s_atc_probe_buffer"
  }

  final case class SAtomicAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_add")
    override val name: String = "s_atomic_add"
  }

  final case class SAtomicAddX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_add_x2")
    override val name: String = "s_atomic_add_x2"
  }

  final case class SAtomicAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_and")
    override val name: String = "s_atomic_and"
  }

  final case class SAtomicAndX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_and_x2")
    override val name: String = "s_atomic_and_x2"
  }

  final case class SAtomicCmpswap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_cmpswap")
    override val name: String = "s_atomic_cmpswap"
  }

  final case class SAtomicCmpswapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_cmpswap_x2")
    override val name: String = "s_atomic_cmpswap_x2"
  }

  final case class SAtomicDec(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_dec")
    override val name: String = "s_atomic_dec"
  }

  final case class SAtomicDecX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_dec_x2")
    override val name: String = "s_atomic_dec_x2"
  }

  final case class SAtomicInc(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_inc")
    override val name: String = "s_atomic_inc"
  }

  final case class SAtomicIncX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_inc_x2")
    override val name: String = "s_atomic_inc_x2"
  }

  final case class SAtomicOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_or")
    override val name: String = "s_atomic_or"
  }

  final case class SAtomicOrX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_or_x2")
    override val name: String = "s_atomic_or_x2"
  }

  final case class SAtomicSmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_smax")
    override val name: String = "s_atomic_smax"
  }

  final case class SAtomicSmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_smax_x2")
    override val name: String = "s_atomic_smax_x2"
  }

  final case class SAtomicSmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_smin")
    override val name: String = "s_atomic_smin"
  }

  final case class SAtomicSminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_smin_x2")
    override val name: String = "s_atomic_smin_x2"
  }

  final case class SAtomicSub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_sub")
    override val name: String = "s_atomic_sub"
  }

  final case class SAtomicSubX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_sub_x2")
    override val name: String = "s_atomic_sub_x2"
  }

  final case class SAtomicSwap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_swap")
    override val name: String = "s_atomic_swap"
  }

  final case class SAtomicSwapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_swap_x2")
    override val name: String = "s_atomic_swap_x2"
  }

  final case class SAtomicUmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_umax")
    override val name: String = "s_atomic_umax"
  }

  final case class SAtomicUmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_umax_x2")
    override val name: String = "s_atomic_umax_x2"
  }

  final case class SAtomicUmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_umin")
    override val name: String = "s_atomic_umin"
  }

  final case class SAtomicUminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_umin_x2")
    override val name: String = "s_atomic_umin_x2"
  }

  final case class SAtomicXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_xor")
    override val name: String = "s_atomic_xor"
  }

  final case class SAtomicXorX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_atomic_xor_x2")
    override val name: String = "s_atomic_xor_x2"
  }

  final case class SBarrier(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_barrier")
    override val name: String = "s_barrier"
  }

  final case class SBarrierInit(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_barrier_init")
    override val name: String = "s_barrier_init"
  }

  final case class SBarrierJoin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_barrier_join")
    override val name: String = "s_barrier_join"
  }

  final case class SBarrierLeave(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_barrier_leave")
    override val name: String = "s_barrier_leave"
  }

  final case class SBarrierSignal(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_barrier_signal")
    override val name: String = "s_barrier_signal"
  }

  final case class SBarrierSignalIsfirst(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_barrier_signal_isfirst")
    override val name: String = "s_barrier_signal_isfirst"
  }

  final case class SBarrierWait(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_barrier_wait")
    override val name: String = "s_barrier_wait"
  }

  final case class SBcnt0I32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bcnt0_i32_b32")
    override val name: String = "s_bcnt0_i32_b32"
  }

  final case class SBcnt0I32B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bcnt0_i32_b64")
    override val name: String = "s_bcnt0_i32_b64"
  }

  final case class SBcnt1I32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bcnt1_i32_b32")
    override val name: String = "s_bcnt1_i32_b32"
  }

  final case class SBcnt1I32B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bcnt1_i32_b64")
    override val name: String = "s_bcnt1_i32_b64"
  }

  final case class SBfeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bfe_i32")
    override val name: String = "s_bfe_i32"
  }

  final case class SBfeI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bfe_i64")
    override val name: String = "s_bfe_i64"
  }

  final case class SBfeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bfe_u32")
    override val name: String = "s_bfe_u32"
  }

  final case class SBfeU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bfe_u64")
    override val name: String = "s_bfe_u64"
  }

  final case class SBfmB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bfm_b32")
    override val name: String = "s_bfm_b32"
  }

  final case class SBfmB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bfm_b64")
    override val name: String = "s_bfm_b64"
  }

  final case class SBitcmp0B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bitcmp0_b32")
    override val name: String = "s_bitcmp0_b32"
  }

  final case class SBitcmp0B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bitcmp0_b64")
    override val name: String = "s_bitcmp0_b64"
  }

  final case class SBitcmp1B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bitcmp1_b32")
    override val name: String = "s_bitcmp1_b32"
  }

  final case class SBitcmp1B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bitcmp1_b64")
    override val name: String = "s_bitcmp1_b64"
  }

  final case class SBitreplicateB64B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bitreplicate_b64_b32")
    override val name: String = "s_bitreplicate_b64_b32"
  }

  final case class SBitset0B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bitset0_b32")
    override val name: String = "s_bitset0_b32"
  }

  final case class SBitset0B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bitset0_b64")
    override val name: String = "s_bitset0_b64"
  }

  final case class SBitset1B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bitset1_b32")
    override val name: String = "s_bitset1_b32"
  }

  final case class SBitset1B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_bitset1_b64")
    override val name: String = "s_bitset1_b64"
  }

  final case class SBranch(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_branch")
    override val name: String = "s_branch"
  }

  final case class SBrevB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_brev_b32")
    override val name: String = "s_brev_b32"
  }

  final case class SBrevB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_brev_b64")
    override val name: String = "s_brev_b64"
  }

  final case class SBufferAtomicAdd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_add")
    override val name: String = "s_buffer_atomic_add"
  }

  final case class SBufferAtomicAddX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_add_x2")
    override val name: String = "s_buffer_atomic_add_x2"
  }

  final case class SBufferAtomicAnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_and")
    override val name: String = "s_buffer_atomic_and"
  }

  final case class SBufferAtomicAndX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_and_x2")
    override val name: String = "s_buffer_atomic_and_x2"
  }

  final case class SBufferAtomicCmpswap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_cmpswap")
    override val name: String = "s_buffer_atomic_cmpswap"
  }

  final case class SBufferAtomicCmpswapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_cmpswap_x2")
    override val name: String = "s_buffer_atomic_cmpswap_x2"
  }

  final case class SBufferAtomicDec(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_dec")
    override val name: String = "s_buffer_atomic_dec"
  }

  final case class SBufferAtomicDecX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_dec_x2")
    override val name: String = "s_buffer_atomic_dec_x2"
  }

  final case class SBufferAtomicInc(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_inc")
    override val name: String = "s_buffer_atomic_inc"
  }

  final case class SBufferAtomicIncX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_inc_x2")
    override val name: String = "s_buffer_atomic_inc_x2"
  }

  final case class SBufferAtomicOr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_or")
    override val name: String = "s_buffer_atomic_or"
  }

  final case class SBufferAtomicOrX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_or_x2")
    override val name: String = "s_buffer_atomic_or_x2"
  }

  final case class SBufferAtomicSmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_smax")
    override val name: String = "s_buffer_atomic_smax"
  }

  final case class SBufferAtomicSmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_smax_x2")
    override val name: String = "s_buffer_atomic_smax_x2"
  }

  final case class SBufferAtomicSmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_smin")
    override val name: String = "s_buffer_atomic_smin"
  }

  final case class SBufferAtomicSminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_smin_x2")
    override val name: String = "s_buffer_atomic_smin_x2"
  }

  final case class SBufferAtomicSub(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_sub")
    override val name: String = "s_buffer_atomic_sub"
  }

  final case class SBufferAtomicSubX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_sub_x2")
    override val name: String = "s_buffer_atomic_sub_x2"
  }

  final case class SBufferAtomicSwap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_swap")
    override val name: String = "s_buffer_atomic_swap"
  }

  final case class SBufferAtomicSwapX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_swap_x2")
    override val name: String = "s_buffer_atomic_swap_x2"
  }

  final case class SBufferAtomicUmax(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_umax")
    override val name: String = "s_buffer_atomic_umax"
  }

  final case class SBufferAtomicUmaxX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_umax_x2")
    override val name: String = "s_buffer_atomic_umax_x2"
  }

  final case class SBufferAtomicUmin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_umin")
    override val name: String = "s_buffer_atomic_umin"
  }

  final case class SBufferAtomicUminX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_umin_x2")
    override val name: String = "s_buffer_atomic_umin_x2"
  }

  final case class SBufferAtomicXor(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_xor")
    override val name: String = "s_buffer_atomic_xor"
  }

  final case class SBufferAtomicXorX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_atomic_xor_x2")
    override val name: String = "s_buffer_atomic_xor_x2"
  }

  final case class SBufferLoadB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_b128")
    override val name: String = "s_buffer_load_b128"
  }

  final case class SBufferLoadB256(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_b256")
    override val name: String = "s_buffer_load_b256"
  }

  final case class SBufferLoadB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_b32")
    override val name: String = "s_buffer_load_b32"
  }

  final case class SBufferLoadB512(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_b512")
    override val name: String = "s_buffer_load_b512"
  }

  final case class SBufferLoadB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_b64")
    override val name: String = "s_buffer_load_b64"
  }

  final case class SBufferLoadB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_b96")
    override val name: String = "s_buffer_load_b96"
  }

  final case class SBufferLoadDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_dword")
    override val name: String = "s_buffer_load_dword"
  }

  final case class SBufferLoadDwordx16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_dwordx16")
    override val name: String = "s_buffer_load_dwordx16"
  }

  final case class SBufferLoadDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_dwordx2")
    override val name: String = "s_buffer_load_dwordx2"
  }

  final case class SBufferLoadDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_dwordx4")
    override val name: String = "s_buffer_load_dwordx4"
  }

  final case class SBufferLoadDwordx8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_dwordx8")
    override val name: String = "s_buffer_load_dwordx8"
  }

  final case class SBufferLoadI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_i16")
    override val name: String = "s_buffer_load_i16"
  }

  final case class SBufferLoadI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_i8")
    override val name: String = "s_buffer_load_i8"
  }

  final case class SBufferLoadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_u16")
    override val name: String = "s_buffer_load_u16"
  }

  final case class SBufferLoadU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_load_u8")
    override val name: String = "s_buffer_load_u8"
  }

  final case class SBufferPrefetchData(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_prefetch_data")
    override val name: String = "s_buffer_prefetch_data"
  }

  final case class SBufferStoreDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_store_dword")
    override val name: String = "s_buffer_store_dword"
  }

  final case class SBufferStoreDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_store_dwordx2")
    override val name: String = "s_buffer_store_dwordx2"
  }

  final case class SBufferStoreDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_buffer_store_dwordx4")
    override val name: String = "s_buffer_store_dwordx4"
  }

  final case class SCallB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_call_b64")
    override val name: String = "s_call_b64"
  }

  final case class SCallI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_call_i64")
    override val name: String = "s_call_i64"
  }

  final case class SCbranchCdbgsys(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_cdbgsys")
    override val name: String = "s_cbranch_cdbgsys"
  }

  final case class SCbranchCdbgsysAndUser(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_cdbgsys_and_user")
    override val name: String = "s_cbranch_cdbgsys_and_user"
  }

  final case class SCbranchCdbgsysOrUser(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_cdbgsys_or_user")
    override val name: String = "s_cbranch_cdbgsys_or_user"
  }

  final case class SCbranchCdbguser(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_cdbguser")
    override val name: String = "s_cbranch_cdbguser"
  }

  final case class SCbranchExecnz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_execnz")
    override val name: String = "s_cbranch_execnz"
  }

  final case class SCbranchExecz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_execz")
    override val name: String = "s_cbranch_execz"
  }

  final case class SCbranchGFork(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_g_fork")
    override val name: String = "s_cbranch_g_fork"
  }

  final case class SCbranchIFork(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_i_fork")
    override val name: String = "s_cbranch_i_fork"
  }

  final case class SCbranchJoin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_join")
    override val name: String = "s_cbranch_join"
  }

  final case class SCbranchScc0(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_scc0")
    override val name: String = "s_cbranch_scc0"
  }

  final case class SCbranchScc1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_scc1")
    override val name: String = "s_cbranch_scc1"
  }

  final case class SCbranchVccnz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_vccnz")
    override val name: String = "s_cbranch_vccnz"
  }

  final case class SCbranchVccz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cbranch_vccz")
    override val name: String = "s_cbranch_vccz"
  }

  final case class SCeilF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ceil_f16")
    override val name: String = "s_ceil_f16"
  }

  final case class SCeilF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ceil_f32")
    override val name: String = "s_ceil_f32"
  }

  final case class SClause(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_clause")
    override val name: String = "s_clause"
  }

  final case class SClsI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cls_i32")
    override val name: String = "s_cls_i32"
  }

  final case class SClsI32I64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cls_i32_i64")
    override val name: String = "s_cls_i32_i64"
  }

  final case class SClzI32U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_clz_i32_u32")
    override val name: String = "s_clz_i32_u32"
  }

  final case class SClzI32U64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_clz_i32_u64")
    override val name: String = "s_clz_i32_u64"
  }

  final case class SCmovB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmov_b32")
    override val name: String = "s_cmov_b32"
  }

  final case class SCmovB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmov_b64")
    override val name: String = "s_cmov_b64"
  }

  final case class SCmovkI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmovk_i32")
    override val name: String = "s_cmovk_i32"
  }

  final case class SCmpEqF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_eq_f16")
    override val name: String = "s_cmp_eq_f16"
  }

  final case class SCmpEqF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_eq_f32")
    override val name: String = "s_cmp_eq_f32"
  }

  final case class SCmpEqI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_eq_i32")
    override val name: String = "s_cmp_eq_i32"
  }

  final case class SCmpEqU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_eq_u32")
    override val name: String = "s_cmp_eq_u32"
  }

  final case class SCmpEqU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_eq_u64")
    override val name: String = "s_cmp_eq_u64"
  }

  final case class SCmpGeF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_ge_f16")
    override val name: String = "s_cmp_ge_f16"
  }

  final case class SCmpGeF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_ge_f32")
    override val name: String = "s_cmp_ge_f32"
  }

  final case class SCmpGeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_ge_i32")
    override val name: String = "s_cmp_ge_i32"
  }

  final case class SCmpGeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_ge_u32")
    override val name: String = "s_cmp_ge_u32"
  }

  final case class SCmpGtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_gt_f16")
    override val name: String = "s_cmp_gt_f16"
  }

  final case class SCmpGtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_gt_f32")
    override val name: String = "s_cmp_gt_f32"
  }

  final case class SCmpGtI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_gt_i32")
    override val name: String = "s_cmp_gt_i32"
  }

  final case class SCmpGtU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_gt_u32")
    override val name: String = "s_cmp_gt_u32"
  }

  final case class SCmpLeF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_le_f16")
    override val name: String = "s_cmp_le_f16"
  }

  final case class SCmpLeF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_le_f32")
    override val name: String = "s_cmp_le_f32"
  }

  final case class SCmpLeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_le_i32")
    override val name: String = "s_cmp_le_i32"
  }

  final case class SCmpLeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_le_u32")
    override val name: String = "s_cmp_le_u32"
  }

  final case class SCmpLgF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_lg_f16")
    override val name: String = "s_cmp_lg_f16"
  }

  final case class SCmpLgF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_lg_f32")
    override val name: String = "s_cmp_lg_f32"
  }

  final case class SCmpLgI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_lg_i32")
    override val name: String = "s_cmp_lg_i32"
  }

  final case class SCmpLgU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_lg_u32")
    override val name: String = "s_cmp_lg_u32"
  }

  final case class SCmpLgU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_lg_u64")
    override val name: String = "s_cmp_lg_u64"
  }

  final case class SCmpLtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_lt_f16")
    override val name: String = "s_cmp_lt_f16"
  }

  final case class SCmpLtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_lt_f32")
    override val name: String = "s_cmp_lt_f32"
  }

  final case class SCmpLtI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_lt_i32")
    override val name: String = "s_cmp_lt_i32"
  }

  final case class SCmpLtU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_lt_u32")
    override val name: String = "s_cmp_lt_u32"
  }

  final case class SCmpNeqF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_neq_f16")
    override val name: String = "s_cmp_neq_f16"
  }

  final case class SCmpNeqF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_neq_f32")
    override val name: String = "s_cmp_neq_f32"
  }

  final case class SCmpNgeF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_nge_f16")
    override val name: String = "s_cmp_nge_f16"
  }

  final case class SCmpNgeF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_nge_f32")
    override val name: String = "s_cmp_nge_f32"
  }

  final case class SCmpNgtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_ngt_f16")
    override val name: String = "s_cmp_ngt_f16"
  }

  final case class SCmpNgtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_ngt_f32")
    override val name: String = "s_cmp_ngt_f32"
  }

  final case class SCmpNleF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_nle_f16")
    override val name: String = "s_cmp_nle_f16"
  }

  final case class SCmpNleF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_nle_f32")
    override val name: String = "s_cmp_nle_f32"
  }

  final case class SCmpNlgF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_nlg_f16")
    override val name: String = "s_cmp_nlg_f16"
  }

  final case class SCmpNlgF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_nlg_f32")
    override val name: String = "s_cmp_nlg_f32"
  }

  final case class SCmpNltF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_nlt_f16")
    override val name: String = "s_cmp_nlt_f16"
  }

  final case class SCmpNltF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_nlt_f32")
    override val name: String = "s_cmp_nlt_f32"
  }

  final case class SCmpOF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_o_f16")
    override val name: String = "s_cmp_o_f16"
  }

  final case class SCmpOF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_o_f32")
    override val name: String = "s_cmp_o_f32"
  }

  final case class SCmpUF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_u_f16")
    override val name: String = "s_cmp_u_f16"
  }

  final case class SCmpUF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmp_u_f32")
    override val name: String = "s_cmp_u_f32"
  }

  final case class SCmpkEqI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_eq_i32")
    override val name: String = "s_cmpk_eq_i32"
  }

  final case class SCmpkEqU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_eq_u32")
    override val name: String = "s_cmpk_eq_u32"
  }

  final case class SCmpkGeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_ge_i32")
    override val name: String = "s_cmpk_ge_i32"
  }

  final case class SCmpkGeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_ge_u32")
    override val name: String = "s_cmpk_ge_u32"
  }

  final case class SCmpkGtI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_gt_i32")
    override val name: String = "s_cmpk_gt_i32"
  }

  final case class SCmpkGtU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_gt_u32")
    override val name: String = "s_cmpk_gt_u32"
  }

  final case class SCmpkLeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_le_i32")
    override val name: String = "s_cmpk_le_i32"
  }

  final case class SCmpkLeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_le_u32")
    override val name: String = "s_cmpk_le_u32"
  }

  final case class SCmpkLgI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_lg_i32")
    override val name: String = "s_cmpk_lg_i32"
  }

  final case class SCmpkLgU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_lg_u32")
    override val name: String = "s_cmpk_lg_u32"
  }

  final case class SCmpkLtI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_lt_i32")
    override val name: String = "s_cmpk_lt_i32"
  }

  final case class SCmpkLtU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cmpk_lt_u32")
    override val name: String = "s_cmpk_lt_u32"
  }

  final case class SCodeEnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_code_end")
    override val name: String = "s_code_end"
  }

  final case class SCselectB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cselect_b32")
    override val name: String = "s_cselect_b32"
  }

  final case class SCselectB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cselect_b64")
    override val name: String = "s_cselect_b64"
  }

  final case class SCtzI32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ctz_i32_b32")
    override val name: String = "s_ctz_i32_b32"
  }

  final case class SCtzI32B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ctz_i32_b64")
    override val name: String = "s_ctz_i32_b64"
  }

  final case class SCvtF16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cvt_f16_f32")
    override val name: String = "s_cvt_f16_f32"
  }

  final case class SCvtF32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cvt_f32_f16")
    override val name: String = "s_cvt_f32_f16"
  }

  final case class SCvtF32I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cvt_f32_i32")
    override val name: String = "s_cvt_f32_i32"
  }

  final case class SCvtF32U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cvt_f32_u32")
    override val name: String = "s_cvt_f32_u32"
  }

  final case class SCvtHiF32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cvt_hi_f32_f16")
    override val name: String = "s_cvt_hi_f32_f16"
  }

  final case class SCvtI32F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cvt_i32_f32")
    override val name: String = "s_cvt_i32_f32"
  }

  final case class SCvtPkRtzF16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cvt_pk_rtz_f16_f32")
    override val name: String = "s_cvt_pk_rtz_f16_f32"
  }

  final case class SCvtU32F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_cvt_u32_f32")
    override val name: String = "s_cvt_u32_f32"
  }

  final case class SDcacheDiscard(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_dcache_discard")
    override val name: String = "s_dcache_discard"
  }

  final case class SDcacheDiscardX2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_dcache_discard_x2")
    override val name: String = "s_dcache_discard_x2"
  }

  final case class SDcacheInv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_dcache_inv")
    override val name: String = "s_dcache_inv"
  }

  final case class SDcacheInvVol(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_dcache_inv_vol")
    override val name: String = "s_dcache_inv_vol"
  }

  final case class SDcacheWb(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_dcache_wb")
    override val name: String = "s_dcache_wb"
  }

  final case class SDcacheWbVol(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_dcache_wb_vol")
    override val name: String = "s_dcache_wb_vol"
  }

  final case class SDecperflevel(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_decperflevel")
    override val name: String = "s_decperflevel"
  }

  final case class SDelayAlu(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_delay_alu")
    override val name: String = "s_delay_alu"
  }

  final case class SDenormMode(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_denorm_mode")
    override val name: String = "s_denorm_mode"
  }

  final case class SEndpgm(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_endpgm")
    override val name: String = "s_endpgm"
  }

  final case class SEndpgmOrderedPsDone(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_endpgm_ordered_ps_done")
    override val name: String = "s_endpgm_ordered_ps_done"
  }

  final case class SEndpgmSaved(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_endpgm_saved")
    override val name: String = "s_endpgm_saved"
  }

  final case class SFf0I32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ff0_i32_b32")
    override val name: String = "s_ff0_i32_b32"
  }

  final case class SFf0I32B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ff0_i32_b64")
    override val name: String = "s_ff0_i32_b64"
  }

  final case class SFf1I32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ff1_i32_b32")
    override val name: String = "s_ff1_i32_b32"
  }

  final case class SFf1I32B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ff1_i32_b64")
    override val name: String = "s_ff1_i32_b64"
  }

  final case class SFlbitI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_flbit_i32")
    override val name: String = "s_flbit_i32"
  }

  final case class SFlbitI32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_flbit_i32_b32")
    override val name: String = "s_flbit_i32_b32"
  }

  final case class SFlbitI32B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_flbit_i32_b64")
    override val name: String = "s_flbit_i32_b64"
  }

  final case class SFlbitI32I64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_flbit_i32_i64")
    override val name: String = "s_flbit_i32_i64"
  }

  final case class SFloorF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_floor_f16")
    override val name: String = "s_floor_f16"
  }

  final case class SFloorF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_floor_f32")
    override val name: String = "s_floor_f32"
  }

  final case class SFmaakF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_fmaak_f32")
    override val name: String = "s_fmaak_f32"
  }

  final case class SFmacF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_fmac_f16")
    override val name: String = "s_fmac_f16"
  }

  final case class SFmacF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_fmac_f32")
    override val name: String = "s_fmac_f32"
  }

  final case class SFmamkF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_fmamk_f32")
    override val name: String = "s_fmamk_f32"
  }

  final case class SGetBarrierState(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_get_barrier_state")
    override val name: String = "s_get_barrier_state"
  }

  final case class SGetPcI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_get_pc_i64")
    override val name: String = "s_get_pc_i64"
  }

  final case class SGetShaderCyclesU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_get_shader_cycles_u64")
    override val name: String = "s_get_shader_cycles_u64"
  }

  final case class SGetWaveidInWorkgroup(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_get_waveid_in_workgroup")
    override val name: String = "s_get_waveid_in_workgroup"
  }

  final case class SGetpcB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_getpc_b64")
    override val name: String = "s_getpc_b64"
  }

  final case class SGetregB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_getreg_b32")
    override val name: String = "s_getreg_b32"
  }

  final case class SGl1Inv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_gl1_inv")
    override val name: String = "s_gl1_inv"
  }

  final case class SIcacheInv(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_icache_inv")
    override val name: String = "s_icache_inv"
  }

  final case class SIncperflevel(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_incperflevel")
    override val name: String = "s_incperflevel"
  }

  final case class SInstPrefetch(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_inst_prefetch")
    override val name: String = "s_inst_prefetch"
  }

  final case class SLoadB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_b128")
    override val name: String = "s_load_b128"
  }

  final case class SLoadB256(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_b256")
    override val name: String = "s_load_b256"
  }

  final case class SLoadB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_b32")
    override val name: String = "s_load_b32"
  }

  final case class SLoadB512(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_b512")
    override val name: String = "s_load_b512"
  }

  final case class SLoadB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_b64")
    override val name: String = "s_load_b64"
  }

  final case class SLoadB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_b96")
    override val name: String = "s_load_b96"
  }

  final case class SLoadDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_dword")
    override val name: String = "s_load_dword"
  }

  final case class SLoadDwordx16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_dwordx16")
    override val name: String = "s_load_dwordx16"
  }

  final case class SLoadDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_dwordx2")
    override val name: String = "s_load_dwordx2"
  }

  final case class SLoadDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_dwordx4")
    override val name: String = "s_load_dwordx4"
  }

  final case class SLoadDwordx8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_dwordx8")
    override val name: String = "s_load_dwordx8"
  }

  final case class SLoadI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_i16")
    override val name: String = "s_load_i16"
  }

  final case class SLoadI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_i8")
    override val name: String = "s_load_i8"
  }

  final case class SLoadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_u16")
    override val name: String = "s_load_u16"
  }

  final case class SLoadU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_load_u8")
    override val name: String = "s_load_u8"
  }

  final case class SLshl1AddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_lshl1_add_u32")
    override val name: String = "s_lshl1_add_u32"
  }

  final case class SLshl2AddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_lshl2_add_u32")
    override val name: String = "s_lshl2_add_u32"
  }

  final case class SLshl3AddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_lshl3_add_u32")
    override val name: String = "s_lshl3_add_u32"
  }

  final case class SLshl4AddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_lshl4_add_u32")
    override val name: String = "s_lshl4_add_u32"
  }

  final case class SLshlB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_lshl_b32")
    override val name: String = "s_lshl_b32"
  }

  final case class SLshlB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_lshl_b64")
    override val name: String = "s_lshl_b64"
  }

  final case class SLshrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_lshr_b32")
    override val name: String = "s_lshr_b32"
  }

  final case class SLshrB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_lshr_b64")
    override val name: String = "s_lshr_b64"
  }

  final case class SMaxF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_max_f16")
    override val name: String = "s_max_f16"
  }

  final case class SMaxF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_max_f32")
    override val name: String = "s_max_f32"
  }

  final case class SMaxI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_max_i32")
    override val name: String = "s_max_i32"
  }

  final case class SMaxNumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_max_num_f16")
    override val name: String = "s_max_num_f16"
  }

  final case class SMaxNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_max_num_f32")
    override val name: String = "s_max_num_f32"
  }

  final case class SMaxU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_max_u32")
    override val name: String = "s_max_u32"
  }

  final case class SMaximumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_maximum_f16")
    override val name: String = "s_maximum_f16"
  }

  final case class SMaximumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_maximum_f32")
    override val name: String = "s_maximum_f32"
  }

  final case class SMemrealtime(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_memrealtime")
    override val name: String = "s_memrealtime"
  }

  final case class SMemtime(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_memtime")
    override val name: String = "s_memtime"
  }

  final case class SMinF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_min_f16")
    override val name: String = "s_min_f16"
  }

  final case class SMinF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_min_f32")
    override val name: String = "s_min_f32"
  }

  final case class SMinI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_min_i32")
    override val name: String = "s_min_i32"
  }

  final case class SMinNumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_min_num_f16")
    override val name: String = "s_min_num_f16"
  }

  final case class SMinNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_min_num_f32")
    override val name: String = "s_min_num_f32"
  }

  final case class SMinU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_min_u32")
    override val name: String = "s_min_u32"
  }

  final case class SMinimumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_minimum_f16")
    override val name: String = "s_minimum_f16"
  }

  final case class SMinimumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_minimum_f32")
    override val name: String = "s_minimum_f32"
  }

  final case class SMonitorSleep(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_monitor_sleep")
    override val name: String = "s_monitor_sleep"
  }

  final case class SMovB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_mov_b32")
    override val name: String = "s_mov_b32"
  }

  final case class SMovB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_mov_b64")
    override val name: String = "s_mov_b64"
  }

  final case class SMovFedB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_mov_fed_b32")
    override val name: String = "s_mov_fed_b32"
  }

  final case class SMovkI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_movk_i32")
    override val name: String = "s_movk_i32"
  }

  final case class SMovreldB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_movreld_b32")
    override val name: String = "s_movreld_b32"
  }

  final case class SMovreldB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_movreld_b64")
    override val name: String = "s_movreld_b64"
  }

  final case class SMovrelsB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_movrels_b32")
    override val name: String = "s_movrels_b32"
  }

  final case class SMovrelsB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_movrels_b64")
    override val name: String = "s_movrels_b64"
  }

  final case class SMovrelsd2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_movrelsd_2_b32")
    override val name: String = "s_movrelsd_2_b32"
  }

  final case class SMulF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_mul_f16")
    override val name: String = "s_mul_f16"
  }

  final case class SMulF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_mul_f32")
    override val name: String = "s_mul_f32"
  }

  final case class SMulHiI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_mul_hi_i32")
    override val name: String = "s_mul_hi_i32"
  }

  final case class SMulHiU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_mul_hi_u32")
    override val name: String = "s_mul_hi_u32"
  }

  final case class SMulI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_mul_i32")
    override val name: String = "s_mul_i32"
  }

  final case class SMulU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_mul_u64")
    override val name: String = "s_mul_u64"
  }

  final case class SMulkI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_mulk_i32")
    override val name: String = "s_mulk_i32"
  }

  final case class SNandB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_nand_b32")
    override val name: String = "s_nand_b32"
  }

  final case class SNandB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_nand_b64")
    override val name: String = "s_nand_b64"
  }

  final case class SNandSaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_nand_saveexec_b32")
    override val name: String = "s_nand_saveexec_b32"
  }

  final case class SNandSaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_nand_saveexec_b64")
    override val name: String = "s_nand_saveexec_b64"
  }

  final case class SNop(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_nop")
    override val name: String = "s_nop"
  }

  final case class SNorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_nor_b32")
    override val name: String = "s_nor_b32"
  }

  final case class SNorB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_nor_b64")
    override val name: String = "s_nor_b64"
  }

  final case class SNorSaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_nor_saveexec_b32")
    override val name: String = "s_nor_saveexec_b32"
  }

  final case class SNorSaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_nor_saveexec_b64")
    override val name: String = "s_nor_saveexec_b64"
  }

  final case class SNotB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_not_b32")
    override val name: String = "s_not_b32"
  }

  final case class SNotB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_not_b64")
    override val name: String = "s_not_b64"
  }

  final case class SOrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_or_b32")
    override val name: String = "s_or_b32"
  }

  final case class SOrB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_or_b64")
    override val name: String = "s_or_b64"
  }

  final case class SOrNot0SaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_or_not0_saveexec_b32")
    override val name: String = "s_or_not0_saveexec_b32"
  }

  final case class SOrNot0SaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_or_not0_saveexec_b64")
    override val name: String = "s_or_not0_saveexec_b64"
  }

  final case class SOrNot1B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_or_not1_b32")
    override val name: String = "s_or_not1_b32"
  }

  final case class SOrNot1B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_or_not1_b64")
    override val name: String = "s_or_not1_b64"
  }

  final case class SOrNot1SaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_or_not1_saveexec_b32")
    override val name: String = "s_or_not1_saveexec_b32"
  }

  final case class SOrNot1SaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_or_not1_saveexec_b64")
    override val name: String = "s_or_not1_saveexec_b64"
  }

  final case class SOrSaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_or_saveexec_b32")
    override val name: String = "s_or_saveexec_b32"
  }

  final case class SOrSaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_or_saveexec_b64")
    override val name: String = "s_or_saveexec_b64"
  }

  final case class SOrn1SaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_orn1_saveexec_b32")
    override val name: String = "s_orn1_saveexec_b32"
  }

  final case class SOrn1SaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_orn1_saveexec_b64")
    override val name: String = "s_orn1_saveexec_b64"
  }

  final case class SOrn2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_orn2_b32")
    override val name: String = "s_orn2_b32"
  }

  final case class SOrn2B64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_orn2_b64")
    override val name: String = "s_orn2_b64"
  }

  final case class SOrn2SaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_orn2_saveexec_b32")
    override val name: String = "s_orn2_saveexec_b32"
  }

  final case class SOrn2SaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_orn2_saveexec_b64")
    override val name: String = "s_orn2_saveexec_b64"
  }

  final case class SPackHhB32B16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_pack_hh_b32_b16")
    override val name: String = "s_pack_hh_b32_b16"
  }

  final case class SPackHlB32B16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_pack_hl_b32_b16")
    override val name: String = "s_pack_hl_b32_b16"
  }

  final case class SPackLhB32B16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_pack_lh_b32_b16")
    override val name: String = "s_pack_lh_b32_b16"
  }

  final case class SPackLlB32B16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_pack_ll_b32_b16")
    override val name: String = "s_pack_ll_b32_b16"
  }

  final case class SPrefetchData(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_prefetch_data")
    override val name: String = "s_prefetch_data"
  }

  final case class SPrefetchDataPcRel(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_prefetch_data_pc_rel")
    override val name: String = "s_prefetch_data_pc_rel"
  }

  final case class SPrefetchInst(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_prefetch_inst")
    override val name: String = "s_prefetch_inst"
  }

  final case class SPrefetchInstPcRel(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_prefetch_inst_pc_rel")
    override val name: String = "s_prefetch_inst_pc_rel"
  }

  final case class SQuadmaskB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_quadmask_b32")
    override val name: String = "s_quadmask_b32"
  }

  final case class SQuadmaskB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_quadmask_b64")
    override val name: String = "s_quadmask_b64"
  }

  final case class SRfeB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_rfe_b64")
    override val name: String = "s_rfe_b64"
  }

  final case class SRfeI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_rfe_i64")
    override val name: String = "s_rfe_i64"
  }

  final case class SRfeRestoreB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_rfe_restore_b64")
    override val name: String = "s_rfe_restore_b64"
  }

  final case class SRndneF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_rndne_f16")
    override val name: String = "s_rndne_f16"
  }

  final case class SRndneF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_rndne_f32")
    override val name: String = "s_rndne_f32"
  }

  final case class SRoundMode(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_round_mode")
    override val name: String = "s_round_mode"
  }

  final case class SScratchLoadDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_scratch_load_dword")
    override val name: String = "s_scratch_load_dword"
  }

  final case class SScratchLoadDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_scratch_load_dwordx2")
    override val name: String = "s_scratch_load_dwordx2"
  }

  final case class SScratchLoadDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_scratch_load_dwordx4")
    override val name: String = "s_scratch_load_dwordx4"
  }

  final case class SScratchStoreDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_scratch_store_dword")
    override val name: String = "s_scratch_store_dword"
  }

  final case class SScratchStoreDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_scratch_store_dwordx2")
    override val name: String = "s_scratch_store_dwordx2"
  }

  final case class SScratchStoreDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_scratch_store_dwordx4")
    override val name: String = "s_scratch_store_dwordx4"
  }

  final case class SSendmsg(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sendmsg")
    override val name: String = "s_sendmsg"
  }

  final case class SSendmsgRtnB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sendmsg_rtn_b32")
    override val name: String = "s_sendmsg_rtn_b32"
  }

  final case class SSendmsgRtnB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sendmsg_rtn_b64")
    override val name: String = "s_sendmsg_rtn_b64"
  }

  final case class SSendmsghalt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sendmsghalt")
    override val name: String = "s_sendmsghalt"
  }

  final case class SSetGprIdxIdx(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_set_gpr_idx_idx")
    override val name: String = "s_set_gpr_idx_idx"
  }

  final case class SSetGprIdxMode(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_set_gpr_idx_mode")
    override val name: String = "s_set_gpr_idx_mode"
  }

  final case class SSetGprIdxOff(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_set_gpr_idx_off")
    override val name: String = "s_set_gpr_idx_off"
  }

  final case class SSetGprIdxOn(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_set_gpr_idx_on")
    override val name: String = "s_set_gpr_idx_on"
  }

  final case class SSetInstPrefetchDistance(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_set_inst_prefetch_distance")
    override val name: String = "s_set_inst_prefetch_distance"
  }

  final case class SSetPcI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_set_pc_i64")
    override val name: String = "s_set_pc_i64"
  }

  final case class SSetValuCoexecMode(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_set_valu_coexec_mode")
    override val name: String = "s_set_valu_coexec_mode"
  }

  final case class SSetVgprMsb(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_set_vgpr_msb")
    override val name: String = "s_set_vgpr_msb"
  }

  final case class SSethalt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sethalt")
    override val name: String = "s_sethalt"
  }

  final case class SSetkill(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_setkill")
    override val name: String = "s_setkill"
  }

  final case class SSetpcB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_setpc_b64")
    override val name: String = "s_setpc_b64"
  }

  final case class SSetprio(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_setprio")
    override val name: String = "s_setprio"
  }

  final case class SSetprioIncWg(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_setprio_inc_wg")
    override val name: String = "s_setprio_inc_wg"
  }

  final case class SSetregB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_setreg_b32")
    override val name: String = "s_setreg_b32"
  }

  final case class SSetregImm32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_setreg_imm32_b32")
    override val name: String = "s_setreg_imm32_b32"
  }

  final case class SSetvskip(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_setvskip")
    override val name: String = "s_setvskip"
  }

  final case class SSextI32I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sext_i32_i16")
    override val name: String = "s_sext_i32_i16"
  }

  final case class SSextI32I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sext_i32_i8")
    override val name: String = "s_sext_i32_i8"
  }

  final case class SSleep(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sleep")
    override val name: String = "s_sleep"
  }

  final case class SSleepVar(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sleep_var")
    override val name: String = "s_sleep_var"
  }

  final case class SStoreDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_store_dword")
    override val name: String = "s_store_dword"
  }

  final case class SStoreDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_store_dwordx2")
    override val name: String = "s_store_dwordx2"
  }

  final case class SStoreDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_store_dwordx4")
    override val name: String = "s_store_dwordx4"
  }

  final case class SSubCoCiU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sub_co_ci_u32")
    override val name: String = "s_sub_co_ci_u32"
  }

  final case class SSubCoI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sub_co_i32")
    override val name: String = "s_sub_co_i32"
  }

  final case class SSubCoU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sub_co_u32")
    override val name: String = "s_sub_co_u32"
  }

  final case class SSubF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sub_f16")
    override val name: String = "s_sub_f16"
  }

  final case class SSubF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sub_f32")
    override val name: String = "s_sub_f32"
  }

  final case class SSubI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sub_i32")
    override val name: String = "s_sub_i32"
  }

  final case class SSubNcU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sub_nc_u64")
    override val name: String = "s_sub_nc_u64"
  }

  final case class SSubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_sub_u32")
    override val name: String = "s_sub_u32"
  }

  final case class SSubbU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_subb_u32")
    override val name: String = "s_subb_u32"
  }

  final case class SSubvectorLoopBegin(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_subvector_loop_begin")
    override val name: String = "s_subvector_loop_begin"
  }

  final case class SSubvectorLoopEnd(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_subvector_loop_end")
    override val name: String = "s_subvector_loop_end"
  }

  final case class SSwapPcI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_swap_pc_i64")
    override val name: String = "s_swap_pc_i64"
  }

  final case class SSwappcB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_swappc_b64")
    override val name: String = "s_swappc_b64"
  }

  final case class STrap(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_trap")
    override val name: String = "s_trap"
  }

  final case class STruncF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_trunc_f16")
    override val name: String = "s_trunc_f16"
  }

  final case class STruncF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_trunc_f32")
    override val name: String = "s_trunc_f32"
  }

  final case class STtracedata(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ttracedata")
    override val name: String = "s_ttracedata"
  }

  final case class STtracedataImm(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_ttracedata_imm")
    override val name: String = "s_ttracedata_imm"
  }

  final case class SVersion(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_version")
    override val name: String = "s_version"
  }

  final case class SWaitAlu(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_alu")
    override val name: String = "s_wait_alu"
  }

  final case class SWaitAsynccnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_asynccnt")
    override val name: String = "s_wait_asynccnt"
  }

  final case class SWaitBvhcnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_bvhcnt")
    override val name: String = "s_wait_bvhcnt"
  }

  final case class SWaitDscnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_dscnt")
    override val name: String = "s_wait_dscnt"
  }

  final case class SWaitEvent(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_event")
    override val name: String = "s_wait_event"
  }

  final case class SWaitExpcnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_expcnt")
    override val name: String = "s_wait_expcnt"
  }

  final case class SWaitIdle(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_idle")
    override val name: String = "s_wait_idle"
  }

  final case class SWaitKmcnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_kmcnt")
    override val name: String = "s_wait_kmcnt"
  }

  final case class SWaitLoadcnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_loadcnt")
    override val name: String = "s_wait_loadcnt"
  }

  final case class SWaitLoadcntDscnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_loadcnt_dscnt")
    override val name: String = "s_wait_loadcnt_dscnt"
  }

  final case class SWaitSamplecnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_samplecnt")
    override val name: String = "s_wait_samplecnt"
  }

  final case class SWaitStorecnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_storecnt")
    override val name: String = "s_wait_storecnt"
  }

  final case class SWaitStorecntDscnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_storecnt_dscnt")
    override val name: String = "s_wait_storecnt_dscnt"
  }

  final case class SWaitTensorcnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_tensorcnt")
    override val name: String = "s_wait_tensorcnt"
  }

  final case class SWaitXcnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wait_xcnt")
    override val name: String = "s_wait_xcnt"
  }

  final case class SWaitcnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_waitcnt")
    override val name: String = "s_waitcnt"
  }

  final case class SWaitcntDepctr(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_waitcnt_depctr")
    override val name: String = "s_waitcnt_depctr"
  }

  final case class SWaitcntExpcnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_waitcnt_expcnt")
    override val name: String = "s_waitcnt_expcnt"
  }

  final case class SWaitcntLgkmcnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_waitcnt_lgkmcnt")
    override val name: String = "s_waitcnt_lgkmcnt"
  }

  final case class SWaitcntVmcnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_waitcnt_vmcnt")
    override val name: String = "s_waitcnt_vmcnt"
  }

  final case class SWaitcntVscnt(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_waitcnt_vscnt")
    override val name: String = "s_waitcnt_vscnt"
  }

  final case class SWakeup(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wakeup")
    override val name: String = "s_wakeup"
  }

  final case class SWakeupBarrier(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wakeup_barrier")
    override val name: String = "s_wakeup_barrier"
  }

  final case class SWqmB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wqm_b32")
    override val name: String = "s_wqm_b32"
  }

  final case class SWqmB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_wqm_b64")
    override val name: String = "s_wqm_b64"
  }

  final case class SXnorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_xnor_b32")
    override val name: String = "s_xnor_b32"
  }

  final case class SXnorB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_xnor_b64")
    override val name: String = "s_xnor_b64"
  }

  final case class SXnorSaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_xnor_saveexec_b32")
    override val name: String = "s_xnor_saveexec_b32"
  }

  final case class SXnorSaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_xnor_saveexec_b64")
    override val name: String = "s_xnor_saveexec_b64"
  }

  final case class SXorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_xor_b32")
    override val name: String = "s_xor_b32"
  }

  final case class SXorB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_xor_b64")
    override val name: String = "s_xor_b64"
  }

  final case class SXorSaveexecB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_xor_saveexec_b32")
    override val name: String = "s_xor_saveexec_b32"
  }

  final case class SXorSaveexecB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("s_xor_saveexec_b64")
    override val name: String = "s_xor_saveexec_b64"
  }

  final case class ScratchLoadB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_b128")
    override val name: String = "scratch_load_b128"
  }

  final case class ScratchLoadB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_b32")
    override val name: String = "scratch_load_b32"
  }

  final case class ScratchLoadB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_b64")
    override val name: String = "scratch_load_b64"
  }

  final case class ScratchLoadB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_b96")
    override val name: String = "scratch_load_b96"
  }

  final case class ScratchLoadBlock(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_block")
    override val name: String = "scratch_load_block"
  }

  final case class ScratchLoadD16B16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_d16_b16")
    override val name: String = "scratch_load_d16_b16"
  }

  final case class ScratchLoadD16HiB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_d16_hi_b16")
    override val name: String = "scratch_load_d16_hi_b16"
  }

  final case class ScratchLoadD16HiI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_d16_hi_i8")
    override val name: String = "scratch_load_d16_hi_i8"
  }

  final case class ScratchLoadD16HiU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_d16_hi_u8")
    override val name: String = "scratch_load_d16_hi_u8"
  }

  final case class ScratchLoadD16I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_d16_i8")
    override val name: String = "scratch_load_d16_i8"
  }

  final case class ScratchLoadD16U8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_d16_u8")
    override val name: String = "scratch_load_d16_u8"
  }

  final case class ScratchLoadDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_dword")
    override val name: String = "scratch_load_dword"
  }

  final case class ScratchLoadDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_dwordx2")
    override val name: String = "scratch_load_dwordx2"
  }

  final case class ScratchLoadDwordx3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_dwordx3")
    override val name: String = "scratch_load_dwordx3"
  }

  final case class ScratchLoadDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_dwordx4")
    override val name: String = "scratch_load_dwordx4"
  }

  final case class ScratchLoadI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_i16")
    override val name: String = "scratch_load_i16"
  }

  final case class ScratchLoadI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_i8")
    override val name: String = "scratch_load_i8"
  }

  final case class ScratchLoadLdsDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_lds_dword")
    override val name: String = "scratch_load_lds_dword"
  }

  final case class ScratchLoadLdsSbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_lds_sbyte")
    override val name: String = "scratch_load_lds_sbyte"
  }

  final case class ScratchLoadLdsSshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_lds_sshort")
    override val name: String = "scratch_load_lds_sshort"
  }

  final case class ScratchLoadLdsUbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_lds_ubyte")
    override val name: String = "scratch_load_lds_ubyte"
  }

  final case class ScratchLoadLdsUshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_lds_ushort")
    override val name: String = "scratch_load_lds_ushort"
  }

  final case class ScratchLoadSbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_sbyte")
    override val name: String = "scratch_load_sbyte"
  }

  final case class ScratchLoadSbyteD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_sbyte_d16")
    override val name: String = "scratch_load_sbyte_d16"
  }

  final case class ScratchLoadSbyteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_sbyte_d16_hi")
    override val name: String = "scratch_load_sbyte_d16_hi"
  }

  final case class ScratchLoadShortD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_short_d16")
    override val name: String = "scratch_load_short_d16"
  }

  final case class ScratchLoadShortD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_short_d16_hi")
    override val name: String = "scratch_load_short_d16_hi"
  }

  final case class ScratchLoadSshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_sshort")
    override val name: String = "scratch_load_sshort"
  }

  final case class ScratchLoadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_u16")
    override val name: String = "scratch_load_u16"
  }

  final case class ScratchLoadU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_u8")
    override val name: String = "scratch_load_u8"
  }

  final case class ScratchLoadUbyte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_ubyte")
    override val name: String = "scratch_load_ubyte"
  }

  final case class ScratchLoadUbyteD16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_ubyte_d16")
    override val name: String = "scratch_load_ubyte_d16"
  }

  final case class ScratchLoadUbyteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_ubyte_d16_hi")
    override val name: String = "scratch_load_ubyte_d16_hi"
  }

  final case class ScratchLoadUshort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_load_ushort")
    override val name: String = "scratch_load_ushort"
  }

  final case class ScratchStoreB128(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_b128")
    override val name: String = "scratch_store_b128"
  }

  final case class ScratchStoreB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_b16")
    override val name: String = "scratch_store_b16"
  }

  final case class ScratchStoreB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_b32")
    override val name: String = "scratch_store_b32"
  }

  final case class ScratchStoreB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_b64")
    override val name: String = "scratch_store_b64"
  }

  final case class ScratchStoreB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_b8")
    override val name: String = "scratch_store_b8"
  }

  final case class ScratchStoreB96(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_b96")
    override val name: String = "scratch_store_b96"
  }

  final case class ScratchStoreBlock(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_block")
    override val name: String = "scratch_store_block"
  }

  final case class ScratchStoreByte(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_byte")
    override val name: String = "scratch_store_byte"
  }

  final case class ScratchStoreByteD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_byte_d16_hi")
    override val name: String = "scratch_store_byte_d16_hi"
  }

  final case class ScratchStoreD16HiB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_d16_hi_b16")
    override val name: String = "scratch_store_d16_hi_b16"
  }

  final case class ScratchStoreD16HiB8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_d16_hi_b8")
    override val name: String = "scratch_store_d16_hi_b8"
  }

  final case class ScratchStoreDword(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_dword")
    override val name: String = "scratch_store_dword"
  }

  final case class ScratchStoreDwordx2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_dwordx2")
    override val name: String = "scratch_store_dwordx2"
  }

  final case class ScratchStoreDwordx3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_dwordx3")
    override val name: String = "scratch_store_dwordx3"
  }

  final case class ScratchStoreDwordx4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_dwordx4")
    override val name: String = "scratch_store_dwordx4"
  }

  final case class ScratchStoreShort(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_short")
    override val name: String = "scratch_store_short"
  }

  final case class ScratchStoreShortD16Hi(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("scratch_store_short_d16_hi")
    override val name: String = "scratch_store_short_d16_hi"
  }

  final case class TbufferLoadD16FormatX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_d16_format_x")
    override val name: String = "tbuffer_load_d16_format_x"
  }

  final case class TbufferLoadD16FormatXy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_d16_format_xy")
    override val name: String = "tbuffer_load_d16_format_xy"
  }

  final case class TbufferLoadD16FormatXyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_d16_format_xyz")
    override val name: String = "tbuffer_load_d16_format_xyz"
  }

  final case class TbufferLoadD16FormatXyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_d16_format_xyzw")
    override val name: String = "tbuffer_load_d16_format_xyzw"
  }

  final case class TbufferLoadFormatD16X(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_format_d16_x")
    override val name: String = "tbuffer_load_format_d16_x"
  }

  final case class TbufferLoadFormatD16Xy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_format_d16_xy")
    override val name: String = "tbuffer_load_format_d16_xy"
  }

  final case class TbufferLoadFormatD16Xyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_format_d16_xyz")
    override val name: String = "tbuffer_load_format_d16_xyz"
  }

  final case class TbufferLoadFormatD16Xyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_format_d16_xyzw")
    override val name: String = "tbuffer_load_format_d16_xyzw"
  }

  final case class TbufferLoadFormatX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_format_x")
    override val name: String = "tbuffer_load_format_x"
  }

  final case class TbufferLoadFormatXy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_format_xy")
    override val name: String = "tbuffer_load_format_xy"
  }

  final case class TbufferLoadFormatXyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_format_xyz")
    override val name: String = "tbuffer_load_format_xyz"
  }

  final case class TbufferLoadFormatXyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_load_format_xyzw")
    override val name: String = "tbuffer_load_format_xyzw"
  }

  final case class TbufferStoreD16FormatX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_d16_format_x")
    override val name: String = "tbuffer_store_d16_format_x"
  }

  final case class TbufferStoreD16FormatXy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_d16_format_xy")
    override val name: String = "tbuffer_store_d16_format_xy"
  }

  final case class TbufferStoreD16FormatXyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_d16_format_xyz")
    override val name: String = "tbuffer_store_d16_format_xyz"
  }

  final case class TbufferStoreD16FormatXyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_d16_format_xyzw")
    override val name: String = "tbuffer_store_d16_format_xyzw"
  }

  final case class TbufferStoreFormatD16X(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_format_d16_x")
    override val name: String = "tbuffer_store_format_d16_x"
  }

  final case class TbufferStoreFormatD16Xy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_format_d16_xy")
    override val name: String = "tbuffer_store_format_d16_xy"
  }

  final case class TbufferStoreFormatD16Xyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_format_d16_xyz")
    override val name: String = "tbuffer_store_format_d16_xyz"
  }

  final case class TbufferStoreFormatD16Xyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_format_d16_xyzw")
    override val name: String = "tbuffer_store_format_d16_xyzw"
  }

  final case class TbufferStoreFormatX(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_format_x")
    override val name: String = "tbuffer_store_format_x"
  }

  final case class TbufferStoreFormatXy(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_format_xy")
    override val name: String = "tbuffer_store_format_xy"
  }

  final case class TbufferStoreFormatXyz(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_format_xyz")
    override val name: String = "tbuffer_store_format_xyz"
  }

  final case class TbufferStoreFormatXyzw(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tbuffer_store_format_xyzw")
    override val name: String = "tbuffer_store_format_xyzw"
  }

  final case class TensorLoadToLds(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tensor_load_to_lds")
    override val name: String = "tensor_load_to_lds"
  }

  final case class TensorStoreFromLds(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("tensor_store_from_lds")
    override val name: String = "tensor_store_from_lds"
  }

  final case class VAccvgprMovB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_accvgpr_mov_b32")
    override val name: String = "v_accvgpr_mov_b32"
  }

  final case class VAccvgprRead(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_accvgpr_read")
    override val name: String = "v_accvgpr_read"
  }

  final case class VAccvgprWrite(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_accvgpr_write")
    override val name: String = "v_accvgpr_write"
  }

  final case class VAdd3U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add3_u32")
    override val name: String = "v_add3_u32"
  }

  final case class VAddCoCiU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_co_ci_u32")
    override val name: String = "v_add_co_ci_u32"
  }

  final case class VAddCoU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_co_u32")
    override val name: String = "v_add_co_u32"
  }

  final case class VAddF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_f16")
    override val name: String = "v_add_f16"
  }

  final case class VAddF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_f32")
    override val name: String = "v_add_f32"
  }

  final case class VAddF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_f64")
    override val name: String = "v_add_f64"
  }

  final case class VAddF64E64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_f64_e64")
    override val name: String = "v_add_f64_e64"
  }

  final case class VAddI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_i16")
    override val name: String = "v_add_i16"
  }

  final case class VAddI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_i32")
    override val name: String = "v_add_i32"
  }

  final case class VAddLshlU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_lshl_u32")
    override val name: String = "v_add_lshl_u32"
  }

  final case class VAddMaxI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_max_i32")
    override val name: String = "v_add_max_i32"
  }

  final case class VAddMaxU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_max_u32")
    override val name: String = "v_add_max_u32"
  }

  final case class VAddMinI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_min_i32")
    override val name: String = "v_add_min_i32"
  }

  final case class VAddMinU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_min_u32")
    override val name: String = "v_add_min_u32"
  }

  final case class VAddNcI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_nc_i16")
    override val name: String = "v_add_nc_i16"
  }

  final case class VAddNcI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_nc_i32")
    override val name: String = "v_add_nc_i32"
  }

  final case class VAddNcU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_nc_u16")
    override val name: String = "v_add_nc_u16"
  }

  final case class VAddNcU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_nc_u32")
    override val name: String = "v_add_nc_u32"
  }

  final case class VAddNcU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_nc_u64")
    override val name: String = "v_add_nc_u64"
  }

  final case class VAddU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_u16")
    override val name: String = "v_add_u16"
  }

  final case class VAddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_add_u32")
    override val name: String = "v_add_u32"
  }

  final case class VAddcCoU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_addc_co_u32")
    override val name: String = "v_addc_co_u32"
  }

  final case class VAlignbitB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_alignbit_b32")
    override val name: String = "v_alignbit_b32"
  }

  final case class VAlignbyteB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_alignbyte_b32")
    override val name: String = "v_alignbyte_b32"
  }

  final case class VAndB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_and_b16")
    override val name: String = "v_and_b16"
  }

  final case class VAndB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_and_b32")
    override val name: String = "v_and_b32"
  }

  final case class VAndOrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_and_or_b32")
    override val name: String = "v_and_or_b32"
  }

  final case class VAshrI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ashr_i32")
    override val name: String = "v_ashr_i32"
  }

  final case class VAshrI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ashr_i64")
    override val name: String = "v_ashr_i64"
  }

  final case class VAshrPkI8I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ashr_pk_i8_i32")
    override val name: String = "v_ashr_pk_i8_i32"
  }

  final case class VAshrPkU8I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ashr_pk_u8_i32")
    override val name: String = "v_ashr_pk_u8_i32"
  }

  final case class VAshrrevI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ashrrev_i16")
    override val name: String = "v_ashrrev_i16"
  }

  final case class VAshrrevI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ashrrev_i32")
    override val name: String = "v_ashrrev_i32"
  }

  final case class VAshrrevI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ashrrev_i64")
    override val name: String = "v_ashrrev_i64"
  }

  final case class VBcntU32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_bcnt_u32_b32")
    override val name: String = "v_bcnt_u32_b32"
  }

  final case class VBfeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_bfe_i32")
    override val name: String = "v_bfe_i32"
  }

  final case class VBfeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_bfe_u32")
    override val name: String = "v_bfe_u32"
  }

  final case class VBfiB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_bfi_b32")
    override val name: String = "v_bfi_b32"
  }

  final case class VBfmB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_bfm_b32")
    override val name: String = "v_bfm_b32"
  }

  final case class VBfrevB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_bfrev_b32")
    override val name: String = "v_bfrev_b32"
  }

  final case class VBitop3B16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_bitop3_b16")
    override val name: String = "v_bitop3_b16"
  }

  final case class VBitop3B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_bitop3_b32")
    override val name: String = "v_bitop3_b32"
  }

  final case class VCeilF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ceil_f16")
    override val name: String = "v_ceil_f16"
  }

  final case class VCeilF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ceil_f32")
    override val name: String = "v_ceil_f32"
  }

  final case class VCeilF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ceil_f64")
    override val name: String = "v_ceil_f64"
  }

  final case class VClrexcp(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_clrexcp")
    override val name: String = "v_clrexcp"
  }

  final case class VClsI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cls_i32")
    override val name: String = "v_cls_i32"
  }

  final case class VClzI32U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_clz_i32_u32")
    override val name: String = "v_clz_i32_u32"
  }

  final case class VCmpClassF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_class_f16")
    override val name: String = "v_cmp_class_f16"
  }

  final case class VCmpClassF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_class_f32")
    override val name: String = "v_cmp_class_f32"
  }

  final case class VCmpClassF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_class_f64")
    override val name: String = "v_cmp_class_f64"
  }

  final case class VCmpEqF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_eq_f16")
    override val name: String = "v_cmp_eq_f16"
  }

  final case class VCmpEqF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_eq_f32")
    override val name: String = "v_cmp_eq_f32"
  }

  final case class VCmpEqF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_eq_f64")
    override val name: String = "v_cmp_eq_f64"
  }

  final case class VCmpEqI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_eq_i16")
    override val name: String = "v_cmp_eq_i16"
  }

  final case class VCmpEqI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_eq_i32")
    override val name: String = "v_cmp_eq_i32"
  }

  final case class VCmpEqI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_eq_i64")
    override val name: String = "v_cmp_eq_i64"
  }

  final case class VCmpEqU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_eq_u16")
    override val name: String = "v_cmp_eq_u16"
  }

  final case class VCmpEqU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_eq_u32")
    override val name: String = "v_cmp_eq_u32"
  }

  final case class VCmpEqU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_eq_u64")
    override val name: String = "v_cmp_eq_u64"
  }

  final case class VCmpFF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_f_f16")
    override val name: String = "v_cmp_f_f16"
  }

  final case class VCmpFF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_f_f32")
    override val name: String = "v_cmp_f_f32"
  }

  final case class VCmpFF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_f_f64")
    override val name: String = "v_cmp_f_f64"
  }

  final case class VCmpFI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_f_i16")
    override val name: String = "v_cmp_f_i16"
  }

  final case class VCmpFI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_f_i32")
    override val name: String = "v_cmp_f_i32"
  }

  final case class VCmpFI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_f_i64")
    override val name: String = "v_cmp_f_i64"
  }

  final case class VCmpFU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_f_u16")
    override val name: String = "v_cmp_f_u16"
  }

  final case class VCmpFU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_f_u32")
    override val name: String = "v_cmp_f_u32"
  }

  final case class VCmpFU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_f_u64")
    override val name: String = "v_cmp_f_u64"
  }

  final case class VCmpGeF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ge_f16")
    override val name: String = "v_cmp_ge_f16"
  }

  final case class VCmpGeF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ge_f32")
    override val name: String = "v_cmp_ge_f32"
  }

  final case class VCmpGeF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ge_f64")
    override val name: String = "v_cmp_ge_f64"
  }

  final case class VCmpGeI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ge_i16")
    override val name: String = "v_cmp_ge_i16"
  }

  final case class VCmpGeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ge_i32")
    override val name: String = "v_cmp_ge_i32"
  }

  final case class VCmpGeI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ge_i64")
    override val name: String = "v_cmp_ge_i64"
  }

  final case class VCmpGeU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ge_u16")
    override val name: String = "v_cmp_ge_u16"
  }

  final case class VCmpGeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ge_u32")
    override val name: String = "v_cmp_ge_u32"
  }

  final case class VCmpGeU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ge_u64")
    override val name: String = "v_cmp_ge_u64"
  }

  final case class VCmpGtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_gt_f16")
    override val name: String = "v_cmp_gt_f16"
  }

  final case class VCmpGtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_gt_f32")
    override val name: String = "v_cmp_gt_f32"
  }

  final case class VCmpGtF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_gt_f64")
    override val name: String = "v_cmp_gt_f64"
  }

  final case class VCmpGtI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_gt_i16")
    override val name: String = "v_cmp_gt_i16"
  }

  final case class VCmpGtI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_gt_i32")
    override val name: String = "v_cmp_gt_i32"
  }

  final case class VCmpGtI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_gt_i64")
    override val name: String = "v_cmp_gt_i64"
  }

  final case class VCmpGtU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_gt_u16")
    override val name: String = "v_cmp_gt_u16"
  }

  final case class VCmpGtU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_gt_u32")
    override val name: String = "v_cmp_gt_u32"
  }

  final case class VCmpGtU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_gt_u64")
    override val name: String = "v_cmp_gt_u64"
  }

  final case class VCmpLeF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_le_f16")
    override val name: String = "v_cmp_le_f16"
  }

  final case class VCmpLeF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_le_f32")
    override val name: String = "v_cmp_le_f32"
  }

  final case class VCmpLeF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_le_f64")
    override val name: String = "v_cmp_le_f64"
  }

  final case class VCmpLeI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_le_i16")
    override val name: String = "v_cmp_le_i16"
  }

  final case class VCmpLeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_le_i32")
    override val name: String = "v_cmp_le_i32"
  }

  final case class VCmpLeI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_le_i64")
    override val name: String = "v_cmp_le_i64"
  }

  final case class VCmpLeU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_le_u16")
    override val name: String = "v_cmp_le_u16"
  }

  final case class VCmpLeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_le_u32")
    override val name: String = "v_cmp_le_u32"
  }

  final case class VCmpLeU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_le_u64")
    override val name: String = "v_cmp_le_u64"
  }

  final case class VCmpLgF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lg_f16")
    override val name: String = "v_cmp_lg_f16"
  }

  final case class VCmpLgF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lg_f32")
    override val name: String = "v_cmp_lg_f32"
  }

  final case class VCmpLgF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lg_f64")
    override val name: String = "v_cmp_lg_f64"
  }

  final case class VCmpLgI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lg_i16")
    override val name: String = "v_cmp_lg_i16"
  }

  final case class VCmpLgI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lg_i32")
    override val name: String = "v_cmp_lg_i32"
  }

  final case class VCmpLgI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lg_i64")
    override val name: String = "v_cmp_lg_i64"
  }

  final case class VCmpLgU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lg_u16")
    override val name: String = "v_cmp_lg_u16"
  }

  final case class VCmpLgU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lg_u32")
    override val name: String = "v_cmp_lg_u32"
  }

  final case class VCmpLgU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lg_u64")
    override val name: String = "v_cmp_lg_u64"
  }

  final case class VCmpLtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lt_f16")
    override val name: String = "v_cmp_lt_f16"
  }

  final case class VCmpLtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lt_f32")
    override val name: String = "v_cmp_lt_f32"
  }

  final case class VCmpLtF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lt_f64")
    override val name: String = "v_cmp_lt_f64"
  }

  final case class VCmpLtI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lt_i16")
    override val name: String = "v_cmp_lt_i16"
  }

  final case class VCmpLtI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lt_i32")
    override val name: String = "v_cmp_lt_i32"
  }

  final case class VCmpLtI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lt_i64")
    override val name: String = "v_cmp_lt_i64"
  }

  final case class VCmpLtU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lt_u16")
    override val name: String = "v_cmp_lt_u16"
  }

  final case class VCmpLtU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lt_u32")
    override val name: String = "v_cmp_lt_u32"
  }

  final case class VCmpLtU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_lt_u64")
    override val name: String = "v_cmp_lt_u64"
  }

  final case class VCmpNeI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ne_i16")
    override val name: String = "v_cmp_ne_i16"
  }

  final case class VCmpNeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ne_i32")
    override val name: String = "v_cmp_ne_i32"
  }

  final case class VCmpNeI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ne_i64")
    override val name: String = "v_cmp_ne_i64"
  }

  final case class VCmpNeU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ne_u16")
    override val name: String = "v_cmp_ne_u16"
  }

  final case class VCmpNeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ne_u32")
    override val name: String = "v_cmp_ne_u32"
  }

  final case class VCmpNeU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ne_u64")
    override val name: String = "v_cmp_ne_u64"
  }

  final case class VCmpNeqF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_neq_f16")
    override val name: String = "v_cmp_neq_f16"
  }

  final case class VCmpNeqF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_neq_f32")
    override val name: String = "v_cmp_neq_f32"
  }

  final case class VCmpNeqF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_neq_f64")
    override val name: String = "v_cmp_neq_f64"
  }

  final case class VCmpNgeF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nge_f16")
    override val name: String = "v_cmp_nge_f16"
  }

  final case class VCmpNgeF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nge_f32")
    override val name: String = "v_cmp_nge_f32"
  }

  final case class VCmpNgeF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nge_f64")
    override val name: String = "v_cmp_nge_f64"
  }

  final case class VCmpNgtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ngt_f16")
    override val name: String = "v_cmp_ngt_f16"
  }

  final case class VCmpNgtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ngt_f32")
    override val name: String = "v_cmp_ngt_f32"
  }

  final case class VCmpNgtF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_ngt_f64")
    override val name: String = "v_cmp_ngt_f64"
  }

  final case class VCmpNleF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nle_f16")
    override val name: String = "v_cmp_nle_f16"
  }

  final case class VCmpNleF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nle_f32")
    override val name: String = "v_cmp_nle_f32"
  }

  final case class VCmpNleF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nle_f64")
    override val name: String = "v_cmp_nle_f64"
  }

  final case class VCmpNlgF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nlg_f16")
    override val name: String = "v_cmp_nlg_f16"
  }

  final case class VCmpNlgF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nlg_f32")
    override val name: String = "v_cmp_nlg_f32"
  }

  final case class VCmpNlgF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nlg_f64")
    override val name: String = "v_cmp_nlg_f64"
  }

  final case class VCmpNltF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nlt_f16")
    override val name: String = "v_cmp_nlt_f16"
  }

  final case class VCmpNltF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nlt_f32")
    override val name: String = "v_cmp_nlt_f32"
  }

  final case class VCmpNltF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_nlt_f64")
    override val name: String = "v_cmp_nlt_f64"
  }

  final case class VCmpOF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_o_f16")
    override val name: String = "v_cmp_o_f16"
  }

  final case class VCmpOF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_o_f32")
    override val name: String = "v_cmp_o_f32"
  }

  final case class VCmpOF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_o_f64")
    override val name: String = "v_cmp_o_f64"
  }

  final case class VCmpTF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_t_f16")
    override val name: String = "v_cmp_t_f16"
  }

  final case class VCmpTF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_t_f32")
    override val name: String = "v_cmp_t_f32"
  }

  final case class VCmpTF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_t_f64")
    override val name: String = "v_cmp_t_f64"
  }

  final case class VCmpTI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_t_i16")
    override val name: String = "v_cmp_t_i16"
  }

  final case class VCmpTI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_t_i32")
    override val name: String = "v_cmp_t_i32"
  }

  final case class VCmpTI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_t_i64")
    override val name: String = "v_cmp_t_i64"
  }

  final case class VCmpTU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_t_u16")
    override val name: String = "v_cmp_t_u16"
  }

  final case class VCmpTU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_t_u32")
    override val name: String = "v_cmp_t_u32"
  }

  final case class VCmpTU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_t_u64")
    override val name: String = "v_cmp_t_u64"
  }

  final case class VCmpTruF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_tru_f16")
    override val name: String = "v_cmp_tru_f16"
  }

  final case class VCmpTruF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_tru_f32")
    override val name: String = "v_cmp_tru_f32"
  }

  final case class VCmpTruF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_tru_f64")
    override val name: String = "v_cmp_tru_f64"
  }

  final case class VCmpTruI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_tru_i16")
    override val name: String = "v_cmp_tru_i16"
  }

  final case class VCmpTruI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_tru_i32")
    override val name: String = "v_cmp_tru_i32"
  }

  final case class VCmpTruI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_tru_i64")
    override val name: String = "v_cmp_tru_i64"
  }

  final case class VCmpTruU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_tru_u16")
    override val name: String = "v_cmp_tru_u16"
  }

  final case class VCmpTruU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_tru_u32")
    override val name: String = "v_cmp_tru_u32"
  }

  final case class VCmpTruU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_tru_u64")
    override val name: String = "v_cmp_tru_u64"
  }

  final case class VCmpUF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_u_f16")
    override val name: String = "v_cmp_u_f16"
  }

  final case class VCmpUF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_u_f32")
    override val name: String = "v_cmp_u_f32"
  }

  final case class VCmpUF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmp_u_f64")
    override val name: String = "v_cmp_u_f64"
  }

  final case class VCmpxClassF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_class_f16")
    override val name: String = "v_cmpx_class_f16"
  }

  final case class VCmpxClassF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_class_f32")
    override val name: String = "v_cmpx_class_f32"
  }

  final case class VCmpxClassF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_class_f64")
    override val name: String = "v_cmpx_class_f64"
  }

  final case class VCmpxEqF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_eq_f16")
    override val name: String = "v_cmpx_eq_f16"
  }

  final case class VCmpxEqF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_eq_f32")
    override val name: String = "v_cmpx_eq_f32"
  }

  final case class VCmpxEqF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_eq_f64")
    override val name: String = "v_cmpx_eq_f64"
  }

  final case class VCmpxEqI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_eq_i16")
    override val name: String = "v_cmpx_eq_i16"
  }

  final case class VCmpxEqI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_eq_i32")
    override val name: String = "v_cmpx_eq_i32"
  }

  final case class VCmpxEqI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_eq_i64")
    override val name: String = "v_cmpx_eq_i64"
  }

  final case class VCmpxEqU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_eq_u16")
    override val name: String = "v_cmpx_eq_u16"
  }

  final case class VCmpxEqU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_eq_u32")
    override val name: String = "v_cmpx_eq_u32"
  }

  final case class VCmpxEqU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_eq_u64")
    override val name: String = "v_cmpx_eq_u64"
  }

  final case class VCmpxFF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_f_f16")
    override val name: String = "v_cmpx_f_f16"
  }

  final case class VCmpxFF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_f_f32")
    override val name: String = "v_cmpx_f_f32"
  }

  final case class VCmpxFF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_f_f64")
    override val name: String = "v_cmpx_f_f64"
  }

  final case class VCmpxFI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_f_i16")
    override val name: String = "v_cmpx_f_i16"
  }

  final case class VCmpxFI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_f_i32")
    override val name: String = "v_cmpx_f_i32"
  }

  final case class VCmpxFI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_f_i64")
    override val name: String = "v_cmpx_f_i64"
  }

  final case class VCmpxFU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_f_u16")
    override val name: String = "v_cmpx_f_u16"
  }

  final case class VCmpxFU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_f_u32")
    override val name: String = "v_cmpx_f_u32"
  }

  final case class VCmpxFU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_f_u64")
    override val name: String = "v_cmpx_f_u64"
  }

  final case class VCmpxGeF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ge_f16")
    override val name: String = "v_cmpx_ge_f16"
  }

  final case class VCmpxGeF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ge_f32")
    override val name: String = "v_cmpx_ge_f32"
  }

  final case class VCmpxGeF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ge_f64")
    override val name: String = "v_cmpx_ge_f64"
  }

  final case class VCmpxGeI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ge_i16")
    override val name: String = "v_cmpx_ge_i16"
  }

  final case class VCmpxGeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ge_i32")
    override val name: String = "v_cmpx_ge_i32"
  }

  final case class VCmpxGeI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ge_i64")
    override val name: String = "v_cmpx_ge_i64"
  }

  final case class VCmpxGeU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ge_u16")
    override val name: String = "v_cmpx_ge_u16"
  }

  final case class VCmpxGeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ge_u32")
    override val name: String = "v_cmpx_ge_u32"
  }

  final case class VCmpxGeU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ge_u64")
    override val name: String = "v_cmpx_ge_u64"
  }

  final case class VCmpxGtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_gt_f16")
    override val name: String = "v_cmpx_gt_f16"
  }

  final case class VCmpxGtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_gt_f32")
    override val name: String = "v_cmpx_gt_f32"
  }

  final case class VCmpxGtF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_gt_f64")
    override val name: String = "v_cmpx_gt_f64"
  }

  final case class VCmpxGtI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_gt_i16")
    override val name: String = "v_cmpx_gt_i16"
  }

  final case class VCmpxGtI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_gt_i32")
    override val name: String = "v_cmpx_gt_i32"
  }

  final case class VCmpxGtI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_gt_i64")
    override val name: String = "v_cmpx_gt_i64"
  }

  final case class VCmpxGtU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_gt_u16")
    override val name: String = "v_cmpx_gt_u16"
  }

  final case class VCmpxGtU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_gt_u32")
    override val name: String = "v_cmpx_gt_u32"
  }

  final case class VCmpxGtU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_gt_u64")
    override val name: String = "v_cmpx_gt_u64"
  }

  final case class VCmpxLeF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_le_f16")
    override val name: String = "v_cmpx_le_f16"
  }

  final case class VCmpxLeF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_le_f32")
    override val name: String = "v_cmpx_le_f32"
  }

  final case class VCmpxLeF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_le_f64")
    override val name: String = "v_cmpx_le_f64"
  }

  final case class VCmpxLeI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_le_i16")
    override val name: String = "v_cmpx_le_i16"
  }

  final case class VCmpxLeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_le_i32")
    override val name: String = "v_cmpx_le_i32"
  }

  final case class VCmpxLeI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_le_i64")
    override val name: String = "v_cmpx_le_i64"
  }

  final case class VCmpxLeU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_le_u16")
    override val name: String = "v_cmpx_le_u16"
  }

  final case class VCmpxLeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_le_u32")
    override val name: String = "v_cmpx_le_u32"
  }

  final case class VCmpxLeU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_le_u64")
    override val name: String = "v_cmpx_le_u64"
  }

  final case class VCmpxLgF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lg_f16")
    override val name: String = "v_cmpx_lg_f16"
  }

  final case class VCmpxLgF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lg_f32")
    override val name: String = "v_cmpx_lg_f32"
  }

  final case class VCmpxLgF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lg_f64")
    override val name: String = "v_cmpx_lg_f64"
  }

  final case class VCmpxLgI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lg_i16")
    override val name: String = "v_cmpx_lg_i16"
  }

  final case class VCmpxLgI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lg_i32")
    override val name: String = "v_cmpx_lg_i32"
  }

  final case class VCmpxLgI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lg_i64")
    override val name: String = "v_cmpx_lg_i64"
  }

  final case class VCmpxLgU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lg_u16")
    override val name: String = "v_cmpx_lg_u16"
  }

  final case class VCmpxLgU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lg_u32")
    override val name: String = "v_cmpx_lg_u32"
  }

  final case class VCmpxLgU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lg_u64")
    override val name: String = "v_cmpx_lg_u64"
  }

  final case class VCmpxLtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lt_f16")
    override val name: String = "v_cmpx_lt_f16"
  }

  final case class VCmpxLtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lt_f32")
    override val name: String = "v_cmpx_lt_f32"
  }

  final case class VCmpxLtF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lt_f64")
    override val name: String = "v_cmpx_lt_f64"
  }

  final case class VCmpxLtI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lt_i16")
    override val name: String = "v_cmpx_lt_i16"
  }

  final case class VCmpxLtI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lt_i32")
    override val name: String = "v_cmpx_lt_i32"
  }

  final case class VCmpxLtI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lt_i64")
    override val name: String = "v_cmpx_lt_i64"
  }

  final case class VCmpxLtU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lt_u16")
    override val name: String = "v_cmpx_lt_u16"
  }

  final case class VCmpxLtU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lt_u32")
    override val name: String = "v_cmpx_lt_u32"
  }

  final case class VCmpxLtU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_lt_u64")
    override val name: String = "v_cmpx_lt_u64"
  }

  final case class VCmpxNeI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ne_i16")
    override val name: String = "v_cmpx_ne_i16"
  }

  final case class VCmpxNeI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ne_i32")
    override val name: String = "v_cmpx_ne_i32"
  }

  final case class VCmpxNeI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ne_i64")
    override val name: String = "v_cmpx_ne_i64"
  }

  final case class VCmpxNeU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ne_u16")
    override val name: String = "v_cmpx_ne_u16"
  }

  final case class VCmpxNeU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ne_u32")
    override val name: String = "v_cmpx_ne_u32"
  }

  final case class VCmpxNeU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ne_u64")
    override val name: String = "v_cmpx_ne_u64"
  }

  final case class VCmpxNeqF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_neq_f16")
    override val name: String = "v_cmpx_neq_f16"
  }

  final case class VCmpxNeqF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_neq_f32")
    override val name: String = "v_cmpx_neq_f32"
  }

  final case class VCmpxNeqF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_neq_f64")
    override val name: String = "v_cmpx_neq_f64"
  }

  final case class VCmpxNgeF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nge_f16")
    override val name: String = "v_cmpx_nge_f16"
  }

  final case class VCmpxNgeF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nge_f32")
    override val name: String = "v_cmpx_nge_f32"
  }

  final case class VCmpxNgeF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nge_f64")
    override val name: String = "v_cmpx_nge_f64"
  }

  final case class VCmpxNgtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ngt_f16")
    override val name: String = "v_cmpx_ngt_f16"
  }

  final case class VCmpxNgtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ngt_f32")
    override val name: String = "v_cmpx_ngt_f32"
  }

  final case class VCmpxNgtF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_ngt_f64")
    override val name: String = "v_cmpx_ngt_f64"
  }

  final case class VCmpxNleF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nle_f16")
    override val name: String = "v_cmpx_nle_f16"
  }

  final case class VCmpxNleF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nle_f32")
    override val name: String = "v_cmpx_nle_f32"
  }

  final case class VCmpxNleF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nle_f64")
    override val name: String = "v_cmpx_nle_f64"
  }

  final case class VCmpxNlgF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nlg_f16")
    override val name: String = "v_cmpx_nlg_f16"
  }

  final case class VCmpxNlgF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nlg_f32")
    override val name: String = "v_cmpx_nlg_f32"
  }

  final case class VCmpxNlgF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nlg_f64")
    override val name: String = "v_cmpx_nlg_f64"
  }

  final case class VCmpxNltF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nlt_f16")
    override val name: String = "v_cmpx_nlt_f16"
  }

  final case class VCmpxNltF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nlt_f32")
    override val name: String = "v_cmpx_nlt_f32"
  }

  final case class VCmpxNltF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_nlt_f64")
    override val name: String = "v_cmpx_nlt_f64"
  }

  final case class VCmpxOF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_o_f16")
    override val name: String = "v_cmpx_o_f16"
  }

  final case class VCmpxOF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_o_f32")
    override val name: String = "v_cmpx_o_f32"
  }

  final case class VCmpxOF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_o_f64")
    override val name: String = "v_cmpx_o_f64"
  }

  final case class VCmpxTF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_t_f16")
    override val name: String = "v_cmpx_t_f16"
  }

  final case class VCmpxTF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_t_f32")
    override val name: String = "v_cmpx_t_f32"
  }

  final case class VCmpxTF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_t_f64")
    override val name: String = "v_cmpx_t_f64"
  }

  final case class VCmpxTI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_t_i16")
    override val name: String = "v_cmpx_t_i16"
  }

  final case class VCmpxTI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_t_i32")
    override val name: String = "v_cmpx_t_i32"
  }

  final case class VCmpxTI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_t_i64")
    override val name: String = "v_cmpx_t_i64"
  }

  final case class VCmpxTU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_t_u16")
    override val name: String = "v_cmpx_t_u16"
  }

  final case class VCmpxTU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_t_u32")
    override val name: String = "v_cmpx_t_u32"
  }

  final case class VCmpxTU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_t_u64")
    override val name: String = "v_cmpx_t_u64"
  }

  final case class VCmpxTruF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_tru_f16")
    override val name: String = "v_cmpx_tru_f16"
  }

  final case class VCmpxTruF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_tru_f32")
    override val name: String = "v_cmpx_tru_f32"
  }

  final case class VCmpxTruF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_tru_f64")
    override val name: String = "v_cmpx_tru_f64"
  }

  final case class VCmpxTruI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_tru_i16")
    override val name: String = "v_cmpx_tru_i16"
  }

  final case class VCmpxTruI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_tru_i32")
    override val name: String = "v_cmpx_tru_i32"
  }

  final case class VCmpxTruI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_tru_i64")
    override val name: String = "v_cmpx_tru_i64"
  }

  final case class VCmpxTruU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_tru_u16")
    override val name: String = "v_cmpx_tru_u16"
  }

  final case class VCmpxTruU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_tru_u32")
    override val name: String = "v_cmpx_tru_u32"
  }

  final case class VCmpxTruU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_tru_u64")
    override val name: String = "v_cmpx_tru_u64"
  }

  final case class VCmpxUF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_u_f16")
    override val name: String = "v_cmpx_u_f16"
  }

  final case class VCmpxUF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_u_f32")
    override val name: String = "v_cmpx_u_f32"
  }

  final case class VCmpxUF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cmpx_u_f64")
    override val name: String = "v_cmpx_u_f64"
  }

  final case class VCndmaskB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cndmask_b16")
    override val name: String = "v_cndmask_b16"
  }

  final case class VCndmaskB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cndmask_b32")
    override val name: String = "v_cndmask_b32"
  }

  final case class VCosBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cos_bf16")
    override val name: String = "v_cos_bf16"
  }

  final case class VCosF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cos_f16")
    override val name: String = "v_cos_f16"
  }

  final case class VCosF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cos_f32")
    override val name: String = "v_cos_f32"
  }

  final case class VCtzI32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ctz_i32_b32")
    override val name: String = "v_ctz_i32_b32"
  }

  final case class VCubeidF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cubeid_f32")
    override val name: String = "v_cubeid_f32"
  }

  final case class VCubemaF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cubema_f32")
    override val name: String = "v_cubema_f32"
  }

  final case class VCubescF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cubesc_f32")
    override val name: String = "v_cubesc_f32"
  }

  final case class VCubetcF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cubetc_f32")
    override val name: String = "v_cubetc_f32"
  }

  final case class VCvtF16Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f16_bf8")
    override val name: String = "v_cvt_f16_bf8"
  }

  final case class VCvtF16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f16_f32")
    override val name: String = "v_cvt_f16_f32"
  }

  final case class VCvtF16Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f16_fp8")
    override val name: String = "v_cvt_f16_fp8"
  }

  final case class VCvtF16I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f16_i16")
    override val name: String = "v_cvt_f16_i16"
  }

  final case class VCvtF16U16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f16_u16")
    override val name: String = "v_cvt_f16_u16"
  }

  final case class VCvtF32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_bf16")
    override val name: String = "v_cvt_f32_bf16"
  }

  final case class VCvtF32Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_bf8")
    override val name: String = "v_cvt_f32_bf8"
  }

  final case class VCvtF32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_f16")
    override val name: String = "v_cvt_f32_f16"
  }

  final case class VCvtF32F64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_f64")
    override val name: String = "v_cvt_f32_f64"
  }

  final case class VCvtF32Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_fp8")
    override val name: String = "v_cvt_f32_fp8"
  }

  final case class VCvtF32I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_i32")
    override val name: String = "v_cvt_f32_i32"
  }

  final case class VCvtF32U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_u32")
    override val name: String = "v_cvt_f32_u32"
  }

  final case class VCvtF32Ubyte0(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_ubyte0")
    override val name: String = "v_cvt_f32_ubyte0"
  }

  final case class VCvtF32Ubyte1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_ubyte1")
    override val name: String = "v_cvt_f32_ubyte1"
  }

  final case class VCvtF32Ubyte2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_ubyte2")
    override val name: String = "v_cvt_f32_ubyte2"
  }

  final case class VCvtF32Ubyte3(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f32_ubyte3")
    override val name: String = "v_cvt_f32_ubyte3"
  }

  final case class VCvtF64F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f64_f32")
    override val name: String = "v_cvt_f64_f32"
  }

  final case class VCvtF64I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f64_i32")
    override val name: String = "v_cvt_f64_i32"
  }

  final case class VCvtF64U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_f64_u32")
    override val name: String = "v_cvt_f64_u32"
  }

  final case class VCvtFloorI32F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_floor_i32_f32")
    override val name: String = "v_cvt_floor_i32_f32"
  }

  final case class VCvtFlrI32F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_flr_i32_f32")
    override val name: String = "v_cvt_flr_i32_f32"
  }

  final case class VCvtI16F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_i16_f16")
    override val name: String = "v_cvt_i16_f16"
  }

  final case class VCvtI32F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_i32_f32")
    override val name: String = "v_cvt_i32_f32"
  }

  final case class VCvtI32F64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_i32_f64")
    override val name: String = "v_cvt_i32_f64"
  }

  final case class VCvtI32I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_i32_i16")
    override val name: String = "v_cvt_i32_i16"
  }

  final case class VCvtNearestI32F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_nearest_i32_f32")
    override val name: String = "v_cvt_nearest_i32_f32"
  }

  final case class VCvtNormI16F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_norm_i16_f16")
    override val name: String = "v_cvt_norm_i16_f16"
  }

  final case class VCvtNormU16F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_norm_u16_f16")
    override val name: String = "v_cvt_norm_u16_f16"
  }

  final case class VCvtOffF32I4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_off_f32_i4")
    override val name: String = "v_cvt_off_f32_i4"
  }

  final case class VCvtPkBf16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_bf16_f32")
    override val name: String = "v_cvt_pk_bf16_f32"
  }

  final case class VCvtPkBf8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_bf8_f16")
    override val name: String = "v_cvt_pk_bf8_f16"
  }

  final case class VCvtPkBf8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_bf8_f32")
    override val name: String = "v_cvt_pk_bf8_f32"
  }

  final case class VCvtPkF16Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_f16_bf8")
    override val name: String = "v_cvt_pk_f16_bf8"
  }

  final case class VCvtPkF16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_f16_f32")
    override val name: String = "v_cvt_pk_f16_f32"
  }

  final case class VCvtPkF16Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_f16_fp8")
    override val name: String = "v_cvt_pk_f16_fp8"
  }

  final case class VCvtPkF32Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_f32_bf8")
    override val name: String = "v_cvt_pk_f32_bf8"
  }

  final case class VCvtPkF32Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_f32_fp8")
    override val name: String = "v_cvt_pk_f32_fp8"
  }

  final case class VCvtPkFp8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_fp8_f16")
    override val name: String = "v_cvt_pk_fp8_f16"
  }

  final case class VCvtPkFp8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_fp8_f32")
    override val name: String = "v_cvt_pk_fp8_f32"
  }

  final case class VCvtPkI16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_i16_f32")
    override val name: String = "v_cvt_pk_i16_f32"
  }

  final case class VCvtPkI16I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_i16_i32")
    override val name: String = "v_cvt_pk_i16_i32"
  }

  final case class VCvtPkNormI16F16_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_norm_i16_f16")
    override val name: String = "v_cvt_pk_norm_i16_f16"
  }

  final case class VCvtPkNormI16F32_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_norm_i16_f32")
    override val name: String = "v_cvt_pk_norm_i16_f32"
  }

  final case class VCvtPkNormU16F16_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_norm_u16_f16")
    override val name: String = "v_cvt_pk_norm_u16_f16"
  }

  final case class VCvtPkNormU16F32_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_norm_u16_f32")
    override val name: String = "v_cvt_pk_norm_u16_f32"
  }

  final case class VCvtPkRtzF16F32_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_rtz_f16_f32")
    override val name: String = "v_cvt_pk_rtz_f16_f32"
  }

  final case class VCvtPkU16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_u16_f32")
    override val name: String = "v_cvt_pk_u16_f32"
  }

  final case class VCvtPkU16U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_u16_u32")
    override val name: String = "v_cvt_pk_u16_u32"
  }

  final case class VCvtPkU8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pk_u8_f32")
    override val name: String = "v_cvt_pk_u8_f32"
  }

  final case class VCvtPkaccumU8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pkaccum_u8_f32")
    override val name: String = "v_cvt_pkaccum_u8_f32"
  }

  final case class VCvtPknormI16F16_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pknorm_i16_f16")
    override val name: String = "v_cvt_pknorm_i16_f16"
  }

  final case class VCvtPknormI16F32_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pknorm_i16_f32")
    override val name: String = "v_cvt_pknorm_i16_f32"
  }

  final case class VCvtPknormU16F16_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pknorm_u16_f16")
    override val name: String = "v_cvt_pknorm_u16_f16"
  }

  final case class VCvtPknormU16F32_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pknorm_u16_f32")
    override val name: String = "v_cvt_pknorm_u16_f32"
  }

  final case class VCvtPkrtzF16F32_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pkrtz_f16_f32")
    override val name: String = "v_cvt_pkrtz_f16_f32"
  }

  final case class VCvtPkrtzF16F32E64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_pkrtz_f16_f32_e64")
    override val name: String = "v_cvt_pkrtz_f16_f32_e64"
  }

  final case class VCvtRpiI32F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_rpi_i32_f32")
    override val name: String = "v_cvt_rpi_i32_f32"
  }

  final case class VCvtScalePk16Bf16Bf6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk16_bf16_bf6")
    override val name: String = "v_cvt_scale_pk16_bf16_bf6"
  }

  final case class VCvtScalePk16Bf16Fp6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk16_bf16_fp6")
    override val name: String = "v_cvt_scale_pk16_bf16_fp6"
  }

  final case class VCvtScalePk16F16Bf6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk16_f16_bf6")
    override val name: String = "v_cvt_scale_pk16_f16_bf6"
  }

  final case class VCvtScalePk16F16Fp6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk16_f16_fp6")
    override val name: String = "v_cvt_scale_pk16_f16_fp6"
  }

  final case class VCvtScalePk16F32Bf6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk16_f32_bf6")
    override val name: String = "v_cvt_scale_pk16_f32_bf6"
  }

  final case class VCvtScalePk16F32Fp6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk16_f32_fp6")
    override val name: String = "v_cvt_scale_pk16_f32_fp6"
  }

  final case class VCvtScalePk8Bf16Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk8_bf16_bf8")
    override val name: String = "v_cvt_scale_pk8_bf16_bf8"
  }

  final case class VCvtScalePk8Bf16Fp4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk8_bf16_fp4")
    override val name: String = "v_cvt_scale_pk8_bf16_fp4"
  }

  final case class VCvtScalePk8Bf16Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk8_bf16_fp8")
    override val name: String = "v_cvt_scale_pk8_bf16_fp8"
  }

  final case class VCvtScalePk8F16Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk8_f16_bf8")
    override val name: String = "v_cvt_scale_pk8_f16_bf8"
  }

  final case class VCvtScalePk8F16Fp4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk8_f16_fp4")
    override val name: String = "v_cvt_scale_pk8_f16_fp4"
  }

  final case class VCvtScalePk8F16Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk8_f16_fp8")
    override val name: String = "v_cvt_scale_pk8_f16_fp8"
  }

  final case class VCvtScalePk8F32Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk8_f32_bf8")
    override val name: String = "v_cvt_scale_pk8_f32_bf8"
  }

  final case class VCvtScalePk8F32Fp4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk8_f32_fp4")
    override val name: String = "v_cvt_scale_pk8_f32_fp4"
  }

  final case class VCvtScalePk8F32Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scale_pk8_f32_fp8")
    override val name: String = "v_cvt_scale_pk8_f32_fp8"
  }

  final case class VCvtScalef322xpk16Bf6F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_2xpk16_bf6_f32")
    override val name: String = "v_cvt_scalef32_2xpk16_bf6_f32"
  }

  final case class VCvtScalef322xpk16Fp6F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_2xpk16_fp6_f32")
    override val name: String = "v_cvt_scalef32_2xpk16_fp6_f32"
  }

  final case class VCvtScalef32F16Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_f16_bf8")
    override val name: String = "v_cvt_scalef32_f16_bf8"
  }

  final case class VCvtScalef32F16Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_f16_fp8")
    override val name: String = "v_cvt_scalef32_f16_fp8"
  }

  final case class VCvtScalef32F32Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_f32_bf8")
    override val name: String = "v_cvt_scalef32_f32_bf8"
  }

  final case class VCvtScalef32F32Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_f32_fp8")
    override val name: String = "v_cvt_scalef32_f32_fp8"
  }

  final case class VCvtScalef32Pk16Bf6Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk16_bf6_bf16")
    override val name: String = "v_cvt_scalef32_pk16_bf6_bf16"
  }

  final case class VCvtScalef32Pk16Bf6F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk16_bf6_f16")
    override val name: String = "v_cvt_scalef32_pk16_bf6_f16"
  }

  final case class VCvtScalef32Pk16Bf6F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk16_bf6_f32")
    override val name: String = "v_cvt_scalef32_pk16_bf6_f32"
  }

  final case class VCvtScalef32Pk16Fp6Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk16_fp6_bf16")
    override val name: String = "v_cvt_scalef32_pk16_fp6_bf16"
  }

  final case class VCvtScalef32Pk16Fp6F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk16_fp6_f16")
    override val name: String = "v_cvt_scalef32_pk16_fp6_f16"
  }

  final case class VCvtScalef32Pk16Fp6F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk16_fp6_f32")
    override val name: String = "v_cvt_scalef32_pk16_fp6_f32"
  }

  final case class VCvtScalef32Pk32Bf16Bf6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk32_bf16_bf6")
    override val name: String = "v_cvt_scalef32_pk32_bf16_bf6"
  }

  final case class VCvtScalef32Pk32Bf16Fp6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk32_bf16_fp6")
    override val name: String = "v_cvt_scalef32_pk32_bf16_fp6"
  }

  final case class VCvtScalef32Pk32Bf6Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk32_bf6_bf16")
    override val name: String = "v_cvt_scalef32_pk32_bf6_bf16"
  }

  final case class VCvtScalef32Pk32Bf6F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk32_bf6_f16")
    override val name: String = "v_cvt_scalef32_pk32_bf6_f16"
  }

  final case class VCvtScalef32Pk32F16Bf6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk32_f16_bf6")
    override val name: String = "v_cvt_scalef32_pk32_f16_bf6"
  }

  final case class VCvtScalef32Pk32F16Fp6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk32_f16_fp6")
    override val name: String = "v_cvt_scalef32_pk32_f16_fp6"
  }

  final case class VCvtScalef32Pk32F32Bf6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk32_f32_bf6")
    override val name: String = "v_cvt_scalef32_pk32_f32_bf6"
  }

  final case class VCvtScalef32Pk32F32Fp6(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk32_f32_fp6")
    override val name: String = "v_cvt_scalef32_pk32_f32_fp6"
  }

  final case class VCvtScalef32Pk32Fp6Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk32_fp6_bf16")
    override val name: String = "v_cvt_scalef32_pk32_fp6_bf16"
  }

  final case class VCvtScalef32Pk32Fp6F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk32_fp6_f16")
    override val name: String = "v_cvt_scalef32_pk32_fp6_f16"
  }

  final case class VCvtScalef32Pk8Bf8Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk8_bf8_bf16")
    override val name: String = "v_cvt_scalef32_pk8_bf8_bf16"
  }

  final case class VCvtScalef32Pk8Bf8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk8_bf8_f16")
    override val name: String = "v_cvt_scalef32_pk8_bf8_f16"
  }

  final case class VCvtScalef32Pk8Bf8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk8_bf8_f32")
    override val name: String = "v_cvt_scalef32_pk8_bf8_f32"
  }

  final case class VCvtScalef32Pk8Fp4Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk8_fp4_bf16")
    override val name: String = "v_cvt_scalef32_pk8_fp4_bf16"
  }

  final case class VCvtScalef32Pk8Fp4F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk8_fp4_f16")
    override val name: String = "v_cvt_scalef32_pk8_fp4_f16"
  }

  final case class VCvtScalef32Pk8Fp4F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk8_fp4_f32")
    override val name: String = "v_cvt_scalef32_pk8_fp4_f32"
  }

  final case class VCvtScalef32Pk8Fp8Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk8_fp8_bf16")
    override val name: String = "v_cvt_scalef32_pk8_fp8_bf16"
  }

  final case class VCvtScalef32Pk8Fp8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk8_fp8_f16")
    override val name: String = "v_cvt_scalef32_pk8_fp8_f16"
  }

  final case class VCvtScalef32Pk8Fp8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk8_fp8_f32")
    override val name: String = "v_cvt_scalef32_pk8_fp8_f32"
  }

  final case class VCvtScalef32PkBf16Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_bf16_bf8")
    override val name: String = "v_cvt_scalef32_pk_bf16_bf8"
  }

  final case class VCvtScalef32PkBf16Fp4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_bf16_fp4")
    override val name: String = "v_cvt_scalef32_pk_bf16_fp4"
  }

  final case class VCvtScalef32PkBf16Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_bf16_fp8")
    override val name: String = "v_cvt_scalef32_pk_bf16_fp8"
  }

  final case class VCvtScalef32PkBf8Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_bf8_bf16")
    override val name: String = "v_cvt_scalef32_pk_bf8_bf16"
  }

  final case class VCvtScalef32PkBf8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_bf8_f16")
    override val name: String = "v_cvt_scalef32_pk_bf8_f16"
  }

  final case class VCvtScalef32PkBf8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_bf8_f32")
    override val name: String = "v_cvt_scalef32_pk_bf8_f32"
  }

  final case class VCvtScalef32PkF16Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_f16_bf8")
    override val name: String = "v_cvt_scalef32_pk_f16_bf8"
  }

  final case class VCvtScalef32PkF16Fp4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_f16_fp4")
    override val name: String = "v_cvt_scalef32_pk_f16_fp4"
  }

  final case class VCvtScalef32PkF16Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_f16_fp8")
    override val name: String = "v_cvt_scalef32_pk_f16_fp8"
  }

  final case class VCvtScalef32PkF32Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_f32_bf8")
    override val name: String = "v_cvt_scalef32_pk_f32_bf8"
  }

  final case class VCvtScalef32PkF32Fp4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_f32_fp4")
    override val name: String = "v_cvt_scalef32_pk_f32_fp4"
  }

  final case class VCvtScalef32PkF32Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_f32_fp8")
    override val name: String = "v_cvt_scalef32_pk_f32_fp8"
  }

  final case class VCvtScalef32PkFp4Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_fp4_bf16")
    override val name: String = "v_cvt_scalef32_pk_fp4_bf16"
  }

  final case class VCvtScalef32PkFp4F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_fp4_f16")
    override val name: String = "v_cvt_scalef32_pk_fp4_f16"
  }

  final case class VCvtScalef32PkFp4F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_fp4_f32")
    override val name: String = "v_cvt_scalef32_pk_fp4_f32"
  }

  final case class VCvtScalef32PkFp8Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_fp8_bf16")
    override val name: String = "v_cvt_scalef32_pk_fp8_bf16"
  }

  final case class VCvtScalef32PkFp8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_fp8_f16")
    override val name: String = "v_cvt_scalef32_pk_fp8_f16"
  }

  final case class VCvtScalef32PkFp8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_pk_fp8_f32")
    override val name: String = "v_cvt_scalef32_pk_fp8_f32"
  }

  final case class VCvtScalef32SrBf8Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_bf8_bf16")
    override val name: String = "v_cvt_scalef32_sr_bf8_bf16"
  }

  final case class VCvtScalef32SrBf8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_bf8_f16")
    override val name: String = "v_cvt_scalef32_sr_bf8_f16"
  }

  final case class VCvtScalef32SrBf8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_bf8_f32")
    override val name: String = "v_cvt_scalef32_sr_bf8_f32"
  }

  final case class VCvtScalef32SrFp8Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_fp8_bf16")
    override val name: String = "v_cvt_scalef32_sr_fp8_bf16"
  }

  final case class VCvtScalef32SrFp8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_fp8_f16")
    override val name: String = "v_cvt_scalef32_sr_fp8_f16"
  }

  final case class VCvtScalef32SrFp8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_fp8_f32")
    override val name: String = "v_cvt_scalef32_sr_fp8_f32"
  }

  final case class VCvtScalef32SrPk16Bf6Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk16_bf6_bf16")
    override val name: String = "v_cvt_scalef32_sr_pk16_bf6_bf16"
  }

  final case class VCvtScalef32SrPk16Bf6F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk16_bf6_f16")
    override val name: String = "v_cvt_scalef32_sr_pk16_bf6_f16"
  }

  final case class VCvtScalef32SrPk16Bf6F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk16_bf6_f32")
    override val name: String = "v_cvt_scalef32_sr_pk16_bf6_f32"
  }

  final case class VCvtScalef32SrPk16Fp6Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk16_fp6_bf16")
    override val name: String = "v_cvt_scalef32_sr_pk16_fp6_bf16"
  }

  final case class VCvtScalef32SrPk16Fp6F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk16_fp6_f16")
    override val name: String = "v_cvt_scalef32_sr_pk16_fp6_f16"
  }

  final case class VCvtScalef32SrPk16Fp6F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk16_fp6_f32")
    override val name: String = "v_cvt_scalef32_sr_pk16_fp6_f32"
  }

  final case class VCvtScalef32SrPk32Bf6Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk32_bf6_bf16")
    override val name: String = "v_cvt_scalef32_sr_pk32_bf6_bf16"
  }

  final case class VCvtScalef32SrPk32Bf6F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk32_bf6_f16")
    override val name: String = "v_cvt_scalef32_sr_pk32_bf6_f16"
  }

  final case class VCvtScalef32SrPk32Bf6F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk32_bf6_f32")
    override val name: String = "v_cvt_scalef32_sr_pk32_bf6_f32"
  }

  final case class VCvtScalef32SrPk32Fp6Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk32_fp6_bf16")
    override val name: String = "v_cvt_scalef32_sr_pk32_fp6_bf16"
  }

  final case class VCvtScalef32SrPk32Fp6F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk32_fp6_f16")
    override val name: String = "v_cvt_scalef32_sr_pk32_fp6_f16"
  }

  final case class VCvtScalef32SrPk32Fp6F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk32_fp6_f32")
    override val name: String = "v_cvt_scalef32_sr_pk32_fp6_f32"
  }

  final case class VCvtScalef32SrPk8Bf8Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk8_bf8_bf16")
    override val name: String = "v_cvt_scalef32_sr_pk8_bf8_bf16"
  }

  final case class VCvtScalef32SrPk8Bf8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk8_bf8_f16")
    override val name: String = "v_cvt_scalef32_sr_pk8_bf8_f16"
  }

  final case class VCvtScalef32SrPk8Bf8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk8_bf8_f32")
    override val name: String = "v_cvt_scalef32_sr_pk8_bf8_f32"
  }

  final case class VCvtScalef32SrPk8Fp4Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk8_fp4_bf16")
    override val name: String = "v_cvt_scalef32_sr_pk8_fp4_bf16"
  }

  final case class VCvtScalef32SrPk8Fp4F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk8_fp4_f16")
    override val name: String = "v_cvt_scalef32_sr_pk8_fp4_f16"
  }

  final case class VCvtScalef32SrPk8Fp4F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk8_fp4_f32")
    override val name: String = "v_cvt_scalef32_sr_pk8_fp4_f32"
  }

  final case class VCvtScalef32SrPk8Fp8Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk8_fp8_bf16")
    override val name: String = "v_cvt_scalef32_sr_pk8_fp8_bf16"
  }

  final case class VCvtScalef32SrPk8Fp8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk8_fp8_f16")
    override val name: String = "v_cvt_scalef32_sr_pk8_fp8_f16"
  }

  final case class VCvtScalef32SrPk8Fp8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk8_fp8_f32")
    override val name: String = "v_cvt_scalef32_sr_pk8_fp8_f32"
  }

  final case class VCvtScalef32SrPkFp4Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk_fp4_bf16")
    override val name: String = "v_cvt_scalef32_sr_pk_fp4_bf16"
  }

  final case class VCvtScalef32SrPkFp4F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk_fp4_f16")
    override val name: String = "v_cvt_scalef32_sr_pk_fp4_f16"
  }

  final case class VCvtScalef32SrPkFp4F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_scalef32_sr_pk_fp4_f32")
    override val name: String = "v_cvt_scalef32_sr_pk_fp4_f32"
  }

  final case class VCvtSrBf16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_sr_bf16_f32")
    override val name: String = "v_cvt_sr_bf16_f32"
  }

  final case class VCvtSrBf8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_sr_bf8_f16")
    override val name: String = "v_cvt_sr_bf8_f16"
  }

  final case class VCvtSrBf8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_sr_bf8_f32")
    override val name: String = "v_cvt_sr_bf8_f32"
  }

  final case class VCvtSrF16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_sr_f16_f32")
    override val name: String = "v_cvt_sr_f16_f32"
  }

  final case class VCvtSrFp8F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_sr_fp8_f16")
    override val name: String = "v_cvt_sr_fp8_f16"
  }

  final case class VCvtSrFp8F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_sr_fp8_f32")
    override val name: String = "v_cvt_sr_fp8_f32"
  }

  final case class VCvtSrPkBf16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_sr_pk_bf16_f32")
    override val name: String = "v_cvt_sr_pk_bf16_f32"
  }

  final case class VCvtSrPkF16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_sr_pk_f16_f32")
    override val name: String = "v_cvt_sr_pk_f16_f32"
  }

  final case class VCvtU16F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_u16_f16")
    override val name: String = "v_cvt_u16_f16"
  }

  final case class VCvtU32F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_u32_f32")
    override val name: String = "v_cvt_u32_f32"
  }

  final case class VCvtU32F64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_u32_f64")
    override val name: String = "v_cvt_u32_f64"
  }

  final case class VCvtU32U16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_cvt_u32_u16")
    override val name: String = "v_cvt_u32_u16"
  }

  final case class VDivFixupF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_div_fixup_f16")
    override val name: String = "v_div_fixup_f16"
  }

  final case class VDivFixupF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_div_fixup_f32")
    override val name: String = "v_div_fixup_f32"
  }

  final case class VDivFixupF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_div_fixup_f64")
    override val name: String = "v_div_fixup_f64"
  }

  final case class VDivFixupLegacyF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_div_fixup_legacy_f16")
    override val name: String = "v_div_fixup_legacy_f16"
  }

  final case class VDivFmasF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_div_fmas_f32")
    override val name: String = "v_div_fmas_f32"
  }

  final case class VDivFmasF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_div_fmas_f64")
    override val name: String = "v_div_fmas_f64"
  }

  final case class VDivScaleF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_div_scale_f32")
    override val name: String = "v_div_scale_f32"
  }

  final case class VDivScaleF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_div_scale_f64")
    override val name: String = "v_div_scale_f64"
  }

  final case class VDot2Bf16Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot2_bf16_bf16")
    override val name: String = "v_dot2_bf16_bf16"
  }

  final case class VDot2F16F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot2_f16_f16")
    override val name: String = "v_dot2_f16_f16"
  }

  final case class VDot2F32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot2_f32_bf16")
    override val name: String = "v_dot2_f32_bf16"
  }

  final case class VDot2F32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot2_f32_f16")
    override val name: String = "v_dot2_f32_f16"
  }

  final case class VDot2I32I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot2_i32_i16")
    override val name: String = "v_dot2_i32_i16"
  }

  final case class VDot2U32U16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot2_u32_u16")
    override val name: String = "v_dot2_u32_u16"
  }

  final case class VDot2accF32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot2acc_f32_f16")
    override val name: String = "v_dot2acc_f32_f16"
  }

  final case class VDot2cF32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot2c_f32_bf16")
    override val name: String = "v_dot2c_f32_bf16"
  }

  final case class VDot2cF32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot2c_f32_f16")
    override val name: String = "v_dot2c_f32_f16"
  }

  final case class VDot2cI32I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot2c_i32_i16")
    override val name: String = "v_dot2c_i32_i16"
  }

  final case class VDot4F32Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot4_f32_bf8_bf8")
    override val name: String = "v_dot4_f32_bf8_bf8"
  }

  final case class VDot4F32Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot4_f32_bf8_fp8")
    override val name: String = "v_dot4_f32_bf8_fp8"
  }

  final case class VDot4F32Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot4_f32_fp8_bf8")
    override val name: String = "v_dot4_f32_fp8_bf8"
  }

  final case class VDot4F32Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot4_f32_fp8_fp8")
    override val name: String = "v_dot4_f32_fp8_fp8"
  }

  final case class VDot4I32I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot4_i32_i8")
    override val name: String = "v_dot4_i32_i8"
  }

  final case class VDot4I32Iu8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot4_i32_iu8")
    override val name: String = "v_dot4_i32_iu8"
  }

  final case class VDot4U32U8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot4_u32_u8")
    override val name: String = "v_dot4_u32_u8"
  }

  final case class VDot4cI32I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot4c_i32_i8")
    override val name: String = "v_dot4c_i32_i8"
  }

  final case class VDot8I32I4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot8_i32_i4")
    override val name: String = "v_dot8_i32_i4"
  }

  final case class VDot8I32Iu4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot8_i32_iu4")
    override val name: String = "v_dot8_i32_iu4"
  }

  final case class VDot8U32U4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot8_u32_u4")
    override val name: String = "v_dot8_u32_u4"
  }

  final case class VDot8cI32I4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dot8c_i32_i4")
    override val name: String = "v_dot8c_i32_i4"
  }

  final case class VDualAddF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_add_f32")
    override val name: String = "v_dual_add_f32"
  }

  final case class VDualAddF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_add_f64")
    override val name: String = "v_dual_add_f64"
  }

  final case class VDualAddNcU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_add_nc_u32")
    override val name: String = "v_dual_add_nc_u32"
  }

  final case class VDualAndB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_and_b32")
    override val name: String = "v_dual_and_b32"
  }

  final case class VDualAshrrevI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_ashrrev_i32")
    override val name: String = "v_dual_ashrrev_i32"
  }

  final case class VDualBitop2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_bitop2_b32")
    override val name: String = "v_dual_bitop2_b32"
  }

  final case class VDualCndmaskB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_cndmask_b32")
    override val name: String = "v_dual_cndmask_b32"
  }

  final case class VDualDot2accF32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_dot2acc_f32_bf16")
    override val name: String = "v_dual_dot2acc_f32_bf16"
  }

  final case class VDualDot2accF32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_dot2acc_f32_f16")
    override val name: String = "v_dual_dot2acc_f32_f16"
  }

  final case class VDualFmaF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_fma_f32")
    override val name: String = "v_dual_fma_f32"
  }

  final case class VDualFmaF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_fma_f64")
    override val name: String = "v_dual_fma_f64"
  }

  final case class VDualFmaakF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_fmaak_f32")
    override val name: String = "v_dual_fmaak_f32"
  }

  final case class VDualFmacF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_fmac_f32")
    override val name: String = "v_dual_fmac_f32"
  }

  final case class VDualFmamkF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_fmamk_f32")
    override val name: String = "v_dual_fmamk_f32"
  }

  final case class VDualLshlrevB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_lshlrev_b32")
    override val name: String = "v_dual_lshlrev_b32"
  }

  final case class VDualLshrrevB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_lshrrev_b32")
    override val name: String = "v_dual_lshrrev_b32"
  }

  final case class VDualMaxF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_max_f32")
    override val name: String = "v_dual_max_f32"
  }

  final case class VDualMaxI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_max_i32")
    override val name: String = "v_dual_max_i32"
  }

  final case class VDualMaxNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_max_num_f32")
    override val name: String = "v_dual_max_num_f32"
  }

  final case class VDualMaxNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_max_num_f64")
    override val name: String = "v_dual_max_num_f64"
  }

  final case class VDualMinF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_min_f32")
    override val name: String = "v_dual_min_f32"
  }

  final case class VDualMinI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_min_i32")
    override val name: String = "v_dual_min_i32"
  }

  final case class VDualMinNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_min_num_f32")
    override val name: String = "v_dual_min_num_f32"
  }

  final case class VDualMinNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_min_num_f64")
    override val name: String = "v_dual_min_num_f64"
  }

  final case class VDualMovB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_mov_b32")
    override val name: String = "v_dual_mov_b32"
  }

  final case class VDualMulDx9ZeroF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_mul_dx9_zero_f32")
    override val name: String = "v_dual_mul_dx9_zero_f32"
  }

  final case class VDualMulF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_mul_f32")
    override val name: String = "v_dual_mul_f32"
  }

  final case class VDualMulF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_mul_f64")
    override val name: String = "v_dual_mul_f64"
  }

  final case class VDualSubF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_sub_f32")
    override val name: String = "v_dual_sub_f32"
  }

  final case class VDualSubNcU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_sub_nc_u32")
    override val name: String = "v_dual_sub_nc_u32"
  }

  final case class VDualSubrevF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_dual_subrev_f32")
    override val name: String = "v_dual_subrev_f32"
  }

  final case class VExpBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_exp_bf16")
    override val name: String = "v_exp_bf16"
  }

  final case class VExpF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_exp_f16")
    override val name: String = "v_exp_f16"
  }

  final case class VExpF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_exp_f32")
    override val name: String = "v_exp_f32"
  }

  final case class VExpLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_exp_legacy_f32")
    override val name: String = "v_exp_legacy_f32"
  }

  final case class VFfbhI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ffbh_i32")
    override val name: String = "v_ffbh_i32"
  }

  final case class VFfbhU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ffbh_u32")
    override val name: String = "v_ffbh_u32"
  }

  final case class VFfblB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ffbl_b32")
    override val name: String = "v_ffbl_b32"
  }

  final case class VFloorF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_floor_f16")
    override val name: String = "v_floor_f16"
  }

  final case class VFloorF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_floor_f32")
    override val name: String = "v_floor_f32"
  }

  final case class VFloorF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_floor_f64")
    override val name: String = "v_floor_f64"
  }

  final case class VFmaDx9ZeroF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_dx9_zero_f32")
    override val name: String = "v_fma_dx9_zero_f32"
  }

  final case class VFmaF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_f16")
    override val name: String = "v_fma_f16"
  }

  final case class VFmaF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_f32")
    override val name: String = "v_fma_f32"
  }

  final case class VFmaF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_f64")
    override val name: String = "v_fma_f64"
  }

  final case class VFmaLegacyF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_legacy_f16")
    override val name: String = "v_fma_legacy_f16"
  }

  final case class VFmaLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_legacy_f32")
    override val name: String = "v_fma_legacy_f32"
  }

  final case class VFmaMixF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_mix_f32")
    override val name: String = "v_fma_mix_f32"
  }

  final case class VFmaMixF32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_mix_f32_bf16")
    override val name: String = "v_fma_mix_f32_bf16"
  }

  final case class VFmaMixhiBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_mixhi_bf16")
    override val name: String = "v_fma_mixhi_bf16"
  }

  final case class VFmaMixhiF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_mixhi_f16")
    override val name: String = "v_fma_mixhi_f16"
  }

  final case class VFmaMixloBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_mixlo_bf16")
    override val name: String = "v_fma_mixlo_bf16"
  }

  final case class VFmaMixloF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fma_mixlo_f16")
    override val name: String = "v_fma_mixlo_f16"
  }

  final case class VFmaakF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmaak_f16")
    override val name: String = "v_fmaak_f16"
  }

  final case class VFmaakF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmaak_f32")
    override val name: String = "v_fmaak_f32"
  }

  final case class VFmaakF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmaak_f64")
    override val name: String = "v_fmaak_f64"
  }

  final case class VFmacDx9ZeroF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmac_dx9_zero_f32")
    override val name: String = "v_fmac_dx9_zero_f32"
  }

  final case class VFmacF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmac_f16")
    override val name: String = "v_fmac_f16"
  }

  final case class VFmacF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmac_f32")
    override val name: String = "v_fmac_f32"
  }

  final case class VFmacF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmac_f64")
    override val name: String = "v_fmac_f64"
  }

  final case class VFmacLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmac_legacy_f32")
    override val name: String = "v_fmac_legacy_f32"
  }

  final case class VFmamkF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmamk_f16")
    override val name: String = "v_fmamk_f16"
  }

  final case class VFmamkF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmamk_f32")
    override val name: String = "v_fmamk_f32"
  }

  final case class VFmamkF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fmamk_f64")
    override val name: String = "v_fmamk_f64"
  }

  final case class VFractF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fract_f16")
    override val name: String = "v_fract_f16"
  }

  final case class VFractF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fract_f32")
    override val name: String = "v_fract_f32"
  }

  final case class VFractF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_fract_f64")
    override val name: String = "v_fract_f64"
  }

  final case class VFrexpExpI16F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_frexp_exp_i16_f16")
    override val name: String = "v_frexp_exp_i16_f16"
  }

  final case class VFrexpExpI32F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_frexp_exp_i32_f32")
    override val name: String = "v_frexp_exp_i32_f32"
  }

  final case class VFrexpExpI32F64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_frexp_exp_i32_f64")
    override val name: String = "v_frexp_exp_i32_f64"
  }

  final case class VFrexpMantF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_frexp_mant_f16")
    override val name: String = "v_frexp_mant_f16"
  }

  final case class VFrexpMantF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_frexp_mant_f32")
    override val name: String = "v_frexp_mant_f32"
  }

  final case class VFrexpMantF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_frexp_mant_f64")
    override val name: String = "v_frexp_mant_f64"
  }

  final case class VInterpMovF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_mov_f32")
    override val name: String = "v_interp_mov_f32"
  }

  final case class VInterpP10F16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p10_f16_f32")
    override val name: String = "v_interp_p10_f16_f32"
  }

  final case class VInterpP10F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p10_f32")
    override val name: String = "v_interp_p10_f32"
  }

  final case class VInterpP10RtzF16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p10_rtz_f16_f32")
    override val name: String = "v_interp_p10_rtz_f16_f32"
  }

  final case class VInterpP1F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p1_f32")
    override val name: String = "v_interp_p1_f32"
  }

  final case class VInterpP1llF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p1ll_f16")
    override val name: String = "v_interp_p1ll_f16"
  }

  final case class VInterpP1lvF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p1lv_f16")
    override val name: String = "v_interp_p1lv_f16"
  }

  final case class VInterpP2F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p2_f16")
    override val name: String = "v_interp_p2_f16"
  }

  final case class VInterpP2F16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p2_f16_f32")
    override val name: String = "v_interp_p2_f16_f32"
  }

  final case class VInterpP2F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p2_f32")
    override val name: String = "v_interp_p2_f32"
  }

  final case class VInterpP2HiF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p2_hi_f16")
    override val name: String = "v_interp_p2_hi_f16"
  }

  final case class VInterpP2LegacyF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p2_legacy_f16")
    override val name: String = "v_interp_p2_legacy_f16"
  }

  final case class VInterpP2RtzF16F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_interp_p2_rtz_f16_f32")
    override val name: String = "v_interp_p2_rtz_f16_f32"
  }

  final case class VLdexpF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ldexp_f16")
    override val name: String = "v_ldexp_f16"
  }

  final case class VLdexpF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ldexp_f32")
    override val name: String = "v_ldexp_f32"
  }

  final case class VLdexpF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_ldexp_f64")
    override val name: String = "v_ldexp_f64"
  }

  final case class VLerpU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lerp_u8")
    override val name: String = "v_lerp_u8"
  }

  final case class VLogBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_log_bf16")
    override val name: String = "v_log_bf16"
  }

  final case class VLogClampF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_log_clamp_f32")
    override val name: String = "v_log_clamp_f32"
  }

  final case class VLogF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_log_f16")
    override val name: String = "v_log_f16"
  }

  final case class VLogF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_log_f32")
    override val name: String = "v_log_f32"
  }

  final case class VLogLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_log_legacy_f32")
    override val name: String = "v_log_legacy_f32"
  }

  final case class VLshlAddU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshl_add_u32")
    override val name: String = "v_lshl_add_u32"
  }

  final case class VLshlAddU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshl_add_u64")
    override val name: String = "v_lshl_add_u64"
  }

  final case class VLshlB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshl_b32")
    override val name: String = "v_lshl_b32"
  }

  final case class VLshlB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshl_b64")
    override val name: String = "v_lshl_b64"
  }

  final case class VLshlOrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshl_or_b32")
    override val name: String = "v_lshl_or_b32"
  }

  final case class VLshlrevB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshlrev_b16")
    override val name: String = "v_lshlrev_b16"
  }

  final case class VLshlrevB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshlrev_b32")
    override val name: String = "v_lshlrev_b32"
  }

  final case class VLshlrevB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshlrev_b64")
    override val name: String = "v_lshlrev_b64"
  }

  final case class VLshlrevB64E64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshlrev_b64_e64")
    override val name: String = "v_lshlrev_b64_e64"
  }

  final case class VLshrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshr_b32")
    override val name: String = "v_lshr_b32"
  }

  final case class VLshrB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshr_b64")
    override val name: String = "v_lshr_b64"
  }

  final case class VLshrrevB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshrrev_b16")
    override val name: String = "v_lshrrev_b16"
  }

  final case class VLshrrevB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshrrev_b32")
    override val name: String = "v_lshrrev_b32"
  }

  final case class VLshrrevB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_lshrrev_b64")
    override val name: String = "v_lshrrev_b64"
  }

  final case class VMacF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mac_f16")
    override val name: String = "v_mac_f16"
  }

  final case class VMacF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mac_f32")
    override val name: String = "v_mac_f32"
  }

  final case class VMacLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mac_legacy_f32")
    override val name: String = "v_mac_legacy_f32"
  }

  final case class VMadCoI64I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_co_i64_i32")
    override val name: String = "v_mad_co_i64_i32"
  }

  final case class VMadCoU64U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_co_u64_u32")
    override val name: String = "v_mad_co_u64_u32"
  }

  final case class VMadF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_f16")
    override val name: String = "v_mad_f16"
  }

  final case class VMadF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_f32")
    override val name: String = "v_mad_f32"
  }

  final case class VMadI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_i16")
    override val name: String = "v_mad_i16"
  }

  final case class VMadI32I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_i32_i16")
    override val name: String = "v_mad_i32_i16"
  }

  final case class VMadI32I24(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_i32_i24")
    override val name: String = "v_mad_i32_i24"
  }

  final case class VMadI64I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_i64_i32")
    override val name: String = "v_mad_i64_i32"
  }

  final case class VMadLegacyF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_legacy_f16")
    override val name: String = "v_mad_legacy_f16"
  }

  final case class VMadLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_legacy_f32")
    override val name: String = "v_mad_legacy_f32"
  }

  final case class VMadLegacyI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_legacy_i16")
    override val name: String = "v_mad_legacy_i16"
  }

  final case class VMadLegacyU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_legacy_u16")
    override val name: String = "v_mad_legacy_u16"
  }

  final case class VMadMixF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_mix_f32")
    override val name: String = "v_mad_mix_f32"
  }

  final case class VMadMixhiF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_mixhi_f16")
    override val name: String = "v_mad_mixhi_f16"
  }

  final case class VMadMixloF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_mixlo_f16")
    override val name: String = "v_mad_mixlo_f16"
  }

  final case class VMadNcI64I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_nc_i64_i32")
    override val name: String = "v_mad_nc_i64_i32"
  }

  final case class VMadNcU64U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_nc_u64_u32")
    override val name: String = "v_mad_nc_u64_u32"
  }

  final case class VMadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_u16")
    override val name: String = "v_mad_u16"
  }

  final case class VMadU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_u32")
    override val name: String = "v_mad_u32"
  }

  final case class VMadU32U16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_u32_u16")
    override val name: String = "v_mad_u32_u16"
  }

  final case class VMadU32U24(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_u32_u24")
    override val name: String = "v_mad_u32_u24"
  }

  final case class VMadU64U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mad_u64_u32")
    override val name: String = "v_mad_u64_u32"
  }

  final case class VMadakF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_madak_f16")
    override val name: String = "v_madak_f16"
  }

  final case class VMadakF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_madak_f32")
    override val name: String = "v_madak_f32"
  }

  final case class VMadmkF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_madmk_f16")
    override val name: String = "v_madmk_f16"
  }

  final case class VMadmkF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_madmk_f32")
    override val name: String = "v_madmk_f32"
  }

  final case class VMax3F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max3_f16")
    override val name: String = "v_max3_f16"
  }

  final case class VMax3F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max3_f32")
    override val name: String = "v_max3_f32"
  }

  final case class VMax3I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max3_i16")
    override val name: String = "v_max3_i16"
  }

  final case class VMax3I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max3_i32")
    override val name: String = "v_max3_i32"
  }

  final case class VMax3NumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max3_num_f16")
    override val name: String = "v_max3_num_f16"
  }

  final case class VMax3NumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max3_num_f32")
    override val name: String = "v_max3_num_f32"
  }

  final case class VMax3U16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max3_u16")
    override val name: String = "v_max3_u16"
  }

  final case class VMax3U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max3_u32")
    override val name: String = "v_max3_u32"
  }

  final case class VMaxF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_f16")
    override val name: String = "v_max_f16"
  }

  final case class VMaxF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_f32")
    override val name: String = "v_max_f32"
  }

  final case class VMaxF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_f64")
    override val name: String = "v_max_f64"
  }

  final case class VMaxF64E64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_f64_e64")
    override val name: String = "v_max_f64_e64"
  }

  final case class VMaxI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_i16")
    override val name: String = "v_max_i16"
  }

  final case class VMaxI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_i32")
    override val name: String = "v_max_i32"
  }

  final case class VMaxI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_i64")
    override val name: String = "v_max_i64"
  }

  final case class VMaxLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_legacy_f32")
    override val name: String = "v_max_legacy_f32"
  }

  final case class VMaxNumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_num_f16")
    override val name: String = "v_max_num_f16"
  }

  final case class VMaxNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_num_f32")
    override val name: String = "v_max_num_f32"
  }

  final case class VMaxNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_num_f64")
    override val name: String = "v_max_num_f64"
  }

  final case class VMaxU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_u16")
    override val name: String = "v_max_u16"
  }

  final case class VMaxU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_u32")
    override val name: String = "v_max_u32"
  }

  final case class VMaxU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_max_u64")
    override val name: String = "v_max_u64"
  }

  final case class VMaximum3F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maximum3_f16")
    override val name: String = "v_maximum3_f16"
  }

  final case class VMaximum3F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maximum3_f32")
    override val name: String = "v_maximum3_f32"
  }

  final case class VMaximumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maximum_f16")
    override val name: String = "v_maximum_f16"
  }

  final case class VMaximumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maximum_f32")
    override val name: String = "v_maximum_f32"
  }

  final case class VMaximumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maximum_f64")
    override val name: String = "v_maximum_f64"
  }

  final case class VMaximumminimumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maximumminimum_f16")
    override val name: String = "v_maximumminimum_f16"
  }

  final case class VMaximumminimumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maximumminimum_f32")
    override val name: String = "v_maximumminimum_f32"
  }

  final case class VMaxminF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maxmin_f16")
    override val name: String = "v_maxmin_f16"
  }

  final case class VMaxminF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maxmin_f32")
    override val name: String = "v_maxmin_f32"
  }

  final case class VMaxminI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maxmin_i32")
    override val name: String = "v_maxmin_i32"
  }

  final case class VMaxminNumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maxmin_num_f16")
    override val name: String = "v_maxmin_num_f16"
  }

  final case class VMaxminNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maxmin_num_f32")
    override val name: String = "v_maxmin_num_f32"
  }

  final case class VMaxminU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_maxmin_u32")
    override val name: String = "v_maxmin_u32"
  }

  final case class VMbcntHiU32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mbcnt_hi_u32_b32")
    override val name: String = "v_mbcnt_hi_u32_b32"
  }

  final case class VMbcntHiU32B32E64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mbcnt_hi_u32_b32_e64")
    override val name: String = "v_mbcnt_hi_u32_b32_e64"
  }

  final case class VMbcntLoU32B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mbcnt_lo_u32_b32")
    override val name: String = "v_mbcnt_lo_u32_b32"
  }

  final case class VMed3F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_med3_f16")
    override val name: String = "v_med3_f16"
  }

  final case class VMed3F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_med3_f32")
    override val name: String = "v_med3_f32"
  }

  final case class VMed3I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_med3_i16")
    override val name: String = "v_med3_i16"
  }

  final case class VMed3I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_med3_i32")
    override val name: String = "v_med3_i32"
  }

  final case class VMed3NumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_med3_num_f16")
    override val name: String = "v_med3_num_f16"
  }

  final case class VMed3NumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_med3_num_f32")
    override val name: String = "v_med3_num_f32"
  }

  final case class VMed3U16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_med3_u16")
    override val name: String = "v_med3_u16"
  }

  final case class VMed3U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_med3_u32")
    override val name: String = "v_med3_u32"
  }

  final case class VMfmaF3216x16x128F8f6f4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x128_f8f6f4")
    override val name: String = "v_mfma_f32_16x16x128_f8f6f4"
  }

  final case class VMfmaF3216x16x16Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x16_bf16")
    override val name: String = "v_mfma_f32_16x16x16_bf16"
  }

  final case class VMfmaF3216x16x16F16_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x16_f16")
    override val name: String = "v_mfma_f32_16x16x16_f16"
  }

  final case class VMfmaF3216x16x16bf161k(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x16bf16_1k")
    override val name: String = "v_mfma_f32_16x16x16bf16_1k"
  }

  final case class VMfmaF3216x16x16f16_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x16f16")
    override val name: String = "v_mfma_f32_16x16x16f16"
  }

  final case class VMfmaF3216x16x14bF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x1_4b_f32")
    override val name: String = "v_mfma_f32_16x16x1_4b_f32"
  }

  final case class VMfmaF3216x16x1f32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x1f32")
    override val name: String = "v_mfma_f32_16x16x1f32"
  }

  final case class VMfmaF3216x16x2bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x2bf16")
    override val name: String = "v_mfma_f32_16x16x2bf16"
  }

  final case class VMfmaF3216x16x32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x32_bf16")
    override val name: String = "v_mfma_f32_16x16x32_bf16"
  }

  final case class VMfmaF3216x16x32Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x32_bf8_bf8")
    override val name: String = "v_mfma_f32_16x16x32_bf8_bf8"
  }

  final case class VMfmaF3216x16x32Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x32_bf8_fp8")
    override val name: String = "v_mfma_f32_16x16x32_bf8_fp8"
  }

  final case class VMfmaF3216x16x32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x32_f16")
    override val name: String = "v_mfma_f32_16x16x32_f16"
  }

  final case class VMfmaF3216x16x32Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x32_fp8_bf8")
    override val name: String = "v_mfma_f32_16x16x32_fp8_bf8"
  }

  final case class VMfmaF3216x16x32Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x32_fp8_fp8")
    override val name: String = "v_mfma_f32_16x16x32_fp8_fp8"
  }

  final case class VMfmaF3216x16x44bBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x4_4b_bf16")
    override val name: String = "v_mfma_f32_16x16x4_4b_bf16"
  }

  final case class VMfmaF3216x16x44bF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x4_4b_f16")
    override val name: String = "v_mfma_f32_16x16x4_4b_f16"
  }

  final case class VMfmaF3216x16x4F32_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x4_f32")
    override val name: String = "v_mfma_f32_16x16x4_f32"
  }

  final case class VMfmaF3216x16x4bf161k(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x4bf16_1k")
    override val name: String = "v_mfma_f32_16x16x4bf16_1k"
  }

  final case class VMfmaF3216x16x4f16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x4f16")
    override val name: String = "v_mfma_f32_16x16x4f16"
  }

  final case class VMfmaF3216x16x4f32_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x4f32")
    override val name: String = "v_mfma_f32_16x16x4f32"
  }

  final case class VMfmaF3216x16x8Xf32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x8_xf32")
    override val name: String = "v_mfma_f32_16x16x8_xf32"
  }

  final case class VMfmaF3216x16x8bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_16x16x8bf16")
    override val name: String = "v_mfma_f32_16x16x8bf16"
  }

  final case class VMfmaF3232x32x16Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x16_bf16")
    override val name: String = "v_mfma_f32_32x32x16_bf16"
  }

  final case class VMfmaF3232x32x16Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x16_bf8_bf8")
    override val name: String = "v_mfma_f32_32x32x16_bf8_bf8"
  }

  final case class VMfmaF3232x32x16Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x16_bf8_fp8")
    override val name: String = "v_mfma_f32_32x32x16_bf8_fp8"
  }

  final case class VMfmaF3232x32x16F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x16_f16")
    override val name: String = "v_mfma_f32_32x32x16_f16"
  }

  final case class VMfmaF3232x32x16Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x16_fp8_bf8")
    override val name: String = "v_mfma_f32_32x32x16_fp8_bf8"
  }

  final case class VMfmaF3232x32x16Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x16_fp8_fp8")
    override val name: String = "v_mfma_f32_32x32x16_fp8_fp8"
  }

  final case class VMfmaF3232x32x12bF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x1_2b_f32")
    override val name: String = "v_mfma_f32_32x32x1_2b_f32"
  }

  final case class VMfmaF3232x32x1f32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x1f32")
    override val name: String = "v_mfma_f32_32x32x1f32"
  }

  final case class VMfmaF3232x32x2F32_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x2_f32")
    override val name: String = "v_mfma_f32_32x32x2_f32"
  }

  final case class VMfmaF3232x32x2bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x2bf16")
    override val name: String = "v_mfma_f32_32x32x2bf16"
  }

  final case class VMfmaF3232x32x2f32_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x2f32")
    override val name: String = "v_mfma_f32_32x32x2f32"
  }

  final case class VMfmaF3232x32x42bBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x4_2b_bf16")
    override val name: String = "v_mfma_f32_32x32x4_2b_bf16"
  }

  final case class VMfmaF3232x32x42bF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x4_2b_f16")
    override val name: String = "v_mfma_f32_32x32x4_2b_f16"
  }

  final case class VMfmaF3232x32x4Xf32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x4_xf32")
    override val name: String = "v_mfma_f32_32x32x4_xf32"
  }

  final case class VMfmaF3232x32x4bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x4bf16")
    override val name: String = "v_mfma_f32_32x32x4bf16"
  }

  final case class VMfmaF3232x32x4bf161k(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x4bf16_1k")
    override val name: String = "v_mfma_f32_32x32x4bf16_1k"
  }

  final case class VMfmaF3232x32x4f16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x4f16")
    override val name: String = "v_mfma_f32_32x32x4f16"
  }

  final case class VMfmaF3232x32x64F8f6f4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x64_f8f6f4")
    override val name: String = "v_mfma_f32_32x32x64_f8f6f4"
  }

  final case class VMfmaF3232x32x8Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x8_bf16")
    override val name: String = "v_mfma_f32_32x32x8_bf16"
  }

  final case class VMfmaF3232x32x8F16_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x8_f16")
    override val name: String = "v_mfma_f32_32x32x8_f16"
  }

  final case class VMfmaF3232x32x8bf161k(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x8bf16_1k")
    override val name: String = "v_mfma_f32_32x32x8bf16_1k"
  }

  final case class VMfmaF3232x32x8f16_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_32x32x8f16")
    override val name: String = "v_mfma_f32_32x32x8f16"
  }

  final case class VMfmaF324x4x116bF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_4x4x1_16b_f32")
    override val name: String = "v_mfma_f32_4x4x1_16b_f32"
  }

  final case class VMfmaF324x4x1f32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_4x4x1f32")
    override val name: String = "v_mfma_f32_4x4x1f32"
  }

  final case class VMfmaF324x4x2bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_4x4x2bf16")
    override val name: String = "v_mfma_f32_4x4x2bf16"
  }

  final case class VMfmaF324x4x416bBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_4x4x4_16b_bf16")
    override val name: String = "v_mfma_f32_4x4x4_16b_bf16"
  }

  final case class VMfmaF324x4x416bF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_4x4x4_16b_f16")
    override val name: String = "v_mfma_f32_4x4x4_16b_f16"
  }

  final case class VMfmaF324x4x4bf161k(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_4x4x4bf16_1k")
    override val name: String = "v_mfma_f32_4x4x4bf16_1k"
  }

  final case class VMfmaF324x4x4f16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f32_4x4x4f16")
    override val name: String = "v_mfma_f32_4x4x4f16"
  }

  final case class VMfmaF6416x16x4F64_1(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f64_16x16x4_f64")
    override val name: String = "v_mfma_f64_16x16x4_f64"
  }

  final case class VMfmaF6416x16x4f64_2(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f64_16x16x4f64")
    override val name: String = "v_mfma_f64_16x16x4f64"
  }

  final case class VMfmaF644x4x44bF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f64_4x4x4_4b_f64")
    override val name: String = "v_mfma_f64_4x4x4_4b_f64"
  }

  final case class VMfmaF644x4x4f64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_f64_4x4x4f64")
    override val name: String = "v_mfma_f64_4x4x4f64"
  }

  final case class VMfmaI3216x16x16i8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_16x16x16i8")
    override val name: String = "v_mfma_i32_16x16x16i8"
  }

  final case class VMfmaI3216x16x32I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_16x16x32_i8")
    override val name: String = "v_mfma_i32_16x16x32_i8"
  }

  final case class VMfmaI3216x16x44bI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_16x16x4_4b_i8")
    override val name: String = "v_mfma_i32_16x16x4_4b_i8"
  }

  final case class VMfmaI3216x16x4i8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_16x16x4i8")
    override val name: String = "v_mfma_i32_16x16x4i8"
  }

  final case class VMfmaI3216x16x64I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_16x16x64_i8")
    override val name: String = "v_mfma_i32_16x16x64_i8"
  }

  final case class VMfmaI3232x32x16I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_32x32x16_i8")
    override val name: String = "v_mfma_i32_32x32x16_i8"
  }

  final case class VMfmaI3232x32x32I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_32x32x32_i8")
    override val name: String = "v_mfma_i32_32x32x32_i8"
  }

  final case class VMfmaI3232x32x42bI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_32x32x4_2b_i8")
    override val name: String = "v_mfma_i32_32x32x4_2b_i8"
  }

  final case class VMfmaI3232x32x4i8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_32x32x4i8")
    override val name: String = "v_mfma_i32_32x32x4i8"
  }

  final case class VMfmaI3232x32x8i8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_32x32x8i8")
    override val name: String = "v_mfma_i32_32x32x8i8"
  }

  final case class VMfmaI324x4x416bI8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_4x4x4_16b_i8")
    override val name: String = "v_mfma_i32_4x4x4_16b_i8"
  }

  final case class VMfmaI324x4x4i8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_i32_4x4x4i8")
    override val name: String = "v_mfma_i32_4x4x4i8"
  }

  final case class VMfmaScaleF3216x16x128F8f6f4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_scale_f32_16x16x128_f8f6f4")
    override val name: String = "v_mfma_scale_f32_16x16x128_f8f6f4"
  }

  final case class VMfmaScaleF3232x32x64F8f6f4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mfma_scale_f32_32x32x64_f8f6f4")
    override val name: String = "v_mfma_scale_f32_32x32x64_f8f6f4"
  }

  final case class VMin3F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min3_f16")
    override val name: String = "v_min3_f16"
  }

  final case class VMin3F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min3_f32")
    override val name: String = "v_min3_f32"
  }

  final case class VMin3I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min3_i16")
    override val name: String = "v_min3_i16"
  }

  final case class VMin3I32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min3_i32")
    override val name: String = "v_min3_i32"
  }

  final case class VMin3NumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min3_num_f16")
    override val name: String = "v_min3_num_f16"
  }

  final case class VMin3NumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min3_num_f32")
    override val name: String = "v_min3_num_f32"
  }

  final case class VMin3U16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min3_u16")
    override val name: String = "v_min3_u16"
  }

  final case class VMin3U32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min3_u32")
    override val name: String = "v_min3_u32"
  }

  final case class VMinF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_f16")
    override val name: String = "v_min_f16"
  }

  final case class VMinF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_f32")
    override val name: String = "v_min_f32"
  }

  final case class VMinF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_f64")
    override val name: String = "v_min_f64"
  }

  final case class VMinF64E64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_f64_e64")
    override val name: String = "v_min_f64_e64"
  }

  final case class VMinI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_i16")
    override val name: String = "v_min_i16"
  }

  final case class VMinI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_i32")
    override val name: String = "v_min_i32"
  }

  final case class VMinI64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_i64")
    override val name: String = "v_min_i64"
  }

  final case class VMinLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_legacy_f32")
    override val name: String = "v_min_legacy_f32"
  }

  final case class VMinNumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_num_f16")
    override val name: String = "v_min_num_f16"
  }

  final case class VMinNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_num_f32")
    override val name: String = "v_min_num_f32"
  }

  final case class VMinNumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_num_f64")
    override val name: String = "v_min_num_f64"
  }

  final case class VMinU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_u16")
    override val name: String = "v_min_u16"
  }

  final case class VMinU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_u32")
    override val name: String = "v_min_u32"
  }

  final case class VMinU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_min_u64")
    override val name: String = "v_min_u64"
  }

  final case class VMinimum3F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minimum3_f16")
    override val name: String = "v_minimum3_f16"
  }

  final case class VMinimum3F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minimum3_f32")
    override val name: String = "v_minimum3_f32"
  }

  final case class VMinimumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minimum_f16")
    override val name: String = "v_minimum_f16"
  }

  final case class VMinimumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minimum_f32")
    override val name: String = "v_minimum_f32"
  }

  final case class VMinimumF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minimum_f64")
    override val name: String = "v_minimum_f64"
  }

  final case class VMinimummaximumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minimummaximum_f16")
    override val name: String = "v_minimummaximum_f16"
  }

  final case class VMinimummaximumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minimummaximum_f32")
    override val name: String = "v_minimummaximum_f32"
  }

  final case class VMinmaxF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minmax_f16")
    override val name: String = "v_minmax_f16"
  }

  final case class VMinmaxF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minmax_f32")
    override val name: String = "v_minmax_f32"
  }

  final case class VMinmaxI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minmax_i32")
    override val name: String = "v_minmax_i32"
  }

  final case class VMinmaxNumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minmax_num_f16")
    override val name: String = "v_minmax_num_f16"
  }

  final case class VMinmaxNumF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minmax_num_f32")
    override val name: String = "v_minmax_num_f32"
  }

  final case class VMinmaxU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_minmax_u32")
    override val name: String = "v_minmax_u32"
  }

  final case class VMovB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mov_b16")
    override val name: String = "v_mov_b16"
  }

  final case class VMovB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mov_b32")
    override val name: String = "v_mov_b32"
  }

  final case class VMovB64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mov_b64")
    override val name: String = "v_mov_b64"
  }

  final case class VMovreldB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_movreld_b32")
    override val name: String = "v_movreld_b32"
  }

  final case class VMovrelsB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_movrels_b32")
    override val name: String = "v_movrels_b32"
  }

  final case class VMovrelsd2B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_movrelsd_2_b32")
    override val name: String = "v_movrelsd_2_b32"
  }

  final case class VMovrelsdB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_movrelsd_b32")
    override val name: String = "v_movrelsd_b32"
  }

  final case class VMqsadPkU16U8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mqsad_pk_u16_u8")
    override val name: String = "v_mqsad_pk_u16_u8"
  }

  final case class VMqsadU32U8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mqsad_u32_u8")
    override val name: String = "v_mqsad_u32_u8"
  }

  final case class VMsadU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_msad_u8")
    override val name: String = "v_msad_u8"
  }

  final case class VMulDx9ZeroF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_dx9_zero_f32")
    override val name: String = "v_mul_dx9_zero_f32"
  }

  final case class VMulF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_f16")
    override val name: String = "v_mul_f16"
  }

  final case class VMulF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_f32")
    override val name: String = "v_mul_f32"
  }

  final case class VMulF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_f64")
    override val name: String = "v_mul_f64"
  }

  final case class VMulF64E64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_f64_e64")
    override val name: String = "v_mul_f64_e64"
  }

  final case class VMulHiI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_hi_i32")
    override val name: String = "v_mul_hi_i32"
  }

  final case class VMulHiI32I24(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_hi_i32_i24")
    override val name: String = "v_mul_hi_i32_i24"
  }

  final case class VMulHiU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_hi_u32")
    override val name: String = "v_mul_hi_u32"
  }

  final case class VMulHiU32U24(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_hi_u32_u24")
    override val name: String = "v_mul_hi_u32_u24"
  }

  final case class VMulI32I24(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_i32_i24")
    override val name: String = "v_mul_i32_i24"
  }

  final case class VMulLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_legacy_f32")
    override val name: String = "v_mul_legacy_f32"
  }

  final case class VMulLoI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_lo_i32")
    override val name: String = "v_mul_lo_i32"
  }

  final case class VMulLoU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_lo_u16")
    override val name: String = "v_mul_lo_u16"
  }

  final case class VMulLoU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_lo_u32")
    override val name: String = "v_mul_lo_u32"
  }

  final case class VMulU32U24(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_u32_u24")
    override val name: String = "v_mul_u32_u24"
  }

  final case class VMulU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mul_u64")
    override val name: String = "v_mul_u64"
  }

  final case class VMullitF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_mullit_f32")
    override val name: String = "v_mullit_f32"
  }

  final case class VNop(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_nop")
    override val name: String = "v_nop"
  }

  final case class VNotB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_not_b16")
    override val name: String = "v_not_b16"
  }

  final case class VNotB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_not_b32")
    override val name: String = "v_not_b32"
  }

  final case class VOr3B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_or3_b32")
    override val name: String = "v_or3_b32"
  }

  final case class VOrB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_or_b16")
    override val name: String = "v_or_b16"
  }

  final case class VOrB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_or_b32")
    override val name: String = "v_or_b32"
  }

  final case class VPackB32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pack_b32_f16")
    override val name: String = "v_pack_b32_f16"
  }

  final case class VPermB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_perm_b32")
    override val name: String = "v_perm_b32"
  }

  final case class VPermPk16B4U4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_perm_pk16_b4_u4")
    override val name: String = "v_perm_pk16_b4_u4"
  }

  final case class VPermPk16B6U4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_perm_pk16_b6_u4")
    override val name: String = "v_perm_pk16_b6_u4"
  }

  final case class VPermPk16B8U4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_perm_pk16_b8_u4")
    override val name: String = "v_perm_pk16_b8_u4"
  }

  final case class VPermlane16B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlane16_b32")
    override val name: String = "v_permlane16_b32"
  }

  final case class VPermlane16SwapB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlane16_swap_b32")
    override val name: String = "v_permlane16_swap_b32"
  }

  final case class VPermlane16VarB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlane16_var_b32")
    override val name: String = "v_permlane16_var_b32"
  }

  final case class VPermlane32SwapB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlane32_swap_b32")
    override val name: String = "v_permlane32_swap_b32"
  }

  final case class VPermlane64B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlane64_b32")
    override val name: String = "v_permlane64_b32"
  }

  final case class VPermlaneBcastB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlane_bcast_b32")
    override val name: String = "v_permlane_bcast_b32"
  }

  final case class VPermlaneDownB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlane_down_b32")
    override val name: String = "v_permlane_down_b32"
  }

  final case class VPermlaneIdxGenB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlane_idx_gen_b32")
    override val name: String = "v_permlane_idx_gen_b32"
  }

  final case class VPermlaneUpB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlane_up_b32")
    override val name: String = "v_permlane_up_b32"
  }

  final case class VPermlaneXorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlane_xor_b32")
    override val name: String = "v_permlane_xor_b32"
  }

  final case class VPermlanex16B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlanex16_b32")
    override val name: String = "v_permlanex16_b32"
  }

  final case class VPermlanex16VarB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_permlanex16_var_b32")
    override val name: String = "v_permlanex16_var_b32"
  }

  final case class VPipeflush(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pipeflush")
    override val name: String = "v_pipeflush"
  }

  final case class VPkAddBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_add_bf16")
    override val name: String = "v_pk_add_bf16"
  }

  final case class VPkAddF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_add_f16")
    override val name: String = "v_pk_add_f16"
  }

  final case class VPkAddF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_add_f32")
    override val name: String = "v_pk_add_f32"
  }

  final case class VPkAddI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_add_i16")
    override val name: String = "v_pk_add_i16"
  }

  final case class VPkAddMaxI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_add_max_i16")
    override val name: String = "v_pk_add_max_i16"
  }

  final case class VPkAddMaxU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_add_max_u16")
    override val name: String = "v_pk_add_max_u16"
  }

  final case class VPkAddMinI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_add_min_i16")
    override val name: String = "v_pk_add_min_i16"
  }

  final case class VPkAddMinU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_add_min_u16")
    override val name: String = "v_pk_add_min_u16"
  }

  final case class VPkAddU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_add_u16")
    override val name: String = "v_pk_add_u16"
  }

  final case class VPkAshrrevI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_ashrrev_i16")
    override val name: String = "v_pk_ashrrev_i16"
  }

  final case class VPkFmaBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_fma_bf16")
    override val name: String = "v_pk_fma_bf16"
  }

  final case class VPkFmaF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_fma_f16")
    override val name: String = "v_pk_fma_f16"
  }

  final case class VPkFmaF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_fma_f32")
    override val name: String = "v_pk_fma_f32"
  }

  final case class VPkFmacF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_fmac_f16")
    override val name: String = "v_pk_fmac_f16"
  }

  final case class VPkLshlrevB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_lshlrev_b16")
    override val name: String = "v_pk_lshlrev_b16"
  }

  final case class VPkLshrrevB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_lshrrev_b16")
    override val name: String = "v_pk_lshrrev_b16"
  }

  final case class VPkMadI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_mad_i16")
    override val name: String = "v_pk_mad_i16"
  }

  final case class VPkMadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_mad_u16")
    override val name: String = "v_pk_mad_u16"
  }

  final case class VPkMax3I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_max3_i16")
    override val name: String = "v_pk_max3_i16"
  }

  final case class VPkMax3NumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_max3_num_f16")
    override val name: String = "v_pk_max3_num_f16"
  }

  final case class VPkMax3U16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_max3_u16")
    override val name: String = "v_pk_max3_u16"
  }

  final case class VPkMaxF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_max_f16")
    override val name: String = "v_pk_max_f16"
  }

  final case class VPkMaxI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_max_i16")
    override val name: String = "v_pk_max_i16"
  }

  final case class VPkMaxNumBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_max_num_bf16")
    override val name: String = "v_pk_max_num_bf16"
  }

  final case class VPkMaxNumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_max_num_f16")
    override val name: String = "v_pk_max_num_f16"
  }

  final case class VPkMaxU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_max_u16")
    override val name: String = "v_pk_max_u16"
  }

  final case class VPkMaximum3F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_maximum3_f16")
    override val name: String = "v_pk_maximum3_f16"
  }

  final case class VPkMaximumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_maximum_f16")
    override val name: String = "v_pk_maximum_f16"
  }

  final case class VPkMin3I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_min3_i16")
    override val name: String = "v_pk_min3_i16"
  }

  final case class VPkMin3NumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_min3_num_f16")
    override val name: String = "v_pk_min3_num_f16"
  }

  final case class VPkMin3U16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_min3_u16")
    override val name: String = "v_pk_min3_u16"
  }

  final case class VPkMinF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_min_f16")
    override val name: String = "v_pk_min_f16"
  }

  final case class VPkMinI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_min_i16")
    override val name: String = "v_pk_min_i16"
  }

  final case class VPkMinNumBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_min_num_bf16")
    override val name: String = "v_pk_min_num_bf16"
  }

  final case class VPkMinNumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_min_num_f16")
    override val name: String = "v_pk_min_num_f16"
  }

  final case class VPkMinU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_min_u16")
    override val name: String = "v_pk_min_u16"
  }

  final case class VPkMinimum3F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_minimum3_f16")
    override val name: String = "v_pk_minimum3_f16"
  }

  final case class VPkMinimumF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_minimum_f16")
    override val name: String = "v_pk_minimum_f16"
  }

  final case class VPkMovB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_mov_b32")
    override val name: String = "v_pk_mov_b32"
  }

  final case class VPkMulBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_mul_bf16")
    override val name: String = "v_pk_mul_bf16"
  }

  final case class VPkMulF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_mul_f16")
    override val name: String = "v_pk_mul_f16"
  }

  final case class VPkMulF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_mul_f32")
    override val name: String = "v_pk_mul_f32"
  }

  final case class VPkMulLoU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_mul_lo_u16")
    override val name: String = "v_pk_mul_lo_u16"
  }

  final case class VPkSubI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_sub_i16")
    override val name: String = "v_pk_sub_i16"
  }

  final case class VPkSubU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_pk_sub_u16")
    override val name: String = "v_pk_sub_u16"
  }

  final case class VPrngB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_prng_b32")
    override val name: String = "v_prng_b32"
  }

  final case class VQsadPkU16U8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_qsad_pk_u16_u8")
    override val name: String = "v_qsad_pk_u16_u8"
  }

  final case class VRcpBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rcp_bf16")
    override val name: String = "v_rcp_bf16"
  }

  final case class VRcpClampF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rcp_clamp_f32")
    override val name: String = "v_rcp_clamp_f32"
  }

  final case class VRcpClampF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rcp_clamp_f64")
    override val name: String = "v_rcp_clamp_f64"
  }

  final case class VRcpF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rcp_f16")
    override val name: String = "v_rcp_f16"
  }

  final case class VRcpF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rcp_f32")
    override val name: String = "v_rcp_f32"
  }

  final case class VRcpF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rcp_f64")
    override val name: String = "v_rcp_f64"
  }

  final case class VRcpIflagF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rcp_iflag_f32")
    override val name: String = "v_rcp_iflag_f32"
  }

  final case class VRcpLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rcp_legacy_f32")
    override val name: String = "v_rcp_legacy_f32"
  }

  final case class VReadfirstlaneB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_readfirstlane_b32")
    override val name: String = "v_readfirstlane_b32"
  }

  final case class VReadlaneB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_readlane_b32")
    override val name: String = "v_readlane_b32"
  }

  final case class VReadlaneB32E64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_readlane_b32_e64")
    override val name: String = "v_readlane_b32_e64"
  }

  final case class VRndneF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rndne_f16")
    override val name: String = "v_rndne_f16"
  }

  final case class VRndneF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rndne_f32")
    override val name: String = "v_rndne_f32"
  }

  final case class VRndneF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rndne_f64")
    override val name: String = "v_rndne_f64"
  }

  final case class VRsqBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rsq_bf16")
    override val name: String = "v_rsq_bf16"
  }

  final case class VRsqClampF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rsq_clamp_f32")
    override val name: String = "v_rsq_clamp_f32"
  }

  final case class VRsqClampF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rsq_clamp_f64")
    override val name: String = "v_rsq_clamp_f64"
  }

  final case class VRsqF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rsq_f16")
    override val name: String = "v_rsq_f16"
  }

  final case class VRsqF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rsq_f32")
    override val name: String = "v_rsq_f32"
  }

  final case class VRsqF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rsq_f64")
    override val name: String = "v_rsq_f64"
  }

  final case class VRsqLegacyF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_rsq_legacy_f32")
    override val name: String = "v_rsq_legacy_f32"
  }

  final case class VSExpF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_s_exp_f16")
    override val name: String = "v_s_exp_f16"
  }

  final case class VSExpF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_s_exp_f32")
    override val name: String = "v_s_exp_f32"
  }

  final case class VSLogF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_s_log_f16")
    override val name: String = "v_s_log_f16"
  }

  final case class VSLogF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_s_log_f32")
    override val name: String = "v_s_log_f32"
  }

  final case class VSRcpF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_s_rcp_f16")
    override val name: String = "v_s_rcp_f16"
  }

  final case class VSRcpF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_s_rcp_f32")
    override val name: String = "v_s_rcp_f32"
  }

  final case class VSRsqF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_s_rsq_f16")
    override val name: String = "v_s_rsq_f16"
  }

  final case class VSRsqF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_s_rsq_f32")
    override val name: String = "v_s_rsq_f32"
  }

  final case class VSSqrtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_s_sqrt_f16")
    override val name: String = "v_s_sqrt_f16"
  }

  final case class VSSqrtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_s_sqrt_f32")
    override val name: String = "v_s_sqrt_f32"
  }

  final case class VSadHiU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sad_hi_u8")
    override val name: String = "v_sad_hi_u8"
  }

  final case class VSadU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sad_u16")
    override val name: String = "v_sad_u16"
  }

  final case class VSadU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sad_u32")
    override val name: String = "v_sad_u32"
  }

  final case class VSadU8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sad_u8")
    override val name: String = "v_sad_u8"
  }

  final case class VSatPk4I4I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sat_pk4_i4_i8")
    override val name: String = "v_sat_pk4_i4_i8"
  }

  final case class VSatPk4U4U8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sat_pk4_u4_u8")
    override val name: String = "v_sat_pk4_u4_u8"
  }

  final case class VSatPkU8I16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sat_pk_u8_i16")
    override val name: String = "v_sat_pk_u8_i16"
  }

  final case class VScreenPartition4seB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_screen_partition_4se_b32")
    override val name: String = "v_screen_partition_4se_b32"
  }

  final case class VSinBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sin_bf16")
    override val name: String = "v_sin_bf16"
  }

  final case class VSinF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sin_f16")
    override val name: String = "v_sin_f16"
  }

  final case class VSinF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sin_f32")
    override val name: String = "v_sin_f32"
  }

  final case class VSmfmacF3216x16x128Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x128_bf8_bf8")
    override val name: String = "v_smfmac_f32_16x16x128_bf8_bf8"
  }

  final case class VSmfmacF3216x16x128Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x128_bf8_fp8")
    override val name: String = "v_smfmac_f32_16x16x128_bf8_fp8"
  }

  final case class VSmfmacF3216x16x128Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x128_fp8_bf8")
    override val name: String = "v_smfmac_f32_16x16x128_fp8_bf8"
  }

  final case class VSmfmacF3216x16x128Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x128_fp8_fp8")
    override val name: String = "v_smfmac_f32_16x16x128_fp8_fp8"
  }

  final case class VSmfmacF3216x16x32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x32_bf16")
    override val name: String = "v_smfmac_f32_16x16x32_bf16"
  }

  final case class VSmfmacF3216x16x32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x32_f16")
    override val name: String = "v_smfmac_f32_16x16x32_f16"
  }

  final case class VSmfmacF3216x16x64Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x64_bf16")
    override val name: String = "v_smfmac_f32_16x16x64_bf16"
  }

  final case class VSmfmacF3216x16x64Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x64_bf8_bf8")
    override val name: String = "v_smfmac_f32_16x16x64_bf8_bf8"
  }

  final case class VSmfmacF3216x16x64Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x64_bf8_fp8")
    override val name: String = "v_smfmac_f32_16x16x64_bf8_fp8"
  }

  final case class VSmfmacF3216x16x64F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x64_f16")
    override val name: String = "v_smfmac_f32_16x16x64_f16"
  }

  final case class VSmfmacF3216x16x64Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x64_fp8_bf8")
    override val name: String = "v_smfmac_f32_16x16x64_fp8_bf8"
  }

  final case class VSmfmacF3216x16x64Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_16x16x64_fp8_fp8")
    override val name: String = "v_smfmac_f32_16x16x64_fp8_fp8"
  }

  final case class VSmfmacF3232x32x16Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x16_bf16")
    override val name: String = "v_smfmac_f32_32x32x16_bf16"
  }

  final case class VSmfmacF3232x32x16F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x16_f16")
    override val name: String = "v_smfmac_f32_32x32x16_f16"
  }

  final case class VSmfmacF3232x32x32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x32_bf16")
    override val name: String = "v_smfmac_f32_32x32x32_bf16"
  }

  final case class VSmfmacF3232x32x32Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x32_bf8_bf8")
    override val name: String = "v_smfmac_f32_32x32x32_bf8_bf8"
  }

  final case class VSmfmacF3232x32x32Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x32_bf8_fp8")
    override val name: String = "v_smfmac_f32_32x32x32_bf8_fp8"
  }

  final case class VSmfmacF3232x32x32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x32_f16")
    override val name: String = "v_smfmac_f32_32x32x32_f16"
  }

  final case class VSmfmacF3232x32x32Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x32_fp8_bf8")
    override val name: String = "v_smfmac_f32_32x32x32_fp8_bf8"
  }

  final case class VSmfmacF3232x32x32Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x32_fp8_fp8")
    override val name: String = "v_smfmac_f32_32x32x32_fp8_fp8"
  }

  final case class VSmfmacF3232x32x64Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x64_bf8_bf8")
    override val name: String = "v_smfmac_f32_32x32x64_bf8_bf8"
  }

  final case class VSmfmacF3232x32x64Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x64_bf8_fp8")
    override val name: String = "v_smfmac_f32_32x32x64_bf8_fp8"
  }

  final case class VSmfmacF3232x32x64Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x64_fp8_bf8")
    override val name: String = "v_smfmac_f32_32x32x64_fp8_bf8"
  }

  final case class VSmfmacF3232x32x64Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_f32_32x32x64_fp8_fp8")
    override val name: String = "v_smfmac_f32_32x32x64_fp8_fp8"
  }

  final case class VSmfmacI3216x16x128I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_i32_16x16x128_i8")
    override val name: String = "v_smfmac_i32_16x16x128_i8"
  }

  final case class VSmfmacI3216x16x64I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_i32_16x16x64_i8")
    override val name: String = "v_smfmac_i32_16x16x64_i8"
  }

  final case class VSmfmacI3232x32x32I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_i32_32x32x32_i8")
    override val name: String = "v_smfmac_i32_32x32x32_i8"
  }

  final case class VSmfmacI3232x32x64I8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_smfmac_i32_32x32x64_i8")
    override val name: String = "v_smfmac_i32_32x32x64_i8"
  }

  final case class VSqrtBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sqrt_bf16")
    override val name: String = "v_sqrt_bf16"
  }

  final case class VSqrtF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sqrt_f16")
    override val name: String = "v_sqrt_f16"
  }

  final case class VSqrtF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sqrt_f32")
    override val name: String = "v_sqrt_f32"
  }

  final case class VSqrtF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sqrt_f64")
    override val name: String = "v_sqrt_f64"
  }

  final case class VSubCoCiU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_co_ci_u32")
    override val name: String = "v_sub_co_ci_u32"
  }

  final case class VSubCoU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_co_u32")
    override val name: String = "v_sub_co_u32"
  }

  final case class VSubF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_f16")
    override val name: String = "v_sub_f16"
  }

  final case class VSubF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_f32")
    override val name: String = "v_sub_f32"
  }

  final case class VSubI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_i16")
    override val name: String = "v_sub_i16"
  }

  final case class VSubI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_i32")
    override val name: String = "v_sub_i32"
  }

  final case class VSubNcI16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_nc_i16")
    override val name: String = "v_sub_nc_i16"
  }

  final case class VSubNcI32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_nc_i32")
    override val name: String = "v_sub_nc_i32"
  }

  final case class VSubNcU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_nc_u16")
    override val name: String = "v_sub_nc_u16"
  }

  final case class VSubNcU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_nc_u32")
    override val name: String = "v_sub_nc_u32"
  }

  final case class VSubNcU64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_nc_u64")
    override val name: String = "v_sub_nc_u64"
  }

  final case class VSubU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_u16")
    override val name: String = "v_sub_u16"
  }

  final case class VSubU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_sub_u32")
    override val name: String = "v_sub_u32"
  }

  final case class VSubbCoU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_subb_co_u32")
    override val name: String = "v_subb_co_u32"
  }

  final case class VSubbrevCoU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_subbrev_co_u32")
    override val name: String = "v_subbrev_co_u32"
  }

  final case class VSubrevCoCiU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_subrev_co_ci_u32")
    override val name: String = "v_subrev_co_ci_u32"
  }

  final case class VSubrevCoU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_subrev_co_u32")
    override val name: String = "v_subrev_co_u32"
  }

  final case class VSubrevF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_subrev_f16")
    override val name: String = "v_subrev_f16"
  }

  final case class VSubrevF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_subrev_f32")
    override val name: String = "v_subrev_f32"
  }

  final case class VSubrevNcU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_subrev_nc_u32")
    override val name: String = "v_subrev_nc_u32"
  }

  final case class VSubrevU16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_subrev_u16")
    override val name: String = "v_subrev_u16"
  }

  final case class VSubrevU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_subrev_u32")
    override val name: String = "v_subrev_u32"
  }

  final case class VSwapB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swap_b16")
    override val name: String = "v_swap_b16"
  }

  final case class VSwapB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swap_b32")
    override val name: String = "v_swap_b32"
  }

  final case class VSwaprelB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swaprel_b32")
    override val name: String = "v_swaprel_b32"
  }

  final case class VSwmmacBf1616x16x32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_bf16_16x16x32_bf16")
    override val name: String = "v_swmmac_bf16_16x16x32_bf16"
  }

  final case class VSwmmacBf1616x16x64Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_bf16_16x16x64_bf16")
    override val name: String = "v_swmmac_bf16_16x16x64_bf16"
  }

  final case class VSwmmacBf16f3216x16x64Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_bf16f32_16x16x64_bf16")
    override val name: String = "v_swmmac_bf16f32_16x16x64_bf16"
  }

  final case class VSwmmacF1616x16x128Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f16_16x16x128_bf8_bf8")
    override val name: String = "v_swmmac_f16_16x16x128_bf8_bf8"
  }

  final case class VSwmmacF1616x16x128Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f16_16x16x128_bf8_fp8")
    override val name: String = "v_swmmac_f16_16x16x128_bf8_fp8"
  }

  final case class VSwmmacF1616x16x128Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f16_16x16x128_fp8_bf8")
    override val name: String = "v_swmmac_f16_16x16x128_fp8_bf8"
  }

  final case class VSwmmacF1616x16x128Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f16_16x16x128_fp8_fp8")
    override val name: String = "v_swmmac_f16_16x16x128_fp8_fp8"
  }

  final case class VSwmmacF1616x16x32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f16_16x16x32_f16")
    override val name: String = "v_swmmac_f16_16x16x32_f16"
  }

  final case class VSwmmacF1616x16x64F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f16_16x16x64_f16")
    override val name: String = "v_swmmac_f16_16x16x64_f16"
  }

  final case class VSwmmacF3216x16x128Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x128_bf8_bf8")
    override val name: String = "v_swmmac_f32_16x16x128_bf8_bf8"
  }

  final case class VSwmmacF3216x16x128Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x128_bf8_fp8")
    override val name: String = "v_swmmac_f32_16x16x128_bf8_fp8"
  }

  final case class VSwmmacF3216x16x128Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x128_fp8_bf8")
    override val name: String = "v_swmmac_f32_16x16x128_fp8_bf8"
  }

  final case class VSwmmacF3216x16x128Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x128_fp8_fp8")
    override val name: String = "v_swmmac_f32_16x16x128_fp8_fp8"
  }

  final case class VSwmmacF3216x16x32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x32_bf16")
    override val name: String = "v_swmmac_f32_16x16x32_bf16"
  }

  final case class VSwmmacF3216x16x32Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x32_bf8_bf8")
    override val name: String = "v_swmmac_f32_16x16x32_bf8_bf8"
  }

  final case class VSwmmacF3216x16x32Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x32_bf8_fp8")
    override val name: String = "v_swmmac_f32_16x16x32_bf8_fp8"
  }

  final case class VSwmmacF3216x16x32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x32_f16")
    override val name: String = "v_swmmac_f32_16x16x32_f16"
  }

  final case class VSwmmacF3216x16x32Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x32_fp8_bf8")
    override val name: String = "v_swmmac_f32_16x16x32_fp8_bf8"
  }

  final case class VSwmmacF3216x16x32Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x32_fp8_fp8")
    override val name: String = "v_swmmac_f32_16x16x32_fp8_fp8"
  }

  final case class VSwmmacF3216x16x64Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x64_bf16")
    override val name: String = "v_swmmac_f32_16x16x64_bf16"
  }

  final case class VSwmmacF3216x16x64F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_f32_16x16x64_f16")
    override val name: String = "v_swmmac_f32_16x16x64_f16"
  }

  final case class VSwmmacI3216x16x128Iu8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_i32_16x16x128_iu8")
    override val name: String = "v_swmmac_i32_16x16x128_iu8"
  }

  final case class VSwmmacI3216x16x32Iu4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_i32_16x16x32_iu4")
    override val name: String = "v_swmmac_i32_16x16x32_iu4"
  }

  final case class VSwmmacI3216x16x32Iu8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_i32_16x16x32_iu8")
    override val name: String = "v_swmmac_i32_16x16x32_iu8"
  }

  final case class VSwmmacI3216x16x64Iu4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_swmmac_i32_16x16x64_iu4")
    override val name: String = "v_swmmac_i32_16x16x64_iu4"
  }

  final case class VTanhBf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_tanh_bf16")
    override val name: String = "v_tanh_bf16"
  }

  final case class VTanhF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_tanh_f16")
    override val name: String = "v_tanh_f16"
  }

  final case class VTanhF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_tanh_f32")
    override val name: String = "v_tanh_f32"
  }

  final case class VTrigPreopF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_trig_preop_f64")
    override val name: String = "v_trig_preop_f64"
  }

  final case class VTruncF16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_trunc_f16")
    override val name: String = "v_trunc_f16"
  }

  final case class VTruncF32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_trunc_f32")
    override val name: String = "v_trunc_f32"
  }

  final case class VTruncF64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_trunc_f64")
    override val name: String = "v_trunc_f64"
  }

  final case class VWmmaBf1616x16x16Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_bf16_16x16x16_bf16")
    override val name: String = "v_wmma_bf16_16x16x16_bf16"
  }

  final case class VWmmaBf1616x16x32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_bf16_16x16x32_bf16")
    override val name: String = "v_wmma_bf16_16x16x32_bf16"
  }

  final case class VWmmaBf16f3216x16x32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_bf16f32_16x16x32_bf16")
    override val name: String = "v_wmma_bf16f32_16x16x32_bf16"
  }

  final case class VWmmaF1616x16x128Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f16_16x16x128_bf8_bf8")
    override val name: String = "v_wmma_f16_16x16x128_bf8_bf8"
  }

  final case class VWmmaF1616x16x128Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f16_16x16x128_bf8_fp8")
    override val name: String = "v_wmma_f16_16x16x128_bf8_fp8"
  }

  final case class VWmmaF1616x16x128Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f16_16x16x128_fp8_bf8")
    override val name: String = "v_wmma_f16_16x16x128_fp8_bf8"
  }

  final case class VWmmaF1616x16x128Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f16_16x16x128_fp8_fp8")
    override val name: String = "v_wmma_f16_16x16x128_fp8_fp8"
  }

  final case class VWmmaF1616x16x16F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f16_16x16x16_f16")
    override val name: String = "v_wmma_f16_16x16x16_f16"
  }

  final case class VWmmaF1616x16x32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f16_16x16x32_f16")
    override val name: String = "v_wmma_f16_16x16x32_f16"
  }

  final case class VWmmaF1616x16x64Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f16_16x16x64_bf8_bf8")
    override val name: String = "v_wmma_f16_16x16x64_bf8_bf8"
  }

  final case class VWmmaF1616x16x64Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f16_16x16x64_bf8_fp8")
    override val name: String = "v_wmma_f16_16x16x64_bf8_fp8"
  }

  final case class VWmmaF1616x16x64Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f16_16x16x64_fp8_bf8")
    override val name: String = "v_wmma_f16_16x16x64_fp8_bf8"
  }

  final case class VWmmaF1616x16x64Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f16_16x16x64_fp8_fp8")
    override val name: String = "v_wmma_f16_16x16x64_fp8_fp8"
  }

  final case class VWmmaF3216x16x128Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x128_bf8_bf8")
    override val name: String = "v_wmma_f32_16x16x128_bf8_bf8"
  }

  final case class VWmmaF3216x16x128Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x128_bf8_fp8")
    override val name: String = "v_wmma_f32_16x16x128_bf8_fp8"
  }

  final case class VWmmaF3216x16x128F8f6f4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x128_f8f6f4")
    override val name: String = "v_wmma_f32_16x16x128_f8f6f4"
  }

  final case class VWmmaF3216x16x128Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x128_fp8_bf8")
    override val name: String = "v_wmma_f32_16x16x128_fp8_bf8"
  }

  final case class VWmmaF3216x16x128Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x128_fp8_fp8")
    override val name: String = "v_wmma_f32_16x16x128_fp8_fp8"
  }

  final case class VWmmaF3216x16x16Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x16_bf16")
    override val name: String = "v_wmma_f32_16x16x16_bf16"
  }

  final case class VWmmaF3216x16x16Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x16_bf8_bf8")
    override val name: String = "v_wmma_f32_16x16x16_bf8_bf8"
  }

  final case class VWmmaF3216x16x16Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x16_bf8_fp8")
    override val name: String = "v_wmma_f32_16x16x16_bf8_fp8"
  }

  final case class VWmmaF3216x16x16F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x16_f16")
    override val name: String = "v_wmma_f32_16x16x16_f16"
  }

  final case class VWmmaF3216x16x16Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x16_fp8_bf8")
    override val name: String = "v_wmma_f32_16x16x16_fp8_bf8"
  }

  final case class VWmmaF3216x16x16Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x16_fp8_fp8")
    override val name: String = "v_wmma_f32_16x16x16_fp8_fp8"
  }

  final case class VWmmaF3216x16x32Bf16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x32_bf16")
    override val name: String = "v_wmma_f32_16x16x32_bf16"
  }

  final case class VWmmaF3216x16x32F16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x32_f16")
    override val name: String = "v_wmma_f32_16x16x32_f16"
  }

  final case class VWmmaF3216x16x4F32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x4_f32")
    override val name: String = "v_wmma_f32_16x16x4_f32"
  }

  final case class VWmmaF3216x16x64Bf8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x64_bf8_bf8")
    override val name: String = "v_wmma_f32_16x16x64_bf8_bf8"
  }

  final case class VWmmaF3216x16x64Bf8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x64_bf8_fp8")
    override val name: String = "v_wmma_f32_16x16x64_bf8_fp8"
  }

  final case class VWmmaF3216x16x64Fp8Bf8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x64_fp8_bf8")
    override val name: String = "v_wmma_f32_16x16x64_fp8_bf8"
  }

  final case class VWmmaF3216x16x64Fp8Fp8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_16x16x64_fp8_fp8")
    override val name: String = "v_wmma_f32_16x16x64_fp8_fp8"
  }

  final case class VWmmaF3232x16x128F4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_f32_32x16x128_f4")
    override val name: String = "v_wmma_f32_32x16x128_f4"
  }

  final case class VWmmaI3216x16x16Iu4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_i32_16x16x16_iu4")
    override val name: String = "v_wmma_i32_16x16x16_iu4"
  }

  final case class VWmmaI3216x16x16Iu8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_i32_16x16x16_iu8")
    override val name: String = "v_wmma_i32_16x16x16_iu8"
  }

  final case class VWmmaI3216x16x32Iu4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_i32_16x16x32_iu4")
    override val name: String = "v_wmma_i32_16x16x32_iu4"
  }

  final case class VWmmaI3216x16x64Iu8(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_i32_16x16x64_iu8")
    override val name: String = "v_wmma_i32_16x16x64_iu8"
  }

  final case class VWmmaScale16F3216x16x128F8f6f4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_scale16_f32_16x16x128_f8f6f4")
    override val name: String = "v_wmma_scale16_f32_16x16x128_f8f6f4"
  }

  final case class VWmmaScale16F3232x16x128F4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_scale16_f32_32x16x128_f4")
    override val name: String = "v_wmma_scale16_f32_32x16x128_f4"
  }

  final case class VWmmaScaleF3216x16x128F8f6f4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_scale_f32_16x16x128_f8f6f4")
    override val name: String = "v_wmma_scale_f32_16x16x128_f8f6f4"
  }

  final case class VWmmaScaleF3232x16x128F4(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_wmma_scale_f32_32x16x128_f4")
    override val name: String = "v_wmma_scale_f32_32x16x128_f4"
  }

  final case class VWritelaneB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_writelane_b32")
    override val name: String = "v_writelane_b32"
  }

  final case class VWritelaneB32E64(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_writelane_b32_e64")
    override val name: String = "v_writelane_b32_e64"
  }

  final case class VXadU32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_xad_u32")
    override val name: String = "v_xad_u32"
  }

  final case class VXnorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_xnor_b32")
    override val name: String = "v_xnor_b32"
  }

  final case class VXor3B32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_xor3_b32")
    override val name: String = "v_xor3_b32"
  }

  final case class VXorB16(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_xor_b16")
    override val name: String = "v_xor_b16"
  }

  final case class VXorB32(arguments: InstructionArguments) extends KnownInstruction {
    override val opcode: Opcode = Opcode.unsafe("v_xor_b32")
    override val name: String = "v_xor_b32"
  }

  private def decode0(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_format_d16_x" => Some(BufferLoadFormatD16X(arguments))
      case "buffer_store_d16_format_xyz" => Some(BufferStoreD16FormatXyz(arguments))
      case "buffer_store_d16_hi_b16" => Some(BufferStoreD16HiB16(arguments))
      case "buffer_store_format_xy" => Some(BufferStoreFormatXy(arguments))
      case "ds_direct_load" => Some(DsDirectLoad(arguments))
      case "ds_max_u64" => Some(DsMaxU64(arguments))
      case "ds_min_f32" => Some(DsMinF32(arguments))
      case "ds_min_num_rtn_f32" => Some(DsMinNumRtnF32(arguments))
      case "ds_write_src2_b32" => Some(DsWriteSrc2B32(arguments))
      case "global_load_lds_ushort" => Some(GlobalLoadLdsUshort(arguments))
      case "image_atomic_max_int" => Some(ImageAtomicMaxInt(arguments))
      case "image_gather4_c_b_cl_o" => Some(ImageGather4CBClO(arguments))
      case "image_load_mip_pck4" => Some(ImageLoadMipPck4(arguments))
      case "s_cmp_u_f16" => Some(SCmpUF16(arguments))
      case "s_min_f32" => Some(SMinF32(arguments))
      case "s_mul_u64" => Some(SMulU64(arguments))
      case "s_or_not1_saveexec_b64" => Some(SOrNot1SaveexecB64(arguments))
      case "s_scratch_load_dwordx2" => Some(SScratchLoadDwordx2(arguments))
      case "v_cmpx_gt_i16" => Some(VCmpxGtI16(arguments))
      case "v_cmpx_nle_f64" => Some(VCmpxNleF64(arguments))
      case "v_cos_f32" => Some(VCosF32(arguments))
      case "v_cvt_scalef32_sr_pk16_bf6_f32" => Some(VCvtScalef32SrPk16Bf6F32(arguments))
      case "v_interp_p10_rtz_f16_f32" => Some(VInterpP10RtzF16F32(arguments))
      case "v_mfma_f32_16x16x4_4b_f16" => Some(VMfmaF3216x16x44bF16(arguments))
      case "v_min3_f32" => Some(VMin3F32(arguments))
      case "v_minmax_u32" => Some(VMinmaxU32(arguments))
      case "v_not_b32" => Some(VNotB32(arguments))
      case "v_sat_pk_u8_i16" => Some(VSatPkU8I16(arguments))
      case "v_subrev_f16" => Some(VSubrevF16(arguments))
      case _ => None
    }

  private def decode1(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_fmin" => Some(BufferAtomicFmin(arguments))
      case "buffer_store_format_xyzw" => Some(BufferStoreFormatXyzw(arguments))
      case "ds_cmpst_rtn_f64" => Some(DsCmpstRtnF64(arguments))
      case "ds_max_i32" => Some(DsMaxI32(arguments))
      case "flat_atomic_add_f32" => Some(FlatAtomicAddF32(arguments))
      case "flat_atomic_fcmpswap" => Some(FlatAtomicFcmpswap(arguments))
      case "flat_store_b32" => Some(FlatStoreB32(arguments))
      case "global_atomic_or_b32" => Some(GlobalAtomicOrB32(arguments))
      case "global_load_addtid_b32" => Some(GlobalLoadAddtidB32(arguments))
      case "global_load_u16" => Some(GlobalLoadU16(arguments))
      case "image_gather4" => Some(ImageGather4(arguments))
      case "s_alloc_vgpr" => Some(SAllocVgpr(arguments))
      case "s_cmp_ge_f32" => Some(SCmpGeF32(arguments))
      case "s_cmpk_ge_u32" => Some(SCmpkGeU32(arguments))
      case "s_max_i32" => Some(SMaxI32(arguments))
      case "s_max_num_f32" => Some(SMaxNumF32(arguments))
      case "s_movreld_b32" => Some(SMovreldB32(arguments))
      case "s_mul_i32" => Some(SMulI32(arguments))
      case "s_sub_i32" => Some(SSubI32(arguments))
      case "s_wait_samplecnt" => Some(SWaitSamplecnt(arguments))
      case "scratch_load_sshort" => Some(ScratchLoadSshort(arguments))
      case "scratch_store_short" => Some(ScratchStoreShort(arguments))
      case "tbuffer_load_d16_format_xy" => Some(TbufferLoadD16FormatXy(arguments))
      case "v_ashr_i64" => Some(VAshrI64(arguments))
      case "v_cmpx_eq_u32" => Some(VCmpxEqU32(arguments))
      case "v_cmpx_gt_i64" => Some(VCmpxGtI64(arguments))
      case "v_cmpx_o_f32" => Some(VCmpxOF32(arguments))
      case "v_cvt_scalef32_pk_fp8_f32" => Some(VCvtScalef32PkFp8F32(arguments))
      case "v_lshrrev_b16" => Some(VLshrrevB16(arguments))
      case "v_mad_co_i64_i32" => Some(VMadCoI64I32(arguments))
      case "v_mfma_f64_16x16x4_f64" => Some(VMfmaF6416x16x4F64_1(arguments))
      case "v_or3_b32" => Some(VOr3B32(arguments))
      case "v_readlane_b32_e64" => Some(VReadlaneB32E64(arguments))
      case "v_swmmac_f16_16x16x128_bf8_bf8" => Some(VSwmmacF1616x16x128Bf8Bf8(arguments))
      case "v_wmma_f16_16x16x128_bf8_bf8" => Some(VWmmaF1616x16x128Bf8Bf8(arguments))
      case _ => None
    }

  private def decode2(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_fmax_x2" => Some(BufferAtomicFmaxX2(arguments))
      case "buffer_store_format_xyz" => Some(BufferStoreFormatXyz(arguments))
      case "ds_dec_u64" => Some(DsDecU64(arguments))
      case "ds_store_2addr_b64" => Some(DsStore2addrB64(arguments))
      case "ds_storexchg_2addr_rtn_b32" => Some(DsStorexchg2addrRtnB32(arguments))
      case "global_atomic_add_f64" => Some(GlobalAtomicAddF64(arguments))
      case "global_load_b96" => Some(GlobalLoadB96(arguments))
      case "image_atomic_inc" => Some(ImageAtomicInc(arguments))
      case "image_sample_cl" => Some(ImageSampleCl_2(arguments))
      case "s_atomic_swap_x2" => Some(SAtomicSwapX2(arguments))
      case "s_bfm_b64" => Some(SBfmB64(arguments))
      case "s_cmp_le_f16" => Some(SCmpLeF16(arguments))
      case "s_load_b128" => Some(SLoadB128(arguments))
      case "s_scratch_load_dwordx4" => Some(SScratchLoadDwordx4(arguments))
      case "v_clz_i32_u32" => Some(VClzI32U32(arguments))
      case "v_cvt_scalef32_pk32_bf6_f16" => Some(VCvtScalef32Pk32Bf6F16(arguments))
      case "v_cvt_scalef32_sr_pk16_fp6_f32" => Some(VCvtScalef32SrPk16Fp6F32(arguments))
      case "v_lshrrev_b64" => Some(VLshrrevB64(arguments))
      case "v_mad_u16" => Some(VMadU16(arguments))
      case "v_max3_f32" => Some(VMax3F32(arguments))
      case "v_mfma_f32_16x16x8bf16" => Some(VMfmaF3216x16x8bf16(arguments))
      case "v_pk_min_num_bf16" => Some(VPkMinNumBf16(arguments))
      case "v_pk_min_u16" => Some(VPkMinU16(arguments))
      case "v_sub_co_ci_u32" => Some(VSubCoCiU32(arguments))
      case "v_wmma_bf16f32_16x16x32_bf16" => Some(VWmmaBf16f3216x16x32Bf16(arguments))
      case "v_xor_b16" => Some(VXorB16(arguments))
      case _ => None
    }

  private def decode3(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_and_b64" => Some(BufferAtomicAndB64(arguments))
      case "buffer_load_ubyte_d16_hi" => Some(BufferLoadUbyteD16Hi(arguments))
      case "flat_atomic_cmpswap" => Some(FlatAtomicCmpswap(arguments))
      case "flat_atomic_xor_b32" => Some(FlatAtomicXorB32(arguments))
      case "flat_store_d16_hi_b16" => Some(FlatStoreD16HiB16(arguments))
      case "global_load_tr6_b96" => Some(GlobalLoadTr6B96(arguments))
      case "image_sample_c_l" => Some(ImageSampleCL_1(arguments))
      case "image_sample_lz_o" => Some(ImageSampleLzO(arguments))
      case "s_cbranch_cdbguser" => Some(SCbranchCdbguser(arguments))
      case "s_setvskip" => Some(SSetvskip(arguments))
      case "s_wait_bvhcnt" => Some(SWaitBvhcnt(arguments))
      case "scratch_load_d16_hi_b16" => Some(ScratchLoadD16HiB16(arguments))
      case "v_cmp_nlg_f16" => Some(VCmpNlgF16(arguments))
      case "v_cubetc_f32" => Some(VCubetcF32(arguments))
      case "v_frexp_mant_f32" => Some(VFrexpMantF32(arguments))
      case "v_readfirstlane_b32" => Some(VReadfirstlaneB32(arguments))
      case "v_smfmac_f32_16x16x64_bf8_fp8" => Some(VSmfmacF3216x16x64Bf8Fp8(arguments))
      case "v_swmmac_f16_16x16x128_fp8_bf8" => Some(VSwmmacF1616x16x128Fp8Bf8(arguments))
      case "v_wmma_f16_16x16x128_fp8_bf8" => Some(VWmmaF1616x16x128Fp8Bf8(arguments))
      case _ => None
    }

  private def decode4(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_inc_x2" => Some(BufferAtomicIncX2(arguments))
      case "buffer_atomic_swap" => Some(BufferAtomicSwap(arguments))
      case "buffer_load_format_xyzw" => Some(BufferLoadFormatXyzw(arguments))
      case "ds_bvh_stack_rtn_b32" => Some(DsBvhStackRtnB32(arguments))
      case "ds_gws_sema_release_all" => Some(DsGwsSemaReleaseAll(arguments))
      case "ds_min_rtn_u32" => Some(DsMinRtnU32(arguments))
      case "flat_atomic_min_u64" => Some(FlatAtomicMinU64(arguments))
      case "flat_load_sbyte" => Some(FlatLoadSbyte(arguments))
      case "global_atomic_xor_b64" => Some(GlobalAtomicXorB64(arguments))
      case "global_load_u8" => Some(GlobalLoadU8(arguments))
      case "image_atomic_add_flt" => Some(ImageAtomicAddFlt(arguments))
      case "s_atomic_cmpswap_x2" => Some(SAtomicCmpswapX2(arguments))
      case "s_buffer_atomic_cmpswap" => Some(SBufferAtomicCmpswap(arguments))
      case "s_buffer_atomic_swap" => Some(SBufferAtomicSwap(arguments))
      case "s_cbranch_g_fork" => Some(SCbranchGFork(arguments))
      case "s_cbranch_vccnz" => Some(SCbranchVccnz(arguments))
      case "s_cmpk_le_i32" => Some(SCmpkLeI32(arguments))
      case "s_xor_saveexec_b32" => Some(SXorSaveexecB32(arguments))
      case "v_cmp_gt_u32" => Some(VCmpGtU32(arguments))
      case "v_cmp_nlg_f64" => Some(VCmpNlgF64(arguments))
      case "v_cmp_tru_u16" => Some(VCmpTruU16(arguments))
      case "v_cvt_scalef32_pk32_fp6_f16" => Some(VCvtScalef32Pk32Fp6F16(arguments))
      case "v_fma_mix_f32_bf16" => Some(VFmaMixF32Bf16(arguments))
      case "v_mbcnt_hi_u32_b32" => Some(VMbcntHiU32B32(arguments))
      case "v_min_i16" => Some(VMinI16(arguments))
      case "v_subbrev_co_u32" => Some(VSubbrevCoU32(arguments))
      case "v_wmma_f16_16x16x64_bf8_bf8" => Some(VWmmaF1616x16x64Bf8Bf8(arguments))
      case _ => None
    }

  private def decode5(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_add_f64" => Some(BufferAtomicAddF64(arguments))
      case "buffer_store_format_x" => Some(BufferStoreFormatX(arguments))
      case "ds_cond_sub_rtn_u32" => Some(DsCondSubRtnU32(arguments))
      case "ds_pk_add_f16" => Some(DsPkAddF16(arguments))
      case "ds_read2_b64" => Some(DsRead2B64(arguments))
      case "ds_xor_rtn_b64" => Some(DsXorRtnB64(arguments))
      case "flat_atomic_min_i32" => Some(FlatAtomicMinI32(arguments))
      case "global_store_async_from_lds_b32" => Some(GlobalStoreAsyncFromLdsB32(arguments))
      case "s_and_not1_saveexec_b32" => Some(SAndNot1SaveexecB32(arguments))
      case "s_code_end" => Some(SCodeEnd(arguments))
      case "s_min_num_f16" => Some(SMinNumF16(arguments))
      case "s_wqm_b32" => Some(SWqmB32(arguments))
      case "scratch_load_d16_i8" => Some(ScratchLoadD16I8(arguments))
      case "tbuffer_load_format_d16_xy" => Some(TbufferLoadFormatD16Xy(arguments))
      case "v_cmp_eq_i16" => Some(VCmpEqI16(arguments))
      case "v_cmp_lt_u16" => Some(VCmpLtU16(arguments))
      case "v_cmp_tru_u64" => Some(VCmpTruU64(arguments))
      case "v_cmpx_gt_f16" => Some(VCmpxGtF16(arguments))
      case "v_cvt_pk_f32_bf8" => Some(VCvtPkF32Bf8(arguments))
      case "v_cvt_scale_pk16_f16_fp6" => Some(VCvtScalePk16F16Fp6(arguments))
      case "v_cvt_scalef32_pk8_fp4_f32" => Some(VCvtScalef32Pk8Fp4F32(arguments))
      case "v_ffbl_b32" => Some(VFfblB32(arguments))
      case "v_fma_mixhi_bf16" => Some(VFmaMixhiBf16(arguments))
      case "v_fma_mixlo_f16" => Some(VFmaMixloF16(arguments))
      case "v_madmk_f16" => Some(VMadmkF16(arguments))
      case "v_mfma_f32_32x32x2_f32" => Some(VMfmaF3232x32x2F32_1(arguments))
      case "v_mfma_i32_32x32x16_i8" => Some(VMfmaI3232x32x16I8(arguments))
      case "v_min3_num_f16" => Some(VMin3NumF16(arguments))
      case "v_min_i64" => Some(VMinI64(arguments))
      case "v_pk_max3_u16" => Some(VPkMax3U16(arguments))
      case _ => None
    }

  private def decode6(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_d16_hi_format_x" => Some(BufferLoadD16HiFormatX(arguments))
      case "ds_max_f32" => Some(DsMaxF32(arguments))
      case "flat_load_b32" => Some(FlatLoadB32(arguments))
      case "flat_load_sbyte_d16" => Some(FlatLoadSbyteD16(arguments))
      case "global_atomic_min_i64" => Some(GlobalAtomicMinI64(arguments))
      case "global_atomic_swap_x2" => Some(GlobalAtomicSwapX2(arguments))
      case "global_wbinv" => Some(GlobalWbinv(arguments))
      case "image_gather4_c_l" => Some(ImageGather4CL_1(arguments))
      case "image_sample_c_cd_o" => Some(ImageSampleCCdO(arguments))
      case "image_sample_c_o" => Some(ImageSampleCO(arguments))
      case "s_bitcmp1_b32" => Some(SBitcmp1B32(arguments))
      case "s_cmp_eq_u32" => Some(SCmpEqU32(arguments))
      case "s_flbit_i32_b32" => Some(SFlbitI32B32(arguments))
      case "s_max_f32" => Some(SMaxF32(arguments))
      case "s_minimum_f16" => Some(SMinimumF16(arguments))
      case "s_mul_f32" => Some(SMulF32(arguments))
      case "s_sendmsghalt" => Some(SSendmsghalt(arguments))
      case "s_sub_f32" => Some(SSubF32(arguments))
      case "scratch_load_i16" => Some(ScratchLoadI16(arguments))
      case "scratch_load_lds_dword" => Some(ScratchLoadLdsDword(arguments))
      case "v_cmp_eq_i64" => Some(VCmpEqI64(arguments))
      case "v_cmp_lt_u64" => Some(VCmpLtU64(arguments))
      case "v_cmp_nge_f16" => Some(VCmpNgeF16(arguments))
      case "v_cmp_tru_i32" => Some(VCmpTruI32(arguments))
      case "v_cmpx_gt_f64" => Some(VCmpxGtF64(arguments))
      case "v_cmpx_lg_i16" => Some(VCmpxLgI16(arguments))
      case "v_lerp_u8" => Some(VLerpU8(arguments))
      case "v_lshl_b32" => Some(VLshlB32(arguments))
      case "v_pk_maximum3_f16" => Some(VPkMaximum3F16(arguments))
      case "v_smfmac_f32_32x32x32_bf8_bf8" => Some(VSmfmacF3232x32x32Bf8Bf8(arguments))
      case "v_wmma_f16_16x16x64_fp8_bf8" => Some(VWmmaF1616x16x64Fp8Bf8(arguments))
      case _ => None
    }

  private def decode7(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_xor_b64" => Some(BufferAtomicXorB64(arguments))
      case "buffer_load_b32" => Some(BufferLoadB32(arguments))
      case "ds_load_b64" => Some(DsLoadB64(arguments))
      case "ds_load_u16_d16" => Some(DsLoadU16D16(arguments))
      case "ds_min_num_f32" => Some(DsMinNumF32(arguments))
      case "ds_rsub_rtn_u64" => Some(DsRsubRtnU64(arguments))
      case "flat_atomic_fmin_x2" => Some(FlatAtomicFminX2(arguments))
      case "flat_atomic_swap_b32" => Some(FlatAtomicSwapB32(arguments))
      case "flat_atomic_umin" => Some(FlatAtomicUmin(arguments))
      case "global_atomic_min_num_f32" => Some(GlobalAtomicMinNumF32(arguments))
      case "global_wb" => Some(GlobalWb(arguments))
      case "image_load_pck" => Some(ImageLoadPck(arguments))
      case "image_sample_cd_cl_g16" => Some(ImageSampleCdClG16_2(arguments))
      case "s_atomic_umax" => Some(SAtomicUmax(arguments))
      case "s_swappc_b64" => Some(SSwappcB64(arguments))
      case "scratch_load_i8" => Some(ScratchLoadI8(arguments))
      case "v_add_u16" => Some(VAddU16(arguments))
      case "v_cmp_lt_i32" => Some(VCmpLtI32(arguments))
      case "v_cmp_nge_f64" => Some(VCmpNgeF64(arguments))
      case "v_cmp_u_f16" => Some(VCmpUF16(arguments))
      case "v_cmpx_lg_i64" => Some(VCmpxLgI64(arguments))
      case "v_cmpx_ngt_f16" => Some(VCmpxNgtF16(arguments))
      case "v_cvt_scalef32_pk8_bf8_bf16" => Some(VCvtScalef32Pk8Bf8Bf16(arguments))
      case "v_cvt_scalef32_sr_pk8_bf8_f16" => Some(VCvtScalef32SrPk8Bf8F16(arguments))
      case "v_cvt_scalef32_sr_pk8_fp4_bf16" => Some(VCvtScalef32SrPk8Fp4Bf16(arguments))
      case "v_div_fixup_f32" => Some(VDivFixupF32(arguments))
      case "v_fma_f32" => Some(VFmaF32(arguments))
      case "v_fma_mixhi_f16" => Some(VFmaMixhiF16(arguments))
      case "v_log_f16" => Some(VLogF16(arguments))
      case "v_mad_u32_u16" => Some(VMadU32U16(arguments))
      case "v_mfma_f32_32x32x4bf16_1k" => Some(VMfmaF3232x32x4bf161k(arguments))
      case "v_mfma_i32_16x16x4i8" => Some(VMfmaI3216x16x4i8(arguments))
      case "v_mfma_i32_32x32x4_2b_i8" => Some(VMfmaI3232x32x42bI8(arguments))
      case "v_perm_pk16_b8_u4" => Some(VPermPk16B8U4(arguments))
      case "v_pk_mul_bf16" => Some(VPkMulBf16(arguments))
      case "v_qsad_pk_u16_u8" => Some(VQsadPkU16U8(arguments))
      case "v_sqrt_bf16" => Some(VSqrtBf16(arguments))
      case _ => None
    }

  private def decode8(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_add" => Some(BufferAtomicAdd(arguments))
      case "ds_and_b64" => Some(DsAndB64(arguments))
      case "ds_cmpst_b64" => Some(DsCmpstB64(arguments))
      case "ds_load_2addr_b32" => Some(DsLoad2addrB32(arguments))
      case "ds_max_num_f64" => Some(DsMaxNumF64(arguments))
      case "ds_or_src2_b32" => Some(DsOrSrc2B32(arguments))
      case "flat_atomic_and_x2" => Some(FlatAtomicAndX2(arguments))
      case "flat_atomic_min_num_f64" => Some(FlatAtomicMinNumF64(arguments))
      case "global_atomic_max_num_f64" => Some(GlobalAtomicMaxNumF64(arguments))
      case "image_sample_d_o_g16" => Some(ImageSampleDOG16(arguments))
      case "image_store_pck2" => Some(ImageStorePck2(arguments))
      case "s_bitset0_b64" => Some(SBitset0B64(arguments))
      case "s_cvt_i32_f32" => Some(SCvtI32F32(arguments))
      case "s_cvt_u32_f32" => Some(SCvtU32F32(arguments))
      case "s_orn2_saveexec_b32" => Some(SOrn2SaveexecB32(arguments))
      case "s_trunc_f16" => Some(STruncF16(arguments))
      case "s_wait_dscnt" => Some(SWaitDscnt(arguments))
      case "tbuffer_load_format_xyzw" => Some(TbufferLoadFormatXyzw(arguments))
      case "v_cmp_u_f64" => Some(VCmpUF64(arguments))
      case "v_cmpx_ne_u32" => Some(VCmpxNeU32(arguments))
      case "v_cmpx_ngt_f64" => Some(VCmpxNgtF64(arguments))
      case "v_dual_sub_f32" => Some(VDualSubF32(arguments))
      case "v_exp_f16" => Some(VExpF16(arguments))
      case "v_mad_u64_u32" => Some(VMadU64U32(arguments))
      case "v_mfma_f32_16x16x16_f16" => Some(VMfmaF3216x16x16F16_1(arguments))
      case "v_mfma_f32_16x16x16bf16_1k" => Some(VMfmaF3216x16x16bf161k(arguments))
      case "v_pk_max_u16" => Some(VPkMaxU16(arguments))
      case "v_pk_sub_u16" => Some(VPkSubU16(arguments))
      case _ => None
    }

  private def decode9(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_cmpswap_b64" => Some(BufferAtomicCmpswapB64(arguments))
      case "buffer_atomic_swap_x2" => Some(BufferAtomicSwapX2(arguments))
      case "buffer_store_byte" => Some(BufferStoreByte(arguments))
      case "cluster_load_b128" => Some(ClusterLoadB128(arguments))
      case "ds_cmpstore_rtn_b64" => Some(DsCmpstoreRtnB64(arguments))
      case "ds_load_addtid_b32" => Some(DsLoadAddtidB32(arguments))
      case "ds_mskor_b64" => Some(DsMskorB64(arguments))
      case "ds_sub_src2_u64" => Some(DsSubSrc2U64(arguments))
      case "flat_atomic_smin" => Some(FlatAtomicSmin(arguments))
      case "global_store_b96" => Some(GlobalStoreB96(arguments))
      case "image_gather4_c_o" => Some(ImageGather4CO(arguments))
      case "s_atc_probe" => Some(SAtcProbe(arguments))
      case "s_atomic_smax" => Some(SAtomicSmax(arguments))
      case "s_barrier_wait" => Some(SBarrierWait(arguments))
      case "s_cmp_neq_f16" => Some(SCmpNeqF16(arguments))
      case "s_decperflevel" => Some(SDecperflevel(arguments))
      case "s_load_b512" => Some(SLoadB512(arguments))
      case "s_lshl_b32" => Some(SLshlB32(arguments))
      case "s_orn2_b32" => Some(SOrn2B32(arguments))
      case "s_wait_loadcnt" => Some(SWaitLoadcnt(arguments))
      case "scratch_load_b128" => Some(ScratchLoadB128(arguments))
      case "scratch_store_b8" => Some(ScratchStoreB8(arguments))
      case "v_add_i32" => Some(VAddI32(arguments))
      case "v_cmpx_ge_i16" => Some(VCmpxGeI16(arguments))
      case "v_cvt_scalef32_sr_pk8_fp8_f16" => Some(VCvtScalef32SrPk8Fp8F16(arguments))
      case "v_cvt_sr_bf8_f32" => Some(VCvtSrBf8F32(arguments))
      case "v_mad_f32" => Some(VMadF32(arguments))
      case "v_med3_num_f32" => Some(VMed3NumF32(arguments))
      case "v_mfma_f32_16x16x1_4b_f32" => Some(VMfmaF3216x16x14bF32(arguments))
      case "v_min_f16" => Some(VMinF16(arguments))
      case "v_perm_pk16_b6_u4" => Some(VPermPk16B6U4(arguments))
      case "v_permlane_bcast_b32" => Some(VPermlaneBcastB32(arguments))
      case "v_rsq_clamp_f32" => Some(VRsqClampF32(arguments))
      case "v_sin_bf16" => Some(VSinBf16(arguments))
      case _ => None
    }

  private def decode10(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_format_d16_hi_x" => Some(BufferLoadFormatD16HiX(arguments))
      case "ds_add_f64" => Some(DsAddF64(arguments))
      case "ds_and_rtn_b32" => Some(DsAndRtnB32(arguments))
      case "ds_bvh_stack_push8_pop2_rtn_b64" => Some(DsBvhStackPush8Pop2RtnB64(arguments))
      case "ds_load_tr4_b64" => Some(DsLoadTr4B64(arguments))
      case "ds_max_src2_u32" => Some(DsMaxSrc2U32(arguments))
      case "flat_atomic_max_u64" => Some(FlatAtomicMaxU64(arguments))
      case "flat_atomic_min_f32" => Some(FlatAtomicMinF32(arguments))
      case "flat_load_d16_hi_b16" => Some(FlatLoadD16HiB16(arguments))
      case "global_load_d16_u8" => Some(GlobalLoadD16U8(arguments))
      case "global_load_monitor_b64" => Some(GlobalLoadMonitorB64(arguments))
      case "image_atomic_dec" => Some(ImageAtomicDec(arguments))
      case "image_sample_cd_cl" => Some(ImageSampleCdCl_2(arguments))
      case "image_sample_cl_o" => Some(ImageSampleClO_2(arguments))
      case "image_store_pck4" => Some(ImageStorePck4(arguments))
      case "s_atomic_and_x2" => Some(SAtomicAndX2(arguments))
      case "s_cmp_gt_f16" => Some(SCmpGtF16(arguments))
      case "s_nand_b64" => Some(SNandB64(arguments))
      case "scratch_store_d16_hi_b16" => Some(ScratchStoreD16HiB16(arguments))
      case "tbuffer_store_d16_format_xy" => Some(TbufferStoreD16FormatXy(arguments))
      case "v_ashr_pk_i8_i32" => Some(VAshrPkI8I32(arguments))
      case "v_cmp_eq_f16" => Some(VCmpEqF16(arguments))
      case "v_cmp_lg_u32" => Some(VCmpLgU32(arguments))
      case "v_cmpx_ge_i64" => Some(VCmpxGeI64(arguments))
      case "v_cndmask_b32" => Some(VCndmaskB32(arguments))
      case "v_cvt_sr_pk_bf16_f32" => Some(VCvtSrPkBf16F32(arguments))
      case "v_dot4_i32_iu8" => Some(VDot4I32Iu8(arguments))
      case "v_dual_add_nc_u32" => Some(VDualAddNcU32(arguments))
      case "v_max_i16" => Some(VMaxI16(arguments))
      case "v_min_f64" => Some(VMinF64(arguments))
      case "v_permlanex16_b32" => Some(VPermlanex16B32(arguments))
      case _ => None
    }

  private def decode11(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_gl1_inv" => Some(BufferGl1Inv(arguments))
      case "buffer_load_format_xy" => Some(BufferLoadFormatXy(arguments))
      case "buffer_load_ubyte" => Some(BufferLoadUbyte(arguments))
      case "flat_atomic_max_i32" => Some(FlatAtomicMaxI32(arguments))
      case "global_atomic_cmpswap_f32" => Some(GlobalAtomicCmpswapF32(arguments))
      case "global_atomic_min_f64" => Some(GlobalAtomicMinF64(arguments))
      case "image_atomic_umax" => Some(ImageAtomicUmax(arguments))
      case "image_sample_c_lz_o" => Some(ImageSampleCLzO(arguments))
      case "s_and_not1_wrexec_b32" => Some(SAndNot1WrexecB32(arguments))
      case "s_cmp_nlt_f32" => Some(SCmpNltF32(arguments))
      case "s_movrels_b64" => Some(SMovrelsB64(arguments))
      case "s_subb_u32" => Some(SSubbU32(arguments))
      case "v_add_co_ci_u32" => Some(VAddCoCiU32(arguments))
      case "v_cmp_eq_f64" => Some(VCmpEqF64(arguments))
      case "v_cmp_f_i16" => Some(VCmpFI16(arguments))
      case "v_cmp_tru_f32" => Some(VCmpTruF32(arguments))
      case "v_cmpx_lg_f16" => Some(VCmpxLgF16(arguments))
      case "v_cmpx_t_i16" => Some(VCmpxTI16(arguments))
      case "v_cvt_scalef32_sr_pk_fp4_f32" => Some(VCvtScalef32SrPkFp4F32(arguments))
      case "v_fmamk_f16" => Some(VFmamkF16(arguments))
      case "v_max_i64" => Some(VMaxI64(arguments))
      case "v_med3_i16" => Some(VMed3I16(arguments))
      case "v_perm_pk16_b4_u4" => Some(VPermPk16B4U4(arguments))
      case "v_pk_ashrrev_i16" => Some(VPkAshrrevI16(arguments))
      case "v_smfmac_f32_32x32x64_bf8_fp8" => Some(VSmfmacF3232x32x64Bf8Fp8(arguments))
      case "v_subrev_co_ci_u32" => Some(VSubrevCoCiU32(arguments))
      case "v_wmma_f16_16x16x32_f16" => Some(VWmmaF1616x16x32F16(arguments))
      case _ => None
    }

  private def decode12(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_max_num_f32" => Some(BufferAtomicMaxNumF32(arguments))
      case "ds_read2st64_b64" => Some(DsRead2st64B64(arguments))
      case "ds_rsub_src2_u32" => Some(DsRsubSrc2U32(arguments))
      case "ds_wrxchg_rtn_b32" => Some(DsWrxchgRtnB32(arguments))
      case "ds_xor_b64" => Some(DsXorB64(arguments))
      case "flat_atomic_dec_u64" => Some(FlatAtomicDecU64(arguments))
      case "flat_atomic_fmax" => Some(FlatAtomicFmax(arguments))
      case "global_atomic_max_i64" => Some(GlobalAtomicMaxI64(arguments))
      case "global_load_i8" => Some(GlobalLoadI8(arguments))
      case "image_sample_c_d_g16" => Some(ImageSampleCDG16_1(arguments))
      case "s_and_not1_b32" => Some(SAndNot1B32(arguments))
      case "s_buffer_atomic_or" => Some(SBufferAtomicOr(arguments))
      case "s_buffer_load_i16" => Some(SBufferLoadI16(arguments))
      case "s_cmpk_gt_i32" => Some(SCmpkGtI32(arguments))
      case "s_cselect_b64" => Some(SCselectB64(arguments))
      case "s_getreg_b32" => Some(SGetregB32(arguments))
      case "s_or_not1_b32" => Some(SOrNot1B32(arguments))
      case "s_set_gpr_idx_idx" => Some(SSetGprIdxIdx(arguments))
      case "s_set_gpr_idx_on" => Some(SSetGprIdxOn(arguments))
      case "s_sub_co_u32" => Some(SSubCoU32(arguments))
      case "s_wait_alu" => Some(SWaitAlu(arguments))
      case "tbuffer_load_format_d16_x" => Some(TbufferLoadFormatD16X(arguments))
      case "v_add_co_u32" => Some(VAddCoU32(arguments))
      case "v_and_b32" => Some(VAndB32(arguments))
      case "v_cmp_f_i64" => Some(VCmpFI64(arguments))
      case "v_cmp_lt_f32" => Some(VCmpLtF32(arguments))
      case "v_cmp_ne_i16" => Some(VCmpNeI16(arguments))
      case "v_cmpx_lg_f64" => Some(VCmpxLgF64(arguments))
      case "v_cmpx_t_i64" => Some(VCmpxTI64(arguments))
      case "v_cvt_sr_f16_f32" => Some(VCvtSrF16F32(arguments))
      case "v_fmamk_f64" => Some(VFmamkF64(arguments))
      case "v_interp_p2_hi_f16" => Some(VInterpP2HiF16(arguments))
      case "v_ldexp_f16" => Some(VLdexpF16(arguments))
      case "v_maxmin_u32" => Some(VMaxminU32(arguments))
      case "v_mfma_i32_4x4x4_16b_i8" => Some(VMfmaI324x4x416bI8(arguments))
      case "v_msad_u8" => Some(VMsadU8(arguments))
      case "v_s_rcp_f32" => Some(VSRcpF32(arguments))
      case "v_subrev_co_u32" => Some(VSubrevCoU32(arguments))
      case "v_swmmac_bf16_16x16x64_bf16" => Some(VSwmmacBf1616x16x64Bf16(arguments))
      case _ => None
    }

  private def decode13(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_cmpswap_x2" => Some(BufferAtomicCmpswapX2(arguments))
      case "ds_load_u16_d16_hi" => Some(DsLoadU16D16Hi(arguments))
      case "ds_read_addtid_b32" => Some(DsReadAddtidB32(arguments))
      case "global_atomic_and_b32" => Some(GlobalAtomicAndB32(arguments))
      case "global_atomic_sub_clamp_u32" => Some(GlobalAtomicSubClampU32(arguments))
      case "image_atomic_min_flt" => Some(ImageAtomicMinFlt(arguments))
      case "image_atomic_smax" => Some(ImageAtomicSmax(arguments))
      case "s_ceil_f16" => Some(SCeilF16(arguments))
      case "s_load_i16" => Some(SLoadI16(arguments))
      case "s_mul_hi_i32" => Some(SMulHiI32(arguments))
      case "tbuffer_store_format_x" => Some(TbufferStoreFormatX(arguments))
      case "tbuffer_store_format_xyzw" => Some(TbufferStoreFormatXyzw(arguments))
      case "v_bfe_u32" => Some(VBfeU32(arguments))
      case "v_cmp_ge_u32" => Some(VCmpGeU32(arguments))
      case "v_cmp_ne_i64" => Some(VCmpNeI64(arguments))
      case "v_cmp_t_u16" => Some(VCmpTU16(arguments))
      case "v_cmpx_nlg_f16" => Some(VCmpxNlgF16(arguments))
      case "v_cvt_scalef32_pk_bf16_bf8" => Some(VCvtScalef32PkBf16Bf8(arguments))
      case "v_dual_cndmask_b32" => Some(VDualCndmaskB32(arguments))
      case "v_frexp_exp_i16_f16" => Some(VFrexpExpI16F16(arguments))
      case "v_ldexp_f64" => Some(VLdexpF64(arguments))
      case "v_mad_co_u64_u32" => Some(VMadCoU64U32(arguments))
      case "v_max3_num_f32" => Some(VMax3NumF32(arguments))
      case "v_min3_i16" => Some(VMin3I16(arguments))
      case "v_mov_b32" => Some(VMovB32(arguments))
      case "v_readlane_b32" => Some(VReadlaneB32(arguments))
      case "v_smfmac_f32_32x32x64_fp8_fp8" => Some(VSmfmacF3232x32x64Fp8Fp8(arguments))
      case _ => None
    }

  private def decode14(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_and" => Some(BufferAtomicAnd(arguments))
      case "buffer_atomic_min_u32" => Some(BufferAtomicMinU32(arguments))
      case "buffer_inv" => Some(BufferInv(arguments))
      case "buffer_store_b128" => Some(BufferStoreB128(arguments))
      case "ds_add_rtn_u64" => Some(DsAddRtnU64(arguments))
      case "ds_bpermute_b32" => Some(DsBpermuteB32(arguments))
      case "ds_cmpst_rtn_b32" => Some(DsCmpstRtnB32(arguments))
      case "ds_load_b128" => Some(DsLoadB128(arguments))
      case "ds_min_i64" => Some(DsMinI64(arguments))
      case "ds_storexchg_2addr_stride64_rtn_b32" => Some(DsStorexchg2addrStride64RtnB32(arguments))
      case "flat_atomic_add_x2" => Some(FlatAtomicAddX2(arguments))
      case "flat_load_ushort" => Some(FlatLoadUshort(arguments))
      case "global_load_sbyte" => Some(GlobalLoadSbyte(arguments))
      case "global_store_short" => Some(GlobalStoreShort(arguments))
      case "image_load_pck_sgn" => Some(ImageLoadPckSgn(arguments))
      case "s_brev_b64" => Some(SBrevB64(arguments))
      case "s_cmov_b64" => Some(SCmovB64(arguments))
      case "s_floor_f32" => Some(SFloorF32(arguments))
      case "s_lshl2_add_u32" => Some(SLshl2AddU32(arguments))
      case "s_prefetch_data" => Some(SPrefetchData(arguments))
      case "s_wait_expcnt" => Some(SWaitExpcnt(arguments))
      case "tbuffer_store_format_d16_xy" => Some(TbufferStoreFormatD16Xy(arguments))
      case "v_add_f32" => Some(VAddF32(arguments))
      case "v_cmp_class_f16" => Some(VCmpClassF16(arguments))
      case "v_cmp_le_u16" => Some(VCmpLeU16(arguments))
      case "v_cmp_t_u64" => Some(VCmpTU64(arguments))
      case "v_cmpx_ge_f16" => Some(VCmpxGeF16(arguments))
      case "v_cmpx_nlg_f64" => Some(VCmpxNlgF64(arguments))
      case "v_cmpx_tru_u16" => Some(VCmpxTruU16(arguments))
      case "v_cvt_f64_i32" => Some(VCvtF64I32(arguments))
      case "v_cvt_pk_u8_f32" => Some(VCvtPkU8F32(arguments))
      case "v_cvt_scale_pk8_f32_fp4" => Some(VCvtScalePk8F32Fp4(arguments))
      case "v_div_fmas_f32" => Some(VDivFmasF32(arguments))
      case "v_dot2_f16_f16" => Some(VDot2F16F16(arguments))
      case "v_max_num_f16" => Some(VMaxNumF16(arguments))
      case "v_mfma_f32_32x32x4_2b_bf16" => Some(VMfmaF3232x32x42bBf16(arguments))
      case "v_min_num_f32" => Some(VMinNumF32(arguments))
      case "v_s_exp_f32" => Some(VSExpF32(arguments))
      case _ => None
    }

  private def decode15(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_and_x2" => Some(BufferAtomicAndX2(arguments))
      case "buffer_load_d16_b16" => Some(BufferLoadD16B16(arguments))
      case "buffer_load_sshort" => Some(BufferLoadSshort(arguments))
      case "flat_atomic_add" => Some(FlatAtomicAdd(arguments))
      case "global_load_short_d16_hi" => Some(GlobalLoadShortD16Hi(arguments))
      case "image_load_mip_pck_sgn" => Some(ImageLoadMipPckSgn(arguments))
      case "image_sample_c_lz" => Some(ImageSampleCLz(arguments))
      case "s_bitreplicate_b64_b32" => Some(SBitreplicateB64B32(arguments))
      case "s_call_b64" => Some(SCallB64(arguments))
      case "s_endpgm_saved" => Some(SEndpgmSaved(arguments))
      case "s_ff1_i32_b64" => Some(SFf1I32B64(arguments))
      case "s_wait_asynccnt" => Some(SWaitAsynccnt(arguments))
      case "scratch_load_sbyte" => Some(ScratchLoadSbyte(arguments))
      case "tbuffer_store_d16_format_xyzw" => Some(TbufferStoreD16FormatXyzw(arguments))
      case "v_add_nc_u16" => Some(VAddNcU16(arguments))
      case "v_cmp_class_f64" => Some(VCmpClassF64(arguments))
      case "v_cmp_le_u64" => Some(VCmpLeU64(arguments))
      case "v_cmp_t_i32" => Some(VCmpTI32(arguments))
      case "v_cmpx_ge_f64" => Some(VCmpxGeF64(arguments))
      case "v_cmpx_tru_u64" => Some(VCmpxTruU64(arguments))
      case "v_cvt_pknorm_i16_f32" => Some(VCvtPknormI16F32_2(arguments))
      case "v_cvt_pknorm_u16_f32" => Some(VCvtPknormU16F32_2(arguments))
      case "v_cvt_scalef32_f16_bf8" => Some(VCvtScalef32F16Bf8(arguments))
      case "v_cvt_scalef32_f32_fp8" => Some(VCvtScalef32F32Fp8(arguments))
      case "v_fract_f32" => Some(VFractF32(arguments))
      case "v_max3_i16" => Some(VMax3I16(arguments))
      case "v_max_f16" => Some(VMaxF16(arguments))
      case "v_max_num_f64" => Some(VMaxNumF64(arguments))
      case "v_maximum_f16" => Some(VMaximumF16(arguments))
      case "v_mfma_f32_4x4x1f32" => Some(VMfmaF324x4x1f32(arguments))
      case "v_mfma_f32_4x4x4bf16_1k" => Some(VMfmaF324x4x4bf161k(arguments))
      case "v_minimum_f32" => Some(VMinimumF32(arguments))
      case "v_mul_f16" => Some(VMulF16(arguments))
      case "v_permlane_up_b32" => Some(VPermlaneUpB32(arguments))
      case "v_sad_hi_u8" => Some(VSadHiU8(arguments))
      case "v_trunc_f16" => Some(VTruncF16(arguments))
      case _ => None
    }

  private def decode16(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_sub_clamp_u32" => Some(BufferAtomicSubClampU32(arguments))
      case "ds_cmpstore_f64" => Some(DsCmpstoreF64(arguments))
      case "ds_dec_src2_u32" => Some(DsDecSrc2U32(arguments))
      case "ds_gws_init" => Some(DsGwsInit(arguments))
      case "ds_or_b64" => Some(DsOrB64(arguments))
      case "flat_atomic_max_f32" => Some(FlatAtomicMaxF32(arguments))
      case "flat_store_byte" => Some(FlatStoreByte(arguments))
      case "flat_store_dwordx2" => Some(FlatStoreDwordx2(arguments))
      case "global_load_d16_b16" => Some(GlobalLoadD16B16(arguments))
      case "image_gather4_b" => Some(ImageGather4B(arguments))
      case "image_gather4h_pck" => Some(ImageGather4hPck(arguments))
      case "image_load_mip_pck" => Some(ImageLoadMipPck(arguments))
      case "s_absdiff_i32" => Some(SAbsdiffI32(arguments))
      case "s_atomic_add_x2" => Some(SAtomicAddX2(arguments))
      case "s_cmp_lg_f16" => Some(SCmpLgF16(arguments))
      case "s_cmp_o_f32" => Some(SCmpOF32(arguments))
      case "s_dcache_wb_vol" => Some(SDcacheWbVol(arguments))
      case "s_sub_nc_u64" => Some(SSubNcU64(arguments))
      case "v_add_nc_u64" => Some(VAddNcU64(arguments))
      case "v_bfrev_b32" => Some(VBfrevB32(arguments))
      case "v_cmp_f_f16" => Some(VCmpFF16(arguments))
      case "v_cmp_le_i32" => Some(VCmpLeI32(arguments))
      case "v_cmpx_nge_f16" => Some(VCmpxNgeF16(arguments))
      case "v_cmpx_t_f16" => Some(VCmpxTF16(arguments))
      case "v_cmpx_tru_i32" => Some(VCmpxTruI32(arguments))
      case "v_cvt_pkrtz_f16_f32" => Some(VCvtPkrtzF16F32_2(arguments))
      case "v_cvt_scale_pk8_bf16_bf8" => Some(VCvtScalePk8Bf16Bf8(arguments))
      case "v_dual_fmamk_f32" => Some(VDualFmamkF32(arguments))
      case "v_ffbh_i32" => Some(VFfbhI32(arguments))
      case "v_interp_p2_legacy_f16" => Some(VInterpP2LegacyF16(arguments))
      case "v_max_f64" => Some(VMaxF64(arguments))
      case "v_maximum_f64" => Some(VMaximumF64(arguments))
      case "v_med3_f16" => Some(VMed3F16(arguments))
      case "v_mqsad_u32_u8" => Some(VMqsadU32U8(arguments))
      case "v_mul_f64" => Some(VMulF64(arguments))
      case "v_rndne_f32" => Some(VRndneF32(arguments))
      case "v_smfmac_f32_32x32x32_f16" => Some(VSmfmacF3232x32x32F16(arguments))
      case "v_sub_u32" => Some(VSubU32(arguments))
      case "v_trunc_f64" => Some(VTruncF64(arguments))
      case _ => None
    }

  private def decode17(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_inc_u64" => Some(BufferAtomicIncU64(arguments))
      case "ds_read_u16" => Some(DsReadU16(arguments))
      case "ds_write_b32" => Some(DsWriteB32(arguments))
      case "flat_atomic_fmax_x2" => Some(FlatAtomicFmaxX2(arguments))
      case "flat_store_dwordx3" => Some(FlatStoreDwordx3(arguments))
      case "global_atomic_csub" => Some(GlobalAtomicCsub(arguments))
      case "global_atomic_max_f64" => Some(GlobalAtomicMaxF64(arguments))
      case "global_atomic_sub_u32" => Some(GlobalAtomicSubU32(arguments))
      case "image_gather4_c" => Some(ImageGather4C(arguments))
      case "s_ashr_i32" => Some(SAshrI32(arguments))
      case "s_atomic_umin" => Some(SAtomicUmin(arguments))
      case "s_dcache_discard" => Some(SDcacheDiscard(arguments))
      case "s_or_b32" => Some(SOrB32(arguments))
      case "scratch_load_sbyte_d16" => Some(ScratchLoadSbyteD16(arguments))
      case "v_add_nc_i32" => Some(VAddNcI32(arguments))
      case "v_addc_co_u32" => Some(VAddcCoU32(arguments))
      case "v_cmp_f_f64" => Some(VCmpFF64(arguments))
      case "v_cmpx_nge_f64" => Some(VCmpxNgeF64(arguments))
      case "v_cmpx_t_f64" => Some(VCmpxTF64(arguments))
      case "v_cvt_scalef32_pk8_fp8_bf16" => Some(VCvtScalef32Pk8Fp8Bf16(arguments))
      case "v_cvt_scalef32_pk_bf8_f16" => Some(VCvtScalef32PkBf8F16(arguments))
      case "v_dot2c_i32_i16" => Some(VDot2cI32I16(arguments))
      case "v_dual_min_num_f32" => Some(VDualMinNumF32(arguments))
      case "v_dual_mul_dx9_zero_f32" => Some(VDualMulDx9ZeroF32(arguments))
      case "v_swmmac_i32_16x16x128_iu8" => Some(VSwmmacI3216x16x128Iu8(arguments))
      case _ => None
    }

  private def decode18(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_pk_add_bf16" => Some(BufferAtomicPkAddBf16(arguments))
      case "buffer_atomic_pk_add_f16" => Some(BufferAtomicPkAddF16(arguments))
      case "buffer_load_d16_format_xyzw" => Some(BufferLoadD16FormatXyzw(arguments))
      case "buffer_load_format_d16_xyzw" => Some(BufferLoadFormatD16Xyzw(arguments))
      case "buffer_store_b96" => Some(BufferStoreB96(arguments))
      case "ds_append" => Some(DsAppend(arguments))
      case "ds_load_2addr_stride64_b32" => Some(DsLoad2addrStride64B32(arguments))
      case "ds_max_rtn_u32" => Some(DsMaxRtnU32(arguments))
      case "ds_read_b96" => Some(DsReadB96(arguments))
      case "ds_read_u8_d16" => Some(DsReadU8D16(arguments))
      case "flat_atomic_and_b64" => Some(FlatAtomicAndB64(arguments))
      case "flat_load_u8" => Some(FlatLoadU8(arguments))
      case "flat_load_ubyte" => Some(FlatLoadUbyte(arguments))
      case "flat_store_dwordx4" => Some(FlatStoreDwordx4(arguments))
      case "flat_store_short_d16_hi" => Some(FlatStoreShortD16Hi(arguments))
      case "global_load_d16_i8" => Some(GlobalLoadD16I8(arguments))
      case "global_store_async_from_lds_b8" => Some(GlobalStoreAsyncFromLdsB8(arguments))
      case "image_sample_c_cd" => Some(ImageSampleCCd(arguments))
      case "image_sample_c_cl_o" => Some(ImageSampleCClO(arguments))
      case "s_add_i32" => Some(SAddI32(arguments))
      case "s_and_not0_saveexec_b32" => Some(SAndNot0SaveexecB32(arguments))
      case "s_atomic_smin_x2" => Some(SAtomicSminX2(arguments))
      case "s_barrier_join" => Some(SBarrierJoin(arguments))
      case "s_cmpk_lg_i32" => Some(SCmpkLgI32(arguments))
      case "s_fmamk_f32" => Some(SFmamkF32(arguments))
      case "s_min_f16" => Some(SMinF16(arguments))
      case "s_store_dwordx2" => Some(SStoreDwordx2(arguments))
      case "s_xnor_b64" => Some(SXnorB64(arguments))
      case "v_cmp_neq_f32" => Some(VCmpNeqF32(arguments))
      case "v_cos_f16" => Some(VCosF16(arguments))
      case "v_cvt_f32_f16" => Some(VCvtF32F16(arguments))
      case "v_cvt_norm_u16_f16" => Some(VCvtNormU16F16(arguments))
      case "v_cvt_scale_pk8_f16_bf8" => Some(VCvtScalePk8F16Bf8(arguments))
      case "v_cvt_scale_pk8_f32_fp8" => Some(VCvtScalePk8F32Fp8(arguments))
      case "v_cvt_scalef32_sr_pk16_bf6_f16" => Some(VCvtScalef32SrPk16Bf6F16(arguments))
      case "v_cvt_scalef32_sr_pk32_fp6_bf16" => Some(VCvtScalef32SrPk32Fp6Bf16(arguments))
      case "v_dual_fma_f32" => Some(VDualFmaF32(arguments))
      case "v_dual_max_num_f64" => Some(VDualMaxNumF64(arguments))
      case "v_min3_f16" => Some(VMin3F16(arguments))
      case "v_not_b16" => Some(VNotB16(arguments))
      case "v_permlane_down_b32" => Some(VPermlaneDownB32(arguments))
      case "v_swap_b32" => Some(VSwapB32(arguments))
      case "v_tanh_f32" => Some(VTanhF32(arguments))
      case _ => None
    }

  private def decode19(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "cluster_load_async_to_lds_b8" => Some(ClusterLoadAsyncToLdsB8(arguments))
      case "ds_min_f64" => Some(DsMinF64(arguments))
      case "ds_min_num_rtn_f64" => Some(DsMinNumRtnF64(arguments))
      case "ds_store_b128" => Some(DsStoreB128(arguments))
      case "ds_write_src2_b64" => Some(DsWriteSrc2B64(arguments))
      case "flat_store_b16" => Some(FlatStoreB16(arguments))
      case "global_atomic_umax" => Some(GlobalAtomicUmax(arguments))
      case "image_atomic_max_flt" => Some(ImageAtomicMaxFlt(arguments))
      case "image_gather4h" => Some(ImageGather4h(arguments))
      case "s_atomic_smin" => Some(SAtomicSmin(arguments))
      case "s_cmp_ge_f16" => Some(SCmpGeF16(arguments))
      case "s_endpgm" => Some(SEndpgm(arguments))
      case "s_max_num_f16" => Some(SMaxNumF16(arguments))
      case "tbuffer_load_format_xy" => Some(TbufferLoadFormatXy(arguments))
      case "tbuffer_store_d16_format_x" => Some(TbufferStoreD16FormatX(arguments))
      case "v_cmpx_eq_u16" => Some(VCmpxEqU16(arguments))
      case "v_cmpx_o_f16" => Some(VCmpxOF16(arguments))
      case "v_ctz_i32_b32" => Some(VCtzI32B32(arguments))
      case "v_cvt_f32_f64" => Some(VCvtF32F64(arguments))
      case "v_cvt_f64_f32" => Some(VCvtF64F32(arguments))
      case "v_cvt_scalef32_pk_fp8_f16" => Some(VCvtScalef32PkFp8F16(arguments))
      case "v_log_legacy_f32" => Some(VLogLegacyF32(arguments))
      case "v_s_sqrt_f32" => Some(VSSqrtF32(arguments))
      case _ => None
    }

  private def decode20(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_max_u32" => Some(BufferAtomicMaxU32(arguments))
      case "buffer_atomic_sub_u32" => Some(BufferAtomicSubU32(arguments))
      case "buffer_wbl2" => Some(BufferWbl2(arguments))
      case "ds_add_rtn_f32" => Some(DsAddRtnF32(arguments))
      case "ds_max_i64" => Some(DsMaxI64(arguments))
      case "ds_wrxchg2_rtn_b32" => Some(DsWrxchg2RtnB32(arguments))
      case "flat_atomic_add_f64" => Some(FlatAtomicAddF64(arguments))
      case "flat_store_b64" => Some(FlatStoreB64(arguments))
      case "global_atomic_or_b64" => Some(GlobalAtomicOrB64(arguments))
      case "s_atomic_umin_x2" => Some(SAtomicUminX2(arguments))
      case "s_cmp_nle_f32" => Some(SCmpNleF32(arguments))
      case "s_movreld_b64" => Some(SMovreldB64(arguments))
      case "s_store_dwordx4" => Some(SStoreDwordx4(arguments))
      case "tbuffer_store_d16_format_xyz" => Some(TbufferStoreD16FormatXyz(arguments))
      case "v_clrexcp" => Some(VClrexcp(arguments))
      case "v_cmp_t_f32" => Some(VCmpTF32(arguments))
      case "v_cmpx_eq_u64" => Some(VCmpxEqU64(arguments))
      case "v_cmpx_o_f64" => Some(VCmpxOF64(arguments))
      case "v_cvt_scalef32_sr_pk16_fp6_f16" => Some(VCvtScalef32SrPk16Fp6F16(arguments))
      case "v_mad_legacy_u16" => Some(VMadLegacyU16(arguments))
      case "v_max3_f16" => Some(VMax3F16(arguments))
      case "v_mfma_f32_16x16x4_4b_bf16" => Some(VMfmaF3216x16x44bBf16(arguments))
      case "v_mfma_f32_32x32x2bf16" => Some(VMfmaF3232x32x2bf16(arguments))
      case "v_mfma_i32_16x16x64_i8" => Some(VMfmaI3216x16x64I8(arguments))
      case "v_minmax_i32" => Some(VMinmaxI32(arguments))
      case "v_permlane16_var_b32" => Some(VPermlane16VarB32(arguments))
      case "v_pk_add_min_u16" => Some(VPkAddMinU16(arguments))
      case "v_pk_fmac_f16" => Some(VPkFmacF16(arguments))
      case "v_pk_mad_u16" => Some(VPkMadU16(arguments))
      case "v_smfmac_f32_16x16x128_bf8_bf8" => Some(VSmfmacF3216x16x128Bf8Bf8(arguments))
      case "v_smfmac_f32_32x32x32_bf16" => Some(VSmfmacF3232x32x32Bf16(arguments))
      case _ => None
    }

  private def decode21(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_add_x2" => Some(BufferAtomicAddX2(arguments))
      case "ds_storexchg_2addr_rtn_b64" => Some(DsStorexchg2addrRtnB64(arguments))
      case "flat_atomic_and" => Some(FlatAtomicAnd(arguments))
      case "flat_store_b128" => Some(FlatStoreB128(arguments))
      case "global_atomic_fmin_x2" => Some(GlobalAtomicFminX2(arguments))
      case "global_atomic_smax" => Some(GlobalAtomicSmax(arguments))
      case "global_load_async_to_lds_b128" => Some(GlobalLoadAsyncToLdsB128(arguments))
      case "global_load_i16" => Some(GlobalLoadI16(arguments))
      case "global_load_lds_dword" => Some(GlobalLoadLdsDword(arguments))
      case "image_atomic_umin" => Some(ImageAtomicUmin(arguments))
      case "s_and_b32" => Some(SAndB32(arguments))
      case "s_atomic_add" => Some(SAtomicAdd(arguments))
      case "s_cmpk_ge_i32" => Some(SCmpkGeI32(arguments))
      case "s_rfe_i64" => Some(SRfeI64(arguments))
      case "tbuffer_load_format_d16_xyz" => Some(TbufferLoadFormatD16Xyz(arguments))
      case "v_cmp_le_f32" => Some(VCmpLeF32(arguments))
      case "v_cmpx_eq_i32" => Some(VCmpxEqI32(arguments))
      case "v_cmpx_lt_u32" => Some(VCmpxLtU32(arguments))
      case "v_cmpx_tru_f32" => Some(VCmpxTruF32(arguments))
      case "v_cvt_scalef32_pk_f32_fp4" => Some(VCvtScalef32PkF32Fp4(arguments))
      case "v_floor_f32" => Some(VFloorF32(arguments))
      case "v_frexp_exp_i32_f32" => Some(VFrexpExpI32F32(arguments))
      case "v_frexp_mant_f16" => Some(VFrexpMantF16(arguments))
      case "v_mfma_f32_4x4x4_16b_bf16" => Some(VMfmaF324x4x416bBf16(arguments))
      case "v_movrelsd_b32" => Some(VMovrelsdB32(arguments))
      case "v_wmma_f32_16x16x16_bf8_bf8" => Some(VWmmaF3216x16x16Bf8Bf8(arguments))
      case "v_wmma_f32_16x16x32_f16" => Some(VWmmaF3216x16x32F16(arguments))
      case "v_wmma_i32_16x16x16_iu4" => Some(VWmmaI3216x16x16Iu4(arguments))
      case _ => None
    }

  private def decode22(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_dec_u32" => Some(BufferAtomicDecU32(arguments))
      case "buffer_atomic_rsub_x2" => Some(BufferAtomicRsubX2(arguments))
      case "ds_pk_add_rtn_f16" => Some(DsPkAddRtnF16(arguments))
      case "flat_atomic_cmpswap_x2" => Some(FlatAtomicCmpswapX2(arguments))
      case "flat_atomic_fmin" => Some(FlatAtomicFmin(arguments))
      case "flat_atomic_xor_b64" => Some(FlatAtomicXorB64(arguments))
      case "global_atomic_smin_x2" => Some(GlobalAtomicSminX2(arguments))
      case "global_load_lds_sshort" => Some(GlobalLoadLdsSshort(arguments))
      case "global_load_ubyte_d16" => Some(GlobalLoadUbyteD16(arguments))
      case "image_load_by2" => Some(ImageLoadBy2(arguments))
      case "image_sample_d_cl_o_g16" => Some(ImageSampleDClOG16(arguments))
      case "s_atc_probe_buffer" => Some(SAtcProbeBuffer(arguments))
      case "s_bfe_u32" => Some(SBfeU32(arguments))
      case "s_monitor_sleep" => Some(SMonitorSleep(arguments))
      case "s_mov_b32" => Some(SMovB32(arguments))
      case "s_wait_tensorcnt" => Some(SWaitTensorcnt(arguments))
      case "scratch_load_short_d16" => Some(ScratchLoadShortD16(arguments))
      case "v_bfi_b32" => Some(VBfiB32(arguments))
      case "v_cmp_gt_u16" => Some(VCmpGtU16(arguments))
      case "v_cmpx_class_f32" => Some(VCmpxClassF32(arguments))
      case "v_cvt_pk_norm_i16_f32" => Some(VCvtPkNormI16F32_1(arguments))
      case "v_cvt_pk_norm_u16_f32" => Some(VCvtPkNormU16F32_1(arguments))
      case "v_cvt_scalef32_pk32_bf6_bf16" => Some(VCvtScalef32Pk32Bf6Bf16(arguments))
      case "v_cvt_scalef32_pk32_f16_bf6" => Some(VCvtScalef32Pk32F16Bf6(arguments))
      case "v_cvt_scalef32_pk32_f32_fp6" => Some(VCvtScalef32Pk32F32Fp6(arguments))
      case "v_frexp_mant_f64" => Some(VFrexpMantF64(arguments))
      case "v_mad_i16" => Some(VMadI16(arguments))
      case "v_maximum3_f32" => Some(VMaximum3F32(arguments))
      case "v_mfma_f32_4x4x1_16b_f32" => Some(VMfmaF324x4x116bF32(arguments))
      case "v_mul_hi_u32_u24" => Some(VMulHiU32U24(arguments))
      case "v_nop" => Some(VNop(arguments))
      case "v_pk_min_i16" => Some(VPkMinI16(arguments))
      case "v_sad_u32" => Some(VSadU32(arguments))
      case "v_smfmac_f32_16x16x128_fp8_bf8" => Some(VSmfmacF3216x16x128Fp8Bf8(arguments))
      case _ => None
    }

  private def decode23(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "ds_min_rtn_u64" => Some(DsMinRtnU64(arguments))
      case "ds_store_2addr_stride64_b32" => Some(DsStore2addrStride64B32(arguments))
      case "flat_atomic_or_b32" => Some(FlatAtomicOrB32(arguments))
      case "image_atomic_dec_uint" => Some(ImageAtomicDecUint(arguments))
      case "image_atomic_smin" => Some(ImageAtomicSmin(arguments))
      case "image_atomic_xor" => Some(ImageAtomicXor(arguments))
      case "image_msaa_load" => Some(ImageMsaaLoad(arguments))
      case "image_sample_c_cd_g16" => Some(ImageSampleCCdG16(arguments))
      case "image_store_mip_pck2" => Some(ImageStoreMipPck2(arguments))
      case "s_add_f32" => Some(SAddF32(arguments))
      case "s_atomic_or" => Some(SAtomicOr(arguments))
      case "s_buffer_load_b256" => Some(SBufferLoadB256(arguments))
      case "s_cvt_f16_f32" => Some(SCvtF16F32(arguments))
      case "s_movrelsd_2_b32" => Some(SMovrelsd2B32(arguments))
      case "s_ttracedata" => Some(STtracedata(arguments))
      case "s_xor_saveexec_b64" => Some(SXorSaveexecB64(arguments))
      case "scratch_load_sbyte_d16_hi" => Some(ScratchLoadSbyteD16Hi(arguments))
      case "v_add_lshl_u32" => Some(VAddLshlU32(arguments))
      case "v_cmp_gt_u64" => Some(VCmpGtU64(arguments))
      case "v_cmp_o_f32" => Some(VCmpOF32(arguments))
      case "v_cvt_f32_bf8" => Some(VCvtF32Bf8(arguments))
      case "v_cvt_scalef32_pk8_fp4_f16" => Some(VCvtScalef32Pk8Fp4F16(arguments))
      case "v_dual_and_b32" => Some(VDualAndB32(arguments))
      case "v_mfma_f32_16x16x8_xf32" => Some(VMfmaF3216x16x8Xf32(arguments))
      case "v_mfma_i32_32x32x32_i8" => Some(VMfmaI3232x32x32I8(arguments))
      case "v_mfma_i32_32x32x8i8" => Some(VMfmaI3232x32x8i8(arguments))
      case "v_sqrt_f32" => Some(VSqrtF32(arguments))
      case "v_wmma_f32_16x16x16_fp8_bf8" => Some(VWmmaF3216x16x16Fp8Bf8(arguments))
      case _ => None
    }

  private def decode24(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_store_format_d16_xyz" => Some(BufferStoreFormatD16Xyz(arguments))
      case "ds_and_src2_b32" => Some(DsAndSrc2B32(arguments))
      case "ds_min_rtn_i32" => Some(DsMinRtnI32(arguments))
      case "ds_write_b8_d16_hi" => Some(DsWriteB8D16Hi(arguments))
      case "flat_atomic_min_i64" => Some(FlatAtomicMinI64(arguments))
      case "flat_atomic_or_x2" => Some(FlatAtomicOrX2(arguments))
      case "flat_atomic_swap_x2" => Some(FlatAtomicSwapX2(arguments))
      case "global_load_dword_addtid" => Some(GlobalLoadDwordAddtid(arguments))
      case "global_store_async_from_lds_b64" => Some(GlobalStoreAsyncFromLdsB64(arguments))
      case "global_store_b8" => Some(GlobalStoreB8(arguments))
      case "image_atomic_sub" => Some(ImageAtomicSub(arguments))
      case "image_load_by4" => Some(ImageLoadBy4(arguments))
      case "s_and_not0_wrexec_b32" => Some(SAndNot0WrexecB32(arguments))
      case "s_and_not1_saveexec_b64" => Some(SAndNot1SaveexecB64(arguments))
      case "s_ctz_i32_b32" => Some(SCtzI32B32(arguments))
      case "s_cvt_f32_u32" => Some(SCvtF32U32(arguments))
      case "s_max_f16" => Some(SMaxF16(arguments))
      case "s_memrealtime" => Some(SMemrealtime(arguments))
      case "s_mul_f16" => Some(SMulF16(arguments))
      case "s_sub_f16" => Some(SSubF16(arguments))
      case "s_wqm_b64" => Some(SWqmB64(arguments))
      case "v_add3_u32" => Some(VAdd3U32(arguments))
      case "v_cmp_gt_i32" => Some(VCmpGtI32(arguments))
      case "v_cmp_tru_i16" => Some(VCmpTruI16(arguments))
      case "v_cvt_flr_i32_f32" => Some(VCvtFlrI32F32(arguments))
      case "v_cvt_pkrtz_f16_f32_e64" => Some(VCvtPkrtzF16F32E64(arguments))
      case "v_cvt_scalef32_pk32_bf16_bf6" => Some(VCvtScalef32Pk32Bf16Bf6(arguments))
      case "v_mad_i32_i24" => Some(VMadI32I24(arguments))
      case "v_permlane32_swap_b32" => Some(VPermlane32SwapB32(arguments))
      case "v_pk_minimum3_f16" => Some(VPkMinimum3F16(arguments))
      case "v_pk_mul_lo_u16" => Some(VPkMulLoU16(arguments))
      case "v_writelane_b32_e64" => Some(VWritelaneB32E64(arguments))
      case _ => None
    }

  private def decode25(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_cond_sub_u32" => Some(BufferAtomicCondSubU32(arguments))
      case "buffer_atomic_inc" => Some(BufferAtomicInc(arguments))
      case "buffer_atomic_smin_x2" => Some(BufferAtomicSminX2(arguments))
      case "buffer_store_d16_hi_b8" => Some(BufferStoreD16HiB8(arguments))
      case "buffer_store_dword" => Some(BufferStoreDword(arguments))
      case "ds_load_u16" => Some(DsLoadU16(arguments))
      case "ds_max_f64" => Some(DsMaxF64(arguments))
      case "ds_sub_u32" => Some(DsSubU32(arguments))
      case "ds_write_b128" => Some(DsWriteB128(arguments))
      case "flat_atomic_cmpswap_b32" => Some(FlatAtomicCmpswapB32(arguments))
      case "flat_atomic_swap" => Some(FlatAtomicSwap(arguments))
      case "flat_load_b64" => Some(FlatLoadB64(arguments))
      case "flat_load_short_d16_hi" => Some(FlatLoadShortD16Hi(arguments))
      case "global_atomic_add" => Some(GlobalAtomicAdd(arguments))
      case "global_store_d16_hi_b16" => Some(GlobalStoreD16HiB16(arguments))
      case "image_sample_d_cl" => Some(ImageSampleDCl(arguments))
      case "image_store_mip_pck4" => Some(ImageStoreMipPck4(arguments))
      case "s_andn2_b32" => Some(SAndn2B32(arguments))
      case "s_atomic_cmpswap" => Some(SAtomicCmpswap(arguments))
      case "s_bitcmp1_b64" => Some(SBitcmp1B64(arguments))
      case "s_buffer_atomic_swap_x2" => Some(SBufferAtomicSwapX2(arguments))
      case "s_buffer_store_dword" => Some(SBufferStoreDword(arguments))
      case "s_cmp_eq_u64" => Some(SCmpEqU64(arguments))
      case "s_flbit_i32_b64" => Some(SFlbitI32B64(arguments))
      case "s_quadmask_b32" => Some(SQuadmaskB32(arguments))
      case "s_wait_kmcnt" => Some(SWaitKmcnt(arguments))
      case "s_wait_storecnt_dscnt" => Some(SWaitStorecntDscnt(arguments))
      case "s_xor_b32" => Some(SXorB32(arguments))
      case "scratch_load_b32" => Some(ScratchLoadB32(arguments))
      case "v_cmp_lt_i16" => Some(VCmpLtI16(arguments))
      case "v_cmp_tru_i64" => Some(VCmpTruI64(arguments))
      case "v_cvt_scalef32_pk_f16_bf8" => Some(VCvtScalef32PkF16Bf8(arguments))
      case "v_cvt_scalef32_pk_f32_fp8" => Some(VCvtScalef32PkF32Fp8(arguments))
      case "v_div_fixup_f16" => Some(VDivFixupF16(arguments))
      case "v_dual_add_f32" => Some(VDualAddF32(arguments))
      case "v_fma_f16" => Some(VFmaF16(arguments))
      case "v_log_bf16" => Some(VLogBf16(arguments))
      case "v_lshl_b64" => Some(VLshlB64(arguments))
      case "v_lshlrev_b32" => Some(VLshlrevB32(arguments))
      case "v_minmax_f32" => Some(VMinmaxF32(arguments))
      case "v_permlane64_b32" => Some(VPermlane64B32(arguments))
      case "v_pk_add_u16" => Some(VPkAddU16(arguments))
      case "v_pk_fma_f32" => Some(VPkFmaF32(arguments))
      case "v_pk_max3_i16" => Some(VPkMax3I16(arguments))
      case "v_swmmac_f32_16x16x32_bf8_fp8" => Some(VSwmmacF3216x16x32Bf8Fp8(arguments))
      case "v_wmma_bf16_16x16x16_bf16" => Some(VWmmaBf1616x16x16Bf16(arguments))
      case "v_wmma_f32_16x16x16_bf16" => Some(VWmmaF3216x16x16Bf16(arguments))
      case "v_wmma_i32_16x16x16_iu8" => Some(VWmmaI3216x16x16Iu8(arguments))
      case _ => None
    }

  private def decode26(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_b64" => Some(BufferLoadB64(arguments))
      case "ds_load_b96" => Some(DsLoadB96(arguments))
      case "ds_min_num_f64" => Some(DsMinNumF64(arguments))
      case "flat_atomic_swap_b64" => Some(FlatAtomicSwapB64(arguments))
      case "flat_load_i8" => Some(FlatLoadI8(arguments))
      case "global_atomic_min_num_f64" => Some(GlobalAtomicMinNumF64(arguments))
      case "global_store_byte" => Some(GlobalStoreByte(arguments))
      case "image_atomic_fmax" => Some(ImageAtomicFmax(arguments))
      case "image_bvh_dual_intersect_ray" => Some(ImageBvhDualIntersectRay(arguments))
      case "image_gather4_l" => Some(ImageGather4L(arguments))
      case "image_sample_c_cl" => Some(ImageSampleCCl(arguments))
      case "image_store_mip" => Some(ImageStoreMip(arguments))
      case "s_buffer_atomic_sub_x2" => Some(SBufferAtomicSubX2(arguments))
      case "s_cmp_eq_i32" => Some(SCmpEqI32(arguments))
      case "s_cmp_lt_u32" => Some(SCmpLtU32(arguments))
      case "s_get_shader_cycles_u64" => Some(SGetShaderCyclesU64(arguments))
      case "s_get_waveid_in_workgroup" => Some(SGetWaveidInWorkgroup(arguments))
      case "s_gl1_inv" => Some(SGl1Inv(arguments))
      case "s_or_not0_saveexec_b32" => Some(SOrNot0SaveexecB32(arguments))
      case "s_prefetch_inst" => Some(SPrefetchInst(arguments))
      case "scratch_store_b32" => Some(ScratchStoreB32(arguments))
      case "v_cmp_lt_i64" => Some(VCmpLtI64(arguments))
      case "v_cmpx_eq_f32" => Some(VCmpxEqF32(arguments))
      case "v_cmpx_ne_u16" => Some(VCmpxNeU16(arguments))
      case "v_div_fixup_f64" => Some(VDivFixupF64(arguments))
      case "v_fma_f64" => Some(VFmaF64(arguments))
      case "v_mbcnt_lo_u32_b32" => Some(VMbcntLoU32B32(arguments))
      case "v_pk_add_max_u16" => Some(VPkAddMaxU16(arguments))
      case "v_smfmac_f32_32x32x32_fp8_fp8" => Some(VSmfmacF3232x32x32Fp8Fp8(arguments))
      case _ => None
    }

  private def decode27(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_umin_x2" => Some(BufferAtomicUminX2(arguments))
      case "buffer_store_d16_format_xyzw" => Some(BufferStoreD16FormatXyzw(arguments))
      case "ds_gws_sema_p" => Some(DsGwsSemaP(arguments))
      case "ds_load_2addr_b64" => Some(DsLoad2addrB64(arguments))
      case "ds_or_src2_b64" => Some(DsOrSrc2B64(arguments))
      case "ds_rsub_u32" => Some(DsRsubU32(arguments))
      case "global_atomic_inc_u32" => Some(GlobalAtomicIncU32(arguments))
      case "global_atomic_pk_add_bf16" => Some(GlobalAtomicPkAddBf16(arguments))
      case "image_atomic_pk_add_bf16" => Some(ImageAtomicPkAddBf16(arguments))
      case "image_sample_b_cl" => Some(ImageSampleBCl(arguments))
      case "image_sample_c_cd_o_g16" => Some(ImageSampleCCdOG16(arguments))
      case "s_atomic_and" => Some(SAtomicAnd(arguments))
      case "s_buffer_atomic_xor_x2" => Some(SBufferAtomicXorX2(arguments))
      case "s_nor_b32" => Some(SNorB32(arguments))
      case "s_orn2_saveexec_b64" => Some(SOrn2SaveexecB64(arguments))
      case "scratch_store_byte" => Some(ScratchStoreByte(arguments))
      case "v_add_i16" => Some(VAddI16(arguments))
      case "v_cmpx_f_u32" => Some(VCmpxFU32(arguments))
      case "v_cmpx_ne_u64" => Some(VCmpxNeU64(arguments))
      case "v_cvt_i16_f16" => Some(VCvtI16F16(arguments))
      case "v_cvt_sr_bf8_f16" => Some(VCvtSrBf8F16(arguments))
      case "v_cvt_u16_f16" => Some(VCvtU16F16(arguments))
      case "v_mad_f16" => Some(VMadF16(arguments))
      case "v_mad_legacy_f32" => Some(VMadLegacyF32(arguments))
      case "v_max_legacy_f32" => Some(VMaxLegacyF32(arguments))
      case "v_med3_num_f16" => Some(VMed3NumF16(arguments))
      case "v_perm_b32" => Some(VPermB32(arguments))
      case "v_pk_min_f16" => Some(VPkMinF16(arguments))
      case "v_swmmac_f32_16x16x32_fp8_fp8" => Some(VSwmmacF3216x16x32Fp8Fp8(arguments))
      case "v_wmma_bf16_16x16x32_bf16" => Some(VWmmaBf1616x16x32Bf16(arguments))
      case "v_wmma_scale_f32_32x16x128_f4" => Some(VWmmaScaleF3232x16x128F4(arguments))
      case _ => None
    }

  private def decode28(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "ds_add_gs_reg_rtn" => Some(DsAddGsRegRtn(arguments))
      case "ds_cond_sub_u32" => Some(DsCondSubU32(arguments))
      case "ds_load_tr16_b128" => Some(DsLoadTr16B128(arguments))
      case "ds_min_src2_u32" => Some(DsMinSrc2U32(arguments))
      case "ds_xor_src2_b32" => Some(DsXorSrc2B32(arguments))
      case "global_load_ubyte" => Some(GlobalLoadUbyte(arguments))
      case "global_store_byte_d16_hi" => Some(GlobalStoreByteD16Hi(arguments))
      case "global_store_d16_hi_b8" => Some(GlobalStoreD16HiB8(arguments))
      case "image_gather4_b_cl_o" => Some(ImageGather4BClO(arguments))
      case "s_atomic_smax_x2" => Some(SAtomicSmaxX2(arguments))
      case "s_cmp_ngt_f32" => Some(SCmpNgtF32(arguments))
      case "s_lshl_b64" => Some(SLshlB64(arguments))
      case "s_orn2_b64" => Some(SOrn2B64(arguments))
      case "s_sendmsg_rtn_b32" => Some(SSendmsgRtnB32(arguments))
      case "s_set_gpr_idx_mode" => Some(SSetGprIdxMode(arguments))
      case "v_cmp_lg_u16" => Some(VCmpLgU16(arguments))
      case "v_cmpx_ne_i32" => Some(VCmpxNeI32(arguments))
      case "v_cmpx_neq_f32" => Some(VCmpxNeqF32(arguments))
      case "v_cndmask_b16" => Some(VCndmaskB16(arguments))
      case "v_cubema_f32" => Some(VCubemaF32(arguments))
      case "v_cvt_u32_u16" => Some(VCvtU32U16(arguments))
      case "v_dual_lshlrev_b32" => Some(VDualLshlrevB32(arguments))
      case "v_interp_mov_f32" => Some(VInterpMovF32(arguments))
      case "v_mfma_f32_4x4x2bf16" => Some(VMfmaF324x4x2bf16(arguments))
      case "v_mfma_i32_16x16x16i8" => Some(VMfmaI3216x16x16i8(arguments))
      case "v_mul_lo_u32" => Some(VMulLoU32(arguments))
      case "v_pk_max_i16" => Some(VPkMaxI16(arguments))
      case "v_pk_sub_i16" => Some(VPkSubI16(arguments))
      case "v_rsq_clamp_f64" => Some(VRsqClampF64(arguments))
      case _ => None
    }

  private def decode29(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_d16_format_xy" => Some(BufferLoadD16FormatXy(arguments))
      case "buffer_load_sbyte_d16_hi" => Some(BufferLoadSbyteD16Hi(arguments))
      case "ds_and_rtn_b64" => Some(DsAndRtnB64(arguments))
      case "ds_cmpstore_b32" => Some(DsCmpstoreB32(arguments))
      case "ds_max_src2_u64" => Some(DsMaxSrc2U64(arguments))
      case "ds_min_rtn_f32" => Some(DsMinRtnF32(arguments))
      case "ds_sub_clamp_u32" => Some(DsSubClampU32(arguments))
      case "ds_sub_gs_reg_rtn" => Some(DsSubGsRegRtn(arguments))
      case "flat_atomic_min_f64" => Some(FlatAtomicMinF64(arguments))
      case "global_atomic_sub_x2" => Some(GlobalAtomicSubX2(arguments))
      case "global_atomic_umin" => Some(GlobalAtomicUmin(arguments))
      case "image_atomic_max_uint" => Some(ImageAtomicMaxUint(arguments))
      case "image_atomic_sub_uint" => Some(ImageAtomicSubUint(arguments))
      case "image_gather4_o" => Some(ImageGather4O(arguments))
      case "image_sample_c_d_cl_o" => Some(ImageSampleCDClO_1(arguments))
      case "s_cmp_nlt_f16" => Some(SCmpNltF16(arguments))
      case "scratch_load_ubyte" => Some(ScratchLoadUbyte(arguments))
      case "tbuffer_load_d16_format_xyz" => Some(TbufferLoadD16FormatXyz(arguments))
      case "v_cmp_gt_f32" => Some(VCmpGtF32(arguments))
      case "v_cmp_lg_u64" => Some(VCmpLgU64(arguments))
      case "v_cmp_tru_f16" => Some(VCmpTruF16(arguments))
      case "v_cvt_pk_bf8_f32" => Some(VCvtPkBf8F32(arguments))
      case "v_cvt_pk_f16_fp8" => Some(VCvtPkF16Fp8(arguments))
      case "v_cvt_scalef32_pk_fp8_bf16" => Some(VCvtScalef32PkFp8Bf16(arguments))
      case "v_cvt_scalef32_sr_pk_fp4_f16" => Some(VCvtScalef32SrPkFp4F16(arguments))
      case "v_dot4_i32_i8" => Some(VDot4I32I8(arguments))
      case "v_dual_min_i32" => Some(VDualMinI32(arguments))
      case "v_pipeflush" => Some(VPipeflush(arguments))
      case "v_pk_max3_num_f16" => Some(VPkMax3NumF16(arguments))
      case "v_sin_f32" => Some(VSinF32(arguments))
      case "v_smfmac_f32_16x16x64_bf8_bf8" => Some(VSmfmacF3216x16x64Bf8Bf8(arguments))
      case "v_smfmac_i32_16x16x128_i8" => Some(VSmfmacI3216x16x128I8(arguments))
      case "v_swmmac_f32_16x16x128_bf8_fp8" => Some(VSwmmacF3216x16x128Bf8Fp8(arguments))
      case "v_wmma_f32_16x16x128_bf8_fp8" => Some(VWmmaF3216x16x128Bf8Fp8(arguments))
      case _ => None
    }

  private def decode30(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_min_num_f32" => Some(BufferAtomicMinNumF32(arguments))
      case "buffer_atomic_or_b32" => Some(BufferAtomicOrB32(arguments))
      case "buffer_load_format_x" => Some(BufferLoadFormatX(arguments))
      case "ds_max_src2_i32" => Some(DsMaxSrc2I32(arguments))
      case "ds_pk_add_rtn_bf16" => Some(DsPkAddRtnBf16(arguments))
      case "flat_atomic_max_i64" => Some(FlatAtomicMaxI64(arguments))
      case "global_atomic_xor_x2" => Some(GlobalAtomicXorX2(arguments))
      case "global_load_b128" => Some(GlobalLoadB128(arguments))
      case "image_gather4_c_l_o" => Some(ImageGather4CLO_1(arguments))
      case "image_sample_c_cd_cl_o" => Some(ImageSampleCCdClO(arguments))
      case "s_and_not1_wrexec_b64" => Some(SAndNot1WrexecB64(arguments))
      case "s_atomic_umax_x2" => Some(SAtomicUmaxX2(arguments))
      case "s_prefetch_data_pc_rel" => Some(SPrefetchDataPcRel(arguments))
      case "s_setprio" => Some(SSetprio(arguments))
      case "v_and_b16" => Some(VAndB16(arguments))
      case "v_ashr_pk_u8_i32" => Some(VAshrPkU8I32(arguments))
      case "v_cmp_lg_i32" => Some(VCmpLgI32(arguments))
      case "v_cmp_lt_f16" => Some(VCmpLtF16(arguments))
      case "v_cmp_tru_f64" => Some(VCmpTruF64(arguments))
      case "v_cmpx_le_u32" => Some(VCmpxLeU32(arguments))
      case "v_exp_legacy_f32" => Some(VExpLegacyF32(arguments))
      case "v_interp_p2_f32" => Some(VInterpP2F32(arguments))
      case "v_mfma_f32_32x32x2f32" => Some(VMfmaF3232x32x2f32_2(arguments))
      case "v_mfma_f32_4x4x4f16" => Some(VMfmaF324x4x4f16(arguments))
      case "v_mfma_f64_4x4x4_4b_f64" => Some(VMfmaF644x4x44bF64(arguments))
      case "v_mul_hi_u32" => Some(VMulHiU32(arguments))
      case "v_permlanex16_var_b32" => Some(VPermlanex16VarB32(arguments))
      case "v_prng_b32" => Some(VPrngB32(arguments))
      case "v_s_rcp_f16" => Some(VSRcpF16(arguments))
      case "v_wmma_scale16_f32_16x16x128_f8f6f4" => Some(VWmmaScale16F3216x16x128F8f6f4(arguments))
      case _ => None
    }

  private def decode31(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_max_num_f64" => Some(BufferAtomicMaxNumF64(arguments))
      case "ds_gws_barrier" => Some(DsGwsBarrier(arguments))
      case "ds_rsub_src2_u64" => Some(DsRsubSrc2U64(arguments))
      case "ds_store_b32" => Some(DsStoreB32(arguments))
      case "ds_wrxchg_rtn_b64" => Some(DsWrxchgRtnB64(arguments))
      case "global_atomic_and" => Some(GlobalAtomicAnd(arguments))
      case "global_atomic_fmax_x2" => Some(GlobalAtomicFmaxX2(arguments))
      case "global_atomic_smin" => Some(GlobalAtomicSmin(arguments))
      case "global_store_b128" => Some(GlobalStoreB128(arguments))
      case "image_bvh8_intersect_ray" => Some(ImageBvh8IntersectRay(arguments))
      case "image_load_mip_by2" => Some(ImageLoadMipBy2(arguments))
      case "image_load_pck2" => Some(ImageLoadPck2(arguments))
      case "s_abs_i32" => Some(SAbsI32(arguments))
      case "s_and_not1_b64" => Some(SAndNot1B64(arguments))
      case "s_buffer_load_b32" => Some(SBufferLoadB32(arguments))
      case "s_clause" => Some(SClause(arguments))
      case "s_cls_i32_i64" => Some(SClsI32I64(arguments))
      case "s_cmp_eq_f32" => Some(SCmpEqF32(arguments))
      case "s_cmpk_eq_u32" => Some(SCmpkEqU32(arguments))
      case "s_or_not1_b64" => Some(SOrNot1B64(arguments))
      case "scratch_load_d16_hi_u8" => Some(ScratchLoadD16HiU8(arguments))
      case "tbuffer_load_format_xyz" => Some(TbufferLoadFormatXyz(arguments))
      case "v_cmp_ge_u16" => Some(VCmpGeU16(arguments))
      case "v_cmp_lt_f64" => Some(VCmpLtF64(arguments))
      case "v_cvt_pk_fp8_f32" => Some(VCvtPkFp8F32(arguments))
      case "v_cvt_scale_pk16_f16_bf6" => Some(VCvtScalePk16F16Bf6(arguments))
      case "v_cvt_scale_pk16_f32_fp6" => Some(VCvtScalePk16F32Fp6(arguments))
      case "v_cvt_scalef32_pk8_bf8_f32" => Some(VCvtScalef32Pk8Bf8F32(arguments))
      case "v_cvt_scalef32_sr_bf8_bf16" => Some(VCvtScalef32SrBf8Bf16(arguments))
      case "v_cvt_scalef32_sr_pk_fp4_bf16" => Some(VCvtScalef32SrPkFp4Bf16(arguments))
      case "v_max3_num_f16" => Some(VMax3NumF16(arguments))
      case "v_mfma_f32_32x32x1f32" => Some(VMfmaF3232x32x1f32(arguments))
      case "v_mfma_f32_4x4x4_16b_f16" => Some(VMfmaF324x4x416bF16(arguments))
      case "v_mov_b16" => Some(VMovB16(arguments))
      case "v_pk_lshrrev_b16" => Some(VPkLshrrevB16(arguments))
      case "v_pk_mov_b32" => Some(VPkMovB32(arguments))
      case "v_swmmac_f32_16x16x128_fp8_fp8" => Some(VSwmmacF3216x16x128Fp8Fp8(arguments))
      case "v_wmma_f32_16x16x128_fp8_fp8" => Some(VWmmaF3216x16x128Fp8Fp8(arguments))
      case _ => None
    }

  private def decode32(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_cmpswap" => Some(BufferAtomicCmpswap(arguments))
      case "buffer_atomic_fcmpswap" => Some(BufferAtomicFcmpswap(arguments))
      case "flat_atomic_inc" => Some(FlatAtomicInc(arguments))
      case "global_atomic_and_b64" => Some(GlobalAtomicAndB64(arguments))
      case "global_atomic_smax_x2" => Some(GlobalAtomicSmaxX2(arguments))
      case "global_load_tr16_b128" => Some(GlobalLoadTr16B128(arguments))
      case "image_atomic_add_uint" => Some(ImageAtomicAddUint(arguments))
      case "image_sample_c_d_o_g16" => Some(ImageSampleCDOG16_1(arguments))
      case "lds_direct_load" => Some(LdsDirectLoad(arguments))
      case "s_buffer_atomic_add" => Some(SBufferAtomicAdd(arguments))
      case "s_floor_f16" => Some(SFloorF16(arguments))
      case "s_get_barrier_state" => Some(SGetBarrierState(arguments))
      case "s_load_b32" => Some(SLoadB32(arguments))
      case "s_mov_fed_b32" => Some(SMovFedB32(arguments))
      case "s_sub_co_i32" => Some(SSubCoI32(arguments))
      case "scratch_load_dwordx2" => Some(ScratchLoadDwordx2(arguments))
      case "scratch_store_b128" => Some(ScratchStoreB128(arguments))
      case "v_add_f16" => Some(VAddF16(arguments))
      case "v_cmp_ge_u64" => Some(VCmpGeU64(arguments))
      case "v_cvt_pk_f16_f32" => Some(VCvtPkF16F32(arguments))
      case "v_cvt_scalef32_pk32_fp6_bf16" => Some(VCvtScalef32Pk32Fp6Bf16(arguments))
      case "v_dual_dot2acc_f32_f16" => Some(VDualDot2accF32F16(arguments))
      case "v_maxmin_i32" => Some(VMaxminI32(arguments))
      case "v_min_num_f16" => Some(VMinNumF16(arguments))
      case "v_mov_b64" => Some(VMovB64(arguments))
      case "v_movrelsd_2_b32" => Some(VMovrelsd2B32(arguments))
      case "v_pack_b32_f16" => Some(VPackB32F16(arguments))
      case "v_pk_add_f32" => Some(VPkAddF32(arguments))
      case "v_rcp_f32" => Some(VRcpF32(arguments))
      case "v_s_exp_f16" => Some(VSExpF16(arguments))
      case "v_wmma_f32_16x16x64_bf8_fp8" => Some(VWmmaF3216x16x64Bf8Fp8(arguments))
      case _ => None
    }

  private def decode33(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_dec" => Some(BufferAtomicDec(arguments))
      case "buffer_atomic_fcmpswap_x2" => Some(BufferAtomicFcmpswapX2(arguments))
      case "buffer_atomic_min_u64" => Some(BufferAtomicMinU64(arguments))
      case "buffer_load_format_d16_xy" => Some(BufferLoadFormatD16Xy(arguments))
      case "ds_cmpst_f32" => Some(DsCmpstF32(arguments))
      case "ds_cmpst_rtn_b64" => Some(DsCmpstRtnB64(arguments))
      case "ds_gws_sema_v" => Some(DsGwsSemaV(arguments))
      case "ds_storexchg_2addr_stride64_rtn_b64" => Some(DsStorexchg2addrStride64RtnB64(arguments))
      case "global_store_async_from_lds_b128" => Some(GlobalStoreAsyncFromLdsB128(arguments))
      case "image_load_mip_by4" => Some(ImageLoadMipBy4(arguments))
      case "image_load_pck4" => Some(ImageLoadPck4(arguments))
      case "image_sample_b_o" => Some(ImageSampleBO(arguments))
      case "image_sample_c_d_cl" => Some(ImageSampleCDCl_1(arguments))
      case "s_buffer_load_dwordx16" => Some(SBufferLoadDwordx16(arguments))
      case "s_rfe_restore_b64" => Some(SRfeRestoreB64(arguments))
      case "scratch_load_dwordx3" => Some(ScratchLoadDwordx3(arguments))
      case "v_add_f64" => Some(VAddF64(arguments))
      case "v_bfe_i32" => Some(VBfeI32(arguments))
      case "v_cmp_ge_i32" => Some(VCmpGeI32(arguments))
      case "v_cmp_t_i16" => Some(VCmpTI16(arguments))
      case "v_cvt_pknorm_i16_f16" => Some(VCvtPknormI16F16_2(arguments))
      case "v_cvt_pknorm_u16_f16" => Some(VCvtPknormU16F16_2(arguments))
      case "v_cvt_scalef32_pk8_fp8_f32" => Some(VCvtScalef32Pk8Fp8F32(arguments))
      case "v_div_fmas_f64" => Some(VDivFmasF64(arguments))
      case "v_fmac_dx9_zero_f32" => Some(VFmacDx9ZeroF32(arguments))
      case "v_fract_f16" => Some(VFractF16(arguments))
      case "v_min_num_f64" => Some(VMinNumF64(arguments))
      case "v_minimum_f16" => Some(VMinimumF16(arguments))
      case "v_pk_max_f16" => Some(VPkMaxF16(arguments))
      case "v_s_rsq_f32" => Some(VSRsqF32(arguments))
      case "v_subrev_u32" => Some(VSubrevU32(arguments))
      case _ => None
    }

  private def decode34(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_cmpswap_f32" => Some(BufferAtomicCmpswapF32(arguments))
      case "buffer_atomic_min_i32" => Some(BufferAtomicMinI32(arguments))
      case "ds_cmpstore_rtn_f32" => Some(DsCmpstoreRtnF32(arguments))
      case "global_atomic_add_u32" => Some(GlobalAtomicAddU32(arguments))
      case "global_atomic_fmax" => Some(GlobalAtomicFmax(arguments))
      case "global_load_ubyte_d16_hi" => Some(GlobalLoadUbyteD16Hi(arguments))
      case "image_sample_c_cd_cl" => Some(ImageSampleCCdCl(arguments))
      case "s_andn2_wrexec_b32" => Some(SAndn2WrexecB32(arguments))
      case "s_bitset1_b32" => Some(SBitset1B32(arguments))
      case "s_cmp_nlg_f32" => Some(SCmpNlgF32(arguments))
      case "s_cmp_o_f16" => Some(SCmpOF16(arguments))
      case "s_getpc_b64" => Some(SGetpcB64(arguments))
      case "scratch_load_dwordx4" => Some(ScratchLoadDwordx4(arguments))
      case "tensor_store_from_lds" => Some(TensorStoreFromLds(arguments))
      case "v_accvgpr_mov_b32" => Some(VAccvgprMovB32(arguments))
      case "v_cmp_le_i16" => Some(VCmpLeI16(arguments))
      case "v_cmp_t_i64" => Some(VCmpTI64(arguments))
      case "v_cmpx_tru_i16" => Some(VCmpxTruI16(arguments))
      case "v_cvt_scalef32_pk16_fp6_bf16" => Some(VCvtScalef32Pk16Fp6Bf16(arguments))
      case "v_dual_min_f32" => Some(VDualMinF32(arguments))
      case "v_fract_f64" => Some(VFractF64(arguments))
      case "v_mfma_f32_16x16x32_f16" => Some(VMfmaF3216x16x32F16(arguments))
      case "v_mfma_f32_32x32x4_2b_f16" => Some(VMfmaF3232x32x42bF16(arguments))
      case "v_mfma_f32_32x32x4bf16" => Some(VMfmaF3232x32x4bf16(arguments))
      case "v_minimum_f64" => Some(VMinimumF64(arguments))
      case "v_or_b32" => Some(VOrB32(arguments))
      case "v_pk_add_bf16" => Some(VPkAddBf16(arguments))
      case "v_rndne_f16" => Some(VRndneF16(arguments))
      case "v_sub_nc_u32" => Some(VSubNcU32(arguments))
      case "v_sub_u16" => Some(VSubU16(arguments))
      case "v_wmma_f32_16x16x64_fp8_fp8" => Some(VWmmaF3216x16x64Fp8Fp8(arguments))
      case _ => None
    }

  private def decode35(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_rsub" => Some(BufferAtomicRsub(arguments))
      case "buffer_atomic_smax_x2" => Some(BufferAtomicSmaxX2(arguments))
      case "buffer_load_sbyte_d16" => Some(BufferLoadSbyteD16(arguments))
      case "ds_dec_src2_u64" => Some(DsDecSrc2U64(arguments))
      case "ds_inc_u32" => Some(DsIncU32(arguments))
      case "ds_max_src2_f32" => Some(DsMaxSrc2F32(arguments))
      case "ds_nop" => Some(DsNop(arguments))
      case "ds_read_u16_d16" => Some(DsReadU16D16(arguments))
      case "ds_write_b16" => Some(DsWriteB16(arguments))
      case "flat_atomic_max_f64" => Some(FlatAtomicMaxF64(arguments))
      case "flat_atomic_sub_u32" => Some(FlatAtomicSubU32(arguments))
      case "global_atomic_fcmpswap" => Some(GlobalAtomicFcmpswap(arguments))
      case "image_sample_c_b_cl" => Some(ImageSampleCBCl(arguments))
      case "s_atomic_swap" => Some(SAtomicSwap(arguments))
      case "s_barrier" => Some(SBarrier(arguments))
      case "s_buffer_load_b128" => Some(SBufferLoadB128(arguments))
      case "s_cmp_le_u32" => Some(SCmpLeU32(arguments))
      case "s_icache_inv" => Some(SIcacheInv(arguments))
      case "scratch_load_lds_ushort" => Some(ScratchLoadLdsUshort(arguments))
      case "v_add_nc_i16" => Some(VAddNcI16(arguments))
      case "v_cmp_le_i64" => Some(VCmpLeI64(arguments))
      case "v_cmp_lg_f32" => Some(VCmpLgF32(arguments))
      case "v_cmpx_tru_i64" => Some(VCmpxTruI64(arguments))
      case "v_cvt_i32_f32" => Some(VCvtI32F32(arguments))
      case "v_cvt_u32_f32" => Some(VCvtU32F32(arguments))
      case "v_dual_max_i32" => Some(VDualMaxI32(arguments))
      case "v_mfma_f32_16x16x16f16" => Some(VMfmaF3216x16x16f16_2(arguments))
      case "v_mfma_f32_16x16x4_f32" => Some(VMfmaF3216x16x4F32_1(arguments))
      case "v_pk_min3_u16" => Some(VPkMin3U16(arguments))
      case "v_rndne_f64" => Some(VRndneF64(arguments))
      case "v_swmmac_i32_16x16x32_iu4" => Some(VSwmmacI3216x16x32Iu4(arguments))
      case "v_trig_preop_f64" => Some(VTrigPreopF64(arguments))
      case _ => None
    }

  private def decode36(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "cluster_load_async_to_lds_b32" => Some(ClusterLoadAsyncToLdsB32(arguments))
      case "ds_inc_rtn_u32" => Some(DsIncRtnU32(arguments))
      case "ds_write_b64" => Some(DsWriteB64(arguments))
      case "export" => Some(Export(arguments))
      case "flat_load_sshort" => Some(FlatLoadSshort(arguments))
      case "global_atomic_sub_u64" => Some(GlobalAtomicSubU64(arguments))
      case "global_load_ushort" => Some(GlobalLoadUshort(arguments))
      case "image_atomic_fmin" => Some(ImageAtomicFmin(arguments))
      case "image_gather4_b_o" => Some(ImageGather4BO(arguments))
      case "image_get_lod" => Some(ImageGetLod(arguments))
      case "s_andn2_saveexec_b32" => Some(SAndn2SaveexecB32(arguments))
      case "s_ashr_i64" => Some(SAshrI64(arguments))
      case "s_or_b64" => Some(SOrB64(arguments))
      case "v_ceil_f32" => Some(VCeilF32(arguments))
      case "v_cmp_neq_f16" => Some(VCmpNeqF16(arguments))
      case "v_cvt_f32_ubyte0" => Some(VCvtF32Ubyte0(arguments))
      case "v_dual_dot2acc_f32_bf16" => Some(VDualDot2accF32Bf16(arguments))
      case "v_dual_min_num_f64" => Some(VDualMinNumF64(arguments))
      case "v_fmac_f32" => Some(VFmacF32(arguments))
      case "v_fmac_legacy_f32" => Some(VFmacLegacyF32(arguments))
      case "v_mad_u32_u24" => Some(VMadU32U24(arguments))
      case "v_max_f64_e64" => Some(VMaxF64E64(arguments))
      case "v_maxmin_num_f32" => Some(VMaxminNumF32(arguments))
      case "v_mfma_f32_16x16x2bf16" => Some(VMfmaF3216x16x2bf16(arguments))
      case "v_mfma_f64_4x4x4f64" => Some(VMfmaF644x4x4f64(arguments))
      case "v_minmax_num_f32" => Some(VMinmaxNumF32(arguments))
      case "v_sub_i32" => Some(VSubI32(arguments))
      case "v_swap_b16" => Some(VSwapB16(arguments))
      case "v_tanh_f16" => Some(VTanhF16(arguments))
      case _ => None
    }

  private def decode37(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_add_u32" => Some(BufferAtomicAddU32(arguments))
      case "buffer_atomic_umax_x2" => Some(BufferAtomicUmaxX2(arguments))
      case "cluster_load_b32" => Some(ClusterLoadB32(arguments))
      case "ds_condxchg32_rtn_b64" => Some(DsCondxchg32RtnB64(arguments))
      case "ds_load_2addr_stride64_b64" => Some(DsLoad2addrStride64B64(arguments))
      case "ds_max_rtn_u64" => Some(DsMaxRtnU64(arguments))
      case "ds_read_i16" => Some(DsReadI16(arguments))
      case "ds_store_b16_d16_hi" => Some(DsStoreB16D16Hi(arguments))
      case "flat_atomic_or" => Some(FlatAtomicOr(arguments))
      case "global_load_short_d16" => Some(GlobalLoadShortD16(arguments))
      case "image_get_resinfo" => Some(ImageGetResinfo(arguments))
      case "s_and_not0_saveexec_b64" => Some(SAndNot0SaveexecB64(arguments))
      case "s_bitcmp0_b32" => Some(SBitcmp0B32(arguments))
      case "s_cmp_nge_f32" => Some(SCmpNgeF32(arguments))
      case "s_set_inst_prefetch_distance" => Some(SSetInstPrefetchDistance(arguments))
      case "s_ttracedata_imm" => Some(STtracedataImm(arguments))
      case "v_cmp_neq_f64" => Some(VCmpNeqF64(arguments))
      case "v_cvt_f32_ubyte1" => Some(VCvtF32Ubyte1(arguments))
      case "v_cvt_pk_rtz_f16_f32" => Some(VCvtPkRtzF16F32_1(arguments))
      case "v_cvt_scale_pk16_bf16_fp6" => Some(VCvtScalePk16Bf16Fp6(arguments))
      case "v_dot8_i32_i4" => Some(VDot8I32I4(arguments))
      case "v_dual_fma_f64" => Some(VDualFmaF64(arguments))
      case "v_fma_legacy_f32" => Some(VFmaLegacyF32(arguments))
      case "v_log_clamp_f32" => Some(VLogClampF32(arguments))
      case "v_maxmin_f32" => Some(VMaxminF32(arguments))
      case "v_s_sqrt_f16" => Some(VSSqrtF16(arguments))
      case "v_smfmac_f32_32x32x64_bf8_bf8" => Some(VSmfmacF3232x32x64Bf8Bf8(arguments))
      case _ => None
    }

  private def decode38(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_swap_b32" => Some(BufferAtomicSwapB32(arguments))
      case "ds_load_tr8_b64" => Some(DsLoadTr8B64(arguments))
      case "ds_max_rtn_i32" => Some(DsMaxRtnI32(arguments))
      case "ds_read_b128" => Some(DsReadB128(arguments))
      case "flat_store_byte_d16_hi" => Some(FlatStoreByteD16Hi(arguments))
      case "s_atomic_inc" => Some(SAtomicInc(arguments))
      case "s_buffer_atomic_and" => Some(SBufferAtomicAnd(arguments))
      case "s_cmp_nle_f16" => Some(SCmpNleF16(arguments))
      case "s_nor_saveexec_b32" => Some(SNorSaveexecB32(arguments))
      case "tbuffer_load_d16_format_xyzw" => Some(TbufferLoadD16FormatXyzw(arguments))
      case "tbuffer_load_format_d16_xyzw" => Some(TbufferLoadFormatD16Xyzw(arguments))
      case "tbuffer_store_format_xyz" => Some(TbufferStoreFormatXyz(arguments))
      case "v_accvgpr_read" => Some(VAccvgprRead(arguments))
      case "v_cmp_ge_f32" => Some(VCmpGeF32(arguments))
      case "v_cmp_nlt_f32" => Some(VCmpNltF32(arguments))
      case "v_cmp_t_f16" => Some(VCmpTF16(arguments))
      case "v_cmpx_gt_u32" => Some(VCmpxGtU32(arguments))
      case "v_cvt_f32_ubyte2" => Some(VCvtF32Ubyte2(arguments))
      case "v_dot2_i32_i16" => Some(VDot2I32I16(arguments))
      case "v_smfmac_i32_16x16x64_i8" => Some(VSmfmacI3216x16x64I8(arguments))
      case _ => None
    }

  private def decode39(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_max_u64" => Some(BufferAtomicMaxU64(arguments))
      case "buffer_atomic_min_f32" => Some(BufferAtomicMinF32(arguments))
      case "buffer_atomic_sub_u64" => Some(BufferAtomicSubU64(arguments))
      case "buffer_load_b128" => Some(BufferLoadB128(arguments))
      case "ds_add_rtn_f64" => Some(DsAddRtnF64(arguments))
      case "ds_mskor_rtn_b32" => Some(DsMskorRtnB32(arguments))
      case "ds_wrxchg2_rtn_b64" => Some(DsWrxchg2RtnB64(arguments))
      case "flat_store_b96" => Some(FlatStoreB96(arguments))
      case "image_atomic_swap" => Some(ImageAtomicSwap(arguments))
      case "image_bvh_intersect_ray" => Some(ImageBvhIntersectRay(arguments))
      case "image_sample_cd_g16" => Some(ImageSampleCdG16_2(arguments))
      case "image_store_mip_pck" => Some(ImageStoreMipPck(arguments))
      case "s_cbranch_execz" => Some(SCbranchExecz(arguments))
      case "s_ff0_i32_b32" => Some(SFf0I32B32(arguments))
      case "s_fmac_f32" => Some(SFmacF32(arguments))
      case "s_nop" => Some(SNop(arguments))
      case "s_wakeup" => Some(SWakeup(arguments))
      case "scratch_load_d16_hi_i8" => Some(ScratchLoadD16HiI8(arguments))
      case "scratch_load_lds_sbyte" => Some(ScratchLoadLdsSbyte(arguments))
      case "v_ashrrev_i32" => Some(VAshrrevI32(arguments))
      case "v_bcnt_u32_b32" => Some(VBcntU32B32(arguments))
      case "v_cmp_le_f16" => Some(VCmpLeF16(arguments))
      case "v_cmp_t_f64" => Some(VCmpTF64(arguments))
      case "v_cmpx_eq_i16" => Some(VCmpxEqI16(arguments))
      case "v_cmpx_lt_u16" => Some(VCmpxLtU16(arguments))
      case "v_cmpx_tru_f16" => Some(VCmpxTruF16(arguments))
      case "v_cvt_f32_ubyte3" => Some(VCvtF32Ubyte3(arguments))
      case "v_cvt_scalef32_sr_pk8_fp4_f32" => Some(VCvtScalef32SrPk8Fp4F32(arguments))
      case "v_cvt_scalef32_sr_pk8_fp8_bf16" => Some(VCvtScalef32SrPk8Fp8Bf16(arguments))
      case "v_floor_f16" => Some(VFloorF16(arguments))
      case "v_mfma_f32_32x32x8_bf16" => Some(VMfmaF3232x32x8Bf16(arguments))
      case "v_mfma_i32_16x16x32_i8" => Some(VMfmaI3216x16x32I8(arguments))
      case "v_smfmac_f32_32x32x64_fp8_bf8" => Some(VSmfmacF3232x32x64Fp8Bf8(arguments))
      case "v_swmmac_i32_16x16x32_iu8" => Some(VSwmmacI3216x16x32Iu8(arguments))
      case "v_wmma_scale_f32_16x16x128_f8f6f4" => Some(VWmmaScaleF3216x16x128F8f6f4(arguments))
      case _ => None
    }

  private def decode40(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_max_i32" => Some(BufferAtomicMaxI32(arguments))
      case "buffer_invl2" => Some(BufferInvl2(arguments))
      case "buffer_load_dword" => Some(BufferLoadDword(arguments))
      case "buffer_load_short_d16" => Some(BufferLoadShortD16(arguments))
      case "buffer_wbinvl1_vol" => Some(BufferWbinvl1Vol(arguments))
      case "flat_atomic_dec" => Some(FlatAtomicDec(arguments))
      case "flat_atomic_smin_x2" => Some(FlatAtomicSminX2(arguments))
      case "flat_load_ubyte_d16" => Some(FlatLoadUbyteD16(arguments))
      case "global_load_b32" => Some(GlobalLoadB32(arguments))
      case "s_and_b64" => Some(SAndB64(arguments))
      case "s_buffer_atomic_dec_x2" => Some(SBufferAtomicDecX2(arguments))
      case "s_dcache_inv" => Some(SDcacheInv(arguments))
      case "s_get_pc_i64" => Some(SGetPcI64(arguments))
      case "s_wait_loadcnt_dscnt" => Some(SWaitLoadcntDscnt(arguments))
      case "v_cmp_le_f64" => Some(VCmpLeF64(arguments))
      case "v_cmpx_class_f16" => Some(VCmpxClassF16(arguments))
      case "v_cmpx_eq_i64" => Some(VCmpxEqI64(arguments))
      case "v_cmpx_lt_u64" => Some(VCmpxLtU64(arguments))
      case "v_cmpx_tru_f64" => Some(VCmpxTruF64(arguments))
      case "v_cvt_pk_norm_i16_f16" => Some(VCvtPkNormI16F16_1(arguments))
      case "v_cvt_pk_norm_u16_f16" => Some(VCvtPkNormU16F16_1(arguments))
      case "v_cvt_scalef32_2xpk16_bf6_f32" => Some(VCvtScalef322xpk16Bf6F32(arguments))
      case "v_dot2_f32_f16" => Some(VDot2F32F16(arguments))
      case "v_dual_max_f32" => Some(VDualMaxF32(arguments))
      case "v_dual_mul_f32" => Some(VDualMulF32(arguments))
      case "v_floor_f64" => Some(VFloorF64(arguments))
      case "v_frexp_exp_i32_f64" => Some(VFrexpExpI32F64(arguments))
      case "v_mac_f32" => Some(VMacF32(arguments))
      case "v_mac_legacy_f32" => Some(VMacLegacyF32(arguments))
      case "v_mad_legacy_i16" => Some(VMadLegacyI16(arguments))
      case "v_maximum3_f16" => Some(VMaximum3F16(arguments))
      case "v_minimum3_f32" => Some(VMinimum3F32(arguments))
      case "v_pk_add_min_i16" => Some(VPkAddMinI16(arguments))
      case "v_pk_mad_i16" => Some(VPkMadI16(arguments))
      case "v_sad_u16" => Some(VSadU16(arguments))
      case "v_smfmac_f32_16x16x32_f16" => Some(VSmfmacF3216x16x32F16(arguments))
      case "v_wmma_f32_16x16x4_f32" => Some(VWmmaF3216x16x4F32(arguments))
      case _ => None
    }

  private def decode41(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_dec_u64" => Some(BufferAtomicDecU64(arguments))
      case "buffer_atomic_or_x2" => Some(BufferAtomicOrX2(arguments))
      case "buffer_load_d16_hi_b16" => Some(BufferLoadD16HiB16(arguments))
      case "ds_add_src2_u32" => Some(DsAddSrc2U32(arguments))
      case "ds_ordered_count" => Some(DsOrderedCount(arguments))
      case "exp" => Some(Exp(arguments))
      case "global_atomic_swap_b32" => Some(GlobalAtomicSwapB32(arguments))
      case "global_inv" => Some(GlobalInv(arguments))
      case "image_sample_c_b_o" => Some(ImageSampleCBO(arguments))
      case "s_add_f16" => Some(SAddF16(arguments))
      case "s_bfe_u64" => Some(SBfeU64(arguments))
      case "s_buffer_atomic_smin_x2" => Some(SBufferAtomicSminX2(arguments))
      case "s_denorm_mode" => Some(SDenormMode(arguments))
      case "s_incperflevel" => Some(SIncperflevel(arguments))
      case "s_mov_b64" => Some(SMovB64(arguments))
      case "s_not_b32" => Some(SNotB32(arguments))
      case "s_rndne_f32" => Some(SRndneF32(arguments))
      case "s_trap" => Some(STrap(arguments))
      case "scratch_store_short_d16_hi" => Some(ScratchStoreShortD16Hi(arguments))
      case "v_cmp_o_f16" => Some(VCmpOF16(arguments))
      case "v_cmpx_class_f64" => Some(VCmpxClassF64(arguments))
      case "v_cmpx_lt_i32" => Some(VCmpxLtI32(arguments))
      case "v_cvt_scalef32_f32_bf8" => Some(VCvtScalef32F32Bf8(arguments))
      case "v_dot2acc_f32_f16" => Some(VDot2accF32F16(arguments))
      case "v_dot2c_f32_bf16" => Some(VDot2cF32Bf16(arguments))
      case "v_dot4_u32_u8" => Some(VDot4U32U8(arguments))
      case "v_sqrt_f16" => Some(VSqrtF16(arguments))
      case "v_sub_f32" => Some(VSubF32(arguments))
      case _ => None
    }

  private def decode42(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_gl0_inv" => Some(BufferGl0Inv(arguments))
      case "ds_add_u32" => Some(DsAddU32(arguments))
      case "ds_bvh_stack_push4_pop1_rtn_b32" => Some(DsBvhStackPush4Pop1RtnB32(arguments))
      case "ds_max_num_rtn_f32" => Some(DsMaxNumRtnF32(arguments))
      case "ds_or_rtn_b32" => Some(DsOrRtnB32(arguments))
      case "ds_store_2addr_stride64_b64" => Some(DsStore2addrStride64B64(arguments))
      case "ds_storexchg_rtn_b32" => Some(DsStorexchgRtnB32(arguments))
      case "ds_sub_rtn_u32" => Some(DsSubRtnU32(arguments))
      case "flat_atomic_or_b64" => Some(FlatAtomicOrB64(arguments))
      case "flat_atomic_umin_x2" => Some(FlatAtomicUminX2(arguments))
      case "global_atomic_fcmpswap_x2" => Some(GlobalAtomicFcmpswapX2(arguments))
      case "global_atomic_inc" => Some(GlobalAtomicInc(arguments))
      case "global_atomic_or_x2" => Some(GlobalAtomicOrX2(arguments))
      case "image_sample_c_d_cl_o_g16" => Some(ImageSampleCDClOG16_1(arguments))
      case "s_bfe_i32" => Some(SBfeI32(arguments))
      case "s_buffer_load_b512" => Some(SBufferLoadB512(arguments))
      case "s_cbranch_vccz" => Some(SCbranchVccz(arguments))
      case "s_delay_alu" => Some(SDelayAlu(arguments))
      case "s_setpc_b64" => Some(SSetpcB64(arguments))
      case "s_subvector_loop_begin" => Some(SSubvectorLoopBegin(arguments))
      case "tbuffer_load_format_x" => Some(TbufferLoadFormatX(arguments))
      case "v_cmp_gt_i16" => Some(VCmpGtI16(arguments))
      case "v_cmp_o_f64" => Some(VCmpOF64(arguments))
      case "v_cvt_pk_u16_u32" => Some(VCvtPkU16U32(arguments))
      case "v_cvt_scalef32_2xpk16_fp6_f32" => Some(VCvtScalef322xpk16Fp6F32(arguments))
      case "v_cvt_sr_pk_f16_f32" => Some(VCvtSrPkF16F32(arguments))
      case "v_dual_ashrrev_i32" => Some(VDualAshrrevI32(arguments))
      case "v_mfma_f32_16x16x32_bf8_fp8" => Some(VMfmaF3216x16x32Bf8Fp8(arguments))
      case "v_mfma_f32_32x32x8f16" => Some(VMfmaF3232x32x8f16_2(arguments))
      case "v_min_u32" => Some(VMinU32(arguments))
      case "v_mqsad_pk_u16_u8" => Some(VMqsadPkU16U8(arguments))
      case "v_mul_hi_i32_i24" => Some(VMulHiI32I24(arguments))
      case "v_sqrt_f64" => Some(VSqrtF64(arguments))
      case _ => None
    }

  private def decode43(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_d16_u8" => Some(BufferLoadD16U8(arguments))
      case "buffer_load_format_xyz" => Some(BufferLoadFormatXyz(arguments))
      case "ds_and_src2_b64" => Some(DsAndSrc2B64(arguments))
      case "ds_load_tr6_b96" => Some(DsLoadTr6B96(arguments))
      case "ds_max_rtn_f32" => Some(DsMaxRtnF32(arguments))
      case "ds_min_rtn_i64" => Some(DsMinRtnI64(arguments))
      case "ds_read_b64_tr_b4" => Some(DsReadB64TrB4(arguments))
      case "flat_load_u16" => Some(FlatLoadU16(arguments))
      case "global_atomic_cmpswap_b32" => Some(GlobalAtomicCmpswapB32(arguments))
      case "global_atomic_dec_x2" => Some(GlobalAtomicDecX2(arguments))
      case "global_atomic_min_u32" => Some(GlobalAtomicMinU32(arguments))
      case "s_and_not0_wrexec_b64" => Some(SAndNot0WrexecB64(arguments))
      case "s_barrier_leave" => Some(SBarrierLeave(arguments))
      case "s_buffer_atomic_umin_x2" => Some(SBufferAtomicUminX2(arguments))
      case "s_cmp_gt_u32" => Some(SCmpGtU32(arguments))
      case "s_ctz_i32_b64" => Some(SCtzI32B64(arguments))
      case "s_sub_co_ci_u32" => Some(SSubCoCiU32(arguments))
      case "v_cmp_eq_u32" => Some(VCmpEqU32(arguments))
      case "v_cmp_gt_i64" => Some(VCmpGtI64(arguments))
      case "v_cmpx_u_f32" => Some(VCmpxUF32(arguments))
      case "v_cvt_f16_u16" => Some(VCvtF16U16(arguments))
      case "v_cvt_rpi_i32_f32" => Some(VCvtRpiI32F32(arguments))
      case "v_lshlrev_b16" => Some(VLshlrevB16(arguments))
      case "v_madak_f32" => Some(VMadakF32(arguments))
      case "v_mfma_f32_32x32x4_xf32" => Some(VMfmaF3232x32x4Xf32(arguments))
      case "v_minmax_f16" => Some(VMinmaxF16(arguments))
      case "v_pk_fma_f16" => Some(VPkFmaF16(arguments))
      case "v_swmmac_f16_16x16x32_f16" => Some(VSwmmacF1616x16x32F16(arguments))
      case _ => None
    }

  private def decode44(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_u16" => Some(BufferLoadU16(arguments))
      case "ds_dec_rtn_u32" => Some(DsDecRtnU32(arguments))
      case "ds_param_load" => Some(DsParamLoad(arguments))
      case "ds_permute_b32" => Some(DsPermuteB32(arguments))
      case "ds_sub_u64" => Some(DsSubU64(arguments))
      case "flat_atomic_cmpswap_b64" => Some(FlatAtomicCmpswapB64(arguments))
      case "flat_load_b96" => Some(FlatLoadB96(arguments))
      case "flat_load_ubyte_d16_hi" => Some(FlatLoadUbyteD16Hi(arguments))
      case "global_atomic_cmpswap_x2" => Some(GlobalAtomicCmpswapX2(arguments))
      case "global_atomic_fmin" => Some(GlobalAtomicFmin(arguments))
      case "global_load_d16_hi_b16" => Some(GlobalLoadD16HiB16(arguments))
      case "image_gather4_lz_o" => Some(ImageGather4LzO(arguments))
      case "s_andn2_b64" => Some(SAndn2B64(arguments))
      case "s_cvt_f32_i32" => Some(SCvtF32I32(arguments))
      case "s_or_saveexec_b32" => Some(SOrSaveexecB32(arguments))
      case "s_quadmask_b64" => Some(SQuadmaskB64(arguments))
      case "s_setreg_b32" => Some(SSetregB32(arguments))
      case "s_xor_b64" => Some(SXorB64(arguments))
      case "scratch_load_b64" => Some(ScratchLoadB64(arguments))
      case "scratch_store_b16" => Some(ScratchStoreB16(arguments))
      case "tbuffer_store_format_d16_xyz" => Some(TbufferStoreFormatD16Xyz(arguments))
      case "v_cmpx_eq_f16" => Some(VCmpxEqF16(arguments))
      case "v_cmpx_lg_u32" => Some(VCmpxLgU32(arguments))
      case "v_cvt_off_f32_i4" => Some(VCvtOffF32I4(arguments))
      case "v_cvt_scale_pk8_f32_bf8" => Some(VCvtScalePk8F32Bf8(arguments))
      case "v_dual_add_f64" => Some(VDualAddF64(arguments))
      case "v_lshl_or_b32" => Some(VLshlOrB32(arguments))
      case "v_lshlrev_b64" => Some(VLshlrevB64(arguments))
      case "v_mfma_f32_16x16x32_fp8_fp8" => Some(VMfmaF3216x16x32Fp8Fp8(arguments))
      case "v_mfma_f32_32x32x16_bf8_fp8" => Some(VMfmaF3232x32x16Bf8Fp8(arguments))
      case "v_mul_dx9_zero_f32" => Some(VMulDx9ZeroF32(arguments))
      case "v_mul_u32_u24" => Some(VMulU32U24(arguments))
      case "v_pk_max_num_f16" => Some(VPkMaxNumF16(arguments))
      case _ => None
    }

  private def decode45(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_max_f32" => Some(BufferAtomicMaxF32(arguments))
      case "buffer_load_b96" => Some(BufferLoadB96(arguments))
      case "ds_atomic_barrier_arrive_rtn_b64" => Some(DsAtomicBarrierArriveRtnB64(arguments))
      case "ds_load_i16" => Some(DsLoadI16(arguments))
      case "flat_atomic_inc_u32" => Some(FlatAtomicIncU32(arguments))
      case "flat_load_monitor_b32" => Some(FlatLoadMonitorB32(arguments))
      case "global_load_async_to_lds_b32" => Some(GlobalLoadAsyncToLdsB32(arguments))
      case "global_store_dword_addtid" => Some(GlobalStoreDwordAddtid(arguments))
      case "image_sample_d_cl_o" => Some(ImageSampleDClO(arguments))
      case "s_buffer_atomic_or_x2" => Some(SBufferAtomicOrX2(arguments))
      case "s_or_not0_saveexec_b64" => Some(SOrNot0SaveexecB64(arguments))
      case "scratch_store_b64" => Some(ScratchStoreB64(arguments))
      case "v_add_f64_e64" => Some(VAddF64E64(arguments))
      case "v_cmpx_eq_f64" => Some(VCmpxEqF64(arguments))
      case "v_cmpx_f_u16" => Some(VCmpxFU16(arguments))
      case "v_mad_legacy_f16" => Some(VMadLegacyF16(arguments))
      case "v_min_legacy_f32" => Some(VMinLegacyF32(arguments))
      case "v_pk_add_i16" => Some(VPkAddI16(arguments))
      case "v_pk_maximum_f16" => Some(VPkMaximumF16(arguments))
      case "v_s_log_f32" => Some(VSLogF32(arguments))
      case _ => None
    }

  private def decode46(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_xor" => Some(BufferAtomicXor(arguments))
      case "ds_read_i8_d16_hi" => Some(DsReadI8D16Hi(arguments))
      case "ds_rsub_u64" => Some(DsRsubU64(arguments))
      case "flat_atomic_cond_sub_u32" => Some(FlatAtomicCondSubU32(arguments))
      case "flat_store_dword" => Some(FlatStoreDword(arguments))
      case "global_atomic_inc_u64" => Some(GlobalAtomicIncU64(arguments))
      case "global_load_d16_hi_u8" => Some(GlobalLoadD16HiU8(arguments))
      case "global_load_tr4_b64" => Some(GlobalLoadTr4B64(arguments))
      case "s_atomic_dec" => Some(SAtomicDec(arguments))
      case "s_atomic_or_x2" => Some(SAtomicOrX2(arguments))
      case "s_branch" => Some(SBranch(arguments))
      case "s_cmp_lt_i32" => Some(SCmpLtI32(arguments))
      case "s_cmp_ngt_f16" => Some(SCmpNgtF16(arguments))
      case "s_maximum_f32" => Some(SMaximumF32(arguments))
      case "s_nor_b64" => Some(SNorB64(arguments))
      case "s_set_gpr_idx_off" => Some(SSetGprIdxOff(arguments))
      case "s_xnor_saveexec_b32" => Some(SXnorSaveexecB32(arguments))
      case "v_and_or_b32" => Some(VAndOrB32(arguments))
      case "v_cmpx_f_u64" => Some(VCmpxFU64(arguments))
      case "v_cmpx_lt_f32" => Some(VCmpxLtF32(arguments))
      case "v_cmpx_ne_i16" => Some(VCmpxNeI16(arguments))
      case "v_cmpx_neq_f16" => Some(VCmpxNeqF16(arguments))
      case "v_dot4_f32_bf8_fp8" => Some(VDot4F32Bf8Fp8(arguments))
      case "v_dot8c_i32_i4" => Some(VDot8cI32I4(arguments))
      case "v_mfma_f32_32x32x16_fp8_fp8" => Some(VMfmaF3232x32x16Fp8Fp8(arguments))
      case "v_mfma_f32_32x32x4f16" => Some(VMfmaF3232x32x4f16(arguments))
      case "v_mfma_f32_32x32x64_f8f6f4" => Some(VMfmaF3232x32x64F8f6f4(arguments))
      case "v_mul_lo_u16" => Some(VMulLoU16(arguments))
      case "v_mullit_f32" => Some(VMullitF32(arguments))
      case "v_pk_add_max_i16" => Some(VPkAddMaxI16(arguments))
      case _ => None
    }

  private def decode47(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_csub_u32" => Some(BufferAtomicCsubU32(arguments))
      case "buffer_atomic_sub" => Some(BufferAtomicSub(arguments))
      case "buffer_store_d16_format_x" => Some(BufferStoreD16FormatX(arguments))
      case "buffer_store_short" => Some(BufferStoreShort(arguments))
      case "ds_min_src2_u64" => Some(DsMinSrc2U64(arguments))
      case "ds_read_b64_tr_b8" => Some(DsReadB64TrB8(arguments))
      case "ds_wrxchg2st64_rtn_b32" => Some(DsWrxchg2st64RtnB32(arguments))
      case "ds_xor_src2_b64" => Some(DsXorSrc2B64(arguments))
      case "flat_atomic_max_num_f32" => Some(FlatAtomicMaxNumF32(arguments))
      case "flat_load_dword" => Some(FlatLoadDword(arguments))
      case "global_atomic_swap" => Some(GlobalAtomicSwap(arguments))
      case "global_store_b32" => Some(GlobalStoreB32(arguments))
      case "s_andn1_wrexec_b32" => Some(SAndn1WrexecB32(arguments))
      case "s_cbranch_scc0" => Some(SCbranchScc0(arguments))
      case "s_lshl3_add_u32" => Some(SLshl3AddU32(arguments))
      case "s_sendmsg_rtn_b64" => Some(SSendmsgRtnB64(arguments))
      case "tbuffer_store_format_d16_x" => Some(TbufferStoreFormatD16X(arguments))
      case "v_cmp_gt_f16" => Some(VCmpGtF16(arguments))
      case "v_cmp_nle_f32" => Some(VCmpNleF32(arguments))
      case "v_cmpx_f_i32" => Some(VCmpxFI32(arguments))
      case "v_cmpx_ge_u32" => Some(VCmpxGeU32(arguments))
      case "v_cmpx_ne_i64" => Some(VCmpxNeI64(arguments))
      case "v_cmpx_neq_f64" => Some(VCmpxNeqF64(arguments))
      case "v_cvt_f16_fp8" => Some(VCvtF16Fp8(arguments))
      case "v_cvt_pk_bf8_f16" => Some(VCvtPkBf8F16(arguments))
      case "v_dual_sub_nc_u32" => Some(VDualSubNcU32(arguments))
      case "v_lshl_add_u32" => Some(VLshlAddU32(arguments))
      case "v_mad_mixlo_f16" => Some(VMadMixloF16(arguments))
      case "v_pk_min3_num_f16" => Some(VPkMin3NumF16(arguments))
      case "v_pk_mul_f32" => Some(VPkMulF32(arguments))
      case "v_sin_f16" => Some(VSinF16(arguments))
      case "v_wmma_i32_16x16x32_iu4" => Some(VWmmaI3216x16x32Iu4(arguments))
      case _ => None
    }

  private def decode48(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_d16_format_x" => Some(BufferLoadD16FormatX(arguments))
      case "ds_cmpstore_b64" => Some(DsCmpstoreB64(arguments))
      case "ds_inc_src2_u32" => Some(DsIncSrc2U32(arguments))
      case "ds_min_rtn_f64" => Some(DsMinRtnF64(arguments))
      case "ds_min_src2_i32" => Some(DsMinSrc2I32(arguments))
      case "ds_store_b8" => Some(DsStoreB8(arguments))
      case "flat_prefetch_b8" => Some(FlatPrefetchB8(arguments))
      case "image_gather4_c_lz_o" => Some(ImageGather4CLzO(arguments))
      case "image_gather8h_pck" => Some(ImageGather8hPck(arguments))
      case "lds_param_load" => Some(LdsParamLoad(arguments))
      case "s_and_saveexec_b32" => Some(SAndSaveexecB32(arguments))
      case "s_barrier_signal" => Some(SBarrierSignal(arguments))
      case "s_bcnt0_i32_b32" => Some(SBcnt0I32B32(arguments))
      case "s_buffer_atomic_inc_x2" => Some(SBufferAtomicIncX2(arguments))
      case "s_cbranch_scc1" => Some(SCbranchScc1(arguments))
      case "s_lshl4_add_u32" => Some(SLshl4AddU32(arguments))
      case "v_add_min_u32" => Some(VAddMinU32(arguments))
      case "v_cmp_gt_f64" => Some(VCmpGtF64(arguments))
      case "v_cmp_lg_i16" => Some(VCmpLgI16(arguments))
      case "v_cmpx_le_u16" => Some(VCmpxLeU16(arguments))
      case "v_cmpx_nlt_f32" => Some(VCmpxNltF32(arguments))
      case "v_cvt_i32_i16" => Some(VCvtI32I16(arguments))
      case "v_cvt_scalef32_pk32_f32_bf6" => Some(VCvtScalef32Pk32F32Bf6(arguments))
      case "v_dot4_f32_fp8_fp8" => Some(VDot4F32Fp8Fp8(arguments))
      case "v_interp_p2_f16" => Some(VInterpP2F16(arguments))
      case "v_lshr_b32" => Some(VLshrB32(arguments))
      case "v_max_u32" => Some(VMaxU32(arguments))
      case "v_mul_lo_i32" => Some(VMulLoI32(arguments))
      case _ => None
    }

  private def decode49(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_min_num_f64" => Some(BufferAtomicMinNumF64(arguments))
      case "buffer_atomic_or_b64" => Some(BufferAtomicOrB64(arguments))
      case "buffer_load_d16_hi_u8" => Some(BufferLoadD16HiU8(arguments))
      case "buffer_load_u8" => Some(BufferLoadU8(arguments))
      case "ds_max_src2_i64" => Some(DsMaxSrc2I64(arguments))
      case "ds_read_u16_d16_hi" => Some(DsReadU16D16Hi(arguments))
      case "ds_store_b16" => Some(DsStoreB16(arguments))
      case "ds_write2_b32" => Some(DsWrite2B32(arguments))
      case "global_atomic_max_u32" => Some(GlobalAtomicMaxU32(arguments))
      case "global_atomic_pk_add_f16" => Some(GlobalAtomicPkAddF16(arguments))
      case "s_andn1_saveexec_b32" => Some(SAndn1SaveexecB32(arguments))
      case "s_bcnt1_i32_b32" => Some(SBcnt1I32B32(arguments))
      case "s_buffer_atomic_inc" => Some(SBufferAtomicInc(arguments))
      case "s_cmp_eq_f16" => Some(SCmpEqF16(arguments))
      case "s_cmp_lg_u32" => Some(SCmpLgU32(arguments))
      case "s_waitcnt_vscnt" => Some(SWaitcntVscnt(arguments))
      case "v_cmp_f_u32" => Some(VCmpFU32(arguments))
      case "v_cmp_lg_i64" => Some(VCmpLgI64(arguments))
      case "v_cmpx_le_u64" => Some(VCmpxLeU64(arguments))
      case "v_cmpx_t_u32" => Some(VCmpxTU32(arguments))
      case "v_cvt_pk_fp8_f16" => Some(VCvtPkFp8F16(arguments))
      case "v_cvt_scalef32_pk8_bf8_f16" => Some(VCvtScalef32Pk8Bf8F16(arguments))
      case "v_cvt_scalef32_pk_fp4_f32" => Some(VCvtScalef32PkFp4F32(arguments))
      case "v_div_scale_f32" => Some(VDivScaleF32(arguments))
      case "v_dot8_u32_u4" => Some(VDot8U32U4(arguments))
      case "v_fmaak_f32" => Some(VFmaakF32(arguments))
      case "v_mad_mixhi_f16" => Some(VMadMixhiF16(arguments))
      case "v_med3_u32" => Some(VMed3U32(arguments))
      case "v_minimummaximum_f32" => Some(VMinimummaximumF32(arguments))
      case "v_smfmac_f32_16x16x64_fp8_fp8" => Some(VSmfmacF3216x16x64Fp8Fp8(arguments))
      case "v_sub_co_u32" => Some(VSubCoU32(arguments))
      case _ => None
    }

  private def decode50(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_csub" => Some(BufferAtomicCsub(arguments))
      case "buffer_store_d16_format_xy" => Some(BufferStoreD16FormatXy(arguments))
      case "ds_load_i8_d16" => Some(DsLoadI8D16(arguments))
      case "ds_store_b64" => Some(DsStoreB64(arguments))
      case "flat_atomic_smax_x2" => Some(FlatAtomicSmaxX2(arguments))
      case "global_atomic_csub_u32" => Some(GlobalAtomicCsubU32(arguments))
      case "global_atomic_dec" => Some(GlobalAtomicDec(arguments))
      case "image_sample_cd_cl_o" => Some(ImageSampleCdClO_2(arguments))
      case "s_buffer_load_b64" => Some(SBufferLoadB64(arguments))
      case "s_nand_saveexec_b32" => Some(SNandSaveexecB32(arguments))
      case "s_sext_i32_i16" => Some(SSextI32I16(arguments))
      case "scratch_store_block" => Some(ScratchStoreBlock(arguments))
      case "tensor_load_to_lds" => Some(TensorLoadToLds(arguments))
      case "v_bfm_b32" => Some(VBfmB32(arguments))
      case "v_cmp_ne_u32" => Some(VCmpNeU32(arguments))
      case "v_cmpx_le_i32" => Some(VCmpxLeI32(arguments))
      case "v_cvt_f16_f32" => Some(VCvtF16F32(arguments))
      case "v_cvt_scalef32_sr_pk32_bf6_f32" => Some(VCvtScalef32SrPk32Bf6F32(arguments))
      case "v_dot2_u32_u16" => Some(VDot2U32U16(arguments))
      case "v_fma_dx9_zero_f32" => Some(VFmaDx9ZeroF32(arguments))
      case "v_mul_hi_i32" => Some(VMulHiI32(arguments))
      case "v_pk_add_f16" => Some(VPkAddF16(arguments))
      case "v_rcp_f16" => Some(VRcpF16(arguments))
      case "v_smfmac_f32_32x32x16_bf16" => Some(VSmfmacF3232x32x16Bf16(arguments))
      case "v_wmma_f32_16x16x128_f8f6f4" => Some(VWmmaF3216x16x128F8f6f4(arguments))
      case _ => None
    }

  private def decode51(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_d16_i8" => Some(BufferLoadD16I8(arguments))
      case "ds_min_u32" => Some(DsMinU32(arguments))
      case "flat_atomic_sub_x2" => Some(FlatAtomicSubX2(arguments))
      case "global_atomic_dec_u32" => Some(GlobalAtomicDecU32(arguments))
      case "global_atomic_inc_x2" => Some(GlobalAtomicIncX2(arguments))
      case "global_atomic_or" => Some(GlobalAtomicOr(arguments))
      case "image_gather4_cl_o" => Some(ImageGather4ClO_2(arguments))
      case "image_load_mip" => Some(ImageLoadMip(arguments))
      case "image_sample_c_b_cl_o" => Some(ImageSampleCBClO(arguments))
      case "s_add_co_u32" => Some(SAddCoU32(arguments))
      case "s_buffer_atomic_smax_x2" => Some(SBufferAtomicSmaxX2(arguments))
      case "s_clz_i32_u32" => Some(SClzI32U32(arguments))
      case "s_cmp_lt_f32" => Some(SCmpLtF32(arguments))
      case "s_cmpk_eq_i32" => Some(SCmpkEqI32(arguments))
      case "s_cmpk_lt_u32" => Some(SCmpkLtU32(arguments))
      case "s_load_b64" => Some(SLoadB64(arguments))
      case "s_lshr_b32" => Some(SLshrB32(arguments))
      case "s_min_u32" => Some(SMinU32(arguments))
      case "scratch_load_ubyte_d16" => Some(ScratchLoadUbyteD16(arguments))
      case "v_accvgpr_write" => Some(VAccvgprWrite(arguments))
      case "v_cmp_ge_i16" => Some(VCmpGeI16(arguments))
      case "v_cvt_f32_u32" => Some(VCvtF32U32(arguments))
      case "v_cvt_scalef32_pk8_fp8_f16" => Some(VCvtScalef32Pk8Fp8F16(arguments))
      case "v_cvt_scalef32_pk_f32_bf8" => Some(VCvtScalef32PkF32Bf8(arguments))
      case "v_fma_mixlo_bf16" => Some(VFmaMixloBf16(arguments))
      case "v_mfma_f32_32x32x8bf16_1k" => Some(VMfmaF3232x32x8bf161k(arguments))
      case "v_min3_u32" => Some(VMin3U32(arguments))
      case "v_permlane_xor_b32" => Some(VPermlaneXorB32(arguments))
      case "v_rcp_f64" => Some(VRcpF64(arguments))
      case "v_s_rsq_f16" => Some(VSRsqF16(arguments))
      case "v_subrev_u16" => Some(VSubrevU16(arguments))
      case "v_swaprel_b32" => Some(VSwaprelB32(arguments))
      case "v_swmmac_f16_16x16x128_bf8_fp8" => Some(VSwmmacF1616x16x128Bf8Fp8(arguments))
      case "v_swmmac_f32_16x16x32_bf8_bf8" => Some(VSwmmacF3216x16x32Bf8Bf8(arguments))
      case "v_wmma_f16_16x16x128_bf8_fp8" => Some(VWmmaF1616x16x128Bf8Fp8(arguments))
      case _ => None
    }

  private def decode52(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_umax" => Some(BufferAtomicUmax(arguments))
      case "buffer_wbinvl1" => Some(BufferWbinvl1(arguments))
      case "ds_bpermute_fi_b32" => Some(DsBpermuteFiB32(arguments))
      case "ds_cmpst_f64" => Some(DsCmpstF64(arguments))
      case "ds_swizzle_b32" => Some(DsSwizzleB32(arguments))
      case "ds_write2st64_b32" => Some(DsWrite2st64B32(arguments))
      case "flat_atomic_add_u32" => Some(FlatAtomicAddU32(arguments))
      case "flat_atomic_umax_x2" => Some(FlatAtomicUmaxX2(arguments))
      case "flat_atomic_xor_x2" => Some(FlatAtomicXorX2(arguments))
      case "global_load_block" => Some(GlobalLoadBlock(arguments))
      case "s_add_co_ci_u32" => Some(SAddCoCiU32(arguments))
      case "s_barrier_init" => Some(SBarrierInit(arguments))
      case "s_buffer_atomic_umax" => Some(SBufferAtomicUmax(arguments))
      case "s_cmp_ge_u32" => Some(SCmpGeU32(arguments))
      case "s_cmp_nlg_f16" => Some(SCmpNlgF16(arguments))
      case "s_endpgm_ordered_ps_done" => Some(SEndpgmOrderedPsDone(arguments))
      case "v_bitop3_b32" => Some(VBitop3B32(arguments))
      case "v_cmp_ge_i64" => Some(VCmpGeI64(arguments))
      case "v_cmpx_f_f32" => Some(VCmpxFF32(arguments))
      case "v_cubesc_f32" => Some(VCubescF32(arguments))
      case "v_cvt_scalef32_sr_pk32_bf6_bf16" => Some(VCvtScalef32SrPk32Bf6Bf16(arguments))
      case "v_cvt_scalef32_sr_pk32_fp6_f32" => Some(VCvtScalef32SrPk32Fp6F32(arguments))
      case "v_dot2_f32_bf16" => Some(VDot2F32Bf16(arguments))
      case "v_exp_bf16" => Some(VExpBf16(arguments))
      case "v_lshlrev_b64_e64" => Some(VLshlrevB64E64(arguments))
      case "v_mfma_f32_16x16x4f32" => Some(VMfmaF3216x16x4f32_2(arguments))
      case "v_mul_f64_e64" => Some(VMulF64E64(arguments))
      case "v_or_b16" => Some(VOrB16(arguments))
      case "v_rcp_clamp_f32" => Some(VRcpClampF32(arguments))
      case "v_rcp_legacy_f32" => Some(VRcpLegacyF32(arguments))
      case "v_smfmac_f32_16x16x32_bf16" => Some(VSmfmacF3216x16x32Bf16(arguments))
      case "v_smfmac_f32_32x32x32_fp8_bf8" => Some(VSmfmacF3232x32x32Fp8Bf8(arguments))
      case "v_sub_nc_u16" => Some(VSubNcU16(arguments))
      case _ => None
    }

  private def decode53(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_min_i64" => Some(BufferAtomicMinI64(arguments))
      case "ds_cmpstore_rtn_f64" => Some(DsCmpstoreRtnF64(arguments))
      case "ds_min_src2_f32" => Some(DsMinSrc2F32(arguments))
      case "flat_atomic_pk_add_bf16" => Some(FlatAtomicPkAddBf16(arguments))
      case "flat_atomic_xor" => Some(FlatAtomicXor(arguments))
      case "global_atomic_add_u64" => Some(GlobalAtomicAddU64(arguments))
      case "s_add_pc_i64" => Some(SAddPcI64(arguments))
      case "s_andn2_wrexec_b64" => Some(SAndn2WrexecB64(arguments))
      case "s_atomic_sub_x2" => Some(SAtomicSubX2(arguments))
      case "s_bitset1_b64" => Some(SBitset1B64(arguments))
      case "s_buffer_atomic_umax_x2" => Some(SBufferAtomicUmaxX2(arguments))
      case "s_load_dword" => Some(SLoadDword(arguments))
      case "s_orn1_saveexec_b32" => Some(SOrn1SaveexecB32(arguments))
      case "s_set_vgpr_msb" => Some(SSetVgprMsb(arguments))
      case "s_setreg_imm32_b32" => Some(SSetregImm32B32(arguments))
      case "s_waitcnt_lgkmcnt" => Some(SWaitcntLgkmcnt(arguments))
      case "scratch_load_block" => Some(ScratchLoadBlock(arguments))
      case "scratch_load_lds_ubyte" => Some(ScratchLoadLdsUbyte(arguments))
      case "v_cmp_lg_f16" => Some(VCmpLgF16(arguments))
      case "v_max3_u32" => Some(VMax3U32(arguments))
      case "v_maximumminimum_f32" => Some(VMaximumminimumF32(arguments))
      case "v_mfma_i32_16x16x4_4b_i8" => Some(VMfmaI3216x16x44bI8(arguments))
      case "v_rsq_f32" => Some(VRsqF32(arguments))
      case "v_screen_partition_4se_b32" => Some(VScreenPartition4seB32(arguments))
      case "v_sub_nc_u64" => Some(VSubNcU64(arguments))
      case "v_swmmac_f16_16x16x128_fp8_fp8" => Some(VSwmmacF1616x16x128Fp8Fp8(arguments))
      case "v_swmmac_f32_16x16x32_f16" => Some(VSwmmacF3216x16x32F16(arguments))
      case "v_swmmac_f32_16x16x32_fp8_bf8" => Some(VSwmmacF3216x16x32Fp8Bf8(arguments))
      case "v_wmma_f16_16x16x128_fp8_fp8" => Some(VWmmaF1616x16x128Fp8Fp8(arguments))
      case _ => None
    }

  private def decode54(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_smax" => Some(BufferAtomicSmax(arguments))
      case "buffer_store_format_d16_xy" => Some(BufferStoreFormatD16Xy(arguments))
      case "ds_inc_u64" => Some(DsIncU64(arguments))
      case "ds_max_src2_f64" => Some(DsMaxSrc2F64(arguments))
      case "ds_read_u8_d16_hi" => Some(DsReadU8D16Hi(arguments))
      case "ds_store_b8_d16_hi" => Some(DsStoreB8D16Hi(arguments))
      case "ds_sub_clamp_rtn_u32" => Some(DsSubClampRtnU32(arguments))
      case "flat_atomic_sub" => Some(FlatAtomicSub(arguments))
      case "flat_atomic_sub_u64" => Some(FlatAtomicSubU64(arguments))
      case "global_load_d16_hi_i8" => Some(GlobalLoadD16HiI8(arguments))
      case "global_load_lds_sbyte" => Some(GlobalLoadLdsSbyte(arguments))
      case "image_load" => Some(ImageLoad(arguments))
      case "image_store_mip_by2" => Some(ImageStoreMipBy2(arguments))
      case "s_atomic_xor_x2" => Some(SAtomicXorX2(arguments))
      case "s_buffer_atomic_smax" => Some(SBufferAtomicSmax(arguments))
      case "s_call_i64" => Some(SCallI64(arguments))
      case "scratch_load_short_d16_hi" => Some(ScratchLoadShortD16Hi(arguments))
      case "v_add_max_u32" => Some(VAddMaxU32(arguments))
      case "v_ceil_f16" => Some(VCeilF16(arguments))
      case "v_cmp_lg_f64" => Some(VCmpLgF64(arguments))
      case "v_cvt_i32_f64" => Some(VCvtI32F64(arguments))
      case "v_cvt_scalef32_pk16_bf6_f32" => Some(VCvtScalef32Pk16Bf6F32(arguments))
      case "v_cvt_scalef32_sr_pk16_bf6_bf16" => Some(VCvtScalef32SrPk16Bf6Bf16(arguments))
      case "v_cvt_u32_f64" => Some(VCvtU32F64(arguments))
      case "v_fmac_f16" => Some(VFmacF16(arguments))
      case "v_maxmin_num_f16" => Some(VMaxminNumF16(arguments))
      case "v_min_f64_e64" => Some(VMinF64E64(arguments))
      case "v_minmax_num_f16" => Some(VMinmaxNumF16(arguments))
      case "v_smfmac_i32_32x32x64_i8" => Some(VSmfmacI3232x32x64I8(arguments))
      case "v_sub_i16" => Some(VSubI16(arguments))
      case "v_sub_nc_i32" => Some(VSubNcI32(arguments))
      case "v_swmmac_i32_16x16x64_iu4" => Some(VSwmmacI3216x16x64Iu4(arguments))
      case "v_wmma_f16_16x16x64_bf8_fp8" => Some(VWmmaF1616x16x64Bf8Fp8(arguments))
      case _ => None
    }

  private def decode55(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "cluster_load_async_to_lds_b64" => Some(ClusterLoadAsyncToLdsB64(arguments))
      case "ds_inc_rtn_u64" => Some(DsIncRtnU64(arguments))
      case "ds_write_b96" => Some(DsWriteB96(arguments))
      case "flat_atomic_sub_clamp_u32" => Some(FlatAtomicSubClampU32(arguments))
      case "flat_load_short_d16" => Some(FlatLoadShortD16(arguments))
      case "global_load_monitor_b128" => Some(GlobalLoadMonitorB128(arguments))
      case "image_gather4_c_cl_o" => Some(ImageGather4CClO(arguments))
      case "image_sample_d_o" => Some(ImageSampleDO(arguments))
      case "s_add_nc_u64" => Some(SAddNcU64(arguments))
      case "s_andn2_saveexec_b64" => Some(SAndn2SaveexecB64(arguments))
      case "s_cmp_le_i32" => Some(SCmpLeI32(arguments))
      case "s_cmp_nge_f16" => Some(SCmpNgeF16(arguments))
      case "s_swap_pc_i64" => Some(SSwapPcI64(arguments))
      case "s_waitcnt_vmcnt" => Some(SWaitcntVmcnt(arguments))
      case "scratch_load_ushort" => Some(ScratchLoadUshort(arguments))
      case "scratch_store_dword" => Some(ScratchStoreDword(arguments))
      case "v_ceil_f64" => Some(VCeilF64(arguments))
      case "v_cmp_ngt_f32" => Some(VCmpNgtF32(arguments))
      case "v_cmpx_le_f32" => Some(VCmpxLeF32(arguments))
      case "v_cubeid_f32" => Some(VCubeidF32(arguments))
      case "v_cvt_pk_f16_bf8" => Some(VCvtPkF16Bf8(arguments))
      case "v_cvt_pk_f32_fp8" => Some(VCvtPkF32Fp8(arguments))
      case "v_cvt_scalef32_sr_bf8_f32" => Some(VCvtScalef32SrBf8F32(arguments))
      case "v_cvt_sr_fp8_f32" => Some(VCvtSrFp8F32(arguments))
      case "v_fma_legacy_f16" => Some(VFmaLegacyF16(arguments))
      case "v_fmac_f64" => Some(VFmacF64(arguments))
      case "v_maxmin_f16" => Some(VMaxminF16(arguments))
      case "v_mfma_f32_16x16x1f32" => Some(VMfmaF3216x16x1f32(arguments))
      case "v_mfma_i32_32x32x4i8" => Some(VMfmaI3232x32x4i8(arguments))
      case "v_mfma_i32_4x4x4i8" => Some(VMfmaI324x4x4i8(arguments))
      case "v_pk_min3_i16" => Some(VPkMin3I16(arguments))
      case "v_swmmac_f32_16x16x128_bf8_bf8" => Some(VSwmmacF3216x16x128Bf8Bf8(arguments))
      case "v_wmma_f32_16x16x128_bf8_bf8" => Some(VWmmaF3216x16x128Bf8Bf8(arguments))
      case "v_xor3_b32" => Some(VXor3B32(arguments))
      case _ => None
    }

  private def decode56(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_add_u64" => Some(BufferAtomicAddU64(arguments))
      case "buffer_store_b32" => Some(BufferStoreB32(arguments))
      case "cluster_load_b64" => Some(ClusterLoadB64(arguments))
      case "ds_read_b32" => Some(DsReadB32(arguments))
      case "ds_write_addtid_b32" => Some(DsWriteAddtidB32(arguments))
      case "global_atomic_umin_x2" => Some(GlobalAtomicUminX2(arguments))
      case "image_gather4_c_lz" => Some(ImageGather4CLz(arguments))
      case "image_gather4_lz" => Some(ImageGather4Lz(arguments))
      case "image_store_mip_by4" => Some(ImageStoreMipBy4(arguments))
      case "s_bitcmp0_b64" => Some(SBitcmp0B64(arguments))
      case "s_cbranch_join" => Some(SCbranchJoin(arguments))
      case "s_dcache_inv_vol" => Some(SDcacheInvVol(arguments))
      case "s_flbit_i32" => Some(SFlbitI32(arguments))
      case "s_inst_prefetch" => Some(SInstPrefetch(arguments))
      case "s_store_dword" => Some(SStoreDword(arguments))
      case "v_cmp_ge_f16" => Some(VCmpGeF16(arguments))
      case "v_cmp_nlt_f16" => Some(VCmpNltF16(arguments))
      case "v_cmpx_gt_u16" => Some(VCmpxGtU16(arguments))
      case "v_cvt_scalef32_pk16_fp6_f32" => Some(VCvtScalef32Pk16Fp6F32(arguments))
      case "v_dual_mov_b32" => Some(VDualMovB32(arguments))
      case "v_mfma_f32_16x16x16_bf16" => Some(VMfmaF3216x16x16Bf16(arguments))
      case "v_mfma_f32_32x32x16_bf16" => Some(VMfmaF3232x32x16Bf16(arguments))
      case "v_permlane16_b32" => Some(VPermlane16B32(arguments))
      case "v_smfmac_f32_32x32x32_bf8_fp8" => Some(VSmfmacF3232x32x32Bf8Fp8(arguments))
      case "v_swmmac_f32_16x16x64_bf16" => Some(VSwmmacF3216x16x64Bf16(arguments))
      case "v_wmma_f16_16x16x64_fp8_fp8" => Some(VWmmaF1616x16x64Fp8Fp8(arguments))
      case _ => None
    }

  private def decode57(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_swap_b64" => Some(BufferAtomicSwapB64(arguments))
      case "buffer_load_d16_hi_i8" => Some(BufferLoadD16HiI8(arguments))
      case "buffer_load_i8" => Some(BufferLoadI8(arguments))
      case "buffer_store_lds_dword" => Some(BufferStoreLdsDword(arguments))
      case "ds_max_rtn_i64" => Some(DsMaxRtnI64(arguments))
      case "ds_max_u32" => Some(DsMaxU32(arguments))
      case "global_load_dword" => Some(GlobalLoadDword(arguments))
      case "s_buffer_atomic_dec" => Some(SBufferAtomicDec(arguments))
      case "s_fmac_f16" => Some(SFmacF16(arguments))
      case "s_lshl1_add_u32" => Some(SLshl1AddU32(arguments))
      case "s_max_u32" => Some(SMaxU32(arguments))
      case "s_nor_saveexec_b64" => Some(SNorSaveexecB64(arguments))
      case "s_or_not1_saveexec_b32" => Some(SOrNot1SaveexecB32(arguments))
      case "s_sendmsg" => Some(SSendmsg(arguments))
      case "s_sub_u32" => Some(SSubU32(arguments))
      case "s_waitcnt_depctr" => Some(SWaitcntDepctr(arguments))
      case "scratch_load_lds_sshort" => Some(ScratchLoadLdsSshort(arguments))
      case "scratch_store_dwordx2" => Some(ScratchStoreDwordx2(arguments))
      case "v_ashrrev_i16" => Some(VAshrrevI16(arguments))
      case "v_cmp_ge_f64" => Some(VCmpGeF64(arguments))
      case "v_cmp_nlt_f64" => Some(VCmpNltF64(arguments))
      case "v_cmpx_gt_u64" => Some(VCmpxGtU64(arguments))
      case "v_cmpx_nle_f32" => Some(VCmpxNleF32(arguments))
      case "v_cvt_scale_pk16_f32_bf6" => Some(VCvtScalePk16F32Bf6(arguments))
      case "v_cvt_scalef32_sr_fp8_f32" => Some(VCvtScalef32SrFp8F32(arguments))
      case "v_cvt_scalef32_sr_pk8_fp4_f16" => Some(VCvtScalef32SrPk8Fp4F16(arguments))
      case "v_swmmac_f32_16x16x128_fp8_bf8" => Some(VSwmmacF3216x16x128Fp8Bf8(arguments))
      case "v_wmma_f32_16x16x128_fp8_bf8" => Some(VWmmaF3216x16x128Fp8Bf8(arguments))
      case _ => None
    }

  private def decode58(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_min_f64" => Some(BufferAtomicMinF64(arguments))
      case "buffer_atomic_sub_x2" => Some(BufferAtomicSubX2(arguments))
      case "buffer_load_dwordx2" => Some(BufferLoadDwordx2(arguments))
      case "ds_cmpst_rtn_f32" => Some(DsCmpstRtnF32(arguments))
      case "ds_mskor_rtn_b64" => Some(DsMskorRtnB64(arguments))
      case "ds_store_addtid_b32" => Some(DsStoreAddtidB32(arguments))
      case "global_load_sshort" => Some(GlobalLoadSshort(arguments))
      case "global_store_addtid_b32" => Some(GlobalStoreAddtidB32(arguments))
      case "image_store_pck" => Some(ImageStorePck(arguments))
      case "s_cbranch_i_fork" => Some(SCbranchIFork(arguments))
      case "s_cmp_u_f32" => Some(SCmpUF32(arguments))
      case "s_ff0_i32_b64" => Some(SFf0I32B64(arguments))
      case "s_rfe_b64" => Some(SRfeB64(arguments))
      case "s_scratch_load_dword" => Some(SScratchLoadDword(arguments))
      case "s_wait_idle" => Some(SWaitIdle(arguments))
      case "s_wait_storecnt" => Some(SWaitStorecnt(arguments))
      case "scratch_load_dword" => Some(ScratchLoadDword(arguments))
      case "scratch_store_dwordx3" => Some(ScratchStoreDwordx3(arguments))
      case "v_ashr_i32" => Some(VAshrI32(arguments))
      case "v_ashrrev_i64" => Some(VAshrrevI64(arguments))
      case "v_cmpx_gt_i32" => Some(VCmpxGtI32(arguments))
      case "v_dot4c_i32_i8" => Some(VDot4cI32I8(arguments))
      case "v_mac_f16" => Some(VMacF16(arguments))
      case "v_mad_nc_i64_i32" => Some(VMadNcI64I32(arguments))
      case "v_mfma_f32_16x16x32_bf16" => Some(VMfmaF3216x16x32Bf16(arguments))
      case "v_minimum3_f16" => Some(VMinimum3F16(arguments))
      case "v_subrev_f32" => Some(VSubrevF32(arguments))
      case "v_wmma_f32_16x16x64_bf8_bf8" => Some(VWmmaF3216x16x64Bf8Bf8(arguments))
      case _ => None
    }

  private def decode59(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_max_i64" => Some(BufferAtomicMaxI64(arguments))
      case "buffer_atomic_xor_x2" => Some(BufferAtomicXorX2(arguments))
      case "buffer_load_dwordx3" => Some(BufferLoadDwordx3(arguments))
      case "buffer_store_d16_hi_format_x" => Some(BufferStoreD16HiFormatX(arguments))
      case "buffer_store_short_d16_hi" => Some(BufferStoreShortD16Hi(arguments))
      case "ds_dec_u32" => Some(DsDecU32(arguments))
      case "ds_store_2addr_b32" => Some(DsStore2addrB32(arguments))
      case "flat_atomic_pk_add_f16" => Some(FlatAtomicPkAddF16(arguments))
      case "global_atomic_add_f32" => Some(GlobalAtomicAddF32(arguments))
      case "global_load_b64" => Some(GlobalLoadB64(arguments))
      case "global_load_dwordx2" => Some(GlobalLoadDwordx2(arguments))
      case "image_atomic_inc_uint" => Some(ImageAtomicIncUint(arguments))
      case "image_atomic_min_uint" => Some(ImageAtomicMinUint(arguments))
      case "s_addc_u32" => Some(SAddcU32(arguments))
      case "s_addk_i32" => Some(SAddkI32(arguments))
      case "s_atomic_xor" => Some(SAtomicXor(arguments))
      case "s_bfm_b32" => Some(SBfmB32(arguments))
      case "s_buffer_atomic_and_x2" => Some(SBufferAtomicAndX2(arguments))
      case "s_rndne_f16" => Some(SRndneF16(arguments))
      case "s_scratch_store_dword" => Some(SScratchStoreDword(arguments))
      case "scratch_store_dwordx4" => Some(ScratchStoreDwordx4(arguments))
      case "tbuffer_store_format_d16_xyzw" => Some(TbufferStoreFormatD16Xyzw(arguments))
      case "v_alignbit_b32" => Some(VAlignbitB32(arguments))
      case "v_cmpx_lt_i16" => Some(VCmpxLtI16(arguments))
      case "v_cvt_scalef32_pk_bf16_fp4" => Some(VCvtScalef32PkBf16Fp4(arguments))
      case "v_dual_mul_f64" => Some(VDualMulF64(arguments))
      case "v_interp_p2_rtz_f16_f32" => Some(VInterpP2RtzF16F32(arguments))
      case "v_lshrrev_b32" => Some(VLshrrevB32(arguments))
      case "v_mfma_f32_16x16x128_f8f6f4" => Some(VMfmaF3216x16x128F8f6f4(arguments))
      case "v_smfmac_f32_16x16x64_f16" => Some(VSmfmacF3216x16x64F16(arguments))
      case "v_sub_f16" => Some(VSubF16(arguments))
      case "v_swmmac_bf16f32_16x16x64_bf16" => Some(VSwmmacBf16f3216x16x64Bf16(arguments))
      case "v_xad_u32" => Some(VXadU32(arguments))
      case _ => None
    }

  private def decode60(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_and_b32" => Some(BufferAtomicAndB32(arguments))
      case "buffer_atomic_or" => Some(BufferAtomicOr(arguments))
      case "buffer_load_dwordx4" => Some(BufferLoadDwordx4(arguments))
      case "buffer_load_short_d16_hi" => Some(BufferLoadShortD16Hi(arguments))
      case "ds_add_src2_u64" => Some(DsAddSrc2U64(arguments))
      case "ds_condxchg32_rtn_b128" => Some(DsCondxchg32RtnB128(arguments))
      case "ds_read_u8" => Some(DsReadU8(arguments))
      case "flat_load_b128" => Some(FlatLoadB128(arguments))
      case "global_atomic_swap_b64" => Some(GlobalAtomicSwapB64(arguments))
      case "global_load_dwordx3" => Some(GlobalLoadDwordx3(arguments))
      case "global_load_sbyte_d16_hi" => Some(GlobalLoadSbyteD16Hi(arguments))
      case "s_atomic_sub" => Some(SAtomicSub(arguments))
      case "s_cmp_le_f32" => Some(SCmpLeF32(arguments))
      case "s_cmpk_le_u32" => Some(SCmpkLeU32(arguments))
      case "s_dcache_wb" => Some(SDcacheWb(arguments))
      case "s_load_dwordx16" => Some(SLoadDwordx16(arguments))
      case "s_not_b64" => Some(SNotB64(arguments))
      case "s_pack_ll_b32_b16" => Some(SPackLlB32B16(arguments))
      case "tbuffer_load_d16_format_x" => Some(TbufferLoadD16FormatX(arguments))
      case "v_cmpx_lt_i64" => Some(VCmpxLtI64(arguments))
      case "v_mad_u32" => Some(VMadU32(arguments))
      case "v_mfma_f32_32x32x16_f16" => Some(VMfmaF3232x32x16F16(arguments))
      case "v_mfma_scale_f32_16x16x128_f8f6f4" => Some(VMfmaScaleF3216x16x128F8f6f4(arguments))
      case "v_min_u16" => Some(VMinU16(arguments))
      case "v_pk_max_num_bf16" => Some(VPkMaxNumBf16(arguments))
      case "v_wmma_f32_16x16x64_fp8_bf8" => Some(VWmmaF3216x16x64Fp8Bf8(arguments))
      case "v_writelane_b32" => Some(VWritelaneB32(arguments))
      case "v_xor_b32" => Some(VXorB32(arguments))
      case _ => None
    }

  private def decode61(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_format_d16_xyz" => Some(BufferLoadFormatD16Xyz(arguments))
      case "ds_add_u64" => Some(DsAddU64(arguments))
      case "ds_max_num_rtn_f64" => Some(DsMaxNumRtnF64(arguments))
      case "ds_or_rtn_b64" => Some(DsOrRtnB64(arguments))
      case "ds_storexchg_rtn_b64" => Some(DsStorexchgRtnB64(arguments))
      case "ds_sub_rtn_u64" => Some(DsSubRtnU64(arguments))
      case "ds_wrap_rtn_b32" => Some(DsWrapRtnB32(arguments))
      case "flat_atomic_min_u32" => Some(FlatAtomicMinU32(arguments))
      case "global_atomic_cmpswap" => Some(GlobalAtomicCmpswap(arguments))
      case "global_atomic_xor_b32" => Some(GlobalAtomicXorB32(arguments))
      case "global_load_dwordx4" => Some(GlobalLoadDwordx4(arguments))
      case "image_atomic_add" => Some(ImageAtomicAdd(arguments))
      case "image_bvh64_intersect_ray" => Some(ImageBvh64IntersectRay(arguments))
      case "image_sample_b" => Some(ImageSampleB(arguments))
      case "s_bfe_i64" => Some(SBfeI64(arguments))
      case "s_cbranch_cdbgsys_and_user" => Some(SCbranchCdbgsysAndUser(arguments))
      case "v_cmp_eq_u16" => Some(VCmpEqU16(arguments))
      case "v_cmp_nlg_f32" => Some(VCmpNlgF32(arguments))
      case "v_cmpx_u_f16" => Some(VCmpxUF16(arguments))
      case "v_cvt_scalef32_pk8_fp4_bf16" => Some(VCvtScalef32Pk8Fp4Bf16(arguments))
      case "v_interp_p1_f32" => Some(VInterpP1F32(arguments))
      case "v_interp_p1lv_f16" => Some(VInterpP1lvF16(arguments))
      case "v_madak_f16" => Some(VMadakF16(arguments))
      case "v_min_u64" => Some(VMinU64(arguments))
      case "v_rsq_bf16" => Some(VRsqBf16(arguments))
      case "v_sad_u8" => Some(VSadU8(arguments))
      case "v_wmma_f16_16x16x16_f16" => Some(VWmmaF1616x16x16F16(arguments))
      case _ => None
    }

  private def decode62(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_add_f32" => Some(BufferAtomicAddF32(arguments))
      case "buffer_atomic_umin" => Some(BufferAtomicUmin(arguments))
      case "cluster_load_async_to_lds_b128" => Some(ClusterLoadAsyncToLdsB128(arguments))
      case "ds_load_i8_d16_hi" => Some(DsLoadI8D16Hi(arguments))
      case "ds_max_rtn_f64" => Some(DsMaxRtnF64(arguments))
      case "ds_read2_b32" => Some(DsRead2B32(arguments))
      case "ds_read_b64_tr_b16" => Some(DsReadB64TrB16(arguments))
      case "ds_write_b8" => Some(DsWriteB8(arguments))
      case "ds_xor_rtn_b32" => Some(DsXorRtnB32(arguments))
      case "global_atomic_and_x2" => Some(GlobalAtomicAndX2(arguments))
      case "global_atomic_cmpswap_b64" => Some(GlobalAtomicCmpswapB64(arguments))
      case "global_atomic_min_u64" => Some(GlobalAtomicMinU64(arguments))
      case "global_load_async_to_lds_b8" => Some(GlobalLoadAsyncToLdsB8(arguments))
      case "image_sample_c" => Some(ImageSampleC(arguments))
      case "s_buffer_atomic_umin" => Some(SBufferAtomicUmin(arguments))
      case "s_buffer_load_dwordx2" => Some(SBufferLoadDwordx2(arguments))
      case "scratch_load_u16" => Some(ScratchLoadU16(arguments))
      case "v_cmp_eq_u64" => Some(VCmpEqU64(arguments))
      case "v_cmp_tru_u32" => Some(VCmpTruU32(arguments))
      case "v_cmpx_lg_u16" => Some(VCmpxLgU16(arguments))
      case "v_cmpx_u_f64" => Some(VCmpxUF64(arguments))
      case "v_cvt_norm_i16_f16" => Some(VCvtNormI16F16(arguments))
      case "v_cvt_pk_i16_i32" => Some(VCvtPkI16I32(arguments))
      case "v_cvt_scale_pk8_bf16_fp4" => Some(VCvtScalePk8Bf16Fp4(arguments))
      case "v_dual_lshrrev_b32" => Some(VDualLshrrevB32(arguments))
      case "v_min_i32" => Some(VMinI32(arguments))
      case "v_pk_min_num_f16" => Some(VPkMinNumF16(arguments))
      case "v_swmmac_f16_16x16x64_f16" => Some(VSwmmacF1616x16x64F16(arguments))
      case "v_wmma_scale16_f32_32x16x128_f4" => Some(VWmmaScale16F3232x16x128F4(arguments))
      case _ => None
    }

  private def decode63(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_store_format_d16_hi_x" => Some(BufferStoreFormatD16HiX(arguments))
      case "ds_dec_rtn_u64" => Some(DsDecRtnU64(arguments))
      case "flat_load_i16" => Some(FlatLoadI16(arguments))
      case "global_atomic_min_i32" => Some(GlobalAtomicMinI32(arguments))
      case "global_atomic_xor" => Some(GlobalAtomicXor(arguments))
      case "global_store_block" => Some(GlobalStoreBlock(arguments))
      case "image_sample_c_d_o" => Some(ImageSampleCDO_1(arguments))
      case "image_sample_d" => Some(ImageSampleD(arguments))
      case "s_buffer_prefetch_data" => Some(SBufferPrefetchData(arguments))
      case "s_cmp_gt_i32" => Some(SCmpGtI32(arguments))
      case "s_load_dwordx2" => Some(SLoadDwordx2(arguments))
      case "s_min_num_f32" => Some(SMinNumF32(arguments))
      case "s_or_saveexec_b64" => Some(SOrSaveexecB64(arguments))
      case "s_sethalt" => Some(SSethalt(arguments))
      case "s_sext_i32_i8" => Some(SSextI32I8(arguments))
      case "s_sleep" => Some(SSleep(arguments))
      case "scratch_load_b96" => Some(ScratchLoadB96(arguments))
      case "scratch_store_d16_hi_b8" => Some(ScratchStoreD16HiB8(arguments))
      case "v_cmp_eq_i32" => Some(VCmpEqI32(arguments))
      case "v_cmp_lt_u32" => Some(VCmpLtU32(arguments))
      case "v_cmpx_gt_f32" => Some(VCmpxGtF32(arguments))
      case "v_cmpx_lg_u64" => Some(VCmpxLgU64(arguments))
      case "v_cvt_f16_i16" => Some(VCvtF16I16(arguments))
      case "v_cvt_scale_pk16_bf16_bf6" => Some(VCvtScalePk16Bf16Bf6(arguments))
      case "v_cvt_scalef32_pk_bf16_fp8" => Some(VCvtScalef32PkBf16Fp8(arguments))
      case "v_cvt_scalef32_pk_bf8_bf16" => Some(VCvtScalef32PkBf8Bf16(arguments))
      case "v_div_fixup_legacy_f16" => Some(VDivFixupLegacyF16(arguments))
      case "v_dot2c_f32_f16" => Some(VDot2cF32F16(arguments))
      case "v_madmk_f32" => Some(VMadmkF32(arguments))
      case "v_min3_num_f32" => Some(VMin3NumF32(arguments))
      case "v_movrels_b32" => Some(VMovrelsB32(arguments))
      case "v_pk_minimum_f16" => Some(VPkMinimumF16(arguments))
      case "v_s_log_f16" => Some(VSLogF16(arguments))
      case "v_sat_pk4_i4_i8" => Some(VSatPk4I4I8(arguments))
      case "v_sat_pk4_u4_u8" => Some(VSatPk4U4U8(arguments))
      case _ => None
    }

  private def decode64(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_max_f64" => Some(BufferAtomicMaxF64(arguments))
      case "buffer_atomic_smin" => Some(BufferAtomicSmin(arguments))
      case "buffer_atomic_xor_b32" => Some(BufferAtomicXorB32(arguments))
      case "buffer_load_i16" => Some(BufferLoadI16(arguments))
      case "ds_load_b32" => Some(DsLoadB32(arguments))
      case "ds_load_u8" => Some(DsLoadU8(arguments))
      case "ds_read_b96_tr_b6" => Some(DsReadB96TrB6(arguments))
      case "ds_rsub_rtn_u32" => Some(DsRsubRtnU32(arguments))
      case "flat_atomic_inc_u64" => Some(FlatAtomicIncU64(arguments))
      case "flat_load_d16_hi_u8" => Some(FlatLoadD16HiU8(arguments))
      case "flat_load_d16_u8" => Some(FlatLoadD16U8(arguments))
      case "flat_load_monitor_b64" => Some(FlatLoadMonitorB64(arguments))
      case "global_atomic_sub" => Some(GlobalAtomicSub(arguments))
      case "global_load_async_to_lds_b64" => Some(GlobalLoadAsyncToLdsB64(arguments))
      case "global_load_sbyte_d16" => Some(GlobalLoadSbyteD16(arguments))
      case "image_sample_d_g16" => Some(ImageSampleDG16(arguments))
      case "s_buffer_atomic_smin" => Some(SBufferAtomicSmin(arguments))
      case "s_buffer_load_dword" => Some(SBufferLoadDword(arguments))
      case "s_buffer_load_dwordx4" => Some(SBufferLoadDwordx4(arguments))
      case "s_cvt_pk_rtz_f16_f32" => Some(SCvtPkRtzF16F32(arguments))
      case "s_flbit_i32_i64" => Some(SFlbitI32I64(arguments))
      case "s_load_u8" => Some(SLoadU8(arguments))
      case "s_maximum_f16" => Some(SMaximumF16(arguments))
      case "s_minimum_f32" => Some(SMinimumF32(arguments))
      case "s_mulk_i32" => Some(SMulkI32(arguments))
      case "s_pack_hl_b32_b16" => Some(SPackHlB32B16(arguments))
      case "s_setkill" => Some(SSetkill(arguments))
      case "s_version" => Some(SVersion(arguments))
      case "scratch_store_b96" => Some(ScratchStoreB96(arguments))
      case "tbuffer_store_format_xy" => Some(TbufferStoreFormatXy(arguments))
      case "v_cmp_nge_f32" => Some(VCmpNgeF32(arguments))
      case "v_cmpx_lg_i32" => Some(VCmpxLgI32(arguments))
      case "v_cmpx_lt_f16" => Some(VCmpxLtF16(arguments))
      case "v_cvt_floor_i32_f32" => Some(VCvtFloorI32F32(arguments))
      case "v_cvt_scale_pk8_f16_fp4" => Some(VCvtScalePk8F16Fp4(arguments))
      case "v_cvt_scalef32_sr_pk16_fp6_bf16" => Some(VCvtScalef32SrPk16Fp6Bf16(arguments))
      case "v_dual_bitop2_b32" => Some(VDualBitop2B32(arguments))
      case "v_mbcnt_hi_u32_b32_e64" => Some(VMbcntHiU32B32E64(arguments))
      case "v_mul_i32_i24" => Some(VMulI32I24(arguments))
      case _ => None
    }

  private def decode65(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "ds_and_b32" => Some(DsAndB32(arguments))
      case "ds_cmpst_b32" => Some(DsCmpstB32(arguments))
      case "ds_max_num_f32" => Some(DsMaxNumF32(arguments))
      case "flat_atomic_dec_x2" => Some(FlatAtomicDecX2(arguments))
      case "flat_atomic_min_num_f32" => Some(FlatAtomicMinNumF32(arguments))
      case "flat_load_monitor_b128" => Some(FlatLoadMonitorB128(arguments))
      case "global_atomic_max_num_f32" => Some(GlobalAtomicMaxNumF32(arguments))
      case "global_load_tr_b128" => Some(GlobalLoadTrB128(arguments))
      case "global_store_b16" => Some(GlobalStoreB16(arguments))
      case "s_bitset0_b32" => Some(SBitset0B32(arguments))
      case "s_buffer_atomic_add_x2" => Some(SBufferAtomicAddX2(arguments))
      case "s_buffer_load_u8" => Some(SBufferLoadU8(arguments))
      case "s_load_dwordx4" => Some(SLoadDwordx4(arguments))
      case "s_xnor_saveexec_b64" => Some(SXnorSaveexecB64(arguments))
      case "scratch_load_d16_b16" => Some(ScratchLoadD16B16(arguments))
      case "v_add_u32" => Some(VAddU32(arguments))
      case "v_cmp_nle_f16" => Some(VCmpNleF16(arguments))
      case "v_cmp_u_f32" => Some(VCmpUF32(arguments))
      case "v_cmpx_f_i16" => Some(VCmpxFI16(arguments))
      case "v_cmpx_ge_u16" => Some(VCmpxGeU16(arguments))
      case "v_cmpx_lt_f64" => Some(VCmpxLtF64(arguments))
      case "v_cmpx_ngt_f32" => Some(VCmpxNgtF32(arguments))
      case "v_cvt_pkaccum_u8_f32" => Some(VCvtPkaccumU8F32(arguments))
      case "v_cvt_scalef32_f16_fp8" => Some(VCvtScalef32F16Fp8(arguments))
      case "v_cvt_scalef32_sr_pk8_bf8_f32" => Some(VCvtScalef32SrPk8Bf8F32(arguments))
      case "v_dual_fmac_f32" => Some(VDualFmacF32(arguments))
      case "v_log_f32" => Some(VLogF32(arguments))
      case "v_mfma_f32_32x32x8_f16" => Some(VMfmaF3232x32x8F16_1(arguments))
      case "v_mfma_scale_f32_32x32x64_f8f6f4" => Some(VMfmaScaleF3232x32x64F8f6f4(arguments))
      case "v_pk_fma_bf16" => Some(VPkFmaBf16(arguments))
      case "v_pk_mul_f16" => Some(VPkMulF16(arguments))
      case "v_smfmac_f32_16x16x64_bf16" => Some(VSmfmacF3216x16x64Bf16(arguments))
      case _ => None
    }

  private def decode66(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_cmpswap_b32" => Some(BufferAtomicCmpswapB32(arguments))
      case "ds_add_src2_f32" => Some(DsAddSrc2F32(arguments))
      case "ds_bvh_stack_push8_pop1_rtn_b32" => Some(DsBvhStackPush8Pop1RtnB32(arguments))
      case "ds_cmpstore_rtn_b32" => Some(DsCmpstoreRtnB32(arguments))
      case "ds_mskor_b32" => Some(DsMskorB32(arguments))
      case "ds_sub_src2_u32" => Some(DsSubSrc2U32(arguments))
      case "ds_wrxchg2st64_rtn_b64" => Some(DsWrxchg2st64RtnB64(arguments))
      case "flat_atomic_max_num_f64" => Some(FlatAtomicMaxNumF64(arguments))
      case "flat_store_b8" => Some(FlatStoreB8(arguments))
      case "global_atomic_umax_x2" => Some(GlobalAtomicUmaxX2(arguments))
      case "global_store_b64" => Some(GlobalStoreB64(arguments))
      case "image_sample_c_d_cl_g16" => Some(ImageSampleCDClG16_1(arguments))
      case "s_addk_co_i32" => Some(SAddkCoI32(arguments))
      case "s_andn1_wrexec_b64" => Some(SAndn1WrexecB64(arguments))
      case "s_load_b256" => Some(SLoadB256(arguments))
      case "s_trunc_f32" => Some(STruncF32(arguments))
      case "v_cls_i32" => Some(VClsI32(arguments))
      case "v_cmp_nle_f64" => Some(VCmpNleF64(arguments))
      case "v_cmpx_f_i64" => Some(VCmpxFI64(arguments))
      case "v_cmpx_ge_u64" => Some(VCmpxGeU64(arguments))
      case "v_cmpx_nlt_f16" => Some(VCmpxNltF16(arguments))
      case "v_cvt_scale_pk8_bf16_fp8" => Some(VCvtScalePk8Bf16Fp8(arguments))
      case "v_dot2_bf16_bf16" => Some(VDot2Bf16Bf16(arguments))
      case "v_exp_f32" => Some(VExpF32(arguments))
      case "v_interp_p2_f16_f32" => Some(VInterpP2F16F32(arguments))
      case "v_lshl_add_u64" => Some(VLshlAddU64(arguments))
      case "v_mad_mix_f32" => Some(VMadMixF32(arguments))
      case "v_max_u16" => Some(VMaxU16(arguments))
      case "v_permlane_idx_gen_b32" => Some(VPermlaneIdxGenB32(arguments))
      case "v_smfmac_f32_32x32x16_f16" => Some(VSmfmacF3232x32x16F16(arguments))
      case _ => None
    }

  private def decode67(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_fmax" => Some(BufferAtomicFmax(arguments))
      case "buffer_store_b8" => Some(BufferStoreB8(arguments))
      case "buffer_store_dwordx2" => Some(BufferStoreDwordx2(arguments))
      case "ds_add_f32" => Some(DsAddF32(arguments))
      case "ds_atomic_async_barrier_arrive_b64" => Some(DsAtomicAsyncBarrierArriveB64(arguments))
      case "ds_inc_src2_u64" => Some(DsIncSrc2U64(arguments))
      case "ds_min_src2_i64" => Some(DsMinSrc2I64(arguments))
      case "ds_write_b16_d16_hi" => Some(DsWriteB16D16Hi(arguments))
      case "flat_atomic_max_u32" => Some(FlatAtomicMaxU32(arguments))
      case "global_load_monitor_b32" => Some(GlobalLoadMonitorB32(arguments))
      case "image_atomic_and" => Some(ImageAtomicAnd(arguments))
      case "image_atomic_fcmpswap" => Some(ImageAtomicFcmpswap(arguments))
      case "image_atomic_or" => Some(ImageAtomicOr(arguments))
      case "image_gather4_c_cl" => Some(ImageGather4CCl(arguments))
      case "image_gather4_cl" => Some(ImageGather4Cl_2(arguments))
      case "image_sample_b_cl_o" => Some(ImageSampleBClO(arguments))
      case "image_sample_c_cd_cl_o_g16" => Some(ImageSampleCCdClOG16(arguments))
      case "image_sample_cd_o_g16" => Some(ImageSampleCdOG16_2(arguments))
      case "image_sample_l_o" => Some(ImageSampleLO(arguments))
      case "image_sample_lz" => Some(ImageSampleLz(arguments))
      case "s_and_saveexec_b64" => Some(SAndSaveexecB64(arguments))
      case "s_atomic_dec_x2" => Some(SAtomicDecX2(arguments))
      case "s_bcnt0_i32_b64" => Some(SBcnt0I32B64(arguments))
      case "s_buffer_store_dwordx2" => Some(SBufferStoreDwordx2(arguments))
      case "s_cmp_neq_f32" => Some(SCmpNeqF32(arguments))
      case "s_cvt_f32_f16" => Some(SCvtF32F16(arguments))
      case "s_nand_b32" => Some(SNandB32(arguments))
      case "s_set_valu_coexec_mode" => Some(SSetValuCoexecMode(arguments))
      case "scratch_store_byte_d16_hi" => Some(ScratchStoreByteD16Hi(arguments))
      case "v_cmp_f_u16" => Some(VCmpFU16(arguments))
      case "v_cmpx_ge_i32" => Some(VCmpxGeI32(arguments))
      case "v_cmpx_nlt_f64" => Some(VCmpxNltF64(arguments))
      case "v_cmpx_t_u16" => Some(VCmpxTU16(arguments))
      case "v_cvt_pk_i16_f32" => Some(VCvtPkI16F32(arguments))
      case "v_cvt_pk_u16_f32" => Some(VCvtPkU16F32(arguments))
      case "v_cvt_scalef32_pk_fp4_f16" => Some(VCvtScalef32PkFp4F16(arguments))
      case "v_cvt_scalef32_sr_pk8_fp8_f32" => Some(VCvtScalef32SrPk8Fp8F32(arguments))
      case "v_fmaak_f16" => Some(VFmaakF16(arguments))
      case "v_interp_p1ll_f16" => Some(VInterpP1llF16(arguments))
      case "v_lshr_b64" => Some(VLshrB64(arguments))
      case "v_max_u64" => Some(VMaxU64(arguments))
      case "v_med3_u16" => Some(VMed3U16(arguments))
      case "v_min_f32" => Some(VMinF32(arguments))
      case "v_minimummaximum_f16" => Some(VMinimummaximumF16(arguments))
      case "v_mul_u64" => Some(VMulU64(arguments))
      case _ => None
    }

  private def decode68(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_fmin_x2" => Some(BufferAtomicFminX2(arguments))
      case "buffer_store_dwordx3" => Some(BufferStoreDwordx3(arguments))
      case "ds_consume" => Some(DsConsume(arguments))
      case "ds_read_i8" => Some(DsReadI8(arguments))
      case "ds_write2_b64" => Some(DsWrite2B64(arguments))
      case "flat_atomic_fcmpswap_x2" => Some(FlatAtomicFcmpswapX2(arguments))
      case "flat_store_short" => Some(FlatStoreShort(arguments))
      case "global_atomic_add_x2" => Some(GlobalAtomicAddX2(arguments))
      case "global_atomic_max_u64" => Some(GlobalAtomicMaxU64(arguments))
      case "global_atomic_min_f32" => Some(GlobalAtomicMinF32(arguments))
      case "global_load_lds_ubyte" => Some(GlobalLoadLdsUbyte(arguments))
      case "global_store_dword" => Some(GlobalStoreDword(arguments))
      case "global_store_short_d16_hi" => Some(GlobalStoreShortD16Hi(arguments))
      case "image_gather4_b_cl" => Some(ImageGather4BCl(arguments))
      case "s_andn1_saveexec_b64" => Some(SAndn1SaveexecB64(arguments))
      case "s_bcnt1_i32_b64" => Some(SBcnt1I32B64(arguments))
      case "s_buffer_load_dwordx8" => Some(SBufferLoadDwordx8(arguments))
      case "s_buffer_load_u16" => Some(SBufferLoadU16(arguments))
      case "s_cmp_gt_f32" => Some(SCmpGtF32(arguments))
      case "s_cmp_lg_u64" => Some(SCmpLgU64(arguments))
      case "s_cmpk_gt_u32" => Some(SCmpkGtU32(arguments))
      case "s_dcache_discard_x2" => Some(SDcacheDiscardX2(arguments))
      case "s_movk_i32" => Some(SMovkI32(arguments))
      case "s_movrels_b32" => Some(SMovrelsB32(arguments))
      case "s_pack_lh_b32_b16" => Some(SPackLhB32B16(arguments))
      case "s_round_mode" => Some(SRoundMode(arguments))
      case "s_subvector_loop_end" => Some(SSubvectorLoopEnd(arguments))
      case "s_waitcnt" => Some(SWaitcnt(arguments))
      case "v_add_min_i32" => Some(VAddMinI32(arguments))
      case "v_cmp_eq_f32" => Some(VCmpEqF32(arguments))
      case "v_cmp_f_u64" => Some(VCmpFU64(arguments))
      case "v_cmp_ne_u16" => Some(VCmpNeU16(arguments))
      case "v_cmpx_le_i16" => Some(VCmpxLeI16(arguments))
      case "v_cmpx_t_u64" => Some(VCmpxTU64(arguments))
      case "v_cvt_pk_bf16_f32" => Some(VCvtPkBf16F32(arguments))
      case "v_cvt_scale_pk8_f16_fp8" => Some(VCvtScalePk8F16Fp8(arguments))
      case "v_cvt_scalef32_pk16_bf6_bf16" => Some(VCvtScalef32Pk16Bf6Bf16(arguments))
      case "v_cvt_scalef32_sr_pk32_bf6_f16" => Some(VCvtScalef32SrPk32Bf6F16(arguments))
      case "v_cvt_sr_bf16_f32" => Some(VCvtSrBf16F32(arguments))
      case "v_div_scale_f64" => Some(VDivScaleF64(arguments))
      case "v_fma_mix_f32" => Some(VFmaMixF32(arguments))
      case "v_fmaak_f64" => Some(VFmaakF64(arguments))
      case "v_max_i32" => Some(VMaxI32(arguments))
      case "v_mfma_f32_16x16x32_bf8_bf8" => Some(VMfmaF3216x16x32Bf8Bf8(arguments))
      case "v_tanh_bf16" => Some(VTanhBf16(arguments))
      case _ => None
    }

  private def decode69(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_d16_format_xyz" => Some(BufferLoadD16FormatXyz(arguments))
      case "buffer_load_ubyte_d16" => Some(BufferLoadUbyteD16(arguments))
      case "buffer_load_ushort" => Some(BufferLoadUshort(arguments))
      case "buffer_store_dwordx4" => Some(BufferStoreDwordx4(arguments))
      case "ds_gws_sema_br" => Some(DsGwsSemaBr(arguments))
      case "ds_read2st64_b32" => Some(DsRead2st64B32(arguments))
      case "ds_store_b96" => Some(DsStoreB96(arguments))
      case "ds_xor_b32" => Some(DsXorB32(arguments))
      case "flat_atomic_cmpswap_f32" => Some(FlatAtomicCmpswapF32(arguments))
      case "flat_atomic_dec_u32" => Some(FlatAtomicDecU32(arguments))
      case "global_atomic_max_i32" => Some(GlobalAtomicMaxI32(arguments))
      case "image_atomic_pk_add_f16" => Some(ImageAtomicPkAddF16(arguments))
      case "image_sample_c_b" => Some(ImageSampleCB(arguments))
      case "s_buffer_atomic_cmpswap_x2" => Some(SBufferAtomicCmpswapX2(arguments))
      case "s_buffer_load_b96" => Some(SBufferLoadB96(arguments))
      case "s_buffer_store_dwordx4" => Some(SBufferStoreDwordx4(arguments))
      case "s_cmp_lg_i32" => Some(SCmpLgI32(arguments))
      case "s_cmp_lt_f16" => Some(SCmpLtF16(arguments))
      case "s_cselect_b32" => Some(SCselectB32(arguments))
      case "s_load_dwordx8" => Some(SLoadDwordx8(arguments))
      case "s_load_u16" => Some(SLoadU16(arguments))
      case "s_mul_hi_u32" => Some(SMulHiU32(arguments))
      case "s_nand_saveexec_b64" => Some(SNandSaveexecB64(arguments))
      case "v_cmp_f_i32" => Some(VCmpFI32(arguments))
      case "v_cmp_ne_u64" => Some(VCmpNeU64(arguments))
      case "v_cmpx_le_i64" => Some(VCmpxLeI64(arguments))
      case "v_cmpx_lg_f32" => Some(VCmpxLgF32(arguments))
      case "v_cmpx_t_i32" => Some(VCmpxTI32(arguments))
      case "v_fmamk_f32" => Some(VFmamkF32(arguments))
      case "v_med3_i32" => Some(VMed3I32(arguments))
      case "v_min3_u16" => Some(VMin3U16(arguments))
      case "v_subb_co_u32" => Some(VSubbCoU32(arguments))
      case _ => None
    }

  private def decode70(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "ds_load_u8_d16" => Some(DsLoadU8D16(arguments))
      case "ds_load_u8_d16_hi" => Some(DsLoadU8D16Hi(arguments))
      case "ds_min_u64" => Some(DsMinU64(arguments))
      case "flat_load_sbyte_d16_hi" => Some(FlatLoadSbyteD16Hi(arguments))
      case "flat_store_d16_hi_b8" => Some(FlatStoreD16HiB8(arguments))
      case "global_atomic_dec_u64" => Some(GlobalAtomicDecU64(arguments))
      case "global_prefetch_b8" => Some(GlobalPrefetchB8(arguments))
      case "global_store_dwordx2" => Some(GlobalStoreDwordx2(arguments))
      case "image_atomic_min_int" => Some(ImageAtomicMinInt(arguments))
      case "image_atomic_rsub" => Some(ImageAtomicRsub(arguments))
      case "image_gather4_l_o" => Some(ImageGather4LO(arguments))
      case "image_sample_cd" => Some(ImageSampleCd_2(arguments))
      case "s_buffer_atomic_xor" => Some(SBufferAtomicXor(arguments))
      case "s_clz_i32_u64" => Some(SClzI32U64(arguments))
      case "s_load_b96" => Some(SLoadB96(arguments))
      case "s_lshr_b64" => Some(SLshrB64(arguments))
      case "s_prefetch_inst_pc_rel" => Some(SPrefetchInstPcRel(arguments))
      case "s_setprio_inc_wg" => Some(SSetprioIncWg(arguments))
      case "v_bitop3_b16" => Some(VBitop3B16(arguments))
      case "v_cmp_ne_i32" => Some(VCmpNeI32(arguments))
      case "v_cmpx_f_f16" => Some(VCmpxFF16(arguments))
      case "v_cos_bf16" => Some(VCosBf16(arguments))
      case "v_cvt_f32_bf16" => Some(VCvtF32Bf16(arguments))
      case "v_cvt_f64_u32" => Some(VCvtF64U32(arguments))
      case "v_cvt_nearest_i32_f32" => Some(VCvtNearestI32F32(arguments))
      case "v_cvt_scalef32_sr_pk32_fp6_f16" => Some(VCvtScalef32SrPk32Fp6F16(arguments))
      case "v_ldexp_f32" => Some(VLdexpF32(arguments))
      case "v_mad_nc_u64_u32" => Some(VMadNcU64U32(arguments))
      case "v_mfma_f32_16x16x32_fp8_bf8" => Some(VMfmaF3216x16x32Fp8Bf8(arguments))
      case "v_mfma_f32_16x16x4f16" => Some(VMfmaF3216x16x4f16(arguments))
      case "v_mfma_f32_32x32x16_bf8_bf8" => Some(VMfmaF3232x32x16Bf8Bf8(arguments))
      case "v_permlane16_swap_b32" => Some(VPermlane16SwapB32(arguments))
      case "v_smfmac_f32_16x16x128_bf8_fp8" => Some(VSmfmacF3216x16x128Bf8Fp8(arguments))
      case "v_wmma_i32_16x16x64_iu8" => Some(VWmmaI3216x16x64Iu8(arguments))
      case _ => None
    }

  private def decode71(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_store_format_d16_xyzw" => Some(BufferStoreFormatD16Xyzw(arguments))
      case "ds_add_rtn_u32" => Some(DsAddRtnU32(arguments))
      case "ds_min_i32" => Some(DsMinI32(arguments))
      case "ds_pk_add_bf16" => Some(DsPkAddBf16(arguments))
      case "ds_write2st64_b64" => Some(DsWrite2st64B64(arguments))
      case "flat_atomic_add_u64" => Some(FlatAtomicAddU64(arguments))
      case "global_store_dwordx3" => Some(GlobalStoreDwordx3(arguments))
      case "image_sample_c_cd_cl_g16" => Some(ImageSampleCCdClG16(arguments))
      case "image_sample_c_d" => Some(ImageSampleCD_1(arguments))
      case "image_sample_l" => Some(ImageSampleL(arguments))
      case "s_add_co_i32" => Some(SAddCoI32(arguments))
      case "s_brev_b32" => Some(SBrevB32(arguments))
      case "s_buffer_atomic_sub" => Some(SBufferAtomicSub(arguments))
      case "s_ceil_f32" => Some(SCeilF32(arguments))
      case "s_cmov_b32" => Some(SCmovB32(arguments))
      case "s_cmovk_i32" => Some(SCmovkI32(arguments))
      case "s_cmpk_lt_i32" => Some(SCmpkLtI32(arguments))
      case "s_min_i32" => Some(SMinI32(arguments))
      case "s_sleep_var" => Some(SSleepVar(arguments))
      case "s_wait_xcnt" => Some(SWaitXcnt(arguments))
      case "s_waitcnt_expcnt" => Some(SWaitcntExpcnt(arguments))
      case "v_cmp_t_u32" => Some(VCmpTU32(arguments))
      case "v_cmpx_f_f64" => Some(VCmpxFF64(arguments))
      case "v_cmpx_nlg_f32" => Some(VCmpxNlgF32(arguments))
      case "v_cvt_f32_i32" => Some(VCvtF32I32(arguments))
      case "v_cvt_scalef32_pk_f16_fp4" => Some(VCvtScalef32PkF16Fp4(arguments))
      case "v_dual_subrev_f32" => Some(VDualSubrevF32(arguments))
      case "v_interp_p10_f32" => Some(VInterpP10F32(arguments))
      case "v_mad_i32_i16" => Some(VMadI32I16(arguments))
      case "v_max3_u16" => Some(VMax3U16(arguments))
      case "v_maximumminimum_f16" => Some(VMaximumminimumF16(arguments))
      case "v_mfma_f32_16x16x4bf16_1k" => Some(VMfmaF3216x16x4bf161k(arguments))
      case "v_min3_i32" => Some(VMin3I32(arguments))
      case "v_mul_legacy_f32" => Some(VMulLegacyF32(arguments))
      case "v_rcp_clamp_f64" => Some(VRcpClampF64(arguments))
      case "v_rsq_f16" => Some(VRsqF16(arguments))
      case "v_wmma_f32_16x16x16_bf8_fp8" => Some(VWmmaF3216x16x16Bf8Fp8(arguments))
      case "v_wmma_f32_16x16x16_f16" => Some(VWmmaF3216x16x16F16(arguments))
      case "v_wmma_f32_16x16x32_bf16" => Some(VWmmaF3216x16x32Bf16(arguments))
      case _ => None
    }

  private def decode72(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_dec_x2" => Some(BufferAtomicDecX2(arguments))
      case "ds_load_i8" => Some(DsLoadI8(arguments))
      case "ds_min_src2_f64" => Some(DsMinSrc2F64(arguments))
      case "flat_load_d16_hi_i8" => Some(FlatLoadD16HiI8(arguments))
      case "flat_load_d16_i8" => Some(FlatLoadD16I8(arguments))
      case "global_atomic_cond_sub_u32" => Some(GlobalAtomicCondSubU32(arguments))
      case "global_atomic_ordered_add_b64" => Some(GlobalAtomicOrderedAddB64(arguments))
      case "global_load_lds_dwordx3" => Some(GlobalLoadLdsDwordx3(arguments))
      case "global_load_tr_b64" => Some(GlobalLoadTrB64(arguments))
      case "global_store_dwordx4" => Some(GlobalStoreDwordx4(arguments))
      case "image_gather4_c_b" => Some(ImageGather4CB(arguments))
      case "image_gather4_c_b_cl" => Some(ImageGather4CBCl(arguments))
      case "image_gather4_c_b_o" => Some(ImageGather4CBO(arguments))
      case "s_cmp_ge_i32" => Some(SCmpGeI32(arguments))
      case "s_ff1_i32_b32" => Some(SFf1I32B32(arguments))
      case "s_load_i8" => Some(SLoadI8(arguments))
      case "s_orn1_saveexec_b64" => Some(SOrn1SaveexecB64(arguments))
      case "s_pack_hh_b32_b16" => Some(SPackHhB32B16(arguments))
      case "s_set_pc_i64" => Some(SSetPcI64(arguments))
      case "s_wait_event" => Some(SWaitEvent(arguments))
      case "v_cmp_class_f32" => Some(VCmpClassF32(arguments))
      case "v_cmp_le_u32" => Some(VCmpLeU32(arguments))
      case "v_cmpx_ge_f32" => Some(VCmpxGeF32(arguments))
      case "v_cmpx_tru_u32" => Some(VCmpxTruU32(arguments))
      case "v_cvt_scalef32_pk16_bf6_f16" => Some(VCvtScalef32Pk16Bf6F16(arguments))
      case "v_cvt_scalef32_pk32_f16_fp6" => Some(VCvtScalef32Pk32F16Fp6(arguments))
      case "v_dot4_f32_bf8_bf8" => Some(VDot4F32Bf8Bf8(arguments))
      case "v_dual_fmaak_f32" => Some(VDualFmaakF32(arguments))
      case "v_ffbh_u32" => Some(VFfbhU32(arguments))
      case "v_mad_i64_i32" => Some(VMadI64I32(arguments))
      case "v_max_num_f32" => Some(VMaxNumF32(arguments))
      case "v_mfma_f32_32x32x16_fp8_bf8" => Some(VMfmaF3232x32x16Fp8Bf8(arguments))
      case "v_mfma_f64_16x16x4f64" => Some(VMfmaF6416x16x4f64_2(arguments))
      case "v_movreld_b32" => Some(VMovreldB32(arguments))
      case "v_rsq_f64" => Some(VRsqF64(arguments))
      case "v_smfmac_f32_16x16x128_fp8_fp8" => Some(VSmfmacF3216x16x128Fp8Fp8(arguments))
      case "v_sub_nc_i16" => Some(VSubNcI16(arguments))
      case "v_swmmac_f32_16x16x64_f16" => Some(VSwmmacF3216x16x64F16(arguments))
      case "v_xnor_b32" => Some(VXnorB32(arguments))
      case _ => None
    }

  private def decode73(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_load_sbyte" => Some(BufferLoadSbyte(arguments))
      case "buffer_store_byte_d16_hi" => Some(BufferStoreByteD16Hi(arguments))
      case "ds_cmpstore_f32" => Some(DsCmpstoreF32(arguments))
      case "ds_or_b32" => Some(DsOrB32(arguments))
      case "flat_atomic_inc_x2" => Some(FlatAtomicIncX2(arguments))
      case "flat_atomic_umax" => Some(FlatAtomicUmax(arguments))
      case "flat_load_dwordx2" => Some(FlatLoadDwordx2(arguments))
      case "global_load_lds_dwordx4" => Some(GlobalLoadLdsDwordx4(arguments))
      case "image_atomic_cmpswap" => Some(ImageAtomicCmpswap(arguments))
      case "image_store" => Some(ImageStore(arguments))
      case "image_store_by2" => Some(ImageStoreBy2(arguments))
      case "s_buffer_load_i8" => Some(SBufferLoadI8(arguments))
      case "s_cbranch_cdbgsys" => Some(SCbranchCdbgsys(arguments))
      case "s_cbranch_cdbgsys_or_user" => Some(SCbranchCdbgsysOrUser(arguments))
      case "s_cvt_hi_f32_f16" => Some(SCvtHiF32F16(arguments))
      case "s_scratch_store_dwordx2" => Some(SScratchStoreDwordx2(arguments))
      case "scratch_load_d16_u8" => Some(ScratchLoadD16U8(arguments))
      case "scratch_load_ubyte_d16_hi" => Some(ScratchLoadUbyteD16Hi(arguments))
      case "v_add_nc_u32" => Some(VAddNcU32(arguments))
      case "v_cmp_ngt_f16" => Some(VCmpNgtF16(arguments))
      case "v_cmpx_le_f16" => Some(VCmpxLeF16(arguments))
      case "v_cvt_f16_bf8" => Some(VCvtF16Bf8(arguments))
      case "v_cvt_f32_fp8" => Some(VCvtF32Fp8(arguments))
      case "v_cvt_scalef32_pk_fp4_bf16" => Some(VCvtScalef32PkFp4Bf16(arguments))
      case "v_cvt_scalef32_sr_bf8_f16" => Some(VCvtScalef32SrBf8F16(arguments))
      case "v_cvt_scalef32_sr_fp8_bf16" => Some(VCvtScalef32SrFp8Bf16(arguments))
      case "v_cvt_scalef32_sr_pk8_bf8_bf16" => Some(VCvtScalef32SrPk8Bf8Bf16(arguments))
      case "v_cvt_sr_fp8_f16" => Some(VCvtSrFp8F16(arguments))
      case "v_max3_i32" => Some(VMax3I32(arguments))
      case "v_max_f32" => Some(VMaxF32(arguments))
      case "v_maximum_f32" => Some(VMaximumF32(arguments))
      case "v_mul_f32" => Some(VMulF32(arguments))
      case "v_pk_lshlrev_b16" => Some(VPkLshlrevB16(arguments))
      case "v_smfmac_i32_32x32x32_i8" => Some(VSmfmacI3232x32x32I8(arguments))
      case "v_subrev_nc_u32" => Some(VSubrevNcU32(arguments))
      case "v_trunc_f32" => Some(VTruncF32(arguments))
      case "v_wmma_f32_16x16x16_fp8_fp8" => Some(VWmmaF3216x16x16Fp8Fp8(arguments))
      case _ => None
    }

  private def decode74(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_atomic_inc_u32" => Some(BufferAtomicIncU32(arguments))
      case "buffer_store_b16" => Some(BufferStoreB16(arguments))
      case "ds_read_i8_d16" => Some(DsReadI8D16(arguments))
      case "flat_load_d16_b16" => Some(FlatLoadD16B16(arguments))
      case "flat_load_dwordx3" => Some(FlatLoadDwordx3(arguments))
      case "global_atomic_max_f32" => Some(GlobalAtomicMaxF32(arguments))
      case "global_load_tr8_b64" => Some(GlobalLoadTr8B64(arguments))
      case "image_load_mip_pck2" => Some(ImageLoadMipPck2(arguments))
      case "image_sample" => Some(ImageSample(arguments))
      case "image_sample_cd_o" => Some(ImageSampleCdO_2(arguments))
      case "image_sample_d_cl_g16" => Some(ImageSampleDClG16(arguments))
      case "image_sample_o" => Some(ImageSampleO(arguments))
      case "s_add_u32" => Some(SAddU32(arguments))
      case "s_cmp_lg_f32" => Some(SCmpLgF32(arguments))
      case "s_cmpk_lg_u32" => Some(SCmpkLgU32(arguments))
      case "s_fmaak_f32" => Some(SFmaakF32(arguments))
      case "s_memtime" => Some(SMemtime(arguments))
      case "v_add_max_i32" => Some(VAddMaxI32(arguments))
      case "v_alignbyte_b32" => Some(VAlignbyteB32(arguments))
      case "v_cmp_f_f32" => Some(VCmpFF32(arguments))
      case "v_cmp_ngt_f64" => Some(VCmpNgtF64(arguments))
      case "v_cmpx_le_f64" => Some(VCmpxLeF64(arguments))
      case "v_cmpx_nge_f32" => Some(VCmpxNgeF32(arguments))
      case "v_cmpx_t_f32" => Some(VCmpxTF32(arguments))
      case "v_cvt_scalef32_pk16_fp6_f16" => Some(VCvtScalef32Pk16Fp6F16(arguments))
      case "v_cvt_scalef32_pk32_bf16_fp6" => Some(VCvtScalef32Pk32Bf16Fp6(arguments))
      case "v_dot4_f32_fp8_bf8" => Some(VDot4F32Fp8Bf8(arguments))
      case "v_dot8_i32_iu4" => Some(VDot8I32Iu4(arguments))
      case "v_med3_f32" => Some(VMed3F32(arguments))
      case "v_rcp_bf16" => Some(VRcpBf16(arguments))
      case "v_rcp_iflag_f32" => Some(VRcpIflagF32(arguments))
      case "v_wmma_f32_32x16x128_f4" => Some(VWmmaF3232x16x128F4(arguments))
      case _ => None
    }

  private def decode75(name: String, arguments: InstructionArguments): Option[KnownInstruction] =
    name match {
      case "buffer_store_b64" => Some(BufferStoreB64(arguments))
      case "buffer_store_format_d16_x" => Some(BufferStoreFormatD16X(arguments))
      case "ds_read_b64" => Some(DsReadB64(arguments))
      case "flat_atomic_and_b32" => Some(FlatAtomicAndB32(arguments))
      case "flat_atomic_smax" => Some(FlatAtomicSmax(arguments))
      case "flat_load_dwordx4" => Some(FlatLoadDwordx4(arguments))
      case "image_sample_c_l_o" => Some(ImageSampleCLO_1(arguments))
      case "image_sample_cd_cl_o_g16" => Some(ImageSampleCdClOG16_2(arguments))
      case "image_store_by4" => Some(ImageStoreBy4(arguments))
      case "s_atomic_inc_x2" => Some(SAtomicIncX2(arguments))
      case "s_barrier_signal_isfirst" => Some(SBarrierSignalIsfirst(arguments))
      case "s_cbranch_execnz" => Some(SCbranchExecnz(arguments))
      case "s_cls_i32" => Some(SClsI32(arguments))
      case "s_scratch_store_dwordx4" => Some(SScratchStoreDwordx4(arguments))
      case "s_wakeup_barrier" => Some(SWakeupBarrier(arguments))
      case "s_xnor_b32" => Some(SXnorB32(arguments))
      case "scratch_load_u8" => Some(ScratchLoadU8(arguments))
      case "v_cmpx_nle_f16" => Some(VCmpxNleF16(arguments))
      case "v_cvt_scalef32_pk_bf8_f32" => Some(VCvtScalef32PkBf8F32(arguments))
      case "v_cvt_scalef32_pk_f16_fp8" => Some(VCvtScalef32PkF16Fp8(arguments))
      case "v_cvt_scalef32_sr_fp8_f16" => Some(VCvtScalef32SrFp8F16(arguments))
      case "v_dual_max_num_f32" => Some(VDualMaxNumF32(arguments))
      case "v_interp_p10_f16_f32" => Some(VInterpP10F16F32(arguments))
      case "v_mfma_f32_32x32x1_2b_f32" => Some(VMfmaF3232x32x12bF32(arguments))
      case "v_rsq_legacy_f32" => Some(VRsqLegacyF32(arguments))
      case "v_smfmac_f32_16x16x64_fp8_bf8" => Some(VSmfmacF3216x16x64Fp8Bf8(arguments))
      case "v_swmmac_bf16_16x16x32_bf16" => Some(VSwmmacBf1616x16x32Bf16(arguments))
      case "v_swmmac_f32_16x16x32_bf16" => Some(VSwmmacF3216x16x32Bf16(arguments))
      case _ => None
    }
}
