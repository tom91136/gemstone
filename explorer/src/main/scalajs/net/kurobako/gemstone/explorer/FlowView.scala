package net.kurobako.gemstone.explorer

import com.raquo.laminar.api.L.*
import net.kurobako.gemstone.collection.*

object FlowView {

  def flow(image: ProgramListing.Image, function: String): Either[String, ControlFlow] = image.language match {
    case Language.SASS   => SASSModel.controlFlow(image.bytes, function)
    case Language.AMDGPU => AMDGPUModel.controlFlow(image.bytes, function)
    case _               => Left("no control flow for this ISA")
  }

  def render(language: Language, flow: ControlFlow): HtmlElement = {
    def header(index: Int): Boolean = flow.loops.exists(_.header == index)
    def body(index: Int): Boolean   = flow.loops.exists(_.body.contains(index))
    div(
      cls := "flow-body",
      flow.blocks.zipWithIndex.map { (block, index) =>
        val range =
          s"${SandboxEvidence.address(language, block.first)}-${SandboxEvidence.address(language, block.last)}"
        val edges =
          if (block.open) "open"
          else if (block.successors.isEmpty) "exit"
          else s"-> ${block.successors.mkString(", ")}"
        div(
          cls := Vec(
            "block",
            if (header(index)) "loop-header" else "",
            if (body(index) && !header(index)) "loop-body" else ""
          ).filter(_.nonEmpty).mkString(" "),
          span(cls := "address", range),
          span(cls := "text", s"block $index"),
          span(cls := "successors", edges)
        )
      },
      flow.loops.map(loop =>
        div(
          cls := "loop",
          s"loop at block ${loop.header}: body ${loop.body.mkString(", ")}; latches ${loop.latches.mkString(", ")}"
        )
      )
    )
  }
}
