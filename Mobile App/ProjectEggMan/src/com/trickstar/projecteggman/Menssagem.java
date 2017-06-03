package com.trickstar.projecteggman;

public class Menssagem {
	private String nome;
	private String menssagem;
	private int cor;
	
	public Menssagem(String nome, String menssagem, int cor)
	{
		this.setNome(nome);
		this.setMenssagem(menssagem);
		this.setCor(cor);
	}

	public String getMenssagem() {
		return menssagem;
	}

	public void setMenssagem(String menssagem) {
		this.menssagem = menssagem;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getCor() {
		return cor;
	}

	public void setCor(int cor) {
		this.cor = cor;
	}
}
