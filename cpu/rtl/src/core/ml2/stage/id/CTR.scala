package cpu.core.ml2

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port.ml2._
import cpu.temp._

class CTR extends Module  with ConfigInstRV32I
                          with ConfigInstRV32M
                          with ConfigInstRVPri
                          with Build {
    val io = IO(new Bundle {
        val iPC       = Input(UInt(ADDR_WIDTH.W))
        val iInst     = Input(UInt(INST_WIDTH.W))
        val iWaitFlag = Input(Bool())

        val pCTR  = new CTRIO
    })

    var lInst = ListLookup(
        io.iInst,
        List(INST_NAME_X),
        Array(
            INST_SLL    -> List(INST_NAME_SLL),
            INST_SLLI   -> List(INST_NAME_SLLI),
            INST_SRL    -> List(INST_NAME_SRL),
            INST_SRLI   -> List(INST_NAME_SRLI),
            INST_SRA    -> List(INST_NAME_SRA),
            INST_SRAI   -> List(INST_NAME_SRAI),
            INST_ADD    -> List(INST_NAME_ADD),
            INST_ADDI   -> List(INST_NAME_ADDI),
            INST_SUB    -> List(INST_NAME_SUB),
            INST_LUI    -> List(INST_NAME_LUI),
            INST_AUIPC  -> List(INST_NAME_AUIPC),
            INST_XOR    -> List(INST_NAME_XOR),
            INST_XORI   -> List(INST_NAME_XORI),
            INST_OR     -> List(INST_NAME_OR),
            INST_ORI    -> List(INST_NAME_ORI),
            INST_AND    -> List(INST_NAME_AND),
            INST_ANDI   -> List(INST_NAME_ANDI),
            INST_SLT    -> List(INST_NAME_SLT),
            INST_SLTU   -> List(INST_NAME_SLTU),
            INST_SLTIU  -> List(INST_NAME_SLTIU),
            INST_BEQ    -> List(INST_NAME_BEQ),
            INST_BNE    -> List(INST_NAME_BNE),
            INST_BLT    -> List(INST_NAME_BLT),
            INST_BGE    -> List(INST_NAME_BGE),
            INST_BLTU   -> List(INST_NAME_BLTU),
            INST_BGEU   -> List(INST_NAME_BGEU),
            INST_JAL    -> List(INST_NAME_JAL),
            INST_JALR   -> List(INST_NAME_JALR),
            INST_EBREAK -> List(INST_NAME_EBREAK),
            INST_LB     -> List(INST_NAME_LB),
            INST_LH     -> List(INST_NAME_LH),
            INST_LBU    -> List(INST_NAME_LBU),
            INST_LHU    -> List(INST_NAME_LHU),
            INST_LW     -> List(INST_NAME_LW),
            INST_SB     -> List(INST_NAME_SB),
            INST_SH     -> List(INST_NAME_SH),
            INST_SW     -> List(INST_NAME_SW),

            INST_MUL    -> List(INST_NAME_MUL),
            INST_MULH   -> List(INST_NAME_MULH),
            INST_MULHSU -> List(INST_NAME_MULHSU),
            INST_MULHU  -> List(INST_NAME_MULHU),
            INST_DIV    -> List(INST_NAME_DIV),
            INST_DIVU   -> List(INST_NAME_DIVU),
            INST_REM    -> List(INST_NAME_REM),
            INST_REMU   -> List(INST_NAME_REMU)
        )
    )

    val wInstName = lInst(0)

    val rStateCurr = RegInit(STATE_IF)

    val wPCWrEn      = WireInit(EN_FL)
    val wPCWrSrc     = WireInit(PC_WR_SRC_X)
    val wPCNextEn    = WireInit(EN_FL)
    val wPCJumpEn    = WireInit(EN_FL)
    val wMemRdInstEn = WireInit(EN_FL)
    val wMemRdLoadEn = WireInit(EN_FL)
    val wMemRdSrc    = WireInit(MEM_RD_SRC_X)
    val wMemWrEn     = WireInit(EN_FL)
    val wMemByt      = WireInit(MEM_BYT_X)
    val wIRWrEn      = WireInit(EN_FL)
    val wGPRWrEn     = WireInit(EN_FL)
    val wGPRWrSrc    = WireInit(REG_WR_SRC_X)
    val wALUType     = WireInit(ALU_TYPE_X)
    val wALURS1      = WireInit(ALU_RS1_X)
    val wALURS2      = WireInit(ALU_RS2_X)
    val wEndPreFlag  = WireInit(EN_FL)

    switch (rStateCurr) {
        is (STATE_IF) {
            // rStateCurr := STATE_ID
            rStateCurr := Mux(io.iWaitFlag, rStateCurr, STATE_ID)

            wPCNextEn    := EN_TR
            wMemRdInstEn := EN_TR
            wMemRdSrc    := MEM_RD_SRC_PC
            wIRWrEn      := EN_TR
            wALUType     := ALU_TYPE_ADD
            wALURS1      := ALU_RS1_PC
            wALURS2      := ALU_RS2_4
            wEndPreFlag  := EN_TR


        }
        is (STATE_ID) {
            rStateCurr := STATE_EX

            when (wInstName === INST_NAME_BEQ  ||
                wInstName === INST_NAME_BNE  ||
                wInstName === INST_NAME_BLT  ||
                wInstName === INST_NAME_BGE  ||
                wInstName === INST_NAME_BLTU ||
                wInstName === INST_NAME_BGEU) {
                wPCJumpEn := EN_TR
                wALUType  := ALU_TYPE_ADD
                wALURS1   := ALU_RS1_PC
                wALURS2   := ALU_RS2_IMM_B
            }
            when (wInstName === INST_NAME_JAL) {
                wPCJumpEn := EN_TR
                wALUType  := ALU_TYPE_ADD
                wALURS1   := ALU_RS1_PC
                wALURS2   := ALU_RS2_IMM_J
            }
        }
        is (STATE_EX) {
            rStateCurr := STATE_WB

            when (wInstName === INST_NAME_SLL   ||
                wInstName === INST_NAME_SLLI  ||
                wInstName === INST_NAME_SRL   ||
                wInstName === INST_NAME_SRLI  ||
                wInstName === INST_NAME_SRA   ||
                wInstName === INST_NAME_SRAI) {
                wALUType := MuxLookup(wInstName, ALU_TYPE_X) (
                    Seq(
                        INST_NAME_SLL   -> ALU_TYPE_SLL,
                        INST_NAME_SLLI  -> ALU_TYPE_SLL,
                        INST_NAME_SRL   -> ALU_TYPE_SRL,
                        INST_NAME_SRLI  -> ALU_TYPE_SRL,
                        INST_NAME_SRA   -> ALU_TYPE_SRA,
                        INST_NAME_SRAI  -> ALU_TYPE_SRA
                    )
                )
                wALURS1  := ALU_RS1_GPR
                wALURS2  := MuxLookup(wInstName, ALU_RS2_X) (
                    Seq(
                        INST_NAME_SLL   -> ALU_RS2_GPR,
                        INST_NAME_SLLI  -> ALU_RS2_IMM_I,
                        INST_NAME_SRL   -> ALU_RS2_GPR,
                        INST_NAME_SRLI  -> ALU_RS2_IMM_I,
                        INST_NAME_SRA   -> ALU_RS2_GPR,
                        INST_NAME_SRAI  -> ALU_RS2_IMM_I
                    )
                )
            }
            .elsewhen (wInstName === INST_NAME_ADD   ||
                    wInstName === INST_NAME_ADDI  ||
                    wInstName === INST_NAME_SUB   ||
                    wInstName === INST_NAME_LUI   ||
                    wInstName === INST_NAME_AUIPC) {
                wALUType := MuxLookup(wInstName, ALU_TYPE_X) (
                    Seq(
                        INST_NAME_ADD   -> ALU_TYPE_ADD,
                        INST_NAME_ADDI  -> ALU_TYPE_ADD,
                        INST_NAME_SUB   -> ALU_TYPE_SUB,
                        INST_NAME_LUI   -> ALU_TYPE_ADD,
                        INST_NAME_AUIPC -> ALU_TYPE_ADD
                    )
                )
                wALURS1  := MuxLookup(wInstName, ALU_RS1_GPR) (
                    Seq(
                        INST_NAME_LUI   -> ALU_RS1_X,
                        INST_NAME_AUIPC -> ALU_RS1_PC
                    )
                )
                wALURS2  := MuxLookup(wInstName,ALU_RS2_X) (
                    Seq(
                        INST_NAME_ADD   -> ALU_RS2_GPR,
                        INST_NAME_ADDI  -> ALU_RS2_IMM_I,
                        INST_NAME_SUB   -> ALU_RS2_GPR,
                        INST_NAME_LUI   -> ALU_RS2_IMM_U,
                        INST_NAME_AUIPC -> ALU_RS2_IMM_U,
                    )
                )
            }
            .elsewhen (wInstName === INST_NAME_XOR  ||
                    wInstName === INST_NAME_XORI ||
                    wInstName === INST_NAME_OR   ||
                    wInstName === INST_NAME_ORI  ||
                    wInstName === INST_NAME_AND  ||
                    wInstName === INST_NAME_ANDI) {
                wALUType := MuxLookup(wInstName, ALU_TYPE_X) (
                    Seq(
                        INST_NAME_XOR  -> ALU_TYPE_XOR,
                        INST_NAME_XORI -> ALU_TYPE_XOR,
                        INST_NAME_OR   -> ALU_TYPE_OR,
                        INST_NAME_ORI  -> ALU_TYPE_OR,
                        INST_NAME_AND  -> ALU_TYPE_AND,
                        INST_NAME_ANDI -> ALU_TYPE_AND
                    )
                )
                wALURS1  := ALU_RS1_GPR
                wALURS2  := MuxLookup(wInstName, ALU_RS2_X) (
                    Seq(
                        INST_NAME_XOR  -> ALU_RS2_GPR,
                        INST_NAME_XORI -> ALU_RS2_IMM_I,
                        INST_NAME_OR   -> ALU_RS2_GPR,
                        INST_NAME_ORI  -> ALU_RS2_IMM_I,
                        INST_NAME_AND  -> ALU_RS2_GPR,
                        INST_NAME_ANDI -> ALU_RS2_IMM_I
                    )
                )
            }
            .elsewhen (wInstName === INST_NAME_SLT  ||
                    wInstName === INST_NAME_SLTU ||
                    wInstName === INST_NAME_SLTIU) {
                wALUType := Mux(wInstName === INST_NAME_SLT,
                                ALU_TYPE_SLT,
                                ALU_TYPE_SLTU)
                wALURS1  := ALU_RS1_GPR
                wALURS2  := Mux(wInstName === INST_NAME_SLTIU,
                                ALU_RS2_IMM_I,
                                ALU_RS2_GPR)
            }
            .elsewhen (wInstName === INST_NAME_BEQ  ||
                    wInstName === INST_NAME_BNE  ||
                    wInstName === INST_NAME_BLT  ||
                    wInstName === INST_NAME_BGE  ||
                    wInstName === INST_NAME_BLTU ||
                    wInstName === INST_NAME_BGEU) {
                rStateCurr := STATE_IF

                wPCWrEn  := EN_TR
                wPCWrSrc := PC_WR_SRC_JUMP
                wALUType := MuxLookup(wInstName, ALU_TYPE_X) (
                    Seq(
                        INST_NAME_BEQ  -> ALU_TYPE_BEQ,
                        INST_NAME_BNE  -> ALU_TYPE_BNE,
                        INST_NAME_BLT  -> ALU_TYPE_BLT,
                        INST_NAME_BGE  -> ALU_TYPE_BGE,
                        INST_NAME_BLTU -> ALU_TYPE_BLTU,
                        INST_NAME_BGEU -> ALU_TYPE_BGEU
                    )
                )
                wALURS1  := ALU_RS1_GPR
                wALURS2  := ALU_RS2_GPR
            }
            .elsewhen (wInstName === INST_NAME_JAL) {
                wALUType := ALU_TYPE_ADD
                wALURS1  := ALU_RS1_PC
                wALURS2  := ALU_RS2_4
            }
            .elsewhen (wInstName === INST_NAME_JALR) {
                rStateCurr := STATE_LS

                wPCJumpEn := EN_TR
                wALUType  := ALU_TYPE_JALR
                wALURS1   := ALU_RS1_GPR
                wALURS2   := ALU_RS2_IMM_I
            }
            .elsewhen (wInstName === INST_NAME_LB  ||
                    wInstName === INST_NAME_LH  ||
                    wInstName === INST_NAME_LBU ||
                    wInstName === INST_NAME_LHU ||
                    wInstName === INST_NAME_LW) {
                rStateCurr := STATE_LS

                wALUType := ALU_TYPE_ADD
                wALURS1  := ALU_RS1_GPR
                wALURS2  := ALU_RS2_IMM_I
            }
            .elsewhen (wInstName === INST_NAME_SB ||
                    wInstName === INST_NAME_SH ||
                    wInstName === INST_NAME_SW) {
                rStateCurr := STATE_LS

                wALUType := ALU_TYPE_ADD
                wALURS1  := ALU_RS1_GPR
                wALURS2  := ALU_RS2_IMM_S
            }
            .elsewhen (wInstName === INST_NAME_MUL    ||
                    wInstName === INST_NAME_MULH   ||
                    wInstName === INST_NAME_MULHSU ||
                    wInstName === INST_NAME_MULHU  ||
                    wInstName === INST_NAME_DIV    ||
                    wInstName === INST_NAME_DIVU   ||
                    wInstName === INST_NAME_REM    ||
                    wInstName === INST_NAME_REMU) {
                wALUType := MuxLookup(wInstName, ALU_TYPE_X) (
                    Seq(
                        INST_NAME_MUL    -> ALU_TYPE_MUL,
                        INST_NAME_MULH   -> ALU_TYPE_MULH,
                        INST_NAME_MULHSU -> ALU_TYPE_MULHSU,
                        INST_NAME_MULHU  -> ALU_TYPE_MULHU,
                        INST_NAME_DIV    -> ALU_TYPE_DIV,
                        INST_NAME_DIVU   -> ALU_TYPE_DIVU,
                        INST_NAME_REM    -> ALU_TYPE_REM,
                        INST_NAME_REMU   -> ALU_TYPE_REMU
                    )
                )
                wALURS1  := ALU_RS1_GPR
                wALURS2  := ALU_RS2_GPR
            }
        }
        is (STATE_LS) {
            when (wInstName === INST_NAME_JALR) {
                rStateCurr := STATE_WB

                wALUType := ALU_TYPE_ADD
                wALURS1  := ALU_RS1_PC
                wALURS2  := ALU_RS2_4
            }
            .elsewhen (wInstName === INST_NAME_LB  ||
                    wInstName === INST_NAME_LH  ||
                    wInstName === INST_NAME_LBU ||
                    wInstName === INST_NAME_LHU ||
                    wInstName === INST_NAME_LW) {
                rStateCurr := STATE_WB

                wMemRdLoadEn := EN_TR
                wMemRdSrc    := MEM_RD_SRC_ALU
            }
            .elsewhen (wInstName === INST_NAME_SB ||
                    wInstName === INST_NAME_SH ||
                    wInstName === INST_NAME_SW) {
                rStateCurr := STATE_IF

                wPCWrEn  := EN_TR
                wPCWrSrc := PC_WR_SRC_NEXT
                wMemWrEn := EN_TR
                wMemByt  := MuxLookup(wInstName, MEM_BYT_X) (
                    Seq(
                        INST_NAME_SB -> MEM_BYT_1_U,
                        INST_NAME_SH -> MEM_BYT_2_U,
                        INST_NAME_SW -> MEM_BYT_4_U
                    )
                )
                wALURS2  := ALU_RS2_GPR
            }
        }
        is (STATE_WB) {
            rStateCurr := STATE_IF

            wPCWrEn   := EN_TR
            wPCWrSrc  := PC_WR_SRC_NEXT
            wGPRWrEn  := EN_TR
            wGPRWrSrc := REG_WR_SRC_ALU
            when (wInstName === INST_NAME_JAL ||
                wInstName === INST_NAME_JALR) {
                wPCWrSrc := PC_WR_SRC_JUMP
            }
            .elsewhen (wInstName === INST_NAME_LB  ||
                    wInstName === INST_NAME_LH  ||
                    wInstName === INST_NAME_LBU ||
                    wInstName === INST_NAME_LHU ||
                    wInstName === INST_NAME_LW) {
                wMemByt := MuxLookup(wInstName, MEM_BYT_X) (
                    Seq(
                        INST_NAME_LB  -> MEM_BYT_1_S,
                        INST_NAME_LH  -> MEM_BYT_2_S,
                        INST_NAME_LBU -> MEM_BYT_1_U,
                        INST_NAME_LHU -> MEM_BYT_2_U,
                        INST_NAME_LW  -> MEM_BYT_4_S
                    )
                )
                wGPRWrSrc := REG_WR_SRC_MEM
            }
        }
    }

    io.pCTR.oInstName    := wInstName
    io.pCTR.oStateCurr   := rStateCurr
    io.pCTR.oPCWrEn      := wPCWrEn
    io.pCTR.oPCWrSrc     := wPCWrSrc
    io.pCTR.oPCNextEn    := wPCNextEn
    io.pCTR.oPCJumpEn    := wPCJumpEn
    io.pCTR.oMemRdInstEn := wMemRdInstEn
    io.pCTR.oMemRdLoadEn := wMemRdLoadEn
    io.pCTR.oMemRdSrc    := wMemRdSrc
    io.pCTR.oMemWrEn     := wMemWrEn
    io.pCTR.oMemByt      := wMemByt
    io.pCTR.oIRWrEn      := wIRWrEn
    io.pCTR.oGPRWrEn     := wGPRWrEn
    io.pCTR.oGPRWrSrc    := wGPRWrSrc
    io.pCTR.oALUType     := wALUType
    io.pCTR.oALURS1      := wALURS1
    io.pCTR.oALURS2      := wALURS2
    io.pCTR.oEndPreFlag  := wEndPreFlag
}
