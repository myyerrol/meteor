package cpu.core.ml1

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.temp._
import cpu.port._
import cpu.calc._
import cpu.bus._

class EXU extends Module with ConfigInst with Build {
    val io = IO(new Bundle {
        val pBase    = Flipped(new BaseIO)
        val pGPRWr   =         new GPRWrIO
        val pCSRRd   = Flipped(new CSRRdIO)
        val pCSRWr   =         new CSRWrIO
        val pMemData = Flipped(new MemDualDataIO)
        val pIDUCtr  = Flipped(new IDUCtrIO)
        val pIDUData = Flipped(new IDUDataIO)
        val pEXUJmp  =         new EXUJmpIO
        val pEXUOut  =         new EXUOutIO

        val oRdFlag  = Output(Bool())
        val oWrFlag  = Output(Bool())
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
        io.pEXUJmp.bJmpPC := io.pIDUData.bCSRRdData
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
    wMemRdAddr := ADDR_ZERO

    io.pMemData.pRd.bEn   := true.B
    io.pMemData.pRd.bAddr := wMemRdAddr

    when (io.pIDUCtr.bMemWrEn) {
        io.pMemData.pWr.bEn   := true.B
        io.pMemData.pWr.bAddr := mALU.io.oOut
        io.pMemData.pWr.bData := io.pIDUData.bJmpOrWrData
        io.pMemData.pWr.bMask := MuxLookup(
            io.pIDUCtr.bMemByt,
            VecInit(("b1111".U).asBools)) (
            Seq(
                MEM_BYT_1_U -> VecInit(false.B, false.B, false.B, true.B),
                MEM_BYT_2_U -> VecInit(false.B, false.B, true.B,  true.B),
                MEM_BYT_4_U -> VecInit(("b1111".U).asBools)
            )
        )
    }
    .otherwise {
        io.pMemData.pWr.bEn   := false.B
        io.pMemData.pWr.bAddr := ADDR_ZERO
        io.pMemData.pWr.bData := ADDR_ZERO
        io.pMemData.pWr.bMask := VecInit(("b1111".U).asBools)
    }

    val wGPRWrData = MuxLookup(io.pIDUCtr.bRegWrSrc, DATA_ZERO) (
        Seq(
            REG_WR_SRC_ALU -> mALU.io.oOut,
            REG_WR_SRC_PC  -> (io.pBase.bPC + 4.U(ADDR_WIDTH.W)),
            REG_WR_SRC_CSR -> io.pIDUData.bCSRRdData
        )
    )

    val wMemDataRdData = Wire(UInt(DATA_WIDTH.W))

    if (MEM_TYPE.equals("axi4-lite")) {
        val mAXI4LSUM = Module(new AXI4LiteLSUM)
        val mAXI4LSUS = Module(new AXI4LiteLSUS)

        mAXI4LSUM.io.iRdEn   := true.B
        mAXI4LSUM.io.iRdAddr := wMemRdAddr
        mAXI4LSUM.io.iWrEn   := io.pIDUCtr.bMemWrEn
        mAXI4LSUM.io.iWrAddr := mALU.io.oOut
        mAXI4LSUM.io.iWrData := io.pIDUData.bJmpOrWrData
        mAXI4LSUM.io.iWrStrb := MuxLookup(
            io.pIDUCtr.bMemByt,
            VecInit(("b1111".U).asBools)) (
            Seq(
                MEM_BYT_1_U -> VecInit(false.B, false.B, false.B, true.B),
                MEM_BYT_2_U -> VecInit(false.B, false.B, true.B,  true.B),
                MEM_BYT_4_U -> VecInit(("b1111".U).asBools)
            )
        )
        mAXI4LSUM.io.pAR     <> mAXI4LSUS.io.pAR
        mAXI4LSUM.io.pR      <> mAXI4LSUS.io.pR
        mAXI4LSUM.io.pAW     <> mAXI4LSUS.io.pAW
        mAXI4LSUM.io.pW      <> mAXI4LSUS.io.pW
        mAXI4LSUM.io.pB      <> mAXI4LSUS.io.pB
        mAXI4LSUS.io.iRdEn   := mAXI4LSUM.io.iRdEn
        mAXI4LSUS.io.iRdData := io.pMemData.pRd.bData
        mAXI4LSUS.io.iWrEn   := mAXI4LSUM.io.iWrEn

        io.pMemData.pRd.bEn   := mAXI4LSUS.io.oRdEn
        io.pMemData.pRd.bAddr := mAXI4LSUS.io.oRdAddr
        io.pMemData.pWr.bEn   := mAXI4LSUS.io.oWrEn
        io.pMemData.pWr.bAddr := mAXI4LSUS.io.oWrAddr
        io.pMemData.pWr.bData := mAXI4LSUS.io.oWrData
        io.pMemData.pWr.bMask := mAXI4LSUS.io.oWrStrb

        io.oRdFlag := mAXI4LSUM.io.oRdFlag
        io.oWrFlag := mAXI4LSUM.io.oWrFlag

        wMemDataRdData := mAXI4LSUM.io.oRdData
    }
    else {
        wMemDataRdData := io.pMemData.pRd.bData
    }

    when (io.pIDUCtr.bRegWrEn) {
        io.pGPRWr.bWrEn   := true.B
        io.pGPRWr.bWrAddr := io.pIDUData.bGPRRdAddr
        when (io.pIDUCtr.bRegWrSrc === REG_WR_SRC_MEM) {
            wMemRdAddr := mALU.io.oOut
            val wMemRdDataByt1 = wMemDataRdData(BYTE_WIDTH * 1 - 1, 0)
            val wMemRdDataByt2 = wMemDataRdData(BYTE_WIDTH * 2 - 1, 0)
            val wMemRdDataByt4 = wMemDataRdData(BYTE_WIDTH * 4 - 1, 0)
            val wMemRdData = MuxLookup(io.pIDUCtr.bMemByt, DATA_ZERO) (
                Seq(
                    MEM_BYT_1_S ->
                        ExtenSign(wMemRdDataByt1, DATA_WIDTH),
                    MEM_BYT_1_U ->
                        ExtenZero(wMemRdDataByt1, DATA_WIDTH),
                    MEM_BYT_2_S ->
                        ExtenSign(wMemRdDataByt2, DATA_WIDTH),
                    MEM_BYT_2_U ->
                        ExtenZero(wMemRdDataByt2, DATA_WIDTH),
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

    io.pCSRRd.bRdAddr := DontCare
    when (io.pIDUCtr.bRegWrEn & io.pIDUCtr.bRegWrSrc === REG_WR_SRC_CSR) {
        io.pCSRWr.bWrEn   := true.B
        io.pCSRWr.bWrAddr := io.pIDUData.bCSRWrAddr
        io.pCSRWr.bWrData := mALU.io.oOut
    }
    .elsewhen (io.pIDUCtr.bInstName === INST_NAME_MRET) {
        val wRdMSTAData = io.pCSRRd.bRdMSTAData
        io.pCSRWr.bWrEn   := true.B
        io.pCSRWr.bWrAddr := CSR_MSTATUS
        io.pCSRWr.bWrData := Cat(wRdMSTAData(31, 13),
                                 0.U(2.W),
                                 wRdMSTAData(10,  8),
                                 1.U(1.W),
                                 wRdMSTAData( 6,  4),
                                 wRdMSTAData( 7),
                                 wRdMSTAData( 2,  0))
    }
    .otherwise {
        io.pCSRWr.bWrEn   := false.B
        io.pCSRWr.bWrAddr := ADDR_ZERO
        io.pCSRWr.bWrData := DATA_ZERO
    }
    when (io.pIDUCtr.bInstName === INST_NAME_ECALL) {
        val wRdMSTAData = io.pCSRRd.bRdMSTAData
        io.pCSRWr.bWrEn   := true.B
        io.pCSRWr.bWrAddr := CSR_MSTATUS
        io.pCSRWr.bWrData := Cat(wRdMSTAData(31, 13),
                                 3.U(2.W),
                                 wRdMSTAData(10,  8),
                                 wRdMSTAData( 3),
                                 wRdMSTAData( 6,  4),
                                 0.U(1.W),
                                 wRdMSTAData( 2,  0))

        io.pCSRWr.bWrMEn      := true.B
        io.pCSRWr.bWrMEPCData := io.pBase.bPC
        io.pCSRWr.bWrMCAUData := CSR_CODE_M_ECALL
    }
    .otherwise {
        io.pCSRWr.bWrMEn      := false.B
        io.pCSRWr.bWrMEPCData := DATA_ZERO
        io.pCSRWr.bWrMCAUData := DATA_ZERO
    }

    if (MEM_TYPE.equals("axi4-lite")) {
        // mAXI4LSUM.io.iRdEn   := true.B
        // mAXI4LSUM.io.iRdAddr := wMemRdAddr
        // mAXI4LSUM.io.iWrEn   := io.pIDUCtr.bMemWrEn
        // mAXI4LSUM.io.iWrAddr := mALU.io.oOut
        // mAXI4LSUM.io.iWrData := io.pIDUData.bJmpOrWrData
        // mAXI4LSUM.io.iWrStrb := MuxLookup(
        //     io.pIDUCtr.bMemByt,
        //     VecInit(("b1111".U).asBools)) (
        //     Seq(
        //         MEM_BYT_1_U -> VecInit(false.B, false.B, false.B, true.B),
        //         MEM_BYT_2_U -> VecInit(false.B, false.B, true.B,  true.B),
        //         MEM_BYT_4_U -> VecInit(("b1111".U).asBools)
        //     )
        // )
        // mAXI4LSUM.io.pAR     <> mAXI4LSUS.io.pAR
        // mAXI4LSUM.io.pR      <> mAXI4LSUS.io.pR
        // mAXI4LSUM.io.pAW     <> mAXI4LSUS.io.pAW
        // mAXI4LSUM.io.pW      <> mAXI4LSUS.io.pW
        // mAXI4LSUM.io.pB      <> mAXI4LSUS.io.pB
        // mAXI4LSUS.io.iRdEn   := mAXI4LSUM.io.iRdEn
        // mAXI4LSUS.io.iRdData := io.pMemData.pRd.bData
        // mAXI4LSUS.io.iWrEn   := mAXI4LSUM.io.iWrEn

        // io.pMemData.pRd.bEn   := mAXI4LSUS.io.oRdEn
        // io.pMemData.pRd.bAddr := mAXI4LSUS.io.oRdAddr
        // io.pMemData.pWr.bEn   := mAXI4LSUS.io.oWrEn
        // io.pMemData.pWr.bAddr := mAXI4LSUS.io.oWrAddr
        // io.pMemData.pWr.bData := mAXI4LSUS.io.oWrData
        // io.pMemData.pWr.bMask := mAXI4LSUS.io.oWrStrb

        // io.oRdFlag := mAXI4LSUM.io.oRdFlag
        // io.oWrFlag := mAXI4LSUM.io.oWrFlag
    }
}
