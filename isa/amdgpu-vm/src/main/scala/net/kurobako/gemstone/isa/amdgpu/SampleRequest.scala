package net.kurobako.gemstone.isa.amdgpu

/** One lane's sample: the coordinates, in the order the address lists them, and the results in register values. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:MutableExposure"))
private[isa] class SampleRequest {
  val coordinates: Array[Double] = Array.ofDim(3)
  var layer: Double              = 0.0
  val gradients: Array[Double]   = Array.ofDim(6)
  var level: VMIR.SampleLevel    = VMIR.SampleLevel.Zero
  var lod: Double                = 0.0
  var bias: Double               = 0.0
  var minimumLod: Double         = Double.NaN
  var compare: Boolean           = false
  var reference: Double          = 0.0
  val offsets: Array[Int]        = Array.ofDim(3)
  var gather: Int                = -1
  var unnormalised: Boolean      = false
  val result: Array[Long]        = Array.ofDim(4)
}
