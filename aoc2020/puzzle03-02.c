﻿#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main()
{
	FILE* file;
	if (fopen_s(&file, "c:\\tmp\\input03-02.dat", "r")) {
		printf("Couldn\'t open file");
		return 1;
	}
	char line[128];
	char line2[128];
	long trees1 = 0;
	long trees3 = 0;
	long trees5 = 0;
	long trees7 = 0;
	long trees2d = 0;
	const int width = 31;
	int line_no = 0;
	while (!feof(file)) {
		if(!fgets(line, 126, file)) {
			break;
		}
		if ((line_no % 2) == 0 && line[line_no / 2 % width] == '#') ++trees2d;
		if (line[line_no % width] == '#') ++trees1;
		if (line[line_no * 3 % width] == '#') ++trees3;
		if (line[line_no * 5 % width] == '#') ++trees5;
		if (line[line_no * 7 % width] == '#') ++trees7;
		++line_no;
	}
	printf("Trees 1x1: %ld\n", trees1);
	printf("Trees 1x3: %ld\n", trees3);
	printf("Trees 1x5: %ld\n", trees5);
	printf("Trees 1x7: %ld\n", trees7);
	printf("Trees 2x1: %ld\n", trees2d);
	printf("Trees: %lld\n", ((long long) trees2d) * trees1 * trees3 * trees5 * trees7 );
	fclose(file);
}
