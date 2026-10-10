package net.kurobako.gemstone.isa.ptx

import Program.*
import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class LinkerSuite extends munit.FunSuite {
  test("linked modules isolate private data and functions while preserving local shadowing") {
    def module(entry: String, result: Int): String = s""".version 8.0
      .target sm_80
      .address_size 64
      .global .u32 value = $result;
      .visible .global .u64 $entry = generic(value);
      .func (.param .u32 result) helper(.param .u32 value) {
        .reg .u32 %value;
        ld.param.u32 %value, [value];
        add.u32 %value, %value, $result;
        st.param.u32 [result], %value;
        ret;
      }
      .visible .entry $entry(.param .u64 output) {
        .reg .u64 %output, %function;
        .reg .u32 %value;
        .param .u32 result, argument;
        ld.param.u64 %output, [output];
        st.param.u32 [argument], 1;
        mov.u64 %function, helper;
        call (result), %function, (argument);
        ld.param.u32 %value, [result];
        st.global.u32 [%output], %value;
        ret;
      }"""
    val parsed    = Parser.parse(module("first", 41) + "\n" + module("second", 83)).toOption.get
    val linked    = Linker.link(parsed).toOption.get
    val functions = linked.statements.collect { case function: Statement.Function => function }
    assertEquals(functions.map(_.name).distinct.size, 2)
    val privateData = linked.statements.collect {
      case data: Statement.Data if data.elementType == DataType.U32 => data.name
    }
    assertEquals(privateData.distinct.size, 2)
    val targets = linked.statements.collect {
      case Statement.Data(
            elementType = DataType.U64,
            initialiser = Vec(Operand.GenericAddress(Operand.Symbol(name)))
          ) => name
    }
    assertEquals(targets, privateData)
    val entries = linked.statements.collect { case entry: Statement.Entry => entry }
    entries.zip(Vec(42, 84)).foreach { case (entry, expected) =>
      val kernel = compileProgram(linked, entry.name)
      val memory = DeviceMemory(4)
      assertEquals(
        execute(
          kernel,
          1,
          1,
          Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
          memory
        ),
        Right(())
      )
      assertEquals(memory.readInt32(memory.start), Right(expected))
    }
  }

  test("extern declarations link to visible definitions even when declarations occur last") {
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .visible .global .u32 shared = 42;
      .visible .func implementation() { ret; }
      .version 8.0
      .target sm_80
      .address_size 64
      .extern .global .u32 shared;
      .extern .func implementation();
      .visible .entry main() { call implementation, (); ret; }
    """
    val parsed = Parser.parse(source).toOption.get
    val loaded = LoadedProgram.load(parsed).toOption.get
    assertEquals(loaded.global("shared").toOption.get.initial, Vec[Byte](42, 0, 0, 0))
    val linked    = Linker.link(parsed).toOption.get
    val functions = linked.statements.collect { case function: Statement.Function => function }
    assertEquals(functions.map(_.name).distinct, Vec("implementation": NonEmpty[String]))
    val entry  = linked.statements.collect { case entry: Statement.Entry => entry }.head
    val kernel = compileProgram(linked, entry.name)
    assertEquals(execute(kernel, 1, 1, Map.empty, DeviceMemory(4)), Right(()))
  }
  for (strongFirst <- Vec(false, true))
    test(s"strong data, functions and entries override weak definitions: strongFirst=$strongFirst") {
      def module(linkage: String, value: Int, extra: String): String = s""".version 8.0
        .target sm_80
        .address_size 64
        .$linkage .global .u32 selected = $value;
        .$linkage .func (.param .u32 result) implementation() {
          st.param.u32 [result], $value;
          ret;
        }
        .$linkage .entry overridden(.param .u64 output$extra) { ret; }
      """
      val strong = module("visible", 42, "")
      val weak   = module("weak", 7, ", .param .u32 extra")
      val client = """.version 8.0
        .target sm_80
        .address_size 64
        .visible .entry client(.param .u64 output) {
          .reg .u64 %output;
          .reg .u32 %value;
          .param .u32 result;
          ld.param.u64 %output, [output];
          call (result), implementation, ();
          ld.param.u32 %value, [result];
          st.global.u32 [%output], %value;
          ret;
        }
      """
      val source = (if (strongFirst) strong + weak else weak + strong) + client
      val parsed = Parser.parse(source).toOption.get
      val loaded = LoadedProgram.load(parsed).toOption.get
      assertEquals(loaded.global("selected").toOption.get.initial, Vec[Byte](42, 0, 0, 0))
      assertEquals(loaded.kernel("overridden").toOption.get.parameters.size, 1)
      val linked = Linker.link(parsed).toOption.get
      val entry  = linked.statements.collectFirst { case entry: Statement.Entry if entry.name == "client" => entry }.get
      val kernel = compileProgram(linked, entry.name)
      val memory = DeviceMemory(4)
      assertEquals(
        execute(
          kernel,
          1,
          1,
          Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
          memory
        ),
        Right(())
      )
      assertEquals(memory.readInt32(memory.start), Right(42))
    }

}
