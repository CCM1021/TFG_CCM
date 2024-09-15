import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import snax_acc.gemm.{BlockGemm, DefaultConfig}

class BlockGemmTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior of "BlockGemm"

  it should "pass basic functionality test" in {
    test(new BlockGemm(DefaultConfig.gemmConfig)) { c =>
      // Reset the module
      c.reset.poke(true.B)
      c.clock.step(2) // Ensure sufficient time for reset

      // Release reset
      c.reset.poke(false.B)
      c.clock.step(1) // Advance the clock to process the release of reset

      // Example of poking inputs
      c.io.ctrl.bits.M_i.poke(1.U)
      c.io.ctrl.bits.N_i.poke(1.U)
      c.io.ctrl.bits.K_i.poke(1.U)
      c.io.ctrl.bits.subtraction_constant_i.poke(0.U)

      c.io.ctrl.valid.poke(true.B)
      c.io.ctrl.ready.expect(true.B)
      c.io.ctrl.fire.poke(true.B)
      c.clock.step(1)

      // Example of poking data inputs
      c.io.data.a_i.bits.poke(0x12345678.U)
      c.io.data.b_i.bits.poke(0x87654321.U)
      c.io.data.c_i.bits.poke(0x00000000.U)
      c.io.data.a_i.valid.poke(true.B)
      c.io.data.b_i.valid.poke(true.B)
      c.io.data.c_i.valid.poke(true.B)
      c.io.data.a_i.ready.expect(true.B)
      c.io.data.b_i.ready.expect(true.B)
      c.io.data.c_i.ready.expect(true.B)
      c.clock.step(1)

      // Expect output data to be valid
      c.io.data.d_o.valid.expect(true.B)
      // Additional checks can be performed based on expected results
    }
  }
}
