package net.kurobako.gemstone.server.recording

final class JournalPrefixSuite extends munit.FunSuite {
  test("bounded prefixes validate full declared frame lengths without retaining payloads") {
    val maximum = RPCJournal.Limits().maxFrameBytes
    val frame   = RPCJournal.Frames.normalized(
      RPCJournal.Kind.Request,
      JournalFixtures.execution(3, Array.fill[Byte](256 * 1024)(17)),
      maximum
    )
    val prefix = frame.take(184)
    RPCJournal.Frames.validatePrefix(RPCJournal.Kind.Request, prefix, frame.length, maximum, normalized = true)
    val _ = intercept[java.io.IOException] {
      RPCJournal.Frames.validatePrefix(RPCJournal.Kind.Request, prefix, frame.length + 1, maximum, normalized = true)
    }
    val _ = intercept[java.io.IOException] {
      RPCJournal.Frames.validatePrefix(RPCJournal.Kind.Response, prefix, frame.length, maximum, normalized = true)
    }
    val _ = intercept[java.io.IOException] {
      RPCJournal.Frames.validatePrefix(
        RPCJournal.Kind.Request,
        prefix.take(8),
        frame.length,
        maximum,
        normalized = true
      )
    }
    prefix(8) = 1
    intercept[java.io.IOException] {
      RPCJournal.Frames.validatePrefix(RPCJournal.Kind.Request, prefix, frame.length, maximum, normalized = true)
    }
  }
}
