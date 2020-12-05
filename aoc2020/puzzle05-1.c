#include <stdio.h>
#include <string.h>

int main()
{
	FILE* file;
	if (fopen_s(&file, "c:\\tmp\\input05.dat", "r")) {
		printf("Couldn\'t open file");
		return 1;
	}
	unsigned int max_id = 0;
	char line[12];
	int busy[1024];
	memset(busy, 0, sizeof busy);
	while (!feof(file)) {
		if (!fgets(line, 11, file)) {
			break;
		}
		unsigned int id = 0;
		char* ptr = line;
		for (int i = 0; i < 10; ++i, ++ptr) {
			id <<= 1;
			if (*ptr == 'B' || *ptr == 'R') id |= 1;
		}
		busy[id] = 1;
		if (max_id < id) max_id = id;
	}
	int* busy_ptr = busy;
	while (!*busy_ptr++);
	while (*busy_ptr++);
	printf("Max ID: %u My ID: %d\n", max_id, busy_ptr - busy - 1);
	fclose(file);
}
