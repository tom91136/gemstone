package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object GenerateSpirvIsa {
  private enum Json {
    case Object(fields: Map[Predef.String, Json])
    case Array(values: Vector[Json])
    case String(value: Predef.String)
    case Number(value: BigDecimal)
    case Bool(value: Boolean)
    case Null
  }

  private final case class Operand(kind: String, name: Option[String], quantifier: Option[String])
  private final case class Entry(
      opcode: Int,
      name: String,
      instructionClass: String,
      version: Option[String],
      capabilities: Vector[String],
      operands: Vector[Operand]
  )

  def main(arguments: Array[String]): Unit = arguments.toVector match {
    case Vector("catalog", grammar, output, packageVersion) =>
      val entries = readGrammar(Paths.get(grammar))
      write(Paths.get(output), renderCatalog(entries, packageVersion))
      println(s"generated ${entries.size} SPIR-V core instructions")
    case Vector("tests", grammar, tests, output, packageVersion) =>
      val entries  = readGrammar(Paths.get(grammar))
      val testRoot = Paths.get(tests)
      val upstream = Vector("binary_parse_test.cpp", "binary_endianness_test.cpp", "val/val_opencl_test.cpp")
        .map(name => name -> discoveredTests(testRoot.resolve(name)))
      write(Paths.get(output), renderTests(entries.size, upstream, packageVersion))
      println(
        s"generated SPIR-V reference suite for ${entries.size} opcodes and ${upstream.map(_._2.size).sum} upstream tests"
      )
    case _ =>
      throw new IllegalArgumentException(
        "usage: GenerateSpirvIsa catalog <grammar> <output> <package> | tests <grammar> <tests> <output> <package>"
      )
  }

  private def readGrammar(path: Path): Vector[Entry] = {
    val root = new JsonParser(Files.readString(path, StandardCharsets.UTF_8)).parse() match {
      case value: Json.Object => value
      case _                  => throw new IllegalArgumentException("SPIR-V grammar root is not an object")
    }
    field(root, "instructions") match {
      case Json.Array(values) =>
        values.map {
          case obj: Json.Object =>
            val operands = optionalField(obj, "operands").toVector.flatMap {
              case Json.Array(items) => items.map {
                  case operand: Json.Object =>
                    Operand(
                      stringField(operand, "kind"),
                      optionalString(operand, "name"),
                      optionalString(operand, "quantifier")
                    )
                  case _ => throw new IllegalArgumentException("instruction operand is not an object")
                }
              case _ => throw new IllegalArgumentException("instruction operands are not an array")
            }
            val capabilities = optionalField(obj, "capabilities").toVector.flatMap {
              case Json.Array(items) => items.map {
                  case Json.String(value) => value
                  case _                  => throw new IllegalArgumentException("capability is not a string")
                }
              case _ => throw new IllegalArgumentException("capabilities are not an array")
            }
            Entry(
              opcode = numberField(obj, "opcode"),
              name = stringField(obj, "opname"),
              instructionClass = stringField(obj, "class"),
              version = optionalString(obj, "version"),
              capabilities = capabilities,
              operands = operands
            )
          case _ => throw new IllegalArgumentException("instruction entry is not an object")
        }.sortBy(_.opcode)
      case _ => throw new IllegalArgumentException("SPIR-V grammar instructions are not an array")
    }
  }

  private def discoveredTests(path: Path): Vector[String] = {
    val pattern = raw"(?:TEST|TEST_F|TEST_P)\s*\(\s*[^,]+,\s*([A-Za-z0-9_]+)\s*\)".r
    pattern.findAllMatchIn(Files.readString(path, StandardCharsets.UTF_8)).map(_.group(1)).toVector.distinct.sorted
  }

  private def renderCatalog(entries: Vector[Entry], packageVersion: String): String = {
    val values = entries.map { entry =>
      val version      = entry.version.map(value => s"Some(\"${escape(value)}\")").getOrElse("None")
      val capabilities = entry.capabilities.map(value => s"\"${escape(value)}\"").mkString("Vector(", ", ", ")")
      val operands     = entry.operands.map { operand =>
        val name       = operand.name.map(value => s"Some(\"${escape(value)}\")").getOrElse("None")
        val quantifier = operand.quantifier.map(value => s"Some(\"${escape(value)}\")").getOrElse("None")
        s"OperandInfo(\"${escape(operand.kind)}\", $name, $quantifier)"
      }.mkString("Vector(", ", ", ")")
      s"    InstructionInfo(Opcode.unsafe(${entry.opcode}), \"${escape(entry.name)}\", \"${escape(entry.instructionClass)}\", $version, $capabilities, $operands)"
    }
    val chunks = values.grouped(75).zipWithIndex.map { case (chunk, index) =>
      s"  private def chunk$index: Vector[InstructionInfo] = Vector(\n${chunk.mkString(",\n")}\n  )"
    }.toVector
    val chunkNames = chunks.indices.map(index => s"chunk$index").mkString("Vector(", ", ", ").flatten")
    s"""|// Generated from the pinned Khronos SPIR-V core grammar. Do not edit by hand.
        |package net.kurobako.gemstone.isa.spirv.$packageVersion.generated
        |
        |import net.kurobako.gemstone.isa.spirv.$packageVersion.InstructionInfo
        |import net.kurobako.gemstone.isa.spirv.$packageVersion.Opcode
        |import net.kurobako.gemstone.isa.spirv.$packageVersion.OperandInfo
        |
        |object Catalog {
        |${chunks.mkString("\n\n")}
        |
        |  val instructions: Vector[InstructionInfo] = $chunkNames
        |}
        |""".stripMargin
  }

  private def renderTests(
      opcodeCount: Int,
      upstream: Vector[(String, Vector[String])],
      packageVersion: String
  ): String = {
    val sourceRows = upstream.map { case (file, names) => s"    \"${escape(file)}\" -> ${names.size}" }.mkString(",\n")
    s"""|// Generated from pinned Khronos grammar and SPIRV-Tools tests. Do not edit by hand.
        |package net.kurobako.gemstone.isa.spirv.$packageVersion.generated
        |
        |import net.kurobako.gemstone.isa.spirv.$packageVersion.*
        |
        |final class ReferenceSuite extends munit.FunSuite {
        |  private val upstreamTestCounts: Map[String, Int] = Map(
        |$sourceRows
        |  )
        |
        |  test("grammar opcode corpus is complete and round-trips little endian") {
        |    assertEquals(InstructionCatalog.instructions.size, $opcodeCount)
        |    InstructionCatalog.instructions.foreach { info =>
        |      val operands = Vector.fill(info.minimumOperandCount)(Word(0))
        |      val module = Module(
        |        Header(Version.V1_6, Word(0), Word(1), Word(0)),
        |        Vector(Instruction(info.opcode, operands)),
        |        Endianness.Little
        |      )
        |      val bytes = Binary.serialize(module).fold(error => fail(error.message), identity)
        |      assertEquals(Binary.parse(bytes), Right(module), info.name)
        |    }
        |  }
        |
        |  test("SPIRV-Tools endian oracle is represented") {
        |    assert(upstreamTestCounts("binary_endianness_test.cpp") > 0)
        |    val module = Module(Header(Version.V1_6, Word(7), Word(2), Word(0)), Vector.empty, Endianness.Big)
        |    val bytes = Binary.serialize(module).fold(error => fail(error.message), identity)
        |    assertEquals(Binary.parse(bytes), Right(module))
        |  }
        |
        |  test("SPIRV-Tools parser negative classes are fail-closed") {
        |    assert(upstreamTestCounts("binary_parse_test.cpp") >= 20)
        |    val malformed = Vector(
        |      IArray[Byte](1, 2, 3),
        |      words(0x07230203, 0x00010600, 0, 1),
        |      words(0xdeadbeef, 0x00010600, 0, 1, 0),
        |      words(0x07230203, 0x00010600, 0, 1, 0, 0),
        |      words(0x07230203, 0x00010600, 0, 1, 0, (3 << 16) | 17, 6)
        |    )
        |    malformed.foreach(bytes => assert(Binary.parse(bytes).isLeft))
        |  }
        |
        |  test("SPIRV-Tools OpenCL validation corpus is tracked") {
        |    assert(upstreamTestCounts("val/val_opencl_test.cpp") >= 10)
        |  }
        |
        |  private def words(values: Int*): IArray[Byte] = {
        |    val bytes = new Array[Byte](values.size * 4)
        |    values.zipWithIndex.foreach { case (value, index) =>
        |      bytes(index * 4) = value.toByte
        |      bytes(index * 4 + 1) = (value >>> 8).toByte
        |      bytes(index * 4 + 2) = (value >>> 16).toByte
        |      bytes(index * 4 + 3) = (value >>> 24).toByte
        |    }
        |    IArray.unsafeFromArray(bytes)
        |  }
        |}
        |""".stripMargin
  }

  private def field(obj: Json.Object, name: String): Json =
    obj.fields.getOrElse(name, throw new IllegalArgumentException(s"missing JSON field $name"))
  private def optionalField(obj: Json.Object, name: String): Option[Json] = obj.fields.get(name)
  private def stringField(obj: Json.Object, name: String): String         = field(obj, name) match {
    case Json.String(value) => value
    case _                  => throw new IllegalArgumentException(s"JSON field $name is not a string")
  }
  private def optionalString(obj: Json.Object, name: String): Option[String] = optionalField(obj, name).map {
    case Json.String(value) => value
    case _                  => throw new IllegalArgumentException(s"JSON field $name is not a string")
  }
  private def numberField(obj: Json.Object, name: String): Int = field(obj, name) match {
    case Json.Number(value) if value.isValidInt => value.toInt
    case _                                      => throw new IllegalArgumentException(s"JSON field $name is not an int")
  }
  private def escape(value: String): String = value.flatMap {
    case '\\' => "\\\\"
    case '"'  => "\\\""
    case '\n' => "\\n"
    case '\r' => "\\r"
    case '\t' => "\\t"
    case char => char.toString
  }

  private def write(path: Path, content: String): Unit = {
    val _ = Files.createDirectories(path.getParent)
    val _ = Files.writeString(path, content, StandardCharsets.UTF_8)
  }

  private final class JsonParser(source: String) {
    private var index = 0

    def parse(): Json = {
      val value = parseValue()
      whitespace()
      require(index == source.length, s"trailing JSON at offset $index")
      value
    }

    private def parseValue(): Json = {
      whitespace()
      if (index >= source.length) fail("unexpected end of JSON")
      source.charAt(index) match {
        case '{'                                 => parseObject()
        case '['                                 => parseArray()
        case '"'                                 => Json.String(parseString())
        case 't'                                 => literal("true", Json.Bool(true))
        case 'f'                                 => literal("false", Json.Bool(false))
        case 'n'                                 => literal("null", Json.Null)
        case char if char == '-' || char.isDigit => parseNumber()
        case char                                => fail(s"unexpected JSON character '$char'")
      }
    }

    private def parseObject(): Json.Object = {
      index += 1
      whitespace()
      val fields = Map.newBuilder[String, Json]
      if (consume('}')) return Json.Object(fields.result())
      var done = false
      while (!done) {
        whitespace()
        if (index >= source.length || source.charAt(index) != '"') fail("expected object key")
        val name = parseString()
        whitespace()
        expect(':')
        fields += name -> parseValue()
        whitespace()
        if (consume('}')) done = true else expect(',')
      }
      Json.Object(fields.result())
    }

    private def parseArray(): Json.Array = {
      index += 1
      whitespace()
      val values = Vector.newBuilder[Json]
      if (consume(']')) return Json.Array(values.result())
      var done = false
      while (!done) {
        values += parseValue()
        whitespace()
        if (consume(']')) done = true else expect(',')
      }
      Json.Array(values.result())
    }

    private def parseString(): String = {
      expect('"')
      val result = new StringBuilder
      var done   = false
      while (!done && index < source.length) {
        val char = source.charAt(index)
        index += 1
        char match {
          case '"'  => done = true
          case '\\' =>
            if (index >= source.length) fail("unterminated escape")
            val escaped = source.charAt(index)
            index += 1
            escaped match {
              case '"'  => result += '"'
              case '\\' => result += '\\'
              case '/'  => result += '/'
              case 'b'  => result += '\b'
              case 'f'  => result += '\f'
              case 'n'  => result += '\n'
              case 'r'  => result += '\r'
              case 't'  => result += '\t'
              case 'u'  =>
                if (index + 4 > source.length) fail("short unicode escape")
                result += Integer.parseInt(source.substring(index, index + 4), 16).toChar
                index += 4
              case other => fail(s"invalid escape '$other'")
            }
          case other => result += other
        }
      }
      if (!done) fail("unterminated string")
      result.result()
    }

    private def parseNumber(): Json.Number = {
      val start = index
      while (index < source.length && "-+0123456789.eE".contains(source.charAt(index))) index += 1
      Json.Number(BigDecimal(source.substring(start, index)))
    }

    private def literal(value: String, result: Json): Json = {
      if (!source.startsWith(value, index)) fail(s"expected $value")
      index += value.length
      result
    }

    private def whitespace(): Unit = while (index < source.length && source.charAt(index).isWhitespace) index += 1
    private def consume(char: Char): Boolean =
      if (index < source.length && source.charAt(index) == char) { index += 1; true }
      else false
    private def expect(char: Char): Unit       = if (!consume(char)) fail(s"expected '$char'")
    private def fail(message: String): Nothing = throw new IllegalArgumentException(s"$message at JSON offset $index")
  }
}
