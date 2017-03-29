import java.util.*;
import java.io.*;

public class LexicalAnalyzer
{
	// character classes
	public static final int LETTER = 0;
	public static final int DIGIT = 1;
	public static final int EOF = -1;
	public static final int EOL = -2;
	public static final int UNKNOWN = 99;

	// token codes, not all implemented, just wanted to cover bases
	public static final int INT_LIT = 10;
	public static final int IDENT = 11;
	public static final int COMMENT = 12;
	public static final int FLOAT_LIT = 13;
	public static final int STRING_LIT = 14;
	public static final int UTIL_PACKAGE = 15;
	public static final int IO_PACKAGE = 16;
	public static final int CHAR_LIT = 17;
	public static final int START_PROGRAM = 78;
	public static final int END_PROGRAM = 79;

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
	public static final int DOUBLE_QUOTE = 58;
	public static final int SINGLE_QUOTE = 59;

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
	public static final int FLOAT_TYPE = 87;
	public static final int INT_TYPE = 88;
	public static final int SHORT_TYPE = 90;
	public static final int STATIC_TYPE = 91;
	public static final int TRUE_TYPE = 92;
	public static final int FALSE_TYPE = 93;
	public static final int STRING_TYPE = 94;

	public static final int RESTRICTED_CLASS = 95;
	public static final int GUARDED_CLASS = 96;
	public static final int OPEN_CLASS = 97;
	public static final int VACANT_CLASS = 98;

	// parallel arrays to speed up the process of looking for errors
	public String[] keywords = {"boolean","@break","byte","@case","character","class",
		"constant","@continue","@default","@do","@else","@elseif","@endif","@endfor","@endwhile",
		"float","@for","getLine","@if","include","integer","new","printMessage","restricted","guarded","open",
		"return","short","static","@switch","@while","vacant","true","false","util","io","startprogram",
		"endprogram", "string"};
	public int[] keywordsTokens = {BOOLEAN_TYPE, BREAK_CODE, BYTE_TYPE, CASE_CODE,
		CHARACTER_TYPE, CLASS_TYPE, CONST_TYPE, CONTINUE_CODE, DEFAULT_CODE, DO_CODE, ELSE_CODE,
		ELSEIF_CODE, ENDIF_CODE, ENDFOR_CODE, ENDWHILE_CODE, FLOAT_TYPE, FOR_CODE, GETLINE_CODE, IF_CODE, INCLUDE_CODE,
		INT_TYPE, NEW_CODE, PRINT_CODE, RESTRICTED_CLASS, GUARDED_CLASS, OPEN_CLASS, RETURN_CODE, SHORT_TYPE,
		STATIC_TYPE, SWITCH_CODE, WHILE_CODE, VACANT_CLASS, TRUE_TYPE, FALSE_TYPE, UTIL_PACKAGE, IO_PACKAGE, 
		START_PROGRAM, END_PROGRAM, STRING_TYPE};

	public String[] relationalOperators = {"==","<",">","!=","<=",">="};
	public int[] relationalTokens = {EQUALS_OP, LESS_SYM, GREATER_SYM, NOTEQUALS_OP, LESSEQUALS_OP,
		GREATEREQUALS_OP};

	public String[] mathimaticalOperators = {"+","-","%","*","/"};
	public int[] mathimaticalTokens = {ADD_OP, SUB_OP, MOD_OP, MULT_OP, DIV_OP};

	public String[] assignmentOperators = {"::", "="};
	public int[] assignmentTokens = {TYPE_DEFINE, ASSIGN_OP};

	public String[] comparisonOperators = {"&&","||","!"};
	public int[] comparisonTokens = {AND_OP, OR_OP, NOT_OP};

	public String[] otherSymbols = {",",";","->","@","(",")","[","]",":","&","|",".","{","}"};
	public int[] otherTokens = {COMMA_SYM, SEMI_COLON, ARROW_OP, AT_SYM, LEFT_PAREN, RIGHT_PAREN, 
		LEFT_BRACE, RIGHT_BRACE, COLON_SYM, AMPERSAND_SYM, PIPE_SYM, DOT_SYM, LEFT_BRACKET, RIGHT_BRACKET};

	// global variables
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
	public boolean error = false;

	// convert string from file into a character array
	public void setCharacters(char[] line) {
		characters = line;
	}

	// check is the character is the start to a relational operator
	public boolean isRelationalStart(char ch) {
		int tempIndex = index + 1;

		if(tempIndex < characters.length) {
			if((ch == '<' || ch == '>' || ch == '!' || ch == '=') && !Character.isWhitespace(characters[index])) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// check if character is the start of an object operator
	public boolean isObjectOperatorStart(char ch) {
		if(ch == '-') {
			return true;
		} else {
			return false;
		}
	}

	// check if character is the start of a variable type definer
	public boolean isVariableTypeStart(char ch) {
		if(ch == ':') {
			return true;
 		} else {
 			return false;
 		}
	}

	// check if character is the start of a comment
	public boolean isCommentStart(char ch) {
		if(ch == '/') {
			return true;
		} else {
			return false;
		}
	}

	// check if character is the start of a statement
	public boolean isStatementStart(char ch) {
		if(ch == '@') {
			return true;
		} else {
			return false;
		}
	}

	// check if character is the start of a logical operator
	public boolean isLogicStart(char ch) {
		if(ch == '|' || ch == '&') {
			return true;
		} else {
			return false;
		}
	}

	// build the next lexeme if it is more than one character
	public void buildLexeme(String[] keyArray, int[] tokenArray) {
		int key = -1;  // index not found
		addChar();  // add character to array
		
		if(index < characters.length) {
			nextChar = characters[index];  // get character from array
			String temp = new String(lexeme);  // convert char[] to string
			String text = temp.trim() + String.valueOf(nextChar);  // trim extra spaces

			if(Arrays.asList(keyArray).indexOf(text) != -1) {  // if index found
				key = Arrays.asList(keyArray).indexOf(text);
				addChar();
				nextToken = tokenArray[key];  // find coresponding token from array
				if(index < characters.length) {
					index++;
				}	
			} else if(nextChar != '=' && !Character.isWhitespace(nextChar)) {  // wrong syntax
				System.out.print("SYNTAX ERROR - Relational operator not recognized - ");
				error();
			}
		} else {
			String character = String.valueOf(nextChar);

			// check if character has a defined token 
			if(Arrays.asList(mathimaticalOperators).indexOf(character) != -1) {
				key = Arrays.asList(mathimaticalOperators).indexOf(character);
				nextToken = mathimaticalTokens[key];
			} else if(Arrays.asList(otherSymbols).indexOf(character) != -1) {
				key = Arrays.asList(otherSymbols).indexOf(character);
				nextToken = otherTokens[key];
			} else if(Arrays.asList(assignmentOperators).indexOf(character) != -1) {
				key = Arrays.asList(assignmentOperators).indexOf(character);
				nextToken = assignmentTokens[key];
			} else if(Arrays.asList(comparisonOperators).indexOf(character) != -1) {
				key = Arrays.asList(comparisonOperators).indexOf(character);
				nextToken = comparisonTokens[key];
			} else if(Arrays.asList(relationalOperators).indexOf(character) != -1) {
				key = Arrays.asList(relationalOperators).indexOf(character);
				nextToken = relationalTokens[key];
			}
		}
	}

	// build the lexeme for a comment
	public void buildComment() {
		int key = -1;  // index not found
		addChar();  // add character to array

		if(index < characters.length - 1) {
			nextChar = characters[index];
			if(nextChar == '-') {  // next char needed for comment
				addChar();
				index++;
				nextChar = characters[index];
				if(nextChar == '-') {  // next char needed for comment
					addChar();
					while(index < characters.length-1) {  // grab the rest of the line
						index++;
						lexeme[index] = characters[index];
					}
					index++;
					nextToken = COMMENT;
				} else {  // wrong syntax
					System.out.print("SYNTAX ERROR - Incorrect comment start - ");
					error();
				}
			}
		}
	}

	// build the lexeme for a statement
	public void buildStatement() {
		int key = -1;  // index not found
		addChar();
		nextChar = characters[index];
		
		// grab all the following characters as long as it's a letter
		while(index <= characters.length - 1 && Character.isLetter(nextChar)) {
			addChar();
			index++;
			if (index < characters.length) {
				nextChar = characters[index];
			}
		}

		String temp = new String(lexeme);

		if(Arrays.asList(keywords).indexOf(temp.trim()) != -1) {  // if found in array
			key = Arrays.asList(keywords).indexOf(temp.trim());
			nextToken = keywordsTokens[key];
		} else {
			System.out.print("SYNTAX ERROR - Incorrect statement declaration - ");
			error();
		}
	}

	// build a lexeme for a character literal
	public void buildSingleQuote() {
		addChar();
		getChar();  // get next character from input stream

		if(charClass == SINGLE_QUOTE) {  // empty char literal
			addChar();
			index++;
			nextToken = CHAR_LIT;
		} else {
			addChar();
			getChar();

			if(charClass != SINGLE_QUOTE) {  // more than one character, syntax error
				System.out.print("SYNTAX ERROR - Missing end quote - ");
				error();
			} else {
				addChar();
				index++;
				nextToken = CHAR_LIT;
			}
		}
	}

	// assign a token to an unknown symbol
	public int lookup(char ch) {
		String character = String.valueOf(ch);
		int key = -1;

		// check to see if the start of a larger lexeme
		if(isRelationalStart(ch)) {
			buildLexeme(relationalOperators, relationalTokens);
		} else if(isLogicStart(ch)) {
			buildLexeme(comparisonOperators, comparisonTokens);
		} else if(isObjectOperatorStart(ch)) {
			buildLexeme(otherSymbols, otherTokens);
		} else if(isVariableTypeStart(ch)) {
			buildLexeme(assignmentOperators, assignmentTokens);
		} else if(isCommentStart(ch)) {
			buildComment();
		} else if(isStatementStart(ch)) {
			buildStatement();
		} else {  // check arrays for character
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

	// add nextChar to lexeme
	public void addChar() {
		if (lexLen <= 98) {
			lexeme[lexLen++] = nextChar;
			lexeme[lexLen] = 0;
		}
		else {
			System.out.println("Error - lexeme is too long");
		}
	}

	// get the next character of input and determine its character class
	public void getChar() {
		if (index < characters.length) {
			nextChar = characters[index];
			
			if (Character.isLetter(nextChar)) {
				charClass = LETTER;
			} else if (Character.isDigit(nextChar)) {
				charClass = DIGIT;
			} else if (nextChar == '.') {
				charClass = DOT_SYM;
			} else if (nextChar == '\"') {
				charClass = DOUBLE_QUOTE;
			} else if (nextChar == '\'') {
				charClass = SINGLE_QUOTE;
			} else {
				charClass = UNKNOWN;
			}
		} else {
			charClass = EOL;
		}

		index++;
	}

	// call getChar until it returns a non-whitespace character
	void getNonBlank() {
		while (Character.isWhitespace(nextChar)) {
			getChar();
		}
	}

	// simple lexical analyzer for arithmetic expressions
	public int lex() {
		lexeme = new char[250];
		lexLen = 0;
		getNonBlank();

		switch (charClass) {

			// parse identifiers
			case LETTER:
				addChar();
				getChar();
				while (charClass == LETTER || charClass == DIGIT) {
					addChar();
					getChar();
				}
				nextToken = IDENT;
				break;
			
			// parse integer literals
			case DIGIT:
				boolean decimal = false;
				addChar();
				getChar();
				while (charClass == DIGIT || charClass == DOT_SYM) {
					if(decimal == false && charClass == DOT_SYM) {
						decimal = true;
						addChar();
						getChar();
					}
					if(decimal == true && charClass == DOT_SYM) {
						System.out.print("SYNTAX ERROR - More than one decimal - ");
						error();
						break;
					} else {
						addChar();
						getChar();
					}
				}

				if(decimal) {
					nextToken = FLOAT_LIT;
				} else {
					nextToken = INT_LIT;
				}
				break;

			// parse string literal
			case DOUBLE_QUOTE:
				addChar();
				getChar();
				boolean endQuote = false;
				while(index < characters.length && !endQuote) {
					if(charClass == DOUBLE_QUOTE) {
						addChar();
						endQuote = true;
						getChar();
					} else {
						addChar();
						getChar();
					}
				}
				if(!endQuote) {
					System.out.print("SYNTAX ERROR - Missing end quote - ");
					error();
				}
				nextToken = STRING_LIT;
				break;

			case SINGLE_QUOTE:
				buildSingleQuote();

			// parse parentheses, operators and other symbols
			case UNKNOWN:
				lookup(nextChar);
				getChar();
				break;

			// end of line from file
			case EOL:
				lexeme[0] = 'E';
				lexeme[1] = 'O';
				lexeme[2] = 'L';
				lexeme[3] = 0;
				nextToken = EOL;
				break;
		
		}

		String value = new String(lexeme);

		if(nextToken == IDENT) {  // check if identifier is a reserved keyword
			int key = Arrays.asList(keywords).indexOf(value.trim());
			if(key != -1) {
				nextToken = keywordsTokens[key];
			}
		}

		// for printing the lexeme and it's corresponding token
		// if(nextToken != EOL && !error) {
		// 	System.out.println("Next token is: " + nextToken + "....Next lexeme is " + value.trim());
		// }

		prevToken = nextToken;
		return nextToken;
	}

	// an error was found
	public void error() {
		error = true;
	}

	// MAIN CLASS -------------------------------------------------------------------
	public static void main(String[] args) {

		int lineCount = 1;  // keep track of source code line numbers
        boolean error = false;
        ArrayList<Integer> tokenArray = new ArrayList<Integer>();
        ArrayList<String> lexemeArray = new ArrayList<String>();

		// open the input data file and process its contents
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("projectProgram.elm")))) {
        	String line;
			while((line = reader.readLine()) != null && !error) {
				lineCount++;
				LexicalAnalyzer analyze = new LexicalAnalyzer(); 
				char[] characters = line.toCharArray();  // convert string to character array
				
				analyze.setCharacters(characters);
				analyze.getChar();
				do {
					analyze.lex();  // call to lexical analyzer

					String temp = new String(analyze.lexeme);
					String lexeme = temp.trim();  // trim white spaces
					
					tokenArray.add(analyze.nextToken);
					lexemeArray.add(lexeme);
				} while(analyze.nextToken != EOL && analyze.error == false);

				if(analyze.error == true) {
					error = true;
				}
			}

			// if there was a lexeme error
			if(error == true) {
				System.out.print("Line " + lineCount + "\n");
			} else {  // parse program
				tokenArray.add(-1);
				lexemeArray.add("EOF");

				parseProgram(tokenArray, lexemeArray);
				// System.out.println("Next token is: -1....Next lexeme is EOF");
			}
        } catch (IOException e) {
            System.out.println("ERROR - cannot open file");
        }
	}

	// function to call the program parser
	public static void parseProgram(ArrayList<Integer> tokenArray, ArrayList<String> lexemeArray) {
		try{
		    PrintWriter writer = new PrintWriter("Test.java", "UTF-8");
		    RecursiveParser parser = new RecursiveParser(tokenArray, lexemeArray, writer);

		    parser.parsePackages();  // parse the program

		    // if syntax error, write over file to simply print an error message
			if(parser.getError()) {
				writer.close();

				try {
					PrintWriter error = new PrintWriter("Test.java", "UTF-8");
					error.println("import java.util.*;");
					error.println("import java.io.*;");
					error.println("public class Test\r\n{");
					error.println("public static void main(String[] args)\r\n{");
					error.println("System.out.println(\"Error generating file.\");");
					error.println("}\r\n}");
					error.close();
				} catch(IOException e) {
					System.out.println("ERROR - cannot open file");
				}
			} else { // no errors found
				System.out.println("FINISHED - No errors found");
				writer.close();
			}
		} catch (IOException e) {
		   System.out.println("ERROR - cannot open file");
		}
	}
}


// RECURSIVE-DECENT PARSER ------------------------------------------------------------
class RecursiveParser extends LexicalAnalyzer
{
	// global variables
	public int nextToken;
	public int index;
	public int line;
	public ArrayList<Integer> tokenArray;
	public ArrayList<String> lexemeArray;
	public PrintWriter writeFile;

	public ArrayList<String> variableArray = new ArrayList<String>();
	public ArrayList<String> typeArray = new ArrayList<String>();

	public String programName;
	public boolean errors = false;
	public boolean ifStatement = false;

	// assign starting values to the global variables
	public RecursiveParser(ArrayList<Integer> tokens, ArrayList<String> lexemes, PrintWriter writer) {
		tokenArray = tokens;
		lexemeArray	= lexemes;
		writeFile = writer;
		index = 0;
		line = 1;
	}

	// get the nextToken from the array
	public void nextToken() {
		index++;
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}
	}

	// print error message to console
	public void error(String message) {
		System.out.println("ERROR - Line " + line + ": " + message);

		// go to end of the line and continue parsing
		while(nextToken != EOL && index < tokenArray.size()) {
			index++;
			nextToken = tokenArray.get(index);
		}
		line++;
		index++;

		errors = true;
	}

	// return errors boolean value
	public boolean getError() {
		return errors;
	}

	// parse the included program packages, if any
	public void parsePackages() {
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}

		if(nextToken == INCLUDE_CODE) {
			writeFile.print("import");  // print corresponding java code to file
			nextToken();  // get next token
			if(nextToken == UTIL_PACKAGE || nextToken == IO_PACKAGE) {
				if(nextToken == UTIL_PACKAGE) {
					writeFile.print(" java.util.*");  // print corresponding java code
				} else if(nextToken == IO_PACKAGE) {
					writeFile.print(" java.io.*");  // print corresponding java code
				}

				nextToken();  // get next token
				if(nextToken != SEMI_COLON) {
					String message = "Missing semi-colon";  // create error message
					error(message);  // print message and line number to console
				} else {
					writeFile.print(lexemeArray.get(index));  // write lexeme to file
					nextToken();  // get next token
					if(nextToken != EOL) {
						String message = "No multiple declarations";
						error(message);
					} else {
						writeFile.print("\r\n");  // print new line to file
						line++;
						index++;
						parsePackages(); // recursive parsePackage call
					}
				}
			} else {
				String message = "Invalid package";
				error(message);
				parsePackages(); // recursive parsePackage call
			}
		} else if(nextToken == EOL) {
			line++;
			index++;
			writeFile.print("\r\n");
			parsePackages();  // recursive parsePackage call
		} else if(nextToken == START_PROGRAM) {
			writeFile.print("public class ");  // print corresponding java code
			index++;
			parseProgram();  // call to program body parse
		} else {
			String message = "Incorrect start program declaration";
			error(message);
		}
	}

	// parse the starting declaration for the program
	public void parseProgram() {
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}
	
		if(nextToken == IDENT) {
			programName = lexemeArray.get(index);  // first indent is the name of the file
			writeFile.print(programName);  // write lexeme to file
			nextToken();  // get next token
			if(nextToken == COLON_SYM) {
				writeFile.print("\r\n{\r\npublic static void main(String[] args) {\r\n");
				nextToken();
				if(nextToken == EOL) {
					writeFile.print("\r\n");
					line++;
					index++;
					parseBlock();
				}
			} else {
				String message = "Missing colon";
				error(message);
			}
		} else {
			System.out.println("other operations to come");
		}
	}

	// parse all the statements in the program block
	public void parseBlock() {
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}

		if(nextToken == EOF) {
			System.out.println("reached end of the file");
		} else if(nextToken == EOL) {
			writeFile.print("\r\n");
			line++;
			index++;
			parseBlock();  // recursive parseBlock call
		} else if(nextToken == COMMENT) {
			String comment = lexemeArray.get(index);
			String translatedComment = comment.replaceAll("--", "/");  // java comment
			writeFile.print(translatedComment);  // write comment to file
			nextToken();  // get next token
			if(nextToken == EOL) {
				writeFile.print("\r\n");
				line++;
				index++;
				parseBlock();  // recursive parseBlock call
			}
		} else if(nextToken == INT_TYPE || nextToken == FLOAT_TYPE || nextToken == STRING_TYPE) {
			if(nextToken == INT_TYPE) {
				writeFile.print("int");
			} else if(nextToken == FLOAT_TYPE) {
				writeFile.print("float");
			} else if(nextToken == STRING_TYPE) {
				writeFile.print("String");
			}

			String type = lexemeArray.get(index);
			index++;
			variableDeclaration(type);  // call to parse variable declaration
		} else if(nextToken == PRINT_CODE) {
			writeFile.print("System.out.println");  // write java code
			nextToken();
			if(nextToken == LEFT_PAREN) {
				writeFile.print(lexemeArray.get(index));
				nextToken();
				if(nextToken == IDENT) {
					String variableName = lexemeArray.get(index);

					// check if variable was initialized
					if(variableArray.indexOf(variableName) != -1) {
						int key = variableArray.indexOf(variableName);
						String variableType = typeArray.get(key);

						writeFile.print(lexemeArray.get(index)); // write variable

						// if variable declaration also mathematical assignment
						index++;
						mathematicalAssignment(variableType);  // call to mathematical assignment
					} else {
						String message = "Variable may not have been initialized";
						error(message);
						parseBlock();  // even with error, continue parsing
					}

				} else if(nextToken == STRING_LIT) {
					writeFile.print(lexemeArray.get(index));
					nextToken();
					if(nextToken == ADD_OP) {
						writeFile.print(lexemeArray.get(index));
						index++;
						String type = "string";
						mathematicalAssignment(type);
					} else if(nextToken == RIGHT_PAREN) {
						writeFile.print(lexemeArray.get(index));
						nextToken();  // get next token
						if(nextToken == SEMI_COLON) {
							writeFile.print(lexemeArray.get(index));  // write to file
							nextToken();
							if(nextToken == EOL) {
								writeFile.print("\r\n");
								line++;  // keep track of line number
								index++;
								parseBlock();  // recursive paresBlock call 

							} else {
								String message = "Only one declaration per line";
								error(message);
								parseBlock();  // even with errors, continue to parse
							}
						} else {
							String message = "Missing semi-colon";
							error(message);
							parseBlock();
						}
					} else {
						String message = "Incorrect printMessage parameters";
						error(message);
						parseBlock();
					}
				} else {
					String message = "Incorrect printMessage parameters";
					error(message);
					parseBlock();  // even with errors, continue to parse
				}
			} else {
				String message = "Missing left parentheses";  // error message
				error(message);
				parseBlock();  // even with errors, continue to parse
			}
		} else if(nextToken == IF_CODE) {
			writeFile.print("if");
			ifStatement = true;
			index++;
			parseIfStatement();  // call to parseIfStatement
		} else if(nextToken == ELSE_CODE) {
			writeFile.print("}else");
			ifStatement = true;
			nextToken();  // get token
			if(nextToken == COLON_SYM) {
				writeFile.print("{\r\n");
				index++;
				parseBlock();  // recursive parseBlock call
			} else {
				String message = "Missing colon";
				error(message);
				parseBlock();  // even with errors, continue to parse
			}
		} else if(nextToken == ENDIF_CODE && ifStatement) {
			writeFile.print("}\r\n");
			ifStatement = false;
			index++;
			parseBlock();  // recursive parseBlock call
		} else if(nextToken == END_PROGRAM && !ifStatement) {
			nextToken();
			if(nextToken == IDENT) {
				String variableName = lexemeArray.get(index);
				// make sure ending identifier is the same as the start
				if(variableName.equals(programName)) {
					nextToken();
					if(nextToken == SEMI_COLON) {
						writeFile.print("}\r\n}");
						nextToken();
						if(nextToken == EOL) {
							writeFile.print("\r\n");
						} else {
							String message = "Only one declaration per line";
							error(message);
							parseBlock();
						}
					} else {
						String message = "Missing semi-colon";
						error(message);
						parseBlock();  // even with errors, continue to parse
					}
				} else {
					String message = "Name does not match program name";
					error(message);
					parseBlock();
				}
			} else {
				String message = "Incorrect end program declaration";
				error(message);
				parseBlock();
			}
		} else if(nextToken == SEMI_COLON && tokenArray.get(index-1) == RIGHT_PAREN) {
			writeFile.print(lexemeArray.get(index));
			nextToken();
			if(nextToken == EOL) {
				writeFile.print("\r\n");
				line++;
				index++;
				parseBlock();  // recursive parseBlock call
			} else {
				String message = "Only one declaration per line";
				error(message);
				parseBlock();  // even with errors, continue to parse
			}
		} else {
			String message = "Incorrect Expression";
			error(message);
			parseBlock();
		}
	}


	// parse the declaration of a variable
	public void variableDeclaration(String type) {
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}
		
		// declare an array variable
		if(nextToken == LEFT_BRACE) {
			writeFile.print(lexemeArray.get(index));
			nextToken();  // get next token
			if(nextToken == RIGHT_BRACE) {
				writeFile.print(lexemeArray.get(index));
				nextToken();
				if(nextToken == TYPE_DEFINE) {
					writeFile.print(" ");
					nextToken();
					if(nextToken == IDENT) {
						writeFile.print(lexemeArray.get(index));
						nextToken();

						// add variable to arraylist to keep track of inialized 
						// variables and their types
						variableArray.add(lexemeArray.get(index));
						typeArray.add(type);
						if(nextToken == SEMI_COLON) {
							writeFile.print(lexemeArray.get(index));
							line++;
							index++;
							parseBlock();  // recursive parseBlock call
						} else if(nextToken == ASSIGN_OP) {
							writeFile.print(lexemeArray.get(index));
							index++;
							arrayAssignment(type);  // call to arrayAssignment
						} else {
							String message = "Incorrect array initialization";
							error(message);
							parseBlock();
						}
					} else {
						String message = "Incorrect array initialization";
						error(message);
						parseBlock();  // even with errors, continue to parse
					}
				} else {
					String message = "Incorrect array initialization";
					error(message);
					parseBlock();
				}
			} else {
				String message = "Missing ending left brace";  // error message
				error(message);
				parseBlock();
			}
		} else if(nextToken == TYPE_DEFINE) {
			writeFile.print(" ");
			nextToken();
			if(nextToken == IDENT) {
				writeFile.print(lexemeArray.get(index));  // write to file

				// keep track of variables and type
				variableArray.add(lexemeArray.get(index));
				typeArray.add(type);
				
				nextToken();  // get next token
				if(nextToken == ASSIGN_OP) {
					writeFile.print(lexemeArray.get(index));
					index++;
					parseAssignment(type);
				} else if(nextToken == SEMI_COLON) {
					writeFile.print(lexemeArray.get(index));
					nextToken();
					if(nextToken == EOL) {
						writeFile.print("\r\n");
						line++;
						index++;
						parseBlock();  // recursive parseBlock call
					} else {
						String message = "Only one declaration per line";
						error(message);
						parseBlock();
					}
				} else {
					String message = "Incorrect varibale initialization";
					error(message);
					parseBlock();
				}			
			} else {
				String message = "Incorrect variable initialization";
				error(message);
				parseBlock();
			}
		} else {
			String message = "Incorrect variable initialization";
			error(message);
			parseBlock();  // even with errors, continue to parse
		}
	}

	public void parseAssignment(String type) {
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}

		if(nextToken == INT_LIT && type.equals("integer")) {
			writeFile.print(lexemeArray.get(index));
			index++;
			assignmentStatement();
		} else if(nextToken == FLOAT_LIT && type.equals("float")) {
			writeFile.print(lexemeArray.get(index));
			index++;
			assignmentStatement();
		} else if(nextToken == STRING_LIT && type.equals("string")) {
			writeFile.print(lexemeArray.get(index));
			index++;
			assignmentStatement();
		} else if(nextToken == IDENT) {
			String variableName = lexemeArray.get(index);

			if(variableArray.indexOf(variableName) != -1) {
				int key = variableArray.indexOf(variableName);
				String variableType = typeArray.get(key);
				
				if(type.equals("string")) {
					writeFile.print(lexemeArray.get(index));
					index++;
					mathematicalAssignment(type);
				} else if(!type.equals(variableType)) {
					String message = "Incompatable type values";
					error(message);
					parseBlock();
				} else {
					writeFile.print(lexemeArray.get(index));
					index++;
					mathematicalAssignment(type);
				}
			} else {  // variable not found in variable array
				String message = "Variable may not have been initialized";
				error(message);
				parseBlock();
			}
		} else if(nextToken == SEMI_COLON) {
			writeFile.print(lexemeArray.get(index));
			nextToken();
			if(nextToken == EOL) {
				writeFile.print("\r\n");  // write to file
				line++;
				index++;
				parseBlock();
			} else {
				String message = "Only one declaraction per line";
				error(message);
				parseBlock();
			}
		} else if(nextToken == ADD_OP || nextToken == SUB_OP || nextToken == MOD_OP ||
	   			  nextToken == MULT_OP || nextToken == DIV_OP) {
			writeFile.print(lexemeArray.get(index));
			index++;
			mathematicalAssignment(type);  // call to matematicalAssignment
		} else {
			String message = "Incorrect type value";
			error(message);
			parseBlock();  // even with errors, continue to parse
		}
	}

	public void mathematicalAssignment(String type) {
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}

		// if next token is a mathematical operator and previous was a ident or literal
		if( (nextToken == ADD_OP || nextToken == SUB_OP || nextToken == MOD_OP ||
		   nextToken == MULT_OP || nextToken == DIV_OP) && (tokenArray.get(index-1) == IDENT ||
		   tokenArray.get(index-1) == INT_LIT || tokenArray.get(index-1) == FLOAT_LIT || 
		   tokenArray.get(index-1) == STRING_LIT) ) {

		   	writeFile.print(lexemeArray.get(index));
			nextToken();  // get token
			// if literal is the same as variable type
			if( (nextToken == INT_LIT && type.equals("integer")) || 
				(nextToken == FLOAT_LIT && type.equals("float")) | 
				(nextToken == STRING_LIT && type.equals("string"))) {

				writeFile.print(lexemeArray.get(index));
				nextToken();
				if(nextToken == SEMI_COLON) {
					writeFile.print(lexemeArray.get(index));  // write to file
					nextToken();
					if(nextToken == EOL) {
						writeFile.print("\r\n");
						line++;
						index++;
						parseBlock();  // recursive parseBlock call
					} else {
						String message = "Only one declaraction per line";
						error(message);
						parseBlock();
					}
				} else {	
					System.out.println(lexemeArray.get(index));
					String message = "Incorrect type value";
					error(message);
					parseBlock();  // even with errors, continue to parse
				}
			} else if(nextToken == IDENT) {
				String variableName = lexemeArray.get(index);

				if(variableArray.indexOf(variableName) != -1) {
					int key = variableArray.indexOf(variableName);
					String variableType = typeArray.get(key);
					
					if(type.equals("string")) {
						writeFile.print(lexemeArray.get(index));
						index++;
						mathematicalAssignment(type);  // call to mathematicalAssignment
					} else if(!type.equals(variableType)) {
						String message = "Incompatable type values";
						error(message);
						parseBlock();
					} else {
						writeFile.print(lexemeArray.get(index));
						index++;
						mathematicalAssignment(type); // call to mathematical assignment
					}
				} else {
					String message = "Variable may not have been initialized";
					error(message);
					parseBlock();
				}
			} else {
				String message = "Incorrect type value";  // error message
				error(message);
				parseBlock();
			}
		} else if( (nextToken == IDENT || nextToken == STRING_LIT) && (tokenArray.get(index-1) == ADD_OP || 
				tokenArray.get(index-1) == SUB_OP || tokenArray.get(index-1) == MOD_OP ||
		   		tokenArray.get(index-1) == MULT_OP || tokenArray.get(index-1) == DIV_OP) ) {
			
			if(nextToken == STRING_LIT) {
				writeFile.print(lexemeArray.get(index));
				index++;
				mathematicalAssignment(type);
			} else {
				String variableName = lexemeArray.get(index);

				if(variableArray.indexOf(variableName) != -1) {
					int key = variableArray.indexOf(variableName);
					String variableType = typeArray.get(key);
					
					if(type.equals("string")) {
						writeFile.print(lexemeArray.get(index));
						index++;
						mathematicalAssignment(type);
					} else if(!type.equals(variableType)) {
						String message = "Incompatable type values";
						error(message);
						parseBlock();
					} else {
						writeFile.print(lexemeArray.get(index));
						index++;
						mathematicalAssignment(type);
					}
				} else {
					String message = "Variable may not have been initialized";
					error(message);
					parseBlock();
				}
			}
		} else if(nextToken == SEMI_COLON) {
			writeFile.print(lexemeArray.get(index));  // write to file
			nextToken();  // get next token
			if(nextToken == EOL) {
				writeFile.print("\r\n");
				line++;
				index++;
				parseBlock();  // recursive parseBlock call
			} else {
				String message = "Only one declaraction per line";
				error(message);
				parseBlock();
			}
		} else if(nextToken == RIGHT_PAREN) {
			writeFile.print(lexemeArray.get(index));
			index++;
			parseBlock();
		} else {
			String message = "Incorrect expression";  // error message
			error(message);
			parseBlock();  // even with errors, continue to parse
		}
	}

	public void assignmentStatement() {
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}

		if(nextToken == SEMI_COLON) {
			writeFile.print(lexemeArray.get(index));
			index++;
			nextToken = tokenArray.get(index);  // get next token
			if(nextToken == EOL) {
				writeFile.print("\r\n");
				line++;
				index++;
				parseBlock();  // recursive parseBlock call
			} else {
				String message = "Only one declaration per line";
				error(message);
				parseBlock();
			}
		} else {
			String message = "Missing semi-colon";
			error(message);
			parseBlock();  // even with errors, continue to parse
		}
	}

	// check for brackes after array assignment
	public void arrayAssignment(String type) {
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}
		
		System.out.println(lexemeArray.get(index) + " " + nextToken);
		if(nextToken == LEFT_BRACKET) {
			writeFile.print(lexemeArray.get(index));
			index++;
			arrayLiteral(type);  // call to array literal assignment
		} else {
			String message = "Incorrect array initialization";
			error(message);
			parseBlock();  // even with errors, continue to parse
		}
	}

	// check the contents of the literals assigned to an array
	public void arrayLiteral(String type) {
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}

		if( (nextToken == INT_LIT && type.equals("integer")) ||
			(nextToken == FLOAT_LIT && type.equals("float")) ||
			(nextToken == STRING_LIT && type.equals("string")) ) {

			writeFile.print(lexemeArray.get(index));
			nextToken();
			if(nextToken == COMMA_SYM) {
				writeFile.print(lexemeArray.get(index));
				index++;
				arrayLiteral(type);  // recursive arrayLiteral call
			} else if(nextToken == RIGHT_BRACKET) {
				writeFile.print(lexemeArray.get(index));
				nextToken();
				if(nextToken == SEMI_COLON) {
					writeFile.print(lexemeArray.get(index));
					nextToken();
					if(nextToken == EOL) {
						writeFile.print("\r\n");
						line++;
						index++;
						parseBlock();  // recursive parseBlock call
					} else {
						String message = "Only one declaraction per line";
						error(message);
						parseBlock();
					}
				} else {
					String message = "Missing semi-colon";
					error(message);
					parseBlock();  // even with errors, continue to parse
				}
			} else {
				String message = "Missing comma delimiter";  // error message
				error(message);
				parseBlock();
			}
		} else {
			String message = "Value type not compatable with array type";
			error(message);
			parseBlock();
		}
	}

	// parse the parameters of an if statement
	public void parseIfStatement() {
		if(index < tokenArray.size()) {
			nextToken = tokenArray.get(index);
		}

		if(nextToken == LEFT_PAREN) {
			writeFile.print(lexemeArray.get(index));
			nextToken();  // get next token
			if(nextToken == IDENT) {
				String variableName = lexemeArray.get(index);

				// if variable was initialized
				if(variableArray.indexOf(variableName) != -1) {
					int key = variableArray.indexOf(variableName);
					String variableType = typeArray.get(key);
					
					writeFile.print(lexemeArray.get(index));
					nextToken();  // get next token
					if(nextToken == EQUALS_OP || nextToken == LESS_SYM || nextToken == GREATER_SYM ||
						nextToken == NOTEQUALS_OP || nextToken == LESSEQUALS_OP || nextToken == GREATEREQUALS_OP)	 {

						writeFile.print(lexemeArray.get(index));
						nextToken();

						if(nextToken == IDENT || nextToken == INT_LIT || nextToken == FLOAT_LIT ||
							nextToken == STRING_LIT) {

							if(nextToken == IDENT) {
								if(variableArray.indexOf(variableName) != -1) {
									key = variableArray.indexOf(variableName);
									variableType = typeArray.get(key);
									
									writeFile.print(lexemeArray.get(index));
									nextToken();  // get next token
									if(nextToken == AND_OP || nextToken == OR_OP || nextToken == NOT_OP) {
										System.out.println("operation symbol");
										writeFile.print(lexemeArray.get(index));
										index++;
										parseIfStatement();  // recursive parseIfStatement call
									} else if(nextToken == RIGHT_PAREN) {
										writeFile.print(lexemeArray.get(index));
										nextToken();
										if(nextToken == COLON_SYM) {
											writeFile.print("{\r\n");
											nextToken();
											if(nextToken == EOL) {
												writeFile.print("\r\n");
												line++;
												index++;
												parseBlock();
											} else {
												String message = "Only one declaraction per line";
												error(message);
												parseBlock();  // even with errors, continue to parse
											}
										} else {
											String message = "Missing colon";
											error(message);
											parseBlock();
										}
									} else {
										String message = "Incorrect expression";
										error(message);
										parseBlock();
									}
								} else {
									String message = "Variable may not have been initialized";
									error(message);
									parseBlock();  // even with errors, continue to parse
								}
							} else {
								writeFile.print(lexemeArray.get(index));
								nextToken();
								if(nextToken == AND_OP || nextToken == OR_OP || nextToken == NOT_OP) {
									System.out.println("operationldkfjslkdfjskldjf");
									writeFile.print(lexemeArray.get(index));
									index++;
									parseIfStatement();
								} else if(nextToken == RIGHT_PAREN) {
									writeFile.print(lexemeArray.get(index));
									nextToken();
									if(nextToken == COLON_SYM) {
										writeFile.print("{\r\n");  // write to file
										nextToken();
										if(nextToken == EOL) {
											writeFile.print("\r\n");
											line++;
											index++;
											parseBlock();
										} else {
											String message = "Only one declaraction per line";
											error(message);
											parseBlock();
										}
									} else {
										String message = "Missing colon";
										error(message);
										parseBlock();
									}
								} else {
									String message = "Incorrect expression";
									error(message);
									parseBlock();
								}
							}
						} else {
							String message = "Incorrect expression";
							error(message);
							parseBlock();  // even with errors, continue to parse
						}
					} else {
						String message = "Incorrect relational operator";  // error message
						error(message);
						parseBlock();
					}
				} else {
					String message = "Variable may not have been initialized";
					error(message);
					parseBlock();
				}
			} else {
				String message = "Incorrect expression";
				error(message);
				parseBlock();
			}
		} else if(nextToken == IDENT) {
			System.out.println("next :" + lexemeArray.get(index));
			String variableName = lexemeArray.get(index);

			if(variableArray.indexOf(variableName) != -1) {
				int key = variableArray.indexOf(variableName);
				String variableType = typeArray.get(key);
				
				writeFile.print(lexemeArray.get(index));  // write to file
				nextToken();  // get next token

				if(nextToken == EQUALS_OP || nextToken == LESS_SYM || nextToken == GREATER_SYM ||
					nextToken == NOTEQUALS_OP || nextToken == LESSEQUALS_OP || nextToken == GREATEREQUALS_OP)	 {

					writeFile.print(lexemeArray.get(index));
					nextToken();  // get next token

					if(nextToken == IDENT || nextToken == INT_LIT || nextToken == FLOAT_LIT ||
						nextToken == STRING_LIT) {

						if(nextToken == IDENT) {
							if(variableArray.indexOf(variableName) != -1) {
								key = variableArray.indexOf(variableName);
								variableType = typeArray.get(key);
								
								writeFile.print(lexemeArray.get(index));
								nextToken();
								if(nextToken == AND_OP || nextToken == OR_OP || nextToken == NOT_OP) {
									System.out.println("operation symbol");
									writeFile.print(lexemeArray.get(index));
									index++;
									parseIfStatement();  // recursive parseIfStatement
								} else if(nextToken == RIGHT_PAREN) {
									writeFile.print(lexemeArray.get(index));
									nextToken();
									if(nextToken == COLON_SYM) {
										writeFile.print("{");
										nextToken();
										if(nextToken == EOL) {
											writeFile.print("\r\n");
											line++;
											index++;
											parseBlock();  // recursive parseBlock
										} else {
											String message = "Only one declaraction per line";
											error(message);
											parseBlock();
										}
									} else {
										String message = "Missing colon";
										error(message);
										parseBlock();
									}
								} else {
									String message = "Incorrect expression";
									error(message);
									parseBlock();
								}
							} else {
								String message = "Variable may not have been initialized";
								error(message);
								parseBlock();
							}
						} else {
							writeFile.print(lexemeArray.get(index));
							nextToken();
							System.out.print(nextToken);
							if(nextToken == AND_OP || nextToken == OR_OP || nextToken == NOT_OP) {
								writeFile.print(lexemeArray.get(index));
								index++;
								parseIfStatement();  // recursive parseIfStatement
							} else if(nextToken == RIGHT_PAREN) {
								writeFile.print(lexemeArray.get(index));
								nextToken();
								if(nextToken == COLON_SYM) {
									writeFile.print("{");
									nextToken();
									if(nextToken == EOL) {
										writeFile.print("\r\n");
										line++;
										index++;
										parseBlock();  // recursive parseBlock call
									} else {
										String message = "Only one declaraction per line";
										error(message);
										parseBlock();
									}
								} else {
									String message = "Missing colon";
									error(message);
									parseBlock();
								}
							} else {
								String message = "Incorrect expression";
								error(message);
								parseBlock();
							}
						}
					} else {
						String message = "Incorrect expression";
						error(message);
						parseBlock();
					}
				} else {
					String message = "Incorrect relational operator";
					error(message);
					parseBlock();  // even with errors, continue to parse
				}
			}
		} else {
			String message = "Missing left parentheses";
			error(message);
			parseBlock();
		}
	}
}