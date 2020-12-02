var
  numbers: array [0..2020] of Boolean;
  i, number: integer;
  infile: text;

begin
  assign( infile, 'c:\tmp\input01.dat' );
  reset( infile );
  for i := 0 to 2020 do numbers[i] := False;
  readln( infile, number );
  numbers[number] := True;
  while not eof( infile ) do begin
    readln( infile, number );
    if numbers[2020 - number] then begin
      writeln( 2020 - number, ' * ', number, ' = ', number * (2020 - number) );
      readln;
      exit
    end;
    numbers[number] := True
  end;
  close( infile );
  writeln( 'No solution!' )
end.
