package net.kurobako.gemstone.isa.spirv.v1_6

import java.lang.ref.WeakReference
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

class SharedDecoderSuite extends munit.FunSuite {
  private def operand(kind: OperandKind, value: Int): Operand = Operand(
    kind,
    Vec.nonEmpty(Word(value))
  )

  private def module(value: Int, endianness: Endianness = Endianness.Little): Module = Module(
    Header(ISA.version, Word(0), Word(8), Word(0)),
    Vec(
      Instruction.TypeInt(
        operand(OperandKind.IdResult, 1),
        operand(OperandKind.LiteralInteger, 32),
        operand(OperandKind.LiteralInteger, 0)
      ),
      Instruction.SpecConstant(
        operand(OperandKind.IdResultType, 1),
        operand(OperandKind.IdResult, 2),
        operand(OperandKind.LiteralContextDependentNumber, value)
      )
    ),
    endianness
  )

  private def bytes(value: Int): Vec[Byte] = ISA.encode(module(value)).toOption.get

  test("specialized modules share unchanged instruction graphs and keep distinct constant values") {
    val decoder = SharedDecoder()
    val first   = decoder.decode(bytes(5)).toOption.get
    val second  = decoder.decode(bytes(10)).toOption.get
    val third   = decoder.decode(bytes(5)).toOption.get
    assertEquals(first, module(5))
    assertEquals(second, module(10))
    assert(first.instructions(0) eq second.instructions(0))
    assert(first.instructions(1) ne second.instructions(1))
    assert(first.instructions(1) eq third.instructions(1))
  }

  test("sharing preserves module headers and byte order") {
    val decoder = SharedDecoder()
    val first   = decoder.decode(bytes(5)).toOption.get
    val changed = module(5, Endianness.Big).copy(header = module(5).header.copy(bound = Word(64)))
    val second  = decoder.decode(ISA.encode(changed).toOption.get).toOption.get
    assertEquals(second, changed)
    assert(first.instructions(0) eq second.instructions(0))
  }

  test("malformed modules retain ordinary decoder errors and do not poison subsequent loads") {
    val decoder = SharedDecoder()
    val first   = decoder.decode(bytes(5)).toOption.get
    val unknown = Binary.encode(RawModule(
      first.header,
      Vec(RawInstruction(SPIRVOpcodeNumber(65535), Vec.empty)),
      first.endianness
    )).toOption.get
    Seq(Vec.empty[Byte], bytes(5).dropRight(1), unknown).foreach { invalid =>
      assert(ISA.decode(invalid).isLeft)
      assertEquals(decoder.decode(invalid), ISA.decode(invalid))
    }
    val next = decoder.decode(bytes(5)).toOption.get
    assert(first.instructions(0) eq next.instructions(0))
  }

  test("concurrent loads share immutable instructions without mixing specialization values") {
    val decoder  = SharedDecoder()
    val anchor   = decoder.decode(bytes(0)).toOption.get
    val executor = Executors.newFixedThreadPool(4)
    try {
      val futures = (1 to 32).map { value =>
        executor.submit((() => decoder.decode(bytes(value)).toOption.get): Callable[Module])
      }
      futures.zipWithIndex.foreach { (future, index) =>
        val decoded = future.get(10, TimeUnit.SECONDS)
        assertEquals(decoded, module(index + 1))
        assert(anchor.instructions(0) eq decoded.instructions(0))
      }
    } finally {
      val _ = executor.shutdownNow()
    }
  }

  test("released instructions can be reclaimed while the decoder remains live") {
    val decoder = SharedDecoder()
    val result  = AtomicReference[WeakReference[Instruction]]()
    // A terminated thread leaves no conservative native stack roots for the module.
    val worker = Thread { () =>
      val decoded = decoder.decode(bytes(5)).toOption.get
      result.set(WeakReference(decoded.instructions.head))
    }
    worker.start()
    worker.join()
    val collected = (1 to 30).exists { _ =>
      scala.sys.runtime.gc()
      Thread.sleep(20)
      Option(result.get().get()).isEmpty
    }
    // Keep the decoder live through collection, without keeping a module live.
    assert(decoder.decode(bytes(10)).isRight)
    assert(collected, "decoder retained a released instruction graph")
  }

  test("hash collisions preserve distinct constants and live entries during reclamation") {
    def encoded(low: Int, high: Int): Vec[Byte] = {
      val constant = Instruction.SpecConstant(
        operand(OperandKind.IdResultType, 1),
        operand(OperandKind.IdResult, 2),
        Operand(
          OperandKind.LiteralContextDependentNumber,
          Vec.nonEmpty(Word(low), Word(high))
        )
      )
      ISA.encode(module(0).copy(instructions =
        Vec(
          Instruction.TypeInt(
            operand(OperandKind.IdResult, 1),
            operand(OperandKind.LiteralInteger, 64),
            operand(OperandKind.LiteralInteger, 0)
          ),
          constant
        )
      )).toOption.get
    }
    val firstBytes  = encoded(1418299984, -1284610799)
    val secondBytes = encoded(-1012570844, 172645704)
    val decoder     = SharedDecoder()
    val first       = decoder.decode(firstBytes).toOption.get.instructions(1)
    val result      = AtomicReference[(WeakReference[Instruction], Int, Boolean)]()
    val worker      = Thread { () =>
      val second = decoder.decode(secondBytes).toOption.get.instructions(1)
      result.set((WeakReference(second), second.hashCode(), first != second && (first ne second)))
    }
    worker.start()
    worker.join()
    val (reference, hash, distinct) = result.get()
    assertEquals(first.hashCode(), hash)
    assert(distinct, "hash collision merged distinct instructions")
    val collected = (1 to 30).exists { _ =>
      scala.sys.runtime.gc()
      Thread.sleep(20)
      Option(reference.get()).isEmpty
    }
    assert(collected, "a live colliding entry retained its released sibling")
    assert(first eq decoder.decode(firstBytes).toOption.get.instructions(1))
    assert(first != decoder.decode(secondBytes).toOption.get.instructions(1))
  }
}
