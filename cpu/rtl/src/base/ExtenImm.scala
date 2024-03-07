package cpu.base

import chisel3._
import chisel3.util._

object ExtenImm extends ConfigInst {
    def apply(cInst: UInt, cType: String) = {
        val wImmI      = cInst(31, 20)
        val wImmIExten = ExtenSign(wImmI, DATA_WIDTH)
        val wImmS      = Cat(cInst(31, 25), cInst(11, 7))
        val wImmSExten = ExtenSign(wImmS, DATA_WIDTH)
        val wImmB      = Cat(cInst(31), cInst(7), cInst(30, 25), cInst(11, 8), 0.U(1.W))
        val wImmBExten = ExtenSign(wImmB, DATA_WIDTH)
        val wImmU      = Cat(cInst(31, 12), 0.U(12.W))
        val wImmUExten = ExtenSign(wImmU, DATA_WIDTH)
        val wImmJ      = Cat(cInst(31), cInst(19, 12), cInst(20), cInst(30, 21), 0.U(1.W))
        val wImmJExten = ExtenSign(wImmJ, DATA_WIDTH)

        cType match {
            case "immI" => wImmIExten
            case "immS" => wImmSExten
            case "immB" => wImmBExten
            case "immU" => wImmUExten
            case "immJ" => wImmJExten
        }
    }
}