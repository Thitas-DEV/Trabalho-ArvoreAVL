
import java.io.*;
import java.nio.file.*;
import java.util.*;


class Livro {
    int bookID;
    String title;
    String authors;
    String isbn;

    public Livro(int bookID, String title, String authors, String isbn) {
        this.bookID = bookID;
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        return "[" + bookID + "] " + title + " - " + authors + " (ISBN: " + isbn + ")";
    }
}


class NoAVL {
    Livro dado;
    NoAVL esquerdo, direito;
    int altura;

    public NoAVL(Livro dado) {
        this.dado = dado;
        this.altura = 1;
    }
}


class ArvoreAVL {
    private NoAVL raiz;

    private int altura(NoAVL no) {
        return (no == null) ? 0 : no.altura;
    }

    private int fatorBalanceamento(NoAVL no) {
        return (no == null) ? 0 : altura(no.esquerdo) - altura(no.direito);
    }

    private NoAVL rotacaoDireita(NoAVL y) {
        NoAVL x = y.esquerdo;
        NoAVL T2 = x.direito;

        x.direito = y;
        y.esquerdo = T2;

        y.altura = Math.max(altura(y.esquerdo), altura(y.direito)) + 1;
        x.altura = Math.max(altura(x.esquerdo), altura(x.direito)) + 1;

        return x;
    }

    private NoAVL rotacaoEsquerda(NoAVL x) {
        NoAVL y = x.direito;
        NoAVL T2 = y.esquerdo;

        y.esquerdo = x;
        x.direito = T2;

        x.altura = Math.max(altura(x.esquerdo), altura(x.direito)) + 1;
        y.altura = Math.max(altura(y.esquerdo), altura(y.direito)) + 1;

        return y;
    }

    public void inserir(Livro livro) {
        raiz = inserir(raiz, livro);
    }

    private NoAVL inserir(NoAVL no, Livro livro) {
        if (no == null) return new NoAVL(livro);

        if (livro.bookID < no.dado.bookID)
            no.esquerdo = inserir(no.esquerdo, livro);
        else if (livro.bookID > no.dado.bookID)
            no.direito = inserir(no.direito, livro);
        else
            return no;

        no.altura = 1 + Math.max(altura(no.esquerdo), altura(no.direito));

        int balance = fatorBalanceamento(no);

        if (balance > 1 && livro.bookID < no.esquerdo.dado.bookID)
            return rotacaoDireita(no);

        if (balance < -1 && livro.bookID > no.direito.dado.bookID)
            return rotacaoEsquerda(no);

        if (balance > 1 && livro.bookID > no.esquerdo.dado.bookID) {
            no.esquerdo = rotacaoEsquerda(no.esquerdo);
            return rotacaoDireita(no);
        }

        if (balance < -1 && livro.bookID < no.direito.dado.bookID) {
            no.direito = rotacaoDireita(no.direito);
            return rotacaoEsquerda(no);
        }

        return no;
    }

    public Livro buscar(int bookID) {
        NoAVL no = buscar(raiz, bookID);
        return (no != null) ? no.dado : null;
    }

    private NoAVL buscar(NoAVL no, int bookID) {
        if (no == null || no.dado.bookID == bookID) return no;
        if (bookID < no.dado.bookID)
            return buscar(no.esquerdo, bookID);
        else
            return buscar(no.direito, bookID);
    }

    public void alterar(int bookID, Livro novoLivro) {
        remover(bookID);
        inserir(novoLivro);
    }

    public void remover(int bookID) {
        raiz = remover(raiz, bookID);
    }

    private NoAVL remover(NoAVL no, int bookID) {
        if (no == null) return null;

        if (bookID < no.dado.bookID)
            no.esquerdo = remover(no.esquerdo, bookID);
        else if (bookID > no.dado.bookID)
            no.direito = remover(no.direito, bookID);
        else {
            if ((no.esquerdo == null) || (no.direito == null)) {
                NoAVL temp = (no.esquerdo != null) ? no.esquerdo : no.direito;
                if (temp == null) {
                    no = null;
                } else {
                    no = temp;
                }
            } else {
                NoAVL temp = menorValor(no.direito);
                no.dado = temp.dado;
                no.direito = remover(no.direito, temp.dado.bookID);
            }
        }

        if (no == null) return null;

        no.altura = Math.max(altura(no.esquerdo), altura(no.direito)) + 1;
        int balance = fatorBalanceamento(no);

        if (balance > 1 && fatorBalanceamento(no.esquerdo) >= 0)
            return rotacaoDireita(no);

        if (balance > 1 && fatorBalanceamento(no.esquerdo) < 0) {
            no.esquerdo = rotacaoEsquerda(no.esquerdo);
            return rotacaoDireita(no);
        }

        if (balance < -1 && fatorBalanceamento(no.direito) <= 0)
            return rotacaoEsquerda(no);

        if (balance < -1 && fatorBalanceamento(no.direito) > 0) {
            no.direito = rotacaoDireita(no.direito);
            return rotacaoEsquerda(no);
        }

        return no;
    }

    private NoAVL menorValor(NoAVL no) {
        NoAVL atual = no;
        while (atual.esquerdo != null)
            atual = atual.esquerdo;
        return atual;
    }

    public void emOrdem() {
        emOrdem(raiz);
    }

    private void emOrdem(NoAVL no) {
        if (no != null) {
            emOrdem(no.esquerdo);
            System.out.println(no.dado);
            emOrdem(no.direito);
        }
    }


    public static void main(String[] args) throws IOException {
        ArvoreAVL arvore = new ArvoreAVL();
        TreeMap<Integer, Livro> treeMap = new TreeMap<>();
        List<Livro> livros = new ArrayList<>();

        long inicioLeitura = System.nanoTime();

        try (BufferedReader br = Files.newBufferedReader(Paths.get("bin/books_limpo.csv"))) {
            String linha;
            br.readLine(); // pular cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (partes.length >= 4) {
                    try {
                        int id = Integer.parseInt(partes[0].trim());
                        String titulo = partes[1].trim();
                        String autores = partes[2].trim();
                        String isbn = partes[3].trim();
                        Livro livro = new Livro(id, titulo, autores, isbn);
                        arvore.inserir(livro);
                        treeMap.put(id, livro);
                        livros.add(livro);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }

        long fimLeitura = System.nanoTime();
        System.out.println("Tempo para inserir todos os dados na AVL: " + ((fimLeitura - inicioLeitura) / 1_000_000) + " ms");

        // Testes de desempenho
        List<Integer> ids = new ArrayList<>();
        for (Livro l : livros) ids.add(l.bookID);
        Collections.shuffle(ids);

        try (PrintWriter log = new PrintWriter("desempenho.txt")) {
            log.println("AVL vs TreeMap");

            long iniBuscaAVL = System.nanoTime();
            for (int i = 0; i < 1000 && i < ids.size(); i++) arvore.buscar(ids.get(i));
            long fimBuscaAVL = System.nanoTime();

            long iniBuscaTM = System.nanoTime();
            for (int i = 0; i < 1000 && i < ids.size(); i++) treeMap.get(ids.get(i));
            long fimBuscaTM = System.nanoTime();

            log.println("Busca AVL: " + ((fimBuscaAVL - iniBuscaAVL) / 1_000_000) + " ms");
            log.println("Busca TreeMap: " + ((fimBuscaTM - iniBuscaTM) / 1_000_000) + " ms");

            long iniRemocao = System.nanoTime();
            for (int i = 0; i < 1000 && i < ids.size(); i++) arvore.remover(ids.get(i));
            long fimRemocao = System.nanoTime();
            log.println("Remocao AVL: " + ((fimRemocao - iniRemocao) / 1_000_000) + " ms");
        }

        System.out.println("Testes de desempenho registrados em desempenho.txt");
    }
} 
