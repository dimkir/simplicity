package simplicity;

/**
* This class provides method to generate
* random names, like the ones 
* heroku uses for their host names.
* Eg.
* quiet-lake
* big-mountain
* flying-dragon
* ... etc.
* names combined of adjective and noun.
*/

import java.util.Random;
import processing.core.PApplet;
import java.util.ArrayList;

public class RandomNames
{
    /**
    * We use Singleton pattern to be able to use
    * static method RandomNames.getName() from inside sketch
    * to generte default names.
    */
    private static RandomNames instance;
    
    private PApplet sketch;
    private Random random = new Random();
    
    private ArrayList<String> mNouns = new ArrayList<String>();
    private ArrayList<String> mAdjectives = new ArrayList<String>();
  
    /**
    * this should be simplier.
    */
    private int randInt(int rightExclBoundary){
       return this.random.nextInt(rightExclBoundary);
    }
      
  
    public static String getName()
    {
        spawnInstanceIfNotExists();
        
        return instance.getRandomName();
    }
    
    private static void spawnInstanceIfNotExists(){
       if ( instance == null ){
          instance = new RandomNames();
       }
    }

    /**
    * This is default constructor.
    */
    RandomNames(){
        fillWordsFromString(mNouns, "time year people way day man thing woman life child world school state family student group country problem hand part place case week company system program question work government number night point home water room mother area money story fact month lot right"); 
        fillWordsFromString(mAdjectives, "good new old great high small large long young right early big late full far low bad sure clear likely real black white free easy short strong true hard poor wide simple close fine wrong french nice happy red sorry dead heavy cold ready green deep left complete hot fair huge" );
    }
    
    
    /**
    * This is constructor, which would allow you to use
    * your own custom textfiles containing words.
    *
    */
    public RandomNames(String adjectiveFile, String nounFile,  PApplet ssketch){
         String[] adjectives = ssketch.loadStrings(adjectiveFile);
         String[] nouns = ssketch.loadStrings(nounFile);
         fillWordsFromStringar(mAdjectives, adjectives);
         fillWordsFromStringar(mNouns, nouns);
         
    }
    
    private void fillWordsFromString(ArrayList<String> wordArlis, String s){
        String[] words =    s.split("\\s+");
        fillWordsFromStringar(wordArlis, words);
    }
    
    private void fillWordsFromStringar(ArrayList<String> wordArlis, String[] wordCandidates){
        for(String s : wordCandidates){
           s = s.trim();
           if ( ! s.equals("") ){
              wordArlis.add(s);
           }
        }
    }
    
    
    
    
    public String getRandomName(){
       return randomAdjective() + " "  + randomNoun();
    }   
    
    private String randomAdjective(){
       int idx = randInt(mAdjectives.size());
       return mAdjectives.get(idx);
    }
    
    private String randomNoun(){
        int idx = randInt(mNouns.size());
        return mNouns.get(idx);
    }
    
    @Override
    public String toString(){
       return String.format("RandomNames has %d adjectives and %d nouns, total possible %d name combinations",
              mAdjectives.size(), mNouns.size(), mAdjectives.size() * mNouns.size() 
              ); 
    }
    
    
    public static String info(){
//       if ( this.instance == null ){
//          return "RandomNames is not initialized";
//       }
       spawnInstanceIfNotExists();
       return instance.toString();
    }

    
    
}
