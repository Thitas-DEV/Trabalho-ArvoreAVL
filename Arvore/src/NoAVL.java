class NoAVL {
    Livro dado;
    NoAVL esquerdo, direito;
    int altura;

    public NoAVL(Livro dado) {
        this.dado = dado;
        this.altura = 1;
    }
}