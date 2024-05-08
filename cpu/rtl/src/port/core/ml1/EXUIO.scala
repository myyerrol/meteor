package cpu.port.ml1

import chisel3._
import chisel3.util._

import cpu.base._

class EXUJmpIO extends Bundle with ConfigIO {
    val bJmpEn = Output(Bool())
    val bJmpPC = Output(UInt(ADDR_WIDTH.W))
}

class  EXUOutIO extends Bundle with ConfigIO {
    val bALUOut = Output(UInt(DATA_WIDTH.W))
}
