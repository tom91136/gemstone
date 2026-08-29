package net.kurobako.gemstone.abi

import net.kurobako.gemstone.Col

import scala.compiletime.testing.typeCheckErrors

class ABISuite extends munit.FunSuite {
  test("pointer capabilities enforce initialization and access direction") {
    val input = Pointer.input[Int, One](Col(7))
    assertEquals(input.read(), 7)

    val output = Pointer.output[Int, One](1)
    assertEquals(output.snapshot, None)
    output.write(11)
    assert(output.snapshot.exists(_.sameElements(Seq(11))))

    val partial = Pointer.output[Int, Fixed[2]](2)
    partial.write(3, 0)
    assertEquals(partial.snapshot, None)
    partial.write(5, 1)
    assert(partial.snapshot.exists(_.sameElements(Seq(3, 5))))
  }

  test("pointer capabilities are bounds checked") {
    val values = Pointer.inputOutput[Int, Fixed[2]](Col(1, 2))
    assertEquals(values.read(1), 2)
    intercept[IllegalArgumentException](values.write(3, 2))
  }

  test("pointer extents validate their runtime storage shape") {
    val _ = intercept[IllegalArgumentException](Pointer.input[Int, One](Col(1, 2)))
    val _ = intercept[IllegalArgumentException](Pointer.output[Int, One](0))
    val _ = intercept[IllegalArgumentException](Pointer.input[Int, Fixed[2]](Col(1)))
    val _ = intercept[IllegalArgumentException](Pointer.output[Int, Fixed[2]](3))

    val empty = Pointer.output[Byte, CountedBy["count"]](0)
    assert(empty.snapshot.exists(_.isEmpty))

    assertEquals(Pointer.input[Byte, NulTerminated](Col[Byte](120.toByte, 0.toByte)).length, 2)
    intercept[IllegalArgumentException](Pointer.input[Byte, NulTerminated](Col[Byte](120.toByte)))
  }

  test("opaque pointers cannot be dereferenced") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      def readOpaque(pointer: OpaquePointer): CVoid = pointer.read()
    """)
    assert(errors.exists(_.message.contains("CanDereference")))
  }

  test("nullable is a nominal and explicit ABI value") {
    val pointer                    = Pointer.input[Int, One](Col(7))
    val missing: Nullable[In[Int]] = Nullable.Null
    val present: Nullable[In[Int]] = Nullable.NonNull(pointer)
    assertEquals(missing.toOption, None)
    assertEquals(present.toOption.map(_.read()), Some(7))
  }

  test("callbacks are typed functions with explicit failure") {
    val callback: Callback[(Int, String), String] = arguments => Right(arguments._2 * arguments._1)

    assertEquals(callback((3, "go")), Right("gogogo"))
  }

  test("nominal C values are constructible without losing their C type identity") {
    val flags = CValue.fromUnsignedLong["ze_init_flags_t"](3L, 4)

    assertEquals(flags.byteSize, 4)
    assertEquals(flags.toUnsignedLong, 3L)
    assertEquals(flags.bytes.toSeq, Seq[Byte](3, 0, 0, 0))

    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      val flags: CValue["ze_init_flags_t"] = CValue.fromUnsignedLong["other_flags_t"](1L, 4)
    """)
    assert(errors.nonEmpty)
  }
}
