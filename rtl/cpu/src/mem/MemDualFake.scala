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

    val wRdDataA = Wire(Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
    val wRdDataB = Wire(Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))

    wRdDataA := (mMem match {
        case asyncMem: Mem[_]         => asyncMem.read(io.pMem.bRdAddrA)
        case syncMem:  SyncReadMem[_] => syncMem.read(io.pMem.bRdAddrA,
                                                      io.pMem.bRdEn)
    })
    wRdDataB := (mMem match {
        case asyncMem: Mem[_]         => asyncMem.read(io.pMem.bRdAddrB)
        case syncMem:  SyncReadMem[_] => syncMem.read(io.pMem.bRdAddrB,
                                                      io.pMem.bRdEn)
    })

    io.pMem.bRdDataA := wRdDataA.reverse.foldLeft(0.U(BYTE_WIDTH.W)) {
        (sum, nxt) => Cat(nxt, sum)
    }
    io.pMem.bRdDataB := wRdDataB.reverse.foldLeft(0.U(BYTE_WIDTH.W)) {
        (sum, nxt) => Cat(nxt, sum)
    }

    val wWrData = Wire(Vec(MASK_WIDTH, UInt(BYTE_WIDTH.W)))
    for (i <- 0 until MASK_WIDTH) {
        wWrData(i) := io.pMem.bWrData(BYTE_WIDTH * i + (BYTE_WIDTH - 1),
                                 BYTE_WIDTH * i)
    }

    when (io.pMem.bWrEn) {
        mMem.write(io.pMem.bWrAddr, wWrData, io.pMem.bWrMask)
    }
    .otherwise {
        mMem.write(io.pMem.bWrAddr, mMem.read(io.pMem.bWrAddr), io.pMem.bWrMask)
    }
}
