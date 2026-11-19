package net.kurobako.gemstone.isa.check

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.ControlWords
import net.kurobako.gemstone.isa.sass.Cubin
import net.kurobako.gemstone.isa.sass.RegeneratedCubin

/** Writes each cubin, under its own name in the output directory, with the scheduling fields of every kernel
  * regenerated from the control-word table of its target.
  */
object RegeneratedCubinRunner {

  private val usage = "usage: RegeneratedCubinRunner <output directory> <cubin>..."

  def main(arguments: Array[String]): Unit = {
    if (arguments.length < 2) throw IllegalArgumentException(usage)
    val output = Files.createDirectories(Path.of(arguments(0)))
    arguments.drop(1).foreach { input =>
      val image       = Vec.unsafeWrapArray(Files.readAllBytes(Path.of(input)))
      val regenerated = for {
        module <- Cubin.decode(image).left.map(_.raw)
        table  <- ControlWords.tables.get(s"sm_${module.sm}").toRight(s"no control-word table for sm_${module.sm}")
        bytes  <- RegeneratedCubin(image, table).left.map(_.raw)
      } yield bytes
      regenerated.fold(
        error => println(s"$input: $error"),
        bytes => {
          val target = output.resolve(Path.of(input).getFileName)
          Files.write(target, bytes.toArray)
          println(s"$input -> $target")
        }
      )
    }
  }
}
