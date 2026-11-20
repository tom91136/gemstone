package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*

object hardwareFlagTables {
  case class FlagForm(text: String, code: Vec[Int], rows: String)

  val prologue: Vec[Int] = Vec(
    0x7e, 0x04, 0x92, 0xbe, 0x81, 0x03, 0xfe, 0xbe, 0x80, 0x03, 0xff, 0xbe, 0x06, 0x80, 0x07, 0xbf, 0x06, 0x03, 0xea,
    0xbe, 0x06, 0x03, 0x90, 0xbe, 0x05, 0x03, 0x8a, 0xbe, 0x04, 0x03, 0x8b, 0xbe, 0x03, 0x03, 0x0c, 0x7e, 0x02, 0x03,
    0x0e, 0x7e, 0xff, 0x03, 0x88, 0xbe, 0xef, 0xbe, 0xad, 0xde, 0xff, 0x03, 0x89, 0xbe, 0xef, 0xbe, 0xad, 0xde, 0xff,
    0x03, 0x8e, 0xbe, 0xef, 0xbe, 0xad, 0xde, 0xff, 0x02, 0x10, 0x7e, 0xef, 0xbe, 0xad, 0xde, 0xff, 0x02, 0x12, 0x7e,
    0xef, 0xbe, 0xad, 0xde
  )
  val epilogue: Vec[Int] = Vec(0x81, 0x80, 0x14, 0x85, 0x6a, 0x03, 0x95, 0xbe, 0x12, 0x04, 0xfe, 0xbe)

  val forms: Vec[FlagForm] = Vec(
    FlagForm(
      "s_not_b32 s8, s4",
      Vec(0x04, 0x07, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 fffffffa deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 fffffffa deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 80000000 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 7fffffff deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_not_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x08, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 ffffffff fffffffe deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 fffffffe ffffffff deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 fffffffa fffffffa deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 fffffffa fffffffa deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 0 fffffffe deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 80000000 7fffffff deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 7fffffff fffffffe deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 7fffffff 80000000 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 0 7fffffff deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_wqm_b32 s8, s4",
      Vec(0x04, 0x09, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 f deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 f deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 f deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 f0000000 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 f0000000 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_wqm_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x0a, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 0 f deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 f 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 f f deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 f f deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff f deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff f0000000 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 f0000000 f deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 f0000000 ffffffff deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff f0000000 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bcnt0_i32_b32 s8, s4",
      Vec(0x04, 0x0d, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 20 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 20 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1f deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1e deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1e deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1f deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 20 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1f deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bcnt0_i32_b64 s8, s[4:5]",
      Vec(0x04, 0x0e, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 40 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 3f deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 3f deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 3c deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 3c deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1f deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 20 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 3e deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 40 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 20 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1f deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bcnt1_i32_b32 s8, s4",
      Vec(0x04, 0x0f, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 2 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 2 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 20 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 20 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1f deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 20 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bcnt1_i32_b64 s8, s[4:5]",
      Vec(0x04, 0x10, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 4 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 4 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 21 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 40 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 20 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 2 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 20 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 21 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_and_saveexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x24, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_or_saveexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x25, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_xor_saveexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x26, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_andn2_saveexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x27, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_orn2_saveexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x28, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_nand_saveexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x29, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_nor_saveexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x2a, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_xnor_saveexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x2b, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_quadmask_b32 s8, s4",
      Vec(0x04, 0x2c, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ff deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 80 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_quadmask_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x2d, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 100 0 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 101 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 101 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1ff 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffff 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 80ff 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 180 0 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 ff80 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 80ff 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_abs_i32 s8, s4",
      Vec(0x04, 0x34, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 5 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 5 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000000 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 80000000 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_andn1_saveexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x37, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 1 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 1 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_orn1_saveexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x38, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_andn1_wrexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x39, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_andn2_wrexec_b64 s[8:9], s[4:5]",
      Vec(0x04, 0x3a, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 0 1 deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 4 5 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 4 5 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffe 1 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 fffffffe ffffffff deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7ffffffe 80000000 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000000 1 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 80000000 7fffffff deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 fffffffe 80000000 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_and_saveexec_b32 s8, s4",
      Vec(0x04, 0x3c, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_or_saveexec_b32 s8, s4",
      Vec(0x04, 0x3d, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_xor_saveexec_b32 s8, s4",
      Vec(0x04, 0x3e, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_andn2_saveexec_b32 s8, s4",
      Vec(0x04, 0x3f, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_orn2_saveexec_b32 s8, s4",
      Vec(0x04, 0x40, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_nand_saveexec_b32 s8, s4",
      Vec(0x04, 0x41, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_nor_saveexec_b32 s8, s4",
      Vec(0x04, 0x42, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_xnor_saveexec_b32 s8, s4",
      Vec(0x04, 0x43, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_andn1_saveexec_b32 s8, s4",
      Vec(0x04, 0x44, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_orn1_saveexec_b32 s8, s4",
      Vec(0x04, 0x45, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_andn1_wrexec_b32 s8, s4",
      Vec(0x04, 0x46, 0x88, 0xbe),
      """
      0 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_andn2_wrexec_b32 s8, s4",
      Vec(0x04, 0x47, 0x88, 0xbe),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 4 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 4 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 fffffffe deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7ffffffe deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000000 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 80000000 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_add_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x80),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 a deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 a deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 fffffffe deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 80000001 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_sub_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x88, 0x80),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 7fffffff deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_add_i32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x81),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 a deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 a deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 fffffffe deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 80000001 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_sub_i32 s8, s4, s5",
      Vec(0x04, 0x05, 0x88, 0x81),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 7fffffff deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_addc_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x82),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 a deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 b deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 80000002 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 1 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_subb_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x88, 0x82),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 7ffffffe deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_min_i32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x83),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 5 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 5 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 80000000 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000000 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 80000000 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 80000000 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_min_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x88, 0x83),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 5 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 5 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 80000000 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_max_i32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x84),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 5 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 5 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_max_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x88, 0x84),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 5 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 5 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 80000000 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000000 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 80000000 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_and_b32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x87),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 5 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 5 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 80000000 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_and_b64 s[8:9], s[4:5], s[10:11]",
      Vec(0x04, 0x0a, 0x88, 0x87),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 5 5 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 5 5 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 1 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 80000000 80000000 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_or_b32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x88),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 5 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 5 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000001 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_or_b64 s[8:9], s[4:5], s[10:11]",
      Vec(0x04, 0x0a, 0x88, 0x88),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 1 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 1 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 5 5 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 5 5 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000001 80000001 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_xor_b32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x89),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000001 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_xor_b64 s[8:9], s[4:5], s[10:11]",
      Vec(0x04, 0x0a, 0x88, 0x89),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 1 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 1 deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffe fffffffe deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000001 80000001 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7fffffff 7fffffff deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_andn2_b32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x8a),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000000 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 80000000 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_andn2_b64 s[8:9], s[4:5], s[10:11]",
      Vec(0x04, 0x0a, 0x88, 0x8a),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 0 1 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffe 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff 80000000 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 80000000 1 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 80000000 7fffffff deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7fffffff 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_orn2_b32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x8b),
      """
      0 0 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 fffffffe deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 80000000 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_orn2_b64 s[8:9], s[4:5], s[10:11]",
      Vec(0x04, 0x0a, 0x88, 0x8b),
      """
      0 0 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 fffffffe ffffffff deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 ffffffff fffffffe deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff 1 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff 80000000 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 fffffffe 7fffffff deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 80000000 7fffffff deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff 80000000 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_nand_b32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x8c),
      """
      0 0 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 fffffffa deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 fffffffa deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_nand_b64 s[8:9], s[4:5], s[10:11]",
      Vec(0x04, 0x0a, 0x88, 0x8c),
      """
      0 0 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 fffffffa fffffffa deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 fffffffa fffffffa deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffe fffffffe deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7fffffff 7fffffff deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_nor_b32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x8d),
      """
      0 0 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 fffffffa deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 fffffffa deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 7ffffffe deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_nor_b64 s[8:9], s[4:5], s[10:11]",
      Vec(0x04, 0x0a, 0x88, 0x8d),
      """
      0 0 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 fffffffe fffffffe deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 fffffffe fffffffe deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 fffffffa fffffffa deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 fffffffa fffffffa deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 7ffffffe 7ffffffe deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_xnor_b32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x8e),
      """
      0 0 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 7ffffffe deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 80000000 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_xnor_b64 s[8:9], s[4:5], s[10:11]",
      Vec(0x04, 0x0a, 0x88, 0x8e),
      """
      0 0 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 fffffffe fffffffe deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 fffffffe fffffffe deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 ffffffff ffffffff deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 1 1 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 7ffffffe 7ffffffe deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 80000000 80000000 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_lshl_b32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x8f),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 a0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 a0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffe deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 80000000 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_lshl_b64 s[8:9], s[4:5], s5",
      Vec(0x04, 0x05, 0x88, 0x8f),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 0 2 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 a0 a0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 a0 a0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffe 3 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 0 80000000 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff 80000000 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 0 3 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff 80000000 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_lshr_b32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x90),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 40000000 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_lshr_b64 s[8:9], s[4:5], s5",
      Vec(0x04, 0x05, 0x88, 0x90),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 80000000 0 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 28000000 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 28000000 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff 80000000 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 c0000000 0 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff 80000000 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_ashr_i32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x91),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 c0000000 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_ashr_i64 s[8:9], s[4:5], s5",
      Vec(0x04, 0x05, 0x88, 0x91),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 80000000 0 deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 0 deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 28000000 0 deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 28000000 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7fffffff 80000000 deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 c0000000 0 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 ffffffff 80000000 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bfe_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x88, 0x93),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bfe_i32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x94),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bfe_u64 s[8:9], s[4:5], s5",
      Vec(0x04, 0x05, 0x88, 0x94),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 1 0 deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bfe_i64 s[8:9], s[4:5], s5",
      Vec(0x04, 0x05, 0x08, 0x95),
      """
      0 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffff ffffffff deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 0 deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 0 0 deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_absdiff_i32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x96),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 2 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 7fffffff deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 1 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_lshl1_add_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x97),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 2 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 f deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 f deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 ffffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 fffffffd deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7ffffffe deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7ffffffe deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_lshl2_add_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x88, 0x97),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 4 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 19 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 19 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffffd deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 fffffffb deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7ffffffc deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7ffffffc deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_lshl3_add_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x08, 0x98),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 8 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 2d deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 2d deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffff9 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 fffffff7 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7ffffff8 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7ffffff8 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_lshl4_add_u32 s8, s4, s5",
      Vec(0x04, 0x05, 0x88, 0x98),
      """
      0 0 0 -> 0 0 0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 1 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 10 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 55 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 55 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 fffffff1 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 ffffffef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 7ffffff0 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 1 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 7fffffff deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 7ffffff0 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_eq_i32 s4, s5",
      Vec(0x04, 0x05, 0x00, 0xbf),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_lg_i32 s4, s5",
      Vec(0x04, 0x05, 0x01, 0xbf),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_gt_i32 s4, s5",
      Vec(0x04, 0x05, 0x02, 0xbf),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_ge_i32 s4, s5",
      Vec(0x04, 0x05, 0x03, 0xbf),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_lt_i32 s4, s5",
      Vec(0x04, 0x05, 0x04, 0xbf),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_le_i32 s4, s5",
      Vec(0x04, 0x05, 0x05, 0xbf),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_eq_u32 s4, s5",
      Vec(0x04, 0x05, 0x06, 0xbf),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_lg_u32 s4, s5",
      Vec(0x04, 0x05, 0x07, 0xbf),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_gt_u32 s4, s5",
      Vec(0x04, 0x05, 0x08, 0xbf),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_ge_u32 s4, s5",
      Vec(0x04, 0x05, 0x09, 0xbf),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_lt_u32 s4, s5",
      Vec(0x04, 0x05, 0x0a, 0xbf),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_le_u32 s4, s5",
      Vec(0x04, 0x05, 0x0b, 0xbf),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bitcmp0_b32 s4, s5",
      Vec(0x04, 0x05, 0x0c, 0xbf),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bitcmp1_b32 s4, s5",
      Vec(0x04, 0x05, 0x0d, 0xbf),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bitcmp0_b64 s[4:5], s5",
      Vec(0x04, 0x05, 0x0e, 0xbf),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_bitcmp1_b64 s[4:5], s5",
      Vec(0x04, 0x05, 0x0f, 0xbf),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_eq_u64 s[4:5], s[10:11]",
      Vec(0x04, 0x0a, 0x12, 0xbf),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmp_lg_u64 s[4:5], s[10:11]",
      Vec(0x04, 0x0a, 0x13, 0xbf),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_eq_i32 s8, 1",
      Vec(0x01, 0x00, 0x88, 0xb1),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_eq_i32 s8, -1",
      Vec(0xff, 0xff, 0x88, 0xb1),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_lg_i32 s8, 1",
      Vec(0x01, 0x00, 0x08, 0xb2),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_lg_i32 s8, -1",
      Vec(0xff, 0xff, 0x08, 0xb2),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_gt_i32 s8, 1",
      Vec(0x01, 0x00, 0x88, 0xb2),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_gt_i32 s8, -1",
      Vec(0xff, 0xff, 0x88, 0xb2),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_ge_i32 s8, 1",
      Vec(0x01, 0x00, 0x08, 0xb3),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_ge_i32 s8, -1",
      Vec(0xff, 0xff, 0x08, 0xb3),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_lt_i32 s8, 1",
      Vec(0x01, 0x00, 0x88, 0xb3),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_lt_i32 s8, -1",
      Vec(0xff, 0xff, 0x88, 0xb3),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_le_i32 s8, 1",
      Vec(0x01, 0x00, 0x08, 0xb4),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_le_i32 s8, -1",
      Vec(0xff, 0xff, 0x08, 0xb4),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_eq_u32 s8, 1",
      Vec(0x01, 0x00, 0x88, 0xb4),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_eq_u32 s8, 0xffff",
      Vec(0xff, 0xff, 0x88, 0xb4),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_lg_u32 s8, 1",
      Vec(0x01, 0x00, 0x08, 0xb5),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_lg_u32 s8, 0xffff",
      Vec(0xff, 0xff, 0x08, 0xb5),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_gt_u32 s8, 1",
      Vec(0x01, 0x00, 0x88, 0xb5),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_gt_u32 s8, 0xffff",
      Vec(0xff, 0xff, 0x88, 0xb5),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_ge_u32 s8, 1",
      Vec(0x01, 0x00, 0x08, 0xb6),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_ge_u32 s8, 0xffff",
      Vec(0xff, 0xff, 0x08, 0xb6),
      """
      0 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_lt_u32 s8, 1",
      Vec(0x01, 0x00, 0x88, 0xb6),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_lt_u32 s8, 0xffff",
      Vec(0xff, 0xff, 0x88, 0xb6),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_le_u32 s8, 1",
      Vec(0x01, 0x00, 0x08, 0xb7),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_cmpk_le_u32 s8, 0xffff",
      Vec(0xff, 0xff, 0x08, 0xb7),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_addk_i32 s8, 1",
      Vec(0x01, 0x00, 0x88, 0xb7),
      """
      0 0 0 -> 0 0 deadbef0 deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbef0 deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbef0 deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbef0 deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbef0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbef0 deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbef0 deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbef0 deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbef0 deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbef0 deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbef0 deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbef0 deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "s_addk_i32 s8, -1",
      Vec(0xff, 0xff, 0x88, 0xb7),
      """
      0 0 0 -> 0 0 deadbeee deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeee deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeee deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeee deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 0 1 deadbeee deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeee deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 0 1 deadbeee deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeee deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 0 1 deadbeee deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 0 1 deadbeee deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeee deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeee deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_add_co_ci_u32_e32 v8, vcc_lo, v2, v3, vcc_lo",
      Vec(0x02, 0x07, 0x10, 0x50),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef 0 deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef 1 deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef 1 deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef a deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef b deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef 0 deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef ffffffff deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef 80000002 deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef 1 deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef ffffffff deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef 7fffffff deadbeef
      """
    ),
    FlagForm(
      "v_sub_co_ci_u32_e32 v8, vcc_lo, v2, v3, vcc_lo",
      Vec(0x02, 0x07, 0x10, 0x52),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef 0 deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef 1 deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef 0 deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef fffffffe deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef 7ffffffe deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef 1 deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef 7fffffff deadbeef
      """
    ),
    FlagForm(
      "v_subrev_co_ci_u32_e32 v8, vcc_lo, v2, v3, vcc_lo",
      Vec(0x02, 0x07, 0x10, 0x54),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef 0 deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef 1 deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef 0 deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef 2 deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef 1 deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef 80000000 deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef ffffffff deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef 80000001 deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x00, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x01, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x02, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x03, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x04, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lg_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x05, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x06, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_o_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x07, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_u_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x08, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nge_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x09, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlg_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x0a, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ngt_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x0b, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nle_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x0c, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_neq_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x0d, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlt_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x0e, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_tru_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x0f, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x20, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x21, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x22, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x23, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x24, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lg_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x25, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x26, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_o_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x27, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_u_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x28, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nge_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x29, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlg_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x2a, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ngt_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x2b, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nle_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x2c, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_neq_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x2d, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlt_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x2e, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_tru_f64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0x2f, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_i32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x80, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_i32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x81, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_i32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x82, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_i32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x83, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_i32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x84, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_i32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x85, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_i32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x86, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_t_i32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x87, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_class_f32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x88, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_i16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x89, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_i16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x8a, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_i16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x8b, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_i16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x8c, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_i16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x8d, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_i16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x8e, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_class_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0x8f, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_i64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xa0, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_i64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xa1, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_i64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xa2, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_i64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xa3, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_i64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xa4, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_i64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xa5, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_i64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xa6, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_t_i64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xa7, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_class_f64_e64 s14, v[2:3], v3",
      Vec(0x0e, 0x00, 0xa8, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_u16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xa9, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_u16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xaa, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_u16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xab, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_u16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xac, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_u16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xad, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_u16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xae, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_u32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xc0, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_u32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xc1, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_u32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xc2, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_u32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xc3, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_u32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xc4, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_u32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xc5, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_u32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xc6, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_t_u32_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xc7, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xc8, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xc9, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xca, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xcb, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xcc, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lg_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xcd, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xce, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_o_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xcf, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_u64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xe0, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_u64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xe1, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_u64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xe2, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_u64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xe3, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_u64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xe4, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_u64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xe5, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_u64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xe6, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_t_u64_e64 s14, v[2:3], v[6:7]",
      Vec(0x0e, 0x00, 0xe7, 0xd4, 0x02, 0x0d, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_u_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xe8, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nge_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xe9, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlg_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xea, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ngt_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xeb, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nle_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xec, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_neq_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xed, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlt_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xee, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_tru_f16_e64 s14, v2, v3",
      Vec(0x0e, 0x00, 0xef, 0xd4, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_add_co_ci_u32_e64 v8, s14, v2, v3, s16",
      Vec(0x08, 0x0e, 0x28, 0xd5, 0x02, 0x07, 0x42, 0x00),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 0 deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 a deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 b deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 0 deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 ffffffff deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 ffffffff deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 80000002 deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 1 deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 ffffffff deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 7fffffff deadbeef
      """
    ),
    FlagForm(
      "v_sub_co_ci_u32_e64 v8, s14, v2, v3, s16",
      Vec(0x08, 0x0e, 0x29, 0xd5, 0x02, 0x07, 0x42, 0x00),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 0 deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 ffffffff deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 0 deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 ffffffff deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 fffffffe deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 ffffffff deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 ffffffff deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 7ffffffe deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 ffffffff deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 7fffffff deadbeef
      """
    ),
    FlagForm(
      "v_subrev_co_ci_u32_e64 v8, s14, v2, v3, s16",
      Vec(0x08, 0x0e, 0x2a, 0xd5, 0x02, 0x07, 0x42, 0x00),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 0 deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 ffffffff deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 0 deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 1 ffffffff deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 2 deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 ffffffff deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 80000000 deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 1 ffffffff deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 ffffffff deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 80000001 deadbeef
      """
    ),
    FlagForm(
      "v_div_scale_f32 v8, s14, v2, v3, v4",
      Vec(0x08, 0x0e, 0x6d, 0xd5, 0x02, 0x07, 0x12, 0x04),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 ffc00000 deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 ffc00000 deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 ffc00000 deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 ffc00000 deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 16200000 deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 ffc00000 deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 ffffffff deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 ffc00000 deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 80000000 deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 ffc00000 deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 ffc00000 deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 ffc00000 deadbeef
      """
    ),
    FlagForm(
      "v_div_scale_f64 v[8:9], s14, v[2:3], v[6:7], v[2:3]",
      Vec(0x08, 0x0e, 0x6e, 0xd5, 0x02, 0x0d, 0x0a, 0x04),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 0 fff80000
      0 1 0 -> 0 0 deadbeef deadbeef 0 0 6d00000
      1 0 0 -> 0 0 deadbeef deadbeef 0 0 4d00000
      5 5 0 -> 0 0 deadbeef deadbeef 0 140000 6f40000
      5 5 1 -> 1 1 deadbeef deadbeef 0 140000 6f40000
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 ffffffff 1
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 ffffffff ffffffff
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 7fffffff 80000000
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 0 6d80000
      0 0 1 -> 1 1 deadbeef deadbeef 0 0 fff80000
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 80000000 7fffffff
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 ffffffff 80000000
      """
    ),
    FlagForm(
      "v_mad_u64_u32 v[8:9], s14, v2, v3, v[2:3]",
      Vec(0x08, 0x0e, 0x76, 0xd5, 0x02, 0x07, 0x0a, 0x04),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 0 0
      0 1 0 -> 0 0 deadbeef deadbeef 0 0 1
      1 0 0 -> 0 0 deadbeef deadbeef 0 1 0
      5 5 0 -> 0 0 deadbeef deadbeef 0 1e 5
      5 5 1 -> 1 1 deadbeef deadbeef 0 1e 5
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 fffffffe 2
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 0 fffffffe
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 ffffffff bfffffff
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 0 2
      0 0 1 -> 1 1 deadbeef deadbeef 0 0 0
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 0 bfffffff
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 7fffffff 0
      """
    ),
    FlagForm(
      "v_mad_i64_i32 v[8:9], s14, v2, v3, v[2:3]",
      Vec(0x08, 0x0e, 0x77, 0xd5, 0x02, 0x07, 0x0a, 0x04),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 0 0
      0 1 0 -> 0 0 deadbeef deadbeef 0 0 1
      1 0 0 -> 0 0 deadbeef deadbeef 0 1 0
      5 5 0 -> 0 0 deadbeef deadbeef 0 1e 5
      5 5 1 -> 1 1 deadbeef deadbeef 0 1e 5
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 fffffffe 1
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 0 0
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 ffffffff 40000000
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 0 1
      0 0 1 -> 1 1 deadbeef deadbeef 0 0 0
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 0 40000000
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 7fffffff 80000001
      """
    ),
    FlagForm(
      "v_add_co_u32 v8, s14, v2, v3",
      Vec(0x08, 0x0e, 0x0f, 0xd7, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 0 deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 a deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 a deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 0 deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 1 fffffffe deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 ffffffff deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 80000001 deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 0 deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 ffffffff deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 7fffffff deadbeef
      """
    ),
    FlagForm(
      "v_sub_co_u32 v8, s14, v2, v3",
      Vec(0x08, 0x0e, 0x10, 0xd7, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 0 deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 1 ffffffff deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 0 deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 0 deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 0 fffffffe deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 0 deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 ffffffff deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 0 7fffffff deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 0 deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 7fffffff deadbeef
      """
    ),
    FlagForm(
      "v_subrev_co_u32 v8, s14, v2, v3",
      Vec(0x08, 0x0e, 0x19, 0xd7, 0x02, 0x07, 0x02, 0x02),
      """
      0 0 0 -> 0 0 deadbeef deadbeef 0 0 deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef 1 ffffffff deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef 0 0 deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef 0 0 deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef 1 2 deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef 0 0 deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef 0 1 deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef 1 80000001 deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef 0 0 deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef 1 ffffffff deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef 1 80000001 deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x00, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x02, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x04, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x06, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x08, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lg_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x0a, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x0c, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_o_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x0e, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_u_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x10, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nge_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x12, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlg_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x14, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ngt_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x16, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nle_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x18, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_neq_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x1a, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlt_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x1c, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_tru_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x1e, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x40, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x42, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x44, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x46, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x48, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lg_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x4a, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x4c, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_o_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x4e, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_u_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x50, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nge_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x52, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlg_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x54, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ngt_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x56, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nle_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x58, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_neq_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x5a, 0x7c),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlt_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x5c, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_tru_f64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x5e, 0x7c),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_i32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x00, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_i32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x02, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_i32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x04, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_i32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x06, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_i32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x08, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_i32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x0a, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_i32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x0c, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_t_i32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x0e, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_class_f32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x10, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_i16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x12, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_i16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x14, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_i16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x16, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_i16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x18, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_i16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x1a, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_i16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x1c, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_class_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x1e, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_i64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x40, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_i64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x42, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_i64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x44, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_i64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x46, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_i64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x48, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_i64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x4a, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_i64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x4c, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_t_i64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0x4e, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_class_f64_e32 vcc_lo, v[2:3], v3",
      Vec(0x02, 0x07, 0x50, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_u16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x52, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_u16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x54, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_u16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x56, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_u16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x58, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_u16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x5a, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_u16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x5c, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_u32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x80, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_u32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x82, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_u32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x84, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_u32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x86, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_u32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x88, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_u32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x8a, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_u32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x8c, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_t_u32_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x8e, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x90, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x92, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x94, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x96, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x98, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lg_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x9a, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x9c, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_o_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0x9e, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_f_u64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xc0, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_lt_u64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xc2, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_eq_u64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xc4, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_le_u64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xc6, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_gt_u64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xc8, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ne_u64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xca, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ge_u64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xcc, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_t_u64_e32 vcc_lo, v[2:3], v[6:7]",
      Vec(0x02, 0x0d, 0xce, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_u_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0xd0, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nge_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0xd2, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlg_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0xd4, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_ngt_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0xd6, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nle_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0xd8, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_neq_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0xda, 0x7d),
      """
      0 0 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_nlt_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0xdc, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 0 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    ),
    FlagForm(
      "v_cmp_tru_f16_e32 vcc_lo, v2, v3",
      Vec(0x02, 0x07, 0xde, 0x7d),
      """
      0 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      1 0 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      5 5 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 1 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff ffffffff 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      7fffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 1 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      0 0 1 -> 1 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      80000000 7fffffff 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      ffffffff 80000000 0 -> 0 1 deadbeef deadbeef deadbeef deadbeef deadbeef
      """
    )
  )
}
