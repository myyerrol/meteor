package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class MemDualIO extends Bundle with ConfigIO {
    val bRdEn   =  Input(Bool())
    val bRdAddr =  Input(UInt(ADDR_WIDTH.W))
    val bWrEn   =  Input(Bool())
    val bWrAddr =  Input(UInt(ADDR_WIDTH.W))
    val bWrData =  Input(UInt(DATA_WIDTH.W))
    val bWrMask =  Input(Vec(MASK_WIDTH, Bool()))

    val bRdData = Output(UInt(DATA_WIDTH.W))
}
