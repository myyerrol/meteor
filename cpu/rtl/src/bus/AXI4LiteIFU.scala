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
        val oRdResp = Output(UInt(RESP_WIDTH.W))
        val oRdFlag = Output(Bool())

        val pAR     = new AXI4LiteARIO
        val pR      = new AXI4LiteRIO
    })

    val rARValid = RegInit(false.B)
    val rARAddr  = RegInit(ADDR_ZERO)
    val rRValid  = RegInit(false.B)

    val wARReady = Wire(Bool())
    val wRReady  = Wire(Bool())

    wARReady := io.pAR.bReady
    wRReady  := true.B

    io.oRdData    := DATA_ZERO
    io.oRdResp    := AXI4_RESP_OKEY
    io.oRdFlag    := false.B
    io.pAR.bValid := rARValid
    io.pAR.bAddr  := rARAddr
    io.pR.bReady  := wRReady

    val sRdAddrValid :: sRdAddrShake :: sRdDataShake :: Nil = Enum(3)
    val rRdState = RegInit(sRdAddrValid)
    switch (rRdState) {
        is (sRdAddrValid) {
            when (rARValid) {
                rRdState := sRdAddrShake
            }
            .otherwise {
                rRdState := sRdAddrValid
            }
        }
        is (sRdAddrShake) {
            when (rARValid && wARReady) {
                rRdState := sRdDataShake
            }
            .otherwise {
                rRdState := sRdAddrShake
            }
        }
        is (sRdDataShake) {
            when (rRValid && wRReady) {
                rRdState := sRdAddrValid
            }
            .otherwise {
                rRdState := sRdDataShake
            }
        }
    }
    switch (rRdState) {
        is (sRdAddrValid) {
            rARValid := io.iRdEn
            rARAddr  := rARAddr
            rRValid  := false.B
        }
        is (sRdAddrShake) {
            rARValid := false.B
            rARAddr  := io.iRdAddr
            rRValid  := io.pR.bValid
        }
        is (sRdDataShake) {
            rARValid := false.B
            rARAddr  := rARAddr
            rRValid  := false.B

            io.oRdData := io.pR.bData
            io.oRdResp := io.pR.bResp
            io.oRdFlag := true.B
        }
    }
}

class AXI4LiteIFUS extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRdEn   = Input(Bool())
        val iRdData = Input(UInt(DATA_WIDTH.W))
        val oRdEn   = Output(Bool())
        val oRdAddr = Output(UInt(ADDR_WIDTH.W))

        val pAR     = Flipped(new AXI4LiteARIO)
        val pR      = Flipped(new AXI4LiteRIO)
    })

    io.oRdEn      := io.iRdEn
    io.oRdAddr    := io.pAR.bAddr
    io.pAR.bReady := true.B

    io.pR.bValid := Mux(io.pAR.bValid && io.pAR.bReady, true.B, false.B)
    io.pR.bData  := io.iRdData
    io.pR.bResp  := AXI4_RESP_OKEY
}
