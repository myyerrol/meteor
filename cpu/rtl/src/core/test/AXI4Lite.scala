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

    val mAXI4LiteIFUM = Module(new AXI4LiteIFUM)
    val mAXI4LiteIFUS = Module(new AXI4LiteIFUS)

    val wRdEn = WireInit(true.B)
    val rAddr = RegInit(ADDR_INIT)

    mAXI4LiteIFUM.io.iRdEn   := wRdEn
    mAXI4LiteIFUM.io.iRdAddr := rAddr
    mAXI4LiteIFUM.io.pAR     <> mAXI4LiteIFUS.io.pAR
    mAXI4LiteIFUM.io.pR      <> mAXI4LiteIFUS.io.pR
    mAXI4LiteIFUS.io.iRdEn   := mAXI4LiteIFUM.io.oRdEn
    mAXI4LiteIFUS.io.iRdData := mMem.io.pMemInst.pRd.bData
    mAXI4LiteIFUS.io.iState  := mAXI4LiteIFUM.io.oState
    mAXI4LiteIFUS.io.iRValid := true.B

    mMem.io.pMemInst.pRd.bEn   := mAXI4LiteIFUS.io.oRdEn
    mMem.io.pMemInst.pRd.bAddr := mAXI4LiteIFUS.io.oRdAddr
    mMem.io.pMemData           := DontCare

    val wRdData = mAXI4LiteIFUM.io.oRdData
    val wRdResp = mAXI4LiteIFUM.io.oRdResp
    val wRdFlag = mAXI4LiteIFUM.io.oRdFlag

    when (wRdFlag) {
        rAddr := rAddr + 4.U
    }

    io.pState.bEndPreFlag := DontCare
    io.pState.bEndAllFlag := DontCare
    io.pState.bEndAllData := DontCare
    io.pState.bCSRType    := DontCare

    printf("AXI4Lite IFU Read\n")
    printf("[axi]      state: %d\n", mAXI4LiteIFUM.io.oState)
    printf("[axi] [ar] valid: %d, ready: %d, addr: %x\n",
           mAXI4LiteIFUM.io.pAR.bValid,
           mAXI4LiteIFUM.io.pAR.bReady,
           mAXI4LiteIFUM.io.pAR.bAddr)
    printf("[axi] [r]  valid: %d, ready: %d, data: %x, resp: %d\n",
           mAXI4LiteIFUM.io.pR.bValid,
           mAXI4LiteIFUM.io.pR.bReady,
           mAXI4LiteIFUM.io.pR.bData,
           mAXI4LiteIFUM.io.pR.bResp)
    printf("------------------------------------------------------\n")
    printf("[axi] [m] flag: %d, data: %x, resp: %d\n",
           mAXI4LiteIFUM.io.oRdFlag,
           mAXI4LiteIFUM.io.oRdData,
           mAXI4LiteIFUM.io.oRdResp)
    printf("[axi] [s] en:   %d, addr: %x\n",
           mAXI4LiteIFUS.io.oRdEn,
           mAXI4LiteIFUS.io.oRdAddr)
    printf("\n");
}
