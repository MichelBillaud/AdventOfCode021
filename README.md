# AdventOfCode 2021

Mes solutions pour les problèmes de l'[Advent Of Code 2021](https://adventofcode.com/).

Si vous participez, ne lisez pas celle du jour

## Day 1 - Sonar Sweep (Fortran 77)

Quand j'ai commencé à la fac, j'ai appris Fortran 66 (aka. Fortran
IV), quelques années plus tard, j'ai enseigné quelque temps Fortran 77
à l'IUT.

Le problème était facile, c'était une occasion de me remettre à
Fortran 77.


## Day 2 - Dive (Bash)

Pas trop de calcul, des données dans un format facile à lire, et qu'on
peut traiter au vol, avec un peu de *shell* (`bash`) ça le fait très bien.

## Day 3 - Binary Diagnostic (Java)

Comme il faut stocker les données lues pour faire plusieurs passes
dessus, je choisis un langage plus civilisé où les conteneurs sont
faciles à utiliser.  Pourquoi pas Java.

Pendant qu'on y est, je passe par des `streams` pour faire les traitements
de base, plutot que des boucles.

## Day 4 - Bingo (Java)

Pour la même raison (il faut traiter des *listes* de "Boards"), choix
de Java.

J'en profite pour caser un pattern `Builder`, un objet qui accumule
les données qui permettront de construire une `Board`. Ensuite on 
appelle sa méthode `build`, et il nous refile l'objet construit.

Ici le builder reçoit des lignes de 5 nombres qu'il entasse dans un tableau.

~~~java
   static class BoardBuilder {

        int[][] numbers = new int[5][0];
        int currentLine = 0;

        void addRow(int t[]) {
            numbers[currentLine++] = t;
        }

        Board build() {
            return new Board(this);
        }
    }
~~~

et le constructeur de `Board` s'en sert

~~~java
static class Board {

	final int[][] numbers;
	final Map<Integer, Position> positions = new HashMap<>();
	final boolean[][] marks = new boolean[5][5];

	Board(BoardBuilder bb) {
		numbers = bb.numbers;
		for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 5; c++) {
				positions.put(numbers[r][c], new Position(r, c));
			}
		}
	}
...
}
~~~

Ah, et je fais du java moderne, j'ai utilisé un `record` !

~~~java
package bingo;

public record Position(int row, int col) {

}
~~~

## Day 5 - Vents (Java)

Un usage plus prononcé des *streams* pour le calcul :

~~~java
long count = lines.stream()
	.filter(Line::isHorizontalOrVertical) // part 1
	.flatMap(Line::allPositions)
	.collect(Collectors.groupingBy(
		Function.identity(),
		Collectors.counting()))
	.entrySet()
	.stream()
	.filter(e -> e.getValue() >= 2)
	.count();
~~~

et aussi l'énumération des points d'une ligne

~~~java
public record Line(Position start, Position end) {

    ...
	
    Stream<Position> allPositions() {
        final int dr = start.dRow(end);
        final int dc = start.dCol(end);
        final int d = start.distance(end);
        return IntStream.rangeClosed(0, d).mapToObj(
                k -> new Position(
						start.row() + k * dr,
						start.col() + k * dc));
    }
}
~~~


J'aurais pu en faire autant pour la lecture, avec la méthode
`lines()`, comme le jour 3. J'ai dû avoir peur qu'en partie 2, il y
ait des données supplémentaires de type différent à lire.

Après coup, je me suis décidé à simplifier la lecture (que je faisais
à coup de `Scanner.nextLine` + `String.split`) avec des *regex* (bizarrement, 
j'ai toujours traîné des pieds pour les utiliser. Il n'est jamais trop tard).


## Day 6 - Lanternfish (Java)

**Langage.** L'analyse de la ligne de données (une grande chaîne, avec
des champs séparés par des virgules), aurait compliqué la
programmation en Fortran.  Va pour Java. Ca aurait été faisable en C,
je n'y ai pas pensé sur le moment.  Il va falloir que je pense à
changer de langage, la prochaine fois.

**Algorithme.** Ai eu la chance de voir l'astuce (tableau de comptage
par état) dès le début.

Comme tout le monde, la différence entre la partie 1 et la partie 2,
c'était le débordement des entiers, réglé par un passage de `int` à `long`.

Évidemment, sauf si on a eu la mauvaise idée de travailler sur une
liste des états des poissons, ça explose en taille et temps de calcul
(en gros, doublement toutes les 6 étapes ? et il y en a 256 !)


## Day 7 - Treachery of Whales (Java)

**Langage.** La flemme de changer.

**Algo.** Il me semble que la fonction qui donne la somme des
distances d'un point p a un ensemble E de points n'a qu'un mimimum
local, qui est le minimum tout court. Je rêve peut-être. C'est un peu
loin tout ça.

En tout cas ça a marché.

## Day 8 - Seven Segments (Java)

Programmation en pointillé, en faisant autre chose (vidage de maison).

Le petit portable que j'ai emmené en déplacement m'a fait quelques
misères avec Netbeans. Au début je me suis obstiné
un certain temps à ne pas tenir compte de la consigne "une entrée par ligne".

**Algo.** En suivant la recommandation d'une "careful analysis", ai fini
par trouver que 

- le bit du segment `a` s'obtenait comme différence des masque
binaires des représentations `7 ` et du `1` (identifiables - partie
1 - parce que les seuls qui ont respectivement 3 et 2 bits à 1) ;
- le `d` est différence des bits communs à 2,3 et 5
(qui ont 5 bits levés) et de ceux de 0, 6 et 9 (6 bits) ;
- etc.

Travaux quelque peu ralentis suite au déclenchement intempestif d'un
extincteur en le posant dans la voiture de mon frère qui voulait le
jeter à la décharge, et la séance d'aspirateur qui s'en est suivie.

Bon à savoir : les décharges ne prennent pas les extincteurs non percutés.
Là, c'est fait.

## Day 9 -  Smoke Basins (Java)

Parcours de sous-graphes dans une grille. Ça faisait longtemps.

PS: Apparemment j'ai fait un peu plus général que ce qui était demandé, 
faute d'avoir tenu compte de la phrase

~~~
and all other locations will always be part of exactly one basin.
~~~

ce qui exclut d'existence de "crêtes" qui déversent dans 2 bassines,
comme les `3` dans

~~~
132
231
~~~

Je n'avais pas interprété
comme une caractérisque (importante !) des jeux d'essai.

Eussé-je mieux compris, j'aurais tenté un genre set-union-find, pour
déterminer les composantes connexes qui ne contiennent pas de 9 (histoire
de ne pas me refaire un flooding).


Faut bien lire l'énoncé qu'ils disaient.


## Day 10 - SyntaxScoring (C + un peu de TypeScript)

**Langage** Un peu de C pour changer.

** Programmation**

- Un peu de distraction. Faire la somme des scores, c'est bien, mais c'est
pas forcément ce qui est demandé.
- La flemme de relire la page de manuel de `qsort` pour trier un
malheureux tableau de `long`, et de même pour réécrire un tri en *O(n
log n)*. Il n'y a jamais qu'au pire une centaine éléments à ordonner,
l'insertion au fur et à mesure dans un tableau ordonné, ça fait bien
l'affaire.

**PS** : après coup, j'ai refait la partie 1 en TypeScript, que je ne
maîtrise pas du tout (lecture asynchrone du fichier...)

Pour le faire tourner, en ligne de commande :

~~~
$ cd Day10-TypeScript
$ nodejs day10.js
~~~

## Day 11 - Dumbo Octopus (Java)

**Programmation.** Je voulais en faire une version Cobol pour changer,
mais je n'ai pas retrouvé le package `gnu-cobol` sous Debian.

Du coup (!), deux versions en Java :

- Dans la version "quick and dirty" qui m'a servi à trouver la
solution, la partie 2 a été obtenue par copier-coller éhonté de la partie 1.

- Dans la version propre, un peu de refactoring après coup fait
apparaitre un objet qui décrit l'état de la grille, et des méthodes
qui agissent dessus. La méthode `step` exécute un pas de
transformation, et retourne, dans un objet `StepInfo`, des infos
calculées au passage. Ici il n'y a que le nombre de cellules
"flashées", mais ça pourrait s'étendre.

## Day 12 - Passage Pathing (Java)

**Problème.** Là on rentre dans l'énumération récursive des
chemins dans un graphe, sous contrainte.

**Programmation.** Gain de temps notable en introduisant dès le départ
une classe pour représenter l'objet sur lequel on travaille, avec

- ensemble des noms de grandes et petites cavernes,
- dictionnaire : nom -> liste noms de cavernes accessibles.

Idée générale, définir une méthode récursive qui exécute une action
(un `Runnable` donné en paramètre) quand un chemin est trouvé.

~~~java
    public int nbDistinctPaths1() {
        final int[] counter = {0}; // faking a mutable integer 
        var visitedSmallCaves = new HashSet<String>(List.of("start"));
        forEachPath1("start", visitedSmallCaves,
                () -> counter[0]++
        );
        return counter[0];
    }
~~~

se lit : appliquer l'action "ajouter 1 au compteur" pour chaque chemin
qui part de `"start"` (et qui arrive à `"end"`) sans repasser 2 fois par
la même petite caverne (ni repasser par celles qui ont été déjà visitées).

Astuce : tableau contenant un seul `int` pour simuler un `int` mutable.


## Day 13 - Transparent Origami (C)

**Nota bene.** Renonçant à essayer de faire le malin avec des
solutions originales et/ou astucieuses et/ou exotiques (ça me coûte
!), j'essaie maintenant de mettre en évidence le processus de
**construction** des programmes, sachant qu'on n'a pas les
spécifications de la partie 2 quand on réalise la partie 1.

J'explique : 

1. Pour la partie 1, je fais simple, mais propre. Au lieu de tout coller
dans le `main()`, je découpe raisonnablement en fonctions.
2. Quand j'ai l'énoncé de la partie 2, je "réusine" le code de la partie
1 pour faire apparaître du code commun utilisable pour la seconde partie.
Ça doit donner le même résultat.
3. Ensuite seulement, je développe la partie 2 en appelant le code commun.

D'où la présence de deux sources

- [Day13-Transparent-Origami/C/2021-13-part1-only.](Day13-Transparent-Origami/C/2021-13-part1-only.c)
- [Day13-Transparent-Origami/C/2021-13.c](Day13-Transparent-Origami/C/2021-13.c)

**Algorithme**. Le fichier de données comporte un millier de lignes,
avec des coordonnées allant jusqu'à *(1310,997)*, j'ai préféré évité
de travailler sur une matrice de points. Surtout que c'est possible
avec un tableau de coordonnées *(x,y)* :

- Pour plier, on modifie les coordonnées de points qui sont
  "rabattus";
- Ça peut faire apparaître des doublons : on les élimine en triant et
  en "tassant" pour ôter les répétitions.

Pour l'affichage, on produit les "pixels" un par un, en comparant la
position du pixel courant et celle du prochain point à traiter (pris
dans le tableau ordonné). Si c'est la bonne position, on affiche un dièse,
si c'est un point de la même ligne un espace, sinon on saute à la ligne.

## Day 14 - Extended Polymerization - (C)

Pas besoin de collections, donc C fait l'affaire.

FORTRAN aurait fait le boulot (tableaux multidimensionnels !), et je
me rend compte après coup que la lecture des données n'aurait pas été
trop compliquée, parce que le format des règles

~~~
CH -> B
HH -> N
~~~

est fixe.

Ah oui, en COBOL aussi. À voir.


## Day 15 - Chiton - (Java)

Recherche de plus courte distance, 
encore Dijkstra...

Les sommets à visiter sont dans un `HashSet` c'est pas optimal,
mais ça demande moins de travail qu'un tas avec mise à jour.

Suffisant pour passer sur le gros exemple.


# Day 16 - Packet Decoder (C)

Décodage et traitement de paquets (binaires codés en hexa) imbriqués.
En C parce que ce n'est pas bien compliqué. Juste embêtant.

Il serait intéressant de revoir ça comme décodage d'un flux de bits, 
avec comme primitive

~~~C
	int next_bit(Generator *)
~~~

peut-être même en C++ pour avoir

~~~
class Generator {
public:
	int next_bit();
};
~~~

?

# Day 17 - Trick Shot (C)

J'étais occupé une grande partie de la journée à dépanner des trucs
chez des copains et aller assister à une thèse, et j'ai perdu un temps
conséquent à essayer d'éviter le "brute force" pour la première
partie. Bref, un jour de retard.

La seconde partie m'a ramenée au bon vieux bourrinage, qu'on pourrait
d'ailleurs améliorer un peu.

**Recherche des intervalles pour les vitesses initiales**

1.  On sait que la somme des entiers de 1 à
n s'obtient par

~~~C
int s(int n)
{
    return (n * (n+1))/2;
}
~~~

et avec un petit effort, en cherchant la racine positive d'une
équation du second degré, on a la fonction réciproque


~~~C
float sum_root (int d)
{
    return (sqrt(1.0 + 8.0 * d) - 1.0 ) /2;
}
~~~

dont on peut se servir pour calculer la vitesse horizontale minimale
pour atteindre le rectangle cible (à partir du x minimum). Je dis ça
mais j'ai eu la flemme de reporter ça dans les boucles sur `t`.

2. Pour la vitesse horizontale maximum, on peut prendre le x maximum.
Au delà, on ne risque pas de mettre un pied dans le rectangle.

3. Pour la vitesse verticale minimum, il suffit de viser très bas.
Avec *vy = y_min* on aura au mieux le coin bas-gauche de la cible,
avec *vx = 1*.

4. la vitesse verticale maximum, c'est plus amusant. Exemple, pour une
vitesse verticale de 3, on va monter pour atteindre le sommet *6 =
3+2+1* :

>  0, 0+3=3, 3+2=5, 5+1=6, 6+0=6

à partir de là, on redescend en passant par les mêmes altitudes

> 6-1=5, 5-2=3, 3-3=0

et donc repasser par 0. Le prochain niveau sera -4.
Et on ne pourra pas atteindre  les cases d'altitude -3 à -1.

On peut donc choisir, comme vitesse verticale maximum, l'opposé de $y_min$.
Avec une vitesse plus élevée, impossible d'atteindre la cible.


Ce raisonnement marche parce qu'on a des hypothèses tacites vérifiées
par les exemples, par exemple que la cible est (strictement) dans le
quart de plan en base à droite. Sinon ça serait une autre histoire.


# Day 18 - Snailfish (C++)

Toujours un jour de retard.

Me suis *évidemment* embarqué bille en tête dans le parsing pour
ramener la chaîne à un arbre (en Java). Pas de problème, c'est le genre de
choses que je sais bien faire, sauf que ça ne mène pas à grand
chose. *It's a trap*.

Seconde tentative, traiter directement la chaîne de caractères. Passage à
C++ pour changer. Mais là encore de petits problèmes avec les
"explode" qui peuvent fabriquent des nombres plus grands que 10.
Probablement aussi des erreurs en pensant pouvoir combiner
astucieusement le "number spliting" avec l'"exploding".
Bref, c'est pas *noethérien*, il faut vraiment appliquer les
2 étapes dans l'ordre.

Troisième tentative, la bonne, travailler sur un tableau d'*Items*,
qui sont des symboles (crochets, virgules) ou des valeurs numériques.
Pris comme ça, c'est beaucoup plus facile.

# Day 19 - Beacon-Scanner - (DELAYED)

Jamais fait ce genre de choses. J'y réfléchis. Je crois que j'ai une idée,
à base de

- projections sur les axes
- recherche des différences les plus fréquents entre les coordonnées
d'une map selon un axe et d'un autre selon un autre axe,
éventuellement retourné.

Encore à creuser. Je fais le 20 d'abord.


# Day 20 - Trench Map - (C++)

Suis tombé comme tout le monde sur le problème de la couleur
de fond d'une image infinie.

# Day 19 - Beacon-Scanner - (Java)

L'idée de compter les différences entre les points d'une liste A et 
d'une liste B était viable. Répétée pour les 24 rotations.

La bonne idée de programmation est d'avoir une méthode qui pour une
rotation R et deux listes A, B de coordonnées retourne éventuellement
(optionnel) la translation entre A et RB.

En fait, c'est très rapide. Pas besoin de projeter sur les axes. Il y
aurait sûrement des choses à factoriser si on voulait gagner du temps,
mais bon.

Je génère les rotations complétion (par produit) des 3 rotations d'un
1/4 de tour sur les 3 axes. C'est plus drôle que de les rentrer à la
main.

Pour la seconde partie, il y a une exploration du graphe des "scanners"
de proche en proche, ce qui permet de déterminer leur position.

# Day 20 - Dirac Dice (C++)

Gros calculs répétitifs avec récursion qui explose : traité avec
mémoization.

L'occasion de revoir comment on définit le hachage des classes
en C++.

Très efficace : seulement 6 ms.


##	À suivre.


...
