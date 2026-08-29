// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

private[amdgpu] object MatrixInstructionDefs {
  case object SWaitTensorcnt extends InstructionDef(
        "s_wait_tensorcnt",
        decoder(form(1)((operands, mods) => Instruction.SWaitTensorcnt(operands(0), mods))),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "SOPP",
            75,
            Col(
              OperandInfo(Some("SIMM16"), "SIMM16", 16, true, false, false)
            ),
            Some("WAVE_CONTROL"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TensorLoadToLds extends InstructionDef(
        "tensor_load_to_lds",
        decoder(form(4)((operands, mods) =>
          Instruction.TensorLoadToLds(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VIMAGE",
            196,
            Col(
              OperandInfo(Some("VADDR0"), "SGPR", 128, true, false, false),
              OperandInfo(Some("VADDR1"), "SGPR", 256, true, false, false),
              OperandInfo(Some("VADDR2"), "SREG", 128, true, false, false),
              OperandInfo(Some("VADDR3"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object TensorStoreFromLds extends InstructionDef(
        "tensor_store_from_lds",
        decoder(form(4)((operands, mods) =>
          Instruction.TensorStoreFromLds(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VIMAGE",
            197,
            Col(
              OperandInfo(Some("VADDR0"), "SGPR", 128, true, false, false),
              OperandInfo(Some("VADDR1"), "SGPR", 256, true, false, false),
              OperandInfo(Some("VADDR2"), "SREG", 128, true, false, false),
              OperandInfo(Some("VADDR3"), "SREG", 128, true, false, false)
            ),
            Some("VMEM"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x128F8f6f4 extends InstructionDef(
        "v_mfma_f32_16x16x128_f8f6f4",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x128F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            45,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x16Bf16 extends InstructionDef(
        "v_mfma_f32_16x16x16_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            97,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            97,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x16F16_1 extends InstructionDef(
        "v_mfma_f32_16x16x16_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16F16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            77,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            77,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x16bf161k extends InstructionDef(
        "v_mfma_f32_16x16x16bf16_1k",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            103,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x16f16_2 extends InstructionDef(
        "v_mfma_f32_16x16x16f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16f16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            77,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            77,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x14bF32 extends InstructionDef(
        "v_mfma_f32_16x16x1_4b_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x14bF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x1f32 extends InstructionDef(
        "v_mfma_f32_16x16x1f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x1f32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            65,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x2bf16 extends InstructionDef(
        "v_mfma_f32_16x16x2bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            105,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            105,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32Bf16 extends InstructionDef(
        "v_mfma_f32_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            53,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32Bf8Bf8 extends InstructionDef(
        "v_mfma_f32_16x16x32_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            112,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            112,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32Bf8Fp8 extends InstructionDef(
        "v_mfma_f32_16x16x32_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            113,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            113,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32F16 extends InstructionDef(
        "v_mfma_f32_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            84,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32Fp8Bf8 extends InstructionDef(
        "v_mfma_f32_16x16x32_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            114,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            114,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x32Fp8Fp8 extends InstructionDef(
        "v_mfma_f32_16x16x32_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            115,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            115,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x44bBf16 extends InstructionDef(
        "v_mfma_f32_16x16x4_4b_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x44bBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            94,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            94,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x44bF16 extends InstructionDef(
        "v_mfma_f32_16x16x4_4b_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x44bF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            73,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            73,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x4F32_1 extends InstructionDef(
        "v_mfma_f32_16x16x4_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4F32_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x4bf161k extends InstructionDef(
        "v_mfma_f32_16x16x4bf16_1k",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            100,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x4f16 extends InstructionDef(
        "v_mfma_f32_16x16x4f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4f16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            73,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            73,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x4f32_2 extends InstructionDef(
        "v_mfma_f32_16x16x4f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4f32_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            69,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x8Xf32 extends InstructionDef(
        "v_mfma_f32_16x16x8_xf32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x8Xf32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            62,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3216x16x8bf16 extends InstructionDef(
        "v_mfma_f32_16x16x8bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x8bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            109,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            109,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16Bf16 extends InstructionDef(
        "v_mfma_f32_32x32x16_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            55,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16Bf8Bf8 extends InstructionDef(
        "v_mfma_f32_32x32x16_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            116,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            116,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16Bf8Fp8 extends InstructionDef(
        "v_mfma_f32_32x32x16_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            117,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            117,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16F16 extends InstructionDef(
        "v_mfma_f32_32x32x16_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16Fp8Bf8 extends InstructionDef(
        "v_mfma_f32_32x32x16_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            118,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            118,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x16Fp8Fp8 extends InstructionDef(
        "v_mfma_f32_32x32x16_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            119,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            119,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x12bF32 extends InstructionDef(
        "v_mfma_f32_32x32x1_2b_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x12bF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x1f32 extends InstructionDef(
        "v_mfma_f32_32x32x1f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x1f32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            64,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x2F32_1 extends InstructionDef(
        "v_mfma_f32_32x32x2_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x2F32_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x2bf16 extends InstructionDef(
        "v_mfma_f32_32x32x2bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            104,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x2f32_2 extends InstructionDef(
        "v_mfma_f32_32x32x2f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x2f32_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            68,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x42bBf16 extends InstructionDef(
        "v_mfma_f32_32x32x4_2b_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x42bBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x42bF16 extends InstructionDef(
        "v_mfma_f32_32x32x4_2b_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x42bF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            72,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            72,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x4Xf32 extends InstructionDef(
        "v_mfma_f32_32x32x4_xf32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4Xf32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            63,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x4bf16 extends InstructionDef(
        "v_mfma_f32_32x32x4bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            108,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            108,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x4bf161k extends InstructionDef(
        "v_mfma_f32_32x32x4bf16_1k",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            99,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x4f16 extends InstructionDef(
        "v_mfma_f32_32x32x4f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4f16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            72,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            72,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x64F8f6f4 extends InstructionDef(
        "v_mfma_f32_32x32x64_f8f6f4",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x64F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            46,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x8Bf16 extends InstructionDef(
        "v_mfma_f32_32x32x8_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            96,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            96,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x8F16_1 extends InstructionDef(
        "v_mfma_f32_32x32x8_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8F16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            76,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            76,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x8bf161k extends InstructionDef(
        "v_mfma_f32_32x32x8bf16_1k",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            102,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF3232x32x8f16_2 extends InstructionDef(
        "v_mfma_f32_32x32x8f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8f16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            76,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            76,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x116bF32 extends InstructionDef(
        "v_mfma_f32_4x4x1_16b_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x116bF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x1f32 extends InstructionDef(
        "v_mfma_f32_4x4x1f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x1f32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            66,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x2bf16 extends InstructionDef(
        "v_mfma_f32_4x4x2bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            107,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            107,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x416bBf16 extends InstructionDef(
        "v_mfma_f32_4x4x4_16b_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x416bBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            95,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            95,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x416bF16 extends InstructionDef(
        "v_mfma_f32_4x4x4_16b_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x416bF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x4bf161k extends InstructionDef(
        "v_mfma_f32_4x4x4bf16_1k",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            101,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF324x4x4f16 extends InstructionDef(
        "v_mfma_f32_4x4x4f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x4f16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            74,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF6416x16x4F64_1 extends InstructionDef(
        "v_mfma_f64_16x16x4_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF6416x16x4F64_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            110,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            110,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF6416x16x4f64_2 extends InstructionDef(
        "v_mfma_f64_16x16x4f64",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF6416x16x4f64_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            110,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF644x4x44bF64 extends InstructionDef(
        "v_mfma_f64_4x4x4_4b_f64",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF644x4x44bF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            111,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            111,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaF644x4x4f64 extends InstructionDef(
        "v_mfma_f64_4x4x4f64",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF644x4x4f64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            111,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 64, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3216x16x16i8 extends InstructionDef(
        "v_mfma_i32_16x16x16i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x16i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            85,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3216x16x32I8 extends InstructionDef(
        "v_mfma_i32_16x16x32_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x32I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            87,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            87,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3216x16x44bI8 extends InstructionDef(
        "v_mfma_i32_16x16x4_4b_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x44bI8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3216x16x4i8 extends InstructionDef(
        "v_mfma_i32_16x16x4i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x4i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            81,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3216x16x64I8 extends InstructionDef(
        "v_mfma_i32_16x16x64_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x64I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            54,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3232x32x16I8 extends InstructionDef(
        "v_mfma_i32_32x32x16_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x16I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            86,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            86,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3232x32x32I8 extends InstructionDef(
        "v_mfma_i32_32x32x32_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x32I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            56,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3232x32x42bI8 extends InstructionDef(
        "v_mfma_i32_32x32x4_2b_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x42bI8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            80,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            80,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3232x32x4i8 extends InstructionDef(
        "v_mfma_i32_32x32x4i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x4i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            80,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            80,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 1024, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 1024, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI3232x32x8i8 extends InstructionDef(
        "v_mfma_i32_32x32x8i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x8i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            84,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            84,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI324x4x416bI8 extends InstructionDef(
        "v_mfma_i32_4x4x4_16b_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI324x4x416bI8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            82,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            82,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaI324x4x4i8 extends InstructionDef(
        "v_mfma_i32_4x4x4i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI324x4x4i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA1,
            "cdna1",
            "VOP3P_MFMA",
            82,
            Col(
              OperandInfo(Some("VDST"), "ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA2,
            "cdna2",
            "VOP3P_MFMA",
            82,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaScaleF3216x16x128F8f6f4 extends InstructionDef(
        "v_mfma_scale_f32_16x16x128_f8f6f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VMfmaScaleF3216x16x128F8f6f4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3PX2",
            45,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 128, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VMfmaScaleF3232x32x64F8f6f4 extends InstructionDef(
        "v_mfma_scale_f32_32x32x64_f8f6f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VMfmaScaleF3232x32x64F8f6f4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3PX2",
            46,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_ACCVGPR_OR_CONST", 512, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x128Bf8Bf8 extends InstructionDef(
        "v_smfmac_f32_16x16x128_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            59,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x128Bf8Fp8 extends InstructionDef(
        "v_smfmac_f32_16x16x128_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            60,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x128Fp8Bf8 extends InstructionDef(
        "v_smfmac_f32_16x16x128_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            61,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x128Fp8Fp8 extends InstructionDef(
        "v_smfmac_f32_16x16x128_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            67,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x32Bf16 extends InstructionDef(
        "v_smfmac_f32_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            102,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            102,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x32F16 extends InstructionDef(
        "v_smfmac_f32_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            98,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            98,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x64Bf16 extends InstructionDef(
        "v_smfmac_f32_16x16x64_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            57,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x64Bf8Bf8 extends InstructionDef(
        "v_smfmac_f32_16x16x64_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            120,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            120,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x64Bf8Fp8 extends InstructionDef(
        "v_smfmac_f32_16x16x64_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            121,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            121,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x64F16 extends InstructionDef(
        "v_smfmac_f32_16x16x64_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            90,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x64Fp8Bf8 extends InstructionDef(
        "v_smfmac_f32_16x16x64_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            122,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            122,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3216x16x64Fp8Fp8 extends InstructionDef(
        "v_smfmac_f32_16x16x64_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            123,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            123,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x16Bf16 extends InstructionDef(
        "v_smfmac_f32_32x32x16_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x16F16 extends InstructionDef(
        "v_smfmac_f32_32x32x16_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            100,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            100,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x32Bf16 extends InstructionDef(
        "v_smfmac_f32_32x32x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x32Bf8Bf8 extends InstructionDef(
        "v_smfmac_f32_32x32x32_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            124,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            124,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x32Bf8Fp8 extends InstructionDef(
        "v_smfmac_f32_32x32x32_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            125,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            125,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x32F16 extends InstructionDef(
        "v_smfmac_f32_32x32x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            91,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x32Fp8Bf8 extends InstructionDef(
        "v_smfmac_f32_32x32x32_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            126,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            126,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x32Fp8Fp8 extends InstructionDef(
        "v_smfmac_f32_32x32x32_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            127,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            127,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x64Bf8Bf8 extends InstructionDef(
        "v_smfmac_f32_32x32x64_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            75,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x64Bf8Fp8 extends InstructionDef(
        "v_smfmac_f32_32x32x64_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            78,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x64Fp8Bf8 extends InstructionDef(
        "v_smfmac_f32_32x32x64_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            79,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacF3232x32x64Fp8Fp8 extends InstructionDef(
        "v_smfmac_f32_32x32x64_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            83,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacI3216x16x128I8 extends InstructionDef(
        "v_smfmac_i32_16x16x128_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacI3216x16x128I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            58,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacI3216x16x64I8 extends InstructionDef(
        "v_smfmac_i32_16x16x64_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacI3216x16x64I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            106,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            106,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacI3232x32x32I8 extends InstructionDef(
        "v_smfmac_i32_32x32x32_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacI3232x32x32I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA3,
            "cdna3",
            "VOP3P_MFMA",
            108,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            108,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSmfmacI3232x32x64I8 extends InstructionDef(
        "v_smfmac_i32_32x32x64_i8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacI3232x32x64I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA4,
            "cdna4",
            "VOP3P_MFMA",
            71,
            Col(
              OperandInfo(Some("VDST"), "VGPR_OR_ACCVGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR_OR_ACCVGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR_OR_ACCVGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacBf1616x16x32Bf16 extends InstructionDef(
        "v_swmmac_bf16_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacBf1616x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            83,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacBf1616x16x64Bf16 extends InstructionDef(
        "v_swmmac_bf16_16x16x64_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacBf1616x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            104,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacBf16f3216x16x64Bf16 extends InstructionDef(
        "v_swmmac_bf16f32_16x16x64_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacBf16f3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            105,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x128Bf8Bf8 extends InstructionDef(
        "v_swmmac_f16_16x16x128_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            122,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x128Bf8Fp8 extends InstructionDef(
        "v_swmmac_f16_16x16x128_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            121,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x128Fp8Bf8 extends InstructionDef(
        "v_swmmac_f16_16x16x128_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            120,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x128Fp8Fp8 extends InstructionDef(
        "v_swmmac_f16_16x16x128_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            119,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x32F16 extends InstructionDef(
        "v_swmmac_f16_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            82,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF1616x16x64F16 extends InstructionDef(
        "v_swmmac_f16_16x16x64_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            103,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x128Bf8Bf8 extends InstructionDef(
        "v_swmmac_f32_16x16x128_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            118,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x128Bf8Fp8 extends InstructionDef(
        "v_swmmac_f32_16x16x128_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            117,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x128Fp8Bf8 extends InstructionDef(
        "v_swmmac_f32_16x16x128_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            116,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x128Fp8Fp8 extends InstructionDef(
        "v_swmmac_f32_16x16x128_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            115,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32Bf16 extends InstructionDef(
        "v_swmmac_f32_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            81,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32Bf8Bf8 extends InstructionDef(
        "v_swmmac_f32_16x16x32_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            90,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32Bf8Fp8 extends InstructionDef(
        "v_swmmac_f32_16x16x32_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            89,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32F16 extends InstructionDef(
        "v_swmmac_f32_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            80,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32Fp8Bf8 extends InstructionDef(
        "v_swmmac_f32_16x16x32_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            88,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x32Fp8Fp8 extends InstructionDef(
        "v_swmmac_f32_16x16x32_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            87,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x64Bf16 extends InstructionDef(
        "v_swmmac_f32_16x16x64_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            102,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacF3216x16x64F16 extends InstructionDef(
        "v_swmmac_f32_16x16x64_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            101,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacI3216x16x128Iu8 extends InstructionDef(
        "v_swmmac_i32_16x16x128_iu8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x128Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            123,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacI3216x16x32Iu4 extends InstructionDef(
        "v_swmmac_i32_16x16x32_iu4",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x32Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            85,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacI3216x16x32Iu8 extends InstructionDef(
        "v_swmmac_i32_16x16x32_iu8",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x32Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            84,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VSwmmacI3216x16x64Iu4 extends InstructionDef(
        "v_swmmac_i32_16x16x64_iu4",
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x64Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            86,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaBf1616x16x16Bf16 extends InstructionDef(
        "v_wmma_bf16_16x16x16_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaBf1616x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3P",
            67,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3P",
            67,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            67,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaBf1616x16x32Bf16 extends InstructionDef(
        "v_wmma_bf16_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaBf1616x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            99,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaBf16f3216x16x32Bf16 extends InstructionDef(
        "v_wmma_bf16f32_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaBf16f3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            100,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x128Bf8Bf8 extends InstructionDef(
        "v_wmma_f16_16x16x128_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            135,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x128Bf8Fp8 extends InstructionDef(
        "v_wmma_f16_16x16x128_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            134,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x128Fp8Bf8 extends InstructionDef(
        "v_wmma_f16_16x16x128_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            133,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x128Fp8Fp8 extends InstructionDef(
        "v_wmma_f16_16x16x128_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            132,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x16F16 extends InstructionDef(
        "v_wmma_f16_16x16x16_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3P",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3P",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            66,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x32F16 extends InstructionDef(
        "v_wmma_f16_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            97,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x64Bf8Bf8 extends InstructionDef(
        "v_wmma_f16_16x16x64_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            113,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x64Bf8Fp8 extends InstructionDef(
        "v_wmma_f16_16x16x64_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            112,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x64Fp8Bf8 extends InstructionDef(
        "v_wmma_f16_16x16x64_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            111,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF1616x16x64Fp8Fp8 extends InstructionDef(
        "v_wmma_f16_16x16x64_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            110,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 128, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 128, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x128Bf8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x128_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            131,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x128Bf8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x128_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            130,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x128F8f6f4 extends InstructionDef(
        "v_wmma_f32_16x16x128_f8f6f4",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x128Fp8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x128_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            129,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x128Fp8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x128_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            128,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16Bf16 extends InstructionDef(
        "v_wmma_f32_16x16x16_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3P",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3P",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            65,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16Bf8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x16_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            73,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16Bf8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x16_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            72,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16F16 extends InstructionDef(
        "v_wmma_f32_16x16x16_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3P",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3P",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            64,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16Fp8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x16_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            71,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x16Fp8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x16_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            70,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x32Bf16 extends InstructionDef(
        "v_wmma_f32_16x16x32_bf16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            98,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x32F16 extends InstructionDef(
        "v_wmma_f32_16x16x32_f16",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            96,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x4F32 extends InstructionDef(
        "v_wmma_f32_16x16x4_f32",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x4F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            93,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x64Bf8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x64_bf8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            109,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x64Bf8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x64_bf8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            108,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x64Fp8Bf8 extends InstructionDef(
        "v_wmma_f32_16x16x64_fp8_bf8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            107,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3216x16x64Fp8Fp8 extends InstructionDef(
        "v_wmma_f32_16x16x64_fp8_fp8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            106,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaF3232x16x128F4 extends InstructionDef(
        "v_wmma_f32_32x16x128_f4",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3232x16x128F4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            136,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 512, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaI3216x16x16Iu4 extends InstructionDef(
        "v_wmma_i32_16x16x16_iu4",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x16Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3P",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3P",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            69,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 32, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaI3216x16x16Iu8 extends InstructionDef(
        "v_wmma_i32_16x16x16_iu8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x16Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX11,
            "rdna3",
            "VOP3P",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX11,
            "rdna3_5",
            "VOP3P",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 128, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          ),
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            68,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaI3216x16x32Iu4 extends InstructionDef(
        "v_wmma_i32_16x16x32_iu4",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x32Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.GFX12,
            "rdna4",
            "VOP3P",
            74,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 64, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaI3216x16x64Iu8 extends InstructionDef(
        "v_wmma_i32_16x16x64_iu8",
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x64Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3P",
            114,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaScale16F3216x16x128F8f6f4 extends InstructionDef(
        "v_wmma_scale16_f32_16x16x128_f8f6f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScale16F3216x16x128F8f6f4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3PX3",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaScale16F3232x16x128F4 extends InstructionDef(
        "v_wmma_scale16_f32_32x16x128_f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScale16F3232x16x128F4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3PX3",
            136,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 512, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 64, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 64, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaScaleF3216x16x128F8f6f4 extends InstructionDef(
        "v_wmma_scale_f32_16x16x128_f8f6f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScaleF3216x16x128F8f6f4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3PX2",
            51,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 256, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 256, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )
  case object VWmmaScaleF3232x16x128F4 extends InstructionDef(
        "v_wmma_scale_f32_32x16x128_f4",
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScaleF3232x16x128F4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        Metadata(Col(
          Encoding(
            Family.CDNA5,
            "cdna5",
            "VOP3PX2",
            136,
            Col(
              OperandInfo(Some("VDST"), "VGPR", 512, false, true, false),
              OperandInfo(Some("SRC0"), "SRC_VGPR", 512, true, false, false),
              OperandInfo(Some("SRC1"), "SRC_VGPR", 256, true, false, false),
              OperandInfo(Some("SRC2"), "SRC_VGPR_OR_INLINE", 512, true, false, false),
              OperandInfo(Some("SCALE_SRC0"), "SRC_SIMPLE", 32, true, false, false),
              OperandInfo(Some("SCALE_SRC1"), "SRC_SIMPLE", 32, true, false, false)
            ),
            Some("VALU"),
            MetadataSource.AMDXML
          )
        ))
      )

  val definitions: Col[InstructionDef] = Col(
    SWaitTensorcnt,
    TensorLoadToLds,
    TensorStoreFromLds,
    VMfmaF3216x16x128F8f6f4,
    VMfmaF3216x16x16Bf16,
    VMfmaF3216x16x16F16_1,
    VMfmaF3216x16x16bf161k,
    VMfmaF3216x16x16f16_2,
    VMfmaF3216x16x14bF32,
    VMfmaF3216x16x1f32,
    VMfmaF3216x16x2bf16,
    VMfmaF3216x16x32Bf16,
    VMfmaF3216x16x32Bf8Bf8,
    VMfmaF3216x16x32Bf8Fp8,
    VMfmaF3216x16x32F16,
    VMfmaF3216x16x32Fp8Bf8,
    VMfmaF3216x16x32Fp8Fp8,
    VMfmaF3216x16x44bBf16,
    VMfmaF3216x16x44bF16,
    VMfmaF3216x16x4F32_1,
    VMfmaF3216x16x4bf161k,
    VMfmaF3216x16x4f16,
    VMfmaF3216x16x4f32_2,
    VMfmaF3216x16x8Xf32,
    VMfmaF3216x16x8bf16,
    VMfmaF3232x32x16Bf16,
    VMfmaF3232x32x16Bf8Bf8,
    VMfmaF3232x32x16Bf8Fp8,
    VMfmaF3232x32x16F16,
    VMfmaF3232x32x16Fp8Bf8,
    VMfmaF3232x32x16Fp8Fp8,
    VMfmaF3232x32x12bF32,
    VMfmaF3232x32x1f32,
    VMfmaF3232x32x2F32_1,
    VMfmaF3232x32x2bf16,
    VMfmaF3232x32x2f32_2,
    VMfmaF3232x32x42bBf16,
    VMfmaF3232x32x42bF16,
    VMfmaF3232x32x4Xf32,
    VMfmaF3232x32x4bf16,
    VMfmaF3232x32x4bf161k,
    VMfmaF3232x32x4f16,
    VMfmaF3232x32x64F8f6f4,
    VMfmaF3232x32x8Bf16,
    VMfmaF3232x32x8F16_1,
    VMfmaF3232x32x8bf161k,
    VMfmaF3232x32x8f16_2,
    VMfmaF324x4x116bF32,
    VMfmaF324x4x1f32,
    VMfmaF324x4x2bf16,
    VMfmaF324x4x416bBf16,
    VMfmaF324x4x416bF16,
    VMfmaF324x4x4bf161k,
    VMfmaF324x4x4f16,
    VMfmaF6416x16x4F64_1,
    VMfmaF6416x16x4f64_2,
    VMfmaF644x4x44bF64,
    VMfmaF644x4x4f64,
    VMfmaI3216x16x16i8,
    VMfmaI3216x16x32I8,
    VMfmaI3216x16x44bI8,
    VMfmaI3216x16x4i8,
    VMfmaI3216x16x64I8,
    VMfmaI3232x32x16I8,
    VMfmaI3232x32x32I8,
    VMfmaI3232x32x42bI8,
    VMfmaI3232x32x4i8,
    VMfmaI3232x32x8i8,
    VMfmaI324x4x416bI8,
    VMfmaI324x4x4i8,
    VMfmaScaleF3216x16x128F8f6f4,
    VMfmaScaleF3232x32x64F8f6f4,
    VSmfmacF3216x16x128Bf8Bf8,
    VSmfmacF3216x16x128Bf8Fp8,
    VSmfmacF3216x16x128Fp8Bf8,
    VSmfmacF3216x16x128Fp8Fp8,
    VSmfmacF3216x16x32Bf16,
    VSmfmacF3216x16x32F16,
    VSmfmacF3216x16x64Bf16,
    VSmfmacF3216x16x64Bf8Bf8,
    VSmfmacF3216x16x64Bf8Fp8,
    VSmfmacF3216x16x64F16,
    VSmfmacF3216x16x64Fp8Bf8,
    VSmfmacF3216x16x64Fp8Fp8,
    VSmfmacF3232x32x16Bf16,
    VSmfmacF3232x32x16F16,
    VSmfmacF3232x32x32Bf16,
    VSmfmacF3232x32x32Bf8Bf8,
    VSmfmacF3232x32x32Bf8Fp8,
    VSmfmacF3232x32x32F16,
    VSmfmacF3232x32x32Fp8Bf8,
    VSmfmacF3232x32x32Fp8Fp8,
    VSmfmacF3232x32x64Bf8Bf8,
    VSmfmacF3232x32x64Bf8Fp8,
    VSmfmacF3232x32x64Fp8Bf8,
    VSmfmacF3232x32x64Fp8Fp8,
    VSmfmacI3216x16x128I8,
    VSmfmacI3216x16x64I8,
    VSmfmacI3232x32x32I8,
    VSmfmacI3232x32x64I8,
    VSwmmacBf1616x16x32Bf16,
    VSwmmacBf1616x16x64Bf16,
    VSwmmacBf16f3216x16x64Bf16,
    VSwmmacF1616x16x128Bf8Bf8,
    VSwmmacF1616x16x128Bf8Fp8,
    VSwmmacF1616x16x128Fp8Bf8,
    VSwmmacF1616x16x128Fp8Fp8,
    VSwmmacF1616x16x32F16,
    VSwmmacF1616x16x64F16,
    VSwmmacF3216x16x128Bf8Bf8,
    VSwmmacF3216x16x128Bf8Fp8,
    VSwmmacF3216x16x128Fp8Bf8,
    VSwmmacF3216x16x128Fp8Fp8,
    VSwmmacF3216x16x32Bf16,
    VSwmmacF3216x16x32Bf8Bf8,
    VSwmmacF3216x16x32Bf8Fp8,
    VSwmmacF3216x16x32F16,
    VSwmmacF3216x16x32Fp8Bf8,
    VSwmmacF3216x16x32Fp8Fp8,
    VSwmmacF3216x16x64Bf16,
    VSwmmacF3216x16x64F16,
    VSwmmacI3216x16x128Iu8,
    VSwmmacI3216x16x32Iu4,
    VSwmmacI3216x16x32Iu8,
    VSwmmacI3216x16x64Iu4,
    VWmmaBf1616x16x16Bf16,
    VWmmaBf1616x16x32Bf16,
    VWmmaBf16f3216x16x32Bf16,
    VWmmaF1616x16x128Bf8Bf8,
    VWmmaF1616x16x128Bf8Fp8,
    VWmmaF1616x16x128Fp8Bf8,
    VWmmaF1616x16x128Fp8Fp8,
    VWmmaF1616x16x16F16,
    VWmmaF1616x16x32F16,
    VWmmaF1616x16x64Bf8Bf8,
    VWmmaF1616x16x64Bf8Fp8,
    VWmmaF1616x16x64Fp8Bf8,
    VWmmaF1616x16x64Fp8Fp8,
    VWmmaF3216x16x128Bf8Bf8,
    VWmmaF3216x16x128Bf8Fp8,
    VWmmaF3216x16x128F8f6f4,
    VWmmaF3216x16x128Fp8Bf8,
    VWmmaF3216x16x128Fp8Fp8,
    VWmmaF3216x16x16Bf16,
    VWmmaF3216x16x16Bf8Bf8,
    VWmmaF3216x16x16Bf8Fp8,
    VWmmaF3216x16x16F16,
    VWmmaF3216x16x16Fp8Bf8,
    VWmmaF3216x16x16Fp8Fp8,
    VWmmaF3216x16x32Bf16,
    VWmmaF3216x16x32F16,
    VWmmaF3216x16x4F32,
    VWmmaF3216x16x64Bf8Bf8,
    VWmmaF3216x16x64Bf8Fp8,
    VWmmaF3216x16x64Fp8Bf8,
    VWmmaF3216x16x64Fp8Fp8,
    VWmmaF3232x16x128F4,
    VWmmaI3216x16x16Iu4,
    VWmmaI3216x16x16Iu8,
    VWmmaI3216x16x32Iu4,
    VWmmaI3216x16x64Iu8,
    VWmmaScale16F3216x16x128F8f6f4,
    VWmmaScale16F3232x16x128F4,
    VWmmaScaleF3216x16x128F8f6f4,
    VWmmaScaleF3232x16x128F4
  )
}
