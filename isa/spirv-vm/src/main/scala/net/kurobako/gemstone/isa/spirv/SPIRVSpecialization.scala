package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*

object SPIRVSpecialization extends Backend.Specialization {
  def describe(image: Vec[Byte]): Either[Fault, Vec[SpecializationInfo]] = Specialization.describe(image)

  def specialize(image: Vec[Byte], constants: Vec[SpecializationConstant]): Either[Fault, Vec[Byte]] =
    Specialization.specialize(image, constants)

  def link(images: Vec[Vec[Byte]]): Either[Fault, Backend.LinkedImage] = Linkage.link(images, OpenCLBuiltins.supplies)
}
