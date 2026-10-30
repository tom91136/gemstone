package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*

class CubinPatchSuite extends Suite {
  private val image = fixtures.Fixture.copyAddCubin

  private def text(module: Cubin.Module, kernel: String): Vec[Byte] =
    module.sections(module.kernels.find(_.name == kernel).getOrElse(fail(s"missing $kernel")).section).bytes

  test("a kernel's code is replaced in place with code of the same size") {
    val original = text(Cubin.decode(image).value, "copy_add")
    val edited   = original.updated(0, (original(0) ^ 1).toByte)
    val patched  = Cubin.patchKernel(image, "copy_add", edited).value
    assertEquals(text(Cubin.decode(patched).value, "copy_add"), edited)
    assertEquals(patched.size, image.size)
    assertEquals(Cubin.patchKernel(image, "copy_add", original).value, image)
  }

  test("patching rejects unknown kernels and code of a different size") {
    val original = text(Cubin.decode(image).value, "copy_add")
    assert(Cubin.patchKernel(image, "absent", original).isLeft)
    assert(Cubin.patchKernel(image, "copy_add", original ++ Vec.fill(16)(0.toByte)).isLeft)
  }
}
