package cpu.core.ml.me32ls.stage

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.calc._

class EXU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iPC          =  Input(UInt(ADDR_WIDTH.W))

        val iInstName    =  Input(UInt(SIGS_WIDTH.W))
        val iALUType     =  Input(UInt(SIGS_WIDTH.W))
        val iJmpEn       =  Input(Bool())
        val iMemWrEn     =  Input(Bool())
        val iMemByt      =  Input(UInt(SIGS_WIDTH.W))
        val iGPRWrEn     =  Input(Bool())
        val iGPRWrSrc    =  Input(UInt(SIGS_WIDTH.W))

        val iGPRRdAddr   =  Input(UInt(ADDR_WIDTH.W))
        val iALURS1Data  =  Input(UInt(DATA_WIDTH.W))
        val iALURS2Data  =  Input(UInt(DATA_WIDTH.W))
        val iJmpOrWrData =  Input(UInt(DATA_WIDTH.W))
        val iMemRdData   =  Input(UInt(DATA_WIDTH.W))

        val oJmpEn       = Output(Bool())
        val oJmpPC       = Output(UInt(ADDR_WIDTH.W))
        val oGPRWrEn     = Output(Bool())
        val oGPRWrAddr   = Output(UInt(ADDR_WIDTH.W))
        val oGPRWrData   = Output(UInt(DATA_WIDTH.W))
        val oMemRdAddr   = Output(UInt(ADDR_WIDTH.W))

        val pMem         = new MemDualIO
    })

    val mALU = Module(new ALU)
    mALU.io.iType    := io.iALUType
    mALU.io.iRS1Data := io.iALURS1Data
    mALU.io.iRS2Data := io.iALURS2Data

    when ((io.iInstName === INST_NAME_BEQ   ||
           io.iInstName === INST_NAME_BNE   ||
           io.iInstName === INST_NAME_BLT   ||
           io.iInstName === INST_NAME_BGE   ||
           io.iInstName === INST_NAME_BLTU  ||
           io.iInstName === INST_NAME_BGEU) &&
           (mALU.io.oOut === 1.U)) {
        io.oJmpEn := true.B
        io.oJmpPC := io.iPC + io.iJmpOrWrData
    }
    .elsewhen ((io.iInstName === INST_NAME_ECALL)) {
        io.oJmpEn := true.B
        io.oJmpPC := ADDR_ZERO
    }
    .elsewhen (io.iJmpEn === true.B) {
        io.oJmpEn := true.B
        io.oJmpPC := mALU.io.oOut
    }
    .otherwise {
        io.oJmpEn := false.B
        io.oJmpPC := io.iPC
    }

    // io.pMem.bRdEn := true.B
    // io.pMem.bRdAddr :=

    when (io.iMemWrEn) {
        io.pMem.bWrEn   := true.B
        io.pMem.bWrAddr := mALU.io.oOut
        io.pMem.bWrData := io.iJmpOrWrData
        io.pMem.bWrMask := MuxLookup(
            io.iMemByt,
            VecInit(("b1111".U).asBools))(
            Seq(
                MEM_BYT_1_U -> VecInit(("b0001".U).asBools),
                MEM_BYT_2_U -> VecInit(("b0011".U).asBools),
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

    val wGPRWrData = MuxLookup(io.iGPRWrSrc, DATA_ZERO)(
        Seq(
            GPR_WR_SRC_ALU -> mALU.io.oOut,
            GPR_WR_SRC_PC  -> (io.iPC + 4.U(ADDR_WIDTH.W)),
            GPR_WR_SRC_CSR -> DATA_ZERO
        )
    )
    when (io.iGPRWrEn) {
        io.oGPRWrEn   := false.B
        io.oGPRWrAddr := io.iGPRRdAddr
        when (io.iGPRWrSrc === GPR_WR_SRC_MEM) {
            io.oMemRdAddr := mALU.io.oOut
            val wMemRdDataByt1 = io.iMemRdData(BYTE_WIDTH * 1 - 1, 0)
            val wMemRdDataByt2 = io.iMemRdData(BYTE_WIDTH * 2 - 1, 0)
            val wMemRdDataByt4 = io.iMemRdData(BYTE_WIDTH * 4 - 1, 0)
            val wMemRdData = MuxLookup(io.iMemByt, DATA_ZERO)(
                Seq(
                    (io.iMemByt === MEM_BYT_1_S) ->
                        ExtenSign(wMemRdDataByt1, DATA_WIDTH - BYTE_WIDTH * 1),
                    (io.iMemByt === MEM_BYT_1_U) ->
                        ExtenZero(wMemRdDataByt1, DATA_WIDTH - BYTE_WIDTH * 1),
                    (io.iMemByt === MEM_BYT_2_S) ->
                        ExtenSign(wMemRdDataByt2, DATA_WIDTH - BYTE_WIDTH * 2),
                    (io.iMemByt === MEM_BYT_2_U) ->
                        ExtenZero(wMemRdDataByt2, DATA_WIDTH - BYTE_WIDTH * 2),
                    (io.iMemByt === MEM_BYT_4_S) ->
                        ExtenSign(wMemRdDataByt2, DATA_WIDTH - BYTE_WIDTH * 4),
                    (io.iMemByt === MEM_BYT_4_U) ->
                        ExtenZero(wMemRdDataByt4, DATA_WIDTH - BYTE_WIDTH * 4)
                )
            )
            io.oGPRWrData := wMemRdData
        }
        .otherwise {
            io.oGPRWrData := wGPRWrData
        }
    }
    .otherwise {
        io.oGPRWrEn   := false.B
        io.oGPRWrAddr := 0.U(ADDR_WIDTH.W)
        io.oGPRWrData := 0.U(DATA_WIDTH.W)
    }
}
