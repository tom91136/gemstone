package net.kurobako.gemstone.isa.sass

import ModifierSemantics.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Branches, calls, convergence and block barriers, cluster synchronisation, and warp-collective operations. */
private object VMControlCompiler {

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    def barrier(operand: Operand): VMIR.Register = {
      if (!VMOperand.isRegister(operand, RegisterBank.B)) unsupported("invalid convergence barrier register")
      registerLayout.barrier(
        VMOperand.registerIndex(operand).raw.refine[Int in 0 ~ 15].getOrElse(
          unsupported("convergence barrier register is outside B0-B15")
        )
      )
    }
    (operation: @unchecked) match {
      case _: SASSInstruction.Nanosleep if modifiers == Vec(Modifier.WARP) =>
        def duration(value: Operand): Boolean = value match {
          case Operand.ConstantBank(bank, offset) => bank == 0 && VMOperand.integer(offset).exists(_ >= 0)
          case _                                  => VMOperand.isRegister(value, allowZero = true) ||
            VMOperand.integer(value).exists(v => v >= 0 && v <= 0xffffffffL)
        }
        if (legacy || operands.size != 1 || !duration(operands.head) || instruction.guard.nonEmpty)
          unsupported("unsupported NANOSLEEP.WARP form")
        true
      // The PTX sleep is an approximate scheduling hint with a legal zero-delay outcome for every u32 duration.
      case _: SASSInstruction.Nop | _: SASSInstruction.Yield | _: SASSInstruction.Nanosleep =>
        requireModifiers(Set.empty)
        true
      case _: SASSInstruction.Bpt if modifiers == Vec(Modifier.TRAP) =>
        emit(Trap)
        true
      case _: SASSInstruction.Bmov =>
        requireModifiers(Set(Modifier.`32`, Modifier.CLEAR))
        if (operands.size != 2 || !operands.exists(VMOperand.isRegister(_, RegisterBank.B)))
          unsupported("barrier move requires a barrier register")
        def value(operand: Operand): Value.Register =
          if (VMOperand.isRegister(operand, RegisterBank.B)) Value.Register(barrier(operand)) else destination(operand)
        if (operands.head != VMOperand.Zero)
          emit(Move(
            value(operands.head).slot,
            if (VMOperand.isRegister(operands(1), RegisterBank.B)) value(operands(1)) else source(operands(1)),
            32
          ))
        if (modifiers.contains(Modifier.CLEAR)) {
          if (!VMOperand.isRegister(operands(1), RegisterBank.B)) unsupported("barrier clear requires a barrier source")
          emit(Move(barrier(operands(1)), Value.Immediate(0L), 32))
        }
        true
      case _: SASSInstruction.Bssy =>
        requireModifiers(Set(Modifier.RECONVERGENT, Modifier.RELIABLE))
        emit(Convergence(barrier(operands.head), ConvergenceAction.Setup))
        true
      case _: SASSInstruction.Bsync =>
        requireModifiers(Set(Modifier.RECONVERGENT, Modifier.RELIABLE))
        emit(Convergence(barrier(operands.head), ConvergenceAction.Synchronise))
        true
      case _: SASSInstruction.Break =>
        requireModifiers(Set(Modifier.RELIABLE))
        emit(Convergence(barrier(operands.head), ConvergenceAction.Break))
        true
      case _: SASSInstruction.Endcollective =>
        requireModifiers(Set.empty)
        if (operands.nonEmpty) unsupported("ENDCOLLECTIVE operands")
        true
      case _: SASSInstruction.Warpsync if modifiers == Vec(Modifier.COLLECTIVE) =>
        val mask   = source(operands.head)
        val member = binary(IntegerOperation.BitwiseAnd, mask, Value.LaneMaskEqual)
        val absent = compare(Comparison.Equal, NumericFormat.Unsigned(32), member, Value.Immediate(0L))
        branch(codeAddress(operands(1)), Some(ExecutionPredicate(absent.slot, false)))
        emit(WarpBarrier(mask))
        true
      case _: SASSInstruction.Warpsync if modifiers == Vec(Modifier.COLLECTIVE, Modifier.ALL) =>
        emit(WarpBarrier(Value.Immediate(0xffffffffL)))
        true
      case _: SASSInstruction.Warpsync =>
        requireModifiers(Set(Modifier.ALL))
        val mask =
          if (modifiers.contains(Modifier.ALL)) {
            if (operands.nonEmpty) unsupported("WARPSYNC.ALL operands")
            Value.Immediate(0xffffffffL)
          } else {
            requireOperands(1, "WARPSYNC requires a member mask")
            source(operands.head)
          }
        emit(WarpBarrier(mask))
        true
      case _: SASSInstruction.Bar if modifiers.contains(Modifier.RED) =>
        requireModifiers(Set(Modifier.RED, Modifier.OR, Modifier.AND, Modifier.DEFER_BLOCKING))
        emit(BarrierReduce(
          VMRegisters.Predicate.Barrier.slot,
          Value.Immediate(barrierId(operands.head).raw.toLong),
          predicateSource(operands(1)).slot,
          if (modifiers.contains(Modifier.OR)) PredicateOperation.Or else PredicateOperation.And
        ))
        true
      case _: SASSInstruction.B2r if modifiers == Vec(Modifier.RESULT) =>
        val reduced: Value.Predicate = Value.Predicate(VMRegisters.Predicate.Barrier.slot)
        if (operands.head != VMOperand.Zero) store(operands.head, bit(reduced))
        if (operands.size == 2 && operands(1) != VMOperand.True) copyPredicate(predicate(operands(1)), reduced)
        true
      case _: SASSInstruction.Bar =>
        if (modifiers != Vec(Modifier.SYNC, Modifier.DEFER_BLOCKING) && modifiers != Vec(Modifier.SYNC))
          unsupported("unsupported block barrier")
        if (operands.size < 1 || operands.size > 2) unsupported("block barrier requires an id and optional count")
        def value(operand: Operand): Value =
          if (VMOperand.isRegister(operand, RegisterBank.R)) source(operand)
          else Value.Immediate(barrierId(operand).raw.toLong)
        emit(BarrierSync(value(operands.head), operands.lift(1).map(value(_))))
        true
      case _: SASSInstruction.Cal | _: SASSInstruction.Pret if legacy =>
        requireModifiers(Set.empty)
        requireOperands(1, "unsupported legacy call operands")
        val target = codeAddress(operands.head)
        if (!addresses.contains(target)) unsupported("external device call or return address")
        val call = VMOperand.is[SASSInstruction.Cal](operation)
        emit(ReturnStackPush(
          if (call)
            addresses.find(_ > instruction.address.raw).getOrElse(unsupported("call has no return instruction")).toLong
          else target.toLong
        ))
        if (call) branch(target)
        true
      case _: SASSInstruction.Call =>
        requireModifiers(Set(Modifier.REL, Modifier.NOINC))
        if (operands.size == 1) {
          val target = codeAddress(operands.head)
          if (!addresses.contains(target)) unsupported("external device call")
          branch(target)
        } else if (operands.size == 2)
          dispatch(binary(IntegerOperation.Add, source(operands.head), source(operands(1))), addresses)
        else unsupported("unsupported device call operands")
        true
      case _: SASSInstruction.Brx | _: SASSInstruction.Brxu =>
        requireModifiers(Set.empty)
        requireOperands(2, "unsupported indirect branch operands")
        val next = instruction.address.raw + generation.instructionBytes.raw
        dispatch(
          binary(IntegerOperation.Add, source(operands.head), Value.Immediate(next + number(operands(1)))),
          addresses
        )
        true
      case _: SASSInstruction.Lepc =>
        requireModifiers(Set.empty)
        store(operands.head, Value.Immediate(instruction.address.raw.toLong))
        true
      case _: SASSInstruction.Ret if legacy && operands.isEmpty =>
        requireModifiers(Set.empty)
        val target = temp()
        emit(ReturnStackPop(target.slot))
        dispatch(target, returnBases.distinct.filter(addresses.contains))
        true
      case _: SASSInstruction.Ret =>
        requireModifiers(Set(Modifier.ABS, Modifier.REL, Modifier.NODEC))
        if (operands.size != 2 || returnBases.isEmpty) unsupported("unsupported return")
        val target  = binary(IntegerOperation.Add, source(operands.head), source(operands(1)))
        val targets =
          if (modifiers.contains(Modifier.REL)) returnBases else returnBases.map(_ + number(operands(1)).toInt)
        dispatch(target, targets.distinct.filter(addresses.contains))
        true
      case _: SASSInstruction.Exit =>
        requireModifiers(Set.empty); emit(Return)
        true
      case _: SASSInstruction.Bra
          if legacy && modifiers.size == 2 && modifiers.head == Modifier.CC &&
            Set(Modifier.EQ, Modifier.EQU, Modifier.NE, Modifier.NEU)(modifiers.last) =>
        requireOperands(1, "condition-code branch requires a target")
        branch(
          codeAddress(operands.head),
          Some(ExecutionPredicate(
            readConditionFlag(VMConditionCodes.Flag.Zero).slot,
            Set(Modifier.NE, Modifier.NEU)(modifiers.last)
          ))
        )
        true
      case _: SASSInstruction.Bra if !legacy && (modifiers == Vec(Modifier.DIV) || modifiers == Vec(Modifier.CONV)) =>
        if (operands.size != 1 && operands.size != 2) unsupported("convergence branch operands")
        val mask   = if (operands.size == 2) source(operands.head) else Value.Immediate(0xffffffffL)
        val live   = binary(IntegerOperation.BitwiseAnd, mask, Value.LiveMask)
        val active = binary(IntegerOperation.BitwiseAnd, mask, Value.ActiveMask)
        val all    = compare(Comparison.Equal, NumericFormat.Unsigned(32), live, active)
        branch(codeAddress(operands.last), Some(ExecutionPredicate(all.slot, modifiers.head == Modifier.DIV)))
        true
      case _: SASSInstruction.Bra if modifiers.contains(Modifier.ANY) =>
        requireModifiers(Set(Modifier.U, Modifier.ANY))
        requireOperands(1, "warp branch requires a target")
        val input = predicateSource(instruction.guard.map(_.predicate).getOrElse(VMOperand.True))
        val taken = predicateTemp()
        emit(VotePredicate(taken.slot, input.slot, Value.ActiveMask, VoteOperation.Any))
        branch(codeAddress(operands.head), Some(ExecutionPredicate(taken.slot, false)))
        true
      case _: SASSInstruction.Bra =>
        requireModifiers(Set(Modifier.U, Modifier.DIV, Modifier.CONV))
        val target = codeAddress(operands.last)
        if (operands.size == 2 && Set(OperandSelector.EQU, OperandSelector.NEU).exists(VMOperand.has(operands.head, _)))
          branch(
            target,
            Some(ExecutionPredicate(
              readConditionFlag(VMConditionCodes.Flag.Zero).slot,
              operands.head != Operand.Qualified(Operand.Symbol("CC"), Vec(OperandSelector.EQU))
            ))
          )
        else if (operands.size == 2 && operands.head != Operand.Unary(VMOperand.UniformZero, false, true, false))
          branch(target, Some(executionPredicate(operands.head)))
        else branch(target)
        true
      // Dependent launches are serialised until kernel completion.
      case _: SASSInstruction.Preexit =>
        requireModifiers(Set.empty)
        if (operands.nonEmpty) unsupported("dependent launch hint operands")
        true
      case _: SASSInstruction.Ucgabar_Arv | _: SASSInstruction.Ucgabararv =>
        requireModifiers(Set.empty)
        emit(ClusterBarrier(arrive = true))
        true
      case _: SASSInstruction.Ucgabar_Wait | _: SASSInstruction.Ucgabarwait =>
        requireModifiers(Set.empty)
        emit(ClusterBarrier(arrive = false))
        true
      case _: SASSInstruction.Ugetnextworkid =>
        if (modifiers != Vec(Modifier.SELFCAST) || operands.size != 2)
          unsupported("unsupported cluster cancellation operands")
        val response = address(operands.head, false, sharedUniform = true, sharedAccess = true)
        val barrier  = address(operands(1), false, sharedUniform = true, sharedAccess = true)
        val elected  = predicateTemp()
        emit(Elect(None, elected.slot, Value.ActiveMask))
        guarded(MBarrierLoad(barrier), elected)
        guarded(ClusterTryCancel(response, barrier), elected)
        true
      case _: SASSInstruction.Shfl =>
        requireModifiers(Set(Modifier.UP, Modifier.DOWN, Modifier.IDX, Modifier.BFLY))
        val result = temp()
        val valid  = predicateTemp()
        val mode   = modifiers(0) match {
          case Modifier.DOWN => ShuffleMode.Down
          case Modifier.UP   => ShuffleMode.Up
          case Modifier.IDX  => ShuffleMode.Index
          case Modifier.BFLY => ShuffleMode.Butterfly
          case other         => unsupported(s"unsupported SASS shuffle mode $other")
        }
        emit(Shuffle(
          mode,
          result.slot,
          Some(valid.slot),
          source(operands(2)),
          source(operands(3)),
          source(operands(4)),
          activeMask(),
          32
        ))
        store(operands(1), result)
        if (operands.head != VMOperand.True) copyPredicate(predicate(operands.head), valid)
        true
      case _: SASSInstruction.Redux | _: SASSInstruction.Credux =>
        requireModifiers(Set(
          Modifier.MIN,
          Modifier.MAX,
          Modifier.SUM,
          Modifier.AND,
          Modifier.OR,
          Modifier.XOR,
          Modifier.U32,
          Modifier.S32
        ))
        requireOperands(2, "expected two reduction operands")
        val reduction = modifiers.headOption.getOrElse(Modifier.AND)
        val mask      = activeMask()
        val signed    =
          if (VMOperand.is[SASSInstruction.Credux](operation)) modifiers.contains(Modifier.S32)
          else !Set(Modifier.AND, Modifier.OR, Modifier.XOR, Modifier.SUM)(reduction) &&
          !modifiers.contains(Modifier.U32)
        val result = temp()
        emit(WarpReduce(result.slot, source(operands(1)), mask, integerOperation(reduction, signed), 32))
        store(operands.head, result)
        true
      case _: SASSInstruction.Elect =>
        requireModifiers(Set.empty)
        if (operands.size != 3 || operands(2) != VMOperand.True)
          unsupported("unsupported leader election candidate predicate")
        emit(Elect(
          if (VMOperand.isZero(operands(1))) None else Some(destination(operands(1)).slot),
          predicate(operands.head).slot,
          Value.ActiveMask
        ))
        true
      case _: SASSInstruction.Vote | _: SASSInstruction.Voteu =>
        requireModifiers(Set(Modifier.ANY, Modifier.ALL, Modifier.EQ))
        val mask  = activeMask()
        val input = predicateSource(operands.last)
        if (operands.size == 3 && !VMOperand.isZero(operands.head))
          emit(VoteBallot(destination(operands.head).slot, input.slot, mask))
        val output = if (operands.size == 2) operands.head else operands(1)
        if (!VMOperand.isAlwaysPredicate(output))
          emit(VotePredicate(
            predicate(output).slot,
            input.slot,
            mask,
            modifiers(0) match {
              case Modifier.EQ  => VoteOperation.Uniform
              case Modifier.ALL => VoteOperation.All
              case Modifier.ANY => VoteOperation.Any
              case other        => unsupported(s"unsupported SASS vote operation ${other.name}")
            }
          ))
        true
      case _ => false
    }
  }

  /** The active lanes, captured before the operation can change them. */
  private def activeMask()(using context: VMCompilationContext): Value.Register = {
    val mask = context.temp()
    context.emit(Move(mask.slot, Value.ActiveMask, 32))
    mask
  }
}
