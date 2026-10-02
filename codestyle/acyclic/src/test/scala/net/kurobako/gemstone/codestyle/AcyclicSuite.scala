package net.kurobako.gemstone.codestyle

import dotty.tools.dotc.Driver
import dotty.tools.dotc.reporting.StoreReporter
import java.nio.file.Files
import java.nio.file.Path
import munit.FunSuite
import scala.jdk.CollectionConverters.*

class AcyclicSuite extends FunSuite {
  private def compile(sources: (String, String)*): List[String] = inspect(sources*)((errors, _) => errors)

  private def inspect[A](sources: (String, String)*)(check: (List[String], Path) => A): A = {
    val directory = Files.createTempDirectory("gemstone-acyclic-")
    try {
      val output = Files.createDirectory(directory.resolve("classes"))
      val files  = sources.map { case (name, source) =>
        val path = directory.resolve(name)
        Files.createDirectories(path.getParent)
        Files.writeString(path, source).toString
      }
      val reporter  = new StoreReporter()
      val arguments = Array(
        "-classpath",
        System.getProperty("gemstone.acyclic.classpath"),
        "-d",
        output.toString,
        s"-Xplugin:${System.getProperty("gemstone.acyclic.jar")}",
        "-Xplugin-require:acyclic",
        "-color:never"
      ) ++ files
      val result = new Driver().process(arguments, reporter)
      check(result.allErrors.map(_.message).toList, directory.resolve("classes-architecture"))
    } finally {
      val paths = Files.walk(directory)
      try paths.iterator().asScala.toList.reverse.foreach(Files.delete)
      finally paths.close()
    }
  }

  private def assertCycle(sources: (String, String)*): Unit = {
    val errors = compile(sources*)
    assertEquals(errors.size, 1, errors.mkString("\n"))
    assert(errors.head.startsWith("Cyclic source dependency ("), errors.head)
    sources.foreach { case (name, _) => assert(errors.head.contains(s"$name:1 ->"), errors.head) }
  }

  test("mutual references report both files and source lines") {
    assertCycle("A.scala" -> "class A(val b: B)", "B.scala" -> "class B(val a: A)")
  }

  test("nested type arguments contribute dependencies") {
    assertCycle("A.scala" -> "class A(val b: List[Option[B]])", "B.scala" -> "class B(val a: List[A])")
  }

  test("inheritance contributes dependencies") {
    assertCycle("A.scala" -> "class A extends B", "B.scala" -> "trait B { def a: A }")
  }

  test("constructor-only parameters contribute dependencies") {
    assertCycle("A.scala" -> "class A(b: B)", "B.scala" -> "class B(a: A)")
  }

  test("class type parameter bounds contribute dependencies") {
    assertCycle("A.scala" -> "class A[T <: B]", "B.scala" -> "class B(val a: A[?])")
  }

  test("term references and inferred result types contribute dependencies") {
    assertCycle("A.scala" -> "object A { def b = B.n }", "B.scala" -> "object B { val n = 1; def a = A.b }")
  }

  test("type aliases contribute dependencies") {
    assertCycle("A.scala" -> "object A { type Item = B }", "B.scala" -> "class B { def a: A.type = A }")
  }

  test("opaque type arguments retain their defining file") {
    assertCycle(
      "A.scala" -> "object A { opaque type Id = Long; def b: B = ??? }",
      "B.scala" -> "class B(val id: Option[A.Id])"
    )
  }

  test("selected givens contribute dependencies") {
    assertCycle(
      "A.scala" -> "object A { import B.given; val n = 1; def value = summon[Ordering[Int]] }",
      "B.scala" -> "object B { given Ordering[Int] = Ordering.by(_ + A.n) }"
    )
  }

  test("annotations contribute dependencies") {
    assertCycle(
      "A.scala" -> "@B class A",
      "B.scala" -> "class B extends scala.annotation.StaticAnnotation { def owner: A = ??? }"
    )
  }

  test("cycles through actual macro expansion are exempt regardless of names") {
    assertEquals(
      compile(
        "A.scala" -> "object A { val n = 1; inline def value: Int = ${ B.impl } }",
        "B.scala" -> "import scala.quoted.*; object B { def impl(using Quotes): Expr[Int] = '{ A.n } }"
      ),
      Nil
    )
  }

  test("a longer cycle crossing a macro expansion is exempt") {
    assertEquals(
      compile(
        "A.scala" -> "object A { val n = 1; inline def value: Int = ${ B.impl } }",
        "B.scala" -> "import scala.quoted.*; object B { def impl(using Quotes): Expr[Int] = C.impl }",
        "C.scala" -> "import scala.quoted.*; object C { def impl(using Quotes): Expr[Int] = '{ A.n } }"
      ),
      Nil
    )
  }

  test("a normal dependency parallel to a macro expansion is still checked") {
    assertCycle(
      "A.scala" -> "object A { val n = B.n; inline def value: Int = ${ B.impl } }",
      "B.scala" -> "import scala.quoted.*; object B { val n = 1; def impl(using Quotes): Expr[Int] = '{ A.n } }"
    )
  }

  test("macro naming does not exempt an ordinary cycle") {
    assertCycle("macros.scala" -> "class Macros(val b: B)", "B.scala" -> "class B(val a: Macros)")
  }

  test("ordinary inline methods do not exempt a cycle") {
    assertCycle(
      "A.scala" -> "object A { val n = 1; inline def value: Int = B.n }",
      "B.scala" -> "object B { def n: Int = A.n }"
    )
  }

  test("cycles spanning three files are reported") {
    assertCycle("A.scala" -> "class A(val b: B)", "B.scala" -> "class B(val c: C)", "C.scala" -> "class C(val a: A)")
  }

  test("a whole mutually dependent group is reported, including overlapping cycles") {
    assertCycle(
      "A.scala" -> "class A(val b: B)",
      "B.scala" -> "class B(val a: A, val c: C)",
      "C.scala" -> "class C(val b: B)"
    )
  }

  test("an ordinary subcycle remains checked when its group also contains a macro") {
    val errors = compile(
      "A.scala" -> "object A { val n = 1; inline def value: Int = ${ B.impl } }",
      "B.scala" ->
        "import scala.quoted.*; object B { def impl(using Quotes): Expr[Int] = '{ A.n }; def c = C.n; val n = 1 }",
      "C.scala" -> "object C { def n = B.n }"
    )
    assertEquals(errors.size, 1)
    assert(errors.head.contains("B.scala:1 ->"))
    assert(errors.head.contains("C.scala:1 ->"))
    assert(!errors.head.contains("A.scala"))
  }

  test("independent cycles are both reported") {
    val errors = compile(
      "A.scala" -> "class A(val b: B)",
      "B.scala" -> "class B(val a: A)",
      "C.scala" -> "class C(val d: D)",
      "D.scala" -> "class D(val c: C)"
    )
    assertEquals(errors.size, 2)
    assert(errors.forall(_.startsWith("Cyclic source dependency (")))
  }

  test("diamonds and self references are acyclic across files") {
    assertEquals(
      compile(
        "A.scala" -> "class A(val b: B, val c: C, val a: Option[A])",
        "B.scala" -> "class B(val d: D)",
        "C.scala" -> "class C(val d: D)",
        "D.scala" -> "class D"
      ),
      Nil
    )
  }

  test("mutual references in one file are allowed") {
    assertEquals(compile("Both.scala" -> "class A(val b: B); class B(val a: A)"), Nil)
  }

  test("unused imports do not create dependencies") {
    assertEquals(
      compile(
        "A.scala" -> "package example; import B.*; object A { val n = 1 }",
        "B.scala" -> "package example; object B { def n = A.n }"
      ),
      Nil
    )
  }

  test("coupling counts distinct types, follows transitive edges and distinguishes relationships") {
    inspect(
      "A.scala" -> "class A(val b: B, val again: B) extends Parent { def value: C = b.value; def make: B = new B }",
      "B.scala" -> "class B { def value: C = new C }",
      "C.scala" -> "class C",
      "Parent.scala" -> "trait Parent"
    ) { (errors, output) =>
      assertEquals(errors, Nil)
      val rows =
        Files.readString(output.resolve("coupling.tsv")).linesIterator.drop(1).map(_.split("\t").toVector).toVector
      val byName = rows.map(row => row(1) -> row).toMap
      assertEquals(byName("A").slice(2, 5), Vector("3", "0", "3"))
      assertEquals(byName("B").slice(2, 5), Vector("1", "1", "1"))
      assertEquals(byName("C").slice(2, 5), Vector("0", "2", "0"))
      val dependencies = Files.readString(output.resolve("dependencies.tsv"))
      for (edge <- Vector("A\tParent\tInherits", "A\tB\tHolds", "A\tC\tExposes", "A\tB\tCalls", "A\tB\tConstructs"))
        assert(dependencies.contains(edge), s"missing $edge in $dependencies")
      assert(Files.readString(output.resolve("scope.txt")).contains("current compiler batch"))
    }
  }

  test("transitive reach crosses source categories without mixing their labels") {
    inspect(
      "src/main/scala/A.scala"           -> "class A(val b: B)",
      "src/main/scala/B.scala"           -> "class B(val c: C)",
      "src/generated/main/scala/C.scala" -> "class C(val d: D)",
      "src/test/scala/D.scala"           -> "class D"
    ) { (errors, output) =>
      assertEquals(errors, Nil)
      val rows =
        Files.readString(output.resolve("coupling.tsv")).linesIterator.drop(1).map(_.split("\t").toVector).toVector
      val byName = rows.map(row => row(1) -> row).toMap
      assertEquals(byName("A").slice(2, 5), Vector("1", "0", "3"))
      assertEquals(byName("C").head, "generated")
      assertEquals(byName("D").head, "test")
    }
  }

  test("macro dependencies remain in the coupling report") {
    inspect(
      "A.scala" -> "object A { val n = 1; inline def value: Int = ${ B.impl } }",
      "B.scala" -> "import scala.quoted.*; object B { def impl(using Quotes): Expr[Int] = '{ A.n } }"
    ) { (errors, output) =>
      assertEquals(errors, Nil)
      assert(Files.readString(output.resolve("dependencies.tsv")).contains("A\tB\tMacroExpansion"))
    }
  }

  test("macro suspension preserves the whole compilation report") {
    inspect(
      "A.scala" -> "object A { val n = 1; inline def value: Int = ${ B.impl } }",
      "B.scala" -> "import scala.quoted.*; object B { def impl(using Quotes): Expr[Int] = '{ A.n } }",
      "C.scala" -> "object C { val n = A.value }"
    ) { (errors, output) =>
      assertEquals(errors, Nil)
      val scope = Files.readString(output.resolve("scope.txt"))
      for (file <- Vector("A.scala", "B.scala", "C.scala")) assert(scope.contains(file), scope)
      val dependencies = Files.readString(output.resolve("dependencies.tsv"))
      assert(dependencies.contains("A\tB\tMacroExpansion"), dependencies)
      assert(dependencies.contains("C\tA\tCalls"), dependencies)
    }
  }

  test("normal cycles involving suspended sources are still rejected") {
    val errors = compile(
      "A.scala"              -> "class A(val b: B) { val n = API.value }",
      "B.scala"              -> "class B(val a: A)",
      "API.scala"            -> "object API { transparent inline def value: Any = ${ Implementation.value } }",
      "Implementation.scala" ->
        "import scala.quoted.*; object Implementation { def value(using Quotes): Expr[Int] = '{ 1 } }"
    )
    assertEquals(errors.size, 1, errors.mkString("\n"))
    assert(errors.head.contains("A.scala:1 ->"))
    assert(errors.head.contains("B.scala:1 ->"))
    assert(!errors.head.contains("Implementation.scala"))
  }

  test("exposure respects private enclosing owners and private methods") {
    inspect(
      "API.scala" ->
        "object API { private class Hidden { def value: B = ??? }; def value: B = ???; private def secret: C = ??? }",
      "B.scala" -> "class B",
      "C.scala" -> "class C"
    ) { (errors, output) =>
      assertEquals(errors, Nil)
      val dependencies = Files.readString(output.resolve("dependencies.tsv"))
      assert(dependencies.contains("API\tB\tExposes"), dependencies)
      assert(!dependencies.contains("API.Hidden\tB\tExposes"), dependencies)
      assert(!dependencies.contains("API\tC\tExposes"), dependencies)
    }
  }

  test("namespace prefixes are recorded separately from dependencies on nested types") {
    inspect(
      "Model.scala" -> "object Model { class Item; class Unrelated }",
      "Use.scala"   -> "class Use(val item: Model.Item)"
    ) { (errors, output) =>
      assertEquals(errors, Nil)
      val dependencies = Files.readString(output.resolve("dependencies.tsv"))
      assert(dependencies.contains("Use\tModel\tQualifier"), dependencies)
      assert(dependencies.contains("Use\tModel.Item\tHolds"), dependencies)
      val rows =
        Files.readString(output.resolve("coupling.tsv")).linesIterator.drop(1).map(_.split("\t").toVector).toVector
      assertEquals(rows.find(_(1) == "Use").get(2), "1")
      val families =
        Files.readString(output.resolve("families.tsv")).linesIterator.drop(1).map(_.split("\t").toVector).toVector
      assertEquals(families.map(_(1)).toSet, Set("Model", "Use"))
      assertEquals(families.find(_(1) == "Use").get(2), "1")
    }
  }

  test("top-level opaque types have source names without synthetic package holders") {
    inspect(
      "Key.scala" -> "opaque type Key = Long; object Key { def create: Key = 0L }",
      "Use.scala" -> "class Use(val key: Key)"
    ) { (errors, output) =>
      assertEquals(errors, Nil)
      val dependencies = Files.readString(output.resolve("dependencies.tsv"))
      assert(dependencies.contains("Use\tKey\tHolds"), dependencies)
      assert(!dependencies.contains("$package"), dependencies)
    }
  }

  test("macro edges are retained but excluded from authored coupling counts") {
    inspect(
      "A.scala" -> "object A { val n = 1; inline def value: Int = ${ B.impl } }",
      "B.scala" -> "import scala.quoted.*; object B { def impl(using Quotes): Expr[Int] = '{ A.n } }"
    ) { (errors, output) =>
      assertEquals(errors, Nil)
      val rows =
        Files.readString(output.resolve("coupling.tsv")).linesIterator.drop(1).map(_.split("\t").toVector).toVector
      assertEquals(rows.find(_(1) == "A").get(2), "0")
      assert(Files.readString(output.resolve("dependencies.tsv")).contains("A\tB\tMacroExpansion"))
    }
  }

  test("generated enum methods are distinguished from authored dependencies") {
    inspect("Choice.scala" -> "enum Choice { case First(value: Int); case Second }") { (errors, output) =>
      assertEquals(errors, Nil)
      val dependencies = Files.readString(output.resolve("dependencies.tsv"))
      assert(dependencies.contains("\tsynthetic"), dependencies)
      val families =
        Files.readString(output.resolve("families.tsv")).linesIterator.drop(1).map(_.split("\t").toVector).toVector
      assertEquals(families.size, 1)
      assertEquals(families.head.slice(2, 5), Vector("0", "0", "0"))
    }
  }

  test("authored lambda bodies and top-level companions remain in coupling counts") {
    inspect(
      "Key.scala" -> "opaque type Key = Long; object Key { def make: () => B = () => new B }",
      "B.scala"   -> "class B"
    ) { (errors, output) =>
      assertEquals(errors, Nil)
      val rows =
        Files.readString(output.resolve("coupling.tsv")).linesIterator.drop(1).map(_.split("\t").toVector).toVector
      assertEquals(rows.find(_(1) == "Key").get(2), "1")
    }
  }

  test("repeated compilations keep detecting cycles") {
    for (_ <- 1 to 2) assertCycle("A.scala" -> "class A(val b: B)", "B.scala" -> "class B(val a: A)")
  }
}
