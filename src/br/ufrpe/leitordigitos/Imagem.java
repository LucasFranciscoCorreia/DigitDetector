package br.ufrpe.leitordigitos;

public class Imagem {
	private byte imagem[][];
	private char label;
	
	public Imagem() {
		imagem = new byte[28][28];
	}
	
	public Imagem(byte imagem[][], char label) {
		this.imagem = imagem;
		this.label = label;
	}
	
	public void setImagem(byte imagem[][], char label) {
		this.imagem = imagem;
		this.label = label;
	}
	
	public byte[][] getImagem() {
		return imagem;
	}
	
	public char getLabel() {
		return this.label;
	}
}
