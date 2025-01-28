package br.ufrpe.leitordigitos.process

import br.ufrpe.digitreader.Image
import br.ufrpe.leitordigitos.Main
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

abstract class KNN(private val imgs: Array<Image>, val kNeighbors: Int, val quantity: Int) {
    private val percentage = 0.90
    var treinos: List<Image> = ArrayList()
    private val rand = Random(System.currentTimeMillis())
    private var testes: List<Image> = ArrayList()
    private var tabela: Array<Array<Int>> = Array(10) { Array(10) {0} }

    private fun prepararTreinoETeste() {
        println("Preparar casos de treino e teste: ")
        val treinoList = ArrayList<Image>()
        val testeList = ArrayList<Image>()
        var dados = ArrayList<Image>()
        dados.add(imgs[0])
        for (i in 1..59999) {
            dados.add(imgs[i])
            if (imgs[i - 1].label != imgs[i].label || i == 59999) {
                println(dados.size)
                if (i != 59999) {
                    dados.removeAt(dados.size - 1)
                }
                prepararDados(treinoList, testeList, dados)
                dados.clear()
                dados.add(imgs[i])
            }
        }
        this.treinos = treinoList
        this.testes = testeList
        println("Casos de treino e teste prontos\n")
    }

    private fun prepararDados(treino: ArrayList<Image>, testes: ArrayList<Image>, dados: ArrayList<Image>) {
        var cont = 0
        // val rand = Random(System.currentTimeMillis())
        while (cont < quantity * percentage) {
            val num = rand.nextInt(dados.size)
            treino.add(dados[num])
            dados.removeAt(num)
            cont++
        }
        while (cont < quantity) {
            val num = rand.nextInt(dados.size)
            testes.add(dados[num])
            dados.removeAt(num)
            cont++
        }
    }

    private fun vizinhoMaisProximo(vizinhos: Array<Image>): Char {
        val res = HashMap<Char, Int>()
        for (vizinho in vizinhos) {
            res[vizinho.label] = res.getOrDefault(vizinho.label, 0) + 1
        }
        val maior = res.values.maxOrNull() ?: 0
        val candidatos = res.filterValues { it == maior }.keys.toList()
        val rand = Random(System.currentTimeMillis())
        return candidatos[rand.nextInt(candidatos.size)]
    }

    private fun columnSum(i: Int): Int{
        var sum = 0
        for (j in tabela.indices){
            sum += tabela[j][i]
        }
        return sum
    }

    fun start() {
        println("Iniciando KNN: ")
        val tempo = System.currentTimeMillis().toDouble()
        tabela = Array(10) { Array(10) {0} }
        prepararTreinoETeste()
        val atual = processTestCases()
        printTabela()
        println(atual.toString() + "/" + this.testes.size)
        val dado1 = atual.toDouble()
        val dado2 = testes.size
        Main.quantidade += (quantity * 10).toString() + ","
        Main.percentual += String.format(Locale.US, "%.2f,", (dado1 / dado2) * 100.0)
        Main.tempo += String.format(Locale.US, "%.2f,", (System.currentTimeMillis().toDouble()-tempo)/1000)
        println(tabela[0][0])
        println(tabela[0].sum())
        Main.precision = Main.precision.mapIndexed{ i, it ->
            it + String.format(Locale.US, "%.2f,", tabela[i][i].toDouble()/columnSum(i))
        }.toTypedArray()
        Main.recall = Main.recall.mapIndexed{ i, it ->
            it + String.format(Locale.US, "%.2f,", tabela[i][i].toDouble()/tabela[i].sum())
        }.toTypedArray()
            println("Sucesso: " + (dado1 / dado2) * 100.0 + "%")
        println("Tempo de teste: " + (System.currentTimeMillis() - tempo) / 1000 + "s")
        println("Encerrado\n")
    }

    private fun processTestCases(): Int {
        var atual = 0
        for (teste in this.testes) {
            val vizinhos = this.acharVizinhoMaisProximo(teste)
            val vizinho = vizinhoMaisProximo(vizinhos)
            if (teste.label == vizinho) {
                atual++
            }
            tabela[teste.label.code - 48][vizinho.code - 48]++
        }
        return atual
    }

    private fun printTabela() {
        for (i in tabela.indices) {
            print("\n|\t")
            for (j in tabela[i].indices) {
                print(tabela[i][j].toString() + "\t|\t")
            }
            println()
        }
    }

    abstract fun acharVizinhoMaisProximo(imagem: Image): Array<Image>
}