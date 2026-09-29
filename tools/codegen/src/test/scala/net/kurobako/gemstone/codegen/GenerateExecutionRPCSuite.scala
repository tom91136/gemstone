package net.kurobako.gemstone.codegen

import generator.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.testing.files.*

class GenerateExecutionRPCSuite extends munit.FunSuite {
  test("native requests retain wire field order, integer widths and scalar newtype representations") {
    inDirectory { output =>
      GenerateExecutionRPC.main(Array(output.toString))
      val files = snapshot(output)
      assertEquals(files.keySet, Set("gemstone_execution_rpc.hpp", "gemstone_execution_rpc.cpp"))
      val header = files("gemstone_execution_rpc.hpp").replaceAll("\\s+", " ")
      assert(header.contains(
        "struct ReadAllocation { AllocationId allocation{}; LongPositive0 offset{}; IntPositive0 size{}; };"
      ))
      assert(header.contains(
        "struct WriteAllocation { AllocationId allocation{}; LongPositive0 offset{}; std::vector<uint8_t> bytes{}; };"
      ))
      assert(header.contains("struct AllocationId { int64_t value{}; };"))
      assert(header.contains("struct IntPositive0 { int32_t value{}; };"))
      assert(header.contains(
        "template <> struct glz::meta<gemstone::execution::AllocationId> { static constexpr auto value = &gemstone::execution::AllocationId::value; };"
      ))
      assert(header.contains("inline constexpr std::string_view ReadAllocation = \"ReadAllocation\";"))
      assert(files(
        "gemstone_execution_rpc.hpp"
      ).contains(Emission.literal(ExecutionRPC.Schema.canonical)))
      val source = files("gemstone_execution_rpc.cpp").replaceAll("\\s+", " ")
      assert(source.contains(
        "Result<std::vector<uint8_t>> Client::call(const ReadAllocation &request) { return implementation_->call<std::vector<uint8_t>>(operation::ReadAllocation, request); }"
      ))
      GenerateExecutionRPC.main(Array(output.toString))
      assertEquals(snapshot(output), files)
    }
  }

  test("nullary enums, tagged sums and empty requests have distinct MessagePack representations") {
    inDirectory { output =>
      GenerateExecutionRPC.main(Array(output.toString))
      val header = snapshot(output)("gemstone_execution_rpc.hpp").replaceAll("\\s+", " ")
      assert(header.contains("enum class AllocationKind : uint8_t { Device = 0, Managed = 1, Shared = 2, Host = 3 };"))
      assert(header.contains(
        "using CompletionStatus = std::variant<CompletionStatusPending, CompletionStatusSucceeded, CompletionStatusFailed>;"
      ))
      assert(header.contains(
        "static constexpr std::string_view tag = \"t\"; static constexpr std::string_view content = \"v\"; static constexpr auto ids = std::array<std::string_view, 3>{\"Pending\", \"Succeeded\", \"Failed\"};"
      ))
      assert(header.contains(
        "template <> struct glz::meta<gemstone::execution::Ping> { static constexpr auto value = glz::object(); };"
      ))
    }
  }
}
