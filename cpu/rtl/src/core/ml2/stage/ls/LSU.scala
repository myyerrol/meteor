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

        val oWaitEn      = Output(Bool())

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

    // if (MEM_TYPE.equals("axi4-lite")) {
    //     val mAXI4LiteIFUM = Module(new AXI4LiteRdM)
    //     val mAXI4LiteIFUS = Module(new AXI4LiteRdS)

    //     val mMem = Module(new MemDualFakeBB)

    //     mAXI4LiteIFUM.iRdEn   := io.pLSU.oMemRdInstEn
    //     mAXI4LiteIFUM.iRdAddr := io.pLSU.oMemRdAddrInst
    //     mAXI4LiteIFUM.pAR <> mAXI4LiteIFUS.pAR
    //     mAXI4LiteIFUM.pR  <> mAXi4LiteIFUS.pR

    //     mAXI4LiteIFUS.io.iRdData := mMem.io.pMemInst.pRd.bData

    //     mMem.io.pMemInst.pRd.bEn   := mAXI4LiteIFUS.io.oRdEn
    //     mMem.io.pMemInst.pRd.bAddr := mAXi4LiteIFUS.io.oRdAddr

    //     io.pLSU.oMemRdDataInst := mAXI4LiteIFUM.io.oRdData
    // }

    io.oWaitEn := false.B

    val mMem = Module(new MemDualFakeBB)
    mMem.io.pMemInst.pRd.bEn   := io.pLSU.oMemRdInstEn
    mMem.io.pMemInst.pRd.bAddr := io.pLSU.oMemRdAddrInst
    mMem.io.pMemData.pRd.bEn   := io.pLSU.oMemRdLoadEn
    mMem.io.pMemData.pRd.bAddr := io.pLSU.oMemRdAddrLoad
    mMem.io.pMemData.pWr.bEn   := io.pLSU.oMemWrEn
    mMem.io.pMemData.pWr.bAddr := io.pLSU.oMemWrAddr
    mMem.io.pMemData.pWr.bData := io.pLSU.oMemWrData
    mMem.io.pMemData.pWr.bMask := MuxLookup(
        io.iMemByt,
        VecInit(("b1111".U).asBools)) (
        Seq(
            MEM_BYT_1_U -> VecInit(false.B, false.B, false.B, true.B),
            MEM_BYT_2_U -> VecInit(false.B, false.B, true.B,  true.B),
            MEM_BYT_4_U -> VecInit(("b1111".U).asBools)
        )
    )

    io.pLSU.oMemRdDataInst := mMem.io.pMemInst.pRd.bData
    io.pLSU.oMemRdDataLoad := mMem.io.pMemData.pRd.bData

    mMRU.io.iData := mMem.io.pMemData.pRd.bData

    io.pLSU.oMemRdData := mMRU.io.oData
}
