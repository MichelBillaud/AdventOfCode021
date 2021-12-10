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
malheureux tableau de `long`, et de même pour réécrire un tri en $O(n
log n)$. Il n'y a jamais qu'au pire une centaine éléments à ordonner,
l'insertion au fur et à mesure dans un tableau ordonné, ça fait bien
l'affaire.

**PS** : après coup, j'ai refait la partie 1 en TypeScript, que je ne maîtrise 
pas du tout (lecture asynchrone du fichier...)

##	À suivre.

...
