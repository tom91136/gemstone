package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.Col

import java.nio.file.Files
import java.nio.file.Path
import scala.annotation.tailrec

object GemstoneLauncher {
  private final case class Options(api: ComputeAPI, layout: RuntimeLayout, command: Col[String])

  def main(arguments: Array[String]): Unit = {
    val exit = parse(arguments).fold(
      error => {
        System.err.println(s"gemstone: ${error.message}")
        2
      },
      run
    )
    if (exit != 0) System.exit(exit)
  }

  private def run(options: Options): Int = {
    val missing = providerPaths(options.layout).filterNot(Files.exists(_))
    if (missing.nonEmpty) {
      System.err.println(s"gemstone: runtime provider does not exist: ${missing.head}")
      2
    } else
      GemstoneProcess.attested(options.api, options.layout, sys.env) { environment =>
        val builder = ProcessBuilder(options.command*).inheritIO()
        environment.applyTo(builder.environment())
        builder.start().waitFor()
      }.fold(
        error => {
          System.err.println(s"gemstone: ${error.message}")
          86
        },
        identity
      )
  }

  private def parse(arguments: Array[String]): Either[LaunchError, Options] = {
    @tailrec
    def loop(
        index: Int,
        api: Option[ComputeAPI],
        layout: RuntimeLayout
    ): Either[LaunchError, Options] =
      if (index >= arguments.length)
        Left(LaunchError("usage: gemstone run --api <api> [provider option] -- <command> [arguments...]"))
      else
        arguments(index) match {
          case "run"                                => loop(index + 1, api, layout)
          case "--" if index + 1 < arguments.length =>
            api.toRight(LaunchError("--api is required")).map(Options(_, layout, Col.from(arguments.drop(index + 1))))
          case option if index + 1 >= arguments.length =>
            Left(LaunchError(s"incomplete launcher option: $option"))
          case "--api" =>
            ComputeAPI.parse(arguments(index + 1)) match {
              case Left(error)  => Left(error)
              case Right(value) => loop(index + 2, Some(value), layout)
            }
          case "--cuda-driver" =>
            loop(index + 2, api, layout.copy(cudaDriver = Some(Path.of(arguments(index + 1)))))
          case "--hip-runtime" =>
            loop(index + 2, api, layout.copy(hipRuntime = Some(Path.of(arguments(index + 1)))))
          case "--hsa-runtime" =>
            loop(index + 2, api, layout.copy(hsaRuntime = Some(Path.of(arguments(index + 1)))))
          case "--opencl-icd" =>
            loop(index + 2, api, layout.copy(openCLICD = Some(Path.of(arguments(index + 1)))))
          case "--levelzero-driver" =>
            loop(index + 2, api, layout.copy(levelZeroDriver = Some(Path.of(arguments(index + 1)))))
          case option => Left(LaunchError(s"unknown launcher option: $option"))
        }

    loop(0, None, fromEnvironment(sys.env))
  }

  private def fromEnvironment(environment: Map[String, String]): RuntimeLayout = RuntimeLayout(
    cudaDriver = environment.get("GEMSTONE_CUDA_DRIVER").filter(_.nonEmpty).map(Path.of(_)),
    hipRuntime = environment.get("GEMSTONE_HIP_RUNTIME").filter(_.nonEmpty).map(Path.of(_)),
    hsaRuntime = environment.get("GEMSTONE_HSA_RUNTIME").filter(_.nonEmpty).map(Path.of(_)),
    openCLICD = environment.get("GEMSTONE_OPENCL_ICD").filter(_.nonEmpty).map(Path.of(_)),
    levelZeroDriver = environment.get("GEMSTONE_LEVELZERO_DRIVER").filter(_.nonEmpty).map(Path.of(_))
  )

  private def providerPaths(layout: RuntimeLayout): Col[Path] =
    Col(layout.cudaDriver, layout.hipRuntime, layout.hsaRuntime, layout.openCLICD, layout.levelZeroDriver).flatten
}
