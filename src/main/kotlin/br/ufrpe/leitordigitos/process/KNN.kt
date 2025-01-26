package br.ufrpe.leitordigitos.process

import br.ufrpe.leitordigitos.Imagem
import br.ufrpe.leitordigitos.repository.ImageRepository

abstract class KNN(repository: ImageRepository, val kn: Int, qnt: Int) {
    val porcentagem = 0.66
    lateinit var treino: Array<Imagem>
    lateinit var teste: Array<Imagem>
    var tabela: Array<IntArray> = Array(10) { Array(10) {0} }
}