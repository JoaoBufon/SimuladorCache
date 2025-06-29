package br.ucs.simulador;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

enum WritePolicy {
    WRITE_THROUGH,
    WRITE_BACK
}

public class SimuladorCache {
	private static int HIT_TIME = 5;
	private static int MP_ACESS_TIME =70;
	private static String ARQUIVO_SAIDA = "saida.txt";
	private static String ARQUIVO_ENTRADA = "oficial.cache";

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Política de escrita (0-write-through, 1-write-back): ");
            int wp = sc.nextInt();
            WritePolicy writePolicy = (wp == 0) ? WritePolicy.WRITE_THROUGH : WritePolicy.WRITE_BACK;

            System.out.print("Tamanho da linha (bytes, potência de 2): ");
            int tamanhoBloco = sc.nextInt();

            System.out.print("Número de linhas (potência de 2): ");
            int numLinhas = sc.nextInt();

            System.out.print("Associatividade por conjunto (potência de 2): ");
            int associatividade = sc.nextInt();

            System.out.print("Política de substituição (LRU/Aleatória): ");
            String repl = sc.next();
            boolean useLRU = repl.equalsIgnoreCase("LRU");

            MemoriaPrincipal mainMem = new MemoriaPrincipal(MP_ACESS_TIME, MP_ACESS_TIME);
            Cache cache = new Cache(tamanhoBloco, numLinhas, associatividade, HIT_TIME,
                                     writePolicy, useLRU, mainMem);

            cache.simular(ARQUIVO_ENTRADA);
            cache.escreverEstatisticas(ARQUIVO_SAIDA, ARQUIVO_ENTRADA,
                             wp, tamanhoBloco, numLinhas, associatividade,
                             HIT_TIME, repl, MP_ACESS_TIME, MP_ACESS_TIME);

            System.out.println("Simulação concluída. Resultados gravados em " + ARQUIVO_SAIDA);
        } catch (Exception e) {
            System.err.println("Erro durante a simulação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
