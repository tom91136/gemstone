package net.kurobako.gemstone.semantics

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.vm.*

/** The operations machine semantics are written in: reads, effects, checks and control requests over lane values of
  * representation `V`, a `Long` to execute and a [[render.Term]] to render. Every method is inline, so semantics
  * written against a statically known instance expand into that instance's code. The ISA fixes `O`, the operands it
  * reads, `W`, those it writes, `L`, the lane an [[everyLane]] body runs on, `P`, a program location, `D`, bytes in
  * flight between a read and the writes that follow it, and `S`, the step an executed instruction yields.
  *
  * A statement takes the rest of the semantics as `next`, and `F[A]` is what they yield: execution stops at the first
  * fault and rendering lists the statements in order. A lane body ends in [[done]] and an instruction in [[advance]] or
  * a control request, so no statement's outcome stands in for the instruction's result. The lane is an argument because
  * an instance passed into a lambda does not expand inline. A control request names the lanes the instruction executes
  * for; scheduling, reconvergence and barrier release stay in the VM. Scalar stores and writes take effect when the
  * instruction completes, bulk byte stores at once. Values are two's complement in 64 bits and a condition holds when
  * it is non-zero. ISAs add operations by extending this trait.
  */
trait MachineAlgebra[V, O, W, L, P, D, S, F[_]] extends Any {

  /** The location of the instruction. */
  inline def pc: V

  /** Continues at the next instruction. */
  inline def advance: F[S]

  /** The end of an [[everyLane]] body. */
  inline def done: F[Unit]

  /** An operand with the same value on every lane, such as a scalar condition code. */
  inline def uniform(operand: O): V

  /** The lanes continue at `target`. */
  inline def jump(target: P): F[S]

  /** The lanes continue at the location `target` computes; the instance faults if it names no instruction. */
  inline def jumpTo(target: V): F[S]

  /** The lanes continue at `taken` where `condition`, the same on every lane, holds and fall through elsewhere. */
  inline def branch(condition: V, taken: P): F[S]

  /** The lanes continue at `taken` where `condition`, the same on every lane, holds and at `otherwise` elsewhere. */
  inline def branch(condition: V, taken: P, otherwise: P): F[S]

  /** Each lane continues at the location `target` computes for it. */
  inline def jumpEach(inline target: L => V): F[S]

  /** Each lane continues at `taken` where its `condition` holds and falls through elsewhere. */
  inline def branchEach(inline condition: L => V, taken: P): F[S]

  /** The lanes end. */
  inline def exit: F[S]

  /** The lanes arrive at the barrier named `barrier`, which expects `count` threads, and wait for it if `blocking`. */
  inline def park(barrier: V, count: V, inline blocking: Boolean): F[S]

  /** `body` on every lane the instruction executes for in turn, then `next`. */
  inline def everyLane[A](inline body: L => F[Unit])(inline next: => F[A]): F[A]

  /** The value of `operand` on `lane`. */
  inline def read(lane: L, operand: O): V

  /** `value` written to `target` on `lane`, then `next`. */
  inline def write[A](lane: L, target: W, value: V)(inline next: => F[A]): F[A]

  /** `next` over the `width` bits at `address` in `space`, zero-extended. */
  inline def load[A](lane: L, inline space: Space, address: V, width: ScalarBitWidth)(inline next: V => F[A]): F[A]

  /** The low `width` bits of `value` stored at `address` in `space`, then `next`. */
  inline def store[A](lane: L, inline space: Space, address: V, value: V, width: ScalarBitWidth)(
      inline next: => F[A]
  ): F[A]

  /** `next` over `length` bytes at `address` in `space`; `what` names the access if it faults. */
  inline def loadBytes[A](lane: L, inline space: Space, address: V, length: V, what: String)(
      inline next: D => F[A]
  ): F[A]

  /** `data` stored at `address` in `space`, then `next`. */
  inline def storeBytes[A](lane: L, inline space: Space, address: V, data: D, what: String)(
      inline next: => F[A]
  ): F[A]

  /** `primitive` applied to the arguments. */
  inline def compute(primitive: Primitive[Primitive.Unary], first: V): V
  inline def compute(primitive: Primitive[Primitive.Binary], first: V, second: V): V
  inline def compute(primitive: Primitive[Primitive.Ternary], first: V, second: V, third: V): V

  /** `next` where `condition` holds, otherwise the fault; `label` names the check when rendered. */
  inline def require[A](condition: V, label: String)(inline fault: => Fault)(inline next: => F[A]): F[A]
}
