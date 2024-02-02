package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.mem._

class Top extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pEnd   = new EndIO
        val pTrace = new TraceIO
    });

    val mGPR = Module(new GPR)
    val mMem = Module(new MemDualFakeBB)

    val mIFU = Module(new IFU)
    val mIDU = Module(new IDU)
    val mEXU = Module(new EXU)
    val mLSU = Module(new LSU)
    val mWBU = Module(new WBU)

    io.pEnd.bFlag := false.B
    io.pEnd.bData := mGPR.io.pGPRRd.bRdEData

    io.pTrace.pBase.bPC   := mIFU.io.pBase.bPC
    io.pTrace.pBase.bInst := mMem.io.pMem.bRdDataA
    io.pTrace.pGPRRd      <> mGPR.io.pGPRRd
    io.pTrace.pGPRWr      <> mWBU.io.pGPRWrO
    io.pTrace.pMem        <> mLSU.io.pMemO
    io.pTrace.pIDUCtr     <> mIDU.io.pIDUCtr
    io.pTrace.pIDUData    <> mIDU.io.pIDUData
    io.pTrace.pEXUJmp     <> mEXU.io.pEXUJmp
    io.pTrace.pEXUOut     <> mEXU.io.pEXUOut

    when (mIDU.io.pIDUCtr.bInstName === INST_NAME_X) {
        assert(false.B, "Invalid instruction at 0x%x", mIFU.io.pBase.bPC)
    }
    .elsewhen (mIDU.io.pIDUCtr.bInstName === INST_NAME_EBREAK) {
        io.pEnd.bFlag := true.B
    }
    .otherwise {
        io.pEnd.bFlag := false.B
    }

    mGPR.io.pGPRRS <> mIDU.io.pGPRRS
    mGPR.io.pGPRWr <> mWBU.io.pGPRWrO

    mMem.io.pMem <> mLSU.io.pMemO

    mIFU.io.pEXUJmp <> mEXU.io.pEXUJmp

    mIDU.io.pBase.bPC   := mIFU.io.pBase.bPC
    mIDU.io.pBase.bInst := mMem.io.pMem.bRdDataA

    mEXU.io.pBase.bPC   := mIFU.io.pBase.bPC
    mEXU.io.pBase.bInst := DontCare

    mEXU.io.pIDUCtr  <> mIDU.io.pIDUCtr
    mEXU.io.pIDUData <> mIDU.io.pIDUData

    mLSU.io.pMemI <> mEXU.io.pMem

    mWBU.io.pGPRWrI <> mEXU.io.pGPRWr
}
