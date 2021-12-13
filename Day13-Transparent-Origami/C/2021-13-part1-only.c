#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#define MAX_NB_POINTS 1000

struct point {
    int x, y;
};

void part1(const char filename[]);

int main()
{
    printf("AOC 2021 - Day 13 - Transparent Origami\n");

    part1("../small.txt");
    part1("../large.txt");

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
    int r = p1->x - p2->x;
    return (r == 0) ? (p1->y - p2->y) : r;
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

void part1(const char filename[])
{
    FILE * datafile = fopen(filename, "r");
    if (datafile == NULL) {
        perror("reading data file");
        exit (EXIT_FAILURE);
    }
    struct point points[MAX_NB_POINTS];

    // 1. load the points
    int nb_points = 0;
    while (true) {
        char line[80];
        fgets(line, 80, datafile);
        struct point p;
        if (sscanf(line, "%d,%d", &p.x, &p.y) == 2) {
            points[nb_points++] = p;
        } else {
            break;
        }
    }
    // check
    /*
    for (int i = 0; i < nb_points; i++) {
        printf("%d %d\n", points[i].x, points[i].y);
    }
    */

    // 1. read folding instruction
    char line[80];
    fgets(line, 80, datafile);
    char filler1[10], filler2[10], letter;
    int middle;
    sscanf(line, "%s %s %c=%d", filler1, filler2, &letter, &middle);
    // printf("folding along %c axis with middle at %d\n", letter, middle);

    // 2. folding
    for (int i = 0; i < nb_points; i++) {
        if (letter == 'x') {
            fold(& points[i].x, middle);
        } else {
            fold(& points[i].y, middle);
        }
    }

    // 3. removing duplicates
    // printf("before = %d points\n", nb_points);
    sort_and_deduplicate(points, &nb_points);

    printf("Part 1, %s, after 1 folding, %d points\n",
           filename, nb_points);
    fclose(datafile);
}
