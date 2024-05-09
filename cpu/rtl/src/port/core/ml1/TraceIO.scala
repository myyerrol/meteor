package cpu.port.ml1

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class TraceIO extends Bundle with ConfigIO {
    val pBase    =         new BaseIO
    val pGPRRd   =         new GPRRdIO
    val pGPRWr   =         new GPRWrIO
    val pCSRRd   =         new CSRRdIO
    val pCSRWr   =         new CSRWrIO
    val pMemInst = Flipped(new MemDualInstIO)
    val pMemData = Flipped(new MemDualDataIO)

    val pIDUCtr  =         new IDUCtrIO
    val pIDUData =         new IDUDataIO
    val pEXUJmp  =         new EXUJmpIO
    val pEXUOut  =         new EXUOutIO
}
