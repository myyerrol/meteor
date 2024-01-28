package cpu.mem

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class MemDualFake(val cTimeType: String) extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pMem = new MemDualIO
    })

    val mMem = cTimeType match {
        case "async" => Mem(MEMS_NUM,
                            Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
        case "sync"  => SyncReadMem(MEMS_NUM,
                                    Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
    }

    val wRdData = Wire(Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))

    wRdData := (mMem match {
        case asyncMem: Mem[_]         => asyncMem.read(io.pMem.iRdAddr)
        case syncMem:  SyncReadMem[_] => syncMem.read(io.pMem.iRdAddr,
                                                      io.pMem.iRdEn)
    })

    io.pMem.oRdData := wRdData.reverse.foldLeft(0.U(BYTE_WIDTH.W)) {
        (sum, nxt) => Cat(nxt, sum)
    }

    val wWrData = Wire(Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
    for (i <- 0 until MASK_WIDTH) {
        wWrData(i) := io.pMem.iWrData(BYTE_WIDTH * i + (BYTE_WIDTH - 1),
                                 BYTE_WIDTH * i)
    }

    when (io.pMem.iWrEn) {
        mMem.write(io.pMem.iWrAddr, wWrData, io.pMem.iWrMask)
    }
    .otherwise {
        mMem.write(io.pMem.iWrAddr,
                   mMem.read(io.pMem.iWrAddr),
                   io.pMem.iWrMask)
    }
}
