package net.kurobako.gemstone.abi

import scala.compiletime.testing.typeCheckErrors

trait DerivationFixture {
  def abstractCall(value: Int): Int
  def delegatedCall(value: Int): Int   = delegate["gemstone_fixture_delegate"](value)
  def unsupportedCall(value: Int): Int = unsupported
}

trait CallbackDerivationFixture {
  def register(
      callback: Callback[(Int, CString, Pointer[CVoid, ReadWrite, Opaque]), Boolean]
  ): Unit
  def registerNamed(callback: CallbackTypedef["fixture_callback_t", Tuple1[Int], Unit]): Unit
}

class ABIDerivationSuite extends munit.FunSuite {
  test("method implementation modes survive TASTy boundaries") {
    assertEquals(
      ABIDerivation.methods[DerivationFixture],
      Vector(
        ABIMethod("abstractCall", MethodImplementation.Remote),
        ABIMethod("delegatedCall", MethodImplementation.NativeDelegate("gemstone_fixture_delegate")),
        ABIMethod("unsupportedCall", MethodImplementation.Unsupported)
      )
    )
  }

  test("normalized C signatures survive TASTy boundaries") {
    assertEquals(
      ABIDerivation.cFunctions[DerivationFixture],
      Vector(
        CFunction("abstractCall", "int (int)"),
        CFunction("delegatedCall", "int (int)"),
        CFunction("unsupportedCall", "int (int)")
      )
    )
  }

  test("normalized C signatures derive from retained Scala ABI types") {
    assertEquals(
      ABIDerivation.cFunctions[DerivationFixture],
      Vector(
        CFunction("abstractCall", "int (int)"),
        CFunction("delegatedCall", "int (int)"),
        CFunction("unsupportedCall", "int (int)")
      )
    )
  }

  test("native delegates retain their exact C declaration") {
    assertEquals(
      ABIDerivation.nativeDelegates[DerivationFixture],
      Vector(
        ABIDeclaration(
          "delegatedCall",
          "int (int)",
          Vector("value"),
          MethodImplementation.NativeDelegate("gemstone_fixture_delegate")
        )
      )
    )
  }

  test("callback C signatures derive from typed argument tuples and results") {
    assertEquals(
      ABIDerivation.cFunctions[CallbackDerivationFixture],
      Vector(
        CFunction(
          "register",
          "void (bool (*)(int, const char *, void *))"
        ),
        CFunction("registerNamed", "void (fixture_callback_t)")
      )
    )
  }

  test("native delegates must forward parameters exactly") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      trait InvalidDelegate {
        def invalidDelegate(first: Int, second: Int): Int =
          delegate["gemstone_invalid_delegate"](second, first)
      }
      ABIDerivation.methods[InvalidDelegate]
    """)
    assert(errors.exists(_.message.contains("must forward every parameter exactly once")))
  }
}
