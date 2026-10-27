package net.kurobako.gemstone.isa.spirv

import VMProgram.*

/** `calls` calls to `function`, each returning a value of `resultType`. A parameter whose entry of `fixed` is not
  * negative takes the value of that id in every call.
  */
private class Sweep(val function: Function, val resultType: Int, val fixed: Array[Int], val calls: Int)
