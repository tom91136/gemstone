package net.kurobako.gemstone.execution

import net.kurobako.gemstone.Col

class ProtocolSuite extends munit.FunSuite {
  test("identities reject the wire sentinel") {
    assert(AllocationId.fromLong(0L).isLeft)
    assert(ProgramId.fromLong(-1L).isLeft)
    assert(CompletionId.fromLong(1L).isRight)
  }

  test("dispatch dimensions and kernel names are validated centrally") {
    val program = ProgramId.fromLong(1L).toOption.get
    assert(Dimensions.create(0, 1, 1).isLeft)
    val one = Dimensions.One
    assert(KernelDispatch.create(program, "", one, one, Col.empty).isLeft)
    assert(KernelDispatch.create(program, "fill", one, one, Col(KernelArgument.Scalar(Col(1.toByte)))).isRight)
  }

  test("launch geometry maps every linear group and local lane into three-dimensional coordinates") {
    val groupSize  = Dimensions.create(2, 3, 2).toOption.get
    val groupCount = Dimensions.create(3, 2, 1).toOption.get
    val geometry   = LaunchGeometry.create(groupSize, groupCount).toOption.get

    assertEquals(geometry.localCoordinates(7), Coordinates(1, 0, 1))
    assertEquals(geometry.groupCoordinates(4), Coordinates(1, 1, 0))
    assertEquals(
      geometry.globalCoordinates(Coordinates(1, 1, 0), Coordinates(1, 2, 1)),
      Coordinates(3, 5, 1)
    )
  }

  test("launch geometry carries the OpenCL work dimension and global offset") {
    val groupSize  = Dimensions.create(4, 1, 1).toOption.get
    val groupCount = Dimensions.create(2, 1, 1).toOption.get
    val geometry   = LaunchGeometry.create(
      groupSize,
      groupCount,
      Some(Dimensions.create(7, 1, 1).toOption.get),
      1,
      Coordinates(11, 0, 0)
    ).toOption.get
    assertEquals(geometry.workDim, 1)
    assertEquals(geometry.globalOffset, Coordinates(11, 0, 0))
    assertEquals(geometry.globalCoordinates(Coordinates(1, 0, 0), Coordinates(0, 0, 0)), Coordinates(15, 0, 0))
    assert(geometry.containsGlobal(Coordinates(11, 0, 0)))
    assert(geometry.containsGlobal(Coordinates(17, 0, 0)))
    assert(!geometry.containsGlobal(Coordinates(10, 0, 0)))
    assert(!geometry.containsGlobal(Coordinates(18, 0, 0)))
    assert(LaunchGeometry.create(groupSize, groupCount, workDim = 0).isLeft)
  }

  test("pointer relocations cannot refer to an absent kernel argument") {
    val program    = ProgramId.fromLong(1L).toOption.get
    val target     = AllocationId.fromLong(2L).toOption.get
    val relocation = PointerRelocation(
      sourceAllocation = None,
      sourceArgument = Some(1),
      sourceOffset = 0L,
      target = target,
      targetOffset = 0L
    )
    assert(
      KernelDispatch
        .create(
          program,
          "kernel",
          Dimensions.One,
          Dimensions.One,
          Col(KernelArgument.Scalar(Col.empty)),
          pointerRelocations = Col(relocation)
        )
        .left
        .exists(_.contains("in-range source argument"))
    )
  }
}
