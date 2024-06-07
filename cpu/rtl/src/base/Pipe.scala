package cpu.base

import chisel3._
import chisel3.util._

import cpu.port._
import cpu.port.ml3._

class Pipe(vStage: String) extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iValid = Input(Bool())
        val iReady = Input(Bool())
        val pBaseI = Flipped(new BaseIO)
        // val pSpecI = Flipped(new SpecIO(vStage))

        val pBaseO = new BaseIO
        val pSpecO = new SpecIO(vStage)
    })

    // val rBase = Reg(Vec)

    when (io.iValid && io.iReady) {
    }

}
