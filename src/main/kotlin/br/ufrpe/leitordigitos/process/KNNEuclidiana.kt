package br.ufrpe.leitordigitos.process

import br.ufrpe.leitordigitos.Imagem
import kotlin.math.pow
import kotlin.math.sqrt

class KNNEuclidiana(imgs: Array<Imagem>, kn: Int, qnt: Int): KNN(imgs, kn, qnt) {
    override fun acharVizinhoMaisProximo(imagem: Imagem): Array<Imagem> {
        var maiorDist = Double.MAX_VALUE
        val knn = arrayOfNulls<Imagem>(kn)
        val referencias = Array(kn) {Double.MAX_VALUE}

        for (teste in super.teste) {
            var cont = 0.0
            for (i in 0..27) {
                for (j in 0..27) {
                    cont += (imagem.imagem[i][j].toUByte().toInt() - teste.imagem[i][j].toUByte().toInt()).toDouble().pow(2)
                }
            }
            cont = sqrt(cont)
            if (cont < maiorDist) {
                maiorDist = adicionarElemento(knn, referencias, teste, cont)
            }
        }
        return knn.requireNoNulls()
    }

    private fun adicionarElemento(knn: Array<Imagem?>, referencias: Array<Double>, imagem: Imagem, cont: Double): Double {
        var maiorI = 0
        var maior = referencias[0]

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

        return maior
    }
}