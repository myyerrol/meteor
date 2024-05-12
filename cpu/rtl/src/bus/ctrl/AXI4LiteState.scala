package cpu.bus

import chisel3._
import chisel3.util._

class AXI4LiteState extends Module {
    val sRdAddrValid :: sRdAddrShake :: sRdDataShake :: Nil = Enum(3)
    val rRdState = RegInit(sRdAddrValid)
}
