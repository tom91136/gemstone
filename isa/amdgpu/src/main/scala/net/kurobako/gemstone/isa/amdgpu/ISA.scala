package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

/** Encoding catalogs represented by the pinned AMD XML and Mesa ACO inputs. Catalog availability does not imply
  * complete processor validation or execution support.
  */
enum ISA {
  case GFX6, GFX7, GFX8, GFX9
  case RDNA1, RDNA2, RDNA3, RDNA3_5, RDNA4
  case CDNA1, CDNA2, CDNA3, CDNA4, CDNA5

  lazy val instructions: Vec[Instruction.Definition] =
    Instruction.definitions.view.filter(_.meta.supports(this)).toVec
}
