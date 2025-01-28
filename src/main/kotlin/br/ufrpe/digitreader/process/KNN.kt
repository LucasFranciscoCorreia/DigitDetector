package br.ufrpe.digitreader.process

import br.ufrpe.digitreader.Image
import br.ufrpe.digitreader.Main
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

/**
 * Abstract class representing the K-Nearest Neighbors (KNN) algorithm.
 *
 * @property imgs An array of Image objects used for training and testing.
 * @property kNeighbors The number of nearest neighbors to consider.
 * @property quantity The total number of images to be used for training and testing.
 */
abstract class KNN(private val imgs: Array<Image>, val kNeighbors: Int, val quantity: Int) {
    /**
     * The percentage value used for some calculation or threshold.
     * This value is set to 90%.
     */
    private val percentage = 0.90

    /**
     * A list of Image objects representing the training set for the K-Nearest Neighbors (KNN) algorithm.
     */
    var trainingSet: List<Image> = ArrayList()
    
    /**
     * A random number generator initialized with the current system time in milliseconds.
     */
    private val rand = Random(System.currentTimeMillis())
    
    /**
     * A list of Image objects representing the test dataset.
     */
    private var testSet: List<Image> = ArrayList()
    
    /**
     * A 2D array representing the confusion matrix with 10 rows and 10 columns, 
     * initialized with zeros.
     */
    private var table: Array<Array<Int>> = Array(10) { Array(10) {0} }

    /**
     * Prepares the training and test sets by iterating through the list of images.
     * It groups images by their labels and then splits them into training and test sets.
     * 
     * The method performs the following steps:
     * 1. Initializes empty lists for training and test sets.
     * 2. Iterates through the list of images.
     * 3. Groups images by their labels.
     * 4. Splits the grouped images into training and test sets.
     * 5. Assigns the prepared training and test sets to the respective class properties.
     * 
     * The method prints the progress and the size of each group of images.
     */
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

    /**
     * Prepares the training and test datasets by randomly selecting images from the provided data list.
     *
     * @param training The list to which the training images will be added.
     * @param test The list to which the test images will be added.
     * @param data The list of images from which the training and test images will be selected.
     *
     * The function first fills the training list with a specified percentage of the total quantity of images.
     * Then, it fills the test list with the remaining images until the total quantity is reached.
     */
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

    /**
     * Determines the most frequent label among the given neighbors and returns it.
     * If there is a tie, a random label from the candidates is chosen.
     *
     * @param neighbors An array of Image objects representing the neighbors.
     * @return The label of the nearest neighbor.
     */
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

    /**
     * Calculates the sum of the elements in a specified column of the table.
     *
     * @param i The index of the column to sum.
     * @return The sum of the elements in the specified column.
     */
    private fun columnSum(i: Int): Int {
        var sum = 0
        for (j in table.indices) {
            sum += table[j][i]
        }
        return sum
    }

    /**
     * Starts the K-Nearest Neighbors (KNN) algorithm process.
     * 
     * This function initializes the confusion matrix, prepares the training and test sets,
     * processes the test cases, and prints the results. It also updates the main statistics
     * such as quantity, percentage, time, precision, and recall.
     * 
     * The function performs the following steps:
     * 1. Initializes the confusion matrix.
     * 2. Prepares the training and test sets.
     * 3. Processes the test cases and calculates the number of correct predictions.
     * 4. Prints the confusion matrix.
     * 5. Updates the main statistics (quantity, percentage, time, precision, and recall).
     * 6. Prints the success rate and test time.
     * 
     * The function uses the current system time to measure the duration of the process.
     * The results are printed to the console and stored in the Main object.
     */
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

    /**
     * Processes the test cases to determine the number of correctly classified instances.
     *
     * This function iterates over the test set, finds the nearest neighbors for each test case,
     * and compares the predicted label with the actual label. It also updates a confusion matrix
     * table with the results.
     *
     * @return The number of correctly classified test cases.
     */
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

    /**
     * Prints the contents of the `table` in a formatted manner.
     * Each row of the table is printed on a new line, with each element
     * separated by tab characters and enclosed within vertical bars.
     */
    private fun printTable() {
        for (i in table.indices) {
            print("\n|\t")
            for (j in table[i].indices) {
                print(table[i][j].toString() + "\t|\t")
            }
            println()
        }
    }

    /**
     * Finds the nearest neighbors of the given image.
     *
     * @param image The image for which to find the nearest neighbors.
     * @return An array of images representing the nearest neighbors.
     */
    abstract fun findNearestNeighbor(image: Image): Array<Image>
}
