package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.execution.ExecutionLimits
import net.kurobako.gemstone.server.GemstoneRPCServer
import net.kurobako.gemstone.server.execution.ExecutionConfiguration

import cats.syntax.either.*

import scala.util.Using

private[gemstone] object GemstoneProcess {
  def run[A](
      apis: Set[ComputeAPI],
      layout: RuntimeLayout,
      inherited: Map[String, String]
  )(child: Environment => A): Either[LaunchError, A] = configuredLimits(inherited).flatMap { limits =>
    Using.resource(GemstoneRPCServer.start(limits, inherited)) { server =>
      Launch.plan(apis, layout, Endpoint(server.port, server.client), inherited).map(plan => child(plan.environment))
    }
  }

  private def configuredLimits(inherited: Map[String, String]): Either[LaunchError, ExecutionLimits] =
    ExecutionConfiguration.limits(inherited)
      .leftMap(message => LaunchError(s"invalid execution profile: $message"))
}
