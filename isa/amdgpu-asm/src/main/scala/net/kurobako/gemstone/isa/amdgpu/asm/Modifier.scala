package net.kurobako.gemstone.isa.amdgpu.asm

import net.kurobako.gemstone.constraint.*

case class Modifier(name: NonEmpty[String], value: Option[NonEmpty[String]] = None)
