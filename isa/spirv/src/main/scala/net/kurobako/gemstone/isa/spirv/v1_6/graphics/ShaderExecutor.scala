package net.kurobako.gemstone.isa.spirv.v1_6.graphics

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Loads once per stage and invokes bounded batches locally, under the submission's memory and budget. */
private class ShaderExecutor(
    shader: GraphicsShader,
    memory: GraphicsAccess,
    workgroups: Workgroups,
    handler: Event => Either[Fault, Unit],
    program: LoadedProgram
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
  private val relocations                                    = shader.relocations
  def execute(count: Int, records: Array[Array[Byte]]): Unit = {
    require(count >= 0 && (!shader.fragmentQuads || count % 4 == 0), "invalid shader invocation count")
    require(records.length == shader.interfaces.size, "shader interface record count mismatch")
    if (count > 0) {
      val regions = records.zipWithIndex.map { (bytes, index) =>
        require(
          bytes.length.toLong == count.toLong * shader.interfaces(index).size.raw,
          "shader record storage size mismatch"
        )
        memory.allocate(bytes)._1
      }
      for (first <- 0 until count by 1024) {
        val batch          = math.min(1024, count - first).unsafe[Int > 0]
        val start          = first.unsafe[Int >= 0]
        val group: Int > 0 = if (shader.fragmentQuads) 4 else 1
        val arguments      = descriptors ++ push ++ Vec.from(regions.zipWithIndex.map { (region, index) =>
          KernelTask.Argument.Memory(
            region,
            start.toLong * shader.interfaces(index).size.toLong,
            Some(batch.toLong * shader.interfaces(index).size.toLong)
          )
        })
        val task = KernelTask.validated(
          shader.entry,
          LaunchGeometry.Dimensions(group, 1, 1),
          LaunchGeometry.Dimensions((batch.raw / group.raw).unsafe[Int > 0], 1, 1),
          arguments,
          pointerRelocations = relocations,
          workDim = 1
        )
          .fold(error => throw GraphicsAccess.Failure(Fault.InvalidTask(error)), identity)
        program.execute(task, memory, memory.control, workgroups, handler)
          .fold(fault => throw GraphicsAccess.Failure(fault), _ => ())
      }
    }
  }
}
