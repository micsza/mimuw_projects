all:
	happy -gca ParBlah.y
	alex -g LexBlah.x
	ghc Main.hs

clean:
	-rm -f *.log *.aux *.hi *.o *.dvi

distclean: clean
	-rm -f DocBlah.* LexBlah.* ParBlah.* LayoutBlah.* SkelBlah.* PrintBlah.* TestBlah.* AbsBlah.* TestBlah ErrM.* SharedString.* ComposOp.* Blah.dtd XMLBlah.* Makefile*
