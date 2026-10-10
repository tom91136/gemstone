package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*

enum CodeObjectError {
  case Invalid(detail: NonEmpty[String])
  case Unsupported(detail: NonEmpty[String])
  def message: NonEmpty[String] = this match {
    case Invalid(detail)     => detail
    case Unsupported(detail) => detail
  }
}
