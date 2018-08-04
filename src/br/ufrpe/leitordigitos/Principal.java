package br.ufrpe.leitordigitos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Principal {
	public static int atual;
	static Imagem img[] = new Imagem[60000];
	static double porcentagem = 0.66;
	static Imagem treino[];
	static Imagem teste[];
	static int KNN = 1;
	static int qnt = 100;
	static Random rand = new Random();
	static int tabela[][] = new int[10][10];
	static String quantidade = "quantidade <- c(";
	static String percentual = "porcentagem <- c(";
	public static void main(String[] args) throws IOException {
		pegarImagensArff();
		int i = 0;
		int j = 0;
		do {
			BufferedWriter writer = new BufferedWriter(new FileWriter("grafico.r"));
			do {
				System.out.println(j+"-"+i);
				prepararTreinoETeste();
				realizarKNNManhattan();
				//		realizarKNNEuclidiana();
				//		realizarKNNCosseno();
			}while(++i < 10);
			i = 0;
			qnt+= 100;
			writer.write(quantidade.substring(0, quantidade.length()-1) + ")");
			writer.newLine();
			writer.write(percentual.substring(0, percentual.length()-1) + ")");
			writer.newLine();
			writer.write("plot(porcentagem~quantidade)");
			writer.flush();
			writer.close();
		}while(++j < 8);
	}

	private static void realizarKNNCosseno() {
		System.out.println("Iniciando KNN com distancia Cosseno: ");
		atual = 0;
		int testes = teste.length;
		double tempo = System.currentTimeMillis();
		for(int i = 0; i < testes;i++) {
			Imagem vizinhos[] = acharVizinhosMaisProximosCosseno(teste[i]);
			char vizinho = vizinhoMaisProximo(vizinhos);
			if(teste[i].getLabel() == vizinho) {
				atual++;
			}
			tabela[teste[i].getLabel()-48][vizinho-48]++;
		}
		for(int i = 0 ; i < tabela.length;i++) {
			System.out.print("\n|\t");
			for(int j = 0; j < tabela[i].length;j++) {
				System.out.print(tabela[i][j] + "\t|\t");
				tabela[i][j] = 0;
			}
			System.out.println();
		}
		System.out.println(atual +"/"+testes);
		double dado1 = atual;
		double dado2 = testes;
		System.out.println("Sucesso: " + (dado1/dado2)*100.0 + "%");
		System.out.println("Tempo de teste: " + (System.currentTimeMillis()-tempo)/1000 +"s");
		System.out.println("Encerrado\n");
	}

	private static Imagem[] acharVizinhosMaisProximosCosseno(Imagem imagem) {
		double menorDist = Double.MIN_VALUE;
		Imagem knn[] = new Imagem[KNN];
		double referencias[] = new double[KNN];
		for(int i = 0; i < KNN;i++) {
			referencias[i] = -1;
		}
		byte referencia[][] = imagem.getImagem();
		for(int k = 0; k < treino.length;k++) {
			byte teste[][] = treino[k].getImagem();
			double cont = 0;
			double x = 0, y = 0, xy = 0;
			for(int i = 0; i < 28;i++) {
				for(int j = 0; j< 28;j++) {
					x += (Byte.toUnsignedInt(referencia[i][j])*Byte.toUnsignedInt(referencia[i][j]));
					y += (Byte.toUnsignedInt(teste[i][j])*(Byte.toUnsignedInt(teste[i][j])));
					xy += Byte.toUnsignedInt(referencia[i][j])*Byte.toUnsignedInt(teste[i][j]);
				}
			}
			x = Math.sqrt(x);
			y = Math.sqrt(y);
			cont = xy/(x*y);
			if(cont > menorDist) {
				menorDist = adicionarElementoCos(knn, referencias, treino[k], cont);
			}
		}
		return knn;
	}

	private static double adicionarElementoCos(Imagem[] knn, double[] referencias, Imagem imagem, double cont) {
		int menorI = 0;
		double menor = referencias[0];
		for(short i = 1; i < referencias.length;i++) {
			if(referencias[i] < menor) {
				menor = referencias[i];
				menorI = i;
			}
		}
		referencias[menorI] = (cont);
		knn[menorI] = imagem;
		menor = referencias[0];
		for(short i = 1; i < referencias.length;i++) {
			if(referencias[i] < menor) {
				menor = referencias[i];
			}
		}
		return menor;
	}

	private static void realizarKNNEuclidiana() {
		System.out.println("Iniciando KNN com distancia Euclidiana: ");
		atual = 0;
		int testes = teste.length;
		double tempo = System.currentTimeMillis();
		for(int i = 0; i < testes;i++) {
			Imagem vizinhos[] = acharVizinhosMaisProximosEuclidiana(teste[i]);
			char vizinho = vizinhoMaisProximo(vizinhos);
			if(teste[i].getLabel() == vizinho) {
				atual++;
			}
			tabela[teste[i].getLabel()-48][vizinho-48]++;
		}
		for(int i = 0 ; i < tabela.length;i++) {
			System.out.print("\n|\t");
			for(int j = 0; j < tabela[i].length;j++) {
				System.out.print(tabela[i][j] + "\t|\t");
				tabela[i][j] = 0;
			}
			System.out.println();
		}
		System.out.println(atual +"/"+testes);
		double dado1 = atual;
		double dado2 = testes;
		System.out.println("Sucesso: " + (dado1/dado2)*100.0 + "%");
		System.out.println("Tempo de teste: " + (System.currentTimeMillis()-tempo)/1000 +"s");
		System.out.println("Encerrado\n");
	}

	private static Imagem[] acharVizinhosMaisProximosEuclidiana(Imagem imagem) {
		double maiorDist = Double.MAX_VALUE;
		Imagem knn[] = new Imagem[KNN];
		double referencias[] = new double[KNN];
		for(int i = 0; i < KNN;i++) {
			referencias[i] = Double.MAX_VALUE;
		}
		byte referencia[][] = imagem.getImagem();
		for(int k = 0; k < treino.length;k++) {
			byte teste[][] = treino[k].getImagem();
			double cont = 0;
			for(int i = 0; i < 28;i++) {
				for(int j = 0; j< 28;j++) {
					cont += Math.pow(Byte.toUnsignedInt(referencia[i][j])-Byte.toUnsignedInt(teste[i][j]),2);
				}
			}
			cont = Math.sqrt(cont);
			if(cont < maiorDist) {
				maiorDist = adicionarElemento(knn, referencias, treino[k], cont);
			}
		}
		return knn;
	}

	private static double adicionarElemento(Imagem[] knn, double[] referencias, Imagem imagem, double cont) {
		int maiorI = 0;
		double maior = referencias[0];
		for(short i = 1; i < referencias.length;i++) {
			if(referencias[i] > maior) {
				maior = referencias[i];
				maiorI = i;
			}
		}
		referencias[maiorI] = (long) Math.floor(cont);
		knn[maiorI] = imagem;
		maior = referencias[0];
		for(short i = 1; i < referencias.length;i++) {
			if(referencias[i] > maior) {
				maior = referencias[i];
			}
		}
		return maior;
	}

	private static long adicionarElemento(Imagem[] knn01, long[] referencias, Imagem imagem, long cont) {
		int maiorI = 0;
		long maior = referencias[0];
		for(short i = 1; i < referencias.length;i++) {
			if(referencias[i] > maior) {
				maior = referencias[i];
				maiorI = i;
			}
		}
		referencias[maiorI] = cont;
		knn01[maiorI] = imagem;
		maior = referencias[0];
		for(short i = 1; i < referencias.length;i++) {
			if(referencias[i] > maior) {
				maior = referencias[i];
			}
		}
		return maior;
	}

	private static void realizarKNNManhattan() {
		System.out.println("Iniciando KNN com distancia Manhattan: ");
		atual = 0;
		int testes = teste.length;
		double tempo = System.currentTimeMillis();
		for(int i = 0; i < testes;i++) {
			Imagem vizinhos[] = acharVizinhosMaisProximosManhattan(teste[i]);
			char vizinho = vizinhoMaisProximo(vizinhos);
			if(teste[i].getLabel() == vizinho) {
				atual++;
			}
			//System.out.print(vizinho + " ");
			//tabela[teste[i].getLabel()-48][vizinho-48]++;
		}
		//		for(int i = 0 ; i < tabela.length;i++) {
		//			System.out.print("\n|\t");
		//			for(int j = 0; j < tabela[i].length;j++) {
		//				System.out.print(tabela[i][j] + "\t|\t");
		//				tabela[i][j] = 0;
		//			}
		//			System.out.println();
		//		}
		System.out.println(atual +"/"+testes);
		double dado1 = atual;
		double dado2 = testes;
		quantidade += (qnt*10)+",";
		percentual += (dado1/dado2)*100.0+",";
		System.out.println("Sucesso: " + (dado1/dado2)*100.0 + "%");
		System.out.println("Tempo de teste: " + (System.currentTimeMillis()-tempo)/1000 +"s");
		System.out.println("Encerrado\n");
	}

	private static char vizinhoMaisProximo(Imagem[] vizinhos) {
		byte res[] = new byte[10];
		for(int i = 0; i < vizinhos.length;i++) {
			res[vizinhos[i].getLabel()-48]++;
		}
		int maior = 0;
		for(int i = 0; i < 10;i++) {
			if(res[i] > maior) {
				maior = res[i];
			}
		}
		int qnt = 0;
		for(int i = 0; i < 10;i++) {
			if(maior == res[i]) {
				qnt++;
			}
		}
		int k = rand.nextInt(qnt);
		int cont = 0;
		for(int i = 0; i < 10;i++) {
			if(res[i] == maior && cont < k) {
				cont++;
			}else if(res[i] == maior && cont == k){
				return (char) (i+48);
			}
		}
		return ' ';
	}

	private static Imagem[] acharVizinhosMaisProximosManhattan(Imagem imagem) {
		long maiorDist = Long.MAX_VALUE;
		Imagem knn[] = new Imagem[KNN];
		long referencias[] = new long[KNN];
		for(int i = 0; i < KNN;i++) {
			referencias[i] = Long.MAX_VALUE;
		}
		byte referencia[][] = imagem.getImagem();
		for(int k = 0; k < treino.length;k++) {
			byte teste[][] = treino[k].getImagem();
			long cont = 0;
			for(int i = 0; i < 28;i++) {
				for(int j = 0; j< 28;j++) {
					cont += Math.abs(Byte.toUnsignedInt(referencia[i][j])-Byte.toUnsignedInt(teste[i][j]));
				}
			}


			if(cont < maiorDist) {
				maiorDist = adicionarElemento(knn, referencias, treino[k], cont);
			}
		}
		return knn;
	}

	private static void prepararTreinoETeste() {
		System.out.println("Preparar casos de treino e teste: ");
		ArrayList<Imagem> treino = new ArrayList<>();
		ArrayList<Imagem> teste = new ArrayList<>();
		ArrayList<Imagem> dados = new ArrayList<>();
		dados.add(img[0]);
		for(int i = 1; i < 60000; i++) {
			dados.add(img[i]);
			if(img[i-1].getLabel() != img[i].getLabel() || i == 59999) {
				if(i != 59999) {
					dados.remove(dados.size()-1);					
				}
				prepararDados(treino,teste, dados);
				dados = new ArrayList<>();
				dados.add(img[i]);
			}
		}
		Collections.shuffle(treino);
		Collections.shuffle(teste);
		Principal.treino = (Imagem[]) treino.toArray(new Imagem[treino.size()]);
		Principal.teste = (Imagem[]) teste.toArray(new Imagem[teste.size()]);
		System.out.println("Casos de treino e teste prontos\n");
	}

	private static void prepararDados(ArrayList<Imagem> treino, ArrayList<Imagem> testes, ArrayList<Imagem> dados) {
		int cont = 0;
		while(cont < qnt*porcentagem) {
			int num = rand.nextInt(dados.size());
			treino.add(dados.get(num));
			dados.remove(num);
			Collections.shuffle(dados);
			cont++;
		}
		while(cont < qnt) {
			int num = rand.nextInt(dados.size());
			testes.add(dados.get(num));
			dados.remove(num);
			Collections.shuffle(dados);
			cont++;
		}
	}

	private static void pegarImagensArff() throws IOException {
		System.out.println("Iniciando leitura do arquivo: ");
		double tempo = System.currentTimeMillis();
		BufferedReader reader = new BufferedReader(new FileReader("digitos.arff"));
		byte imagem[][];
		while(!reader.readLine().equals("@data"));
		for(int k = 0; k < 60000;k++) {
			String linha[] = reader.readLine().split(",");
			imagem = new byte[28][28];
			for(int i= 0; i < 28;i++) {
				for(int j = 0; j < 28;j++) {
					try {
						imagem[i][j] = Byte.parseByte(linha[28*i+j]);											
					}catch(NumberFormatException e) {
						int s = Integer.parseInt(linha[28*i+j]);
						imagem[i][j] = (byte) (s & ((byte) 0xFF));
					}
				}
			}
			img[k] = new Imagem(imagem, linha[linha.length-1].charAt(0));
		}
		reader.close();
		System.out.println("Tempo de leitura do arquivo: " + (System.currentTimeMillis()-tempo)/1000+"s");
	}
}