/**************************************************************************
 * Copyright 2008 Brian Dougherty                                          *
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
package org.ascent.binpacking;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BasicBinPacker {
	protected List<ClassicItem> itemsToPack_;
	/*
	 * Item has :
	 * consumedResources
	 */
	protected List<ClassicBin> bins_;
	int boundType_;//0 = 69.4%, 1 = LL bound, 2 = Harmonic (100%)
	/*
	 * bins has: 
	 * List of items in bin
	 * Space for each consumedResource (see Lei Layland
	 */
	
	
	public BasicBinPacker (List<ClassicItem> items, List<ClassicBin> bins, int bt){
		itemsToPack_ = items; //Need to sort the items here
		bins_ = bins;
		boundType_ = bt;
		//sortBins();
		//sortItems();
		Comparator <ClassicBin> binComp = new Comparator<ClassicBin>(){
		
			
			public int compare(ClassicBin arg0, ClassicBin arg1) {//decreasing sort bins
				// TODO Auto-generated method stub
				if(arg0.getSpaceLeft_()[0] == arg1.getSpaceLeft_()[0])
				return 0;
				
				else if(arg0.getSpaceLeft_()[0] > arg1.getSpaceLeft_()[0])
					return -1;
				
				return 1;
			}
			
			
			
		};
		
		Comparator <ClassicItem> itemComp = new Comparator<ClassicItem>(){
		
			
			public int compare(ClassicItem arg0, ClassicItem arg1) {//decreasing sort bins
				// TODO Auto-generated method stub
				if(arg0.getConsumedResources_()[0] == arg1.getConsumedResources_()[0])
					return 0;
				
				if(arg0.getConsumedResources_()[0] > arg1.getConsumedResources_()[0])
					return -1;
				
				return 1;
			}
			
			
			
		};
		Collections.sort(bins_,binComp);
		Collections.sort(itemsToPack_,itemComp);
		//Collections.sort(bins,  )
	}
	
	
	public boolean packItems(){
		boolean success;
		for( ClassicItem item : itemsToPack_){
			success =placeItem(item);
			if(success == false){
				//System.out.println("Unsuccessful. Failed on " + item.getName());
				
				return false;
			}
		}
		return true;
	}
	
	protected boolean placeItem(ClassicItem item){
		for(ClassicBin bin : bins_){
			
			if( fitsBin(item, bin)){
				// put it in the bin
				
			
			////System.out.println(" " + bin.getBinName_() +" can hold item " + item.getName());
			bin = putInBin(item, bin); // this will change in the original bin right?
			
		//	bins_.remove(bin.getBinId_());
		//	bins_.add(  bin.getBinId_(), bin);
			return true;
			}
			
		}
		
		return false;
	}
	
	protected boolean fitsBin(ClassicItem item, ClassicBin bin){
		double [] consumedResources  = item.getConsumedResources_();
		double [] spaceLeft = bin.getSpaceLeft_();
		//System.out.println("Spaceleft is " + spaceLeft[0]);
		
		if(spaceLeft[0] != 1.0){ // if there is something in the bin
			
			//System.out.println("There is something in the bin");
			if(sumItems(bin,item) > getBound(bin,item)){
				//System.out.println(item.getName()+ " can't fit " + bin.getBinName_()+" with other items");
				////System.out.println("")
				return false;
			}
		}
		else{//if there is no item in the bin
			if(item.getConsumedResources_()[0] > 1){//if the item to put in consumes more that 100%
				//System.out.println(" Single item consumes more thatn 100%");
				return false;
			}
		}
		
		for(int i = 1; i <spaceLeft.length; i++){
		//	//System.out.println("CR [i] = "+ consumedResources[i] +" & space Left = " +spaceLeft[i] );
			if(consumedResources[i] > spaceLeft[i]){
				//System.out.println(" Resources overconsumed. Won't fit");
				return false; // some resource of the item doens't fit that dimension of the bin.
			}
			
		}
		return true; // all resources fit
		
	}
	
	protected double getBound(ClassicBin bin,ClassicItem item){
		if( boundType_ == 0){
			return .694;
		}
		if(boundType_ == 1){
			return getLLBound(bin,item);
		}
		if(boundType_ ==2){
			return 1;
		}
		return -1;
	}
	protected ClassicBin putInBin(ClassicItem item, ClassicBin bin){
		////System.out.println("About to add an item to Bin");
		bin.addItem(item);
		double [] sl = bin.getSpaceLeft_();
		double []cr = item.getConsumedResources_();
		for( int i = 0; i < cr.length; i++){
			sl[i] = sl[i] - cr[i];
		}
		//System.out.println(" Space left is now " + sl[0]);
		bin.setSpaceLeft_(sl);
		return bin;
	}
	
	protected double sumItems(ClassicBin bin, ClassicItem item){
		List<ClassicItem>Bitems = bin.getItems();
		double sum  = 0;
		for(ClassicItem bitem : Bitems){
			sum += bitem.getConsumedResources_()[0];
		}
		////System.out.println(" bin has " + bin.getItems().size() + " items");
		return sum+=item.getConsumedResources_()[0];
	}
	
	protected double getLLBound(ClassicBin LLbin, ClassicItem item){
		List<ClassicItem> hypotheticalItems = LLbin.getItems();
		//double [] cr = {100.0,100.0,100.0};
		//Item decoy = new Item(100,"100",cr);
		hypotheticalItems.add(item);
		double LLbound = hypotheticalItems.size() * ( Math.pow(2, (1.0 / hypotheticalItems.size()))-1 );
		System.out.print(" LL bound for bin " + LLbin.getBinId_() + " is " + LLbound);
		hypotheticalItems.remove(item);//Weird.
		return LLbound;
	}
	
	public List<ClassicBin> getBins(){
		return bins_;
	}
	
	
	public static void main(String args[]){
		List <ClassicBin> bins = new ArrayList();
		List <ClassicItem> items = new ArrayList(); 
		
		ClassicBin b1 = new ClassicBin(1,"P1",new double [] {1});
		bins.add(b1);
        
        ClassicBin b2 = new ClassicBin(2,"P2",new double [] {1});
        bins.add(b2);
        
        ClassicBin b3 = new ClassicBin(3,"P21",new double [] {1});
        bins.add(b3);
        
        ClassicBin b4 = new ClassicBin(4,"P22",new double [] {1});
        bins.add(b4);
        
        ClassicBin b5 = new ClassicBin(5,"P23",new double [] {1});
        bins.add(b5);
        
        ClassicBin b6 = new ClassicBin(6,"P24",new double [] {1});
        bins.add(b6);
        
        ClassicBin b7 = new ClassicBin(7,"P25",new double [] {1});
        bins.add(b7);
        
        ClassicBin b8 = new ClassicBin(8,"P26",new double [] {1});
        bins.add(b8);
        
        ClassicBin b9 = new ClassicBin(9,"P27",new double [] {1});
        bins.add(b9);
        
        ClassicBin b10 = new ClassicBin(10,"P28",new double[] {1});
        bins.add(b10);
        
        ClassicBin b11 = new ClassicBin(11,"P29",new double[] {1});
        bins.add(b11);
        
        ClassicBin b12 = new ClassicBin(12,"P30",new double [] {1});
        bins.add(b12);
        
        ClassicBin b13 = new ClassicBin(13,"P31",new double [] {1});
        bins.add(b13);
        
        ClassicBin b14 = new ClassicBin(14,"P32",new double [] {1});
        bins.add(b14);
        
        
        ClassicItem i1 = new ClassicItem(1,"App1",new double [] {.31},7);
        items.add(i1);
       
        ClassicItem i2 = new ClassicItem(2,"App2",new double [] {.31},7);
        items.add(i2);
        
        ClassicItem i3 = new ClassicItem(3,"App3",new double [] {.94},7);
        items.add(i3);
        
        ClassicItem i4 = new ClassicItem(4,"App4",new double [] {.35},8);
        items.add(i4);
        
        ClassicItem i5 = new ClassicItem(5,"App5",new double [] {.44},5);
        items.add(i5);
        
        ClassicItem i6 = new ClassicItem(6,"App6",new double [] {.43},7);
        items.add(i6);
        
        ClassicItem i7 = new ClassicItem(7,"App7",new double [] {.30},7);
        items.add(i7);
        
        ClassicItem i8 = new ClassicItem(8,"App8",new double [] {.58},3);
        items.add(i8);
       
        ClassicItem i9 = new ClassicItem(9,"App9",new double [] {.98},9);
        items.add(i9);
        
        ClassicItem i10 = new ClassicItem(10,"App10",new double [] {.27},8);
        items.add(i10);
        
        ClassicItem i11 = new ClassicItem(11,"App11",new double [] {.54},6);
        items.add(i11);
        
        ClassicItem i12 = new ClassicItem(12,"App12",new double [] {.96},1);
        items.add(i12);
        
        ClassicItem i13 = new ClassicItem(13,"App13",new double [] {.41},7);
        items.add(i13);
        
        ClassicItem i14 = new ClassicItem(14,"App14",new double [] {.37},7);
        items.add(i14);
		/*double [] item1Cr = {.05,6.0,3.0};
		double [] item2Cr = {.20,8.0,3.0};
		double [] item3Cr = {.35,4.0,3.0};
		double [] item4Cr = {.15,2.0,3.0};
		double [] item5Cr = {.75,5.0,3.0};
		ClassicItem item1 = new ClassicItem(1,"Item 1", item1Cr);
		ClassicItem item2 = new ClassicItem(2,"Item 2", item2Cr);
		ClassicItem item3 = new ClassicItem(3,"Item 3", item3Cr);
		ClassicItem item4 = new ClassicItem(4,"Item 4", item4Cr);
		ClassicItem item5 = new ClassicItem(5,"Item 5", item5Cr);
		
		items.add(item1);
		items.add(item2);
		items.add(item3);
		items.add(item4);
		items.add(item5);
		
		double [] bin1Cr = {.694,10.0,7.0};
		double [] bin2Cr = {.694,114.0,10.0};
		double [] bin3Cr = {.694,10.0,5};
		double [] bin4Cr = {.694,7.0,10.0};
		double [] bin5Cr = {.694,9.0,6.0};
		
		ClassicBin bin1 = new ClassicBin("bin 1 ", 1, bin1Cr);
		ClassicBin bin2 = new ClassicBin("bin 2 ", 2, bin2Cr);
		ClassicBin bin3 = new ClassicBin("bin 3 ", 3, bin3Cr);
		ClassicBin bin4 = new ClassicBin("bin 4 ", 4, bin4Cr);
		ClassicBin bin5 = new ClassicBin("bin 5 ", 5, bin5Cr);
		
		
		bins.add(bin1);
		bins.add(bin2);
		bins.add(bin3);
		bins.add(bin4);
		bins.add(bin5);*/
		BasicBinPacker bfp = new BasicBinPacker(items,bins,2); 
		/*
		 * The third option is which type of bound we are setting
		 * 0 = .694, 1 = LLBound, 2= 1 (Harmonic 100 %)
		 */
		List<ClassicBin> doneBins = bfp.getBins();
		////System.out.println("doneBins size is " + doneBins.size());
		for(ClassicBin b : doneBins){
			
			////System.out.println("Bin " + b.getBinName_() + "has items "+ b.getItems().size());
		}
		bfp.packItems();
		doneBins = bfp.getBins();
		//System.out.println(" done Bins size is " + doneBins.size());
		for(ClassicBin b : doneBins){
			int sumTasks = 0;
			for(ClassicItem i : b.getItems()){
				////System.out.println( b.getBinName_() + " has item " + i.getName() + " with " + i.getNumTasks_() + " tasks");
				sumTasks += i.getNumTasks_();
			}
			//System.out.println(" BIN " + b.getBinName_() + " has total tasks "+ sumTasks );
			
			////System.out.println("Bin " + b.getBinName_() + "has items "+ b.getItems().size());
		}
		
		
	
	}
     

}
