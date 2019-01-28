package entity;

import javafx.util.Pair;

import java.util.Date;
import java.util.List;

/**
 * Class Emprunt
 * Created by Alexis on 13/12/2018
 */
public class Emprunt {
    private Date date;
    private Personnel utilisateur;
    private List<Pair<Materiel,Integer>> materiels;

    public Emprunt(Personnel utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Personnel getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Personnel utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void addMateriel (Materiel materiel, Integer qte) {
        this.materiels.add(new Pair<>(materiel,qte));
    }
}
