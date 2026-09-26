package net.kurobako.gemstone

import scala.compiletime.ops.string.{Length, Matches}
import scala.compiletime.ops.int.{<=, >}
import scala.compiletime.ops.boolean.&&

/** Type-level predicates used by the compile-time `apply` constructors of domain types. */
private[gemstone] object CompileTime {
  type NonEmpty[S <: String]      = (Length[S] > 0) =:= true
  type NoNul[S <: String]         = Matches[S, "[^\u0000]*"] =:= true
  type NonEmptyNoNul[S <: String] = ((Length[S] > 0) && Matches[S, "[^\u0000]*"]) =:= true

  type PrintableIdentity[S <: String] = Matches[S, "[!-~]{1,128}"] =:= true

  type BoundedNonEmptyNoNul[S <: String, Max <: Int] =
    ((Length[S] > 0) && (Length[S] <= Max) && Matches[S, "[^\u0000]*"]) =:= true
}
