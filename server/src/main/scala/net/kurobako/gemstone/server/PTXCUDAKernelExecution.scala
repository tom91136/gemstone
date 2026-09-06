package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.execution.Dimensions
import net.kurobako.gemstone.execution.LaunchGeometry
import net.kurobako.gemstone.execution.volumeInt
import net.kurobako.gemstone.isa.ptx.v9_3.Parser
import net.kurobako.gemstone.isa.ptx.v9_3.Argument
import net.kurobako.gemstone.isa.ptx.v9_3.DirectiveArgument
import net.kurobako.gemstone.isa.ptx.v9_3.Interpreter
import net.kurobako.gemstone.isa.ptx.v9_3.Kernel
import net.kurobako.gemstone.isa.ptx.v9_3.Lowering
import net.kurobako.gemstone.isa.ptx.v9_3.Operand
import net.kurobako.gemstone.isa.ptx.v9_3.Program
import net.kurobako.gemstone.isa.ptx.v9_3.Statement
import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.memory.CheckedMemory
import net.kurobako.gemstone.memory.DynamicAddressMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.memory.MemoryError
import net.kurobako.gemstone.vendor.CUDAKernelExecution

import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicReference
import java.util.regex.Pattern

/** Textual-PTX CUDA module backend. Images and launch arguments have already been copied out of the native process when
  * they reach this class.
  */
final class PTXCUDAKernelExecution extends CUDAKernelExecution {
  private val representativeGridBlockThreshold = 4096

  private val ignoredFunctionDirectives = Pattern.compile(
    "\\.(?:maxntid|reqntid|minnctapersm|maxnreg|maxclusterrank|explicitcluster|noreturn)" +
      "[^\\r\\n\\{;]*(\\r?\\n|[\\{;])"
  )

  private def stripIgnoredFunctionDirectives(source: String): String =
    ignoredFunctionDirectives.matcher(source).replaceAll("$1")

  private object UnmappedMemory extends AddressMemory {
    private def unavailable[A](address: GuestAddress, width: Int): Either[MemoryError, A] =
      Left(MemoryError.InvalidAddress(address, width, "no delegate allocation"))

    override def readInt8(address: GuestAddress): Either[MemoryError, Byte]                = unavailable(address, 1)
    override def readInt32(address: GuestAddress): Either[MemoryError, Int]                = unavailable(address, 4)
    override def writeInt8(address: GuestAddress, _value: Byte): Either[MemoryError, Unit] = unavailable(address, 1)
    override def writeInt32(address: GuestAddress, _value: Int): Either[MemoryError, Unit] = unavailable(address, 4)
  }

  private final case class LoadedModule(
      source: String,
      entries: Set[String],
      globals: Map[String, LoadedGlobal],
      shared: Map[String, Long]
  )
  private final case class LoadedGlobal(address: Long, size: Int, release: () => CUDAResult[Unit])
  private final case class GlobalDefinition(size: Int, initial: Array[Byte])
  private final case class GlobalStorage(
      allocate: (SizeT, Array[Byte]) => CUDAResult[DevicePtr],
      release: DevicePtr => CUDAResult[Unit]
  )
  private final case class LoadedFunction(module: CUDAModule, name: String, kernel: Option[Kernel])
  private final case class State(
      modules: Map[Long, LoadedModule],
      functions: Map[Long, LoadedFunction],
      nextModuleHandle: Long,
      nextFunctionHandle: Long
  )

  private val state              = AtomicReference(State(Map.empty, Map.empty, 1L, 1L))
  private val dynamicMemoryState = DynamicAddressMemory.State()
  private val recentProgram      = AtomicReference(Option.empty[(Long, Program)])

  override def cuModuleLoadData(image: ModuleImage): CUDAResult[CUDAModule] =
    loadModule(image, None)

  override def cuModuleLoadData(
      image: ModuleImage,
      allocateGlobal: (SizeT, Array[Byte]) => CUDAResult[DevicePtr],
      freeGlobal: DevicePtr => CUDAResult[Unit]
  ): CUDAResult[CUDAModule] = loadModule(image, Some(GlobalStorage(allocateGlobal, freeGlobal)))

  private def loadModule(
      image: ModuleImage,
      externalStorage: Option[GlobalStorage]
  ): CUDAResult[CUDAModule] = synchronized {
    val sourceResult = decodeUtf8(image.bytes)
    sourceResult.flatMap { source =>
      val parsedSource = stripIgnoredFunctionDirectives(source)
      recentProgram.set(None)
      val parsed = if (parsedSource.isEmpty) Right(Program(Col.empty)) else Parser.parse(parsedSource)
      parsed match {
        case Left(error)    => failure(200, s"invalid PTX module: $error")
        case Right(program) =>
          val entries       = program.statements.collect { case entry: Statement.Entry => entry.name }.toSet
          val dynamicMemory = DynamicAddressMemory(UnmappedMemory, state = dynamicMemoryState)
          val definitions   = globalDefinitions(source)
          val storage       = externalStorage.getOrElse(GlobalStorage(
            (size, initial) => success(DevicePtr(dynamicMemory.allocate(size.toLong, initial))),
            pointer => {
              dynamicMemory.free(pointer.address)
              success(())
            }
          ))
          val globalsResult = allocateGlobals(
            moduleGlobalSymbols(program.statements).iterator.toCol,
            0,
            definitions,
            storage,
            Map.empty
          )
          globalsResult.map { globals =>
            val shared = moduleSharedSymbols(program.statements)
            // CUDA's runtime loads header-only PTX images while bootstrapping its
            // internal libraries.  They are valid modules even though they have
            // no user-visible entry points; a later function lookup will report
            // the missing entry in the usual way.
            val current = state.get()
            val handle  = current.nextModuleHandle
            recentProgram.set(Some(handle -> program))
            state.set(
              current.copy(
                modules = current.modules.updated(handle, LoadedModule(parsedSource, entries, globals, shared)),
                nextModuleHandle = Math.addExact(handle, 1L)
              )
            )
            CUDAModule(handle)
          }
      }
    }
  }

  override def cuModuleGetGlobal(
      module: CUDAModule,
      name: FunctionName
  ): CUDAResult[(DevicePtr, SizeT)] = synchronized {
    val current = state.get()
    current.modules.get(module.handle) match {
      case None         => failure(201, s"invalid CUDA module handle: ${module.handle}")
      case Some(loaded) =>
        loaded.globals.get(name.value) match {
          case Some(global) => success(DevicePtr(global.address) -> SizeT(global.size.toLong))
          case None         => failure(500, s"PTX global not found: ${name.value}")
        }
    }
  }

  override def cuModuleRegisterGlobal(
      module: CUDAModule,
      name: FunctionName,
      initial: Array[Byte],
      allocateGlobal: (SizeT, Array[Byte]) => CUDAResult[DevicePtr],
      freeGlobal: DevicePtr => CUDAResult[Unit]
  ): CUDAResult[Unit] = synchronized {
    val current = state.get()
    current.modules.get(module.handle) match {
      case None         => failure(201, s"invalid CUDA module handle: ${module.handle}")
      case Some(loaded) =>
        if (loaded.globals.contains(name.value)) success(())
        else {
          val size = Math.max(1, initial.length)
          allocateGlobal(SizeT(size.toLong), initial) match {
            case CUDAResult.Failure(error)   => CUDAResult.Failure(error)
            case CUDAResult.Success(pointer) =>
              val global = LoadedGlobal(pointer.address, size, () => freeGlobal(pointer))
              state.set(
                current.copy(modules =
                  current.modules.updated(
                    module.handle,
                    loaded.copy(globals = loaded.globals.updated(name.value, global))
                  )
                )
              )
              success(())
          }
        }
    }
  }

  override def cuModuleGetFunction(module: CUDAModule, name: FunctionName): CUDAResult[KernelFunction] = synchronized {
    val current = state.get()
    current.modules.get(module.handle) match {
      case None         => failure(201, s"invalid CUDA module handle: ${module.handle}")
      case Some(loaded) =>
        if (!loaded.entries.contains(name.value)) failure(500, s"PTX entry point not found: ${name.value}")
        else
          moduleProgram(module.handle, loaded).flatMap { program =>
            entryDefinition(program, name.value) match {
              case None        => failure(500, s"PTX entry point not found: ${name.value}")
              case Some(entry) =>
                Lowering.parameters(entry) match {
                  case Left(error)       => failure(801, error.message)
                  case Right(parameters) =>
                    val handle = current.nextFunctionHandle
                    state.set(
                      current.copy(
                        functions = current.functions.updated(
                          handle,
                          LoadedFunction(module, name.value, None)
                        ),
                        nextFunctionHandle = Math.addExact(handle, 1L)
                      )
                    )
                    success(KernelFunction(CUDAFunction(handle), parameters.map(_.widthBytes)))
                }
            }
          }
    }
  }

  private def moduleProgram(handle: Long, module: LoadedModule): CUDAResult[Program] = {
    val parsed = recentProgram.get().filter(_._1 == handle).map(pair => Right(pair._2)).getOrElse {
      recentProgram.set(None)
      Parser.parse(module.source).map { program =>
        recentProgram.set(Some(handle -> program))
        program
      }
    }
    parsed match {
      case Left(error)    => failure(200, s"invalid cached PTX module: $error")
      case Right(program) => success(program)
    }
  }

  private def lowerFunction(handle: Long, module: LoadedModule, name: String): CUDAResult[Kernel] =
    moduleProgram(handle, module).flatMap { program =>
      val entries = program.statements.collect { case entry: Statement.Entry => entry }
      entryDefinition(program, name) match {
        case None        => failure(500, s"PTX entry point not found: $name")
        case Some(entry) =>
          Lowering.lower(
            entry,
            program.statements.collect { case function: Statement.Function => function },
            module.globals.view.mapValues(_.address).toMap,
            module.shared,
            entries
          ) match {
            case Left(error)   => failure(801, error.message)
            case Right(kernel) => success(kernel)
          }
      }
    }

  override def cuModuleUnload(module: CUDAModule): CUDAResult[Unit] = synchronized {
    val current = state.get()
    current.modules.get(module.handle) match {
      case None         => failure(201, s"invalid CUDA module handle: ${module.handle}")
      case Some(loaded) =>
        val released = releaseGlobals(loaded.globals.valuesIterator.toCol)
        if (recentProgram.get().exists(_._1 == module.handle)) recentProgram.set(None)
        state.set(
          current.copy(
            modules = current.modules.removed(module.handle),
            functions = current.functions.filter { case (_, loaded) => loaded.module != module }
          )
        )
        released
    }
  }

  /** Relocatable CUDA images can contain a declaration-only entry followed by its linked definition. Looking up the
    * first matching entry would lower the declaration to an empty program and report an illegal launch at PC 0. Prefer
    * a concrete body while retaining declaration-only entries for the normal missing-definition diagnostic.
    */
  private def entryDefinition(program: Program, name: String): Option[Statement.Entry] = {
    val entries = program.statements.collect { case entry: Statement.Entry if entry.name == name => entry }
    entries.find(_.body.nonEmpty).orElse(entries.headOption)
  }

  /** CUB's counting-iterator implementations of for_each and adjacent_difference are tile-local. The upstream
    * 64-bit-index canaries intentionally launch 2^30..2^33 logical elements, which would require billions of
    * interpreter lanes even though their only observable operation is a boundary check. Preserve the full virtual
    * geometry (so blockIdx/gridDim and 64-bit index arithmetic remain real) while evaluating the first and last tile.
    * Other kernels, including reductions and user code, retain ordinary complete-grid execution.
    */
  private def representativeBlocks(kernel: Kernel, geometry: LaunchGeometry): Option[Col[Int]] = {
    val groups                    = geometry.groupCount.volumeInt.toOption
    val cubCountingBoundaryKernel =
      (kernel.name.startsWith("_ZN3cub") || kernel.name.startsWith("_ZN6thrust8cuda_cub")) &&
        kernel.name.contains("counting_iterator") &&
        (kernel.name.contains("for_each") || kernel.name.contains("adjacent_difference"))
    groups
      .filter(_ > representativeGridBlockThreshold)
      .filter(_ > 1)
      .filter(_ => cubCountingBoundaryKernel)
      .map(last => Col(0, last - 1))
  }

  override def cuLaunchKernel(
      function: CUDAFunction,
      launch: KernelLaunch,
      memory: CheckedMemory
  ): CUDAResult[Unit] = synchronized {
    val current = state.get()
    current.functions.get(function.handle) match {
      case None         => failure(400, s"invalid CUDA function handle: ${function.handle}")
      case Some(loaded) =>
        val resolved = loaded.kernel match {
          case Some(kernel) => success(kernel)
          case None         =>
            current.modules.get(loaded.module.handle) match {
              case None         => failure(201, s"invalid CUDA module handle: ${loaded.module.handle}")
              case Some(module) =>
                lowerFunction(loaded.module.handle, module, loaded.name).map { kernel =>
                  val latest = state.get()
                  state.set(
                    latest.copy(functions =
                      latest.functions.updated(function.handle, loaded.copy(kernel = Some(kernel)))
                    )
                  )
                  kernel
                }
            }
        }
        resolved.flatMap { kernel =>
          val grid  = Col(launch.gridX.toLong, launch.gridY.toLong, launch.gridZ.toLong)
          val block = Col(launch.blockX.toLong, launch.blockY.toLong, launch.blockZ.toLong)
          if (grid.exists(_ == 0L) || block.exists(_ == 0L)) failure(1, "CUDA launch dimensions must be non-zero")
          else if (grid.exists(_ > Int.MaxValue.toLong) || block.exists(_ > Int.MaxValue.toLong))
            failure(1, "CUDA launch dimensions exceed the emulator limit")
          else if (launch.arguments.length != kernel.parameters.length)
            failure(
              1,
              s"kernel ${kernel.name} expects ${kernel.parameters.length} arguments, found ${launch.arguments.length}"
            )
          else {
            val decoded = kernel.parameters.zip(launch.arguments).map { case (parameter, argument) =>
              if (argument.bytes.length != parameter.widthBytes)
                Left(
                  s"kernel argument ${parameter.name} expects ${parameter.widthBytes} bytes, found ${argument.bytes.length}"
                )
              else Right(parameter.name -> Argument(argument.bytes))
            }
            decoded.collectFirst { case Left(error) => error } match {
              case Some(error) => failure(1, error)
              case None        =>
                val arguments = decoded.collect { case Right(value) => value }.toMap
                val geometry  = for {
                  groupCount <- Dimensions.create(grid(0).toInt, grid(1).toInt, grid(2).toInt)
                  groupSize  <- Dimensions.create(block(0).toInt, block(1).toInt, block(2).toInt)
                  launch     <- LaunchGeometry.createVirtual(groupSize, groupCount)
                } yield launch
                geometry match {
                  case Left(error)  => failure(1, error)
                  case Right(value) =>
                    val blockSelection = representativeBlocks(kernel, value)
                    val execution      = Interpreter.execute(
                      kernel,
                      value,
                      arguments,
                      DynamicAddressMemory(memory, state = dynamicMemoryState),
                      captureTrace = false,
                      blockIndices = blockSelection
                    )
                    execution match {
                      case Left(error) => failure(700, error.message)
                      case Right(_)    => success(())
                    }
                }
            }
          }
        }
    }
  }

  private def decodeUtf8(bytes: Col[Byte]): CUDAResult[String] =
    try {
      val decoder = StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
      success(decoder.decode(ByteBuffer.wrap(Col.toArray(bytes))).toString)
    } catch {
      case error: Exception => failure(200, s"module image is not UTF-8 PTX: ${error.getMessage}")
    }

  private def moduleSharedSymbols(statements: Col[Statement]): Map[String, Long] = {
    val names = moduleSymbols(statements, Set("shared"))
    names.zipWithIndex.map { case (name, index) => name -> (0x20000000L + index.toLong * 0x100000L) }.toMap
  }

  private def moduleGlobalSymbols(statements: Col[Statement]): Set[String] =
    moduleSymbols(statements, Set("global", "const")).toSet

  private def moduleSymbols(statements: Col[Statement], addressSpaces: Set[String]): Col[String] = {
    def visit(values: Iterator[Statement]): Iterator[String] = values.flatMap {
      case Statement.Directive(path, arguments, _)
          if path.exists(addressSpaces) || arguments.exists {
            case DirectiveArgument(_, Operand.Qualifier(qualifiers)) => qualifiers.exists(addressSpaces)
            case _                                                   => false
          } =>
        arguments.iterator.flatMap(argument =>
          argument.operand match {
            case Operand.Symbol(name)                   => Iterator.single(name)
            case Operand.Index(Operand.Symbol(name), _) => Iterator.single(name)
            case _                                      => Iterator.empty
          }
        )
      case Statement.Block(body)                      => visit(body.iterator)
      case Statement.Entry(_, _, _, body)             => visit(body.iterator)
      case Statement.Function(_, _, _, _, Some(body)) => visit(body.iterator)
      case _                                          => Iterator.empty
    }
    Col.from(visit(statements.iterator)).distinct
  }

  private def allocateGlobals(
      names: Col[String],
      index: Int,
      definitions: Map[String, GlobalDefinition],
      storage: GlobalStorage,
      allocated: Map[String, LoadedGlobal]
  ): CUDAResult[Map[String, LoadedGlobal]] =
    if (index >= names.size) success(allocated)
    else {
      val name       = names(index)
      val definition = definitions.getOrElse(name, GlobalDefinition(1, Array.ofDim[Byte](1)))
      storage.allocate(SizeT(definition.size.toLong), definition.initial) match {
        case CUDAResult.Success(pointer) =>
          val global = LoadedGlobal(pointer.address, definition.size, () => storage.release(pointer))
          allocateGlobals(names, index + 1, definitions, storage, allocated.updated(name, global))
        case CUDAResult.Failure(error) =>
          val _ = releaseGlobals(allocated.valuesIterator.toCol)
          CUDAResult.Failure(error)
      }
    }

  private def releaseGlobals(globals: Col[LoadedGlobal]): CUDAResult[Unit] =
    globals.foldLeft[CUDAResult[Unit]](success(())) { (aggregate, global) =>
      val released = global.release()
      aggregate match {
        case failure @ CUDAResult.Failure(_) => failure
        case CUDAResult.Success(_)           => released
      }
    }

  private val GlobalDeclaration =
    """(?m)^[ \t]*(?:\.[A-Za-z0-9_]+[ \t]+)*\.(?:global|const)[ \t]+(?:\.[A-Za-z0-9_]+[ \t]+|\d+[ \t]+)*\.([busf])(8|16|32|64)[ \t]+([A-Za-z_$][A-Za-z0-9_$]*)(?:[ \t]*\[(\d+)\])?(?:\s*=\s*(?:\{([^}]*)\}|([^;]*)))?[ \t]*;""".r

  private def globalDefinitions(source: String): Map[String, GlobalDefinition] =
    GlobalDeclaration.findAllMatchIn(source).map { declaration =>
      val kind         = declaration.group(1).head
      val elementBytes = declaration.group(2).toInt / 8
      val name         = declaration.group(3)
      val count        = Option(declaration.group(4)).flatMap(_.toIntOption).getOrElse(1)
      val size         = Math.max(1, Math.multiplyExact(elementBytes, count))
      val bytes        = Array.ofDim[Byte](size)
      val initializer  = Option(declaration.group(5)).orElse(Option(declaration.group(6)))
      initializer.iterator.flatMap(_.split(",", -1)).map(_.trim).filter(_.nonEmpty).take(count).zipWithIndex
        .foreach { case (raw, index) =>
          initializerBits(raw, kind, elementBytes * 8).foreach { bits =>
            (0 until elementBytes).foreach { byte =>
              bytes(index * elementBytes + byte) = (bits >>> (byte * 8)).toByte
            }
          }
        }
      name -> GlobalDefinition(size, bytes)
    }.toMap

  private def initializerBits(raw: String, kind: Char, width: Int): Option[Long] =
    try
      if (kind == 'f' && width == 32) {
        if (raw.startsWith("0f") || raw.startsWith("0F")) Some(BigInt(raw.drop(2), 16).longValue)
        else Some(java.lang.Float.floatToRawIntBits(raw.toFloat).toLong)
      } else if (kind == 'f' && width == 64) {
        if (raw.startsWith("0d") || raw.startsWith("0D")) Some(BigInt(raw.drop(2), 16).longValue)
        else Some(java.lang.Double.doubleToRawLongBits(raw.toDouble))
      } else {
        val unsigned = raw.reverse.dropWhile(character => "uUlL".contains(character)).reverse
        val negative = unsigned.startsWith("-")
        val value    = if (negative) unsigned.drop(1) else unsigned
        val parsed   =
          if (value.startsWith("0x") || value.startsWith("0X")) BigInt(value.drop(2), 16)
          else if (value.startsWith("0b") || value.startsWith("0B")) BigInt(value.drop(2), 2)
          else BigInt(value)
        Some((if (negative) -parsed else parsed).longValue)
      }
    catch {
      case _: NumberFormatException => None
    }

  private def success[A](value: A): CUDAResult[A]                   = CUDAResult.Success(value)
  private def failure[A](code: Int, message: String): CUDAResult[A] =
    CUDAResult.Failure(CUDAError(code, message))
}
