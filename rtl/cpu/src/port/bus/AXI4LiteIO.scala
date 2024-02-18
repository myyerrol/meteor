package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class AXI4LiteARIO extends Bundle with ConfigIO {
    val bReady = Input(Bool())

    val bValid = Output(Bool())
    val bAddr  = Output(UInt(ADDR_WIDTH.W))
}

class AXI4LiteRIO extends Bundle with ConfigIO {
    val bValid = Input(Bool())
    val bData  = Input(UInt(DATA_WIDTH.W))
    val bResp  = Input(UInt(RESP_WIDTH.W))

    val bReady = Output(Bool())
}

class AXI4LiteIO extends Bundle with ConfigIO {
    val pAR = new AXI4LiteARIO
    val pR  = new AXI4LiteRIO
}
