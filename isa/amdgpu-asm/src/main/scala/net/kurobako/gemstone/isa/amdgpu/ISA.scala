package net.kurobako.gemstone.isa.amdgpu

object ISA {
  given upickle.default.ReadWriter[ISA] = upickle.default.readwriter[String].bimap(_.toString, ISA.valueOf(_))
}

/** Encoding catalogues represented by the pinned AMD XML and Mesa ACO inputs. Catalogue availability does not imply
  * complete processor validation or execution support.
  */
enum ISA derives CanEqual {
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
  def isPreGFX9: Boolean = this match {
    case GFX6 | GFX7 | GFX8 => true
    case _                  => false
  }
  def isGFX6To9: Boolean = this match {
    case GFX6 | GFX7 | GFX8 | GFX9 => true
    case _                         => false
  }
  def isGFX9OrEarlier: Boolean = this match {
    case CDNA1 | CDNA2 | CDNA3 | CDNA4 => true
    case _                             => isGFX6To9
  }
  def isGFX10: Boolean = this match {
    case RDNA1 | RDNA2 => true
    case _             => false
  }
  def isGFX11OrLaterGraphics: Boolean = this match {
    case RDNA3 | RDNA3_5 | RDNA4 | GFX11_7 => true
    case _                                 => false
  }
  def isGFX11OrLater: Boolean = isGFX11OrLaterGraphics || this == CDNA5
}
