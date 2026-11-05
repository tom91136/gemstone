package net.kurobako.gemstone.isa.spirv

import Operand.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Combines SPIR-V modules into one, resolving `LinkageAttributes` imports against exports of the other modules.
  *
  * An import resolves against an export or `LinkOnceODR` definition in the set. Imports the loader supplies by name
  * (`supplied`) or as a `BuiltIn` variable stay as imports, and any other unresolved import fails the link. Resolved
  * imports, duplicate `LinkOnceODR` definitions and structurally equal undecorated types and constants collapse into
  * one declaration. Exports keep their linkage decoration, so a linked module links again with further modules.
  */
object Linkage {

  private case class Symbol(name: String, id: Int, linkage: Int)

  private case class Part(top: Vector[Instruction], functions: Vector[Vector[Instruction]])

  private val Unique = Set("OpCapability", "OpExtension", "OpMemoryModel")

  private val Mergeable = Set(
    "OpConstant",
    "OpConstantTrue",
    "OpConstantFalse",
    "OpConstantNull",
    "OpConstantComposite",
    "OpExtInstImport"
  )

  def link(images: Vec[Vec[Byte]], supplied: String => Boolean): Either[Fault, Backend.LinkedImage] = for {
    modules           <- images.toVector.traverse(image => ISA.decode(image).left.map(error => failure(error.message)))
    (module, kernels) <- link(modules, supplied)
    bytes             <- ISA.encode(module).left.map(error => failure(error.message))
  } yield Backend.LinkedImage(bytes, Vec.from(kernels))

  def link(
      modules: Vector[Module],
      supplied: String => Boolean
  ): Either[Fault, (Module, Vector[Backend.LinkedKernel])] = {
    val kernels = linkedKernels(modules)
    modules match {
      case Vector()       => Left(failure("no modules to link"))
      case Vector(single) => Right((single, kernels))
      case _              => combine(modules, kernels, supplied).map(module => (module, kernels))
    }
  }

  private def combine(
      modules: Vector[Module],
      kernels: Vector[Backend.LinkedKernel],
      supplied: String => Boolean
  ): Either[Fault, Module] = {
    val offsets = modules.scanLeft(0)(_ + _.header.bound.raw)
    val model   = memoryModel(modules.head)
    for {
      _       <- Either.cond(modules.forall(memoryModel(_) == model), (), failure("memory models differ"))
      shifted <- modules.zip(offsets).zipWithIndex.traverse { case ((module, offset), index) =>
        renamed(module.instructions.toVector, kernels.filter(_.module == index))
          .flatMap(rewrite(_, id => id + offset))
          .map(split(_))
      }
      top       = shifted.flatMap(_.top)
      functions = shifted.flatMap(_.functions)
      symbols   = top.collect {
        case Instruction.Decorate(target, decoration)
            if unsigned(decoration) == Decoration.LinkageAttributes && decoration.words.length >= 3 =>
          Symbol(
            LiteralString.decode(decoration.words.slice(1, decoration.words.length - 1)),
            Operand.id(target),
            decoration.words.last.raw
          )
      }
      defined = symbols.filter(_.linkage != LinkageType.Import).groupBy(_.name)
      _ <- defined.collectFirst {
        case (name, group) if group.size > 1 && group.exists(_.linkage == LinkageType.Export) =>
          failure(s"symbol '$name' is exported more than once")
      }.toLeft(())
      alias = mutable.HashMap.empty[Int, Int]
      _ = defined.values.foreach(group => group.tail.foreach(duplicate => alias.update(duplicate.id, group.head.id)))
      imports = symbols.filter(_.linkage == LinkageType.Import)
      _ = imports.foreach(symbol => defined.get(symbol.name).foreach(group => alias.update(symbol.id, group.head.id)))
      resolve    = (id: Int) => alias.getOrElse(id, id)
      _          = merge(top, resolve, alias)
      signatures = signatureOf(top, functions)
      _ <- imports.collectFirst {
        case symbol
            if defined.get(symbol.name).exists(group =>
              signatures.get(symbol.id).map(_.map(resolve)) != signatures.get(group.head.id).map(_.map(resolve))
            ) =>
          failure(s"imported symbol '${symbol.name}' does not match its export")
      }.toLeft(())
      builtins = top.collect {
        case Instruction.Decorate(target, decoration) if unsigned(decoration) == Decoration.BuiltIn =>
          Operand.id(target)
      }.toSet
      _ <- imports.collectFirst {
        case symbol if !defined.contains(symbol.name) && !supplied(symbol.name) && !builtins.contains(symbol.id) =>
          failure(s"unresolved symbol '${symbol.name}'")
      }.toLeft(())
      (aliased, kept)        = functions.partition(function => alias.contains(functionId(function)))
      dropped                = alias.keySet.toSet ++ aliased.flatMap(parameters(_))
      survivors              = top.filter(instruction => !removed(instruction, alias, dropped))
      ordered                = distinctHeader(survivors.sortBy(rank(_)))
      (bodies, declarations) = kept.partition(hasBody(_))
      emitted <- rewrite(ordered ++ declarations.flatten ++ bodies.flatten, resolve)
    } yield Module(
      Header(modules.map(_.header.version).max, modules.head.header.generator, Word(offsets.last), Word(0)),
      Vec.from(emitted),
      modules.head.endianness
    )
  }

  private def linkedKernels(modules: Vector[Module]): Vector[Backend.LinkedKernel] = {
    val sources = modules.zipWithIndex.map { (module, index) =>
      module.instructions.toVector.collect {
        case i if i.opcode == "OpEntryPoint" => (index, LiteralString.decode(i.operands(2).words))
      }
    }
    val taken   = mutable.HashSet.from(sources.flatten.map(_._2))
    val claimed = mutable.HashSet.empty[String]
    val kernels = Vector.newBuilder[Backend.LinkedKernel]
    sources.foreach { entries =>
      val linked = entries.foldLeft(Vector.empty[Backend.LinkedKernel]) { case (acc, (index, name)) =>
        if (!claimed.contains(name)) acc :+ Backend.LinkedKernel(index, name, name)
        else {
          val fresh = Iterator.from(1).map(n => s"$name.$n").find(!taken.contains(_)).get
          taken += fresh
          acc :+ Backend.LinkedKernel(index, name, fresh)
        }
      }
      claimed ++= linked.map(_.linkageName)
      kernels ++= linked
    }
    kernels.result()
  }

  private def renamed(
      instructions: Vector[Instruction],
      kernels: Vector[Backend.LinkedKernel]
  ): Either[Fault, Vector[Instruction]] = {
    val entryPoints = instructions.scanLeft(0) { (count, instruction) =>
      if (instruction.opcode == "OpEntryPoint") count + 1 else count
    }
    instructions.zip(entryPoints).traverse { case (instruction, entry) =>
      if (instruction.opcode != "OpEntryPoint") Right(instruction)
      else {
        val kernel = kernels(entry)
        if (kernel.name == kernel.linkageName) Right(instruction) else withName(instruction, kernel.linkageName)
      }
    }
  }

  private def withName(instruction: Instruction, name: String): Either[Fault, Instruction] = {
    val operands = instruction.operands
    val words    = operands.take(2).flatMap(_.words) ++ LiteralString.encode(name) ++ operands.drop(3).flatMap(_.words)
    Instruction.decode(RawInstruction(instruction.opcodeNumber, words)).left.map(error => failure(error.message))
  }

  private def memoryModel(module: Module): Option[Vec[Word]] =
    module.instructions.collectFirst { case i if i.opcode == "OpMemoryModel" => i.operands.flatMap(_.words) }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def split(instructions: Vector[Instruction]): Part = {
    val top       = Vector.newBuilder[Instruction]
    val functions = Vector.newBuilder[Vector[Instruction]]
    var current   = Vector.newBuilder[Instruction]
    var inside    = false
    instructions.foreach { instruction =>
      if (instruction.opcode == "OpFunction") inside = true
      if (inside) current += instruction else top += instruction
      if (instruction.opcode == "OpFunctionEnd") {
        functions += current.result()
        current = Vector.newBuilder[Instruction]
        inside = false
      }
    }
    Part(top.result(), functions.result())
  }

  private def functionId(function: Vector[Instruction]): Int = Operand.id(function.head.operands(1))

  private def hasBody(function: Vector[Instruction]): Boolean = function.exists(_.opcode == "OpLabel")

  private def parameters(function: Vector[Instruction]): Vector[Int] =
    function.collect { case i if i.opcode == "OpFunctionParameter" => Operand.id(i.operands(1)) }

  private def signatureOf(top: Vector[Instruction], functions: Vector[Vector[Instruction]]): Map[Int, Vector[Int]] =
    (top.collect {
      case i if i.opcode == "OpVariable" =>
        Operand.id(i.operands(1)) -> Vector(1, Operand.id(i.operands(0)), Operand.id(i.operands(2)))
    } ++ functions.map(function =>
      functionId(function) -> Vector(0, Operand.id(function.head.operands(0)), Operand.id(function.head.operands(3)))
    )).toMap

  private def result(instruction: Instruction): Option[Int] =
    instruction.operands.collectFirst { case operand if operand.kind == OperandKind.IdResult => Operand.id(operand) }

  private def mergeable(instruction: Instruction): Boolean = {
    val opcode: String = instruction.opcode
    (opcode.startsWith("OpType") && opcode != "OpTypeForwardPointer") || Mergeable.contains(opcode)
  }

  private def decorates(instruction: Instruction): Boolean = {
    val opcode: String = instruction.opcode
    opcode.startsWith("OpDecorate") || opcode.startsWith("OpMemberDecorate")
  }

  /** Aliases each type, constant and extended instruction set import to its first structurally equal declaration. */
  private def merge(top: Vector[Instruction], resolve: Int => Int, alias: mutable.HashMap[Int, Int]): Unit = {
    val decorated = top.collect { case i if decorates(i) => Operand.id(i.operands.head) }.toSet
    val seen      = mutable.HashMap.empty[(String, Vector[Int]), Int]
    top.foreach { instruction =>
      if (mergeable(instruction)) result(instruction).filterNot(decorated.contains).foreach { id =>
        val key = (
          instruction.opcode: String,
          instruction.operands.filter(_.kind != OperandKind.IdResult)
            .flatMap(operand => remap(operand, resolve)).map(_.raw).toVector
        )
        seen.get(key) match {
          case Some(first) => alias.update(id, first)
          case None        => seen.update(key, id)
        }
      }
    }
  }

  private def removed(instruction: Instruction, alias: collection.Map[Int, Int], dropped: Set[Int]): Boolean = {
    val opcode: String = instruction.opcode
    if (opcode == "OpVariable" || mergeable(instruction)) result(instruction).exists(alias.contains)
    else
      rank(instruction) match {
        case 7 | 9 =>
          instruction.operands.headOption.exists(operand =>
            operand.kind == OperandKind.IdRef && dropped.contains(Operand.id(operand))
          )
        case _ => false
      }
  }

  private def rank(instruction: Instruction): Int = {
    val opcode: String = instruction.opcode
    opcode match {
      case "OpCapability"                                                      => 0
      case "OpExtension"                                                       => 1
      case "OpExtInstImport"                                                   => 2
      case "OpMemoryModel"                                                     => 3
      case "OpEntryPoint"                                                      => 4
      case "OpExecutionMode" | "OpExecutionModeId"                             => 5
      case "OpString" | "OpSource" | "OpSourceContinued" | "OpSourceExtension" => 6
      case "OpName" | "OpMemberName"                                           => 7
      case "OpModuleProcessed"                                                 => 8
      case "OpGroupDecorate" | "OpGroupMemberDecorate" | "OpDecorationGroup"   => 9
      case _ if decorates(instruction)                                         => 9
      case _                                                                   => 10
    }
  }

  private def distinctHeader(instructions: Vector[Instruction]): Vector[Instruction] = {
    val seen = mutable.HashSet.empty[(String, Vector[Int])]
    val kept = Vector.newBuilder[Instruction]
    instructions.foreach { instruction =>
      val opcode: String = instruction.opcode
      if (!Unique.contains(opcode) || seen.add((opcode, instruction.operands.flatMap(_.words).map(_.raw).toVector)))
        kept += instruction
    }
    kept.result()
  }

  private def rewrite(instructions: Vector[Instruction], f: Int => Int): Either[Fault, Vector[Instruction]] =
    instructions.traverse { instruction =>
      Instruction.decode(RawInstruction(instruction.opcodeNumber, instruction.operands.flatMap(remap(_, f))))
        .left.map(error => failure(error.message))
    }

  /** The words of `operand` with every id, including those in enumerant parameters, mapped through `f`. */
  private def remap(operand: Operand, f: Int => Int): Vec[Word] = {
    val words                                                     = operand.words
    def mapped(word: Word): Word                                  = Word(f(word.raw))
    def walk(kind: OperandKind, offset: Int): (Vector[Word], Int) =
      if (kind.category == OperandKind.Category.Id) (Vector(mapped(words(offset))), offset + 1)
      else if (kind == OperandKind.LiteralContextDependentNumber) (words.drop(offset).toVector, words.size)
      else if (kind == OperandKind.LiteralString) {
        val end = words.indexWhere(word => (0 until 4).exists(i => ((word.raw >>> (i * 8)) & 0xff) == 0), offset) + 1
        (words.slice(offset, end).toVector, end)
      } else Instruction.operandKinds.get(kind) match {
        case Some(info) =>
          val value    = words(offset).toUnsigned.toLong
          val selected = ExtensionEnumerants.selected(info, value)
          selected.flatMap(_.parameterKinds).foldLeft((Vector(words(offset)), offset + 1)) {
            case ((collected, at), parameter) =>
              val (next, end) = walk(parameter, at)
              (collected ++ next, end)
          }
        case None => (Vector(words(offset)), offset + 1)
      }
    operand.kind match {
      case OperandKind.PairLiteralIntegerIdRef => Vec.from(words.init :+ mapped(words.last))
      case OperandKind.PairIdRefLiteralInteger => Vec.from(mapped(words.head) +: words.tail)
      case OperandKind.PairIdRefIdRef          => words.map(mapped(_))
      case kind                                => Vec.from(walk(kind, 0)._1)
    }
  }

  private def failure(detail: String): Fault = Fault.InvalidRequest(s"linkage: $detail")
}
