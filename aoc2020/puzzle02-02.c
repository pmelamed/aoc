// puzzle02-02.cpp : Этот файл содержит функцию "main". Здесь начинается и заканчивается выполнение программы.
//
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main()
{
	char line[200];
	FILE* input;
	if (fopen_s(&input, "c:\\tmp\\input.dat", "r")) {
		return -1;
	}
	int wrong_count = 0;
	int valid_count = 0;
	while (!feof(input)) {
		fgets(line, 198, input);
		char* token_end = line;
		int first = strtol(token_end, &token_end, 10);
		int second = strtol(token_end + 1, &token_end, 10);
		char ch = token_end[1];
		token_end += 4;
		int len = strlen(token_end);
		if (len && token_end[len - 1] == '\n') {
			--len;
		}
		if (!len) {
			continue;
		}
		int count = 0;
		if (first <= len && token_end[first - 1] == ch) {
			++count;
		}
		if (second <= len && token_end[second - 1] == ch) {
			++count;
		}
		if (count!=1) {
			++wrong_count;
			printf("WRONG: first = %d(%c), second = %d(%c), char = %c, count=%d, pwd = %s", first, token_end[first - 1], second, token_end[second - 1], ch, count, token_end);
		}
		else {
			++valid_count;
			printf("VALID: first = %d(%c), second = %d(%c), char = %c, count=%d, pwd = %s", first, token_end[first - 1], second, token_end[second - 1], ch, count, token_end);
		}
	}
	fclose(input);
	printf("\nWrong count: %d", wrong_count);
	printf("Valid count: %d", valid_count);
}
