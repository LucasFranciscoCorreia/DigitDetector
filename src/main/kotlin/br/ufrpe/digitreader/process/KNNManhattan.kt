package br.ufrpe.digitreader.process

import br.ufrpe.digitreader.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.abs
/**
 * A class that implements the k-nearest neighbors (KNN) algorithm using the Manhattan distance metric.
 *
 * @param images An array of Image objects representing the training set.
 * @param kn The number of nearest neighbors to find.
 * @param quantity The quantity of images to process.
 */
class KNNManhattan(images: Array<Image>, kn: Int, quantity: Int) : KNN(images, kn, quantity) {
    /**
     * A lock used to ensure thread safety when accessing shared resources.
     */
    private val lock: Lock = ReentrantLock()

    /**
     * Finds the nearest neighbors to the given image using the Manhattan distance metric.
     *
     * This method uses the k-nearest neighbors (KNN) algorithm to find the closest images
     * in the training set to the provided image. The Manhattan distance is used to measure
     * the similarity between images.
     *
     * @param image The image for which to find the nearest neighbors.
     * @return An array of the nearest neighbor images.
     * @throws IllegalStateException If the knn array contains null elements.
     */
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

    /**
     * Adds an image element to the KNN array and updates the reference counts.
     *
     * This function locks the critical section to ensure thread safety while modifying
     * the KNN array and reference counts. It finds the index of the maximum reference count,
     * replaces the element at that index with the new image, and updates the reference count.
     * Finally, it returns the updated maximum reference count.
     *
     * @param knn An array of Image objects representing the KNN elements.
     * @param references An array of Long values representing the reference counts for each KNN element.
     * @param image The new Image object to be added to the KNN array.
     * @param count The reference count associated with the new image.
     * @return The updated maximum reference count after adding the new image.
     */
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