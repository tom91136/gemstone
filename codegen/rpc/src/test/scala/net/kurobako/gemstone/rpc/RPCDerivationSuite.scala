package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.cuda.CUDA

import scala.compiletime.testing.typeCheckErrors

class RPCDerivationSuite extends munit.FunSuite {
  private val schema = RPCDerivation.schema[CUDA]

  test("CUDA trait owns the complete pinned Driver surface plus the legacy v2 context entry point") {
    val driver = RPCDerivation.methodNames[CUDA].filter(name => name.startsWith("cu") && !name.startsWith("cuda"))
    assertEquals(driver.size, 484)
    assertEquals(driver.distinct.size, 484)
    assert(driver.contains("cuMemAlloc_v2"))
    assert(driver.contains("cuLaunchKernel"))
  }

  test("derives stable identities solely from exact C function names") {
    assertEquals(schema.methods.size, 27)
    assertEquals(schema.methods.map(_.name).toSeq, schema.methods.map(_.name).sorted.toSeq)
    assertEquals(schema.methods.map(_.name).distinct.size, schema.methods.size)
    assertEquals(schema.methods.map(_.messageId).distinct.size, schema.methods.size)
    assertEquals(schema.fingerprint.length, 64)
    val repeated = RPCDerivation.schema[CUDA]
    assertEquals(schema.interfaceName, repeated.interfaceName)
    assertEquals(schema.fingerprint, repeated.fingerprint)
    def canonical(value: RPCSchema) = value.methods.map { method =>
      (method.name, method.messageId, method.parameters.toSeq, method.result)
    }.toSeq
    assertEquals(canonical(schema), canonical(repeated))
  }

  test("derives typed callback capabilities and their native user data") {
    val host = schema.methods.find(_.name == "cuLaunchHostFunc").getOrElse(fail("missing cuLaunchHostFunc"))
    assertEquals(
      host.parameters.map(parameter => parameter.name -> parameter.wireType).toVector,
      Vector("hStream" -> WireType.Stream, "fn" -> WireType.CUDAHostCallback, "userData" -> WireType.CallbackData)
    )

    val stream = schema.methods.find(_.name == "cuStreamAddCallback").getOrElse(fail("missing cuStreamAddCallback"))
    assertEquals(
      stream.parameters.map(parameter => parameter.name -> parameter.wireType).toVector,
      Vector(
        "hStream"  -> WireType.Stream,
        "callback" -> WireType.CUDAStreamCallback,
        "userData" -> WireType.CallbackData,
        "flags"    -> WireType.UInt32
      )
    )
  }

  test("rejects a Scala name that is not a CUDA C symbol") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.cuda.CUDAStatus
      import net.kurobako.gemstone.rpc.RPCDerivation
      trait InvalidName {
        def free(pointer: Long): CUDAStatus
      }
      RPCDerivation.schema[InvalidName]
    """)
    assert(errors.exists(_.message.contains("exact C symbol")))
  }

  test("rejects overloaded endpoints") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.cuda.CUDAStatus
      import net.kurobako.gemstone.rpc.RPCDerivation
      trait Overloaded {
        def cuThing(value: Int): CUDAStatus
        def cuThing(value: Int, other: Int): CUDAStatus
      }
      RPCDerivation.schema[Overloaded]
    """)
    assert(errors.exists(_.message.contains("may not be overloaded")))
  }

  test("rejects a parameter without an explicit wire type") {
    val errors = typeCheckErrors("""
      import net.kurobako.gemstone.cuda.CUDAStatus
      import net.kurobako.gemstone.rpc.RPCDerivation
      trait UnsafePointer {
        def cuUnsafe(pointer: Long): CUDAStatus
      }
      RPCDerivation.schema[UnsafePointer]
    """)
    assert(errors.exists(_.message.contains("unsupported RPC wire type")))
  }

  test("rejects invalid pointer extent relationships") {
    val missingCount = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      import net.kurobako.gemstone.cuda.CUDAStatus
      import net.kurobako.gemstone.rpc.RPCDerivation
      trait MissingCount {
        def cuMissing(buffer: InputBuffer[Byte, "count"]): CUDAStatus
      }
      RPCDerivation.schema[MissingCount]
    """)
    assert(missingCount.exists(_.message.contains("parameter is absent")))

    val nonIntegralCount = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      import net.kurobako.gemstone.cuda.*
      import net.kurobako.gemstone.rpc.RPCDerivation
      trait NonIntegralCount {
        def cuNonIntegral(buffer: InputBuffer[Byte, "count"], count: Stream): CUDAStatus
      }
      RPCDerivation.schema[NonIntegralCount]
    """)
    assert(nonIntegralCount.exists(_.message.contains("must have an integral ABI type")))

    val invalidFixed = typeCheckErrors("""
      import net.kurobako.gemstone.abi.*
      import net.kurobako.gemstone.cuda.CUDAStatus
      import net.kurobako.gemstone.rpc.RPCDerivation
      trait InvalidFixed {
        def cuInvalid(buffer: Pointer[Byte, Read, Fixed[0]]): CUDAStatus
      }
      RPCDerivation.schema[InvalidFixed]
    """)
    assert(invalidFixed.exists(_.message.contains("must be positive")))
  }
}
