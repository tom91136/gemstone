package net.kurobako.gemstone.isa.spirv

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.testing.*
import scala.jdk.CollectionConverters.*
import scala.util.Using

class MethodSizeSuite extends munit.FunSuite {
  // Module loading, lowering and interpretation, with their nested and anonymous classes. Generated grammar tables
  // initialise once and are not listed.
  private val Hand = Seq(
    "ArithmeticOps",
    "AtomicOps",
    "Binary",
    "CollectiveOps",
    "ComparisonOps",
    "CompositeOps",
    "ControlOps",
    "ConversionOps",
    "CubeMap",
    "ExtendedOps",
    "ImageOps",
    "ImageTexelCodec",
    "LaneOps",
    "MemoryOps",
    "ModuleNormalisation",
    "Op",
    "OpenCLBuiltins",
    "OpenCLMath",
    "OperandDecoder",
    "RawInstruction",
    "RawModule",
    "ShaderLowering",
    "ShaderPreparation",
    "SharedDecoder",
    "Specialization",
    "SpecializationOperation",
    "VMCollectives",
    "VMInterpreter",
    "VMInvocation",
    "VMLoader",
    "VMLowering",
    "VMMemory",
    "VMProgram",
    "VMShaderInterface",
    "VMValue"
  )
  // The margin makes growth fail here before HotSpot stops compiling the method.
  private val Limit = bytecode.HugeMethodLimit - 1000

  private def classNames(directory: Path): Vector[String] =
    Using.resource(Files.list(directory))(_.toList.asScala.map(_.getFileName.toString).toVector)
      .filter(_.endsWith(".class")).map(_.stripSuffix(".class"))

  private def directory(owner: Class[?]): Path =
    Path.of(owner.getProtectionDomain.getCodeSource.getLocation.toURI).resolve(owner.getPackageName.replace('.', '/'))

  test("loading, lowering and interpreter methods stay well below HotSpot's huge-method limit") {
    val packageName = classOf[Header].getPackageName
    val vm          = directory(VMProgram.getClass)
    val classes     = Seq(directory(classOf[Header]), vm).flatMap(classNames(_))
      .filter(name => Hand.exists(owner => name == owner || name.startsWith(s"$owner$$"))).map(s"$packageName." + _) ++
      classNames(vm.resolve("graphics")).map(s"$packageName.graphics." + _)
    assert(Hand.forall(owner => classes.exists(_.startsWith(s"$packageName.$owner"))), classes)
    val oversized = for {
      name           <- classes
      (method, size) <- bytecode.methodSizes(Class.forName(name, false, getClass.getClassLoader))
      if size > Limit
    } yield s"$name.$method" -> size
    assertEquals(oversized.toMap, Map.empty[String, Int])
  }
}
