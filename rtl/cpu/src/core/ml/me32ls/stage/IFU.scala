package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class IFU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iJmpEn = Input(Bool())
        val iJmpPC = Input(UInt(ADDR_WIDTH.W))

        val pIFU   = new IFUIO
    })

    val rPC     = RegInit(ADDR_INIT)
    val wPCNext = Mux(io.iJmpEn, io.iJmpPC, rPC + 4.U(ADDR_WIDTH.W))

    rPC := wPCNext

    io.pIFU.bPC := rPC
}
