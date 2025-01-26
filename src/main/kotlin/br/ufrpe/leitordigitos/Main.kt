package br.ufrpe.leitordigitos

import java.io.*
import java.util.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

class Main {
    companion object {
        var qnt: Int = 100
        var tabela: Array<IntArray> = Array(10) { IntArray(10) }
        var quantidade: String = "quantidade <- c("
        var percentual: String = "porcentagem <- c("

        private fun realizarKNNCosseno() {
            println("Iniciando KNN com distancia Cosseno: ")
            val testes = teste.size
            val tempo = System.currentTimeMillis().toDouble()
            for (i in 0..<testes) {
                val vizinhos = acharVizinhosMaisProximosCosseno(teste[i])
                val vizinho = vizinhoMaisProximo(vizinhos)
                if (teste[i].label == vizinho) {
                    atual++
                }
                tabela[teste[i].label.code - 48][vizinho.code - 48]++
            }
            for (i in tabela.indices) {
                print("\n|\t")
                for (j in tabela[i].indices) {
                    print(tabela[i][j].toString() + "\t|\t")
                    tabela[i][j] = 0
                }
                println()
            }
            println(atual.toString() + "/" + testes)
            val dado1 = atual.toDouble()
            val dado2 = testes.toDouble()
            println("Sucesso: " + (dado1 / dado2) * 100.0 + "%")
            println("Tempo de teste: " + (System.currentTimeMillis() - tempo) / 1000 + "s")
            println("Encerrado\n")
        }

        private fun acharVizinhosMaisProximosCosseno(imagem: Imagem): Array<Imagem> {
            var menorDist = Double.MIN_VALUE
            val knn = arrayOfNulls<Imagem>(KNN)
            val referencias = DoubleArray(KNN)
            for (i in 0..<KNN) {
                referencias[i] = -1.0
            }
            val referencia: Array<ByteArray> = imagem.imagem
            for (k in treino.indices) {
                val teste: Array<ByteArray> = treino[k].imagem
                var cont = 0.0
                var x = 0.0
                var y = 0.0
                var xy = 0.0
                for (i in 0..27) {
                    for (j in 0..27) {
                        x += (referencia[i][j].toUByte() * referencia[i][j].toUByte()).toDouble()
                        y += (teste[i][j].toUByte() * teste[i][j].toUByte()).toDouble()
                        xy += (referencia[i][j].toUByte() * teste[i][j].toUByte()).toDouble()
                    }
                }
                x = sqrt(x)
                y = sqrt(y)
                cont = xy / (x * y)
                if (cont > menorDist) {
                    menorDist = adicionarElementoCos(
                        knn, referencias,
                        treino[k], cont
                    )
                }
            }
            return knn.requireNoNulls()
        }

        private fun adicionarElementoCos(knn: Array<Imagem?>,referencias: DoubleArray,imagem: Imagem?,cont: Double): Double {
            var cont = cont
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

        private fun realizarKNNEuclidiana() {
            println("Iniciando KNN com distancia Euclidiana: ")
            atual = 0
            val testes = teste.size
            val tempo = System.currentTimeMillis().toDouble()
            for (i in 0..<testes) {
                val vizinhos = acharVizinhosMaisProximosEuclidiana(teste[i])
                val vizinho = vizinhoMaisProximo(vizinhos)
                if (teste[i].label == vizinho) {
                    atual++
                }
                tabela[teste[i].label.code - 48][vizinho.code - 48]++
            }
            for (i in tabela.indices) {
                print("\n|\t")
                for (j in tabela[i].indices) {
                    print(tabela[i][j].toString() + "\t|\t")
                    tabela[i][j] = 0
                }
                println()
            }
            println(atual.toString() + "/" + testes)
            val dado1 = atual.toDouble()
            val dado2 = testes.toDouble()
            println("Sucesso: " + (dado1 / dado2) * 100.0 + "%")
            println("Tempo de teste: " + (System.currentTimeMillis() - tempo) / 1000 + "s")
            println("Encerrado\n")
        }

        private fun acharVizinhosMaisProximosEuclidiana(imagem: Imagem): Array<Imagem> {
            var maiorDist = Double.MAX_VALUE
            val knn = arrayOfNulls<Imagem>(KNN)
            val referencias = DoubleArray(KNN)
            for (i in 0 until KNN) {
                referencias[i] = Double.MAX_VALUE
            }
            val referencia: Array<ByteArray> = imagem.imagem
            for (k in treino.indices) {
                val teste: Array<ByteArray> = treino[k].imagem
                var cont = 0.0
                for (i in 0..27) {
                    for (j in 0..27) {
                        cont += (referencia[i][j].toUByte() - teste[i][j].toUByte()).toDouble().pow(2.0)
                    }
                }
                cont = sqrt(cont)
                if (cont < maiorDist) {
                    maiorDist = adicionarElemento(knn, referencias,treino[k], cont)
                }
            }
            return knn.requireNoNulls()
        }

        private fun adicionarElemento(knn: Array<Imagem?>,referencias: DoubleArray,imagem: Imagem,cont: Double): Double {
            var maiorI = 0
            var maior = referencias[0]
            for (i in 1..<referencias.size) {
                if (referencias[i.toInt()] > maior) {
                    maior = referencias[i.toInt()]
                    maiorI = i.toInt()
                }
            }
            referencias[maiorI] = floor(cont).toLong().toDouble()
            knn[maiorI] = imagem
            maior = referencias[0]
            for (i in 1..<referencias.size) {
                if (referencias[i.toInt()] > maior) {
                    maior = referencias[i.toInt()]
                }
            }
            return maior
        }

        private fun adicionarElemento(knn01: Array<Imagem?>,referencias: LongArray,imagem: Imagem,cont: Long): Long {
            var maiorI = 0
            var maior = referencias[0]
            for (i in 1..<referencias.size) {
                if (referencias[i.toInt()] > maior) {
                    maior = referencias[i.toInt()]
                    maiorI = i.toInt()
                }
            }
            referencias[maiorI] = cont
            knn01[maiorI] = imagem
            maior = referencias[0]
            for (i in 1..<referencias.size) {
                if (referencias[i.toInt()] > maior) {
                    maior = referencias[i.toInt()]
                }
            }
            return maior
        }

        fun realizarKNNManhattan() {
            println("Iniciando KNN com distancia Manhattan: ")
            atual = 0
            val testes = teste.size
            val tempo = System.currentTimeMillis().toDouble()
            for (i in 0..<testes) {
                val vizinhos = acharVizinhosMaisProximosManhattan(teste[i])
                val vizinho = vizinhoMaisProximo(vizinhos)
                if (teste[i].label == vizinho) {
                    atual++
                }
            }
            println(atual.toString() + "/" + testes)
            val dado1 = atual.toDouble()
            val dado2 = testes.toDouble()
            quantidade += (qnt * 10).toString() + ","
            percentual += ((dado1 / dado2) * 100.0).toString() + ","
            println("Sucesso: " + (dado1 / dado2) * 100.0 + "%")
            println("Tempo de teste: " + (System.currentTimeMillis() - tempo) / 1000 + "s")
            println("Encerrado\n")
        }

        private fun vizinhoMaisProximo(vizinhos: Array<Imagem>): Char {
            val res = ByteArray(10)
            for (i in vizinhos.indices) {
                res[vizinhos[i].label.code - 48]++
            }
            var maior = 0
            for (i in 0..9) {
                if (res[i] > maior) {
                    maior = res[i].toInt()
                }
            }
            var qnt = 0
            for (i in 0..9) {
                if (maior == res[i].toInt()) {
                    qnt++
                }
            }
            val k = rand.nextInt(qnt)
            var cont = 0
            for (i in 0..9) {
                if (res[i].toInt() == maior && cont < k) {
                    cont++
                } else if (res[i].toInt() == maior && cont == k) {
                    return (i + 48).toChar()
                }
            }
            return ' '
        }

        private fun acharVizinhosMaisProximosManhattan(imagem: Imagem): Array<Imagem> {
            var maiorDist = Long.MAX_VALUE
            val knn = arrayOfNulls<Imagem>(KNN)
            val referencias = LongArray(KNN)
            for (i in 0..<KNN) {
                referencias[i] = Long.MAX_VALUE
            }
            val referencia: Array<ByteArray> = imagem.imagem
            for (k in treino.indices) {
                val teste: Array<ByteArray> = treino[k].imagem
                var cont: Long = 0
                for (i in 0..27) {
                    for (j in 0..27) {
                        cont = (cont + abs(
                            (referencia[i][j].toUByte() - teste[i][j].toUByte()).toDouble()
                        )).toLong()
                    }
                }


                if (cont < maiorDist) {
                    maiorDist = adicionarElemento(
                        knn, referencias,
                        treino[k], cont
                    )
                }
            }
            return knn.requireNoNulls()
        }

        fun prepararTreinoETeste() {
            println("Preparar casos de treino e teste: ")
            val treino = ArrayList<Imagem>()
            val teste = ArrayList<Imagem>()
            var dados = ArrayList<Imagem>()
            dados.add(img[0])
            for (i in 1..59999) {
                dados.add(img[i])
                if (img[i - 1].label != img[i].label || i == 59999) {
                    if (i != 59999) {
                        dados.removeAt(dados.size - 1)
                    }
                    prepararDados(treino, teste, dados)
                    dados = ArrayList()
                    dados.add(img[i])
                }
            }
            Collections.shuffle(treino)
            Collections.shuffle(teste)
            this.treino = treino.toTypedArray<Imagem>()
            this.teste = teste.toTypedArray<Imagem>()
            println("Casos de treino e teste prontos\n")
        }

        private fun prepararDados(treino: ArrayList<Imagem>, testes: ArrayList<Imagem>, dados: ArrayList<Imagem>) {
            var cont = 0
            while (cont < qnt * porcentagem) {
                val num = rand.nextInt(dados.size)
                treino.add(dados[num])
                dados.removeAt(num)
                Collections.shuffle(dados)
                cont++
            }
            while (cont < qnt) {
                val num = rand.nextInt(dados.size)
                testes.add(dados[num])
                dados.removeAt(num)
                Collections.shuffle(dados)
                cont++
            }
        }

        @Throws(IOException::class)
        fun pegarImagensArff() {
            println("Iniciando leitura do arquivo: ")
            val tempo = System.currentTimeMillis().toDouble()
            val file = File(javaClass.getResource("digitos.arff").file)
            val reader = BufferedReader(FileReader(file))
            var imagem: Array<ByteArray>
            val imgs = ArrayList<Imagem>()
            while (reader.readLine() != "@data");
            for (k in 0..59999) {
                val linha = reader.readLine().split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                imagem = Array(28) { ByteArray(28) }
                for (i in 0..27) {
                    for (j in 0..27) {
                        try {
                            imagem[i][j] = linha[28 * i + j].toByte()
                        } catch (e: NumberFormatException) {
                            val s = linha[28 * i + j].toInt()
                            imagem[i][j] = (s and (0xFF.toByte()).toInt()).toByte()
                        }
                    }
                }
                imgs.add(Imagem(imagem, linha[linha.size - 1][0]))
            }
            reader.close()
            img = imgs.toTypedArray()
            println("Tempo de leitura do arquivo: " + (System.currentTimeMillis() - tempo) / 1000 + "s")
        }
    }
}

    fun main() {
        Main.pegarImagensArff()
        var i = 0
        var j = 0
        var qnt = 100
        do {
            val writer = BufferedWriter(FileWriter("grafico.r"))
            do {
                println("$j-$i")
                //Main.prepararTreinoETeste()
                //Main.realizarKNNManhattan()
                //Main.realizarKNNEuclidiana();
                //Main.realizarKNNCosseno();
            } while (++i < 10)
            i = 0
            qnt += 100
            writer.write(Main.quantidade.substring(0, Main.quantidade.length - 1) + ")")
            writer.newLine()
            writer.write(Main.percentual.substring(0, Main.percentual.length - 1) + ")")
            writer.newLine()
            writer.write("plot(porcentagem~quantidade)")
            writer.flush()
            writer.close()
        } while (++j < 8)
    }
