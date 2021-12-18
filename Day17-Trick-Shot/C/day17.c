#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>

// AOC 2021 - Day 17 - parts 1 and 2
// M billaud 18 dec 2021 (1 day late)

// turned into brute force computing !

int s(int n)
{
    return (n * (n+1))/2;
}

int sum(int a, int b)
{
    return ((a+b) *((abs(b-a)) +1)) / 2;
}

// solution of x(x+1)/2 = d
// ok at least for x = 1 .. 10000
float sum_root (int d)
{
    return (sqrt(1.0 + 8.0 * d) - 1.0 ) /2;
}

int y_speed(int vy, int t)
{
    return vy - t;
}

int y_position(int vy, int t)
{
    return vy * t - s(t-1);
}

int x_position(int vx, int t)
{
    if (t > vx) t = vx;
    return ((2*vx - t + 1)*t)/2;
}

// stop worrying and use brute force

void part1(int x_min, int x_max, int y_min, int y_max)
{
    printf("# Part 1 solving for [%d..%d] x [%d..%d]\n",
           x_min, x_max, y_min, y_max
          );

    int t_max = 1000; // - 2* y_min + 1; ?

    // initial y speed in decreasing order,
    // so the first solution is the best one.

    for  (int vy0 = -y_min; vy0 >= y_min; vy0--) {
        for (int vx0 = 1; vx0 <= t_max; vx0++) {
            for (int t = 1; t <= t_max; t++) {
                int x = x_position(vx0, t);
                if (x > x_max) break;

                int y = y_position(vy0, t);
                if (y < y_min && y_speed(vy0, t) < 0) break;

                if (x_min <= x && x <= x_max
                        && y_min <= y && y <= y_max) {
                    printf("- initial speed (%d,%d) leads "
                           "to (%d, %d) in target after %d steps, "
                           "\n- max altitude is %d.\n",
                           vx0, vy0, x, y, t, s(vy0));
                    return;
                }
            }
        }
    }
}

void part2(int x_min, int x_max, int y_min, int y_max)
{
    printf("# Part 2 solving for [%d..%d] x [%d..%d]\n",
           x_min, x_max, y_min, y_max
          );

    int t_max = 1000; // - 2* y_min + 1; ?

    // bourrin
    int nb_solutions = 0;
    for (int vx0 = 1; vx0 <= t_max; vx0++) {
        for  (int vy0 = -y_min; vy0 >= y_min; vy0--) {
            for (int t = 1; t <= t_max; t++) {
                int x = x_position(vx0, t);
                if (x > x_max) break;

                int y = y_position(vy0, t);
                if (y < y_min && y_speed(vy0, t) < 0) break;

                if (x_min <= x && x <= x_max
                        && y_min <= y && y <= y_max) {
                    nb_solutions ++;
                    break;
                }
            }
        }
    }
    printf("- %d solutions found.\n", nb_solutions);
}

int main()
{
    printf("\n# Day 17 - part 1 - answer is maximum altitude\n\n");

    part1(20,  30, -10, -5);
    part1(153, 199, -114, -75);

    printf("\n# Day 17 - part 2 - answer is number of solutions\n\n");

    part2(20,  30, -10, -5);
    part2(153, 199, -114, -75);


    return EXIT_SUCCESS;
}
