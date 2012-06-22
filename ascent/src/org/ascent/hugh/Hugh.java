/**************************************************************************
 * Copyright 2009 Brian Dougherty                                          *
 *                                                                         *
 * Licensed under the Apache License, Version 2.0 (the "License");         *
 * you may not use this file except in compliance with the License.        *
 * You may obtain a copy of the License at                                 *
 *                                                                         *
 * http://www.apache.org/licenses/LICENSE-2.0                              *
 *                                                                         *
 * Unless required by applicable law or agreed to in writing, software     *
 * distributed under the License is distributed on an "AS IS" BASIS,       *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.*
 * See the License for the specific language governing permissions and     *
 * limitations under the License.                                          *
 **************************************************************************/
package org.ascent.hugh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ascent.binpacking.BasicBinPacker;
import org.ascent.binpacking.ClassicBin;
import org.ascent.binpacking.ClassicItem;

public class Hugh {
	TheHouse th_ = new TheHouse();
	ArrayList<Bettor> playerList_ = new ArrayList();
	ArrayList<BettingProgression> bpList_ =new ArrayList();
	List <ClassicBin> bins_ = new ArrayList();
	List <ClassicItem> items_ = new ArrayList();
	ArrayList<Personality> personalityList_ = new ArrayList();
	BettingBinPacker bbpacker_;
	
	public Hugh(){
		prepareBP();
	}
	
	public State placeBet(Bettor bettor){
		State betToPlace = bbpacker_.makeState(bettor.getFavNums());
		return betToPlace;
	}
	
	public double spinWheel( State st){
		th_.evaluateBet(st);
		return 0;
	}
	
	private void makeBettingProgressions(){
		double [] test = {1.0,2.0,1.0};
		double []martinWin = {1.0,2.0,2.0,1.5};
		double [] martinLoss = {1.0,2.0};
		// Collection <Double> test2 =   new Collection(test);
		//bpList= new ArrayList(Arrays.asList(test));
		ArrayList<Double>  testMartinWin= new ArrayList();
		ArrayList<Double>  testMartinLoss = new ArrayList(Arrays.asList(martinLoss));
		for(int i = 0; i < martinWin.length; i++){
			testMartinWin.add(new Double(martinWin[i]));
		}
		for(int i = 0; i < martinLoss.length; i++){
			testMartinLoss.add(new Double(martinLoss[i]));
		}
		
		
		
	//	bpList_.ad //(0, -1);
		
		BettingProgression bp1 = new BettingProgression("MartingaleProgression", testMartinWin, testMartinLoss);
		bpList_.add(bp1);
	}
	private void makePersonalities(){
		Personality persona1 = new Personality(8.5, 7, 5,2,"Jason- The College Student");
		personalityList_.add(persona1);
		Personality persona2 = new Personality(1.5,10,2,1,"Margaret- The Ancient Lady");
		personalityList_.add(persona2);
		Personality persona3 = new Personality(5,4,7,5, "Willie - The Sleaze");
		personalityList_.add(persona3);
		Personality persona4 = new Personality(3,1,2.5,9, "Mike-The Whale");
		personalityList_.add(persona4);
		Personality persona5 = new Personality(10,3,5,5, "Chris- The Bridge Jumper");
		personalityList_.add(persona5);
	}
	
	private void makeBettors(){
		int numBettors = 1;
		
		for(Personality p : personalityList_){
			if(numBettors > 0){
				PlayerData pd = new PlayerData(p.getName_());
				System.out.println("pd is " + pd.getName());
				ArrayList<Integer> fakeNums = makeFakeNums(p.getSpread_());
				Bettor player = new Bettor(p, fakeNums, pd, 14);
			    player.setBp(bpList_.get(0));
				System.out.println("player is " + player);
				playerList_.add(player);
				numBettors--;
			}
		}
	}
	
	private ArrayList<Integer> makeFakeNums(double spread){
		int spreadFactor = (int) Math.round(1-(1/(spread+1)));
		ArrayList<Integer> favNums = new ArrayList();
		System.out.println("Spread factor is " + spread);
		for(int i = 0; i < spreadFactor; i++){
			int favNum = (int) (Math.round((Math.random()) * (items_.size()-1)));// Math.random(items_.size());
			System.out.println("Items size is " + items_.size());
			System.out.println("favNum is " + favNum);
			if(!favNums.contains(new Integer(favNum))){
				favNums.add(new Integer(favNum));
				
			}
			else{
				while(favNums.contains(new Integer(favNum))){
					
					favNum = (int) (Math.round((Math.random()) * items_.size()));
				}
				favNums.add(new Integer(favNum));
				
			}
			System.out.println("added " + favNum + "to favNums");
		}
		return favNums;
		
	}
	
	public TheHouse getTh(){
		return th_;
	}
	
	public void prepareBP(){
		 bins_ = new ArrayList();
		items_ = new ArrayList(); 
		
		ClassicBin b1 = new ClassicBin(1,"P1",new double [] {1});
		bins_.add(b1);
        
        ClassicBin b2 = new ClassicBin(2,"P2",new double [] {1});
        bins_.add(b2);
        
        ClassicBin b3 = new ClassicBin(3,"P21",new double [] {1});
        bins_.add(b3);
        
        ClassicBin b4 = new ClassicBin(4,"P22",new double [] {1});
        bins_.add(b4);
        
        ClassicBin b5 = new ClassicBin(5,"P23",new double [] {1});
        bins_.add(b5);
        
        ClassicBin b6 = new ClassicBin(6,"P24",new double [] {1});
        bins_.add(b6);
        
        ClassicBin b7 = new ClassicBin(7,"P25",new double [] {1});
        bins_.add(b7);
        
        ClassicBin b8 = new ClassicBin(8,"P26",new double [] {1});
        bins_.add(b8);
        
        ClassicBin b9 = new ClassicBin(9,"P27",new double [] {1});
        bins_.add(b9);
        
        ClassicBin b10 = new ClassicBin(10,"P28",new double[] {1});
        bins_.add(b10);
        
        ClassicBin b11 = new ClassicBin(11,"P29",new double[] {1});
        bins_.add(b11);
        
        ClassicBin b12 = new ClassicBin(12,"P30",new double [] {1});
        bins_.add(b12);
        
        ClassicBin b13 = new ClassicBin(13,"P31",new double [] {1});
        bins_.add(b13);
        
        ClassicBin b14 = new ClassicBin(14,"P32",new double [] {1});
        bins_.add(b14);
        
        
        ClassicItem i1 = new ClassicItem(1,"App1",new double [] {.31},7);
        items_.add(i1);
       
        ClassicItem i2 = new ClassicItem(2,"App2",new double [] {.31},7);
        items_.add(i2);
        
        ClassicItem i3 = new ClassicItem(3,"App3",new double [] {.94},7);
        items_.add(i3);
        
        ClassicItem i4 = new ClassicItem(4,"App4",new double [] {.35},8);
        items_.add(i4);
        
        ClassicItem i5 = new ClassicItem(5,"App5",new double [] {.44},5);
        items_.add(i5);
        
        ClassicItem i6 = new ClassicItem(6,"App6",new double [] {.43},7);
        items_.add(i6);
        
        ClassicItem i7 = new ClassicItem(7,"App7",new double [] {.30},7);
        items_.add(i7);
        
        ClassicItem i8 = new ClassicItem(8,"App8",new double [] {.58},3);
        items_.add(i8);
       
        ClassicItem i9 = new ClassicItem(9,"App9",new double [] {.98},9);
        items_.add(i9);
        
        ClassicItem i10 = new ClassicItem(10,"App10",new double [] {.27},8);
        items_.add(i10);
        
        ClassicItem i11 = new ClassicItem(11,"App11",new double [] {.54},6);
        items_.add(i11);
        
        ClassicItem i12 = new ClassicItem(12,"App12",new double [] {.96},1);
        items_.add(i12);
        
        ClassicItem i13 = new ClassicItem(13,"App13",new double [] {.41},7);
        items_.add(i13);
        
        ClassicItem i14 = new ClassicItem(14,"App14",new double [] {.37},7);
        items_.add(i14);
        bbpacker_ = new BettingBinPacker(items_, bins_, 2);
	}

	public ArrayList<Bettor> getPlayerList_() {
		return playerList_;
	}

	public void setPlayerList_(ArrayList<Bettor> playerList_) {
		this.playerList_ = playerList_;
	}
	
	public static void main(String args[]){
		Hugh testHugh = new Hugh();
		testHugh.makePersonalities();
		testHugh.makeBettingProgressions();
		testHugh.makeBettors();
		
		ArrayList<Bettor> bettors = testHugh.getPlayerList_();
		for(Bettor bettor : bettors){
			System.out.println(bettor);
		}
		Bettor jason = bettors.get(0);
		jason.setBankroll(100.00);
		State testState ;
		Bet jasonsBet = new Bet();
		double wagerAmount = 10;
		double betCount = 0;
		TheHouse testHughHouse = testHugh.getTh();
		ArrayList<Bet> jasonsBets;
		Double jasonsBankroll =  jason.getBankroll();
		int numBetsTillGone = 0;
		boolean win;
		while(jasonsBankroll >0){
			numBetsTillGone ++;
			jasonsBets = new ArrayList();
			testState = testHugh.placeBet(jason);
			if(jasonsBet.getStatus() == 0){
				wagerAmount = wagerAmount * jason.getBp().getStartMultiplier();
			}
			
			else if(jasonsBet.getStatus() == 1){
				wagerAmount = wagerAmount * jason.getBp().getWin();
			}
			
			else{
				wagerAmount = wagerAmount * jason.getBp().getLoss();
			}
			
			if(wagerAmount > jasonsBankroll){
				wagerAmount = jasonsBankroll;
			}
			System.out.println("My name is Jason, and I am betting "+ wagerAmount + " on " + testState);
			
			jasonsBet.setBetAmount_(wagerAmount);
			jasonsBet.setBetState_(testState);
			
			
			System.out.println("jasons bet status : " + jasonsBet.getStatus());
			jasonsBankroll -= wagerAmount;
			jasonsBets.add(jasonsBet);
			
			jasonsBankroll += testHughHouse.spinWheel(jasonsBets);
			jason.reBet(jasonsBet);
			System.out.println("jasons bet status : " + jasonsBet.getStatus());
		    System.out.println("Jasons BankRoll is " + jasonsBankroll);
		    
		    testHugh.prepareBP();
		}
		
		System.out.println("It took " + numBetsTillGone +"bets to blow the entire bankroll");
		
	}

	
}
