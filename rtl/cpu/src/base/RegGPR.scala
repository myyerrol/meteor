package cpu.base

import chisel3._
import chisel3.util._

import cpu.port._

class RegGPR extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRd1Addr = Input(UInt(GPRS_WIDTH.W))
        val iRd2Addr = Input(UInt(GPRS_WIDTH.W))
        val iWrEn    = Input(Bool())
        val iWrAddr  = Input(UInt(ADDR_WIDTH.W))
        val iWrData  = Input(UInt(DATA_WIDTH.W))

        val pRegGPR  = new RegGPRIO
    })

    val mRegGPR = Mem(GPRS_NUM, UInt(DATA_WIDTH.W))
    mRegGPR(0.U) := 0.U(DATA_WIDTH.W)

    when (io.iWrEn) {
        mRegGPR(io.iWrAddr) := io.iWrData
    }
    .otherwise {
        mRegGPR(io.iWrAddr) := mRegGPR(io.iWrAddr)
    }

    io.pRegGPR.bRd1Data  := mRegGPR(io.iRd1Addr)
    io.pRegGPR.bRd2Data  := mRegGPR(io.iRd2Addr)
    io.pRegGPR.bRdEData  := mRegGPR(GPRS_END)

    io.pRegGPR.bRdData0  := mRegGPR( 0.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData1  := mRegGPR( 1.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData2  := mRegGPR( 2.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData3  := mRegGPR( 3.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData4  := mRegGPR( 4.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData5  := mRegGPR( 5.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData6  := mRegGPR( 6.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData7  := mRegGPR( 7.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData8  := mRegGPR( 8.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData9  := mRegGPR( 9.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData10 := mRegGPR(10.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData11 := mRegGPR(11.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData12 := mRegGPR(12.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData13 := mRegGPR(13.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData14 := mRegGPR(14.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData15 := mRegGPR(15.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData16 := mRegGPR(16.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData17 := mRegGPR(17.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData18 := mRegGPR(18.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData19 := mRegGPR(19.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData20 := mRegGPR(20.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData21 := mRegGPR(21.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData22 := mRegGPR(22.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData23 := mRegGPR(23.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData24 := mRegGPR(24.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData25 := mRegGPR(25.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData26 := mRegGPR(26.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData27 := mRegGPR(27.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData28 := mRegGPR(28.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData29 := mRegGPR(29.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData30 := mRegGPR(30.U(GPRS_WIDTH.W))
    io.pRegGPR.bRdData31 := mRegGPR(31.U(GPRS_WIDTH.W))
}
