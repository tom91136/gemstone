package net.kurobako.gemstone.testing

import java.nio.file.Files
import java.nio.file.Path
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Checks that no method of a class in the packages of `owners`, subpackages and nested and anonymous classes included,
  * is too large for the JIT. A package is read from the class path entry its owner comes from.
  */
class MethodSizeCheck(owners: Class[?]*) extends Suite {

  /** Whether the classes of this name, nested and anonymous ones included, are left out. */
  protected def excluded(name: String): Boolean = false

  test(s"${owners.map(_.getPackageName).distinct.mkString(", ")} has no method too large for the JIT") {
    val oversized = for {
      owner          <- owners
      (name, file)   <- classFiles(owner)
      (method, size) <- bytecode.methodSizes(file.toString, Files.newInputStream(file))
      if size > bytecode.MethodLimit
    } yield s"$name.$method" -> size
    assertEquals(oversized.toMap, Map.empty[String, Int])
  }

  private def classFiles(owner: Class[?]): Vector[(String, Path)] = {
    val segments = owner.getName.count(_ == '.') + 1
    val root     =
      Iterator.iterate(Path.of(owner.getResource(s"/${owner.getName.replace('.', '/')}.class").toURI))(_.getParent)
        .drop(segments).next()
    Using.resource(Files.walk(root.resolve(owner.getPackageName.replace('.', '/'))))(
      _.toList.asScala.view.filter(_.toString.endsWith(".class"))
        .map(file => root.relativize(file).toString.stripSuffix(".class").replace('/', '.') -> file)
        .filterNot((name, _) => excluded(name.split('.').last.takeWhile(_ != '$'))).toVector
    )
  }
}
