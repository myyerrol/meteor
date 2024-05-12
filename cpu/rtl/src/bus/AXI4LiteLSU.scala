package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteLSU extends Module {
    val io = IO(new Bundle {
        val pRdM = new AXI4LiteRdMIO
        val pWrM = new AXI4LiteWrMIO
    })

    val mAXI4LiteRdM = Module(new AXI4LiteRdM)
    val mAXI4LiteWrM = Module(new AXI4LiteWrM)

    io.pRdM <> mAXI4LiteRdM.io.pRdM
    io.pWrM <> mAXI4LiteWrM.io.pWrM
}
