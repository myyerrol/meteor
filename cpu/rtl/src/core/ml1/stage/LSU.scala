package cpu.core.ml1

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class LSU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pMemDataI =         new MemDualDataIO
        val pMemDataO = Flipped(new MemDualDataIO)
    })

    io.pMemDataO <> io.pMemDataI
}
