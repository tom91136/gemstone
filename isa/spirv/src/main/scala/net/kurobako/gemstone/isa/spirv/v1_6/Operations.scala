package net.kurobako.gemstone.isa.spirv.v1_6

private[v1_6] object Operations {
  enum FloatingOperation(val opcode: String) {
    case Add      extends FloatingOperation("OpFAdd")
    case Subtract extends FloatingOperation("OpFSub")
    case Multiply extends FloatingOperation("OpFMul")
    case Divide   extends FloatingOperation("OpFDiv")
    case Minimum  extends FloatingOperation("OpenCL.std fmin")
    case Maximum  extends FloatingOperation("OpenCL.std fmax")
  }

  enum FloatingComparison(val opcode: String) {
    case OrderedEqual              extends FloatingComparison("OpFOrdEqual")
    case OrderedNotEqual           extends FloatingComparison("OpFOrdNotEqual")
    case Ordered                   extends FloatingComparison("OpOrdered")
    case UnorderedEqual            extends FloatingComparison("OpFUnordEqual")
    case UnorderedNotEqual         extends FloatingComparison("OpFUnordNotEqual")
    case UnorderedLessThan         extends FloatingComparison("OpFUnordLessThan")
    case UnorderedGreaterThan      extends FloatingComparison("OpFUnordGreaterThan")
    case UnorderedLessThanEqual    extends FloatingComparison("OpFUnordLessThanEqual")
    case UnorderedGreaterThanEqual extends FloatingComparison("OpFUnordGreaterThanEqual")
    case Unordered                 extends FloatingComparison("OpUnordered")
    case OrderedLessThan           extends FloatingComparison("OpFOrdLessThan")
    case OrderedLessThanEqual      extends FloatingComparison("OpFOrdLessThanEqual")
    case OrderedGreaterThan        extends FloatingComparison("OpFOrdGreaterThan")
    case OrderedGreaterThanEqual   extends FloatingComparison("OpFOrdGreaterThanEqual")
  }

  enum FloatingClassification(val opcode: String) {
    case Infinite extends FloatingClassification("OpIsInf")
    case NaN      extends FloatingClassification("OpIsNan")
    case Finite   extends FloatingClassification("OpIsFinite")
    case Normal   extends FloatingClassification("OpIsNormal")
    case Signed   extends FloatingClassification("OpSignBitSet")
  }
}
