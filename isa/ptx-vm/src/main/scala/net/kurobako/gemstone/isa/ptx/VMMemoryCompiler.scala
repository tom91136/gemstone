package net.kurobako.gemstone.isa.ptx

import Lowering.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.*

/** Lowers `ld` and `st` from one reading of their state space, ordering and type qualifiers. */
private object VMMemoryCompiler {
  private enum Space derives CanEqual {
    case Parameter, Shared, Local, Global, Generic
  }

  private case class Form(modifiers: Vec[Modifier]) {
    def has(modifier: Modifier): Boolean = modifiers.contains(modifier)

    val space: Space =
      if (has(Modifier.Param)) Space.Parameter
      else if (has(Modifier.Shared) || has(Modifier.SharedCta)) Space.Shared
      else if (has(Modifier.Local)) Space.Local
      else if (has(Modifier.Global) || has(Modifier.Const)) Space.Global
      else Space.Generic

    val signed: Boolean = modifiers.lastOption.exists(PartialFunction.cond(_) {
      case Modifier.S8 | Modifier.S16 | Modifier.S32 | Modifier.S64 => true
    })

    /** Strong system-scope accesses publish through the host on every access. */
    val access: MemoryAccess = space match {
      case Space.Shared => MemoryAccess.Shared
      case Space.Local  => MemoryAccess.Local
      case Space.Global => MemoryAccess.Global(has(Modifier.Relaxed) && has(Modifier.Sys))
      case _            => MemoryAccess.Generic(has(Modifier.Relaxed) && has(Modifier.Sys))
    }

    // Acquire loads and release stores of global words use the host's sequentially consistent atomics.
    def ordered(order: Modifier): Boolean =
      has(order) && space == Space.Global &&
        modifiers.lastOption.exists(PartialFunction.cond(_) {
          case Modifier.U32 | Modifier.B32 | Modifier.U64 | Modifier.B64 => true
        })
  }

  /** A 128-bit access moves two 64-bit words at once; the VM performs a lane's accesses in order, so its ordering
    * qualifiers need nothing further, but host-published system-scope accesses are word-sized.
    */
  private def wide(form: Form): Either[Error, MemoryAccess] =
    if (form.space == Space.Parameter || form.has(Modifier.Sys))
      invalid("b128 memory access requires a memory state space below system scope")
    else Right(form.access)

  private def parameterAddress(operand: Operand): Option[(NonEmpty[String], Long)] = operand match {
    case Operand.Address(Vec(Operand.Symbol(name)))                      => Some(name -> 0L)
    case Operand.Address(Vec(Operand.Add(Operand.Symbol(name), offset))) =>
      NumericLiteral.immediate(offset).map(name -> _)
    case _ => None
  }

  private def parameterOffset(offset: Long): Either[Error, Int >= 0] =
    if (offset >= 0L && offset <= Int.MaxValue.toLong) Right(offset.toInt.unsafe[Int >= 0])
    else invalid(s"parameter offset is outside Int range: $offset")

  def load(destination: Operand, address: Operand, modifiers: Vec[Modifier])(
      using Scope
  ): Either[Error, VMIR.Instruction] = loaded(destination, address, modifiers, Coherence.Coherent)

  def loadNonCoherent(destination: Operand, address: Operand, modifiers: Vec[Modifier])(
      using Scope
  ): Either[Error, VMIR.Instruction] = loaded(destination, address, modifiers, Coherence.NonCoherent)

  private enum Coherence derives CanEqual { case Coherent, NonCoherent }

  private def loaded(destination: Operand, address: Operand, modifiers: Vec[Modifier], coherence: Coherence)(
      using scope: Scope
  ): Either[Error, VMIR.Instruction] = {
    val form                                                                        = Form(modifiers)
    def plain(target: Value, access: MemoryAccess): Either[Error, VMIR.Instruction] =
      scalarWidth(modifiers).flatMap { width =>
        if (Operand.isVectorLiteral(destination))
          scope.registers(destination).map(VMIR.Instruction.LoadVector(_, target, width, form.signed, access))
        else scope.register(destination).map(VMIR.Instruction.Load(_, target, width, form.signed, access))
      }
    if (form.has(Modifier.B128))
      for {
        access       <- wide(form)
        destinations <- scope.registers(destination).flatMap(pair(_, "ld.b128 destination"))
        target       <- scope.address(address)
      } yield VMIR.Instruction.LoadVector(destinations, target, 64, false, access)
    else if (coherence == Coherence.NonCoherent) scope.address(address).flatMap(plain(_, MemoryAccess.Global(false)))
    else form.space match {
      case Space.Parameter =>
        parameterAddress(address) match {
          case Some((name, offset)) if scope.isKernelParameter(name) =>
            plain(Value.Immediate(scope.kernelParameter(name).getOrElse(0L) + offset), MemoryAccess.Generic(false))
          case Some((name, offset)) =>
            scope.spilledParameter(name).fold(
              for {
                parameter    <- scope.parameter(name)
                start        <- parameterOffset(offset)
                width        <- scalarWidth(modifiers)
                destinations <- scope.registers(destination)
              } yield VMIR.Instruction.LoadParameter(destinations, parameter, start, width, form.signed)
            )(local => plain(Value.Immediate(local + offset), MemoryAccess.Local))
          case None => scope.address(address).flatMap(plain(_, MemoryAccess.Generic(false)))
        }
      case _ if form.ordered(Modifier.Acquire) && !Operand.isVectorLiteral(destination) =>
        for {
          width    <- scalarWidth(modifiers)
          register <- scope.register(destination)
          target   <- scope.address(address)
        } yield VMIR.Instruction.Atomic(
          Some(register),
          target,
          Value.Immediate(0L),
          width,
          MemorySpace.Global,
          AtomicOperation.Add
        )
      case _ => scope.address(address).flatMap(plain(_, form.access))
    }
  }

  def store(address: Operand, source: Operand, modifiers: Vec[Modifier])(using
      scope: Scope
  )
      : Either[Error, VMIR.Instruction] = {
    val form                                                                        = Form(modifiers)
    def plain(target: Value, access: MemoryAccess): Either[Error, VMIR.Instruction] =
      scalarWidth(modifiers).flatMap { width =>
        if (Operand.isVectorLiteral(source))
          scope.values(source).map(VMIR.Instruction.StoreVector(target, _, width, access))
        else scope.value(source).map(VMIR.Instruction.Store(target, _, width, access))
      }
    if (form.has(Modifier.B128))
      for {
        access  <- wide(form)
        sources <- scope.values(source).flatMap(pair(_, "st.b128 source"))
        target  <- scope.address(address)
      } yield VMIR.Instruction.StoreVector(target, sources, 64, access)
    else form.space match {
      case Space.Parameter =>
        parameterAddress(address) match {
          case Some((name, offset)) if scope.isKernelParameter(name) =>
            plain(Value.Immediate(scope.kernelParameter(name).getOrElse(0L) + offset), MemoryAccess.Generic(false))
          case Some((name, offset)) =>
            scope.spilledParameter(name).fold(
              for {
                parameter <- scope.parameter(name)
                start     <- parameterOffset(offset)
                width     <- scalarWidth(modifiers)
                value     <- scope.value(source)
              } yield VMIR.Instruction.StoreParameter(parameter, start, value, width)
            )(local => plain(Value.Immediate(local + offset), MemoryAccess.Local))
          case None => scope.address(address).flatMap(plain(_, MemoryAccess.Generic(false)))
        }
      case _ if form.ordered(Modifier.Release) && !Operand.isVectorLiteral(source) =>
        for {
          width  <- scalarWidth(modifiers)
          target <- scope.address(address)
          value  <- scope.value(source)
        } yield VMIR.Instruction.Atomic(None, target, value, width, MemorySpace.Global, AtomicOperation.Exchange)
      case _ => scope.address(address).flatMap(plain(_, form.access))
    }
  }
}
