package br.ufrpe.leitordigitos.process

import br.ufrpe.digitreader.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.pow
import kotlin.math.sqrt

class KNNEuclidiana(imgs: Array<Image>, kn: Int, qnt: Int): KNN(imgs, kn, qnt) {
    private val lock = ReentrantLock()

    override fun acharVizinhoMaisProximo(imagem: Image): Array<Image> {
        val knn: Array<Image?> = arrayOfNulls(super.kNeighbors)
        val referencias = Array(kNeighbors) { Double.MAX_VALUE }
        runBlocking {
            var maiorDist = Double.MAX_VALUE
            super.treinos.chunked(100).forEach { chunck: List<Image> ->
                launch(Dispatchers.Default) {
                    chunck.forEach { treino: Image ->
                        val cont = calcularDistanciaEuclidiana(imagem, treino)
                        if(cont < maiorDist){
                            maiorDist = addElement(knn, referencias, treino, cont)
                        }
                    }
                }
            }
        }
        return knn.requireNoNulls()
    }

    private fun calcularDistanciaEuclidiana(imagem: Image, treino: Image): Double {
        var cont = 0.0
        for (i in 0..27) {
            for (j in 0..27) {
                cont += (imagem.image[i][j].toUByte().toInt() - treino.image[i][j].toUByte().toInt()).toDouble()
                    .pow(2)
            }
        }
        return sqrt(cont)
    }
    private fun addElement(knn: Array<Image?>,referencias: Array<Double>,imagem: Image,cont: Double): Double {
        lock.lock()
        var maiorI = 0
        var maior = referencias[0]
        try {
            for (i in 1 until referencias.size) {
                if (referencias[i] > maior) {
                    maior = referencias[i]
                    maiorI = i
                }
            }

            referencias[maiorI] = cont
            knn[maiorI] = imagem
            maior = referencias[0]
            for (i in 1 until referencias.size) {
                if (referencias[i] > maior) {
                    maior = referencias[i]
                }
            }
        } finally {
            lock.unlock()
        }

        return maior
    }
}