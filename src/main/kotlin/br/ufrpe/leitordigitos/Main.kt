package br.ufrpe.leitordigitos

import br.ufrpe.leitordigitos.process.KNN
import br.ufrpe.leitordigitos.process.KNNCosseno
import br.ufrpe.leitordigitos.process.KNNEuclidiana
import br.ufrpe.leitordigitos.process.KNNManhattan
import jdk.internal.org.jline.utils.Colors
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


class Main {
    companion object {
        var quantidade: String = "quantidade <- c("
        var percentual: String = "porcentagem <- c("

        fun pegarImagens(): Array<Imagem> {
            val reader = ObjectInputStream(BufferedInputStream(GZIPInputStream(FileInputStream("src/main/resources/br/ufrpe/leitordigitos/digitos.bin"))))
            println("reading...")
            val imgs: Array<Imagem> = reader.readObject() as Array<Imagem>
            println("read")
            reader.close()
            return imgs
        }
    }
}


fun main() {
    val imgs: Array<Imagem> = Main.pegarImagens()
    val writer = BufferedWriter(FileWriter("grafico.r"))
    var i = 0
    var j = 0
    val kn = 5
    var qnt = 100
    do {
        do {
            println("$j-$i")
            //val knn: KNN = KNNManhattan(imgs, kn, qnt)
            //val knn: KNN = KNNEuclidiana(imgs, kn, qnt)
            val knn:KNN = KNNCosseno(imgs, kn, qnt)
            knn.start()
        } while (++i < 30)
        i = 0
        qnt += 100
    } while (++j < 10)
    writer.write(Main.quantidade.substring(0, Main.quantidade.length - 1) + ")")
    writer.newLine()
    writer.write(Main.percentual.substring(0, Main.percentual.length - 1) + ")")
    writer.newLine()
    writer.write("plot(porcentagem~quantidade)")
    writer.flush()
    writer.close()
}
