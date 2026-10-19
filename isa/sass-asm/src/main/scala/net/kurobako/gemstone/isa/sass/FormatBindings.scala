package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.isa.catalogue.*

/** Floating immediate placement shared by decoding and assembly. */
object FormatBindings {
  def floatingShift(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      field: NVDisasm.Syntax.Field,
      format: FloatEncoding
  ): Int = {
    // legacy encodings keep the top bits of an immediate, such as 10 of a binary16 or 20 of a binary32
    def compact(bits: Int): Boolean = database.width < 128 && form.fields.exists { assignment =>
      assignment.positions.size == bits &&
      (assignment.expression match {
        case Expression.Reference(name, None, _) => name == field.binding
        case _                                   => false
      })
    }
    format match {
      case FloatEncoding.F16Imm if compact(10) => 6
      case FloatEncoding.F32Imm if compact(20) => 12
      case FloatEncoding.F64Imm if compact(20) => 44
      case FloatEncoding.F64Imm                => 32
      case FloatEncoding.E8M7Imm               => 16
      case _                                   => 0
    }
  }
}
