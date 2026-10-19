package net.kurobako.gemstone.explorer

extension (language: Language) {
  def model: Model = language match {
    case Language.PTX    => PTXModel
    case Language.SASS   => SASSModel
    case Language.AMDGPU => AMDGPUModel
    case Language.SPIRV  => SPIRVModel
  }
}
