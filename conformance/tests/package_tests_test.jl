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
end
