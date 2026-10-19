package net.kurobako.gemstone.isa.spirv

/** The staged semantics families of core instructions, in the order consumers try them. No two families take one
  * opcode, so the order decides nothing but how soon a consumer finds the family.
  */
enum CoreFamily derives CanEqual {
  case Integer, BitField, Extended, Comparison, Floating, OrderedComparison, UnorderedComparison, Classification,
    Conversion, Atomic, Pointer, IntegerDot, FusedMultiplyAdd, Fence, IntelInteger, IntelConversion, MixedDot, Freeze,
    BitcastExtract, CooperativeMatrix
}
