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

  test("concrete API methods must use a declarative implementation marker") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      trait InvalidImplementation {
        def accidentalLogic(value: Int): Int = value + 1
      }
      ABIDerivation.methods[InvalidImplementation]
    """)
    assert(errors.exists(_.message.contains("must consist solely")))
  }

  test("nullable ABI values must be pointer-shaped") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      trait InvalidNullable {
        def invalidNullable(value: Nullable[Int]): Unit
      }
      ABIDerivation.cFunctions[InvalidNullable]
    """)
    assert(errors.exists(_.message.contains("requires a pointer-shaped ABI value")))
  }

  test("literal ABI declarations reject malformed C spellings and array extents") {
    val badType = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      trait InvalidCValue {
        def invalidType(value: CValue["int); injected("]): Unit
      }
      ABIDerivation.cFunctions[InvalidCValue]
    """)
    assert(badType.exists(_.message.contains("invalid C type spelling")))

    val badArray = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      trait InvalidArray {
        def invalidArray(value: CArray[Int, 0]): Unit
      }
      ABIDerivation.cFunctions[InvalidArray]
    """)
    assert(badArray.exists(_.message.contains("must have a positive size")))
  }

  test("ABI pointer counts must name an integral parameter") {
    val missing = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      trait MissingCount {
        def copy(buffer: InputBuffer[Byte, "count"]): Unit
      }
      ABIDerivation.cFunctions[MissingCount]
    """)
    assert(missing.exists(_.message.contains("parameter is absent")))

    val nonIntegral = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      trait NonIntegralCount {
        def copy(buffer: InputBuffer[Byte, "count"], count: Double): Unit
      }
      ABIDerivation.cFunctions[NonIntegralCount]
    """)
    assert(nonIntegral.exists(_.message.contains("must have an integral ABI type")))
  }
}
