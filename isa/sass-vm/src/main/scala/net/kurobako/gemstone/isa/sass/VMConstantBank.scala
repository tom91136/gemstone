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
  private def section(bank: Int)(using context: VMCompilationContext): Option[Cubin.Section] =
    context.module.sections.find(section =>
      section.name == s".nv.constant$bank.${context.kernel.name}" || section.name == s".nv.constant$bank"
    )

  private def index(bank: BigInt)(using context: VMCompilationContext): Int =
    if (!bank.isValidInt || bank < 0) context.unsupported("invalid constant bank") else bank.toInt

  /** Base of the c0 launch dimensions (block, then grid); Blackwell inserts a word between them. */
  private def dimensions(sm: Int): Int =
    if (sm < 50) 0x28 else if (sm < 70) 8 else if (sm >= 100) 0x360 else 0

  def dynamic(bankValue: BigInt, offset: Operand.Address, bits: ScalarBitWidth)(using
      context: VMCompilationContext
  ): Value = {
    import context.*
    val bank        = index(bankValue)
    val bankSection = section(bank).getOrElse(unsupported("unknown dynamic constant bank"))
    if (module.sections.exists(s => s.isRelocation && s.info == bankSection.index.raw && s.size.raw != 0))
      unsupported("dynamic access to relocated constants")
    if (offset.index.nonEmpty || offset.mode.nonEmpty || offset.scale.raw != 1 || offset.descriptor.nonEmpty)
      unsupported("unsupported dynamic constant offset")
    val parameters   = bank == 0 && kernel.parameterSize.raw > 0
    val displacement = offset.displacement - (if (parameters) kernel.parameterBase.raw else 0)
    val wideOffset   = widen(source(offset.base.getOrElse(unsupported("missing dynamic constant index"))))
    if (displacement != 0)
      emit(IntegerBinary(IntegerOperation.Add, wideOffset.slot, wideOffset, literal(Operand.Integer(displacement)), 64))
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

  def static(bankValue: BigInt, wideOffset: Long, bits: ScalarBitWidth)(using context: VMCompilationContext): Value = {
    import context.*
    val bank = index(bankValue)
    if (wideOffset < 0 || wideOffset > Int.MaxValue) unsupported("constant offset out of range")
    val offset                                                   = wideOffset.toInt
    val bytes                                                    = bits.toScalarByteWidth.raw
    val result                                                   = temp()
    val launch                                                   = dimensions(sm)
    val grid                                                     = launch + (if (sm >= 100) 16 else 12)
    val c0                                                       = bank == 0
    val verified                                                 = verifiedLegacy
    def constant(value: Long, width: ScalarBitWidth = 32): Value = {
      emit(Move(result.slot, Value.Immediate(value), width))
      result
    }
    if (
      c0 && offset >= kernel.parameterBase.raw && offset + bytes <= kernel.parameterBase.raw + kernel.parameterSize.raw
    ) {
      emit(Load(
        result.slot,
        Value.Immediate(VMAddressSpace.ParameterBase + offset - kernel.parameterBase.raw),
        bits,
        false,
        MemoryAccess.Generic(false)
      ))
      result
    } else if (
      c0 && bits.raw == 32 &&
      (offset - launch) % 4 == 0 &&
      ((offset >= launch && offset < launch + 12) || (offset >= grid && offset < grid + 12))
    ) {
      val axes = Vec(LaunchGeometry.Axis.X, LaunchGeometry.Axis.Y, LaunchGeometry.Axis.Z)
      val word = (offset - launch) / 4 - (if (sm >= 100 && offset >= launch + 16) 1 else 0)
      emit(Move(result.slot, if (word < 3) Value.BlockDim(axes(word)) else Value.GridDim(axes(word - 3)), 32))
      result
    }
    // Legacy cvta.shared.to.generic uses this c0 field as the shared-space tag.
    else if (c0 && sm < 50 && bits.raw == 32 && offset == 0x20) constant(VMAddressSpace.SharedBase)
    // CUDA 11.8 cvta.shared uses c0[0] for the low shared-aperture word.
    else if (c0 && verified && bits.raw == 32 && offset == 0) constant(VMAddressSpace.SharedBase)
    // Local addresses occupy the VM's low aperture. Pascal additionally reads the high aperture words for shared and
    // local cvta.
    else if (c0 && verified && bits.raw == 32 && (offset == 4 || (sm == 60 && Set(0x100, 0x104)(offset)))) constant(0L)
    // Modern cvta.shared.to.generic adds the shared aperture base from c0[0x18:0x20].
    else if (c0 && sm >= 70 && sm < 90 && offset == 0x18 && Set(32, 64)(bits.raw))
      constant(VMAddressSpace.SharedBase, bits)
    else if (c0 && sm >= 70 && sm < 90 && offset == 0x1c && bits.raw == 32) constant(0L)
    // Stack origin and opaque global-memory descriptor. Global accesses use checked VM addresses.
    else if (
      c0 && Set(
        if (sm < 50) 0x44 else if (sm < 70) 0x20 else if (sm >= 100) 0x37c else 0x28,
        if (sm >= 100) 0x358 else if (sm >= 90) 0x208 else 0x118
      )(offset)
    ) constant(0L, if (bits.raw == 64) 64 else 32)
    else {
      val bankSection = section(bank).getOrElse(unsupported(s"unknown constant bank $bank offset $offset"))
      if (offset + bytes > bankSection.size.raw) unsupported("constant load outside bank")
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
            if (bits.raw == 64) 64 else 32
          )
      }
    }
  }
}
