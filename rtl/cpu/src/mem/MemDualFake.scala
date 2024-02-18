package cpu.mem

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class MemDualFake(val cTimeType: String) extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pInst = new MemDualInstIO
        val pData = new MemDualDataIO
    })

    val mMem = cTimeType match {
        case "async" => Mem(MEMS_NUM,
                            Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
        case "sync"  => SyncReadMem(MEMS_NUM,
                                    Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
    }

    val wRdInst = Wire(Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
    val wRdData = Wire(Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))

    wRdInst := (mMem match {
        case asyncMem: Mem[_]         => asyncMem.read(io.pInst.pRd.bAddr)
        case syncMem:  SyncReadMem[_] => syncMem.read(io.pInst.pRd.bAddr,
                                                      io.pInst.pRd.bEn)
    })
    wRdData := (mMem match {
        case asyncMem: Mem[_]         => asyncMem.read(io.pData.pRd.bAddr)
        case syncMem:  SyncReadMem[_] => syncMem.read(io.pData.pRd.bAddr,
                                                      io.pData.pRd.bEn)
    })

    io.pInst.pRd.bData := wRdInst.reverse.foldLeft(0.U(BYTE_WIDTH.W)) {
        (sum, nxt) => Cat(nxt, sum)
    }
    io.pData.pRd.bData := wRdData.reverse.foldLeft(0.U(BYTE_WIDTH.W)) {
        (sum, nxt) => Cat(nxt, sum)
    }

    val wWrData = Wire(Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
    for (i <- 0 until MASK_WIDTH) {
        wWrData(i) := io.pData.pWr.bData(BYTE_WIDTH * i + (BYTE_WIDTH - 1),
                                         BYTE_WIDTH * i)
    }

    when (io.pData.pWr.bEn) {
        mMem.write(io.pData.pWr.bAddr, wWrData, io.pData.pWr.bMask)
    }
    .otherwise {
        mMem.write(io.pData.pWr.bAddr,
                   mMem.read(io.pData.pWr.bAddr),
                   io.pData.pWr.bMask)
    }
}
