% Szafraniuk
/**
 * Program zaliczeniowy nr 3, JiPP MIM UW 2018/19.
 * Autor: Michał Szafraniuk, Indeks: 219673.
 *
 * Program NIE implementuje poprawnych zapytań dla predykatu jestSucc z
 * nieukonkretnionymi argumentami nr 2 i 3.
 *
 */

:- use_module(library(lists)).

% -------------------- jestEFGrafem --------------------------------------------

/**
 * jestEFGrafem/1 :
 * jestEFGrafem(+Term : EF-term).
 *
 * Sukces jeśli Term jest termem poprawnie opisującym EF graf.
 */
jestEFGrafem([]).
jestEFGrafem(G) :-
    jestEFGrafem(G, []).

jestEFGrafem([], _).
jestEFGrafem([ node(V, Es, Fs) | Ns], GAcc) :-
    \+ jestWGrafie(V, GAcc),
    poprawnaListaE([node(V, Es, Fs) | Ns], GAcc),
    poprawnaListaF([node(V, Es, Fs) | Ns], GAcc),
    jestEFGrafem(Ns, [node(V, Es, Fs)|GAcc]).


/**
 * poprawnaListaE/2 :
 * poprawnaListaE(+[node(V, Es, Fs) | Ns] : EF-term, -GAcc : akumulator wierzchołków).
 *
 * Sukces jeśli lista E-krawędzi Es wierzchołka V jest poprawna,
 * tj. nie zawiera wierzchołków spoza listy reprezentacji grafu, która
 * składa się z wierzchołka V, wierzchołków na GAcc oraz wierzchołków z listy Ns.
 */
poprawnaListaE([node(_, [], _) | _], _).
% E-pętla
poprawnaListaE([node(V, [V|Es], _) | Ns], GAcc) :-
    poprawnaListaE([node(V, Es, _) | Ns], GAcc).
% E-krawędź do sprawdzanego wcześniej wierzchołka
poprawnaListaE([node(V, [E|Es], _) | Ns], GAcc) :-
    jestWGrafie(E, GAcc), % add cut?
    poprawnaListaE([node(V, Es, _) | Ns], GAcc).
% E-krawędź do jeszcze nie sprawdzanego wierzchołka
poprawnaListaE([node(V, [E|Es], _) | Ns], GAcc) :-
    jestWGrafie(E, Ns),
    poprawnaListaE([node(V, Es, _) | Ns], GAcc).


/**
 * poprawnaListaF/2 :
 * poprawnaListaF(+[node(V, Es, Fs) | Ns] : EF-term, -GAcc : akumulator wierzchołków).
 *
 * Sukces jeśli lista F-krawędzi Fs wierzchołka V jest poprawna,
 * tj. nie zawiera wierzchołków spoza listy reprezentacji grafu, która
 * składa się z wierzchołka V, wierzchołków na GAcc oraz wierzchołków z listy Ns.
 */
poprawnaListaF([node(_, _, []) | _], _).
% F-pętla
poprawnaListaF([node(V, _, [V|Fs]) | Ns], GAcc) :-
    poprawnaListaF([node(V, _, Fs) | Ns], GAcc).
% F-krawędź do sprawdzanego już wierzchołka
poprawnaListaF([node(V, _, [F|Fs]) | Ns], GAcc) :-
    jestWGrafie(F, GAcc),
    fKrawedz(F, V, GAcc),
    poprawnaListaF([node(V, _, Fs) | Ns], GAcc).
% F-krawędź do jeszcze niesprawdzanego wierzchołka
poprawnaListaF([node(V, _, [F|Fs]) | Ns], GAcc) :-
    jestWGrafie(F, Ns),
    fKrawedz(F, V, Ns),
    poprawnaListaF([node(V, _, Fs) | Ns], GAcc).

% -------------------- jestDobrzeUłożony ---------------------------------------

/**
 * jestDobrzeUlozony/1 :
 * jestDobrzeUlozony(+G : EF-graf).
 *
 * Sukces jeśli argument G reprezentuje poprawnie EF-graf, który posiada dokładnie
 * jedną parę wejścia/wyjścia (S, T), istnieje w G ścieżka z S do T pokrywająca
 * zbiór wierzchołków G oraz dla każdego wierzchołka istnieją co najwyżej trzy
 * F-krawędzie zawierające ten wierzchołek.
 */
jestDobrzeUlozony(G) :-
    jestEFGrafem(G),
    wejscieGrafu(G, S),
    wyjscieGrafu(G, T),
    eTrawersPokrywajacy(S, T, G),
    trzyF(G).


/**
 * eTrawers/4 :
 * eTrawers(+S: wierzchołek, +T: wierzchołek, +G: EF-graf, -VT: lista wierzchołków).
 *
 * Sukces jeśli VT zawiera listę wierzchołków będących E-ścieżką z S do T w G.
 */
eTrawers(S, T, G, VT) :-
    eTrawers(S, T, G, [], [S], VT).


/**
 * eTrawers/6:
 * eTrawers(+S: wierzchołek, +T: wierzchołek, +G: EF-graf,
 *   +EAcc: lista wierzchołków, +VAcc: lista wierzchołków, -VT: lista wierzchołków).
 *
 * Sukces jeśli S = T i VT jest listą wierzchołków na eTrawersie z S do T.
 *
 * Predykat pomocniczny wykorzystujący akumulatory do uzgadniania E-ścieżek z S do
 * T w G. Akumulator EAcc "przechowuje" odwiedzone krawędzie (symbol funkcyjny e/2) aby unikać
 * wielokrotnego używania danej krawędzi. VAcc akumuluje wierzchołki na ścieżce.
 */
eTrawers(S, T, G, EAcc, VAcc, VT) :-
    eKrawedz(S, X, G),
    \+ member(e(S, X), EAcc),
    eTrawers(X, T, G, [e(S, X) | EAcc], [X|VAcc], VT).
eTrawers(T, T, _, _, VAcc, VAcc).


/**
 * eTrawersPokrywajacy/3:
 * eTrawersPokrywajacy(+S: wierzchołek, +T: wierzchołek, +G: EF-graf).
 *
 * Sukces jeśli w G istnieje E-ścieżka z S do T pokrywająca cały zbiór wierzchołków G.
 */
eTrawersPokrywajacy(S, T, G) :-
    eTrawers(S, T, G, VT),
    wierzcholki(G, VG),
    subset(VG, VT),
    subset(VT, VG).


/**
 * trzyF/1:
 * trzyF(+G : EF-graf).
 *
 * Sukces jeśli dla każdego wierzchołka w G istnieją co najwyżej trzy F-krawędzie
 * zawierające ten wierzchołek. Implicite korzysta z własności poprawnej reprezentacji
 * EF-grafu G.
 */
trzyF([node(_, _, Fs) | Ns]) :-
    length(Fs, K),
    K =< 3,
    trzyF(Ns).
trzyF([]).

% -------------------- jestDobrzePermutujacy -----------------------------------

/**
 * jestDobrzePermutujacy/1 :
 * jestDobrzePermutujacy(+G: EF-graf).
 *
 * Sukces jeśli G jest dobrze permutującym EF-grafem. W szczególności sprawdza
 * warunek konieczny jakim jest "bycie dobrze ułożonym".
 */
jestDobrzePermutujacy(G) :-
    jestDobrzeUlozony(G),
    wejscieGrafu(G, S),
    wyjscieGrafu(G, T),
    jestDobrzeRozwidlajacy(G, T),
    jestDobrzeZwidlajacy(G, S).


/**
 * jestDobrzeRozwidlajacy/2 :
 * jestDobrzeRozwidlajacy(+G: EF-graf, +T: wierzchołęk).
 *
 * Sukces jeśli dla EF-grafu G z wyjściem T spełniony jest warunek nr 1 definicji
 * grafu dobrze permutującego.
 */
jestDobrzeRozwidlajacy(G, T) :-
    jestDobrzeRozwidlajacy(G, G, T).


/**
 * jestDobrzeRozwidlajacy/3 :
 * jestDobrzeRozwidlajacy(+GAcc: EF-graf, +G: EF-graf, +T: wierzchołęk).
 *
 * Sukces jeśli dla akumulatora EF-grafu GAcc (inicjowany jako G) oraz wyjścia T dla G,
 * spełniony jest warunek nr 1 definicji grafu dobrze permutującego.
 */
jestDobrzeRozwidlajacy([node(V, Es, Fs) | Ns], G, T) :-
    dobrzeRozwidla(node(V, Es, Fs), G, T),
    jestDobrzeRozwidlajacy(Ns, G, T).
jestDobrzeRozwidlajacy([], _, _).


/**
 * jestDobrzeZwidlajacy/2 :
 * jestDobrzeZwidlajacy(+G: EF-graf, +S: wierzchołęk).
 *
 * Sukces jeśli dla EF-grafu G z wejściem S spełniony jest warunek nr 2 definicji
 * grafu dobrze permutującego. Korzysta z faktu, iż własność "dobrego zwidlania"
 * jest tożsama z własnością "dobrego rozwidlania" dla grafu transponowanego.
 */
jestDobrzeZwidlajacy(G, S) :-
    grafTransponowany(G, GT),
    jestDobrzeRozwidlajacy(GT, S).


/**
 * dobrzeRozwidla/3:
 * dobrzeRozwidla(+V: wierzchołek, +G: EF-graf, +T: wierzchołek).
 *
 * Sukces jeśli wierzchołek V dobrze rozwidla się w G, którego wyjściem jest T, tj.
 * dla V spełniony jest warunek nr 1 definicji grafu dobrze permutującego.
 */
dobrzeRozwidla(node(_, Es, Fs), G, T) :-
    pary(Es, Fs, Ps),
    dobrzeLaczy(Ps, G, T).


/**
 * dobrzeLaczy/3
 * dobrzeLaczy(+Ps: lista par wierzchołków, +G: EF-graf, +T: wierzchołek).
 *
 * Sukces jeśli wszystkie pary wierzchołków z Ps dobrze się łączą
 * (patrz definicja dobrzeLaczy/2).
 */
dobrzeLaczy([para(X, Y) | Ps], G, T) :-
    Y \= T;
    dobrzeLaczy(para(X, Y), G),
    dobrzeLaczy(Ps, G, T).
dobrzeLaczy([], _, _).


/**
 * dobrzeLaczy/2:
 * dobrzeLaczy(+P: para wierzchołków, +G: EF-graf).
 *
 * Sukces jeśli para P==para(X, Y) dobrze łączy, tzn. istnieje w G wierzchołek Z,
 * taki, że (Y, Z) jest E-krawędzią a {X, Z} F-krawędzą w G.
 */
dobrzeLaczy(para(X, Y), G) :-
    fKrawedzieWierzcholka(X, G, XFs),
    eKrawedzieWierzcholka(Y, G, YEs),
    \+ intersection(XFs, YEs, []).


/**
 * fGraf/2 :
 * fGraf(+G: Ef-graf, -FG: EF-graf).
 *
 * Sukces jeśli FG jest F-grafem G, tzn. jeśli G = <V, E, F> to FG = <V, empty, F>.
 */
fGraf(G, FG) :-
    fGraf(G, [], FG).
fGraf([node(V, _, Fs) | Ns], FGAcc, FG) :-
    fGraf(Ns, [node(V, [], Fs) | FGAcc], FG).
fGraf([], FGAcc, FGAcc).


/**
 * grafTransponowany/2 :
 * grafTransponowany(+G: EF-graf, -GT: EF-graf).
 *
 * Sukces jeśli GT jest grafem transponowanym dla G, tzn. grafem G z
 * E-krawędziami skierowanymi przeciwnie.
 */
grafTransponowany(G, GT) :-
    fGraf(G, FG),
    grafTransponowany(G, FG, GT).


/**
* grafTransponowany/3 :
* grafTransponowany(+G: EF-graf, +GTAcc: EF-graf, -GT: EF-graf).
 *
 * Sukces jeśli grafy GTAcc i GT są tożsame i są grafami transponowanymi do G.
 * W założeniu GTAcc jest inicjowany jako F-graf G, który następnie akumuluje
 * E-krawędzie G z przeciwnymi zwrotami.
 */
grafTransponowany([node(V, [U|Es], Fs) | Ns], GTAcc, GT) :-
    dajKrawedzie(U, GTAcc, UEs, UFs),
    subtract(GTAcc, [node(U, _, _)], GTAccExV),
    grafTransponowany([node(V, Es, Fs) | Ns], [node(U, [V|UEs], UFs) | GTAccExV], GT).
grafTransponowany([node(_V, [], _Fs) | Ns], GTAcc, GT) :-
    grafTransponowany(Ns, GTAcc, GT).
grafTransponowany([], GTAcc, GTAcc).

% -------------------- jestSucc ------------------------------------------------
/**
 * jestSucc/3 :
 * jestSucc(+G : EF-graf, +Xs : lista wierzchołków, +Ys : lista wierzchołków).
 *
 * Sukces jeśli Xs orax Ys są poprawnymi F-ścieżkami w G oraz Ys jest
 * następnikiem Xs w G. Implementacja zakłada, iż Xs oraz Ys są termami
 * ustalonymi.
 */
jestSucc(G, Xs, Ys) :-
    jestFSciezka(Xs, G),
    jestFSciezka(Ys, G),
    jestNastepnikiem(G, Xs, Ys).


/**
 * jestFSciezka/2 :
 * jestFSciezka(+Xs : lista wierzchołków, +G : EF-graf).
 *
 * Sukces jeśli Xs jest F-ścieżką w G (przujmujemy, iż lista pusta jest
 * F-ścieżką).
 */
jestFSciezka([X, Z | Xs], G) :-
    fKrawedzieWierzcholka(X, G, XFs),
    member(Z, XFs),
    jestFSciezka([Z | Xs], G).
jestFSciezka([X], G) :-
    wierzcholki(G, VG),
    member(X, VG).
jestFSciezka([], _).


/**
 * jestNastepnikiem/3 :
 * jestNastepnikiem(+G : EF-graf, +Xs : F-ścieżka w G, +Ys : F-ścieżka w G).
 *
 * Sukces jeśli Ys jest następnikiem Xs w G.
 */
jestNastepnikiem(G, [X|Xs], [Y|Ys]) :-
    eKrawedzieWierzcholka(X, G, XEs),
    member(Y, XEs),
    jestNastepnikiem(G, Xs, Ys).
jestNastepnikiem(_, [], _).

% -------------------- predykaty pomocnicze z dziedziny problemu ---------------

/**
 * jestWejsciem/2:
 * jestWejsciem(?V: wierzchołek, +G: EF-graf).
 *
 * Sukces jeśli V jest wejściem w G.
 */
jestWejsciem(V, [node(_, Es, _) | Ns]) :-
    \+ member(V, Es),
    jestWejsciem(V, Ns).
jestWejsciem(_, []).


/**
 * wejsciaGrafu/2:
 * wejsciaGrafu(+G: EF-graf, -Res: lista wierzchołków).
 *
 * Sukces jeśli Res jest listą wierzchołków będacych "wejściami" w G.
 */
wejsciaGrafu(G, Res) :-
    wejsciaGrafu(G, G, [], Res).


/**
 * wejsciaGrafu/4:
 * wejsciaGrafu(+H: EF-graf, +G: EF-graf, +SAcc: lista wierzchołków,
 *    -Res: lista wierzchołków).
 *
 * Sukces jeśli akumulator H dla grafu G (inicjowany jako G) zostaje
 * wyczyszczony a akumulator wejść SAcc uzgadnia się z Res.
 */
wejsciaGrafu([node(V, _, _) | Ns], G, SAcc, Res) :-
    jestWejsciem(V, G),
    wejsciaGrafu(Ns, G, [V|SAcc], Res).
wejsciaGrafu([node(V, _, _) | Ns], G, SAcc, Res) :-
    \+ jestWejsciem(V, G),
    wejsciaGrafu(Ns, G, SAcc, Res).
wejsciaGrafu([], _, SAcc, SAcc).


/**
 * wejscieGrafu/2:
 * wejscieGrafu(+G: EF-graf, ?S - wierzchołek).
 *
 * Sukces jeśli S jest jedynym wejściem w G.
 */
wejscieGrafu(G, S) :-
    wejsciaGrafu(G, [S]).


/**
 * jestWyjsciem/2:
 * jestWyjsciem(?V: wierzchołek, +G: EF-graf).
 *
 * Sukces jeśli V jest wyjściem w G.
 */
jestWyjsciem(V, [node(V, [], _) | _]).
jestWyjsciem(V, [node(_, _, _) | Ns]) :-
    jestWyjsciem(V, Ns).


/**
 * wyjsciaGrafu/2:
 * wyjsciaGrafu(+G: EF-graf, -Res: lista wierzchołków).
 *
 * Sukces jeśli Res jest listą wierzchołków będacych "wyjściami" w G.
 */
wyjsciaGrafu(G, Res) :-
    wyjsciaGrafu(G, G, [], Res).


/**
 * wyjsciaGrafu/4:
 * wyjsciaGrafu(+H: EF-graf, +G: EF-graf, +TAcc: lista wierzchołków,
 *    -Res: lista wierzchołków).
 *
 * Sukces jeśli akumulator H dla grafu G (inicjowany jako G)  zostaje
 * wyczyszczony a akumulator wyjść SAcc uzgadnia się z Res.
 */
wyjsciaGrafu([node(V, _, _) | Ns], G, TAcc, Res) :-
    jestWyjsciem(V, G),
    wyjsciaGrafu(Ns, G, [V|TAcc], Res).
wyjsciaGrafu([node(V, _, _) | Ns], G, TAcc, Res) :-
    \+ jestWyjsciem(V, G),
    wyjsciaGrafu(Ns, G, TAcc, Res).
wyjsciaGrafu([], _, TAcc, TAcc).


/**
 * wyjscieGrafu/2:
 * wyjscieGrafu(+G: EF-graf, ?T - wierzchołek).
 *
 * Sukces jeśli T jest jedynym wyjściem w G.
 */
wyjscieGrafu(G, T) :-
    wyjsciaGrafu(G, [T]).


/**
 * jestWGrafie/2:
 * jestWGrafie(?V: wierzchołek, +G: EF-graf).
 *
 * Sukces jeśli wierzchołek V znajduje się na liście reprezentacji G.
 */
jestWGrafie(V, [node(V, _, _) | _]) :- !.
jestWGrafie(V, [_ | Ns]) :-
    jestWGrafie(V, Ns).


/**
 * dajKrawedzie/4:
 * dajKrawedzie(+V: wierzchołek, +G: EF-graf, -Es: lista wierzchołków,
 *    -Fs: lista wierzchołków).
 *
 * Sukces jeśli Es oraz Fs są listami E- oraz F-krawędzi w grafie G wychodzącymi
 * z wierzchołka V.
 */
dajKrawedzie(V, [node(V, Es, Fs) | _], Es, Fs) :- !.
dajKrawedzie(V, [node(U, _, _) | Ns], VEs, VFs) :-
    U \= V, % czy to potrzebne?
    dajKrawedzie(V, Ns, VEs, VFs).
dajKrawedzie(_, [], [], []).


/**
 * eKrawedz/3 :
 * eKrawedz(+U : wierzchołek, +V : wierzchołek, +G : EF-graf).
 *
 * Sukces jeśli (U, V) jest jest E-krawędzią w G.
 */
eKrawedz(U, V, [node(U, Es, _) | _]) :-
    member(V, Es).
eKrawedz(U, V, [node(W, _, _) | Ns]) :-
    U \= W,
    eKrawedz(U, V, Ns).


/**
 * fKrawedz/3 :
 * fKrawedz(+U : wierzchołek, +V : wierzchołek, +G : EF-graf).
 *
 * Sukces jeśli (U, V) jest jest F-krawędzią w G.
 */
fKrawedz(U, V, [node(U, _, Fs) | _]) :-
    member(V, Fs).
fKrawedz(U, V, [node(W, _, _) | Ns]) :-
    U \= W, % czy dołożyć U <> W? zeby nie wchodzil do tej galezi
    fKrawedz(U, V, Ns).


/**
 * eKrawedzieWierzcholka/3 :
 * eKrawedzieWierzcholka(+V : wierzchołek, +G : EF-graf,
 *    -ES : lista wierzchołków).
 *
 * Sukces jeśli ES jest listą E-krawędzi V w G.
 */
eKrawedzieWierzcholka(V, [node(V, Es, _) | _], Es) :- !.
eKrawedzieWierzcholka(V, [node(U, _, _) | Ns], VEs) :-
    U \= V, % czy to potrzebne?
    eKrawedzieWierzcholka(V, Ns, VEs).


/**
 * fKrawedzieWierzcholka/3 :
 * fKrawedzieWierzcholka(+V : wierzchołek, +G : EF-graf,
 *    -FS : lista wierzchołków).
 *
 * Sukces jeśli FS jest listą F-krawędzi V w G.
 */
fKrawedzieWierzcholka(V, [node(V, _, Fs) | _], Fs) :- !.
fKrawedzieWierzcholka(V, [node(U, _, _) | Ns], VFs) :-
    U \= V, % czy to potrzebne?
    fKrawedzieWierzcholka(V, Ns, VFs).


/**
 * wierzcholki/2:
 *
 * wierzcholki(+G: EF-graf, -VG: lista wierchołków).
 *
 * Sukces jeśli VG jest listą wierzchołków G.
 */
wierzcholki(G, VG) :-
    wierzcholki(G, [], VG).


/**
 * wierzcholki/3:
 *
 * wierzcholki(+G: EF-graf, +VAcc: lista wierzchołków, -VG: lista wierchołków).
 *
 * Sukces jeśli VG oraz VAcc uzgadniają się do listy wierzchołków G.
 */
wierzcholki([node(V, _, _) | Ns], VAcc, VG) :-
    wierzcholki(Ns, [V|VAcc], VG).
wierzcholki([], VAcc, VAcc).

% -------------------- pozostałe predykaty pomocnicze --------------------------

/**
 * pary/3:
 * pary(+Xs: lista, +Ys: lista, Ps: lista par).
 *
 * Sukces jeśli Ps jest listą wszystkich par (symbol funkcyjny para/2) elementów
 * Xs i Ys.
 */
pary(Xs, Ys, Ps) :-
    findall(para(X,Y), (member(X, Xs), member(Y, Ys)), Ps).

% podzbior(+xs, +ys) : sukces
/**
 * podzbior/2:
 * podzbior(+Xs: lista, +Ys: lista).
 *
 * Sukces jeśli zbiór wartości listy Xs jest podzbiorem zbioru wartości listy Ys.
 */
podzbior([], _).
podzbior([X|Xs], Ys) :-
    member(X, Ys),
    podzbior(Xs, Ys).
