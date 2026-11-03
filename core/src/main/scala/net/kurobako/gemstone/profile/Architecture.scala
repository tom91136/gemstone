package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*

object Architecture {

  /** Where a table value comes from. */
  enum Source derives CanEqual {

    /** Stated in the vendor's documentation, named by `document`. */
    case Documented(document: String)

    /** Measured by a probe on `card`, with the driver and toolkit it ran under. */
    case Probed(probe: String, card: String, driver: String, toolkit: String)

    /** Neither documented nor measured; `basis` says what the value rests on. */
    case Estimate(basis: String)
  }

  /** A table value and where it comes from. */
  case class Sourced[A](value: A, source: Source) derives CanEqual

  /** How lane addresses to device memory become transactions: the unit the memory system moves, and the cache line
    * those units belong to.
    */
  case class Global(sectorBytes: Sourced[Int], lineBytes: Sourced[Int]) derives CanEqual

  /** How shared memory or LDS serves a request: banks of `bankBytes` each, one word per bank per wavefront. */
  case class Banks(count: Sourced[Int], bankBytes: Sourced[Int]) derives CanEqual

  /** What one multiprocessor (an SM, or a WGP) serves per cycle, which turns the waste of a profile into cycles the
    * resource spends on it: `sectorsPerCycle` global sectors through the L1 or L0 path, `wavefrontsPerCycle` shared
    * memory or LDS wavefronts, and `issuesPerCycle` subgroup issues across its schedulers or SIMDs. These are
    * throughput bounds of an otherwise idle multiprocessor, not timing.
    */
  case class Throughput(
      sectorsPerCycle: Sourced[Double],
      wavefrontsPerCycle: Sourced[Double],
      issuesPerCycle: Sourced[Double]
  ) derives CanEqual

  /** What one multiprocessor (an SM, or a WGP) holds at once. Its register file is split into `partitions`, one per
    * scheduler, and a subgroup takes registers in lane-register granules. Shared memory is allocated per workgroup in
    * granules, after a reservation the runtime makes for every workgroup.
    */
  case class Limits(
      subgroups: Sourced[Int],
      workgroups: Sourced[Int],
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

  private val cudaGuide        = Source.Documented("CUDA C++ Programming Guide 13, technical specifications for 8.9")
  private val cudaOccupancy    = Source.Documented("cuda_occupancy.h, CUDA 13.3")
  private val cudaBestPractice =
    Source.Documented("CUDA C++ Best Practices Guide 13, coalesced access to global memory")
  private val rdna2Isa       = Source.Documented("AMD RDNA2 Instruction Set Architecture reference guide, 2020")
  private val rdnaWhitepaper = Source.Documented("AMD RDNA architecture whitepaper, 2019")
  private val llvmAmdgpu     = Source.Documented("LLVM AMDGPUUsage and AMDGPUBaseInfo for GFX10.3")
  private val adaPeak        = Source.Probed(
    "ncu peak_sustained of l1tex data-stage wavefronts (128 bytes, 4 sectors) and sm instructions issued over a " +
      "profile_memory launch (Nextcloud probe kit)",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 615.78.08",
    "CUDA 13.3, ncu 2026.2.1"
  )

  val sm89: Architecture = Architecture(
    "sm_89",
    Global(Sourced(32, cudaBestPractice), Sourced(128, cudaBestPractice)),
    Banks(Sourced(32, cudaGuide), Sourced(4, cudaGuide)),
    Limits(
      subgroups = Sourced(48, cudaGuide),
      workgroups = Sourced(24, cudaOccupancy),
      workgroupLanes = Sourced(1024, cudaGuide),
      partitions = Sourced(4, cudaOccupancy),
      partitionRegisters = Sourced(16384, cudaOccupancy),
      registerGranule = Sourced(256, cudaOccupancy),
      laneRegisters = Sourced(255, cudaGuide),
      workgroupRegisters = Some(Sourced(65536, cudaGuide)),
      shared = Sourced(102400, cudaGuide),
      workgroupShared = Sourced(101376, cudaGuide),
      reservedShared = Sourced(1024, cudaGuide),
      sharedGranule = Sourced(128, cudaOccupancy)
    ),
    Throughput(
      sectorsPerCycle = Sourced(4.0, adaPeak),
      wavefrontsPerCycle = Sourced(1.0, adaPeak),
      issuesPerCycle = Sourced(4.0, adaPeak)
    )
  )

  private val rdna2Timing = Source.Probed(
    "profile_timing.hip, s_memtime around dependent chains on one wave32 (Nextcloud probe kit)",
    "Ryzen 9 9950X3D integrated GPU, gfx1036, run as gfx1030",
    "amdgpu, ROCm 7.1.1 runtime",
    "ROCm 7.1.1 hipcc"
  )

  val gfx1030: Architecture = Architecture(
    "gfx1030",
    Global(
      Sourced(128, rdna2Timing),
      Sourced(128, rdna2Isa)
    ),
    Banks(Sourced(32, rdna2Timing), Sourced(4, rdna2Isa)),
    Limits(
      subgroups = Sourced(64, llvmAmdgpu),
      workgroups = Sourced(32, Source.Estimate("no documented per-WGP workgroup limit below the wave limit")),
      workgroupLanes = Sourced(1024, llvmAmdgpu),
      partitions = Sourced(4, rdnaWhitepaper),
      partitionRegisters = Sourced(32768, llvmAmdgpu),
      registerGranule = Sourced(512, llvmAmdgpu),
      laneRegisters = Sourced(256, llvmAmdgpu),
      workgroupRegisters = None,
      shared = Sourced(131072, rdnaWhitepaper),
      workgroupShared = Sourced(65536, llvmAmdgpu),
      reservedShared = Sourced(0, llvmAmdgpu),
      sharedGranule = Sourced(512, llvmAmdgpu)
    ),
    Throughput(
      sectorsPerCycle = Sourced(
        2.0,
        Source.Estimate(
          "on gfx1036 each further 128-byte line a wave touches costs about one more L0 cycle, so one line per cycle " +
            "per CU, two CUs per WGP"
        )
      ),
      wavefrontsPerCycle = Sourced(
        2.0,
        Source.Estimate(
          "on gfx1036 each further conflicting lane costs one more LDS cycle, so one wavefront per cycle per CU, two " +
            "CUs per WGP"
        )
      ),
      issuesPerCycle = Sourced(
        4.0,
        Source.Estimate("the WGP has four SIMD32s (RDNA whitepaper), assumed to each issue one wave per cycle")
      )
    )
  )

  /** gfx1036 shares the RDNA2 memory system of gfx1030. */
  val gfx1036: Architecture = gfx1030.copy(name = "gfx1036")

  val all: Vec[Architecture] = Vec(sm89, gfx1030, gfx1036)

  /** What profiles NVIDIA programs unless another architecture is chosen: the probed RTX 4060 Ti. */
  val nvidiaDefault: Architecture = sm89

  /** What profiles AMDGPU programs unless another architecture is chosen: the probed gfx1036. */
  val amdgpuDefault: Architecture = gfx1036

  def named(name: String): Option[Architecture] = all.find(_.name == name)
}

/** What a profile needs to know about one GPU architecture, each value with its provenance. */
case class Architecture(
    name: String,
    global: Architecture.Global,
    shared: Architecture.Banks,
    limits: Architecture.Limits,
    throughput: Architecture.Throughput
) derives CanEqual
