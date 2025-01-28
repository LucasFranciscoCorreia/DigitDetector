package br.ufrpe.digitreader.process

import br.ufrpe.digitreader.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.abs

class KNNManhattan(images: Array<Image>, kn: Int, quantity: Int) : KNN(images, kn, quantity) {
    private val lock: Lock = ReentrantLock()

    override fun findNearestNeighbor(image: Image): Array<Image> {
        val knn = arrayOfNulls<Image>(super.kNeighbors)
        val references = Array(super.kNeighbors) { Long.MAX_VALUE }

        runBlocking {
            var maxDist = Long.MAX_VALUE
            super.trainingSet.chunked(100).forEach { chunk: List<Image> ->
                launch(Dispatchers.Default) {
                    chunk.forEach { trainingImage: Image ->
                        var count: Long = 0

                        for (i in 0 until 28) {
                            for (j in 0 until 28) {
                                count += abs(
                                    (image.image[i][j].toUByte().toInt() - trainingImage.image[i][j].toUByte().toInt())
                                ).toLong()
                            }
                        }

                        if (count < maxDist) {
                            maxDist = addElement(knn, references, trainingImage, count)
                        }

                    }
                }
            }
        }
        if (knn.any { it == null }) {
            throw IllegalStateException("knn array contains null elements")
        }
        return knn.requireNoNulls()
    }

    private fun addElement(knn: Array<Image?>, references: Array<Long>, image: Image, count: Long): Long {
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
            references[maxIndex] = count
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