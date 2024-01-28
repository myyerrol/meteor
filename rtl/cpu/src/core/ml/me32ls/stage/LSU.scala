package cpu.core.ml.me32ls.stage

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.mem._

class LSU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pMem = new MemDualIO
    })

    val mMem = Module(new MemDualFake("async"))

    io.pMem <> mMem.io.pMem
}
