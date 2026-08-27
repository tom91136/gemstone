package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.cuda.CUDA

import scala.compiletime.testing.typeCheckErrors

class RPCDerivationSuite extends munit.FunSuite {
  private val schema = RPCDerivation.schema[CUDA]

  test("CUDA trait owns the complete GPUOcelot Driver surface") {
    val driver = RPCDerivation.methodNames[CUDA].filter(name => name.startsWith("cu") && !name.startsWith("cuda"))
    assertEquals(driver.size, 144)
    assertEquals(driver.distinct.size, 144)
    assert(driver.contains("cuMemAlloc_v2"))
    assert(driver.contains("cuLaunchKernel"))
  }

  test("derives stable identities solely from exact C function names") {
    assertEquals(schema.methods.size, 20)
    assertEquals(schema.methods.map(_.name), schema.methods.map(_.name).sorted)
    assertEquals(schema.methods.map(_.name).distinct.size, schema.methods.size)
    assertEquals(schema.methods.map(_.messageId).distinct.size, schema.methods.size)
    assertEquals(schema.fingerprint.length, 64)
    assertEquals(schema, RPCDerivation.schema[CUDA])
  }

  test("rejects a Scala name that is not a CUDA C symbol") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.cuda.CUDAResult
      import net.kurobako.gemstone.rpc.RPCDerivation
      trait InvalidName {
        def free(pointer: Long): CUDAResult[Unit]
      }
      RPCDerivation.schema[InvalidName]
    """)
    assert(errors.exists(_.message.contains("exact C symbol")))
  }

  test("rejects overloaded endpoints") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.cuda.CUDAResult
      import net.kurobako.gemstone.rpc.RPCDerivation
      trait Overloaded {
        def cuThing(value: Int): CUDAResult[Unit]
        def cuThing(value: Int, other: Int): CUDAResult[Unit]
      }
      RPCDerivation.schema[Overloaded]
    """)
    assert(errors.exists(_.message.contains("may not be overloaded")))
  }

  test("rejects a parameter without an explicit wire type") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.cuda.CUDAResult
      import net.kurobako.gemstone.rpc.RPCDerivation
      trait UnsafePointer {
        def cuUnsafe(pointer: Long): CUDAResult[Unit]
      }
      RPCDerivation.schema[UnsafePointer]
    """)
    assert(errors.exists(_.message.contains("unsupported RPC wire type")))
  }
}
