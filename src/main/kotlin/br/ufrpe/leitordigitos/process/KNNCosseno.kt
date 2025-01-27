package br.ufrpe.leitordigitos.process

import br.ufrpe.leitordigitos.Imagem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.sqrt

class KNNCosseno(imgs: Array<Imagem>, kn: Int, qnt: Int): KNN(imgs, kn, qnt) {
    override fun acharVizinhoMaisProximo(imagem: Imagem): Array<Imagem> {
        var menorDist = -1.0
        val knn = arrayOfNulls<Imagem>(super.kn)
        val referencias = Array(super.kn) {-1.0}
        runBlocking {
            treino.toList().chunked(100).forEach { chunck: List<Imagem> ->
                launch(Dispatchers.Default) {
                    chunck.forEach { treino: Imagem ->
                        var x = 0.0
                        var y = 0.0
                        var xy = 0.0
                        for (i in 0..27) {
                            for (j in 0..27) {
                                x += (treino.imagem[i][j].toUByte().toInt() * treino.imagem[i][j].toUByte().toInt()).toDouble()
                                y += (imagem.imagem[i][j].toUByte().toInt() * imagem.imagem[i][j].toUByte().toInt()).toDouble()
                                xy += (treino.imagem[i][j].toUByte().toInt() * imagem.imagem[i][j].toUByte().toInt()).toDouble()
                            }
                        }
                        x = sqrt(x)
                        y = sqrt(y)
                        val cont = xy / (x * y)
                        if (cont > menorDist) {
                            menorDist = adicionarElemento(knn, referencias,treino, cont)
                        }
                    }
                }
            }
        }
        return knn.requireNoNulls()
    }
    @Synchronized
    private fun adicionarElemento(knn: Array<Imagem?>, referencias: Array<Double>, imagem: Imagem, cont: Double): Double {
        var menorI = 0
        var menor = referencias[0]
        for (i in 1..<referencias.size) {
            if (referencias[i] < menor) {
                menor = referencias[i]
                menorI = i
            }
        }
        referencias[menorI] = (cont)
        knn[menorI] = imagem
        menor = referencias[0]
        for (i in 1..<referencias.size) {
            if (referencias[i] < menor) {
                menor = referencias[i]
            }
        }
        return menor
    }
}