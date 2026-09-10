package net.kurobako.gemstone.server

class JournalPrefixSuite extends munit.FunSuite {
  test("bounded prefixes validate full declared frame lengths without retaining payloads") {
    val maximum = RPCJournal.Limits().maxFrameBytes
    val frame   = RPCFrames.normalized(
      RPCJournal.Kind.Request,
      JournalFixtures.execution(3, Array.fill[Byte](256 * 1024)(17)),
      maximum
    )
    val prefix = frame.take(184)
    RPCFrames.validatePrefix(RPCJournal.Kind.Request, prefix, frame.length, maximum, redacted = true)
    intercept[java.io.IOException] {
      RPCFrames.validatePrefix(RPCJournal.Kind.Request, prefix, frame.length + 1, maximum, redacted = true)
    }
    intercept[java.io.IOException] {
      RPCFrames.validatePrefix(RPCJournal.Kind.Response, prefix, frame.length, maximum, redacted = true)
    }
    intercept[java.io.IOException] {
      RPCFrames.validatePrefix(RPCJournal.Kind.Request, prefix.take(8), frame.length, maximum, redacted = true)
    }
    prefix(8) = 1
    intercept[java.io.IOException] {
      RPCFrames.validatePrefix(RPCJournal.Kind.Request, prefix, frame.length, maximum, redacted = true)
    }
  }
}
