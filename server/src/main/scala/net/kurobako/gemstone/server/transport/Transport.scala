package net.kurobako.gemstone.server.transport

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

object Transport {
  type ClientId = ClientId.T
  object ClientId extends RefinedType[Long, Positive]

  type RPCPort = RPCPort.T
  object RPCPort extends RefinedType[Int, Interval.Closed[1, 65535]]
}
