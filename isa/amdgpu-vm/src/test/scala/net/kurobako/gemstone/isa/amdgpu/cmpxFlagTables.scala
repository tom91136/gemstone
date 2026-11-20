package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*

object cmpxFlagTables {
  case class CmpxForm(text: String, code: Vec[Int], execs: String)

  val edge: Vec[Long] = Vec(
    0x0000000000000000L,
    0x8000000000000000L,
    0x0000000000000001L,
    0xffffffffffffffffL,
    0x3ff0000000000000L,
    0xbff0000000000000L,
    0x4000000000000000L,
    0x3fe0000000000000L,
    0x7ff0000000000000L,
    0xfff0000000000000L,
    0x7ff8000000000000L,
    0x7ff0000000000001L,
    0x000fffffffffffffL,
    0x7fffffffffffffffL,
    0x8000000000000001L,
    0x0000000000000003L,
    0x000000003f800000L,
    0x00000000bf800000L,
    0x000000007f800000L,
    0x00000000ff800000L,
    0x000000007fc00000L,
    0x0000000000800000L,
    0x000000007f7fffffL,
    0x00000000ffff0000L,
    0x000000000000ffffL,
    0x0000000080000000L,
    0x000000007fffffffL,
    0x00000000ffffffffL,
    0x0000000100000000L,
    0x00000001ffffffffL,
    0x0000000040000000L,
    0x00000000c0000000L
  )

  val masks: Vec[(Long, Long)] = Vec(
    (0xffffffffL, 0L),
    (0x0L, 0L),
    (0x1L, 1L),
    (0x80000000L, 5L),
    (0x55555555L, 9L),
    (0xaaaaaaaaL, 13L),
    (0xffffL, 17L),
    (0xf00f0ff0L, 23L)
  )

  val forms: Vec[CmpxForm] = Vec(
    CmpxForm(
      "v_cmpx_f_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x10, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x11, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 80000000 00000040 000a0a22 000000b2 800a0080"
    ),
    CmpxForm(
      "v_cmpx_eq_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x12, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "d36fcff7 00000000 00000001 00000000 10000005 02000000 00000100 00000e20"
    ),
    CmpxForm(
      "v_cmpx_le_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x13, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "d36fcff7 00000000 00000001 80000000 10000045 020a0a22 000001b2 800a0ea0"
    ),
    CmpxForm(
      "v_cmpx_gt_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x14, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 41410500 00208000 0000c805 50050100"
    ),
    CmpxForm(
      "v_cmpx_lg_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x15, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 80000000 41410540 002a8a22 0000c8b7 d00f0180"
    ),
    CmpxForm(
      "v_cmpx_ge_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x16, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "d36fcff7 00000000 00000001 00000000 51410505 02208000 0000c905 50050f20"
    ),
    CmpxForm(
      "v_cmpx_o_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x17, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "d36fcff7 00000000 00000001 80000000 51410545 022a8a22 0000c9b7 d00f0fa0"
    ),
    CmpxForm(
      "v_cmpx_u_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x18, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2c903008 00000000 00000000 00000000 04145010 a8802088 00003648 20000050"
    ),
    CmpxForm(
      "v_cmpx_nge_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x19, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2c903008 00000000 00000000 80000000 04145050 a88a2aaa 000036fa a00a00d0"
    ),
    CmpxForm(
      "v_cmpx_nlg_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x1a, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 00000000 14145015 aa802088 00003748 20000e70"
    ),
    CmpxForm(
      "v_cmpx_ngt_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x1b, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 14145055 aa8a2aaa 000037fa a00a0ef0"
    ),
    CmpxForm(
      "v_cmpx_nle_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x1c, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2c903008 00000000 00000000 00000000 45555510 a8a0a088 0000fe4d 70050150"
    ),
    CmpxForm(
      "v_cmpx_neq_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x1d, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2c903008 00000000 00000000 80000000 45555550 a8aaaaaa 0000feff f00f01d0"
    ),
    CmpxForm(
      "v_cmpx_nlt_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x1e, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 00000000 55555515 aaa0a088 0000ff4d 70050f70"
    ),
    CmpxForm(
      "v_cmpx_tru_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x1f, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_f_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x30, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x31, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 80000000 40554000 8a808220 00004227 00030220"
    ),
    CmpxForm(
      "v_cmpx_eq_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x32, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffd3f7 00000000 00000001 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_le_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x33, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffd3f7 00000000 00000001 80000000 40554000 8a808220 00004227 00030220"
    ),
    CmpxForm(
      "v_cmpx_gt_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x34, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 11001141 002a0082 000091d0 f00401d0"
    ),
    CmpxForm(
      "v_cmpx_lg_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x35, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 80000000 51555141 8aaa82a2 0000d3f7 f00703f0"
    ),
    CmpxForm(
      "v_cmpx_ge_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x36, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffd3f7 00000000 00000001 00000000 11001141 002a0082 000091d0 f00401d0"
    ),
    CmpxForm(
      "v_cmpx_o_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x37, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffd3f7 00000000 00000001 80000000 51555141 8aaa82a2 0000d3f7 f00703f0"
    ),
    CmpxForm(
      "v_cmpx_u_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x38, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00002c08 00000000 00000000 00000000 04000414 20002808 00002c08 00080c00"
    ),
    CmpxForm(
      "v_cmpx_nge_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x39, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00002c08 00000000 00000000 80000000 44554414 aa80aa28 00006e2f 000b0e20"
    ),
    CmpxForm(
      "v_cmpx_nlg_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x3a, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 00000000 04000414 20002808 00002c08 00080c00"
    ),
    CmpxForm(
      "v_cmpx_ngt_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x3b, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 44554414 aa80aa28 00006e2f 000b0e20"
    ),
    CmpxForm(
      "v_cmpx_nle_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x3c, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00002c08 00000000 00000000 00000000 15001555 202a288a 0000bdd8 f00c0dd0"
    ),
    CmpxForm(
      "v_cmpx_neq_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x3d, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00002c08 00000000 00000000 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_nlt_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x3e, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 00000000 15001555 202a288a 0000bdd8 f00c0dd0"
    ),
    CmpxForm(
      "v_cmpx_tru_f64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0x3f, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_f_i32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x90, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_i32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x91, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 80000000 00001040 aa8a2aaa 000022ba a00a0080"
    ),
    CmpxForm(
      "v_cmpx_eq_i32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x92, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 00000000 10000005 00000000 00001000 00000e20"
    ),
    CmpxForm(
      "v_cmpx_le_i32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x93, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 10001045 aa8a2aaa 000032ba a00a0ea0"
    ),
    CmpxForm(
      "v_cmpx_gt_i32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x94, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 45554510 00208000 0000cd45 50050150"
    ),
    CmpxForm(
      "v_cmpx_ne_i32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x95, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 80000000 45555550 aaaaaaaa 0000efff f00f01d0"
    ),
    CmpxForm(
      "v_cmpx_ge_i32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x96, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 00000000 55554515 00208000 0000dd45 50050f70"
    ),
    CmpxForm(
      "v_cmpx_t_i32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x97, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_class_f32_e64 v2, v6",
      Vec(0x7e, 0x00, 0x98, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2d403008 00000000 00000000 00000000 04140010 80002a00 000016a0 80000050"
    ),
    CmpxForm(
      "v_cmpx_lt_i16_e64 v2, v6",
      Vec(0x7e, 0x00, 0x99, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 01401040 2820000a 00002008 20000000"
    ),
    CmpxForm(
      "v_cmpx_eq_i16_e64 v2, v6",
      Vec(0x7e, 0x00, 0x9a, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 54010505 028a20a0 00001153 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_le_i16_e64 v2, v6",
      Vec(0x7e, 0x00, 0x9b, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55411545 2aaa20aa 0000315b 700f0fa0"
    ),
    CmpxForm(
      "v_cmpx_gt_i16_e64 v2, v6",
      Vec(0x7e, 0x00, 0x9c, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00144010 80008a00 0000cea4 80000050"
    ),
    CmpxForm(
      "v_cmpx_ne_i16_e64 v2, v6",
      Vec(0x7e, 0x00, 0x9d, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 01545050 a8208a0a 0000eeac a0000050"
    ),
    CmpxForm(
      "v_cmpx_ge_i16_e64 v2, v6",
      Vec(0x7e, 0x00, 0x9e, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 54154515 828aaaa0 0000dff7 d00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_class_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0x9f, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2d403008 00000000 00000000 00000000 04140010 80002a00 000016a0 80000050"
    ),
    CmpxForm(
      "v_cmpx_f_i64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xb0, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_i64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xb1, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 80000000 40554014 aa80822a 0000422f 000b0220"
    ),
    CmpxForm(
      "v_cmpx_eq_i64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xb2, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_le_i64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xb3, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000000 80000000 40554014 aa80822a 0000422f 000b0220"
    ),
    CmpxForm(
      "v_cmpx_gt_i64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xb4, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000001 00000000 15001541 002a2880 0000bdd0 f0040dd0"
    ),
    CmpxForm(
      "v_cmpx_ne_i64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xb5, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_ge_i64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xb6, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 00000000 15001541 002a2880 0000bdd0 f0040dd0"
    ),
    CmpxForm(
      "v_cmpx_t_i64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xb7, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_class_f64_e64 v[2:3], v6",
      Vec(0x7e, 0x00, 0xb8, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2d403808 00000000 00000000 00000000 04140010 80002a00 000016a0 80000850"
    ),
    CmpxForm(
      "v_cmpx_lt_u16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xb9, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00140050 80200a02 000006a0 80000050"
    ),
    CmpxForm(
      "v_cmpx_eq_u16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xba, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 54010505 028a20a0 00001153 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_le_u16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xbb, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 54150555 82aa2aa2 000017f3 d00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_gt_u16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xbc, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 01405000 28008008 0000e80c 20000000"
    ),
    CmpxForm(
      "v_cmpx_ne_u16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xbd, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 01545050 a8208a0a 0000eeac a0000050"
    ),
    CmpxForm(
      "v_cmpx_ge_u16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xbe, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55415505 2a8aa0a8 0000f95f 700f0fa0"
    ),
    CmpxForm(
      "v_cmpx_f_u32_e64 v2, v6",
      Vec(0x7e, 0x00, 0xd0, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_u32_e64 v2, v6",
      Vec(0x7e, 0x00, 0xd1, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 04554550 80000aa2 000047f7 100001d0"
    ),
    CmpxForm(
      "v_cmpx_eq_u32_e64 v2, v6",
      Vec(0x7e, 0x00, 0xd2, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 00000000 10000005 00000000 00001000 00000e20"
    ),
    CmpxForm(
      "v_cmpx_le_u32_e64 v2, v6",
      Vec(0x7e, 0x00, 0xd3, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 00000000 14554555 80000aa2 000057f7 10000ff0"
    ),
    CmpxForm(
      "v_cmpx_gt_u32_e64 v2, v6",
      Vec(0x7e, 0x00, 0xd4, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 80000000 41001000 2aaaa008 0000a808 e00f0000"
    ),
    CmpxForm(
      "v_cmpx_ne_u32_e64 v2, v6",
      Vec(0x7e, 0x00, 0xd5, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 80000000 45555550 aaaaaaaa 0000efff f00f01d0"
    ),
    CmpxForm(
      "v_cmpx_ge_u32_e64 v2, v6",
      Vec(0x7e, 0x00, 0xd6, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 51001005 2aaaa008 0000b808 e00f0e20"
    ),
    CmpxForm(
      "v_cmpx_t_u32_e64 v2, v6",
      Vec(0x7e, 0x00, 0xd7, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_f_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xd8, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xd9, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00000040 00200002 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_eq_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xda, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "d2bfcff7 00000000 00000001 80000000 50010505 028a00a0 00000153 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_le_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xdb, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "d2bfcff7 00000000 00000001 80000000 50010545 02aa00a2 00000153 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_gt_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xdc, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00004000 00008000 0000c804 00000000"
    ),
    CmpxForm(
      "v_cmpx_lg_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xdd, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00004040 00208002 0000c804 00000000"
    ),
    CmpxForm(
      "v_cmpx_ge_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xde, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "d2bfcff7 00000000 00000001 80000000 50014505 028a80a0 0000c957 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_o_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xdf, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "d2bfcff7 00000000 00000001 80000000 50014545 02aa80a2 0000c957 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_f_u64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xf0, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_u64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xf1, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000001 80000000 55550015 aa808002 00000005 000f0400"
    ),
    CmpxForm(
      "v_cmpx_eq_u64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xf2, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_le_u64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xf3, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55550015 aa808002 00000005 000f0400"
    ),
    CmpxForm(
      "v_cmpx_gt_u64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xf4, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000000 00000000 00005540 002a2aa8 0000fffa f0000bf0"
    ),
    CmpxForm(
      "v_cmpx_ne_u64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xf5, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "00000000 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_ge_u64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xf6, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000000 00000000 00005540 002a2aa8 0000fffa f0000bf0"
    ),
    CmpxForm(
      "v_cmpx_t_u64_e64 v[2:3], v[6:7]",
      Vec(0x7e, 0x00, 0xf7, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_u_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xf8, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2d403008 00000000 00000000 00000000 05541010 a8002a08 000036a8 a0000050"
    ),
    CmpxForm(
      "v_cmpx_nge_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xf9, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2d403008 00000000 00000000 00000000 05541050 a8202a0a 000036a8 a0000050"
    ),
    CmpxForm(
      "v_cmpx_nlg_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xfa, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55551515 aa8a2aa8 000037fb f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_ngt_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xfb, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55551555 aaaa2aaa 000037fb f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_nle_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xfc, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2d403008 00000000 00000000 00000000 05545010 a800aa08 0000feac a0000050"
    ),
    CmpxForm(
      "v_cmpx_neq_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xfd, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "2d403008 00000000 00000000 00000000 05545050 a820aa0a 0000feac a0000050"
    ),
    CmpxForm(
      "v_cmpx_nlt_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xfe, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55555515 aa8aaaa8 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_tru_f16_e64 v2, v6",
      Vec(0x7e, 0x00, 0xff, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_f_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x20, 0x7c),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x22, 0x7c),
      "00000000 00000000 00000000 80000000 00000040 000a0a22 000000b2 800a0080"
    ),
    CmpxForm(
      "v_cmpx_eq_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x24, 0x7c),
      "d36fcff7 00000000 00000001 00000000 10000005 02000000 00000100 00000e20"
    ),
    CmpxForm(
      "v_cmpx_le_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x26, 0x7c),
      "d36fcff7 00000000 00000001 80000000 10000045 020a0a22 000001b2 800a0ea0"
    ),
    CmpxForm(
      "v_cmpx_gt_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x28, 0x7c),
      "00000000 00000000 00000000 00000000 41410500 00208000 0000c805 50050100"
    ),
    CmpxForm(
      "v_cmpx_lg_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x2a, 0x7c),
      "00000000 00000000 00000000 80000000 41410540 002a8a22 0000c8b7 d00f0180"
    ),
    CmpxForm(
      "v_cmpx_ge_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x2c, 0x7c),
      "d36fcff7 00000000 00000001 00000000 51410505 02208000 0000c905 50050f20"
    ),
    CmpxForm(
      "v_cmpx_o_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x2e, 0x7c),
      "d36fcff7 00000000 00000001 80000000 51410545 022a8a22 0000c9b7 d00f0fa0"
    ),
    CmpxForm(
      "v_cmpx_u_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x30, 0x7c),
      "2c903008 00000000 00000000 00000000 04145010 a8802088 00003648 20000050"
    ),
    CmpxForm(
      "v_cmpx_nge_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x32, 0x7c),
      "2c903008 00000000 00000000 80000000 04145050 a88a2aaa 000036fa a00a00d0"
    ),
    CmpxForm(
      "v_cmpx_nlg_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x34, 0x7c),
      "ffffffff 00000000 00000001 00000000 14145015 aa802088 00003748 20000e70"
    ),
    CmpxForm(
      "v_cmpx_ngt_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x36, 0x7c),
      "ffffffff 00000000 00000001 80000000 14145055 aa8a2aaa 000037fa a00a0ef0"
    ),
    CmpxForm(
      "v_cmpx_nle_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x38, 0x7c),
      "2c903008 00000000 00000000 00000000 45555510 a8a0a088 0000fe4d 70050150"
    ),
    CmpxForm(
      "v_cmpx_neq_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x3a, 0x7c),
      "2c903008 00000000 00000000 80000000 45555550 a8aaaaaa 0000feff f00f01d0"
    ),
    CmpxForm(
      "v_cmpx_nlt_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x3c, 0x7c),
      "ffffffff 00000000 00000001 00000000 55555515 aaa0a088 0000ff4d 70050f70"
    ),
    CmpxForm(
      "v_cmpx_tru_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x3e, 0x7c),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_f_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x60, 0x7c),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x62, 0x7c),
      "00000000 00000000 00000000 80000000 40554000 8a808220 00004227 00030220"
    ),
    CmpxForm(
      "v_cmpx_eq_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x64, 0x7c),
      "ffffd3f7 00000000 00000001 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_le_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x66, 0x7c),
      "ffffd3f7 00000000 00000001 80000000 40554000 8a808220 00004227 00030220"
    ),
    CmpxForm(
      "v_cmpx_gt_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x68, 0x7c),
      "00000000 00000000 00000000 00000000 11001141 002a0082 000091d0 f00401d0"
    ),
    CmpxForm(
      "v_cmpx_lg_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x6a, 0x7c),
      "00000000 00000000 00000000 80000000 51555141 8aaa82a2 0000d3f7 f00703f0"
    ),
    CmpxForm(
      "v_cmpx_ge_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x6c, 0x7c),
      "ffffd3f7 00000000 00000001 00000000 11001141 002a0082 000091d0 f00401d0"
    ),
    CmpxForm(
      "v_cmpx_o_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x6e, 0x7c),
      "ffffd3f7 00000000 00000001 80000000 51555141 8aaa82a2 0000d3f7 f00703f0"
    ),
    CmpxForm(
      "v_cmpx_u_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x70, 0x7c),
      "00002c08 00000000 00000000 00000000 04000414 20002808 00002c08 00080c00"
    ),
    CmpxForm(
      "v_cmpx_nge_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x72, 0x7c),
      "00002c08 00000000 00000000 80000000 44554414 aa80aa28 00006e2f 000b0e20"
    ),
    CmpxForm(
      "v_cmpx_nlg_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x74, 0x7c),
      "ffffffff 00000000 00000001 00000000 04000414 20002808 00002c08 00080c00"
    ),
    CmpxForm(
      "v_cmpx_ngt_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x76, 0x7c),
      "ffffffff 00000000 00000001 80000000 44554414 aa80aa28 00006e2f 000b0e20"
    ),
    CmpxForm(
      "v_cmpx_nle_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x78, 0x7c),
      "00002c08 00000000 00000000 00000000 15001555 202a288a 0000bdd8 f00c0dd0"
    ),
    CmpxForm(
      "v_cmpx_neq_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x7a, 0x7c),
      "00002c08 00000000 00000000 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_nlt_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x7c, 0x7c),
      "ffffffff 00000000 00000001 00000000 15001555 202a288a 0000bdd8 f00c0dd0"
    ),
    CmpxForm(
      "v_cmpx_tru_f64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x7e, 0x7c),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_f_i32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x20, 0x7d),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_i32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x22, 0x7d),
      "00000000 00000000 00000000 80000000 00001040 aa8a2aaa 000022ba a00a0080"
    ),
    CmpxForm(
      "v_cmpx_eq_i32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x24, 0x7d),
      "ffffffff 00000000 00000001 00000000 10000005 00000000 00001000 00000e20"
    ),
    CmpxForm(
      "v_cmpx_le_i32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x26, 0x7d),
      "ffffffff 00000000 00000001 80000000 10001045 aa8a2aaa 000032ba a00a0ea0"
    ),
    CmpxForm(
      "v_cmpx_gt_i32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x28, 0x7d),
      "00000000 00000000 00000000 00000000 45554510 00208000 0000cd45 50050150"
    ),
    CmpxForm(
      "v_cmpx_ne_i32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x2a, 0x7d),
      "00000000 00000000 00000000 80000000 45555550 aaaaaaaa 0000efff f00f01d0"
    ),
    CmpxForm(
      "v_cmpx_ge_i32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x2c, 0x7d),
      "ffffffff 00000000 00000001 00000000 55554515 00208000 0000dd45 50050f70"
    ),
    CmpxForm(
      "v_cmpx_t_i32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x2e, 0x7d),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_class_f32_e32 v2, v6",
      Vec(0x02, 0x0d, 0x30, 0x7d),
      "2d403008 00000000 00000000 00000000 04140010 80002a00 000016a0 80000050"
    ),
    CmpxForm(
      "v_cmpx_lt_i16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x32, 0x7d),
      "00000000 00000000 00000000 00000000 01401040 2820000a 00002008 20000000"
    ),
    CmpxForm(
      "v_cmpx_eq_i16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x34, 0x7d),
      "ffffffff 00000000 00000001 80000000 54010505 028a20a0 00001153 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_le_i16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x36, 0x7d),
      "ffffffff 00000000 00000001 80000000 55411545 2aaa20aa 0000315b 700f0fa0"
    ),
    CmpxForm(
      "v_cmpx_gt_i16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x38, 0x7d),
      "00000000 00000000 00000000 00000000 00144010 80008a00 0000cea4 80000050"
    ),
    CmpxForm(
      "v_cmpx_ne_i16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x3a, 0x7d),
      "00000000 00000000 00000000 00000000 01545050 a8208a0a 0000eeac a0000050"
    ),
    CmpxForm(
      "v_cmpx_ge_i16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x3c, 0x7d),
      "ffffffff 00000000 00000001 80000000 54154515 828aaaa0 0000dff7 d00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_class_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x3e, 0x7d),
      "2d403008 00000000 00000000 00000000 04140010 80002a00 000016a0 80000050"
    ),
    CmpxForm(
      "v_cmpx_f_i64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x60, 0x7d),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_i64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x62, 0x7d),
      "00000000 00000000 00000000 80000000 40554014 aa80822a 0000422f 000b0220"
    ),
    CmpxForm(
      "v_cmpx_eq_i64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x64, 0x7d),
      "ffffffff 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_le_i64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x66, 0x7d),
      "ffffffff 00000000 00000000 80000000 40554014 aa80822a 0000422f 000b0220"
    ),
    CmpxForm(
      "v_cmpx_gt_i64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x68, 0x7d),
      "00000000 00000000 00000001 00000000 15001541 002a2880 0000bdd0 f0040dd0"
    ),
    CmpxForm(
      "v_cmpx_ne_i64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x6a, 0x7d),
      "00000000 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_ge_i64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x6c, 0x7d),
      "ffffffff 00000000 00000001 00000000 15001541 002a2880 0000bdd0 f0040dd0"
    ),
    CmpxForm(
      "v_cmpx_t_i64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x6e, 0x7d),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_class_f64_e32 v[2:3], v6",
      Vec(0x02, 0x0d, 0x70, 0x7d),
      "2d403808 00000000 00000000 00000000 04140010 80002a00 000016a0 80000850"
    ),
    CmpxForm(
      "v_cmpx_lt_u16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x72, 0x7d),
      "00000000 00000000 00000000 00000000 00140050 80200a02 000006a0 80000050"
    ),
    CmpxForm(
      "v_cmpx_eq_u16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x74, 0x7d),
      "ffffffff 00000000 00000001 80000000 54010505 028a20a0 00001153 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_le_u16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x76, 0x7d),
      "ffffffff 00000000 00000001 80000000 54150555 82aa2aa2 000017f3 d00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_gt_u16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x78, 0x7d),
      "00000000 00000000 00000000 00000000 01405000 28008008 0000e80c 20000000"
    ),
    CmpxForm(
      "v_cmpx_ne_u16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x7a, 0x7d),
      "00000000 00000000 00000000 00000000 01545050 a8208a0a 0000eeac a0000050"
    ),
    CmpxForm(
      "v_cmpx_ge_u16_e32 v2, v6",
      Vec(0x02, 0x0d, 0x7c, 0x7d),
      "ffffffff 00000000 00000001 80000000 55415505 2a8aa0a8 0000f95f 700f0fa0"
    ),
    CmpxForm(
      "v_cmpx_f_u32_e32 v2, v6",
      Vec(0x02, 0x0d, 0xa0, 0x7d),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_u32_e32 v2, v6",
      Vec(0x02, 0x0d, 0xa2, 0x7d),
      "00000000 00000000 00000000 00000000 04554550 80000aa2 000047f7 100001d0"
    ),
    CmpxForm(
      "v_cmpx_eq_u32_e32 v2, v6",
      Vec(0x02, 0x0d, 0xa4, 0x7d),
      "ffffffff 00000000 00000001 00000000 10000005 00000000 00001000 00000e20"
    ),
    CmpxForm(
      "v_cmpx_le_u32_e32 v2, v6",
      Vec(0x02, 0x0d, 0xa6, 0x7d),
      "ffffffff 00000000 00000001 00000000 14554555 80000aa2 000057f7 10000ff0"
    ),
    CmpxForm(
      "v_cmpx_gt_u32_e32 v2, v6",
      Vec(0x02, 0x0d, 0xa8, 0x7d),
      "00000000 00000000 00000000 80000000 41001000 2aaaa008 0000a808 e00f0000"
    ),
    CmpxForm(
      "v_cmpx_ne_u32_e32 v2, v6",
      Vec(0x02, 0x0d, 0xaa, 0x7d),
      "00000000 00000000 00000000 80000000 45555550 aaaaaaaa 0000efff f00f01d0"
    ),
    CmpxForm(
      "v_cmpx_ge_u32_e32 v2, v6",
      Vec(0x02, 0x0d, 0xac, 0x7d),
      "ffffffff 00000000 00000001 80000000 51001005 2aaaa008 0000b808 e00f0e20"
    ),
    CmpxForm(
      "v_cmpx_t_u32_e32 v2, v6",
      Vec(0x02, 0x0d, 0xae, 0x7d),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_f_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xb0, 0x7d),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xb2, 0x7d),
      "00000000 00000000 00000000 00000000 00000040 00200002 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_eq_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xb4, 0x7d),
      "d2bfcff7 00000000 00000001 80000000 50010505 028a00a0 00000153 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_le_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xb6, 0x7d),
      "d2bfcff7 00000000 00000001 80000000 50010545 02aa00a2 00000153 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_gt_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xb8, 0x7d),
      "00000000 00000000 00000000 00000000 00004000 00008000 0000c804 00000000"
    ),
    CmpxForm(
      "v_cmpx_lg_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xba, 0x7d),
      "00000000 00000000 00000000 00000000 00004040 00208002 0000c804 00000000"
    ),
    CmpxForm(
      "v_cmpx_ge_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xbc, 0x7d),
      "d2bfcff7 00000000 00000001 80000000 50014505 028a80a0 0000c957 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_o_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xbe, 0x7d),
      "d2bfcff7 00000000 00000001 80000000 50014545 02aa80a2 0000c957 500f0fa0"
    ),
    CmpxForm(
      "v_cmpx_f_u64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xe0, 0x7d),
      "00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_lt_u64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xe2, 0x7d),
      "00000000 00000000 00000001 80000000 55550015 aa808002 00000005 000f0400"
    ),
    CmpxForm(
      "v_cmpx_eq_u64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xe4, 0x7d),
      "ffffffff 00000000 00000000 00000000 00000000 00000000 00000000 00000000"
    ),
    CmpxForm(
      "v_cmpx_le_u64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xe6, 0x7d),
      "ffffffff 00000000 00000001 80000000 55550015 aa808002 00000005 000f0400"
    ),
    CmpxForm(
      "v_cmpx_gt_u64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xe8, 0x7d),
      "00000000 00000000 00000000 00000000 00005540 002a2aa8 0000fffa f0000bf0"
    ),
    CmpxForm(
      "v_cmpx_ne_u64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xea, 0x7d),
      "00000000 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_ge_u64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xec, 0x7d),
      "ffffffff 00000000 00000000 00000000 00005540 002a2aa8 0000fffa f0000bf0"
    ),
    CmpxForm(
      "v_cmpx_t_u64_e32 v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xee, 0x7d),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_u_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xf0, 0x7d),
      "2d403008 00000000 00000000 00000000 05541010 a8002a08 000036a8 a0000050"
    ),
    CmpxForm(
      "v_cmpx_nge_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xf2, 0x7d),
      "2d403008 00000000 00000000 00000000 05541050 a8202a0a 000036a8 a0000050"
    ),
    CmpxForm(
      "v_cmpx_nlg_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xf4, 0x7d),
      "ffffffff 00000000 00000001 80000000 55551515 aa8a2aa8 000037fb f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_ngt_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xf6, 0x7d),
      "ffffffff 00000000 00000001 80000000 55551555 aaaa2aaa 000037fb f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_nle_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xf8, 0x7d),
      "2d403008 00000000 00000000 00000000 05545010 a800aa08 0000feac a0000050"
    ),
    CmpxForm(
      "v_cmpx_neq_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xfa, 0x7d),
      "2d403008 00000000 00000000 00000000 05545050 a820aa0a 0000feac a0000050"
    ),
    CmpxForm(
      "v_cmpx_nlt_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xfc, 0x7d),
      "ffffffff 00000000 00000001 80000000 55555515 aa8aaaa8 0000ffff f00f0ff0"
    ),
    CmpxForm(
      "v_cmpx_tru_f16_e32 v2, v6",
      Vec(0x02, 0x0d, 0xfe, 0x7d),
      "ffffffff 00000000 00000001 80000000 55555555 aaaaaaaa 0000ffff f00f0ff0"
    )
  )
}
