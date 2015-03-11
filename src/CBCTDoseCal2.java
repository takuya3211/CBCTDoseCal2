
import java.io.*;

class CBCTDoseCal2 {
    //CT値の閾値を設定する
	static int CTSize = 410;
	static int airThreshold = 200;
	static int lungThreshold = 400;
	static int waterThreshold = 1100;
	static int boneThreshold = 1300;
	//static File targetDir = new File("c:/pic/");
	static File targetDir = new File("/Users/takuya/Dropbox/program/workspace/CBCTDoseCal2/pic/");
	String targetFile = null;
	static File [] listFile = targetDir.listFiles(getFileExtensionFilter(".dcm")); //*.dcmだけをリストにする
	
    public static void main(String[] args) {
    	setAllThreshold(5);//組織の閾値を設定する
    	doIt();//画像処理
	}
  
   public static void doIt(){
	   //test
	   try {
       	int j = 0;
       	for(j = 0; j <listFile.length; j ++){
       			FileInputStream input = new FileInputStream(listFile[j].getAbsolutePath());
	            //FileInputStream input2 = new FileInputStream(listFile[j].getAbsolutePath());
	            String toFile = targetDir+"/make/"+ listFile[j].getName();
	            FileOutputStream output = new FileOutputStream(toFile);

	            int filesize = input.available();
	            int headersize=filesize - (CTSize*CTSize * 2); //CTSize*CTSize*2バイトの画像
	            //System.out.println(headersize); //ヘッダサイズのプリント
	            byte buf[]=new byte[2];
	            byte dicombuf[] = new byte[headersize];
	            int len=0;
	            int i = 0;
	            input.read(dicombuf);
	            byte tempbuf = 0;
	            byte bufForIntercept[] = new byte[4];
	            int RescaleInterceptPosition = headersize;
	            for(i = 0; i < headersize; i++){
	            	tempbuf = dicombuf[i];
	            	if ( i < headersize -4) {
	            		bufForIntercept[0] = dicombuf[i];
	            		bufForIntercept[1] = dicombuf[i + 1];
	            		bufForIntercept[2] = dicombuf[i + 2];
	            		bufForIntercept[3] = dicombuf[i + 3];
	            		
	            		if ( bufForIntercept[0] == 0x28 &&
	            				bufForIntercept[1] == 0x00 &&
	            				bufForIntercept[2] == 0x52 &&
	            				bufForIntercept[3] == 0x10){
	            			System.out.println( i + " Hit!"); //切片が何バイト目にあるかプリント
	            			RescaleInterceptPosition = i;
	            		}
	            	}
	            	
	            	//画素値変換の切片を書き換える
	            	if(i == RescaleInterceptPosition + 4){
	            		output.write(0x05);
	            	}else if(i == RescaleInterceptPosition+9){
	            		output.write(0x31);
	            		output.write(0x30);
	            	}else if((i == RescaleInterceptPosition+10) || (i == RescaleInterceptPosition+11) ){
	            		output.write(0x30);
		            }else{
		            	output.write(tempbuf);
		            }
	            }
	            	
	            //output2.write(dicombuf);
	            input.read(buf);
	            //output.write(buf);
		            
		            
		           //ここからが画像の領域の処理
	            while((len=input.read(buf))!=-1){
			            //  if(i <= 100) System.out.println(bytetoHex(buf[0]) + " "  + bytetoHex(buf[1]));
			         
		            int pixelValue =  exchangeToInt(bytetoHex(buf[0]),bytetoHex(buf[1]));
		            //if(i > 1134) System.out.println(pixelValue);
		            byte[] newpixelValue= new byte[2];
		            newpixelValue = pixelValueOverride(pixelValue,airThreshold, lungThreshold, waterThreshold, boneThreshold); 
		            buf[0] = newpixelValue[0];
		            buf[1] = newpixelValue[1];
		            output.write(buf,0,len);
	            }
	           output.write(0x00);
	           output.write(0x00);
	           //ここまでが画像の領域の処理
   
		            
	            output.flush();
	            output.close();
	            input.close();
	            //input2.close();
	            System.out.println(j+1 + "/" + listFile.length);

        	}
        } catch (IOException e) {
            System.out.println(e);
        }
   }
   
   public static void doItKeikaku(){
	   try {
       	int j = 0;
       	for(j = 0; j <listFile.length; j ++){
       		 FileInputStream input = new FileInputStream(listFile[j].getAbsolutePath());
	            //FileInputStream input2 = new FileInputStream(listFile[j].getAbsolutePath());
	            String toFile = targetDir+"/make/"+ listFile[j].getName();
	            FileOutputStream output = new FileOutputStream(toFile);

	            int filesize = input.available();
	            int headersize=filesize - (512*512 * 2); //512*512*2バイトの画像計画CT
	            //System.out.println(headersize); //ヘッダサイズのプリント
	            byte buf[]=new byte[2];
	            byte dicombuf[] = new byte[headersize];
	            int len=0;
	            int i = 0;
	            input.read(dicombuf);
	            byte tempbuf = 0;
	            byte bufForIntercept[] = new byte[4];
	            int RescaleInterceptPosition = headersize;
	            for(i = 0; i < headersize; i++){
	            	tempbuf = dicombuf[i];
	            	if ( i < headersize -4) {
	            		bufForIntercept[0] = dicombuf[i];
	            		bufForIntercept[1] = dicombuf[i + 1];
	            		bufForIntercept[2] = dicombuf[i + 2];
	            		bufForIntercept[3] = dicombuf[i + 3];
	            		
	            		if ( bufForIntercept[0] == 0x28 &&
	            				bufForIntercept[1] == 0x00 &&
	            				bufForIntercept[2] == 0x52 &&
	            				bufForIntercept[3] == 0x10){
	            			System.out.println( i + " Hit!"); //切片が何バイト目にあるかプリント
	            			RescaleInterceptPosition = i;
	            		}
	            	}
	            	
	            	//画素値変換の切片を書き換える
	            	/*if(i == RescaleInterceptPosition + 4){
	            		output.write(0x05);
	            	}else if(i == RescaleInterceptPosition+9){
	            		output.write(0x31);
	            		output.write(0x30);
	            	}else if((i == RescaleInterceptPosition+10) || (i == RescaleInterceptPosition+11) ){
	            		output.write(0x30);
		            }else{
		            	output.write(tempbuf);
		            }*/
	            	output.write(tempbuf);
	            }
	            	
	            //output2.write(dicombuf);
	            input.read(buf);
	            //output.write(buf);
		            
		            
		           //ここからが画像の領域の処理
		            while((len=input.read(buf))!=-1){
				            //  if(i <= 100) System.out.println(bytetoHex(buf[0]) + " "  + bytetoHex(buf[1]));
				         
			            int pixelValue =  exchangeToInt(bytetoHex(buf[0]),bytetoHex(buf[1]));
			            //if(i > 1134) System.out.println(pixelValue);
			            byte[] newpixelValue= new byte[2];
			            newpixelValue = pixelValueOverrideKeikaku(pixelValue,airThreshold, lungThreshold, waterThreshold, boneThreshold); 
			            buf[0] = newpixelValue[0];
			            buf[1] = newpixelValue[1];
			            output.write(buf,0,len);
		            }
		           output.write(0x00);
		           output.write(0x00);
		           //ここまでが画像の領域の処理
   
		            
		            output.flush();
		            output.close();
		            input.close();
		            //input2.close();
		            System.out.println(j+1 + "/" + listFile.length);

	        	}
	        } catch (IOException e) {
	            System.out.println(e);
	        }
   }
   public static void setAllThreshold(int inputCount){
		int thresholdCounter = 0;
			for ( thresholdCounter = 0 ; thresholdCounter < inputCount; thresholdCounter ++){
			setThreshold(thresholdCounter);
		}
   }
    public static void  setThreshold(int thresholdCase){//入力された閾値を設定するメソッド
    	String thresholdMessage[] = {
    			"Input Air Threshold(200)",
    			"Input Lung Threshold(400)",
    			"Input Water Threshold(1100)",
    			"Input Bone Threshold(1300)",
    			"Input CT Size(410)"
    			};
        BufferedReader thresholdInput = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println(thresholdMessage[thresholdCase]);
			String tempThreshold="";
			tempThreshold = thresholdInput.readLine();
			if (!tempThreshold.equals("")) {
				switch(thresholdCase){
					case 0: {
						airThreshold = Integer.parseInt(tempThreshold);
						break;
					}
					case 1: {
						lungThreshold = Integer.parseInt(tempThreshold);
						break;
					}
					case 2: {
						waterThreshold = Integer.parseInt(tempThreshold);
						break;
					}
					case 3: {
						boneThreshold = Integer.parseInt(tempThreshold);
						break;
					}
					case 4: {
						CTSize = Integer.parseInt(tempThreshold);
						break;
					}
				}	
			}
					
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
    
    
    
    public static byte[] pixelValueOverride(int pixelvalue, int Air, int Lung, int Water, int Bone) {
    	byte [] newPixelValue = new byte[2];
    	int mediumCase = 0;
    	if(pixelvalue <= Air) {
    		mediumCase = 0;
    	}else if((pixelvalue > Air) && (pixelvalue <= Lung)) {
    		mediumCase = 1;
    	} else if((pixelvalue > Lung) && (pixelvalue <= Water)){
    		mediumCase = 2;
    	} else if((pixelvalue > Water)){
    		mediumCase = 3;
    	}
    	
    	switch (mediumCase) {
    	case 0:
    		newPixelValue[0] = (byte)0x00;
    		newPixelValue[1] = (byte)0x00;
    		//newPixelValue[0] = 0;
    		//newPixelValue[1] = 0;
    		break;
    	case 1:
    		newPixelValue[0] = (byte)0x2C;
    		newPixelValue[1] = (byte)0x01;
    		break;
    	case 2:
    		newPixelValue[0] = (byte)0xE8;
    		newPixelValue[1] = (byte)0x03;
    		break;
    	case 3:
    		newPixelValue[0] = (byte)0x14;
    		newPixelValue[1] = (byte)0x05;
    		break;
    		
    	}

    	return newPixelValue;
    			
    }
    
    public static byte[] pixelValueOverrideKeikaku(int pixelvalue, int Air, int Lung, int Water, int Bone) {
    	byte [] newPixelValue = new byte[2];
    	int mediumCase = 0;
    	if(pixelvalue <= Air) {
    		mediumCase = 0;
    	}else if((pixelvalue > Air) && (pixelvalue <= Lung)) {
    		mediumCase = 1;
    	} else if((pixelvalue > Lung) && (pixelvalue <= Water)){
    		mediumCase = 2;
    	} else if((pixelvalue > Water)){
    		mediumCase = 3;
    	}
    	
    	switch (mediumCase) {
    	case 0:
    		newPixelValue[0] = (byte)0x00;
    		newPixelValue[1] = (byte)0x00;
    		//newPixelValue[0] = 0;
    		//newPixelValue[1] = 0;
    		break;
    	case 1:
    		newPixelValue[0] = (byte)0x44;
    		newPixelValue[1] = (byte)0x01;
    		break;
    	case 2:
    		newPixelValue[0] = (byte)0x00;
    		newPixelValue[1] = (byte)0x04;
    		break;
    	case 3:
    		newPixelValue[0] = (byte)0x2C;
    		newPixelValue[1] = (byte)0x05;
    		break;
    		
    	}

    	return newPixelValue;
    			
    }
    
	public static String bytetoHex(byte inputint){//intをHexの文章にして先頭を０で埋める
		String kaeshi;
		
		kaeshi = Integer.toHexString(inputint & 0xff);
		switch (kaeshi.length()+2) {
			case 1: kaeshi = "000" + kaeshi; break;
			case 2: kaeshi = "00" + kaeshi; break;
			case 3: kaeshi = "0" + kaeshi; break;
			case 4: break;
			default: break;
		}
				
		return kaeshi;
	}
	
	public static int exchangeToInt (String a, String b){
		//16進数表記の文字列をInt型に変換
		return Integer.parseInt(b + a,16);
	}
	//引数が4つの場合。データのサイズのセグメントに使う
	public static String exchange(String a, String b, String c, String d){
			return d + c + b + a;
	}

    public static FilenameFilter getFileExtensionFilter(String extension) {  //listtargetDirで使うメソッド
        final String _extension = extension;  
        return new FilenameFilter() {  
            public boolean accept(File file, String name) {  
                boolean ret = name.endsWith(_extension) || name.endsWith(_extension.toUpperCase()) || name.endsWith(_extension.toLowerCase());   
                return ret;  
            }  
        };  
    }  
    
    public static void  listtargetDir(File targetDir){
		if (targetDir.exists() && targetDir.isDirectory()) {
		File[] fileList = targetDir.listFiles(getFileExtensionFilter(".dcm")); 
		for (int i = 0; i < fileList.length; i++) 
			{
			//System.out.println(fileList[i].getName());
			//System.out.println(fileList[i].getAbsolutePath());
			}
		}
    }
}
