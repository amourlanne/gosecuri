package entity;

/**
 * Class Materiel
 * Created by Alexis on 28/01/2019
 */
public class Materiel {
    private String libellé;
    private int quantité = 0;

    public Materiel(String libellé) {
        this.libellé = libellé;
    }

    public String getLibellé() {
        return libellé;
    }

    public void setLibellé(String libellé) {
        this.libellé = libellé;
    }

    public int getQuantité() {
        return quantité;
    }

    public void setQuantité(int quantité) {
        this.quantité = quantité;
    }
}
