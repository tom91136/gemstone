// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions4 {
  object Hmmafp16PipeRegRegRegReg extends Definition(
        "Hmmafp16PipeRegRegRegReg",
        "HMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "hmma_848__444_f16_f16",
          "hmma_848__444_f32",
          "hmma_848__884_f16_f16",
          "hmma_848__884_f32",
          "hmma_848_old_row_col__444_f16_f16",
          "hmma_848_old_row_col__444_f32",
          "hmma_848_old_row_col__884_f16_f16",
          "hmma_848_old_row_col__884_f32",
          "hmma__444_f16_f16",
          "hmma__444_f32",
          "hmma__884_f16_f16",
          "hmma__884_f32",
          "hmma_old_row_col__444_f16_f16",
          "hmma_old_row_col__444_f32",
          "hmma_old_row_col__884_f16_f16",
          "hmma_old_row_col__884_f32",
          "hmma_x8_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hmmafp16PipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hmmafp16PipeRegRegRegRegPred extends Definition(
        "Hmmafp16PipeRegRegRegRegPred",
        "HMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("hmma_x8_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Hmmafp16PipeRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Hmmafp16PipeRegRegRegRegPredRegImm extends Definition(
        "Hmmafp16PipeRegRegRegRegPredRegImm",
        "HMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hmma_sparse_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) =>
          Some(Instruction.Hmmafp16PipeRegRegRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object Hmmafp16PipeRegRegRegRegRegImm extends Definition(
        "Hmmafp16PipeRegRegRegRegRegImm",
        "HMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hmma_sparse_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.Hmmafp16PipeRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegPredPredRegAddressImmPred extends Definition(
        "Hmnmx2RegPredPredRegAddressImmPred",
        "HMNMX2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2_pred__RCxR", "hmnmx2_pred_fixed__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.Hmnmx2RegPredPredRegAddressImmPred(dst, pu, pv, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegPredPredRegConstPred extends Definition(
        "Hmnmx2RegPredPredRegConstPred",
        "HMNMX2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2_pred__RCR", "hmnmx2_pred_fixed__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegPredPredRegConstPred(dst, pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegPredPredRegImmImmPred extends Definition(
        "Hmnmx2RegPredPredRegImmImmPred",
        "HMNMX2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2_pred__RIR", "hmnmx2_pred_fixed__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegPredPredRegImmImmPred(dst, pu, pv, srcA, sb, sb1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegPredPredRegRegPred extends Definition(
        "Hmnmx2RegPredPredRegRegPred",
        "HMNMX2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2_pred__RRR", "hmnmx2_pred__RUR", "hmnmx2_pred_fixed__RRR", "hmnmx2_pred_fixed__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegPredPredRegRegPred(dst, pu, pv, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegRegAddressImmPred extends Definition(
        "Hmnmx2RegRegAddressImmPred",
        "HMNMX2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2__RCxR", "hmnmx2_fixed__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegRegConstPred extends Definition(
        "Hmnmx2RegRegConstPred",
        "HMNMX2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2__RCR", "hmnmx2_fixed__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegRegImmImmPred extends Definition(
        "Hmnmx2RegRegImmImmPred",
        "HMNMX2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2__RIR", "hmnmx2_fixed__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegRegImmImmPred(dst, srcA, sb, sb1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegRegRegPred extends Definition(
        "Hmnmx2RegRegRegPred",
        "HMNMX2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2__RRR", "hmnmx2__RUR", "hmnmx2_fixed__RRR", "hmnmx2_fixed__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2fp16PipeRegPredPredRegAddressImmPred extends Definition(
        "Hmnmx2fp16PipeRegPredPredRegAddressImmPred",
        "HMNMX2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2_pred__RCxR", "hmnmx2_pred_fixed__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2fp16PipeRegPredPredRegAddressImmPred(
            dst,
            pu,
            pv,
            srcA,
            srcB,
            sb_offset,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Hmnmx2fp16PipeRegPredPredRegConstPred extends Definition(
        "Hmnmx2fp16PipeRegPredPredRegConstPred",
        "HMNMX2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2_pred__RCR", "hmnmx2_pred_fixed__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2fp16PipeRegPredPredRegConstPred(dst, pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2fp16PipeRegPredPredRegImmImmPred extends Definition(
        "Hmnmx2fp16PipeRegPredPredRegImmImmPred",
        "HMNMX2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2_pred__RIR", "hmnmx2_pred_fixed__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.Hmnmx2fp16PipeRegPredPredRegImmImmPred(dst, pu, pv, srcA, sb, sb1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2fp16PipeRegPredPredRegRegPred extends Definition(
        "Hmnmx2fp16PipeRegPredPredRegRegPred",
        "HMNMX2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2_pred__RRR", "hmnmx2_pred__RUR", "hmnmx2_pred_fixed__RRR", "hmnmx2_pred_fixed__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2fp16PipeRegPredPredRegRegPred(dst, pu, pv, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2fp16PipeRegRegAddressImmPred extends Definition(
        "Hmnmx2fp16PipeRegRegAddressImmPred",
        "HMNMX2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2__RCxR", "hmnmx2_fixed__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2fp16PipeRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2fp16PipeRegRegConstPred extends Definition(
        "Hmnmx2fp16PipeRegRegConstPred",
        "HMNMX2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2__RCR", "hmnmx2_fixed__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2fp16PipeRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2fp16PipeRegRegImmImmPred extends Definition(
        "Hmnmx2fp16PipeRegRegImmImmPred",
        "HMNMX2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2__RIR", "hmnmx2_fixed__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2fp16PipeRegRegImmImmPred(dst, srcA, sb, sb1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2fp16PipeRegRegRegPred extends Definition(
        "Hmnmx2fp16PipeRegRegRegPred",
        "HMNMX2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hmnmx2__RRR", "hmnmx2__RUR", "hmnmx2_fixed__RRR", "hmnmx2_fixed__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2fp16PipeRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2NoOperands extends Definition("Hmul2NoOperands", "HMUL2", Vec(), Vec("hmul2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hmul2NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hmul2Const
      extends Definition("Hmul2Const", "HMUL2", Vec(Slot("sb", OperandKind.ConstantBank)), Vec("hmul2__v0")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hmul2Const(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2ImmImm extends Definition(
        "Hmul2ImmImm",
        "HMUL2",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hmul2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hmul2ImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2RegRegAddressImm extends Definition(
        "Hmul2RegRegAddressImm",
        "HMUL2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("hmul2__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.Hmul2RegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2RegRegConst extends Definition(
        "Hmul2RegRegConst",
        "HMUL2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("hmul2__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.Hmul2RegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2RegRegImmImm extends Definition(
        "Hmul2RegRegImmImm",
        "HMUL2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate)
        ),
        Vec("hmul2__RI", "hmul2_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1)
            ) => Some(Instruction.Hmul2RegRegImmImm(dst, srcA, sb, sb1, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2RegRegReg extends Definition(
        "Hmul2RegRegReg",
        "HMUL2",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("hmul2__RR", "hmul2__RU", "hmul2_fixed__RR", "hmul2_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Hmul2RegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hmul232iImmImm extends Definition(
        "Hmul232iImmImm",
        "HMUL2_32I",
        Vec(Slot("imm", OperandKind.Immediate), Slot("imm2", OperandKind.Immediate)),
        Vec("hmul2_32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm), InstructionOperand.Immediate(imm2)) =>
          Some(Instruction.Hmul232iImmImm(imm, imm2, modifiers, guard))
        case _ => None
      }
  }
  object Hmul232iRegRegImmImm extends Definition(
        "Hmul232iRegRegImmImm",
        "HMUL2_32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate)
        ),
        Vec("hmul2_32i_", "hmul2_32i_fixed_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1)
            ) => Some(Instruction.Hmul232iRegRegImmImm(dst, srcA, sb, sb1, modifiers, guard))
        case _ => None
      }
  }
  object Hmul232ifp16PipeRegRegImmImm extends Definition(
        "Hmul232ifp16PipeRegRegImmImm",
        "HMUL2_32Ifp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate)
        ),
        Vec("hmul2_32i_", "hmul2_32i_fixed_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1)
            ) => Some(Instruction.Hmul232ifp16PipeRegRegImmImm(dst, srcA, sb, sb1, modifiers, guard))
        case _ => None
      }
  }
  object Hmul232ifp16g0PipeImmImm extends Definition(
        "Hmul232ifp16g0PipeImmImm",
        "HMUL2_32Ifp16g0_pipe",
        Vec(Slot("imm", OperandKind.Immediate), Slot("imm2", OperandKind.Immediate)),
        Vec("hmul2_32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm), InstructionOperand.Immediate(imm2)) =>
          Some(Instruction.Hmul232ifp16g0PipeImmImm(imm, imm2, modifiers, guard))
        case _ => None
      }
  }
  object Hmul232ifp16g1PipeImmImm extends Definition(
        "Hmul232ifp16g1PipeImmImm",
        "HMUL2_32Ifp16g1_pipe",
        Vec(Slot("imm", OperandKind.Immediate), Slot("imm2", OperandKind.Immediate)),
        Vec("hmul2_32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm), InstructionOperand.Immediate(imm2)) =>
          Some(Instruction.Hmul232ifp16g1PipeImmImm(imm, imm2, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2fp16PipeRegRegAddressImm extends Definition(
        "Hmul2fp16PipeRegRegAddressImm",
        "HMUL2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("hmul2__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.Hmul2fp16PipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2fp16PipeRegRegConst extends Definition(
        "Hmul2fp16PipeRegRegConst",
        "HMUL2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("hmul2__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.Hmul2fp16PipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2fp16PipeRegRegImmImm extends Definition(
        "Hmul2fp16PipeRegRegImmImm",
        "HMUL2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate)
        ),
        Vec("hmul2__RI", "hmul2_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1)
            ) => Some(Instruction.Hmul2fp16PipeRegRegImmImm(dst, srcA, sb, sb1, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2fp16PipeRegRegReg extends Definition(
        "Hmul2fp16PipeRegRegReg",
        "HMUL2fp16_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("hmul2__RR", "hmul2__RU", "hmul2_fixed__RR", "hmul2_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Hmul2fp16PipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2fp16g0PipeNoOperands
      extends Definition("Hmul2fp16g0PipeNoOperands", "HMUL2fp16g0_pipe", Vec(), Vec("hmul2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hmul2fp16g0PipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hmul2fp16g0PipeConst extends Definition(
        "Hmul2fp16g0PipeConst",
        "HMUL2fp16g0_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("hmul2__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hmul2fp16g0PipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2fp16g0PipeImmImm extends Definition(
        "Hmul2fp16g0PipeImmImm",
        "HMUL2fp16g0_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hmul2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hmul2fp16g0PipeImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2fp16g1PipeNoOperands
      extends Definition("Hmul2fp16g1PipeNoOperands", "HMUL2fp16g1_pipe", Vec(), Vec("hmul2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hmul2fp16g1PipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hmul2fp16g1PipeConst extends Definition(
        "Hmul2fp16g1PipeConst",
        "HMUL2fp16g1_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("hmul2__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hmul2fp16g1PipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2fp16g1PipeImmImm extends Definition(
        "Hmul2fp16g1PipeImmImm",
        "HMUL2fp16g1_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hmul2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hmul2fp16g1PipeImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hset2NoOperands extends Definition("Hset2NoOperands", "HSET2", Vec(), Vec("hset2_bop__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hset2NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hset2Const
      extends Definition("Hset2Const", "HSET2", Vec(Slot("sb", OperandKind.ConstantBank)), Vec("hset2_bop__v0")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hset2Const(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hset2ConstPred extends Definition(
        "Hset2ConstPred",
        "HSET2",
        Vec(Slot("sb", OperandKind.ConstantBank), Slot("ps", OperandKind.Predicate)),
        Vec("hset2__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb), InstructionOperand.Predicate(ps)) =>
          Some(Instruction.Hset2ConstPred(sb, ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2ImmImm extends Definition(
        "Hset2ImmImm",
        "HSET2",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hset2_bop__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hset2ImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hset2ImmImmPred extends Definition(
        "Hset2ImmImmPred",
        "HSET2",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate), Slot("ps", OperandKind.Predicate)),
        Vec("hset2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb2),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.Hset2ImmImmPred(sb, sb2, ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2Pred extends Definition("Hset2Pred", "HSET2", Vec(Slot("ps", OperandKind.Predicate)), Vec("hset2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(ps)) => Some(Instruction.Hset2Pred(ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegAddressImm extends Definition(
        "Hset2RegRegAddressImm",
        "HSET2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("hset2_noBop__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Hset2RegRegAddressImm(dst, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegAddressImmPred extends Definition(
        "Hset2RegRegAddressImmPred",
        "HSET2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hset2__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2RegRegAddressImmPred(dst, srcA, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegConst extends Definition(
        "Hset2RegRegConst",
        "HSET2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("hset2_noBop__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Hset2RegRegConst(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegConstPred extends Definition(
        "Hset2RegRegConstPred",
        "HSET2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hset2__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2RegRegConstPred(dst, srcA, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegImmImm extends Definition(
        "Hset2RegRegImmImm",
        "HSET2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate)
        ),
        Vec("hset2_noBop__RI", "hset2_noBop_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1)
            ) => Some(Instruction.Hset2RegRegImmImm(dst, srcA, sc, sc1, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegImmImmPred extends Definition(
        "Hset2RegRegImmImmPred",
        "HSET2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hset2__RI", "hset2_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2RegRegImmImmPred(dst, srcA, sc, sc1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegReg extends Definition(
        "Hset2RegRegReg",
        "HSET2",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("hset2_noBop__RR", "hset2_noBop__RU", "hset2_noBop_fixed__RR", "hset2_noBop_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hset2RegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegRegPred extends Definition(
        "Hset2RegRegRegPred",
        "HSET2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hset2__RR", "hset2__RU", "hset2_fixed__RR", "hset2_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2RegRegRegPred(dst, srcA, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16PipeRegRegAddressImm extends Definition(
        "Hset2fp16PipeRegRegAddressImm",
        "HSET2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("hset2_noBop__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Hset2fp16PipeRegRegAddressImm(dst, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16PipeRegRegAddressImmPred extends Definition(
        "Hset2fp16PipeRegRegAddressImmPred",
        "HSET2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hset2__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2fp16PipeRegRegAddressImmPred(dst, srcA, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16PipeRegRegConst extends Definition(
        "Hset2fp16PipeRegRegConst",
        "HSET2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("hset2_noBop__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Hset2fp16PipeRegRegConst(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16PipeRegRegConstPred extends Definition(
        "Hset2fp16PipeRegRegConstPred",
        "HSET2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hset2__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2fp16PipeRegRegConstPred(dst, srcA, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16PipeRegRegImmImm extends Definition(
        "Hset2fp16PipeRegRegImmImm",
        "HSET2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate)
        ),
        Vec("hset2_noBop__RI", "hset2_noBop_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1)
            ) => Some(Instruction.Hset2fp16PipeRegRegImmImm(dst, srcA, sc, sc1, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16PipeRegRegImmImmPred extends Definition(
        "Hset2fp16PipeRegRegImmImmPred",
        "HSET2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hset2__RI", "hset2_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2fp16PipeRegRegImmImmPred(dst, srcA, sc, sc1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16PipeRegRegReg extends Definition(
        "Hset2fp16PipeRegRegReg",
        "HSET2fp16_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("hset2_noBop__RR", "hset2_noBop__RU", "hset2_noBop_fixed__RR", "hset2_noBop_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hset2fp16PipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16PipeRegRegRegPred extends Definition(
        "Hset2fp16PipeRegRegRegPred",
        "HSET2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hset2__RR", "hset2__RU", "hset2_fixed__RR", "hset2_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2fp16PipeRegRegRegPred(dst, srcA, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g0PipeNoOperands
      extends Definition("Hset2fp16g0PipeNoOperands", "HSET2fp16g0_pipe", Vec(), Vec("hset2_bop__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hset2fp16g0PipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g0PipeConst extends Definition(
        "Hset2fp16g0PipeConst",
        "HSET2fp16g0_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("hset2_bop__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hset2fp16g0PipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g0PipeConstPred extends Definition(
        "Hset2fp16g0PipeConstPred",
        "HSET2fp16g0_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank), Slot("ps", OperandKind.Predicate)),
        Vec("hset2__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb), InstructionOperand.Predicate(ps)) =>
          Some(Instruction.Hset2fp16g0PipeConstPred(sb, ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g0PipeImmImm extends Definition(
        "Hset2fp16g0PipeImmImm",
        "HSET2fp16g0_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hset2_bop__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hset2fp16g0PipeImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g0PipeImmImmPred extends Definition(
        "Hset2fp16g0PipeImmImmPred",
        "HSET2fp16g0_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate), Slot("ps", OperandKind.Predicate)),
        Vec("hset2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb2),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.Hset2fp16g0PipeImmImmPred(sb, sb2, ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g0PipePred extends Definition(
        "Hset2fp16g0PipePred",
        "HSET2fp16g0_pipe",
        Vec(Slot("ps", OperandKind.Predicate)),
        Vec("hset2__v2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(ps)) => Some(Instruction.Hset2fp16g0PipePred(ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g1PipeNoOperands
      extends Definition("Hset2fp16g1PipeNoOperands", "HSET2fp16g1_pipe", Vec(), Vec("hset2_bop__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hset2fp16g1PipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g1PipeConst extends Definition(
        "Hset2fp16g1PipeConst",
        "HSET2fp16g1_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("hset2_bop__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hset2fp16g1PipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g1PipeConstPred extends Definition(
        "Hset2fp16g1PipeConstPred",
        "HSET2fp16g1_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank), Slot("ps", OperandKind.Predicate)),
        Vec("hset2__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb), InstructionOperand.Predicate(ps)) =>
          Some(Instruction.Hset2fp16g1PipeConstPred(sb, ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g1PipeImmImm extends Definition(
        "Hset2fp16g1PipeImmImm",
        "HSET2fp16g1_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hset2_bop__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hset2fp16g1PipeImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g1PipeImmImmPred extends Definition(
        "Hset2fp16g1PipeImmImmPred",
        "HSET2fp16g1_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate), Slot("ps", OperandKind.Predicate)),
        Vec("hset2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb2),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.Hset2fp16g1PipeImmImmPred(sb, sb2, ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2fp16g1PipePred extends Definition(
        "Hset2fp16g1PipePred",
        "HSET2fp16g1_pipe",
        Vec(Slot("ps", OperandKind.Predicate)),
        Vec("hset2__v2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(ps)) => Some(Instruction.Hset2fp16g1PipePred(ps, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegAddressImm extends Definition(
        "Hsetp2PredPredRegAddressImm",
        "HSETP2",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("hsetp2_noBop__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Hsetp2PredPredRegAddressImm(pu, pv, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegAddressImmPred extends Definition(
        "Hsetp2PredPredRegAddressImmPred",
        "HSETP2",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hsetp2__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hsetp2PredPredRegAddressImmPred(pu, pv, srcA, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegConst extends Definition(
        "Hsetp2PredPredRegConst",
        "HSETP2",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_HSETP2", "hsetp2_noBop__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.Hsetp2PredPredRegConst(dstPredicate, nPd, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegConstPred extends Definition(
        "Hsetp2PredPredRegConstPred",
        "HSETP2",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_HSETP2", "hsetp2__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Hsetp2PredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegImmImm extends Definition(
        "Hsetp2PredPredRegImmImm",
        "HSETP2",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("uImmH0", OperandKind.Immediate)
        ),
        Vec("NoBop_Imm_HSETP2", "hsetp2_noBop__RI", "hsetp2_noBop_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(uImmH0)
            ) => Some(Instruction.Hsetp2PredPredRegImmImm(dstPredicate, nPd, srcA, uImm, uImmH0, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegImmImmPred extends Definition(
        "Hsetp2PredPredRegImmImmPred",
        "HSETP2",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("uImmH0", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_HSETP2", "hsetp2__RI", "hsetp2_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(uImmH0),
              InstructionOperand.Predicate(pa)
            ) =>
          Some(Instruction.Hsetp2PredPredRegImmImmPred(dstPredicate, nPd, srcA, uImm, uImmH0, pa, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegReg extends Definition(
        "Hsetp2PredPredRegReg",
        "HSETP2",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop_HSETP2", "hsetp2_noBop__RR", "hsetp2_noBop__RU", "hsetp2_noBop_fixed__RR", "hsetp2_noBop_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Hsetp2PredPredRegReg(dstPredicate, nPd, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegRegPred extends Definition(
        "Hsetp2PredPredRegRegPred",
        "HSETP2",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("HSETP2", "hsetp2__RR", "hsetp2__RU", "hsetp2_fixed__RR", "hsetp2_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Hsetp2PredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16PipePredPredRegAddressImm extends Definition(
        "Hsetp2fp16PipePredPredRegAddressImm",
        "HSETP2fp16_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("hsetp2_noBop__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Hsetp2fp16PipePredPredRegAddressImm(pu, pv, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16PipePredPredRegAddressImmPred extends Definition(
        "Hsetp2fp16PipePredPredRegAddressImmPred",
        "HSETP2fp16_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hsetp2__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.Hsetp2fp16PipePredPredRegAddressImmPred(pu, pv, srcA, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16PipePredPredRegConst extends Definition(
        "Hsetp2fp16PipePredPredRegConst",
        "HSETP2fp16_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("hsetp2_noBop__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Hsetp2fp16PipePredPredRegConst(pu, pv, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16PipePredPredRegConstPred extends Definition(
        "Hsetp2fp16PipePredPredRegConstPred",
        "HSETP2fp16_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hsetp2__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hsetp2fp16PipePredPredRegConstPred(pu, pv, srcA, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16PipePredPredRegImmImm extends Definition(
        "Hsetp2fp16PipePredPredRegImmImm",
        "HSETP2fp16_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate)
        ),
        Vec("hsetp2_noBop__RI", "hsetp2_noBop_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1)
            ) => Some(Instruction.Hsetp2fp16PipePredPredRegImmImm(pu, pv, srcA, sc, sc1, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16PipePredPredRegImmImmPred extends Definition(
        "Hsetp2fp16PipePredPredRegImmImmPred",
        "HSETP2fp16_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hsetp2__RI", "hsetp2_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hsetp2fp16PipePredPredRegImmImmPred(pu, pv, srcA, sc, sc1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16PipePredPredRegReg extends Definition(
        "Hsetp2fp16PipePredPredRegReg",
        "HSETP2fp16_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("hsetp2_noBop__RR", "hsetp2_noBop__RU", "hsetp2_noBop_fixed__RR", "hsetp2_noBop_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hsetp2fp16PipePredPredRegReg(pu, pv, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16PipePredPredRegRegPred extends Definition(
        "Hsetp2fp16PipePredPredRegRegPred",
        "HSETP2fp16_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hsetp2__RR", "hsetp2__RU", "hsetp2_fixed__RR", "hsetp2_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hsetp2fp16PipePredPredRegRegPred(pu, pv, srcA, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16g0PipePredPredRegConst extends Definition(
        "Hsetp2fp16g0PipePredPredRegConst",
        "HSETP2fp16g0_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.Hsetp2fp16g0PipePredPredRegConst(dstPredicate, nPd, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16g0PipePredPredRegConstPred extends Definition(
        "Hsetp2fp16g0PipePredPredRegConstPred",
        "HSETP2fp16g0_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Hsetp2fp16g0PipePredPredRegConstPred(
            dstPredicate,
            nPd,
            srcA,
            srcConst,
            pa,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Hsetp2fp16g0PipePredPredRegImmImm extends Definition(
        "Hsetp2fp16g0PipePredPredRegImmImm",
        "HSETP2fp16g0_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("uImmH0", OperandKind.Immediate)
        ),
        Vec("NoBop_Imm_HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(uImmH0)
            ) =>
          Some(Instruction.Hsetp2fp16g0PipePredPredRegImmImm(dstPredicate, nPd, srcA, uImm, uImmH0, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16g0PipePredPredRegImmImmPred extends Definition(
        "Hsetp2fp16g0PipePredPredRegImmImmPred",
        "HSETP2fp16g0_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("uImmH0", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(uImmH0),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Hsetp2fp16g0PipePredPredRegImmImmPred(
            dstPredicate,
            nPd,
            srcA,
            uImm,
            uImmH0,
            pa,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Hsetp2fp16g0PipePredPredRegReg extends Definition(
        "Hsetp2fp16g0PipePredPredRegReg",
        "HSETP2fp16g0_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop_HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Hsetp2fp16g0PipePredPredRegReg(dstPredicate, nPd, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16g0PipePredPredRegRegPred extends Definition(
        "Hsetp2fp16g0PipePredPredRegRegPred",
        "HSETP2fp16g0_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) =>
          Some(Instruction.Hsetp2fp16g0PipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16g1PipePredPredRegConst extends Definition(
        "Hsetp2fp16g1PipePredPredRegConst",
        "HSETP2fp16g1_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.Hsetp2fp16g1PipePredPredRegConst(dstPredicate, nPd, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16g1PipePredPredRegConstPred extends Definition(
        "Hsetp2fp16g1PipePredPredRegConstPred",
        "HSETP2fp16g1_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Hsetp2fp16g1PipePredPredRegConstPred(
            dstPredicate,
            nPd,
            srcA,
            srcConst,
            pa,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Hsetp2fp16g1PipePredPredRegImmImm extends Definition(
        "Hsetp2fp16g1PipePredPredRegImmImm",
        "HSETP2fp16g1_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("uImmH0", OperandKind.Immediate)
        ),
        Vec("NoBop_Imm_HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(uImmH0)
            ) =>
          Some(Instruction.Hsetp2fp16g1PipePredPredRegImmImm(dstPredicate, nPd, srcA, uImm, uImmH0, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16g1PipePredPredRegImmImmPred extends Definition(
        "Hsetp2fp16g1PipePredPredRegImmImmPred",
        "HSETP2fp16g1_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("uImmH0", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(uImmH0),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Hsetp2fp16g1PipePredPredRegImmImmPred(
            dstPredicate,
            nPd,
            srcA,
            uImm,
            uImmH0,
            pa,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Hsetp2fp16g1PipePredPredRegReg extends Definition(
        "Hsetp2fp16g1PipePredPredRegReg",
        "HSETP2fp16g1_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop_HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Hsetp2fp16g1PipePredPredRegReg(dstPredicate, nPd, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2fp16g1PipePredPredRegRegPred extends Definition(
        "Hsetp2fp16g1PipePredPredRegRegPred",
        "HSETP2fp16g1_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("HSETP2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) =>
          Some(Instruction.Hsetp2fp16g1PipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object I2fRegAddressImm extends Definition(
        "I2fRegAddressImm",
        "I2F",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec(
          "i2f_Rd64__CX_16b",
          "i2f_Rd64__CX_32b",
          "i2f_Rd64__CX_64b",
          "i2f_Rd64__CX_8b",
          "i2f__CX_16b",
          "i2f__CX_32b",
          "i2f__CX_64b",
          "i2f__CX_8b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.I2fRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object I2fRegConst extends Definition(
        "I2fRegConst",
        "I2F",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec(
          "Const I2F",
          "Const I2F16",
          "Const_I2F",
          "Const_I2F16",
          "Const_I2F64",
          "Const_I2F_F64",
          "i2f_Rd64__Cb_16b",
          "i2f_Rd64__Cb_32b",
          "i2f_Rd64__Cb_64b",
          "i2f_Rd64__Cb_8b",
          "i2f__Cb_16b",
          "i2f__Cb_32b",
          "i2f__Cb_64b",
          "i2f__Cb_8b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.I2fRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fRegImm extends Definition(
        "I2fRegImm",
        "I2F",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "Imm I2F",
          "Imm I2F16",
          "Imm_I2F",
          "Imm_I2F16",
          "Imm_I2F64",
          "Imm_I2F_F64",
          "i2f_Rd64__IS_32b",
          "i2f_Rd64__IS_64b",
          "i2f_Rd64__IU_32b",
          "i2f_Rd64__IU_64b",
          "i2f__IS_32b",
          "i2f__IS_64b",
          "i2f__IU_32b",
          "i2f__IU_64b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.I2fRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fRegReg extends Definition(
        "I2fRegReg",
        "I2F",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "I2F",
          "I2F16",
          "I2F64",
          "I2F_F64",
          "i2f_Rd64__Rb_16b",
          "i2f_Rd64__Rb_32b",
          "i2f_Rd64__Rb_64b",
          "i2f_Rd64__Rb_8b",
          "i2f_Rd64__UR_16b",
          "i2f_Rd64__UR_32b",
          "i2f_Rd64__UR_64b",
          "i2f_Rd64__UR_8b",
          "i2f__Rb_16b",
          "i2f__Rb_32b",
          "i2f__Rb_64b",
          "i2f__Rb_8b",
          "i2f__UR_16b",
          "i2f__UR_32b",
          "i2f__UR_64b",
          "i2f__UR_8b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2fRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2fpRegAddressImm extends Definition(
        "I2fpRegAddressImm",
        "I2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("i2fp__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.I2fpRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object I2fpRegConst extends Definition(
        "I2fpRegConst",
        "I2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("i2fp__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.I2fpRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fpRegImm extends Definition(
        "I2fpRegImm",
        "I2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("i2fp__RsIR", "i2fp__RuIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.I2fpRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fpRegReg extends Definition(
        "I2fpRegReg",
        "I2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("i2fp__RRR", "i2fp__RUR", "i2fp__Rbx2", "i2fp__URbx2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2fpRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2fpintPipeRegAddressImm extends Definition(
        "I2fpintPipeRegAddressImm",
        "I2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("i2fp__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.I2fpintPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object I2fpintPipeRegConst extends Definition(
        "I2fpintPipeRegConst",
        "I2FPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("i2fp__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.I2fpintPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fpintPipeRegImm extends Definition(
        "I2fpintPipeRegImm",
        "I2FPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("i2fp__RsIR", "i2fp__RuIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.I2fpintPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fpintPipeRegReg extends Definition(
        "I2fpintPipeRegReg",
        "I2FPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("i2fp__RRR", "i2fp__RUR", "i2fp__Rbx2", "i2fp__URbx2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2fpintPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2ffmaxPipeRegConst extends Definition(
        "I2ffmaxPipeRegConst",
        "I2FfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const I2F", "Const I2F16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.I2ffmaxPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object I2ffmaxPipeRegImm extends Definition(
        "I2ffmaxPipeRegImm",
        "I2FfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm I2F", "Imm I2F16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.I2ffmaxPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object I2ffmaxPipeRegReg extends Definition(
        "I2ffmaxPipeRegReg",
        "I2FfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("I2F", "I2F16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2ffmaxPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2fmioPipeRegAddressImm extends Definition(
        "I2fmioPipeRegAddressImm",
        "I2Fmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec(
          "i2f_Rd64__CX_16b",
          "i2f_Rd64__CX_32b",
          "i2f_Rd64__CX_64b",
          "i2f_Rd64__CX_8b",
          "i2f__CX_16b",
          "i2f__CX_32b",
          "i2f__CX_64b",
          "i2f__CX_8b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.I2fmioPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object I2fmioPipeRegConst extends Definition(
        "I2fmioPipeRegConst",
        "I2Fmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec(
          "Const_I2F",
          "Const_I2F16",
          "Const_I2F64",
          "Const_I2F_F64",
          "i2f_Rd64__Cb_16b",
          "i2f_Rd64__Cb_32b",
          "i2f_Rd64__Cb_64b",
          "i2f_Rd64__Cb_8b",
          "i2f__Cb_16b",
          "i2f__Cb_32b",
          "i2f__Cb_64b",
          "i2f__Cb_8b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.I2fmioPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fmioPipeRegImm extends Definition(
        "I2fmioPipeRegImm",
        "I2Fmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "Imm_I2F",
          "Imm_I2F16",
          "Imm_I2F64",
          "Imm_I2F_F64",
          "i2f_Rd64__IS_32b",
          "i2f_Rd64__IS_64b",
          "i2f_Rd64__IU_32b",
          "i2f_Rd64__IU_64b",
          "i2f__IS_32b",
          "i2f__IS_64b",
          "i2f__IU_32b",
          "i2f__IU_64b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.I2fmioPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fmioPipeRegReg extends Definition(
        "I2fmioPipeRegReg",
        "I2Fmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "I2F",
          "I2F16",
          "I2F64",
          "I2F_F64",
          "i2f_Rd64__Rb_16b",
          "i2f_Rd64__Rb_32b",
          "i2f_Rd64__Rb_64b",
          "i2f_Rd64__Rb_8b",
          "i2f_Rd64__UR_16b",
          "i2f_Rd64__UR_32b",
          "i2f_Rd64__UR_64b",
          "i2f_Rd64__UR_8b",
          "i2f__Rb_16b",
          "i2f__Rb_32b",
          "i2f__Rb_64b",
          "i2f__Rb_8b",
          "i2f__UR_16b",
          "i2f__UR_32b",
          "i2f__UR_64b",
          "i2f__UR_8b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2fmioPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2iRegAddressImm extends Definition(
        "I2iRegAddressImm",
        "I2I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("i2i__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.I2iRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object I2iRegConst extends Definition(
        "I2iRegConst",
        "I2I",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("Const I2I", "Const I2I16", "Const_I2I", "Const_I2I16", "i2i__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.I2iRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2iRegImm extends Definition(
        "I2iRegImm",
        "I2I",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm I2I", "Imm I2I16", "Imm_I2I", "Imm_I2I16", "i2i__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.I2iRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2iRegReg extends Definition(
        "I2iRegReg",
        "I2I",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("I2I", "I2I16", "i2i__RRR_RRR", "i2i__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2iRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2ipRegRegAddressImmReg extends Definition(
        "I2ipRegRegAddressImmReg",
        "I2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "i2ip_24__RCxR_RCxR",
          "i2ip_24_relu__RCxR_RCxR",
          "i2ip_28__RCxR_RCxR",
          "i2ip_28_relu__RCxR_RCxR",
          "i2ip__RCxR_RCxR",
          "i2ip_relu__RCxR_RCxR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2ipRegRegConstReg extends Definition(
        "I2ipRegRegConstReg",
        "I2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "i2ip_24__RCR_RCR",
          "i2ip_24_relu__RCR_RCR",
          "i2ip_28__RCR_RCR",
          "i2ip_28_relu__RCR_RCR",
          "i2ip__RCR_RCR",
          "i2ip_relu__RCR_RCR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2ipRegRegImmReg extends Definition(
        "I2ipRegRegImmReg",
        "I2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "i2ip_24__RsIR_RIR",
          "i2ip_24_relu__RsIR_RIR",
          "i2ip_28__RsIR_RIR",
          "i2ip_28_relu__RsIR_RIR",
          "i2ip__RsIR_RIR",
          "i2ip_relu__RsIR_RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2ipRegRegRegReg extends Definition(
        "I2ipRegRegRegReg",
        "I2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "i2ip_24__RRR_RRR",
          "i2ip_24__RUR_RUR",
          "i2ip_24_relu__RRR_RRR",
          "i2ip_24_relu__RUR_RUR",
          "i2ip_28__RRR_RRR",
          "i2ip_28__RUR_RUR",
          "i2ip_28_relu__RRR_RRR",
          "i2ip_28_relu__RUR_RUR",
          "i2ip__RRR_RRR",
          "i2ip__RUR_RUR",
          "i2ip_relu__RRR_RRR",
          "i2ip_relu__RUR_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2ipintPipeRegRegAddressImmReg extends Definition(
        "I2ipintPipeRegRegAddressImmReg",
        "I2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "i2ip_24__RCxR_RCxR",
          "i2ip_24_relu__RCxR_RCxR",
          "i2ip_28__RCxR_RCxR",
          "i2ip_28_relu__RCxR_RCxR",
          "i2ip__RCxR_RCxR",
          "i2ip_relu__RCxR_RCxR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipintPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2ipintPipeRegRegConstReg extends Definition(
        "I2ipintPipeRegRegConstReg",
        "I2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "i2ip_24__RCR_RCR",
          "i2ip_24_relu__RCR_RCR",
          "i2ip_28__RCR_RCR",
          "i2ip_28_relu__RCR_RCR",
          "i2ip__RCR_RCR",
          "i2ip_relu__RCR_RCR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipintPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2ipintPipeRegRegImmReg extends Definition(
        "I2ipintPipeRegRegImmReg",
        "I2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "i2ip_24__RsIR_RIR",
          "i2ip_24_relu__RsIR_RIR",
          "i2ip_28__RsIR_RIR",
          "i2ip_28_relu__RsIR_RIR",
          "i2ip__RsIR_RIR",
          "i2ip_relu__RsIR_RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipintPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2ipintPipeRegRegRegReg extends Definition(
        "I2ipintPipeRegRegRegReg",
        "I2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "i2ip_24__RRR_RRR",
          "i2ip_24__RUR_RUR",
          "i2ip_24_relu__RRR_RRR",
          "i2ip_24_relu__RUR_RUR",
          "i2ip_28__RRR_RRR",
          "i2ip_28__RUR_RUR",
          "i2ip_28_relu__RRR_RRR",
          "i2ip_28_relu__RUR_RUR",
          "i2ip__RRR_RRR",
          "i2ip__RUR_RUR",
          "i2ip_relu__RRR_RRR",
          "i2ip_relu__RUR_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2iintPipeRegAddressImm extends Definition(
        "I2iintPipeRegAddressImm",
        "I2Iint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("i2i__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.I2iintPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object I2iintPipeRegConst extends Definition(
        "I2iintPipeRegConst",
        "I2Iint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("i2i__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.I2iintPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2iintPipeRegImm extends Definition(
        "I2iintPipeRegImm",
        "I2Iint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("i2i__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.I2iintPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2iintPipeRegReg extends Definition(
        "I2iintPipeRegReg",
        "I2Iint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("i2i__RRR_RRR", "i2i__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2iintPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2imioPipeRegConst extends Definition(
        "I2imioPipeRegConst",
        "I2Imio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const_I2I", "Const_I2I16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.I2imioPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object I2imioPipeRegImm extends Definition(
        "I2imioPipeRegImm",
        "I2Imio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_I2I", "Imm_I2I16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.I2imioPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object I2imioPipeRegReg extends Definition(
        "I2imioPipeRegReg",
        "I2Imio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("I2I", "I2I16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2imioPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2ixluPipeRegConst extends Definition(
        "I2ixluPipeRegConst",
        "I2Ixlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const I2I", "Const I2I16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.I2ixluPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object I2ixluPipeRegImm extends Definition(
        "I2ixluPipeRegImm",
        "I2Ixlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm I2I", "Imm I2I16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.I2ixluPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object I2ixluPipeRegReg extends Definition(
        "I2ixluPipeRegReg",
        "I2Ixlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("I2I", "I2I16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2ixluPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IabsRegAddressImm extends Definition(
        "IabsRegAddressImm",
        "IABS",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("iabs__RCxR_Cx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IabsRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IabsRegConst extends Definition(
        "IabsRegConst",
        "IABS",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("iabs__RCR_C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.IabsRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object IabsRegImm extends Definition(
        "IabsRegImm",
        "IABS",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("iabs__RsIR_I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.IabsRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object IabsRegReg extends Definition(
        "IabsRegReg",
        "IABS",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("iabs__RRR_R", "iabs__RUR_UR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.IabsRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IabsintPipeRegAddressImm extends Definition(
        "IabsintPipeRegAddressImm",
        "IABSint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("iabs__RCxR_Cx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IabsintPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IabsintPipeRegConst extends Definition(
        "IabsintPipeRegConst",
        "IABSint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("iabs__RCR_C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.IabsintPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object IabsintPipeRegImm extends Definition(
        "IabsintPipeRegImm",
        "IABSint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("iabs__RsIR_I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.IabsintPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object IabsintPipeRegReg extends Definition(
        "IabsintPipeRegReg",
        "IABSint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("iabs__RRR_R", "iabs__RUR_UR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.IabsintPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegAddressImm extends Definition(
        "IaddRegPredRegAddressImm",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("iadd_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IaddRegPredRegAddressImm(dst, pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegAddressImmPred extends Definition(
        "IaddRegPredRegAddressImmPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegAddressImmPred(dst, pu, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegConst extends Definition(
        "IaddRegPredRegConst",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("iadd_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IaddRegPredRegConst(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegConstPred extends Definition(
        "IaddRegPredRegConstPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegConstPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegImm extends Definition(
        "IaddRegPredRegImm",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("iadd_64_imm__RsIR_RIR", "iadd_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IaddRegPredRegImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegImmPred extends Definition(
        "IaddRegPredRegImmPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_64_x_imm__RIR_RIR", "iadd_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegImmPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegImmReg extends Definition(
        "IaddRegPredRegImmReg",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd_xor_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddRegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegImmRegPred extends Definition(
        "IaddRegPredRegImmRegPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_xor_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegImmRegPred(dst, pu, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegReg extends Definition(
        "IaddRegPredRegReg",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("iadd_64_noimm__RRR_RRR", "iadd_64_noimm__RUR_RUR", "iadd_noimm__RRR_RRR", "iadd_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IaddRegPredRegReg(dst, pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegRegPred extends Definition(
        "IaddRegPredRegRegPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_64_x_noimm__RRR_RRR", "iadd_64_x_noimm__RUR_RUR", "iadd_x_noimm__RRR_RRR", "iadd_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegRegPred(dst, pu, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegRegReg extends Definition(
        "IaddRegPredRegRegReg",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd_xor_noimm__RRR_RRR", "iadd_xor_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddRegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegRegRegPred extends Definition(
        "IaddRegPredRegRegRegPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_xor_noimm__RRR_RRR", "iadd_x_xor_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegRegRegPred(dst, pu, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegAddressImm extends Definition(
        "IaddRegRegAddressImm",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("iadd_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IaddRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegAddressImmPred extends Definition(
        "IaddRegRegAddressImmPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegConst extends Definition(
        "IaddRegRegConst",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("Const IADD", "Const_IADD", "iadd_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IaddRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegConstPred extends Definition(
        "IaddRegRegConstPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegImm extends Definition(
        "IaddRegRegImm",
        "IADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm IADD", "Imm_IADD", "iadd_64_imm__RsIR_RIR", "iadd_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IaddRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegImmPred extends Definition(
        "IaddRegRegImmPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_64_x_imm__RIR_RIR", "iadd_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegImmReg extends Definition(
        "IaddRegRegImmReg",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd_xor_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegImmRegPred extends Definition(
        "IaddRegRegImmRegPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_xor_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegReg extends Definition(
        "IaddRegRegReg",
        "IADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("IADD", "iadd_64_noimm__RRR_RRR", "iadd_64_noimm__RUR_RUR", "iadd_noimm__RRR_RRR", "iadd_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IaddRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegRegPred extends Definition(
        "IaddRegRegRegPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_64_x_noimm__RRR_RRR", "iadd_64_x_noimm__RUR_RUR", "iadd_x_noimm__RRR_RRR", "iadd_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegRegReg extends Definition(
        "IaddRegRegRegReg",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd_xor_noimm__RRR_RRR", "iadd_xor_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegRegRegPred extends Definition(
        "IaddRegRegRegRegPred",
        "IADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_xor_noimm__RRR_RRR", "iadd_x_xor_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegAddressImmReg extends Definition(
        "Iadd3RegPredPredRegAddressImmReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) =>
          Some(Instruction.Iadd3RegPredPredRegAddressImmReg(dst, pu, pv, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegAddressImmRegPredPred extends Definition(
        "Iadd3RegPredPredRegAddressImmRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegPredPredRegAddressImmRegPredPred(
            dst,
            pu,
            pv,
            srcA,
            srcB,
            sb_offset,
            srcC,
            pp,
            pq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegConstReg extends Definition(
        "Iadd3RegPredPredRegConstReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredPredRegConstReg(dst, pu, pv, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegConstRegPredPred extends Definition(
        "Iadd3RegPredPredRegConstRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3RegPredPredRegConstRegPredPred(dst, pu, pv, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegImmReg extends Definition(
        "Iadd3RegPredPredRegImmReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredPredRegImmReg(dst, pu, pv, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegImmRegPredPred extends Definition(
        "Iadd3RegPredPredRegImmRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3RegPredPredRegImmRegPredPred(dst, pu, pv, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegRegReg extends Definition(
        "Iadd3RegPredPredRegRegReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredPredRegRegReg(dst, pu, pv, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegRegRegPredPred extends Definition(
        "Iadd3RegPredPredRegRegRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3RegPredPredRegRegRegPredPred(dst, pu, pv, srcA, srcB, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegAddressImmReg extends Definition(
        "Iadd3RegPredRegAddressImmReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredRegAddressImmReg(dst, pu, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegAddressImmRegPredPred extends Definition(
        "Iadd3RegPredRegAddressImmRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegPredRegAddressImmRegPredPred(
            dst,
            pu,
            srcA,
            srcB,
            sb_offset,
            srcC,
            pp,
            pq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3RegPredRegConstReg extends Definition(
        "Iadd3RegPredRegConstReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredRegConstReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegConstRegPredPred extends Definition(
        "Iadd3RegPredRegConstRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegPredRegConstRegPredPred(dst, pu, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegImmReg extends Definition(
        "Iadd3RegPredRegImmReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegImmRegPredPred extends Definition(
        "Iadd3RegPredRegImmRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegPredRegImmRegPredPred(dst, pu, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegRegReg extends Definition(
        "Iadd3RegPredRegRegReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegRegRegPredPred extends Definition(
        "Iadd3RegPredRegRegRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegPredRegRegRegPredPred(dst, pu, srcA, srcB, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegAddressImmReg extends Definition(
        "Iadd3RegRegAddressImmReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegAddressImmRegPredPred extends Definition(
        "Iadd3RegRegAddressImmRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3RegRegAddressImmRegPredPred(dst, srcA, srcB, sb_offset, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegConstReg extends Definition(
        "Iadd3RegRegConstReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("BConst_IADD3", "iadd3_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegConstRegPredPred extends Definition(
        "Iadd3RegRegConstRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegRegConstRegPredPred(dst, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegImmReg extends Definition(
        "Iadd3RegRegImmReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("BImm_IADD3", "iadd3_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegImmRegPredPred extends Definition(
        "Iadd3RegRegImmRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegRegImmRegPredPred(dst, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegRegReg extends Definition(
        "Iadd3RegRegRegReg",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IADD3", "iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegRegRegPredPred extends Definition(
        "Iadd3RegRegRegRegPredPred",
        "IADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegRegRegRegPredPred(dst, srcA, srcB, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iRegPredRegImm extends Definition(
        "Iadd32iRegPredRegImm",
        "IADD32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("iadd32i_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Iadd32iRegPredRegImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iRegPredRegImmPred extends Definition(
        "Iadd32iRegPredRegImmPred",
        "IADD32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd32i_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Iadd32iRegPredRegImmPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iRegRegImm extends Definition(
        "Iadd32iRegRegImm",
        "IADD32I",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("IADD32I", "iadd32i_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Iadd32iRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iRegRegImmPred extends Definition(
        "Iadd32iRegRegImmPred",
        "IADD32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd32i_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Iadd32iRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32ifauPipeRegRegImm extends Definition(
        "Iadd32ifauPipeRegRegImm",
        "IADD32Ifau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("IADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.Iadd32ifauPipeRegRegImm(dst, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32ifmaiPipeRegRegImm extends Definition(
        "Iadd32ifmaiPipeRegRegImm",
        "IADD32Ifmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("IADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.Iadd32ifmaiPipeRegRegImm(dst, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32ifxuPipeRegRegImm extends Definition(
        "Iadd32ifxuPipeRegRegImm",
        "IADD32Ifxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("IADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.Iadd32ifxuPipeRegRegImm(dst, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iintPipeRegPredRegImm extends Definition(
        "Iadd32iintPipeRegPredRegImm",
        "IADD32Iint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("iadd32i_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Iadd32iintPipeRegPredRegImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iintPipeRegPredRegImmPred extends Definition(
        "Iadd32iintPipeRegPredRegImmPred",
        "IADD32Iint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd32i_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Iadd32iintPipeRegPredRegImmPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iintPipeRegRegImm extends Definition(
        "Iadd32iintPipeRegRegImm",
        "IADD32Iint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("iadd32i_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Iadd32iintPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iintPipeRegRegImmPred extends Definition(
        "Iadd32iintPipeRegRegImmPred",
        "IADD32Iint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd32i_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Iadd32iintPipeRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32ixluPipeRegRegImm extends Definition(
        "Iadd32ixluPipeRegRegImm",
        "IADD32Ixlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("IADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.Iadd32ixluPipeRegRegImm(dst, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3fmaiPipeRegRegConstReg extends Definition(
        "Iadd3fmaiPipeRegRegConstReg",
        "IADD3fmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("BConst_IADD3")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3fmaiPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3fmaiPipeRegRegImmReg extends Definition(
        "Iadd3fmaiPipeRegRegImmReg",
        "IADD3fmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm20", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("BImm_IADD3")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm20),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3fmaiPipeRegRegImmReg(dst, srcA, uImm20, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3fmaiPipeRegRegRegReg extends Definition(
        "Iadd3fmaiPipeRegRegRegReg",
        "IADD3fmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IADD3")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3fmaiPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3fxuPipeRegRegConstReg extends Definition(
        "Iadd3fxuPipeRegRegConstReg",
        "IADD3fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("BConst_IADD3")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3fxuPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3fxuPipeRegRegImmReg extends Definition(
        "Iadd3fxuPipeRegRegImmReg",
        "IADD3fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm20", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("BImm_IADD3")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm20),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3fxuPipeRegRegImmReg(dst, srcA, uImm20, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3fxuPipeRegRegRegReg extends Definition(
        "Iadd3fxuPipeRegRegRegReg",
        "IADD3fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IADD3")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3fxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredPredRegAddressImmReg extends Definition(
        "Iadd3intPipeRegPredPredRegAddressImmReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegPredPredRegAddressImmReg(
            dst,
            pu,
            pv,
            srcA,
            srcB,
            sb_offset,
            srcC,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredPredRegAddressImmRegPredPred extends Definition(
        "Iadd3intPipeRegPredPredRegAddressImmRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3intPipeRegPredPredRegAddressImmRegPredPred(
            dst,
            pu,
            pv,
            srcA,
            srcB,
            sb_offset,
            srcC,
            pp,
            pq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredPredRegConstReg extends Definition(
        "Iadd3intPipeRegPredPredRegConstReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegPredPredRegConstReg(dst, pu, pv, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredPredRegConstRegPredPred extends Definition(
        "Iadd3intPipeRegPredPredRegConstRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3intPipeRegPredPredRegConstRegPredPred(
            dst,
            pu,
            pv,
            srcA,
            sb,
            srcC,
            pp,
            pq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredPredRegImmReg extends Definition(
        "Iadd3intPipeRegPredPredRegImmReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegPredPredRegImmReg(dst, pu, pv, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredPredRegImmRegPredPred extends Definition(
        "Iadd3intPipeRegPredPredRegImmRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3intPipeRegPredPredRegImmRegPredPred(
            dst,
            pu,
            pv,
            srcA,
            sb,
            srcC,
            pp,
            pq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredPredRegRegReg extends Definition(
        "Iadd3intPipeRegPredPredRegRegReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegPredPredRegRegReg(dst, pu, pv, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredPredRegRegRegPredPred extends Definition(
        "Iadd3intPipeRegPredPredRegRegRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3intPipeRegPredPredRegRegRegPredPred(
            dst,
            pu,
            pv,
            srcA,
            srcB,
            srcC,
            pp,
            pq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredRegAddressImmReg extends Definition(
        "Iadd3intPipeRegPredRegAddressImmReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) =>
          Some(Instruction.Iadd3intPipeRegPredRegAddressImmReg(dst, pu, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredRegAddressImmRegPredPred extends Definition(
        "Iadd3intPipeRegPredRegAddressImmRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3intPipeRegPredRegAddressImmRegPredPred(
            dst,
            pu,
            srcA,
            srcB,
            sb_offset,
            srcC,
            pp,
            pq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredRegConstReg extends Definition(
        "Iadd3intPipeRegPredRegConstReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegPredRegConstReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredRegConstRegPredPred extends Definition(
        "Iadd3intPipeRegPredRegConstRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3intPipeRegPredRegConstRegPredPred(dst, pu, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredRegImmReg extends Definition(
        "Iadd3intPipeRegPredRegImmReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredRegImmRegPredPred extends Definition(
        "Iadd3intPipeRegPredRegImmRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3intPipeRegPredRegImmRegPredPred(dst, pu, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredRegRegReg extends Definition(
        "Iadd3intPipeRegPredRegRegReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegPredRegRegRegPredPred extends Definition(
        "Iadd3intPipeRegPredRegRegRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3intPipeRegPredRegRegRegPredPred(dst, pu, srcA, srcB, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegRegAddressImmReg extends Definition(
        "Iadd3intPipeRegRegAddressImmReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegRegAddressImmRegPredPred extends Definition(
        "Iadd3intPipeRegRegAddressImmRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3intPipeRegRegAddressImmRegPredPred(
            dst,
            srcA,
            srcB,
            sb_offset,
            srcC,
            pp,
            pq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3intPipeRegRegConstReg extends Definition(
        "Iadd3intPipeRegRegConstReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegRegConstRegPredPred extends Definition(
        "Iadd3intPipeRegRegConstRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3intPipeRegRegConstRegPredPred(dst, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegRegImmReg extends Definition(
        "Iadd3intPipeRegRegImmReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegRegImmRegPredPred extends Definition(
        "Iadd3intPipeRegRegImmRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3intPipeRegRegImmRegPredPred(dst, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegRegRegReg extends Definition(
        "Iadd3intPipeRegRegRegReg",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3intPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3intPipeRegRegRegRegPredPred extends Definition(
        "Iadd3intPipeRegRegRegRegPredPred",
        "IADD3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3intPipeRegRegRegRegPredPred(dst, srcA, srcB, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object IaddfauPipeRegRegConst extends Definition(
        "IaddfauPipeRegRegConst",
        "IADDfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IaddfauPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IaddfauPipeRegRegImm extends Definition(
        "IaddfauPipeRegRegImm",
        "IADDfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IaddfauPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IaddfauPipeRegRegReg extends Definition(
        "IaddfauPipeRegRegReg",
        "IADDfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IaddfauPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddfmaiPipeRegRegConst extends Definition(
        "IaddfmaiPipeRegRegConst",
        "IADDfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IaddfmaiPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IaddfmaiPipeRegRegImm extends Definition(
        "IaddfmaiPipeRegRegImm",
        "IADDfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IaddfmaiPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IaddfmaiPipeRegRegReg extends Definition(
        "IaddfmaiPipeRegRegReg",
        "IADDfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IaddfmaiPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddfxuPipeRegRegConst extends Definition(
        "IaddfxuPipeRegRegConst",
        "IADDfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IaddfxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IaddfxuPipeRegRegImm extends Definition(
        "IaddfxuPipeRegRegImm",
        "IADDfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IaddfxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IaddfxuPipeRegRegReg extends Definition(
        "IaddfxuPipeRegRegReg",
        "IADDfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IaddfxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegAddressImm extends Definition(
        "IaddintPipeRegPredRegAddressImm",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("iadd_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IaddintPipeRegPredRegAddressImm(dst, pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegAddressImmPred extends Definition(
        "IaddintPipeRegPredRegAddressImmPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.IaddintPipeRegPredRegAddressImmPred(dst, pu, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegConst extends Definition(
        "IaddintPipeRegPredRegConst",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("iadd_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IaddintPipeRegPredRegConst(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegConstPred extends Definition(
        "IaddintPipeRegPredRegConstPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddintPipeRegPredRegConstPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    Hmmafp16PipeRegRegRegReg,
    Hmmafp16PipeRegRegRegRegPred,
    Hmmafp16PipeRegRegRegRegPredRegImm,
    Hmmafp16PipeRegRegRegRegRegImm,
    Hmnmx2RegPredPredRegAddressImmPred,
    Hmnmx2RegPredPredRegConstPred,
    Hmnmx2RegPredPredRegImmImmPred,
    Hmnmx2RegPredPredRegRegPred,
    Hmnmx2RegRegAddressImmPred,
    Hmnmx2RegRegConstPred,
    Hmnmx2RegRegImmImmPred,
    Hmnmx2RegRegRegPred,
    Hmnmx2fp16PipeRegPredPredRegAddressImmPred,
    Hmnmx2fp16PipeRegPredPredRegConstPred,
    Hmnmx2fp16PipeRegPredPredRegImmImmPred,
    Hmnmx2fp16PipeRegPredPredRegRegPred,
    Hmnmx2fp16PipeRegRegAddressImmPred,
    Hmnmx2fp16PipeRegRegConstPred,
    Hmnmx2fp16PipeRegRegImmImmPred,
    Hmnmx2fp16PipeRegRegRegPred,
    Hmul2NoOperands,
    Hmul2Const,
    Hmul2ImmImm,
    Hmul2RegRegAddressImm,
    Hmul2RegRegConst,
    Hmul2RegRegImmImm,
    Hmul2RegRegReg,
    Hmul232iImmImm,
    Hmul232iRegRegImmImm,
    Hmul232ifp16PipeRegRegImmImm,
    Hmul232ifp16g0PipeImmImm,
    Hmul232ifp16g1PipeImmImm,
    Hmul2fp16PipeRegRegAddressImm,
    Hmul2fp16PipeRegRegConst,
    Hmul2fp16PipeRegRegImmImm,
    Hmul2fp16PipeRegRegReg,
    Hmul2fp16g0PipeNoOperands,
    Hmul2fp16g0PipeConst,
    Hmul2fp16g0PipeImmImm,
    Hmul2fp16g1PipeNoOperands,
    Hmul2fp16g1PipeConst,
    Hmul2fp16g1PipeImmImm,
    Hset2NoOperands,
    Hset2Const,
    Hset2ConstPred,
    Hset2ImmImm,
    Hset2ImmImmPred,
    Hset2Pred,
    Hset2RegRegAddressImm,
    Hset2RegRegAddressImmPred,
    Hset2RegRegConst,
    Hset2RegRegConstPred,
    Hset2RegRegImmImm,
    Hset2RegRegImmImmPred,
    Hset2RegRegReg,
    Hset2RegRegRegPred,
    Hset2fp16PipeRegRegAddressImm,
    Hset2fp16PipeRegRegAddressImmPred,
    Hset2fp16PipeRegRegConst,
    Hset2fp16PipeRegRegConstPred,
    Hset2fp16PipeRegRegImmImm,
    Hset2fp16PipeRegRegImmImmPred,
    Hset2fp16PipeRegRegReg,
    Hset2fp16PipeRegRegRegPred,
    Hset2fp16g0PipeNoOperands,
    Hset2fp16g0PipeConst,
    Hset2fp16g0PipeConstPred,
    Hset2fp16g0PipeImmImm,
    Hset2fp16g0PipeImmImmPred,
    Hset2fp16g0PipePred,
    Hset2fp16g1PipeNoOperands,
    Hset2fp16g1PipeConst,
    Hset2fp16g1PipeConstPred,
    Hset2fp16g1PipeImmImm,
    Hset2fp16g1PipeImmImmPred,
    Hset2fp16g1PipePred,
    Hsetp2PredPredRegAddressImm,
    Hsetp2PredPredRegAddressImmPred,
    Hsetp2PredPredRegConst,
    Hsetp2PredPredRegConstPred,
    Hsetp2PredPredRegImmImm,
    Hsetp2PredPredRegImmImmPred,
    Hsetp2PredPredRegReg,
    Hsetp2PredPredRegRegPred,
    Hsetp2fp16PipePredPredRegAddressImm,
    Hsetp2fp16PipePredPredRegAddressImmPred,
    Hsetp2fp16PipePredPredRegConst,
    Hsetp2fp16PipePredPredRegConstPred,
    Hsetp2fp16PipePredPredRegImmImm,
    Hsetp2fp16PipePredPredRegImmImmPred,
    Hsetp2fp16PipePredPredRegReg,
    Hsetp2fp16PipePredPredRegRegPred,
    Hsetp2fp16g0PipePredPredRegConst,
    Hsetp2fp16g0PipePredPredRegConstPred,
    Hsetp2fp16g0PipePredPredRegImmImm,
    Hsetp2fp16g0PipePredPredRegImmImmPred,
    Hsetp2fp16g0PipePredPredRegReg,
    Hsetp2fp16g0PipePredPredRegRegPred,
    Hsetp2fp16g1PipePredPredRegConst,
    Hsetp2fp16g1PipePredPredRegConstPred,
    Hsetp2fp16g1PipePredPredRegImmImm,
    Hsetp2fp16g1PipePredPredRegImmImmPred,
    Hsetp2fp16g1PipePredPredRegReg,
    Hsetp2fp16g1PipePredPredRegRegPred,
    I2fRegAddressImm,
    I2fRegConst,
    I2fRegImm,
    I2fRegReg,
    I2fpRegAddressImm,
    I2fpRegConst,
    I2fpRegImm,
    I2fpRegReg,
    I2fpintPipeRegAddressImm,
    I2fpintPipeRegConst,
    I2fpintPipeRegImm,
    I2fpintPipeRegReg,
    I2ffmaxPipeRegConst,
    I2ffmaxPipeRegImm,
    I2ffmaxPipeRegReg,
    I2fmioPipeRegAddressImm,
    I2fmioPipeRegConst,
    I2fmioPipeRegImm,
    I2fmioPipeRegReg,
    I2iRegAddressImm,
    I2iRegConst,
    I2iRegImm,
    I2iRegReg,
    I2ipRegRegAddressImmReg,
    I2ipRegRegConstReg,
    I2ipRegRegImmReg,
    I2ipRegRegRegReg,
    I2ipintPipeRegRegAddressImmReg,
    I2ipintPipeRegRegConstReg,
    I2ipintPipeRegRegImmReg,
    I2ipintPipeRegRegRegReg,
    I2iintPipeRegAddressImm,
    I2iintPipeRegConst,
    I2iintPipeRegImm,
    I2iintPipeRegReg,
    I2imioPipeRegConst,
    I2imioPipeRegImm,
    I2imioPipeRegReg,
    I2ixluPipeRegConst,
    I2ixluPipeRegImm,
    I2ixluPipeRegReg,
    IabsRegAddressImm,
    IabsRegConst,
    IabsRegImm,
    IabsRegReg,
    IabsintPipeRegAddressImm,
    IabsintPipeRegConst,
    IabsintPipeRegImm,
    IabsintPipeRegReg,
    IaddRegPredRegAddressImm,
    IaddRegPredRegAddressImmPred,
    IaddRegPredRegConst,
    IaddRegPredRegConstPred,
    IaddRegPredRegImm,
    IaddRegPredRegImmPred,
    IaddRegPredRegImmReg,
    IaddRegPredRegImmRegPred,
    IaddRegPredRegReg,
    IaddRegPredRegRegPred,
    IaddRegPredRegRegReg,
    IaddRegPredRegRegRegPred,
    IaddRegRegAddressImm,
    IaddRegRegAddressImmPred,
    IaddRegRegConst,
    IaddRegRegConstPred,
    IaddRegRegImm,
    IaddRegRegImmPred,
    IaddRegRegImmReg,
    IaddRegRegImmRegPred,
    IaddRegRegReg,
    IaddRegRegRegPred,
    IaddRegRegRegReg,
    IaddRegRegRegRegPred,
    Iadd3RegPredPredRegAddressImmReg,
    Iadd3RegPredPredRegAddressImmRegPredPred,
    Iadd3RegPredPredRegConstReg,
    Iadd3RegPredPredRegConstRegPredPred,
    Iadd3RegPredPredRegImmReg,
    Iadd3RegPredPredRegImmRegPredPred,
    Iadd3RegPredPredRegRegReg,
    Iadd3RegPredPredRegRegRegPredPred,
    Iadd3RegPredRegAddressImmReg,
    Iadd3RegPredRegAddressImmRegPredPred,
    Iadd3RegPredRegConstReg,
    Iadd3RegPredRegConstRegPredPred,
    Iadd3RegPredRegImmReg,
    Iadd3RegPredRegImmRegPredPred,
    Iadd3RegPredRegRegReg,
    Iadd3RegPredRegRegRegPredPred,
    Iadd3RegRegAddressImmReg,
    Iadd3RegRegAddressImmRegPredPred,
    Iadd3RegRegConstReg,
    Iadd3RegRegConstRegPredPred,
    Iadd3RegRegImmReg,
    Iadd3RegRegImmRegPredPred,
    Iadd3RegRegRegReg,
    Iadd3RegRegRegRegPredPred,
    Iadd32iRegPredRegImm,
    Iadd32iRegPredRegImmPred,
    Iadd32iRegRegImm,
    Iadd32iRegRegImmPred,
    Iadd32ifauPipeRegRegImm,
    Iadd32ifmaiPipeRegRegImm,
    Iadd32ifxuPipeRegRegImm,
    Iadd32iintPipeRegPredRegImm,
    Iadd32iintPipeRegPredRegImmPred,
    Iadd32iintPipeRegRegImm,
    Iadd32iintPipeRegRegImmPred,
    Iadd32ixluPipeRegRegImm,
    Iadd3fmaiPipeRegRegConstReg,
    Iadd3fmaiPipeRegRegImmReg,
    Iadd3fmaiPipeRegRegRegReg,
    Iadd3fxuPipeRegRegConstReg,
    Iadd3fxuPipeRegRegImmReg,
    Iadd3fxuPipeRegRegRegReg,
    Iadd3intPipeRegPredPredRegAddressImmReg,
    Iadd3intPipeRegPredPredRegAddressImmRegPredPred,
    Iadd3intPipeRegPredPredRegConstReg,
    Iadd3intPipeRegPredPredRegConstRegPredPred,
    Iadd3intPipeRegPredPredRegImmReg,
    Iadd3intPipeRegPredPredRegImmRegPredPred,
    Iadd3intPipeRegPredPredRegRegReg,
    Iadd3intPipeRegPredPredRegRegRegPredPred,
    Iadd3intPipeRegPredRegAddressImmReg,
    Iadd3intPipeRegPredRegAddressImmRegPredPred,
    Iadd3intPipeRegPredRegConstReg,
    Iadd3intPipeRegPredRegConstRegPredPred,
    Iadd3intPipeRegPredRegImmReg,
    Iadd3intPipeRegPredRegImmRegPredPred,
    Iadd3intPipeRegPredRegRegReg,
    Iadd3intPipeRegPredRegRegRegPredPred,
    Iadd3intPipeRegRegAddressImmReg,
    Iadd3intPipeRegRegAddressImmRegPredPred,
    Iadd3intPipeRegRegConstReg,
    Iadd3intPipeRegRegConstRegPredPred,
    Iadd3intPipeRegRegImmReg,
    Iadd3intPipeRegRegImmRegPredPred,
    Iadd3intPipeRegRegRegReg,
    Iadd3intPipeRegRegRegRegPredPred,
    IaddfauPipeRegRegConst,
    IaddfauPipeRegRegImm,
    IaddfauPipeRegRegReg,
    IaddfmaiPipeRegRegConst,
    IaddfmaiPipeRegRegImm,
    IaddfmaiPipeRegRegReg,
    IaddfxuPipeRegRegConst,
    IaddfxuPipeRegRegImm,
    IaddfxuPipeRegRegReg,
    IaddintPipeRegPredRegAddressImm,
    IaddintPipeRegPredRegAddressImmPred,
    IaddintPipeRegPredRegConst,
    IaddintPipeRegPredRegConstPred
  )
}
