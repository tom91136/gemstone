package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.server.transport.{ClientId, RPCPort}

private[gemstone] final case class Endpoint(port: RPCPort, client: ClientId)
