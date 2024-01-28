package cpu.base

import chisel3._
import chisel3.util._

import cpu.port._

class RegGPR extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRS1Addr = Input(UInt(GPRS_WIDTH.W))
        val iRS2Addr = Input(UInt(GPRS_WIDTH.W))
        val iWrEn    = Input(Bool())
        val iWrAddr  = Input(UInt(ADDR_WIDTH.W))
        val iWrData  = Input(UInt(DATA_WIDTH.W))

        val pGPR     = new RegGPRIO
    })

    val mGPR = Mem(GPRS_NUM, UInt(DATA_WIDTH.W))
    mGPR(0.U) := 0.U(DATA_WIDTH.W)

    when (io.iWrEn) {
        mGPR(io.iWrAddr) := io.iWrData
    }
    .otherwise {
        mGPR(io.iWrAddr) := mGPR(io.iWrAddr)
    }

    io.pGPR.bRS1Data  := mGPR(io.iRS1Addr)
    io.pGPR.bRS2Data  := mGPR(io.iRS2Addr)
    io.pGPR.bRdEData  := mGPR(GPRS_END)

    io.pGPR.bRdData0  := mGPR( 0.U(GPRS_WIDTH.W))
    io.pGPR.bRdData1  := mGPR( 1.U(GPRS_WIDTH.W))
    io.pGPR.bRdData2  := mGPR( 2.U(GPRS_WIDTH.W))
    io.pGPR.bRdData3  := mGPR( 3.U(GPRS_WIDTH.W))
    io.pGPR.bRdData4  := mGPR( 4.U(GPRS_WIDTH.W))
    io.pGPR.bRdData5  := mGPR( 5.U(GPRS_WIDTH.W))
    io.pGPR.bRdData6  := mGPR( 6.U(GPRS_WIDTH.W))
    io.pGPR.bRdData7  := mGPR( 7.U(GPRS_WIDTH.W))
    io.pGPR.bRdData8  := mGPR( 8.U(GPRS_WIDTH.W))
    io.pGPR.bRdData9  := mGPR( 9.U(GPRS_WIDTH.W))
    io.pGPR.bRdData10 := mGPR(10.U(GPRS_WIDTH.W))
    io.pGPR.bRdData11 := mGPR(11.U(GPRS_WIDTH.W))
    io.pGPR.bRdData12 := mGPR(12.U(GPRS_WIDTH.W))
    io.pGPR.bRdData13 := mGPR(13.U(GPRS_WIDTH.W))
    io.pGPR.bRdData14 := mGPR(14.U(GPRS_WIDTH.W))
    io.pGPR.bRdData15 := mGPR(15.U(GPRS_WIDTH.W))
    io.pGPR.bRdData16 := mGPR(16.U(GPRS_WIDTH.W))
    io.pGPR.bRdData17 := mGPR(17.U(GPRS_WIDTH.W))
    io.pGPR.bRdData18 := mGPR(18.U(GPRS_WIDTH.W))
    io.pGPR.bRdData19 := mGPR(19.U(GPRS_WIDTH.W))
    io.pGPR.bRdData20 := mGPR(20.U(GPRS_WIDTH.W))
    io.pGPR.bRdData21 := mGPR(21.U(GPRS_WIDTH.W))
    io.pGPR.bRdData22 := mGPR(22.U(GPRS_WIDTH.W))
    io.pGPR.bRdData23 := mGPR(23.U(GPRS_WIDTH.W))
    io.pGPR.bRdData24 := mGPR(24.U(GPRS_WIDTH.W))
    io.pGPR.bRdData25 := mGPR(25.U(GPRS_WIDTH.W))
    io.pGPR.bRdData26 := mGPR(26.U(GPRS_WIDTH.W))
    io.pGPR.bRdData27 := mGPR(27.U(GPRS_WIDTH.W))
    io.pGPR.bRdData28 := mGPR(28.U(GPRS_WIDTH.W))
    io.pGPR.bRdData29 := mGPR(29.U(GPRS_WIDTH.W))
    io.pGPR.bRdData30 := mGPR(30.U(GPRS_WIDTH.W))
    io.pGPR.bRdData31 := mGPR(31.U(GPRS_WIDTH.W))
}
