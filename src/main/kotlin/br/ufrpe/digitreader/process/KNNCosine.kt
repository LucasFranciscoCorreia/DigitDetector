package br.ufrpe.digitreader.process

import br.ufrpe.digitreader.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.sqrt

/**
 * A class that implements the K-Nearest Neighbors (KNN) algorithm using cosine similarity.
 *
 * @param images An array of Image objects representing the training set.
 * @param kNeighbors The number of nearest neighbors to consider.
 * @param quantity The number of images to process.
 */
class KNNCosine(images: Array<Image>, kNeighbors: Int, quantity: Int): KNN(images, kNeighbors, quantity) {
    /**
     * A lock used to ensure thread safety when accessing shared resources.
     * This lock is an instance of `ReentrantLock`, which allows the same thread
     * to acquire the lock multiple times without causing a deadlock.
     */
    private val lock = ReentrantLock()

    /**
     * Finds the nearest neighbors to the given image using cosine similarity.
     *
     * This function calculates the cosine similarity between the given image and each image in the training set.
     * It returns an array of the k-nearest neighbors based on the highest cosine similarity scores.
     *
     * @param image The image for which to find the nearest neighbors.
     * @return An array of the k-nearest neighbor images.
     */
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

    /**
     * Adds an element to the k-nearest neighbors (KNN) array based on cosine similarity.
     *
     * This function finds the smallest cosine similarity value in the references array,
     * replaces it with the new cosine similarity value, and updates the corresponding
     * image in the KNN array. It returns the smallest cosine similarity value after the update.
     *
     * @param knn An array of Image objects representing the k-nearest neighbors.
     * @param references An array of Double values representing the cosine similarities of the KNN.
     * @param image The new Image object to be added to the KNN array.
     * @param cosineSimilarity The cosine similarity value of the new image.
     * @return The smallest cosine similarity value in the references array after the update.
     */
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