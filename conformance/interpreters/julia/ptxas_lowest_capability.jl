using CUDACore

capability = minimum(CUDACore.ptxas_compat().cap)
println(Int(capability.major) * 10 + Int(capability.minor))
