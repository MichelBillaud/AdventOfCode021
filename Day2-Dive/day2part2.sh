#!/bin/bash

# day 2 part 2

let x=0
let y=0
let aim=0
while read command units; do
	  case $command in
		  down)    let aim+=units ;;
		  up)      let aim-=units ;;
		  forward) let x+=units ; let y+=units*aim ;;
	  esac
	  printf "after %10s %d \t x=%d\ty=%d\taim=%d\n" $command $units $x $y $aim 
done
echo "x=$x, y=$y, product=$((x*y))"

