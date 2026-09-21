package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.server.GemstoneRPCServer
import net.kurobako.gemstone.server.execution.ExecutionConfiguration
import net.kurobako.gemstone.server.transport.RPCPort
import net.kurobako.gemstone.vm.TaskLimits

import cats.syntax.either.*

import scala.util.Using

private[gemstone] object GemstoneProcess {
  def run[A](
      apis: Set[ComputeAPI],
      layout: RuntimeLayout,
      inherited: Map[String, String],
      serverPort: Option[RPCPort] = None
  )(child: Environment => A): Either[LaunchError, A] = serverPort match {
    case Some(port) =>
      Using.resource(RegisteredClient.register(port)) { client =>
        launch(apis, layout, inherited, client.endpoint, child)
      }
    case None =>
      configuredLimits(inherited).flatMap { limits =>
        Using.resource(GemstoneRPCServer.start(limits, inherited)) { running =>
          launch(apis, layout, inherited, Endpoint(running.port, running.client), child)
        }
      }
  }

  private def launch[A](
      apis: Set[ComputeAPI],
      layout: RuntimeLayout,
      inherited: Map[String, String],
      endpoint: Endpoint,
      child: Environment => A
  ): Either[LaunchError, A] =
    Launch.plan(apis, layout, endpoint, inherited).map(plan => child(plan.environment))

  private def configuredLimits(inherited: Map[String, String]): Either[LaunchError, TaskLimits] =
    ExecutionConfiguration.limits(inherited)
      .leftMap(message => LaunchError(s"invalid execution profile: $message"))
}
