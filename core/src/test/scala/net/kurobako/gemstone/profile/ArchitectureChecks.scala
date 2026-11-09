package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*

object ArchitectureChecks {

  @SuppressWarnings(Array("scalafix:PublicSignatures.any-type", "scalafix:BannedSyntax.product-traversal"))
  def values(value: Any): Vec[Architecture.Sourced[?]] = value match {
    case sourced: Architecture.Sourced[?] => Vec(sourced)
    case iterable: Iterable[?]            => Vec.from(iterable.flatMap(values(_)))
    case product: Product                 => Vec.from(product.productIterator.flatMap(values(_)))
    case _                                => Vec.empty
  }

  def problems(architecture: Architecture): Vec[String] = values(architecture).flatMap { each =>
    import Architecture.Source.*
    each.source match {
      case Probed(probe, card, driver, toolkit) if Vec(probe, card, driver, toolkit).exists(_.isBlank) =>
        Vec(s"${architecture.name}: blank probe field")
      case Documented(document) if document.isBlank => Vec(s"${architecture.name}: blank document")
      case Published(citation, measured) if citation.isBlank || measured.isBlank =>
        Vec(s"${architecture.name}: blank citation")
      case Inherited(from, basis) if basis.isBlank || Architecture.named(from).isEmpty =>
        Vec(s"${architecture.name}: inherited from unknown $from or without a basis")
      case Estimate(basis) if basis.isBlank => Vec(s"${architecture.name}: blank estimate basis")
      case _                                => Vec.empty
    }
  }
}
