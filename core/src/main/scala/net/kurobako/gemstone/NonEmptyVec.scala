package net.kurobako.gemstone

import net.kurobako.gemstone.Constraint.Positive

import scala.reflect.ClassTag

/** A [[Vec]] that is statically known to hold at least one element. */
type NonEmptyVec[+A] = cats.data.NonEmptyVector[A]

object NonEmptyVec {
  def apply[A](first: A, rest: A*): NonEmptyVec[A] = cats.data.NonEmptyVector.of(first, rest*)

  def fromVec[A](values: Vec[A]): Either[String, NonEmptyVec[A]] =
    fromVector(values.toVector)

  def fromVector[A](values: Vector[A]): Either[String, NonEmptyVec[A]] =
    cats.data.NonEmptyVector.fromVector(values).toRight("collection must not be empty")

  def unsafeFromVec[A](values: Vec[A]): NonEmptyVec[A] =
    fromVec(values).fold(message => throw IllegalArgumentException(message), identity)

  extension [A](values: NonEmptyVec[A]) {
    def toVec(using ClassTag[A]): Vec[A] = Vec.from(values.toVector)
    def positiveSize: Positive[Int]      = Positive.unsafe(values.length)
  }
}
