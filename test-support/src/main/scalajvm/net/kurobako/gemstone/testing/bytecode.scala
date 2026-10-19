package net.kurobako.gemstone.testing

import java.io.DataInputStream
import java.io.InputStream
import scala.annotation.tailrec
import scala.util.Using

/** Method bytecode sizes read from class files. HotSpot never JIT-compiles a method whose bytecode exceeds
  * `HugeMethodLimit` bytes, so per-instruction interpreter and compiler paths must stay below it.
  */
object bytecode {
  val HugeMethodLimit: Int = 8000

  /** The size beyond which a method fails its check, a margin under [[HugeMethodLimit]] so that growth fails the check
    * before HotSpot stops compiling the method.
    */
  val MethodLimit: Int = HugeMethodLimit - 1000

  /** Bytecode length of every method of `owner` that has code, keyed by name and descriptor. */
  def methodSizes(owner: Class[?]): Map[String, Int] = {
    val resource = s"/${owner.getName.replace('.', '/')}.class"
    methodSizes(resource, owner.getResourceAsStream(resource))
  }

  /** Bytecode length of every method of the class file in `stream`, which it closes, keyed by name and descriptor. */
  def methodSizes(resource: String, stream: InputStream): Map[String, Int] =
    Using.resource(DataInputStream(stream)) { in =>
      val _         = in.skipBytes(8)
      val constants = in.readUnsignedShort()
      val utf8      = Array.ofDim[String](constants)
      // long and double entries occupy two constant pool slots
      @tailrec def readConstants(index: Int): Unit = if (index < constants) {
        val slots = in.readUnsignedByte() match {
          case 1                                  => utf8(index) = in.readUTF(); 1
          case 5 | 6                              => in.skipBytes(8); 2
          case 3 | 4 | 9 | 10 | 11 | 12 | 17 | 18 => in.skipBytes(4); 1
          case 15                                 => in.skipBytes(3); 1
          case 7 | 8 | 16 | 19 | 20               => in.skipBytes(2); 1
          case tag => throw IllegalArgumentException(s"$resource: constant pool tag $tag")
        }
        readConstants(index + slots)
      }
      readConstants(1)
      val _                      = in.skipBytes(6)
      val _                      = in.skipBytes(2 * in.readUnsignedShort())
      def skipAttributes(): Unit = (0 until in.readUnsignedShort()).foreach { _ =>
        val _ = in.skipBytes(2)
        val _ = in.skipBytes(in.readInt())
      }
      (0 until in.readUnsignedShort()).foreach { _ =>
        val _ = in.skipBytes(6)
        skipAttributes()
      }
      (0 until in.readUnsignedShort()).flatMap { _ =>
        val _          = in.skipBytes(2)
        val name       = utf8(in.readUnsignedShort())
        val descriptor = utf8(in.readUnsignedShort())
        (0 until in.readUnsignedShort()).flatMap { _ =>
          val attribute = utf8(in.readUnsignedShort())
          val length    = in.readInt()
          if (attribute == "Code") {
            val _    = in.skipBytes(4)
            val size = in.readInt()
            val _    = in.skipBytes(length - 8)
            Some(s"$name$descriptor" -> size)
          } else {
            val _ = in.skipBytes(length)
            None
          }
        }
      }.toMap
    }

  /** Methods of `owner` too large for the JIT. */
  def oversized(owner: Class[?]): Map[String, Int] = methodSizes(owner).filter((_, size) => size > HugeMethodLimit)
}
