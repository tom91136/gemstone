package net.kurobako.gemstone.explorer

enum Language(val title: String) derives CanEqual {
  case PTX    extends Language("PTX")
  case SASS   extends Language("SASS")
  case AMDGPU extends Language("AMDGPU")
  case SPIRV  extends Language("SPIR-V")
}
