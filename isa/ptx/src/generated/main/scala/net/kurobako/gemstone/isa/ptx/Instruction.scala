// Generated PTX instruction model from the pinned NVIDIA ISA 9.3 specification. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.ptx

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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_2(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_3(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_4(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_5(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_7(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_9(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_10(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_11(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_12(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_13(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_14(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_15(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_16(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_17(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataAtom_143_18(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_0(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_1(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_2(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address
    ),
    Vec(
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_4(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address
    ),
    Vec(
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_6(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address
    ),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_10(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address
    ),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_12(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_13(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_14(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_15(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_16(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_17(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_18(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_19(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_20(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.Global), ModifierGroup(Modifier.SharedCta), ModifierGroup(Modifier.BulkGroup)),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_21(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Global), ModifierGroup(Modifier.SharedCta), ModifierGroup(Modifier.BulkGroup)),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_22(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Global), ModifierGroup(Modifier.SharedCta), ModifierGroup(Modifier.BulkGroup)),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_23(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_24(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.BulkGroup),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkMBarrier_107_25(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.BulkGroup),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_0(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_1(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_2(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address
    ),
    Vec(
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_4(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address
    ),
    Vec(
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_6(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address
    ),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_10(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address
    ),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.IgnoreOob),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_12(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_13(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_14(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_15(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_16(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_17(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.MulticastCluster),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_18(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_19(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_20(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.Global), ModifierGroup(Modifier.SharedCta), ModifierGroup(Modifier.BulkGroup)),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_21(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Global), ModifierGroup(Modifier.SharedCta), ModifierGroup(Modifier.BulkGroup)),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_22(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Global), ModifierGroup(Modifier.SharedCta), ModifierGroup(Modifier.BulkGroup)),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask, Modifier.Weak),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_23(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_24(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.BulkGroup),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkGroup_107_25(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Relaxed),
      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.BulkGroup),
      ModifierGroup(Modifier.B128)
    ),
    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint, Modifier.CpMask),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorMBarrier_112_0(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorMBarrier_112_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorMBarrier_112_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorMBarrier_112_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.MulticastCluster,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorMBarrier_112_4(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.MulticastCluster,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorMBarrier_112_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.MulticastCluster,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorMBarrier_112_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.MulticastCluster,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorMBarrier_112_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorMBarrier_112_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorGroup_112_0(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorGroup_112_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorGroup_112_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCta),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorGroup_112_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.MulticastCluster,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorGroup_112_4(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.MulticastCluster,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorGroup_112_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.MulticastCluster,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorGroup_112_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
      ModifierGroup(Modifier.SharedCluster),
      ModifierGroup(Modifier.Global),
      ModifierGroup(Modifier.MbarrierCompleteTxBytes)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.CtaGroup1,
      Modifier.CtaGroup2,
      Modifier.Im2col,
      Modifier.Im2colW,
      Modifier.Im2colW128,
      Modifier.MulticastCluster,
      Modifier.Tile,
      Modifier.TileGather4
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorGroup_112_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCpAsyncBulkTensorGroup_112_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn, Modifier.Rz), ModifierGroup(Modifier.F16), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn, Modifier.Rz), ModifierGroup(Modifier.F16x2), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_4(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rs), ModifierGroup(Modifier.F16x2), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn, Modifier.Rz), ModifierGroup(Modifier.Bf16), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn, Modifier.Rz), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rs), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rna), ModifierGroup(Modifier.Tf32), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_9(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn, Modifier.Rz), ModifierGroup(Modifier.Tf32), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_12(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.F16x2), ModifierGroup(Modifier.E4m3x2, Modifier.E5m2x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_13(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.E4m3x2, Modifier.E5m2x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_14(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.E4m3x2, Modifier.E5m2x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_15(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rs),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E4m3x4, Modifier.E5m2x4),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_16(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m1x2),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_17(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m1x2),
      ModifierGroup(Modifier.Bf16x2, Modifier.F16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_18(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.F16x2), ModifierGroup(Modifier.E2m1x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_19(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.E2m1x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_20(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.E2m1x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_21(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rs),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m1x4),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_22(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m3x2, Modifier.E3m2x2),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_23(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m3x2, Modifier.E3m2x2),
      ModifierGroup(Modifier.Bf16x2, Modifier.F16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_24(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.F16x2), ModifierGroup(Modifier.E2m3x2, Modifier.E3m2x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_25(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.E2m3x2, Modifier.E3m2x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_26(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.E2m3x2, Modifier.E3m2x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_27(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.VectorOperand, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rs),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.E2m3x4, Modifier.E3m2x4),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_28(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.Ue8m0x2), ModifierGroup(Modifier.F32)),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_29(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.Ue8m0x2), ModifierGroup(Modifier.Bf16x2)),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_30(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.Ue8m0x2)),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_31(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.S2f6x2),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_32(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.S2f6x2),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_33(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.S2f6x2),
      ModifierGroup(Modifier.Bf16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_34(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Rn),
      ModifierGroup(Modifier.Satfinite),
      ModifierGroup(Modifier.S2f6x2),
      ModifierGroup(Modifier.Bf16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_35(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.S2f6x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataCvt_100_36(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2), ModifierGroup(Modifier.S2f6x2)),
    _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.Satfinite, Modifier.ScaledN2Ue8m0),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLd_86_0(): InstructionForm = InstructionForm(
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.Ca,
      Modifier.Cg,
      Modifier.Const,
      Modifier.Cs,
      Modifier.Cv,
      Modifier.Global,
      Modifier.Local,
      Modifier.Lu,
      Modifier.Param,
      Modifier.ParamEntry,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8,
      Modifier.Weak
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLd_86_1(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.Ca,
      Modifier.Cg,
      Modifier.Const,
      Modifier.Cs,
      Modifier.Cv,
      Modifier.Global,
      Modifier.Local,
      Modifier.Lu,
      Modifier.Param,
      Modifier.ParamEntry,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8,
      Modifier.Weak
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLd_86_2(): InstructionForm = InstructionForm(
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.L2EvictFirst,
      Modifier.L2EvictLast,
      Modifier.L2EvictNormal,
      Modifier.Const,
      Modifier.Global,
      Modifier.Local,
      Modifier.Param,
      Modifier.ParamEntry,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8,
      Modifier.Weak
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLd_86_3(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(
      Modifier.L1EvictFirst,
      Modifier.L1EvictLast,
      Modifier.L1EvictNormal,
      Modifier.L1EvictUnchanged,
      Modifier.L1NoAllocate,
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.L2EvictFirst,
      Modifier.L2EvictLast,
      Modifier.L2EvictNormal,
      Modifier.Const,
      Modifier.Global,
      Modifier.Local,
      Modifier.Param,
      Modifier.ParamEntry,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8,
      Modifier.Weak
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLd_86_4(): InstructionForm = InstructionForm(
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.Const,
      Modifier.Global,
      Modifier.Local,
      Modifier.Param,
      Modifier.ParamEntry,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLd_86_5(): InstructionForm = InstructionForm(
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.L2EvictFirst,
      Modifier.L2EvictLast,
      Modifier.L2EvictNormal,
      Modifier.Const,
      Modifier.Global,
      Modifier.Local,
      Modifier.Param,
      Modifier.ParamEntry,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLd_86_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.L2EvictFirst,
      Modifier.L2EvictLast,
      Modifier.L2EvictNormal,
      Modifier.Const,
      Modifier.Global,
      Modifier.Local,
      Modifier.Param,
      Modifier.ParamEntry,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLd_86_7(): InstructionForm = InstructionForm(
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.L2EvictFirst,
      Modifier.L2EvictLast,
      Modifier.L2EvictNormal,
      Modifier.Const,
      Modifier.Global,
      Modifier.Local,
      Modifier.Param,
      Modifier.ParamEntry,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLd_86_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.L2EvictFirst,
      Modifier.L2EvictLast,
      Modifier.L2EvictNormal,
      Modifier.Const,
      Modifier.Global,
      Modifier.Local,
      Modifier.Param,
      Modifier.ParamEntry,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLd_86_9(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLdGlobalNc_87_0(): InstructionForm = InstructionForm(
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.Ca,
      Modifier.Cg,
      Modifier.Cs
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLdGlobalNc_87_1(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.Ca,
      Modifier.Cg,
      Modifier.Cs
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLdGlobalNc_87_2(): InstructionForm = InstructionForm(
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.Ca,
      Modifier.Cg,
      Modifier.Cs
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLdGlobalNc_87_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.Ca,
      Modifier.Cg,
      Modifier.Cs
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLdGlobalNc_87_4(): InstructionForm = InstructionForm(
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.L2EvictFirst,
      Modifier.L2EvictLast,
      Modifier.L2EvictNormal
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLdGlobalNc_87_5(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(
      Modifier.L1EvictFirst,
      Modifier.L1EvictLast,
      Modifier.L1EvictNormal,
      Modifier.L1EvictUnchanged,
      Modifier.L1NoAllocate,
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.L2EvictFirst,
      Modifier.L2EvictLast,
      Modifier.L2EvictNormal
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLdGlobalNc_87_6(): InstructionForm = InstructionForm(
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.L2EvictFirst,
      Modifier.L2EvictLast,
      Modifier.L2EvictNormal
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataLdGlobalNc_87_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
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
      Modifier.L2128B,
      Modifier.L2256B,
      Modifier.L264B,
      Modifier.L2CacheHint,
      Modifier.L2EvictFirst,
      Modifier.L2EvictLast,
      Modifier.L2EvictNormal
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_0(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.PhaseTypePrimary,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.PhaseTypePrimary,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_2(): InstructionForm = InstructionForm(
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
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.PhaseTypePrimary), ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_4(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.PhaseTypePrimary), ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.PhaseTypePrimary), ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Parity), ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.PhaseTypeConditional,
      Modifier.PhaseTypePrimary,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Parity), ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.PhaseTypeConditional,
      Modifier.PhaseTypePrimary,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.Parity), ModifierGroup(Modifier.PhaseTypePrimary), ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_9(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Parity), ModifierGroup(Modifier.PhaseTypePrimary), ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_10(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Parity), ModifierGroup(Modifier.PhaseTypePrimary), ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMbarrierTryWait_161_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Parity), ModifierGroup(Modifier.PhaseTypePrimary), ModifierGroup(Modifier.B64)),
    _root_.scala.collection.immutable.Set(
      Modifier.Acquire,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Relaxed,
      Modifier.Shared,
      Modifier.SharedCta
    ),
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMma_170_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k64),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindMxf4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Ue8m0)
    ),
    _root_.scala.collection.immutable.Set(Modifier.ScaleVec2X),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMma_170_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k64),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindMxf4nvf4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.ScaleVec2X, Modifier.ScaleVec4X),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Ue4m3, Modifier.Ue8m0)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMma_170_10(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand
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
    _root_.scala.collection.immutable.Set(Modifier.ScaleVec1X),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMma_170_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k16, Modifier.M16n8k4, Modifier.M16n8k8, Modifier.M8n8k4),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.F64),
      ModifierGroup(Modifier.F64),
      ModifierGroup(Modifier.F64),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMma_170_12(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k16, Modifier.M16n8k32, Modifier.M8n8k16),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.S8, Modifier.U8),
      ModifierGroup(Modifier.S8, Modifier.U8),
      ModifierGroup(Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMma_170_13(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMma_170_14(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
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
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k128),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindMxf4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Ue8m0)
    ),
    _root_.scala.collection.immutable.Set(Modifier.ScaleVec2X),
    _root_.scala.collection.immutable.Set(),
    true
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
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand
    ),
    Vec(
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.M16n8k128),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.KindMxf4nvf4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.ScaleVec2X, Modifier.ScaleVec4X),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.E2m1),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Ue4m3, Modifier.Ue8m0)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
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
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand
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
    _root_.scala.collection.immutable.Set(Modifier.ScaleVec1X),
    _root_.scala.collection.immutable.Set(),
    true
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
      ModifierGroup(Modifier.M16n8k32, Modifier.M16n8k64),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.S32),
      ModifierGroup(Modifier.S8, Modifier.U8),
      ModifierGroup(Modifier.S8, Modifier.U8),
      ModifierGroup(Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Satfinite),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataMmaSpOrderedMetadata_174_12(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_0(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_2(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.F16)),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_4(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.F16x2)),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_6(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.Bf16)),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_8(): InstructionForm = InstructionForm(
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
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_9(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.Noftz), ModifierGroup(Modifier.Bf16x2)),
    _root_.scala.collection.immutable.Set(
      Modifier.L2CacheHint,
      Modifier.Cluster,
      Modifier.Cta,
      Modifier.Global,
      Modifier.Gpu,
      Modifier.Relaxed,
      Modifier.Release,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.Sys
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_10(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.V2, Modifier.V4), ModifierGroup(Modifier.F32)),
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
    Vec(ModifierGroup(Modifier.Add), ModifierGroup(Modifier.V2, Modifier.V4), ModifierGroup(Modifier.F32)),
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_12(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataRed_144_13(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Value, OperandConstraint.Value),
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
    _root_.scala.collection.immutable.Set(),
    false
  )
  private def metadataRed_144_14(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataRed_144_15(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSet_70_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
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
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSet_70_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
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
    _root_.scala.collection.immutable.Set(),
    true
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
      ModifierGroup(Modifier.S16, Modifier.S32, Modifier.U16, Modifier.U32),
      ModifierGroup(Modifier.F16)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ftz),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSet_70_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
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
      ModifierGroup(Modifier.S16, Modifier.S32, Modifier.U16, Modifier.U32),
      ModifierGroup(Modifier.F16)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ftz),
    _root_.scala.collection.immutable.Set(),
    true
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
      ModifierGroup(Modifier.S16, Modifier.S32, Modifier.U16, Modifier.U32),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSet_70_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
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
      ModifierGroup(Modifier.S16, Modifier.S32, Modifier.U16, Modifier.U32),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
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
      ModifierGroup(Modifier.F16x2, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ftz),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSet_70_9(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
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
      ModifierGroup(Modifier.F16x2, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F16x2)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ftz),
    _root_.scala.collection.immutable.Set(),
    true
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSet_70_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSt_89_0(): InstructionForm = InstructionForm(
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
      Modifier.Param,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8,
      Modifier.Wb,
      Modifier.Weak,
      Modifier.Wt
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSt_89_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Param,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8,
      Modifier.Wb,
      Modifier.Weak,
      Modifier.Wt
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSt_89_2(): InstructionForm = InstructionForm(
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
      Modifier.Param,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8,
      Modifier.Weak
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSt_89_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Param,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8,
      Modifier.Weak
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSt_89_4(): InstructionForm = InstructionForm(
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
      Modifier.Param,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSt_89_5(): InstructionForm = InstructionForm(
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
      Modifier.Param,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSt_89_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Param,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSt_89_7(): InstructionForm = InstructionForm(
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
      Modifier.Param,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSt_89_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Address, OperandConstraint.Value),
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
      Modifier.Param,
      Modifier.ParamFunc,
      Modifier.Shared,
      Modifier.SharedCluster,
      Modifier.SharedCta,
      Modifier.V2,
      Modifier.V4,
      Modifier.V8
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataSt_89_9(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_0(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_1(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_2(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_4(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.Block16,
      Modifier.Block32,
      Modifier.ScaleVec1X,
      Modifier.ScaleVec2X,
      Modifier.ScaleVec4X
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_5(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.Block16,
      Modifier.Block32,
      Modifier.ScaleVec1X,
      Modifier.ScaleVec2X,
      Modifier.ScaleVec4X
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_6(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ashift),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ashift),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_10(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.Ashift)
    ),
    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_11(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.Ashift)
    ),
    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_12(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.Block16,
      Modifier.Block32,
      Modifier.ScaleVec1X,
      Modifier.ScaleVec2X,
      Modifier.ScaleVec4X
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_13(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.Block16,
      Modifier.Block32,
      Modifier.ScaleVec1X,
      Modifier.ScaleVec2X,
      Modifier.ScaleVec4X
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_14(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2), ModifierGroup(Modifier.KindI8)),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_15(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2), ModifierGroup(Modifier.KindI8)),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_16(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindI8),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_17(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindI8),
      ModifierGroup(Modifier.Ashift)
    ),
    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05Mma_186_18(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindI8),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ashift),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_0(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_1(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_2(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_3(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_4(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.Block16,
      Modifier.Block32,
      Modifier.ScaleVec1X,
      Modifier.ScaleVec2X,
      Modifier.ScaleVec4X
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_5(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.Block16,
      Modifier.Block32,
      Modifier.ScaleVec1X,
      Modifier.ScaleVec2X,
      Modifier.ScaleVec4X
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_6(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_7(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_8(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.Ashift)
    ),
    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.Ashift)
    ),
    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_10(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ashift),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_11(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ashift),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_12(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.Block16,
      Modifier.Block32,
      Modifier.ScaleVec1X,
      Modifier.ScaleVec2X,
      Modifier.ScaleVec4X
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_13(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindMxf4, Modifier.KindMxf4nvf4, Modifier.KindMxf8f6f4),
      ModifierGroup(Modifier.BlockScale),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(
      Modifier.Block16,
      Modifier.Block32,
      Modifier.ScaleVec1X,
      Modifier.ScaleVec2X,
      Modifier.ScaleVec4X
    ),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_14(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2), ModifierGroup(Modifier.KindI8)),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_15(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2), ModifierGroup(Modifier.KindI8)),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_16(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindI8),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_17(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindI8),
      ModifierGroup(Modifier.Ashift)
    ),
    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTcgen05MmaSp_187_18(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Address,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.VectorOperand,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
      ModifierGroup(Modifier.KindI8),
      ModifierGroup(Modifier.CollectorBufferOp)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Ashift),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_0(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_4(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Base),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Base),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.Base),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_9(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Level),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_10(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Level),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Level),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_12(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Grad),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_13(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Grad),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_14(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Grad),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V4),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32, Modifier.U32),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_15(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Base),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_16(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Base),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_17(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.Base),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_18(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Level),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_19(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Level),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_20(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Level),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_21(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Grad),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_22(): InstructionForm = InstructionForm(
    Vec(
      OperandConstraint.Register,
      OperandConstraint.Address,
      OperandConstraint.Value,
      OperandConstraint.Value,
      OperandConstraint.Value
    ),
    Vec(
      ModifierGroup(Modifier.Grad),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataTex_124_23(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.Grad),
      ModifierGroup(
        Modifier.N1d,
        Modifier.N2d,
        Modifier.N2dms,
        Modifier.N3d,
        Modifier.A1d,
        Modifier.A2d,
        Modifier.A2dms,
        Modifier.Acube,
        Modifier.Cube
      ),
      ModifierGroup(Modifier.V2),
      ModifierGroup(Modifier.F16x2),
      ModifierGroup(Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
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
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
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
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
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
        Modifier.M64n104k8,
        Modifier.M64n112k8,
        Modifier.M64n120k8,
        Modifier.M64n128k8,
        Modifier.M64n136k8,
        Modifier.M64n144k8,
        Modifier.M64n152k8,
        Modifier.M64n160k8,
        Modifier.M64n168k8,
        Modifier.M64n16k8,
        Modifier.M64n176k8,
        Modifier.M64n184k8,
        Modifier.M64n192k8,
        Modifier.M64n200k8,
        Modifier.M64n208k8,
        Modifier.M64n216k8,
        Modifier.M64n224k8,
        Modifier.M64n232k8,
        Modifier.M64n240k8,
        Modifier.M64n248k8,
        Modifier.M64n24k8,
        Modifier.M64n256k8,
        Modifier.M64n32k8,
        Modifier.M64n40k8,
        Modifier.M64n48k8,
        Modifier.M64n56k8,
        Modifier.M64n64k8,
        Modifier.M64n72k8,
        Modifier.M64n80k8,
        Modifier.M64n88k8,
        Modifier.M64n8k8,
        Modifier.M64n96k8
      ),
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(),
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
      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2)
    ),
    _root_.scala.collection.immutable.Set(),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_6(): InstructionForm = InstructionForm(
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
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWgmmaMmaAsync_175_7(): InstructionForm = InstructionForm(
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
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
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
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Bf16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(),
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
      ModifierGroup(Modifier.F32),
      ModifierGroup(Modifier.Tf32),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(),
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
      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
      ModifierGroup(Modifier.E4m3, Modifier.E5m2)
    ),
    _root_.scala.collection.immutable.Set(),
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
    _root_.scala.collection.immutable.Set(),
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
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_1(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F16, Modifier.S8, Modifier.U8)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_2(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F16, Modifier.S8, Modifier.U8)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_3(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F16, Modifier.S8, Modifier.U8)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_4(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_5(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_6(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_7(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_8(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_9(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.Bf16)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_10(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_11(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_12(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k8),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_13(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k8),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_14(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k8),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_15(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k8),
      ModifierGroup(Modifier.Tf32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_16(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k8),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_17(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M16n16k8),
      ModifierGroup(Modifier.F32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_18(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k4),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_19(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k4),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_20(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k4),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_21(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k4),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_22(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k4),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_23(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k4),
      ModifierGroup(Modifier.F64)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_24(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.M8n8k32),
      ModifierGroup(Modifier.S4, Modifier.U4)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_25(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.M8n8k32),
      ModifierGroup(Modifier.S4, Modifier.U4)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_26(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.M8n8k32),
      ModifierGroup(Modifier.S4, Modifier.U4)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_27(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.M8n8k32),
      ModifierGroup(Modifier.S4, Modifier.U4)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_28(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k32),
      ModifierGroup(Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_29(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k32),
      ModifierGroup(Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_30(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.M8n8k128),
      ModifierGroup(Modifier.B1)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_31(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.A),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Row),
      ModifierGroup(Modifier.M8n8k128),
      ModifierGroup(Modifier.B1)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_32(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.M8n8k128),
      ModifierGroup(Modifier.B1)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_33(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.B),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col),
      ModifierGroup(Modifier.M8n8k128),
      ModifierGroup(Modifier.B1)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_34(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k128),
      ModifierGroup(Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
  )
  private def metadataWmmaLoad_167_35(): InstructionForm = InstructionForm(
    Vec(OperandConstraint.Register, OperandConstraint.Address),
    Vec(
      ModifierGroup(Modifier.C),
      ModifierGroup(Modifier.Sync),
      ModifierGroup(Modifier.Aligned),
      ModifierGroup(Modifier.Col, Modifier.Row),
      ModifierGroup(Modifier.M8n8k128),
      ModifierGroup(Modifier.S32)
    ),
    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
    _root_.scala.collection.immutable.Set(),
    true
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S16, Modifier.S32, Modifier.S64)),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(_root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13)),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(6, 5),
                Target(53),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2) -> Target(80)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(6, 2),
                Target(30),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register),
                  Vec(ModifierGroup(Modifier.B32)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.S16x2) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.U16x2) -> Target(90)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S16, Modifier.S64, Modifier.U16, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.F32) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F32x2) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13),
                  _root_.scala.collection.immutable.Set(
                    Modifier.F64,
                    Modifier.Rm,
                    Modifier.Rn,
                    Modifier.Rp,
                    Modifier.Rz
                  ) -> Target(13)
                ),
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(4, 2),
                Target(53),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2) -> Target(90)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(100),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.F32) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.F16, Modifier.F32) -> Target(100)
                ),
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
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(20),
                Map(_root_.scala.collection.immutable.Set(Modifier.Cc) -> Target(20)),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(Modifier.Cc),
                  _root_.scala.collection.immutable.Set(),
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
                Version(7, 3),
                Target(52),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.Pred)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(7, 4),
                Target(80),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.L2EvictNormal)),
                  _root_.scala.collection.immutable.Set(Modifier.Global),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(11),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.Bf16, Modifier.Noftz) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.Bf16x2, Modifier.Noftz) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.F16, Modifier.Noftz) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.F16x2, Modifier.Noftz) -> Target(60),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.F32) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.F64) -> Target(60),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.Global) -> Target(12),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.Shared) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.And) -> Target(32),
                  _root_.scala.collection.immutable.Set(Modifier.B128) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.B16, Modifier.Cas) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.Cas, Modifier.Global) -> Target(12),
                  _root_.scala.collection.immutable.Set(Modifier.Cas, Modifier.Shared) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.Cluster) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Exch, Modifier.Global) -> Target(12),
                  _root_.scala.collection.immutable.Set(Modifier.Exch, Modifier.Shared) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.Global) -> Target(11),
                  _root_.scala.collection.immutable.Set(Modifier.Max) -> Target(32),
                  _root_.scala.collection.immutable.Set(Modifier.Min) -> Target(32),
                  _root_.scala.collection.immutable.Set(Modifier.Or) -> Target(32),
                  _root_.scala.collection.immutable.Set(Modifier.Shared) -> Target(12),
                  _root_.scala.collection.immutable.Set(Modifier.Xor) -> Target(32)
                ),
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
                  metadataAtom_143_9(),
                  metadataAtom_143_10(),
                  metadataAtom_143_11(),
                  metadataAtom_143_12(),
                  metadataAtom_143_13(),
                  metadataAtom_143_14(),
                  metadataAtom_143_15(),
                  metadataAtom_143_16(),
                  metadataAtom_143_17(),
                  metadataAtom_143_18()
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
                Version(1, 0),
                Target(20),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Arrive, Modifier.Cta) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.Cta) -> Target(30),
                  _root_.scala.collection.immutable.Set(Modifier.Cta, Modifier.Red) -> Target(20)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Sync)),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Sync)),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Arrive)),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
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
                    Vec(ModifierGroup(Modifier.Red), ModifierGroup(Modifier.Popc), ModifierGroup(Modifier.U32)),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Red), ModifierGroup(Modifier.Popc), ModifierGroup(Modifier.U32)),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
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
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.And, Modifier.Or),
                      ModifierGroup(Modifier.Pred)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.And, Modifier.Or),
                      ModifierGroup(Modifier.Pred)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Cta),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(6, 0),
                Target(30),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(1, 0),
                Target(20),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Arrive, Modifier.Cta) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.Cta) -> Target(30),
                  _root_.scala.collection.immutable.Set(Modifier.Cta, Modifier.Red) -> Target(20)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Sync)),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Sync)),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Arrive)),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
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
                    Vec(ModifierGroup(Modifier.Red), ModifierGroup(Modifier.Popc), ModifierGroup(Modifier.U32)),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Red), ModifierGroup(Modifier.Popc), ModifierGroup(Modifier.U32)),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
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
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.And, Modifier.Or),
                      ModifierGroup(Modifier.Pred)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.And, Modifier.Or),
                      ModifierGroup(Modifier.Pred)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Cta),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(7, 8),
                Target(90),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Arrive)),
                    _root_.scala.collection.immutable.Set(Modifier.Aligned, Modifier.Relaxed, Modifier.Release),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Wait)),
                    _root_.scala.collection.immutable.Set(Modifier.Acquire, Modifier.Aligned),
                    _root_.scala.collection.immutable.Set(),
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
                Version(2, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(2, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(2, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
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
                Version(7, 6),
                Target(70),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Clamp, Modifier.Wrap), ModifierGroup(Modifier.B32)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.Uni),
                  _root_.scala.collection.immutable.Set(),
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
                Version(2, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(11),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(6, 0),
                Target(30),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.Uni),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Symbol, OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Symbol, OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Symbol),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(Modifier.Uni),
                    _root_.scala.collection.immutable.Set(),
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
                Version(9, 3),
                Target(80),
                Map(),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.Hi, Modifier.Lo), ModifierGroup(Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
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
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.ClusterLaunchControlQueryCancel(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount(
              "clusterlaunchcontrol.query_cancel",
              Vec(2),
              operands.size
            ))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(8, 6),
                Target(100),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.IsCanceled), ModifierGroup(Modifier.Pred), ModifierGroup(Modifier.B128)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.VectorOperand, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.GetFirstCtaid),
                      ModifierGroup(Modifier.V4),
                      ModifierGroup(Modifier.B32),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        Modifier.GetFirstCtaid,
                        Modifier.GetFirstCtaidX,
                        Modifier.GetFirstCtaidY,
                        Modifier.GetFirstCtaidZ
                      ),
                      ModifierGroup(Modifier.B32),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Address),
                  Vec(
                    ModifierGroup(Modifier.Async),
                    ModifierGroup(Modifier.MbarrierCompleteTxBytes),
                    ModifierGroup(Modifier.B128)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.MulticastClusterAll, Modifier.SharedCta),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(2, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(2, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.F32, Modifier.F64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                  _root_.scala.collection.immutable.Set(Modifier.Ftz),
                  _root_.scala.collection.immutable.Set(),
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
                Version(7, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.Ca),
                      ModifierGroup(Modifier.Shared, Modifier.SharedCta),
                      ModifierGroup(Modifier.Global)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2128B,
                      Modifier.L2256B,
                      Modifier.L264B,
                      Modifier.L2CacheHint
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.Ca),
                      ModifierGroup(Modifier.Shared, Modifier.SharedCta),
                      ModifierGroup(Modifier.Global)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2128B,
                      Modifier.L2256B,
                      Modifier.L264B,
                      Modifier.L2CacheHint
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Ca),
                      ModifierGroup(Modifier.Shared, Modifier.SharedCta),
                      ModifierGroup(Modifier.Global)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2128B,
                      Modifier.L2256B,
                      Modifier.L264B,
                      Modifier.L2CacheHint
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                      ModifierGroup(Modifier.Cg),
                      ModifierGroup(Modifier.Shared, Modifier.SharedCta),
                      ModifierGroup(Modifier.Global)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2128B,
                      Modifier.L2256B,
                      Modifier.L264B,
                      Modifier.L2CacheHint
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.Cg),
                      ModifierGroup(Modifier.Shared, Modifier.SharedCta),
                      ModifierGroup(Modifier.Global)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2128B,
                      Modifier.L2256B,
                      Modifier.L264B,
                      Modifier.L2CacheHint
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Cg),
                      ModifierGroup(Modifier.Shared, Modifier.SharedCta),
                      ModifierGroup(Modifier.Global)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2128B,
                      Modifier.L2256B,
                      Modifier.L264B,
                      Modifier.L2CacheHint
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(8, 0),
                Target(90),
                Map(_root_.scala.collection.immutable.Set(Modifier.CpMask) -> Target(100)),
                Vec(
                  metadataCpAsyncBulkMBarrier_107_0(),
                  metadataCpAsyncBulkMBarrier_107_1(),
                  metadataCpAsyncBulkMBarrier_107_2(),
                  metadataCpAsyncBulkMBarrier_107_3(),
                  metadataCpAsyncBulkMBarrier_107_4(),
                  metadataCpAsyncBulkMBarrier_107_5(),
                  metadataCpAsyncBulkMBarrier_107_6(),
                  metadataCpAsyncBulkMBarrier_107_7(),
                  metadataCpAsyncBulkMBarrier_107_8(),
                  metadataCpAsyncBulkMBarrier_107_9(),
                  metadataCpAsyncBulkMBarrier_107_10(),
                  metadataCpAsyncBulkMBarrier_107_11(),
                  metadataCpAsyncBulkMBarrier_107_12(),
                  metadataCpAsyncBulkMBarrier_107_13(),
                  metadataCpAsyncBulkMBarrier_107_14(),
                  metadataCpAsyncBulkMBarrier_107_15(),
                  metadataCpAsyncBulkMBarrier_107_16(),
                  metadataCpAsyncBulkMBarrier_107_17(),
                  metadataCpAsyncBulkMBarrier_107_18(),
                  metadataCpAsyncBulkMBarrier_107_19(),
                  metadataCpAsyncBulkMBarrier_107_20(),
                  metadataCpAsyncBulkMBarrier_107_21(),
                  metadataCpAsyncBulkMBarrier_107_22(),
                  metadataCpAsyncBulkMBarrier_107_23(),
                  metadataCpAsyncBulkMBarrier_107_24(),
                  metadataCpAsyncBulkMBarrier_107_25()
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
                Version(8, 0),
                Target(90),
                Map(_root_.scala.collection.immutable.Set(Modifier.CpMask) -> Target(100)),
                Vec(
                  metadataCpAsyncBulkGroup_107_0(),
                  metadataCpAsyncBulkGroup_107_1(),
                  metadataCpAsyncBulkGroup_107_2(),
                  metadataCpAsyncBulkGroup_107_3(),
                  metadataCpAsyncBulkGroup_107_4(),
                  metadataCpAsyncBulkGroup_107_5(),
                  metadataCpAsyncBulkGroup_107_6(),
                  metadataCpAsyncBulkGroup_107_7(),
                  metadataCpAsyncBulkGroup_107_8(),
                  metadataCpAsyncBulkGroup_107_9(),
                  metadataCpAsyncBulkGroup_107_10(),
                  metadataCpAsyncBulkGroup_107_11(),
                  metadataCpAsyncBulkGroup_107_12(),
                  metadataCpAsyncBulkGroup_107_13(),
                  metadataCpAsyncBulkGroup_107_14(),
                  metadataCpAsyncBulkGroup_107_15(),
                  metadataCpAsyncBulkGroup_107_16(),
                  metadataCpAsyncBulkGroup_107_17(),
                  metadataCpAsyncBulkGroup_107_18(),
                  metadataCpAsyncBulkGroup_107_19(),
                  metadataCpAsyncBulkGroup_107_20(),
                  metadataCpAsyncBulkGroup_107_21(),
                  metadataCpAsyncBulkGroup_107_22(),
                  metadataCpAsyncBulkGroup_107_23(),
                  metadataCpAsyncBulkGroup_107_24(),
                  metadataCpAsyncBulkGroup_107_25()
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
                Version(8, 0),
                Target(90),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(8, 0),
                Target(90),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.L2), ModifierGroup(Modifier.Global)),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.L2), ModifierGroup(Modifier.Global)),
                    _root_.scala.collection.immutable.Set(Modifier.L2CacheHint),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
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
                Version(8, 0),
                Target(90),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
                      ModifierGroup(Modifier.L2),
                      ModifierGroup(Modifier.Global)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Im2col,
                      Modifier.Im2colW,
                      Modifier.Im2colW128,
                      Modifier.Tile,
                      Modifier.TileGather4
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
                      ModifierGroup(Modifier.L2),
                      ModifierGroup(Modifier.Global)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Im2col,
                      Modifier.Im2colW,
                      Modifier.Im2colW128,
                      Modifier.Tile,
                      Modifier.TileGather4
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
                      ModifierGroup(Modifier.L2),
                      ModifierGroup(Modifier.Global)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.L2CacheHint,
                      Modifier.Im2col,
                      Modifier.Im2colW,
                      Modifier.Im2colW128,
                      Modifier.Tile,
                      Modifier.TileGather4
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
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
                Version(8, 0),
                Target(90),
                Map(_root_.scala.collection.immutable.Set(Modifier.SharedCluster) -> Target(100)),
                Vec(
                  metadataCpAsyncBulkTensorMBarrier_112_0(),
                  metadataCpAsyncBulkTensorMBarrier_112_1(),
                  metadataCpAsyncBulkTensorMBarrier_112_2(),
                  metadataCpAsyncBulkTensorMBarrier_112_3(),
                  metadataCpAsyncBulkTensorMBarrier_112_4(),
                  metadataCpAsyncBulkTensorMBarrier_112_5(),
                  metadataCpAsyncBulkTensorMBarrier_112_6(),
                  metadataCpAsyncBulkTensorMBarrier_112_7(),
                  metadataCpAsyncBulkTensorMBarrier_112_8()
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
                Version(8, 0),
                Target(90),
                Map(_root_.scala.collection.immutable.Set(Modifier.SharedCluster) -> Target(100)),
                Vec(
                  metadataCpAsyncBulkTensorGroup_112_0(),
                  metadataCpAsyncBulkTensorGroup_112_1(),
                  metadataCpAsyncBulkTensorGroup_112_2(),
                  metadataCpAsyncBulkTensorGroup_112_3(),
                  metadataCpAsyncBulkTensorGroup_112_4(),
                  metadataCpAsyncBulkTensorGroup_112_5(),
                  metadataCpAsyncBulkTensorGroup_112_6(),
                  metadataCpAsyncBulkTensorGroup_112_7(),
                  metadataCpAsyncBulkTensorGroup_112_8()
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
                Version(8, 0),
                Target(90),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.Read),
                  _root_.scala.collection.immutable.Set(),
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
                Version(7, 0),
                Target(80),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(7, 0),
                Target(80),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.Noinc, Modifier.Shared, Modifier.SharedCta),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(7, 0),
                Target(80),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(7, 0),
                Target(80),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(8, 0),
                Target(90),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Address
                    ),
                    Vec(
                      ModifierGroup(Modifier.SharedCluster),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
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
                      ModifierGroup(Modifier.B32, Modifier.B64, Modifier.S32, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Cluster, Modifier.Cta, Modifier.Relaxed),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.Bf16,
                        Modifier.F16,
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
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
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
                      ModifierGroup(
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.Bf16,
                        Modifier.F16,
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
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                    _root_.scala.collection.immutable.Set(),
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
                      Modifier.L2CacheHint,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                Version(8, 0),
                Target(90),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
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
                    _root_.scala.collection.immutable.Set(
                      Modifier.Im2colNoOffs,
                      Modifier.LevelCacheHint,
                      Modifier.Tile
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.N4d, Modifier.N5d),
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
                    _root_.scala.collection.immutable.Set(
                      Modifier.Im2colNoOffs,
                      Modifier.LevelCacheHint,
                      Modifier.Tile
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
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
                Version(7, 4),
                Target(80),
                Map(),
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
                    _root_.scala.collection.immutable.Set(),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Cvt), ModifierGroup(Modifier.L2), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(),
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
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3 || operands.size == 4))
              Right(Instruction.Cvt(operands(0), operands(1), operands.lift(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("cvt", Vec(2, 3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.F16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.F32) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.F32, Modifier.Ftz) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.F64) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.S16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.S32) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.S64) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.S8) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.U16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.U32) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.U64) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.U8) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2) -> Target(80),
                  _root_.scala.collection.immutable.Set(
                    Modifier.E4m3x2,
                    Modifier.F16x2,
                    Modifier.Satfinite
                  ) -> Target(89),
                  _root_.scala.collection.immutable.Set(
                    Modifier.E4m3x2,
                    Modifier.F32,
                    Modifier.Satfinite
                  ) -> Target(89),
                  _root_.scala.collection.immutable.Set(
                    Modifier.E5m2x2,
                    Modifier.F16x2,
                    Modifier.Satfinite
                  ) -> Target(89),
                  _root_.scala.collection.immutable.Set(
                    Modifier.E5m2x2,
                    Modifier.F32,
                    Modifier.Satfinite
                  ) -> Target(89),
                  _root_.scala.collection.immutable.Set(Modifier.F16x2) -> Target(80),
                  _root_.scala.collection.immutable.Set(
                    Modifier.F32,
                    Modifier.Relu,
                    Modifier.Rn,
                    Modifier.Tf32
                  ) -> Target(90),
                  _root_.scala.collection.immutable.Set(
                    Modifier.F32,
                    Modifier.Relu,
                    Modifier.Rz,
                    Modifier.Tf32
                  ) -> Target(90),
                  _root_.scala.collection.immutable.Set(
                    Modifier.F32,
                    Modifier.Rn,
                    Modifier.Satfinite,
                    Modifier.Tf32
                  ) -> Target(100),
                  _root_.scala.collection.immutable.Set(
                    Modifier.F32,
                    Modifier.Rz,
                    Modifier.Satfinite,
                    Modifier.Tf32
                  ) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13),
                  _root_.scala.collection.immutable.Set(Modifier.Relu) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.Tf32) -> Target(80)
                ),
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
                  metadataCvt_100_30(),
                  metadataCvt_100_31(),
                  metadataCvt_100_32(),
                  metadataCvt_100_33(),
                  metadataCvt_100_34(),
                  metadataCvt_100_35(),
                  metadataCvt_100_36()
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
                Version(6, 5),
                Target(72),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.S2) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.S4) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.U2) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.U4) -> Target(75)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Sat),
                      ModifierGroup(Modifier.S16, Modifier.U16),
                      ModifierGroup(Modifier.S32)
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.Sat),
                      ModifierGroup(Modifier.S2, Modifier.S4, Modifier.S8, Modifier.U2, Modifier.U4, Modifier.U8),
                      ModifierGroup(Modifier.S32),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(2, 0),
                Target(20),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Param) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.Param, Modifier.To) -> Target(70)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(
                        Modifier.Const,
                        Modifier.Global,
                        Modifier.Local,
                        Modifier.Param,
                        Modifier.ParamEntry,
                        Modifier.Shared,
                        Modifier.SharedCluster,
                        Modifier.SharedCta
                      ),
                      ModifierGroup(Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.To),
                      ModifierGroup(
                        Modifier.Const,
                        Modifier.Global,
                        Modifier.Local,
                        Modifier.Param,
                        Modifier.ParamEntry,
                        Modifier.Shared,
                        Modifier.SharedCluster,
                        Modifier.SharedCta
                      ),
                      ModifierGroup(Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(7, 4),
                Target(80),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.L2)),
                  _root_.scala.collection.immutable.Set(Modifier.Global),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(),
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
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.F32) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rm) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rn) -> Target(13),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rp) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rz) -> Target(20)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Full), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(5, 0),
                Target(61),
                Map(),
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
                Version(5, 0),
                Target(61),
                Map(),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.S32, Modifier.U32), ModifierGroup(Modifier.S32, Modifier.U32)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(8, 0),
                Target(90),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                  _root_.scala.collection.immutable.Set(Modifier.Ftz),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                Version(7, 0),
                Target(75),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Approx, Modifier.Bf16, Modifier.Ftz) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Approx, Modifier.Bf16x2, Modifier.Ftz) -> Target(90)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F16, Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Approx),
                      ModifierGroup(Modifier.Ftz),
                      ModifierGroup(Modifier.Bf16, Modifier.Bf16x2)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(9, 3),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.OpRestrictFetching),
                  _root_.scala.collection.immutable.Set(),
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
                Version(9, 3),
                Target(100),
                Map(),
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
                Version(9, 3),
                Target(100),
                Map(),
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
                    _root_.scala.collection.immutable.Set(),
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
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.Multimem),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(Modifier.Bf16, Modifier.F16, Modifier.F32, Modifier.U32, Modifier.U64),
                      ModifierGroup(Modifier.Sync)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.Multimem),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.E4m3, Modifier.E5m2),
                      ModifierGroup(Modifier.Sync)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.Multimem),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Sys),
                      ModifierGroup(Modifier.Bf16, Modifier.F16),
                      ModifierGroup(Modifier.Sync)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(9, 3),
                Target(100),
                Map(),
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
                    _root_.scala.collection.immutable.Set(),
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
                    _root_.scala.collection.immutable.Set(),
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
                Version(9, 3),
                Target(100),
                Map(),
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
                    _root_.scala.collection.immutable.Set(),
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
                    _root_.scala.collection.immutable.Set(),
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
                    _root_.scala.collection.immutable.Set(),
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
                Version(9, 3),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.SyncRestrictReads)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(20),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Acquire) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Async, Modifier.Proxy) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Cluster) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Proxy) -> Target(60),
                  _root_.scala.collection.immutable.Set(Modifier.Release) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Sys) -> Target(20)
                ),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(
                      ModifierGroup(Modifier.Acquire),
                      ModifierGroup(Modifier.SyncRestrictSharedCluster),
                      ModifierGroup(Modifier.Cluster)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.SyncRestrictSharedCta),
                      ModifierGroup(Modifier.Cluster)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(
                      ModifierGroup(Modifier.MbarrierInit),
                      ModifierGroup(Modifier.Release),
                      ModifierGroup(Modifier.Cluster)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Proxy), ModifierGroup(Modifier.Alias, Modifier.Async)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.F32) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F32x2) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13)
                ),
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
                      ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz),
                      ModifierGroup(Modifier.F32x2)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
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
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(4, 2),
                Target(53),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.Oob) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.Relu) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2, Modifier.Oob) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2, Modifier.Relu) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.F16, Modifier.Oob) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.F16, Modifier.Relu) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.F16x2, Modifier.Oob) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.F16x2, Modifier.Relu) -> Target(80)
                ),
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
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Sat),
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
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Relu), ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
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
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Relu), ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
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
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.Relu),
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
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Relu),
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
                    Vec(ModifierGroup(Modifier.Rn), ModifierGroup(Modifier.Oob), ModifierGroup(Modifier.Type)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    false
                  )
                )
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(100),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.F32) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.F16, Modifier.F32) -> Target(100)
                ),
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
                  _root_.scala.collection.immutable.Set(),
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
                Version(6, 0),
                Target(30),
                Map(),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.B32)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(8, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                Version(8, 2),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(7, 8),
                Target(90),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(Modifier.SharedCluster),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.SharedCluster), ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
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
                Version(7, 8),
                Target(90),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.LaunchDependents, Modifier.Wait)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(2, 0),
                Target(20),
                Map(_root_.scala.collection.immutable.Set(Modifier.Param) -> Target(70)),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(
                    Modifier.Const,
                    Modifier.Global,
                    Modifier.Local,
                    Modifier.Param,
                    Modifier.ParamEntry,
                    Modifier.Shared,
                    Modifier.SharedCluster,
                    Modifier.SharedCta
                  )),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(4, 0),
                Target(30),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Samplerref, Modifier.Surfref, Modifier.Texref)),
                  _root_.scala.collection.immutable.Set(),
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
            if (operands.size >= 2 && (operands.size == 2 || operands.size == 3))
              Right(Instruction.Ld(operands(0), operands(1), operands.lift(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("ld", Vec(2, 3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Acquire) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.B128) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.B32, Modifier.V8) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.B64, Modifier.V4) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.Cluster) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13),
                  _root_.scala.collection.immutable.Set(Modifier.L2256B) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.L2CacheHint) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.Mmio) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.Relaxed) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.Weak) -> Target(70)
                ),
                Vec(
                  metadataLd_86_0(),
                  metadataLd_86_1(),
                  metadataLd_86_2(),
                  metadataLd_86_3(),
                  metadataLd_86_4(),
                  metadataLd_86_5(),
                  metadataLd_86_6(),
                  metadataLd_86_7(),
                  metadataLd_86_8(),
                  metadataLd_86_9()
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
                Version(3, 1),
                Target(32),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.B128) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.B32, Modifier.V8) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.B64, Modifier.V4) -> Target(100)
                ),
                Vec(
                  metadataLdGlobalNc_87_0(),
                  metadataLdGlobalNc_87_1(),
                  metadataLdGlobalNc_87_2(),
                  metadataLdGlobalNc_87_3(),
                  metadataLdGlobalNc_87_4(),
                  metadataLdGlobalNc_87_5(),
                  metadataLdGlobalNc_87_6(),
                  metadataLdGlobalNc_87_7()
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
                Version(6, 5),
                Target(75),
                Map(),
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
                    _root_.scala.collection.immutable.Set(Modifier.Shared, Modifier.SharedCta, Modifier.Trans),
                    _root_.scala.collection.immutable.Set(
                      Modifier.M16n16,
                      Modifier.M8n8,
                      Modifier.Trans,
                      Modifier.X1,
                      Modifier.X2,
                      Modifier.X4
                    ),
                    true
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
                    _root_.scala.collection.immutable.Set(Modifier.Shared, Modifier.SharedCta),
                    _root_.scala.collection.immutable.Set(Modifier.X1, Modifier.X2, Modifier.X4),
                    true
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
                    _root_.scala.collection.immutable.Set(Modifier.Shared, Modifier.SharedCta),
                    _root_.scala.collection.immutable.Set(
                      Modifier.M16n16,
                      Modifier.Trans,
                      Modifier.X1,
                      Modifier.X2,
                      Modifier.X4
                    ),
                    true
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
                Version(2, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.B128) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13)
                ),
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                  _root_.scala.collection.immutable.Set(Modifier.Ftz),
                  _root_.scala.collection.immutable.Set(),
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
                Version(4, 3),
                Target(50),
                Map(),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.And, Modifier.Or), ModifierGroup(Modifier.B32)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(10),
                Map(),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(
                    ModifierGroup(Modifier.Cc),
                    ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Hi, Modifier.Lo),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(
                    Modifier.F32,
                    Modifier.Rm,
                    Modifier.Rn,
                    Modifier.Rp,
                    Modifier.Rz
                  ) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13),
                  _root_.scala.collection.immutable.Set(
                    Modifier.F64,
                    Modifier.Rm,
                    Modifier.Rn,
                    Modifier.Rp,
                    Modifier.Rz
                  ) -> Target(13)
                ),
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
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Sat),
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
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(Modifier.Hi, Modifier.Lo),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(),
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
                Version(1, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(
                    OperandConstraint.Register,
                    OperandConstraint.Value,
                    OperandConstraint.Value,
                    OperandConstraint.Value
                  ),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(Modifier.Cc, Modifier.Hi, Modifier.Lo),
                  _root_.scala.collection.immutable.Set(),
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
                Version(7, 8),
                Target(90),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(Modifier.SharedCluster),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.SharedCluster), ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
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
                Version(6, 0),
                Target(70),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(6, 0),
                Target(70),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.S16x2) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.S32) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.U16x2) -> Target(90)
                ),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S16x2, Modifier.S32, Modifier.S8x4)),
                    _root_.scala.collection.immutable.Set(Modifier.Relu),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Abs, Modifier.Xorsign) -> Target(86),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13),
                  _root_.scala.collection.immutable.Set(Modifier.NaN) -> Target(80)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
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
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(7, 0),
                Target(80),
                Map(_root_.scala.collection.immutable.Set(Modifier.Abs, Modifier.Xorsign) -> Target(86)),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Xorsign),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Xorsign),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(7, 0),
                Target(80),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Cluster) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.ExpectTx) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.NoComplete) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Relaxed) -> Target(90)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.SharedCluster
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.ExpectTx), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.NoComplete), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cta,
                      Modifier.Release,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(7, 0),
                Target(80),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Cluster) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.ExpectTx) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.NoComplete) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Relaxed) -> Target(90)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.SharedCluster
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.ExpectTx), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Release,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.NoComplete), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cta,
                      Modifier.Release,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(9, 3),
                Target(90),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(ModifierGroup(Modifier.LayoutV0, Modifier.LayoutV1), ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.SharedCta),
                  _root_.scala.collection.immutable.Set(),
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
                Version(8, 0),
                Target(90),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(
                    Modifier.Cluster,
                    Modifier.Cta,
                    Modifier.Relaxed,
                    Modifier.Shared,
                    Modifier.SharedCluster,
                    Modifier.SharedCta
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(8, 0),
                Target(90),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(
                    Modifier.Cluster,
                    Modifier.Cta,
                    Modifier.Relaxed,
                    Modifier.Shared,
                    Modifier.SharedCluster,
                    Modifier.SharedCta
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(7, 0),
                Target(80),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(
                    Modifier.LayoutV0,
                    Modifier.LayoutV1,
                    Modifier.Shared,
                    Modifier.SharedCta
                  ),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(7, 0),
                Target(80),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.Shared, Modifier.SharedCta),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(7, 0),
                Target(80),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B64)),
                  _root_.scala.collection.immutable.Set(Modifier.LayoutV0),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(1, 0),
                Target(80),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Cluster) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Relaxed) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.TestWait) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.TryWait) -> Target(90)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.PhaseTypePrimary,
                      Modifier.Relaxed,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                      Modifier.Relaxed,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.PhaseTypePrimary), ModifierGroup(Modifier.B64)),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                      Modifier.Relaxed,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                      Modifier.Relaxed,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Parity),
                      ModifierGroup(Modifier.PhaseTypePrimary),
                      ModifierGroup(Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Acquire,
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Relaxed,
                      Modifier.Shared,
                      Modifier.SharedCta
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(80),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Cluster) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Relaxed) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.TestWait) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.TryWait) -> Target(90)
                ),
                Vec(
                  metadataMbarrierTryWait_161_0(),
                  metadataMbarrierTryWait_161_1(),
                  metadataMbarrierTryWait_161_2(),
                  metadataMbarrierTryWait_161_3(),
                  metadataMbarrierTryWait_161_4(),
                  metadataMbarrierTryWait_161_5(),
                  metadataMbarrierTryWait_161_6(),
                  metadataMbarrierTryWait_161_7(),
                  metadataMbarrierTryWait_161_8(),
                  metadataMbarrierTryWait_161_9(),
                  metadataMbarrierTryWait_161_10(),
                  metadataMbarrierTryWait_161_11()
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
                Version(1, 0),
                Target(20),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Acquire) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Async, Modifier.Proxy) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Cluster) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Proxy) -> Target(60),
                  _root_.scala.collection.immutable.Set(Modifier.Release) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Sys) -> Target(20)
                ),
                Vec(
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Cta, Modifier.Gl, Modifier.Sys)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(),
                    Vec(ModifierGroup(Modifier.Proxy), ModifierGroup(Modifier.Alias, Modifier.Async)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(10),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.S16x2) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Relu, Modifier.S32) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.U16x2) -> Target(90)
                ),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S16x2, Modifier.S32, Modifier.S8x4)),
                    _root_.scala.collection.immutable.Set(Modifier.Relu),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Abs, Modifier.Xorsign) -> Target(86),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13),
                  _root_.scala.collection.immutable.Set(Modifier.NaN) -> Target(80)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
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
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(7, 0),
                Target(80),
                Map(_root_.scala.collection.immutable.Set(Modifier.Abs, Modifier.Xorsign) -> Target(86)),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Ftz, Modifier.Xorsign),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Xorsign),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs, Modifier.Xorsign),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4 || operands.size == 8)) Right(Instruction.Mma(
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
            else Left(Instruction.DecodeError.InvalidOperandCount("mma", Vec(4, 8), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(6, 4),
                Target(70),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.And) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.B1) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.BlockScale) -> Target(120),
                  _root_.scala.collection.immutable.Set(Modifier.E4m3) -> Target(89),
                  _root_.scala.collection.immutable.Set(Modifier.E5m2) -> Target(89),
                  _root_.scala.collection.immutable.Set(Modifier.F16) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.M16n8k128) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.M16n8k16) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.M16n8k256) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.M16n8k32) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.M16n8k4) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.M16n8k64) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.M16n8k8) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.M8n8k128) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.M8n8k16) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.M8n8k32) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.M8n8k4) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.S4) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.S8) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.Tf32) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.U4) -> Target(75),
                  _root_.scala.collection.immutable.Set(Modifier.U8) -> Target(75)
                ),
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
                  metadataMma_170_13(),
                  metadataMma_170_14()
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
            if (operands.size >= 6 && (operands.size == 6 || operands.size == 10))
              Right(Instruction.MmaSpOrderedMetadata(
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
              Left(Instruction.DecodeError.InvalidOperandCount("mma.sp::ordered_metadata", Vec(6, 10), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(7, 1),
                Target(80),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.E2m1) -> Target(120),
                  _root_.scala.collection.immutable.Set(Modifier.E2m3) -> Target(120),
                  _root_.scala.collection.immutable.Set(Modifier.E3m2) -> Target(120),
                  _root_.scala.collection.immutable.Set(Modifier.E4m3) -> Target(89),
                  _root_.scala.collection.immutable.Set(Modifier.E5m2) -> Target(89),
                  _root_.scala.collection.immutable.Set(Modifier.SpOrderedMetadata) -> Target(80)
                ),
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
                  metadataMmaSpOrderedMetadata_174_11(),
                  metadataMmaSpOrderedMetadata_174_12()
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
                Version(1, 0),
                Target(13),
                Map(_root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13)),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U32)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(10),
                Map(_root_.scala.collection.immutable.Set(Modifier.B128) -> Target(70)),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B128, Modifier.B16, Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(7, 8),
                Target(75),
                Map(),
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Hi, Modifier.Lo, Modifier.Wide),
                    ModifierGroup(Modifier.S16, Modifier.S32, Modifier.S64, Modifier.U16, Modifier.U32, Modifier.U64)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.F32) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F32x2) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13),
                  _root_.scala.collection.immutable.Set(
                    Modifier.F64,
                    Modifier.Rm,
                    Modifier.Rn,
                    Modifier.Rp,
                    Modifier.Rz
                  ) -> Target(13)
                ),
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(4, 2),
                Target(53),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2) -> Target(90)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Hi, Modifier.Lo), ModifierGroup(Modifier.S32, Modifier.U32)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(9, 1),
                Target(90),
                Map(_root_.scala.collection.immutable.Set(Modifier.CpMask) -> Target(100)),
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
                    _root_.scala.collection.immutable.Set(Modifier.CpMask, Modifier.Weak),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CpMask, Modifier.Weak),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CpMask),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster, Modifier.Cta, Modifier.Gpu, Modifier.Sys),
                      ModifierGroup(Modifier.Global),
                      ModifierGroup(Modifier.SharedCta),
                      ModifierGroup(Modifier.BulkGroup),
                      ModifierGroup(Modifier.B128)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CpMask),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(9, 1),
                Target(90),
                Map(),
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
                      ModifierGroup(
                        Modifier.B32,
                        Modifier.B64,
                        Modifier.Bf16,
                        Modifier.F16,
                        Modifier.F32,
                        Modifier.F64,
                        Modifier.S32,
                        Modifier.S64,
                        Modifier.U32,
                        Modifier.U64
                      )
                    ),
                    _root_.scala.collection.immutable.Set(
                      Modifier.Cluster,
                      Modifier.Cta,
                      Modifier.Gpu,
                      Modifier.Relaxed,
                      Modifier.Sys
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                    _root_.scala.collection.immutable.Set(),
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
                Version(8, 1),
                Target(90),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Add, Modifier.And, Modifier.Max, Modifier.Min, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.B32, Modifier.B64, Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Weak),
                      ModifierGroup(Modifier.Add, Modifier.And, Modifier.Max, Modifier.Min, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.B32, Modifier.B64, Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(8, 1),
                Target(90),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Add, Modifier.And, Modifier.Max, Modifier.Min, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.B32, Modifier.B64, Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(9, 3),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Release),
                    ModifierGroup(Modifier.Gpu, Modifier.Sys),
                    ModifierGroup(Modifier.Add),
                    ModifierGroup(Modifier.S32, Modifier.U32, Modifier.U64)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Global),
                  _root_.scala.collection.immutable.Set(),
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
                Version(8, 1),
                Target(90),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(ModifierGroup(
                      Modifier.B32,
                      Modifier.B64,
                      Modifier.S32,
                      Modifier.S64,
                      Modifier.U32,
                      Modifier.U64
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Weak),
                      ModifierGroup(Modifier.B32, Modifier.B64, Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(9, 3),
                Target(100),
                Map(),
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
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(70),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.U32)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S16, Modifier.S32, Modifier.S64, Modifier.S8x4)),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(_root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13)),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(6, 0),
                Target(53),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16) -> Target(80),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2) -> Target(80)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.Pred)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.Pred)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(20),
                Map(_root_.scala.collection.immutable.Set(Modifier.Mask) -> Target(20)),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Value),
                    Vec(),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Mask)),
                    _root_.scala.collection.immutable.Set(),
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
                Version(2, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(2, 0),
                Target(20),
                Map(_root_.scala.collection.immutable.Set(Modifier.Tensormap) -> Target(90)),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address),
                    Vec(ModifierGroup(Modifier.L1, Modifier.L2)),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Local),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address),
                    Vec(ModifierGroup(Modifier.Global), ModifierGroup(Modifier.L2EvictLast, Modifier.L2EvictNormal)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address),
                    Vec(ModifierGroup(Modifier.Tensormap)),
                    _root_.scala.collection.immutable.Set(Modifier.Const, Modifier.Param),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(2, 0),
                Target(20),
                Map(_root_.scala.collection.immutable.Set(Modifier.Tensormap) -> Target(90)),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(Modifier.L1)),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(2, 0),
                Target(20),
                Map(),
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
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.F32) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rm) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rn) -> Target(13),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rp) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rz) -> Target(20)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(20),
                Map(_root_.scala.collection.immutable.Set(Modifier.Approx, Modifier.F64, Modifier.Ftz) -> Target(20)),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 2),
                Target(11),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.Bf16, Modifier.Noftz) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.Bf16x2, Modifier.Noftz) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.F16, Modifier.Noftz) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.F16x2, Modifier.Noftz) -> Target(60),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.F32) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.F64) -> Target(60),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.Global, Modifier.U64) -> Target(12),
                  _root_.scala.collection.immutable.Set(Modifier.Add, Modifier.Shared, Modifier.U64) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.And) -> Target(32),
                  _root_.scala.collection.immutable.Set(Modifier.Cluster) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Global) -> Target(11),
                  _root_.scala.collection.immutable.Set(Modifier.Max) -> Target(32),
                  _root_.scala.collection.immutable.Set(Modifier.Min) -> Target(32),
                  _root_.scala.collection.immutable.Set(Modifier.Or) -> Target(32),
                  _root_.scala.collection.immutable.Set(Modifier.Shared) -> Target(12),
                  _root_.scala.collection.immutable.Set(Modifier.Xor) -> Target(32)
                ),
                Vec(
                  metadataRed_144_0(),
                  metadataRed_144_1(),
                  metadataRed_144_2(),
                  metadataRed_144_3(),
                  metadataRed_144_4(),
                  metadataRed_144_5(),
                  metadataRed_144_6(),
                  metadataRed_144_7(),
                  metadataRed_144_8(),
                  metadataRed_144_9(),
                  metadataRed_144_10(),
                  metadataRed_144_11(),
                  metadataRed_144_12(),
                  metadataRed_144_13(),
                  metadataRed_144_14(),
                  metadataRed_144_15()
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
                Version(8, 1),
                Target(90),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Global) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.Gpu) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.Mmio) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.Release) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.Sys) -> Target(100)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster),
                      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
                      ModifierGroup(Modifier.Dec, Modifier.Inc),
                      ModifierGroup(Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.SharedCluster),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster),
                      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
                      ModifierGroup(Modifier.Max, Modifier.Min),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.SharedCluster),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster),
                      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
                      ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.SharedCluster),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Relaxed),
                      ModifierGroup(Modifier.Cluster),
                      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
                      ModifierGroup(Modifier.Add),
                      ModifierGroup(Modifier.S32, Modifier.U32, Modifier.U64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.SharedCluster),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(7, 0),
                Target(80),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Abs) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.NaN) -> Target(100)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.And, Modifier.Or, Modifier.Xor), ModifierGroup(Modifier.B32)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Max, Modifier.Min), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.NaN, Modifier.Abs),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(10),
                Map(),
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.Uni),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(1, 0),
                Target(13),
                Map(_root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13)),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(20),
                Map(_root_.scala.collection.immutable.Set(Modifier.Approx, Modifier.F64, Modifier.Ftz) -> Target(20)),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(),
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
                Version(1, 0),
                Target(13),
                Map(_root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13)),
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
                Version(1, 0),
                Target(13),
                Map(_root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13)),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(4, 2),
                Target(53),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.B16, Modifier.Bf16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.B32, Modifier.Bf16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.B64, Modifier.Bf16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.F16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.F32) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.F64) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.S16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.S32) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.S64) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.U16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.U32) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.U64) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2, Modifier.S32) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2, Modifier.U32) -> Target(90)
                ),
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
                Version(8, 0),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Dec, Modifier.Inc),
                    ModifierGroup(Modifier.Sync),
                    ModifierGroup(Modifier.Aligned),
                    ModifierGroup(Modifier.U32)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(1, 0),
                Target(13),
                Map(_root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13)),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(4, 2),
                Target(53),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2) -> Target(90)
                ),
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(3, 1),
                Target(32),
                Map(),
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
                Version(3, 0),
                Target(30),
                Map(),
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
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(6, 0),
                Target(30),
                Map(),
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(10),
                Map(),
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                  _root_.scala.collection.immutable.Set(Modifier.Ftz),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(13),
                Map(_root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13)),
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
                    _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.F32) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rm) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rn) -> Target(13),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rp) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F64, Modifier.Rz) -> Target(20)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F32)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz), ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.B128) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.B32, Modifier.V8) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.B64, Modifier.V4) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.Cluster) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13),
                  _root_.scala.collection.immutable.Set(Modifier.Mmio) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.Relaxed) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.Release) -> Target(70),
                  _root_.scala.collection.immutable.Set(Modifier.Weak) -> Target(70)
                ),
                Vec(
                  metadataSt_89_0(),
                  metadataSt_89_1(),
                  metadataSt_89_2(),
                  metadataSt_89_3(),
                  metadataSt_89_4(),
                  metadataSt_89_5(),
                  metadataSt_89_6(),
                  metadataSt_89_7(),
                  metadataSt_89_8(),
                  metadataSt_89_9()
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
                Version(8, 1),
                Target(90),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Global) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.Mmio) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.Release) -> Target(100)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.MbarrierCompleteTxBytes),
                      ModifierGroup(
                        Modifier.B128,
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
                      Modifier.SharedCluster,
                      Modifier.V2,
                      Modifier.V4,
                      Modifier.Weak
                    ),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(),
                  _root_.scala.collection.immutable.Set(Modifier.SharedCta, Modifier.Weak),
                  _root_.scala.collection.immutable.Set(),
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
                Version(7, 3),
                Target(52),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(7, 3),
                Target(52),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register),
                  Vec(ModifierGroup(Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(7, 8),
                Target(90),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Sync),
                    ModifierGroup(Modifier.Aligned),
                    ModifierGroup(Modifier.M16n8, Modifier.M8n8),
                    ModifierGroup(Modifier.X1, Modifier.X2, Modifier.X4),
                    ModifierGroup(Modifier.B16, Modifier.B8)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Shared, Modifier.SharedCta, Modifier.Trans),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S16, Modifier.S64, Modifier.U16, Modifier.U32, Modifier.U64)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.S32, Modifier.S8x4, Modifier.U8x4)),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(13),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.F32) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F32x2) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.F64) -> Target(13),
                  _root_.scala.collection.immutable.Set(
                    Modifier.F64,
                    Modifier.Rm,
                    Modifier.Rn,
                    Modifier.Rp,
                    Modifier.Rz
                  ) -> Target(13)
                ),
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F64)),
                    _root_.scala.collection.immutable.Set(Modifier.Rm, Modifier.Rn, Modifier.Rp, Modifier.Rz),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(4, 2),
                Target(53),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Bf16x2) -> Target(90)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.F16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Ftz, Modifier.Rn, Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Bf16x2)),
                    _root_.scala.collection.immutable.Set(Modifier.Rn),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
              ),
              Metadata.Entry(
                Version(1, 0),
                Target(100),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Bf16, Modifier.F32) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.F16, Modifier.F32) -> Target(100)
                ),
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
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(20),
                Map(_root_.scala.collection.immutable.Set(Modifier.Cc) -> Target(20)),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(20),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.S32, Modifier.S64, Modifier.U32, Modifier.U64)),
                  _root_.scala.collection.immutable.Set(Modifier.Cc),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 0),
                Target(20),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.A1d) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.A2d) -> Target(20)
                ),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Address),
                  Vec(
                    ModifierGroup(Modifier.B),
                    ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d, Modifier.A1d, Modifier.A2d),
                    ModifierGroup(Modifier.None, Modifier.V2, Modifier.V4),
                    ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.B8),
                    ModifierGroup(Modifier.Clamp, Modifier.Trap, Modifier.Zero)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.Ca, Modifier.Cg, Modifier.Cs, Modifier.Cv),
                  _root_.scala.collection.immutable.Set(),
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
                Version(1, 5),
                Target(10),
                Map(),
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
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(2, 0),
                Target(20),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.B64) -> Target(50),
                  _root_.scala.collection.immutable.Set(Modifier.Max) -> Target(50),
                  _root_.scala.collection.immutable.Set(Modifier.Min) -> Target(50)
                ),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.B),
                      ModifierGroup(Modifier.Add, Modifier.And, Modifier.Max, Modifier.Min, Modifier.Or),
                      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d),
                      ModifierGroup(Modifier.B32, Modifier.B64),
                      ModifierGroup(Modifier.Clamp, Modifier.Trap, Modifier.Zero)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.P),
                      ModifierGroup(Modifier.Add, Modifier.And, Modifier.Max, Modifier.Min, Modifier.Or),
                      ModifierGroup(Modifier.N1d, Modifier.N2d, Modifier.N3d),
                      ModifierGroup(Modifier.B32, Modifier.B64),
                      ModifierGroup(Modifier.Clamp, Modifier.Trap, Modifier.Zero)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(20),
                Map(_root_.scala.collection.immutable.Set(Modifier.P) -> Target(20)),
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
                    _root_.scala.collection.immutable.Set(),
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
                Version(7, 6),
                Target(70),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Clamp, Modifier.Wrap), ModifierGroup(Modifier.S32, Modifier.U32)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(7, 0),
                Target(75),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Approx), ModifierGroup(Modifier.F32)),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              ),
              Metadata.Entry(
                Version(7, 0),
                Target(75),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Approx, Modifier.Bf16) -> Target(90),
                  _root_.scala.collection.immutable.Set(Modifier.Approx, Modifier.Bf16x2) -> Target(90)
                ),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.Approx),
                    ModifierGroup(Modifier.Bf16, Modifier.Bf16x2, Modifier.F16, Modifier.F16x2)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
                    ModifierGroup(Modifier.Sync),
                    ModifierGroup(Modifier.Aligned),
                    ModifierGroup(Modifier.B32)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.SharedCta),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
                      ModifierGroup(Modifier.MbarrierArriveOne),
                      ModifierGroup(Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.MulticastCluster, Modifier.SharedCluster),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
                      ModifierGroup(Modifier.MbarrierArriveOne),
                      ModifierGroup(Modifier.B64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.MulticastCluster, Modifier.SharedCluster),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
                    ModifierGroup(Modifier.N128x128b, Modifier.N128x256b, Modifier.N4x256b)
                  ),
                  _root_.scala.collection.immutable.Set(Modifier.B4x16P64, Modifier.B6x16P32, Modifier.B8x16),
                  _root_.scala.collection.immutable.Set(),
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value, OperandConstraint.Value),
                  Vec(
                    ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
                    ModifierGroup(Modifier.Sync),
                    ModifierGroup(Modifier.Aligned),
                    ModifierGroup(Modifier.B32)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.N16x128b, Modifier.N16x256b, Modifier.N16x64b, Modifier.N32x32b),
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
                    _root_.scala.collection.immutable.Set(Modifier.Pack16b),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.N16x32bx2),
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
                    _root_.scala.collection.immutable.Set(Modifier.Pack16b),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.N32x32b),
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                      ModifierGroup(Modifier.N16x32bx2),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.Red),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.N32x32b),
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                      ModifierGroup(Modifier.N16x32bx2),
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(8, 6),
                Target(100),
                Map(_root_.scala.collection.immutable.Set(Modifier.KindI8) -> Target(100)),
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
                  metadataTcgen05Mma_186_13(),
                  metadataTcgen05Mma_186_14(),
                  metadataTcgen05Mma_186_15(),
                  metadataTcgen05Mma_186_16(),
                  metadataTcgen05Mma_186_17(),
                  metadataTcgen05Mma_186_18()
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
                Version(8, 6),
                Target(100),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.KindI8) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.KindMxf4) -> Target(100),
                  _root_.scala.collection.immutable.Set(Modifier.KindMxf4nvf4) -> Target(100)
                ),
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
                  metadataTcgen05MmaSp_187_13(),
                  metadataTcgen05MmaSp_187_14(),
                  metadataTcgen05MmaSp_187_15(),
                  metadataTcgen05MmaSp_187_16(),
                  metadataTcgen05MmaSp_187_17(),
                  metadataTcgen05MmaSp_187_18()
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
                Version(8, 6),
                Target(100),
                Map(_root_.scala.collection.immutable.Set(Modifier.KindI8) -> Target(100)),
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
                      ModifierGroup(Modifier.CtaGroup1),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.CtaGroup1),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                      ModifierGroup(Modifier.CtaGroup1),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                      ModifierGroup(Modifier.CtaGroup1),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    Vec(ModifierGroup(Modifier.CtaGroup1), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.CtaGroup1), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    Vec(ModifierGroup(Modifier.CtaGroup1), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.CtaGroup1), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(8, 6),
                Target(100),
                Map(_root_.scala.collection.immutable.Set(Modifier.KindI8) -> Target(100)),
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
                      ModifierGroup(Modifier.CtaGroup1),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.CtaGroup1),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                      ModifierGroup(Modifier.CtaGroup1),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                      ModifierGroup(Modifier.CtaGroup1),
                      ModifierGroup(Modifier.KindF16, Modifier.KindF8f6f4, Modifier.KindTf32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    Vec(ModifierGroup(Modifier.CtaGroup1), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Address,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(ModifierGroup(Modifier.CtaGroup1), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    Vec(ModifierGroup(Modifier.CtaGroup1), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    Vec(ModifierGroup(Modifier.CtaGroup1), ModifierGroup(Modifier.KindI8)),
                    _root_.scala.collection.immutable.Set(Modifier.CollectorBufferOp),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(
                    ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2),
                    ModifierGroup(Modifier.Sync),
                    ModifierGroup(Modifier.Aligned)
                  ),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(8, 6),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address),
                  Vec(ModifierGroup(Modifier.CtaGroup1, Modifier.CtaGroup2), ModifierGroup(Modifier.Down)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.N16x128b, Modifier.N16x256b, Modifier.N16x64b, Modifier.N32x32b),
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
                    _root_.scala.collection.immutable.Set(Modifier.Unpack16b),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.N16x32bx2),
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
                    _root_.scala.collection.immutable.Set(Modifier.Unpack16b),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(8, 6),
                Target(100),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
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
                Version(8, 3),
                Target(90),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Address, OperandConstraint.Address, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(8, 3),
                Target(100),
                Map(),
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(2, 0),
                Target(20),
                Map(),
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
              operands.size >= 2 && (operands.size == 2 || operands.size == 3 || operands.size == 4 || operands.size == 5 || operands.size == 6)
            ) Right(Instruction.Tex(
              operands(0),
              operands(1),
              operands.lift(2),
              operands.lift(3),
              operands.lift(4),
              operands.lift(5),
              mods,
              pred
            ))
            else Left(Instruction.DecodeError.InvalidOperandCount("tex", Vec(2, 3, 4, 5, 6), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(1, 0),
                Target(10),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.Acube) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.Acube, Modifier.Grad) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.Cube, Modifier.Grad) -> Target(20),
                  _root_.scala.collection.immutable.Set(Modifier.F16) -> Target(53),
                  _root_.scala.collection.immutable.Set(Modifier.F16x2) -> Target(53)
                ),
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
                  metadataTex_124_9(),
                  metadataTex_124_10(),
                  metadataTex_124_11(),
                  metadataTex_124_12(),
                  metadataTex_124_13(),
                  metadataTex_124_14(),
                  metadataTex_124_15(),
                  metadataTex_124_16(),
                  metadataTex_124_17(),
                  metadataTex_124_18(),
                  metadataTex_124_19(),
                  metadataTex_124_20(),
                  metadataTex_124_21(),
                  metadataTex_124_22(),
                  metadataTex_124_23()
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
                Version(2, 2),
                Target(20),
                Map(
                  _root_.scala.collection.immutable.Set(Modifier.A2d) -> Target(30),
                  _root_.scala.collection.immutable.Set(Modifier.Acube) -> Target(30),
                  _root_.scala.collection.immutable.Set(Modifier.Cube) -> Target(30)
                ),
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
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.A, Modifier.B, Modifier.G, Modifier.R),
                      ModifierGroup(Modifier.V4),
                      ModifierGroup(Modifier.F32, Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    false
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.A, Modifier.B, Modifier.G, Modifier.R),
                      ModifierGroup(Modifier.V4),
                      ModifierGroup(Modifier.F32, Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.N2d, Modifier.A2d, Modifier.Acube, Modifier.Cube),
                      ModifierGroup(Modifier.V4),
                      ModifierGroup(Modifier.F32, Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.A, Modifier.B, Modifier.G, Modifier.R),
                      ModifierGroup(Modifier.N2d, Modifier.A2d, Modifier.Acube, Modifier.Cube),
                      ModifierGroup(Modifier.V4),
                      ModifierGroup(Modifier.F32, Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address),
                    Vec(
                      ModifierGroup(Modifier.A, Modifier.B, Modifier.G, Modifier.R),
                      ModifierGroup(Modifier.N2d, Modifier.A2d, Modifier.Acube, Modifier.Cube),
                      ModifierGroup(Modifier.V4),
                      ModifierGroup(Modifier.F32, Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
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
                Version(1, 5),
                Target(10),
                Map(_root_.scala.collection.immutable.Set(Modifier.Level) -> Target(30)),
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
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.Level),
                      ModifierGroup(Modifier.Depth, Modifier.Height, Modifier.Width),
                      ModifierGroup(Modifier.B32)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Vabsdiff(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vabsdiff", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(2, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vabsdiff2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vabsdiff2", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vabsdiff4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vabsdiff4", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Vadd(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vadd", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(2, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vadd2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vadd2", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vadd4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vadd4", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vavrg2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vavrg2", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vavrg4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vavrg4", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vmad(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmad", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(2, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat, Modifier.Shr15, Modifier.Shr7),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Po)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat, Modifier.Shr15, Modifier.Shr7),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Vmax(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmax", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(2, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vmax2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmax2", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vmax4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmax4", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Vmin(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmin", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(2, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vmin2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmin2", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vmin4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vmin4", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 2 && (operands.size == 2))
              Right(Instruction.Vote(operands(0), operands(1), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vote", Vec(2), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(1, 2),
                Target(12),
                Map(_root_.scala.collection.immutable.Set(Modifier.B32, Modifier.Ballot) -> Target(20)),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.All, Modifier.Any, Modifier.Uni), ModifierGroup(Modifier.Pred)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Ballot), ModifierGroup(Modifier.B32)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 3 && (operands.size == 3))
              Right(Instruction.VoteSync(operands(0), operands(1), operands(2), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vote.sync", Vec(3), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(6, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.All, Modifier.Any, Modifier.Uni), ModifierGroup(Modifier.Pred)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(ModifierGroup(Modifier.Ballot), ModifierGroup(Modifier.B32)),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Vset(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vset", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(2, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne)
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vset2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vset2", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne)
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vset4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vset4", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne)
                    ),
                    _root_.scala.collection.immutable.Set(),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Eq, Modifier.Ge, Modifier.Gt, Modifier.Le, Modifier.Lt, Modifier.Ne),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Vshl(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vshl", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(2, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Vshr(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vshr", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(2, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.U32),
                      ModifierGroup(Modifier.Clamp, Modifier.Wrap)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 3 && (operands.size == 3 || operands.size == 4))
              Right(Instruction.Vsub(operands(0), operands(1), operands(2), operands.lift(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vsub", Vec(3, 4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(2, 0),
                Target(20),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add, Modifier.Max, Modifier.Min)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vsub2(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vsub2", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
            if (operands.size >= 4 && (operands.size == 4))
              Right(Instruction.Vsub4(operands(0), operands(1), operands(2), operands(3), mods, pred))
            else Left(Instruction.DecodeError.InvalidOperandCount("vsub4", Vec(4), operands.size))
          },
          () =>
            Metadata(Vec.nonEmpty(
              Metadata.Entry(
                Version(3, 0),
                Target(30),
                Map(),
                Vec(
                  InstructionForm(
                    Vec(
                      OperandConstraint.Register,
                      OperandConstraint.Value,
                      OperandConstraint.Value,
                      OperandConstraint.Value
                    ),
                    Vec(
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Sat),
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
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.S32, Modifier.U32),
                      ModifierGroup(Modifier.Add)
                    ),
                    _root_.scala.collection.immutable.Set(),
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(8, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(8, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(8, 0),
                Target(10),
                Map(),
                Vec(
                  metadataWgmmaMmaAsync_175_0(),
                  metadataWgmmaMmaAsync_175_1(),
                  metadataWgmmaMmaAsync_175_2(),
                  metadataWgmmaMmaAsync_175_3(),
                  metadataWgmmaMmaAsync_175_4(),
                  metadataWgmmaMmaAsync_175_5(),
                  metadataWgmmaMmaAsync_175_6(),
                  metadataWgmmaMmaAsync_175_7()
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
                Version(8, 2),
                Target(10),
                Map(),
                Vec(
                  metadataWgmmaMmaAsyncSp_176_0(),
                  metadataWgmmaMmaAsyncSp_176_1(),
                  metadataWgmmaMmaAsyncSp_176_2(),
                  metadataWgmmaMmaAsyncSp_176_3(),
                  metadataWgmmaMmaAsyncSp_176_4(),
                  metadataWgmmaMmaAsyncSp_176_5(),
                  metadataWgmmaMmaAsyncSp_176_6()
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
                Version(8, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.Sync), ModifierGroup(Modifier.Aligned)),
                  _root_.scala.collection.immutable.Set(),
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
                Version(6, 0),
                Target(70),
                Map(),
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
                  metadataWmmaLoad_167_17(),
                  metadataWmmaLoad_167_18(),
                  metadataWmmaLoad_167_19(),
                  metadataWmmaLoad_167_20(),
                  metadataWmmaLoad_167_21(),
                  metadataWmmaLoad_167_22(),
                  metadataWmmaLoad_167_23(),
                  metadataWmmaLoad_167_24(),
                  metadataWmmaLoad_167_25(),
                  metadataWmmaLoad_167_26(),
                  metadataWmmaLoad_167_27(),
                  metadataWmmaLoad_167_28(),
                  metadataWmmaLoad_167_29(),
                  metadataWmmaLoad_167_30(),
                  metadataWmmaLoad_167_31(),
                  metadataWmmaLoad_167_32(),
                  metadataWmmaLoad_167_33(),
                  metadataWmmaLoad_167_34(),
                  metadataWmmaLoad_167_35()
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
                Version(6, 0),
                Target(70),
                Map(_root_.scala.collection.immutable.Set(Modifier.And) -> Target(80)),
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
                    _root_.scala.collection.immutable.Set(),
                    true
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
                Version(6, 0),
                Target(70),
                Map(_root_.scala.collection.immutable.Set(Modifier.M16n16k8) -> Target(80)),
                Vec(
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.D),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
                      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.D),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M16n16k16, Modifier.M32n8k16, Modifier.M8n32k16),
                      ModifierGroup(Modifier.F16, Modifier.F32, Modifier.S32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.D),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M8n8k128, Modifier.M8n8k32),
                      ModifierGroup(Modifier.S32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.D),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M8n8k128, Modifier.M8n8k32),
                      ModifierGroup(Modifier.S32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.D),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M16n16k8),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.D),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M16n16k8),
                      ModifierGroup(Modifier.F32)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.D),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M8n8k4),
                      ModifierGroup(Modifier.F64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  ),
                  InstructionForm(
                    Vec(OperandConstraint.Address, OperandConstraint.Value),
                    Vec(
                      ModifierGroup(Modifier.D),
                      ModifierGroup(Modifier.Sync),
                      ModifierGroup(Modifier.Aligned),
                      ModifierGroup(Modifier.Col, Modifier.Row),
                      ModifierGroup(Modifier.M8n8k4),
                      ModifierGroup(Modifier.F64)
                    ),
                    _root_.scala.collection.immutable.Set(Modifier.Global, Modifier.Shared, Modifier.SharedCta),
                    _root_.scala.collection.immutable.Set(),
                    true
                  )
                )
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
                Version(1, 0),
                Target(10),
                Map(),
                Vec(InstructionForm(
                  Vec(OperandConstraint.Register, OperandConstraint.Value, OperandConstraint.Value),
                  Vec(ModifierGroup(Modifier.B16, Modifier.B32, Modifier.B64, Modifier.Pred)),
                  _root_.scala.collection.immutable.Set(),
                  _root_.scala.collection.immutable.Set(),
                  true
                ))
              )
            ))
        )

    lazy val meta: Metadata = metadata()

  }

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
      predicate: Operand,
      tryCancelResponse: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.ClusterLaunchControlQueryCancel, predicate, tryCancelResponse)
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
      src: Operand,
      secondarySrc: Option[Operand],
      roundingBits: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Cvt, (Vec(dest, src) ++ secondarySrc.toVec ++ roundingBits.toVec)*)
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
  case Ld(dest: Operand, address: Operand, cachePolicy: Option[Operand], mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Ld, (Vec(dest, address) ++ cachePolicy.toVec)*)
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
      scaleASelector: Option[Operand],
      scaleBData: Option[Operand],
      scaleBSelector: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Mma,
        (Vec(
          dest,
          srcA,
          srcB,
          srcC
        ) ++ scaleAData.toVec ++ scaleASelector.toVec ++ scaleBData.toVec ++ scaleBSelector.toVec)*
      )
  case MmaSpOrderedMetadata(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      e: Operand,
      f: Operand,
      scaleAData: Option[Operand],
      scaleASelector: Option[Operand],
      scaleBData: Option[Operand],
      scaleBSelector: Option[Operand],
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
        ) ++ scaleAData.toVec ++ scaleASelector.toVec ++ scaleBData.toVec ++ scaleBSelector.toVec)*
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
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(
        Instruction.Definition.Tex,
        (Vec(
          dest,
          textureAndCoordinates
        ) ++ lodOffsetOrDerivativeX.toVec ++ depthCompareOrDerivativeY.toVec ++ offset.toVec ++ depthCompare.toVec)*
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
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vabsdiff, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vabsdiff2(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vabsdiff2, dest, srcA, srcB, srcC)
  case Vabsdiff4(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vabsdiff4, dest, srcA, srcB, srcC)
  case Vadd(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vadd, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vadd2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vadd2, dest, srcA, srcB, srcC)
  case Vadd4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vadd4, dest, srcA, srcB, srcC)
  case Vavrg2(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vavrg2, dest, srcA, srcB, srcC)
  case Vavrg4(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Operand,
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vavrg4, dest, srcA, srcB, srcC)
  case Vmad(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vmad, dest, srcA, srcB, srcC)
  case Vmax(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vmax, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vmax2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vmax2, dest, srcA, srcB, srcC)
  case Vmax4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vmax4, dest, srcA, srcB, srcC)
  case Vmin(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vmin, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vmin2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vmin2, dest, srcA, srcB, srcC)
  case Vmin4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vmin4, dest, srcA, srcB, srcC)
  case Vote(dest: Operand, srcA: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vote, dest, srcA)
  case VoteSync(dest: Operand, srcA: Operand, memberMask: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.VoteSync, dest, srcA, memberMask)
  case Vset(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vset, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vset2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vset2, dest, srcA, srcB, srcC)
  case Vset4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vset4, dest, srcA, srcB, srcC)
  case Vshl(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vshl, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vshr(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vshr, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vsub(
      dest: Operand,
      srcA: Operand,
      srcB: Operand,
      srcC: Option[Operand],
      mods: Vec[Modifier],
      pred: Option[Predication]
  ) extends Instruction(Instruction.Definition.Vsub, (Vec(dest, srcA, srcB) ++ srcC.toVec)*)
  case Vsub2(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vsub2, dest, srcA, srcB, srcC)
  case Vsub4(dest: Operand, srcA: Operand, srcB: Operand, srcC: Operand, mods: Vec[Modifier], pred: Option[Predication])
      extends Instruction(Instruction.Definition.Vsub4, dest, srcA, srcB, srcC)
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
