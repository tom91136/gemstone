package net.kurobako.gemstone.testing

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

/** Timeouts suit a 32-thread host; fewer threads stretch them up to fourfold, and the scale multiplies the result. */
object timeouts {
  def factor(scale: Double, threads: Long): Double = scale * math.min(4.0, math.max(1.0, 32.0 / threads))

  /** A positive decimal with at most three fractional digits, as the native shims and CMake accept. */
  def scale(value: String): Option[Double] =
    Option.when(value.matches("[0-9]+(\\.[0-9]{1,3})?"))(value.toDouble).filter(_ > 0)

  def threads(value: String): Option[Long] =
    Option.when(value.forall(c => c >= '0' && c <= '9'))(value).flatMap(_.toLongOption).filter(_ > 0)

  private def setting[A](name: String, parse: String => Option[A], expected: String, default: => A): A =
    host.variable(name).fold(default)(value =>
      parse(value).getOrElse(throw IllegalArgumentException(s"$name must be $expected, not '$value'"))
    )

  lazy val hostFactor: Double = factor(
    scale = setting(
      environmentKeys.TimeoutScale,
      scale,
      "a positive number with at most three decimals",
      environmentKeys.TimeoutScaleDefault.toDouble
    ),
    threads = setting(environmentKeys.ThreadCount, threads, "a positive integer", host.availableThreads)
  )

  /** Only for waits expected to succeed; a window that must elapse keeps its length. */
  def scaled(timeout: FiniteDuration): FiniteDuration =
    FiniteDuration(math.round(timeout.toMillis * hostFactor), TimeUnit.MILLISECONDS)
}
