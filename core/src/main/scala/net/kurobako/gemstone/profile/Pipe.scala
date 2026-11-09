package net.kurobako.gemstone.profile

/** The compute pipe that serves an instruction's lane results, matching the pipes of `Architecture.Throughput`. */
enum Pipe derives CanEqual {
  case Fma, Alu, Mufu, Fp64, Tensor
}
