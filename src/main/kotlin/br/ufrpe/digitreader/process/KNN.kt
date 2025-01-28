package br.ufrpe.leitordigitos.process

import br.ufrpe.digitreader.Image
import br.ufrpe.leitordigitos.Main
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

abstract class KNN(private val imgs: Array<Image>, val kNeighbors: Int, val quantity: Int) {
    private val percentage = 0.90
    var trainingSet: List<Image> = ArrayList()
    private val rand = Random(System.currentTimeMillis())
    private var testSet: List<Image> = ArrayList()
    private var table: Array<Array<Int>> = Array(10) { Array(10) {0} }

    private fun prepareTrainingAndTestSets() {
        println("Preparing training and test cases: ")
        val trainingList = ArrayList<Image>()
        val testList = ArrayList<Image>()
        var data = ArrayList<Image>()
        data.add(imgs[0])
        for (i in 1..59999) {
            data.add(imgs[i])
            if (imgs[i - 1].label != imgs[i].label || i == 59999) {
                println(data.size)
                if (i != 59999) {
                    data.removeAt(data.size - 1)
                }
                prepareData(trainingList, testList, data)
                data.clear()
                data.add(imgs[i])
            }
        }
        this.trainingSet = trainingList
        this.testSet = testList
        println("Training and test cases ready\n")
    }

    private fun prepareData(training: ArrayList<Image>, test: ArrayList<Image>, data: ArrayList<Image>) {
        var count = 0
        while (count < quantity * percentage) {
            val num = rand.nextInt(data.size)
            training.add(data[num])
            data.removeAt(num)
            count++
        }
        while (count < quantity) {
            val num = rand.nextInt(data.size)
            test.add(data[num])
            data.removeAt(num)
            count++
        }
    }

    private fun nearestNeighbor(neighbors: Array<Image>): Char {
        val res = HashMap<Char, Int>()
        for (neighbor in neighbors) {
            res[neighbor.label] = res.getOrDefault(neighbor.label, 0) + 1
        }
        val max = res.values.maxOrNull() ?: 0
        val candidates = res.filterValues { it == max }.keys.toList()
        val rand = Random(System.currentTimeMillis())
        return candidates[rand.nextInt(candidates.size)]
    }

    private fun columnSum(i: Int): Int {
        var sum = 0
        for (j in table.indices) {
            sum += table[j][i]
        }
        return sum
    }

    fun start() {
        println("Starting KNN: ")
        val startTime = System.currentTimeMillis().toDouble()
        table = Array(10) { Array(10) {0} }
        prepareTrainingAndTestSets()
        val correct = processTestCases()
        printTable()
        println("$correct/${this.testSet.size}")
        val correctDouble = correct.toDouble()
        val testSize = testSet.size
        Main.quantity += (quantity * 10).toString() + ","
        Main.percentage += String.format(Locale.US, "%.2f,", (correctDouble / testSize) * 100.0)
        Main.time += String.format(Locale.US, "%.2f,", (System.currentTimeMillis().toDouble() - startTime) / 1000)
        println(table[0][0])
        println(table[0].sum())
        Main.precision = Main.precision.mapIndexed { i, it ->
            it + String.format(Locale.US, "%.2f,", table[i][i].toDouble() / columnSum(i))
        }.toTypedArray()
        Main.recall = Main.recall.mapIndexed { i, it ->
            it + String.format(Locale.US, "%.2f,", table[i][i].toDouble() / table[i].sum())
        }.toTypedArray()
        println("Success: " + (correctDouble / testSize) * 100.0 + "%")
        println("Test time: " + (System.currentTimeMillis() - startTime) / 1000 + "s")
        println("Finished\n")
    }

    private fun processTestCases(): Int {
        var correct = 0
        for (test in this.testSet) {
            val neighbors = this.findNearestNeighbor(test)
            val neighbor = nearestNeighbor(neighbors)
            if (test.label == neighbor) {
                correct++
            }
            table[test.label.code - 48][neighbor.code - 48]++
        }
        return correct
    }

    private fun printTable() {
        for (i in table.indices) {
            print("\n|\t")
            for (j in table[i].indices) {
                print(table[i][j].toString() + "\t|\t")
            }
            println()
        }
    }

    abstract fun findNearestNeighbor(image: Image): Array<Image>
}
