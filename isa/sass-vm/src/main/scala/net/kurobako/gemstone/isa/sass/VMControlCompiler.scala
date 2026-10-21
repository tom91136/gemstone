package net.kurobako.gemstone.isa.sass

import ModifierLowering.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Branches, calls, convergence and block barriers, cluster synchronisation, and warp-collective operations. */
private object VMControlCompiler {
  private val BranchModifiers  = Set(Modifier.U, Modifier.DIV, Modifier.CONV)
  private val ShuffleModifiers = Set(Modifier.UP, Modifier.DOWN, Modifier.IDX, Modifier.BFLY)
  private val ReduxModifiers   = Set(
    Modifier.MIN,
    Modifier.MAX,
    Modifier.SUM,
    Modifier.AND,
    Modifier.OR,
    Modifier.XOR,
    Modifier.U32,
    Modifier.S32
  )
  private val VoteModifiers             = Set(Modifier.ANY, Modifier.ALL, Modifier.EQ)
  private val BarrierReductionModifiers =
    Set(Modifier.RED, Modifier.OR, Modifier.AND, Modifier.POPC, Modifier.DEFER_BLOCKING)
  private val ReturnModifiers = Set(Modifier.ABS, Modifier.REL, Modifier.NODEC)

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    def barrier(operand: Operand): VMIR.Register = {
      if (!OperandQueries.isRegister(operand, RegisterBank.B)) unsupported("invalid convergence barrier register")
      registerLayout.barrier(
        OperandQueries.registerIndex(operand).refine[Int in 0 ~ 15].getOrElse(
          unsupported("convergence barrier register is outside B0-B15")
        )
      )
    }
    (operation.form: @unchecked) match {
      case _: Form.Nanosleep if modifiers == Vec(Modifier.WARP) =>
        def duration(value: Operand): Boolean = value match {
          case Operand.ConstantBank(bank, offset) => bank == 0 && OperandQueries.integer(offset).exists(_ >= 0)
          case _                                  => OperandQueries.isRegister(value, allowZero = true) ||
            OperandQueries.integer(value).exists(v => v >= 0 && v <= 0xffffffffL)
        }
        if (legacy || operands.size != 1 || !duration(operands.head) || instruction.guard.nonEmpty)
          unsupported("unsupported NANOSLEEP.WARP form")
        true
      // mbarrier.try_wait's suspend hint sleeps until the phase may have completed; copies complete as they issue.
      case _: Form.Nanosleep if modifiers == Vec(Modifier.SYNCS) => true
      // The PTX sleep is an approximate scheduling hint with a legal zero-delay outcome for every u32 duration.
      case _: Form.Nop | _: Form.Yield | _: Form.Nanosleep =>
        requireModifiers(Set.empty)
        true
      case _: Form.Bpt if modifiers == Vec(Modifier.TRAP) =>
        emit(Trap)
        true
      case _: Form.Bmov =>
        requireModifiers(Set(Modifier.`32`, Modifier.CLEAR))
        if (operands.size != 2 || !operands.exists(OperandQueries.isRegister(_, RegisterBank.B)))
          unsupported("barrier move requires a barrier register")
        def value(operand: Operand): Value.Register =
          if (OperandQueries.isRegister(operand, RegisterBank.B)) Value.Register(barrier(operand))
          else destination(operand)
        if (operands.head != OperandQueries.Zero)
          emit(Move(
            value(operands.head).slot,
            if (OperandQueries.isRegister(operands(1), RegisterBank.B)) value(operands(1)) else source(operands(1)),
            32
          ))
        if (modifiers.contains(Modifier.CLEAR)) {
          if (!OperandQueries.isRegister(operands(1), RegisterBank.B))
            unsupported("barrier clear requires a barrier source")
          emit(Move(barrier(operands(1)), Value.Immediate(0L), 32))
        }
        true
      case _: Form.Bssy =>
        requireModifiers(Set(Modifier.RECONVERGENT, Modifier.RELIABLE))
        emit(Convergence(barrier(operands.head), ConvergenceAction.Setup))
        true
      case _: Form.Bsync =>
        requireModifiers(Set(Modifier.RECONVERGENT, Modifier.RELIABLE))
        emit(Convergence(barrier(operands.head), ConvergenceAction.Synchronise))
        true
      case _: Form.Break =>
        requireModifiers(Set(Modifier.RELIABLE))
        // the predicated form leaves the convergence barrier only where the predicate holds
        operands match {
          case Vec(predicate, register) =>
            guarded(Convergence(barrier(register), ConvergenceAction.Break), predicateSource(predicate))
          case Vec(register) =>
            emit(Convergence(barrier(register), ConvergenceAction.Break))
          case _ => unsupported("BREAK requires a convergence barrier register")
        }
        true
      case _: Form.Endcollective =>
        requireModifiers(Set.empty)
        if (operands.nonEmpty) unsupported("ENDCOLLECTIVE operands")
        true
      case _: Form.Warpsync if modifiers == Vec(Modifier.COLLECTIVE) =>
        val mask   = source(operands.head)
        val member = binary(IntegerOperation.BitwiseAnd, mask, Value.LaneMaskEqual)
        val absent = compare(Comparison.Equal, NumericFormat.Unsigned(32), member, Value.Immediate(0L))
        branch(codeAddress(operands(1)), Some(ExecutionPredicate(absent.slot, negated = false)))
        emit(WarpBarrier(mask))
        true
      case _: Form.Warpsync if modifiers == Vec(Modifier.COLLECTIVE, Modifier.ALL) =>
        emit(WarpBarrier(Value.Immediate(0xffffffffL)))
        true
      case _: Form.Warpsync =>
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
      case _: Form.Bar =>
        blockBarrier()
        true
      case _: Form.B2r if modifiers == Vec(Modifier.RESULT) =>
        val reduced: Value.Predicate = Value.Predicate(VMRegisters.Predicate.Barrier.slot)
        if (operands.head != OperandQueries.Zero) store(operands.head, Value.Register(registerLayout.barrierResult))
        if (operands.size == 2 && operands(1) != OperandQueries.True) copyPredicate(predicate(operands(1)), reduced)
        true
      case _: Form.Jcal | _: Form.Call if textRelocation.exists(_.call) =>
        textRelocation.foreach(relocation => driverCall(relocation.symbol, None))
        true
      case _: Form.Cal | _: Form.Jcal | _: Form.Pret if legacy =>
        legacyCall()
        true
      case _: Form.Call
          if modifiers == Vec(Modifier.ABS, Modifier.NOINC) && operands.size == 1 &&
            OperandQueries.isRegister(operands.head) =>
        registerCall()
        true
      case _: Form.Call if modifiers.contains(Modifier.ABS) =>
        absoluteCall()
        true
      case _: Form.Call =>
        requireModifiers(Set(Modifier.REL, Modifier.NOINC))
        if (operands.size == 1) {
          val target = codeAddress(operands.head)
          if (!addresses.contains(target)) unsupported("external device call")
          branch(target)
        } else if (operands.size == 2)
          dispatch(binary(IntegerOperation.Add, source(operands.head), source(operands(1))), addresses)
        else unsupported("unsupported device call operands")
        true
      case _: Form.Brx | _: Form.Brxu | _: Form.Jmx | _: Form.Jmxu =>
        indirectBranch()
        true
      case _: Form.Lepc | _: Form.Ulepc =>
        programCounter()
        true
      case _: Form.Ret =>
        subroutineReturn()
        true
      case _: Form.Exit =>
        requireModifiers(Set.empty); emit(Return)
        true
      case _: Form.Bra | _: Form.Jmp
          if legacy && modifiers.size == 2 && modifiers.head == Modifier.CC &&
            PartialFunction.cond(modifiers.last) { case Modifier.EQ | Modifier.EQU | Modifier.NE | Modifier.NEU =>
              true
            } =>
        requireOperands(1, "condition-code branch requires a target")
        branch(
          codeAddress(operands.head),
          Some(ExecutionPredicate(
            readConditionFlag(VMConditionCodes.Flag.Zero).slot,
            PartialFunction.cond(modifiers.last) { case Modifier.NE | Modifier.NEU => true }
          ))
        )
        true
      case _: Form.Bra | _: Form.Jmp
          if !legacy && (modifiers == Vec(Modifier.DIV) || modifiers == Vec(Modifier.CONV)) =>
        convergenceBranch()
        true
      case _: Form.Bra | _: Form.Jmp if modifiers.contains(Modifier.ANY) =>
        requireModifiers(Set(Modifier.U, Modifier.ANY))
        requireOperands(1, "warp branch requires a target")
        val input = predicateSource(instruction.guard.map(_.predicate).getOrElse(OperandQueries.True))
        val taken = predicateTemp()
        emit(VotePredicate(taken.slot, input.slot, Value.ActiveMask, VoteOperation.Any))
        branch(codeAddress(operands.head), Some(ExecutionPredicate(taken.slot, negated = false)))
        true
      case _: Form.Bra | _: Form.Jmp =>
        requireModifiers(BranchModifiers)
        val target = codeAddress(operands.last)
        if (
          operands.size == 2 &&
          Set(OperandSelector.EQU, OperandSelector.NEU).exists(OperandQueries.has(operands.head, _))
        )
          branch(
            target,
            Some(ExecutionPredicate(
              readConditionFlag(VMConditionCodes.Flag.Zero).slot,
              operands.head != Operand.Qualified(Operand.Symbol("CC"), Vec(OperandSelector.EQU))
            ))
          )
        else if (
          operands.size == 2 &&
          operands.head != Operand.Unary(OperandQueries.UniformZero, negative = false, not = true, absolute = false)
        )
          branch(target, Some(executionPredicate(operands.head)))
        else branch(target)
        true
      // Dependent launches are serialised until kernel completion.
      case _: Form.Preexit =>
        requireModifiers(Set.empty)
        if (operands.nonEmpty) unsupported("dependent launch hint operands")
        true
      case _: Form.Ucgabar_Arv | _: Form.Ucgabararv =>
        requireModifiers(Set.empty)
        emit(ClusterBarrier(arrive = true))
        true
      case _: Form.Ucgabar_Wait | _: Form.Ucgabarwait =>
        requireModifiers(Set.empty)
        emit(ClusterBarrier(arrive = false))
        true
      case _: Form.Ugetnextworkid =>
        if ((modifiers != Vec(Modifier.SELFCAST) && modifiers != Vec(Modifier.BROADCAST)) || operands.size != 2)
          unsupported("unsupported cluster cancellation operands")
        val response = address(operands.head, global = false, sharedUniform = true, sharedAccess = true)
        val barrier  = address(operands(1), global = false, sharedUniform = true, sharedAccess = true)
        val elected  = predicateTemp()
        emit(Elect(None, elected.slot, Value.ActiveMask))
        guarded(MBarrierLoad(barrier), elected)
        guarded(ClusterTryCancel(response, barrier, modifiers == Vec(Modifier.BROADCAST)), elected)
        true
      case _: Form.Shfl =>
        requireModifiers(ShuffleModifiers)
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
        if (operands.head != OperandQueries.True) copyPredicate(predicate(operands.head), valid)
        true
      case _: Form.Redux | _: Form.Credux =>
        requireModifiers(ReduxModifiers)
        requireOperands(2, "expected two reduction operands")
        val reduction = modifiers.headOption.getOrElse(Modifier.AND)
        val mask      = activeMask()
        // ptxas spells the unsigned minimum and maximum without a type
        val signed = modifiers.contains(Modifier.S32)
        val result = temp()
        emit(WarpReduce(result.slot, source(operands(1)), mask, reduction.integerOperation(signed), 32))
        store(operands.head, result)
        true
      case _: Form.Elect
          if operands.size == 2 || (operands.size == 3 && operands(2) == OperandQueries.True) =>
        requireModifiers(Set.empty)
        emit(Elect(
          if (OperandQueries.isZero(operands(1))) None else Some(destination(operands(1)).slot),
          predicate(operands.head).slot,
          Value.ActiveMask
        ))
        true
      case _: Form.Elect =>
        electAmongCandidates()
        true
      case _: Form.Vote | _: Form.Voteu =>
        requireModifiers(VoteModifiers)
        val mask  = activeMask()
        val input = predicateSource(operands.last)
        if (operands.size == 3 && !OperandQueries.isZero(operands.head))
          emit(VoteBallot(destination(operands.head).slot, input.slot, mask))
        val output = if (operands.size == 2) operands.head else operands(1)
        if (!OperandQueries.isAlwaysPredicate(output))
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

  /** BAR.SYNC, BAR.ARV and BAR.RED. A reduction leaves its result both as the barrier predicate and, as a count or 0 or
    * 1, in the register B2R reads.
    */
  private def blockBarrier()(using context: VMCompilationContext): Unit = {
    import context.*
    if (modifiers.contains(Modifier.RED)) {
      requireModifiers(BarrierReductionModifiers)
      if (operands.size < 2 || operands.size > 3)
        unsupported("block barrier reduction requires an id, count and predicate")
      val reduced: Value.Predicate = Value.Predicate(VMRegisters.Predicate.Barrier.slot)
      val counted: Value.Register  = Value.Register(registerLayout.barrierResult)
      val (id, count)              = barrierOperands(operands.head, Option.when(operands.size == 3)(operands(1)))
      if (modifiers.contains(Modifier.POPC)) {
        emit(BarrierReduce(BarrierResult.Count(counted.slot), id, predicateSource(operands.last).slot, count))
        copyPredicate(reduced, compare(Comparison.NotEqual, NumericFormat.Unsigned(32), counted, Value.Immediate(0L)))
      } else {
        emit(BarrierReduce(
          BarrierResult.Combined(
            reduced.slot,
            if (modifiers.contains(Modifier.OR)) PredicateOperation.Or else PredicateOperation.And
          ),
          id,
          predicateSource(operands.last).slot,
          count
        ))
        emit(Select(counted.slot, Value.Immediate(1L), Value.Immediate(0L), reduced.slot, 32))
      }
    } else if (modifiers == Vec(Modifier.ARV)) {
      requireOperands(2, "BAR.ARV requires an id and a thread count")
      val (id, count) = barrierOperands(operands.head, Some(operands(1)))
      emit(BarrierArrive(id, count.getOrElse(barrierValue(operands(1)))))
    } else {
      if (modifiers != Vec(Modifier.SYNC, Modifier.DEFER_BLOCKING) && modifiers != Vec(Modifier.SYNC))
        unsupported("unsupported block barrier")
      if (operands.size < 1 || operands.size > 2) unsupported("block barrier requires an id and optional count")
      val (id, count) = barrierOperands(operands.head, operands.lift(1))
      emit(BarrierSync(id, count))
    }
  }

  /** A block barrier's id and thread count. ptxas packs a register id and a register count into one register, the count
    * in bits 16 to 27, and names it as both operands; an immediate id takes the count register as it is.
    */
  private def barrierOperands(id: Operand, count: Option[Operand])(using
      context: VMCompilationContext
  ): (Value, Option[Value]) = {
    import context.*
    count match {
      case Some(threads) if OperandQueries.isRegister(id) && OperandQueries.isRegister(threads) =>
        val field = binary(IntegerOperation.ShiftRightLogical, source(threads), Value.Immediate(16L))
        (
          binary(IntegerOperation.BitwiseAnd, source(id), Value.Immediate(0xffffL)),
          Some(binary(IntegerOperation.BitwiseAnd, field, Value.Immediate(0xfffL)))
        )
      case _ => (barrierValue(id), count.map(barrierValue(_)))
    }
  }

  /** BRA.DIV branches unless every live lane of its mask is active; BRA.CONV branches when they all are. */
  private def convergenceBranch()(using context: VMCompilationContext): Unit = {
    import context.*
    if (operands.size != 1 && operands.size != 2) unsupported("convergence branch operands")
    val mask   = if (operands.size == 2) source(operands.head) else Value.Immediate(0xffffffffL)
    val live   = binary(IntegerOperation.BitwiseAnd, mask, Value.LiveMask)
    val active = binary(IntegerOperation.BitwiseAnd, mask, Value.ActiveMask)
    val all    = compare(Comparison.Equal, NumericFormat.Unsigned(32), live, active)
    branch(codeAddress(operands.last), Some(ExecutionPredicate(all.slot, modifiers.head == Modifier.DIV)))
  }

  /** A legacy call pushes the address after it; PRET pushes its operand, the return address of the indirect call that
    * follows.
    */
  private def legacyCall()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set.empty)
    requireOperands(1, "unsupported legacy call operands")
    val target = codeAddress(operands.head)
    if (!addresses.contains(target)) unsupported("external device call or return address")
    val call = !OperandQueries.is[Form.Pret](operation)
    emit(ReturnStackPush(
      if (call)
        addresses.find(_ > instruction.address).getOrElse(unsupported("call has no return instruction")).toLong
      else target.toLong
    ))
    if (call) branch(target)
  }

  private def subroutineReturn()(using context: VMCompilationContext): Unit = {
    import context.*
    if (legacy && operands.isEmpty) {
      requireModifiers(Set.empty)
      val target = temp()
      emit(ReturnStackPop(target.slot))
      dispatch(target, returnBases.distinct.filter(addresses.contains))
    } else {
      requireModifiers(ReturnModifiers)
      if (operands.size != 2 || returnBases.isEmpty) unsupported("unsupported return")
      val target  = binary(IntegerOperation.Add, source(operands.head), source(operands(1)))
      val targets =
        if (modifiers.contains(Modifier.REL)) returnBases else returnBases.map(_ + number(operands(1)).toInt)
      dispatch(target, targets.distinct.filter(addresses.contains))
    }
  }

  /** An absolute call through a register to the driver's device runtime, whose functions the relocated bank 4 supplies,
    * or to code in this kernel.
    */
  private def registerCall()(using context: VMCompilationContext): Unit = {
    import context.*
    val target = source(operands.head, 64)
    val next   = instruction.address.raw + generation.instructionBytes.raw
    val resume = addresses.contains(next)
    Vec.from(Cubin.DriverFunctions).sortBy(_._2).foreach { (name, address) =>
      val reached = compare(Comparison.Equal, NumericFormat.Unsigned(64), target, Value.Immediate(address))
      val guard   = ExecutionPredicate(reached.slot, negated = false)
      driverCall(name, Some(guard))
      if (resume) branch(next.toLong, Some(guard))
    }
    dispatch(binary(IntegerOperation.Add, target, Value.Immediate(0L)), addresses)
  }

  /** Code addresses are offsets in the function's section, as relocated return and target addresses resolve. */
  private def absoluteCall()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.ABS, Modifier.NOINC))
    operands match {
      case Vec(target) if OperandQueries.integer(target).nonEmpty =>
        if (!addresses.contains(codeAddress(target))) unsupported("external device call")
        branch(codeAddress(target))
      case Vec(target, offset) if codeRegister(target) =>
        dispatch(binary(IntegerOperation.Add, source(target), source(offset)), addresses)
      case Vec(target) if codeRegister(target) =>
        dispatch(binary(IntegerOperation.Add, source(target), Value.Immediate(0L)), addresses)
      case _ => unsupported("unsupported absolute device call operands")
    }
  }

  /** BRX and BRXU add the following instruction's address to their target; JMX and JMXU do not. */
  private def indirectBranch()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set.empty)
    if (operands.isEmpty || operands.size > 2 || !codeRegister(operands.head))
      unsupported("unsupported indirect branch operands")
    val offset   = operands.lift(1).fold(0L)(number)
    val relative = OperandQueries.is[Form.Brx](operation) ||
      OperandQueries.is[Form.Brxu](operation)
    val base = if (relative) instruction.address.raw + generation.instructionBytes.raw else 0
    dispatch(binary(IntegerOperation.Add, source(operands.head), Value.Immediate(base + offset)), addresses)
  }

  /** The 64-bit address of this instruction, or of the target it names, which return-address arithmetic reads as a
    * register pair.
    */
  private def programCounter()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set.empty)
    if (operands.isEmpty || operands.size > 2) unsupported("unsupported program counter operands")
    val address = operands.lift(1).fold(instruction.address.toLong)(codeAddress(_).toLong)
    store(operands.head, Value.Immediate(address), if (legacy) 32 else 64)
  }

  /** A call to the driver's device runtime: `malloc` and `free` on the device heap, `vprintf`, whose output the VM
    * discards as PTX `vprintf` does, returning 0, and `__assertfail`, which fails the launch as a failed device
    * assertion does. Arguments and results are in R4 onwards.
    */
  private def driverCall(name: String, guard: Option[ExecutionPredicate])(using context: VMCompilationContext): Unit = {
    import context.*
    def step(instruction: VMIR.Instruction): Unit = guard.fold(emit(instruction))(predicate =>
      guarded(instruction, Value.Predicate(predicate.predicate), predicate.negated)
    )
    val argument                            = Parameter(0)
    val result                              = Parameter(1)
    def register(index: Int): VMIR.Register = destination(Operand.Register(RegisterBank.R, index.unsafe)).slot
    name match {
      case "malloc" =>
        step(StoreParameter(argument, 0, source(Operand.Register(RegisterBank.R, 4), 64), 64))
        step(CallBuiltin(Builtin.Malloc, Vec(result), Vec(argument)))
        step(LoadParameter(Vec(register(4)), result, 0, 32, signed = false))
        step(LoadParameter(Vec(register(5)), result, 4, 32, signed = false))
      case "free" =>
        step(StoreParameter(argument, 0, source(Operand.Register(RegisterBank.R, 4), 64), 64))
        step(CallBuiltin(Builtin.Free, Vec.empty, Vec(argument)))
      case "vprintf"      => step(Move(register(4), Value.Immediate(0L), 32))
      case "__assertfail" => step(CallBuiltin(Builtin.AssertFail, Vec.empty, Vec.empty))
      case _              => step(Trap)
    }
  }

  private def codeRegister(operand: Operand): Boolean =
    OperandQueries.isRegister(operand) || OperandQueries.isRegister(operand, RegisterBank.UR)

  /** The active lanes, captured before the operation can change them. */
  private def activeMask()(using context: VMCompilationContext): Value.Register = {
    val mask = context.temp()
    context.emit(Move(mask.slot, Value.ActiveMask, 32))
    mask
  }

  /** ELECT with candidates: the lowest active lane among those a predicate or a uniform lane mask names leads; with no
    * candidate, none does.
    */
  private def electAmongCandidates()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set.empty)
    requireOperands(3, "ELECT requires a predicate, a destination and candidates")
    val candidates = temp()
    operands(2) match {
      case candidate @ Operand.Predicate(_, _, _) =>
        emit(VoteBallot(candidates.slot, predicateSource(candidate).slot, Value.ActiveMask))
      case mask => emit(IntegerBinary(IntegerOperation.BitwiseAnd, candidates.slot, source(mask), Value.ActiveMask, 32))
    }
    val reversed = temp()
    emit(IntegerUnary(IntegerUnaryOperation.BitReverse, reversed.slot, candidates, 32))
    val leader = temp()
    emit(IntegerUnary(IntegerUnaryOperation.LeadingZeroCount, leader.slot, reversed, 32))
    val leads = compare(Comparison.Equal, NumericFormat.Unsigned(32), Value.LaneId, leader)
    if (!OperandQueries.isAlwaysPredicate(operands.head)) copyPredicate(predicate(operands.head), leads)
    store(operands(1), leader)
  }
}
