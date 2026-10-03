// Generated PTX instruction model from the pinned NVIDIA ISA 9.3 specification. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.ptx.v9_3

import Metadata.*

import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object Instruction {
  enum DecodeError {
    case UnknownOpcode(opcode: String)
    case InvalidOperandCount(opcode: String, expected: Vec[Int], actual: Int)
    case AmbiguousInstruction(opcode: String, definitions: Vec[String])
  }

  type Decoder = (
      operands: Vec[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) => Either[DecodeError, Instruction]

  private def metadataAtom_143_0(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.Add,
        Modifier.And,
        Modifier.Dec,
        Modifier.Inc,
        Modifier.Max,
        Modifier.Min,
        Modifier.Or,
        Modifier.Xor
      ),
      ModifierGroup(
        Modifier.B32,
        Modifier.B64,
        Modifier.F32,
        Modifier.F64,
        Modifier.S32,
        Modifier.S64,
        Modifier.U32,
        Modifier.U64
      )
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.AcqRel,
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Sys
    ),
    false
  )
  private def metadataAtom_143_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Cas), ModifierGroup(Modifier.B128, Modifier.B16, Modifier.B32, Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.AcqRel,
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Sys
    ),
    false
  )
  private def metadataAtom_143_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Exch), ModifierGroup(Modifier.B128, Modifier.B32, Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.AcqRel,
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Sys
    ),
    false
  )
  private def metadataAtom_143_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.F16)),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.AcqRel,
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Sys
    ),
    false
  )
  private def metadataAtom_143_4(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.F16x2)),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.AcqRel,
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Sys
    ),
    false
  )
  private def metadataAtom_143_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.Bf16)),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.AcqRel,
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Sys
    ),
    false
  )
  private def metadataAtom_143_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.Bf16x2)),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.AcqRel,
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Sys
    ),
    false
  )
  private def metadataAtom_143_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.V2, Modifier.V4), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.AcqRel,
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Sys
    ),
    false
  )
  private def metadataAtom_143_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min),
      ModifierGroup(Modifier.Noftz),
      ModifierGroup(Modifier.V2, Modifier.V4, Modifier.V8),
      ModifierGroup(Modifier.Bf16, Modifier.F16)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.AcqRel,
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Sys
    ),
    false
  )
  private def metadataAtom_143_9(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min),
      ModifierGroup(Modifier.Noftz),
      ModifierGroup(Modifier.V2, Modifier.V4),
      ModifierGroup(Modifier.Bf16x2, Modifier.F16x2)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.AcqRel,
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Sys
    ),
    false
  )
  private def metadataCvt_100_0(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.Bf16,
        Modifier.F16,
        Modifier.F32,
        Modifier.F64,
        Modifier.S16,
        Modifier.S32,
        Modifier.S64,
        Modifier.S8,
        Modifier.U16,
        Modifier.U32,
        Modifier.U64,
        Modifier.U8
      ),
      ModifierGroup(
        Modifier.Bf16,
        Modifier.F16,
        Modifier.F32,
        Modifier.F64,
        Modifier.S16,
        Modifier.S32,
        Modifier.S64,
        Modifier.S8,
        Modifier.U16,
        Modifier.U32,
        Modifier.U64,
        Modifier.U8
      )
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.Ftz,
      Modifier.Rmi,
      Modifier.Rni,
      Modifier.Rpi,
      Modifier.Rzi,
      Modifier.Sat
    ),
    false
  )
  private def metadataCvt_100_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.Bf16,
        Modifier.F16,
        Modifier.F32,
        Modifier.F64,
        Modifier.S16,
        Modifier.S32,
        Modifier.S64,
        Modifier.S8,
        Modifier.U16,
        Modifier.U32,
        Modifier.U64,
        Modifier.U8
      ),
      ModifierGroup(
        Modifier.Bf16,
        Modifier.F16,
        Modifier.F32,
        Modifier.F64,
        Modifier.S16,
        Modifier.S32,
        Modifier.S64,
        Modifier.S8,
        Modifier.U16,
        Modifier.U32,
        Modifier.U64,
        Modifier.U8
      )
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.Ftz,
      Modifier.Rm,
      Modifier.Rn,
      Modifier.Rp,
      Modifier.Rz,
      Modifier.Sat
    ),
    false
  )
  private def metadataCvt_100_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn, Modifier.Rz), ModifierGroup(Modifier.F16), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    false
  )
  private def metadataCvt_100_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn, Modifier.Rz), ModifierGroup(Modifier.F16x2), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    false
  )
  private def metadataCvt_100_4(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rs), ModifierGroup(Modifier.F16x2), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    false
  )
  private def metadataCvt_100_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn, Modifier.Rz), ModifierGroup(Modifier.Bf16), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    false
  )
  private def metadataCvt_100_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn, Modifier.Rz), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    false
  )
  private def metadataCvt_100_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rs), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    false
  )
  private def metadataCvt_100_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rna), ModifierGroup(Modifier.Tf32), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    false
  )
  private def metadataCvt_100_9(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn, Modifier.Rz), ModifierGroup(Modifier.Tf32), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    false
  )
  private def metadataCvt_100_10(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E4m3x2, Modifier.E5m2x2),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E4m3x2, Modifier.E5m2x2),
      ModifierGroup(Modifier.Fp16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_12(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.F16x2), ModifierGroup(Modifier.E4m3x2, Modifier.E5m2x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_13(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.E4m3x2, Modifier.E5m2x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    false
  )
  private def metadataCvt_100_14(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Rs),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E4m3x4, Modifier.E5m2x4),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_15(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m1x2),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_16(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m1x2),
      ModifierGroup(Modifier.Bf16x2, Modifier.F16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_17(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.F16x2), ModifierGroup(Modifier.E2m1x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_18(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.E2m1x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    false
  )
  private def metadataCvt_100_19(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Rs),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m1x4),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_20(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m3x2, Modifier.E3m2x2),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_21(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m3x2, Modifier.E3m2x2),
      ModifierGroup(Modifier.Bf16x2, Modifier.F16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_22(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.F16x2), ModifierGroup(Modifier.E2m3x2, Modifier.E3m2x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_23(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.E2m3x2, Modifier.E3m2x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    false
  )
  private def metadataCvt_100_24(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Rs),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m3x4, Modifier.E3m2x4),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    false
  )
  private def metadataCvt_100_25(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.Ue8m0x2), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    false
  )
  private def metadataCvt_100_26(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.Ue8m0x2), ModifierGroup(Modifier.Bf16x2)),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    false
  )
  private def metadataCvt_100_27(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.Ue8m0x2)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataCvt_100_28(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.S2f6x2),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.ScaledN2Ue8m0),
    false
  )
  private def metadataCvt_100_29(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.S2f6x2),
      ModifierGroup(Modifier.Bf16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.ScaledN2Ue8m0),
    false
  )
  private def metadataCvt_100_30(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.S2f6x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    false
  )
  private def metadataMma_170_0(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M8n8k4),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16, Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k8),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16, Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k16),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16, Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k4),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_4(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k8),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Bf16, Modifier.Tf32),
      ModifierGroup(Modifier.Bf16, Modifier.Tf32),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k16),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k16, Modifier.M16n8k32),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.F16, Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k32),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindF8f6f4),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.E2m1, Modifier.E2m3, Modifier.E3m2, Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E2m1, Modifier.E2m3, Modifier.E3m2, Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.F16, Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k64),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Ue8m0)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k64),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Ue8m0)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_10(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k32),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E2m1, Modifier.E2m3, Modifier.E3m2, Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E2m1, Modifier.E2m3, Modifier.E3m2, Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Ue8m0)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k16, Modifier.M16n8k4, Modifier.M16n8k8, Modifier.M8n84),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F64),
      ModifierGroup(Modifier.F64),
      ModifierGroup(Modifier.F64),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMma_170_12(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k32, Modifier.M16n8k64, Modifier.M8n8k32),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.S4, Modifier.U4),
      ModifierGroup(Modifier.S4, Modifier.U4),
      ModifierGroup(Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    false
  )
  private def metadataMma_170_13(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k128, Modifier.M16n8k256, Modifier.M8n8k128),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.B1),
      ModifierGroup(Modifier.B1),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.And, Modifier.Xor),
      ModifierGroup(Modifier.Popc)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_0(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k16),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16, Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_1(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k32),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16, Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_2(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k16),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k32),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_4(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k8),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_5(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k16),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_6(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k64),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k64),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindF8f6f4),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.E2m1, Modifier.E2m3, Modifier.E3m2, Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E2m1, Modifier.E2m3, Modifier.E3m2, Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.F16, Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k128),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Ue8m0)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k128),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Ue8m0)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_10(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k64),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E2m1, Modifier.E2m3, Modifier.E3m2, Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E2m1, Modifier.E2m3, Modifier.E3m2, Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Ue8m0)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataMmaSpOrderedMetadata_174_11(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k128, Modifier.M16n8k64),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.S4, Modifier.U4),
      ModifierGroup(Modifier.S4, Modifier.U4),
      ModifierGroup(Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    false
  )
  private def metadataSet_70_0(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.F16),
      ModifierGroup(
        Modifier.B16,
        Modifier.B32,
        Modifier.B64,
        Modifier.F16,
        Modifier.F32,
        Modifier.F64,
        Modifier.S16,
        Modifier.S32,
        Modifier.S64,
        Modifier.U16,
        Modifier.U32,
        Modifier.U64
      )
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ftz),
    false
  )
  private def metadataSet_70_1(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Unknown("unparsed")
    ),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
      ModifierGroup(Modifier.F16),
      ModifierGroup(
        Modifier.B16,
        Modifier.B32,
        Modifier.B64,
        Modifier.F16,
        Modifier.F32,
        Modifier.F64,
        Modifier.S16,
        Modifier.S32,
        Modifier.S64,
        Modifier.U16,
        Modifier.U32,
        Modifier.U64
      )
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ftz),
    false
  )
  private def metadataSet_70_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(
        Modifier.B16,
        Modifier.B32,
        Modifier.B64,
        Modifier.F16,
        Modifier.F32,
        Modifier.F64,
        Modifier.S16,
        Modifier.S32,
        Modifier.S64,
        Modifier.U16,
        Modifier.U32,
        Modifier.U64
      )
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataSet_70_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Unknown("unparsed")
    ),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(
        Modifier.B16,
        Modifier.B32,
        Modifier.B64,
        Modifier.F16,
        Modifier.F32,
        Modifier.F64,
        Modifier.S16,
        Modifier.S32,
        Modifier.S64,
        Modifier.U16,
        Modifier.U32,
        Modifier.U64
      )
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataSet_70_4(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.Bf16x2, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F16)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ftz),
    false
  )
  private def metadataSet_70_5(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Unknown("unparsed")
    ),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
      ModifierGroup(Modifier.Bf16x2, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F16)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ftz),
    false
  )
  private def metadataSet_70_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.Bf16x2, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataSet_70_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Unknown("unparsed")
    ),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
      ModifierGroup(Modifier.Bf16x2, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataSet_70_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.Bf16x2, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ftz),
    false
  )
  private def metadataSet_70_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Unknown("unparsed")
    ),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
      ModifierGroup(Modifier.Bf16x2, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ftz),
    false
  )
  private def metadataSet_70_10(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.Bf16x2, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.Bf16x2)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataSet_70_11(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Unknown("unparsed")
    ),
    Vec(
      ModifierGroup(
        Modifier.Eq,
        Modifier.Equ,
        Modifier.Ge,
        Modifier.Geu,
        Modifier.Gt,
        Modifier.Gtu,
        Modifier.Le,
        Modifier.Leu,
        Modifier.Lt,
        Modifier.Ltu,
        Modifier.Nan,
        Modifier.Ne,
        Modifier.Neu,
        Modifier.Num
      ),
      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
      ModifierGroup(Modifier.Bf16x2, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.Bf16x2)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05Mma_186_0(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05Mma_186_1(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05Mma_186_2(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Block16, Modifier.Block32),
    false
  )
  private def metadataTcgen05Mma_186_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Block16, Modifier.Block32),
    false
  )
  private def metadataTcgen05Mma_186_4(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05Mma_186_5(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ashift),
    false
  )
  private def metadataTcgen05Mma_186_6(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4), ModifierGroup(Modifier.Ashift)),
    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
    false
  )
  private def metadataTcgen05Mma_186_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Block16, Modifier.Block32),
    false
  )
  private def metadataTcgen05Mma_186_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Block16, Modifier.Block32),
    false
  )
  private def metadataTcgen05Mma_186_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindI8)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05Mma_186_10(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindI8)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05Mma_186_11(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindI8), ModifierGroup(Modifier.CollectorBufferOp)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05Mma_186_12(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindI8), ModifierGroup(Modifier.Ashift)),
    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
    false
  )
  private def metadataTcgen05Mma_186_13(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindI8), ModifierGroup(Modifier.CollectorBufferOp)),
    _root_.scala.collection.immutable.Set(Modifier.Ashift),
    false
  )
  private def metadataTcgen05MmaSp_187_0(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05MmaSp_187_1(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05MmaSp_187_2(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Block16, Modifier.Block32),
    false
  )
  private def metadataTcgen05MmaSp_187_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Block16, Modifier.Block32),
    false
  )
  private def metadataTcgen05MmaSp_187_4(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05MmaSp_187_5(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4), ModifierGroup(Modifier.Ashift)),
    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
    false
  )
  private def metadataTcgen05MmaSp_187_6(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ashift),
    false
  )
  private def metadataTcgen05MmaSp_187_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Block16, Modifier.Block32),
    false
  )
  private def metadataTcgen05MmaSp_187_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Block16, Modifier.Block32),
    false
  )
  private def metadataTcgen05MmaSp_187_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindI8)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05MmaSp_187_10(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindI8)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05MmaSp_187_11(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindI8), ModifierGroup(Modifier.CollectorBufferOp)),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTcgen05MmaSp_187_12(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindI8), ModifierGroup(Modifier.Ashift)),
    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
    false
  )
  private def metadataTcgen05MmaSp_187_13(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.KindI8), ModifierGroup(Modifier.CollectorBufferOp)),
    _root_.scala.collection.immutable.Set(Modifier.Ashift),
    false
  )
  private def metadataTex_124_0(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A1d, Modifier.A2d, Modifier.A2dms, Modifier.Acube, Modifier.Cube),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTex_124_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A1d, Modifier.A2d, Modifier.A2dms, Modifier.Acube, Modifier.Cube),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTex_124_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A1d, Modifier.A2d, Modifier.A2dms, Modifier.Acube, Modifier.Cube),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTex_124_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A1d, Modifier.A2d, Modifier.A2dms, Modifier.Acube, Modifier.Cube),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTex_124_4(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Base),
      ModifierGroup(Modifier.A1d, Modifier.A2d, Modifier.A2dms, Modifier.Acube, Modifier.Cube),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTex_124_5(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Level),
      ModifierGroup(Modifier.A1d, Modifier.A2d, Modifier.A2dms, Modifier.Acube, Modifier.Cube),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTex_124_6(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Grad),
      ModifierGroup(Modifier.A1d, Modifier.A2d, Modifier.A2dms, Modifier.Acube, Modifier.Cube),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTex_124_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Base),
      ModifierGroup(Modifier.A1d, Modifier.A2d, Modifier.A2dms, Modifier.Acube, Modifier.Cube),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTex_124_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Level),
      ModifierGroup(Modifier.A1d, Modifier.A2d, Modifier.A2dms, Modifier.Acube, Modifier.Cube),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataTex_124_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Grad),
      ModifierGroup(Modifier.A1d, Modifier.A2d, Modifier.A2dms, Modifier.Acube, Modifier.Cube),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataWgmmaMmaAsync_175_0(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k16,
        Modifier.M64n112k16,
        Modifier.M64n120k16,
        Modifier.M64n128k16,
        Modifier.M64n136k16,
        Modifier.M64n144k16,
        Modifier.M64n152k16,
        Modifier.M64n160k16,
        Modifier.M64n168k16,
        Modifier.M64n16k16,
        Modifier.M64n176k16,
        Modifier.M64n184k16,
        Modifier.M64n192k16,
        Modifier.M64n200k16,
        Modifier.M64n208k16,
        Modifier.M64n216k16,
        Modifier.M64n224k16,
        Modifier.M64n232k16,
        Modifier.M64n240k16,
        Modifier.M64n248k16,
        Modifier.M64n24k16,
        Modifier.M64n256k16,
        Modifier.M64n32k16,
        Modifier.M64n40k16,
        Modifier.M64n48k16,
        Modifier.M64n56k16,
        Modifier.M64n64k16,
        Modifier.M64n72k16,
        Modifier.M64n80k16,
        Modifier.M64n88k16,
        Modifier.M64n8k16,
        Modifier.M64n96k16
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_1(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k16,
        Modifier.M64n112k16,
        Modifier.M64n120k16,
        Modifier.M64n128k16,
        Modifier.M64n136k16,
        Modifier.M64n144k16,
        Modifier.M64n152k16,
        Modifier.M64n160k16,
        Modifier.M64n168k16,
        Modifier.M64n16k16,
        Modifier.M64n176k16,
        Modifier.M64n184k16,
        Modifier.M64n192k16,
        Modifier.M64n200k16,
        Modifier.M64n208k16,
        Modifier.M64n216k16,
        Modifier.M64n224k16,
        Modifier.M64n232k16,
        Modifier.M64n240k16,
        Modifier.M64n248k16,
        Modifier.M64n24k16,
        Modifier.M64n256k16,
        Modifier.M64n32k16,
        Modifier.M64n40k16,
        Modifier.M64n48k16,
        Modifier.M64n56k16,
        Modifier.M64n64k16,
        Modifier.M64n72k16,
        Modifier.M64n80k16,
        Modifier.M64n88k16,
        Modifier.M64n8k16,
        Modifier.M64n96k16
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_2(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k32,
        Modifier.M64n112k32,
        Modifier.M64n120k32,
        Modifier.M64n128k32,
        Modifier.M64n136k32,
        Modifier.M64n144k32,
        Modifier.M64n152k32,
        Modifier.M64n160k32,
        Modifier.M64n168k32,
        Modifier.M64n16k32,
        Modifier.M64n176k32,
        Modifier.M64n184k32,
        Modifier.M64n192k32,
        Modifier.M64n200k32,
        Modifier.M64n208k32,
        Modifier.M64n216k32,
        Modifier.M64n224k32,
        Modifier.M64n232k32,
        Modifier.M64n240k32,
        Modifier.M64n248k32,
        Modifier.M64n24k32,
        Modifier.M64n256k32,
        Modifier.M64n32k32,
        Modifier.M64n40k32,
        Modifier.M64n48k32,
        Modifier.M64n56k32,
        Modifier.M64n64k32,
        Modifier.M64n72k32,
        Modifier.M64n80k32,
        Modifier.M64n88k32,
        Modifier.M64n8k32,
        Modifier.M64n96k32
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k32,
        Modifier.M64n112k32,
        Modifier.M64n120k32,
        Modifier.M64n128k32,
        Modifier.M64n136k32,
        Modifier.M64n144k32,
        Modifier.M64n152k32,
        Modifier.M64n160k32,
        Modifier.M64n168k32,
        Modifier.M64n16k32,
        Modifier.M64n176k32,
        Modifier.M64n184k32,
        Modifier.M64n192k32,
        Modifier.M64n200k32,
        Modifier.M64n208k32,
        Modifier.M64n216k32,
        Modifier.M64n224k32,
        Modifier.M64n232k32,
        Modifier.M64n240k32,
        Modifier.M64n248k32,
        Modifier.M64n24k32,
        Modifier.M64n256k32,
        Modifier.M64n32k32,
        Modifier.M64n40k32,
        Modifier.M64n48k32,
        Modifier.M64n56k32,
        Modifier.M64n64k32,
        Modifier.M64n72k32,
        Modifier.M64n80k32,
        Modifier.M64n88k32,
        Modifier.M64n8k32,
        Modifier.M64n96k32
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_4(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k32,
        Modifier.M64n112k32,
        Modifier.M64n120k32,
        Modifier.M64n128k32,
        Modifier.M64n136k32,
        Modifier.M64n144k32,
        Modifier.M64n152k32,
        Modifier.M64n160k32,
        Modifier.M64n168k32,
        Modifier.M64n16k32,
        Modifier.M64n176k32,
        Modifier.M64n184k32,
        Modifier.M64n192k32,
        Modifier.M64n200k32,
        Modifier.M64n208k32,
        Modifier.M64n216k32,
        Modifier.M64n224k32,
        Modifier.M64n232k32,
        Modifier.M64n240k32,
        Modifier.M64n248k32,
        Modifier.M64n24k32,
        Modifier.M64n256k32,
        Modifier.M64n32k32,
        Modifier.M64n40k32,
        Modifier.M64n48k32,
        Modifier.M64n56k32,
        Modifier.M64n64k32,
        Modifier.M64n72k32,
        Modifier.M64n80k32,
        Modifier.M64n88k32,
        Modifier.M64n8k32,
        Modifier.M64n96k32
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_5(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k32,
        Modifier.M64n112k32,
        Modifier.M64n120k32,
        Modifier.M64n128k32,
        Modifier.M64n136k32,
        Modifier.M64n144k32,
        Modifier.M64n152k32,
        Modifier.M64n160k32,
        Modifier.M64n168k32,
        Modifier.M64n16k32,
        Modifier.M64n176k32,
        Modifier.M64n184k32,
        Modifier.M64n192k32,
        Modifier.M64n200k32,
        Modifier.M64n208k32,
        Modifier.M64n216k32,
        Modifier.M64n224k32,
        Modifier.M64n232k32,
        Modifier.M64n240k32,
        Modifier.M64n248k32,
        Modifier.M64n24k32,
        Modifier.M64n256k32,
        Modifier.M64n32k32,
        Modifier.M64n40k32,
        Modifier.M64n48k32,
        Modifier.M64n56k32,
        Modifier.M64n64k32,
        Modifier.M64n72k32,
        Modifier.M64n80k32,
        Modifier.M64n88k32,
        Modifier.M64n8k32,
        Modifier.M64n96k32
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_6(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k32,
        Modifier.M64n112k32,
        Modifier.M64n120k32,
        Modifier.M64n128k32,
        Modifier.M64n136k32,
        Modifier.M64n144k32,
        Modifier.M64n152k32,
        Modifier.M64n160k32,
        Modifier.M64n168k32,
        Modifier.M64n16k32,
        Modifier.M64n176k32,
        Modifier.M64n184k32,
        Modifier.M64n192k32,
        Modifier.M64n200k32,
        Modifier.M64n208k32,
        Modifier.M64n216k32,
        Modifier.M64n224k32,
        Modifier.M64n232k32,
        Modifier.M64n240k32,
        Modifier.M64n248k32,
        Modifier.M64n24k32,
        Modifier.M64n256k32,
        Modifier.M64n32k32,
        Modifier.M64n40k32,
        Modifier.M64n48k32,
        Modifier.M64n56k32,
        Modifier.M64n64k32,
        Modifier.M64n72k32,
        Modifier.M64n80k32,
        Modifier.M64n88k32,
        Modifier.M64n8k32,
        Modifier.M64n96k32
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k32,
        Modifier.M64n112k32,
        Modifier.M64n120k32,
        Modifier.M64n128k32,
        Modifier.M64n136k32,
        Modifier.M64n144k32,
        Modifier.M64n152k32,
        Modifier.M64n160k32,
        Modifier.M64n168k32,
        Modifier.M64n16k32,
        Modifier.M64n176k32,
        Modifier.M64n184k32,
        Modifier.M64n192k32,
        Modifier.M64n200k32,
        Modifier.M64n208k32,
        Modifier.M64n216k32,
        Modifier.M64n224k32,
        Modifier.M64n232k32,
        Modifier.M64n240k32,
        Modifier.M64n248k32,
        Modifier.M64n24k32,
        Modifier.M64n256k32,
        Modifier.M64n32k32,
        Modifier.M64n40k32,
        Modifier.M64n48k32,
        Modifier.M64n56k32,
        Modifier.M64n64k32,
        Modifier.M64n72k32,
        Modifier.M64n80k32,
        Modifier.M64n88k32,
        Modifier.M64n8k32,
        Modifier.M64n96k32
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n112k32,
        Modifier.M64n128k32,
        Modifier.M64n144k32,
        Modifier.M64n160k32,
        Modifier.M64n16k32,
        Modifier.M64n176k32,
        Modifier.M64n192k32,
        Modifier.M64n208k32,
        Modifier.M64n224k32,
        Modifier.M64n24k32,
        Modifier.M64n32k32,
        Modifier.M64n48k32,
        Modifier.M64n64k32,
        Modifier.M64n80k32,
        Modifier.M64n8k32,
        Modifier.M64n96k32
      ),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.S8, Modifier.U8),
      ModifierGroup(Modifier.S8, Modifier.U8)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    true
  )
  private def metadataWgmmaMmaAsync_175_9(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n112k32,
        Modifier.M64n128k32,
        Modifier.M64n144k32,
        Modifier.M64n160k32,
        Modifier.M64n16k32,
        Modifier.M64n176k32,
        Modifier.M64n192k32,
        Modifier.M64n208k32,
        Modifier.M64n224k32,
        Modifier.M64n24k32,
        Modifier.M64n32k32,
        Modifier.M64n48k32,
        Modifier.M64n64k32,
        Modifier.M64n80k32,
        Modifier.M64n8k32,
        Modifier.M64n96k32
      ),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.S8, Modifier.U8),
      ModifierGroup(Modifier.S8, Modifier.U8)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    true
  )
  private def metadataWgmmaMmaAsync_175_10(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n112k256,
        Modifier.M64n128k256,
        Modifier.M64n144k256,
        Modifier.M64n160k256,
        Modifier.M64n16k256,
        Modifier.M64n176k256,
        Modifier.M64n192k256,
        Modifier.M64n208k256,
        Modifier.M64n224k256,
        Modifier.M64n240k256,
        Modifier.M64n24k256,
        Modifier.M64n256k256,
        Modifier.M64n32k256,
        Modifier.M64n48k256,
        Modifier.M64n64k256,
        Modifier.M64n80k256,
        Modifier.M64n8k256,
        Modifier.M64n96k256
      ),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.B1),
      ModifierGroup(Modifier.B1),
      ModifierGroup(Modifier.And),
      ModifierGroup(Modifier.Popc)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n112k256,
        Modifier.M64n128k256,
        Modifier.M64n144k256,
        Modifier.M64n160k256,
        Modifier.M64n16k256,
        Modifier.M64n176k256,
        Modifier.M64n192k256,
        Modifier.M64n208k256,
        Modifier.M64n224k256,
        Modifier.M64n240k256,
        Modifier.M64n24k256,
        Modifier.M64n256k256,
        Modifier.M64n32k256,
        Modifier.M64n48k256,
        Modifier.M64n64k256,
        Modifier.M64n80k256,
        Modifier.M64n8k256,
        Modifier.M64n96k256
      ),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.B1),
      ModifierGroup(Modifier.B1),
      ModifierGroup(Modifier.And),
      ModifierGroup(Modifier.Popc)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsyncSp_176_0(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k32,
        Modifier.M64n112k32,
        Modifier.M64n120k32,
        Modifier.M64n128k32,
        Modifier.M64n136k32,
        Modifier.M64n144k32,
        Modifier.M64n152k32,
        Modifier.M64n160k32,
        Modifier.M64n168k32,
        Modifier.M64n16k32,
        Modifier.M64n176k32,
        Modifier.M64n184k32,
        Modifier.M64n192k32,
        Modifier.M64n200k32,
        Modifier.M64n208k32,
        Modifier.M64n216k32,
        Modifier.M64n224k32,
        Modifier.M64n232k32,
        Modifier.M64n240k32,
        Modifier.M64n248k32,
        Modifier.M64n24k32,
        Modifier.M64n256k32,
        Modifier.M64n32k32,
        Modifier.M64n40k32,
        Modifier.M64n48k32,
        Modifier.M64n56k32,
        Modifier.M64n64k32,
        Modifier.M64n72k32,
        Modifier.M64n80k32,
        Modifier.M64n88k32,
        Modifier.M64n8k32,
        Modifier.M64n96k32
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsyncSp_176_1(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k32,
        Modifier.M64n112k32,
        Modifier.M64n120k32,
        Modifier.M64n128k32,
        Modifier.M64n136k32,
        Modifier.M64n144k32,
        Modifier.M64n152k32,
        Modifier.M64n160k32,
        Modifier.M64n168k32,
        Modifier.M64n16k32,
        Modifier.M64n176k32,
        Modifier.M64n184k32,
        Modifier.M64n192k32,
        Modifier.M64n200k32,
        Modifier.M64n208k32,
        Modifier.M64n216k32,
        Modifier.M64n224k32,
        Modifier.M64n232k32,
        Modifier.M64n240k32,
        Modifier.M64n248k32,
        Modifier.M64n24k32,
        Modifier.M64n256k32,
        Modifier.M64n32k32,
        Modifier.M64n40k32,
        Modifier.M64n48k32,
        Modifier.M64n56k32,
        Modifier.M64n64k32,
        Modifier.M64n72k32,
        Modifier.M64n80k32,
        Modifier.M64n88k32,
        Modifier.M64n8k32,
        Modifier.M64n96k32
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.F16),
      ModifierGroup(Modifier.F16)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsyncSp_176_2(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k64,
        Modifier.M64n112k64,
        Modifier.M64n120k64,
        Modifier.M64n128k64,
        Modifier.M64n136k64,
        Modifier.M64n144k64,
        Modifier.M64n152k64,
        Modifier.M64n160k64,
        Modifier.M64n168k64,
        Modifier.M64n16k64,
        Modifier.M64n176k64,
        Modifier.M64n184k64,
        Modifier.M64n192k64,
        Modifier.M64n200k64,
        Modifier.M64n208k64,
        Modifier.M64n216k64,
        Modifier.M64n224k64,
        Modifier.M64n232k64,
        Modifier.M64n240k64,
        Modifier.M64n248k64,
        Modifier.M64n24k64,
        Modifier.M64n256k64,
        Modifier.M64n32k64,
        Modifier.M64n40k64,
        Modifier.M64n48k64,
        Modifier.M64n56k64,
        Modifier.M64n64k64,
        Modifier.M64n72k64,
        Modifier.M64n80k64,
        Modifier.M64n88k64,
        Modifier.M64n8k64,
        Modifier.M64n96k64
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsyncSp_176_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k64,
        Modifier.M64n112k64,
        Modifier.M64n120k64,
        Modifier.M64n128k64,
        Modifier.M64n136k64,
        Modifier.M64n144k64,
        Modifier.M64n152k64,
        Modifier.M64n160k64,
        Modifier.M64n168k64,
        Modifier.M64n16k64,
        Modifier.M64n176k64,
        Modifier.M64n184k64,
        Modifier.M64n192k64,
        Modifier.M64n200k64,
        Modifier.M64n208k64,
        Modifier.M64n216k64,
        Modifier.M64n224k64,
        Modifier.M64n232k64,
        Modifier.M64n240k64,
        Modifier.M64n248k64,
        Modifier.M64n24k64,
        Modifier.M64n256k64,
        Modifier.M64n32k64,
        Modifier.M64n40k64,
        Modifier.M64n48k64,
        Modifier.M64n56k64,
        Modifier.M64n64k64,
        Modifier.M64n72k64,
        Modifier.M64n80k64,
        Modifier.M64n88k64,
        Modifier.M64n8k64,
        Modifier.M64n96k64
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsyncSp_176_4(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k64,
        Modifier.M64n112k64,
        Modifier.M64n120k64,
        Modifier.M64n128k64,
        Modifier.M64n136k64,
        Modifier.M64n144k64,
        Modifier.M64n152k64,
        Modifier.M64n160k64,
        Modifier.M64n168k64,
        Modifier.M64n16k64,
        Modifier.M64n176k64,
        Modifier.M64n184k64,
        Modifier.M64n192k64,
        Modifier.M64n200k64,
        Modifier.M64n208k64,
        Modifier.M64n216k64,
        Modifier.M64n224k64,
        Modifier.M64n232k64,
        Modifier.M64n240k64,
        Modifier.M64n248k64,
        Modifier.M64n24k64,
        Modifier.M64n256k64,
        Modifier.M64n32k64,
        Modifier.M64n40k64,
        Modifier.M64n48k64,
        Modifier.M64n56k64,
        Modifier.M64n64k64,
        Modifier.M64n72k64,
        Modifier.M64n80k64,
        Modifier.M64n88k64,
        Modifier.M64n8k64,
        Modifier.M64n96k64
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsyncSp_176_5(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k64,
        Modifier.M64n112k64,
        Modifier.M64n120k64,
        Modifier.M64n128k64,
        Modifier.M64n136k64,
        Modifier.M64n144k64,
        Modifier.M64n152k64,
        Modifier.M64n160k64,
        Modifier.M64n168k64,
        Modifier.M64n16k64,
        Modifier.M64n176k64,
        Modifier.M64n184k64,
        Modifier.M64n192k64,
        Modifier.M64n200k64,
        Modifier.M64n208k64,
        Modifier.M64n216k64,
        Modifier.M64n224k64,
        Modifier.M64n232k64,
        Modifier.M64n240k64,
        Modifier.M64n248k64,
        Modifier.M64n24k64,
        Modifier.M64n256k64,
        Modifier.M64n32k64,
        Modifier.M64n40k64,
        Modifier.M64n48k64,
        Modifier.M64n56k64,
        Modifier.M64n64k64,
        Modifier.M64n72k64,
        Modifier.M64n80k64,
        Modifier.M64n88k64,
        Modifier.M64n8k64,
        Modifier.M64n96k64
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsyncSp_176_6(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k64,
        Modifier.M64n112k64,
        Modifier.M64n120k64,
        Modifier.M64n128k64,
        Modifier.M64n136k64,
        Modifier.M64n144k64,
        Modifier.M64n152k64,
        Modifier.M64n160k64,
        Modifier.M64n168k64,
        Modifier.M64n16k64,
        Modifier.M64n176k64,
        Modifier.M64n184k64,
        Modifier.M64n192k64,
        Modifier.M64n200k64,
        Modifier.M64n208k64,
        Modifier.M64n216k64,
        Modifier.M64n224k64,
        Modifier.M64n232k64,
        Modifier.M64n240k64,
        Modifier.M64n248k64,
        Modifier.M64n24k64,
        Modifier.M64n256k64,
        Modifier.M64n32k64,
        Modifier.M64n40k64,
        Modifier.M64n48k64,
        Modifier.M64n56k64,
        Modifier.M64n64k64,
        Modifier.M64n72k64,
        Modifier.M64n80k64,
        Modifier.M64n88k64,
        Modifier.M64n8k64,
        Modifier.M64n96k64
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsyncSp_176_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n104k64,
        Modifier.M64n112k64,
        Modifier.M64n120k64,
        Modifier.M64n128k64,
        Modifier.M64n136k64,
        Modifier.M64n144k64,
        Modifier.M64n152k64,
        Modifier.M64n160k64,
        Modifier.M64n168k64,
        Modifier.M64n16k64,
        Modifier.M64n176k64,
        Modifier.M64n184k64,
        Modifier.M64n192k64,
        Modifier.M64n200k64,
        Modifier.M64n208k64,
        Modifier.M64n216k64,
        Modifier.M64n224k64,
        Modifier.M64n232k64,
        Modifier.M64n240k64,
        Modifier.M64n248k64,
        Modifier.M64n24k64,
        Modifier.M64n256k64,
        Modifier.M64n32k64,
        Modifier.M64n40k64,
        Modifier.M64n48k64,
        Modifier.M64n56k64,
        Modifier.M64n64k64,
        Modifier.M64n72k64,
        Modifier.M64n80k64,
        Modifier.M64n88k64,
        Modifier.M64n8k64,
        Modifier.M64n96k64
      ),
      ModifierGroup(Modifier.F16, Modifier.F32),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2)
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsyncSp_176_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n112k64,
        Modifier.M64n128k64,
        Modifier.M64n144k64,
        Modifier.M64n160k64,
        Modifier.M64n16k64,
        Modifier.M64n176k64,
        Modifier.M64n192k64,
        Modifier.M64n208k64,
        Modifier.M64n224k64,
        Modifier.M64n240k64,
        Modifier.M64n24k64,
        Modifier.M64n256k64,
        Modifier.M64n32k64,
        Modifier.M64n48k64,
        Modifier.M64n64k64,
        Modifier.M64n80k64,
        Modifier.M64n8k64,
        Modifier.M64n96k64
      ),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.S8, Modifier.U8),
      ModifierGroup(Modifier.S8, Modifier.U8)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    true
  )
  private def metadataWgmmaMmaAsyncSp_176_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(
        Modifier.M64n112k64,
        Modifier.M64n128k64,
        Modifier.M64n144k64,
        Modifier.M64n160k64,
        Modifier.M64n16k64,
        Modifier.M64n176k64,
        Modifier.M64n192k64,
        Modifier.M64n208k64,
        Modifier.M64n224k64,
        Modifier.M64n240k64,
        Modifier.M64n24k64,
        Modifier.M64n256k64,
        Modifier.M64n32k64,
        Modifier.M64n48k64,
        Modifier.M64n64k64,
        Modifier.M64n80k64,
        Modifier.M64n8k64,
        Modifier.M64n96k64
      ),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.S8, Modifier.U8),
      ModifierGroup(Modifier.S8, Modifier.U8)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    true
  )
  private def metadataWmmaLoad_167_0(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F16, Modifier.S8, Modifier.U8)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F16, Modifier.S8, Modifier.U8)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_4(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k8),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k8),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k8),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_9(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k4),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_10(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k4),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k4),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_12(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.M8n8k32),
      ModifierGroup(Modifier.S4, Modifier.U4)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_13(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.M8n8k32),
      ModifierGroup(Modifier.S4, Modifier.U4)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_14(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k32),
      ModifierGroup(Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_15(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.M8n8k128),
      ModifierGroup(Modifier.B1)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_16(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.M8n8k128),
      ModifierGroup(Modifier.B1)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )
  private def metadataWmmaLoad_167_17(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k128),
      ModifierGroup(Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global),
    false
  )

  enum Definition(
      val opcode: NonEmpty[String],
      val mnemonic: Opcode,
      val fixedModifiers: Vec[Modifier],
      val decoder: Decoder,
      metadata: () => Metadata
  ) {
    case Abs extends Definition(
          "abs",
          Opcode.Abs,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Abs(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("abs", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-abs",
                "9.7.1.11. Integer Arithmetic Instructions: abs",
                "abs",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("abs.type  d, a;\n\n.type = { .s16, .s32, .s64 };"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S16, Modifier.S32, Modifier.S64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                "floating-point-instructions-abs",
                "9.7.3.9. Floating Point Instructions: abs",
                "abs",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("abs{.ftz}.f32  d, a;\nabs.f64        d, a;"),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-abs",
                "9.7.4.6. Half Precision Floating Point Instructions: abs",
                "abs",
                "Half Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "abs{.ftz}.f16    d, a;\nabs{.ftz}.f16x2  d, a;\nabs.bf16         d, a;\nabs.bf16x2       d, a;"
                ),
                Version(6, 5),
                Target(53),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Activemask extends Definition(
          "activemask",
          Opcode.Activemask,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.Activemask(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("activemask", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-activemask",
                "9.7.14.12. Parallel Synchronization and Communication Instructions: activemask",
                "activemask",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("activemask.b32 d;"),
                Version(6, 2),
                Target(30),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register),
                  Vec(ModifierGroup(Modifier.B32)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Add extends Definition(
          "add",
          Opcode.Add,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Add(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("add", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-add",
                "9.7.1.1. Integer Arithmetic Instructions: add",
                "add",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "add.type1        d, a, b;\nadd{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u64,\n           .s16, .s64 };\n\n.type2 = { .u32, .u16x2, .u8x4,\n           .s32, .s16x2, .s8x4 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S16, Modifier.S64, Modifier.U16, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.S16x2,
                      Modifier.S32,
                      Modifier.S8x4,
                      Modifier.U16x2,
                      Modifier.U32,
                      Modifier.U8x4
                    )),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "floating-point-instructions-add",
                "9.7.3.3. Floating Point Instructions: add",
                "add",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "add{.rnd}{.ftz}{.sat}.f32  d, a, b;\nadd{.rnd}{.ftz}.f32x2      d, a, b;\nadd{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Ftz,
                      Modifier.Rm,
                      Modifier.Rn,
                      Modifier.Rp,
                      Modifier.Rz,
                      Modifier.Sat
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32x2)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Ftz,
                      Modifier.Rm,
                      Modifier.Rn,
                      Modifier.Rp,
                      Modifier.Rz
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-add",
                "9.7.4.1. Half Precision Floating Point Instructions: add",
                "add",
                "Half Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "add{.rnd}{.ftz}{.sat}.f16   d, a, b;\nadd{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nadd{.rnd}.bf16   d, a, b;\nadd{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
                ),
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "mixed-precision-floating-point-instructions-add",
                "9.7.5.1. Mixed Precision Floating Point Instructions: add",
                "add",
                "Mixed Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "add{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.F32), ModifierGroup(Modifier.Bf16, Modifier.F16)),
                  _root_.scala.collection.immutable.Set(
                    Modifier.Rm,
                    Modifier.Rn,
                    Modifier.Rp,
                    Modifier.Rz,
                    Modifier.Sat
                  ),
                  true
                ))
              )
            ))
        )
    case AddCc extends Definition(
          "add.cc",
          Opcode.Add,
          Vec(Modifier.Cc),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.AddCc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("add.cc", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-add-cc",
                "9.7.2.1. Extended-Precision Arithmetic Instructions: add.cc",
                "add.cc",
                "Extended-Precision Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "add.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ),
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Addc extends Definition(
          "addc",
          Opcode.Addc,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Addc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("addc", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-addc",
                "9.7.2.2. Extended-Precision Arithmetic Instructions: addc",
                "addc",
                "Extended-Precision Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "addc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ),
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(Modifier.Cc),
                  true
                ))
              )
            ))
        )
    case Alloca extends Definition(
          "alloca",
          Opcode.Alloca,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.Alloca(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("alloca", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "stack-manipulation-instructions-alloca",
                "9.7.18.3. Stack Manipulation Instructions: alloca",
                "alloca",
                "Stack Manipulation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "alloca.type  ptr, size{, immAlign};\n\n.type = { .u32, .u64 };"
                ),
                Version(7, 3),
                Target(52),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case And extends Definition(
          "and",
          Opcode.And,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.And(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("and", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "logic-and-shift-instructions-and",
                "9.7.8.1. Logic and Shift Instructions: and",
                "and",
                "Logic and Shift Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("and.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.Pred)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case ApplyPriority extends Definition(
          "applypriority",
          Opcode.ApplyPriority,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.ApplyPriority(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("applypriority", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-applypriority",
                "9.7.9.17. Data Movement and Conversion Instructions: applypriority",
                "applypriority",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "applypriority{.global}.level::eviction_priority  [a], size;\n\n.level::eviction_priority = { .L2::evict_normal };"
                ),
                Version(7, 4),
                Target(80),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.L2EvictNormal)),
                  _root_.scala.collection.immutable.Set(Modifier.Global),
                  true
                ))
              )
            ))
        )
    case Atom extends Definition(
          "atom",
          Opcode.Atom,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Atom(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("atom", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-atom",
                "9.7.14.5. Parallel Synchronization and Communication Instructions: atom",
                "atom",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "atom{.sem}{.scope}{.space}.op{.level::cache_hint}.type d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.cas.type1 d, [a], b, c;\n\natom{.sem}{.scope}{.space}.exch{.level::cache_hint}.type2 d, [a], b {, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16     d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2   d, [a], b{, cache_policy};\n\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16    d, [a], b{, cache_policy};\natom{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2  d, [a], b{, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .acquire, .release, .acq_rel };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };\n.type1 =              { .b16, .b32, .b64, .b128 };\n.type2 =              { .b32, .b64, .b128 };",
                  "atom{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32                  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_16_bit.half_word_type  d, [a], b{, cache_policy};\natom{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type     d, [a], b{, cache_policy};\n\n.sem =               { .relaxed, .acquire, .release, .acq_rel };\n.scope =             { .cta, .cluster, .gpu, .sys };\n.op =                { .add, .min, .max };\n.half_word_type =    { .f16, .bf16 };\n.packed_type =       { .f16x2, .bf16x2 };\n.vec_16_bit =        { .v2, .v4, .v8 }\n.vec_32_bit =        { .v2, .v4 };\n.level::cache_hint = { .L2::cache_hint }"
                ),
                Version(1, 0),
                Target(11),
                Vec(
                  metadataAtom_143_0(),
                  metadataAtom_143_1(),
                  metadataAtom_143_2(),
                  metadataAtom_143_3(),
                  metadataAtom_143_4(),
                  metadataAtom_143_5(),
                  metadataAtom_143_6(),
                  metadataAtom_143_7(),
                  metadataAtom_143_8(),
                  metadataAtom_143_9()
                )
              )
            ))
        )
    case Bar extends Definition(
          "bar",
          Opcode.Bar,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 1 && (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4)
            ) Right(Instruction.Bar(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bar", Vec(1, 2, 3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-bar",
                "9.7.14.1. Parallel Synchronization and Communication Instructions: bar, barrier",
                "bar, barrier",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "barrier{.cta}.sync{.aligned}      a{, b};\nbarrier{.cta}.arrive{.aligned}    a, b;\n\nbarrier{.cta}.red.popc{.aligned}.u32  d, a{, b}, {!}c;\nbarrier{.cta}.red.op{.aligned}.pred   p, a{, b}, {!}c;\n\nbar{.cta}.sync      a{, b};\nbar{.cta}.arrive    a, b;\n\nbar{.cta}.red.popc.u32  d, a{, b}, {!}c;\nbar{.cta}.red.op.pred   p, a{, b}, {!}c;\n\n.op = { .and, .or };"
                ),
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Sync)),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Arrive)),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(ModifierGroup(Modifier.Red), ModifierGroup(Modifier.Popc), ModifierGroup(Modifier.U32)),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.And, Modifier.Or),
                      ModifierGroup(Modifier.Pred)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
                    false
                  )
                )
              )
            ))
        )
    case BarWarpSync extends Definition(
          "bar.warp.sync",
          Opcode.Bar,
          Vec(Modifier.Warp, Modifier.Sync),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.BarWarpSync(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bar.warp.sync", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-bar-warp-sync",
                "9.7.14.2. Parallel Synchronization and Communication Instructions: bar.warp.sync",
                "bar.warp.sync",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("bar.warp.sync      membermask;"),
                Version(6, 0),
                Target(30),
                Vec(InstructionForm(Vec(OperandConstraint.Value), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ))
        )
    case Barrier extends Definition(
          "barrier",
          Opcode.Barrier,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 1 && (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4)
            ) Right(Instruction.Barrier(operands(0), operands.lift(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("barrier", Vec(1, 2, 3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-bar",
                "9.7.14.1. Parallel Synchronization and Communication Instructions: bar, barrier",
                "bar, barrier",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "barrier{.cta}.sync{.aligned}      a{, b};\nbarrier{.cta}.arrive{.aligned}    a, b;\n\nbarrier{.cta}.red.popc{.aligned}.u32  d, a{, b}, {!}c;\nbarrier{.cta}.red.op{.aligned}.pred   p, a{, b}, {!}c;\n\nbar{.cta}.sync      a{, b};\nbar{.cta}.arrive    a, b;\n\nbar{.cta}.red.popc.u32  d, a{, b}, {!}c;\nbar{.cta}.red.op.pred   p, a{, b}, {!}c;\n\n.op = { .and, .or };"
                ),
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Sync)),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Arrive)),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(ModifierGroup(Modifier.Red), ModifierGroup(Modifier.Popc), ModifierGroup(Modifier.U32)),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.And, Modifier.Or),
                      ModifierGroup(Modifier.Pred)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
                    false
                  )
                )
              )
            ))
        )
    case BarrierCluster extends Definition(
          "barrier.cluster",
          Opcode.Barrier,
          Vec(Modifier.Cluster),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.BarrierCluster(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("barrier.cluster", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-barrier-cluster",
                "9.7.14.3. Parallel Synchronization and Communication Instructions: barrier.cluster",
                "barrier.cluster",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "barrier.cluster.arrive{.sem}{.aligned};\nbarrier.cluster.wait{.acquire}{.aligned};\n\n.sem = {.release, .relaxed}"
                ),
                Version(7, 8),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Arrive)),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Relaxed, Modifier.Release),
                    true
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Wait)),
                    _root_.scala.collection.immutable.Set(Modifier.Acquire, Modifier.Aligned),
                    true
                  )
                )
              )
            ))
        )
    case Bfe extends Definition(
          "bfe",
          Opcode.Bfe,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Bfe(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bfe", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-bfe",
                "9.7.1.20. Integer Arithmetic Instructions: bfe",
                "bfe",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "bfe.type  d, a, b, c;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"
                ),
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Bfi extends Definition(
          "bfi",
          Opcode.Bfi,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 5 && (operands.size == 5))
              Right(Instruction.Bfi(operands(0), operands(1), operands(2), operands(3), operands(4), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bfi", Vec(5), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-bfi",
                "9.7.1.21. Integer Arithmetic Instructions: bfi",
                "bfi",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("bfi.type  f, a, b, c, d;\n\n.type = { .b32, .b64 };"),
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Register
                  ),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Bfind extends Definition(
          "bfind",
          Opcode.Bfind,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Bfind(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bfind", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-bfind",
                "9.7.1.17. Integer Arithmetic Instructions: bfind",
                "bfind",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "bfind.type           d, a;\nbfind.shiftamt.type  d, a;\n\n.type = { .u32, .u64,\n          .s32, .s64 };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Shiftamt),
                      ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ))
        )
    case Bmsk extends Definition(
          "bmsk",
          Opcode.Bmsk,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Bmsk(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bmsk", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-bmsk",
                "9.7.1.23. Integer Arithmetic Instructions: bmsk",
                "bmsk",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("bmsk.mode.b32  d, a, b;\n\n.mode = { .clamp, .wrap };"),
                Version(7, 6),
                Target(70),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Clamp, Modifier.Wrap), ModifierGroup(Modifier.B32)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Bra extends Definition(
          "bra",
          Opcode.Bra,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.Bra(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("bra", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "control-flow-instructions-bra",
                "9.7.13.3. Control Flow Instructions: bra",
                "bra",
                "Control Flow Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "@p   bra{.uni}  tgt;           // tgt is a label\n     bra{.uni}  tgt;           // unconditional branch"
                ),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.Uni),
                  true
                ))
              )
            ))
        )
    case Brev extends Definition(
          "brev",
          Opcode.Brev,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Brev(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("brev", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-brev",
                "9.7.1.19. Integer Arithmetic Instructions: brev",
                "brev",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("brev.type  d, a;\n\n.type = { .b32, .b64 };"),
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Brkpt extends Definition(
          "brkpt",
          Opcode.Brkpt,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.Brkpt(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("brkpt", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "miscellaneous-instructions-brkpt",
                "9.7.20.1. Miscellaneous Instructions: brkpt",
                "brkpt",
                "Miscellaneous Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("brkpt;"),
                Version(1, 0),
                Target(11),
                Vec(InstructionForm(Vec(), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ))
        )
    case BrxIdx extends Definition(
          "brx.idx",
          Opcode.Brx,
          Vec(Modifier.Idx),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.BrxIdx(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("brx.idx", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "control-flow-instructions-brx-idx",
                "9.7.13.4. Control Flow Instructions: brx.idx",
                "brx.idx",
                "Control Flow Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "@p    brx.idx{.uni} index, tlist;\n      brx.idx{.uni} index, tlist;"
                ),
                Version(6, 0),
                Target(30),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.Uni),
                  true
                ))
              )
            ))
        )
    case Call extends Definition(
          "call",
          Opcode.Call,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4) Right(if (
              operands.size >= 2 && Operand.isParenthesized(operands(0))
            ) Instruction.Call(Some(operands(0)), operands(1), operands.lift(2), operands.lift(3), mods, pred)
            else Instruction.Call(None, operands(0), operands.lift(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("call", Vec(1, 2, 3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "control-flow-instructions-call",
                "9.7.13.5. Control Flow Instructions: call",
                "call",
                "Control Flow Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// direct call to named function, func is a symbol\ncall{.uni} (ret-param), func, (param-list);\ncall{.uni} func, (param-list);\ncall{.uni} func;\n\n// indirect call via pointer, with full list of call targets\ncall{.uni} (ret-param), fptr, (param-list), flist;\ncall{.uni} fptr, (param-list), flist;\ncall{.uni} fptr, flist;\n\n// indirect call via pointer, with no knowledge of call targets\ncall{.uni} (ret-param), fptr, (param-list), fproto;\ncall{.uni} fptr, (param-list), fproto;\ncall{.uni} fptr, fproto;"
                ),
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Symbol,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Symbol, OperandConstraint.Unknown("unparsed")),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Symbol),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    true
                  )
                )
              )
            ))
        )
    case Clmad extends Definition(
          "clmad",
          Opcode.Clmad,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Clmad(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("clmad", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-clmad",
                "9.7.1.5. Integer Arithmetic Instructions: clmad",
                "clmad",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("clmad.mode.u64 d, a, b, c;\n\n.mode = { .hi, .lo };"),
                Version(9, 3),
                Target(80),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.Hi, Modifier.Lo), ModifierGroup(Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case ClusterLaunchControlQueryCancel extends Definition(
          "clusterlaunchcontrol.query_cancel",
          Opcode.Clusterlaunchcontrol,
          Vec(Modifier.QueryCancel),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 0 && (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4 || operands.size == 5)
            ) Right(Instruction.ClusterLaunchControlQueryCancel(
              operands.lift(0),
              operands.lift(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "clusterlaunchcontrol.query_cancel",
              Vec(1, 2, 3, 4, 5),
              operands.size
            ))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-query-cancel",
                "9.7.14.19. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.query_cancel",
                "clusterlaunchcontrol.query_cancel",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "clusterlaunchcontrol.query_cancel.is_canceled.pred.b128 pred, try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid.v4.b32.b128 {xdim, ydim, zdim, _},  try_cancel_response;\n\nclusterlaunchcontrol.query_cancel.get_first_ctaid{::dimension}.b32.b128 reg, try_cancel_response;\n\n::dimension = { ::x, ::y, ::z };"
                ),
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.IsCanceled), ModifierGroup(Modifier.Pred), ModifierGroup(Modifier.B128)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.GetFirstCtaid),
                      ModifierGroup(Modifier.V4),
                      ModifierGroup(Modifier.B32),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.GetFirstCtaid),
                      ModifierGroup(Modifier.B32),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case ClusterLaunchControlTryCancel extends Definition(
          "clusterlaunchcontrol.try_cancel",
          Opcode.Clusterlaunchcontrol,
          Vec(Modifier.TryCancel),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.ClusterLaunchControlTryCancel(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "clusterlaunchcontrol.try_cancel",
              Vec(2),
              operands.size
            ))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-clusterlaunchcontrol-try-cancel",
                "9.7.14.18. Parallel Synchronization and Communication Instructions: clusterlaunchcontrol.try_cancel",
                "clusterlaunchcontrol.try_cancel",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "clusterlaunchcontrol.try_cancel.async{.space}.completion_mechanism{.multicast::cluster::all}.b128 [addr], [mbar];\n\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n.space = { .shared::cta };"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Address),
                  Vec(
                    ModifierGroup(Modifier.Async),
                    ModifierGroup(Modifier.MbarrierCompleteTxBytes),
                    ModifierGroup(Modifier.B128)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.MulticastClusterAll, Modifier.SharedCta),
                  false
                ))
              )
            ))
        )
    case Clz extends Definition(
          "clz",
          Opcode.Clz,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Clz(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("clz", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-clz",
                "9.7.1.16. Integer Arithmetic Instructions: clz",
                "clz",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("clz.type  d, a;\n\n.type = { .b32, .b64 };"),
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Cnot extends Definition(
          "cnot",
          Opcode.Cnot,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Cnot(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cnot", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "logic-and-shift-instructions-cnot",
                "9.7.8.5. Logic and Shift Instructions: cnot",
                "cnot",
                "Logic and Shift Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("cnot.type d, a;\n\n.type = { .b16, .b32, .b64 };"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case CopySign extends Definition(
          "copysign",
          Opcode.CopySign,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.CopySign(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("copysign", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-copysign",
                "9.7.3.2. Floating Point Instructions: copysign",
                "copysign",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("copysign.type  d, a, b;\n\n.type = { .f32, .f64 };"),
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.F32, Modifier.F64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Cos extends Definition(
          "cos",
          Opcode.Cos,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Cos(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cos", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-cos",
                "9.7.3.19. Floating Point Instructions: cos",
                "cos",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("cos.approx{.ftz}.f32  d, a;"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                  _root_.scala.collection.immutable.Set(Modifier.Ftz),
                  true
                ))
              )
            ))
        )
    case CpAsync extends Definition(
          "cp.async",
          Opcode.Cp,
          Vec(Modifier.Async),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4 || operands.size == 5))
              Right(Instruction.CpAsync(
                operands(0),
                operands(1),
                operands(2),
                operands.lift(3),
                operands.lift(4),
                mods,
                pred
              ))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async", Vec(3, 4, 5), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async",
                "9.7.9.26.3.1. Data Movement and Conversion Instructions: cp.async",
                "cp.async",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "cp.async.ca.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], cp-size{, src-size}{, cache_policy} ;\ncp.async.cg.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], 16{, src-size}{, cache_policy} ;\ncp.async.ca.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], cp-size{, ignore-src}{, cache_policy} ;\ncp.async.cg.shared{::cta}.global{.level::cache_hint}{.level::prefetch_size}\n                         [dst], [src], 16{, ignore-src}{, cache_policy} ;\n\n.level::cache_hint =     { .L2::cache_hint }\n.level::prefetch_size =  { .L2::64B, .L2::128B, .L2::256B }\ncp-size =                { 4, 8, 16 }"
                ),
                Version(7, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Ca), ModifierGroup(Modifier.Shared), ModifierGroup(Modifier.Global)),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Cg), ModifierGroup(Modifier.Shared), ModifierGroup(Modifier.Global)),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Ca), ModifierGroup(Modifier.Shared), ModifierGroup(Modifier.Global)),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Cg), ModifierGroup(Modifier.Shared), ModifierGroup(Modifier.Global)),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint),
                    false
                  )
                )
              )
            ))
        )
    case CpAsyncBulkMBarrier extends Definition(
          "cp.async.bulk",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            val modifierNames = mods.map(_.name)
            if (
              !modifierNames.contains("bulk_group") && (if (modifierNames.contains("ignore_oob"))
                                                          operands.size == 6 || operands.size == 7
                                                        else if (modifierNames.contains("multicast::cluster"))
                                                          operands.size >= 4 && operands.size <= 6
                                                        else operands.size == 4 || operands.size == 5)
            ) Right(if (modifierNames.contains("ignore_oob")) Instruction.CpAsyncBulkMBarrier(
              operands(0),
              operands(1),
              operands(2),
              operands(5),
              Some(operands(3)),
              Some(operands(4)),
              None,
              operands.lift(6),
              mods,
              pred
            )
            else if (modifierNames.contains("multicast::cluster")) Instruction.CpAsyncBulkMBarrier(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              None,
              None,
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            )
            else Instruction.CpAsyncBulkMBarrier(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              None,
              None,
              None,
              operands.lift(4),
              mods,
              pred
            ))
            else if (!modifierNames.contains("bulk_group"))
              Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk", Vec(4, 5, 6, 7), operands.size))
            else Left(Instruction.DecodeError.UnknownOpcode((("cp.async.bulk") +: modifierNames).mkString(".")))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk",
                "9.7.9.26.4.1. Data Movement and Conversion Instructions: cp.async.bulk",
                "cp.async.bulk",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// global -> shared::cta\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}.type\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }\n\n\n// global -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.multicast}{.level::cache_hint}\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.multicast}{.level::cache_hint}.type\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n.type =                 { .b128 }\n\n\n// shared::cta -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism.type [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.type =                 { .b128 }\n\n\n// shared::cta -> global\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .weak }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}.type\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }"
                ),
                Version(8, 0),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Relaxed),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.MulticastCluster,
                      Modifier.Relaxed
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Relaxed),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask, Modifier.Relaxed),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask),
                    false
                  )
                )
              )
            ))
        )
    case CpAsyncBulkGroup extends Definition(
          "cp.async.bulk",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            val modifierNames = mods.map(_.name)
            if (
              modifierNames.contains("bulk_group") && (operands.size == 3 || operands.size == 4 || operands.size == 5)
            ) Right(if (operands.size == 3)
              Instruction.CpAsyncBulkGroup(operands(0), operands(1), operands(2), None, None, mods, pred)
            else if (operands.size == 4 && modifierNames.contains("cp_mask"))
              Instruction.CpAsyncBulkGroup(operands(0), operands(1), operands(2), None, Some(operands(3)), mods, pred)
            else Instruction.CpAsyncBulkGroup(
              operands(0),
              operands(1),
              operands(2),
              Some(operands(3)),
              operands.lift(4),
              mods,
              pred
            ))
            else if (modifierNames.contains("bulk_group"))
              Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk", Vec(3, 4, 5), operands.size))
            else Left(Instruction.DecodeError.UnknownOpcode((("cp.async.bulk") +: modifierNames).mkString(".")))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk",
                "9.7.9.26.4.1. Data Movement and Conversion Instructions: cp.async.bulk",
                "cp.async.bulk",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// global -> shared::cta\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.ignore_oob}.type\n                      [dstMem], [srcMem], size{, ignoreBytesLeft, ignoreBytesRight}, [mbar] {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }\n\n\n// global -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.multicast}{.level::cache_hint}\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.multicast}{.level::cache_hint}.type\n                      [dstMem], [srcMem], size, [mbar] {, ctaMask} {, cache_policy};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster }\n.type =                 { .b128 }\n\n\n// shared::cta -> shared::cluster\ncp.async.bulk{.sem}.dst.src.completion_mechanism [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .weak }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism.type [dstMem], [srcMem], size, [mbar];\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.type =                 { .b128 }\n\n\n// shared::cta -> global\ncp.async.bulk{.sem}.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .weak }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n\ncp.async.bulk.sem.scope.dst.src.completion_mechanism{.level::cache_hint}{.cp_mask}.type\n                      [dstMem], [srcMem], size {, cache_policy} {, byteMask};\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint =    { .L2::cache_hint }\n.type =                 { .b128 }"
                ),
                Version(8, 0),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Relaxed),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.MulticastCluster,
                      Modifier.Relaxed
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Relaxed),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask, Modifier.Relaxed),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask),
                    false
                  )
                )
              )
            ))
        )
    case CpAsyncBulkCommitGroup extends Definition(
          "cp.async.bulk.commit_group",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.CommitGroup),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.CpAsyncBulkCommitGroup(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.commit_group", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-commit-group",
                "9.7.9.26.6.1. Data Movement and Conversion Instructions: cp.async.bulk.commit_group",
                "cp.async.bulk.commit_group",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("cp.async.bulk.commit_group;"),
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(Vec(), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ))
        )
    case CpAsyncBulkPrefetch extends Definition(
          "cp.async.bulk.prefetch",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.Prefetch),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.CpAsyncBulkPrefetch(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.prefetch", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-prefetch",
                "9.7.9.26.4.3. Data Movement and Conversion Instructions: cp.async.bulk.prefetch",
                "cp.async.bulk.prefetch",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "cp.async.bulk.prefetch.L2.src{.level::cache_hint}   [srcMem], size {, cache_policy};\n\n.src =                { .global }\n.level::cache_hint =  { .L2::cache_hint }"
                ),
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.L2), ModifierGroup(Modifier.Global)),
                  _root_.scala.collection.immutable.Set(Modifier.L2CacheHint),
                  true
                ))
              )
            ))
        )
    case CpAsyncBulkPrefetchTensor extends Definition(
          "cp.async.bulk.prefetch.tensor",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.Prefetch, Modifier.Tensor),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1 || operands.size == 2 || operands.size == 3))
              Right(Instruction.CpAsyncBulkPrefetchTensor(operands(0), operands.lift(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "cp.async.bulk.prefetch.tensor",
              Vec(1, 2, 3),
              operands.size
            ))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-prefetch-tensor",
                "9.7.9.26.5.4. Data Movement and Conversion Instructions: cp.async.bulk.prefetch.tensor",
                "cp.async.bulk.prefetch.tensor",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// global -> L2:\ncp.async.bulk.prefetch.tensor.dim.L2.src{.load_mode}{.level::cache_hint} [tensorMap, tensorCoords]\n                                                             {, im2colInfo } {, cache_policy}\n\n.src =                { .global }\n.dim =                { .1d, .2d, .3d, .4d, .5d }\n.load_mode =          { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =  { .L2::cache_hint }"
                ),
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.L2), ModifierGroup(Modifier.Global)),
                  _root_.scala.collection.immutable.Set(
                    Modifier.L2CacheHint,
                    Modifier.Im2col,
                    Modifier.Im2colW,
                    Modifier.Tile,
                    Modifier.TileGather4
                  ),
                  false
                ))
              )
            ))
        )
    case CpAsyncBulkTensorMBarrier extends Definition(
          "cp.async.bulk.tensor",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.Tensor),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            val modifierNames = mods.map(_.name)
            if (
              !modifierNames.contains(
                "bulk_group"
              ) && (if (modifierNames.exists(_.startsWith("im2col")) && modifierNames.contains("multicast::cluster"))
                      operands.size >= 5 && operands.size <= 6
                    else if (
                      modifierNames.exists(_.startsWith("im2col")) || modifierNames.contains("multicast::cluster")
                    ) operands.size >= 4 && operands.size <= 5
                    else operands.size == 3 || operands.size == 4)
            ) Right(if (modifierNames.exists(_.startsWith("im2col")) && modifierNames.contains("multicast::cluster"))
              Instruction.CpAsyncBulkTensorMBarrier(
                operands(0),
                operands(1),
                operands(2),
                Some(operands(3)),
                operands.lift(4),
                operands.lift(5),
                mods,
                pred
              )
            else if (modifierNames.exists(_.startsWith("im2col"))) Instruction.CpAsyncBulkTensorMBarrier(
              operands(0),
              operands(1),
              operands(2),
              Some(operands(3)),
              None,
              operands.lift(4),
              mods,
              pred
            )
            else if (modifierNames.contains("multicast::cluster")) Instruction.CpAsyncBulkTensorMBarrier(
              operands(0),
              operands(1),
              operands(2),
              None,
              operands.lift(3),
              operands.lift(4),
              mods,
              pred
            )
            else Instruction.CpAsyncBulkTensorMBarrier(
              operands(0),
              operands(1),
              operands(2),
              None,
              None,
              operands.lift(3),
              mods,
              pred
            ))
            else if (!modifierNames.contains("bulk_group"))
              Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.tensor", Vec(3, 4, 5, 6), operands.size))
            else Left(Instruction.DecodeError.UnknownOpcode((("cp.async.bulk.tensor") +: modifierNames).mkString(".")))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-tensor",
                "9.7.9.26.5.2. Data Movement and Conversion Instructions: cp.async.bulk.tensor",
                "cp.async.bulk.tensor",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// global -> shared::cta\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo} {, cache_policy}\n\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n\n\n// global -> shared::cluster\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.multicast}{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo}\n                                   {, ctaMask} {, cache_policy}\n\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster  }\n\n\n// shared::cta -> global\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.level::cache_hint}\n                                   [tensorMap, tensorCoords], [srcMem] {, cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .tile::scatter4, .im2col_no_offs }\n.level::cache_hint =    { .L2::cache_hint }"
                ),
                Version(8, 0),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Im2colNoOffs,
                      Modifier.Tile,
                      Modifier.TileScatter4
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Im2colNoOffs,
                      Modifier.MulticastCluster,
                      Modifier.Tile,
                      Modifier.TileScatter4
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Im2colNoOffs,
                      Modifier.Tile,
                      Modifier.TileScatter4
                    ),
                    false
                  )
                )
              )
            ))
        )
    case CpAsyncBulkTensorGroup extends Definition(
          "cp.async.bulk.tensor",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.Tensor),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            val modifierNames = mods.map(_.name)
            if (modifierNames.contains("bulk_group") && (operands.size == 2 || operands.size == 3))
              Right(Instruction.CpAsyncBulkTensorGroup(operands(0), operands(1), operands.lift(2), mods, pred))
            else if (modifierNames.contains("bulk_group"))
              Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.tensor", Vec(2, 3), operands.size))
            else Left(Instruction.DecodeError.UnknownOpcode((("cp.async.bulk.tensor") +: modifierNames).mkString(".")))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-tensor",
                "9.7.9.26.5.2. Data Movement and Conversion Instructions: cp.async.bulk.tensor",
                "cp.async.bulk.tensor",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// global -> shared::cta\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo} {, cache_policy}\n\n.dst =                  { .shared::cta }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n\n\n// global -> shared::cluster\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.multicast}{.cta_group}{.level::cache_hint}\n                                   [dstMem], [tensorMap, tensorCoords], [mbar]{, im2colInfo}\n                                   {, ctaMask} {, cache_policy}\n\n.dst =                  { .shared::cluster }\n.src =                  { .global }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.cta_group =            { .cta_group::1, .cta_group::2 }\n.load_mode =            { .tile, .tile::gather4, .im2col, .im2col::w, .im2col::w::128 }\n.level::cache_hint =    { .L2::cache_hint }\n.multicast =            { .multicast::cluster  }\n\n\n// shared::cta -> global\ncp.async.bulk.tensor.dim.dst.src{.load_mode}.completion_mechanism{.level::cache_hint}\n                                   [tensorMap, tensorCoords], [srcMem] {, cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .tile::scatter4, .im2col_no_offs }\n.level::cache_hint =    { .L2::cache_hint }"
                ),
                Version(8, 0),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Im2colNoOffs,
                      Modifier.Tile,
                      Modifier.TileScatter4
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Im2colNoOffs,
                      Modifier.MulticastCluster,
                      Modifier.Tile,
                      Modifier.TileScatter4
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Im2colNoOffs,
                      Modifier.Tile,
                      Modifier.TileScatter4
                    ),
                    false
                  )
                )
              )
            ))
        )
    case CpAsyncBulkWaitGroup extends Definition(
          "cp.async.bulk.wait_group",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Bulk, Modifier.WaitGroup),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1))
              Right(Instruction.CpAsyncBulkWaitGroup(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.bulk.wait_group", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-bulk-wait-group",
                "9.7.9.26.6.2. Data Movement and Conversion Instructions: cp.async.bulk.wait_group",
                "cp.async.bulk.wait_group",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("cp.async.bulk.wait_group{.read} N;"),
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.Read),
                  true
                ))
              )
            ))
        )
    case CpAsyncCommitGroup extends Definition(
          "cp.async.commit_group",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.CommitGroup),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.CpAsyncCommitGroup(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.commit_group", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-commit-group",
                "9.7.9.26.3.2. Data Movement and Conversion Instructions: cp.async.commit_group",
                "cp.async.commit_group",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("cp.async.commit_group ;"),
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(Vec(), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ))
        )
    case CpAsyncMbarrierArrive extends Definition(
          "cp.async.mbarrier.arrive",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.Mbarrier, Modifier.Arrive),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1))
              Right(Instruction.CpAsyncMbarrierArrive(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.mbarrier.arrive", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-cp-async-mbarrier-arrive",
                "9.7.14.16.18. Parallel Synchronization and Communication Instructions: cp.async.mbarrier.arrive",
                "cp.async.mbarrier.arrive",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "cp.async.mbarrier.arrive{.noinc}{.shared{::cta}}.b64 [addr];"
                ),
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.Noinc, Modifier.Shared),
                  false
                ))
              )
            ))
        )
    case CpAsyncWaitAll extends Definition(
          "cp.async.wait_all",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.WaitAll),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.CpAsyncWaitAll(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.wait_all", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-wait-group",
                "9.7.9.26.3.3. Data Movement and Conversion Instructions: cp.async.wait_group / cp.async.wait_all",
                "cp.async.wait_group / cp.async.wait_all",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("cp.async.wait_group N;\ncp.async.wait_all ;"),
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(Vec(), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ))
        )
    case CpAsyncWaitGroup extends Definition(
          "cp.async.wait_group",
          Opcode.Cp,
          Vec(Modifier.Async, Modifier.WaitGroup),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.CpAsyncWaitGroup(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.async.wait_group", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-async-wait-group",
                "9.7.9.26.3.3. Data Movement and Conversion Instructions: cp.async.wait_group / cp.async.wait_all",
                "cp.async.wait_group / cp.async.wait_all",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("cp.async.wait_group N;\ncp.async.wait_all ;"),
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(Vec(OperandConstraint.Value), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ))
        )
    case CpReduceAsyncBulk extends Definition(
          "cp.reduce.async.bulk",
          Opcode.Cp,
          Vec(Modifier.Reduce, Modifier.Async, Modifier.Bulk),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.CpReduceAsyncBulk(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cp.reduce.async.bulk", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-reduce-async-bulk",
                "9.7.9.26.4.2. Data Movement and Conversion Instructions: cp.reduce.async.bulk",
                "cp.reduce.async.bulk",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.redOp.type\n              [dstMem], [srcMem], size, [mbar]\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster }\n.dst =                  { .shared::cluster }\n.src =                  { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes }\n.redOp=                 { .and, .or, .xor,\n                          .add, .inc, .dec,\n                          .min, .max }\n.type =                 { .b32, .u32, .s32, .b64, .u64 }\n\n\ncp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism{.level::cache_hint}.redOp.type\n               [dstMem], [srcMem], size{, cache_policy}\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst =                  { .global      }\n.src =                  { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.level::cache_hint    = { .L2::cache_hint }\n.redOp=                 { .and, .or, .xor,\n                          .add, .inc, .dec,\n                          .min, .max }\n.type =                 { .f16, .bf16, .b32, .u32, .s32, .b64, .u64, .s64, .f32, .f64 }\n\n\ncp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism{.level::cache_hint}.add.noftz.type\n               [dstMem], [srcMem], size{, cache_policy}\n\n.sem =                  { .relaxed }\n.scope =                { .cta, .cluster, .gpu, .sys }\n.dst  =                 { .global }\n.src  =                 { .shared::cta }\n.completion_mechanism = { .bulk_group }\n.type =                 { .f16, .bf16 }"
                ),
                Version(8, 0),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(
                        Modifier.Add,
                        Modifier.And,
                        Modifier.Dec,
                        Modifier.Inc,
                        Modifier.Max,
                        Modifier.Min,
                        Modifier.Or,
                        Modifier.Xor
                      ),
                      ModifierGroup(Modifier.Bf16, Modifier.F16)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys
                    ),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(
                        Modifier.Add,
                        Modifier.And,
                        Modifier.Dec,
                        Modifier.Inc,
                        Modifier.Max,
                        Modifier.Min,
                        Modifier.Or,
                        Modifier.Xor
                      ),
                      ModifierGroup(Modifier.Bf16, Modifier.F16)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys
                    ),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(Modifier.Noftz),
                      ModifierGroup(Modifier.Bf16, Modifier.F16)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys
                    ),
                    true
                  )
                )
              )
            ))
        )
    case CpReduceAsyncBulkTensor extends Definition(
          "cp.reduce.async.bulk.tensor",
          Opcode.Cp,
          Vec(Modifier.Reduce, Modifier.Async, Modifier.Bulk, Modifier.Tensor),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.CpReduceAsyncBulkTensor(operands(0), operands(1), operands.lift(2), mods, pred))
            else
              Left(Instruction.DecodeError.InvalidOperandCount("cp.reduce.async.bulk.tensor", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cp-reduce-async-bulk-tensor",
                "9.7.9.26.5.3. Data Movement and Conversion Instructions: cp.reduce.async.bulk.tensor",
                "cp.reduce.async.bulk.tensor",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// shared::cta -> global:\ncp.reduce.async.bulk.tensor.dim.dst.src.redOp{.load_mode}.completion_mechanism{.level::cache_hint}\n                                          [tensorMap, tensorCoords], [srcMem] {,cache_policy}\n\n.dst =                  { .global }\n.src =                  { .shared::cta }\n.dim =                  { .1d, .2d, .3d, .4d, .5d }\n.completion_mechanism = { .bulk_group }\n.load_mode =            { .tile, .im2col_no_offs }\n.redOp =                { .add, .min, .max, .inc, .dec, .and, .or, .xor}"
                ),
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Global),
                    ModifierGroup(Modifier.SharedCta),
                    ModifierGroup(
                      Modifier.Add,
                      Modifier.And,
                      Modifier.Dec,
                      Modifier.Inc,
                      Modifier.Max,
                      Modifier.Min,
                      Modifier.Or,
                      Modifier.Xor
                    ),
                    ModifierGroup(Modifier.BulkGroup)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Im2colNoOffs, Modifier.LevelCacheHint, Modifier.Tile),
                  true
                ))
              )
            ))
        )
    case CreatePolicy extends Definition(
          "createpolicy",
          Opcode.CreatePolicy,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1 || operands.size == 2 || operands.size == 4))
              Right(Instruction.CreatePolicy(
                operands(0),
                operands.lift(1),
                operands.lift(2),
                operands.lift(3),
                mods,
                pred
              ))
            else Left(Instruction.DecodeError.InvalidOperandCount("createpolicy", Vec(1, 2, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-createpolicy",
                "9.7.9.19. Data Movement and Conversion Instructions: createpolicy",
                "createpolicy",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// Range-based policy\ncreatepolicy.range{.global}.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy, [a], primary-size, total-size;\n\n// Fraction-based policy\ncreatepolicy.fractional.level::primary_priority{.level::secondary_priority}.b64\n                                   cache_policy{, fraction};\n\n// Converting the access property from CUDA APIs\ncreatepolicy.cvt.L2.b64            cache_policy, access-property;\n\n.level::primary_priority =   { .L2::evict_last, .L2::evict_normal,\n                               .L2::evict_first, .L2::evict_unchanged };\n.level::secondary_priority = { .L2::evict_first, .L2::evict_unchanged };"
                ),
                Version(7, 4),
                Target(80),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Range),
                      ModifierGroup(
                        Modifier.L2EvictFirst,
                        Modifier.L2EvictLast,
                        Modifier.L2EvictNormal,
                        Modifier.L2EvictUnchanged
                      ),
                      ModifierGroup(Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2EvictFirst,
                      Modifier.L2EvictUnchanged,
                      Modifier.Global
                    ),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Fractional),
                      ModifierGroup(
                        Modifier.L2EvictFirst,
                        Modifier.L2EvictLast,
                        Modifier.L2EvictNormal,
                        Modifier.L2EvictUnchanged
                      ),
                      ModifierGroup(Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2EvictFirst, Modifier.L2EvictUnchanged),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Cvt), ModifierGroup(Modifier.L2), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ))
        )
    case Cvt extends Definition(
          "cvt",
          Opcode.Cvt,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 1 && (operands.size == 2 || operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Cvt(
              operands(0),
              operands.lift(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("cvt", Vec(2, 3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cvt",
                "9.7.9.22. Data Movement and Conversion Instructions: cvt",
                "cvt",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "cvt{.irnd}{.ftz}{.sat}.dtype.atype         d, a;  // integer rounding\ncvt{.frnd}{.ftz}{.sat}.dtype.atype         d, a;  // fp rounding\n\ncvt.frnd2{.relu}{.satfinite}.f16.f32       d, a;\ncvt.frnd2{.relu}{.satfinite}.f16x2.f32     d, a, b;\ncvt.rs{.relu}{.satfinite}.f16x2.f32        d, a, b, rbits;\n\ncvt.frnd2{.relu}{.satfinite}.bf16.f32      d, a;\ncvt.frnd2{.relu}{.satfinite}.bf16x2.f32    d, a, b;\ncvt.rs{.relu}{.satfinite}.bf16x2.f32       d, a, b, rbits;\n\ncvt.rna{.satfinite}.tf32.f32               d, a;\ncvt.frnd2{.satfinite}{.relu}.tf32.f32      d, a;\n\ncvt.rn.satfinite{.relu}.f8x2type.f32       d, a, b;\ncvt.rn.satfinite{.relu}.f8x2type.fp16x2    d, a;\ncvt.rn{.relu}.f16x2.f8x2type               d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f8x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f8x4type.f32       d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f4x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f4x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f4x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f4x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f4x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.rn.satfinite{.relu}.f6x2type.f32        d, a, b;\ncvt.rn.satfinite{.relu}.f6x2type.fp16x2type d, a;\ncvt.rn{.relu}.f16x2.f6x2type                d, a;\ncvt.rn{.relu}{.satfinite}{.scaled::n2::ue8m0}.bf16x2.f6x2type d, a{, scale-factor};\ncvt.rs{.relu}.satfinite.f6x4type.f32        d, {a, b, e, f}, rbits;\n\ncvt.frnd3{.satfinite}.ue8m0x2.f32          d, a, b;\ncvt.frnd3{.satfinite}.ue8m0x2.bf16x2       d, a;\ncvt.rn.bf16x2.ue8m0x2                      d, a;\n\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.f32      d, a, b{, scale-factor};\ncvt.rn.satfinite{.relu}{.scaled::n2::ue8m0}.s2f6x2.bf16x2   d, a{, scale-factor};\ncvt.rn{.satfinite}{.relu}{.scaled::n2::ue8m0}.bf16x2.s2f6x2 d, a{, scale-factor};\n\n.irnd   = { .rni, .rzi, .rmi, .rpi };\n.frnd   = { .rn,  .rz,  .rm,  .rp  };\n.frnd2  = { .rn,  .rz };\n.frnd3  = { .rz,  .rp };\n.dtype = .atype = { .u8,   .u16, .u32, .u64,\n                    .s8,   .s16, .s32, .s64,\n                    .bf16, .f16, .f32, .f64 };\n.f8x2type = { .e4m3x2, .e5m2x2 };\n.f4x2type = { .e2m1x2 };\n.f6x2type = { .e2m3x2, .e3m2x2 };\n.f4x4type = { .e2m1x4 };\n.f8x4type = { .e4m3x4, .e5m2x4 };\n.f6x4type = { .e2m3x4, .e3m2x4 };\n.fp16x2type = { .f16x2, .bf16x2 };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  metadataCvt_100_0(),
                  metadataCvt_100_1(),
                  metadataCvt_100_2(),
                  metadataCvt_100_3(),
                  metadataCvt_100_4(),
                  metadataCvt_100_5(),
                  metadataCvt_100_6(),
                  metadataCvt_100_7(),
                  metadataCvt_100_8(),
                  metadataCvt_100_9(),
                  metadataCvt_100_10(),
                  metadataCvt_100_11(),
                  metadataCvt_100_12(),
                  metadataCvt_100_13(),
                  metadataCvt_100_14(),
                  metadataCvt_100_15(),
                  metadataCvt_100_16(),
                  metadataCvt_100_17(),
                  metadataCvt_100_18(),
                  metadataCvt_100_19(),
                  metadataCvt_100_20(),
                  metadataCvt_100_21(),
                  metadataCvt_100_22(),
                  metadataCvt_100_23(),
                  metadataCvt_100_24(),
                  metadataCvt_100_25(),
                  metadataCvt_100_26(),
                  metadataCvt_100_27(),
                  metadataCvt_100_28(),
                  metadataCvt_100_29(),
                  metadataCvt_100_30()
                )
              )
            ))
        )
    case CvtPack extends Definition(
          "cvt.pack",
          Opcode.Cvt,
          Vec(Modifier.Pack),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.CvtPack(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cvt.pack", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cvt-pack",
                "9.7.9.23. Data Movement and Conversion Instructions: cvt.pack",
                "cvt.pack",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "cvt.pack.sat.convertType.abType  d, a, b;\n    .convertType  = { .u16, .s16 }\n    .abType       = { .s32 }\n\ncvt.pack.sat.convertType.abType.cType  d, a, b, c;\n    .convertType  = { .u2, .s2, .u4, .s4, .u8, .s8 }\n    .abType       = { .s32 }\n    .cType        = { .b32 }"
                ),
                Version(6, 5),
                Target(72),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Sat),
                      ModifierGroup(Modifier.S2, Modifier.S4, Modifier.S8, Modifier.U2, Modifier.U4, Modifier.U8),
                      ModifierGroup(Modifier.S32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Sat),
                      ModifierGroup(Modifier.S2, Modifier.S4, Modifier.S8, Modifier.U2, Modifier.U4, Modifier.U8),
                      ModifierGroup(Modifier.S32),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Cvta extends Definition(
          "cvta",
          Opcode.Cvta,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Cvta(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cvta", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-cvta",
                "9.7.9.21. Data Movement and Conversion Instructions: cvta",
                "cvta",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// convert const, global, local, or shared address to generic address\ncvta.space.size  p, a;        // source address in register a\ncvta.space.size  p, var;      // get generic address of var\ncvta.space.size  p, var+imm;  // generic address of var+offset\n\n// convert generic address to const, global, local, or shared address\ncvta.to.space.size  p, a;\n\n.space = { .const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };\n.size  = { .u32, .u64 };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Const, Modifier.Global, Modifier.Local),
                      ModifierGroup(Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Const, Modifier.Global, Modifier.Local),
                      ModifierGroup(Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Const, Modifier.Global, Modifier.Local),
                      ModifierGroup(Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.To),
                      ModifierGroup(Modifier.Const, Modifier.Global, Modifier.Local),
                      ModifierGroup(Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Discard extends Definition(
          "discard",
          Opcode.Discard,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Discard(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("discard", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-discard",
                "9.7.9.18. Data Movement and Conversion Instructions: discard",
                "discard",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("discard{.global}.level  [a], size;\n\n.level = { .L2 };"),
                Version(7, 4),
                Target(80),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.L2)),
                  _root_.scala.collection.immutable.Set(Modifier.Global),
                  true
                ))
              )
            ))
        )
    case Div extends Definition(
          "div",
          Opcode.Div,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Div(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("div", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-div",
                "9.7.1.9. Integer Arithmetic Instructions: div",
                "div",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "div.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    Modifier.S16,
                    Modifier.S32,
                    Modifier.S64,
                    Modifier.U16,
                    Modifier.U32,
                    Modifier.U64
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                "floating-point-instructions-div",
                "9.7.3.8. Floating Point Instructions: div",
                "div",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "div.approx{.ftz}.f32  d, a, b;  // fast, approximate divide\ndiv.full{.ftz}.f32    d, a, b;  // full-range approximate divide\ndiv.rnd{.ftz}.f32     d, a, b;  // IEEE 754 compliant rounding\ndiv.rnd.f64           d, a, b;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Full), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Dp2a extends Definition(
          "dp2a",
          Opcode.Dp2a,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Dp2a(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("dp2a", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-dp2a",
                "9.7.1.25. Integer Arithmetic Instructions: dp2a",
                "dp2a",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "dp2a.mode.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };\n.mode = { .lo, .hi };"
                ),
                Version(5, 0),
                Target(61),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(Modifier.Hi, Modifier.Lo),
                    ModifierGroup(Modifier.S32, Modifier.U32),
                    ModifierGroup(Modifier.S32, Modifier.U32)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Dp4a extends Definition(
          "dp4a",
          Opcode.Dp4a,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Dp4a(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("dp4a", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-dp4a",
                "9.7.1.24. Integer Arithmetic Instructions: dp4a",
                "dp4a",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "dp4a.atype.btype  d, a, b, c;\n\n.atype = .btype = { .u32, .s32 };"
                ),
                Version(5, 0),
                Target(61),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.S32, Modifier.U32), ModifierGroup(Modifier.S32, Modifier.U32)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case ElectSync extends Definition(
          "elect.sync",
          Opcode.Elect,
          Vec(Modifier.Sync),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.ElectSync(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("elect.sync", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-elect-sync",
                "9.7.14.15. Parallel Synchronization and Communication Instructions: elect.sync",
                "elect.sync",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("elect.sync d|p, membermask;"),
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Ex2 extends Definition(
          "ex2",
          Opcode.Ex2,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Ex2(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ex2", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-ex2",
                "9.7.3.21. Floating Point Instructions: ex2",
                "ex2",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("ex2.approx{.ftz}.f32  d, a;"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                  _root_.scala.collection.immutable.Set(Modifier.Ftz),
                  true
                ))
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-ex2",
                "9.7.4.10. Half Precision Floating Point Instructions: ex2",
                "ex2",
                "Half Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "ex2.approx.atype     d, a;\nex2.approx.ftz.btype d, a;\n\n.atype = { .f16,  .f16x2}\n.btype = { .bf16, .bf16x2}"
                ),
                Version(7, 0),
                Target(75),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F16, Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Approx),
                      ModifierGroup(Modifier.Ftz),
                      ModifierGroup(Modifier.Bf16, Modifier.Bf16x2)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Exit extends Definition(
          "exit",
          Opcode.Exit,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.Exit(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("exit", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "control-flow-instructions-exit",
                "9.7.13.7. Control Flow Instructions: exit",
                "exit",
                "Control Flow Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("exit;"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(Vec(), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ))
        )
    case FabricSubmit extends Definition(
          "fabric.submit",
          Opcode.Fabric,
          Vec(Modifier.Submit),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.FabricSubmit(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.submit", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "fabric-instructions-submit",
                "9.7.10.5.5. Fabric Instructions: fabric.submit",
                "fabric.submit",
                "Fabric Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "fabric.submit{.submitop};\n\n.submitop = { .op_restrict::fetching }"
                ),
                Version(9, 3),
                Target(100),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.OpRestrictFetching),
                  true
                ))
              )
            ))
        )
    case FabricTryGet extends Definition(
          "fabric.try_get",
          Opcode.Fabric,
          Vec(Modifier.TryGet),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.FabricTryGet(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.try_get", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "fabric-instructions-try-get",
                "9.7.10.5.1. Fabric Instructions: fabric.try_get",
                "fabric.try_get",
                "Fabric Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "fabric.try_get.async.dst.completion_mechanism.sem.scope.b128 [dst], [srcLeId, srcDataOff], size, [bar];\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }"
                ),
                Version(9, 3),
                Target(100),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Address,
                    OperandConstraint.Address,
                    OperandConstraint.Value,
                    OperandConstraint.Address
                  ),
                  Vec(
                    ModifierGroup(Modifier.Async),
                    ModifierGroup(Modifier.SharedCta),
                    ModifierGroup(Modifier.Relaxed),
                    ModifierGroup(Modifier.Sys),
                    ModifierGroup(Modifier.B128)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case FabricTryPullred extends Definition(
          "fabric.try_pullred",
          Opcode.Fabric,
          Vec(Modifier.TryPullred),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 5 && (operands.size == 5)) Right(Instruction.FabricTryPullred(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.try_pullred", Vec(5), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "fabric-instructions-try-pullred",
                "9.7.10.5.4. Fabric Instructions: fabric.try_pullred",
                "fabric.try_pullred",
                "Fabric Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "fabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpBit.typeBit.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpMinMax.typeMinMax.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAdd.typeAdd.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc16.typeAddAcc16.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\nfabric.try_pullred.async.multimem.dst.completion_mechanism.sem.scope.redOpAddAcc32.typeAddAcc32.sync [dst], [srcLeId, srcDataOff], size, [bar], imm-membermask;\n\n.dst = { .shared::cta }\n.completion_mechanism = { .mbarrier::complete_tx::bytes.mbarrier::report::fabric }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .bf16, .f16, .e4m3, .e5m2 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32 }\n.redOpAddAcc16 = { .add.acc::f16 }\n.typeAddAcc16 = { .e4m3, .e5m2 }\n.redOpAddAcc32 = { .add.acc::f32 }\n.typeAddAcc32 = { .f16, .bf16 }"
                ),
                Version(9, 3),
                Target(100),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.Multimem),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.B32, Modifier.B64),
                      ModifierGroup(Modifier.Sync)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.Multimem),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.Max, Modifier.Min),
                      ModifierGroup(
                        Modifier.Bf16,
                        Modifier.E4m3,
                        Modifier.E5m2,
                        Modifier.F16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.U32,
                        Modifier.U64
                      ),
                      ModifierGroup(Modifier.Sync)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.Multimem),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(Modifier.Bf16, Modifier.F16, Modifier.F32, Modifier.U32, Modifier.U64),
                      ModifierGroup(Modifier.Sync)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.Multimem),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
                      ModifierGroup(Modifier.Sync)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.Multimem),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.Bf16, Modifier.F16),
                      ModifierGroup(Modifier.Sync)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case FabricTryPut extends Definition(
          "fabric.try_put",
          Opcode.Fabric,
          Vec(Modifier.TryPut),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4 || operands.size == 5)) Right(Instruction.FabricTryPut(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands.lift(4),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.try_put", Vec(4, 5), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "fabric-instructions-try-put",
                "9.7.10.5.2. Fabric Instructions: fabric.try_put",
                "fabric.try_put",
                "Fabric Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "fabric.try_put.async{.multimem}.src.completion_mechanism0.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_put.async{.multimem}.src.completion_mechanism0.cp_mask.sem.scope.b128 [dstLeId, dstDataOff], [src], size, [bar], bytemask;\n\nfabric.try_put.async{.multimem}.src.completion_mechanism1.sem.scope.b128 [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }"
                ),
                Version(9, 3),
                Target(100),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Multimem),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.CpMask),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Multimem),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Multimem),
                    true
                  )
                )
              )
            ))
        )
    case FabricTryRed extends Definition(
          "fabric.try_red",
          Opcode.Fabric,
          Vec(Modifier.TryRed),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.FabricTryRed(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.try_red", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "fabric-instructions-try-red",
                "9.7.10.5.3. Fabric Instructions: fabric.try_red",
                "fabric.try_red",
                "Fabric Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "fabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism0.sem.scope.redOpArith.typeArith [dstLeId, dstDataOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpBit.typeBit [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpMinMax.typeMinMax [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\nfabric.try_red.async{.multimem}.src.completion_mechanism1.sem.scope.redOpAdd.typeAdd [dstLeId, dstDataOff, dstCounterOff], [src], size, [bar];\n\n.src = { .shared::cta }\n.completion_mechanism0 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric }\n.completion_mechanism1 = { .mbarrier::complete_tx::16B.mbarrier::report::fabric.counted::bytes }\n.sem = { .relaxed }\n.scope = { .sys }\n.redOpBit = { .and, .xor, .or }\n.typeBit = { .b32, .b64 }\n.redOpMinMax = { .min, .max }\n.typeMinMax = { .u32, .s32, .u64, .s64, .f16, .bf16 }\n.redOpArith = { .add }\n.typeArith = { .u32, .u64, .f16, .bf16, .f32, .f64 }\n.redOpAdd = { .add }\n.typeAdd = { .u32, .u64, .f16, .bf16, .f32, .f64 }"
                ),
                Version(9, 3),
                Target(100),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.B32, Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Multimem),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.Max, Modifier.Min),
                      ModifierGroup(Modifier.Bf16, Modifier.F16, Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Multimem),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(Modifier.Bf16, Modifier.F16, Modifier.F32, Modifier.F64, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Multimem),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.B32, Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Multimem),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.Max, Modifier.Min),
                      ModifierGroup(Modifier.Bf16, Modifier.F16, Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Multimem),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.Async),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(Modifier.Bf16, Modifier.F16, Modifier.F32, Modifier.F64, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Multimem),
                    true
                  )
                )
              )
            ))
        )
    case FabricWait extends Definition(
          "fabric.wait",
          Opcode.Fabric,
          Vec(Modifier.Wait),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.FabricWait(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fabric.wait", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "fabric-instructions-wait",
                "9.7.10.5.6. Fabric Instructions: fabric.wait",
                "fabric.wait",
                "Fabric Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("fabric.wait.sync_restrict::reads;"),
                Version(9, 3),
                Target(100),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.SyncRestrictReads)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Fence extends Definition(
          "fence",
          Opcode.Fence,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0 || operands.size == 2))
              Right(Instruction.Fence(operands.lift(0), operands.lift(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fence", Vec(0, 2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-membar",
                "9.7.14.4. Parallel Synchronization and Communication Instructions: membar / fence",
                "membar / fence",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// Thread fence:\nfence{.sem}.scope;\n\n// Thread fence:\nfence.acquire.sync_restrict::shared::cluster.cluster;\nfence.release.sync_restrict::shared::cta.cluster;\n\n// Operation fence:\nfence.op_restrict.release.cluster;\n\n// Proxy fence (bi-directional):\nfence.proxy.proxykind;\n\n// Proxy fence (uni-directional):\nfence.proxy.to_proxykind::from_proxykind.release.scope;\nfence.proxy.to_proxykind::from_proxykind.acquire.scope  [addr], size;\nfence.proxy.async::generic.acquire.sync_restrict::shared::cluster.cluster;\nfence.proxy.async::generic.release.sync_restrict::shared::cta.cluster;\nfence.proxy.to_proxykind::from_proxykind_fabric.alias.sem_fabric.sys;\n\n// Old style membar:\nmembar.level;\nmembar.proxy.proxykind;\n\n.sem       = { .sc, .acq_rel, .acquire, .release };\n.scope     = { .cta, .cluster, .gpu, .sys };\n.level     = { .cta, .gl, .sys };\n.proxykind = { .alias, .async, .async.global, .async.shared::{cta, cluster} };\n.op_restrict = { .mbarrier_init };\n.to_proxykind::from_proxykind = {.tensormap::generic};\n.to_proxykind::from_proxykind_fabric = {.generic::fabric, .fabric::generic, .fabric::fabric };\n.sem_fabric = { .acquire, .release };"
                ),
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.AcqRel,
                      Modifier.Acquire,
                      Modifier.Release,
                      Modifier.Sc
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(
                      ModifierGroup(Modifier.Acquire),
                      ModifierGroup(Modifier.SyncRestrictSharedCluster),
                      ModifierGroup(Modifier.Cluster)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.SyncRestrictSharedCta),
                      ModifierGroup(Modifier.Cluster)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(
                      ModifierGroup(Modifier.MbarrierInit),
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.Cluster)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Proxy), ModifierGroup(Modifier.Alias, Modifier.Async, Modifier.Cluster)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(
                      ModifierGroup(Modifier.Proxy),
                      ModifierGroup(Modifier.TensormapGeneric),
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Proxy),
                      ModifierGroup(Modifier.TensormapGeneric),
                      ModifierGroup(Modifier.Acquire),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(
                      ModifierGroup(Modifier.Proxy),
                      ModifierGroup(Modifier.AsyncGeneric),
                      ModifierGroup(Modifier.Acquire),
                      ModifierGroup(Modifier.SyncRestrictSharedCluster),
                      ModifierGroup(Modifier.Cluster)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(
                      ModifierGroup(Modifier.Proxy),
                      ModifierGroup(Modifier.AsyncGeneric),
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.SyncRestrictSharedCta),
                      ModifierGroup(Modifier.Cluster)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(
                      ModifierGroup(Modifier.Proxy),
                      ModifierGroup(Modifier.FabricFabric, Modifier.FabricGeneric, Modifier.GenericFabric),
                      ModifierGroup(Modifier.Alias),
                      ModifierGroup(Modifier.Acquire, Modifier.Release),
                      ModifierGroup(Modifier.Sys)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Fma extends Definition(
          "fma",
          Opcode.Fma,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Fma(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fma", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-fma",
                "9.7.3.6. Floating Point Instructions: fma",
                "fma",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "fma.rnd{.ftz}{.sat}.f32  d, a, b, c;\nfma.rnd{.ftz}.f32x2      d, a, b, c;\nfma.rnd.f64              d, a, b, c;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz),
                      ModifierGroup(Modifier.F32x2)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-fma",
                "9.7.4.4. Half Precision Floating Point Instructions: fma",
                "fma",
                "Half Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "fma.rnd{.ftz}{.sat}.f16     d, a, b, c;\nfma.rnd{.ftz}{.sat}.f16x2   d, a, b, c;\nfma.rnd{.ftz}.relu.f16      d, a, b, c;\nfma.rnd{.ftz}.relu.f16x2    d, a, b, c;\nfma.rnd{.relu}.bf16         d, a, b, c;\nfma.rnd{.relu}.bf16x2       d, a, b, c;\nfma.rnd.oob.{relu}.type     d, a, b, c;\n\n.rnd = { .rn };"
                ),
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Relu), ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Relu), ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.Relu),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Relu),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Oob), ModifierGroup(Modifier.Type)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "mixed-precision-floating-point-instructions-fma",
                "9.7.5.3. Mixed Precision Floating Point Instructions: fma",
                "fma",
                "Mixed Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "fma.rnd{.sat}.f32.abtype  d, a, b, c;\n\n.abtype = { .f16, .bf16};\n.rnd    = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(100),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz),
                    ModifierGroup(Modifier.F32),
                    ModifierGroup(Modifier.Bf16, Modifier.F16)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Sat),
                  true
                ))
              )
            ))
        )
    case Fns extends Definition(
          "fns",
          Opcode.Fns,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Fns(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fns", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-fns",
                "9.7.1.18. Integer Arithmetic Instructions: fns",
                "fns",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("fns.b32 d, mask, base, offset;"),
                Version(6, 0),
                Target(30),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.B32)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Fp8 extends Definition(
          "fp8",
          Opcode.Fp8,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.Fp8(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("fp8", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "asynchronous-warpgroup-level-matrix-instructions-wgmma-mma",
                "9.7.16.5.2. Asynchronous Multiply-and-Accumulate Instruction: wgmma.mma_async",
                "wgmma.mma_async",
                "Asynchronous Multiply-and-Accumulate Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "wgmma.mma_async.sync.aligned.shape.dtype.f16.f16  d, a-desc, b-desc, scale-d, imm-scale-a, imm-scale-b, imm-trans-a, imm-trans-b;\n\nwgmma.mma_async.sync.aligned.shape.dtype.f16.f16  d, a, b-desc, scale-d, imm-scale-a, imm-scale-b, imm-trans-b;\n\n.shape   = {.m64n8k16, .m64n16k16, .m64n24k16, .m64n32k16,\n            .m64n40k16, .m64n48k16, .m64n56k16, .m64n64k16,\n            .m64n72k16, .m64n80k16, .m64n88k16, .m64n96k16,\n            .m64n104k16, .m64n112k16, .m64n120k16, .m64n128k16,\n            .m64n136k16, .m64n144k16, .m64n152k16, .m64n160k16,\n            .m64n168k16, .m64n176k16, .m64n184k16, .m64n192k16,\n            .m64n200k16, .m64n208k16, .m64n216k16, .m64n224k16,\n            .m64n232k16, .m64n240k16, .m64n248k16, .m64n256k16};\n.dtype   = {.f16, .f32};",
                  ".bf16 floating point type:\n\nwgmma.mma_async.sync.aligned.shape.dtype.bf16.bf16  d, a-desc, b-desc, scale-d, imm-scale-a, imm-scale-b, imm-trans-a, imm-trans-b;\n\nwgmma.mma_async.sync.aligned.shape.dtype.bf16.bf16  d, a, b-desc, scale-d, imm-scale-a, imm-scale-b, imm-trans-b;\n\n.shape   = {.m64n8k16, .m64n16k16, .m64n24k16, .m64n32k16,\n            .m64n40k16, .m64n48k16, .m64n56k16, .m64n64k16,\n            .m64n72k16, .m64n80k16, .m64n88k16, .m64n96k16,\n            .m64n104k16, .m64n112k16, .m64n120k16, .m64n128k16,\n            .m64n136k16, .m64n144k16, .m64n152k16, .m64n160k16,\n            .m64n168k16, .m64n176k16, .m64n184k16, .m64n192k16,\n            .m64n200k16, .m64n208k16, .m64n216k16, .m64n224k16,\n            .m64n232k16, .m64n240k16, .m64n248k16, .m64n256k16};\n.dtype  = {.f32};\n\n.tf32 floating point type:\n\nwgmma.mma_async.sync.aligned.shape.dtype.tf32.tf32  d, a-desc, b-desc, scale-d, imm-scale-a, imm-scale-b;\n\nwgmma.mma_async.sync.aligned.shape.dtype.tf32.tf32  d, a, b-desc, scale-d, imm-scale-a, imm-scale-b;\n\n.shape   = {.m64n8k8, .m64n16k8, .m64n24k8, .m64n32k8,\n            .m64n40k8, .m64n48k8, .m64n56k8, .m64n64k8,\n            .m64n72k8, .m64n80k8, .m64n88k8, .m64n96k8,\n            .m64n104k8, .m64n112k8, .m64n120k8, .m64n128k8,\n            .m64n136k8, .m64n144k8, .m64n152k8, .m64n160k8,\n            .m64n168k8, .m64n176k8, .m64n184k8, .m64n192k8,\n            .m64n200k8, .m64n208k8, .m64n216k8, .m64n224k8,\n            .m64n232k8, .m64n240k8, .m64n248k8, .m64n256k8};\n.dtype  = {.f32};\n\nFP8 floating point type\n\nwgmma.mma_async.sync.aligned.shape.dtype.atype.btype  d, a-desc, b-desc, scale-d, imm-scale-a, imm-scale-b;\n\nwgmma.mma_async.sync.aligned.shape.dtype.atype.btype  d, a, b-desc, scale-d, imm-scale-a, imm-scale-b;\n\n.shape   = {.m64n8k32, .m64n16k32, .m64n24k32, .m64n32k32,\n            .m64n40k32, .m64n48k32, .m64n56k32, .m64n64k32,\n            .m64n72k32, .m64n80k32, .m64n88k32, .m64n96k32,\n            .m64n104k32, .m64n112k32, .m64n120k32, .m64n128k32,\n            .m64n136k32, .m64n144k32, .m64n152k32, .m64n160k32,\n            .m64n168k32, .m64n176k32, .m64n184k32, .m64n192k32,\n            .m64n200k32, .m64n208k32, .m64n216k32, .m64n224k32,\n            .m64n232k32, .m64n240k32, .m64n248k32, .m64n256k32};\n.atype  = {.e4m3, .e5m2};\n.btype  = {.e4m3, .e5m2};\n.dtype  = {.f16, .f32};",
                  "wgmma.mma_async.sync.aligned.shape{.satfinite}.s32.atype.btype  d, a-desc, b-desc, scale-d;\n\nwgmma.mma_async.sync.aligned.shape{.satfinite}.s32.atype.btype  d, a, b-desc, scale-d;\n\n.shape   = {.m64n8k32, .m64n16k32, .m64n24k32, .m64n32k32,\n            .m64n48k32, .m64n64k32, .m64n80k32, .m64n96k32,\n            .m64n112k32, .m64n128k32, .m64n144k32, .m64n160k32,\n            .m64n176k32, .m64n192k32, .m64n208k32, .m64n224k32};\n.atype  = {.s8, .u8};\n.btype  = {.s8, .u8};",
                  "wgmma.mma_async.sync.aligned.shape.s32.b1.b1.op.popc  d, a-desc, b-desc, scale-d;\n\nwgmma.mma_async.sync.aligned.shape.s32.b1.b1.op.popc  d, a, b-desc, scale-d;\n\n.shape   = {.m64n8k256, .m64n16k256, .m64n24k256, .m64n32k256,\n            .m64n48k256, .m64n64k256, .m64n80k256, .m64n96k256,\n            .m64n112k256, .m64n128k256, .m64n144k256, .m64n160k256,\n            .m64n176k256, .m64n192k256, .m64n208k256, .m64n224k256,\n            .m64n240k256, .m64n256k256};\n.op  = {.and};"
                ),
                Version(8, 0),
                Target(10),
                Vec(InstructionForm(Vec(OperandConstraint.Value), Vec(), _root_.scala.collection.immutable.Set(), true))
              ),
              Metadata.Entry(
                "asynchronous-warpgroup-level-matrix-instructions-wgmma-mma-sp",
                "9.7.16.6.3. Asynchronous Multiply-and-Accumulate Instruction: wgmma.mma_async.sp",
                "wgmma.mma_async.sp",
                "Asynchronous Multiply-and-Accumulate Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "wgmma.mma_async.sp.sync.aligned.shape.dtype.f16.f16  d, a-desc, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b, imm-trans-a, imm-trans-b;\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.f16.f16  d, a, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b, imm-trans-b;\n\n.shape   = {.m64n8k32, .m64n16k32, .m64n24k32, .m64n32k32,\n            .m64n40k32, .m64n48k32, .m64n56k32, .m64n64k32,\n            .m64n72k32, .m64n80k32, .m64n88k32, .m64n96k32,\n            .m64n104k32, .m64n112k32, .m64n120k32, .m64n128k32,\n            .m64n136k32, .m64n144k32, .m64n152k32, .m64n160k32,\n            .m64n168k32, .m64n176k32, .m64n184k32, .m64n192k32,\n            .m64n200k32, .m64n208k32, .m64n216k32, .m64n224k32,\n            .m64n232k32, .m64n240k32, .m64n248k32, .m64n256k32};\n.dtype   = {.f16, .f32};",
                  ".bf16 floating point type:\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.bf16.bf16  d, a-desc, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b, imm-trans-a, imm-trans-b;\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.bf16.bf16  d, a, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b, imm-trans-b;\n\n.shape   = {.m64n8k32, .m64n16k32, .m64n24k32, .m64n32k32,\n            .m64n40k32, .m64n48k32, .m64n56k32, .m64n64k32,\n            .m64n72k32, .m64n80k32, .m64n88k32, .m64n96k32,\n            .m64n104k32, .m64n112k32, .m64n120k32, .m64n128k32,\n            .m64n136k32, .m64n144k32, .m64n152k32, .m64n160k32,\n            .m64n168k32, .m64n176k32, .m64n184k32, .m64n192k32,\n            .m64n200k32, .m64n208k32, .m64n216k32, .m64n224k32,\n            .m64n232k32, .m64n240k32, .m64n248k32, .m64n256k32};\n.dtype  = {.f32};\n\n.tf32 floating point type:\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.tf32.tf32  d, a-desc, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b;\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.tf32.tf32  d, a, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b;\n\n.shape   = {.m64n8k16, .m64n16k16, .m64n24k16, .m64n32k16,\n            .m64n40k16, .m64n48k16, .m64n56k16, .m64n64k16,\n            .m64n72k16, .m64n80k16, .m64n88k16, .m64n96k16,\n            .m64n104k16, .m64n112k16, .m64n120k16, .m64n128k16,\n            .m64n136k16, .m64n144k16, .m64n152k16, .m64n160k16,\n            .m64n168k16, .m64n176k16, .m64n184k16, .m64n192k16,\n            .m64n200k16, .m64n208k16, .m64n216k16, .m64n224k16,\n            .m64n232k16, .m64n240k16, .m64n248k16, .m64n256k16};\n.dtype  = {.f32};\n\nFP8 floating point type\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.atype.btype  d, a-desc, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b;\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.atype.btype  d, a, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b;\n\n.shape   = {.m64n8k64, .m64n16k64, .m64n24k64, .m64n32k64,\n            .m64n40k64, .m64n48k64, .m64n56k64, .m64n64k64,\n            .m64n72k64, .m64n80k64, .m64n88k64, .m64n96k64,\n            .m64n104k64, .m64n112k64, .m64n120k64, .m64n128k64,\n            .m64n136k64, .m64n144k64, .m64n152k64, .m64n160k64,\n            .m64n168k64, .m64n176k64, .m64n184k64, .m64n192k64,\n            .m64n200k64, .m64n208k64, .m64n216k64, .m64n224k64,\n            .m64n232k64, .m64n240k64, .m64n248k64, .m64n256k64};\n.atype  = {.e4m3, .e5m2};\n.btype  = {.e4m3, .e5m2};\n.dtype  = {.f16, .f32};",
                  "wgmma.mma_async.sp.sync.aligned.shape{.satfinite}.s32.atype.btype  d, a-desc, b-desc, sp-meta, sp-sel, scale-d;\n\nwgmma.mma_async.sp.sync.aligned.shape{.satfinite}.s32.atype.btype  d, a, b-desc, sp-meta, sp-sel, scale-d;\n\n.shape   = {.m64n8k64, .m64n16k64, .m64n24k64, .m64n32k64,\n            .m64n48k64, .m64n64k64, .m64n80k64, .m64n96k64,\n            .m64n112k64, .m64n128k64, .m64n144k64, .m64n160k64,\n            .m64n176k64, .m64n192k64, .m64n208k64, .m64n224k64,\n            .m64n240k64, .m64n256k64};\n.atype  = {.s8, .u8};\n.btype  = {.s8, .u8};"
                ),
                Version(8, 2),
                Target(10),
                Vec(InstructionForm(Vec(OperandConstraint.Value), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ))
        )
    case GetCtaRank extends Definition(
          "getctarank",
          Opcode.GetCtaRank,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.GetCtaRank(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("getctarank", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-getctarank",
                "9.7.9.25. Data Movement and Conversion Instructions: getctarank",
                "getctarank",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "getctarank{.space}.type d, a;\n\n// Get cta rank from source shared memory address in register a.\ngetctarank.shared::cluster.type d, a;\n\n// Get cta rank from shared memory variable.\ngetctarank.shared::cluster.type d, var;\n\n// Get cta rank from shared memory variable+offset.\ngetctarank.shared::cluster.type d, var + imm;\n\n// Get cta rank from generic address of shared memory variable in register a.\ngetctarank.type d, a;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"
                ),
                Version(7, 8),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(Modifier.SharedCluster),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.SharedCluster), ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.SharedCluster), ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.SharedCluster), ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ))
        )
    case GridDependencyControl extends Definition(
          "griddepcontrol",
          Opcode.GridDependencyControl,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.GridDependencyControl(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("griddepcontrol", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-griddepcontrol",
                "9.7.14.14. Parallel Synchronization and Communication Instructions: griddepcontrol",
                "griddepcontrol",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "griddepcontrol.action;\n\n.action   = { .launch_dependents, .wait }"
                ),
                Version(7, 8),
                Target(90),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.LaunchDependents, Modifier.Wait)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case IsSpacePredicate extends Definition(
          "isspacep",
          Opcode.IsSpacePredicate,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.IsSpacePredicate(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("isspacep", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-isspacep",
                "9.7.9.20. Data Movement and Conversion Instructions: isspacep",
                "isspacep",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "isspacep.space  p, a;    // result is .pred\n\n.space = { const, .global, .local, .shared{::cta, ::cluster}, .param{::entry} };"
                ),
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Const, Modifier.Global, Modifier.Local)),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case IsTypePredicate extends Definition(
          "istypep",
          Opcode.IsTypePredicate,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.IsTypePredicate(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("istypep", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "texture-instructions-istypep",
                "9.7.11.6. Texture Instructions: istypep",
                "istypep",
                "Texture Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "istypep.type   p, a;  // result is .pred\n\n.type = { .texref, .samplerref, .surfref };"
                ),
                Version(4, 0),
                Target(30),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Samplerref, Modifier.Surfref, Modifier.Texref)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Ld extends Definition(
          "ld",
          Opcode.Ld,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3 || operands.size == 4))
              Right(Instruction.Ld(operands(0), operands(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ld", Vec(2, 3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-ld",
                "9.7.9.8. Data Movement and Conversion Instructions: ld",
                "ld",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "ld{.weak}{.ss}{.cop}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{.unified}{, cache_policy};\n\nld.volatile{.ss}{.level::prefetch_size}{.vec}.type  d, [a];\n\nld.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.acquire.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}{.vec}.type  d, [a]{, cache_policy};\n\nld.mmio.sem.sys{.global}.type  d, [a];\n\n.ss =                       { .const, .global, .local, .param{::entry, ::func}, .shared{::cta, ::cluster} };\n.cop =                      { .ca, .cg, .cs, .lu, .cv };\n.sem =                      { .acquire, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(
                      Modifier.B128,
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.B8,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.S8,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64,
                      Modifier.U8
                    )),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Ca,
                      Modifier.Cg,
                      Modifier.Const,
                      Modifier.Cs,
                      Modifier.Cv,
                      Modifier.Global,
                      Modifier.Local,
                      Modifier.Lu,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8,
                      Modifier.Weak
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(
                      Modifier.B128,
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.B8,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.S8,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64,
                      Modifier.U8
                    )),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L1EvictFirst,
                      Modifier.L1EvictLast,
                      Modifier.L1EvictNormal,
                      Modifier.L1EvictUnchanged,
                      Modifier.L1NoAllocate,
                      Modifier.L2CacheHint,
                      Modifier.L2EvictFirst,
                      Modifier.L2EvictLast,
                      Modifier.L2EvictNormal,
                      Modifier.Const,
                      Modifier.Global,
                      Modifier.Local,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8,
                      Modifier.Weak
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Volatile),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Const,
                      Modifier.Global,
                      Modifier.Local,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L1EvictFirst,
                      Modifier.L1EvictLast,
                      Modifier.L1EvictNormal,
                      Modifier.L1EvictUnchanged,
                      Modifier.L1NoAllocate,
                      Modifier.L2CacheHint,
                      Modifier.L2EvictFirst,
                      Modifier.L2EvictLast,
                      Modifier.L2EvictNormal,
                      Modifier.Const,
                      Modifier.Global,
                      Modifier.Local,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Acquire),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L1EvictFirst,
                      Modifier.L1EvictLast,
                      Modifier.L1EvictNormal,
                      Modifier.L1EvictUnchanged,
                      Modifier.L1NoAllocate,
                      Modifier.L2CacheHint,
                      Modifier.L2EvictFirst,
                      Modifier.L2EvictLast,
                      Modifier.L2EvictNormal,
                      Modifier.Const,
                      Modifier.Global,
                      Modifier.Local,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Mmio),
                      ModifierGroup(Modifier.Acquire, Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global),
                    false
                  )
                )
              )
            ))
        )
    case LdGlobalNc extends Definition(
          "ld.global.nc",
          Opcode.Ld,
          Vec(Modifier.Global, Modifier.Nc),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.LdGlobalNc(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ld.global.nc", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-ld-global-nc",
                "9.7.9.9. Data Movement and Conversion Instructions: ld.global.nc",
                "ld.global.nc",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "ld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.type                 d, [a]{, cache_policy};\nld.global{.cop}.nc{.level::cache_hint}{.level::prefetch_size}.vec.type             d, [a]{, cache_policy};\n\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.type      d, [a]{, cache_policy};\nld.global.nc{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.level::prefetch_size}.vec.type  d, [a]{, cache_policy};\n\n.cop  =                     { .ca, .cg, .cs };     // cache operation\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate};\n.level2::eviction_priority = {.L2::evict_normal, .L2::evict_first, .L2::evict_last};\n.level::cache_hint =        { .L2::cache_hint };\n.level::prefetch_size =     { .L2::64B, .L2::128B, .L2::256B }\n.vec  =                     { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
                ),
                Version(3, 1),
                Target(32),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.B128,
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.B8,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.S8,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64,
                      Modifier.U8
                    )),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.Ca, Modifier.Cg, Modifier.Cs),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.V2, Modifier.V4, Modifier.V8),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.Ca, Modifier.Cg, Modifier.Cs),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.B128,
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.B8,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.S8,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64,
                      Modifier.U8
                    )),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L1EvictFirst,
                      Modifier.L1EvictLast,
                      Modifier.L1EvictNormal,
                      Modifier.L1EvictUnchanged,
                      Modifier.L1NoAllocate,
                      Modifier.L2CacheHint,
                      Modifier.L2EvictFirst,
                      Modifier.L2EvictLast,
                      Modifier.L2EvictNormal
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.V2, Modifier.V4, Modifier.V8),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L1EvictFirst,
                      Modifier.L1EvictLast,
                      Modifier.L1EvictNormal,
                      Modifier.L1EvictUnchanged,
                      Modifier.L1NoAllocate,
                      Modifier.L2CacheHint,
                      Modifier.L2EvictFirst,
                      Modifier.L2EvictLast,
                      Modifier.L2EvictNormal
                    ),
                    false
                  )
                )
              )
            ))
        )
    case Ldmatrix extends Definition(
          "ldmatrix",
          Opcode.Ldmatrix,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Ldmatrix(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ldmatrix", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "warp-level-matrix-instructions-ldmatrix",
                "9.7.15.5.15. Warp-level matrix load instruction: ldmatrix",
                "ldmatrix",
                "Warp-level matrix load instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "ldmatrix.sync.aligned.shape.num{.trans}{.ss}.type r, [p];\n\nldmatrix.sync.aligned.m8n16.num{.ss}.dst_fmt.src_fmt        r, [p];\nldmatrix.sync.aligned.m16n16.num.trans{.ss}.dst_fmt.src_fmt r, [p];\n\n.shape   = {.m8n8, .m16n16};\n.num     = {.x1, .x2, .x4};\n.ss      = {.shared{::cta}};\n.type    = {.b16, .b8};\n.dst_fmt = { .b8x16 };\n.src_fmt = { .b6x16_p32, .b4x16_p64 };"
                ),
                Version(6, 5),
                Target(75),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.M16n16, Modifier.M8n8),
                      ModifierGroup(Modifier.X1, Modifier.X2, Modifier.X4),
                      ModifierGroup(Modifier.B16, Modifier.B8)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Trans),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.M8n16),
                      ModifierGroup(Modifier.X1, Modifier.X2, Modifier.X4),
                      ModifierGroup(Modifier.B8x16),
                      ModifierGroup(Modifier.B4x16P64, Modifier.B6x16P32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.M16n16),
                      ModifierGroup(Modifier.X1, Modifier.X2, Modifier.X4),
                      ModifierGroup(Modifier.Trans),
                      ModifierGroup(Modifier.B8x16),
                      ModifierGroup(Modifier.B4x16P64, Modifier.B6x16P32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Ldu extends Definition(
          "ldu",
          Opcode.Ldu,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Ldu(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ldu", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-ldu",
                "9.7.9.10. Data Movement and Conversion Instructions: ldu",
                "ldu",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "ldu{.ss}.type      d, [a];       // load from address\nldu{.ss}.vec.type  d, [a];       // vec load from address\n\n.ss   = { .global };             // state space\n.vec  = { .v2, .v4 };\n.type = { .b8, .b16, .b32, .b64, .b128,\n          .u8, .u16, .u32, .u64,\n          .s8, .s16, .s32, .s64,\n                     .f32, .f64 };"
                ),
                Version(2, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(ModifierGroup(
                      Modifier.B128,
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.B8,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.S8,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64,
                      Modifier.U8
                    )),
                    _root_.scala.collection.immutable.Set(Modifier.Global),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.V2, Modifier.V4),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global),
                    false
                  )
                )
              )
            ))
        )
    case Lg2 extends Definition(
          "lg2",
          Opcode.Lg2,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Lg2(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("lg2", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-lg2",
                "9.7.3.20. Floating Point Instructions: lg2",
                "lg2",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("lg2.approx{.ftz}.f32  d, a;"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                  _root_.scala.collection.immutable.Set(Modifier.Ftz),
                  true
                ))
              )
            ))
        )
    case Lop3 extends Definition(
          "lop3",
          Opcode.Lop3,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 5 && (operands.size == 5 || operands.size == 6)) Right(Instruction.Lop3(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("lop3", Vec(5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "logic-and-shift-instructions-lop3",
                "9.7.8.6. Logic and Shift Instructions: lop3",
                "lop3",
                "Logic and Shift Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "lop3.b32 d, a, b, c, immLut;\nlop3.BoolOp.b32 d|p, a, b, c, immLut, q;\n\n.BoolOp   = { .or , .and };"
                ),
                Version(4, 3),
                Target(50),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.B32)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Register
                    ),
                    Vec(ModifierGroup(Modifier.And, Modifier.Or), ModifierGroup(Modifier.B32)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Mad extends Definition(
          "mad",
          Opcode.Mad,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Mad(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mad", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-mad",
                "9.7.1.4. Integer Arithmetic Instructions: mad",
                "mad",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mad.mode.type  d, a, b, c;\nmad.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Hi, Modifier.Lo, Modifier.Wide),
                      ModifierGroup(Modifier.S16, Modifier.S32, Modifier.S64, Modifier.U16, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Hi), ModifierGroup(Modifier.Sat), ModifierGroup(Modifier.S32)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                "floating-point-instructions-mad",
                "9.7.3.7. Floating Point Instructions: mad",
                "mad",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mad{.ftz}{.sat}.f32      d, a, b, c;    // .target sm_1x\nmad.rnd{.ftz}{.sat}.f32  d, a, b, c;    // .target sm_20\nmad.rnd.f64              d, a, b, c;    // .target sm_13 and higher\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case MadCc extends Definition(
          "mad.cc",
          Opcode.Mad,
          Vec(Modifier.Cc),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.MadCc(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mad.cc", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-mad-cc",
                "9.7.2.5. Extended-Precision Arithmetic Instructions: mad.cc",
                "mad.cc",
                "Extended-Precision Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mad{.hi,.lo}.cc.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ),
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(Modifier.Hi, Modifier.Lo),
                  true
                ))
              )
            ))
        )
    case Mad24 extends Definition(
          "mad24",
          Opcode.Mad24,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Mad24(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mad24", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-mad24",
                "9.7.1.7. Integer Arithmetic Instructions: mad24",
                "mad24",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mad24.mode.type  d, a, b, c;\nmad24.hi.sat.s32 d, a, b, c;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Hi, Modifier.Lo), ModifierGroup(Modifier.S32, Modifier.U32)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Hi), ModifierGroup(Modifier.Sat), ModifierGroup(Modifier.S32)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ))
        )
    case Madc extends Definition(
          "madc",
          Opcode.Madc,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Madc(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("madc", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-madc",
                "9.7.2.6. Extended-Precision Arithmetic Instructions: madc",
                "madc",
                "Extended-Precision Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "madc{.hi,.lo}{.cc}.type  d, a, b, c;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ),
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(Modifier.Cc, Modifier.Hi, Modifier.Lo),
                  true
                ))
              )
            ))
        )
    case Mapa extends Definition(
          "mapa",
          Opcode.Mapa,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Mapa(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mapa", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-mapa",
                "9.7.9.24. Data Movement and Conversion Instructions: mapa",
                "mapa",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mapa{.space}.type          d, a, b;\n\n// Maps shared memory address in register a into CTA b.\nmapa.shared::cluster.type  d, a, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh, b;\n\n// Maps shared memory variable into CTA b.\nmapa.shared::cluster.type  d, sh + imm, b;\n\n// Maps generic address in register a into CTA b.\nmapa.type                  d, a, b;\n\n.space = { .shared::cluster }\n.type  = { .u32, .u64 }"
                ),
                Version(7, 8),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(Modifier.SharedCluster),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.SharedCluster), ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.SharedCluster), ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.SharedCluster), ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ))
        )
    case MatchAllSync extends Definition(
          "match.all.sync",
          Opcode.Match,
          Vec(Modifier.All, Modifier.Sync),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.MatchAllSync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("match.all.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-match-sync",
                "9.7.14.11. Parallel Synchronization and Communication Instructions: match.sync",
                "match.sync",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "match.any.sync.type  d, a, membermask;\nmatch.all.sync.type  d[|p], a, membermask;\n\n.type = { .b32, .b64 };"
                ),
                Version(6, 0),
                Target(70),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case MatchAnySync extends Definition(
          "match.any.sync",
          Opcode.Match,
          Vec(Modifier.Any, Modifier.Sync),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.MatchAnySync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("match.any.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-match-sync",
                "9.7.14.11. Parallel Synchronization and Communication Instructions: match.sync",
                "match.sync",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "match.any.sync.type  d, a, membermask;\nmatch.all.sync.type  d[|p], a, membermask;\n\n.type = { .b32, .b64 };"
                ),
                Version(6, 0),
                Target(70),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Max extends Definition(
          "max",
          Opcode.Max,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Max(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("max", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-max",
                "9.7.1.14. Integer Arithmetic Instructions: max",
                "max",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "max.type1         d, a, b;\nmax{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.S16,
                      Modifier.S64,
                      Modifier.U16,
                      Modifier.U16x2,
                      Modifier.U32,
                      Modifier.U64,
                      Modifier.U8x4
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S16x2, Modifier.S32, Modifier.S8x4)),
                    _root_.scala.collection.immutable.Set(Modifier.Relu),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "floating-point-instructions-max",
                "9.7.3.12. Floating Point Instructions: max",
                "max",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "max{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmax{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmax.f64                            d, a, b;"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-max",
                "9.7.4.8. Half Precision Floating Point Instructions: max",
                "max",
                "Half Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "max{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmax{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmax{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"
                ),
                Version(7, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Xorsign),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Xorsign),
                    false
                  )
                )
              )
            ))
        )
    case MbarrierArrive extends Definition(
          "mbarrier.arrive",
          Opcode.Mbarrier,
          Vec(Modifier.Arrive),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.MbarrierArrive(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.arrive", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-arrive",
                "9.7.14.16.16. Parallel Synchronization and Communication Instructions: mbarrier.arrive",
                "mbarrier.arrive",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mbarrier.arrive{.sem.scope}{.shared{::cta}}.b64           state, [addr]{, count};\nmbarrier.arrive{.sem.scope}{.shared::cluster}.b64         _, [addr] {,count}\nmbarrier.arrive.expect_tx{.sem.scope}{.shared{::cta}}.b64 state, [addr], txCount;\nmbarrier.arrive.expect_tx{.sem.scope}{.shared::cluster}.b64   _, [addr], txCount;\nmbarrier.arrive.noComplete{.release.cta}{.shared{::cta}}.b64  state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"
                ),
                Version(7, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Shared
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.SharedCluster
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.ExpectTx), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Shared
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.ExpectTx), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.SharedCluster
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.NoComplete), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(Modifier.Cta, Modifier.Release, Modifier.Shared),
                    false
                  )
                )
              )
            ))
        )
    case MbarrierArriveDrop extends Definition(
          "mbarrier.arrive_drop",
          Opcode.Mbarrier,
          Vec(Modifier.ArriveDrop),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.MbarrierArriveDrop(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.arrive_drop", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-arrive-drop",
                "9.7.14.16.17. Parallel Synchronization and Communication Instructions: mbarrier.arrive_drop",
                "mbarrier.arrive_drop",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mbarrier.arrive_drop{.sem.scope}{.shared{::cta}}.b64              state, [addr] {, count};\nmbarrier.arrive_drop{.sem.scope}{.shared::cluster}.b64            _,     [addr] {, count};\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared{::cta}}.b64    state, [addr], tx_count;\nmbarrier.arrive_drop.expect_tx{.sem.scope}{.shared::cluster}.b64  _,     [addr], tx_count;\nmbarrier.arrive_drop.noComplete{.release.cta}{.shared{::cta}}.b64 state, [addr], count;\n\n.sem   = { .release, .relaxed }\n.scope = { .cta, .cluster }"
                ),
                Version(7, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Shared
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.SharedCluster
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.ExpectTx), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Shared
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.ExpectTx), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.SharedCluster
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.NoComplete), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(Modifier.Cta, Modifier.Release, Modifier.Shared),
                    false
                  )
                )
              )
            ))
        )
    case MbarrierCheckLayout extends Definition(
          "mbarrier.check_layout",
          Opcode.Mbarrier,
          Vec(Modifier.CheckLayout),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.MbarrierCheckLayout(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.check_layout", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-check-layout",
                "9.7.14.16.21. Parallel Synchronization and Communication Instructions: mbarrier.check_layout",
                "mbarrier.check_layout",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mbarrier.check_layout.layout{.ss}.b64 p, [addr];\n\n.layout = { .layout::v0, .layout::v1 }\n.ss = { .shared::cta }"
                ),
                Version(9, 3),
                Target(90),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(ModifierGroup(Modifier.LayoutV0, Modifier.LayoutV1), ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.SharedCta),
                  true
                ))
              )
            ))
        )
    case MbarrierCompleteTx extends Definition(
          "mbarrier.complete_tx",
          Opcode.Mbarrier,
          Vec(Modifier.CompleteTx),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.MbarrierCompleteTx(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.complete_tx", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-complete-tx",
                "9.7.14.16.15. Parallel Synchronization and Communication Instructions: mbarrier.complete_tx",
                "mbarrier.complete_tx",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mbarrier.complete_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"
                ),
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.Cluster, Modifier.Cta, Modifier.Relaxed),
                  false
                ))
              )
            ))
        )
    case MbarrierExpectTx extends Definition(
          "mbarrier.expect_tx",
          Opcode.Mbarrier,
          Vec(Modifier.ExpectTx),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.MbarrierExpectTx(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.expect_tx", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-expect-tx",
                "9.7.14.16.14. Parallel Synchronization and Communication Instructions: mbarrier.expect_tx",
                "mbarrier.expect_tx",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mbarrier.expect_tx{.sem.scope}{.space}.b64 [addr], txCount;\n\n.sem   = { .relaxed }\n.scope = { .cta, .cluster }\n.space = { .shared{::cta}, .shared::cluster }"
                ),
                Version(8, 0),
                Target(90),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.Cluster, Modifier.Cta, Modifier.Relaxed),
                  false
                ))
              )
            ))
        )
    case MbarrierInit extends Definition(
          "mbarrier.init",
          Opcode.Mbarrier,
          Vec(Modifier.Init),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.MbarrierInit(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.init", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-init",
                "9.7.14.16.12. Parallel Synchronization and Communication Instructions: mbarrier.init",
                "mbarrier.init",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mbarrier.init{.layout}{.shared{::cta}}.b64 [addr], count;\n\n.layout = { .layout::v0, .layout::v1 }"
                ),
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.LayoutV0, Modifier.LayoutV1, Modifier.Shared),
                  false
                ))
              )
            ))
        )
    case MbarrierInval extends Definition(
          "mbarrier.inval",
          Opcode.Mbarrier,
          Vec(Modifier.Inval),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.MbarrierInval(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.inval", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-inval",
                "9.7.14.16.13. Parallel Synchronization and Communication Instructions: mbarrier.inval",
                "mbarrier.inval",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("mbarrier.inval{.shared{::cta}}.b64 [addr];"),
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.Shared),
                  false
                ))
              )
            ))
        )
    case MbarrierPendingCount extends Definition(
          "mbarrier.pending_count",
          Opcode.Mbarrier,
          Vec(Modifier.PendingCount),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.MbarrierPendingCount(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.pending_count", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-pending-count",
                "9.7.14.16.20. Parallel Synchronization and Communication Instructions: mbarrier.pending_count",
                "mbarrier.pending_count",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mbarrier.pending_count{.layout}.b64 count, state;\n\n.layout = { .layout::v0 }"
                ),
                Version(7, 0),
                Target(80),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.LayoutV0),
                  false
                ))
              )
            ))
        )
    case MbarrierTestWait extends Definition(
          "mbarrier.test_wait",
          Opcode.Mbarrier,
          Vec(Modifier.TestWait),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4) Right(if (operands.size == 3)
              Instruction.MbarrierTestWait(operands(0), None, operands(1), operands(2), mods, pred)
            else Instruction.MbarrierTestWait(operands(0), Some(operands(1)), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.test_wait", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-test-wait-try-wait",
                "9.7.14.16.19. Parallel Synchronization and Communication Instructions: mbarrier.test_wait / mbarrier.try_wait",
                "mbarrier.test_wait / mbarrier.try_wait",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// without parity\nmbarrier.test_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state;\n\nmbarrier.test_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate\n                                                                   {, reportValue}, [addr], state;\n\n// with parity\nmbarrier.test_wait.parity{.phase_type}{.sem.scope}{.ss}.b64        waitComplete, [addr], phaseParity;\n\nmbarrier.test_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64  waitComplete|reportPredicate\n                                                                      {, reportValue}, [addr], phaseParity;\n\n// without parity\nmbarrier.try_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state {, timeHint};\n\nmbarrier.try_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate {, reportValue},\n                                                                  [addr], state {, timeHint};\n\n// with parity\nmbarrier.try_wait.parity{.phase_type}{.sem.scope}{.ss}.b64            waitComplete, [addr], phaseParity {, timeHint};\n\nmbarrier.try_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64   waitComplete|reportPredicate {, reportValue},\n                                                                      [addr], phaseParity {, timeHint};\n\n\n.ss   = { .shared{::cta} }\n.sem   = { .acquire, .relaxed }\n.scope = { .cta, .cluster }\n.phase_type = { .phase_type::primary, .phase_type::conditional }"
                ),
                Version(1, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.PhaseTypePrimary,
                      Modifier.Relaxed
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.PhaseTypePrimary), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Parity), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.PhaseTypeConditional,
                      Modifier.PhaseTypePrimary,
                      Modifier.Relaxed
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Parity),
                      ModifierGroup(Modifier.PhaseTypePrimary),
                      ModifierGroup(Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed
                    ),
                    false
                  )
                )
              )
            ))
        )
    case MbarrierTryWait extends Definition(
          "mbarrier.try_wait",
          Opcode.Mbarrier,
          Vec(Modifier.TryWait),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 3 || operands.size == 4 || operands.size == 5) Right(if (operands.size == 3)
              Instruction.MbarrierTryWait(operands(0), None, operands(1), operands(2), None, mods, pred)
            else if (operands.size == 4 && !Operand.isWithPredicate(operands(0)))
              Instruction.MbarrierTryWait(operands(0), None, operands(1), operands(2), Some(operands(3)), mods, pred)
            else Instruction.MbarrierTryWait(
              operands(0),
              Some(operands(1)),
              operands(2),
              operands(3),
              operands.lift(4),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("mbarrier.try_wait", Vec(3, 4, 5), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-mbarrier-test-wait-try-wait",
                "9.7.14.16.19. Parallel Synchronization and Communication Instructions: mbarrier.test_wait / mbarrier.try_wait",
                "mbarrier.test_wait / mbarrier.try_wait",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// without parity\nmbarrier.test_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state;\n\nmbarrier.test_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate\n                                                                   {, reportValue}, [addr], state;\n\n// with parity\nmbarrier.test_wait.parity{.phase_type}{.sem.scope}{.ss}.b64        waitComplete, [addr], phaseParity;\n\nmbarrier.test_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64  waitComplete|reportPredicate\n                                                                      {, reportValue}, [addr], phaseParity;\n\n// without parity\nmbarrier.try_wait{.phase_type::primary}{.sem.scope}{.ss}.b64      waitComplete, [addr], state {, timeHint};\n\nmbarrier.try_wait.phase_type::primary{.sem}{.scope}{.ss}.b64      waitComplete|reportPredicate {, reportValue},\n                                                                  [addr], state {, timeHint};\n\n// with parity\nmbarrier.try_wait.parity{.phase_type}{.sem.scope}{.ss}.b64            waitComplete, [addr], phaseParity {, timeHint};\n\nmbarrier.try_wait.parity.phase_type::primary{.sem}{.scope}{.ss}.b64   waitComplete|reportPredicate {, reportValue},\n                                                                      [addr], phaseParity {, timeHint};\n\n\n.ss   = { .shared{::cta} }\n.sem   = { .acquire, .relaxed }\n.scope = { .cta, .cluster }\n.phase_type = { .phase_type::primary, .phase_type::conditional }"
                ),
                Version(1, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.PhaseTypePrimary,
                      Modifier.Relaxed
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.PhaseTypePrimary), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.Parity), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.PhaseTypeConditional,
                      Modifier.PhaseTypePrimary,
                      Modifier.Relaxed
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Parity),
                      ModifierGroup(Modifier.PhaseTypePrimary),
                      ModifierGroup(Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed
                    ),
                    false
                  )
                )
              )
            ))
        )
    case Membar extends Definition(
          "membar",
          Opcode.Membar,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.Membar(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("membar", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-membar",
                "9.7.14.4. Parallel Synchronization and Communication Instructions: membar / fence",
                "membar / fence",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// Thread fence:\nfence{.sem}.scope;\n\n// Thread fence:\nfence.acquire.sync_restrict::shared::cluster.cluster;\nfence.release.sync_restrict::shared::cta.cluster;\n\n// Operation fence:\nfence.op_restrict.release.cluster;\n\n// Proxy fence (bi-directional):\nfence.proxy.proxykind;\n\n// Proxy fence (uni-directional):\nfence.proxy.to_proxykind::from_proxykind.release.scope;\nfence.proxy.to_proxykind::from_proxykind.acquire.scope  [addr], size;\nfence.proxy.async::generic.acquire.sync_restrict::shared::cluster.cluster;\nfence.proxy.async::generic.release.sync_restrict::shared::cta.cluster;\nfence.proxy.to_proxykind::from_proxykind_fabric.alias.sem_fabric.sys;\n\n// Old style membar:\nmembar.level;\nmembar.proxy.proxykind;\n\n.sem       = { .sc, .acq_rel, .acquire, .release };\n.scope     = { .cta, .cluster, .gpu, .sys };\n.level     = { .cta, .gl, .sys };\n.proxykind = { .alias, .async, .async.global, .async.shared::{cta, cluster} };\n.op_restrict = { .mbarrier_init };\n.to_proxykind::from_proxykind = {.tensormap::generic};\n.to_proxykind::from_proxykind_fabric = {.generic::fabric, .fabric::generic, .fabric::fabric };\n.sem_fabric = { .acquire, .release };"
                ),
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Cta, Modifier.Gl, Modifier.Sys)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Proxy), ModifierGroup(Modifier.Alias, Modifier.Async, Modifier.Cluster)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Min extends Definition(
          "min",
          Opcode.Min,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Min(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("min", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-min",
                "9.7.1.13. Integer Arithmetic Instructions: min",
                "min",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "min.type1         d, a, b;\nmin{.relu}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .u16x2, .u8x4, .s16, .s64 };\n.type2 = { .s16x2, .s32, .s8x4 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.S16,
                      Modifier.S64,
                      Modifier.U16,
                      Modifier.U16x2,
                      Modifier.U32,
                      Modifier.U64,
                      Modifier.U8x4
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S16x2, Modifier.S32, Modifier.S8x4)),
                    _root_.scala.collection.immutable.Set(Modifier.Relu),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "floating-point-instructions-min",
                "9.7.3.11. Floating Point Instructions: min",
                "min",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "min{.ftz}{.NaN}{.xorsign.abs}.f32  d, a, b;\nmin{.ftz}{.NaN}{.abs}.f32          d, a, b, c;\nmin.f64                            d, a, b;"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-min",
                "9.7.4.7. Half Precision Floating Point Instructions: min",
                "min",
                "Half Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "min{.ftz}{.NaN}{.xorsign.abs}.f16      d, a, b;\nmin{.ftz}{.NaN}{.xorsign.abs}.f16x2    d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16           d, a, b;\nmin{.NaN}{.xorsign.abs}.bf16x2         d, a, b;"
                ),
                Version(7, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Xorsign),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Xorsign),
                    false
                  )
                )
              )
            ))
        )
    case Mma extends Definition(
          "mma",
          Opcode.Mma,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 4 && (operands.size == 4 || operands.size == 6 || operands.size == 7 || operands.size == 8 || operands.size == 9 || operands.size == 10)
            ) Right(Instruction.Mma(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              operands.lift(7),
              operands.lift(8),
              operands.lift(9),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("mma", Vec(4, 6, 7, 8, 9, 10), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "warp-level-matrix-instructions-mma",
                "9.7.15.5.14. Multiply-and-Accumulate Instruction: mma",
                "mma",
                "Multiply-and-Accumulate Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mma.sync.aligned.m8n8k4.alayout.blayout.dtype.f16.f16.ctype  d, a, b, c;\nmma.sync.aligned.m16n8k8.row.col.dtype.f16.f16.ctype  d, a, b, c;\nmma.sync.aligned.m16n8k16.row.col.dtype.f16.f16.ctype d, a, b, c;\n\n.alayout = {.row, .col};\n.blayout = {.row, .col};\n.ctype   = {.f16, .f32};\n.dtype   = {.f16, .f32};",
                  "mma.sync.aligned.m16n8k4.row.col.f32.tf32.tf32.f32        d, a, b, c;\nmma.sync.aligned.m16n8k8.row.col.f32.atype.btype.f32      d, a, b, c;\nmma.sync.aligned.m16n8k16.row.col.f32.bf16.bf16.f32       d, a, b, c;\nmma.sync.aligned.shape.row.col.dtype.f8type.f8type.ctype  d, a, b, c;\nmma.sync.aligned.m16n8k32.row.col.kind.dtype.f8f6f4type.f8f6f4type.ctype d, a, b, c;\n\n.atype      = {.bf16, .tf32};\n.btype      = {.bf16, .tf32};\n.f8type     = {.e4m3, .e5m2};\n.f8f6f4type = {.e4m3, .e5m2, .e3m2, .e2m3, .e2m1};\n.ctype      = {.f16, .f32};\n.dtype      = {.f16, .f32};\n.shape      = {.m16n8k16, .m16n8k32};\n.kind       = {.kind::f8f6f4};",
                  "mma.sync.aligned.m16n8k64.row.col.kind.block_scale{.scale_vec_size}.f32.e2m1.e2m1.f32.stype d, a, b, c, scale-a-data, {byte-id-a, thread-id-a}, scale-b-data, {byte-id-b, thread-id-b};\n\n.kind           = {.kind::mxf4};\n.scale_vec_size = {.scale_vec::2X};\n.stype          = {.ue8m0};\n\nmma.sync.aligned.m16n8k64.row.col.kind.block_scale.scale_vec_size.f32.e2m1.e2m1.f32.stype d, a, b, c, scale-a-data, {byte-id-a, thread-id-a}, scale-b-data, {byte-id-b, thread-id-b};\n\n.kind           = {.kind::mxf4nvf4};\n.scale_vec_size = {.scale_vec::2X, .scale_vec::4X};\n.stype          = {.ue8m0, .ue4m3};\n\nmma.sync.aligned.m16n8k32.row.col.kind.block_scale{.scale_vec_size}.f32.f8f6f4type.f8f6f4type.f32.stype d, a, b, c, scale-a-data, {byte-id-a, thread-id-a}, scale-b-data, {byte-id-b, thread-id-b};\n\n.kind           = {.kind::mxf8f6f4};\n.scale_vec_size = {.scale_vec::1X};\n.f8f6f4type     = {.e4m3, .e5m2, .e3m2, .e2m3, .e2m1};\n.stype          = {.ue8m0};",
                  "mma.sync.aligned.shape.row.col.f64.f64.f64.f64 d, a, b, c;\n\n.shape   = {.m8n84, .m16n8k4, .m16n8k8, .m16n8k16};",
                  "mma.sync.aligned.shape.row.col{.satfinite}.s32.atype.btype.s32 d, a, b, c;\n\n.shape   = {.m8n8k16, .m16n8k16, .m16n8k32}\n.atype   = {.u8, .s8};\n.btype   = {.u8, .s8};\n\nmma.sync.aligned.shape.row.col{.satfinite}.s32.atype.btype.s32 d, a, b, c;\n\n.shape   = {.m8n8k32, .m16n8k32, .m16n8k64}\n.atype   = {.u4, .s4};\n.btype   = {.u4, .s4};",
                  "mma.sync.aligned.shape.row.col.s32.b1.b1.s32.bitOp.popc d, a, b, c;\n\n.bitOp = {.xor, .and}\n.shape = {.m8n8k128, .m16n8k128, .m16n8k256}"
                ),
                Version(6, 4),
                Target(70),
                Vec(
                  metadataMma_170_0(),
                  metadataMma_170_1(),
                  metadataMma_170_2(),
                  metadataMma_170_3(),
                  metadataMma_170_4(),
                  metadataMma_170_5(),
                  metadataMma_170_6(),
                  metadataMma_170_7(),
                  metadataMma_170_8(),
                  metadataMma_170_9(),
                  metadataMma_170_10(),
                  metadataMma_170_11(),
                  metadataMma_170_12(),
                  metadataMma_170_13()
                )
              )
            ))
        )
    case MmaSpOrderedMetadata extends Definition(
          "mma.sp::ordered_metadata",
          Opcode.Mma,
          Vec(Modifier.SpOrderedMetadata),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 6 && (operands.size == 6 || operands.size == 8 || operands.size == 9 || operands.size == 10 || operands.size == 11 || operands.size == 12)
            ) Right(Instruction.MmaSpOrderedMetadata(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              operands(5),
              operands.lift(6),
              operands.lift(7),
              operands.lift(8),
              operands.lift(9),
              operands.lift(10),
              operands.lift(11),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "mma.sp::ordered_metadata",
              Vec(6, 8, 9, 10, 11, 12),
              operands.size
            ))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "warp-level-matrix-instructions-sparse-mma",
                "9.7.15.6.3. Multiply-and-Accumulate Instruction: mma.sp / mma.sp::ordered_metadata",
                "mma.sp / mma.sp::ordered_metadata",
                "Multiply-and-Accumulate Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mma.spvariant.sync.aligned.m16n8k16.row.col.dtype.f16.f16.ctype  d, a, b, c, e, f;\nmma.spvariant.sync.aligned.m16n8k32.row.col.dtype.f16.f16.ctype  d, a, b, c, e, f;\n\n.ctype     = {.f16, .f32};\n.dtype     = {.f16, .f32};\n.spvariant = {.sp, .sp::ordered_metadata};",
                  "mma.spvariant.sync.aligned.m16n8k16.row.col.f32.bf16.bf16.f32     d, a, b, c, e, f;\nmma.spvariant.sync.aligned.m16n8k32.row.col.f32.bf16.bf16.f32     d, a, b, c, e, f;\nmma.spvariant.sync.aligned.m16n8k8.row.col.f32.tf32.tf32.f32      d, a, b, c, e, f;\nmma.spvariant.sync.aligned.m16n8k16.row.col.f32.tf32.tf32.f32     d, a, b, c, e, f;\nmma.spvariant.sync.aligned.m16n8k64.row.col.f32.f8type.f8type.f32 d, a, b, c, e, f;\nmma.sp::ordered_metadata.sync.aligned.m16n8k64.row.col.kind.dtype.f8f6f4type.f8f6f4type.ctype d, a, b, c, e, f;\n\n.f8type     = {.e4m3, .e5m2};\n.spvariant  = {.sp, .sp::ordered_metadata};\n.f8f6f4type = {.e4m3, .e5m2, .e3m2, .e2m3, .e2m1};\n.kind       = {kind::f8f6f4};\n.ctype      = {.f16, .f32};\n.dtype      = {.f16, .f32};",
                  "mma.spvariant.sync.aligned.m16n8k128.row.col.kind.block_scale{.scale_vec_size}.f32.e2m1.e2m1.f32.stype d, a, b, c, e, f, scale-a-data, {byte-id-a, thread-id-a}, scale-b-data, {byte-id-b, thread-id-b};\n\n.spvariant      = {.sp::ordered_metadata};\n.kind           = {.kind::mxf4};\n.scale_vec_size = {.scale_vec::2X};\n.stype          = {.ue8m0};\n\nmma.spvariant.sync.aligned.m16n8k128.row.col.kind.block_scale.scale_vec_size.f32.e2m1.e2m1.f32.stype d, a, b, c, e, f, scale-a-data, {byte-id-a, thread-id-a}, scale-b-data, {byte-id-b, thread-id-b};\n\n.spvariant      = {.sp::ordered_metadata};\n.kind           = {.kind::mxf4nvf4};\n.scale_vec_size = {.scale_vec::2X, .scale_vec::4X};\n.stype          = {.ue8m0, .ue4m3};\n\nmma.spvariant.sync.aligned.m16n8k64.row.col.kind.block_scale{.scale_vec_size}.f32.f8f6f4type.f8f6f4type.f32.stype d, a, b, c, e, f, scale-a-data, {byte-id-a, thread-id-a}, scale-b-data, {byte-id-b, thread-id-b};\n\n.spvariant      = {.sp::ordered_metadata};\n.kind           = {.kind::mxf8f6f4};\n.scale_vec_size = {.scale_vec::1X};\n.f8f6f4type     = {.e4m3, .e5m2, .e3m2, .e2m3, .e2m1};\n.stype          = {.ue8m0};",
                  "mma.spvariant.sync.aligned.shape.row.col{.satfinite}.s32.atype.btype.s32 d, a, b, c, e, f;\n\n.shape     = {.m16n8k32, .m16n8k64}\n.atype     = {.u8, .s8};\n.btype     = {.u8, .s8};\n.spvariant = {.sp, .sp::ordered_metadata};\n\nmma.spvariant.sync.aligned.shape.row.col{.satfinite}.s32.atype.btype.s32 d, a, b, c, e, f;\n\n.shape     = {.m16n8k64, .m16n8k128}\n.atype     = {.u4, .s4};\n.btype     = {.u4, .s4};\n.spvariant = {.sp, .sp::ordered_metadata};"
                ),
                Version(7, 1),
                Target(80),
                Vec(
                  metadataMmaSpOrderedMetadata_174_0(),
                  metadataMmaSpOrderedMetadata_174_1(),
                  metadataMmaSpOrderedMetadata_174_2(),
                  metadataMmaSpOrderedMetadata_174_3(),
                  metadataMmaSpOrderedMetadata_174_4(),
                  metadataMmaSpOrderedMetadata_174_5(),
                  metadataMmaSpOrderedMetadata_174_6(),
                  metadataMmaSpOrderedMetadata_174_7(),
                  metadataMmaSpOrderedMetadata_174_8(),
                  metadataMmaSpOrderedMetadata_174_9(),
                  metadataMmaSpOrderedMetadata_174_10(),
                  metadataMmaSpOrderedMetadata_174_11()
                )
              )
            ))
        )
    case Mov extends Definition(
          "mov",
          Opcode.Mov,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Mov(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mov", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-mov",
                "9.7.9.3. Data Movement and Conversion Instructions: mov",
                "mov",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mov.type  d, a;\nmov.type  d, sreg;\nmov.type  d, avar;       // get address of variable\nmov.type  d, avar+imm;   // get address of variable with offset\nmov.u32   d, fname;      // get address of device function\nmov.u64   d, fname;      // get address of device function\nmov.u32   d, kernel;     // get address of entry function\nmov.u64   d, kernel;     // get address of entry function\n\n.type = { .pred,\n          .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.Pred,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.Pred,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.Pred,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.Pred,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64
                    )),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "data-movement-and-conversion-instructions-mov-2",
                "9.7.9.4. Data Movement and Conversion Instructions: mov",
                "mov",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("mov.type  d, a;\n\n.type = { .b16, .b32, .b64, .b128 };"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B128, Modifier.B16, Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case Movmatrix extends Definition(
          "movmatrix",
          Opcode.Movmatrix,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Movmatrix(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("movmatrix", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "warp-level-matrix-instructions-movmatrix",
                "9.7.15.5.17. Warp-level matrix transpose instruction: movmatrix",
                "movmatrix",
                "Warp-level matrix transpose instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "movmatrix.sync.aligned.shape.trans.type d, a;\n\n.shape  = {.m8n8};\n.type   = {.b16};"
                ),
                Version(7, 8),
                Target(75),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Sync),
                    ModifierGroup(Modifier.Aligned),
                    ModifierGroup(Modifier.M8n8),
                    ModifierGroup(Modifier.Trans),
                    ModifierGroup(Modifier.B16)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Mul extends Definition(
          "mul",
          Opcode.Mul,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Mul(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mul", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-mul",
                "9.7.1.3. Integer Arithmetic Instructions: mul",
                "mul",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mul.mode.type  d, a, b;\n\n.mode = { .hi, .lo, .wide };\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Hi, Modifier.Lo, Modifier.Wide),
                    ModifierGroup(Modifier.S16, Modifier.S32, Modifier.S64, Modifier.U16, Modifier.U32, Modifier.U64)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                "floating-point-instructions-mul",
                "9.7.3.5. Floating Point Instructions: mul",
                "mul",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mul{.rnd}{.ftz}{.sat}.f32  d, a, b;\nmul{.rnd}{.ftz}.f32x2      d, a, b;\nmul{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Ftz,
                      Modifier.Rm,
                      Modifier.Rn,
                      Modifier.Rp,
                      Modifier.Rz,
                      Modifier.Sat
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32x2)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Ftz,
                      Modifier.Rm,
                      Modifier.Rn,
                      Modifier.Rp,
                      Modifier.Rz
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-mul",
                "9.7.4.3. Half Precision Floating Point Instructions: mul",
                "mul",
                "Half Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mul{.rnd}{.ftz}{.sat}.f16   d, a, b;\nmul{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nmul{.rnd}.bf16   d, a, b;\nmul{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
                ),
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    false
                  )
                )
              )
            ))
        )
    case Mul24 extends Definition(
          "mul24",
          Opcode.Mul24,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Mul24(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("mul24", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-mul24",
                "9.7.1.6. Integer Arithmetic Instructions: mul24",
                "mul24",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "mul24.mode.type  d, a, b;\n\n.mode = { .hi, .lo };\n.type = { .u32, .s32 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Hi, Modifier.Lo), ModifierGroup(Modifier.S32, Modifier.U32)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case MultimemCpAsyncBulk extends Definition(
          "multimem.cp.async.bulk",
          Opcode.Multimem,
          Vec(Modifier.Cp, Modifier.Async, Modifier.Bulk),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4)) Right(Instruction.MultimemCpAsyncBulk(
              operands(0),
              operands(1),
              operands(2),
              operands.lift(3),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.cp.async.bulk", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem-cp-async-bulk",
                "9.7.9.26.4.4. Data Movement and Conversion Instructions: multimem.cp.async.bulk",
                "multimem.cp.async.bulk",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "multimem.cp.async.bulk{.sem}.dst.src.completion_mechanism{.cp_mask}\n    [dstMem], [srcMem], size{, byteMask};\n\n    .sem                  = { .weak }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n\nmultimem.cp.async.bulk.sem.scope.dst.src.completion_mechanism{.cp_mask}.type\n    [dstMem], [srcMem], size{, byteMask};\n\n    .sem                  = { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .type                 = { .b128 }"
                ),
                Version(9, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CpMask, Modifier.Relaxed),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CpMask),
                    false
                  )
                )
              )
            ))
        )
    case MultimemCpReduceAsyncBulk extends Definition(
          "multimem.cp.reduce.async.bulk",
          Opcode.Multimem,
          Vec(Modifier.Cp, Modifier.Reduce, Modifier.Async, Modifier.Bulk),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.MultimemCpReduceAsyncBulk(operands(0), operands(1), operands(2), mods, pred))
            else
              Left(Instruction.DecodeError.InvalidOperandCount("multimem.cp.reduce.async.bulk", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem-cp-reduce-async-bulk",
                "9.7.9.26.4.5. Data Movement and Conversion Instructions: multimem.cp.reduce.async.bulk",
                "multimem.cp.reduce.async.bulk",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "multimem.cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.redOp.type  [dstMem], [srcMem], size;\n\n    .sem                  =  { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .redOp                = { .and, .or, .xor,\n                              .add, .inc, .dec,\n                              .min, .max }\n    .type                 = { .f16, .bf16,\n                              .b32, .u32, .s32,\n                              .b64, .u64, .s64,\n                              .f32, .f64 }\n\nmultimem.cp.reduce.async.bulk{.sem.scope}.dst.src.completion_mechanism.add.noftz.type  [dstMem], [srcMem], size;\n\n    .sem                  = { .relaxed }\n    .scope                = { .cta, .cluster, .gpu, .sys }\n    .dst                  = { .global }\n    .src                  = { .shared::cta }\n    .completion_mechanism = { .bulk_group }\n    .type                 = { .f16, .bf16 }"
                ),
                Version(9, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(
                        Modifier.Add,
                        Modifier.And,
                        Modifier.Dec,
                        Modifier.Inc,
                        Modifier.Max,
                        Modifier.Min,
                        Modifier.Or,
                        Modifier.Xor
                      ),
                      ModifierGroup(Modifier.Bf16, Modifier.F16)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys
                    ),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(Modifier.Noftz),
                      ModifierGroup(Modifier.Bf16, Modifier.F16)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys
                    ),
                    true
                  )
                )
              )
            ))
        )
    case MultimemLdReduce extends Definition(
          "multimem.ld_reduce",
          Opcode.Multimem,
          Vec(Modifier.LdReduce),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.MultimemLdReduce(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.ld_reduce", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem",
                "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red",
                "multimem.ld_reduce, multimem.st, multimem.red",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
                ),
                Version(8, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min),
                      ModifierGroup(
                        Modifier.Bf16,
                        Modifier.Bf16x2,
                        Modifier.E4m3,
                        Modifier.E4m3x2,
                        Modifier.E4m3x4,
                        Modifier.E5m2,
                        Modifier.E5m2x2,
                        Modifier.E5m2x4,
                        Modifier.F16,
                        Modifier.F16x2,
                        Modifier.F32,
                        Modifier.F64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Weak),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min),
                      ModifierGroup(
                        Modifier.Bf16,
                        Modifier.Bf16x2,
                        Modifier.E4m3,
                        Modifier.E4m3x2,
                        Modifier.E4m3x4,
                        Modifier.E5m2,
                        Modifier.E5m2x2,
                        Modifier.E5m2x4,
                        Modifier.F16,
                        Modifier.F16x2,
                        Modifier.F32,
                        Modifier.F64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min),
                      ModifierGroup(
                        Modifier.Bf16,
                        Modifier.Bf16x2,
                        Modifier.E4m3,
                        Modifier.E4m3x2,
                        Modifier.E4m3x4,
                        Modifier.E5m2,
                        Modifier.E5m2x2,
                        Modifier.E5m2x4,
                        Modifier.F16,
                        Modifier.F16x2,
                        Modifier.F32,
                        Modifier.F64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.AccF16,
                      Modifier.AccF32,
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Weak),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min),
                      ModifierGroup(
                        Modifier.Bf16,
                        Modifier.Bf16x2,
                        Modifier.E4m3,
                        Modifier.E4m3x2,
                        Modifier.E4m3x4,
                        Modifier.E5m2,
                        Modifier.E5m2x2,
                        Modifier.E5m2x4,
                        Modifier.F16,
                        Modifier.F16x2,
                        Modifier.F32,
                        Modifier.F64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.AccF16,
                      Modifier.AccF32,
                      Modifier.Global,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8
                    ),
                    false
                  )
                )
              )
            ))
        )
    case MultimemRed extends Definition(
          "multimem.red",
          Opcode.Multimem,
          Vec(Modifier.Red),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.MultimemRed(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.red", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem",
                "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red",
                "multimem.ld_reduce, multimem.st, multimem.red",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
                ),
                Version(8, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min),
                      ModifierGroup(
                        Modifier.Bf16,
                        Modifier.Bf16x2,
                        Modifier.E4m3,
                        Modifier.E4m3x2,
                        Modifier.E4m3x4,
                        Modifier.E5m2,
                        Modifier.E5m2x2,
                        Modifier.E5m2x4,
                        Modifier.F16,
                        Modifier.F16x2,
                        Modifier.F32,
                        Modifier.F64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(
                        Modifier.Bf16,
                        Modifier.Bf16x2,
                        Modifier.F16,
                        Modifier.F16x2,
                        Modifier.F32,
                        Modifier.F64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8
                    ),
                    false
                  )
                )
              )
            ))
        )
    case MultimemRedAsync extends Definition(
          "multimem.red.async",
          Opcode.Multimem,
          Vec(Modifier.Red, Modifier.Async),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.MultimemRedAsync(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.red.async", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-multimem-red-async",
                "9.7.14.8. Parallel Synchronization and Communication Instructions: multimem.red.async",
                "multimem.red.async",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "multimem.red.async.sem.scope{.ss}.op.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.op    = { .add };\n.type  = { .u32, .s32, .u64 };"
                ),
                Version(9, 3),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Release),
                    ModifierGroup(Modifier.Gpu, Modifier.Sys),
                    ModifierGroup(Modifier.Add),
                    ModifierGroup(Modifier.S32, Modifier.U32, Modifier.U64)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Global),
                  true
                ))
              )
            ))
        )
    case MultimemSt extends Definition(
          "multimem.st",
          Opcode.Multimem,
          Vec(Modifier.St),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.MultimemSt(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.st", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem",
                "9.7.9.15. Data Movement and Conversion Instructions: multimem.ld_reduce, multimem.st, multimem.red",
                "multimem.ld_reduce, multimem.st, multimem.red",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// Integer type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op.type      d, [a];\nmultimem.ld_reduce.weak{.ss}.op.type                 d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}.type                [a], b;\nmultimem.st.weak{.ss}.type                           [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.op.type           [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add, .and, .or, .xor }\n.type =     { .b32, .b64,  .u32, .u64, .s32, .s64 }\n\n// Floating point type:\n\nmultimem.ld_reduce{.ldsem}{.scope}{.ss}.op{.acc_prec}{.vec}.type    d, [a];\nmultimem.ld_reduce.weak{.ss}.op{.acc_prec}{.vec}.type               d, [a];\n\nmultimem.st{.stsem}{.scope}{.ss}{.vec}.type                         [a], b;\nmultimem.st.weak{.ss}{.vec}.type                                    [a], b;\n\nmultimem.red{.redsem}{.scope}{.ss}.redop{.vec}.redtype              [a], b;\n\n.ss =       { .global }\n.ldsem =    { .relaxed, .acquire }\n.stsem =    { .relaxed, .release }\n.redsem =   { .relaxed, .release }\n.scope =    { .cta, .cluster, .gpu, .sys }\n.op  =      { .min, .max, .add }\n.redop  =   { .add }\n.acc_prec = { .acc::f32, .acc::f16 }\n.vec =      { .v2, .v4, .v8 }\n.type=      { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64, .e5m2, .e5m2x2, .e5m2x4, .e4m3, .e4m3x2, .e4m3x4 }\n.redtype =  { .f16, .f16x2, .bf16, .bf16x2, .f32, .f64 }"
                ),
                Version(8, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.Bf16,
                      Modifier.Bf16x2,
                      Modifier.E4m3,
                      Modifier.E4m3x2,
                      Modifier.E4m3x4,
                      Modifier.E5m2,
                      Modifier.E5m2x2,
                      Modifier.E5m2x4,
                      Modifier.F16,
                      Modifier.F16x2,
                      Modifier.F32,
                      Modifier.F64
                    )),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Weak),
                      ModifierGroup(
                        Modifier.Bf16,
                        Modifier.Bf16x2,
                        Modifier.E4m3,
                        Modifier.E4m3x2,
                        Modifier.E4m3x4,
                        Modifier.E5m2,
                        Modifier.E5m2x2,
                        Modifier.E5m2x4,
                        Modifier.F16,
                        Modifier.F16x2,
                        Modifier.F32,
                        Modifier.F64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.Bf16,
                      Modifier.Bf16x2,
                      Modifier.E4m3,
                      Modifier.E4m3x2,
                      Modifier.E4m3x4,
                      Modifier.E5m2,
                      Modifier.E5m2x2,
                      Modifier.E5m2x4,
                      Modifier.F16,
                      Modifier.F16x2,
                      Modifier.F32,
                      Modifier.F64
                    )),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Weak),
                      ModifierGroup(
                        Modifier.Bf16,
                        Modifier.Bf16x2,
                        Modifier.E4m3,
                        Modifier.E4m3x2,
                        Modifier.E4m3x4,
                        Modifier.E5m2,
                        Modifier.E5m2x2,
                        Modifier.E5m2x4,
                        Modifier.F16,
                        Modifier.F16x2,
                        Modifier.F32,
                        Modifier.F64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.V2, Modifier.V4, Modifier.V8),
                    false
                  )
                )
              )
            ))
        )
    case MultimemStAsync extends Definition(
          "multimem.st.async",
          Opcode.Multimem,
          Vec(Modifier.St, Modifier.Async),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.MultimemStAsync(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("multimem.st.async", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-multimem-st-async",
                "9.7.9.13. Data Movement and Conversion Instructions: multimem.st.async",
                "multimem.st.async",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "multimem.st.async.sem.scope{.ss}.type [a], b;\n\n.sem   = { .release };\n.scope = { .gpu, .sys };\n.ss    = { .global };\n.type  = { .b8, .b16, .b32, .b64,\n            .u8, .u16, .u32, .u64,\n            .s8, .s16, .s32, .s64,\n            .f32, .f64 };"
                ),
                Version(9, 3),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Release),
                    ModifierGroup(Modifier.Gpu, Modifier.Sys),
                    ModifierGroup(
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.B8,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.S8,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64,
                      Modifier.U8
                    )
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Global),
                  true
                ))
              )
            ))
        )
    case NanoSleep extends Definition(
          "nanosleep",
          Opcode.NanoSleep,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.NanoSleep(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("nanosleep", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "miscellaneous-instructions-nanosleep",
                "9.7.20.2. Miscellaneous Instructions: nanosleep",
                "nanosleep",
                "Miscellaneous Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("nanosleep.u32 t;"),
                Version(1, 0),
                Target(70),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.U32)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Neg extends Definition(
          "neg",
          Opcode.Neg,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Neg(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("neg", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-neg",
                "9.7.1.12. Integer Arithmetic Instructions: neg",
                "neg",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("neg.type  d, a;\n\n.type = { .s8x4, .s16, .s32, .s64 };"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S16, Modifier.S32, Modifier.S64, Modifier.S8x4)),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              ),
              Metadata.Entry(
                "floating-point-instructions-neg",
                "9.7.3.10. Floating Point Instructions: neg",
                "neg",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("neg{.ftz}.f32  d, a;\nneg.f64        d, a;"),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-neg",
                "9.7.4.5. Half Precision Floating Point Instructions: neg",
                "neg",
                "Half Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "neg{.ftz}.f16    d, a;\nneg{.ftz}.f16x2  d, a;\nneg.bf16         d, a;\nneg.bf16x2       d, a;"
                ),
                Version(6, 0),
                Target(53),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Not extends Definition(
          "not",
          Opcode.Not,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Not(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("not", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "logic-and-shift-instructions-not",
                "9.7.8.4. Logic and Shift Instructions: not",
                "not",
                "Logic and Shift Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("not.type d, a;\n\n.type = { .pred, .b16, .b32, .b64 };"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.Pred)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Or extends Definition(
          "or",
          Opcode.Or,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Or(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("or", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "logic-and-shift-instructions-or",
                "9.7.8.2. Logic and Shift Instructions: or",
                "or",
                "Logic and Shift Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("or.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.Pred)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case PmEvent extends Definition(
          "pmevent",
          Opcode.PmEvent,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.PmEvent(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("pmevent", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "miscellaneous-instructions-pmevent",
                "9.7.20.3. Miscellaneous Instructions: pmevent",
                "pmevent",
                "Miscellaneous Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "pmevent       a;    // trigger a single performance monitor event\npmevent.mask  a;    // trigger one or more performance monitor events"
                ),
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(Vec(OperandConstraint.Value), Vec(), _root_.scala.collection.immutable.Set(), true),
                  InstructionForm(
                    Vec(OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Mask)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ))
        )
    case Popc extends Definition(
          "popc",
          Opcode.Popc,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Popc(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("popc", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-popc",
                "9.7.1.15. Integer Arithmetic Instructions: popc",
                "popc",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("popc.type  d, a;\n\n.type = { .b32, .b64 };"),
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Prefetch extends Definition(
          "prefetch",
          Opcode.Prefetch,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.Prefetch(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("prefetch", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-prefetch-prefetchu",
                "9.7.9.16. Data Movement and Conversion Instructions: prefetch, prefetchu",
                "prefetch, prefetchu",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "prefetch{.space}.level                    [a];   // prefetch to data cache\nprefetch.global.level::eviction_priority  [a];   // prefetch to data cache\n\nprefetchu.L1  [a];             // prefetch to uniform cache\n\nprefetch{.tensormap_space}.tensormap [a];  // prefetch the tensormap\n\n.space =                    { .global, .local };\n.level =                    { .L1, .L2 };\n.level::eviction_priority = { .L2::evict_last, .L2::evict_normal };\n.tensormap_space =          { .const, .param };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address),
                    Vec(ModifierGroup(Modifier.L1, Modifier.L2)),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Local),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address),
                    Vec(ModifierGroup(Modifier.Global), ModifierGroup(Modifier.L2EvictLast, Modifier.L2EvictNormal)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address),
                    Vec(ModifierGroup(Modifier.Tensormap)),
                    _root_.scala.collection.immutable.Set(Modifier.Const, Modifier.Param),
                    false
                  )
                )
              )
            ))
        )
    case Prefetchu extends Definition(
          "prefetchu",
          Opcode.Prefetchu,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.Prefetchu(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("prefetchu", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-prefetch-prefetchu",
                "9.7.9.16. Data Movement and Conversion Instructions: prefetch, prefetchu",
                "prefetch, prefetchu",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "prefetch{.space}.level                    [a];   // prefetch to data cache\nprefetch.global.level::eviction_priority  [a];   // prefetch to data cache\n\nprefetchu.L1  [a];             // prefetch to uniform cache\n\nprefetch{.tensormap_space}.tensormap [a];  // prefetch the tensormap\n\n.space =                    { .global, .local };\n.level =                    { .L1, .L2 };\n.level::eviction_priority = { .L2::evict_last, .L2::evict_normal };\n.tensormap_space =          { .const, .param };"
                ),
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(Modifier.L1)),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case Prmt extends Definition(
          "prmt",
          Opcode.Prmt,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Prmt(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("prmt", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-prmt",
                "9.7.9.7. Data Movement and Conversion Instructions: prmt",
                "prmt",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "prmt.b32{.mode}  d, a, b, c;\n\n.mode = { .f4e, .b4e, .rc8, .ecl, .ecr, .rc16 };"
                ),
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.B32)),
                  _root_.scala.collection.immutable.Set(
                    Modifier.B4e,
                    Modifier.Ecl,
                    Modifier.Ecr,
                    Modifier.F4e,
                    Modifier.Rc16,
                    Modifier.Rc8
                  ),
                  true
                ))
              )
            ))
        )
    case Rcp extends Definition(
          "rcp",
          Opcode.Rcp,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Rcp(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rcp", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-rcp",
                "9.7.3.13. Floating Point Instructions: rcp",
                "rcp",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "rcp.approx{.ftz}.f32  d, a;  // fast, approximate reciprocal\nrcp.rnd{.ftz}.f32     d, a;  // IEEE 754 compliant rounding\nrcp.rnd.f64           d, a;  // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case RcpApproxFtzF64 extends Definition(
          "rcp.approx.ftz.f64",
          Opcode.Rcp,
          Vec(Modifier.Approx, Modifier.Ftz, Modifier.F64),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.RcpApproxFtzF64(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rcp.approx.ftz.f64", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-rcp-approx-ftz-f64",
                "9.7.3.14. Floating Point Instructions: rcp.approx.ftz.f64",
                "rcp.approx.ftz.f64",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("rcp.approx.ftz.f64  d, a;"),
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Red extends Definition(
          "red",
          Opcode.Red,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.Red(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("red", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-red",
                "9.7.14.6. Parallel Synchronization and Communication Instructions: red",
                "red",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "red{.sem}{.scope}{.space}.op{.level::cache_hint}.type          [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16    [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.f16x2  [a], b{, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16\n                                                      [a], b {, cache_policy};\n\nred{.sem}{.scope}{.space}.add.noftz{.level::cache_hint}.bf16x2\n                                                      [a], b {, cache_policy};\n\n.space =              { .global, .shared{::cta, ::cluster} };\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n\n.op =                 { .and, .or, .xor,\n                        .add, .inc, .dec,\n                        .min, .max };\n.level::cache_hint =  { .L2::cache_hint };\n.type =               { .b32, .b64, .u32, .u64, .s32, .s64, .f32, .f64 };",
                  "red{.sem}{.scope}{.global}.add{.level::cache_hint}.vec_32_bit.f32 [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}. vec_16_bit.half_word_type [a], b{, cache_policy};\nred{.sem}{.scope}{.global}.op.noftz{.level::cache_hint}.vec_32_bit.packed_type [a], b {, cache_policy};\n\n.sem =                { .relaxed, .release };\n.scope =              { .cta, .cluster, .gpu, .sys };\n.op =                 { .add, .min, .max };\n.half_word_type =     { .f16, .bf16 };\n.packed_type =        { .f16x2,.bf16x2 };\n.vec_16_bit =         { .v2, .v4, .v8 }\n.vec_32_bit =         { .v2, .v4 };\n.level::cache_hint =  { .L2::cache_hint }"
                ),
                Version(1, 2),
                Target(11),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        Modifier.Add,
                        Modifier.And,
                        Modifier.Dec,
                        Modifier.Inc,
                        Modifier.Max,
                        Modifier.Min,
                        Modifier.Or,
                        Modifier.Xor
                      ),
                      ModifierGroup(
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.U32,
                        Modifier.U64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(Modifier.V2, Modifier.V4),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min), ModifierGroup(Modifier.Noftz)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min),
                      ModifierGroup(Modifier.Noftz),
                      ModifierGroup(Modifier.V2, Modifier.V4),
                      ModifierGroup(Modifier.Bf16x2, Modifier.F16x2)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Global,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Sys
                    ),
                    false
                  )
                )
              )
            ))
        )
    case RedAsync extends Definition(
          "red.async",
          Opcode.Red,
          Vec(Modifier.Async),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.RedAsync(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("red.async", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-red-async",
                "9.7.14.7. Parallel Synchronization and Communication Instructions: red.async",
                "red.async",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// Increment and Decrement reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  =                 { .relaxed };\n.scope =                { .cluster };\n.ss   =                 { .shared::cluster };\n.op   =                 { .inc, .dec };\n.type =                 { .u32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n\n// MIN and MAX reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .min, .max };\n.type = { .u32, .s32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// Bitwise AND, OR and XOR reductions\nred.async.sem.scope{.ss}.completion_mechanism.op.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.op   = { .and, .or, .xor };\n.type = { .b32 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\n// ADD reductions\nred.async.sem.scope{.ss}.completion_mechanism.add.type [a], b, [mbar];\n\n.sem  = { .relaxed };\n.scope = { .cluster };\n.ss   = { .shared::cluster };\n.type = { .u32, .s32, .u64 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nred.async{.mmio}.sem.scope{.ss}.add.type [a], b;\n\n.sem  = { .release };\n.scope = { .gpu, .sys };\n.ss   = { .global };\n.type = { .u32, .s32, .u64, .s64 };"
                ),
                Version(8, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Mmio),
                    false
                  )
                )
              )
            ))
        )
    case ReduxSync extends Definition(
          "redux.sync",
          Opcode.Redux,
          Vec(Modifier.Sync),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.ReduxSync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("redux.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-redux-sync",
                "9.7.14.13. Parallel Synchronization and Communication Instructions: redux.sync",
                "redux.sync",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "redux.sync.op.type dst, src, membermask;\n.op   = {.add, .min, .max}\n.type = {.u32, .s32}\n\nredux.sync.op.b32 dst, src, membermask;\n.op   = {.and, .or, .xor}\n\nredux.sync.op{.abs.}{.NaN}.f32 dst, src, membermask;\n.op   = { .min, .max }"
                ),
                Version(7, 0),
                Target(80),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Max, Modifier.Min), ModifierGroup(Modifier.S32, Modifier.U32)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Max, Modifier.Min), ModifierGroup(Modifier.B32)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Max, Modifier.Min), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs),
                    false
                  )
                )
              )
            ))
        )
    case Rem extends Definition(
          "rem",
          Opcode.Rem,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Rem(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rem", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-rem",
                "9.7.1.10. Integer Arithmetic Instructions: rem",
                "rem",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "rem.type  d, a, b;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    Modifier.S16,
                    Modifier.S32,
                    Modifier.S64,
                    Modifier.U16,
                    Modifier.U32,
                    Modifier.U64
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Ret extends Definition(
          "ret",
          Opcode.Ret,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.Ret(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ret", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "control-flow-instructions-ret",
                "9.7.13.6. Control Flow Instructions: ret",
                "ret",
                "Control Flow Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("ret{.uni};"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(Vec(), Vec(), _root_.scala.collection.immutable.Set(Modifier.Uni), true))
              )
            ))
        )
    case Rsqrt extends Definition(
          "rsqrt",
          Opcode.Rsqrt,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Rsqrt(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rsqrt", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-rsqrt",
                "9.7.3.16. Floating Point Instructions: rsqrt",
                "rsqrt",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "rsqrt.approx{.ftz}.f32  d, a;\nrsqrt.approx.f64        d, a;"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ))
        )
    case RsqrtApproxFtzF64 extends Definition(
          "rsqrt.approx.ftz.f64",
          Opcode.Rsqrt,
          Vec(Modifier.Approx, Modifier.Ftz, Modifier.F64),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.RsqrtApproxFtzF64(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("rsqrt.approx.ftz.f64", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-rsqrt-approx-ftz-f64",
                "9.7.3.17. Floating Point Instructions: rsqrt.approx.ftz.f64",
                "rsqrt.approx.ftz.f64",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("rsqrt.approx.ftz.f64 d, a;"),
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Sad extends Definition(
          "sad",
          Opcode.Sad,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Sad(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sad", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-sad",
                "9.7.1.8. Integer Arithmetic Instructions: sad",
                "sad",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "sad.type  d, a, b, c;\n\n.type = { .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(
                    Modifier.S16,
                    Modifier.S32,
                    Modifier.S64,
                    Modifier.U16,
                    Modifier.U32,
                    Modifier.U64
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Selp extends Definition(
          "selp",
          Opcode.Selp,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Selp(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("selp", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "comparison-and-selection-instructions-selp",
                "9.7.6.3. Comparison and Selection Instructions: selp",
                "selp",
                "Comparison and Selection Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "selp.type d, a, b, c;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64,\n                .f32, .f64 };"
                ),
                Version(1, 0),
                Target(13),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(
                    Modifier.B16,
                    Modifier.B32,
                    Modifier.B64,
                    Modifier.F32,
                    Modifier.F64,
                    Modifier.S16,
                    Modifier.S32,
                    Modifier.S64,
                    Modifier.U16,
                    Modifier.U32,
                    Modifier.U64
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Set extends Definition(
          "set",
          Opcode.Set,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Set(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("set", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "comparison-and-selection-instructions-set",
                "9.7.6.1. Comparison and Selection Instructions: set",
                "set",
                "Comparison and Selection Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "set.CmpOp{.ftz}.dtype.stype         d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.stype  d, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.dtype  = { .u32, .s32, .f32 };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Hi,
                        Modifier.Hs,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lo,
                        Modifier.Ls,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.F32, Modifier.S32, Modifier.U32),
                      ModifierGroup(
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Hi,
                        Modifier.Hs,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lo,
                        Modifier.Ls,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.F32, Modifier.S32, Modifier.U32),
                      ModifierGroup(
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-comparison-instructions-set",
                "9.7.7.1. Half Precision Comparison Instructions: set",
                "set",
                "Half Precision Comparison Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "set.CmpOp{.ftz}.f16.stype            d, a, b;\nset.CmpOp.BoolOp{.ftz}.f16.stype     d, a, b, {!}c;\n\nset.CmpOp.bf16.stype                 d, a, b;\nset.CmpOp.BoolOp.bf16.stype          d, a, b, {!}c;\n\nset.CmpOp{.ftz}.dtype.f16            d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16     d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp.dtype.bf16                 d, a, b;\nset.CmpOp.BoolOp.dtype.bf16          d, a, b, {!}c;\n.dtype  = { .u16, .s16, .u32, .s32}\n\nset.CmpOp{.ftz}.dtype.f16x2          d, a, b;\nset.CmpOp.BoolOp{.ftz}.dtype.f16x2   d, a, b, {!}c;\n.dtype  = { .f16x2, .u32, .s32}\n\nset.CmpOp.dtype.bf16x2               d, a, b;\nset.CmpOp.BoolOp.dtype.bf16x2        d, a, b, {!}c;\n.dtype  = { .bf16x2, .u32, .s32}\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.stype  = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n            .f16, .f32, .f64};"
                ),
                Version(4, 2),
                Target(53),
                Vec(
                  metadataSet_70_0(),
                  metadataSet_70_1(),
                  metadataSet_70_2(),
                  metadataSet_70_3(),
                  metadataSet_70_4(),
                  metadataSet_70_5(),
                  metadataSet_70_6(),
                  metadataSet_70_7(),
                  metadataSet_70_8(),
                  metadataSet_70_9(),
                  metadataSet_70_10(),
                  metadataSet_70_11()
                )
              )
            ))
        )
    case SetMaxRegisters extends Definition(
          "setmaxnreg",
          Opcode.SetMaxRegisters,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.SetMaxRegisters(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("setmaxnreg", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "miscellaneous-instructions-setmaxnreg",
                "9.7.20.5. Miscellaneous Instructions: setmaxnreg",
                "setmaxnreg",
                "Miscellaneous Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "setmaxnreg.action.sync.aligned.u32 imm-reg-count;\n\n.action = { .inc, .dec };"
                ),
                Version(8, 0),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Dec, Modifier.Inc),
                    ModifierGroup(Modifier.Sync),
                    ModifierGroup(Modifier.Aligned),
                    ModifierGroup(Modifier.U32)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case Setp extends Definition(
          "setp",
          Opcode.Setp,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Setp(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("setp", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "comparison-and-selection-instructions-setp",
                "9.7.6.2. Comparison and Selection Instructions: setp",
                "setp",
                "Comparison and Selection Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "setp.CmpOp{.ftz}.type         p[|q], a, b;\nsetp.CmpOp.BoolOp{.ftz}.type  p[|q], a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge, lo, ls, hi, hs,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };\n.type   = { .b16, .b32, .b64,\n            .u16, .u32, .u64,\n            .s16, .s32, .s64,\n                  .f32, .f64 };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Hi,
                        Modifier.Hs,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lo,
                        Modifier.Ls,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Hi,
                        Modifier.Hs,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lo,
                        Modifier.Ls,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-comparison-instructions-setp",
                "9.7.7.2. Half Precision Comparison Instructions: setp",
                "setp",
                "Half Precision Comparison Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "setp.CmpOp{.ftz}.f16           p, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16    p, a, b, {!}c;\n\nsetp.CmpOp{.ftz}.f16x2         p|q, a, b;\nsetp.CmpOp.BoolOp{.ftz}.f16x2  p|q, a, b, {!}c;\n\nsetp.CmpOp.bf16                p, a, b;\nsetp.CmpOp.BoolOp.bf16         p, a, b, {!}c;\n\nsetp.CmpOp.bf16x2              p|q, a, b;\nsetp.CmpOp.BoolOp.bf16x2       p|q, a, b, {!}c;\n\n.CmpOp  = { eq, ne, lt, le, gt, ge,\n            equ, neu, ltu, leu, gtu, geu, num, nan };\n.BoolOp = { and, or, xor };"
                ),
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.F16)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.F16)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.F16x2)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.F16x2)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.Bf16)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.Bf16)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.Bf16x2)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(
                        Modifier.Eq,
                        Modifier.Equ,
                        Modifier.Ge,
                        Modifier.Geu,
                        Modifier.Gt,
                        Modifier.Gtu,
                        Modifier.Le,
                        Modifier.Leu,
                        Modifier.Lt,
                        Modifier.Ltu,
                        Modifier.Nan,
                        Modifier.Ne,
                        Modifier.Neu,
                        Modifier.Num
                      ),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.Bf16x2)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Shf extends Definition(
          "shf",
          Opcode.Shf,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Shf(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shf", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "logic-and-shift-instructions-shf",
                "9.7.8.7. Logic and Shift Instructions: shf",
                "shf",
                "Logic and Shift Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "shf.l.mode.b32  d, a, b, c;  // left shift\nshf.r.mode.b32  d, a, b, c;  // right shift\n\n.mode = { .clamp, .wrap };"
                ),
                Version(3, 1),
                Target(32),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.L),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.R),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              )
            ))
        )
    case Shfl extends Definition(
          "shfl",
          Opcode.Shfl,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Shfl(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shfl", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-shfl",
                "9.7.9.5. Data Movement and Conversion Instructions: shfl (deprecated)",
                "shfl (deprecated)",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "shfl.mode.b32  d[|p], a, b, c;\n\n.mode = { .up, .down, .bfly, .idx };"
                ),
                Version(3, 0),
                Target(30),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(Modifier.Bfly, Modifier.Down, Modifier.Idx, Modifier.Up),
                    ModifierGroup(Modifier.B32)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case ShflSync extends Definition(
          "shfl.sync",
          Opcode.Shfl,
          Vec(Modifier.Sync),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 5 && (operands.size == 5))
              Right(Instruction.ShflSync(operands(0), operands(1), operands(2), operands(3), operands(4), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shfl.sync", Vec(5), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-shfl-sync",
                "9.7.9.6. Data Movement and Conversion Instructions: shfl.sync",
                "shfl.sync",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "shfl.sync.mode.b32  d[|p], a, b, c, membermask;\n\n.mode = { .up, .down, .bfly, .idx };"
                ),
                Version(6, 0),
                Target(30),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(Modifier.Bfly, Modifier.Down, Modifier.Idx, Modifier.Up),
                    ModifierGroup(Modifier.B32)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Shl extends Definition(
          "shl",
          Opcode.Shl,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Shl(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shl", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "logic-and-shift-instructions-shl",
                "9.7.8.8. Logic and Shift Instructions: shl",
                "shl",
                "Logic and Shift Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("shl.type d, a, b;\n\n.type = { .b16, .b32, .b64 };"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Shr extends Definition(
          "shr",
          Opcode.Shr,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Shr(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("shr", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "logic-and-shift-instructions-shr",
                "9.7.8.9. Logic and Shift Instructions: shr",
                "shr",
                "Logic and Shift Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "shr.type d, a, b;\n\n.type = { .b16, .b32, .b64,\n          .u16, .u32, .u64,\n          .s16, .s32, .s64 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    Modifier.B16,
                    Modifier.B32,
                    Modifier.B64,
                    Modifier.S16,
                    Modifier.S32,
                    Modifier.S64,
                    Modifier.U16,
                    Modifier.U32,
                    Modifier.U64
                  )),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Sin extends Definition(
          "sin",
          Opcode.Sin,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Sin(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sin", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-sin",
                "9.7.3.18. Floating Point Instructions: sin",
                "sin",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("sin.approx{.ftz}.f32  d, a;"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                  _root_.scala.collection.immutable.Set(Modifier.Ftz),
                  true
                ))
              )
            ))
        )
    case Slct extends Definition(
          "slct",
          Opcode.Slct,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Slct(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("slct", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "comparison-and-selection-instructions-slct",
                "9.7.6.4. Comparison and Selection Instructions: slct",
                "slct",
                "Comparison and Selection Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "slct.dtype.s32        d, a, b, c;\nslct{.ftz}.dtype.f32  d, a, b, c;\n\n.dtype = { .b16, .b32, .b64,\n           .u16, .u32, .u64,\n           .s16, .s32, .s64,\n                 .f32, .f64 };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64
                      ),
                      ModifierGroup(Modifier.S32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64
                      ),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    true
                  )
                )
              )
            ))
        )
    case Sqrt extends Definition(
          "sqrt",
          Opcode.Sqrt,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Sqrt(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sqrt", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-sqrt",
                "9.7.3.15. Floating Point Instructions: sqrt",
                "sqrt",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "sqrt.approx{.ftz}.f32  d, a; // fast, approximate square root\nsqrt.rnd{.ftz}.f32     d, a; // IEEE 754 compliant rounding\nsqrt.rnd.f64           d, a; // IEEE 754 compliant rounding\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case St extends Definition(
          "st",
          Opcode.St,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.St(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("st", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-st",
                "9.7.9.11. Data Movement and Conversion Instructions: st",
                "st",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "st{.weak}{.ss}{.cop}{.level::cache_hint}{.vec}.type   [a], b{, cache_policy};\nst{.weak}{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.volatile{.ss}{.vec}.type                           [a], b;\nst.relaxed.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.release.scope{.ss}{.level1::eviction_priority}{.level2::eviction_priority}{.level::cache_hint}{.vec}.type\n                                                      [a], b{, cache_policy};\nst.mmio.sem.sys{.global}.type         [a], b;\n\n.ss =                       { .global, .local, .param{::func}, .shared{::cta, ::cluster} };\n.sem =                      { .release, .relaxed };\n.level1::eviction_priority = { .L1::evict_normal, .L1::evict_unchanged,\n                               .L1::evict_first, .L1::evict_last, .L1::no_allocate };\n.level2::eviction_priority = { .L2::evict_normal, .L2::evict_first, .L2::evict_last };\n.level::cache_hint =        { .L2::cache_hint };\n.cop =                      { .wb, .cg, .cs, .wt };\n.sem =                      { .relaxed, .release };\n.scope =                    { .cta, .cluster, .gpu, .sys };\n.vec =                      { .v2, .v4, .v8 };\n.type =                     { .b8, .b16, .b32, .b64, .b128,\n                              .u8, .u16, .u32, .u64,\n                              .s8, .s16, .s32, .s64,\n                              .f32, .f64 };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.B128,
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.B8,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.S8,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64,
                      Modifier.U8
                    )),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Cg,
                      Modifier.Cs,
                      Modifier.Global,
                      Modifier.Local,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8,
                      Modifier.Wb,
                      Modifier.Weak,
                      Modifier.Wt
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.B128,
                      Modifier.B16,
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.B8,
                      Modifier.F32,
                      Modifier.F64,
                      Modifier.S16,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.S8,
                      Modifier.U16,
                      Modifier.U32,
                      Modifier.U64,
                      Modifier.U8
                    )),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L1EvictFirst,
                      Modifier.L1EvictLast,
                      Modifier.L1EvictNormal,
                      Modifier.L1EvictUnchanged,
                      Modifier.L1NoAllocate,
                      Modifier.L2CacheHint,
                      Modifier.L2EvictFirst,
                      Modifier.L2EvictLast,
                      Modifier.L2EvictNormal,
                      Modifier.Global,
                      Modifier.Local,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8,
                      Modifier.Weak
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Volatile),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Global,
                      Modifier.Local,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L1EvictFirst,
                      Modifier.L1EvictLast,
                      Modifier.L1EvictNormal,
                      Modifier.L1EvictUnchanged,
                      Modifier.L1NoAllocate,
                      Modifier.L2CacheHint,
                      Modifier.L2EvictFirst,
                      Modifier.L2EvictLast,
                      Modifier.L2EvictNormal,
                      Modifier.Global,
                      Modifier.Local,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L1EvictFirst,
                      Modifier.L1EvictLast,
                      Modifier.L1EvictNormal,
                      Modifier.L1EvictUnchanged,
                      Modifier.L1NoAllocate,
                      Modifier.L2CacheHint,
                      Modifier.L2EvictFirst,
                      Modifier.L2EvictLast,
                      Modifier.L2EvictNormal,
                      Modifier.Global,
                      Modifier.Local,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.V8
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Mmio),
                      ModifierGroup(Modifier.Relaxed, Modifier.Release),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(
                        Modifier.B128,
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global),
                    false
                  )
                )
              )
            ))
        )
    case StAsync extends Definition(
          "st.async",
          Opcode.St,
          Vec(Modifier.Async),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.StAsync(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("st.async", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-st-async",
                "9.7.9.12. Data Movement and Conversion Instructions: st.async",
                "st.async",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "st.async{.weak}{.ss}.completion_mechanism{.vec}.type [a], b, [mbar];\n\n.ss   =                 { .shared::cluster };\n.type =                 { .b32, .b64, .b128,\n                          .u32, .u64,\n                          .s32, .s64,\n                          .f32, .f64 };\n.vec  =                 { .v2, .v4 };\n.completion_mechanism = { .mbarrier::complete_tx::bytes };\n\nst.async{.mmio}.sem.scope{.ss}.type [a], b;\n\n.sem =                  { .release };\n.scope =                { .gpu, .sys };\n.ss =                   { .global };\n.type =                 { .b8, .b16, .b32, .b64,\n                          .u8, .u16, .u32, .u64,\n                          .s8, .s16, .s32, .s64,\n                                     .f32, .f64 };"
                ),
                Version(8, 1),
                Target(90),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
                      ModifierGroup(
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.V2, Modifier.V4, Modifier.Weak),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(
                        Modifier.B16,
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.B8,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S16,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.S8,
                        Modifier.U16,
                        Modifier.U32,
                        Modifier.U64,
                        Modifier.U8
                      )
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Mmio),
                    false
                  )
                )
              )
            ))
        )
    case StBulk extends Definition(
          "st.bulk",
          Opcode.St,
          Vec(Modifier.Bulk),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.StBulk(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("st.bulk", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-st-bulk",
                "9.7.9.14. Data Movement and Conversion Instructions: st.bulk",
                "st.bulk",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "st.bulk{.weak}{.shared::cta}  [a], size, initval; // initval must be zero"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.SharedCta, Modifier.Weak),
                  true
                ))
              )
            ))
        )
    case StackRestore extends Definition(
          "stackrestore",
          Opcode.StackRestore,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.StackRestore(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("stackrestore", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "stack-manipulation-instructions-stackrestore",
                "9.7.18.2. Stack Manipulation Instructions: stackrestore",
                "stackrestore",
                "Stack Manipulation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("stackrestore.type  a;\n\n.type = { .u32, .u64 };"),
                Version(7, 3),
                Target(52),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case StackSave extends Definition(
          "stacksave",
          Opcode.StackSave,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.StackSave(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("stacksave", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "stack-manipulation-instructions-stacksave",
                "9.7.18.1. Stack Manipulation Instructions: stacksave",
                "stacksave",
                "Stack Manipulation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("stacksave.type  d;\n\n.type = { .u32, .u64 };"),
                Version(7, 3),
                Target(52),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register),
                  Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Stmatrix extends Definition(
          "stmatrix",
          Opcode.Stmatrix,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Stmatrix(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("stmatrix", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "warp-level-matrix-instructions-stmatrix",
                "9.7.15.5.16. Warp-level matrix store instruction: stmatrix",
                "stmatrix",
                "Warp-level matrix store instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "stmatrix.sync.aligned.shape.num{.trans}{.ss}.type [p], r;\n\n.shape  = {.m8n8, .m16n8};\n.num    = {.x1, .x2, .x4};\n.ss     = {.shared{::cta}};\n.type   = {.b16, .b8};"
                ),
                Version(7, 8),
                Target(90),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Register),
                  Vec(
                    ModifierGroup(Modifier.Sync),
                    ModifierGroup(Modifier.Aligned),
                    ModifierGroup(Modifier.M16n8, Modifier.M8n8),
                    ModifierGroup(Modifier.X1, Modifier.X2, Modifier.X4),
                    ModifierGroup(Modifier.B16, Modifier.B8)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Trans),
                  false
                ))
              )
            ))
        )
    case Sub extends Definition(
          "sub",
          Opcode.Sub,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Sub(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sub", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-sub",
                "9.7.1.2. Integer Arithmetic Instructions: sub",
                "sub",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "sub.type1        d, a, b;\nsub{.sat}.type2  d, a, b;\n\n.type1 = { .u16, .u32, .u64,\n           .s16, .s64 };\n\n.type2 = { .s32, .u8x4, .s8x4 };"
                ),
                Version(1, 0),
                Target(10),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S16, Modifier.S64, Modifier.U16, Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S32, Modifier.S8x4, Modifier.U8x4)),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "floating-point-instructions-sub",
                "9.7.3.4. Floating Point Instructions: sub",
                "sub",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "sub{.rnd}{.ftz}{.sat}.f32  d, a, b;\nsub{.rnd}{.ftz}.f32x2      d, a, b;\nsub{.rnd}.f64              d, a, b;\n\n.rnd = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(13),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Ftz,
                      Modifier.Rm,
                      Modifier.Rn,
                      Modifier.Rp,
                      Modifier.Rz,
                      Modifier.Sat
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32x2)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Ftz,
                      Modifier.Rm,
                      Modifier.Rn,
                      Modifier.Rp,
                      Modifier.Rz
                    ),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-sub",
                "9.7.4.2. Half Precision Floating Point Instructions: sub",
                "sub",
                "Half Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "sub{.rnd}{.ftz}{.sat}.f16   d, a, b;\nsub{.rnd}{.ftz}{.sat}.f16x2 d, a, b;\n\nsub{.rnd}.bf16   d, a, b;\nsub{.rnd}.bf16x2 d, a, b;\n\n.rnd = { .rn };"
                ),
                Version(4, 2),
                Target(53),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    false
                  )
                )
              ),
              Metadata.Entry(
                "mixed-precision-floating-point-instructions-sub",
                "9.7.5.2. Mixed Precision Floating Point Instructions: sub",
                "sub",
                "Mixed Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "sub{.rnd}{.sat}.f32.atype  d, a, c;\n\n.atype = { .f16, .bf16};\n.rnd   = { .rn, .rz, .rm, .rp };"
                ),
                Version(1, 0),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.F32), ModifierGroup(Modifier.Bf16, Modifier.F16)),
                  _root_.scala.collection.immutable.Set(
                    Modifier.Rm,
                    Modifier.Rn,
                    Modifier.Rp,
                    Modifier.Rz,
                    Modifier.Sat
                  ),
                  true
                ))
              )
            ))
        )
    case SubCc extends Definition(
          "sub.cc",
          Opcode.Sub,
          Vec(Modifier.Cc),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.SubCc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sub.cc", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-sub-cc",
                "9.7.2.3. Extended-Precision Arithmetic Instructions: sub.cc",
                "sub.cc",
                "Extended-Precision Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "sub.cc.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ),
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Subc extends Definition(
          "subc",
          Opcode.Subc,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Subc(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("subc", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "extended-precision-arithmetic-instructions-subc",
                "9.7.2.4. Extended-Precision Arithmetic Instructions: subc",
                "subc",
                "Extended-Precision Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "subc{.cc}.type  d, a, b;\n\n.type = { .u32, .s32, .u64, .s64 };"
                ),
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(Modifier.Cc),
                  true
                ))
              )
            ))
        )
    case Suld extends Definition(
          "suld",
          Opcode.Suld,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Suld(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("suld", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "surface-instructions-suld",
                "9.7.12.1. Surface Instructions: suld",
                "suld",
                "Surface Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "suld.b.geom{.cop}.vec.dtype.clamp  d, [a, b];  // unformatted\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d };\n.cop   = { .ca, .cg, .cs, .cv };               // cache operation\n.vec   = { none, .v2, .v4 };\n.dtype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"
                ),
                Version(1, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(
                    ModifierGroup(Modifier.B),
                    ModifierGroup(Modifier.A1d, Modifier.A2d),
                    ModifierGroup(Modifier.None, Modifier.V2, Modifier.V4),
                    ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.B8),
                    ModifierGroup(Modifier.Clamp, Modifier.Trap, Modifier.Zero)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Ca, Modifier.Cg, Modifier.Cs, Modifier.Cv),
                  true
                ))
              )
            ))
        )
    case Suq extends Definition(
          "suq",
          Opcode.Suq,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2)) Right(Instruction.Suq(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("suq", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "surface-instructions-suq",
                "9.7.12.4. Surface Instructions: suq",
                "suq",
                "Surface Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "suq.query.b32   d, [a];\n\n.query = { .width, .height, .depth,\n           .channel_data_type, .channel_order,\n           .array_size, .memory_layout };"
                ),
                Version(1, 5),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(
                    ModifierGroup(
                      Modifier.ArraySize,
                      Modifier.ChannelDataType,
                      Modifier.ChannelOrder,
                      Modifier.Depth,
                      Modifier.Height,
                      Modifier.MemoryLayout,
                      Modifier.Width
                    ),
                    ModifierGroup(Modifier.B32)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case Sured extends Definition(
          "sured",
          Opcode.Sured,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Sured(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sured", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "surface-instructions-sured",
                "9.7.12.3. Surface Instructions: sured",
                "sured",
                "Surface Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "sured.b.op.geom.ctype.clamp  [a,b],c; // byte addressing\nsured.p.op.geom.ctype.clamp  [a,b],c; // sample addressing\n\n.op    = { .add, .min, .max, .and, .or };\n.geom  = { .1d, .2d, .3d };\n.ctype = { .u32, .u64, .s32, .b32, .s64 };  // for sured.b\n.ctype = { .b32, .b64 };                    // for sured.p\n.clamp = { .trap, .clamp, .zero };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.B),
                      ModifierGroup(Modifier.Add, Modifier.And, Modifier.Max, Modifier.Min, Modifier.Or),
                      ModifierGroup(Modifier.B32, Modifier.B64),
                      ModifierGroup(Modifier.Clamp, Modifier.Trap, Modifier.Zero)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.P),
                      ModifierGroup(Modifier.Add, Modifier.And, Modifier.Max, Modifier.Min, Modifier.Or),
                      ModifierGroup(Modifier.B32, Modifier.B64),
                      ModifierGroup(Modifier.Clamp, Modifier.Trap, Modifier.Zero)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Sust extends Definition(
          "sust",
          Opcode.Sust,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Sust(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("sust", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "surface-instructions-sust",
                "9.7.12.2. Surface Instructions: sust",
                "sust",
                "Surface Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "sust.b.{1d,2d,3d}{.cop}.vec.ctype.clamp  [a, b], c;  // unformatted\nsust.p.{1d,2d,3d}.vec.b32.clamp          [a, b], c;  // formatted\n\nsust.b.{a1d,a2d}{.cop}.vec.ctype.clamp   [a, b], c;  // unformatted\n\n.cop   = { .wb, .cg, .cs, .wt };                     // cache operation\n.vec   = { none, .v2, .v4 };\n.ctype = { .b8 , .b16, .b32, .b64 };\n.clamp = { .trap, .clamp, .zero };"
                ),
                Version(1, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.B),
                      ModifierGroup(Modifier.None, Modifier.V2, Modifier.V4),
                      ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.B8),
                      ModifierGroup(Modifier.Clamp, Modifier.Trap, Modifier.Zero)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Cg, Modifier.Cs, Modifier.Wb, Modifier.Wt),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.P),
                      ModifierGroup(Modifier.None, Modifier.V2, Modifier.V4),
                      ModifierGroup(Modifier.B32),
                      ModifierGroup(Modifier.Clamp, Modifier.Trap, Modifier.Zero)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Szext extends Definition(
          "szext",
          Opcode.Szext,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Szext(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("szext", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "integer-arithmetic-instructions-szext",
                "9.7.1.22. Integer Arithmetic Instructions: szext",
                "szext",
                "Integer Arithmetic Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "szext.mode.type  d, a, b;\n\n.mode = { .clamp, .wrap };\n.type = { .u32, .s32 };"
                ),
                Version(7, 6),
                Target(70),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Clamp, Modifier.Wrap), ModifierGroup(Modifier.S32, Modifier.U32)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Tanh extends Definition(
          "tanh",
          Opcode.Tanh,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Tanh(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tanh", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-tanh",
                "9.7.3.22. Floating Point Instructions: tanh",
                "tanh",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("tanh.approx.f32 d, a;"),
                Version(7, 0),
                Target(75),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                "half-precision-floating-point-instructions-tanh",
                "9.7.4.9. Half Precision Floating Point Instructions: tanh",
                "tanh",
                "Half Precision Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tanh.approx.type d, a;\n\n.type = {.f16, .f16x2, .bf16, .bf16x2}"
                ),
                Version(7, 0),
                Target(75),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Approx),
                    ModifierGroup(Modifier.Bf16, Modifier.Bf16x2, Modifier.F16, Modifier.F16x2)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case Tcgen05Alloc extends Definition(
          "tcgen05.alloc",
          Opcode.Tcgen05,
          Vec(Modifier.Alloc),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Tcgen05Alloc(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.alloc", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit",
                "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
                "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
                "Tensorcore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned), ModifierGroup(Modifier.B32)),
                  _root_.scala.collection.immutable.Set(Modifier.SharedCta),
                  false
                ))
              )
            ))
        )
    case Tcgen05Commit extends Definition(
          "tcgen05.commit",
          Opcode.Tcgen05,
          Vec(Modifier.Commit),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1 || operands.size == 2))
              Right(Instruction.Tcgen05Commit(operands(0), operands.lift(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.commit", Vec(1, 2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen-async-sync-operations-commit",
                "9.7.17.12.1. TensorCore 5th Generation Instructions: tcgen05.commit",
                "tcgen05.commit",
                "TensorCore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.commit.cta_group.completion_mechanism{.shared::cluster}{.multicast}.b64\n                                                            [mbar] {, ctaMask};\n\n.completion_mechanism = { .mbarrier::arrive::one }\n.cta_group            = { .cta_group::1, .cta_group::2 }\n.multicast            = { .multicast::cluster }"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.MbarrierArriveOne), ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.MulticastCluster, Modifier.SharedCluster),
                  false
                ))
              )
            ))
        )
    case Tcgen05Cp extends Definition(
          "tcgen05.cp",
          Opcode.Tcgen05,
          Vec(Modifier.Cp),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Tcgen05Cp(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.cp", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-cp",
                "9.7.17.9.2. Tensorcore 5th Generation Instructions: tcgen05.cp",
                "tcgen05.cp",
                "Tensorcore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.cp.cta_group.shape{.multicast}{.dst_fmt.src_fmt} [taddr], s-desc;\n\n.cta_group = { .cta_group::1, .cta_group::2 }\n.src_fmt   = { .b6x16_p32 , .b4x16_p64 }\n.dst_fmt   = { .b8x16 }\n.shape     = { .128x256b, .4x256b, .128x128b, .64x128b**, .32x128b*** }\n.multicast = { .warpx2::02_13** , .warpx2::01_23**, .warpx4*** }"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.B4x16P64, Modifier.B6x16P32, Modifier.B8x16),
                  false
                ))
              )
            ))
        )
    case Tcgen05Dealloc extends Definition(
          "tcgen05.dealloc",
          Opcode.Tcgen05,
          Vec(Modifier.Dealloc),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Tcgen05Dealloc(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.dealloc", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit",
                "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
                "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
                "Tensorcore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned), ModifierGroup(Modifier.B32)),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case Tcgen05FenceAfterThreadSync extends Definition(
          "tcgen05.fence::after_thread_sync",
          Opcode.Tcgen05,
          Vec(Modifier.FenceAfterThreadSync),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.Tcgen05FenceAfterThreadSync(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "tcgen05.fence::after_thread_sync",
              Vec(0),
              operands.size
            ))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-special-sync-operations-fence",
                "9.7.17.11.1. TensorCore 5th Generation Instructions: tcgen05.fence",
                "tcgen05.fence",
                "TensorCore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.fence::before_thread_sync ;\ntcgen05.fence::after_thread_sync  ;"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(Vec(), Vec(), _root_.scala.collection.immutable.Set(), false))
              )
            ))
        )
    case Tcgen05FenceBeforeThreadSync extends Definition(
          "tcgen05.fence::before_thread_sync",
          Opcode.Tcgen05,
          Vec(Modifier.FenceBeforeThreadSync),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.Tcgen05FenceBeforeThreadSync(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "tcgen05.fence::before_thread_sync",
              Vec(0),
              operands.size
            ))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-special-sync-operations-fence",
                "9.7.17.11.1. TensorCore 5th Generation Instructions: tcgen05.fence",
                "tcgen05.fence",
                "TensorCore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.fence::before_thread_sync ;\ntcgen05.fence::after_thread_sync  ;"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(Vec(), Vec(), _root_.scala.collection.immutable.Set(), false))
              )
            ))
        )
    case Tcgen05Ld extends Definition(
          "tcgen05.ld",
          Opcode.Tcgen05,
          Vec(Modifier.Ld),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3 || operands.size == 4))
              Right(Instruction.Tcgen05Ld(operands(0), operands(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.ld", Vec(2, 3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-ld",
                "9.7.17.8.3. Tensorcore 5th Generation Instructions: tcgen05.ld",
                "tcgen05.ld",
                "Tensorcore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// Base load instruction:\n\ntcgen05.ld.sync.aligned.shape1.num{.pack}.b32    r, [taddr];\n\ntcgen05.ld.sync.aligned.shape2.num{.pack}.b32    r, [taddr], immHalfSplitoff;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.pack   = { .pack::16b }\n\n// Floating point type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp{.abs}{.NaN}.f32 r, redval, [taddr], immHalfSplitoff;\n\n// Integer type load along with reduction :\n\ntcgen05.ld.red.sync.aligned.shape3.num.redOp.type r, redval, [taddr];\n\ntcgen05.ld.red.sync.aligned.shape4.num.redOp.type r, redval, [taddr], immHalfSplitoff;\n\n.shape3 = { .32x32b   }\n.shape4 = { .16x32bx2 }\n.redOp  = { .min, .max }\n.type   = { .u32, .s32 }"
                ),
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(
                        Modifier.X1,
                        Modifier.X128,
                        Modifier.X16,
                        Modifier.X2,
                        Modifier.X32,
                        Modifier.X4,
                        Modifier.X64,
                        Modifier.X8
                      ),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(
                        Modifier.X1,
                        Modifier.X128,
                        Modifier.X16,
                        Modifier.X2,
                        Modifier.X32,
                        Modifier.X4,
                        Modifier.X64,
                        Modifier.X8
                      ),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(
                        Modifier.X1,
                        Modifier.X128,
                        Modifier.X16,
                        Modifier.X2,
                        Modifier.X32,
                        Modifier.X4,
                        Modifier.X64,
                        Modifier.X8
                      ),
                      ModifierGroup(Modifier.Max, Modifier.Min),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(
                        Modifier.X1,
                        Modifier.X128,
                        Modifier.X16,
                        Modifier.X2,
                        Modifier.X32,
                        Modifier.X4,
                        Modifier.X64,
                        Modifier.X8
                      ),
                      ModifierGroup(Modifier.Max, Modifier.Min),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(
                        Modifier.X1,
                        Modifier.X128,
                        Modifier.X16,
                        Modifier.X2,
                        Modifier.X32,
                        Modifier.X4,
                        Modifier.X64,
                        Modifier.X8
                      ),
                      ModifierGroup(Modifier.Max, Modifier.Min),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(
                        Modifier.X1,
                        Modifier.X128,
                        Modifier.X16,
                        Modifier.X2,
                        Modifier.X32,
                        Modifier.X4,
                        Modifier.X64,
                        Modifier.X8
                      ),
                      ModifierGroup(Modifier.Max, Modifier.Min),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Tcgen05Mma extends Definition(
          "tcgen05.mma",
          Opcode.Tcgen05,
          Vec(Modifier.Mma),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 5 || operands.size == 6 || operands.size == 7) Right(if (operands.size == 5)
              Instruction.Tcgen05Mma(
                operands(0),
                operands(1),
                operands(2),
                operands(3),
                None,
                None,
                None,
                operands(4),
                None,
                mods,
                pred
              )
            else if (Operand.isAddress(operands(4))) Instruction.Tcgen05Mma(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              Some(operands(4)),
              Some(operands(5)),
              None,
              operands(6),
              None,
              mods,
              pred
            )
            else Instruction.Tcgen05Mma(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              None,
              None,
              Some(operands(4)),
              operands(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.mma", Vec(5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-mma-instructions-mma",
                "9.7.17.10.9.1. TensorCore 5th Generation Instructions: tcgen05.mma",
                "tcgen05.mma",
                "TensorCore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.cta_group.kind   [d-tmem],  a-desc,  b-desc, idesc,\n                             { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind   [d-tmem], [a-tmem], b-desc, idesc,\n                             { disable-output-lane }, enable-input-d {, scale-input-d};\n\n.kind      = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.cta_group = { .cta_group::1, .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 2. Floating-point type with block scaling:\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}\n                                        [d-tmem],  a-desc,  b-desc, idesc,\n                                        [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}\n                                        [d-tmem], [a-tmem], b-desc, idesc,\n                                        [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.cta_group      = { .cta_group::1,   .cta_group::2 }\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n\n----------------------------------------------------------------------------------\n\n// 3. Convolution MMA for floating-point type without block scaling:\n\ntcgen05.mma.cta_group.kind.collector_usage [d-tmem],  a-desc,  b-desc, idesc,\n                                           { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc, idesc,\n                                                    { disable-output-lane }, enable-input-d {, scale-input-d};\n\ntcgen05.mma.cta_group.kind.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                    { disable-output-lane }, enable-input-d {, scale-input-d};\n\n.kind      = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.cta_group = { .cta_group::1,   .cta_group::2 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 4. Activation Stationary MMA for floating-point type with block scaling:\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                            [d-tmem],  a-desc,  b-desc, idesc,\n                                            [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                            [d-tmem], [a-tmem], b-desc, idesc,\n                                            [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.cta_group       = { .cta_group::1,   .cta_group::2 }\n.scale_vectorsize  = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.kind            = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 5. Integer type:\n\ntcgen05.mma.cta_group.kind::i8  [d-tmem],  a-desc,  b-desc, idesc,\n                                { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8  [d-tmem], [a-tmem], b-desc, idesc,\n                                { disable-output-lane }, enable-input-d;\n\n.cta_group = { .cta_group::1,   .cta_group::2  }\n\n----------------------------------------------------------------------------------\n\n// 6. Convolution MMA for integer type:\n\ntcgen05.mma.cta_group.kind::i8.collector_usage          [d-tmem],  a-desc,  b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.cta_group.kind::i8{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc, idesc,\n                                                        { disable-output-lane }, enable-input-d;\n\n.cta_group       = { .cta_group::1,   .cta_group::2  }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }"
                ),
                Version(8, 6),
                Target(100),
                Vec(
                  metadataTcgen05Mma_186_0(),
                  metadataTcgen05Mma_186_1(),
                  metadataTcgen05Mma_186_2(),
                  metadataTcgen05Mma_186_3(),
                  metadataTcgen05Mma_186_4(),
                  metadataTcgen05Mma_186_5(),
                  metadataTcgen05Mma_186_6(),
                  metadataTcgen05Mma_186_7(),
                  metadataTcgen05Mma_186_8(),
                  metadataTcgen05Mma_186_9(),
                  metadataTcgen05Mma_186_10(),
                  metadataTcgen05Mma_186_11(),
                  metadataTcgen05Mma_186_12(),
                  metadataTcgen05Mma_186_13()
                )
              )
            ))
        )
    case Tcgen05MmaSp extends Definition(
          "tcgen05.mma.sp",
          Opcode.Tcgen05,
          Vec(Modifier.Mma, Modifier.Sp),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size == 6 || operands.size == 7 || operands.size == 8) Right(if (operands.size == 6)
              Instruction.Tcgen05MmaSp(
                operands(0),
                operands(1),
                operands(2),
                operands(3),
                operands(4),
                None,
                None,
                None,
                operands(5),
                None,
                mods,
                pred
              )
            else if (Operand.isAddress(operands(5))) Instruction.Tcgen05MmaSp(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              Some(operands(5)),
              Some(operands(6)),
              None,
              operands(7),
              None,
              mods,
              pred
            )
            else Instruction.Tcgen05MmaSp(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              None,
              None,
              Some(operands(5)),
              operands(6),
              operands.lift(7),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.mma.sp", Vec(6, 7, 8), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-mma-instructions-mma-sp",
                "9.7.17.10.9.2. TensorCore 5th Generation Instructions: tcgen05.mma.sp",
                "tcgen05.mma.sp",
                "TensorCore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.sp.cta_group.kind  [d-tmem],  a-desc,  b-desc, [sp-meta-tmem] ,  idesc,\n                               { disable-output-lane }, enable-input-d{, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind  [d-tmem], [a-tmem], b-desc, [sp-meta-tmem] , idesc,\n                               { disable-output-lane }, enable-input-d{, scale-input-d};\n\n.kind       = { .kind::f16, , .kind::tf32, .kind::f8f6f4 }\n.cta_group  = { .cta_group::1,  .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 2. Floating-point type with block scaling:\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}\n                                         [d-tmem],  a-desc,  b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}\n                                         [d-tmem], [a-tmem], b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.cta_group      = { .cta_group::1,  .cta_group::2 }\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n\n----------------------------------------------------------------------------------\n\n// 3. Convolution MMA with floating-point type without block scaling:\n\ntcgen05.mma.sp.cta_group.kind.collector_usage           [d-tmem],  a-desc,  b-desc,\n                                                        [sp-meta-tmem] ,  idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind.ashift{.collector_usage}  [d-tmem], [a-tmem], b-desc,\n                                                        [sp-meta-tmem] , idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\ntcgen05.mma.sp.cta_group.kind{.ashift}.collector_usage  [d-tmem], [a-tmem], b-desc,\n                                                        [sp-meta-tmem] , idesc,\n                                                        { disable-output-lane }, enable-input-d\n                                                        {, scale-input-d};\n\n.kind            = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 4. Activation Stationary MMA with floating-point type with block scaling:\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                         [d-tmem],  a-desc,  b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind.block_scale{.scale_vectorsize}.collector_usage\n                                         [d-tmem], [a-tmem], b-desc , [sp-meta-tmem] , idesc,\n                                         [scale-A-tmem], [scale-B-tmem], enable-input-d;\n\n.kind = { .kind::mxf8f6f4, .kind::mxf4, .kind::mxf4nvf4 }\n.scale_vectorsize = { .scale_vec::1X, .scale_vec::2X, .scale_vec::4X, .block16, .block32 }\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }\n\n----------------------------------------------------------------------------------\n\n// 5. Integer type:\n\ntcgen05.mma.sp.cta_group.kind::i8 [d-tmem],  a-desc,  b-desc, [sp-meta-tmem] , idesc,\n                                  { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8 [d-tmem], [a-tmem], b-desc, [sp-meta-tmem] , idesc,\n                                  { disable-output-lane }, enable-input-d;\n\n.cta_group      = { .cta_group::1,  .cta_group::2 }\n\n----------------------------------------------------------------------------------\n\n// 6. Convolution MMA with Integer type:\n\ntcgen05.mma.sp.cta_group.kind::i8.collector_usage          [d-tmem],  a-desc,  b-desc,\n                                                           [sp-meta-tmem] , idesc,\n                                                           { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8.ashift{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                           [sp-meta-tmem], idesc ,\n                                                           { disable-output-lane }, enable-input-d;\n\ntcgen05.mma.sp.cta_group.kind::i8{.ashift}.collector_usage [d-tmem], [a-tmem], b-desc,\n                                                           [sp-meta-tmem], idesc ,\n                                                           { disable-output-lane }, enable-input-d;\n\n.collector_usage = { .collector::buffer::op }\n::buffer         = { ::a }\n::op             = { ::fill, ::use, ::lastuse, ::discard* }"
                ),
                Version(8, 6),
                Target(100),
                Vec(
                  metadataTcgen05MmaSp_187_0(),
                  metadataTcgen05MmaSp_187_1(),
                  metadataTcgen05MmaSp_187_2(),
                  metadataTcgen05MmaSp_187_3(),
                  metadataTcgen05MmaSp_187_4(),
                  metadataTcgen05MmaSp_187_5(),
                  metadataTcgen05MmaSp_187_6(),
                  metadataTcgen05MmaSp_187_7(),
                  metadataTcgen05MmaSp_187_8(),
                  metadataTcgen05MmaSp_187_9(),
                  metadataTcgen05MmaSp_187_10(),
                  metadataTcgen05MmaSp_187_11(),
                  metadataTcgen05MmaSp_187_12(),
                  metadataTcgen05MmaSp_187_13()
                )
              )
            ))
        )
    case Tcgen05MmaWs extends Definition(
          "tcgen05.mma.ws",
          Opcode.Tcgen05,
          Vec(Modifier.Mma, Modifier.Ws),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 5 && (operands.size == 5 || operands.size == 6)) Right(Instruction.Tcgen05MmaWs(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.mma.ws", Vec(5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-mma-instructions-mma-ws",
                "9.7.17.10.9.3. TensorCore 5th Generation Instructions: tcgen05.mma.ws",
                "tcgen05.mma.ws",
                "TensorCore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.ws.cta_group::1.kind{.collector_usage}    [d-tmem],  a-desc,  b-desc,  idesc,\n                                                      enable-input-d {, zero-column-mask-desc };\n\ntcgen05.mma.ws.cta_group::1.kind{.collector_usage}    [d-tmem], [a-tmem], b-desc, idesc,\n                                                      enable-input-d {, zero-column-mask-desc };\n\n.kind = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n\n----------------------------------------------------------------------------------\n\n// 2. Integer type:\n\ntcgen05.mma.ws.cta_group::1.kind::i8{.collector_usage} [d-tmem],  a-desc,  b-desc, idesc,\n                                                       enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.cta_group::1.kind::i8{.collector_usage} [d-tmem], [a-tmem], b-desc, idesc,\n                                                       enable-input-d {, zero-column-mask-desc};\n\n.collector_usage = { .collector::buffer::op }\n::buffer = { ::b0, ::b1, ::b2, ::b3 }\n::op   = { ::fill, ::use, ::lastuse, ::discard}"
                ),
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.CtaGroup),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.CtaGroup),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.CtaGroup), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.CtaGroup), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    false
                  )
                )
              )
            ))
        )
    case Tcgen05MmaWsSp extends Definition(
          "tcgen05.mma.ws.sp",
          Opcode.Tcgen05,
          Vec(Modifier.Mma, Modifier.Ws, Modifier.Sp),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 6 && (operands.size == 6 || operands.size == 7)) Right(Instruction.Tcgen05MmaWsSp(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              operands(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.mma.ws.sp", Vec(6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-mma-instructions-mma-ws-sp",
                "9.7.17.10.9.4. TensorCore 5th Generation Instructions: tcgen05.mma.ws.sp",
                "tcgen05.mma.ws.sp",
                "TensorCore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 1. Floating-point type without block scaling:\n\ntcgen05.mma.ws.sp.cta_group::1.kind{.collector_usage} [d-tmem],  a-desc,  b-desc,\n                                                      [sp-meta-tmem] ,  idesc,\n                                                      enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.sp.cta_group::1.kind{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                      [sp-meta-tmem] , idesc,\n                                                      enable-input-d {, zero-column-mask-desc};\n\n.kind = { .kind::f16, .kind::tf32, .kind::f8f6f4 }\n\n----------------------------------------------------------------------------------\n\n// 2. Integer type:\n\ntcgen05.mma.ws.sp.cta_group::1.kind::i8{.collector_usage} [d-tmem], a-desc, b-desc,\n                                                          [sp-meta-tmem] , idesc,\n                                                          enable-input-d {, zero-column-mask-desc};\n\ntcgen05.mma.ws.sp.cta_group::1.kind::i8{.collector_usage} [d-tmem], [a-tmem], b-desc,\n                                                          [sp-meta-tmem] , idesc,\n                                                          enable-input-d {, zero-column-mask-desc};\n\n.collector_usage = { .collector::buffer::op }\n::buffer = { ::b0, ::b1, ::b2, ::b3 }\n::op   = { ::fill, ::use, ::lastuse, ::discard }"
                ),
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.CtaGroup),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.CtaGroup),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.CtaGroup), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.CtaGroup), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    false
                  )
                )
              )
            ))
        )
    case Tcgen05RelinquishAllocPermit extends Definition(
          "tcgen05.relinquish_alloc_permit",
          Opcode.Tcgen05,
          Vec(Modifier.RelinquishAllocPermit),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.Tcgen05RelinquishAllocPermit(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "tcgen05.relinquish_alloc_permit",
              Vec(0),
              operands.size
            ))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-alloc-dealloc-relinquish-alloc-permit",
                "9.7.17.7.1. Tensorcore 5th Generation Instructions: tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
                "tcgen05.alloc, tcgen05.dealloc, tcgen05.relinquish_alloc_permit",
                "Tensorcore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.alloc.cta_group.sync.aligned{.shared::cta}.b32  [dst], nCols;\n\ntcgen05.dealloc.cta_group.sync.aligned.b32              taddr, nCols;\n\ntcgen05.relinquish_alloc_permit.cta_group.sync.aligned;\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case Tcgen05Shift extends Definition(
          "tcgen05.shift",
          Opcode.Tcgen05,
          Vec(Modifier.Shift),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.Tcgen05Shift(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.shift", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-shift",
                "9.7.17.9.3. Tensorcore 5th Generation Instructions: tcgen05.shift",
                "tcgen05.shift",
                "Tensorcore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.shift.cta_group.down  [taddr];\n\n.cta_group = { .cta_group::1, .cta_group::2 }"
                ),
                Version(8, 6),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(Modifier.Down)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Tcgen05St extends Definition(
          "tcgen05.st",
          Opcode.Tcgen05,
          Vec(Modifier.St),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.Tcgen05St(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.st", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-st",
                "9.7.17.8.4. Tensorcore 5th Generation Instructions: tcgen05.st",
                "tcgen05.st",
                "Tensorcore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.st.sync.aligned.shape1.num{.unpack}.b32    [taddr], r;\n\ntcgen05.st.sync.aligned.shape2.num{.unpack}.b32    [taddr], immHalfSplitoff, r;\n\n.shape1 = { .16x64b, .16x128b, .16x256b, .32x32b }\n.shape2 = { .16x32bx2 }\n.num    = { .x1, .x2, .x4, .x8, .x16, .x32, .x64, .x128 }\n.unpack = { .unpack::16b }"
                ),
                Version(8, 6),
                Target(100),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Register),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(
                        Modifier.X1,
                        Modifier.X128,
                        Modifier.X16,
                        Modifier.X2,
                        Modifier.X32,
                        Modifier.X4,
                        Modifier.X64,
                        Modifier.X8
                      ),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Register),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(
                        Modifier.X1,
                        Modifier.X128,
                        Modifier.X16,
                        Modifier.X2,
                        Modifier.X32,
                        Modifier.X4,
                        Modifier.X64,
                        Modifier.X8
                      ),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Tcgen05WaitLd extends Definition(
          "tcgen05.wait::ld",
          Opcode.Tcgen05,
          Vec(Modifier.WaitLd),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.Tcgen05WaitLd(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.wait::ld", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-wait",
                "9.7.17.8.5. Tensorcore 5th Generation Instructions: tcgen05.wait",
                "tcgen05.wait",
                "Tensorcore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.wait_operation.sync.aligned;\n\n.wait_operation = { .wait::ld, .wait::st }"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case Tcgen05WaitSt extends Definition(
          "tcgen05.wait::st",
          Opcode.Tcgen05,
          Vec(Modifier.WaitSt),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.Tcgen05WaitSt(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tcgen05.wait::st", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "tcgen05-instructions-tcgen05-wait",
                "9.7.17.8.5. Tensorcore 5th Generation Instructions: tcgen05.wait",
                "tcgen05.wait",
                "Tensorcore 5th Generation Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tcgen05.wait_operation.sync.aligned;\n\n.wait_operation = { .wait::ld, .wait::st }"
                ),
                Version(8, 6),
                Target(100),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
                  false
                ))
              )
            ))
        )
    case TensorMapCopyFenceProxy extends Definition(
          "tensormap.cp_fenceproxy",
          Opcode.Tensormap,
          Vec(Modifier.CpFenceproxy),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.TensorMapCopyFenceProxy(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tensormap.cp_fenceproxy", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-tensormap-cp-fenceproxy",
                "9.7.14.17. Parallel Synchronization and Communication Instructions: tensormap.cp_fenceproxy",
                "tensormap.cp_fenceproxy",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tensormap.cp_fenceproxy.cp_qualifiers.fence_qualifiers.sync.aligned  [dst], [src], size;\n\n.cp_qualifiers    = { .global.shared::cta }\n.fence_qualifiers = { .to_proxy::from_proxy.release.scope }\n.to_proxy::from_proxy  = { .tensormap::generic }\n.scope            = { .cta, .cluster, .gpu , .sys }"
                ),
                Version(8, 3),
                Target(90),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case TensorMapReplace extends Definition(
          "tensormap.replace",
          Opcode.Tensormap,
          Vec(Modifier.Replace),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.TensorMapReplace(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tensormap.replace", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "data-movement-and-conversion-instructions-tensormap-replace",
                "9.7.9.27. Data Movement and Conversion Instructions: tensormap.replace",
                "tensormap.replace",
                "Data Movement and Conversion Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tensormap.replace.mode.field1{.ss}.b1024.type  [addr], new_val;\ntensormap.replace.mode.field2{.ss}.b1024.type  [addr], ord, new_val;\ntensormap.replace.mode.field3{.ss}.b1024.type  [addr], new_val;\n\n.mode    = { .tile }\n.field1  = { .global_address, .rank }\n.field2  = { .box_dim, .global_dim, .global_stride, .element_stride  }\n.field3  = { .elemtype,  .interleave_layout, .swizzle_mode, .swizzle_atomicity, .fill_mode }\n.ss      = { .global, .shared::cta }\n.type    = { .b32, .b64 }"
                ),
                Version(8, 3),
                Target(100),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Tile),
                      ModifierGroup(Modifier.GlobalAddress, Modifier.Rank),
                      ModifierGroup(Modifier.B1024),
                      ModifierGroup(Modifier.B32, Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.SharedCta),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Tile),
                      ModifierGroup(Modifier.BoxDim, Modifier.ElementStride, Modifier.GlobalDim, Modifier.GlobalStride),
                      ModifierGroup(Modifier.B1024),
                      ModifierGroup(Modifier.B32, Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.SharedCta),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Tile),
                      ModifierGroup(
                        Modifier.Elemtype,
                        Modifier.FillMode,
                        Modifier.InterleaveLayout,
                        Modifier.SwizzleAtomicity,
                        Modifier.SwizzleMode
                      ),
                      ModifierGroup(Modifier.B1024),
                      ModifierGroup(Modifier.B32, Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.SharedCta),
                    false
                  )
                )
              )
            ))
        )
    case TestPredicate extends Definition(
          "testp",
          Opcode.TestPredicate,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.TestPredicate(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("testp", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "floating-point-instructions-testp",
                "9.7.3.1. Floating Point Instructions: testp",
                "testp",
                "Floating Point Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "testp.op.type  p, a;  // result is .pred\n\n.op   = { .finite, .infinite,\n          .number, .notanumber,\n          .normal, .subnormal };\n.type = { .f32, .f64 };"
                ),
                Version(2, 0),
                Target(20),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(
                      Modifier.Finite,
                      Modifier.Infinite,
                      Modifier.Normal,
                      Modifier.Notanumber,
                      Modifier.Number,
                      Modifier.Subnormal
                    ),
                    ModifierGroup(Modifier.F32, Modifier.F64)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case Tex extends Definition(
          "tex",
          Opcode.Tex,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 2 && (operands.size == 2 || operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Tex(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tex", Vec(2, 3, 4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "texture-instructions-tex",
                "9.7.11.3. Texture Instructions: tex",
                "tex",
                "Texture Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tex.geom.v4.dtype.ctype  d, [a, c] {, e} {, f};\ntex.geom.v4.dtype.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\ntex.geom.v2.f16x2.ctype  d[|p], [a, c] {, e} {, f};\ntex.geom.v2.f16x2.ctype  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n// mipmaps\ntex.base.geom.v4.dtype.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v4.dtype.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v4.dtype.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\ntex.base.geom.v2.f16x2.ctype   d[|p], [a, {b,} c] {, e} {, f};\ntex.level.geom.v2.f16x2.ctype  d[|p], [a, {b,} c], lod {, e} {, f};\ntex.grad.geom.v2.f16x2.ctype   d[|p], [a, {b,} c], dPdx, dPdy {, e} {, f};\n\n.geom  = { .1d, .2d, .3d, .a1d, .a2d, .cube, .acube, .2dms, .a2dms };\n.dtype = { .u32, .s32, .f16,  .f32 };\n.ctype = {       .s32, .f32 };          // .cube, .acube require .f32\n                                        // .2dms, .a2dms require .s32"
                ),
                Version(1, 0),
                Target(10),
                Vec(
                  metadataTex_124_0(),
                  metadataTex_124_1(),
                  metadataTex_124_2(),
                  metadataTex_124_3(),
                  metadataTex_124_4(),
                  metadataTex_124_5(),
                  metadataTex_124_6(),
                  metadataTex_124_7(),
                  metadataTex_124_8(),
                  metadataTex_124_9()
                )
              )
            ))
        )
    case Tld4 extends Definition(
          "tld4",
          Opcode.Tld4,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3 || operands.size == 4))
              Right(Instruction.Tld4(operands(0), operands(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("tld4", Vec(2, 3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "texture-instructions-tld4",
                "9.7.11.4. Texture Instructions: tld4",
                "tld4",
                "Texture Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "tld4.comp.2d.v4.dtype.f32    d[|p], [a, c] {, e} {, f};\ntld4.comp.geom.v4.dtype.f32  d[|p], [a, b, c] {, e} {, f};  // explicit sampler\n\n.comp  = { .r, .g, .b, .a };\n.geom  = { .2d, .a2d, .cube, .acube };\n.dtype = { .u32, .s32, .f32 };"
                ),
                Version(2, 2),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.A, Modifier.B, Modifier.G, Modifier.R),
                      ModifierGroup(Modifier.V4),
                      ModifierGroup(Modifier.F32, Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.A, Modifier.B, Modifier.G, Modifier.R),
                      ModifierGroup(Modifier.A2d, Modifier.Acube, Modifier.Cube),
                      ModifierGroup(Modifier.V4),
                      ModifierGroup(Modifier.F32, Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Trap extends Definition(
          "trap",
          Opcode.Trap,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.Trap(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("trap", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "miscellaneous-instructions-trap",
                "9.7.20.4. Miscellaneous Instructions: trap",
                "trap",
                "Miscellaneous Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("trap;"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(Vec(), Vec(), _root_.scala.collection.immutable.Set(), true))
              )
            ))
        )
    case Txq extends Definition(
          "txq",
          Opcode.Txq,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.Txq(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("txq", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "texture-instructions-txq",
                "9.7.11.5. Texture Instructions: txq",
                "txq",
                "Texture Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "txq.tquery.b32         d, [a];       // texture attributes\ntxq.level.tlquery.b32  d, [a], lod;  // texture attributes\ntxq.squery.b32         d, [a];       // sampler attributes\n\n.tquery  = { .width, .height, .depth,\n             .channel_data_type, .channel_order,\n             .normalized_coords, .array_size,\n             .num_mipmap_levels, .num_samples};\n\n.tlquery = { .width, .height, .depth };\n\n.squery  = { .force_unnormalized_coords, .filter_mode,\n             .addr_mode_0, addr_mode_1, addr_mode_2 };"
                ),
                Version(1, 5),
                Target(10),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(
                        Modifier.ArraySize,
                        Modifier.ChannelDataType,
                        Modifier.ChannelOrder,
                        Modifier.Depth,
                        Modifier.Height,
                        Modifier.NormalizedCoords,
                        Modifier.NumMipmapLevels,
                        Modifier.NumSamples,
                        Modifier.Width
                      ),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Level),
                      ModifierGroup(Modifier.Depth, Modifier.Height, Modifier.Width),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(
                        Modifier.AddrMode0,
                        Modifier.AddrMode1,
                        Modifier.AddrMode2,
                        Modifier.FilterMode,
                        Modifier.ForceUnnormalizedCoords
                      ),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vabsdiff extends Definition(
          "vabsdiff",
          Opcode.Vabsdiff,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 2 && (operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Vabsdiff(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vabsdiff", Vec(3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
                "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
                "vadd, vsub, vabsdiff, vmin, vmax",
                "Scalar Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  )
                )
              )
            ))
        )
    case Vabsdiff2 extends Definition(
          "vabsdiff2",
          Opcode.Vabsdiff2,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vabsdiff2(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vabsdiff2", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vabsdiff4 extends Definition(
          "vabsdiff4",
          Opcode.Vabsdiff4,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vabsdiff4(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vabsdiff4", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vadd extends Definition(
          "vadd",
          Opcode.Vadd,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 2 && (operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Vadd(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vadd", Vec(3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
                "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
                "vadd, vsub, vabsdiff, vmin, vmax",
                "Scalar Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  )
                )
              )
            ))
        )
    case Vadd2 extends Definition(
          "vadd2",
          Opcode.Vadd2,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vadd2(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vadd2", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vadd4 extends Definition(
          "vadd4",
          Opcode.Vadd4,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vadd4(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vadd4", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vavrg2 extends Definition(
          "vavrg2",
          Opcode.Vavrg2,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vavrg2(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vavrg2", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vavrg4 extends Definition(
          "vavrg4",
          Opcode.Vavrg4,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vavrg4(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vavrg4", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vmad extends Definition(
          "vmad",
          Opcode.Vmad,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 1 && (operands.size == 1 || operands.size == 2 || operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Vmad(
              operands(0),
              operands.lift(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmad", Vec(1, 2, 3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "scalar-video-instructions-vmad",
                "9.7.19.1.3. Scalar Video Instructions: vmad",
                "vmad",
                "Scalar Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 32-bit scalar operation\nvmad.dtype.atype.btype{.sat}{.scale}     d, {-}a{.asel}, {-}b{.bsel},\n                                         {-}c;\nvmad.dtype.atype.btype.po{.sat}{.scale}  d, a{.asel}, b{.bsel}, c;\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.scale = { .shr7, .shr15 };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat, Modifier.Shr15, Modifier.Shr7),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Po)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat, Modifier.Shr15, Modifier.Shr7),
                    false
                  )
                )
              )
            ))
        )
    case Vmax extends Definition(
          "vmax",
          Opcode.Vmax,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 2 && (operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Vmax(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmax", Vec(3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
                "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
                "vadd, vsub, vabsdiff, vmin, vmax",
                "Scalar Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  )
                )
              )
            ))
        )
    case Vmax2 extends Definition(
          "vmax2",
          Opcode.Vmax2,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vmax2(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmax2", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vmax4 extends Definition(
          "vmax4",
          Opcode.Vmax4,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vmax4(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmax4", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vmin extends Definition(
          "vmin",
          Opcode.Vmin,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 2 && (operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Vmin(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmin", Vec(3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
                "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
                "vadd, vsub, vabsdiff, vmin, vmax",
                "Scalar Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  )
                )
              )
            ))
        )
    case Vmin2 extends Definition(
          "vmin2",
          Opcode.Vmin2,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vmin2(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmin2", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vmin4 extends Definition(
          "vmin4",
          Opcode.Vmin4,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vmin4(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmin4", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vote extends Definition(
          "vote",
          Opcode.Vote,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1 || operands.size == 2))
              Right(Instruction.Vote(operands(0), operands.lift(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vote", Vec(1, 2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-vote",
                "9.7.14.9. Parallel Synchronization and Communication Instructions: vote (deprecated)",
                "vote (deprecated)",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "vote.mode.pred  d, {!}a;\nvote.ballot.b32 d, {!}a;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"
                ),
                Version(1, 2),
                Target(12),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Unknown("unparsed")),
                    Vec(ModifierGroup(Modifier.All, Modifier.Any, Modifier.Uni), ModifierGroup(Modifier.Pred)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Unknown("unparsed")),
                    Vec(ModifierGroup(Modifier.Ballot), ModifierGroup(Modifier.B32)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case VoteSync extends Definition(
          "vote.sync",
          Opcode.Vote,
          Vec(Modifier.Sync),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.VoteSync(operands(0), operands.lift(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vote.sync", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "parallel-synchronization-and-communication-instructions-vote-sync",
                "9.7.14.10. Parallel Synchronization and Communication Instructions: vote.sync",
                "vote.sync",
                "Parallel Synchronization and Communication Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "vote.sync.mode.pred  d, {!}a, membermask;\nvote.sync.ballot.b32 d, {!}a, membermask;  // 'ballot' form, returns bitmask\n\n.mode = { .all, .any, .uni };"
                ),
                Version(6, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.All, Modifier.Any, Modifier.Uni), ModifierGroup(Modifier.Pred)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Unknown("unparsed"), OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Ballot), ModifierGroup(Modifier.B32)),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vset extends Definition(
          "vset",
          Opcode.Vset,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 2 && (operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Vset(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vset", Vec(3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "scalar-video-instructions-vset",
                "9.7.19.1.4. Scalar Video Instructions: vset",
                "vset",
                "Scalar Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 32-bit scalar operation, with optional secondary operation\nvset.atype.btype.cmp       d, a{.asel}, b{.bsel};\nvset.atype.btype.cmp.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvset.atype.btype.cmp  d.dsel, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vset2 extends Definition(
          "vset2",
          Opcode.Vset2,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vset2(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vset2", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vset2",
                "9.7.19.2.2. SIMD Video Instructions: vset2",
                "vset2",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvset2.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset2.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vset4 extends Definition(
          "vset4",
          Opcode.Vset4,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vset4(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vset4", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vset4",
                "9.7.19.2.4. SIMD Video Instructions: vset4",
                "vset4",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvset4.atype.btype.cmp  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvset4.atype.btype.cmp.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n.atype = .btype = { .u32, .s32 };\n.cmp   = { .eq, .ne, .lt, .le, .gt, .ge };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vshl extends Definition(
          "vshl",
          Opcode.Vshl,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 2 && (operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Vshl(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vshl", Vec(3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "scalar-video-instructions-vshl-vshr",
                "9.7.19.1.2. Scalar Video Instructions: vshl, vshr",
                "vshl, vshr",
                "Scalar Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.u32{.sat}.mode       d, a{.asel}, b{.bsel};\nvop.dtype.atype.u32{.sat}.mode.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.u32{.sat}.mode  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vshl, vshr };\n.dtype = .atype = { .u32, .s32 };\n.mode  = { .clamp, .wrap };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  )
                )
              )
            ))
        )
    case Vshr extends Definition(
          "vshr",
          Opcode.Vshr,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 2 && (operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Vshr(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vshr", Vec(3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "scalar-video-instructions-vshl-vshr",
                "9.7.19.1.2. Scalar Video Instructions: vshl, vshr",
                "vshl, vshr",
                "Scalar Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.u32{.sat}.mode       d, a{.asel}, b{.bsel};\nvop.dtype.atype.u32{.sat}.mode.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.u32{.sat}.mode  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vshl, vshr };\n.dtype = .atype = { .u32, .s32 };\n.mode  = { .clamp, .wrap };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  )
                )
              )
            ))
        )
    case Vsub extends Definition(
          "vsub",
          Opcode.Vsub,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 2 && (operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Vsub(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vsub", Vec(3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "scalar-video-instructions-vadd-vsub-vabsdiff-vmin-vmax",
                "9.7.19.1.1. Scalar Video Instructions: vadd, vsub, vabsdiff, vmin, vmax",
                "vadd, vsub, vabsdiff, vmin, vmax",
                "Scalar Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// 32-bit scalar operation, with optional secondary operation\nvop.dtype.atype.btype{.sat}       d, a{.asel}, b{.bsel};\nvop.dtype.atype.btype{.sat}.op2   d, a{.asel}, b{.bsel}, c;\n\n// 32-bit scalar operation, with optional data merge\nvop.dtype.atype.btype{.sat}  d.dsel, a{.asel}, b{.bsel}, c;\n\n vop   = { vadd, vsub, vabsdiff, vmin, vmax };\n.dtype = .atype = .btype = { .u32, .s32 };\n.dsel  = .asel  = .bsel  = { .b0, .b1, .b2, .b3, .h0, .h1 };\n.op2   = { .add, .min, .max };"
                ),
                Version(2, 0),
                Target(20),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed")
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  )
                )
              )
            ))
        )
    case Vsub2 extends Definition(
          "vsub2",
          Opcode.Vsub2,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vsub2(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vsub2", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd2-vsub2-vavrg2-vabsdiff2-vmin2-vmax2",
                "9.7.19.2.1. SIMD Video Instructions: vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop2.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop2.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\n\n vop2  = { vadd2, vsub2, vavrg2, vabsdiff2, vmin2, vmax2 };\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .h0, .h1, .h10 };  // defaults to .h10\n.asel  = .bsel  = { .hxy, where x,y are from { 0, 1, 2, 3 } };\n   .asel defaults to .h10\n   .bsel defaults to .h32"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case Vsub4 extends Definition(
          "vsub4",
          Opcode.Vsub4,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 3 && (operands.size == 4 || operands.size == 5 || operands.size == 6 || operands.size == 7)
            ) Right(Instruction.Vsub4(
              operands(0),
              operands.lift(1),
              operands(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("vsub4", Vec(4, 5, 6, 7), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "simd-video-instructions-vadd4-vsub4-vavrg4-vabsdiff4-vmin4-vmax4",
                "9.7.19.2.3. SIMD Video Instructions: vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4",
                "SIMD Video Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// SIMD instruction with secondary SIMD merge operation\nvop4.dtype.atype.btype{.sat}  d{.mask}, a{.asel}, b{.bsel}, c;\n\n// SIMD instruction with secondary accumulate operation\nvop4.dtype.atype.btype.add  d{.mask}, a{.asel}, b{.bsel}, c;\nvop4  = { vadd4, vsub4, vavrg4, vabsdiff4, vmin4, vmax4 };\n\n.dtype = .atype = .btype = { .u32, .s32 };\n.mask  = { .b0,\n           .b1, .b10\n           .b2, .b20, .b21, .b210,\n           .b3, .b30, .b31, .b310, .b32, .b320, .b321, .b3210 };\n    defaults to .b3210\n.asel = .bsel = .bxyzw, where x,y,z,w are from { 0, ..., 7 };\n   .asel defaults to .b3210\n   .bsel defaults to .b7654"
                ),
                Version(3, 0),
                Target(30),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value,
                      OperandConstraint.Unknown("unparsed"),
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case WgmmaCommitGroup extends Definition(
          "wgmma.commit_group",
          Opcode.Wgmma,
          Vec(Modifier.CommitGroup),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.WgmmaCommitGroup(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("wgmma.commit_group", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "asynchronous-warpgroup-level-matrix-instructions-wgmma-commit-group",
                "9.7.16.7.2. Asynchronous Multiply-and-Accumulate Instruction: wgmma.commit_group",
                "wgmma.commit_group",
                "Asynchronous Multiply-and-Accumulate Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("wgmma.commit_group.sync.aligned;"),
                Version(8, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case WgmmaFence extends Definition(
          "wgmma.fence",
          Opcode.Wgmma,
          Vec(Modifier.Fence),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 0 && (operands.size == 0)) Right(Instruction.WgmmaFence(mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("wgmma.fence", Vec(0), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "asynchronous-warpgroup-level-matrix-instructions-wgmma-fence",
                "9.7.16.7.1. Asynchronous Multiply-and-Accumulate Instruction: wgmma.fence",
                "wgmma.fence",
                "Asynchronous Multiply-and-Accumulate Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("wgmma.fence.sync.aligned;"),
                Version(8, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case WgmmaMmaAsync extends Definition(
          "wgmma.mma_async",
          Opcode.Wgmma,
          Vec(Modifier.MmaAsync),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 4 && (operands.size == 4 || operands.size == 6 || operands.size == 7 || operands.size == 8)
            ) Right(Instruction.WgmmaMmaAsync(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands.lift(4),
              operands.lift(5),
              operands.lift(6),
              operands.lift(7),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("wgmma.mma_async", Vec(4, 6, 7, 8), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "asynchronous-warpgroup-level-matrix-instructions-wgmma-mma",
                "9.7.16.5.2. Asynchronous Multiply-and-Accumulate Instruction: wgmma.mma_async",
                "wgmma.mma_async",
                "Asynchronous Multiply-and-Accumulate Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "wgmma.mma_async.sync.aligned.shape.dtype.f16.f16  d, a-desc, b-desc, scale-d, imm-scale-a, imm-scale-b, imm-trans-a, imm-trans-b;\n\nwgmma.mma_async.sync.aligned.shape.dtype.f16.f16  d, a, b-desc, scale-d, imm-scale-a, imm-scale-b, imm-trans-b;\n\n.shape   = {.m64n8k16, .m64n16k16, .m64n24k16, .m64n32k16,\n            .m64n40k16, .m64n48k16, .m64n56k16, .m64n64k16,\n            .m64n72k16, .m64n80k16, .m64n88k16, .m64n96k16,\n            .m64n104k16, .m64n112k16, .m64n120k16, .m64n128k16,\n            .m64n136k16, .m64n144k16, .m64n152k16, .m64n160k16,\n            .m64n168k16, .m64n176k16, .m64n184k16, .m64n192k16,\n            .m64n200k16, .m64n208k16, .m64n216k16, .m64n224k16,\n            .m64n232k16, .m64n240k16, .m64n248k16, .m64n256k16};\n.dtype   = {.f16, .f32};",
                  ".bf16 floating point type:\n\nwgmma.mma_async.sync.aligned.shape.dtype.bf16.bf16  d, a-desc, b-desc, scale-d, imm-scale-a, imm-scale-b, imm-trans-a, imm-trans-b;\n\nwgmma.mma_async.sync.aligned.shape.dtype.bf16.bf16  d, a, b-desc, scale-d, imm-scale-a, imm-scale-b, imm-trans-b;\n\n.shape   = {.m64n8k16, .m64n16k16, .m64n24k16, .m64n32k16,\n            .m64n40k16, .m64n48k16, .m64n56k16, .m64n64k16,\n            .m64n72k16, .m64n80k16, .m64n88k16, .m64n96k16,\n            .m64n104k16, .m64n112k16, .m64n120k16, .m64n128k16,\n            .m64n136k16, .m64n144k16, .m64n152k16, .m64n160k16,\n            .m64n168k16, .m64n176k16, .m64n184k16, .m64n192k16,\n            .m64n200k16, .m64n208k16, .m64n216k16, .m64n224k16,\n            .m64n232k16, .m64n240k16, .m64n248k16, .m64n256k16};\n.dtype  = {.f32};\n\n.tf32 floating point type:\n\nwgmma.mma_async.sync.aligned.shape.dtype.tf32.tf32  d, a-desc, b-desc, scale-d, imm-scale-a, imm-scale-b;\n\nwgmma.mma_async.sync.aligned.shape.dtype.tf32.tf32  d, a, b-desc, scale-d, imm-scale-a, imm-scale-b;\n\n.shape   = {.m64n8k8, .m64n16k8, .m64n24k8, .m64n32k8,\n            .m64n40k8, .m64n48k8, .m64n56k8, .m64n64k8,\n            .m64n72k8, .m64n80k8, .m64n88k8, .m64n96k8,\n            .m64n104k8, .m64n112k8, .m64n120k8, .m64n128k8,\n            .m64n136k8, .m64n144k8, .m64n152k8, .m64n160k8,\n            .m64n168k8, .m64n176k8, .m64n184k8, .m64n192k8,\n            .m64n200k8, .m64n208k8, .m64n216k8, .m64n224k8,\n            .m64n232k8, .m64n240k8, .m64n248k8, .m64n256k8};\n.dtype  = {.f32};\n\nFP8 floating point type\n\nwgmma.mma_async.sync.aligned.shape.dtype.atype.btype  d, a-desc, b-desc, scale-d, imm-scale-a, imm-scale-b;\n\nwgmma.mma_async.sync.aligned.shape.dtype.atype.btype  d, a, b-desc, scale-d, imm-scale-a, imm-scale-b;\n\n.shape   = {.m64n8k32, .m64n16k32, .m64n24k32, .m64n32k32,\n            .m64n40k32, .m64n48k32, .m64n56k32, .m64n64k32,\n            .m64n72k32, .m64n80k32, .m64n88k32, .m64n96k32,\n            .m64n104k32, .m64n112k32, .m64n120k32, .m64n128k32,\n            .m64n136k32, .m64n144k32, .m64n152k32, .m64n160k32,\n            .m64n168k32, .m64n176k32, .m64n184k32, .m64n192k32,\n            .m64n200k32, .m64n208k32, .m64n216k32, .m64n224k32,\n            .m64n232k32, .m64n240k32, .m64n248k32, .m64n256k32};\n.atype  = {.e4m3, .e5m2};\n.btype  = {.e4m3, .e5m2};\n.dtype  = {.f16, .f32};",
                  "wgmma.mma_async.sync.aligned.shape{.satfinite}.s32.atype.btype  d, a-desc, b-desc, scale-d;\n\nwgmma.mma_async.sync.aligned.shape{.satfinite}.s32.atype.btype  d, a, b-desc, scale-d;\n\n.shape   = {.m64n8k32, .m64n16k32, .m64n24k32, .m64n32k32,\n            .m64n48k32, .m64n64k32, .m64n80k32, .m64n96k32,\n            .m64n112k32, .m64n128k32, .m64n144k32, .m64n160k32,\n            .m64n176k32, .m64n192k32, .m64n208k32, .m64n224k32};\n.atype  = {.s8, .u8};\n.btype  = {.s8, .u8};",
                  "wgmma.mma_async.sync.aligned.shape.s32.b1.b1.op.popc  d, a-desc, b-desc, scale-d;\n\nwgmma.mma_async.sync.aligned.shape.s32.b1.b1.op.popc  d, a, b-desc, scale-d;\n\n.shape   = {.m64n8k256, .m64n16k256, .m64n24k256, .m64n32k256,\n            .m64n48k256, .m64n64k256, .m64n80k256, .m64n96k256,\n            .m64n112k256, .m64n128k256, .m64n144k256, .m64n160k256,\n            .m64n176k256, .m64n192k256, .m64n208k256, .m64n224k256,\n            .m64n240k256, .m64n256k256};\n.op  = {.and};"
                ),
                Version(8, 0),
                Target(10),
                Vec(
                  metadataWgmmaMmaAsync_175_0(),
                  metadataWgmmaMmaAsync_175_1(),
                  metadataWgmmaMmaAsync_175_2(),
                  metadataWgmmaMmaAsync_175_3(),
                  metadataWgmmaMmaAsync_175_4(),
                  metadataWgmmaMmaAsync_175_5(),
                  metadataWgmmaMmaAsync_175_6(),
                  metadataWgmmaMmaAsync_175_7(),
                  metadataWgmmaMmaAsync_175_8(),
                  metadataWgmmaMmaAsync_175_9(),
                  metadataWgmmaMmaAsync_175_10(),
                  metadataWgmmaMmaAsync_175_11()
                )
              )
            ))
        )
    case WgmmaMmaAsyncSp extends Definition(
          "wgmma.mma_async.sp",
          Opcode.Wgmma,
          Vec(Modifier.MmaAsync, Modifier.Sp),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (
              operands.size >= 6 && (operands.size == 6 || operands.size == 8 || operands.size == 9 || operands.size == 10)
            ) Right(Instruction.WgmmaMmaAsyncSp(
              operands(0),
              operands(1),
              operands(2),
              operands(3),
              operands(4),
              operands(5),
              operands.lift(6),
              operands.lift(7),
              operands.lift(8),
              operands.lift(9),
              mods,
              pred
            ))
            else
              Left(Instruction.DecodeError.InvalidOperandCount("wgmma.mma_async.sp", Vec(6, 8, 9, 10), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "asynchronous-warpgroup-level-matrix-instructions-wgmma-mma-sp",
                "9.7.16.6.3. Asynchronous Multiply-and-Accumulate Instruction: wgmma.mma_async.sp",
                "wgmma.mma_async.sp",
                "Asynchronous Multiply-and-Accumulate Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "wgmma.mma_async.sp.sync.aligned.shape.dtype.f16.f16  d, a-desc, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b, imm-trans-a, imm-trans-b;\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.f16.f16  d, a, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b, imm-trans-b;\n\n.shape   = {.m64n8k32, .m64n16k32, .m64n24k32, .m64n32k32,\n            .m64n40k32, .m64n48k32, .m64n56k32, .m64n64k32,\n            .m64n72k32, .m64n80k32, .m64n88k32, .m64n96k32,\n            .m64n104k32, .m64n112k32, .m64n120k32, .m64n128k32,\n            .m64n136k32, .m64n144k32, .m64n152k32, .m64n160k32,\n            .m64n168k32, .m64n176k32, .m64n184k32, .m64n192k32,\n            .m64n200k32, .m64n208k32, .m64n216k32, .m64n224k32,\n            .m64n232k32, .m64n240k32, .m64n248k32, .m64n256k32};\n.dtype   = {.f16, .f32};",
                  ".bf16 floating point type:\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.bf16.bf16  d, a-desc, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b, imm-trans-a, imm-trans-b;\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.bf16.bf16  d, a, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b, imm-trans-b;\n\n.shape   = {.m64n8k32, .m64n16k32, .m64n24k32, .m64n32k32,\n            .m64n40k32, .m64n48k32, .m64n56k32, .m64n64k32,\n            .m64n72k32, .m64n80k32, .m64n88k32, .m64n96k32,\n            .m64n104k32, .m64n112k32, .m64n120k32, .m64n128k32,\n            .m64n136k32, .m64n144k32, .m64n152k32, .m64n160k32,\n            .m64n168k32, .m64n176k32, .m64n184k32, .m64n192k32,\n            .m64n200k32, .m64n208k32, .m64n216k32, .m64n224k32,\n            .m64n232k32, .m64n240k32, .m64n248k32, .m64n256k32};\n.dtype  = {.f32};\n\n.tf32 floating point type:\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.tf32.tf32  d, a-desc, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b;\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.tf32.tf32  d, a, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b;\n\n.shape   = {.m64n8k16, .m64n16k16, .m64n24k16, .m64n32k16,\n            .m64n40k16, .m64n48k16, .m64n56k16, .m64n64k16,\n            .m64n72k16, .m64n80k16, .m64n88k16, .m64n96k16,\n            .m64n104k16, .m64n112k16, .m64n120k16, .m64n128k16,\n            .m64n136k16, .m64n144k16, .m64n152k16, .m64n160k16,\n            .m64n168k16, .m64n176k16, .m64n184k16, .m64n192k16,\n            .m64n200k16, .m64n208k16, .m64n216k16, .m64n224k16,\n            .m64n232k16, .m64n240k16, .m64n248k16, .m64n256k16};\n.dtype  = {.f32};\n\nFP8 floating point type\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.atype.btype  d, a-desc, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b;\n\nwgmma.mma_async.sp.sync.aligned.shape.dtype.atype.btype  d, a, b-desc, sp-meta, sp-sel, scale-d, imm-scale-a, imm-scale-b;\n\n.shape   = {.m64n8k64, .m64n16k64, .m64n24k64, .m64n32k64,\n            .m64n40k64, .m64n48k64, .m64n56k64, .m64n64k64,\n            .m64n72k64, .m64n80k64, .m64n88k64, .m64n96k64,\n            .m64n104k64, .m64n112k64, .m64n120k64, .m64n128k64,\n            .m64n136k64, .m64n144k64, .m64n152k64, .m64n160k64,\n            .m64n168k64, .m64n176k64, .m64n184k64, .m64n192k64,\n            .m64n200k64, .m64n208k64, .m64n216k64, .m64n224k64,\n            .m64n232k64, .m64n240k64, .m64n248k64, .m64n256k64};\n.atype  = {.e4m3, .e5m2};\n.btype  = {.e4m3, .e5m2};\n.dtype  = {.f16, .f32};",
                  "wgmma.mma_async.sp.sync.aligned.shape{.satfinite}.s32.atype.btype  d, a-desc, b-desc, sp-meta, sp-sel, scale-d;\n\nwgmma.mma_async.sp.sync.aligned.shape{.satfinite}.s32.atype.btype  d, a, b-desc, sp-meta, sp-sel, scale-d;\n\n.shape   = {.m64n8k64, .m64n16k64, .m64n24k64, .m64n32k64,\n            .m64n48k64, .m64n64k64, .m64n80k64, .m64n96k64,\n            .m64n112k64, .m64n128k64, .m64n144k64, .m64n160k64,\n            .m64n176k64, .m64n192k64, .m64n208k64, .m64n224k64,\n            .m64n240k64, .m64n256k64};\n.atype  = {.s8, .u8};\n.btype  = {.s8, .u8};"
                ),
                Version(8, 2),
                Target(10),
                Vec(
                  metadataWgmmaMmaAsyncSp_176_0(),
                  metadataWgmmaMmaAsyncSp_176_1(),
                  metadataWgmmaMmaAsyncSp_176_2(),
                  metadataWgmmaMmaAsyncSp_176_3(),
                  metadataWgmmaMmaAsyncSp_176_4(),
                  metadataWgmmaMmaAsyncSp_176_5(),
                  metadataWgmmaMmaAsyncSp_176_6(),
                  metadataWgmmaMmaAsyncSp_176_7(),
                  metadataWgmmaMmaAsyncSp_176_8(),
                  metadataWgmmaMmaAsyncSp_176_9()
                )
              )
            ))
        )
    case WgmmaWaitGroup extends Definition(
          "wgmma.wait_group",
          Opcode.Wgmma,
          Vec(Modifier.WaitGroup),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 1 && (operands.size == 1)) Right(Instruction.WgmmaWaitGroup(operands(0), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("wgmma.wait_group", Vec(1), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "asynchronous-warpgroup-level-matrix-instructions-wgmma-wait-group",
                "9.7.16.7.3. Asynchronous Multiply-and-Accumulate Instruction: wgmma.wait_group",
                "wgmma.wait_group",
                "Asynchronous Multiply-and-Accumulate Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("wgmma.wait_group.sync.aligned N;"),
                Version(8, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )
    case WmmaLoad extends Definition(
          "wmma.load",
          Opcode.Wmma,
          Vec(Modifier.Load),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.WmmaLoad(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("wmma.load", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "warp-level-matrix-instructions-wmma-ld",
                "9.7.15.4.3. Warp-level Matrix Load Instruction: wmma.load",
                "wmma.load",
                "Warp-level Matrix Load Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "wmma.load.a.sync.aligned.layout.shape{.ss}.atype r, [p] {, stride};\nwmma.load.b.sync.aligned.layout.shape{.ss}.btype r, [p] {, stride};\nwmma.load.c.sync.aligned.layout.shape{.ss}.ctype r, [p] {, stride};\n\n.layout = {.row, .col};\n.shape  = {.m16n16k16, .m8n32k16, .m32n8k16};\n.ss     = {.global, .shared{::cta}};\n.atype  = {.f16, .s8, .u8};\n.btype  = {.f16, .s8, .u8};\n.ctype  = {.f16, .f32, .s32};",
                  "wmma.load.a.sync.aligned.layout.shape{.ss}.atype r, [p] {, stride}\nwmma.load.b.sync.aligned.layout.shape{.ss}.btype r, [p] {, stride}\nwmma.load.c.sync.aligned.layout.shape{.ss}.ctype r, [p] {, stride}\n.layout = {.row, .col};\n.shape  = {.m16n16k16, .m8n32k16, .m32n8k16};\n.ss     = {.global, .shared{::cta}};\n.atype  = {.bf16 };\n.btype  = {.bf16 };\n.ctype  = {.f32 };",
                  "wmma.load.a.sync.aligned.layout.shape{.ss}.atype r, [p] {, stride}\nwmma.load.b.sync.aligned.layout.shape{.ss}.btype r, [p] {, stride}\nwmma.load.c.sync.aligned.layout.shape{.ss}.ctype r, [p] {, stride}\n.layout = {.row, .col};\n.shape  = {.m16n16k8 };\n.ss     = {.global, .shared{::cta}};\n.atype  = {.tf32 };\n.btype  = {.tf32 };\n.ctype  = {.f32 };",
                  "wmma.load.a.sync.aligned.layout.shape{.ss}.atype r, [p] {, stride}\nwmma.load.b.sync.aligned.layout.shape{.ss}.btype r, [p] {, stride}\nwmma.load.c.sync.aligned.layout.shape{.ss}.ctype r, [p] {, stride}\n.layout = {.row, .col};\n.shape  = {.m8n8k4 };\n.ss     = {.global, .shared{::cta}};\n.atype  = {.f64 };\n.btype  = {.f64 };\n.ctype  = {.f64 };",
                  "wmma.load.a.sync.aligned.row.shape{.ss}.atype r, [p] {, stride}\nwmma.load.b.sync.aligned.col.shape{.ss}.btype r, [p] {, stride}\nwmma.load.c.sync.aligned.layout.shape{.ss}.ctype r, [p] {, stride}\n.layout = {.row, .col};\n.shape  = {.m8n8k32};\n.ss     = {.global, .shared{::cta}};\n.atype  = {.s4, .u4};\n.btype  = {.s4, .u4};\n.ctype  = {.s32};",
                  "wmma.load.a.sync.aligned.row.shape{.ss}.atype r, [p] {, stride}\nwmma.load.b.sync.aligned.col.shape{.ss}.btype r, [p] {, stride}\nwmma.load.c.sync.aligned.layout.shape{.ss}.ctype r, [p] {, stride}\n.layout = {.row, .col};\n.shape  = {.m8n8k128};\n.ss     = {.global, .shared{::cta}};\n.atype  = {.b1};\n.btype  = {.b1};\n.ctype  = {.s32};"
                ),
                Version(6, 0),
                Target(70),
                Vec(
                  metadataWmmaLoad_167_0(),
                  metadataWmmaLoad_167_1(),
                  metadataWmmaLoad_167_2(),
                  metadataWmmaLoad_167_3(),
                  metadataWmmaLoad_167_4(),
                  metadataWmmaLoad_167_5(),
                  metadataWmmaLoad_167_6(),
                  metadataWmmaLoad_167_7(),
                  metadataWmmaLoad_167_8(),
                  metadataWmmaLoad_167_9(),
                  metadataWmmaLoad_167_10(),
                  metadataWmmaLoad_167_11(),
                  metadataWmmaLoad_167_12(),
                  metadataWmmaLoad_167_13(),
                  metadataWmmaLoad_167_14(),
                  metadataWmmaLoad_167_15(),
                  metadataWmmaLoad_167_16(),
                  metadataWmmaLoad_167_17()
                )
              )
            ))
        )
    case WmmaMma extends Definition(
          "wmma.mma",
          Opcode.Wmma,
          Vec(Modifier.Mma),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.WmmaMma(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("wmma.mma", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "warp-level-matrix-instructions-wmma-mma",
                "9.7.15.4.5. Warp-level Matrix Multiply-and-Accumulate Instruction: wmma.mma",
                "wmma.mma",
                "Warp-level Matrix Multiply-and-Accumulate Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "// Floating point (.f16 multiplicands) wmma.mma\nwmma.mma.sync.aligned.alayout.blayout.shape.dtype.ctype d, a, b, c;\n\n// Integer (.u8/.s8 multiplicands) wmma.mma\nwmma.mma.sync.aligned.alayout.blayout.shape.s32.atype.btype.s32{.satfinite} d, a, b, c;\n\n.alayout = {.row, .col};\n.blayout = {.row, .col};\n.shape  =  {.m16n16k16, .m8n32k16, .m32n8k16};\n.dtype   = {.f16, .f32};\n.atype   = {.s8, .u8};\n.btype   = {.s8, .u8};\n.ctype   = {.f16, .f32};",
                  "wmma.mma.sync.aligned.alayout.blayout.shape.f32.atype.btype.f32 d, a, b, c;\n.alayout = {.row, .col};\n.blayout = {.row, .col};\n.shape   = {.m16n16k16, .m8n32k16, .m32n8k16};\n.atype   = {.bf16 };\n.btype   = {.bf16};",
                  "wmma.mma.sync.aligned.alayout.blayout.shape.f32.atype.btype.f32 d, a, b, c;\n.alayout = {.row, .col};\n.blayout = {.row, .col};\n.shape   = {.m16n16k8 };\n.atype   = {.tf32 };\n.btype   = {.tf32};",
                  "wmma.mma.sync.aligned.alayout.blayout.shape{.rnd}.f64.f64.f64.f64 d, a, b, c;\n.alayout = {.row, .col};\n.blayout = {.row, .col};\n.shape   = {.m8n8k4 };\n.rnd = { .rn, .rz, .rm, .rp };",
                  "wmma.mma.sync.aligned.row.col.shape.s32.atype.btype.s32{.satfinite} d, a, b, c;\n.shape  = {.m8n8k32};\n.atype  = {.s4, .u4};\n.btype  = {.s4, .u4};",
                  "wmma.mma.op.popc.sync.aligned.row.col.shape.s32.atype.btype.s32 d, a, b, c;\n.shape  = {.m8n8k128};\n.atype  = {.b1};\n.btype  = {.b1};\n.op     = {.xor, .and}"
                ),
                Version(6, 0),
                Target(70),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
                      ModifierGroup(Modifier.F16, Modifier.F32),
                      ModifierGroup(Modifier.F16, Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
                      ModifierGroup(Modifier.S32),
                      ModifierGroup(Modifier.S8, Modifier.U8),
                      ModifierGroup(Modifier.S8, Modifier.U8),
                      ModifierGroup(Modifier.S32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
                      ModifierGroup(Modifier.F32),
                      ModifierGroup(Modifier.Bf16),
                      ModifierGroup(Modifier.Bf16),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M16n16k8),
                      ModifierGroup(Modifier.F32),
                      ModifierGroup(Modifier.Tf32),
                      ModifierGroup(Modifier.Tf32),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M8n8k4),
                      ModifierGroup(Modifier.F64),
                      ModifierGroup(Modifier.F64),
                      ModifierGroup(Modifier.F64),
                      ModifierGroup(Modifier.F64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Row),
                      ModifierGroup(Modifier.Col),
                      ModifierGroup(Modifier.M8n8k32),
                      ModifierGroup(Modifier.S32),
                      ModifierGroup(Modifier.S4, Modifier.U4),
                      ModifierGroup(Modifier.S4, Modifier.U4),
                      ModifierGroup(Modifier.S32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
                    false
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.And, Modifier.Xor),
                      ModifierGroup(Modifier.Popc),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Row),
                      ModifierGroup(Modifier.Col),
                      ModifierGroup(Modifier.M8n8k128),
                      ModifierGroup(Modifier.S32),
                      ModifierGroup(Modifier.B1),
                      ModifierGroup(Modifier.B1),
                      ModifierGroup(Modifier.S32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              )
            ))
        )
    case WmmaStore extends Definition(
          "wmma.store",
          Opcode.Wmma,
          Vec(Modifier.Store),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.WmmaStore(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("wmma.store", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "warp-level-matrix-instructions-wmma-st",
                "9.7.15.4.4. Warp-level Matrix Store Instruction: wmma.store",
                "wmma.store",
                "Warp-level Matrix Store Instruction",
                Vec.nonEmpty[NonEmpty[java.lang.String]](
                  "wmma.store.d.sync.aligned.layout.shape{.ss}.type [p], r {, stride};\n\n.layout = {.row, .col};\n.shape  = {.m16n16k16, .m8n32k16, .m32n8k16};\n.ss     = {.global, .shared{::cta}};\n.type   = {.f16, .f32, .s32};\n\nwmma.store.d.sync.aligned.layout.shape{.ss}.type [p], r {, stride}\n.layout = {.row, .col};\n.shape  = {.m8n8k32, .m8n8k128};\n.ss     = {.global, .shared{::cta}};\n.type   = {.s32};\n\nwmma.store.d.sync.aligned.layout.shape{.ss}.type [p], r {, stride}\n.layout = {.row, .col};\n.shape  = {.m16n16k8};\n.ss     = {.global, .shared{::cta}};\n.type   = {.f32};\n\nwmma.store.d.sync.aligned.layout.shape{.ss}.type [p], r {, stride}\n.layout = {.row, .col};\n.shape  = {.m8n8k4 };\n.ss     = {.global, .shared{::cta}};\n.type   = {.f64};"
                ),
                Version(6, 0),
                Target(70),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.D),
                    ModifierGroup(Modifier.Sync),
                    ModifierGroup(Modifier.Aligned),
                    ModifierGroup(Modifier.Col, Modifier.Row),
                    ModifierGroup(Modifier.M8n8k4),
                    ModifierGroup(Modifier.F64)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Global),
                  false
                ))
              )
            ))
        )
    case Xor extends Definition(
          "xor",
          Opcode.Xor,
          Vec(),
          (
              operands: Vec[Operand],
              mods: Vec[Modifier],
              pred: Option[Predication]
          ) => {
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.Xor(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("xor", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                "logic-and-shift-instructions-xor",
                "9.7.8.3. Logic and Shift Instructions: xor",
                "xor",
                "Logic and Shift Instructions",
                Vec.nonEmpty[NonEmpty[java.lang.String]]("xor.type d, a, b;\n\n.type = { .pred, .b16, .b32, .b64 };"),
                Version(1, 0),
                Target(10),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.Pred)),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )

    lazy val meta: Metadata = metadata()

  }

  val version: Version = Version(9, 3)

  val definitions: Vec[Definition] = Definition.values.toVec

  private lazy val DefinitionsByMnemonic: Map[String, Vec[Definition]] =
    definitions.groupMap(_.opcode.takeWhile(_ != '.'))(value => value)

  def decode(
      opcode: String,
      operands: Vec[Operand],
      mods: Vec[Modifier] = Vec.empty,
      pred: Option[Predication] = None
  ): Either[DecodeError, Instruction] = {
    val opcodeParts = Vec.from(opcode.toLowerCaseRoot.split('.')).flatMap(_.refine[NonEmpty[String]].toOption)
    val name = opcodeParts.headOption.getOrElse("")
    val allModifiers = opcodeParts.drop(1).map(value => Modifier(value)) ++ mods
    decodeParsed(name, operands, allModifiers, pred)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def decodeParsed(
      mnemonic: String,
      operands: Vec[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ): Either[DecodeError, Instruction] = {
    val name = mnemonic.toLowerCaseRoot
    val allModifiers = mods
    val definitions = DefinitionsByMnemonic.getOrElse(name, Vec.empty[Definition])
    var specificity = -1
    var index = 0
    while (index < definitions.size) {
      val definition = definitions(index)
      if (definition.fixedModifiers.size > specificity && fixedModifiersMatch(allModifiers, definition.fixedModifiers))
        specificity = definition.fixedModifiers.size
      index += 1
    }

    var successful: Option[(Definition, Instruction)] = None
    var ambiguous = Vec.empty[String]
    var expected = Vec.empty[Int]
    index = 0
    while (index < definitions.size) {
      val definition = definitions(index)
      if (definition.fixedModifiers.size == specificity && fixedModifiersMatch(allModifiers, definition.fixedModifiers))
        definition.decoder(operands, allModifiers.drop(specificity), pred) match {
          case Right(instruction) => successful match {
              case None => successful = Some(definition -> instruction)
              case Some((first, _)) =>
                if (ambiguous.isEmpty) ambiguous = Vec(first.toString)
                ambiguous = ambiguous :+ definition.toString
            }
          case Left(DecodeError.InvalidOperandCount(_, counts, _)) => expected = expected ++ counts
          case Left(_) => ()
        }
      index += 1
    }

    if (ambiguous.nonEmpty)
      Left(DecodeError.AmbiguousInstruction(qualifiedOpcode(name, allModifiers), ambiguous))
    else successful match {
      case Some((_, instruction)) => Right(instruction)
      case None if expected.nonEmpty =>
        Left(DecodeError.InvalidOperandCount(
          qualifiedOpcode(name, allModifiers),
          expected.distinct.sorted,
          operands.size
        ))
      case None => Left(DecodeError.UnknownOpcode(qualifiedOpcode(name, allModifiers)))
    }
  }

  private def fixedModifiersMatch(modifiers: Vec[Modifier], fixed: Vec[Modifier]): Boolean =
    fixed.size <= modifiers.size && fixed.indices.forall(index => modifiers(index) == fixed(index))

  private def qualifiedOpcode(name: String, modifiers: Vec[Modifier]): String =
    (name +: modifiers.map(_.name)).mkString(".")
}

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum Instruction(val definition: Instruction.Definition, operandValues: Operand*) {
  case Abs(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Abs, dest, srcA)
  case Activemask(dest: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Activemask, dest)
  case Add(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Add, dest, srcA, srcB)
  case AddCc(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.AddCc, dest, srcA, srcB)
  case Addc(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Addc, dest, srcA, srcB)
  case Alloca(
      pointer: Operand,
      size: Operand,
      alignment: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Alloca, (Vec(pointer, size) ++ alignment.toVec)*)
  case And(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.And, dest, srcA, srcB)
  case ApplyPriority(address: Operand, size: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.ApplyPriority, address, size)
  case Atom(
      dest: Operand,
      address: Operand,
      value: Operand,
      compareOrCachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Atom, (Vec(dest, address, value) ++ compareOrCachePolicy.toVec)*)
  case Bar(
      barrier: Operand,
      threadCount: Option[Operand],
      predicate: Option[Operand],
      value: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Bar,
        (Vec(barrier) ++ threadCount.toVec ++ predicate.toVec ++ value.toVec)*
      )
  case BarWarpSync(barrier: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.BarWarpSync, barrier)
  case Barrier(
      barrier: Operand,
      threadCount: Option[Operand],
      predicate: Option[Operand],
      value: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Barrier,
        (Vec(barrier) ++ threadCount.toVec ++ predicate.toVec ++ value.toVec)*
      )
  case BarrierCluster(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.BarrierCluster)
  case Bfe(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Bfe, dest, srcA, srcB, srcC)
  case Bfi(
      dest: Operand,
      insertValue: Operand,
      baseValue: Operand,
      bitPosition: Operand,
      bitLength: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Bfi, dest, insertValue, baseValue, bitPosition, bitLength)
  case Bfind(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Bfind, dest, srcA)
  case Bmsk(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Bmsk, dest, srcA, srcB)
  case Bra(target: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Bra, target)
  case Brev(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Brev, dest, srcA)
  case Brkpt(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.Brkpt)
  case BrxIdx(index: Operand, tlist: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.BrxIdx, index, tlist)
  case Call(
      returnParams: Option[Operand],
      function: Operand,
      params: Option[Operand],
      targets: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Call,
        (returnParams.toVec ++ Vec(function) ++ params.toVec ++ targets.toVec)*
      )
  case Clmad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Clmad, dest, srcA, srcB, srcC)
  case ClusterLaunchControlQueryCancel(
      predicate: Option[Operand],
      tryCancelResponse: Option[Operand],
      zdim: Option[Operand],
      operand4: Option[Operand],
      tryCancelResponse2: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.ClusterLaunchControlQueryCancel,
        (predicate.toVec ++ tryCancelResponse.toVec ++ zdim.toVec ++ operand4.toVec ++ tryCancelResponse2.toVec)*
      )
  case ClusterLaunchControlTryCancel(
      address: Operand,
      barrierAddr: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.ClusterLaunchControlTryCancel, address, barrierAddr)
  case Clz(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Clz, dest, srcA)
  case Cnot(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Cnot, dest, srcA)
  case CopySign(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CopySign, dest, srcA, srcB)
  case Cos(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Cos, dest, srcA)
  case CpAsync(
      destAddr: Operand,
      srcAddr: Operand,
      copySize: Operand,
      srcSizeOrIgnore: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsync,
        (Vec(destAddr, srcAddr, copySize) ++ srcSizeOrIgnore.toVec ++ cachePolicy.toVec)*
      )
  case CpAsyncBulkMBarrier(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      ignoredBytesLeft: Option[Operand],
      ignoredBytesRight: Option[Operand],
      ctaMask: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsyncBulkMBarrier,
        (Vec(destAddr, srcAddr, size) ++ ignoredBytesLeft.toVec ++ ignoredBytesRight.toVec ++ Vec(
          barrierAddr
        ) ++ ctaMask.toVec ++ cachePolicy.toVec)*
      )
  case CpAsyncBulkGroup(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      cachePolicy: Option[Operand],
      byteMask: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsyncBulkGroup,
        (Vec(destAddr, srcAddr, size) ++ cachePolicy.toVec ++ byteMask.toVec)*
      )
  case CpAsyncBulkCommitGroup(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncBulkCommitGroup)
  case CpAsyncBulkPrefetch(
      srcAddr: Operand,
      size: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.CpAsyncBulkPrefetch, (Vec(srcAddr, size) ++ cachePolicy.toVec)*)
  case CpAsyncBulkPrefetchTensor(
      tensorMapAndCoordinates: Operand,
      im2colInfo: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsyncBulkPrefetchTensor,
        (Vec(tensorMapAndCoordinates) ++ im2colInfo.toVec ++ cachePolicy.toVec)*
      )
  case CpAsyncBulkTensorMBarrier(
      destAddr: Operand,
      tensorMapAndCoordinates: Operand,
      barrierAddr: Operand,
      im2colInfo: Option[Operand],
      ctaMask: Option[Operand],
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsyncBulkTensorMBarrier,
        (Vec(destAddr, tensorMapAndCoordinates, barrierAddr) ++ im2colInfo.toVec ++ ctaMask.toVec ++ cachePolicy.toVec)*
      )
  case CpAsyncBulkTensorGroup(
      tensorMapAndCoordinates: Operand,
      srcAddr: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CpAsyncBulkTensorGroup,
        (Vec(tensorMapAndCoordinates, srcAddr) ++ cachePolicy.toVec)*
      )
  case CpAsyncBulkWaitGroup(pendingGroupCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncBulkWaitGroup, pendingGroupCount)
  case CpAsyncCommitGroup(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncCommitGroup)
  case CpAsyncMbarrierArrive(address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncMbarrierArrive, address)
  case CpAsyncWaitAll(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncWaitAll)
  case CpAsyncWaitGroup(pendingGroupCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.CpAsyncWaitGroup, pendingGroupCount)
  case CpReduceAsyncBulk(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.CpReduceAsyncBulk, (Vec(destAddr, srcAddr, size) ++ cachePolicy.toVec)*)
  case CpReduceAsyncBulkTensor(
      address: Operand,
      srcAddr: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.CpReduceAsyncBulkTensor, (Vec(address, srcAddr) ++ cachePolicy.toVec)*)
  case CreatePolicy(
      cachePolicy: Operand,
      accessProperty: Option[Operand],
      primarySize: Option[Operand],
      totalSize: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.CreatePolicy,
        (Vec(cachePolicy) ++ accessProperty.toVec ++ primarySize.toVec ++ totalSize.toVec)*
      )
  case Cvt(
      dest: Operand,
      src: Option[Operand],
      secondarySrc: Option[Operand],
      roundingBits: Option[Operand],
      f: Option[Operand],
      rbits: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Cvt,
        (Vec(dest) ++ src.toVec ++ secondarySrc.toVec ++ roundingBits.toVec ++ f.toVec ++ rbits.toVec)*
      )
  case CvtPack(
      dest: Operand,
      src: Operand,
      secondarySrc: Operand,
      roundingBits: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.CvtPack, (Vec(dest, src, secondarySrc) ++ roundingBits.toVec)*)
  case Cvta(dest: Operand, src: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Cvta, dest, src)
  case Discard(address: Operand, size: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Discard, address, size)
  case Div(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Div, dest, srcA, srcB)
  case Dp2a(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Dp2a, dest, srcA, srcB, srcC)
  case Dp4a(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Dp4a, dest, srcA, srcB, srcC)
  case ElectSync(dest: Operand, memberMask: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.ElectSync, dest, memberMask)
  case Ex2(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Ex2, dest, srcA)
  case Exit(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.Exit)
  case FabricSubmit(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.FabricSubmit)
  case FabricTryGet(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.FabricTryGet, destAddr, srcAddr, size, barrierAddr)
  case FabricTryPullred(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      memberMask: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.FabricTryPullred, destAddr, srcAddr, size, barrierAddr, memberMask)
  case FabricTryPut(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      bytemask: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.FabricTryPut,
        (Vec(destAddr, srcAddr, size, barrierAddr) ++ bytemask.toVec)*
      )
  case FabricTryRed(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      barrierAddr: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.FabricTryRed, destAddr, srcAddr, size, barrierAddr)
  case FabricWait(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.FabricWait)
  case Fence(address: Option[Operand], size: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Fence, (address.toVec ++ size.toVec)*)
  case Fma(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Fma, dest, srcA, srcB, srcC)
  case Fns(dest: Operand, mask: Operand, base: Operand, offset: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Fns, dest, mask, base, offset)
  case Fp8(floating: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Fp8, floating)
  case GetCtaRank(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.GetCtaRank, dest, srcA)
  case GridDependencyControl(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.GridDependencyControl)
  case IsSpacePredicate(predicate: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.IsSpacePredicate, predicate, srcA)
  case IsTypePredicate(predicate: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.IsTypePredicate, predicate, srcA)
  case Ld(
      dest: Operand,
      address: Operand,
      cachePolicy: Option[Operand],
      cachePolicy2: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Ld, (Vec(dest, address) ++ cachePolicy.toVec ++ cachePolicy2.toVec)*)
  case LdGlobalNc(
      dest: Operand,
      address: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.LdGlobalNc, (Vec(dest, address) ++ cachePolicy.toVec)*)
  case Ldmatrix(r: Operand, address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Ldmatrix, r, address)
  case Ldu(dest: Operand, address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Ldu, dest, address)
  case Lg2(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Lg2, dest, srcA)
  case Lop3(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      immLut: Operand,
      predicateQ: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Lop3, (Vec(dest, srcA, srcB, srcC, immLut) ++ predicateQ.toVec)*)
  case Mad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mad, dest, srcA, srcB, srcC)
  case MadCc(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MadCc, dest, srcA, srcB, srcC)
  case Mad24(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mad24, dest, srcA, srcB, srcC)
  case Madc(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Madc, dest, srcA, srcB, srcC)
  case Mapa(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mapa, dest, srcA, srcB)
  case MatchAllSync(dest: Operand, srcA: Operand, memberMask: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MatchAllSync, dest, srcA, memberMask)
  case MatchAnySync(dest: Operand, srcA: Operand, memberMask: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MatchAnySync, dest, srcA, memberMask)
  case Max(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Max, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case MbarrierArrive(
      state: Operand,
      barrierAddr: Operand,
      countOrTransactionCount: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.MbarrierArrive,
        (Vec(state, barrierAddr) ++ countOrTransactionCount.toVec)*
      )
  case MbarrierArriveDrop(
      state: Operand,
      barrierAddr: Operand,
      countOrTransactionCount: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.MbarrierArriveDrop,
        (Vec(state, barrierAddr) ++ countOrTransactionCount.toVec)*
      )
  case MbarrierCheckLayout(predicate: Operand, barrierAddr: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MbarrierCheckLayout, predicate, barrierAddr)
  case MbarrierCompleteTx(
      barrierAddr: Operand,
      transactionCount: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.MbarrierCompleteTx, barrierAddr, transactionCount)
  case MbarrierExpectTx(barrierAddr: Operand, transactionCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MbarrierExpectTx, barrierAddr, transactionCount)
  case MbarrierInit(barrierAddr: Operand, count: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MbarrierInit, barrierAddr, count)
  case MbarrierInval(barrierAddr: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MbarrierInval, barrierAddr)
  case MbarrierPendingCount(dest: Operand, state: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MbarrierPendingCount, dest, state)
  case MbarrierTestWait(
      completion: Operand,
      reportValue: Option[Operand],
      barrierAddr: Operand,
      stateOrPhase: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.MbarrierTestWait,
        (Vec(completion) ++ reportValue.toVec ++ Vec(barrierAddr, stateOrPhase))*
      )
  case MbarrierTryWait(
      completion: Operand,
      reportValue: Option[Operand],
      barrierAddr: Operand,
      stateOrPhase: Operand,
      timeHint: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.MbarrierTryWait,
        (Vec(completion) ++ reportValue.toVec ++ Vec(barrierAddr, stateOrPhase) ++ timeHint.toVec)*
      )
  case Membar(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.Membar)
  case Min(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Min, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Mma(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      scaleAData: Option[Operand],
      byteIdA: Option[Operand],
      threadIdA: Option[Operand],
      scaleBData: Option[Operand],
      byteIdB: Option[Operand],
      threadIdB: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Mma,
        (Vec(
          dest,
          srcA,
          srcB,
          srcC
        ) ++ scaleAData.toVec ++ byteIdA.toVec ++ threadIdA.toVec ++ scaleBData.toVec ++ byteIdB.toVec ++ threadIdB.toVec)*
      )
  case MmaSpOrderedMetadata(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      e: Operand,
      f: Operand,
      scaleAData: Option[Operand],
      byteIdA: Option[Operand],
      threadIdA: Option[Operand],
      scaleBData: Option[Operand],
      byteIdB: Option[Operand],
      threadIdB: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.MmaSpOrderedMetadata,
        (Vec(
          dest,
          srcA,
          srcB,
          srcC,
          e,
          f
        ) ++ scaleAData.toVec ++ byteIdA.toVec ++ threadIdA.toVec ++ scaleBData.toVec ++ byteIdB.toVec ++ threadIdB.toVec)*
      )
  case Mov(dest: Operand, src: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mov, dest, src)
  case Movmatrix(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Movmatrix, dest, srcA)
  case Mul(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mul, dest, srcA, srcB)
  case Mul24(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Mul24, dest, srcA, srcB)
  case MultimemCpAsyncBulk(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      byteMask: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.MultimemCpAsyncBulk, (Vec(destAddr, srcAddr, size) ++ byteMask.toVec)*)
  case MultimemCpReduceAsyncBulk(
      destAddr: Operand,
      srcAddr: Operand,
      size: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.MultimemCpReduceAsyncBulk, destAddr, srcAddr, size)
  case MultimemLdReduce(dest: Operand, address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MultimemLdReduce, dest, address)
  case MultimemRed(address: Operand, value: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MultimemRed, address, value)
  case MultimemRedAsync(address: Operand, value: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MultimemRedAsync, address, value)
  case MultimemSt(address: Operand, value: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MultimemSt, address, value)
  case MultimemStAsync(address: Operand, value: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.MultimemStAsync, address, value)
  case NanoSleep(nanoseconds: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.NanoSleep, nanoseconds)
  case Neg(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Neg, dest, srcA)
  case Not(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Not, dest, srcA)
  case Or(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Or, dest, srcA, srcB)
  case PmEvent(event: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.PmEvent, event)
  case Popc(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Popc, dest, srcA)
  case Prefetch(address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Prefetch, address)
  case Prefetchu(address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Prefetchu, address)
  case Prmt(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Prmt, dest, srcA, srcB, srcC)
  case Rcp(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Rcp, dest, srcA)
  case RcpApproxFtzF64(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.RcpApproxFtzF64, dest, srcA)
  case Red(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Red, (Vec(address, value) ++ cachePolicy.toVec)*)
  case RedAsync(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.RedAsync, (Vec(address, value) ++ cachePolicy.toVec)*)
  case ReduxSync(dest: Operand, src: Operand, memberMask: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.ReduxSync, dest, src, memberMask)
  case Rem(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Rem, dest, srcA, srcB)
  case Ret(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.Ret)
  case Rsqrt(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Rsqrt, dest, srcA)
  case RsqrtApproxFtzF64(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.RsqrtApproxFtzF64, dest, srcA)
  case Sad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sad, dest, srcA, srcB, srcC)
  case Selp(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Selp, dest, srcA, srcB, srcC)
  case Set(
      dest: Operand,
      left: Operand,
      right: Operand,
      booleanSrc: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Set, (Vec(dest, left, right) ++ booleanSrc.toVec)*)
  case SetMaxRegisters(immRegCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.SetMaxRegisters, immRegCount)
  case Setp(
      dest: Operand,
      left: Operand,
      right: Operand,
      booleanSrc: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Setp, (Vec(dest, left, right) ++ booleanSrc.toVec)*)
  case Shf(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Shf, dest, srcA, srcB, srcC)
  case Shfl(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Shfl, dest, srcA, srcB, srcC)
  case ShflSync(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      memberMask: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.ShflSync, dest, srcA, srcB, srcC, memberMask)
  case Shl(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Shl, dest, srcA, srcB)
  case Shr(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Shr, dest, srcA, srcB)
  case Sin(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sin, dest, srcA)
  case Slct(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Slct, dest, srcA, srcB, srcC)
  case Sqrt(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sqrt, dest, srcA)
  case St(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.St, (Vec(address, value) ++ cachePolicy.toVec)*)
  case StAsync(
      address: Operand,
      value: Operand,
      cachePolicy: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.StAsync, (Vec(address, value) ++ cachePolicy.toVec)*)
  case StBulk(address: Operand, value: Operand, cachePolicy: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.StBulk, address, value, cachePolicy)
  case StackRestore(srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.StackRestore, srcA)
  case StackSave(dest: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.StackSave, dest)
  case Stmatrix(address: Operand, r: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Stmatrix, address, r)
  case Sub(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sub, dest, srcA, srcB)
  case SubCc(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.SubCc, dest, srcA, srcB)
  case Subc(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Subc, dest, srcA, srcB)
  case Suld(dest: Operand, address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Suld, dest, address)
  case Suq(dest: Operand, address: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Suq, dest, address)
  case Sured(address: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sured, address, srcC)
  case Sust(address: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Sust, address, srcC)
  case Szext(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Szext, dest, srcA, srcB)
  case Tanh(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tanh, dest, srcA)
  case Tcgen05Alloc(destAddr: Operand, columnCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05Alloc, destAddr, columnCount)
  case Tcgen05Commit(barrierAddr: Operand, ctaMask: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05Commit, (Vec(barrierAddr) ++ ctaMask.toVec)*)
  case Tcgen05Cp(destAddr: Operand, srcDesc: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05Cp, destAddr, srcDesc)
  case Tcgen05Dealloc(tensorMemoryAddr: Operand, columnCount: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05Dealloc, tensorMemoryAddr, columnCount)
  case Tcgen05FenceAfterThreadSync(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05FenceAfterThreadSync)
  case Tcgen05FenceBeforeThreadSync(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05FenceBeforeThreadSync)
  case Tcgen05Ld(
      destRegs: Operand,
      reductionValue: Operand,
      address: Option[Operand],
      halfSplitOffset: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tcgen05Ld,
        (Vec(destRegs, reductionValue) ++ address.toVec ++ halfSplitOffset.toVec)*
      )
  case Tcgen05Mma(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      instDesc: Operand,
      scaleAAddr: Option[Operand],
      scaleBAddr: Option[Operand],
      disabledOutputLanes: Option[Operand],
      enableInputD: Operand,
      scaleInputD: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tcgen05Mma,
        (Vec(
          destAddr,
          srcA,
          srcBDesc,
          instDesc
        ) ++ scaleAAddr.toVec ++ scaleBAddr.toVec ++ disabledOutputLanes.toVec ++ Vec(
          enableInputD
        ) ++ scaleInputD.toVec)*
      )
  case Tcgen05MmaSp(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      sparseMetadataAddr: Operand,
      instDesc: Operand,
      scaleAAddr: Option[Operand],
      scaleBAddr: Option[Operand],
      disabledOutputLanes: Option[Operand],
      enableInputD: Operand,
      scaleInputD: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tcgen05MmaSp,
        (Vec(
          destAddr,
          srcA,
          srcBDesc,
          sparseMetadataAddr,
          instDesc
        ) ++ scaleAAddr.toVec ++ scaleBAddr.toVec ++ disabledOutputLanes.toVec ++ Vec(
          enableInputD
        ) ++ scaleInputD.toVec)*
      )
  case Tcgen05MmaWs(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      instDesc: Operand,
      enableInputD: Operand,
      zeroColumnMaskDesc: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tcgen05MmaWs,
        (Vec(destAddr, srcA, srcBDesc, instDesc, enableInputD) ++ zeroColumnMaskDesc.toVec)*
      )
  case Tcgen05MmaWsSp(
      destAddr: Operand,
      srcA: Operand,
      srcBDesc: Operand,
      sparseMetadataAddr: Operand,
      instDesc: Operand,
      enableInputD: Operand,
      zeroColumnMaskDesc: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tcgen05MmaWsSp,
        (Vec(destAddr, srcA, srcBDesc, sparseMetadataAddr, instDesc, enableInputD) ++ zeroColumnMaskDesc.toVec)*
      )
  case Tcgen05RelinquishAllocPermit(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05RelinquishAllocPermit)
  case Tcgen05Shift(tensorMemoryAddr: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05Shift, tensorMemoryAddr)
  case Tcgen05St(
      address: Operand,
      halfSplitOffset: Operand,
      srcRegs: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Tcgen05St, (Vec(address, halfSplitOffset) ++ srcRegs.toVec)*)
  case Tcgen05WaitLd(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05WaitLd)
  case Tcgen05WaitSt(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Tcgen05WaitSt)
  case TensorMapCopyFenceProxy(
      dest: Operand,
      srcOrOrdinal: Operand,
      valueOrSize: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.TensorMapCopyFenceProxy, dest, srcOrOrdinal, valueOrSize)
  case TensorMapReplace(
      dest: Operand,
      srcOrOrdinal: Operand,
      valueOrSize: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.TensorMapReplace, (Vec(dest, srcOrOrdinal) ++ valueOrSize.toVec)*)
  case TestPredicate(predicate: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.TestPredicate, predicate, srcA)
  case Tex(
      dest: Operand,
      textureAndCoordinates: Operand,
      lodOffsetOrDerivativeX: Option[Operand],
      depthCompareOrDerivativeY: Option[Operand],
      offset: Option[Operand],
      depthCompare: Option[Operand],
      f: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tex,
        (Vec(
          dest,
          textureAndCoordinates
        ) ++ lodOffsetOrDerivativeX.toVec ++ depthCompareOrDerivativeY.toVec ++ offset.toVec ++ depthCompare.toVec ++ f.toVec)*
      )
  case Tld4(
      dest: Operand,
      textureAndCoordinates: Operand,
      offset: Option[Operand],
      depthCompare: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tld4,
        (Vec(dest, textureAndCoordinates) ++ offset.toVec ++ depthCompare.toVec)*
      )
  case Trap(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.Trap)
  case Txq(dest: Operand, texture: Operand, lod: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Txq, (Vec(dest, texture) ++ lod.toVec)*)
  case Vabsdiff(
      dest: Operand,
      srcA: Operand,
      operand3: Option[Operand],
      srcB: Option[Operand],
      operand5: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vabsdiff,
        (Vec(dest, srcA) ++ operand3.toVec ++ srcB.toVec ++ operand5.toVec ++ srcC.toVec)*
      )
  case Vabsdiff2(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vabsdiff2,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vabsdiff4(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vabsdiff4,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vadd(
      dest: Operand,
      srcA: Operand,
      operand3: Option[Operand],
      srcB: Option[Operand],
      operand5: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vadd,
        (Vec(dest, srcA) ++ operand3.toVec ++ srcB.toVec ++ operand5.toVec ++ srcC.toVec)*
      )
  case Vadd2(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vadd2,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vadd4(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vadd4,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vavrg2(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vavrg2,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vavrg4(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vavrg4,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vmad(
      dest: Operand,
      srcA: Option[Operand],
      operand3: Option[Operand],
      srcB: Option[Operand],
      operand5: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vmad,
        (Vec(dest) ++ srcA.toVec ++ operand3.toVec ++ srcB.toVec ++ operand5.toVec ++ srcC.toVec)*
      )
  case Vmax(
      dest: Operand,
      srcA: Operand,
      operand3: Option[Operand],
      srcB: Option[Operand],
      operand5: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vmax,
        (Vec(dest, srcA) ++ operand3.toVec ++ srcB.toVec ++ operand5.toVec ++ srcC.toVec)*
      )
  case Vmax2(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vmax2,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vmax4(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vmax4,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vmin(
      dest: Operand,
      srcA: Operand,
      operand3: Option[Operand],
      srcB: Option[Operand],
      operand5: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vmin,
        (Vec(dest, srcA) ++ operand3.toVec ++ srcB.toVec ++ operand5.toVec ++ srcC.toVec)*
      )
  case Vmin2(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vmin2,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vmin4(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vmin4,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vote(dest: Operand, operand2: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vote, (Vec(dest) ++ operand2.toVec)*)
  case VoteSync(
      dest: Operand,
      operand2: Option[Operand],
      memberMask: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.VoteSync, (Vec(dest) ++ operand2.toVec ++ memberMask.toVec)*)
  case Vset(
      dest: Operand,
      srcA: Operand,
      operand3: Option[Operand],
      srcB: Option[Operand],
      operand5: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vset,
        (Vec(dest, srcA) ++ operand3.toVec ++ srcB.toVec ++ operand5.toVec ++ srcC.toVec)*
      )
  case Vset2(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vset2,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vset4(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vset4,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vshl(
      dest: Operand,
      srcA: Operand,
      operand3: Option[Operand],
      srcB: Option[Operand],
      operand5: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vshl,
        (Vec(dest, srcA) ++ operand3.toVec ++ srcB.toVec ++ operand5.toVec ++ srcC.toVec)*
      )
  case Vshr(
      dest: Operand,
      srcA: Operand,
      operand3: Option[Operand],
      srcB: Option[Operand],
      operand5: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vshr,
        (Vec(dest, srcA) ++ operand3.toVec ++ srcB.toVec ++ operand5.toVec ++ srcC.toVec)*
      )
  case Vsub(
      dest: Operand,
      srcA: Operand,
      operand3: Option[Operand],
      srcB: Option[Operand],
      operand5: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vsub,
        (Vec(dest, srcA) ++ operand3.toVec ++ srcB.toVec ++ operand5.toVec ++ srcC.toVec)*
      )
  case Vsub2(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vsub2,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case Vsub4(
      dest: Operand,
      operand2: Option[Operand],
      srcA: Operand,
      operand4: Option[Operand],
      srcB: Option[Operand],
      operand6: Option[Operand],
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Vsub4,
        (Vec(dest) ++ operand2.toVec ++ Vec(srcA) ++ operand4.toVec ++ srcB.toVec ++ operand6.toVec ++ srcC.toVec)*
      )
  case WgmmaCommitGroup(mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.WgmmaCommitGroup)
  case WgmmaFence(mods: Vec[Modifier], pred: Option[Predication]) extends Instruction(Instruction.Definition.WgmmaFence)
  case WgmmaMmaAsync(
      dest: Operand,
      aDesc: Operand,
      bDesc: Operand,
      scaleD: Operand,
      immScaleA: Option[Operand],
      immScaleB: Option[Operand],
      immTransA: Option[Operand],
      immTransB: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.WgmmaMmaAsync,
        (Vec(dest, aDesc, bDesc, scaleD) ++ immScaleA.toVec ++ immScaleB.toVec ++ immTransA.toVec ++ immTransB.toVec)*
      )
  case WgmmaMmaAsyncSp(
      dest: Operand,
      aDesc: Operand,
      bDesc: Operand,
      spMeta: Operand,
      spSel: Operand,
      scaleD: Operand,
      immScaleA: Option[Operand],
      immScaleB: Option[Operand],
      immTransA: Option[Operand],
      immTransB: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.WgmmaMmaAsyncSp,
        (Vec(
          dest,
          aDesc,
          bDesc,
          spMeta,
          spSel,
          scaleD
        ) ++ immScaleA.toVec ++ immScaleB.toVec ++ immTransA.toVec ++ immTransB.toVec)*
      )
  case WgmmaWaitGroup(n: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.WgmmaWaitGroup, n)
  case WmmaLoad(r: Operand, address: Operand, stride: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.WmmaLoad, (Vec(r, address) ++ stride.toVec)*)
  case WmmaMma(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.WmmaMma, dest, srcA, srcB, srcC)
  case WmmaStore(address: Operand, r: Operand, stride: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.WmmaStore, (Vec(address, r) ++ stride.toVec)*)
  case Xor(dest: Operand, srcA: Operand, srcB: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Xor, dest, srcA, srcB)

  val operands: Vec[Operand] = operandValues.toVec

  final def opcode: NonEmpty[String] = definition.opcode

  final def fixedModifiers: Vec[Modifier] = definition.fixedModifiers

  final def decoder: Instruction.Decoder = definition.decoder

  final def meta: Metadata = definition.meta

  def mods: Vec[Modifier]

  def pred: Option[Predication]

  final def qualifiedOpcode: NonEmpty[String] =
    if (mods.isEmpty) opcode else s"$opcode.${mods.map(_.name).mkString(".")}"
  lazy val signature: InstructionSignature =
    InstructionSignature(definition.mnemonic, definition.fixedModifiers ++ mods)

}
