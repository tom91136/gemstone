package net.kurobako.gemstone.texture

object FilterMode {
  def fromCode(code: Int): Option[FilterMode] = values.find(_.code == code)
}

enum FilterMode(val code: Int) derives CanEqual {
  case Point  extends FilterMode(TextureLayout.Filter.Point)
  case Linear extends FilterMode(TextureLayout.Filter.Linear)
}
