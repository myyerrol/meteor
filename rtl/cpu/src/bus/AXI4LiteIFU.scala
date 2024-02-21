package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteIFUMaster extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRdEn = Input(Bool())
        val iAddr = Input(UInt(ADDR_WIDTH.W))
        val oData = Output(UInt(DATA_WIDTH.W))

        val pAR   = new AXI4LiteARIO
        val pR    = new AXI4LiteRIO
    })

    // AXI4-Lite AR
    val rARValid = RegInit(false.B)
    val wARReady = WireInit(false.B)

    rARValid := Mux(rARValid, true.B, io.iRdEn)
    wARReady := io.pAR.bReady

    when (rARValid && wARReady) {
        io.pAR.bAddr := io.iAddr
        rARValid     := false.B
    }
    .otherwise {
        io.pAR.bAddr := ADDR_ZERO
        rARValid     := rARValid
    }

    io.pAR.bValid := rARValid

    // AXI4-Lite R
    io.pR.bReady := true.B
    io.oData     := DATA_ZERO

    when (io.pR.bValid && io.pR.bReady) {
        io.oData := Mux(io.pR.bResp === AXI4_RESP_OKEY,
                        io.pR.bData,
                        io.pR.bResp)
    }
}

class AXI4LiteIFUSlave extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iData = Input(UInt(DATA_WIDTH.W))
        val oAddr = Output(UInt(ADDR_WIDTH.W))

        val pAR   = Flipped(new AXI4LiteARIO)
        val pR    = Flipped(new AXI4LiteRIO)
    })

    // AXI4-Lite AR
    io.oAddr      := io.pAR.bAddr
    io.pAR.bReady := true.B

    // AXI4-Lite R
    when (io.pAR.bValid && io.pAR.bReady) {
        io.pR.bValid := true.B
    }
    .otherwise {
        io.pR.bValid := false.B
    }

    io.pR.bData := io.iData
    io.pR.bResp := Mux(io.iData =/= DATA_ZERO,
                       AXI4_RESP_OKEY,
                       AXI4_RESP_SLVEER)
}
