package Semana2;

import java.util.ArrayList;
import java.util.List;

public class HelloWorld {

    public static class MinhaClasse {
        private String nome;
        private int idade;

        // construtor da classe
        public MinhaClasse(String nome, int idade) {
            this.nome = nome;
            this.idade = idade;
        }

        public MinhaClasse() {
            this.nome = "";
            this.idade = 0;
        }

        public String getNome() {
            return this.nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public int getIdade() {
            return idade;
        }

        public void setIdade(int idade) {
            this.idade = idade;
        }

    }

    public static void main(String[] args) {

        MinhaClasse mc = new MinhaClasse("Raphael", 43);
        IO.println(mc.getNome());
        IO.println(mc.getIdade());

        MinhaClasse mc2 = new MinhaClasse();
        mc2.setNome("Raphael");
        mc2.setIdade(43);
        IO.println(mc2.getNome());
        IO.println(mc2.getIdade());

        IO.println("Hello World");
        int nomeVariavel = 0;
        IO.println(nomeVariavel);
        float fNumero = 1.0f;
        String string = "nova string";
        String s = new String("nova string");
        IO.println(string);
        IO.println(s);

        int[] vInt = { 1, 4, 5 };
        IO.println(vInt[0]);
        IO.println(vInt[1]);
        IO.println(vInt[2]);

        int[] vetInt = new int[3];
        vetInt[0] = 1;
        vetInt[1] = 4;
        vetInt[2] = 5;

        for (int i = 0; i < vetInt.length; i++) {
            IO.println(vetInt[i]);
        }
        IO.println("--------");

        for (int v : vetInt) {
            IO.println(v);
        }

        int[][] matriz = { { 1, 3, 4 }, { 2, 4, 5 }, { 3, 4, 5, 6, 7, 8, } };
        for (int i = 0; i < matriz[0].length; i++) {
            IO.println(matriz[0][i]);
        }
        for (int j = 0; j < matriz[1].length; j++) {
            IO.println(matriz[1][j]);
        }
        for (int k = 0; k < matriz[2].length; k++) {
            IO.println(matriz[2][k]);
        }

        int valorDeEntrada;
        valorDeEntrada = Integer.parseInt(IO.readln("Qual sua idade?"));

        IO.println(valorDeEntrada);

        String minhaString;
        minhaString = IO.readln("Qual é o seu nome?");
        IO.println(minhaString);

        var minhaLista = new ArrayList<String>();
        minhaLista.add("string");
        minhaLista.add("string2");
        minhaLista.add("string3");

        for (String valor : minhaLista) {
            IO.println(valor);
        }
    }
}
