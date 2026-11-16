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
end
