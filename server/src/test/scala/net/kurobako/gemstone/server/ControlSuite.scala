package net.kurobako.gemstone.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.file.Path
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.testing.*
import scala.util.Using
import scala.util.control.NonFatal

class ControlSuite extends Suite {

  private def withControlServer[A](replyDelayMillis: Int => Option[Long])(body: Endpoint => A): A =
    Using.resource(Sockets.listen(Bind.Tcp())) { listener =>
      val thread = Thread(
        () =>
          try
            while (!listener.isClosed)
              Using.resource(listener.accept()) { channel =>
                val input     = DataInputStream(channel.input)
                val output    = DataOutputStream(channel.output)
                val magic     = input.readInt()
                val operation = input.readInt()
                val size      = input.readInt()
                input.readFully(Array.ofDim[Byte](size))
                require(magic == Control.Magic)
                replyDelayMillis(operation).foreach { delayMillis =>
                  Thread.sleep(delayMillis)
                  Control.writeResponse(output, operation, 3L)
                }
              }
          catch { case NonFatal(_) => () },
        "control-test-server"
      )
      thread.setDaemon(true)
      thread.start()
      body(listener.endpoint)
    }

  private val directory                = Path.of("/unused")
  private val apis: NonEmpty[Set[API]] = Set(API.HSA).unsafe

  test("a control exchange slower than five seconds still completes") {
    withControlServer(_ => Some(5500L)) { endpoint =>
      assertEquals(Control.register(endpoint).raw, 3L)
    }
  }

  test("the child's exit status survives a failed client release") {
    withControlServer(operation => Option.when(operation == Control.RegisterClient)(0L)) { endpoint =>
      assertEquals(Launch.launch(apis, directory, Map.empty, Some(endpoint))(_ => 7), Right(7))
    }
  }
}
