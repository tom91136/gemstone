package net.kurobako.gemstone.isa.catalogue

import upickle.default.ReadWriter

case class ConditionRow(expression: Int, message: Int, requires: Option[Int]) derives ReadWriter
