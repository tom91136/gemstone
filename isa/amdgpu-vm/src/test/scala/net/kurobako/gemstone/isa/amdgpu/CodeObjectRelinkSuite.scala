package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class CodeObjectRelinkSuite extends munit.FunSuite {
  private val vectorCopy  = CodeObject.decode(Vec.unsafeWrapArray(bytes.hex(codeObjects.vectorCopyGfx906))).value
  private val noOperation = Vec.unsafeWrapArray(bytes.words(0xbf800000))

  private def kernel(codeObject: CodeObject, name: String): (Long, Vec[Byte]) = {
    val image  = elf.Image.parse(codeObject.image).value
    val symbol = image.symbols.flatMap(_.find(_.name.exists(_.raw == name))).getOrElse(fail(s"missing $name"))
    symbol.value -> image.symbolBytes(symbol).value
  }

  private def run(codeObject: CodeObject, name: NonEmpty[String], buffers: Int): Either[Fault, ArrayMemory.Snapshot] = {
    val memory  = ArrayMemory()
    val regions = Vec.fill(buffers)(memory.allocate(256L))
    regions.headOption.foreach(region => memory.write(region, 0L, Vec.tabulate(256)(_.toByte)).value)
    val device = Device(BackendRegistry(Vec(AMDGPUBackend())), memory)
    device.load(ProgramFormat.HSACodeObject, codeObject.image).flatMap { program =>
      try {
        val task = KernelTask.validated(
          name,
          Dimensions(64, 1, 1),
          Dimensions.One,
          regions.map(KernelTask.Argument.Memory(_, 0L))
        ).value
        device.launch(program, task).map(_ => memory.snapshot())
      } finally program.close()
    }
  }

  test("a kernel that ends its section grows in place and keeps running as before") {
    val (address, code) = kernel(vectorCopy, "vector_copy")
    val grown           = vectorCopy.replaceKernel("vector_copy", noOperation ++ code).value
    assertEquals(kernel(grown, "vector_copy"), address -> (noOperation ++ code))
    val dynamic         = elf.Image.parse(grown.image).value.symbolTables.values.flatten
      .filter(_.name.exists(_.raw == "vector_copy")).map(_.size.raw).toVector
    assertEquals(dynamic, Vector(code.size + 4, code.size + 4))
    assertEquals(run(grown, "vector_copy", 3), run(vectorCopy, "vector_copy", 3))
    assertEquals(vectorCopy.replaceKernel("vector_copy", code).value, vectorCopy)
  }

  test("a kernel followed by other code moves to the end of its section, descriptor and all") {
    val endProgram = bytes.words(0xbf810000)
    val image      = CodeObject.decode(Vec.unsafeWrapArray(elfImage.amdgpu(
      endProgram ++ endProgram,
      kernargSize = 0,
      kernelCodeProperties = 0x0a,
      extraSymbols = Seq(elfImage.Symbol("tail", 2, 4, 0x1004L, 4L))
    ))).value
    val trap  = noOperation ++ noOperation ++ Vec.unsafeWrapArray(bytes.words(0xbf920003))
    val moved = image.replaceKernel("kernel", trap).value
    assertEquals(kernel(moved, "kernel"), 0x1100L -> trap)
    assertEquals(kernel(moved, "tail")._1, 0x1004L)
    val descriptor = elf.Image.parse(moved.image).value
    val entry      = kernel(moved, "kernel.kd")._2
    assertEquals(LittleEndian.decodeInt64(entry, 16), 0x1100L - 0x2000L)
    assert(run(moved, "kernel", 0).left.exists(_.message.raw.contains("s_trap 3")), run(moved, "kernel", 0))
    assert(descriptor.section(".text").exists(_.size.raw == 0x10c))
  }
}
