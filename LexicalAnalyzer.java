import java.util.*;
import java.io.*;

public class LexicalAnalyzer
{
	/* Character classes */
	public static final int LETTER = 0;
	public static final int DIGIT = 1;
	public static final int SPACE = 2;
	public static final int COMMENT = 3;
	public static final int EOF = -1;
	public static final int UNKNOWN = 99;

	/* Token codes */
	public static final int INT_LIT = 10;
	public static final int IDENT = 11;
	public static final int ASSIGN_OP = 20;
	public static final int ADD_OP = 21;
	public static final int SUB_OP = 22;
	public static final int MULT_OP = 23;
	public static final int DIV_OP = 24;
	public static final int LEFT_PAREN = 25;
	public static final int RIGHT_PAREN = 26;

	/* Global declarations */
	/* Variables */
	public int charClass;
	public char[] lexeme;
	public char[] characters;
	public char nextChar;
	public char prevChar;
	public int index = 0;
	public int lexLen;
	public int token;
	public int nextToken;
	public int prevToken;
	public File in_fp;

	public void setCharacters(char[] line) {
		characters = line;
	}

	public int lookup(char ch) {

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

				if(prevToken == DIV_OP) {
					getComment();
				}
				break;
			
			case '/':
				addChar();
				nextToken = DIV_OP;
				break;
		}

		prevToken = nextToken;

		return nextToken;
	}

	/* addChar - a function to add nextChar to lexeme */
	public void addChar() {
		if (lexLen <= 98) {
			lexeme[lexLen++] = nextChar;
			lexeme[lexLen] = 0;
		}
		else {
			System.out.println("Error - lexeme is too long");
		}
	}

	/* getChar - a function to get the next character of input and determine its character class */
	public void getChar() {
		if (index < characters.length) {
			nextChar = characters[index];
			
			if (Character.isLetter(nextChar)) {
				charClass = LETTER;
			} else if (Character.isDigit(nextChar)) {
				charClass = DIGIT;
			} else {
				charClass = UNKNOWN;
			}
		} else {
			charClass = EOF;
		}

		index++;
	}

	/* getNonBlank - a function to call getChar until it returns a non-whitespace character */
	void getNonBlank() {
		while (Character.isWhitespace(nextChar)) {
			getChar();
		}
	}

	/* lex - a simple lexical analyzer for arithmetic expressions */
	public int lex() {
		lexeme = new char[100];
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

			/*  */
			case SPACE:
				getChar();
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

		System.out.println("Next token is: " + nextToken + ", Next lexeme is " + new String(lexeme));
		return nextToken;
	}

	public void getComment() {
		while (!(nextToken == DIV_OP && prevToken == MULT_OP)) {
			addChar();
			getChar();
		}
	}

	// main class -----------------------------------------------------------------------------------------
	public static void main(String[] args) {
		LexicalAnalyzer front = new LexicalAnalyzer(); 

		/* Open the input data file and process its contents */
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("front.in.txt")))) {
        	String line;
			while((line = reader.readLine()) != null) {
				char[] characters = line.toCharArray();
				
				front.setCharacters(characters);
				front.getChar();
				do {
					front.lex();
				} while(front.nextToken != EOF);
			}
        } catch (IOException e) {
            System.out.println("ERROR - cannot open front.in");
        }
	}
}