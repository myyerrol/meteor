package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class MemSingleIO extends Bundle with ConfigIO {
    val iAddr   =  Input(UInt(ADDR_WIDTH.W))
    val iWrEn   =  Input(Bool())
    val iWrData =  Input(UInt(DATA_WIDTH.W))
    val iWrMask =  Input(Vec(MASK_WIDTH, Bool()))

    val oRdData = Output(UInt(DATA_WIDTH.W))
}
