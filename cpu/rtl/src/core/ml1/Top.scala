package cpu.core.ml1

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.port.ml1._
import cpu.temp._
import cpu.mem._

class Top extends Module with ConfigInst with Build {
    val io = IO(new Bundle {
        val pState = new StateIO
        val pTrace = new TraceIO
    });

    val mGPR = Module(new GPR)
    val mCSR = Module(new CSR)
    val mMem = Module(new MemDualFakeBB)

    val mIFU = Module(new IFU)
    val mIDU = Module(new IDU)
    val mEXU = Module(new EXU)
    val mLSU = Module(new LSU)
    val mWBU = Module(new WBU)

    io.pState.bEndPreFlag := true.B
    io.pState.bEndAllFlag := false.B
    io.pState.bEndAllData := mGPR.io.pGPRRd.bRdEData
    io.pState.bCSRType    := MuxLookup(mIDU.io.pIDUCtr.bInstName, CSR_TYPE_X) (
        Seq(
            INST_NAME_ECALL -> CSR_TYPE_ECALL,
            INST_NAME_MRET  -> CSR_TYPE_MRET
        )
    )

    io.pTrace.pBase.bPC          := mIFU.io.pBase.bPC
    io.pTrace.pBase.bPCNext      := mIFU.io.pBase.bPCNext
    io.pTrace.pBase.bPCEn        := mIFU.io.pBase.bPCEn
    io.pTrace.pBase.bInst        := mMem.io.pMemInst.pRd.bData
    io.pTrace.pGPRRd             <> mGPR.io.pGPRRd
    io.pTrace.pGPRWr             <> mWBU.io.pGPRWrO
    io.pTrace.pCSRRd             <> mCSR.io.pCSRRd
    io.pTrace.pCSRWr             <> mWBU.io.pCSRWrO
    io.pTrace.pMemInst.pRd.bEn   := mMem.io.pMemInst.pRd.bEn
    io.pTrace.pMemInst.pRd.bAddr := mMem.io.pMemInst.pRd.bAddr
    io.pTrace.pMemData           <> mLSU.io.pMemDataO
    io.pTrace.pIDUCtr            <> mIDU.io.pIDUCtr
    io.pTrace.pIDUData           <> mIDU.io.pIDUData
    io.pTrace.pEXUJmp            <> mEXU.io.pEXUJmp
    io.pTrace.pEXUOut            <> mEXU.io.pEXUOut

    mGPR.io.pGPRRS <> mIDU.io.pGPRRS
    mGPR.io.pGPRWr <> mWBU.io.pGPRWrO
    mCSR.io.pCSRRd <> mIDU.io.pCSRRd
    mCSR.io.pCSRWr <> mWBU.io.pCSRWrO

    mMem.io.iClock             := clock
    mMem.io.iReset             := reset
    mMem.io.pMemInst.pRd.bEn   := true.B
    mMem.io.pMemInst.pRd.bAddr := mIFU.io.pBase.bPC
    mMem.io.pMemData           <> mLSU.io.pMemDataO

    mIFU.io.iPCEn   := true.B
    mIFU.io.pEXUJmp <> mEXU.io.pEXUJmp

    mIDU.io.pBase.bPC     := mIFU.io.pBase.bPC
    mIDU.io.pBase.bPCEn   := DontCare
    mIDU.io.pBase.bPCNext := DontCare
    mIDU.io.pBase.bInst   := mMem.io.pMemInst.pRd.bData

    mEXU.io.pBase.bPC          := mIFU.io.pBase.bPC
    mEXU.io.pBase.bPCNext      := DontCare
    mEXU.io.pBase.bPCEn        := DontCare
    mEXU.io.pBase.bInst        := DontCare
    mEXU.io.pCSRRd.bRdData     := DontCare
    mEXU.io.pCSRRd.bRdMSTAData := mCSR.io.pCSRRd.bRdMSTAData
    mEXU.io.pCSRRd.bRdMTVEData := DontCare
    mEXU.io.pCSRRd.bRdMEPCData := DontCare
    mEXU.io.pCSRRd.bRdMCAUData := DontCare

    mEXU.io.pIDUCtr  <> mIDU.io.pIDUCtr
    mEXU.io.pIDUData <> mIDU.io.pIDUData

    mLSU.io.pMemDataI <> mEXU.io.pMemData

    mWBU.io.pGPRWrI <> mEXU.io.pGPRWr
    mWBU.io.pCSRWrI <> mEXU.io.pCSRWr

    when (mIDU.io.pBase.bInst =/= DATA_ZERO &&
          mIDU.io.pIDUCtr.bInstName === INST_NAME_X) {
        assert(false.B, "Invalid instruction at 0x%x", mIFU.io.pBase.bPC)
    }
    .elsewhen (mIDU.io.pIDUCtr.bInstName === INST_NAME_EBREAK) {
        io.pState.bEndAllFlag := true.B
    }
    .otherwise {
        io.pState.bEndAllFlag := false.B
    }
}
