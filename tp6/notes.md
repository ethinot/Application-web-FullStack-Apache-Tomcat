# TP6 - Web Performance

## Avant le début du TP

* **Désactiver le cache sur Chrome** : F12 -> Network Tab -> cocher la case "Désactiver le cache"
* **Désactiver Ad-Blocks Chrome** : Extensions -> gérer les extensions -> désactiver  
* Les mesures de temps doivent être en **miliseconde (ms)**

## 1 - Onglet Performance du navigateur

* Récupération de la page HTML (transaction HTTP)
  * Première requête (GET) sur l'URL www.univ-lyon1.fr/

* Récupération de la / des feuille(s) CSS (transaction(s) HTTP à partir du début de la navigation) ; ne tenez pas compte des CSS demandées par YouTube et TarteAuCitron
  * Faut zoomer de ouf pour le CSS il est en violet
  * Somme de tous les chargment ?

* Fin du premier rendu (de la page blanche au premier contenu)
  * Clique sur le frame pour mettre le marqueur, puis zoomer pour avoir le nombre de ms écouler

* Fin du rendu de l'app shell (cadre applicatif sans contenu)
  
* Fin du chemin critique de rendu (cf. cours) 
  * Parsing HMTL (compositon DOM) -> Analyser le code HTML (en *bleu*)
  * Parsing CSS (composition CSSOM)
  * Rendu (combinaison DOM/CSSOM, mise en page (layout), affichage)   
  * Partie **Pincipal** 

* Durée totale du chargement
  * Section **Résumé** 

## 2 - APIs de performance

### 2.1 - Opérations à réaliser