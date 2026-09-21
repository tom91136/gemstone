package net.kurobako.gemstone.isa

import net.kurobako.gemstone.Vec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** Format-neutral information needed by a native facade to marshal a kernel launch. */
final case class KernelMetadata private (
    argumentBlockSize: Int :| Positive0,
    explicitParameterCount: Int :| Positive0,
    parameters: Vec[KernelMetadata.Parameter]
)

object KernelMetadata {
  enum ParameterKind {
    case Scalar
    case Memory
    case Local
  }

  final case class Parameter(
      width: Int :| Positive,
      offset: Int :| Positive0,
      kind: ParameterKind,
      containsPointers: Boolean
  )

  def validated(
      argumentBlockSize: Int :| Positive0,
      explicitParameterCount: Int :| Positive0,
      parameters: Vec[Parameter]
  ): Either[String, KernelMetadata] =
    for {
      _ <- Either.cond(
        explicitParameterCount <= parameters.size,
        (),
        "explicit parameter count exceeds the parameter count"
      )
      _ <- Either.cond(
        parameters.forall(parameter =>
          parameter.offset.toLong + parameter.width.toLong <= argumentBlockSize.toLong
        ),
        (),
        "kernel parameter lies outside the argument block"
      )
      _ <- Either.cond(
        parameters.zip(parameters.drop(1)).forall { case (previous, current) =>
          previous.offset.toLong + previous.width.toLong <= current.offset.toLong
        },
        (),
        "kernel parameters must be ordered and non-overlapping"
      )
    } yield KernelMetadata(argumentBlockSize, explicitParameterCount, parameters)
}
