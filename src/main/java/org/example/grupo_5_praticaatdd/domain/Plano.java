package org.example.grupo_5_praticaatdd.domain;

public enum Plano {
    Basico("Básico"),
    Premium("Premium");

    private final String rotulo;

    Plano(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public static Plano fromRotulo(String rotulo) {
        return switch (rotulo.trim()) {
            case "Básico", "Basico" -> Basico;
            case "Premium" -> Premium;
            default -> throw new IllegalArgumentException("Plano inválido: " + rotulo);
        };
    }
}
