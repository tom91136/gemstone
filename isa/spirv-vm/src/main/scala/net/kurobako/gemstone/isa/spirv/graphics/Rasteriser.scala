package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.spirv.*
import scala.collection.mutable

private object Rasteriser {
  val PixelCentre: (Double, Double) = (.5, .5)

  private val FourSamples = Vector((.375, .125), (.875, .375), (.125, .625), (.625, .875))

  def samplePosition(samples: Int in (1, 4), sample: Int): (Double, Double) =
    if (samples == 1) PixelCentre else FourSamples(sample)
}

/** Homogeneous clipping, primitive coverage and interpolation. All primitive types share quad/sample traversal. */
private class Rasteriser(draw: GraphicsDraw, memory: GraphicsAccess) {
  import VertexStage.*

  private val inputs =
    draw.fragment.toVector.flatMap(shader => ShaderRecords.fields(shader, ShaderDirection.Input).map(_._2))
  private val flat       = inputs.filter(_.qualifiers.flat).flatMap(_.location.rawElements).toSet
  private val linear     = inputs.filter(_.qualifiers.noPerspective).flatMap(_.location.rawElements).toSet
  private val centroid   = inputs.filter(_.qualifiers.centroid).flatMap(_.location.rawElements).toSet
  val perSample: Boolean = draw.raster.sampleShading ||
    inputs.exists(_.builtin.exists(b => b.raw == BuiltIn.SampleId || b.raw == BuiltIn.SamplePosition))
  private val quads    = draw.fragment.exists(_.fragmentQuads)
  private val viewport = draw.viewport
  private type Point = (Double, Double)
  private def edge(a: Point, b: Point, p: Point): Double = (b._1 - a._1) * (p._2 - a._2) - (b._2 - a._2) * (p._1 - a._1)
  private def distances(p: IArray[Float]): Array[Double] = Array(
    p(3).toDouble + p(0),
    p(3).toDouble - p(0),
    p(3).toDouble + p(1),
    p(3).toDouble - p(1),
    p(2).toDouble,
    p(3).toDouble - p(2)
  )
  private def interpolate(a: RasterVertex, b: RasterVertex, t: Double): RasterVertex = {
    val position = Array.tabulate(4)(i => (a.position(i) + t * (b.position(i) - a.position(i))).toFloat)
    val varying  = a.varying.map { (location, values) =>
      val w         = (1 - t) * a.position(3) + t * b.position(3)
      val parameter = if (linear(location) && w != 0) t * b.position(3) / w else t
      location ->
        (if (flat(location)) values
         else Array.tabulate(4)(i => (values(i) + parameter * (b.varying(location)(i) - values(i))).toFloat))
    }
    RasterVertex(IArray.unsafeFromArray(position), varying)
  }
  private def clip(vertices: Vector[RasterVertex]): Vector[RasterVertex] = (0 until 6).foldLeft(vertices) {
    (polygon, plane) =>
      if (polygon.isEmpty) polygon
      else {
        val output = Vector.newBuilder[RasterVertex]
        polygon.indices.foreach { index =>
          val previous = polygon((index + polygon.size - 1) % polygon.size)
          val pd       = distances(previous.position)(plane)
          val current  = polygon(index)
          val cd       = distances(current.position)(plane)
          if ((pd >= 0) != (cd >= 0)) output += interpolate(previous, current, (pd / (pd - cd)).toFloat.toDouble)
          if (cd >= 0) output += current
        }
        output.result()
      }
  }
  private def exact(v: RasterVertex): Point = {
    val w = 1.0 / v.position(3)
    (
      viewport.x + (v.position(0) * w + 1) * viewport.width / 2,
      viewport.y + (v.position(1) * w + 1) * viewport.height / 2
    )
  }
  private def screen(v: RasterVertex): Point = {
    def rounded(value: Double): Double = math.copySign(math.floor(math.abs(value * 256) + .5), value) / 256
    val point                          = exact(v)
    (rounded(point._1), rounded(point._2))
  }
  private def polygonCovers(corners: Vector[Point], p: Point): Boolean = corners.indices.forall { i =>
    val a = corners(i); val b = corners((i + 1) % corners.size)
    val e = edge(a, b, p)
    e > 0 || (e == 0 && (b._2 < a._2 || (b._2 == a._2 && b._1 > a._1)))
  }
  private class Primitive(
      val vertices: Vector[RasterVertex],
      val provoking: RasterVertex,
      val corners: Vector[Point],
      val front: Boolean,
      val weights: Point => Array[Double],
      val covers: Point => Boolean,
      val bias: Double = 0,
      val pointCentre: Option[Point] = None
  )

  private def triangle(vertices: Vector[RasterVertex], provoking: RasterVertex): Option[Primitive] =
    if (vertices.exists(_.position(3) == 0)) None
    else {
      val original   = vertices.map(screen(_))
      val signedArea = edge(original(0), original(1), original(2))
      val front      = if (draw.raster.frontCounterClockwise) signedArea < 0 else signedArea > 0
      if (signedArea == 0 || (front && draw.raster.cullFront) || (!front && draw.raster.cullBack)) None
      else {
        val order   = if (signedArea < 0) Vector(0, 2, 1) else Vector(0, 1, 2)
        val corners = order.map(original)
        val sorted  = order.map(vertices)
        val area    = math.abs(signedArea)
        val z       = sorted.map(v =>
          viewport.minDepth + v.position(2).toDouble / v.position(3) * (viewport.maxDepth - viewport.minDepth)
        )
        val dzdx = ((z(1) - z(0)) * (corners(2)._2 - corners(0)._2) - (z(2) - z(0)) * (corners(1)._2 - corners(0)._2)) /
          area
        val dzdy = ((corners(1)._1 - corners(0)._1) * (z(2) - z(0)) - (corners(2)._1 - corners(0)._1) * (z(1) - z(0))) /
          area
        val bias = if (!draw.raster.biasEnabled) 0.0
        else {
          val maximum = z.map(math.abs).max.toFloat
          val unit    = if (draw.depth.exists(_.format.depth == GraphicsDepthFormat.Unorm16)) 1.0 / 65535
          else Math.scalb(1.0, if (maximum == 0) -149 else math.max(-149, Math.getExponent(maximum) - 23))
          val value = draw.raster.biasConstant * unit + draw.raster.biasSlope * math.max(math.abs(dzdx), math.abs(dzdy))
          if (draw.raster.biasClamp > 0) math.min(value, draw.raster.biasClamp.toDouble)
          else if (draw.raster.biasClamp < 0) math.max(value, draw.raster.biasClamp.toDouble) else value
        }
        Some(Primitive(
          sorted,
          provoking,
          corners,
          front,
          p => Array.tabulate(3)(i => edge(corners((i + 1) % 3), corners((i + 2) % 3), p) / area),
          p => polygonCovers(corners, p),
          bias
        ))
      }
    }
  private def line(a: RasterVertex, b: RasterVertex): Option[Primitive] = {
    val da = distances(a.position); val db = distances(b.position)
    if (da.indices.exists(i => da(i) < 0 && db(i) < 0)) None
    else {
      val first    = da.indices.foldLeft(0.0)((t, i) => if (da(i) < 0) math.max(t, da(i) / (da(i) - db(i))) else t)
      val last     = da.indices.foldLeft(1.0)((t, i) => if (db(i) < 0) math.min(t, da(i) / (da(i) - db(i))) else t)
      val vertices = Vector(interpolate(a, b, first), interpolate(a, b, last))
      if (first > last || vertices.exists(_.position(3) <= 0)) None
      else {
        val start = screen(vertices(0)); val end = screen(vertices(1))
        if (start == end) None
        else {
          val horizontal                             = math.abs(end._1 - start._1) >= math.abs(end._2 - start._2)
          def shift(p: Point, amount: Double): Point = if (horizontal) (p._1, p._2 + amount) else (p._1 + amount, p._2)
          val points  = Vector(shift(start, -.5), shift(end, -.5), shift(end, .5), shift(start, .5))
          val corners = if (edge(points(0), points(1), points(2)) < 0)
            Vector(points(0), points(3), points(2), points(1))
          else points
          Some(Primitive(
            vertices,
            a,
            corners,
            front = true,
            p => {
              val t =
                if (horizontal) (p._1 - start._1) / (end._1 - start._1) else (p._2 - start._2) / (end._2 - start._2)
              Array(1 - t, t)
            },
            p => polygonCovers(corners, p)
          ))
        }
      }
    }
  }
  private def point(v: RasterVertex): Option[Primitive] =
    if (v.position(3) <= 0 || distances(v.position).exists(_ < 0)) None
    else {
      val centre  = exact(v)
      val corners = Vector((centre._1 - .5, centre._2 - .5), (centre._1 + .5, centre._2 + .5))
      Some(Primitive(
        Vector(v),
        v,
        corners,
        true,
        _ => Array(1.0),
        p => p._1 >= corners(0)._1 && p._1 < corners(1)._1 && p._2 >= corners(0)._2 && p._2 < corners(1)._2,
        pointCentre = Some(centre)
      ))
    }
  private def rasterise(primitive: Primitive): Vector[Fragment] = {
    val minX = Vector(0, draw.scissor.x, draw.renderArea.x, math.floor(primitive.corners.map(_._1).min).toInt).max
    val minY = Vector(0, draw.scissor.y, draw.renderArea.y, math.floor(primitive.corners.map(_._2).min).toInt).max
    val maxX = Vector(
      draw.width.toLong,
      draw.scissor.x.toLong + draw.scissor.width,
      draw.renderArea.x.toLong + draw.renderArea.width,
      math.ceil(primitive.corners.map(_._1).max).toLong
    ).min.toInt
    val maxY = Vector(
      draw.height.toLong,
      draw.scissor.y.toLong + draw.scissor.height,
      draw.renderArea.y.toLong + draw.renderArea.height,
      math.ceil(primitive.corners.map(_._2).max).toLong
    ).min.toInt
    val invW                                 = primitive.vertices.map(v => 1.0 / v.position(3))
    def depth(weights: Array[Double]): Float =
      (viewport.minDepth +
        weights.indices.map(i => weights(i) * primitive.vertices(i).position(2) * invW(i)).sum *
        (viewport.maxDepth - viewport.minDepth) + primitive.bias).toFloat
    val fragments = Vector.newBuilder[Fragment]
    val step      = if (quads) 2 else 1
    for (qy <- (if (quads) minY & ~1 else minY) until maxY by step) {
      memory.checkpoint()
      for {
        qx     <- (if (quads) minX & ~1 else minX) until maxX by step
        sample <- 0 until (if (perSample) draw.raster.samples else 1)
      } {
        val evaluation =
          if (perSample) Rasteriser.samplePosition(draw.raster.samples, sample) else Rasteriser.PixelCentre
        val group = Vector.newBuilder[Fragment]
        for (lane <- 0 until (if (quads) 4 else 1)) {
          val x        = qx + (lane & 1); val y = qy + (lane >> 1)
          val coverage = (0 until draw.raster.samples).foldLeft(0) { (mask, s) =>
            val position = Rasteriser.samplePosition(draw.raster.samples, s)
            if (
              (draw.raster.sampleMask & (1 << s)) != 0 &&
              (!perSample || s == sample) && x >= minX && x < maxX && y >= minY && y < maxY &&
              primitive.covers((x + position._1, y + position._2))
            ) mask | (1 << s)
            else mask
          }
          if (coverage != 0 || quads) {
            val p               = (x + evaluation._1, y + evaluation._2)
            val weights         = primitive.weights(p)
            val rw              = weights.indices.map(i => weights(i) * invW(i)).sum
            val centroidWeights = if (coverage != 0 && !primitive.covers(p)) {
              val position = Rasteriser.samplePosition(draw.raster.samples, Integer.numberOfTrailingZeros(coverage))
              primitive.weights((x + position._1, y + position._2))
            } else weights
            val varying = primitive.vertices.head.varying.map { (location, values) =>
              location ->
                (if (flat(location)) primitive.provoking.varying(location)
                 else {
                   val w           = if (centroid(location)) centroidWeights else weights
                   val denominator = w.indices.map(i => w(i) * invW(i)).sum
                   Array.tabulate(4) { component =>
                     val same = primitive.vertices.forall(_.varying(location)(component) == values(component))
                     if (same) values(component)
                     else w.indices.map(i =>
                       w(i) * (if (linear(location)) 1.0 else invW(i) / denominator) *
                         primitive.vertices(i).varying(location)(component)
                     ).sum.toFloat
                   }
                 })
            }
            val sampleDepths = Array.tabulate(draw.raster.samples) { s =>
              val position = Rasteriser.samplePosition(draw.raster.samples, s);
              depth(primitive.weights((x + position._1, y + position._2)))
            }
            val pointCoord = primitive.pointCentre.fold(IArray(0f, 0f))(centre =>
              IArray((p._1 + .5 - centre._1).toFloat, (p._2 + .5 - centre._2).toFloat)
            )
            group += Fragment(
              x,
              y,
              depth(weights),
              rw.toFloat,
              primitive.front,
              coverage,
              IArray.unsafeFromArray(sampleDepths),
              varying,
              pointCoord,
              sample
            )
          }
        }
        val lanes = group.result()
        if (lanes.exists(_.coverage != 0)) fragments ++= lanes
      }
    }
    fragments.result()
  }
  def execute(output: VertexStage.Output): Vector[Fragment] = {
    val primitives = mutable.ArrayBuffer.empty[Primitive]
    if (draw.raster.topology == GraphicsTopology.PointList)
      output.vertices.foreach(v => point(v).foreach(primitives += _))
    else for {
      instance     <- 0 until draw.call.instances
      (begin, end) <- output.segments
    } {
      val base     = instance * output.count
      val topology = draw.raster.topology
      val isLine   = topology == GraphicsTopology.LineList || topology == GraphicsTopology.LineStrip
      val width    = if (isLine) 2 else 3
      val step     =
        if (topology == GraphicsTopology.LineList) 2 else if (topology == GraphicsTopology.TriangleList) 3 else 1
      for (vertex <- begin until (end - width + 1) by step)
        if (isLine) line(output.vertices(base + vertex), output.vertices(base + vertex + 1)).foreach(primitives += _)
        else {
          val indices = if (topology == GraphicsTopology.TriangleFan) Vector(vertex + 1, vertex + 2, begin)
          else if (topology == GraphicsTopology.TriangleStrip && (vertex - begin) % 2 != 0)
            Vector(vertex, vertex + 2, vertex + 1)
          else Vector(vertex, vertex + 1, vertex + 2)
          val original = indices.map(i => output.vertices(base + i))
          val polygon  = clip(original)
          for (fan <- 1 until polygon.size - 1)
            triangle(Vector(polygon(0), polygon(fan), polygon(fan + 1)), original.head).foreach(primitives += _)
        }
    }
    primitives.toVector.flatMap(rasterise(_))
  }
}
