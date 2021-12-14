#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <ctype.h>
#include <limits.h>

// AOC 2021 - Day 14 Extended Polymerization - part 1 only


#define LINE_LENGTH 100

#define val(a_letter) ((a_letter) - 'A')
#define chr(an_int) ((an_int) + 'A')
#define pair(left_int, right_int) (26 * (left_int) + (right_int))
#define left(pair_number) ((pair_number) / 26)
#define right(pair_number) ((pair_number) % 26)

void count_template(const char template[], int nb_pairs[])
{
    for (int i = 0; i < 26*26; i++) {
        nb_pairs[i] = 0;
    }
    for (int i = 0; isupper(template[i+1]); i++) {
        int pair = pair(val(template[i]), val(template[i+1]));
		/*
        printf("adding pair %c %c at position %d\n",
               template[i], template[i+1], pair);
        printf("\t position known as %c %c\n",
               chr(left(pair)), chr(right(pair)));
        */
		nb_pairs[pair] += 1;
    }
}

void show_pairs(const char message[], const int nb_pairs[26*26])
{
    printf("%s: ", message);
    for (int p = 0; p < 26*26; p++) {
        if (nb_pairs[p] != 0) {
            printf("%c%c*%d ", chr(left(p)), chr(right(p)), nb_pairs[p]);
        }
    }
    printf("\n");
}

void step(int nb_pairs[26*26], const int insertions[26*26])
{
    int tmp [26*26] = {0};
    for (int p = 0; p < 26*26; p++) {
        if (insertions[p] >= 0) {
            tmp[pair(left(p), insertions[p])] += nb_pairs[p];
            tmp[pair(insertions[p], right(p))] += nb_pairs[p];
        }
    }
    for (int i = 0; i < 26*26; i++) {
        nb_pairs[i] = tmp[i];
    }
}

void run_example(const char filename[], int nb_steps)
{
    FILE *datafile = fopen(filename, "r");
    if (datafile == NULL) {
        perror("reading data file");
        exit (EXIT_FAILURE);
    };
    int nb_pairs[26 * 26]; // pairs in template
    char line[LINE_LENGTH];

    fgets(line, LINE_LENGTH, datafile);
    count_template(line, nb_pairs);
    int first = val(line[0]);

    // forget empty line
    fgets(line, LINE_LENGTH, datafile);

    // read rules
    int insertions[26*26];
    for (int i = 0; i < 26*26; i++) insertions[i] = -1;

    while (fgets(line, LINE_LENGTH, datafile) != NULL) {
        // printf("rule> %s", line);
        char left, right, middle;
        sscanf(line, "%c%c -> %c", &left, &right, &middle);
        // printf("- insertion de %c entre %c et %c\n", middle, left, right);
        int pair = pair(val(left), val(right));
        insertions[pair] = val(middle);
    }
    fclose(datafile);
    // show_pairs("before", nb_pairs);

    for (int k = 0; k < nb_steps; k++) {
        // printf("step %d\n", k);
        step(nb_pairs,  insertions)	;
        // show_pairs("after", nb_pairs);
    }
    // stats
    int count[26] = {0};
    count[first] = 1;
    for (int p = 0; p < 26*26; p++) {
        count[right(p)] += nb_pairs[p];
    }
    for (int i = 0; i < 26; i++) {
        if (count[i] != 0) printf("%c*%d ", chr(i), count[i]);
    }
    printf("\n");
    int min = INT_MAX, max = INT_MIN;;
    for (int i = 0; i < 26; i ++) {
        if (count[i] > 0) {
            if (count[i] < min) min = count[i] ;
            if (count[i] > max) max = count[i] ;
        }
    }

    printf("Part 1, in '%s' after %d steps, "
           "max %d - min %d = %d\n",
           filename, nb_steps, max, min, max-min);

}


int main()
{
    printf("# AOC 2021 - Day 14 Extended Polymerization - Part 1\n");
    run_example("../small.txt", 10);
    run_example("../large.txt", 10);
    return EXIT_SUCCESS;
}
