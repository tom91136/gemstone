/*
rule = GemstoneInterpolationBraces
 */
// format: off
package fix

object InterpolationBraces {
  def checks(other: String, next: String, n: Int, suffix_ : String, `type`: String): Unit = {
    val a = s"expected register, found ${other}"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val b = s"${other}!"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val c = s"${other}.field"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val d = s"${other}/path"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val e = s"${other}$next"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val f = s"${other}$$"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val g = s"""line ${other}!"""// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val h = raw"${other}\n"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val i = f"${n}%04d"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val j = s"${ other }"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val k = s"${other}\n"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
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
    val operatorSuffix = s"${suffix_}+"// assert: GemstoneInterpolationBraces.redundant-interpolation-braces
    val statements = s"${println(other); next}"
  }

  @SuppressWarnings(Array("scalafix:GemstoneInterpolationBraces"))
  def suppressed(other: String): String = s"${other}"
}
