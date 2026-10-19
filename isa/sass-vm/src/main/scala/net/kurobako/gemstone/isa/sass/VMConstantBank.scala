package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.vm.*

/** Constant-bank reads: kernel parameters, the driver-owned launch fields of bank 0, and bound or initialised user
  * banks.
  */
private object VMConstantBank {
  private val Axes = Vec(LaunchGeometry.Axis.X, LaunchGeometry.Axis.Y, LaunchGeometry.Axis.Z)
  private def section(bank: Int)(using context: VMCompilationContext): Option[Cubin.Section] =
    context.module.sections.find(section =>
      section.name == s".nv.constant$bank.${context.kernel.name}" || section.name == s".nv.constant$bank"
    )

  private def index(bank: BigInt)(using context: VMCompilationContext): Int =
    if (!bank.isValidInt || bank < 0) context.unsupported("invalid constant bank") else bank.toInt

  /** The driver's cluster table SR_CgaSize selects; the VM keeps one, at the table's own offsets. */
  val ClusterTable: Long = 0L

  /** The cluster launch field of c0 at `offset`: the cluster shape, its float reciprocals for dividing block indices,
    * the grid of clusters and the blocks per cluster, from 0x144 on sm_90 and in the table at 0x2a0 from sm_100.
    */
  private def clusterField(offset: Int)(using context: VMCompilationContext): Option[Value] = {
    import context.*
    val table                                         = if (sm >= 100) 0x2a0 else 0x144
    val count                                         = if (sm >= 100) 0x2cc else 0x188
    def axis(start: Int): Option[LaunchGeometry.Axis] =
      Option.when(offset >= start && offset < start + 12 && (offset - start) % 4 == 0)(Axes((offset - start) / 4))
    axis(table).map(Value.ClusterShape(_))
      .orElse(axis(table + (if (sm >= 100) 0x10 else 0xc)).map { dimension =>
        val shape = convert(
          Value.ClusterShape(dimension),
          NumericFormat.Unsigned(32),
          NumericFormat.IEEE(32),
          Some(ConversionRounding.RN)
        )
        val result = temp()
        emit(FloatUnary(FloatUnaryOperation.Reciprocal, result.slot, shape, 32))
        result
      })
      .orElse(axis(table + (if (sm >= 100) 0x20 else 0x18)).map(Value.ClusterCount(_)))
      .orElse(Option.when(offset == count)(Value.ClusterCtaCount))
  }

  private def clusterOffset(offset: Int): Boolean =
    offset >= 0x144 && offset < 0x168 || offset == 0x188

  /** Base of the c0 launch dimensions (block, then grid); Blackwell inserts a word between them. */
  private def dimensions(sm: Int): Int =
    if (sm < 50) 0x28 else if (sm < 70) 8 else if (sm >= 100) 0x360 else 0

  def dynamic(bankValue: BigInt, offset: Operand.Address, bits: ScalarBitWidth)(using
      context: VMCompilationContext
  ): Value = {
    import context.*
    val bank        = index(bankValue)
    val bankSection = section(bank).getOrElse(unsupported("unknown dynamic constant bank"))
    val relocated   = module.sections.exists(s => s.isRelocation && s.info == bankSection.index.raw && s.size != 0)
    if (relocated && bank == 0) unsupported("dynamic access to relocated constants")
    if (offset.index.nonEmpty || offset.mode.nonEmpty || offset.scale != 1 || offset.descriptor.nonEmpty)
      unsupported("unsupported dynamic constant offset")
    // ptxas indexes the relocated function table of bank 4 only on paths to driver helpers the VM does not provide,
    // such as sm_120 copies into another cluster CTA.
    if (relocated) {
      emit(Trap)
      Value.Immediate(0L)
    } else if (bank == 0 && sm >= 100 && offset.index.isEmpty && offset.displacement < kernel.parameterBase.raw) {
      val field = clusterField(offset.displacement.toInt).getOrElse(unsupported("unknown dynamic driver constant"))
      val index = source(offset.base.getOrElse(unsupported("missing dynamic constant index")))
      guarded(Trap, compare(Comparison.NotEqual, NumericFormat.Unsigned(32), index, Value.Immediate(ClusterTable)))
      field
    } else {
      val parameters   = bank == 0 && kernel.parameterSize > 0
      val displacement = offset.displacement - (if (parameters) kernel.parameterBase.raw else 0)
      val wideOffset   = widen(source(offset.base.getOrElse(unsupported("missing dynamic constant index"))))
      if (displacement != 0)
        emit(IntegerBinary(
          IntegerOperation.Add,
          wideOffset.slot,
          wideOffset,
          literal(Operand.Integer(displacement)),
          64
        ))
      val base = Value.Immediate(
        if (parameters) VMAddressSpace.ParameterBase
        else boundSymbols.get(Cubin.constantSymbol(bankSection.index)).fold(0L)(_.raw)
      )
      val result = temp()
      emit(VMMemoryCompiler.loadOf(
        Vec(result),
        binary(IntegerOperation.Add, base, wideOffset, 64),
        bits,
        false,
        if (parameters) MemoryAccess.Generic(false) else MemoryAccess.Global(false)
      ))
      result
    }
  }

  def static(bankValue: BigInt, wideOffset: Long, bits: ScalarBitWidth)(using context: VMCompilationContext): Value = {
    import context.*
    val bank = index(bankValue)
    if (wideOffset < 0 || wideOffset > Int.MaxValue) unsupported("constant offset out of range")
    val offset                                                   = wideOffset.toInt
    val bytes                                                    = bits.toScalarByteWidth
    val result                                                   = temp()
    val launch                                                   = dimensions(sm)
    val grid                                                     = launch + (if (sm >= 100) 16 else 12)
    val c0                                                       = bank == 0
    val verified                                                 = verifiedLegacy
    def constant(value: Long, width: ScalarBitWidth = 32): Value = {
      emit(Move(result.slot, Value.Immediate(value), width))
      result
    }
    def isDimension(at: Int): Boolean =
      c0 &&
        (at - launch) % 4 == 0 &&
        ((at >= launch && at < launch + 12) || (at >= grid && at < grid + 12))
    def dimension(at: Int): Value = {
      val word = (at - launch) / 4 - (if (sm >= 100 && at >= launch + 16) 1 else 0)
      if (word < 3) Value.BlockDim(Axes(word)) else Value.GridDim(Axes(word - 3))
    }
    if (c0 && offset >= kernel.parameterBase && offset + bytes <= kernel.parameterBase.raw + kernel.parameterSize) {
      emit(Load(
        result.slot,
        Value.Immediate(VMAddressSpace.ParameterBase + offset - kernel.parameterBase),
        bits,
        false,
        MemoryAccess.Generic(false)
      ))
      result
    } else if (bits == 32 && isDimension(offset)) {
      emit(Move(result.slot, dimension(offset), 32))
      result
    }
    // ULDC.64 reads two dimensions at once, as ptxas multiplies %ntid.x by %ntid.y from sm_75
    else if (bits == 64 && isDimension(offset) && isDimension(offset + 4)) {
      emit(MoveCombine(result.slot, Vec(dimension(offset), dimension(offset + 4)), 32))
      result
    }
    // Legacy cvta.shared.to.generic uses this c0 field as the shared-space tag.
    else if (c0 && sm < 50 && bits == 32 && offset == 0x20) constant(VMAddressSpace.SharedBase)
    // CUDA 11.8 cvta.shared uses c0[0] for the low shared-aperture word.
    else if (c0 && verified && bits == 32 && offset == 0) constant(VMAddressSpace.SharedBase)
    // Local addresses occupy the VM's low aperture. Pascal additionally reads the high aperture words for shared and
    // local cvta.
    else if (c0 && verified && bits == 32 && (offset == 4 || (sm == 60 && Set(0x100, 0x104)(offset)))) constant(0L)
    // Modern cvta.shared.to.generic adds the shared aperture base from c0[0x18:0x20].
    else if (c0 && sm >= 70 && sm < 90 && offset == 0x18 && PartialFunction.cond(bits.raw) { case 32 | 64 => true })
      constant(VMAddressSpace.SharedBase, bits)
    else if (c0 && sm >= 70 && sm < 90 && offset == 0x1c && bits == 32) constant(0L)
    // The generic address of the parameter block, from which ptxas addresses __grid_constant__ tensor maps.
    else if (
      c0 && sm >= 90 && offset == (if (sm >= 100) 0x348 else 0x198) &&
      PartialFunction.cond(bits.raw) { case 32 | 64 => true }
    )
      constant(VMAddressSpace.ParameterBase, bits)
    else if (c0 && sm >= 90 && offset == (if (sm >= 100) 0x34c else 0x19c) && bits == 32)
      constant(VMAddressSpace.ParameterBase >>> 32)
    else if (c0 && sm >= 90 && bits == 32 && offset == (if (sm >= 100) 0x36c else 0x140)) {
      emit(Move(result.slot, Value.ExplicitCluster, 32))
      result
    } else if (c0 && sm >= 90 && sm < 100 && bits == 32 && clusterOffset(offset)) clusterField(offset).get
    // ptxas reads %nsmid from this driver field.
    else if (c0 && sm >= 70 && bits == 32 && offset == (if (sm >= 100) 0x2d0 else 0x10c))
      constant(EmulatedDevice.Multiprocessors.toLong)
    // Stack origin and opaque global-memory descriptor. Global accesses use checked VM addresses.
    else if (
      c0 && Set(
        if (sm < 50) 0x44 else if (sm < 70) 0x20 else if (sm >= 100) 0x37c else 0x28,
        if (sm >= 100) 0x358 else if (sm >= 90) 0x208 else 0x118
      )(offset)
    ) constant(0L, if (bits == 64) 64 else 32)
    else {
      val bankSection = section(bank).getOrElse(unsupported(s"unknown constant bank $bank offset $offset"))
      if (offset + bytes > bankSection.size) unsupported("constant load outside bank")
      module.sections.filter(s => s.isRelocation && s.info == bankSection.index.raw).foreach { table =>
        Cubin.relocationEntries(table).fold(_ => unsupported("invalid constant relocation table"), identity).foreach {
          entry =>
            if (entry.offset < offset + bytes && entry.offset + 8 > offset)
              unsupported("relocated constant address is not implemented")
        }
      }
      boundSymbols.get(Cubin.constantSymbol(bankSection.index)) match {
        case Some(base) =>
          emit(VMMemoryCompiler.loadOf(
            Vec(result),
            Value.Immediate(base.raw + offset.toLong),
            bits,
            false,
            MemoryAccess.Global(false)
          ))
          result
        // Without a runtime binding, the cubin initialiser is the bank's only storage.
        case None => constant(
            LittleEndian.decode(bits.toScalarByteWidth)(index => bankSection.bytes(offset + index)),
            if (bits == 64) 64 else 32
          )
      }
    }
  }
}
