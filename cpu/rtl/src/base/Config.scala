package cpu.base

import chisel3._
import chisel3.util._

trait ConfigIO {
    val RESP_WIDTH = 2
    val MODE_WIDTH = 2
    val GPRS_WIDTH = 5
    val CSRS_WIDTH = 12
    val BYTE_WIDTH = 8
    val SIGS_WIDTH = 10
    val INST_WIDTH = 32
    val ADDR_WIDTH = 32
    val DATA_WIDTH = 32
    val MASK_WIDTH = DATA_WIDTH / BYTE_WIDTH

    val GPRS_NUM = 1 << GPRS_WIDTH
    val CSRS_NUM = 1 << CSRS_WIDTH
    val MEMS_NUM = 1 << 16

    val AXSM_WIDTH = 2

    val AXBT_WIDTH = 2
    val AXSZ_WIDTH = 3
    val AXID_WIDTH = 4
    val AXCH_WIDTH = 4
    val AXPR_WIDTH = 4
    val AXQS_WIDTH = 4
    val AXRE_WIDTH = 4
    val AXUS_WIDTH = 4
    val AXLN_WIDTH = 8

    val CSR_TYPE = 2
}

trait ConfigInst extends ConfigIO {
    val ADDR_INIT = "x80000000".U(ADDR_WIDTH.W)
    val INST_ZERO = "x00000000".U(INST_WIDTH.W)
    val ADDR_ZERO = "x00000000".U(ADDR_WIDTH.W)
    val DATA_ZERO = "x00000000".U(DATA_WIDTH.W)
    val MASK_ZERO = "x00000000".U(MASK_WIDTH.W)

    val GPR_ZERO         =     0.U(GPRS_WIDTH.W)
    val GPR_END          =    10.U(GPRS_WIDTH.W)
    val CSR_MSTATUS      = 0x300.U(CSRS_WIDTH.W)
    val CSR_MTVEC        = 0x305.U(CSRS_WIDTH.W)
    val CSR_MEPC         = 0x341.U(CSRS_WIDTH.W)
    val CSR_MCAUSE       = 0x342.U(CSRS_WIDTH.W)
    val CSR_CODE_M_ECALL =    11.U
    val CSR_MSTATUS_INIT = "x00001800".U(ADDR_WIDTH.W)

    val CSR_TYPE_X     = 0.U(CSR_TYPE.W)
    val CSR_TYPE_ECALL = 1.U(CSR_TYPE.W)
    val CSR_TYPE_MRET  = 2.U(CSR_TYPE.W)

    val EN_TR = true.B
    val EN_FL = false.B

    val STATE_X  = 0.U(SIGS_WIDTH.W)
    val STATE_IF = 1.U(SIGS_WIDTH.W)
    val STATE_ID = 2.U(SIGS_WIDTH.W)
    val STATE_EX = 3.U(SIGS_WIDTH.W)
    val STATE_LS = 4.U(SIGS_WIDTH.W)
    val STATE_WB = 5.U(SIGS_WIDTH.W)

    val INST_NAME_X      =  0.U(SIGS_WIDTH.W)
    val INST_NAME_SLL    =  1.U(SIGS_WIDTH.W)
    val INST_NAME_SLLI   =  2.U(SIGS_WIDTH.W)
    val INST_NAME_SRL    =  3.U(SIGS_WIDTH.W)
    val INST_NAME_SRLI   =  4.U(SIGS_WIDTH.W)
    val INST_NAME_SRA    =  5.U(SIGS_WIDTH.W)
    val INST_NAME_SRAI   =  6.U(SIGS_WIDTH.W)
    val INST_NAME_ADD    =  7.U(SIGS_WIDTH.W)
    val INST_NAME_ADDI   =  8.U(SIGS_WIDTH.W)
    val INST_NAME_SUB    =  9.U(SIGS_WIDTH.W)
    val INST_NAME_LUI    = 10.U(SIGS_WIDTH.W)
    val INST_NAME_AUIPC  = 11.U(SIGS_WIDTH.W)
    val INST_NAME_XOR    = 12.U(SIGS_WIDTH.W)
    val INST_NAME_XORI   = 13.U(SIGS_WIDTH.W)
    val INST_NAME_OR     = 14.U(SIGS_WIDTH.W)
    val INST_NAME_ORI    = 15.U(SIGS_WIDTH.W)
    val INST_NAME_AND    = 16.U(SIGS_WIDTH.W)
    val INST_NAME_ANDI   = 17.U(SIGS_WIDTH.W)
    val INST_NAME_SLT    = 18.U(SIGS_WIDTH.W)
    val INST_NAME_SLTI   = 19.U(SIGS_WIDTH.W)
    val INST_NAME_SLTU   = 20.U(SIGS_WIDTH.W)
    val INST_NAME_SLTIU  = 21.U(SIGS_WIDTH.W)
    val INST_NAME_BEQ    = 22.U(SIGS_WIDTH.W)
    val INST_NAME_BNE    = 23.U(SIGS_WIDTH.W)
    val INST_NAME_BLT    = 24.U(SIGS_WIDTH.W)
    val INST_NAME_BGE    = 25.U(SIGS_WIDTH.W)
    val INST_NAME_BLTU   = 26.U(SIGS_WIDTH.W)
    val INST_NAME_BGEU   = 27.U(SIGS_WIDTH.W)
    val INST_NAME_JAL    = 28.U(SIGS_WIDTH.W)
    val INST_NAME_JALR   = 29.U(SIGS_WIDTH.W)
    val INST_NAME_FENCE  = 30.U(SIGS_WIDTH.W)
    val INST_NAME_FENCEI = 31.U(SIGS_WIDTH.W)
    val INST_NAME_ECALL  = 32.U(SIGS_WIDTH.W)
    val INST_NAME_EBREAK = 33.U(SIGS_WIDTH.W)
    val INST_NAME_CSRRW  = 34.U(SIGS_WIDTH.W)
    val INST_NAME_CSRRS  = 35.U(SIGS_WIDTH.W)
    val INST_NAME_CSRRC  = 36.U(SIGS_WIDTH.W)
    val INST_NAME_CSRRWI = 37.U(SIGS_WIDTH.W)
    val INST_NAME_CSRRSI = 38.U(SIGS_WIDTH.W)
    val INST_NAME_CSRRCI = 39.U(SIGS_WIDTH.W)
    val INST_NAME_LB     = 40.U(SIGS_WIDTH.W)
    val INST_NAME_LH     = 41.U(SIGS_WIDTH.W)
    val INST_NAME_LBU    = 42.U(SIGS_WIDTH.W)
    val INST_NAME_LHU    = 43.U(SIGS_WIDTH.W)
    val INST_NAME_LW     = 44.U(SIGS_WIDTH.W)
    val INST_NAME_SB     = 45.U(SIGS_WIDTH.W)
    val INST_NAME_SH     = 46.U(SIGS_WIDTH.W)
    val INST_NAME_SW     = 47.U(SIGS_WIDTH.W)
    val INST_NAME_MUL    = 48.U(SIGS_WIDTH.W)
    val INST_NAME_MULH   = 49.U(SIGS_WIDTH.W)
    val INST_NAME_MULHSU = 50.U(SIGS_WIDTH.W)
    val INST_NAME_MULHU  = 51.U(SIGS_WIDTH.W)
    val INST_NAME_DIV    = 52.U(SIGS_WIDTH.W)
    val INST_NAME_DIVU   = 53.U(SIGS_WIDTH.W)
    val INST_NAME_REM    = 54.U(SIGS_WIDTH.W)
    val INST_NAME_REMU   = 55.U(SIGS_WIDTH.W)
    val INST_NAME_MRET   = 56.U(SIGS_WIDTH.W)

    val PC_WR_SRC_X    = 0.U(SIGS_WIDTH.W)
    val PC_WR_SRC_NEXT = 1.U(SIGS_WIDTH.W)
    val PC_WR_SRC_JUMP = 2.U(SIGS_WIDTH.W)

    val MEM_RD_SRC_X   = 0.U(SIGS_WIDTH.W)
    val MEM_RD_SRC_PC  = 1.U(SIGS_WIDTH.W)
    val MEM_RD_SRC_ALU = 2.U(SIGS_WIDTH.W)

    val MEM_BYT_X   = 0.U(SIGS_WIDTH.W)
    val MEM_BYT_1_U = 1.U(SIGS_WIDTH.W)
    val MEM_BYT_2_U = 2.U(SIGS_WIDTH.W)
    val MEM_BYT_4_U = 3.U(SIGS_WIDTH.W)
    val MEM_BYT_8_U = 4.U(SIGS_WIDTH.W)
    val MEM_BYT_1_S = 5.U(SIGS_WIDTH.W)
    val MEM_BYT_2_S = 6.U(SIGS_WIDTH.W)
    val MEM_BYT_4_S = 7.U(SIGS_WIDTH.W)
    val MEM_BYT_8_S = 8.U(SIGS_WIDTH.W)

    val REG_WR_SRC_X   = 0.U(SIGS_WIDTH.W)
    val REG_WR_SRC_ALU = 1.U(SIGS_WIDTH.W)
    val REG_WR_SRC_MEM = 2.U(SIGS_WIDTH.W)
    val REG_WR_SRC_PC  = 3.U(SIGS_WIDTH.W)
    val REG_WR_SRC_CSR = 4.U(SIGS_WIDTH.W)

    val ALU_TYPE_X      =  0.U(SIGS_WIDTH.W)
    val ALU_TYPE_SLL    =  1.U(SIGS_WIDTH.W)
    val ALU_TYPE_SRL    =  2.U(SIGS_WIDTH.W)
    val ALU_TYPE_SRA    =  3.U(SIGS_WIDTH.W)
    val ALU_TYPE_ADD    =  4.U(SIGS_WIDTH.W)
    val ALU_TYPE_SUB    =  5.U(SIGS_WIDTH.W)
    val ALU_TYPE_XOR    =  6.U(SIGS_WIDTH.W)
    val ALU_TYPE_OR     =  7.U(SIGS_WIDTH.W)
    val ALU_TYPE_AND    =  8.U(SIGS_WIDTH.W)
    val ALU_TYPE_SLT    =  9.U(SIGS_WIDTH.W)
    val ALU_TYPE_SLTU   = 10.U(SIGS_WIDTH.W)
    val ALU_TYPE_BEQ    = 11.U(SIGS_WIDTH.W)
    val ALU_TYPE_BNE    = 12.U(SIGS_WIDTH.W)
    val ALU_TYPE_BLT    = 13.U(SIGS_WIDTH.W)
    val ALU_TYPE_BGE    = 14.U(SIGS_WIDTH.W)
    val ALU_TYPE_BLTU   = 15.U(SIGS_WIDTH.W)
    val ALU_TYPE_BGEU   = 16.U(SIGS_WIDTH.W)
    val ALU_TYPE_JALR   = 17.U(SIGS_WIDTH.W)
    val ALU_TYPE_MUL    = 18.U(SIGS_WIDTH.W)
    val ALU_TYPE_MULH   = 19.U(SIGS_WIDTH.W)
    val ALU_TYPE_MULHSU = 20.U(SIGS_WIDTH.W)
    val ALU_TYPE_MULHU  = 21.U(SIGS_WIDTH.W)
    val ALU_TYPE_DIV    = 22.U(SIGS_WIDTH.W)
    val ALU_TYPE_DIVU   = 23.U(SIGS_WIDTH.W)
    val ALU_TYPE_REM    = 24.U(SIGS_WIDTH.W)
    val ALU_TYPE_REMU   = 25.U(SIGS_WIDTH.W)

    val ALU_RS1_X     = 0.U(SIGS_WIDTH.W)
    val ALU_RS1_PC    = 1.U(SIGS_WIDTH.W)
    val ALU_RS1_GPR   = 2.U(SIGS_WIDTH.W)
    val ALU_RS1_IMM   = 3.U(SIGS_WIDTH.W)
    val ALU_RS1_4     = 4.U(SIGS_WIDTH.W)

    val ALU_RS2_X     = 0.U(SIGS_WIDTH.W)
    val ALU_RS2_GPR   = 1.U(SIGS_WIDTH.W)
    val ALU_RS2_IMM_I = 2.U(SIGS_WIDTH.W)
    val ALU_RS2_IMM_S = 3.U(SIGS_WIDTH.W)
    val ALU_RS2_IMM_B = 4.U(SIGS_WIDTH.W)
    val ALU_RS2_IMM_U = 5.U(SIGS_WIDTH.W)
    val ALU_RS2_IMM_J = 6.U(SIGS_WIDTH.W)
    val ALU_RS2_4     = 7.U(SIGS_WIDTH.W)

    val JMP_TR = 1.U(SIGS_WIDTH.W)
    val JMP_FL = 0.U(SIGS_WIDTH.W)

    val MEM_WR_TR = 1.U(SIGS_WIDTH.W)
    val MEM_WR_FL = 0.U(SIGS_WIDTH.W)

    val REG_WR_TR = 1.U(SIGS_WIDTH.W)
    val REG_WR_FL = 0.U(SIGS_WIDTH.W)

    val CSR_WR_T = 1.U(SIGS_WIDTH.W)
    val CSR_WR_F = 0.U(SIGS_WIDTH.W)

    val AXI4_RESP_OKEY   = 0.U(RESP_WIDTH.W)
    val AXI4_RESP_EXOKAY = 1.U(RESP_WIDTH.W)
    val AXI4_RESP_SLVEER = 2.U(RESP_WIDTH.W)
    val AXI4_RESP_DECEER = 3.U(RESP_WIDTH.W)

    val AXI4_MODE_RD = 0.U(MODE_WIDTH.W)
    val AXI4_MODE_WR = 1.U(MODE_WIDTH.W)
    val AXI4_MODE_RW = 2.U(MODE_WIDTH.W)
}

trait ConfigInstRV32I extends ConfigInst {
    // Shifts
    val INST_SLL    = BitPat("b0000000_?????_?????_001_?????_0110011")
    val INST_SLLI   = BitPat("b000000?_?????_?????_001_?????_0010011")
    val INST_SRL    = BitPat("b0000000_?????_?????_101_?????_0110011")
    val INST_SRLI   = BitPat("b000000?_?????_?????_101_?????_0010011")
    val INST_SRA    = BitPat("b0100000_?????_?????_101_?????_0110011")
    val INST_SRAI   = BitPat("b010000?_?????_?????_101_?????_0010011")
    // Arithmetic
    val INST_ADD    = BitPat("b0000000_?????_?????_000_?????_0110011")
    val INST_ADDI   = BitPat("b???????_?????_?????_000_?????_0010011")
    val INST_SUB    = BitPat("b0100000_?????_?????_000_?????_0110011")
    val INST_LUI    = BitPat("b???????_?????_?????_???_?????_0110111")
    val INST_AUIPC  = BitPat("b???????_?????_?????_???_?????_0010111")
        // Logical
    val INST_XOR    = BitPat("b0000000_?????_?????_100 ?????_0110011")
    val INST_XORI   = BitPat("b???????_?????_?????_100_?????_0010011")
    val INST_OR     = BitPat("b0000000_?????_?????_110_?????_0110011")
    val INST_ORI    = BitPat("b???????_?????_?????_110_?????_0010011")
    val INST_AND    = BitPat("b0000000_?????_?????_111_?????_0110011")
    val INST_ANDI   = BitPat("b???????_?????_?????_111_?????_0010011")
    // Compare
    val INST_SLT    = BitPat("b0000000_?????_?????_010_?????_0110011")
    val INST_SLTI   = BitPat("b???????_?????_?????_010_?????_0010011")
    val INST_SLTU   = BitPat("b0000000_?????_?????_011_?????_0110011")
    val INST_SLTIU  = BitPat("b???????_?????_?????_011_?????_0010011")
    // Branches
    val INST_BEQ    = BitPat("b???????_?????_?????_000_?????_1100011")
    val INST_BNE    = BitPat("b???????_?????_?????_001_?????_1100011")
    val INST_BLT    = BitPat("b???????_?????_?????_100_?????_1100011")
    val INST_BGE    = BitPat("b???????_?????_?????_101_?????_1100011")
    val INST_BLTU   = BitPat("b???????_?????_?????_110_?????_1100011")
    val INST_BGEU   = BitPat("b???????_?????_?????_111_?????_1100011")
    // Jump & Link
    val INST_JAL    = BitPat("b???????_?????_?????_???_?????_1101111")
    val INST_JALR   = BitPat("b???????_?????_?????_000_?????_1100111")
    // Synch
    val INST_FENCE  = BitPat("b0000_????????_00000_000_00000_0001111")
    val INST_FENCEI = BitPat("b0000000_00000_00000_001_00000_0001111")
    // Environment
    val INST_ECALL  = BitPat("b0000000_00000_00000_000_00000_1110011")
    val INST_EBREAK = BitPat("b0000000_00001_00000_000_00000_1110011")
    // CSR
    val INST_CSRRW  = BitPat("b???????_?????_?????_001_?????_1110011")
    val INST_CSRRS  = BitPat("b???????_?????_?????_010_?????_1110011")
    val INST_CSRRC  = BitPat("b???????_?????_?????_011_?????_1110011")
    val INST_CSRRWI = BitPat("b???????_?????_?????_101_?????_1110011")
    val INST_CSRRSI = BitPat("b???????_?????_?????_110_?????_1110011")
    val INST_CSRRCI = BitPat("b???????_?????_?????_111_?????_1110011")
    // Loads
    val INST_LB     = BitPat("b???????_?????_?????_000_?????_0000011")
    val INST_LH     = BitPat("b???????_?????_?????_001_?????_0000011")
    val INST_LBU    = BitPat("b???????_?????_?????_100_?????_0000011")
    val INST_LHU    = BitPat("b???????_?????_?????_101_?????_0000011")
    val INST_LW     = BitPat("b???????_?????_?????_010_?????_0000011")
    // Stores
    val INST_SB     = BitPat("b???????_?????_?????_000_?????_0100011")
    val INST_SH     = BitPat("b???????_?????_?????_001_?????_0100011")
    val INST_SW     = BitPat("b???????_?????_?????_010_?????_0100011")
}

trait ConfigInstRV64I extends ConfigInstRV32I {
    // Shifts
    val INST_SLLW   = BitPat("b0000000_?????_?????_001_?????_0111011")
    val INST_SLLIW  = BitPat("b0000000_?????_?????_001_?????_0011011")
    val INST_SRLW   = BitPat("b0000000_?????_?????_101_?????_0111011")
    val INST_SRLIW  = BitPat("b0000000_?????_?????_101_?????_0011011")
    val INST_SRAW   = BitPat("b0100000_?????_?????_101_?????_0111011")
    val INST_SRAIW  = BitPat("b0100000_?????_?????_101_?????_0011011")
    // Arithmetic
    val INST_ADDW   = BitPat("b0000000_?????_?????_000_?????_0111011")
    val INST_ADDIW  = BitPat("b???????_?????_?????_000_?????_0011011")
    val INST_SUBW   = BitPat("b0100000_?????_?????_000_?????_0111011")
    // Loads
    val INST_LWU    = BitPat("b???????_?????_?????_110_?????_0000011")
    val INST_LD     = BitPat("b???????_?????_?????_011_?????_0000011")
    // Stores
    val INST_SD     = BitPat("b???????_?????_?????_011_?????_0100011")
}

trait ConfigInstRV32M extends ConfigInst {
    // Multiply
    val INST_MUL    = BitPat("b0000001_?????_?????_000_?????_0110011")
    val INST_MULH   = BitPat("b0000001_?????_?????_001_?????_0110011")
    val INST_MULHSU = BitPat("b0000001_?????_?????_010_?????_0110011")
    val INST_MULHU  = BitPat("b0000001_?????_?????_011_?????_0110011")
    // Divide
    val INST_DIV    = BitPat("b0000001_?????_?????_100_?????_0110011")
    val INST_DIVU   = BitPat("b0000001_?????_?????_101_?????_0110011")
    // Remainder
    val INST_REM    = BitPat("b0000001_?????_?????_110_?????_0110011")
    val INST_REMU   = BitPat("b0000001_?????_?????_111_?????_0110011")
}

trait ConfigInstRV64M extends ConfigInstRV32M {
    // Multiply
    val INST_MULW   = BitPat("b0000001_?????_?????_000_?????_0111011")
    // Divide
    val INST_DIVW   = BitPat("b0000001_?????_?????_100_?????_0111011")
    val INST_DIVUW  = BitPat("b0000001_?????_?????_101_?????_0111011")
    // Remainder
    val INST_REMW   = BitPat("b0000001_?????_?????_110_?????_0111011")
    val INST_REMUW  = BitPat("b0000001_?????_?????_111_?????_0111011")
}

trait ConfigInstRVPri extends ConfigInst {
    val INST_MRET   = BitPat("b0011000_00010_00000_000_00000_1110011")
}