package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Loads once per stage and invokes bounded batches locally, under the submission's memory and budget. */
private class ShaderExecutor(
    shader: GraphicsShader,
    memory: GraphicsAccess,
    handler: Event => Either[Fault, Unit],
    program: VMProgram
) {
  private val descriptors = shader.arguments.map { argument =>
    val range = argument.getOrElse(throw IllegalArgumentException("shader descriptor was not prepared"))
    KernelTask.Argument.Memory(range.allocation, range.offset, Some(range.size))
  }
  private val push: Vec[KernelTask.Argument] = if (shader.pushConstants.isEmpty) Vec.empty
  else {
    val (region, size) = memory.allocate(shader.pushConstants.toArray)
    Vec(KernelTask.Argument.Memory(region, 0L, Some(size)))
  }
  private val relocations = shader.relocations
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def execute(count: Int >= 0, records: Array[Array[Byte]]): Unit = {
    require(!shader.fragmentQuads || count.raw % 4 == 0, "invalid shader invocation count")
    require(records.length == shader.interfaces.size, "shader interface record count mismatch")
    if (count.raw > 0) {
      val regions = records.zipWithIndex.map { (bytes, index) =>
        require(
          bytes.length.toLong == (count.toLong.widen[Long >= 0] * shader.interfaces(index).size.toLong).raw,
          "shader record storage size mismatch"
        )
        memory.allocate(bytes)._1
      }
      var start: Int >= 0 = 0
      while (start < count) {
        val batch          = (count - start).raw.unsafe[Int > 0].min(1024)
        val group: Int > 0 = if (shader.fragmentQuads) 4 else 1
        val arguments      = descriptors ++ push ++ Vec.from(regions.zipWithIndex.map { (region, index) =>
          KernelTask.Argument.Memory(
            region,
            start.toLong.widen[Long >= 0] * shader.interfaces(index).size.toLong,
            Some(batch.toLong.widen[Long > 0] * shader.interfaces(index).size.toLong)
          )
        })
        val task = KernelTask.validated(
          shader.entry,
          LaunchGeometry.Dimensions(group, 1, 1),
          LaunchGeometry.Dimensions(batch.ceilDiv(group), 1, 1),
          arguments,
          pointerRelocations = relocations,
          workDim = 1
        )
          .fold(error => throw GraphicsAccess.Failure(Fault.InvalidRequest(error)), identity)
        program.execute(task, memory, memory.control, handler)
          .fold(fault => throw GraphicsAccess.Failure(fault), _ => ())
        start = start + batch
      }
    }
  }
}
