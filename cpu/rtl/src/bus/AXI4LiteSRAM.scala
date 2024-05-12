package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteSRAM extends Module {
    val io = IO(new Bundle {
        val pRdS = new AXI4LiteRdSIO
    })

    val mAXI4LiteRdS = Module(new AXI4LiteRdS)
    io.pRdS <> mAXI4LiteRdS.io.pRdS
}
