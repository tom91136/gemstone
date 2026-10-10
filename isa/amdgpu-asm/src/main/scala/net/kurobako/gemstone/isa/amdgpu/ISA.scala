package net.kurobako.gemstone.isa.amdgpu

object ISA {
  given upickle.default.ReadWriter[ISA] = upickle.default.readwriter[String].bimap(_.toString, ISA.valueOf(_))
}

/** Encoding catalogues represented by the pinned AMD XML and Mesa ACO inputs. Catalogue availability does not imply
  * complete processor validation or execution support.
  */
enum ISA {
  case GFX6, GFX7, GFX8, GFX9, GFX11_7
  case RDNA1, RDNA2, RDNA3, RDNA3_5, RDNA4
  case CDNA1, CDNA2, CDNA3, CDNA4, CDNA5

  def isRDNA: Boolean = this match {
    case RDNA1 | RDNA2 | RDNA3 | RDNA3_5 | RDNA4 => true
    case _                                       => false
  }
  def isCDNA: Boolean = this match {
    case CDNA1 | CDNA2 | CDNA3 | CDNA4 | CDNA5 => true
    case _                                     => false
  }
}
