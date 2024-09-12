import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class TileTester extends AnyFlatSpec with ChiselScalatestTester {
  "Tile" should "do dot product computation correctly" in {
    test(new Tile(DefaultConfig.gemmConfig)) { c =>
      // Inicializa entradas
      c.io.ctrl.dotprod_a_b.poke(true.B)
      c.io.ctrl.a_b_c_ready_o.poke(true.B)
      c.io.ctrl.accumulate_i.poke(false.B)
      c.io.data_a_i(0).poke(1.U)
      c.io.data_a_i(1).poke(2.U)
      c.io.data_b_i(0).poke(1.U)
      c.io.data_b_i(1).poke(3.U)

      // Ejecuta un ciclo de reloj y verifica la salida
      c.clock.step(1)
      c.io.ctrl.d_valid_o.expect(true.B)  // Debe haber resultado válido
      c.io.data_d_o.expect(7.S)           // 1*1 + 2*3 = 7
    }
  }
}
