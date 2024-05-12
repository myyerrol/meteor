package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class AXI4LiteRdMIO extends Bundle with ConfigIO {
    // Data from/to customized moodule
    val iRdEn    = Input(Bool())
    val iRdAddr  = Input(UInt(ADDR_WIDTH.W))
    // Data from/to master machine
    val oRdEn    = Output(Bool())
    // Data from/to slave machine
    val oRdFlag  = Output(Bool())
    val oRdData  = Output(UInt(DATA_WIDTH.W))
    val oRdResp  = Output(UInt(RESP_WIDTH.W))
    val oRdState = Output(UInt(AXSM_WIDTH.W))

    val pAR      = new AXI4LiteARIO
    val pR       = new AXI4LiteRIO
}

class AXI4LiteRdSIO extends Bundle with ConfigIO {
    // Data from/to master machine
    val iRdEn    = Input(Bool())
    val iRdState = Input(UInt(AXSM_WIDTH.W))
    // Data from/to memory
    val iRValid  = Input(Bool())
    val iRdData  = Input(UInt(DATA_WIDTH.W))
    val iRdResp  = Input(UInt(RESP_WIDTH.W))
    val oRdEn    = Output(Bool())
    val oRdAddr  = Output(UInt(ADDR_WIDTH.W))

    val pAR      = Flipped(new AXI4LiteARIO)
    val pR       = Flipped(new AXI4LiteRIO)
}
