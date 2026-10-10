package net.kurobako.gemstone.codegen

import scala.concurrent.duration.*

class SASSConditionsSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 120.seconds

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

  private val sharedMemorySize = Expr.Binary(
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

  private def destinationSize(description: SASSDescription.Description): Expr = {
    val predicate = description.classes.find(_.name == "lds__sImmOffset").flatMap(_.section("PREDICATES")).toVector
      .flatMap(_.entries).find(_.name == "IDEST_SIZE").getOrElse(fail("missing shared-memory IDEST_SIZE predicate"))
    val parsed =
      parse(s"ERROR ${predicate.value} : \"shared-memory size\"").fold(error => fail(error.message), identity)
    assertEquals(parsed.map(_.message), Vector("shared-memory size"))
    parsed.head.expression
  }

  test("parses nested ternaries in a shared-memory size predicate") {
    val source =
      """ARCHITECTURE "Volta"
                   |CLASS "lds__sImmOffset"
                   |FORMAT Opcode UImm(4):sz;
                   |PREDICATES
                   |  IDEST_SIZE = ((sz >= 8) ? ((sz <= 12) ? 1 : ((sz == 13) ? 2 : 4 )) : ((sz <= 4) ? 1 : ((sz == 5) ? 2 : 4 )))*32;
                   |""".stripMargin
    assertEquals(destinationSize(SASSDescription.parse(source).fold(e => fail(e.message), identity)), sharedMemorySize)
  }

  test("parses nested ternaries in the pinned legacy shared-memory description") {
    val digest = ExtractSASS.expected("legacy")("SM72")
    val table  = generator.sassReferences.tables.find(_.digest == digest).getOrElse(fail("missing SM72 description"))
    assertEquals(destinationSize(table.description), sharedMemorySize)
  }

  ExtractSASS.expected.values.flatMap(_.values).toVector.distinct.sorted.foreach { digest =>
    test(s"parses every condition block in $digest") {
      val table  = generator.sassReferences.tables.find(_.digest == digest).getOrElse(fail(s"missing $digest"))
      val parsed = table.description.classes.flatMap { klass =>
        klass.sections.view.filter(section => Set("CONDITION", "CONDITIONS")(section.name)).map(section =>
          parse(section.body).fold(error => fail(s"${klass.name}: ${error.message}"), identity)
        )
      }
      assert(parsed.exists(_.nonEmpty), digest)
    }
  }
}
