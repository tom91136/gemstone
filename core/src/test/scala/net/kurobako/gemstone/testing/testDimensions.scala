package net.kurobako.gemstone.testing

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

def testDimensions(x: Int > 0, y: Int > 0 = 1, z: Int > 0 = 1): Dimensions = Dimensions(x, y, z)
