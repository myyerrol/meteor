package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.bus._
import cpu.mem._

class Top extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pState = new StateIO
        val pTrace = new TraceIO
    });

    val mGPR = Module(new GPR)
    val mCSR = Module(new CSR)
    val mMem = Module(new MemDualFakeBB)

    val mAXI4IFUM = Module(new AXI4LiteIFUMaster)
    val mAXI4IFUS = Module(new AXI4LiteIFUSlave)

    val mIFU = Module(new IFU)
    val mIDU = Module(new IDU)
    val mEXU = Module(new EXU)
    val mLSU = Module(new LSU)
    val mWBU = Module(new WBU)

    io.pState.bEndFlag := false.B
    io.pState.bEndData := mGPR.io.pGPRRd.bRdEData

    io.pTrace.pBase.bPC   := mIFU.io.pBase.bPC
    // io.pTrace.pBase.bInst := mMem.io.pMem.bRdDataA
    io.pTrace.pBase.bInst := mAXI4IFUM.io.oData
    io.pTrace.pGPRRd      <> mGPR.io.pGPRRd
    io.pTrace.pGPRWr      <> mWBU.io.pGPRWrO
    io.pTrace.pCSRRd      <> mCSR.io.pCSRRd
    io.pTrace.pCSRWr      <> mWBU.io.pCSRWrO
    io.pTrace.pMem        <> mLSU.io.pMemO
    io.pTrace.pIDUCtr     <> mIDU.io.pIDUCtr
    io.pTrace.pIDUData    <> mIDU.io.pIDUData
    io.pTrace.pEXUJmp     <> mEXU.io.pEXUJmp
    io.pTrace.pEXUOut     <> mEXU.io.pEXUOut

    mGPR.io.pGPRRS <> mIDU.io.pGPRRS
    mGPR.io.pGPRWr <> mWBU.io.pGPRWrO
    mCSR.io.pCSRRd <> mIDU.io.pCSRRd
    mCSR.io.pCSRWr <> mWBU.io.pCSRWrO

    // mMem.io.pMem   <> mLSU.io.pMemO
    // mMem.io.pMem.bRdEn    := true.B
    // mMem.io.pMem.bRdAddrA := mAXI4IFUS.io.oAddr
    mMem.io.pRdInst.bEn   := true.B
    mMem.io.pRdInst.bAddr := mAXI4IFUS.io.oAddr

    mAXI4IFUM.io.iRdEn := true.B
    mAXI4IFUM.io.iAddr := mIFU.io.pBase.bPC
    // mAXI4IFUS.io.iData := mMem.io.pMem.bRdDataA
    mAXI4IFUS.io.iData := mMem.io.pRdInst.bData

    mAXI4IFUM.io.pAR <> mAXI4IFUS.io.pAR
    mAXI4IFUM.io.pR  <> mAXI4IFUS.io.pR

    mIFU.io.iEn := false.B
    mIFU.io.pEXUJmp <> mEXU.io.pEXUJmp

    mIDU.io.pBase.bPC   := mIFU.io.pBase.bPC
    // mIDU.io.pBase.bInst := mMem.io.pMem.bRdDataA
    mIDU.io.pBase.bInst := mAXI4IFUM.io.oData

    mEXU.io.pBase.bPC   := mIFU.io.pBase.bPC
    mEXU.io.pBase.bInst := DontCare

    mEXU.io.pIDUCtr  <> mIDU.io.pIDUCtr
    mEXU.io.pIDUData <> mIDU.io.pIDUData

    mLSU.io.pMemI <> mEXU.io.pMem

    mWBU.io.pGPRWrI <> mEXU.io.pGPRWr
    mWBU.io.pCSRWrI <> mEXU.io.pCSRWr

    when (mIDU.io.pIDUCtr.bInstName === INST_NAME_X) {
        assert(false.B, "Invalid instruction at 0x%x", mIFU.io.pBase.bPC)
    }
    .elsewhen (mIDU.io.pIDUCtr.bInstName === INST_NAME_EBREAK) {
        io.pState.bEndFlag := true.B
    }
    .otherwise {
        io.pState.bEndFlag := false.B
    }

    io.pState.bCSRType := MuxLookup(mIDU.io.pIDUCtr.bInstName, 0.U(2.W)) (
        Seq(
            INST_NAME_ECALL -> 1.U(2.W),
            INST_NAME_MRET  -> 2.U(2.W)
        )
    )
}
