package br.ufrpe.leitordigitos

import br.ufrpe.digitreader.Image
import br.ufrpe.leitordigitos.process.KNN
import br.ufrpe.leitordigitos.process.KNNCosseno
import br.ufrpe.leitordigitos.process.KNNEuclidiana
import br.ufrpe.leitordigitos.process.KNNManhattan
import java.io.*
import java.util.zip.GZIPInputStream


class Main {
    companion object {
        var quantity: String = "quantidade <- c("
        var percentage: String = "porcentagem <- c("
        var time: String = "tempo <- c("
        var precision: Array<String> = arrayOf(
            "precision.0 <- c(",
            "precision.1 <- c(",
            "precision.2 <- c(",
            "precision.3 <- c(",
            "precision.4 <- c(",
            "precision.5 <- c(",
            "precision.6 <- c(",
            "precision.7 <- c(",
            "precision.8 <- c(",
            "precision.9 <- c(",
        )
        var recall: Array<String> = arrayOf(
            "recall.0 <- c(",
            "recall.1 <- c(",
            "recall.2 <- c(",
            "recall.3 <- c(",
            "recall.4 <- c(",
            "recall.5 <- c(",
            "recall.6 <- c(",
            "recall.7 <- c(",
            "recall.8 <- c(",
            "recall.9 <- c(",
        )

        fun pegarImagens(): Array<Image> {
            val reader = ObjectInputStream(BufferedInputStream(GZIPInputStream(FileInputStream("src/main/resources/br/ufrpe/leitordigitos/digitos.bin"))))
            println("reading...")
            val imgs: Array<Image> = reader.readObject() as Array<Image>
            println("read")
            reader.close()
            return imgs
        }

    }
}


fun main() {
    val imgs: Array<Image> = Main.pegarImagens()
    val writer = BufferedWriter(FileWriter("grafico.r"))
    var i = 0
    var j = 0
    val kn = 5
    var qnt = 100
    do {
        //val knn: KNN = KNNManhattan(imgs, kn, qnt)
        //val knn: KNN = KNNEuclidiana(imgs, kn, qnt)
        val knn:KNN = KNNCosseno(imgs, kn, qnt)
        do {
            println("$j-$i")
            knn.start()
        } while (++i < 1)
        i = 0
        qnt += 100
    } while (++j < 1)
    writer.write(Main.quantity.substring(0, Main.quantity.length - 1) + ")")
    writer.newLine()
    writer.write(Main.percentage.substring(0, Main.percentage.length - 1) + ")")
    writer.newLine()
    writer.write(Main.time.substring(0, Main.time.length - 1) + ")")
    writer.newLine()
    Main.precision.forEach{
        writer.write(it.substring(0, it.length-1)+')')
        writer.newLine()
    }
    Main.recall.forEach{
        writer.write(it.substring(0, it.length-1)+')')
        writer.newLine()
    }
    writer.write("plot(porcentagem~quantidade)")
    writer.flush()
    writer.close()
}
