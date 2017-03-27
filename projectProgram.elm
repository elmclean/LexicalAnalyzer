include util;
include io;

startprogram Test:
	/-- variable initialization
	integer::num1 = 5;
	integer::num2 = 10;
	integer::sum = num1 + num2;

	printMessage("The sum of num1 and num2 is " + sum);
endprogram Test;