package cpu.port.ml2

import chisel3._
import chisel3.util._

import cpu.base._

class IFUIO extends Bundle with ConfigIO {
    val oPC     = Output(UInt(ADDR_WIDTH.W))
    val oPCNext = Output(UInt(ADDR_WIDTH.W))
    val oInst   = Output(UInt(INST_WIDTH.W))
}
