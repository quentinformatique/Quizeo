/*
 * ControleurCreationQuestionEtCategorie.java
 * IUT de Rodez, pas de copyright ni de "copyleft"
 */

package application.controleurs.editeur;

import java.util.ArrayList;

import application.Quiz;
import application.exception.CreerQuestionException;
import application.exception.DifficulteException;
import application.exception.HomonymeException;
import application.exception.InvalidFormatException;
import application.exception.InvalidNameException;
import application.exception.ReponseException;
import application.modele.Categorie;
import application.modele.ModelePrincipal;
import application.vue.AlertBox;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * Controlleur de la page Creation de Question et de Catégorie.
 * Celui-ci instance  des méthodes liée au bouton de la page
 *
 * @author Néo BECOGNE
 * @author Quentin COSTES
 * @author François DE SAINT PALAIS
 * @author Lucas DESCRIAUD
 * @author Tom DOUAUD
 */


public class ControleurCreationQuestionEtCategorie {


    private ArrayList<Categorie> categories;

    private ModelePrincipal modele;
    
    @FXML private ComboBox<Categorie> selectCategorie;
    
    @FXML private ToggleGroup difficulte;

	@FXML private TextArea saisieFeedback;
	
	@FXML private TextField saisieLibeleQuestion;
	@FXML private TextField saisieReponseVrai;
	@FXML private TextField saisiePremiereReponseFausse;
	@FXML private TextField saisieSecondeReponseFausse;
	@FXML private TextField saisieTroisiemeReponseFausse;
	@FXML private TextField saisieQuatriemeReponseFausse;

	@FXML private TextField saisieNomCategorie;

	@FXML Tab tabCategorie;
	@FXML TabPane tabPane;
	
	private ModelePrincipal model = ModelePrincipal.getInstance();

	/**
	 * Méthode liée au bouton annuler de la question, vide les champs 
	 */
	@FXML
	private void annulerQuestion() {
		viderChampsQuestion();
	}
	
	/**
	 * Méthode liée au bouton annuler de la catégorie, vide les champs 
	 */
	@FXML
	private void annulerCategorie() {
		viderChampsCategorie();
	}
	
	@FXML
	private void aideCategorie() {
		model.setDisplayCategoriePane(true);
		model.setPagePrecedente("CreationQuestionEtCategorie.fxml");
		Quiz.chargerEtChangerVue("Aide.fxml");
	}
	
	@FXML
	private void aideQuestion() {
		model.setPagePrecedente("CreationQuestionEtCategorie.fxml");
		Quiz.chargerEtChangerVue("Aide.fxml");
	}



	@FXML
	public void initialize() {
        // On récupère l'instance du Modèle
	    modele = ModelePrincipal.getInstance();

	    miseAJourListeCategorie();

	    // Permet de sélectionner un onglet dans le TabPane
	    if (ModelePrincipal.getInstance().isDisplayCategoriePane()) {
	    	tabPane.getSelectionModel().select(tabCategorie);
	    	ModelePrincipal.getInstance().setDisplayCategoriePane(false);
	    }
    }

	/**
	 * Met à jour la ComboBox de Catégorie avec les catégories du modèle
     */
    private void miseAJourListeCategorie() {
        // Récupération puis ajout des nom de catégorie
        categories = modele.getCategories();
        
        selectCategorie.getItems().clear();
        selectCategorie.getItems().addAll(categories);
    }

    /**
	 * Méthode liée au bouton Accueil
	 * envoie vers la page Accueil.fxml
	 */
	@FXML
	private void retourAcceuil() {
		Quiz.changerVue("Editeur.fxml");
	}

    /**
     * Méthode liée au bouton valider, qui crée une nouvelle question
     * à partir des champs
     */
	@FXML
	private void validerQuestion() {
		try {
			// Récupération de l'indice de la catégorie choisie
			int indiceCategorieChoisie = getIndiceCategorieChoisie();
			if (indiceCategorieChoisie < 0) {
				throw new 
				NullPointerException("Il n'y a pas de catégorie choisie");
			}
			
			// Récupération du nom de la question
			String libeleQuestion = getLibeleQuestion();
			
			// Récupération de la difficultée
			int valeurDifficulte = getDifficulte();
			
			// Récupération du feedback
			String feedback = getFeedback();
			
			// Récupération de la réponse vrai
			String reponseVrai = getReponseVrai();
			
			// Récupération des réponses fausses
			ArrayList<String> mauvaiseReponses = getMauvaiseReponse();
			
			
			creerEtGererQuestion(indiceCategorieChoisie, libeleQuestion, valeurDifficulte, feedback,
					reponseVrai, mauvaiseReponses);
			
		} catch (NullPointerException e) {
        	AlertBox.showErrorBox("Les champs requis pour une question ne sont pas tous remplis");
        }
	}

    /** 
     * @return La liste des mauvaise réponse choisie
     */
    private ArrayList<String> getMauvaiseReponse() {
        ArrayList<String> mauvaiseReponses = new ArrayList<>();
        if (!saisiePremiereReponseFausse.getText().isBlank()) {
            mauvaiseReponses.add(saisiePremiereReponseFausse.getText());
        }
        if (!saisieSecondeReponseFausse.getText().isBlank()) {
            mauvaiseReponses.add(saisieSecondeReponseFausse.getText());
        }
        if (!saisieTroisiemeReponseFausse.getText().isBlank()) {
            mauvaiseReponses.add(saisieTroisiemeReponseFausse.getText());
        }
        if (!saisieQuatriemeReponseFausse.getText().isBlank()) {
            mauvaiseReponses.add(saisieQuatriemeReponseFausse.getText());
        }
        return mauvaiseReponses;
    }

    /** 
     * @return La réponse vrai
     */
    private String getReponseVrai() {
        return saisieReponseVrai.getText();
    }

    /** 
     * @return Le feedback
     */
    private String getFeedback() {
        return saisieFeedback.getText();
    }

    /** 
     * @return La difficulté
     */
    private int getDifficulte() {
        int reponse = Integer.MAX_VALUE;
        if (difficulte.getSelectedToggle() != null) {
            reponse = ModelePrincipal.LABEL_DIFFICULTE_TO_INT.get(
                    ((RadioButton) difficulte.getSelectedToggle()).getText());
        }
        return reponse;
    }

    /** 
     * @return Le libelle de la question
     */
    private String getLibeleQuestion() {
        return saisieLibeleQuestion.getText();
    }
	
    /**
     * @return L'indice de la catégorie choisie.
     */
	private int getIndiceCategorieChoisie() {
	    return selectCategorie.getSelectionModel().getSelectedIndex();
	}
	
    /**
     * Créer une question dans le modèle et gère les possibles exceptions qui
     * peuvent apparaître
     * @param indiceCategorieChoisie L'indice de la catégorie choisie
     * @param libeleQuestion Le libelle de la question à créer
     * @param valeurDifficulte La difficulté de la question
     * @param feedback Le feedback de la question
     * @param reponseVrai La réponse vrai de la question
     * @param mauvaiseReponses La liste des mauvaise réponse de la question
     */
    private void creerEtGererQuestion(int indiceCategorieChoisie, String libeleQuestion,
            int valeurDifficulte, String feedback, String reponseVrai, ArrayList<String> mauvaiseReponses) {
        // Création de la question
        boolean questionCreer = false;
        try {
            questionCreer = modele.creerQuestion(libeleQuestion,
                    indiceCategorieChoisie, valeurDifficulte, reponseVrai,
                    mauvaiseReponses, feedback);

        } catch (InvalidFormatException e) {
            AlertBox.showErrorBox(e.getMessage());
        } catch (InvalidNameException e) {
            AlertBox.showErrorBox(e.getMessage());
        } catch (ReponseException e) {
            AlertBox.showErrorBox("Attention, les mauvaises réponses ne doivent "
                    + "pas être en double ET la bonne réponse ne peut pas être "
                    + "une mauvaise réponse");
        } catch (HomonymeException e) {
            AlertBox.showWarningBox("La question saisie existe déjà");
        } catch (DifficulteException e) {
            AlertBox.showWarningBox("Il manque la difficultée de la question");
        } catch (CreerQuestionException e) {
            e.printStackTrace();
        }
        if (questionCreer) {
            AlertBox.showSuccessBox("Question crée avec succès !");
            viderChampsQuestion();
        }
    }

    /**
     * Méthodes liée au bouton valider,
     * qui devra enregistrer les champs  dans la banques Catégori
     */
	@FXML
    private void validerCategorie() {

		boolean categorieCreer = false;

        String nom = saisieNomCategorie.getText().strip();
		creerEtGererCategorie(categorieCreer, nom);
    }
	
	/**
     * Créer une catégorie dans le modèle et gère les possibles exceptions qui
     * peuvent apparaître
     * @param categorieCreer
     * @param nom
     */
    private void creerEtGererCategorie(boolean categorieCreer,String nom) {
        try {
            categorieCreer = modele.creerCategorie(nom);

        } catch (InvalidNameException e) {
            AlertBox.showErrorBox("Veuillez saisir une nom de catégorie valide "
                    + ": entre 1 et 30 caractères maximum et qui ne dois pas contenir d'accents");
        } catch (HomonymeException e) {
            AlertBox.showWarningBox("La catégorie saisie existe déjà");
        }
        
        if (categorieCreer) {
            AlertBox.showSuccessBox("Categorie crée avec succès !");
            miseAJourListeCategorie();
            Quiz.charger("EditerCategories.fxml");
            viderChampsCategorie();
        }
    }
	
	/**
	 * Vide les champs de la créationQuestion
	 */
	private void viderChampsQuestion() {
	    difficulte.selectToggle(null);
	    
	    saisieLibeleQuestion.setText("");
	    saisieFeedback.setText("");
	    saisieReponseVrai.setText("");
	    
	    saisiePremiereReponseFausse.setText("");
	    saisieSecondeReponseFausse.setText("");
	    saisieTroisiemeReponseFausse.setText("");
	    saisieQuatriemeReponseFausse.setText("");
	    /* 
	     * Nous sommes obligés de garder la value de la combobox 
	     * meme si ce n'est pas le plus optimal pour l'utilisateur
	     * car la combo box ne prends que des catégories et non une String
	     */
	}

	/**
	 * Vide les champs de la créationCategorie
	 */
	private void viderChampsCategorie() {
	    saisieNomCategorie.setText("");
	}
}
