package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import scala.util.matching.Regex

/** Why no instance of a PTX or SASS form has a definition, from the form's instruction class and how the VM treats its
  * instances.
  */
private object NvidiaReasons {

  /** Texture, surface, attribute, interpolation and output instructions of the graphics pipeline. */
  private val graphics = Set(
    "tex",
    "tld4",
    "txq",
    "suld",
    "sust",
    "sured",
    "suq",
    "istypep",
    "AL2P",
    "ALD",
    "AST",
    "IPA",
    "ISBERD",
    "ISBEWR",
    "OUT",
    "PIXLD",
    "KIL",
    "FSWZADD",
    "VOTE_VTG",
    "TEX",
    "TEXS",
    "TLD",
    "TLDS",
    "TLD4",
    "TLD4S",
    "TMML",
    "TXA",
    "TXD",
    "TXQ",
    "FOOTPRINT",
    "SULD",
    "SUST",
    "SURED",
    "SUATOM",
    "SUQUERY",
    "SUCLAMP",
    "SUEAU",
    "SUBFM",
    "SULDGA",
    "SUSTGA",
    "LDTRAM"
  )

  /** Caching, scheduling and profiling hints, which change nothing a compute kernel can observe. */
  private val hints = Set(
    "prefetch",
    "prefetchu",
    "applypriority",
    "discard",
    "pmevent",
    "nanosleep",
    "cp.async.bulk.prefetch",
    "cp.async.bulk.prefetch.tensor",
    "NANOSLEEP",
    "YIELD",
    "NOP",
    "CCTL",
    "CCTLL",
    "CCTLT",
    "UBLKPF",
    "UTMAPF",
    "PMTRIG",
    "CCTLS"
  )

  /** CUDA Fabric Transport operations, which address memory through logical endpoint handles. */
  private def fabric(mnemonic: String): Boolean = mnemonic.startsWith("fabric.")

  /** Kepler's pixel-quad swizzle and primitive clip and cull test, which serve fragment and vertex shading. */
  private val shading = Set("FSWZ", "FCCO")

  /** Legacy argument reductions whose result is an undocumented operand format that only the next `MUFU` reads. */
  private val reductions = Set("RRO")

  private val unproduced =
    "ptxas 11.8 and 13.3 lower none of the PTX that reaches this instruction to this form, so nothing defines the " +
      "effect its encoding names."

  /** SASS memory, atomic, barrier and synchronisation forms that ptxas does not emit, by mnemonic and a pattern over
    * the form's name, with why they are not modelled.
    */
  private val unemitted: Vec[(mnemonic: String, form: Regex, reason: String)] = Vec(
    ("BAR", "(?i).*(scan|syncall).*".r, unproduced),
    ("LD", ".*_zd.*".r, unproduced),
    ("LDG", ".*(_zd|SPARSE).*".r, unproduced),
    ("LDS", ".*_zd.*".r, unproduced),
    ("STAS", "stas_(64__RaRZ|_Ra32).*".r, s"$unproduced st.async addresses a register pair."),
    ("REDAS", "redas_(64__RaRZ|_Ra32).*".r, s"$unproduced red.async addresses a register pair."),
    (
      "UTCATOMSWS",
      "utcatomsws_cas.*".r,
      s"$unproduced tcgen05.alloc and tcgen05.dealloc claim and release granules with FIND_AND_SET and AND."
    ),
    ("__HIR0X130", ".*".r, unproduced),
    ("__HIR0X160", ".*".r, unproduced),
    ("__HIR0X170", ".*".r, unproduced),
    ("SYNCS", "syncs_(flush|ld|tcnt|uniform_ld|uniform_cas)_.*".r, unproduced),
    ("UBLKCP", ".*one.*".r, unproduced),
    ("UBLKRED", ".*one.*".r, unproduced),
    ("UCGABARGET", ".*".r, unproduced),
    ("UCGABARSET", ".*".r, unproduced),
    ("STP", ".*".r, unproduced),
    (
      "ULDC",
      "uldc_(imm|ur_offs).*".r,
      s"$unproduced It reads constant memory at a flat address in a uniform register."
    ),
    ("LDCU", "ldcu_ur_offs.*".r, s"$unproduced It reads constant memory at a flat address in a uniform register."),
    ("LDCU", "ldcu_256_.*".r, unproduced),
    ("HGMMA", ".*_URa_Rb_Rc_".r, s"$unproduced It takes B from registers, which no wgmma.mma_async form does."),
    ("IGMMA", ".*_URa_Rb_Rc_".r, s"$unproduced It takes B from registers, which no wgmma.mma_async form does."),
    ("QGMMA", ".*_URa_Rb_Rc_".r, s"$unproduced It takes B from registers, which no wgmma.mma_async form does."),
    ("BGMMA", ".*_URa_Rb_Rc_".r, s"$unproduced It takes B from registers, which no wgmma.mma_async form does."),
    ("WARPGROUP", "warpgroup_wait_.*".r, unproduced),
    ("WARPGROUPSET", ".*".r, unproduced)
  )

  /** Uniform-datapath bulk and tensor operations, which issue once per warp: their lowering is guarded by an election.
    */
  private val warpIssued = Set("UBLKCP", "UBLKRED", "UMEMSETS", "UTMALDG", "UTMASTG", "UTMAREDG")

  /** Tensor memory loads and stores ptxas does not emit: it lowers tcgen05.ld and tcgen05.st to LDTM and STTM. */
  private val tensorMemory = Set("LDT", "STT")

  /** tcgen05 copies, shifts, commits and products, and the release stores and reductions of st.async and red.async,
    * which issue once per warp on the uniform datapath: their lowering is guarded by an election.
    */
  private val tensorIssued = Set("UTCCP", "UTCSHIFT", "UTCBAR", "USTGR", "UREDGR")

  /** The tcgen05 products, whose element types and shape come from the instruction descriptor register at run time. */
  private val tensorProducts = Set("UTCHMMA", "UTCIMMA", "UTCQMMA", "UTCOMMA")

  /** Why a form of the graphics pipeline, Fabric Transport, shading, argument reduction or a hint is not modelled,
    * whatever its instances lower to.
    */
  def outside(mnemonic: String): Option[Unavailable] =
    if (graphics(mnemonic))
      Some(Unavailable.NotModelled("It belongs to the graphics pipeline's texture, surface or attribute state."))
    else if (fabric(mnemonic))
      Some(Unavailable.NotModelled(
        "It moves data through CUDA Fabric Transport handles, whose logical endpoints only the driver's logical " +
          "endpoint API creates, and Gemstone's runtime creates none."
      ))
    else if (shading(mnemonic))
      Some(Unavailable.NotModelled(
        "It is a graphics shading operation (pixel-quad swizzle or primitive clip test) no compute toolchain emits."
      ))
    else if (reductions(mnemonic))
      Some(Unavailable.NotModelled(
        "Its result is an undocumented operand format only MUFU reads; the VM folds it into the consuming MUFU."
      ))
    else if (hints(mnemonic))
      Some(Unavailable.NotModelled(
        "It only hints at caching, scheduling or profiling, which a kernel cannot observe."
      ))
    else None

  /** Why a SASS form is not modelled whatever its instances lower to, from its mnemonic and form name. */
  def excluded(mnemonic: String, form: String): Option[Unavailable] =
    outside(mnemonic).orElse(
      if (tensorMemory(mnemonic))
        Some(Unavailable.NotModelled(
          s"$unproduced ptxas lowers tcgen05.ld and tcgen05.st to LDTM and STTM, which the VM runs."
        ))
      else if (tensorIssued(mnemonic) && !form.contains("one") && !form.contains("flush"))
        Some(Unavailable.Mechanics(
          "One lane of each warp issues a tensor memory copy, shift or commit, or a release store or reduction, which " +
            "the VM completes when issued."
        ))
      else if (mnemonic == "RPCMOV" && Set("rpcmov_dstPc_", "rpcmov_srcPc_")(form))
        Some(Unavailable.Mechanics(
          "It moves a 32-bit half of the return address register, which ptxas uses as scratch around warp-aggregated " +
            "mbarrier arrivals."
        ))
      else if (tensorProducts(mnemonic) && !form.contains("one"))
        Some(Unavailable.Mechanics(
          "One lane of each warp issues a tcgen05 product, whose element types and shape the instruction descriptor " +
            "register gives at run time; tcgen05.mma with an immediate descriptor describes each element."
        ))
      else if (mnemonic == "UGETNEXTWORKID" && !form.contains("one"))
        Some(Unavailable.Mechanics(
          "One lane of each warp requests a cluster launch control cancellation; the VM reserves every CTA at launch, " +
            "so it writes the failed response and completes the mbarrier transaction."
        ))
      else if (warpIssued(mnemonic) && !form.contains("one"))
        Some(Unavailable.Mechanics(
          "One lane of each warp issues an asynchronous bulk or tensor copy, reduction or fill, which the VM completes " +
            "when issued."
        ))
      else
        unemitted.collectFirst {
          case (`mnemonic`, pattern, reason) if pattern.matches(form) => Unavailable.NotModelled(reason)
        }
    )

  /** Why an instruction has no definition, from its mnemonic and why each of its instances lowers to none. */
  def of(mnemonic: String, failures: Vec[Unrendered]): Unavailable = outside(mnemonic).getOrElse {
    val staged    = failures.collect { case Unrendered.Unstaged(operations) => operations }
    val unstaged  = staged.flatten.distinct
    val noOp      = unstaged == Vec("NoOp") || failures.contains(Unrendered.Unstaged(Vec.empty))
    def undefined = failures.collectFirst { case Unrendered.Undefined(reason) => reason }.nonEmpty
    val failed    = unstaged.isEmpty && failures.exists {
      case Unrendered.Rejected(_) | Unrendered.Malformed(_) => true
      case _                                                => false
    }
    if (failed) Unavailable.of(failures)
    else if (noOp && mnemonic.startsWith("cp.async"))
      Unavailable.Mechanics("It orders asynchronous copies, which the VM completes when issued.")
    else if (noOp && (mnemonic.startsWith("wgmma") || mnemonic == "WARPGROUP"))
      Unavailable.Mechanics("It orders asynchronous warpgroup matrix products, which the VM completes when issued.")
    else if (staged.isEmpty && undefined)
      Unavailable.Mechanics("It reads barrier, convergence or matrix state the VM keeps outside registers.")
    else Unavailable.of(failures)
  }
}
