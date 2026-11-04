// Generated from Gemstone TextureDescriptorABI. Regenerate with: sbt codegen
package net.kurobako.gemstone.texture

import javax.annotation.processing.Generated

@Generated(Array("sbt codegen"))
object TextureLayout {
  object Table {
    inline val BaseAddress = 0x3f0000000000L
    inline val EntryBytes = 0x40L
    inline val Entries = 0x10000L
  }

  object Field {
    inline val Base = 0
    inline val Pitch = 8
    inline val SlicePitch = 16
    inline val Width = 24
    inline val Height = 28
    inline val Depth = 32
    inline val Object = 36
    inline val Dimensions = 37
    inline val ChannelKind = 38
    inline val Flags = 39
    inline val BitsX = 40
    inline val BitsY = 41
    inline val BitsZ = 42
    inline val BitsW = 43
    inline val AddressX = 44
    inline val AddressY = 45
    inline val AddressZ = 46
    inline val Filter = 47
    inline val BorderX = 48
    inline val BorderY = 52
    inline val BorderZ = 56
    inline val BorderW = 60
  }

  object Flag {
    inline val NormalisedCoordinates = 1
    inline val Srgb = 2
    inline val NormalisedRead = 4
    inline val ResourceShift = 3
    inline val ResourceMask = 3
  }

  object KeplerTable {
    inline val BaseAddress = 0x7e00000000L
  }

  object Object {
    inline val Free = 0
    inline val Texture = 1
    inline val Surface = 2
  }

  object Channel {
    inline val Signed = 0
    inline val Unsigned = 1
    inline val Float = 2
    inline val UnsignedNormalised = 3
    inline val SignedNormalised = 4
  }

  object Address {
    inline val Wrap = 0
    inline val Clamp = 1
    inline val Mirror = 2
    inline val Border = 3
  }

  object Filter {
    inline val Point = 0
    inline val Linear = 1
  }

  object Resource {
    inline val Array = 0
    inline val MipmappedArray = 1
    inline val Linear = 2
    inline val Pitch2D = 3
  }

  object Kepler {
    inline val Base = 0
    inline val Format = 4
    inline val LimitX = 8
    inline val Pitch = 12
    inline val LimitY = 16
    inline val LimitZ = 24
    inline val Height = 28
    inline val Bytes = 32
    inline val Alignment = 256
    inline val MaxPitch = 65280
    inline val MaxRows = 65535
    inline val MaxBytes = 65536
  }
}
