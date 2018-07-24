package br.ufrpe.leitordigitos;

public class Tempo extends Thread {

	@Override
	public void run() {
		int total = Principal.teste.length/100;
		int i = 1;
		while(true) {
			if(Principal.atual > i*total) {
				i++;
				System.out.println((i/10) +"% concluido");
			}
		}
	}
}
