package br.ufrpe.leitordigitos.process

import br.ufrpe.digitreader.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.abs

class KNNManhattan(imgs: Array<Image>, kn: Int, qnt: Int) :KNN(imgs, kn, qnt) {
    private val lock: Lock = ReentrantLock()

    override fun acharVizinhoMaisProximo(imagem: Image): Array<Image> {
        val knn = arrayOfNulls<Image>(super.kNeighbors)
        val referencias = Array(super.kNeighbors) {Long.MAX_VALUE}

        runBlocking {
            var maiorDist = Long.MAX_VALUE
            super.treinos.chunked(100).forEach { chunck: List<Image> ->
                launch (Dispatchers.Default) {
                    chunck.forEach { treino: Image ->
                        var cont: Long = 0

                        for (i in 0 until 28) {
                            for (j in 0 until 28) {
                                cont += abs(
                                    (imagem.image[i][j].toUByte().toInt() - treino.image[i][j].toUByte().toInt())
                                ).toLong()
                            }
                        }

                        if (cont < maiorDist) {
                            maiorDist = adicionarElemento(knn, referencias, treino, cont)
                        }

                    }
                }
            }
        }
        if (knn.any { it == null }) {
            throw IllegalStateException("knn array contains null elements")
        }
        return knn.requireNoNulls()
    }

    private fun adicionarElemento(knn: Array<Image?>, referencias: Array<Long>, imagem: Image, cont: Long): Long {
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