package net.kurobako.gemstone.texture

/** Resolves handles against the table at its fixed address. Only the low 32 bits of a handle name an entry. */
object TextureTable {
  enum Failure derives CanEqual {

    /** The handle is zero or past the last entry. */
    case InvalidHandle(handle: Long)

    /** The entry is free, malformed, or of the other object kind. */
    case NotLive(handle: Long)

    /** The table or its entry is not readable. */
    case Unreadable(address: Long)
  }

  def index(handle: Long): Long = handle & 0xffffffffL

  def entryAddress(handle: Long): Option[Long] = {
    val slot = index(handle)
    Option.when(slot != 0L && slot < TextureLayout.Table.Entries)(
      TextureLayout.Table.BaseAddress + slot * TextureLayout.Table.EntryBytes
    )
  }

  def resolve(handle: Long, expected: ObjectKind, memory: TexelMemory): Either[Failure, TextureDescriptor] =
    for {
      address <- entryAddress(handle).toRight(Failure.InvalidHandle(handle))
      entry   <- memory.read(address, TextureLayout.Table.EntryBytes.toInt).toRight(Failure.Unreadable(address))
      found   <- TextureDescriptor.decode(entry).filter(_.kind == expected).toRight(Failure.NotLive(handle))
    } yield found
}
