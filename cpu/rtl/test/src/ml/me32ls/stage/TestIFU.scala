import chisel3._
import chiseltest._
import utest._

import cpu.base._
import cpu.core.ml.me32ls._

object TestIFU extends ChiselUtestTester with ConfigInst {
    val tests = Tests {
        test("PCNext") {
            testCircuit(new IFU(), Seq(WriteVcdAnnotation, VerilatorBackendAnnotation)) { dut =>
                var vPCNext: Long = 0x80000000L
                dut.io.iJmpEn.poke(false.B)
                dut.io.iJmpPC.poke(0x00000000L)
                for (i <- 1 until 50) {
                    dut.io.pIFU.bPC.expect(vPCNext.U)
                    dut.clock.step()
                    vPCNext = vPCNext + 4
                }
            }
        }
        test("PCJump") {
            testCircuit(new IFU()) { dut =>
                var vPCJump: Long = 0x80008000L
                dut.io.iJmpEn.poke(true.B)
                for (i <- 1 until 50) {
                    dut.io.iJmpPC.poke(vPCJump.U)
                    dut.clock.step()
                    dut.io.pIFU.bPC.expect(vPCJump.U)
                    vPCJump = vPCJump + 8
                }
            }
        }
        test("PCNextJump") {
            testCircuit(new IFU()) { dut =>
                var vPCNext: Long = 0x80000000L
                var vPCJump: Long = 0x80008000L
                for (i <- 1 until 50) {
                    if (i % 2 == 0) {
                        dut.io.iJmpEn.poke(true.B)
                        dut.io.iJmpPC.poke(vPCJump.U)
                        dut.clock.step()
                        dut.io.pIFU.bPC.expect(vPCJump.U)
                        vPCNext = vPCJump + 4
                        vPCJump = vPCJump + 8
                    }
                    else {
                        dut.io.iJmpEn.poke(false.B)
                        dut.io.iJmpPC.poke(0x00000000L)
                        if (i == 1) {
                            dut.io.pIFU.bPC.expect(vPCNext.U)
                            dut.clock.step()
                            vPCNext = vPCNext + 4
                        }
                        else {
                            dut.clock.step()
                            dut.io.pIFU.bPC.expect(vPCNext.U)
                            vPCNext = vPCNext + 4
                        }
                    }
                }
            }
        }
    }
}
