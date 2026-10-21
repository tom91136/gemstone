package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class CodeObjectPatchSuite extends munit.FunSuite {
  private val endProgram  = bytes.words(0xbf810000)
  private val noOperation = bytes.words(0xbf800000)
  private val image       = CodeObject.decode(Vec.unsafeWrapArray(elfImage.amdgpu(
    endProgram,
    kernargSize = 0,
    kernelCodeProperties = 0x0a
  ))).value

  private def code(codeObject: CodeObject): Vec[Byte] = {
    val image = elf.Image.parse(codeObject.image).value
    image.symbols.flatMap(
      _.find(_.name.exists(_.raw == "kernel"))
    ).fold(fail("missing kernel"))(image.symbolBytes(_).value)
  }

  test("a kernel's code is replaced at its symbol with code of the same size") {
    val patched = image.patchKernel("kernel", Vec.unsafeWrapArray(noOperation)).value
    assertEquals(code(patched), Vec.unsafeWrapArray(noOperation))
    assertEquals(patched.image.size, image.image.size)
    assertEquals(image.patchKernel("kernel", Vec.unsafeWrapArray(endProgram)).value, image)
  }

  test("a patched code object loads and runs the new code") {
    def run(codeObject: CodeObject) = AMDGPUBackend().load(codeObject.image).flatMap { program =>
      try program.execute(fixtures.singleTask(), vm.TaskContext(RegionMemory(), vm.TaskControl(), _ => Right(())))
      finally program.close()
    }
    assertEquals(run(image), Right(()))
    val trap = image.patchKernel("kernel", Vec.unsafeWrapArray(bytes.words(0xbf920002))).value
    assert(run(trap).left.exists(_.message.raw.contains("s_trap 2")), run(trap))
  }

  test("patching rejects unknown kernels and code of a different size") {
    assert(image.patchKernel("absent", Vec.unsafeWrapArray(noOperation)).isLeft)
    assert(image.patchKernel("kernel", Vec.unsafeWrapArray(noOperation ++ noOperation)).isLeft)
  }
}
