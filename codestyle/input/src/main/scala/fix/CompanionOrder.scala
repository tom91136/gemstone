/*
rule = CompanionOrder
 */
package fix

class CompanionOrder
object CompanionOrder { // assert: CompanionOrder.companion-order
  trait Newtype[A] {
    opaque type Type = A
  }

  class Record
  object Record // assert: CompanionOrder.companion-order

  trait Service
  object Service // assert: CompanionOrder.companion-order

  enum Mode { case First }
  object Mode // assert: CompanionOrder.companion-order

  type Items = List[Int]
  object Items // assert: CompanionOrder.companion-order

  object Id extends Newtype[Long] // assert: CompanionOrder.companion-order
  type Id = Id.Type

  object QualifiedId extends CompanionOrder.Newtype[Long] // assert: CompanionOrder.companion-order
  type QualifiedId = QualifiedId.Type

  object GoodRecord
  case class GoodRecord(value: Int)

  object GoodService
  trait GoodService

  object GoodMode
  enum GoodMode { case First }

  object GoodItems
  type GoodItems = List[Int]

  type GoodId = GoodId.Type
  object GoodId extends Newtype[Long]

  object ScopeOne { class Unrelated  }
  object ScopeTwo { object Unrelated }

  def local(): Unit = {
    class Local
    object Local // assert: CompanionOrder.companion-order
    ()
  }

  class Suppressed
  @SuppressWarnings(Array("scalafix:CompanionOrder"))
  object Suppressed
}
