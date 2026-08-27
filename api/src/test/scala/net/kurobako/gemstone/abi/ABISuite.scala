package net.kurobako.gemstone.abi

class ABISuite extends munit.FunSuite {
  test("pointer capabilities enforce initialization and access direction") {
    val input = Pointer.input[Int, One](IArray(7))
    assertEquals(input.read(), 7)

    val output = Pointer.output[Int, One](1)
    assertEquals(output.snapshot, None)
    output.write(11)
    assertEquals(output.snapshot, Some(Vector(11)))

    val uninitialized = new Pointer[Int, ReadWrite, One](Vector(0), initialized = false)
    intercept[IllegalArgumentException](uninitialized.read())
  }

  test("pointer capabilities are bounds checked") {
    val values = Pointer.inputOutput[Int, Fixed[2]](IArray(1, 2))
    assertEquals(values.read(1), 2)
    intercept[IllegalArgumentException](values.write(3, 2))
  }

  test("callbacks are typed functions with explicit failure") {
    val callback = new Callback[(Int, String), String] {
      override def apply(arguments: (Int, String)): Either[CallbackFailure, String] =
        Right(arguments._2 * arguments._1)
    }

    assertEquals(callback((3, "go")), Right("gogogo"))
  }
}
