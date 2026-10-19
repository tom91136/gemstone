package net.kurobako.gemstone.isa.spirv

/** Operand classification for lowering, and the lane kernel shape that specialisation constant folding evaluates. */
private object LaneOps {
  import Operand.*

  def isPointer(tpe: VMProgram.RuntimeType): Boolean = tpe match {
    case VMProgram.RuntimeType.Pointer(_, _) => true
    case _                                   => false
  }

  trait Kernel2 { def apply(a: Long, b: Long): Long }

  /** The static kind of an operand value. */
  // the id is read on every value of the hot path, and hoisting it turns the patterns into nested ones
  @SuppressWarnings(Array("baumkuchen:common-factor"))
  enum Typed {
    case Undefined(id: Int)
    case Opaque(id: Int, typeId: Int)
    case Data(id: Int, typeId: Int)

    def id: Int
  }

  def typed(site: Site, value: Operand): Typed = typed(site, id(value))

  def typed(site: Site, key: Int): Typed = {
    val typeId = site.program.valueType(key)
    if (typeId < 0) Typed.Undefined(key)
    else site.program.runtimeType(typeId) match {
      case Some(VMProgram.RuntimeType.Bool | VMProgram.RuntimeType.Integer(_, _) | VMProgram.RuntimeType.Floating(_) |
          VMProgram.RuntimeType.Float8(_) |
          VMProgram.RuntimeType.VectorType(
            _,
            _
          ) | VMProgram.RuntimeType.MatrixType(_, _) | VMProgram.RuntimeType.Array(_, _) |
          VMProgram.RuntimeType.Structure(_) | VMProgram.RuntimeType.Event | VMProgram.RuntimeType.NamedBarrier |
          VMProgram.RuntimeType.ReserveId |
          VMProgram.RuntimeType.DeviceEvent | VMProgram.RuntimeType.Queue) =>
        Typed.Data(key, typeId)
      case _ => Typed.Opaque(key, typeId)
    }
  }
}
