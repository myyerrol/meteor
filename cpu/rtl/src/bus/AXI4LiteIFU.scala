package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteIFUM extends AXI4LiteState with ConfigInst {
    val io = IO(new Bundle {
        val iRdEn   = Input(Bool())
        val iRdAddr = Input(UInt(ADDR_WIDTH.W))
        val oRdEn   = Output(Bool())
        val oRdFlag = Output(Bool())
        val oRdData = Output(UInt(DATA_WIDTH.W))
        val oRdResp = Output(UInt(RESP_WIDTH.W))
        val oState  = Output(UInt(AXSM_WIDTH.W))

        val pAR     = new AXI4LiteARIO
        val pR      = new AXI4LiteRIO
    })

    val rARValid = RegInit(false.B)
    val rARAddr  = RegInit(ADDR_ZERO)
    val rRValid  = RegInit(false.B)

    val wARReady = Wire(Bool())
    val wRValid  = Wire(Bool())
    val wRReady  = Wire(Bool())

    wARReady := io.pAR.bReady
    wRValid  := io.pR.bValid
    wRReady  := true.B

    io.oRdEn      := false.B
    io.oRdData    := DATA_ZERO
    io.oRdResp    := AXI4_RESP_OKEY
    io.oRdFlag    := false.B
    io.oState     := rRdState
    io.pAR.bValid := rARValid
    io.pAR.bAddr  := rARAddr
    io.pR.bReady  := wRReady

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
            when (wRValid && wRReady) {
                rRdState := sRdAddrValid
            }
            .otherwise {
                rRdState := sRdDataShake
            }
        }
    }
    switch (rRdState) {
        is (sRdAddrValid) {
            rARValid := Mux(!rARValid, io.iRdEn, true.B)
            rARAddr  := rARAddr
        }
        is (sRdAddrShake) {
            when (rARValid && wARReady) {
                io.oRdEn := true.B
                rARValid := false.B
                rARAddr  := io.iRdAddr
            }
        }
        is (sRdDataShake) {
            io.oRdEn := true.B
            when (wRValid && wRReady) {
                io.oRdFlag := true.B
                io.oRdData := io.pR.bData
                io.oRdResp := io.pR.bResp
                rARValid   := rARValid
                rARAddr    := rARAddr
            }
        }
    }
}

class AXI4LiteIFUS extends AXI4LiteState with ConfigInst {
    val io = IO(new Bundle {
        // Master
        val iRdEn   = Input(Bool())
        val iState  = Input(UInt(AXSM_WIDTH.W))
        // Mem
        val iRValid = Input(Bool())
        val iRdData = Input(UInt(DATA_WIDTH.W))

        val oRdEn   = Output(Bool())
        val oRdAddr = Output(UInt(ADDR_WIDTH.W))

        val pAR     = Flipped(new AXI4LiteARIO)
        val pR      = Flipped(new AXI4LiteRIO)
    })

    io.oRdEn      := io.iRdEn
    io.oRdAddr    := io.pAR.bAddr
    io.pAR.bReady := true.B

    val rARShake = RegInit(false.B)
    val rRValid  = RegInit(false.B)

    // when (io.iState === 1.U && io.pAR.bValid && io.pAR.bReady) {
    //     rRValid := true.B
    // }

    rARShake := Mux(io.iState === sRdAddrShake &&
                    io.pAR.bValid && io.pAR.bReady,
                    true.B,
                    rARShake)
    rRValid  := Mux(rARShake, io.iRValid, rRValid)

    when (io.pR.bValid && io.pR.bReady) {
        rARShake := false.B
        rRValid  := false.B
    }

    io.pR.bValid := rRValid
    io.pR.bData  := io.iRdData
    io.pR.bResp  := AXI4_RESP_OKEY
}
