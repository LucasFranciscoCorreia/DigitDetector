package br.ufrpe.digitreader.process

import br.ufrpe.digitreader.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A class that implements the k-nearest neighbors (KNN) algorithm using Euclidean distance.
 *
 * This class extends the KNN class and provides an implementation of the findNearestNeighbor method
 * that uses coroutines to parallelize the computation of distances between the input image and the
 * images in the training set. The training set is processed in chunks to improve performance.
 *
 * @param images An array of Image objects representing the training set.
 * @param kn The number of nearest neighbors to find.
 * @param quantity The number of images in the training set.
 */
class KNNEuclidean(images: Array<Image>, kn: Int, quantity: Int): KNN(images, kn, quantity) {
    /**
     * A lock used to ensure thread safety when accessing shared resources.
     */
    private val lock = ReentrantLock()

    /**
     * Finds the nearest neighbors to the given image using the k-nearest neighbors algorithm with Euclidean distance.
     *
     * This method uses coroutines to parallelize the computation of distances between the input image and the images
     * in the training set. The training set is processed in chunks to improve performance.
     *
     * @param image The image for which the nearest neighbors are to be found.
     * @return An array of the nearest neighbor images.
     */
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

    /**
     * Calculates the Euclidean distance between two images.
     *
     * This function computes the Euclidean distance between the pixel values of two images.
     * The images are assumed to be 28x28 pixels in size.
     *
     * @param image The first image to compare.
     * @param trainingImage The second image to compare.
     * @return The Euclidean distance between the two images.
     */
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

    /**
     * Adds an element to the k-nearest neighbors (KNN) array and updates the reference distances.
     *
     * This function locks the critical section to ensure thread safety while modifying the KNN array
     * and the reference distances. It finds the maximum distance in the references array, replaces
     * the element at that position with the new image and distance, and then recalculates the maximum
     * distance in the updated references array.
     *
     * @param knn An array of Image objects representing the k-nearest neighbors.
     * @param references An array of Double values representing the distances of the k-nearest neighbors.
     * @param image The new Image object to be added to the KNN array.
     * @param distance The distance of the new Image object to be added.
     * @return The maximum distance in the updated references array.
     */
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