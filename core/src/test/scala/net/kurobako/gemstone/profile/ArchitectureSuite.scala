package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class ArchitectureSuite extends Suite {
  import Architecture.*

  private val probed     = Source.Probed("probe", "card", "driver", "toolkit")
  private val documented = Source.Documented("document")
  private val published  = Source.Published("citation", "gpu")
  private val compiled   = Source.Compiled("ptxas", "cuda_13.3.r13.3/compiler.38244171_0", "sm_90")
  private val inherited  = Source.Inherited("sm_89", "same multiprocessor")
  private val estimate   = Source.Estimate("basis")

  private def known(name: String): Architecture = Architecture.named(name).getOrElse(fail(s"no architecture $name"))

  test("a target with a card of its own silicon reports its memory and DRAM rate per multiprocessor clock") {
    val sm89   = known("sm_89")
    val memory = sm89.memory.getOrElse(fail("no memory"))
    assertEquals((memory.busWidthBits.value, memory.l2Bytes.value), (128, 32L << 20))
    assertEquals(memory.busWidthBits.source.kind, Source.Estimate("").kind)
    assert(memory.l2Bytes.source.detail.contains("RTX 4060 Ti"))
    val dram = sm89.throughput.dram.getOrElse(fail("no DRAM rate"))
    assertEqualsDouble(dram.value, 288.0 * 1000.0 / (34 * 2535), 1e-9)
    assert(dram.source.detail.contains("34 multiprocessors at 2535 MHz"))
    assertEquals(Architecture.nvidiaDefault.memory, sm89.memory)
  }

  test("rdna targets report the memory of a shipping card of their own silicon") {
    Vec(
      "gfx1011" -> (2048, 4L << 20),
      "gfx1012" -> (128, 2L << 20),
      "gfx1030" -> (256, 4L << 20),
      "gfx1031" -> (192, 3L << 20),
      "gfx1100" -> (384, 6L << 20),
      "gfx1101" -> (256, 4L << 20),
      "gfx1102" -> (128, 2L << 20)
    ).foreach { (name, expected) =>
      val memory = known(name).memory.getOrElse(fail(s"$name has no memory"))
      assertEquals((memory.busWidthBits.value, memory.l2Bytes.value), expected)
    }
  }

  test("a target derived from a carded one carries no card of its own") {
    assertEquals(known("sm_86").memory, None)
    assertEquals(known("sm_86").throughput.dram, None)
  }

  test("a shared-memory target has no device memory") {
    assertEquals(known("gfx1036").memory, None)
    assertEquals(known("gfx1036").throughput.dram, None)
    assertEquals(Architecture.amdgpuDefault.memory, None)
  }

  test("sources order strongest first") {
    assertEquals(
      Vec(estimate, inherited, compiled, published, documented, probed).sorted,
      Vec(probed, documented, published, compiled, inherited, estimate)
    )
  }

  test("the weakest of several sources is the least trusted") {
    assertEquals(Source.weakest(probed), probed)
    assertEquals(Source.weakest(probed, documented, probed), documented)
    assertEquals(Source.weakest(documented, published, probed), published)
    assertEquals(Source.weakest(documented, estimate, inherited), estimate)
  }

  test("each source names its kind") {
    assertEquals(
      Vec(probed, documented, published, compiled, inherited, estimate).map(_.kind),
      Vec("probed", "documented", "published", "compiled", "inherited", "estimate")
    )
  }

  test("each source details its provenance") {
    assertEquals(published.detail, "citation, measured on gpu")
    assertEquals(compiled.detail, "ptxas cuda_13.3.r13.3/compiler.38244171_0, target sm_90")
    assertEquals(inherited.detail, "from sm_89, same multiprocessor")
    assertEquals(estimate.detail, "basis")
  }

  test("every table is sourced and named once") {
    assertEquals(Architecture.all.map(_.name).distinct.size, Architecture.all.size)
    assertEquals(Architecture.all.flatMap(ArchitectureChecks.problems), Vec.empty[String])
  }

  test("aliases resolve to a table") {
    assert(Architecture.aliases.values.forall(Architecture.named(_).isDefined))
  }

  test("unlisted processors have no table") {
    assertEquals(Vec("gfx800", "gfx1001").map(Architecture.named), Vec(None, None))
  }

  test("sibling processors have tables") {
    assert(Vec("gfx805", "gfx902", "gfx909", "gfx1154", "gfx1171", "gfx1172").forall(Architecture.named(_).isDefined))
  }

  test("a derived architecture inherits every value from its origin") {
    val derived = Architecture.sm89.derived("sm_x", "same design")
    assertEquals(derived.name, "sm_x")
    assertEquals(
      ArchitectureChecks.values(derived).map(_.source).toSet,
      Set[Source](
        Source.Inherited("sm_89", "same design"),
        Source.Estimate(Architecture.sm89.latency.dram.source.detail)
      )
    )
    val withoutCard = Architecture.sm89.copy(throughput = Architecture.sm89.throughput.copy(dram = None), memory = None)
    assertEquals(
      ArchitectureChecks.values(derived).map(_.value),
      ArchitectureChecks.values(withoutCard).map(_.value)
    )
  }

  private def assertMeasured(sourced: Sourced[Double], measured: Double): Unit =
    assertMeasured(Some(sourced), measured)

  private def assertMeasured(sourced: Option[Sourced[Double]], measured: Double): Unit = {
    assertEqualsDouble(sourced.fold(0.0)(_.value), measured, measured * 0.01)
    assertEquals(sourced.map(_.source.kind), Some("probed"))
  }

  test("sm_89 mufu rate matches its probe") {
    assertMeasured(Architecture.sm89.throughput.mufu, 15.9601)
  }

  test("sm_89 tensor rate matches its probe") {
    assertMeasured(Architecture.sm89.throughput.tensor, 511.2998)
  }

  test("sm_89 shared latency matches its probe") {
    assertMeasured(Architecture.sm89.latency.shared, 24.00)
  }

  test("sm_89 l1 latency matches its probe") {
    assertMeasured(Architecture.sm89.latency.l1, 39.97)
  }

  test("sm_89 l2 latency matches its probe") {
    assertMeasured(Architecture.sm89.latency.l2, 269.46)
  }

  test("sm_89 dram latency matches its probe") {
    assertMeasured(Architecture.sm89.latency.dram, 628.29)
  }

  test("sm_89 load rate matches its probe") {
    assertMeasured(Architecture.sm89.throughput.loads, 0.5)
  }

  test("sm_86, sm_87 and sm_88 inherit the sm_89 load rate") {
    val loadRate = Some((0.5, "inherited"))
    assertEquals(
      Vec(Architecture.sm86, Architecture.sm87, Architecture.sm88)
        .map(_.throughput.loads.map(sourced => (sourced.value, sourced.source.kind))),
      Vec(loadRate, loadRate, loadRate)
    )
  }

  test("sm_89 shares its fp64 pipe across partitions and its siblings inherit that") {
    assertEquals(Architecture.sm89.sharedPipes.keySet, Set(Pipe.Fp64))
    assertEquals(Architecture.sm89.sharedPipes(Pipe.Fp64).kind, "probed")
    assertEquals(Architecture.sm86.sharedPipes.view.mapValues(_.kind).toMap, Map(Pipe.Fp64 -> "inherited"))
    assertEquals(Architecture.sm90.sharedPipes, Map.empty[Pipe, Source])
  }

  test("sm_89 charges the constant-bank DFMA interval its probe measured") {
    val rate = Architecture.sm89.throughput.fp64Constant.getOrElse(fail("no constant fp64 rate"))
    assertEqualsDouble(32.0 / rate.value, 19.0, 1e-9)
    assertEquals(rate.source.kind, "probed")
    assertEquals(Architecture.sm86.throughput.fp64Constant.map(_.source.kind), Some("inherited"))
  }

  test("gfx1036 fma dependent latency is the chain probe, and its siblings have none") {
    assertEquals(Architecture.gfx1036.pipeLatency.view.mapValues(_.value).toMap, Map(Pipe.Fma -> 5))
    assertEquals(Architecture.gfx1036.pipeLatency(Pipe.Fma).source.kind, "probed")
    assertEquals(Architecture.gfx1030.pipeLatency, Map.empty[Pipe, Sourced[Int]])
  }

  test("gfx1036 fp64 rate matches its probe") {
    assertMeasured(Architecture.gfx1036.throughput.fp64, 7.9996)
  }

  test("gfx1036 shared latency matches its probe") {
    assertMeasured(Architecture.gfx1036.latency.shared, 54.0034)
  }

  test("gfx1036 l1 latency matches its probe") {
    assertMeasured(Architecture.gfx1036.latency.l1, 114.0037)
  }

  test("gfx1036 l2 latency matches its probe") {
    assertMeasured(Architecture.gfx1036.latency.l2, 232.0237)
  }

  test("gfx1036 dram latency matches its probe") {
    assertMeasured(Architecture.gfx1036.latency.dram, 595.7363)
  }

  test("gfx90a latency is the MI210 measurement") {
    val latency = Architecture.gfx90a.latency
    assertEquals(latency.shared.value, 55.0)
    assertEquals(latency.l1.map(_.value), Some(125.0))
    assertEquals(latency.l2.map(_.value), Some(310.0))
    assertEquals(latency.dram.value, 748.0)
    assertEquals(latency.dram.source.kind, "published")
    assert(latency.dram.source.detail.contains("AMD Instinct MI210"))
  }

  test("gcn latency is an estimate from the MI210 measurement") {
    Vec(Architecture.gfx906, Architecture.gfx908).foreach { a =>
      assertEquals(a.latency.shared.value, 55.0)
      assertEquals(a.latency.l1.map(_.value), Some(125.0))
      assertEquals(a.latency.l2.map(s => s.value -> s.source.kind), Some(310.0 -> "estimate"))
      assertEquals(a.latency.dram.value, 748.0)
      assertEquals(a.latency.dram.source.kind, "estimate")
      assert(a.latency.dram.source.detail.contains("AMD Instinct MI210"))
    }
  }

  test("cdna3 and cdna4 latency inherits shared and l1 and estimates l2 and dram from the MI210") {
    Vec(Architecture.gfx940, Architecture.gfx941, Architecture.gfx942, Architecture.gfx950).foreach { a =>
      assertEquals(a.latency.shared.source.kind, "inherited")
      assertEquals(a.latency.l1.map(_.source.kind), Some("inherited"))
      assertEquals(a.latency.l2.map(s => s.value -> s.source.kind), Some(310.0 -> "estimate"))
      assertEquals(a.latency.dram.source.kind, "estimate")
      assert(a.latency.dram.source.detail.contains("AMD Instinct MI210"))
    }
  }

  test("sm_120 l2 and dram latency are published from the RTX 5080") {
    val latency = Architecture.sm120.latency
    assertEquals(latency.l2.map(_.source.kind), Some("published"))
    assertEquals(latency.dram.source.kind, "published")
    assert(latency.dram.source.detail.contains("RTX 5080"))
    assertEquals(latency.shared.source.kind, "estimate")
    assertEquals(latency.l1.map(_.source.kind), Some("estimate"))
  }

  test("sm_121 l2 and dram latency are estimates naming the RTX 5080") {
    val latency = Architecture.sm121.latency
    assertEquals(latency.shared.source.kind, "inherited")
    assertEquals(latency.l2.map(_.source.kind), Some("estimate"))
    assertEquals(latency.dram.source.kind, "estimate")
    assert(latency.dram.source.detail.contains("RTX 5080"))
  }

  test("gfx1030 latency is an estimate from the gfx1036 probe") {
    val latency = Architecture.gfx1030.latency
    assertEquals(latency.dram.value, 595.7363)
    assertEquals(latency.dram.source.kind, "estimate")
    assert(latency.dram.source.detail.contains("9950X3D"))
  }

  test("sm_72 fp64 rate is the documented 32 lanes") {
    assertEquals(Architecture.sm72.throughput.fp64.value, 32.0)
    assertEquals(Architecture.sm72.throughput.fp64.source.kind, "documented")
  }

  test("sm_103 and sm_110 fp64 rates are documented at 2 lanes") {
    Vec(Architecture.sm103, Architecture.sm110).foreach { a =>
      assertEquals(a.throughput.fp64.value, 2.0)
      assertEquals(a.throughput.fp64.source.kind, "documented")
    }
  }

  test("sm_37 fma rate cites the Programming Guide 11.8 table 3") {
    assert(Architecture.sm37.throughput.fma.source.detail.contains("Programming Guide 11.8, Table 3"))
  }

  test("sm_100 fma rate cites the Best Practices Guide 13 table 5") {
    assert(Architecture.sm100.throughput.fma.source.detail.contains("Best Practices Guide 13, Table 5"))
  }

  test("kepler shared memory banks are 8 bytes wide") {
    assertEquals(
      Vec(Architecture.sm35, Architecture.sm37).map(a => a.name -> a.shared.bankBytes.value),
      Vec("sm_35" -> 8, "sm_37" -> 8)
    )
  }

  test("maxwell and pascal issue one instruction per scheduler") {
    assertEquals(
      Vec(
        Architecture.sm50,
        Architecture.sm52,
        Architecture.sm53,
        Architecture.sm60,
        Architecture.sm61,
        Architecture.sm62
      ).map(a => a.name -> a.throughput.issues.value),
      Vec("sm_50" -> 4.0, "sm_52" -> 4.0, "sm_53" -> 4.0, "sm_60" -> 2.0, "sm_61" -> 4.0, "sm_62" -> 4.0)
    )
  }

  test("kepler issue count is an estimate") {
    assertEquals(Architecture.sm37.throughput.issues.source.kind, "estimate")
  }

  test("maxwell and pascal register partitions hold 16384 registers") {
    assertEquals(
      Vec(
        Architecture.sm50,
        Architecture.sm52,
        Architecture.sm53,
        Architecture.sm61,
        Architecture.sm62
      ).map(a => a.name -> a.limits.partitionRegisters.value),
      Vec("sm_50" -> 16384, "sm_52" -> 16384, "sm_53" -> 16384, "sm_61" -> 16384, "sm_62" -> 16384)
    )
  }

  test("ampere orin shared memory is 164 KiB, 163 KiB per workgroup") {
    assertEquals(
      Vec(Architecture.sm87, Architecture.sm88).map(a =>
        a.name -> (a.limits.shared.value, a.limits.workgroupShared.value)
      ),
      Vec("sm_87" -> (167936, 166912), "sm_88" -> (167936, 166912))
    )
  }

  test("southern islands shared memory is 32 KiB") {
    assertEquals(
      Vec(Architecture.gfx600, Architecture.gfx601, Architecture.gfx602).map(a => a.name -> a.limits.shared.value),
      Vec("gfx600" -> 32768, "gfx601" -> 32768, "gfx602" -> 32768)
    )
  }

  test("gfx125x lane registers are 1024") {
    assertEquals(
      Vec(Architecture.gfx1250, Architecture.gfx1251).map(a => a.name -> a.limits.laneRegisters.value),
      Vec("gfx1250" -> 1024, "gfx1251" -> 1024)
    )
  }

  test("rdna3 family fp64 is four multiply-adds per multiprocessor") {
    assertEquals(
      Vec(
        Architecture.gfx1100,
        Architecture.gfx1101,
        Architecture.gfx1102,
        Architecture.gfx1103,
        Architecture.gfx1150,
        Architecture.gfx1151,
        Architecture.gfx1152,
        Architecture.gfx1153,
        Architecture.gfx1154,
        Architecture.gfx1170,
        Architecture.gfx1171,
        Architecture.gfx1172
      ).map(a => a.name -> a.throughput.fp64.value),
      Vec(
        "gfx1100",
        "gfx1101",
        "gfx1102",
        "gfx1103",
        "gfx1150",
        "gfx1151",
        "gfx1152",
        "gfx1153",
        "gfx1154",
        "gfx1170",
        "gfx1171",
        "gfx1172"
      ).map(_ -> 4.0)
    )
  }

  test("fp64 rates of gfx1150, gfx1200 and gfx1250 are estimates") {
    assertEquals(
      Vec(Architecture.gfx1150, Architecture.gfx1200, Architecture.gfx1250).map(a =>
        a.name -> (a.throughput.fp64.value, a.throughput.fp64.source.kind)
      ),
      Vec("gfx1150" -> (4.0, "estimate"), "gfx1200" -> (8.0, "estimate"), "gfx1250" -> (8.0, "estimate"))
    )
  }

  test("gfx125x cache line sizes are estimates") {
    assertEquals(
      Vec(Architecture.gfx1250, Architecture.gfx1251).map(a =>
        a.name -> Vec(a.global.sectorBytes, a.global.lineBytes).map(s => (s.value, s.source.kind))
      ),
      Vec(
        "gfx1250" -> Vec(128 -> "estimate", 128 -> "estimate"),
        "gfx1251" -> Vec(128 -> "inherited", 128 -> "inherited")
      )
    )
  }

  test("gfx1201 matrix rate is an estimate of its own") {
    assertEquals(
      Vec(Architecture.gfx1200, Architecture.gfx1201).map(a =>
        a.name -> (a.throughput.tensor.value, a.throughput.tensor.source.kind)
      ),
      Vec("gfx1200" -> (2048.0, "estimate"), "gfx1201" -> (2048.0, "estimate"))
    )
  }

  test("rdna2 sector size is an estimate") {
    assertEquals(
      Vec(Architecture.gfx1030, Architecture.gfx1036).map(a =>
        a.name -> (a.global.sectorBytes.value, a.global.sectorBytes.source.kind)
      ),
      Vec("gfx1030" -> (128, "estimate"), "gfx1036" -> (128, "estimate"))
    )
  }

  test("gfx8 cache lines are documented") {
    val lineBytes = (64, "documented")
    assertEquals(
      Vec(Architecture.gfx801, Architecture.gfx802, Architecture.gfx803, Architecture.gfx810).map(a =>
        a.name -> (a.global.lineBytes.value, a.global.lineBytes.source.kind)
      ),
      Vec("gfx801", "gfx802", "gfx803", "gfx810").map(_ -> lineBytes)
    )
  }

  test("unreachable cache geometry is an estimate") {
    val estimates = Vec("estimate", "estimate")
    assertEquals(
      Vec(Architecture.gfx906, Architecture.gfx942, Architecture.gfx1030).map(a =>
        a.name -> Vec(a.global.sectorBytes, a.global.lineBytes).map(_.source.kind)
      ),
      Vec("gfx906", "gfx942", "gfx1030").map(_ -> estimates)
    )
  }

  test("unreachable bank widths are estimates") {
    assertEquals(
      Vec(
        Architecture.gfx600,
        Architecture.gfx601,
        Architecture.gfx602,
        Architecture.gfx700,
        Architecture.gfx906,
        Architecture.gfx1030
      ).map(a => a.name -> a.shared.bankBytes.source.kind),
      Vec("gfx600", "gfx601", "gfx602", "gfx700", "gfx906", "gfx1030").map(_ -> "estimate")
    )
  }

  test("unreachable fp64 rates are estimates") {
    assertEquals(
      Vec(
        Architecture.gfx906,
        Architecture.gfx908,
        Architecture.gfx90a,
        Architecture.gfx942,
        Architecture.gfx950,
        Architecture.gfx1030
      ).map(a => a.name -> a.throughput.fp64.source.kind),
      Vec("gfx906", "gfx908", "gfx90a", "gfx942", "gfx950", "gfx1030").map(_ -> "estimate")
    )
  }

  test("unreachable matrix rates are estimates") {
    assertEquals(
      Vec(Architecture.gfx908, Architecture.gfx90a, Architecture.gfx942, Architecture.gfx950).map(a =>
        a.name -> a.throughput.tensor.source.kind
      ),
      Vec("gfx908", "gfx90a", "gfx942", "gfx950").map(_ -> "estimate")
    )
  }

  test("unreachable issue and pipe rates are estimates") {
    assertEquals(
      Vec(
        Architecture.gfx906.throughput.issues,
        Architecture.gfx906.throughput.fma,
        Architecture.gfx906.throughput.alu,
        Architecture.gfx906.throughput.loadStore,
        Architecture.gfx906.throughput.shared,
        Architecture.gfx1030.throughput.fma,
        Architecture.gfx1150.throughput.fma
      ).map(_.source.kind),
      Vec("estimate", "estimate", "estimate", "estimate", "estimate", "estimate", "estimate")
    )
  }

  private case class Pair(first: Architecture.Sourced[String], second: Architecture.Sourced[Double])
  private case class Nested(
      pair: Pair,
      present: Option[Architecture.Sourced[Int]],
      absent: Option[Architecture.Sourced[Int]],
      listed: Vec[Architecture.Sourced[Long]]
  )

  test("values finds every sourced value in nested products, options and collections") {
    val nested = Nested(
      Pair(Sourced("a", probed), Sourced(1.5, documented)),
      Some(Sourced(2, estimate)),
      None,
      Vec(Sourced(3L, published))
    )
    assertEquals(ArchitectureChecks.values(nested).map(_.source), Vec(probed, documented, estimate, published))
  }
}
