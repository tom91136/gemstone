package net.kurobako.gemstone.isa.spirv

private object ResultIds {
  inline def apply(resultType: Int, dest: Int): ResultIds = (resultType.toLong << 32) | (dest.toLong & 0xffffffffL)

  inline def apply(resultType: Operand, dest: Operand): ResultIds = apply(Operand.id(resultType), Operand.id(dest))

  extension (ids: ResultIds) {
    inline def resultType: Int = (ids >>> 32).toInt
    inline def dest: Int       = ids.toInt
  }
}

/** The result type id and result id of an instruction, packed into one value. */
private opaque type ResultIds = Long
