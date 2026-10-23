package net.kurobako.gemstone.isa.ptx

import scala.annotation.unused

/** Scala.js has no soft references to retain values with, so nothing outlives its last use. */
class Retained[V] {
  def put(@unused hash: Int, @unused value: V): Unit = ()

  def take(@unused hash: Int)(@unused matches: V => Boolean): Option[V] = None
}
