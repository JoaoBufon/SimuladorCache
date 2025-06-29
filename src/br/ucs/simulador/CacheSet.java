package br.ucs.simulador;

import java.util.Random;

public class CacheSet {
    private LinhaCache[] linhas;
    private boolean usarLRU;
    private Random random = new Random();

    public CacheSet(int associativity, boolean useLRU) {
        this.usarLRU = useLRU;
        linhas = new LinhaCache[associativity];
        for (int i = 0; i < associativity; i++) {
            linhas[i] = new LinhaCache();
        }
    }

    public LinhaCache procurarLinha(long tag) {
        for (LinhaCache line : linhas) {
            if (line.isValido() && line.getTag() == tag) {
                return line;
            }
        }
        return null;
    }

    public LinhaCache removeEInsere(long tag, long counter) {
        LinhaCache victim = null;
        for (LinhaCache line : linhas) {
            if (!line.isValido()) {
                victim = line;
                break;
            }
        }
        if (victim == null) {
            if (usarLRU) {
                long min = Long.MAX_VALUE;
                for (LinhaCache line : linhas) {
                    if (line.getUltimoUsado() < min) {
                        min = line.getUltimoUsado();
                        victim = line;
                    }
                }
            } else {
                int idx = random.nextInt(linhas.length);
                victim = linhas[idx];
            }
        }
        LinhaCache evicted = null;
        if (victim.isValido()) {
            evicted = new LinhaCache(victim);
        }
        victim.setValido(true);
        victim.setTag(tag);
        victim.setDirty(false);
        victim.setUltimoUsado(counter);
        return evicted;
    }

    public LinhaCache[] getLinhas() {
        return linhas;
    }

	public boolean isUsarLRU() {
		return usarLRU;
	}

	public void setUsarLRU(boolean usarLRU) {
		this.usarLRU = usarLRU;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public void setLinhas(LinhaCache[] linhas) {
		this.linhas = linhas;
	}
}