import chisel3._
import chisel3.util._
import org.scalatest.FreeSpec
import snax_acc.gemm._

class BlockGemmTest extends FreeSpec with Matchers {
  "BlockGemm" - {
    "should perform matrix multiplication correctly" in {
      val params = DefaultConfig.gemmConfig
      val dut = new BlockGemm(params)

      // Set up input data
      val aData = Array.fill(params.meshRow * params.tileSize * params.dataWidthA)(0.U)
      val bData = Array.fill(params.tileSize * params.meshCol * params.dataWidthB)(0.U)
      val cData = Array.fill(params.meshRow * params.meshCol * params.dataWidthC)(0.U)

      // Set up expected output data
      val expectedOutput = Array.fill(params.meshRow * params.meshCol * params.dataWidthC)(0.U)

      // Drive input data
      dut.io.data.a_i.bits := aData
      dut.io.data.b_i.bits := bData
      dut.io.data.c_i.bits := cData

      // Drive control signals
      dut.io.ctrl.bits.M_i := 2.U
      dut.io.ctrl.bits.N_i := 3.U
      dut.io.ctrl.bits.K_i := 4.U
      dut.io.ctrl.bits.subtraction_constant_i := 0.U

      // Run the simulation
      dut.clock.step(10) // Initialize the module
      dut.io.ctrl.fire := true.B
      dut.clock.step(100) // Run the computation

      // Check the output data
      val outputData = dut.io.data.d_o.bits
      outputData shouldBe expectedOutput
    }
  }
}