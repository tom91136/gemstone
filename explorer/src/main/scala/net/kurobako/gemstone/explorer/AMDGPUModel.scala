package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.semantics.render.*
import scala.collection.mutable

/** AMDGPU opcodes with their per-ISA encodings, operand metadata and instruction word layouts. */
object AMDGPUModel extends Model {
  lazy val targets: Vec[String]     = Target.all.map(_.raw)
  val anyTarget: Boolean            = true
  def defaultTarget: Option[String] = Some("gfx1030")

  private def target(name: Option[String]): Either[String, Option[Target]] = name match {
    case Some(value) => value.refine[Target].map(Some(_)).left.map(_.raw)
    case None        => Right(None)
  }

  private def encoding(layout: EncodingCatalogue.Layout, opcode: Int): View.Encoding = {
    def positions(field: EncodingCatalogue.Field): Vec[Int] =
      field.ranges.flatMap(range => Vec.tabulate(range.bits)(range.offset.raw + _))
    val fixed = Vec.tabulate(layout.bits)(identity).collect {
      case bit if layout.fixedMask.testBit(bit) => bit -> layout.fixedPrefix.testBit(bit)
    }.toMap
    val operation = layout.fields.find(_.name == EncodingField.OP).fold(Map.empty[Int, Boolean]) { field =>
      val logicals = field.ranges.scanLeft(0)((logical, range) => logical + range.padding + range.bits)
      val bits     = field.ranges.zip(logicals).flatMap((range, logical) =>
        Vec.tabulate(range.bits)(bit => (range.offset.raw + bit) -> (logical + range.padding + bit))
      )
      bits.map((physical, logical) => physical -> (logical < 32 && ((opcode >>> logical) & 1) != 0)).toMap
    }
    View.Encoding(
      layout.bits,
      layout.fields.map(field =>
        View.Field(
          field.name.toString,
          positions(field),
          field.ranges.map(range =>
            s"[${range.offset.raw + range.bits - 1}:${range.offset}]" +
              (if (range.padding > 0) s" << ${range.padding}" else "")
          ).mkString(field.name.toString + " ", ", ", "")
        )
      ),
      fixed ++ operation
    )
  }

  private def operandName(info: OperandInfo): String     = AMDGPUInstances.operandName(info)
  private def formName(value: Metadata.Encoding): String = s"${value.isa} ${value.format}"

  private val built = java.util.Collections.synchronizedMap(java.util.IdentityHashMap[Metadata.Encoding, View.Form]())

  private def form(opcode: Opcode, value: Metadata.Encoding): View.Form =
    built.computeIfAbsent(value, _ => build(opcode, value))

  private def build(opcode: Opcode, value: Metadata.Encoding): View.Form = {
    val explicit = value.operands.filterNot(_.implicitOperand)
    val names    = explicit.map(operandName(_))
    View.Form(
      formName(value),
      Vec[View.Token](View.Token.Text(opcode.mnemonic.raw)) ++ names.zip(explicit).zipWithIndex.flatMap {
        case ((name, info), index) =>
          Vec(View.Token.Text(if (index == 0) " " else ", "), View.Token.Slot(name, info.kind.toString))
      },
      value.operands.map(info =>
        View.OperandSlot(
          operandName(info),
          info.kind.toString,
          Vec(
            Option.when(info.output)("output"),
            Option.when(info.implicitOperand)("implicit"),
            Option.when(info.bits > 0)(s"${info.bits} bits")
          ).flatten.mkString(", ")
        )
      ),
      properties = Vec(
        "ISA"    -> value.isa.toString,
        "Format" -> value.format.toString,
        "Opcode" -> s"${value.opcode} (0x${value.opcode.toHexString})"
      ) ++ Option.when(value.groups.nonEmpty)("Groups" -> value.groups.mkString(", ")).toVec ++
        Option.when(value.processors.nonEmpty)("Processors" -> value.processors.mkString(", ")).toVec,
      encoding = EncodingCatalogue.layout(value.isa, value.format).map(encoding(_, value.opcode))
    )
  }

  private val cache = mutable.Map.empty[Option[String], Vec[View.Entry]]

  def entries(name: Option[String]): Vec[View.Entry] = target(name).fold(
    _ => Vec.empty,
    selected =>
      cache.getOrElseUpdate(
        name,
        InstructionDefs.definitions.groupBy(_.mnemonic.raw).toVec.sortBy(_._1).flatMap { (mnemonic, opcodes) =>
          val supported = opcodes.flatMap(opcode =>
            opcode.meta.encodings.collect { case value if selected.forall(value.supports) => (opcode, value) }
          )
          Option.when(supported.nonEmpty)(View.Entry(
            Language.AMDGPU,
            mnemonic,
            supported.map((_, value) => formName(value)),
            () => supported.map(form(_, _)),
            name
          ))
        }
      )
  )

  /** The target an instance of `form` is written for and the catalogue encoding it takes: the selected target, or the
    * first that supports the encoding when none is selected.
    */
  private def encoding(name: Option[String], mnemonic: String, form: View.Form): Option[(Target, Metadata.Encoding)] = {
    val formOperands = form.operands.map(_.name)
    for {
      selected <- target(name).toOption
      value    <- InstructionDefs.forOpcode(mnemonic).view.flatMap(_.meta.encodings).find(value =>
        formName(value) == form.name && value.operands.map(operandName(_)) == formOperands &&
          selected.forall(value.supports)
      )
      target <- selected.orElse(Target.all.find(value.supports))
    } yield (target, value)
  }

  /** Instances of a form on the selected target, or on the first that supports it when none is selected, as
    * [[AMDGPUInstances]] writes them. Each spelling maps to the operand it binds, as the operand table names it, and
    * each state an implicit operand stands for to that operand. A form no toolchain writes has none.
    */
  def instances(name: Option[String], mnemonic: String, form: View.Form)
      : Vec[(target: Target, text: String, operands: Map[String, String])] =
    encoding(name, mnemonic, form).toVec.filter((_, value) => AMDGPUInstances.unwritable(mnemonic, value).isEmpty)
      .flatMap { (target, value) =>
        val states = value.operands.view.filter(info => info.implicitOperand || info.kind == OperandKind.EXEC)
          .flatMap(info => AMDGPUInstances.state(info.kind).map(_ -> operandName(info))).toMap
        AMDGPUInstances.written(target, mnemonic, value).map(instance =>
          (target = target, text = instance.text, operands = states ++ instance.binds)
        )
      }

  private type Outcomes = Vec[(text: String, operands: Map[String, String], outcome: Either[Unrendered, Definition])]

  private def outcomes(entry: View.Entry, form: View.Form): Outcomes = {
    val formOperands = form.operands.map(_.name)
    encoding(entry.target, entry.mnemonic, form).toVec.flatMap { (_, value) =>
      instances(entry.target, entry.mnemonic, form).map((target, text, operands) =>
        (
          text = text,
          operands = operands,
          outcome = InstructionSemantics.describe(
            target,
            text,
            operands,
            formOperands,
            Some(entry.mnemonic),
            Some(value)
          )
        )
      )
    }
  }

  private def numbered(prefix: String, name: String): Option[Int] =
    Option.when(name.startsWith(prefix))(name.drop(prefix.length)).flatMap(_.toIntOption)

  def registers(name: Option[String]): Vec[View.RegisterFile] =
    target(name.orElse(defaultTarget)).toOption.flatten.fold(Vec.empty[View.RegisterFile]) { target =>
      val isa       = target.isa
      val selectors = EncodingCatalogue.sourceSelectors(isa).toVec.sortBy((spelling, value) => (value, spelling))
      def file(prefix: String, title: String, description: String): Option[View.RegisterFile] = {
        val registers = selectors.filter((spelling, _) => numbered(prefix, spelling).nonEmpty)
          .map((spelling, value) => View.Register(spelling, Some(value)))
        Option.when(registers.nonEmpty)(View.RegisterFile(title, description, registers))
      }
      val special = selectors.filter((spelling, _) =>
        spelling.headOption.exists(_.isLetter) && !NumberedPrefixes.exists(numbered(_, spelling).nonEmpty)
      ).map((spelling, value) => View.Register(spelling, Some(value)))
      val constants = selectors.filterNot((spelling, _) => spelling.headOption.exists(_.isLetter))
        .map((spelling, value) => View.Register(spelling, Some(value)))
      Vec(
        file("s", "SGPR", s"Scalar registers shared by the wave ($isa source selector values)."),
        file("v", "VGPR", "Vector registers, one 32-bit lane value per work-item; sources encode them from 256."),
        Option.when(isa.isCDNA)(View.RegisterFile(
          "AGPR",
          "Accumulation registers a0-a255 for matrix instructions; ACCVGPR operands select them instead of VGPRs.",
          Vec.empty
        )),
        file("ttmp", "TTMP", "Trap handler temporaries."),
        Option.when(special.nonEmpty)(View.RegisterFile(
          "Special",
          "Named sources such as VCC, EXEC, M0, SCC and the aperture bases.",
          special
        )),
        Option.when(constants.nonEmpty)(View.RegisterFile(
          "Inline constants",
          "Integer and floating constants.",
          constants
        ))
      ).flatten
    }

  val binary: Boolean = true

  private def selected(name: Option[String]): Either[String, Target] =
    target(name.orElse(defaultTarget)).flatMap(_.toRight("no AMDGPU target selected"))

  def assemble(source: String, name: Option[String]): Either[String, String] = for {
    target  <- selected(name)
    program <- asm.Parser.parse(source).left.map(_.diagnostic.raw)
    bytes   <- asm.Assembler.encode(program, target, TargetDefaults.waveSize(target)).left.map(_.toString)
  } yield Hex.render(bytes)

  def disassemble(bytes: Vec[Byte], name: Option[String]): Either[String, String] = for {
    target  <- selected(name)
    program <- asm.Disassembler(target, TargetDefaults.waveSize(target)).decode(bytes).left.map(_.toString)
  } yield asm.Printer.render(program)

  val assemblyExample: String = "v_add_f32 v0, v1, v2\ns_movk_i32 s5, 0xfffe\n"
  val binaryExample: String   = "01 05 00 06 fe ff 05 b0\n"

  /** The semantics of the form's instances with the operand each instance's spellings bind. */
  def spelt(entry: View.Entry, form: View.Form): Vec[(described: Described, operands: Map[String, String])] =
    described(outcomes(entry, form))

  private def described(found: Outcomes): Vec[(described: Described, operands: Map[String, String])] =
    found.collect { case (text, operands, Right(definition)) => (Described(text, definition), operands) }

  protected def render(entry: View.Entry, form: View.Form): Either[Unavailable, Vec[Described]] = {
    val found   = outcomes(entry, form)
    val defined = described(found).map(_.described)
    Either.cond(defined.nonEmpty, defined, diagnose(entry, form, found))
  }

  /** Encodings outside compute: image sampling, which takes a sampler, the ray-tracing family, whose BVH intersections
    * and LDS traversal stacks serve ray queries, multisampled loads, whose surfaces only graphics pipelines create and
    * lay out, exports, interpolation and LDS parameter loads, and the vector encodings of interpolation, whose operands
    * name attributes, and v_interp_p2_hi_f16, whose catalogue record names none. Image loads, stores and atomics and
    * typed buffer access compute on kernels, so the pipeline gives their reason.
    */
  private def graphics(mnemonic: String, value: Metadata.Encoding): Boolean =
    mnemonic.contains("_bvh") || mnemonic.startsWith("v_interp_") ||
      (value.format.isImage &&
        (value.operands.exists(info =>
          info.field.contains(EncodingField.SSAMP) || info.field.contains(EncodingField.SAMP)
        ) || mnemonic.contains("_msaa_"))) ||
      (value.format match {
        case EncodingFormat.EXP | EncodingFormat.VEXPORT | EncodingFormat.VINTERP | EncodingFormat.VINTERP_INREG |
            EncodingFormat.VINTRP | EncodingFormat.LDSDIR | EncodingFormat.VDSDIR => true
        case _ => false
      }) || value.operands.exists(info => info.kind == OperandKind.ATTR || info.kind == OperandKind.PARAM)

  private val NamedBarrierSetup =
    Set("s_barrier_init", "s_barrier_join", "s_barrier_leave", "s_get_barrier_state", "s_wakeup_barrier")

  private val ScratchWidths = Vec("dword", "dwordx2", "dwordx4")
  private val ScalarScratch = Vec("load", "store").flatMap(access =>
    ScratchWidths.map(width => s"s_scratch_${access}_$width")
  ).toSet

  private val NumberedPrefixes = Vec("s", "v", "ttmp")

  private val VgprBlocks = Set("global_load_block", "global_store_block", "scratch_load_block", "scratch_store_block")

  /** Why the VM leaves out a mnemonic the hardware executes, where nothing in a compute dispatch gives it a meaning or
    * no toolchain writes it: the graphics pipeline's fixed-function state, and opcodes only an opcode table lists.
    */
  private def outside(mnemonic: String): Option[String] = mnemonic match {
    case "v_screen_partition_4se_b32" =>
      Some(
        "It looks up the rasteriser's table of shader-engine masks to partition the screen across four shader " +
          "engines. A compute dispatch has no screen, and the ISA description gives its purpose but not the table."
      )
    case "s_endpgm_ordered_ps_done" =>
      Some("It ends a pixel shader on leaving its primitive-ordered critical section, which compute waves never enter.")
    case "ds_add_gs_reg_rtn" | "ds_sub_gs_reg_rtn" =>
      Some(
        "It updates a geometry-shader register held in GDS to count streamout output, which only the geometry shader " +
          "stage uses."
      )
    case "s_mov_fed_b32" =>
      Some(
        "Only Mesa's ACO opcode table lists it, for GFX6, GFX7 and GFX10, and the RDNA ISA descriptions do not; ACO " +
          "never selects it and LLVM does not define it, so no toolchain writes it."
      )
    case "ds_condxchg32_rtn_b128" =>
      Some(
        "Only Mesa's ACO opcode table lists it, for GFX7 and GFX8; ACO never selects it and LLVM does not define " +
          "it, so no toolchain writes it."
      )
    case "v_mfma_ld_scale_b32" =>
      Some(
        "It loads the block scales of the next f8f6f4 matrix instruction into hidden state. The VM runs it only " +
          "fused with that instruction as v_mfma_scale_*, which encodes both."
      )
    case _ => None
  }

  /** Why the VM leaves out a mnemonic compute kernels can run, where no public source specifies what it does: the ISA
    * description gives one sentence and LLVM only the operands.
    */
  private def unspecified(mnemonic: String): Option[String] = mnemonic match {
    case "s_sendmsg_rtn_b32" =>
      Some(
        "No public source gives what it returns for MSG_RTN_GET_REALTIME, the real-time clock the VM reads through " +
          "s_sendmsg_rtn_b64; its other replies, the doorbell, the draw or dispatch ID, the trap handler's addresses " +
          "and wave save, come from the queue and trap handler, which the VM does not have."
      )
    case scratch if ScalarScratch(scratch) =>
      Some(
        "It reaches the wave's scratch through the scalar scratch aperture, which LLVM defines but never selects. No " +
          "public source gives the address it forms within the wave's scratch, which the VM keeps per lane."
      )
    case block if VgprBlocks(block) =>
      Some(
        "It moves 32 consecutive VGPRs as a block and reads M0; LLVM saves and restores callee-saved VGPRs with the " +
          "scratch forms. No public source gives the block's layout in memory or what M0 selects."
      )
    case "global_atomic_ordered_add_b64" =>
      Some(
        "It increments the value of an (ID, value) pair in memory where the ID matches the shader's, and LLVM " +
          "selects it for llvm.amdgcn.global.atomic.ordered.add.b64. No public source gives how the pair packs into " +
          "64 bits or what it returns."
      )
    case _ => None
  }

  /** The reason the encoding gives where it has one, such as graphics or a record no toolchain writes, or else the
    * reason the instances give: a VM rejection before machine mechanics, and those before instances that do not
    * assemble to the form, so the gap an instance exposes shows even where other instances have definitions.
    */
  private def diagnose(entry: View.Entry, form: View.Form, computed: => Outcomes): Unavailable = {
    import Unavailable.*
    encoding(entry.target, entry.mnemonic, form) match {
      case None => NotInstantiated("No encoding of the form for the target.")
      case Some((_, value)) if graphics(entry.mnemonic, value) =>
        NotModelled(
          if (entry.mnemonic.contains("_bvh")) s"${entry.mnemonic} belongs to the ray-tracing family."
          else s"${value.format} encodes graphics: sampling, multisampling, exports or interpolation."
        )
      case Some((found, _)) if NamedBarrierSetup(entry.mnemonic) && found.isa == ISA.RDNA4 =>
        NotModelled(
          "It sets up or reads named barriers, which LLVM's execution synchronization model gives GFX12.5 alone; AMD's " +
            "RDNA4 ISA lists none of these instructions."
        )
      case Some((_, value)) =>
        outside(entry.mnemonic).map(NotModelled(_))
          .orElse(unspecified(entry.mnemonic).map(NotImplemented(_)))
          .orElse(AMDGPUInstances.unsupported(entry.mnemonic, value).map(NotImplemented(_)))
          .orElse(AMDGPUInstances.unwritable(entry.mnemonic, value).map(NotModelled(_)))
          .getOrElse {
            val found    = computed
            val failures = found.flatMap(_.outcome.left.toOption)
            if (found.isEmpty) NotInstantiated("No instance of the form can be written.")
            else failures.collectFirst { case Unrendered.Rejected(reason) => NotImplemented(reason) }
              .getOrElse(Unavailable.of(failures))
          }
    }
  }
}
