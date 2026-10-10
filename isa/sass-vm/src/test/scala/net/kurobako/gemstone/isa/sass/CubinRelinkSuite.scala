package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class CubinRelinkSuite extends munit.FunSuite {
  private val image  = fixtures.Fixture.copyAddCubin
  private val module = Cubin.decode(image).value
  private val text   =
    module.sections(module.kernels.find(_.name == "copy_add").getOrElse(fail("missing copy_add")).section.raw).bytes
  private val code = CodeSection.decode(Vec.toArray(text), Target.of(module.sm).value).value
  private val nop  = code.instructions.find(_.mnemonic.raw == "NOP").map(instruction =>
    text.slice(instruction.address.raw, instruction.address.raw + 16)
  ).getOrElse(fail("copy_add has no NOP padding"))

  private def exits(cubin: Vec[Byte]): Vec[Long] = {
    val parsed = elf.Image.parse(cubin).value
    val info   = parsed.contents(parsed.section(".nv.info.copy_add").get)
    val at     = info.indexOfSlice(Vec[Byte](4, 0x1c))
    val size   = LittleEndian.decodeUInt16(info, at + 2).raw
    Vec.tabulate(size / 4)(index => LittleEndian.decodeInt32(info, at + 4 + index * 4).toLong & 0xffffffffL)
  }

  private def run(cubin: Vec[Byte]): ArrayMemory.Snapshot = {
    val memory  = ArrayMemory()
    val source  = memory.allocate(128L)
    val target  = memory.allocate(128L)
    val _       = memory.write(source, 0L, Vec.tabulate(128)(_.toByte))
    val device  = Device(BackendRegistry(Vec(CubinBackend())), memory)
    val program = device.load(ProgramFormat.CUDACubin, cubin).value
    try {
      val task = KernelTask.validated(
        "copy_add",
        Dimensions(32, 1, 1),
        Dimensions.One,
        Vec(KernelTask.Argument.Memory(source, 0L), KernelTask.Argument.Memory(target, 0L))
      ).value
      assert(device.launch(program, task).isRight)
      memory.snapshot()
    } finally program.close()
  }

  test("a kernel grown by an instruction keeps running as before, with its exit offsets moved") {
    val relinked = Cubin.replaceKernel(image, "copy_add", nop ++ text).value
    val decoded  = Cubin.decode(relinked).value
    assertEquals(
      decoded.sections(decoded.kernels.find(_.name == "copy_add").get.section.raw).bytes,
      nop ++ text
    )
    assertEquals(exits(relinked), exits(image).map(_ + 16))
    assertEquals(run(relinked), run(image))
  }

  test("replacing a kernel with its own code reproduces the image") {
    assertEquals(Cubin.replaceKernel(image, "copy_add", text).value, image)
  }
}
