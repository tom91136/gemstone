package net.kurobako.gemstone.isa.spirv

import Operand.*
import VMInvocation.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*

/** SPIR-V image sampling, gathers and level-of-detail queries, as requests to a [[SPIRVMachine]]. A sample reads the
  * descriptors of its image and sampler, picks the levels of detail, and for each level takes the texels of its filter
  * through the address modes, then weights them. Floats and doubles are words in partial results, laid out by
  * [[Registers]], and the arithmetic on them is that of [[Sampling]].
  */
private object SamplingSemantics {

  /** What a gather reads. */
  enum Gather derives CanEqual {
    case Off, Depth
    case Component(operand: Int)
  }

  /** The operands of a sample or gather. */
  case class Sample(
      resultType: Int,
      dest: Int,
      image: Int,
      coordinate: Int,
      dref: Option[Int],
      explicit: Boolean,
      modifiers: Option[Operand],
      projective: Boolean,
      gather: Gather
  )

  /** The operands of a level of detail query. */
  case class Query(resultType: Int, dest: Int, image: Int, coordinate: Int)

  /** The partial results a sample works in. */
  object Registers {
    inline val Sampler    = 0
    inline val Coords     = 16
    inline val Direction  = 24
    inline val Face       = 27
    inline val FaceS      = 28
    inline val FaceT      = 29
    inline val Gradient   = 30
    inline val Offset     = 37
    inline val Offsets    = 40
    inline val Checked    = 48
    inline val Reference  = 49
    inline val Lambda     = 50
    inline val Filter     = 51
    inline val Selected   = 52
    inline val Mode       = 53
    inline val Level      = 56
    inline val Texel      = 64
    inline val Taps       = 68
    inline val Results    = 100
    inline val Sx         = 108
    inline val Sy         = 109
    inline val Sz         = 110
    inline val Bx         = 111
    inline val By         = 112
    inline val Bz         = 113
    inline val Components = 114
    inline val Layer      = 115
    inline val X          = 116
    inline val Y          = 117
    inline val Z          = 118
    inline val Ix         = 119
    inline val Iy         = 120
    inline val Iz         = 121
    inline val Outside    = 122
    inline val Subs       = 123
    inline val Count      = 124
  }
  import Registers.*

  private val Hints = ImageOperands.SignExtend | ImageOperands.ZeroExtend | ImageOperands.Nontemporal

  private val HalfBits: Long = java.lang.Double.doubleToRawLongBits(0.5)

  private val InfinityBits: Long = java.lang.Double.doubleToRawLongBits(Double.NegativeInfinity)

  private def form(
      t: Operand,
      d: Operand,
      i: Operand,
      c: Operand,
      mods: Option[Operand],
      dref: Option[Operand] = None,
      explicit: Boolean = false,
      projective: Boolean = false,
      gather: Gather = Gather.Off
  ): Option[Sample] = Some(
    Sample(
      resultType = id(t),
      dest = id(d),
      image = id(i),
      coordinate = id(c),
      dref = dref.map(id(_)),
      explicit = explicit,
      modifiers = mods,
      projective = projective,
      gather = gather
    )
  )

  def sampleOf(instruction: Instruction): Option[Sample] = instruction match {
    case Instruction.ImageSampleImplicitLod(t, d, i, c, m)        => form(t, d, i, c, m)
    case Instruction.ImageSampleExplicitLod(t, d, i, c, m)        => form(t, d, i, c, Some(m), explicit = true)
    case Instruction.ImageSampleDrefImplicitLod(t, d, i, c, r, m) => form(t, d, i, c, m, dref = Some(r))
    case Instruction.ImageSampleDrefExplicitLod(t, d, i, c, r, m) =>
      form(t, d, i, c, Some(m), dref = Some(r), explicit = true)
    case Instruction.ImageSampleProjImplicitLod(t, d, i, c, m) => form(t, d, i, c, m, projective = true)
    case Instruction.ImageSampleProjExplicitLod(t, d, i, c, m) =>
      form(t, d, i, c, Some(m), explicit = true, projective = true)
    case Instruction.ImageSampleProjDrefImplicitLod(t, d, i, c, r, m) =>
      form(t, d, i, c, m, dref = Some(r), projective = true)
    case Instruction.ImageSampleProjDrefExplicitLod(t, d, i, c, r, m) =>
      form(t, d, i, c, Some(m), dref = Some(r), explicit = true, projective = true)
    case Instruction.ImageGather(t, d, i, c, component, m) =>
      form(t, d, i, c, m, gather = Gather.Component(id(component)))
    case Instruction.ImageDrefGather(t, d, i, c, r, m) => form(t, d, i, c, m, dref = Some(r), gather = Gather.Depth)
    case _                                             => None
  }

  def queryOf(instruction: Instruction): Option[Query] = instruction match {
    case Instruction.ImageQueryLod(t, d, i, c) => Some(Query(id(t), id(d), id(i), id(c)))
    case _                                     => None
  }

  private inline def literal[V](inline algebra: Algebra[V])(value: Long): V = algebra.literal(value)

  private inline def reg[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      index: Int
  ): V = machine.partial(algebra.literal(index.toLong))

  private inline def regAt[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      base: Int,
      offset: V
  ): V = machine.partial(algebra.add(algebra.literal(base.toLong), offset))

  private inline def store[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      index: Int,
      value: V
  )(inline next: => F[Unit]): F[Unit] = machine.setPartial(algebra.literal(index.toLong), value)(next)

  private inline def storeAt[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      base: Int,
      offset: V,
      value: V
  ): F[Unit] = machine.setPartial(algebra.add(algebra.literal(base.toLong), offset), value)(machine.done)

  private inline def flag[V](inline algebra: Algebra[V])(value: Boolean): V = algebra.literal(if (value) 1L else 0L)

  private inline def verified[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      ok: Boolean,
      label: String
  )(inline fault: => Fault)(inline next: => F[Unit]): F[Unit] =
    if (ok) next else machine.require(algebra.literal(0L), label)(fault)(next)

  private inline def widen[V](inline algebra: Algebra[V])(word: V): V =
    algebra.primitive(word)(Sampling.widen(_: UInt[64]))

  private inline def word[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      index: Int
  ): V = reg(algebra, machine)(Sampler + index)

  private inline def scaled[V](inline algebra: Algebra[V])(gradient: V, extent: V, unnormalised: V): V =
    algebra.primitive(gradient, extent, unnormalised)(Sampling.scaled(_: UInt[64], _: UInt[64], _: UInt[64]))

  private inline def onDirection[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      inline function: (UInt[64], UInt[64], UInt[64]) => UInt[64]
  ): V = algebra.primitive(
    reg(algebra, machine)(Direction),
    reg(algebra, machine)(Direction + 1),
    reg(algebra, machine)(Direction + 2)
  )(function)

  private inline def onGradient[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      base: Int
  )(inline function: (UInt[64], UInt[64], UInt[64], UInt[64], UInt[64], UInt[64]) => UInt[64]): V = algebra.primitive(
    reg(algebra, machine)(Direction),
    reg(algebra, machine)(Direction + 1),
    reg(algebra, machine)(Direction + 2),
    reg(algebra, machine)(base),
    reg(algebra, machine)(base + 1),
    reg(algebra, machine)(base + 2)
  )(function)

  /** The level of detail the gradients across and down give, in texels of the level bound at [[Level]] unless the
    * `unnormalised` coordinates are already; on a cube, the gradients are those of the coordinates on the face.
    */
  private inline def gradientLod[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      cube: V,
      unnormalised: V
  ): V = {
    def onFace(base: Int, axis: Int): V = algebra.select(
      cube,
      if (axis == 0) onGradient(algebra, machine)(base)(Sampling.gradientS)
      else if (axis == 1) onGradient(algebra, machine)(base)(Sampling.gradientT)
      else algebra.literal(0L),
      reg(algebra, machine)(base + axis)
    )
    def extent(axis: Int): V            = reg(algebra, machine)(Level + axis)
    def texels(base: Int, axis: Int): V = scaled(algebra)(onFace(base, axis), extent(axis), unnormalised)
    algebra.primitive(
      texels(Gradient + 1, 0),
      texels(Gradient + 1, 1),
      texels(Gradient + 1, 2),
      texels(Gradient + 4, 0),
      texels(Gradient + 4, 1),
      texels(Gradient + 4, 2)
    )(Sampling.detail(_: UInt[64], _: UInt[64], _: UInt[64], _: UInt[64], _: UInt[64], _: UInt[64]))
  }

  /** The widened coordinates of the direction, and where it lands on a cube. */
  private inline def directed[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      dims: V,
      cube: V
  )(inline next: => F[Unit]): F[Unit] =
    machine.repeat(dims)(axis =>
      storeAt(algebra, machine)(Direction, axis, widen(algebra)(regAt(algebra, machine)(Coords, axis)))
    )(
      machine.when(cube)(
        store(algebra, machine)(Face, onDirection(algebra, machine)(Sampling.face))(
          store(algebra, machine)(FaceS, onDirection(algebra, machine)(Sampling.faceS))(
            store(algebra, machine)(FaceT, onDirection(algebra, machine)(Sampling.faceT))(machine.done)
          )
        )
      )(next)
    )

  /** The `count` offsets of the operand are partial results from `into`, within the limits of the device. */
  private inline def offsets[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      operand: Int,
      count: V,
      into: Int,
      label: String
  )(inline fault: => Fault)(inline next: => F[Unit]): F[Unit] = {
    val sources = Array(operand)
    store(algebra, machine)(Checked, algebra.literal(1L))(
      machine.repeat(count) { lane =>
        val offset = algebra.signExtend(machine.leafAt(sources, 1, lane), 32)
        val within = algebra.and(
          algebra.lessOrEqual(algebra.literal(-8L), offset, signed = true),
          algebra.lessOrEqual(offset, algebra.literal(7L), signed = true)
        )
        store(algebra, machine)(Checked, algebra.and(reg(algebra, machine)(Checked), within))(
          storeAt(algebra, machine)(into, lane, offset)
        )
      }(machine.require(reg(algebra, machine)(Checked), label)(fault)(next))
    )
  }

  /** Level `pass` of the sample: the texels of its filter, taken through the address modes, then weighted. */
  private inline def atLevel[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      sample: Sample,
      dims: V,
      arrayed: V,
      cube: V,
      format: V,
      levels: V,
      filter: V,
      selected: V,
      mode: V,
      pass: V,
      channel: V
  )(inline next: => F[Unit]): F[Unit] = {
    def wordAt(index: Int): V = word(algebra, machine)(index)
    def lit(value: Long): V   = algebra.literal(value)
    val gathering             = sample.gather != Gather.Off
    val volume                = algebra.and(algebra.equal(dims, lit(3L)), algebra.equal(cube, lit(0L)))
    val nearest               = algebra.equal(filter, lit(ImageDescriptor.Filter.Nearest.toLong))
    val linear                = algebra.equal(filter, lit(ImageDescriptor.Filter.Linear.toLong))
    machine.level(
      sample.image,
      algebra.primitive(selected, levels, mode, pass)(
        Sampling.level(_: UInt[64], _: UInt[64], _: UInt[64], _: UInt[64])
      ),
      Level
    ) {
      val width        = reg(algebra, machine)(Level)
      val height       = reg(algebra, machine)(Level + 1)
      val depth        = reg(algebra, machine)(Level + 2)
      val layers       = reg(algebra, machine)(Level + 3)
      val unnormalised = wordAt(ImageDescriptor.SamplerWord.Unnormalized)
      val layerCount   = algebra.select(cube, algebra.divide(layers, lit(6L), signed = false), layers)
      val layerExpr    = algebra.select(
        arrayed,
        algebra.primitive(regAt(algebra, machine)(Coords, dims), layerCount)(Sampling.layer(_: UInt[64], _: UInt[64])),
        lit(0L)
      )
      def texelSpace(coordinate: V, extent: V, axis: Int): V =
        algebra.primitive(coordinate, extent, reg(algebra, machine)(Offset + axis), unnormalised)(
          Sampling.texelSpace(_: UInt[64], _: UInt[64], _: UInt[64], _: UInt[64])
        )
      val sxExpr =
        texelSpace(algebra.select(cube, reg(algebra, machine)(FaceS), reg(algebra, machine)(Direction)), width, 0)
      val syExpr = algebra.select(
        algebra.equal(dims, lit(1L)),
        lit(HalfBits),
        texelSpace(algebra.select(cube, reg(algebra, machine)(FaceT), reg(algebra, machine)(Direction + 1)), height, 1)
      )
      val szExpr = algebra.select(volume, texelSpace(reg(algebra, machine)(Direction + 2), depth, 2), lit(HalfBits))
      val componentsExpr         = algebra.primitive(format)(Sampling.components(_: UInt[64]))
      def base(coordinate: V): V = algebra.select(
        nearest,
        algebra.primitive(coordinate)(Sampling.nearest(_: UInt[64])),
        algebra.primitive(coordinate)(Sampling.lowest(_: UInt[64]))
      )
      def weight(coordinate: V, lowest: V): V =
        algebra.primitive(coordinate, lowest)(Sampling.fraction(_: UInt[64], _: UInt[64]))
      def indexed(coordinate: V, extent: V, mode: Int): V =
        algebra.primitive(coordinate, extent, wordAt(mode))(Sampling.address(_: UInt[64], _: UInt[64], _: UInt[64]))
      def sx: V         = reg(algebra, machine)(Sx)
      def sy: V         = reg(algebra, machine)(Sy)
      def sz: V         = reg(algebra, machine)(Sz)
      def bx: V         = reg(algebra, machine)(Bx)
      def by: V         = reg(algebra, machine)(By)
      def bz: V         = reg(algebra, machine)(Bz)
      def layer: V      = reg(algebra, machine)(Layer)
      def components: V = reg(algebra, machine)(Components)
      val taps          =
        if (gathering) lit(4L) else algebra.select(nearest, lit(1L), algebra.select(volume, lit(8L), lit(4L)))
      val face    = reg(algebra, machine)(Face)
      val colour  = wordAt(ImageDescriptor.SamplerWord.BorderColour)
      val squares = algebra.or(
        algebra.equal(cube, lit(0L)),
        algebra.and(
          algebra.equal(width, height),
          algebra.and(
            algebra.lessOrEqual(lit(6L), layers, signed = false),
            algebra.equal(algebra.remainder(layers, lit(6L), signed = false), lit(0L))
          )
        )
      )
      def tapBody(tap: V): F[Unit] = {
        val first = algebra.multiply(tap, lit(4L))
        val ox    =
          if (gathering) regAt(algebra, machine)(Offsets, algebra.multiply(tap, lit(2L)))
          else algebra.and(tap, lit(1L))
        val oy =
          if (gathering)
            regAt(algebra, machine)(Offsets, algebra.add(algebra.multiply(tap, lit(2L)), lit(1L)))
          else algebra.and(algebra.shiftRightLogical(tap, lit(1L)), lit(1L))
        val oz          = if (gathering) lit(0L) else algebra.shiftRightLogical(tap, lit(2L))
        def x: V        = reg(algebra, machine)(X)
        def y: V        = reg(algebra, machine)(Y)
        def ix: V       = reg(algebra, machine)(Ix)
        def iy: V       = reg(algebra, machine)(Iy)
        def iz: V       = reg(algebra, machine)(Iz)
        def outside: V  = reg(algebra, machine)(Outside)
        def subs: V     = reg(algebra, machine)(Subs)
        def subsExpr: V =
          if (gathering) lit(1L)
          else algebra.select(
            cube,
            algebra.primitive(face, x, y, width, linear)(
              Sampling.cubeTaps(_: UInt[64], _: UInt[64], _: UInt[64], _: UInt[64], _: UInt[64])
            ),
            lit(1L)
          )
        store(algebra, machine)(X, algebra.add(bx, ox))(
          store(algebra, machine)(Y, algebra.add(by, oy))(
            store(algebra, machine)(Z, algebra.add(bz, oz))(
              store(algebra, machine)(Ix, indexed(x, width, ImageDescriptor.SamplerWord.AddressU))(
                store(algebra, machine)(Iy, indexed(y, height, ImageDescriptor.SamplerWord.AddressV))(
                  store(algebra, machine)(
                    Iz,
                    algebra.select(
                      volume,
                      indexed(reg(algebra, machine)(Z), depth, ImageDescriptor.SamplerWord.AddressW),
                      lit(0L)
                    )
                  )(
                    store(algebra, machine)(
                      Outside,
                      algebra.lessThan(algebra.or(algebra.or(ix, iy), iz), lit(0L), signed = true)
                    )(
                      store(algebra, machine)(Subs, subsExpr)(
                        machine.require(squares, "cube image faces")(
                          Fault.InvalidRequest("cube image requires square faces in groups of six")
                        )(
                          machine.repeat(lit(4L))(lane =>
                            storeAt(algebra, machine)(Taps, algebra.add(first, lane), lit(0L))
                          )(
                            machine.repeat(subs) { sub =>
                              val slice = algebra.select(
                                cube,
                                algebra.add(
                                  algebra.multiply(layer, lit(6L)),
                                  algebra.primitive(face, x, y, width, linear, sub)(
                                    Sampling.cubeFace(
                                      _: UInt[64],
                                      _: UInt[64],
                                      _: UInt[64],
                                      _: UInt[64],
                                      _: UInt[64],
                                      _: UInt[64]
                                    )
                                  )
                                ),
                                layer
                              )
                              machine.repeat(lit(4L))(lane =>
                                storeAt(algebra, machine)(
                                  Texel,
                                  lane,
                                  algebra.primitive(
                                    colour,
                                    components,
                                    lane
                                  )(Sampling.border(_: UInt[64], _: UInt[64], _: UInt[64]))
                                )
                              )(
                                machine.when(algebra.or(cube, algebra.equal(outside, lit(0L))))(
                                  machine.texel(
                                    slice,
                                    algebra.select(
                                      cube,
                                      algebra.primitive(face, x, y, width, linear, sub)(
                                        Sampling.cubeX(
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64]
                                        )
                                      ),
                                      ix
                                    ),
                                    algebra.select(
                                      cube,
                                      algebra.primitive(face, x, y, width, linear, sub)(
                                        Sampling.cubeY(
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64]
                                        )
                                      ),
                                      algebra.add(algebra.multiply(iz, height), iy)
                                    ),
                                    Texel
                                  )(machine.done)
                                )(
                                  machine.repeat(lit(if (sample.dref.isDefined) 1L else 4L)) { lane =>
                                    val at       = algebra.add(first, lane)
                                    val fetched  = regAt(algebra, machine)(Texel, lane)
                                    val compared =
                                      if (sample.dref.isDefined)
                                        algebra.primitive(
                                          wordAt(ImageDescriptor.SamplerWord.CompareOp),
                                          reg(algebra, machine)(Reference),
                                          fetched,
                                          format
                                        )(Sampling.compared(
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64]
                                        ))
                                      else fetched
                                    storeAt(algebra, machine)(
                                      Taps,
                                      at,
                                      algebra.primitive(
                                        regAt(algebra, machine)(Taps, at),
                                        compared,
                                        subs,
                                        cube
                                      )(
                                        Sampling.accumulated(
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64],
                                          _: UInt[64]
                                        )
                                      )
                                    )
                                  }(machine.done)
                                )
                              )
                            }(
                              if (gathering && sample.dref.isEmpty)
                                machine.swizzle(
                                  sample.image,
                                  algebra.add(lit(Taps.toLong), first)
                                )(machine.done)
                              else machine.done
                            )
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      }
      def combine(): F[Unit] =
        machine.repeat(lit(if (gathering) 4L else if (sample.dref.isDefined) 1L else 4L)) { lane =>
          val slot                    = algebra.add(algebra.multiply(pass, lit(4L)), lane)
          def tapAt(tap: Int): V      = regAt(algebra, machine)(Taps + tap * 4, lane)
          def bilinear(first: Int): V = algebra.primitive(
            tapAt(first),
            tapAt(first + 1),
            tapAt(first + 2),
            tapAt(first + 3),
            weight(sx, bx),
            weight(sy, by)
          )(Sampling.bilinear(
            _: UInt[64],
            _: UInt[64],
            _: UInt[64],
            _: UInt[64],
            _: UInt[64],
            _: UInt[64]
          ))
          val flat = bilinear(0)
          storeAt(algebra, machine)(
            Results,
            slot,
            if (gathering)
              regAt(algebra, machine)(Taps, algebra.add(algebra.multiply(lane, lit(4L)), channel))
            else algebra.select(
              nearest,
              tapAt(0),
              algebra.select(
                volume,
                algebra.primitive(flat, bilinear(4), weight(sz, bz))(
                  Sampling.lerp(_: UInt[64], _: UInt[64], _: UInt[64])
                ),
                flat
              )
            )
          )
        }(next)
      store(algebra, machine)(Sx, sxExpr)(
        store(algebra, machine)(Sy, syExpr)(
          store(algebra, machine)(Sz, szExpr)(
            store(algebra, machine)(Components, componentsExpr)(
              store(algebra, machine)(Layer, layerExpr)(
                store(algebra, machine)(Bx, base(sx))(
                  store(algebra, machine)(By, base(sy))(
                    store(algebra, machine)(Bz, base(sz))(
                      machine.repeat(taps)(tap => tapBody(tap))(combine())
                    )
                  )
                )
              )
            )
          )
        )
      )
    }
  }

  /** The levels are weighted into the result, which is swizzled and written as integers or floats. */
  private inline def finished[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      sample: Sample,
      format: V,
      mode: V,
      selected: V
  ): F[Unit] = {
    val gathering = sample.gather != Gather.Off
    val count     = if (gathering || sample.dref.isEmpty) 4 else 1
    val integral  =
      if (sample.dref.isDefined) algebra.literal(0L) else algebra.primitive(format)(Sampling.integral(_: UInt[64]))
    val fraction = algebra.select(
      algebra.equal(mode, algebra.literal(2L)),
      algebra.primitive(selected)(Sampling.between(_: UInt[64])),
      algebra.literal(0L)
    )
    def written(): F[Unit] = machine.eachLeaf(Array.empty[Int], count, sample.dest, sample.resultType)(lane =>
      algebra.primitive(integral, regAt(algebra, machine)(Results, algebra.literal(lane.toLong)))(
        Sampling.packed(_: UInt[64], _: UInt[64])
      )
    )(machine.advance)
    machine.repeat(algebra.literal(count.toLong))(lane =>
      storeAt(algebra, machine)(
        Results,
        lane,
        algebra.primitive(regAt(algebra, machine)(Results, lane), regAt(algebra, machine)(Results + 4, lane), fraction)(
          Sampling.mixed(_: UInt[64], _: UInt[64], _: UInt[64])
        )
      )
    )(if (gathering || sample.dref.isDefined) written()
    else machine.swizzle(sample.image, algebra.literal(Results.toLong))(written()))
  }

  private inline def checked[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      condition: V,
      label: String
  )(inline fault: => Fault)(inline next: => F[Unit]): F[Unit] = machine.require(condition, label)(fault)(next)

  /** The first `count` leaves of the operand are doubles in the partial results from `into`, then `next`. */
  private inline def widened[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      operand: Int,
      count: V,
      into: Int
  )(inline next: => F[Unit]): F[Unit] = {
    val sources = Array(operand)
    machine.repeat(count)(lane =>
      storeAt(algebra, machine)(into, lane, widen(algebra)(machine.leafAt(sources, 1, lane)))
    )(next)
  }

  /** The operands of a sample or gather, of `form`, are read, then the descriptors, the levels of detail, the filter
    * and the texels, and the result is written.
    */
  inline def sample[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      form: Sample
  ): F[Unit] = {
    def lit(value: Long): V   = algebra.literal(value)
    def wordAt(index: Int): V = word(algebra, machine)(index)
    val gathering             = form.gather != Gather.Off
    val operands              = ImageOps.operandWords(form.modifiers)
    val mask                  = operands.headOption.fold(-1)(_ & ~Hints)
    val lodMask               = mask & (ImageOperands.Bias | ImageOperands.Lod | ImageOperands.Grad)
    val offsetIndex           = 1 + (if (lodMask == ImageOperands.Grad) 2 else if (lodMask != 0) 1 else 0)
    val offsetMask            = ImageOperands.ConstOffset | ImageOperands.Offset
    val allMask               = offsetMask | ImageOperands.ConstOffsets
    val sources               = Array(form.coordinate)

    def levelling(
        dims: V,
        arrayed: V,
        cube: V,
        cn: V,
        present: V,
        channel: V,
        format: V,
        levels: V,
        lod: V
    ): F[Unit] = {
      val resolved                     = if (lodMask == ImageOperands.Grad) lit(1L) else present
      def weighed(baseLod: V): F[Unit] = {
        val bias = algebra.primitive(
          wordAt(ImageDescriptor.SamplerWord.MipLodBias),
          if (lodMask == ImageOperands.Bias) lod else lit(0L)
        )(Sampling.bias(_: UInt[64], _: UInt[64]))
        val lambdaExpr = algebra.primitive(
          wordAt(ImageDescriptor.SamplerWord.MinLod),
          wordAt(ImageDescriptor.SamplerWord.MaxLod),
          baseLod,
          bias
        )(Sampling.lambda(_: UInt[64], _: UInt[64], _: UInt[64], _: UInt[64]))
        def lambda: V     = reg(algebra, machine)(Lambda)
        def filter: V     = reg(algebra, machine)(Filter)
        def selected: V   = reg(algebra, machine)(Selected)
        def mode: V       = reg(algebra, machine)(Mode)
        def filterExpr: V =
          if (gathering) lit(ImageDescriptor.Filter.Linear.toLong)
          else algebra.select(
            algebra.primitive(lambda)(Sampling.positive(_: UInt[64])),
            wordAt(ImageDescriptor.SamplerWord.MinFilter),
            wordAt(ImageDescriptor.SamplerWord.MagFilter)
          )
        val nearest         = lit(ImageDescriptor.Filter.Nearest.toLong)
        def selectedExpr: V = algebra.primitive(lambda, levels)(Sampling.selected(_: UInt[64], _: UInt[64]))
        def modeExpr: V     =
          if (gathering) lit(0L)
          else algebra.select(
            algebra.equal(
              wordAt(ImageDescriptor.SamplerWord.MipmapMode),
              lit(ImageDescriptor.MipmapMode.Nearest.toLong)
            ),
            lit(1L),
            lit(2L)
          )
        def passed(): F[Unit] =
          machine.repeat(algebra.select(algebra.equal(mode, lit(2L)), lit(2L), lit(1L)))(pass =>
            atLevel(algebra, machine)(form, dims, arrayed, cube, format, levels, filter, selected, mode, pass, channel)(
              machine.done
            )
          )(finished(algebra, machine)(form, format, mode, selected))
        def verifiedFilter(): F[Unit] =
          checked(algebra, machine)(
            algebra.or(
              algebra.equal(filter, nearest),
              algebra.equal(filter, lit(ImageDescriptor.Filter.Linear.toLong))
            ),
            "sample filter"
          )(Fault.Unsupported("sample filter is not nearest or linear"))(
            checked(algebra, machine)(
              if (gathering) lit(1L)
              else algebra.or(
                algebra.equal(filter, nearest),
                algebra.equal(algebra.primitive(format)(Sampling.integral(_: UInt[64])), lit(0L))
              ),
              "integer filter"
            )(Fault.InvalidRequest("integer images require nearest filtering"))(
              checked(algebra, machine)(
                algebra.or(
                  machine.vulkan,
                  algebra.equal(
                    lit(if (form.dref.isDefined) 1L else 0L),
                    algebra.notEqual(wordAt(ImageDescriptor.SamplerWord.CompareEnable), lit(0L))
                  )
                ),
                "depth comparison"
              )(Fault.InvalidRequest("depth comparison and sampler disagree"))(
                checked(algebra, machine)(
                  if (gathering || form.explicit) lit(1L)
                  else algebra.or(
                    present,
                    algebra.or(
                      algebra.equal(levels, lit(1L)),
                      algebra.primitive(
                        wordAt(ImageDescriptor.SamplerWord.MinLod),
                        wordAt(ImageDescriptor.SamplerWord.MaxLod)
                      )(Sampling.fixed(_: UInt[64], _: UInt[64]))
                    )
                  ),
                  "implicit mip selection"
                )(Fault.Unsupported("implicit mip selection needs fragment derivatives"))(
                  passed()
                )
              )
            )
          )
        store(algebra, machine)(Lambda, lambdaExpr)(
          store(algebra, machine)(Filter, filterExpr)(
            store(algebra, machine)(Selected, selectedExpr)(store(algebra, machine)(Mode, modeExpr)(verifiedFilter()))
          )
        )
      }
      if (lodMask == ImageOperands.Lod) weighed(lod)
      else machine.when(resolved)(
        machine.level(form.image, lit(0L), Level)(
          if (lodMask == ImageOperands.Grad)
            machine.require(
              algebra.and(
                algebra.lessOrEqual(dims, machine.count(operands(1)), signed = true),
                algebra.lessOrEqual(dims, machine.count(operands(2)), signed = true)
              ),
              "gradient dimensionality"
            )(Fault.InvalidRequest("sample gradient dimensionality does not match"))(machine.done)
          else machine.done
        )
      )(
        weighed(
          algebra.select(
            resolved,
            gradientLod(algebra, machine)(cube, wordAt(ImageDescriptor.SamplerWord.Unnormalized)),
            lit(0L)
          )
        )
      )
    }

    def operated(dims: V, arrayed: V, cube: V, cn: V, present: V, channel: V, format: V, levels: V): F[Unit] = {
      def lodded(next: V => F[Unit]): F[Unit] =
        if (lodMask == ImageOperands.Bias || lodMask == ImageOperands.Lod)
          checked(algebra, machine)(algebra.equal(machine.count(operands(1)), lit(1L)), "sample LOD scalar")(
            Fault.InvalidRequest("sample LOD is not scalar")
          )(next(widen(algebra)(machine.leaf(operands(1), 0))))
        else next(lit(0L))
      def gradiented(next: => F[Unit]): F[Unit] =
        if (lodMask == ImageOperands.Grad)
          checked(algebra, machine)(
            algebra.and(
              algebra.lessOrEqual(lit(0L), machine.count(operands(1)), signed = true),
              algebra.lessOrEqual(lit(0L), machine.count(operands(2)), signed = true)
            ),
            "sample gradients"
          )(Fault.InvalidRequest("sample gradients are not floating values"))(
            widened(algebra, machine)(
              operands(1),
              algebra.minimum(machine.count(operands(1)), dims, signed = true),
              Gradient + 1
            )(
              widened(algebra, machine)(
                operands(2),
                algebra.minimum(machine.count(operands(2)), dims, signed = true),
                Gradient + 4
              )(next)
            )
          )
        else next
      def gathered(next: => F[Unit]): F[Unit] =
        if (!gathering) next
        else if ((mask & ImageOperands.ConstOffsets) == 0)
          store(algebra, machine)(Offsets + 1, lit(1L))(
            store(algebra, machine)(Offsets + 2, lit(1L))(
              store(algebra, machine)(Offsets + 3, lit(1L))(store(algebra, machine)(Offsets + 4, lit(1L))(next))
            )
          )
        else
          checked(algebra, machine)(algebra.equal(cube, lit(0L)), "cube gather offsets")(
            Fault.InvalidRequest("cube gather does not allow ConstOffsets")
          )(
            checked(algebra, machine)(
              algebra.equal(machine.count(operands(offsetIndex)), lit(8L)),
              "gather offsets"
            )(Fault.InvalidRequest("gather ConstOffsets requires four two-dimensional offsets"))(
              offsets(algebra, machine)(operands(offsetIndex), lit(8L), Offsets, "gather offset limits")(
                Fault.InvalidRequest("gather ConstOffsets exceeds device limits")
              )(next)
            )
          )
      def offset(next: => F[Unit]): F[Unit] =
        if ((mask & offsetMask) == 0) next
        else checked(algebra, machine)(algebra.equal(cube, lit(0L)), "cube offset")(
          Fault.InvalidRequest("cube sampling does not allow ConstOffset")
        )(
          checked(algebra, machine)(
            algebra.equal(machine.count(operands(offsetIndex)), dims),
            "offset dimensionality"
          )(Fault.InvalidRequest("image ConstOffset dimensionality does not match"))(
            offsets(algebra, machine)(operands(offsetIndex), dims, Offset, "offset limits")(
              Fault.InvalidRequest("image ConstOffset exceeds device limits")
            )(next)
          )
        )
      verified(algebra, machine)(
        if (gathering)
          mask == 0 || mask == ImageOperands.ConstOffset || mask == ImageOperands.Offset ||
          mask == ImageOperands.ConstOffsets
        else
          (mask & ~(ImageOperands.Bias | ImageOperands.Lod | ImageOperands.Grad | ImageOperands.ConstOffset)) == 0 &&
          (lodMask == 0 || lodMask == ImageOperands.Bias || lodMask == ImageOperands.Lod ||
            lodMask == ImageOperands.Grad),
        "image operands"
      )(Fault.Unsupported("unsupported sample/gather image operands"))(
        verified(algebra, machine)(
          operands.length == offsetIndex + (if ((mask & allMask) != 0) 1 else 0),
          "operand count"
        )(Fault.InvalidRequest("invalid sample operand count"))(
          offset(
            gathered(
              lodded(lod =>
                gradiented(
                  verified(algebra, machine)(
                    gathering || !form.explicit || lodMask == ImageOperands.Lod || lodMask == ImageOperands.Grad,
                    "explicit sampling"
                  )(Fault.Unsupported("explicit sampling requires Lod or Grad"))(
                    checked(algebra, machine)(
                      if (gathering || form.explicit) lit(1L)
                      else algebra.or(
                        present,
                        algebra.or(
                          algebra.equal(
                            wordAt(ImageDescriptor.SamplerWord.MinFilter),
                            wordAt(ImageDescriptor.SamplerWord.MagFilter)
                          ),
                          algebra.primitive(
                            wordAt(ImageDescriptor.SamplerWord.MinLod),
                            wordAt(ImageDescriptor.SamplerWord.MaxLod)
                          )(Sampling.bounded(_: UInt[64], _: UInt[64]))
                        )
                      ),
                      "implicit level of detail"
                    )(Fault.Unsupported("implicit LOD with distinct min/mag filters needs fragment derivatives"))(
                      levelling(dims, arrayed, cube, cn, present, channel, format, levels, lod)
                    )
                  )
                )
              )
            )
          )
        )
      )
    }

    def described(dims: V, arrayed: V, cube: V, cn: V, present: V, channel: V): F[Unit] =
      machine.samplerWords(form.image, Sampler)(
        machine.formatWord(form.image)(formatWord =>
          machine.levelCount(form.image)(levels =>
            store(algebra, machine)(Checked, lit(1L))(
              machine.repeat(algebra.add(dims, arrayed))(lane =>
                machine.setPartial(
                  lit(Checked.toLong),
                  algebra.and(
                    reg(algebra, machine)(Checked),
                    algebra.primitive(regAt(algebra, machine)(Coords, lane))(Sampling.finite(_: UInt[64]))
                  )
                )(machine.done)
              )(
                checked(algebra, machine)(
                  algebra.and(
                    algebra.lessOrEqual(algebra.add(dims, arrayed), cn, signed = true),
                    reg(algebra, machine)(Checked)
                  ),
                  "sample coordinates"
                )(Fault.InvalidRequest("invalid sample coordinate dimensionality"))(
                  directed(algebra, machine)(dims, cube)(
                    checked(algebra, machine)(
                      algebra.or(
                        algebra.equal(cube, lit(0L)),
                        algebra.equal(wordAt(ImageDescriptor.SamplerWord.Unnormalized), lit(0L))
                      ),
                      "cube coordinates"
                    )(Fault.InvalidRequest("cube sampling requires normalised coordinates"))(
                      operated(
                        dims,
                        arrayed,
                        cube,
                        cn,
                        present,
                        channel,
                        algebra.and(formatWord, lit(ImageDescriptor.Flags.FormatMask)),
                        levels
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )

    def quadded(dims: V, arrayed: V, cube: V, cn: V, channel: V): F[Unit] =
      if (!gathering && !form.explicit)
        machine.gradients(Coords, cn, form.resultType, form.dest, Gradient)(present =>
          described(dims, arrayed, cube, cn, present, channel)
        )
      else described(dims, arrayed, cube, cn, lit(0L), channel)

    def referenced(dims: V, arrayed: V, cube: V, cn: V, q: V): F[Unit] = {
      def channelled(next: V => F[Unit]): F[Unit] = form.gather match {
        case Gather.Component(component) =>
          machine.scalar(component)(c =>
            checked(algebra, machine)(algebra.lessThan(c, lit(4L), signed = false), "gather component")(
              Fault.InvalidRequest("gather component must be in [0,3]")
            )(next(c))
          )
        case _ => next(lit(0L))
      }
      def gathered(channel: V): F[Unit] =
        checked(algebra, machine)(
          if (gathering) algebra.or(algebra.equal(dims, lit(2L)), cube) else lit(1L),
          "gather image"
        )(Fault.InvalidRequest("gather requires a 2D or cube image"))(quadded(dims, arrayed, cube, cn, channel))
      form.dref match {
        case Some(value) =>
          machine.scalar(value)(reference =>
            store(algebra, machine)(
              Reference,
              algebra.primitive(reference, q)(Sampling.reference(_: UInt[64], _: UInt[64]))
            )(channelled(channel => gathered(channel)))
          )
        case None => channelled(channel => gathered(channel))
      }
    }

    def coordinated(dims: V, arrayed: V, cube: V): F[Unit] = {
      val n        = machine.count(form.coordinate)
      val integral = machine.integral(form.coordinate)
      machine.repeat(n) { lane =>
        val supplied = machine.leafAt(sources, 1, lane)
        storeAt(algebra, machine)(
          Coords,
          lane,
          algebra.select(integral, algebra.primitive(supplied)(Sampling.converted(_: UInt[64])), supplied)
        )
      }(
        checked(algebra, machine)(
          if (form.projective)
            algebra.and(
              algebra.and(algebra.equal(arrayed, lit(0L)), algebra.equal(cube, lit(0L))),
              algebra.lessThan(dims, n, signed = true)
            )
          else lit(1L),
          "projective coordinates"
        )(Fault.InvalidRequest("projective sampling requires non-array 1D, 2D or 3D coordinates and q"))(
          if (form.projective) {
            val q = regAt(algebra, machine)(Coords, dims)
            machine.repeat(dims)(lane =>
              storeAt(algebra, machine)(
                Coords,
                lane,
                algebra.primitive(regAt(algebra, machine)(Coords, lane), q)(Sampling.divided(_: UInt[64], _: UInt[64]))
              )
            )(referenced(dims, arrayed, cube, dims, q))
          } else referenced(dims, arrayed, cube, n, lit(Sampling.SingleOne))
        )
      )
    }

    machine.partials(Count)(
      machine.view(form.image, query = false)((dims, arrayed, cube) =>
        machine.numeric(form.coordinate)(coordinated(dims, arrayed, cube))
      )
    )
  }

  /** The level of detail of the coordinates of a query, of `form`, from the gradients across its quad and the sampler.
    */
  inline def query[V, D, F[_]](inline algebra: Algebra[V], inline machine: SPIRVMachine[V, D, F])(
      form: Query
  ): F[Unit] = {
    def lit(value: Long): V = algebra.literal(value)
    val sources             = Array(form.coordinate)
    machine.partials(Count)(
      machine.numeric(form.coordinate)(
        machine.view(form.image, query = true) { (dims, _, cube) =>
          val n = machine.count(form.coordinate)
          machine.require(algebra.lessOrEqual(dims, n, signed = true), "query coordinates")(
            Fault.InvalidRequest("LOD query coordinate shape does not match image")
          )(
            machine.repeat(n)(lane => storeAt(algebra, machine)(Coords, lane, machine.leafAt(sources, 1, lane)))(
              machine.gradients(Coords, n, form.resultType, form.dest, Gradient)(present =>
                machine.sampler(form.image, "LOD query has no sampler")(
                  machine.level(form.image, lit(0L), Level)(
                    machine.levelCount(form.image)(levels =>
                      machine.samplerWord(form.image, ImageDescriptor.SamplerWord.MipLodBias)(bias =>
                        machine.samplerWord(form.image, ImageDescriptor.SamplerWord.MinLod)(lowest =>
                          machine.samplerWord(form.image, ImageDescriptor.SamplerWord.MaxLod)(highest =>
                            machine.samplerWord(form.image, ImageDescriptor.SamplerWord.MipmapMode)(mipmap =>
                              machine.lodResult(form.resultType)(
                                directed(algebra, machine)(dims, cube) {
                                  val base = algebra.select(
                                    present,
                                    gradientLod(algebra, machine)(cube, lit(0L)),
                                    lit(InfinityBits)
                                  )
                                  val lod =
                                    algebra.primitive(base, algebra.primitive(bias)(Sampling.queried(_: UInt[64])))(
                                      Matrices.add(_: UInt[64], _: UInt[64])
                                    )
                                  val clamped = algebra.primitive(lowest, highest, levels, lod)(
                                    Sampling.clamped(_: UInt[64], _: UInt[64], _: UInt[64], _: UInt[64])
                                  )
                                  val selected =
                                    algebra.primitive(mipmap, clamped)(Sampling.reported(_: UInt[64], _: UInt[64]))
                                  machine.eachLeaf(Array.empty[Int], 2, form.dest, form.resultType)(lane =>
                                    algebra.primitive(if (lane == 0) selected else lod, lit(32L))(
                                      Matrices.narrow(_: UInt[64], _: UInt[64])
                                    )
                                  )(machine.advance)
                                }
                              )
                            )
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        }
      )
    )
  }

  def execute(execution: InvocationState, instruction: Instruction): Option[Op.Flow[Unit]] =
    sampleOf(instruction).map(runSample(execution, _)).orElse(queryOf(instruction).map(runQuery(execution, _)))

  private def runSample(execution: InvocationState, form: Sample): Op.Flow[Unit] =
    sample(Execute, InvocationMachine(execution))(form)

  private def runQuery(execution: InvocationState, form: Query): Op.Flow[Unit] =
    query(Execute, InvocationMachine(execution))(form)

  /** The forms the explorer shows for sampling, gathers and level of detail queries. */
  def render(instruction: Instruction, machine: SPIRVMachineRender): Option[Term.Steps] =
    sampleOf(instruction).map(sample(Render, machine)(_)).orElse(queryOf(instruction).map(query(Render, machine)(_)))
}
