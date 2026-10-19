package net.kurobako.gemstone.isa.amdgpu.asm

enum RegisterFile derives CanEqual {
  case Scalar, VectorRegister, Accumulator, TrapTemporary
}
