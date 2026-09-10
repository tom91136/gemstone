package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.server.GemstoneRPCServer
import net.kurobako.gemstone.execution.ExecutionLimits

import cats.syntax.either.*

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
    val server = GemstoneRPCServer.start(token, ExecutionLimits.Default, inherited)
    try Launch.plan(api, layout, Endpoint(server.port, token), inherited).map(plan => run(plan.environment))
    finally server.close()
  }

  def attested[A](
      api: ComputeAPI,
      layout: RuntimeLayout,
      inherited: Map[String, String]
  )(run: Environment => A): Either[LaunchError, A] =
    attestedWhen(api, layout, inherited, (_: A) => true)(run)

  private def attestedWhen[A](
      api: ComputeAPI,
      layout: RuntimeLayout,
      inherited: Map[String, String],
      requireEvidence: A => Boolean
  )(run: Environment => A): Either[LaunchError, A] =
    configuredLimits(inherited).flatMap { limits =>
      val token  = randomToken()
      val server = GemstoneRPCServer.start(token, limits, inherited)
      try
        Launch.plan(api, layout, Endpoint(server.port, token), inherited).flatMap { plan =>
          val before = server.usage
          val result = run(plan.environment)
          val after  = server.usage
          Either.cond(
            !requireEvidence(result) || plan.requiredEvidence.satisfied(before, after),
            result,
            LaunchError(
              s"${api.id} process completed without an authenticated Gemstone request; " +
                "runtime injection failed or a vendor GPU was used"
            )
          )
        }
      finally server.close()
    }

  private[launcher] def attestedExit(api: ComputeAPI, layout: RuntimeLayout, inherited: Map[String, String])(
      run: Environment => Int
  ): Either[LaunchError, Int] = attestedWhen(api, layout, inherited, (exit: Int) => exit == 0)(run)

  private def configuredLimits(inherited: Map[String, String]): Either[LaunchError, ExecutionLimits] =
    ExecutionLimits.fromEnvironment(inherited).leftMap(message => LaunchError(s"invalid execution profile: $message"))

  private def randomToken(): String = net.kurobako.gemstone.server.RPCAuthentication.token()
}
