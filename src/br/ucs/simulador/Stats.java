package br.ucs.simulador;

public class Stats {
    private long acessosLeitura = 0;
    private long acessosEscrita = 0;
    private long acessosTotais = 0;

    private long hitsLeitura = 0;
    private long hitsEscrita = 0;
    private long hitsTotais = 0;

    private long leiturasMemoria = 0;
    private long escritasMemoria = 0;

    private long tempoTotal = 0;

	public Stats() {
		super();
	}

	public Stats(long acessosLeitura, long acessosEscrita, long acessosTotais, long hitsLeitura, long hitsEscrita,
			long hitsTotais, long leiturasMemoria, long escritasMemoria, long tempoTotal) {
		super();
		this.acessosLeitura = acessosLeitura;
		this.acessosEscrita = acessosEscrita;
		this.acessosTotais = acessosTotais;
		this.hitsLeitura = hitsLeitura;
		this.hitsEscrita = hitsEscrita;
		this.hitsTotais = hitsTotais;
		this.leiturasMemoria = leiturasMemoria;
		this.escritasMemoria = escritasMemoria;
		this.tempoTotal = tempoTotal;
	}

	public long getAcessosLeitura() {
		return acessosLeitura;
	}

	public void setAcessosLeitura(long tempoTotal) {
		this.acessosLeitura = tempoTotal;
	}

	public long getAcessosEscrita() {
		return acessosEscrita;
	}

	public void setAcessosEscrita(long acessosEscrita) {
		this.acessosEscrita = acessosEscrita;
	}

	public long getAcessosTotais() {
		return acessosTotais;
	}

	public void setAcessosTotais(long acessosTotais) {
		this.acessosTotais = acessosTotais;
	}

	public long getHitsLeitura() {
		return hitsLeitura;
	}

	public void setHitsLeitura(long hitsLeitura) {
		this.hitsLeitura = hitsLeitura;
	}

	public long getHitsEscrita() {
		return hitsEscrita;
	}

	public void setHitsEscrita(long hitsEscrita) {
		this.hitsEscrita = hitsEscrita;
	}

	public long getHitsTotais() {
		return hitsTotais;
	}

	public void setHitsTotais(long hitsTotais) {
		this.hitsTotais = hitsTotais;
	}

	public long getLeiturasMemoria() {
		return leiturasMemoria;
	}

	public void setLeiturasMemoria(long leiturasMemoria) {
		this.leiturasMemoria = leiturasMemoria;
	}

	public long getEscritasMemoria() {
		return escritasMemoria;
	}

	public void setEscritasMemoria(long escritasMemoria) {
		this.escritasMemoria = escritasMemoria;
	}

	public long getTempoTotal() {
		return tempoTotal;
	}

	public void setTempoTotal(long tempoTotal) {
		this.tempoTotal = tempoTotal;
	}
}