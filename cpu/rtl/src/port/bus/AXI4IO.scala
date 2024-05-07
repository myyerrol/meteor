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

class AXI4RIO extends Bundle with ConfigIO {
    val bID    = Input(UInt(AXID_WIDTH.W))
    val bData  = Input(UInt(DATA_WIDTH.W))
    val bResp  = Input(UInt(RESP_WIDTH.W))
    val bLast  = Input(Bool())
    val bUser  = Input(UInt(AXUS_WIDTH.W))
    val bValid = Input(Bool())

    val bReady = Output(Bool())
}

class AXI4AWIO extends Bundle with ConfigIO {
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

class AXI4WIO extends Bundle with ConfigIO {
    val bReady = Input(Bool())

    val bID    = Output(UInt(AXID_WIDTH.W))
    val bData  = Output(UInt(DATA_WIDTH.W))
    val bStrb  = Output(Vec(MASK_WIDTH, Bool()))
    val bLast  = Output(Bool())
    val bUser  = Output(UInt(AXUS_WIDTH.W))
    val bValid = Output(Bool())
}

class AXI4BIO extends Bundle with ConfigIO {
    val bID    = Input(UInt(AXID_WIDTH.W))
    val bResp  = Input(UInt(RESP_WIDTH.W))
    val bUser  = Input(UInt(AXUS_WIDTH.W))
    val bValid = Input(Bool())

    val bReady = Output(Bool())
}