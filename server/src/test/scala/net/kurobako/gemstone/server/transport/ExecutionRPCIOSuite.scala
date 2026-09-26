package net.kurobako.gemstone.server.transport

import net.kurobako.gemstone.*

import net.kurobako.gemstone.memory.AllocationKind
import net.kurobako.gemstone.server.{ExecutionRPCTestClient, GemstoneRPCServer}
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation

import scala.util.Using

final class ExecutionRPCIOSuite extends munit.FunSuite {
  test("execution transport encodes and decodes the current ping operation") {
    Using.Manager { use =>
      val server = use(GemstoneRPCServer.start())
      val client = use(ExecutionRPCTestClient(server.port, server.client, process = 7L, incarnation = 9L))
      assert(client.call(Operation.RegisterAllocation(AllocationKind.Shared, PositiveLong(8L))).isRight)
    }.get
  }
}
