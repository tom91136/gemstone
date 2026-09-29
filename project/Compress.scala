import Paths.*
import java.io.BufferedInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Traversal-safe extraction for the archive formats consumed by the build. */
object Compress {
  def extractTarXz(
      archive: Path,
      destination: Path,
      archiveRoot: String,
      select: Path => Boolean
  ): Unit = extractTar(
    archive = archive,
    destination = destination,
    archiveRoot = archiveRoot,
    select = select
  ) { input =>
    XZCompressorInputStream.builder().setInputStream(input).setDecompressConcatenated(true).get()
  }

  def extractTarGz(
      archive: Path,
      destination: Path,
      archiveRoot: String,
      select: Path => Boolean
  ): Unit = extractTar(
    archive = archive,
    destination = destination,
    archiveRoot = archiveRoot,
    select = select
  )(GzipCompressorInputStream(_))

  def extractDebXz(archive: Path, destination: Path, select: Path => Boolean): Unit = {
    val _     = destination.createDirectories()
    val found = Using.Manager { use =>
      val arInput = use(ArArchiveInputStream(use(BufferedInputStream(Files.newInputStream(archive)))))
      archiveEntries(arInput.getNextEntry).find(_.getName == "data.tar.xz") match {
        case Some(_) =>
          val xzInput = use(XZCompressorInputStream.builder()
            .setInputStream(arInput)
            .setDecompressConcatenated(true)
            .get())
          val tarInput = use(TarArchiveInputStream(xzInput))
          archiveEntries(tarInput.getNextEntry).foreach { tarEntry =>
            val relative = Option.when(!tarEntry.isDirectory)(safeRelative(tarEntry.getName.stripPrefix("./")))
            relative.filter(select).foreach { relative =>
              require(tarEntry.isFile, s"selected archive entry is not a regular file: ${tarEntry.getName}")
              require(
                !tarEntry.isSymbolicLink && !tarEntry.isLink,
                s"archive links are not permitted: ${tarEntry.getName}"
              )
              copyEntry(tarInput, destination.resolveWithin(relative))
            }
          }
          true
        case None => false
      }
    }.get
    require(found, s"Debian archive has no data.tar.xz payload: $archive")
  }

  def extractZip(archive: Path, destination: Path, select: Path => Boolean): Unit =
    extractZip(archive = archive, destination = destination, archiveRoot = None, select = select)

  def extractZipRoot(
      archive: Path,
      destination: Path,
      archiveRoot: String,
      select: Path => Boolean
  ): Unit = extractZip(
    archive = archive,
    destination = destination,
    archiveRoot = Some(archiveRoot),
    select = select
  )

  private def extractTar(
      archive: Path,
      destination: Path,
      archiveRoot: String,
      select: Path => Boolean
  )(decompress: InputStream => InputStream): Unit = {
    val _ = destination.createDirectories()
    Using.Manager { use =>
      val fileInput       = use(BufferedInputStream(Files.newInputStream(archive)))
      val compressedInput = use(decompress(fileInput))
      val tarInput        = use(TarArchiveInputStream(compressedInput))
      archiveEntries(tarInput.getNextEntry).foreach { entry =>
        val relative = archiveRelative(entry.getName, archiveRoot)
        relative.filter(_ => !entry.isDirectory).filter(select).foreach { relative =>
          require(entry.isFile, s"selected archive entry is not a regular file: ${entry.getName}")
          require(!entry.isSymbolicLink && !entry.isLink, s"archive links are not permitted: ${entry.getName}")
          copyEntry(tarInput, destination.resolveWithin(relative))
        }
      }
    }.get
  }

  private def extractZip(
      archive: Path,
      destination: Path,
      archiveRoot: Option[String],
      select: Path => Boolean
  ): Unit = {
    val _ = destination.createDirectories()
    Using.Manager { use =>
      val zipInput = use(ZipInputStream(use(BufferedInputStream(Files.newInputStream(archive)))))
      archiveEntries(zipInput.getNextEntry).foreach { entry =>
        val relative = archiveRoot.fold(Some(safeRelative(entry.getName)))(root => archiveRelative(entry.getName, root))
        relative.filter(_ => !entry.isDirectory).filter(select).foreach { relative =>
          copyEntry(zipInput, destination.resolveWithin(relative))
        }
        zipInput.closeEntry()
      }
    }.get
  }

  private def archiveRelative(name: String, archiveRoot: String): Option[Path] = {
    val path = safeRelative(name)
    require(path.getNameCount >= 1 && s"${path.getName(0)}" == archiveRoot, s"unexpected archive root: $name")
    if (path.getNameCount == 1) None else Some(path.subpath(1, path.getNameCount))
  }

  private def safeRelative(name: String): Path = {
    require(name.nonEmpty && !name.contains('\\') && !name.contains('\u0000'), s"unsafe archive path: $name")
    val raw = path"$name"
    require(!raw.isAbsolute, s"absolute archive path is not permitted: $name")
    require(
      raw.asScala.view.forall(segment => s"$segment" != "." && s"$segment" != ".."),
      s"archive traversal is not permitted: $name"
    )
    raw.normalize
  }

  private def copyEntry(input: InputStream, target: Path): Unit = {
    target.parent.foreach(_.createDirectories())
    val _ = Files.copy(input, target)
  }

  private def archiveEntries[A](next: => A): Iterator[A] =
    Iterator.continually(Option(next)).takeWhile(_.nonEmpty).flatten
}
