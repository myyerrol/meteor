package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteIFUM extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRdEn   = Input(Bool())
        val iRdAddr = Input(UInt(ADDR_WIDTH.W))
        val oRdData = Output(UInt(DATA_WIDTH.W))
        val oRdFlag = Output(Bool())

        val pAR   = new AXI4LiteARIO
        val pR    = new AXI4LiteRIO
    })

    // AXI4-Lite AR
    val rARValid = Reg(Bool())
    val wARReady = Wire(Bool())

    wARReady := io.pAR.bReady

    when (rARValid && wARReady) {
        io.pAR.bAddr := io.iRdAddr
        rARValid     := false.B
    }
    .otherwise {
        io.pAR.bAddr := ADDR_ZERO
        rARValid     := Mux(rARValid, true.B, io.iRdEn)
    }

    io.oRdFlag    := rARValid && wARReady
    io.pAR.bValid := rARValid

    // AXI4-Lite R
    io.pR.bReady := true.B
    io.oRdData   := DATA_ZERO

    when (io.pR.bValid && io.pR.bReady) {
        io.oRdData := Mux(io.pR.bResp === AXI4_RESP_OKEY,
                          io.pR.bData,
                          io.pR.bResp)
    }
}

class AXI4LiteIFUS extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRdData = Input(UInt(DATA_WIDTH.W))
        val oRdAddr = Output(UInt(ADDR_WIDTH.W))

        val pAR     = Flipped(new AXI4LiteARIO)
        val pR      = Flipped(new AXI4LiteRIO)
    })

    // AXI4-Lite AR
    io.oRdAddr    := io.pAR.bAddr
    io.pAR.bReady := true.B

    // AXI4-Lite R
    when (io.pAR.bValid && io.pAR.bReady) {
        io.pR.bValid := true.B
    }
    .otherwise {
        io.pR.bValid := false.B
    }

    io.pR.bData := io.iRdData
    io.pR.bResp := Mux(io.iRdData =/= DATA_ZERO,
                       AXI4_RESP_OKEY,
                       AXI4_RESP_SLVEER)
}
