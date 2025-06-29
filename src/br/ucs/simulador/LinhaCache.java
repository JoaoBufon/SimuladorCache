package br.ucs.simulador;

public class LinhaCache {
    private long tag;
    private boolean valido = false;
    private boolean dirty = false;
    private long ultimoUsado = 0;

    public LinhaCache() {}

    public LinhaCache(LinhaCache other) {
        this.tag = other.tag;
        this.valido = other.valido;
        this.dirty = other.dirty;
        this.ultimoUsado = other.ultimoUsado;
    }

	public long getTag() {
		return tag;
	}

	public void setTag(long tag) {
		this.tag = tag;
	}

	public boolean isValido() {
		return valido;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public long getUltimoUsado() {
		return ultimoUsado;
	}

	public void setUltimoUsado(long lastUsed) {
		this.ultimoUsado = lastUsed;
	}
}