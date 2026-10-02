package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import VMIR.{Instruction as _, *}
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

private object VMCompiler {

  enum SourceError {
    case Syntax(error: ParseError)
    case MissingEntry
    case Compilation(error: Error)

    def message: NonEmpty[String] = this match {
      case Syntax(error)      => error.message
      case MissingEntry       => "missing .entry declaration"
      case Compilation(error) => error.message
    }
  }

  def compile(source: String): Either[SourceError, Kernel] =
    Parser.parse(source).leftMap(SourceError.Syntax(_)).flatMap { program =>
      program.statements.collectFirst { case entry: Statement.Entry if entry.body.nonEmpty => entry }
        .toRight(SourceError.MissingEntry)
        .flatMap(entry =>
          compile(entry, program.statements.collect { case function: Statement.Function => function })
            .leftMap(SourceError.Compilation(_))
        )
    }

  enum Error {
    case Invalid(detail: NonEmpty[String])
    case Unsupported(instruction: NonEmpty[String], detail: String)

    def message: NonEmpty[String] = this match {
      case Invalid(detail)                  => detail
      case Unsupported(instruction, detail) =>
        s"unsupported instruction: $instruction ($detail)"
    }
  }

  def compile(entry: Statement.Entry): Either[Error, Kernel] = compile(entry, Vec.empty, Map.empty)

  def parameterLayout(
      entry: Statement.Entry
  ): Either[Error, Vec[(KernelParameter, Int >= 0)]] =
    for {
      parameters <- lowerParameters(entry.parameters)
      offsets    <- parameterOffsets(entry.parameters, parameters)
    } yield parameters.zip(offsets)

  def compile(entry: Statement.Entry, functions: Vec[Statement.Function]): Either[Error, Kernel] =
    compile(entry, functions, Map.empty)

  def compile(
      entry: Statement.Entry,
      functions: Vec[Statement.Function],
      globalSymbols: Map[NonEmpty[String], GuestAddress]
  ): Either[Error, Kernel] = compile(entry, functions, globalSymbols, Map.empty)

  def compile(
      entry: Statement.Entry,
      functions: Vec[Statement.Function],
      globalSymbols: Map[NonEmpty[String], GuestAddress],
      additionalSharedSymbols: Map[NonEmpty[String], Long]
  ): Either[Error, Kernel] =
    compile(entry, functions, globalSymbols, additionalSharedSymbols, Vec.empty)

  def compile(
      entry: Statement.Entry,
      functions: Vec[Statement.Function],
      globalSymbols: Map[NonEmpty[String], GuestAddress],
      additionalSharedSymbols: Map[NonEmpty[String], Long],
      additionalEntries: Vec[Statement.Entry]
  ): Either[Error, Kernel] = {
    val Statement.Entry(_, name, parameters, definition, _) = entry
    val functionNames                                       = functions.view.map(_.name).toSet ++
      additionalEntries.view
        .map(entry => entry.name)
        .toSet
    for {
      body              <- definition.toRight(Error.Invalid(s"entry $name has no body"))
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
      functions: Map[NonEmpty[String], DeviceFunction]
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
      functionNames: Set[NonEmpty[String]]
  ): Either[Error, Map[NonEmpty[String], DeviceFunction]] = {
    val functionDefinitions = functions.view.map(function => function.name -> function).toMap
    val entryDefinitions    = entries.view.map(entry => entry.name -> entry).toMap
    val pending             = mutable.Queue.empty[NonEmpty[String]]
    val lowered             = mutable.Map.empty[NonEmpty[String], DeviceFunction]

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
      functionNames: Set[NonEmpty[String]]
  ): Either[Error, ExecutableProgram[PredicatedInstruction]] = {
    val flat = flattenScoped(body) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val labels           = mutable.Map.empty[NonEmpty[String], Pc]
    var instructionIndex = 0
    var statementIndex   = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Label(label) =>
          if (labels.contains(label))
            return Left(Error.Invalid(s"duplicate label: $label"))
          labels(label) = instructionIndex.unsafe[Pc]
        case Statement.Instruction(_)                                       => instructionIndex += 1
        case Statement.UnsupportedInstruction(opcode, operands, mods, pred) =>
          return Left(Error.Unsupported(
            opcode,
            s"instruction is not implemented (modifiers: $mods, operands: $operands, predicate: $pred)"
          ))
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
                  Error.Invalid(s"${instruction.qualifiedOpcode}: $detail")
                case Error.Unsupported(_, detail) =>
                  Error.Unsupported(instruction.opcode, detail)
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
      functionNames: Set[NonEmpty[String]]
  ): Either[Error, DeviceFunction] = {
    val Statement.Function(_, name, returnParameters, parameters, body, _) = function
    body match {
      case None             => Left(Error.Invalid(s"function $name has no body"))
      case Some(statements) =>
        for {
          _                 <- lowerParameters(returnParameters)
          loweredParameters <- lowerParameters(parameters)
          offsets           <- parameterOffsets(parameters, loweredParameters)
          program           <- lowerProgram(statements, functionNames)
        } yield DeviceFunction(
          name,
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
      functionNames: Set[NonEmpty[String]]
  ): Either[Error, DeviceFunction] = {
    val Statement.Entry(_, name, parameters, definition, _) = entry
    for {
      body              <- definition.toRight(Error.Invalid(s"entry $name has no body"))
      loweredParameters <- lowerParameters(parameters)
      offsets           <- parameterOffsets(parameters, loweredParameters)
      program           <- lowerProgram(body, functionNames)
    } yield DeviceFunction(
      name,
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
  ): Either[Error, Vec[Int >= 0]] = {
    val result = Vec.newBuilder[Int >= 0]
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
      result += offset.toInt.unsafe[Int >= 0]
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
      val width     = (parameter.elementType.widthBits.raw / 8).unsafe[Int > 0]
      val elements  = parameter.dimensions.foldLeft[Either[Error, Long > 0]](Right(1L)) {
        (current, value) =>
          for {
            count   <- current
            element <- value.toRight(
              Error.Invalid(s"kernel parameter ${parameter.name} has an unsized dimension")
            ).flatMap(immediate(_))
            product <-
              if (element > 0 && count.raw <= Int.MaxValue.toLong / element)
                Right((count.raw * element).unsafe[Long > 0])
              else invalid(s"kernel parameter ${parameter.name} has an invalid array dimension: $element")
          } yield product
      }
      elements match {
        case Left(error)  => return Left(error)
        case Right(count) =>
          val totalWidth = width.raw.toLong * count.raw
          if (totalWidth > Int.MaxValue.toLong)
            return invalid(
              s"kernel parameter ${parameter.name} is too wide: $totalWidth bytes"
            )
          val pointer = parameter.qualifiers.exists {
            case DeclarationQualifier.Attribute(name) => name.raw == "ptr"
            case _                                    => false
          }
          result += KernelParameter(parameter.name, totalWidth.toInt.unsafe[Int > 0], pointer)
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

    def registerNames(operand: Operand): scala.collection.View[NonEmpty[String]] = operand match {
      case Operand.Register(name, _)    => Vector(name).view
      case Operand.Symbol(name)         => Vector(name).view
      case Operand.VectorLiteral(items) => items.view.flatMap(registerNames(_))
      case Operand.Parenthesized(items) => items.view.flatMap(registerNames(_))
      case Operand.Qualified(base, _)   => registerNames(base)
      case _                            => Vector.empty.view
    }

    def declaredRegisters(body: Vec[Statement]): Vec[NonEmpty[String]] =
      Vec.toVec(body.view.flatMap {
        case Statement.Directive(path, arguments, _, _)
            if path.head.raw == "reg" =>
          arguments.view.flatMap(argument => registerNames(argument.operand))
        case _ => Vector.empty.view
      }).distinct

    def renameOperand(operand: Operand, names: Map[NonEmpty[String], NonEmpty[String]]): Operand = operand match {
      case Operand.Register(name, part)                 => Operand.Register(names.getOrElse(name, name), part)
      case Operand.Symbol(name) if names.contains(name) =>
        Operand.Register(names(name), None)
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
        names: Map[NonEmpty[String], NonEmpty[String]]
    ): Either[Error, Instruction] = {
      val operands = instruction.operands.map(renameOperand(_, names))
      val pred     = instruction.pred.map(value => value.copy(operand = renameOperand(value.operand, names)))
      instruction.decoder(operands, instruction.mods, pred).leftMap(error =>
        Error.Invalid(
          s"failed to preserve scoped instruction ${instruction.qualifiedOpcode}: $error"
        )
      )
    }

    def visit(
        body: Vec[Statement],
        names: Map[NonEmpty[String], NonEmpty[String]]
    ): Either[Error, Vec[Statement]] = {
      val result = Vec.newBuilder[Statement]
      var index  = 0
      while (index < body.size) {
        body(index) match {
          case Statement.Block(nested) =>
            val scope = nextScope
            nextScope += 1
            val scopedNames = declaredRegisters(nested).foldLeft(names) { (current, name) =>
              current.updated(name, s"$name$$scope$scope")
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
  private val AtomicOrders = Set(Modifier.Relaxed, Modifier.Acquire, Modifier.Release, Modifier.AcqRel)
  private val AtomicScopes = Set(Modifier.Cta, Modifier.Gpu, Modifier.Sys)

  private def atomicSignature(signature: InstructionSignature): InstructionSignature = {
    val modifiers = signature.modifiers match {
      case scope +: Modifier.Global +: rest if AtomicScopes(scope) => Modifier.Global +: rest
      case order +: scope +: Modifier.Global +: rest if AtomicOrders(order) && AtomicScopes(scope) =>
        Modifier.Global +: rest
      case other => other
    }
    signature.copy(modifiers = modifiers)
  }

  private def atomicSpace(signature: InstructionSignature): VMIR.AtomicSpace =
    atomicSignature(signature).modifiers.headOption match {
      case Some(Modifier.Global) => VMIR.AtomicSpace.Global
      case Some(Modifier.Shared) => VMIR.AtomicSpace.Shared
      case _                     => VMIR.AtomicSpace.Generic
    }

  private def atomicBinaryForm(signature: InstructionSignature): Boolean = {
    val modifiers = signature.modifiers match {
      case (Modifier.Global | Modifier.Shared) +: rest => rest
      case other                                       => other
    }
    modifiers match {
      case Vec(Modifier.Max, Modifier.U32 | Modifier.U64)                              => true
      case Vec(Modifier.And | Modifier.Or | Modifier.Xor, Modifier.B32 | Modifier.B64) => true
      case _                                                                           => false
    }
  }

  private def orderedMemoryForm(signature: InstructionSignature, load: Boolean): Boolean = signature.modifiers match {
    case Vec(order, scope, Modifier.Global, Modifier.U32 | Modifier.U64 | Modifier.B32 | Modifier.B64) =>
      AtomicScopes(scope) && (order == Modifier.Relaxed || order == (if (load) Modifier.Acquire else Modifier.Release))
    case _ => false
  }

  private def fenceForm(signature: InstructionSignature): Boolean = {
    val scopes = Set(Modifier.Cta, Modifier.Gpu, Modifier.Sys, Modifier.Cluster)
    signature.modifiers match {
      case Vec(scope)        => scopes(scope)
      case Vec(order, scope) => Set(Modifier.Sc, Modifier.AcqRel, Modifier.Acquire, Modifier.Release)(order) &&
        scopes(scope)
      case _ => false
    }
  }

  private def atomicIntegerOperands(
      destination: Operand,
      address: Operand,
      source: Operand,
      signature: InstructionSignature
  ): Either[Error, (NonEmpty[String], Value, Value, Int where (Between[8, 64] & MultipleOf[8]), VMIR.AtomicSpace)] =
    for {
      destinationName <- registerName(destination)
      addressValue    <- addressValue(address)
      sourceValue     <- value(source)
      width: (Int where (Between[8, 64] & MultipleOf[8])) =
        if (signature.modifiers.lastOption.exists(_.bitWidth == 64)) 64 else 32
    } yield (
      destinationName,
      addressValue,
      sourceValue,
      width,
      atomicSpace(signature)
    )

  private def lowerInstruction(
      instruction: Instruction,
      labels: Map[NonEmpty[String], Pc],
      functionNames: Set[NonEmpty[String]]
  ): Either[Error, PredicatedInstruction] = {
    val signature  = instruction.signature
    val qualifiers = signature.modifiers
    val lowered    = instruction match {
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if (signature.opcode == Opcode.Ld && qualifiers.startsWith(Vec(Modifier.Param)) && qualifiers.size > 1) &&
            isDirectParameterAddress(address) =>
        for {
          destinationNames   <- registerNames(destination)
          parameterAndOffset <- parameterAddress(address)
          width              <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadParameter(
          destinationNames,
          parameterAndOffset._1,
          parameterAndOffset._2,
          width,
          qualifiers.exists(_.isSigned)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if signature.opcode == Opcode.St && qualifiers.startsWith(Vec(Modifier.Param)) && qualifiers.size > 1 =>
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
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if signature.opcode == Opcode.Ld && qualifiers.startsWith(Vec(Modifier.Param)) && qualifiers.size > 1 =>
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
              qualifiers.exists(_.isSigned)
            )
          else
            VMIR.Instruction.LoadParameterIndirect(
              destinationNames.head,
              addressValue,
              width,
              qualifiers.exists(_.isSigned)
            )
      case Instruction.Mov(destination, source, _, _)
          if signature.matches(Opcode.Mov, Modifier.Pred) =>
        for {
          destinationName <- predicateRegisterName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.MovePredicate(destinationName, sourceValue)
      case Instruction.Activemask(destination, _, _) =>
        registerName(destination).map(destinationName =>
          VMIR.Instruction.Move(destinationName, Value.ActiveMask, 32)
        )
      case Instruction.Mov(destination, source, _, _)
          if Set(
            InstructionSignature(Opcode.Mov, Vec(Modifier.U32)),
            InstructionSignature(Opcode.Mov, Vec(Modifier.S32)),
            InstructionSignature(Opcode.Mov, Vec(Modifier.B32))
          ).contains(signature) &&
            !Operand.isVectorLiteral(destination) &&
            !Operand.isVectorLiteral(source) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 32)
      case Instruction.Mov(destination, source, modifiers, _)
          if Set(
            InstructionSignature(Opcode.Mov, Vec(Modifier.U8)),
            InstructionSignature(Opcode.Mov, Vec(Modifier.S8)),
            InstructionSignature(Opcode.Mov, Vec(Modifier.B8)),
            InstructionSignature(Opcode.Mov, Vec(Modifier.U16)),
            InstructionSignature(Opcode.Mov, Vec(Modifier.S16)),
            InstructionSignature(Opcode.Mov, Vec(Modifier.B16))
          ).contains(signature) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, width)
      case Instruction.Mov(destination, source, _, _)
          if (signature.matches(Opcode.Mov, Modifier.U64) || signature.matches(Opcode.Mov, Modifier.B64)) &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- splitRegisterNames(destination)
          sourceValue      <- value(source)
          _                <- requirePair(destinationNames, "mov.b64 destination")
        } yield VMIR.Instruction.MoveSplit(destinationNames, sourceValue, 32)
      case Instruction.Mov(destination, source, _, _)
          if signature.matches(Opcode.Mov, Modifier.B32) && Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- splitRegisterNames(destination)
          sourceValue      <- value(source)
          _                <- requirePair(destinationNames, "mov.b32 destination")
        } yield VMIR.Instruction.MoveSplit(destinationNames, sourceValue, 16)
      case Instruction.Mov(destination, source, _, _)
          if (signature.matches(Opcode.Mov, Modifier.U64) || signature.matches(Opcode.Mov, Modifier.B64)) &&
            Operand.isVectorLiteral(source) =>
        for {
          destinationName <- registerName(destination)
          sourceValues    <- vectorValues(source)
          _               <- requirePair(sourceValues, "mov.b64 source")
        } yield VMIR.Instruction.MoveCombine(destinationName, sourceValues, 32)
      case Instruction.Mov(destination, source, _, _)
          if signature.matches(Opcode.Mov, Modifier.B32) && Operand.isVectorLiteral(source) =>
        for {
          destinationName <- registerName(destination)
          sourceValues    <- vectorValues(source)
          _               <- requirePair(sourceValues, "mov.b32 source")
        } yield VMIR.Instruction.MoveCombine(destinationName, sourceValues, 16)
      case Instruction.Mov(destination, source, _, _)
          if signature.matches(Opcode.Mov, Modifier.U64) || signature.matches(Opcode.Mov, Modifier.B64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- functionValue(source, functionNames, promoteUnknown = false)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64)
      case Instruction.Mov(destination, source, _, _)
          if signature.matches(Opcode.Mov, Modifier.F32) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 32)
      case Instruction.Mov(destination, source, _, _)
          if signature.matches(Opcode.Mov, Modifier.F64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64)
      case Instruction.Neg(destination, source, modifiers, _) if !qualifiers.exists(_.isFloating) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.IntegerUnary(IntegerUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Neg(destination, source, modifiers, _) if qualifiers.exists(_.isFloating) =>
        floatUnary(FloatUnaryOperation.Negate, destination, source, modifiers)
      case Instruction.Abs(destination, source, modifiers, _)
          if qualifiers.exists(_.isFloating) =>
        floatUnary(FloatUnaryOperation.Absolute, destination, source, modifiers)
      case Instruction.Abs(destination, source, modifiers, _)
          if signature.opcode == Opcode.Abs && qualifiers.nonEmpty =>
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
      case Instruction.RcpApproxFtzF64(destination, source, _, _) =>
        floatUnary(FloatUnaryOperation.Reciprocal, destination, source, qualifiers, Some(64))
      case Instruction.Rcp(destination, source, modifiers, _)
          if signature.opcode == Opcode.Rcp && qualifiers.nonEmpty =>
        floatUnary(FloatUnaryOperation.Reciprocal, destination, source, modifiers)
      case Instruction.RsqrtApproxFtzF64(destination, source, _, _) =>
        floatUnary(
          FloatUnaryOperation.ReciprocalSquareRoot,
          destination,
          source,
          qualifiers,
          Some(64)
        )
      case Instruction.Rsqrt(destination, source, modifiers, _)
          if signature.opcode == Opcode.Rsqrt && qualifiers.nonEmpty =>
        floatUnary(FloatUnaryOperation.ReciprocalSquareRoot, destination, source, modifiers)
      case Instruction.Sqrt(destination, source, modifiers, _)
          if signature.opcode == Opcode.Sqrt && qualifiers.nonEmpty =>
        floatUnary(FloatUnaryOperation.SquareRoot, destination, source, modifiers)
      case Instruction.Ex2(destination, source, modifiers, _)
          if signature.opcode == Opcode.Ex2 && qualifiers.nonEmpty =>
        floatUnary(FloatUnaryOperation.Exponent2, destination, source, modifiers)
      case Instruction.Lg2(destination, source, modifiers, _)
          if signature.opcode == Opcode.Lg2 && qualifiers.nonEmpty =>
        floatUnary(FloatUnaryOperation.Log2, destination, source, modifiers)
      case Instruction.Sin(destination, source, modifiers, _)
          if signature.opcode == Opcode.Sin && qualifiers.nonEmpty =>
        floatUnary(FloatUnaryOperation.Sine, destination, source, modifiers)
      case Instruction.Cos(destination, source, modifiers, _)
          if signature.opcode == Opcode.Cos && qualifiers.nonEmpty =>
        floatUnary(FloatUnaryOperation.Cosine, destination, source, modifiers)
      case Instruction.Tanh(destination, source, modifiers, _)
          if signature.opcode == Opcode.Tanh && qualifiers.nonEmpty =>
        floatUnary(FloatUnaryOperation.Tanh, destination, source, modifiers)
      case Instruction.Not(destination, source, modifiers, _) =>
        if (signature.matches(Opcode.Not, Modifier.Pred))
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
          if signature.opcode == Opcode.Popc && qualifiers.nonEmpty =>
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
          if signature.opcode == Opcode.Clz && qualifiers.nonEmpty =>
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
          if signature.opcode == Opcode.Bfind && qualifiers.nonEmpty =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.BitFind(
          destinationName,
          sourceValue,
          width,
          qualifiers.exists(_.isSigned),
          qualifiers.contains(Modifier.Shiftamt)
        )
      case Instruction.Dp4a(destination, left, right, addend, modifiers, _)
          if signature.opcode == Opcode.Dp4a && qualifiers.nonEmpty =>
        val types = modifiers.filter(Set(Modifier.S32, Modifier.U32))
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
            types.head == Modifier.S32,
            types(1) == Modifier.S32
          )
      case Instruction.Brev(destination, source, modifiers, _)
          if signature.opcode == Opcode.Brev && qualifiers.nonEmpty =>
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
      case Instruction.Mul(destination, left, right, _, _)
          if signature.matches(Opcode.Mul, Modifier.Wide, Modifier.U32) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned32,
          destinationName,
          leftValue,
          rightValue,
          64
        )
      case Instruction.Mul(destination, left, right, _, _)
          if signature.matches(Opcode.Mul, Modifier.Wide, Modifier.S32) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideSigned32,
          destinationName,
          leftValue,
          rightValue,
          64
        )
      case Instruction.Mul(destination, left, right, _, _)
          if signature.matches(Opcode.Mul, Modifier.Wide, Modifier.U16) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned16,
          destinationName,
          leftValue,
          rightValue,
          32
        )
      case Instruction.Mul(destination, left, right, _, _)
          if signature.matches(Opcode.Mul, Modifier.Wide, Modifier.S16) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideSigned16,
          destinationName,
          leftValue,
          rightValue,
          32
        )
      case Instruction.Div(destination, left, right, modifiers, _)
          if qualifiers.endsWith(Vec(Modifier.F16)) ||
            qualifiers.endsWith(Vec(Modifier.F32)) ||
            qualifiers.endsWith(Vec(Modifier.F64)) =>
        floatBinary(FloatOperation.Divide, destination, left, right, modifiers)
      case Instruction.Div(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.Div && qualifiers.nonEmpty =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Divide(qualifiers.exists(_.isSigned)),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Rem(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.Rem && qualifiers.nonEmpty =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Remainder(qualifiers.exists(_.isSigned)),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Mul(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.Mul && qualifiers.startsWith(Vec(Modifier.Lo)) && qualifiers.size > 1 =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.MultiplyLow, _, destination, left, right))
      case Instruction.Mul(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.Mul && qualifiers.startsWith(Vec(Modifier.Hi)) && qualifiers.size > 1 =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.MultiplyHigh(qualifiers.exists(_.isSigned)),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Mul(destination, left, right, _, _)
          if qualifiers.endsWith(Vec(Modifier.F16x2)) || qualifiers.endsWith(Vec(Modifier.Bf16x2)) =>
        packedFloatBinary(
          FloatOperation.Multiply,
          destination,
          left,
          right,
          qualifiers.endsWith(Vec(Modifier.Bf16x2))
        )
      case Instruction.Mul(destination, left, right, modifiers, _)
          if (signature.opcode == Opcode.Mul && qualifiers.nonEmpty) &&
            (qualifiers.endsWith(Vec(Modifier.F16)) ||
              qualifiers.endsWith(Vec(Modifier.F32)) ||
              qualifiers.endsWith(Vec(Modifier.F64))) =>
        floatBinary(FloatOperation.Multiply, destination, left, right, modifiers)
      case Instruction.CopySign(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.CopySign && qualifiers.nonEmpty =>
        floatBinary(FloatOperation.CopySign, destination, left, right, modifiers)
      case Instruction.Fma(destination, left, right, addend, _, _)
          if qualifiers.endsWith(Vec(Modifier.F16x2)) || qualifiers.endsWith(Vec(Modifier.Bf16x2)) =>
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
          qualifiers.endsWith(Vec(Modifier.Bf16x2)),
          saturate = qualifiers.contains(Modifier.Sat)
        )
      case Instruction.Fma(destination, left, right, addend, _, _)
          if qualifiers.endsWith(Vec(Modifier.Bf16)) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield VMIR.Instruction.FusedMultiplyAddBf16(destinationName, leftValue, rightValue, addendValue)
      case Instruction.Fma(destination, left, right, addend, modifiers, _)
          if signature.opcode == Opcode.Fma && qualifiers.nonEmpty =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width           <- floatWidth(modifiers)
        } yield VMIR.Instruction.FusedMultiplyAdd(
          destinationName,
          leftValue,
          rightValue,
          addendValue,
          width,
          floatControl(modifiers)
        )
      case Instruction.Shl(destination, left, right, _, _) if signature.matches(Opcode.Shl, Modifier.B32) =>
        integerBinary(
          IntegerOperation.ShiftLeft,
          32,
          destination,
          left,
          right
        )
      case Instruction.Shl(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.Shl && qualifiers.nonEmpty =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.ShiftLeft, _, destination, left, right))
      case Instruction.Shr(destination, left, right, modifiers, _)
          if (signature.opcode == Opcode.Shr && qualifiers.nonEmpty && Set(
            Modifier.U16,
            Modifier.U16x2,
            Modifier.U2,
            Modifier.U32,
            Modifier.U4,
            Modifier.U64,
            Modifier.U8,
            Modifier.U8x4
          )(qualifiers(0))) ||
            (signature.opcode == Opcode.Shr && qualifiers.nonEmpty && Set(
              Modifier.B1024,
              Modifier.B128,
              Modifier.B16,
              Modifier.B32,
              Modifier.B64,
              Modifier.B8,
              Modifier.B8x16
            )(qualifiers(0))) =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.ShiftRightLogical, _, destination, left, right))
      case Instruction.Shr(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.Shr && qualifiers.nonEmpty && Set(
            Modifier.S16,
            Modifier.S16x2,
            Modifier.S2,
            Modifier.S32,
            Modifier.S4,
            Modifier.S64,
            Modifier.S8,
            Modifier.S8x4
          )(qualifiers(0)) =>
        integerWidth(modifiers).flatMap(integerBinary(
          IntegerOperation.ShiftRightArithmetic,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Shf(destination, low, high, amount, _, _)
          if (signature.opcode == Opcode.Shf && qualifiers.nonEmpty) && qualifiers.endsWith(Vec(Modifier.B32)) =>
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
          signature.opcode == Opcode.Shf && qualifiers.startsWith(Vec(Modifier.L)) && qualifiers.size > 1,
          qualifiers.contains(Modifier.Clamp)
        )
      case Instruction.ShflSync(destination, source, delta, clamp, memberMask, modifiers, _)
          if Set(
            InstructionSignature(Opcode.Shfl, Vec(Modifier.Sync, Modifier.Down, Modifier.B32)),
            InstructionSignature(Opcode.Shfl, Vec(Modifier.Sync, Modifier.Down, Modifier.B64))
          ).contains(signature) =>
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
          if Set(
            InstructionSignature(Opcode.Shfl, Vec(Modifier.Sync, Modifier.Up, Modifier.B32)),
            InstructionSignature(Opcode.Shfl, Vec(Modifier.Sync, Modifier.Up, Modifier.B64))
          ).contains(signature) =>
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
          if Set(
            InstructionSignature(Opcode.Shfl, Vec(Modifier.Sync, Modifier.Idx, Modifier.B32)),
            InstructionSignature(Opcode.Shfl, Vec(Modifier.Sync, Modifier.Idx, Modifier.B64))
          ).contains(signature) =>
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
          if Set(
            InstructionSignature(Opcode.Shfl, Vec(Modifier.Sync, Modifier.Bfly, Modifier.B32)),
            InstructionSignature(Opcode.Shfl, Vec(Modifier.Sync, Modifier.Bfly, Modifier.B64))
          ).contains(signature) =>
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
      case Instruction.Vote(destination, Some(predicate), _, _)
          if signature.matches(Opcode.Vote, Modifier.Ballot, Modifier.B32) =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
        } yield VMIR.Instruction.VoteBallot(destinationName, predicateName, Value.Immediate(0xffffffffL))
      case Instruction.VoteSync(destination, Some(predicate), Some(memberMask), _, _)
          if signature.matches(Opcode.Vote, Modifier.Sync, Modifier.Ballot, Modifier.B32) =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
          memberMaskValue <- value(memberMask)
        } yield VMIR.Instruction.VoteBallot(destinationName, predicateName, memberMaskValue)
      case Instruction.VoteSync(destination, Some(predicate), Some(memberMask), _, _)
          if signature.matches(Opcode.Vote, Modifier.Sync, Modifier.All, Modifier.Pred) ||
            signature.matches(Opcode.Vote, Modifier.Sync, Modifier.Any, Modifier.Pred) ||
            signature.matches(Opcode.Vote, Modifier.Sync, Modifier.Uni, Modifier.Pred) =>
        for {
          destinationName <- predicateRegisterName(destination)
          predicateName   <- predicateRegisterName(predicate)
          memberMaskValue <- value(memberMask)
          operation = if (qualifiers.endsWith(Vec(Modifier.All, Modifier.Pred))) VoteOperation.All
          else if (qualifiers.endsWith(Vec(Modifier.Any, Modifier.Pred))) VoteOperation.Any
          else VoteOperation.Uniform
        } yield VMIR.Instruction.VotePredicate(destinationName, predicateName, memberMaskValue, operation)
      case Instruction.Bfe(destination, source, start, widthValue, modifiers, _)
          if signature.opcode == Opcode.Bfe && qualifiers.nonEmpty =>
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
          signed = qualifiers.exists(_.isSigned)
        )
      case Instruction.Bfi(destination, insertValue, baseValue, bitPosition, bitLength, modifiers, _)
          if signature.opcode == Opcode.Bfi && qualifiers.nonEmpty =>
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
          if signature.opcode == Opcode.Bmsk && qualifiers.nonEmpty =>
        for {
          destinationName <- registerName(destination)
          startValue      <- value(start)
          widthOperand    <- value(widthValue)
          mode            <- modifiers.view.collectFirst {
            case Modifier.Clamp => BitMaskMode.Clamp
            case Modifier.Wrap  => BitMaskMode.Wrap
          }
            .toRight(Error.Invalid("bmsk requires .clamp or .wrap"))
          _ <- modifiers.find(_ == Modifier.B32).toRight(
            Error.Invalid("bmsk requires a .b32 width")
          )
        } yield VMIR.Instruction.BitMask(destinationName, startValue, widthOperand, mode)
      case Instruction.Prmt(destination, left, right, control, _, _)
          if signature.opcode == Opcode.Prmt && qualifiers.startsWith(Vec(Modifier.B32)) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          controlValue    <- value(control)
          suffix = qualifiers.drop(1).headOption
          mode <- BytePermuteMode.values.find(_.modifier == suffix).toRight(
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
          if qualifiers.contains(Modifier.Lo) &&
            (qualifiers.endsWith(Vec(Modifier.S32)) || qualifiers.endsWith(Vec(Modifier.U32)) ||
              qualifiers.endsWith(Vec(Modifier.S64)) || qualifiers.endsWith(Vec(Modifier.U64))) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width: (Int where (Between[8, 64] & MultipleOf[8])) =
            if (qualifiers.lastOption.exists(_.bitWidth == 32)) 32 else 64
        } yield VMIR.Instruction.MultiplyAdd(
          destination = destinationName,
          left = leftValue,
          right = rightValue,
          addend = addendValue,
          operation = IntegerMultiplyAddOperation.Low(
            operandWidth = width,
            resultWidth = width,
            carryIn = false,
            carryOut = qualifiers.contains(Modifier.Cc)
          )
        )
      case Instruction.MadCc(destination, left, right, addend, _, _)
          if qualifiers.contains(Modifier.Lo) &&
            (qualifiers.endsWith(Vec(Modifier.S32)) || qualifiers.endsWith(Vec(Modifier.U32)) ||
              qualifiers.endsWith(Vec(Modifier.S64)) || qualifiers.endsWith(Vec(Modifier.U64))) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width: (Int where (Between[8, 64] & MultipleOf[8])) =
            if (qualifiers.lastOption.exists(_.bitWidth == 32)) 32 else 64
        } yield VMIR.Instruction.MultiplyAdd(
          destination = destinationName,
          left = leftValue,
          right = rightValue,
          addend = addendValue,
          operation =
            IntegerMultiplyAddOperation.Low(operandWidth = width, resultWidth = width, carryIn = false, carryOut = true)
        )
      case Instruction.Mad(destination, left, right, addend, _, _)
          if qualifiers.contains(Modifier.Wide) && qualifiers.endsWith(Vec(Modifier.U32)) =>
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
            IntegerMultiplyAddOperation.Low(
              operandWidth = 32,
              resultWidth = 64,
              carryIn = false,
              carryOut = false
            )
        )
      case Instruction.Madc(destination, left, right, addend, _, _)
          if (qualifiers.contains(Modifier.Lo) || qualifiers.contains(Modifier.Hi)) &&
            (qualifiers.endsWith(Vec(Modifier.S32)) || qualifiers.endsWith(Vec(Modifier.U32)) ||
              qualifiers.endsWith(Vec(Modifier.S64)) || qualifiers.endsWith(Vec(Modifier.U64))) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width: (Int where (Between[8, 64] & MultipleOf[8])) =
            if (qualifiers.lastOption.exists(_.bitWidth == 32)) 32 else 64
          operation = if (qualifiers.contains(Modifier.Hi))
            IntegerMultiplyAddOperation.High(
              operandWidth = width,
              resultWidth = width,
              carryIn = true,
              carryOut = qualifiers.contains(Modifier.Cc)
            )
          else IntegerMultiplyAddOperation.Low(
            operandWidth = width,
            resultWidth = width,
            carryIn = true,
            carryOut = qualifiers.contains(Modifier.Cc)
          )
        } yield VMIR.Instruction.MultiplyAdd(
          destination = destinationName,
          left = leftValue,
          right = rightValue,
          addend = addendValue,
          operation = operation
        )
      case Instruction.AddCc(destination, left, right, _, _)
          if qualifiers.endsWith(Vec(Modifier.U32)) || qualifiers.endsWith(Vec(Modifier.S32)) ||
            qualifiers.endsWith(Vec(Modifier.U64)) || qualifiers.endsWith(Vec(Modifier.S64)) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width: (Int where (Between[8, 64] & MultipleOf[8])) =
            if (qualifiers.lastOption.exists(_.bitWidth == 32)) 32 else 64
        } yield VMIR.Instruction.IntegerAddWithCarry(
          destinationName,
          leftValue,
          rightValue,
          width,
          carryIn = false,
          carryOut = true
        )
      case Instruction.Addc(destination, left, right, _, _)
          if qualifiers.endsWith(Vec(Modifier.U32)) || qualifiers.endsWith(Vec(Modifier.S32)) ||
            qualifiers.endsWith(Vec(Modifier.U64)) || qualifiers.endsWith(Vec(Modifier.S64)) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width: (Int where (Between[8, 64] & MultipleOf[8])) =
            if (qualifiers.lastOption.exists(_.bitWidth == 32)) 32 else 64
        } yield VMIR.Instruction.IntegerAddWithCarry(
          destinationName,
          leftValue,
          rightValue,
          width,
          carryIn = true,
          carryOut = qualifiers.contains(Modifier.Cc)
        )
      case Instruction.SubCc(destination, left, right, _, _)
          if qualifiers.endsWith(Vec(Modifier.U32)) || qualifiers.endsWith(Vec(Modifier.S32)) ||
            qualifiers.endsWith(Vec(Modifier.U64)) || qualifiers.endsWith(Vec(Modifier.S64)) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width: (Int where (Between[8, 64] & MultipleOf[8])) =
            if (qualifiers.lastOption.exists(_.bitWidth == 32)) 32 else 64
        } yield VMIR.Instruction.IntegerSubWithBorrow(
          destinationName,
          leftValue,
          rightValue,
          width,
          borrowIn = false,
          borrowOut = true
        )
      case Instruction.Subc(destination, left, right, _, _)
          if qualifiers.endsWith(Vec(Modifier.U32)) || qualifiers.endsWith(Vec(Modifier.S32)) ||
            qualifiers.endsWith(Vec(Modifier.U64)) || qualifiers.endsWith(Vec(Modifier.S64)) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width: (Int where (Between[8, 64] & MultipleOf[8])) =
            if (qualifiers.lastOption.exists(_.bitWidth == 32)) 32 else 64
        } yield VMIR.Instruction.IntegerSubWithBorrow(
          destinationName,
          leftValue,
          rightValue,
          width,
          borrowIn = true,
          borrowOut = qualifiers.contains(Modifier.Cc)
        )
      case Instruction.Add(destination, left, right, _, _)
          if signature.matches(Opcode.Add, Modifier.U32) || signature.matches(Opcode.Add, Modifier.S32) =>
        integerBinary(IntegerOperation.Add, 32, destination, left, right)
      case Instruction.Add(destination, left, right, _, _)
          if signature.matches(Opcode.Add, Modifier.U64) || signature.matches(Opcode.Add, Modifier.S64) =>
        integerBinary(IntegerOperation.Add, 64, destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if signature.matches(Opcode.Sub, Modifier.U32) || signature.matches(Opcode.Sub, Modifier.S32) =>
        integerBinary(
          IntegerOperation.Subtract,
          32,
          destination,
          left,
          right
        )
      case Instruction.Sub(destination, left, right, _, _)
          if signature.matches(Opcode.Sub, Modifier.U64) || signature.matches(Opcode.Sub, Modifier.S64) =>
        integerBinary(
          IntegerOperation.Subtract,
          64,
          destination,
          left,
          right
        )
      case Instruction.And(destination, left, right, _, _)
          if signature.matches(Opcode.And, Modifier.Pred) =>
        predicateBinary(PredicateOperation.And, destination, left, right)
      case Instruction.Or(destination, left, right, _, _)
          if signature.matches(Opcode.Or, Modifier.Pred) =>
        predicateBinary(PredicateOperation.Or, destination, left, right)
      case Instruction.Xor(destination, left, right, _, _)
          if signature.matches(Opcode.Xor, Modifier.Pred) =>
        predicateBinary(PredicateOperation.Xor, destination, left, right)
      case Instruction.And(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.And && qualifiers.nonEmpty =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.BitwiseAnd, _, destination, left, right))
      case Instruction.Or(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.Or && qualifiers.nonEmpty =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.BitwiseOr, _, destination, left, right))
      case Instruction.Xor(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.Xor && qualifiers.nonEmpty =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.BitwiseXor, _, destination, left, right))
      case Instruction.Add(destination, left, right, _, _)
          if qualifiers.endsWith(Vec(Modifier.F16x2)) || qualifiers.endsWith(Vec(Modifier.Bf16x2)) =>
        packedFloatBinary(
          FloatOperation.Add,
          destination,
          left,
          right,
          qualifiers.endsWith(Vec(Modifier.Bf16x2))
        )
      case Instruction.Add(destination, left, right, modifiers, _)
          if (signature.opcode == Opcode.Add && qualifiers.nonEmpty) &&
            (qualifiers.endsWith(Vec(Modifier.F16)) ||
              qualifiers.endsWith(Vec(Modifier.F32)) ||
              qualifiers.endsWith(Vec(Modifier.F64))) =>
        floatBinary(FloatOperation.Add, destination, left, right, modifiers)
      case Instruction.Sub(destination, left, right, _, _)
          if qualifiers.endsWith(Vec(Modifier.F16x2)) || qualifiers.endsWith(Vec(Modifier.Bf16x2)) =>
        packedFloatBinary(
          FloatOperation.Subtract,
          destination,
          left,
          right,
          qualifiers.endsWith(Vec(Modifier.Bf16x2))
        )
      case Instruction.Sub(destination, left, right, modifiers, _)
          if (signature.opcode == Opcode.Sub && qualifiers.nonEmpty) &&
            (qualifiers.endsWith(Vec(Modifier.F16)) ||
              qualifiers.endsWith(Vec(Modifier.F32)) ||
              qualifiers.endsWith(Vec(Modifier.F64))) =>
        floatBinary(FloatOperation.Subtract, destination, left, right, modifiers)
      case Instruction.Add(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.Add && qualifiers.nonEmpty =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.Add, _, destination, left, right))
      case Instruction.Sub(destination, left, right, modifiers, _)
          if signature.opcode == Opcode.Sub && qualifiers.nonEmpty =>
        integerWidth(modifiers).flatMap(integerBinary(IntegerOperation.Subtract, _, destination, left, right))
      case Instruction.Min(destination, left, right, _, _, _)
          if qualifiers.endsWith(Vec(Modifier.F16x2)) || qualifiers.endsWith(Vec(Modifier.Bf16x2)) =>
        packedFloatBinary(
          FloatOperation.Minimum,
          destination,
          left,
          right,
          qualifiers.endsWith(Vec(Modifier.Bf16x2))
        )
      case Instruction.Min(destination, left, right, _, modifiers, _)
          if qualifiers.exists(_.isFloating) =>
        floatBinary(FloatOperation.Minimum, destination, left, right, modifiers)
      case Instruction.Max(destination, left, right, _, _, _)
          if qualifiers.endsWith(Vec(Modifier.F16x2)) || qualifiers.endsWith(Vec(Modifier.Bf16x2)) =>
        packedFloatBinary(
          FloatOperation.Maximum,
          destination,
          left,
          right,
          qualifiers.endsWith(Vec(Modifier.Bf16x2))
        )
      case Instruction.Max(destination, left, right, _, modifiers, _)
          if qualifiers.exists(_.isFloating) =>
        floatBinary(FloatOperation.Maximum, destination, left, right, modifiers)
      case Instruction.Min(destination, left, right, _, modifiers, _)
          if signature.opcode == Opcode.Min && qualifiers.nonEmpty =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Minimum(qualifiers.exists(_.isSigned)),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Max(destination, left, right, _, modifiers, _)
          if signature.opcode == Opcode.Max && qualifiers.nonEmpty =>
        integerWidth(modifiers).flatMap { width =>
          integerBinary(
            IntegerOperation.Maximum(qualifiers.exists(_.isSigned)),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Cvta(destination, source, _, _)
          if signature.matches(Opcode.Cvta, Modifier.Global, Modifier.U64) ||
            signature.matches(Opcode.Cvta, Modifier.To, Modifier.Global, Modifier.U64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvta(destination, source, _, _)
          if signature.matches(Opcode.Cvta, Modifier.Local, Modifier.U64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvta(destination, source, _, _)
          if signature.matches(Opcode.Cvta, Modifier.To, Modifier.Local, Modifier.U64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvta(destination, source, _, _)
          if signature.matches(Opcode.Cvta, Modifier.Shared, Modifier.U64) ||
            signature.matches(Opcode.Cvta, Modifier.To, Modifier.Shared, Modifier.U64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvt(destination, Some(source), None, None, None, None, modifiers, _) =>
        val types = modifiers.flatMap(modifier => ConversionType.values.find(_.modifier == modifier))
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
            modifiers.view.flatMap(modifier => ConversionRounding.values.find(_.modifier == modifier))
              .headOption
          )
      case Instruction.Selp(destination, whenTrue, whenFalse, predicate, modifiers, _)
          if signature.opcode == Opcode.Selp && qualifiers.nonEmpty =>
        for {
          destinationName <- registerName(destination)
          falseValue      <- value(whenFalse)
          trueValue       <- value(whenTrue)
          predicateName   <- predicateRegisterName(predicate)
          width           <- scalarWidth(modifiers)
        } yield VMIR.Instruction.Select(destinationName, trueValue, falseValue, predicateName, width)
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if orderedMemoryForm(signature, load = true) =>
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
          if orderedMemoryForm(signature, load = false) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.AtomicExchange(
          "_": NonEmpty[String],
          addressValue,
          sourceValue,
          width,
          VMIR.AtomicSpace.Global
        )
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if (qualifiers match {
            case Vec(scalar) => scalar.bitWidth > 0
            case _           => false
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
          qualifiers.exists(_.isSigned)
        )
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if (signature.opcode == Opcode.Ld && qualifiers.nonEmpty) &&
            !qualifiers.contains(Modifier.Param) &&
            !qualifiers.contains(Modifier.Shared) &&
            !qualifiers.contains(Modifier.Local) &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGenericVector(
          destinationNames,
          addressValue,
          width,
          qualifiers.lastOption.exists(_.isSigned)
        )
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if (signature.opcode == Opcode.Ld && qualifiers.nonEmpty) && qualifiers.contains(Modifier.Global) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          qualifiers.lastOption.exists(_.isSigned)
        )
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if (signature.opcode == Opcode.Ld && qualifiers.nonEmpty) &&
            !qualifiers.contains(Modifier.Param) &&
            !qualifiers.contains(Modifier.Shared) &&
            !qualifiers.contains(Modifier.Local) &&
            !qualifiers.contains(Modifier.Global) &&
            !Operand.isVectorLiteral(destination) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          qualifiers.lastOption.exists(_.isSigned)
        )
      case Instruction.LdGlobalNc(destination, address, None, modifiers, _)
          if (signature.opcode == Opcode.Ld && qualifiers.startsWith(Vec(Modifier.Global, Modifier.Nc)) &&
            qualifiers.size > 2) && Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGlobalVector(
          destinationNames,
          addressValue,
          width,
          qualifiers.exists(_.isSigned)
        )
      case Instruction.LdGlobalNc(destination, address, None, modifiers, _)
          if signature.opcode == Opcode.Ld && qualifiers.startsWith(Vec(Modifier.Global, Modifier.Nc)) &&
            qualifiers.size > 2 =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          qualifiers.lastOption.exists(_.isSigned)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if (qualifiers match {
            case Vec(scalar) => scalar.bitWidth > 0
            case _           => false
          }) &&
            !Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreGeneric(addressValue, sourceValue, width)
      case Instruction.St(address, source, None, modifiers, _)
          if (signature.opcode == Opcode.St && qualifiers.nonEmpty) &&
            !qualifiers.contains(Modifier.Shared) &&
            !qualifiers.contains(Modifier.Local) &&
            Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreGenericVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if (signature.opcode == Opcode.St && qualifiers.nonEmpty) &&
            !qualifiers.contains(Modifier.Shared) &&
            !qualifiers.contains(Modifier.Local) &&
            !Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreGlobal(addressValue, sourceValue, width)
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if (signature.opcode == Opcode.Ld && qualifiers.nonEmpty) && qualifiers.contains(Modifier.Shared) &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadSharedVector(
          destinationNames,
          addressValue,
          width,
          qualifiers.lastOption.exists(_.isSigned)
        )
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if (signature.opcode == Opcode.Ld && qualifiers.nonEmpty) && qualifiers.contains(Modifier.Shared) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadShared(
          destinationName,
          addressValue,
          width,
          qualifiers.lastOption.exists(_.isSigned)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if (signature.opcode == Opcode.St && qualifiers.nonEmpty) && qualifiers.contains(Modifier.Shared) &&
            Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreSharedVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if (signature.opcode == Opcode.St && qualifiers.nonEmpty) && qualifiers.contains(Modifier.Shared) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreShared(addressValue, sourceValue, width)
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if (signature.opcode == Opcode.Ld && qualifiers.nonEmpty) && qualifiers.contains(Modifier.Local) &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadLocalVector(
          destinationNames,
          addressValue,
          width,
          qualifiers.lastOption.exists(_.isSigned)
        )
      case Instruction.Ld(destination, address, None, None, modifiers, _)
          if (signature.opcode == Opcode.Ld && qualifiers.nonEmpty) && qualifiers.contains(Modifier.Local) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield VMIR.Instruction.LoadLocal(
          destinationName,
          addressValue,
          width,
          qualifiers.lastOption.exists(_.isSigned)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if (signature.opcode == Opcode.St && qualifiers.nonEmpty) && qualifiers.contains(Modifier.Local) &&
            Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(modifiers)
        } yield VMIR.Instruction.StoreLocalVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if (signature.opcode == Opcode.St && qualifiers.nonEmpty) && qualifiers.contains(Modifier.Local) =>
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
            if (Set(4L, 8L, 16L).contains(value)) Right(value.toInt.unsafe[Int > 0])
            else invalid(s"cp.async copy size must be 4, 8, or 16 bytes, found $value")
          }
        } yield VMIR.Instruction.AsyncCopy(destinationValue, sourceValue, size)
      case Instruction.CpAsyncCommitGroup(_, _)  => Right(VMIR.Instruction.AsyncCommit)
      case Instruction.CpAsyncWaitAll(_, _)      => Right(VMIR.Instruction.AsyncWait)
      case Instruction.CpAsyncWaitGroup(_, _, _) => Right(VMIR.Instruction.AsyncWait)
      case Instruction.ReduxSync(destination, source, memberMask, modifiers, _)
          if signature.opcode == Opcode.Redux && qualifiers.startsWith(Vec(Modifier.Sync)) && qualifiers.size > 1 =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
          operation       <-
            if (qualifiers.contains(Modifier.Add)) Right(IntegerOperation.Add)
            else if (qualifiers.contains(Modifier.Min))
              Right(IntegerOperation.Minimum(modifiers.exists(value => value.isSigned && value.bitWidth == width.raw)))
            else if (qualifiers.contains(Modifier.Max))
              Right(IntegerOperation.Maximum(modifiers.exists(value => value.isSigned && value.bitWidth == width.raw)))
            else invalid(s"unsupported redux.sync operation ${instruction.qualifiedOpcode}")
        } yield VMIR.Instruction.WarpReduce(destinationName, sourceValue, memberMaskValue, operation, width)
      case Instruction.Atom(destination, address, source, None, _, _)
          if atomicBinaryForm(atomicSignature(signature)) =>
        atomicIntegerOperands(destination, address, source, signature).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            val operation =
              if (atomicSignature(signature).modifiers.contains(Modifier.Max)) IntegerOperation.Maximum(false)
              else if (atomicSignature(signature).modifiers.contains(Modifier.And)) IntegerOperation.BitwiseAnd
              else if (atomicSignature(signature).modifiers.contains(Modifier.Or)) IntegerOperation.BitwiseOr
              else IntegerOperation.BitwiseXor
            VMIR.Instruction.AtomicBinary(destinationName, addressValue, sourceValue, width, space, operation)
        }
      case Instruction.Atom(destination, address, comparator, Some(source), _, _)
          if Set(
            InstructionSignature(Opcode.Atom, Vec(Modifier.Cas, Modifier.B32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Cas, Modifier.B64)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Global, Modifier.Cas, Modifier.B32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Global, Modifier.Cas, Modifier.B64)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Shared, Modifier.Cas, Modifier.B32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Shared, Modifier.Cas, Modifier.B64))
          )(atomicSignature(signature)) =>
        for {
          comparatorValue <- value(comparator)
          operands        <- atomicIntegerOperands(destination, address, source, signature)
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
            InstructionSignature(Opcode.Atom, Vec(Modifier.Exch, Modifier.B32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Exch, Modifier.B64)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Global, Modifier.Exch, Modifier.B32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Global, Modifier.Exch, Modifier.B64)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Shared, Modifier.Exch, Modifier.B32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Shared, Modifier.Exch, Modifier.B64))
          )(atomicSignature(signature)) =>
        atomicIntegerOperands(destination, address, source, signature).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            VMIR.Instruction.AtomicExchange(destinationName, addressValue, sourceValue, width, space)
        }
      case Instruction.Atom(destination, address, source, None, _, _)
          if Set(
            InstructionSignature(Opcode.Atom, Vec(Modifier.Add, Modifier.U32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Add, Modifier.U64)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Global, Modifier.Add, Modifier.U32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Global, Modifier.Add, Modifier.U64)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Shared, Modifier.Add, Modifier.U32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Shared, Modifier.Add, Modifier.U64))
          )(atomicSignature(signature)) =>
        atomicIntegerOperands(destination, address, source, signature).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            VMIR.Instruction.AtomicAdd(destinationName, addressValue, sourceValue, width, space)
        }
      case Instruction.Atom(destination, address, source, None, _, _)
          if Set(
            InstructionSignature(Opcode.Atom, Vec(Modifier.Min, Modifier.U32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Min, Modifier.U64)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Shared, Modifier.Min, Modifier.U32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Shared, Modifier.Min, Modifier.U64)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Global, Modifier.Min, Modifier.U32)),
            InstructionSignature(Opcode.Atom, Vec(Modifier.Global, Modifier.Min, Modifier.U64))
          )(atomicSignature(signature)) =>
        atomicIntegerOperands(destination, address, source, signature).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            VMIR.Instruction.AtomicMin(destinationName, addressValue, sourceValue, width, space)
        }
      case Instruction.Atom(destination, address, source, None, _, _)
          if atomicSignature(signature) ==
            InstructionSignature(Opcode.Atom, Vec(Modifier.Global, Modifier.Add, Modifier.F32)) ||
            atomicSignature(signature) ==
            InstructionSignature(Opcode.Atom, Vec(Modifier.Global, Modifier.Add, Modifier.F64)) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width: (Int in (16, 32, 64)) =
            if (atomicSignature(signature).modifiers.endsWith(Vec(Modifier.F64))) 64 else 32
        } yield VMIR.Instruction.AtomicAddFloat(destinationName, addressValue, sourceValue, width)
      case Instruction.Setp(destination, left, right, None, modifiers, _) =>
        lowerSetPredicate(destination, left, right, modifiers)
      case Instruction.Bar(destination, Some(barrier), Some(predicate), None, _, _)
          if signature.matches(Opcode.Bar, Modifier.Red, Modifier.And, Modifier.Pred) ||
            signature.matches(Opcode.Bar, Modifier.Red, Modifier.Or, Modifier.Pred) =>
        for {
          destinationName <- predicateRegisterName(destination)
          barrierId       <- immediate(barrier)
          predicateName   <- predicateRegisterName(predicate)
          operation = if (qualifiers.endsWith(Vec(Modifier.And, Modifier.Pred))) PredicateOperation.And
          else PredicateOperation.Or
          checkedBarrierId <-
            if (barrierId >= 0L && barrierId <= Int.MaxValue.toLong)
              Right(barrierId.toInt.unsafe[Int >= 0])
            else invalid(s"barrier id is outside Int range: $barrierId")
        } yield VMIR.Instruction.BarrierReduce(destinationName, checkedBarrierId, predicateName, operation)
      case Instruction.Bra(target, _, _)
          if signature == InstructionSignature(Opcode.Bra, Vec.empty[Modifier]) ||
            signature.matches(Opcode.Bra, Modifier.Uni) =>
        target match {
          case Operand.Symbol(label) =>
            labels.get(label)
              .toRight(Error.Invalid(s"unknown label: $label"))
              .map(VMIR.Instruction.Branch(_))
          case other => invalid(s"expected branch label, found $other")
        }
      case Instruction.Bar(_, _, _, _, _, Some(_)) if signature.matches(Opcode.Bar, Modifier.Sync) =>
        invalid("bar.sync must not be predicated in the supported execution profile")
      case Instruction.Bar(barrier, None, None, None, _, _) if signature.matches(Opcode.Bar, Modifier.Sync) =>
        immediate(barrier).flatMap { value =>
          if (value >= 0L && value <= Int.MaxValue.toLong)
            Right(VMIR.Instruction.BarrierSync(value.toInt.unsafe[Int >= 0]))
          else invalid(s"barrier id is outside Int range: $value")
        }
      case Instruction.Membar(_, _) if signature.opcode == Opcode.Membar && qualifiers.nonEmpty =>
        Right(VMIR.Instruction.MemoryBarrier)
      case Instruction.Fence(None, None, _, _)
          if fenceForm(signature) =>
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
        Left(Error.Unsupported(instruction.opcode, "no handwritten semantic family"))
    }
    for {
      _         <- validateFloatingInstruction(instruction)
      operation <- lowered
      predicate <- lowerPredicate(instruction.pred)
    } yield PredicatedInstruction(operation, predicate)
  }

  private val FloatingOpcodes = Set(
    Opcode.Add,
    Opcode.Sub,
    Opcode.Mul,
    Opcode.Div,
    Opcode.Fma,
    Opcode.Abs,
    Opcode.Neg,
    Opcode.Min,
    Opcode.Max,
    Opcode.CopySign,
    Opcode.Rcp,
    Opcode.Rsqrt,
    Opcode.Sqrt,
    Opcode.Ex2,
    Opcode.Lg2,
    Opcode.Sin,
    Opcode.Cos,
    Opcode.Tanh
  )
  private val ScalarFloatTypes = Set(Modifier.F16, Modifier.F32, Modifier.F64)
  private val PackedFloatTypes = Set(Modifier.F16x2, Modifier.Bf16x2)

  private def floatControl(modifiers: Vec[Modifier]): FloatControl = FloatControl(
    flushSubnormal = modifiers.contains(Modifier.Ftz),
    saturate = modifiers.contains(Modifier.Sat),
    propagateNaN = modifiers.contains(Modifier.NaN),
    absolute = modifiers.contains(Modifier.Abs),
    xorSign = modifiers.contains(Modifier.Xorsign)
  )

  private def validateFloatingInstruction(instruction: Instruction): Either[Error, Unit] = {
    val signature = instruction.signature
    val modifiers = signature.modifiers
    val types     = ScalarFloatTypes ++ PackedFloatTypes + Modifier.Bf16 + Modifier.F32x2
    if (!FloatingOpcodes(signature.opcode) || !modifiers.exists(types)) Right(())
    else {
      val packed  = modifiers.exists(PackedFloatTypes)
      val bf16    = modifiers.contains(Modifier.Bf16)
      val allowed = if (packed)
        PackedFloatTypes ++ (if (signature.opcode == Opcode.Fma) Set(Modifier.Rn, Modifier.Sat) else Set(Modifier.Rn))
      else if (bf16) Set(Modifier.Bf16, Modifier.Rn)
      else {
        val behavior = signature.opcode match {
          case Opcode.Abs | Opcode.Neg => Set(Modifier.Ftz)
          case Opcode.Min | Opcode.Max => Set(Modifier.Ftz, Modifier.NaN, Modifier.Abs, Modifier.Xorsign)
          case Opcode.CopySign         => Set.empty[Modifier]
          case Opcode.Add | Opcode.Sub | Opcode.Mul | Opcode.Fma => Set(Modifier.Rn, Modifier.Ftz, Modifier.Sat)
          case Opcode.Div => Set(Modifier.Rn, Modifier.Ftz, Modifier.Approx, Modifier.Full)
          case _          => Set(Modifier.Rn, Modifier.Ftz, Modifier.Approx)
        }
        ScalarFloatTypes ++ behavior
      }
      val extraOperand = instruction match {
        case Instruction.Min(_, _, _, Some(_), _, _) | Instruction.Max(_, _, _, Some(_), _, _) => true
        case _                                                                                 => false
      }
      val unsupported = modifiers.filterNot(allowed)
      if (extraOperand) Left(Error.Unsupported(instruction.opcode, "three-input min/max is not implemented"))
      else if (bf16 && signature.opcode != Opcode.Fma)
        Left(Error.Unsupported(instruction.opcode, "scalar bf16 arithmetic is not implemented for this operation"))
      else if (unsupported.nonEmpty)
        Left(Error.Unsupported(
          instruction.opcode,
          s"unsupported floating modifiers: ${unsupported.map(_.name).mkString(".")}"
        ))
      else if (modifiers.distinct.size != modifiers.size || modifiers.count(types) != 1)
        invalid("expected one floating type and no duplicate modifiers")
      else if (modifiers.contains(Modifier.Xorsign) && !modifiers.contains(Modifier.Abs))
        invalid("xorsign requires abs")
      else Right(())
    }
  }

  private def integerBinary(
      operation: IntegerOperation,
      width: Int where (Between[8, 64] & MultipleOf[8]),
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
    } yield VMIR.Instruction.FloatBinary(
      operation,
      destinationName,
      leftValue,
      rightValue,
      width,
      floatControl(modifiers)
    )

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
      fixedWidth: Option[Int in (16, 32, 64)] = None
  ): Either[Error, VMIR.Instruction] =
    for {
      destinationName <- registerName(destination)
      sourceValue     <- value(source)
      width           <- fixedWidth.fold(floatWidth(modifiers))(Right(_))
    } yield VMIR.Instruction.FloatUnary(operation, destinationName, sourceValue, width, floatControl(modifiers))

  private def lowerSetPredicate(
      destination: Operand,
      left: Operand,
      right: Operand,
      instructionModifiers: Vec[Modifier]
  ): Either[Error, VMIR.Instruction] = {
    val modifiers   = instructionModifiers
    val instruction = ("setp" +: modifiers.map(_.name)).mkString(".")
    val comparison  = modifiers.collectFirst(Function.unlift {
      case Modifier.Num => Some(Comparison.Ordered)
      case Modifier.Nan => Some(Comparison.Unordered)
      case Modifier.Eq  => Some(Comparison.Equal)
      case Modifier.Equ => Some(Comparison.EqualUnordered)
      case Modifier.Ne  => Some(Comparison.NotEqual)
      case Modifier.Neu => Some(Comparison.NotEqualUnordered)
      case Modifier.Lt  => Some(Comparison.LessThan)
      case Modifier.Ltu => Some(Comparison.LessThanUnordered)
      case Modifier.Le  => Some(Comparison.LessThanOrEqual)
      case Modifier.Leu => Some(Comparison.LessThanOrEqualUnordered)
      case Modifier.Gt  => Some(Comparison.GreaterThan)
      case Modifier.Gtu => Some(Comparison.GreaterThanUnordered)
      case Modifier.Ge  => Some(Comparison.GreaterThanOrEqual)
      case Modifier.Geu => Some(Comparison.GreaterThanOrEqualUnordered)
      case _            => None
    })
    val kind = modifiers.collectFirst {
      case Modifier.U8   => 'u' -> 8
      case Modifier.S8   => 's' -> 8
      case Modifier.U16  => 'u' -> 16
      case Modifier.S16  => 's' -> 16
      case Modifier.U32  => 'u' -> 32
      case Modifier.S32  => 's' -> 32
      case Modifier.U64  => 'u' -> 64
      case Modifier.S64  => 's' -> 64
      case Modifier.B8   => 'u' -> 8
      case Modifier.B16  => 'u' -> 16
      case Modifier.B32  => 'u' -> 32
      case Modifier.B64  => 'u' -> 64
      case Modifier.F16  => 'f' -> 16
      case Modifier.F32  => 'f' -> 32
      case Modifier.F64  => 'f' -> 64
      case Modifier.Bf16 => 'b' -> 16
      case Modifier.Pred => 'p' -> 1
    }
    for {
      predicate <- comparison.toRight(
        Error.Invalid(s"unsupported setp comparison: $instruction")
      )
      kindAndWidth <- kind.toRight(
        Error.Invalid(s"unsupported setp type: $instruction")
      )
      _ <- Either.cond(
        !modifiers.contains(Modifier.Ftz) || kindAndWidth == ('f', 32),
        (),
        Error.Unsupported("setp", "ftz requires f32 comparison operands")
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
            width      <- kindAndWidth._2.refine[Int where (Between[8, 64] & MultipleOf[8])]
              .leftMap(message => Error.Invalid(s"invalid setp width: $message"))
          } yield VMIR.Instruction.SetPredicate(
            destinationName,
            predicate,
            kindAndWidth._1,
            leftValue,
            rightValue,
            width,
            flushSubnormal = modifiers.contains(Modifier.Ftz)
          )
    } yield lowered
  }

  private def lowerPredicate(value: Option[Predication]): Either[Error, Option[ExecutionPredicate]] =
    value match {
      case None                                => Right(None)
      case Some(Predication(negated, operand)) =>
        predicateRegisterName(operand).map(name => Some(ExecutionPredicate(name, negated)))
    }

  private def predicateRegisterName(operand: Operand): Either[Error, NonEmpty[String]] =
    registerName(operand, "predicate register")

  private def registerName(operand: Operand, describe: String = "register"): Either[Error, NonEmpty[String]] =
    operand match {
      case Operand.Register(name, None) => Right(name)
      case Operand.Symbol(name)         => Right(name)
      case other                        => invalid(s"expected $describe, found $other")
    }

  private def valueRegisterName(operand: Operand): Either[Error, NonEmpty[String]] = operand match {
    case Operand.Register(name, None)                         => Right(name)
    case Operand.Symbol(name) if isBareRegisterName(name.raw) => Right(name)
    case other                                                => invalid(s"expected register, found $other")
  }

  private def valueDestination(
      operand: Operand
  ): Either[Error, (NonEmpty[String], Option[NonEmpty[String]])] = operand match {
    case Operand.WithPredicate(base, predicate) =>
      for {
        destinationName <- valueRegisterName(base)
        predicateName   <- predicateRegisterName(predicate)
      } yield destinationName -> Some(predicateName)
    case other => valueRegisterName(other).map(_ -> None)
  }

  private def registerNames(operand: Operand): Either[Error, Vec[NonEmpty[String]]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[Error, Vec[NonEmpty[String]]]](Right(Vec.empty)) { (current, item) =>
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

  private def splitRegisterNames(operand: Operand): Either[Error, Vec[NonEmpty[String]]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[Error, Vec[NonEmpty[String]]]](Right(Vec.empty)) { (current, item) =>
        for {
          names <- current
          name  <- item match {
            case Operand.Symbol(name) if name.raw == "_" => Right("_": NonEmpty[String])
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

  private def callParameters(operand: Option[Operand]): Either[Error, Vec[NonEmpty[String]]] = operand match {
    case None        => Right(Vec.empty)
    case Some(value) => callParameters(value)
  }

  private def callParameters(operand: Operand): Either[Error, Vec[NonEmpty[String]]] = operand match {
    case Operand.Symbol(name)         => Right(Vec(name))
    case Operand.Parenthesized(items) =>
      items.foldLeft[Either[Error, Vec[NonEmpty[String]]]](Right(Vec.empty)) { (current, item) =>
        for {
          names <- current
          value <- callParameters(item)
        } yield names ++ value
      }
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[Error, Vec[NonEmpty[String]]]](Right(Vec.empty)) { (current, item) =>
        for {
          names <- current
          value <- callParameters(item)
        } yield names ++ value
      }
    case other => invalid(s"expected call parameter names, found $other")
  }

  private def functionValue(
      operand: Operand,
      functionNames: Set[NonEmpty[String]],
      promoteUnknown: Boolean
  ): Either[Error, Value] = operand match {
    case Operand.Symbol(name)
        if promoteUnknown || functionNames.contains(name) =>
      Right(Value.Function(name))
    case Operand.Symbol(name) => Right(Value.Symbol(name))
    case other                => value(other)
  }

  private def sharedSymbols(body: Vec[Statement]): Map[NonEmpty[String], Long] = {
    val declarations = flatten(body).collect {
      case Statement.Data(DataAddressSpace.Shared, _, _, name, _, _) => Iterator.single(name)
      case Statement.Directive(path, arguments, _, _)
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
    case other                        => invalid(s"expected address operand, found $other")
  }

  private def parameterAddress(
      operand: Operand
  ): Either[Error, (NonEmpty[String], Int >= 0)] = operand match {
    case Operand.Address(Vec(Operand.Symbol(name))) =>
      Right(name -> 0)
    case Operand.Address(Vec(Operand.Add(Operand.Symbol(name), offset))) =>
      immediate(offset).flatMap(value =>
        if (value >= 0L && value <= Int.MaxValue.toLong)
          Right(name -> value.toInt.unsafe[Int >= 0])
        else invalid(s"parameter offset is outside Int range: $value")
      )
    case other => invalid(s"expected parameter address, found $other")
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

  private def integerWidth(modifiers: Vec[Modifier]): Either[Error, Int where (Between[8, 64] & MultipleOf[8])] =
    scalarBitWidth(modifiers, "integer type")

  private def floatWidth(modifiers: Vec[Modifier]): Either[Error, Int in (16, 32, 64)] =
    modifiers.reverse.view.collectFirst {
      case value if Set(Modifier.F16, Modifier.F32, Modifier.F64)(value) => value.bitWidth
    }.toRight(Error.Invalid(
      s"expected an f16, f32, or f64 type in ${modifiers.map(_.name.raw).mkString(".")}"
    ))
      .flatMap(width =>
        width.refine[Int in (16, 32, 64)].leftMap(message => Error.Invalid(message))
      )

  private def scalarWidth(modifiers: Vec[Modifier]): Either[Error, Int where (Between[8, 64] & MultipleOf[8])] =
    scalarBitWidth(modifiers, "scalar type")

  private def scalarBitWidth(
      modifiers: Vec[Modifier],
      describe: String
  ): Either[Error, Int where (Between[8, 64] & MultipleOf[8])] =
    modifiers.reverse.view.collectFirst {
      case value if value.bitWidth > 0 => value.bitWidth
    }.filter(Set(8, 16, 32, 64).contains)
      .toRight(Error.Invalid(
        s"expected an 8, 16, 32, or 64-bit $describe in ${modifiers.map(_.name.raw).mkString(".")}"
      ))
      .flatMap(width =>
        width.refine[Int where (Between[8, 64] & MultipleOf[8])].leftMap(message => Error.Invalid(message))
      )

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
      Right(Value.Register(name))
    case Operand.Symbol(name)     => Right(Value.Symbol(name))
    case other: Operand.Immediate => immediate(other).map(Value.Immediate(_))
    case Operand.Negate(value)    => immediate(value).map(number => Value.Immediate(-number))
    case other                    => invalid(s"expected register or immediate, found $other")
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
    case other => invalid(s"expected integer immediate, found $other")
  }

  private def isBareRegisterName(name: String): Boolean =
    name == "lo" || name == "hi" || Array("r", "rd", "p").exists(prefix =>
      name.startsWith(prefix) && name.drop(prefix.length).forall(_.isDigit)
    )

  private def invalid[A](detail: NonEmpty[String]): Either[Error, A] = Left(Error.Invalid(detail))
}
