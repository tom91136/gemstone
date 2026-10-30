package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.pickler.given
import net.kurobako.gemstone.testing.Suite
import scala.util.Try
import upickle.default.ReadWriter
import upickle.default.read
import upickle.default.write

class PicklerSuite extends Suite {
  private case class Named(name: NonEmpty[String]) derives ReadWriter
  private case class Field(offset: Int >= 0, bits: Int in 1 ~ 64, scale: Double in 0.0 ~ 1.0) derives ReadWriter
  private case class PlainField(offset: Int, bits: Int, scale: Double) derives ReadWriter

  test("refined strings retain their JSON string representation") {
    val name: NonEmpty[String] = "kernel"
    assertEquals(write(name), "\"kernel\"")
    assertEquals(read[NonEmpty[String]](write(name)), name)
    assertEquals(read[Named](write(Named(name))), Named(name))
    val embeddedNul: NonEmpty[String] = "a\u0000b"
    assertEquals(read[NonEmpty[String]](write(embeddedNul)), embeddedNul)
  }

  test("direct and derived readers reject invalid refined strings") {
    assert(Try(read[NonEmpty[String]](write(""))).isFailure)
    assert(Try(read[Named](ujson.Obj("name" -> ""))).isFailure)
  }

  test("refined numbers keep their carrier's JSON, and readers reject values outside the refinement") {
    val field = Field(offset = 3, bits = 64, scale = 0.5)
    assertEquals(write(field), write(PlainField(offset = 3, bits = 64, scale = 0.5)))
    assertEquals(read[Field](write(field)), field)
    assertEquals(read[Int in 1 ~ 64]("64").raw, 64)
    val failure  = Try(read[Int in 1 ~ 64]("65")).failed.get
    val messages = Iterator.unfold(Option(failure))(_.map(cause => (cause.getMessage, Option(cause.getCause))))
    assert(messages.exists(message => message.contains("Membership") && message.endsWith(": 65")))
    assert(Try(read[Field](write(PlainField(offset = -1, bits = 64, scale = 0.5)))).isFailure)
    assert(Try(read[Field](write(PlainField(offset = 3, bits = 64, scale = 1.5)))).isFailure)
  }
}
