/* front.c - a lexical analyzer system for simple arithmetic expressions */
#include <stdio.h>
#include <ctype.h>

/* Global declarations */
/* Variables */
int charClass;
char lexeme [100];
char nextChar;
int lexLen;
int token;
int nextToken;
int prevToken;
FILE *in_fp, *fopen();

/* Function declarations */
void addChar();
void getChar();
void getNonBlank();
int lex();

/* Character classes */
#define LETTER 0
#define DIGIT 1
#define UNKNOWN 99

/* Token codes */
#define INT_LIT 10
#define IDENT 11
#define ASSIGN_OP 20
#define ADD_OP 21
#define SUB_OP 22
#define MULT_OP 23
#define DIV_OP 24
#define LEFT_PAREN 25
#define RIGHT_PAREN 26

/* Part A token codes */
#define FOR_CODE 30
#define IF_CODE 31
#define ELSE_CODE 32
#define WHILE_CODE 33
#define DO_CODE 34
#define INT_CODE 35
#define FLOAT_CODE 36
#define SWITCH_CODE 37
#define SEMI_COLON 38
#define LESS_SYM 39
#define GREATER_SYM 40
#define EQUAL_SYM 41
#define LEFT_BRACE 42
#define RIGHT_BRACE 43
#define COMMENT 44

/******************************************************/
/* main driver */
main() {
	/* Open the input data file and process its contents */
	if ((in_fp = fopen("front.in.txt", "r")) == NULL)
		printf("ERROR - cannot open front.in \n");
	else {
		getChar();
		do {
			lex();
		} while (nextToken != EOF);
	}

	int length, i=0, state=0;
	char line[100];

	// printf("Enter the string to check for comments: ");
	// fgets(line, sizeof(line), stdin);
	// while(i) {
	// 	switch(state) {
	// 		case 0:
	// 			if(line[i] == '/') {
	// 				state = 1;
	// 				i++;
	// 			} else {
	// 				i++;
	// 			}
	// 			break;

	// 		case 1:
	// 			if(line[i] == '*') {
	// 				state = 2;
	// 				i++;
	// 			} else {
	// 				i++;
	// 			}
	// 			break;

	// 		case 2:
	// 			if(line[i] == '*') {
	// 				state = 3;
	// 				i++;
	// 			} else {
	// 				i++;
	// 			}
	// 			break;

	// 		case 3:
	// 			if(line[i] == '/') {
	// 				state = 4;
	// 				i++;
	// 			} else {
	// 				state = 2;
	// 				i++;
	// 			}
	// 			break;
	// 	}
	// }

	// if(state == 4) {
	// 	printf("\nThe entered string is recognized as a comment.\n");
	// } else {
	// 	printf("\n The endtered string is not recognized as a comment.\n");
	// }
}
/*****************************************************/
/* lookup - a function to lookup operators and parentheses and return the token */
int lookup(char ch) {
	switch (ch) {
		case '(':
			addChar();
			nextToken = LEFT_PAREN;
			break;
		
		case ')':
			addChar();
			nextToken = RIGHT_PAREN;
			break;
		
		case '+':
			addChar();
			nextToken = ADD_OP;
			break;
		
		case '-':
			addChar();
			nextToken = SUB_OP;
			break;
		
		case '*':
			addChar();
			nextToken = MULT_OP;
			break;

		case '/':
			addChar();
			nextToken = DIV_OP;

			if ((nextChar = getc(in_fp)) != EOF) {
				if(nextChar == '*') {
					nextToken = COMMENT;
				}
			}

			break;

		case ';':
			addChar();
			nextToken = SEMI_COLON;
			break;

		case '<':
			addChar();
			nextToken = LESS_SYM;
			break;

		case '>':
			addChar();
			nextToken = GREATER_SYM;
			break;

		case '=':
			addChar();
			nextToken = EQUAL_SYM;
			break;

		case '{':
			addChar();
			nextToken = LEFT_BRACE;
			break;

		case '}':
			addChar();
			nextToken = RIGHT_BRACE;
			break;
		
		default:
			addChar();
			nextToken = EOF;
			break;
	}
	prevToken = nextToken;

	return nextToken;
}

/*****************************************************/
/* addChar - a function to add nextChar to lexeme */
void addChar() {
	if (lexLen <= 98) {
		lexeme[lexLen++] = nextChar;
		lexeme[lexLen] = 0;
	} else {
		printf("Error - lexeme is too long \n");
	}
}
/*****************************************************/
/* getChar - a function to get the next character of input and determine its character class */
void getChar() {

	if ((nextChar = getc(in_fp)) != EOF) {
		if (isalpha(nextChar)) {
			charClass = LETTER;
		} else if (isdigit(nextChar)) {
			charClass = DIGIT;
		} else { 
			charClass = UNKNOWN; 
		}
	} else {
		charClass = EOF;
	}
}
/*****************************************************/
/* getNonBlank - a function to call getChar until it returns a non-whitespace character */
void getNonBlank() {
	while (isspace(nextChar)) {
		getChar();
	}
}

/*****************************************************/
/* lex - a simple lexical analyzer for arithmetic expressions */
int lex() {
	lexLen = 0;
	getNonBlank();
	switch (charClass) {

		/* Parse identifiers */
		case LETTER:
			addChar();
			getChar();
			while (charClass == LETTER || charClass == DIGIT) {
				addChar();
				getChar();
			}
			
			nextToken = IDENT;
			break;
		
		/* Parse integer literals */
		case DIGIT:
			addChar();
			getChar();
			while (charClass == DIGIT) {
				addChar();
				getChar();
			}
			nextToken = INT_LIT;
			break;
		
		/* Parentheses and operators */
		case UNKNOWN:
			lookup(nextChar);
			getChar();
			break;
		
		/* EOF */
		case EOF:
			nextToken = EOF;
			lexeme[0] = 'E';
			lexeme[1] = 'O';
			lexeme[2] = 'F';
			lexeme[3] = 0;
			break;
	
	} /* End of switch */
	
	prevToken = nextToken;

	if(nextToken == IDENT) {
		if(strcmp(lexeme, "for") == 0) {
			nextToken = FOR_CODE;
		} else if(strcmp(lexeme, "if") == 0) {
			nextToken = IF_CODE;
		} else if(strcmp(lexeme, "else") == 0) {
			nextToken = ELSE_CODE;
		} else if(strcmp(lexeme, "while") == 0) {
			nextToken = WHILE_CODE;
		} else if(strcmp(lexeme, "do") == 0) {
			nextToken = DO_CODE;
		} else if(strcmp(lexeme, "int") == 0) {
			nextToken = INT_CODE;
		} else if(strcmp(lexeme, "float") == 0) {
			nextToken = FLOAT_CODE;
		} else if(strcmp(lexeme, "switch") == 0) {
			nextToken = SWITCH_CODE;
		}
	}

	printf("Next token is: %d, Next lexeme is %s\n", nextToken, lexeme);
	return nextToken;
} /* End of function lex */
