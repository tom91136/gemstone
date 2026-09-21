package net.kurobako.gemstone.isa

trait Printer[-A] {
  def render(value: A): String
}
