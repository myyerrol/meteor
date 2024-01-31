package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class EndIO extends Bundle with ConfigIO {
    val bFlag = Output(Bool())
    val bData = Output(UInt(DATA_WIDTH.W))
}
