#include <iostream>
#include <unordered_map>

// Advent of code 2021 - Day 20 - Dirac Dice part 2

using namespace std;

struct Situation {
	 int p1, p2; // positions
	 int s1, s2; // scores
	 
	 Situation(int p1 , int p2, int s1 = 0, int s2 = 0)
		  : p1{p1}, p2{p2}, s1{s1}, s2{s2} {
	 }
	 
	 bool operator==(const Situation &) const = default; // C++20
	 
	 std::size_t hash_code() const {
		  std::size_t result = 5;
		  result = 5;
		  result = 17 * result + s1;
		  result = 17 * result + s2;		
		  result = 13 * result + p1;
		  result = 13 * result + p2;				
		  return result;
	 }
 
	 bool won_by_player() const {
		  return (s1 >= 21);
	 }
	 
	 bool won_by_opponent() const {
		  return (s2 >= 21);
	 }

	 Situation after(int dices) const {
		  int np1 = p1 + dices;
		  np1 = (np1 - 1) % 10 + 1; // new position
		  return {p2, np1, s2, s1 + np1};
	 }
	 
};

template <>
struct std::hash<Situation> {
	 std::size_t operator()(const Situation& s) const {
		  return s.hash_code();
	 }
};

struct  Result {
	 long int win1, win2;  // #univers where p1 (resp. p2) wins

	 Result(long win1, long win2) 
		  : win1{win1}, win2{win2}
		  {}

	 Result() = default;
	 
};

// --------------------------------------------

std::unordered_map<Situation,Result> memo;

Result evaluate(const Situation & situation) {
	 if (situation.won_by_player()) {
		  return {1, 0};
	 }
	 if (situation.won_by_opponent()) {
		  return {0, 1};
	 }
	 
	 if (auto it = memo.find(situation); it != memo.end()) {
		  return it->second;
	 }
	 const int freq[10] = {0, 0, 0, 1, 3, 6, 7, 6, 3, 1};

	 long win1 = 0, win2 = 0;
	 for (int dices = 3; dices < 10; dices ++) {
		  auto r = evaluate(situation.after(dices));
		  // opponent is p1 in next situation
		  win1 += freq[dices] * r.win2;
		  win2 += freq[dices] * r.win1;
	 }
	 Result result {win1, win2};
	 memo[situation] = result;
	 return result;
}

void part2(const std::string &name, int p1, int p2)
{
	 memo.clear();
	 
	 auto r = evaluate({p1, p2});
	 std::cout << "#Part 2 with " << name << " data, starting at "
			   << p1 << ", " << p2  << std::endl
			   << "\t" << r.win1 << std::endl
			   << "\t" << r.win2
			   << std::endl;
	 std::cout << "- info: " << memo.size() << " results memoized\n"
			   << std::endl;
}	

int main()
{
//	part2("test", 4, 8);
    part2("real", 7, 9);
	return EXIT_SUCCESS;
}
