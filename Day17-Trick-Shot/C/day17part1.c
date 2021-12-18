#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>

// AOC 2021 - Day 17 - part 1
// M billaud 18 dec 2021 (1 day late)

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


void solve(int x_min, int x_max, int y_min, int y_max)
{
    printf("# solving for [%d..%d] x [%d..%d]\n",
           x_min, x_max, y_min, y_max
          );

    int t_max = - 2* y_min + 1;
    printf("tmax = %d\n", t_max);

    // printf("# remplissage tableau des solutions horizontales\n") ;
    int vx_t[2000] = {0};
    for (int vx0 = 1; vx0 <= t_max; vx0++) {
        for (int t = 1; t <= t_max; t++) {
            int x = x_position(vx0, t);
            if (x > x_max) break;
            if (x >= x_min) {
                vx_t[t] = vx0;
                // printf(" t=%d\tvx0=%d\n", t, vx_t[t]);
            }
        }
    };

    // printf("# tableau des solutions axe vertical\n");
    int vy_t[1000] = {0};
    for (int vy0 = -y_min; vy0 >= y_min; vy0--) {
        for (int t = 1; t <= t_max; t++) {
            int y = y_position(vy0, t);
            if (y < y_min && y_speed(vy0, t) < 0) break;
            if (y_min <= y && y <= y_max) {
                vy_t[t] = vy0;
                // printf(" t=%d\tvy0=%d\n", t, vy_t[t]);
                if (vx_t[t] != 0) {
                    printf("** found t = %d (%d, %d) max = %d\n",
                           // t, vx_t[t], vy_t[t], s(vy_t[t]));
                           t, vx_t[t], vy0, s(vy0));
                    return;
                }
            }
        }
    }
}

int main()
{
    printf("Day 17 - part 1 - answer is y highest position\n");

    solve(20,  30, -10, -5);
    solve(153, 199, -114, -75);


    return EXIT_SUCCESS;
}
