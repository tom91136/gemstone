package net.kurobako.gemstone.isa.ptx

import Program.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as _, Parameter as _, *}
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.text.{SourceError as SyntaxError, *}
import scala.collection.mutable

private object VMCompiler {
  import Lowering.*

  enum SourceError {
    case Syntax(error: SyntaxError)
    case MissingEntry
    case Compilation(error: Error)

    def message: NonEmpty[String] = this match {
      case Syntax(error)      => error.diagnostic
      case MissingEntry       => "missing .entry declaration"
      case Compilation(error) => error.message
    }
  }

  /** Program-wide facts shared by every kernel compiled from one linked module. Shared variables, whether declared at
    * module scope or inside a routine, have one packed layout with dynamic shared storage after every sized variable.
    */
  case class Module(
      entries: Map[NonEmpty[String], Statement.Entry],
      functions: Map[NonEmpty[String], Statement.Function],
      functionTable: FunctionTable,
      shared: Map[NonEmpty[String], Long],
      routineShared: Map[NonEmpty[String], Map[NonEmpty[String], Long]],
      initialisedFunctions: Vec[NonEmpty[String]]
  )

  def module(program: Program): Either[Error, Module] = {
    val entries = program.statements.collect { case entry: Statement.Entry => entry }
      .groupMapReduce(_.name)(identity)(Linker.preferDefinition(_, _))
    val functions = program.statements.collect { case function: Statement.Function => function }
      .groupMapReduce(_.name)(identity)(Linker.preferDefinition(_, _))
    val table  = FunctionTable(functions.keySet ++ entries.keySet)
    val module = program.statements.collect {
      case value: Statement.Data if value.addressSpace == DataAddressSpace.Shared => value
    }.groupMapReduce(_.name)(identity)(Linker.preferDefinition(_, _)).values.toVec.sortBy(_.name.raw)
    val routines = (entries.view.mapValues(_.body) ++ functions.view.mapValues(_.body)).toVec.sortBy(_._1.raw)
      .map((name, body) => name -> body.fold(Vec.empty[Statement.Data])(sharedDeclarations(_)))
    val initialised = program.statements.collect {
      case value: Statement.Data if value.addressSpace != DataAddressSpace.Shared => value.initialiser
    }.flatten.flatMap(expressionSymbols(_)).filter(table.address(_).nonEmpty).distinct
    SharedLayout(module, routines).map((shared, routineShared) =>
      Module(entries, functions, table, shared, routineShared, initialised)
    )
  }

  /** Symbols used as values in an expression. Memory operands and call argument lists name storage rather than use its
    * address, so they are not descended.
    */
  private def expressionSymbols(operand: Operand): Vec[NonEmpty[String]] = operand match {
    case Operand.Symbol(name)           => Vec(name)
    case Operand.GenericAddress(symbol) => expressionSymbols(symbol)
    case Operand.Add(left, right)       => expressionSymbols(left) ++ expressionSymbols(right)
    case Operand.Subtract(left, right)  => expressionSymbols(left) ++ expressionSymbols(right)
    case Operand.Qualified(base, _)     => expressionSymbols(base)
    case Operand.VectorLiteral(items)   => items.flatMap(expressionSymbols(_))
    case _                              => Vec.empty[NonEmpty[String]]
  }

  /** Names introduced by the arguments of a declaration directive such as `.reg` or `.param`. */
  private def declaredOperandNames(operand: Operand): Vec[NonEmpty[String]] = operand match {
    case Operand.Register(name, _)    => Vec(name)
    case Operand.Symbol(name)         => Vec(name)
    case Operand.Index(base, _)       => declaredOperandNames(base)
    case Operand.Qualified(base, _)   => declaredOperandNames(base)
    case Operand.VectorLiteral(items) => items.flatMap(declaredOperandNames(_))
    case Operand.Parenthesized(items) => items.flatMap(declaredOperandNames(_))
    case _                            => Vec.empty[NonEmpty[String]]
  }

  private def sharedDeclarations(body: Vec[Statement]): Vec[Statement.Data] = body.flatMap {
    case Statement.Block(nested)                                                => sharedDeclarations(nested)
    case value: Statement.Data if value.addressSpace == DataAddressSpace.Shared => Vec(value)
    case _                                                                      => Vec.empty[Statement.Data]
  }.distinctBy(_.name)

  private def alignment(declaration: Statement.Data): Long =
    declaration.qualifiers.collectFirst { case DeclarationQualifier.Alignment(bytes) => bytes.raw.toLong }
      .getOrElse(declaration.elementType.widthBits.raw.toLong / 8L)

  private def aligned(address: Long, alignment: Long): Long = address + (alignment - address % alignment) % alignment

  private def extentBytes(declaration: Statement.Data): Long = declaration.extent match {
    case Extent.Sized(elements) => elements.raw.toLong * declaration.elementType.byteCount.raw
    case _                      => declaration.elementType.byteCount.raw.toLong
  }

  private object SharedLayout {
    def apply(
        module: Vec[Statement.Data],
        routines: Vec[(NonEmpty[String], Vec[Statement.Data])]
    ): Either[Error, (Map[NonEmpty[String], Long], Map[NonEmpty[String], Map[NonEmpty[String], Long]])] = {
      val owners       = (None -> module) +: routines.map((name, declarations) => Some(name) -> declarations)
      val (sized, all) =
        (
          owners.flatMap((owner, values) =>
            values.collect { case value if value.extent != Extent.Unsized => owner -> value }
          ),
          owners
        )
      sized.foldLeft[Either[Error, (Long, Vec[(Option[NonEmpty[String]], NonEmpty[String], Long)])]](
        Right(VMAddressSpace.SharedBase -> Vec.empty)
      ) { case (layout, (owner, declaration)) =>
        layout.flatMap { (next, placed) =>
          val address = aligned(next, alignment(declaration))
          val end     = address + extentBytes(declaration)
          Either.cond(
            end <= VMAddressSpace.SharedLimit,
            end -> (placed :+ (owner, declaration.name, address)),
            Error.Invalid(s"shared declaration '${declaration.name}' exceeds the shared address aperture")
          )
        }
      }.flatMap { (next, placed) =>
        val dynamic =
          all.flatMap((owner, values) =>
            values.collect { case value if value.extent == Extent.Unsized => owner -> value }
          )
        val dynamicAlignment = dynamic.foldLeft(1L)((current, value) => math.max(current, alignment(value._2)))
        val base             = aligned(next, dynamicAlignment)
        Either.cond(
          dynamic.isEmpty || base < VMAddressSpace.SharedLimit,
          placed ++ dynamic.map((owner, declaration) => (owner, declaration.name, base)),
          Error.Invalid("dynamic shared storage exceeds the shared address aperture")
        )
      }.map { placements =>
        val byOwner = placements.groupMap(_._1)(placement => placement._2 -> placement._3).view.mapValues(_.toMap)
        byOwner.getOrElse(None, Map.empty) ->
          routines.map((name, _) => name -> byOwner.getOrElse(Some(name), Map.empty)).toMap
      }
    }
  }

  def compile(source: String): Either[SourceError, Kernel] =
    Parser.parse(source).leftMap(SourceError.Syntax(_)).flatMap { program =>
      program.statements.collectFirst { case entry: Statement.Entry if entry.body.nonEmpty => entry.name }
        .toRight(SourceError.MissingEntry)
        .flatMap(entry =>
          module(program).flatMap(compile(_, entry, Map.empty)).leftMap(SourceError.Compilation(_))
        )
    }

  /** Kernel parameters in declaration order with their ABI offsets in the parameter block. */
  def parameterLayout(parameters: Vec[Parameter]): Either[Error, Vec[KernelParameter]] =
    parameters.foldLeft[Either[Error, (Long, Vec[KernelParameter])]](Right(0L -> Vec.empty)) {
      case (layout, parameter) =>
        for {
          (offset, placed) <- layout
          width            <- parameterWidth(parameter)
          alignment = parameter.qualifiers.collectFirst { case DeclarationQualifier.Alignment(bytes) => bytes.raw }
            .getOrElse(width.raw).toLong
          start = aligned(offset, alignment)
          end   = start + width.raw.toLong
          _ <- Either.cond(
            end <= Int.MaxValue.toLong,
            (),
            Error.Invalid(s"kernel parameter ${parameter.name} layout exceeds the supported range")
          )
          pointer = parameter.qualifiers.contains(DeclarationQualifier.Attribute("ptr"))
        } yield end -> (placed :+ KernelParameter(parameter.name, start.toInt.unsafe[Int >= 0], width, pointer))
    }.map(_._2)

  private def parameterWidth(parameter: Parameter): Either[Error, Int > 0] =
    parameter.dimensions.foldLeft[Either[Error, Long]](Right(parameter.elementType.byteCount.raw.toLong)) {
      (width, dimension) =>
        for {
          current  <- width
          elements <- dimension.toRight(Error.Invalid(s"kernel parameter ${parameter.name} has an unsized dimension"))
            .flatMap(operand => NumericLiteral.immediate(operand).toRight(Error.Invalid(s"invalid dimension $operand")))
          _ <- Either.cond(
            elements > 0 && current <= Int.MaxValue.toLong / elements,
            (),
            Error.Invalid(s"kernel parameter ${parameter.name} has an invalid array dimension: $elements")
          )
        } yield current * elements
    }.flatMap(_.toInt.refine[Int > 0].left.map(_ => Error.Invalid(s"kernel parameter ${parameter.name} is too wide")))

  private def clusterDirectives(directives: Vec[Statement.Directive]): Either[Error, Unit] = directives.traverse_ {
    case Statement.Directive(path, arguments, _, _) if path.raw == Vec("reqnctapercluster") =>
      arguments.traverse(argument => NumericLiteral.immediate(argument.operand)).filter(_ == Vec(1L, 1L, 1L))
        .toRight(Error.Invalid("multi-workgroup PTX clusters are not supported")).map(_ => ())
    case Statement.Directive(path = path) if path.raw == Vec("explicitcluster") =>
      invalid("PTX kernels requiring an explicit cluster launch are not supported")
    case _ => Right(())
  }

  /** One kernel compilation: lowers the entry and every routine it can reach. */
  class KernelCompilation(val module: Module, val globals: Map[NonEmpty[String], GuestAddress]) {
    private val pending   = mutable.Queue.empty[NonEmpty[String]]
    private val seen      = mutable.Set.empty[NonEmpty[String]]
    private val functions = mutable.LinkedHashMap.empty[NonEmpty[String], DeviceFunction]
    private val entries   = mutable.LinkedHashMap.empty[NonEmpty[String], Entry]

    def reference(name: NonEmpty[String]): Unit = if (seen.add(name)) pending.enqueue(name)

    // Fail on the first invalid callee while draining the reachable call graph.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def lowerReachable(): Either[Error, (Map[NonEmpty[String], DeviceFunction], Map[NonEmpty[String], Entry])] = {
      var failure: Option[Error] = None
      while (pending.nonEmpty && failure.isEmpty) {
        val name    = pending.dequeue()
        val lowered = module.functions.get(name) match {
          case Some(function) if function.body.nonEmpty => lowerFunction(function, this).map(functions(name) = _)
          case Some(_)                                  => Right(())
          case None                                     =>
            module.entries.get(name).filter(_.body.nonEmpty).traverse_(entry =>
              lowerEntry(entry, this).map(entries(name) = _)
            )
        }
        failure = lowered.left.toOption
      }
      failure.toLeft(functions.toMap -> entries.toMap)
    }
  }

  def compile(module: Module, entry: NonEmpty[String], globals: Map[NonEmpty[String], GuestAddress])
      : Either[Error, Kernel] = {
    val compilation = KernelCompilation(module, globals)
    module.initialisedFunctions.foreach(compilation.reference(_))
    for {
      declaration          <- module.entries.get(entry).toRight(Error.Invalid(s"missing entry $entry"))
      root                 <- lowerEntry(declaration, compilation)
      (functions, entries) <- compilation.lowerReachable()
    } yield Kernel(root, "PTX", functions, entries, module.functionTable)
  }

  private def lowerEntry(entry: Statement.Entry, compilation: KernelCompilation): Either[Error, Entry] =
    for {
      _          <- clusterDirectives(entry.directives)
      body       <- entry.body.toRight(Error.Invalid(s"entry ${entry.name} has no body"))
      parameters <- parameterLayout(entry.parameters)
      routine    <- lowerRoutine(
        entry.name,
        body,
        parameters.map(parameter => parameter.name -> (VMAddressSpace.ParameterBase + parameter.offset.raw)).toMap,
        Vec.empty,
        Vec.empty,
        compilation
      )
    } yield Entry(routine._1, parameters)

  private def lowerFunction(
      function: Statement.Function,
      compilation: KernelCompilation
  ): Either[Error, DeviceFunction] =
    for {
      body    <- function.body.toRight(Error.Invalid(s"function ${function.name} has no body"))
      _       <- (function.parameters ++ function.returnParameters).traverse_(parameterWidth(_))
      lowered <- lowerRoutine(
        function.name,
        body,
        Map.empty,
        function.parameters,
        function.returnParameters,
        compilation
      )
      (routine, scope) = lowered
      parameters <- function.parameters.traverse(parameter => scope.parameter(parameter.name))
      returns    <- function.returnParameters.traverse(parameter => scope.parameter(parameter.name))
    } yield DeviceFunction(routine, parameters, returns)

  /** Frame-local offsets of `.local` variables followed by the parameters whose address is taken. */
  // Runs once per routine at compile time, and the offsets are Map values either way.
  @scala.annotation.nowarn("msg=baumkuchen:loop-boxing")
  private def frameLocals(statements: Vec[Statement], spilled: Vec[Parameter])
      : Either[Error, (Map[NonEmpty[String], Long], Int >= 0)] =
    spilled.traverse { parameter =>
      parameterWidth(parameter).map { width =>
        val alignment = parameter.qualifiers.collectFirst { case DeclarationQualifier.Alignment(bytes) => bytes.raw }
          .getOrElse(parameter.elementType.byteCount.raw)
        (parameter.name, alignment.toLong, width.raw.toLong)
      }
    }.flatMap { spills =>
      val data = statements.collect {
        case value: Statement.Data if value.addressSpace == DataAddressSpace.Local =>
          (value.name, alignment(value), extentBytes(value))
      }
      val (offsets, end) = (data ++ spills).foldLeft(Map.empty[NonEmpty[String], Long] -> 0L) {
        case ((offsets, next), (name, alignment, size)) =>
          val address = aligned(next, alignment)
          (offsets.updated(name, address), address + size)
      }
      Option.when(end <= Int.MaxValue.toLong)(end.toInt).flatMap(_.refine[Int >= 0].toOption)
        .toRight(Error.Invalid("local storage exceeds the supported range")).map(offsets -> _)
    }

  /** Function parameters used as a value rather than through `[name]`: PTX copies these to the stack and their address
    * is a `.local` address.
    */
  private def addressTaken(statements: Vec[Statement], parameters: Vec[Parameter]): Vec[Parameter] = {
    val names = statements.flatMap {
      case Statement.Instruction(instruction) => instruction.operands.flatMap(expressionSymbols(_))
      case _                                  => Vec.empty[NonEmpty[String]]
    }.toSet
    parameters.filter(parameter => names(parameter.name))
  }

  /** Copies between a spilled parameter and its stack slot in at most 8-byte parts. */
  private def spillCopy(parameter: Parameter, toStack: Boolean)(using
      scope: Scope
  )
      : Either[Error, Vec[VMIR.Instruction]] =
    for {
      width     <- parameterWidth(parameter)
      slot      <- scope.parameter(parameter.name)
      temporary <- scope.register(Operand.Register("spill temporary", None))
      local     <- scope.spilledParameter(parameter.name).toRight(Error.Invalid(s"${parameter.name} is not spilled"))
    } yield {
      val parts = Vec.from(Iterator.unfold(0) { offset =>
        Vec[ScalarBitWidth](64, 32, 16, 8).find(_.raw / 8 <= width.raw - offset).map(part =>
          (offset.unsafe[Int >= 0], part) -> (offset + part.raw / 8)
        )
      })
      parts.flatMap { (offset, part) =>
        val stack = Value.Immediate(local + offset.raw)
        if (toStack)
          Vec(
            VMIR.Instruction.LoadParameter(Vec(temporary), slot, offset, part, false),
            VMIR.Instruction.Store(stack, Value.Register(temporary), part, MemoryAccess.Local)
          )
        else
          Vec(
            VMIR.Instruction.Load(temporary, stack, part, signed = false, MemoryAccess.Local),
            VMIR.Instruction.StoreParameter(slot, offset, Value.Register(temporary), part)
          )
      }
    }

  private def lowerRoutine(
      name: NonEmpty[String],
      body: Vec[Statement],
      kernelParameters: Map[NonEmpty[String], Long],
      inputs: Vec[Parameter],
      returns: Vec[Parameter],
      compilation: KernelCompilation
  ): Either[Error, (Routine, Scope)] =
    for {
      flat <- flattenScoped(body, (kernelParameters.keySet ++ (inputs ++ returns).map(_.name)).toSet)
      spilled = addressTaken(flat, inputs ++ returns)
      (locals, bytes) <- frameLocals(flat, spilled)
      _ <- flat.collectFirst { case Statement.UnsupportedInstruction(opcode, operands, modifiers, predication) =>
        val qualified = (opcode +: modifiers.map(_.name.raw)).mkString(".")
        val rendered  = predication.map(value => s"$value ").getOrElse("") + qualified +
          (if (operands.isEmpty) "" else operands.mkString(" ", ", ", "")) + ";"
        Error.Unsupported(rendered.unsafe[NonEmpty[String]], "instruction is not implemented")
      }.toLeft(())
      scope = Scope(
        compilation.module.functionTable,
        compilation.module.functions.filter(_._2.body.nonEmpty).keySet,
        compilation.globals,
        kernelParameters,
        ((inputs ++ returns).map(_.name) ++ declaredParameters(flat)).toSet,
        spilled.map(_.name).toSet,
        locals,
        compilation.module.shared ++ compilation.module.routineShared.getOrElse(name, Map.empty)
      )
      ordinals = flat.scanLeft(0L) {
        case (count, Statement.Instruction(_)) => count + 1
        case (count, _)                        => count
      }
      statements <- flat.zip(ordinals).flatTraverse {
        case (Statement.Label(label), _)                   => Right(Vec(VMAssembler.Statement.Label(label)))
        case (Statement.Instruction(instruction), ordinal) =>
          VMInstructionCompiler.lower(instruction)(using scope)
            .map(lowered => Vec(VMAssembler.Statement.Source(ordinal.unsafe[Long >= 0]), lowered)).leftMap {
              case Error.Invalid(detail)        => Error.Invalid(s"${instruction.qualifiedOpcode}: $detail")
              case Error.Unsupported(_, detail) => Error.Unsupported(instruction.qualifiedOpcode, detail)
            }
        case _ => Right(Vec.empty[VMAssembler.Statement])
      }
      spillIn  <- inputs.intersect(spilled).flatTraverse(spillCopy(_, toStack = true)(using scope))
      spillOut <- returns.intersect(spilled).flatTraverse(spillCopy(_, toStack = false)(using scope))
      program  <- VMAssembler.assemble(spillIn.map(VMAssembler.Statement.Execute(_)) ++ statements.flatMap {
        case VMAssembler.Statement.Execute(VMIR.Instruction.Return, predication) =>
          spillOut.map(VMAssembler.Statement.Execute(_, predication)) :+
            VMAssembler.Statement.Execute(VMIR.Instruction.Return, predication)
        case statement => Vec(statement)
      }).leftMap(Error.Invalid(_))
    } yield {
      scope.directCalls.foreach(compilation.reference(_))
      scope.addressTaken.foreach(compilation.reference(_))
      Routine(name, program, scope.registerCount, scope.predicateCount, bytes, scope.predicateNames) -> scope
    }

  private def declaredParameters(statements: Vec[Statement]): Vec[NonEmpty[String]] = statements.flatMap {
    case Statement.Directive(path, arguments, _, _) if path.head.raw == "param" =>
      arguments.flatMap(argument => declaredOperandNames(argument.operand))
    case _ => Vec.empty[NonEmpty[String]]
  }

  private def expandRange(name: NonEmpty[String]): Vec[NonEmpty[String]] = name.raw match {
    case s"$base<$count>" if base.nonEmpty && count.toIntOption.exists(_ > 0) =>
      Vec.tabulate(count.toInt)(index => s"$base$index".unsafe[NonEmpty[String]])
    case _ => Vec(name)
  }

  /** Resolves declared parameter names and alpha-renames block-local storage before flattening PTX scopes. */
  // The nested visitor shares one monotonically increasing scope identity.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def flattenScoped(
      statements: Vec[Statement],
      parameters: Set[NonEmpty[String]]
  ): Either[Error, Vec[Statement]] = {
    var nextScope = 0

    def declaredNames(body: Vec[Statement], kind: String): Vec[NonEmpty[String]] = body.flatMap {
      case Statement.Directive(path, arguments, _, _) if path.head.raw == kind =>
        arguments.flatMap(argument => declaredOperandNames(argument.operand))
      case _ => Vec.empty[NonEmpty[String]]
    }.flatMap(expandRange(_)).distinct

    def registerBindings(body: Vec[Statement], scope: Option[Int]): Map[NonEmpty[String], Operand] =
      body.collect { case value: Statement.Directive if value.path.head.raw == "reg" => value }.flatMap { declaration =>
        val qualifiers = declaration.path.raw ++ declaration.arguments.flatMap {
          case DirectiveArgument(_, Operand.Qualifier(path)) => path.raw
          case _                                             => Vec.empty[NonEmpty[String]]
        }
        val components =
          if (qualifiers.contains("v2") || qualifiers.contains("b128")) 2 else if (qualifiers.contains("v4")) 4 else 1
        declaredNames(Vec(declaration), "reg").flatMap { name =>
          val scoped: NonEmpty[String] = scope.fold(name)(index => s"$name$$scope$index")
          if (components == 1) Vec(name -> Operand.Register(scoped, None))
          else {
            val aliases   = Vec(Vec("x", "r"), Vec("y", "g"), Vec("z", "b"), Vec("w", "a")).take(components)
            val registers = aliases.map(fields => Operand.Register(s"$scoped.${fields.head}", None))
            Vec(name -> Operand.VectorLiteral(registers)) ++ aliases.zip(registers).flatMap { (fields, register) =>
              fields.map(field => (s"$name.$field": NonEmpty[String]) -> register)
            }
          }
        }
      }.toMap

    def rename(operand: Operand, names: Map[NonEmpty[String], Operand]): Operand = operand match {
      case Operand.Register(name, part) =>
        part.flatMap(part => names.get(s"$name.${part.spelling}")).getOrElse(names.get(name) match {
          case Some(Operand.Register(renamed, _)) => Operand.Register(renamed, part)
          case Some(value) if part.isEmpty        => value
          case _                                  => operand
        })
      case Operand.Symbol(name) => names.getOrElse(name, operand)
      case other                => other.mapChildren(rename(_, names))
    }

    def visit(body: Vec[Statement], names: Map[NonEmpty[String], Operand]): Either[Error, Vec[Statement]] =
      body.flatTraverse {
        case Statement.Block(nested) =>
          val scope = nextScope
          nextScope += 1
          val scoped = declaredNames(nested, "param").foldLeft(names ++ registerBindings(nested, Some(scope))) {
            (current, name) => current.updated(name, Operand.Symbol(s"$name$$scope$scope"))
          }
          visit(nested, scoped)
        case directive: Statement.Directive if directive.path.head.raw == "param" =>
          Right(Vec(directive.copy(arguments =
            directive.arguments.map(argument =>
              argument.copy(operand = argument.operand match {
                case Operand.Register(name, None) if expandRange(name).size > 1 =>
                  Operand.VectorLiteral(expandRange(name).map(part => rename(Operand.Register(part, None), names)))
                case value => rename(value, names)
              })
            )
          )))
        case Statement.Instruction(instruction) =>
          instruction.decoder(
            instruction.operands.map(rename(_, names)),
            instruction.mods,
            instruction.pred.map(value => value.copy(operand = rename(value.operand, names)))
          ).bimap(
            error => Error.Invalid(s"failed to preserve scoped instruction ${instruction.qualifiedOpcode}: $error"),
            value => Vec(Statement.Instruction(value))
          )
        case Statement.UnsupportedInstruction(opcode, operands, modifiers, predication) =>
          Right(Vec(Statement.UnsupportedInstruction(
            opcode,
            operands.map(rename(_, names)),
            modifiers,
            predication.map(value => value.copy(operand = rename(value.operand, names)))
          )))
        case value => Right(Vec(value))
      }

    val parameterNames = parameters ++ declaredNames(statements, "param")
    visit(statements, registerBindings(statements, None) ++ parameterNames.map(name => name -> Operand.Symbol(name)))
  }
}
