package net.kurobako.gemstone.isa.spirv

/** A cast between a Generic pointer and a pointer into `storageClass` to `pointee`, which `explicit` names. */
private class PointerCast(
    val opcode: String,
    val program: VMProgram,
    val pointee: Int,
    val storageClass: Int,
    val explicit: Boolean
)
