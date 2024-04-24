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

        val pAR   = new AXI4LiteARIO
        val pR    = new AXI4LiteRIO
    })

    val rARValid = RegInit(false.B)
    val rRValid  = RegInit(false.B)
    val rARAddr  = RegInit(ADDR_ZERO)

    val wARReady = Wire(Bool())
    val wRReady  = Wire(Bool())

    wARReady := io.pAR.bReady
    wRReady  := true.B

    val sRd0 :: sRd1 :: sRd2 :: Nil = Enum(3)
    val rState = RegInit(sRd0)
    switch (rState) {
        is (sRd0) {
            when (rARValid) {
                rState := sRd1
            }
            .otherwise {
                rState := sRd0
            }
        }
        is (sRd1) {
            when (rARValid && wARReady) {
                rState := sRd2
            }
            .otherwise {
                rState := sRd1
            }
        }
        is (sRd2) {
            when (rRValid && wRReady) {
                rState := sRd0
            }
            .otherwise {
                rState := sRd2
            }
        }
    }

    io.oRdData    := DATA_ZERO
    io.oRdResp    := AXI4_RESP_OKEY
    io.oRdFlag    := false.B
    io.pAR.bValid := rARValid
    io.pAR.bAddr  := rARAddr
    io.pR.bReady  := wRReady

    switch (rState) {
        is (sRd0) {
            rARValid := io.iRdEn
            rRValid  := false.B
        }
        is (sRd1) {
            rARValid := false.B
            rRValid  := io.pR.bValid
            rARAddr  := io.iRdAddr
        }
        is (sRd2) {
            rARValid := false.B
            rRValid  := false.B

            io.oRdFlag := true.B
            io.oRdData := io.pR.bData
            io.oRdResp := io.pR.bResp
        }
    }
}

class AXI4LiteIFUS extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRdData = Input(UInt(DATA_WIDTH.W))
        val oRdAddr = Output(UInt(ADDR_WIDTH.W))

        val pAR     = Flipped(new AXI4LiteARIO)
        val pR      = Flipped(new AXI4LiteRIO)
    })

    io.oRdAddr    := io.pAR.bAddr
    io.pAR.bReady := true.B

    io.pR.bValid := Mux(io.pAR.bValid && io.pAR.bReady, true.B, false.B)
    io.pR.bData := io.iRdData
    io.pR.bResp := Mux(io.iRdData =/= DATA_ZERO,
                       AXI4_RESP_OKEY,
                       AXI4_RESP_SLVEER)
}
