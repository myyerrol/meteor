package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class MemSingleIO extends Bundle with ConfigIO {
    val bAddr   =  Input(UInt(ADDR_WIDTH.W))
    val bWrEn   =  Input(Bool())
    val bWrData =  Input(UInt(DATA_WIDTH.W))
    val bWrMask =  Input(Vec(MASK_WIDTH, Bool()))

    val bRdData = Output(UInt(DATA_WIDTH.W))
}
