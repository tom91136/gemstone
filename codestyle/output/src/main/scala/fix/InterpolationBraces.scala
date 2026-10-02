// format: off
package fix

object InterpolationBraces {
  def checks(other: String, next: String, n: Int, suffix_ : String, `type`: String): Unit = {
    val a = s"expected register, found ${other}"
    val b = s"${other}!"
    val c = s"${other}.field"
    val d = s"${other}/path"
    val e = s"${other}$next"
    val f = s"${other}$$"
    val g = s"""line ${other}!"""
    val h = raw"${other}\n"
    val i = f"${n}%04d"
    val j = s"${ other }"
    val k = s"${other}\n"
    val l = s"${other}text"
    val m = s"${other}0"
    val o = s"${other}_suffix"
    val p = s"${other}é"
    val q = s"${other}λ"
    val r = s"${other.length}"
    val t = s"${other.toString()}"
    val u = s"${if (n > 0) other else next}"
    val v = s"${other /* keep explanation */}"
    val w = s"${`type`}"
    val x = s"$other"
    val y = "${other}"
    val z = s"$${other}"
    val operatorSuffix = s"${suffix_}+"
    val statements = s"${println(other); next}"
  }

  @SuppressWarnings(Array("scalafix:GemstoneInterpolationBraces"))
  def suppressed(other: String): String = s"${other}"
}
