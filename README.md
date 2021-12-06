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

et aussi la génération des points d'une ligne

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

**Langage.** L'analyse de la ligne de données (une grande chaîne, avec des champs
séparés par des virgules), aurait compliqué un programmation en
Fortran.  Va pour Java. Ca aurait été faisable en C, je n'y ai pas
pensé sur le moment.  Il va falloir que je pense à changer de langage,
la prochaine fois.

**Algorithme.** Ai eu la chance de voir l'astuce (tableau de comptage
par état) dès le début.

Comme tout le monde, la différence entre la partie 1 et la partie 2,
c'était le débordement des entiers, réglé par un passage de `int` à `long`.

Évidemment, sauf si on a eu la mauvaise idée de travailler sur une
liste des états des poissons, ça explose en taille et temps de calcul
(en gros, doublement toutes les 6 étapes ? et il y en a 256 !)

##	à suivre.

...
