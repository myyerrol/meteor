package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class StateIO extends Bundle with ConfigIO {
    val bEndFlag = Output(Bool())
    val bEndData = Output(UInt(DATA_WIDTH.W))
    val bCSRType = Output(UInt(2.W))
}
