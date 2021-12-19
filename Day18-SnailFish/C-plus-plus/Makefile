CXXFLAGS = -std=c++20
CXXFLAGS += -Wall -Wextra -pedantic -Werror -Wno-unused
# CXXFLAGS += -MMD

CXXFLAGS += -g

EXECS = snail-fish


all : $(EXECS)


-include $(wildcard *.d)

clean:
	$(RM) *~ *.o *.d

mrproper: clean
	$(RM) $(EXECS)
