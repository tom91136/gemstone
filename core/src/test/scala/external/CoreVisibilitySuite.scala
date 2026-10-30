package external

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.msgpack.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.testing.Suite
import scala.compiletime.testing.typeCheckErrors

/** Exercise visibility as a caller outside the Gemstone package hierarchy. */
class CoreVisibilitySuite extends Suite {
  private case class Payload(value: Int) derives Wire.Codec

  test("derived codecs and runtime refinements work from an external package") {
    def positive(value: Int): Int > 0 = value.unsafe[Int > 0]
    assertEquals(positive(3).raw, 3)
    val _       = intercept[IllegalArgumentException](positive(0))
    val codec   = summon[Wire.Codec[Payload]]
    val limits  = Limits(256, 4096, 1024)
    val decoded = codec.encode(Payload(7), limits).flatMap(codec.decode(_, limits))
    assertEquals(decoded, Right(Payload(7)))
  }

  test("collection utilities are public and unsafe wrapping shares array storage") {
    val source            = Array(1, 2)
    val wrapped: Vec[Int] = Vec.unsafeWrapArray(source)
    // Deliberately mutate the source to verify the ownership obligation of this API.
    source(0) = 3
    assertEquals(wrapped.toVector, Vector(3, 2))
    val cache = LRUCache[String, Int](2)
    assertEquals(cache.get("key")(1), 1)
    assertEquals(cache.get("key")(2), 1)
  }

  test("codec storage access remains internal") {
    assert(typeCheckErrors("net.kurobako.gemstone.rpc.Wire.Codec[Int].erased").nonEmpty)
    assert(typeCheckErrors("net.kurobako.gemstone.rpc.Wire.Codec[Int].nullary").nonEmpty)
    assert(typeCheckErrors(
      "net.kurobako.gemstone.rpc.Wire.Codec.instance[Int](null, (_, _) => (), _ => Left(\"\"))"
    ).nonEmpty)
  }
}
