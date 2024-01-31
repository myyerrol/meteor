package cpu.base

import chisel3._
import chisel3.util._

import cpu.port._

class GPR extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pGPRRS =         new GPRRSIO
        val pGPRRd =         new GPRRdIO
        val pGPRWr = Flipped(new GPRWrIO)
    })

    val mGPR = Mem(GPRS_NUM, UInt(DATA_WIDTH.W))
    mGPR(0.U) := 0.U(DATA_WIDTH.W)

    when (io.pGPRWr.bWrEn) {
        mGPR(io.pGPRWr.bWrAddr) := io.pGPRWr.bWrData
    }
    .otherwise {
        mGPR(io.pGPRWr.bWrAddr) := mGPR(io.pGPRWr.bWrAddr)
    }

    io.pGPRRS.bRS1Data  := mGPR(io.pGPRRS.bRS1Addr)
    io.pGPRRS.bRS2Data  := mGPR(io.pGPRRS.bRS2Addr)

    io.pGPRRd.bRdEData  := mGPR(GPRS_END)
    io.pGPRRd.bRdData0  := mGPR( 0.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData1  := mGPR( 1.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData2  := mGPR( 2.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData3  := mGPR( 3.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData4  := mGPR( 4.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData5  := mGPR( 5.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData6  := mGPR( 6.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData7  := mGPR( 7.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData8  := mGPR( 8.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData9  := mGPR( 9.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData10 := mGPR(10.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData11 := mGPR(11.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData12 := mGPR(12.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData13 := mGPR(13.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData14 := mGPR(14.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData15 := mGPR(15.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData16 := mGPR(16.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData17 := mGPR(17.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData18 := mGPR(18.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData19 := mGPR(19.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData20 := mGPR(20.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData21 := mGPR(21.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData22 := mGPR(22.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData23 := mGPR(23.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData24 := mGPR(24.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData25 := mGPR(25.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData26 := mGPR(26.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData27 := mGPR(27.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData28 := mGPR(28.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData29 := mGPR(29.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData30 := mGPR(30.U(GPRS_WIDTH.W))
    io.pGPRRd.bRdData31 := mGPR(31.U(GPRS_WIDTH.W))
}
