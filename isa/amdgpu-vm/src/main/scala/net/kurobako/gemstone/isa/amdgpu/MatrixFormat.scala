package net.kurobako.gemstone.isa.amdgpu

/** The element format of a matrix operand, `width` bits packed little-endian across its registers. */
private[isa] enum MatrixFormat(val width: Int) derives CanEqual {
  case F64     extends MatrixFormat(64)
  case F32     extends MatrixFormat(32)
  case XF32    extends MatrixFormat(32)
  case F16     extends MatrixFormat(16)
  case BF16    extends MatrixFormat(16)
  case I32     extends MatrixFormat(32)
  case I8      extends MatrixFormat(8)
  case U8      extends MatrixFormat(8)
  case I4      extends MatrixFormat(4)
  case U4      extends MatrixFormat(4)
  case FP8FNUZ extends MatrixFormat(8)
  case BF8FNUZ extends MatrixFormat(8)
  case FP8     extends MatrixFormat(8)
  case BF8     extends MatrixFormat(8)
  case FP6     extends MatrixFormat(6)
  case BF6     extends MatrixFormat(6)
  case FP4     extends MatrixFormat(4)

  def integer: Boolean = this match {
    case I32 | I8 | U8 | I4 | U4 => true
    case _                       => false
  }
}
