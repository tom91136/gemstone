package net.kurobako.gemstone.isa.sass

/** Floating immediate placement shared by decoding and assembly. */
object FormatBindings {
  def floatingShift(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      field: NVDisasm.Syntax.Field,
      format: FloatEncoding
  ): Int = {
    val compact = database.width.raw < 128 && form.fields.exists { assignment =>
      assignment.positions.size == 20 &&
      (assignment.expression match {
        case NVDisasm.Expression.Reference(name, None, _) => name == field.binding
        case _                                            => false
      })
    }
    format match {
      case FloatEncoding.F32Imm if compact => 12
      case FloatEncoding.F64Imm if compact => 44
      case FloatEncoding.F64Imm            => 32
      case FloatEncoding.E8M7Imm           => 16
      case _                               => 0
    }
  }
}
