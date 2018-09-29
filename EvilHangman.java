package hangman;

import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.io.*;
import java.util.Scanner;
import java.util.Map;
import java.util.Iterator;

public class EvilHangman implements IEvilHangmanGame {
	public EvilHangman(){
		madeGuesses = new HashSet<Character>();
		dict = new Dictionary();
		correctGuesses = new HashSet<Character>();
	}
	Dictionary dict;
	Set<Character> correctGuesses;
	
	public static void main(String[] args){
		try{
			String dictionary = new String();
			dictionary = args[0];
			File temp = new File(dictionary);

			int wordLength = Integer.parseInt(args[1]);
			int guesses = Integer.parseInt(args[2]);

			if(wordLength < 2){
				System.out.println("Word length must be at least 2!");
				return;
			}
			if(guesses < 1){
				System.out.println("Number of guesses must be at least 1!");
				return;
			}

			EvilHangman myGame = new EvilHangman();
			myGame.startGame(temp, wordLength);

			if(myGame.dict.getWordsSize() == 0){
				System.out.println("Dictionary file was empty!");
				return;
			}

			StringBuilder tempSB = new StringBuilder();

			for(int i=0;i<wordLength;i++){
				tempSB.append("-");
			}

			String wordPattern = tempSB.toString();
			int i=0;
			Scanner userInput = new Scanner(System.in);

			while(i!=guesses){
				int tempCount = 0;
				for(int k=0;k<wordLength;k++){
					if(wordPattern.charAt(k) != '-')
						tempCount++;
					else
						break;
				}
				if(tempCount == wordLength){
					System.out.println("You won! The word was " + wordPattern);
					userInput.close();
					return;
				}

				if(guesses-i!=1){
					System.out.println("You have " + (guesses-i) + " guesses left");
					System.out.print("used letters: ");
				}
				else{
					System.out.println("You have " + (guesses-i) + " guess left");
					System.out.print("Used letters: ");
				}


				for(char ch:myGame.madeGuesses){
					System.out.print(ch+" ");
				}
				System.out.println();
				System.out.println("Word: " + wordPattern);
				System.out.print("Enter guess: ");
				try{
					String guess = new String();
					while(true){
						guess = userInput.next();
						if(guess.length() != 1){
							System.out.print("Please enter a single letter");
						}
						else if(!Character.isLetter(guess.charAt(0))){
							System.out.print("That was not a letter. Please enter a single letter: ");
						}
						else if(guess.charAt(0) == '\n'){
							System.out.print("Please enter a single letter: ");
						}
						else
							break;
					}
					char charGuess = guess.charAt(0);
					charGuess = Character.toLowerCase(charGuess);
					if(myGame.madeGuesses.contains(charGuess)){
						throw new GuessAlreadyMadeException();
					}
					Set<String> newWords = myGame.makeGuess(charGuess);
					if(newWords.isEmpty()){
						System.out.println("Sorry, There are no '"+charGuess+"'s");
					}
					else{
						myGame.dict.setWords(newWords);
						wordPattern = myGame.getPattern(newWords.iterator().next(),myGame.correctGuesses,charGuess);
						int count = 0;
						for(int j=0;j<wordPattern.length();j++){
							if(wordPattern.charAt(j) == charGuess){
								count++;
							}
						}
						if(count==0){
							System.out.println("Sorry, There are no '"+charGuess+"'s");
							System.out.println();
						}
						else{
							if(count == 1){
								System.out.println("Yes, there is "+count+" "+charGuess);
								System.out.println();
								myGame.correctGuesses.add(charGuess);
							}
							else{
								System.out.println("Yes, there are "+count+" "+charGuess+"'s");
								System.out.println();
								myGame.correctGuesses.add(charGuess);
							}
						}
					}
					i++;
				}
				catch(GuessAlreadyMadeException ex){
					System.out.println("You have already guessed that letter!");
					System.out.println();
				}
			}
			userInput.close();
			int tempCount = 0;
			for(int k=0;k<wordLength;k++){
				if(wordPattern.charAt(k) == '-'){
					System.out.println("You lose! \nThe word was: "+myGame.dict.getWord());
					return;
				}
			}
			System.out.println("You won! \nThe word was " + wordPattern);
		}
		catch(ArrayIndexOutOfBoundsException ex){
			System.out.println("Number of arguments invalid.");
		}
	}
	private HashSet<Character> madeGuesses;
	 
	@Override
	public void startGame(File dictionary, int wordLength) {
		// TODO Auto-generated method stub
		try{
			FileReader myReader = new FileReader(dictionary);
			Scanner myScanner = new Scanner(dictionary);
			while(myScanner.hasNext()){
				String next = myScanner.next();
				if(next.length() == wordLength)
					dict.addWord(next);
			}
			myReader.close();
			myScanner.close();
		}
		catch(IOException ex){
			System.out.println("Error reading file '" + dictionary.toString() + "'");
		}
	}

	@Override
	public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
		// TODO Auto-generated method stub
		madeGuesses.add(guess);
		Map<String,Set<String>> patternMap = dict.makePatternSet(guess, correctGuesses);
		Iterator it = patternMap.entrySet().iterator();
		Set<String> maxSet = new TreeSet<String>();
		String maxPattern = new String();
		//find max set
		while(it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			Set<String> temp = (Set<String>)pair.getValue();
			if(temp.size() > maxSet.size()){
				maxSet = temp;
				maxPattern = (String)pair.getKey();
			}
			else if(temp.size() == maxSet.size()){
				String patternCurrent = (String)pair.getKey();
				int countCurrent = 0;
				int countMax = 0;
				for(int i=0;i<patternCurrent.length();i++){
					if(patternCurrent.charAt(i) == guess){
						countCurrent++;
					}
					if(maxPattern.charAt(i) == guess)
						countMax++;
				}
				if(countCurrent < countMax){
					maxSet = temp;
					maxPattern = patternCurrent;
				}
				else if(countCurrent == countMax){
					for(int i=patternCurrent.length()-1;i >= 0;i--){
						if(patternCurrent.charAt(i) != '-' && maxPattern.charAt(i) == '-'){
							maxSet = temp;
							maxPattern = patternCurrent;
							break;
						}
						else if(patternCurrent.charAt(i) == '-' && maxPattern.charAt(i) !='-'){
							break;
						}
					}
				}
			}
		}

		dict.setWords(maxSet);
		return maxSet;
	}
	public String getPattern(String word,Set<Character> correctGuesses,char guess){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<word.length();i++){
			boolean exsistingChar = false;
			if(word.charAt(i)==guess){
				exsistingChar = true;
				sb.append(guess);
			}
			else{
				for(char c:correctGuesses){
					if(c == word.charAt(i)){
						sb.append(c);
						exsistingChar = true;
						break;
					}
				}
			}
			if(!exsistingChar){
				sb.append("-");
			}
		}
		return sb.toString();
	}
}
