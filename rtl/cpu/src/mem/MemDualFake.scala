package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class MemDualFake(val cTimeType: String) extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRdEn   = Input(Bool())
        val iRdAddr = Input(UInt(ADDR_WIDTH.W))

        val iWrEn   = Input(Bool())
        val iWrAddr = Input(UInt(ADDR_WIDTH.W))
        val iWrData = Input(UInt(DATA_WIDTH.W))
        val iWrMask = Input(UInt(BYTE_WIDTH.W))

        val oRdData = Output(UInt(DATA_WIDTH.W))
    })

    val mMem = Mem(MEMS_NUM, UInt(DATA_WIDTH.W))
    if (cTimeType.equals("async")) {
        mMem = Mem(MEMS_NUM, UInt(DATA_WIDTH.W))
    }
    else if (cTimeType.equals("sync")) {
        mMem = SyncReadMem(MEMS_NUM, UInt(DATA_WIDTH.W))
    }

    when (io.iRdEn) {
        io.oRdData := mMem(io.iRdAddr)
    }
    .otherwise {
        io.oRdData := io.oRdData
    }

    when (io.iWrEn) {
        mMem(io.iWrAddr) := io.iWrData
    }
    .otherwise {
        mMem(io.iWrAddr) := mMem(io.iWrAddr)
    }
}
