package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class IFU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pBase   =         new BaseIO
        val pEXUJmp = Flipped(new EXUJmpIO)
    })

    val rPC     = RegInit(ADDR_INIT)
    val wPCNext = Mux(io.pEXUJmp.bJmpEn,
                      io.pEXUJmp.bJmpPC,
                      rPC + 4.U(ADDR_WIDTH.W))

    rPC := wPCNext

    io.pBase.bPC   := rPC
    io.pBase.bInst := DontCare
}
