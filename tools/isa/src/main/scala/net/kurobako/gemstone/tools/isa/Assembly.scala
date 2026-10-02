package net.kurobako.gemstone.tools.isa

import cats.syntax.all.*
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.v9_3 as ptx
import net.kurobako.gemstone.isa.spirv.v1_6 as spirv
import scala.annotation.tailrec

object Assembly {
  enum Format {
    case PTX, SPIRV
    case AMDGPU(target: amdgpu.Target, waveSize: Int in (32, 64) = 32)
  }

  enum Error {
    case InvalidUTF8
    case PTX(error: ptx.Program.ParseError)
    case SPIRVAssembly(error: spirv.asm.Assembler.Error)
    case SPIRVBinary(error: spirv.ISA.DecodeError)
    case CodeObject(error: amdgpu.CodeObjectError)
    case AMDGPUParse(error: amdgpu.asm.Parser.Error)
    case AMDGPUEncoding(error: amdgpu.asm.CodecError)
    case AMDGPUDecoding(offset: Int >= 0, error: amdgpu.asm.CodecError)
  }

  /** PTX is textual: assembly and disassembly both return its canonical source, not a CUDA cubin. */
  def assemble(source: String, format: Format): Either[Error, Vec[Byte]] = format match {
    case Format.SPIRV => spirv.asm.Assembler.encode(source).leftMap(Error.SPIRVAssembly(_))
    case Format.PTX   =>
      ptx.Parser.parse(source).leftMap(Error.PTX(_)).map(program => utf8(ptx.asm.Printer.render(program)))
    case Format.AMDGPU(target, waveSize) => amdgpu.asm.Parser.parse(source).leftMap(Error.AMDGPUParse(_)).flatMap {
        program =>
          amdgpu.asm.Assembler.encode(program, target, waveSize).leftMap(Error.AMDGPUEncoding(_))
      }
  }

  def disassemble(bytes: Vec[Byte], format: Format): Either[Error, String] = format match {
    case Format.SPIRV => spirv.ISA.decode(bytes).map(spirv.asm.Printer.render(_)).leftMap(Error.SPIRVBinary(_))
    case Format.PTX   =>
      text(bytes).flatMap(source => ptx.Parser.parse(source).leftMap(Error.PTX(_))).map(ptx.asm.Printer.render(_))
    case Format.AMDGPU(target, waveSize) =>
      if (bytes.take(4) == Vec[Byte](0x7f, 'E'.toByte, 'L'.toByte, 'F'.toByte))
        disassembleCodeObject(bytes, target, None, waveSize)
      else disassembleInstructions(bytes, target, waveSize)
  }

  def disassembleCodeObject(
      image: Vec[Byte],
      target: amdgpu.Target,
      section: Option[String],
      waveSize: Int in (32, 64) = 32
  ): Either[Error, String] =
    amdgpu.CodeObject.decode(image).flatMap(_.executableSections).flatMap { sections =>
      section match {
        case Some(name) => sections.filter(_.name == name) match {
            case Vec(value) => Right(value)
            case _          => Left(amdgpu.CodeObjectError.Invalid(s"expected one executable section named '$name'"))
          }
        case None => sections match {
            case Vec(value) => Right(value)
            case _          => Left(amdgpu.CodeObjectError.Invalid(
                s"select an executable section with --section: ${sections.map(_.name).mkString(", ")}"
              ))
          }
      }
    }.leftMap(Error.CodeObject(_)).flatMap(section => disassembleInstructions(section.bytes, target, waveSize))

  private def disassembleInstructions(
      bytes: Vec[Byte],
      target: amdgpu.Target,
      waveSize: Int in (32, 64)
  ): Either[Error, String] = {
    val instructions = Vec.newBuilder[amdgpu.asm.Program.Statement]
    @tailrec
    def loop(offset: Int >= 0): Either[Error, String] =
      if (offset.raw == bytes.size) Right(amdgpu.asm.Printer.render(amdgpu.asm.Program(instructions.result())))
      else amdgpu.asm.Binary.decodeStatementAt(bytes, offset, target, waveSize) match {
        case Left(error)    => Left(Error.AMDGPUDecoding(offset, error))
        case Right(decoded) =>
          instructions += decoded.statement
          loop(offset + decoded.size)
      }
    loop(0)
  }

  def text(bytes: Vec[Byte]): Either[Error, String] =
    try Right(StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(Vec.toArray(bytes))).toString)
    catch { case _: CharacterCodingException => Left(Error.InvalidUTF8) }

  private def utf8(value: String): Vec[Byte] = Vec.unsafeWrapArray(value.getBytes(StandardCharsets.UTF_8))
}
