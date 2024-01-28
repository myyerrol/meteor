package cpu.mem

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
        val iWrMask = Input(Vec(MASK_WIDTH, Bool()))

        val oRdData = Output(UInt(DATA_WIDTH.W))
    })

    val mMem = cTimeType match {
        case "async" => Mem(MEMS_NUM,
                            Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
        case "sync"  => SyncReadMem(MEMS_NUM,
                                    Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
    }

    val wRdData = Wire(Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))

    wRdData := (mMem match {
        case asyncMem: Mem[_]         => asyncMem.read(io.iRdAddr)
        case syncMem:  SyncReadMem[_] => syncMem.read(io.iRdAddr, io.iRdEn)
    })

    io.oRdData := wRdData.reverse.foldLeft(0.U(BYTE_WIDTH.W)) {
        (sum, nxt) => Cat(nxt, sum)
    }

    val wWrData = Wire(Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
    for (i <- 0 until MASK_WIDTH) {
        wWrData(i) := io.iWrData(BYTE_WIDTH * i + (BYTE_WIDTH - 1),
                                 BYTE_WIDTH * i)
    }

    when (io.iWrEn) {
        mMem.write(io.iWrAddr, wWrData, io.iWrMask)
    }
    .otherwise {
        mMem.write(io.iWrAddr, mMem.read(io.iWrAddr), io.iWrMask)
    }
}
