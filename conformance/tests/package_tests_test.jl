using Test

include(joinpath(@__DIR__, "..", "interpreters", "julia", "package_tests.jl"))

@testset "package test listing" begin
    @testset "cases reads the runner list and failure-marked entries" begin
        listing = "Available tests:\n - array  (1.50s)\n × execution  (2.00s)\n - level-zero\n"
        @test cases(listing) == ["array", "execution", "level-zero"]
    end

    @testset "cases reads the boxed list a package prints before the runner list" begin
        listing = "[ Info: System information:\n┌ Info: Available tests:\n│ core/codegen\n│ gpuarrays/alloc cache\n└ \n"
        @test cases(listing) == ["core/codegen", "gpuarrays/alloc cache"]
    end

    @testset "listing does not run the package's dependency setup" begin
        ENV["JULIA_PKG_OFFLINE"] = "true"
        mktempdir() do directory
            script = joinpath(directory, "runtests.jl")
            write(script, "import Pkg\nPkg.add([\"EnzymeCore\", \"Enzyme\"])\nprintln(\"Available tests:\")\nprintln(\" - alpha\")\nexit(0)\n")
            passed, output = list_suite(script)
            @test passed
            @test cases(output) == ["alpha"]
        end
    end
    @testset "listing runs under the route's driver and without device selectors" begin
        mktempdir() do directory
            script = joinpath(directory, "runtests.jl")
            write(script, "println(\"Available tests:\")\nfor key in (\"LD_PRELOAD\", \"LD_LIBRARY_PATH\", \"ROCM_PATH\", \"CUDA_VISIBLE_DEVICES\", \"HIP_VISIBLE_DEVICES\", \"ROCR_VISIBLE_DEVICES\")\n  println(\" - \", key, \"=\", get(ENV, key, \"\"))\nend\nexit(0)\n")
            environment = Dict("LD_LIBRARY_PATH" => "/gemstone/driver", "ROCM_PATH" => "/gemstone/rocm", "CUDA_VISIBLE_DEVICES" => "0", "HIP_VISIBLE_DEVICES" => "0", "ROCR_VISIBLE_DEVICES" => "0")
            passed, output = withenv(environment...) do
                list_suite(script)
            end
            @test passed
            @test cases(output) == ["LD_PRELOAD=", "LD_LIBRARY_PATH=/gemstone/driver", "ROCM_PATH=/gemstone/rocm", "CUDA_VISIBLE_DEVICES=", "HIP_VISIBLE_DEVICES=", "ROCR_VISIBLE_DEVICES="]
        end
    end

    @testset "cases group by their top-level directory" begin
        @test batch_group("core/array") == "core"
        @test batch_group("gpuarrays/alloc cache") == "gpuarrays"
        @test batch_group("libraries/cublas/level1") == "libraries"
        @test batch_group("array") === nothing
        @test listing_line("core/array") == "core/array\tcore"
        @test listing_line("array") == "array"
    end

    @testset "runner rows become case markers" begin
        translate = translator(Set(["core/alpha", "core/beta", "core/gamma delta"]))
        @test translate("core/alpha         (1) \u2502         started at 2026-11-19T22:45:47.666") ==
              ["conformance-case-begin: core/alpha", "core/alpha         (1) \u2502         started at 2026-11-19T22:45:47.666"]
        passed = "core/alpha         (1) \u2502     0.08 \u2502     4.95 \u2502   96.81 \u2502   0.00 \u2502  0.0 \u2502       1.72 \u2502   466.16 \u2502"
        @test translate(passed) == [passed, "conformance-case-end: ?"]
        failed = "core/beta          (1) \u2502     0.02 \u2502     0.28 \u2502    failed at 2026-11-19T22:45:49.805"
        @test translate(failed) == [failed, "conformance-case-end: ?"]
        crashed = "core/beta          (1) \u2502         crashed at 2026-11-19T22:45:49.805"
        @test translate(crashed) == [crashed, "conformance-case-end: ?"]
        spaced = "core/gamma delta   (1) \u2502         started at 2026-11-19T22:45:49.831"
        @test translate(spaced)[1] == "conformance-case-begin: core/gamma delta"
        @test translate("    core/alpha       |    1            1  0.1s") == ["    core/alpha       |    1            1  0.1s"]
        @test translate("other/case          (1) \u2502         started at 2026-11-19T22:45:49.831") ==
              ["other/case          (1) \u2502         started at 2026-11-19T22:45:49.831"]
        @test translate("\e[31mcore/beta          (1) \u2502     0.02 \u2502     0.28 \u2502    failed at 2026-11-19T22:45:49.805\e[0m")[2] ==
              "conformance-case-end: ?"
    end

    @testset "the summary table says which tests failed" begin
        output = "noise\nTest Summary:        | Pass  Fail  Error  Total  Time\n  Overall            |    2     1      1      4  7.9s\n    core/alpha       |    1                   1  0.1s\n    core/beta        |          1             1  0.0s\n    core/gamma delta |    1                   1  0.0s\n    core/crashed     |                 1      1  0.0s\n\nError in testset core/beta:\n"
        names = ["core/alpha", "core/beta", "core/gamma delta", "core/crashed", "core/absent"]
        @test summary_statuses(output, names) == Dict("core/alpha" => 0, "core/beta" => 1, "core/gamma delta" => 0, "core/crashed" => 1)
        @test summary_statuses("no table here", names) == Dict{String, Int}()
    end

    @testset "a batch streams the runner's rows between markers and returns the whole output" begin
        mktempdir() do directory
            script = joinpath(directory, "runtests.jl")
            write(script, """
                println("jobs: ", join(ARGS, " "))
                println("core/one          (1) \u2502         started at now")
                println("core/one          (1) \u2502     0.08 \u2502     4.95 \u2502")
                println("core/two          (1) \u2502         started at now")
                println("core/two          (1) \u2502     0.09 \u2502     4.96 \u2502")
                println("core/three        (1) \u2502         started at now")
                println(stderr, "core/three        (1) \u2502     0.02 \u2502    failed at now")
                println("Test Summary:  | Pass  Fail  Total  Time")
                println("  Overall      |    2     2      4  7.9s")
                println("    core/one   |    1            1  0.1s")
                println("    core/two   |          1      1  0.0s")
                println("    core/three |          1      1  0.0s")
                exit(1)
                """)
            io = IOBuffer()
            passed, output = run_batch(script, ["core/one", "core/two", "core/three"], io)
            @test !passed
            lines = split(String(take!(io)), '\n')
            @test lines[1] == "jobs: --jobs=1 --verbose core/one core/two core/three"
            @test lines[2:3] == ["conformance-case-begin: core/one", "core/one          (1) \u2502         started at now"]
            @test count(==("conformance-case-end: ?"), lines) == 3
            @test count(==("conformance-case-end: 1"), lines) == 0
            @test lines[end - 2:end - 1] == ["conformance-case-status: 0 core/one", "conformance-case-status: 1 core/two"]
            @test !any(line -> startswith(line, "conformance-case-status: ") && occursin("core/three", line), lines)
            @test occursin("Test Summary", output)
        end
    end

    @testset "a passing batch settles every provisional case and a failure without a table settles none" begin
        mktempdir() do directory
            rows = "println(\"core/one          (1) \\u2502         started at now\")\nprintln(\"core/one          (1) \\u2502     0.08 \\u2502\")\n"
            good = joinpath(directory, "good.jl")
            write(good, rows)
            io = IOBuffer()
            passed, _ = run_batch(good, ["core/one"], io)
            @test passed
            @test occursin("conformance-case-status: 0 core/one", String(take!(io)))
            bad = joinpath(directory, "bad.jl")
            write(bad, rows * "exit(1)\n")
            passed, _ = run_batch(bad, ["core/one"], io)
            @test !passed
            @test !occursin("conformance-case-status", String(take!(io)))
        end
    end

    @testset "cases after the first failure are left unsettled so the driver reruns them in a fresh process" begin
        mktempdir() do directory
            script = joinpath(directory, "runtests.jl")
            write(script, """
                for name in ("core/one", "core/two", "core/three", "core/four")
                    println(name, "  (1) \\u2502         started at now")
                    println(name == "core/two" ? stderr : stdout, name, "  (1) \\u2502     0.02 \\u2502    ", name == "core/two" ? "failed at now" : "0.5 \\u2502")
                end
                println("Test Summary:  | Pass  Fail  Total  Time")
                println("  Overall      |    3     1      4  7.9s")
                for name in ("core/one", "core/two", "core/three", "core/four")
                    println("    ", name, " |", name == "core/two" ? "          1" : "    1    ", "      1  0.1s")
                end
                exit(1)
                """)
            io = IOBuffer()
            run_batch(script, ["core/one", "core/two", "core/three", "core/four"], io)
            statuses = filter(startswith("conformance-case-status: "), split(String(take!(io)), '\n'))
            @test statuses == ["conformance-case-status: 0 core/one", "conformance-case-status: 1 core/two"]
        end
    end
end
