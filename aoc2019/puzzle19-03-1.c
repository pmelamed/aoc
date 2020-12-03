#include <stdio.h>
#include <stdlib.h>

typedef struct _fragment {
	int start;
	int end;
	int flat;
} fragment;

typedef struct _position {
	int x;
	int y;
} position;

int is_vertical(char def) {
	return (def == 'U' || def == 'D');
}

char* get_fragment(position* p, char* def, fragment* f) {
	char* end;
	int len = strtol(def + 1, &end, 10);
	switch (*def) {
	case 'R':
		f->start = p->x;
		f->end = p->x + len;
		f->flat = p->y;
		p->x += len;
		break;
	case 'L':
		f->start = p->x - len;
		f->end = p->x;
		f->flat = p->y;
		p->x -= len;
		break;
	case 'U':
		f->start = p->y;
		f->end = p->y + len;
		f->flat = p->x;
		p->y += len;
		break;
	case 'D':
		f->start = p->y - len;
		f->end = p->y;
		f->flat = p->x;
		p->y -= len;
		break;
	}
	return end;
}

int main()
{
	char line[2048];
	char* def_ptr;
	fragment vertical[1024], * last_vertical = vertical;
	fragment horizontal[1024], * last_horizontal = horizontal;
	position pos;
	FILE* file;
	if (fopen_s(&file, "c:\\tmp\\input19-03.txt", "r")) {
		return 1;
	}

	// Read first wire
	fgets(line, 2046, file);
	def_ptr = line;
	pos.x = 0;
	pos.y = 0;
	do {
		if (*def_ptr == ',') {
			++def_ptr;
		}
		if (is_vertical(*def_ptr)) {
			def_ptr = get_fragment(&pos, def_ptr, last_vertical++);
		}
		else {
			def_ptr = get_fragment(&pos, def_ptr, last_horizontal++);
		}
	} while (*def_ptr == ',');

	// Find crosses
	fgets(line, 2046, file);
	def_ptr = line;
	pos.x = 0;
	pos.y = 0;
	fragment frag;
	fragment* sample_start, * sample_end;
	char direction;
	int distance, min_distance = -1;
	do {
		if (*def_ptr == ',') {
			++def_ptr;
		}
		direction = *def_ptr;
		def_ptr = get_fragment(&pos, def_ptr, &frag);
		if (is_vertical(direction)) {
			sample_start = horizontal;
			sample_end = last_horizontal;
		}
		else {
			sample_start = vertical;
			sample_end = last_vertical;
		}
		for (fragment* sample = sample_start; sample != sample_end; ++sample) {
			if (frag.flat >= sample->start && frag.flat <= sample->end && sample->flat >= frag.start && sample->flat <= frag.end) {
				distance = abs(frag.flat) + abs(sample->flat);
				if (distance != 0 && (min_distance < 0 || distance < min_distance)) {
					min_distance = distance;
				}
			}
		}
	} while (*def_ptr == ',');
	printf("Min.distance: %d\n", min_distance);
	fclose(file);
}
