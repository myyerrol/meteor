package cpu.core.ml2

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class IFU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iInstName = Input(UInt(SIGS_WIDTH.W))
        val iPCWrEn   = Input(Bool())
        val iPCWrSrc  = Input(UInt(SIGS_WIDTH.W))
        val iIRWrEn   = Input(Bool())

        val iPCNext   = Input(UInt(ADDR_WIDTH.W))
        val iPCJump   = Input(UInt(ADDR_WIDTH.W))
        val iALUZero  = Input(Bool())
        val iInst     = Input(UInt(INST_WIDTH.W))

        val oPC   = Output(UInt(ADDR_WIDTH.W))
        val oInst = Output(UInt(INST_WIDTH.W))
    })

    val rPC  = RegInit(ADDR_INIT)
    val wPCNext = WireInit(ADDR_INIT)

    when (io.iPCWrSrc === PC_WR_SRC_NEXT) {
        wPCNext := io.iPCNext
    }
    .elsewhen (io.iPCWrSrc === PC_WR_SRC_JUMP) {
        when (io.iInstName === INST_NAME_BEQ  ||
              io.iInstName === INST_NAME_BNE  ||
              io.iInstName === INST_NAME_BLT  ||
              io.iInstName === INST_NAME_BGE  ||
              io.iInstName === INST_NAME_BLTU ||
              io.iInstName === INST_NAME_BGEU) {
            wPCNext := Mux(io.iALUZero === 1.U, io.iPCJump, io.iPCNext)
        }
        .otherwise {
            wPCNext := io.iPCJump
        }
    }

    when (io.iPCWrEn) {
        rPC    := wPCNext
        io.oPC := rPC
    }
    .otherwise {
        io.oPC := rPC
    }

    val mIRU = Module(new IRU)
    mIRU.io.iEn   := io.iIRWrEn
    mIRU.io.iData := io.iInst

    io.oInst := mIRU.io.oData
}
