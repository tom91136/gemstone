import java.nio.file.Files
import java.nio.file.Path
import sbt.io.IO

object Paths {
  extension (path: Path) {
    def /(child: String): Path                                  = path.resolve(child)
    def /(child: Path): Path                                    = path.resolve(child)
    def parent: Option[Path]                                    = Option(path.getParent)
    def absolute: Path                                          = path.toAbsolutePath.normalize
    def createDirectories(): Path                               = Files.createDirectories(path)
    def isFile: Boolean                                         = Files.isRegularFile(path)
    def withTempDirectory[A](prefix: String)(use: Path => A): A = {
      val directory = Files.createTempDirectory(path.createDirectories(), prefix)
      try use(directory)
      finally IO.delete(directory.toFile)
    }
  }
}
