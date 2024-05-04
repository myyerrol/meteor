package cpu.port

import chisel3._
import chisel3.util._

import cpu.base._

class AXI4ARIO extends Bundle with ConfigIO {
    val bReady  = Input(Bool())

    val bID     = Output(UInt(AXID_WIDTH.W))
    val bAddr   = Output(UInt(ADDR_WIDTH.W))
    val bLen    = Output(UInt(AXLN_WIDTH.W))
    val bSize   = Output(UInt(AXSZ_WIDTH.W))
    val bBrust  = Output(UInt(AXBT_WIDTH.W))
    val bCache  = Output(UInt(AXCH_WIDTH.W))
    val bProt   = Output(UInt(AXPR_WIDTH.W))
    val bQoS    = Output(UInt(AXQS_WIDTH.W))
    val bRegion = Output(UInt(AXRE_WIDTH.W))
    val bUser   = Output(UInt(AXUS_WIDTH.W))
    val bValid  = Output(Bool())
}
