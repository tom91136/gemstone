package net.kurobako.gemstone.isa.check

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.profile.PipelineSimulator

object RewriteSearchRunner {

  private val usage =
    "usage: RewriteSearchRunner <code-object> <kernel> <target> [--seed n] [--budget n] [--temperature t] " +
      "[--window n] [--lanes n] [--shared bytes] [--placement n,n,...]"

  private val flags = Set("--seed", "--budget", "--temperature", "--window", "--lanes", "--shared", "--placement")

  def main(arguments: Array[String]): Unit = {
    if (arguments.length < 3) throw IllegalArgumentException(usage)
    val path    = arguments(0)
    val kernel  = arguments(1)
    val target  = arguments(2)
    val options = arguments.drop(3).grouped(2).map {
      case Array(flag, value) if flags(flag) => flag -> value
      case _                                 => throw IllegalArgumentException(usage)
    }.toMap
    val defaults = RewriteSearch.Config.default
    val config   = RewriteSearch.Config(
      seed = options.get("--seed").fold(defaults.seed)(_.toLong),
      budget = options.get("--budget").fold(defaults.budget)(_.toInt),
      temperature = options.get("--temperature").fold(defaults.temperature)(_.toDouble),
      states = defaults.states,
      limit = defaults.limit
    )
    val launch = for {
      named  <- target.refine[Target].left.map(_.raw)
      lanes  <- options.get("--lanes").fold(TargetDefaults.waveSize(named).raw)(_.toInt).refine[Int > 0].left.map(_.raw)
      shared <- options.get("--shared").fold(0L)(_.toLong).refine[Long >= 0].left.map(_.raw)
    } yield AMDGPUBlockCost.Launch(
      lanes,
      shared,
      options.get("--placement").map(text => PipelineSimulator.Placement(Vec.from(text.split(',').map(_.toInt))))
    )
    val file    = Path.of(path)
    val image   = Vec.unsafeWrapArray(Files.readAllBytes(file))
    val label   = file.getFileName.toString.takeWhile(_ != '.')
    val windows = options.get("--window").fold(AMDGPUWindows.of(image, kernel, target))(size =>
      AMDGPUWindows.of(image, kernel, target, size.toInt)
    )
    launch.flatMap(stated => windows.map(_ -> stated)) match {
      case Left(reason)           => throw IllegalArgumentException(reason)
      case Right((found, stated)) =>
        found.foreach { window =>
          val result = RewriteSearch.amdgpu(image, kernel, target, window, stated, config)
          RewriteSearch.lines(label, window, result).foreach(println)
        }
    }
  }
}
