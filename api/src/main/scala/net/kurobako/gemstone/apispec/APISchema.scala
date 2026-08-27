package net.kurobako.gemstone.apispec

enum CType(val cName: String) {
  case Int32          extends CType("int32_t")
  case PointerToInt32 extends CType("int32_t *")
}

enum Direction {
  case In
  case Out
  case InOut
}

enum ReturnConvention {
  case StatusCode
  case DirectInt32
}

final case class Parameter(
    name: String,
    cType: CType,
    direction: Direction,
    nullable: Boolean = false,
    elements: Int = 1
)

final case class APIFunction(
    name: String,
    opcode: Int,
    returnConvention: ReturnConvention,
    parameters: Vector[Parameter],
    threadLocalError: Boolean = false,
    synchronous: Boolean = true
)

final case class API(
    name: String,
    abiVersion: String,
    soname: String,
    symbolVersion: String,
    functions: Vector[APIFunction]
)

object GemstoneAPIRegistry {
  val protocolVersion: Int = 1

  val cuda: API = API(
    name = "cuda",
    abiVersion = "phase-0",
    soname = "libcuda.so.1",
    symbolVersion = "GEMSTONE_0.1",
    functions = Vector(
      APIFunction(
        name = "cudaGetDeviceCount",
        opcode = 1,
        returnConvention = ReturnConvention.StatusCode,
        parameters = Vector(
          Parameter("count", CType.PointerToInt32, Direction.Out, nullable = false, elements = 1)
        ),
        threadLocalError = true
      ),
      APIFunction(
        name = "gemstoneAddI32",
        opcode = 2,
        returnConvention = ReturnConvention.DirectInt32,
        parameters = Vector(
          Parameter("left", CType.Int32, Direction.In),
          Parameter("right", CType.Int32, Direction.In)
        )
      )
    )
  )

  val apis: Vector[API] = Vector(cuda)
}
