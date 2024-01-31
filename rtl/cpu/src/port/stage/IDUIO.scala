package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class IDUCtrIO extends Bundle with ConfigIO {
    val bInstName = Output(UInt(SIGS_WIDTH.W))
    val bALUType  = Output(UInt(SIGS_WIDTH.W))
    val bALURS1   = Output(UInt(SIGS_WIDTH.W))
    val bALURS2   = Output(UInt(SIGS_WIDTH.W))
    val bJmpEn    = Output(Bool())
    val bMemWrEn  = Output(Bool())
    val bMemByt   = Output(UInt(SIGS_WIDTH.W))
    val bGPRWrEn  = Output(Bool())
    val bGPRWrSrc = Output(UInt(SIGS_WIDTH.W))
}

class IDUDataIO extends Bundle with ConfigIO {
    val bGPRRdAddr   = Output(UInt(ADDR_WIDTH.W))
    val bALURS1Data  = Output(UInt(DATA_WIDTH.W))
    val bALURS2Data  = Output(UInt(DATA_WIDTH.W))
    val bJmpOrWrData = Output(UInt(DATA_WIDTH.W))
}
