package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class TraceIO extends Bundle with ConfigIO {
    val pBase    =         new BaseIO
    val pGPRRd   =         new GPRRdIO
    val pGPRWr   =         new GPRWrIO
    val pMem     = Flipped(new MemDualIO)
    val pIDUCtr  =         new IDUCtrIO
    val pIDUData =         new IDUDataIO
    val pEXUJmp  =         new EXUJmpIO
}
