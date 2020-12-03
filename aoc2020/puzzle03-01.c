#include <stdio.h>

int main()
{
	FILE* file;
	if (fopen_s(&file, "c:\\tmp\\input03-01.dat", "r")) {
		printf("Couldn\'t open file");
		return 1;
	}
	char line[64];
	int xcoord = 0;
	int trees = 0;
	while (!feof(file)) {
		fgets(line, 62, file);
		if (line[xcoord] == '#') ++trees;
		xcoord = (xcoord + 3) % 31;
	}
	printf("Trees: %d\n", trees);
	fclose(file);
}
