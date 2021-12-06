#!/bin/bash

let x=0
let y=0
while read command units; do
	  case $command in
		  forward) let x+=units ;;
		  down)    let y+=units ;;
		  up)      let y-=units ;;
	  esac
done
echo "x=$x, y=$y, product=$((x*y))"

