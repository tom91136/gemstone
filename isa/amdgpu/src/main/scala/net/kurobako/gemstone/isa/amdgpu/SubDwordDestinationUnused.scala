package net.kurobako.gemstone.isa.amdgpu

object SubDwordDestinationUnused {
  def fromEncoding(encoding: Int): Option[SubDwordDestinationUnused] = values.find(_.encoding == encoding)
  def fromName(name: String): Option[SubDwordDestinationUnused]      = values.find(_.assemblyName == name)
}

enum SubDwordDestinationUnused(val encoding: Int) {
  case Pad        extends SubDwordDestinationUnused(EncodingConstants.SDWAUnused.UNUSED_PAD)
  case SignExtend extends SubDwordDestinationUnused(EncodingConstants.SDWAUnused.UNUSED_SEXT)
  case Preserve   extends SubDwordDestinationUnused(EncodingConstants.SDWAUnused.UNUSED_PRESERVE)

  def assemblyName: String = EncodingConstants.SdwaUnused(encoding)
}
