package net.kurobako.gemstone.texture

object AddressMode {
  def fromCode(code: Int): Option[AddressMode] = values.find(_.code == code)
}

enum AddressMode(val code: Int) derives CanEqual {
  case Wrap   extends AddressMode(TextureLayout.Address.Wrap)
  case Clamp  extends AddressMode(TextureLayout.Address.Clamp)
  case Mirror extends AddressMode(TextureLayout.Address.Mirror)
  case Border extends AddressMode(TextureLayout.Address.Border)
}
