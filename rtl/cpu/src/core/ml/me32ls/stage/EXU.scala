package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.calc._

class EXU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pBase    = Flipped(new BaseIO)
        val pGPRWr   =         new GPRWrIO
        val pMem     = Flipped(new MemDualIO)
        val pIDUCtr  = Flipped(new IDUCtrIO)
        val pIDUData = Flipped(new IDUDataIO)
        val pEXUJmp  =         new EXUJmpIO
        val pEXUOut  =         new EXUOutIO
    })

    val mALU = Module(new ALU)
    mALU.io.iType    := io.pIDUCtr.bALUType
    mALU.io.iRS1Data := io.pIDUData.bALURS1Data
    mALU.io.iRS2Data := io.pIDUData.bALURS2Data

    io.pEXUOut.bALUOut := mALU.io.oOut

    when ((io.pIDUCtr.bInstName === INST_NAME_BEQ   ||
           io.pIDUCtr.bInstName === INST_NAME_BNE   ||
           io.pIDUCtr.bInstName === INST_NAME_BLT   ||
           io.pIDUCtr.bInstName === INST_NAME_BGE   ||
           io.pIDUCtr.bInstName === INST_NAME_BLTU  ||
           io.pIDUCtr.bInstName === INST_NAME_BGEU) &&
           (mALU.io.oOut === 1.U)) {
        io.pEXUJmp.bJmpEn := true.B
        io.pEXUJmp.bJmpPC := io.pBase.bPC + io.pIDUData.bJmpOrWrData
    }
    .elsewhen ((io.pIDUCtr.bInstName === INST_NAME_ECALL)) {
        io.pEXUJmp.bJmpEn := true.B
        io.pEXUJmp.bJmpPC := ADDR_ZERO
    }
    .elsewhen (io.pIDUCtr.bJmpEn === true.B) {
        io.pEXUJmp.bJmpEn := true.B
        io.pEXUJmp.bJmpPC := mALU.io.oOut
    }
    .otherwise {
        io.pEXUJmp.bJmpEn := false.B
        io.pEXUJmp.bJmpPC := io.pBase.bPC
    }

    val wMemRdAddr = Wire(UInt(ADDR_WIDTH.W))
    io.pMem.bRdEn    := true.B
    io.pMem.bRdAddrA := io.pBase.bPC
    io.pMem.bRdAddrB := wMemRdAddr

    when (io.pIDUCtr.bMemWrEn) {
        io.pMem.bWrEn   := true.B
        io.pMem.bWrAddr := mALU.io.oOut
        io.pMem.bWrData := io.pIDUData.bJmpOrWrData
        io.pMem.bWrMask := MuxLookup(
            io.pIDUCtr.bMemByt,
            VecInit(("b1111".U).asBools))(
            Seq(
                MEM_BYT_1_U -> VecInit(false.B, false.B, false.B, true.B),
                MEM_BYT_2_U -> VecInit(false.B, false.B, true.B,  true.B),
                MEM_BYT_4_U -> VecInit(("b1111".U).asBools)
            )
        )
    }
    .otherwise {
        io.pMem.bWrEn   := false.B
        io.pMem.bWrAddr := ADDR_ZERO
        io.pMem.bWrData := ADDR_ZERO
        io.pMem.bWrMask := VecInit(("b1111".U).asBools)
    }

    val wGPRWrData = MuxLookup(io.pIDUCtr.bGPRWrSrc, DATA_ZERO)(
        Seq(
            GPR_WR_SRC_ALU -> mALU.io.oOut,
            GPR_WR_SRC_PC  -> (io.pBase.bPC + 4.U(ADDR_WIDTH.W)),
            GPR_WR_SRC_CSR -> DATA_ZERO
        )
    )

    wMemRdAddr := ADDR_ZERO
    when (io.pIDUCtr.bGPRWrEn) {
        io.pGPRWr.bWrEn   := true.B
        io.pGPRWr.bWrAddr := io.pIDUData.bGPRRdAddr
        when (io.pIDUCtr.bGPRWrSrc === GPR_WR_SRC_MEM) {
            wMemRdAddr := mALU.io.oOut
            val wMemRdDataByt1 = io.pMem.bRdDataB(BYTE_WIDTH * 1 - 1, 0)
            val wMemRdDataByt2 = io.pMem.bRdDataB(BYTE_WIDTH * 2 - 1, 0)
            val wMemRdDataByt4 = io.pMem.bRdDataB(BYTE_WIDTH * 4 - 1, 0)
            val wMemRdData = MuxLookup(io.pIDUCtr.bMemByt, DATA_ZERO)(
                Seq(
                    MEM_BYT_1_S ->
                        ExtenSign(wMemRdDataByt1, DATA_WIDTH - BYTE_WIDTH * 1),
                    MEM_BYT_1_U ->
                        ExtenZero(wMemRdDataByt1, DATA_WIDTH - BYTE_WIDTH * 1),
                    MEM_BYT_2_S ->
                        ExtenSign(wMemRdDataByt2, DATA_WIDTH - BYTE_WIDTH * 2),
                    MEM_BYT_2_U ->
                        ExtenZero(wMemRdDataByt2, DATA_WIDTH - BYTE_WIDTH * 2),
                    MEM_BYT_4_S ->
                        ExtenSign(wMemRdDataByt4, DATA_WIDTH),
                    MEM_BYT_4_U ->
                        ExtenZero(wMemRdDataByt4, DATA_WIDTH)
                )
            )
            io.pGPRWr.bWrData := wMemRdData
        }
        .otherwise {
            io.pGPRWr.bWrData := wGPRWrData
        }
    }
    .otherwise {
        io.pGPRWr.bWrEn   := false.B
        io.pGPRWr.bWrAddr := ADDR_ZERO
        io.pGPRWr.bWrData := DATA_ZERO
    }
}
