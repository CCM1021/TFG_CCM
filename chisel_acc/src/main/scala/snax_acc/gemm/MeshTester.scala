import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class MeshTester extends AnyFlatSpec with ChiselScalatestTester {
  "Mesh" should "compute matrix multiplication correctly" in {
    test(new Mesh(DefaultConfig.gemmConfig)) { c =>
      // Inicializa las entradas
      for (i <- 0 until DefaultConfig.gemmConfig.meshRow) {
        for (j <- 0 until DefaultConfig.gemmConfig.tileSize) {
          c.io.data_a_i(i)(j).poke(1.U)
        }
      }
      for (i <- 0 until DefaultConfig.gemmConfig.meshCol) {
        for (j <- 0 until DefaultConfig.gemmConfig.tileSize) {
          c.io.data_b_i(i)(j).poke(2.U)
        }
      }
      c.io.ctrl.dotprod_a_b.poke(true.B)
      c.io.ctrl.a_b_c_ready_o.poke(true.B)

      // Ejecuta un ciclo de reloj y verifica el resultado
      c.clock.step(1)
      c.io.ctrl.d_valid_o.expect(true.B)  // Debe haber resultado válido
      for (i <- 0 until DefaultConfig.gemmConfig.meshRow) {
        for (j <- 0 until DefaultConfig.gemmConfig.meshCol) {
          c.io.data_d_o(i)(j).expect((DefaultConfig.gemmConfig.tileSize * 2).S)
        }
      }
    }
  }
}
