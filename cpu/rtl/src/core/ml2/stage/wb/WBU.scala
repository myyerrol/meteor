package cpu.core.ml2

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port.ml2._

class WBU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iInstName = Input(UInt(SIGS_WIDTH.W))
        val iMemByt   = Input(UInt(SIGS_WIDTH.W))
        val iGPRWrSrc = Input(UInt(SIGS_WIDTH.W))
        val iALUOut   = Input(UInt(DATA_WIDTH.W))
        val iMemData  = Input(UInt(DATA_WIDTH.W))

        val pWBU      = new WBUIO
    })

    when (io.iGPRWrSrc ===REG_WR_SRC_MEM) {
        val wMemData     = io.iMemData
        val wMemDataByt1 = wMemData(BYTE_WIDTH * 1 - 1, 0)
        val wMemDataByt2 = wMemData(BYTE_WIDTH * 2 - 1, 0)
        val wMemDataByt4 = wMemData(BYTE_WIDTH * 4 - 1, 0)
        val wMemDataMux  = MuxLookup(io.iMemByt, DATA_ZERO) (
            Seq(
                MEM_BYT_1_S ->
                    ExtenSign(wMemDataByt1, DATA_WIDTH),
                MEM_BYT_1_U ->
                    ExtenZero(wMemDataByt1, DATA_WIDTH),
                MEM_BYT_2_S ->
                    ExtenSign(wMemDataByt2, DATA_WIDTH),
                MEM_BYT_2_U ->
                    ExtenZero(wMemDataByt2, DATA_WIDTH),
                MEM_BYT_4_S ->
                    ExtenSign(wMemDataByt4, DATA_WIDTH),
                MEM_BYT_4_U ->
                    ExtenZero(wMemDataByt4, DATA_WIDTH)
            )
        )
        io.pWBU.oGPRWrData := wMemDataMux
    }
    .elsewhen (io.iGPRWrSrc === REG_WR_SRC_ALU) {
        io.pWBU.oGPRWrData := io.iALUOut
    }
    .otherwise {
        io.pWBU.oGPRWrData := DATA_ZERO
    }
}
