package net.kurobako.gemstone.memory

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type DeviceAllocationByteSize = DeviceAllocationByteSize.T
object DeviceAllocationByteSize extends RefinedType[Int, Positive] {
  def fromLong(value: Long): Either[String, DeviceAllocationByteSize] =
    if (value < 1L || value > Int.MaxValue.toLong)
      Left(s"device allocation size is outside 1 to ${Int.MaxValue}: $value")
    else either(value.toInt)
}
