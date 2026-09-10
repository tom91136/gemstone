package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.execution.*

import cats.syntax.all.*

import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; architectural state

enum LoweringError {
  case Invalid(detail: String)
  case Unsupported(instruction: String, detail: String)

  def message: String = this match {
    case Invalid(detail)                  => detail
    case Unsupported(instruction, detail) => s"unsupported instruction: $instruction ($detail)"
  }
}

@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
object Lowering {
  def lower(entry: Statement.Entry): Either[LoweringError, Kernel] = lower(entry, Col.empty, Map.empty)

  def parameters(entry: Statement.Entry): Either[LoweringError, Col[KernelParameter]] =
    lowerParameters(entry.parameters)

  def lower(entry: Statement.Entry, functions: Col[Statement.Function]): Either[LoweringError, Kernel] =
    lower(entry, functions, Map.empty)

  def lower(
      entry: Statement.Entry,
      functions: Col[Statement.Function],
      globalSymbols: Map[String, Long]
  ): Either[LoweringError, Kernel] = lower(entry, functions, globalSymbols, Map.empty)

  def lower(
      entry: Statement.Entry,
      functions: Col[Statement.Function],
      globalSymbols: Map[String, Long],
      additionalSharedSymbols: Map[String, Long]
  ): Either[LoweringError, Kernel] =
    lower(entry, functions, globalSymbols, additionalSharedSymbols, Col.empty)

  def lower(
      entry: Statement.Entry,
      functions: Col[Statement.Function],
      globalSymbols: Map[String, Long],
      additionalSharedSymbols: Map[String, Long],
      additionalEntries: Col[Statement.Entry]
  ): Either[LoweringError, Kernel] = {
    val Statement.Entry(_, name, parameters, body) = entry
    val functionNames = functions.iterator.map(_.name).toSet ++ additionalEntries.iterator.map(_.name).toSet
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
      globalSymbols
    )
  }

  private def lowerReachableFunctions(
      program: ExecutableProgram[PredicatedInstruction],
      functions: Col[Statement.Function],
      entries: Col[Statement.Entry],
      functionNames: Set[String]
  ): Either[LoweringError, Map[String, DeviceFunction]] = {
    val functionDefinitions = functions.iterator.map(function => function.name -> function).toMap
    val entryDefinitions    = entries.iterator.map(entry => entry.name -> entry).toMap
    val pending             = mutable.Queue.empty[String]
    val lowered             = mutable.Map.empty[String, DeviceFunction]

    def enqueueCalls(value: ExecutableProgram[PredicatedInstruction]): Unit =
      value.instructions.foreach {
        case PredicatedInstruction(ExecutableInstruction.Call(Value.Function(function), _, _), _) =>
          if (!lowered.contains(function) && !pending.contains(function)) pending.enqueue(function)
        case _ => ()
      }

    def enqueueAddressTakenFunctions(value: ExecutableProgram[PredicatedInstruction]): Unit =
      value.instructions.foreach {
        case PredicatedInstruction(ExecutableInstruction.Move(_, Value.Function(function), _), _) =>
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

  private def lowerProgram(
      body: Col[Statement],
      functionNames: Set[String]
  ): Either[LoweringError, ExecutableProgram[PredicatedInstruction]] = {
    val flat = flattenScoped(body) match {
      case Left(error)  => return Left(error)
      case Right(value) => value
    }
    val labels           = mutable.Map.empty[String, Pc]
    var instructionIndex = 0
    var statementIndex   = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Label(label) =>
          if (labels.contains(label)) return Left(LoweringError.Invalid(s"duplicate label: $label"))
          labels(label) = Pc.unsafe(instructionIndex)
        case Statement.Instruction(_)                                       => instructionIndex += 1
        case Statement.UnsupportedInstruction(opcode, operands, mods, pred) =>
          val qualified = (opcode +: mods.map(_.name)).mkString(".")
          val rendered  = pred.map(value => s"${Printer.render(value)} ").getOrElse("") +
            qualified + (if (operands.isEmpty) "" else operands.map(Printer.render).mkString(" ", ", ", "")) + ";"
          return Left(LoweringError.Unsupported(rendered, "instruction is not implemented"))
        case _ => ()
      }
      statementIndex += 1
    }

    val resolvedLabels = labels.toMap
    val lowered        = Col.newBuilder[PredicatedInstruction]
    statementIndex = 0
    while (statementIndex < flat.size) {
      flat(statementIndex) match {
        case Statement.Instruction(instruction) =>
          lowerInstruction(instruction, resolvedLabels, functionNames) match {
            case Left(error) =>
              val contextual = error match {
                case LoweringError.Invalid(detail) =>
                  LoweringError.Invalid(s"${Printer.render(instruction)}: $detail")
                case LoweringError.Unsupported(_, detail) =>
                  LoweringError.Unsupported(Printer.render(instruction), detail)
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
      functionNames: Set[String]
  ): Either[LoweringError, DeviceFunction] = {
    val Statement.Function(_, name, returnParameters, parameters, body) = function
    body match {
      case None             => Left(LoweringError.Invalid(s"function $name has no body"))
      case Some(statements) =>
        for {
          _                 <- lowerParameters(returnParameters)
          loweredParameters <- lowerParameters(parameters)
          program           <- lowerProgram(statements, functionNames)
        } yield DeviceFunction(
          name,
          parameters.map(_.name),
          loweredParameters.map(_.widthBytes),
          parameterOffsets(parameters, loweredParameters),
          returnParameters.map(_.name),
          program,
          sharedSymbols(statements)
        )
    }
  }

  private def lowerEntryFunction(
      entry: Statement.Entry,
      functionNames: Set[String]
  ): Either[LoweringError, DeviceFunction] = {
    val Statement.Entry(_, name, parameters, body) = entry
    for {
      loweredParameters <- lowerParameters(parameters)
      program           <- lowerProgram(body, functionNames)
    } yield DeviceFunction(
      name,
      parameters.map(_.name),
      loweredParameters.map(_.widthBytes),
      parameterOffsets(parameters, loweredParameters),
      Col.empty,
      program,
      sharedSymbols(body)
    )
  }

  private def parameterOffsets(
      declarations: Col[Parameter],
      parameters: Col[KernelParameter]
  ): Col[Int] = {
    val result = Col.newBuilder[Int]
    var offset = 0L
    declarations.zip(parameters).foreach { case (declaration, parameter) =>
      val qualifiers        = declaration.qualifiers
      val explicitAlignment = qualifiers.sliding(2).collectFirst {
        case Col("align", value) => value.toIntOption
      }.flatten
      val alignment = explicitAlignment.getOrElse(parameter.widthBytes)
      offset = (offset + alignment.toLong - 1L) & -alignment.toLong
      result += Math.toIntExact(offset)
      offset = Math.addExact(offset, parameter.widthBytes.toLong)
    }
    result.result()
  }

  private def lowerParameters(parameters: Col[Parameter]): Either[LoweringError, Col[KernelParameter]] = {
    val result = Col.newBuilder[KernelParameter]
    var index  = 0
    while (index < parameters.length) {
      val parameter = parameters(index)
      val widths    = parameter.qualifiers.collect {
        case "u8" | "s8" | "b8"            => 1
        case "u16" | "s16" | "b16"         => 2
        case "u32" | "s32" | "b32" | "f32" => 4
        case "u64" | "s64" | "b64" | "f64" => 8
      }
      widths.lastOption match {
        case Some(width) =>
          val elements = parameter.dimensions.foldLeft[Either[LoweringError, Long]](Right(1L)) { (current, value) =>
            for {
              count   <- current
              element <- immediate(value)
              product <-
                if (element > 0 && count <= Int.MaxValue.toLong / element) Right(count * element)
                else invalid(s"kernel parameter ${parameter.name} has an invalid array dimension: $element")
            } yield product
          }
          elements match {
            case Left(error)  => return Left(error)
            case Right(count) => result += KernelParameter(parameter.name, Math.toIntExact(width.toLong * count))
          }
        case None => return Left(LoweringError.Invalid(s"kernel parameter ${parameter.name} has no scalar width"))
      }
      index += 1
    }
    Right(result.result())
  }

  /** Flatten PTX blocks while alpha-renaming registers declared in nested scopes. */
  private def flattenScoped(statements: Col[Statement]): Either[LoweringError, Col[Statement]] = {
    var nextScope = 0

    def registerNames(operand: Operand): Iterator[String] = operand match {
      case Operand.Register(name, _)    => Iterator.single(name)
      case Operand.VectorLiteral(items) => items.iterator.flatMap(registerNames)
      case Operand.Parenthesized(items) => items.iterator.flatMap(registerNames)
      case _                            => Iterator.empty
    }

    def declaredRegisters(body: Col[Statement]): Col[String] =
      Col.toCol(body.iterator.flatMap {
        case Statement.Directive(path, arguments, _)
            if path == Col("reg") || path.headOption.contains("reg") =>
          arguments.iterator.flatMap(argument => registerNames(argument.operand))
        case _ => Iterator.empty
      }).distinct

    def renameOperand(operand: Operand, names: Map[String, String]): Operand = operand match {
      case Operand.Register(name, part)  => Operand.Register(names.getOrElse(name, name), part)
      case Operand.VectorLiteral(items)  => Operand.VectorLiteral(items.map(renameOperand(_, names)))
      case Operand.Parenthesized(items)  => Operand.Parenthesized(items.map(renameOperand(_, names)))
      case Operand.Address(items)        => Operand.Address(items.map(renameOperand(_, names)))
      case Operand.Index(base, index)    => Operand.Index(renameOperand(base, names), renameOperand(index, names))
      case Operand.Add(left, right)      => Operand.Add(renameOperand(left, names), renameOperand(right, names))
      case Operand.Subtract(left, right) =>
        Operand.Subtract(renameOperand(left, names), renameOperand(right, names))
      case Operand.WithPredicate(base, predicate) =>
        Operand.WithPredicate(renameOperand(base, names), renameOperand(predicate, names))
      case Operand.Negate(value) => Operand.Negate(renameOperand(value, names))
      case value                 => value
    }

    def renameInstruction(instruction: Instruction, names: Map[String, String]): Either[LoweringError, Instruction] = {
      val operands = instruction.operands.map(renameOperand(_, names))
      val pred     = instruction.pred.map(value => value.copy(operand = renameOperand(value.operand, names)))
      Instruction.decode(instruction.opcode, operands, instruction.mods, pred).leftMap(error =>
        LoweringError.Invalid(s"failed to preserve scoped instruction ${Printer.render(instruction)}: $error")
      )
    }

    def visit(body: Col[Statement], names: Map[String, String]): Either[LoweringError, Col[Statement]] = {
      val result = Col.newBuilder[Statement]
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

  private def flatten(statements: Col[Statement]): Col[Statement] = statements.flatMap {
    case Statement.Block(body) => flatten(body)
    case value                 => Col(value)
  }

  private def lowerInstruction(
      instruction: Instruction,
      labels: Map[String, Pc],
      functionNames: Set[String]
  ): Either[LoweringError, PredicatedInstruction] = {
    val lowered = instruction match {
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.param.") && isDirectParameterAddress(address) =>
        for {
          destinationNames   <- registerNames(destination)
          parameterAndOffset <- parameterAddress(address)
          width              <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadParameter(
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
        } yield ExecutableInstruction.StoreParameter(
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
          if (destination.isInstanceOf[Operand.VectorLiteral])
            ExecutableInstruction.LoadParameterIndirectVector(
              destinationNames,
              addressValue,
              width,
              instruction.qualifiedOpcode.contains(".s")
            )
          else
            ExecutableInstruction.LoadParameterIndirect(
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
        } yield ExecutableInstruction.MovePredicate(destinationName, sourceValue)
      case Instruction.Activemask(destination, _, _) =>
        registerName(destination).map(destinationName =>
          ExecutableInstruction.Move(destinationName, Value.ActiveMask, 32)
        )
      case Instruction.Mov(destination, source, _, _)
          if Set("mov.u32", "mov.s32", "mov.b32").contains(instruction.qualifiedOpcode) &&
            !destination.isInstanceOf[Operand.VectorLiteral] &&
            !source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 32)
      case Instruction.Mov(destination, source, modifiers, _)
          if Set("mov.u8", "mov.s8", "mov.b8", "mov.u16", "mov.s16", "mov.b16").contains(instruction.qualifiedOpcode) =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, width)
      case Instruction.Mov(destination, source, _, _)
          if (instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64") &&
            destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- splitRegisterNames(destination)
          sourceValue      <- value(source)
          _                <- requirePair(destinationNames, "mov.b64 destination")
        } yield ExecutableInstruction.MoveSplit(destinationNames, sourceValue, 32)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.b32" && destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- splitRegisterNames(destination)
          sourceValue      <- value(source)
          _                <- requirePair(destinationNames, "mov.b32 destination")
        } yield ExecutableInstruction.MoveSplit(destinationNames, sourceValue, 16)
      case Instruction.Mov(destination, source, _, _)
          if (instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64") &&
            source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationName <- registerName(destination)
          sourceValues    <- vectorValues(source)
          _               <- requirePair(sourceValues, "mov.b64 source")
        } yield ExecutableInstruction.MoveCombine(destinationName, sourceValues, 32)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.b32" && source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationName <- registerName(destination)
          sourceValues    <- vectorValues(source)
          _               <- requirePair(sourceValues, "mov.b32 source")
        } yield ExecutableInstruction.MoveCombine(destinationName, sourceValues, 16)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.u64" || instruction.qualifiedOpcode == "mov.b64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- functionValue(source, functionNames, promoteUnknown = false)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.f32" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 32)
      case Instruction.Mov(destination, source, _, _)
          if instruction.qualifiedOpcode == "mov.f64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Neg(destination, source, modifiers, _) if !instruction.qualifiedOpcode.contains(".f") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.IntegerUnary(IntegerUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Neg(destination, source, modifiers, _) if instruction.qualifiedOpcode.contains(".f") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- floatWidth(modifiers)
        } yield ExecutableInstruction.FloatUnary(FloatUnaryOperation.Negate, destinationName, sourceValue, width)
      case Instruction.Abs(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("abs.f") =>
        floatUnary(FloatUnaryOperation.Absolute, destination, source, modifiers)
      case Instruction.Abs(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("abs.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.IntegerUnary(
          IntegerUnaryOperation.Absolute,
          destinationName,
          sourceValue,
          width
        )
      case Instruction.RcpApproxFtzF64(destination, source, modifiers, _) =>
        floatUnary(FloatUnaryOperation.Reciprocal, destination, source, modifiers, Some(64))
      case Instruction.Rcp(destination, source, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("rcp.") =>
        floatUnary(FloatUnaryOperation.Reciprocal, destination, source, modifiers)
      case Instruction.RsqrtApproxFtzF64(destination, source, modifiers, _) =>
        floatUnary(FloatUnaryOperation.ReciprocalSquareRoot, destination, source, modifiers, Some(64))
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
          } yield ExecutableInstruction.PredicateUnary(destinationName, sourceName)
        else
          for {
            destinationName <- registerName(destination)
            sourceValue     <- value(source)
            width           <- integerWidth(modifiers)
          } yield ExecutableInstruction.IntegerUnary(
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
        } yield ExecutableInstruction.IntegerUnary(
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
        } yield ExecutableInstruction.IntegerUnary(
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
        } yield ExecutableInstruction.BitFind(
          destinationName,
          sourceValue,
          width,
          instruction.qualifiedOpcode.contains(".s"),
          instruction.qualifiedOpcode.contains(".shiftamt.")
        )
      case Instruction.Dp4a(destination, left, right, addend, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("dp4a.") =>
        val types = modifiers.map(_.name).filter(Set("s32", "u32"))
        if (types.size != 2) invalid(s"expected two dp4a operand types: ${instruction.qualifiedOpcode}")
        else
          for {
            destinationName <- registerName(destination)
            leftValue       <- value(left)
            rightValue      <- value(right)
            addendValue     <- value(addend)
          } yield ExecutableInstruction.DotProduct4(
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
        } yield ExecutableInstruction.IntegerUnary(
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
        } yield ExecutableInstruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned32,
          destinationName,
          leftValue,
          rightValue,
          64
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.s32" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield ExecutableInstruction.IntegerBinary(
          IntegerOperation.MultiplyWideSigned32,
          destinationName,
          leftValue,
          rightValue,
          64
        )
      case Instruction.Mul(destination, left, right, _, _) if instruction.qualifiedOpcode == "mul.wide.u16" =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
        } yield ExecutableInstruction.IntegerBinary(
          IntegerOperation.MultiplyWideUnsigned16,
          destinationName,
          leftValue,
          rightValue,
          32
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
        } yield ExecutableInstruction.FusedMultiplyAddPacked(
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
        } yield ExecutableInstruction.FusedMultiplyAddBf16(destinationName, leftValue, rightValue, addendValue)
      case Instruction.Fma(destination, left, right, addend, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("fma.") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
          width           <- floatWidth(modifiers)
        } yield ExecutableInstruction.FusedMultiplyAdd(destinationName, leftValue, rightValue, addendValue, width)
      case Instruction.Shl(destination, left, right, _, _) if instruction.qualifiedOpcode == "shl.b32" =>
        integerBinary(IntegerOperation.ShiftLeft, 32, destination, left, right)
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
      case Instruction.ShflSync(destination, source, delta, clamp, memberMask, modifiers, _)
          if Set("shfl.sync.down.b32", "shfl.sync.down.b64").contains(instruction.qualifiedOpcode) =>
        for {
          destinationInfo <- valueDestination(destination)
          sourceValue     <- value(source)
          deltaValue      <- value(delta)
          clampValue      <- value(clamp)
          memberMaskValue <- value(memberMask)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.ShuffleDown(
          destinationInfo._1,
          destinationInfo._2,
          sourceValue,
          deltaValue,
          clampValue,
          memberMaskValue,
          width
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
        } yield ExecutableInstruction.ShuffleUp(
          destinationInfo._1,
          destinationInfo._2,
          sourceValue,
          deltaValue,
          clampValue,
          memberMaskValue,
          width
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
        } yield ExecutableInstruction.ShuffleIndex(
          destinationInfo._1,
          destinationInfo._2,
          sourceValue,
          indexValue,
          clampValue,
          memberMaskValue,
          width
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
        } yield ExecutableInstruction.ShuffleBfly(
          destinationInfo._1,
          destinationInfo._2,
          sourceValue,
          indexValue,
          clampValue,
          memberMaskValue,
          width
        )
      case Instruction.Vote(destination, predicate, _, _) if instruction.qualifiedOpcode == "vote.ballot.b32" =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
        } yield ExecutableInstruction.VoteBallot(destinationName, predicateName, Value.Immediate(0xffffffffL))
      case Instruction.VoteSync(destination, predicate, memberMask, _, _)
          if instruction.qualifiedOpcode == "vote.sync.ballot.b32" =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
          memberMaskValue <- value(memberMask)
        } yield ExecutableInstruction.VoteBallot(destinationName, predicateName, memberMaskValue)
      case Instruction.VoteSync(destination, predicate, memberMask, _, _)
          if instruction.qualifiedOpcode == "vote.sync.all.pred" ||
            instruction.qualifiedOpcode == "vote.sync.any.pred" ||
            instruction.qualifiedOpcode == "vote.sync.uni.pred" =>
        for {
          destinationName <- registerName(destination)
          predicateName   <- predicateRegisterName(predicate)
          memberMaskValue <- value(memberMask)
          operation = if (instruction.qualifiedOpcode.endsWith(".all.pred")) VoteOperation.All
          else if (instruction.qualifiedOpcode.endsWith(".any.pred")) VoteOperation.Any
          else VoteOperation.Uniform
        } yield ExecutableInstruction.VotePredicate(destinationName, predicateName, memberMaskValue, operation)
      case Instruction.Bfe(destination, source, start, widthValue, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("bfe.") =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
          startValue      <- value(start)
          widthOperand    <- value(widthValue)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.BitFieldExtract(
          destinationName,
          sourceValue,
          startValue,
          widthOperand,
          width,
          instruction.qualifiedOpcode.contains(".s")
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
        } yield ExecutableInstruction.BitFieldInsert(
          destinationName,
          insertOperand,
          baseOperand,
          positionOperand,
          lengthOperand,
          width
        )
      case Instruction.Bmsk(destination, start, widthValue, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("bmsk.") =>
        for {
          destinationName <- registerName(destination)
          startValue      <- value(start)
          widthOperand    <- value(widthValue)
          mode            <- modifiers.map(_.name).find(name => name == "clamp" || name == "wrap")
            .toRight(LoweringError.Invalid("bmsk requires .clamp or .wrap"))
          _ <- modifiers.map(_.name).find(_.startsWith("b32")).toRight(
            LoweringError.Invalid("bmsk requires a .b32 width")
          )
        } yield ExecutableInstruction.BitMask(destinationName, startValue, widthOperand, mode)
      case Instruction.Prmt(destination, left, right, control, _, _)
          if instruction.qualifiedOpcode.startsWith("prmt.b32") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          controlValue    <- value(control)
          mode = instruction.qualifiedOpcode.split("\\.").drop(2).headOption.getOrElse("")
        } yield ExecutableInstruction.BytePermute(
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
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.MultiplyAdd(
          destinationName,
          leftValue,
          rightValue,
          addendValue,
          IntegerMultiplyAddOperation.Low(
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
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.MultiplyAdd(
          destinationName,
          leftValue,
          rightValue,
          addendValue,
          IntegerMultiplyAddOperation.Low(width, width, carryIn = false, carryOut = true)
        )
      case Instruction.Mad(destination, left, right, addend, _, _)
          if instruction.qualifiedOpcode.contains(".wide.") && instruction.qualifiedOpcode.endsWith(".u32") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          addendValue     <- value(addend)
        } yield ExecutableInstruction.MultiplyAdd(
          destinationName,
          leftValue,
          rightValue,
          addendValue,
          IntegerMultiplyAddOperation.Low(32, 64, carryIn = false, carryOut = false)
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
          width     = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
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
        } yield ExecutableInstruction.MultiplyAdd(destinationName, leftValue, rightValue, addendValue, operation)
      case Instruction.AddCc(destination, left, right, _, _)
          if instruction.qualifiedOpcode.endsWith(".u32") || instruction.qualifiedOpcode.endsWith(".s32") ||
            instruction.qualifiedOpcode.endsWith(".u64") || instruction.qualifiedOpcode.endsWith(".s64") =>
        for {
          destinationName <- registerName(destination)
          leftValue       <- value(left)
          rightValue      <- value(right)
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.IntegerAddWithCarry(
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
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.IntegerAddWithCarry(
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
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.IntegerSubWithBorrow(
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
          width = if (instruction.qualifiedOpcode.endsWith("32")) 32 else 64
        } yield ExecutableInstruction.IntegerSubWithBorrow(
          destinationName,
          leftValue,
          rightValue,
          width,
          borrowIn = true,
          borrowOut = instruction.qualifiedOpcode.contains(".cc.")
        )
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "add.u32" || instruction.qualifiedOpcode == "add.s32" =>
        integerBinary(IntegerOperation.Add, 32, destination, left, right)
      case Instruction.Add(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "add.u64" || instruction.qualifiedOpcode == "add.s64" =>
        integerBinary(IntegerOperation.Add, 64, destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "sub.u32" || instruction.qualifiedOpcode == "sub.s32" =>
        integerBinary(IntegerOperation.Subtract, 32, destination, left, right)
      case Instruction.Sub(destination, left, right, _, _)
          if instruction.qualifiedOpcode == "sub.u64" || instruction.qualifiedOpcode == "sub.s64" =>
        integerBinary(IntegerOperation.Subtract, 64, destination, left, right)
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
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvta(destination, source, _, _) if instruction.qualifiedOpcode == "cvta.local.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvta(destination, source, _, _) if instruction.qualifiedOpcode == "cvta.to.local.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvta(destination, source, _, _)
          if instruction.qualifiedOpcode == "cvta.shared.u64" || instruction.qualifiedOpcode == "cvta.to.shared.u64" =>
        for {
          destinationName <- registerName(destination)
          sourceValue     <- value(source)
        } yield ExecutableInstruction.Move(destinationName, sourceValue, 64)
      case Instruction.Cvt(destination, source, None, None, modifiers, _) =>
        val types = modifiers.map(_.name).filter(isScalarType)
        if (types.size < 2) invalid(s"expected conversion source and destination types: ${instruction.qualifiedOpcode}")
        else
          for {
            destinationName <- registerName(destination)
            sourceValue     <- value(source)
          } yield ExecutableInstruction.Convert(
            destinationName,
            sourceValue,
            types.last,
            types(types.size - 2),
            modifiers.map(_.name).find(name => Set("rn", "rni", "rz", "rzi", "rm", "rmi", "rp", "rpi").contains(name))
          )
      case Instruction.Selp(destination, whenTrue, whenFalse, predicate, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("selp.") =>
        for {
          destinationName <- registerName(destination)
          falseValue      <- value(whenFalse)
          trueValue       <- value(whenTrue)
          predicateName   <- registerName(predicate)
          width           <- scalarWidth(modifiers)
        } yield ExecutableInstruction.Select(destinationName, trueValue, falseValue, predicateName, width)
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.matches("ld\\.(?:[usb]\\d+|f\\d+)") &&
            !destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGeneric(
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
            destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGenericVector(
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
        } yield ExecutableInstruction.LoadGlobal(
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
            !destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          width           <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.LdGlobalNc(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.global.nc.") && destination == Operand.Symbol("datum") =>
        for {
          addressValue <- addressValue(address)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGlobal(
          "datum",
          addressValue,
          width,
          instruction.qualifiedOpcode.contains(".s")
        )
      case Instruction.LdGlobalNc(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith(
            "ld.global.nc."
          ) && destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadGlobalVector(
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
        } yield ExecutableInstruction.LoadGlobal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.matches("st\\.(?:[usb]\\d+|f\\d+)") &&
            !source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreGeneric(addressValue, sourceValue, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") &&
            !instruction.qualifiedOpcode.contains(".shared.") &&
            !instruction.qualifiedOpcode.contains(".local.") &&
            source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- registerNames(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreGenericVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") &&
            !instruction.qualifiedOpcode.contains(".shared.") &&
            !instruction.qualifiedOpcode.contains(".local.") &&
            !source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreGlobal(addressValue, sourceValue, width)
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".shared.") &&
            destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadSharedVector(
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
        } yield ExecutableInstruction.LoadShared(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".shared.") &&
            source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- registerNames(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreSharedVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".shared.") =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreShared(addressValue, sourceValue, width)
      case Instruction.Ld(destination, address, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("ld.") && instruction.qualifiedOpcode.contains(".local.") &&
            destination.isInstanceOf[Operand.VectorLiteral] =>
        for {
          destinationNames <- registerNames(destination)
          addressValue     <- addressValue(address)
          width            <- integerWidth(modifiers)
        } yield ExecutableInstruction.LoadLocalVector(
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
        } yield ExecutableInstruction.LoadLocal(
          destinationName,
          addressValue,
          width,
          isSignedScalarOpcode(instruction.qualifiedOpcode)
        )
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".local.") &&
            source.isInstanceOf[Operand.VectorLiteral] =>
        for {
          addressValue <- addressValue(address)
          sourceNames  <- registerNames(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreLocalVector(addressValue, sourceNames, width)
      case Instruction.St(address, source, None, modifiers, _)
          if instruction.qualifiedOpcode.startsWith("st.") && instruction.qualifiedOpcode.contains(".local.") =>
        for {
          addressValue <- addressValue(address)
          sourceValue  <- value(source)
          width        <- integerWidth(modifiers)
        } yield ExecutableInstruction.StoreLocal(addressValue, sourceValue, width)
      case Instruction.CpAsync(destination, source, copySize, _, _, _, _) =>
        for {
          destinationValue <- addressValue(destination)
          sourceValue      <- addressValue(source)
          size             <- immediate(copySize).flatMap { value =>
            if (Set(4L, 8L, 16L).contains(value)) Right(value.toInt)
            else invalid(s"cp.async copy size must be 4, 8, or 16 bytes, found $value")
          }
        } yield ExecutableInstruction.AsyncCopy(destinationValue, sourceValue, size)
      case Instruction.CpAsyncCommitGroup(_, _)  => Right(ExecutableInstruction.AsyncCommit)
      case Instruction.CpAsyncWaitAll(_, _)      => Right(ExecutableInstruction.AsyncWait)
      case Instruction.CpAsyncWaitGroup(_, _, _) => Right(ExecutableInstruction.AsyncWait)
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
              Right(IntegerOperation.Minimum(modifiers.exists(_.name == s"s$width")))
            else if (instruction.qualifiedOpcode.contains(".max."))
              Right(IntegerOperation.Maximum(modifiers.exists(_.name == s"s$width")))
            else invalid(s"unsupported redux.sync operation ${instruction.qualifiedOpcode}")
        } yield ExecutableInstruction.WarpReduce(destinationName, sourceValue, memberMaskValue, operation, width)
      case Instruction.Atom(destination, address, source, None, _, _)
          if instruction.qualifiedOpcode.matches("atom\\.global\\.add\\.u(?:32|64)") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width = if (instruction.qualifiedOpcode.endsWith(".u64")) 64 else 32
        } yield ExecutableInstruction.AtomicAdd(destinationName, addressValue, sourceValue, width, global = true)
      case Instruction.Atom(destination, address, source, None, _, _)
          if instruction.qualifiedOpcode.matches("atom\\.shared\\.add\\.u(?:32|64)") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width = if (instruction.qualifiedOpcode.endsWith(".u64")) 64 else 32
        } yield ExecutableInstruction.AtomicAdd(destinationName, addressValue, sourceValue, width, global = false)
      case Instruction.Atom(destination, address, source, None, _, _)
          if instruction.qualifiedOpcode.matches("atom\\.(?:shared|global)\\.min\\.u(?:32|64)") =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width = if (instruction.qualifiedOpcode.endsWith(".u64")) 64 else 32
        } yield ExecutableInstruction.AtomicMin(
          destinationName,
          addressValue,
          sourceValue,
          width,
          global = instruction.qualifiedOpcode.startsWith("atom.global.")
        )
      case Instruction.Atom(destination, address, source, None, _, _)
          if instruction.qualifiedOpcode == "atom.global.add.f32" ||
            instruction.qualifiedOpcode == "atom.global.add.f64" =>
        for {
          destinationName <- registerName(destination)
          addressValue    <- addressValue(address)
          sourceValue     <- value(source)
          width = if (instruction.qualifiedOpcode.endsWith(".f64")) 64 else 32
        } yield ExecutableInstruction.AtomicAddFloat(destinationName, addressValue, sourceValue, width)
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
            if (barrierId >= 0L && barrierId <= Int.MaxValue.toLong) Right(barrierId.toInt)
            else invalid(s"barrier id is outside Int range: $barrierId")
        } yield ExecutableInstruction.BarrierReduce(destinationName, checkedBarrierId, predicateName, operation)
      case Instruction.Bra(target, _, _)
          if instruction.qualifiedOpcode == "bra" || instruction.qualifiedOpcode == "bra.uni" =>
        target match {
          case Operand.Symbol(label) => labels.get(label).toRight(LoweringError.Invalid(s"unknown label: $label"))
              .map(ExecutableInstruction.Branch.apply)
          case other => invalid(s"expected branch label, found ${Printer.render(other)}")
        }
      case Instruction.Bar(_, _, _, _, _, Some(_)) if instruction.qualifiedOpcode == "bar.sync" =>
        invalid("bar.sync must not be predicated in the supported execution profile")
      case Instruction.Bar(barrier, None, None, None, _, _) if instruction.qualifiedOpcode == "bar.sync" =>
        immediate(barrier).flatMap { value =>
          if (value >= 0L && value <= Int.MaxValue.toLong) Right(ExecutableInstruction.BarrierSync(value.toInt))
          else invalid(s"barrier id is outside Int range: $value")
        }
      case Instruction.Membar(_, _) if instruction.qualifiedOpcode.startsWith("membar.") =>
        Right(ExecutableInstruction.MemoryBarrier)
      case Instruction.Prefetch(_, _, _) =>
        Right(ExecutableInstruction.Prefetch)
      case Instruction.NanoSleep(_, _, _) =>
        // nanosleep is a scheduling hint; it has no observable architectural
        // effect in the deterministic emulator.
        Right(ExecutableInstruction.Prefetch)
      case Instruction.BarWarpSync(memberMask, _, _) =>
        value(memberMask).map(ExecutableInstruction.WarpBarrier.apply)
      case Instruction.Exit(_, _)                                        => Right(ExecutableInstruction.Return)
      case Instruction.Ret(_, _)                                         => Right(ExecutableInstruction.Return)
      case Instruction.Trap(_, _)                                        => Right(ExecutableInstruction.Trap)
      case Instruction.Call(returnParameters, function, params, _, _, _) =>
        for {
          functionValue  <- functionValue(function, functionNames, promoteUnknown = true)
          returnNames    <- callParameters(returnParameters)
          parameterNames <- callParameters(params)
        } yield ExecutableInstruction.Call(functionValue, returnNames, parameterNames)
      case _ =>
        Left(LoweringError.Unsupported(Printer.render(instruction), "no handwritten semantic family"))
    }
    for {
      operation <- lowered
      predicate <- lowerPredicate(instruction.pred)
    } yield PredicatedInstruction(operation, predicate)
  }

  private def integerBinary(
      operation: IntegerOperation,
      width: Int,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- predicateRegisterName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
    } yield ExecutableInstruction.IntegerBinary(operation, destinationName, leftValue, rightValue, width)

  private def floatBinary(
      operation: FloatOperation,
      destination: Operand,
      left: Operand,
      right: Operand,
      modifiers: Col[Modifier]
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
      width           <- integerWidth(modifiers)
    } yield ExecutableInstruction.FloatBinary(operation, destinationName, leftValue, rightValue, width)

  private def packedFloatBinary(
      operation: FloatOperation,
      destination: Operand,
      left: Operand,
      right: Operand,
      bf16: Boolean
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- registerName(destination)
      leftValue       <- value(left)
      rightValue      <- value(right)
    } yield ExecutableInstruction.FloatBinaryPacked(operation, destinationName, leftValue, rightValue, bf16)

  private def floatUnary(
      operation: FloatUnaryOperation,
      destination: Operand,
      source: Operand,
      modifiers: Col[Modifier],
      fixedWidth: Option[Int] = None
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- registerName(destination)
      sourceValue     <- value(source)
      width           <- fixedWidth.fold(floatWidth(modifiers))(Right.apply)
    } yield ExecutableInstruction.FloatUnary(operation, destinationName, sourceValue, width)

  private def lowerSetPredicate(
      destination: Operand,
      left: Operand,
      right: Operand,
      instructionModifiers: Col[Modifier]
  ): Either[LoweringError, ExecutableInstruction] = {
    val modifiers   = instructionModifiers.map(_.name)
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
      predicate       <- comparison.toRight(LoweringError.Invalid(s"unsupported setp comparison: $instruction"))
      kindAndWidth    <- kind.toRight(LoweringError.Invalid(s"unsupported setp type: $instruction"))
      destinationName <- predicateRegisterName(destination)
      lowered         <-
        if (kindAndWidth._1 == 'p')
          for {
            leftName  <- predicateRegisterName(left)
            rightName <- predicateRegisterName(right)
          } yield ExecutableInstruction.PredicateCompare(destinationName, predicate, leftName, rightName)
        else
          for {
            leftValue  <- value(left)
            rightValue <- value(right)
          } yield ExecutableInstruction.SetPredicate(
            destinationName,
            predicate,
            kindAndWidth._1,
            leftValue,
            rightValue,
            kindAndWidth._2
          )
    } yield lowered
  }

  private def lowerPredicate(value: Option[Predication]): Either[LoweringError, Option[ExecutionPredicate]] =
    value match {
      case None                                => Right(None)
      case Some(Predication(negated, operand)) =>
        predicateRegisterName(operand).map(name => Some(ExecutionPredicate(name, negated)))
    }

  private def predicateRegisterName(operand: Operand): Either[LoweringError, String] = operand match {
    case Operand.Register(name, None) => Right(name)
    case Operand.Symbol(name)         => Right(name)
    case other                        => invalid(s"expected predicate register, found ${Printer.render(other)}")
  }

  private def registerName(operand: Operand): Either[LoweringError, String] = operand match {
    case Operand.Register(name, None) => Right(name)
    case Operand.Symbol(name)         => Right(name)
    case other                        => invalid(s"expected register, found ${Printer.render(other)}")
  }

  private def valueRegisterName(operand: Operand): Either[LoweringError, String] = operand match {
    case Operand.Register(name, None)                     => Right(name)
    case Operand.Symbol("datum")                          => Right("datum")
    case Operand.Symbol(name) if isBareRegisterName(name) => Right(name)
    case other => invalid(s"expected register, found ${Printer.render(other)}")
  }

  private def valueDestination(operand: Operand): Either[LoweringError, (String, Option[String])] = operand match {
    case Operand.WithPredicate(base, predicate) =>
      for {
        destinationName <- valueRegisterName(base)
        predicateName   <- predicateRegisterName(predicate)
      } yield destinationName -> Some(predicateName)
    case other => valueRegisterName(other).map(_ -> None)
  }

  private def registerNames(operand: Operand): Either[LoweringError, Col[String]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[LoweringError, Col[String]]](Right(Col.empty)) { (current, item) =>
        for {
          names <- current
          name  <- registerName(item)
        } yield names :+ name
      }
    case other => registerName(other).map(Col(_))
  }

  private def vectorValues(operand: Operand): Either[LoweringError, Col[Value]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[LoweringError, Col[Value]]](Right(Col.empty)) { (current, item) =>
        for {
          values <- current
          value  <- value(item)
        } yield values :+ value
      }
    case other => value(other).map(Col(_))
  }

  private def splitRegisterNames(operand: Operand): Either[LoweringError, Col[String]] = operand match {
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[LoweringError, Col[String]]](Right(Col.empty)) { (current, item) =>
        for {
          names <- current
          name  <- item match {
            case Operand.Symbol("_") => Right("_")
            case other               => registerName(other)
          }
        } yield names :+ name
      }
    case other => registerName(other).map(Col(_))
  }

  private def requirePair[A](names: Col[A], detail: String): Either[LoweringError, Unit] =
    if (names.size == 2) Right(()) else invalid(s"$detail must contain two registers")

  private def predicateBinary(
      operation: PredicateOperation,
      destination: Operand,
      left: Operand,
      right: Operand
  ): Either[LoweringError, ExecutableInstruction] =
    for {
      destinationName <- predicateRegisterName(destination)
      leftName        <- predicateRegisterName(left)
      rightName       <- predicateRegisterName(right)
    } yield ExecutableInstruction.PredicateBinary(operation, destinationName, leftName, rightName)

  private def callParameters(operand: Option[Operand]): Either[LoweringError, Col[String]] = operand match {
    case None        => Right(Col.empty)
    case Some(value) => callParameters(value)
  }

  private def callParameters(operand: Operand): Either[LoweringError, Col[String]] = operand match {
    case Operand.Symbol(name)         => Right(Col(name))
    case Operand.Parenthesized(items) =>
      items.foldLeft[Either[LoweringError, Col[String]]](Right(Col.empty)) { (current, item) =>
        for {
          names <- current
          value <- callParameters(item)
        } yield names ++ value
      }
    case Operand.VectorLiteral(items) =>
      items.foldLeft[Either[LoweringError, Col[String]]](Right(Col.empty)) { (current, item) =>
        for {
          names <- current
          value <- callParameters(item)
        } yield names ++ value
      }
    case other => invalid(s"expected call parameter names, found ${Printer.render(other)}")
  }

  private def functionValue(
      operand: Operand,
      functionNames: Set[String],
      promoteUnknown: Boolean
  ): Either[LoweringError, Value] = operand match {
    case Operand.Symbol(name) if promoteUnknown || functionNames.contains(name) => Right(Value.Function(name))
    case Operand.Symbol(name)                                                   => Right(Value.Symbol(name))
    case other                                                                  => value(other)
  }

  private def sharedSymbols(body: Col[Statement]): Map[String, Long] = {
    val declarations = flatten(body).collect {
      case Statement.Directive(path, arguments, _) if path == Col("shared") =>
        arguments.iterator.flatMap(argument =>
          argument.operand match {
            case Operand.Symbol(name)                   => Iterator.single(name)
            case Operand.Index(Operand.Symbol(name), _) => Iterator.single(name)
            case _                                      => Iterator.empty
          }
        )
    }.flatten.distinct
    declarations.zipWithIndex.map { case (name, index) => name -> (0x10000000L + index.toLong * 0x100000L) }.toMap
  }

  private def addressValue(operand: Operand): Either[LoweringError, Value] = operand match {
    case Operand.Address(Col(single)) => valueWithOffset(single)
    case other                        => invalid(s"expected address operand, found ${Printer.render(other)}")
  }

  private def parameterAddress(operand: Operand): Either[LoweringError, (String, Int)] = operand match {
    case Operand.Address(Col(Operand.Symbol(name)))                      => Right(name -> 0)
    case Operand.Address(Col(Operand.Add(Operand.Symbol(name), offset))) =>
      immediate(offset).flatMap(value =>
        if (value >= 0L && value <= Int.MaxValue.toLong) Right(name -> value.toInt)
        else invalid(s"parameter offset is outside Int range: $value")
      )
    case other => invalid(s"expected parameter address, found ${Printer.render(other)}")
  }

  private def isDirectParameterAddress(operand: Operand): Boolean = operand match {
    case Operand.Address(Col(Operand.Symbol(_)))                 => true
    case Operand.Address(Col(Operand.Add(Operand.Symbol(_), _))) => true
    case _                                                       => false
  }

  private def valueWithOffset(operand: Operand): Either[LoweringError, Value] = operand match {
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

  private def integerWidth(modifiers: Col[Modifier]): Either[LoweringError, Int] =
    modifiers.reverseIterator.collectFirst {
      case Modifier(name) if isScalarType(name) => scalarTypeWidth(name)
    }.filter(Set(8, 16, 32, 64).contains).toRight(
      LoweringError.Invalid(
        s"expected an 8, 16, 32, or 64-bit integer type in ${modifiers.map(_.name).mkString(".")}"
      )
    )

  private def floatWidth(modifiers: Col[Modifier]): Either[LoweringError, Int] =
    modifiers.reverseIterator.collectFirst {
      case Modifier(name) if name == "f16" || name == "f32" || name == "f64" => name.drop(1).toInt
    }.toRight(LoweringError.Invalid(s"expected an f16, f32, or f64 type in ${modifiers.map(_.name).mkString(".")}"))

  private def scalarWidth(modifiers: Col[Modifier]): Either[LoweringError, Int] =
    modifiers.reverseIterator.collectFirst {
      case Modifier(name) if isScalarType(name) => scalarTypeWidth(name)
    }.filter(Set(8, 16, 32, 64).contains).toRight(
      LoweringError.Invalid(s"expected an 8, 16, 32, or 64-bit scalar type in ${modifiers.map(_.name).mkString(".")}")
    )

  private def isScalarType(name: String): Boolean =
    name == "bf16" || (name.length > 1 && "usbf".contains(name.head) && name.tail.forall(_.isDigit))

  private def scalarTypeWidth(name: String): Int = if (name == "bf16") 16 else name.drop(1).toInt

  private def value(operand: Operand): Either[LoweringError, Value] = operand match {
    case Operand.Register("%tid.x", None)                               => Right(Value.ThreadId(Axis.X))
    case Operand.Register("%tid.y", None)                               => Right(Value.ThreadId(Axis.Y))
    case Operand.Register("%tid.z", None)                               => Right(Value.ThreadId(Axis.Z))
    case Operand.Register("%ctaid.x", None)                             => Right(Value.BlockId(Axis.X))
    case Operand.Register("%ctaid.y", None)                             => Right(Value.BlockId(Axis.Y))
    case Operand.Register("%ctaid.z", None)                             => Right(Value.BlockId(Axis.Z))
    case Operand.Register("%ntid.x", None)                              => Right(Value.BlockDim(Axis.X))
    case Operand.Register("%ntid.y", None)                              => Right(Value.BlockDim(Axis.Y))
    case Operand.Register("%ntid.z", None)                              => Right(Value.BlockDim(Axis.Z))
    case Operand.Register("%nctaid.x", None)                            => Right(Value.GridDim(Axis.X))
    case Operand.Register("%nctaid.y", None)                            => Right(Value.GridDim(Axis.Y))
    case Operand.Register("%nctaid.z", None)                            => Right(Value.GridDim(Axis.Z))
    case Operand.Register("%laneid", None)                              => Right(Value.LaneId)
    case Operand.Register("%lanemask_ge", None)                         => Right(Value.LaneMaskGreaterEqual)
    case Operand.Register("%lanemask_gt", None)                         => Right(Value.LaneMaskGreater)
    case Operand.Register("%lanemask_le", None)                         => Right(Value.LaneMaskLessEqual)
    case Operand.Register("%lanemask_lt", None)                         => Right(Value.LaneMaskLess)
    case Operand.Register("%lanemask_eq", None)                         => Right(Value.LaneMaskEqual)
    case Operand.Register(name, None) if PredicateFile.index(name) >= 0 =>
      Right(Value.PredicateSlot(PredicateFile.index(name)))
    case Operand.Register(name, None) if RegisterFile.index(name) >= 0 =>
      Right(Value.RegisterSlot(RegisterFile.index(name)))
    case Operand.Register(name, None)                           => Right(Value.Register(name))
    case Operand.Symbol("datum")                                => Right(Value.Register("datum"))
    case Operand.Symbol(name) if PredicateFile.index(name) >= 0 => Right(Value.PredicateSlot(PredicateFile.index(name)))
    case Operand.Symbol(name) if isBareRegisterName(name) && RegisterFile.index(s"%$name") >= 0 =>
      Right(Value.RegisterSlot(RegisterFile.index(s"%$name")))
    case Operand.Symbol(name) if isBareRegisterName(name) => Right(Value.Register(name))
    case Operand.Symbol(name)                             => Right(Value.Symbol(name))
    case other: Operand.Immediate                         => immediate(other).map(Value.Immediate.apply)
    case Operand.Negate(value)                            => immediate(value).map(number => Value.Immediate(-number))
    case other => invalid(s"expected register or immediate, found ${Printer.render(other)}")
  }

  private def immediate(operand: Operand): Either[LoweringError, Long] = operand match {
    case Operand.Negate(value)  => immediate(value).map(number => -number)
    case Operand.Immediate(raw) =>
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
    name.matches("(?:r|rd|p)\\d*|lo|hi")

  private def isSignedScalarOpcode(opcode: String): Boolean =
    opcode.endsWith(".s8") || opcode.endsWith(".s16") || opcode.endsWith(".s32") || opcode.endsWith(".s64")

  private def invalid[A](detail: String): Either[LoweringError, A] = Left(LoweringError.Invalid(detail))
}
