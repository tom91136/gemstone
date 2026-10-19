package net.kurobako.gemstone.isa.amdgpu

import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.semantics.*

/** The staged semantics of register moves and instructions that move values between lanes. */
private object MoveStaged {

  inline def staged[V, R](inline algebra: Algebra[V], instruction: Instruction)(inline sinks: AMDGPUSinks[V, R])(
      otherwise: => R
  ): R = instruction match {
    case swap: Instruction.VSwapHalves =>
      sinks.section(
        sinks.swapHalves(swap)(
          (d, s) => algebra.mergeHalf(d, algebra.halfOf(s, swap.srcHigh), swap.destHigh),
          (s, d) => algebra.mergeHalf(s, algebra.halfOf(d, swap.destHigh), swap.srcHigh)
        )
      )
    case swap: Instruction.VPermlaneSwap => sinks.section(sinks.swapLanes(swap)(a => algebra.truncate(a, 32)))
    case permute @ Instruction.VPermuteLanes(mode = LanePermutation.Row) =>
      sinks.section(
        sinks.permuteLanes(permute)((lane, low, high) =>
          algebra.or(algebra.and(lane, algebra.literal(~15L)), algebra.laneNibble(lane, low, high))
        )(a => algebra.moved(a))
      )
    case permute @ Instruction.VPermuteLanes(mode = LanePermutation.CrossRow) =>
      sinks.section(
        sinks.permuteLanes(permute)((lane, low, high) =>
          algebra.or(
            algebra.xor(algebra.and(lane, algebra.literal(~15L)), algebra.literal(16L)),
            algebra.laneNibble(lane, low, high)
          )
        )(a => algebra.moved(a))
      )
    case permute @ Instruction.VPermuteLanes(mode = LanePermutation.RowVariable) =>
      sinks.section(
        sinks.permuteLanes(permute)((lane, selector, _) =>
          algebra.or(algebra.and(lane, algebra.literal(~15L)), algebra.rowLane(selector))
        )(a => algebra.moved(a))
      )
    case permute @ Instruction.VPermuteLanes(mode = LanePermutation.CrossRowVariable) =>
      sinks.section(
        sinks.permuteLanes(permute)((lane, selector, _) =>
          algebra.or(
            algebra.xor(algebra.and(lane, algebra.literal(~15L)), algebra.literal(16L)),
            algebra.rowLane(selector)
          )
        )(a => algebra.moved(a))
      )
    case permute @ Instruction.VPermuteLanes(mode = LanePermutation.HalfWave) =>
      sinks.section(
        sinks.permuteLanes(permute)((lane, _, _) => algebra.xor(lane, algebra.literal(32L)))(a => algebra.moved(a))
      )
    case Instruction.VMove64(dest, src) =>
      sinks.section(
        sinks.pairUnary(dest, src, PairRead.Integer, SourceModifiers.Unmodified)(a => algebra.moved64(a))
      )
    case Instruction.VAccumulatorRead(dest, src) =>
      sinks.section(
        sinks.accumulatorMove(RegisterBank.Vector, dest, src)(a => algebra.moved(a))
      )
    case Instruction.VAccumulatorWrite(dest, src) =>
      sinks.section(
        sinks.vectorUnary(RegisterBank.Accumulator, dest, src)(a => algebra.moved(a))
      )
    case Instruction.VAccumulatorMove(dest, src) =>
      sinks.section(
        sinks.accumulatorMove(RegisterBank.Accumulator, dest, src)(a => algebra.moved(a))
      )
    case Instruction.VMoveRelative(dest, src, field) =>
      sinks.section(
        sinks.moveRelative(dest, src, field)(a => algebra.moved(a))
      )
    case Instruction.VMoveRelativeDestination(dest, src, sourceField, field) =>
      sinks.section(
        sinks.moveRelativeDestination(dest, src, sourceField, field)(a => algebra.moved(a))
      )
    case Instruction.VSwapRelative(dest, src) =>
      sinks.section(
        sinks.swapRelative(dest, src)(a => algebra.moved(a))
      )
    case Instruction.VSwap(dest, src) =>
      sinks.section(
        sinks.swap(dest, src)(a => algebra.moved(a))
      )
    case routed: Instruction.VDPP =>
      routed.control match {
        case DPPControl.DPP16(dppControl, rowMask, bankMask, boundControl) =>
          sinks.section(
            sinks.dpp16(routed, rowMask, bankMask, boundControl)(lane => algebra.dppSource(dppControl, lane))(a =>
              algebra.moved(a)
            )
          )
        case DPPControl.DPP8(selectors) =>
          sinks.section(
            sinks.dpp8(routed)(lane => algebra.dpp8Source(algebra.literal(selectors.toLong), lane))(a =>
              algebra.moved(a)
            )
          )
      }
    case Instruction.VReadFirstLane(dest, src) =>
      sinks.section(
        sinks.readFirstLane(dest, src)(a => algebra.moved(a))
      )
    case Instruction.VReadLane(dest, src, lane) =>
      sinks.section(
        sinks.readLane(dest, src, lane)((index, lanes) => algebra.selectedLane(index, lanes))(a => algebra.moved(a))
      )
    case Instruction.VWriteLane(dest, src, lane) =>
      sinks.section(
        sinks.writeLane(dest, src, lane)((index, lanes) => algebra.selectedLane(index, lanes))(a => algebra.moved(a))
      )
    case Instruction.DSPermute(dest, address, src, offset, bank) =>
      sinks.section(
        sinks.permute(dest, address, src, offset, bank)((at, lanes) =>
          algebra.addressedLane(at, algebra.literal(offset.toLong), lanes)
        )(a => algebra.moved(a))
      )
    case Instruction.DSBPermute(dest, address, src, offset, bank, fetchInactive) =>
      sinks.section(
        sinks.backwardPermute(dest, address, src, offset, bank, fetchInactive)(at =>
          algebra.addressedLane(at, algebra.literal(offset.toLong), algebra.literal(64L))
        )(a => algebra.moved(a))
      )
    case Instruction.DSSwizzle(dest, src, control, bank) =>
      sinks.section(
        sinks.swizzle(dest, src, bank)(lane => algebra.swizzleSource(algebra.literal(control.toLong), lane))(a =>
          algebra.moved(a)
        )
      )
    case _ => otherwise
  }
}
