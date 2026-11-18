package net.kurobako.gemstone.isa.check

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*

object RewriteSearchRunner {

  private val usage =
    "usage: RewriteSearchRunner <code-object> <kernel> <target> [--seed n] [--budget n] [--temperature t] [--window n]"

  def main(arguments: Array[String]): Unit = {
    if (arguments.length < 3) throw IllegalArgumentException(usage)
    val path    = arguments(0)
    val kernel  = arguments(1)
    val target  = arguments(2)
    val options = arguments.drop(3).grouped(2).map {
      case Array(flag @ ("--seed" | "--budget" | "--temperature" | "--window"), value) => flag -> value
      case _ => throw IllegalArgumentException(usage)
    }.toMap
    val defaults = RewriteSearch.Config.default
    val config   = RewriteSearch.Config(
      seed = options.get("--seed").fold(defaults.seed)(_.toLong),
      budget = options.get("--budget").fold(defaults.budget)(_.toInt),
      temperature = options.get("--temperature").fold(defaults.temperature)(_.toDouble),
      states = defaults.states,
      limit = defaults.limit
    )
    val file    = Path.of(path)
    val image   = Vec.unsafeWrapArray(Files.readAllBytes(file))
    val label   = file.getFileName.toString.takeWhile(_ != '.')
    val windows = options.get("--window").fold(AMDGPUWindows.of(image, kernel, target))(size =>
      AMDGPUWindows.of(image, kernel, target, size.toInt)
    )
    windows match {
      case Left(reason) => throw IllegalArgumentException(reason)
      case Right(found) =>
        found.foreach { window =>
          val result = RewriteSearch.amdgpu(image, kernel, target, window, config)
          RewriteSearch.lines(label, window, result).foreach(println)
        }
    }
  }
}
