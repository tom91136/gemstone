package net.kurobako.gemstone.isa.spirv.v1_6

private object VMScalarIntegerInstruction {
  val Add: Byte      = 0
  val Subtract: Byte = 1
  val Multiply: Byte = 2
}

private class VMScalarIntegerInstruction(
    val operation: Byte,
    val resultType: Int,
    val destination: Int,
    val left: Int,
    val right: Int,
    val leftConstant: Boolean,
    val rightConstant: Boolean,
    val leftConstantBits: Long,
    val rightConstantBits: Long,
    val resultMask: Long
)
