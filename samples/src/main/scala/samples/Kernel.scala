package samples

import java.util.HexFormat
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Runs a kernel that reads one 32-bit input per lane and writes one 32-bit output per lane, on a device of its own. */
object Kernel {
  val Lanes: Int = 8

  val inputs: Vec[Long] = Vec.tabulate(Lanes)(lane => lane * 10L)

  /** The kernel's outputs for [[inputs]], given the order its parameters take the input and output buffers in. */
  def run(
      backend: Backend,
      image: Vec[Byte],
      name: NonEmpty[String],
      arguments: (input: AllocationId, output: AllocationId) => Vec[KernelTask.Argument]
  ): Either[String, Vec[Long]] = {
    val memory = ArrayMemory()
    val input  = memory.allocate((Lanes * 4L).unsafe[Long > 0])
    val output = memory.allocate((Lanes * 4L).unsafe[Long > 0])
    val device = Device(BackendRegistry(Vec(backend)), memory)
    for {
      _       <- memory.write(input, 0L, inputs.flatMap(LittleEndian.toVec(_, 4))).left.map(_.description.raw)
      program <- device.load(backend.format, image).left.map(_.description.raw)
      task    <-
        KernelTask.validated(name, Dimensions(Lanes.unsafe[Int > 0], 1, 1), Dimensions.One, arguments(input, output))
          .left.map(_.raw)
      _ <- try device.launch(program, task).left.map(_.description.raw)
      finally program.close()
    } yield Vec.tabulate(Lanes)(lane => LittleEndian.decode(memory.bytes(output), lane * 4, 4))
  }

  /** Bytes written as hexadecimal text, ignoring whitespace. */
  def hex(text: String): Vec[Byte] = Vec.unsafeWrapArray(HexFormat.of().parseHex(text.filterNot(_.isWhitespace)))
}
