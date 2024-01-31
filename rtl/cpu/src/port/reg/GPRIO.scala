package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class GPRRdIO extends Bundle with ConfigIO {
    val bRS1Data  = Output(UInt(DATA_WIDTH.W))
    val bRS2Data  = Output(UInt(DATA_WIDTH.W))
    val bRdEData  = Output(UInt(DATA_WIDTH.W))

    val bRdData0  = Output(UInt(DATA_WIDTH.W))
    val bRdData1  = Output(UInt(DATA_WIDTH.W))
    val bRdData2  = Output(UInt(DATA_WIDTH.W))
    val bRdData3  = Output(UInt(DATA_WIDTH.W))
    val bRdData4  = Output(UInt(DATA_WIDTH.W))
    val bRdData5  = Output(UInt(DATA_WIDTH.W))
    val bRdData6  = Output(UInt(DATA_WIDTH.W))
    val bRdData7  = Output(UInt(DATA_WIDTH.W))
    val bRdData8  = Output(UInt(DATA_WIDTH.W))
    val bRdData9  = Output(UInt(DATA_WIDTH.W))
    val bRdData10 = Output(UInt(DATA_WIDTH.W))
    val bRdData11 = Output(UInt(DATA_WIDTH.W))
    val bRdData12 = Output(UInt(DATA_WIDTH.W))
    val bRdData13 = Output(UInt(DATA_WIDTH.W))
    val bRdData14 = Output(UInt(DATA_WIDTH.W))
    val bRdData15 = Output(UInt(DATA_WIDTH.W))
    val bRdData16 = Output(UInt(DATA_WIDTH.W))
    val bRdData17 = Output(UInt(DATA_WIDTH.W))
    val bRdData18 = Output(UInt(DATA_WIDTH.W))
    val bRdData19 = Output(UInt(DATA_WIDTH.W))
    val bRdData20 = Output(UInt(DATA_WIDTH.W))
    val bRdData21 = Output(UInt(DATA_WIDTH.W))
    val bRdData22 = Output(UInt(DATA_WIDTH.W))
    val bRdData23 = Output(UInt(DATA_WIDTH.W))
    val bRdData24 = Output(UInt(DATA_WIDTH.W))
    val bRdData25 = Output(UInt(DATA_WIDTH.W))
    val bRdData26 = Output(UInt(DATA_WIDTH.W))
    val bRdData27 = Output(UInt(DATA_WIDTH.W))
    val bRdData28 = Output(UInt(DATA_WIDTH.W))
    val bRdData29 = Output(UInt(DATA_WIDTH.W))
    val bRdData30 = Output(UInt(DATA_WIDTH.W))
    val bRdData31 = Output(UInt(DATA_WIDTH.W))
}

class GPRWrIO extends Bundle with ConfigIO {
    val bWrEn   = Output(Bool())
    val bWrAddr = Output(UInt(ADDR_WIDTH.W))
    val bWrData = Output(UInt(DATA_WIDTH.W))
}

