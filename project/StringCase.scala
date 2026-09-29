import java.util.Locale

/** Locale-independent casing for build utilities, independent of application sources. */
object StringCase {
  extension (value: String) {
    def toUpperCaseRoot: String = value.toUpperCase(Locale.ROOT)
    def toLowerCaseRoot: String = value.toLowerCase(Locale.ROOT)
  }
}
