package net.kurobako.gemstone.isa.check

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.profile.PipelineSimulator

object RewriteSearchRunner {

  private val usage =
    "usage: RewriteSearchRunner <code-object> <kernel> <target> [--seed n] [--budget n] [--temperature t] " +
      "[--window n] [--lanes n] [--shared bytes] [--placement n,n,...] [--record groups [--arguments arg,arg,...]]"

  private val flags = Set(
    "--seed",
    "--budget",
    "--temperature",
    "--window",
    "--lanes",
    "--shared",
    "--placement",
    "--record",
    "--arguments"
  )

  /** The launch the flags state. `--record` has the VM launch that many workgroups of `--lanes` lanes of the kernel and
    * costs from what it records, with one [[LaunchArgument]] of `--arguments` for each kernel parameter.
    */
  private def launch(
      image: Vec[Byte],
      kernel: String,
      target: String,
      options: Map[String, String]
  ): Either[String, AMDGPUBlockCost.Launch] = {
    val placement =
      options.get("--placement").map(text => PipelineSimulator.Placement(Vec.from(text.split(',').map(_.toInt))))
    for {
      arguments <- options.get("--arguments").fold(Right(None))(LaunchArgument.parse(_).map(Some(_)))
      fallback  <- if (sass(target)) Right(SASSLanes)
      else target.refine[Target].left.map(_.raw).map(TargetDefaults.waveSize(_).raw)
      lanes  <- options.get("--lanes").fold(fallback)(_.toInt).refine[Int > 0].left.map(_.raw)
      stated <- options.get("--record") match {
        case None =>
          for {
            _      <- Either.cond(arguments.isEmpty, (), "--arguments needs --record")
            shared <- options.get("--shared").fold(0L)(_.toLong).refine[Long >= 0].left.map(_.raw)
          } yield AMDGPUBlockCost.Launch.Stated(lanes, shared, placement)
        case Some(groups) =>
          for {
            _      <- Either.cond(!options.contains("--shared"), (), "--record takes the shared bytes the launch has")
            count  <- groups.toInt.refine[Int > 0].left.map(_.raw)
            stream <- LaunchRecording.record(
              if (sass(target)) Language.SASS else Language.AMDGPU,
              image,
              kernel,
              lanes,
              count,
              arguments.getOrElse(Vec.empty)
            )
          } yield AMDGPUBlockCost.Launch.Recorded(stream, placement)
      }
    } yield stated
  }

  private val SASSLanes = 32

  private def sass(target: String): Boolean = target.startsWith("sm_")

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
    val file    = Path.of(path)
    val image   = Vec.unsafeWrapArray(Files.readAllBytes(file))
    val label   = file.getFileName.toString.takeWhile(_ != '.')
    val windows =
      if (sass(target)) options.get("--window").fold(SASSWindows.of(image, kernel, target))(size =>
        SASSWindows.of(image, kernel, target, size.toInt)
      )
      else options.get("--window").fold(AMDGPUWindows.of(image, kernel, target))(size =>
        AMDGPUWindows.of(image, kernel, target, size.toInt)
      )
    launch(image, kernel, target, options).flatMap(stated => windows.map(_ -> stated)) match {
      case Left(reason)           => throw IllegalArgumentException(reason)
      case Right((found, stated)) =>
        found.foreach { window =>
          val result =
            if (sass(target)) RewriteSearch.sass(image, kernel, target, window, stated, config)
            else RewriteSearch.amdgpu(image, kernel, target, window, stated, config)
          RewriteSearch.lines(label, window, result).foreach(println)
        }
    }
  }
}
