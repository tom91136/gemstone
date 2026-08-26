package net.kurobako.gemstone.codegen

final class GenerateInstructionAdtSuite extends munit.FunSuite {
  test("generated case names cannot collide on case-insensitive filesystems") {
    val source = GenerateInstructionAdt.render(
      Vector("image_sample_c_d_o", "image_sample_cd_o", "v_cvt_pk_norm", "v_cvt_pknorm"),
      GenerateInstructionAdt.Shape(
        packageName = "example",
        sourceDescription = "test data",
        syntaxType = "Syntax",
        argumentsType = "Arguments",
        opcodeType = "Opcode",
        keyExpression = "value.name",
        argumentsExpression = "value.arguments",
        opcodeExpression = value => s"Opcode(\"$value\")"
      )
    )
    val classNames = raw"final case class ([A-Za-z0-9_]+)".r.findAllMatchIn(source).map(_.group(1)).toVector

    assertEquals(classNames.size, 4)
    assertEquals(classNames.map(_.toLowerCase).distinct.size, classNames.size)
  }
}
