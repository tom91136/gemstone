package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import scala.util.Using

class ExecutionSpecializationSuite extends munit.FunSuite {
  test("an execution service needs no image specializer") {
    Using.resource(ExecutionSession(Nil)) { service =>
      assert(service.describeSpecialization(Vec.empty).isLeft)
      assert(service.specializeImage(Vec.empty, Vec.empty).isLeft)
    }
  }

  test("image transformation uses the configured capability without loading a program") {
    val image     = Vec[Byte](1, 2, 3)
    val constants = Vec(SpecializationConstant(Binary32(7L), Vec[Byte](9)))
    object Specializer extends RuntimeBackend.Specialization {
      override def describe(input: Vec[Byte]): Either[ExecutionFailure, Vec[SpecializationInfo]] = {
        assertEquals(input, image)
        Right(Vec(SpecializationInfo(Binary32(7L), 1)))
      }
      override def specialize(
          input: Vec[Byte],
          supplied: Vec[SpecializationConstant]
      ): Either[ExecutionFailure, Vec[Byte]] = {
        assertEquals(input, image)
        assertEquals(supplied, constants)
        Right(input ++ supplied.head.value)
      }
    }
    Using.resource(ExecutionSession(Nil, specialization = Some(Specializer))) { service =>
      assertEquals(service.describeSpecialization(image), Right(Vec(SpecializationInfo(Binary32(7L), 1))))
      assertEquals(service.specializeImage(image, constants), Right(Vec[Byte](1, 2, 3, 9)))
    }
  }
}
