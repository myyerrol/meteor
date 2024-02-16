package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class WBU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pGPRWrI = Flipped(new GPRWrIO)
        val pCSRWrI = Flipped(new CSRWrIO)

        val pGPRWrO =         new GPRWrIO
        val pCSRWrO =         new CSRWrIO
    })

    io.pGPRWrO <> io.pGPRWrI
    io.pCSRWrO <> io.pCSRWrI
}
