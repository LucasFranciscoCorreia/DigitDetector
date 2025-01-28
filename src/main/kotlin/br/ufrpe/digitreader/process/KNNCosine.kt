package br.ufrpe.digitreader.process

import br.ufrpe.digitreader.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.sqrt

class KNNCosseno(images: Array<Image>, kNeighbors: Int, quantity: Int): KNN(images, kNeighbors, quantity) {
    override fun findNearestNeighbor(image: Image): Array<Image> {
        var smallestDistance = -1.0
        val knn = arrayOfNulls<Image>(super.kNeighbors)
        val references = Array(super.kNeighbors) {-1.0}
        runBlocking {
            super.trainingSet.toList().chunked(100).forEach { chunk: List<Image> ->
                launch(Dispatchers.Default) {
                    chunk.forEach { trainingImage: Image ->
                        var x = 0.0
                        var y = 0.0
                        var xy = 0.0
                        for (i in 0..27) {
                            for (j in 0..27) {
                                x += (trainingImage.image[i][j].toUByte().toInt() * trainingImage.image[i][j].toUByte().toInt()).toDouble()
                                y += (image.image[i][j].toUByte().toInt() * image.image[i][j].toUByte().toInt()).toDouble()
                                xy += (trainingImage.image[i][j].toUByte().toInt() * image.image[i][j].toUByte().toInt()).toDouble()
                            }
                        }
                        x = sqrt(x)
                        y = sqrt(y)
                        val cosineSimilarity = if (x != 0.0 && y != 0.0) xy / (x * y) else 0.0
                        if (x != 0.0 && y != 0.0 && cosineSimilarity > smallestDistance) {
                            smallestDistance = addElement(knn, references, trainingImage, cosineSimilarity)
                        }
                    }
                }
            }
        }
        return knn.requireNoNulls()
    }
    private val lock = ReentrantLock()

    private fun addElement(knn: Array<Image?>, references: Array<Double>, image: Image, cosineSimilarity: Double): Double {
        lock.lock()
        var smallestIndex = 0
        var smallest = references[0]
        try {
            for (i in 1 until references.size) {
                if (references[i] < smallest) {
                    smallest = references[i]
                    smallestIndex = i
                }
            }
            references[smallestIndex] = cosineSimilarity
            knn[smallestIndex] = image
            smallest = references[0]
            for (i in 1 until references.size) {
                if (references[i] < smallest) {
                    smallest = references[i]
                }
            }
        } finally {
            lock.unlock()
        }
        return smallest
    }
}