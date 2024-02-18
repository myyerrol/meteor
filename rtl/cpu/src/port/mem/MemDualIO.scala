package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class MemDualRdIO extends Bundle with ConfigIO {
    val bEn   = Input(Bool())
    val bAddr = Input(UInt(ADDR_WIDTH.W))

    val bData = Output(UInt(DATA_WIDTH.W))
}

class MemDualWrIO extends Bundle with ConfigIO {
    val bEn   = Input(Bool())
    val bAddr = Input(UInt(ADDR_WIDTH.W))
    val bData = Input(UInt(DATA_WIDTH.W))
    val bMask = Input(Vec(MASK_WIDTH, Bool()))
}

class MemDualInstIO extends Bundle {
    val pRd = new MemDualRdIO
}

class MemDualDataIO extends Bundle {
    val pRd = new MemDualRdIO
    val pWr = new MemDualWrIO
}
