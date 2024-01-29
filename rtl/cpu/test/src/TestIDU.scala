import chisel3._
import chiseltest._
import utest._

import cpu.core.ml.me32ls._

object TestIDU extends ChiselUtestTester {
    val tests = Tests {
        test("IDU") {
            testCircuit(new IFU(), Seq(WriteVcdAnnotation)) { dut =>
                dut.io.pIFU.bPC.expect("x80000000".U)
                dut.clock.step()
            }
        }
        test("IDU") {
            testCircuit(new IFU(), Seq(WriteVcdAnnotation)) { dut =>
                dut.io.pIFU.bPC.expect("x80000000".U)
                dut.clock.step()
            }
        }
    }
}
