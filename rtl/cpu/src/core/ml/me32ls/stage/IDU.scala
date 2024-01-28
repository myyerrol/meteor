package cpu.core.ml.me32ls.stage

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class IDU extends Module with ConfigInstPattern {
    val io = IO(new Bundle {
        val iPC          =  Input(UInt(ADDR_WIDTH.W))
        val iInst        =  Input(UInt(INST_WIDTH.W))
        val iGPRRS1Data  =  Input(UInt(DATA_WIDTH.W))
        val iGPRRS2Data  =  Input(UInt(DATA_WIDTH.W))

        val oInstName    = Output(UInt(SIGS_WIDTH.W))
        val oALUType     = Output(UInt(SIGS_WIDTH.W))
        val oALURS1      = Output(UInt(SIGS_WIDTH.W))
        val oALURS2      = Output(UInt(SIGS_WIDTH.W))
        val oJmpEn       = Output(Bool())
        val oMemWrEn     = Output(Bool())
        val oMemByt      = Output(UInt(SIGS_WIDTH.W))
        val oGPRWrEn     = Output(Bool())
        val oGPRWrSrc    = Output(UInt(SIGS_WIDTH.W))

        val oGPRRS1Addr  = Output(UInt(ADDR_WIDTH.W))
        val oGPRRS2Addr  = Output(UInt(ADDR_WIDTH.W))
        val oGPRRdAddr   = Output(UInt(ADDR_WIDTH.W))
        val oALURS1Data  = Output(UInt(DATA_WIDTH.W))
        val oALURS2Data  = Output(UInt(DATA_WIDTH.W))
        val oJmpOrWrData = Output(UInt(DATA_WIDTH.W))
    })

    val wInst = io.iInst;
    var lInst = ListLookup(
        wInst,
        List(INST_NAME_X, ALU_TYPE_X, ALU_RS1_X, ALU_RS2_X, JMP_FL, MEM_WR_FL, MEM_BYT_X, GPR_WR_FL, GPR_WR_SRC_X),
        Array(
            INST_SLL    -> List(INST_NAME_SLL,    ALU_TYPE_SLL,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SLLI   -> List(INST_NAME_SLLI,   ALU_TYPE_SLL,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRLI   -> List(INST_NAME_SRLI,   ALU_TYPE_SRL,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRA    -> List(INST_NAME_SRA,    ALU_TYPE_SRA,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRAI   -> List(INST_NAME_SRAI,   ALU_TYPE_SRA,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SLLW   -> List(INST_NAME_SLLW,   ALU_TYPE_SLLW,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SLLIW  -> List(INST_NAME_SLLIW,  ALU_TYPE_SLL,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRLW   -> List(INST_NAME_SRLW,   ALU_TYPE_SRLW,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRLIW  -> List(INST_NAME_SRLIW,  ALU_TYPE_SRLIW, ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRAW   -> List(INST_NAME_SRAW,   ALU_TYPE_SRAW,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRAIW  -> List(INST_NAME_SRAIW,  ALU_TYPE_SRAIW, ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),

            INST_ADD    -> List(INST_NAME_ADD,    ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_ADDI   -> List(INST_NAME_ADDI,   ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SUB    -> List(INST_NAME_SUB,    ALU_TYPE_SUB,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_LUI    -> List(INST_NAME_LUI,    ALU_TYPE_ADD,   ALU_RS1_X,    ALU_RS2_IMM_U, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_AUIPC  -> List(INST_NAME_AUIPC,  ALU_TYPE_ADD,   ALU_RS1_PC,   ALU_RS2_IMM_U, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_ADDW   -> List(INST_NAME_ADDW,   ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_ADDIW  -> List(INST_NAME_ADDIW,  ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SUBW   -> List(INST_NAME_SUBW,   ALU_TYPE_SUB,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),

            INST_XOR    -> List(INST_NAME_XOR,    ALU_TYPE_XOR,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_XORI   -> List(INST_NAME_XORI,   ALU_TYPE_XOR,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_OR     -> List(INST_NAME_OR,     ALU_TYPE_OR,    ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_ORI    -> List(INST_NAME_ORI,    ALU_TYPE_OR,    ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_AND    -> List(INST_NAME_AND,    ALU_TYPE_AND,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_ANDI   -> List(INST_NAME_ANDI,   ALU_TYPE_AND,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),

            INST_SLT    -> List(INST_NAME_SLT,    ALU_TYPE_SLT,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SLTU   -> List(INST_NAME_SLTU,   ALU_TYPE_SLTU,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SLTIU  -> List(INST_NAME_SLTIU,  ALU_TYPE_SLTU,  ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),

            INST_BEQ    -> List(INST_NAME_BEQ,    ALU_TYPE_BEQ,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_BNE    -> List(INST_NAME_BNE,    ALU_TYPE_BNE,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_BLT    -> List(INST_NAME_BLT,    ALU_TYPE_BLT,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_BGE    -> List(INST_NAME_BGE,    ALU_TYPE_BGE,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_BLTU   -> List(INST_NAME_BLTU,   ALU_TYPE_BLTU,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_BGEU   -> List(INST_NAME_BGEU,   ALU_TYPE_BGEU,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),

            INST_JAL    -> List(INST_NAME_JAL,    ALU_TYPE_ADD,   ALU_RS1_PC,   ALU_RS2_IMM_J, JMP_TR, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_PC),
            INST_JALR   -> List(INST_NAME_JALR,   ALU_TYPE_JALR,  ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_TR, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_PC),

            INST_LB     -> List(INST_NAME_LB,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_1_S, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_LH     -> List(INST_NAME_LH,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_2_S, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_LBU    -> List(INST_NAME_LBU,    ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_1_U, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_LHU    -> List(INST_NAME_LHU,    ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_2_U, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_LW     -> List(INST_NAME_LW,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_4_S, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_LWU    -> List(INST_NAME_LWU,    ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_4_U, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_LD     -> List(INST_NAME_LD,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_8_S, GPR_WR_TR, GPR_WR_SRC_MEM),

            INST_SB     -> List(INST_NAME_SB,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_S, JMP_FL, MEM_WR_TR, MEM_BYT_1_U, GPR_WR_FL, GPR_WR_SRC_X),
            INST_SH     -> List(INST_NAME_SH,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_S, JMP_FL, MEM_WR_TR, MEM_BYT_2_U, GPR_WR_FL, GPR_WR_SRC_X),
            INST_SW     -> List(INST_NAME_SW,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_S, JMP_FL, MEM_WR_TR, MEM_BYT_4_U, GPR_WR_FL, GPR_WR_SRC_X),
            INST_SD     -> List(INST_NAME_SD,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_S, JMP_FL, MEM_WR_TR, MEM_BYT_8_U, GPR_WR_FL, GPR_WR_SRC_X),

            INST_ECALL  -> List(INST_NAME_ECALL,  ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_EBREAK -> List(INST_NAME_EBREAK, ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),

            INST_MUL    -> List(INST_NAME_MUL,    ALU_TYPE_MUL,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_MULW   -> List(INST_NAME_MULW,   ALU_TYPE_MUL,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_DIVU   -> List(INST_NAME_DIVU,   ALU_TYPE_DIVU,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_DIVW   -> List(INST_NAME_DIVW,   ALU_TYPE_DIVW,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_DIVUW  -> List(INST_NAME_DIVUW,  ALU_TYPE_DIVUW, ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_REMU   -> List(INST_NAME_REMU,   ALU_TYPE_REMU,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_REMW   -> List(INST_NAME_REMW,   ALU_TYPE_REMW,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU)
        )
    )
    val wInstName = lInst(0)
    val wALUType  = lInst(1)
    val wALURS1   = lInst(2)
    val wALURS2   = lInst(3)
    val wJmpEn    = lInst(4)
    val wMemWrEn  = lInst(5)
    val wMemByt   = lInst(6)
    val wGPRWrEn  = lInst(7)
    val wGPRWrSrc = lInst(8)

    io.oInstName := wInstName
    io.oALUType  := wALUType
    io.oALURS1   := wALURS1
    io.oALURS2   := wALURS2
    io.oJmpEn    := wJmpEn
    io.oMemWrEn  := wMemWrEn
    io.oMemByt   := wMemByt
    io.oGPRWrEn  := wGPRWrEn
    io.oGPRWrSrc := wGPRWrSrc

    io.oGPRRS1Addr := wInst(19, 15)
    io.oGPRRS2Addr := wInst(24, 20)
    io.oGPRRdAddr  := wInst(11,  7)
    io.oALURS1Data := MuxLookup(wALURS1, DATA_ZERO)(
        Seq(
           ALU_RS1_X   -> DATA_ZERO,
           ALU_RS1_GPR -> io.iGPRRS1Data,
           ALU_RS1_PC  -> io.iPC
        )
    )
    io.oALURS2Data := MuxLookup(wALURS2, DATA_ZERO)(
        Seq(
            ALU_RS2_X     -> DATA_ZERO,
            ALU_RS2_GPR   -> io.iGPRRS2Data,
            ALU_RS2_CSR   -> DATA_ZERO,
            ALU_RS2_IMM_I -> ExtenImm(wInst, "immI"),
            ALU_RS2_IMM_S -> ExtenImm(wInst, "immS"),
            ALU_RS2_IMM_U -> ExtenImm(wInst, "immU"),
            ALU_RS2_IMM_J -> ExtenImm(wInst, "immJ"),
        )
    )
    io.oJmpOrWrData := Mux(
        (wInstName === INST_NAME_BEQ)  ||
        (wInstName === INST_NAME_BNE)  ||
        (wInstName === INST_NAME_BLT)  ||
        (wInstName === INST_NAME_BGE)  ||
        (wInstName === INST_NAME_BLTU) ||
        (wInstName === INST_NAME_BGEU),
        ExtenImm(wInst, "immB"),
        io.iGPRRS2Data
    )
}
