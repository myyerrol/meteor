package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteLSUM extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRdEn   = Input(Bool())
        val iRdAddr = Input(UInt(ADDR_WIDTH.W))
        val oRdData = Output(UInt(DATA_WIDTH.W))
        val oRdFlag = Output(Bool())

        val iWrEn   = Input(Bool())
        val iWrAddr = Input(UInt(ADDR_WIDTH.W))
        val iWrData = Input(UInt(DATA_WIDTH.W))
        val iWrStrb = Input(UInt(MASK_WIDTH.W))
        val oWrFlag = Output(Bool())

        val pAR     = new AXI4LiteARIO
        val pR      = new AXI4LiteRIO
        val pAW     = new AXI4LiteAWIO
        val pW      = new AXI4LiteWIO
        val pB      = new AXI4LiteBIO
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

    // AXI4-Lite AW
    val rAWValid = Reg(Bool())
    val wAWReady = io.pAW.bReady

    when (rAWValid && wAWReady) {
        io.pAW.bAddr := io.iWrAddr
        rAWValid     := false.B
    }
    .otherwise {
        io.pAW.bAddr := ADDR_ZERO
        rAWValid     := Mux(rAWValid, true.B, io.iWrEn)
    }

    io.pAW.bValid := rAWValid

    // AXI4-Lite W
    val wWValid = rAWValid && wAWReady
    val wWReady = io.pW.bReady

    when (wWValid && wWReady) {
        io.pW.bData := io.iWrData
        io.pW.bStrb := io.iWrStrb
    }
    .otherwise {
        io.pW.bData := DATA_ZERO
        io.pW.bStrb := MASK_ZERO
    }

    io.pW.bValid := wWValid

    // AXI4-Lite B
    io.pB.bReady := true.B
    io.oWrFlag   := Mux(
        io.pB.bValid && io.pB.bReady && io.pB.bResp === AXI4_RESP_OKEY,
        true.B,
        false.B
    )
}

class AXI4LiteLSUS extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRdData = Input(UInt(DATA_WIDTH.W))
        val oRdAddr = Output(UInt(ADDR_WIDTH.W))

        val iWrResp = Input(UInt(RESP_WIDTH.W))
        val oWrAddr = Output(UInt(ADDR_WIDTH.W))
        val oWrData = Output(UInt(DATA_WIDTH.W))
        val oWrStrb = Output(UInt(MASK_WIDTH.W))

        val pAR     = Flipped(new AXI4LiteARIO)
        val pR      = Flipped(new AXI4LiteRIO)
        val pAW     = Flipped(new AXI4LiteAWIO)
        val pW      = Flipped(new AXI4LiteWIO)
        val pB      = Flipped(new AXI4LiteBIO)
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

    // AXI4-Lite AW
    io.oWrAddr    := io.pAW.bAddr
    io.pAW.bReady := true.B

    // AXI4-Lite W
    io.pW.bReady := true.B
    io.oWrData   := io.pW.bData
    io.oWrStrb   := io.pW.bStrb

    // AXI4-Lite B
    when (io.pW.bValid && io.pW.bReady) {
        io.pB.bValid := true.B
    }
    .otherwise {
        io.pB.bValid := false.B
    }

    io.pB.bResp := io.iWrResp
}
