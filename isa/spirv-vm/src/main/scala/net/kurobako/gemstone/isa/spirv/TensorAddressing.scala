package net.kurobako.gemstone.isa.spirv

/** The tensor layout, view and tensor transfer operations rendered definitions request of the machine. Layouts and
  * views are handles to the VM's state. A transfer takes the lane of the matrix it fills, then the matrix's lanes.
  */
private[isa] trait TensorAddressing {
  def createLayout[T](): T
  def setLayoutDimension[T](layout: Any, dimensions: Any*): T
  def setLayoutStride[T](layout: Any, strides: Any*): T
  def setLayoutBlockSize[T](layout: Any, sizes: Any*): T
  def sliceLayout[T](layout: Any, slices: Any*): T
  def setLayoutClampValue[T](layout: Any, value: Any): T
  def createView[T](): T
  def setViewDimension[T](view: Any, dimensions: Any*): T
  def setViewStride[T](view: Any, strides: Any*): T
  def setViewClip[T](view: Any, rowOffset: Any, rowSpan: Any, columnOffset: Any, columnSpan: Any): T
  def tensorLoad[T](lane: Any, arguments: Any*): T
  def tensorStore(arguments: Any*): Unit
  def loadAbsolute[T](address: Any, offset: Any, bytes: Any, pointee: Any): T
  def storeAbsolute(address: Any, offset: Any, bytes: Any, value: Any, pointee: Any): Unit
}
