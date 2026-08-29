package net.kurobako.gemstone.isa

import net.kurobako.gemstone.Col

/** Result of checking an ISA value against a selected target or environment.
  *
  * `Invalid` means the value violates a fully understood rule. `Unsupported` means validation cannot yet make a
  * complete claim for the value. Keeping these outcomes distinct prevents incomplete validators from accepting input.
  */
enum ValidationResult[+Issue] {
  case Valid
  case Invalid(issues: Col[Issue])
  case Unsupported(issues: Col[Issue])
}
