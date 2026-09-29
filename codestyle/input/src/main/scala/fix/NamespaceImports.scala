/*
rule = GemstoneNamespaceImports
 */
package net.kurobako.gemstone.importfixture {
  class Item
  object Items {
    val value                          = 1
    given itemOrdering: Ordering[Item] = Ordering.by(_ => 0)
  }
}

package fix {
  object NamespaceImports {
    import net.kurobako.gemstone.importfixture.Item // assert: GemstoneNamespaceImports.namespace-import
    val item: Item = new Item

    val typed: net.kurobako.gemstone.importfixture.Item = // assert: GemstoneNamespaceImports.qualified-reference
      item
    val qualifiedValue =
      net.kurobako.gemstone.importfixture.Items.value // assert: GemstoneNamespaceImports.qualified-reference
    val rooted =
      new _root_.net.kurobako.gemstone.importfixture.Item // assert: GemstoneNamespaceImports.qualified-reference
    val escaped =
      `net`.kurobako.gemstone.importfixture.Items.value // assert: GemstoneNamespaceImports.qualified-reference

    // format: off
    object Grouped {
      import net.kurobako.gemstone.importfixture.{Item, Items} // assert: GemstoneNamespaceImports.namespace-import
      val pair = (new Item, Items.value)
    }
    // format: on

    object Allowed {
      import net.kurobako.gemstone.importfixture.*
      import net.kurobako.gemstone.importfixture.Items.{given, *}
      import net.kurobako.gemstone.importfixture.{Item as RenamedItem, *}
      import scala.collection.immutable.List
      val item     = new RenamedItem
      val ordering = summon[Ordering[Item]]
      val values   = List(value)
      val source   = "net.kurobako.gemstone.importfixture.Item"
      export net.kurobako.gemstone.importfixture.Items.value
    }

    // format: off
    object Relative {
      import net.kurobako.gemstone.importfixture.*
      import Items.value // assert: GemstoneNamespaceImports.namespace-import
      val copy = value
    }
    // format: on

    object NamedGiven {
      import net.kurobako.gemstone.importfixture.*
      import net.kurobako.gemstone.importfixture.Items.itemOrdering
      val ordering = summon[Ordering[Item]]
    }

    object Unrelated {
      object net {
        object kurobako {
          object gemstone {
            val value = 1
          }
        }
      }
      val copy = net.kurobako.gemstone.value
    }
  }
}
