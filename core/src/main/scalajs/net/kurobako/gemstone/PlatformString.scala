package net.kurobako.gemstone

/** Scala.js formats without locales and converts case with Unicode rules. */
private object PlatformString {
  def format(pattern: String, arguments: AnyRef*): String = String.format(pattern, arguments*)

  @SuppressWarnings(Array("scalafix:ForbiddenAPIs"))
  def upper(value: String): String = value.toUpperCase()

  @SuppressWarnings(Array("scalafix:ForbiddenAPIs"))
  def lower(value: String): String = value.toLowerCase()
}
