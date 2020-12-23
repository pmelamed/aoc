#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <time.h>

typedef struct __cup {
	int label;
	struct __cup* next;
} cup;

cup* buildInitialCircle(cup* cups, char* config, int max) {
	cup* cup = NULL, * prevCup = NULL, * firstCup = NULL;
	while (*config) {
		cup = cups + (*(config++) - '0');
		if (prevCup != NULL) {
			prevCup->next = cup;
		}
		else {
			firstCup = cup;
		}
		prevCup = cup;
	}
	for (int label = 10; label <= max; ++label) {
		prevCup->next = cups + label;
		prevCup = prevCup->next;
	}
	prevCup->next = firstCup;
	return firstCup;
}

int getDestination(int current, int max, int skipped1, int skipped2, int skipped3) {
	do {
		if (current == 1) {
			current = max;
		}
		else {
			--current;
		}
	} while (current == skipped1 || current == skipped2 || current == skipped3);
	return current;
}

void makeMove(cup* current, cup* cups, int max) {
	// Pick up cups
	cup* firstPickedUp = current->next;
	cup* lastPickedUp = current->next->next->next;
	current->next = lastPickedUp->next;

	// Find destination
	cup* destination = cups + getDestination(
		current->label,
		max,
		firstPickedUp->label,
		firstPickedUp->next->label,
		lastPickedUp->label
	);
	cup* next = destination->next;
	destination->next = firstPickedUp;
	lastPickedUp->next = next;
}

void playGame(cup* cups, char* input, int max, int moves) {
	for (int label = 1; label <= max; label++) cups[label].label = label;
	cup* current = buildInitialCircle(cups, input, max);

	for (int move = 0; move < moves; ++move) {
		makeMove(current, cups, max);
		current = current->next;
	}
}

void task1(char* input, char* resultBuf) {
	cup cups[10];
	playGame(cups, input, 9, 100);
	cup* cup = cups + 1;
	for (int index = 0; index < 8; ++index) {
		cup = cup->next;
		*(resultBuf++) = cup->label + '0';
	}
	*resultBuf = 0;
}

long long task2(char* input) {
	cup* cups = malloc(1000001 * sizeof(cup));
	playGame(cups, input, 1000000, 10000000);
	cup* after1 = cups[1].next;
	long long result = ((long long)(after1->label)) * ((long long)after1->next->label);
	free(cups);
	return result;
}

void day(char* input, char* expected1, long long expected2) {
	char buffer[9];
	printf("%s Task #1 - <%s> -> ", input, expected1);
	clock_t start = clock();
	task1(input, buffer);
	clock_t end = clock();
	printf("<%s> - [%lldms]", buffer, ((long long)end - start) * 1000 / CLOCKS_PER_SEC);
	if (!strcmp(buffer, expected1)) {
		puts(" - OK!");
	}
	else {
		puts(" - Failed!");
	}
	printf("%s task #2 - %lld -> ", input, expected2);
	start = clock();
	long long result = task2(input, buffer);
	end = clock();
	printf("%lld - [%lldms]", result, ((long long)end - start) * 1000 / CLOCKS_PER_SEC);
	if (result == expected2) {
		puts(" - OK!");
	}
	else {
		puts(" - Failed!");
	}
}

int main()
{
	day("389125467", "67384529", 149245887792L);
	day("523764819", "49576328", 511780369955L);
}



