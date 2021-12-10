"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    Object.defineProperty(o, k2, { enumerable: true, get: function() { return m[k]; } });
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
var fs = __importStar(require("fs"));
var rd = __importStar(require("readline"));
// import { REPL_MODE_STRICT } from 'repl';
console.log("This is Day 10");
var opening = "([{<", closing = ")]}>";
var scores = [3, 57, 1197, 25137];
function corruptionScore(s) {
    var stack = [];
    var chars = s.split("");
    for (var _i = 0, chars_1 = chars; _i < chars_1.length; _i++) {
        var c = chars_1[_i];
        // console.log(`processing "${c}`)
        var type = opening.indexOf(c);
        if (type >= 0) {
            stack.push(type);
        }
        else {
            // console.log(`closing`) 
            type = closing.indexOf(c);
            var topType = stack.pop();
            if (topType === undefined) {
                return 0;
            }
            else 
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
function part1(filename) {
    var reader = rd.createInterface(fs.createReadStream(filename));
    var total = 0;
    var n = 0;
    reader.on("line", function (line) {
        total = total + corruptionScore(line);
        // console.log(`${n} total is now ${total}`);
        n += 1;
    });
    reader.on("close", function () {
        console.log("Part 1 score " + total + " for " + filename);
    });
}
part1("../small.txt");
part1("../large.txt");
