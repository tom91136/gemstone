# BabelStream cases for gemstone, using the pinned Julia implementation and the route's Julia package environment.
# Usage: babelstream.jl <cuda|amdgpu|oneapi> --list | --case <name>

const backends = Dict(
    "cuda" => "CUDAStream.jl",
    "amdgpu" => "AMDGPUStream.jl",
    "oneapi" => "oneAPIStream.jl",
    "ka" => "KernelAbstractionsStream.jl",
    "kernelabstractions" => "KernelAbstractionsStream.jl",
)

function classify(text)
    if occursin("no kernel image", text) || occursin("invalid device function", text)
        return "missing code for the target"
    elseif occursin("symbol not found", text) || occursin("CUDA_ERROR_NOT_FOUND", text)
        return "missing entry point"
    elseif occursin("@LocalMemory", text) && occursin("not defined", text) || occursin("Device 1 out of range (1..0)", text)
        return "package mismatch"
    end
    return "unclassified"
end

function execute(command)
    path, io = mktemp()
    process = Base.run(pipeline(command, stdout = io, stderr = io); wait = false)
    wait(process)
    close(io)
    output = read(path, String)
    rm(path)
    print(output)
    return success(process), output
end

function main()
    backend = ARGS[1]
    haskey(backends, backend) || error("unknown backend: $backend")
    source = joinpath(@__DIR__, "babelstream", "src", backends[backend])
    if "--list" in ARGS
        println("babelstream")
        return 0
    end
    index = findfirst(==("--case"), ARGS)
    index === nothing && error("--case is required unless --list is given")
    ARGS[index + 1] == "babelstream" || error("unknown case: $(ARGS[index + 1])")
    arguments = ["-s", "4096", "-n", "2"]
    if backend == "oneapi"
        push!(arguments, "--float")
    end
    command = `$(Base.julia_cmd()) --startup-file=no --project=$(Base.active_project()) $source $arguments`
    println("running: ", command)
    passed, output = execute(command)
    if !passed
        println("conformance-classification: ", classify(output))
        return 1
    end
    return 0
end

exit(main())
