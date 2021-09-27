import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//card class
class Card {
  int value;
  String suit;
  boolean faceUp;

  Card(int value, String suit, boolean faceUp) {
    this.value = value;
    this.suit = suit;
    this.faceUp = faceUp;
  }

  Card(int value, String suit) {
    this(value, suit, false);
  }

  //draw method
  WorldImage draw() {
    RectangleImage rect = new RectangleImage(50, 70, OutlineMode.SOLID, Color.CYAN);
    if (faceUp) {
      TextImage suitDraw = new TextImage(this.suit, 20, Color.RED);
      TextImage num;
      if (this.value == 1) {
        num = new TextImage("A", 20, Color.BLACK);
      }
      else if (this.value == 11) {
        num = new TextImage("J", 20, Color.BLACK);
      }
      else if (this.value == 12) {
        num = new TextImage("Q", 20, Color.BLACK);
      }
      else if (this.value == 13) {
        num = new TextImage("K", 20, Color.BLACK);
      }
      else {
        num = new TextImage(Integer.toString(this.value), 20, Color.BLACK);
      }
      return new OverlayImage(new AboveImage(suitDraw, num), rect);
    }
    else {
      return rect;
    }
  }
}

//class for the game
class Concentration extends World {
  ArrayList<ArrayList<Card>> cards;
  Random rand;
  int flip;
  Posn prev;
  Posn curr;
  int score;
  int randomizer;

  Concentration(Random rand, int flip, Posn prev, Posn curr, int score, int randomizer) {
    this.rand = rand;
    this.flip = flip;
    this.prev = prev;
    this.curr = curr;
    this.score = score;
    this.randomizer = randomizer;
    
    this.shuffleDeck(this.rand);
  }
  
  Concentration(Random rand, int flip, Posn prev, Posn curr, int score) {
    this.rand = rand;
    this.flip = flip;
    this.prev = prev;
    this.curr = curr;
    this.score = score;
    this.randomizer = 0;
    
    this.shuffleDeck(this.rand);
  }

  Concentration() {
    this(new Random(), 0, new Posn(-1,-1), new Posn(-1,-1), 26);
  }
  
  void shuffleDeck(Random rand) {
    this.rand = rand;
    this.cards = new ArrayList<ArrayList<Card>>();

    //initialize a list of possible values
    ArrayList<Integer> possibleValues = new ArrayList<Integer>(
        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13));

    //initialize a list of possible suits
    ArrayList<String> possibleSuits = new ArrayList<String>(Arrays.asList("♣", "♦", "♥", "♠"));

    ArrayList<Card> possibleCards = new ArrayList<Card>();
    for (String s : possibleSuits) {
      for (Integer i : possibleValues) {
        possibleCards.add(new Card(i , s));
      }
    }
    //Add rows to the board
    for (int i = 0; i < 4; i++) {
      //initialize the current row as empty
      ArrayList<Card> row = new ArrayList<Card>();

      //Add cards to the row
      for (int j = 0; j < 13; j++) {
        int index = this.rand.nextInt(possibleCards.size());
        Card c = possibleCards.get(index);
        row.add(c);
        possibleCards.remove(index);
      }
      this.cards.add(row);
    }
  }

  //draws the cards into the game
  public WorldScene makeScene() {
    RectangleImage spacerX = new RectangleImage(10, 70, OutlineMode.SOLID, Color.WHITE);
    RectangleImage spacerY = new RectangleImage(50, 10, OutlineMode.SOLID, Color.WHITE);
    WorldScene scene = this.getEmptyScene();
    WorldImage board = new EmptyImage();
    for (ArrayList<Card> row : this.cards) {
      WorldImage temp = new EmptyImage();
      for (Card c : row) {
        temp = new BesideImage(temp, spacerX, c.draw());
      }
      board = new AboveImage(board, spacerY, temp);
    }
    scene.placeImageXY(board, 400, 200);
    WorldImage score = new TextImage("Score: " + Integer.toString(this.score), 24, Color.BLACK);
    scene.placeImageXY(score, 50, 15);
    return scene;
  }

  //mouse clicked handler
  public void onMouseClicked(Posn pos) {
    int indexX = (int) (pos.x - 20) / 60;
    int indexY = (int) (pos.y - 50) / 80;

    if (indexX > 12 || indexX < 0 || indexY > 3 || indexY < 0) {
      return;
    }
    if (!this.cards.get(indexY).get(indexX).faceUp) {
      if (this.flip % 2 == 0) {
        this.cards.get(indexY).get(indexX).faceUp = true;
        this.prev = new Posn(indexX, indexY);
        this.flip++;
      }
      else {
        this.cards.get(indexY).get(indexX).faceUp = true;
        this.curr = new Posn(indexX, indexY);
        this.flip++;
      }
    }
    this.makeScene();
  }
  
  //on tick handler
  public void onTick() {
    if (this.flip % 2 == 0 && this.flip > 1) {
      if (this.cards.get(this.curr.y).get(this.curr.x).value == 
          this.cards.get(this.prev.y).get(this.prev.x).value) {
        this.score--;
        if (this.score == 0) {
          this.endOfWorld("You win!");
        }
      }
      else {
        this.cards.get(this.curr.y).get(this.curr.x).faceUp = false;
        this.cards.get(this.prev.y).get(this.prev.x).faceUp = false;
      }
      this.flip = 0;
    }
  }
  
  //victory scene screen
  public WorldScene lastScene(String msg) {
    WorldScene temp =  new WorldScene(800,400);
    temp.placeImageXY(new TextImage(msg, 80, Color.BLUE), 400, 200);
    return temp;
    
  }
  
  //on key handler
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.randomizer++;
      this.rand = new Random(randomizer);
      this.flip = 0;
      this.prev = new Posn(-1, -1);
      this.curr = new Posn(-1, -1);
      this.score = 26;
      
      this.shuffleDeck(this.rand);
    }
    else {
      return;
    }
  }

}

//examples class
class ExamplesConcentration {
  Card c1 = new Card(1, "♠", false);
  Card c2 = new Card(1, "♠", true);
  Card c3 = new Card(6, "♥", false);
  Card c4 = new Card(6, "♥", true);
  //"♣", "♦", "♥", "♠"
  ArrayList<Card> row1 = new ArrayList<Card>(Arrays.asList(new Card(3, "♠", false), 
      new Card(12, "♦", false), new Card(7, "♠", false), new Card(9, "♥", false), 
      new Card(12, "♠", false), new Card(6, "♣", false), new Card(5, "♠", false), 
      new Card(2, "♦", false), new Card(7, "♥", false), new Card(2, "♠", false), 
      new Card(3, "♦", false), new Card(11, "♦", false), new Card(13, "♥", false)));
  ArrayList<Card> row2 = new ArrayList<Card>(Arrays.asList(new Card(10, "♠", false), 
      new Card(4, "♣", false), new Card(9, "♠", false), new Card(13, "♣", false), 
      new Card(12, "♥", false), new Card(10, "♦", false), new Card(6, "♦", false), 
      new Card(1, "♣", false), new Card(9, "♦", false), new Card(1, "♥", false), 
      new Card(8, "♥", false), new Card(5, "♦", false), new Card(3, "♣", false)));
  ArrayList<Card> row3 = new ArrayList<Card>(Arrays.asList(new Card(10, "♥", false), 
      new Card(4, "♠", false), new Card(6, "♠", false), new Card(13, "♦", false), 
      new Card(9, "♣", false), new Card(1, "♠", false), new Card(3, "♥", false), 
      new Card(8, "♦", false), new Card(1, "♦", false), new Card(11, "♠", false), 
      new Card(8, "♣", false), new Card(5, "♥", false), new Card(11, "♥", false)));
  ArrayList<Card> row4 = new ArrayList<Card>(Arrays.asList(new Card(2, "♥", false), 
      new Card(13, "♠", false), new Card(12, "♣", false), new Card(7, "♣", false), 
      new Card(2, "♣", false), new Card(11, "♣", false), new Card(10, "♣", false), 
      new Card(5, "♣", false), new Card(8, "♠", false), new Card(7, "♦", false), 
      new Card(4, "♥", false), new Card(6, "♥", false), new Card(4, "♦", false)));
  
  ArrayList<ArrayList<Card>> premade = new ArrayList<ArrayList<Card>>(Arrays.asList(
      row1, row2, row3, row4));
  
  //tests the draw function in Card class
  void testDraw(Tester t) {
    t.checkExpect(this.c1.draw(), new RectangleImage(50, 70, OutlineMode.SOLID, Color.CYAN));
    t.checkExpect(this.c2.draw(), 
        new OverlayImage(new AboveImage(new TextImage("♠", 20, Color.RED), 
            new TextImage("A", 20, Color.BLACK)), 
            new RectangleImage(50, 70, OutlineMode.SOLID, Color.CYAN)));
    t.checkExpect(this.c3.draw(), new RectangleImage(50, 70, OutlineMode.SOLID, Color.CYAN));
    t.checkExpect(this.c4.draw(), 
        new OverlayImage(new AboveImage(new TextImage("♥", 20, Color.RED), 
            new TextImage("6", 20, Color.BLACK)), 
            new RectangleImage(50, 70, OutlineMode.SOLID, Color.CYAN)));
  }


  //big bang
  void testBigBang(Tester t) {
    Concentration game = new Concentration(new Random(10), 0, new Posn(-1, -1), 
        new Posn(-1,-1), 26);
    game.bigBang(800, 400, .5);
  }
  
  //tests shuffleDeck
  void testShuffleDeck(Tester t) {
    Concentration game1 = new Concentration(new Random(2), 0, new Posn(-1, -1), 
        new Posn(-1,-1), 26);
    game1.shuffleDeck(new Random(10));
    t.checkExpect(game1.cards, premade);
  }
  
  void testOnMouseClicked(Tester t) {
    Concentration game2 = new Concentration(new Random(10), 0, new Posn(-1, -1), 
        new Posn(-1,-1), 26);
    Concentration game3 = new Concentration(new Random(10), 0, new Posn(-1, -1), 
        new Posn(-1,-1), 26);
    game2.onMouseClicked(new Posn(700, 400));
    t.checkExpect(game2, game3);
    game2.onMouseClicked(new Posn(40, 60));
    t.checkExpect(game2.cards.get(0).get(0), new Card(3, "♠", true));
    game2.onMouseClicked(new Posn(80, 60));
    t.checkExpect(game2.cards.get(0).get(1), new Card(12, "♦", true));
  }
  
  void testOnTick(Tester t) {
    Concentration game2 = new Concentration(new Random(10), 0, new Posn(-1, -1), 
        new Posn(-1,-1), 26);
    t.checkExpect(game2.score, 26);
    game2.onMouseClicked(new Posn(40, 60));
    game2.onMouseClicked(new Posn(80, 60));
    game2.onTick();
    Concentration game3 = new Concentration(new Random(10), 0, new Posn(0, 0), 
        new Posn(1,0), 26);
    //no match has occurred
    t.checkExpect(game2, game3);
    game2.onMouseClicked(new Posn(265, 60));
    game2.onMouseClicked(new Posn(80, 60));
    game2.onTick();
    //a match has occurred
    t.checkExpect(game2.score, 25);
    game2.score = 0;
    game2.onTick();
    t.checkExpect(game2.score, 0);
  }
  
  void testLastScene(Tester t) {
    Concentration game2 = new Concentration(new Random(10), 0, new Posn(-1, -1), 
        new Posn(-1,-1), 26);
    WorldScene temp =  new WorldScene(800,400);
    temp.placeImageXY(new TextImage("You win!", 80, Color.BLUE), 400, 200);
    t.checkExpect(game2.lastScene("You win!"), temp);
    
  }
  
  void testOnKeyEvent(Tester t) {
    Concentration game2 = new Concentration(new Random(10), 0, new Posn(-1, -1), 
        new Posn(-1,-1), 26);
    game2.onMouseClicked(new Posn(40, 60));
    game2.onKeyEvent("k");
    Concentration game3 = new Concentration(new Random(10), 0, new Posn(-1, -1), 
        new Posn(-1,-1), 26);
    game3.onMouseClicked(new Posn(40, 60));
    //any key other than r is pressed, nothing changes
    t.checkExpect(game2, game3);
    game2.onKeyEvent("r");
    Concentration game4 = new Concentration(new Random(1), 0, new Posn(-1, -1), 
        new Posn(-1,-1), 26, 1);
    t.checkExpect(game2, game4);
    
    
  }
}

