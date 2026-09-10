#!/usr/bin/env python3
from pathlib import Path
import sys
image = Path(sys.argv[1]).read_bytes()
Path(sys.argv[2]).write_text("#pragma once\ninline constexpr unsigned char dispatch_image[] = {\n" +
                           ",".join(str(value) for value in image) + "\n};\n")
