// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions15 {
  object Vimnmx3RegRegImmRegPred extends Definition(
        "Vimnmx3RegRegImmRegPred",
        "VIMNMX3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx3__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vimnmx3RegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vimnmx3RegRegRegRegPred extends Definition(
        "Vimnmx3RegRegRegRegPred",
        "VIMNMX3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx3__RRR_RRR", "vimnmx3__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vimnmx3RegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vimnmx3intPipeRegRegAddressImmRegPred extends Definition(
        "Vimnmx3intPipeRegRegAddressImmRegPred",
        "VIMNMX3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx3__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vimnmx3intPipeRegRegAddressImmRegPred(
            dst,
            srcA,
            srcB,
            sb_offset,
            srcC,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Vimnmx3intPipeRegRegConstRegPred extends Definition(
        "Vimnmx3intPipeRegRegConstRegPred",
        "VIMNMX3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx3__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vimnmx3intPipeRegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vimnmx3intPipeRegRegImmRegPred extends Definition(
        "Vimnmx3intPipeRegRegImmRegPred",
        "VIMNMX3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx3__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vimnmx3intPipeRegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vimnmx3intPipeRegRegRegRegPred extends Definition(
        "Vimnmx3intPipeRegRegRegRegPred",
        "VIMNMX3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx3__RRR_RRR", "vimnmx3__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vimnmx3intPipeRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxintPipeRegPredPredRegAddressImmPred extends Definition(
        "VimnmxintPipeRegPredPredRegAddressImmPred",
        "VIMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx_pred__RCxR_RCxR")
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
            ) => Some(Instruction.VimnmxintPipeRegPredPredRegAddressImmPred(
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
  object VimnmxintPipeRegPredPredRegConstPred extends Definition(
        "VimnmxintPipeRegPredPredRegConstPred",
        "VIMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx_pred__RCR_RCR")
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
            ) => Some(Instruction.VimnmxintPipeRegPredPredRegConstPred(dst, pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxintPipeRegPredPredRegImmPred extends Definition(
        "VimnmxintPipeRegPredPredRegImmPred",
        "VIMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx__RIR_RsIR", "vimnmx_pred__RIR_RsIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxintPipeRegPredPredRegImmPred(dst, pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxintPipeRegPredPredRegRegPred extends Definition(
        "VimnmxintPipeRegPredPredRegRegPred",
        "VIMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx__RRR_RRR", "vimnmx__RUR_RUR", "vimnmx_pred__RRR_RRR", "vimnmx_pred__RUR_RUR")
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
            ) => Some(Instruction.VimnmxintPipeRegPredPredRegRegPred(dst, pu, pv, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxintPipeRegRegAddressImmPred extends Definition(
        "VimnmxintPipeRegRegAddressImmPred",
        "VIMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxintPipeRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxintPipeRegRegConstPred extends Definition(
        "VimnmxintPipeRegRegConstPred",
        "VIMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxintPipeRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxintPipeRegRegImmPred extends Definition(
        "VimnmxintPipeRegRegImmPred",
        "VIMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx__RIR_RsIR", "vimnmx_nopred__RIR_RsIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxintPipeRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxintPipeRegRegRegPred extends Definition(
        "VimnmxintPipeRegRegRegPred",
        "VIMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx__RRR_RRR", "vimnmx__RUR_RUR", "vimnmx_nopred__RRR_RRR", "vimnmx_nopred__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxintPipeRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object VmadRegRegImmReg extends Definition(
        "VmadRegRegImmReg",
        "VMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_VMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmadRegRegReg extends Definition(
        "VmadRegRegReg",
        "VMAD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec(
          "Imm VMAD",
          "SImm_VMAD",
          "a32 Imm VMAD",
          "a32_Imm_VMAD",
          "a32_SImm_VMAD",
          "a8 Imm VMAD",
          "a8_Imm_VMAD",
          "a8_SImm_VMAD"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmadRegRegRegReg extends Definition(
        "VmadRegRegRegReg",
        "VMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VMAD",
          "a32 VMAD",
          "a32 b32 VMAD",
          "a32 b8 VMAD",
          "a32_VMAD",
          "a32_b32_VMAD",
          "a32_b8_VMAD",
          "a8 VMAD",
          "a8 b32 VMAD",
          "a8 b8 VMAD",
          "a8_VMAD",
          "a8_b32_VMAD",
          "a8_b8_VMAD",
          "b32 VMAD",
          "b32_VMAD",
          "b8 VMAD",
          "b8_VMAD"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmadfmaxPipeRegRegReg extends Definition(
        "VmadfmaxPipeRegRegReg",
        "VMADfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("Imm VMAD", "a32 Imm VMAD", "a8 Imm VMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadfmaxPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmadfmaxPipeRegRegRegReg extends Definition(
        "VmadfmaxPipeRegRegRegReg",
        "VMADfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VMAD",
          "a32 VMAD",
          "a32 b32 VMAD",
          "a32 b8 VMAD",
          "a8 VMAD",
          "a8 b32 VMAD",
          "a8 b8 VMAD",
          "b32 VMAD",
          "b8 VMAD"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadfmaxPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmadmioPipeRegRegImmReg extends Definition(
        "VmadmioPipeRegRegImmReg",
        "VMADmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_VMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadmioPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmadmioPipeRegRegReg extends Definition(
        "VmadmioPipeRegRegReg",
        "VMADmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("SImm_VMAD", "a32_Imm_VMAD", "a32_SImm_VMAD", "a8_Imm_VMAD", "a8_SImm_VMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadmioPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmadmioPipeRegRegRegReg extends Definition(
        "VmadmioPipeRegRegRegReg",
        "VMADmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VMAD",
          "a32_VMAD",
          "a32_b32_VMAD",
          "a32_b8_VMAD",
          "a8_VMAD",
          "a8_b32_VMAD",
          "a8_b8_VMAD",
          "b32_VMAD",
          "b8_VMAD"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadmioPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmnmxRegRegImmReg extends Definition(
        "VmnmxRegRegImmReg",
        "VMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "Imm VMNMX",
          "Imm_VMNMX",
          "SImm_VMNMX",
          "a32 Imm VMNMX",
          "a32_Imm_VMNMX",
          "a32_SImm_VMNMX",
          "a8 Imm VMNMX",
          "a8_Imm_VMNMX",
          "a8_SImm_VMNMX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmnmxRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmnmxRegRegRegReg extends Definition(
        "VmnmxRegRegRegReg",
        "VMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VMNMX",
          "a32 VMNMX",
          "a32 b32 VMNMX",
          "a32 b8 VMNMX",
          "a32_VMNMX",
          "a32_b32_VMNMX",
          "a32_b8_VMNMX",
          "a8 VMNMX",
          "a8 b32 VMNMX",
          "a8 b8 VMNMX",
          "a8_VMNMX",
          "a8_b32_VMNMX",
          "a8_b8_VMNMX",
          "b32 VMNMX",
          "b32_VMNMX",
          "b8 VMNMX",
          "b8_VMNMX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmnmxRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx2RegRegImmReg extends Definition(
        "Vmnmx2RegRegImmReg",
        "VMNMX2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VMNMX2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx2RegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx2RegRegRegReg extends Definition(
        "Vmnmx2RegRegRegReg",
        "VMNMX2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VMNMX2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx2RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx2xluPipeRegRegImmReg extends Definition(
        "Vmnmx2xluPipeRegRegImmReg",
        "VMNMX2xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VMNMX2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx2xluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx2xluPipeRegRegRegReg extends Definition(
        "Vmnmx2xluPipeRegRegRegReg",
        "VMNMX2xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VMNMX2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx2xluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx4RegRegImmReg extends Definition(
        "Vmnmx4RegRegImmReg",
        "VMNMX4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VMNMX4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx4RegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx4RegRegRegReg extends Definition(
        "Vmnmx4RegRegRegReg",
        "VMNMX4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VMNMX4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx4RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx4xluPipeRegRegImmReg extends Definition(
        "Vmnmx4xluPipeRegRegImmReg",
        "VMNMX4xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VMNMX4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx4xluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx4xluPipeRegRegRegReg extends Definition(
        "Vmnmx4xluPipeRegRegRegReg",
        "VMNMX4xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VMNMX4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx4xluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmnmxfxuPipeRegRegImmReg extends Definition(
        "VmnmxfxuPipeRegRegImmReg",
        "VMNMXfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_VMNMX", "SImm_VMNMX", "a32_Imm_VMNMX", "a32_SImm_VMNMX", "a8_Imm_VMNMX", "a8_SImm_VMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmnmxfxuPipeRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmnmxfxuPipeRegRegRegReg extends Definition(
        "VmnmxfxuPipeRegRegRegReg",
        "VMNMXfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VMNMX",
          "a32_VMNMX",
          "a32_b32_VMNMX",
          "a32_b8_VMNMX",
          "a8_VMNMX",
          "a8_b32_VMNMX",
          "a8_b8_VMNMX",
          "b32_VMNMX",
          "b8_VMNMX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmnmxfxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmnmxxluPipeRegRegImmReg extends Definition(
        "VmnmxxluPipeRegRegImmReg",
        "VMNMXxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VMNMX", "a32 Imm VMNMX", "a8 Imm VMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmnmxxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmnmxxluPipeRegRegRegReg extends Definition(
        "VmnmxxluPipeRegRegRegReg",
        "VMNMXxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VMNMX",
          "a32 VMNMX",
          "a32 b32 VMNMX",
          "a32 b8 VMNMX",
          "a8 VMNMX",
          "a8 b32 VMNMX",
          "a8 b8 VMNMX",
          "b32 VMNMX",
          "b8 VMNMX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmnmxxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VoteImm extends Definition("VoteImm", "VOTE", Vec(Slot("uImm", OperandKind.Immediate)), Vec("VOTE.VTG")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.VoteImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object VotePredPred extends Definition(
        "VotePredPred",
        "VOTE",
        Vec(Slot("dstPredicate", OperandKind.Predicate), Slot("pa", OperandKind.Predicate)),
        Vec("VOTE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(dstPredicate), InstructionOperand.Predicate(pa)) =>
          Some(Instruction.VotePredPred(dstPredicate, pa, modifiers, guard))
        case _ => None
      }
  }
  object VoteRegPredPred extends Definition(
        "VoteRegPredPred",
        "VOTE",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("VOTE", "vote_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VoteRegPredPred(dst, dstPredicate, pa, modifiers, guard))
        case _ => None
      }
  }
  object VoteuRegPredPred extends Definition(
        "VoteuRegPredPred",
        "VOTEU",
        Vec(Slot("dst", OperandKind.Register), Slot("uPu", OperandKind.Predicate), Slot("pp", OperandKind.Predicate)),
        Vec("voteu_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VoteuRegPredPred(dst, uPu, pp, modifiers, guard))
        case _ => None
      }
  }
  object VoteuudpPipeRegPredPred extends Definition(
        "VoteuudpPipeRegPredPred",
        "VOTEUudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uPu", OperandKind.Predicate), Slot("pp", OperandKind.Predicate)),
        Vec("voteu_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VoteuudpPipeRegPredPred(dst, uPu, pp, modifiers, guard))
        case _ => None
      }
  }
  object VoteVtgImm
      extends Definition("VoteVtgImm", "VOTE_VTG", Vec(Slot("sa", OperandKind.Immediate)), Vec("vote_vtg_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.VoteVtgImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object VoteVtgPredImm extends Definition(
        "VoteVtgPredImm",
        "VOTE_VTG",
        Vec(Slot("pu", OperandKind.Predicate), Slot("sa", OperandKind.Immediate)),
        Vec("vote_vtg_cmp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.VoteVtgPredImm(pu, sa, modifiers, guard))
        case _ => None
      }
  }
  object VoteVtgPredPredImmPred extends Definition(
        "VoteVtgPredPredImmPred",
        "VOTE_VTG",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("sa", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vote_vtg_bop_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Immediate(sa),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VoteVtgPredPredImmPred(pu, pv, sa, pp, modifiers, guard))
        case _ => None
      }
  }
  object VoteVtgfePipeImm extends Definition(
        "VoteVtgfePipeImm",
        "VOTE_VTGfe_pipe",
        Vec(Slot("sa", OperandKind.Immediate)),
        Vec("vote_vtg_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.VoteVtgfePipeImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object VoteVtgfePipePredImm extends Definition(
        "VoteVtgfePipePredImm",
        "VOTE_VTGfe_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("sa", OperandKind.Immediate)),
        Vec("vote_vtg_cmp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.VoteVtgfePipePredImm(pu, sa, modifiers, guard))
        case _ => None
      }
  }
  object VoteVtgfePipePredPredImmPred extends Definition(
        "VoteVtgfePipePredPredImmPred",
        "VOTE_VTGfe_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("sa", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vote_vtg_bop_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Immediate(sa),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VoteVtgfePipePredPredImmPred(pu, pv, sa, pp, modifiers, guard))
        case _ => None
      }
  }
  object VotebarPipeImm
      extends Definition("VotebarPipeImm", "VOTEbar_pipe", Vec(Slot("uImm", OperandKind.Immediate)), Vec("VOTE.VTG")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.VotebarPipeImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object VotebarPipePredPred extends Definition(
        "VotebarPipePredPred",
        "VOTEbar_pipe",
        Vec(Slot("dstPredicate", OperandKind.Predicate), Slot("pa", OperandKind.Predicate)),
        Vec("VOTE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(dstPredicate), InstructionOperand.Predicate(pa)) =>
          Some(Instruction.VotebarPipePredPred(dstPredicate, pa, modifiers, guard))
        case _ => None
      }
  }
  object VotebarPipeRegPredPred extends Definition(
        "VotebarPipeRegPredPred",
        "VOTEbar_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("VOTE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VotebarPipeRegPredPred(dst, dstPredicate, pa, modifiers, guard))
        case _ => None
      }
  }
  object VotefxuPipeImm
      extends Definition("VotefxuPipeImm", "VOTEfxu_pipe", Vec(Slot("uImm", OperandKind.Immediate)), Vec("VOTE.VTG")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.VotefxuPipeImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object VotefxuPipePredPred extends Definition(
        "VotefxuPipePredPred",
        "VOTEfxu_pipe",
        Vec(Slot("dstPredicate", OperandKind.Predicate), Slot("pa", OperandKind.Predicate)),
        Vec("VOTE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(dstPredicate), InstructionOperand.Predicate(pa)) =>
          Some(Instruction.VotefxuPipePredPred(dstPredicate, pa, modifiers, guard))
        case _ => None
      }
  }
  object VotefxuPipeRegPredPred extends Definition(
        "VotefxuPipeRegPredPred",
        "VOTEfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("VOTE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VotefxuPipeRegPredPred(dst, dstPredicate, pa, modifiers, guard))
        case _ => None
      }
  }
  object VoteintPipeRegPredPred extends Definition(
        "VoteintPipeRegPredPred",
        "VOTEint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("pp", OperandKind.Predicate)),
        Vec("vote_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VoteintPipeRegPredPred(dst, pu, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vsel2RegRegImmPred extends Definition(
        "Vsel2RegRegImmPred",
        "VSEL2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm VSEL2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel2RegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object Vsel2RegRegRegPred extends Definition(
        "Vsel2RegRegRegPred",
        "VSEL2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("VSEL2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel2RegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object Vsel2xluPipeRegRegImmPred extends Definition(
        "Vsel2xluPipeRegRegImmPred",
        "VSEL2xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm VSEL2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel2xluPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object Vsel2xluPipeRegRegRegPred extends Definition(
        "Vsel2xluPipeRegRegRegPred",
        "VSEL2xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("VSEL2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel2xluPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object Vsel4RegRegImmPred extends Definition(
        "Vsel4RegRegImmPred",
        "VSEL4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm VSEL4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel4RegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object Vsel4RegRegRegPred extends Definition(
        "Vsel4RegRegRegPred",
        "VSEL4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("VSEL4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel4RegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object Vsel4xluPipeRegRegImmPred extends Definition(
        "Vsel4xluPipeRegRegImmPred",
        "VSEL4xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm VSEL4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel4xluPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object Vsel4xluPipeRegRegRegPred extends Definition(
        "Vsel4xluPipeRegRegRegPred",
        "VSEL4xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("VSEL4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel4xluPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object VsetRegRegImmReg extends Definition(
        "VsetRegRegImmReg",
        "VSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "Imm VSET",
          "Imm_VSET",
          "SImm_VSET",
          "a32 Imm VSET",
          "a32_Imm_VSET",
          "a32_SImm_VSET",
          "a8 Imm VSET",
          "a8_Imm_VSET",
          "a8_SImm_VSET"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VsetRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VsetRegRegRegReg extends Definition(
        "VsetRegRegRegReg",
        "VSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VSET",
          "a32 VSET",
          "a32 b32 VSET",
          "a32 b8 VSET",
          "a32_VSET",
          "a32_b32_VSET",
          "a32_b8_VSET",
          "a8 VSET",
          "a8 b32 VSET",
          "a8 b8 VSET",
          "a8_VSET",
          "a8_b32_VSET",
          "a8_b8_VSET",
          "b32 VSET",
          "b32_VSET",
          "b8 VSET",
          "b8_VSET"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VsetRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset2RegRegImmReg extends Definition(
        "Vset2RegRegImmReg",
        "VSET2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VSET2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset2RegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset2RegRegRegReg extends Definition(
        "Vset2RegRegRegReg",
        "VSET2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VSET2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset2RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset2xluPipeRegRegImmReg extends Definition(
        "Vset2xluPipeRegRegImmReg",
        "VSET2xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VSET2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset2xluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset2xluPipeRegRegRegReg extends Definition(
        "Vset2xluPipeRegRegRegReg",
        "VSET2xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VSET2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset2xluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset4RegRegImmReg extends Definition(
        "Vset4RegRegImmReg",
        "VSET4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VSET4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset4RegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset4RegRegRegReg extends Definition(
        "Vset4RegRegRegReg",
        "VSET4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VSET4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset4RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset4xluPipeRegRegImmReg extends Definition(
        "Vset4xluPipeRegRegImmReg",
        "VSET4xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VSET4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset4xluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset4xluPipeRegRegRegReg extends Definition(
        "Vset4xluPipeRegRegRegReg",
        "VSET4xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VSET4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset4xluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VsetpPredPredRegImmPred extends Definition(
        "VsetpPredPredRegImmPred",
        "VSETP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec(
          "Imm VSETP",
          "Imm_VSETP_bop",
          "SImm_VSETP_bop",
          "a32 Imm VSETP",
          "a32_Imm_VSETP_bop",
          "a32_SImm_VSETP_bop",
          "a8 Imm VSETP",
          "a8_Imm_VSETP_bop",
          "a8_SImm_VSETP_bop"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VsetpPredPredRegImmPred(dstPredicate, nPd, srcA, sImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object VsetpPredPredRegRegPred extends Definition(
        "VsetpPredPredRegRegPred",
        "VSETP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec(
          "VSETP",
          "VSETP_bop",
          "a32 VSETP",
          "a32 b32 VSETP",
          "a32 b8 VSETP",
          "a32_VSETP_bop",
          "a32_b32_VSETP_bop",
          "a32_b8_VSETP_bop",
          "a8 VSETP",
          "a8 b32 VSETP",
          "a8 b8 VSETP",
          "a8_VSETP_bop",
          "a8_b32_VSETP_bop",
          "a8_b8_VSETP_bop",
          "b32 VSETP",
          "b32_VSETP_bop",
          "b8 VSETP",
          "b8_VSETP_bop"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VsetpPredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object VsetpPredRegImm extends Definition(
        "VsetpPredRegImm",
        "VSETP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec(
          "Imm_VSETP",
          "NoBop Imm VSETP",
          "NoBop a32 Imm VSETP",
          "NoBop a8 Imm VSETP",
          "SImm_VSETP",
          "a32_Imm_VSETP",
          "a32_SImm_VSETP",
          "a8_Imm_VSETP",
          "a8_SImm_VSETP"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.VsetpPredRegImm(dstPredicate, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object VsetpPredRegReg extends Definition(
        "VsetpPredRegReg",
        "VSETP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec(
          "NoBop VSETP",
          "NoBop a32 VSETP",
          "NoBop a32 b32 VSETP",
          "NoBop a32 b8 VSETP",
          "NoBop a8 VSETP",
          "NoBop a8 b32 VSETP",
          "NoBop a8 b8 VSETP",
          "NoBop b32 VSETP",
          "NoBop b8 VSETP",
          "VSETP",
          "a32_VSETP",
          "a32_b32_VSETP",
          "a32_b8_VSETP",
          "a8_VSETP",
          "a8_b32_VSETP",
          "a8_b8_VSETP",
          "b32_VSETP",
          "b8_VSETP"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.VsetpPredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object VsetpfxuPipePredPredRegImmPred extends Definition(
        "VsetpfxuPipePredPredRegImmPred",
        "VSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec(
          "Imm_VSETP_bop",
          "SImm_VSETP_bop",
          "a32_Imm_VSETP_bop",
          "a32_SImm_VSETP_bop",
          "a8_Imm_VSETP_bop",
          "a8_SImm_VSETP_bop"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VsetpfxuPipePredPredRegImmPred(dstPredicate, nPd, srcA, sImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object VsetpfxuPipePredPredRegRegPred extends Definition(
        "VsetpfxuPipePredPredRegRegPred",
        "VSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec(
          "VSETP_bop",
          "a32_VSETP_bop",
          "a32_b32_VSETP_bop",
          "a32_b8_VSETP_bop",
          "a8_VSETP_bop",
          "a8_b32_VSETP_bop",
          "a8_b8_VSETP_bop",
          "b32_VSETP_bop",
          "b8_VSETP_bop"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VsetpfxuPipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object VsetpfxuPipePredRegImm extends Definition(
        "VsetpfxuPipePredRegImm",
        "VSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("Imm_VSETP", "SImm_VSETP", "a32_Imm_VSETP", "a32_SImm_VSETP", "a8_Imm_VSETP", "a8_SImm_VSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.VsetpfxuPipePredRegImm(dstPredicate, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object VsetpfxuPipePredRegReg extends Definition(
        "VsetpfxuPipePredRegReg",
        "VSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec(
          "VSETP",
          "a32_VSETP",
          "a32_b32_VSETP",
          "a32_b8_VSETP",
          "a8_VSETP",
          "a8_b32_VSETP",
          "a8_b8_VSETP",
          "b32_VSETP",
          "b8_VSETP"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.VsetpfxuPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object VsetpxluPipePredPredRegImmPred extends Definition(
        "VsetpxluPipePredPredRegImmPred",
        "VSETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm VSETP", "a32 Imm VSETP", "a8 Imm VSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VsetpxluPipePredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object VsetpxluPipePredPredRegRegPred extends Definition(
        "VsetpxluPipePredPredRegRegPred",
        "VSETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec(
          "VSETP",
          "a32 VSETP",
          "a32 b32 VSETP",
          "a32 b8 VSETP",
          "a8 VSETP",
          "a8 b32 VSETP",
          "a8 b8 VSETP",
          "b32 VSETP",
          "b8 VSETP"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VsetpxluPipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object VsetpxluPipePredRegImm extends Definition(
        "VsetpxluPipePredRegImm",
        "VSETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("NoBop Imm VSETP", "NoBop a32 Imm VSETP", "NoBop a8 Imm VSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.VsetpxluPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object VsetpxluPipePredRegReg extends Definition(
        "VsetpxluPipePredRegReg",
        "VSETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec(
          "NoBop VSETP",
          "NoBop a32 VSETP",
          "NoBop a32 b32 VSETP",
          "NoBop a32 b8 VSETP",
          "NoBop a8 VSETP",
          "NoBop a8 b32 VSETP",
          "NoBop a8 b8 VSETP",
          "NoBop b32 VSETP",
          "NoBop b8 VSETP"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.VsetpxluPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object VsetfxuPipeRegRegImmReg extends Definition(
        "VsetfxuPipeRegRegImmReg",
        "VSETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_VSET", "SImm_VSET", "a32_Imm_VSET", "a32_SImm_VSET", "a8_Imm_VSET", "a8_SImm_VSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VsetfxuPipeRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VsetfxuPipeRegRegRegReg extends Definition(
        "VsetfxuPipeRegRegRegReg",
        "VSETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VSET",
          "a32_VSET",
          "a32_b32_VSET",
          "a32_b8_VSET",
          "a8_VSET",
          "a8_b32_VSET",
          "a8_b8_VSET",
          "b32_VSET",
          "b8_VSET"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VsetfxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VsetxluPipeRegRegImmReg extends Definition(
        "VsetxluPipeRegRegImmReg",
        "VSETxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VSET", "a32 Imm VSET", "a8 Imm VSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VsetxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VsetxluPipeRegRegRegReg extends Definition(
        "VsetxluPipeRegRegRegReg",
        "VSETxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VSET",
          "a32 VSET",
          "a32 b32 VSET",
          "a32 b8 VSET",
          "a8 VSET",
          "a8 b32 VSET",
          "a8 b8 VSET",
          "b32 VSET",
          "b8 VSET"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VsetxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshlRegRegImmReg extends Definition(
        "VshlRegRegImmReg",
        "VSHL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "Imm VSHL",
          "Imm_VSHL",
          "SImm_VSHL",
          "a32 Imm VSHL",
          "a32_Imm_VSHL",
          "a32_SImm_VSHL",
          "a8 Imm VSHL",
          "a8_Imm_VSHL",
          "a8_SImm_VSHL"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshlRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshlRegRegRegReg extends Definition(
        "VshlRegRegRegReg",
        "VSHL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VSHL",
          "a32 VSHL",
          "a32 b32 VSHL",
          "a32 b8 VSHL",
          "a32_VSHL",
          "a32_b32_VSHL",
          "a32_b8_VSHL",
          "a8 VSHL",
          "a8 b32 VSHL",
          "a8 b8 VSHL",
          "a8_VSHL",
          "a8_b32_VSHL",
          "a8_b8_VSHL",
          "b32 VSHL",
          "b32_VSHL",
          "b8 VSHL",
          "b8_VSHL"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshlRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshlfxuPipeRegRegImmReg extends Definition(
        "VshlfxuPipeRegRegImmReg",
        "VSHLfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_VSHL", "SImm_VSHL", "a32_Imm_VSHL", "a32_SImm_VSHL", "a8_Imm_VSHL", "a8_SImm_VSHL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshlfxuPipeRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshlfxuPipeRegRegRegReg extends Definition(
        "VshlfxuPipeRegRegRegReg",
        "VSHLfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VSHL",
          "a32_VSHL",
          "a32_b32_VSHL",
          "a32_b8_VSHL",
          "a8_VSHL",
          "a8_b32_VSHL",
          "a8_b8_VSHL",
          "b32_VSHL",
          "b8_VSHL"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshlfxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshlxluPipeRegRegImmReg extends Definition(
        "VshlxluPipeRegRegImmReg",
        "VSHLxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VSHL", "a32 Imm VSHL", "a8 Imm VSHL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshlxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshlxluPipeRegRegRegReg extends Definition(
        "VshlxluPipeRegRegRegReg",
        "VSHLxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VSHL",
          "a32 VSHL",
          "a32 b32 VSHL",
          "a32 b8 VSHL",
          "a8 VSHL",
          "a8 b32 VSHL",
          "a8 b8 VSHL",
          "b32 VSHL",
          "b8 VSHL"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshlxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshrRegRegImmReg extends Definition(
        "VshrRegRegImmReg",
        "VSHR",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "Imm VSHL",
          "Imm_VSHR",
          "SImm_VSHR",
          "a32 Imm VSHL",
          "a32_Imm_VSHR",
          "a32_SImm_VSHR",
          "a8 Imm VSHL",
          "a8_Imm_VSHR",
          "a8_SImm_VSHR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshrRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshrRegRegRegReg extends Definition(
        "VshrRegRegRegReg",
        "VSHR",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VSHL",
          "VSHR",
          "a32 VSHL",
          "a32 b32 VSHL",
          "a32 b8 VSHL",
          "a32_VSHR",
          "a32_b32_VSHR",
          "a32_b8_VSHR",
          "a8 VSHL",
          "a8 b32 VSHL",
          "a8 b8 VSHL",
          "a8_VSHR",
          "a8_b32_VSHR",
          "a8_b8_VSHR",
          "b32 VSHL",
          "b32_VSHR",
          "b8 VSHL",
          "b8_VSHR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshrRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshrfxuPipeRegRegImmReg extends Definition(
        "VshrfxuPipeRegRegImmReg",
        "VSHRfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_VSHR", "SImm_VSHR", "a32_Imm_VSHR", "a32_SImm_VSHR", "a8_Imm_VSHR", "a8_SImm_VSHR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshrfxuPipeRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshrfxuPipeRegRegRegReg extends Definition(
        "VshrfxuPipeRegRegRegReg",
        "VSHRfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VSHR",
          "a32_VSHR",
          "a32_b32_VSHR",
          "a32_b8_VSHR",
          "a8_VSHR",
          "a8_b32_VSHR",
          "a8_b8_VSHR",
          "b32_VSHR",
          "b8_VSHR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshrfxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshrxluPipeRegRegImmReg extends Definition(
        "VshrxluPipeRegRegImmReg",
        "VSHRxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VSHL", "a32 Imm VSHL", "a8 Imm VSHL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshrxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshrxluPipeRegRegRegReg extends Definition(
        "VshrxluPipeRegRegRegReg",
        "VSHRxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VSHL",
          "a32 VSHL",
          "a32 b32 VSHL",
          "a32 b8 VSHL",
          "a8 VSHL",
          "a8 b32 VSHL",
          "a8 b8 VSHL",
          "b32 VSHL",
          "b8 VSHL"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshrxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object WarpgroupNoOperands
      extends Definition("WarpgroupNoOperands", "WARPGROUP", Vec(), Vec("warpgroup_arrive_", "warpgroup_wait_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.WarpgroupNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object WarpgroupImm extends Definition(
        "WarpgroupImm",
        "WARPGROUP",
        Vec(Slot("gsbcnt", OperandKind.Immediate)),
        Vec("warpgroup_depbar_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(gsbcnt)) => Some(Instruction.WarpgroupImm(gsbcnt, modifiers, guard))
        case _ => None
      }
  }
  object WarpgroupsetReg extends Definition(
        "WarpgroupsetReg",
        "WARPGROUPSET",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("warpgroupset__AC", "warpgroupset__SB")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.WarpgroupsetReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object WarpgroupsetmioPipeReg extends Definition(
        "WarpgroupsetmioPipeReg",
        "WARPGROUPSETmio_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("warpgroupset__AC", "warpgroupset__SB")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.WarpgroupsetmioPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object WarpgroupmioPipeNoOperands extends Definition(
        "WarpgroupmioPipeNoOperands",
        "WARPGROUPmio_pipe",
        Vec(),
        Vec("warpgroup_arrive_", "warpgroup_wait_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.WarpgroupmioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object WarpgroupmioPipeImm extends Definition(
        "WarpgroupmioPipeImm",
        "WARPGROUPmio_pipe",
        Vec(Slot("gsbcnt", OperandKind.Immediate)),
        Vec("warpgroup_depbar_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(gsbcnt)) =>
          Some(Instruction.WarpgroupmioPipeImm(gsbcnt, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncNoOperands extends Definition("WarpsyncNoOperands", "WARPSYNC", Vec(), Vec("warpsync__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.WarpsyncNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncAddressImm extends Definition(
        "WarpsyncAddressImm",
        "WARPSYNC",
        Vec(Slot("srcB", OperandKind.Address), Slot("sb_offset", OperandKind.Immediate)),
        Vec("warpsync__RCXR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.WarpsyncAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncConst
      extends Definition("WarpsyncConst", "WARPSYNC", Vec(Slot("sb", OperandKind.ConstantBank)), Vec("warpsync__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.WarpsyncConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncImm extends Definition(
        "WarpsyncImm",
        "WARPSYNC",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("warpsync__RIR", "warpsync_collective__RIR", "warpsync_rel__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.WarpsyncImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPred
      extends Definition("WarpsyncPred", "WARPSYNC", Vec(Slot("pp", OperandKind.Predicate)), Vec("warpsync__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.WarpsyncPred(pp, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPredAddressImm extends Definition(
        "WarpsyncPredAddressImm",
        "WARPSYNC",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("warpsync__RCXR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.WarpsyncPredAddressImm(pp, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPredConst extends Definition(
        "WarpsyncPredConst",
        "WARPSYNC",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.ConstantBank)),
        Vec("warpsync__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.WarpsyncPredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPredImm extends Definition(
        "WarpsyncPredImm",
        "WARPSYNC",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("warpsync__RIR", "warpsync_collective__RIR", "warpsync_rel__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.WarpsyncPredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPredReg extends Definition(
        "WarpsyncPredReg",
        "WARPSYNC",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("warpsync__RRR", "warpsync__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.WarpsyncPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPredRegImm extends Definition(
        "WarpsyncPredRegImm",
        "WARPSYNC",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("warpsync_collective__RRR", "warpsync_rel__RRR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.WarpsyncPredRegImm(pp, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncReg extends Definition(
        "WarpsyncReg",
        "WARPSYNC",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("warpsync__RRR", "warpsync__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.WarpsyncReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncRegImm extends Definition(
        "WarpsyncRegImm",
        "WARPSYNC",
        Vec(Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("warpsync_collective__RRR", "warpsync_rel__RRR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.WarpsyncRegImm(srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipeNoOperands
      extends Definition("WarpsynccbuPipeNoOperands", "WARPSYNCcbu_pipe", Vec(), Vec("warpsync__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.WarpsynccbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipeAddressImm extends Definition(
        "WarpsynccbuPipeAddressImm",
        "WARPSYNCcbu_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("sb_offset", OperandKind.Immediate)),
        Vec("warpsync__RCXR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.WarpsynccbuPipeAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipeConst extends Definition(
        "WarpsynccbuPipeConst",
        "WARPSYNCcbu_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("warpsync__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.WarpsynccbuPipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipeImm extends Definition(
        "WarpsynccbuPipeImm",
        "WARPSYNCcbu_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("warpsync__RIR", "warpsync_collective__RIR", "warpsync_rel__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.WarpsynccbuPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipePred extends Definition(
        "WarpsynccbuPipePred",
        "WARPSYNCcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate)),
        Vec("warpsync__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.WarpsynccbuPipePred(pp, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipePredAddressImm extends Definition(
        "WarpsynccbuPipePredAddressImm",
        "WARPSYNCcbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("warpsync__RCXR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.WarpsynccbuPipePredAddressImm(pp, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipePredConst extends Definition(
        "WarpsynccbuPipePredConst",
        "WARPSYNCcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.ConstantBank)),
        Vec("warpsync__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.WarpsynccbuPipePredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipePredImm extends Definition(
        "WarpsynccbuPipePredImm",
        "WARPSYNCcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("warpsync__RIR", "warpsync_collective__RIR", "warpsync_rel__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.WarpsynccbuPipePredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipePredReg extends Definition(
        "WarpsynccbuPipePredReg",
        "WARPSYNCcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("warpsync__RRR", "warpsync__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.WarpsynccbuPipePredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipePredRegImm extends Definition(
        "WarpsynccbuPipePredRegImm",
        "WARPSYNCcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("warpsync_collective__RRR", "warpsync_rel__RRR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.WarpsynccbuPipePredRegImm(pp, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipeReg extends Definition(
        "WarpsynccbuPipeReg",
        "WARPSYNCcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("warpsync__RRR", "warpsync__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.WarpsynccbuPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object WarpsynccbuPipeRegImm extends Definition(
        "WarpsynccbuPipeRegImm",
        "WARPSYNCcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("warpsync_collective__RRR", "warpsync_rel__RRR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.WarpsynccbuPipeRegImm(srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object XmadRegRegConstReg extends Definition(
        "XmadRegRegConstReg",
        "XMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ConstB_XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object XmadRegRegImmReg extends Definition(
        "XmadRegRegImmReg",
        "XMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ImmB_XMAD", "SImmB_XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object XmadRegRegRegConst extends Definition(
        "XmadRegRegRegConst",
        "XMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("ConstC_XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.XmadRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object XmadRegRegRegReg extends Definition(
        "XmadRegRegRegReg",
        "XMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object XmadfmaiPipeRegRegConstReg extends Definition(
        "XmadfmaiPipeRegRegConstReg",
        "XMADfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ConstB_XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadfmaiPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object XmadfmaiPipeRegRegImmReg extends Definition(
        "XmadfmaiPipeRegRegImmReg",
        "XMADfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ImmB_XMAD", "SImmB_XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadfmaiPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object XmadfmaiPipeRegRegRegConst extends Definition(
        "XmadfmaiPipeRegRegRegConst",
        "XMADfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("ConstC_XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.XmadfmaiPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object XmadfmaiPipeRegRegRegReg extends Definition(
        "XmadfmaiPipeRegRegRegReg",
        "XMADfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadfmaiPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object XmadfxuPipeRegRegConstReg extends Definition(
        "XmadfxuPipeRegRegConstReg",
        "XMADfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ConstB_XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadfxuPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object XmadfxuPipeRegRegImmReg extends Definition(
        "XmadfxuPipeRegRegImmReg",
        "XMADfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ImmB_XMAD", "SImmB_XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadfxuPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object XmadfxuPipeRegRegRegConst extends Definition(
        "XmadfxuPipeRegRegRegConst",
        "XMADfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("ConstC_XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.XmadfxuPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object XmadfxuPipeRegRegRegReg extends Definition(
        "XmadfxuPipeRegRegRegReg",
        "XMADfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("XMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadfxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object YieldNoOperands extends Definition("YieldNoOperands", "YIELD", Vec(), Vec("yield_inst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.YieldNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object YieldPred
      extends Definition("YieldPred", "YIELD", Vec(Slot("pp", OperandKind.Predicate)), Vec("yield_inst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.YieldPred(pp, modifiers, guard))
        case _ => None
      }
  }
  object YieldcbuPipeNoOperands
      extends Definition("YieldcbuPipeNoOperands", "YIELDcbu_pipe", Vec(), Vec("yield_inst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.YieldcbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object YieldcbuPipePred extends Definition(
        "YieldcbuPipePred",
        "YIELDcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate)),
        Vec("yield_inst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.YieldcbuPipePred(pp, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x002NoOperands extends Definition("Hir0x002NoOperands", "__HIR0X002", Vec(), Vec("ttucctl_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x002NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x002Hir0x00aPipeNoOperands
      extends Definition("Hir0x002Hir0x00aPipeNoOperands", "__HIR0X002__HIR0X00A_pipe", Vec(), Vec("ttucctl_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x002Hir0x00aPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x003NoOperands extends Definition("Hir0x003NoOperands", "__HIR0X003", Vec(), Vec("ttuclose_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x003NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x003Pred
      extends Definition("Hir0x003Pred", "__HIR0X003", Vec(Slot("pu", OperandKind.Predicate)), Vec("ttuclose_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu)) => Some(Instruction.Hir0x003Pred(pu, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x003Hir0x00aPipeNoOperands
      extends Definition("Hir0x003Hir0x00aPipeNoOperands", "__HIR0X003__HIR0X00A_pipe", Vec(), Vec("ttuclose_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x003Hir0x00aPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x003Hir0x00aPipePred extends Definition(
        "Hir0x003Hir0x00aPipePred",
        "__HIR0X003__HIR0X00A_pipe",
        Vec(Slot("pu", OperandKind.Predicate)),
        Vec("ttuclose_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu)) => Some(Instruction.Hir0x003Hir0x00aPipePred(pu, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x004NoOperands extends Definition("Hir0x004NoOperands", "__HIR0X004", Vec(), Vec("ttugo_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x004NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x004Hir0x00aPipeNoOperands
      extends Definition("Hir0x004Hir0x00aPipeNoOperands", "__HIR0X004__HIR0X00A_pipe", Vec(), Vec("ttugo_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x004Hir0x00aPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x005PredRegRegImm extends Definition(
        "Hir0x005PredRegRegImm",
        "__HIR0X005",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("immU16", OperandKind.Immediate)
        ),
        Vec("ttuld_", "ttuld__close", "ttuld__no_close")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(immU16)
            ) => Some(Instruction.Hir0x005PredRegRegImm(pu, rd2, dst, immU16, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x005RegRegImm extends Definition(
        "Hir0x005RegRegImm",
        "__HIR0X005",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("immU16", OperandKind.Immediate)
        ),
        Vec("ttuld_", "ttuld__close", "ttuld__no_close")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(immU16)
            ) => Some(Instruction.Hir0x005RegRegImm(rd2, dst, immU16, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x005Hir0x00aPipePredRegRegImm extends Definition(
        "Hir0x005Hir0x00aPipePredRegRegImm",
        "__HIR0X005__HIR0X00A_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("immU16", OperandKind.Immediate)
        ),
        Vec("ttuld_", "ttuld__close", "ttuld__no_close")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(immU16)
            ) => Some(Instruction.Hir0x005Hir0x00aPipePredRegRegImm(pu, rd2, dst, immU16, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x005Hir0x00aPipeRegRegImm extends Definition(
        "Hir0x005Hir0x00aPipeRegRegImm",
        "__HIR0X005__HIR0X00A_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("immU16", OperandKind.Immediate)
        ),
        Vec("ttuld_", "ttuld__close", "ttuld__no_close")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(immU16)
            ) => Some(Instruction.Hir0x005Hir0x00aPipeRegRegImm(rd2, dst, immU16, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x006Imm
      extends Definition("Hir0x006Imm", "__HIR0X006", Vec(Slot("sb", OperandKind.Immediate)), Vec("ttumacrofuse_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.Hir0x006Imm(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x006Hir0x00aPipeImm extends Definition(
        "Hir0x006Hir0x00aPipeImm",
        "__HIR0X006__HIR0X00A_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("ttumacrofuse_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.Hir0x006Hir0x00aPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x007NoOperands extends Definition("Hir0x007NoOperands", "__HIR0X007", Vec(), Vec("ttuopen_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x007NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x007Hir0x00aPipeNoOperands
      extends Definition("Hir0x007Hir0x00aPipeNoOperands", "__HIR0X007__HIR0X00A_pipe", Vec(), Vec("ttuopen_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x007Hir0x00aPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x008ImmRegReg extends Definition(
        "Hir0x008ImmRegReg",
        "__HIR0X008",
        Vec(
          Slot("immU16", OperandKind.Immediate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ttust_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(immU16),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hir0x008ImmRegReg(immU16, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x008Hir0x00aPipeImmRegReg extends Definition(
        "Hir0x008Hir0x00aPipeImmRegReg",
        "__HIR0X008__HIR0X00A_pipe",
        Vec(
          Slot("immU16", OperandKind.Immediate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ttust_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(immU16),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hir0x008Hir0x00aPipeImmRegReg(immU16, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x020NoOperands extends Definition("Hir0x020NoOperands", "__HIR0X020", Vec(), Vec("ide_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x020NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x020Imm
      extends Definition("Hir0x020Imm", "__HIR0X020", Vec(Slot("sb", OperandKind.Immediate)), Vec("ide_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.Hir0x020Imm(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x020intPipeNoOperands
      extends Definition("Hir0x020intPipeNoOperands", "__HIR0X020int_pipe", Vec(), Vec("ide_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x020intPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x020intPipeImm extends Definition(
        "Hir0x020intPipeImm",
        "__HIR0X020int_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("ide_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.Hir0x020intPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120AddressImm extends Definition(
        "Hir0x120AddressImm",
        "__HIR0X120",
        Vec(Slot("srcB", OperandKind.Address), Slot("sb_offset", OperandKind.Immediate)),
        Vec("nanotrap__CX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.Hir0x120AddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120Const
      extends Definition("Hir0x120Const", "__HIR0X120", Vec(Slot("sb", OperandKind.ConstantBank)), Vec("nanotrap__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hir0x120Const(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120Imm
      extends Definition("Hir0x120Imm", "__HIR0X120", Vec(Slot("sb", OperandKind.Immediate)), Vec("nanotrap__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.Hir0x120Imm(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120PredAddressImm extends Definition(
        "Hir0x120PredAddressImm",
        "__HIR0X120",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("nanotrap__CX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.Hir0x120PredAddressImm(pp, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120PredConst extends Definition(
        "Hir0x120PredConst",
        "__HIR0X120",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.ConstantBank)),
        Vec("nanotrap__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.Hir0x120PredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120PredImm extends Definition(
        "Hir0x120PredImm",
        "__HIR0X120",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("nanotrap__I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Hir0x120PredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120PredReg extends Definition(
        "Hir0x120PredReg",
        "__HIR0X120",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("nanotrap__R", "nanotrap__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Hir0x120PredReg(pp, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120Reg extends Definition(
        "Hir0x120Reg",
        "__HIR0X120",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("nanotrap__R", "nanotrap__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.Hir0x120Reg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120cbuPipeAddressImm extends Definition(
        "Hir0x120cbuPipeAddressImm",
        "__HIR0X120cbu_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("sb_offset", OperandKind.Immediate)),
        Vec("nanotrap__CX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.Hir0x120cbuPipeAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120cbuPipeConst extends Definition(
        "Hir0x120cbuPipeConst",
        "__HIR0X120cbu_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("nanotrap__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hir0x120cbuPipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120cbuPipeImm extends Definition(
        "Hir0x120cbuPipeImm",
        "__HIR0X120cbu_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("nanotrap__I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.Hir0x120cbuPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120cbuPipePredAddressImm extends Definition(
        "Hir0x120cbuPipePredAddressImm",
        "__HIR0X120cbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("nanotrap__CX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.Hir0x120cbuPipePredAddressImm(pp, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120cbuPipePredConst extends Definition(
        "Hir0x120cbuPipePredConst",
        "__HIR0X120cbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.ConstantBank)),
        Vec("nanotrap__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.Hir0x120cbuPipePredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120cbuPipePredImm extends Definition(
        "Hir0x120cbuPipePredImm",
        "__HIR0X120cbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("nanotrap__I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Hir0x120cbuPipePredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120cbuPipePredReg extends Definition(
        "Hir0x120cbuPipePredReg",
        "__HIR0X120cbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("nanotrap__R", "nanotrap__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Hir0x120cbuPipePredReg(pp, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120cbuPipeReg extends Definition(
        "Hir0x120cbuPipeReg",
        "__HIR0X120cbu_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("nanotrap__R", "nanotrap__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.Hir0x120cbuPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x130AddressAddressAddressAddressAddressAddressPred extends Definition(
        "Hir0x130AddressAddressAddressAddressAddressAddressPred",
        "__HIR0X130",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utcmxqmma_1cta_scale__A_gdesc",
          "utcmxqmma_1cta_scale__A_tmem",
          "utcmxqmma_1cta_scale_one__A_gdesc",
          "utcmxqmma_1cta_scale_one__A_tmem",
          "utcmxqmma_2cta_scale__A_gdesc",
          "utcmxqmma_2cta_scale__A_tmem",
          "utcmxqmma_2cta_scale_one__A_gdesc",
          "utcmxqmma_2cta_scale_one__A_tmem"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Address(uRe),
              InstructionOperand.Address(uRh),
              InstructionOperand.Address(uRi),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Hir0x130AddressAddressAddressAddressAddressAddressPred(
            srcA,
            srcB,
            srcC,
            uRe,
            uRh,
            uRi,
            uPp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Hir0x130udpPipeAddressAddressAddressAddressAddressAddressPred extends Definition(
        "Hir0x130udpPipeAddressAddressAddressAddressAddressAddressPred",
        "__HIR0X130udp_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utcmxqmma_1cta_scale__A_gdesc",
          "utcmxqmma_1cta_scale__A_tmem",
          "utcmxqmma_1cta_scale_one__A_gdesc",
          "utcmxqmma_1cta_scale_one__A_tmem",
          "utcmxqmma_2cta_scale__A_gdesc",
          "utcmxqmma_2cta_scale__A_tmem",
          "utcmxqmma_2cta_scale_one__A_gdesc",
          "utcmxqmma_2cta_scale_one__A_tmem"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Address(uRe),
              InstructionOperand.Address(uRh),
              InstructionOperand.Address(uRi),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Hir0x130udpPipeAddressAddressAddressAddressAddressAddressPred(
            srcA,
            srcB,
            srcC,
            uRe,
            uRh,
            uRi,
            uPp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Hir0x150RegRegRegRegPredRegRegReg extends Definition(
        "Hir0x150RegRegRegRegPredRegRegReg",
        "__HIR0X150",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register)
        ),
        Vec("mxqmma_scale_")
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
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi)
            ) =>
          Some(Instruction.Hir0x150RegRegRegRegPredRegRegReg(dst, srcA, srcB, srcC, uPp, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x150RegRegRegRegRegRegReg extends Definition(
        "Hir0x150RegRegRegRegRegRegReg",
        "__HIR0X150",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register)
        ),
        Vec("mxqmma_scale_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi)
            ) => Some(Instruction.Hir0x150RegRegRegRegRegRegReg(dst, srcA, srcB, srcC, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x150fp16PipeRegRegRegRegPredRegRegReg extends Definition(
        "Hir0x150fp16PipeRegRegRegRegPredRegRegReg",
        "__HIR0X150fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register)
        ),
        Vec("mxqmma_scale_")
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
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi)
            ) => Some(Instruction.Hir0x150fp16PipeRegRegRegRegPredRegRegReg(
            dst,
            srcA,
            srcB,
            srcC,
            uPp,
            re,
            rh,
            uRi,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Hir0x150fp16PipeRegRegRegRegRegRegReg extends Definition(
        "Hir0x150fp16PipeRegRegRegRegRegRegReg",
        "__HIR0X150fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register)
        ),
        Vec("mxqmma_scale_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi)
            ) =>
          Some(Instruction.Hir0x150fp16PipeRegRegRegRegRegRegReg(dst, srcA, srcB, srcC, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x160Reg extends Definition(
        "Hir0x160Reg",
        "__HIR0X160",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("utcstsws_", "utcstsws_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.Hir0x160Reg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x160udpPipeReg extends Definition(
        "Hir0x160udpPipeReg",
        "__HIR0X160udp_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("utcstsws_", "utcstsws_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.Hir0x160udpPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x170Reg extends Definition(
        "Hir0x170Reg",
        "__HIR0X170",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("utcldsws_", "utcldsws_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.Hir0x170Reg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x170udpPipeReg extends Definition(
        "Hir0x170udpPipeReg",
        "__HIR0X170udp_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("utcldsws_", "utcldsws_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.Hir0x170udpPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1a0RegRegRegRegImmImm extends Definition(
        "Hir0x1a0RegRegRegRegImmImm",
        "__HIR0X1A0",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("vecidx2", OperandKind.Immediate),
          Slot("mdidx", OperandKind.Immediate)
        ),
        Vec("genmetadata_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(vecidx2),
              InstructionOperand.Immediate(mdidx)
            ) => Some(Instruction.Hir0x1a0RegRegRegRegImmImm(dst, srcA, srcB, srcC, vecidx2, mdidx, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1a0intPipeRegRegRegRegImmImm extends Definition(
        "Hir0x1a0intPipeRegRegRegRegImmImm",
        "__HIR0X1A0int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("vecidx2", OperandKind.Immediate),
          Slot("mdidx", OperandKind.Immediate)
        ),
        Vec("genmetadata_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(vecidx2),
              InstructionOperand.Immediate(mdidx)
            ) =>
          Some(Instruction.Hir0x1a0intPipeRegRegRegRegImmImm(dst, srcA, srcB, srcC, vecidx2, mdidx, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1b0RegRegRegReg extends Definition(
        "Hir0x1b0RegRegRegReg",
        "__HIR0X1B0",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("gather_bitextract_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hir0x1b0RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1b0intPipeRegRegRegReg extends Definition(
        "Hir0x1b0intPipeRegRegRegReg",
        "__HIR0X1B0int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("gather_bitextract_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hir0x1b0intPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1e0NoOperands extends Definition("Hir0x1e0NoOperands", "__HIR0X1E0", Vec(), Vec("rtt_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x1e0NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1e0cbuPipeNoOperands
      extends Definition("Hir0x1e0cbuPipeNoOperands", "__HIR0X1E0cbu_pipe", Vec(), Vec("rtt_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x1e0cbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1f0ImmReg extends Definition(
        "Hir0x1f0ImmReg",
        "__HIR0X1F0",
        Vec(Slot("barname", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("r2b_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(barname), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Hir0x1f0ImmReg(barname, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1f0mioPipeImmReg extends Definition(
        "Hir0x1f0mioPipeImmReg",
        "__HIR0X1F0mio_pipe",
        Vec(Slot("barname", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("r2b_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(barname), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Hir0x1f0mioPipeImmReg(barname, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1f2Reg
      extends Definition("Hir0x1f2Reg", "__HIR0X1F2", Vec(Slot("dst", OperandKind.Register)), Vec("ucgabar_get_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.Hir0x1f2Reg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1f2udpPipeReg extends Definition(
        "Hir0x1f2udpPipeReg",
        "__HIR0X1F2udp_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("ucgabar_get_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.Hir0x1f2udpPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1f3Reg
      extends Definition("Hir0x1f3Reg", "__HIR0X1F3", Vec(Slot("srcB", OperandKind.Register)), Vec("ucgabar_set_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.Hir0x1f3Reg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1f3udpPipeReg extends Definition(
        "Hir0x1f3udpPipeReg",
        "__HIR0X1F3udp_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("ucgabar_set_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.Hir0x1f3udpPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    Vimnmx3RegRegImmRegPred,
    Vimnmx3RegRegRegRegPred,
    Vimnmx3intPipeRegRegAddressImmRegPred,
    Vimnmx3intPipeRegRegConstRegPred,
    Vimnmx3intPipeRegRegImmRegPred,
    Vimnmx3intPipeRegRegRegRegPred,
    VimnmxintPipeRegPredPredRegAddressImmPred,
    VimnmxintPipeRegPredPredRegConstPred,
    VimnmxintPipeRegPredPredRegImmPred,
    VimnmxintPipeRegPredPredRegRegPred,
    VimnmxintPipeRegRegAddressImmPred,
    VimnmxintPipeRegRegConstPred,
    VimnmxintPipeRegRegImmPred,
    VimnmxintPipeRegRegRegPred,
    VmadRegRegImmReg,
    VmadRegRegReg,
    VmadRegRegRegReg,
    VmadfmaxPipeRegRegReg,
    VmadfmaxPipeRegRegRegReg,
    VmadmioPipeRegRegImmReg,
    VmadmioPipeRegRegReg,
    VmadmioPipeRegRegRegReg,
    VmnmxRegRegImmReg,
    VmnmxRegRegRegReg,
    Vmnmx2RegRegImmReg,
    Vmnmx2RegRegRegReg,
    Vmnmx2xluPipeRegRegImmReg,
    Vmnmx2xluPipeRegRegRegReg,
    Vmnmx4RegRegImmReg,
    Vmnmx4RegRegRegReg,
    Vmnmx4xluPipeRegRegImmReg,
    Vmnmx4xluPipeRegRegRegReg,
    VmnmxfxuPipeRegRegImmReg,
    VmnmxfxuPipeRegRegRegReg,
    VmnmxxluPipeRegRegImmReg,
    VmnmxxluPipeRegRegRegReg,
    VoteImm,
    VotePredPred,
    VoteRegPredPred,
    VoteuRegPredPred,
    VoteuudpPipeRegPredPred,
    VoteVtgImm,
    VoteVtgPredImm,
    VoteVtgPredPredImmPred,
    VoteVtgfePipeImm,
    VoteVtgfePipePredImm,
    VoteVtgfePipePredPredImmPred,
    VotebarPipeImm,
    VotebarPipePredPred,
    VotebarPipeRegPredPred,
    VotefxuPipeImm,
    VotefxuPipePredPred,
    VotefxuPipeRegPredPred,
    VoteintPipeRegPredPred,
    Vsel2RegRegImmPred,
    Vsel2RegRegRegPred,
    Vsel2xluPipeRegRegImmPred,
    Vsel2xluPipeRegRegRegPred,
    Vsel4RegRegImmPred,
    Vsel4RegRegRegPred,
    Vsel4xluPipeRegRegImmPred,
    Vsel4xluPipeRegRegRegPred,
    VsetRegRegImmReg,
    VsetRegRegRegReg,
    Vset2RegRegImmReg,
    Vset2RegRegRegReg,
    Vset2xluPipeRegRegImmReg,
    Vset2xluPipeRegRegRegReg,
    Vset4RegRegImmReg,
    Vset4RegRegRegReg,
    Vset4xluPipeRegRegImmReg,
    Vset4xluPipeRegRegRegReg,
    VsetpPredPredRegImmPred,
    VsetpPredPredRegRegPred,
    VsetpPredRegImm,
    VsetpPredRegReg,
    VsetpfxuPipePredPredRegImmPred,
    VsetpfxuPipePredPredRegRegPred,
    VsetpfxuPipePredRegImm,
    VsetpfxuPipePredRegReg,
    VsetpxluPipePredPredRegImmPred,
    VsetpxluPipePredPredRegRegPred,
    VsetpxluPipePredRegImm,
    VsetpxluPipePredRegReg,
    VsetfxuPipeRegRegImmReg,
    VsetfxuPipeRegRegRegReg,
    VsetxluPipeRegRegImmReg,
    VsetxluPipeRegRegRegReg,
    VshlRegRegImmReg,
    VshlRegRegRegReg,
    VshlfxuPipeRegRegImmReg,
    VshlfxuPipeRegRegRegReg,
    VshlxluPipeRegRegImmReg,
    VshlxluPipeRegRegRegReg,
    VshrRegRegImmReg,
    VshrRegRegRegReg,
    VshrfxuPipeRegRegImmReg,
    VshrfxuPipeRegRegRegReg,
    VshrxluPipeRegRegImmReg,
    VshrxluPipeRegRegRegReg,
    WarpgroupNoOperands,
    WarpgroupImm,
    WarpgroupsetReg,
    WarpgroupsetmioPipeReg,
    WarpgroupmioPipeNoOperands,
    WarpgroupmioPipeImm,
    WarpsyncNoOperands,
    WarpsyncAddressImm,
    WarpsyncConst,
    WarpsyncImm,
    WarpsyncPred,
    WarpsyncPredAddressImm,
    WarpsyncPredConst,
    WarpsyncPredImm,
    WarpsyncPredReg,
    WarpsyncPredRegImm,
    WarpsyncReg,
    WarpsyncRegImm,
    WarpsynccbuPipeNoOperands,
    WarpsynccbuPipeAddressImm,
    WarpsynccbuPipeConst,
    WarpsynccbuPipeImm,
    WarpsynccbuPipePred,
    WarpsynccbuPipePredAddressImm,
    WarpsynccbuPipePredConst,
    WarpsynccbuPipePredImm,
    WarpsynccbuPipePredReg,
    WarpsynccbuPipePredRegImm,
    WarpsynccbuPipeReg,
    WarpsynccbuPipeRegImm,
    XmadRegRegConstReg,
    XmadRegRegImmReg,
    XmadRegRegRegConst,
    XmadRegRegRegReg,
    XmadfmaiPipeRegRegConstReg,
    XmadfmaiPipeRegRegImmReg,
    XmadfmaiPipeRegRegRegConst,
    XmadfmaiPipeRegRegRegReg,
    XmadfxuPipeRegRegConstReg,
    XmadfxuPipeRegRegImmReg,
    XmadfxuPipeRegRegRegConst,
    XmadfxuPipeRegRegRegReg,
    YieldNoOperands,
    YieldPred,
    YieldcbuPipeNoOperands,
    YieldcbuPipePred,
    Hir0x002NoOperands,
    Hir0x002Hir0x00aPipeNoOperands,
    Hir0x003NoOperands,
    Hir0x003Pred,
    Hir0x003Hir0x00aPipeNoOperands,
    Hir0x003Hir0x00aPipePred,
    Hir0x004NoOperands,
    Hir0x004Hir0x00aPipeNoOperands,
    Hir0x005PredRegRegImm,
    Hir0x005RegRegImm,
    Hir0x005Hir0x00aPipePredRegRegImm,
    Hir0x005Hir0x00aPipeRegRegImm,
    Hir0x006Imm,
    Hir0x006Hir0x00aPipeImm,
    Hir0x007NoOperands,
    Hir0x007Hir0x00aPipeNoOperands,
    Hir0x008ImmRegReg,
    Hir0x008Hir0x00aPipeImmRegReg,
    Hir0x020NoOperands,
    Hir0x020Imm,
    Hir0x020intPipeNoOperands,
    Hir0x020intPipeImm,
    Hir0x120AddressImm,
    Hir0x120Const,
    Hir0x120Imm,
    Hir0x120PredAddressImm,
    Hir0x120PredConst,
    Hir0x120PredImm,
    Hir0x120PredReg,
    Hir0x120Reg,
    Hir0x120cbuPipeAddressImm,
    Hir0x120cbuPipeConst,
    Hir0x120cbuPipeImm,
    Hir0x120cbuPipePredAddressImm,
    Hir0x120cbuPipePredConst,
    Hir0x120cbuPipePredImm,
    Hir0x120cbuPipePredReg,
    Hir0x120cbuPipeReg,
    Hir0x130AddressAddressAddressAddressAddressAddressPred,
    Hir0x130udpPipeAddressAddressAddressAddressAddressAddressPred,
    Hir0x150RegRegRegRegPredRegRegReg,
    Hir0x150RegRegRegRegRegRegReg,
    Hir0x150fp16PipeRegRegRegRegPredRegRegReg,
    Hir0x150fp16PipeRegRegRegRegRegRegReg,
    Hir0x160Reg,
    Hir0x160udpPipeReg,
    Hir0x170Reg,
    Hir0x170udpPipeReg,
    Hir0x1a0RegRegRegRegImmImm,
    Hir0x1a0intPipeRegRegRegRegImmImm,
    Hir0x1b0RegRegRegReg,
    Hir0x1b0intPipeRegRegRegReg,
    Hir0x1e0NoOperands,
    Hir0x1e0cbuPipeNoOperands,
    Hir0x1f0ImmReg,
    Hir0x1f0mioPipeImmReg,
    Hir0x1f2Reg,
    Hir0x1f2udpPipeReg,
    Hir0x1f3Reg,
    Hir0x1f3udpPipeReg
  )
}
