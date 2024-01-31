package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class EXUJmpIO extends Bundle with ConfigIO {
    val bJmpEn = Output(Bool())
    val bJmpPC = Output(UInt(ADDR_WIDTH.W))
}
