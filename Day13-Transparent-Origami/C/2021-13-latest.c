#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

// AOC 2021 - Day 13
// Michel Billaud,  13 dec 2021
// the latest version

#define MAX_NB_POINTS 1000

struct point {
    int x, y;
};

struct instruction {
    bool x_axis;
    int middle;
};

/*
    refactoring de la partie 1
    - fonction pour lecture des points
    - fonction pour lecture d'instruction de pliage
    - fonction pour exécution pliage
    - changement comparateur pour trier par y puis c

    développement de la partie 2
    - boucle de lecture+traitement des instructions de pliage
    - affichage du tableau final.

    améliorations après coup
    - regroupement x_axis et middle dans structure instruction
*/

void part1(const char filename[]);
void part2(const char filename[]);

int main()
{
    printf("AOC 2021 - Day 13 - Transparent Origami\n");

    part1("../small.txt");
    part1("../large.txt");
    part2("../small.txt");
    part2("../large.txt");

    return EXIT_SUCCESS;
}

void fold(int *coord, int middle)
{
    if (*coord > middle) {
        *coord = 2*middle - *coord;
    }
}

int compare_points(const void *ptr1, const void *ptr2)
{
    const struct point *p1 = ptr1, *p2 = ptr2;
    int r = p1->y - p2->y;
    return (r == 0) ? (p1->x - p2->x) : r;
}

void sort_and_deduplicate(struct point points[], int *nb_points)
{
    qsort(points, *nb_points, sizeof(points[0]), compare_points);
    int dst = 1;
    for (int src = 1; src < *nb_points; src++) {
        if (compare_points(& points[src], &points[dst-1])) {
            points[dst ++] = points[src];
        }
    }
    *nb_points = dst;
}

void load_points(FILE *datafile, struct point points[], int *nb_points)
{
    *nb_points = 0;
    while (true) {
        char line[80];
        fgets(line, 80, datafile);
        struct point p;
        if (sscanf(line, "%d,%d", &p.x, &p.y) == 2) {
            points[(*nb_points)++] = p;
        } else {
            break;
        }
    }
}

void fold_array(struct point points[], int nb_points, const struct instruction *inst )
{
    for (int i = 0; i < nb_points; i++) {
        if (inst->x_axis) {
            fold(& points[i].x, inst->middle);
        } else {
            fold(& points[i].y, inst->middle);
        }
    }
}

bool read_instruction(FILE * datafile, struct instruction *inst)
{
    char line[80];
    if (fgets(line, 80, datafile) == NULL) {
        return false;
    };
    char filler1[10], filler2[10], letter;
    int r = sscanf(line, "%s %s %c=%d",
                   filler1, filler2,
                   &letter, & inst->middle);
    inst->x_axis = (letter == 'x');
    return r == 4;
}

void part1(const char filename[])
{
    FILE * datafile = fopen(filename, "r");
    if (datafile == NULL) {
        perror("reading data file");
        exit (EXIT_FAILURE);
    }

    // 1. load points
    struct point points[MAX_NB_POINTS];
    int nb_points;
    load_points(datafile, points, &nb_points);

    // 2. read folding instruction
    struct instruction inst;
    read_instruction(datafile, &inst);
    // printf("folding along %c axis with middle at %d\n", letter, middle);

    // 3. fold
    fold_array(points, nb_points, & inst);

    // 4. remove duplicates
    sort_and_deduplicate(points, &nb_points);

    printf("Part 1, %s, after 1 folding, %d points\n",
           filename, nb_points);
    fclose(datafile);
}

void display_points(struct point points[], int nb_points, char *dot, char *space )
{
    int x = 0, y = 0;
    int next_p = 0;
    while (next_p < nb_points) {
        if (points[next_p].y == y) { // same line
            if (points[next_p].x == x) { // right position
                printf("%s", dot);
                next_p ++;
            } else {   // other position on the same line
                printf("%s", space);
            }
            x++;
        } else {
            printf("\n"); // other line
            y ++;
            x = 0;
        }
    }
    printf("\n");
}

void part2(const char filename[])
{
    FILE * datafile = fopen(filename, "r");
    if (datafile == NULL) {
        perror("reading data file");
        exit (EXIT_FAILURE);
    }

    // load points
    struct point points[MAX_NB_POINTS];
    int nb_points;
    load_points(datafile, points, &nb_points);

    // read and process instructions
    for (struct instruction inst; read_instruction(datafile, &inst); ) {
        fold_array(points, nb_points, &inst);
    }

    sort_and_deduplicate(points, &nb_points);

    printf("Part 2, %s, after foldings, %d points\n",
           filename, nb_points);
    printf("~~~\n");
    display_points(points, nb_points, "[]", "  ");
    fclose(datafile);
    printf("~~~\n");
}
