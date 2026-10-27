package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** The machine operations SPIR-V semantics request: those of [[MachineAlgebra]] over the ids of values, the blocks of a
  * function and the invocation as the only lane, with the control SPIR-V adds to them. An operand names a value by its
  * id, and a block is entered from another one, whose label [[predecessor]] reads.
  */
private trait SPIRVMachine[V, D, F[_]] extends Any with MachineAlgebra[V, Int, Int, Unit, Block, D, Unit, F] {

  /** `next` over the scalar `operand` holds; the instance faults if it holds no scalar. */
  inline def scalar(operand: Int)(inline next: V => F[Unit]): F[Unit]

  /** The label of the block the invocation entered the current one from. */
  inline def predecessor: V

  /** The invocation enters the block `label`, then continues at the next instruction. */
  inline def enter(label: Int): F[Unit]

  /** Each phi of `group` takes the value of its edge from the predecessor and the invocation continues after them; it
    * faults if a phi has no such edge.
    */
  inline def phi(group: Phis): F[Unit]

  /** The invocation continues at the block of the case whose literal `selector` matches, or at the default block. */
  inline def switch(selector: V, cases: Cases): F[Unit]

  /** The invocation runs the function of `callee` with its arguments, then continues after the call. */
  inline def call(callee: Callee): F[Unit]

  /** The invocation runs the function the pointer of `call` holds. */
  inline def callIndirect(call: Indirect): F[Unit]

  /** The invocation runs the function of `sweep` once for each of its calls in turn, call `i` with the arguments
    * `argument(i, previous, parameter)` gives for the parameters not fixed, where `previous` is the value call `i - 1`
    * returned or zero, and then `next` over the value each call returned. Each call returns to the same instruction.
    */
  inline def sweep(sweep: Sweep)(inline argument: (Int, V, Int) => V)(inline next: (Int => V) => F[Unit]): F[Unit]

  /** The invocation returns to its caller, or ends if it has none, with no value. */
  inline def returnControl: F[Unit]

  /** The invocation returns the value `value` holds to its caller. */
  inline def returnValue(value: Int): F[Unit]

  /** The fragment invocation is discarded. */
  inline def kill: F[Unit]

  /** The fragment invocation terminates. */
  inline def terminate: F[Unit]

  /** The invocation reached code that must not run. */
  inline def unreachable: F[Unit]

  /** The invocation aborts with the message `message` holds. */
  inline def abort(message: Int): F[Unit]

  /** The invocation waits at the workgroup or subgroup barrier of the `scope` the operand holds, with the memory
    * `scope` and `semantics` the operands hold; it faults unless those operands name valid controls.
    */
  inline def controlBarrier(scope: Int, memory: Int, semantics: Int): F[Unit]

  /** The invocation arrives at the barrier of the `scope` the operand holds without waiting for it, then `next`; it
    * faults unless those operands name valid controls.
    */
  inline def arriveBarrier(scope: Int, memory: Int, semantics: Int)(inline next: => F[Unit]): F[Unit]

  /** Memory is fenced at the `scope` and `semantics` the operands hold, then `next`. */
  inline def fence(scope: Int, semantics: Int)(inline next: => F[Unit]): F[Unit]

  /** The invocation waits at the named barrier `barrier` names, which spans the subgroups of the workgroup, with the
    * memory `scope` and `semantics` the operands hold.
    */
  inline def namedBarrier(barrier: Int, memory: Int, semantics: Int): F[Unit]

  /** The named barrier `target`, typed `typeId`, expects the `count` the operand holds, then `next`. */
  inline def initializeBarrier(target: Int, typeId: Int, count: Int)(inline next: => F[Unit]): F[Unit]

  /** The invocation joins `collective`: it offers its operands to the invocations that reach the same instruction, and
    * once those of the scope have arrived the result computed over all of them is written to the target the collective
    * names, and the invocation continues after it. It faults unless its operands name a valid collective.
    */
  inline def collective(collective: Collective): F[Unit]

  /** The `size` bytes at the address of the pointer `pointer` holds are loaded into `target`, typed `typeId`, then
    * `next`.
    */
  inline def loadScalar(pointer: Int, size: Int in 1 ~ 8, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The pointer `pointer` addresses is loaded into `target`, typed `typeId`, then `next`. */
  inline def loadAddress(
      pointer: Int,
      target: Int,
      typeId: Int,
      instruction: Instruction
  )(inline next: => F[Unit]): F[Unit]

  /** The `size` bytes at the address of `pointer` are loaded into `target` as a value of type `typeId`, then `next`. */
  inline def loadValue(pointer: Int, target: Int, typeId: Int, size: Int >= 0)(inline next: => F[Unit]): F[Unit]

  /** The handle `pointer` addresses is loaded into `target`, typed `typeId` and naming `pointee` unless it is -1, then
    * `next`.
    */
  inline def loadHandle(pointer: Int, target: Int, typeId: Int, pointee: Int)(inline next: => F[Unit]): F[Unit]

  /** The sampled image `pointer` addresses is loaded into `target`, typed `typeId`, then `next`. */
  inline def loadSampled(pointer: Int, target: Int, typeId: Int, imageType: Int)(inline next: => F[Unit]): F[Unit]

  /** The low `size` bytes of `source` are stored at the address of `pointer`, then `next`. */
  inline def storeScalar(pointer: Int, source: Int, size: Int in 1 ~ 8, instruction: Instruction)(
      inline next: => F[Unit]
  ): F[Unit]

  /** The value `source` holds is stored at the address of `pointer`, then `next`. */
  inline def storeValue(pointer: Int, source: Int, instruction: Instruction)(inline next: => F[Unit]): F[Unit]

  /** The pointee of `origin` is copied to the address of `target`, then `next`. */
  inline def copyMemory(target: Int, origin: Int)(inline next: => F[Unit]): F[Unit]

  /** The `size` bytes at the address of `origin` are copied to the address of `target`, then `next`. */
  inline def copyMemorySized(target: Int, origin: Int, size: Int)(inline next: => F[Unit]): F[Unit]

  /** The `count` elements of the `element` bytes the operand holds, or of the pointee of `destination` if `element` is
    * -1, are copied from `source` to `destination` with `stride` elements between each, and `target`, typed `typeId`,
    * is the event of the copy, then `next`. The instance faults unless the operands name the workgroup scope and
    * pointers in workgroup and cross-workgroup storage.
    */
  inline def groupAsyncCopy(
      target: Int,
      typeId: Int,
      scope: Int,
      destination: Int,
      source: Int,
      count: Int,
      stride: Int,
      element: Int
  )(inline next: => F[Unit]): F[Unit]

  /** The `count` events at the address of `events` are complete, since a copy finishes when it is issued, then `next`;
    * the instance faults unless the operands name the workgroup scope and a count of at least zero.
    */
  inline def waitEvents(scope: Int, count: Int, events: Int)(inline next: => F[Unit]): F[Unit]

  /** The atomic scalar of `size` bytes at the address of `pointer` is loaded into `target`, typed `typeId`, once
    * `scope` and `semantics` pass their checks, then `next`.
    */
  inline def atomicLoad(pointer: Int, size: Int in 1 ~ 8, target: Int, typeId: Int, scope: Int, semantics: Int)(
      inline next: => F[Unit]
  ): F[Unit]

  /** The low `size` bytes of `source` are stored atomically at the address of `pointer`, once `scope` and `semantics`
    * pass their checks, then `next`.
    */
  inline def atomicStore(pointer: Int, source: Int, size: Int in 1 ~ 8, scope: Int, semantics: Int)(
      inline next: => F[Unit]
  ): F[Unit]

  /** The 32-bit flag at the address of `pointer` is set atomically, and `target` receives whether it was set before. */
  inline def flagTestAndSet(pointer: Int, target: Int, typeId: Int, scope: Int, semantics: Int)(
      inline next: => F[Unit]
  ): F[Unit]

  /** The 32-bit flag at the address of `pointer` is cleared atomically, once `semantics` passes its checks, then
    * `next`.
    */
  inline def flagClear(pointer: Int, scope: Int, semantics: Int)(inline next: => F[Unit]): F[Unit]

  /** The `count` scalar leaves of `source` from leaf `offset` are `target`, typed `typeId`, then `next`. */
  inline def extract(source: Int, offset: Int, count: Int, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The leaves of the values `parts` hold, in order, are `target`, typed `typeId`, then `next`. */
  inline def construct(parts: Array[Int], leaves: Int, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The leaves of `composite` with the `count` from leaf `offset` replaced by those of `source` are `target`, typed
    * `typeId`, then `next`.
    */
  inline def insert(source: Int, composite: Int, offset: Int, count: Int, target: Int, typeId: Int)(
      inline next: => F[Unit]
  ): F[Unit]

  /** Leaf `i` of `target`, typed `typeId`, is leaf `selection(i)` of the leaves of `left` then `right`, or 0 where that
    * is `0xffffffff`, then `next`.
    */
  inline def shuffle(left: Int, right: Int, selection: Array[Long], target: Int, typeId: Int)(
      inline next: => F[Unit]
  ): F[Unit]

  /** The `rows` by `columns` matrix `source` holds is transposed into `target`, typed `typeId`, then `next`; the
    * instance faults if the value is undefined or does not hold `rows * columns` leaves.
    */
  inline def transpose(source: Int, rows: Int, columns: Int, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The invocation prints the C string `format` holds, with the values its `arguments` hold, to the printf buffer,
    * sets `target`, typed `typeId`, to 0, then `next`; the instance faults unless the arguments match the conversions.
    */
  inline def printf(format: Int, arguments: Array[Int], target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The `count` elements of `size` bytes at element `offset` past the address of `pointer` are `target`, typed
    * `typeId`, then `next`; the instance faults if the access leaves the address space.
    */
  inline def loadVector(offset: Int, pointer: Int, count: Int, size: Int in 1 ~ 8, target: Int, typeId: Int)(
      inline next: => F[Unit]
  ): F[Unit]

  /** The lanes of `data` are stored as `count` elements of `size` bytes at element `offset` past the address of
    * `pointer`, then `next`; the instance faults if the lanes do not match `count` or the access leaves the address
    * space.
    */
  inline def storeVector(data: Int, offset: Int, pointer: Int, count: Int, size: Int in 1 ~ 8)(
      inline next: => F[Unit]
  ): F[Unit]

  /** The leaf of `vector` that `index` holds is `target`, typed `typeId`, then `next`; the instance faults if it is
    * outside the vector.
    */
  inline def extractDynamic(vector: Int, index: Int, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The leaves of `vector` with the one `index` holds replaced by `component` are `target`, typed `typeId`, then
    * `next`; the instance faults if it is outside the vector.
    */
  inline def insertDynamic(vector: Int, component: Int, index: Int, target: Int, typeId: Int)(
      inline next: => F[Unit]
  ): F[Unit]

  /** The value `source` holds is `target`, typed `typeId`, then `next`. */
  inline def copyValue(source: Int, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The operand of the first condition that holds is `target`, typed `typeId`, then `next`; the instance faults if no
    * condition holds. `pairs` alternates conditions and operands.
    */
  inline def conditionalCopy(pairs: Array[Int], target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** `pointer` names an address, then `next`; the instance faults if it does not. */
  inline def prefetch(pointer: Int)(inline next: => F[Unit]): F[Unit]

  /** `target`, typed `typeId`, is the zero value of its type, then `next`; the instance faults if the type has none,
    * naming `opcode`.
    */
  inline def zero(target: Int, typeId: Int, opcode: String)(inline next: => F[Unit]): F[Unit]

  /** The clock of the scope the operand `scope` holds is `target`, typed `typeId`, then `next`; `halves` splits its
    * 64-bit count into two 32-bit leaves, low first. The instance faults unless the scope is one a clock reads.
    */
  inline def readClock(scope: Int, target: Int, typeId: Int, halves: Boolean)(inline next: => F[Unit]): F[Unit]

  /** `target`, typed `typeId`, is the pointer `base` moved along `indexes` through the layout `chain` names, after
    * `element` strides of its base type if that is not -1, then `next`; the instance faults if an index is outside the
    * type it indexes.
    */
  inline def accessChain(base: Int, element: Int, indexes: Array[Int], chain: AccessChain, target: Int, typeId: Int)(
      inline next: => F[Unit]
  ): F[Unit]

  /** The low bits of the address of `pointer` that `mask` selects are `target`, typed `typeId`, then `next`. */
  inline def pointerToInteger(pointer: Int, mask: Long, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The low bits of each address in the vector of pointers `source` that `mask` selects are `target`, typed `typeId`,
    * then `next`.
    */
  inline def pointersToIntegers(source: Int, mask: Long, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The integer `source` holds is the address of the pointer `target`, typed `typeId` and to `pointee` in
    * `storageClass`, or the function pointer if `function`, then `next`.
    */
  inline def integerToPointer(
      source: Int,
      pointee: Int,
      storageClass: Int,
      function: Boolean,
      target: Int,
      typeId: Int
  )(
      inline next: => F[Unit]
  ): F[Unit]

  /** The vector of `lanes` integers `source` holds is the vector of pointers `target`, typed `typeId`, then `next`. */
  inline def integersToPointers(source: Int, lanes: Int, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The pointer `source` holds is `target`, typed `typeId`, as a Generic pointer, then `next`. */
  inline def castToGeneric(source: Int, cast: PointerCast, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The Generic pointer `source` holds is `target`, typed `typeId`, in the storage class `cast` names, or null where
    * it lies in another, then `next`.
    */
  inline def castFromGeneric(source: Int, cast: PointerCast, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The elements of the runtime array that member `member` of the structure `structure` points at, typed `declared` if
    * given, are `target`, typed `typeId`, then `next`.
    */
  inline def arrayLength(structure: Int, member: Int, declared: Option[Int], target: Int, typeId: Int)(
      inline next: => F[Unit]
  ): F[Unit]

  /** `target`, typed `typeId`, is a variable of the pointee of `typeId` allocated in private storage, holding the value
    * `initialiser` names if given, then `next`.
    */
  inline def variable(target: Int, typeId: Int, storageClass: Int, initialiser: Option[Int])(
      inline next: => F[Unit]
  ): F[Unit]

  /** `target`, typed `typeId`, is `length` elements of `element` allocated in private storage, then `next`; the
    * instance faults if the allocation is outside private storage.
    */
  inline def variableLengthArray(target: Int, typeId: Int, element: Int, length: V)(inline next: => F[Unit]): F[Unit]

  /** `target`, typed `typeId`, is the current top of private storage, which `restoreMemory` returns to, then `next`. */
  inline def saveMemory(target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** Private storage is released back to the position the pointer `pointer` holds, then `next`; the instance faults
    * unless that pointer is a saved position below the top.
    */
  inline def restoreMemory(pointer: Int)(inline next: => F[Unit]): F[Unit]

  /** `target`, typed `typeId`, is the memory semantics the Generic pointer `source` holds, then `next`. */
  inline def memorySemantics(source: Int, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The invocation performs the image operation `instruction` names over the images and samplers its operands hold,
    * writes its result, and continues after it, or waits for the derivatives of its quad; it faults unless the operands
    * name a valid view and access.
    */
  inline def image(instruction: Instruction): F[Unit]

  /** `target`, typed `typeId`, is the scalar `value`, then `next`. */
  inline def scalarValue(target: Int, typeId: Int, value: Long)(inline next: => F[Unit]): F[Unit]

  /** The invocation performs the cooperative vector operation `instruction` names on its own vector and the matrices
    * and biases in memory, then continues after it; it faults unless its operands name valid vectors, kinds and
    * layouts.
    */
  inline def cooperativeVector(instruction: Instruction): F[Unit]

  /** The invocation performs the pipe operation `instruction` names on the pipes and packets its operands hold, writes
    * its result, then continues after it; it faults unless the operands name a valid pipe and packet.
    */
  inline def pipe(instruction: Instruction): F[Unit]

  /** The invocation performs the device-side enqueue, event or kernel query `instruction` names on the device queue,
    * writes its result, then continues after it; it faults unless the operands name a valid queue and range.
    */
  inline def deviceEnqueue(instruction: Instruction): F[Unit]

  /** The invocation performs the tensor layout, view or tensor transfer `instruction` names, writes its result, then
    * continues after it; it faults unless the operands name a valid layout, view and matrix.
    */
  inline def tensorAddressing(instruction: Instruction): F[Unit]

  /** The handle `source` holds is the address of a descriptor of type `typeId`, which `target` receives as a pointer to
    * that type, then `next`.
    */
  inline def convertHandle(source: Int, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** `target`, typed `typeId` and pointing to `pointeeType`, is the address `base` holds moved by `element` strides of
    * `stride` bytes and `offset` bytes, then `next`.
    */
  inline def rawAccessChain(
      base: Int,
      stride: Int,
      element: Int,
      offset: Int,
      pointeeType: Int,
      target: Int,
      typeId: Int
  )(inline next: => F[Unit]): F[Unit]

  /** `target`, typed `typeId`, is the `size` bytes at the address of `pointer` if the scalar `predicate` holds, and
    * `default` otherwise, then `next`; the address is checked only where the predicate holds.
    */
  inline def predicatedLoad(predicate: Int, pointer: Int, default: Int, target: Int, typeId: Int, size: Int >= 0)(
      inline next: => F[Unit]
  ): F[Unit]

  /** The value `source` holds is stored at the address of `pointer` if the scalar `predicate` holds, then `next`. */
  inline def predicatedStore(predicate: Int, pointer: Int, source: Int)(inline next: => F[Unit]): F[Unit]

  /** Lane `i` of `target`, typed `typeId`, is the `width`-byte element at the address lane `i` of `pointers` holds,
    * where lane `i` of `mask` holds, and lane `i` of `fill` otherwise, then `next`.
    */
  inline def maskedGather(pointers: Int, mask: Int, fill: Int, target: Int, typeId: Int, width: Int in 1 ~ 8)(
      inline next: => F[Unit]
  ): F[Unit]

  /** Lane `i` of `data` is stored as a `width`-byte element at the address lane `i` of `pointers` holds, where lane `i`
    * of `mask` holds, then `next`.
    */
  inline def maskedScatter(data: Int, pointers: Int, mask: Int, width: Int in 1 ~ 8)(inline next: => F[Unit]): F[Unit]

  /** `target`, typed `typeId`, is the sampled image of the image type the sampled image type `typeId` names, bound to
    * the descriptor the handle `source` holds, then `next`.
    */
  inline def convertSampledHandle(source: Int, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** Leaf `i` of the `count` of `target`, typed `typeId`, is `body(i)`, then `next`; the instance faults if a value of
    * `sources` is undefined.
    */
  inline def eachLeaf(sources: Array[Int], count: Int, target: Int, typeId: Int)(inline body: Int => V)(
      inline next: => F[Unit]
  ): F[Unit]

  /** Leaf `index` of the value `source` holds, which must be one of the `sources` of [[eachLeaf]]. */
  inline def leaf(source: Int, index: Int): V

  /** The leaf `index` selects of the leaves, in order, of the first `values` of `sources`; it lies below their total.
    */
  inline def leafAt(sources: Array[Int], values: Int, index: V): V

  /** Leaf `index` of the line the invocation's own lane holds of the `rows` by `columns` matrix `source`: the lane's
    * row of a matrix of use 0 or 2, its column of a matrix of use 1, or zero for a lane past the lines.
    */
  inline def ownLeaf(source: Int, index: Int, rows: Int, columns: Int, use: Int): V

  /** `target`, typed `typeId`, is the leaves of `source` from leaf `index`, as many as `typeId` holds, then `next`; the
    * instance faults unless those leaves lie within `source`.
    */
  inline def subArray(source: Int, index: Int, target: Int, typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** The bytes a stride of `pointer` counts: the size of its pointee under Vulkan, otherwise `width`. */
  inline def strideBytes(pointer: Int, width: Int in 1 ~ 8): V

  /** The index of the invocation in its subgroup, and the size of the subgroup it launched with. */
  inline def subgroupLocal: V

  inline def subgroupSize: V

  /** Leaf `i` of the `count` of `target`, typed `typeId`, is the `width`-byte element `offset(i)` bytes past the
    * address of `pointer`, then `next`; the instance faults if an access does.
    */
  inline def loadLeaves(pointer: Int, count: Int, width: Int in 1 ~ 8, target: Int, typeId: Int)(
      inline offset: Int => V
  )(inline next: => F[Unit]): F[Unit]

  /** The low `width` bytes of leaf `i` of the `count` of `source` are stored `offset(i)` bytes past the address of
    * `pointer`, then `next`; the instance faults if the value is undefined or an access does.
    */
  inline def storeLeaves(pointer: Int, source: Int, count: Int, width: Int in 1 ~ 8)(inline offset: Int => V)(
      inline next: => F[Unit]
  ): F[Unit]

  /** `next` over the `bytes` bytes `offset` bytes past the address of `pointer`, zero-extended; the instance faults if
    * the access does.
    */
  inline def loadElement(pointer: Int, offset: V, bytes: V)(inline next: V => F[Unit]): F[Unit]

  /** `next` over the `bytes` bytes `offset` bytes past the absolute `address`, zero-extended and typed as `pointee`;
    * the instance faults if the access does.
    */
  inline def loadAbsolute(address: V, offset: V, bytes: V, pointee: Int)(inline next: V => F[Unit]): F[Unit]

  /** The `bytes` bytes `offset` bytes past the absolute `address` hold `value`, typed as `pointee`, then `next`; the
    * instance faults if the access does.
    */
  inline def storeAbsolute(address: V, offset: V, bytes: V, value: V, pointee: Int)(inline next: => F[Unit]): F[Unit]

  /** The `bytes` bytes `offset` bytes past the address of `pointer` hold `value`, then `next`; the instance faults if
    * the access does.
    */
  inline def storeElement(pointer: Int, offset: V, bytes: V, value: V)(inline next: => F[Unit]): F[Unit]

  /** `next` over the `bytes` bytes `offset` bytes into the level the image is bound to, zero-extended; the instance
    * faults if the access does.
    */
  inline def loadLevel(offset: V, bytes: V)(inline next: V => F[Unit]): F[Unit]

  /** The `bytes` bytes `offset` bytes into the level the image is bound to hold `value`, then `next`. */
  inline def storeLevel(offset: V, bytes: V, value: V)(inline next: => F[Unit]): F[Unit]

  /** `body` on each index below `count` in turn, then `next`; the instance stops at the first fault. */
  inline def repeat(count: V)(inline body: V => F[Unit])(inline next: => F[Unit]): F[Unit]

  /** `count` partial results, all zero, then `next`. */
  inline def partials(count: Int)(inline next: => F[Unit]): F[Unit]

  /** Partial result `lane` is `value`, then `next`. */
  inline def setPartial(lane: V, value: V)(inline next: => F[Unit]): F[Unit]

  /** Partial result `lane`. */
  inline def partial(lane: V): V

  /** 1 where the invocation runs under Vulkan rules. */
  inline def vulkan: V

  /** 1 where the elements of the operand are integers. */
  inline def integral(operand: Int): V

  /** The leaves of the operand, or -1 where it is not a defined numeric value. */
  inline def count(operand: Int): V

  /** `next` where the operand is a defined numeric value; the instance faults otherwise. */
  inline def numeric(operand: Int)(inline next: => F[Unit]): F[Unit]

  /** `next` over the dimensions of the sampled `image` names, then whether it is arrayed, then whether it is a cube;
    * the instance faults unless the operand is a view that sampling, or `query` of its level of detail, accepts.
    */
  inline def view(image: Int, query: Boolean)(inline next: (V, V, V) => F[Unit]): F[Unit]

  /** `next` where the sampled image has a sampler; the instance faults otherwise. */
  inline def sampler(image: Int, missing: NonEmpty[String])(inline next: => F[Unit]): F[Unit]

  /** The words of the sampler of the sampled image are partial results from `into`, then `next`. */
  inline def samplerWords(image: Int, into: Int)(inline next: => F[Unit]): F[Unit]

  /** `next` over word `word` of the sampler record of the sampled image. */
  inline def samplerWord(image: Int, word: Int)(inline next: V => F[Unit]): F[Unit]

  /** `next` over the format word of the descriptor of the image. */
  inline def formatWord(image: Int)(inline next: V => F[Unit]): F[Unit]

  /** `next` over the levels of the image; the instance faults unless it has a valid level table. */
  inline def levelCount(image: Int)(inline next: V => F[Unit]): F[Unit]

  /** Level `level` of the image is bound as the one texels are read from, and its width, height, depth, layers,
    * samples, row stride and format are partial results from `into`, then `next`; the instance faults unless the level
    * is valid.
    */
  inline def level(image: Int, level: V, into: Int)(inline next: => F[Unit]): F[Unit]

  /** The texel at column `x` of row `row` of `layer` in the bound level is decoded to four partial results from `into`,
    * as doubles, then `next`.
    */
  inline def texel(layer: V, x: V, row: V, into: Int)(inline next: => F[Unit]): F[Unit]

  /** The swizzle of the image is applied to the four partial results from `from`, then `next`. */
  inline def swizzle(image: Int, from: V)(inline next: => F[Unit]): F[Unit]

  /** The invocation waits for the gradients of the coordinates in the `count` partial results from `from` across its
    * quad, or `next` over whether it has them, which are, as doubles, partial results from `into` + 1 across and from
    * `into` + 4 down.
    */
  inline def gradients(from: Int, count: V, resultType: Int, dest: Int, into: Int)(inline next: V => F[Unit]): F[Unit]

  /** `next` where the type is a vector of two 32-bit floats; the instance faults otherwise. */
  inline def lodResult(typeId: Int)(inline next: => F[Unit]): F[Unit]

  /** `body` where `condition` holds, then `next`; the instance stops at the first fault. */
  inline def when(condition: V)(inline body: => F[Unit])(inline next: => F[Unit]): F[Unit]
}
