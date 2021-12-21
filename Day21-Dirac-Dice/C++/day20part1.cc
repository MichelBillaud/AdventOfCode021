#include <iostream>

// Advent of code 2021 - Day 20 - Dirac Dice part 1

using namespace std;

class Dice {
	int m_last = 0;
	int m_rolled = 0;
public:
	int roll() {
		m_rolled += 1;
		m_last += 1;
		if (m_last > 100) m_last = 1;
		// std::cout << "Dice rolls " << m_last << std::endl;
		return m_last;
	}
	int rolled() {
		return m_rolled;
	}
};

void advance(int &position, int &score, Dice &dice)
{
	for (int i = 0; i < 3; i ++) {
		position += dice.roll();
		position = ((position - 1) % 10)  + 1;
	}

	score += position;
}

void part1(const std::string &name, int p1, int p2)
{
	Dice dice;
	std::cout << "# Part 1 (" << name << ") with initial positions "
			  << p1 << ", " << p2 << std::endl;

	int s1 = 0, s2 = 0; // scores
	int winner = 0;
	while(true) {
		advance(p1, s1, dice);
		// std::cout << "player 1 moves to " << p1 << " score " << s1 << std::endl;
		if (s1 >= 1000) { winner = 1; break;}
		advance(p2, s2, dice);
		// std::cout << "player 2 moves to " << p2 << " score " << s2 << std::endl;		
		if (s2 >= 1000) { winner = 2; break;}
	}

	int rolled = dice.rolled();
	int score = rolled * ((winner == 1) ? s2: s1);
	std::cout << "\tplayer " << winner << " wins " << std::endl
			  << "\tscores " << s1 << ", " << s2 << std::endl
		      << "\tdice rolled " << rolled << " times " << std::endl
		      << "\tscore " << score << std::endl;
}	

int main()
{
	part1("test", 4, 8);
	part1("real", 7, 9);
	return EXIT_SUCCESS;
}
