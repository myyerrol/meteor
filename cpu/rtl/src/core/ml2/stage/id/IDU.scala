package cpu.core.ml2

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.port.ml2._

class IDU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iPC        = Input(UInt(ADDR_WIDTH.W))
        val iInst      = Input(UInt(INST_WIDTH.W))
        val iGPRWrData = Input(UInt(DATA_WIDTH.W))

        val pCTR       = new CTRIO
        // val pGPRRS     = new GPRRSIO
        val pGPRRd     = new GPRRdIO
        // val pGPRWr     = new GPRWrIO
        val pIDU       = new IDUIO
    })

    val mCTR = Module(new CTR)
    mCTR.io.iPC   := io.iPC
    mCTR.io.iInst := io.iInst

    io.pCTR <> mCTR.io.pCTR

    val wInst = io.iInst

    val mGPR = Module(new GPR)
    val wRS1Addr = wInst(19, 15)
    val wRS2Addr = wInst(24, 20)
    val wRDAddr  = wInst(11, 07)

    mGPR.io.pGPRRS.bRS1Addr := wRS1Addr
    mGPR.io.pGPRRS.bRS2Addr := wRS2Addr
    mGPR.io.pGPRWr.bWrEn    := io.pCTR.oGPRWrEn
    mGPR.io.pGPRWr.bWrAddr  := wRDAddr
    mGPR.io.pGPRWr.bWrData  := io.iGPRWrData

    // io.pGPRRS <> mGPR.io.pGPRRS
    io.pGPRRd <> mGPR.io.pGPRRd
    // io.pGPRWr <> mGPR.io.pGPRWr

    io.pIDU.oRS1Addr := wRS1Addr
    io.pIDU.oRS2Addr := wRS2Addr
    io.pIDU.oRDAddr  := wRDAddr

    val mGPRRS1 = Module(new GPRRS1)
    val mGPRRS2 = Module(new GPRRS2)
    mGPRRS1.io.iEn   := true.B
    mGPRRS2.io.iEn   := true.B
    mGPRRS1.io.iData := mGPR.io.pGPRRS.bRS1Data
    mGPRRS2.io.iData := mGPR.io.pGPRRS.bRS2Data

    io.pIDU.oRS1Data := mGPRRS1.io.oData
    io.pIDU.oRS2Data := mGPRRS2.io.oData
    io.pIDU.oEndData := mGPR.io.pGPRRd.bRdEData

    io.pIDU.oImmData := MuxLookup(io.pCTR.oALURS2, DATA_ZERO) (
        Seq(
            ALU_RS2_IMM_I -> ExtenImm(wInst, "immI"),
            ALU_RS2_IMM_S -> ExtenImm(wInst, "immS"),
            ALU_RS2_IMM_B -> ExtenImm(wInst, "immB"),
            ALU_RS2_IMM_U -> ExtenImm(wInst, "immU"),
            ALU_RS2_IMM_J -> ExtenImm(wInst, "immJ"),
        )
    )
}
