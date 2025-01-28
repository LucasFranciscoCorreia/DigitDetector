package br.ufrpe.leitordigitos.process

import br.ufrpe.digitreader.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.sqrt

class KNNCosseno(imgs: Array<Image>, kn: Int, qnt: Int): KNN(imgs, kn, qnt) {
    override fun acharVizinhoMaisProximo(image: Image): Array<Image> {
        var menorDist = -1.0
        val knn = arrayOfNulls<Image>(super.kNeighbors)
        val referencias = Array(super.kNeighbors) {-1.0}
        runBlocking {
            super.treinos.toList().chunked(100).forEach { chunck: List<Image> ->
                launch(Dispatchers.Default) {
                    chunck.forEach { treino: Image ->
                        var x = 0.0
                        var y = 0.0
                        var xy = 0.0
                        for (i in 0..27) {
                            for (j in 0..27) {
                                x += (treino.image[i][j].toUByte().toInt() * treino.image[i][j].toUByte().toInt()).toDouble()
                                y += (image.image[i][j].toUByte().toInt() * image.image[i][j].toUByte().toInt()).toDouble()
                                xy += (treino.image[i][j].toUByte().toInt() * image.image[i][j].toUByte().toInt()).toDouble()
                            }
                        }
                        x = sqrt(x)
                        y = sqrt(y)
                        val cont = if (x != 0.0 && y != 0.0) xy / (x * y) else 0.0
                        if (x != 0.0 && y != 0.0 && cont > menorDist) {
                            menorDist = adicionarElemento(knn, referencias,treino, cont)
                        }
                    }
                }
            }
        }
        return knn.requireNoNulls()
    }
    private val lock = ReentrantLock()

    private fun adicionarElemento(knn: Array<Image?>, referencias: Array<Double>, image: Image, cont: Double): Double {
        lock.lock()
        var menorI = 0
        var menor = referencias[0]
        try {
            for (i in 1 until referencias.size) {
                if (referencias[i] < menor) {
                    menor = referencias[i]
                    menorI = i
                }
            }
            referencias[menorI] = (cont)
            knn[menorI] = image
            menor = referencias[0]
            for (i in 1..<referencias.size) {
                if (referencias[i] < menor) {
                    menor = referencias[i]
                }
            }
        } finally {
            lock.unlock()
        }
        return menor
    }
}