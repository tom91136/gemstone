/*
rule = GemstoneNoExplicitNew
 */
package fix

object NoExplicitNew {
  val rejected  = new StringBuilder() // assert: GemstoneNoExplicitNew.explicit-new
  val anonymous = new Runnable {      // assert: GemstoneNoExplicitNew.anonymous-allocation
    override def run(): Unit = ()
  }
  val accepted   = StringBuilder()
  val suppressed = new StringBuilder() // scalafix:ok GemstoneNoExplicitNew
}
