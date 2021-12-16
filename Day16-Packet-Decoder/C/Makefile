CFLAGS = -std=c17
CFLAGS += -Wall -Wextra -pedantic -Werror -Wno-unused
CFLAGS += -MMD
CFLAGS += -D_XOPEN_SOURCE=700

CFLAGS += -g
EXECS = packet_decoder_part2 packet_decoder_part1

RUNNABLE = packet_decoder_part2


all : $(EXECS)

run: $(RUNNABLE)
	./$<

prog: prog.o # bar.o

-include $(wildcard *.d)

clean:
	$(RM) *~ *.o *.d

mrproper: clean
	$(RM) $(EXECS)
