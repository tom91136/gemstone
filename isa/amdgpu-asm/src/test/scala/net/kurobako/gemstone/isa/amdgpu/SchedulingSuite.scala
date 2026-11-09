package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.testing.*

class SchedulingSuite extends Suite {
  import Scheduling.*

  private def target(name: String): Target = Target.all.find(_.raw == name).get

  private def vmem(latency: Int): Option[Timing] = Some(Timing(latency = latency, cycles = 1, writes = "WriteVMEM"))

  test("timings come from the LLVM model of each processor") {
    assertEquals(Scheduling.timing(target("gfx906"), "global_load_dword", lds = false), vmem(80))
    assertEquals(Scheduling.timing(target("gfx1030"), "global_load_dword", lds = false), vmem(320))
    assertEquals(Scheduling.timing(target("gfx1100"), "global_load_b32", lds = false), vmem(320))
    assertEquals(Scheduling.timing(target("gfx1100"), "global_load_dword", lds = false), None)
    assertEquals(Scheduling.timing(target("gfx906"), "v_add_nc_u32", lds = false), None)
  }

  test("aliases share the timings of their canonical processor") {
    assertEquals(
      Scheduling.timing(target("gfx1031"), "v_add_f32", lds = false),
      Scheduling.timing(target("gfx1030"), "v_add_f32", lds = false)
    )
    assertEquals(
      Scheduling.timing(target("gfx12"), "s_nop", lds = false),
      Scheduling.timing(target("gfx1200"), "s_nop", lds = false)
    )
  }

  test("targets without an LLVM processor model have no timings") {
    assertEquals(Scheduling.timing(target("gfx940"), "v_add_f32", lds = false), None)
  }

  test("lds loads are timed apart from plain loads") {
    assertEquals(Scheduling.timing(target("gfx1030"), "global_load_ubyte", lds = true).map(_.cycles), Some(2))
    assertEquals(Scheduling.timing(target("gfx1030"), "global_load_ubyte", lds = false).map(_.cycles), Some(1))
  }

  test("lds-only mnemonics are timed in both forms") {
    assertEquals(Scheduling.timing(target("gfx906"), "buffer_store_lds_dword", lds = false), vmem(80))
    assertEquals(Scheduling.timing(target("gfx906"), "buffer_store_lds_dword", lds = true), vmem(80))
    assertEquals(Scheduling.timing(target("gfx1030"), "buffer_store_lds_dword", lds = false), None)
  }

  test("latency and cycles agree with llvm-mca 23.1.0") {
    val rows = Vector(
      ("gfx906", "v_add_f32", 1, 1),
      ("gfx906", "v_fma_f32", 1, 1),
      ("gfx906", "v_rcp_f32", 4, 1),
      ("gfx906", "v_exp_f32", 4, 1),
      ("gfx906", "v_mov_b32", 1, 1),
      ("gfx906", "v_mul_lo_u32", 4, 1),
      ("gfx906", "s_mov_b32", 1, 1),
      ("gfx906", "s_add_u32", 1, 1),
      ("gfx906", "ds_read_b32", 5, 1),
      ("gfx906", "global_load_dword", 80, 1),
      ("gfx1030", "v_add_f32", 5, 1),
      ("gfx1030", "v_fma_f32", 5, 1),
      ("gfx1030", "v_rcp_f32", 10, 1),
      ("gfx1030", "v_exp_f32", 10, 1),
      ("gfx1030", "v_mov_b32", 5, 1),
      ("gfx1030", "v_mul_lo_u32", 8, 1),
      ("gfx1030", "s_mov_b32", 2, 1),
      ("gfx1030", "s_add_u32", 2, 1),
      ("gfx1030", "ds_read_b32", 20, 1),
      ("gfx1030", "global_load_dword", 320, 1),
      ("gfx1030", "global_load_sbyte", 320, 1),
      ("gfx1030", "global_load_sshort", 320, 1),
      ("gfx1030", "global_load_ubyte", 320, 1),
      ("gfx1030", "global_load_ushort", 320, 1),
      ("gfx1030", "scratch_load_sbyte", 320, 1),
      ("gfx1030", "scratch_load_sshort", 320, 1),
      ("gfx1030", "scratch_load_ubyte", 320, 1),
      ("gfx1030", "scratch_load_ushort", 320, 1),
      ("gfx1100", "v_add_f32", 5, 1),
      ("gfx1100", "v_fma_f32", 5, 1),
      ("gfx1100", "v_rcp_f32", 10, 1),
      ("gfx1100", "v_exp_f32", 10, 1),
      ("gfx1100", "v_mov_b32", 5, 1),
      ("gfx1100", "v_mul_lo_u32", 8, 1),
      ("gfx1100", "s_mov_b32", 2, 1),
      ("gfx1100", "s_add_u32", 2, 1),
      ("gfx1100", "ds_load_b32", 20, 1),
      ("gfx1100", "global_load_b32", 320, 1),
      ("gfx906", "s_nop", 1, 1),
      ("gfx906", "global_load_sbyte", 80, 1),
      ("gfx906", "v_swap_b32", 2, 2),
      ("gfx1030", "s_nop", 2, 1),
      ("gfx1030", "v_add_co_u32", 5, 2),
      ("gfx1030", "v_swap_b32", 6, 2),
      ("gfx1100", "s_nop", 2, 1),
      ("gfx1100", "s_delay_alu", 2, 1),
      ("gfx1100", "v_add_co_u32", 5, 2),
      ("gfx1100", "v_swap_b32", 6, 2)
    )
    val ldsRows = Vector(
      ("gfx1030", "global_load_dword", 320, 2),
      ("gfx1030", "global_load_sbyte", 320, 2),
      ("gfx1030", "global_load_sshort", 320, 2),
      ("gfx1030", "global_load_ubyte", 320, 2),
      ("gfx1030", "global_load_ushort", 320, 2),
      ("gfx1030", "scratch_load_dword", 320, 2),
      ("gfx1030", "scratch_load_sbyte", 320, 2),
      ("gfx1030", "scratch_load_sshort", 320, 2),
      ("gfx1030", "scratch_load_ubyte", 320, 2),
      ("gfx1030", "scratch_load_ushort", 320, 2)
    )
    rows.foreach { (processor, mnemonic, latency, cycles) =>
      val timing = Scheduling.timing(target(processor), mnemonic, lds = false)
      assertEquals(timing.map(t => (t.latency, t.cycles)), Some((latency, cycles)))
    }
    ldsRows.foreach { (processor, mnemonic, latency, cycles) =>
      val timing = Scheduling.timing(target(processor), mnemonic, lds = true)
      assertEquals(timing.map(t => (t.latency, t.cycles)), Some((latency, cycles)))
    }
  }
}
