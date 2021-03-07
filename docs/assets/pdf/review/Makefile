PROJECT=main
TEX_ENGINE=xelatex

BUILDTEX=$(TEX_ENGINE) -shell-escape $(PROJECT).tex
REMOVE_AUXILIARY=make clean

all:
	xelatex -shell-escape main
	makeindex main.idx -s StyleInd.ist
	biber main
	xelatex -shell-escape main x 2
	$(REMOVE_AUXILIARY)

clean-all:
	rm -rf _minted-main *.aux *.bbl *.bcf *.blg *.idx *.ilg *.ind *.log *.ptc main.run.xml *.toc main.pdf *~

clean:
	rm -rf _minted-main *.aux *.bbl *.bcf *.blg *.idx *.ilg *.ind *.log *.ptc main.run.xml *.toc *~
