package net.kurobako.gemstone.texture

object ObjectKind {
  def fromCode(code: Int): Option[ObjectKind] = values.find(_.code == code)
}

enum ObjectKind(val code: Int) derives CanEqual {
  case Texture extends ObjectKind(TextureLayout.Object.Texture)
  case Surface extends ObjectKind(TextureLayout.Object.Surface)
}
