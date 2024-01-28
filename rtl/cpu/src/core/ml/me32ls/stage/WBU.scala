package cpu.core.ml.me32ls.stage

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class WBU extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iGPRWrEn   =  Input(Bool())
        val iGPRWrAddr =  Input(UInt(ADDR_WIDTH.W))
        val iGPRWrData =  Input(UInt(DATA_WIDTH.W))

        val oGPRWrEn   = Output(Bool())
        val oGPRWrAddr = Output(UInt(ADDR_WIDTH.W))
        val oGPRWrData = Output(UInt(DATA_WIDTH.W))
    })

    io.oGPRWrEn   := io.iGPRWrEn
    io.oGPRWrAddr := io.iGPRWrAddr
    io.oGPRWrData := io.iGPRWrData
}
