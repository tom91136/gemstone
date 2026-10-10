package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** One AMDGPU wave as an observer sees it before its next instruction. */
trait WaveView extends ArchitecturalState {

  /** The wave's lane count, 32 or 64, or fewer for a partial wave. */
  def lanes: Int in 1 ~ 64

  /** EXEC: bit `n` set when lane `n` is active. */
  def exec: Long

  /** SGPR `index`, in 32 bits. */
  def scalar(index: Int in 0 ~ 127): Long

  /** VGPR `index` of `lane`, in 32 bits. */
  def vector(index: Int in 0 ~ 255, lane: Int in 0 ~ 63): Long
}
