package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as _, *}
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
  ): VMRegisterFile.ChunkCount = {
    var highest                                   = -1
    def inspectName(name: NonEmpty[String]): Unit = {
      val index = VMRegisterFile.indexRaw(name.raw)
      if (index >= 0) highest = math.max(highest, index)
    }
    @annotation.tailrec
    def inspectValue(value: Value): Unit = value match {
      case Value.Register(name)       => inspectName(name)
      case Value.RegisterSlot(index)  => highest = math.max(highest, index.raw)
      case Value.Predicate(name)      => inspectName(name)
      case Value.PredicateSlot(_)     => ()
      case Value.Function(name)       => inspectName(name)
      case Value.Symbol(name)         => inspectName(name)
      case Value.Immediate(_)         => ()
      case Value.Address(base, _)     => inspectValue(base)
      case Value.LaneId               => ()
      case Value.ActiveMask           => ()
      case Value.LaneMaskGreaterEqual => ()
      case Value.LaneMaskGreater      => ()
      case Value.LaneMaskLessEqual    => ()
      case Value.LaneMaskLess         => ()
      case Value.LaneMaskEqual        => ()
      case Value.ThreadId(_)          => ()
      case Value.BlockId(_)           => ()
      case Value.BlockDim(_)          => ()
      case Value.GridDim(_)           => ()
    }
    def inspectInstruction(instruction: PredicatedInstruction): Unit = {
      instruction.predication.foreach(predicate => inspectName(predicate.name))
      instruction.instruction match {
        case VMIR.Instruction.Tensor(operation, _) => operation match {
            case VMTensor.Operation.Load(_, destinations, address, stride, _, _) =>
              destinations.foreach(inspectName(_))
              inspectValue(address)
              inspectValue(stride)
            case VMTensor.Operation.Store(_, sources, address, stride, _, _) =>
              sources.foreach(inspectValue(_))
              inspectValue(address)
              inspectValue(stride)
            case VMTensor.Operation.Multiply(_, _, _, _, destinations, left, right, addend, _) =>
              destinations.foreach(inspectName(_))
              left.foreach(inspectValue(_))
              right.foreach(inspectValue(_))
              addend.foreach(inspectValue(_))
            case VMTensor.Operation.LoadMatrix(destinations, address, _, _) =>
              destinations.foreach(inspectName(_))
              inspectValue(address)
            case VMTensor.Operation.StoreMatrix(sources, address, _, _) =>
              sources.foreach(inspectValue(_))
              inspectValue(address)
          }
        case VMIR.Instruction.ConvertTF32(destination, source) =>
          inspectName(destination)
          inspectValue(source)
        case VMIR.Instruction.LoadParameter(destinations, _, _, _, _)           => destinations.foreach(inspectName(_))
        case VMIR.Instruction.StoreParameter(_, _, source, _)                   => inspectValue(source)
        case VMIR.Instruction.LoadParameterIndirect(destination, address, _, _) =>
          inspectName(destination)
          inspectValue(address)
        case VMIR.Instruction.LoadParameterIndirectVector(destinations, address, _, _) =>
          destinations.foreach(inspectName(_))
          inspectValue(address)
        case VMIR.Instruction.Move(destination, source, _) =>
          inspectName(destination)
          inspectValue(source)
        case VMIR.Instruction.MovePredicate(destination, source) =>
          inspectName(destination)
          inspectValue(source)
        case VMIR.Instruction.MoveSplit(destinations, source, _) =>
          destinations.foreach(inspectName(_))
          inspectValue(source)
        case VMIR.Instruction.MoveCombine(destination, sources, _) =>
          inspectName(destination)
          sources.foreach(inspectValue(_))
        case VMIR.Instruction.IntegerUnary(_, destination, source, _) =>
          inspectName(destination)
          inspectValue(source)
        case VMIR.Instruction.BitFind(destination, source, _, _, _) =>
          inspectName(destination)
          inspectValue(source)
        case VMIR.Instruction.DotProduct4(destination, left, right, addend, _, _) =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
          inspectValue(addend)
        case VMIR.Instruction.IntegerBinary(_, destination, left, right, _) =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
        case VMIR.Instruction.FunnelShift(destination, low, high, amount, _, _) =>
          inspectName(destination)
          inspectValue(low)
          inspectValue(high)
          inspectValue(amount)
        case VMIR.Instruction.IntegerAddWithCarry(destination, left, right, _, _, _) =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
        case VMIR.Instruction.IntegerSubWithBorrow(destination, left, right, _, _, _) =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
        case VMIR.Instruction.IntegerAddThree(destination, first, second, third, _, _, _, _, _) =>
          inspectName(destination)
          inspectValue(first)
          inspectValue(second)
          inspectValue(third)
        case VMIR.Instruction.WriteConditionFlag(_, value)         => inspectValue(value)
        case VMIR.Instruction.ReadConditionFlag(destination, _, _) => inspectName(destination)
        case VMIR.Instruction.InvalidateConditionFlag(_) | VMIR.Instruction.InvalidateAddThreeCarry => ()
        case VMIR.Instruction.MultiplyAdd(destination, left, right, addend, _)                      =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
          inspectValue(addend)
        case VMIR.Instruction.FloatBinary(_, destination, left, right, _, _) =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
        case VMIR.Instruction.FloatBinaryPacked(_, destination, left, right, _) =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
        case VMIR.Instruction.FloatUnary(_, destination, source, _, _) =>
          inspectName(destination)
          inspectValue(source)
        case VMIR.Instruction.FusedMultiplyAdd(destination, left, right, addend, _, _, _) =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
          inspectValue(addend)
        case VMIR.Instruction.FusedMultiplyAddBf16(destination, left, right, addend) =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
          inspectValue(addend)
        case VMIR.Instruction.FusedMultiplyAddPacked(destination, left, right, addend, _, _) =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
          inspectValue(addend)
        case VMIR.Instruction.ShuffleDown(destination, validity, source, delta, clamp, memberMask, _) =>
          inspectName(destination)
          validity.foreach(inspectName(_))
          inspectValue(source)
          inspectValue(delta)
          inspectValue(clamp)
          inspectValue(memberMask)
        case VMIR.Instruction.ShuffleUp(destination, validity, source, delta, clamp, memberMask, _) =>
          inspectName(destination)
          validity.foreach(inspectName(_))
          inspectValue(source)
          inspectValue(delta)
          inspectValue(clamp)
          inspectValue(memberMask)
        case VMIR.Instruction.ShuffleIndex(destination, validity, source, index, clamp, memberMask, _) =>
          inspectName(destination)
          validity.foreach(inspectName(_))
          inspectValue(source)
          inspectValue(index)
          inspectValue(clamp)
          inspectValue(memberMask)
        case VMIR.Instruction.ShuffleBfly(destination, validity, source, index, clamp, memberMask, _) =>
          inspectName(destination)
          validity.foreach(inspectName(_))
          inspectValue(source)
          inspectValue(index)
          inspectValue(clamp)
          inspectValue(memberMask)
        case VMIR.Instruction.VoteBallot(destination, predicate, memberMask) =>
          inspectName(destination)
          inspectName(predicate)
          inspectValue(memberMask)
        case VMIR.Instruction.VotePredicate(destination, predicate, memberMask, _) =>
          inspectName(destination)
          inspectName(predicate)
          inspectValue(memberMask)
        case VMIR.Instruction.BitFieldExtract(destination, source, start, widthValue, _, _) =>
          inspectName(destination)
          inspectValue(source)
          inspectValue(start)
          inspectValue(widthValue)
        case VMIR.Instruction.BitFieldInsert(destination, insertValue, baseValue, bitPosition, bitLength, _) =>
          inspectName(destination)
          inspectValue(insertValue)
          inspectValue(baseValue)
          inspectValue(bitPosition)
          inspectValue(bitLength)
        case VMIR.Instruction.BitMask(destination, start, widthValue, _) =>
          inspectName(destination)
          inspectValue(start)
          inspectValue(widthValue)
        case VMIR.Instruction.BytePermute(destination, left, right, control, _) =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
          inspectValue(control)
        case VMIR.Instruction.Select(destination, whenTrue, whenFalse, predicate, _) =>
          inspectName(destination)
          inspectValue(whenTrue)
          inspectValue(whenFalse)
          inspectName(predicate)
        case VMIR.Instruction.Convert(destination, source, _, _, _) =>
          inspectName(destination)
          inspectValue(source)
        case VMIR.Instruction.Load(destination, address, _, _, _) =>
          inspectName(destination)
          inspectValue(address)
        case VMIR.Instruction.LoadVector(destinations, address, _, _, _) =>
          destinations.foreach(inspectName(_))
          inspectValue(address)
        case VMIR.Instruction.Store(address, source, _, _) =>
          inspectValue(address)
          inspectValue(source)
        case VMIR.Instruction.StoreVector(address, sources, _, _) =>
          inspectValue(address)
          sources.foreach(inspectValue(_))
        case VMIR.Instruction.AsyncCopy(destination, source, _) =>
          inspectValue(destination)
          inspectValue(source)
        case VMIR.Instruction.AsyncCommit | VMIR.Instruction.AsyncWait | VMIR.Instruction.Prefetch => ()
        case VMIR.Instruction.SetPredicate(destination, _, _, left, right, _)                      =>
          inspectName(destination)
          inspectValue(left)
          inspectValue(right)
        case VMIR.Instruction.PredicateCompare(destination, _, left, right) =>
          inspectName(destination)
          inspectName(left)
          inspectName(right)
        case VMIR.Instruction.PredicateUnary(destination, source) =>
          inspectName(destination)
          inspectName(source)
        case VMIR.Instruction.PredicateBinary(_, destination, left, right) =>
          inspectName(destination)
          inspectName(left)
          inspectName(right)
        case VMIR.Instruction.Branch(_) | VMIR.Instruction.BarrierSync(_) => ()
        case VMIR.Instruction.BarrierReduce(destination, _, predicate, _) =>
          inspectName(destination)
          inspectName(predicate)
        case VMIR.Instruction.AtomicAdd(destination, address, source, _, _) =>
          inspectName(destination)
          inspectValue(address)
          inspectValue(source)
        case VMIR.Instruction.AtomicCompareExchange(destination, address, comparator, source, _, _) =>
          inspectName(destination)
          inspectValue(address)
          inspectValue(comparator)
          inspectValue(source)
        case VMIR.Instruction.AtomicExchange(destination, address, source, _, _) =>
          inspectName(destination)
          inspectValue(address)
          inspectValue(source)
        case VMIR.Instruction.AtomicBinary(destination, address, source, _, _, _) =>
          inspectName(destination)
          inspectValue(address)
          inspectValue(source)
        case VMIR.Instruction.AtomicAddFloat(destination, address, source, _) =>
          inspectName(destination)
          inspectValue(address)
          inspectValue(source)
        case VMIR.Instruction.WarpReduce(destination, source, memberMask, _, _) =>
          inspectName(destination)
          inspectValue(source)
          inspectValue(memberMask)
        case VMIR.Instruction.MemoryBarrier | VMIR.Instruction.Convergence(_, _) | VMIR.Instruction.SASSReturnPush(_) =>
          ()
        case VMIR.Instruction.SASSReturnPop(destination)                   => inspectName(destination)
        case VMIR.Instruction.WarpBarrier(memberMask)                      => inspectValue(memberMask)
        case VMIR.Instruction.Call(function, returnParameters, parameters) =>
          inspectValue(function)
          returnParameters.foreach(inspectName(_))
          parameters.foreach(inspectName(_))
        case VMIR.Instruction.Trap | VMIR.Instruction.Return => ()
      }
    }
    def inspectProgram(value: ExecutableProgram[PredicatedInstruction]): Unit =
      value.instructions.foreach(inspectInstruction(_))

    inspectProgram(program)
    functions.values.foreach(function => inspectProgram(function.program))
    if (highest < 0) 1 else ((highest >>> 6) + 1).unsafe[VMRegisterFile.ChunkCount]
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

    def enqueueFunctionReferences(value: ExecutableProgram[PredicatedInstruction]): Unit = {
      val addressTaken                              = Vec.newBuilder[NonEmpty[String]]
      def enqueue(function: NonEmpty[String]): Unit =
        if (!lowered.contains(function) && !pending.contains(function)) pending.enqueue(function)
      value.instructions.foreach {
        case PredicatedInstruction(VMIR.Instruction.Call(Value.Function(function), _, _), _) => enqueue(function)
        case PredicatedInstruction(VMIR.Instruction.Move(_, Value.Function(function), _), _) => addressTaken += function
        case _                                                                               => ()
      }
      addressTaken.result().foreach(enqueue(_))
    }

    enqueueFunctionReferences(program)
    while (pending.nonEmpty) {
      val name = pending.dequeue()
      functionDefinitions.get(name) match {
        case Some(function) if function.body.nonEmpty =>
          lowerFunction(function, functionNames) match {
            case Left(error)  => return Left(error)
            case Right(value) =>
              lowered(name) = value
              enqueueFunctionReferences(value.program)
          }
        case Some(_) => ()
        case None    => entryDefinitions.get(name) match {
            case Some(entry) =>
              lowerEntryFunction(entry, functionNames) match {
                case Left(error)  => return Left(error)
                case Right(value) =>
                  lowered(name) = value
                  enqueueFunctionReferences(value.program)
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
    val labels                     = mutable.Map.empty[NonEmpty[String], Pc]
    var instructionIndex: Int >= 0 = 0
    var statementIndex             = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Label(label) =>
          if (labels.contains(label))
            return Left(Error.Invalid(s"duplicate label: $label"))
          labels(label) = Pc(instructionIndex)
        case Statement.Instruction(_)                                       => instructionIndex += 1
        case Statement.UnsupportedInstruction(opcode, operands, mods, pred) =>
          val qualified                  = (opcode +: mods.map(_.name.raw)).mkString(".")
          val rendered: NonEmpty[String] = pred.map(value => s"$value ").getOrElse("") +
            qualified + (if (operands.isEmpty) "" else operands.mkString(" ", ", ", "")) + ";"
          return Left(
            Error.Unsupported(rendered, "instruction is not implemented")
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
                  Error.Invalid(s"${instruction.qualifiedOpcode}: $detail")
                case Error.Unsupported(_, detail) =>
                  Error.Unsupported(instruction.qualifiedOpcode, detail)
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
      val parameter      = parameters(index)
      val width: Int > 0 = parameter.elementType.byteCount.widen[Int > 0]
      val elements       = parameter.dimensions.foldLeft[Either[Error, Long > 0]](Right(1L)) {
        (current, value) =>
          for {
            count   <- current
            element <- value.toRight(
              Error.Invalid(s"kernel parameter ${parameter.name} has an unsized dimension")
            ).flatMap(immediate(_))
            elementCount <- element.refine[Long > 0].left.map(_ =>
              Error.Invalid(s"kernel parameter ${parameter.name} has an invalid array dimension: $element")
            )
            product <-
              if (count.raw <= Int.MaxValue.toLong / elementCount.raw)
                Right(count * elementCount)
              else invalid(s"kernel parameter ${parameter.name} has an invalid array dimension: $element")
          } yield product
      }
      elements match {
        case Left(error)  => return Left(error)
        case Right(count) =>
          val totalWidth = width.toLong.widen[Long > 0] * count
          if (totalWidth.raw > Int.MaxValue.toLong)
            return invalid(
              s"kernel parameter ${parameter.name} is too wide: $totalWidth bytes"
            )
          val pointer = parameter.qualifiers.exists {
            case DeclarationQualifier.Attribute(name) => name.raw == "ptr"
            case _                                    => false
          }
          result += KernelParameter(parameter.name, totalWidth.toIntExact, pointer)
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
      case Operand.Register(name, _)    => Vec(name).view
      case Operand.Symbol(name)         => Vec(name).view
      case Operand.VectorLiteral(items) => items.view.flatMap(registerNames(_))
      case Operand.Parenthesized(items) => items.view.flatMap(registerNames(_))
      case Operand.Qualified(base, _)   => registerNames(base)
      case _                            => Vec.empty[NonEmpty[String]].view
    }

    def declaredRegisters(body: Vec[Statement]): Vec[NonEmpty[String]] =
      Vec.toVec(body.view.flatMap {
        case Statement.Directive(path, arguments, _, _)
            if path.head.raw == "reg" =>
          arguments.view.flatMap(argument => registerNames(argument.operand))
        case _ => Vec.empty[NonEmpty[String]].view
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

  // The native host CAS path provides sequential consistency, satisfying these
  // weaker PTX atomic orders. Keep unsupported qualifier combinations explicit.
  private def atomicModifiers(modifiers: Vec[Modifier]): Vec[Modifier] = {
    val scopes = Set(Modifier.Cta, Modifier.Gpu, Modifier.Sys)
    val orders = Set(Modifier.Relaxed, Modifier.Acquire, Modifier.Release, Modifier.AcqRel)
    modifiers match {
      case Vec(scope, Modifier.Global, rest*) if scopes(scope)                         => Modifier.Global +: rest.toVec
      case Vec(order, scope, Modifier.Global, rest*) if orders(order) && scopes(scope) =>
        Modifier.Global +: rest.toVec
      case _ => modifiers
    }
  }

  private def atomicSpace(modifiers: Vec[Modifier]): VMIR.AtomicSpace =
    if (modifiers.contains(Modifier.Global)) VMIR.AtomicSpace.Global
    else if (modifiers.contains(Modifier.Shared)) VMIR.AtomicSpace.Shared
    else VMIR.AtomicSpace.Generic

  private def atomicIntegerOperands(
      destination: Operand,
      address: Operand,
      source: Operand,
      modifiers: Vec[Modifier]
  ): Either[Error, (NonEmpty[String], Value, Value, Int where (Between[8, 64] & MultipleOf[8]), VMIR.AtomicSpace)] =
    for {
      destinationName <- registerName(destination)
      addressValue    <- addressValue(address)
      sourceValue     <- value(source)
    } yield (
      destinationName,
      addressValue,
      sourceValue,
      if (modifiers.exists(Set(Modifier.B64, Modifier.S64, Modifier.U64).contains)) 64: Int where
        (Between[8, 64] & MultipleOf[8])
      else 32: Int where (Between[8, 64] & MultipleOf[8]),
      atomicSpace(modifiers)
    )

  private def allModifiers(instruction: Instruction): Vec[Modifier] = instruction.signature.modifiers

  private def hasModifier(instruction: Instruction, modifier: Modifier): Boolean =
    allModifiers(instruction).contains(modifier)

  private def hasAnyModifier(instruction: Instruction, modifiers: Set[Modifier]): Boolean =
    allModifiers(instruction).exists(modifiers.contains)

  private def endsWithModifier(instruction: Instruction, modifier: Modifier): Boolean =
    allModifiers(instruction).lastOption.contains(modifier)

  private def modifiersStartWith(instruction: Instruction, modifiers: Modifier*): Boolean = {
    val actual = allModifiers(instruction)
    actual.take(modifiers.size) == Vec.from(modifiers)
  }

  private def startsWithAnyModifier(instruction: Instruction, modifiers: Set[Modifier]): Boolean =
    allModifiers(instruction).headOption.exists(modifiers.contains)

  private def modifiersAre(instruction: Instruction, modifiers: Modifier*): Boolean =
    allModifiers(instruction) == Vec.from(modifiers)

  private val signedScalarModifiers: Set[Modifier] = Set(Modifier.S8, Modifier.S16, Modifier.S32, Modifier.S64)
  private val floatScalarModifiers: Set[Modifier]  = Set(Modifier.F16, Modifier.F32, Modifier.F64, Modifier.Bf16)

  private def bytePermuteMode(modifier: Modifier): Option[BytePermuteMode] = modifier match {
    case Modifier.F4e  => Some(BytePermuteMode.F4E)
    case Modifier.B4e  => Some(BytePermuteMode.B4E)
    case Modifier.Rc8  => Some(BytePermuteMode.RC8)
    case Modifier.Ecl  => Some(BytePermuteMode.ECL)
    case Modifier.Ecr  => Some(BytePermuteMode.ECR)
    case Modifier.Rc16 => Some(BytePermuteMode.RC16)
    case _             => None
  }

  private def lowerInstruction(
      instruction: Instruction,
      labels: Map[NonEmpty[String], Pc],
      functionNames: Set[NonEmpty[String]]
  ): Either[Error, PredicatedInstruction] = {
    lazy val lowered = instruction match {
      case Instruction.Cvt(destination, Some(source), None, None, None, None, _, _)
          if modifiersAre(instruction, Modifier.Rna, Modifier.Tf32, Modifier.F32) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.ConvertTF32(destinationName, sourceValue)
      case _: Instruction.WmmaLoad | _: Instruction.WmmaStore | _: Instruction.WmmaMma |
          _: Instruction.Mma | _: Instruction.Ldmatrix | _: Instruction.Stmatrix =>
        VMTensorCompiler.lower(instruction)
      case Instruction.Ld(destination, address, None, None, _, _)
          if modifiersStartWith(instruction, Modifier.Param) && isDirectParameterAddress(address) =>
        for {
          destinationNames   <- registerNames(destination)
          parameterAndOffset <- parameterAddress(address)
          width              <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.LoadParameter(
          destinationNames,
          parameterAndOffset._1,
          parameterAndOffset._2,
          width,
          hasAnyModifier(instruction, signedScalarModifiers)
        )
      case Instruction.St(address, source, None, _, _)
          if modifiersStartWith(instruction, Modifier.Param) =>
        for {
          parameterAndOffset <- parameterAddress(address)
          sourceValue        <- value(source)
          width              <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.StoreParameter(
          parameterAndOffset._1,
          parameterAndOffset._2,
          sourceValue,
          width
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if modifiersStartWith(instruction, Modifier.Param) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(allModifiers(instruction))
        } yield
          if (Operand.isVectorLiteral(destination))
            VMIR.Instruction.LoadParameterIndirectVector(
              destinationNames,
              addressValue,
              width,
              hasAnyModifier(instruction, signedScalarModifiers)
            )
          else
            VMIR.Instruction.LoadParameterIndirect(
              destinationNames.head,
              addressValue,
              width,
              hasAnyModifier(instruction, signedScalarModifiers)
            )
      case Instruction.Mov(destination, source, _, _)
          if modifiersAre(instruction, Modifier.Pred) =>
        for {
          destinationName <- predicateRegisterName(destination)
          sourceValue     <- predicateValue(source)
        } yield VMIR.Instruction.MovePredicate(destinationName, sourceValue)
      case Instruction.Activemask(destination, _, _) =>
        registerName(destination).map(destinationName =>
          VMIR.Instruction.Move(destinationName, Value.ActiveMask, 32: Int where (Between[8, 64] & MultipleOf[8]))
        )
      case Instruction.Mov(destination, source, _, _)
          if (modifiersAre(instruction, Modifier.U32) || modifiersAre(instruction, Modifier.S32) ||
            modifiersAre(instruction, Modifier.B32)) &&
            !Operand.isVectorLiteral(destination) &&
            !Operand.isVectorLiteral(source) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 32: Int where (Between[8, 64] & MultipleOf[8]))
      case Instruction.Mov(destination, source, _, _)
          if modifiersAre(instruction, Modifier.U8) || modifiersAre(instruction, Modifier.S8) ||
            modifiersAre(instruction, Modifier.B8) || modifiersAre(instruction, Modifier.U16) ||
            modifiersAre(instruction, Modifier.S16) || modifiersAre(instruction, Modifier.B16) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Move(destinationName, sourceValue, width)
      case Instruction.Mov(destination, source, _, _)
          if (modifiersAre(instruction, Modifier.U64) || modifiersAre(instruction, Modifier.B64)) &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- splitRegisterNames(destination)
          sourceValue      <- value(source)
          _                <- requirePair(destinationNames, "mov.b64 destination")
        } yield VMIR.Instruction.MoveSplit(destinationNames, sourceValue, 32: Int in (16, 32))
      case Instruction.Mov(destination, source, _, _)
          if modifiersAre(instruction, Modifier.B32) && Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- splitRegisterNames(destination)
          sourceValue      <- value(source)
          _                <- requirePair(destinationNames, "mov.b32 destination")
        } yield VMIR.Instruction.MoveSplit(destinationNames, sourceValue, 16: Int in (16, 32))
      case Instruction.Mov(destination, source, _, _)
          if (modifiersAre(instruction, Modifier.U64) || modifiersAre(instruction, Modifier.B64)) &&
            Operand.isVectorLiteral(source) =>
        for {
          destinationName <- registerName(destination)
          sourceValues    <- vectorValues(source)
          _               <- requirePair(sourceValues, "mov.b64 source")
        } yield VMIR.Instruction.MoveCombine(destinationName, sourceValues, 32: Int in (16, 32))
      case Instruction.Mov(destination, source, _, _)
          if modifiersAre(instruction, Modifier.B32) && Operand.isVectorLiteral(source) =>
        for {
          destinationName <- registerName(destination)
          sourceValues    <- vectorValues(source)
          _               <- requirePair(sourceValues, "mov.b32 source")
        } yield VMIR.Instruction.MoveCombine(destinationName, sourceValues, 16: Int in (16, 32))
      case Instruction.Mov(destination, source, _, _)
          if modifiersAre(instruction, Modifier.U64) || modifiersAre(instruction, Modifier.B64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- functionValue(source, functionNames, promoteUnknown = false)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64: Int where (Between[8, 64] & MultipleOf[8]))
      case Instruction.Mov(destination, source, _, _)
          if modifiersAre(instruction, Modifier.F32) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 32: Int where (Between[8, 64] & MultipleOf[8]))
      case Instruction.Mov(destination, source, _, _)
          if modifiersAre(instruction, Modifier.F64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64: Int where (Between[8, 64] & MultipleOf[8]))
      case Instruction.Neg(destination, source, _, _) if !hasAnyModifier(instruction, floatScalarModifiers) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.IntegerUnary(IntegerUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Neg(destination, source, _, _) if hasAnyModifier(instruction, floatScalarModifiers) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- floatWidth(allModifiers(instruction))
        } yield VMIR.Instruction.FloatUnary(
          FloatUnaryOperation.Negate,
          destinationName,
          sourceValue,
          width,
          floatControl(allModifiers(instruction))
        )
      case Instruction.Abs(destination, source, _, _)
          if hasAnyModifier(instruction, floatScalarModifiers) =>
        floatUnary(FloatUnaryOperation.Absolute, destination, source, allModifiers(instruction))
      case Instruction.Abs(destination, source, _, _) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.IntegerUnary(
          IntegerUnaryOperation.Absolute,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.RcpApproxFtzF64(destination, source, _, _) =>
        floatUnary(
          FloatUnaryOperation.Reciprocal,
          destination,
          source,
          allModifiers(instruction),
          Some(64: Int in (16, 32, 64))
        )
      case Instruction.Rcp(destination, source, _, _) =>
        floatUnary(FloatUnaryOperation.Reciprocal, destination, source, allModifiers(instruction))
      case Instruction.RsqrtApproxFtzF64(destination, source, _, _) =>
        floatUnary(
          FloatUnaryOperation.ReciprocalSquareRoot,
          destination,
          source,
          allModifiers(instruction),
          Some(64: Int in (16, 32, 64))
        )
      case Instruction.Rsqrt(destination, source, _, _) =>
        floatUnary(FloatUnaryOperation.ReciprocalSquareRoot, destination, source, allModifiers(instruction))
      case Instruction.Sqrt(destination, source, _, _) =>
        floatUnary(FloatUnaryOperation.SquareRoot, destination, source, allModifiers(instruction))
      case Instruction.Ex2(destination, source, _, _) =>
        floatUnary(FloatUnaryOperation.Exponent2, destination, source, allModifiers(instruction))
      case Instruction.Lg2(destination, source, _, _) =>
        floatUnary(FloatUnaryOperation.Log2, destination, source, allModifiers(instruction))
      case Instruction.Sin(destination, source, _, _) =>
        floatUnary(FloatUnaryOperation.Sine, destination, source, allModifiers(instruction))
      case Instruction.Cos(destination, source, _, _) =>
        floatUnary(FloatUnaryOperation.Cosine, destination, source, allModifiers(instruction))
      case Instruction.Tanh(destination, source, _, _) =>
        floatUnary(FloatUnaryOperation.Tanh, destination, source, allModifiers(instruction))
      case Instruction.Not(destination, source, _, _) =>
        if (modifiersAre(instruction, Modifier.Pred))
          for {
            destinationName <- predicateRegisterName(destination)
            sourceName      <- predicateRegisterName(source)
          } yield VMIR.Instruction.PredicateUnary(destinationName, sourceName)
        else
          for {
            destinationName <- registerName(destination)
            sourceValue     <- value(source)
            width           <- integerWidth(allModifiers(instruction))
          } yield VMIR.Instruction.IntegerUnary(
            IntegerUnaryOperation.BitwiseNot,
            destinationName,
            sourceValue,
            width
          )
      case Instruction.Popc(destination, source, _, _) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.IntegerUnary(
          IntegerUnaryOperation.PopulationCount,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.Clz(destination, source, _, _) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.IntegerUnary(
          IntegerUnaryOperation.LeadingZeroCount,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.Bfind(destination, source, _, _) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.BitFind(
          destinationName,
          sourceValue,
          width,
          hasAnyModifier(instruction, signedScalarModifiers),
          hasModifier(instruction, Modifier.Shiftamt)
        )
      case Instruction.Dp4a(destination, left, right, addend, modifiers, _) =>
        val types = modifiers.filter(Set(Modifier.S32, Modifier.U32).contains)
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
      case Instruction.Brev(destination, source, _, _) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.IntegerUnary(
          IntegerUnaryOperation.BitReverse,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.Mul(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.Wide, Modifier.U32) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned32,
          destinationName,
          leftValue,
          rightValue,
          64: Int where (Between[8, 64] & MultipleOf[8])
        )
      case Instruction.Mul(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.Wide, Modifier.S32) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideSigned32,
          destinationName,
          leftValue,
          rightValue,
          64: Int where (Between[8, 64] & MultipleOf[8])
        )
      case Instruction.Mul(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.Wide, Modifier.U16) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned16,
          destinationName,
          leftValue,
          rightValue,
          32: Int where (Between[8, 64] & MultipleOf[8])
        )
      case Instruction.Mul(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.Wide, Modifier.S16) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield VMIR.Instruction.IntegerBinary(
          IntegerOperation.MultiplyWideSigned16,
          destinationName,
          leftValue,
          rightValue,
          32: Int where (Between[8, 64] & MultipleOf[8])
        )
      case Instruction.Div(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.F16) ||
            endsWithModifier(instruction, Modifier.F32) ||
            endsWithModifier(instruction, Modifier.F64) =>
        floatBinary(FloatOperation.Divide, destination, left, right, allModifiers(instruction))
      case Instruction.Div(destination, left, right, _, _) =>
        integerWidth(allModifiers(instruction)).flatMap { width =>
          integerBinary(
            IntegerOperation.Divide(hasAnyModifier(instruction, signedScalarModifiers)),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Rem(destination, left, right, _, _) =>
        integerWidth(allModifiers(instruction)).flatMap { width =>
          integerBinary(
            IntegerOperation.Remainder(hasAnyModifier(instruction, signedScalarModifiers)),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Mul(destination, left, right, _, _)
          if modifiersStartWith(instruction, Modifier.Lo) =>
        integerWidth(allModifiers(instruction)).flatMap(integerBinary(
          IntegerOperation.MultiplyLow,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Mul(destination, left, right, _, _)
          if modifiersStartWith(instruction, Modifier.Hi) =>
        integerWidth(allModifiers(instruction)).flatMap { width =>
          integerBinary(
            IntegerOperation.MultiplyHigh(hasAnyModifier(instruction, signedScalarModifiers)),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Mul(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.F16x2) || endsWithModifier(instruction, Modifier.Bf16x2) =>
        packedFloatBinary(
          FloatOperation.Multiply,
          destination,
          left,
          right,
          endsWithModifier(instruction, Modifier.Bf16x2)
        )
      case Instruction.Mul(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.F16) ||
            endsWithModifier(instruction, Modifier.F32) ||
            endsWithModifier(instruction, Modifier.F64) =>
        floatBinary(FloatOperation.Multiply, destination, left, right, allModifiers(instruction))
      case Instruction.CopySign(destination, left, right, _, _) =>
        floatBinary(FloatOperation.CopySign, destination, left, right, allModifiers(instruction))
      case Instruction.Fma(destination, left, right, addend, _, _)
          if endsWithModifier(instruction, Modifier.F16x2) || endsWithModifier(instruction, Modifier.Bf16x2) =>
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
          endsWithModifier(instruction, Modifier.Bf16x2),
          saturate = allModifiers(instruction).contains(Modifier.Sat)
        )
      case Instruction.Fma(destination, left, right, addend, _, _)
          if endsWithModifier(instruction, Modifier.Bf16) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield VMIR.Instruction.FusedMultiplyAddBf16(destinationName, leftValue, rightValue, addendValue)
      case Instruction.Fma(destination, left, right, addend, _, _) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width           <- floatWidth(allModifiers(instruction))
        } yield VMIR.Instruction.FusedMultiplyAdd(
          destinationName,
          leftValue,
          rightValue,
          addendValue,
          width,
          floatControl(allModifiers(instruction)),
          allModifiers(instruction).view.flatMap(conversionRounding(_)).headOption.getOrElse(ConversionRounding.RN)
        )
      case Instruction.Shl(destination, left, right, _, _) if modifiersAre(instruction, Modifier.B32) =>
        integerBinary(
          IntegerOperation.ShiftLeft,
          32: Int where (Between[8, 64] & MultipleOf[8]),
          destination,
          left,
          right
        )
      case Instruction.Shl(destination, left, right, _, _) =>
        integerWidth(allModifiers(instruction)).flatMap(integerBinary(
          IntegerOperation.ShiftLeft,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Shr(destination, left, right, _, _)
          if startsWithAnyModifier(
            instruction,
            Set(
              Modifier.U8,
              Modifier.U16,
              Modifier.U32,
              Modifier.U64,
              Modifier.B8,
              Modifier.B16,
              Modifier.B32,
              Modifier.B64
            )
          ) =>
        integerWidth(allModifiers(instruction)).flatMap(integerBinary(
          IntegerOperation.ShiftRightLogical,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Shr(destination, left, right, _, _)
          if startsWithAnyModifier(instruction, signedScalarModifiers) =>
        integerWidth(allModifiers(instruction)).flatMap(integerBinary(
          IntegerOperation.ShiftRightArithmetic,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Shf(destination, low, high, amount, _, _)
          if endsWithModifier(instruction, Modifier.B32) =>
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
          modifiersStartWith(instruction, Modifier.L),
          hasModifier(instruction, Modifier.Clamp)
        )
      case Instruction.ShflSync(destination, source, delta, clamp, memberMask, _, _)
          if modifiersAre(instruction, Modifier.Sync, Modifier.Down, Modifier.B32) ||
            modifiersAre(instruction, Modifier.Sync, Modifier.Down, Modifier.B64) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          deltaValue      <- value(delta)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.ShuffleDown(
          destination = destinationInfo._1,
          validity = destinationInfo._2,
          source = sourceValue,
          delta = deltaValue,
          clamp = clampValue,
          memberMask = memberMaskValue,
          width = width
        )
      case Instruction.ShflSync(destination, source, delta, clamp, memberMask, _, _)
          if modifiersAre(instruction, Modifier.Sync, Modifier.Up, Modifier.B32) ||
            modifiersAre(instruction, Modifier.Sync, Modifier.Up, Modifier.B64) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          deltaValue      <- value(delta)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.ShuffleUp(
          destination = destinationInfo._1,
          validity = destinationInfo._2,
          source = sourceValue,
          delta = deltaValue,
          clamp = clampValue,
          memberMask = memberMaskValue,
          width = width
        )
      case Instruction.ShflSync(destination, source, index, clamp, memberMask, _, _)
          if modifiersAre(instruction, Modifier.Sync, Modifier.Idx, Modifier.B32) ||
            modifiersAre(instruction, Modifier.Sync, Modifier.Idx, Modifier.B64) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          indexValue      <- value(index)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.ShuffleIndex(
          destination = destinationInfo._1,
          validity = destinationInfo._2,
          source = sourceValue,
          index = indexValue,
          clamp = clampValue,
          memberMask = memberMaskValue,
          width = width
        )
      case Instruction.ShflSync(destination, source, index, clamp, memberMask, _, _)
          if modifiersAre(instruction, Modifier.Sync, Modifier.Bfly, Modifier.B32) ||
            modifiersAre(instruction, Modifier.Sync, Modifier.Bfly, Modifier.B64) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          indexValue      <- value(index)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(allModifiers(instruction))
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
          if modifiersAre(instruction, Modifier.Ballot, Modifier.B32) =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
        } yield VMIR.Instruction.VoteBallot(destinationName, predicateName, Value.Immediate(0xffffffffL))
      case Instruction.VoteSync(destination, Some(predicate), Some(memberMask), _, _)
          if modifiersAre(instruction, Modifier.Sync, Modifier.Ballot, Modifier.B32) =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
          memberMaskValue <- value(memberMask)
        } yield VMIR.Instruction.VoteBallot(destinationName, predicateName, memberMaskValue)
      case Instruction.VoteSync(destination, Some(predicate), Some(memberMask), _, _)
          if modifiersAre(instruction, Modifier.Sync, Modifier.All, Modifier.Pred) ||
            modifiersAre(instruction, Modifier.Sync, Modifier.Any, Modifier.Pred) ||
            modifiersAre(instruction, Modifier.Sync, Modifier.Uni, Modifier.Pred) =>
        for {
          destinationName <- predicateRegisterName(destination)
          predicateName   <- predicateRegisterName(predicate)
          memberMaskValue <- value(memberMask)
          operation = if (hasModifier(instruction, Modifier.All)) VoteOperation.All
          else if (hasModifier(instruction, Modifier.Any)) VoteOperation.Any
          else VoteOperation.Uniform
        } yield VMIR.Instruction.VotePredicate(destinationName, predicateName, memberMaskValue, operation)
      case Instruction.Bfe(destination, source, start, widthValue, _, _) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          startValue      <- value(start)
          widthOperand    <- value(widthValue)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.BitFieldExtract(
          destination = destinationName,
          source = sourceValue,
          start = startValue,
          widthValue = widthOperand,
          width = width,
          signed = hasAnyModifier(instruction, signedScalarModifiers)
        )
      case Instruction.Bfi(destination, insertValue, baseValue, bitPosition, bitLength, _, _) =>
        for {
          destinationName <- registerName(destination)
          insertOperand   <- value(insertValue)
          baseOperand     <- value(baseValue)
          positionOperand <- value(bitPosition)
          lengthOperand   <- value(bitLength)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.BitFieldInsert(
          destination = destinationName,
          insertValue = insertOperand,
          baseValue = baseOperand,
          bitPosition = positionOperand,
          bitLength = lengthOperand,
          width = width
        )
      case Instruction.Bmsk(destination, start, widthValue, _, _) =>
        for {
          destinationName <- registerName(destination)
          startValue      <- value(start)
          widthOperand    <- value(widthValue)
          fullModifiers = allModifiers(instruction)
          mode <- fullModifiers.view.collectFirst {
            case Modifier.Clamp => BitMaskMode.Clamp
            case Modifier.Wrap  => BitMaskMode.Wrap
          }
            .toRight(Error.Invalid("bmsk requires .clamp or .wrap"))
          _ <- Either.cond(
            fullModifiers.contains(Modifier.B32),
            (),
            Error.Invalid("bmsk requires a .b32 width")
          )
        } yield VMIR.Instruction.BitMask(destinationName, startValue, widthOperand, mode)
      case Instruction.Prmt(destination, left, right, control, _, _)
          if allModifiers(instruction).headOption.contains(Modifier.B32) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          controlValue    <- value(control)
          fullModifiers = allModifiers(instruction)
          mode <- fullModifiers.drop(1).headOption.fold(Option(BytePermuteMode.Default))(bytePermuteMode(_))
            .toRight(Error.Invalid(s"unsupported prmt mode: ${fullModifiers.map(_.name.raw).mkString(".")}"))
        } yield VMIR.Instruction.BytePermute(
          destinationName,
          leftValue,
          rightValue,
          controlValue,
          mode
        )
      case Instruction.Mad(destination, left, right, addend, _, _)
          if hasModifier(instruction, Modifier.Lo) &&
            (endsWithModifier(instruction, Modifier.S32) || endsWithModifier(instruction, Modifier.U32) ||
              endsWithModifier(instruction, Modifier.S64) || endsWithModifier(instruction, Modifier.U64)) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width = if (hasAnyModifier(instruction, Set(Modifier.B32, Modifier.U32, Modifier.S32))) 32: Int where
            (Between[8, 64] & MultipleOf[8])
          else 64: Int where (Between[8, 64] & MultipleOf[8])
        } yield VMIR.Instruction.MultiplyAdd(
          destination = destinationName,
          left = leftValue,
          right = rightValue,
          addend = addendValue,
          operation = IntegerMultiplyAddOperation.Low(
            operandWidth = width,
            resultWidth = width,
            carryIn = false,
            carryOut = hasModifier(instruction, Modifier.Cc)
          )
        )
      case Instruction.MadCc(destination, left, right, addend, _, _)
          if hasModifier(instruction, Modifier.Lo) &&
            (endsWithModifier(instruction, Modifier.S32) || endsWithModifier(instruction, Modifier.U32) ||
              endsWithModifier(instruction, Modifier.S64) || endsWithModifier(instruction, Modifier.U64)) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width = if (hasAnyModifier(instruction, Set(Modifier.B32, Modifier.U32, Modifier.S32))) 32: Int where
            (Between[8, 64] & MultipleOf[8])
          else 64: Int where (Between[8, 64] & MultipleOf[8])
        } yield VMIR.Instruction.MultiplyAdd(
          destination = destinationName,
          left = leftValue,
          right = rightValue,
          addend = addendValue,
          operation =
            IntegerMultiplyAddOperation.Low(operandWidth = width, resultWidth = width, carryIn = false, carryOut = true)
        )
      case Instruction.Mad(destination, left, right, addend, _, _)
          if hasModifier(instruction, Modifier.Wide) && endsWithModifier(instruction, Modifier.U32) =>
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
              operandWidth = 32: Int where (Between[8, 64] & MultipleOf[8]),
              resultWidth = 64: Int where (Between[8, 64] & MultipleOf[8]),
              carryIn = false,
              carryOut = false
            )
        )
      case Instruction.Madc(destination, left, right, addend, _, _)
          if (hasModifier(instruction, Modifier.Lo) || hasModifier(instruction, Modifier.Hi)) &&
            (endsWithModifier(instruction, Modifier.S32) || endsWithModifier(instruction, Modifier.U32) ||
              endsWithModifier(instruction, Modifier.S64) || endsWithModifier(instruction, Modifier.U64)) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width = if (hasAnyModifier(instruction, Set(Modifier.B32, Modifier.U32, Modifier.S32))) 32: Int where
            (Between[8, 64] & MultipleOf[8])
          else 64: Int where (Between[8, 64] & MultipleOf[8])
          operation = if (hasModifier(instruction, Modifier.Hi))
            IntegerMultiplyAddOperation.High(
              operandWidth = width,
              resultWidth = width,
              carryIn = true,
              carryOut = hasModifier(instruction, Modifier.Cc)
            )
          else IntegerMultiplyAddOperation.Low(
            operandWidth = width,
            resultWidth = width,
            carryIn = true,
            carryOut = hasModifier(instruction, Modifier.Cc)
          )
        } yield VMIR.Instruction.MultiplyAdd(
          destination = destinationName,
          left = leftValue,
          right = rightValue,
          addend = addendValue,
          operation = operation
        )
      case Instruction.AddCc(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.U32) || endsWithModifier(instruction, Modifier.S32) ||
            endsWithModifier(instruction, Modifier.U64) || endsWithModifier(instruction, Modifier.S64) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (hasAnyModifier(instruction, Set(Modifier.B32, Modifier.U32, Modifier.S32))) 32: Int where
            (Between[8, 64] & MultipleOf[8])
          else 64: Int where (Between[8, 64] & MultipleOf[8])
        } yield VMIR.Instruction.IntegerAddWithCarry(
          destinationName,
          leftValue,
          rightValue,
          width,
          carryIn = false,
          carryOut = true
        )
      case Instruction.Addc(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.U32) || endsWithModifier(instruction, Modifier.S32) ||
            endsWithModifier(instruction, Modifier.U64) || endsWithModifier(instruction, Modifier.S64) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (hasAnyModifier(instruction, Set(Modifier.B32, Modifier.U32, Modifier.S32))) 32: Int where
            (Between[8, 64] & MultipleOf[8])
          else 64: Int where (Between[8, 64] & MultipleOf[8])
        } yield VMIR.Instruction.IntegerAddWithCarry(
          destinationName,
          leftValue,
          rightValue,
          width,
          carryIn = true,
          carryOut = hasModifier(instruction, Modifier.Cc)
        )
      case Instruction.SubCc(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.U32) || endsWithModifier(instruction, Modifier.S32) ||
            endsWithModifier(instruction, Modifier.U64) || endsWithModifier(instruction, Modifier.S64) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (hasAnyModifier(instruction, Set(Modifier.B32, Modifier.U32, Modifier.S32))) 32: Int where
            (Between[8, 64] & MultipleOf[8])
          else 64: Int where (Between[8, 64] & MultipleOf[8])
        } yield VMIR.Instruction.IntegerSubWithBorrow(
          destinationName,
          leftValue,
          rightValue,
          width,
          borrowIn = false,
          borrowOut = true
        )
      case Instruction.Subc(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.U32) || endsWithModifier(instruction, Modifier.S32) ||
            endsWithModifier(instruction, Modifier.U64) || endsWithModifier(instruction, Modifier.S64) =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (hasAnyModifier(instruction, Set(Modifier.B32, Modifier.U32, Modifier.S32))) 32: Int where
            (Between[8, 64] & MultipleOf[8])
          else 64: Int where (Between[8, 64] & MultipleOf[8])
        } yield VMIR.Instruction.IntegerSubWithBorrow(
          destinationName,
          leftValue,
          rightValue,
          width,
          borrowIn = true,
          borrowOut = hasModifier(instruction, Modifier.Cc)
        )
      case Instruction.Add(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.U32) || modifiersAre(instruction, Modifier.S32) =>
        integerBinary(IntegerOperation.Add, 32: Int where (Between[8, 64] & MultipleOf[8]), destination, left, right)
      case Instruction.Add(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.U64) || modifiersAre(instruction, Modifier.S64) =>
        integerBinary(IntegerOperation.Add, 64: Int where (Between[8, 64] & MultipleOf[8]), destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.U32) || modifiersAre(instruction, Modifier.S32) =>
        integerBinary(
          IntegerOperation.Subtract,
          32: Int where (Between[8, 64] & MultipleOf[8]),
          destination,
          left,
          right
        )
      case Instruction.Sub(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.U64) || modifiersAre(instruction, Modifier.S64) =>
        integerBinary(
          IntegerOperation.Subtract,
          64: Int where (Between[8, 64] & MultipleOf[8]),
          destination,
          left,
          right
        )
      case Instruction.And(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.Pred) =>
        predicateBinary(PredicateOperation.And, destination, left, right)
      case Instruction.Or(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.Pred) =>
        predicateBinary(PredicateOperation.Or, destination, left, right)
      case Instruction.Xor(destination, left, right, _, _)
          if modifiersAre(instruction, Modifier.Pred) =>
        predicateBinary(PredicateOperation.Xor, destination, left, right)
      case Instruction.And(destination, left, right, _, _) =>
        integerWidth(allModifiers(instruction)).flatMap(integerBinary(
          IntegerOperation.BitwiseAnd,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Or(destination, left, right, _, _) =>
        integerWidth(allModifiers(instruction)).flatMap(integerBinary(
          IntegerOperation.BitwiseOr,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Xor(destination, left, right, _, _) =>
        integerWidth(allModifiers(instruction)).flatMap(integerBinary(
          IntegerOperation.BitwiseXor,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Add(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.F16x2) || endsWithModifier(instruction, Modifier.Bf16x2) =>
        packedFloatBinary(
          FloatOperation.Add,
          destination,
          left,
          right,
          endsWithModifier(instruction, Modifier.Bf16x2)
        )
      case Instruction.Add(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.F16) ||
            endsWithModifier(instruction, Modifier.F32) ||
            endsWithModifier(instruction, Modifier.F64) =>
        floatBinary(FloatOperation.Add, destination, left, right, allModifiers(instruction))
      case Instruction.Sub(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.F16x2) || endsWithModifier(instruction, Modifier.Bf16x2) =>
        packedFloatBinary(
          FloatOperation.Subtract,
          destination,
          left,
          right,
          endsWithModifier(instruction, Modifier.Bf16x2)
        )
      case Instruction.Sub(destination, left, right, _, _)
          if endsWithModifier(instruction, Modifier.F16) ||
            endsWithModifier(instruction, Modifier.F32) ||
            endsWithModifier(instruction, Modifier.F64) =>
        floatBinary(FloatOperation.Subtract, destination, left, right, allModifiers(instruction))
      case Instruction.Add(destination, left, right, _, _) =>
        integerWidth(allModifiers(instruction)).flatMap(integerBinary(
          IntegerOperation.Add,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Sub(destination, left, right, _, _) =>
        integerWidth(allModifiers(instruction)).flatMap(integerBinary(
          IntegerOperation.Subtract,
          _,
          destination,
          left,
          right
        ))
      case Instruction.Min(destination, left, right, _, _, _)
          if endsWithModifier(instruction, Modifier.F16x2) || endsWithModifier(instruction, Modifier.Bf16x2) =>
        packedFloatBinary(
          if (hasModifier(instruction, Modifier.NaN)) FloatOperation.Minimum else FloatOperation.MinimumNumber,
          destination,
          left,
          right,
          endsWithModifier(instruction, Modifier.Bf16x2)
        )
      case Instruction.Min(destination, left, right, _, _, _)
          if hasAnyModifier(instruction, floatScalarModifiers) =>
        floatBinary(
          if (hasModifier(instruction, Modifier.NaN)) FloatOperation.Minimum else FloatOperation.MinimumNumber,
          destination,
          left,
          right,
          allModifiers(instruction)
        )
      case Instruction.Max(destination, left, right, _, _, _)
          if endsWithModifier(instruction, Modifier.F16x2) || endsWithModifier(instruction, Modifier.Bf16x2) =>
        packedFloatBinary(
          if (hasModifier(instruction, Modifier.NaN)) FloatOperation.Maximum else FloatOperation.MaximumNumber,
          destination,
          left,
          right,
          endsWithModifier(instruction, Modifier.Bf16x2)
        )
      case Instruction.Max(destination, left, right, _, _, _)
          if hasAnyModifier(instruction, floatScalarModifiers) =>
        floatBinary(
          if (hasModifier(instruction, Modifier.NaN)) FloatOperation.Maximum else FloatOperation.MaximumNumber,
          destination,
          left,
          right,
          allModifiers(instruction)
        )
      case Instruction.Min(destination, left, right, _, _, _) =>
        integerWidth(allModifiers(instruction)).flatMap { width =>
          integerBinary(
            IntegerOperation.Minimum(hasAnyModifier(instruction, signedScalarModifiers)),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Max(destination, left, right, _, _, _) =>
        integerWidth(allModifiers(instruction)).flatMap { width =>
          integerBinary(
            IntegerOperation.Maximum(hasAnyModifier(instruction, signedScalarModifiers)),
            width,
            destination,
            left,
            right
          )
        }
      case Instruction.Cvta(destination, source, _, _)
          if modifiersAre(instruction, Modifier.Global, Modifier.U64) ||
            modifiersAre(instruction, Modifier.To, Modifier.Global, Modifier.U64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64: Int where (Between[8, 64] & MultipleOf[8]))
      case Instruction.Cvta(destination, source, _, _) if modifiersAre(instruction, Modifier.Local, Modifier.U64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64: Int where (Between[8, 64] & MultipleOf[8]))
      case Instruction.Cvta(destination, source, _, _)
          if modifiersAre(instruction, Modifier.To, Modifier.Local, Modifier.U64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64: Int where (Between[8, 64] & MultipleOf[8]))
      case Instruction.Cvta(destination, source, _, _)
          if modifiersAre(instruction, Modifier.Shared, Modifier.U64) ||
            modifiersAre(instruction, Modifier.To, Modifier.Shared, Modifier.U64) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield VMIR.Instruction.Move(destinationName, sourceValue, 64: Int where (Between[8, 64] & MultipleOf[8]))
      case Instruction.Cvt(destination, Some(source), None, None, None, None, modifiers, _) =>
        val types = modifiers.flatMap(conversionType(_))
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
            modifiers.view.flatMap(conversionRounding(_))
              .headOption
          )
      case Instruction.Selp(destination, whenTrue, whenFalse, predicate, _, _) =>
        for {
          destinationName <- registerName(destination)
          falseValue      <- value(whenFalse)
          trueValue       <- value(whenTrue)
          predicateName   <- predicateRegisterName(predicate)
          width           <- scalarWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Select(destinationName, trueValue, falseValue, predicateName, width)
      case Instruction.Ld(destination, address, None, None, _, _)
          if modifiersStartWith(instruction, Modifier.Relaxed, Modifier.Sys, Modifier.Global) &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.LoadVector(
          destinationNames,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Global(true)
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if modifiersStartWith(instruction, Modifier.Relaxed, Modifier.Sys) &&
            !hasModifier(instruction, Modifier.Global) && Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.LoadVector(
          destinationNames,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Generic(true)
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if allModifiers(instruction) match {
            case Vec(Modifier.Relaxed, Modifier.Sys, suffix) =>
              Set(
                Modifier.U8,
                Modifier.B8,
                Modifier.S8,
                Modifier.U16,
                Modifier.B16,
                Modifier.S16,
                Modifier.U32,
                Modifier.B32,
                Modifier.S32,
                Modifier.U64,
                Modifier.B64,
                Modifier.S64
              ).contains(suffix)
            case _ => false
          } =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Load(
          destinationName,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Generic(true)
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if allModifiers(instruction) match {
            case Vec(Modifier.Relaxed, Modifier.Sys, Modifier.Global, suffix) =>
              Set(
                Modifier.U8,
                Modifier.B8,
                Modifier.S8,
                Modifier.U16,
                Modifier.B16,
                Modifier.S16,
                Modifier.U32,
                Modifier.B32,
                Modifier.S32,
                Modifier.U64,
                Modifier.B64,
                Modifier.S64
              ).contains(suffix)
            case _ => false
          } =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Load(
          destinationName,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Global(true)
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if allModifiers(instruction) match {
            case Vec(Modifier.Acquire, scope, Modifier.Global, suffix) =>
              Set(Modifier.Cta, Modifier.Gpu, Modifier.Sys).contains(scope) &&
              Set(Modifier.U32, Modifier.B32, Modifier.U64, Modifier.B64).contains(suffix)
            case _ => false
          } =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.AtomicAdd(
          destinationName,
          addressValue,
          VMIR.Value.Immediate(0),
          width,
          VMIR.AtomicSpace.Global
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if (allModifiers(instruction) match {
            case Vec(Modifier.Relaxed, scope, suffix) =>
              Set(Modifier.Cta, Modifier.Gpu).contains(scope) &&
              Set(
                Modifier.U8,
                Modifier.B8,
                Modifier.S8,
                Modifier.U16,
                Modifier.B16,
                Modifier.S16,
                Modifier.U32,
                Modifier.B32,
                Modifier.S32,
                Modifier.U64,
                Modifier.B64,
                Modifier.S64
              ).contains(suffix)
            case _ => false
          }) &&
            !Operand.isVectorLiteral(destination) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Load(
          destinationName,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Generic(false)
        )
      case Instruction.St(address, source, None, _, _)
          if modifiersStartWith(instruction, Modifier.Relaxed, Modifier.Sys, Modifier.Global) &&
            Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.StoreVector(addressValue, sourceNames, width, MemoryAccess.Global(true))
      case Instruction.St(address, source, None, _, _)
          if modifiersStartWith(instruction, Modifier.Relaxed, Modifier.Sys) &&
            !hasModifier(instruction, Modifier.Global) && Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.StoreVector(addressValue, sourceNames, width, MemoryAccess.Generic(true))
      case Instruction.St(address, source, None, _, _)
          if allModifiers(instruction) match {
            case Vec(Modifier.Relaxed, Modifier.Sys, Modifier.Global, suffix) =>
              Set(
                Modifier.U8,
                Modifier.B8,
                Modifier.U16,
                Modifier.B16,
                Modifier.U32,
                Modifier.B32,
                Modifier.U64,
                Modifier.B64
              ).contains(suffix)
            case _ => false
          } =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Store(addressValue, sourceValue, width, MemoryAccess.Global(true))
      case Instruction.St(address, source, None, _, _)
          if allModifiers(instruction) match {
            case Vec(Modifier.Relaxed, Modifier.Sys, suffix) =>
              Set(
                Modifier.U8,
                Modifier.B8,
                Modifier.U16,
                Modifier.B16,
                Modifier.U32,
                Modifier.B32,
                Modifier.U64,
                Modifier.B64
              ).contains(suffix)
            case _ => false
          } =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Store(addressValue, sourceValue, width, MemoryAccess.Generic(true))
      case Instruction.St(address, source, None, _, _)
          if allModifiers(instruction) match {
            case Vec(Modifier.Release, scope, Modifier.Global, suffix) =>
              Set(Modifier.Cta, Modifier.Gpu, Modifier.Sys).contains(scope) &&
              Set(Modifier.U32, Modifier.B32, Modifier.U64, Modifier.B64).contains(suffix)
            case _ => false
          } =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.AtomicExchange(
          "_": NonEmpty[String],
          addressValue,
          sourceValue,
          width,
          VMIR.AtomicSpace.Global
        )
      case Instruction.St(address, source, None, _, _)
          if (allModifiers(instruction) match {
            case Vec(Modifier.Relaxed, scope, suffix) =>
              Set(Modifier.Cta, Modifier.Gpu).contains(scope) &&
              Set(
                Modifier.U8,
                Modifier.B8,
                Modifier.U16,
                Modifier.B16,
                Modifier.U32,
                Modifier.B32,
                Modifier.U64,
                Modifier.B64
              ).contains(suffix)
            case _ => false
          }) &&
            !Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Store(addressValue, sourceValue, width, MemoryAccess.Generic(false))
      case Instruction.Ld(destination, address, None, None, _, _)
          if (allModifiers(instruction).size == 1 && allModifiers(instruction).headOption.exists(Set(
            Modifier.Bf16,
            Modifier.B8,
            Modifier.B16,
            Modifier.B32,
            Modifier.B64,
            Modifier.U8,
            Modifier.U16,
            Modifier.U32,
            Modifier.U64,
            Modifier.S8,
            Modifier.S16,
            Modifier.S32,
            Modifier.S64,
            Modifier.F16,
            Modifier.F32,
            Modifier.F64
          ).contains)) &&
            !Operand.isVectorLiteral(destination) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Load(
          destinationName,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Generic(false)
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if !hasModifier(instruction, Modifier.Param) &&
            !hasModifier(instruction, Modifier.Shared) &&
            !hasModifier(instruction, Modifier.Local) &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.LoadVector(
          destinationNames,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Generic(false)
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if hasModifier(instruction, Modifier.Global) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Load(
          destinationName,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Global(false)
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if !hasModifier(instruction, Modifier.Param) &&
            !hasModifier(instruction, Modifier.Shared) &&
            !hasModifier(instruction, Modifier.Local) &&
            !hasModifier(instruction, Modifier.Global) &&
            !Operand.isVectorLiteral(destination) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Load(
          destinationName,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Global(false)
        )
      case Instruction.LdGlobalNc(destination, address, None, _, _)
          if modifiersStartWith(instruction, Modifier.Global, Modifier.Nc) &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.LoadVector(
          destinationNames,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Global(false)
        )
      case Instruction.LdGlobalNc(destination, address, None, _, _)
          if modifiersStartWith(instruction, Modifier.Global, Modifier.Nc) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Load(
          destinationName,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Global(false)
        )
      case Instruction.St(address, source, None, _, _)
          if (allModifiers(instruction).size == 1 && allModifiers(instruction).headOption.exists(Set(
            Modifier.Bf16,
            Modifier.B8,
            Modifier.B16,
            Modifier.B32,
            Modifier.B64,
            Modifier.U8,
            Modifier.U16,
            Modifier.U32,
            Modifier.U64,
            Modifier.S8,
            Modifier.S16,
            Modifier.S32,
            Modifier.S64,
            Modifier.F16,
            Modifier.F32,
            Modifier.F64
          ).contains)) &&
            !Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Store(addressValue, sourceValue, width, MemoryAccess.Generic(false))
      case Instruction.St(address, source, None, _, _)
          if !hasModifier(instruction, Modifier.Shared) &&
            !hasModifier(instruction, Modifier.Local) &&
            Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.StoreVector(addressValue, sourceNames, width, MemoryAccess.Generic(false))
      case Instruction.St(address, source, None, _, _)
          if !hasModifier(instruction, Modifier.Shared) &&
            !hasModifier(instruction, Modifier.Local) &&
            !Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Store(addressValue, sourceValue, width, MemoryAccess.Global(false))
      case Instruction.Ld(destination, address, None, None, _, _)
          if hasModifier(instruction, Modifier.Shared) &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.LoadVector(
          destinationNames,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Shared
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if hasModifier(instruction, Modifier.Shared) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Load(
          destinationName,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Shared
        )
      case Instruction.St(address, source, None, _, _)
          if hasModifier(instruction, Modifier.Shared) &&
            Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.StoreVector(addressValue, sourceNames, width, MemoryAccess.Shared)
      case Instruction.St(address, source, None, _, _)
          if hasModifier(instruction, Modifier.Shared) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Store(addressValue, sourceValue, width, MemoryAccess.Shared)
      case Instruction.Ld(destination, address, None, None, _, _)
          if hasModifier(instruction, Modifier.Local) &&
            Operand.isVectorLiteral(destination) =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.LoadVector(
          destinationNames,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Local
        )
      case Instruction.Ld(destination, address, None, None, _, _)
          if hasModifier(instruction, Modifier.Local) =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Load(
          destinationName,
          addressValue,
          width,
          allModifiers(instruction).lastOption.exists(signedScalarModifiers.contains),
          MemoryAccess.Local
        )
      case Instruction.St(address, source, None, _, _)
          if hasModifier(instruction, Modifier.Local) &&
            Operand.isVectorLiteral(source) =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- vectorValues(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.StoreVector(addressValue, sourceNames, width, MemoryAccess.Local)
      case Instruction.St(address, source, None, _, _)
          if hasModifier(instruction, Modifier.Local) =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(allModifiers(instruction))
        } yield VMIR.Instruction.Store(addressValue, sourceValue, width, MemoryAccess.Local)
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
      case Instruction.ReduxSync(destination, source, memberMask, _, _)
          if allModifiers(instruction) match {
            case Vec(Modifier.Sync, operation, width) =>
              Set(Modifier.Add, Modifier.Min, Modifier.Max).contains(operation) &&
              Set(Modifier.U32, Modifier.S32, Modifier.U64, Modifier.S64).contains(width)
            case _ => false
          } =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(allModifiers(instruction))
          operation = allModifiers(instruction)(1) match {
            case Modifier.Add => IntegerOperation.Add
            case Modifier.Min => IntegerOperation.Minimum(hasAnyModifier(instruction, signedScalarModifiers))
            case _            => IntegerOperation.Maximum(hasAnyModifier(instruction, signedScalarModifiers))
          }
        } yield VMIR.Instruction.WarpReduce(destinationName, sourceValue, memberMaskValue, operation, width)
      case Instruction.Atom(destination, address, source, None, _, _)
          if {
            def compatible(operation: Modifier, width: Modifier): Boolean = operation match {
              case Modifier.Max | Modifier.Min => Set(Modifier.U32, Modifier.U64, Modifier.S32, Modifier.S64)(width)
              case Modifier.And | Modifier.Or | Modifier.Xor => Set(Modifier.B32, Modifier.B64)(width)
              case _                                         => false
            }
            atomicModifiers(allModifiers(instruction)) match {
              case Vec(operation, width) => compatible(operation, width)
              case Vec(space, operation, width) if Set(Modifier.Global, Modifier.Shared)(space) =>
                compatible(operation, width)
              case Vec(scope, Modifier.Global, operation, width)
                  if Set(Modifier.Cta, Modifier.Gpu, Modifier.Sys)(scope) => compatible(operation, width)
              case Vec(order, scope, Modifier.Global, operation, width)
                  if Set(Modifier.Relaxed, Modifier.Acquire, Modifier.Release, Modifier.AcqRel)(order) &&
                    Set(Modifier.Cta, Modifier.Gpu, Modifier.Sys)(scope) => compatible(operation, width)
              case _ => false
            }
          } =>
        atomicIntegerOperands(destination, address, source, allModifiers(instruction)).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            val tokens    = allModifiers(instruction)
            val operation = tokens.find(Set(
              Modifier.Max,
              Modifier.Min,
              Modifier.And,
              Modifier.Or,
              Modifier.Xor
            ).contains).get
            val result = operation match {
              case Modifier.Max => IntegerOperation.Maximum(tokens.exists(Set(Modifier.S32, Modifier.S64)))
              case Modifier.Min => IntegerOperation.Minimum(tokens.exists(Set(Modifier.S32, Modifier.S64)))
              case Modifier.And => IntegerOperation.BitwiseAnd
              case Modifier.Or  => IntegerOperation.BitwiseOr
              case _            => IntegerOperation.BitwiseXor
            }
            VMIR.Instruction.AtomicBinary(destinationName, addressValue, sourceValue, width, space, result)
        }
      case Instruction.Atom(destination, address, comparator, Some(source), _, _)
          if atomicModifiers(allModifiers(instruction)) match {
            case Vec(Modifier.Cas, width) => Set(Modifier.B32, Modifier.B64)(width)
            case Vec(space, Modifier.Cas, width) if Set(Modifier.Global, Modifier.Shared)(space) =>
              Set(Modifier.B32, Modifier.B64)(width)
            case _ => false
          } =>
        for {
          comparatorValue <- value(comparator)
          operands        <- atomicIntegerOperands(destination, address, source, allModifiers(instruction))
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
          if atomicModifiers(allModifiers(instruction)) match {
            case Vec(Modifier.Exch, width) => Set(Modifier.B32, Modifier.B64)(width)
            case Vec(space, Modifier.Exch, width) if Set(Modifier.Global, Modifier.Shared)(space) =>
              Set(Modifier.B32, Modifier.B64)(width)
            case _ => false
          } =>
        atomicIntegerOperands(destination, address, source, allModifiers(instruction)).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            VMIR.Instruction.AtomicExchange(destinationName, addressValue, sourceValue, width, space)
        }
      case Instruction.Atom(destination, address, source, None, _, _)
          if atomicModifiers(allModifiers(instruction)) match {
            case Vec(Modifier.Add, width) => Set(Modifier.U32, Modifier.U64)(width)
            case Vec(space, Modifier.Add, width) if Set(Modifier.Global, Modifier.Shared)(space) =>
              Set(Modifier.U32, Modifier.U64)(width)
            case _ => false
          } =>
        atomicIntegerOperands(destination, address, source, allModifiers(instruction)).map {
          case (destinationName, addressValue, sourceValue, width, space) =>
            VMIR.Instruction.AtomicAdd(destinationName, addressValue, sourceValue, width, space)
        }
      case Instruction.Atom(destination, address, source, None, _, _)
          if atomicModifiers(allModifiers(instruction)) match {
            case Vec(Modifier.Global, Modifier.Add, width) => Set(Modifier.F32, Modifier.F64)(width)
            case _                                         => false
          } =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width = if (allModifiers(instruction).lastOption.contains(Modifier.F64)) 64: Int in (16, 32, 64)
          else 32: Int in (16, 32, 64)
        } yield VMIR.Instruction.AtomicAddFloat(destinationName, addressValue, sourceValue, width)
      case Instruction.Setp(destination, left, right, None, _, _) =>
        lowerSetPredicate(destination, left, right, allModifiers(instruction))
      case Instruction.Bar(destination, Some(barrier), Some(predicate), None, _, _)
          if allModifiers(instruction) == Vec(Modifier.Red, Modifier.And, Modifier.Pred) ||
            allModifiers(instruction) == Vec(Modifier.Red, Modifier.Or, Modifier.Pred) =>
        for {
          destinationName <- predicateRegisterName(destination)
          barrierId       <- immediate(barrier)
          predicateName   <- predicateRegisterName(predicate)
          operation = if (modifiersAre(instruction, Modifier.Red, Modifier.And, Modifier.Pred))
            PredicateOperation.And
          else PredicateOperation.Or
          checkedBarrierId <-
            if (barrierId >= 0L && barrierId <= Int.MaxValue.toLong)
              Right(barrierId.toInt.unsafe[Int >= 0])
            else invalid(s"barrier id is outside Int range: $barrierId")
        } yield VMIR.Instruction.BarrierReduce(destinationName, checkedBarrierId, predicateName, operation)
      case Instruction.Bra(target, _, _)
          if allModifiers(instruction).isEmpty || allModifiers(instruction) == Vec(Modifier.Uni) =>
        target match {
          case Operand.Symbol(label) =>
            labels.get(label)
              .toRight(Error.Invalid(s"unknown label: $label"))
              .map(VMIR.Instruction.Branch(_))
          case other => invalid(s"expected branch label, found $other")
        }
      case Instruction.Bar(_, _, _, _, _, Some(_))
          if allModifiers(instruction) == Vec(Modifier.Sync) =>
        invalid("bar.sync must not be predicated in the supported execution profile")
      case Instruction.Bar(barrier, None, None, None, _, _)
          if allModifiers(instruction) == Vec(Modifier.Sync) =>
        immediate(barrier).flatMap { value =>
          if (value >= 0L && value <= Int.MaxValue.toLong)
            Right(VMIR.Instruction.BarrierSync(value.toInt.unsafe[Int >= 0]))
          else invalid(s"barrier id is outside Int range: $value")
        }
      case Instruction.GridDependencyControl(_, _)
          if allModifiers(instruction) == Vec(Modifier.Wait) ||
            allModifiers(instruction) == Vec(Modifier.LaunchDependents) =>
        // The launcher retains completion dependencies instead of scheduling
        // dependent grids early. A full fence gives the required visibility;
        // launch_dependents may conservatively delay release until grid exit.
        Right(VMIR.Instruction.MemoryBarrier)
      case Instruction.Membar(_, _)
          if allModifiers(instruction).size == 1 &&
            allModifiers(instruction).headOption.exists(Set(Modifier.Cta, Modifier.Gl, Modifier.Sys).contains) =>
        Right(VMIR.Instruction.MemoryBarrier)
      case Instruction.Fence(None, None, _, _)
          if {
            val modifiers = allModifiers(instruction)
            val scope     = Set(Modifier.Cta, Modifier.Gpu, Modifier.Sys, Modifier.Cluster)
            val order     = Set(Modifier.Sc, Modifier.AcqRel, Modifier.Acquire, Modifier.Release)
            modifiers match {
              case Vec(space)           => scope(space)
              case Vec(ordering, space) => order(ordering) && scope(space)
              case _                    => false
            }
          } =>
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
        Left(Error.Unsupported(instruction.qualifiedOpcode, "no handwritten semantic family"))
    }
    allModifiers(instruction).collectFirst { case Modifier.Unrecognized(spelling) => spelling } match {
      case Some(spelling) => Left(Error.Unsupported(instruction.qualifiedOpcode, s"unknown modifier: ${spelling.raw}"))
      case None           =>
        for {
          _         <- validateFloatingInstruction(instruction)
          operation <- lowered
          predicate <- lowerPredicate(instruction.pred)
        } yield PredicatedInstruction(operation, predicate)
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
        PackedFloatTypes ++
          (signature.opcode match {
            case Opcode.Fma              => Set(Modifier.Rn, Modifier.Sat)
            case Opcode.Min | Opcode.Max => Set(Modifier.NaN)
            case _                       => Set(Modifier.Rn)
          })
      else if (bf16) Set(Modifier.Bf16, Modifier.Rn)
      else {
        val behavior = signature.opcode match {
          case Opcode.Abs | Opcode.Neg              => Set(Modifier.Ftz)
          case Opcode.Min | Opcode.Max              => Set(Modifier.Ftz, Modifier.NaN, Modifier.Abs, Modifier.Xorsign)
          case Opcode.CopySign                      => Set.empty[Modifier]
          case Opcode.Add | Opcode.Sub | Opcode.Mul => Set(Modifier.Rn, Modifier.Ftz, Modifier.Sat)
          case Opcode.Fma => Set(Modifier.Rn, Modifier.Rz, Modifier.Rm, Modifier.Rp, Modifier.Ftz, Modifier.Sat)
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
      if (extraOperand) Left(Error.Unsupported(instruction.qualifiedOpcode, "three-input min/max is not implemented"))
      else if (bf16 && signature.opcode != Opcode.Fma)
        Left(Error.Unsupported(
          instruction.qualifiedOpcode,
          "scalar bf16 arithmetic is not implemented for this operation"
        ))
      else if (unsupported.nonEmpty)
        Left(Error.Unsupported(
          instruction.qualifiedOpcode,
          s"unsupported floating modifiers: ${unsupported.mkString(".")}"
        ))
      else if (modifiers.distinct.size != modifiers.size || modifiers.count(types) != 1)
        invalid("expected one floating type and no duplicate modifiers")
      else if (modifiers.contains(Modifier.Xorsign) && !modifiers.contains(Modifier.Abs))
        invalid("xorsign requires abs")
      else Right(())
    }
  }

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

  private def conversionType(modifier: Modifier): Option[ConversionType] = modifier match {
    case Modifier.B8   => Some(ConversionType.B8)
    case Modifier.S8   => Some(ConversionType.S8)
    case Modifier.U8   => Some(ConversionType.U8)
    case Modifier.B16  => Some(ConversionType.B16)
    case Modifier.S16  => Some(ConversionType.S16)
    case Modifier.U16  => Some(ConversionType.U16)
    case Modifier.F16  => Some(ConversionType.F16)
    case Modifier.Bf16 => Some(ConversionType.BF16)
    case Modifier.B32  => Some(ConversionType.B32)
    case Modifier.S32  => Some(ConversionType.S32)
    case Modifier.U32  => Some(ConversionType.U32)
    case Modifier.F32  => Some(ConversionType.F32)
    case Modifier.B64  => Some(ConversionType.B64)
    case Modifier.S64  => Some(ConversionType.S64)
    case Modifier.U64  => Some(ConversionType.U64)
    case Modifier.F64  => Some(ConversionType.F64)
    case _             => None
  }

  private def conversionRounding(modifier: Modifier): Option[ConversionRounding] = modifier match {
    case Modifier.Rn  => Some(ConversionRounding.RN)
    case Modifier.Rni => Some(ConversionRounding.RNI)
    case Modifier.Rz  => Some(ConversionRounding.RZ)
    case Modifier.Rzi => Some(ConversionRounding.RZI)
    case Modifier.Rm  => Some(ConversionRounding.RM)
    case Modifier.Rmi => Some(ConversionRounding.RMI)
    case Modifier.Rp  => Some(ConversionRounding.RP)
    case Modifier.Rpi => Some(ConversionRounding.RPI)
    case _            => None
  }

  private def lowerSetPredicate(
      destination: Operand,
      left: Operand,
      right: Operand,
      instructionModifiers: Vec[Modifier]
  ): Either[Error, VMIR.Instruction] = {
    val instruction = ("setp" +: instructionModifiers.map(_.name.raw)).mkString(".")
    val comparison  = instructionModifiers.collectFirst(Function.unlift {
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
    val operandType = instructionModifiers.find(modifier =>
      modifier == Modifier.Pred || conversionType(modifier).nonEmpty
    )
    for {
      predicate <- comparison.toRight(
        Error.Invalid(s"unsupported setp comparison: $instruction")
      )
      selectedType <- operandType.toRight(
        Error.Invalid(s"unsupported setp type: $instruction")
      )
      _ <- Either.cond(
        !instructionModifiers.contains(Modifier.Ftz) || selectedType == Modifier.F32,
        (),
        Error.Unsupported("setp", "ftz requires f32 comparison operands")
      )
      destinationName <- predicateRegisterName(destination)
      lowered         <-
        if (selectedType == Modifier.Pred)
          for {
            leftName  <- predicateRegisterName(left)
            rightName <- predicateRegisterName(right)
          } yield VMIR.Instruction.PredicateCompare(destinationName, predicate, leftName, rightName)
        else
          for {
            leftValue   <- value(left)
            rightValue  <- value(right)
            numericType <- conversionType(selectedType).toRight(Error.Invalid(s"unsupported setp type: $instruction"))
          } yield VMIR.Instruction.SetPredicate(
            destinationName,
            predicate,
            numericType.format,
            leftValue,
            rightValue,
            flushSubnormal = instructionModifiers.contains(Modifier.Ftz)
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

  private def predicateValue(operand: Operand): Either[Error, Value] = operand match {
    case Operand.Register(name, None) => Right(predicateValue(name))
    case Operand.Symbol(name)         => Right(predicateValue(name))
    case other                        => value(other)
  }

  private def predicateValue(name: NonEmpty[String]): Value =
    VMPredicateFile.slot(name).fold[Value](Value.Predicate(name))(Value.PredicateSlot(_))

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

  def registerNames(operand: Operand): Either[Error, Vec[NonEmpty[String]]] = operand match {
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
    val declarations                              = Vec.newBuilder[NonEmpty[String]]
    def collect(statements: Vec[Statement]): Unit = statements.foreach {
      case Statement.Block(nested)                                   => collect(nested)
      case Statement.Data(DataAddressSpace.Shared, _, _, name, _, _) => declarations += name
      case Statement.Directive(path, arguments, _, _)
          if path.size == 1 && path.head.raw == "shared" =>
        arguments.foreach { argument =>
          argument.operand match {
            case Operand.Symbol(name)                   => declarations += name
            case Operand.Index(Operand.Symbol(name), _) => declarations += name
            case _                                      => ()
          }
        }
      case _ => ()
    }
    collect(body)
    val distinct = declarations.result().distinct
    distinct.zipWithIndex.map { case (name, index) =>
      name -> (VMAddressSpace.SharedBase + index.toLong * VMAddressSpace.KernelSharedStride)
    }.toMap
  }

  def addressValue(operand: Operand): Either[Error, Value] = operand match {
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
      case Modifier.F16 => 16
      case Modifier.F32 => 32
      case Modifier.F64 => 64
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
      case Modifier.B8 | Modifier.S8 | Modifier.U8                                   => 8
      case Modifier.B16 | Modifier.S16 | Modifier.U16 | Modifier.F16 | Modifier.Bf16 => 16
      case Modifier.B32 | Modifier.S32 | Modifier.U32 | Modifier.F32                 => 32
      case Modifier.B64 | Modifier.S64 | Modifier.U64 | Modifier.F64                 => 64
    }.filter(Set(8, 16, 32, 64).contains)
      .toRight(Error.Invalid(
        s"expected an 8, 16, 32, or 64-bit $describe in ${modifiers.map(_.name.raw).mkString(".")}"
      ))
      .flatMap(width =>
        width.refine[Int where (Between[8, 64] & MultipleOf[8])].leftMap(message => Error.Invalid(message))
      )

  private def specialRegisterValue(name: NonEmpty[String]): Option[Value] =
    OperandDomains.SpecialRegister.fromSpelling(name) match {
      case OperandDomains.SpecialRegister.TidX       => Some(Value.ThreadId(Axis.X))
      case OperandDomains.SpecialRegister.TidY       => Some(Value.ThreadId(Axis.Y))
      case OperandDomains.SpecialRegister.TidZ       => Some(Value.ThreadId(Axis.Z))
      case OperandDomains.SpecialRegister.CtaidX     => Some(Value.BlockId(Axis.X))
      case OperandDomains.SpecialRegister.CtaidY     => Some(Value.BlockId(Axis.Y))
      case OperandDomains.SpecialRegister.CtaidZ     => Some(Value.BlockId(Axis.Z))
      case OperandDomains.SpecialRegister.NtidX      => Some(Value.BlockDim(Axis.X))
      case OperandDomains.SpecialRegister.NtidY      => Some(Value.BlockDim(Axis.Y))
      case OperandDomains.SpecialRegister.NtidZ      => Some(Value.BlockDim(Axis.Z))
      case OperandDomains.SpecialRegister.NctaidX    => Some(Value.GridDim(Axis.X))
      case OperandDomains.SpecialRegister.NctaidY    => Some(Value.GridDim(Axis.Y))
      case OperandDomains.SpecialRegister.NctaidZ    => Some(Value.GridDim(Axis.Z))
      case OperandDomains.SpecialRegister.LaneId     => Some(Value.LaneId)
      case OperandDomains.SpecialRegister.LaneMaskGe => Some(Value.LaneMaskGreaterEqual)
      case OperandDomains.SpecialRegister.LaneMaskGt => Some(Value.LaneMaskGreater)
      case OperandDomains.SpecialRegister.LaneMaskLe => Some(Value.LaneMaskLessEqual)
      case OperandDomains.SpecialRegister.LaneMaskLt => Some(Value.LaneMaskLess)
      case OperandDomains.SpecialRegister.LaneMaskEq => Some(Value.LaneMaskEqual)
      case OperandDomains.SpecialRegister.Unknown(_) => None
    }

  def value(operand: Operand): Either[Error, Value] = operand match {
    case Operand.Register(name, None) =>
      specialRegisterValue(name).fold {
        Right(VMRegisterFile.slot(name).fold[Value](Value.Register(name))(Value.RegisterSlot(_)))
      }(Right(_))
    case Operand.Symbol(name) if isBareRegisterName(name.raw) =>
      val canonical: NonEmpty[String] = s"%${name.raw}"
      Right(VMRegisterFile.slot(canonical).fold[Value](Value.Register(name))(Value.RegisterSlot(_)))
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
      val suffixStripped = NumericLiteral.stripIntegerSuffix(signless)
      val parsed         =
        try {
          val bitPattern = NumericLiteral.hexadecimalPayload(suffixStripped, 'f')
            .orElse(NumericLiteral.hexadecimalPayload(suffixStripped, 'd'))
          val magnitude = bitPattern.fold(NumericLiteral.integer(suffixStripped))(digits => BigInt(digits, 16))
          Some(magnitude * sign)
        } catch {
          case _: NumberFormatException => None
        }
      parsed match {
        case Some(number) if number.isValidLong                        => Right(number.toLong)
        case Some(number) if number >= 0 && number < (BigInt(1) << 64) => Right(number.toLong)
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
