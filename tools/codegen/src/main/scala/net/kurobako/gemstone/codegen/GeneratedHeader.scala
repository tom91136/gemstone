package net.kurobako.gemstone.codegen

/** Single source of the banner and annotation emitted by every generator. */
object GeneratedHeader {

  /** Repository command that regenerates every checked-in generated artifact. */
  val Regenerate: String = "sbt codegen"

  def scala(subject: String): String = line("//", subject)

  def cpp(subject: String): String = s"/* Generated $subject. Regenerate with: $Regenerate */\n"

  def line(prefix: String, subject: String): String =
    s"$prefix Generated $subject. Regenerate with: $Regenerate\n"

  /** Standard `javax.annotation.processing.Generated` marker (`java.compiler`, SOURCE retention). */
  def annotation(): String =
    s"""@Generated(Array("$Regenerate"))\n"""
}
