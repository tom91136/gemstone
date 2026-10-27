// Generated encoding constants from pinned AMD and LLVM inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
// mirrors LLVM's full tables so the copy stays easy to resync
@SuppressWarnings(Array("baumkuchen:unused-member"))
private object EncodingConstants {
  object Source {
    val SGPR_MIN: Int = 0
    val SGPR_MAX_SI: Int = 101
    val SGPR_MAX_GFX10: Int = 105
    val TTMP_VI_MIN: Int = 112
    val TTMP_VI_MAX: Int = 123
    val TTMP_GFX9PLUS_MIN: Int = 108
    val TTMP_GFX9PLUS_MAX: Int = 123
    val INLINE_INTEGER_C_MIN: Int = 128
    val INLINE_INTEGER_C_POSITIVE_MAX: Int = 192
    val INLINE_INTEGER_C_MAX: Int = 208
    val INLINE_FLOATING_C_MIN: Int = 240
    val INLINE_FLOATING_C_MAX: Int = 248
    val LITERAL64_CONST: Int = 254
    val LITERAL_CONST: Int = 255
    val VGPR_MIN: Int = 256
    val VGPR_MAX: Int = 511
    val IS_VGPR: Int = 256
  }
  object DPP {
    val QUAD_PERM_FIRST: Int = 0
    val QUAD_PERM_ID: Int = 228
    val QUAD_PERM_LAST: Int = 255
    val DPP_UNUSED1: Int = 256
    val ROW_SHL0: Int = 256
    val ROW_SHL_FIRST: Int = 257
    val ROW_SHL_LAST: Int = 271
    val DPP_UNUSED2: Int = 272
    val ROW_SHR0: Int = 272
    val ROW_SHR_FIRST: Int = 273
    val ROW_SHR_LAST: Int = 287
    val DPP_UNUSED3: Int = 288
    val ROW_ROR0: Int = 288
    val ROW_ROR_FIRST: Int = 289
    val ROW_ROR_LAST: Int = 303
    val WAVE_SHL1: Int = 304
    val DPP_UNUSED4_FIRST: Int = 305
    val DPP_UNUSED4_LAST: Int = 307
    val WAVE_ROL1: Int = 308
    val DPP_UNUSED5_FIRST: Int = 309
    val DPP_UNUSED5_LAST: Int = 311
    val WAVE_SHR1: Int = 312
    val DPP_UNUSED6_FIRST: Int = 313
    val DPP_UNUSED6_LAST: Int = 315
    val WAVE_ROR1: Int = 316
    val DPP_UNUSED7_FIRST: Int = 317
    val DPP_UNUSED7_LAST: Int = 319
    val ROW_MIRROR: Int = 320
    val ROW_HALF_MIRROR: Int = 321
    val BCAST15: Int = 322
    val BCAST31: Int = 323
    val DPP_UNUSED8_FIRST: Int = 324
    val DPP_UNUSED8_LAST: Int = 335
    val ROW_NEWBCAST_FIRST: Int = 336
    val ROW_NEWBCAST_LAST: Int = 351
    val ROW_SHARE0: Int = 336
    val ROW_SHARE_FIRST: Int = 336
    val ROW_SHARE_LAST: Int = 351
    val ROW_XMASK0: Int = 352
    val ROW_XMASK_FIRST: Int = 352
    val ROW_XMASK_LAST: Int = 367
    val DPP_LAST: Int = 367
  }
  object DPP8 {
    val DPP_FI_0: Int = 0
    val DPP_FI_1: Int = 1
    val DPP8_FI_0: Int = 233
    val DPP8_FI_1: Int = 234
  }
  object SDWA {
    val BYTE_0: Int = 0
    val BYTE_1: Int = 1
    val BYTE_2: Int = 2
    val BYTE_3: Int = 3
    val WORD_0: Int = 4
    val WORD_1: Int = 5
    val DWORD: Int = 6
  }
  object SDWAUnused {
    val UNUSED_PAD: Int = 0
    val UNUSED_SEXT: Int = 1
    val UNUSED_PRESERVE: Int = 2
  }
  object Swizzle {
    val QUAD_PERM_ENC: Int = 32768
    val QUAD_PERM_ENC_MASK: Int = 65280
    val BITMASK_PERM_ENC: Int = 0
    val BITMASK_PERM_ENC_MASK: Int = 32768
    val FFT_MODE_ENC: Int = 57344
    val ROTATE_MODE_ENC: Int = 49152
    val FFT_ROTATE_MODE_MASK: Int = 61440
    val ROTATE_MODE_LO: Int = 49152
    val FFT_MODE_LO: Int = 57344
    val LANE_MASK: Int = 3
    val LANE_MAX: Int = 3
    val LANE_SHIFT: Int = 2
    val LANE_NUM: Int = 4
    val BITMASK_MASK: Int = 31
    val BITMASK_MAX: Int = 31
    val BITMASK_WIDTH: Int = 5
    val BITMASK_AND_SHIFT: Int = 0
    val BITMASK_OR_SHIFT: Int = 5
    val BITMASK_XOR_SHIFT: Int = 10
    val FFT_SWIZZLE_MASK: Int = 31
    val FFT_SWIZZLE_MAX: Int = 31
    val ROTATE_MAX_SIZE: Int = 31
    val ROTATE_DIR_SHIFT: Int = 10
    val ROTATE_DIR_MASK: Int = 1
    val ROTATE_SIZE_SHIFT: Int = 5
    val ROTATE_SIZE_MASK: Int = 31
  }
  object OutputModifiers {
    val NONE: Int = 0
    val MUL2: Int = 1
    val MUL4: Int = 2
    val DIV2: Int = 3
  }
  val SdwaSelectors: Vector[NonEmpty[String]] =
    Vector("BYTE_0", "BYTE_1", "BYTE_2", "BYTE_3", "WORD_0", "WORD_1", "DWORD")
  val SdwaUnused: Vector[NonEmpty[String]] = Vector("UNUSED_PAD", "UNUSED_SEXT", "UNUSED_PRESERVE")
  val TemporalHints: Map[String, Int] = Map(
    "BYPASS" -> 3,
    "HT" -> 2,
    "LU" -> 3,
    "NT" -> 1,
    "NT_HT" -> 6,
    "NT_RT" -> 4,
    "NT_WB" -> 7,
    "RT" -> 0,
    "RT_NT" -> 5,
    "WB" -> 3
  )
  val MemoryScopes: Map[String, Int] = Map("CU" -> 0, "DEV" -> 2, "SE" -> 1, "SYS" -> 3)
  val AtomicHints: Map[String, Int] =
    Map("CASCADE" -> 4, "CASCADE_RETURN" -> 5, "NT" -> 2, "NT_RETURN" -> 3, "RETURN" -> 1, "RT" -> 0)
  object Fixed {
    inline val VccLo = 106
    inline val VccHi = 107
    inline val M0 = 124
    inline val NullSgpr = 125
    inline val ExecLo = 126
    inline val ExecHi = 127
    inline val SccSource = 253
    inline val LdsDirectSource = 254
    inline val DppSource = 250
    inline val SdwaSource = 249
    val InlineFloatingValues: Vector[Double] = Vector(0.5, -0.5, 1.0, -1.0, 2.0, -2.0, 4.0, -4.0, 0.15915494)
    val InlineFloatingConstants: Vector[Long] = Vector(1056964608L, 3204448256L, 1065353216L, 3212836864L, 1073741824L,
      3221225472L, 1082130432L, 3229614080L, 1042479491L)
  }
  val R_AMDGPU_NONE: Int = 0
  val R_AMDGPU_ABS32_LO: Int = 1
  val R_AMDGPU_ABS32_HI: Int = 2
  val R_AMDGPU_ABS64: Int = 3
  val R_AMDGPU_REL32: Int = 4
  val R_AMDGPU_REL64: Int = 5
  val R_AMDGPU_ABS32: Int = 6
  val R_AMDGPU_GOTPCREL: Int = 7
  val R_AMDGPU_GOTPCREL32_LO: Int = 8
  val R_AMDGPU_GOTPCREL32_HI: Int = 9
  val R_AMDGPU_REL32_LO: Int = 10
  val R_AMDGPU_REL32_HI: Int = 11
  val R_AMDGPU_RELATIVE64: Int = 13
  val R_AMDGPU_REL16: Int = 14
  object ELFMachine {
    val Mask: Int = 0x0ff
    val Names: Map[Int, String] = Map(
      0x20 -> "gfx600",
      0x21 -> "gfx601",
      0x22 -> "gfx700",
      0x23 -> "gfx701",
      0x24 -> "gfx702",
      0x25 -> "gfx703",
      0x26 -> "gfx704",
      0x28 -> "gfx801",
      0x29 -> "gfx802",
      0x2a -> "gfx803",
      0x2b -> "gfx810",
      0x2c -> "gfx900",
      0x2d -> "gfx902",
      0x2e -> "gfx904",
      0x2f -> "gfx906",
      0x30 -> "gfx908",
      0x31 -> "gfx909",
      0x32 -> "gfx90c",
      0x33 -> "gfx1010",
      0x34 -> "gfx1011",
      0x35 -> "gfx1012",
      0x36 -> "gfx1030",
      0x37 -> "gfx1031",
      0x38 -> "gfx1032",
      0x39 -> "gfx1033",
      0x3a -> "gfx602",
      0x3b -> "gfx705",
      0x3c -> "gfx805",
      0x3d -> "gfx1035",
      0x3e -> "gfx1034",
      0x3f -> "gfx90a",
      0x41 -> "gfx1100",
      0x42 -> "gfx1013",
      0x43 -> "gfx1150",
      0x44 -> "gfx1103",
      0x45 -> "gfx1036",
      0x46 -> "gfx1101",
      0x47 -> "gfx1102",
      0x48 -> "gfx1200",
      0x49 -> "gfx1250",
      0x4a -> "gfx1151",
      0x4c -> "gfx942",
      0x4e -> "gfx1201",
      0x4f -> "gfx950",
      0x50 -> "gfx1310",
      0x51 -> "gfx9-generic",
      0x52 -> "gfx10-1-generic",
      0x53 -> "gfx10-3-generic",
      0x54 -> "gfx11-generic",
      0x55 -> "gfx1152",
      0x57 -> "gfx1154",
      0x58 -> "gfx1153",
      0x59 -> "gfx12-generic",
      0x5a -> "gfx1251",
      0x5b -> "gfx12-5-generic",
      0x5c -> "gfx1172",
      0x5d -> "gfx1170",
      0x5e -> "gfx1171",
      0x5f -> "gfx9-4-generic",
      0x62 -> "gfx11-7-generic",
      0x63 -> "gfx13-generic"
    )
  }
  object KernelCodeProperty {
    val ENABLE_SGPR_PRIVATE_SEGMENT_BUFFER: Int = 0x1
    val ENABLE_SGPR_DISPATCH_PTR: Int = 0x2
    val ENABLE_SGPR_QUEUE_PTR: Int = 0x4
    val ENABLE_SGPR_KERNARG_SEGMENT_PTR: Int = 0x8
    val ENABLE_SGPR_DISPATCH_ID: Int = 0x10
    val ENABLE_SGPR_FLAT_SCRATCH_INIT: Int = 0x20
    val ENABLE_SGPR_PRIVATE_SEGMENT_SIZE: Int = 0x40
    val ENABLE_WAVEFRONT_SIZE32: Int = 0x400
    val USES_DYNAMIC_STACK: Int = 0x800
  }
}
