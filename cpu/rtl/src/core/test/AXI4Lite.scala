package cpu.core.test

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.mem._
import cpu.bus._

class AXI4Lite extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pState = new StateIO
    });

    val mMem = Module(new MemDualFakeBB)
    mMem.io.iClock := clock
    mMem.io.iReset := reset

    // // AXI4-Lite Read Test
    // // ------------------------------------------------------------------------
    // val mAXI4LiteRdM = Module(new AXI4LiteRdM)
    // val mAXI4LiteRdS = Module(new AXI4LiteRdS)

    // val wRdEn = WireInit(true.B)
    // val rAddr = RegInit(ADDR_INIT)

    // val (cRValidNum, cRValidFlag) = Counter(mAXI4LiteRdS.io.pRdS.oRdEn, 5)

    // mAXI4LiteRdM.io.pRdM.iRdEn    := wRdEn
    // mAXI4LiteRdM.io.pRdM.iRdAddr  := rAddr
    // mAXI4LiteRdM.io.pRdM.pAR      <> mAXI4LiteRdS.io.pRdS.pAR
    // mAXI4LiteRdM.io.pRdM.pR       <> mAXI4LiteRdS.io.pRdS.pR
    // mAXI4LiteRdS.io.pRdS.iRdEn    := mAXI4LiteRdM.io.pRdM.oRdEn
    // mAXI4LiteRdS.io.pRdS.iRdState := mAXI4LiteRdM.io.pRdM.oRdState
    // mAXI4LiteRdS.io.pRdS.iRValid  := cRValidFlag
    // mAXI4LiteRdS.io.pRdS.iRdData  := mMem.io.pMemInst.pRd.bData
    // mAXI4LiteRdS.io.pRdS.iRdResp  := AXI4_RESP_OKEY

    // mMem.io.pMemInst.pRd.bEn   := mAXI4LiteRdS.io.pRdS.oRdEn
    // mMem.io.pMemInst.pRd.bAddr := mAXI4LiteRdS.io.pRdS.oRdAddr
    // mMem.io.pMemData           := DontCare

    // when (mAXI4LiteRdM.io.pRdM.oRdFlag) {
    //     rAddr := rAddr + 4.U
    // }

    // io.pState.bEndPreFlag := DontCare
    // io.pState.bEndAllFlag := Mux(mAXI4LiteRdM.io.pRdM.oRdData === INST_EBRK,
    //                              true.B,
    //                              false.B)
    // io.pState.bEndAllData := DontCare
    // io.pState.bCSRType    := DontCare

    // AXI4-Lite Write Test
    // ------------------------------------------------------------------------
    val mAXI4LiteWrM = Module(new AXI4LiteWrM)
    val mAXI4LiteWrS = Module(new AXI4LiteWrS)

    val wWrEn = WireInit(true.B)
    val rAddr = RegInit(ADDR_INIT)

    val (cBValidNum, cBValidFlag) = Counter(mAXI4LiteWrS.io.pWrS.oWrEn, 5)

    mAXI4LiteWrM.io.pWrM.iWrEn     := wWrEn
    mAXI4LiteWrM.io.pWrM.iWrAddr   := rAddr
    mAXI4LiteWrM.io.pWrM.iWrData   := "x00000001".U(DATA_WIDTH.W)
    mAXI4LiteWrM.io.pWrM.iWrStrb   := VecInit(true.B, true.B, true.B, true.B)
    mAXI4LiteWrM.io.pWrM.pAW       <> mAXI4LiteWrS.io.pWrS.pAW
    mAXI4LiteWrM.io.pWrM.pW        <> mAXI4LiteWrS.io.pWrS.pW
    mAXI4LiteWrM.io.pWrM.pB        <> mAXI4LiteWrS.io.pWrS.pB
    mAXI4LiteWrS.io.pWrS.iWrEn     := mAXI4LiteWrM.io.pWrM.oWrEn
    mAXI4LiteWrS.io.pWrS.iWrState  := mAXI4LiteWrM.io.pWrM.oWrState
    mAXI4LiteWrS.io.pWrS.iBValid   := cBValidFlag
    mAXI4LiteWrS.io.pWrS.iWrResp   := AXI4_RESP_OKEY

    mMem.io.pMemData.pWr.bEn   := mAXI4LiteWrS.io.pWrS.oWrEn
    mMem.io.pMemData.pWr.bAddr := mAXI4LiteWrS.io.pWrS.oWrAddr
    mMem.io.pMemData.pWr.bData := mAXI4LiteWrS.io.pWrS.oWrData
    mMem.io.pMemData.pWr.bMask := mAXI4LiteWrS.io.pWrS.oWrStrb
    mMem.io.pMemInst           := DontCare
    mMem.io.pMemData.pRd       := DontCare

    when (mAXI4LiteWrM.io.pWrM.oWrFlag) {
        rAddr := rAddr + 4.U
    }

    io.pState.bEndPreFlag := DontCare
    io.pState.bEndAllFlag := DontCare
    io.pState.bEndAllData := DontCare
    io.pState.bCSRType    := DontCare
}
