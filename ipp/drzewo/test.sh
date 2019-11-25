#!/bin/bash

# skrypt testujący dla programu solution (IPP zadanie nr 1 "Drzewo")
# wywolanie: 
# ./test.sh <prog> <dir> LUB
# ./test.sh -v <prog> <dir>

vflag=0
if [ "$1" == "-v" ]; then
    vflag=1;
    shift 1;
fi

if (($#<2)); then 
    printf "error: too few arguments";
    exit 1;
fi

program=$1
directory=$2

if [ ! -d "$directory" ]; then
    printf "error: directory `basename $directory` does not exist";
    exit 1;
fi

if [ ! -x "$program" ]; then
    printf "error: executable file `basename $program` does not exist";
    exit 1;
fi

all_count=0
fail_count_out=0
fail_count_err=0

printf "*** test results:"

for f in $directory/*.in; do 
    ((all_count++))
    printf "\n$f: "
    if ((vflag == 0)); then
        ./$program <$f >${f%in}myout;
        cmp -s ${f%in}myout ${f%in}out
        if (($? == 1)); then
            ((fail_count_out++))
        fi
        printf "\n  stdout => "
        if (($? == 0)); then
            printf "PASSED"
        else
            printf "FAILED"
        fi
        rm -f ${f%in}myout
    fi

    if ((vflag == 1)); then
        ./$program -v <$f 1>${f%in}myout 2>${f%in}myerr;
        cmp -s ${f%in}myout ${f%in}out
        if (($? == 1)); then
            ((fail_count_out++))
        fi
        printf "\n  stdout => "
        if (($? == 0)); then
            printf "PASSED"
        else
            printf "FAILED"
        fi
        cmp -s ${f%in}myerr ${f%in}err
        if (($? == 1)); then
        ((fail_count_err++))
        fi
        printf "\n  stderr => "
        if (($? == 0)); then
            printf "PASSED"
        else
            printf "FAILED"
        fi
        rm -f ${f%in}myout
        rm -f ${f%in}myerr
    fi
done

passed_count_out=$((all_count-fail_count_out))

printf "\n\n*** test results summary: "
if ((vflag == 0)); then
    printf "\n# of stdout tests passed = $passed_count_out / $all_count run in total\n"
fi
if ((vflag == 1)); then
    passed_count_err=$((all_count-fail_count_err))
    printf "\n# of stdout tests passed = $passed_count_out / $all_count run in total"
    printf "\n# of stderr tests passed = $passed_count_err / $all_count run in total\n"
fi