package net.kurobako.gemstone.server

import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.isa.ptx.v9_3.Parser
import net.kurobako.gemstone.isa.ptx.v9_3.PTXArgument
import net.kurobako.gemstone.isa.ptx.v9_3.PTXInterpreter
import net.kurobako.gemstone.isa.ptx.v9_3.PTXKernel
import net.kurobako.gemstone.isa.ptx.v9_3.PTXLowerer
import net.kurobako.gemstone.isa.ptx.v9_3.Statement
import net.kurobako.gemstone.memory.CheckedMemory
import net.kurobako.gemstone.scheduler.LaunchShape
import net.kurobako.gemstone.vendor.CUDAKernelExecution

import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import scala.collection.mutable

/** Textual-PTX CUDA module backend. Images and launch arguments have already been copied out of the native process when
  * they reach this class.
  */
final class PTXCUDAKernelExecution extends CUDAKernelExecution {
  private final case class LoadedModule(entries: Map[String, Statement.Entry])
  private final case class LoadedFunction(module: CUDAModule, kernel: PTXKernel)

  private val modules            = mutable.Map.empty[Long, LoadedModule]
  private val functions          = mutable.Map.empty[Long, LoadedFunction]
  private var nextModuleHandle   = 1L
  private var nextFunctionHandle = 1L

  override def cuModuleLoadData(image: ModuleImage): CUDAResult[CUDAModule] = synchronized {
    decodeUtf8(image.bytes).flatMap { source =>
      Parser.parse(source) match {
        case Left(error)    => failure(200, s"invalid PTX module: $error")
        case Right(program) =>
          val entries = program.statements.collect { case entry: Statement.Entry => entry.name -> entry }.toMap
          if (entries.isEmpty) failure(200, "PTX module contains no entry points")
          else {
            val handle = nextModuleHandle
            nextModuleHandle = Math.addExact(nextModuleHandle, 1L)
            modules(handle) = LoadedModule(entries)
            success(CUDAModule(handle))
          }
      }
    }
  }

  override def cuModuleGetFunction(module: CUDAModule, name: FunctionName): CUDAResult[KernelFunction] = synchronized {
    modules.get(module.handle) match {
      case None         => failure(201, s"invalid CUDA module handle: ${module.handle}")
      case Some(loaded) =>
        loaded.entries.get(name.value) match {
          case None        => failure(500, s"PTX entry point not found: ${name.value}")
          case Some(entry) =>
            PTXLowerer.lower(entry) match {
              case Left(error)   => failure(801, error.message)
              case Right(kernel) =>
                val handle = nextFunctionHandle
                nextFunctionHandle = Math.addExact(nextFunctionHandle, 1L)
                functions(handle) = LoadedFunction(module, kernel)
                val widths = IArray.unsafeFromArray(kernel.parameters.map(_.widthBytes.toByte).toArray)
                success(KernelFunction(CUDAFunction(handle), widths))
            }
        }
    }
  }

  override def cuModuleUnload(module: CUDAModule): CUDAResult[Unit] = synchronized {
    if (modules.remove(module.handle).isEmpty) failure(201, s"invalid CUDA module handle: ${module.handle}")
    else {
      functions.filterInPlace((_, loaded) => loaded.module != module)
      success(())
    }
  }

  override def cuLaunchKernel(
      function: CUDAFunction,
      launch: KernelLaunch,
      memory: CheckedMemory
  ): CUDAResult[Unit] = synchronized {
    functions.get(function.handle) match {
      case None         => failure(400, s"invalid CUDA function handle: ${function.handle}")
      case Some(loaded) =>
        val grid  = Vector(launch.gridX.toLong, launch.gridY.toLong, launch.gridZ.toLong)
        val block = Vector(launch.blockX.toLong, launch.blockY.toLong, launch.blockZ.toLong)
        if (grid.exists(_ == 0L) || block.exists(_ == 0L)) failure(1, "CUDA launch dimensions must be non-zero")
        else if (grid(1) != 1L || grid(2) != 1L || block(1) != 1L || block(2) != 1L)
          failure(801, "the initial PTX executor supports one-dimensional launches")
        else if (launch.sharedMemoryBytes.toLong != 0L)
          failure(801, "dynamic shared memory is not implemented")
        else if (grid.head > Int.MaxValue.toLong || block.head > Int.MaxValue.toLong)
          failure(1, "CUDA launch dimensions exceed the emulator limit")
        else if (launch.arguments.length != loaded.kernel.parameters.length)
          failure(
            1,
            s"kernel ${loaded.kernel.name} expects ${loaded.kernel.parameters.length} arguments, found ${launch.arguments.length}"
          )
        else {
          val decoded = loaded.kernel.parameters.zip(launch.arguments.toSeq).map { case (parameter, argument) =>
            if (argument.bytes.length != parameter.widthBytes)
              Left(
                s"kernel argument ${parameter.name} expects ${parameter.widthBytes} bytes, found ${argument.bytes.length}"
              )
            else Right(parameter.name -> PTXArgument(decodeLittleEndian(argument.bytes)))
          }
          decoded.collectFirst { case Left(error) => error } match {
            case Some(error) => failure(1, error)
            case None        =>
              val arguments = decoded.collect { case Right(value) => value }.toMap
              val shape     = LaunchShape.create(grid.head.toInt, block.head.toInt)
              shape match {
                case Left(error)  => failure(1, error)
                case Right(value) =>
                  PTXInterpreter.execute(loaded.kernel, value, arguments, memory) match {
                    case Left(error) => failure(700, error.message)
                    case Right(_)    => success(())
                  }
              }
          }
        }
    }
  }

  private def decodeUtf8(bytes: IArray[Byte]): CUDAResult[String] =
    try {
      val decoder = StandardCharsets.UTF_8.newDecoder()
        .onMalformedInput(CodingErrorAction.REPORT)
        .onUnmappableCharacter(CodingErrorAction.REPORT)
      success(decoder.decode(ByteBuffer.wrap(IArray.genericWrapArray(bytes).toArray)).toString)
    } catch {
      case error: Exception => failure(200, s"module image is not UTF-8 PTX: ${error.getMessage}")
    }

  private def decodeLittleEndian(bytes: IArray[Byte]): Long = {
    var value = 0L
    var index = 0
    while (index < bytes.length) {
      value |= (bytes(index).toLong & 0xffL) << (index * 8)
      index += 1
    }
    value
  }

  private def success[A](value: A): CUDAResult[A]                   = CUDAResult.Success(value)
  private def failure[A](code: Int, message: String): CUDAResult[A] = CUDAResult.Failure(CUDAError(code, message))
}
