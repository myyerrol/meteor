package cpu.core.ml.me32ls.stage

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class LSU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pMemI =         new MemDualIO
        val pMemO = Flipped(new MemDualIO)
    })

    io.pMemO <> io.pMemI
}
