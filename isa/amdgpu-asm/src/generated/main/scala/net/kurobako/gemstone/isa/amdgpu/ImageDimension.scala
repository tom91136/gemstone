// Generated from LLVM AMDGPUDimProps. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum ImageDimension(val encoding: Int, val assemblyName: String, val coordinates: Int, val gradients: Int)
    derives CanEqual {
  case Image1d extends ImageDimension(encoding = 0, assemblyName = "SQ_RSRC_IMG_1D", coordinates = 1, gradients = 2)
  case Image2d extends ImageDimension(encoding = 1, assemblyName = "SQ_RSRC_IMG_2D", coordinates = 2, gradients = 4)
  case Image3d extends ImageDimension(encoding = 2, assemblyName = "SQ_RSRC_IMG_3D", coordinates = 3, gradients = 6)
  case ImageCube extends ImageDimension(encoding = 3, assemblyName = "SQ_RSRC_IMG_CUBE", coordinates = 3, gradients = 4)
  case Image1dArray
      extends ImageDimension(encoding = 4, assemblyName = "SQ_RSRC_IMG_1D_ARRAY", coordinates = 2, gradients = 2)
  case Image2dArray
      extends ImageDimension(encoding = 5, assemblyName = "SQ_RSRC_IMG_2D_ARRAY", coordinates = 3, gradients = 4)
  case Image2dMsaa
      extends ImageDimension(encoding = 6, assemblyName = "SQ_RSRC_IMG_2D_MSAA", coordinates = 3, gradients = 4)
  case Image2dMsaaArray
      extends ImageDimension(encoding = 7, assemblyName = "SQ_RSRC_IMG_2D_MSAA_ARRAY", coordinates = 4, gradients = 4)
}
