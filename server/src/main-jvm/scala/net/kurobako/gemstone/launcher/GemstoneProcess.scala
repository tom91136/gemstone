package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.server.GemstoneRPCServer
import net.kurobako.gemstone.execution.ExecutionLimits

import cats.syntax.either.*

import java.security.SecureRandom
import java.util.HexFormat

object GemstoneProcess {

  /** Inject Gemstone into a child process without requiring a compute execution.
    *
    * This is for provider-linked host-only tests which exercise compile-time or host-side behavior but never submit
    * work. Compute tests must use [[attested]] instead.
    */
  def injected[A](
      api: ComputeAPI,
      layout: RuntimeLayout,
      inherited: Map[String, String]
  )(run: Environment => A): Either[LaunchError, A] = {
    val token  = randomToken()
    val server = GemstoneRPCServer.start(token, ExecutionLimits.Default, api.id)
    try Launch.plan(api, layout, Endpoint(server.port, token), inherited).map(plan => run(plan.environment))
    finally server.close()
  }

  def attested[A](
      api: ComputeAPI,
      layout: RuntimeLayout,
      inherited: Map[String, String]
  )(run: Environment => A): Either[LaunchError, A] =
    configuredLimits(inherited).flatMap { limits =>
      val token  = randomToken()
      val server = GemstoneRPCServer.start(token, limits, api.id)
      try
        Launch.plan(api, layout, Endpoint(server.port, token), inherited).flatMap { plan =>
          val before = server.usage
          val result = run(plan.environment)
          val after  = server.usage
          Either.cond(
            plan.requiredEvidence.satisfied(before, after),
            result,
            LaunchError(
              s"${api.id} process completed without an authenticated Gemstone request; " +
                "runtime injection failed or a vendor GPU was used"
            )
          )
        }
      finally server.close()
    }

  private def configuredLimits(inherited: Map[String, String]): Either[LaunchError, ExecutionLimits] =
    ExecutionLimits.fromEnvironment(inherited).leftMap(message => LaunchError(s"invalid execution profile: $message"))

  private def randomToken(): String = {
    val bytes = Array.ofDim[Byte](32)
    SecureRandom().nextBytes(bytes)
    HexFormat.of().formatHex(bytes)
  }
}
