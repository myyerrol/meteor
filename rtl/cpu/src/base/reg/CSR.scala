package cpu.base

import chisel3._
import chisel3.util._

import cpu.port._

class CSR extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pCSRRd =         new CSRRdIO
        val pCSRWr = Flipped(new CSRWrIO)
    })

    val mCSR = Mem(CSRS_NUM, UInt(DATA_WIDTH.W))

    io.pCSRRd.bRdData     := mCSR(io.pCSRRd.bRdAddr)
    io.pCSRRd.bRdMSTAData := mCSR(CSR_MSTATUS)
    io.pCSRRd.bRdMTVEData := mCSR(CSR_MTVEC)
    io.pCSRRd.bRdMEPCData := mCSR(CSR_MEPC)
    io.pCSRRd.bRdMCAUData := mCSR(CSR_MCAUSE)

    when (io.pCSRWr.bWrEn) {
        mCSR(io.pCSRWr.bWrAddr) := io.pCSRWr.bWrData
    }

    when (io.pCSRWr.bWrMEn) {
        mCSR(CSR_MEPC)   := io.pCSRWr.bWrMEPCData
        mCSR(CSR_MCAUSE) := io.pCSRWr.bWrMCAUData
    }
}
