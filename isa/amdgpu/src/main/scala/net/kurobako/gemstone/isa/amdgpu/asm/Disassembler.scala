package net.kurobako.gemstone.isa.amdgpu.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.amdgpu.*
import scala.annotation.tailrec

object Disassembler {
  enum Error {
    case Instruction(offset: Int >= 0, error: CodecError)
  }
}

/** Decodes an AMDGPU instruction stream, retaining the byte offset of a failed instruction. */
class Disassembler(target: Target, waveSize: Int in (32, 64) = 32) {
  def decode(bytes: Vec[Byte]): Either[Disassembler.Error, Program] = {
    val statements = Vec.newBuilder[Program.Statement]
    val array      = Vec.toArray(bytes)
    @tailrec
    def loop(offset: Int >= 0): Either[Disassembler.Error, Program] =
      if (offset.raw == bytes.size) Right(Program(statements.result()))
      else Binary.decodeStatementAt(array, offset, target, waveSize) match {
        case Left(error)    => Left(Disassembler.Error.Instruction(offset, error))
        case Right(decoded) =>
          statements += decoded.statement
          loop(offset + decoded.size)
      }
    loop(0)
  }
}
