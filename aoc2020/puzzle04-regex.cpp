#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <regex>
#include <map>
#include <functional>

using namespace std;

typedef bool (*validator_fn)(const string& value);
map<string, validator_fn> attribute_validators;
map<string, uint8_t> attribute_masks;

bool is_in_range(const string& value, int minval, int maxval) {
	int val = stoi(value);
	return minval <= val && val <= maxval;
}

bool is_height_valid(const string& value) {
	smatch match;
	if (regex_search(value, match, regex("^([0-9]+)(in|cm)$", regex::ECMAScript | regex::icase))) {
		if (match[2] == "cm")
			return is_in_range(match[1], 150, 193);
		else
			return is_in_range(match[1], 59, 76);
	}
	return false;
}

bool is_matching(const string& val, const string& regexp) {
	return regex_search(val, regex(regexp, regex::ECMAScript | regex::icase));
}

void process_attribute(const string& attr, uint8_t& presence, uint8_t& validity) {
	regex re("^([a-z]{3}):(.+)$", regex::ECMAScript | regex::icase);
	smatch match;
	regex_search(attr, match, re);
	string attr_name = match[1];
	auto mask_iterator = attribute_masks.find(attr_name);
	if (mask_iterator != attribute_masks.end()) {
		presence |= mask_iterator->second;
		validator_fn validator = attribute_validators.at(attr_name);
		if ((*validator)(match[2])) {
			validity |= mask_iterator->second;
		}
	}
}

void process_passport(const string& passport_line, uint8_t& presence, uint8_t& validity) {
	istringstream iss(passport_line);
	string attribute;
	while (getline(iss, attribute, ' ')) {
		process_attribute(attribute, presence, validity);
	}
}

int main()
{
	ifstream file("c:\\tmp\\input04.dat");
	if (!file.is_open()) return 1;
	attribute_masks["byr"] = 0x01; attribute_validators["byr"] = [](const string& v) {return is_in_range(v, 1920, 2002); };
	attribute_masks["iyr"] = 0x02; attribute_validators["iyr"] = [](const string& v) {return is_in_range(v, 2010, 2020); };
	attribute_masks["eyr"] = 0x04; attribute_validators["eyr"] = [](const string& v) {return is_in_range(v, 2020, 2030); };
	attribute_masks["hgt"] = 0x08; attribute_validators["hgt"] = is_height_valid;
	attribute_masks["hcl"] = 0x10; attribute_validators["hcl"] = [](const string& v) {return is_matching(v, "^#[0-9a-f]{6}$"); };
	attribute_masks["ecl"] = 0x20; attribute_validators["ecl"] = [](const string& v) {return is_matching(v, "^(amb|blu|brn|gry|grn|hzl|oth)$"); };
	attribute_masks["pid"] = 0x40; attribute_validators["pid"] = [](const string& v) {return is_matching(v, "^[0-9]{9}$"); };
	string line;
	string passport_line;
	uint8_t presence, validity;
	int present_attributes = 0, 
		valid_attributes = 0;
	while (!file.eof()) {
		passport_line.clear();
		while (getline(file, line) && !line.empty()) {
			passport_line += " ";
			passport_line += line;
		}
		presence = 0;
		validity = 0;
		process_passport(passport_line, presence, validity);
		if (presence == 0x7F) ++present_attributes;
		if (validity == 0x7F) ++valid_attributes;
	}
	cout << "Present: " << present_attributes << " Valid attributes: " << valid_attributes << endl;
}
