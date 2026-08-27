// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu.common

private[common] object InstructionDefs17 {
  case object VCvtPknormI16F16_2 extends InstructionDef(
        "v_cvt_pknorm_i16_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPknormI16F16_2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            665,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            665,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            665,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            665,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            786,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            786,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            786,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            786,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            786,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            786,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            786,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            786,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            665,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCvtPknormI16F32_2 extends InstructionDef(
        "v_cvt_pknorm_i16_f32",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPknormI16F32_2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            660,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            660,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            660,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            660,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            872,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            872,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            872,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            872,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            872,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            872,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            872,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            872,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            301,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            301,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            660,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            660,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCvtPknormU16F16_2 extends InstructionDef(
        "v_cvt_pknorm_u16_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPknormU16F16_2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            666,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            666,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            666,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            666,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            787,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            787,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            787,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            787,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            787,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            787,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            787,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            787,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 16, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 16, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            666,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 16, true, false, false),
              OperandInfo(None, "float", 16, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCvtPknormU16F32_2 extends InstructionDef(
        "v_cvt_pknorm_u16_f32",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPknormU16F32_2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            661,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            661,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            661,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            661,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            873,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            873,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            873,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            873,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            873,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            873,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            873,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            873,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP3",
            302,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP3",
            302,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            661,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            661,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCvtPkrtzF16F32_2 extends InstructionDef(
        "v_cvt_pkrtz_f16_f32",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPkrtzF16F32_2(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            662,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            662,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            662,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            662,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_INST_LITERAL",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP16",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_DPP8",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP2_VOP_SDWA",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            303,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            303,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            303,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            303,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_INST_LITERAL",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP16",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_DPP8",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP2_VOP_SDWA",
            47,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("VSRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            303,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            303,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            303,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            303,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC_NOLDS", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP2",
            47,
            Vector(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP2",
            47,
            Vector(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCvtPkrtzF16F32E64 extends InstructionDef(
        "v_cvt_pkrtz_f16_f32_e64",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPkrtzF16F32E64(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP3",
            662,
            Vector(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP3",
            662,
            Vector(
              OperandInfo(None, "float", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCvtRpiI32F32 extends InstructionDef(
        "v_cvt_rpi_i32_f32",
        decoder(form(2)((operands, mods) => Instruction.VCvtRpiI32F32(operands(0), operands(1), mods))),
        Metadata(Vector(
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_INST_LITERAL",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_VOP_DPP",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP1_VOP_SDWA",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA1,
            "cdna1",
            "VOP3",
            332,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_INST_LITERAL",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_DPP",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP1_VOP_SDWA",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA2,
            "cdna2",
            "VOP3",
            332,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_INST_LITERAL",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_DPP",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP1_VOP_SDWA",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA3,
            "cdna3",
            "VOP3",
            332,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_INST_LITERAL",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_DPP",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP1_VOP_SDWA",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            332,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_INST_LITERAL",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_DPP16",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_DPP8",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP1_VOP_SDWA",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3",
            396,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna1",
            "VOP3_INST_LITERAL",
            396,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_INST_LITERAL",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_DPP16",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_DPP8",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP1_VOP_SDWA",
            12,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("VSRC0"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3",
            396,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX10,
            "rdna2",
            "VOP3_INST_LITERAL",
            396,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.GFX6,
            "gfx6",
            "VOP1",
            12,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX7,
            "gfx7",
            "VOP1",
            12,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX8,
            "gfx8",
            "VOP1",
            12,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          ),
          Encoding(
            Generation.GFX9,
            "gfx9",
            "VOP1",
            12,
            Vector(
              OperandInfo(None, "uint", 32, false, true, false),
              OperandInfo(None, "float", 32, true, false, false)
            ),
            Some("valu32"),
            MetadataSource.MesaACO
          )
        ))
      )
  case object VCvtScalePk16Bf16Bf6 extends InstructionDef(
        "v_cvt_scale_pk16_bf16_bf6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16Bf16Bf6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            715,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            715,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk16Bf16Fp6 extends InstructionDef(
        "v_cvt_scale_pk16_bf16_fp6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16Bf16Fp6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            712,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            712,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk16F16Bf6 extends InstructionDef(
        "v_cvt_scale_pk16_f16_bf6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16F16Bf6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            714,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            714,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk16F16Fp6 extends InstructionDef(
        "v_cvt_scale_pk16_f16_fp6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16F16Fp6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            711,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            711,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk16F32Bf6 extends InstructionDef(
        "v_cvt_scale_pk16_f32_bf6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16F32Bf6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            716,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            716,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk16F32Fp6 extends InstructionDef(
        "v_cvt_scale_pk16_f32_fp6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16F32Fp6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            713,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            713,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 96, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk8Bf16Bf8 extends InstructionDef(
        "v_cvt_scale_pk8_bf16_bf8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8Bf16Bf8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            684,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            684,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk8Bf16Fp4 extends InstructionDef(
        "v_cvt_scale_pk8_bf16_fp4",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8Bf16Fp4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            672,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            672,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk8Bf16Fp8 extends InstructionDef(
        "v_cvt_scale_pk8_bf16_fp8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8Bf16Fp8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            681,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            681,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk8F16Bf8 extends InstructionDef(
        "v_cvt_scale_pk8_f16_bf8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F16Bf8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            683,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            683,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk8F16Fp4 extends InstructionDef(
        "v_cvt_scale_pk8_f16_fp4",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F16Fp4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            671,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            671,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk8F16Fp8 extends InstructionDef(
        "v_cvt_scale_pk8_f16_fp8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F16Fp8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            680,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            680,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk8F32Bf8 extends InstructionDef(
        "v_cvt_scale_pk8_f32_bf8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F32Bf8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            685,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            685,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk8F32Fp4 extends InstructionDef(
        "v_cvt_scale_pk8_f32_fp4",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F32Fp4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            673,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            673,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalePk8F32Fp8 extends InstructionDef(
        "v_cvt_scale_pk8_f32_fp8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F32Fp8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            682,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            682,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef322xpk16Bf6F32 extends InstructionDef(
        "v_cvt_scalef32_2xpk16_bf6_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef322xpk16Bf6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            595,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef322xpk16Fp6F32 extends InstructionDef(
        "v_cvt_scalef32_2xpk16_fp6_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef322xpk16Fp6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            594,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32F16Bf8 extends InstructionDef(
        "v_cvt_scalef32_f16_bf8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32F16Bf8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            587,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 8, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32F16Fp8 extends InstructionDef(
        "v_cvt_scalef32_f16_fp8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32F16Fp8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            586,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 8, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32F32Bf8 extends InstructionDef(
        "v_cvt_scalef32_f32_bf8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32F32Bf8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            572,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 8, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32F32Fp8 extends InstructionDef(
        "v_cvt_scalef32_f32_fp8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32F32Fp8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            571,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 8, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk16Bf6Bf16 extends InstructionDef(
        "v_cvt_scalef32_pk16_bf6_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Bf6Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            722,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            722,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk16Bf6F16 extends InstructionDef(
        "v_cvt_scalef32_pk16_bf6_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Bf6F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            720,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            720,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk16Bf6F32 extends InstructionDef(
        "v_cvt_scalef32_pk16_bf6_f32",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Bf6F32(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            718,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            718,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk16Fp6Bf16 extends InstructionDef(
        "v_cvt_scalef32_pk16_fp6_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Fp6Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            721,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            721,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk16Fp6F16 extends InstructionDef(
        "v_cvt_scalef32_pk16_fp6_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Fp6F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            719,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            719,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk16Fp6F32 extends InstructionDef(
        "v_cvt_scalef32_pk16_fp6_f32",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Fp6F32(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            717,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            717,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk32Bf16Bf6 extends InstructionDef(
        "v_cvt_scalef32_pk32_bf16_bf6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Bf16Bf6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            611,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 192, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk32Bf16Fp6 extends InstructionDef(
        "v_cvt_scalef32_pk32_bf16_fp6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Bf16Fp6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            609,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 192, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk32Bf6Bf16 extends InstructionDef(
        "v_cvt_scalef32_pk32_bf6_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Bf6Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            603,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk32Bf6F16 extends InstructionDef(
        "v_cvt_scalef32_pk32_bf6_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Bf6F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            602,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk32F16Bf6 extends InstructionDef(
        "v_cvt_scalef32_pk32_f16_bf6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32F16Bf6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            610,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 192, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk32F16Fp6 extends InstructionDef(
        "v_cvt_scalef32_pk32_f16_fp6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32F16Fp6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            608,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 192, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk32F32Bf6 extends InstructionDef(
        "v_cvt_scalef32_pk32_f32_bf6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32F32Bf6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            599,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 192, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk32F32Fp6 extends InstructionDef(
        "v_cvt_scalef32_pk32_f32_fp6",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32F32Fp6(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            598,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 192, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk32Fp6Bf16 extends InstructionDef(
        "v_cvt_scalef32_pk32_fp6_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Fp6Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            601,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk32Fp6F16 extends InstructionDef(
        "v_cvt_scalef32_pk32_fp6_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Fp6F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            600,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk8Bf8Bf16 extends InstructionDef(
        "v_cvt_scalef32_pk8_bf8_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Bf8Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            693,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            693,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk8Bf8F16 extends InstructionDef(
        "v_cvt_scalef32_pk8_bf8_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Bf8F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            710,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            710,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk8Bf8F32 extends InstructionDef(
        "v_cvt_scalef32_pk8_bf8_f32",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Bf8F32(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            709,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            709,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk8Fp4Bf16 extends InstructionDef(
        "v_cvt_scalef32_pk8_fp4_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp4Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            696,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            696,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk8Fp4F16 extends InstructionDef(
        "v_cvt_scalef32_pk8_fp4_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp4F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            691,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            691,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk8Fp4F32 extends InstructionDef(
        "v_cvt_scalef32_pk8_fp4_f32",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp4F32(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            688,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            688,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk8Fp8Bf16 extends InstructionDef(
        "v_cvt_scalef32_pk8_fp8_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp8Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            692,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            692,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk8Fp8F16 extends InstructionDef(
        "v_cvt_scalef32_pk8_fp8_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp8F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            708,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            708,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32Pk8Fp8F32 extends InstructionDef(
        "v_cvt_scalef32_pk8_fp8_f32",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp8F32(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            707,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            707,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkBf16Bf8 extends InstructionDef(
        "v_cvt_scalef32_pk_bf16_bf8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkBf16Bf8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            618,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkBf16Fp4 extends InstructionDef(
        "v_cvt_scalef32_pk_bf16_fp4",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkBf16Fp4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            593,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 8, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkBf16Fp8 extends InstructionDef(
        "v_cvt_scalef32_pk_bf16_fp8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkBf16Fp8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            617,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkBf8Bf16 extends InstructionDef(
        "v_cvt_scalef32_pk_bf8_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkBf8Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            581,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkBf8F16 extends InstructionDef(
        "v_cvt_scalef32_pk_bf8_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkBf8F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            577,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkBf8F32 extends InstructionDef(
        "v_cvt_scalef32_pk_bf8_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32PkBf8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            566,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkF16Bf8 extends InstructionDef(
        "v_cvt_scalef32_pk_f16_bf8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF16Bf8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            585,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkF16Fp4 extends InstructionDef(
        "v_cvt_scalef32_pk_f16_fp4",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF16Fp4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            592,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 8, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkF16Fp8 extends InstructionDef(
        "v_cvt_scalef32_pk_f16_fp8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF16Fp8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            584,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkF32Bf8 extends InstructionDef(
        "v_cvt_scalef32_pk_f32_bf8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF32Bf8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            570,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkF32Fp4 extends InstructionDef(
        "v_cvt_scalef32_pk_f32_fp4",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF32Fp4(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            575,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 8, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkF32Fp8 extends InstructionDef(
        "v_cvt_scalef32_pk_f32_fp8",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF32Fp8(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            569,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkFp4Bf16 extends InstructionDef(
        "v_cvt_scalef32_pk_fp4_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkFp4Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            589,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkFp4F16 extends InstructionDef(
        "v_cvt_scalef32_pk_fp4_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkFp4F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            588,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkFp4F32 extends InstructionDef(
        "v_cvt_scalef32_pk_fp4_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32PkFp4F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            573,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkFp8Bf16 extends InstructionDef(
        "v_cvt_scalef32_pk_fp8_bf16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkFp8Bf16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            580,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkFp8F16 extends InstructionDef(
        "v_cvt_scalef32_pk_fp8_f16",
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkFp8F16(operands(0), operands(1), operands(2), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            576,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32PkFp8F32 extends InstructionDef(
        "v_cvt_scalef32_pk_fp8_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32PkFp8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            565,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 16, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrBf8Bf16 extends InstructionDef(
        "v_cvt_scalef32_sr_bf8_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrBf8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            583,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrBf8F16 extends InstructionDef(
        "v_cvt_scalef32_sr_bf8_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrBf8F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            579,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrBf8F32 extends InstructionDef(
        "v_cvt_scalef32_sr_bf8_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrBf8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            568,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrFp8Bf16 extends InstructionDef(
        "v_cvt_scalef32_sr_fp8_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrFp8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            582,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrFp8F16 extends InstructionDef(
        "v_cvt_scalef32_sr_fp8_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrFp8F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            578,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 16, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrFp8F32 extends InstructionDef(
        "v_cvt_scalef32_sr_fp8_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrFp8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            567,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk16Bf6Bf16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk16_bf6_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Bf6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            728,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            728,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            728,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            728,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk16Bf6F16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk16_bf6_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Bf6F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            726,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            726,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            726,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            726,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk16Bf6F32 extends InstructionDef(
        "v_cvt_scalef32_sr_pk16_bf6_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Bf6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            724,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            724,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            724,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            724,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk16Fp6Bf16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk16_fp6_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Fp6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            727,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            727,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            727,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            727,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk16Fp6F16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk16_fp6_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Fp6F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            725,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            725,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            725,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            725,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk16Fp6F32 extends InstructionDef(
        "v_cvt_scalef32_sr_pk16_fp6_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Fp6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            723,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            723,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            723,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            723,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 96, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk32Bf6Bf16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk32_bf6_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Bf6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            607,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk32Bf6F16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk32_bf6_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Bf6F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            606,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk32Bf6F32 extends InstructionDef(
        "v_cvt_scalef32_sr_pk32_bf6_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Bf6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            597,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 1024, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk32Fp6Bf16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk32_fp6_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Fp6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            605,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk32Fp6F16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk32_fp6_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Fp6F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            604,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk32Fp6F32 extends InstructionDef(
        "v_cvt_scalef32_sr_pk32_fp6_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Fp6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            596,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 192, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 1024, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk8Bf8Bf16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk8_bf8_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Bf8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            706,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            706,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            706,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            706,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk8Bf8F16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk8_bf8_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Bf8F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            705,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            705,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            705,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            705,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk8Bf8F32 extends InstructionDef(
        "v_cvt_scalef32_sr_pk8_bf8_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Bf8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            665,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            665,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            665,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            665,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk8Fp4Bf16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk8_fp4_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp4Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            700,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            700,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            700,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            700,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk8Fp4F16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk8_fp4_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp4F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            697,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            697,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            697,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            697,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk8Fp4F32 extends InstructionDef(
        "v_cvt_scalef32_sr_pk8_fp4_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp4F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            663,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            663,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            663,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            663,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 32, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk8Fp8Bf16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk8_fp8_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            704,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            704,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            704,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            704,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk8Fp8F16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk8_fp8_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp8F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            703,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            703,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            703,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            703,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPk8Fp8F32 extends InstructionDef(
        "v_cvt_scalef32_sr_pk8_fp8_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3",
            664,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            664,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            664,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Generation.CDNA5,
            "cdna5",
            "VOP3_INST_LITERAL",
            664,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false),
              OperandInfo(Some("LITERAL"), "SRC", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPkFp4Bf16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk_fp4_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPkFp4Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            591,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VCvtScalef32SrPkFp4F16 extends InstructionDef(
        "v_cvt_scalef32_sr_pk_fp4_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPkFp4F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Vector(
          Encoding(
            Generation.CDNA4,
            "cdna4",
            "VOP3",
            590,
            Vector(
              OperandInfo(Some("VDST"), "VGPR", 8, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_NOLIT", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Vector[InstructionDef] = Vector(
    VCvtPknormI16F16_2,
    VCvtPknormI16F32_2,
    VCvtPknormU16F16_2,
    VCvtPknormU16F32_2,
    VCvtPkrtzF16F32_2,
    VCvtPkrtzF16F32E64,
    VCvtRpiI32F32,
    VCvtScalePk16Bf16Bf6,
    VCvtScalePk16Bf16Fp6,
    VCvtScalePk16F16Bf6,
    VCvtScalePk16F16Fp6,
    VCvtScalePk16F32Bf6,
    VCvtScalePk16F32Fp6,
    VCvtScalePk8Bf16Bf8,
    VCvtScalePk8Bf16Fp4,
    VCvtScalePk8Bf16Fp8,
    VCvtScalePk8F16Bf8,
    VCvtScalePk8F16Fp4,
    VCvtScalePk8F16Fp8,
    VCvtScalePk8F32Bf8,
    VCvtScalePk8F32Fp4,
    VCvtScalePk8F32Fp8,
    VCvtScalef322xpk16Bf6F32,
    VCvtScalef322xpk16Fp6F32,
    VCvtScalef32F16Bf8,
    VCvtScalef32F16Fp8,
    VCvtScalef32F32Bf8,
    VCvtScalef32F32Fp8,
    VCvtScalef32Pk16Bf6Bf16,
    VCvtScalef32Pk16Bf6F16,
    VCvtScalef32Pk16Bf6F32,
    VCvtScalef32Pk16Fp6Bf16,
    VCvtScalef32Pk16Fp6F16,
    VCvtScalef32Pk16Fp6F32,
    VCvtScalef32Pk32Bf16Bf6,
    VCvtScalef32Pk32Bf16Fp6,
    VCvtScalef32Pk32Bf6Bf16,
    VCvtScalef32Pk32Bf6F16,
    VCvtScalef32Pk32F16Bf6,
    VCvtScalef32Pk32F16Fp6,
    VCvtScalef32Pk32F32Bf6,
    VCvtScalef32Pk32F32Fp6,
    VCvtScalef32Pk32Fp6Bf16,
    VCvtScalef32Pk32Fp6F16,
    VCvtScalef32Pk8Bf8Bf16,
    VCvtScalef32Pk8Bf8F16,
    VCvtScalef32Pk8Bf8F32,
    VCvtScalef32Pk8Fp4Bf16,
    VCvtScalef32Pk8Fp4F16,
    VCvtScalef32Pk8Fp4F32,
    VCvtScalef32Pk8Fp8Bf16,
    VCvtScalef32Pk8Fp8F16,
    VCvtScalef32Pk8Fp8F32,
    VCvtScalef32PkBf16Bf8,
    VCvtScalef32PkBf16Fp4,
    VCvtScalef32PkBf16Fp8,
    VCvtScalef32PkBf8Bf16,
    VCvtScalef32PkBf8F16,
    VCvtScalef32PkBf8F32,
    VCvtScalef32PkF16Bf8,
    VCvtScalef32PkF16Fp4,
    VCvtScalef32PkF16Fp8,
    VCvtScalef32PkF32Bf8,
    VCvtScalef32PkF32Fp4,
    VCvtScalef32PkF32Fp8,
    VCvtScalef32PkFp4Bf16,
    VCvtScalef32PkFp4F16,
    VCvtScalef32PkFp4F32,
    VCvtScalef32PkFp8Bf16,
    VCvtScalef32PkFp8F16,
    VCvtScalef32PkFp8F32,
    VCvtScalef32SrBf8Bf16,
    VCvtScalef32SrBf8F16,
    VCvtScalef32SrBf8F32,
    VCvtScalef32SrFp8Bf16,
    VCvtScalef32SrFp8F16,
    VCvtScalef32SrFp8F32,
    VCvtScalef32SrPk16Bf6Bf16,
    VCvtScalef32SrPk16Bf6F16,
    VCvtScalef32SrPk16Bf6F32,
    VCvtScalef32SrPk16Fp6Bf16,
    VCvtScalef32SrPk16Fp6F16,
    VCvtScalef32SrPk16Fp6F32,
    VCvtScalef32SrPk32Bf6Bf16,
    VCvtScalef32SrPk32Bf6F16,
    VCvtScalef32SrPk32Bf6F32,
    VCvtScalef32SrPk32Fp6Bf16,
    VCvtScalef32SrPk32Fp6F16,
    VCvtScalef32SrPk32Fp6F32,
    VCvtScalef32SrPk8Bf8Bf16,
    VCvtScalef32SrPk8Bf8F16,
    VCvtScalef32SrPk8Bf8F32,
    VCvtScalef32SrPk8Fp4Bf16,
    VCvtScalef32SrPk8Fp4F16,
    VCvtScalef32SrPk8Fp4F32,
    VCvtScalef32SrPk8Fp8Bf16,
    VCvtScalef32SrPk8Fp8F16,
    VCvtScalef32SrPk8Fp8F32,
    VCvtScalef32SrPkFp4Bf16,
    VCvtScalef32SrPkFp4F16
  )
}
