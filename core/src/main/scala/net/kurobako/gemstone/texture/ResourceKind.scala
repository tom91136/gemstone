package net.kurobako.gemstone.texture

object ResourceKind {
  def fromCode(code: Int): Option[ResourceKind] = values.find(_.code == code)
}

/** What backs the object. They differ in how the shim derives the descriptor, not in how it is sampled. */
enum ResourceKind(val code: Int) derives CanEqual {
  case Array          extends ResourceKind(TextureLayout.Resource.Array)
  case MipmappedArray extends ResourceKind(TextureLayout.Resource.MipmappedArray)
  case Linear         extends ResourceKind(TextureLayout.Resource.Linear)
  case Pitch2D        extends ResourceKind(TextureLayout.Resource.Pitch2D)
}
