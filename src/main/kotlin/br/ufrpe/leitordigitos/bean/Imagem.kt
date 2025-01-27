package br.ufrpe.leitordigitos

import java.io.Serializable

class Imagem(val imagem: Array<Array<Byte>>, val label: Char) : Serializable