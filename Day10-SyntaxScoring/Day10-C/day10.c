// AOC2021 - day10.c -- syntax scoring
 
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

#define BUFFER_SIZE 1024
#define STACK_SIZE 256

int corruption_score(char line[])
{
    char stack[STACK_SIZE];
    int stack_size = 0;

    char *opening = "([{<";
    char *closing = ")]}>";
    int scores[] = { 3, 57, 1197, 25137};
    int i = 0;
    while(true) {
        char c = line[i++];
        char *p;
        if (strchr(opening, c) != NULL) {
            stack[stack_size++] = c;
        } else if((p = strchr(closing, c)) != NULL) {
            if (stack_size == 0) {
                // printf("stack underflow\n");
                return 0;
            }
            int type = p - closing;
            stack_size--;
            if (stack[stack_size] != opening[type]) {
                // printf("Closing char in string %c doesn't match opening %c\n",
                //        c, stack[stack_size]);
                return scores[type];
            }
        } else {
            break;
        }
    };
    return 0;
}


void part1(char filename[])
{
    FILE *in = fopen(filename, "r");
    if (in == NULL) {
		perror("trying to read data file in part 1");
        fprintf(stderr, "can't open %s\n", filename);
        exit(EXIT_FAILURE);
    }
    char line[BUFFER_SIZE];
    int total = 0;
    while (fgets(line, BUFFER_SIZE, in)) {
        int s = corruption_score(line);
        // printf("- \"%s\"Part 1 Score = %d\n", line, s);
        total += s;
    }
    printf("Part 1 total for %s = %d\n", filename, total);
    fclose(in);
}

long autocompletion_score(char line[])
{
    int stack[STACK_SIZE]; // pile de types
    int stack_size = 0;

    char *opening = "([{<";
    char *closing = ")]}>";
    long scores[] = { 1, 2, 3, 4};
    int i = 0;
    while(true) {
        char c = line[i++];
        char *p = strchr(opening, c);
        if (p != NULL) {
            stack[stack_size++] = p - opening;
        } else if((p = strchr(closing, c)) != NULL) {
            if (stack_size == 0) {
                // printf("stack underflow\n");
                return 0;
            }
            int type = p - closing;
            stack_size--;

            if (stack[stack_size] != type) {
                // printf("Closing char in string %c doesn't match opening %c\n",
                //        c, stack[stack_size]);
                return 0;
            }
        } else {
            break;
        }
    };
    // what's left in the stack ?
    long score = 0;
    // printf("Completed by  ");
    for (int i = stack_size - 1; i >= 0; i--) {
        // printf("%c", closing[stack[i]]);
        score = score * 5 + scores[stack[i]];
    }
    // printf("   value = %ld\n", score);
    return score;

}

void part2(char filename[])
{
    FILE *in = fopen(filename, "r");
    if (in == NULL) {
		perror("trying to read data file in part 2");
        fprintf(stderr, "can't open %s\n", filename);
        exit(EXIT_FAILURE);
    }
  
	// data set is rather small (less than 100 lines), 
	// use insertion in ordered arra of scores
    long all_scores[100];
    int nb_scores = 0; 
	char line[BUFFER_SIZE];
    while (fgets(line, BUFFER_SIZE, in)) {
        long s = autocompletion_score(line);
        // printf("- \"%s\"Part 2 Score = %ld\n", line, s);
        if (s != 0) {
            // insertion
            int hole = nb_scores++;
            while (hole > 0 && all_scores[hole - 1] > s) {
                all_scores[hole] = all_scores[hole - 1];
                hole--;
            }
            all_scores[hole] = s;
        }
    }
    /*
    printf(" scores = ");
    for (int i = 0; i < nb_scores; i++) {
    	printf("%ld ", all_scores[i]);
    }
    printf("\n");
    */
    long middle_score = all_scores[nb_scores / 2];
    printf("Part 2 score for %s = %ld\n", filename, middle_score);
    fclose(in);
}

void process_file(char filename[])
{
    part1(filename);
    part2(filename);
}

int main()
{
    printf("This is day #10\n");
    process_file("../small.txt");
    process_file("../large.txt");
}
