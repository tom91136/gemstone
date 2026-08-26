package net.kurobako.gemstone.vendor.native

import net.kurobako.gemstone.generated.GeneratedApi
import net.kurobako.gemstone.vendor.CudaError
import net.kurobako.gemstone.vendor.CudaRuntime

import scala.scalanative.unsafe.CFuncPtr0
import scala.scalanative.unsafe.CFuncPtr1
import scala.scalanative.unsafe.CInt
import scala.scalanative.unsafe.CLongLong
import scala.scalanative.unsafe.CStruct4
import scala.scalanative.unsafe.CStruct6
import scala.scalanative.unsafe.CUnsignedInt
import scala.scalanative.unsafe.CUnsignedLongLong
import scala.scalanative.unsafe.Ptr
import scala.scalanative.unsafe.exported
import scala.scalanative.unsafe.extern
import scala.scalanative.unsafe.name
import scala.scalanative.unsafe.stackalloc
import scala.scalanative.unsigned._

object CoreServer {
  type WireRequest = CStruct6[
    CUnsignedInt,
    CUnsignedInt,
    CUnsignedLongLong,
    CUnsignedLongLong,
    CLongLong,
    CLongLong
  ]
  type WireResponse    = CStruct4[CUnsignedInt, CInt, CUnsignedLongLong, CLongLong]
  type TakeRequest     = CFuncPtr0[Ptr[WireRequest]]
  type CompleteRequest = CFuncPtr1[Ptr[WireResponse], Unit]

  @exported("gemsiStartServer")
  def gemsiStartServer(takeRequest: TakeRequest, completeRequest: CompleteRequest): CInt = {
    val status =
      try {
        val server = new Thread(
          () => serverLoop(takeRequest, completeRequest),
          "gemsi-core-server"
        )
        server.setDaemon(true)
        server.start()
        CudaError.Success.value
      } catch {
        case _: Throwable => CudaError.InitializationError.value
      }
    NativeGc.setMutatorThreadState(1)
    status
  }

  private def serverLoop(takeRequest: TakeRequest, completeRequest: CompleteRequest): Unit = {
    val response = stackalloc[WireResponse]()
    var running  = true
    while (running) {
      val request = takeRequest()
      val opcode  = request._2.toInt
      val result  =
        if (request._1.toInt != GeneratedApi.ProtocolVersion) {
          GeneratedApi.DispatchResult(CudaError.InvalidValue.value, 0L)
        } else if (opcode == GeneratedApi.Opcode.Shutdown) {
          running = false
          GeneratedApi.DispatchResult(CudaError.Success.value, 0L)
        } else {
          try GeneratedApi.dispatch(opcode, request._5, request._6, CudaRuntime.emulated)
          catch {
            case _: Throwable => GeneratedApi.DispatchResult(CudaError.Unknown.value, 0L)
          }
        }

      response._1 = GeneratedApi.ProtocolVersion.toUInt
      response._2 = result.status
      response._3 = request._3
      response._4 = result.result0
      completeRequest(response)
    }
  }

  @extern
  private object NativeGc {
    @name("scalanative_GC_set_mutator_thread_state")
    def setMutatorThreadState(state: CInt): Unit = extern
  }
}
