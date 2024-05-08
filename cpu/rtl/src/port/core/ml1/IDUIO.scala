package cpu.port.ml1

import chisel3._
import chisel3.util._

import cpu.base._
import scala.collection.Stepper.UnboxingIntStepper

class IDUCtrIO extends Bundle with ConfigIO {
    val bInstName = Output(UInt(SIGS_WIDTH.W))
    val bALUType  = Output(UInt(SIGS_WIDTH.W))
    val bALURS1   = Output(UInt(SIGS_WIDTH.W))
    val bALURS2   = Output(UInt(SIGS_WIDTH.W))
    val bJmpEn    = Output(Bool())
    val bMemWrEn  = Output(Bool())
    val bMemByt   = Output(UInt(SIGS_WIDTH.W))
    val bRegWrEn  = Output(Bool())
    val bRegWrSrc = Output(UInt(SIGS_WIDTH.W))
}

class IDUDataIO extends Bundle with ConfigIO {
    val bGPRRdAddr   = Output(UInt(ADDR_WIDTH.W))
    val bCSRRdData   = Output(UInt(DATA_WIDTH.W))
    val bCSRWrAddr   = Output(UInt(ADDR_WIDTH.W))
    val bALURS1Data  = Output(UInt(DATA_WIDTH.W))
    val bALURS2Data  = Output(UInt(DATA_WIDTH.W))
    val bJmpOrWrData = Output(UInt(DATA_WIDTH.W))
}
