package net.kurobako.gemstone.launcher

case class Environment(set: Map[String, String], unset: Set[String]) {
  def applyTo(target: java.util.Map[String, String]): Unit = {
    unset.foreach(target.remove(_))
    set.foreach { case (name, value) => target.put(name, value) }
  }
}
