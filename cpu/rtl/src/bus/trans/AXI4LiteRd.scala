package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteRdM extends AXI4LiteState with ConfigInst {
    val io = IO(new Bundle {
        val pRdM = new AXI4LiteRdMIO
    })

    val wARReady = Wire(Bool())
    val wRValid  = Wire(Bool())
    val wRReady  = Wire(Bool())

    wARReady := io.pRdM.pAR.bReady
    wRValid  := io.pRdM.pR.bValid
    wRReady  := true.B

    val rARValid = RegInit(false.B)
    val rARAddr  = RegInit(ADDR_ZERO)
    val rRValid  = RegInit(false.B)

    io.pRdM.oRdEn      := false.B
    io.pRdM.oRdData    := DATA_ZERO
    io.pRdM.oRdResp    := AXI4_RESP_OKEY
    io.pRdM.oRdFlag    := false.B
    io.pRdM.oRdState   := rRdState
    io.pRdM.pAR.bValid := rARValid
    io.pRdM.pAR.bAddr  := rARAddr
    io.pRdM.pR.bReady  := wRReady

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
            rARValid := Mux(!rARValid, io.pRdM.iRdEn, true.B)
            rARAddr  := rARAddr
        }
        is (sRdAddrShake) {
            when (rARValid && wARReady) {
                io.pRdM.oRdEn := true.B
                rARValid      := false.B
                rARAddr       := io.pRdM.iRdAddr
            }
        }
        is (sRdDataShake) {
            io.pRdM.oRdEn := true.B
            when (wRValid && wRReady) {
                io.pRdM.oRdFlag := true.B
                io.pRdM.oRdData := io.pRdM.pR.bData
                io.pRdM.oRdResp := io.pRdM.pR.bResp
                rARValid   := rARValid
                rARAddr    := rARAddr
            }
        }
    }
}

class AXI4LiteRdS extends AXI4LiteState with ConfigInst {
    val io = IO(new Bundle {
        val pRdS = new AXI4LiteRdSIO
    })

    // AXI4-Lite read address
    io.pRdS.oRdEn      := io.pRdS.iRdEn
    io.pRdS.oRdAddr    := io.pRdS.pAR.bAddr
    io.pRdS.pAR.bReady := true.B

    // AXI4-Lite read data
    val rARShake = RegInit(false.B)
    val rRValid  = RegInit(false.B)

    rARShake := Mux(io.pRdS.iRdState === sRdAddrShake &&
                    io.pRdS.pAR.bValid && io.pRdS.pAR.bReady,
                    true.B,
                    rARShake)
    rRValid  := Mux(rARShake, io.pRdS.iRValid, rRValid)

    when (io.pRdS.pR.bValid && io.pRdS.pR.bReady) {
        rARShake := false.B
        rRValid  := false.B
    }

    io.pRdS.pR.bValid := rRValid
    io.pRdS.pR.bData  := io.pRdS.iRdData
    io.pRdS.pR.bResp  := io.pRdS.iRdResp
}
