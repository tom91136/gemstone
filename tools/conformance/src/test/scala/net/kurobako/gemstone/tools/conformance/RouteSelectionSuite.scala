package net.kurobako.gemstone.tools.conformance

class RouteSelectionSuite extends munit.FunSuite {
  private val inventory =
    Vector("cuda-ptx/sm_89", "cuda-sass/sm_89", "cuda-ptx/sm_89/clang", "cuda-sass/sm_89/clang", "sycl")

  private def select(selector: String): Vector[String] =
    inventory.filter(CLI.routeMatches(_, selector, inventory))

  test("an exact route name selects only that route") {
    assertEquals(select("cuda-sass/sm_89"), Vector("cuda-sass/sm_89"))
  }

  test("a shared route name selects the code modes and compilers") {
    assertEquals(
      select("cuda/sm_89"),
      Vector("cuda-ptx/sm_89", "cuda-sass/sm_89", "cuda-ptx/sm_89/clang", "cuda-sass/sm_89/clang")
    )
  }

  test("a route prefix selects its variants") {
    assertEquals(select("cuda-sass"), Vector("cuda-sass/sm_89", "cuda-sass/sm_89/clang"))
  }
}
