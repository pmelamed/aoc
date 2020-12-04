#include <stdio.h>
#include <string.h>
#include <stdlib.h>

const char* field_codes[] = {
	"byr:",
	"iyr:",
	"eyr:",
	"hgt:",
	"hcl:",
	"ecl:",
	"pid:",
	"cid:"
};

int is_valid_year(char* attr, char* attr_end, int min_val, int max_val) {
	char* end;
	int val = strtol(attr, &end, 10);
	return end == attr_end && end - attr == 4 && min_val <= val && val <= max_val;
}

int is_byr_valid(char* attr, char* attr_end) {
	return is_valid_year(attr, attr_end, 1920, 2002);
}

int is_iyr_valid(char* attr, char* attr_end) {
	return is_valid_year(attr, attr_end, 2010, 2020);
}

int is_eyr_valid(char* attr, char* attr_end) {
	return is_valid_year(attr, attr_end, 2020, 2030);
}

int is_hgt_valid(char* attr, char* attr_end) {
	char* end;
	int val = strtol(attr, &end, 10);
	if (attr_end - end != 2) return 0;
	if (end[0] == 'c' && end[1] == 'm') {
		return 150 <= val && val <= 193;
	}
	if (end[0] == 'i' && end[1] == 'n') {
		return 59 <= val && val <= 76;
	}
	return 0;
}

int is_hcl_valid(char* attr, char* attr_end) {
	if (attr_end - attr != 7) return 0;
	if (*attr != '#') return 0;
	while (++attr != attr_end) {
		if (!('0' <= *attr && *attr <= '9' || 'a' <= *attr && *attr <= 'f'))
			return 0;
	}
	return 1;
}

int is_ecl_valid(char* attr, char* attr_end) {
	const char* valid_colors[] = { "amb", "blu", "brn", "gry", "grn", "hzl", "oth" };
	const char** valid_colors_end = valid_colors + sizeof(valid_colors) / sizeof(valid_colors[0]);
	if (attr_end - attr != 3) return 0;
	for (char** color = valid_colors; color != valid_colors_end; ++color)
		if (!strncmp(attr, *color, 3)) return 1;
	return 0;
}

int is_pid_valid(char* attr, char* attr_end) {
	if (attr_end - attr != 9) return 0;
	while (attr != attr_end) {
		if ('0' > *attr || *attr > '9') return 0;
		++attr;
	}
	return 1;
}

int is_cid_valid(char* attr, char* attr_end) {
	return 1;
}

typedef int (*validator)(char* attr_start, char* attr_end);
const validator validators[] = {
	is_byr_valid,
	is_iyr_valid,
	is_eyr_valid,
	is_hgt_valid,
	is_hcl_valid,
	is_ecl_valid,
	is_pid_valid,
	is_cid_valid
};

const char** field_codes_end = field_codes + sizeof(field_codes) / sizeof(field_codes[0]);

int is_line_end(char ptr) {
	return ptr == '\n' || ptr == 0;
}

void scan_line(char* line, int* fields, int* valid_fields) {
	while (!is_line_end(*line)) {
		int mask = 1;
		validator* valid_fn = validators;
		for (char** field = field_codes; field != field_codes_end; ++field, mask <<= 1, ++valid_fn) {
			if (!strncmp(line, *field, 4)) {
				*fields |= mask;
				line += 4;
				char* attr = line;
				while (*line != ' ' && !is_line_end(*line)) ++line;
				if ((*valid_fn)(attr, line)) {
					*valid_fields |= mask;
				}
				while (*line == ' ') ++line;
				break;
			}
		}
	}
}

int main()
{
	FILE* file;
	char line[128];
	int valid_set = 0;
	int valid_all = 0;
	if (fopen_s(&file, "c:\\tmp\\input04.dat", "r")) {
		return 1;
	}
	while (!feof(file)) {
		int next = 0;
		int fields = 0;
		int valid_fields = 0;
		do {
			if (!fgets(line, 126, file)) break;
			next = is_line_end(*line);
			if (!next) scan_line(line, &fields, &valid_fields);
		} while (!next);
		if ((fields & 0x7F) == 0x7F) ++valid_set;
		if ((valid_fields & 0x7F) == 0x7F) ++valid_all;
	}
	printf("Valid passports: set=%d valid=%d\n", valid_set, valid_all);
}
