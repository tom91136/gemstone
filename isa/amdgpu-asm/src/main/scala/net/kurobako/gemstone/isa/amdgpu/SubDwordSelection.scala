package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*

object SubDwordSelection {
  def fromEncoding(encoding: Int): Option[SubDwordSelection] = values.find(_.encoding == encoding)
  def fromName(name: String): Option[SubDwordSelection]      = values.find(_.assemblyName == name)
}

enum SubDwordSelection(
    val encoding: Int,
    val width: Int in 1 ~ 64,
    val shift: Int in 0 ~ 24,
    val upperBit: Int in 1 ~ 64
) {
  case Byte0 extends SubDwordSelection(EncodingConstants.SDWA.BYTE_0, 8, 0, 8)
  case Byte1 extends SubDwordSelection(EncodingConstants.SDWA.BYTE_1, 8, 8, 16)
  case Byte2 extends SubDwordSelection(EncodingConstants.SDWA.BYTE_2, 8, 16, 24)
  case Byte3 extends SubDwordSelection(EncodingConstants.SDWA.BYTE_3, 8, 24, 32)
  case Word0 extends SubDwordSelection(EncodingConstants.SDWA.WORD_0, 16, 0, 16)
  case Word1 extends SubDwordSelection(EncodingConstants.SDWA.WORD_1, 16, 16, 32)
  case DWord extends SubDwordSelection(EncodingConstants.SDWA.DWORD, 32, 0, 32)
  def assemblyName: NonEmpty[String] = EncodingConstants.SdwaSelectors(encoding)
}
