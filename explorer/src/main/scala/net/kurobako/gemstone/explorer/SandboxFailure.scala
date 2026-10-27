package net.kurobako.gemstone.explorer

/** Why a sandbox program did not run. These are setup failures, not a reference interpretation of the program: the
  * explorer shows the Program page or the language's sample instead of emulating anything.
  */
enum SandboxFailure derives CanEqual {

  def language: Language
  def detail: String

  /** This slice has no synthesised program for the language or target. */
  case Unavailable(override val language: Language, override val detail: String)

  /** The instruction sequence or the module around it did not assemble. */
  case Assembly(override val language: Language, override val detail: String)

  /** The assembled code could not replace the template's kernel. */
  case Patch(override val language: Language, override val detail: String)

  /** The device backend did not load the patched image. */
  case Load(override val language: Language, override val detail: String)

  /** The launch task's arguments or memory did not match the kernel. */
  case Setup(override val language: Language, override val detail: String)

  /** The kernel was loaded but the VM rejected the launch itself. */
  case Launch(override val language: Language, override val detail: String)

  /** The Program page runs this language's sample on the real VM when synthesis is unavailable. */
  def programPage: String = s"Program page (${language.title})"
}
