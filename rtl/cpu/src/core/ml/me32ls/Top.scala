package cpu.core.ml.me32ls

import chisel3._
import chisel3.util._

import cpu.mem._

class Top extends Module {
    val io = IO(new Bundle {
        val a = Input(UInt(32.W))
        val b = Input(UInt(32.W))
        val c = Output(UInt(32.W))
    });

    val mMemDualFake = Module(new MemDualFake("sync"))
    mMemDualFake.io.pMem.iRdEn := true.B
    mMemDualFake.io.pMem.iWrEn := true.B

    mMemDualFake.io.pMem.iRdAddr := 0.U
    mMemDualFake.io.pMem.iWrAddr := 0.U
    mMemDualFake.io.pMem.iWrData := 0.U

    val maskInt = "b1110".U
    val maskVec = VecInit(maskInt.asBools)
    mMemDualFake.io.pMem.iWrMask := maskVec

    val test = WireInit(UInt(32.W), 0.U)
    test := mMemDualFake.io.pMem.oRdData

    io.c := 1.U + test
}
