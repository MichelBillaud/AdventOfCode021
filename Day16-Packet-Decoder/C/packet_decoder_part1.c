#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

// AOC 2021 - 16 dec 2021 - Packet Decoder - part1 only

int val(char c)
{
    // not EBCDIC friendly :-)
    return c >= 'A' ? c - 'A' + 10 : c - '0';
}

int bit(char string[], int position)
{
    int char_number = position / 4;
    int bit_number = position % 4;
    return (val(string[char_number]) >> (3 - bit_number)) & 1;
}

unsigned long long extract_bits(char string[], int start, int length)
{
    unsigned long long result = 0;
    for (int i = 0; i < length; i++) {
        result = (result << 1) | bit(string, start + i);
    }
    return result;
}

/* PRELIMINARY WORK */

int explain_packet(char string[], int start);

int explain_literal_packet(char string[], int start)
{
    int next = start + 6;
    long long literal_value = 0;
    while(true) {
        unsigned slice = extract_bits(string, next, 5);
        next += 5;
        literal_value = 16 * literal_value + (slice & 0xF);
        if ((slice & 0x10) == 0) break;
    }
    printf("=> Literal value: %lld\n", literal_value);
    return next;
}

int explain_operator_packet(char string[], int start)
{
    int length_type_id = extract_bits(string, start + 6, 1);
    printf("length type id = %d\n", length_type_id);
    int len = length_type_id == 0 ? 15 : 11;
    printf("length = %d\n", len);
    int number = extract_bits(string, start + 7, len);
    printf("length/number sub packets = %d\n", number);
    int next = start + 7 + len;
    if (length_type_id == 0) {
        int end = next + number;
        while (next < end) {
            next = explain_packet(string, next);
        }
    } else {
        for (int i = 0; i < number; i++) {
            next = explain_packet(string, next);
        }
    }
    return next;
}

int explain_packet(char string[], int start)
{
    int packet_version = extract_bits(string, start + 0, 3);
    int type_id = extract_bits(string, start+3, 3);
    printf("Version: %d\n", packet_version);
    printf("   Type: %d\n", type_id );
    return (type_id == 4)
           ? explain_literal_packet(string, start)
           : explain_operator_packet(string, start);
}

void explain_string(char string[])
{
    printf("String : %s\n", string);
    int len = strlen(string);
    printf("Bits   : ");
    for (int i=0; i < 4*len; i++) {
        printf("%d", bit(string, i));
    }
    printf("\n");
    explain_packet(string, 0);
    printf(".\n");
}

void preliminary_tests()
{
    // explain_string("D2FE28");
    // explain_string("38006F45291200");
    explain_string("EE00D40C823060");
}

// STUFF For part 1. Same structure as preliminary stuff, but
// with sum of version numbers and sum of literals (an accident
// because I didn't read the indications carefuly enough,
// but I keep it as suspect this is for the 2nt part
// with the usual considerations on int/long/long long.

int process_packet(char string[], int start,
                   int * sum_versions,
                   long long *sum_literals);

int process_literal_packet(char string[], int start, 
                   int * sum_versions,
                   long long *sum_literals)
{
    int next = start + 6;
    long long literal_value = 0L;
    while(true) {
        unsigned slice = extract_bits(string, next, 5);
        next += 5;
        literal_value = 16 * literal_value + (slice & 0xF);
        if ((slice & 0x10) == 0) break;
    }
    *sum_literals += literal_value;
    return next;
}

int process_operator_packet(char string[], int start,
                   int * sum_versions,
                   long long *sum_literals)
{
    int length_type_id = extract_bits(string, start + 6, 1);
    int len = length_type_id == 0 ? 15 : 11;
    int number = extract_bits(string, start + 7, len);
    int next = start + 7 + len;
    if (length_type_id == 0) {
        int end = next + number;
        while (next < end) {
            next = process_packet(string, next, sum_versions, sum_literals);
        }
    } else {
        for (int i = 0; i < number; i++) {
            next = process_packet(string, next, sum_versions, sum_literals);
        }
    }
    return next;
}

int process_packet(char string[], int start, 
                   int * sum_versions,
                   long long *sum_literals)
{
    int packet_version = extract_bits(string, start + 0, 3);
    int type_id = extract_bits(string, start+3, 3);
    *sum_versions += packet_version;
    return (type_id == 4)
           ? process_literal_packet(string, start, sum_versions, sum_literals)
           : process_operator_packet(string, start, sum_versions, sum_literals);
}



int process_string_part1(char string[])
{
    int sum_versions = 0;
    long long sum_literals = 0LL;

    process_packet(string, 0, &sum_versions, &sum_literals);
    return sum_versions;
}

void part1_tests()
{
    char *strings[] = {"D2FE28", "38006F45291200", "EE00D40C823060", NULL};
    for (char **p = strings; *p != NULL; p++) {
        printf("Test P1> %s => %d\n",
               *p, process_string_part1(*p));
    }
}

void part1(const char filename[])
{
    FILE * datafile = fopen(filename, "r");
    if (datafile == NULL) {
        perror("cant open data file");
        exit(EXIT_FAILURE);
    };
    char buffer[2048];
    fgets(buffer, 2048, datafile);
    fclose(datafile);
    explain_string(buffer);
    printf("# part 1: sum of version numbers for '%s' is %d\n",
           filename, process_string_part1(buffer));

}

int main()
{
    printf("AOC Day 16 - part 1 only\n");
    // preliminary_tests();
    part1_tests();
    part1("../large.txt");
    return EXIT_SUCCESS;
}
