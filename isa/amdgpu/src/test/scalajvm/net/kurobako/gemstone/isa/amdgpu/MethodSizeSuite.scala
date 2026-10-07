package net.kurobako.gemstone.isa.amdgpu

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.testing.*
import scala.jdk.CollectionConverters.*
import scala.util.Using

class MethodSizeSuite extends munit.FunSuite {
  // Classes that run per executed instruction or per decoded word, with their nested and anonymous classes.
  private val PerInstruction = Seq(
    "InstructionDecoder",
    "InstructionLayout",
    "InstructionWord",
    "SourceOperand",
    "VMAddressSpace",
    "VMArithmetic",
    "VMCompiler",
    "VMDispatchMemory",
    "VMInterpreter",
    "VMRegisterBounds",
    "VMState"
  )
  // The margin makes growth fail here before HotSpot stops compiling the method.
  private val Limit = bytecode.HugeMethodLimit - 1000

  test("per-instruction methods stay well below HotSpot's huge-method limit") {
    val packageName = classOf[ISA].getPackageName
    val directory   =
      Path.of(classOf[ISA].getProtectionDomain.getCodeSource.getLocation.toURI).resolve(packageName.replace('.', '/'))
    val classes = Using.resource(Files.list(directory))(_.toList.asScala.map(_.getFileName.toString).toVector)
      .filter(_.endsWith(".class")).map(_.stripSuffix(".class"))
      .filter(name => PerInstruction.exists(owner => name == owner || name.startsWith(s"$owner$$")))
    assert(PerInstruction.forall(owner => classes.exists(_.startsWith(owner))), classes)
    val oversized = for {
      name           <- classes
      (method, size) <- bytecode.methodSizes(Class.forName(s"$packageName.$name", false, getClass.getClassLoader))
      if size > Limit
    } yield s"$name.$method" -> size
    assertEquals(oversized.toMap, Map.empty[String, Int])
  }
}
