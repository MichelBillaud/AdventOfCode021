/*
	Advent of Code 2021 - Day 18 - Snailfish
	1 day late.
	Michel Billaud - 19 dec 2021
*/

#include <iostream>

#include <fstream>
#include <sstream>
#include <string>

#include <vector>
#include <optional>

enum ItemType {	LEFT, COMMA, RIGHT, NUMBER };

class Item
{
    ItemType m_type;
    int m_value;
public:
    Item(ItemType type, int value = 0)
        : m_type {type}
        , m_value{value}
    {}
    std::string to_string() const
    {
        switch (m_type) {
        case LEFT :
            return "[";
        case COMMA :
            return ",";
        case RIGHT :
            return "]";
        case NUMBER :
            return std::to_string(m_value);
        }
        return "*BUG IN TO_STRING*";
    }

    ItemType type() const
    {
        return m_type;
    }
    int value() const
    {
        return m_value;
    }
    bool has_type(ItemType type) const
    {
        return m_type == type;
    }
    void add_digit(char c)
    {
        m_value = 10 * m_value + c - '0';
    }
};


std::vector<Item> parse_string(const std::string &s)
{
    std::vector<Item> items;

    for (auto c : s) {
        switch (c) {
        case '[' :
            items.emplace_back(LEFT);
            break;
        case ',' :
            items.emplace_back(COMMA);
            break;
        case ']' :
            items.emplace_back(RIGHT);
            break;
        default:
            if (items.empty() || ! items.back().has_type(NUMBER)) {
                items.emplace_back(NUMBER);
            }
            items.back().add_digit(c);
        }
    }
    return items;
}


class Number
{
    std::vector<Item> m_items;
public:
    Number(const std::string & s)
        : m_items {parse_string(s) }
    {}

    Number() = default;

    std::ostream &  out (std::ostream & o) const
    {
        for (auto i : m_items) {
            o << i.to_string();
        }
        return o;
    }

    Number operator+(Number other) const
    {
        Number s;
        s.add({LEFT})
        .add_range(*this, 0, m_items.size())
        .add({COMMA})
        .add_range(other, 0, other.m_items.size())
        .add({RIGHT});
        return s;
    }

private:
    std::optional<size_t> index_of_exploding_pair() const
    {
        int level = 0;
        int index = 0;
        for (auto item : m_items) {
            switch (item.type()) {
            case LEFT :
                level += 1;
                if (level == 5) return index;
                break;
            case RIGHT :
                level += -1;
                break;
            default:
                break;
            }
            index ++;
        }
        return {};
    }

    std::optional<size_t> index_of_splitting_number() const
    {
        int index = 0;
        for (auto item : m_items) {
            if (item.has_type(NUMBER) && item.value() >= 10) {
                // std::cout << "number found @ " << index << std::endl;
                return index;
            }
            index += 1;
        }
        // std::cout << "number not found" << std::endl;
        return {};
    }

    Number & add_range(const Number &other, int begin, int end)
    {
        for (int i = begin; i < end; i++) {
            m_items.push_back(other.m_items[i]);
        }
        return *this;
    }

    Number& add(const Item & item)
    {
        m_items.push_back(item);
        return *this;
    }


    std::optional<size_t> index_of_number(int start, int step) const
    {
        //	std::cout << "calling " << start << ", " << step << std:: endl;
        int len = m_items.size();
        for (int i = start; 0 <= i && i < len; i += step) {
            //	std::cout << i << " ";
            if (m_items[i].type() == NUMBER) {
                //          std::cout << "numner FOUND @ " << i << std::endl;
                return {i};
            }
        }
        // std::cout << "number not found" << std::endl;
        return {};
    }

    Number explode_at(int left_bracket) const
    {
        Number result;
        // std::cout << "Exploding pair at " << left_bracket << std::endl;
        if (auto left_number = index_of_number(left_bracket - 1, -1)) {
            int l = m_items[*left_number].value() +
                    m_items[left_bracket + 1].value();
            result.add_range(*this, 0, *left_number)
            .add({NUMBER, l})
            .add_range(*this, *left_number + 1, left_bracket);
        } else {
            result.add_range(*this, 0, left_bracket);
        }
        result.m_items.emplace_back(NUMBER, 0);
        if (auto right_number = index_of_number(left_bracket + 5, +1)) {
            int r = m_items[*right_number].value()
                    + m_items[left_bracket + 3].value();
            result.add_range(*this, left_bracket + 5, *right_number)
            .add({NUMBER, r})
            .add_range(*this, *right_number+1, m_items.size());
        } else {
            result.add_range(*this, left_bracket + 5, m_items.size());
        }
        return result;
    }

    Number split_at(int index) const
    {
        // std::cout << "Splitting at " << index << std::endl;
        int value = m_items[index].value();
        int left = value / 2;
        int right = value - left;

        Number result;
        result.add_range(*this, 0, index)
        .add({LEFT})
        .add({NUMBER, left })
        .add({COMMA})
        .add({NUMBER, right})
        .add({RIGHT})
        .add_range(*this, index + 1, m_items.size());
        return result;
    }

public:
    Number reduced() const
    {
        if (auto left_bracket = index_of_exploding_pair()) {
            return explode_at(*left_bracket).reduced();
        } else if (auto split_number = index_of_splitting_number()) {
            return split_at(*split_number).reduced();
        } else {
            return *this;
        }

    }
    int magnitude()
    {
        std::vector<int> stack;
        for (auto item : m_items) {
            switch (item.type()) {
            case NUMBER :
                stack.push_back(item.value());
                break;
            case RIGHT : {
                int right = stack.back();
                stack.pop_back();
                int left = stack.back();
                stack.back() = 3*left + 2*right;
            }
            break;
            default:
                break;
            }
        }
        return stack.back();
    }
};

std::ostream & operator<< (std::ostream & o, const Number & n)
{
    return n.out(o);
}

void test_string(const std::string &src, const std::string &expected)
{

    std::cout << "## Testing " << src << std::endl;
    Number n {src};
    std::cout << "      read " << n << std::endl;

    std::cout << "- expected " << expected << std::endl;
    auto  result = n.reduced();
    std::cout << "- result   " << result << std::endl;
    /*
    std::cout << ((result == expected) ? "- Perfect !" : "- ERROR") << std::endl;
    */
    std::cout << std::endl;

}


void tests()
{
    test_string("[[[[[9,8],1],2],3],4]",
                "[[[[0,9],2],3],4]");
    test_string("[7,[6,[5,[4,[3,2]]]]]",
                "[7,[6,[5,[7,0]]]]");
    test_string("[[6,[5,[4,[3,2]]]],1]",
                "[[6,[5,[7,0]]],3]");
    test_string("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]",
                "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]");
    test_string(   "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]",
                   "[[3,[2,[8,0]]],[9,[5,[7,0]]]]");
    test_string("[[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]],[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]]",
                "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]");

}

void part1(const std::string &filename)
{
    std::cout << "reading file " << filename << std::endl;

    std::ifstream file(filename, std::ifstream::in);
    std::string line;
    std::getline(file, line);
    // std::cout << "\t" << line << std::endl;
    Number sum{line};
    // std::cout << "\t" << sum << std::endl;;
    while(std::getline(file, line)) {
        Number tmp{line};
        // std::cout << "+\t" << tmp << std::endl;
        sum = sum + tmp;
        // std::cout << "=\t" << sum << std::endl;
        sum = sum.reduced();
        // std::cout << "R\t" << sum << std::endl << std::endl;
    }
    std::cout << "#Part 1 "<< filename << std::endl
              << "sum       " << sum << std::endl
              << "magnitude " << sum.magnitude() << std::endl
              << std::endl;
    file.close();
}


void check_magnitude(const std::string &string, int expected)
{
    std::cout
            << "string   : " << string << std::endl
            << "expected : " << expected << std::endl;
    Number n(string);
    std::cout
            << "magnitude: " << n.magnitude() << std::endl
            << std::endl;
}

void tests_magnitude()
{
    std::cout << "Tests for magnitude" << std::endl;

    check_magnitude("[[1,2],[[3,4],5]]", 143);
    check_magnitude("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]", 1384);
    check_magnitude("[[[[1,1],[2,2]],[3,3]],[4,4]]", 445);
    check_magnitude("[[[[3,0],[5,3]],[4,4]],[5,5]]", 791);
    check_magnitude("[[[[5,0],[7,4]],[5,5]],[6,6]]", 1137);
    check_magnitude("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]", 3488);
}

void part2(const std::string &filename)
{
    std::cout << "# Part 2, file '" << filename << "'" << std::endl;

    std::ifstream file(filename, std::ifstream::in);
    std::string line;
    std::vector<Number> numbers;
    while(std::getline(file, line)) {
        numbers.emplace_back(line);
    }
    int len = numbers.size();
    int max = 0;
    for (int i = 0; i < len; i ++) {
        for (int j = i+1; j < len; j++) {
            auto s1 = (numbers[i] + numbers[j]).reduced().magnitude();
            if (s1 > max) max = s1;
            auto s2 = (numbers[j] + numbers[i]).reduced().magnitude();
            if (s2 > max) max = s2;
        }
    }
    std::cout << "largest sum is " << max << std::endl;
    file.close();
}

int main()
{
    std::cout << "Hello, snailfish\n" << std::endl;

    //tests();
    // part1("../small.txt");
    //part1("../medium.txt");
    //part1("../large.txt");

    //tests_magnitude();

    part2("../last.txt");
    part2("../large.txt");

    return EXIT_SUCCESS;
}
