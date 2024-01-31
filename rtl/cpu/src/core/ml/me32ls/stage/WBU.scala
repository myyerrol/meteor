package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class WBU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pGPRWrI = Flipped(new GPRWrIO)
        val pGPRWrO =         new GPRWrIO
    })

    io.pGPRWrO <> io.pGPRWrI
}
