package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*

private object ShaderRecords {
  def allocate(shader: GraphicsShader, count: Int >= 0): Array[Array[Byte]] =
    shader.interfaces.map(i => Array.ofDim[Byte]((count * i.size).raw)).toArray
  def fields(
      shader: GraphicsShader,
      direction: ShaderDirection
  ): Vector[(Int, ShaderField)] = shader.interfaces.toVector.zipWithIndex.flatMap { (record, index) =>
    if (record.direction != direction) Vector.empty
    else record.fields.toVector.map(index -> _) ++ record.builtin.toVector.map(b =>
      index -> ShaderField(0, record.size, None, Some(b), record.qualifiers)
    )
  }
  def word(data: Array[Byte], offset: Int): Int             = LittleEndian.decodeInt32(data, offset)
  def put(data: Array[Byte], offset: Int, value: Int): Unit = LittleEndian.write(value.toLong, data, offset, 4)
  def bits(value: Float): Int                               = java.lang.Float.floatToRawIntBits(value)
  def float(value: Int): Float                              = java.lang.Float.intBitsToFloat(value)
}
