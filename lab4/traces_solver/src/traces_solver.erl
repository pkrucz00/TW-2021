#!/usr/bin/env escript

%%%-------------------------------------------------------------------
%%% @author Paweł Kruczkiewicz
%%% @doc
%%%
%%% @end
%%% Created : 20. lis 2021 10:44
%%%-------------------------------------------------------------------
-module(traces_solver).
-author("Paweł Kruczkiewicz").
%% API
-export([main/1]).

%% Jedynym "odpowiednikiem" mavena dla Erlanga, jaki udało mi się znaleźć, jest narzędzie Rebar, które tworzy projekty OTP, które tutaj nie miały zastosowania.
%% Z tego powodu zastosowałem tzw. escripts czyli skrypty erlangowe. Dzięki nim można uruchomić program jedną komendą w konsoli

%% Przykładowe poprawne uruchomienia programu (w folderze roboczym, w którym znajduje się ten program):
%%
%% escript traces_solver.erl A={a,b,c,d} a)x=x+y;b)y=y+2*z;c)x=3*x+z;d)z=y-z w=baadcb
%% escript traces_solver.erl A={a,b,c,d,e,f} a)x=x+1;b)y=y+2*z;c)x=3*x+z;d)w=w+v;e)z=y-z;f)v=x+v w=acdefbbe
main([AlphabetArg, TransactionsArg, WordArg]) ->
  {Alphabet, Transactions, Word} = parse_args(AlphabetArg, TransactionsArg, WordArg),

  D = compute_dependency_set(Alphabet, Transactions),
  I = compute_independence_set(Alphabet, D),

  Fnf = compute_fnf(Word, Transactions, D),

  DickGraph = compute_dickert_graph(Word, D),
  DotRepresentation = dickert_graph_to_dot(DickGraph),

  FnfFromGraph = compute_fnf_from_graph(Word, DickGraph),

  io:format("D = ~p,~nI = ~p,~nFnf = ~p,~nDickert Graph:~n~s,~nFNF from graph=~p~n",
    [D, I, Fnf, DotRepresentation, FnfFromGraph]);

main(_) ->
  io:format("Wrong number of arguments~nusage: main.erl alphabet production word"),
  halt(1).


%% parser
parse_args(AlphabetArgs, TransactionArg, WordArg) ->
  {parse_alphabet(AlphabetArgs), parse_transactions(TransactionArg), parse_word(WordArg)}.

parse_alphabet(AlphabetArg) ->
  TrimmedAlphabetString = string:trim(AlphabetArg, both, "A={}"),
  string:lexemes(TrimmedAlphabetString, ",").

%transakcje muszą być oddzielone średnikami
parse_transactions(TransactionsArg) ->
  TransactionStrings = string:lexemes(TransactionsArg, ";"),
  TransactionTuples = [parse_transaction_string(Ts) || Ts <- TransactionStrings],
  maps:from_list(TransactionTuples).

parse_transaction_string(TransactionString) ->
  SplitTransaction = lists:filter(fun(T) -> is_lower(T) end, string:lexemes(TransactionString, ")=+-*/")),
  [TransactionChar | [TransactionProduct | Rest]] = SplitTransaction,
  {TransactionChar, {TransactionProduct, Rest}}.

is_lower(Char) -> ("a" =< Char) and (Char =< "z").

parse_word(WordArg)->
string:trim(WordArg, both, "w=").


%% dependency set
%% obliczany jest jako iloczyn kartezjański alfabetu, z którego wybierane są
%% krotki posiadające cechy opisane funkcji `are_dependent`
compute_dependency_set(Alphabet, Transactions) ->
  [{X, Y} || X <- Alphabet, Y <- Alphabet,
            are_dependent(maps:get(X, Transactions, no_transaction),
                          maps:get(Y, Transactions, no_transaction))].

are_dependent({XProduct , XParameters}, {YProduct , YParameters}) ->
  XProduct == YProduct                          % transakcje modyfikują tę samą zmienną
    orelse lists:member(XProduct, YParameters)  % produkt transakcji pierwszej jest w w parametrach drugiej ...
    orelse lists:member(YProduct, XParameters); % ... lub na odwrót

are_dependent(no_transaction, _) -> {error, transaction_not_specified};
are_dependent(_, no_transaction) -> {error, transaction_not_specified}.


%% independence set
%% Zbiór niezależności to po prostu (AxA)\D
compute_independence_set(Alphabet, D) ->
  [{X, Y} || X <- Alphabet, Y <- Alphabet, not(lists:member({X,Y}, D))].


%% Foat's normal form
%% Użyto algorytmu z polecanej w treści ćwiczenia książki
%% Polega on na użyciu stosu dla każdej transakcji z alfabetu A,
%% 1. uzupełniamy go wg opisanego niżej sposobu, a następnie
%% 2. iteracyjnie wybieramy do klasy te znaki,
%%    które są na szczycie stosu i dla każdego z nich usuwamy markery ze stosów zależnych do niego elementów.
%% 3. Powtarzamy krok 2. aż nie będzie więcej liter na szczytach stosów
compute_fnf(Word, Transactions, D) ->
  InitTransactionStacks = create_stacks(Transactions),
  FilledStack = lists:foldr(fun (Ch, Stacks) -> fnf_process_char(Ch, Stacks, D) end, InitTransactionStacks, Word),
  compute_fnf_nested_list(FilledStack, D).

create_stacks(Transactions) ->
  maps:from_list([{hd(K), []} || K <- maps:keys(Transactions)]).

%dla kazdego elementu, idąc od prawej,
% wrzucamy go na własny stos oraz
% do każdego stosu ,przypisanego pozostałym, zależnym od iterowanego elementu elementom, dorzucamy marker "."
fnf_process_char(Ch, TransactionStacks, D) ->
  TransactionStackWithChar = add_to_stacks(Ch, [Ch], TransactionStacks),  % dodajemy go do własnego stosu
  AllNonCommuting = [hd(Y) || {X, Y} <- D , (X == [Ch]) and not(X == Y)], % wszystkie zależne z nim elementy
  lists:foldl(fun (NonCommutingChar, Stacks) -> add_to_stacks(NonCommutingChar, ".", Stacks) end,
    TransactionStackWithChar, AllNonCommuting). % dostają marker ".", bo nie mogą być wykonywane w tym samym czasie co przetwarzany element

add_to_stacks(Ch, NewStackHead, TransactionStacks) ->
  OldStack = maps:get(Ch, TransactionStacks),
  NewStack = [NewStackHead | OldStack],
  maps:update(Ch, NewStack, TransactionStacks).

compute_fnf_nested_list(FilledStack, D) ->
  lists:reverse(compute_fnf_nested_list(FilledStack, D, [])).

compute_fnf_nested_list(FilledStack, D, Result) ->
  LettersFromTop = get_letters_from_top_of_stacks(maps:values(FilledStack)),
  process_top_letters(LettersFromTop, FilledStack, D, Result).

process_top_letters([], _, _, Result) ->  Result;
process_top_letters(LettersFromTop, FilledStack, D, Result) ->
  UpdatedStack = update_stacks(FilledStack, LettersFromTop, D),
  compute_fnf_nested_list(UpdatedStack, D, [LettersFromTop | Result]).

update_stacks(FilledStack, LettersFromTop, D) ->
  lists:foldl(
    fun(Letter, IteratedStack) -> update_stack_by_top_letter(Letter, IteratedStack, D) end,
    FilledStack,
    LettersFromTop).

update_stack_by_top_letter(Letter, FilledStack, D) ->
  FilledStackWithLessTopMarks = delete_marks_from_productions_dependent_on_letters_from_top(Letter, FilledStack, D),
  delete_letter_from_top(FilledStackWithLessTopMarks, hd(Letter)).

delete_marks_from_productions_dependent_on_letters_from_top(TopLetter, FilledStack, D) ->
  maps:map(fun(KeyLetter, Stack) -> delete_mark_if_necessary(Stack, should_delete_mark([KeyLetter], TopLetter, D)) end, FilledStack).

should_delete_mark(Letter, TopLetter, D) ->
  lists:member({Letter, TopLetter}, D).

delete_mark_if_necessary(["." | Tail], true) -> Tail;
delete_mark_if_necessary(Stack, _ ) -> Stack.

delete_letter_from_top(Stacks, Letter) ->
  OldLetterStack = maps:get(Letter, Stacks),
  [ _ | NewLetterStack] = OldLetterStack,
  Stacks#{Letter := NewLetterStack}.

get_letters_from_top_of_stacks(Stacks) ->
  [hd(Stack) || Stack <- Stacks, not(hd(Stack)==".")].

compute_dickert_graph(Word, D) ->
  Vertices = create_vertices(Word),
  UntrimmedGraph = create_untrimmed_graph(Vertices, D),
  trim_graph(UntrimmedGraph).

create_vertices(Word) ->
  LetterizedWord = [[Letter] || Letter <- Word],   %% making list of strings makes printing a little easier
  lists:zip(LetterizedWord, lists:seq(1, length(Word))).

create_untrimmed_graph(Vertices, D) ->
  lists:foldr(fun (Vertex, CurrentGraph) -> add_vertex_to_graph(CurrentGraph, Vertex, D) end, #{}, Vertices).

add_vertex_to_graph(CurrentGraph, Vertex, D) ->
  EdgesOfVertex = compute_edges_of_vertex(maps:keys(CurrentGraph), Vertex, D),
  CurrentGraph#{Vertex => EdgesOfVertex}.

compute_edges_of_vertex(NeighbouringVertices, {VerLabel, _}, D) ->
  [ {NeighVerLabel, NeighVerNum} || {NeighVerLabel, NeighVerNum} <- NeighbouringVertices,
                                    lists:member({VerLabel, NeighVerLabel}, D)].

% Ucinanie drzewka jest prostym algorytmem:
% 1. Tworzymy listę wierzchołków osiągalnych z danego wierzchołka dla każdego wierzchołka (używamy do tego algorytmu DFS)
% 2. Tworzymy nowy graf G', w którym pozostawiamy jedynie wierzchołki
%    (U,V'): dla każdego U w G, dla każdego V' w Adj(U) nieprawdą jest, że istnieje V zawarte w Adj(U) t. że Reach(V, V')
% Słowem: Zostawiamy jedynie te krawędzie, do których nie mozna dostać się w inny sposób
trim_graph(UntrimmedGraph) ->
  ReachabilityGraph = maps:map(fun (K, _) -> get_reachable_vertices(K, UntrimmedGraph) end, UntrimmedGraph),
  maps:map(fun (_, AdjVerts) -> remove_redundant_edges(AdjVerts, ReachabilityGraph) end, UntrimmedGraph).

get_reachable_vertices(Vertex, UntrimmedGraph) ->
  dfs(maps:get(Vertex, UntrimmedGraph), UntrimmedGraph, [], []).

dfs([], _, _, Result) -> Result;
dfs([CurrVertex | Tail], UntrimmedGraph, Visited , Result) ->
  NeighbouringVertices = maps:get(CurrVertex, UntrimmedGraph),
  NotVisitedNeighVerts = lists:filter(
    fun (NeighVertex) -> not(lists:member(NeighVertex, Visited)) and not(lists:member(NeighVertex, Tail)) end,
    NeighbouringVertices),
  NewStack = lists:merge(Tail, NotVisitedNeighVerts),
  dfs(NewStack, UntrimmedGraph, [CurrVertex | Visited],  [CurrVertex | Result]).

remove_redundant_edges(AdjVerts, ReachabilityGraph) ->
  lists:filter(fun (VPrime) -> not(is_reachable(VPrime, AdjVerts, ReachabilityGraph)) end, AdjVerts).

is_reachable(VPrime, Vs, ReachabilityGraph) ->
  lists:any(fun (V) -> lists:member(VPrime, maps:get(V, ReachabilityGraph)) end, Vs).


%% Dickert Graph to graphviz notation
dickert_graph_to_dot(DickGraph) ->
  Prefix = "digraph g {\n",
  LabelPart = get_labels(maps:keys(DickGraph)),
  ConnectionsPart = get_arcs(DickGraph),
  Suffix = "}\n",
  Prefix ++ ConnectionsPart ++ LabelPart ++ Suffix.

get_labels(Vertices) ->
  lists:append(
    lists:map(
      fun({VerLabel, VerInd}) -> integer_to_list(VerInd) ++ "[label=" ++ VerLabel ++ "]\n" end,
      Vertices)).

get_arcs(DickGraph) ->
  EdgeTuples = get_indexed_edges_graph(DickGraph),
  edge_tuples_to_string(EdgeTuples).

get_indexed_edges_graph(DickGraph) ->
  GraphAsList = maps:to_list(DickGraph),
  lists:flatmap(
    fun ({{_, UInd}, AdjVertices}) -> get_edges(UInd, AdjVertices) end,
    GraphAsList).

get_edges(UInd, AdjVertices) ->
  lists:map(fun({_, VInd}) -> {UInd, VInd} end, AdjVertices).

edge_tuples_to_string(EdgeTuples) ->
  lists:append(
    lists:map(fun ({U, V}) -> integer_to_list(U) ++ " -> " ++ integer_to_list(V) ++ ";\n" end,
      EdgeTuples
  )).

%% Zamiana grafu na FNF
% Do zamiany grafu na FNF posłużono się następującym algorytmem:
% Przechodzimy po kolejnych znakach naszego słowa wejściowego w.
% Pierwszy znak trafia do pierwszej klasy równoważności
% Każdy kolejny znak trafia do nowej klasy równoważności,
%   jeżeli w grafie znajduje się łuk z któregokolwiek wierzchołka z obecnie przetwarzanej klasy równoważności
% W przeciwnym wypadku wierzchołek trafia do przetwarzanej klasy równoważności
compute_fnf_from_graph(Word, DickGraph) ->
  [FirstVertex | Tail] = create_vertices(Word),
  EqClassVertices = compute_fnf_from_graph(DickGraph, Tail, [[FirstVertex]]),
  lists:reverse(lists:map(fun (T) -> get_only_labels(T) end, EqClassVertices)).


compute_fnf_from_graph(_, [], Result) -> Result;
compute_fnf_from_graph(DickGraph, [CurrVertex | TailVertices], [LatestEqClass | TailClasses] = Result) ->
  case is_in_the_same_equivalence_class(CurrVertex, LatestEqClass, DickGraph) of
      true -> compute_fnf_from_graph(DickGraph, TailVertices, [[CurrVertex] | Result]);
      false -> compute_fnf_from_graph(DickGraph, TailVertices, [[CurrVertex | LatestEqClass] | TailClasses])
  end.

is_in_the_same_equivalence_class(CurrVertex, LatestEqClass, DickGraph) ->
  lists:any(fun(ClassVert) -> is_arc_in_graph(ClassVert, CurrVertex, DickGraph) end, LatestEqClass).

is_arc_in_graph(U, V, Graph) ->
  lists:member(V, maps:get(U, Graph)).

get_only_labels(VerticesList) ->
lists:map(fun ({VerLabel, _}) -> VerLabel end, VerticesList).