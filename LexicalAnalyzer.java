import java.util.*;
import java.io.*;

public class LexicalAnalyzer
{
	/* Character classes */
	public static final int LETTER = 0;
	public static final int DIGIT = 1;
	public static final int EOF = -1;
	public static final int UNKNOWN = 99;

	/* Token codes */
	public static final int INT_LIT = 10;
	public static final int IDENT = 11;
	public static final int COMMENT = 12;

	public static final int ASSIGN_OP = 20;
	public static final int ADD_OP = 21;
	public static final int SUB_OP = 22;
	public static final int MULT_OP = 23;
	public static final int DIV_OP = 24;
	public static final int EQUALS_OP = 25;
	public static final int NOTEQUALS_OP = 26;
	public static final int LESSEQUALS_OP = 27;
	public static final int GREATEREQUALS_OP = 28;
	public static final int AND_OP = 29;
	public static final int OR_OP = 30;
	public static final int NOT_OP = 31;
	public static final int ARROW_OP = 32;
	public static final int MOD_OP = 33;

	public static final int LEFT_PAREN = 41;
	public static final int RIGHT_PAREN = 42;
	public static final int LEFT_BRACE = 43;
	public static final int RIGHT_BRACE = 44;
	public static final int TYPE_DEFINE = 45;
	public static final int COMMA_SYM = 46;
	public static final int COLON_SYM = 47;
	public static final int AT_SYM = 48;
	public static final int RIGHT_BRACKET = 49;
	public static final int LEFT_BRACKET = 50;
	public static final int SEMI_COLON = 51;
	public static final int LESS_SYM = 52;
	public static final int GREATER_SYM = 53;
	public static final int EQUAL_SYM = 54;
	public static final int AMPERSAND_SYM = 55;
	public static final int PIPE_SYM = 56;
	public static final int DOT_SYM = 57;

	public static final int FOR_CODE = 60;
	public static final int ENDFOR_CODE = 61;
	public static final int IF_CODE = 62;
	public static final int ELSE_CODE = 63;
	public static final int ELSEIF_CODE = 64;
	public static final int ENDIF_CODE = 65;
	public static final int WHILE_CODE = 66;
	public static final int DO_CODE = 67;
	public static final int ENDWHILE_CODE = 68;
	public static final int SWITCH_CODE = 69;
	public static final int CASE_CODE = 70;
	public static final int DEFAULT_CODE = 71;
	public static final int CONTINUE_CODE = 72;
	public static final int BREAK_CODE = 73;
	public static final int END_BLOCK = 74;
	public static final int GETLINE_CODE = 75;
	public static final int PRINT_CODE = 76;
	public static final int INCLUDE_CODE = 77;
	public static final int RETURN_CODE = 78;
	public static final int NEW_CODE = 79;

	public static final int ARRAY_TYPE = 80;
	public static final int BOOLEAN_TYPE = 81;
	public static final int BYTE_TYPE = 82;
	public static final int CHARACTER_TYPE = 83;
	public static final int CLASS_TYPE = 84;
	public static final int CONST_TYPE = 85;
	public static final int DOUBLE_TYPE = 86;
	public static final int FLOAT_TYPE = 87;
	public static final int INT_TYPE = 88;
	public static final int LONG_TYPE = 89;
	public static final int SHORT_TYPE = 90;
	public static final int STATIC_TYPE = 91;

	public static final int RESTRICTED_CLASS = 95;
	public static final int GUARDED_CLASS = 96;
	public static final int OPEN_CLASS = 97;
	public static final int VACANT_CLASS = 98;

	public String[] keywords = {"array","boolean","break","byte","case","character","class",
		"constant","continue","default","do","double","else","elseif","endif","endfor","endwhile",
		"float","for","getLine","if","include","integer","long","new","print","restricted","guarded","open",
		"return","short","static","switch","while","vacant"};
	public int[] keywordsTokens = {ARRAY_TYPE, BOOLEAN_TYPE, BREAK_CODE, BYTE_TYPE, CASE_CODE,
		CHARACTER_TYPE, CLASS_TYPE, CONST_TYPE, CONTINUE_CODE, DEFAULT_CODE, DO_CODE, DOUBLE_TYPE, ELSE_CODE,
		ELSEIF_CODE, ENDIF_CODE, ENDFOR_CODE, ENDWHILE_CODE, FLOAT_TYPE, FOR_CODE, GETLINE_CODE, IF_CODE, INCLUDE_CODE,
		INT_TYPE, LONG_TYPE, NEW_CODE, PRINT_CODE, RESTRICTED_CLASS, GUARDED_CLASS, OPEN_CLASS, RETURN_CODE, SHORT_TYPE,
		STATIC_TYPE, SWITCH_CODE, WHILE_CODE, VACANT_CLASS};

	public String[] relationalOperators = {"==","<",">","!=","<=",">="};
	public int[] relationalTokens = {EQUALS_OP, LESS_SYM, GREATER_SYM, NOTEQUALS_OP, LESSEQUALS_OP,
		GREATEREQUALS_OP};

	public String[] mathimaticalOperators = {"+","-","%","*","/"};
	public int[] mathimaticalTokens = {ADD_OP, SUB_OP, MOD_OP, MULT_OP, DIV_OP};

	public String[] assignmentOperators = {"::", "="};
	public int[] assignmentTokens = {TYPE_DEFINE, ASSIGN_OP};

	public String[] comparisonOperators = {"&&","||","!"};
	public int[] comparisonTokens = {AND_OP, OR_OP, NOT_OP};

	public String[] otherSymbols = {",",";","->","@","(",")","[","]",":","&","|","."};
	public int[] otherTokens = {COMMA_SYM, SEMI_COLON, ARROW_OP, AT_SYM, RIGHT_PAREN, LEFT_PAREN, 
		RIGHT_BRACE, LEFT_BRACE, COLON_SYM, AMPERSAND_SYM, PIPE_SYM, DOT_SYM};

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

	public boolean isRelationalStart(char ch) {
		if(ch == '<' || ch == '>' || ch == '!' || ch == '=') {
			return true;
		} else {
			return false;
		}
	}

	public boolean isObjectOperatorStart(char ch) {
		if(ch == '-') {
			return true;
		} else {
			return false;
		}
	}

	public boolean isVariableTypeStart(char ch) {
		if(ch == ':') {
			return true;
 		} else {
 			return false;
 		}
	}

	public boolean isCommentStart(char ch) {
		if(ch == '/') {
			return true;
		} else {
			return false;
		}
	}

	public void buildLexeme(String[] keyArray, int[] tokenArray) {
		int key = -1;
		addChar();
		if(index < characters.length) {
			nextChar = characters[index];

			String temp = new String(lexeme);
			String text = temp.trim() + String.valueOf(nextChar);

			if(Arrays.asList(keyArray).indexOf(text) != -1) {
				key = Arrays.asList(keyArray).indexOf(text);
				addChar();
				nextToken = tokenArray[key];
				if(index < characters.length) {
					index++;
				}	
			}
		}
	}

	public void buildComment() {
		int key = -1;
		addChar();
		if(index < characters.length - 1) {
			nextChar = characters[index];

			if(nextChar == '.') {
				addChar();
				index++;
				nextChar = characters[index];
				if(nextChar == '.') {
					System.out.println("comment");
					do {
						addChar();
						index++;
						nextChar = characters[index];
					} while(index < characters.length);
				}
			}
		}
	}

	public int lookup(char ch) {
		String character = String.valueOf(ch);
		int key = -1;

		if(isRelationalStart(ch)) {
			buildLexeme(relationalOperators, relationalTokens);
		} else if(isObjectOperatorStart(ch)) {
			buildLexeme(otherSymbols, otherTokens);
		} else if(isVariableTypeStart(ch)) {
			buildLexeme(assignmentOperators, assignmentTokens);
		} else if(isCommentStart(ch)) {
			buildComment();
		} else {
			if(Arrays.asList(mathimaticalOperators).indexOf(character) != -1) {
				key = Arrays.asList(mathimaticalOperators).indexOf(character);
				addChar();
				nextToken = mathimaticalTokens[key];
			} else if(Arrays.asList(otherSymbols).indexOf(character) != -1) {
				key = Arrays.asList(otherSymbols).indexOf(character);
				addChar();
				nextToken = otherTokens[key];
			} else if(Arrays.asList(assignmentOperators).indexOf(character) != -1) {
				key = Arrays.asList(assignmentOperators).indexOf(character);
				addChar();
				nextToken = assignmentTokens[key];
			} else if(Arrays.asList(comparisonOperators).indexOf(character) != -1) {
				key = Arrays.asList(comparisonOperators).indexOf(character);
				addChar();
				nextToken = comparisonTokens[key];
			} else if(Arrays.asList(relationalOperators).indexOf(character) != -1) {
				key = Arrays.asList(relationalOperators).indexOf(character);
				addChar();
				nextToken = relationalTokens[key];
			}
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
			
			/* Parentheses and operators */
			case UNKNOWN:
				lookup(nextChar);
				getChar();
				break;

			/* EOF */
			case EOF:
				lexeme[0] = 'E';
				lexeme[1] = 'O';
				lexeme[2] = 'F';
				lexeme[3] = 0;
				nextToken = EOF;
				break;
		
		} /* End of switch */

		String value = new String(lexeme);

		if(nextToken == IDENT) {
			int key = Arrays.asList(keywords).indexOf(value.trim());  
			if(key != -1) {
				nextToken = keywordsTokens[key];
			}
		}


		if(nextToken != EOF) {
			System.out.println("Next token is: " + nextToken + "....Next lexeme is " + value.trim());
		}

		prevToken = nextToken;
		return nextToken;
	}

	// main class -----------------------------------------------------------------------------------------
	public static void main(String[] args) {

		/* Open the input data file and process its contents */
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("front.in.txt")))) {
        	String line;
			while((line = reader.readLine()) != null) {
				LexicalAnalyzer front = new LexicalAnalyzer(); 
				char[] characters = line.toCharArray();
				
				front.setCharacters(characters);
				front.getChar();
				do {
					front.lex();
				} while(front.nextToken != EOF);
			}
			System.out.println("Next token is: -1....Next lexeme is EOF");
        } catch (IOException e) {
            System.out.println("ERROR - cannot open front.in");
        }
	}
}