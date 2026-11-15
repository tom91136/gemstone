# Bounded package tests for gemstone. The case list is the package's own runtests.jl --list, and one case runs one of
# those names in a fresh process, so selection stays by module or name pattern rather than a copied list.
# Usage: package_tests.jl <package> --list | --case <name>

function classify(text)
    if occursin("no kernel image", text) || occursin("invalid device function", text)
        return "missing code for the target"
    elseif occursin("symbol not found", text) || occursin("CUDA_ERROR_NOT_FOUND", text)
        return "missing entry point"
    end
    return "emulator defect"
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
        match(r"^ - (.+)$", line) === nothing || push!(names, match(r"^ - (.+)$", line).captures[1])
    end
    if isempty(names)
        list = match(r"Available tests to run: \[(.*)\]", output)
        list === nothing || append!(names, [strip(name, '"') for name in split(list.captures[1], ", ") if !isempty(name)])
    end
    return names
end

function main()
    package = ARGS[1]
    script = suite(package)
    isfile(script) || error("package has no test suite: $script")
    if "--list" in ARGS
        passed, output = execute(`$(Base.julia_cmd()) --startup-file=no --project=$(Base.active_project()) $script --list`)
        passed || (println("conformance-classification: ", classify(output)); return 1)
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

exit(main())
