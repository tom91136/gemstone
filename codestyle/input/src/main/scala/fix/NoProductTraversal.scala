/*
rule = GemstoneNoProductTraversal
 */
package fix

object NoProductTraversal {
  def inspect(value: Any): String = value match {
    case product: Product => product.toString // assert: GemstoneNoProductTraversal.product-traversal
    case number: Int => number.toString
    case _ => "other"
  }

  def qualified(value: Any): Boolean = value match {
    case _: scala.Product => true // assert: GemstoneNoProductTraversal.product-traversal
    case _ => false
  }

  def rooted(value: Any): Boolean = value match {
    case _: _root_.scala.Product => true // assert: GemstoneNoProductTraversal.product-traversal
    case _ => false
  }

  def fields(value: Product) = value.productIterator // assert: GemstoneNoProductTraversal.product-traversal
  def field(value: Product) = value.productElement(0) // assert: GemstoneNoProductTraversal.product-traversal
  def names(value: Product) = value.productElementNames // assert: GemstoneNoProductTraversal.product-traversal
  def name(value: Product) = value.productElementName(0) // assert: GemstoneNoProductTraversal.product-traversal
  def arity(value: Product) = value.productArity // assert: GemstoneNoProductTraversal.product-traversal

  case class Node(value: Int)
  def typed(value: Node): Int = value match {
    case Node(number) => number
  }

  @SuppressWarnings(Array("scalafix:GemstoneNoProductTraversal"))
  def derivedField(value: Product): Any = value.productElement(0)
}
