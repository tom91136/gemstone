package net.kurobako.gemstone.cuda

trait CUDA extends CUDASurface with CUDACustomSurface

object CUDA {
  val HeaderVersion: String = CUDASurface.HeaderVersion
  val SurfaceSize: Int      = CUDASurface.SurfaceSize
}
