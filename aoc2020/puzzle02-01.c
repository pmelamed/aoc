// puzzle02-01.cpp : Этот файл содержит функцию "main". Здесь начинается и заканчивается выполнение программы.
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
		char *token_end = line;
		int min_limit = strtol(token_end, &token_end, 10);
		int max_limit = strtol(token_end + 1, &token_end, 10);
		char ch = token_end[1];
		token_end += 4;
		char* rest = token_end;
		int count = 0;
		while (*token_end) {
			if (*token_end == ch) {
				++count;
			}
			++token_end;
		}
		if (count<min_limit || count>max_limit) {
			++wrong_count;
			printf("WRONG: min = %d, max = %d, char = %c, count=%d, pwd = %s", min_limit, max_limit, ch, count, rest);
		} else {
			++valid_count;
			printf("VALID: min = %d, max = %d, char = %c, count=%d, pwd = %s", min_limit, max_limit, ch, count, rest);
		}
	}
	fclose(input);
	printf("\nWrong count: %d", wrong_count);
	printf("Valid count: %d", valid_count);
}
