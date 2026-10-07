package net.kurobako.gemstone

/** JVM text operations explicitly select the root locale. */
private object PlatformString {
  def format(pattern: String, arguments: AnyRef*): String = String.format(java.util.Locale.ROOT, pattern, arguments*)

  @SuppressWarnings(Array("scalafix:ForbiddenAPIs"))
  def upper(value: String): String = value.toUpperCase(java.util.Locale.ROOT)

  @SuppressWarnings(Array("scalafix:ForbiddenAPIs"))
  def lower(value: String): String = value.toLowerCase(java.util.Locale.ROOT)
}
