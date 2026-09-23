package net.kurobako.gemstone.isa.spirv.v1_6

import VMIR.{Constant, RuntimeType}
import VMInvocation.{RuntimeValue, runtimeValue}

import net.kurobako.gemstone.vm.{Fault, Memory, MemoryRegion}

import java.util.concurrent.atomic.AtomicIntegerArray
import scala.collection.concurrent.TrieMap

private[v1_6] final class VMLoadedProgram(val ir: VMIR.Program) {
  export ir.*

  // Zero identifies a program used directly through the VM, outside RPC.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile var programId: Long = 0L

  val constants: Map[Int, RuntimeValue] = ir.constantData.view.mapValues(runtimeValue).toMap

  private val typeLookup = {
    val bound = types.keys.view.maxOption.fold(0L)(id => id.toLong + 1L)
    val dense =
      if (bound > 0L && bound <= 1000000L) Array.fill[Option[RuntimeType]](bound.toInt)(None)
      else Array.empty[Option[RuntimeType]]
    types.foreach { case (id, value) =>
      if (id >= 0 && id < dense.length) dense(id) = Some(value)
    }
    dense
  }

  def runtimeType(id: Int): Option[RuntimeType] =
    if (id >= 0 && id < typeLookup.length) typeLookup(id) else types.get(id)

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private val scalarIntegerInstructions = {
    val compiled = Array.ofDim[VMScalarIntegerInstruction | Null](module.instructions.size)
    var pc       = 0
    while (pc < module.instructions.size) {
      val instruction = module.instructions(pc) match {
        case Instruction.IAdd(resultType, destination, left, right) =>
          compileScalarInteger(VMScalarIntegerInstruction.Add, resultType, destination, left, right)
        case Instruction.ISub(resultType, destination, left, right) =>
          compileScalarInteger(VMScalarIntegerInstruction.Subtract, resultType, destination, left, right)
        case Instruction.IMul(resultType, destination, left, right) =>
          compileScalarInteger(VMScalarIntegerInstruction.Multiply, resultType, destination, left, right)
        case _ => null
      }
      compiled(pc) = instruction
      pc += 1
    }
    compiled
  }

  def scalarIntegerInstruction(pc: Int): VMScalarIntegerInstruction | Null =
    scalarIntegerInstructions(pc)

  private def compileScalarInteger(
      operation: Byte,
      resultType: Operand,
      destination: Operand,
      left: Operand,
      right: Operand
  ): VMScalarIntegerInstruction | Null = {
    val typeId = Operand.id(resultType)
    runtimeType(typeId) match {
      case Some(RuntimeType.Integer(width, _)) =>
        val leftId        = Operand.id(left)
        val rightId       = Operand.id(right)
        val leftConstant  = scalarConstant(leftId)
        val rightConstant = scalarConstant(rightId)
        VMScalarIntegerInstruction(
          operation,
          typeId,
          Operand.id(destination),
          leftId,
          rightId,
          leftConstant.isDefined,
          rightConstant.isDefined,
          leftConstant.getOrElse(0L),
          rightConstant.getOrElse(0L),
          if (width.value == 64) -1L else (1L << width.value) - 1L
        )
      case _ => null // scalafix:ok DisableSyntax.null
    }
  }

  private def scalarConstant(id: Int): Option[Long] =
    constantData.get(id) match {
      case Some(Constant.Bits(_, values)) if values.size == 1 => Some(values.head)
      case _                                                  => None
    }

  private val denseTypeSizeCache                       = AtomicIntegerArray(typeLookup.length)
  private val overflowTypeSizeCache: TrieMap[Int, Int] = TrieMap.empty

  def cachedTypeSize(id: Int)(compute: => Int): Int =
    if (id >= 0 && id < typeLookup.length) {
      val cached = denseTypeSizeCache.get(id)
      if (cached != 0) cached - 1
      else {
        val size = compute
        // Zero represents an empty slot, so unusual values retain the map fallback.
        if (size < 0 || size == Int.MaxValue) overflowTypeSizeCache.getOrElseUpdate(id, size)
        else {
          val _ = denseTypeSizeCache.compareAndSet(id, 0, size + 1)
          size
        }
      }
    } else overflowTypeSizeCache.getOrElseUpdate(id, compute)

  val typeAlignmentCache: TrieMap[Int, Int]                  = TrieMap.empty
  val scalarLeafCountCache: TrieMap[Int, Either[Fault, Int]] = TrieMap.empty
  final case class GlobalBinding(region: MemoryRegion, offset: Long, memory: Memory)
  val boundGlobals: TrieMap[Int, GlobalBinding] = TrieMap.empty
}
