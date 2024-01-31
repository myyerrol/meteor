package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.mem._

class Top extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pBase  =         new BaseIO
        val pEnd   =         new EndIO
        val pGPRRd =         new GPRRdIO
        val pMem   = Flipped(new MemDualIO)
    });

    val mGPR = Module(new GPR)

    val mIFU = Module(new IFU)
    val mIDU = Module(new IDU)
    val mEXU = Module(new EXU)
    val mLSU = Module(new LSU)
    val mWBU = Module(new WBU)

    io.pBase.bPC   := mIFU.io.pBase.bPC
    io.pBase.bInst := io.pMem.bRdDataA

    io.pEnd.bFlag := false.B
    io.pEnd.bData := mGPR.io.pGPRRd.bRdEData
    io.pGPRRd     <> mGPR.io.pGPRRd
    io.pMem       <> mLSU.io.pMemO

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

    mIFU.io.pEXUJmp <> mEXU.io.pEXUJmp

    mIDU.io.pBase.bPC   := mIFU.io.pBase.bPC
    mIDU.io.pBase.bInst := io.pMem.bRdDataA

    mEXU.io.pBase.bPC   := mIFU.io.pBase.bPC
    mEXU.io.pBase.bInst := DontCare

    mEXU.io.pIDUCtr  <> mIDU.io.pIDUCtr
    mEXU.io.pIDUData <> mIDU.io.pIDUData

    mLSU.io.pMemI <> mEXU.io.pMem

    mWBU.io.pGPRWrI <> mEXU.io.pGPRWr
}
