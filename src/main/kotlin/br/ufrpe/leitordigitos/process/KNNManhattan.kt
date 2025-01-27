package br.ufrpe.leitordigitos.process

import br.ufrpe.leitordigitos.Imagem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.abs

class KNNManhattan(imgs: Array<Imagem>, kn: Int, qnt: Int) :KNN(imgs, kn, qnt) {
    override fun acharVizinhoMaisProximo(imagem: Imagem): Array<Imagem> {
        var maiorDist = Long.MAX_VALUE
        val knn = arrayOfNulls<Imagem>(super.kn)
        val referencias = Array(super.kn) {Long.MAX_VALUE}

        runBlocking {
            treino.toList().chunked(100).forEach { chunck: List<Imagem> ->
                launch (Dispatchers.Default) {
                    chunck.forEach { treino: Imagem ->
                        var cont: Long = 0

                        for (i in 0 until 28) {
                            for (j in 0 until 28) {
                                cont += abs(
                                    (imagem.imagem[i][j].toUByte().toInt() - treino.imagem[i][j].toUByte().toInt())
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

        return knn.requireNoNulls()
    }

    private fun adicionarElemento(knn: Array<Imagem?>, referencias: Array<Long>, imagem: Imagem, cont: Long): Long {
        var maiorI = 0
        var maior = referencias[0]
        for (i in 1..<referencias.size) {
            if (referencias[i] > maior) {
                maior = referencias[i]
                maiorI = i
            }
        }
        referencias[maiorI] = cont
        knn[maiorI] = imagem
        maior = referencias[0]
        for (i in 1..<referencias.size) {
            if (referencias[i] > maior) {
                maior = referencias[i]
            }
        }
        return maior
    }
}