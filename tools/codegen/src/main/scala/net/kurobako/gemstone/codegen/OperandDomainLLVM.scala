package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.string.*

/** The operand facts LLVM's AMDGPU target tables state: register encodings, file extents, and operand classes. */
object OperandDomainLLVM {

  /** A register with the encoded `code` it has under `variant`, the TableGen suffix naming its generations. */
  case class RegisterDef(asmName: String, code: Int, variant: String)

  /** An operand class; `literal` is false for the inline-constant-only classes. */
  case class OperandClass(literal: Boolean)

  case class Model(
      encValues: Map[String, Int],
      registers: Vector[RegisterDef],
      fileLast: Map[String, Int],
      operandClasses: Map[String, OperandClass],
      exportTargets: Map[String, Int]
  ) {
    def variantsOf(name: String): Vector[RegisterDef] = registers.filter(_.asmName == name)
  }

  private val Variants = "(.*)_(gfxpre11|gfx11plus|vi|gfx9plus|ci)".r
  private val Plain    = """(?m)^\s*defm\s+(\w+)\s*:\s*SIRegLoHi16\s*<\s*"([^"]*)"\s*,\s*([0-9]+)\s*[,>]""".r
  private val Aperture = """defm\s+\w+\s*:\s*ApertureRegister<"([^"]*)",\s*([0-9]+)>""".r
  private val Scratch  = """defm\s+FLAT_SCR_(?:LO|HI)\s*:\s*FLAT_SCR_LOHI_m<"([^"]*)",\s*([0-9]+),\s*([0-9]+)>""".r
  private val Trap     =
    """defm\s+TTMP#Index#_(vi|gfx9plus)\s*:\s*SIRegLoHi16<"ttmp"#Index,\s*!add\(([0-9]+),\s*Index\)>""".r
  private val Direct   = """def\s+LDS_DIRECT\s*:\s*SIReg\s*<"([^"]*)",\s*([0-9]+)>""".r
  private val Extent   = raw"foreach\s+Index\s*=\s*0\.\.\.([0-9]+)\s+in\s*\{\s*defm\s+(SGPR|VGPR|AGPR)#Index".r
  private val Sequence = """sequence\s+"(TTMP)%u",\s*0,\s*([0-9]+)""".r
  private val Class    =
    raw"def\s+(\w+)\s*:\s*SrcRegOrImmA?9(?:_t16)?\s*<\s*(\w+)\s*,\s*\"OPERAND_REG_(IMM|INLINE_C|INLINE_AC)_\w+\"".r
  private val Vector9 = raw"def\s+(\w+)\s*:\s*SrcReg9\s*<\s*(\w+)\s*>".r
  private val Enum    = raw"(?m)^\s*(\w+)\s*=\s*([0-9]+)\s*,".r

  def read(root: Path): Model = {
    def text(file: String): String = Files.readString(root.resolve(file), StandardCharsets.UTF_8)
    val defines                    = text("SIDefines.h")
    val registers                  = text("SIRegisterInfo.td")
    Model(
      encValues = enumeration(defines, "namespace EncValues"),
      registers = registerDefs(registers),
      fileLast = Extent.findAllMatchIn(registers).map(found => found.group(2).toLowerCaseRoot -> found.group(1).toInt)
        .toMap ++ Sequence.findAllMatchIn(registers).map(found => "ttmp" -> found.group(2).toInt),
      operandClasses = Class.findAllMatchIn(registers).map { found =>
        found.group(1) -> OperandClass(found.group(3) == "IMM")
      }.toMap ++ Vector9.findAllMatchIn(registers).map { found =>
        found.group(1) -> OperandClass(literal = false)
      },
      exportTargets = exportTargets(enumeration(defines, "ET_MRT0 = 0"))
    )
  }

  private def registerDefs(source: String): Vector[RegisterDef] = {
    def definition(tdName: String, asm: String, code: Int): RegisterDef = tdName match {
      case Variants(_, variant) => RegisterDef(asm, code, variant)
      case _                    => RegisterDef(asm, code, "")
    }
    val plain = Plain.findAllMatchIn(source).map(found =>
      definition(found.group(1), found.group(2), found.group(3).toInt)
    ).toVector
    val apertures = Aperture.findAllMatchIn(source).map(found => RegisterDef(found.group(1), found.group(2).toInt, ""))
    val scratch   = Scratch.findAllMatchIn(source).flatMap { found =>
      Vector(
        RegisterDef(found.group(1), found.group(2).toInt, "ci"),
        RegisterDef(found.group(1), found.group(3).toInt, "vi")
      )
    }
    val trap = Trap.findAllMatchIn(source).flatMap { found =>
      Vector.tabulate(16)(index => RegisterDef(s"ttmp$index", found.group(2).toInt + index, found.group(1)))
    }
    val direct   = Direct.findAllMatchIn(source).map(found => RegisterDef(found.group(1), found.group(2).toInt, ""))
    val all      = plain ++ apertures ++ scratch ++ trap ++ direct
    val variants = all.filter(_.variant.nonEmpty).map(_.asmName).toSet
    all.filterNot(register =>
      register.asmName.isEmpty || (register.variant.isEmpty && variants(register.asmName) && register.code == 0)
    ).distinct
  }

  private def enumeration(text: String, anchor: String): Map[String, Int] = {
    val start = text.indexOf(anchor)
    require(start >= 0, s"missing LLVM table $anchor")
    val body = text.substring(start, text.indexOf("};", start))
    Enum.findAllMatchIn(body).map(found => found.group(1) -> found.group(2).toInt).toMap
  }

  private def exportTargets(values: Map[String, Int]): Map[String, Int] = {
    def range(name: String, first: String, last: String) = (values(first) to values(last)).zipWithIndex.map(
      (code, index) => s"$name$index" -> code
    )
    (range("mrt", "ET_MRT0", "ET_MRT7") ++ range("pos", "ET_POS0", "ET_POS4") ++
      range("param", "ET_PARAM0", "ET_PARAM31") ++
      Vector(
        "mrtz"            -> values("ET_MRTZ"),
        "null"            -> values("ET_NULL"),
        "prim"            -> values("ET_PRIM"),
        "dual_src_blend0" -> values("ET_DUAL_SRC_BLEND0"),
        "dual_src_blend1" -> values("ET_DUAL_SRC_BLEND1")
      )).toMap
  }
}
