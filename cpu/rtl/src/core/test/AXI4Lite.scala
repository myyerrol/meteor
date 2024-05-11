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
    val rAddr = RegInit("x80000004".U(ADDR_WIDTH.W))

    mAXI4LiteIFUM.io.iRdEn   := wRdEn
    mAXI4LiteIFUM.io.iRdAddr := rAddr
    mAXI4LiteIFUM.io.pAR     <> mAXI4LiteIFUS.io.pAR
    mAXI4LiteIFUM.io.pR      <> mAXI4LiteIFUS.io.pR
    mAXI4LiteIFUS.io.iRdData := mMem.io.pMemInst.pRd.bData

    mMem.io.pMemInst.pRd.bEn   := mAXI4LiteIFUS.io.oRdEn
    mMem.io.pMemInst.pRd.bAddr := mAXI4LiteIFUS.io.oRdAddr
    mMem.io.pMemData           := DontCare

    val wRdData = mAXI4LiteIFUM.io.oRdData
    val wRdResp = mAXI4LiteIFUM.io.oRdResp
    val wRdFlag = mAXI4LiteIFUM.io.oRdFlag

    io.pState.bEndPreFlag := DontCare
    io.pState.bEndAllFlag := DontCare
    io.pState.bEndAllData := DontCare
    io.pState.bCSRType    := DontCare

    printf("AXI4Lite\n")
    printf("State: %d\n", mAXI4LiteIFUM.io.oState)
    printf("AR Valid: %d, Ready: %d, Addr: %x\n",
           mAXI4LiteIFUM.io.pAR.bValid,
           mAXI4LiteIFUM.io.pAR.bReady,
           mAXI4LiteIFUM.io.pAR.bAddr)
    printf("R  Valid: %d, Ready: %d, Data: %x, Resp: %d\n",
           mAXI4LiteIFUM.io.pR.bValid,
           mAXI4LiteIFUM.io.pR.bReady,
           mAXI4LiteIFUM.io.pR.bData,
           mAXI4LiteIFUM.io.pR.bResp)
    printf("\n");
}
