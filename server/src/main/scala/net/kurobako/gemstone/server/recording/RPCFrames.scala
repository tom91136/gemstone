package net.kurobako.gemstone.server.recording

import net.kurobako.gemstone.rpc.ExecutionRPC

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.IOException
import java.nio.ByteBuffer

/** Framing only: API value codecs remain owned by the protocol implementation/generated schema. */
private[server] object RPCFrames {
  val ExecutionRequest                              = ExecutionRPC.RequestMagic
  val ExecutionResponse                             = ExecutionRPC.ResponseMagic
  def magic(bytes: Array[Byte]): Int                = ByteBuffer.wrap(bytes).getInt()
  def integer(bytes: Array[Byte], offset: Int): Int = ByteBuffer.wrap(bytes, offset, 4).getInt()
  def long(bytes: Array[Byte], offset: Int): Long   = ByteBuffer.wrap(bytes, offset, 8).getLong()
  val ClientOffset: Int                             = ExecutionRPC.RequestClientOffset
  def normalized(
      kind: RPCJournal.Kind,
      bytes: Array[Byte],
      maximum: Int :| Interval.Closed[1, 2147479551]
  ): Array[Byte] = {
    validate(kind, bytes, maximum, normalized = false)
    if (kind != RPCJournal.Kind.Request) bytes
    else {
      val result = bytes.clone()
      ByteBuffer.wrap(result).putLong(ClientOffset, 0L)
      result
    }
  }
  val MaximumHeaderBytes = ExecutionRPC.RequestHeaderBytes
  def validate(
      kind: RPCJournal.Kind,
      bytes: Array[Byte],
      maximum: Int :| Interval.Closed[1, 2147479551],
      normalized: Boolean
  ): Unit =
    validatePrefix(kind, bytes, bytes.length, maximum, normalized)

  private[server] def validatePrefix(
      kind: RPCJournal.Kind,
      bytes: Array[Byte],
      frameSize: Int,
      maximum: Int :| Interval.Closed[1, 2147479551],
      normalized: Boolean
  ): Unit = {
    def check(value: Boolean): Unit = if (!value) throw IOException("RPC journal: invalid RPC frame")
    check(bytes.length >= 12 && bytes.length <= frameSize && frameSize <= maximum)
    check(long(bytes, ExecutionRPC.RequestFingerprintOffset) == ExecutionRPC.Schema.fingerprint)
    val (header, payload) = kind match {
      case RPCJournal.Kind.Request if magic(bytes) == ExecutionRequest =>
        check(bytes.length >= ExecutionRPC.RequestHeaderBytes)
        (ExecutionRPC.RequestHeaderBytes, integer(bytes, ExecutionRPC.RequestPayloadSizeOffset))
      case RPCJournal.Kind.Response if magic(bytes) == ExecutionResponse =>
        check(bytes.length >= ExecutionRPC.ResponseHeaderBytes)
        check(integer(bytes, ExecutionRPC.ResponseStatusOffset) == 0 ||
          integer(bytes, ExecutionRPC.ResponseStatusOffset) == 1)
        (ExecutionRPC.ResponseHeaderBytes, integer(bytes, ExecutionRPC.ResponsePayloadSizeOffset))
      case _ => throw IOException("RPC journal: frame kind/magic differ")
    }
    check(payload >= 0 && header.toLong + payload == frameSize.toLong)
    if (normalized && kind == RPCJournal.Kind.Request) {
      check(ByteBuffer.wrap(bytes).getLong(ClientOffset) == 0L)
    }
  }
}
