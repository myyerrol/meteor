package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class IDU extends Module with ConfigInstRV32I with ConfigInstRV32M {
    val io = IO(new Bundle {
        val pBase    = Flipped(new BaseIO)
        val pGPRRS   = Flipped(new GPRRSIO)
        val pIDUCtr  =         new IDUCtrIO
        val pIDUData =         new IDUDataIO
    })

    val wInst = io.pBase.bInst;
    var lInst = ListLookup(
        wInst,
        List(INST_NAME_X, ALU_TYPE_X, ALU_RS1_X, ALU_RS2_X, JMP_FL, MEM_WR_FL, MEM_BYT_X, GPR_WR_FL, GPR_WR_SRC_X),
        Array(
            INST_SLL    -> List(INST_NAME_SLL,    ALU_TYPE_SLL,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SLLI   -> List(INST_NAME_SLLI,   ALU_TYPE_SLL,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRL    -> List(INST_NAME_SRL,    ALU_TYPE_SRL,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRLI   -> List(INST_NAME_SRLI,   ALU_TYPE_SRL,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRA    -> List(INST_NAME_SRA,    ALU_TYPE_SRA,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SRAI   -> List(INST_NAME_SRAI,   ALU_TYPE_SRA,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_ADD    -> List(INST_NAME_ADD,    ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_ADDI   -> List(INST_NAME_ADDI,   ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SUB    -> List(INST_NAME_SUB,    ALU_TYPE_SUB,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_LUI    -> List(INST_NAME_LUI,    ALU_TYPE_ADD,   ALU_RS1_X,    ALU_RS2_IMM_U, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_AUIPC  -> List(INST_NAME_AUIPC,  ALU_TYPE_ADD,   ALU_RS1_PC,   ALU_RS2_IMM_U, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_XOR    -> List(INST_NAME_XOR,    ALU_TYPE_XOR,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_XORI   -> List(INST_NAME_XORI,   ALU_TYPE_XOR,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_OR     -> List(INST_NAME_OR,     ALU_TYPE_OR,    ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_ORI    -> List(INST_NAME_ORI,    ALU_TYPE_OR,    ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_AND    -> List(INST_NAME_AND,    ALU_TYPE_AND,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_ANDI   -> List(INST_NAME_ANDI,   ALU_TYPE_AND,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SLT    -> List(INST_NAME_SLT,    ALU_TYPE_SLT,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_SLTI   -> List(INST_NAME_SLTI,   ALU_TYPE_SLT,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
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
            INST_FENCE  -> List(INST_NAME_FENCE,  ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_FENCEI -> List(INST_NAME_FENCEI, ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_ECALL  -> List(INST_NAME_ECALL,  ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_EBREAK -> List(INST_NAME_EBREAK, ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_CSRRW  -> List(INST_NAME_CSRRW,  ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_CSRRS  -> List(INST_NAME_CSRRS,  ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_CSRRC  -> List(INST_NAME_CSRRC,  ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_CSRRWI -> List(INST_NAME_CSRRWI, ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_CSRRSI -> List(INST_NAME_CSRRSI, ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_CSRRCI -> List(INST_NAME_CSRRCI, ALU_TYPE_X,     ALU_RS1_X,    ALU_RS2_X,     JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_FL, GPR_WR_SRC_X),
            INST_LB     -> List(INST_NAME_LB,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_1_S, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_LH     -> List(INST_NAME_LH,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_2_S, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_LBU    -> List(INST_NAME_LBU,    ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_1_U, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_LHU    -> List(INST_NAME_LHU,    ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_2_U, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_LW     -> List(INST_NAME_LW,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_I, JMP_FL, MEM_WR_FL, MEM_BYT_4_S, GPR_WR_TR, GPR_WR_SRC_MEM),
            INST_SB     -> List(INST_NAME_SB,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_S, JMP_FL, MEM_WR_TR, MEM_BYT_1_U, GPR_WR_FL, GPR_WR_SRC_X),
            INST_SH     -> List(INST_NAME_SH,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_S, JMP_FL, MEM_WR_TR, MEM_BYT_2_U, GPR_WR_FL, GPR_WR_SRC_X),
            INST_SW     -> List(INST_NAME_SW,     ALU_TYPE_ADD,   ALU_RS1_GPR,  ALU_RS2_IMM_S, JMP_FL, MEM_WR_TR, MEM_BYT_4_U, GPR_WR_FL, GPR_WR_SRC_X),
            INST_MUL    -> List(INST_NAME_MUL,    ALU_TYPE_MUL,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_MULH   -> List(INST_NAME_MULH,   ALU_TYPE_MUL,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_MULHSU -> List(INST_NAME_MULHSU, ALU_TYPE_MULSU, ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_MULHU  -> List(INST_NAME_MULHU,  ALU_TYPE_MULU,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_DIV    -> List(INST_NAME_DIV,    ALU_TYPE_DIV,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_DIVU   -> List(INST_NAME_DIVU,   ALU_TYPE_DIVU,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_REM    -> List(INST_NAME_REM,    ALU_TYPE_REM,   ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU),
            INST_REMU   -> List(INST_NAME_REMU,   ALU_TYPE_REMU,  ALU_RS1_GPR,  ALU_RS2_GPR,   JMP_FL, MEM_WR_FL, MEM_BYT_X,   GPR_WR_TR, GPR_WR_SRC_ALU)
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

    io.pGPRRS.bRS1Addr := wInst(19, 15)
    io.pGPRRS.bRS2Addr := wInst(24, 20)

    io.pIDUData.bGPRRdAddr  := wInst(11, 7)
    io.pIDUData.bALURS1Data := MuxLookup(wALURS1, DATA_ZERO)(
        Seq(
           ALU_RS1_X   -> DATA_ZERO,
           ALU_RS1_GPR -> io.pGPRRS.bRS1Data,
           ALU_RS1_PC  -> io.pBase.bPC
        )
    )
    io.pIDUData.bALURS2Data := MuxLookup(wALURS2, DATA_ZERO)(
        Seq(
            ALU_RS2_X     -> DATA_ZERO,
            ALU_RS2_GPR   -> io.pGPRRS.bRS2Data,
            ALU_RS2_CSR   -> DATA_ZERO,
            ALU_RS2_IMM_I -> ExtenImm(wInst, "immI"),
            ALU_RS2_IMM_S -> ExtenImm(wInst, "immS"),
            ALU_RS2_IMM_U -> ExtenImm(wInst, "immU"),
            ALU_RS2_IMM_J -> ExtenImm(wInst, "immJ"),
        )
    )
    io.pIDUData.bJmpOrWrData := Mux(
        (wInstName === INST_NAME_BEQ)  ||
        (wInstName === INST_NAME_BNE)  ||
        (wInstName === INST_NAME_BLT)  ||
        (wInstName === INST_NAME_BGE)  ||
        (wInstName === INST_NAME_BLTU) ||
        (wInstName === INST_NAME_BGEU),
        ExtenImm(wInst, "immB"),
        io.pGPRRS.bRS2Data
    )

    io.pIDUCtr.bInstName := wInstName
    io.pIDUCtr.bALUType  := wALUType
    io.pIDUCtr.bALURS1   := wALURS1
    io.pIDUCtr.bALURS2   := wALURS2
    io.pIDUCtr.bJmpEn    := wJmpEn
    io.pIDUCtr.bMemWrEn  := wMemWrEn
    io.pIDUCtr.bMemByt   := wMemByt
    io.pIDUCtr.bGPRWrEn  := wGPRWrEn
    io.pIDUCtr.bGPRWrSrc := wGPRWrSrc
}
