package net.kurobako.gemstone.server.transport

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[gemstone] type RPCPort = RPCPort.T
private[gemstone] object RPCPort extends RefinedType[Int, Interval.Closed[1, 65535]]

private[gemstone] type ClientId = ClientId.T
private[gemstone] object ClientId extends RefinedType[Long, Positive]
