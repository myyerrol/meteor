package cpu.bus

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._

class AXI4LiteWrM extends AXI4LiteState with ConfigInst {
    val io = IO(new Bundle {
        val pWrM = new AXI4LiteWrMIO
    })

    val wAWReady = Wire(Bool())
    val wWReady  = Wire(Bool())
    val wBValid  = Wire(Bool())
    val wBReady  = Wire(Bool())

    wAWReady := io.pWrM.pAW.bReady
    wWReady  := io.pWrM.pW.bReady
    wBReady  := true.B

    val rAWValid = RegInit(false.B)
    val rWValid  = RegInit(false.B)

    io.pWrM.oWrEn      := false.B
    io.pWrM.oWrFlag    := false.B
    io.pWrM.oWrResp    := AXI4_RESP_OKEY
    io.pWrM.oWrState   := rWrState
    io.pWrM.pAW.bValid := rAWValid
    io.pWrM.pAW.bAddr  := io.pWrM.iWrAddr
    io.pWrM.pW.bValid  := rWValid
    io.pWrM.pW.bData   := io.pWrM.iWrData
    io.pWrM.pW.bStrb   := io.pWrM.iWrStrb
    io.pWrM.pB.bReady  := wBReady

    switch (rWrState) {
        is (sWrAddrValid) {
            when (rAWValid) {
                rWrState := sWrAddrShake
            }
            .otherwise {
                rWrState := sWrAddrValid
            }
        }
        is (sWrAddrShake) {
            when (rAWValid && wAWReady) {
                rWrState := sWrDataShake
            }
            .otherwise {
                rWrState := sWrAddrShake
            }
        }
        is (sWrDataShake) {
            when (rWValid && wWReady) {
                rWrState := sWrDataResp
            }
            .otherwise {
                rWrState := sWrDataShake
            }
        }
        is (sWrDataResp) {
            when (wBValid && wBReady) {
                rWrState := sWrAddrValid
            }
            .otherwise {
                rWrState := sWrDataResp
            }
        }
    }
    switch (rWrState) {
        is (sWrAddrValid) {
            rAWValid := Mux(!rAWValid, io.pWrM.iWrEn, true.B)
            rWValid  := rWValid
        }
        is (sWrAddrShake) {
            when (rAWValid && wAWReady) {
                rAWValid := false.B
                rWValid  := true.B
            }
        }
        is (sWrDataShake) {
            when (rWValid && wWReady) {
                io.pWrM.oWrEn := true.B
                rAWValid      := rAWValid
                rWValid       := false.B
            }
        }
        is (sWrDataResp) {
            io.pWrM.oWrEn := true.B
            when (wBValid && wBReady) {
                io.pWrM.oWrFlag := true.B
                io.pWrM.oWrResp := io.pWrM.pB.bResp
                rAWValid        := rAWValid
                rWValid         := rWValid
            }
        }
    }
}

class AXI4LiteWrS extends AXI4LiteState with ConfigInst {
    val io = IO(new Bundle {
        val pWrS = new AXI4LiteWrSIO
    })

    // AXI4-Lite write address
    io.pWrS.oWrEn      := io.pWrS.iWrEn
    io.pWrS.oWrAddr    := io.pWrS.pAW.bAddr
    io.pWrS.pAW.bReady := true.B

    // AXI4-Lite write data
    io.pWrS.oWrData   := io.pWrS.pW.bData
    io.pWrS.oWrStrb   := io.pWrS.pW.bStrb
    io.pWrS.pW.bReady := true.B

    // AXI4-Lite write response
    val rWShake = RegInit(false.B)
    val rBValid = RegInit(false.B)

    rWShake := Mux(io.pWrS.iWrState === sWrDataShake &&
                   io.pWrS.pW.bValid && io.pWrS.pW.bReady,
                   true.B,
                   rWShake)
    rBValid := Mux(rWShake, io.pWrS.iBValid, rBValid)

    when (io.pWrS.pB.bValid && io.pWrS.pB.bReady) {
        rWShake := false.B
        rBValid := false.B
    }

    io.pWrS.pB.bValid := rBValid
    io.pWrS.pB.bResp  := io.pWrS.iWrResp
}
