import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import snax_acc.gemm.{BlockGemm, DefaultConfig}
import scala.util.Random

class BlockGemmTest extends AnyFlatSpec with ChiselScalatestTester {
  "BlockGemm" should "process input data correctly with 2x2 matrix" in {
    test(new BlockGemm(DefaultConfig.gemmConfig)) { dut =>

      // Configurar parámetros del control
      dut.io.ctrl.bits.M_i.poke(4.U)
      dut.io.ctrl.bits.K_i.poke(4.U)
      dut.io.ctrl.bits.N_i.poke(4.U)
      dut.io.ctrl.bits.subtraction_constant_i.poke(0.U)

      // Habilitar ctrl.valid
      dut.io.ctrl.valid.poke(true.B)
      dut.clock.step(1) // Esperar a que el módulo esté listo
      dut.io.ctrl.valid.poke(false.B)

      // Generar matrices 2x2 aleatorias
      val random = new Random()
      val matrixA = Seq.fill(4)(random.nextInt(100).U(32.W))
      val matrixB = Seq.fill(4)(random.nextInt(100).U(32.W))
      val matrixC = Seq.fill(4)(0.U(32.W))

      // Mostrar matrices generadas (solo para depuración)
      println(s"Matrix A: ${matrixA.mkString(", ")}")
      println(s"Matrix B: ${matrixB.mkString(", ")}")
      println(s"Matrix C: ${matrixC.mkString(", ")}")

      // Calcular el resultado esperado en Scala
      var expectedSum: BigInt = 0
      for (i <- 0 until 4) {
        expectedSum += (matrixA(i).litValue * matrixB(i).litValue) + matrixC(i).litValue
      }

      // Proveer datos de entrada
      dut.io.data.a_i.valid.poke(true.B)
      dut.io.data.b_i.valid.poke(true.B)
      dut.io.data.c_i.valid.poke(true.B)

      // Enviar los valores de las matrices
      for (i <- 0 until 4) {
        dut.io.data.a_i.bits.poke(matrixA(i))
        dut.io.data.b_i.bits.poke(matrixB(i))
        dut.io.data.c_i.bits.poke(matrixC(i))
        dut.clock.step(1) // Un paso por cada dato enviado
      }

      // Medir tiempo de ejecución
      val startTime = System.nanoTime()

      // Esperar a que el módulo procese los datos
      dut.clock.step(10)

      // Medir el tiempo después de la ejecución
      val endTime = System.nanoTime()
      val elapsedTime = endTime - startTime
      println(f"Tiempo de ejecucion: $elapsedTime nanosegundos")

      // Verificar salida
      if (dut.io.data.d_o.valid.peek().litToBoolean) {
        val output = dut.io.data.d_o.bits.peek().litValue
        println(f"Output D: $output%d")
        println(f"resultado calculado en TB: $expectedSum")

        // Comparar con el valor esperado
        val error = (output - expectedSum).abs
        println(f"Error de aproximacion: $error")

        // Verificar si el error es aceptable
        if (error < 1) {
          println("Error de aproximacion menor a 1")
        } else {
          println("Error de aproximacion mayor al permitido")
        }
      } else {
        println("Output D no es valido aun.")
      }

      // Esperar algunos ciclos adicionales si es necesario
      dut.clock.step(10)
    }
  }
}
