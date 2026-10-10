package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

/** Conservative opaque-predicate proof for CUDA 11.8 Maxwell/Pascal generic-address sequences. */
private object LegacyAddressRewrite {
  private val Comparisons = Set(
    Instruction.Modifier.`EQ`,
    Instruction.Modifier.`NE`,
    Instruction.Modifier.`LT`,
    Instruction.Modifier.`LE`,
    Instruction.Modifier.`GT`,
    Instruction.Modifier.`GE`
  )
  private val GenericMemoryModifiers = (for {
    cache <- Vec(Vec.empty[Instruction.Modifier], Vec(Instruction.Modifier.CG), Vec(Instruction.Modifier.LU))
    width <- Vec(
      Vec.empty[Instruction.Modifier],
      Vec(Instruction.Modifier.U8),
      Vec(Instruction.Modifier.S8),
      Vec(Instruction.Modifier.U16),
      Vec(Instruction.Modifier.S16),
      Vec(Instruction.Modifier.`64`),
      Vec(Instruction.Modifier.`128`)
    )
  } yield Vec(Instruction.Modifier.E) ++ cache ++ width).toSet

  private def hasModifier(instruction: DecodedInstruction, modifier: Instruction.Modifier): Boolean =
    instruction.modifiers.contains(modifier)

  /** Registers a load writes or a store reads. */
  private def accessWords(instruction: DecodedInstruction): Int =
    if (hasModifier(instruction, Instruction.Modifier.`128`)) 4
    else if (hasModifier(instruction, Instruction.Modifier.`64`)) 2
    else 1

  private def controlFlowTargets(instructions: Vec[DecodedInstruction]): Set[Int] =
    instructions.flatMap(codeTargets(_)).toSet ++
      LegacyControlFlow.functionEntries(instructions).map(instructions(_).address.raw)

  private def codeTargets(instruction: DecodedInstruction): Set[Int] =
    if (!isControlFlow(instruction)) Set.empty
    else instruction.operands.flatMap(
      OperandQueries.integer(_).filter(value => value >= 0 && value <= Int.MaxValue).map(_.toInt)
    ).toSet

  private def isLea(instruction: DecodedInstruction): Boolean =
    OperandQueries.is[Instruction.Lea](instruction.operation)
  private def isNop(instruction: DecodedInstruction): Boolean =
    OperandQueries.is[Instruction.Nop](instruction.operation)
  private def isMemBar(instruction: DecodedInstruction): Boolean =
    OperandQueries.is[Instruction.Membar](instruction.operation)
  private def isLoad(instruction: DecodedInstruction): Boolean =
    OperandQueries.is[Instruction.Ld](instruction.operation)
  private def isStore(instruction: DecodedInstruction): Boolean =
    OperandQueries.is[Instruction.St](instruction.operation)
  private def isPredicateBankTransfer(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.P2r | _: Instruction.R2p => true
    case _                                       => false
  }
  private def isIndirectTransfer(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Brx | _: Instruction.Brxu | _: Instruction.Jmx => true
    case _: Instruction.Jmp | _: Instruction.Call | _: Instruction.Cal =>
      instruction.operands.exists(isGeneralRegister(_))
    case _ => false
  }
  private def isPredicateSetter(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Isetp | _: Instruction.Uisetp | _: Instruction.Fsetp | _: Instruction.Dsetp |
        _: Instruction.Psetp | _: Instruction.Hsetp2 => true
    case _ => false
  }
  private def isPredicateForm(instruction: DecodedInstruction): Boolean =
    isAuxiliaryHigh(instruction) && instruction.operands.size == 5
  private def isScalarStore(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Sts | _: Instruction.Stl => true
    case _                                       => false
  }
  private def isReconvergenceSetup(instruction: DecodedInstruction): Boolean =
    LegacyControlFlow.setupKind(instruction).nonEmpty
  private def isAdd(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Iadd | _: Instruction.Iadd32i => true
    case _                                            => false
  }
  private def isControlFlow(instruction: DecodedInstruction): Boolean = ControlFlow.transfers(instruction.operation)

  private def predicate(operand: Operand): Option[Int] = operand match {
    case Operand.Predicate(Some(index), false, _) => Some(index.raw)
    case _                                        => None
  }
  private def positivePredicate(operand: Operand): Option[Int] = operand match {
    case Operand.Predicate(Some(index), false, false) => Some(index.raw)
    case _                                            => None
  }
  private def isPredicateOperand(operand: Operand): Boolean = operand match {
    case Operand.Predicate(Some(_), false, _) => true
    case Operand.Predicate(None, false, _)    => true
    case Operand.Symbol("PR")                 => true
    case _                                    => false
  }
  @annotation.tailrec
  private def generalRegisterBase(operand: Operand, allowZero: Boolean): Boolean =
    OperandQueries.without(operand, OperandSelector.reuse) match {
      case Operand.Register(RegisterBank.R, index) => allowZero || index.raw != 255
      case Operand.Qualified(inner, _)             => generalRegisterBase(inner, allowZero)
      case _                                       => false
    }
  private def isGeneralRegister(operand: Operand, allowZero: Boolean = false): Boolean =
    OperandQueries.qualifiers(operand).isEmpty && generalRegisterBase(operand, allowZero)
  @annotation.tailrec
  private def registerIndex(operand: Operand): Option[Int in 0 ~ 255] =
    OperandQueries.without(operand, OperandSelector.reuse) match {
      case Operand.Register(RegisterBank.R, index) => Some(index)
      case Operand.Qualified(inner, _)             => registerIndex(inner)
      case _                                       => None
    }
  private def isRegisterDestination(operand: Operand, allowZero: Boolean, allowCarry: Boolean): Boolean = {
    val suffixes = OperandQueries.qualifiers(operand)
    (suffixes.isEmpty || (allowCarry && suffixes == Vec(OperandSelector.CC))) && generalRegisterBase(operand, allowZero)
  }
  private def isIdentityMove(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Mov if instruction.modifiers.isEmpty && instruction.operands.size == 2 =>
      val destination = OperandQueries.without(instruction.operands(0), OperandSelector.reuse)
      isGeneralRegister(destination) &&
      destination == OperandQueries.without(instruction.operands(1), OperandSelector.reuse)
    case _ => false
  }
  private def predicateGuardReads(instruction: DecodedInstruction, target: Int): Boolean =
    instruction.guard.exists(guard => predicate(guard.predicate).contains(target))

  private def isCarryWriter(instruction: DecodedInstruction): Boolean =
    hasModifier(instruction, Instruction.Modifier.`CC`) ||
      (hasModifier(instruction, Instruction.Modifier.`X`) &&
        !OperandQueries.is[Instruction.Isetp](instruction.operation)) ||
      instruction.operands.headOption.exists(OperandQueries.has(_, OperandSelector.CC))

  private def predicateDestinationIndices(instruction: DecodedInstruction): Set[Int] =
    if (isPredicateSetter(instruction)) Set(0, 1)
    else if (isPredicateForm(instruction)) Set(0)
    else instruction.operation match {
      case _: Instruction.Lop if instruction.operands.headOption.exists(positivePredicate(_).nonEmpty) => Set(0)
      case _: Instruction.Vote => Set(if (instruction.operands.size == 2) 0 else 1)
      case _: Instruction.Shfl => Set(0)
      case _                   => Set.empty
    }

  private def isPredicateDefinition(instruction: DecodedInstruction, target: Int): Boolean =
    instruction.operands.zipWithIndex.exists { case (operand, index) =>
      positivePredicate(operand).contains(target) && predicateDestinationIndices(instruction)(index)
    }

  private def hasPredicateRead(instruction: DecodedInstruction, target: Int): Boolean =
    predicateGuardReads(instruction, target) ||
      instruction.operands.zipWithIndex.exists { case (operand, index) =>
        predicate(operand).contains(target) && !predicateDestinationIndices(instruction)(index)
      }

  private def isAddress(operand: Operand, register: Int in 0 ~ 255): Boolean = operand match {
    case Operand.Address(Some(Operand.Register(RegisterBank.R, baseIndex)), None, displacement, scale, None, None) =>
      scale.raw == 1 && baseIndex == register
    case _ => false
  }

  private val PaddingImmediate: InstructionOperand.Immediate =
    InstructionOperand.Immediate.from(Operand.Integer(0)).fold(
      error => throw IllegalStateException(error),
      identity
    )

  private def isGenericMemory(
      instruction: DecodedInstruction,
      target: Int,
      lowAddress: Int in 0 ~ 255
  ): Boolean = {
    val args = instruction.operands
    args.size == 3 && positivePredicate(args.last).contains(target) && GenericMemoryModifiers(instruction.modifiers) &&
    !(isStore(instruction) &&
      instruction.modifiers.lastOption.exists(Set(Instruction.Modifier.S8, Instruction.Modifier.S16))) &&
    ((isLoad(instruction) && isAddress(args(1), lowAddress)) ||
      (isStore(instruction) && isAddress(args.head, lowAddress)))
  }

  private def isExplicitStore(instruction: DecodedInstruction): Boolean =
    isScalarStore(instruction) && instruction.operands.size == 2 &&
      (instruction.operands.head match {
        case Operand.Address(_, _, _, _, _, _) => true
        case _                                 => false
      }) && isGeneralRegister(instruction.operands(1)) &&
      !isCarryWriter(instruction)

  private def isSchedulingBarrier(instruction: DecodedInstruction): Boolean =
    instruction.operation match {
      case _: Instruction.Depbar => !instruction.operands.exists(isPredicateOperand(_))
      case _: Instruction.Bar    => instruction.modifiers == Vec(Instruction.Modifier.SYNC) &&
        !instruction.operands.exists(isPredicateOperand(_))
      case _ => false
    }

  private def explicitLoadIndependent(instruction: DecodedInstruction, protectedRegisters: Set[Int]): Boolean = {
    val explicitLoad = instruction.operation match {
      case _: Instruction.Ldg | _: Instruction.Lds | _: Instruction.Ldl | _: Instruction.Ldc => true
      case _                                                                                 => false
    }
    val width = accessWords(instruction)
    explicitLoad && instruction.operands.size == 2 && !isCarryWriter(instruction) &&
    instruction.operands.headOption.filter(isGeneralRegister(_)).flatMap(registerIndex(_)).exists(first =>
      !(first.raw until first.raw + width).exists(protectedRegisters)
    )
  }

  /** Retained scalar instructions with one register result; predicate effects are tracked separately. */
  private def scalarDestination(instruction: DecodedInstruction): Option[Int in 0 ~ 255] = {
    val args       = instruction.operands
    val mods       = instruction.modifiers
    val scalarForm = instruction.operation match {
      case _: Instruction.Mov | _: Instruction.Mov32i => mods.isEmpty && args.size == 2
      case _: Instruction.S2r                         => mods.isEmpty && args.size == 2 &&
        (args(1) match {
          case Operand.SpecialRegister(_) => true
          case _                          => false
        })
      case _: Instruction.Iadd | _: Instruction.Iadd32i =>
        mods.forall(Set(Instruction.Modifier.`U32`, Instruction.Modifier.`X`)) && args.size == 3
      case _: Instruction.Lop | _: Instruction.Lop32i =>
        (args.size == 3 || (args.size == 4 && args.headOption.exists(isPredicateOperand(_)))) && mods.forall(Set(
          Instruction.Modifier.AND,
          Instruction.Modifier.OR,
          Instruction.Modifier.XOR,
          Instruction.Modifier.PASS_B,
          Instruction.Modifier.Z,
          Instruction.Modifier.NZ
        ))
      case _: Instruction.Lop3 => args.size == 5 && mods == Vec(Instruction.Modifier.LUT)
      case _: Instruction.Shfl => args.size == 5 &&
        args.head == Operand.Predicate(None, uniform = false, negated = false)
      case _: Instruction.Iadd3                      => mods.isEmpty && args.size == 4
      case _: Instruction.Iscadd                     => mods.isEmpty && args.size == 4
      case _: Instruction.Xmad                       => args.size == 4 && !isCarryWriter(instruction)
      case _: Instruction.Fadd | _: Instruction.Fmul => mods.isEmpty && args.size == 3
      case _: Instruction.Ffma                       => mods.isEmpty && args.size == 4
      case _: Instruction.Pset                       => args.size == 4 && mods.forall(Set(
          Instruction.Modifier.AND,
          Instruction.Modifier.OR,
          Instruction.Modifier.XOR,
          Instruction.Modifier.BF
        ))
      case _: Instruction.Shr | _: Instruction.Shl =>
        mods.forall(Set(Instruction.Modifier.`U32`, Instruction.Modifier.`W`)) && args.size == 3
      case _: Instruction.Sel   => mods.isEmpty && args.size == 4 && isPredicateOperand(args.last)
      case _: Instruction.Imnmx => mods.forall(Set(Instruction.Modifier.U32)) && args.size == 4 &&
        isPredicateOperand(args.last)
      case _: Instruction.Icmp =>
        mods.forall(Comparisons + Instruction.Modifier.`U32`) && mods.count(Comparisons) == 1 && args.size == 4
      case _ => false
    }
    val predicateOutput = instruction.operation match {
      case _: Instruction.Shfl => true
      case _: Instruction.Lop  => args.size == 4
      case _                   => false
    }
    val predicateSelector = instruction.operation match {
      case _: Instruction.Sel | _: Instruction.Imnmx => true
      case _                                         => false
    }
    val destination = if (predicateOutput) args.lift(1) else args.headOption
    val dataSources = if (predicateOutput) args.drop(2)
    else if (predicateSelector) args.drop(1).dropRight(1) else args.drop(1)
    val predicateData = dataSources.exists(isPredicateOperand(_)) &&
      !OperandQueries.is[Instruction.Pset](instruction.operation)
    destination.filter(output =>
      scalarForm && !predicateData &&
        isRegisterDestination(output, allowZero = true, allowCarry = true) &&
        (!OperandQueries.has(output, OperandSelector.CC) || isAdd(instruction) ||
          OperandQueries.is[Instruction.Iscadd](instruction.operation))
    ).flatMap(registerIndex(_))
  }

  private class ControlFlow(instructions: Vec[DecodedInstruction]) {
    val indices: Map[Int, Int] = instructions.zipWithIndex.map { case (instruction, index) =>
      instruction.address.raw -> index
    }.toMap
    def targets(instruction: DecodedInstruction): Option[Vec[Int]] = {
      val addresses = codeTargets(instruction).toVec
      Option.when(addresses.size == 1 && addresses.forall(indices.contains(_)))(addresses.map(indices(_)))
    }
    private val legacy  = LegacyControlFlow(instructions)
    private val entries = LegacyControlFlow.functionEntries(instructions)
    // a return may resume after any call or at any pushed return address
    private val returns: Vec[Int] = instructions.indices.toVec.flatMap { index =>
      instructions(index).operation match {
        case _: Instruction.Cal  => Vec(index + 1).filter(_ < instructions.size)
        case _: Instruction.Pret => targets(instructions(index)).getOrElse(Vec.empty)
        case _                   => Vec.empty
      }
    }
    def successors(index: Int): Option[Vec[Int]] = {
      val instruction = instructions(index)
      val next        = Vec(index + 1)
      val fallthrough = if (LegacyControlFlow.terminates(instruction)) Vec.empty[Int] else next
      if (LegacyControlFlow.never(instruction)) Some(next)
      else if (hasModifier(instruction, Instruction.Modifier.`S`)) None
      else instruction.operation match {
        case _: Instruction.Bra                                            => targets(instruction).map(_ ++ fallthrough)
        case _: Instruction.Ssy | _: Instruction.Pbk | _: Instruction.Pret => targets(instruction).map(_ => next)
        case _: Instruction.Cal                                            => targets(instruction)
        case _: Instruction.Brx if LegacyControlFlow.isIndirectCall(instructions, index) => Some(entries)
        case _: Instruction.Ret if instruction.operands.isEmpty                          => Some(returns ++ fallthrough)
        case _: Instruction.Brk | _: Instruction.Sync                                    =>
          legacy.transfer(instruction).map(setup =>
            Vec(indices(setup.target.raw)) ++ (if (instruction.guard.nonEmpty) next else Vec.empty[Int])
          )
        case _: Instruction.Exit                                                           => Some(fallthrough)
        case _: Instruction.Bpt if instruction.modifiers == Vec(Instruction.Modifier.TRAP) => Some(fallthrough)
        case _ if isControlFlow(instruction)                                               => None
        case _                                                                             => Some(next)
      }
    }
  }

  private enum PredicateOrigin { case Original, Conditional, Replaced }
  private case class ProofState(index: Int, origin: PredicateOrigin, addressValid: Boolean, guardStable: Boolean)

  private def genericMemoryIndependent(instruction: DecodedInstruction, registers: Set[Int]): Boolean =
    (isLoad(instruction) || isStore(instruction)) && GenericMemoryModifiers(instruction.modifiers) &&
      (isStore(instruction) || instruction.operands.headOption.flatMap(registerIndex(_)).exists { first =>
        !(first.raw until first.raw + accessWords(instruction)).exists(registers)
      })

  private def preservesAddress(instruction: DecodedInstruction, registers: Set[Int]): Boolean = {
    val addressDestination = if (!isLea(instruction)) None
    else if (isPredicateForm(instruction)) instruction.operands.lift(1).flatMap(registerIndex(_))
    else instruction.operands.headOption.flatMap(registerIndex(_))
    isControlFlow(instruction) || isPredicateSetter(instruction) || isNop(instruction) || isIdentityMove(instruction) ||
    isMemBar(instruction) || isSchedulingBarrier(instruction) || isExplicitStore(instruction) ||
    scalarDestination(instruction).exists(index => !registers(index.raw)) ||
    addressDestination.exists(index => !registers(index.raw)) ||
    explicitLoadIndependent(instruction, registers) || genericMemoryIndependent(instruction, registers)
  }

  /** Track every route to a consumer, including routes that replace or bypass this predicate producer. */
  private def memoryConsumers(
      instructions: Vec[DecodedInstruction],
      highIndex: Int,
      target: Int,
      lowAddress: Int in 0 ~ 255
  ): Vec[(Int, DecodedInstruction)] = {
    val high         = instructions(highIndex)
    val flow         = ControlFlow(instructions)
    val registers    = Set(lowAddress.raw, lowAddress.raw + 1)
    val entryTargets = controlFlowTargets(instructions)

    def replacedOnGuard(index: Int): Boolean = instructions(index).guard.exists { guard =>
      predicate(guard.predicate).exists { condition =>
        @annotation.tailrec
        def seek(cursor: Int): Boolean =
          if (cursor < 0 || cursor == highIndex || condition == target) false
          else {
            val previous = instructions(cursor)
            if (
              entryTargets(instructions(cursor + 1).address.raw) || isControlFlow(previous) ||
              isPredicateBankTransfer(previous) ||
              previous.operands.exists(operand => predicate(operand).contains(condition))
            ) false
            else if (
              previous.guard.contains(guard) && isPredicateDefinition(previous, target) &&
              !hasPredicateRead(previous, target)
            ) true
            else seek(cursor - 1)
          }
        seek(index - 1)
      }
    }
    val pending = scala.collection.mutable.ArrayDeque(
      ProofState(0, PredicateOrigin.Replaced, addressValid = false, guardStable = false),
      ProofState(highIndex + 1, PredicateOrigin.Original, true, true)
    )
    val consumers  = scala.collection.mutable.Set.empty[Int]
    val otherReads = scala.collection.mutable.Set.empty[Int]
    @annotation.tailrec
    def visit(seen: Set[ProofState]): Unit =
      if (pending.nonEmpty) {
        val state = pending.removeHead()
        if (state.index == highIndex || seen(state)) visit(seen)
        else if (state.index < 0 || state.index >= instructions.size)
          throw IllegalArgumentException("generic address proof reaches outside the function")
        else {
          val instruction            = instructions(state.index)
          def unsupported(): Nothing = throw IllegalArgumentException(
            s"unverified auxiliary P$target use at 0x${instruction.address.raw.toHexString} from LEA.HI.X at 0x${high.address.raw.toHexString}"
          )
          if (state.origin != PredicateOrigin.Replaced && isPredicateBankTransfer(instruction)) unsupported()
          if (hasPredicateRead(instruction, target)) {
            if (state.origin == PredicateOrigin.Replaced || replacedOnGuard(state.index)) otherReads += state.index
            else if (
              state.origin == PredicateOrigin.Original && state.addressValid && state.guardStable &&
              !predicateGuardReads(instruction, target) && isGenericMemory(instruction, target, lowAddress) &&
              (high.guard.isEmpty || instruction.guard == high.guard)
            ) consumers += state.index
            else unsupported()
          }
          val origin = if (state.origin == PredicateOrigin.Replaced) PredicateOrigin.Replaced
          else if (isPredicateDefinition(instruction, target)) {
            if (instruction.guard.isEmpty || (state.guardStable && instruction.guard == high.guard))
              PredicateOrigin.Replaced
            else PredicateOrigin.Conditional
          } else state.origin
          val addressValid = state.addressValid && preservesAddress(instruction, registers)
          val guardStable  = state.guardStable &&
            !high.guard.flatMap(guard => predicate(guard.predicate)).exists { condition =>
              instruction.operands.exists(operand => predicate(operand).contains(condition))
            }
          val next = flow.successors(state.index).getOrElse(unsupported())
          pending.prependAll(next.map(index =>
            if (origin == PredicateOrigin.Replaced) ProofState(index, origin, addressValid = false, guardStable = false)
            else ProofState(index, origin, addressValid, guardStable)
          ))
          visit(seen + state)
        }
      }
    visit(Set.empty)
    if (consumers.isEmpty || consumers.exists(otherReads))
      throw IllegalArgumentException(s"auxiliary P$target has consumers without one preserved address producer")
    consumers.toVec.sorted.map(index => index -> instructions(index))
  }

  private def isAuxiliaryHigh(instruction: DecodedInstruction): Boolean =
    isLea(instruction) && instruction.modifiers == Vec(Instruction.Modifier.`HI`, Instruction.Modifier.`X`) &&
      instruction.operands.headOption.exists(operand => positivePredicate(operand).nonEmpty)

  private def rewriteHigh(
      instructions: Vec[DecodedInstruction],
      high: DecodedInstruction,
      highIndex: Int,
      controlFlowTargets: Set[Int]
  ): Map[Int, DecodedInstruction] = {
    val target = positivePredicate(high.operands.head).get
    val args   = high.operands
    if (
      args.size != 5 || !isRegisterDestination(args(1), allowZero = false, allowCarry = false) ||
      !isGeneralRegister(args(2)) ||
      !(OperandQueries.isZero(args(3)) ||
        (args(3) match {
          case Operand.ConstantBank(_, offset) => OperandQueries.integer(offset).nonEmpty
          case _                               => false
        })) || !isGeneralRegister(args(4), allowZero = true)
    )
      throw IllegalArgumentException(s"unverified generic LEA.HI.X operands at 0x${high.address.raw.toHexString}")

    val previousCarryWriter = instructions.take(highIndex).lastIndexWhere(isCarryWriter(_))
    if (previousCarryWriter < 0)
      throw IllegalArgumentException(s"generic LEA.HI.X at 0x${high.address.raw.toHexString} has no LEA.CC producer")
    val low            = instructions(previousCarryWriter)
    val lowArgs        = low.operands
    val lowDestination = lowArgs.headOption.flatMap(registerIndex(_))
    val lowAddress     = lowDestination.getOrElse(
      throw IllegalArgumentException(
        s"generic LEA.HI.X at 0x${high.address.raw.toHexString} has unsupported LEA.CC producer"
      )
    )
    val lowSource      = lowArgs.lift(1).flatMap(registerIndex(_))
    val lowOffsetValid = lowArgs.lift(2).exists(value =>
      OperandQueries.isZero(value) ||
        OperandQueries.integer(value).exists(number => number >= -0x80000 && number <= BigInt("ffffffff", 16))
    )
    if (
      !isLea(low) || low.modifiers.nonEmpty || lowArgs.size != 3 ||
      !isRegisterDestination(lowArgs.head, allowZero = false, allowCarry = true) ||
      !OperandQueries.has(lowArgs.head, OperandSelector.CC) ||
      !lowArgs.lift(1).exists(isGeneralRegister(_)) || !lowOffsetValid ||
      lowSource != registerIndex(args(2)) || low.guard != high.guard ||
      predicateGuardReads(low, target) || predicateGuardReads(high, target)
    )
      throw IllegalArgumentException(
        s"generic LEA.HI.X at 0x${high.address.raw.toHexString} has unsupported LEA.CC producer at 0x${low.address.raw.toHexString}"
      )

    val region = instructions.slice(previousCarryWriter, highIndex + 1)
    if (
      region.exists(instruction => isControlFlow(instruction) && !isReconvergenceSetup(instruction)) ||
      controlFlowTargets.exists(address => address > low.address.raw && address <= high.address.raw)
    )
      throw IllegalArgumentException(
        s"control flow enters or crosses generic LEA sequence at 0x${low.address.raw.toHexString}"
      )

    def independentPredicate(instruction: DecodedInstruction): Boolean =
      isPredicateSetter(instruction) && !isCarryWriter(instruction) &&
        !high.guard.flatMap(guard => predicate(guard.predicate)).exists(condition =>
          instruction.operands.exists(operand => predicate(operand).contains(condition))
        )

    val protectedRegisters = (Set(lowAddress) ++ lowSource.toSet).map(_.raw)
    if (
      instructions.slice(previousCarryWriter + 1, highIndex).exists { instruction =>
        val scalarBridge = !isCarryWriter(instruction) &&
          scalarDestination(instruction).exists(destination => !protectedRegisters(destination.raw))
        !isExplicitStore(instruction) && !isNop(instruction) && !isIdentityMove(instruction) && !scalarBridge &&
        !independentPredicate(instruction) &&
        !explicitLoadIndependent(instruction, protectedRegisters) && !isSchedulingBarrier(instruction) &&
        !isMemBar(instruction) &&
        !isReconvergenceSetup(instruction) && !genericMemoryIndependent(instruction, protectedRegisters)
      }
    )
      throw IllegalArgumentException(
        s"generic LEA.CC at 0x${low.address.raw.toHexString} has an unverified instruction before LEA.HI.X"
      )

    val highAddress  = registerIndex(args(1))
    val expectedHigh = lowAddress.raw + 1
    if (highAddress.forall(_.raw != expectedHigh))
      throw IllegalArgumentException(
        s"generic address pair requires adjacent registers R${lowAddress.raw}/R$expectedHigh"
      )

    val memoryReads = memoryConsumers(instructions, highIndex, target, lowAddress)

    val rewrittenLow = low.operation match {
      case Instruction.LeaRegRegImm(dst, srcA, source, mods, pred) =>
        low.rewrite(Instruction.LeaRegRegImmImm(dst, srcA, source, PaddingImmediate, mods, pred))
      case Instruction.LeaRegRegReg(dst, srcA, srcB, mods, pred) =>
        low.rewrite(Instruction.LeaRegRegRegImm(dst, srcA, srcB, PaddingImmediate, mods, pred))
      case _ =>
        throw IllegalArgumentException(
          s"unsupported typed LEA.CC producer at 0x${low.address.raw.toHexString}"
        )
    }
    val rewrittenHigh = high.operation match {
      case Instruction.LeaPredRegRegRegReg(_, dst, srcA, srcB, srcC, mods, pred) =>
        high.rewrite(Instruction.LeaRegRegRegRegImm(dst, srcA, srcB, srcC, PaddingImmediate, mods, pred))
      case Instruction.LeaPredRegRegConstReg(_, dst, srcA, srcConst, srcC, mods, pred) =>
        high.rewrite(Instruction.LeaRegRegConstRegImm(dst, srcA, srcConst, srcC, PaddingImmediate, mods, pred))
      case _ =>
        throw IllegalArgumentException(
          s"unsupported typed LEA.HI.X consumer at 0x${high.address.raw.toHexString}"
        )
    }
    val rewrittenMemory = memoryReads.map { case (index, memory) =>
      val rewritten = memory.operation match {
        case Instruction.LdRegAddressPred(dst, address, _, mods, pred) =>
          memory.rewrite(Instruction.LdRegAddress(dst, address, mods, pred))
        case Instruction.StAddressRegPred(address, source, _, mods, pred) =>
          memory.rewrite(Instruction.StAddressReg(address, source, mods, pred))
        case _ =>
          throw IllegalArgumentException(
            s"unsupported typed generic-memory predicate use at 0x${memory.address.raw.toHexString}"
          )
      }
      index -> rewritten
    }
    Map(previousCarryWriter -> rewrittenLow, highIndex -> rewrittenHigh) ++ rewrittenMemory
  }

  /** Complementary predicated instructions cannot affect a producer's lanes while its guard is stable. */
  private def guardedView(
      instructions: Vec[DecodedInstruction],
      highIndex: Int,
      targets: Set[Int]
  ): Vec[DecodedInstruction] = instructions(highIndex).guard match {
    case Some(guard) if predicate(guard.predicate).nonEmpty =>
      val condition                                          = predicate(guard.predicate).get
      def boundary(instruction: DecodedInstruction): Boolean =
        isControlFlow(instruction) || isPredicateBankTransfer(instruction) ||
          instruction.operands.exists(operand => predicate(operand).contains(condition))
      val start     = instructions.take(highIndex).lastIndexWhere(boundary(_)) + 1
      val following = instructions.drop(highIndex + 1).indexWhere(boundary(_))
      val end       = if (following < 0) instructions.size else highIndex + 1 + following
      val first     = instructions(start).address.raw
      val last      = instructions(end - 1).address.raw
      if (targets.exists(address => address > first && address <= last)) instructions
      else instructions.zipWithIndex.filterNot { case (instruction, index) =>
        index >= start && index < end &&
        instruction.guard.exists(_.predicate == guard.predicate.copy(negated = !guard.predicate.negated))
      }.map(_._1)
    case _ => instructions
  }

  private def rewriteFunction(function: DecodedFunction): DecodedFunction = {
    val instructions    = function.instructions
    val hasAuxiliaryLea = instructions.exists(isAuxiliaryHigh(_))
    val targets         = controlFlowTargets(instructions)
    if (
      hasAuxiliaryLea &&
      instructions.indices.exists(index =>
        isIndirectTransfer(instructions(index)) && !LegacyControlFlow.isIndirectCall(instructions, index)
      )
    )
      throw IllegalArgumentException(
        s"${function.name} has indirect control flow; generic LEA target is not statically bounded"
      )

    val originalIndices = instructions.zipWithIndex.map((instruction, index) => instruction.address -> index).toMap
    val rewrites        = instructions.zipWithIndex.foldLeft(Map.empty[Int, DecodedInstruction]) {
      case (current, (high, highIndex)) if isAuxiliaryHigh(high) =>
        val view         = guardedView(instructions, highIndex, targets)
        val position     = view.indexWhere(_.address == high.address)
        val replacements = rewriteHigh(view, high, position, targets)
        current ++ replacements.map((index, instruction) => originalIndices(view(index).address) -> instruction)
      case (current, _) => current
    }
    function.copy(instructions =
      instructions.indices.map(index => rewrites.getOrElse(index, instructions(index))).toVec
    )
  }

  /** Remove only verified generic-memory auxiliary-predicate idioms on sm_50/sm_60. */
  def apply(sm: Int, function: DecodedFunction): DecodedFunction =
    if (!VMCompilationContext.verifiedLegacy(sm)) function
    else try rewriteFunction(function)
    catch {
      case error: IllegalArgumentException =>
        throw IllegalArgumentException(s"${function.name} (sm_$sm): ${error.getMessage}", error)
    }
}
