// Generated encoding constants from pinned AMD and LLVM inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

@javax.annotation.processing.Generated(Array("sbt codegen"))
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
  object CachePolicy {
    val GLC: Int = 1
    val SLC: Int = 2
    val DLC: Int = 4
    val SCC: Int = 16
    val SC0: Int = 1
    val SC1: Int = 16
    val NT: Int = 2
    val ALL_pregfx12: Int = 23
    val SWZ_pregfx12: Int = 8
    val TH: Int = 7
    val TH_RT: Int = 0
    val TH_NT: Int = 1
    val TH_HT: Int = 2
    val TH_LU: Int = 3
    val TH_WB: Int = 3
    val TH_NT_RT: Int = 4
    val TH_RT_NT: Int = 5
    val TH_NT_HT: Int = 6
    val TH_NT_WB: Int = 7
    val TH_BYPASS: Int = 3
    val TH_RESERVED: Int = 7
    val TH_ATOMIC_RETURN: Int = 1
    val TH_ATOMIC_NT: Int = 2
    val TH_ATOMIC_CASCADE: Int = 4
    val SCOPE_SHIFT: Int = 3
    val SCOPE_MASK: Int = 3
    val SCOPE: Int = 24
    val SCOPE_CU: Int = 0
    val SCOPE_SE: Int = 8
    val SCOPE_DEV: Int = 16
    val SCOPE_SYS: Int = 24
    val NV: Int = 32
    val SWZ: Int = 64
    val SCAL: Int = 2048
    val ALL: Int = 63
    val TH_TYPE_LOAD: Int = 128
    val TH_TYPE_STORE: Int = 256
    val TH_TYPE_ATOMIC: Int = 512
    val TH_REAL_BYPASS: Int = 1024
    val VOLATILE: Int = -2147483648
    val VIRTUAL_BITS: Int = -2147483648
  }
  val SdwaSelectors: Vector[String] = Vector("BYTE_0", "BYTE_1", "BYTE_2", "BYTE_3", "WORD_0", "WORD_1", "DWORD")
  val SdwaUnused: Vector[String] = Vector("UNUSED_PAD", "UNUSED_SEXT", "UNUSED_PRESERVE")
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
    val VccLo: Int = 106
    val VccHi: Int = 107
    val M0: Int = 124
    val NullSgpr: Int = 125
    val ExecLo: Int = 126
    val ExecHi: Int = 127
    val VccZeroSource: Int = 251
    val ExecZeroSource: Int = 252
    val SccSource: Int = 253
    val LdsDirectSource: Int = 254
    val SmemGfx6Prefix: Long = 48L
    val SmemGfx10Prefix: Long = 61L
    val Sop1Prefix: Long = 381L
    val SopCPrefix: Long = 382L
    val SopPPrefix: Long = 383L
    val SopKPrefix: Long = 11L
    val Sop2Prefix: Long = 2L
    val Vop3PPrefix: Long = 51L
    val Vop3Prefix: Long = 52L
    val Vop3LiteralPrefix: Long = 53L
    val DsPrefix: Long = 54L
    val GlobalFlatPrefix: Long = 55L
    val MubufPrefix: Long = 56L
    val Vop1Prefix: Long = 63L
    val VopCPrefix: Long = 62L
    val DppSource: Int = 250
    val SdwaSource: Int = 249
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
}
