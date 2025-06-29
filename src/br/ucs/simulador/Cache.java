package br.ucs.simulador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class Cache {
    private int tamanhoBloco;
    private int numLinhas;
    private int associatividade;
    private int numSets;
    private long hitTime;
    private WritePolicy writePolicy;
    private boolean usarLRU;
    private MemoriaPrincipal mem;
    private Stats stats;
    private CacheSet[] sets;
    private long contAcessos = 0;

    public Cache(int blockSize, int numLines, int associatividade, long hitTime,
                 WritePolicy writePolicy, boolean usarLRU, MemoriaPrincipal mem) {
        this.tamanhoBloco = blockSize;
        this.numLinhas = numLines;
        this.associatividade = associatividade;
        this.numSets = numLines / associatividade;
        this.hitTime = hitTime;
        this.writePolicy = writePolicy;
        this.usarLRU = usarLRU;
        this.mem = mem;
        this.stats = new Stats();
        this.sets = new CacheSet[numSets];
        for (int i = 0; i < numSets; i++) {
            sets[i] = new CacheSet(associatividade, usarLRU);
        }
    }

    public void simular(String arquivo) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(arquivo));
        String linha;
        while ((linha = reader.readLine()) != null) {
            String[] parts = linha.trim().split("\\s+");
            if (parts.length < 2) continue;
            long endereco = Long.parseLong(parts[0], 16);
            char op = parts[1].charAt(0);
            if (op == 'R') {
                ler(endereco);
            } else if (op == 'W') {
                escrever(endereco);
            }
        }
        reader.close();
        this.flush();
    }

    private void ler(long endereco) {
        stats.setAcessosTotais(stats.getAcessosTotais() + 1);
        stats.setAcessosLeitura(stats.getAcessosLeitura() + 1);

        long blockNumber = endereco / tamanhoBloco;
        long tag = blockNumber;
        int setIndex = (int) (blockNumber % numSets);
        CacheSet set = sets[setIndex];

        LinhaCache line = set.procurarLinha(tag);
        if (line != null && line.isValido()) {
            // Hit
            stats.setHitsLeitura(stats.getHitsLeitura() + 1);
            stats.setHitsTotais(stats.getHitsTotais() + 1);
            stats.setTempoTotal(stats.getTempoTotal() + hitTime);
            contAcessos++;
            line.setUltimoUsado(contAcessos);
        } else {
            // Miss
            stats.setTempoTotal(stats.getTempoTotal() + hitTime);
            stats.setTempoTotal(stats.getTempoTotal() + mem.getTempoLeitura());
            stats.setLeiturasMemoria(stats.getLeiturasMemoria() + 1);
            contAcessos++;
            LinhaCache evicted = set.removeEInsere(tag, contAcessos);
            if (evicted != null && evicted.isDirty()) {
                stats.setEscritasMemoria(stats.getEscritasMemoria() + 1);
                stats.setTempoTotal(stats.getTempoTotal() + mem.getTempoEscrita());
            }
        }
    }

    // Operação de escrita
    private void escrever(long endereco) {
        stats.setAcessosTotais(stats.getAcessosTotais() + 1);
        stats.setAcessosEscrita(stats.getAcessosEscrita() + 1);

        long blockNumber = endereco / tamanhoBloco;
        long tag = blockNumber;
        int setIndex = (int) (blockNumber % numSets);
        CacheSet set = sets[setIndex];

        LinhaCache line = set.procurarLinha(tag);
		if (line != null && line.isValido()) {
            stats.setHitsEscrita(stats.getHitsEscrita() + 1);
            stats.setHitsTotais(stats.getHitsTotais() + 1);
            if (writePolicy == WritePolicy.WRITE_THROUGH) {
                stats.setTempoTotal(stats.getTempoTotal() + hitTime);
                stats.setTempoTotal(stats.getTempoTotal() + mem.getTempoEscrita());
                stats.setEscritasMemoria(stats.getEscritasMemoria() + 1);
            } else {
                stats.setTempoTotal(stats.getTempoTotal() + hitTime);
                contAcessos++;
                line.setUltimoUsado(contAcessos);
                line.setDirty(true);
            }
        } else {
            stats.setTempoTotal(stats.getTempoTotal() + hitTime);
            contAcessos++;
            
            stats.setTempoTotal(stats.getTempoTotal() + mem.getTempoLeitura());
            stats.setEscritasMemoria(stats.getEscritasMemoria() + 1);
            LinhaCache evicted = set.removeEInsere(tag, contAcessos);
            if (evicted != null && evicted.isDirty()) {
                stats.setEscritasMemoria(stats.getEscritasMemoria() + 1);
                stats.setTempoTotal(stats.getTempoTotal() + mem.getTempoEscrita());
            }
            if (writePolicy == WritePolicy.WRITE_THROUGH) {
                stats.setTempoTotal(stats.getTempoTotal() + mem.getTempoEscrita());
                stats.setEscritasMemoria(stats.getEscritasMemoria() + 1);
            } else {
                LinhaCache newline = set.procurarLinha(tag);
                newline.setDirty(true);
            }
        }
    }

    // Flush de todas as linhas sujas
    private void flush() {
        for (CacheSet set : sets) {
            for (LinhaCache line : set.getLinhas()) {
                if (line.isValido() && line.isDirty()) {
                    stats.setEscritasMemoria(stats.getEscritasMemoria() + 1);
                    stats.setTempoTotal(stats.getTempoTotal() + mem.getTempoEscrita());
                    line.setDirty(false);
                }
            }
        }
    }

    // Gera o arquivo de saída com as estatísticas
    public void escreverEstatisticas(String outputFile, String traceFileName,
                           int writePolicyParam, int blockSize, int numLines,
                           int associativity, long hitTime, String replPolicy,
                           long memReadTime, long memWriteTime) throws IOException {
        DecimalFormat df = new DecimalFormat("0.0000");
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        writer.write("==== Parâmetros de entrada ===="); writer.newLine();
        writer.write("Arquivo de endereços: " + traceFileName); writer.newLine();
        writer.write("Arquivo de saída: " + outputFile); writer.newLine();
        writer.write("Política de escrita: " + writePolicyParam + " (" + writePolicy + ")"); writer.newLine();
        writer.write("Tamanho da linha: " + blockSize + " bytes"); writer.newLine();
        writer.write("Número de linhas: " + numLines); writer.newLine();
        writer.write("Associatividade por conjunto: " + associativity); writer.newLine();
        writer.write("Tempo de acesso (hit time): " + hitTime + " ns"); writer.newLine();
        writer.write("Política de substituição: " + replPolicy); writer.newLine();
        writer.write("Memória principal (R/W): " + memReadTime + "/" + memWriteTime + " ns"); writer.newLine();
        writer.newLine();

        writer.write("==== Estatísticas de acesso ===="); writer.newLine();
        writer.write("Total de endereços (leituras=" + stats.getAcessosLeitura() + ", escritas=" + stats.getAcessosEscrita() + ", total=" + stats.getAcessosTotais() + ")"); writer.newLine();
        writer.write("Acessos à memória principal (leituras=" + stats.getLeiturasMemoria() + ", escritas=" + stats.getEscritasMemoria() + ")"); writer.newLine();
        writer.newLine();

        double readHitRate = stats.getAcessosLeitura() > 0 ? (double) stats.getHitsLeitura() / stats.getAcessosLeitura() : 0;
        double writeHitRate = stats.getAcessosEscrita() > 0 ? (double) stats.getHitsEscrita() / stats.getAcessosEscrita() : 0;
        double globalHitRate = stats.getAcessosTotais() > 0 ? (double) stats.getHitsTotais() / stats.getAcessosTotais() : 0;

        writer.write("Taxa de acerto leitura: " + df.format(readHitRate) + " (" + stats.getHitsLeitura() + "/" + stats.getAcessosLeitura() + ")"); writer.newLine();
        writer.write("Taxa de acerto escrita: " + df.format(writeHitRate) + " (" + stats.getHitsEscrita() + "/" + stats.getAcessosEscrita() + ")"); writer.newLine();
        writer.write("Taxa de acerto global: " + df.format(globalHitRate) + " (" + stats.getHitsTotais() + "/" + stats.getAcessosTotais() + ")"); writer.newLine();
        writer.newLine();

        double avgTime = stats.getAcessosTotais() > 0 ? (double) stats.getTempoTotal() / stats.getAcessosTotais() : 0;
        writer.write("Tempo médio de acesso (ns): " + df.format(avgTime)); writer.newLine();

        writer.close();
    }

	public int getTamanhoBloco() {
		return tamanhoBloco;
	}

	public void setTamanhoBloco(int tamanhoBloco) {
		this.tamanhoBloco = tamanhoBloco;
	}

	public int getNumLinhas() {
		return numLinhas;
	}

	public void setNumLinhas(int numLinhas) {
		this.numLinhas = numLinhas;
	}

	public int getAssociatividade() {
		return associatividade;
	}

	public void setAssociatividade(int associatividade) {
		this.associatividade = associatividade;
	}

	public int getNumSets() {
		return numSets;
	}

	public void setNumSets(int numSets) {
		this.numSets = numSets;
	}

	public long getHitTime() {
		return hitTime;
	}

	public void setHitTime(long hitTime) {
		this.hitTime = hitTime;
	}

	public WritePolicy getWritePolicy() {
		return writePolicy;
	}

	public void setWritePolicy(WritePolicy writePolicy) {
		this.writePolicy = writePolicy;
	}

	public boolean isUsarLRU() {
		return usarLRU;
	}

	public void setUsarLRU(boolean usarLRU) {
		this.usarLRU = usarLRU;
	}

	public MemoriaPrincipal getMem() {
		return mem;
	}

	public void setMem(MemoriaPrincipal mem) {
		this.mem = mem;
	}

	public Stats getStats() {
		return stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public CacheSet[] getSets() {
		return sets;
	}

	public void setSets(CacheSet[] sets) {
		this.sets = sets;
	}

	public long getContAcessos() {
		return contAcessos;
	}

	public void setContAcessos(long contAcessos) {
		this.contAcessos = contAcessos;
	}
}