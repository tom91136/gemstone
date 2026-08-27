package net.kurobako.gemstone.scheduler

final case class LaunchShape private (blocks: Int, threadsPerBlock: Int)

object LaunchShape {
  def create(blocks: Int, threadsPerBlock: Int): Either[String, LaunchShape] =
    if (blocks <= 0) Left(s"blocks must be positive: $blocks")
    else if (threadsPerBlock <= 0) Left(s"threads per block must be positive: $threadsPerBlock")
    else Right(LaunchShape(blocks, threadsPerBlock))
}
