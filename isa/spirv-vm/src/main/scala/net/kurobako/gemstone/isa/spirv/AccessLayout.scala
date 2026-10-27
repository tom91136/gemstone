package net.kurobako.gemstone.isa.spirv

import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.vm.*

/** The layout walk of an access chain, written once over the representation `V` of an address: execution walks `Long`
  * addresses and rendering walks terms. The walk reads the type layout of the program for the byte offset of each
  * index.
  */
private object AccessLayout {

  /** Walks the `indexes` from the pointer `start`, preceded by `element` strides of the base type for a pointer chain.
    * `number` is the value of index `position` and `index` its representation, the element index at -1; `pending` is
    * the fault reading an index raised, which stops the walk. It ends in `reached`, with the address, the type and
    * matrix layout reached and the pointer whose descriptor the chain selected, or in `failed`.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
  inline def walk[V, A](
      chain: AccessChain,
      start: Pointer,
      element: Boolean,
      count: Int,
      origin: V,
      inline address: Long => V,
      inline number: Int => Long,
      inline index: Int => V,
      inline pending: () => Fault | Null,
      inline plus: (V, Long) => V,
      inline scaled: (V, V, Long) => V,
      inline failed: Fault => A,
      inline reached: (V, Int, Option[MatrixLayout], Pointer) => A
  ): A = {
    val program               = chain.program
    var source                = start
    var matrixLayout          = start.matrixLayout
    var offset                = origin
    var typeId                = chain.baseType.getOrElse(start.pointee)
    var position              = 0
    var failure: Fault | Null = null
    if (element) {
      val first = index(-1)
      failure = pending()
      if (failure == null) {
        val stride = chain.baseStride.getOrElse(program.storageTypeSize(typeId, start.matrixLayout).raw.toLong)
        offset = scaled(offset, first, stride)
      }
    }
    while (position < count && failure == null) {
      val selected = number(position)
      val current  = index(position)
      failure = pending()
      if (failure == null) program.runtimeType(typeId) match {
        case Some(RuntimeType.Structure(members)) =>
          if (selected < 0L || selected >= members.size.toLong)
            failure = Fault.InvalidRequest(s"structure member index $selected is out of range")
          else {
            offset = plus(offset, program.structMemberOffset(typeId, members, selected.toInt).raw.toLong)
            matrixLayout = program.memberMatrixLayouts.get(typeId -> selected.toInt)
            typeId = members(selected.toInt)
          }
        case Some(RuntimeType.Array(member, _)) =>
          source.descriptors match {
            case Some(descriptors) =>
              if (selected < 0 || selected >= descriptors.size || element)
                failure = Fault.InvalidRequest(s"descriptor array index $selected is out of range")
              else {
                source = descriptors(selected.toInt)
                offset = address(source.address)
              }
            case None =>
              val stride = program.arrayStrides.get(typeId) match {
                case Some(width) => width.raw.toLong
                case None        => program.storageTypeSize(member, matrixLayout).raw.toLong
              }
              offset = scaled(offset, current, stride)
          }
          typeId = member
        case Some(RuntimeType.MatrixType(column, _)) =>
          val stride: Long =
            if (matrixLayout.exists(_.rowMajor)) program.runtimeType(column) match {
              case Some(RuntimeType.VectorType(component, _)) => program.typeSize(component).raw.toLong
              case _                                          =>
                failure = Fault.InvalidRequest("invalid matrix column type")
                0L
            }
            else matrixLayout.fold(program.typeSize(column).raw.toLong)(_.stride.raw.toLong)
          offset = scaled(offset, current, stride)
          matrixLayout = matrixLayout.filter(_.rowMajor)
          typeId = column
        case Some(RuntimeType.VectorType(component, _)) =>
          offset = scaled(
            offset,
            current,
            matrixLayout.filter(_.rowMajor).map(_.stride.raw.toLong).getOrElse(program.typeSize(component).raw.toLong)
          )
          matrixLayout = None
          typeId = component
        case Some(value) => failure = Fault.InvalidRequest(s"cannot index through SPIR-V type $value")
        case None        => failure = Fault.InvalidRequest(s"SPIR-V type %$typeId is undefined")
      }
      position += 1
    }
    if (failure != null) failed(failure) else reached(offset, typeId, matrixLayout, source)
  }
}
