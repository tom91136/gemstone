package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.execution.Dimensions
import net.kurobako.gemstone.execution.LaunchGeometry
import net.kurobako.gemstone.isa.ptx.v9_3.Parser
import net.kurobako.gemstone.isa.ptx.v9_3.Argument
import net.kurobako.gemstone.isa.ptx.v9_3.Interpreter
import net.kurobako.gemstone.isa.ptx.v9_3.Kernel
import net.kurobako.gemstone.isa.ptx.v9_3.Lowering
import net.kurobako.gemstone.isa.ptx.v9_3.Statement
import net.kurobako.gemstone.memory.CheckedMemory
import net.kurobako.gemstone.vendor.CUDAKernelExecution

import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicReference

/** Textual-PTX CUDA module backend. Images and launch arguments have already been copied out of the native process when
  * they reach this class.
  */
final class PTXCUDAKernelExecution extends CUDAKernelExecution {
  private final case class LoadedModule(entries: Map[String, Statement.Entry])
  private final case class LoadedFunction(module: CUDAModule, kernel: Kernel)
  private final case class State(
      modules: Map[Long, LoadedModule],
      functions: Map[Long, LoadedFunction],
      nextModuleHandle: Long,
      nextFunctionHandle: Long
  )

  private val state = AtomicReference(State(Map.empty, Map.empty, 1L, 1L))

  override def cuModuleLoadData(image: ModuleImage): CUDAResult[CUDAModule] = synchronized {
    decodeUtf8(image.bytes).flatMap { source =>
      Parser.parse(source) match {
        case Left(error)    => failure(200, s"invalid PTX module: $error")
        case Right(program) =>
          val entries = program.statements.collect { case entry: Statement.Entry => entry.name -> entry }.toMap
          if (entries.isEmpty) failure(200, "PTX module contains no entry points")
          else {
            val current = state.get()
            val handle  = current.nextModuleHandle
            state.set(
              current.copy(
                modules = current.modules.updated(handle, LoadedModule(entries)),
                nextModuleHandle = Math.addExact(handle, 1L)
              )
            )
            success(CUDAModule(handle))
          }
      }
    }
  }

  override def cuModuleGetFunction(module: CUDAModule, name: FunctionName): CUDAResult[KernelFunction] = synchronized {
    val current = state.get()
    current.modules.get(module.handle) match {
      case None         => failure(201, s"invalid CUDA module handle: ${module.handle}")
      case Some(loaded) =>
        loaded.entries.get(name.value) match {
          case None        => failure(500, s"PTX entry point not found: ${name.value}")
          case Some(entry) =>
            Lowering.lower(entry) match {
              case Left(error)   => failure(801, error.message)
              case Right(kernel) =>
                val handle = current.nextFunctionHandle
                state.set(
                  current.copy(
                    functions = current.functions.updated(handle, LoadedFunction(module, kernel)),
                    nextFunctionHandle = Math.addExact(handle, 1L)
                  )
                )
                val widths = kernel.parameters.map(_.widthBytes.toByte)
                success(KernelFunction(CUDAFunction(handle), widths))
            }
        }
    }
  }

  override def cuModuleUnload(module: CUDAModule): CUDAResult[Unit] = synchronized {
    val current = state.get()
    if (!current.modules.contains(module.handle)) failure(201, s"invalid CUDA module handle: ${module.handle}")
    else {
      state.set(
        current.copy(
          modules = current.modules.removed(module.handle),
          functions = current.functions.filter { case (_, loaded) => loaded.module != module }
        )
      )
      success(())
    }
  }

  override def cuLaunchKernel(
      function: CUDAFunction,
      launch: KernelLaunch,
      memory: CheckedMemory
  ): CUDAResult[Unit] = synchronized {
    state.get().functions.get(function.handle) match {
      case None         => failure(400, s"invalid CUDA function handle: ${function.handle}")
      case Some(loaded) =>
        val grid  = Col(launch.gridX.toLong, launch.gridY.toLong, launch.gridZ.toLong)
        val block = Col(launch.blockX.toLong, launch.blockY.toLong, launch.blockZ.toLong)
        if (grid.exists(_ == 0L) || block.exists(_ == 0L)) failure(1, "CUDA launch dimensions must be non-zero")
        else if (launch.sharedMemoryBytes.toLong != 0L)
          failure(801, "dynamic shared memory is not implemented")
        else if (grid.exists(_ > Int.MaxValue.toLong) || block.exists(_ > Int.MaxValue.toLong))
          failure(1, "CUDA launch dimensions exceed the emulator limit")
        else if (launch.arguments.length != loaded.kernel.parameters.length)
          failure(
            1,
            s"kernel ${loaded.kernel.name} expects ${loaded.kernel.parameters.length} arguments, found ${launch.arguments.length}"
          )
        else {
          val decoded = loaded.kernel.parameters.zip(launch.arguments).map { case (parameter, argument) =>
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
                launch     <- LaunchGeometry.create(groupSize, groupCount)
              } yield launch
              geometry match {
                case Left(error)  => failure(1, error)
                case Right(value) =>
                  Interpreter.execute(loaded.kernel, value, arguments, memory, captureTrace = false) match {
                    case Left(error) => failure(700, error.message)
                    case Right(_)    => success(())
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

  private def success[A](value: A): CUDAResult[A]                   = CUDAResult.Success(value)
  private def failure[A](code: Int, message: String): CUDAResult[A] = CUDAResult.Failure(CUDAError(code, message))
}
