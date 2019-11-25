PROGRAM = solution
PROGRAM_DBG = solution.dbg
CC = gcc
CFLAGS = -Wall -O2
DEBUG = -g

all: $(PROGRAM)
debug: CFLAGS += $(DEBUG)
debug: $(PROGRAM_DBG)

parser.o: parser.c parser.h
	$(CC) -c $(CFLAGS) -o $@ $<

list.o: list.c list.h
	$(CC) -c $(CFLAGS) -o $@ $<

tree.o: tree.c tree.h list.h
	$(CC) -c $(CFLAGS) -o $@ $<

$(PROGRAM).o: $(PROGRAM).c
	$(CC) -c $(CFLAGS) -o $@ $<

$(PROGRAM): parser.o list.o tree.o $(PROGRAM).o
	$(CC) -o $@ $^

$(PROGRAM_DBG).o: $(PROGRAM).c
	$(CC) -c $(CFLAGS) -o $@ $<

$(PROGRAM_DBG): parser.o list.o tree.o $(PROGRAM_DBG).o
	$(CC) -o $@ $^

.PHONY: all clean

clean:
	rm -rf $(PROGRAM) $(PROGRAM_DBG) *.o