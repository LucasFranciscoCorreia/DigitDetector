package br.ufrpe.leitordigitos.process

import br.ufrpe.leitordigitos.Imagem
import br.ufrpe.leitordigitos.Main
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

abstract class KNN(private val imgs: Array<Imagem>, val kn: Int, val qnt: Int) {
    val porcentagem = 0.90
    lateinit var treino: Array<Imagem>
    lateinit var teste: Array<Imagem>
    var tabela: Array<Array<Int>> = Array(10) { Array(10) {0} }

    private fun prepararTreinoETeste() {
        println("Preparar casos de treino e teste: ")
        val treino = ArrayList<Imagem>()
        val teste = ArrayList<Imagem>()
        var dados = ArrayList<Imagem>()
        dados.add(imgs[0])
        for (i in 1..59999) {
            dados.add(imgs[i])
            if (imgs[i - 1].label != imgs[i].label || i == 59999) {
                if (i != 59999) {
                    dados.removeAt(dados.size - 1)
                }
                prepararDados(treino, teste, dados)
                dados = ArrayList()
                dados.add(imgs[i])
            }
        }
        this.treino = treino.toTypedArray<Imagem>()
        this.teste = teste.toTypedArray<Imagem>()
        println("Casos de treino e teste prontos\n")
    }

    private fun prepararDados(treino: ArrayList<Imagem>, testes: ArrayList<Imagem>, dados: ArrayList<Imagem>) {
        var cont = 0
        val rand = Random(System.currentTimeMillis())
        while (cont < qnt * porcentagem) {
            val num = rand.nextInt(dados.size)
            treino.add(dados[num])
            dados.removeAt(num)
            cont++
        }
        while (cont < qnt) {
            val num = rand.nextInt(dados.size)
            testes.add(dados[num])
            dados.removeAt(num)
            cont++
        }
    }

    private fun vizinhoMaisProximo(vizinhos: Array<Imagem>): Char {
        val res = Array<Byte>(10) {0}
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
        val rand = Random(System.currentTimeMillis())
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

    fun start(){
        fun printTabela(){
            for (i in tabela.indices) {
                print("\n|\t")
                for (j in tabela[i].indices) {
                    print(tabela[i][j].toString() + "\t|\t")
                    tabela[i][j] = 0
                }
                println()
            }
        }
        println("Iniciando KNN: ")
        var atual = 0
        val tempo = System.currentTimeMillis().toDouble()
        prepararTreinoETeste()
        for (teste in this.teste) {
            val vizinhos = this.acharVizinhoMaisProximo(teste)
            val vizinho = vizinhoMaisProximo(vizinhos)
            if (teste.label == vizinho) {
                atual++
            }
            tabela[teste.label.code - 48][vizinho.code - 48]++
        }

        printTabela()
        println(atual.toString() + "/" + teste.size)
        val dado1 = atual.toDouble()
        val dado2 = teste.size.toDouble()
        Main.quantidade += (qnt * 10).toString() + ","
        Main.percentual += String.format(Locale.US, "%.2f,",(dado1 / dado2) * 100.0)
        println("Sucesso: " + (dado1 / dado2) * 100.0 + "%")
        println("Tempo de teste: " + (System.currentTimeMillis() - tempo) / 1000 + "s")
        println("Encerrado\n")
    }

    abstract fun acharVizinhoMaisProximo(imagem: Imagem): Array<Imagem>
}