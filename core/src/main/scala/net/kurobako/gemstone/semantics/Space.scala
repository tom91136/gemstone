package net.kurobako.gemstone.semantics

/** The address spaces a machine operation reaches, named as the machine model rather than as any one ISA. */
enum Space derives CanEqual {

  /** Memory every workgroup of the launch sees. */
  case Global

  /** Memory the lanes of one workgroup share: CTA shared memory, LDS or workgroup storage. */
  case Shared

  /** Memory private to one lane. */
  case Local

  /** Read-only memory bound at launch. */
  case Constant

  /** Memory addressed without naming a space, which the address itself selects. */
  case Generic
}
