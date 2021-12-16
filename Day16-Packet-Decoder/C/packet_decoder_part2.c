#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

// AOC 2021 - 16 dec 2021 - Packet Decoder - part 2
// Michel Billaud

// 
typedef unsigned long long int big_number;

// big number format specifie
#define BNF "%lld"

int val(char c)
{
    // not EBCDIC friendly
    return c >= 'A' ? c - 'A' + 10 : c - '0';
}

int bit(char string[], int position)
{
    int char_number = position / 4;
    int bit_number = position % 4;
    return (val(string[char_number]) >> (3 - bit_number)) & 1;
}

big_number extract_bits(char string[], int start, int length)
{
    big_number result = 0;
    for (int i = 0; i < length; i++) {
        result = (result << 1) | bit(string, start + i);
    }
    return result;
}


int evaluate_packet(char string[], int start,
                    big_number *value);

int evaluate_literal_packet(char string[], int start,
                            big_number *value)
{
    int next = start + 6;
    big_number literal_value = 0L;
    while(true) {
        unsigned slice = extract_bits(string, next, 5);
        next += 5;
        literal_value = (literal_value << 4) + (slice & 0xF);
        if ((slice & 0x10) == 0) break;
    }
    *value = literal_value;
    return next;
}

void combine_sum(big_number *acc, big_number val, bool first)
{
    *acc = first ? val : *acc + val;
}

void combine_product(big_number *acc, big_number val, bool first)
{
    *acc = first ? val : *acc * val;
}

void combine_min(big_number *acc, big_number val, bool first)
{
    if (first || (val < *acc)) *acc = val;
}

void combine_max(big_number *acc, big_number val, bool first)
{
    if (first || (val > *acc)) *acc = val;
}

void combine_greater(big_number *acc, big_number val, bool first)
{
    *acc = first ? val : (*acc > val);
}

void combine_less(big_number *acc, big_number val, bool first)
{
    *acc = first ? val : (*acc < val);
}

void combine_equal(big_number *acc, big_number val, bool first)
{
    *acc = first ? val : (*acc == val);
}

typedef void (*combiner)(big_number *, big_number, bool);

const combiner op[] = {
    combine_sum,
    combine_product,
    combine_min,
    combine_max,
    NULL,
    combine_greater,
    combine_less,
    combine_equal
};

int evaluate_operator_packet(char string[], int start,
                             big_number *result)
{
    int type_id = extract_bits(string, start+3, 3);
    int length_type_id = extract_bits(string, start + 6, 1);
    int len = length_type_id == 0 ? 15 : 11;
    int number = extract_bits(string, start + 7, len);
    int next = start + 7 + len;
    bool first = true;

    if (length_type_id == 0) {
        int end = next + number;
        while (next < end) {
            big_number value;
            next = evaluate_packet(string, next,  &value);
            op[type_id](result, value, first);
            first = false;
        }
    } else {
        for (int i = 0; i < number; i++) {
            big_number value;
            next = evaluate_packet(string, next, &value);
            op[type_id](result, value, first);
            first = false;
        }
    }
    return next;
}

int evaluate_packet(char string[], int start,
                    big_number *value)
{
    int packet_version = extract_bits(string, start + 0, 3);
    int type_id = extract_bits(string, start+3, 3);

    return (type_id == 4)
           ? evaluate_literal_packet(string, start, value)
           : evaluate_operator_packet(string, start, value);
}

big_number process_string_part2(char string[])
{
    big_number value;

    evaluate_packet(string, 0, &value);
    return value;
}

void part2_tests()
{
    char *strings[] = {
        "D2FE28",          // 2021
        "38006F45291200",  // 1
        "EE00D40C823060",  // 3
        "C200B40A82",      // 3
        "04005AC33890",    // 54
        "880086C3E88112",  // 7
        "CE00C43D881120",  // 9 finds the maximum of 7, 8, and 9, resulting in the value 9.
        "D8005AC2A8F0",    // 1, because 5 is less than 15.
        "F600BC2D8F",      // 0, because 5 is not greater than 15.
        "9C005AC2F8F0",    // 0, because 5 is not equal to 15.
        "9C0141080250320F1802104A08", // 1,
        NULL
    };
    for (char **p = strings; *p != NULL; p++) {
        printf("Test P2> %s => " BNF "\n",
               *p, process_string_part2(*p));
    }
}

void part2(const char filename[])
{
    FILE * datafile = fopen(filename, "r");
    if (datafile == NULL) {
        perror("cant open data file");
        exit(EXIT_FAILURE);
    };
    char buffer[2048];
    fgets(buffer, 2048, datafile);
    fclose(datafile);
 
    printf("# part 2: value for '%s' is " BNF "\n",
           filename, process_string_part2(buffer));
}

int main()
{
    printf("AOC Day 16 - part 2 only\n");

    part2_tests();
    part2("../large.txt");

    return EXIT_SUCCESS;
}
