package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class AXI4LiteWrMIO extends Bundle with ConfigIO {
    // Data from/to customized moodule
    val iWrEn    = Input(Bool())
    val iWrAddr  = Input(UInt(ADDR_WIDTH.W))
    val iWrData  = Input(UInt(DATA_WIDTH.W))
    val iWrStrb  = Input(Vec(MASK_WIDTH, Bool()))
    // Data from/to master machine
    val oWrEn    = Output(Bool())
    // Data from/to slave machine
    val oWrFlag  = Output(Bool())
    val oWrResp  = Output(UInt(RESP_WIDTH.W))
    val oWrState = Output(UInt(AXSM_WIDTH.W))

    val pAW      = new AXI4LiteAWIO
    val pW       = new AXI4LiteWIO
    val pB       = new AXI4LiteBIO
}

class AXI4LiteWrSIO extends Bundle with ConfigIO {
    // Data from/to master machine
    val iWrEn    = Input(Bool())
    val iWrState = Input(UInt(AXSM_WIDTH.W))
    // Data from/to memory
    val iBValid  = Input(Bool())
    val iWrResp  = Input(UInt(RESP_WIDTH.W))
    val oWrEn    = Output(Bool())
    val oWrAddr  = Output(UInt(ADDR_WIDTH.W))
    val oWrData  = Output(UInt(DATA_WIDTH.W))
    val oWrStrb  = Output(Vec(MASK_WIDTH, Bool()))

    val pAW      = Flipped(new AXI4LiteAWIO)
    val pW       = Flipped(new AXI4LiteWIO)
    val pB       = Flipped(new AXI4LiteBIO)
}
