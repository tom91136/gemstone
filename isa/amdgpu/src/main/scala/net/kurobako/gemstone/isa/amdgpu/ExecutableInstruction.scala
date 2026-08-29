package net.kurobako.gemstone.isa.amdgpu

private[amdgpu] enum ScalarBranchCondition {
  case Always, SccZero, SccNonZero, VccZero, VccNonZero, ExecZero, ExecNonZero
}

private[amdgpu] enum InstructionSource {
  case Scalar(index: Int)
  case VectorRegister(index: Int)
  case Constant(value: Long)
}

private[amdgpu] enum SubDwordSelection(val width: Int, val shift: Int) {
  case Byte0 extends SubDwordSelection(8, 0)
  case Byte1 extends SubDwordSelection(8, 8)
  case Byte2 extends SubDwordSelection(8, 16)
  case Byte3 extends SubDwordSelection(8, 24)
  case Word0 extends SubDwordSelection(16, 0)
  case Word1 extends SubDwordSelection(16, 16)
  case DWord extends SubDwordSelection(32, 0)
}

enum ExecutableInstruction {
  case SLoad(dest: Int, count: Int, base: Int, offset: Int)
  case SMove(dest: Int, src: InstructionSource)
  case SMoveImmediate(dest: Int, value: Int)
  case SMultiplyImmediate(dest: Int, value: Int)
  case SMove64(dest: Int, src: InstructionSource)
  case SAndSaveExec(dest: Int, src: InstructionSource)
  case SBinary(opcode: String, dest: Int, srcA: InstructionSource, srcB: InstructionSource)
  case SBinary64(opcode: String, dest: Int, srcA: InstructionSource, srcB: InstructionSource)
  case SCSelect(dest: Int, srcA: InstructionSource, srcB: InstructionSource, width: Int)
  case SCmpEqI32(src: Int, immediate: Int)
  case SCmp(opcode: String, srcA: InstructionSource, srcB: InstructionSource)
  case VMove(dest: Int, src: InstructionSource)
  case VBitReverse(dest: Int, src: InstructionSource)
  case VMoveDPP(
      dest: Int,
      src: Int,
      control: Int,
      rowMask: Int,
      bankMask: Int,
      boundControl: Boolean,
      fetchInactive: Boolean
  )
  case VCndMask(dest: Int, srcA: InstructionSource, srcB: InstructionSource, condition: InstructionSource)
  case VReadFirstLane(dest: Int, src: InstructionSource)
  case VMaskBitCount(dest: Int, mask: InstructionSource, base: InstructionSource, high: Boolean)
  case VBinary(opcode: String, dest: Int, srcA: InstructionSource, srcB: InstructionSource)
  case VBinarySubDword(
      opcode: String,
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      destSelection: SubDwordSelection,
      srcASelection: SubDwordSelection,
      srcBSelection: SubDwordSelection
  )
  case VPackedBinary(
      opcode: String,
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcAHighForHigh: Boolean,
      srcBHighForHigh: Boolean
  )
  case VAddCarry(
      dest: Int,
      carryDest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      carryIn: Option[InstructionSource]
  )
  case VTernary(opcode: String, dest: Int, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
  case VMadU64U32(dest: Int, carryDest: Int, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
  case VFloat64Add(dest: Int, srcA: InstructionSource, srcB: InstructionSource, negateB: Boolean)
  case VCompare(opcode: String, srcA: InstructionSource, srcB: InstructionSource)
  case VCompareTo(dest: Int, opcode: String, srcA: InstructionSource, srcB: InstructionSource)
  case VShiftLeft64(dest: Int, amount: InstructionSource, src: InstructionSource)
  case GlobalLoad(dest: Int, address: Int, scalarAddress: Option[Int], bytes: Int, offset: Int)
  case GlobalLoadD16(dest: Int, address: Int, scalarAddress: Option[Int], offset: Int, high: Boolean)
  case GlobalStore(address: Int, scalarAddress: Option[Int], src: Int, bytes: Int, offset: Int)
  case GlobalStoreD16High(address: Int, scalarAddress: Option[Int], src: Int, bytes: Int, offset: Int)
  case FlatLoad(dest: Int, address: Int, bytes: Int, offset: Int)
  case FlatStore(address: Int, src: Int, bytes: Int, offset: Int)
  case DSRead(dest: Int, address: Int, bytes: Int, offsetA: Int, offsetB: Option[Int])
  case DSBPermute(dest: Int, address: Int, src: Int)
  case DSWrite(address: Int, srcA: Int, srcB: Option[Int], bytes: Int, offsetA: Int, offsetB: Option[Int])
  case DSWriteD16High(address: Int, src: Int, offset: Int)
  case BranchRelative(condition: ScalarBranchCondition, displacement: Int)
  case Branch(condition: ScalarBranchCondition, target: Int)
  case Barrier
  case Wait
  case End
}
