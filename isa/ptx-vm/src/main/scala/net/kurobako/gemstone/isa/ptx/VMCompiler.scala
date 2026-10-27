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
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

private object VMCompiler {
  private val VectorComponentAliases = Vec(Vec("x", "r"), Vec("y", "g"), Vec("z", "b"), Vec("w", "a"))

  /** The architecture a module without `.target` is lowered for, where an instruction's arithmetic depends on it. */
  private val DefaultArchitecture: Int = 89

  /** The first offset of a frame's local storage, above the null address whose generic pointer the hardware holds in
    * the global window.
    */
  private val LocalFrameBase: Long = 16L

  import Lowering.*

  enum SourceError derives CanEqual {
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
      initialisedFunctions: Vec[NonEmpty[String]],
      architecture: Int
  )

  /** The architecture the first `.target` names; some arithmetic differs between architectures. */
  private def architecture(program: Program): Int = program.statements.collectFirst {
    case Statement.Directive(path, arguments, _, _) if path.raw == Vec("target") =>
      arguments.view.map(_.operand).collect {
        case Operand.Symbol(name) if name.raw.startsWith("sm_") => Target.parse(name.raw).toOption
      }.flatten.headOption
  }.flatten.fold(DefaultArchitecture)(_.raw)

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
      Module(
        entries,
        functions,
        table,
        shared,
        routineShared,
        initialised,
        architecture(program)
      )
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
    declaration.qualifiers.collectFirst { case DeclarationQualifier.Alignment(bytes) => bytes.toLong }
      .getOrElse(declaration.elementType.widthBits.toLong / 8L)

  private def extentBytes(declaration: Statement.Data): Long = declaration.extent match {
    case Extent.Sized(elements) => elements.toLong * declaration.elementType.byteCount
    case _                      => declaration.elementType.byteCount.toLong
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
          val address = next.alignUp(alignment(declaration))
          val end     = address + extentBytes(declaration)
          Either.cond(
            end <= VMAddressSpace.ClusterWindowBase,
            end -> (placed :+ (owner, declaration.name, address)),
            Error.Invalid(s"shared declaration '${declaration.name}' exceeds the shared address aperture")
          )
        }
      }.flatMap { (next, placed) =>
        val dynamic =
          all.flatMap((owner, values) =>
            values.collect { case value if value.extent == Extent.Unsized => owner -> value }
          )
        val dynamicAlignment = math.max(1L, dynamic.map((_, value) => alignment(value)).maxOption.getOrElse(1L))
        val base             = next.alignUp(dynamicAlignment)
        Either.cond(
          dynamic.isEmpty || base < VMAddressSpace.ClusterWindowBase,
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
          alignment = parameter.qualifiers.collectFirst { case DeclarationQualifier.Alignment(bytes) => bytes }
            .getOrElse(width).toLong
          start = offset.alignUp(alignment)
          end   = start + width.toLong
          _ <- Either.cond(
            end <= Int.MaxValue.toLong,
            (),
            Error.Invalid(s"kernel parameter ${parameter.name} layout exceeds the supported range")
          )
          pointer = parameter.qualifiers.contains(DeclarationQualifier.Attribute("ptr"))
        } yield end -> (placed :+ KernelParameter(parameter.name, start.toInt.unsafe[Int >= 0], width, pointer))
    }.map(_._2)

  private def parameterWidth(parameter: Parameter): Either[Error, Int > 0] =
    parameter.dimensions.foldLeft[Either[Error, Long]](Right(parameter.elementType.byteCount.toLong)) {
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

  /** The cluster shape `.reqnctapercluster` requires; `.explicitcluster` alone leaves the shape to the launch. */
  private def clusterDirectives(directives: Vec[Statement.Directive]): Either[Error, Option[Dimensions]] =
    directives.collectFirst {
      case Statement.Directive(path, arguments, _, _) if path.raw == Vec("reqnctapercluster") => arguments
    }.traverse { arguments =>
      arguments.traverse(argument => NumericLiteral.immediate(argument.operand)).flatMap {
        case Vec(x, y, z) if x > 0 && y > 0 && z > 0 && x * y * z <= VMInterpreter.ClusterLimit =>
          Some(Dimensions(x.toInt.unsafe[Int > 0], y.toInt.unsafe[Int > 0], z.toInt.unsafe[Int > 0]))
        case _ => None
      }.toRight(Error.Invalid("invalid .reqnctapercluster cluster shape"))
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
      cluster              <- clusterDirectives(declaration.directives)
      (functions, entries) <- compilation.lowerReachable()
    } yield Kernel(root, "PTX", functions, entries, module.functionTable, cluster)
  }

  private def lowerEntry(entry: Statement.Entry, compilation: KernelCompilation): Either[Error, Entry] =
    for {
      _          <- clusterDirectives(entry.directives)
      body       <- entry.body.toRight(Error.Invalid(s"entry ${entry.name} has no body"))
      parameters <- parameterLayout(entry.parameters)
      routine    <- lowerRoutine(
        entry.name,
        body,
        parameters.map(parameter => parameter.name -> (VMAddressSpace.ParameterBase + parameter.offset)).toMap,
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
  private def frameLocals(statements: Vec[Statement], spilled: Vec[Parameter])
      : Either[Error, (Map[NonEmpty[String], Long], Int >= 0)] =
    spilled.traverse { parameter =>
      parameterWidth(parameter).map { width =>
        val alignment = parameter.qualifiers.collectFirst { case DeclarationQualifier.Alignment(bytes) => bytes.raw }
          .getOrElse(parameter.elementType.byteCount.raw)
        (parameter.name, alignment.toLong, width.toLong)
      }
    }.flatMap { spills =>
      val data = statements.collect {
        case value: Statement.Data if value.addressSpace == DataAddressSpace.Local =>
          (value.name, alignment(value), extentBytes(value))
      }
      val (offsets, end) = (data ++ spills).foldLeft(Map.empty[NonEmpty[String], Long] -> LocalFrameBase) {
        case ((offsets, next), (name, alignment, size)) =>
          val address = next.alignUp(alignment)
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
  private enum Spill derives CanEqual { case ToStack, FromStack }

  private def spillCopy(parameter: Parameter, direction: Spill)(using
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
        Vec[ScalarBitWidth](64, 32, 16, 8).find(_.raw / 8 <= width - offset).map(part =>
          (offset.unsafe[Int >= 0], part) -> (offset + part / 8)
        )
      })
      parts.flatMap { (offset, part) =>
        val stack = Value.Immediate(local + offset)
        if (direction == Spill.ToStack)
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

  /** The labels each `.branchtargets` list names. */
  private def branchTargets(statements: Vec[Statement]): Map[NonEmpty[String], Vec[NonEmpty[String]]] =
    statements.zip(statements.drop(1)).collect {
      case (Statement.Label(name), Statement.Directive(path, arguments, _, _)) if path.raw == Vec("branchtargets") =>
        name -> arguments.collect { case DirectiveArgument(_, Operand.Symbol(label)) => label }
    }.toMap

  /** `brx.idx` as one jump: each lane takes the label its index selects, and an index outside the list, whose behaviour
    * PTX leaves undefined, falls through.
    */
  private def indexedBranch(
      index: Operand,
      table: NonEmpty[String],
      guard: Option[Predication],
      tables: Map[NonEmpty[String], Vec[NonEmpty[String]]]
  )(using scope: Scope): Either[Error, VMAssembler.Statement] =
    for {
      labels    <- tables.get(table).toRight(Error.Invalid(s"brx.idx names no .branchtargets list $table"))
      selector  <- scope.value(index)
      predicate <- guard.traverse(value => scope.predicate(value.operand).map(ExecutionPredicate(_, value.negated)))
    } yield VMAssembler.Statement.BranchTable(selector, labels, predicate)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def lowerRoutine(
      name: NonEmpty[String],
      body: Vec[Statement],
      kernelParameters: Map[NonEmpty[String], Long],
      inputs: Vec[Parameter],
      returns: Vec[Parameter],
      compilation: KernelCompilation
  ): Either[Error, (Routine, Scope)] =
    for {
      scoped <- flattenScoped(body, (kernelParameters.keySet ++ (inputs ++ returns).map(_.name)).toSet)
      tables = branchTargets(scoped)
      flat   = scoped.filter {
        case Statement.Directive(path = path) => path.raw != Vec("branchtargets")
        case _                                => true
      }
      spilled = addressTaken(flat, inputs ++ returns)
      (locals, bytes) <- frameLocals(flat, spilled)
      _ <- flat.collectFirst { case Statement.UnsupportedInstruction(opcode, operands, modifiers, predication) =>
        val qualified = (opcode +: modifiers.map(_.name.raw)).mkString(".")
        val rendered  = predication.fold("")(value => s"$value ") + qualified +
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
        compilation.module.shared ++ compilation.module.routineShared.getOrElse(name, Map.empty),
        declaredRegisters(flat).toSet,
        compilation.module.architecture
      )
      ordinals = {
        var count  = 0L
        val before = Array.ofDim[Long](flat.size)
        flat.indices.foreach { index =>
          before(index) = count
          flat(index) match {
            case Statement.Instruction(_) => count += 1
            case _                        => ()
          }
        }
        Vec.from(before)
      }
      statements <- flat.zip(ordinals).flatTraverse {
        case (Statement.Label(label), _) => Right(Vec(VMAssembler.Statement.Label(label)))
        case (Statement.Instruction(Instruction(Form.BrxIdx(index, Operand.Symbol(table)), _, guard)), ordinal) =>
          indexedBranch(index, table, guard, tables)(using scope).map(branch =>
            Vec(VMAssembler.Statement.Source(ordinal.unsafe[Long >= 0]), branch)
          )
        case (Statement.Instruction(instruction), ordinal) =>
          lowerInstruction(instruction)(using scope)
            .map(VMAssembler.Statement.Source(ordinal.unsafe[Long >= 0]) +: _).leftMap {
              case Error.Invalid(detail)        => Error.Invalid(s"${instruction.qualifiedOpcode}: $detail")
              case Error.Unsupported(_, detail) => Error.Unsupported(instruction.qualifiedOpcode, detail)
            }
        case _ => Right(Vec.empty[VMAssembler.Statement])
      }
      spillIn  <- inputs.intersect(spilled).flatTraverse(spillCopy(_, Spill.ToStack)(using scope))
      spillOut <- returns.intersect(spilled).flatTraverse(spillCopy(_, Spill.FromStack)(using scope))
      program  <- VMAssembler.assemble(spillIn.map(VMAssembler.Statement.Execute(_)) ++ statements.flatMap {
        case VMAssembler.Statement.Execute(VMIR.Instruction.Return, predication) =>
          spillOut.map(VMAssembler.Statement.Execute(_, predication)) :+
            VMAssembler.Statement.Execute(VMIR.Instruction.Return, predication)
        case statement => Vec(statement)
      }).leftMap(Error.Invalid(_))
    } yield {
      scope.directCalls.foreach(compilation.reference(_))
      scope.addressTaken.foreach(compilation.reference(_))
      Routine(
        name = name,
        program = program,
        registerCount = scope.registerCount,
        predicateCount = scope.predicateCount,
        localBytes = bytes,
        predicateNames = scope.predicateNames,
        registerNames = scope.registerNames
      ) -> scope
    }

  private def lowerInstruction(instruction: Instruction)(using
      scope: Scope
  ): Either[Error, Vec[VMAssembler.Statement]] = {
    val modifiers = instruction.signature.modifiers
    for {
      _ <- modifiers.collectFirst {
        case Modifier.Unrecognised(spelling) if !VMTensorMemoryCompiler.Spellings(spelling.raw) => spelling
      }
        .toLeft(()).leftMap(spelling =>
          Error.Unsupported(instruction.qualifiedOpcode, s"unknown modifier: ${spelling.raw}")
        )
      _         <- VMInstructionCompiler.validateFloating(instruction)
      predicate <- instruction.pred.traverse(value =>
        scope.predicate(value.operand).map(ExecutionPredicate(_, value.negated))
      )
      lowered <- instruction.form match {
        case Form.Bra(target) if modifiers.isEmpty || modifiers == Vec(Modifier.Uni) =>
          target match {
            case Operand.Symbol(label) => Right(Vec(VMAssembler.Statement.Branch(label, predicate)))
            case other                 => invalid(s"expected branch label, found $other")
          }
        case _ =>
          VMSequenceCompiler.lower(instruction, modifiers)
            .getOrElse(VMInstructionCompiler.lowerOperation(instruction, modifiers).map(Vec(_)))
            .map(_.map(VMAssembler.Statement.Execute(_, predicate)))
      }
    } yield lowered
  }

  private def declaredParameters(statements: Vec[Statement]): Vec[NonEmpty[String]] = statements.flatMap {
    case Statement.Directive(path, arguments, _, _) if path.head.raw == "param" =>
      arguments.flatMap(argument => declaredOperandNames(argument.operand))
    case _ => Vec.empty[NonEmpty[String]]
  }

  private def declaredRegisters(statements: Vec[Statement]): Vec[NonEmpty[String]] = statements.flatMap {
    case Statement.Directive(path, arguments, _, _) if path.head.raw == "reg" =>
      arguments.flatMap(argument => declaredOperandNames(argument.operand).flatMap(expandRange(_)))
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
            val aliases   = VectorComponentAliases.take(components)
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
          instruction.decoder(instruction.operands.map(rename(_, names)), instruction.mods).bimap(
            error => Error.Invalid(s"failed to preserve scoped instruction ${instruction.qualifiedOpcode}: $error"),
            form =>
              Vec(Statement.Instruction(instruction.copy(
                form = form,
                pred = instruction.pred.map(value => value.copy(operand = rename(value.operand, names)))
              )))
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
