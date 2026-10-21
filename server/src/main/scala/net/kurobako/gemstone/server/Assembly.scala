package net.kurobako.gemstone.server

import cats.syntax.all.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.text.*

object Assembly {
  enum Format derives CanEqual {
    case PTX, SPIRV
    case SASS(target: sass.Target)
    case AMDGPU(target: amdgpu.Target, waveSize: Int in (32, 64) = 32)
  }

  enum Error {
    case InvalidUTF8
    case TextEncoding(error: UTF8.Error)
    case Syntax(error: SourceError)
    case PTXDecode(error: ptx.ISA.DecodeError)
    case SASS(error: sass.asm.Error)
    case Cubin(detail: NonEmpty[String])
    case SPIRVEncoding(error: spirv.Binary.BinaryError)
    case SPIRVBinary(error: spirv.ISA.DecodeError)
    case CodeObject(error: amdgpu.CodeObjectError)
    case AMDGPUEncoding(error: amdgpu.asm.CodecError)
    case AMDGPUDecoding(error: amdgpu.asm.Disassembler.Error)
  }

  /** PTX is textual: assembly and disassembly both return its canonical source, not a CUDA cubin. */
  def assemble(source: String, format: Format): Either[Error, Vec[Byte]] = format match {
    case Format.SPIRV =>
      spirv.asm.Parser.parse(
        source
      ).leftMap(Error.Syntax(_)).flatMap(spirv.ISA.encode(_).leftMap(Error.SPIRVEncoding(_)))
    case Format.PTX => ptx.Parser.parse(source).leftMap(Error.Syntax(_))
        .flatMap(program => UTF8.encode(ptx.asm.Printer.render(program)).leftMap(Error.TextEncoding(_)))
    case Format.SASS(target) =>
      sass.asm.Parser.parseProgram(source).leftMap(Error.SASS(_))
        .flatMap(sass.asm.Assembler(target).encode(_).leftMap(Error.SASS(_)))
    case Format.AMDGPU(target, waveSize) => amdgpu.asm.Parser.parse(source).leftMap(Error.Syntax(_)).flatMap {
        program =>
          amdgpu.asm.Assembler.encode(program, amdgpu.TargetMode(target, waveSize)).leftMap(Error.AMDGPUEncoding(_))
      }
  }

  /** `section` selects one executable section of an ELF input; a single executable section needs no selection. */
  def disassemble(bytes: Vec[Byte], format: Format, section: Option[String] = None): Either[Error, String] =
    format match {
      case Format.SPIRV        => spirv.ISA.decode(bytes).map(spirv.asm.Printer.render(_)).leftMap(Error.SPIRVBinary(_))
      case Format.PTX          => ptx.ISA.decode(bytes).leftMap(Error.PTXDecode(_)).map(ptx.asm.Printer.render(_))
      case Format.SASS(target) =>
        (if (section.isDefined || elf.hasMagic(bytes)) cubinSection(bytes, target, section) else Right(bytes))
          .flatMap(code =>
            sass.asm.Disassembler(target).decode(code).flatMap(sass.asm.Printer.print(_)).leftMap(Error.SASS(_))
          )
      case Format.AMDGPU(target, waveSize) =>
        (if (section.isDefined || elf.hasMagic(bytes)) codeObjectSection(bytes, section) else Right(bytes))
          .flatMap(code =>
            amdgpu.asm.Disassembler(amdgpu.TargetMode(target, waveSize)).decode(code).leftMap(Error.AMDGPUDecoding(_))
              .map(amdgpu.asm.Printer.render(_))
          )
    }

  private def codeObjectSection(image: Vec[Byte], section: Option[String]): Either[Error, Vec[Byte]] =
    amdgpu.CodeObject.decode(image).flatMap(_.executableSections).flatMap { sections =>
      section.fold(sections)(name => sections.filter(_.name == name)) match {
        case Vec(value) => Right(value.bytes)
        case _          => Left(amdgpu.CodeObjectError.Invalid(
            s"select one executable section with --section: ${sections.map(_.name).mkString(", ")}"
          ))
      }
    }.leftMap(Error.CodeObject(_))

  private def cubinSection(image: Vec[Byte], target: sass.Target, section: Option[String]): Either[Error, Vec[Byte]] =
    sass.Cubin.decode(image).leftMap(detail => Error.Cubin(s"invalid cubin: $detail")).flatMap { module =>
      val executable = module.sections.filter(value => (value.flags & 4L) != 0L)
      if (module.sm != target.sm) Left(Error.Cubin(s"cubin sm_${module.sm} differs from requested ${target.name}"))
      else section.fold(executable)(name => executable.filter(_.name == name)) match {
        case Vec(value) => Right(value.bytes)
        case _          => Left(Error.Cubin(
            s"select one executable cubin section with --section: ${executable.map(_.name).mkString(", ")}"
          ))
      }
    }

  def text(bytes: Vec[Byte]): Either[Error, String] = UTF8.decode(bytes).leftMap(_ => Error.InvalidUTF8)
}
