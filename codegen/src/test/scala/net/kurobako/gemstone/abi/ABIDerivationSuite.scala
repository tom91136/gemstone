package net.kurobako.gemstone.abi

trait DerivationFixture {
  @CSignature("int (int)")
  def abstractCall(value: Int): Int

  @CSignature("int (int)")
  @NativeDelegate("gemstone_fixture_delegate")
  def delegatedCall(value: Int): Int = delegate["gemstone_fixture_delegate"](value)
  @CSignature("int (int)")
  def unsupportedCall(value: Int): Int = unsupported
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
}
