package net.kurobako.gemstone

/** Scala Native uses root-locale formatting and Unicode case conversion without a default locale. */
private object PlatformString {
  def format(pattern: String, arguments: AnyRef*): String = String.format(pattern, arguments*)

  @SuppressWarnings(Array("scalafix:GemstoneSafeStringCase"))
  def upper(value: String): String = value.toUpperCase()

  @SuppressWarnings(Array("scalafix:GemstoneSafeStringCase"))
  def lower(value: String): String = value.toLowerCase()
}
