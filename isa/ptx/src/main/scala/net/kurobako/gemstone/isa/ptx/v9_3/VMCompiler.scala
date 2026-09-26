package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VMIR.{Instruction as _, *}

import net.kurobako.gemstone.NonEmptyString
import net.kurobako.gemstone.NonNegativeInt
import net.kurobako.gemstone.PositiveInt
import net.kurobako.gemstone.PositiveLong
import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.vm.LaunchGeometry.Axis

import cats.syntax.all.*

import scala.collection.mutable

private[v9_3] object VMCompiler {

  enum SourceError {
    case Syntax(error: ParseError)
    case MissingEntry
    case Compilation(error: Error)

    def message: String = this match {
      case Syntax(error)      => error.message
      case MissingEntry       => "missing .entry declaration"
      case Compilation(error) => error.message
    }
  }

  def compile(source: String): Either[SourceError, Kernel] =
    Parser.parse(source).leftMap(SourceError.Syntax(_)).flatMap { program =>
      program.statements.collectFirst { case entry: Statement.Entry => entry }
        .toRight(SourceError.MissingEntry)
        .flatMap(entry =>
          compile(entry, program.statements.collect { case function: Statement.Function => function })
            .leftMap(SourceError.Compilation(_))
        )
    }

  enum Error {
    case Invalid(detail: String)
    case Unsupported(instruction: InstructionText, detail: String)

    def message: String = this match {
      case Invalid(detail)                  => detail
      case Unsupported(instruction, detail) =>
        s"unsupported instruction: ${instruction.raw} ($detail)"
    }
  }

  def compile(entry: Statement.Entry): Either[Error, Kernel] = compile(entry, Vec.empty, Map.empty)

  def parameterLayout(
      entry: Statement.Entry
  ): Either[Error, Vec[(KernelParameter, NonNegativeInt)]] =
    for {
      parameters <- lowerParameters(entry.parameters)
      offsets    <- parameterOffsets(entry.parameters, parameters)
    } yield parameters.zip(offsets)

  def compile(entry: Statement.Entry, functions: Vec[Statement.Function]): Either[Error, Kernel] =
    compile(entry, functions, Map.empty)

  def compile(
      entry: Statement.Entry,
      functions: Vec[Statement.Function],
      globalSymbols: Map[SymbolName, GuestAddress]
  ): Either[Error, Kernel] = compile(entry, functions, globalSymbols, Map.empty)

  def compile(
      entry: Statement.Entry,
      functions: Vec[Statement.Function],
      globalSymbols: Map[SymbolName, GuestAddress],
      additionalSharedSymbols: Map[SymbolName, Long]
  ): Either[Error, Kernel] =
    compile(entry, functions, globalSymbols, additionalSharedSymbols, Vec.empty)

  def compile(
      entry: Statement.Entry,
      functions: Vec[Statement.Function],
      globalSymbols: Map[SymbolName, GuestAddress],
      additionalSharedSymbols: Map[SymbolName, Long],
      additionalEntries: Vec[Statement.Entry]
  ): Either[Error, Kernel] = {
    val Statement.Entry(_, name, parameters, body) = entry
    val functionNames                              = functions.view.map(_.name).toSet ++
      additionalEntries.view
        .map(entry => FunctionName(entry.name.toNonEmptyString))
        .toSet
    for {
      loweredParameters <- lowerParameters(parameters)
      loweredProgram    <- lowerProgram(body, functionNames)
      loweredFunctions  <- lowerReachableFunctions(loweredProgram, functions, additionalEntries, functionNames)
    } yield Kernel(
      name,
      loweredParameters,
      loweredProgram,
      loweredFunctions,
      sharedSymbols(body) ++ additionalSharedSymbols,
      globalSymbols,
      registerChunkCount(loweredProgram, loweredFunctions)
    )
  }

  // Lowered register slots retain their class offset, so the highest referenced slot determines the directory size.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def registerChunkCount(
      program: ExecutableProgram[PredicatedInstruction],
      functions: Map[FunctionName, DeviceFunction]
  ): Int = {
    var highest                   = -1
    def inspect(value: Any): Unit = value match {
      case Value.RegisterSlot(index) => highest = math.max(highest, index)
      case raw: String               =>
        val index = VMRegisterFile.indexRaw(raw)
        if (index >= 0 && VMPredicateFile.indexRaw(raw) < 0) highest = math.max(highest, index)
      case values: Iterable[?] => values.foreach(inspect(_))
      case value: Product      => value.productIterator.foreach(inspect(_))
      case _                   => ()
    }
    program.instructions.foreach(inspect(_))
    functions.values.foreach(function => function.program.instructions.foreach(inspect(_)))
    if (highest < 0) 1 else (highest >>> 6) + 1
  }

  // Traverse the reachable call graph with a mutable work queue and fail on the first invalid callee.
  @SuppressWarnings(
    Array(
      "scalafix:DisableSyntax.return"
    )
  )
  private def lowerReachableFunctions(
      program: ExecutableProgram[PredicatedInstruction],
      functions: Vec[Statement.Function],
      entries: Vec[Statement.Entry],
      functionNames: Set[FunctionName]
  ): Either[Error, Map[FunctionName, DeviceFunction]] = {
    val functionDefinitions = functions.view.map(function => function.name -> function).toMap
    val entryDefinitions    = entries.view.map(entry => FunctionName(entry.name.toNonEmptyString) -> entry).toMap
    val pending             = mutable.Queue.empty[FunctionName]
    val lowered             = mutable.Map.empty[FunctionName, DeviceFunction]

    def enqueueCalls(value: ExecutableProgram[PredicatedInstruction]): Unit =
      value.instructions.foreach {
        case PredicatedInstruction(VMIR.Instruction.Call(Value.Function(function), _, _), _) =>
          if (!lowered.contains(function) && !pending.contains(function)) pending.enqueue(function)
        case _ => ()
      }

    def enqueueAddressTakenFunctions(value: ExecutableProgram[PredicatedInstruction]): Unit =
      value.instructions.foreach {
        case PredicatedInstruction(VMIR.Instruction.Move(_, Value.Function(function), _), _) =>
          if (!lowered.contains(function) && !pending.contains(function)) pending.enqueue(function)
        case _ => ()
      }

    enqueueCalls(program)
    enqueueAddressTakenFunctions(program)
    while (pending.nonEmpty) {
      val name = pending.dequeue()
      functionDefinitions.get(name) match {
        case Some(function) if function.body.nonEmpty =>
          lowerFunction(function, functionNames) match {
            case Left(error)  => return Left(error)
            case Right(value) =>
              lowered(name) = value
              enqueueCalls(value.program)
              enqueueAddressTakenFunctions(value.program)
          }
        case Some(_) => ()
        case None    => entryDefinitions.get(name) match {
            case Some(entry) =>
              lowerEntryFunction(entry, functionNames) match {
                case Left(error)  => return Left(error)
                case Right(value) =>
                  lowered(name) = value
                  enqueueCalls(value.program)
                  enqueueAddressTakenFunctions(value.program)
              }
            case None => ()
          }
      }
    }
    Right(lowered.toMap)
  }

  // Resolve labels and emit instructions in two allocation-conscious passes over the flattened program.
  @SuppressWarnings(
    Array(
      "scalafix:DisableSyntax.var",
      "scalafix:DisableSyntax.return"
    )
  )
  private def lowerProgram(
      body: Vec[Statement],
      functionNames: Set[FunctionName]
  ): Either[Error, ExecutableProgram[PredicatedInstruction]] = {
    val flat = flattenScoped(body) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val labels           = mutable.Map.empty[LabelName, Pc]
    var instructionIndex = 0
    var statementIndex   = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Label(label) =>
          if (labels.contains(label))
            return Left(Error.Invalid(s"duplicate label: $label"))
          labels(label) = Pc.unsafe(instructionIndex)
        case Statement.Instruction(_)                                       => instructionIndex += 1
        case Statement.UnsupportedInstruction(opcode, operands, mods, pred) =>
          val qualified = (opcode +: mods.map(_.name)).mkString(".")
          val rendered  = pred.map(value => s"${Printer.render(value)} ").getOrElse("") +
            qualified + (if (operands.isEmpty) "" else operands.map(Printer.render).mkString(" ", ", ", "")) + ";"
          return Left(
            Error.Unsupported(InstructionText.unsafe(rendered), "instruction is not implemented")
          )
        case _ => ()
      }
      statementIndex += 1
    }

    val resolvedLabels = labels.toMap
    val lowered        = Vec.newBuilder[PredicatedInstruction]
    statementIndex = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Instruction(instruction) =>
          lowerInstruction(instruction, resolvedLabels, functionNames) match {
            case Left(error) =>
              val contextual = error match {
                case Error.Invalid(detail) =>
                  Error.Invalid(s"${Printer.render(instruction)}: $detail")
                case Error.Unsupported(_, detail) =>
                  Error.Unsupported(Printer.render(instruction), detail)
              }
              return Left(contextual)
            case Right(value) => lowered += value
          }
        case _ => ()
      }
      statementIndex += 1
    }
    Right(ExecutableProgram(lowered.result()))
  }

  private def lowerFunction(
      function: Statement.Function,
      functionNames: Set[FunctionName]
  ): Either[Error, DeviceFunction] = {
    val Statement.Function(_, name, returnParameters, parameters, body) = function
    body match {
      case None             => Left(Error.Invalid(s"function $name has no body"))
      case Some(statements) =>
        for {
          _                 <- lowerParameters(returnParameters)
          loweredParameters <- lowerParameters(parameters)
          offsets           <- parameterOffsets(parameters, loweredParameters)
          program           <- lowerProgram(statements, functionNames)
        } yield DeviceFunction(
          FunctionName(name.toNonEmptyString),
          parameters.map(_.name),
          loweredParameters.map(_.widthBytes),
          offsets,
          returnParameters.map(_.name),
          program,
          sharedSymbols(statements)
        )
    }
  }

  private def lowerEntryFunction(
      entry: Statement.Entry,
      functionNames: Set[FunctionName]
  ): Either[Error, DeviceFunction] = {
    val Statement.Entry(_, name, parameters, body) = entry
    for {
      loweredParameters <- lowerParameters(parameters)
      offsets           <- parameterOffsets(parameters, loweredParameters)
      program           <- lowerProgram(body, functionNames)
    } yield DeviceFunction(
      FunctionName(name.toNonEmptyString),
      parameters.map(_.name),
      loweredParameters.map(_.widthBytes),
      offsets,
      Vec.empty,
      program,
      sharedSymbols(body)
    )
  }

  // Carry the ABI offset through the ordered parameter layout.
  // This is a syntax-validation boundary. Keep the successful layout proof instead of widening offsets back to Int.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def parameterOffsets(
      declarations: Vec[Parameter],
      parameters: Vec[KernelParameter]
  ): Either[Error, Vec[NonNegativeInt]] = {
    val result = Vec.newBuilder[NonNegativeInt]
    var offset = 0L
    var index  = 0
    while (index < declarations.size) {
      val declaration = declarations(index)
      val parameter   = parameters(index)
      val alignment   = declaration.qualifiers.collectFirst {
        case DeclarationQualifier.Alignment(bytes) => bytes
      }.getOrElse(parameter.widthBytes)
      val padding = (alignment.raw.toLong - (offset % alignment.raw.toLong)) % alignment.raw.toLong
      if (offset > Int.MaxValue.toLong - padding)
        return invalid(s"kernel parameter ${declaration.name} offset exceeds the supported range")
      offset += padding
      result += NonNegativeInt.unsafe(offset.toInt)
      if (offset > Int.MaxValue.toLong - parameter.widthBytes.raw.toLong)
        return invalid(s"kernel parameter ${declaration.name} layout exceeds the supported range")
      offset += parameter.widthBytes.raw.toLong
      index += 1
    }
    Right(result.result())
  }

  // Decode parameters in order and stop at the first malformed declaration.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def lowerParameters(parameters: Vec[Parameter]): Either[Error, Vec[KernelParameter]] = {
    val result = Vec.newBuilder[KernelParameter]
    var index  = 0
    while (index < parameters.length) {
      val parameter = parameters(index)
      val width     = PositiveInt.unsafe(parameter.elementType.widthBits.raw / 8)
      val elements  = parameter.dimensions.foldLeft[Either[Error, PositiveLong]](Right(PositiveLong.One)) {
        (current, value) =>
          for {
            count   <- current
            element <- immediate(value)
            product <-
              if (element > 0 && count.raw <= Int.MaxValue.toLong / element)
                Right(PositiveLong.unsafe(count.raw * element))
              else invalid(s"kernel parameter ${parameter.name} has an invalid array dimension: $element")
          } yield product
      }
      elements match {
        case Left(error)  => return Left(error)
        case Right(count) =>
          val totalWidth = (width.toLong * count).raw
          if (totalWidth > Int.MaxValue.toLong)
            return invalid(
              s"kernel parameter ${parameter.name} is too wide: $totalWidth bytes"
            )
          val pointer = parameter.qualifiers.exists {
            case DeclarationQualifier.Attribute(name) => name.raw == "ptr"
            case _                                    => false
          }
          result += KernelParameter(parameter.name, PositiveInt.unsafe(totalWidth.toInt), pointer)
      }
      index += 1
    }
    Right(result.result())
  }

  /** Flatten PTX blocks while alpha-renaming registers declared in nested scopes. */
  // The nested visitor shares one monotonically increasing scope identity and fails fast.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def flattenScoped(statements: Vec[Statement]): Either[Error, Vec[Statement]] = {
    var nextScope = 0

    def registerNames(operand: Operand): collection.View[RegisterName] = operand match {
      case Operand.Register(name, _)    => Vector(name).view
      case Operand.Symbol(name)         => Vector(RegisterName(name.toNonEmptyString)).view
      case Operand.VectorLiteral(items) => items.view.flatMap(registerNames(_))
      case Operand.Parenthesized(items) => items.view.flatMap(registerNames(_))
      case Operand.Qualified(base, _)   => registerNames(base)
      case _                            => Vector.empty.view
    }

    def declaredRegisters(body: Vec[Statement]): Vec[RegisterName] =
      Vec.toVec(body.view.flatMap {
        case Statement.Directive(path, arguments, _)
            if path.head.raw == "reg" =>
          arguments.view.flatMap(argument => registerNames(argument.operand))
        case _ => Vector.empty.view
      }).distinct

    def renameOperand(operand: Operand, names: Map[RegisterName, RegisterName]): Operand = operand match {
      case Operand.Register(name, part) => Operand.Register(names.getOrElse(name, name), part)
      case Operand.Symbol(name) if names.contains(RegisterName(name.toNonEmptyString)) =>
        Operand.Register(names(RegisterName(name.toNonEmptyString)), None)
      case Operand.VectorLiteral(items)  => Operand.VectorLiteral(items.map(renameOperand(_, names)))
      case Operand.Parenthesized(items)  => Operand.Parenthesized(items.map(renameOperand(_, names)))
      case Operand.Address(items)        => Operand.Address(items.map(renameOperand(_, names)))
      case Operand.Qualified(base, part) => Operand.Qualified(renameOperand(base, names), part)
      case Operand.Index(base, index)    => Operand.Index(renameOperand(base, names), renameOperand(index, names))
      case Operand.Add(left, right)      => Operand.Add(renameOperand(left, names), renameOperand(right, names))
      case Operand.Subtract(left, right) =>
        Operand.Subtract(renameOperand(left, names), renameOperand(right, names))
      case Operand.WithPredicate(base, predicate) =>
        Operand.WithPredicate(renameOperand(base, names), renameOperand(predicate, names))
      case Operand.Negate(value) => Operand.Negate(renameOperand(value, names))
      case value                 => value
    }

    def renameInstruction(
        instruction: Instruction,
        names: Map[RegisterName, RegisterName]
    ): Either[Error, Instruction] = {
      val operands = instruction.operands.map(renameOperand(_, names))
      val pred     = instruction.pred.map(value => value.copy(operand = renameOperand(value.operand, names)))
      instruction.decoder(operands, instruction.mods, pred).leftMap(error =>
        Error.Invalid(
          s"failed to preserve scoped instruction ${Printer.render(instruction)}: $error"
        )
      )
    }

    def visit(
        body: Vec[Statement],
        names: Map[RegisterName, RegisterName]
    ): Either[Error, Vec[Statement]] = {
      val result = Vec.newBuilder[Statement]
      var index  = 0
      while (index < body.size) {
        body(index) match {
          case Statement.Block(nested) =>
            val scope = nextScope
            nextScope += 1
            val scopedNames = declaredRegisters(nested).foldLeft(names) { (current, name) =>
              current.updated(name, RegisterName.unsafe(s"${name.raw}$$scope$scope"))
            }
            visit(nested, scopedNames) match {
              case Left(error)  => return Left(error)
              case Right(value) => value.foreach(result += _)
            }
          case Statement.Instruction(instruction) =>
            renameInstruction(instruction, names) match {
              case Left(error)  => return Left(error)
              case Right(value) => result += Statement.Instruction(value)
            }
          case Statement.UnsupportedInstruction(opcode, operands, mods, pred) =>
            val renamedOperands = operands.map(renameOperand(_, names))
            val renamedPred     = pred.map(value => value.copy(operand = renameOperand(value.operand, names)))
            result += Statement.UnsupportedInstruction(opcode, renamedOperands, mods, renamedPred)
          case value => result += value
        }
        index += 1
      }
      Right(result.result())
    }

    visit(statements, Map.empty)
  }

  private def flatten(statements: Vec[Statement]): Vec[Statement] = statements.flatMap {
    case Statement.Block(body) => flatten(body)
    case value                 => Vec(value)
  }

  // The native host CAS path provides sequential consistency, satisfying these
  // weaker PTX atomic orders. Keep unsupported qualifier combinations explicit.
  private def atomicOpcode(opcode: String): String =
    opcode.replaceFirst(
      "^atom\\.(?:(?:relaxed|acquire|release|acq_rel)\\.)?(?:cta|gpu|sys)\\.global\\.",
      "atom.global."
    )

  private def atomicSpace(opcode: String): VMIR.AtomicSpace = {
    val normalized = atomicOpcode(opcode)
    if (normalized.startsWith("atom.global.")) VMIR.AtomicSpace.Global
    else if (normalized.startsWith("atom.shared.")) VMIR.AtomicSpace.Shared
    else VMIR.AtomicSpace.Generic
  }

  private def atomicIntegerOperands(
      destination: Operand,
      address: Operand,
      source: Operand,
      opcode: String
  ): Either[Error, (RegisterName, Value, Value, BitWidth, VMIR.AtomicSpace)] =
    for {
      destinationName <- registerName(destination)
      addressValue    <- addressValue(address)
      sourceValue     <- value(source)
    } yield (
      destinationName,
      addressValue,
      sourceValue,
      if (opcode.endsWith("64")) BitWidth(64) else BitWidth(32),
      atomicSpace(opcode)
    )

  private def lowerInstruction(
      instruction: Instruction,
      labels: Map[LabelName, Pc],
      functionNames: Set[FunctionName]
  ): Either[Error, PredicatedInstruction] = {
    val lowered = instruction match {
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.param.") && isDirectParameterAddress(address) =>
        for {
          destinationNames   <- registerNames(destination)
          parameterAndOffset <- parameterAddress(address)
          width              <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadParameter(
          destinationNames,
          parameterAndOffset._1,
          parameterAndOffset._2,
          width,
          instruction.qualifiedOpcode.contains(".s")
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.param.") =>
        for {
          parameterAndOffset <- parameterAddress(address)
          sourceValue        <- value(source)
          width              <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreParameter(
          parameterAndOffset._1,
          parameterAndOffset._2,
          sourceValue,
          width
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.param.") =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield
          if (Operand.isVectorLiteral(destination))
            VMIR.Instruction.LoadParameterIndirectVector(
              destinationNames,
              addressValue,
              width,
              instruction.qualifiedOpcode.contains(".s")
            )
          else
            VMIR.Instruction.LoadParameterIndirect(
              destinationNames.head,
              addressValue,
              width,
              instruction.qualifiedOpcode.contains(".s")
            )
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.pred" =>
        for {
          destinationName <- predicateRegisterName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.MovePredicate(destinationName, sourceValue)
      case Instruction.Activemask(destination, _, _) =>
        registerName(destination).map(destinationName =>
          VMIR.Instruction.Move(destinationName, Value.ActiveMask, BitWidth(32))
        )
      case Instruction.Mov(destination, source, _, _)
          if Set("mov.u32", "mov.s32", "mov.b32").contains(instruction.qualifiedOpcode) &&
            !Operand.isVectorLiteral(destination) &&
            !Operand.isVectorLiteral(source) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, BitWidth(32))
      case Instruction.Mov(destination, source, modifiers, _)
          if Set("mov.u8", "mov.s8", "mov.b8", "mov.u16", "mov.s16", "mov.b16").contains(instruction.qualifiedOpcode) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, width)
      case Instruction.Mov(destination, source, _, _)
          if (instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64") &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- splitRegisterNames(destination)
          sourceValue      <- value(source)
          _                <- requirePair(destinationNames, "mov.b64 destination")
        } yield VMIR.Instruction.MoveSplit(destinationNames, sourceValue, PartWidth(32))
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.b32" && Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- splitRegisterNames(destination)
          sourceValue      <- value(source)
          _                <- requirePair(destinationNames, "mov.b32 destination")
        } yield VMIR.Instruction.MoveSplit(destinationNames, sourceValue, PartWidth(16))
      case Instruction.Mov(destination, source, _, _)
          if (instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64") &&
            Operand.isVectorLiteral(source) =>
        for {
          destinationName <- registerName(destination)
          sourceValues    <- vectorValues(source)
          _               <- requirePair(sourceValues, "mov.b64 source")
        } yield VMIR.Instruction.MoveCombine(destinationName, sourceValues, PartWidth(32))
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.b32" && Operand.isVectorLiteral(source) =>
        for {
          destinationName <- registerName(destination)
          sourceValues    <- vectorValues(source)
          _               <- requirePair(sourceValues, "mov.b32 source")
        } yield VMIR.Instruction.MoveCombine(destinationName, sourceValues, PartWidth(16))
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- functionValue(source, functionNames, promoteUnknown = false)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, BitWidth(64))
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.f32" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, BitWidth(32))
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.f64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, BitWidth(64))
      case Instruction.Neg(destination, source, modifiers, _) if !instruction.qualifiedOpcode.contains(".f") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.IntegerUnary(IntegerUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Neg(destination, source, modifiers, _) if instruction.qualifiedOpcode.contains(".f") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- floatWidth(modifiers)
        } yield VMIR.Instruction.FloatUnary(FloatUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Abs(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("abs.f") =>
        floatUnary(FloatUnaryOperation.Absolute, destination, source, modifiers)
      case Instruction.Abs(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("abs.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.IntegerUnary(
          IntegerUnaryOperation.Absolute,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.RcpApproxFtzF64(destination, source, modifiers, _) =>
        floatUnary(FloatUnaryOperation.Reciprocal, destination, source, modifiers, Some(FloatWidth(64)))
      case Instruction.Rcp(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("rcp.") =>
        floatUnary(FloatUnaryOperation.Reciprocal, destination, source, modifiers)
      case Instruction.RsqrtApproxFtzF64(destination, source, modifiers, _) =>
        floatUnary(
          FloatUnaryOperation.ReciprocalSquareRoot,
          destination,
          source,
          modifiers,
          Some(FloatWidth(64))
        )
      case Instruction.Rsqrt(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("rsqrt.") =>
        floatUnary(FloatUnaryOperation.ReciprocalSquareRoot, destination, source, modifiers)
      case Instruction.Sqrt(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sqrt.") =>
        floatUnary(FloatUnaryOperation.SquareRoot, destination, source, modifiers)
      case Instruction.Ex2(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ex2.") =>
        floatUnary(FloatUnaryOperation.Exponent2, destination, source, modifiers)
      case Instruction.Lg2(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("lg2.") =>
        floatUnary(FloatUnaryOperation.Log2, destination, source, modifiers)
      case Instruction.Sin(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sin.") =>
        floatUnary(FloatUnaryOperation.Sine, destination, source, modifiers)
      case Instruction.Cos(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("cos.") =>
        floatUnary(FloatUnaryOperation.Cosine, destination, source, modifiers)
      case Instruction.Tanh(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("tanh.") =>
        floatUnary(FloatUnaryOperation.Tanh, destination, source, modifiers)
      case Instruction.Not(destination, source, modifiers, _) =>
        if (instruction.qualifiedOpcode == "not.pred")
          for {
            destinationName <- predicateRegisterName(destination)
            sourceName      <- predicateRegisterName(source)
          } yield VMIR.Instruction.PredicateUnary(destinationName, sourceName)
        else
          for {
            destinationName <- registerName(destination)
            sourceValue     <- value(source)
            width           <- integerWidth(modifiers)
          } yield VMIR.Instruction.IntegerUnary(
            IntegerUnaryOperation.BitwiseNot,
            destinationName,
            sourceValue,
            width
          )
      case Instruction.Popc(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("popc.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.IntegerUnary(
          IntegerUnaryOperation.PopulationCount,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.Clz(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("clz.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.IntegerUnary(
          IntegerUnaryOperation.LeadingZeroCount,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.Bfind(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("bfind.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.BitFind(
          destinationName,
          sourceValue,
          width,
          instruction.qualifiedOpcode.contains(".s"),
          instruction.qualifiedOpcode.contains(".shiftamt.")
        )
      case Instruction.Dp4a(destination, left, right, addend, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("dp4a.") =>
        val types = modifiers.map(_.name.raw).filter(Set("s32", "u32"))
        if (types.size != 2) invalid(s"expected two dp4a operand types: ${instruction.qualifiedOpcode}")
        else
          for {
            destinationName <- registerName(destination)
            leftValue       <- value(left)
            rightValue      <- value(right)
            addendValue     <- value(addend)
          } yield VMIR.Instruction.DotProduct4(
            destinationName,
            leftValue,
            rightValue,
            addendValue,
            types.head == "s32",
            types(1) == "s32"
          )
      case Instruction.Brev(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("brev.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.IntegerUnary(
          IntegerUnaryOperation.BitReverse,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.u32" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned32,
          destinationName,
          leftValue,
          rightValue,
          BitWidth(64)
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.s32" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideSigned32,
          destinationName,
          leftValue,
          rightValue,
          BitWidth(64)
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.u16" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned16,
          destinationName,
          leftValue,
          rightValue,
          BitWidth(32)
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.s16" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideSigned16,
          destinationName,
          leftValue,
          rightValue,
          BitWidth(32)
        )
      case Instruction.Div(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.endsWith(".f16") ||
            instruction.qualifiedOpcode.endsWith(".f32") ||
            instruction.qualifiedOpcode.endsWith(".f64") =>
        floatBinary(FloatOperation.Divide, destination, left, right, modifiers)
      case Instruction.Div(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("div.") =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Divide(instruction.qualifiedOpcode.contains(".s")),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Rem(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("rem.") =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Remainder(instruction.qualifiedOpcode.contains(".s")),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Mul(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("mul.lo.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.MultiplyLow, _, destination, left, right))
      case Instruction.Mul(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("mul.hi.") =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.MultiplyHigh(instruction.qualifiedOpcode.contains(".s")),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Mul(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        packedFloatBinary(
          FloatOperation.Multiply,
          destination,
          left,
          right,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Mul(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("mul.") &&
            (instruction.qualifiedOpcode.endsWith(".f16") ||
              instruction.qualifiedOpcode.endsWith(".f32") ||
              instruction.qualifiedOpcode.endsWith(".f64")) =>
        floatBinary(FloatOperation.Multiply, destination, left, right, modifiers)
      case Instruction.CopySign(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("copysign.") =>
        floatBinary(FloatOperation.CopySign, destination, left, right, modifiers)
      case Instruction.Fma(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield VMIR.Instruction.FusedMultiplyAddPacked(
          destinationName,
          leftValue,
          rightValue,
          addendValue,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Fma(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.endsWith(".bf16") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield VMIR.Instruction.FusedMultiplyAddBf16(destinationName, leftValue, rightValue, addendValue)
      case Instruction.Fma(destination, left, right, addend, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("fma.") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width           <- floatWidth(modifiers)
        } yield VMIR.Instruction.FusedMultiplyAdd(destinationName, leftValue, rightValue, addendValue, width)
      case Instruction.Shl(destination, left, right, _, _) if instruction.qualifiedOpcode == "shl.b32" =>
        integerBinary(IntegerOperation.ShiftLeft, BitWidth(32), destination, left, right)
      case Instruction.Shl(destination, left, right, modifiers, _) if instruction.qualifiedOpcode.startsWith("shl.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.ShiftLeft, _, destination, left, right))
      case Instruction.Shr(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("shr.u") || instruction.qualifiedOpcode.startsWith("shr.b") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.ShiftRightLogical, _, destination, left, right))
      case Instruction.Shr(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("shr.s") =>
        integerWidth(modifiers).flatMap(integerBinary(
          IntegerOperation.ShiftRightArithmetic,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Shf(destination, low, high, amount, _, _)
          if instruction.qualifiedOpcode.startsWith("shf.") && instruction.qualifiedOpcode.endsWith(".b32") =>
        for {
          destinationName <- registerName(destination)
          lowValue        <- value(low)
          highValue       <- value(high)
          amountValue     <- value(amount)
        } yield VMIR.Instruction.FunnelShift(
          destinationName,
          lowValue,
          highValue,
          amountValue,
          instruction.qualifiedOpcode.startsWith("shf.l."),
          instruction.qualifiedOpcode.contains(".clamp.")
        )
      case Instruction.ShflSync(destination, source, delta, clamp, memberMask, modifiers, _)
          if Set("shfl.sync.down.b32", "shfl.sync.down.b64").contains(instruction.qualifiedOpcode) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          deltaValue      <- value(delta)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.ShuffleDown(
          destination = destinationInfo._1,
          validity = destinationInfo._2,
          source = sourceValue,
          delta = deltaValue,
          clamp = clampValue,
          memberMask = memberMaskValue,
          width = width
        )
      case Instruction.ShflSync(destination, source, delta, clamp, memberMask, modifiers, _)
          if Set("shfl.sync.up.b32", "shfl.sync.up.b64").contains(instruction.qualifiedOpcode) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          deltaValue      <- value(delta)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.ShuffleUp(
          destination = destinationInfo._1,
          validity = destinationInfo._2,
          source = sourceValue,
          delta = deltaValue,
          clamp = clampValue,
          memberMask = memberMaskValue,
          width = width
        )
      case Instruction.ShflSync(destination, source, index, clamp, memberMask, modifiers, _)
          if Set("shfl.sync.idx.b32", "shfl.sync.idx.b64").contains(instruction.qualifiedOpcode) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          indexValue      <- value(index)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.ShuffleIndex(
          destination = destinationInfo._1,
          validity = destinationInfo._2,
          source = sourceValue,
          index = indexValue,
          clamp = clampValue,
          memberMask = memberMaskValue,
          width = width
        )
      case Instruction.ShflSync(destination, source, index, clamp, memberMask, modifiers, _)
          if Set("shfl.sync.bfly.b32", "shfl.sync.bfly.b64").contains(instruction.qualifiedOpcode) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          indexValue      <- value(index)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.ShuffleBfly(
          destination = destinationInfo._1,
          validity = destinationInfo._2,
          source = sourceValue,
          index = indexValue,
          clamp = clampValue,
          memberMask = memberMaskValue,
          width = width
        )
      case Instruction.Vote(destination, predicate, _, _) if instruction.qualifiedOpcode == "vote.ballot.b32" =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
        } yield VMIR.Instruction.VoteBallot(destinationName, predicateName, Value.Immediate(0xffffffffL))
      case Instruction.VoteSync(destination, predicate, memberMask, _, _)
          if instruction.qualifiedOpcode == "vote.sync.ballot.b32" =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
          memberMaskValue <- value(memberMask)
        } yield VMIR.Instruction.VoteBallot(destinationName, predicateName, memberMaskValue)
      case Instruction.VoteSync(destination, predicate, memberMask, _, _)
          if instruction.qualifiedOpcode == "vote.sync.all.pred" ||
            instruction.qualifiedOpcode == "vote.sync.any.pred" ||
            instruction.qualifiedOpcode == "vote.sync.uni.pred" =>
        for {
          destinationName <- predicateRegisterName(destination)
          predicateName   <- predicateRegisterName(predicate)
          memberMaskValue <- value(memberMask)
          operation = if (instruction.qualifiedOpcode.endsWith(".all.pred")) VoteOperation.All
          else if (instruction.qualifiedOpcode.endsWith(".any.pred")) VoteOperation.Any
          else VoteOperation.Uniform
        } yield VMIR.Instruction.VotePredicate(destinationName, predicateName, memberMaskValue, operation)
      case Instruction.Bfe(destination, source, start, widthValue, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("bfe.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          startValue      <- value(start)
          widthOperand    <- value(widthValue)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.BitFieldExtract(
          destination = destinationName,
          source = sourceValue,
          start = startValue,
          widthValue = widthOperand,
          width = width,
          signed = instruction.qualifiedOpcode.contains(".s")
        )
      case Instruction.Bfi(destination, insertValue, baseValue, bitPosition, bitLength, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("bfi.") =>
        for {
          destinationName <- registerName(destination)
          insertOperand   <- value(insertValue)
          baseOperand     <- value(baseValue)
          positionOperand <- value(bitPosition)
          lengthOperand   <- value(bitLength)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.BitFieldInsert(
          destination = destinationName,
          insertValue = insertOperand,
          baseValue = baseOperand,
          bitPosition = positionOperand,
          bitLength = lengthOperand,
          width = width
        )
      case Instruction.Bmsk(destination, start, widthValue, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("bmsk.") =>
        for {
          destinationName <- registerName(destination)
          startValue      <- value(start)
          widthOperand    <- value(widthValue)
          mode            <- modifiers.view.collectFirst {
            case Modifier(name) if name.raw == "clamp" => BitMaskMode.Clamp
            case Modifier(name) if name.raw == "wrap"  => BitMaskMode.Wrap
          }
            .toRight(Error.Invalid("bmsk requires .clamp or .wrap"))
          _ <- modifiers.map(_.name.raw).find(_.startsWith("b32")).toRight(
            Error.Invalid("bmsk requires a .b32 width")
          )
        } yield VMIR.Instruction.BitMask(destinationName, startValue, widthOperand, mode)
      case Instruction.Prmt(destination, left, right, control, _, _)
          if instruction.qualifiedOpcode.startsWith("prmt.b32") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          controlValue    <- value(control)
          suffix = instruction.qualifiedOpcode.split('.').drop(2).headOption.getOrElse("")
          mode <- BytePermuteMode.values.find(_.suffix == suffix).toRight(
            Error.Invalid(s"unsupported prmt mode: $suffix")
          )
        } yield VMIR.Instruction.BytePermute(
          destinationName,
          leftValue,
          rightValue,
          controlValue,
          mode
        )
      case Instruction.Mad(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.contains(".lo.") &&
            (instruction.qualifiedOpcode.endsWith(".s32") || instruction.qualifiedOpcode.endsWith(".u32") ||
              instruction.qualifiedOpcode.endsWith(".s64") || instruction.qualifiedOpcode.endsWith(".u64")) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width = if (instruction.qualifiedOpcode.endsWith("32")) BitWidth(32) else BitWidth(64)
        } yield VMIR.Instruction.MultiplyAdd(
          destination = destinationName,
          left = leftValue,
          right = rightValue,
          addend = addendValue,
          operation = IntegerMultiplyAddOperation.Low(
            width,
            width,
            carryIn = false,
            carryOut = instruction.qualifiedOpcode.contains(".cc.")
          )
        )
      case Instruction.MadCc(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.contains(".lo.") &&
            (instruction.qualifiedOpcode.endsWith(".s32") || instruction.qualifiedOpcode.endsWith(".u32") ||
              instruction.qualifiedOpcode.endsWith(".s64") || instruction.qualifiedOpcode.endsWith(".u64")) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width = if (instruction.qualifiedOpcode.endsWith("32")) BitWidth(32) else BitWidth(64)
        } yield VMIR.Instruction.MultiplyAdd(
          destination = destinationName,
          left = leftValue,
          right = rightValue,
          addend = addendValue,
          operation = IntegerMultiplyAddOperation.Low(width, width, carryIn = false, carryOut = true)
        )
      case Instruction.Mad(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.contains(".wide.") && instruction.qualifiedOpcode.endsWith(".u32") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield VMIR.Instruction.MultiplyAdd(
          destination = destinationName,
          left = leftValue,
          right = rightValue,
          addend = addendValue,
          operation =
            IntegerMultiplyAddOperation.Low(BitWidth(32), BitWidth(64), carryIn = false, carryOut = false)
        )
      case Instruction.Madc(destination, left, right, addend, _, _)
          if (instruction.qualifiedOpcode.contains(".lo.") || instruction.qualifiedOpcode.contains(".hi.")) &&
            (instruction.qualifiedOpcode.endsWith(".s32") || instruction.qualifiedOpcode.endsWith(".u32") ||
              instruction.qualifiedOpcode.endsWith(".s64") || instruction.qualifiedOpcode.endsWith(".u64")) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width     = if (instruction.qualifiedOpcode.endsWith("32")) BitWidth(32) else BitWidth(64)
          operation = if (instruction.qualifiedOpcode.contains(".hi."))
            IntegerMultiplyAddOperation.High(
              width,
              width,
              carryIn = true,
              carryOut = instruction.qualifiedOpcode.contains(".cc.")
            )
          else IntegerMultiplyAddOperation.Low(
            width,
            width,
            carryIn = true,
            carryOut = instruction.qualifiedOpcode.contains(".cc.")
          )
        } yield VMIR.Instruction.MultiplyAdd(
          destination = destinationName,
          left = leftValue,
          right = rightValue,
          addend = addendValue,
          operation = operation
        )
      case Instruction.AddCc(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".u32") || instruction.qualifiedOpcode.endsWith(".s32") ||
            instruction.qualifiedOpcode.endsWith(".u64") || instruction.qualifiedOpcode.endsWith(".s64") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (instruction.qualifiedOpcode.endsWith("32")) BitWidth(32) else BitWidth(64)
        } yield VMIR.Instruction.IntegerAddWithCarry(
          destinationName,
          leftValue,
          rightValue,
          width,
          carryIn = false,
          carryOut = true
        )
      case Instruction.Addc(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".u32") || instruction.qualifiedOpcode.endsWith(".s32") ||
            instruction.qualifiedOpcode.endsWith(".u64") || instruction.qualifiedOpcode.endsWith(".s64") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (instruction.qualifiedOpcode.endsWith("32")) BitWidth(32) else BitWidth(64)
        } yield VMIR.Instruction.IntegerAddWithCarry(
          destinationName,
          leftValue,
          rightValue,
          width,
          carryIn = true,
          carryOut = instruction.qualifiedOpcode.contains(".cc.")
        )
      case Instruction.SubCc(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".u32") || instruction.qualifiedOpcode.endsWith(".s32") ||
            instruction.qualifiedOpcode.endsWith(".u64") || instruction.qualifiedOpcode.endsWith(".s64") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (instruction.qualifiedOpcode.endsWith("32")) BitWidth(32) else BitWidth(64)
        } yield VMIR.Instruction.IntegerSubWithBorrow(
          destinationName,
          leftValue,
          rightValue,
          width,
          borrowIn = false,
          borrowOut = true
        )
      case Instruction.Subc(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".u32") || instruction.qualifiedOpcode.endsWith(".s32") ||
            instruction.qualifiedOpcode.endsWith(".u64") || instruction.qualifiedOpcode.endsWith(".s64") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (instruction.qualifiedOpcode.endsWith("32")) BitWidth(32) else BitWidth(64)
        } yield VMIR.Instruction.IntegerSubWithBorrow(
          destinationName,
          leftValue,
          rightValue,
          width,
          borrowIn = true,
          borrowOut = instruction.qualifiedOpcode.contains(".cc.")
        )
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "add.u32" || instruction.qualifiedOpcode == "add.s32" =>
        integerBinary(IntegerOperation.Add, BitWidth(32), destination, left, right)
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "add.u64" || instruction.qualifiedOpcode == "add.s64" =>
        integerBinary(IntegerOperation.Add, BitWidth(64), destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "sub.u32" || instruction.qualifiedOpcode == "sub.s32" =>
        integerBinary(IntegerOperation.Subtract, BitWidth(32), destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "sub.u64" || instruction.qualifiedOpcode == "sub.s64" =>
        integerBinary(IntegerOperation.Subtract, BitWidth(64), destination, left, right)
      case Instruction.And(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "and.pred" =>
        predicateBinary(PredicateOperation.And, destination, left, right)
      case Instruction.Or(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "or.pred" =>
        predicateBinary(PredicateOperation.Or, destination, left, right)
      case Instruction.Xor(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "xor.pred" =>
        predicateBinary(PredicateOperation.Xor, destination, left, right)
      case Instruction.And(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("and.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.BitwiseAnd, _, destination, left, right))
      case Instruction.Or(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("or.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.BitwiseOr, _, destination, left, right))
      case Instruction.Xor(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("xor.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.BitwiseXor, _, destination, left, right))
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        packedFloatBinary(
          FloatOperation.Add,
          destination,
          left,
          right,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Add(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("add.") &&
            (instruction.qualifiedOpcode.endsWith(".f16") ||
              instruction.qualifiedOpcode.endsWith(".f32") ||
              instruction.qualifiedOpcode.endsWith(".f64")) =>
        floatBinary(FloatOperation.Add, destination, left, right, modifiers)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        packedFloatBinary(
          FloatOperation.Subtract,
          destination,
          left,
          right,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Sub(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sub.") &&
            (instruction.qualifiedOpcode.endsWith(".f16") ||
              instruction.qualifiedOpcode.endsWith(".f32") ||
              instruction.qualifiedOpcode.endsWith(".f64")) =>
        floatBinary(FloatOperation.Subtract, destination, left, right, modifiers)
      case Instruction.Add(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("add.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.Add, _, destination, left, right))
      case Instruction.Sub(destination, left, right, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("sub.") =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.Subtract, _, destination, left, right))
      case Instruction.Min(destination, left, right, _, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        packedFloatBinary(
          FloatOperation.Minimum,
          destination,
          left,
          right,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Min(destination, left, right, _, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("min.f") =>
        floatBinary(FloatOperation.Minimum, destination, left, right, modifiers)
      case Instruction.Max(destination, left, right, _, _, _)
          if instruction.qualifiedOpcode.endsWith(".f16x2") || instruction.qualifiedOpcode.endsWith(".bf16x2") =>
        packedFloatBinary(
          FloatOperation.Maximum,
          destination,
          left,
          right,
          instruction.qualifiedOpcode.endsWith(".bf16x2")
        )
      case Instruction.Max(destination, left, right, _, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("max.f") =>
        floatBinary(FloatOperation.Maximum, destination, left, right, modifiers)
      case Instruction.Min(destination, left, right, _, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("min.") =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Minimum(instruction.qualifiedOpcode.contains(".s")),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Max(destination, left, right, _, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("max.") =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Maximum(instruction.qualifiedOpcode.contains(".s")),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Cvta(destination, source, _, _)
          if instruction.qualifiedOpcode == "cvta.global.u64" || instruction.qualifiedOpcode == "cvta.to.global.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, BitWidth(64))
      case Instruction.Cvta(destination, source, _, _) if instruction.qualifiedOpcode == "cvta.local.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, BitWidth(64))
      case Instruction.Cvta(destination, source, _, _) if instruction.qualifiedOpcode == "cvta.to.local.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, BitWidth(64))
      case Instruction.Cvta(destination, source, _, _)
          if instruction.qualifiedOpcode == "cvta.shared.u64" || instruction.qualifiedOpcode == "cvta.to.shared.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, BitWidth(64))
      case Instruction.Cvt(destination, source, None, None, modifiers, _) =>
        val types = modifiers.map(_.name.raw).flatMap(name => ConversionType.values.find(_.mnemonic == name))
        if (types.size < 2) invalid(s"expected conversion source and destination types: ${instruction.qualifiedOpcode}")
        else
          for {
            destinationName <- registerName(destination)
            sourceValue     <- value(source)
          } yield VMIR.Instruction.Convert(
            destinationName,
            sourceValue,
            types.last,
            types(types.size - 2),
            modifiers.view.map(_.name.raw)
              .flatMap(name => ConversionRounding.values.find(_.mnemonic == name))
              .headOption
          )
      case Instruction.Selp(destination, whenTrue, whenFalse, predicate, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("selp.") =>
        for {
          destinationName <- registerName(destination)
          falseValue      <- value(whenFalse)
          trueValue       <- value(whenTrue)
          predicateName   <- predicateRegisterName(predicate)
          width           <- scalarWidth(modifiers)
        } yield VMIR.Instruction.Select(destinationName, trueValue, falseValue, predicateName, width)
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.matches("ld\\.(acquire|relaxed)\\.(cta|gpu|sys)\\.global\\.[ub](32|64)") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.AtomicAdd(
          destinationName,
          addressValue,
          VMIR.Value.Immediate(0),
          width,
          VMIR.AtomicSpace.Global
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.matches("st\\.(release|relaxed)\\.(cta|gpu|sys)\\.global\\.[ub](32|64)") =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.AtomicExchange(
          RegisterName("_"),
          addressValue,
          sourceValue,
          width,
          VMIR.AtomicSpace.Global
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if (instruction.qualifiedOpcode.split('.') match {
            case Array("ld", scalar) => isScalarType(scalar)
            case _                   => false
          }) &&
            !Operand.isVectorLiteral(destination) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGeneric(
          destinationName,
          addressValue,
          width,
          instruction.qualifiedOpcode.contains(".s")
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") &&
            !instruction.qualifiedOpcode.contains(".param.") &&
            !instruction.qualifiedOpcode.contains(".shared.") &&
            !instruction.qualifiedOpcode.contains(".local.") &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGenericVector(
          destinationNames,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".global.") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") &&
            !instruction.qualifiedOpcode.contains(".param.") &&
            !instruction.qualifiedOpcode.contains(".shared.") &&
            !instruction.qualifiedOpcode.contains(".local.") &&
            !instruction.qualifiedOpcode.contains(".global.") &&
            !Operand.isVectorLiteral(destination) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.LdGlobalNc(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith(
            "ld.global.nc."
          ) && Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGlobalVector(
          destinationNames,
          addressValue,
          width,
          instruction.qualifiedOpcode.contains(".s")
        )
      case Instruction.LdGlobalNc(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.global.nc.") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if (instruction.qualifiedOpcode.split('.') match {
            case Array("st", scalar) => isScalarType(scalar)
            case _                   => false
          }) &&
            !Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreGeneric(addressValue, sourceValue, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") &&
            !instruction.qualifiedOpcode.contains(".shared.") &&
            !instruction.qualifiedOpcode.contains(".local.") &&
            Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreGenericVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") &&
            !instruction.qualifiedOpcode.contains(".shared.") &&
            !instruction.qualifiedOpcode.contains(".local.") &&
            !Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreGlobal(addressValue, sourceValue, width)
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".shared.") &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadSharedVector(
          destinationNames,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".shared.") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadShared(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".shared.") &&
            Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreSharedVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".shared.") =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreShared(addressValue, sourceValue, width)
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".local.") &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadLocalVector(
          destinationNames,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".local.") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadLocal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".local.") &&
            Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreLocalVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".local.") =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreLocal(addressValue, sourceValue, width)
      case Instruction.CpAsync(destination, source, copySize, _, _, _, _) =>
        for {
          destinationValue <- addressValue(destination)
          sourceValue      <- addressValue(source)
          size             <- immediate(copySize).flatMap { value =>
            if (Set(4L, 8L, 16L).contains(value)) Right(PositiveInt.unsafe(value.toInt))
            else invalid(s"cp.async copy size must be 4, 8, or 16 bytes, found $value")
          }
        } yield VMIR.Instruction.AsyncCopy(destinationValue, sourceValue, size)
      case Instruction.CpAsyncCommitGroup(_, _)  => Right(VMIR.Instruction.AsyncCommit)
      case Instruction.CpAsyncWaitAll(_, _)      => Right(VMIR.Instruction.AsyncWait)
      case Instruction.CpAsyncWaitGroup(_, _, _) => Right(VMIR.Instruction.AsyncWait)
      case Instruction.ReduxSync(destination, source, memberMask, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("redux.sync.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
          operation       <-
            if (instruction.qualifiedOpcode.contains(".add.")) Right(IntegerOperation.Add)
            else if (instruction.qualifiedOpcode.contains(".min."))
              Right(IntegerOperation.Minimum(modifiers.exists(_.name.raw == s"s${width.raw}")))
            else if (instruction.qualifiedOpcode.contains(".max."))
              Right(IntegerOperation.Maximum(modifiers.exists(_.name.raw == s"s${width.raw}")))
            else invalid(s"unsupported redux.sync operation ${instruction.qualifiedOpcode}")
        } yield VMIR.Instruction.WarpReduce(destinationName, sourceValue, memberMaskValue, operation, width)
      case Instruction.Atom(destination, address, source, None, _, _)
          if atomicOpcode(instruction.qualifiedOpcode).matches(
            "atom\\.(global\\.|shared\\.)?(max\\.u|and\\.b|or\\.b|xor\\.b)(32|64)"
          ) =>
        atomicIntegerOperands(destination, address, source, instruction.qualifiedOpcode).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            val operation =
              if (atomicOpcode(instruction.qualifiedOpcode).contains(".max.")) IntegerOperation.Maximum(false)
              else if (atomicOpcode(instruction.qualifiedOpcode).contains(".and.")) IntegerOperation.BitwiseAnd
              else if (atomicOpcode(instruction.qualifiedOpcode).contains(".or.")) IntegerOperation.BitwiseOr
              else IntegerOperation.BitwiseXor
            VMIR.Instruction.AtomicBinary(destinationName, addressValue, sourceValue, width, space, operation)
        }
      case Instruction.Atom(destination, address, comparator, Some(source), _, _)
          if Set(
            "atom.cas.b32",
            "atom.cas.b64",
            "atom.global.cas.b32",
            "atom.global.cas.b64",
            "atom.shared.cas.b32",
            "atom.shared.cas.b64"
          )(atomicOpcode(instruction.qualifiedOpcode)) =>
        for {
          comparatorValue <- value(comparator)
          operands        <- atomicIntegerOperands(destination, address, source, instruction.qualifiedOpcode)
        } yield {
          val (destinationName, addressValue, sourceValue, width, space) = operands
          VMIR.Instruction.AtomicCompareExchange(
            destinationName,
            addressValue,
            comparatorValue,
            sourceValue,
            width,
            space
          )
        }
      case Instruction.Atom(destination, address, source, None, _, _)
          if Set(
            "atom.exch.b32",
            "atom.exch.b64",
            "atom.global.exch.b32",
            "atom.global.exch.b64",
            "atom.shared.exch.b32",
            "atom.shared.exch.b64"
          )(atomicOpcode(instruction.qualifiedOpcode)) =>
        atomicIntegerOperands(destination, address, source, instruction.qualifiedOpcode).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            VMIR.Instruction.AtomicExchange(destinationName, addressValue, sourceValue, width, space)
        }
      case Instruction.Atom(destination, address, source, None, _, _)
          if Set(
            "atom.add.u32",
            "atom.add.u64",
            "atom.global.add.u32",
            "atom.global.add.u64",
            "atom.shared.add.u32",
            "atom.shared.add.u64"
          )(atomicOpcode(instruction.qualifiedOpcode)) =>
        atomicIntegerOperands(destination, address, source, instruction.qualifiedOpcode).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            VMIR.Instruction.AtomicAdd(destinationName, addressValue, sourceValue, width, space)
        }
      case Instruction.Atom(destination, address, source, None, _, _)
          if Set(
            "atom.min.u32",
            "atom.min.u64",
            "atom.shared.min.u32",
            "atom.shared.min.u64",
            "atom.global.min.u32",
            "atom.global.min.u64"
          )(
            atomicOpcode(instruction.qualifiedOpcode)
          ) =>
        atomicIntegerOperands(destination, address, source, instruction.qualifiedOpcode).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            VMIR.Instruction.AtomicMin(destinationName, addressValue, sourceValue, width, space)
        }
      case Instruction.Atom(destination, address, source, None, _, _)
          if atomicOpcode(instruction.qualifiedOpcode) == "atom.global.add.f32" ||
            atomicOpcode(instruction.qualifiedOpcode) == "atom.global.add.f64" =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width = if (atomicOpcode(instruction.qualifiedOpcode).endsWith(".f64")) FloatWidth(64)
          else FloatWidth(32)
        } yield VMIR.Instruction.AtomicAddFloat(destinationName, addressValue, sourceValue, width)
      case Instruction.Setp(destination, left, right, None, modifiers, _) =>
        lowerSetPredicate(destination, left, right, modifiers)
      case Instruction.Bar(destination, Some(barrier), Some(predicate), None, _, _)
          if instruction.qualifiedOpcode == "bar.red.and.pred" || instruction.qualifiedOpcode == "bar.red.or.pred" =>
        for {
          destinationName <- predicateRegisterName(destination)
          barrierId       <- immediate(barrier)
          predicateName   <- predicateRegisterName(predicate)
          operation = if (instruction.qualifiedOpcode.endsWith(".and.pred")) PredicateOperation.And
          else PredicateOperation.Or
          checkedBarrierId <-
            if (barrierId >= 0L && barrierId <= Int.MaxValue.toLong)
              Right(NonNegativeInt.unsafe(barrierId.toInt))
            else invalid(s"barrier id is outside Int range: $barrierId")
        } yield VMIR.Instruction.BarrierReduce(destinationName, checkedBarrierId, predicateName, operation)
      case Instruction.Bra(target, _, _)
          if instruction.qualifiedOpcode == "bra" || instruction.qualifiedOpcode == "bra.uni" =>
        target match {
          case Operand.Symbol(label) =>
            labels.get(LabelName(label.toNonEmptyString))
              .toRight(Error.Invalid(s"unknown label: $label"))
              .map(VMIR.Instruction.Branch(_))
          case other => invalid(s"expected branch label, found ${Printer.render(other)}")
        }
      case Instruction.Bar(_, _, _, _, _, Some(_)) if instruction.qualifiedOpcode == "bar.sync" =>
        invalid("bar.sync must not be predicated in the supported execution profile")
      case Instruction.Bar(barrier, None, None, None, _, _) if instruction.qualifiedOpcode == "bar.sync" =>
        immediate(barrier).flatMap { value =>
          if (value >= 0L && value <= Int.MaxValue.toLong)
            Right(VMIR.Instruction.BarrierSync(NonNegativeInt.unsafe(value.toInt)))
          else invalid(s"barrier id is outside Int range: $value")
        }
      case Instruction.Membar(_, _) if instruction.qualifiedOpcode.startsWith("membar.") =>
        Right(VMIR.Instruction.MemoryBarrier)
      case Instruction.Fence(None, None, _, _)
          if instruction.qualifiedOpcode.matches(
            "fence(?:\\.(?:sc|acq_rel|acquire|release))?\\.(?:cta|gpu|sys|cluster)"
          ) =>
        // Execute supported thread fences with stronger full-fence semantics.
        Right(VMIR.Instruction.MemoryBarrier)
      case Instruction.Prefetch(_, _, _) =>
        Right(VMIR.Instruction.Prefetch)
      case Instruction.NanoSleep(_, _, _) =>
        // nanosleep is a scheduling hint; it has no observable architectural
        // effect in the deterministic emulator.
        Right(VMIR.Instruction.Prefetch)
      case Instruction.BarWarpSync(memberMask, _, _) =>
        value(memberMask).map(VMIR.Instruction.WarpBarrier(_))
      case Instruction.Exit(_, _)                                        => Right(VMIR.Instruction.Return)
      case Instruction.Ret(_, _)                                         => Right(VMIR.Instruction.Return)
      case Instruction.Trap(_, _)                                        => Right(VMIR.Instruction.Trap)
      case Instruction.Call(returnParameters, function, params, _, _, _) =>
        for {
          functionValue  <- functionValue(function, functionNames, promoteUnknown = true)
          returnNames    <- callParameters(returnParameters)
          parameterNames <- callParameters(params)
        } yield VMIR.Instruction.Call(functionValue, returnNames, parameterNames)
      case _ =>
        Left(Error.Unsupported(Printer.render(instruction), "no handwritten semantic family"))
    }
    for {
      operation <- lowered
      predicate <- lowerPredicate(instruction.pred)
    } yield PredicatedInstruction(operation, predicate)
  }

  private def integerBinary(
      operation: IntegerOperation,
      width: BitWidth,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[Error, VMIR.Instruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
    } yield VMIR.Instruction.IntegerBinary(operation, destinationName, leftValue, rightValue, width)

  private def floatBinary(
      operation: FloatOperation,
      destination: Operand,
      left: Operand,
      right: Operand,
      modifiers: Vec[Modifier]
  ): Either[Error, VMIR.Instruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
      width           <- floatWidth(modifiers)
    } yield VMIR.Instruction.FloatBinary(operation, destinationName, leftValue, rightValue, width)

  private def packedFloatBinary(
      operation: FloatOperation,
      destination: Operand,
      left: Operand,
      right: Operand,
      bf16: Boolean
  ): Either[Error, VMIR.Instruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
    } yield VMIR.Instruction.FloatBinaryPacked(operation, destinationName, leftValue, rightValue, bf16)

  private def floatUnary(
      operation: FloatUnaryOperation,
      destination: Operand,
      source: Operand,
      modifiers: Vec[Modifier],
      fixedWidth: Option[FloatWidth] = None
  ): Either[Error, VMIR.Instruction] =
    for {
      destinationName <- registerName(destination)
      sourceValue     <- value(source)
      width           <- fixedWidth.fold(floatWidth(modifiers))(Right(_))
    } yield VMIR.Instruction.FloatUnary(operation, destinationName, sourceValue, width)

  private def lowerSetPredicate(
      destination: Operand,
      left: Operand,
      right: Operand,
      instructionModifiers: Vec[Modifier]
  ): Either[Error, VMIR.Instruction] = {
    val modifiers   = instructionModifiers.map(_.name.raw)
    val instruction = ("setp" +: modifiers).mkString(".")
    val comparison  = modifiers.collectFirst(Function.unlift {
      case "num" => Some(Comparison.Ordered)
      case "nan" => Some(Comparison.Unordered)
      case "eq"  => Some(Comparison.Equal)
      case "equ" => Some(Comparison.EqualUnordered)
      case "ne"  => Some(Comparison.NotEqual)
      case "neu" => Some(Comparison.NotEqualUnordered)
      case "lt"  => Some(Comparison.LessThan)
      case "ltu" => Some(Comparison.LessThanUnordered)
      case "le"  => Some(Comparison.LessThanOrEqual)
      case "leu" => Some(Comparison.LessThanOrEqualUnordered)
      case "gt"  => Some(Comparison.GreaterThan)
      case "gtu" => Some(Comparison.GreaterThanUnordered)
      case "ge"  => Some(Comparison.GreaterThanOrEqual)
      case "geu" => Some(Comparison.GreaterThanOrEqualUnordered)
      case _     => None
    })
    val kind = modifiers.collectFirst {
      case "u8"   => 'u' -> 8
      case "s8"   => 's' -> 8
      case "u16"  => 'u' -> 16
      case "s16"  => 's' -> 16
      case "u32"  => 'u' -> 32
      case "s32"  => 's' -> 32
      case "u64"  => 'u' -> 64
      case "s64"  => 's' -> 64
      case "b8"   => 'u' -> 8
      case "b16"  => 'u' -> 16
      case "b32"  => 'u' -> 32
      case "b64"  => 'u' -> 64
      case "f16"  => 'f' -> 16
      case "f32"  => 'f' -> 32
      case "f64"  => 'f' -> 64
      case "bf16" => 'b' -> 16
      case "pred" => 'p' -> 1
    }
    for {
      predicate <- comparison.toRight(
        Error.Invalid(s"unsupported setp comparison: $instruction")
      )
      kindAndWidth <- kind.toRight(
        Error.Invalid(s"unsupported setp type: $instruction")
      )
      destinationName <- predicateRegisterName(destination)
      lowered         <-
        if (kindAndWidth._1 == 'p')
          for {
            leftName  <- predicateRegisterName(left)
            rightName <- predicateRegisterName(right)
          } yield VMIR.Instruction.PredicateCompare(destinationName, predicate, leftName, rightName)
        else
          for {
            leftValue  <- value(left)
            rightValue <- value(right)
            width      <- BitWidth.either(kindAndWidth._2)
              .leftMap(message => Error.Invalid(s"invalid setp width: $message"))
          } yield VMIR.Instruction.SetPredicate(
            destinationName,
            predicate,
            kindAndWidth._1,
            leftValue,
            rightValue,
            width
          )
    } yield lowered
  }

  private def lowerPredicate(value: Option[Predication]): Either[Error, Option[ExecutionPredicate]] =
    value match {
      case None                                => Right(None)
      case Some(Predication(negated, operand)) =>
        predicateRegisterName(operand).map(name => Some(ExecutionPredicate(name, negated)))
    }

  private def predicateRegisterName(operand: Operand): Either[Error, RegisterName] =
    registerName(operand, "predicate register")

  private def registerName(operand: Operand, describe: String = "register"): Either[Error, RegisterName] =
    operand match {
      case Operand.Register(name, None) => Right(name)
      case Operand.Symbol(name)         => Right(RegisterName(name.toNonEmptyString))
      case other                        => invalid(s"expected $describe, found ${Printer.render(other)}")
    }

  private def valueRegisterName(operand: Operand): Either[Error, RegisterName] = operand match {
    case Operand.Register(name, None)                         => Right(name)
    case Operand.Symbol(name) if isBareRegisterName(name.raw) => Right(RegisterName(name.toNonEmptyString))
    case other => invalid(s"expected register, found ${Printer.render(other)}")
  }

  private def valueDestination(
      operand: Operand
  ): Either[Error, (RegisterName, Option[RegisterName])] = operand match {
    case Operand.WithPredicate(base, predicate) =>
      for {
        destinationName <- valueRegisterName(base)
        predicateName   <- predicateRegisterName(predicate)
      } yield destinationName -> Some(predicateName)
    case other => valueRegisterName(other).map(_ -> None)
  }

  private def registerNames(operand: Operand): Either[Error, Vec[RegisterName]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[Error, Vec[RegisterName]]](Right(Vec.empty)) { (current, item) =>
        for {
          names <- current
          name  <- registerName(item)
        } yield names :+ name
      }
    case other => registerName(other).map(Vec(_))
  }

  private def vectorValues(operand: Operand): Either[Error, Vec[Value]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[Error, Vec[Value]]](Right(Vec.empty)) { (current, item) =>
        for {
          values <- current
          value  <- value(item)
        } yield values :+ value
      }
    case other => value(other).map(Vec(_))
  }

  private def splitRegisterNames(operand: Operand): Either[Error, Vec[RegisterName]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[Error, Vec[RegisterName]]](Right(Vec.empty)) { (current, item) =>
        for {
          names <- current
          name  <- item match {
            case Operand.Symbol(name) if name.raw == "_" => Right(RegisterName("_"))
            case other                                   => registerName(other)
          }
        } yield names :+ name
      }
    case other => registerName(other).map(Vec(_))
  }

  private def requirePair[A](names: Vec[A], detail: String): Either[Error, Unit] =
    if (names.size == 2) Right(()) else invalid(s"$detail must contain two registers")

  private def predicateBinary(
      operation: PredicateOperation,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[Error, VMIR.Instruction] =
    for {
      destinationName <- predicateRegisterName(destination)
      leftName        <- predicateRegisterName(left)
      rightName       <- predicateRegisterName(right)
    } yield VMIR.Instruction.PredicateBinary(operation, destinationName, leftName, rightName)

  private def callParameters(operand: Option[Operand]): Either[Error, Vec[ParameterName]] = operand match {
    case None        => Right(Vec.empty)
    case Some(value) => callParameters(value)
  }

  private def callParameters(operand: Operand): Either[Error, Vec[ParameterName]] = operand match {
    case Operand.Symbol(name)         => Right(Vec(ParameterName(name.toNonEmptyString)))
    case Operand.Parenthesized(items) =>
      items.foldLeft[Either[Error, Vec[ParameterName]]](Right(Vec.empty)) { (current, item) =>
        for {
          names <- current
          value <- callParameters(item)
        } yield names ++ value
      }
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[Error, Vec[ParameterName]]](Right(Vec.empty)) { (current, item) =>
        for {
          names <- current
          value <- callParameters(item)
        } yield names ++ value
      }
    case other => invalid(s"expected call parameter names, found ${Printer.render(other)}")
  }

  private def functionValue(
      operand: Operand,
      functionNames: Set[FunctionName],
      promoteUnknown: Boolean
  ): Either[Error, Value] = operand match {
    case Operand.Symbol(name)
        if promoteUnknown || functionNames.contains(FunctionName(name.toNonEmptyString)) =>
      Right(Value.Function(FunctionName(name.toNonEmptyString)))
    case Operand.Symbol(name) => Right(Value.Symbol(name))
    case other                => value(other)
  }

  private def sharedSymbols(body: Vec[Statement]): Map[SymbolName, Long] = {
    val declarations = flatten(body).collect {
      case Statement.Data(DataAddressSpace.Shared, _, _, name, _, _) => Iterator.single(name)
      case Statement.Directive(path, arguments, _)
          if path.size == 1 && path.head.raw == "shared" =>
        arguments.view.flatMap(argument =>
          argument.operand match {
            case Operand.Symbol(name)                   => Iterator.single(name)
            case Operand.Index(Operand.Symbol(name), _) => Iterator.single(name)
            case _                                      => Iterator.empty
          }
        )
    }.flatten.distinct
    declarations.zipWithIndex.map { case (name, index) =>
      name -> (PTXAddressSpace.SharedBase + index.toLong * PTXAddressSpace.KernelSharedStride)
    }.toMap
  }

  private def addressValue(operand: Operand): Either[Error, Value] = operand match {
    case Operand.Address(Vec(single)) => valueWithOffset(single)
    case other                        => invalid(s"expected address operand, found ${Printer.render(other)}")
  }

  private def parameterAddress(
      operand: Operand
  ): Either[Error, (ParameterName, NonNegativeInt)] = operand match {
    case Operand.Address(Vec(Operand.Symbol(name))) =>
      Right(ParameterName(name.toNonEmptyString) -> NonNegativeInt.Zero)
    case Operand.Address(Vec(Operand.Add(Operand.Symbol(name), offset))) =>
      immediate(offset).flatMap(value =>
        if (value >= 0L && value <= Int.MaxValue.toLong)
          Right(ParameterName(name.toNonEmptyString) -> NonNegativeInt.unsafe(value.toInt))
        else invalid(s"parameter offset is outside Int range: $value")
      )
    case other => invalid(s"expected parameter address, found ${Printer.render(other)}")
  }

  private def isDirectParameterAddress(operand: Operand): Boolean = operand match {
    case Operand.Address(Vec(Operand.Symbol(_)))                 => true
    case Operand.Address(Vec(Operand.Add(Operand.Symbol(_), _))) => true
    case _                                                       => false
  }

  private def valueWithOffset(operand: Operand): Either[Error, Value] = operand match {
    case Operand.Add(base, offset) =>
      for {
        baseValue   <- value(base)
        offsetValue <- immediate(offset)
      } yield Value.Address(baseValue, offsetValue)
    case Operand.Subtract(base, offset) =>
      for {
        baseValue   <- value(base)
        offsetValue <- immediate(offset)
      } yield Value.Address(baseValue, -offsetValue)
    case Operand.Symbol(name) => Right(Value.Symbol(name))
    case other                => value(other)
  }

  private def integerWidth(modifiers: Vec[Modifier]): Either[Error, BitWidth] =
    scalarBitWidth(modifiers, "integer type")

  private def floatWidth(modifiers: Vec[Modifier]): Either[Error, FloatWidth] =
    modifiers.reverseIterator.collectFirst {
      case Modifier(name) if Set("f16", "f32", "f64").contains(name.raw) => name.raw.drop(1).toInt
    }.toRight(Error.Invalid(
      s"expected an f16, f32, or f64 type in ${modifiers.map(_.name.raw).mkString(".")}"
    ))
      .flatMap(width =>
        FloatWidth.either(width).leftMap(message => Error.Invalid(message))
      )

  private def scalarWidth(modifiers: Vec[Modifier]): Either[Error, BitWidth] =
    scalarBitWidth(modifiers, "scalar type")

  private def scalarBitWidth(modifiers: Vec[Modifier], describe: String): Either[Error, BitWidth] =
    modifiers.reverseIterator.collectFirst {
      case Modifier(name) if isScalarType(name.raw) => scalarTypeWidth(name.raw)
    }.filter(Set(8, 16, 32, 64).contains)
      .toRight(Error.Invalid(
        s"expected an 8, 16, 32, or 64-bit $describe in ${modifiers.map(_.name.raw).mkString(".")}"
      ))
      .flatMap(width =>
        BitWidth.either(width).leftMap(message => Error.Invalid(message))
      )

  private def isScalarType(name: String): Boolean =
    name == "bf16" || (name.length > 1 && "usbf".contains(name.head) && name.tail.forall(_.isDigit))

  private def scalarTypeWidth(name: String): Int = if (name == "bf16") 16 else name.drop(1).toInt

  private def value(operand: Operand): Either[Error, Value] = operand match {
    case Operand.Register(name, None) =>
      name.raw match {
        case "%tid.x"                                  => Right(Value.ThreadId(Axis.X))
        case "%tid.y"                                  => Right(Value.ThreadId(Axis.Y))
        case "%tid.z"                                  => Right(Value.ThreadId(Axis.Z))
        case "%ctaid.x"                                => Right(Value.BlockId(Axis.X))
        case "%ctaid.y"                                => Right(Value.BlockId(Axis.Y))
        case "%ctaid.z"                                => Right(Value.BlockId(Axis.Z))
        case "%ntid.x"                                 => Right(Value.BlockDim(Axis.X))
        case "%ntid.y"                                 => Right(Value.BlockDim(Axis.Y))
        case "%ntid.z"                                 => Right(Value.BlockDim(Axis.Z))
        case "%nctaid.x"                               => Right(Value.GridDim(Axis.X))
        case "%nctaid.y"                               => Right(Value.GridDim(Axis.Y))
        case "%nctaid.z"                               => Right(Value.GridDim(Axis.Z))
        case "%laneid"                                 => Right(Value.LaneId)
        case "%lanemask_ge"                            => Right(Value.LaneMaskGreaterEqual)
        case "%lanemask_gt"                            => Right(Value.LaneMaskGreater)
        case "%lanemask_le"                            => Right(Value.LaneMaskLessEqual)
        case "%lanemask_lt"                            => Right(Value.LaneMaskLess)
        case "%lanemask_eq"                            => Right(Value.LaneMaskEqual)
        case raw if VMPredicateFile.indexRaw(raw) >= 0 => Right(Value.PredicateSlot(VMPredicateFile.indexRaw(raw)))
        case raw if VMRegisterFile.indexRaw(raw) >= 0  => Right(Value.RegisterSlot(VMRegisterFile.indexRaw(raw)))
        case _                                         => Right(Value.Register(name))
      }
    case Operand.Symbol(name) if VMPredicateFile.indexRaw(name.raw) >= 0 =>
      Right(Value.PredicateSlot(VMPredicateFile.indexRaw(name.raw)))
    case Operand.Symbol(name)
        if isBareRegisterName(name.raw) && VMRegisterFile.indexRaw(s"%${name.raw}") >= 0 =>
      Right(Value.RegisterSlot(VMRegisterFile.indexRaw(s"%${name.raw}")))
    case Operand.Symbol(name) if isBareRegisterName(name.raw) =>
      Right(Value.Register(RegisterName(name.toNonEmptyString)))
    case Operand.Symbol(name)     => Right(Value.Symbol(name))
    case other: Operand.Immediate => immediate(other).map(Value.Immediate(_))
    case Operand.Negate(value)    => immediate(value).map(number => Value.Immediate(-number))
    case other                    => invalid(s"expected register or immediate, found ${Printer.render(other)}")
  }

  private def immediate(operand: Operand): Either[Error, Long] = operand match {
    case Operand.Negate(value)   => immediate(value).map(number => -number)
    case Operand.Immediate(text) =>
      val raw            = text.raw
      val signless       = raw.dropWhile(character => character == '+' || character == '-')
      val sign           = if (raw.startsWith("-")) -1 else 1
      val suffixStripped = signless.reverse.dropWhile(character => "uUlL".contains(character)).reverse
      val parsed         =
        try
          Some(
            if (suffixStripped.startsWith("0x") || suffixStripped.startsWith("0X"))
              BigInt(suffixStripped.drop(2), 16) * sign
            else if (suffixStripped.startsWith("0b") || suffixStripped.startsWith("0B"))
              BigInt(suffixStripped.drop(2), 2) * sign
            else if (suffixStripped.startsWith("0f") || suffixStripped.startsWith("0F"))
              BigInt(suffixStripped.drop(2), 16) * sign
            else if (suffixStripped.startsWith("0d") || suffixStripped.startsWith("0D"))
              BigInt(suffixStripped.drop(2), 16) * sign
            else BigInt(suffixStripped) * sign
          )
        catch {
          case _: NumberFormatException => None
        }
      parsed match {
        case Some(number) if number.isValidLong                        => Right(number.longValue)
        case Some(number) if number >= 0 && number < (BigInt(1) << 64) => Right(number.longValue)
        case Some(_) => invalid(s"integer immediate is outside 64-bit range: $raw")
        case None    => invalid(s"expected integer immediate, found $raw")
      }
    case other => invalid(s"expected integer immediate, found ${Printer.render(other)}")
  }

  private def isBareRegisterName(name: String): Boolean =
    name == "lo" || name == "hi" || Array("r", "rd", "p").exists(prefix =>
      name.startsWith(prefix) && name.drop(prefix.length).forall(_.isDigit)
    )

  private def isSignedScalarOpcode(opcode: String): Boolean =
    opcode.endsWith(".s8") || opcode.endsWith(".s16") || opcode.endsWith(".s32") || opcode.endsWith(".s64")

  private def invalid[A](detail: String): Either[Error, A] =
    NonEmptyString.either(detail) match {
      case Left(_)        => Left(Error.Invalid("invalid PTX input"))
      case Right(refined) => Left(Error.Invalid(refined.raw))
    }
}
