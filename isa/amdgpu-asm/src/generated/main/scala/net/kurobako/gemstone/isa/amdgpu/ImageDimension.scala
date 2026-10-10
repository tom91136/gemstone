// Generated from LLVM AMDGPUDimProps. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

@javax.annotation.processing.Generated(Array("sbt codegen"))
enum ImageDimension(val encoding: Int, val assemblyName: String, val coordinates: Int, val gradients: Int) {
  case Image1d extends ImageDimension(0, "SQ_RSRC_IMG_1D", 1, 2)
  case Image2d extends ImageDimension(1, "SQ_RSRC_IMG_2D", 2, 4)
  case Image3d extends ImageDimension(2, "SQ_RSRC_IMG_3D", 3, 6)
  case ImageCube extends ImageDimension(3, "SQ_RSRC_IMG_CUBE", 3, 4)
  case Image1dArray extends ImageDimension(4, "SQ_RSRC_IMG_1D_ARRAY", 2, 2)
  case Image2dArray extends ImageDimension(5, "SQ_RSRC_IMG_2D_ARRAY", 3, 4)
  case Image2dMsaa extends ImageDimension(6, "SQ_RSRC_IMG_2D_MSAA", 3, 4)
  case Image2dMsaaArray extends ImageDimension(7, "SQ_RSRC_IMG_2D_MSAA_ARRAY", 4, 4)
}
