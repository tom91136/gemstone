package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.constraint.*

/** Closed semantic vocabularies for the portions of PTX operands that have known meaning. */
object OperandDomains {
  object RegisterPart {
    def fromSpelling(value: NonEmpty[String]): RegisterPart = value.raw match {
      case "lo" => RegisterPart.Lo
      case "hi" => RegisterPart.Hi
      case "b0" => RegisterPart.B0
      case "b1" => RegisterPart.B1
      case "b2" => RegisterPart.B2
      case "b3" => RegisterPart.B3
      case _    => RegisterPart.Unknown(value)
    }
  }

  enum RegisterPart(val spelling: NonEmpty[String]) {
    case Lo                               extends RegisterPart("lo")
    case Hi                               extends RegisterPart("hi")
    case B0                               extends RegisterPart("b0")
    case B1                               extends RegisterPart("b1")
    case B2                               extends RegisterPart("b2")
    case B3                               extends RegisterPart("b3")
    case Unknown(value: NonEmpty[String]) extends RegisterPart(value)
  }

  object QualifiedOperand {
    def fromSpelling(value: NonEmpty[String]): QualifiedOperand = value.raw match {
      case "x" => QualifiedOperand.X
      case "y" => QualifiedOperand.Y
      case "z" => QualifiedOperand.Z
      case "w" => QualifiedOperand.W
      case _   => QualifiedOperand.Unknown(value)
    }
  }

  enum QualifiedOperand(val spelling: NonEmpty[String]) {
    case X                                extends QualifiedOperand("x")
    case Y                                extends QualifiedOperand("y")
    case Z                                extends QualifiedOperand("z")
    case W                                extends QualifiedOperand("w")
    case Unknown(value: NonEmpty[String]) extends QualifiedOperand(value)
  }

  object SpecialRegister {
    def fromSpelling(value: NonEmpty[String]): SpecialRegister = value.raw match {
      case "%tid.x"             => SpecialRegister.TidX
      case "%tid.y"             => SpecialRegister.TidY
      case "%tid.z"             => SpecialRegister.TidZ
      case "%ctaid.x"           => SpecialRegister.CtaidX
      case "%ctaid.y"           => SpecialRegister.CtaidY
      case "%ctaid.z"           => SpecialRegister.CtaidZ
      case "%ntid.x"            => SpecialRegister.NtidX
      case "%ntid.y"            => SpecialRegister.NtidY
      case "%ntid.z"            => SpecialRegister.NtidZ
      case "%nctaid.x"          => SpecialRegister.NctaidX
      case "%nctaid.y"          => SpecialRegister.NctaidY
      case "%nctaid.z"          => SpecialRegister.NctaidZ
      case "%dynamic_smem_size" => SpecialRegister.DynamicSmemSize
      case "%laneid"            => SpecialRegister.LaneId
      case "%lanemask_ge"       => SpecialRegister.LaneMaskGe
      case "%lanemask_gt"       => SpecialRegister.LaneMaskGt
      case "%lanemask_le"       => SpecialRegister.LaneMaskLe
      case "%lanemask_lt"       => SpecialRegister.LaneMaskLt
      case "%lanemask_eq"       => SpecialRegister.LaneMaskEq
      case _                    => SpecialRegister.Unknown(value)
    }
  }

  enum SpecialRegister(val spelling: NonEmpty[String]) {
    case TidX                             extends SpecialRegister("%tid.x")
    case TidY                             extends SpecialRegister("%tid.y")
    case TidZ                             extends SpecialRegister("%tid.z")
    case CtaidX                           extends SpecialRegister("%ctaid.x")
    case CtaidY                           extends SpecialRegister("%ctaid.y")
    case CtaidZ                           extends SpecialRegister("%ctaid.z")
    case NtidX                            extends SpecialRegister("%ntid.x")
    case NtidY                            extends SpecialRegister("%ntid.y")
    case NtidZ                            extends SpecialRegister("%ntid.z")
    case NctaidX                          extends SpecialRegister("%nctaid.x")
    case NctaidY                          extends SpecialRegister("%nctaid.y")
    case NctaidZ                          extends SpecialRegister("%nctaid.z")
    case DynamicSmemSize                  extends SpecialRegister("%dynamic_smem_size")
    case LaneId                           extends SpecialRegister("%laneid")
    case LaneMaskGe                       extends SpecialRegister("%lanemask_ge")
    case LaneMaskGt                       extends SpecialRegister("%lanemask_gt")
    case LaneMaskLe                       extends SpecialRegister("%lanemask_le")
    case LaneMaskLt                       extends SpecialRegister("%lanemask_lt")
    case LaneMaskEq                       extends SpecialRegister("%lanemask_eq")
    case Unknown(value: NonEmpty[String]) extends SpecialRegister(value)
  }

}
