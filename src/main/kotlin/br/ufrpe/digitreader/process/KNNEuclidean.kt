package br.ufrpe.digitreader.process

import br.ufrpe.digitreader.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.pow
import kotlin.math.sqrt

class KNNEuclidean(images: Array<Image>, kn: Int, quantity: Int): KNN(images, kn, quantity) {
    private val lock = ReentrantLock()

    override fun findNearestNeighbor(image: Image): Array<Image> {
        val knn: Array<Image?> = arrayOfNulls(super.kNeighbors)
        val references = Array(kNeighbors) { Double.MAX_VALUE }
        runBlocking {
            var maxDistance = Double.MAX_VALUE
            super.trainingSet.chunked(100).forEach { chunk: List<Image> ->
                launch(Dispatchers.Default) {
                    chunk.forEach { trainingImage: Image ->
                        val distance = calculateEuclideanDistance(image, trainingImage)
                        if(distance < maxDistance){
                            maxDistance = addElement(knn, references, trainingImage, distance)
                        }
                    }
                }
            }
        }
        return knn.requireNoNulls()
    }

    private fun calculateEuclideanDistance(image: Image, trainingImage: Image): Double {
        var sum = 0.0
        for (i in 0..27) {
            for (j in 0..27) {
                sum += (image.image[i][j].toUByte().toInt() - trainingImage.image[i][j].toUByte().toInt()).toDouble()
                    .pow(2)
            }
        }
        return sqrt(sum)
    }
    private fun addElement(knn: Array<Image?>, references: Array<Double>, image: Image, distance: Double): Double {
        lock.lock()
        var maxIndex = 0
        var max = references[0]
        try {
            for (i in 1 until references.size) {
                if (references[i] > max) {
                    max = references[i]
                    maxIndex = i
                }
            }

            references[maxIndex] = distance
            knn[maxIndex] = image
            max = references[0]
            for (i in 1 until references.size) {
                if (references[i] > max) {
                    max = references[i]
                }
            }
        } finally {
            lock.unlock()
        }

        return max
    }
}