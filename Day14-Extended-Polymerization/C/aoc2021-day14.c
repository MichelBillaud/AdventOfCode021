#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <ctype.h>
#include <limits.h>

// AOC 2021 - Day 14 Extended Polymerization - parts 1 and 2

// Change from part1: 
// int counters now use long int type

/*
DATA STRUCTURES.
    As templates grow exponentially, it was clear from the start
    that actually computing them was an unsuitable idea.

    Instead, we count _pairs_ of adjacent letters in templates.
    There are not too many pairs: 26*26 = 676, so they fit into
    a one dimensional array.

    Also, there is at most one rule for each XY pair of letter,
    so "inserted" letter for each pair are stored as another 26*26 array.

COMPUTING THE NEXT TEMPLATE.
    If we have N occurrences of LR in a template,
    a rule LR -> M will add N occurrences of LM and MR in the new
    template.
    Remark that the first and last letter are unchanged.

FINAL STATISTICS.
    A 26 integer array to count the pairs according to 
    their _right_ letter.
    Add 1 for the leftmost letter.

*/


#define LINE_LENGTH 100

#define val(a_letter) ((a_letter) - 'A')
#define chr(an_int) ((an_int) + 'A')
#define pair(left_int, right_int) (26 * (left_int) + (right_int))
#define left(pair_number) ((pair_number) / 26)
#define right(pair_number) ((pair_number) % 26)

void count_template(const char template[], long nb_pairs[])
{
    for (int i = 0; i < 26*26; i++) {
        nb_pairs[i] = 0L;
    }
    for (int i = 0; isupper(template[i+1]); i++) {
        int pair = pair(val(template[i]), val(template[i+1]));
		nb_pairs[pair] += 1;
    }
}

void execute_step(long nb_pairs[26*26], const int insertions[26*26])
{
    long tmp [26*26] = {0};
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
    long nb_pairs[26 * 26]; // pairs in template
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
        char left, right, middle;
        sscanf(line, "%c%c -> %c", &left, &right, &middle);
        int pair = pair(val(left), val(right));
        insertions[pair] = val(middle);
    }
    fclose(datafile);
  
    // apply steps 
    for (int k = 0; k < nb_steps; k++) {
        execute_step(nb_pairs,  insertions)	;
    }
    // compute stats
    long count[26] = {0};
    count[first] = 1L;
    for (int p = 0; p < 26*26; p++) {
        count[right(p)] += nb_pairs[p];
    }
  
    long min = LONG_MAX, max = LONG_MIN;;
    for (int i = 0; i < 26; i ++) {
        if (count[i] > 0) {
            if (count[i] < min) min = count[i] ;
            if (count[i] > max) max = count[i] ;
        }
    }

    // let's do it now
    printf("- in '%s' after %d steps, "
           "max %ld - min %ld = %ld\n",
           filename, nb_steps, max, min, max-min);

}

int main()
{
    printf("# AOC 2021 - Day 14 Extended Polymerization - Parts 1 and 2\n");
    
    printf("# Part 1\n");
    run_example("../small.txt", 10);
    run_example("../large.txt", 10);

    printf("# Part 2\n");
    run_example("../small.txt", 40);
    run_example("../large.txt", 40);
    
    return EXIT_SUCCESS;
}
