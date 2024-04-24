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
        val oRdResp = Output(UInt(RESP_WIDTH.W))
        val oRdFlag = Output(Bool())

        val iWrEn   = Input(Bool())
        val iWrAddr = Input(UInt(ADDR_WIDTH.W))
        val iWrData = Input(UInt(DATA_WIDTH.W))
        val iWrStrb = Input(UInt(MASK_WIDTH.W))
        val oWrResp = Output(UInt(RESP_WIDTH.W))
        val oWrFlag = Output(Bool())

        val pAR     = new AXI4LiteARIO
        val pR      = new AXI4LiteRIO
        val pAW     = new AXI4LiteAWIO
        val pW      = new AXI4LiteWIO
        val pB      = new AXI4LiteBIO
    })

    val rARAddr  = RegInit(ADDR_ZERO)
    val rARValid = RegInit(false.B)
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

            io.oRdFlag := true.B
            io.oRdData := io.pR.bData
            io.oRdResp := io.pR.bResp
        }
    }

    val rAWValid = RegInit(false.B)
    val rAWAddr  = RegInit(ADDR_ZERO)
    val rWValid  = RegInit(false.B)
    val rBValid  = RegInit(false.B)

    val wAWReady = Wire(Bool())
    val wWReady  = Wire(Bool())
    val wBReady  = Wire(Bool())

    wAWReady := io.pAW.bReady
    wWReady  := io.pW.bReady
    wBReady  := true.B

    io.oWrResp    := AXI4_RESP_OKEY
    io.oWrFlag    := false.B
    io.pAW.bValid := rAWValid
    io.pAW.bAddr  := rAWAddr
    io.pW.bValid  := rWValid
    io.pW.bData   := io.iWrData
    io.pW.bStrb   := io.iWrStrb
    io.pB.bReady  := wBReady

    val sWrAddrValid :: sWrAddrShake :: sWrDataShake :: sWrDataResp :: Nil = Enum(4)
    val rWrState = RegInit(sWrAddrValid)
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
            when (rBValid && wBReady) {
                rWrState := sWrAddrValid
            }
            .otherwise {
                rWrState := sWrDataResp
            }
        }
    }
    switch (rWrState) {
        is (sWrAddrValid) {
            rAWValid := io.iWrEn
            rWValid  := false.B
        }
        is (sWrAddrShake) {
            rAWValid := false.B
            rWValid  := io.pW.bValid
        }
        is (sWrDataShake) {
            rAWValid := false.B
            rWValid  := false.B
        }
        is (sWrDataResp) {
            rAWValid := false.B
            rWValid  := false.B
        }
    }


    // // AXI4-Lite AW
    // val rAWValid = Reg(Bool())
    // val wAWReady = io.pAW.bReady

    // when (rAWValid && wAWReady) {
    //     io.pAW.bAddr := io.iWrAddr
    //     rAWValid     := false.B
    // }
    // .otherwise {
    //     io.pAW.bAddr := ADDR_ZERO
    //     rAWValid     := Mux(rAWValid, true.B, io.iWrEn)
    // }

    // io.pAW.bValid := rAWValid

    // // AXI4-Lite W
    // val wWValid = rAWValid && wAWReady
    // val wWReady = io.pW.bReady

    // when (wWValid && wWReady) {
    //     io.pW.bData := io.iWrData
    //     io.pW.bStrb := io.iWrStrb
    // }
    // .otherwise {
    //     io.pW.bData := DATA_ZERO
    //     io.pW.bStrb := MASK_ZERO
    // }

    // io.pW.bValid := wWValid

    // // AXI4-Lite B
    // io.pB.bReady := true.B
    // io.oWrFlag   := Mux(
    //     io.pB.bValid && io.pB.bReady && io.pB.bResp === AXI4_RESP_OKEY,
    //     true.B,
    //     false.B
    // )
}

class AXI4LiteLSUS extends Module with ConfigInst {
    val io = IO(new Bundle {
        val iRdData = Input(UInt(DATA_WIDTH.W))
        val oRdAddr = Output(UInt(ADDR_WIDTH.W))

        val oWrAddr = Output(UInt(ADDR_WIDTH.W))
        val oWrData = Output(UInt(DATA_WIDTH.W))
        val oWrStrb = Output(UInt(MASK_WIDTH.W))

        val pAR     = Flipped(new AXI4LiteARIO)
        val pR      = Flipped(new AXI4LiteRIO)
        val pAW     = Flipped(new AXI4LiteAWIO)
        val pW      = Flipped(new AXI4LiteWIO)
        val pB      = Flipped(new AXI4LiteBIO)
    })

    io.oRdAddr    := io.pAR.bAddr
    io.pAR.bReady := true.B

    io.pR.bValid := Mux(io.pAR.bValid && io.pAR.bReady, true.B, false.B)
    io.pR.bData  := io.iRdData
    io.pR.bResp  := Mux(io.iRdData =/= DATA_ZERO,
                        AXI4_RESP_OKEY,
                        AXI4_RESP_SLVEER)

    io.oWrAddr    := io.pAW.bAddr
    io.pAW.bReady := true.B

    io.oWrData   := io.pW.bData
    io.oWrStrb   := io.pW.bStrb
    io.pW.bReady := true.B

    io.pB.bValid := Mux(io.pW.bValid && io.pW.bReady, true.B, false.B)
    io.pB.bResp  := AXI4_RESP_OKEY
}
