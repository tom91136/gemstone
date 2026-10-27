package net.kurobako.gemstone.isa.spirv

import VMProgram.*

/** A call to `function` with the values of `arguments`, which delivers a value of `resultType` to `destination`. */
private class Callee(val function: Function, val arguments: Array[Int], val resultType: Int, val destination: Int)
