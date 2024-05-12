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

    val mAXI4LiteIFU  = Module(new AXI4LiteRdM)
    val mAXI4LiteSRAM = Module(new AXI4LiteSRAM)

    val wRdEn = WireInit(true.B)
    val rAddr = RegInit(ADDR_INIT)

    val (cRValidNum, cRValidFlag) = Counter(mAXI4LiteSRAM.io.pRdS.oRdEn, 10)

    mAXI4LiteIFU.io.pRdM.iRdEn     := wRdEn
    mAXI4LiteIFU.io.pRdM.iRdAddr   := rAddr
    mAXI4LiteIFU.io.pRdM.pAR       <> mAXI4LiteSRAM.io.pRdS.pAR
    mAXI4LiteIFU.io.pRdM.pR        <> mAXI4LiteSRAM.io.pRdS.pR
    mAXI4LiteSRAM.io.pRdS.iRdEn    := mAXI4LiteIFU.io.pRdM.oRdEn
    mAXI4LiteSRAM.io.pRdS.iRdState := mAXI4LiteIFU.io.pRdM.oRdState
    mAXI4LiteSRAM.io.pRdS.iRdData  := mMem.io.pMemInst.pRd.bData
    mAXI4LiteSRAM.io.pRdS.iRdResp  := AXI4_RESP_OKEY
    mAXI4LiteSRAM.io.pRdS.iRValid  := cRValidFlag

    mMem.io.pMemInst.pRd.bEn   := mAXI4LiteSRAM.io.pRdS.oRdEn
    mMem.io.pMemInst.pRd.bAddr := mAXI4LiteSRAM.io.pRdS.oRdAddr
    mMem.io.pMemData           := DontCare

    when (mAXI4LiteIFU.io.pRdM.oRdFlag) {
        rAddr := rAddr + 4.U
    }

    io.pState.bEndPreFlag := DontCare
    io.pState.bEndAllFlag := Mux(mAXI4LiteIFU.io.pRdM.oRdData === INST_EBRK,
                                 true.B,
                                 false.B)
    io.pState.bEndAllData := DontCare
    io.pState.bCSRType    := DontCare

    printf("AXI4Lite Read\n")
    printf("[axi]      state: %d\n", mAXI4LiteIFU.io.pRdM.oRdState)
    printf("[axi] [ar] valid: %d, ready: %d, addr: %x\n",
           mAXI4LiteIFU.io.pRdM.pAR.bValid,
           mAXI4LiteIFU.io.pRdM.pAR.bReady,
           mAXI4LiteIFU.io.pRdM.pAR.bAddr)
    printf("[axi] [r]  valid: %d, ready: %d, data: %x, resp: %d\n",
           mAXI4LiteIFU.io.pRdM.pR.bValid,
           mAXI4LiteIFU.io.pRdM.pR.bReady,
           mAXI4LiteIFU.io.pRdM.pR.bData,
           mAXI4LiteIFU.io.pRdM.pR.bResp)
    printf("------------------------------------------------------\n")
    printf("[axi] [m] flag: %d, data: %x, resp: %d\n",
           mAXI4LiteIFU.io.pRdM.oRdFlag,
           mAXI4LiteIFU.io.pRdM.oRdData,
           mAXI4LiteIFU.io.pRdM.oRdResp)
    printf("[axi] [s] en:   %d, addr: %x\n",
           mAXI4LiteSRAM.io.pRdS.oRdEn,
           mAXI4LiteSRAM.io.pRdS.oRdAddr)
    printf("------------------------------------------------------\n")
    printf("[axi] [t] rvalid num: %d, rvalid flag: %d\n",
           cRValidNum,
           cRValidFlag)
    printf("\n");
}
