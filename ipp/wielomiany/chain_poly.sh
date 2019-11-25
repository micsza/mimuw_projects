#!/bin/bash

# Skrypt do obsługi kalkulatora stosowego na wielomianach wielu zmiennych.
# Skrypt uruchamia kalkulator dla kolejnych plików o określonej strukturze.

# Wywolanie: 
# ./chain_poly.sh <prog> <dir>
# gdzie <prog> to nazwa programu kalkulatora a <dir> to katalog z danymi.

# Sprawdź poprawność parametrów wywołania: liczba, katalog, plik.
if (($#!=2)); then 
	printf "Error: # of input args is $# while it should be 2 => exit 1.\n"
	exit 1
fi

program=$1
directory=$2

if [ ! -d "$directory" ]; then
  printf "Error: directory `basename $directory` does not exist => exit 1.\n"
  exit 1
fi

if [ ! -x "$program" ]; then
  printf "Error: executable file `basename $program` does not exist => exit 1.\n"
  exit 1
fi

# Wyszukaj pierwszy plik z pierwszą linijką "START".
next_stop=0;
start_file=$(find $directory/ -type f -exec sed '/START/F;Q' {} \;)

if [ ! -f "$start_file" ] ; then
	printf "Error: file with 'START' head not found => exit 1.\n"
	exit 1
fi

last_line=$(tail -1 "$start_file")
if [[ $last_line == FILE* ]] ; then 
	next_file=${last_line#* }
elif [[ $last_line == STOP ]] ; then
		next_stop=1;
	else
		printf "Error: No match for either FILE or STOP => exit 1."
		exit 1
	fi

# Usuń pierwszą i ostanią linię z pliku startowego, wynik zapisz do pliku tymczasowego.
sed '1d;$d' "$start_file" >tmp_in

# Uruchom program zadając na wejście plik tymczasowy.
./$program <tmp_in >tmp_out 

# Wykonuj pętlę dopóki nie natrafisz na plik z linijką "STOP" na końcu.
while ! ((next_stop))
do
	head -n -1 $directory/"$next_file" >tmp_next
	cat tmp_out > tmp_in
	cat tmp_next >> tmp_in
	./$program <tmp_in >tmp_out
	last_line=$(tail -1 $directory/"$next_file") 
	if [[ $last_line == FILE* ]] ; then 
		next_file=${last_line#* }
	elif [[ $last_line == STOP* ]] ; then
		next_stop=1;
	else
		printf "Error: No match for either FILE or STOP => exit 1."
		exit 1
	fi
done

# Wypisz wynik ostatniego uruchomienia kalkulatora, posprzątaj i zwróć 0.
cat tmp_out
rm -f tmp_in
rm -f tmp_out
rm -f tmp_next
exit 0
