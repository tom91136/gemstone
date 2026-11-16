import Pkg

Pkg.add(::Vector{String}; kwargs...) = nothing

if Base.find_package("CUDA") !== nothing
    using CUDA
    @eval $(parentmodule(CUDA.versioninfo)).versioninfo(::IO) = nothing
end

include(popfirst!(ARGS))
