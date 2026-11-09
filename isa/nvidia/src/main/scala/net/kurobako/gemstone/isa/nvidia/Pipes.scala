package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.profile.*

object Pipes {

  private def float(width: Int): Pipe = if (width == 64) Pipe.Fp64 else Pipe.Fma

  def of(instruction: Instruction): Option[Pipe] = instruction match {
    case _: (Instruction.Tensor | Instruction.TensorMemoryMultiply) => Some(Pipe.Tensor)
    case _: (Instruction.IntegerUnary | Instruction.BitFind | Instruction.CompareSetCombined |
          Instruction.SetPredicateCombined | Instruction.FunnelShift | Instruction.Logic3 |
          Instruction.IntegerSubtractAdd | Instruction.IntegerAddWithCarry | Instruction.IntegerSubtractWithBorrow |
          Instruction.IntegerAddThree | Instruction.BitFieldExtract | Instruction.BitFieldInsert | Instruction.BitMask |
          Instruction.BytePermute | Instruction.Select | Instruction.CompareSet |
          Instruction.SetPredicate) => Some(Pipe.Alu)
    case _: (Instruction.MultiplyAdd | Instruction.FusedMultiplyAddBf16 | Instruction.FusedMultiplyAddPacked |
          Instruction.FloatBinaryPacked | Instruction.FloatUnaryPacked) => Some(Pipe.Fma)
    case Instruction.MufuUnary(_, _, _, _, _)   => Some(Pipe.Mufu)
    case instruction: Instruction.IntegerBinary =>
      instruction.operation match {
        case IntegerOperation.Add | IntegerOperation.Subtract | IntegerOperation.AddSaturated |
            IntegerOperation.SubtractSaturated | IntegerOperation.BitwiseOr | IntegerOperation.BitwiseXor |
            IntegerOperation.BitwiseAnd | IntegerOperation.Minimum(_) | IntegerOperation.Maximum(_) |
            IntegerOperation.ShiftLeft | IntegerOperation.ShiftRightLogical |
            IntegerOperation.ShiftRightArithmetic =>
          Some(Pipe.Alu)
        case IntegerOperation.MultiplyLow | IntegerOperation.MultiplyHigh(_) |
            IntegerOperation.MultiplyWideUnsigned16 | IntegerOperation.MultiplyWideSigned16 |
            IntegerOperation.MultiplyWideUnsigned32 | IntegerOperation.MultiplyWideSigned32 =>
          Some(Pipe.Fma)
        case IntegerOperation.Divide(_) | IntegerOperation.Remainder(_) | IntegerOperation.CarrylessMultiply(_) =>
          None
      }
    case instruction: Instruction.FloatBinary =>
      if (instruction.operation == FloatOperation.Divide) None else Some(float(instruction.width))
    case instruction: Instruction.FloatUnary =>
      instruction.operation match {
        case FloatUnaryOperation.Negate | FloatUnaryOperation.Absolute => Some(float(instruction.width))
        case FloatUnaryOperation.Reciprocal | FloatUnaryOperation.SquareRoot |
            FloatUnaryOperation.ReciprocalSquareRoot | FloatUnaryOperation.Exponent2 | FloatUnaryOperation.Log2 |
            FloatUnaryOperation.Sine | FloatUnaryOperation.Cosine | FloatUnaryOperation.Tanh =>
          Some(if (instruction.width == 64) Pipe.Fp64 else Pipe.Mufu)
      }
    case instruction: Instruction.FusedMultiplyAdd => Some(float(instruction.width))
    case Instruction.InvalidateAddThreeCarry | Instruction.TensorMemoryRelinquish | Instruction.NoOp |
        Instruction.Yield | Instruction.Spin | Instruction.Fence | Instruction.Trap | Instruction.Return |
        Instruction.Exit | _: (Instruction.Texture | Instruction.ConvertTF32 | Instruction.LoadParameter |
          Instruction.StoreParameter | Instruction.Move | Instruction.MoveVector | Instruction.UniformMove |
          Instruction.MovePredicate | Instruction.MoveSplit | Instruction.MoveCombine | Instruction.DotProduct4 |
          Instruction.DotProduct2 | Instruction.SumAbsoluteDifference | Instruction.FindNthSet |
          Instruction.PackSaturated | Instruction.ConvertToFloat8 | Instruction.ConvertNarrow |
          Instruction.ConvertWiden | Instruction.ConvertFromFloat8 | Instruction.SpaceTest |
          Instruction.PackedSaturatingAdd | Instruction.VideoSimd | Instruction.VideoScalar |
          Instruction.VideoMultiplyAdd | Instruction.ConvertStochastic | Instruction.SelectSign |
          Instruction.TestProperty | Instruction.StackAllocate | Instruction.StackSave | Instruction.StackRestore |
          Instruction.WriteConditionFlag | Instruction.ReadConditionFlag | Instruction.ReadConditionPredicate |
          Instruction.InvalidateConditionFlag | Instruction.Shuffle | Instruction.VoteBallot | Instruction.Elect |
          Instruction.WarpReduceFloat | Instruction.WarpMatch | Instruction.VotePredicate | Instruction.Convert |
          Instruction.ConvertPacked | Instruction.Load | Instruction.LoadVector | Instruction.Store |
          Instruction.StoreVector | Instruction.AsyncCopy | Instruction.AsyncBulkStore | Instruction.AsyncBulkCopy |
          Instruction.AsyncBulkReduce | Instruction.AsyncBulkFill | Instruction.AsyncTensor |
          Instruction.TensorMapReplace | Instruction.TensorMapCopy | Instruction.AsyncSharedStore |
          Instruction.AsyncSharedReduce | Instruction.ClusterMap | Instruction.ClusterRankOf |
          Instruction.ClusterBarrier | Instruction.ClusterTryCancel | Instruction.ClusterCancelStatus |
          Instruction.ClusterCancelCta | Instruction.TensorMemoryAllocate | Instruction.TensorMemoryDeallocate |
          Instruction.TensorMemoryClaim | Instruction.TensorMemoryRelease | Instruction.TensorMemoryLoad |
          Instruction.TensorMemoryStore | Instruction.TensorMemoryCopy | Instruction.TensorMemoryShift |
          Instruction.TensorMemoryCommit | Instruction.MBarrierLoad | Instruction.MBarrierInit |
          Instruction.MBarrierArrive | Instruction.MBarrierPending | Instruction.MBarrierTransaction |
          Instruction.MBarrierTest | Instruction.MBarrierInvalidate | Instruction.AsyncCommit | Instruction.AsyncWait |
          Instruction.SharedLockLoad | Instruction.SharedStoreUnlock | Instruction.PredicateCompare |
          Instruction.PredicateUnary | Instruction.PredicateBinary | Instruction.Branch | Instruction.BranchTable |
          Instruction.BarrierSync | Instruction.BarrierArrive | Instruction.BarrierReduce | Instruction.Atomic |
          Instruction.AtomicCompareExchange | Instruction.WarpReduce | Instruction.Convergence |
          Instruction.ReturnStackPush | Instruction.ReturnStackPop | Instruction.ReturnStackSave |
          Instruction.WarpBarrier | Instruction.Call | Instruction.CallBuiltin) => None
  }
}
