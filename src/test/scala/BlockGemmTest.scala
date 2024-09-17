import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import snax_acc.gemm.{BlockGemm, DefaultConfig}

class BlockGemmTest extends AnyFlatSpec with ChiselScalatestTester {
  "BlockGemm" should "process input data correctly and measure performance" in {
    test(new BlockGemm(DefaultConfig.gemmConfig)) { dut =>

      // Configuración inicial
      dut.io.ctrl.bits.M_i.poke(2.U) // Ejemplo de valores para dimensiones de la matriz
      dut.io.ctrl.bits.K_i.poke(2.U)
      dut.io.ctrl.bits.N_i.poke(2.U)
      dut.io.ctrl.bits.subtraction_constant_i.poke(0.U) // Constante de ejemplo

      // Habilitar ctrl.valid
      dut.io.ctrl.valid.poke(true.B)

      // Esperar a que el módulo esté listo
      dut.clock.step(1)

      // Deshabilitar ctrl.valid
      dut.io.ctrl.valid.poke(false.B)

      // Proveer datos de entrada
      dut.io.data.a_i.valid.poke(true.B)
      dut.io.data.b_i.valid.poke(true.B)
      dut.io.data.c_i.valid.poke(true.B)

      // Asignar valores de ejemplo a las entradas
      // Asegúrate de que coincidan con el dataWidth esperado por tu diseño
      dut.io.data.a_i.bits.poke("h0000000A".U(32.W)) // Valor de 32 bits de ejemplo
      dut.io.data.b_i.bits.poke("h00000002".U(32.W))
      dut.io.data.c_i.bits.poke("h00000000".U(32.W))

      // Medir tiempo de ejecución
      val startTime = System.nanoTime()

      // Esperar a que el módulo procese los datos
      dut.clock.step(10) // Ajusta esto según tu tiempo de procesamiento

      // Medir el tiempo después de la ejecución
      val endTime = System.nanoTime()
      val elapsedTime = endTime - startTime
      println(f"Tiempo de ejecucion: $elapsedTime nanosegundos")

      // Verificar salida
      if (dut.io.data.d_o.valid.peek().litToBoolean) {
        val output = dut.io.data.d_o.bits.peek().litValue
        println(f"Output D: 0x$output%X")

        // Comparar con el valor esperado (ejemplo)
        val expectedOutput = 1.U // Calcula el resultado esperado según tu diseño
        val error = (output - expectedOutput.litValue).abs
        println(f"Error de aproximacion: $error")

        // Verificar si el error es aceptable según tus criterios
        if (error < 1) {
          println("valor menor a 1")
        }// Ajusta el umbral según sea necesario
      } else {
        println("Output D no es válido aún.")
      }

      // Esperar algunos ciclos adicionales
      dut.clock.step(10) // Tiempo de espera adicional si es necesario
    }
  }
}
