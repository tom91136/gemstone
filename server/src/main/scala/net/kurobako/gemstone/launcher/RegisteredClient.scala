package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.server.transport.{RPCPort, ServerControl}

import java.util.concurrent.atomic.AtomicBoolean

private[gemstone] final class RegisteredClient private (
    val endpoint: Endpoint
) extends AutoCloseable {
  private val closed = AtomicBoolean(false)

  override def close(): Unit =
    if (closed.compareAndSet(false, true))
      ServerControl.release(endpoint.port, endpoint.client)
}

private[gemstone] object RegisteredClient {
  def register(port: RPCPort): RegisteredClient =
    RegisteredClient(Endpoint(port, ServerControl.register(port)))
}
