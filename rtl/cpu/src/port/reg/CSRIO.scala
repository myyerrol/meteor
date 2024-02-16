package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class CSRRdIO extends Bundle with ConfigIO {
    val bRdAddr     = Input(UInt(ADDR_WIDTH.W))

    val bRdData     = Output(UInt(DATA_WIDTH.W))
    val bRdMSTAData = Output(UInt(DATA_WIDTH.W))
    val bRdMTVEData = Output(UInt(DATA_WIDTH.W))
    val bRdMEPCData = Output(UInt(DATA_WIDTH.W))
    val bRdMCAUData = Output(UInt(DATA_WIDTH.W))
}

class CSRWrIO extends Bundle with ConfigIO {
    val bWrEn       = Output(Bool())
    val bWrMEn      = Output(Bool())
    val bWrAddr     = Output(UInt(ADDR_WIDTH.W))
    val bWrData     = Output(UInt(DATA_WIDTH.W))
    val bWrMEPCData = Output(UInt(DATA_WIDTH.W))
    val bWrMCAUData = Output(UInt(DATA_WIDTH.W))
}
