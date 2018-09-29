package hangman;

import java.util.HashSet;
import java.util.TreeSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
public class Dictionary {
	public Dictionary(){
		words = new TreeSet<String>();
		patternMap = new HashMap<String, Set<String> >();
	}
	
	private Map<String, Set<String> > patternMap;
	private Set<String> words;
	
	public void setWords(Set<String> s){
		words = s;
	}
	public int getWordsSize(){
		return words.size();
	}

	public Map<String,Set<String>> makePatternSet(char c, Set<Character> correctGuesses){
		Map<String,Set<String>> newMap = new HashMap<String,Set<String>>();
		for(String w:words){
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<w.length();i++){
				if(w.charAt(i) == c){
					sb.append(c);
				}
				else{
					boolean existingChar = false;
					for(char x:correctGuesses){
						if(w.charAt(i) == x){
							sb.append(x);
							existingChar = true;
							break;
						}
					}
					if(!existingChar){
						sb.append("-");
					}
				}
			}
			if(!newMap.containsKey(sb.toString())){
				newMap.put(sb.toString(), new HashSet<String>());
			}
			Set<String> temp = newMap.get(sb.toString());
			temp.add(w);
		}
		return newMap;
	}

	public void printDictionary(){
		for(String w:words){
			System.out.println(w);
		}
	}
	public void setPatternMap(Map<String, Set<String>> newMap){
		this.patternMap = newMap;
	}
	public void addWord(String w){
		w = w.toLowerCase();
		words.add(w);
	}
	
	public void trim(int wordLength){
		Set<String> newWords = new HashSet<String>();
		for(String word : words){
			if(word.length() == wordLength)
				newWords.add(word);
		}
		words = newWords;
	}

	public String getWord(){
		return words.iterator().next();
	}
}
