package cpu.base

import chisel3._
import chisel3.util._

object ExtenSign {
    def apply(cNum: UInt, cLen: Int) = {
        val numLen  = cNum.getWidth
        val signBit = cNum(numLen - 1)
        if (numLen >= cLen) {
            cNum(cLen - 1, 0)
        }
        else {
            Cat(Fill(cLen - numLen, signBit), cNum)
        }
    }
}

object ExtenZero {
    def apply(cNum: UInt, cLen: Int) = {
        val numLen  = cNum.getWidth
        if (numLen >= cLen) {
            cNum(cLen - 1, 0)
        }
        else {
            Cat(0.U((cLen - numLen).W), cNum)
        }
    }
}
