package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteSRAM extends Module {
    val io = IO(new Bundle {
        val pRdS = new AXI4LiteRdSIO
        val pWrS = new AXI4LiteWrSIO
    })

    val mAXI4LiteRdS = Module(new AXI4LiteRdS)
    val mAXI4LiteWrS = Module(new AXI4LiteWrS)

    io.pRdS <> mAXI4LiteRdS.io.pRdS
    io.pWrS <> mAXI4LiteWrS.io.pWrS
}
