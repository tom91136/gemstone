# Bounded package tests for gemstone. The case list is the package's own runtests.jl --list, and one case runs one of
# those names in a fresh process, so selection stays by module or name pattern rather than a copied list. Cases under one
# top-level test directory form a batch group that --batch runs in one process.
# Usage: package_tests.jl <package> --list | --case <name> | --batch <file of case names>

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

function batch_group(name)
    slash = findfirst('/', name)
    return slash === nothing ? nothing : name[1:prevind(name, slash)]
end

function listing_line(name)
    group = batch_group(name)
    return group === nothing ? name : "$name\t$group"
end

const RUNNER_ROW = Regex("^(.+?)\\s+\\(\\d+\\)\\s+\u2502(.*)\$")

function translator(names, provisional = String[])
    return function (line)
        row = match(RUNNER_ROW, replace(line, r"\e\[[0-9;]*m" => ""))
        row === nothing && return [line]
        name = row.captures[1]
        name in names || return [line]
        occursin("started at", row.captures[2]) && return ["conformance-case-begin: $name", line]
        occursin(r"\b(failed|crashed) at\b", row.captures[2]) && return [line, "conformance-case-end: 1"]
        push!(provisional, name)
        return [line, "conformance-case-end: ?"]
    end
end

# the runner prints an ordinary row for a test whose nested testsets failed, so only the closing table is reliable
function summary_statuses(output, names)
    statuses = Dict{String, Int}()
    columns = nothing
    for line in split(output, '\n')
        if columns === nothing
            occursin(r"^Test Summary:\s*\|", line) || continue
            bar = findfirst('|', line)
            columns = Dict(m.match => bar + m.offset + length(m.match) - 1 for m in eachmatch(r"\S+", line[bar + 1:end]))
            continue
        end
        bar = findfirst('|', line)
        bar === nothing && break
        name = strip(line[1:prevind(line, bar)])
        name in names || continue
        stops = Set(bar + m.offset + length(m.match) - 1 for m in eachmatch(r"\S+", line[bar + 1:end]))
        statuses[name] = any(column -> haskey(columns, column) && columns[column] in stops, ("Fail", "Error")) ? 1 : 0
    end
    return statuses
end

function run_batch(script, names, io = stdout)
    provisional = String[]
    translate = translator(Set(names), provisional)
    command = `$(Base.julia_cmd()) --startup-file=no --project=$(Base.active_project()) $script --jobs=1 --verbose $names`
    pipe = Pipe()
    process = Base.run(pipeline(command, stdout = pipe, stderr = pipe); wait = false)
    close(pipe.in)
    output = IOBuffer()
    for line in eachline(pipe.out)
        for translated in translate(line)
            println(io, translated)
        end
        flush(io)
        println(output, line)
    end
    wait(process)
    passed = success(process)
    text = String(take!(output))
    statuses = passed ? Dict(name => 0 for name in provisional) : summary_statuses(text, provisional)
    for name in provisional
        haskey(statuses, name) && println(io, "conformance-case-status: $(statuses[name]) $name")
    end
    flush(io)
    return passed, text
end

const LISTING_WRAPPER = joinpath(@__DIR__, "listing.jl")

function list_suite(script)
    return withenv("CUDA_VISIBLE_DEVICES" => nothing, "ROCR_VISIBLE_DEVICES" => nothing, "HIP_VISIBLE_DEVICES" => nothing) do
        execute(`$(Base.julia_cmd()) --startup-file=no --project=$(Base.active_project()) $LISTING_WRAPPER $script --list`; echo = false)
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
            println(listing_line(name))
        end
        return 0
    end
    batch = findfirst(==("--batch"), ARGS)
    if batch !== nothing
        names = filter(!isempty, readlines(ARGS[batch + 1]))
        passed, output = run_batch(script, names)
        if !passed
            println("conformance-classification: ", classify(output))
            return 1
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
