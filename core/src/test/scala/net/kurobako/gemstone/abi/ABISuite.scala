package net.kurobako.gemstone.abi

import net.kurobako.gemstone.Vec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.compiletime.testing.typeCheckErrors

final class ABISuite extends munit.FunSuite {
  test("pointer capabilities enforce initialization and access direction") {
    val input = Pointer.input[Int, One](Vec(7)).fold(error => fail(error.message), value => value)
    assertEquals(input.read(), Right(7))

    val output = Pointer.output[Int, One](1).fold(error => fail(error.message), value => value)
    assertEquals(output.snapshot, Right(None))
    assertEquals(output.write(11), Right(()))
    assert(output.snapshot.exists(_.exists(_.sameElements(Seq(11)))))

    val partial = Pointer.output[Int, Fixed[2]](2).fold(error => fail(error.message), value => value)
    assertEquals(partial.write(3, 0), Right(()))
    assertEquals(partial.snapshot, Right(None))
    assertEquals(partial.write(5, 1), Right(()))
    assert(partial.snapshot.exists(_.exists(_.sameElements(Seq(3, 5)))))
  }

  test("pointer capabilities are bounds checked") {
    val values = Pointer.inputOutput[Int, Fixed[2]](Vec(1, 2)).fold(error => fail(error.message), value => value)
    assertEquals(values.read(1), Right(2))
    assert(values.write(3, 2).isLeft)
  }

  test("pointer extents validate their runtime storage shape") {
    assert(Pointer.input[Int, One](Vec(1, 2)).isLeft)
    assert(Pointer.output[Int, One](0).isLeft)
    assert(Pointer.input[Int, Fixed[2]](Vec(1)).isLeft)
    assert(Pointer.output[Int, Fixed[2]](3).isLeft)
    assert(
      typeCheckErrors("""
        import net.kurobako.gemstone.abi.*
        Pointer.output[Int, Fixed[0]](0)
      """).nonEmpty
    )

    val empty = Pointer.output[Byte, CountedBy["count"]](0).fold(error => fail(error.message), value => value)
    assert(empty.snapshot.exists(_.exists(_.isEmpty)))

    val bytes =
      NulTerminatedBytes.either(Vec[Byte](120.toByte, 0.toByte)).fold(message => fail(message), value => value)
    assertEquals(Pointer.cString(bytes).length, 2)
    assert(NulTerminatedBytes.either(Vec[Byte](120.toByte)).isLeft)
  }

  test("opaque pointers cannot be dereferenced") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      def readOpaque(pointer: OpaquePointer): Either[PointerError, CVoid] = pointer.read()
    """)
    assert(errors.exists(_.message.contains("CanDereference")))
  }

  test("nullable is a nominal and explicit ABI value") {
    val pointer = Pointer.input[Int, One](Vec(7)).fold(error => fail(error.message), value => value)
    val missing: Nullable[net.kurobako.gemstone.abi.In[Int]] = Nullable.Null
    val present: Nullable[net.kurobako.gemstone.abi.In[Int]] = Nullable.NonNull(pointer)
    assertEquals(missing.toOption, None)
    assertEquals(present.toOption.map(_.read()), Some(Right(7)))
  }

  test("callbacks are typed functions with explicit failure") {
    val callback: Callback[(Int, String), String] = arguments => Right(arguments._2 * arguments._1)

    assertEquals(callback((3, "go")), Right("gogogo"))
  }

  test("nominal C values are constructible without losing their C type identity") {
    val flags = CValue.fromUnsignedLong["ze_init_flags_t"](3L, 4)

    assertEquals(flags.byteSize, 4)
    assertEquals(flags.scalarByteSize, 4)
    assertEquals(flags.toUnsignedLong, 3L)
    assertEquals(flags.bytes.toSeq, Seq[Byte](3, 0, 0, 0))

    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      val flags: CValue["ze_init_flags_t"] = CValue.fromUnsignedLong["other_flags_t"](1L, 4)
    """)
    assert(errors.nonEmpty)

    val record = CValue.fromBytes["record"](Vec.fill(9)(0.toByte).refineUnsafe[MinLength[1]])
    assertEquals(record.scalar, None)
  }
}
