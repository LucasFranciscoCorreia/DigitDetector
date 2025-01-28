package br.ufrpe.digitreader

import java.io.Serializable

class Image(val image: Array<Array<Byte>>, val label: Char) : Serializable