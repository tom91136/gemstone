package net.kurobako.gemstone.isa.spirv.v1_6

import VMInvocation.{RuntimeValue, runtimeValue}

import net.kurobako.gemstone.vm.Fault

import scala.collection.concurrent.TrieMap

private[v1_6] final class VMLoadedProgram(val ir: VMIR.Program) {
  export ir.*

  val constants: Map[Int, RuntimeValue] = ir.constantData.view.mapValues(runtimeValue).toMap

  val typeSizeCache: TrieMap[Int, Int]                       = TrieMap.empty
  val typeAlignmentCache: TrieMap[Int, Int]                  = TrieMap.empty
  val scalarLeafCountCache: TrieMap[Int, Either[Fault, Int]] = TrieMap.empty
}
