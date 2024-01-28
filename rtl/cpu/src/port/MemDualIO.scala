package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class MemDualIO extends Bundle with ConfigIO {
    val iRdEn   =  Input(Bool())
    val iRdAddr =  Input(UInt(ADDR_WIDTH.W))
    val iWrEn   =  Input(Bool())
    val iWrAddr =  Input(UInt(ADDR_WIDTH.W))
    val iWrData =  Input(UInt(DATA_WIDTH.W))
    val iWrMask =  Input(Vec(MASK_WIDTH, Bool()))

    val oRdData = Output(UInt(DATA_WIDTH.W))
}
