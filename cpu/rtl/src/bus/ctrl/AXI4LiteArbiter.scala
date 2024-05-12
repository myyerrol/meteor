package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteArbiter extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iState = Input(UInt(SIGS_WIDTH.W))

        val pRdM   = new AXI4LiteRdMIO
        val pWrM   = new AXI4LiteWrMIO
    })

    val mAXI4LiteIFU = Module(new AXI4LiteIFU)
    val mAXI4LiteLSU = Module(new AXI4LiteLSU)

    io.pWrM <> mAXI4LiteLSU.io.pWrM

    when (io.iState === STATE_IF) {
        io.pRdM <> mAXI4LiteIFU.io.pRdM
    }
    .elsewhen (io.iState === STATE_LS) {
        io.pRdM <> mAXI4LiteLSU.io.pRdM
    }
    .otherwise {
        io.pRdM <> mAXI4LiteIFU.io.pRdM
    }
}
