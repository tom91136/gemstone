package net.kurobako.gemstone.memory

import net.kurobako.gemstone.rpc.Wire.Codec

enum AllocationKind derives Codec {
  case Device
  case Managed
  case Shared
  case Host
}
