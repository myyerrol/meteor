package cpu.port.ml2

import chisel3._
import chisel3.util._

import cpu.base._

class WBUIO extends Bundle with ConfigIO {
    val oGPRWrData = Output(UInt(DATA_WIDTH.W))
}
