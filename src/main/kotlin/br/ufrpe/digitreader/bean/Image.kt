/**
 * Represents an image with its corresponding label.
 *
 * @property image A 2D array of bytes representing the pixel values of the image.
 * @property label A character representing the label of the image.
 */
package br.ufrpe.digitreader

import java.io.Serializable

class Image(val image: Array<Array<Byte>>, val label: Char) : Serializable