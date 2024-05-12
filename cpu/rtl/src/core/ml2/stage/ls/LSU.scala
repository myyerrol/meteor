package cpu.core.ml2

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port.ml2._
import cpu.temp._
import cpu.mem._
import cpu.bus._

class LSU extends Module with ConfigInst
                         with Build {
    val io = IO(new Bundle {
        val iMemRdInstEn = Input(Bool())
        val iMemRdLoadEn = Input(Bool())
        val iMemRdSrc    = Input(UInt(SIGS_WIDTH.W))
        val iMemWrEn     = Input(Bool())
        val iMemByt      = Input(UInt(SIGS_WIDTH.W))

        val iPC          = Input(UInt(ADDR_WIDTH.W))
        val iALUOut      = Input(UInt(DATA_WIDTH.W))
        val iMemWrData   = Input(UInt(DATA_WIDTH.W))

        val iState       = Input(UInt(SIGS_WIDTH.W))

        val oWaitFlag    = Output(Bool())

        val pLSU         = new LSUIO
    })

    io.pLSU.oMemRdInstEn   := io.iMemRdInstEn
    io.pLSU.oMemRdLoadEn   := io.iMemRdLoadEn
    io.pLSU.oMemRdAddrInst := io.iPC
    io.pLSU.oMemRdAddrLoad := io.iALUOut

    when (io.iMemWrEn) {
        io.pLSU.oMemWrEn   := true.B
        io.pLSU.oMemWrAddr := io.iALUOut
        io.pLSU.oMemWrData := io.iMemWrData
        io.pLSU.oMemWrLen  := MuxLookup(io.iMemByt, 1.U(BYTE_WIDTH.W)) (
            Seq(
                MEM_BYT_1_U -> 1.U(BYTE_WIDTH.W),
                MEM_BYT_2_U -> 2.U(BYTE_WIDTH.W),
                MEM_BYT_4_U -> 4.U(BYTE_WIDTH.W)
            )
        )
    }
    .otherwise {
        io.pLSU.oMemWrEn   := false.B
        io.pLSU.oMemWrAddr := DATA_ZERO
        io.pLSU.oMemWrData := DATA_ZERO
        io.pLSU.oMemWrLen  := 1.U(BYTE_WIDTH.W)
    }

    io.pLSU.oMemRdDataInst := DontCare
    io.pLSU.oMemRdDataLoad := DontCare

    val mMRU = Module(new MRU)
    mMRU.io.iEn   := true.B
    mMRU.io.iData := DontCare

    val mMem = Module(new MemDualFakeBB)
    mMem.io.iClock := clock
    mMem.io.iReset := reset

    io.oWaitFlag := false.B

    val wMemWrMask = MuxLookup(
        io.iMemByt,
        VecInit(("b1111".U).asBools)) (
        Seq(
            MEM_BYT_1_U -> VecInit(false.B, false.B, false.B, true.B),
            MEM_BYT_2_U -> VecInit(false.B, false.B, true.B,  true.B),
            MEM_BYT_4_U -> VecInit(("b1111".U).asBools)
        )
    )

    if (MEM_TYPE.equals("direct")) {
        mMem.io.pMemInst.pRd.bEn   := io.pLSU.oMemRdInstEn
        mMem.io.pMemInst.pRd.bAddr := io.pLSU.oMemRdAddrInst
        mMem.io.pMemData.pRd.bEn   := io.pLSU.oMemRdLoadEn
        mMem.io.pMemData.pRd.bAddr := io.pLSU.oMemRdAddrLoad
        mMem.io.pMemData.pWr.bEn   := io.pLSU.oMemWrEn
        mMem.io.pMemData.pWr.bAddr := io.pLSU.oMemWrAddr
        mMem.io.pMemData.pWr.bData := io.pLSU.oMemWrData
        mMem.io.pMemData.pWr.bMask := wMemWrMask
    }
    else if (MEM_TYPE.equals("axi4-lite")) {
        val mAXI4LiteIFU     = Module(new AXI4LiteIFU)
        val mAXI4LiteLSU     = Module(new AXI4LiteLSU)
        val mAXI4LiteSRAM    = Module(new AXI4LiteSRAM)
        mAXI4LiteLSU.io.pWrM  := DontCare
        mAXI4LiteSRAM.io.pWrS := DontCare

        mAXI4LiteIFU.io.pRdM.iRdEn      := io.pLSU.oMemRdInstEn
        mAXI4LiteIFU.io.pRdM.iRdAddr    := io.pLSU.oMemRdAddrInst
        mAXI4LiteIFU.io.pRdM.pAR.bReady := mAXI4LiteSRAM.io.pRdS.pAR.bReady
        mAXI4LiteIFU.io.pRdM.pR.bValid  := mAXI4LiteSRAM.io.pRdS.pR.bValid
        mAXI4LiteIFU.io.pRdM.pR.bData   := mAXI4LiteSRAM.io.pRdS.pR.bData
        mAXI4LiteIFU.io.pRdM.pR.bResp   := mAXI4LiteSRAM.io.pRdS.pR.bResp

        mAXI4LiteLSU.io.pWrM.iWrEn      := io.pLSU.oMemWrEn
        mAXI4LiteLSU.io.pWrM.iWrAddr    := io.pLSU.oMemWrAddr
        mAXI4LiteLSU.io.pWrM.iWrData    := io.pLSU.oMemWrData
        mAXI4LiteLSU.io.pWrM.iWrStrb    := wMemWrMask
        mAXI4LiteLSU.io.pWrM.pAW        <> mAXI4LiteSRAM.io.pWrS.pAW
        mAXI4LiteLSU.io.pWrM.pW         <> mAXI4LiteSRAM.io.pWrS.pW
        mAXI4LiteLSU.io.pWrM.pB         <> mAXI4LiteSRAM.io.pWrS.pB

        mAXI4LiteLSU.io.pRdM.iRdEn      := io.pLSU.oMemRdLoadEn
        mAXI4LiteLSU.io.pRdM.iRdAddr    := io.pLSU.oMemRdAddrLoad
        mAXI4LiteLSU.io.pRdM.pAR.bReady := mAXI4LiteSRAM.io.pRdS.pAR.bReady
        mAXI4LiteLSU.io.pRdM.pR.bValid  := mAXI4LiteSRAM.io.pRdS.pR.bValid
        mAXI4LiteLSU.io.pRdM.pR.bData   := mAXI4LiteSRAM.io.pRdS.pR.bData
        mAXI4LiteLSU.io.pRdM.pR.bResp   := mAXI4LiteSRAM.io.pRdS.pR.bResp

        mAXI4LiteSRAM.io.pWrS.iWrEn     := mAXI4LiteLSU.io.pWrM.oWrEn
        mAXI4LiteSRAM.io.pWrS.iWrState  := mAXI4LiteLSU.io.pWrM.oWrState
        mAXI4LiteSRAM.io.pWrS.iBValid   := true.B
        mAXI4LiteSRAM.io.pWrS.iWrResp   := AXI4_RESP_OKEY

        when (io.iState === STATE_IF) {
            mAXI4LiteSRAM.io.pRdS.pAR.bValid := mAXI4LiteIFU.io.pRdM.pAR.bValid
            mAXI4LiteSRAM.io.pRdS.pAR.bAddr  := mAXI4LiteIFU.io.pRdM.pAR.bAddr
            mAXI4LiteSRAM.io.pRdS.pR.bReady  := mAXI4LiteIFU.io.pRdM.pR.bReady
            mAXI4LiteSRAM.io.pRdS.iRdEn      := mAXI4LiteIFU.io.pRdM.oRdEn
            mAXI4LiteSRAM.io.pRdS.iRdState   := mAXI4LiteIFU.io.pRdM.oRdState
            mAXI4LiteSRAM.io.pRdS.iRValid    := true.B
            mAXI4LiteSRAM.io.pRdS.iRdData    := mMem.io.pMemInst.pRd.bData
            mAXI4LiteSRAM.io.pRdS.iRdResp    := AXI4_RESP_OKEY
        }
        .otherwise {
            mAXI4LiteSRAM.io.pRdS.pAR.bValid := mAXI4LiteLSU.io.pRdM.pAR.bValid
            mAXI4LiteSRAM.io.pRdS.pAR.bAddr  := mAXI4LiteLSU.io.pRdM.pAR.bAddr
            mAXI4LiteSRAM.io.pRdS.pR.bReady  := mAXI4LiteLSU.io.pRdM.pR.bReady
            mAXI4LiteSRAM.io.pRdS.iRdEn      := mAXI4LiteLSU.io.pRdM.oRdEn
            mAXI4LiteSRAM.io.pRdS.iRdState   := mAXI4LiteLSU.io.pRdM.oRdState
            mAXI4LiteSRAM.io.pRdS.iRValid    := true.B
            mAXI4LiteSRAM.io.pRdS.iRdData    := mMem.io.pMemData.pRd.bData
            mAXI4LiteSRAM.io.pRdS.iRdResp    := AXI4_RESP_OKEY
        }

        mMem.io.pMemInst.pRd.bEn   := mAXI4LiteSRAM.io.pRdS.oRdEn
        mMem.io.pMemInst.pRd.bAddr := mAXI4LiteSRAM.io.pRdS.oRdAddr
        mMem.io.pMemData           := DontCare
        mMem.io.pMemData.pWr.bEn   := mAXI4LiteSRAM.io.pWrS.oWrEn
        mMem.io.pMemData.pWr.bAddr := mAXI4LiteSRAM.io.pWrS.oWrAddr
        mMem.io.pMemData.pWr.bData := mAXI4LiteSRAM.io.pWrS.oWrData
        mMem.io.pMemData.pWr.bMask := mAXI4LiteSRAM.io.pWrS.oWrStrb

        io.oWaitFlag := ~mAXI4LiteIFU.io.pRdM.oRdFlag
    }

    io.pLSU.oMemRdDataInst := mMem.io.pMemInst.pRd.bData
    io.pLSU.oMemRdDataLoad := mMem.io.pMemData.pRd.bData

    mMRU.io.iData := mMem.io.pMemData.pRd.bData

    io.pLSU.oMemRdData := mMRU.io.oData
}
