import * as fs from 'fs';
import * as rd from 'readline'

import { REPL_MODE_STRICT } from 'repl';

console.log("Hello")

const opening = "([{<", closing = ")]}>";
const scores: number[] = [3, 57, 1197, 25137];

function corruptionScore(s: string): number {
  
    var stack: number[] = [];
    var chars: string[] = s.split("");

    for (var c of chars) {
        // console.log(`processing "${c}`)
        var type = opening.indexOf(c);
        if (type >= 0) {
            stack.push(type);
        } else {
            // console.log(`closing`) 
            type = closing.indexOf(c);
            var topType = stack.pop()
            if (topType === undefined) {
                return 0;
            } else
                // console.log("non vide")
                if (type != topType) {
                    // console.log(`unexpected ${c} type ${type} score ${scores[type]}`)
                    return scores[type];
         
                }
        }

    }
    return 0;
}


/*
function test(s: string) {
    let score = corruptionScore(s);
    console.log(`score ${score} \tfor "${s}"`);
}

test("()")
test(")")
test("(]")
test("()>")
*/

function part1(filename: string) {
    var reader = rd.createInterface(fs.createReadStream(filename))
    var total : number = 0;
    var n = 0;
    reader.on("line", (line: string) => {
        total = total + corruptionScore(line);
        // console.log(`${n} total is now ${total}`);
        n += 1;
    });
    reader.on("close", () => {
        console.log(`Part 1 score ${total} for ${filename}`);
    })
}

part1("small.txt");
part1("large.txt");

