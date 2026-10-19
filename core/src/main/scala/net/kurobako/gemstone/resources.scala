package net.kurobako.gemstone

import upickle.default.Reader
import upickle.default.read

object resources {

  /** Generated JSON packaged next to `owner`; a missing resource is a build defect, not an input error. */
  def json[A: Reader](owner: Class[?], name: String): A = PlatformResources.read(owner, name)(read[A](_))
}
