var
  numbers: array [1..200] of LongInt;
  products: array [0..4040] of LongInt;
  number: LongInt;
  numbersCount, i: integer;
  infile: text;

begin
  for i := 0 to 2020 do products[i] := 0;
  assign( infile, 'c:\tmp\input01.dat' );
  reset( infile );
  numbersCount := 2;
  readln( infile, numbers[1] );
  readln( infile, numbers[2] );
  products[numbers[1] + numbers[2]] := numbers[1] * numbers[2];
  while not eof( infile ) do begin
    readln( infile, number );
    if products[2020 - number] <> 0 then begin
      writeln( 'Result: ', products[2020 - number] * number );
      readln;
      exit
    end;
    for i := 1 to numbersCount
        do products[number + numbers[i]] := number * numbers[i];
    inc( numbersCount );
    numbers[numbersCount] := number
  end;
  close( infile );
  writeln( 'No solution!' )
end.
