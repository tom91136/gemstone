# Bounded package tests for gemstone. The case list is the package's own runtests.jl --list, and one case runs one of
# those names in a fresh process, so selection stays by module or name pattern rather than a copied list.
# Usage: package_tests.jl <package> --list | --case <name>

function classify(text)
    if occursin("no kernel image", text) || occursin("invalid device function", text)
        return "missing code for the target"
    elseif occursin("symbol not found", text) || occursin("CUDA_ERROR_NOT_FOUND", text)
        return "missing entry point"
    end
    return "unclassified"
end

function execute(command; echo = true)
    path, io = mktemp()
    process = Base.run(pipeline(command, stdout = io, stderr = io); wait = false)
    wait(process)
    close(io)
    output = read(path, String)
    rm(path)
    if echo
        print(output)
    end
    return success(process), output
end

function package_root(package)
    path = Base.find_package(package)
    path === nothing && error("package is not available: $package")
    return dirname(dirname(path))
end

function suite(package)
    return joinpath(package_root(package), "test", "runtests.jl")
end

function cases(output)
    names = String[]
    for line in split(output, '\n')
        m = match(r"^ [-\u00d7] (.*?)(?:\s+\([0-9.]+s\))?$", line)
        m === nothing || push!(names, m.captures[1])
    end
    if isempty(names)
        boxed = false
        for line in split(output, '\n')
            if occursin(r"^\u250c Info: Available tests:", line)
                boxed = true
            elseif boxed
                m = match(r"^\u2502 (.+)$", line)
                m === nothing ? (boxed = false) : push!(names, m.captures[1])
            end
        end
    end
    if isempty(names)
        list = match(r"Available tests to run: \[(.*)\]", output)
        list === nothing || append!(names, [strip(name, '"') for name in split(list.captures[1], ", ") if !isempty(name)])
    end
    return names
end

const LISTING_WRAPPER = "import Pkg; Pkg.add(::Vector{String}; kwargs...) = nothing; include(popfirst!(ARGS))"

function list_suite(script)
    return withenv("CUDA_VISIBLE_DEVICES" => nothing, "ROCR_VISIBLE_DEVICES" => nothing, "HIP_VISIBLE_DEVICES" => nothing) do
        execute(`$(Base.julia_cmd()) --startup-file=no --project=$(Base.active_project()) -e $LISTING_WRAPPER $script --list`; echo = false)
    end
end

function main()
    package = ARGS[1]
    script = suite(package)
    isfile(script) || error("package has no test suite: $script")
    if "--list" in ARGS
        passed, output = list_suite(script)
        if !passed
            println(stderr, output)
            println("conformance-classification: ", classify(output))
            return 1
        end
        for name in cases(output)
            println(name)
        end
        return 0
    end
    index = findfirst(==("--case"), ARGS)
    index === nothing && error("--case is required unless --list is given")
    name = ARGS[index + 1]
    println("running: $package test $name")
    passed, output = execute(`$(Base.julia_cmd()) --startup-file=no --project=$(Base.active_project()) $script --jobs=1 $name`)
    if !passed
        println("conformance-classification: ", classify(output))
        return 1
    end
    return 0
end

if abspath(PROGRAM_FILE) == @__FILE__
    exit(main())
end
