package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.constraint.*
import scala.collection.mutable

object VMRegisterFile {
  type Slot       = Int in 0 ~ 6143
  type ChunkCount = Int in 1 ~ 96

  def slot(name: NonEmpty[String]): Option[Slot] = {
    val index = indexRaw(name)
    if (index < 0) None else Some(index.unsafe[Slot])
  }

  // This is on the execution hot path; parse without allocating substrings or retaining names globally.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  def indexRaw(name: String): Int = {
    val length = name.length
    val start  = if (length > 0 && name.charAt(0) == '%') 1 else 0
    if (start == length) return -1
    // Offsets are multiples of 1024, so the low three bits can carry the digit position without a tuple allocation.
    val parsed = name.charAt(start) match {
      case 'r' =>
        if (start + 1 < length) name.charAt(start + 1) match {
          case 'd' => start + 2
          case 's' => 1024 | (start + 2)
          case _   => 2048 | (start + 1)
        }
        else return -1
      case 'f' =>
        if (start + 1 < length && name.charAt(start + 1) == 'd') 3072 | (start + 2)
        else 4096 | (start + 1)
      case 'p' => 5120 | (start + 1)
      case _   => return -1
    }
    val digits = parsed & 7
    val offset = parsed & ~7
    if (digits == length) return -1

    var index = digits
    var value = 0
    while (index < length) {
      val digit = name.charAt(index) - '0'
      if (digit < 0 || digit > 9) return -1
      value = value * 10 + digit
      if (value >= 1024) return -1
      index += 1
    }
    value + offset
  }

  def index(name: NonEmpty[String]): Int = indexRaw(name)

  def name(index: Int): NonEmpty[String] =
    if (index >= 0 && index < 1024) s"%rd$index"
    else if (index < 2048) s"%rs${index - 1024}"
    else if (index < 3072) s"%r${index - 2048}"
    else if (index < 4096) s"%fd${index - 3072}"
    else if (index < 5120) s"%f${index - 4096}"
    else if (index < 6144) s"%p${index - 5120}"
    else s"%r$index"
}

/** Dense storage for canonical register names. Register names are stable for a kernel, so parsing the small numeric
  * register suffix on every access is cheaper than allocating and hashing a String in a mutable map.
  */
// Lazy chunks avoid allocating 48 KiB per lane in the register-file hot path.
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
class VMRegisterFile(chunkCount: VMRegisterFile.ChunkCount = 96) {
  private var values: Array[Array[Long] | Null] | Null = null
  private var named: mutable.Map[String, Long] | Null  = null

  def get(name: NonEmpty[String]): Long = {
    val index = VMRegisterFile.indexRaw(name)
    if (index >= 0) getSlotRaw(index)
    else named match {
      case null   => 0L
      case values => values.getOrElse(name, 0L)
    }
  }

  inline def getSlot(index: VMRegisterFile.Slot): Long = getSlotRaw(index.raw)

  inline def updateSlot(index: VMRegisterFile.Slot, value: Long): Unit = updateSlotRaw(index.raw, value)

  def getSlotRaw(index: Int): Long = {
    require(index >= 0 && index < 6144, s"invalid register slot: $index")
    values match {
      case null                                     => 0L
      case chunks if (index >>> 6) >= chunks.length => 0L
      case chunks                                   => chunks(index >>> 6) match {
          case null  => 0L
          case chunk => chunk(index & 63)
        }
    }
  }

  def apply(name: NonEmpty[String]): Long = get(name)

  def update(name: NonEmpty[String], value: Long): Unit = {
    val index = VMRegisterFile.indexRaw(name)
    if (index >= 0) updateSlotRaw(index, value)
    else {
      val values = named match {
        case null =>
          val allocated = mutable.Map.empty[String, Long]
          named = allocated
          allocated
        case existing => existing
      }
      values(name) = value
    }
  }

  def updateSlotRaw(index: Int, value: Long): Unit = {
    require(index >= 0 && index < 6144, s"invalid register slot: $index")
    val chunkIndex = index >>> 6
    val chunks     = values match {
      case null =>
        val allocated = Array.ofDim[Array[Long] | Null](math.max(chunkCount.raw, chunkIndex + 1))
        values = allocated
        allocated
      // The initial chunk count is an allocation hint, not an architectural limit.
      case existing if chunkIndex >= existing.length =>
        val grown = java.util.Arrays.copyOf(existing, math.min(96, math.max(chunkIndex + 1, existing.length * 2)))
        values = grown
        grown
      case existing => existing
    }
    chunks(chunkIndex) match {
      case null =>
        val chunk = Array.ofDim[Long](64)
        chunks(chunkIndex) = chunk
        chunk(index & 63) = value
      case chunk => chunk(index & 63) = value
    }
  }

  def clear(): Unit = {
    values match {
      case null   => ()
      case chunks =>
        var index = 0
        while (index < chunks.length) {
          chunks(index) match {
            case null  => ()
            case chunk => java.util.Arrays.fill(chunk, 0L)
          }
          index += 1
        }
    }
    named match {
      case null   => ()
      case values => values.clear()
    }
  }

}
