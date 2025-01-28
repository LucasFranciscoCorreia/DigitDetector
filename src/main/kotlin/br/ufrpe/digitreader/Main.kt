package br.ufrpe.digitreader

import br.ufrpe.digitreader.process.KNN
import br.ufrpe.digitreader.process.KNNCosine
import br.ufrpe.digitreader.process.KNNEuclidean
import br.ufrpe.digitreader.process.KNNManhattan
import java.io.*
import java.util.zip.GZIPInputStream


/**
 * Main class containing companion object with variables and functions for digit recognition.
 */
class Main {
    companion object {
        /**
         * A variable that holds a string representing a quantity in R syntax.
         * The string is initialized with "quantity <- c(" which is the beginning
         * of an R command to create a vector named 'quantity'.
         */
        var quantity: String = "quantity <- c("

        /**
         * A variable to store a string representation of a percentage in R syntax.
         * The string starts with "percentage <- c(" which is used to assign a vector of values to the variable 'percentage' in R.
         */
        var percentage: String = "percentage <- c("
        
        /**
         * Variable to store a string representation of a time series in R language format.
         * The initial value is set to "time <- c(" which is the beginning of an R command
         * to create a vector named 'time'.
         */
        var time: String = "time <- c("
        
        /**
         * An array of strings representing precision values for different classes.
         * Each element in the array corresponds to a precision value for a specific class (0-9).
         * The format of each string is "precision.<class> <- c(", where <class> is the class number.
         */
        var precision: Array<String> = arrayOf(
            "precision.0 <- c(",
            "precision.1 <- c(",
            "precision.2 <- c(",
            "precision.3 <- c(",
            "precision.4 <- c(",
            "precision.5 <- c(",
            "precision.6 <- c(",
            "precision.7 <- c(",
            "precision.8 <- c(",
            "precision.9 <- c(",
        )
        
        /**
         * An array of strings representing recall values for digits 0 through 9.
         * Each element in the array is a string that initializes a recall variable for a specific digit.
         * 
         * Example:
         * - "recall.0 <- c(" initializes the recall variable for digit 0.
         * - "recall.1 <- c(" initializes the recall variable for digit 1.
         * - and so on up to digit 9.
         */
        var recall: Array<String> = arrayOf(
            "recall.0 <- c(",
            "recall.1 <- c(",
            "recall.2 <- c(",
            "recall.3 <- c(",
            "recall.4 <- c(",
            "recall.5 <- c(",
            "recall.6 <- c(",
            "recall.7 <- c(",
            "recall.8 <- c(",
            "recall.9 <- c(",
        )

        /**
         * Fetches images from a binary file.
         *
         * This function reads a compressed binary file containing an array of `Image` objects.
         * It uses a `GZIPInputStream` to decompress the file and an `ObjectInputStream` to read the objects.
         *
         * @return An array of `Image` objects read from the binary file.
         * @throws IOException If an I/O error occurs while reading the file.
         * @throws ClassNotFoundException If the class of a serialized object cannot be found.
         */
        fun fetchImages(): Array<Image> {
            val reader = ObjectInputStream(BufferedInputStream(GZIPInputStream(FileInputStream("src/main/resources/br/ufrpe/digitreader/digits.bin"))))
            println("reading...")
            val imgs: Array<Image> = reader.readObject() as Array<Image>
            println("read")
            reader.close()
            return imgs
        }

    }
}


/**
 * The main function of the digit recognition application.
 * This function fetches images, initializes a KNN classifier, and performs classification.
 * The results are written to a file named "chart.r".
 *
 * The function performs the following steps:
 * 1. Fetches images using `Main.fetchImages()`.
 * 2. Initializes a `BufferedWriter` to write results to "chart.r".
 * 3. Sets initial values for loop counters and parameters.
 * 4. Loops to perform KNN classification with different quantities of images.
 * 5. Writes the results (quantity, percentage, time, precision, recall) to the file.
 * 6. Generates an R plot command to visualize the results.
 *
 * Note: The KNN classifier used is `KNNCosine`. Other classifiers (`KNNManhattan`, `KNNEuclidean`) are commented out.
 */
fun main() {
    val imgs: Array<Image> = Main.fetchImages()
    val writer = BufferedWriter(FileWriter("chart.r"))
    var i = 0
    var j = 0
    val kn = 5
    var qnt = 100
    do {
        //val knn: KNN = KNNManhattan(imgs, kn, qnt)
        //val knn: KNN = KNNEuclidean(imgs, kn, qnt)
        val knn:KNN = KNNCosine(imgs, kn, qnt)
        do {
            println("$j-$i")
            knn.start()
        } while (++i < 30)
        i = 0
        qnt += 100
    } while (++j < 10)
    writer.write(Main.quantity.substring(0, Main.quantity.length - 1) + ")")
    writer.newLine()
    writer.write(Main.percentage.substring(0, Main.percentage.length - 1) + ")")
    writer.newLine()
    writer.write(Main.time.substring(0, Main.time.length - 1) + ")")
    writer.newLine()
    Main.precision.forEach{
        writer.write(it.substring(0, it.length-1)+')')
        writer.newLine()
    }
    Main.recall.forEach{
        writer.write(it.substring(0, it.length-1)+')')
        writer.newLine()
    }
    writer.flush()
    writer.close()
}
