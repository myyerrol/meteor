package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.mem._

class Top extends Module with ConfigInst {
    val io = IO(new Bundle {
        val oPC   = Output(UInt(ADDR_WIDTH.W))
        val oInst = Output(UInt(DATA_WIDTH.W))
        val oEnd  = Output(UInt(DATA_WIDTH.W))
    });

    val mGPR = Module(new RegGPR())
    val mMem = Module(new MemDualFake("async"))

    val mIFU = Module(new IFU())
    val mIDU = Module(new IDU())
    val mEXU = Module(new EXU())
    val mLSU = Module(new LSU())
    val mWBU = Module(new WBU())

    io.oPC   := mIFU.io.pIFU.bPC
    io.oInst := mMem.io.pMem.bRdDataA
    io.oEnd  := mGPR.io.pGPR.bRdEData

    mGPR.io.iRS1Addr := mIDU.io.oGPRRS1Addr
    mGPR.io.iRS2Addr := mIDU.io.oGPRRS2Addr
    mGPR.io.iWrEn    := mWBU.io.oGPRWrEn
    mGPR.io.iWrAddr  := mWBU.io.oGPRWrAddr
    mGPR.io.iWrData  := mWBU.io.oGPRWrData

    mMem.io.pMem <> mLSU.io.pMemO

    mIFU.io.iJmpEn := mEXU.io.oJmpEn
    mIFU.io.iJmpPC := mEXU.io.oJmpPC

    mIDU.io.iPC         := mIFU.io.pIFU.bPC
    mIDU.io.iInst       := mMem.io.pMem.bRdDataA
    mIDU.io.iGPRRS1Data := mGPR.io.pGPR.bRS1Data
    mIDU.io.iGPRRS2Data := mGPR.io.pGPR.bRS2Data

    mEXU.io.iPC          := mIFU.io.pIFU.bPC
    mEXU.io.iInstName    := mIDU.io.oInstName
    mEXU.io.iALUType     := mIDU.io.oALUType
    mEXU.io.iJmpEn       := mIDU.io.oJmpEn
    mEXU.io.iMemWrEn     := mIDU.io.oMemWrEn
    mEXU.io.iMemByt      := mIDU.io.oMemByt
    mEXU.io.iGPRWrEn     := mIDU.io.oGPRWrEn
    mEXU.io.iGPRWrSrc    := mIDU.io.oGPRWrSrc
    mEXU.io.iGPRRdAddr   := mIDU.io.oGPRRdAddr
    mEXU.io.iALURS1Data  := mIDU.io.oALURS1Data
    mEXU.io.iALURS2Data  := mIDU.io.oALURS2Data
    mEXU.io.iJmpOrWrData := mIDU.io.oJmpOrWrData
    mEXU.io.iMemRdData   := mMem.io.pMem.bRdDataB

    mLSU.io.pMemI <> mEXU.io.pMem

    mWBU.io.iGPRWrEn   := mEXU.io.oGPRWrEn
    mWBU.io.iGPRWrAddr := mEXU.io.oGPRWrAddr
    mWBU.io.iGPRWrData := mEXU.io.oGPRWrData
}
