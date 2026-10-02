package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

/** Conservative opaque-predicate proof for CUDA 11.8 Maxwell/Pascal generic-address sequences. */
private object VMAddressAnalysis {
  private val Comparisons = Set(
    Instruction.Modifier.`EQ`,
    Instruction.Modifier.`NE`,
    Instruction.Modifier.`LT`,
    Instruction.Modifier.`LE`,
    Instruction.Modifier.`GT`,
    Instruction.Modifier.`GE`
  )
  private val GenericMemoryModifiers = Set(
    Vec(Instruction.Modifier.`E`),
    Vec(Instruction.Modifier.`E`, Instruction.Modifier.`CG`),
    Vec(Instruction.Modifier.`E`, Instruction.Modifier.`CG`, Instruction.Modifier.`64`),
    Vec(Instruction.Modifier.`E`, Instruction.Modifier.`CG`, Instruction.Modifier.`128`),
    Vec(Instruction.Modifier.`E`, Instruction.Modifier.`CG`, Instruction.Modifier.`U8`),
    Vec(Instruction.Modifier.`E`, Instruction.Modifier.`CG`, Instruction.Modifier.`S8`)
  )

  private def modifiers(instruction: DecodedInstruction): Vec[Instruction.Modifier] = instruction.modifiers
  private def hasModifier(instruction: DecodedInstruction, modifier: Instruction.Modifier): Boolean =
    modifiers(instruction).contains(modifier)

  private def isLea(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Lea => true
    case _                  => false
  }
  private def isNop(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Nop => true
    case _                  => false
  }
  private def isMemBar(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Membar => true
    case _                     => false
  }
  private def isLoad(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Ld => true
    case _                 => false
  }
  private def isStore(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.St => true
    case _                 => false
  }
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
    isLea(instruction) && modifiers(instruction) == Vec(Instruction.Modifier.`HI`, Instruction.Modifier.`X`) &&
      instruction.operands.size == 5 && instruction.operands.headOption.exists(positivePredicate(_).nonEmpty)
  private def isScalarStore(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Sts | _: Instruction.Stl => true
    case _                                       => false
  }
  private def isSafeDuringPredicateLifetime(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Mov | _: Instruction.Mov32i | _: Instruction.Sel | _: Instruction.Icmp |
        _: Instruction.Nop | _: Instruction.Membar | _: Instruction.Ld | _: Instruction.St |
        _: Instruction.Ldg | _: Instruction.Stg | _: Instruction.Lds | _: Instruction.Sts |
        _: Instruction.Ldl | _: Instruction.Stl | _: Instruction.Iadd | _: Instruction.Iadd32i |
        _: Instruction.Iscadd | _: Instruction.Imul | _: Instruction.Imul32i | _: Instruction.Lea |
        _: Instruction.Ulea | _: Instruction.Lop | _: Instruction.Lop3 | _: Instruction.Prmt |
        _: Instruction.Shl | _: Instruction.Shr | _: Instruction.Shf | _: Instruction.Imad |
        _: Instruction.Xmad | _: Instruction.Fadd | _: Instruction.Fmul | _: Instruction.Ffma |
        _: Instruction.Fsel | _: Instruction.Fchk | _: Instruction.Frnd => true
    case _ => false
  }
  private def isSsy(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Ssy => true
    case _                  => false
  }
  private def isExit(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Exit => true
    case _                   => false
  }
  private def isAdd(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Iadd | _: Instruction.Iadd32i => true
    case _                                            => false
  }
  private def isSel(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Sel => true
    case _                  => false
  }
  private def isControlFlow(instruction: DecodedInstruction): Boolean = instruction.operation match {
    case _: Instruction.Bra | _: Instruction.Brx | _: Instruction.Brxu | _: Instruction.Call |
        _: Instruction.Cal | _: Instruction.Jmp | _: Instruction.Jmx | _: Instruction.Ret |
        _: Instruction.Exit | _: Instruction.Bssy | _: Instruction.Bsync | _: Instruction.Ssy |
        _: Instruction.Pbk | _: Instruction.Pcnt | _: Instruction.Sync | _: Instruction.Pret |
        _: Instruction.Brk | _: Instruction.Cont | _: Instruction.Bpt | _: Instruction.Kill |
        _: Instruction.Yield => true
    case _ => hasModifier(instruction, Instruction.Modifier.`S`)
  }

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
  private def operandSelectors(operand: Operand): Vec[OperandSelector] =
    VMOperand.selectors(VMOperand.without(operand, OperandSelector.reuse))
  @annotation.tailrec
  private def generalRegisterBase(operand: Operand, allowZero: Boolean): Boolean =
    VMOperand.without(operand, OperandSelector.reuse) match {
      case Operand.Register(RegisterBank.R, index) => allowZero || index.raw != 255
      case Operand.Qualified(inner, _)             => generalRegisterBase(inner, allowZero)
      case _                                       => false
    }
  private def isGeneralRegister(operand: Operand, allowZero: Boolean = false): Boolean =
    operandSelectors(operand).isEmpty && generalRegisterBase(operand, allowZero)
  @annotation.tailrec
  private def registerIndex(operand: Operand): Option[Int where Between[0, 255]] =
    VMOperand.without(operand, OperandSelector.reuse) match {
      case Operand.Register(RegisterBank.R, index) => Some(index)
      case Operand.Qualified(inner, _)             => registerIndex(inner)
      case _                                       => None
    }
  private def isRegisterDestination(operand: Operand, allowZero: Boolean, allowCarry: Boolean): Boolean = {
    val suffixes = operandSelectors(operand)
    (suffixes.isEmpty || (allowCarry && suffixes == Vec(OperandSelector.CC))) && generalRegisterBase(operand, allowZero)
  }
  private def predicateGuardReads(instruction: DecodedInstruction, target: Int): Boolean =
    instruction.guard.exists(guard => predicate(guard.predicate).contains(target))

  private def isCarryWriter(instruction: DecodedInstruction): Boolean =
    hasModifier(instruction, Instruction.Modifier.`CC`) || hasModifier(instruction, Instruction.Modifier.`X`) ||
      instruction.operands.headOption.exists(VMOperand.has(_, OperandSelector.CC))

  private def isIndirectControlFlow(instruction: DecodedInstruction): Boolean =
    isIndirectTransfer(instruction)

  private def isUnconditionalExit(instruction: DecodedInstruction): Boolean =
    isExit(instruction) && instruction.guard.isEmpty

  private def predicateDestinationIndices(instruction: DecodedInstruction): Set[Int] =
    if (isPredicateSetter(instruction)) Set(0, 1)
    else if (isPredicateForm(instruction)) Set(0)
    else Set.empty

  private def isPredicateDefinition(instruction: DecodedInstruction, target: Int): Boolean =
    instruction.operands.zipWithIndex.exists { case (operand, index) =>
      positivePredicate(operand).contains(target) && predicateDestinationIndices(instruction)(index)
    }

  private def hasPredicateRead(instruction: DecodedInstruction, target: Int): Boolean =
    predicateGuardReads(instruction, target) ||
      instruction.operands.zipWithIndex.exists { case (operand, index) =>
        predicate(operand).contains(target) && !predicateDestinationIndices(instruction)(index)
      }

  private def isUnconditionalPredicateDefinition(instruction: DecodedInstruction, target: Int): Boolean =
    isPredicateDefinition(instruction, target) && instruction.guard.isEmpty && !hasPredicateRead(instruction, target)

  private def isAddress(operand: Operand, register: Int where Between[0, 255]): Boolean = operand match {
    case Operand.Address(Some(Operand.Register(RegisterBank.R, baseIndex)), None, displacement, scale, None, None) =>
      scale.raw == 1 && baseIndex == register && displacement == 0
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
      lowAddress: Int where Between[0, 255]
  ): Boolean = {
    val args = instruction.operands
    args.size == 3 && positivePredicate(args.last).contains(target) && GenericMemoryModifiers(modifiers(instruction)) &&
    !(isStore(instruction) && modifiers(instruction).lastOption.contains(Instruction.Modifier.`S8`)) &&
    ((isLoad(instruction) && isAddress(args(1), lowAddress)) ||
      (isStore(instruction) && isAddress(args.head, lowAddress)))
  }

  private def isExplicitStore(instruction: DecodedInstruction): Boolean =
    isScalarStore(instruction) && instruction.guard.isEmpty && instruction.operands.size == 2 &&
      (instruction.operands.head match {
        case Operand.Address(_, _, _, _, _, _) => true
        case _                                 => false
      }) && isGeneralRegister(instruction.operands(1)) &&
      !isCarryWriter(instruction)

  /** Retained scalar instructions with one register result and no predicate outputs. */
  private def scalarDestination(instruction: DecodedInstruction): Option[Int where Between[0, 255]] = {
    val args       = instruction.operands
    val mods       = modifiers(instruction)
    val scalarForm = instruction.operation match {
      case _: Instruction.Mov | _: Instruction.Mov32i   => mods.isEmpty && args.size == 2
      case _: Instruction.Iadd | _: Instruction.Iadd32i =>
        mods.forall(Set(Instruction.Modifier.`U32`, Instruction.Modifier.`X`)) && args.size == 3
      case _: Instruction.Shr | _: Instruction.Shl =>
        mods.forall(Set(Instruction.Modifier.`U32`, Instruction.Modifier.`W`)) && args.size == 3
      case _: Instruction.Sel  => mods.isEmpty && args.size == 4 && isPredicateOperand(args.last)
      case _: Instruction.Icmp =>
        mods.forall(Comparisons + Instruction.Modifier.`U32`) && mods.count(Comparisons) == 1 && args.size == 4
      case _ => false
    }
    val dataSources   = if (isSel(instruction)) args.drop(1).dropRight(1) else args.drop(1)
    val predicateData = dataSources.exists(isPredicateOperand(_))
    args.headOption.filter(_ =>
      scalarForm && !predicateData &&
        isRegisterDestination(args.head, allowZero = true, allowCarry = true) &&
        (!VMOperand.has(args.head, OperandSelector.CC) || isAdd(instruction))
    ).flatMap(registerIndex(_))
  }

  private def predicateLifetime(
      instructions: Vec[DecodedInstruction],
      highIndex: Int,
      target: Int
  ): Vec[(Int, DecodedInstruction)] =
    instructions.zipWithIndex.drop(highIndex + 1).takeWhile { case (instruction, _) =>
      !isControlFlow(instruction) && !isUnconditionalPredicateDefinition(instruction, target)
    }.map { case (instruction, index) => index -> instruction }

  /** Prove that no continuation observes the erased predicate. */
  private def predicateDeadAfter(
      instructions: Vec[DecodedInstruction],
      start: Int,
      target: Int
  ): Boolean = {
    val indices = instructions.zipWithIndex.map { case (instruction, index) => instruction.address.raw -> index }.toMap
    def targetAddresses(instruction: DecodedInstruction): Set[Int] =
      if (!isControlFlow(instruction)) Set.empty
      else instruction.operands.flatMap(operand =>
        VMOperand.integer(operand)
          .filter(value => value >= 0 && value <= Int.MaxValue).map(_.toInt)
      ).toSet
    def targets(instruction: DecodedInstruction): Option[Vec[Int]] = {
      val addresses = targetAddresses(instruction).toVec
      Option.when(addresses.size == 1 && addresses.forall(indices.contains(_)))(addresses.map(indices(_)))
    }
    def provenSyncTarget(index: Int): Option[Vec[Int]] = {
      val setupIndex = instructions.take(index).lastIndexWhere(isSsy(_))
      if (setupIndex < 0) None
      else {
        val setup         = instructions(setupIndex)
        val externalEntry = instructions.zipWithIndex.exists { case (instruction, sourceIndex) =>
          (sourceIndex < setupIndex || sourceIndex > index) && targetAddresses(instruction).exists(address =>
            address > setup.address.raw && address <= instructions(index).address.raw
          )
        }
        val boundedStackEffects = instructions.slice(setupIndex + 1, index).forall { instruction =>
          !isControlFlow(instruction) ||
          (!hasModifier(instruction, Instruction.Modifier.`S`) &&
            (instruction.operation match {
              case _: Instruction.Bra =>
                targets(instruction).exists(_.forall(target => target > setupIndex && target <= index))
              case _: Instruction.Sync | _: Instruction.Exit => true
              case _                                         => false
            }))
        }
        if (setup.guard.nonEmpty || externalEntry || !boundedStackEffects) None
        else targets(setup).filter(_.head > index)
      }
    }
    def successors(instruction: DecodedInstruction, index: Int): Option[Vec[Int]] = {
      val next = Vec(index + 1)
      if (hasModifier(instruction, Instruction.Modifier.`S`)) None
      else instruction.operation match {
        case _: Instruction.Bra => targets(instruction).map(destinations =>
            destinations ++ (if (instruction.guard.nonEmpty) next else Vec.empty)
          )
        case _: Instruction.Ssy  => targets(instruction).map(_ => next)
        case _: Instruction.Sync =>
          val possibleTargets = provenSyncTarget(index).getOrElse(
            instructions.take(index).filter(isSsy(_)).flatMap(setup =>
              targets(setup).filter(_.head > index)
            ).flatten
          )
          Option.when(possibleTargets.nonEmpty)((possibleTargets ++ next).distinct)
        case _: Instruction.Exit             => Some(next)
        case _ if isControlFlow(instruction) => None
        case _                               => Some(next)
      }
    }
    val pending = scala.collection.mutable.ArrayDeque(start)
    @scala.annotation.tailrec
    def visit(seen: Set[Int]): Boolean =
      if (pending.isEmpty) true
      else {
        val index = pending.removeHead()
        if (seen(index)) visit(seen)
        else if (index < 0 || index >= instructions.size) false
        else {
          val instruction = instructions(index)
          if (isPredicateBankTransfer(instruction) || hasPredicateRead(instruction, target)) false
          else if (isUnconditionalExit(instruction) || isUnconditionalPredicateDefinition(instruction, target))
            visit(seen + index)
          else successors(instruction, index) match {
            case Some(next) =>
              pending.prependAll(next)
              visit(seen + index)
            case None => false
          }
        }
      }
    visit(Set.empty)
  }

  private def isAuxiliaryHigh(instruction: DecodedInstruction): Boolean =
    isLea(instruction) && modifiers(instruction) == Vec(Instruction.Modifier.`HI`, Instruction.Modifier.`X`) &&
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
      !isGeneralRegister(args(2)) || !VMOperand.isZero(args(3)) || !isGeneralRegister(args(4), allowZero = true)
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
      VMOperand.isZero(value) ||
        VMOperand.integer(value).exists(number => number >= 0 && number <= BigInt("ffffffff", 16))
    )
    if (
      !isLea(low) || modifiers(low).nonEmpty || lowArgs.size != 3 ||
      !isRegisterDestination(lowArgs.head, allowZero = false, allowCarry = true) ||
      !VMOperand.has(lowArgs.head, OperandSelector.CC) ||
      !lowArgs.lift(1).exists(isGeneralRegister(_)) || !lowOffsetValid ||
      lowSource != registerIndex(args(2)) || low.guard != high.guard ||
      predicateGuardReads(low, target) || predicateGuardReads(high, target)
    )
      throw IllegalArgumentException(
        s"generic LEA.HI.X at 0x${high.address.raw.toHexString} has unsupported LEA.CC producer at 0x${low.address.raw.toHexString}"
      )

    val lifetime = predicateLifetime(instructions, highIndex, target)
    lifetime.foreach { case (_, instruction) =>
      if (isPredicateBankTransfer(instruction))
        throw IllegalArgumentException(
          s"${instruction.mnemonic.raw} reads or writes the predicate register bank during auxiliary P$target lifetime"
        )
      if (isControlFlow(instruction))
        throw IllegalArgumentException(
          s"control flow before auxiliary P$target is killed at 0x${instruction.address.raw.toHexString}"
        )
    }
    val memoryReads = lifetime.collect {
      case (index, instruction) if instruction.operands.exists(operand => predicate(operand).contains(target)) =>
        index -> instruction
    }
    val otherReads = lifetime.collect {
      case (index, instruction) if predicateGuardReads(instruction, target) => index -> instruction
    }
    val memoryIndices = memoryReads.map(_._1).toSet
    if (
      memoryReads.isEmpty || otherReads.nonEmpty || memoryReads.exists { case (_, instruction) =>
        !isGenericMemory(instruction, target, lowAddress) || instruction.guard != high.guard
      } || lifetime.exists { case (_, instruction) =>
        !isSafeDuringPredicateLifetime(instruction) && !isGenericMemory(instruction, target, lowAddress)
      }
    )
      throw IllegalArgumentException(
        s"P$target must have only matching generic LD.E/ST.E uses and no control flow before its kill"
      )

    val memoryIndex = memoryReads.last._1
    if (!predicateDeadAfter(instructions, memoryIndex + 1, target))
      throw IllegalArgumentException(s"auxiliary predicate P$target may escape after its generic memory consumers")
    val endAddress = instructions(memoryIndex).address.raw
    val region     = instructions.slice(previousCarryWriter, memoryIndex + 1)
    if (
      region.exists(isControlFlow(_)) || region.exists(isIndirectControlFlow(_)) ||
      controlFlowTargets.exists(address => address > low.address.raw && address <= endAddress)
    )
      throw IllegalArgumentException(
        s"control flow enters or crosses generic LEA sequence at 0x${low.address.raw.toHexString}"
      )

    val protectedRegisters = (args.drop(1) :+ Operand.Register(RegisterBank.R, lowAddress)).flatMap(
      registerIndex(
        _
      )
    ).toSet
    if (
      instructions.slice(previousCarryWriter + 1, highIndex).exists { instruction =>
        val scalarBridge = !isCarryWriter(instruction) &&
          scalarDestination(instruction).exists(destination => !protectedRegisters(destination))
        !isExplicitStore(instruction) && !isNop(instruction) && !scalarBridge
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

    instructions.zipWithIndex.slice(highIndex + 1, memoryIndex).foreach { case (between, index) =>
      val scalarBridge = scalarDestination(between).exists(destination =>
        !Set(lowAddress.raw, expectedHigh)(destination.raw)
      )
      val harmlessNop   = isNop(between) && between.operands.isEmpty
      val harmlessStore = isExplicitStore(between)
      val memoryBarrier = isMemBar(between) && modifiers(between) == Vec(Instruction.Modifier.`CTA`) &&
        between.guard.isEmpty && between.operands.isEmpty
      val earlierMemory   = memoryIndices(index)
      val clobbersAddress = earlierMemory && isLoad(between) && {
        val count = if (modifiers(between).lastOption.contains(Instruction.Modifier.`128`)) 4
        else if (modifiers(between).lastOption.contains(Instruction.Modifier.`64`)) 2 else 1
        val first = between.operands.headOption.flatMap(registerIndex(_))
        first.forall(register =>
          (register.raw until register.raw + count).exists(index => Set(lowAddress.raw, expectedHigh)(index))
        )
      }
      if (
        (!earlierMemory && !scalarBridge && !harmlessNop && !harmlessStore && !memoryBarrier) ||
        (!earlierMemory && hasPredicateRead(between, target)) || clobbersAddress ||
        (isCarryWriter(between) && !scalarBridge)
      )
        throw IllegalArgumentException(
          s"unverified instruction between generic LEA.HI.X and memory consumer at 0x${between.address.raw.toHexString}"
        )
    }

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

  private def rewriteFunction(function: DecodedFunction): DecodedFunction = {
    val instructions       = function.instructions
    val hasAuxiliaryLea    = instructions.exists(isAuxiliaryHigh(_))
    val controlFlowTargets = instructions.flatMap(instruction =>
      if (isControlFlow(instruction)) instruction.operands.flatMap(operand =>
        VMOperand.integer(operand)
          .filter(value => value >= 0 && value <= Int.MaxValue).map(_.toInt)
      )
      else Vec.empty[Int]
    ).toSet
    if (hasAuxiliaryLea && instructions.exists(isIndirectControlFlow(_)))
      throw IllegalArgumentException(
        s"${function.name} has indirect control flow; generic LEA target is not statically bounded"
      )

    val rewrites = instructions.zipWithIndex.foldLeft(Map.empty[Int, DecodedInstruction]) {
      case (current, (high, highIndex)) if isAuxiliaryHigh(high) =>
        current ++ rewriteHigh(instructions, high, highIndex, controlFlowTargets)
      case (current, _) => current
    }
    function.copy(instructions =
      instructions.indices.map(index => rewrites.getOrElse(index, instructions(index))).toVec
    )
  }

  /** Remove only verified generic-memory auxiliary-predicate idioms on sm_50/sm_60. */
  def rewrite(sm: Int, function: DecodedFunction): DecodedFunction =
    if (!Set(50, 60)(sm)) function
    else try rewriteFunction(function)
    catch {
      case error: IllegalArgumentException =>
        throw IllegalArgumentException(s"${function.name} (sm_$sm): ${error.getMessage}", error)
    }
}
