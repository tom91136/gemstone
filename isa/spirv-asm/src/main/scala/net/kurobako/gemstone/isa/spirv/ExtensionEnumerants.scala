package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** Enumerants that extension specifications define outside the core grammar and that their producers emit: the
  * component flags SPV_INTEL_joint_matrix adds to the cooperative matrix operands, which Intel's compilers set for
  * bfloat16 and TF32 matrices.
  */
object ExtensionEnumerants {
  private def flag(name: NonEmpty[String], value: Long): OperandEnumerant =
    OperandEnumerant(name, value, Vec.empty, Vec.empty)

  private val extensions: Map[OperandKind, Vec[OperandEnumerant]] = Map(
    OperandKind.CooperativeMatrixOperands -> Vec(
      flag("MatrixAAndBTF32ComponentsINTEL", 0x20L),
      flag("MatrixAAndBBFloat16ComponentsINTEL", 0x40L),
      flag("MatrixCBFloat16ComponentsINTEL", 0x80L),
      flag("MatrixResultBFloat16ComponentsINTEL", 0x100L)
    )
  )

  /** The grammar's enumerants of `info`, then the extensions'. */
  def of(info: OperandKindInfo): Vec[OperandEnumerant] = info.enumerants ++ extensions.getOrElse(info.kind, Vec.empty)
}
