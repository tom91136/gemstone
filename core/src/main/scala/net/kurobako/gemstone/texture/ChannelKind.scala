package net.kurobako.gemstone.texture

object ChannelKind {
  def fromCode(code: Int): Option[ChannelKind] = values.find(_.code == code)
}

enum ChannelKind(val code: Int) derives CanEqual {
  case Signed             extends ChannelKind(TextureLayout.Channel.Signed)
  case Unsigned           extends ChannelKind(TextureLayout.Channel.Unsigned)
  case Float              extends ChannelKind(TextureLayout.Channel.Float)
  case UnsignedNormalised extends ChannelKind(TextureLayout.Channel.UnsignedNormalised)
  case SignedNormalised   extends ChannelKind(TextureLayout.Channel.SignedNormalised)

  def isInteger: Boolean    = this == Signed || this == Unsigned
  def isNormalised: Boolean = this == UnsignedNormalised || this == SignedNormalised
}
