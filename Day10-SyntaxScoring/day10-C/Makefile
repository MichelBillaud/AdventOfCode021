CFLAGS = -std=c17
CFLAGS += -Wall -Wextra -pedantic -Werror -Wno-unused
CFLAGS += -MMD
# CFLAGS += -D_POSIX_C_SOURCE=200809L
# CFLAGS += -g

EXECS = day10


all : $(EXECS)

day10: day10.o

-include $(wildcard *.d)

clean:
	$(RM) *~ *.o *.d

mrproper: clean
	$(RM) $(EXECS)