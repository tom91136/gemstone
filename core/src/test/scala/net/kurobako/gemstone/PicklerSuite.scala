package net.kurobako.gemstone

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.pickler.given
import scala.util.Try
import upickle.default.ReadWriter
import upickle.default.read
import upickle.default.write

class PicklerSuite extends munit.FunSuite {
  private case class Named(name: NonEmpty[String]) derives ReadWriter

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
}
