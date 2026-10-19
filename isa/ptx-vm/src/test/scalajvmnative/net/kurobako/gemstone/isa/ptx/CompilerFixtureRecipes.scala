package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*

class CompilerFixtureRecipes extends NvidiaFixtureSuite {
  reproduces("CUDA 11.8.89 compute_35 saxpy PTX")(
    tools = (ptxas = "11.8.89", nvdisasm = None),
    inputs = Vec("saxpy.cu" -> CompilerFixtures.saxpySource),
    script = Vec("nvcc -ccbin gcc-11 -arch=compute_35 -ptx -O2 saxpy.cu -o output.ptx"),
    output = "output.ptx",
    expected = CompilerFixtures.saxpySm35Ptx
  )
  reproduces("CUDA 11.8.89 compute_35 device_call PTX")(
    tools = (ptxas = "11.8.89", nvdisasm = None),
    inputs = Vec("device_call.cu" -> CompilerFixtures.deviceCallSource),
    script = Vec("nvcc -ccbin gcc-11 -arch=compute_35 -ptx -O2 device_call.cu -o output.ptx"),
    output = "output.ptx",
    expected = CompilerFixtures.deviceCallSm35Ptx
  )
  reproduces("CUDA 13.3.73 compute_89 shared_local PTX")(
    tools = (ptxas = "13.3.73", nvdisasm = None),
    inputs = Vec("shared_local.cu" -> CompilerFixtures.sharedLocalSource),
    script = Vec("nvcc -arch=compute_89 -ptx -O2 shared_local.cu -o output.ptx"),
    output = "output.ptx",
    expected = CompilerFixtures.sharedLocalSm89Ptx
  )
  reproduces("CUDA 13.3.73 compute_89 packed PTX")(
    tools = (ptxas = "13.3.73", nvdisasm = None),
    inputs = Vec("packed.cu" -> CompilerFixtures.packedSource),
    script = Vec("nvcc -arch=compute_89 -ptx -O2 packed.cu -o output.ptx"),
    output = "output.ptx",
    expected = CompilerFixtures.packedSm89Ptx
  )
}
