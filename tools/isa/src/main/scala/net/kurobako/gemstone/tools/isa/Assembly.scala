package net.kurobako.gemstone.tools.isa

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.v9_3 as ptx
import net.kurobako.gemstone.isa.spirv.v1_6 as spirv
import net.kurobako.gemstone.string.*

object Assembly {
  enum Format {
    case PTX, SPIRV
    case SASS(target: sass.Target)
    case AMDGPU(target: amdgpu.Target, waveSize: Int in (32, 64) = 32)
  }

  enum Error {
    case InvalidUTF8
    case TextEncoding(error: UTF8.Error)
    case PTXDecode(error: ptx.ISA.DecodeError)
    case SASS(error: sass.asm.Error)
    case Cubin(detail: NonEmpty[String])
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
      ptx.ISA.decode(source).leftMap(Error.PTX(_)).flatMap(ptx.asm.Assembler.encode(_).leftMap(Error.TextEncoding(_)))
    case Format.SASS(target) =>
      sass.asm.Parser.parseProgram(source).leftMap(Error.SASS(_))
        .flatMap(sass.asm.Assembler(target).encode(_).leftMap(Error.SASS(_)))
    case Format.AMDGPU(target, waveSize) => amdgpu.asm.Parser.parse(source).leftMap(Error.AMDGPUParse(_)).flatMap {
        program =>
          amdgpu.asm.Assembler.encode(program, target, waveSize).leftMap(Error.AMDGPUEncoding(_))
      }
  }

  def disassemble(bytes: Vec[Byte], format: Format): Either[Error, String] = format match {
    case Format.SPIRV =>
      spirv.asm.Disassembler.decode(bytes).map(spirv.asm.Printer.render(_)).leftMap(Error.SPIRVBinary(_))
    case Format.PTX =>
      ptx.asm.Disassembler.decode(bytes).leftMap(Error.PTXDecode(_)).map(ptx.asm.Printer.render(_))
    case Format.SASS(target) =>
      if (elf.hasMagic(bytes)) disassembleCubin(bytes, target, None)
      else disassembleSASS(bytes, target)
    case Format.AMDGPU(target, waveSize) =>
      if (elf.hasMagic(bytes))
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
  ): Either[Error, String] =
    amdgpu.asm.Disassembler(target, waveSize).decode(bytes).leftMap {
      case amdgpu.asm.Disassembler.Error.Instruction(offset, error) => Error.AMDGPUDecoding(offset, error)
    }.map(amdgpu.asm.Printer.render(_))

  private def disassembleSASS(bytes: Vec[Byte], target: sass.Target): Either[Error, String] =
    sass.asm.Disassembler(target).decode(bytes).flatMap(sass.asm.Printer.print(_)).leftMap(Error.SASS(_))

  def disassembleCubin(image: Vec[Byte], target: sass.Target, section: Option[String]): Either[Error, String] =
    sass.Cubin.decode(Vec.toArray(image)).leftMap(detail => Error.Cubin(s"invalid cubin: $detail"))
      .flatMap { module =>
        val executable = module.sections.filter(value => (value.flags & 4L) != 0L)
        val selected   = section.fold(executable)(name => executable.filter(_.name == name))
        if (module.sm != target.sm)
          Left(Error.Cubin(s"cubin sm_${module.sm} differs from requested ${target.name}"))
        else selected match {
          case Vec(value) => disassembleSASS(value.bytes, target)
          case _          => Left(Error.Cubin("select one executable cubin section with --section"))
        }
      }

  def text(bytes: Vec[Byte]): Either[Error, String] = UTF8.decode(bytes).leftMap(_ => Error.InvalidUTF8)
}
