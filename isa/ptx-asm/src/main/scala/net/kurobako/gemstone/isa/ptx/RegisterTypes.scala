package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

/** Applies the type checking tables to the register operands of an instruction, using the `.reg` declarations in scope.
  */
object RegisterTypes {
  type Scope = Map[String, PtxType]

  private val Relaxed      = Set("ld", "st", "cvt")
  private val Destinations = Map("ld" -> Set(0), "st" -> Set(1), "cvt" -> Set(0))

  /** The register operands of `instruction` whose declared types no rule accepts, by operand position. */
  def violations(instruction: Instruction, scope: Scope): Vec[(position: Int, violation: RegisterViolation)] =
    if (scope.isEmpty) Vec.empty
    else {
      val opcode       = instruction.opcode.raw
      val relaxed      = Relaxed(opcode)
      val destinations = Destinations.getOrElse(opcode, Set.empty[Int])
      val types        = PtxTypes.registerTypes(instruction)
      instruction.operands.zipWithIndex.flatMap { (operand, position) =>
        types.lift(position).toVec.flatMap { candidates =>
          registers(operand, scope).flatMap { (name, declared) =>
            def accepts(expected: PtxType): Boolean =
              if (!relaxed) PtxTypes.agrees(expected, declared)
              else if (destinations(position)) PtxTypes.acceptsDestination(expected, declared)
              else PtxTypes.acceptsSource(expected, declared)
            if (candidates.exists(accepts)) Vec.empty[(position: Int, violation: RegisterViolation)]
            else
              candidates.headOption.toVec.map { expected =>
                val rule =
                  if (!relaxed) RegisterRule.Exact
                  else if (destinations(position)) RegisterRule.Destination
                  else RegisterRule.Source
                val allowed =
                  if (!relaxed) PtxTypes.allowedOperands(expected)
                  else if (destinations(position)) PtxTypes.allowedDestinations(expected)
                  else PtxTypes.allowedSources(expected)
                (
                  position = position,
                  violation = RegisterViolation.Incompatible(name, declared.name, expected.name, rule, allowed)
                )
              }
          }
        }
      }.distinct
    }

  private def registers(operand: Operand, scope: Scope): Vec[(name: String, declared: PtxType)] = operand match {
    case Operand.Register(name, _)       => declared(name.raw, scope)
    case Operand.Symbol(name)            => declared(name.raw, scope)
    case Operand.WithPredicate(base, _)  => registers(base, scope)
    case Operand.Negate(value)           => registers(value, scope)
    case Operand.Not(value)              => registers(value, scope)
    case Operand.VectorLiteral(elements) => elements.flatMap(registers(_, scope))
    case _                               => Vec.empty
  }

  private def declared(name: String, scope: Scope): Vec[(name: String, declared: PtxType)] =
    scope.get(name).toVec.map(declared => (name = name, declared = declared))
}
