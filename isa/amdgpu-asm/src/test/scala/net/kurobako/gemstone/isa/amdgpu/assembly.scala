package net.kurobako.gemstone.isa.amdgpu

import munit.Location
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.testing.*

/** Assembly scaffolding shared by the assembler, disassembler and catalogue suites. */
private object assembly extends munit.Assertions {
  def hex(text: String): Vec[Byte] = Vec.unsafeWrapArray(bytes.hex(text))

  def parseInstruction(source: String): Either[String, Instruction] =
    Parser.parse(source).left.map(_.toString).flatMap {
      case Program(Vec(Program.Statement.Instruction(value))) => Right(value)
      case other                                              => Left(s"expected one instruction: $other")
    }

  def parse(source: String)(using Location): Instruction = parseInstruction(source).fold(fail(_), identity)

  def parseStatement(source: String)(using Location): Program.Statement =
    Parser.parse(source).fold(error => fail(s"$source: $error"), _.statements.head)

  def encoded(instruction: Instruction, target: Target, waveSize: Int in (32, 64) = 32)(using Location): Vec[Byte] =
    Binary.encode(instruction, TargetMode(target, waveSize)).fold(
      error => fail(s"${Printer.render(instruction)}: $error"),
      identity
    )

  def disassembled(code: Vec[Byte], target: Target, waveSize: Int in (32, 64) = 32)(using Location): Instruction =
    Binary.decode(code, TargetMode(target, waveSize)).fold(error => fail(s"$target: $error"), identity)
}
