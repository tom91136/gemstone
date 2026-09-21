package net.kurobako.gemstone.server

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation

import scala.util.Using

final class ExecutionRPCIntegrationSuite extends munit.FunSuite {
  test("a server with the default backends rejects an unregistered program encoding") {
    Using.Manager { use =>
      val server = use(GemstoneRPCServer.start())
      val client = use(ExecutionRPCTestClient(server.port, server.client))
      assert(client.call(Operation.LoadProgram("not-registered", Vec.empty)).isLeft)
    }.get
  }
}
