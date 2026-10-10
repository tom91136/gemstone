package net.kurobako.gemstone.isa.spirv

import Operand.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import scala.collection.mutable

/** Add the standard builtin records needed by the software graphics runtime in one pass. Shader instructions remain
  * unchanged. New IDs and declarations belong to this module only.
  */
private object ShaderLowering {
  case class Result(module: Module, earlyFragmentTests: Boolean, fragmentQuads: Boolean)

  private val quadInstructions = Set(
    Instruction.Definition.ImageSampleImplicitLod,
    Instruction.Definition.ImageSampleDrefImplicitLod,
    Instruction.Definition.ImageSampleProjImplicitLod,
    Instruction.Definition.ImageSampleProjDrefImplicitLod,
    Instruction.Definition.ImageQueryLod,
    Instruction.Definition.DPdx,
    Instruction.Definition.DPdy,
    Instruction.Definition.Fwidth,
    Instruction.Definition.DPdxFine,
    Instruction.Definition.DPdyFine,
    Instruction.Definition.FwidthFine,
    Instruction.Definition.DPdxCoarse,
    Instruction.Definition.DPdyCoarse,
    Instruction.Definition.FwidthCoarse
  )

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def lower(module: Module, stage: ShaderStageKind): Result = {
    val fragment = stage == ShaderStageKind.Fragment
    val quads = fragment && module.instructions.exists(instruction => quadInstructions.contains(instruction.definition))
    val early = fragment && module.instructions.exists {
      case Instruction.ExecutionMode(_, mode) => unsigned(mode) == ExecutionMode.EarlyFragmentTests
      case _                                  => false
    }
    val interfaces = module.instructions.collect { case Instruction.EntryPoint(interface = values) =>
      values.map(id(_)).toSet
    }.flatten.toSet
    val builtins = module.instructions.collect {
      case Instruction.Decorate(target, decoration)
          if interfaces.contains(id(target)) &&
            unsigned(decoration) == Decoration.BuiltIn => id(target) -> decoration.words.apply(1).raw
    }.toMap
    val variables = module.instructions.collect { case Instruction.Variable(_, dest, storage, _) =>
      id(dest) -> unsigned(storage)
    }.toMap
    def hasBuiltin(builtin: Long, storage: Long): Boolean = builtins.exists { case (variable, value) =>
      value.toLong == builtin && variables.get(variable).contains(storage)
    }
    val inputAttachments = fragment && module.instructions.exists {
      case Instruction.TypeImage(dim = dim) => unsigned(dim) == Dim.SubpassData
      case _                                => false
    }
    val kill         = fragment && module.instructions.exists { case Instruction.Kill() => true; case _ => false }
    val declarations = mutable.ArrayBuffer.empty[Instruction]
    val decorations  = mutable.ArrayBuffer.empty[Instruction]
    val added        = mutable.ArrayBuffer.empty[Operand]
    var bound        = module.header.bound.toUnsigned.raw
    def fresh(): Int = {
      require(bound > 0 && bound < Int.MaxValue, "shader ID bound exhausted")
      val result = bound.toInt
      bound += 1
      result
    }
    def operand(kind: OperandKind, words: Int*): Operand =
      Operand(kind, Vec.from(words.map(Word(_))).unsafe)
    def result(value: Int): Operand   = operand(OperandKind.IdResult, value)
    def ref(value: Int): Operand      = operand(OperandKind.IdRef, value)
    def typ(value: Int): Operand      = operand(OperandKind.IdResultType, value)
    def literal(value: Int): Operand  = operand(OperandKind.LiteralInteger, value)
    def storage(value: Long): Operand = operand(OperandKind.StorageClass, value.toInt)
    def declaration(existing: PartialFunction[Instruction, Int], create: Int => Instruction): Int =
      (module.instructions ++ Vec.from(declarations)).collect(existing).headOption.getOrElse {
        val value = fresh()
        declarations += create(value)
        value
      }
    def variable(pointee: Int, storageClass: Long, builtin: Long, initialiser: Option[Int] = None): Unit = {
      val pointer = declaration(
        {
          case Instruction.TypePointer(dest, stored, target)
              if unsigned(stored) == storageClass && id(target) == pointee => id(dest)
        },
        value => Instruction.TypePointer(result(value), storage(storageClass), ref(pointee))
      )
      val value = fresh()
      declarations += Instruction.Variable(typ(pointer), result(value), storage(storageClass), initialiser.map(ref(_)))
      decorations += Instruction.Decorate(
        ref(value),
        operand(OperandKind.Decoration, Decoration.BuiltIn, builtin.toInt)
      )
      added += ref(value)
    }
    if (inputAttachments && !hasBuiltin(BuiltIn.FragCoord, StorageClass.Input)) {
      val float = declaration(
        { case Instruction.TypeFloat(dest, width, None) if id(width) == 32 => id(dest) },
        value => Instruction.TypeFloat(result(value), literal(32), None)
      )
      val vector = declaration(
        { case Instruction.TypeVector(dest, component, count) if id(component) == float && id(count) == 4 => id(dest) },
        value => Instruction.TypeVector(result(value), ref(float), literal(4))
      )
      variable(vector, StorageClass.Input, BuiltIn.FragCoord)
    }
    if (kill && !hasBuiltin(BuiltIn.SampleMask, StorageClass.Output)) {
      val uint = declaration(
        { case Instruction.TypeInt(dest, width, signed) if id(width) == 32 && id(signed) == 0 => id(dest) },
        value => Instruction.TypeInt(result(value), literal(32), literal(0))
      )
      val one = fresh(); val all = fresh(); val array = fresh(); val initial = fresh()
      declarations +=
        Instruction.Constant(typ(uint), result(one), operand(OperandKind.LiteralContextDependentNumber, 1))
      declarations +=
        Instruction.Constant(typ(uint), result(all), operand(OperandKind.LiteralContextDependentNumber, -1))
      declarations += Instruction.TypeArray(result(array), ref(uint), ref(one))
      declarations += Instruction.ConstantComposite(typ(array), result(initial), Vec(ref(all)))
      variable(array, StorageClass.Output, BuiltIn.SampleMask, Some(initial))
    }
    // Early depth/stencil rejection must suppress shader side effects even when
    // the shader does not need derivative quads.
    if ((quads || early) && !hasBuiltin(BuiltIn.HelperInvocation, StorageClass.Input)) {
      val boolean =
        declaration({ case Instruction.TypeBool(dest) => id(dest) }, value => Instruction.TypeBool(result(value)))
      variable(boolean, StorageClass.Input, BuiltIn.HelperInvocation)
    }
    if (added.isEmpty) Result(module, early, quads)
    else {
      val (prefix, functions) = module.instructions.span { case _: Instruction.Function => false; case _ => true }
      require(functions.nonEmpty, "shader has no function")
      val (preamble, data) = prefix.span(instruction => instruction.definition.opcodeNumber.raw < SPIRV.Opcode.TypeVoid)
      val instructions     = (preamble ++ Vec.from(decorations) ++ data ++ Vec.from(declarations) ++ functions).map {
        case Instruction.EntryPoint(model, function, name, values) =>
          Instruction.EntryPoint(model, function, name, values ++ Vec.from(added))
        case value => value
      }
      Result(
        module.copy(header = module.header.copy(bound = Word(bound.toInt)), instructions = instructions),
        early,
        quads
      )
    }
  }
}
