#include <iostream>

#include <fstream>
#include <cassert>

#include <array>
#include <vector>

// Advent Of Code 2021 - Day 20 - Trench Map
// Michel Billaud - 20 dec 2021

// ------------------------------------------------------------------

class Algo
{
    std::array<bool, 512> m_bits;
public:
    Algo(const std::array<bool, 512>  &bits)
        : m_bits {bits}
    {
    }
    bool at(int index) const
    {
        return m_bits[index];
    }
    void dump() const
    {
        for (int i = 0; i < 512; i++) {
            std::cout << m_bits[i] << " ";
            if (i % 10 == 9) std::cout << std::endl;
        }
        std::cout << std::endl;
    }
};

// ---------------------------------------------------------------

class Image
{
    std::vector<std::vector<bool>> m_lit;
    int m_rows, m_cols;
    int m_r_begin, m_r_end, m_c_begin, m_c_end;
    bool m_background;

public:
    // Image() = default;
    Image(const std::vector<std::vector<bool>> &bits, bool background)
        : m_lit{bits},
          m_background{background}
    {
        m_rows = bits.size();
        m_cols =  bits[0].size();
    }

    int rows() const
    {
        return m_rows;
    }
    
    int cols() const
    {
        return m_cols;
    }
    
    void display(const std::string &message) const
    {
        std::cout << message << std::endl;
        for (auto &row : m_lit) {
            for (auto bit : row) std::cout << (bit ? "#" : ".");
            std::cout << std::endl;
        }
        std::cout << std::endl;
    }

    bool inside_image(int r, int c) const
    {
        return (0 <= r ) && (r < m_rows)
               && (0 <= c) && (c < m_cols );
    }

    bool bit(int r, int c) const
    {
        return inside_image(r, c)
               ? m_lit[r][c]
               : m_background;
    }

    int bit_index(int r, int c) const
    {
        int index = 0;
        for (int vr = r-1 ; vr <= r+1; vr ++) {
            for (int vc = c-1; vc <= c+1; vc++) {
                index = 2*index + (bit(vr, vc) ? 1 : 0);
            }
        }
        return index;
    }

    Image enhanced(const Algo & algo) const
    {
        const int PADDING = 2;
        std::vector<std::vector<bool>> bits;
        for (int r = -PADDING; r < m_rows+PADDING; r++) {
            std::vector<bool> row;
            for (int c = -PADDING; c < m_cols + PADDING; c++) {
                int index = bit_index(r, c);
                row.push_back(algo.at(index));
            }
            bits.push_back(row);
        }
        return Image{bits, algo.at(m_background ? 511 : 0)};
    }

    int nb_pixels_lit () const
    {
        int count = 0;
        for (int r = 0; r < m_rows; r++) {
            for (int c = 0; c < m_cols; c++) {
                if (m_lit[r][c]) count += 1;
            }
        }
        return count;
    }
};

// ------------------------------------------------------

class Puzzle
{
    Algo m_algo;
    Image m_image;

public:

    Puzzle (const Algo & algo, const Image &image)
        : m_algo{algo}
        , m_image(image)
    {}

    const Image & image() const
    {
        return m_image;
    }

    const Algo  & algo() const
    {
        return m_algo ;
    }

    void display_enhanced() const
    {
        m_image.enhanced(m_algo).display("Enhanced");
    }

};

class PuzzleReader
{
public:
    std::array<bool, 512> bits_algo;
    std::vector<std::vector<bool>> bits_image;

    PuzzleReader(const std::string &filename)
    {
        std::ifstream file(filename);
        if (file.fail()) {
            std::cerr << "FAILURE. Can't open '" << filename
                      << "' file." << std::endl;
            exit (EXIT_FAILURE);
        }

        // read algo

        std::string line;
        std::getline(file, line);

        assert(line.length() == 512);
        for (int i = 0; i < 512 ; i++) {
            bits_algo[i] = (line[i] == '#');
        }

        // read image

        std::getline(file, line); // ignore

        while(std::getline(file, line)) {
            std::vector<bool> row;
            for (char c: line) {
                row.push_back( c == '#');
            }
            bits_image.push_back(row);
        }
    }

    Puzzle build(bool background = false)
    {
        return { Algo{bits_algo}, 
        Image{bits_image, background}};
    }
};

void part1(const std::string &filename)
{
    /*
      Start with the original input image
      and apply the image enhancement algorithm twice,
      being careful to account for the infinite size of
      the images. How many pixels are lit in the resulting image?
      */
    const auto puzzle = PuzzleReader{filename}.build();
    const auto & algo = puzzle.algo();
    int nb = puzzle.image()
             .enhanced(algo)
             .enhanced(algo)
             .nb_pixels_lit();

    std::cout <<"# Part 1, file " << filename
              <<  " has " << nb << " pixels lit "
              << "after 2 enhancements."
              << std::endl;
}

void part2(const std::string &filename, int times)
{
    auto puzzle = PuzzleReader{filename} .build();
    /*
    If you enhance the starting input image in the above example
    a total of 50 times, 3351 pixels are lit in the final output image.

    Start again with the original input image and apply the image
    enhancement algorithm 50 times.
    How many pixels are lit in the resulting image?
    */
    const auto & algo = puzzle.algo();
    auto img = puzzle.image();
    for (int i = 0; i < times; i++) {
        img = img.enhanced(algo);
    }

    std::cout <<"# Part 2, file " << filename
              <<  " enhanced " << times << " times has "
              << img.nb_pixels_lit() << " pixels lit."
              << std::endl;
}

int main()
{
    std::cout << "Day20 Trench Map\n" << std::endl;
   
    // part1("../small.txt");
    // part1("../large.txt");

    part2("../small.txt", 50);
    part2("../large.txt", 50);
    return EXIT_SUCCESS;
}
