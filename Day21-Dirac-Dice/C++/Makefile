CXXFLAGS = -std=c++20
CXXFLAGS += -Wall -Wextra -pedantic -Werror -Wno-unused
# CXXFLAGS += -MMD

#CXXFLAGS += -g
CXXFLAGS += -O3

EXECS = day20part1 day20part2


all : $(EXECS)


-include $(wildcard *.d)

clean:
	$(RM) *~ *.o *.d

mrproper: clean
	$(RM) $(EXECS)
