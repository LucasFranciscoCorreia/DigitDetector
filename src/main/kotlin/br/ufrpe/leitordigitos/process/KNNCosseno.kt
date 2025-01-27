package br.ufrpe.leitordigitos.process

import br.ufrpe.leitordigitos.Imagem
import kotlin.math.sqrt

class KNNCosseno(imgs: Array<Imagem>, kn: Int, qnt: Int): KNN(imgs, kn, qnt) {
    override fun acharVizinhoMaisProximo(imagem: Imagem): Array<Imagem> {
        var menorDist = -1.0
        val knn = arrayOfNulls<Imagem>(super.kn)
        val referencias = Array(super.kn) {-1.0}
        for (ref in treino) {
            var x = 0.0
            var y = 0.0
            var xy = 0.0
            for (i in 0..27) {
                for (j in 0..27) {
                    x += (ref.imagem[i][j].toUByte().toInt() * ref.imagem[i][j].toUByte().toInt()).toDouble()
                    y += (imagem.imagem[i][j].toUByte().toInt() * imagem.imagem[i][j].toUByte().toInt()).toDouble()
                    xy += (ref.imagem[i][j].toUByte().toInt() * imagem.imagem[i][j].toUByte().toInt()).toDouble()
                }
            }
            x = sqrt(x)
            y = sqrt(y)
            val cont = xy / (x * y)
            if (cont > menorDist) {
                menorDist = adicionarElemento(knn, referencias,ref, cont)
            }
        }
        return knn.requireNoNulls()
    }

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