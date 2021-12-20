CXXFLAGS = -std=c++20
CXXFLAGS += -Wall -Wextra -pedantic -Werror -Wno-unused
# CXXFLAGS += -MMD

CXXFLAGS += -g

RUN = day20
EXECS = $(RUN)

all : $(EXECS)

run: $(RUN)
	./$(RUN)

-include $(wildcard *.d)

clean:
	$(RM) *~ *.o *.d

mrproper: clean
	$(RM) $(EXECS)
