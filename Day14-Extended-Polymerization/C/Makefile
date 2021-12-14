CFLAGS = -std=c17
CFLAGS += -Wall -Wextra -pedantic -Werror -Wno-unused
CFLAGS += -MMD
CFLAGS += -D_XOPEN_SOURCE=700

CFLAGS += -g
EXECS = aoc2021-day14-part1
EXECS += aoc2021-day14

all : $(EXECS)

-include $(wildcard *.d)

clean:
	$(RM) *~ *.o *.d

mrproper: clean
	$(RM) $(EXECS)
