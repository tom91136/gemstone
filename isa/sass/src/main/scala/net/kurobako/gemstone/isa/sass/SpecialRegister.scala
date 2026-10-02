package net.kurobako.gemstone.isa.sass

import OperandDomains.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object SpecialRegister {
  private val known = Vec(
    SRZ,
    SR_TID_X,
    SR_TID_Y,
    SR_TID_Z,
    SR_CTAID_X,
    SR_CTAID_Y,
    SR_CTAID_Z,
    SR_NTID_X,
    SR_NTID_Y,
    SR_NTID_Z,
    UR_TID_X,
    UR_TID_Y,
    UR_TID_Z,
    UR_CTAID_X,
    UR_CTAID_Y,
    UR_CTAID_Z,
    SR_LANEID,
    SR_WARPID,
    SR_CLOCKLO,
    SR_CLOCKHI,
    SR_PM0,
    SR_SMID,
    SR_GRIDID,
    SR_GLOBALTIMERLO,
    SR_GLOBALTIMERHI,
    SR_GEMASK,
    SR_GTMASK,
    SR_LEMASK,
    SR_LTMASK,
    SR_EQMASK,
    PR,
    desc
  )
  def fromSpelling(value: String): SpecialRegister = known.find(_.spelling == value).getOrElse(Unknown(nonEmpty(value)))
}

enum SpecialRegister(val spellingValue: NonEmpty[String]) {
  inline def spelling: String = spellingValue
  case SRZ                                       extends SpecialRegister("SRZ")
  case SR_TID_X                                  extends SpecialRegister("SR_TID.X")
  case SR_TID_Y                                  extends SpecialRegister("SR_TID.Y")
  case SR_TID_Z                                  extends SpecialRegister("SR_TID.Z")
  case SR_CTAID_X                                extends SpecialRegister("SR_CTAID.X")
  case SR_CTAID_Y                                extends SpecialRegister("SR_CTAID.Y")
  case SR_CTAID_Z                                extends SpecialRegister("SR_CTAID.Z")
  case SR_NTID_X                                 extends SpecialRegister("SR_NTID.X")
  case SR_NTID_Y                                 extends SpecialRegister("SR_NTID.Y")
  case SR_NTID_Z                                 extends SpecialRegister("SR_NTID.Z")
  case UR_TID_X                                  extends SpecialRegister("UR_TID.X")
  case UR_TID_Y                                  extends SpecialRegister("UR_TID.Y")
  case UR_TID_Z                                  extends SpecialRegister("UR_TID.Z")
  case UR_CTAID_X                                extends SpecialRegister("UR_CTAID.X")
  case UR_CTAID_Y                                extends SpecialRegister("UR_CTAID.Y")
  case UR_CTAID_Z                                extends SpecialRegister("UR_CTAID.Z")
  case SR_LANEID                                 extends SpecialRegister("SR_LANEID")
  case SR_WARPID                                 extends SpecialRegister("SR_WARPID")
  case SR_CLOCKLO                                extends SpecialRegister("SR_CLOCKLO")
  case SR_CLOCKHI                                extends SpecialRegister("SR_CLOCKHI")
  case SR_PM0                                    extends SpecialRegister("SR_PM0")
  case SR_SMID                                   extends SpecialRegister("SR_SMID")
  case SR_GRIDID                                 extends SpecialRegister("SR_GRIDID")
  case SR_GLOBALTIMERLO                          extends SpecialRegister("SR_GLOBALTIMERLO")
  case SR_GLOBALTIMERHI                          extends SpecialRegister("SR_GLOBALTIMERHI")
  case SR_GEMASK                                 extends SpecialRegister("SR_GEMASK")
  case SR_GTMASK                                 extends SpecialRegister("SR_GTMASK")
  case SR_LEMASK                                 extends SpecialRegister("SR_LEMASK")
  case SR_LTMASK                                 extends SpecialRegister("SR_LTMASK")
  case SR_EQMASK                                 extends SpecialRegister("SR_EQMASK")
  case PR                                        extends SpecialRegister("PR")
  case desc                                      extends SpecialRegister("desc")
  case Unknown(vendorSpelling: NonEmpty[String]) extends SpecialRegister(vendorSpelling)
}
