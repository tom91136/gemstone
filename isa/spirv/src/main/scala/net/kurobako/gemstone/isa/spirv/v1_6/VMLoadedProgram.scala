package net.kurobako.gemstone.isa.spirv.v1_6

import VMInvocation.{RuntimeValue, runtimeValue}

import net.kurobako.gemstone.vm.{Fault, Memory, MemoryRegion}

import scala.collection.concurrent.TrieMap

private[v1_6] final class VMLoadedProgram(val ir: VMIR.Program) {
  export ir.*

  // Zero identifies a program used directly through the VM, outside RPC.
  @volatile var programId: Long = 0L

  val constants: Map[Int, RuntimeValue] = ir.constantData.view.mapValues(runtimeValue).toMap

  val typeSizeCache: TrieMap[Int, Int]                       = TrieMap.empty
  val typeAlignmentCache: TrieMap[Int, Int]                  = TrieMap.empty
  val scalarLeafCountCache: TrieMap[Int, Either[Fault, Int]] = TrieMap.empty
  final case class GlobalBinding(region: MemoryRegion, offset: Long, memory: Memory)
  val boundGlobals: TrieMap[Int, GlobalBinding] = TrieMap.empty
}
