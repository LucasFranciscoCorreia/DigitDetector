package br.ufrpe.leitordigitos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Principal {
	public static volatile int atual;
	static Imagem img[] = new Imagem[60000];
	static double porcentagem = 0.9;
	static Imagem treino[];
	static Imagem teste[];
	static int KNN = 3;
	static int qnt = 1000;
	public static void main(String[] args) throws IOException {
		pegarImagensArff();
		prepararTreinoETeste();
		realizarKNN01();
		realizarKNN();
	}
	
	private static void realizarKNN() {
		System.out.println("Iniciando KNN: ");
		atual = 0;
		int testes = teste.length;
		Imagem pt;
		double tempo = System.currentTimeMillis();
		for(int i = 0; i < testes;i++) {
			Imagem vizinhos[] = acharVizinhosMaisProximos(teste[i]);
			char vizinho = vizinhoMaisProximo(vizinhos);
			if(teste[i].getLabel() == vizinho) {
				atual++;
			}
		}
		System.out.println(atual +"/"+testes);
		double dado1 = atual;
		double dado2 = testes;
		System.out.println("Sucesso: " + (dado1/dado2)*100.0 + "%");
		System.out.println("Tempo de teste: " + (System.currentTimeMillis()-tempo)/1000 +"s");
		System.out.println("Encerrado");
	}

	private static char vizinhoMaisProximo(Imagem[] vizinhos) {
		byte res[] = new byte[10];
		for(int i = 0; i < vizinhos.length;i++) {
			res[vizinhos[i].getLabel()-48]++;
		}
		int maior = 0;
		int maiorI = 0;
		for(int i = 0; i < 10;i++) {
			if(res[i] > maior) {
				maior = res[i];
				maiorI = i;
			}
		}
		return (char) (maiorI+48);
	}

	private static Imagem[] acharVizinhosMaisProximos(Imagem imagem) {
		short menorReferencia = 0;
		Imagem knn01[] = new Imagem[KNN];
		short referencias[] = new short[KNN];
		byte referencia[][] = imagem.getImagem();
		for(int k = 0; k<treino.length;k++) {
			byte teste[][] = treino[k].getImagem();
			short cont = 0;
			for(int i = 0; i < 28;i++) {
				for(int j = 0; j < 28;j++) {
					if(referencia[i][j] == teste[i][j]) {
						cont++;
					}
				}
			}
			if(cont > menorReferencia) {
				menorReferencia = adicionarElemento(knn01, referencias, treino[k],cont);
			}
		}
		return knn01;
	}

	private static void prepararTreinoETeste() {
		System.out.println("Preparar casos de treino e teste: ");
		ArrayList<Imagem> treino = new ArrayList<>();
		ArrayList<Imagem> teste = new ArrayList<>();
		int min = 0;
		for(int i = 1; i < 60000; i++) {
			if(img[i-1].getLabel() != img[i].getLabel() || i == 59999) {
				int j;
				double t = (qnt*porcentagem);
				t += min;
				int end = (int) Math.floor(t);
				for(j = min; j < end;j++) {
					treino.add(img[j]);
				}
				end = min+qnt;
				
				for(;j < end;j++) {
					teste.add(img[j]);
				}
				min = i;
			}
		}
		Principal.treino = (Imagem[]) treino.toArray(new Imagem[treino.size()]);
		Principal.teste = (Imagem[]) teste.toArray(new Imagem[teste.size()]);
		System.out.println("Casos de treino e teste prontos\n");
	}

	private static void realizarKNN01() {
		System.out.println("Iniciando KNN com valoras diferentes de 0 como 1: ");
		atual = 0;
		int testes = teste.length;
		Imagem pt;
		double tempo = System.currentTimeMillis();
		for(int i = 0; i < testes;i++) {
			Imagem vizinhos[] = acharVizinhosMaisProximos01(teste[i]);
			char vizinho = vizinhoMaisProximo(vizinhos);
			if(teste[i].getLabel() == vizinho) {
				atual++;
			}
		}
		System.out.println(atual +"/"+testes);
		double dado1 = atual;
		double dado2 = testes;
		System.out.println("Sucesso: " + (dado1/dado2)*100.0 + "%");
		System.out.println("Tempo de teste: " + (System.currentTimeMillis()-tempo)/1000 +"s");
		System.out.println("Encerrado\n");
	}

	private static Imagem[] acharVizinhosMaisProximos01(Imagem img) {
		short menorReferencia = 0;
		Imagem knn01[] = new Imagem[KNN];
		short referencias[] = new short[KNN];
		byte referencia[][] = img.getImagem();
		for(int k = 0; k<treino.length;k++) {
			byte teste[][] = treino[k].getImagem();
			short cont = 0;
			for(int i = 0; i < 28;i++) {
				for(int j = 0; j < 28;j++) {
					if(referencia[i][j] == teste[i][j] && teste[i][j] == 0) {
						cont++;
					}else if(referencia[i][j] != 0 && teste[i][j] != 0) {
						cont++;
					}
				}
			}
			if(cont > menorReferencia) {
				menorReferencia = adicionarElemento(knn01, referencias, treino[k],cont);
			}
		}
		return knn01;
	}

	private static short adicionarElemento(Imagem[] knn01, short[] referencias, Imagem imagem, short cont) {
		short menorI = 0;
		short menor = referencias[0];
		for(short i = 1; i < referencias.length;i++) {
			if(referencias[i] < menor) {
				menor = referencias[i];
				menorI = i;
			}
		}
		referencias[menorI] = cont;
		knn01[menorI] = imagem;
		menor = referencias[0];
		for(short i = 1; i < referencias.length;i++) {
			if(referencias[i] < menor) {
				menor = referencias[i];
			}
		}
		return menor;
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
