/*
rule = GemstoneNoPackagePrivate
 */
package fix.visibility {
  private[visibility] class PackageClass // assert: GemstoneNoPackagePrivate.package-private
  private[fix] object AncestorObject     // assert: GemstoneNoPackagePrivate.package-private

  class Constructor private[visibility] () // assert: GemstoneNoPackagePrivate.package-private

  class Auxiliary(value: Int) {
    private[visibility] def this() = this(0) // assert: GemstoneNoPackagePrivate.package-private
  }
  class Parameter(private[visibility] val value: Int) // assert: GemstoneNoPackagePrivate.package-private
  class PrivateConstructor private ()

  object Members {
    private[visibility] val value = 1     // assert: GemstoneNoPackagePrivate.package-private
    private[fix] def method: Int  = value // assert: GemstoneNoPackagePrivate.package-private
    private[visibility] type Alias = Int // assert: GemstoneNoPackagePrivate.package-private

    @scala.annotation.publicInBinary
    private[visibility] def binaryVisible: Int = 1 // assert: GemstoneNoPackagePrivate.package-private

    @SuppressWarnings(Array("scalafix:GemstoneNoPackagePrivate"))
    @scala.annotation.publicInBinary
    private[visibility] def macroBoundary: Int = 1

    private val local                             = 1
    def publicValue: Int                          = local
    protected[visibility] def protectedValue: Int = local
  }

  private object Namespace {
    def value: Int = 1
  }

  object Owner {
    private[Owner] class Nested
    class Constructed private[Owner] ()
    object Child {
      private[Owner] val value = 1
    }
  }

  // An enclosing object may have the same spelling as the package.
  object visibility {
    private[visibility] val value = 1
  }
}

// Package names need not start with a lowercase letter.
package fix.Uppercase {
  private[Uppercase] class PackageClass // assert: GemstoneNoPackagePrivate.package-private
}
