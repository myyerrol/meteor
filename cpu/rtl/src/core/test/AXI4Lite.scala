package cpu.core.test

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.mem._
import cpu.bus._

class AXI4Lite extends Module with ConfigInst {
    val io = IO(new Bundle {
        val pState = new StateIO
    });

    val mMem = Module(new MemDualFakeBB)

    val mAXI4LiteRdM = Module(new AXI4LiteRdM)
    val mAXI4LiteRdS = Module(new AXI4LiteRdS)

    val wRdEn = WireInit(true.B)
    val rAddr = RegInit(ADDR_INIT)

    mAXI4LiteRdM.io.pRdM.iRdEn    := wRdEn
    mAXI4LiteRdM.io.pRdM.iRdAddr  := rAddr
    mAXI4LiteRdM.io.pRdM.pAR      <> mAXI4LiteRdS.io.pRdS.pAR
    mAXI4LiteRdM.io.pRdM.pR       <> mAXI4LiteRdS.io.pRdS.pR
    mAXI4LiteRdS.io.pRdS.iRdEn    := mAXI4LiteRdM.io.pRdM.oRdEn
    mAXI4LiteRdS.io.pRdS.iRdState := mAXI4LiteRdM.io.pRdM.oRdState
    mAXI4LiteRdS.io.pRdS.iRdData  := mMem.io.pMemInst.pRd.bData
    mAXI4LiteRdS.io.pRdS.iRdResp  := AXI4_RESP_OKEY
    mAXI4LiteRdS.io.pRdS.iRValid  := true.B

    mMem.io.pMemInst.pRd.bEn   := mAXI4LiteRdS.io.pRdS.oRdEn
    mMem.io.pMemInst.pRd.bAddr := mAXI4LiteRdS.io.pRdS.oRdAddr
    mMem.io.pMemData           := DontCare

    when (mAXI4LiteRdM.io.pRdM.oRdFlag) {
        rAddr := rAddr + 4.U
    }

    io.pState.bEndPreFlag := DontCare
    io.pState.bEndAllFlag := DontCare
    io.pState.bEndAllData := DontCare
    io.pState.bCSRType    := DontCare

    printf("AXI4Lite Read\n")
    printf("[axi]      state: %d\n", mAXI4LiteRdM.io.pRdM.oRdState)
    printf("[axi] [ar] valid: %d, ready: %d, addr: %x\n",
           mAXI4LiteRdM.io.pRdM.pAR.bValid,
           mAXI4LiteRdM.io.pRdM.pAR.bReady,
           mAXI4LiteRdM.io.pRdM.pAR.bAddr)
    printf("[axi] [r]  valid: %d, ready: %d, data: %x, resp: %d\n",
           mAXI4LiteRdM.io.pRdM.pR.bValid,
           mAXI4LiteRdM.io.pRdM.pR.bReady,
           mAXI4LiteRdM.io.pRdM.pR.bData,
           mAXI4LiteRdM.io.pRdM.pR.bResp)
    printf("------------------------------------------------------\n")
    printf("[axi] [m] flag: %d, data: %x, resp: %d\n",
           mAXI4LiteRdM.io.pRdM.oRdFlag,
           mAXI4LiteRdM.io.pRdM.oRdData,
           mAXI4LiteRdM.io.pRdM.oRdResp)
    printf("[axi] [s] en:   %d, addr: %x\n",
           mAXI4LiteRdS.io.pRdS.oRdEn,
           mAXI4LiteRdS.io.pRdS.oRdAddr)
    printf("\n");
}
