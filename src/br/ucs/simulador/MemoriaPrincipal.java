package br.ucs.simulador;

public class MemoriaPrincipal {
    private long tempoLeitura;
    private long tempoEscrita;

    public MemoriaPrincipal(long tempoLeitura, long tempoEscrita) {
        this.tempoLeitura = tempoLeitura;
        this.tempoEscrita = tempoEscrita;
    }

	public long getTempoLeitura() {
		return tempoLeitura;
	}

	public void setTempoLeitura(long tempoLeitura) {
		this.tempoLeitura = tempoLeitura;
	}

	public long getTempoEscrita() {
		return tempoEscrita;
	}

	public void setTempoEscrita(long tempoEscrita) {
		this.tempoEscrita = tempoEscrita;
	}
}
