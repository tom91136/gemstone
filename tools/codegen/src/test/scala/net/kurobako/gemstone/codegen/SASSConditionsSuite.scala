package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.GZIPInputStream
import net.kurobako.gemstone.string.*
import scala.util.Using

class SASSConditionsSuite extends munit.FunSuite {
  import SASSConditions.*

  test("parses nested arithmetic, attribute, enum, parameter, and TABLES expressions") {
    val source = """
      |OOR_REG_ERROR
      | (((Rd)==`Register@RZ)||(((Rd)<=(%MAX_REG_COUNT-1))&&((Rd)!=`Register@R254))) :
      | "register outside range"
      |ILLEGAL_INSTR_ENCODING_ERROR
      | DEFINED TABLES_mem_0(sem,sco,private) -> !(Pg@not == `Predicate@PT) :
      | "invalid combination"
      |""".stripMargin
    val parsed = parse(source).fold(error => fail(error.message), identity)
    assertEquals(parsed.size, 2)
    assertEquals(parsed.head.category, "OOR_REG_ERROR")
    assertEquals(parsed.head.message, "register outside range")
    assertEquals(
      parsed(1).expression,
      Expr.Binary(
        "->",
        Expr.Call(
          "DEFINED",
          Vector(Expr.Call(
            "TABLES_mem_0",
            Vector(
              Expr.Field("sem", None),
              Expr.Field("sco", None),
              Expr.Field("private", None)
            )
          ))
        ),
        Expr.Unary("!", Expr.Binary("==", Expr.Field("Pg", Some("not")), Expr.EnumValue("Predicate", "PT")))
      )
    )
  }

  test("reads named numeric parameters and constants") {
    val source =
      "PARAMETERS\n MAX_REG_COUNT = 254\n SHADER_TYPE = 0\n CONSTANT = 0\nCONSTANTS\n ST_CS = 5\n HEX = 0x10\nSTRING_MAP\n"
    val result = definitions(source).fold(error => fail(error.message), identity)
    assertEquals(
      result.parameters,
      Map("MAX_REG_COUNT" -> BigInt(254), "SHADER_TYPE" -> BigInt(0), "CONSTANT" -> BigInt(0))
    )
    assertEquals(result.constants, Map("ST_CS" -> BigInt(5), "HEX" -> BigInt(16)))
  }

  test("parses nested ternaries in the pinned legacy shared-memory description") {
    val root  = Path.of("target/sass-codegen-references")
    val table =
      readTable(root.resolve("tables/2d374095a77f87553eeeb444c8d09af2fdfef00fa6886e4eeb306d20cca4f4d8.txt.gz"))
    val source = SASSPreprocessor.preprocess(table, architectureSemantics = false)
      .fold(error => fail(error.message), identity)
    val description = SASSDescription.parse(source).fold(error => fail(error.toString), identity)
    val klass       =
      description.classes.find(_.name == "lds__sImmOffset").getOrElse(fail("missing legacy lds__sImmOffset class"))
    assert(klass.raw.toUpperCaseRoot.contains("IOPERAND_TYPE_SHARED_ADDRESS"))
    val actualConditions =
      parse(klass.section("CONDITIONS").map(_.body).getOrElse(fail("missing legacy lds conditions")))
        .fold(error => fail(s"${klass.name}: ${error.message}"), identity)
    assert(actualConditions.exists(_.category.endsWith("_ERROR")))
    val predicate = klass.section("PREDICATES").toVector.flatMap(_.entries).find(_.name == "IDEST_SIZE")
      .getOrElse(fail("missing shared-memory IDEST_SIZE predicate"))
    val parsed = parse(s"ERROR ${predicate.value} : \"shared-memory size\"")
      .fold(error => fail(error.message), identity)
    assertEquals(parsed.map(_.category), Vector("ERROR"))
    assertEquals(parsed.head.message, "shared-memory size")
    assertEquals(
      parsed.head.expression,
      Expr.Binary(
        "*",
        Expr.Conditional(
          Expr.Binary(">=", Expr.Field("sz", None), Expr.Number(8)),
          Expr.Conditional(
            Expr.Binary("<=", Expr.Field("sz", None), Expr.Number(12)),
            Expr.Number(1),
            Expr.Conditional(Expr.Binary("==", Expr.Field("sz", None), Expr.Number(13)), Expr.Number(2), Expr.Number(4))
          ),
          Expr.Conditional(
            Expr.Binary("<=", Expr.Field("sz", None), Expr.Number(4)),
            Expr.Number(1),
            Expr.Conditional(Expr.Binary("==", Expr.Field("sz", None), Expr.Number(5)), Expr.Number(2), Expr.Number(4))
          )
        ),
        Expr.Number(32)
      )
    )
  }

  private val root   = Path.of("target/sass-codegen-references")
  private val tables = ujson.read(Files.readString(root.resolve("manifest.json")))("tables").arr.toVector
  assertEquals(tables.size, 16)
  tables.foreach { metadata =>
    test(s"parses every condition block in ${metadata("sha256").str}") {
      val relativePath   = metadata("path").str
      val digest         = metadata("sha256").str
      val raw            = readTable(root.resolve(relativePath))
      val rawDescription =
        SASSDescription.parse(raw).fold(error => fail(s"$digest raw description: ${error.message}"), identity)
      assertEquals(
        rawDescription.classes.count(klass => !klass.alternate),
        metadata("classes").num.toInt,
        s"$digest primary class count"
      )
      assertEquals(
        rawDescription.classes.count(_.alternate),
        metadata("alternate_classes").num.toInt,
        s"$digest alternate class count"
      )
      assertEquals(
        rawDescription.classes.filterNot(_.alternate).map(_.name).distinct.size,
        metadata("unique_classes").num.toInt,
        s"$digest unique primary class count"
      )
      val source = SASSPreprocessor.preprocess(raw, architectureSemantics = false)
        .fold(error => fail(s"$digest preprocessing: ${error.message}"), identity)
      val description =
        SASSDescription.parse(source).fold(error => fail(s"$digest description: ${error.message}"), identity)
      assert(
        description.classes.nonEmpty && description.classes.size <= rawDescription.classes.size,
        s"$digest active class count"
      )
      assertEquals(
        description.classes.map(_.name).distinct.size,
        description.classes.size,
        s"$digest active class names"
      )
      val conditionBlocks = description.classes.flatMap(klass =>
        klass.sections.filter(section => Set("CONDITION", "CONDITIONS")(section.name)).map(klass -> _)
      )
      assert(conditionBlocks.nonEmpty, s"$digest has no parsed CONDITIONS blocks")
      val parsed = conditionBlocks.map { case (klass, section) =>
        parse(section.body).fold(error => fail(s"$digest ${klass.name}: ${error.message}"), identity)
      }
      assert(parsed.map(_.size).sum > 0)
    }
  }

  private def readTable(path: Path): String =
    Using.resource(GZIPInputStream(Files.newInputStream(path)))(stream =>
      Using.resource(scala.io.Source.fromInputStream(stream))(source => source.mkString)
    )
}
