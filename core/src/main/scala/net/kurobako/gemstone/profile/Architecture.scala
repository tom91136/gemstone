package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.string.*

object Architecture {

  object Source {

    given Ordering[Source] = Ordering.by(_.ordinal)

    /** The least trusted of the sources given. */
    def weakest(first: Source, rest: Source*): Source = (first +: rest).max
  }

  /** Where a table value comes from. The cases are declared strongest first, and `Ordering[Source]` follows that
    * declaration order.
    */
  enum Source derives CanEqual {

    /** Measured by a probe on `card`, with the driver and toolkit it ran under. */
    case Probed(probe: String, card: String, driver: String, toolkit: String)

    /** Stated in the vendor's documentation, named by `document`. */
    case Documented(document: String)

    /** Measured by a third party: `citation` names the publication, `measured` the hardware it measured. */
    case Published(citation: String, measured: String)

    /** Measured or documented for the target `from`, carried over here for the reason in `basis`. */
    case Inherited(from: String, basis: String)

    /** Neither documented nor measured; `basis` says what the value rests on. */
    case Estimate(basis: String)

    def kind: String = productPrefix.toLowerCaseRoot

    def detail: String = this match {
      case Probed(probe, card, driver, toolkit) => s"$probe on $card, driver $driver, $toolkit"
      case Documented(document)                 => document
      case Published(citation, measured)        => s"$citation, measured on $measured"
      case Inherited(from, basis)               => s"from $from, $basis"
      case Estimate(basis)                      => basis
    }
  }

  /** A table value and where it comes from. */
  case class Sourced[A](value: A, source: Source) derives CanEqual

  /** How lane addresses to device memory become transactions. `lineBytes` and `sectorBytes` describe the first-level
    * cache that serves global loads: the vector L1 on AMD, the L1 on NVIDIA.
    */
  case class Global(sectorBytes: Sourced[Int], lineBytes: Sourced[Int]) derives CanEqual

  /** How shared memory or LDS serves a request: banks of `bankBytes` each, one word per bank per wavefront. */
  case class Banks(count: Sourced[Int], bankBytes: Sourced[Int]) derives CanEqual

  /** What one multiprocessor (an SM, a WGP, or a CU) serves per cycle, which turns the waste of a profile into cycles
    * the resource spends on it. `issues` counts subgroup instructions issued across its schedulers or SIMDs. The
    * compute pipes count lane results per cycle: `fma` for FP32 multiply-add, `alu` for 32-bit integer and logic,
    * `mufu` for transcendentals, `fp64` for double-precision multiply-add, and `tensor` for dense 16-bit
    * multiply-accumulates of the matrix units, zero where there are none. `loadStore` counts global sectors through the
    * L1 or L0 path and `shared` counts shared memory or LDS wavefronts, both reached with 8-byte full-warp loads.
    * `loads` counts warp load instructions per cycle for loads of up to 8 bytes per lane. 16-byte loads take 4 clocks
    * at any lane count on sm_89, so at 16 active lanes the bound understates them. These are throughput bounds of an
    * otherwise idle multiprocessor, not timing.
    */
  case class Throughput(
      issues: Sourced[Double],
      fma: Sourced[Double],
      alu: Sourced[Double],
      mufu: Sourced[Double],
      fp64: Sourced[Double],
      tensor: Sourced[Double],
      loadStore: Sourced[Double],
      shared: Sourced[Double],
      loads: Option[Sourced[Double]] = None,
      dram: Option[Sourced[Double]] = None
  ) derives CanEqual

  /** Load-to-use cycles of a dependent chain at each memory tier. A tier is absent when no source gives its cycles.
    * `l2` and `dram` describe one named card; a sibling of that card carries an estimate naming it.
    */
  case class Latency(
      shared: Sourced[Double],
      l1: Option[Sourced[Double]],
      l2: Option[Sourced[Double]],
      dram: Sourced[Double]
  ) derives CanEqual

  /** The device memory of the reference card a target's values describe, as the device reports it. */
  case class Memory(busWidthBits: Sourced[Int], l2Bytes: Sourced[Long]) derives CanEqual

  enum WorkgroupCap derives CanEqual {
    case AllWorkgroups, MultiSubgroupWorkgroups
  }

  /** What one multiprocessor (an SM, a WGP, or a CU) holds at once. Its register file is split into `partitions`, one
    * per scheduler, and a subgroup takes registers in lane-register granules. `shared` is the addressable size of
    * shared memory or LDS, not necessarily the physical array. Shared memory is allocated per workgroup in granules,
    * after a reservation the runtime makes for every workgroup. `workgroups` caps resident workgroups, and
    * `workgroupCap` says which: NVIDIA caps every workgroup, while AMD caps only workgroups of more than one subgroup,
    * since a single wave needs no barrier (LLVM's `getMaxWorkGroupsPerCU`).
    */
  case class Limits(
      subgroups: Sourced[Int],
      workgroups: Sourced[Int],
      workgroupCap: WorkgroupCap,
      workgroupLanes: Sourced[Int],
      partitions: Sourced[Int],
      partitionRegisters: Sourced[Int],
      registerGranule: Sourced[Int],
      laneRegisters: Sourced[Int],
      workgroupRegisters: Option[Sourced[Int]],
      shared: Sourced[Int],
      workgroupShared: Sourced[Int],
      reservedShared: Sourced[Int],
      sharedGranule: Sourced[Int]
  ) derives CanEqual

  private def guideLimits(cc: String) =
    Source.Documented(s"CUDA C++ Programming Guide 13, technical specifications for compute capability $cc")
  private def guideRates(cc: String) =
    Source.Documented(s"CUDA C++ Programming Guide 13, throughput of native arithmetic instructions for $cc")
  private val occupancyHeader = Source.Documented("cuda_occupancy.h, CUDA 13.3")
  private val sectors         = Source.Documented(
    "CUDA C++ Best Practices Guide 13, coalesced access to global memory: 32-byte sectors in 128-byte segments, " +
      "unchanged from Maxwell to Blackwell"
  )
  private val banks = Source.Documented(
    "CUDA C++ Programming Guide 13, shared memory: 32 banks of 4 bytes, unchanged from Maxwell to Blackwell"
  )
  private val keplerSectors = Source.Estimate(
    "32-byte L2 line of K80 in the same table; its 128-byte L1 load granularity is not used, so global loads are " +
      "assumed to bypass L1"
  )
  private val keplerBanks = Source.Documented(
    "CUDA C++ Programming Guide 11.8, appendix K.3.3 shared memory: 32 banks, with two addressing modes"
  )
  private val assumedL1 = Source.Estimate(
    "one 128-byte line (4 sectors) per cycle as probed on sm_89; no cited L1 rate for this multiprocessor"
  )
  private val sharedRate = Source.Documented(
    "CUDA C++ Programming Guide 13, shared memory: one 4-byte word per bank per cycle, so one wavefront per cycle"
  )
  private val noTensor =
    Source.Documented("CUDA C++ Programming Guide 13: matrix units first appear at compute capability 7.0")

  private def limits(
      cc: String,
      warps: Int,
      blocks: Int,
      registers: Int,
      blockRegisters: Int,
      partitions: Int,
      shared: Int,
      blockShared: Int,
      reserved: Int,
      granule: Int
  ): Limits = {
    val guide = guideLimits(cc)
    Limits(
      subgroups = Sourced(warps, guide),
      workgroups = Sourced(blocks, occupancyHeader),
      workgroupCap = WorkgroupCap.AllWorkgroups,
      workgroupLanes = Sourced(1024, guide),
      partitions = Sourced(partitions, occupancyHeader),
      partitionRegisters = Sourced(registers / partitions, guide),
      registerGranule = Sourced(256, occupancyHeader),
      laneRegisters = Sourced(255, guide),
      workgroupRegisters = Some(Sourced(blockRegisters, guide)),
      shared = Sourced(shared, guide),
      workgroupShared = Sourced(blockShared, guide),
      reservedShared = Sourced(reserved, if (reserved == 0) occupancyHeader else guide),
      sharedGranule = Sourced(granule, occupancyHeader)
    )
  }

  private def kib(n: Int): Int = n * 1024

  private def guideTable3(cc: String) =
    Source.Documented(s"CUDA C++ Programming Guide 11.8, Table 3, throughput of native arithmetic instructions for $cc")
  private def bestPracticesTable5(column: String) =
    Source.Documented(
      s"CUDA C++ Best Practices Guide 13, Table 5, throughput of native arithmetic instructions for $column"
    )
  private def guided(cc: String)(value: Double): Sourced[Double]     = Sourced(value, guideRates(cc))
  private def table3(cc: String)(value: Double): Sourced[Double]     = Sourced(value, guideTable3(cc))
  private def table5(column: String)(value: Double): Sourced[Double] = Sourced(value, bestPracticesTable5(column))
  private def rates(
      issues: Sourced[Double],
      fma: Sourced[Double],
      alu: Sourced[Double],
      mufu: Sourced[Double],
      fp64: Sourced[Double],
      tensor: Sourced[Double],
      loadStore: Sourced[Double] = Sourced(4.0, assumedL1)
  ): Throughput = Throughput(issues, fma, alu, mufu, fp64, tensor, loadStore, Sourced(1.0, sharedRate))

  private def schedulers(cc: String, count: Int, dispatch: Int): Sourced[Double] = Sourced(
    (count * dispatch).toDouble,
    Source.Documented(
      s"CUDA C++ Programming Guide 13, compute capability $cc: $count warp schedulers, each dispatching $dispatch " +
        "instruction(s) per cycle"
    )
  )

  private def published(citation: String, measured: String)(value: Double) =
    Sourced(value, Source.Published(citation, measured))

  private val jiaTuring =
    "Jia et al., Dissecting the NVidia Turing T4 GPU via Microbenchmarking, arXiv:1903.07486, 2019"
  private val jiaVolta =
    "Jia et al., Dissecting the NVIDIA Volta GPU Architecture via Microbenchmarking, arXiv:1804.06826, 2018"
  private val meiChu = "Mei and Chu, Dissecting GPU Memory Hierarchy through Microbenchmarking, IEEE TPDS 2017"
  private val luo = "Luo et al., Benchmarking and Dissecting the Nvidia Hopper GPU Architecture, arXiv:2402.13499, 2024"
  private val mt4g     = "MT4G, arXiv:2511.05958, 2025"
  private val jarmusch = "Jarmusch et al., arXiv:2507.10789, 2025"

  private val keplerLine = Sourced(128, Source.Published(s"$jiaVolta, Table 3.1, L1 line size", "Tesla K80, GK210"))
  private val gk110Dram  = published(s"$meiChu, Figure 14, pattern P4", "GTX 780, GK110")(339)

  private def jia(measured: String, shared: Double, l1: Double, l2: Double, dram: Sourced[Double]) = Latency(
    published(s"$jiaTuring, Table 3.1", measured)(shared),
    Some(published(s"$jiaTuring, Table 3.1", measured)(l1)),
    Some(published(s"$jiaTuring, Table 3.1", measured)(l2)),
    dram
  )

  private def luoLatency(measured: String, shared: Double, l1: Double, l2: Double, dram: Double) = {
    def cited(value: Double) = published(s"$luo, Table IV", measured)(value)
    Latency(cited(shared), Some(cited(l1)), Some(cited(l2)), cited(dram))
  }

  private def estimatedLatency(basis: String, shared: Double, l1: Double, l2: Double, dram: Double) = {
    val source = Source.Estimate(basis)
    Latency(Sourced(shared, source), Some(Sourced(l1, source)), Some(Sourced(l2, source)), Sourced(dram, source))
  }

  private val nearestDramEstimate = Source.Estimate(
    "no DRAM latency for this chip in the cited tables; the GTX 980 figure of Mei and Chu (Figure 14, pattern P4 " +
      "with L1 off) as the nearest measured value"
  )

  private val keplerBankWidth = Source.Documented(
    "CUDA C++ Programming Guide 11.8, appendix K.3: 64 bits per bank for compute capability 3.x; " +
      "Jia et al., arXiv:1903.07486, Table 3.1 gives 8 bytes for Tesla K80, GK210"
  )

  val sm37: Architecture = Architecture(
    "sm_37",
    Global(Sourced(32, keplerSectors), keplerLine),
    Banks(Sourced(32, keplerBanks), Sourced(8, keplerBankWidth)),
    limits(
      "3.7",
      warps = 64,
      blocks = 16,
      registers = 131072,
      blockRegisters = 65536,
      partitions = 4,
      shared = kib(112),
      blockShared = kib(48),
      reserved = 0,
      granule = 256
    ),
    rates(
      Sourced(
        8.0,
        Source.Estimate(
          "four warp schedulers with two dispatch units each on the Kepler SMX; no fetched source gives the issue count"
        )
      ),
      fma = table3("3.5 and 3.7")(192),
      alu = table3("3.5 and 3.7")(160),
      mufu = table3("3.5 and 3.7")(32),
      fp64 = table3("3.5 and 3.7")(64),
      tensor = Sourced(0.0, noTensor)
    ),
    jia(
      "Tesla K80, GK210",
      26,
      35,
      200,
      gk110Dram.copy(source =
        Source.Inherited("sm_35", "K80 DRAM latency is not in the cited tables; GK210 is a GK110 revision")
      )
    )
  )

  val sm35: Architecture = {
    val base = sm37.derived(
      "sm_35",
      "GK110 and GK210 share the Kepler SMX and its memory path; only register file and shared memory differ"
    )
    base.copy(
      limits = limits("3.5", 64, 16, 65536, 65536, 4, kib(48), kib(48), 0, 256),
      latency = base.latency.copy(
        l2 = Some(published(s"$meiChu, Figure 14, pattern P1 (L2 hit)", "GTX 780, GK110")(198)),
        dram = gk110Dram
      )
    )
  }.on(Card("GeForce GTX 780", 384, 1536L, Some(288.4), Some(12), Some(902)))

  val sm52: Architecture = Architecture(
    "sm_52",
    Global(Sourced(32, sectors), Sourced(128, sectors)),
    Banks(Sourced(32, banks), Sourced(4, banks)),
    limits(
      "5.2",
      warps = 64,
      blocks = 32,
      registers = 65536,
      blockRegisters = 65536,
      partitions = 4,
      shared = kib(96),
      blockShared = kib(48),
      reserved = 0,
      granule = 256
    ),
    rates(
      schedulers("5.2", count = 4, dispatch = 1),
      fma = table3("5.0, 5.2 and 5.3")(128),
      alu = table3("5.0, 5.2 and 5.3")(128),
      mufu = table3("5.0, 5.2 and 5.3")(32),
      fp64 = table3("5.0, 5.2 and 5.3")(4),
      tensor = Sourced(0.0, noTensor)
    ),
    jia(
      "Tesla M60, GM204",
      23,
      82,
      207,
      published(s"$meiChu, Figure 14, pattern P4 with L1 off", "GTX 980, GM204")(383)
    )
  ).on(Card("GeForce GTX 980", 256, 2048L, Some(224.4), Some(16), Some(1216)))

  val sm50: Architecture = sm52
    .derived("sm_50", "GM107 and GM204 are the same Maxwell SMM; only the shared memory size differs")
    .copy(limits = limits("5.0", 64, 32, 65536, 65536, 4, kib(64), kib(48), 0, 256))

  val sm53: Architecture = sm52
    .derived("sm_53", "Tegra X1 uses the Maxwell SMM of GM204 with smaller shared memory")
    .copy(limits = limits("5.3", 64, 32, 65536, 32768, 4, kib(64), kib(48), 0, 256))

  val sm60: Architecture = Architecture(
    "sm_60",
    Global(Sourced(32, sectors), Sourced(128, sectors)),
    Banks(Sourced(32, banks), Sourced(4, banks)),
    limits(
      "6.0",
      warps = 64,
      blocks = 32,
      registers = 65536,
      blockRegisters = 65536,
      partitions = 2,
      shared = kib(64),
      blockShared = kib(48),
      reserved = 0,
      granule = 256
    ),
    rates(
      schedulers("6.0", count = 2, dispatch = 1),
      fma = table3("6.0")(64),
      alu = table3("6.0")(64),
      mufu = table3("6.0")(16),
      fp64 = table3("6.0")(32),
      tensor = Sourced(0.0, noTensor)
    ),
    jia("Tesla P100, GP100", 24, 82, 234, Sourced(383.0, nearestDramEstimate))
  )

  val sm61: Architecture = Architecture(
    "sm_61",
    Global(Sourced(32, sectors), Sourced(128, sectors)),
    Banks(Sourced(32, banks), Sourced(4, banks)),
    limits(
      "6.1",
      warps = 64,
      blocks = 32,
      registers = 65536,
      blockRegisters = 65536,
      partitions = 4,
      shared = kib(96),
      blockShared = kib(48),
      reserved = 0,
      granule = 256
    ),
    rates(
      schedulers("6.1", count = 4, dispatch = 1),
      fma = table3("6.1 and 6.2")(128),
      alu = table3("6.1 and 6.2")(128),
      mufu = table3("6.1 and 6.2")(32),
      fp64 = table3("6.1 and 6.2")(4),
      tensor = Sourced(0.0, noTensor)
    ),
    jia("Tesla P4, GP104", 23, 82, 216, Sourced(383.0, nearestDramEstimate))
  ).on(Card("GeForce GTX 1080", 256, 2048L, Some(320.3), Some(20), Some(1733)))

  val sm62: Architecture = sm61
    .derived("sm_62", "Parker's GP10B is a GP10x SM with smaller shared memory")
    .copy(limits = limits("6.2", 64, 32, 65536, 32768, 4, kib(64), kib(48), 0, 256))

  private val voltaTensor = Source.Documented(
    "NVIDIA Tesla V100 GPU architecture whitepaper, 2017: 8 tensor cores per SM, 64 FP16 multiply-accumulates " +
      "per cycle each"
  )
  private val turingTensor = Source.Documented(
    "NVIDIA Turing GPU architecture whitepaper, 2018: 8 tensor cores per SM, 64 FP16 multiply-accumulates " +
      "per cycle each"
  )
  private val a100Tensor = Source.Documented(
    "NVIDIA A100 tensor core GPU architecture whitepaper, 2020: 4 tensor cores per SM, 256 dense FP16 " +
      "multiply-accumulates per cycle each"
  )
  private val hopperTensor = Source.Documented(
    "NVIDIA H100 tensor core GPU architecture whitepaper, 2022: twice the dense MMA rate of the A100 SM, " +
      "clock for clock"
  )

  val sm70: Architecture = Architecture(
    "sm_70",
    Global(Sourced(32, sectors), Sourced(128, sectors)),
    Banks(Sourced(32, banks), Sourced(4, banks)),
    limits(
      "7.0",
      warps = 64,
      blocks = 32,
      registers = 65536,
      blockRegisters = 65536,
      partitions = 4,
      shared = kib(96),
      blockShared = kib(96),
      reserved = 0,
      granule = 256
    ),
    rates(
      schedulers("7.0", count = 4, dispatch = 1),
      fma = table3("7.x")(64),
      alu = table3("7.x to 9.0")(64),
      mufu = guided("7.0")(16),
      fp64 = table3("7.x")(32),
      tensor = Sourced(512.0, voltaTensor)
    ),
    Latency(
      published(s"$jiaTuring, Table 3.1", "Tesla V100, GV100")(19),
      Some(published(s"$jiaTuring, Table 3.1", "Tesla V100, GV100")(28)),
      Some(published(s"$jiaTuring, Table 3.1", "Tesla V100, GV100")(193)),
      published(s"$jiaVolta, Figure 3.2 (L2 miss, TLB hit)", "Tesla V100, GV100")(375)
    )
  ).on(Card("Quadro GV100", 4096, 6144L, Some(870.0), Some(80), Some(1627)))

  val sm72: Architecture = {
    val base = sm70.derived("sm_72", "Xavier's GV10B is a Volta SM")
    base.copy(throughput = base.throughput.copy(fp64 = table3("7.x")(32.0)))
  }

  val sm75: Architecture = Architecture(
    "sm_75",
    Global(Sourced(32, sectors), Sourced(128, sectors)),
    Banks(Sourced(32, banks), Sourced(4, banks)),
    limits(
      "7.5",
      warps = 32,
      blocks = 16,
      registers = 65536,
      blockRegisters = 65536,
      partitions = 4,
      shared = kib(64),
      blockShared = kib(64),
      reserved = 0,
      granule = 256
    ),
    rates(
      schedulers("7.5", count = 4, dispatch = 1),
      fma = table3("7.x")(64),
      alu = guided("7.5")(64),
      mufu = guided("7.5")(16),
      fp64 = table3("7.5")(2),
      tensor = Sourced(512.0, turingTensor)
    ),
    Latency(
      published(s"$jiaTuring, Table 3.1", "Tesla T4, TU104")(19),
      Some(published(s"$jiaTuring, Table 3.1", "Tesla T4, TU104")(32)),
      Some(published(s"$jiaTuring, Table 3.1", "Tesla T4, TU104")(188)),
      published(s"$jiaTuring, Figure 3.5 (L2 miss, TLB hit)", "Tesla T4, TU104")(296)
    )
  ).on(Card("GeForce RTX 2080", 256, 4096L, Some(448.0), Some(46), Some(1710)))

  val sm80: Architecture = Architecture(
    "sm_80",
    Global(Sourced(32, sectors), Sourced(128, sectors)),
    Banks(Sourced(32, banks), Sourced(4, banks)),
    limits(
      "8.0",
      warps = 64,
      blocks = 32,
      registers = 65536,
      blockRegisters = 65536,
      partitions = 4,
      shared = kib(164),
      blockShared = kib(163),
      reserved = 1024,
      granule = 128
    ),
    rates(
      schedulers("8.0", count = 4, dispatch = 1),
      fma = table3("8.0")(64),
      alu = guided("8.0")(64),
      mufu = table3("8.0 to 9.0")(16),
      fp64 = table3("8.0")(32),
      tensor = Sourced(1024.0, a100Tensor)
    ),
    luoLatency("A100 PCIe", 29.0, 37.9, 261.5, 466.3)
  )

  private val adaPeak = Source.Probed(
    "ncu peak_sustained of lsu data-stage wavefronts (4 sectors each, global and shared) and of sm instructions " +
      "issued, one profile_memory launch (Nextcloud probe kit)",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 615.78.08",
    "CUDA 13.3, ncu 2026.2.1"
  )
  private val cudaGuide89        = Source.Documented("CUDA C++ Programming Guide 13, technical specifications for 8.9")
  private val cudaBestPractice89 =
    Source.Documented("CUDA C++ Best Practices Guide 13, coalesced access to global memory")

  private val adaRate = Source.Probed(
    "ffma, mufu, dfma and hmma rate kernels, 34 blocks of 256 threads, median of 10 repeats (Nextcloud probe kit)",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 615.78.08",
    "CUDA 13.3"
  )
  private val adaLatency = Source.Probed(
    "dependent chains timed with clock64, median of 10 launches (Nextcloud probe kit)",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 615.78.08",
    "CUDA 13.3"
  )
  private val adaLoads = Source.Probed(
    "sweep3 warp load width sweep (Nextcloud probe kit profile-soraws-uk-sm_89-rate)",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 615.78.08",
    "CUDA 13.3, nvcc"
  )

  val sm89: Architecture = Architecture(
    "sm_89",
    Global(Sourced(32, cudaBestPractice89), Sourced(128, cudaBestPractice89)),
    Banks(Sourced(32, cudaGuide89), Sourced(4, cudaGuide89)),
    limits(
      "8.9",
      warps = 48,
      blocks = 24,
      registers = 65536,
      blockRegisters = 65536,
      partitions = 4,
      shared = 102400,
      blockShared = 101376,
      reserved = 1024,
      granule = 128
    ),
    rates(
      Sourced(4.0, adaPeak),
      fma = guided("8.9")(128),
      alu = guided("8.9")(64),
      mufu = guided("8.9")(16),
      fp64 = guided("8.9")(2),
      tensor = Sourced(512.0, adaRate),
      loadStore = Sourced(4.0, adaPeak)
    ).copy(shared = Sourced(1.0, adaPeak), mufu = Sourced(16.0, adaRate), loads = Some(Sourced(0.5, adaLoads))),
    Latency(
      Sourced(24.00, adaLatency),
      Some(Sourced(39.97, adaLatency)),
      Some(Sourced(269.46, adaLatency)),
      Sourced(628.29, adaLatency)
    )
  ).on(Card("GeForce RTX 4060 Ti", 128, 32768L, Some(288.0), Some(34), Some(2535)))

  val sm86: Architecture = sm89
    .derived(
      "sm_86",
      "GA10x and AD10x share the 128 KiB L1 and shared memory, 128 FP32 lanes per SM and GDDR memory system"
    )
    .copy(limits = limits("8.6", 48, 16, 65536, 65536, 4, 102400, 101376, 1024, 128))

  val sm87: Architecture = sm86
    .derived("sm_87", "Orin's GA10B is a GA10x SM")
    .copy(limits = limits("8.7", 48, 16, 65536, 65536, 4, kib(164), kib(163), 1024, 128))
    .copy(
      latency = estimatedLatency(
        "no published Orin measurement; the A100 row of Luo et al. as an Ampere order of magnitude",
        29.0,
        37.9,
        261.5,
        466.3
      )
    )

  val sm88: Architecture = sm87
    .derived("sm_88", "sm_88 has no published specification; the neighbouring Ampere-class Tegra part is assumed")

  private val hopperRates = rates(
    schedulers("9.0", count = 4, dispatch = 1),
    fma = table3("8.6 to 9.0")(128),
    alu = guided("9.0")(64),
    mufu = table3("8.0 to 9.0")(16),
    fp64 = table3("9.0")(64),
    tensor = Sourced(2048.0, hopperTensor)
  )

  val sm90: Architecture = Architecture(
    "sm_90",
    Global(Sourced(32, sectors), Sourced(128, sectors)),
    Banks(Sourced(32, banks), Sourced(4, banks)),
    limits(
      "9.0",
      warps = 64,
      blocks = 32,
      registers = 65536,
      blockRegisters = 65536,
      partitions = 4,
      shared = kib(228),
      blockShared = kib(227),
      reserved = 1024,
      granule = 128
    ),
    hopperRates,
    luoLatency("H800 PCIe", 29.0, 40.7, 263.0, 478.8)
  )

  private val blackwellTensor = Source.Estimate(
    "dense FP16 tensor peak of the B200 (2250 TFLOPS, recalled and not checked) over 148 SMs at about 1.85 GHz, " +
      "about 4096 multiply-accumulates per SM per cycle"
  )

  val sm100: Architecture = {
    val base =
      sm90.derived("sm_100", "the Blackwell SM keeps the Hopper pipe widths and arithmetic throughput table row")
    base.copy(
      limits = limits("10.0", 64, 32, 65536, 65536, 4, kib(228), kib(227), 1024, 128),
      throughput = rates(
        schedulers("10.0", 4, 1),
        fma = table5("9.0 and 10.0")(128),
        alu = guided("10.0")(64),
        mufu = table5("7.5 to 12.1")(16),
        fp64 = table5("10.0")(64),
        tensor = Sourced(4096.0, blackwellTensor)
      ),
      latency = estimatedLatency(
        "no published Blackwell measurement; the H800 row of Luo et al. as a Hopper-class order of magnitude",
        29.0,
        40.7,
        263.0,
        478.8
      )
    )
  }.on(Card("B200", 8192, 61440L))

  val sm101: Architecture = sm100
    .derived("sm_101", "same Blackwell SM; the resident block limit differs per cuda_occupancy.h")
    .copy(limits = limits("10.1", 64, 24, 65536, 65536, 4, kib(228), kib(227), 1024, 128))

  val sm103: Architecture = {
    val base = sm100.derived("sm_103", "same Blackwell SM")
    base.copy(
      limits = limits("10.3", 64, 32, 65536, 65536, 4, kib(228), kib(227), 1024, 128),
      throughput = base.throughput.copy(
        fp64 = Sourced(2.0, bestPracticesTable5("10.3"))
      )
    )
  }

  val sm110: Architecture = {
    val base = sm101.derived("sm_110", "sm_101 was renamed sm_110 in CUDA 13, the same SM")
    base.copy(
      limits = limits("11.0", 64, 24, 65536, 65536, 4, kib(228), kib(227), 1024, 128),
      throughput = base.throughput.copy(fp64 = Sourced(2.0, bestPracticesTable5("10.3 to 12.1")))
    )
  }

  private val consumerBlackwellTensor = Source.Estimate(
    "dense FP16 tensor peak of the RTX 5090 (419 TFLOPS, FP16 accumulate, recalled and not checked) over 170 SMs " +
      "at 2.41 GHz, about 512 multiply-accumulates per SM per cycle"
  )

  private val consumerBlackwellLatency = {
    val shared = Source.Estimate(
      "no published consumer Blackwell measurement; the RTX 4090 row of Luo et al. as an Ada-class order of magnitude"
    )
    def cited(value: Double) = published(jarmusch, "GeForce RTX 5080")(value)
    Latency(Sourced(30.1, shared), Some(Sourced(43.4, shared)), Some(cited(358)), cited(876.7))
  }

  val sm120: Architecture = Architecture(
    "sm_120",
    Global(Sourced(32, sectors), Sourced(128, sectors)),
    Banks(Sourced(32, banks), Sourced(4, banks)),
    limits(
      "12.0",
      warps = 48,
      blocks = 24,
      registers = 65536,
      blockRegisters = 65536,
      partitions = 4,
      shared = 102400,
      blockShared = 101376,
      reserved = 1024,
      granule = 128
    ),
    rates(
      schedulers("12.0", count = 4, dispatch = 1),
      fma = table5("8.6 to 12.1")(128),
      alu = guided("12.0")(64),
      mufu = table5("7.5 to 12.1")(16),
      fp64 = table5("10.3 to 12.1")(2),
      tensor = Sourced(512.0, consumerBlackwellTensor)
    ),
    consumerBlackwellLatency
  ).on(Card("GeForce RTX 5080", 256, 65536L, Some(960.0), Some(84), Some(2617)))

  val sm121: Architecture = sm120
    .derived("sm_121", "sm_121 is the same SM as sm_120 in a different package")

  private val nvidiaAll: Vec[Architecture] = Vec(
    sm35,
    sm37,
    sm50,
    sm52,
    sm53,
    sm60,
    sm61,
    sm62,
    sm70,
    sm72,
    sm75,
    sm80,
    sm86,
    sm87,
    sm88,
    sm89,
    sm90,
    sm100,
    sm101,
    sm103,
    sm110,
    sm120,
    sm121
  )

  private def llvm(what: String)         = Source.Documented(s"LLVM 23.1.0 AMDGPUBaseInfo and AMDGPU.td, $what")
  private def amdgpuTd(what: String)     = Source.Documented(s"LLVM 23.1.0 AMDGPU.td, $what")
  private def recalled(document: String) = Source.Estimate(s"recalled from $document; not read")
  private val rdna2Isa                   = "AMD RDNA2 Instruction Set Architecture reference guide, 2020"
  private val rdna3Isa                   = "AMD RDNA3 Instruction Set Architecture reference guide, 2023"
  private val rdnaWhitepaper             = "AMD RDNA architecture whitepaper, 2019"
  private val gcnWhitepaper              = "AMD Graphics Core Next architecture whitepaper, 2012"
  private val gcnIsa                     = "AMD Graphics Core Next 3 Instruction Set Architecture reference guide, 2016"
  private val siIsa                      = "AMD Southern Islands Series Instruction Set Architecture, 2012"
  private val gcn3Lds                    = Source.Documented(s"$gcnIsa, lines 920-924: 32 banks of 4 bytes")
  private val gcn3Lines                  = Source.Documented(s"$gcnIsa, lines 3645-3646: 64-byte L1 cache lines")
  private val volcanicLds = amdgpuTd("FeatureAddressableLocalMemorySize65536 in FeatureVolcanicIslands, line 1501")
  private val rdna2Timing = Source.Probed(
    "profile_timing.hip, s_memtime around dependent chains on one wave32 (Nextcloud probe kit)",
    "Ryzen 9 9950X3D integrated GPU, gfx1036, run as gfx1030",
    "amdgpu, ROCm 7.1.1 runtime",
    "ROCm 7.1.1 hipcc"
  )

  private val mi210Latency = {
    def cited(value: Double) = published(s"$mt4g, Table III", "AMD Instinct MI210")(value)
    Latency(cited(55), Some(cited(125)), Some(cited(310)), cited(748))
  }

  private val gcnLatency = {
    val source = Source.Estimate("AMD Instinct MI210 measurement (MT4G, Table III)")
    Latency(
      Sourced(mi210Latency.shared.value, source),
      mi210Latency.l1.map(tier => Sourced(tier.value, source)),
      mi210Latency.l2.map(tier => Sourced(tier.value, source)),
      Sourced(mi210Latency.dram.value, source)
    )
  }

  private def rdnaProbeLatency(source: Source) = Latency(
    Sourced(54.0034, source),
    Some(Sourced(114.0037, source)),
    Some(Sourced(232.0237, source)),
    Sourced(595.7363, source)
  )

  private def gcnLimits(
      waves: Int,
      registers: Int,
      granule: Int,
      lds: Int,
      ldsGranule: Int,
      ldsSource: Source
  ): Limits = Limits(
    subgroups = Sourced(waves, llvm("getMaxWavesPerEU times getEUsPerCU")),
    workgroups = Sourced(16, llvm("getMaxWorkGroupsPerCU, the barrier limit of a CU")),
    workgroupCap = WorkgroupCap.MultiSubgroupWorkgroups,
    workgroupLanes = Sourced(1024, llvm("getMaxFlatWorkGroupSize")),
    partitions = Sourced(4, llvm("getEUsPerCU, lines 1196-1214, four SIMDs per CU")),
    partitionRegisters = Sourced(
      registers,
      llvm("getTotalNumVGPRs, lines 1429-1438, architectural VGPRs only, 256 per lane before gfx90a")
    ),
    registerGranule = Sourced(granule, llvm("getVGPRAllocGranule times the wavefront size")),
    laneRegisters = Sourced(registers / 64, llvm("getAddressableNumVGPRs")),
    workgroupRegisters = None,
    shared = Sourced(lds, ldsSource),
    workgroupShared = Sourced(lds, ldsSource),
    reservedShared = Sourced(0, llvm("no per-workgroup LDS reservation")),
    sharedGranule = Sourced(ldsGranule, llvm(s"getLdsDwGranularity, the $lds-byte LDS row"))
  )

  private def gcnThroughput(fp64: Sourced[Double], tensor: Sourced[Double]) = Throughput(
    issues = Sourced(1.0, recalled(gcnWhitepaper)),
    fma = Sourced(64.0, recalled(gcnWhitepaper)),
    alu = Sourced(64.0, recalled(gcnWhitepaper)),
    mufu = Sourced(
      16.0,
      Source.Estimate("assumed quarter rate of FP32 for transcendental VALU operations; no rate is cited")
    ),
    fp64 = fp64,
    tensor = tensor,
    loadStore = Sourced(1.0, recalled(gcnWhitepaper)),
    shared = Sourced(1.0, recalled(gcnIsa))
  )

  private val noMatrix = Source.Documented("the ISA reference guide of this generation defines no matrix instructions")

  private def fp64Ratio(ratio: Int, basis: String) = Sourced(64.0 / ratio, Source.Estimate(basis))

  private val gcnSectors = recalled("AMD Graphics Core Next architecture whitepaper, 2012: 64-byte L1 cache lines")

  val gfx906: Architecture = Architecture(
    "gfx906",
    Global(Sourced(64, gcnSectors), Sourced(64, gcnSectors)),
    Banks(Sourced(32, llvm("FeatureLDSBankCount32 of ISA version 9.0")), Sourced(4, recalled(gcnIsa))),
    gcnLimits(
      waves = 40,
      registers = 16384,
      granule = 256,
      lds = 65536,
      ldsGranule = 512,
      ldsSource = amdgpuTd("FeatureAddressableLocalMemorySize65536 in FeatureISAVersion9_0_Common, line 1735")
    ),
    gcnThroughput(
      Sourced(
        32.0,
        Source.Estimate(
          "Wikipedia Graphics_Core_Next page, Vega 20 Radeon Instinct row: FP64 at half the FP32 rate; " +
            "the primary AMD source was unreachable"
        )
      ),
      Sourced(0.0, noMatrix)
    ),
    gcnLatency
  )

  private val gfx6Basis = "GFX6 keeps the GCN CU of four SIMD16s; only the LDS addressing and register file differ"

  private val gfx6Limits = gcnLimits(
    waves = 40,
    registers = 16384,
    granule = 256,
    lds = 32768,
    ldsGranule = 256,
    ldsSource = amdgpuTd("FeatureAddressableLocalMemorySize32768 in FeatureSouthernIslands, line 1467")
  )

  private def gfx6(name: String, ratio: Int, basis: String): Architecture =
    gfx906.derived(name, gfx6Basis).copy(
      limits = gfx6Limits,
      throughput = gfx906.throughput.copy(fp64 = fp64Ratio(ratio, basis)),
      shared = Banks(Sourced(32, llvm("FeatureLDSBankCount32 of ISA version 6.0")), Sourced(4, recalled(siIsa)))
    )

  private val consumerGcn = "consumer GCN parts run FP64 at 1/16 of FP32; the compute parts of this family are faster"

  val gfx600: Architecture = gfx6("gfx600", 4, "Tahiti runs FP64 at 1/4 of FP32")
  val gfx601: Architecture = gfx6("gfx601", 16, consumerGcn)
  val gfx602: Architecture = gfx6("gfx602", 16, consumerGcn)

  private val gfx7Limits = gcnLimits(
    waves = 40,
    registers = 16384,
    granule = 256,
    lds = 65536,
    ldsGranule = 512,
    ldsSource = amdgpuTd("FeatureAddressableLocalMemorySize65536 in FeatureSeaIslands, line 1483")
  )

  private def gfx7(name: String, banks: Int, ratio: Int, basis: String): Architecture =
    gfx906.derived(name, "GFX7 keeps the GCN CU of four SIMD16s; only LDS size and instruction set differ").copy(
      limits = gfx7Limits,
      throughput = gfx906.throughput.copy(fp64 = fp64Ratio(ratio, basis)),
      shared = Banks(
        Sourced(banks, llvm(s"FeatureLDSBankCount$banks of the ISA version of $name")),
        Sourced(4, recalled(gcnIsa))
      )
    )

  val gfx700: Architecture = gfx7("gfx700", banks = 32, ratio = 16, basis = consumerGcn)
  val gfx701: Architecture =
    gfx7(
      "gfx701",
      banks = 32,
      ratio = 8,
      basis = "Hawaii runs FP64 at 1/8 of FP32 on consumer boards and 1/2 on compute boards"
    )
  val gfx702: Architecture = gfx7("gfx702", banks = 16, ratio = 16, basis = consumerGcn)
  val gfx703: Architecture = gfx7("gfx703", banks = 16, ratio = 16, basis = consumerGcn)
  val gfx704: Architecture = gfx7("gfx704", banks = 32, ratio = 16, basis = consumerGcn)
  val gfx705: Architecture = gfx7("gfx705", banks = 16, ratio = 16, basis = consumerGcn)

  private def gfx8(name: String, banks: Int, ratio: Int): Architecture = {
    val base = gfx906.derived(name, "GFX8 keeps the GCN CU of four SIMD16s with 64 KiB of LDS")
    base.copy(
      limits = base.limits.copy(
        shared = Sourced(65536, volcanicLds),
        workgroupShared = Sourced(65536, volcanicLds)
      ),
      global = base.global.copy(lineBytes = Sourced(64, gcn3Lines)),
      throughput = gfx906.throughput.copy(fp64 = fp64Ratio(ratio, consumerGcn)),
      shared = Banks(
        Sourced(banks, llvm(s"FeatureLDSBankCount$banks of the ISA version of $name")),
        Sourced(4, gcn3Lds)
      )
    )
  }

  val gfx801: Architecture = gfx8("gfx801", banks = 32, ratio = 16)
  val gfx802: Architecture = gfx8("gfx802", banks = 32, ratio = 16)
  val gfx803: Architecture = gfx8("gfx803", banks = 32, ratio = 16)
  val gfx805: Architecture = gfx802.derived(
    "gfx805",
    "LLVM gives gfx805 the feature set of gfx802 (GCNProcessors.td 143); the CU is assumed to match, not checked"
  )
  val gfx810: Architecture = gfx8("gfx810", banks = 16, ratio = 16)

  private def gfx9(name: String): Architecture =
    gfx906.derived(name, "Vega parts share the GFX9 CU; FP64 runs at 1/16 of FP32 outside gfx906").copy(
      throughput = gfx906.throughput.copy(fp64 = fp64Ratio(16, consumerGcn))
    )

  val gfx900: Architecture = gfx9("gfx900")
  val gfx902: Architecture = gfx900.derived(
    "gfx902",
    "LLVM gives gfx902 the feature set of gfx900 (GCNProcessors.td 167); the CU is assumed to match, not checked"
  )
  val gfx904: Architecture = gfx9("gfx904")
  val gfx909: Architecture = gfx900.derived(
    "gfx909",
    "LLVM gives gfx909 the feature set of gfx900 (GCNProcessors.td 183); the CU is assumed to match, not checked"
  )
  val gfx90c: Architecture = gfx9("gfx90c")

  private val mi100Tensor = recalled(
    "AMD CDNA architecture whitepaper, 2020: MI100 peak FP16 matrix rate of 184.6 TFLOPS, 1024 FLOPs per CU per cycle"
  )
  private val mi200Tensor = recalled(
    "AMD CDNA 2 architecture whitepaper, 2021: MI250X peak FP16 matrix rate of 383 TFLOPS, 1024 FLOPs per CU per cycle"
  )
  private val mi300Tensor = recalled(
    "AMD CDNA 3 architecture whitepaper, 2023: MI300X peak dense FP16 matrix rate of 1307 TFLOPS, 2048 FLOPs per CU " +
      "per cycle"
  )
  private val mi350Tensor = recalled(
    "AMD CDNA 4 architecture whitepaper, 2025: 4096 dense FP16 FLOPs per CU per cycle, twice CDNA 3"
  )

  val gfx908: Architecture = gfx906
    .derived("gfx908", "CDNA1 keeps the GFX9 CU and adds matrix cores")
    .copy(
      throughput = gfx906.throughput.copy(
        fp64 = Sourced(
          32.0,
          recalled(
            "AMD Instinct MI100 specification: peak FP64 of 11.5 TFLOPS against 23.1 TFLOPS FP32, half rate"
          )
        ),
        tensor = Sourced(512.0, mi100Tensor)
      )
    )

  private val cdna2Limits = gcnLimits(
    waves = 32,
    registers = 32768,
    granule = 512,
    lds = 65536,
    ldsGranule = 512,
    ldsSource = llvm("getLocalMemorySize")
  )
    .copy(laneRegisters = Sourced(512, llvm("getTotalNumVGPRs, 512 unified VGPRs and AGPRs on gfx90a")))

  val gfx90a: Architecture = gfx908
    .derived("gfx90a", "CDNA2 keeps the CDNA1 CU with a unified 512-entry register file and eight waves per SIMD")
    .copy(
      limits = cdna2Limits,
      throughput = gfx908.throughput.copy(
        fp64 = Sourced(
          64.0,
          recalled("AMD Instinct MI250X specification: peak vector FP64 of 47.9 TFLOPS, equal to vector FP32")
        ),
        tensor = Sourced(512.0, mi200Tensor)
      ),
      latency = mi210Latency
    )

  private val cdna3Lines = recalled("AMD CDNA 3 architecture whitepaper, 2023: 128-byte vector L1 cache lines")

  val gfx942: Architecture = gfx90a
    .derived("gfx942", "CDNA3 keeps the CDNA2 CU, register file and wave limits")
    .copy(
      global = Global(Sourced(128, cdna3Lines), Sourced(128, cdna3Lines)),
      throughput = gfx90a.throughput.copy(
        fp64 = Sourced(
          64.0,
          recalled(
            "AMD Instinct MI300X specification: peak vector FP64 of 81.7 TFLOPS over 304 CUs at 2.1 GHz"
          )
        ),
        tensor = Sourced(1024.0, mi300Tensor)
      )
    )

  private val gfx940Basis =
    "values are those of gfx942; LLVM dropped this processor between llvmorg-20-init and 22.1.8, " +
      "and the decoder keeps it for older code objects"

  val gfx940: Architecture = gfx942.derived("gfx940", gfx940Basis)
  val gfx941: Architecture = gfx942.derived("gfx941", gfx940Basis)

  val gfx950: Architecture = gfx942
    .derived("gfx950", "CDNA4 keeps the CDNA3 CU and register file; LDS, matrix rate and LDS bandwidth grow")
    .copy(
      limits = cdna2Limits.copy(
        shared = Sourced(163840, llvm("FeatureAddressableLocalMemorySize163840")),
        workgroupShared = Sourced(163840, llvm("FeatureAddressableLocalMemorySize163840")),
        sharedGranule = Sourced(1280, llvm("getLdsDwGranularity, 320 dwords"))
      ),
      throughput = gfx942.throughput.copy(
        fp64 = Sourced(
          64.0,
          recalled(
            "AMD Instinct MI355X specification: peak vector FP64 of 78.6 TFLOPS over 256 CUs at 2.4 GHz"
          )
        ),
        tensor = Sourced(2048.0, mi350Tensor),
        shared = Sourced(
          2.0,
          Source.Estimate("assumed 2.0 LDS bandwidth over CDNA3 (the CDNA 4 description is not read)")
        )
      )
    )

  private def rdnaLimits(
      waves: Int,
      registers: Int,
      granule: Int,
      generation: String
  ): Limits = Limits(
    subgroups = Sourced(waves, llvm(s"getMaxWavesPerEU times four SIMDs of a WGP, $generation")),
    workgroups = Sourced(32, llvm("getMaxWorkGroupsPerCU, the barrier limit of a WGP")),
    workgroupCap = WorkgroupCap.MultiSubgroupWorkgroups,
    workgroupLanes = Sourced(1024, llvm("getMaxFlatWorkGroupSize")),
    partitions = Sourced(4, llvm("getEUsPerCU, lines 1196-1214")),
    partitionRegisters = Sourced(registers, llvm(s"getTotalNumVGPRs times the wave32 size, $generation")),
    registerGranule = Sourced(granule, llvm(s"getVGPRAllocGranule times the wave32 size, $generation")),
    laneRegisters = Sourced(256, llvm("getAddressableNumVGPRs")),
    workgroupRegisters = None,
    shared = Sourced(131072, llvm("getLocalMemorySize, 131072 in WGP mode, lines 1172-1182")),
    workgroupShared = Sourced(65536, llvm("getAddressableLocalMemorySize")),
    reservedShared = Sourced(0, llvm("no per-workgroup LDS reservation")),
    sharedGranule = Sourced(512, llvm("getLdsDwGranularity"))
  )

  val gfx1030: Architecture = Architecture(
    "gfx1030",
    Global(
      Sourced(
        128,
        Source.Estimate("assumed equal to the 128-byte L0 line; the timing probe does not measure sector size")
      ),
      Sourced(128, recalled(rdna2Isa))
    ),
    Banks(Sourced(32, rdna2Timing), Sourced(4, recalled(rdna2Isa))),
    rdnaLimits(waves = 64, registers = 32768, granule = 512, generation = "gfx10.3"),
    Throughput(
      issues = Sourced(
        4.0,
        Source.Estimate("assumed, not cited: one wave per SIMD32 per cycle on the four SIMD32s of a WGP")
      ),
      fma = Sourced(128.0, recalled(rdnaWhitepaper)),
      alu = Sourced(128.0, recalled(rdnaWhitepaper)),
      mufu = Sourced(
        32.0,
        Source.Estimate("assumed quarter rate of FP32 for transcendental VALU operations; no rate is cited")
      ),
      fp64 = Sourced(
        8.0,
        recalled(
          "AMD Radeon RX 6900 XT specification: FP64 at 1/16 of the FP32 rate; " +
            "1.44 TFLOPS over 40 WGPs at 2.25 GHz gives 8 per WGP"
        )
      ),
      tensor = Sourced(0.0, amdgpuTd("FeatureISAVersion10_3_0, lines 1970-1981 (partial read), has no WMMA feature")),
      loadStore = Sourced(
        2.0,
        Source.Estimate("assumed: one 128-byte line per cycle per CU, two CUs per WGP; not cited")
      ),
      shared = Sourced(
        2.0,
        Source.Estimate("assumed: one LDS wavefront per cycle per CU, two CUs per WGP; not cited")
      )
    ),
    rdnaProbeLatency(Source.Estimate("gfx1036 probe on the Ryzen 9 9950X3D integrated GPU; gfx1030 not probed"))
  )

  private val gfx1036Rate = Source.Probed(
    "fma_f64 rate kernel, 8 chains, 1024 threads per WGP, median of 10 launches (Nextcloud probe kit)",
    "Ryzen 9 9950X3D integrated GPU, gfx1036, one WGP",
    "amdgpu, ROCm 7.1.1 runtime",
    "HIP 7.1.52802, clang 20.0.0.rocm"
  )
  private val gfx1036Latency = Source.Probed(
    "dependent chains timed with clock64 over 4096 hops, median of 10 launches (Nextcloud probe kit)",
    "Ryzen 9 9950X3D integrated GPU, gfx1036, one WGP",
    "amdgpu, ROCm 7.1.1 runtime",
    "HIP 7.1.52802, clang 20.0.0.rocm"
  )

  val gfx1036: Architecture = gfx1030.copy(
    name = "gfx1036",
    throughput = gfx1030.throughput.copy(fp64 = Sourced(8.0, gfx1036Rate)),
    latency = rdnaProbeLatency(gfx1036Latency)
  )

  private def rdna2(name: String): Architecture =
    gfx1030.derived(
      name,
      "RDNA2 parts share the gfx1030 WGP, L0 cache and LDS design and differ in WGP count; " +
        "chip mapping recalled from the Wikipedia Radeon RX 6000 series page, not read"
    )

  val gfx1031: Architecture = rdna2("gfx1031")
  val gfx1032: Architecture = rdna2("gfx1032")
  val gfx1033: Architecture = rdna2("gfx1033")
  val gfx1034: Architecture = rdna2("gfx1034")
  val gfx1035: Architecture = rdna2("gfx1035")

  val gfx1010: Architecture = gfx1030
    .derived("gfx1010", "RDNA1 shares the WGP, 128-byte L0 line, and LDS design with RDNA2")
    .copy(
      limits = rdnaLimits(80, 32768, 256, "gfx10.1"),
      throughput = gfx1030.throughput.copy(
        fp64 = Sourced(
          8.0,
          recalled(
            "Wikipedia Radeon RX 5700 XT row: FP64 of 609.6 GFLOPS at 1.905 GHz over 20 WGPs gives 8 per WGP"
          )
        )
      )
    )

  private def rdna1(name: String): Architecture = gfx1010.derived(name, "same RDNA1 WGP as gfx1010")

  val gfx1011: Architecture = rdna1("gfx1011")
  val gfx1012: Architecture = rdna1("gfx1012")
  val gfx1013: Architecture = rdna1("gfx1013")

  private val rdna3Tensor = Source.Estimate(
    "WMMA on the vector ALUs; dense FP16 peak of the RX 7900 XTX (gfx1100), 122.8 TFLOPS over 96 CUs at 2.5 GHz " +
      "(recalled and not checked), 256 multiply-accumulates per CU per cycle, 512 per WGP"
  )

  private val rdna3Fp64 = recalled(
    "Wikipedia Radeon RX 7900 XTX row: FP64 of 0.960 TFLOPS at 2.5 GHz over 48 WGPs gives 4 per WGP"
  )

  private def rdna3Limits(registers: Int, granule: Int, generation: String) =
    rdnaLimits(64, registers, granule, generation)

  val gfx1150: Architecture = gfx1030
    .derived("gfx1150", "RDNA3.5 keeps the RDNA2 WGP, L0 line, and LDS; dual-issue FP32 and WMMA are added")
    .copy(
      limits = rdna3Limits(registers = 32768, granule = 512, generation = "gfx11.5"),
      throughput = gfx1030.throughput.copy(
        fma = Sourced(256.0, recalled(rdna3Isa)),
        fp64 = Sourced(4.0, rdna3Fp64),
        tensor = Sourced(512.0, rdna3Tensor)
      )
    )

  private def rdna3(name: String, registers: Int, granule: Int, generation: String): Architecture =
    gfx1150.derived(name, "same RDNA3 family WGP as gfx1150").copy(limits = rdna3Limits(registers, granule, generation))

  val gfx1100: Architecture = rdna3("gfx1100", registers = 49152, granule = 768, generation = "gfx11.0 with 1.5x VGPRs")
  val gfx1101: Architecture = rdna3("gfx1101", registers = 49152, granule = 768, generation = "gfx11.0 with 1.5x VGPRs")
  val gfx1102: Architecture = rdna3("gfx1102", registers = 32768, granule = 512, generation = "gfx11.0")
  val gfx1103: Architecture = rdna3("gfx1103", registers = 32768, granule = 512, generation = "gfx11.0")
  val gfx1151: Architecture = rdna3("gfx1151", registers = 49152, granule = 768, generation = "gfx11.5 with 1.5x VGPRs")
  val gfx1152: Architecture = rdna3("gfx1152", registers = 32768, granule = 512, generation = "gfx11.5")
  val gfx1153: Architecture = rdna3("gfx1153", registers = 32768, granule = 512, generation = "gfx11.5")
  val gfx1154: Architecture = gfx1153.derived(
    "gfx1154",
    "LLVM gives gfx1154 the feature set of gfx1153 (GCNProcessors.td 307); the CU is assumed to match, not checked"
  )

  val gfx1170: Architecture = gfx1150.derived(
    "gfx1170",
    "LLVM 23.1.0 accepts gfx1170 (GCNProcessors.td 311); its RDNA3.5 WGP is assumed to match gfx1150, not checked"
  )
  val gfx1171: Architecture = gfx1170.derived(
    "gfx1171",
    "LLVM gives gfx1171 the feature set of gfx1170 (GCNProcessors.td 315); the CU is assumed to match, not checked"
  )
  val gfx1172: Architecture = gfx1170.derived(
    "gfx1172",
    "LLVM gives gfx1172 the feature set of gfx1170 (GCNProcessors.td 319); the CU is assumed to match, not checked"
  )

  private val rdna4Tensor = Source.Estimate(
    "dense FP16 matrix peak of the RX 9070 XT, 389 TFLOPS over 64 CUs at 2.97 GHz (recalled and not checked), " +
      "1024 multiply-accumulates per CU per cycle, 2048 per WGP"
  )

  private val unsourcedFp64 =
    Source.Estimate("no source read for this part; 8.0 is the RDNA2 value carried over, not checked")

  val gfx1200: Architecture = gfx1150
    .derived("gfx1200", "RDNA4 keeps the RDNA3 WGP, L0 line, and LDS; the VGPR file grows 1.5x and WMMA doubles")
    .copy(
      limits = rdnaLimits(64, 49152, 768, "gfx12"),
      throughput = gfx1150.throughput.copy(
        fp64 = Sourced(8.0, unsourcedFp64),
        tensor = Sourced(
          2048.0,
          Source.Estimate(
            "same per-WGP rate as gfx1201, which carries the RX 9070 XT figure (recalled, not checked)"
          )
        )
      )
    )
    .on(Card("Radeon RX 9060 XT", 128, 4096L, Some(320.0), Some(32), Some(3130)))

  val gfx1201: Architecture = gfx1200
    .derived("gfx1201", "same RDNA4 WGP as gfx1200")
    .copy(throughput = gfx1200.throughput.copy(tensor = Sourced(2048.0, rdna4Tensor)))
    .on(Card("Radeon RX 9070 XT", 256, 8192L, Some(640.0), Some(64), Some(2970)))

  private val gfx1250Estimate = Source.Estimate(
    "per multiprocessor (one CU of four SIMD32s): no published per-CU rates for this part; " +
      "fma takes four SIMD32s, tensor takes the CDNA4 dense rate per CU, " +
      "and loadStore and shared take one per cycle each"
  )

  private val gfx125xLines = Source.Estimate(
    "assumed 128-byte sector and line as on RDNA; LLVM has no data cache line size and no gfx125x document was read"
  )

  val gfx1250: Architecture = gfx1150
    .derived("gfx1250", "gfx1250 is wave32 like RDNA but organised as a CU with four SIMD32s")
    .copy(
      global = Global(Sourced(128, gfx125xLines), Sourced(128, gfx125xLines)),
      limits = Limits(
        subgroups = Sourced(64, llvm("getMaxWavesPerEU times getEUsPerCU, four SIMDs of a CU")),
        workgroups = Sourced(16, llvm("getMaxWorkGroupsPerCU, the barrier limit of a CU")),
        workgroupCap = WorkgroupCap.MultiSubgroupWorkgroups,
        workgroupLanes = Sourced(1024, llvm("getMaxFlatWorkGroupSize")),
        partitions = Sourced(4, llvm("getEUsPerCU, lines 1196-1214, four SIMDs per CU")),
        partitionRegisters = Sourced(32768, llvm("getTotalNumVGPRs times the wave32 size")),
        registerGranule = Sourced(512, llvm("getVGPRAllocGranule times the wave32 size")),
        laneRegisters = Sourced(1024, amdgpuTd("Feature1024AddressableVGPRs in the gfx125x set, line 2165")),
        workgroupRegisters = None,
        shared = Sourced(327680, llvm("FeatureAddressableLocalMemorySize327680")),
        workgroupShared = Sourced(327680, llvm("FeatureAddressableLocalMemorySize327680")),
        reservedShared = Sourced(0, llvm("no per-workgroup LDS reservation")),
        sharedGranule = Sourced(2048, llvm("getLdsDwGranularity, 512 dwords"))
      ),
      throughput = gfx1150.throughput.copy(
        fp64 = Sourced(8.0, unsourcedFp64),
        fma = Sourced(128.0, gfx1250Estimate),
        tensor = Sourced(2048.0, gfx1250Estimate),
        loadStore = Sourced(1.0, gfx1250Estimate),
        shared = Sourced(1.0, gfx1250Estimate)
      )
    )

  val gfx1251: Architecture = gfx1250.derived("gfx1251", "same gfx125x CU as gfx1250")

  private val amdgpuAll: Vec[Architecture] = Vec(
    gfx600,
    gfx601,
    gfx602,
    gfx700,
    gfx701,
    gfx702,
    gfx703,
    gfx704,
    gfx705,
    gfx801,
    gfx802,
    gfx803,
    gfx805,
    gfx810,
    gfx900,
    gfx902,
    gfx904,
    gfx906,
    gfx909,
    gfx90c,
    gfx908,
    gfx90a,
    gfx940,
    gfx941,
    gfx942,
    gfx950,
    gfx1250,
    gfx1251,
    gfx1010,
    gfx1011,
    gfx1012,
    gfx1013,
    gfx1030,
    gfx1031,
    gfx1032,
    gfx1033,
    gfx1034,
    gfx1035,
    gfx1036,
    gfx1100,
    gfx1101,
    gfx1102,
    gfx1103,
    gfx1150,
    gfx1151,
    gfx1152,
    gfx1153,
    gfx1154,
    gfx1170,
    gfx1171,
    gfx1172,
    gfx1200,
    gfx1201
  )

  val aliases: Map[String, String] = Map(
    "tahiti"   -> "gfx600",
    "verde"    -> "gfx601",
    "pitcairn" -> "gfx601",
    "oland"    -> "gfx602",
    "hainan"   -> "gfx602",
    "kaveri"   -> "gfx700",
    "hawaii"   -> "gfx701",
    "kabini"   -> "gfx703",
    "mullins"  -> "gfx703",
    "bonaire"  -> "gfx704",
    "carrizo"  -> "gfx801",
    "iceland"  -> "gfx802",
    "tonga"    -> "gfx802",
    "fiji"     -> "gfx803",
    "stoney"   -> "gfx810",
    "gfx9"     -> "gfx900",
    "gfx12"    -> "gfx1200"
  )

  /** One shipping card of a target's own silicon. A target without one has no device memory values. */
  private case class Card(
      name: String,
      busWidthBits: Int,
      l2KiB: Long,
      bandwidthGBs: Option[Double] = None,
      multiprocessors: Option[Int] = None,
      boostMhz: Option[Int] = None
  )

  val all: Vec[Architecture] = nvidiaAll ++ amdgpuAll

  /** What profiles NVIDIA programs unless another architecture is chosen: the probed RTX 4060 Ti. */
  val nvidiaDefault: Architecture = sm89

  /** What profiles AMDGPU programs unless another architecture is chosen: the probed gfx1036. */
  val amdgpuDefault: Architecture = gfx1036

  /** The architecture of a table name or of an LLVM processor alias, such as `tahiti` for `gfx600`. */
  def named(name: String): Option[Architecture] =
    all.find(_.name == name).orElse(aliases.get(name).flatMap(named(_)))
}

/** What a profile needs to know about one GPU architecture, each value with its provenance. */
case class Architecture(
    name: String,
    global: Architecture.Global,
    shared: Architecture.Banks,
    limits: Architecture.Limits,
    throughput: Architecture.Throughput,
    latency: Architecture.Latency,
    memory: Option[Architecture.Memory] = None
) derives CanEqual {

  /** The same architecture under `name`, every value carried over from this one for the reason in `basis`. */
  def derived(name: String, basis: String): Architecture = {
    def inherit[A](value: Architecture.Sourced[A]): Architecture.Sourced[A] =
      value.copy(source = Architecture.Source.Inherited(this.name, basis))
    def sibling(value: Architecture.Sourced[Double]): Architecture.Sourced[Double] =
      value.copy(source = Architecture.Source.Estimate(value.source.detail))
    Architecture(
      name,
      Architecture.Global(inherit(global.sectorBytes), inherit(global.lineBytes)),
      Architecture.Banks(inherit(shared.count), inherit(shared.bankBytes)),
      Architecture.Limits(
        subgroups = inherit(limits.subgroups),
        workgroups = inherit(limits.workgroups),
        workgroupCap = limits.workgroupCap,
        workgroupLanes = inherit(limits.workgroupLanes),
        partitions = inherit(limits.partitions),
        partitionRegisters = inherit(limits.partitionRegisters),
        registerGranule = inherit(limits.registerGranule),
        laneRegisters = inherit(limits.laneRegisters),
        workgroupRegisters = limits.workgroupRegisters.map(inherit(_)),
        shared = inherit(limits.shared),
        workgroupShared = inherit(limits.workgroupShared),
        reservedShared = inherit(limits.reservedShared),
        sharedGranule = inherit(limits.sharedGranule)
      ),
      Architecture.Throughput(
        issues = inherit(throughput.issues),
        fma = inherit(throughput.fma),
        alu = inherit(throughput.alu),
        mufu = inherit(throughput.mufu),
        fp64 = inherit(throughput.fp64),
        tensor = inherit(throughput.tensor),
        loadStore = inherit(throughput.loadStore),
        shared = inherit(throughput.shared),
        loads = throughput.loads.map(inherit(_))
      ),
      Architecture.Latency(
        inherit(latency.shared),
        latency.l1.map(inherit(_)),
        latency.l2.map(sibling(_)),
        sibling(latency.dram)
      )
    )
  }

  private def on(card: Architecture.Card): Architecture = {
    val dram = for {
      bandwidth <- card.bandwidthGBs
      count     <- card.multiprocessors
      clock     <- card.boostMhz
    } yield Architecture.Sourced(
      bandwidth * 1000.0 / (count * clock),
      Architecture.Source.Estimate(s"${card.name}: $bandwidth GB/s over $count multiprocessors at $clock MHz")
    )
    val source = Architecture.Source.Estimate(card.name)
    copy(
      throughput = throughput.copy(dram = dram),
      memory = Some(
        Architecture.Memory(
          Architecture.Sourced(card.busWidthBits, source),
          Architecture.Sourced(card.l2KiB * 1024L, source)
        )
      )
    )
  }
}
