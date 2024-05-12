package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteIFU extends Module {
    val io = IO(new Bundle {
        val pRdM = new AXI4LiteRdMIO
    })

    val mAXI4LiteRdM = Module(new AXI4LiteRdM)
    io.pRdM <> mAXI4LiteRdM.io.pRdM
}
