package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.*
import net.kurobako.gemstone.profile.HardwareLoops.Row
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import net.kurobako.gemstone.vm.LaunchObserver.*

/** Cycles per iteration of loop kernels timed with `clock64` against the simulator. Each measured value is the median
  * over launches of the slowest resident warp's loop cycles, divided by iterations and resident warps, so it is the
  * multiprocessor cycles per warp iteration that [[PipelineSimulator.Block.cyclesPerIteration]] reports. Shared memory
  * pins residency to one workgroup per SM on sm_89 and two per WGP on gfx1036, which holds twice the warps listed.
  *
  * sm_89: RTX 4060 Ti (34 SMs), NVIDIA 620.70.06, CUDA 13.3, 2026-11-10, 1734 launches per case (51 launches on each of
  * 34 SMs), spread under 0.1 percent.
  *
  * gfx1036: Ryzen 9 9950X3D integrated GPU (one WGP, 2 CUs, 2200 MHz), Linux 7.2.15 amdgpu, ROCm 7.2 hipcc, 2026-11-10,
  * 202 launches per case. Cases whose slowest-wave time varies more than 10 percent between launches are not listed.
  * The MUFU and FP64 rows at 4, 8 and 16 warps run 8, 16 and 32 resident waves, all co-resident.
  *
  * The sweep and placement rows were measured on 2026-11-11 with 201 launches after 5 warm-ups: 4096 iterations per
  * launch on sm_89 at host load average 11.8 to 32.7, and 1024 iterations per launch on gfx1036 in two workgroups per
  * launch at host load average 6.9 to 8.6 on 32 threads. The gfx1036 values are shader cycles: `clock64` lowers to
  * `s_memtime`, which counts `SHADER_CYCLES`.
  *
  * The 2026-11-13 probe rows were measured with 201 launches after 5 warm-ups: 4096 iterations per launch on sm_89 at
  * host load average 30 to 36, and 1024 to 4096 iterations on gfx1036 at host load average 21 to 31. Those gfx1036 rows
  * hold one workgroup per launch and divide by the warps listed, not the two workgroups the earlier gfx1036 rows hold.
  * Placements follow slot mod 4 on sm_89 and `hw_id1` bits 9-8 on gfx1036; `same SMSP` and `same SIMD` put every warp
  * on one partition.
  *
  * The expected binding is `Path` when the case runs over 25 percent above the smallest cycles per iteration the card
  * reached with 1 to 32 resident warps, and otherwise the resource whose instruction count and rate give that smallest
  * value.
  */
class HardwareValidationSuite extends Suite {
  import ProfileReport.Binding

  private val tolerance = 0.25

  private case class At(name: String, warps: Vec[Int])

  private case class Measured(
      kernel: String,
      warps: Int,
      cycles: Double,
      binding: Option[Binding],
      placement: Option[At] = None
  )

  private val split2    = At("different SMSPs", Vec(1, 1, 0, 0))
  private val same2     = At("same SMSP", Vec(2, 0, 0, 0))
  private val same4     = At("same SMSP", Vec(4, 0, 0, 0))
  private val same8     = At("same SMSP", Vec(8, 0, 0, 0))
  private val slots4    = At("slots 0-3", Vec(1, 1, 1, 1))
  private val spread4   = At("slots 0,4,8,12", Vec(4, 0, 0, 0))
  private val oneSimd   = At("one SIMD", Vec(1, 0, 0, 0))
  private val sameSimd2 = At("same SIMD", Vec(2, 0, 0, 0))
  private val sameSimd4 = At("same SIMD", Vec(4, 0, 0, 0))
  private val sameSimd8 = At("same SIMD", Vec(8, 0, 0, 0))
  private val eachSimd1 = At("one per SIMD", Vec(1, 1, 1, 1))
  private val eachSimd2 = At("two per SIMD", Vec(2, 2, 2, 2))
  private val eachSimd4 = At("four per SIMD", Vec(4, 4, 4, 4))
  private val eachSimd8 = At("eight per SIMD", Vec(8, 8, 8, 8))

  private val sm89Shared: Long    = 101376L
  private val gfx1036Shared: Long = 65536L

  private val sm89Cases = Vec(
    Measured("chain_ffma", 1, 39.01, Some(Binding.Path)),
    Measured("chain_ffma", 4, 9.75, Some(Binding.Path)),
    Measured("chain_ffma", 16, 3.02, Some(Binding.Issue)),
    Measured("stream_ffma", 1, 34.01, Some(Binding.Path)),
    Measured("stream_ffma", 4, 8.50, Some(Binding.Path)),
    Measured("stream_ffma", 16, 5.75, Some(Binding.Issue)),
    Measured("stream_mufu", 1, 73.01, Some(Binding.Path)),
    Measured("stream_mufu", 4, 18.25, Some(Binding.Arithmetic(Pipe.Mufu))),
    Measured("stream_mufu", 16, 16.00, Some(Binding.Arithmetic(Pipe.Mufu))),
    Measured("stream_dfma", 1, 166.51, Some(Binding.Path)),
    Measured("stream_dfma", 4, 128.20, Some(Binding.Arithmetic(Pipe.Fp64))),
    Measured("stream_dfma", 16, 128.20, Some(Binding.Arithmetic(Pipe.Fp64))),
    Measured("mixed_ldg", 1, 78.23, Some(Binding.Path)),
    Measured("mixed_ldg", 4, 21.80, Some(Binding.Path)),
    Measured("mixed_ldg", 16, 8.00, Some(Binding.Loads)),
    Measured("reduce_tree", 1, 41.01, Some(Binding.Path)),
    Measured("reduce_tree", 4, 10.25, Some(Binding.Path)),
    Measured("reduce_tree", 16, 5.75, Some(Binding.Issue)),
    Measured("stream_ffma_1", 1, 27.02, Some(Binding.Path)),
    Measured("stream_ffma_1", 2, 13.51, Some(Binding.Path), Some(split2)),
    Measured("stream_ffma_1", 2, 16.01, Some(Binding.Path), Some(same2)),
    Measured("stream_ffma_1", 4, 6.76, Some(Binding.Path), Some(slots4)),
    Measured("stream_ffma_1", 4, 8.01, Some(Binding.Path), Some(spread4)),
    Measured("stream_ffma_8", 1, 27.02, Some(Binding.Path)),
    Measured("stream_ffma_8", 2, 13.51, Some(Binding.Path), Some(split2)),
    Measured("stream_ffma_8", 2, 16.01, Some(Binding.Path), Some(same2)),
    Measured("stream_ffma_8", 4, 6.76, Some(Binding.Path), Some(slots4)),
    Measured("stream_ffma_8", 4, 12.61, Some(Binding.Path), Some(spread4)),
    Measured("stream_ffma_64", 1, 81.05, Some(Binding.Path)),
    Measured("stream_dfma_1", 1, 61.02, Some(Binding.Path)),
    Measured("stream_dfma_1", 2, 30.51, Some(Binding.Path), Some(split2)),
    Measured("stream_dfma_1", 4, 19.01, Some(Binding.Arithmetic(Pipe.Fp64)), Some(slots4)),
    Measured("stream_dfma_1", 4, 19.01, Some(Binding.Arithmetic(Pipe.Fp64)), Some(spread4)),
    Measured("stream_dfma_8", 1, 188.03, Some(Binding.Arithmetic(Pipe.Fp64))),
    Measured("stream_dfma_8", 2, 152.02, Some(Binding.Arithmetic(Pipe.Fp64)), Some(split2)),
    Measured("stream_dfma_8", 4, 152.01, Some(Binding.Arithmetic(Pipe.Fp64)), Some(slots4)),
    Measured("stream_dfma_8", 4, 152.59, Some(Binding.Arithmetic(Pipe.Fp64)), Some(spread4)),
    Measured("stream_dfma_16", 1, 340.03, Some(Binding.Arithmetic(Pipe.Fp64))),
    Measured("stream_dfma_reg_1", 1, 65.07, Some(Binding.Path)),
    Measured("stream_dfma_reg_1", 2, 32.54, Some(Binding.Path), Some(split2)),
    Measured("stream_dfma_reg_1", 4, 17.28, Some(Binding.Arithmetic(Pipe.Fp64)), Some(slots4)),
    Measured("stream_dfma_reg_1", 4, 17.27, Some(Binding.Arithmetic(Pipe.Fp64)), Some(spread4)),
    Measured("stream_dfma_reg_8", 1, 166.08, Some(Binding.Path)),
    Measured("stream_dfma_reg_8", 2, 128.04, Some(Binding.Arithmetic(Pipe.Fp64)), Some(split2)),
    Measured("stream_dfma_reg_8", 4, 128.03, Some(Binding.Arithmetic(Pipe.Fp64)), Some(slots4)),
    Measured("stream_dfma_reg_8", 4, 128.22, Some(Binding.Arithmetic(Pipe.Fp64)), Some(spread4)),
    Measured("stream_dfma_reg_16", 1, 294.08, Some(Binding.Arithmetic(Pipe.Fp64))),
    Measured("chain_ffma_1", 1, 27.02, Some(Binding.Path)),
    Measured("chain_ffma_16", 1, 71.03, Some(Binding.Path)),
    Measured("chain_dfma_1", 1, 61.02, Some(Binding.Path)),
    Measured("chain_dfma_8", 1, 384.02, Some(Binding.Path)),
    Measured("chain_dfma_16", 1, 768.02, Some(Binding.Path)),
    Measured("stream_mufu_1", 1, 28.01, Some(Binding.Path)),
    Measured("stream_mufu_4", 1, 36.01, Some(Binding.Arithmetic(Pipe.Mufu))),
    Measured("stream_mufu_16", 1, 137.01, Some(Binding.Arithmetic(Pipe.Mufu))),
    Measured("stream_mufu_16", 2, 128.43, Some(Binding.Arithmetic(Pipe.Mufu)), Some(same2)),
    Measured("stream_mufu_16", 4, 128.04, Some(Binding.Arithmetic(Pipe.Mufu)), Some(same4)),
    Measured("stream_mufu_16", 8, 128.01, Some(Binding.Arithmetic(Pipe.Mufu)), Some(same8)),
    Measured("stream_dfma_reg_2", 1, 81.01, Some(Binding.Path)),
    Measured("stream_dfma_reg_4", 1, 113.01, Some(Binding.Path)),
    Measured("loop_control", 1, 27.01, Some(Binding.Path)),
    Measured("chain_ffma_8", 1, 39.01, Some(Binding.Path)),
    Measured("chain_ffma_8", 4, 11.88, Some(Binding.Issue), Some(same4)),
    Measured("chain_ffma_8", 8, 11.08, Some(Binding.Issue), Some(same8)),
    Measured("stream_ffma_8", 4, 12.91, Some(Binding.Path), Some(same4)),
    Measured("stream_ffma_8", 8, 11.54, Some(Binding.Path), Some(same8))
  )

  private val gfx1036Cases = Vec(
    Measured("chain_ffma", 1, 32.00, Some(Binding.Path)),
    Measured("chain_ffma", 2, 16.00, Some(Binding.Path)),
    Measured("chain_ffma", 16, 3.97, None),
    Measured("stream_ffma", 1, 26.00, Some(Binding.Path)),
    Measured("stream_ffma", 2, 13.00, Some(Binding.Path)),
    Measured("stream_ffma", 16, 5.33, Some(Binding.Issue)),
    Measured("stream_mufu", 1, 31.00, Some(Binding.Path)),
    Measured("stream_mufu", 2, 15.50, Some(Binding.Path)),
    Measured("stream_dfma", 1, 69.02, Some(Binding.Path)),
    Measured("stream_dfma", 2, 34.51, Some(Binding.Arithmetic(Pipe.Fp64))),
    Measured("mixed_ldg", 1, 76.72, Some(Binding.Path)),
    Measured("mixed_ldg", 2, 38.35, Some(Binding.Path)),
    Measured("mixed_ldg", 16, 5.29, Some(Binding.Issue)),
    Measured("reduce_tree", 1, 25.00, Some(Binding.Path)),
    Measured("reduce_tree", 2, 12.50, Some(Binding.Path)),
    Measured("stream_mufu", 4, 8.80, Some(Binding.Arithmetic(Pipe.Mufu))),
    Measured("stream_mufu", 8, 8.42, Some(Binding.Arithmetic(Pipe.Mufu))),
    Measured("stream_mufu", 16, 8.21, Some(Binding.Arithmetic(Pipe.Mufu))),
    Measured("stream_dfma", 4, 32.10, Some(Binding.Arithmetic(Pipe.Fp64))),
    Measured("stream_dfma", 8, 32.09, Some(Binding.Arithmetic(Pipe.Fp64))),
    Measured("stream_dfma", 16, 32.08, Some(Binding.Arithmetic(Pipe.Fp64))),
    Measured("stream_ffma_1", 1, 13.01, Some(Binding.Path)),
    Measured("stream_ffma_8", 1, 22.00, Some(Binding.Path)),
    Measured("stream_ffma_8", 4, 6.20, Some(Binding.Path)),
    Measured("stream_ffma_8", 8, 4.30, Some(Binding.Issue)),
    Measured("stream_ffma_8", 16, 4.11, Some(Binding.Issue)),
    Measured("stream_ffma_64", 1, 50.00, Some(Binding.Path)),
    Measured("chain_ffma_1", 1, 13.01, Some(Binding.Path)),
    Measured("chain_ffma_64", 1, 169.50, Some(Binding.Path)),
    Measured("stream_mufu_1", 1, 11.00, Some(Binding.Path)),
    Measured("stream_mufu_8", 1, 31.00, Some(Binding.Path)),
    Measured("stream_mufu_16", 1, 46.50, Some(Binding.Path)),
    Measured("stream_dfma_1", 1, 11.02, Some(Binding.Path)),
    Measured("stream_dfma_4", 1, 39.52, Some(Binding.Path)),
    Measured("stream_dfma_16", 1, 127.98, Some(Binding.Arithmetic(Pipe.Fp64))),
    Measured("stream_dfma_16_wide", 1, 255.98, Some(Binding.Path), Some(oneSimd)),
    Measured("stream_dfma_16_wide", 2, 255.25, Some(Binding.Path), Some(sameSimd2)),
    Measured("stream_dfma_16_wide", 4, 242.41, Some(Binding.Path), Some(sameSimd4)),
    Measured("stream_dfma_16_wide", 4, 63.99, Some(Binding.Arithmetic(Pipe.Fp64)), Some(eachSimd1)),
    Measured("stream_dfma_16_wide", 8, 63.81, Some(Binding.Arithmetic(Pipe.Fp64)), Some(eachSimd2)),
    Measured("stream_dfma_16_wide", 16, 60.62, Some(Binding.Arithmetic(Pipe.Fp64)), Some(eachSimd4)),
    Measured("stream_dfma_rate", 1, 63.99, Some(Binding.Arithmetic(Pipe.Fp64)), Some(oneSimd)),
    Measured("stream_mufu_16_wide", 1, 93.00, Some(Binding.Path), Some(oneSimd)),
    Measured("stream_mufu_16_wide", 2, 71.34, Some(Binding.Path), Some(sameSimd2)),
    Measured("stream_mufu_16_wide", 4, 67.78, Some(Binding.Path), Some(sameSimd4)),
    Measured("stream_mufu_16_wide", 8, 65.77, Some(Binding.Path), Some(sameSimd8)),
    Measured("stream_mufu_16_wide", 4, 23.25, Some(Binding.Path), Some(eachSimd1)),
    Measured("stream_mufu_16_wide", 8, 17.84, Some(Binding.Arithmetic(Pipe.Mufu)), Some(eachSimd2)),
    Measured("stream_mufu_16_wide", 16, 16.97, Some(Binding.Arithmetic(Pipe.Mufu)), Some(eachSimd4)),
    Measured("stream_mufu_16_wide", 32, 16.45, Some(Binding.Arithmetic(Pipe.Mufu)), Some(eachSimd8)),
    Measured("stream_ffma_8", 4, 10.86, Some(Binding.Path), Some(sameSimd4)),
    Measured("stream_ffma_8", 8, 10.40, Some(Binding.Path), Some(sameSimd8))
  )

  private class State(
      val issued: Long,
      val text: String,
      override val schedule: Option[Schedule],
      override val pipe: Option[Pipe]
  ) extends ArchitecturalState {
    def begins: Boolean   = true
    def unit: MachineUnit =
      MachineUnit(Coordinates(1, 2, 3), issued.toInt.unsafe[Int >= 0], 32.unsafe[Int >= 0], MachineUnit.State.Running)
    def subgroupSize: Int > 0                    = 32
    def eachLane(each: (Int >= 0) => Unit): Unit = (0 until 32).foreach(lane => each(lane.unsafe))
  }

  private val iterations = 4

  private def at(address: Long): Fault.Location =
    Fault.Location("k", Some("f"), Some(address.unsafe), Some(Coordinates(1, 2, 3)), Some(0.unsafe))

  private def loads(row: Row): Boolean = row.text.startsWith("LDG") || row.text.startsWith("global_load")

  private def record(rows: Vec[Row], warps: Int, shared: Long): ProfileStream = {
    val recorder = ProfileRecorder()
    val last     = rows.size + 1
    val graph    = ControlFlowGraph(
      Vec(Some(Vec(1))) ++
        Vec.tabulate(rows.size)(i => if i == rows.size - 1 then Some(Vec(1, last)) else Some(Vec(i + 2))) :+
        None,
      0
    )
    val entry   = Row("entry", None, Vec.empty, Vec.empty, Schedule.Estimated(1.unsafe[Int >= 0]))
    val exit    = Row("exit", None, Vec.empty, Vec.empty, Schedule.Estimated(1.unsafe[Int >= 0]))
    val all     = entry +: rows :+ exit
    val offsets = Vec.tabulate(all.size)(i => 0x10L * (i + 1))
    recorder.launched(KernelResources((32 * warps).unsafe, 32, None, shared.unsafe))
    recorder.controlFlow("k", graph, offsets)
    val order = Vec(0) ++ Vec.fill(iterations)(Vec.tabulate(rows.size)(_ + 1)).flatten :+ last
    order.zipWithIndex.foreach { (index, issued) =>
      val row = all(index)
      assertEquals(
        recorder.instruction(at(offsets(index)), State(issued.toLong, row.text, Some(row.schedule), row.pipe)),
        Right(())
      )
      if (loads(row))
        (0 until 32).foreach(lane =>
          recorder.access(lane.unsafe, Space.Global, AccessKind.Read, 0x100000L + 0x1000L * index + 4L * lane, 4.unsafe)
        )
      recorder.registers(issued.toLong, RegisterUse(row.reads, row.writes))
    }
    recorder.stream
  }

  private def analysed(
      architecture: Architecture,
      rows: Vec[Row],
      warps: Int,
      shared: Long,
      placement: Option[PipelineSimulator.Placement]
  ): PipelineSimulator.Block =
    BlockAnalysis.of(record(rows, warps, shared), architecture, placement).fold(
      fail(_),
      _.regions.find(_.kind == BlockAnalysis.Kind.Loop).map(_.status) match {
        case Some(BlockAnalysis.Status.Executed(timing)) => timing.block
        case Some(BlockAnalysis.Status.Withheld(why))    => fail(s"analysis withheld: $why")
        case other                                       => fail(s"no executed loop: $other")
      }
    )

  private def assertWithin(modelled: Double, measured: Double): Unit =
    assert(
      math.abs(modelled - measured) <= tolerance * measured,
      f"modelled $modelled%.2f against measured $measured%.2f cycles per iteration"
    )

  private def variableLatency(row: Row): Boolean = row.schedule match {
    case Schedule.Encoded(_, barrier) => barrier
    case _                            => false
  }

  private def tableLatency(architecture: Architecture, row: Row): Architecture.Sourced[Int] = {
    def whole(latency: Architecture.Sourced[Double]) =
      Architecture.Sourced(math.round(latency.value).toInt, latency.source)
    val table = for {
      sm       <- NvidiaLatency.sm(architecture)
      producer <- NvidiaLatency.producer(row.text)
      latency  <- NvidiaLatency.measured(sm, producer).map(whole(_)).orElse(NvidiaLatency.fixed(sm, producer))
    } yield latency
    table.getOrElse(row.schedule match {
      case Schedule.Encoded(_, true) if row.text.startsWith("LDG") =>
        architecture.latency.l1.fold(fail("no l1 latency"))(l1 =>
          Architecture.Sourced(math.round(l1.value).toInt, l1.source)
        )
      case schedule =>
        Architecture.Sourced(
          schedule.plannedCycles,
          Architecture.Source.Inherited("compiler encoding", "the compiler's encoded issue stalls")
        )
    })
  }

  private def traffic(architecture: Architecture, row: Row): PipelineSimulator.Traffic =
    if (loads(row)) PipelineSimulator.Traffic(sectors = 128.0 / architecture.global.sectorBytes.value, loads = 1.0)
    else PipelineSimulator.Traffic.none

  private def simulated(
      architecture: Architecture,
      rows: Vec[Row],
      warps: Int,
      placement: Option[PipelineSimulator.Placement]
  ): PipelineSimulator.Block = {
    val ops = rows.zipWithIndex.map { case (row, index) =>
      PipelineSimulator.Op(
        row.pipe,
        32,
        row.reads,
        row.writes,
        tableLatency(architecture, row),
        1,
        traffic = traffic(architecture, row),
        backEdge = index == rows.size - 1,
        rate = NvidiaLatency.operandRate(architecture, row.text)
      )
    }
    PipelineSimulator.simulate(
      architecture,
      Some(Occupancy(1, warps, architecture.limits.subgroups.value, Map.empty)),
      PipelineSimulator.Stream.repeat(ops),
      placement
    )
  }

  private def register(
      architecture: Architecture,
      loops: Map[String, Vec[Row]],
      cases: Vec[Measured],
      shared: Long
  ): Unit = cases.foreach { measured =>
    val rows      = loops(measured.kernel)
    val placement = measured.placement.map(at => PipelineSimulator.Placement(at.warps))
    val name      = s"${architecture.name} ${measured.kernel} with ${measured.warps} warps per workgroup" +
      measured.placement.fold("")(at => s" (${at.name})")
    val direct = rows.exists(variableLatency(_))
    test(s"$name: compiled loop cycles per iteration within 25 percent") {
      assertWithin(analysed(architecture, rows, measured.warps, shared, placement).cyclesPerIteration, measured.cycles)
    }
    measured.binding.foreach { expected =>
      test(s"$name: compiled loop binding") {
        assertEquals(analysed(architecture, rows, measured.warps, shared, placement).binding, expected)
      }
    }
    if direct then {
      test(s"$name: table latency cycles per iteration within 25 percent") {
        assertWithin(simulated(architecture, rows, measured.warps, placement).cyclesPerIteration, measured.cycles)
      }
      measured.binding.foreach { expected =>
        test(s"$name: table latency binding") {
          assertEquals(simulated(architecture, rows, measured.warps, placement).binding, expected)
        }
      }
    }
  }

  private def expectationsFollowRule(architecture: Architecture, cases: Vec[Measured]): Unit =
    test(s"${architecture.name} expected bindings follow the 25 percent rule") {
      val broken = cases.groupBy(_.kernel).values.flatMap { rows =>
        val floor = rows.minBy(_.cycles)
        floor.binding.filter(_ != Binding.Path).fold(Vec.empty[Measured]) { saturated =>
          rows.filter { row =>
            val expected = if (row.cycles > floor.cycles * (1 + tolerance)) Binding.Path else saturated
            row.binding.exists(_ != expected)
          }
        }
      }
      assert(broken.isEmpty, broken.map(row => s"${row.kernel} with ${row.warps} warps").mkString(", "))
    }

  register(Architecture.sm89, HardwareLoops.sm89, sm89Cases, sm89Shared)
  register(Architecture.gfx1036, HardwareLoops.gfx1036, gfx1036Cases, gfx1036Shared)
  expectationsFollowRule(Architecture.sm89, sm89Cases)
  expectationsFollowRule(Architecture.gfx1036, gfx1036Cases)
}
