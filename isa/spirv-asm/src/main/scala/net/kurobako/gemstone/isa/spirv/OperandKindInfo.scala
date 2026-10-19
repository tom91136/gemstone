package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*

object OperandKindInfo {

  /** Reads a kind name followed by its enumerants, separated by tabs. */
  def parse(row: String): OperandKindInfo = {
    val fields = row.split('\t')
    OperandKindInfo(OperandKind.valueOf(fields(0)), Vec.from(fields.view.drop(1).map(OperandEnumerant.parse(_))))
  }
}

case class OperandKindInfo(kind: OperandKind, enumerants: Vec[OperandEnumerant])
