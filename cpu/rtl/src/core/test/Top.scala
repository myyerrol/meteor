package cpu.core.test

import chisel3._
import chisel3.util._

import cpu.base._
import cpu.port._
import cpu.temp._

class Top extends Module with ConfigInst with Build {
    val io = IO(new Bundle {
        val pState = new StateIO
    });

    val mAXI4Lite = Module(new AXI4Lite)

    io.pState <> mAXI4Lite.io.pState
}
